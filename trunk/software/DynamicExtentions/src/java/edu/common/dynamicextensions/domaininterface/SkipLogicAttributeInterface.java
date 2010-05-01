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
    public abstract void setSourceSkipLogicAttribute(CategoryAttributeInterface categoryattributeinterface);
    /**
     * 
     * @return
     */
    public abstract CategoryAttributeInterface getSourceSkipLogicAttribute();
    /**
     * 
     * @return
     */
    public abstract CategoryAttributeInterface getTargetSkipLogicAttribute();
    /**
     * 
     * @param categoryattributeinterface
     */
    public abstract void setTargetSkipLogicAttribute(CategoryAttributeInterface categoryattributeinterface);
    /**
     * 
     * @return
     */
    public abstract String getDefaultValue();
    /**
     * 
     * @param permissiblevalueinterface
     */
    public abstract void setDefaultValue(PermissibleValueInterface permissiblevalueinterface);
    /**
     * 
     * @param dataelementinterface
     */
    public abstract void setDataElement(DataElementInterface dataelementinterface);
    /**
     * 
     * @return
     */
    public abstract DataElementInterface getDataElement();
    /**
     * 
     */
    public abstract void clearDataElementCollection();
    /**
     * 
     * @return
     */
    public abstract boolean getIsSkipLogic();
}