
package edu.common.dynamicextensions.validation;

import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsValidationException;

/**
 * @author chetan_patil
 *
 */
public class RequiredValidator implements ValidatorRuleInterface
{

	/**
	 * @see edu.common.dynamicextensions.validation.ValidatorRuleInterface#validate(edu.common.dynamicextensions.domaininterface.AttributeInterface, java.lang.Object, java.util.Map)
	 * @throws DynamicExtensionsValidationException
	 */
	public boolean validate(AttributeMetadataInterface attribute, Object valueObject,
			Map parameterMap, String controlCaption) throws DynamicExtensionsValidationException
	{
		if (valueObject == null)
		{
			throw new DynamicExtensionsValidationException("Validation failed", null,
					"dynExtn.validation.RequiredValidator", controlCaption);
		}
		else
		{
			if (valueObject instanceof List)
			{
				List valueList = (List) valueObject;
				if (valueList.isEmpty())
				{
					throw new DynamicExtensionsValidationException("Validation failed", null,
							"dynExtn.validation.RequiredValidator", controlCaption);
				}
			}
			else if (valueObject instanceof String && (valueObject.toString()).trim().equals(""))
			{
				throw new DynamicExtensionsValidationException("Validation failed", null,
						"dynExtn.validation.RequiredValidator", controlCaption);
			}
		}

		return true;
	}
}
