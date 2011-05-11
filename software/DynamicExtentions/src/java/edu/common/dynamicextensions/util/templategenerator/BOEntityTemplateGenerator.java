/**
 *
 */

package edu.common.dynamicextensions.util.templategenerator;

import java.io.File;
import java.util.Collection;

import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerConstantsInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.bulkoperator.metadata.Attribute;
import edu.wustl.bulkoperator.metadata.BulkOperationClass;
import edu.wustl.bulkoperator.metadata.BulkOperationMetaData;
import edu.wustl.bulkoperator.metadata.BulkOperationMetadataUtil;
import edu.wustl.bulkoperator.metadata.HookingInformation;
import edu.wustl.bulkoperator.util.BulkOperationException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * @author shrishail_kalshetty.
 * Generates XML And CSV template for Bulk operation.
 */
public class BOEntityTemplateGenerator extends AbstractEntityIterator<BulkOperationClass> implements TemplateGenerator
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	/**
	 * logger for information.
	 */
	private static final Logger LOGGER = Logger.getCommonLogger(BOEntityTemplateGenerator.class);

	/**
	 * Bulk operation class object.
	 */
	protected BulkOperationClass bulkOperationClass;
	/**
	 * Constant for setting default max number of records.
	 */
	private static final Integer MAX_RECORD = 1;
	/**
	 * Constant for adding association between categories.
	 */
	private static final String ARROW_OPERATOR = "->";

	private final String templateName;

	/**
	 * Parameterized constructor.
	 * @param categoryInterface category Interface object.
	 */
	public BOEntityTemplateGenerator(EntityInterface entity,String templateName)
	{
		super(entity);
		this.templateName=templateName;
		iterateEntity(bulkOperationClass);
	}

	/**
	 * Process root category element.
	 * @param rootCategoryEntity CategoryEntityInterface object.
	 * @return BulkOperationClass object
	 */
	protected BulkOperationClass processMainEntity(
			EntityInterface mainEntity)
	{
		if (bulkOperationClass == null)
		{
			bulkOperationClass = new BulkOperationClass();
		}
		BOTemplateGeneratorUtility.setCommonAttributes(bulkOperationClass, templateName);

		bulkOperationClass.setCardinality(DEConstants.Cardinality.ONE.getValue().toString());
		bulkOperationClass.setMaxNoOfRecords(MAX_RECORD);
		bulkOperationClass.setClassName(entity.getName());

		return bulkOperationClass;
	}

	protected void processEntity(EntityInterface entity, BulkOperationClass mainObject)
	{
		for (AttributeInterface attributeInterface : entity
				.getAttributeCollection())
		{

				processAttribute(attributeInterface,mainObject);

		}
		for (AssociationInterface association : entity
				.getAssociationCollection())
		{
			BulkOperationClass innnerObject = processAssociation(association);
			processEntity(association.getTargetEntity(), innnerObject);
			postprocessAssociation(innnerObject, mainObject);
		}
	}


	/**
	 * process each category entity attributes.
	 * @param attribute category attribute.
	 * @param boObject Bulk operation class object.
	 * @param catAttributeVsControl
	 */
	protected void processAttribute(AttributeInterface attribute,
			BulkOperationClass boObject)
	{
		if(!attribute.getName().equals(EntityManagerConstantsInterface.ID_ATTRIBUTE_NAME))
		{
			Attribute bulkOperationAttribute = new Attribute();
			bulkOperationAttribute.setName(attribute.getName());
			bulkOperationAttribute.setBelongsTo("");

				bulkOperationAttribute.setCsvColumnName(getAttributename(boObject.getClassName(),attribute.getName()));

			bulkOperationAttribute.setUpdateBasedOn(false);

			final AttributeTypeInformationInterface attributeType =attribute.getAttributeTypeInformation();
			bulkOperationAttribute.setDataType(attributeType.getAttributeDataType().getName());
			handleDateFormat(bulkOperationAttribute, attributeType);

			boObject.getAttributeCollection().add(bulkOperationAttribute);
		}
	}

	public String getAttributename(String className,String attributeName)
	{
		StringBuffer csvBuffer=new StringBuffer();
		if(className.contains("->"))
		{
			className=className.substring(className.lastIndexOf("->")+2,className.length());
		}
		return csvBuffer.append(className).append("_").append(attributeName).toString();
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
	protected BulkOperationClass processAssociation(
			AssociationInterface association)
	{
		BulkOperationClass subBulkOperationClass = new BulkOperationClass();

		BOTemplateGeneratorUtility.setCommonAttributes(subBulkOperationClass, templateName);

		BOTemplateGeneratorUtility.setCardinality(association.getTargetRole().getMaximumCardinality().getValue()
				, subBulkOperationClass);
		BOTemplateGeneratorUtility.setMaxNumberOfRecords(subBulkOperationClass);
		subBulkOperationClass.setClassName(updateEntityName(association));

		return subBulkOperationClass;

	}

	/**
	 * post process for each category association.
	 * @param innerObject BulkOperationClass object.
	 * @param mainObject BulkOperationClass object.
	 */
	@Override
	protected void postprocessAssociation(BulkOperationClass innerObject,
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
	private String updateEntityName(AssociationInterface association)
	{
		StringBuffer className = new StringBuffer();
		//className.append(association.getEntity().getName()).append(ARROW_OPERATOR);

		className.append(association.getTargetEntity().getName());
		return className.toString();
	}

	/**
	 * @return the bulkOperationClass.
	 */
	public BulkOperationClass getBulkOperationClass()
	{
		return bulkOperationClass;
	}

	/**
	 * @param bulkOperationClass the bulkOperationClass to set.
	 */
	public void setBulkOperationClass(BulkOperationClass bulkOperationClass)
	{
		this.bulkOperationClass = bulkOperationClass;
	}

	/**
	 * This method appends the XML template data into an existing XML file.
	 * @param xmlFilePath XML File Path.
	 * @param mappingXML Mapping XML file path.
	 * @param bulkOperationClass BO class object.
	 * @return BulkOperationMetaData object.
	 * @throws BulkOperationException throws BulkOperationMetaData
	 */
	public  BulkOperationMetaData mergeStaticTemplate(String xmlFilePath,
			String mappingXML) throws BulkOperationException
	{
		BulkOperationMetadataUtil metadataUtil = new BulkOperationMetadataUtil();
		final BulkOperationMetaData bulkMetaData = metadataUtil.unmarshall(xmlFilePath, mappingXML);
		BulkOperationClass rootBulkOperationClass = bulkMetaData.getBulkOperationClass().iterator()
				.next();
		final BulkOperationClass deCategoryBulkOperationClass = rootBulkOperationClass
				.getDynExtCategoryAssociationCollection().iterator().next();
		rootBulkOperationClass.getDynExtCategoryAssociationCollection().clear();
		deCategoryBulkOperationClass.setTemplateName(bulkOperationClass.getTemplateName());
		deCategoryBulkOperationClass.setClassName(entity.getEntityGroup().getName());
		deCategoryBulkOperationClass.getContainmentAssociationCollection().add(bulkOperationClass);
		rootBulkOperationClass.setTemplateName(bulkOperationClass.getTemplateName());
		rootBulkOperationClass.getDynExtEntityAssociationCollection().add(deCategoryBulkOperationClass);
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
	public String createCSVTemplate(BulkOperationMetaData bulkMetaData, File file)
			throws DynamicExtensionsSystemException
	{
		StringBuffer csvStringBuffer = new StringBuffer();
		final BulkOperationClass boObject = bulkMetaData.getBulkOperationClass().iterator().next()
				.getDynExtEntityAssociationCollection().iterator().next();
		HookingInformation hookingInformation = boObject.getHookingInformation().iterator().next();
		for (Attribute attribute : hookingInformation.getAttributeCollection())
		{
			csvStringBuffer.append(attribute.getCsvColumnName()).append(DEConstants.COMMA);
		}
		final Collection<BulkOperationClass> contAssoCollection = boObject
				.getContainmentAssociationCollection();

		BOTemplateGeneratorUtility.processContainmentAssociation(csvStringBuffer, contAssoCollection, "");
		BOTemplateGeneratorUtility.replaceLastDelimiter(csvStringBuffer, DEConstants.COMMA);
		return csvStringBuffer.toString();
	}
}
