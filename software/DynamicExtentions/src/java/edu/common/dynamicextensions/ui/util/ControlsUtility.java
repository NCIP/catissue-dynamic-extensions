
package edu.common.dynamicextensions.ui.util;

/**
 * This class defines miscellaneous methods that are commonly used by many Control objects. *
 * @author chetan_patil
 */
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import edu.common.dynamicextensions.domain.BooleanAttributeTypeInformation;
import edu.common.dynamicextensions.domain.CategoryAttribute;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.userinterface.ListBox;
import edu.common.dynamicextensions.domain.userinterface.SelectControl;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.BooleanValueInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.DataElementInterface;
import edu.common.dynamicextensions.domaininterface.DateTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.DateValueInterface;
import edu.common.dynamicextensions.domaininterface.DoubleValueInterface;
import edu.common.dynamicextensions.domaininterface.FloatValueInterface;
import edu.common.dynamicextensions.domaininterface.IntegerValueInterface;
import edu.common.dynamicextensions.domaininterface.LongValueInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.SkipLogicAttributeInterface;
import edu.common.dynamicextensions.domaininterface.StringValueInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ListBoxInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.webui.util.ControlInformationObject;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.validation.ValidatorUtil;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.CommonServiceLocator;

/**
 * The Class ControlsUtility.
 */
public class ControlsUtility
{


	/**
	 * This method returns the prescribed date format for the given
	 * DateAttributeTypeInformation.
	 *
	 * @param dateAttribute
	 *            the date attribute
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
		return DynamicExtensionsUtility.getDateFormat(dateFormat);
	}

	/**
	 * Sort name value list.
	 *
	 * @param nameValueList
	 *            the name value list
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
	 * This method populates the List of Values of the ListBox in the
	 * NameValueBean Collection.
	 * @param control the control
	 * @param sourceControlValue the source control value
	 * @param encounterDate date of encounter of the visit.
	 * @return List of pair of Name and its corresponding Value.
	 */
	public static List<NameValueBean> populateListOfValues(ControlInterface control,
			List<String> sourceControlValue, Date encounterDate)
	{
		List<NameValueBean> nameValueBeanList = new ArrayList<NameValueBean>();
		BaseAbstractAttributeInterface attribute = control.getBaseAbstractAttribute();
		if (attribute != null)
		{
			AttributeMetadataInterface attributeMetadataInterface = null;
			if (attribute instanceof AttributeMetadataInterface)
			{

				attributeMetadataInterface = (AttributeMetadataInterface) attribute;
				nameValueBeanList.addAll(getPermissibleValuesForAbstractMetadata(attribute,
						control, encounterDate));
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

					attributeMetadataInterface = (AttributeMetadataInterface) filteredAttributeCollection
							.iterator().next();
					/*if (control.getIsSkipLogicTargetControl())
					{
						control.getSourceSkipControl().setSkipLogicControls(sourceControlValue);
					}
					if (control.getIsSkipLogicLoadPermValues())
					{
						try
						{
							List<PermissibleValueInterface> permissibleValueList = getSkipLogicPermissibleValues(
									control.getSourceSkipControl(), control, sourceControlValue);

							nameValueBeanList = getPermissibleValues(permissibleValueList,
									attributeMetadataInterface);
						}
						catch (Exception exception)
						{
							throw new RuntimeException(exception);
						}
					}
					else
					{*/
					nameValueBeanList.addAll(getListOfPermissibleValues(attributeMetadataInterface,
							encounterDate));
					//}
				}
			}

			if (attributeMetadataInterface != null)
			{
				DataElementInterface dataElement = attributeMetadataInterface
						.getDataElement(encounterDate);
				if (dataElement instanceof UserDefinedDEInterface)
				{
					UserDefinedDEInterface userDefinedDEInterface = (UserDefinedDEInterface) dataElement;
					if (userDefinedDEInterface.getIsOrdered())
					{
						sortNameValueList(nameValueBeanList);
						if ("Descending".equalsIgnoreCase(userDefinedDEInterface.getOrder()))
						{
							Collections.reverse(nameValueBeanList);
						}
					}
				}
			}
		}
		return nameValueBeanList;
	}

	private static List<NameValueBean> getPermissibleValuesForAbstractMetadata(
			BaseAbstractAttributeInterface attribute, ControlInterface control, Date encounterDate)
	{
		List<NameValueBean> nameValueBeanList = new ArrayList<NameValueBean>();
		AttributeMetadataInterface attributeMetadataInterface = (AttributeMetadataInterface) attribute;
		if (!((SelectControl) control).getOptionList().isEmpty())
		{

			nameValueBeanList.addAll(((SelectControl) control).getOptionList());
		}
		else
		{
			nameValueBeanList.addAll(getListOfPermissibleValues(attributeMetadataInterface,
					encounterDate));
		}
		return nameValueBeanList;
	}

	/**
	 * Gets the skip logic attribute for attribute.
	 *
	 * @param selectedPermissibleValues
	 *            the selected permissible values
	 * @param srcAttrMetadataInterface
	 *            the src attr metadata interface
	 * @param tgtAttrMetadataInterface
	 *            the tgt attr metadata interface
	 * @return the skip logic attribute for attribute
	 */
	public static SkipLogicAttributeInterface getSkipLogicAttributeForAttribute(
			List<PermissibleValueInterface> selectedPermissibleValues,
			AttributeMetadataInterface srcAttrMetadataInterface,
			AttributeMetadataInterface tgtAttrMetadataInterface)
	{
		SkipLogicAttributeInterface skipLogicAttributeInterface = null;
		if (srcAttrMetadataInterface.getAttributeTypeInformation() instanceof BooleanAttributeTypeInformation)
		{
			CategoryAttributeInterface categoryAttributeInterface = null;
			if (srcAttrMetadataInterface instanceof CategoryAttributeInterface)
			{
				categoryAttributeInterface = (CategoryAttributeInterface) srcAttrMetadataInterface;
			}
			if (categoryAttributeInterface != null)
			{
				for (SkipLogicAttributeInterface skipLogicAttribute : categoryAttributeInterface
						.getDependentSkipLogicAttributes())
				{
					if (skipLogicAttribute.getSourceSkipLogicAttribute().equals(
							srcAttrMetadataInterface)
							&& skipLogicAttribute.getTargetSkipLogicAttribute().equals(
									tgtAttrMetadataInterface))
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
				Collection<PermissibleValueInterface> skipLogicPermissibleValues = srcAttrMetadataInterface
						.getSkipLogicPermissibleValues();
				if (skipLogicPermissibleValues != null)
				{
					for (PermissibleValueInterface skipLogicValue : skipLogicPermissibleValues)
					{
						for (SkipLogicAttributeInterface skipLogicAttribute : skipLogicValue
								.getDependentSkipLogicAttributes())
						{
							if (skipLogicValue.equals(selectedPermissibleValue)
									&& skipLogicAttribute.getSourceSkipLogicAttribute().equals(
											srcAttrMetadataInterface)
									&& skipLogicAttribute.getTargetSkipLogicAttribute().equals(
											tgtAttrMetadataInterface))
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
	 * Gets the skip logic attributes for check box.
	 *
	 * @param attributeMetadataInterface
	 *            the attribute metadata interface
	 * @return the skip logic attributes for check box
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
	 * Gets the skip logic permissible values.
	 *
	 * @param sourceControl
	 *            the source control
	 * @param targetControl
	 *            the target control
	 * @param values
	 *            the values
	 * @return the skip logic permissible values
	 * @throws ParseException
	 *             the parse exception
	 */
	public static List<PermissibleValueInterface> getSkipLogicPermissibleValues(
			ControlInterface sourceControl, ControlInterface targetControl, List<String> values)
			throws ParseException
	{
		List<PermissibleValueInterface> skipLogicPermissibleValueList = new ArrayList<PermissibleValueInterface>();
		List<PermissibleValueInterface> permissibleValueList = new ArrayList<PermissibleValueInterface>();
		if (values != null)
		{
			for (String controlValue : values)
			{
				AttributeMetadataInterface attributeMetadataInterface = ControlsUtility
						.getAttributeMetadataInterface(sourceControl.getBaseAbstractAttribute());
				if (attributeMetadataInterface != null)
				{
					PermissibleValueInterface selectedPermissibleValue = null;
					if (controlValue != null && controlValue.length() > 0)
					{
						selectedPermissibleValue = attributeMetadataInterface
								.getAttributeTypeInformation().getPermissibleValueForString(
										controlValue);
					}
					permissibleValueList.add(selectedPermissibleValue);
				}
			}
			if (sourceControl.getIsSkipLogic())
			{
				AttributeMetadataInterface tgtAttrMetadataInterface = ControlsUtility
						.getAttributeMetadataInterface(targetControl.getBaseAbstractAttribute());
				for (PermissibleValueInterface selectedPermissibleValue : permissibleValueList)
				{
					AttributeMetadataInterface attributeMetadataInterface = ControlsUtility
							.getAttributeMetadataInterface(sourceControl.getBaseAbstractAttribute());
					PermissibleValueInterface skipLogicPermissibleValue = attributeMetadataInterface
							.getSkipLogicPermissibleValue(selectedPermissibleValue);
					if (skipLogicPermissibleValue != null)
					{
						Collection<SkipLogicAttributeInterface> skipLogicAttributes = skipLogicPermissibleValue
								.getDependentSkipLogicAttributes();
						for (SkipLogicAttributeInterface skipLogicAttributeInterface : skipLogicAttributes)
						{
							if (skipLogicAttributeInterface.getTargetSkipLogicAttribute().equals(
									tgtAttrMetadataInterface))
							{
								DataElementInterface dataElementInterface = skipLogicAttributeInterface
										.getDataElement();
								if (dataElementInterface instanceof UserDefinedDEInterface)
								{
									UserDefinedDEInterface userDefinedDEInterface = (UserDefinedDEInterface) dataElementInterface;
									skipLogicPermissibleValueList.addAll(userDefinedDEInterface
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
	 * Gets the attribute metadata interface.
	 *
	 * @param attribute
	 *            the attribute
	 * @return the attribute metadata interface
	 */
	public static AttributeMetadataInterface getAttributeMetadataInterface(
			BaseAbstractAttributeInterface attribute)
	{
		AttributeMetadataInterface attributeMetadataInterface = null;
		if (attribute != null)
		{
			if (attribute instanceof AttributeMetadataInterface)
			{
				attributeMetadataInterface = (AttributeMetadataInterface) attribute;
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

					attributeMetadataInterface = (AttributeMetadataInterface) attributesList.get(0);
				}
			}
		}
		return attributeMetadataInterface;
	}

	/**
	 * Gets a list of permissible values for attribute or category attribute.
	 *
	 * @param attribute
	 *            the attribute
	 * @param encounterDate date of encounter of the visit.
	 * @return List of NameValueBean
	 */
	public static List<NameValueBean> getListOfPermissibleValues(
			AttributeMetadataInterface attribute, Date encounterDate)
	{
		List<PermissibleValueInterface> permissibleValueList = new ArrayList<PermissibleValueInterface>();
		DataElementInterface dataElement = attribute.getDataElement(encounterDate);
		if (dataElement instanceof UserDefinedDEInterface)
		{
			UserDefinedDEInterface userDefinedDEInterface = (UserDefinedDEInterface) dataElement;
			permissibleValueList.addAll(userDefinedDEInterface.getPermissibleValues());
		}

		return getPermissibleValues(permissibleValueList, attribute);
	}

	/**
	 * Gets the permissible values.
	 *
	 * @param permissibleValueList
	 *            the permissible value list
	 * @param attribute
	 *            the attribute
	 * @return the permissible values
	 */
	public static List<NameValueBean> getPermissibleValues(
			Collection<PermissibleValueInterface> permissibleValueList,
			AttributeMetadataInterface attribute)
	{
		List<NameValueBean> nameValueBeanList = new ArrayList<NameValueBean>();
		if (permissibleValueList != null)
		{
			NameValueBean nameValueBean;
			for (PermissibleValueInterface permissibleValue : permissibleValueList)
			{
                if (permissibleValue instanceof DateValueInterface
                        && attribute instanceof DateTypeInformationInterface)
                {
                    DateTypeInformationInterface dateAttribute = (DateTypeInformationInterface) attribute;
                    nameValueBean = getPermissibleDateValue(permissibleValue,
                            dateAttribute);
                }				else if (permissibleValue instanceof DoubleValueInterface)
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
				else if (permissibleValue instanceof BooleanValueInterface)
				{
					nameValueBean = getPermissibleBooleanValue(permissibleValue);
				}
				else
				{
					nameValueBean = getPermissibleStringValue(permissibleValue);
				}
				nameValueBeanList.add(nameValueBean);
			}
		}
		return nameValueBeanList;
	}

	/*	*//**
			* Gets the target entity display attribute list.
			*
			* @param displayAttributeMap
			*            the display attribute map
			* @param separator
			*            the separator
			* @return the target entity display attribute list
			* @throws DynamicExtensionsSystemException
			*             the dynamic extensions system exception
			* @throws DynamicExtensionsApplicationException
			*             the dynamic extensions application exception
			*/
	/*
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
			value.append(attributeValue).append(separator);
		}
		value.delete(value.lastIndexOf(separator), value.length());

		nameValueBean.setName(value.toString());
		displayAttributeList.add(nameValueBean);
	}

	return displayAttributeList;
	}
	*/
	/**
	 * Gets the permissible date value.
	 *
	 * @param permissibleValue
	 *            the permissible value
	 * @param dateAttribute
	 *            the date attribute
	 * @return the permissible date value
	 */
	private static NameValueBean getPermissibleDateValue(
			PermissibleValueInterface permissibleValue, DateTypeInformationInterface dateAttribute)
	{
		DateValueInterface dateValue = (DateValueInterface) permissibleValue;
		NameValueBean nameValueBean = null;

		if (dateValue != null && dateValue.getValue() != null)
		{
			Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
			nameValueBean = new NameValueBean();
			String date = new SimpleDateFormat(getDateFormat(dateAttribute), locale)
					.format(dateValue.getValue());
			nameValueBean.setName(date);
			nameValueBean.setValue(date);
		}
		return nameValueBean;
	}

	/**
	 * Gets the permissible double value.
	 *
	 * @param permissibleValue
	 *            the permissible value
	 * @return the permissible double value
	 */
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

	/**
	 * Gets the permissible float value.
	 *
	 * @param permissibleValue
	 *            the permissible value
	 * @return the permissible float value
	 */
	private static NameValueBean getPermissibleFloatValue(PermissibleValueInterface permissibleValue)
	{
		FloatValueInterface floatValue = (FloatValueInterface) permissibleValue;
		NameValueBean nameValueBean = null;

		if (floatValue != null && floatValue.getValue() != null)
		{
			nameValueBean = new NameValueBean();
			nameValueBean.setName(floatValue.getValue().floatValue());
			nameValueBean.setValue(floatValue.getValue().floatValue());
		}
		return nameValueBean;
	}

	/**
	 * Gets the permissible long value.
	 *
	 * @param permissibleValue
	 *            the permissible value
	 * @return the permissible long value
	 */
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

	/**
	 * Gets the permissible integer value.
	 *
	 * @param permissibleValue
	 *            the permissible value
	 * @return the permissible integer value
	 */
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

	/**
	 * Gets the permissible boolean value.
	 *
	 * @param permissibleValue
	 *            the permissible value
	 * @return the permissible boolean value
	 */
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

	/**
	 * Gets the permissible string value.
	 *
	 * @param permissibleValue
	 *            the permissible value
	 * @return the permissible string value
	 */
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
	 * Added by Preeti.
	 *
	 * @param controlsCollection
	 *            the controls collection
	 * @param controlsSequenceNumbers
	 *            the controls sequence numbers
	 */
	public static void reinitializeSequenceNumbers(Collection<ControlInterface> controlsCollection,
			String controlsSequenceNumbers)
	{
		if (controlsCollection != null && controlsSequenceNumbers != null)
		{
			ControlInterface control;
			Integer[] sequenceNumbers = DynamicExtensionsUtility.convertToIntegerArray(
					controlsSequenceNumbers, ProcessorConstants.CONTROLS_SEQ_NUMBER_SEPARATOR);
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
	 * Gets the child list.
	 *
	 * @param containerInterface
	 *            containerInterface
	 * @return List ChildList
	 * @throws DynamicExtensionsSystemException
	 *             DynamicExtensionsSystemException
	 */
	public static List getChildList(ContainerInterface containerInterface)
			throws DynamicExtensionsSystemException
	{
		List<ControlInformationObject> childList = new ArrayList<ControlInformationObject>();
		if (containerInterface != null)
		{
			Collection controlCollection = containerInterface.getControlCollection();
			ControlConfigurationsFactory controlConfigurationsFactory = ControlConfigurationsFactory
					.getInstance();
			if (controlCollection != null)
			{
				for (int counter = 1; counter <= controlCollection.size(); counter++)
				{
					ControlInterface controlInterface = DynamicExtensionsUtility
							.getControlBySequenceNumber(controlCollection, counter);
					if (controlInterface != null && controlInterface.getCaption() != null
							&& !controlInterface.getCaption().equals(""))
					{
						String controlCaption = controlInterface.getCaption();
						String controlDatatype;
						String controlSequenceNumber = controlInterface.getSequenceNumber()
								.toString();
						String controlName = DynamicExtensionsUtility
								.getControlName(controlInterface);
						if (controlName.equals(ProcessorConstants.ADD_SUBFORM_CONTROL))
						{
							controlDatatype = ProcessorConstants.ADD_SUBFORM_TYPE;
						}
						else
						{
							controlDatatype = getControlCaption(controlConfigurationsFactory
									.getControlDisplayLabel(controlName));
						}
						ControlInformationObject controlInformationObject = new ControlInformationObject(
								controlCaption, controlDatatype, controlSequenceNumber);
						childList.add(controlInformationObject);
					}
				}
			}
		}
		return childList;
	}

	/**
	 * Gets the control caption.
	 *
	 * @param captionKey
	 *            String captionKey
	 * @return String ControlCaption
	 */
	public static String getControlCaption(String captionKey)
	{
		String caption = null;
		if (captionKey != null)
		{
			ResourceBundle resourceBundle = ResourceBundle.getBundle("ApplicationResources");
			if (resourceBundle != null)
			{
				caption = resourceBundle.getString(captionKey);
			}
		}
		return caption;
	}

	/**
	 * Gets the attribute value for skip logic attributes from value map.
	 *
	 * @param fullValueMap
	 *            the full value map
	 * @param valueMap
	 *            the value map
	 * @param skipLogicAttribute
	 *            the skip logic attribute
	 * @param isSameContainerControl
	 *            the is same container control
	 * @param values
	 *            the values
	 * @param entryNumber
	 *            the entry number
	 * @param mapentryNumber
	 *            the mapentry number
	 * @return the attribute value for skip logic attributes from value map
	 */
	public static Object getAttributeValueForSkipLogicAttributesFromValueMap(
			Map<BaseAbstractAttributeInterface, Object> fullValueMap,
			Map<BaseAbstractAttributeInterface, Object> valueMap,
			BaseAbstractAttributeInterface skipLogicAttribute, boolean isSameContainerControl,
			List<Object> values, Integer entryNumber, Integer mapentryNumber)
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
				if (!isSameContainerControl && categoryAttribute.equals(skipLogicAttribute)
						|| isSameContainerControl && categoryAttribute.equals(skipLogicAttribute)
						&& entryNumber.equals(mapentryNumber))
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
					getAttributeValueForSkipLogicAttributesFromValueMap(fullValueMap, map,
							skipLogicAttribute, isSameContainerControl, values, entryNumber,
							++rowNumber);
				}
			}
		}
		return null;
	}

	/**
	 * Sets the value map for enumerated controls.
	 *
	 * @param rowId
	 *            the row id
	 * @param isSameContainerControl
	 *            the is same container control
	 * @param cardinality
	 *            the cardinality
	 * @param fullValueMap
	 *            the full value map
	 * @param entry
	 *            the entry
	 * @param control
	 *            the control
	 * @param isCopyPaste
	 *            the is copy paste
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 */
	private static void setValueMapForEnumeratedControls(Integer rowId,
			boolean isSameContainerControl, boolean cardinality,
			Map<BaseAbstractAttributeInterface, Object> fullValueMap,
			Map.Entry<BaseAbstractAttributeInterface, Object> entry, ControlInterface control,
			boolean isCopyPaste) throws DynamicExtensionsSystemException
	{
		Integer currentRowId = rowId;
		if (rowId == null && isSameContainerControl && cardinality)
		{
			currentRowId = Integer.valueOf(-1);
		}

		Object value = null;
		List<Object> values = new ArrayList<Object>();
		getAttributeValueForSkipLogicAttributesFromValueMap(fullValueMap, fullValueMap, control
				.getSourceSkipControl().getBaseAbstractAttribute(), false, values, Integer
				.valueOf(currentRowId), Integer.valueOf(currentRowId));

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
			if (control.getSourceSkipControl() != null)
			{
				sourceControlValues = control.getSourceSkipControl().getValueAsStrings();
			}
			try
			{
				AttributeMetadataInterface attribute = getAttributeMetadata(control);
				PermissibleValueInterface permissibleValueInterface = attribute
						.getAttributeTypeInformation().getPermissibleValueForString(
								entry.getValue().toString());
				List<PermissibleValueInterface> permissibleValueList = getSkipLogicPermissibleValues(
						control.getSourceSkipControl(), control, sourceControlValues);

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
				if (!isValuePresent)
				{
					entry.setValue(null);
				}
			}
			catch (ParseException e)
			{
				throw new DynamicExtensionsSystemException("ParseException", e);
			}
		}
		else
		{
			if (!isCopyPaste && !control.getIsEnumeratedControl() || isCopyPaste
					&& control.getIsSkipLogicReadOnly() && !control.getIsEnumeratedControl())
			{
				entry.setValue(null);
			}
		}
	}

	/**
	 * @param control
	 * @return
	 */
	private static AttributeMetadataInterface getAttributeMetadata(ControlInterface control)
	{
		AttributeMetadataInterface attribute;
		if (control instanceof ListBox)
		{
			if (((ListBoxInterface) control).getIsMultiSelect())
			{
				AssociationInterface association = (AssociationInterface) ((CategoryAttributeInterface) control
						.getBaseAbstractAttribute()).getAbstractAttribute();
				attribute = (AttributeMetadataInterface) EntityManagerUtil.filterSystemAttributes(
						association.getTargetEntity().getAbstractAttributeCollection()).iterator()
						.next();
			}
			else
			{
				attribute = (AttributeMetadataInterface) control.getBaseAbstractAttribute();
			}
		}
		else
		{
			attribute = (AttributeMetadataInterface) control.getBaseAbstractAttribute();
		}
		return attribute;
	}

	/**
	 * Populate attribute value map for skip logic attributes.
	 *
	 * @param fullValueMap
	 *            the full value map
	 * @param valueMap
	 *            the value map
	 * @param rowId
	 *            the row id
	 * @param cardinality
	 *            the cardinality
	 * @param controlName
	 *            the control name
	 * @param controlsList
	 *            the controls list
	 * @param isCopyPaste
	 *            the is copy paste
	 * @throws DynamicExtensionsApplicationException the dynamic extensions application exception
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	public static void populateAttributeValueMapForSkipLogicAttributes(
			Map<BaseAbstractAttributeInterface, Object> fullValueMap,
			Map<BaseAbstractAttributeInterface, Object> valueMap, Integer rowId,
			boolean cardinality, String controlName, List<ControlInterface> controlsList,
			boolean isCopyPaste) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException
	{
		for (Map.Entry<BaseAbstractAttributeInterface, Object> entry : valueMap.entrySet())
		{
			BaseAbstractAttributeInterface attribute = entry.getKey();
			if (attribute instanceof CategoryAttributeInterface)
			{
				CategoryAttributeInterface categoryAttributeInterface = (CategoryAttributeInterface) attribute;
				if (categoryAttributeInterface != null)
				{
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
						boolean isSameContainerControl = control.getSourceSkipControl()
								.getParentContainer().equals(control.getParentContainer());
						Integer controlSequenceNumber = control.getSequenceNumber();
						StringBuffer sourceControlName = new StringBuffer();
						if (controlSequenceNumber != null)
						{
							sourceControlName.append(control.getSourceSkipControl()
									.getHTMLComponentName());
							if (rowId != null && isSameContainerControl && cardinality
									&& !rowId.equals(Integer.valueOf(-1)))
							{
								sourceControlName.append('_');
								sourceControlName.append(rowId);
							}
						}
						if (found && control.getIsSkipLogicTargetControl()
								&& controlName.equals(sourceControlName.toString()))
						{
							setValueMapForEnumeratedControls(rowId, isSameContainerControl,
									cardinality, fullValueMap, entry, control, isCopyPaste);
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
					populateAttributeValueMapForSkipLogicAttributes(fullValueMap, map,
							++entryNumber, oneToManyCardinality, controlName, controlsList,
							isCopyPaste);
				}
			}
		}
	}

	/**
	 * Defines a RuleInterface for this control.
	 *
	 * @param ruleName
	 *            Rule name
	 * @param control
	 *            the control
	 */
	public static void defineRule(String ruleName, ControlInterface control)
	{
		CategoryAttribute categoryAttribute = (CategoryAttribute) control
				.getBaseAbstractAttribute();

		Collection<RuleInterface> rules = categoryAttribute.getRuleCollection();
		if (rules == null)
		{
			rules = new HashSet<RuleInterface>();
		}
		RuleInterface rule = DomainObjectFactory.getInstance().createRule();
		rule.setName(ruleName);
		rules.add(rule);
		categoryAttribute.setRuleCollection(rules);

		AttributeInterface attribute = categoryAttribute.getAttribute();
		attribute.addRule(rule);
	}

	/**
	 * Checks if the given RuleInterface is already defined for this Control.
	 *
	 * @param ruleName2
	 *            the rule name2
	 * @param control
	 *            the control
	 * @return true, if is rule defined
	 */
	public static boolean isRuleDefined(String ruleName2, ControlInterface control)
	{
		boolean result = false;

		Collection<RuleInterface> ruleCollection = ValidatorUtil.getRuleCollection(control
				.getBaseAbstractAttribute());
		for (RuleInterface rule : ruleCollection)
		{
			String ruleName = rule.getName();
			if (ruleName.equalsIgnoreCase(ruleName2))
			{
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * Method returns the date value of given string value.
	 * @param encounterDate date of encounter of visit.
	 * @return Date formated date.
	 */
	public static Date getFormattedDate(String encounterDate)
	{
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Date givenDate = null;
		try
		{
			if (encounterDate == null || "".equals(encounterDate))
			{
				givenDate = null;
			}
			else
			{
				givenDate = dateformat.parse(encounterDate.trim());
			}
		}
		catch (ParseException parseException)
		{
			// TODO Auto-generated catch block
			new BizLogicException(null, parseException,
					"Error while parsing encounter date to get PV version.");
		}
		return givenDate;
	}

	/**
	 * Convert date to String in given format.
	 * @param date Date which needs to convert in string.
	 * @param datePattern  Data pattern in which date need to be converted.
	 * @return  String converted string.
	 */
	public static String convertDateToString(Date date, String datePattern)
	{
		String encounterDateAsString = null;
		if (date != null)
		{
			DateFormat dateFormat;
			if (datePattern == null)
			{
				dateFormat = new SimpleDateFormat(ProcessorConstants.DATE_ONLY_FORMAT, Locale
						.getDefault());
				encounterDateAsString = dateFormat.format(date);
			}
			else
			{
				dateFormat = new SimpleDateFormat(datePattern, Locale.getDefault());
				encounterDateAsString = dateFormat.format(date);
			}
		}

		return encounterDateAsString;
	}

	/**
	 * Expected controlName format : Control_<container_id>_<in-context_container_id>_<sequence_no>_<y_position>_<row_id>
	 * Row id will be present only in case of Add more controls.
	 * Checks if is control present in add more.
	 * @param htmlControlName the html control name
	 * @return true, if is control present in add more
	 */
	public static boolean isControlPresentInAddMore(String htmlControlName)
	{
		return htmlControlName.split("_").length > 5 ;
	}
}
