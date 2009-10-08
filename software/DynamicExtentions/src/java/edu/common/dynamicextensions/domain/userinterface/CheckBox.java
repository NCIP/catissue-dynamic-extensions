
package edu.common.dynamicextensions.domain.userinterface;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
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
	protected String generateEditModeHTML(Integer rowId) throws DynamicExtensionsSystemException
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
	protected String generateViewModeHTML(Integer rowId) throws DynamicExtensionsSystemException
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
		if (!getIsSkipLogicTargetControl())
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
	 */
	public List<ControlInterface> getSkipLogicControls(
			List<PermissibleValueInterface> selectedPermissibleValues,Integer rowId,List<String> values) 
	{
		List<ControlInterface> skipLogicControls = new ArrayList<ControlInterface>();
		if (isSkipLogic) 
		{
			AttributeMetadataInterface attributeMetadataInterface = ControlsUtility
					.getAttributeMetadataInterface(getBaseAbstractAttribute());
			String checked = getDefaultValueForControl(rowId);
			
			List<SkipLogicAttributeInterface> skipLogicAttributes = ControlsUtility
					.getSkipLogicAttributesForCheckBox(
							attributeMetadataInterface);
			for (SkipLogicAttributeInterface skipLogicAttributeInterface : skipLogicAttributes) 
			{
				ContainerInterface targetContainerInterface = DynamicExtensionsUtility
						.getContainerForAbstractEntity(skipLogicAttributeInterface
								.getTargetSkipLogicAttribute()
								.getCategoryEntity());
				ControlInterface targetControl = DynamicExtensionsUtility
						.getControlForAbstractAttribute(
								(AttributeMetadataInterface) skipLogicAttributeInterface
										.getTargetSkipLogicAttribute(),
								targetContainerInterface);
				ContainerInterface sourceContainerInterface = DynamicExtensionsUtility
						.getContainerForAbstractEntity(skipLogicAttributeInterface
								.getSourceSkipLogicAttribute()
								.getCategoryEntity());
				ControlInterface sourceControl = DynamicExtensionsUtility
						.getControlForAbstractAttribute(
								(AttributeMetadataInterface) skipLogicAttributeInterface
										.getSourceSkipLogicAttribute(),
								sourceContainerInterface);
				if (checked != null)
				{
					if (checked.equalsIgnoreCase("true") || checked.equals("1")|| checked.equals("y") || checked.equals("yes"))
					{
						targetControl.setIsSkipLogicReadOnly(Boolean.valueOf(false));
					}
					else
					{
						targetControl.setIsSkipLogicReadOnly(Boolean.valueOf(true));
					}
				}
				DataElementInterface dataElementInterface = skipLogicAttributeInterface
						.getDataElement();
				UserDefinedDEInterface userDefinedDEInterface = null;
				if (dataElementInterface instanceof UserDefinedDEInterface) 
				{
					userDefinedDEInterface = (UserDefinedDEInterface) dataElementInterface;
				}
				if (userDefinedDEInterface != null
						&& userDefinedDEInterface
								.getPermissibleValueCollection() != null
						&& !userDefinedDEInterface
								.getPermissibleValueCollection().isEmpty()) 
				{
					targetControl.setIsSkipLogicLoadPermValues(Boolean
							.valueOf(true));
				}
				targetControl.setSourceSkipControl(sourceControl);
				if (!sourceControl.getParentContainer().equals(targetControl.getParentContainer()))
				{
					rowId = Integer.valueOf(-1);
				}
				targetControl
						.setIsSkipLogicTargetControl(Boolean.valueOf(true));
				if (rowId != null)
				{
					targetControl.addSourceSkipControlValue(rowId, values);
				}
				skipLogicControls.add(targetControl);
			}
		}
		return skipLogicControls;
	}

	/**
	 * 
	 */
	public List<ControlInterface> setSkipLogicControls(Integer rowId,String[] valueArray)
	{
		List<ControlInterface> controlList = null;
		List<String> values = new ArrayList <String>();
		for (String controlValue : valueArray)
		{
			values.add(controlValue);
		}
		controlList = setSkipLogicControlValues(rowId,values);
		return controlList;
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