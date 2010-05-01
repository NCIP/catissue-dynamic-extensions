
package edu.common.dynamicextensions.domaininterface.validationrules;

import java.util.Collection;

import edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface;

/**
 * Rules are the validations put by the end user on the Attributes of the Entities they create.
 * @author sujay_narkar
 * @version 1.0
 */
public interface RuleInterface extends DynamicExtensionBaseDomainObjectInterface
{

	/**
	 * This method returns the Unique Identifier of the Object.
	 * @return the Unique Identifier of the Object.
	 */
	Long getId();

	/**
	 * This method returns the name of the Rule.
	 * @return the name of the Rule.
	 */
	String getName();

	/**
	 * This method sets the name of the Rule.
	 * @param name the name to be set.
	 */
	void setName(String name);

	/**
	 * This method returns the Collection of RuleParameters.
	 * @return the Collection of RuleParameters.
	 */
	Collection<RuleParameterInterface> getRuleParameterCollection();

	/**
	 * This method sets ruleParameterCollection to the Collection of RuleParameters.
	 * @param ruleParameterCollection the the Collection of RuleParameters to be set.
	 */
	void setRuleParameterCollection(Collection<RuleParameterInterface> ruleParameterCollection);

	Boolean getIsImplicitRule();

	void setIsImplicitRule(Boolean isImplicitRule);

}
