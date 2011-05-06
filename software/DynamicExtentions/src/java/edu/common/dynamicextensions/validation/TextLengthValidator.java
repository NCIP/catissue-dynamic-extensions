/**
 *
 */

package edu.common.dynamicextensions.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domain.StringAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsValidationException;

/**
 * TextLengthValidator Class validates the text as per the text length defined for that Control during its creation.
 * If the no text length is provided or the text lenght is zero, no validation checks are made.
 * @author chetan_patil
 */
public class TextLengthValidator implements ValidatorRuleInterface
{

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.validation.ValidatorRuleInterface#validate(edu.common.dynamicextensions.domaininterface.AttributeInterface, java.lang.Object, java.util.Map)
	 */
	public boolean validate(AttributeMetadataInterface attribute, Object valueObject,
			Map<String, String> parameterMap, String controlCaption)
			throws DynamicExtensionsValidationException
	{
		boolean isValid = false;

		//If control of type TextField is changed to the
		//ListBox while creating category. These validations should be
		//skipped in that case.
		if (valueObject instanceof List)
		{
			isValid = true;
		}
		else if (valueObject != null)
		{

			AttributeTypeInformationInterface attributeTypeInformation = attribute
					.getAttributeTypeInformation();
			if (attributeTypeInformation instanceof StringAttributeTypeInformation)
			{
				String value = valueObject.toString();
				StringAttributeTypeInformation stringAttributeTypeInformation = (StringAttributeTypeInformation) attributeTypeInformation;
				Integer size = stringAttributeTypeInformation.getSize();
				Integer length = value.length();
				for (int i = 0; i < value.length(); i++)
				{
					if (value.charAt(i) == '\n')
					{
						length -= 2;
					}
				}
				//skip the maxlength validation if maxlength for the text field is not defined
				if ((size > 0) && length > size)
				{
					ArrayList<String> placeHolders = new ArrayList<String>();
					placeHolders.add(controlCaption);
					placeHolders.add(Long.toString(size));
					throw new DynamicExtensionsValidationException("Validation failed", null,
							"dynExtn.validation.TextLength", placeHolders);
				}
				else
				{
					isValid = true;
				}
			}
		}

		return isValid;
	}
}
