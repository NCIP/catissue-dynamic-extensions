
package edu.common.dynamicextensions.validation;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domain.PermissibleValue;
import edu.common.dynamicextensions.domain.UserDefinedDE;
import edu.common.dynamicextensions.domain.userinterface.SelectControl;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AssociationMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.EnumeratedControlInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleParameterInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.exception.DynamicExtensionsValidationException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.interfaces.AbstractAttributeUIBeanInterface;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.util.ControlConfigurationsFactory;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.ApplicationProperties;

/**
 * @author Sujay Narkar
 * @author Rahul Ner
 * @author chetan_patil
 */
public class ValidatorUtil
{

	/**
	 * This method validates the entered values for entity.
	 * @param attributeValueMap  key - AttributeInterface
	 *                           value - value that user has entered for this attribute
	 * @return listOfError if any
	 * @throws DynamicExtensionsSystemException : Exception
	 */
	public static List<String> validateEntity(
			Map<BaseAbstractAttributeInterface, Object> attributeValueMap,
			List<String> listOfError, ContainerInterface container, boolean validateNLevel)
			throws DynamicExtensionsSystemException
	{
		if (listOfError == null)
		{
			listOfError = new ArrayList<String>();
		}
		Set<Map.Entry<BaseAbstractAttributeInterface, Object>> attributeSet = attributeValueMap
				.entrySet();
		if (attributeSet != null && !attributeSet.isEmpty())
		{
			for (Map.Entry<BaseAbstractAttributeInterface, Object> attributeValueNode : attributeSet)
			{
				BaseAbstractAttributeInterface abstractAttribute = attributeValueNode.getKey();

				if (abstractAttribute instanceof AttributeMetadataInterface)
				{
					ControlInterface control = DynamicExtensionsUtility
							.getControlForAbstractAttribute(
									(AttributeMetadataInterface) abstractAttribute, container);
					if (control != null)
					{
						listOfError.addAll(validateAttributes(abstractAttribute, attributeValueNode
								.getValue(), DynamicExtensionsUtility
								.replaceHTMLSpecialCharacters(control.getCaption())));
						checkForPermissibleValue(listOfError, control, abstractAttribute,
								attributeValueNode.getValue(), (Date) container
										.getContextParameter(Constants.ENCOUNTER_DATE));
					}
				}
				else if (abstractAttribute instanceof AssociationMetadataInterface)
				{
					AssociationMetadataInterface association = (AssociationMetadataInterface) abstractAttribute;

					validateAssociationData(attributeValueMap, listOfError, abstractAttribute,
							getContainerForAbstractAttribute(association), validateNLevel);
				}
			}
		}

		return listOfError;
	}

	/**
	 * Method performs the validation for the Association data.
	 * @param attributeValueMap  Value map for which validation will be performed.
	 * @param errorList List of errors.
	 * @param abstractAttribute Attribute interface for validation.
	 * @param container Container Interface for validation.
	 * @param validateNLevel whether need to perform for N level or restrict it to
	 * two level.
	 * @throws DynamicExtensionsSystemException Throws when there is system exception.
	 * dynamic extension process.
	 */
	private static void validateAssociationData(
			Map<BaseAbstractAttributeInterface, Object> attributeValueMap, List<String> errorList,
			BaseAbstractAttributeInterface abstractAttribute, ContainerInterface container,
			boolean validateNLevel) throws DynamicExtensionsSystemException
	{
		AssociationMetadataInterface association = (AssociationMetadataInterface) abstractAttribute;

		if (AssociationType.CONTAINTMENT.equals(association.getAssociationType()))
		{
			List<Map<BaseAbstractAttributeInterface, Object>> valueObject = (List<Map<BaseAbstractAttributeInterface, Object>>) attributeValueMap
					.get(abstractAttribute);
			for (Map<BaseAbstractAttributeInterface, Object> subAttributeValueMap : valueObject)
			{
				if (validateNLevel)
				{
					validateEntity(subAttributeValueMap, errorList, container, validateNLevel);
				}
				else
				{
					errorList.addAll(validateEntityAttributes(subAttributeValueMap, container));
				}
			}

		}
	}

	/**
	 * Validate whether values is permissible or not.
	 * @param errorList List of errors.
	 * @param control Control for which validation need to performed.
	 * @param abstractAttribute Attribute for validation.
	 * @param attributeValue Value provided for insertion.
	 * @param encounterDate Date value.
	 * @throws DynamicExtensionsSystemException Throws when there is system exception.
	 */
	public static void checkForPermissibleValue(List<String> errorList, ControlInterface control,
			BaseAbstractAttributeInterface abstractAttribute, Object attributeValue,
			Date encounterDate) throws DynamicExtensionsSystemException
	{
		if (control instanceof EnumeratedControlInterface && attributeValue != null)
		{
			boolean isValuePresent = true;
			if (control instanceof SelectControl)
			{
				SelectControl selectControl = (SelectControl) control;
				List<NameValueBean> optionList = selectControl.getOptionList();
				if (optionList != null && !optionList.isEmpty())
				{
					for (NameValueBean nameValueBean : optionList)
					{
						if (nameValueBean.getValue().equals(attributeValue.toString()))
						{
							isValuePresent = true;
							break;
						}
						else
						{
							isValuePresent = false;
						}
					}
				}
				else
				{
					isValuePresent = validatePermissibleValues(abstractAttribute, attributeValue,
							encounterDate);
				}
			}
			else
			{
				isValuePresent = validatePermissibleValues(abstractAttribute, attributeValue,
						encounterDate);
			}
			if (!isValuePresent)
			{
				errorList.add("Please Enter valid permissible value for attribute "
						+ DynamicExtensionsUtility.replaceHTMLSpecialCharacters(control
								.getCaption()));
			}
		}
	}

	/**
	 * Validate p vs.
	 *
	 * @param abstractAttribute the abstract attribute
	 * @param attributeValue the attribute value
	 * @param encounterDate the encounter date
	 *
	 * @return true, if validate p vs
	 *
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	private static boolean validatePermissibleValues(
			BaseAbstractAttributeInterface abstractAttribute, Object attributeValue,
			Date encounterDate) throws DynamicExtensionsSystemException
	{
		boolean isValuePresent = true;
		AttributeMetadataInterface attributeMetadataInterface = (AttributeMetadataInterface) abstractAttribute;
		if (attributeMetadataInterface.getDataElement(encounterDate) != null)
		{

			try
			{
				isValuePresent = isValidPermissibleValue(attributeValue,
						attributeMetadataInterface, encounterDate);
			}
			catch (ParseException e)
			{
				throw new DynamicExtensionsSystemException(
						"Exception while validating permissible value.", e);
			}
		}
		return isValuePresent;
	}

	/**
	 * This method will check whether the entered value is contained in the defined
	 * permissible value list of that attribute.
	 * @param attributeValue value given for insertion
	 * @param attributeMetadataInterface attribute
	 * @param encounterDate Date value.
	 * @return true if value is valid else false.
	 * @throws ParseException thrown by getAttributeValueList.
	 */
	private static boolean isValidPermissibleValue(Object attributeValue,
			AttributeMetadataInterface attributeMetadataInterface, Date encounterDate)
			throws ParseException
	{
		boolean isValuePresent = false;
		if (attributeValue == null || "".equals(attributeValue.toString()))
		{
			isValuePresent = true;
		}
		else if (attributeValue instanceof ArrayList)
		{//multiselect case
			CategoryAttributeInterface attribute = (CategoryAttributeInterface) attributeMetadataInterface;
			AttributeInterface attributeInterface = getMultiselectAtrribute((AssociationInterface) attribute
					.getAbstractAttribute());
			AttributeTypeInformationInterface attributeTypeInformation = attributeInterface
					.getAttributeTypeInformation();
			List<PermissibleValueInterface> enteredAttributeValueList = getAttributeValueList(
					attributeValue, attributeTypeInformation);
			isValuePresent = validatePVForMulstiSelect(enteredAttributeValueList,
					attributeMetadataInterface, encounterDate);
		}
		else
		{
			Collection<PermissibleValueInterface> permissibleValue = ((UserDefinedDE) attributeMetadataInterface
					.getDataElement(encounterDate)).getPermissibleValueCollection();
			AttributeTypeInformationInterface attributeTypeInformation = attributeMetadataInterface
					.getAttributeTypeInformation();
			PermissibleValue permissibleValueInterface = (PermissibleValue) attributeTypeInformation
					.getPermissibleValueForString(attributeValue.toString());
			if (permissibleValue.contains(permissibleValueInterface))
			{
				isValuePresent = true;
			}
		}

		return isValuePresent;
	}

	/**
	 * Get multi-select attribute from the given association.
	 * @param association Association for which multi select attributes
	 * need to be retrieve.
	 * @return AttributeInterface Multi-select attribute.
	 */
	public static AttributeInterface getMultiselectAtrribute(AssociationInterface association)
	{
		AttributeInterface multiselectAttribute = null;

		for (AttributeInterface attributeInterface : association.getTargetEntity()
				.getAttributeCollection())
		{
			if (attributeInterface.getName().contains(DEConstants.COLLECTIONATTRIBUTE))
			{
				multiselectAttribute = attributeInterface;
				break;
			}
		}
		return multiselectAttribute;
	}

	/**
	 * Validate whether entered values are permissible or not for multi-select.
	 * @param enteredAttributeValueList List of entered values.
	 * @param attributeMetadataInterface Attribute metadata interface to retrieve
	 * permissible value.
	 * @return true if all value entered are permissible or return false.
	 */
	private static boolean validatePVForMulstiSelect(
			List<PermissibleValueInterface> enteredAttributeValueList,
			AttributeMetadataInterface attributeMetadataInterface, Date encounterDate)
	{
		boolean isAllValuePresent = false;
		if (((UserDefinedDE) attributeMetadataInterface.getDataElement(encounterDate))
				.getPermissibleValueCollection().containsAll(enteredAttributeValueList)
				|| enteredAttributeValueList.isEmpty())
		{
			isAllValuePresent = true;
		}

		return isAllValuePresent;
	}

	/**
	 * Retrieve list of permissible value as list of PermissibleValueInterface entered by user.
	 * @param attributeValue values entered by user.
	 * @param attributeTypeInformation  convert object value to string.
	 * @throws ParseException throws by attributeTypeInformation.
	 * @return List of PermissibleValueInterface.
	 */
	private static List<PermissibleValueInterface> getAttributeValueList(Object attributeValue,
			AttributeTypeInformationInterface attributeTypeInformation) throws ParseException
	{
		ArrayList<Map<BaseAbstractAttributeInterface, Object>> attributeValueList = (ArrayList<Map<BaseAbstractAttributeInterface, Object>>) attributeValue;
		List<PermissibleValueInterface> updatedAttributeValueList = new ArrayList<PermissibleValueInterface>();
		for (Map<BaseAbstractAttributeInterface, Object> attributeValueMap : attributeValueList)
		{
			if (!attributeValueMap.values().isEmpty())
			{
				updatedAttributeValueList.add(attributeTypeInformation
						.getPermissibleValueForString(DynamicExtensionsUtility
								.getEscapedStringValue(attributeValueMap.values().toArray()[0]
										.toString())));
			}
		}
		return updatedAttributeValueList;
	}

	/**
	 * Retrieve Container from given getContainerForAbstractAttribute.
	 * @param abstractAttribute AssociationMetadataInterface from which
	 * ContainerInterface will be retrieved.
	 * @return Retrieved ContainerInterface.
	 */
	public static ContainerInterface getContainerForAbstractAttribute(
			AssociationMetadataInterface associationMetadataInterface)
	{
		ContainerInterface containerInterface = null;
		Collection<ContainerInterface> containerCollection = null;
		if (associationMetadataInterface instanceof AssociationInterface)
		{
			AssociationInterface associationInterface = (AssociationInterface) associationMetadataInterface;
			containerCollection = associationInterface.getTargetEntity().getContainerCollection();

		}
		else if (associationMetadataInterface instanceof CategoryAssociationInterface)
		{
			CategoryAssociationInterface categoryAssociationInterface = (CategoryAssociationInterface) associationMetadataInterface;
			containerCollection = categoryAssociationInterface.getTargetCategoryEntity()
					.getContainerCollection();
		}
		List<ContainerInterface> containerList = containerCollection == null
				? new ArrayList<ContainerInterface>()
				: new ArrayList<ContainerInterface>(containerCollection);
		if (!containerList.isEmpty())
		{
			containerInterface = containerList.get(0);
		}
		return containerInterface;

	}

	/**
	 * This method validates the attributes of entity.
	 * @param attributeValueMap Attribute to value map.
	 * @param containerInterface ContainerInterface for validation.
	 * @param abstractContainmentControlInterface
	 * @return errorList List of errors while validating.
	 * @throws DynamicExtensionsSystemException
	 */
	private static List<String> validateEntityAttributes(
			Map<BaseAbstractAttributeInterface, Object> attributeValueMap,
			ContainerInterface containerInterface) throws DynamicExtensionsSystemException
	{
		List<String> errorList = new ArrayList<String>();

		Set<Map.Entry<BaseAbstractAttributeInterface, Object>> attributeSet = attributeValueMap
				.entrySet();
		if (attributeSet != null && !attributeSet.isEmpty())
		{
			for (Map.Entry<BaseAbstractAttributeInterface, Object> attributeValueNode : attributeSet)
			{
				validateAttributeMetaData(containerInterface, errorList, attributeValueNode);
			}
		}

		return errorList;
	}

	/**
	 * This method validates AttributeMetadataInterface attributes.
	 * @param containerInterface ContainerInterface to retrieve the control.
	 * @param errorList List of all errors.
	 * @param attributeValueNode Values entered for the attribute.
	 * @throws DynamicExtensionsSystemException
	 */
	private static void validateAttributeMetaData(ContainerInterface containerInterface,
			List<String> errorList,
			Map.Entry<BaseAbstractAttributeInterface, Object> attributeValueNode)
			throws DynamicExtensionsSystemException
	{
		BaseAbstractAttributeInterface abstractAttribute = attributeValueNode.getKey();
		if (abstractAttribute instanceof AttributeMetadataInterface)
		{
			ControlInterface control = DynamicExtensionsUtility.getControlForAbstractAttribute(
					(AttributeMetadataInterface) abstractAttribute, containerInterface);
			if (control != null && control.getBaseAbstractAttribute() != null)
			{
				errorList.addAll(validateAttributes(abstractAttribute, attributeValueNode
						.getValue(), DynamicExtensionsUtility.replaceHTMLSpecialCharacters(control
						.getCaption())));
			}
		}
	}

	/**
	 * Validate attribute for given attribute.
	 * @param attributeValueNode Value for attribute.
	 * @param controlCaption Caption of control.
	 * @return errorList List of errors.
	 * @throws DynamicExtensionsSystemException
	 */
	public static List<String> validateAttributes(BaseAbstractAttributeInterface abstractAttribute,
			Object val, String controlCaption) throws DynamicExtensionsSystemException
	{
		List<String> errorList = new ArrayList<String>();

		//Bug: 9778 : modified to get explicit and implicit rules also in case of CategoryAttribute.
		//Reviewer: Rajesh Patil
		Collection<RuleInterface> attributeRuleCollection = getRuleCollection(abstractAttribute);
		if (attributeRuleCollection != null && !attributeRuleCollection.isEmpty())
		{

			for (RuleInterface rule : attributeRuleCollection)
			{
				AttributeMetadataInterface attribute = (AttributeMetadataInterface) abstractAttribute;
				String ruleName = rule.getName();
				if ("unique".equals(ruleName))
				{

					Long recordId = attribute.getId();
					checkForUniqueValue(val, controlCaption, errorList, attribute, recordId);
				}
				else
				{
					checkNonUniqueValueValidation(val, controlCaption, errorList, attribute, rule);
				}
			}
		}

		return errorList;
	}

	/**
	 * Validate value for non unique rules.
	 * @param attributeValueNode value entered for the attribute.
	 * @param controlCaption Caption of the control.
	 * @param errorList List of error.
	 * @param attribute Attribute for which validation to perform.
	 * @param rule Rules which needs to be validate.
	 * @throws DynamicExtensionsSystemException
	 */
	private static void checkNonUniqueValueValidation(Object valueObject, String controlCaption,
			List<String> errorList, AttributeMetadataInterface attribute, RuleInterface rule)
			throws DynamicExtensionsSystemException
	{
		String errorMessage;
		if (valueObject instanceof List)
		{
			valueObject = ((List) valueObject).get(0);
		}
		Map<String, String> parameterMap = getParamMap(rule);
		try
		{
			checkValidation(attribute, valueObject, rule, parameterMap, controlCaption);
		}
		catch (DynamicExtensionsValidationException e)
		{
			errorMessage = ApplicationProperties.getValue(e.getErrorCode(), e.getPlaceHolderList());
			if (!errorList.contains(errorMessage))
			{
				errorList.add(errorMessage);
			}
		}
	}

	/**
	 * Will check for Unique value validation for the attribute.
	 * @param attributeValueNode value entered for the attribute.
	 * @param controlCaption Caption of the control.
	 * @param errorList List of error.
	 * @param attribute Attribute for which validation to perform.
	 * @param recordId Identifier of the record.
	 * @throws DynamicExtensionsSystemException
	 */
	private static void checkForUniqueValue(Object valueObject, String controlCaption,
			List<String> errorList, AttributeMetadataInterface attribute, Long recordId)
			throws DynamicExtensionsSystemException
	{
		String errorMessage;
		if (valueObject instanceof List)
		{
			valueObject = ((List) valueObject).get(0);
		}
		try
		{
			checkUniqueValidationForAttribute(attribute, valueObject, recordId, controlCaption);
		}
		catch (DynamicExtensionsValidationException e)
		{
			errorMessage = ApplicationProperties.getValue(e.getErrorCode(), e.getPlaceHolderList());
			errorList.add(errorMessage);
		}
	}

	/**
	 * Retrieve rules for the given attribute.
	 * @param abstractAttribute Attribute for which rules to be retrieve.
	 * @return RuleInterface
	 */
	public static Collection<RuleInterface> getRuleCollection(
			BaseAbstractAttributeInterface abstractAttribute)
	{
		AttributeMetadataInterface attribute = (AttributeMetadataInterface) abstractAttribute;
		Collection<RuleInterface> attributeRuleCollection = attribute.getRuleCollection();
		if (attribute instanceof CategoryAttributeInterface)
		{
			Collection<RuleInterface> implicitRuleCollection = ((CategoryAttributeInterface) attribute)
					.getAbstractAttribute().getRuleCollection();
			attributeRuleCollection.addAll(implicitRuleCollection);
			removeConflictingRules(attributeRuleCollection);
		}
		return attributeRuleCollection;
	}

	/**
	 * Check for unique value rules for attribute.
	 * @param attribute Attribute for which rule to be apply.
	 * @param valueObject value to be verify with.
	 * @param recordId Identifier of record.
	 * @param controlCaption Caption of control.
	 * @throws DynamicExtensionsValidationException
	 * @throws DynamicExtensionsSystemException
	 */
	public static void checkUniqueValidationForAttribute(AttributeMetadataInterface attribute,
			Object valueObject, Long recordId, String controlCaption)
			throws DynamicExtensionsValidationException, DynamicExtensionsSystemException
	{
		Collection<RuleInterface> attributeRuleCollection = attribute.getRuleCollection();

		if (attributeRuleCollection != null && !attributeRuleCollection.isEmpty())
		{
			for (RuleInterface rule : attributeRuleCollection)
			{
				String ruleName = rule.getName();
				if ("unique".equals(ruleName))
				{
					Map<String, String> parameterMap = new HashMap<String, String>();
					if (recordId != null)
					{
						parameterMap.put("recordId", recordId.toString());
					}
					checkValidation(attribute, valueObject, rule, parameterMap, controlCaption);
					break;
				}
			}
		}
	}

	/**
	 * Validate the given attribute with the given value with the given rule.
	 * @param attribute Attribute to be verify.
	 * @param valueObject Value of the object.
	 * @param rule Rule for which validation should be done.
	 * @param parameterMap
	 * @param controlCaption Caption of control.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsValidationException
	 */
	private static void checkValidation(AttributeMetadataInterface attribute, Object valueObject,
			RuleInterface rule, Map<String, String> parameterMap, String controlCaption)
			throws DynamicExtensionsSystemException, DynamicExtensionsValidationException
	{
		String ruleName = rule.getName();
		ValidatorRuleInterface validatorRule = ControlConfigurationsFactory.getInstance()
				.getValidatorRule(ruleName);
		validatorRule.validate(attribute, valueObject, parameterMap, controlCaption);
	}

	/**
	 * This method returns the Map of parameters of the Rule.
	 * @param rule the Rule instance whose Map of parameter are to be fetched.
	 * @return the Map of parameters of the Rule.
	 * 					key - name of parameter
	 * 					value - value of parameter
	 */
	private static Map<String, String> getParamMap(RuleInterface rule)
	{
		Map<String, String> parameterMap = new HashMap<String, String>();
		Collection<RuleParameterInterface> ruleParamCollection = rule.getRuleParameterCollection();
		if (ruleParamCollection != null && !ruleParamCollection.isEmpty())
		{
			for (RuleParameterInterface ruleParameter : ruleParamCollection)
			{
				parameterMap.put(ruleParameter.getName(), ruleParameter.getValue());
			}
		}
		return parameterMap;
	}

	/**
	 * Report invalid user inputs to the user.
	 * @param placeHolderOne
	 * @param placeHolderTwo
	 * @param errorKey
	 * @throws DynamicExtensionsValidationException
	 */
	public static void reportInvalidInput(String placeHolderOne, String placeHolderTwo,
			String errorKey) throws DynamicExtensionsValidationException
	{
		List<String> placeHolders = new ArrayList<String>();
		placeHolders.add(placeHolderOne);
		placeHolders.add(placeHolderTwo);
		throw new DynamicExtensionsValidationException("Validation failed", null, errorKey,
				placeHolders);
	}

	/**
	 * This method check for conflicting rule
	 * @param allValidationRules rule collection
	 * @param attributeName attribute name
	 * @throws DynamicExtensionsSystemException if conflicting rule are present in rule collection
	 */
	public static void checkForConflictingRules(Collection<String> allValidationRules,
			String attributeName) throws DynamicExtensionsSystemException
	{
		if (allValidationRules.contains(ProcessorConstants.ALLOW_FUTURE_DATE)
				&& (allValidationRules
						.contains(ProcessorConstants.ALLOW_PAST_AND_PRESENT_DATE_ONLY)))
		{
			throw new DynamicExtensionsSystemException(
					"Conflicting rules present. Allow Past and Present Date and Allow Future Date rules cannot be applied for attribute:, "
							+ attributeName);
		}

		if ((allValidationRules.contains(ProcessorConstants.DATE_RANGE) || allValidationRules
				.contains(ProcessorConstants.RANGE))
				&& allValidationRules.contains(ProcessorConstants.ALLOW_FUTURE_DATE))
		{
			throw new DynamicExtensionsSystemException(
					"Conflicting rules present. Date range and Allow Future Date rules cannot be applied for attribute:, "
							+ attributeName);
		}

		if ((allValidationRules.contains(ProcessorConstants.DATE_RANGE) || allValidationRules
				.contains(ProcessorConstants.RANGE))
				&& allValidationRules.contains(ProcessorConstants.ALLOW_PAST_AND_PRESENT_DATE_ONLY))
		{
			throw new DynamicExtensionsSystemException(
					"Conflicting rules present. Date range and Allow Past and Present Date rules cannot be applied for attribute:, "
							+ attributeName);
		}

	}

	/**
	 * This method removes conflicting rules from the collection
	 * @param rules rule collection
	 */
	public static void removeConflictingRules(Collection<RuleInterface> rules)
	{
		if (isConflictingRulePresent(rules))
		{
			for (RuleInterface rule : rules)
			{
				if (DEConstants.DATE.equalsIgnoreCase(rule.getName()) && rule.getIsImplicitRule())
				{
					rules.remove(rule);
					break;
				}
			}
		}
	}

	/**
	 * This method check for presence of conflicting rule
	 * @param rules rule collection
	 * @return true or false depending on presence of conflicting rule
	 */
	private static boolean isConflictingRulePresent(Collection<RuleInterface> rules)
	{
		boolean isConflictingRulePresent = false;
		for (RuleInterface attributeRule : rules)
		{
			if (!attributeRule.getIsImplicitRule()
					&& (DEConstants.ALLOW_FUTURE_DATE.equalsIgnoreCase(attributeRule.getName())
							|| DEConstants.RANGE.equalsIgnoreCase(attributeRule.getName()) || DEConstants.DATE_RANGE
							.equalsIgnoreCase(attributeRule.getName())))
			{
				isConflictingRulePresent = true;
			}
		}
		return isConflictingRulePresent;
	}

	/**
	 * This method vaerify that the Max & Min values in the given attributeUIBeanInformationIntf are according to
	 * the Given DE Format or Not.
	 * @param validationRule name of the Rule.
	 * @param attributeUIBeanInformationIntf ui bean.
	 * @param attributeName Name of the Attribute
	 * @throws DynamicExtensionsSystemException Exception.
	 */
	public static void checkForValidDateRangeRule(String validationRule,
			AbstractAttributeUIBeanInterface attributeUIBeanInformationIntf, String attributeName)
			throws DynamicExtensionsSystemException
	{
		if (validationRule.equals(ProcessorConstants.DATE_RANGE))
		{
			try
			{
				Utility.parseDate(attributeUIBeanInformationIntf.getMax(),
						ProcessorConstants.SQL_DATE_ONLY_FORMAT);
				Utility.parseDate(attributeUIBeanInformationIntf.getMin(),
						ProcessorConstants.SQL_DATE_ONLY_FORMAT);
			}
			catch (ParseException exception)
			{
				throw new DynamicExtensionsSystemException("Date Range Specified For Attribute "
						+ attributeName + " is not in proper format", exception);

			}
		}

	}
}