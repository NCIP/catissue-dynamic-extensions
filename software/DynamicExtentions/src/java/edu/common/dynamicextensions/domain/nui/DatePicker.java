package edu.common.dynamicextensions.domain.nui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import edu.common.dynamicextensions.napi.ControlValue;
import edu.common.dynamicextensions.napi.impl.FormRenderer.ContextParameter;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.DHTMLXDateFormatHandler;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;

public class DatePicker extends Control {

	private static final long serialVersionUID = -5384931060284698579L;

	private static final String INPUT_TYPE_HIDDEN_ID_SCONTAINER_ID_VALUE_S = "<input type='hidden' id='%scontainerId' value='%s'/>";

	private static final String INPUT_TYPE_HIDDEN_ID_SCONTROL_ID_VALUE_S = "<input type='hidden' id='%scontrolId' value='%s'/>";

	private static final String INPUT_TYPE_HIDDEN_ID_SDATE_FORMAT_VALUE_S = "<input type='hidden' id='%sdateFormat' value='%s'/>";
	
	private static final String DEFAULT_FORMAT = "MM-dd-yyyy";
	
	public static enum DefaultDateType {
		PREDEFINED,
		CURRENT_DATE,
		NONE
	};

	private String format = DEFAULT_FORMAT;
	
	private boolean showCalendar = true;
	
	private Date defaultDate;
	
	private DefaultDateType defaultDateType = DefaultDateType.NONE;

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public boolean showCalendar() {
		return showCalendar;
	}

	public void setShowCalendar(boolean showCalendar) {
		this.showCalendar = showCalendar;
	}

	public Date getDefaultDate() {
		return defaultDate;
	}

	public void setDefaultDate(Date defaultDate) {
		this.defaultDate = defaultDate;
	}

	public DefaultDateType getDefaultDateType() {
		return defaultDateType;
	}

	public void setDefaultDateType(DefaultDateType defaultDateType) {
		this.defaultDateType = defaultDateType;
	}

	@Override
	public List<ColumnDef> getColumnDefs() {
		if(ProcessorConstants.SQL_DATE_TIME_FORMAT.equals(getFormat()))
		{
			return Collections.singletonList(ColumnDef.get(getDbColumnName(), "TIMESTAMP"));
		}
		else
		{
			return Collections.singletonList(ColumnDef.get(getDbColumnName(), "DATE"));
		}
	}
	
	
	@Override
	public DataType getDataType() {
		return DataType.DATE;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Date fromString(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		
		try {			
			String fmt = format;
			if (fmt == null) {
				fmt = DEFAULT_FORMAT;
			}
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fmt);
			simpleDateFormat.setLenient(false);
			return simpleDateFormat.parse(value);
		} catch (Exception e) {
			throw new RuntimeException("Error creating date object from [" + value + "]", e);
		}
	}
	
	@Override
	public String toString(Object value) {
		String dateValue=null;
		if (value == null) {
			return dateValue;
		}
		
		try {
			String fmt = format;
			if (fmt == null) {
				fmt = DEFAULT_FORMAT;
			}
			if(value instanceof oracle.sql.TIMESTAMP)
			{
				Date date = new Date(((oracle.sql.TIMESTAMP) value).timestampValue().getTime());
				SimpleDateFormat sdf = new SimpleDateFormat(ProcessorConstants.SQL_DATE_TIME_FORMAT);
				dateValue=sdf.format(date);
			}
			else
			{
				dateValue=new SimpleDateFormat(fmt).format((Date)value);
			}
			return dateValue;
		} catch (Exception e) {
			throw new RuntimeException("Error converting date to string: " + value, e);
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		result = prime * result + (showCalendar ? 1231 : 1237);
		result = prime * result	+ ((defaultDate == null) ? 0 : defaultDate.hashCode());
		result = prime * result	+ ((defaultDateType == null) ? 0 : defaultDateType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!super.equals(obj)) {
			return false;
		}
		
		DatePicker other = (DatePicker) obj;
		if (!StringUtils.equals(format, other.format) ||
			showCalendar != other.showCalendar ||
			(defaultDate == null && other.defaultDate != null) ||
			(defaultDate != null && !defaultDate.equals(other.defaultDate)) ||
			defaultDateType != other.defaultDateType) {
			return false;
		}

		return true;
	}

	@Override
	protected String render(String controlName, ControlValue controlValue, Map<ContextParameter, String> contextParameter)	{
		final String dateFormat = format;

		String dateString = getValueForControl(controlValue);
		if (controlValue.getValue() == null)
		{

			if (dateString != null && dateString.length() > 0
					&& dateString.indexOf('-') == 4)
			{
				dateString = reverseDate(dateString);
				Date date = null;
				try
				{
					if (dateFormat.equals(ProcessorConstants.DATE_TIME_FORMAT))
					{
						final SimpleDateFormat format = new SimpleDateFormat(
								ProcessorConstants.DATE_TIME_FORMAT, CommonServiceLocator
										.getInstance().getDefaultLocale());
						date = format.parse(dateString);
					}
					else
					{
						final SimpleDateFormat format = new SimpleDateFormat(
								ProcessorConstants.SQL_DATE_ONLY_FORMAT, CommonServiceLocator
										.getInstance().getDefaultLocale());
						date = format.parse(dateString);
					}
				}
				catch (ParseException e)
				{
					throw new RuntimeException("Error while parsing date", e);
				}
				dateString = new SimpleDateFormat(dateFormat, CommonServiceLocator.getInstance()
						.getDefaultLocale()).format(date);
			}
			if (dateString == null)
			{
				if (DefaultDateType.CURRENT_DATE == defaultDateType)
				{
					dateString = Utility.parseDateToString(new Date(), dateFormat);
				}
				else
				{
					dateString = "";
				}
			}
		}

		String formatSpecifier = new DHTMLXDateFormatHandler().getFormat(dateFormat);
		
		StringBuilder outputStringBuilder=new StringBuilder(26);

		outputStringBuilder.append("<input type='text' id='").append(controlName).append("' name='")
				.append(controlName);
		outputStringBuilder
				.append("' value='" + dateString + "' onchange=\"" + getOnchangeServerCall(controlName))
				.append("\" size='12'");
		if (controlValue.getErrorMessage() != null) {
			outputStringBuilder.append(" title='").append(controlValue.getErrorMessage()).append("' ")
					.append(" class='").append("font_bl_nor_error").append("' ");
		}
		String showCalendarCall = "showCalendar('%s')";

		outputStringBuilder.append("/>").append("<span class='date-format'>[").append(dateFormat).append("]</span>")
				.append(String.format(INPUT_TYPE_HIDDEN_ID_SDATE_FORMAT_VALUE_S, controlName, formatSpecifier))
				.append(String.format(INPUT_TYPE_HIDDEN_ID_SCONTROL_ID_VALUE_S, controlName, this.id))
				.append(String.format(INPUT_TYPE_HIDDEN_ID_SCONTAINER_ID_VALUE_S, controlName, getContainer().getId()))
				.append("<script>").append(String.format(showCalendarCall, controlName)).append(";</script>");

		return outputStringBuilder.toString();
	}
	
	private String getValueForControl(ControlValue controlValue)
	{
		String dateString = null;
		if(controlValue.getValue() != null)
		{
			dateString = (String) controlValue.getValue();
		}
		else if(defaultDate != null)
		{
			SimpleDateFormat sdFormatter = new SimpleDateFormat(format, CommonServiceLocator
					.getInstance().getDefaultLocale());
			dateString =  sdFormatter.format(defaultDate);
		}
		return dateString;
	}

	/**
	 * @param defaultValue
	 * @return defaultValue
	 */
	private String reverseDate(final String defaultValue) {
		// Date is like 1900-01-01 00:00:00.0 for MySQL5
		String date = defaultValue.substring(0, 10); // 1900-01-01
		final String time = defaultValue.substring(10, defaultValue.length()); // 00:00:00.0

		final String year = date.substring(0, 4); // 1900
		final String month = date.substring(5, 7); // 01
		final String day = date.substring(8, date.length()); // 01
		StringBuilder dateString = new StringBuilder().append(defaultValue.substring(0, 10)).append(month).append('-')
				.append(day).append('-').append(year);
		return (dateString.toString() + time);
	}

	@Override
	public List<String> validate(ControlValue controlValue) {

		List<String> errorList = new ArrayList<String>();
	
		try {
			fromString((String) controlValue.getValue());
		} catch (Exception exception) {
			errorList.add(ApplicationProperties.getValue("dynExtn.validation.Date",
					Arrays.asList(getCaption(), getFormat())));
		}
		return errorList;
	}
	
	public List<String> getConditions() {
		List<String> conditions = new ArrayList<String>();
		conditions.add(DEConstants.EQUALS);
		conditions.add(DEConstants.NOT_EQUALS);
		conditions.add(DEConstants.BETWEEN);
		conditions.add(DEConstants.LESS_THAN);
		conditions.add(DEConstants.LESS_THAN_OR_EQUAL_TO);
		conditions.add(DEConstants.GREATER_THAN);
		conditions.add(DEConstants.GREATER_THAN_OR_EQUAL_TO);
		conditions.add(DEConstants.IS_PRESENT);
		conditions.add(DEConstants.IS_NOT_PRESENT);
		return conditions;
	}
}
