
package edu.common.dynamicextensions.domain.userinterface;

import java.util.Collection;
import java.util.List;

import edu.common.dynamicextensions.category.beans.UIProperty;
import edu.common.dynamicextensions.category.enums.TextAreaEnum;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextAreaInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ValidatableInterface;
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
public class TextArea extends Control implements TextAreaInterface, ValidatableInterface
{

	/** The Constant serialVersionUID. */
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
		StringBuffer htmlString =new StringBuffer(99);
		String defaultValue = DynamicExtensionsUtility
				.replaceHTMLSpecialCharacters(getDefaultValueForControl());

		String htmlComponentName = getHTMLComponentName();
		if (getIsSkipLogicTargetControl())
		{
			htmlString.append("<div id='");
			htmlString.append(getHTMLComponentName());
			htmlString.append("_div' name='");
			htmlString.append(getHTMLComponentName());
			htmlString.append("_div'>");
		}
		htmlString.append("<textarea ");
		htmlString.append("class='font_bl_nor' ");
		htmlString.append("name='");
		htmlString.append(htmlComponentName);
		htmlString.append("' ");
		htmlString.append("id='");
		htmlString.append(htmlComponentName);
		htmlString.append("' ");

		//If control is defined as read only through category CSV file,make it Disabled

		if ((this.isReadOnly != null && getIsReadOnly())
				|| (this.isSkipLogicReadOnly != null && this.isSkipLogicReadOnly))
		{
			htmlString.append(ProcessorConstants.DISABLED);
		}
		int noCols = columns.intValue();
		int noRows = rows.intValue();

		if (noCols > 0)
		{
			htmlString.append("cols='");
			htmlString.append(noCols);
			htmlString.append("' ");
		}
		else
		{
			htmlString.append("cols='");
			htmlString.append(Constants.DEFAULT_COLUMN_SIZE);
			htmlString.append("' ");
		}

		if (noRows > 0)
		{
			htmlString.append("rows='");
			htmlString.append(noRows);
			htmlString.append("' ");
		}
		else
		{
			htmlString.append("rows='");
			htmlString.append(Constants.DEFAULT_ROW_SIZE);
			htmlString.append("' ");
		}


		htmlString.append(" wrap='virtual'>");

		if (defaultValue == null || (defaultValue.length() == 0))
		{
			htmlString.append("</textarea>");
		}
		else
		{
			htmlString.append(defaultValue);
			htmlString.append("</textarea>");
		}
		if (getIsSkipLogicTargetControl())
		{
			htmlString.append("<input type='hidden' name='skipLogicControl' id='skipLogicControl' value = '");
			htmlString.append(getHTMLComponentName());
			htmlString.append("_div' /></div>");
		}
		return htmlString.toString();
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

		StringBuffer htmlgenerated =new StringBuffer();
		htmlgenerated.append("&nbsp;");
		if (value != null)
		{
			htmlgenerated.append("<span class = 'font_bl_nor'> ");
			htmlgenerated.append(this.value.toString());
			htmlgenerated.append("</span>");
		}
		return htmlgenerated.toString();

	}

	/**
	 * Gets the default value for control.
	 * @return the default value for control
	 */
	private String getDefaultValueForControl()
	{
		String defaultValue;
		if (this.value == null || this.value.toString().length() == 0)
		{
			if (isSkipLogicDefaultValue())
			{
				defaultValue = getDefaultSkipLogicValue();
			}
			else
			{
				defaultValue = this.getAttibuteMetadataInterface().getDefaultValue(null);
				if (defaultValue == null || defaultValue.length() == 0)
				{
					defaultValue = "";
				}
			}
		}
		else
		{
			defaultValue = this.value.toString();
		}
		return defaultValue;
	}

	/**
	 * Gets the default skip logic value.
	 * @return the default skip logic value
	 */
	private String getDefaultSkipLogicValue()
	{
		CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface) this
				.getAttibuteMetadataInterface();
		Object defaultValue = categoryAttribute.getDefaultSkipLogicValue().getValueAsObject();
		return defaultValue.toString();

	}

	/**
	 * Checks if is skip logic default value.
	 * @return true, if is skip logic default value
	 */
	private boolean isSkipLogicDefaultValue()
	{
		return ((CategoryAttributeInterface) this.getAttibuteMetadataInterface())
				.getDefaultSkipLogicValue() != null;
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