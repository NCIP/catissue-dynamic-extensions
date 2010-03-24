
package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.IntegerValueInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;

/**
 * @author sujay_narkar
 * @hibernate.joined-subclass table="DYEXTN_INTEGER_CONCEPT_VALUE"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class IntegerValue extends PermissibleValue implements IntegerValueInterface
{

	/**
	 * Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = -1598647162647981710L;

	/**
	 * The predefined Integer value.
	 */
	protected Integer value;

	/**
	 * This method returns the predefined value of IntegerValue.
	 * @hibernate.property name="value" type="integer" column="VALUE"
	 * @return the predefined value of IntegerValue.
	 */
	public Integer getValue()
	{
		return value;
	}

	/**
	 * This method sets the value of IntegerValue to the given value.
	 * @param value the value to be set.
	 */
	public void setValue(Integer value)
	{
		this.value = value;
	}

	/**
	 * This method returns the value of DateValue downcasted to the Object.
	 * @return the value of the DateValue downcasted to the Object.
	 */
	public Object getValueAsObject()
	{
		return value;
	}

	/**
	 *
	 */
	public PermissibleValueInterface getObjectCopy()
	{
		IntegerValueInterface integerValueInterface = DomainObjectFactory.getInstance()
				.createIntegerValue();
		integerValueInterface.setValue(value);
		return integerValueInterface;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domain.DynamicExtensionBaseDomainObject#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		boolean isEqual = false;
		if (obj instanceof IntegerValue
				&& (value != null && value.equals(((IntegerValue) obj).getValue())))
		{
			isEqual = true;
		}
		return isEqual;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domain.DynamicExtensionBaseDomainObject#hashCode()
	 */
	public int hashCode()
	{
		return super.hashCode();
	}
}
