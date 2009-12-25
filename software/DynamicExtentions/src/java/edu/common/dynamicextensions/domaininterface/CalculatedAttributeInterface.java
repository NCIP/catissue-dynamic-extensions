package edu.common.dynamicextensions.domaininterface;
/**
 *
 * @author rajesh_patil
 *
 */
public interface CalculatedAttributeInterface
{
	/**
	 *
	 * @return
	 */
	CategoryAttributeInterface getCalculatedAttribute();
	/**
	 *
	 * @param sourceSkipLogicAttribute
	 */
	void setCalculatedAttribute(CategoryAttributeInterface sourceSkipLogicAttribute);
	/**
	 *
	 * @return
	 */
	CategoryAttributeInterface getSourceForCalculatedAttribute();
	/**
	 *
	 * @param targetSkipLogicAttribute
	 */
	void setSourceForCalculatedAttribute(CategoryAttributeInterface targetSkipLogicAttribute);
}
