
package edu.common.dynamicextensions.domain.userinterface;

import java.util.Collection;
import java.util.List;

import edu.common.dynamicextensions.domain.userinterface.beans.UIProperty;
import edu.common.dynamicextensions.domain.userinterface.enums.TextAreaEnum;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextAreaInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:09 PM
 * @hibernate.joined-subclass table="DYEXTN_TEXTAREA"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class TextArea extends Control implements TextAreaInterface
{

	/**
	 *
	 */
	private static final long serialVersionUID = 201964699680324430L;

	/**
	 * Number of columns in the text area.
	 */
	protected Integer columns;

	/**
	 * Number of rows in the text area.
	 */
	protected Integer rows;

	/**
	 * @hibernate.property name="columns" type="integer" column="TEXTAREA_COLUMNS"
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
	 * @hibernate.property name="rows" type="integer" column="TEXTAREA_ROWS"
	 * @return Returns the rows.
	 */
	public Integer getRows()
	{
		return rows;
	}

	/**
	 * @param rows The rows to set.
	 */
	public void setRows(Integer rows)
	{
		this.rows = rows;
	}

	/**
	 * get values as strings
	 * @param rowId
	 * @return list of values as strings
	 */
	public List<String> getValueAsStrings()
	{
		return null;
	}

	/**
	 * This method generates the HTML code for TextArea control on the HTML form
	 * @return HTML code for TextArea
	 * @throws DynamicExtensionsSystemException
	 */
	public String generateEditModeHTML(ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		String htmlString = "";
		String defaultValue = DynamicExtensionsUtility
				.replaceHTMLSpecialCharacters(getDefaultValueForControl());

		String htmlComponentName = getHTMLComponentName();
		if (getIsSkipLogicTargetControl())
		{
			htmlString += "<div id='" + getHTMLComponentName() + "_div' name='"
					+ getHTMLComponentName() + "_div'>";
		}
		htmlString += "<textarea " + "class='font_bl_nor' " + "name='" + htmlComponentName + "' "
				+ "id='" + htmlComponentName + "' ";

		//If control is defined as read only through category CSV file,make it Disabled

		if ((this.isReadOnly != null && getIsReadOnly())
				|| (this.isSkipLogicReadOnly != null && this.isSkipLogicReadOnly))
		{
			htmlString += ProcessorConstants.DISABLED;
		}
		int noCols = columns.intValue();
		int noRows = rows.intValue();

		if (noCols > 0)
		{
			htmlString += "cols='" + noCols + "' ";
		}
		else
		{
			htmlString += "cols='" + Constants.DEFAULT_COLUMN_SIZE + "' ";
		}

		if (noRows > 0)
		{
			htmlString += "rows='" + noRows + "' ";
		}
		else
		{
			htmlString += "rows='" + Constants.DEFAULT_ROW_SIZE + "' ";
		}

		int maxChars = 0;
		AttributeMetadataInterface attibute = this.getAttibuteMetadataInterface();
		if (attibute != null)
		{
			maxChars = attibute.getMaxSize();
		}

		if (maxChars > 0)
		{
			htmlString += " onchange='isDataChanged();' onblur='textCounter(this," + maxChars
					+ ")'  ";
		}

		htmlString += " wrap='virtual'>";

		if (defaultValue == null || (defaultValue.length() == 0))
		{
			htmlString += "</textarea>";
		}
		else
		{
			htmlString += defaultValue + "</textarea>";
		}
		if (getIsSkipLogicTargetControl())
		{
			htmlString += "<input type='hidden' name='skipLogicControl' id='skipLogicControl' value = '"
					+ getHTMLComponentName() + "_div' />";
			htmlString += "</div>";
		}
		return htmlString;
	}

	/**
	 * This method associates the Attribute to this Control.
	 * @param attribute the Attribute to be associated.
	 */
	public void setAttribute(AbstractAttributeInterface attribute)
	{
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domain.userinterface.Control#generateViewModeHTML()
	 */
	protected String generateViewModeHTML(ContainerInterface container)
			throws DynamicExtensionsSystemException
	{

		String htmlgenerated = "&nbsp;";
		if (value != null)
		{
			htmlgenerated = "<span class = 'font_bl_nor'> " + this.value.toString() + "</span>";
		}
		return htmlgenerated;

	}

	/**
	 * @param rowId
	 * @return default value for control
	 */
	private String getDefaultValueForControl()
	{
		String defaultValue = (String) this.value;
		if (!getIsSkipLogicDefaultValue())
		{
			if (this.value == null)
			{
				defaultValue = this.getAttibuteMetadataInterface().getDefaultValue();
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
	 * set value for a control
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

	/**
	 * Returns collection of key-value pairs.
	 */
	public Collection<UIProperty> getControlTypeValues()
	{
		Collection<UIProperty> uiProperties = super.getControlTypeValues();
		TextAreaEnum[] uiPropertyValues = TextAreaEnum.values();

		for (TextAreaEnum propertyType : uiPropertyValues)
		{
			String controlProperty = propertyType.getControlProperty(this);
			if (controlProperty != null)
			{
				uiProperties.add(new UIProperty(propertyType.getValue(), controlProperty));
			}
		}

		return uiProperties;
	}

	/**
	 * Set collection of key-value pairs for a control.
	 */
	public void setControlTypeValues(Collection<UIProperty> uiProperties)
	{
		super.setControlTypeValues(uiProperties);

		for (UIProperty uiProperty : uiProperties)
		{
			TextAreaEnum propertyType = TextAreaEnum.getValue(uiProperty.getKey());
			propertyType.setControlProperty(this, uiProperty.getValue());
		}
	}

}