
package edu.common.dynamicextensions.domain.userinterface;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.MultiSelectCheckBoxInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.beans.NameValueBean;

/**
 * This class is used to create new control of multiselectCheckBox type
 * @author suhas_khot
 * @version 1.3
 * @created 09-Oct-2009 12:22:00 PM
 * @hibernate.joined-subclass table="DYEXTN_MULTISELECT_CHECK_BOX"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class MultiSelectCheckBox extends SelectControl implements MultiSelectCheckBoxInterface
{

	/**
	 * Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * check if checkBox is of multiSelect type or not
	 */
	private Boolean isMultiSelect = false;

	/**
	 * @return the listOfValues
	 */
	public List getListOfValues()
	{
		return listOfValues;
	}

	/**
	 * @param listOfValues the listOfValues to set
	 */
	public void setListOfValues(List listOfValues)
	{
		this.listOfValues = listOfValues;
	}

	/**
	 * This method returns whether the multiSelectCheckBox has a multiselect property or not.
	 * @hibernate.property name="isMultiSelect" type="boolean" column="MULTISELECT"
	 * @return whether the multiSelectCheckBox has a multiselect property or not.
	 */
	public Boolean getIsMultiSelect()
	{
		return isMultiSelect;
	}

	/**
	 * @param isMultiSelect the isMultiSelect to set
	 */
	public void setIsMultiSelect(Boolean isMultiSelect)
	{
		this.isMultiSelect = isMultiSelect;
	}

	/**
	 * List of values for multiSelectCheckBox.
	 */
	private List listOfValues = null;

	/**
	 * This method generates the HTML code to display the MultiselectCheckBox Control on the form.
	 * @return HTML code for MultiselectCheckBox Control.
	 * @throws DynamicExtensionsSystemException
	 */
	protected String generateEditModeHTML(ContainerInterface container) throws DynamicExtensionsSystemException
	{
		String htmlString = "";
		List<NameValueBean> nameValueBeans = null;
		List<String> values = getValueAsStrings();
		String disabled = "";
		//		If control is defined as readonly through category CSV file,make it Disabled
		if ((isReadOnly != null && getIsReadOnly())
				|| (isSkipLogicReadOnly != null && isSkipLogicReadOnly))
		{
			disabled = ProcessorConstants.DISABLED;
		}
		String htmlComponentName = getHTMLComponentName();
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
		if (getIsSkipLogicTargetControl())
		{
			htmlString += "<div id='" + getHTMLComponentName() + "_div' name='"
					+ getHTMLComponentName() + "_div'>";
		}
		if (listOfValues == null)
		{
			List<String> sourceControlValues = null;
			if (getSourceSkipControl() != null)
			{
				sourceControlValues = getSourceSkipControl().getValueAsStrings();
			}
			nameValueBeans = ControlsUtility.populateListOfValues(this, sourceControlValues);
		}

		if (nameValueBeans != null && !nameValueBeans.isEmpty())
		{
			for (NameValueBean nameValueBean : nameValueBeans)
			{
				if (values != null && !values.isEmpty()
						&& values.contains(nameValueBean.getValue()))
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
							+ " value='"
							+ nameValueBean.getValue()
							+ "' "
							+ "id='"
							+ nameValueBean.getValue()
							+ "'"
							+ " checked"
							+ disabled
							+ " onchange='isDataChanged();' ondblclick=\"changeValueForAllCheckBoxes(this);"
							+ (isSkipLogic ? "getSkipLogicControl('" + htmlComponentName
									+ "','" + identifier + "','" + parentContainerId + "');" : "")
							+ "\" onclick=\"changeValueForMultiSelectCheckBox(this);"
							+ (isSkipLogic ? "getSkipLogicControl('" + htmlComponentName
									+ "','" + identifier + "','" + parentContainerId + "');" : "")
							+ "\" /><img src='images/de/spacer.gif' width='2' height='2'>"
							+ "<label for=\"" + htmlComponentName + "\">" + DynamicExtensionsUtility.getUnEscapedStringValue(nameValueBean.getName())
							+ "</label> <img src='images/de/spacer.gif' width='3' height='3'>";
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
							+ " value='"
							+ DynamicExtensionsUtility.getValueForCheckBox(false)
							+ "' "
							+ disabled
							+ "id='"
							+ nameValueBean.getValue()
							+ "'"
							+ " onchange='isDataChanged();' ondblclick=\"changeValueForAllCheckBoxes(this);"
							+ (isSkipLogic ? "getSkipLogicControl('" + htmlComponentName
									+ "','" + identifier + "','" + parentContainerId + "');" : "")
							+ "\" onclick=\"changeValueForMultiSelectCheckBox(this);"
							+ (isSkipLogic ? "getSkipLogicControl('" + htmlComponentName
									+ "','" + identifier + "','" + parentContainerId + "');" : "")
							+ "\" /><img src='images/de/spacer.gif' width='2' height='2'>"
							+ "<label for=\"" + htmlComponentName + "\">" + nameValueBean.getName()
							+ "</label> <img src='images/de/spacer.gif' width='3' height='3'>";
				}
			}
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
	 * This method generates the HTML code in view mode to display the MultiselectCheckBox Control on the form.
	 * @return HTML code for MultiselectCheckBox Control.
	 * @throws DynamicExtensionsSystemException
	 */
	protected String generateViewModeHTML(ContainerInterface container) throws DynamicExtensionsSystemException
	{
		List<String> selectedOptions = new ArrayList<String>();

		AssociationInterface association = getBaseAbstractAttributeAssociation();
		if (association == null)
		{
			if (!(value instanceof List) && value != null)
			{
				List<String> options = new ArrayList<String>();
				options.add((String) value);
				selectedOptions = options;
			}
			else
			{
				selectedOptions = (List<String>) value;
			}
		}
		else
		{
			getValueList(association, selectedOptions);
		}

		StringBuffer htmlString = new StringBuffer();
		if (value != null)
		{
			htmlString.append("<span class = 'font_bl_s'>");
			for (String options : selectedOptions)
			{
				htmlString.append(options);
				htmlString.append("<br>");
			}
			htmlString.append("</span>");
		}
		return htmlString.toString();
	}

	/**
	 * setValues for a control
	 */
	public void setValueAsStrings(List<String> listOfValues)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * get all permissible values for this control
	 * @param rowId
	 * @return List of  all permissible values for this control
	 */
	public List<String> getValueAsStrings()
	{
		List<String> values = new ArrayList<String>();
		AssociationInterface association = getBaseAbstractAttributeAssociation();
		if (association == null)
		{
			if (!(value instanceof List) && value != null)
			{
				List<String> temp = new ArrayList<String>();
				temp.add((String) value);
				values = temp;
			}
			else
			{
				values = (List<String>) value;
			}
		}
		else
		{
			getValueList(association, values);
		}
		if (!getIsSkipLogicDefaultValue())
		{
			if (values == null || values.isEmpty())
			{
				String defaultValue = null;
				values = new ArrayList<String>();
				AttributeMetadataInterface attrMetadataInterface = getAttibuteMetadataInterface();
				if (attrMetadataInterface != null)
				{
					if (attrMetadataInterface instanceof CategoryAttributeInterface)
					{
						AbstractAttributeInterface abstractAttributeInterface = ((CategoryAttributeInterface) attrMetadataInterface)
								.getAbstractAttribute();
						if (abstractAttributeInterface instanceof AttributeInterface)
						{
							defaultValue = attrMetadataInterface.getDefaultValue();
						}
					}
					else
					{
						defaultValue = attrMetadataInterface.getDefaultValue();
					}
					if (defaultValue != null && defaultValue.trim().length() != 0)
					{
						values.add(defaultValue);
					}
				}
			}
		}
		else
		{
			if (values != null && values.isEmpty())
			{
				values.add(getSkipLogicDefaultValue());
			}
		}
		return values;
	}

	/**
	 *
	 * @return
	 */
	public AssociationInterface getBaseAbstractAttributeAssociation()
	{
		AssociationInterface associationInterface = null;
		if (baseAbstractAttribute instanceof AssociationInterface)
		{
			associationInterface = (AssociationInterface) baseAbstractAttribute;
		}
		else if (baseAbstractAttribute instanceof CategoryAttributeInterface)
		{
			CategoryAttributeInterface categoryAttr = (CategoryAttributeInterface) baseAbstractAttribute;
			AbstractAttributeInterface abstractAttr = categoryAttr.getAbstractAttribute();
			if (abstractAttr instanceof AssociationInterface)
			{
				associationInterface = (AssociationInterface) abstractAttr;
			}
		}
		return associationInterface;
	}
	/**
	 *
	 */
	public boolean getIsEnumeratedControl()
	{
		return true;
	}
}
