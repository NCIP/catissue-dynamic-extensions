
package edu.common.dynamicextensions.domain.userinterface;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
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

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domain.userinterface.Control#generateEditModeHTML()
	 */
	@Override
    protected String generateEditModeHTML(final ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		final String checked = getDefaultValueForControl();

		final StringBuilder htmlStringBuffer = new StringBuilder();
		if (getIsSkipLogicTargetControl())
		{
			htmlStringBuffer.append("<div id='");
			htmlStringBuffer.append(getHTMLComponentName());
			htmlStringBuffer.append("_div' name='");
			htmlStringBuffer.append(getHTMLComponentName());
			htmlStringBuffer.append("_div'>");
		}
		String disabled = "";
		//		If control is defined as readonly through category CSV file,make it Disabled
		if ((isReadOnly != null && getIsReadOnly())
				|| (isSkipLogicReadOnly != null && isSkipLogicReadOnly))
		{
			disabled = ProcessorConstants.DISABLED;
		}

		final String htmlComponentName = getHTMLComponentName();
		if (checked != null
				&& (checked.equalsIgnoreCase("true") || checked.equals("1") || checked.equals("y") || checked
						.equals("yes")))
		{
			htmlStringBuffer.append("<input type='checkbox' class='");
			htmlStringBuffer.append(cssClass);
			htmlStringBuffer.append("' name='");
			htmlStringBuffer.append(htmlComponentName);
			htmlStringBuffer.append("' checkedValue='");
			htmlStringBuffer.append(DynamicExtensionsUtility.getValueForCheckBox(true));
			htmlStringBuffer.append("' uncheckedValue='");
			htmlStringBuffer.append(DynamicExtensionsUtility.getValueForCheckBox(false));
			htmlStringBuffer.append("' value='");
			htmlStringBuffer.append(DynamicExtensionsUtility.getValueForCheckBox(true));
			htmlStringBuffer.append("' id='");
			htmlStringBuffer.append(htmlComponentName);
			htmlStringBuffer.append("' checked");
			htmlStringBuffer.append(disabled);
			htmlStringBuffer.append(" onchange=\"");
			htmlStringBuffer.append("\" onclick=\"changeValueForCheckBox(this);");
			htmlStringBuffer.append(getOnchangeServerCall());
			htmlStringBuffer.append("\">");
		}
		else
		{
			htmlStringBuffer.append("<input type='checkbox' class='");
			htmlStringBuffer.append(cssClass);
			htmlStringBuffer.append("' name='");
			htmlStringBuffer.append(htmlComponentName);
			htmlStringBuffer.append("' checkedValue='");
			htmlStringBuffer.append(DynamicExtensionsUtility.getValueForCheckBox(true));
			htmlStringBuffer.append("' uncheckedValue='");
			htmlStringBuffer.append(DynamicExtensionsUtility.getValueForCheckBox(false));
			htmlStringBuffer.append("' value='");
			htmlStringBuffer.append(DynamicExtensionsUtility.getValueForCheckBox(false));
			htmlStringBuffer.append("' id='");
			htmlStringBuffer.append(disabled);
			htmlStringBuffer.append(htmlComponentName);
			htmlStringBuffer.append("' onchange=\"");
			htmlStringBuffer.append("\" onclick=\"changeValueForCheckBox(this);");
			htmlStringBuffer.append(getOnchangeServerCall());
			htmlStringBuffer.append("\">");

		}
		if (getIsSkipLogicTargetControl())
		{
			htmlStringBuffer
					.append("<input type='hidden' name='skipLogicControl' id='skipLogicControl' value = '");
			htmlStringBuffer.append(getHTMLComponentName());
			htmlStringBuffer.append("_div' /></div>");
		}
		return htmlStringBuffer.toString();
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domain.userinterface.Control#generateViewModeHTML()
	 */
	@Override
    protected String generateViewModeHTML(final ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		final StringBuffer htmlStringBuffer = new StringBuffer();
		htmlStringBuffer.append("&nbsp;");
		if (value != null)
		{
			final String checked = (String) value;
			htmlStringBuffer.append("<input type='checkbox' class='");
			htmlStringBuffer.append(cssClass);
			htmlStringBuffer.append("' ");
			htmlStringBuffer.append(DynamicExtensionsUtility.getCheckboxSelectionValue(checked));
			htmlStringBuffer.append(" disabled>");
		}
		return htmlStringBuffer.toString();
	}

	/**
	 * This method sets the corresponding AbstractAttribute of this Control.
	 * @param abstractAttribute AbstractAttribute to be set.
	 */
	public void setAttribute(final AbstractAttributeInterface abstractAttribute)
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 *
	 */
	@Override
    public List<String> getValueAsStrings()
	{
		final List<String> values = new ArrayList<String>();
		values.add(getDefaultValueForControl());
		return values;
	}

	/**
	 *
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
	 * @param selectedPermissibleValues
	 * @return
	 */
	@Override
    public List<SkipLogicAttributeInterface> getNonReadOnlySkipLogicAttributes(
			final List<PermissibleValueInterface> selectedPermissibleValues,
			final AttributeMetadataInterface attributeMetadataInterface)
	{
		final List<SkipLogicAttributeInterface> skipLogicAttributes = new ArrayList<SkipLogicAttributeInterface>();
		final String checked = getDefaultValueForControl();
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
	@Override
    public List<SkipLogicAttributeInterface> getReadOnlySkipLogicAttributes(
			List<PermissibleValueInterface> selectedPermissibleValues,
			final AttributeMetadataInterface attributeMetadataInterface)
	{
		final List<SkipLogicAttributeInterface> skipLogicAttributes = new ArrayList<SkipLogicAttributeInterface>();
		final String checked = getDefaultValueForControl();
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
	@Override
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
	@Override
    public boolean getIsEnumeratedControl()
	{
		return false;
	}
}