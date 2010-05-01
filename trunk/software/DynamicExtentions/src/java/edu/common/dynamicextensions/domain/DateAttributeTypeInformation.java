
package edu.common.dynamicextensions.domain;

import java.text.ParseException;
import java.util.Date;

import edu.common.dynamicextensions.domaininterface.DateTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.DateValueInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerConstantsInterface;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.util.Utility;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:07 PM
 * @hibernate.joined-subclass table="DYEXTN_DATE_TYPE_INFO"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class DateAttributeTypeInformation extends AttributeTypeInformation
		implements
			DateTypeInformationInterface
{

	/**
	 * Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = 5655678242696814276L;

	/**
	 * format of the attribute value (Data entry/display)
	 */
	protected String format;

	/**This method returns the format of the DateAttributeTypeInformation.
	 * @hibernate.property name="format" type="string" column="FORMAT"
	 * @return Returns the format.
	 */
	public String getFormat()
	{
		return format;
	}

	/**
	 * @param format The format to set.
	 */
	public void setFormat(String format)
	{
		this.format = format;
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface#getDataType()
	 */
	public String getDataType()
	{

		return EntityManagerConstantsInterface.DATE_ATTRIBUTE_TYPE;
	}

	/**
	 * 
	 */
	public PermissibleValueInterface getPermissibleValueForString(String value)
			throws ParseException
	{
		String permissibleValue = value.replace("/", "-");

		if (permissibleValue.length() != format.length())
		{
			throw new ParseException("DATE VALUE " + permissibleValue + " INVALID FOR " + format
					+ " DATE FORMAT.", 0);
		}

		if (permissibleValue.endsWith("0000") || permissibleValue.contains("."))
		{
			throw new ParseException("DATE VALUE " + permissibleValue + " INVALID FOR " + format
					+ " DATE FORMAT.", 0);
		}

		DateValueInterface dateValue = DomainObjectFactory.getInstance().createDateValue();

		if (format.equals(ProcessorConstants.MONTH_YEAR_FORMAT))
		{
			Utility.parseDate(permissibleValue, format);
			permissibleValue = DynamicExtensionsUtility.formatMonthAndYearDate(permissibleValue,false);
		}
		if (format.equals(ProcessorConstants.YEAR_ONLY_FORMAT))
		{
			Utility.parseDate(permissibleValue, format);
			permissibleValue = DynamicExtensionsUtility.formatYearDate(permissibleValue,false);
		}

		Date date = null;
		if (format.equals(ProcessorConstants.DATE_TIME_FORMAT))
		{
			date = Utility.parseDate(permissibleValue, ProcessorConstants.DATE_TIME_FORMAT);
		}
		else
		{
			date = Utility.parseDate(permissibleValue, ProcessorConstants.SQL_DATE_ONLY_FORMAT);
		}

		dateValue.setValue(date);

		return dateValue;
	}

}