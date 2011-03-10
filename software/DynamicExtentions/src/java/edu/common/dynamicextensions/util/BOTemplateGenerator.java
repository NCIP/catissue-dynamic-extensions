/**
 *
 */

package edu.common.dynamicextensions.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.PathAssociationRelationInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
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
	 * Constant for setting default max number of records.
	 */
	private static final Integer MAX_RECORD = 1;
	/**
	 * Constant for adding association between categories.
	 */
	private static final String ARROW_OPERATOR = "->";

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
		BOTemplateGeneratorUtility.setCommonAttributes(this.bulkOperationClass, this.category
				.getName());

		this.bulkOperationClass.setCardinality(DEConstants.Cardinality.ONE.getValue().toString());
		this.bulkOperationClass.setMaxNoOfRecords(MAX_RECORD);
		this.bulkOperationClass.setClassName(BOTemplateGeneratorUtility
				.getRootCategoryEntityName(rootCategoryEntity.getName()));

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
			bulkOperationAttribute.setBelongsTo("");
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
			bulkOperationAttribute.setFormat("");
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

		BOTemplateGeneratorUtility.setCommonAttributes(subBulkOperationClass, this.category
				.getName());

		BOTemplateGeneratorUtility.setCardinality(categoryAssociation.getTargetCategoryEntity()
				.getNumberOfEntries(), subBulkOperationClass);
		BOTemplateGeneratorUtility.setMaxNumberOfRecords(subBulkOperationClass);
		subBulkOperationClass.setClassName(updateCatgeoryEntityName(categoryAssociation));

		return subBulkOperationClass;

	}
	protected BulkOperationClass processMultiSelect(
			AssociationInterface association)
	{
		BulkOperationClass subBulkOperationClass = new BulkOperationClass();
		BOTemplateGeneratorUtility.setCommonAttributes(subBulkOperationClass, this.category
				.getName());
		if(association.getTargetRole().getMaximumCardinality().getValue()==100)
		{
			subBulkOperationClass.setCardinality("*");
		}
		else
		{
			subBulkOperationClass.setCardinality(DEConstants.Cardinality.ONE.getValue().toString());
		}
		BOTemplateGeneratorUtility.setMaxNumberOfRecords(subBulkOperationClass);
		subBulkOperationClass.setClassName(association.getName());

		return subBulkOperationClass;

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
		BOTemplateGeneratorUtility.replaceLastDelimiter(className, ARROW_OPERATOR);
		return className.toString();
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
		//Step1: Iterate the given category and generate XML template data.
		iterateCategory(this.bulkOperationClass);
		//Step2: Append generated XML template data to existing XML template.
		final BulkOperationMetaData bulkMetaData = BOTemplateGeneratorUtility
				.appnedCategoryTemplate(xmlFilePath, mappingXML, this.bulkOperationClass);
		//Step3: Write this template data in a file and store it in temporary directory.
		File newDir = BOTemplateGeneratorUtility.saveXMLTemplateCopy(baseDir, mappingXML,
				bulkMetaData, this.bulkOperationClass);
		//Step4: Create CSV template for existing category.
		final File csvFile = new File(newDir + File.separator
				+ this.bulkOperationClass.getTemplateName() + DEConstants.CSV_SUFFIX);
		final String csvTemplateString = BOTemplateGeneratorUtility.createCSVTemplate(bulkMetaData,
				csvFile);
		//Step5: Save CSV template for existing category.
		BOTemplateGeneratorUtility.saveCSVTemplateCopy(csvFile, csvTemplateString);
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

	/**
	 * @return the bulkOperationClass.
	 */
	public BulkOperationClass getBulkOperationClass()
	{
		return this.bulkOperationClass;
	}

	/**
	 * @param bulkOperationClass the bulkOperationClass to set.
	 */
	public void setBulkOperationClass(BulkOperationClass bulkOperationClass)
	{
		this.bulkOperationClass = bulkOperationClass;
	}

}
