
package edu.common.dynamicextensions.domain.userinterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.common.dynamicextensions.domain.CategoryAttribute;
import edu.common.dynamicextensions.domain.userinterface.beans.UIProperty;
import edu.common.dynamicextensions.domain.userinterface.enums.DatePickerEnum;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.DateTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.DatePickerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.CommonServiceLocator;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:07 PM
 * @hibernate.joined-subclass table="DYEXTN_DATEPICKER"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class DatePicker extends Control implements DatePickerInterface
{

	/**
	 * default serial UID
	 */
	private static final long serialVersionUID = 1L;
	private String dateValueType = null;
	private final String dateFormatString = "Date format : ";

	/**
	 * Show Calendar icon on UI
	 */
	private Boolean showCalendar = true;

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
	public void setShowCalendar(Boolean showCalendar)
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
	protected String generateEditModeHTML(ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		AttributeTypeInformationInterface attributeTypeInformation = ((AttributeMetadataInterface) getBaseAbstractAttribute())
				.getAttributeTypeInformation();
		String dateFormat = ControlsUtility.getDateFormat(attributeTypeInformation);

		String defaultValue = getDefaultValueForControl();
		if (value == null)
		{

			if (defaultValue != null && defaultValue.length() > 0
					&& getAttibuteMetadataInterface() != null
					&& getAttibuteMetadataInterface() instanceof CategoryAttribute)
			{
				CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface) getAttibuteMetadataInterface();
				DateTypeInformationInterface dateTypeInformation = (DateTypeInformationInterface) ((AttributeInterface) categoryAttribute
						.getAbstractAttribute()).getAttributeTypeInformation();
				defaultValue = reverseDate(defaultValue);
				Date date = null;
				try
				{
					if (dateFormat.equals(ProcessorConstants.DATE_TIME_FORMAT))
					{
						SimpleDateFormat format = new SimpleDateFormat(
								ProcessorConstants.DATE_TIME_FORMAT, CommonServiceLocator
										.getInstance().getDefaultLocale());
						date = format.parse(defaultValue);
					}
					else
					{
						SimpleDateFormat format = new SimpleDateFormat(
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

		String htmlComponentName = getHTMLComponentName();
		String output = "";
		if (getIsSkipLogicTargetControl())
		{
			output += "<div id='" + getHTMLComponentName() + "_div' name='"
					+ getHTMLComponentName() + "_div'>";
		}
		if (dateFormat.equals(ProcessorConstants.DATE_ONLY_FORMAT))
		{
			output += "<input class='font_bl_nor' name='"
					+ htmlComponentName
					+ "' id='"
					+ htmlComponentName
					+ "' title='Calendar ["
					+ ProcessorConstants.DATE_ONLY_FORMAT
					+ "]' value='"
					+ ((defaultValue != null && defaultValue.trim().length() > 0)
							? defaultValue
							: ProcessorConstants.DATE_ONLY_FORMAT)
					+ "'"
					+ (((isReadOnly != null && isReadOnly) || (isSkipLogicReadOnly != null && isSkipLogicReadOnly))
							? " disabled='" + ProcessorConstants.TRUE
							: "")
					+ " onchange='isDataChanged();' onfocus=\"javascript:clearDate('"
					+ htmlComponentName
					+ "','"
					+ ProcessorConstants.DATE_ONLY_FORMAT
					+ "');\""
					+ " style=\"color:"
					+ ((defaultValue != null && defaultValue.trim().length() > 0)
							? "black;\""
							: "#A9A9A9;\"") + "/>";
			if (getShowCalendar())
			{
				output += "<A onclick=\"showCalendar('"
						+ htmlComponentName
						+ "', "
						+ DynamicExtensionsUtility.getCurrentYear()
						+ ", "
						+ DynamicExtensionsUtility.getCurrentMonth()
						+ ", "
						+ DynamicExtensionsUtility.getCurrentDay()
						+ ", '"
						+ ProcessorConstants.DATE_ONLY_FORMAT
						+ "', 'dataEntryForm', '"
						+ htmlComponentName
						+ "', event, 1900, 2020);\" href=\"javascript://\">"
						+ "&nbsp;<IMG alt=\""
						+ dateFormatString
						+ " ["
						+ ProcessorConstants.DATE_ONLY_FORMAT
						+ "]\" src=\"images/de/calendar.gif\" border=0 /></A>"
						+ "<DIV id=slcalcod"
						+ htmlComponentName
						+ " style=\"Z-INDEX: 10; LEFT: 100px; VISIBILITY: hidden; POSITION: absolute; TOP: 100px\">";
				if (getParentContainer().isAjaxRequest())
				{
					String imgsrc = getParentContainer().getRequest().getContextPath()
							+ "/images/de/";
					String filePath = getParentContainer().getRequest().getSession()
							.getServletContext()
							.getRealPath("/javascripts/de/CalendarComponent.js");
					String jsFunctionName = DEConstants.PROCESS_AJAX_CAL_SCRIPT;
					Object[] jsFunctionParameters = {htmlComponentName,
							DynamicExtensionsUtility.getCurrentDay(),
							DynamicExtensionsUtility.getCurrentMonth(),
							DynamicExtensionsUtility.getCurrentYear(), imgsrc};
					String jsOutput = DynamicExtensionsUtility.executeJavaScriptFunc(filePath,
							jsFunctionName, jsFunctionParameters);
					output += jsOutput;
				}
				else
				{
					output += "<SCRIPT>printCalendar('" + htmlComponentName + "',"
							+ DynamicExtensionsUtility.getCurrentDay() + ","
							+ DynamicExtensionsUtility.getCurrentMonth() + ","
							+ DynamicExtensionsUtility.getCurrentYear() + ");</SCRIPT>";
				}
				output += "</DIV>";
			}
		}
		else if (dateFormat.equals(ProcessorConstants.DATE_TIME_FORMAT))
		{
			output += "<input class='font_bl_nor' name='"
					+ htmlComponentName
					+ "' id='"
					+ htmlComponentName
					+ "' title='Calendar ["
					+ ProcessorConstants.DATE_TIME_FORMAT
					+ "]' value='"
					+ ((defaultValue != null && defaultValue.trim().length() > 0)
							? defaultValue
							: ProcessorConstants.DATE_TIME_FORMAT)
					+ "'"
					+ (((isReadOnly != null && isReadOnly) || (isSkipLogicReadOnly != null && isSkipLogicReadOnly))
							? " disabled='" + ProcessorConstants.TRUE
							: "")
					+ " onchange='isDataChanged();' onfocus=\"javascript:clearDate('"
					+ htmlComponentName
					+ "','"
					+ ProcessorConstants.DATE_TIME_FORMAT
					+ "');\""
					+ " style=\"color:"
					+ ((defaultValue != null && defaultValue.trim().length() > 0)
							? "black;\""
							: "#A9A9A9;\"") + "/>";
			if (getShowCalendar())
			{
				output += "<A onclick=\"showCalendar('"
						+ htmlComponentName
						+ "', "
						+ DynamicExtensionsUtility.getCurrentYear()
						+ ", "
						+ DynamicExtensionsUtility.getCurrentMonth()
						+ ", "
						+ DynamicExtensionsUtility.getCurrentDay()
						+ ", '"
						+ ProcessorConstants.DATE_ONLY_FORMAT
						+ "', 'dataEntryForm', '"
						+ htmlComponentName
						+ "', event, 1900, 2020);\" href=\"javascript://\">"
						+ "&nbsp;<IMG alt=\""
						+ dateFormatString
						+ " ["
						+ ProcessorConstants.DATE_TIME_FORMAT
						+ "]\" src=\"images/de/calendar.gif\" border=0 />&nbsp;</A>"
						+ "<DIV id=slcalcod"
						+ htmlComponentName
						+ " style=\"Z-INDEX: 10; LEFT: 100px; VISIBILITY: hidden; POSITION: absolute; TOP: 100px\">";
				if (getParentContainer().isAjaxRequest())
				{
					String imgsrc = getParentContainer().getRequest().getContextPath()
							+ "/images/de/";
					String filePath = getParentContainer().getRequest().getSession()
							.getServletContext()
							.getRealPath("/javascripts/de/CalendarComponent.js");
					String jsFunctionName = DEConstants.PRINT_TIME_CAL_FOR_AJAX;
					Object[] jsFunctionParameters = {htmlComponentName,
							DynamicExtensionsUtility.getCurrentDay(),
							DynamicExtensionsUtility.getCurrentMonth(),
							DynamicExtensionsUtility.getCurrentYear(),
							DynamicExtensionsUtility.getCurrentHours(),
							DynamicExtensionsUtility.getCurrentMinutes(), imgsrc};
					String jsOutput = DynamicExtensionsUtility.executeJavaScriptFunc(filePath,
							jsFunctionName, jsFunctionParameters);
					output += jsOutput;
				}
				else
				{
					output += "<SCRIPT>printTimeCalendar('" + htmlComponentName + "',"
							+ DynamicExtensionsUtility.getCurrentDay() + ","
							+ DynamicExtensionsUtility.getCurrentMonth() + ","
							+ DynamicExtensionsUtility.getCurrentYear() + ","
							+ DynamicExtensionsUtility.getCurrentHours() + ","
							+ DynamicExtensionsUtility.getCurrentMinutes() + ");</SCRIPT>";
				}
				output += "</DIV>";
			}
		}
		else if (dateFormat.equals(ProcessorConstants.MONTH_YEAR_FORMAT))
		{
			output += "<input class='font_bl_nor' name='"
					+ htmlComponentName
					+ "' id='"
					+ htmlComponentName
					+ "' title='Calender ["
					+ ProcessorConstants.MONTH_YEAR_FORMAT
					+ "]' value='"
					+ ((defaultValue != null && defaultValue.trim().length() > 0)
							? defaultValue
							: ProcessorConstants.MONTH_YEAR_FORMAT)
					+ "'"
					+ (((isReadOnly != null && isReadOnly) || (isSkipLogicReadOnly != null && isSkipLogicReadOnly))
							? " disabled='" + ProcessorConstants.TRUE
							: "")
					+ " onchange='isDataChanged();' onfocus=\"javascript:clearDate('"
					+ htmlComponentName
					+ "','"
					+ ProcessorConstants.MONTH_YEAR_FORMAT
					+ "');\""
					+ " style=\"color:"
					+ ((defaultValue != null && defaultValue.trim().length() > 0)
							? "black;\""
							: "#A9A9A9;\"") + "/>";
			if (getShowCalendar())
			{
				output += "<A onclick=\"showCalendar('"
						+ htmlComponentName
						+ "', "
						+ DynamicExtensionsUtility.getCurrentYear()
						+ ", "
						+ DynamicExtensionsUtility.getCurrentMonth()
						+ ", "
						+ 0
						+ ", '"
						+ ProcessorConstants.MONTH_YEAR_FORMAT
						+ "', 'dataEntryForm', '"
						+ htmlComponentName
						+ "', event, 1900, 2020);\" href=\"javascript://\">&nbsp;<IMG alt=\""
						+ dateFormatString
						+ "["
						+ ProcessorConstants.MONTH_YEAR_FORMAT
						+ "]\" src=\"images/de/calendar.gif\" border=0 />&nbsp;</A>"
						+ "<DIV id=slcalcod"
						+ htmlComponentName
						+ " style=\"Z-INDEX: 10; LEFT: 100px; VISIBILITY: hidden; POSITION: absolute; TOP: 100px\">";
				if (getParentContainer().isAjaxRequest())
				{
					String imgsrc = getParentContainer().getRequest().getContextPath()
							+ "/images/de/";
					String filePath = getParentContainer().getRequest().getSession()
							.getServletContext()
							.getRealPath("/javascripts/de/CalendarComponent.js");
					String jsFunctionName = DEConstants.PRINT_MON_YEAR_CAL_FOR_AJAX;
					Object[] jsFunctionParameters = {htmlComponentName,
							DynamicExtensionsUtility.getCurrentMonth(),
							DynamicExtensionsUtility.getCurrentYear(), imgsrc};
					String jsOutput = DynamicExtensionsUtility.executeJavaScriptFunc(filePath,
							jsFunctionName, jsFunctionParameters);
					output += jsOutput;
				}
				else
				{
					output += "<SCRIPT>printMonthYearCalendar('" + htmlComponentName + "',"
							+ DynamicExtensionsUtility.getCurrentMonth() + ","
							+ DynamicExtensionsUtility.getCurrentYear() + ");</SCRIPT>";
				}
				output += "</DIV>";
			}
		}
		else if (dateFormat.equals(ProcessorConstants.YEAR_ONLY_FORMAT))
		{

			output += "<input class='font_bl_nor' name='"
					+ htmlComponentName
					+ "' id='"
					+ htmlComponentName
					+ "' title='["
					+ ProcessorConstants.YEAR_ONLY_FORMAT
					+ "]' value='"
					+ ((defaultValue != null && defaultValue.trim().length() > 0)
							? defaultValue
							: ProcessorConstants.YEAR_ONLY_FORMAT)
					+ "'"
					+ (((isReadOnly != null && isReadOnly) || (isSkipLogicReadOnly != null && isSkipLogicReadOnly))
							? " disabled='" + ProcessorConstants.TRUE
							: "")
					+ " onchange='isDataChanged();' onfocus=\"javascript:clearDate('"
					+ htmlComponentName
					+ "','"
					+ ProcessorConstants.YEAR_ONLY_FORMAT
					+ "');\""
					+ " style=\"color:"
					+ ((defaultValue != null && defaultValue.trim().length() > 0)
							? "black;\""
							: "#A9A9A9;\"") + "/>";
			if (getShowCalendar())
			{
				output += "<A onclick=\"showCalendar('"
						+ htmlComponentName
						+ "', "
						+ DynamicExtensionsUtility.getCurrentYear()
						+ ", "
						+ 0
						+ ", "
						+ 0
						+ ", '"
						+ ProcessorConstants.YEAR_ONLY_FORMAT
						+ "', 'dataEntryForm', '"
						+ htmlComponentName
						+ "', event, 1900, 2020);\" href=\"javascript://\">&nbsp;<IMG alt=\""
						+ dateFormatString
						+ " ["
						+ ProcessorConstants.YEAR_ONLY_FORMAT
						+ "]\" src=\"images/de/calendar.gif\" border=0 />&nbsp;</A>"
						+ "<DIV id=slcalcod"
						+ htmlComponentName
						+ " style=\"Z-INDEX: 10; LEFT: 100px; VISIBILITY: hidden; POSITION: absolute; TOP: 100px\">";
				if (getParentContainer().isAjaxRequest())
				{
					String imgsrc = getParentContainer().getRequest().getContextPath()
							+ "/images/de/";
					String filePath = getParentContainer().getRequest().getSession()
							.getServletContext()
							.getRealPath("/javascripts/de/CalendarComponent.js");
					String jsFunctionName = DEConstants.PRINT_YEAR_CAL_FOR_AJAX;
					Object[] jsFunctionParameters = {htmlComponentName,
							DynamicExtensionsUtility.getCurrentYear(), imgsrc};
					String jsOutput = DynamicExtensionsUtility.executeJavaScriptFunc(filePath,
							jsFunctionName, jsFunctionParameters);
					output += jsOutput;
				}
				else
				{
					output += "<SCRIPT>printYearCalendar('" + htmlComponentName + "',"
							+ DynamicExtensionsUtility.getCurrentYear() + ");</SCRIPT>";
				}
				output += "</DIV>";
			}
		}
		if (getIsSkipLogicTargetControl())
		{
			output += "<input type='hidden' name='skipLogicControl' id='skipLogicControl' value = '"
					+ getHTMLComponentName() + "_div' />";
			output += "</div>";
		}
		return output;
	}

	/**
	 * @param defaultValue
	 * @return defaultValue
	 */
	private String reverseDate(String defaultValue)
	{
		// Date is like 1900-01-01 00:00:00.0 for MySQL5
		String date = defaultValue.substring(0, 10); // 1900-01-01
		String time = defaultValue.substring(10, defaultValue.length()); // 00:00:00.0

		String year = date.substring(0, 4); // 1900
		String month = date.substring(5, 7); // 01
		String day = date.substring(8, date.length()); // 01

		date = month + "-" + day + "-" + year;
		return (date + time);
	}

	/**
	 * @param attributeInterface
	 *            attribute type object
	 */
	public void setAttribute(AbstractAttributeInterface attributeInterface)
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
	public void setDateValueType(String dateValueType)
	{
		this.dateValueType = dateValueType;
	}

	/**
	 * Generate HTML for viewing data
	 * @param rowId
	 * @return htmlString for view mode
	 */
	protected String generateViewModeHTML(ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		String htmlString = "";
		if (value != null)
		{
			htmlString = "<span class = 'font_bl_nor'> " + value.toString() + "</span>";
		}

		return htmlString;
	}

	/**
	 * get value for a control
	 * @param rowId
	 * @return value for a control
	 */
	public List<String> getValueAsStrings()
	{
		return null;
	}

	/**
	 * set value as string for a control
	 * @param listOfValues list of permissible value
	 */
	public void setValueAsStrings(List<String> listOfValues)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * This method return default value for a control
	 * @param rowId
	 * @return
	 */
	private String getDefaultValueForControl()
	{
		String defaultValue = (String) value;
		if (!getIsSkipLogicDefaultValue())
		{
			if (value == null)
			{
				defaultValue = getAttibuteMetadataInterface().getDefaultValue();
			}
		}
		else
		{
			if (defaultValue == null || defaultValue.length() == 0)
			{
				defaultValue = getSkipLogicDefaultValue();
			}
		}
		return defaultValue;
	}

	/**
	 *
	 */
	public boolean getIsEnumeratedControl()
	{
		return false;
	}

	public Collection<UIProperty> getControlTypeValues()
	{
		Collection<UIProperty> uiProperties = super.getControlTypeValues();
		DatePickerEnum[] uiPropertyValues = DatePickerEnum.values();

		for (DatePickerEnum propertyType : uiPropertyValues)
		{
			String controlProperty = propertyType.getControlProperty(this);
			if (controlProperty != null)
			{
				uiProperties.add(new UIProperty(propertyType.getValue(), controlProperty));
			}
		}

		return uiProperties;
	}

	public void setControlTypeValues(Collection<UIProperty> uiProperties)
	{
		super.setControlTypeValues(uiProperties);

		for (UIProperty uiProperty : uiProperties)
		{
			DatePickerEnum propertyType = DatePickerEnum.getValue(uiProperty.getKey());
			propertyType.setControlProperty(this, uiProperty.getValue());
		}
	}

}