
package edu.common.dynamicextensions.validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.exception.DynamicExtensionsValidationException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.util.global.CommonServiceLocator;

/**
 * @author suhas_khot
 * This class allows us to validate for the future date rule for the corresponding attribute.
 *
 */
public class FutureDateValidator implements ValidatorRuleInterface
{

	/**
	 * This method validate for date to be in valid format.
	 * @param attribute to be validated for allowing future date
	 * @param valueObject value of an attribute
	 * @param parameterMap map of parameter for validating allow future date rule
	 * @param controlCaption name of an attribute
	 * @return true if future date is allowed.
	 * @throws DynamicExtensionsValidationException failed to allow future date validate the date rule
	 * @throws DynamicExtensionsSystemException failed to allow future date validate the date rule
	 */
	public boolean validate(AttributeMetadataInterface attribute, Object valueObject,
			Map<String, String> parameterMap, String controlCaption)
			throws DynamicExtensionsValidationException, DynamicExtensionsSystemException
	{
		/* Check for the validity of the future date */
		DateValidator dateValidator = new DateValidator();
		dateValidator.validate(attribute, valueObject, parameterMap, controlCaption, true);

		validateFutureDate(attribute, valueObject, controlCaption);

		return true;
	}

	/**
	 * This method validate for allowing future date validation.
	 * @param attribute to be validated for allowing future date
	 * @param valueObject value of an attribute
	 * @param controlCaption name of an attribute
	 * @throws DynamicExtensionsValidationException failed to allow future date validate the date rule
	 */
	private void validateFutureDate(AttributeMetadataInterface attribute, Object valueObject,
			String controlCaption) throws DynamicExtensionsValidationException
	{
		AttributeTypeInformationInterface attributeTypeInformation = attribute
				.getAttributeTypeInformation();

		if (((valueObject != null) && (!((String) valueObject).trim().equals("")))
				&& ((attributeTypeInformation != null) && (attributeTypeInformation instanceof DateAttributeTypeInformation)))
		{
			Date inputDate = null;
			DateAttributeTypeInformation dateAttributeTypeInformation = (DateAttributeTypeInformation) attributeTypeInformation;
			String dateFormat = DynamicExtensionsUtility.getDateFormat(dateAttributeTypeInformation.getFormat());
			String value = (String) valueObject;
			try
			{
				Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, locale);
				simpleDateFormat.setLenient(false);
				inputDate = simpleDateFormat.parse(value);
			}
			catch (ParseException parseException)
			{
				ValidatorUtil.reportInvalidInput(controlCaption, "today's date.", "dynExtn.validation.Date.MinDate");
			}

			reportInvalidDate(inputDate, controlCaption);
		}
	}

	/**
	 * This method reports the input for the date is valid or not.
	 * @param tempDate
	 * @param controlCaption
	 * @throws DynamicExtensionsValidationException
	 */
	private void reportInvalidDate(Date tempDate, String controlCaption)
			throws DynamicExtensionsValidationException
	{
		// check if input date is past date.
		if (tempDate.before(new Date()))
		{
			ValidatorUtil.reportInvalidInput(controlCaption, "today's date.",
					"dynExtn.validation.Date.MinDate");
		}
	}
}
