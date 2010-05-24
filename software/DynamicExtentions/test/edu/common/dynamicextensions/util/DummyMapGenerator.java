
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
import edu.common.dynamicextensions.domain.CategoryAttribute;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.FileAttributeRecordValue;
import edu.common.dynamicextensions.domain.FileAttributeTypeInformation;
import edu.common.dynamicextensions.domain.StringAttributeTypeInformation;
import edu.common.dynamicextensions.domain.UserDefinedDE;
import edu.common.dynamicextensions.domain.userinterface.AbstractContainmentControl;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.NumericTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.EnumeratedControlInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleParameterInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
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
	 * Date Attribute  = current date,
	 * Numeric Attribute = 15
	 * Boolean attribute = true.
	 * String attribute  = test String.
	 * other attribute = test String for other data type
	 * If the range is specified on the attribute then depending on the mapStrategry varible the values will
	 * be put.
	 * e.g if mapStrategry = 0 then min range value is used.
	 * if mapStrategry < 0 then value less than min range value is used.
	 * if mapStrategry > 0 then value greater than max range value is used
	 * @param rootCatEntity the root category entity for which to generate the map
	 * @param mapStrategry map generation stratergy
	 * @return generated map.
	 * @throws ParseException
	 * @throws DynamicExtensionsSystemException
	 */
	public Map<BaseAbstractAttributeInterface, Object> createDataValueMapForCategory(
			CategoryEntityInterface rootCatEntity, int mapStrategry) throws ParseException,
			DynamicExtensionsSystemException
	{
		Map<BaseAbstractAttributeInterface, Object> dataValue = new HashMap<BaseAbstractAttributeInterface, Object>();
		ContainerInterface containerInterface = (ContainerInterface) rootCatEntity
				.getContainerCollection().toArray()[0];
		for (ControlInterface control : containerInterface.getAllControls())
		{
			if (control instanceof AbstractContainmentControl)
			{// category assiciation.
				CategoryAssociationInterface catAssociation = (CategoryAssociationInterface) control
						.getBaseAbstractAttribute();
				List dataList = new ArrayList();
				CategoryEntityInterface targetCaEntity = catAssociation.getTargetCategoryEntity();
				dataList.add(createDataValueMapForCategory(targetCaEntity, mapStrategry));
				if (targetCaEntity.getNumberOfEntries().equals(-1))
				{
					dataList.add(createDataValueMapForCategory(targetCaEntity, mapStrategry));
				}
				dataValue.put(catAssociation, dataList);
			}
			else if (control.getBaseAbstractAttribute() != null)
			{
				CategoryAttributeInterface catAttribute = (CategoryAttributeInterface) control
						.getBaseAbstractAttribute();
				if (catAttribute.getAbstractAttribute() instanceof AttributeInterface
						&& !catAttribute.getIsRelatedAttribute())
				{// normal attribute
					AttributeInterface attribute = (AttributeInterface) catAttribute
							.getAbstractAttribute();
					updateDataMap(dataValue, catAttribute, attribute, mapStrategry, control);
				}
				else if (catAttribute.getAbstractAttribute() instanceof AssociationInterface)
				{// multiselect attribute
					AssociationInterface association = (AssociationInterface) catAttribute
							.getAbstractAttribute();
					AttributeInterface multiselectAttr = (AttributeInterface) EntityManagerUtil
							.filterSystemAttributes(
									association.getTargetEntity().getAllAbstractAttributes())
							.iterator().next();
					Map<BaseAbstractAttributeInterface, Object> multiSelectDataValue = new HashMap<BaseAbstractAttributeInterface, Object>();
					for (int i = 0; i < 2; i++)
					{
						updateDataMap(multiSelectDataValue, multiselectAttr, multiselectAttr,
								mapStrategry, control);
					}
					List multiselctValueList = new ArrayList();
					multiselctValueList.add(multiSelectDataValue);
					dataValue.put(catAttribute, multiselctValueList);
				}

			}

		}
		return dataValue;
	}

	private void updateDataMap(Map dataValue, BaseAbstractAttributeInterface catAtt,
			AttributeInterface attribute, int mapStrategry, ControlInterface control)
			throws ParseException, DynamicExtensionsSystemException
	{
		if (control instanceof EnumeratedControlInterface)
		{
			AttributeMetadataInterface attributeInterface = (AttributeMetadataInterface) catAtt;
			if (attributeInterface.getDataElement() != null)
			{
				for (PermissibleValueInterface permissibleValue : ((UserDefinedDE) ((AttributeMetadataInterface) catAtt)
						.getDataElement()).getPermissibleValueCollection())
				{
					dataValue.put(catAtt, permissibleValue.getValueAsObject().toString());
				}
			}
		}
		else
		{
			if (attribute.getAttributeTypeInformation() instanceof DateAttributeTypeInformation)
			{

				String value = getDateValueForAttribute(catAtt, mapStrategry);
				dataValue.put(catAtt, value);
			}
			else if (attribute.getAttributeTypeInformation() instanceof NumericTypeInformationInterface)
			{
				String value = getNumericValueForAttribute(catAtt, mapStrategry);
				dataValue.put(catAtt, value);
			}
			else if (attribute.getAttributeTypeInformation() instanceof BooleanAttributeTypeInformation)
			{
				dataValue.put(catAtt, "true");
			}
			else if (attribute.getAttributeTypeInformation() instanceof StringAttributeTypeInformation)
			{
				dataValue.put(catAtt, "test");
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
	 * Date Attribute  = current date
	 * Numeric Attribute = 15
	 * Boolean attribute = true.
	 * String attribute  = test String.
	 * Other attribtue = test String for other data type.
	 * If the range is specified on the attribute then depending on the mapStrategry varible the values will
	 * be put.
	 * e.g if mapStrategry = 0 then min range value is used.
	 * if mapStrategry < 0 then value less than min range value is used.
	 * if mapStrategry > 0 then value greater than max range value is used
	 * @param rootCatEntity the main entity for which to generate the map
	 * @param mapStrategry
	 * @return generated map.
	 * @throws ParseException
	 * @throws DynamicExtensionsSystemException
	 */
	public Map<BaseAbstractAttributeInterface, Object> createDataValueMapForEntity(
			EntityInterface rootEntity, int mapStrategry) throws ParseException,
			DynamicExtensionsSystemException
	{
		Map<BaseAbstractAttributeInterface, Object> dataValue = new HashMap<BaseAbstractAttributeInterface, Object>();
		ContainerInterface containerInterface = (ContainerInterface) rootEntity
				.getContainerCollection().toArray()[0];
		for (ControlInterface control : containerInterface.getAllControls())
		{
			if (control instanceof AbstractContainmentControl)
			{//association
				List dataList = new ArrayList();
				AssociationInterface association = (AssociationInterface) control
						.getBaseAbstractAttribute();
				EntityInterface targetCaEntity = association.getTargetEntity();
				dataList.add(createDataValueMapForEntity(targetCaEntity, mapStrategry));
				if (association.getTargetRole().getMaximumCardinality().equals(
						DEConstants.Cardinality.MANY.getValue()))
				{
					dataList.add(createDataValueMapForEntity(targetCaEntity, mapStrategry));
				}
				dataValue.put(association, dataList);
			}
			else if (control.getBaseAbstractAttribute() != null)
			{
				if (control.getBaseAbstractAttribute() instanceof AssociationInterface)
				{// multiselect case.

					List dataList = new ArrayList();
					AssociationInterface association = (AssociationInterface) control
							.getBaseAbstractAttribute();
					AttributeInterface attribute = (AttributeInterface) EntityManagerUtil
							.filterSystemAttributes(
									association.getTargetEntity().getAllAbstractAttributes())
							.iterator().next();
					Map<BaseAbstractAttributeInterface, Object> muliselectDataValueMap = new HashMap<BaseAbstractAttributeInterface, Object>();
					updateDataMap(muliselectDataValueMap, attribute, attribute, mapStrategry,
							control);
					dataList.add(muliselectDataValueMap);
					// 2nd value selected
					Map<BaseAbstractAttributeInterface, Object> otherMuliselectDataValueMap = new HashMap<BaseAbstractAttributeInterface, Object>();
					updateDataMap(otherMuliselectDataValueMap, attribute, attribute, mapStrategry,
							control);
					dataList.add(otherMuliselectDataValueMap);
					dataValue.put(association, dataList);

				}
				else
				{// normal attribute
					AttributeInterface attribute = (AttributeInterface) control
							.getBaseAbstractAttribute();
					updateDataMap(dataValue, attribute, attribute, mapStrategry, control);
				}
			}

		}
		return dataValue;
	}

	/**
	 * This will return the value to be added in a map for date Attribute for attribute.
	 * @param attribute attribtue interface
	 * @param mapStratergy
	 * @return valid date value.
	 * @throws ParseException
	 */
	private String getDateValueForAttribute(BaseAbstractAttributeInterface attribute,
			int mapStratergy) throws ParseException
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
		String minParam = getRangeRuleparam(attributeRules, CategoryCSVConstants.MIN);
		String maxParam = getRangeRuleparam(attributeRules, CategoryCSVConstants.MAX);
		String format = ((DateAttributeTypeInformation) abstractAttribute
				.getAttributeTypeInformation()).getFormat();

		String dateValue = getDateValueForAttribute(mapStratergy, minParam, maxParam, format);
		return dateValue;
	}

	private String getDateValueForAttribute(int mapStratergy, String minParam, String maxParam,
			String format) throws ParseException
	{
		Date newDate = new Date();
		if (minParam != null && !"".equals(minParam))
		{

			if (mapStratergy > 0)
			{
				Date date = Utility.parseDate(maxParam, DynamicExtensionsUtility
						.getDateFormat(format));
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				cal.add(Calendar.DAY_OF_MONTH, 1);
				newDate = cal.getTime();
			}
			else if (mapStratergy < 0)
			{
				Date date = Utility.parseDate(minParam, DynamicExtensionsUtility
						.getDateFormat(format));
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				cal.add(Calendar.DAY_OF_MONTH, 1);
				newDate = cal.getTime();
			}
			else
			{
				Date date = Utility.parseDate(minParam, DynamicExtensionsUtility
						.getDateFormat(format));
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				newDate = cal.getTime();
			}

		}
		SimpleDateFormat formatter = new SimpleDateFormat(DynamicExtensionsUtility
				.getDateFormat(format));
		return formatter.format(newDate);
	}

	/**
	 * It will serach for the date_range or range rule & if found will return the minimun value specified
	 * in the range else will return empty string.
	 * @param attributeRules rule collection.
	 * @param paramName name of the parameter whose value is needed.
	 * @return min param value for range rule.
	 */
	private String getRangeRuleparam(Set<RuleInterface> attributeRules, String paramName)
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
					if (ruleParameter.getName().equalsIgnoreCase(paramName))
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
	 * @param mapStratergy
	 * @return valid numeric value.
	 * @throws ParseException
	 */
	private String getNumericValueForAttribute(BaseAbstractAttributeInterface attribute,
			int mapStratergy) throws ParseException
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

		String minParam = getRangeRuleparam(attributeRules, CategoryCSVConstants.MIN);
		String maxParam = getRangeRuleparam(attributeRules, CategoryCSVConstants.MAX);
		return getNumericAttributeValue(mapStratergy, minParam, maxParam);

	}

	/**
	 * This method will return the attribute value depending on the mapStratergy.
	 * If mapStratergy variable is negative it will return the value which is less than the
	 * range specified, if it is positive then it will return the value which is greater than
	 * the specified range and if it is zero then it will return the minimum value itself.
	 * (Boundry value.)
	 * @param mapStratergy map stratergy.
	 * @param minParam minimum value.
	 * @param maxParam
	 * @return
	 */
	private String getNumericAttributeValue(int mapStratergy, String minParam, String maxParam)
	{
		String value = "15";
		if (minParam != null && !"".equals(minParam))
		{
			if (mapStratergy < 0)
			{
				Integer newValue = Integer.valueOf(minParam) - 1;
				value = newValue.toString();
			}
			else if (mapStratergy > 0)
			{
				Integer newValue = Integer.valueOf(maxParam) + 1;
				value = newValue.toString();
			}
			else
			{
				value = minParam;
			}
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
					if (((AttributeInterface) ((CategoryAttribute) attribute)
							.getAbstractAttribute()).getAttributeTypeInformation() instanceof BooleanAttributeTypeInformation)
					{
						if (!(((object.toString().equals("1") || object.toString()
								.equalsIgnoreCase("true")) && (entryObject.getValue().toString()
								.equals("1") || entryObject.getValue().toString().equalsIgnoreCase(
								"true"))) || ((object.toString().equals("0") || object.toString()
								.equalsIgnoreCase("false")) && (entryObject.getValue().toString()
								.equals("0") || entryObject.getValue().toString().equalsIgnoreCase(
								"false")))))
						{
							throw new DynamicExtensionsSystemException("Data values for "
									+ attribute.getName() + " does not match. Retrieve value : "
									+ object.toString() + " Inserted value : "
									+ entryObject.getValue().toString());
						}
					}
					else if (((AttributeInterface) ((CategoryAttribute) attribute)
							.getAbstractAttribute()).getAttributeTypeInformation() instanceof FileAttributeTypeInformation)
					{
						//do not validate
					}
					else if (!entryObject.getValue().toString().equals(object.toString()))
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

	/**
	 * This method will create the Data Value map(Id to value map) for the Given Category Entity .
	 * It will put some hard coded values for the different attributes as follows.
	 * Date Attribute  = current date,
	 * Numeric Attribute = 15
	 * Boolean attribute = true.
	 * String attribute  = test String.
	 * other attribute = test String for other data type
	 * If the range is specified on the attribute then depending on the mapStrategry varible the values will
	 * be put.
	 * e.g if mapStrategry = 0 then min range value is used.
	 * if mapStrategry < 0 then value less than min range value is used.
	 * if mapStrategry > 0 then value greater than max range value is used
	 * @param rootCatEntity the root category entity for which to generate the map
	 * @param mapStrategry map generation strategy
	 * @return generated map.
	 * @throws ParseException
	 * @throws DynamicExtensionsSystemException
	 */
	public Map<Long, Object> createIdToValueMapForCategory(CategoryEntityInterface rootCatEntity,
			int mapStrategry) throws ParseException, DynamicExtensionsSystemException
	{
		Map<Long, Object> dataValue = new HashMap<Long, Object>();
		ContainerInterface containerInterface = (ContainerInterface) rootCatEntity
				.getContainerCollection().toArray()[0];
		for (CategoryAttributeInterface catAtt : rootCatEntity.getAllCategoryAttributes())
		{
			// put the different value for diff attribute type
			if (catAtt.getAbstractAttribute() instanceof AttributeInterface
					&& !catAtt.getIsRelatedAttribute())
			{
				AttributeInterface attribute = (AttributeInterface) catAtt.getAbstractAttribute();
				updateDataMapForIdValue(dataValue, catAtt, attribute, mapStrategry,
						containerInterface);
			}
			else if (!catAtt.getIsRelatedAttribute())
			{
				//multiselect case.
				AssociationInterface association = (AssociationInterface) catAtt
						.getAbstractAttribute();
				AttributeInterface multiselectAttr = (AttributeInterface) EntityManagerUtil
						.filterSystemAttributes(
								association.getTargetEntity().getAllAbstractAttributes())
						.iterator().next();
				Map<Long, Object> multiSelectDataValue = new HashMap<Long, Object>();
				for (int i = 0; i < 2; i++)
				{
					updateDataMapForIdValue(multiSelectDataValue, multiselectAttr, multiselectAttr,
							mapStrategry, containerInterface);
				}
				List multiselctValueList = new ArrayList();
				multiselctValueList.add(multiSelectDataValue);
				dataValue.put(catAtt.getId(), multiselctValueList);
			}
		}
		for (CategoryAssociationInterface catAssociation : rootCatEntity
				.getCategoryAssociationCollection())
		{
			List dataList = new ArrayList();
			CategoryEntityInterface targetCaEntity = catAssociation.getTargetCategoryEntity();
			dataList.add(createIdToValueMapForCategory(targetCaEntity, mapStrategry));
			if (targetCaEntity.getNumberOfEntries().equals(-1))
			{
				dataList.add(createIdToValueMapForCategory(targetCaEntity, mapStrategry));
			}
			dataValue.put(catAssociation.getId(), dataList);
		}
		return dataValue;
	}

	private void updateDataMapForIdValue(Map<Long, Object> dataValue,
			BaseAbstractAttributeInterface catAtt, AttributeInterface attribute, int mapStrategry,
			ContainerInterface containerInterface) throws ParseException,
			DynamicExtensionsSystemException
	{
		ControlInterface control = DynamicExtensionsUtility.getControlForAbstractAttribute(
				(AttributeMetadataInterface) catAtt, containerInterface);
		if (control instanceof EnumeratedControlInterface)
		{
			AttributeMetadataInterface attributeInterface = (AttributeMetadataInterface) catAtt;
			if (attributeInterface.getDataElement() != null)
			{
				for (PermissibleValueInterface permissibleValue : ((UserDefinedDE) ((AttributeMetadataInterface) catAtt)
						.getDataElement()).getPermissibleValueCollection())
				{
					dataValue.put(catAtt.getId(), permissibleValue.getValueAsObject().toString());
				}
			}
		}
		else
		{
			if (attribute.getAttributeTypeInformation() instanceof DateAttributeTypeInformation)
			{

				String value = getDateValueForAttribute(catAtt, mapStrategry);
				dataValue.put(catAtt.getId(), value);
			}
			else if (attribute.getAttributeTypeInformation() instanceof NumericTypeInformationInterface)
			{
				String value = getNumericValueForAttribute(catAtt, mapStrategry);
				dataValue.put(catAtt.getId(), value);
			}
			else if (attribute.getAttributeTypeInformation() instanceof BooleanAttributeTypeInformation)
			{
				dataValue.put(catAtt.getId(), "true");
			}
			else if (attribute.getAttributeTypeInformation() instanceof StringAttributeTypeInformation)
			{
				dataValue.put(catAtt.getId(), "test");
			}
			else if (attribute.getAttributeTypeInformation() instanceof FileAttributeTypeInformation)
			{
				dataValue.put(catAtt.getId(), getFileRecordValueForAttribute());
			}
			else
			{
				dataValue.put(catAtt.getId(), "test String for other data type");
			}
		}
	}
}
