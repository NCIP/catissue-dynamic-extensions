
package edu.common.dynamicextensions.domain.userinterface;

import java.util.Collection;
import java.util.List;

import edu.common.dynamicextensions.domain.userinterface.beans.UIProperty;
import edu.common.dynamicextensions.domain.userinterface.enums.TextFieldEnum;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextFieldInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;

/**
 * This Class represents the TextField (TextBox) of the HTML page.
 * @version 1.0
 * @created 28-Sep-2006 12:20:09 PM
 * @hibernate.joined-subclass table="DYEXTN_TEXTFIELD"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class TextField extends Control implements TextFieldInterface
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Size of the text field to be shown on UI.
	 */
	protected Integer columns;

	/**
	 * Boolean value indicating whether this text field is password field.
	 */
	protected Boolean isPassword;

	/**
	 * Boolean value indicating whether this text field is password field.
	 */
	protected Boolean isUrl;

	/**
	 * @hibernate.property name="columns" type="integer" column="NO_OF_COLUMNS"
	 * @return Returns the columns.
	 */
	public Integer getColumns()
	{
		return columns;
	}

	/**
	 * @param columns The columns to set.
	 */
	public void setColumns(Integer columns)
	{
		this.columns = columns;
	}

	/**
	 * @hibernate.property name="isPassword" type="boolean" column="IS_PASSWORD"
	 * @return Returns the isPassword.
	 */
	public Boolean getIsPassword()
	{
		return isPassword;
	}

	/**
	 * @param isPassword The isPassword to set.
	 */
	public void setIsPassword(Boolean isPassword)
	{
		this.isPassword = isPassword;
	}

	/**
	 * This method generates the HTML code for TextField control on the HTML form
	 * @return HTML code for TextField
	 * @throws DynamicExtensionsSystemException
	 */
	public String generateEditModeHTML(ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		String defaultValue = DynamicExtensionsUtility
				.replaceHTMLSpecialCharacters(getDefaultValueForControl());

		String htmlComponentName = getHTMLComponentName();
		String htmlString = "";
		if (getIsSkipLogicTargetControl() || getIsCalculated())
		{
			htmlString += "<div id='" + getHTMLComponentName() + "_div' name='"
					+ getHTMLComponentName() + "_div'>";
		}
		if (isUrl != null && isUrl)
		{
			htmlString += "<a href='javascript:void(0)' onclick=\"window.open('"
					+ defaultValue
					+ "','','width=800,height=600,toolbar=yes,location=yes,directories=yes,status=yes,menubar=yes,scrollbars=yes,copyhistory=yes,resizable=yes')\">"
					+ defaultValue + "</a>";
		}
		else
		{
			htmlString += "<INPUT "
					+ "class='font_bl_nor' "
					+ "name='"
					+ htmlComponentName
					+ "' "
					+ "id='"
					+ htmlComponentName
					+ "' onchange=\"isDataChanged();"
					+ (this.getIsSourceForCalculatedAttribute() != null
							&& this.getIsSourceForCalculatedAttribute()
							? "calculateAttributes();"
							: "") + "\" value='"
					+ DynamicExtensionsUtility.getEscapedStringValue(defaultValue) + "' ";

			int columnSize = columns.intValue();
			if (columnSize > 0)
			{
				htmlString += "size='" + columnSize + "' ";
				htmlString += "style='width:" + (columnSize + 1) + "ex' ";
			}
			else
			{
				htmlString += "size='" + Constants.DEFAULT_COLUMN_SIZE + "' ";
			}

			if (isPassword != null && isPassword.booleanValue())
			{
				htmlString += " type='password' ";
			}
			else
			{
				htmlString += " type='text' ";
			}

			//set isdisabled property

			if ((this.isReadOnly != null && this.isReadOnly)
					|| (this.isSkipLogicReadOnly != null && this.isSkipLogicReadOnly))
			{
				htmlString += " readonly='" + ProcessorConstants.TRUE + "' ";
			}

			int maxChars = 0;
			AttributeMetadataInterface attibute = this.getAttibuteMetadataInterface();
			if (attibute != null)
			{
				maxChars = attibute.getMaxSize();
			}
			//Changed by: Kunal
			//Incase of input type is chosen as number
			//the max char size is -1
			if (maxChars > 0)
			{
				htmlString += " onblur='textCounter(this," + maxChars + ")'  ";
			}

			htmlString += "/>";
			//String measurementUnit = getMeasurementUnit(this.getAbstractAttribute());
			String measurementUnit = this.getAttibuteMetadataInterface().getMeasurementUnit();
			if (measurementUnit != null)
			{
				if (measurementUnit.equalsIgnoreCase("none"))
				{
					measurementUnit = "";
				}
				htmlString += " " + measurementUnit;
			}
		}
		if (getIsSkipLogicTargetControl())
		{
			htmlString += "<input type='hidden' name='skipLogicControl' id='skipLogicControl' value = '"
					+ getHTMLComponentName() + "_div' />";
		}
		if (getIsCalculated())
		{
			htmlString += "<input type='hidden' name='calculatedControl' id='calculatedControl' value = '"
					+ getHTMLComponentName() + "_div' />";
		}
		if (getIsSkipLogicTargetControl() || getIsCalculated())
		{
			htmlString += "</div>";
		}
		return htmlString;
	}

	/**
	 * This method sets the associated AbstractAttribute of this Control.
	 * @param abstractAttributeInterface AbstractAttribute to be associated.
	 */
	public void setAttribute(AbstractAttributeInterface abstractAttributeInterface)
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method returns the Boolean value that decides whether the value of this control should be treated as normal text or URL.
	 * @hibernate.property name="isUrl" type="boolean" column="IS_URL"
	 * @return the Boolean value	true - value is URL
	 * 								false - value is normal text.
	 */
	public Boolean getIsUrl()
	{
		return isUrl;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.userinterface.TextFieldInterface#setIsUrl(java.lang.Boolean)
	 */
	public void setIsUrl(Boolean isUrl)
	{
		this.isUrl = isUrl;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domain.userinterface.Control#generateViewModeHTML()
	 */
	protected String generateViewModeHTML(ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		String defaultValue = getDefaultValueForControl();
		String htmlString = "&nbsp;";
		if (defaultValue != null)
		{
			if (isUrl != null && (isUrl.booleanValue()))
			{
				htmlString = "<a href='javascript:void(0)' onclick=\"window.open('"
						+ defaultValue
						+ "','','width=800,height=600,toolbar=yes,location=yes,directories=yes,status=yes,menubar=yes,scrollbars=yes,copyhistory=yes,resizable=yes')\">"
						+ defaultValue + "</a>";
			}
			else
			{
				htmlString = "<span class = 'font_bl_nor'> " + defaultValue + "</span>";
			}
		}
		return htmlString;
	}

	private String getDefaultValueForControl()
	{
		String defaultValue = (String) this.value;
		if (!getIsSkipLogicDefaultValue())
		{
			if (this.value == null)
			{
				defaultValue = "";
			}
			else
			{
				defaultValue = (String) this.value;
			}
			if (isUrl != null && (isUrl.booleanValue()))
			{
				defaultValue = this.getAttibuteMetadataInterface().getDefaultValue();
			}
			else
			{
				if (this.value == null)
				{
					defaultValue = this.getAttibuteMetadataInterface().getDefaultValue();
					if (defaultValue == null || (defaultValue.length() == 0))
					{
						defaultValue = "";
					}
				}
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
	public List<String> getValueAsStrings()
	{
		return null;
	}

	/**
	 *
	 */
	public void setValueAsStrings(List<String> listOfValues)
	{
		// TODO Auto-generated method stub

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
		Collection<UIProperty> controlTypeValues = super.getControlTypeValues();
		TextFieldEnum[] uiPropertyValues = TextFieldEnum.values();

		for (TextFieldEnum propertyType : uiPropertyValues)
		{
			String controlProperty = propertyType.getControlProperty(this);
			//LOGGER.debug("Control property value is: " + controlProperty);
			if (controlProperty != null)
			{
				controlTypeValues.add(new UIProperty(propertyType.getValue(), controlProperty));
			}
		}

		return controlTypeValues;
	}

	public void setControlTypeValues(Collection<UIProperty> uiProperties)
	{
		super.setControlTypeValues(uiProperties);

		for (UIProperty uiProperty : uiProperties)
		{
			TextFieldEnum propertyType = TextFieldEnum.getValue(uiProperty.getKey());
			propertyType.setControlProperty(this, uiProperty.getValue());
		}
	}
}