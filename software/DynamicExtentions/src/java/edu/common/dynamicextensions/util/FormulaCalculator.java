package edu.common.dynamicextensions.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.NumericAttributeTypeInformation;
import edu.common.dynamicextensions.domain.PathAssociationRelationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CalculatedAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.parser.FormulaParser;
import edu.common.dynamicextensions.validation.ValidatorUtil;
import edu.wustl.common.util.global.CommonServiceLocator;



/**
 *
 * @author rajesh_patil
 *
 */
public class FormulaCalculator
{
	/**
	 *
	 */
	private FormulaParser formulaParser = null;

	/**
	 *
	 */
	public FormulaCalculator()
	{
		this.formulaParser = new FormulaParser();
	}
	/**
	 *
	 */
	public FormulaCalculator(FormulaParser formulaParser)
	{
		this.formulaParser = formulaParser;
	}
	/**
	 *
	 * @param attributeValueMap
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws ParseException
	 * @throws DynamicExtensionsSystemException
	 */
	public String evaluateFormula(
Map<BaseAbstractAttributeInterface, Object> attributeValueMap,
			CategoryAttributeInterface categoryAttributeInterface, CategoryInterface category,
			Integer entryNumber) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException
			{
		Date dateValue = null;
		String value = "";
		Double formulaValue = null;
		Map<BaseAbstractAttributeInterface, Object> attributeValueMapForValidation = new HashMap<BaseAbstractAttributeInterface, Object>();
		boolean allCalculatedAttributesPresent = true;
		boolean isSameCatrgoryEntityAttribute = false;
		List<String> values = new ArrayList<String>();
		FormulaParser formulaParser = getFormulaParser();
		String formula = categoryAttributeInterface.getFormula().getExpression();
		for (CalculatedAttributeInterface calculatedAttributeInterface : categoryAttributeInterface
				.getCalculatedCategoryAttributeCollection())
		{
			CategoryAttributeInterface calculatedAttribute = calculatedAttributeInterface
					.getSourceForCalculatedAttribute();
			Map<String, String> calculatedKeyValueMap = new HashMap<String, String>();
			values.clear();
			if (calculatedAttribute.getCategoryEntity().equals(categoryAttributeInterface.getCategoryEntity()))
			{
				isSameCatrgoryEntityAttribute = true;
			}
			else
			{
				isSameCatrgoryEntityAttribute = false;
			}
			if (!isSameCatrgoryEntityAttribute && (calculatedAttribute.getCategoryEntity().getNumberOfEntries() == -1))
			{
				evaluateFormulaForMultilineCalulatedAttribute(
						attributeValueMap, calculatedAttribute,
						calculatedKeyValueMap, 1);
				if (!calculatedKeyValueMap.isEmpty())
				{
					attributeValueMapForValidation.put(calculatedAttribute, calculatedKeyValueMap);
				}
				else
				{
					allCalculatedAttributesPresent = false;
				}
			}
			else
			{
				evaluateFormulaForCalulatedAttribute(attributeValueMap,
						calculatedAttribute, isSameCatrgoryEntityAttribute,
						values, entryNumber, entryNumber);
				if (!values.isEmpty())
				{
					attributeValueMapForValidation.put(calculatedAttribute, values.get(0));
				}
				else
				{
					allCalculatedAttributesPresent = false;
				}
			}
		}

		List<String> errorList = new ArrayList<String>();
		Set<Map.Entry<BaseAbstractAttributeInterface, Object>> attributeSet = attributeValueMapForValidation
		.entrySet();
		for (Map.Entry<BaseAbstractAttributeInterface, Object> attributeValueNode : attributeSet)
		{
			CategoryAttributeInterface calculatedAttribute = (CategoryAttributeInterface) attributeValueNode
					.getKey();
			if (!(!(calculatedAttribute.getCategoryEntity()
					.equals(categoryAttributeInterface.getCategoryEntity())) && (calculatedAttribute
							.getCategoryEntity().getNumberOfEntries() == -1)))
			{
				errorList.addAll(ValidatorUtil.validateAttributes(
						attributeValueNode, attributeValueNode.getKey().getName()));
			}
			else
			{
				List<PathAssociationRelationInterface> pathAssociationCollection = calculatedAttribute
				.getCategoryEntity().getPath()
				.getSortedPathAssociationRelationCollection();
				PathAssociationRelationInterface pathAssoRelationInterface = pathAssociationCollection
				.get(pathAssociationCollection.size() - 1);
				String attributeName = calculatedAttribute.getCategoryEntity()
				.getEntity().getName()
				+ "_"
				+ pathAssoRelationInterface
				.getTargetInstanceId().toString()
				+ "_"
				+ calculatedAttribute.getAbstractAttribute().getName();
				StringBuffer newAttribute = new StringBuffer();
				Map<String, String> entryValueMap = (Map<String, String>) attributeValueNode
						.getValue();
				int size = entryValueMap.entrySet().size();
				int count = 0;
				for (Map.Entry<String, String> mapEntry : entryValueMap.entrySet())
				{
					count++;
					newAttribute.append(mapEntry.getKey());
					if (count < size)
					{
						newAttribute.append(",");
					}
				}
				formula = formula.replaceAll(attributeName, newAttribute.toString());
			}
		}
		formulaParser.parseExpression(formula);
		if (allCalculatedAttributesPresent)
		{
			Set<Entry<BaseAbstractAttributeInterface, Object>> entries = attributeValueMapForValidation
					.entrySet();
			for (Map.Entry<BaseAbstractAttributeInterface, Object> entry : entries)
			{
				CategoryAttributeInterface attribute = (CategoryAttributeInterface) entry.getKey();
				List<PathAssociationRelationInterface> pathAssociationCollection = attribute
						.getCategoryEntity()
				.getPath().getSortedPathAssociationRelationCollection();
				PathAssociationRelationInterface pathAssoRelationInterface = pathAssociationCollection
				.get(pathAssociationCollection.size() - 1);
				AttributeMetadataInterface attributeInterface = (AttributeMetadataInterface) attribute
						.getAbstractAttribute();
				if (attributeInterface.getAttributeTypeInformation() instanceof DateAttributeTypeInformation)
				{
					dateValue = getDate(entry.getValue().toString(), attributeInterface);
					Integer defaultValue = dateValue.getDate();
					formulaParser.setVariableValue(attribute.getCategoryEntity()
							.getEntity().getName()
							+ "_"
							+ pathAssoRelationInterface
							.getTargetInstanceId().toString()
							+ "_"
							+ attributeInterface.getName(),
							defaultValue);
				}
				else
				{
					if (!(!(attribute.getCategoryEntity()
							.equals(categoryAttributeInterface.getCategoryEntity())) && (attribute
									.getCategoryEntity().getNumberOfEntries() == -1)))
					{
						PermissibleValueInterface permissibleValueInterface = null;
						try
						{
							permissibleValueInterface = attributeInterface.getAttributeTypeInformation().getPermissibleValueForString(entry.getValue().toString());
						}
						catch (Exception e)
						{
							return "";
						}
						if (permissibleValueInterface != null)
						{
							formulaParser.setVariableValue(attribute.getCategoryEntity()
									.getEntity().getName()
									+ "_"
									+ pathAssoRelationInterface
									.getTargetInstanceId().toString()
									+ "_"
									+ attributeInterface.getName(),
									permissibleValueInterface.getValueAsObject());
						}
					}
					else
					{
						Map<String, String> entryValueMap = (Map<String, String>) entry.getValue();

						for (Map.Entry<String, String> mapEntry : entryValueMap.entrySet())
						{
							PermissibleValueInterface permissibleValueInterface = null;
							try
							{
								permissibleValueInterface = attributeInterface
								.getAttributeTypeInformation()
								.getPermissibleValueForString(
										mapEntry.getValue());
							}
							catch (Exception e)
							{
								return "";
							}
							if (permissibleValueInterface != null)
							{
								formulaParser.setVariableValue(mapEntry
										.getKey(), permissibleValueInterface
										.getValueAsObject());
							}
						}
					}
				}
			}
			formulaValue = formulaParser.evaluateExpression();
			if (formulaValue != null)
			{
				if (((AttributeMetadataInterface) categoryAttributeInterface)
						.getAttributeTypeInformation() instanceof DateAttributeTypeInformation)
				{
					value = setDate(dateValue, formulaValue, (AttributeMetadataInterface) categoryAttributeInterface);
				}
				else
				{
					try
					{
						value = ((NumericAttributeTypeInformation) ((AttributeMetadataInterface) categoryAttributeInterface)
								.getAttributeTypeInformation())
								.getFormattedValue(formulaValue);
					}
					catch (Exception e)
					{
						return "";
					}
				}
			}
		}
		return value;
			}
	/**
	 *
	 * @param attributeValueMap
	 * @return
	 * @throws ParseException
	 */
	private void evaluateFormulaForMultilineCalulatedAttribute(
			Map<BaseAbstractAttributeInterface, Object> attributeValueMap,
			CategoryAttributeInterface calculatedAttribute,
			Map<String, String> calculatedKeyValueMap, int rowCount)
	{
		Set<Entry<BaseAbstractAttributeInterface, Object>> entries = attributeValueMap.entrySet();
		for (Map.Entry<BaseAbstractAttributeInterface, Object> entry : entries)
		{
			BaseAbstractAttributeInterface attribute = entry.getKey();
			if (attribute instanceof CategoryAttributeInterface)
			{
				CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface) attribute;
				if (categoryAttribute.equals(calculatedAttribute))
				{
					String value = (String) entry.getValue();
					if ((value != null) && (value.length() > 0))
					{
						List<PathAssociationRelationInterface> pathAssociationCollection = categoryAttribute
						.getCategoryEntity().getPath()
						.getSortedPathAssociationRelationCollection();
						PathAssociationRelationInterface pathAssoRelationInterface = pathAssociationCollection
						.get(pathAssociationCollection.size() - 1);
						calculatedKeyValueMap.put(categoryAttribute
								.getCategoryEntity().getEntity().getName()
								+ "_"
								+ pathAssoRelationInterface
								.getTargetInstanceId().toString()
								+ "_"
								+ categoryAttribute.getAbstractAttribute()
								.getName() + "_" + rowCount, value);

					}
				}
			}
			else if (attribute instanceof CategoryAssociationInterface)
			{
				int count = 0;
				List<Map<BaseAbstractAttributeInterface, Object>> attributeValueMapList = (List<Map<BaseAbstractAttributeInterface, Object>>) entry
						.getValue();
				for (Map<BaseAbstractAttributeInterface, Object> map : attributeValueMapList)
				{
					count ++;
					evaluateFormulaForMultilineCalulatedAttribute(map,
							calculatedAttribute, calculatedKeyValueMap,count);
				}
			}
		}
	}
	/**
	 *
	 * @param dateValue
	 * @param value
	 * @param attributeInterface
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private Date getDate(String value, AttributeMetadataInterface attributeInterface)
			throws DynamicExtensionsSystemException
	{
		Date dateValue = null;
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ControlsUtility
				.getDateFormat(attributeInterface.getAttributeTypeInformation()),locale);
		simpleDateFormat.setLenient(true);
		try
		{
			dateValue = simpleDateFormat.parse(value);
		}
		catch (ParseException e)
		{
			throw new DynamicExtensionsSystemException("ParseException",e);
		}

		if (dateValue.getHours() >= ProcessorConstants.DATE_TIME_FORMAT_ROUND_OFF)
		{
			dateValue.setHours(dateValue.getHours() - ProcessorConstants.DATE_TIME_FORMAT_ROUND_OFF);
			dateValue.setDate(dateValue.getDate()+ 1);
		}
		return dateValue;
	}
	/**
	 *
	 * @param dateValue
	 * @param formulaValue
	 * @param attributeInterface
	 * @return
	 */
	private String setDate(Date dateValue, Double formulaValue,
			AttributeMetadataInterface attributeInterface)
	{
		String value ="";
		if ((dateValue != null) && (formulaValue != null))
		{
			dateValue.setDate(Integer.valueOf(formulaValue.intValue()));
			Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ControlsUtility
					.getDateFormat(attributeInterface
							.getAttributeTypeInformation()),locale);
			simpleDateFormat.setLenient(true);
			value = simpleDateFormat.format(dateValue);
		}
		return value;
	}
	/**
	 *
	 * @param attributeValueMap
	 * @return
	 * @throws ParseException
	 */
	private void evaluateFormulaForCalulatedAttribute(
			Map<BaseAbstractAttributeInterface, Object> attributeValueMap,
			CategoryAttributeInterface calculatedAttribute, boolean isSameCategoryEntityAttribute,
			List<String> values, Integer entryNumber, Integer mapentryNumber)
	{
		if (!values.isEmpty())
		{
			return;
		}
		Set<Entry<BaseAbstractAttributeInterface, Object>> entries = attributeValueMap.entrySet();
		for (Map.Entry<BaseAbstractAttributeInterface, Object> entry : entries)
		{
			BaseAbstractAttributeInterface attribute = entry.getKey();
			if (attribute instanceof CategoryAttributeInterface)
			{
				CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface) attribute;
				if ((!isSameCategoryEntityAttribute && categoryAttribute
						.equals(calculatedAttribute))
						|| (isSameCategoryEntityAttribute
								&& categoryAttribute
								.equals(calculatedAttribute) && entryNumber
								.equals(mapentryNumber)))
				{
					String value = (String) entry.getValue();
					if ((value != null) && (value.length() > 0))
					{
						values.add(value);
						return;
					}
				}
			}
			else if (attribute instanceof CategoryAssociationInterface)
			{
				Integer rowNumber = 0;
				List<Map<BaseAbstractAttributeInterface, Object>> attributeValueMapList = (List<Map<BaseAbstractAttributeInterface, Object>>) entry
						.getValue();
				for (Map<BaseAbstractAttributeInterface, Object> map : attributeValueMapList)
				{
					rowNumber++;
					evaluateFormulaForCalulatedAttribute(map,calculatedAttribute,isSameCategoryEntityAttribute,values,entryNumber,rowNumber);
				}
			}
		}
	}
	/**
	 * @throws DynamicExtensionsApplicationException
	 * @throws ParseException
	 * @throws DynamicExtensionsSystemException
	 *
	 */
	public String setDefaultValueForCalculatedAttributes(
			CategoryAttributeInterface categoryAttribute, CategoryInterface category)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		StringBuffer message = new StringBuffer("");
		Double formulaValue = null;
		Date dateValue = null;
		Map<BaseAbstractAttributeInterface, Object> attributeValueMap = new HashMap<BaseAbstractAttributeInterface, Object>();
		boolean allCalculatedAttributesPresent = true;
		FormulaParser formulaParser = getFormulaParser();
		formulaParser.parseExpression(categoryAttribute.getFormula().getExpression());
		for (CalculatedAttributeInterface calculatedAttributeInterface : categoryAttribute
				.getCalculatedCategoryAttributeCollection())
		{
			CategoryAttributeInterface calculatedAttribute = calculatedAttributeInterface
					.getSourceForCalculatedAttribute();
			if (calculatedAttribute.getDefaultValuePermissibleValue() == null)
			{
				allCalculatedAttributesPresent = false;
			}
			else
			{
				String value = "";
				if (((AttributeMetadataInterface)calculatedAttribute).getAttributeTypeInformation() instanceof DateAttributeTypeInformation)
				{
					Locale locale = CommonServiceLocator.getInstance()
					.getDefaultLocale();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
							ControlsUtility
							.getDateFormat(((AttributeMetadataInterface) calculatedAttribute)
									.getAttributeTypeInformation()),
									locale);
					simpleDateFormat.setLenient(true);
					value = simpleDateFormat
					.format((Date) calculatedAttribute
							.getDefaultValuePermissibleValue()
							.getValueAsObject());
				}
				else
				{
					value = calculatedAttribute.getDefaultValue();
				}
				attributeValueMap.put(calculatedAttribute,value);
			}
		}
		List<String> errorList = new ArrayList<String>();
		Set<Map.Entry<BaseAbstractAttributeInterface, Object>> attributeSet = attributeValueMap
		.entrySet();
		for (Map.Entry<BaseAbstractAttributeInterface, Object> attributeValueNode : attributeSet)
		{
			errorList.addAll(ValidatorUtil.validateAttributes(
					attributeValueNode, attributeValueNode.getKey().getName()));
		}

		if (allCalculatedAttributesPresent && errorList.isEmpty())
		{
			Set<Entry<BaseAbstractAttributeInterface, Object>> entries = attributeValueMap
					.entrySet();
			for (Map.Entry<BaseAbstractAttributeInterface, Object> entry : entries)
			{
				CategoryAttributeInterface attribute = (CategoryAttributeInterface) entry.getKey();
				AttributeMetadataInterface attributeInterface = (AttributeMetadataInterface) attribute
						.getAbstractAttribute();

				List<PathAssociationRelationInterface> pathAssociationCollection = attribute
						.getCategoryEntity()
				.getPath().getSortedPathAssociationRelationCollection();
				PathAssociationRelationInterface pathAssoRelationInterface = pathAssociationCollection
				.get(pathAssociationCollection.size() - 1);
				if (attributeInterface.getAttributeTypeInformation() instanceof DateAttributeTypeInformation)
				{
					dateValue = getDate(entry.getValue().toString(), attributeInterface);
					Integer defaultValue = dateValue.getDate();
					formulaParser.setVariableValue(attribute.getCategoryEntity()
							.getEntity().getName()
							+ "_"
							+ pathAssoRelationInterface
							.getTargetInstanceId().toString()
							+ "_"
							+ attributeInterface.getName(),
							defaultValue);
				}
				else
				{
					PermissibleValueInterface permissibleValueInterface = null;
					try
					{
						permissibleValueInterface = attributeInterface.getAttributeTypeInformation().getPermissibleValueForString(entry.getValue().toString());
					}
					catch (ParseException e)
					{
						throw new DynamicExtensionsSystemException("ParseException",e);
					}

					formulaParser.setVariableValue(attribute.getCategoryEntity()
							.getEntity().getName()
							+ "_"
							+ pathAssoRelationInterface
							.getTargetInstanceId().toString()
							+ "_"
							+ attribute.getAbstractAttribute().getName(),
							permissibleValueInterface.getValueAsObject());
				}
			}
			formulaValue = formulaParser.evaluateExpression();
			if (formulaValue != null)
			{
				PermissibleValueInterface defaultValue = null;
				if (((AttributeMetadataInterface) categoryAttribute)
						.getAttributeTypeInformation() instanceof DateAttributeTypeInformation)
				{
					if (dateValue != null)
					{
						String value = setDate(dateValue, formulaValue,
								(AttributeMetadataInterface) categoryAttribute);
						try
						{
							defaultValue = ((AttributeMetadataInterface) categoryAttribute)
							.getAttributeTypeInformation()
							.getPermissibleValueForString(value);
						}
						catch (ParseException e)
						{
							throw new DynamicExtensionsSystemException("ParseException",e);
						}
					}
				}
				else
				{
					try
					{
						NumericAttributeTypeInformation numericAttributeTypeInfo = ((NumericAttributeTypeInformation) ((AttributeMetadataInterface) categoryAttribute)
								.getAttributeTypeInformation());
						defaultValue = numericAttributeTypeInfo
						.getPermissibleValueForString(numericAttributeTypeInfo
								.getFormattedValue(formulaValue));
					}
					catch (ParseException e)
					{
						throw new DynamicExtensionsSystemException("ParseException",e);
					}
				}
				categoryAttribute.setDefaultValue(defaultValue);
			}
		}
		else if (!errorList.isEmpty())
		{
			for (String error : errorList)
			{
				message.append(error);
			}
		}
		return message.toString();
	}
	/**
	 *
	 * @return
	 */
	public FormulaParser getFormulaParser()
	{
		return formulaParser;
	}
	/**
	 *
	 * @param formulaParser
	 */
	public void setFormulaParser(FormulaParser formulaParser)
	{
		this.formulaParser = formulaParser;
	}
}
