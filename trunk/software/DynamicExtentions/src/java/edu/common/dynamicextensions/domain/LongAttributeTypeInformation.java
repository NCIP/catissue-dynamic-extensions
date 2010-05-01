
package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.LongTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.LongValueInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerConstantsInterface;

/**
 * @hibernate.joined-subclass table="DYEXTN_LONG_TYPE_INFO" 
 * @hibernate.joined-subclass-key column="IDENTIFIER"  
 * @author sujay_narkar
 *
 */
public class LongAttributeTypeInformation extends NumericAttributeTypeInformation
		implements
			LongTypeInformationInterface
{

	/** 
	 * @see edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface#getDataType()
	 */
	public String getDataType()
	{
		return EntityManagerConstantsInterface.LONG_ATTRIBUTE_TYPE;
	}

	/**
	 * 
	 */
	public PermissibleValueInterface getPermissibleValueForString(String value)
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		LongValueInterface longValue = factory.createLongValue();
		longValue.setValue(Long.valueOf(value));

		return longValue;
	}
	/**
	 * 
	 */
	public String getFormattedValue(Double value)
	{
		return Long.valueOf(value.longValue()).toString();
	}
}
