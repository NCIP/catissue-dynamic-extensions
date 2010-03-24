
package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.BooleanValueInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;

/**
 * @hibernate.joined-subclass table="DYEXTN_BOOLEAN_CONCEPT_VALUE"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 * @author sujay_narkar
 *
 */
public class BooleanValue extends PermissibleValue implements BooleanValueInterface
{

	/**
	 * Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = 6775534423957386289L;

	/**
	 * The value to be stored.
	 */
	protected Boolean value;

	/**
	 * This method returns the value of the BooleanValue.
	 * @hibernate.property name="value" type="boolean" column="VALUE"
	 * @return the value of the BooleanValue.
	 */
	public Boolean getValue()
	{
		return value;
	}

	/**
	 * This method sets the value of the BooleanValue.
	 * @param value the value to be set.
	 */
	public void setValue(Boolean value)
	{
		this.value = value;
	}

	/**
	 * This method returns the value of BooleanValue downcasted to the Object.
	 * @return the value of BooleanValue downcasted to the Object.
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
		BooleanValueInterface booleanValueInterface = DomainObjectFactory.getInstance()
				.createBooleanValue();
		booleanValueInterface.setValue(value);
		return booleanValueInterface;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domain.DynamicExtensionBaseDomainObject#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		boolean isEqual = false;
		if (obj instanceof BooleanValue
				&& (value != null && value.equals(((BooleanValue) obj).getValue())))
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
