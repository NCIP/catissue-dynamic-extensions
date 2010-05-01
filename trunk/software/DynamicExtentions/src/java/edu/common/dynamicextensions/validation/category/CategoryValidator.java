
package edu.common.dynamicextensions.validation.category;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.NumericAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ComboBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ListBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextFieldInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleParameterInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.exception.DynamicExtensionsValidationException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.CategoryHelperInterface.ControlEnum;
import edu.common.dynamicextensions.util.global.CategoryConstants;
import edu.common.dynamicextensions.util.parser.CategoryCSVConstants;
import edu.common.dynamicextensions.util.parser.CategoryCSVFileParser;
import edu.common.dynamicextensions.validation.DateRangeValidator;
import edu.common.dynamicextensions.validation.FutureDateValidator;
import edu.common.dynamicextensions.validation.RangeValidator;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.dao.exception.DAOException;

/**
 * @author kunal_kamble
 *
 */
public class CategoryValidator
{

	Long entityGroupId;

	CategoryCSVFileParser categoryFileParser;

	StringBuffer errorMessage = new StringBuffer();

	/**
	 * @param entityGroup
	 */
	public CategoryValidator(CategoryCSVFileParser categoryFileParser)
	{
		this.categoryFileParser = categoryFileParser;
		ApplicationProperties.initBundle(CategoryCSVConstants.DYEXTN_ERROR_MESSAGES_FILE);
	}

	/**
	 * @return
	 */
	public Long getEntityGroupId()
	{
		return entityGroupId;
	}

	/**
	 * @param entityGroup
	 */
	public void setEntityGroupId(Long entityGroupId)
	{
		this.entityGroupId = entityGroupId;
	}

	/**
	 * @throws DynamicExtensionsSystemException
	 */
	public void validateMultiplicity() throws DynamicExtensionsSystemException
	{
		if (categoryFileParser.readLine()[0].split(":").length < 2)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ getErrorMessageStart()
					+ ApplicationProperties.getValue(CategoryConstants.MULT_UNDEFINED));
		}
	}

	/**
	 * @param entityName
	 * @throws DynamicExtensionsSystemException
	 * @throws ClassNotFoundException 
	 * @throws DAOException 
	 */
	public void validateEntityName(String entityName) throws DynamicExtensionsSystemException,
			DAOException, ClassNotFoundException
	{
		String errorMessage = getErrorMessageStart()
				+ ApplicationProperties.getValue(CategoryConstants.NO_ENTITY) + entityName;

		String entityHQL = "select id from Entity entity where entity.entityGroup.id = "
				+ entityGroupId + " and entity.name = '" + entityName + "'";
		List entityIdList = DynamicExtensionsUtility.executeQuery(entityHQL);
		if (entityIdList.isEmpty())
		{
			throw new DynamicExtensionsSystemException(errorMessage);
		}
		checkForNullRefernce(entityIdList.get(0), errorMessage);
	}

	/**
	 * 
	 */
	public void validateSubcategoryTag()
	{
		categoryFileParser.getTargetContainerCaption();
	}

	/**
	 * @param object
	 * @param message
	 * @throws DynamicExtensionsSystemException
	 */
	public static void checkForNullRefernce(Object object, String message)
			throws DynamicExtensionsSystemException
	{
		if (object == null)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ message);
		}
	}

	/**
	 * This method checks whether the range specified for category attribute is
	 * a valid subset of range specified for its attribute.
	 * @param attribute
	 * @param rules
	 * @throws DynamicExtensionsSystemException
	 * @throws ParseException
	 * @throws DynamicExtensionsValidationException 
	 */
	public static void checkRangeAgainstAttributeValueRange(AttributeInterface attribute,
			Map<String, Object> rules) throws DynamicExtensionsSystemException, ParseException
	{
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
		if (attribute == null)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.NULL_ATTR));
		}

		if (!rules.isEmpty())
		{
			Map<String, Object> catMinMaxValues = null;

			if (rules.containsKey(CategoryCSVConstants.DATE_RANGE))
			{
				catMinMaxValues = (Map<String, Object>) rules.get(CategoryCSVConstants.DATE_RANGE);
				if (attribute == null)
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties.getValue(CategoryConstants.NULL_ATTR));
				}
			}
			else if (rules.containsKey(CategoryCSVConstants.RANGE.toLowerCase(locale)))
			{
				catMinMaxValues = (Map<String, Object>) rules.get(CategoryCSVConstants.RANGE
						.toLowerCase(locale));
			}

			if (catMinMaxValues != null && !catMinMaxValues.isEmpty())
			{
				validateAttributeTypeInformationMetadata(attribute);
				validateRangeValues(attribute, catMinMaxValues);
			}
		}
	}

	/**
	 * This method checks whether the range specified for category attribute is
	 * a valid subset of range specified for its attribute.
	 * @param attribute
	 * @param catMinMaxValues
	 * @throws DynamicExtensionsSystemException
	 */
	private static void validateRangeValues(AttributeInterface attribute,
			Map<String, Object> catMinMaxValues) throws DynamicExtensionsSystemException
	{
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
		String minValue = (String) catMinMaxValues
				.get(CategoryCSVConstants.MIN.toLowerCase(locale));
		String maxValue = (String) catMinMaxValues
				.get(CategoryCSVConstants.MAX.toLowerCase(locale));

		Map<String, String> values = new HashMap<String, String>();

		Set<RuleInterface> attributeRules = new HashSet<RuleInterface>(attribute
				.getRuleCollection());

		for (RuleInterface rule : attributeRules)
		{
			if (rule.getName().equalsIgnoreCase(CategoryCSVConstants.RANGE)
					|| rule.getName().equalsIgnoreCase(ProcessorConstants.DATE_RANGE))
			{
				Set<RuleParameterInterface> ruleParameters = new HashSet<RuleParameterInterface>(
						rule.getRuleParameterCollection());

				for (RuleParameterInterface ruleParameter : ruleParameters)
				{
					if (ruleParameter.getName().equalsIgnoreCase(CategoryCSVConstants.MIN))
					{
						String min = ruleParameter.getValue();
						values.put(CategoryCSVConstants.MIN.toLowerCase(locale), min);
						validateRange(minValue, attribute, values);
						values.clear();
					}
					if (ruleParameter.getName().equalsIgnoreCase(CategoryCSVConstants.MAX))
					{
						String max = ruleParameter.getValue();
						values.put(CategoryCSVConstants.MAX.toLowerCase(locale), max);
						validateRange(maxValue, attribute, values);
						values.clear();
					}
				}
			}
		}

		validateCategoryAttributeRangeValues(minValue, maxValue, attribute);
	}

	/**
	 * This method performs some basic validations like checking if the attribute or
	 * the attribute type information is null. It also checks whether range is specified 
	 * for valid attribute type information.
	 * @param attribute
	 * @throws DynamicExtensionsSystemException
	 */
	private static void validateAttributeTypeInformationMetadata(AttributeInterface attribute)
			throws DynamicExtensionsSystemException
	{
		AttributeTypeInformationInterface attrTypeInfo = attribute.getAttributeTypeInformation();

		if (attrTypeInfo == null)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.NULL_ATTR_TYPE_INFO)
					+ attribute.getName());
		}

		if (!(attrTypeInfo instanceof NumericAttributeTypeInformation || attrTypeInfo instanceof DateAttributeTypeInformation))
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.NON_NUM_RANGE)
					+ attribute.getName());
		}
	}

	/**
	 * This method checks whether the range specified for category attribute 
	 * is a valid subset of range specified for its attribute.
	 * @param catAttrValue
	 * @param attribute
	 * @param values
	 * @throws DynamicExtensionsSystemException
	 */
	private static void validateRange(String catAttrValue, AttributeInterface attribute,
			Map<String, String> values) throws DynamicExtensionsSystemException
	{
		AttributeTypeInformationInterface attrTypeInfo = attribute.getAttributeTypeInformation();

		if (attrTypeInfo instanceof NumericAttributeTypeInformation)
		{
			RangeValidator rangeValidator = new RangeValidator();

			try
			{
				rangeValidator.validate((AttributeMetadataInterface) attribute, catAttrValue,
						values, attribute.getName());
			}
			catch (DynamicExtensionsValidationException e)
			{
				throw new DynamicExtensionsSystemException(ApplicationProperties
						.getValue(CategoryConstants.CREATE_CAT_FAILS)
						+ ApplicationProperties.getValue(e.getErrorCode()) + attribute.getName(), e);
			}
		}
		else
		{
			DateRangeValidator dateRangeValidator = new DateRangeValidator();

			try
			{
				dateRangeValidator.validate((AttributeMetadataInterface) attribute, catAttrValue,
						values, attribute.getName());
			}
			catch (DynamicExtensionsValidationException e)
			{
				throw new DynamicExtensionsSystemException(ApplicationProperties
						.getValue(CategoryConstants.CREATE_CAT_FAILS)
						+ ApplicationProperties.getValue(e.getErrorCode()) + attribute.getName(), e);
			}
		}
	}

	/**
	 * This method check for allowing future date rule.
	 * @param attribute attribute to be validated for allowing future date
	 * @param rules rule collection for that attribute
	 * @throws DynamicExtensionsSystemException  fails to create category
	 * @throws ParseException
	 */
	public static void checkIfFutureDateRuleSpecified(AttributeInterface attribute,
			Map<String, Object> rules) throws DynamicExtensionsSystemException, ParseException
	{
		if (attribute == null)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.NULL_ATTR));
		}
		if (attribute.getAttributeTypeInformation() instanceof DateAttributeTypeInformation)
		{
			for (RuleInterface rule : attribute.getRuleCollection())
			{
				if (rule.getName().equalsIgnoreCase(CategoryCSVConstants.ALLOW_FUTURE_DATE)
						&& ((rules == null || rules.isEmpty()) || (!rules
								.containsKey(CategoryCSVConstants.ALLOW_FUTURE_DATE))))
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties
									.getValue(CategoryConstants.NO_OVERRIDE_FUTURE_DATE_RULE)
							+ attribute.getName());
				}
			}
		}
	}

	/**
	 * this method validates for future date(default value given while creating category) given for an attribute
	 * @param attribute attribute
	 * @param rules rule collection
	 * @throws DynamicExtensionsSystemException fails to create category
	 * @throws ParseException
	 */
	public static void validateCSVFutureDateValue(AttributeInterface attribute,
			Map<String, Object> rules, String value) throws DynamicExtensionsSystemException,
			ParseException
	{
		if (attribute == null)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.NULL_ATTR));
		}
		if (attribute.getAttributeTypeInformation() instanceof DateAttributeTypeInformation)
		{
			if (rules != null && rules.containsKey(CategoryConstants.ALLOW_FUTURE_DATE)
					&& value != null && !(value.trim().equals("")))
			{
				FutureDateValidator futureDateValidation = new FutureDateValidator();
				String defaultDateValue = value.replaceAll("/", "-");
				try
				{
					futureDateValidation.validate((AttributeMetadataInterface) attribute,
							defaultDateValue, null, attribute.getName());
				}
				catch (DynamicExtensionsValidationException e)
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties.getValue(e.getErrorCode())
							+ attribute.getName(), e);
				}
			}
		}
	}

	/**
	 * @param attribute
	 * @param rules
	 * @throws DynamicExtensionsSystemException
	 * @throws ParseException
	 */
	public static void checkRequiredRule(AttributeInterface attribute, Map<String, Object> rules)
			throws DynamicExtensionsSystemException, ParseException
	{
		if (attribute == null)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.NULL_ATTR));
		}

		for (RuleInterface rule : attribute.getRuleCollection())
		{
			if (rule.getName().equalsIgnoreCase(CategoryCSVConstants.REQUIRED)
					&& (rules == null || rules.isEmpty()))
			{
				throw new DynamicExtensionsSystemException(ApplicationProperties
						.getValue(CategoryConstants.CREATE_CAT_FAILS)
						+ ApplicationProperties.getValue(CategoryConstants.NO_OVERRIDE_REQ_RULE)
						+ attribute.getName());
			}
		}
	}

	/**
	 * @return
	 */
	private String getErrorMessageStart()
	{
		return ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
				+ categoryFileParser.getLineNumber();
	}

	/**
	 * User should not be allowed to use root entity twice in the category creation.
	 * @param entityName
	 * @param mainForms
	 * @param catEntNames
	 * @throws DynamicExtensionsSystemException
	 */
	public void isRootEntityUsedTwice(CategoryEntityInterface rootCategoryEntity,
			EntityInterface rootEntityInterface) throws DynamicExtensionsSystemException
	{
		if (rootCategoryEntity != null)
		{
			for (CategoryAssociationInterface categoryAssociationInterface : rootCategoryEntity
					.getCategoryAssociationCollection())
			{
				if (categoryAssociationInterface.getTargetCategoryEntity().getEntity().equals(
						rootEntityInterface))
				{
					String errorMessage = getErrorMessageStart()
							+ ApplicationProperties.getValue(CategoryConstants.ROOT_ENT_TWICE)
							+ categoryAssociationInterface.getTargetCategoryEntity().getEntity()
									.getName();
					throw new DynamicExtensionsSystemException(errorMessage);
				}
				else
				{
					isRootEntityUsedTwice(categoryAssociationInterface.getTargetCategoryEntity(),
							rootEntityInterface);
				}
			}
		}
	}

	/**
	 * This method checks whether 'textArea' is the control type specified for numeric type field.  
	 * @param controlType
	 * @param attribute
	 * @throws DynamicExtensionsSystemException
	 */
	public void isTextAreaForNumeric(String controlType, AttributeInterface attribute)
			throws DynamicExtensionsSystemException
	{
		if (CategoryCSVConstants.TEXT_AREA.equals(controlType))
		{
			AttributeTypeInformationInterface attrTypeInfo = attribute
					.getAttributeTypeInformation();
			if (attrTypeInfo instanceof NumericAttributeTypeInformation)
			{
				throw new DynamicExtensionsSystemException(ApplicationProperties
						.getValue(CategoryConstants.CREATE_CAT_FAILS)
						+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
						+ categoryFileParser.getLineNumber()
						+ ApplicationProperties.getValue(CategoryConstants.NO_TEXTAREA)
						+ attribute.getName());
			}
		}
	}

	/**
	 * This method checks if multiselect specified for a category attribute is valid.
	 * @param entity
	 * @param attributeName
	 * @param control
	 * @throws DynamicExtensionsSystemException
	 */
	public static void checkIsMultiSelectValid(EntityInterface entity, String attributeName,
			ControlInterface control) throws DynamicExtensionsSystemException
	{
		AbstractAttributeInterface abstractAttribute = entity
				.getAbstractAttributeByName(attributeName);

		if (control instanceof ListBoxInterface)
		{
			Boolean isMultiSelect = ((ListBoxInterface) control).getIsMultiSelect();
			Boolean IsUsingAutoCompleteDropdown = ((ListBoxInterface) control)
					.getIsUsingAutoCompleteDropdown();

			if (IsUsingAutoCompleteDropdown != null && IsUsingAutoCompleteDropdown)
			{
				if (isMultiSelect == null || !isMultiSelect)
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties
									.getValue(CategoryConstants.NO_MULTI_SELECT_WITH_ACD)
							+ attributeName);
				}

				Integer noOfRows = ((ListBoxInterface) control).getNoOfRows();
				if (noOfRows != null && noOfRows > 4)
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties
									.getValue(CategoryConstants.NO_NOOFROWS_WITH_ACD)
							+ attributeName);
				}
			}

			if (isMultiSelect != null && isMultiSelect && abstractAttribute != null)
			{
				if (!(abstractAttribute instanceof AssociationInterface))
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties
									.getValue(CategoryConstants.INVALID_MULTI_SELECT)
							+ attributeName);
				}
				else
				{
					Boolean isCollection = ((AssociationInterface) abstractAttribute)
							.getIsCollection();
					if (isCollection != null && !isCollection)
					{
						throw new DynamicExtensionsSystemException(ApplicationProperties
								.getValue(CategoryConstants.CREATE_CAT_FAILS)
								+ ApplicationProperties
										.getValue(CategoryConstants.INVALID_MULTI_SELECT)
								+ attributeName);
					}
				}
			}
		}
		else
		{
			if (abstractAttribute instanceof AssociationInterface)
			{
				Boolean isCollection = ((AssociationInterface) abstractAttribute).getIsCollection();
				if (isCollection != null && isCollection)
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties
									.getValue(CategoryConstants.INVALID_CONTROL_FOR_MULTI_SELECT)
							+ attributeName);
				}
			}
		}
	}

	/**
	 * This method checks if the heading information has been repeated and
	 * throws an exception if it is repeated.
	 * @throws DynamicExtensionsSystemException
	 */
	public static void checkHeadingInfoRepeatation(String lineAfterHeading, long lineNumber)
			throws DynamicExtensionsSystemException
	{
		if (lineAfterHeading.startsWith(CategoryConstants.HEADING))
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue("headingInfoRepeted") + lineNumber);
		}
	}

	/**
	 * This method checks if the note is appropriate.
	 * @param note
	 * @param lineNumber
	 * @throws DynamicExtensionsSystemException
	 */
	public static void checkIfNoteIsAppropriate(String note, long lineNumber)
			throws DynamicExtensionsSystemException
	{
		if (note.trim().split("~").length != 2 || note.trim().split("~")[1].length() > 255
				|| note.contains(":"))
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue("noteNotAppropriate") + lineNumber);
		}
	}

	/**
	 * This method checks if the heading is appropriate.
	 * @param note
	 * @param lineNumber
	 * @throws DynamicExtensionsSystemException
	 */
	public static void checkIfHeadingIsAppropriate(String heading, long lineNumber)
			throws DynamicExtensionsSystemException
	{
		if (heading.split("~").length != 2 || heading.split("~")[1].length() > 255
				|| heading.contains(":") || heading.contains(","))
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue("headingNotAppropriate") + lineNumber);
		}
	}

	/**
	 * @param minValue
	 * @param maxValue
	 * @param attribute
	 * @throws DynamicExtensionsSystemException
	 */
	private static void validateCategoryAttributeRangeValues(String minValue, String maxValue,
			AttributeInterface attribute) throws DynamicExtensionsSystemException
	{
		AttributeTypeInformationInterface attrTypeInfo = attribute.getAttributeTypeInformation();

		if (minValue != null && maxValue != null)
		{
			if (attrTypeInfo instanceof NumericAttributeTypeInformation)
			{
				if (Double.parseDouble(minValue) > Double.parseDouble(maxValue))
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties.getValue(CategoryConstants.INCORRECT_MINMAX)
							+ attribute.getName());
				}
			}
			else
			{
				String dateFormat = ((DateAttributeTypeInformation) attrTypeInfo).getFormat();
				int result = DynamicExtensionsUtility.compareDates(minValue, maxValue, dateFormat);
				if (result == 1 || result == -2)
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties
									.getValue(CategoryConstants.INCORRECT_MINMAX_DATE)
							+ attribute.getName());
				}
			}
		}
	}

	/**
	 * @param controlType
	 * @param controlXPosition
	 * @param container 
	 * @throws DynamicExtensionsSystemException
	 */
	public void validateControlInSingleLine(String controlType, int controlXPosition,
			ContainerInterface container) throws DynamicExtensionsSystemException
	{
		ControlEnum controlEnum = ControlEnum.get(controlType);
		if (!isValidControlType(controlType)
				|| !(controlEnum.equals(ControlEnum.LIST_BOX_CONTROL)
						|| controlEnum.equals(ControlEnum.COMBO_BOX_CONTROL) || controlEnum
						.equals(ControlEnum.TEXT_FIELD_CONTROL)))
		{
			throw new DynamicExtensionsSystemException(getErrorMessageStart()
					+ ApplicationProperties
							.getValue("dyExtn.category.validation.singleLineDisaply"));
		}
	}

	/**
	 * @param controlCollection
	 * @return
	 */
	private static ControlEnum getAllowedControlType(List<ControlInterface> controlCollection)
	{
		ControlEnum controlEnum = null;
		for (ControlInterface controlInterface : controlCollection)
		{
			if (controlInterface.getBaseAbstractAttribute() != null)
			{
				if (controlInterface instanceof ListBoxInterface)
				{
					controlEnum = ControlEnum.LIST_BOX_CONTROL;
				}
				else if (controlInterface instanceof TextFieldInterface)
				{
					controlEnum = ControlEnum.TEXT_FIELD_CONTROL;
				}
				else if (controlInterface instanceof ComboBoxInterface)
				{
					controlEnum = ControlEnum.COMBO_BOX_CONTROL;
				}
			}
		}

		return controlEnum;
	}

	/**
	 * @param containerInterface
	 * @param sequenceNumber
	 * @return
	 */
	private static List<ControlInterface> getAllControlWithSameSequenceNumber(
			ContainerInterface containerInterface, Integer sequenceNumber)
	{
		Collection<ControlInterface> controlCollection = containerInterface.getControlCollection();
		List<ControlInterface> controlsWithSameSequenceNumber = new ArrayList<ControlInterface>();
		if (controlCollection != null && controlCollection.size() > 0)
		{
			for (ControlInterface controlInterface : controlCollection)
			{
				if (sequenceNumber.equals(controlInterface.getSequenceNumber()))
				{
					controlsWithSameSequenceNumber.add(controlInterface);
				}
			}
		}

		return controlsWithSameSequenceNumber;
	}

	/**
	 * @param controlType
	 * @return
	 */
	public static boolean isValidControlType(String controlType)
	{
		boolean isValid = true;

		if (controlType == null)
		{
			isValid = false;
		}

		return isValid;
	}

}
