
package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.ShortTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.ShortValueInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerConstantsInterface;

/**
 * @hibernate.joined-subclass table="DYEXTN_SHORT_TYPE_INFO"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 * @author sujay_narkar
 *
 */
public class ShortAttributeTypeInformation extends NumericAttributeTypeInformation
		implements
			ShortTypeInformationInterface
{

	/**
	 * @see edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface#getDataType()
	 */
	public String getDataType()
	{

		return EntityManagerConstantsInterface.SHORT_ATTRIBUTE_TYPE;
	}

	/**
	 *
	 */
	public PermissibleValueInterface getPermissibleValueForString(String value)
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		ShortValueInterface shortValue = factory.createShortValue();
		shortValue.setValue(Short.valueOf(value));

		return shortValue;
	}

	/**
	 *
	 */
	public String getFormattedValue(Double value)
	{
		return Short.valueOf(value.shortValue()).toString();
	}

	/**
	 * (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface#getAttributeDataType()
	 * @return Class type for attribute.
	 */
	public Class getAttributeDataType()
	{
		return Short.class;
	}

	public String getDefaultValueAsString()
	{
		String defaultValue = null;
		ShortValueInterface shortValue = (ShortValueInterface) getDefaultValue();
		if (shortValue != null)
		{
			Short defaultShort = shortValue.getValue();
			if (defaultShort != null)
			{
				defaultValue = defaultShort.toString();
			}
		}
		return defaultValue;
	}
}
