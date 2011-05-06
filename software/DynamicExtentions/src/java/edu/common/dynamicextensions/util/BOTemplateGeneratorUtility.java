/**
 *
 */

package edu.common.dynamicextensions.util;

import java.io.File;
import java.util.Collection;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.bulkoperator.metadata.Attribute;
import edu.wustl.bulkoperator.metadata.BulkOperationClass;
import edu.wustl.bulkoperator.metadata.BulkOperationMetaData;
import edu.wustl.bulkoperator.metadata.BulkOperationMetadataUtil;
import edu.wustl.bulkoperator.metadata.HookingInformation;
import edu.wustl.bulkoperator.util.BulkOperationException;

/**
 * @author shrishail_kalshetty
 *
 */
public class BOTemplateGeneratorUtility
{

	public static final String MANY = "*";
	/**
	 * Constant for adding association between categories.
	 */
	private static final String ARROW_OPERATOR = "->";
	/**
	 * Constant for specifying attributes belongs to which containment association.
	 */
	private static final String CONTAINMENT_SEPARATOR = "#1";
	/**
	 * Constant for setting batch size.
	 */
	private static final Integer MAX_RECORD = 5;
	/**
	 * Constant for setting default max number of records.
	 */
	private static final Integer MIN_RECORD = 1;
	/**
	 * Constant for adding value to bulk operation class attributes.
	 */
	private static final String BLANK_SPACE = "";
	/**
	 * Constant for setting batch size.
	 */
	private static final Integer BATCH_SIZE = 5;

	/**
	 * constant string
	 */
	private static final String CONTAINMENT = "containment";

	/**
	 * This method sets the common attribute properties.
	 * @param bOClass BulkOperationClass object.
	 */
	public static void setCommonAttributes(BulkOperationClass bOClass, String templateName)
	{
		bOClass.setRoleName(BLANK_SPACE);
		bOClass.setBatchSize(BATCH_SIZE);
		bOClass.setParentRoleName(BLANK_SPACE);

		bOClass.setRelationShipType(CONTAINMENT);
		bOClass.setTemplateName(templateName);
	}

	/**
	 * This method removes the last extra arrow operator.
	 * @param buffer buffer to store resultant string.
	 * @param delimiter delimiter to remove from buffer.
	 */
	public static void replaceLastDelimiter(StringBuffer buffer, String delimiter)
	{
		if (buffer.toString().endsWith(delimiter))
		{
			buffer.replace(0, buffer.length(), buffer.substring(0, buffer.lastIndexOf(delimiter)));
		}
	}

	/**
	 * This method sets the max number of records for category association.
	 * @param bulkOperationClass BulkOperationClass object.
	 */
	public static void setMaxNumberOfRecords(BulkOperationClass bulkOperationClass)
	{
		if (DEConstants.Cardinality.ONE.getValue().toString().equals(
				bulkOperationClass.getCardinality()))
		{
			bulkOperationClass.setMaxNoOfRecords(MIN_RECORD);
		}
		else
		{
			bulkOperationClass.setMaxNoOfRecords(MAX_RECORD);
		}
	}

	/**
	 * Adds the category name to bulk operation class attribute template name and class name.
	 * @param categoryName name of the category.
	 * @return name of the category.
	 */
	public static String getRootCategoryEntityName(String categoryName)
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
	 * This method sets the cardinality between source and target category entity.
	 * @param numberOfEntries Number of Entries.
	 * @param bulkOperationClass Set the cardinality of this object.
	 */
	public static void setCardinality(Integer numberOfEntries, BulkOperationClass bulkOperationClass)
	{
		//to check if cardinality is one to many or not
		if (numberOfEntries < 0)
		{
			bulkOperationClass.setCardinality(MANY);
		}
		else
		{
			bulkOperationClass.setCardinality(DEConstants.Cardinality.ONE.getValue().toString());
		}
	}

	/**
	 * This method appends the XML template data into an existing XML file.
	 * @param xmlFilePath XML File Path.
	 * @param mappingXML Mapping XML file path.
	 * @param bulkOperationClass BO class object.
	 * @return BulkOperationMetaData object.
	 * @throws BulkOperationException throws BulkOperationMetaData
	 */
	public static BulkOperationMetaData appnedCategoryTemplate(String xmlFilePath,
			String mappingXML, BulkOperationClass bulkOperationClass) throws BulkOperationException
	{
		BulkOperationMetadataUtil metadataUtil = new BulkOperationMetadataUtil();
		final BulkOperationMetaData bulkMetaData = metadataUtil.unmarshall(xmlFilePath, mappingXML);
		BulkOperationClass rootBulkOperationClass = bulkMetaData.getBulkOperationClass().iterator()
				.next();
		final BulkOperationClass deCategoryBulkOperationClass = rootBulkOperationClass
				.getDynExtCategoryAssociationCollection().iterator().next();
		deCategoryBulkOperationClass.setTemplateName(bulkOperationClass.getTemplateName());
		deCategoryBulkOperationClass.setClassName(bulkOperationClass.getTemplateName());
		deCategoryBulkOperationClass.getContainmentAssociationCollection().add(bulkOperationClass);
		rootBulkOperationClass.setTemplateName(bulkOperationClass.getTemplateName());
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
	public static String createCSVTemplate(BulkOperationMetaData bulkMetaData, File file)
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
		return csvStringBuffer.toString();
	}

	/**
	 * Process the containment association for adding attribute names in CSV file.
	 * @param csvBuffer buffer to append CSV column names.
	 * @param containmentAssoCollection Collection of Containment association.
	 * @param count Count required to get depth of containment association.
	 */
	private static void processContainmentAssociation(StringBuffer csvBuffer,
			Collection<BulkOperationClass> containmentAssoCollection, Integer count)
	{
		for (BulkOperationClass boClass : containmentAssoCollection)
		{
			appendAttributeCSVNames(csvBuffer, count, boClass);
		}
	}

	/**
	 * This method appends the category attribute CSV names to buffer.
	 * @param csvBuffer buffer to append category attribute CSV names.
	 * @param count number of times to append CA separator.
	 * @param boClass get attribute collection from this object.
	 */
	private static void appendAttributeCSVNames(StringBuffer csvBuffer, Integer count,
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
			final Collection<BulkOperationClass> caCollection = boClass
			.getContainmentAssociationCollection();
			if (!caCollection.isEmpty())
			{
				processContainmentAssociation(csvBuffer, caCollection, count + 1);
			}
		}
	}


	public static String getAttributename(String className,String attributeName)
	{
		StringBuffer csvBuffer=new StringBuffer();
		String instanceId=null;
		if(className.contains("->"))
		{
			className=className.substring(className.lastIndexOf("->")+2,className.length());
			instanceId=className.substring(className.indexOf("[")+1,className.lastIndexOf("]"));
			className=className.substring(0,className.indexOf("["));
		}
		return csvBuffer.append(className).append("_").append(attributeName).append(instanceId).toString();
	}
}
