
package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.LongValueInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;

/**
 * @author sujay_narkar
 * @hibernate.joined-subclass table="DYEXTN_LONG_CONCEPT_VALUE"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class LongValue extends PermissibleValue implements LongValueInterface
{

	/**
	 * Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = -3038941708523130752L;

	/**
	 * The predefined Long value.
	 */
	protected Long value;

	/**
	 * This method returns the predefined value of LongValue.
	 * @hibernate.property name="value" type="long" column="VALUE"
	 * @return the predefined value of LongValue.
	 */
	public Long getValue()
	{
		return value;
	}

	/**
	 * This method sets the value of LongValue to the given value.
	 * @param value the value to be set.
	 */
	public void setValue(Long value)
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
		LongValueInterface longValueInterface = DomainObjectFactory.getInstance().createLongValue();
		longValueInterface.setValue(value);
		return longValueInterface;
	}

	public boolean equals(Object obj)
	{
		boolean isEqual = false;
		if (obj instanceof LongValue
				&& (value != null && value.equals(((LongValue) obj).getValue())))
		{
			isEqual = true;

		}
		return isEqual;
	}
}
