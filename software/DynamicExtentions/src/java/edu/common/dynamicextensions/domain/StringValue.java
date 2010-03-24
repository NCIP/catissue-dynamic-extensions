
package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.StringValueInterface;

/**
 * @author sujay_narkar
 * @hibernate.joined-subclass table="DYEXTN_STRING_CONCEPT_VALUE"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class StringValue extends PermissibleValue implements StringValueInterface
{

	/**
	 * Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = 6552718005216538542L;

	/**
	 * The predefined String value.
	 */
	protected String value;

	/**
	 * This method returns the predefined value of StringValue.
	 * @hibernate.property name="value" type="string" column="VALUE"
	 * @return the predefined value of StringValue.
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * This method sets the value of DateValue to the given value.
	 * @param value the value to be set.
	 */
	public void setValue(String value)
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
		StringValueInterface stringValue = DomainObjectFactory.getInstance().createStringValue();
		stringValue.setValue(value);
		return stringValue;
	}

	/**
	 * This method overrides the equals method of the parent Class.
	 * This method checks the equality of the value .
	 * @return boolean true if the both values objects are equal otherwise false.
	 */
	public boolean equals(Object obj)
	{
		boolean isEqual = false;
		if (obj instanceof StringValue
				&& (value != null && value.equals(((StringValue) obj).getValue())))
		{
			isEqual = true;
		}
		return isEqual;
	}

	/**
	 * This method overrides the equals method of the Object Class.
	 * It returns the HashCode of this instance.
	 * @return int The HashCode of this instance.
	 */
	public int hashCode()
	{
		return super.hashCode();
	}
}
