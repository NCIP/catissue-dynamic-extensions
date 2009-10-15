
package edu.common.dynamicextensions.domain.userinterface;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.DataElementInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.SkipLogicAttributeInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.CheckBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;

/**
 * @author chetan patil
 * @version 1.0
 * @created 28-Sep-2006 12:20:07 PM
 * @hibernate.joined-subclass table="DYEXTN_CHECK_BOX"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class CheckBox extends Control implements CheckBoxInterface
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domain.userinterface.Control#generateEditModeHTML()
	 */
	protected String generateEditModeHTML(Integer rowId,ContainerInterface container) throws DynamicExtensionsSystemException
	{
		String checked = getDefaultValueForControl(rowId);
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
		String htmlString = "";
		if (getIsSkipLogicTargetControl())
		{
			htmlString += "<input type='hidden' name='skipLogicControl' id='skipLogicControl' value = '"
					+ getHTMLComponentName() + "_div' /><div id='"
					+ getHTMLComponentName() + "_div' name='"
					+ getHTMLComponentName() + "_div'>";
		}
		String disabled = "";
		//		If control is defined as readonly through category CSV file,make it Disabled
		if ((this.isReadOnly != null && getIsReadOnly()) || (this.isSkipLogicReadOnly != null && this.isSkipLogicReadOnly))
		{
			disabled = ProcessorConstants.DISABLED;
		}

		String htmlComponentName = getHTMLComponentName();
		if (checked != null && (checked.equalsIgnoreCase("true") || checked.equals("1")|| checked.equals("y") || checked.equals("yes")))
		{
			htmlString += "<input type='checkbox' class='" + this.cssClass + "' name='"
					+ htmlComponentName + "' checkedValue='"
					+ DynamicExtensionsUtility.getValueForCheckBox(true) + "' uncheckedValue='"
					+ DynamicExtensionsUtility.getValueForCheckBox(false) + "'" + "value='"
					+ DynamicExtensionsUtility.getValueForCheckBox(true) + "' " + "id='"
					+ htmlComponentName + "'" + "checked" + disabled
					+ " onchange=\"isDataChanged();\" onclick=\"changeValueForCheckBox(this);"
					+ (this.isSkipLogic ? "getSkipLogicControl('"
							+ htmlComponentName + "','" + identifier + "','"
							+ parentContainerId + "');" : "") + "\">";
		}
		else
		{
			htmlString += "<input type='checkbox' class='" + this.cssClass + "' name='"
					+ htmlComponentName + "' checkedValue='"
					+ DynamicExtensionsUtility.getValueForCheckBox(true) + "' uncheckedValue='"
					+ DynamicExtensionsUtility.getValueForCheckBox(false) + "'" + "value='"
					+ DynamicExtensionsUtility.getValueForCheckBox(false) + "' " + disabled
					+ "id='"
					+ htmlComponentName
					+ "' onchange=\"isDataChanged();\" onclick=\"changeValueForCheckBox(this);"
					+ (this.isSkipLogic ? "getSkipLogicControl('"
							+ htmlComponentName + "','" + identifier + "','"
							+ parentContainerId + "');" : "") + "\">";
		}
		if (getIsSkipLogicTargetControl())
		{
			htmlString += "</div>";
		}
		return htmlString;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domain.userinterface.Control#generateViewModeHTML()
	 */
	protected String generateViewModeHTML(Integer rowId,ContainerInterface container) throws DynamicExtensionsSystemException
	{
		String htmlString = "&nbsp;";
		if (value != null)
		{
			String checked = (String) this.value;
			htmlString = "<input type='checkbox' class='" + cssClass + "' "
					+ DynamicExtensionsUtility.getCheckboxSelectionValue(checked) + " disabled>";
		}
		return htmlString;
	}

	/**
	 * This method sets the corresponding AbstractAttribute of this Control.
	 * @param abstractAttribute AbstractAttribute to be set.
	 */
	public void setAttribute(AbstractAttributeInterface abstractAttribute)
	{
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
		
	}
	/**
	 * 
	 * @return
	 */
	private String getDefaultValueForControl(Integer rowId)
	{
		String defaultValue = String.valueOf(this.value);
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
				defaultValue = getSkipLogicDefaultValue(rowId);
			}
		}
		return defaultValue;
	}
	/**
	 * 
	 * @param selectedPermissibleValues
	 * @return
	 */
	public List<SkipLogicAttributeInterface> getNonReadOnlySkipLogicAttributes(
			List<PermissibleValueInterface> selectedPermissibleValues,
			AttributeMetadataInterface attributeMetadataInterface,Integer rowId) 
	{
		List<SkipLogicAttributeInterface> skipLogicAttributes = new ArrayList<SkipLogicAttributeInterface>();
		String checked = getDefaultValueForControl(rowId);
		if (checked.equalsIgnoreCase("true") || checked.equals("1")|| checked.equals("y") || checked.equals("yes"))
		{
			skipLogicAttributes.addAll(ControlsUtility
					.getSkipLogicAttributesForCheckBox(attributeMetadataInterface));
		}
		return skipLogicAttributes;
	}
	/**
	 * 
	 * @param selectedPermissibleValues
	 * @return
	 */
	public List<SkipLogicAttributeInterface> getReadOnlySkipLogicAttributes(
			List<PermissibleValueInterface> selectedPermissibleValues,
			AttributeMetadataInterface attributeMetadataInterface,Integer rowId) 
	{
		List<SkipLogicAttributeInterface> skipLogicAttributes = new ArrayList<SkipLogicAttributeInterface>();
		String checked = getDefaultValueForControl(rowId);
		if (!checked.equalsIgnoreCase("true") && !checked.equals("1") && !checked.equals("y") && !checked.equals("yes"))
		{
			skipLogicAttributes.addAll(ControlsUtility
					.getSkipLogicAttributesForCheckBox(attributeMetadataInterface));
		}
		return skipLogicAttributes;
	}
	/**
	 * 
	 */
	public void setSkipLogicControls(Integer rowId)
	{
		setSkipLogicControlValues(rowId,null);
	}
	/**
	 * 
	 */
	private List<ControlInterface> setSkipLogicControlValues(Integer rowId,List<String> values)
	{ 
		List<ControlInterface> controlList = null;
		if (values == null)
		{
			values = getValueAsStrings(rowId);
		}
		if (values != null)
		{
			controlList = getSkipLogicControls(null,rowId,values);
		}
		return controlList;
	}
}