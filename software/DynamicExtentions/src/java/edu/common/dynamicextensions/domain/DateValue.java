
package edu.common.dynamicextensions.domain;

import java.sql.Timestamp;
import java.util.Date;

import edu.common.dynamicextensions.domaininterface.DateValueInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;

/**
 * This Class represents the per-defined Date value of the Attribute.
 * @author sujay_narkar
 * @hibernate.joined-subclass table="DYEXTN_DATE_CONCEPT_VALUE"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class DateValue extends PermissibleValue implements DateValueInterface
{

	/**
	 * Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = 3381430917859885601L;

	/**
	 * The predefined Date value.
	 */
	protected Date value;

	/**
	 * This method returns the predefined value of DateValue.
	 * @hibernate.property name="value" type="timestamp" column="VALUE"
	 * @return the predefined value of DateValue.
	 */
	public Date getValue()
	{
		Date defaultDate = null;
		if (value != null)
		{
			defaultDate = new Timestamp(value.getTime());
		}
		return defaultDate;
	}

	/**
	 * This method sets the value of DateValue to the given value.
	 * @param value the value to be set.
	 */
	public void setValue(Date value)
	{
		this.value = value;
	}

	/**
	 * This method returns the value of DateValue downcasted to the Object.
	 * @return the value of the DateValue downcasted to the Object.
	 */
	public Object getValueAsObject()
	{
		Date defaultDate = null;
		if (value != null)
		{
			defaultDate = new Timestamp(value.getTime());
		}
		return defaultDate;
	}

	/**
	 *
	 */
	public PermissibleValueInterface getObjectCopy()
	{
		DateValueInterface dateValueInterface = DomainObjectFactory.getInstance().createDateValue();
		dateValueInterface.setValue(value);
		return dateValueInterface;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domain.DynamicExtensionBaseDomainObject#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		boolean isEqual = false;
		if (obj instanceof DateValue
				&& (value != null && getValue().equals(((DateValue) obj).getValue())))
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
