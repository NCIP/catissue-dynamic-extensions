package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.CalculatedAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;


/**
 * This Class represents the Calculated Attribute.
 * @hibernate.joined-subclass table="DYEXTN_CALCULATED_ATTRIBUTE"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class CalculatedAttribute extends BaseAbstractAttribute implements CalculatedAttributeInterface
{
    /**
     *
     */
    protected static final long serialVersionUID = 12345254735L;
    /**
     *
     */
    protected CategoryAttributeInterface calculatedAttribute;
    /**
     *
     */
    protected CategoryAttributeInterface sourceForCalculatedAttribute;
	/**
	 * @return
	 * @hibernate.many-to-one cascade="save-update" column="SOURCE_CAT_ATTR_ID" class="edu.common.dynamicextensions.domain.CategoryAttribute" constrained="true"
	 */
    public CategoryAttributeInterface getCalculatedAttribute()
    {
        return calculatedAttribute;
    }
    /**
     *
     */
    public void setCalculatedAttribute(CategoryAttributeInterface calculatedAttribute)
    {
        this.calculatedAttribute = calculatedAttribute;
    }
	/**
	 * @return
	 * @hibernate.many-to-one cascade="save-update" column="TARGET_CAL_ATTR_ID" class="edu.common.dynamicextensions.domain.CategoryAttribute" constrained="true"
	 */
    public CategoryAttributeInterface getSourceForCalculatedAttribute()
    {
        return sourceForCalculatedAttribute;
    }
    /**
     *
     */
    public void setSourceForCalculatedAttribute(CategoryAttributeInterface sourceForCalculatedAttribute)
    {
        this.sourceForCalculatedAttribute = sourceForCalculatedAttribute;
    }

}