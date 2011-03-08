/**
 *
 */

package edu.common.dynamicextensions.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import au.com.bytecode.opencsv.CSVReader;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.PathAssociationRelationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.bulkoperator.metadata.Attribute;
import edu.wustl.bulkoperator.metadata.BulkOperationClass;
import edu.wustl.bulkoperator.metadata.BulkOperationMetaData;
import edu.wustl.bulkoperator.metadata.BulkOperationMetadataUtil;
import edu.wustl.bulkoperator.metadata.HookingInformation;
import edu.wustl.bulkoperator.util.BulkOperationException;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * @author shrishail_kalshetty.
 * Generates XML And CSV template for Bulk operation.
 */
public class BOTemplateGenerator extends AbstractCategoryIterator<BulkOperationClass>
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	/**
	 * logger for information.
	 */
	private static final Logger LOGGER = Logger.getCommonLogger(BOTemplateGenerator.class);

	/**
	 * Bulk operation class object.
	 */
	private BulkOperationClass bulkOperationClass;
	/**
	 * Constant for adding association between categories.
	 */
	private static final String ARROW_OPERATOR = "->";
	/**
	 * Constant for adding value to bulk operation class attributes.
	 */
	private static final String BLANK_SPACE = "";
	/**
	 * Constant for containment association.
	 */
	private static final String CONTAINMENT = "containment";
	/**
	 * Constant for specifying attributes belongs to which containment association.
	 */
	private static final String CONTAINMENT_SEPARATOR = "#1";
	/**
	 * Constant for setting batch size.
	 */
	private static final Integer BATCH_SIZE = 5;
	/**
	 * Constant for setting default max number of records.
	 */
	private static final Integer MAX_RECORD = 1;

	/**
	 * Parameterized constructor.
	 * @param categoryInterface category Interface object.
	 */
	public BOTemplateGenerator(CategoryInterface categoryInterface)
	{
		super(categoryInterface);
	}

	/**
	 * Process root category element.
	 * @param rootCategoryEntity CategoryEntityInterface object.
	 * @return BulkOperationClass object
	 */
	@Override
	protected BulkOperationClass processRootCategoryElement(
			CategoryEntityInterface rootCategoryEntity)
	{
		if (this.bulkOperationClass == null)
		{
			this.bulkOperationClass = new BulkOperationClass();
		}
		setCommonAttributes(this.bulkOperationClass);

		this.bulkOperationClass.setCardinality(DEConstants.Cardinality.ONE.getValue().toString());
		this.bulkOperationClass.setMaxNoOfRecords(MAX_RECORD);
		this.bulkOperationClass.setClassName(appendRootCategoryEntityName(rootCategoryEntity
				.getName()));

		return this.bulkOperationClass;
	}

	/**
	 * process each category entity attributes.
	 * @param attribute category attribute.
	 * @param boObject Bulk operation class object.
	 */
	@Override
	protected void processCategoryAttribute(CategoryAttributeInterface attribute,
			BulkOperationClass boObject)
	{
		if (!attribute.getIsRelatedAttribute())
		{
			Attribute bulkOperationAttribute = new Attribute();
			bulkOperationAttribute.setName(attribute.getAbstractAttribute().getName());
			bulkOperationAttribute.setBelongsTo(BLANK_SPACE);
			bulkOperationAttribute.setCsvColumnName(attribute.getAbstractAttribute().getName());
			bulkOperationAttribute.setUpdateBasedOn(false);

			final AttributeTypeInformationInterface attributeType = DynamicExtensionsUtility
					.getBaseAttributeOfcategoryAttribute(attribute).getAttributeTypeInformation();
			bulkOperationAttribute.setDataType(attributeType.getAttributeDataType().getName());
			handleDateFormat(bulkOperationAttribute, attributeType);

			boObject.getAttributeCollection().add(bulkOperationAttribute);
		}
	}

	/**
	 * process each category entity associations.
	 * @param categoryAssociation Category association.
	 * @return Bulk operation class object.
	 */
	@Override
	protected BulkOperationClass processCategoryAssociation(
			CategoryAssociationInterface categoryAssociation)
	{
		BulkOperationClass subBulkOperationClass = new BulkOperationClass();

		setCommonAttributes(subBulkOperationClass);

		setCardinality(categoryAssociation, subBulkOperationClass);
		setMaxNumberOfRecords(subBulkOperationClass);
		subBulkOperationClass.setClassName(updateCatgeoryEntityName(categoryAssociation));

		return subBulkOperationClass;

	}

	private void setCommonAttributes(BulkOperationClass subBulkOperationClass)
	{
		subBulkOperationClass.setRoleName(BLANK_SPACE);
		subBulkOperationClass.setBatchSize(BATCH_SIZE);
		subBulkOperationClass.setParentRoleName(BLANK_SPACE);
		subBulkOperationClass.setRelationShipType(CONTAINMENT);
		subBulkOperationClass.setTemplateName(this.category.getName());
	}

	/**
	 * This method sets the max number of records for category association.
	 * @param subBulkOperationClass BulkOperationClass object.
	 */
	private void setMaxNumberOfRecords(BulkOperationClass subBulkOperationClass)
	{
		if (DEConstants.Cardinality.ONE.getValue().toString().equals(
				subBulkOperationClass.getCardinality()))
		{
			subBulkOperationClass.setMaxNoOfRecords(MAX_RECORD);
		}
		else
		{
			subBulkOperationClass.setMaxNoOfRecords(BATCH_SIZE);
		}
	}

	/**
	 * post process for each category association.
	 * @param innerObject BulkOperationClass object.
	 * @param mainObject BulkOperationClass object.
	 */
	@Override
	protected void postprocessCategoryAssociation(BulkOperationClass innerObject,
			BulkOperationClass mainObject) // NOPMD by Kunal_Kamble on 12/30/10 9:22 PM
	{
		mainObject.getContainmentAssociationCollection().add(innerObject);
		mainObject = innerObject;
	}

	/**
	 * This method handles date format for Date category attribute.
	 * @param bulkOperationAttribute Bulk operation class attribute.
	 * @param attributeType Type of the attribute.
	 */
	private void handleDateFormat(Attribute bulkOperationAttribute,
			final AttributeTypeInformationInterface attributeType)
	{
		if (attributeType instanceof DateAttributeTypeInformation)
		{
			final DateAttributeTypeInformation dateType = (DateAttributeTypeInformation) attributeType;
			bulkOperationAttribute.setFormat(DynamicExtensionsUtility.getDateFormat(dateType
					.getFormat()));
		}
		else
		{
			bulkOperationAttribute.setFormat(BLANK_SPACE);
		}
	}

	/**
	 * Adds the category name to bulk operation class attribute template name and class name.
	 * @param categoryName name of the category.
	 * @return name of the category.
	 */
	private String appendRootCategoryEntityName(String categoryName)
	{
		String[] name = categoryName.split(DEConstants.CLOSING_SQUARE_BRACKET);
		StringBuffer buffer = new StringBuffer();
		for (String string : name)
		{
			buffer.append(string).append(DEConstants.CLOSING_SQUARE_BRACKET).append(ARROW_OPERATOR);
		}
		replaceLastDelimiter(buffer, ARROW_OPERATOR);
		return buffer.toString();
	}

	/**
	 * This method updates the category association target entity name.
	 * @param catAssociation object of the category association.
	 * @param className buffer to store the updated category name.
	 */
	private String updateCatgeoryEntityName(CategoryAssociationInterface catAssociation)
	{
		StringBuffer className = new StringBuffer();
		PathAssociationRelationInterface assocRelation = (PathAssociationRelationInterface) catAssociation
				.getTargetCategoryEntity().getPath().getPathAssociationRelationCollection()
				.toArray()[0];
		className.append(catAssociation.getCategoryEntity().getEntity().getName()).append(
				DEConstants.OPENING_SQUARE_BRACKET).append(assocRelation.getSourceInstanceId())
				.append(DEConstants.CLOSING_SQUARE_BRACKET).append(ARROW_OPERATOR);

		for (PathAssociationRelationInterface pathAssociationRelation : catAssociation
				.getTargetCategoryEntity().getPath().getPathAssociationRelationCollection())
		{
			className.append(pathAssociationRelation.getAssociation().getTargetEntity().getName())
					.append(DEConstants.OPENING_SQUARE_BRACKET).append(
							pathAssociationRelation.getPathSequenceNumber()).append(
							DEConstants.CLOSING_SQUARE_BRACKET).append(ARROW_OPERATOR);
		}
		replaceLastDelimiter(className, ARROW_OPERATOR);
		return className.toString();
	}

	/**
	 * This method removes the last extra arrow operator.
	 * @param buffer buffer to store resultant string.
	 * @param delimiter delimiter to remove from buffer.
	 */
	private void replaceLastDelimiter(StringBuffer buffer, String delimiter)
	{
		if (buffer.toString().endsWith(delimiter))
		{
			buffer.replace(0, buffer.length(), buffer.substring(0, buffer.lastIndexOf(delimiter)));
		}
	}

	/**
	 * This method sets the cardinality between source and target category entity.
	 * @param categoryAssociation CategoryAssociation object.
	 * @param subBulkOperationClass Set the cardinality of this object.
	 */
	private void setCardinality(CategoryAssociationInterface categoryAssociation,
			BulkOperationClass subBulkOperationClass)
	{
		//to check if cardinality is one to many or not
		if (categoryAssociation.getTargetCategoryEntity().getNumberOfEntries() < 0)
		{
			subBulkOperationClass.setCardinality("*");
		}
		else
		{
			subBulkOperationClass.setCardinality(DEConstants.Cardinality.ONE.getValue().toString());
		}
	}

	/**
	 *
	 * @param pathname
	 * @throws IOException
	 *
	 */
	@Deprecated
	private void replaceWord(String pathname) throws IOException
	{
		String line = "";
		String text = "";
		File file = new File(pathname);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		while ((line = reader.readLine()) != null)
		{
			text = text + line;
		}
		reader.close();
		String newText = text.replaceAll("bulkOperationMetaData", "BulkOperationMetadata");
		newText = newText.replaceAll("bulkOperationClass", "BulkOperationClass");
		FileWriter writer = new FileWriter(file);
		writer.write(newText);
		writer.close();
	}

	/**
	 * Generate the XML and CSV template for category required for bulk operation.
	 * @param baseDir Base Directory to store generated template files.
	 * @param xmlFilePath XML Template file path.
	 * @param mappingXML XML file required for generation of XML template.
	 * @throws DynamicExtensionsSystemException throws DESystemException.
	 * @throws BulkOperationException throws Bulk Operation Exception.
	 */
	public void generateXMLAndCSVTemplate(String baseDir, String xmlFilePath, String mappingXML)
			throws DynamicExtensionsSystemException, BulkOperationException
	{
		//Step1:
		iterateCategory(this.bulkOperationClass);
		//Step2:
		final BulkOperationMetaData bulkMetaData = appnedCategoryTemplate(xmlFilePath, mappingXML);
		//Step3:
		File newDir = saveTemplateCopy(baseDir, mappingXML, bulkMetaData);
		//Step4:
		createCSVTemplate(bulkMetaData, new File(newDir + File.separator
				+ this.bulkOperationClass.getTemplateName() + DEConstants.CSV_SUFFIX));
	}

	private File saveTemplateCopy(String baseDir, String mappingXML,
			final BulkOperationMetaData bulkMetaData) throws DynamicExtensionsSystemException
	{
		File newDir = new File(baseDir + File.separator + DEConstants.TEMPLATE_DIR);
		if (!newDir.exists())
		{
			newDir.mkdir();
		}
		try
		{
			final String pathname = newDir + File.separator
					+ this.bulkOperationClass.getTemplateName() + DEConstants.XML_SUFFIX;
			MarshalUtility.marshalObject(mappingXML, bulkMetaData, new FileWriter(
					new File(pathname)));
			replaceWord(pathname);
		}
		catch (IOException exception)
		{
			throw new DynamicExtensionsSystemException(
					"Error while creating XML template for Bulk operation.", exception);
		}
		return newDir;
	}

	private BulkOperationMetaData appnedCategoryTemplate(String xmlFilePath, String mappingXML)
			throws BulkOperationException
	{
		BulkOperationMetadataUtil metadataUtil = new BulkOperationMetadataUtil();
		final BulkOperationMetaData bulkMetaData = metadataUtil.unmarshall(xmlFilePath, mappingXML);
		BulkOperationClass rootBulkOperationClass = bulkMetaData.getBulkOperationClass().iterator()
				.next();
		final BulkOperationClass deCategoryBulkOperationClass = rootBulkOperationClass
				.getDynExtCategoryAssociationCollection().iterator().next();
		deCategoryBulkOperationClass.setTemplateName(this.bulkOperationClass.getTemplateName());
		deCategoryBulkOperationClass.setClassName(this.bulkOperationClass.getTemplateName());
		deCategoryBulkOperationClass.getContainmentAssociationCollection().add(
				this.bulkOperationClass);
		rootBulkOperationClass.setTemplateName(this.bulkOperationClass.getTemplateName());
		bulkMetaData.getBulkOperationClass().removeAll(bulkMetaData.getBulkOperationClass());
		bulkMetaData.getBulkOperationClass().add(rootBulkOperationClass);
		return bulkMetaData;
	}

	/**
	 * Generate CSV template required for Bulk operation.
	 * @param bulkMetaData BulkMetaData object.
	 * @param file File to write.
	 * @throws DynamicExtensionsSystemException throws DESystemException.
	 */
	private void createCSVTemplate(BulkOperationMetaData bulkMetaData, File file)
			throws DynamicExtensionsSystemException
	{
		StringBuffer csvStringBuffer = new StringBuffer();
		final BulkOperationClass boObject = bulkMetaData.getBulkOperationClass().iterator().next()
				.getDynExtCategoryAssociationCollection().iterator().next();
		HookingInformation hookingInformation = boObject.getHookingInformation().iterator().next();
		for (Attribute attribute : hookingInformation.getAttributeCollection())
		{
			csvStringBuffer.append(attribute.getCsvColumnName()).append(DEConstants.COMMA);
		}
		final Collection<BulkOperationClass> contAssoCollection = boObject
				.getContainmentAssociationCollection();

		processContainmentAssociation(csvStringBuffer, contAssoCollection, 0);
		replaceLastDelimiter(csvStringBuffer, DEConstants.COMMA);

		try
		{
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
			bufferedWriter.write(csvStringBuffer.toString());
			bufferedWriter.close();
		}
		catch (IOException exception)
		{
			throw new DynamicExtensionsSystemException(
					"Error while creating CSV template for bulk operation.", exception);
		}
	}

	/**
	 * Process the containment association for adding attribute names in CSV file.
	 * @param csvBuffer buffer to append CSV column names.
	 * @param containmentAssoCollection Collection of Containment association.
	 * @param count Count required to get depth of containment association.
	 */
	private void processContainmentAssociation(StringBuffer csvBuffer,
			Collection<BulkOperationClass> containmentAssoCollection, Integer count)
	{
		for (BulkOperationClass boClass : containmentAssoCollection)
		{
			appendAttributeCSVNames(csvBuffer, count, boClass);
			final Collection<BulkOperationClass> caCollection = boClass
					.getContainmentAssociationCollection();
			if (!caCollection.isEmpty())
			{
				processContainmentAssociation(csvBuffer, caCollection, count + 1);
			}
		}
	}

	/**
	 * This method appends the category attribute CSV names to buffer.
	 * @param csvBuffer buffer to append category attribute CSV names.
	 * @param count number of times to append CA separator.
	 * @param boClass get attribute collection from this object.
	 */
	private void appendAttributeCSVNames(StringBuffer csvBuffer, Integer count,
			BulkOperationClass boClass)
	{
		for (int index = 0; index < boClass.getMaxNoOfRecords(); index++)
		{
			for (Attribute attribute : boClass.getAttributeCollection())
			{
				csvBuffer.append(attribute.getCsvColumnName());
				for (int incr = 0; incr < count; incr++)
				{
					csvBuffer.append(CONTAINMENT_SEPARATOR);
				}
				csvBuffer.append('#').append(index + 1).append(DEConstants.COMMA);
			}
		}
	}

	/**
	 * This method will call through ant target to generate XML and CSV template for Bulk operation.
	 * @param args arguments required to generate template passed through ant target.
	 */
	public static void main(String[] args) // NOPMD by Kunal_Kamble on 12/30/10 7:42 PM
	{
		try
		{
			validateArguments(args);
			if (new File(args[0]).exists())
			{
				CSVReader reader = new CSVReader(new FileReader(args[0]));
				final String[] line = reader.readNext();

				for (String categoryName : line)
				{
					createCategoryTemplate(args, categoryName);
				}
			}
			else
			{
				createCategoryTemplate(args, args[0]);
			}
			LOGGER.info("XML and CSV template files are saved in: ./" + DEConstants.TEMPLATE_DIR);
		}
		catch (BulkOperationException exception)
		{
			LOGGER.error("Bulk Operation Exception: " + exception.getMsgValues());
			LOGGER.error("Detailed Exception: ", exception);
		}
		catch (DynamicExtensionsSystemException exception)
		{
			LOGGER.error("Exception while creating Template: " + exception.getLocalizedMessage());
			LOGGER.error("Detailed Exception: ", exception);
		}
		catch (IOException exception)
		{
			LOGGER.error("Exception while reading category file: "
					+ exception.getLocalizedMessage());
			LOGGER.error("Exception: ", exception);
		}
	}

	/**
	 * This method creates XML and CSV template for specified category.
	 * @param args arguments containing mapping XML file and template XML file.
	 * @param categoryName category name to generate category template.
	 * @throws DynamicExtensionsSystemException throw DESystemException.
	 * @throws BulkOperationException throw BulkOperationException.
	 */
	private static void createCategoryTemplate(String[] args, String categoryName)
			throws DynamicExtensionsSystemException, BulkOperationException
	{
		CategoryInterface categoryInterface = EntityCache.getInstance().getCategoryByName(
				categoryName);
		if (categoryInterface == null)
		{
			throw new BulkOperationException("Category with name '" + categoryName
					+ "' does not exist.");
		}
		else
		{
			BOTemplateGenerator boGen = new BOTemplateGenerator(categoryInterface);
			boGen.generateXMLAndCSVTemplate(System.getProperty("user.dir"), args[1], args[2]);
		}
	}

	/**
	 * This method validates the arguments provided.
	 * @param args arguments to be validated.
	 * @throws BulkOperationException throw BulkOperation Exception.
	 */
	private static void validateArguments(String[] args) throws BulkOperationException
	{
		final Integer minArgLength = 2;
		final Integer maxArgLength = 3;
		if (args.length < minArgLength && args.length > maxArgLength)
		{
			throw new BulkOperationException("Invalid number of arguments are passed.");
		}
		if (!args[MAX_RECORD].endsWith(DEConstants.XML_SUFFIX)
				|| !args[minArgLength].endsWith(DEConstants.XML_SUFFIX))
		{
			throw new BulkOperationException(
					"Input Template and mapping File does not ends with '.xml' extension.");
		}
	}
}
