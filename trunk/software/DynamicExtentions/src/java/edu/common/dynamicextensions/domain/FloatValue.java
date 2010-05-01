
package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.FloatValueInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;

/**
 * @author sujay_narkar
 * @hibernate.joined-subclass table="DYEXTN_FLOAT_CONCEPT_VALUE" 
 * @hibernate.joined-subclass-key column="IDENTIFIER"  
 *
 */
public class FloatValue extends PermissibleValue implements FloatValueInterface
{

	/**
	 * Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = 8774354583244985478L;

	/**
	 * The predefined Float value.
	 */
	protected Float value;

	/**
	 * This method returns the predefined value of FloatValue.
	 * @hibernate.property name="value" type="float" column="VALUE" 
	 * @return Returns the predefined value of FloatValue.
	 */
	public Float getValue()
	{
		return value;
	}

	/**
	 * This method sets the value of FloatValue to the given value.
	 * @param value the value to be set.
	 */
	public void setValue(Float value)
	{
		this.value = value;
	}

	/**
	 * This method returns the value of FloatValue downcasted to the Object.
	 * @return the value of the DateValue downcasted to the Object.
	 */
	public Object getValueAsObject()
	{
		return value;
	}

	/**
	 * 
	 */
	public PermissibleValueInterface clone()
	{
		FloatValueInterface floatValueInterface = DomainObjectFactory.getInstance()
				.createFloatValue();
		floatValueInterface.setValue(this.value);
		return floatValueInterface;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domain.DynamicExtensionBaseDomainObject#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		boolean isEqual = false;
		if (obj instanceof FloatValue && (value!= null && value.equals(((FloatValue) obj).getValue())))
		{
			isEqual = true;
		}
		return isEqual;
	}
}
