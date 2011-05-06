
package edu.common.dynamicextensions.domain;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.common.dynamicextensions.domaininterface.FloatTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.FloatValueInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerConstantsInterface;

/**
 * This Class represent the Floating value Attribute of the Entity.
 * @hibernate.joined-subclass table="DYEXTN_FLOAT_TYPE_INFO"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 * @author sujay_narkar
 */
public class FloatAttributeTypeInformation extends NumericAttributeTypeInformation
		implements
			FloatTypeInformationInterface
{
	/**
	 * @see edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface#getDataType()
	 */
	public String getDataType()
	{

		return EntityManagerConstantsInterface.FLOAT_ATTRIBUTE_TYPE;
	}

	/**
	 *
	 */
	public PermissibleValueInterface getPermissibleValueForString(String value)
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		FloatValueInterface floatValue = factory.createFloatValue();
		floatValue.setValue(new Float(value));

		return floatValue;
	}
	/**
	 *
	 */
	public String getFormattedValue(Double value)
	{
		String formattedValue = "";
		if (value != null)
		{
			if (decimalPlaces.intValue() > 0)
			{
				DecimalFormat formatDecimal = (DecimalFormat) NumberFormat
						.getNumberInstance();
				formatDecimal.setParseBigDecimal(true);
				formatDecimal.setGroupingUsed(false);
				formatDecimal.setMaximumFractionDigits(decimalPlaces
						.intValue());
				formattedValue = formatDecimal.format(Float.valueOf(value
						.floatValue()));
			}
			else
			{
				formattedValue = Float.valueOf(value.floatValue()).toString();
			}
		}
		return formattedValue;
	}

	/**
	 * (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface#getAttributeDataType()
	 * @return Class type for attribute.
	 */
	public Class getAttributeDataType()
	{
		// TODO Auto-generated method stub
		return Float.class;
	}
}
