
package edu.common.dynamicextensions.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domain.BooleanAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.FileAttributeRecordValue;
import edu.common.dynamicextensions.domain.FileAttributeTypeInformation;
import edu.common.dynamicextensions.domain.StringAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.NumericTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleParameterInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.parser.CategoryCSVConstants;
import edu.wustl.common.util.Utility;

/**
 * Utility class for testing purpose which contains the methods for generating the DataValue map for
 * the entities & categories.
 * @author pavan_kalantri
 *
 */
public class DummyMapGenerator
{

	/**
	 * This method will create the Data Value map for the Given Category Entity .
	 * It will put some hard coded values for the different attributes as follows.
	 * Date Attribute  = current date, If range is specified then the min range date + 1 day.
	 * Numeric Attribute = 15 , If range is specified then the min range value.
	 * Boolean attribute = true.
	 * String attribute  = test String.
	 * other attribute = test String for other data type
	 * @param rootCatEntity the root category entity for which to generate the map
	 * @return generated map.
	 * @throws ParseException
	 * @throws DynamicExtensionsSystemException
	 */
	public Map<BaseAbstractAttributeInterface, Object> createDataValueMapForCategory(
			CategoryEntityInterface rootCatEntity) throws ParseException,
			DynamicExtensionsSystemException
	{
		Map<BaseAbstractAttributeInterface, Object> dataValue = new HashMap<BaseAbstractAttributeInterface, Object>();
		for (CategoryAttributeInterface catAtt : rootCatEntity.getAllCategoryAttributes())
		{
			// put the different value for diff attribute type
			if (catAtt.getAbstractAttribute() instanceof AttributeInterface
					&& !catAtt.getIsRelatedAttribute())
			{
				AttributeInterface attribute = (AttributeInterface) catAtt.getAbstractAttribute();
				updateDataMap(dataValue, catAtt, attribute);
			}
			else
			{
				AssociationInterface association = (AssociationInterface) catAtt
						.getAbstractAttribute();
				for (AbstractAttributeInterface attributeInterface : association.getTargetEntity()
						.getAllAttributes())
				{
					updateDataMap(dataValue, catAtt, (AttributeInterface) attributeInterface);
				}
			}
		}
		for (CategoryAssociationInterface catAssociation : rootCatEntity
				.getCategoryAssociationCollection())
		{
			List dataList = new ArrayList();
			CategoryEntityInterface targetCaEntity = catAssociation.getTargetCategoryEntity();
			dataList.add(createDataValueMapForCategory(targetCaEntity));
			if (targetCaEntity.getNumberOfEntries().equals(-1))
			{
				dataList.add(createDataValueMapForCategory(targetCaEntity));
			}
			dataValue.put(catAssociation, dataList);
		}
		return dataValue;
	}

	private void updateDataMap(Map dataValue, BaseAbstractAttributeInterface catAtt,
			AttributeInterface attribute) throws ParseException, DynamicExtensionsSystemException
	{
		if (attribute.getAttributeTypeInformation() instanceof DateAttributeTypeInformation)
		{

			String value = getDateValueForAttribute(catAtt);
			dataValue.put(catAtt, value);
		}
		else if (attribute.getAttributeTypeInformation() instanceof NumericTypeInformationInterface)
		{
			String value = getNumericValueForAttribute(attribute);
			dataValue.put(catAtt, value);
		}
		else if (attribute.getAttributeTypeInformation() instanceof BooleanAttributeTypeInformation)
		{
			dataValue.put(catAtt, "true");
		}
		else if (attribute.getAttributeTypeInformation() instanceof StringAttributeTypeInformation)
		{
			dataValue.put(catAtt, "test String");
		}
		else if (attribute.getAttributeTypeInformation() instanceof FileAttributeTypeInformation)
		{
			dataValue.put(catAtt, getFileRecordValueForAttribute());
		}
		else
		{
			dataValue.put(catAtt, "test String for other data type");
		}
	}

	private FileAttributeRecordValue getFileRecordValueForAttribute()
			throws DynamicExtensionsSystemException
	{
		File formFile = new File("src/java/ApplicationDAOProperties.xml");
		FileAttributeRecordValue fileAttributeRecordValue = new FileAttributeRecordValue();
		fileAttributeRecordValue.setFileContent(getFileContents(formFile));
		fileAttributeRecordValue.setFileName(formFile.getName());
		fileAttributeRecordValue.setContentType("application/x-unknown");
		return fileAttributeRecordValue;
	}

	private byte[] getFileContents(File formFile) throws DynamicExtensionsSystemException
	{
		int length = (int) formFile.length();
		byte[] buffer = new byte[length];
		try
		{

			BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(formFile));
			inputStream.read(buffer);
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Exception occured while creating the file record", e);
		}
		return buffer;
	}

	/**
	 * This method will create the Data Value map for the Given Entity .
	 * It will put some hard coded values for the different attributes as follows.
	 * Date Attribute  = current date, If range is specified then the min range date + 1 day.
	 * Numeric Attribute = 15 , If range is specified then the min range value.
	 * Boolean attribute = true.
	 * String attribute  = test String.
	 * Other attribtue = test String for other data type.
	 * @param rootCatEntity the main entity for which to generate the map
	 * @return generated map.
	 * @throws ParseException
	 * @throws DynamicExtensionsSystemException
	 */
	public Map<AbstractAttributeInterface, Object> createDataValueMapForEntity(
			EntityInterface rootEntity) throws ParseException, DynamicExtensionsSystemException
	{
		Map<AbstractAttributeInterface, Object> dataValue = new HashMap<AbstractAttributeInterface, Object>();
		for (AbstractAttributeInterface abstractAttribute : rootEntity.getAttributeCollection())
		{
			// put the different value for diff attribute type
			if (abstractAttribute instanceof AttributeInterface)
			{
				AttributeInterface attribute = (AttributeInterface) abstractAttribute;
				updateDataMap(dataValue, attribute, attribute);

			}
		}
		for (AssociationInterface association : rootEntity.getAssociationCollection())
		{
			List dataList = new ArrayList();
			EntityInterface targetCaEntity = association.getTargetEntity();
			dataList.add(createDataValueMapForEntity(targetCaEntity));
			if (association.getTargetRole().getMaximumCardinality().equals(
					DEConstants.Cardinality.MANY.getValue()))
			{
				dataList.add(createDataValueMapForEntity(targetCaEntity));
			}
			dataValue.put(association, dataList);
		}
		return dataValue;
	}

	/**
	 * This will return the value to be added in a map for date Attribute for attribute.
	 * @param attribute attribtue interface
	 * @return valid date value.
	 * @throws ParseException
	 */
	private String getDateValueForAttribute(BaseAbstractAttributeInterface attribute)
			throws ParseException
	{
		AttributeInterface abstractAttribute;
		Set<RuleInterface> attributeRules;
		if (attribute instanceof CategoryAttributeInterface)
		{
			CategoryAttributeInterface catAttribute = (CategoryAttributeInterface) attribute;
			abstractAttribute = (AttributeInterface) catAttribute.getAbstractAttribute();

			attributeRules = new HashSet<RuleInterface>(catAttribute.getRuleCollection());
		}
		else
		{
			abstractAttribute = (AttributeInterface) attribute;
			attributeRules = new HashSet<RuleInterface>(abstractAttribute.getRuleCollection());
		}
		Date newDate = new Date();
		String format = ((DateAttributeTypeInformation) abstractAttribute
				.getAttributeTypeInformation()).getFormat();
		String minParam = getMinRuleparam(attributeRules);
		if (minParam != null && !"".equals(minParam))
		{
			Date date = Utility.parseDate(minParam, DynamicExtensionsUtility.getDateFormat(format));
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			newDate = cal.getTime();
		}

		SimpleDateFormat formatter = new SimpleDateFormat(DynamicExtensionsUtility
				.getDateFormat(format));
		return formatter.format(newDate);
	}

	/**
	 * It will serach for the date_range or range rule & if found will return the minimun value specified
	 * in the range else will return empty string.
	 * @param attributeRules rule collection.
	 * @return min param value for range rule.
	 */
	private String getMinRuleparam(Set<RuleInterface> attributeRules)
	{
		String value = "";
		for (RuleInterface rule : attributeRules)
		{
			if (rule.getName().equalsIgnoreCase(CategoryCSVConstants.RANGE)
					|| rule.getName().equalsIgnoreCase(ProcessorConstants.DATE_RANGE))
			{
				Collection<RuleParameterInterface> ruleParameters = rule
						.getRuleParameterCollection();
				for (RuleParameterInterface ruleParameter : ruleParameters)
				{
					if (ruleParameter.getName().equalsIgnoreCase(CategoryCSVConstants.MIN))
					{
						value = ruleParameter.getValue();

						break;
					}
				}
				break;
			}
		}
		return value;
	}

	/**
	 * This will return the value to be added in a map for numeric Attribute for attribute.
	 * @param attribute attribtue interface
	 * @return valid numeric value.
	 * @throws ParseException
	 */
	private String getNumericValueForAttribute(BaseAbstractAttributeInterface attribute)
			throws ParseException
	{
		AttributeInterface abstractAttribute;

		Set<RuleInterface> attributeRules;
		if (attribute instanceof CategoryAttributeInterface)
		{
			CategoryAttributeInterface catAttribute = (CategoryAttributeInterface) attribute;
			abstractAttribute = (AttributeInterface) catAttribute.getAbstractAttribute();
			attributeRules = new HashSet<RuleInterface>(catAttribute.getRuleCollection());
		}
		else
		{
			abstractAttribute = (AttributeInterface) attribute;
			attributeRules = new HashSet<RuleInterface>(abstractAttribute.getRuleCollection());
		}

		abstractAttribute.getAttributeTypeInformation();
		String minParam = getMinRuleparam(attributeRules);
		String value = "15";
		if (minParam != null && !"".equals(minParam))
		{
			value = minParam;
		}
		return value;
	}

	public void validateRetrievedDataValueMap(
			Map<BaseAbstractAttributeInterface, Object> retrievedDataValue,
			Map<BaseAbstractAttributeInterface, Object> dataValuemap)
			throws DynamicExtensionsSystemException
	{
		for (Map.Entry<BaseAbstractAttributeInterface, Object> entryObject : dataValuemap
				.entrySet())
		{
			BaseAbstractAttributeInterface attribute = entryObject.getKey();

			if (attribute instanceof CategoryAttributeInterface)
			{
				if ((entryObject.getValue() instanceof List))
				{
					// multiselect case.
				}
				else
				{
					//normal attribute.
					Object object = retrievedDataValue.get(attribute);
					if (!entryObject.getValue().toString().equals(object.toString()))
					{
						throw new DynamicExtensionsSystemException("Data values for "
								+ attribute.getName() + " does not match. Retrieve value : "
								+ object.toString() + " Inserted value : "
								+ entryObject.getValue().toString());
					}
				}

			}
			else if (attribute instanceof CategoryAssociationInterface)
			{
				List dataValueList = (List) entryObject.getValue();
				List retrievedList = (List) retrievedDataValue.get(attribute);
				if (dataValueList.size() != retrievedList.size())
				{
					throw new DynamicExtensionsSystemException("Data values for "
							+ attribute.getName() + " does not match.");
				}
				for (int i = 0; i < dataValueList.size(); i++)
				{
					Map<BaseAbstractAttributeInterface, Object> catAssocDataValuemap = (Map<BaseAbstractAttributeInterface, Object>) dataValueList
							.get(i);
					Map<BaseAbstractAttributeInterface, Object> retrievedCatAssocDataValuemap = (Map<BaseAbstractAttributeInterface, Object>) retrievedList
							.get(i);
					validateRetrievedDataValueMap(retrievedCatAssocDataValuemap,
							catAssocDataValuemap);
				}
			}
		}

	}
}
