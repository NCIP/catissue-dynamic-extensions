
package edu.common.dynamicextensions.validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsValidationException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.util.global.CommonServiceLocator;

/**
 * @author chetan_patil
 *
 */
public class DateValidator implements ValidatorRuleInterface
{

	private static Map<String, String> datePatternVsRegexMap;
	static
	{
		datePatternVsRegexMap = DynamicExtensionsUtility.getAllValidDatePatterns();
	}

	/**
	 * @see edu.common.dynamicextensions.validation.ValidatorRuleInterface#validate(edu.common.dynamicextensions.domaininterface.AttributeInterface, java.lang.Object, java.util.Map)
	 * @throws DynamicExtensionsValidationException
	 */
	public boolean validate(AttributeMetadataInterface attribute, Object valueObject,
			Map<String, String> parameterMap, String controlCaption)
			throws DynamicExtensionsValidationException
	{
		return validateDate(attribute, valueObject, controlCaption);
	}

	/**
	 * Validate user input for permissible date values for date with range.
	 * @param attribute
	 * @param valueObject
	 * @param parameterMap
	 * @param controlCaption
	 * @param isFromDateRangeValidator
	 * @return
	 * @throws DynamicExtensionsValidationException
	 */
	public boolean validate(AttributeMetadataInterface attribute, Object valueObject,
			Map<String, String> parameterMap, String controlCaption,
			boolean isFromDateRangeValidator) throws DynamicExtensionsValidationException
	{
		return validateDate(attribute, valueObject, controlCaption, isFromDateRangeValidator);
	}

	/**
	 * Validate user input for permissible date values.
	 * @param attribute
	 * @param valueObject
	 * @param controlCaption
	 * @param isFromDateRangeValidator
	 * @return
	 * @throws DynamicExtensionsValidationException
	 */
	private boolean validateDate(AttributeMetadataInterface attribute, Object valueObject,
			String controlCaption, boolean... isFromDateRangeValidator)
			throws DynamicExtensionsValidationException
	{
		AttributeTypeInformationInterface attributeTypeInformation = attribute
				.getAttributeTypeInformation();
		boolean isDateValid = true;

		if (isValidAttributeType(valueObject, attributeTypeInformation))
		{
			String value = valueObject.toString();

			DateAttributeTypeInformation dateAttributeTypeInformation = (DateAttributeTypeInformation) attributeTypeInformation;
			String dateFormat = DynamicExtensionsUtility.getDateFormat(dateAttributeTypeInformation
					.getFormat());
			isDateValid = isValidDate(value, dateFormat, controlCaption, isFromDateRangeValidator);

			if (!isDateValid)
			{
				ValidatorUtil.reportInvalidInput(controlCaption, dateFormat,
						"dynExtn.validation.Date");
			}
		}

		return isDateValid;
	}

	/**
	 * Validates the given value as per the date format using
	 * the regex as well as the parser, in case of range validataor validates
	 * that the date is less than the todays date also.
	 * @param value value to validate.
	 * @param dateFormat format according to which validate.
	 * @param controlCaption controls caption to show in error.
	 * @param isFromDateRangeValidator is from range validation.
	 * @return true if date is in proper format.
	 * @throws DynamicExtensionsValidationException exception.
	 */
	private boolean isValidDate(String value, String dateFormat, String controlCaption,
			boolean... isFromDateRangeValidator) throws DynamicExtensionsValidationException
	{
		boolean isValid = !value.endsWith("0000") && isValidDatePattern(value, dateFormat);
		try
		{
			if (isValid)
			{
				Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, locale);
				simpleDateFormat.setLenient(false);
				Date tempDate = simpleDateFormat.parse(value);

				if (isFromDateRangeValidator.length == 0 && tempDate.after(new Date()))
				{
					ValidatorUtil.reportInvalidInput(controlCaption, "today's date.",
							"dynExtn.validation.Date.Max");
				}
			}
		}
		catch (ParseException parseException)
		{
			isValid = false;
		}
		return isValid;
	}

	/**
	 * Validated weather the value & the attribute type objects are proper.
	 * @param valueObject value object.
	 * @param attributeTypeInformation attribute type info.
	 * @return
	 */
	private boolean isValidAttributeType(Object valueObject,
			AttributeTypeInformationInterface attributeTypeInformation)
	{
		return ((valueObject != null) && (!((String) valueObject).trim().equals("")))
				&& ((attributeTypeInformation != null) && (attributeTypeInformation instanceof DateAttributeTypeInformation));
	}

	/**
	 * validates the checkDate date provided according to the regex mentioned for the
	 * given datePattern.
	 * @param checkDate date value
	 * @param datePattern date pattern against which to validate.
	 * @return valid or not.
	 */
	private boolean isValidDatePattern(String checkDate, String datePattern)
	{
		Pattern pattern = Pattern.compile(datePatternVsRegexMap.get(datePattern),
				Pattern.CASE_INSENSITIVE);
		Matcher mat = pattern.matcher(checkDate);
		return mat.matches();

	}

	/**
	 * It will read the ValidDatePatterns.XML & verify that the date patterns
	 * specified in the ApplicationResources.properties Files is in the given patterns.
	 * else will throw the Exception. It will also read the regex defined for each pattern
	 * & keep it for future date validations using this class.
	 */
	public static void validateGivenDatePatterns()
	{
		if (!(datePatternVsRegexMap.containsKey(ProcessorConstants.DATE_ONLY_FORMAT)
				&& datePatternVsRegexMap.containsKey(ProcessorConstants.DATE_TIME_FORMAT)
				&& datePatternVsRegexMap.containsKey(ProcessorConstants.MONTH_YEAR_FORMAT) && datePatternVsRegexMap
				.containsKey(ProcessorConstants.YEAR_ONLY_FORMAT)))
		{
			throw new IllegalArgumentException(
					"Invalid date pattern specified in the Application resource file");
		}

	}
}
