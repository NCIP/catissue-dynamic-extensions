
package edu.common.dynamicextensions.ui.util;

/**
 * This class defines miscellaneous methods that are commonly used by many Control objects. *
 * @author chetan_patil
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import edu.common.dynamicextensions.domain.BooleanAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.BooleanTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BooleanValueInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.DataElementInterface;
import edu.common.dynamicextensions.domaininterface.DateTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.DateValueInterface;
import edu.common.dynamicextensions.domaininterface.DoubleTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.DoubleValueInterface;
import edu.common.dynamicextensions.domaininterface.FloatTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.FloatValueInterface;
import edu.common.dynamicextensions.domaininterface.IntegerTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.IntegerValueInterface;
import edu.common.dynamicextensions.domaininterface.LongTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.LongValueInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.ShortTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.ShortValueInterface;
import edu.common.dynamicextensions.domaininterface.SkipLogicAttributeInterface;
import edu.common.dynamicextensions.domaininterface.StringTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.StringValueInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AssociationControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.webui.util.ControlInformationObject;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.global.CommonServiceLocator;

public class ControlsUtility
{

	/**
	 * This method returns the default value of the PrimitiveAttribute for displaying in corresponding controls on UI.
	 * @param abstractAttribute the PrimitiveAttribute
	 * @return the Default Value of the PrimitiveAttribute
	 */
	public static String getDefaultValue(AbstractAttributeInterface abstractAttribute)
	{
		String defaultValue = null;
		AttributeTypeInformationInterface abstractAttributeType = null;

		if (abstractAttribute != null && abstractAttribute instanceof AttributeInterface)
		{
			abstractAttributeType = ((AttributeInterface) abstractAttribute)
						.getAttributeTypeInformation();
		}
		if (abstractAttributeType != null)
		{
			if (abstractAttributeType instanceof StringTypeInformationInterface)
			{
				StringTypeInformationInterface stringAttribute = (StringTypeInformationInterface) abstractAttributeType;
				if (stringAttribute != null)
				{
					defaultValue = getDefaultString(stringAttribute);
				}
			}
			else if (abstractAttributeType instanceof BooleanTypeInformationInterface)
			{
				BooleanTypeInformationInterface booleanAttribute = (BooleanTypeInformationInterface) abstractAttributeType;
				if (booleanAttribute != null)
				{
					defaultValue = getDefaultBoolean(booleanAttribute);
				}
			}
			else if (abstractAttributeType instanceof IntegerTypeInformationInterface)
			{
				IntegerTypeInformationInterface integerAttribute = (IntegerTypeInformationInterface) abstractAttributeType;
				if (integerAttribute != null)
				{
					defaultValue = getDefaultInteger(integerAttribute);
				}
			}
			else if (abstractAttributeType instanceof LongTypeInformationInterface)
			{
				LongTypeInformationInterface longAttribute = (LongTypeInformationInterface) abstractAttributeType;
				if (longAttribute != null)
				{
					defaultValue = getDefaultLong(longAttribute);
				}
			}
			else if (abstractAttributeType instanceof DoubleTypeInformationInterface)
			{
				DoubleTypeInformationInterface doubleAttribute = (DoubleTypeInformationInterface) abstractAttributeType;
				if (doubleAttribute != null)
				{
					defaultValue = getDefaultDouble(doubleAttribute);
				}
			}
			else if (abstractAttributeType instanceof FloatTypeInformationInterface)
			{
				FloatTypeInformationInterface floatAttribute = (FloatTypeInformationInterface) abstractAttributeType;
				if (floatAttribute != null)
				{
					defaultValue = getDefaultFloat(floatAttribute);
				}
			}
			else if (abstractAttributeType instanceof ShortTypeInformationInterface)
			{
				ShortTypeInformationInterface shortAttribute = (ShortTypeInformationInterface) abstractAttributeType;
				if (shortAttribute != null)
				{
					defaultValue = getDefaultShort(shortAttribute);
				}
			}
			else if (abstractAttributeType instanceof DateTypeInformationInterface)
			{
				DateTypeInformationInterface dateAttribute = (DateTypeInformationInterface) abstractAttributeType;
				if (dateAttribute != null)
				{
					defaultValue = getDefaultDate(dateAttribute);
				}
			}
		}
		return defaultValue;
	}

	private static String getDefaultString(StringTypeInformationInterface stringAttribute)
	{
		String defaultValue = null;
		StringValueInterface stringValue = (StringValueInterface) stringAttribute.getDefaultValue();
		if (stringValue != null)
		{
			defaultValue = stringValue.getValue();
		}
		return defaultValue;
	}

	private static String getDefaultBoolean(BooleanTypeInformationInterface booleanAttribute)
	{
		String defaultValue = null;
		BooleanValueInterface booleanValue = (BooleanValueInterface) booleanAttribute
				.getDefaultValue();
		if (booleanValue != null)
		{
			Boolean defaultBoolean = booleanValue.getValue();
			if (defaultBoolean != null)
			{
				defaultValue = defaultBoolean.toString();
			}
		}
		return defaultValue;
	}

	private static String getDefaultInteger(IntegerTypeInformationInterface integerAttribute)
	{
		String defaultValue = null;
		IntegerValueInterface integerValue = (IntegerValueInterface) integerAttribute
				.getDefaultValue();
		if (integerValue != null)
		{
			Integer defaultInteger = integerValue.getValue();
			if (defaultInteger != null)
			{
				defaultValue = defaultInteger.toString();
			}
		}
		return defaultValue;
	}

	private static String getDefaultLong(LongTypeInformationInterface longAttribute)
	{
		String defaultValue = null;
		LongValueInterface longValue = (LongValueInterface) longAttribute.getDefaultValue();
		if (longValue != null)
		{
			Long defaultLong = longValue.getValue();
			if (defaultLong != null)
			{
				defaultValue = defaultLong.toString();
			}
		}
		return defaultValue;
	}

	private static String getDefaultDouble(DoubleTypeInformationInterface doubleAttribute)
	{
		String defaultValue = null;
		DoubleValueInterface doubleValue = (DoubleValueInterface) doubleAttribute.getDefaultValue();
		if (doubleValue != null)
		{
			Double defaultDouble = doubleValue.getValue();
			if (defaultDouble != null)
			{
				defaultValue = defaultDouble.toString();
			}
		}
		return defaultValue;
	}

	private static String getDefaultFloat(FloatTypeInformationInterface floatAttribute)
	{
		String defaultValue = null;
		FloatValueInterface floatValue = (FloatValueInterface) floatAttribute.getDefaultValue();
		if (floatValue != null)
		{
			Float defaultFloat = floatValue.getValue();
			if (defaultFloat != null)
			{
				defaultValue = defaultFloat.toString();
			}
		}
		return defaultValue;
	}

	private static String getDefaultShort(ShortTypeInformationInterface shortAttribute)
	{
		String defaultValue = null;
		ShortValueInterface shortValue = (ShortValueInterface) shortAttribute.getDefaultValue();
		if (shortValue != null)
		{
			Short defaultShort = shortValue.getValue();
			if (defaultShort != null)
			{
				defaultValue = defaultShort.toString();
			}
		}
		return defaultValue;
	}

	private static String getDefaultDate(DateTypeInformationInterface dateAttribute)
	{
		String defaultValue = null;
		DateValueInterface dateValue = (DateValueInterface) dateAttribute.getDefaultValue();
		Locale locale= CommonServiceLocator.getInstance().getDefaultLocale();
		if (dateValue != null)
		{
			Date defaultDate = dateValue.getValue();
			if (defaultDate != null)
			{
				defaultValue = new SimpleDateFormat(getDateFormat(dateAttribute),locale)
						.format(defaultDate);
			}
		}
		return defaultValue;
	}

	/**
	 * This method returns the prescribed date format for the given DateAttributeTypeInformation
	 * @param attribute the DateAttributeTypeInformation
	 * @return the date format String
	 */
	public static String getDateFormat(AttributeTypeInformationInterface dateAttribute)
	{
		String dateFormat = null;
		/*
		 * While creating a category of type date if original attribute
		 * is of type String then line
		 * ((DateTypeInformationInterface) dateAttribute).getFormat();
		 * will throw a class cast exception
		 * In this case use DATE_ONLY_FORMAT as a date format
		 * Fixed by: Rajesh
		 * Reviewed by : Sujay
		 */
		if (dateAttribute instanceof DateTypeInformationInterface)
		{
			dateFormat = ((DateTypeInformationInterface) dateAttribute).getFormat();
		}
		if (dateFormat == null)
		{
			dateFormat = ProcessorConstants.DATE_ONLY_FORMAT;
		}
		return dateFormat;
	}
	/**
	 *
	 * @param nameValueList
	 */
	public static void sortNameValueList(List nameValueList)
	{
		if (nameValueList != null && !nameValueList.isEmpty())
		{
			Collections.sort(nameValueList, new Comparator<NameValueBean>()
			{

				public int compare(NameValueBean nameValueBean1, NameValueBean nameValueBean2)
				{
					return nameValueBean1.getName().compareTo(nameValueBean2.getName());
				}
			});
		}
	}

	/**
	 * This method populates the List of Values of the ListBox in the NameValueBean Collection.
	 * @return List of pair of Name and its corresponding Value.
	 */
	public static List<NameValueBean> populateListOfValues(ControlInterface control,List<String> sourceControlValue)
	{
		AttributeMetadataInterface attributeMetadataInterface = null;
		List<NameValueBean> nameValueBeanList = null;
		try
		{
			BaseAbstractAttributeInterface attribute = control.getBaseAbstractAttribute();
			if (attribute != null)
			{
				if (attribute instanceof AttributeMetadataInterface)
				{
					if (control.getIsSkipLogicTargetControl())
					{
						control.getSourceSkipControl().setSkipLogicControls(
								sourceControlValue);
					}
					attributeMetadataInterface = (AttributeMetadataInterface) attribute;
					if (!control.getIsSkipLogicLoadPermValues())
					{
						nameValueBeanList = getListOfPermissibleValues(attributeMetadataInterface);
					}
					else
					{
						List<PermissibleValueInterface> permissibleValueList = getSkipLogicPermissibleValues(control.getSourceSkipControl(),control,sourceControlValue);
						nameValueBeanList = getPermissibleValues(permissibleValueList, attributeMetadataInterface);
					}
				}
				else if (attribute instanceof AssociationInterface)
				{
					AssociationInterface association = (AssociationInterface) attribute;
					if (association.getIsCollection())
					{
						Collection<AbstractAttributeInterface> attributeCollection = association
								.getTargetEntity().getAllAbstractAttributes();
						Collection<AbstractAttributeInterface> filteredAttributeCollection = EntityManagerUtil
								.filterSystemAttributes(attributeCollection);
						List<AbstractAttributeInterface> attributesList = new ArrayList<AbstractAttributeInterface>(
								filteredAttributeCollection);

						attributeMetadataInterface = (AttributeMetadataInterface) attributesList
								.get(0);
						if (control.getIsSkipLogicTargetControl())
						{
							control.getSourceSkipControl().setSkipLogicControls(
									sourceControlValue);
						}
						if (!control.getIsSkipLogicLoadPermValues())
						{
							nameValueBeanList = getListOfPermissibleValues(attributeMetadataInterface);
						}
						else
						{
							List<PermissibleValueInterface> permissibleValueList = getSkipLogicPermissibleValues(control.getSourceSkipControl(),control,sourceControlValue);
							nameValueBeanList = getPermissibleValues(permissibleValueList, attributeMetadataInterface);
						}
					}
					else
					{
						EntityManagerInterface entityManager = EntityManager.getInstance();

						AssociationControlInterface associationControl = (AssociationControlInterface) control;
						Map<Long, List<String>> displayAttributeMap = null;

						String sepatator = associationControl.getSeparator();

						displayAttributeMap = entityManager
								.getRecordsForAssociationControl(associationControl);

						nameValueBeanList = getTargetEntityDisplayAttributeList(
								displayAttributeMap, sepatator);

						sortNameValueList(nameValueBeanList);
					}
				}
			}
		}
		catch (Exception exception)
		{
			throw new RuntimeException(exception);
		}
		if (attributeMetadataInterface != null)
		{
			DataElementInterface dataElement = attributeMetadataInterface.getDataElement();
			if (dataElement instanceof UserDefinedDEInterface)
			{
				UserDefinedDEInterface userDefinedDEInterface = (UserDefinedDEInterface) dataElement;
				if (userDefinedDEInterface != null && userDefinedDEInterface.getIsOrdered())
				{
					sortNameValueList(nameValueBeanList);
				}
			}
		}
		return nameValueBeanList;
	}
	/**
	 *
	 * @param selectedPermissibleValues
	 * @return
	 */
	public static SkipLogicAttributeInterface getSkipLogicAttributeForAttribute(
			List<PermissibleValueInterface> selectedPermissibleValues,
			AttributeMetadataInterface sourceAttributeMetadataInterface,
			AttributeMetadataInterface targetAttributeMetadataInterface)
	{
		SkipLogicAttributeInterface skipLogicAttributeInterface = null;
		if (sourceAttributeMetadataInterface.getAttributeTypeInformation() instanceof BooleanAttributeTypeInformation)
		{
			CategoryAttributeInterface categoryAttributeInterface = null;
			if (sourceAttributeMetadataInterface instanceof CategoryAttributeInterface)
			{
				categoryAttributeInterface = (CategoryAttributeInterface) sourceAttributeMetadataInterface;
			}
			if (categoryAttributeInterface != null)
			{
				for (SkipLogicAttributeInterface skipLogicAttribute : categoryAttributeInterface.getDependentSkipLogicAttributes())
				{
					if (skipLogicAttribute.getSourceSkipLogicAttribute()
							.equals(sourceAttributeMetadataInterface)
							&& skipLogicAttribute.getTargetSkipLogicAttribute()
									.equals(targetAttributeMetadataInterface))
					{
						skipLogicAttributeInterface = skipLogicAttribute;
						break;
					}
				}
			}
		}
		else
		{
			for (PermissibleValueInterface selectedPermissibleValue : selectedPermissibleValues)
			{
				Collection<PermissibleValueInterface> skipLogicPermissibleValues = sourceAttributeMetadataInterface
						.getSkipLogicPermissibleValues();
				if (skipLogicPermissibleValues != null)
				{
					for (PermissibleValueInterface skipLogicValue : skipLogicPermissibleValues)
					{
						for (SkipLogicAttributeInterface skipLogicAttribute : skipLogicValue
								.getDependentSkipLogicAttributes())
						{
							if (skipLogicValue.equals(selectedPermissibleValue)
									&& skipLogicAttribute
											.getSourceSkipLogicAttribute()
											.equals(
													sourceAttributeMetadataInterface)
									&& skipLogicAttribute
											.getTargetSkipLogicAttribute()
											.equals(
													targetAttributeMetadataInterface))
							{
								skipLogicAttributeInterface = skipLogicAttribute;
								break;
							}
						}
						if (skipLogicAttributeInterface != null)
						{
							break;
						}
					}
				}
				if (skipLogicAttributeInterface != null)
				{
					break;
				}
			}
		}
		return skipLogicAttributeInterface;
	}
	/**
	 *
	 * @param selectedPermissibleValues
	 * @return
	 */
	public static Collection<SkipLogicAttributeInterface> getSkipLogicAttributesForCheckBox(
						AttributeMetadataInterface attributeMetadataInterface)
	{
		Collection<SkipLogicAttributeInterface> skipLogicAttributes = null;
		CategoryAttributeInterface categoryAttributeInterface = null;
		if (attributeMetadataInterface instanceof CategoryAttributeInterface)
		{
			categoryAttributeInterface = (CategoryAttributeInterface) attributeMetadataInterface;
		}
		if (categoryAttributeInterface != null)
		{
			skipLogicAttributes = categoryAttributeInterface.getDependentSkipLogicAttributes();
		}
		return skipLogicAttributes;
	}

	/**
	 * @throws ParseException
	 *
	 */
	public static List<PermissibleValueInterface> getSkipLogicPermissibleValues(
			ControlInterface sourceControl, ControlInterface targetControl,List<String> values)
			throws ParseException
	{
		List<PermissibleValueInterface> skipLogicPermissibleValueList = new ArrayList<PermissibleValueInterface>();
		List<PermissibleValueInterface> permissibleValueList = new ArrayList<PermissibleValueInterface>();
		if (values != null)
		{
			for (String controlValue : values)
			{
				PermissibleValueInterface selectedPermissibleValue = null;
				AttributeMetadataInterface attributeMetadataInterface = ControlsUtility
						.getAttributeMetadataInterface(sourceControl
								.getBaseAbstractAttribute());
				if (attributeMetadataInterface != null)
				{
					if (controlValue != null && controlValue.length() > 0)
					{
						selectedPermissibleValue = attributeMetadataInterface
								.getAttributeTypeInformation()
								.getPermissibleValueForString(
										controlValue.toString());
					}
					permissibleValueList.add(selectedPermissibleValue);
				}
			}
			if (sourceControl.getIsSkipLogic())
			{
				AttributeMetadataInterface targetAttributeMetadataInterface = ControlsUtility
						.getAttributeMetadataInterface(targetControl
								.getBaseAbstractAttribute());
				for (PermissibleValueInterface selectedPermissibleValue : permissibleValueList)
				{
					AttributeMetadataInterface attributeMetadataInterface = ControlsUtility
							.getAttributeMetadataInterface(sourceControl
									.getBaseAbstractAttribute());
					PermissibleValueInterface skipLogicPermissibleValue = attributeMetadataInterface
							.getSkipLogicPermissibleValue(selectedPermissibleValue);
					if (skipLogicPermissibleValue != null)
					{
						Collection<SkipLogicAttributeInterface> skipLogicAttributes = skipLogicPermissibleValue
								.getDependentSkipLogicAttributes();
						for (SkipLogicAttributeInterface skipLogicAttributeInterface : skipLogicAttributes)
						{
							if (skipLogicAttributeInterface
									.getTargetSkipLogicAttribute().equals(
											targetAttributeMetadataInterface))
							{
								DataElementInterface dataElementInterface = skipLogicAttributeInterface
										.getDataElement();
								if (dataElementInterface instanceof UserDefinedDEInterface)
								{
									UserDefinedDEInterface userDefinedDEInterface = (UserDefinedDEInterface) dataElementInterface;
									skipLogicPermissibleValueList
											.addAll(userDefinedDEInterface
													.getPermissibleValues());
								}
							}
						}
					}
				}
			}
		}
		return skipLogicPermissibleValueList;
	}
	/**
	 *
	 * @param attribute
	 * @return
	 */
	public static AttributeMetadataInterface getAttributeMetadataInterface(BaseAbstractAttributeInterface attribute)
	{
		AttributeMetadataInterface attributeMetadataInterface = null;
		if (attribute != null)
		{
			if (attribute instanceof AttributeMetadataInterface)
			{
				if (attribute instanceof CategoryAttributeInterface)
				{
					CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface) attribute;
					AbstractAttributeInterface abstractAttribute = categoryAttribute.getAbstractAttribute();
					if (abstractAttribute instanceof AssociationInterface)
					{
						AssociationInterface association = (AssociationInterface) abstractAttribute;
						if (association.getIsCollection())
						{
							Collection<AbstractAttributeInterface> attributeCollection = association
									.getTargetEntity()
									.getAllAbstractAttributes();
							Collection<AbstractAttributeInterface> filteredAttributeCollection = EntityManagerUtil
									.filterSystemAttributes(attributeCollection);
							List<AbstractAttributeInterface> attributesList = new ArrayList<AbstractAttributeInterface>(
									filteredAttributeCollection);

							attributeMetadataInterface = (AttributeMetadataInterface) attributesList
									.get(0);
						}
					}
					else
					{
						attributeMetadataInterface = (AttributeMetadataInterface) attribute;
					}
				}
				else
				{
					attributeMetadataInterface = (AttributeMetadataInterface) attribute;
				}

			}
			else if (attribute instanceof AssociationInterface)
			{
				AssociationInterface association = (AssociationInterface) attribute;
				if (association.getIsCollection())
				{
					Collection<AbstractAttributeInterface> attributeCollection = association
							.getTargetEntity()
							.getAllAbstractAttributes();
					Collection<AbstractAttributeInterface> filteredAttributeCollection = EntityManagerUtil
							.filterSystemAttributes(attributeCollection);
					List<AbstractAttributeInterface> attributesList = new ArrayList<AbstractAttributeInterface>(
							filteredAttributeCollection);

					attributeMetadataInterface = (AttributeMetadataInterface) attributesList
							.get(0);
				}
			}
		}
		return attributeMetadataInterface;
	}

	/**
	 * Gets a list of permissible values for attribute or category attribute.
	 * @param attribute
	 * @return List of NameValueBean
	 */
	public static List<NameValueBean> getListOfPermissibleValues(
			AttributeMetadataInterface attribute)
	{
		List<PermissibleValueInterface> permissibleValueList = new ArrayList<PermissibleValueInterface>();
		DataElementInterface dataElement = attribute.getDataElement();
		if (dataElement instanceof UserDefinedDEInterface)
		{
			UserDefinedDEInterface userDefinedDEInterface = (UserDefinedDEInterface) dataElement;
			permissibleValueList.addAll(userDefinedDEInterface
					.getPermissibleValues());

		}
		return getPermissibleValues(permissibleValueList,attribute);
	}
	/**
	 *
	 * @param permissibleValueList
	 * @return
	 */
	private static List<NameValueBean> getPermissibleValues(List<PermissibleValueInterface> permissibleValueList,AttributeMetadataInterface attribute)
	{
		List<NameValueBean> nameValueBeanList = null;
		if (permissibleValueList != null)
		{
			nameValueBeanList = new ArrayList<NameValueBean>();
			NameValueBean nameValueBean = null;
			for (PermissibleValueInterface permissibleValue : permissibleValueList)
			{
				if (permissibleValue instanceof StringValueInterface)
				{
					nameValueBean = getPermissibleStringValue(permissibleValue);
				}
				else if (permissibleValue instanceof DateValueInterface)
				{
					DateTypeInformationInterface dateAttribute = (DateTypeInformationInterface) attribute;
					nameValueBean = getPermissibleDateValue(permissibleValue, dateAttribute);
				}
				else if (permissibleValue instanceof DoubleValueInterface)
				{
					nameValueBean = getPermissibleDoubleValue(permissibleValue);
				}
				else if (permissibleValue instanceof FloatValueInterface)
				{
					nameValueBean = getPermissibleFloatValue(permissibleValue);
				}
				else if (permissibleValue instanceof LongValueInterface)
				{
					nameValueBean = getPermissibleLongValue(permissibleValue);
				}
				else if (permissibleValue instanceof IntegerValueInterface)
				{
					nameValueBean = getPermissibleIntegerValue(permissibleValue);
				}
				else if (permissibleValue instanceof ShortValueInterface)
				{
					nameValueBean = getPermissibleShortValue(permissibleValue);
				}
				else if (permissibleValue instanceof BooleanValueInterface)
				{
					nameValueBean = getPermissibleBooleanValue(permissibleValue);
				}
				nameValueBeanList.add(nameValueBean);
			}
		}
		return nameValueBeanList;
	}
	private static List<NameValueBean> getTargetEntityDisplayAttributeList(
			Map<Long, List<String>> displayAttributeMap, String separator)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		List<NameValueBean> displayAttributeList = new ArrayList<NameValueBean>();

		Set<Map.Entry<Long, List<String>>> displayAttributeSet = displayAttributeMap.entrySet();
		for (Map.Entry<Long, List<String>> displayAttributeEntry : displayAttributeSet)
		{
			Long recordIdentifier = displayAttributeEntry.getKey();
			List<String> attributeList = displayAttributeEntry.getValue();

			NameValueBean nameValueBean = new NameValueBean();
			nameValueBean.setValue(recordIdentifier.toString());

			StringBuffer value = new StringBuffer();
			for (String attributeValue : attributeList)
			{
				value.append(attributeValue + separator);
			}
			value.delete(value.lastIndexOf(separator), value.length());

			nameValueBean.setName(value.toString());
			displayAttributeList.add(nameValueBean);
		}

		return displayAttributeList;
	}

	private static NameValueBean getPermissibleDateValue(
			PermissibleValueInterface permissibleValue, DateTypeInformationInterface dateAttribute)
	{
		DateValueInterface dateValue = (DateValueInterface) permissibleValue;
		NameValueBean nameValueBean = null;
		Locale locale= CommonServiceLocator.getInstance().getDefaultLocale();
		if (dateValue != null && dateValue.getValue() != null)
		{
			nameValueBean = new NameValueBean();
			String date = new SimpleDateFormat(getDateFormat(dateAttribute),locale).format(dateValue
					.getValue());
			nameValueBean.setName(date);
			nameValueBean.setValue(date);
		}
		return nameValueBean;
	}

	private static NameValueBean getPermissibleDoubleValue(
			PermissibleValueInterface permissibleValue)
	{
		DoubleValueInterface doubleValue = (DoubleValueInterface) permissibleValue;
		NameValueBean nameValueBean = null;

		if (doubleValue != null && doubleValue.getValue() != null)
		{
			nameValueBean = new NameValueBean();
			nameValueBean.setName(doubleValue.getValue().doubleValue());
			nameValueBean.setValue(doubleValue.getValue().doubleValue());
		}
		return nameValueBean;
	}

	private static NameValueBean getPermissibleFloatValue(PermissibleValueInterface permissibleValue)
	{
		FloatValueInterface floatValue = (FloatValueInterface) permissibleValue;
		NameValueBean nameValueBean = null;

		if (floatValue != null & floatValue.getValue() != null)
		{
			nameValueBean = new NameValueBean();
			nameValueBean.setName(floatValue.getValue().floatValue());
			nameValueBean.setValue(floatValue.getValue().floatValue());
		}
		return nameValueBean;
	}

	private static NameValueBean getPermissibleLongValue(PermissibleValueInterface permissibleValue)
	{
		LongValueInterface longValue = (LongValueInterface) permissibleValue;
		NameValueBean nameValueBean = null;

		if (longValue != null && longValue.getValue() != null)
		{
			nameValueBean = new NameValueBean();
			nameValueBean.setName(longValue.getValue().longValue());
			nameValueBean.setValue(longValue.getValue().longValue());
		}
		return nameValueBean;
	}

	private static NameValueBean getPermissibleIntegerValue(
			PermissibleValueInterface permissibleValue)
	{
		IntegerValueInterface integerValue = (IntegerValueInterface) permissibleValue;
		NameValueBean nameValueBean = null;

		if (integerValue != null && integerValue.getValue() != null)
		{
			nameValueBean = new NameValueBean();
			nameValueBean.setName(integerValue.getValue().intValue());
			nameValueBean.setValue(integerValue.getValue().intValue());
		}
		return nameValueBean;
	}

	private static NameValueBean getPermissibleShortValue(PermissibleValueInterface permissibleValue)
	{
		ShortValueInterface shortValue = (ShortValueInterface) permissibleValue;
		NameValueBean nameValueBean = null;

		if (shortValue != null && shortValue.getValue() != null)
		{
			nameValueBean = new NameValueBean();
			nameValueBean.setName(shortValue.getValue().shortValue());
			nameValueBean.setValue(shortValue.getValue().shortValue());
		}
		return nameValueBean;
	}

	private static NameValueBean getPermissibleBooleanValue(
			PermissibleValueInterface permissibleValue)
	{
		BooleanValueInterface booleanValue = (BooleanValueInterface) permissibleValue;
		NameValueBean nameValueBean = null;

		if (booleanValue != null && booleanValue.getValue() != null)
		{
			nameValueBean = new NameValueBean();
			nameValueBean.setName(booleanValue.getValue().booleanValue());
			nameValueBean.setValue(booleanValue.getValue().booleanValue());
		}
		return nameValueBean;
	}

	private static NameValueBean getPermissibleStringValue(
			PermissibleValueInterface permissibleValue)
	{
		StringValueInterface stringValue = (StringValueInterface) permissibleValue;
		NameValueBean nameValueBean = null;

		if (stringValue != null && stringValue.getValue() != null)
		{
			nameValueBean = new NameValueBean();
			nameValueBean.setName(stringValue.getValue().trim());
			nameValueBean.setValue(stringValue.getValue().trim());
		}
		return nameValueBean;
	}

	/**
	 *	Added by Preeti
	 * @param entityInterface
	 * @param sequenceNumbers
	 */
	public static void reinitializeSequenceNumbers(Collection<ControlInterface> controlsCollection,
			String controlsSequenceNumbers)
	{
		if ((controlsCollection != null) && (controlsSequenceNumbers != null))
		{
			ControlInterface control;
			Integer[] sequenceNumbers = DynamicExtensionsUtility.convertToIntegerArray(
					controlsSequenceNumbers, ProcessorConstants.CONTROLS_SEQUENCE_NUMBER_SEPARATOR);
			if (sequenceNumbers != null)
			{
				for (int i = 0; i < sequenceNumbers.length; i++)
				{
					control = DynamicExtensionsUtility.getControlBySequenceNumber(
							controlsCollection, sequenceNumbers[i].intValue());
					if (control != null)
					{
						control.setSequenceNumber(Integer.valueOf(i + 1));
					}
				}
			}
		}
	}

	/**
	 *
	 * @param containerInterface containerInterface
	 * @return List ChildList
	 * @throws DynamicExtensionsSystemException DynamicExtensionsSystemException
	 */
	public static List getChildList(ContainerInterface containerInterface)
			throws DynamicExtensionsSystemException
	{
		List<ControlInformationObject> childList = new ArrayList<ControlInformationObject>();
		if (containerInterface != null)
		{
			Collection controlCollection = containerInterface.getControlCollection();
			ControlInterface controlInterface = null;
			ControlInformationObject controlInformationObject = null;
			String controlCaption = null, controlDatatype = null, controlSequenceNumber = null, controlName = null;
			ControlConfigurationsFactory controlConfigurationsFactory = ControlConfigurationsFactory
					.getInstance();
			if (controlCollection != null)
			{
				for (int counter = 1; counter <= controlCollection.size(); counter++)
				{
					controlInterface = DynamicExtensionsUtility.getControlBySequenceNumber(
							controlCollection, counter);
					if (controlInterface != null && controlInterface.getCaption() != null
							&& !controlInterface.getCaption().equals(""))
					{
						controlCaption = controlInterface.getCaption();
							controlSequenceNumber = controlInterface.getSequenceNumber().toString();
							controlName = DynamicExtensionsUtility.getControlName(controlInterface);
							if (controlName.equals(ProcessorConstants.ADD_SUBFORM_CONTROL))
							{
								controlDatatype = ProcessorConstants.ADD_SUBFORM_TYPE;
							}
							else
							{
								controlDatatype = getControlCaption(controlConfigurationsFactory
										.getControlDisplayLabel(controlName));
							}
							controlInformationObject = new ControlInformationObject(controlCaption,
									controlDatatype, controlSequenceNumber);
							childList.add(controlInformationObject);
					}
				}
			}
		}
		return childList;
	}

	/**
	 *
	 * @param captionKey String captionKey
	 * @return String ControlCaption
	 */
	public static String getControlCaption(String captionKey)
	{
		if (captionKey != null)
		{
			ResourceBundle resourceBundle = ResourceBundle.getBundle("ApplicationResources");
			if (resourceBundle != null)
			{
				return resourceBundle.getString(captionKey);
			}
		}
		return null;
	}
	/**
	 * @throws ParseException
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsSystemException
	 *
	 */
	public static Object getAttributeValueForSkipLogicAttributesFromValueMap(
			Map<BaseAbstractAttributeInterface, Object> fullValueMap,
			Map<BaseAbstractAttributeInterface, Object> valueMap,
			BaseAbstractAttributeInterface skipLogicAttribute,
			boolean isSameContainerControl, List<Object> values,
			Integer entryNumber, Integer mapentryNumber)
	{
		if (!values.isEmpty())
		{
			return values.get(0);
		}
		for (Map.Entry<BaseAbstractAttributeInterface, Object> entry : valueMap.entrySet())
		{
			BaseAbstractAttributeInterface attribute = entry.getKey();
			if (attribute instanceof CategoryAttributeInterface)
			{
				CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface) attribute;
				if ((!isSameContainerControl && categoryAttribute
						.equals(skipLogicAttribute))
						|| (isSameContainerControl
								&& categoryAttribute
										.equals(skipLogicAttribute) && entryNumber
								.equals(mapentryNumber)))
				{
					if (entry.getValue() != null)
					{
						values.add(entry.getValue());
					}
					return entry.getValue();
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
					getAttributeValueForSkipLogicAttributesFromValueMap(
							fullValueMap, map, skipLogicAttribute,
							isSameContainerControl, values, entryNumber,
							rowNumber);
				}
			}
		}
		return null;
	}
	/**
	 *
	 * @param rowId
	 * @param isSameContainerControl
	 * @param cardinality
	 * @param fullValueMap
	 * @param entry
	 * @param control
	 * @throws DynamicExtensionsSystemException
	 */
	private static void setValueMapForEnumeratedControls(Integer rowId,boolean isSameContainerControl,boolean cardinality,Map<BaseAbstractAttributeInterface, Object> fullValueMap,Map.Entry<BaseAbstractAttributeInterface, Object> entry,ControlInterface control) throws DynamicExtensionsSystemException
	{
		if (rowId == null && isSameContainerControl && cardinality)
		{
			rowId = Integer.valueOf(-1);
		}
		Object value = null;
		List<Object> values = new ArrayList<Object>();
		getAttributeValueForSkipLogicAttributesFromValueMap(
				fullValueMap, fullValueMap
						, control
						.getSourceSkipControl()
						.getBaseAbstractAttribute(), false,
				values, Integer.valueOf(rowId), Integer
						.valueOf(rowId));

		if (!values.isEmpty())
		{
			value = values.get(0);
		}
		control.getSourceSkipControl().setValue(value);
		control.getSourceSkipControl().setSkipLogicControls();
		if (control.getIsSkipLogicLoadPermValues())
		{
			List<String> sourceControlValues = null;
			boolean isValuePresent = false;
			PermissibleValueInterface permissibleValueInterface = null;
			List<PermissibleValueInterface> permissibleValueList = null;
			if (control.getSourceSkipControl() != null)
			{
				sourceControlValues = control.getSourceSkipControl().getValueAsStrings();
			}
			try
			{
				permissibleValueInterface = ((AttributeMetadataInterface) control
						.getBaseAbstractAttribute())
						.getAttributeTypeInformation()
						.getPermissibleValueForString(
								entry.getValue().toString());
				permissibleValueList = getSkipLogicPermissibleValues(
						control.getSourceSkipControl(), control, sourceControlValues);
			}
			catch (ParseException e)
			{
				throw new DynamicExtensionsSystemException("ParseException",e);
			}
			if (permissibleValueList != null && permissibleValueInterface != null)
			{
				for (PermissibleValueInterface permissibleValue : permissibleValueList)
				{
					if (permissibleValue.getValueAsObject().equals(
							permissibleValueInterface.getValueAsObject()))
					{
						isValuePresent = true;
						break;
					}
				}
			}
			if(!isValuePresent)
			{
				entry.setValue(null);
			}
		}
		else
		{
			if (!control.getIsEnumeratedControl())
			{
				entry.setValue(null);
			}
		}
	}
	/**
	 * @throws ParseException
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsSystemException
	 *
	 */
	public static void populateAttributeValueMapForSkipLogicAttributes(
			Map<BaseAbstractAttributeInterface, Object> fullValueMap,
			Map<BaseAbstractAttributeInterface, Object> valueMap,
			Integer rowId,boolean cardinality,String controlName,List<ControlInterface> controlsList)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		for (Map.Entry<BaseAbstractAttributeInterface, Object> entry : valueMap.entrySet())
		{
			BaseAbstractAttributeInterface attribute = entry.getKey();
			if (attribute instanceof CategoryAttributeInterface)
			{
				CategoryAttributeInterface categoryAttributeInterface = (CategoryAttributeInterface) attribute;
				if (categoryAttributeInterface != null)
				{
					boolean isSameContainerControl = false;
					String sourceControlName = null;
					ContainerInterface controlContainerInterface = DynamicExtensionsUtility
					.getContainerForAbstractEntity(categoryAttributeInterface
							.getCategoryEntity());

					ControlInterface control = DynamicExtensionsUtility
					.getControlForAbstractAttribute(
							(AttributeMetadataInterface) categoryAttributeInterface,
							controlContainerInterface);
					if (control != null && control.getIsSkipLogicTargetControl())
					{
						boolean found = false;
						for (ControlInterface targetSkipControl : controlsList)
						{
							if (control.equals(targetSkipControl))
							{
								found = true;
								break;
							}
						}
						if (control.getSourceSkipControl().getParentContainer()
								.equals(control.getParentContainer()))
						{
							isSameContainerControl = true;
						}
						else
						{
							isSameContainerControl = false;
						}
						Integer controlSequenceNumber = control
								.getSequenceNumber();
						if (controlSequenceNumber != null)
						{
							sourceControlName = control.getSourceSkipControl()
									.getHTMLComponentName();
							if (rowId != null && isSameContainerControl
									&& cardinality
									&& !rowId.equals(Integer.valueOf(-1)))
							{
								sourceControlName = sourceControlName + "_"
										+ rowId;
							}
						}
						if (found && control.getIsSkipLogicTargetControl()
								&& controlName.equals(sourceControlName))
						{
							setValueMapForEnumeratedControls(rowId,
									isSameContainerControl, cardinality,
									fullValueMap, entry, control);
						}
					}
				}
			}
			else if (attribute instanceof CategoryAssociationInterface)
			{
				List<Map<BaseAbstractAttributeInterface, Object>> attributeValueMapList = (List<Map<BaseAbstractAttributeInterface, Object>>) entry
						.getValue();
				boolean oneToManyCardinality = false;
				CategoryAssociationInterface categoryAssociation = (CategoryAssociationInterface) attribute;
				if (categoryAssociation.getTargetCategoryEntity().getNumberOfEntries() == -1)
				{
					oneToManyCardinality = true;
				}
				Integer entryNumber = 0;
				for (Map<BaseAbstractAttributeInterface, Object> map : attributeValueMapList)
				{
					entryNumber++;
					populateAttributeValueMapForSkipLogicAttributes(fullValueMap, map,
							entryNumber,oneToManyCardinality,controlName,controlsList);
				}
			}
		}
	}

}
