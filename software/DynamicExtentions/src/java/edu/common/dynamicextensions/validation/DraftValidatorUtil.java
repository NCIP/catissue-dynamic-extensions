package edu.common.dynamicextensions.validation;

import java.util.Collection;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

public class DraftValidatorUtil extends ValidatorUtil
{
	/**
	 * Validate attribute for given attribute.
	 * @param attributeValueNode Value for attribute.
	 * @param controlCaption Caption of control.
	 * @return errorList List of errors.
	 * @throws DynamicExtensionsSystemException
	 */
	public List<String> validateAttributes(BaseAbstractAttributeInterface abstractAttribute,
			Object val, String controlCaption) throws DynamicExtensionsSystemException
	{
		Collection<RuleInterface> attributeRuleCollection = getRuleCollectionExcludingRequiredRule(abstractAttribute);
		
		return validateAttribute(abstractAttribute, val,
				controlCaption,
				attributeRuleCollection);
	}
}
