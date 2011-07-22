
package edu.common.dynamicextensions.domain;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.NumericTypeInformationInterface;

/**
 * @author Rahul Ner
 * @hibernate.joined-subclass table="DYEXTN_NUMERIC_TYPE_INFO" 
 * @hibernate.joined-subclass-key column="IDENTIFIER"  
 */
public abstract class NumericAttributeTypeInformation extends AttributeTypeInformation
		implements
			NumericTypeInformationInterface
{

	/**
	 * Serial Version Unique Identifier
	 */
	protected static final long serialVersionUID = -842600137818204271L;

	/**
	 * 
	 */
	protected String measurementUnits;

	/**
	 * Number of digits
	 */
	protected Integer digits;

	/**
	 * Number of decimal places
	 */
	protected Integer decimalPlaces = 0;

	/**
	 * Empty Constructor.
	 */
	protected NumericAttributeTypeInformation()
	{
		super();
	}

	/**
	 * This method returns the measurement units of this Attribute.
	 * @hibernate.property name="measurementUnits" type="string" column="MEASUREMENT_UNITS"  
	 * @return the measurement units of this Attribute.
	 */
	public String getMeasurementUnits()

	{
		return this.measurementUnits;
	}

	/**
	 * This method sets the measurement units of this Attribute.
	 * @param measurementUnits The measurementUnits to set.
	 */
	public void setMeasurementUnits(String measurementUnits)
	{
		this.measurementUnits = measurementUnits;
	}

	/**
	 * @hibernate.property name="decimalPlaces" type="int" column="DECIMAL_PLACES"  
	 */
	public Integer getDecimalPlaces()
	{
		return this.decimalPlaces;
	}

	/**
	 * This method sets the places after the decimal point of the DoubleAttribute.
	 * @param decimalPlaces the places after the decimal point to be set.
	 */
	public void setDecimalPlaces(Integer decimalPlaces)
	{
		this.decimalPlaces = decimalPlaces;
	}

	/**
	 * This method returns the length of the number in digits.
	 * @hibernate.property name="digits" type="int" column="NO_DIGITS" 
	 * @return the length of the number in digits. 
	 */
	public Integer getDigits()
	{
		return this.digits;
	}

	/**
	 * This method sets the length of the number in digits.
	 * @param digits the length of the number in digits.
	 */
	public void setDigits(Integer digits)
	{
		this.digits = digits;
	}
	
	/**
	 *
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	public abstract String getFormattedValue(Double value)
			throws ParseException;
	
	public List<String> getConditions() {
		List<String> conditions = new ArrayList<String>();
		conditions.add("Equals");
		conditions.add("Not Equals");
		conditions.add("Between");
		conditions.add("Not Between");
		conditions.add("Less than");
		conditions.add("Less than or Equal to");
		conditions.add("Greater than");
		conditions.add("Greater than or Equal to");
		conditions.add("Is Present");
		conditions.add("Is Not Present");
		return conditions;
	}
}
