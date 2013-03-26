
package edu.common.dynamicextensions.domain.userinterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.common.dynamicextensions.category.beans.UIProperty;
import edu.common.dynamicextensions.category.enums.DatePickerEnum;
import edu.common.dynamicextensions.domain.CategoryAttribute;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.DateTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.DatePickerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.SummaryControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ValidatableInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.DHTMLXDateFormatHandler;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.CommonServiceLocator;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:07 PM
 * @hibernate.joined-subclass table="DYEXTN_DATEPICKER"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class DatePicker extends Control implements DatePickerInterface, ValidatableInterface,SummaryControlInterface
{

	private static final String INPUT_TYPE_HIDDEN_ID_SCONTAINER_ID_VALUE_S = "<input type='hidden' id='%scontainerId' value='%s'/>";

	private static final String INPUT_TYPE_HIDDEN_ID_SCONTROL_ID_VALUE_S = "<input type='hidden' id='%scontrolId' value='%s'/>";

	private static final String INPUT_TYPE_HIDDEN_ID_SDATE_FORMAT_VALUE_S = "<input type='hidden' id='%sdateFormat' value='%s'/>";

	/** default serial UID. */
	private static final long serialVersionUID = 1L;

	/** The date value type. */
	private String dateValueType = null;

	/** The Constant DATE_FORMAT_STRING. */
	private final static String DATE_FORMAT = "Date format : ";

	private String INPUT_CLASS;

	private final static String DARKGRAY="#A9A9A9;\"";

	private final static String BLACK="black;\"";
	/**
	 * Show Calendar icon on UI
	 */
	private Boolean showCalendar = true;

	public DatePicker()
	{
		INPUT_CLASS="<input class='"+getCSS()+"' name='";
	}
	/**
	 * This method returns the showCalendar of the DatePicker.
	 *
	 * @hibernate.property name="showCalendar" type="boolean"
	 *                     column="SHOWCALENDAR"
	 * @return Returns the showCalendar.
	 */
	public Boolean getShowCalendar()
	{
		return showCalendar;
	}

	/**
	 * @param showCalendar the showCalendar to set
	 */
	public void setShowCalendar(final Boolean showCalendar)
	{
		this.showCalendar = showCalendar;
	}

	/**
	 * This method generates the HTML code for DatePicker control on the HTML
	 * form
	 *
	 * @return HTML code for DatePicker
	 * @throws DynamicExtensionsSystemException
	 *             if couldn't generate the HTML name for the Control.
	 */
	@Override
    protected String generateEditModeHTML(final ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		final AttributeTypeInformationInterface attributeTypeInformation = ((AttributeMetadataInterface) getBaseAbstractAttribute())
				.getAttributeTypeInformation();
		final String dateFormat = ControlsUtility.getDateFormat(attributeTypeInformation);

		String defaultValue = getDefaultValueForControl();
		if (value == null)
		{

			if (defaultValue != null && defaultValue.length() > 0
					&& getAttibuteMetadataInterface() instanceof CategoryAttribute
					&& defaultValue.indexOf('-') == 4)
			{
				final CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface) getAttibuteMetadataInterface();
				final DateTypeInformationInterface dateTypeInformation = (DateTypeInformationInterface) ((AttributeInterface) categoryAttribute
						.getAbstractAttribute()).getAttributeTypeInformation();
				defaultValue = reverseDate(defaultValue);
				Date date = null;
				try
				{
					if (dateFormat.equals(ProcessorConstants.DATE_TIME_FORMAT))
					{
						final SimpleDateFormat format = new SimpleDateFormat(
								ProcessorConstants.DATE_TIME_FORMAT, CommonServiceLocator
										.getInstance().getDefaultLocale());
						date = format.parse(defaultValue);
					}
					else
					{
						final SimpleDateFormat format = new SimpleDateFormat(
								ProcessorConstants.SQL_DATE_ONLY_FORMAT, CommonServiceLocator
										.getInstance().getDefaultLocale());
						date = format.parse(defaultValue);
					}
				}
				catch (ParseException e)
				{
					throw new DynamicExtensionsSystemException("Error while parsing date", e);
				}
				defaultValue = new SimpleDateFormat(ControlsUtility
						.getDateFormat(dateTypeInformation), CommonServiceLocator.getInstance()
						.getDefaultLocale()).format(date);
			}
			if (defaultValue == null)
			{
				if (getDateValueType() != null
						&& getDateValueType().equals(ProcessorConstants.DATE_VALUE_TODAY))
				{
					defaultValue = Utility.parseDateToString(new Date(), dateFormat);
				}
				else
				{
					defaultValue = "";
				}
			}
		}

		final String htmlComponentName = getHTMLComponentName();
		String formatSpecifier = new DHTMLXDateFormatHandler().getFormat(dateFormat);
		
		StringBuffer outputStringBuffer=new StringBuffer(26);

		if (getIsSkipLogicTargetControl())
		{
			outputStringBuffer.append("<div id='");
			outputStringBuffer.append(getHTMLComponentName());
			outputStringBuffer.append("_div' name='");
			outputStringBuffer.append(getHTMLComponentName());
			outputStringBuffer.append("_div'>");
		}
		outputStringBuffer.append("<input type='text' id='");
		outputStringBuffer.append(getHTMLComponentName());
		outputStringBuffer.append("' name='");
		outputStringBuffer.append(getHTMLComponentName());
		
		outputStringBuffer.append("' value='"+defaultValue+"' class='de-date-picker' onchange=\"" +getOnchangeServerCall()
				+"\" size='10'/>");
		outputStringBuffer.append("<span class='date-format'>["+dateFormat+"]</span>");
		
		outputStringBuffer.append(String.format(INPUT_TYPE_HIDDEN_ID_SDATE_FORMAT_VALUE_S,getHTMLComponentName(), formatSpecifier));
		outputStringBuffer.append(String.format(INPUT_TYPE_HIDDEN_ID_SCONTROL_ID_VALUE_S,getHTMLComponentName(), this.id));
		outputStringBuffer.append(String.format(INPUT_TYPE_HIDDEN_ID_SCONTAINER_ID_VALUE_S,getHTMLComponentName(), getParentContainer().getId()));
		String showCalendarCall = "showCalendar('%s')";
		outputStringBuffer.append("<script>"+ String.format(showCalendarCall, htmlComponentName)+";</script>" );
		
		
		if (getIsSkipLogicTargetControl())
		{
			outputStringBuffer.append("<input type='hidden' name='skipLogicControl' id='skipLogicControl' value = '");
			outputStringBuffer.append(getHTMLComponentName());
			outputStringBuffer.append("_div' /></div>");
		}
		return outputStringBuffer.toString();
	}
	

	/**
	 * @param defaultValue
	 * @return defaultValue
	 */
	private String reverseDate(final String defaultValue)
	{
		// Date is like 1900-01-01 00:00:00.0 for MySQL5
		String date = defaultValue.substring(0, 10); // 1900-01-01
		final String time = defaultValue.substring(10, defaultValue.length()); // 00:00:00.0

		final String year = date.substring(0, 4); // 1900
		final String month = date.substring(5, 7); // 01
		final String day = date.substring(8, date.length()); // 01
		StringBuffer dateStringBuffer=new StringBuffer();
		dateStringBuffer.append(defaultValue.substring(0, 10));
		dateStringBuffer.append(month );
		dateStringBuffer.append('-');
		dateStringBuffer.append(day);
		dateStringBuffer.append('-');
		dateStringBuffer.append(year);
		return (date + time);
	}

	/**
	 * @param attributeInterface
	 *            attribute type object
	 */
	public void setAttribute(final AbstractAttributeInterface attributeInterface)
	{
		// TODO empty method.
	}

	/**
	 * This method returns the dateValueType of the DatePicker.
	 *
	 * @hibernate.property name="dateValueType" type="string"
	 *                     column="DATE_VALUE_TYPE"
	 * @return Returns the dateValueType.
	 */
	public String getDateValueType()
	{
		return dateValueType;
	}

	/**
	 * @param dateValueType
	 *            set the date type value
	 */
	public void setDateValueType(final String dateValueType)
	{
		this.dateValueType = dateValueType;
	}

	/**
	 * Generate HTML for viewing data
	 * @param rowId
	 * @return htmlString for view mode
	 */
	@Override
    protected String generateViewModeHTML(final ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		final StringBuffer htmlString = new StringBuffer();
		if (value != null)
		{
			htmlString.append("<span class = 'font_bl_nor'> ");
			htmlString.append(value.toString());
			htmlString.append("</span>");
		}

		return htmlString.toString();
	}

	/**
	 * get value for a control
	 * @param rowId
	 * @return value for a control
	 */
	@Override
    public List<String> getValueAsStrings()
	{
		final List<String> values = new ArrayList<String>();
		values.add(getDefaultValueForControl());
		return values;
	}

	/**
	 * set value as string for a control
	 * @param listOfValues list of permissible value
	 */
	@Override
    public void setValueAsStrings(final List<String> listOfValues)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * Gets the default value for control.
	 * @return the default value for control
	 */
	private String getDefaultValueForControl()
	{
		String defaultValue;
		if (value == null || value.toString().length() == 0)
		{
			if (isSkipLogicDefaultValue())
			{
				defaultValue = getDefaultSkipLogicValue();
			}
			else
			{
				defaultValue = getAttibuteMetadataInterface().getDefaultValue(null);
				if (defaultValue == null || defaultValue.length() == 0)
				{
					defaultValue = "";
				}
			}
		}
		else
		{
			defaultValue = value.toString();
		}
		return defaultValue;
	}

	/**
	 * Gets the default skip logic value.
	 * @return the default skip logic value
	 */
	private String getDefaultSkipLogicValue()
	{
		final CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface) getAttibuteMetadataInterface();
		final Object defaultValue = categoryAttribute.getDefaultSkipLogicValue().getValueAsObject();
		return defaultValue.toString();

	}

	/**
	 * Checks if is skip logic default value.
	 * @return true, if is skip logic default value
	 */
	private boolean isSkipLogicDefaultValue()
	{
		return ((CategoryAttributeInterface) getAttibuteMetadataInterface())
				.getDefaultSkipLogicValue() != null;
	}

	/**
	 *
	 */
	@Override
    public boolean getIsEnumeratedControl()
	{
		return false;
	}

	/**
	 * Returns collection of key-value pairs.
	 */
	@Override
    public Collection<UIProperty> getControlTypeValues()
	{
		final Collection<UIProperty> uiProperties = super.getControlTypeValues();
		final DatePickerEnum[] uiPropertyValues = DatePickerEnum.values();

		for (DatePickerEnum propertyType : uiPropertyValues)
		{
			final String controlProperty = propertyType.getControlProperty(this);
			if (controlProperty != null)
			{
				uiProperties.add(new UIProperty(propertyType.getValue(), controlProperty));
			}
		}

		return uiProperties;
	}

}