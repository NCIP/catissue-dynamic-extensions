
package edu.common.dynamicextensions.domaininterface;

/**
 *
 * @author rajesh_patil
 *
 */
public interface SkipLogicAttributeInterface
{

	/**
	 *
	 * @param categoryattributeinterface
	 */
	void setSourceSkipLogicAttribute(CategoryAttributeInterface categoryattributeinterface);

	/**
	 *
	 * @return
	 */
	CategoryAttributeInterface getSourceSkipLogicAttribute();

	/**
	 *
	 * @return
	 */
	CategoryAttributeInterface getTargetSkipLogicAttribute();

	/**
	 *
	 * @param categoryattributeinterface
	 */
	void setTargetSkipLogicAttribute(CategoryAttributeInterface categoryattributeinterface);

	/**
	 *
	 * @return
	 */
	String getDefaultValue();

	/**
	 *
	 * @param permissiblevalueinterface
	 */
	void setDefaultValue(PermissibleValueInterface permissiblevalueinterface);

	/**
	 *
	 * @param dataelementinterface
	 */
	void setDataElement(DataElementInterface dataelementinterface);

	/**
	 *
	 * @return
	 */
	DataElementInterface getDataElement();

	/**
	 *
	 */
	void clearDataElementCollection();

	/**
	 *
	 * @return
	 */
	boolean getIsSkipLogic();
}