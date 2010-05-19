/**
 *
 */

package edu.common.dynamicextensions.validation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domain.StringAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsValidationException;

/**
 * CharacterSetValidator Class validates the text as per the text length defined for that Control during its creation.
 * If the no text length is provided or the text lenght is zero, no validation checks are made.
 * @author rajesh_patil
 */
public class CharacterSetValidator implements ValidatorRuleInterface
{

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.validation.ValidatorRuleInterface#validate(edu.common.dynamicextensions.domaininterface.AttributeInterface, java.lang.Object, java.util.Map)
	 */
	public boolean validate(AttributeMetadataInterface attribute, Object valueObject,
			Map<String, String> parameterMap, String controlCaption)
			throws DynamicExtensionsValidationException
	{
		boolean isValid = false;
		AttributeTypeInformationInterface attributeTypeInformation = attribute
				.getAttributeTypeInformation();

		//If control of type TextField is changed to the
		//ListBox while creating category. These validations should be
		//skipped in that case.
		if (valueObject instanceof List)
		{
			isValid = true;
		}
		else if (valueObject != null)
		{
			String value = (String) valueObject;
			if (attributeTypeInformation instanceof StringAttributeTypeInformation)
			{
				ArrayList<String> placeHolders = new ArrayList<String>();

				isValid = validateISOCharacterSet(value, placeHolders, controlCaption);
			}
		}

		return isValid;
	}

	/**
	 * validated weather the value contains valid Iso characters or not.
	 * @param value value to be verify
	 * @param placeHolders place holders
	 * @return true if valid characters are used else false.
	 * @throws DynamicExtensionsValidationException exception.
	 */
	public boolean validateISOCharacterSet(String value, List<String> placeHolders,
			String controlCaption) throws DynamicExtensionsValidationException

	{
		BufferedReader reader = new BufferedReader(new StringReader(value));
		try
		{
			int character = reader.read();
			while (character != -1)
			{
				boolean isValidCharSet = ((character < 32 && character != 9 && character != 10 && character != 13)
						|| (character > 126 && character < 160) || character > 255);
				if (isValidCharSet)
				{
					placeHolders.add(controlCaption);
					placeHolders.add(String.valueOf((char) character));
					throw new DynamicExtensionsValidationException("Validation failed", null,
							"dynExtn.validation.characterSet", placeHolders);
				}
				character = reader.read();
			}
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsValidationException("Validation failed", e);
		}
		return true;
	}
}
