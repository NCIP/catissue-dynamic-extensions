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
	CategoryAttributeInterface getSourceCategoryAttribute();
	/**
	 *
	 * @param sourceSkipLogicAttribute
	 */
	void setSourceCategoryAttribute(CategoryAttributeInterface sourceSkipLogicAttribute);
	/**
	 *
	 * @return
	 */
	CategoryAttributeInterface getTargetCalculatedAttribute();
	/**
	 *
	 * @param targetSkipLogicAttribute
	 */
	void setTargetCalculatedAttribute(CategoryAttributeInterface targetSkipLogicAttribute);
}
