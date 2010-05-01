
package edu.common.dynamicextensions.domaininterface;

import java.util.Collection;

import edu.common.dynamicextensions.domain.SemanticAnnotatableInterface;

/**
 * When the value domain for an attribute is user defined,the data element object is of type CaDSRDE 
 * and this object contains a collection of permissible values.  
 * @author sujay_narkar

 */
public interface PermissibleValueInterface extends SemanticAnnotatableInterface
{

	/**
	 * This method returns the unique identifier.
	 * @return the unique identifier.
	 */
	Long getId();

	/**
	 * This method returns the value of DateValue downcasted to the Object.
	 * @return the value of the DateValue downcasted to the Object.
	 */
	Object getValueAsObject();
	/**
	 * 
	 * @return
	 */
	Collection<SkipLogicAttributeInterface> getDependentSkipLogicAttributes();
	/**
	 * 
	 * @param dependentSkipLogicAttributes
	 */
	void setDependentSkipLogicAttributes(
			Collection<SkipLogicAttributeInterface> dependentSkipLogicAttributes);
	/**
	 * This method adds a skip logic attribute.
	 * @param skipLogicAttributeInterface
	 */
	void addDependentSkipLogicAttribute(SkipLogicAttributeInterface skipLogicAttributeInterface);
	/**
	 * This method removes a SkipLogic Attribute.
	 * @param skipLogicAttributeInterface.
	 */
	void removeDependentSkipLogicAttribute(SkipLogicAttributeInterface skipLogicAttributeInterface);
	/**
	 * This method removes all SkipLogic Attributes.
	 */
	void removeAllDependentSkipLogicAttributes();
	/**
	 * 
	 * @param permissibleValue
	 * @return
	 */
	PermissibleValueInterface clone();

}
