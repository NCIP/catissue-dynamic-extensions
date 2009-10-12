
package edu.common.dynamicextensions.domain.userinterface;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.DoubleTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.FloatTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.LongTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.RadioButtonInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.wustl.common.beans.NameValueBean;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:08 PM
 * @hibernate.joined-subclass table="DYEXTN_RADIOBUTTON"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class RadioButton extends Control implements RadioButtonInterface
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method generates the HTML code for RadioButton control on the HTML form
	 * @return HTML code for RadioButton
	 * @throws DynamicExtensionsSystemException
	 */
	public String generateEditModeHTML(Integer rowId,ContainerInterface container) throws DynamicExtensionsSystemException
	{
		List<NameValueBean> nameValueBeanList = null;
		String htmlString = "";
		if (getIsSkipLogicTargetControl())
		{
			htmlString += "<input type='hidden' name='skipLogicControl' id='skipLogicControl' value = '"
					+ getHTMLComponentName() + "_div' /><div id='"
					+ getHTMLComponentName() + "_div' name='"
					+ getHTMLComponentName() + "_div'>";
		}
		String defaultValue = getDefaultValueForControl(rowId);
		String disabled = "";
		//If control is defined as readonly through category CSV file,make it Disabled
		if ((this.isReadOnly != null && getIsReadOnly()) || (this.isSkipLogicReadOnly != null && this.isSkipLogicReadOnly))
		{
			disabled = ProcessorConstants.DISABLED;
		}
		String parentContainerId = "";
		if (this.getParentContainer() != null && this.getParentContainer().getId() != null)
		{
			parentContainerId = this.getParentContainer().getId().toString();
		}
		String identifier = "";
		if (this.getId() != null)
		{
			identifier = this.getId().toString();
		}
		nameValueBeanList = ControlsUtility.populateListOfValues(this,rowId);

		String htmlComponentName = getHTMLComponentName();
		if (nameValueBeanList != null)
		{
			for (NameValueBean nameValueBean : nameValueBeanList)
			{
				String optionName = nameValueBean.getName();
				String optionValue = nameValueBean.getValue();
				if (optionValue.equals(defaultValue))
				{
					htmlString += "<input type='radio' onchange='"
							+ (this.isSkipLogic ? "getSkipLogicControl('"
									+ htmlComponentName + "','" + identifier
									+ "','" + parentContainerId + "');" : "")
							+ "isDataChanged();' "
							+ "class='font_bl_nor' "
							+ "name='"
							+ htmlComponentName
							+ "' "
							+ "value='"
							+ optionValue
							+ "' "
							+ "id='"
							+ optionName
							+ "' checked "
							+ disabled
							+ "  "
							+ ">"
							+ "<label for=\""
							+ htmlComponentName
							+ "\">"
							+ optionName
							+ "</label> <img src='images/de/spacer.gif' width='2' height='2'>";
				}
				else
				{
					htmlString += "<input type='radio' onchange='"
							+ (this.isSkipLogic ? "getSkipLogicControl('"
									+ htmlComponentName + "','" + identifier
									+ "','" + parentContainerId + "');" : "")
							+ "isDataChanged();' "
							+ "class='font_bl_nor' "
							+ "name='"
							+ htmlComponentName
							+ "' "
							+ "value='"
							+ optionValue
							+ "' "
							+ "id='"
							+ optionName
							+ "' "
							+ disabled

							+ " >"
							+ "<label for=\""
							+ htmlComponentName
							+ "\">"
							+ optionName
							+ "</label> <img src='images/de/spacer.gif' width='2' height='2'>";
				}
			}
		}
		if (getIsSkipLogicTargetControl())
		{
			htmlString += "</div>";
		}
		return htmlString;
	}
	/**
	 * 
	 * @return
	 */
	private String getDefaultValueForControl(Integer rowId)
	{
		String defaultValue = (String) this.value;
		if (!getIsSkipLogicTargetControl())
		{
			if (defaultValue == null)
			{
				defaultValue = this.getAttibuteMetadataInterface().getDefaultValue();
				if (defaultValue == null || defaultValue.length() == 0)
				{
					defaultValue = "";
				}
			}
		}
		else
		{
			if (defaultValue == null || defaultValue.length() == 0)
			{
				defaultValue = getSkipLogicDefaultValue(rowId);
			}
		}
		if (defaultValue != null)
		{
			if (defaultValue.length() > 0
					&& this.getAttibuteMetadataInterface()
							.getAttributeTypeInformation() instanceof DoubleTypeInformationInterface)
			{
				double doubleValue = Double.parseDouble(defaultValue);
				defaultValue = Double.toString(doubleValue);
			}
			else if (defaultValue.length() > 0
					&& this.getAttibuteMetadataInterface()
							.getAttributeTypeInformation() instanceof LongTypeInformationInterface)
			{
				long longValue = Long.parseLong(defaultValue);
				defaultValue = Long.toString(longValue);
	
			}
			else if (defaultValue.length() > 0
					&& this.getAttibuteMetadataInterface()
							.getAttributeTypeInformation() instanceof FloatTypeInformationInterface)
			{
				float floatValue = Float.parseFloat(defaultValue);
				defaultValue = Float.toString(floatValue);
	
			}
		}
		return defaultValue;
	}
	/**
	 * This method sets the corresponding Attribute of this control.
	 * @param abstractAttribute the AbstractAttribute to be set.
	 */
	public void setAttribute(AbstractAttributeInterface abstractAttribute)
	{
		// TODO Auto-generated constructor stub
	}

	public String generateViewModeHTML(Integer rowId,ContainerInterface container) throws DynamicExtensionsSystemException
	{
		String htmlString = "&nbsp;";
		if (value != null)
		{
			htmlString = "<span class = '" + cssClass + "'> " + this.value.toString() + "</span>";
		}
		return htmlString;
	}

	/**
	 * 
	 */
	public List<String> getValueAsStrings(Integer rowId) 
	{
		List<String> values = new ArrayList<String>();
		values.add(getDefaultValueForControl(rowId));
		return values;
	}
	/**
	 * 
	 */
	public void setValueAsStrings(List<String> listOfValues) 
	{
		if (!listOfValues.isEmpty())
		{
			setValue(listOfValues.get(0));
		}
	}

}