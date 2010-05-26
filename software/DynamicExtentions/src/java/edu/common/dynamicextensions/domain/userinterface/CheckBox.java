
package edu.common.dynamicextensions.domain.userinterface;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.SkipLogicAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.CheckBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;

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
	protected String generateEditModeHTML(ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		String checked = getDefaultValueForControl();
		String parentContainerId = "";
		if (getParentContainer() != null && getParentContainer().getId() != null)
		{
			parentContainerId = getParentContainer().getId().toString();
		}
		String identifier = "";
		if (getId() != null)
		{
			identifier = getId().toString();
		}
		String htmlString = "";
		if (getIsSkipLogicTargetControl())
		{
			htmlString += "<div id='" + getHTMLComponentName() + "_div' name='"
					+ getHTMLComponentName() + "_div'>";
		}
		String disabled = "";
		//		If control is defined as readonly through category CSV file,make it Disabled
		if ((isReadOnly != null && getIsReadOnly())
				|| (isSkipLogicReadOnly != null && isSkipLogicReadOnly))
		{
			disabled = ProcessorConstants.DISABLED;
		}

		String htmlComponentName = getHTMLComponentName();
		if (checked != null
				&& (checked.equalsIgnoreCase("true") || checked.equals("1") || checked.equals("y") || checked
						.equals("yes")))
		{
			htmlString += "<input type='checkbox' class='"
					+ cssClass
					+ "' name='"
					+ htmlComponentName
					+ "' checkedValue='"
					+ DynamicExtensionsUtility.getValueForCheckBox(true)
					+ "' uncheckedValue='"
					+ DynamicExtensionsUtility.getValueForCheckBox(false)
					+ "'"
					+ "value='"
					+ DynamicExtensionsUtility.getValueForCheckBox(true)
					+ "' "
					+ "id='"
					+ htmlComponentName
					+ "'"
					+ "checked"
					+ disabled
					+ " onchange=\"isDataChanged();\" onclick=\"changeValueForCheckBox(this);"
					+ (isSkipLogic ? "getSkipLogicControl('" + htmlComponentName + "','"
							+ identifier + "','" + parentContainerId + "');" : "") + "\">";
		}
		else
		{
			htmlString += "<input type='checkbox' class='"
					+ cssClass
					+ "' name='"
					+ htmlComponentName
					+ "' checkedValue='"
					+ DynamicExtensionsUtility.getValueForCheckBox(true)
					+ "' uncheckedValue='"
					+ DynamicExtensionsUtility.getValueForCheckBox(false)
					+ "'"
					+ "value='"
					+ DynamicExtensionsUtility.getValueForCheckBox(false)
					+ "' "
					+ disabled
					+ "id='"
					+ htmlComponentName
					+ "' onchange=\"isDataChanged();\" onclick=\"changeValueForCheckBox(this);"
					+ (isSkipLogic ? "getSkipLogicControl('" + htmlComponentName + "','"
							+ identifier + "','" + parentContainerId + "');" : "") + "\">";
		}
		if (getIsSkipLogicTargetControl())
		{
			htmlString += "<input type='hidden' name='skipLogicControl' id='skipLogicControl' value = '"
					+ getHTMLComponentName() + "_div' />";
			htmlString += "</div>";
		}
		return htmlString;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domain.userinterface.Control#generateViewModeHTML()
	 */
	protected String generateViewModeHTML(ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		String htmlString = "&nbsp;";
		if (value != null)
		{
			String checked = (String) value;
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
	public List<String> getValueAsStrings()
	{
		List<String> values = new ArrayList<String>();
		values.add(getDefaultValueForControl());
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
	private String getDefaultValueForControl()
	{
		String defaultValue = String.valueOf(value);
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
	 * @param selectedPermissibleValues
	 * @return
	 */
	public List<SkipLogicAttributeInterface> getNonReadOnlySkipLogicAttributes(
			List<PermissibleValueInterface> selectedPermissibleValues,
			AttributeMetadataInterface attributeMetadataInterface)
	{
		List<SkipLogicAttributeInterface> skipLogicAttributes = new ArrayList<SkipLogicAttributeInterface>();
		String checked = getDefaultValueForControl();
		if (DEConstants.TRUE.equalsIgnoreCase(checked) || "1".equals(checked)
				|| "y".equals(checked) || "yes".equals(checked))
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
			AttributeMetadataInterface attributeMetadataInterface)
	{
		List<SkipLogicAttributeInterface> skipLogicAttributes = new ArrayList<SkipLogicAttributeInterface>();
		String checked = getDefaultValueForControl();
		if (!DEConstants.TRUE.equalsIgnoreCase(checked) && !"1".equals(checked)
				&& !"y".equals(checked) && !"yes".equals(checked))
		{
			skipLogicAttributes.addAll(ControlsUtility
					.getSkipLogicAttributesForCheckBox(attributeMetadataInterface));
		}
		return skipLogicAttributes;
	}

	/**
	 *
	 */
	public void setSkipLogicControls()
	{
		setSkipLogicControlValues(null);
	}

	/**
	 *
	 */
	private List<ControlInterface> setSkipLogicControlValues(List<String> values)
	{
		List<ControlInterface> controlList = null;
		if (values == null)
		{
			values = getValueAsStrings();
		}
		if (values != null)
		{
			controlList = getSkipLogicControls(null, values);
		}
		return controlList;
	}

	/**
	 *
	 */
	public boolean getIsEnumeratedControl()
	{
		return false;
	}
}