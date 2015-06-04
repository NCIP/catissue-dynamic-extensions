
package edu.common.dynamicextensions.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.common.dynamicextensions.domain.DoubleAttributeTypeInformation;
import edu.common.dynamicextensions.domain.FloatAttributeTypeInformation;
import edu.common.dynamicextensions.domain.IntegerAttributeTypeInformation;
import edu.common.dynamicextensions.domain.LongAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.exception.DataTypeFactoryInitializationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsValidationException;
import edu.common.dynamicextensions.ui.util.Constants;

/**
 * This Class validates the range of the numeric data entered by the user. It checks whether the value entered is between the
 * specified range. The extreme values of the range are obtained from the RuleParameter of the Rule instance.
 * @author chetan_patil
 * @version 1.0
 */
public class RangeValidator implements ValidatorRuleInterface
{

	/** The Constant DYN_EXTN_VALIDATION_RANGE_MAX. */
	private static final String DYN_EXTN_VALIDATION_RANGE_MAX = "dynExtn.validation.Range.Maximum";

	/** The Constant DYN_EXTN_VALIDATION_RANGE_MIN. */
	private static final String DYN_EXTN_VALIDATION_RANGE_MIN = "dynExtn.validation.Range.Minimum";

	/**
	 * This method implements the validate method of the ValidatorRuleInterface.
	 * This method validates the numeric data entered by the user against the maximum and the
	 * minimum values of the rule.
	 * @param attribute the Attribute whose corresponding value is to be verified.
	 * @param valueObject the value entered by the user.
	 * @param parameterMap the parameters of the Rule.
	 * @throws DynamicExtensionsValidationException if the value is not following the range Rule.
	 * @throws DataTypeFactoryInitializationException
	 */
	public boolean validate(AttributeMetadataInterface attribute, Object valueObject,
			Map<String, String> parameterMap, String controlCaption)
			throws DynamicExtensionsValidationException, DataTypeFactoryInitializationException
	{
		boolean valid = true;

		//Quick fix
		if (valueObject != null)
		{/* Check for the validity of the number */
			NumberValidator numberValidator = new NumberValidator();
			numberValidator.validate(attribute, valueObject, parameterMap, controlCaption);
		}

		/* Check for the validity of the range of the number against the predefined range*/
		if (valueObject != null && !(valueObject.toString()).trim().equals(""))
		{
			AttributeTypeInformationInterface attributeTypeInformation = attribute
					.getAttributeTypeInformation();

			if (attributeTypeInformation != null)
			{
				String value = valueObject.toString();

				Set<Map.Entry<String, String>> parameterSet = parameterMap.entrySet();
				for (Map.Entry<String, String> parameter : parameterSet)
				{
					if (attributeTypeInformation instanceof LongAttributeTypeInformation)
					{
						checkLongValidation(parameter, controlCaption, value);
					}
					else if (attributeTypeInformation instanceof IntegerAttributeTypeInformation)
					{
						checkIntegerValidation(parameter, controlCaption, value);
					}
					else if (attributeTypeInformation instanceof DoubleAttributeTypeInformation)
					{
						checkDoubleValidation(parameter, controlCaption, value);
					}
					else if (attributeTypeInformation instanceof FloatAttributeTypeInformation)
					{
						checkFloatValidation(parameter, controlCaption, value);
					}
				}
			}
		}

		return valid;
	}

	/**
	 * This method verifies if the number is a proper Long value or not.
	 * @param parameter the parameter of the rule in form of <ParameterName, Value> pair.
	 * @param attributeName the name of the Attribute.
	 * @param value the value to be verified.
	 * @throws DynamicExtensionsValidationException
	 */
	private void checkLongValidation(Map.Entry<String, String> parameter, String controlCaption,
			String value) throws DynamicExtensionsValidationException
	{
		String parameterName = parameter.getKey();
		String parameterValue = parameter.getValue();

		if (parameterName.equals(Constants.MIN_VALUE)
				&& Long.parseLong(value) < Long.parseLong(parameterValue))
		{
			reportOutOfRangeInput(controlCaption, parameterValue, DYN_EXTN_VALIDATION_RANGE_MIN);
		}
		else if (parameterName.equals(Constants.MAX_VALUE)
				&& Long.parseLong(value) > Long.parseLong(parameterValue))
		{
			reportOutOfRangeInput(controlCaption, parameterValue, DYN_EXTN_VALIDATION_RANGE_MAX);
		}
	}

	/**
	 * @param parameter
	 * @param controlCaption
	 * @param value
	 * @throws DynamicExtensionsValidationException
	 */
	private void checkIntegerValidation(Entry<String, String> parameter, String controlCaption,
			String value) throws DynamicExtensionsValidationException
	{
		String parameterName = parameter.getKey();
		String parameterValue = parameter.getValue();

		if (parameterName.equals(Constants.MIN_VALUE))
		{
			if (Integer.parseInt(value) < Integer.parseInt(parameterValue))
			{
				reportOutOfRangeInput(controlCaption, parameterValue, DYN_EXTN_VALIDATION_RANGE_MIN);
			}
		}
		else if (parameterName.equals(Constants.MAX_VALUE)
				&& Integer.parseInt(value) > Integer.parseInt(parameterValue))
		{
			reportOutOfRangeInput(controlCaption, parameterValue, DYN_EXTN_VALIDATION_RANGE_MAX);
		}
	}

	/**
	 * @param parameter
	 * @param controlCaption
	 * @param value
	 * @throws DynamicExtensionsValidationException
	 */
	private void checkDoubleValidation(Map.Entry<String, String> parameter, String controlCaption,
			String value) throws DynamicExtensionsValidationException
	{
		String parameterName = parameter.getKey();
		String parameterValue = parameter.getValue();

		if (parameterName.equals(Constants.MIN_VALUE)
				&& Double.parseDouble(value) < Double.parseDouble(parameterValue))
		{
			reportOutOfRangeInput(controlCaption, parameterValue, DYN_EXTN_VALIDATION_RANGE_MIN);
		}
		else if (parameterName.equals(Constants.MAX_VALUE)
				&& Double.parseDouble(value) > Double.parseDouble(parameterValue))
		{
			reportOutOfRangeInput(controlCaption, parameterValue, DYN_EXTN_VALIDATION_RANGE_MAX);
		}
	}

	/**
	 * @param parameter
	 * @param controlCaption
	 * @param value
	 * @throws DynamicExtensionsValidationException
	 */
	private void checkFloatValidation(Entry<String, String> parameter, String controlCaption,
			String value) throws DynamicExtensionsValidationException
	{
		String parameterName = parameter.getKey();
		String parameterValue = parameter.getValue();

		if (parameterName.equals(Constants.MIN_VALUE))
		{
			if (Float.parseFloat(value) < Float.parseFloat(parameterValue))
			{
				reportOutOfRangeInput(controlCaption, parameterValue, DYN_EXTN_VALIDATION_RANGE_MIN);
			}
		}
		else if (parameterName.equals(Constants.MAX_VALUE)
				&& Float.parseFloat(value) > Float.parseFloat(parameterValue))
		{
			reportOutOfRangeInput(controlCaption, parameterValue, DYN_EXTN_VALIDATION_RANGE_MAX);
		}
	}

	/**
	 * @param controlCaption
	 * @param parameterValue
	 * @param errorKey
	 * @throws DynamicExtensionsValidationException
	 */
	private void reportOutOfRangeInput(String controlCaption, String parameterValue, String errorKey)
			throws DynamicExtensionsValidationException
	{
		List<String> placeHolders = new ArrayList<String>();
		placeHolders.add(controlCaption);
		placeHolders.add(parameterValue);
		throw new DynamicExtensionsValidationException("Validation failed", null, errorKey,
				placeHolders);
	}

}
