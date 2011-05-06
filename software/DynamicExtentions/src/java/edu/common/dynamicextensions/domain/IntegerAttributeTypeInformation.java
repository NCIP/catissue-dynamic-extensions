
package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.IntegerTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.IntegerValueInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerConstantsInterface;

/**
 * @author sujay_narkar
 * @hibernate.joined-subclass table="DYEXTN_INTEGER_TYPE_INFO"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 *
 */
public class IntegerAttributeTypeInformation extends NumericAttributeTypeInformation
		implements
			IntegerTypeInformationInterface
{

	/**
	 * @see edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface#getDataType()
	 */
	public String getDataType()
	{
		return EntityManagerConstantsInterface.INTEGER_ATTRIBUTE_TYPE;
	}

	/**
	 *
	 */
	public PermissibleValueInterface getPermissibleValueForString(String value)
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		IntegerValueInterface integerValue = factory.createIntegerValue();
		integerValue.setValue(Integer.valueOf(value));

		return integerValue;
	}
	/**
	 *
	 */
	public String getFormattedValue(Double value)
	{
		return Integer.valueOf(value.intValue()).toString();
	}

	/**
	 * (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface#getAttributeDataType()
	 * @return Class type for attribute.
	 */
	public Class getAttributeDataType()
	{
		return Integer.class;
	}

}
