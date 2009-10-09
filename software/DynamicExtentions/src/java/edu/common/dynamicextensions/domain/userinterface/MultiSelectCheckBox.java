package edu.common.dynamicextensions.domain.userinterface;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
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
	List listOfValues = null;
	

	/**
	 * This method generates the HTML code to display the MultiselectCheckBox Control on the form.
	 * @return HTML code for MultiselectCheckBox Control.
	 * @throws DynamicExtensionsSystemException
	 */
	@Override
	protected String generateEditModeHTML(Integer rowId) throws DynamicExtensionsSystemException
	{
		String htmlString = "";
		List<NameValueBean> nameValueBeans = null;
		List<String> values = getValueAsStrings(rowId);
		String disabled = "";
		//		If control is defined as readonly through category CSV file,make it Disabled
		if ((this.isReadOnly != null && getIsReadOnly()) || (this.isSkipLogicReadOnly != null && this.isSkipLogicReadOnly))
		{
			disabled = ProcessorConstants.DISABLED;
		}
		String htmlComponentName = getHTMLComponentName();

		if (listOfValues == null)
		{
			nameValueBeans = ControlsUtility.populateListOfValues(this,rowId);
		}
		
		if (nameValueBeans != null && !nameValueBeans.isEmpty())
		{
			for (NameValueBean nameValueBean : nameValueBeans)
			{
				if (values != null && !values.isEmpty()
						&& values.contains(nameValueBean.getValue()))
				{
					htmlString += "<input type='checkbox' class='" + this.cssClass + "' name='"
					+ htmlComponentName + "' checkedValue='"
					+ DynamicExtensionsUtility.getValueForCheckBox(true) + "' uncheckedValue='"
					+ DynamicExtensionsUtility.getValueForCheckBox(false) + "'" + " value='"
					+ nameValueBean.getValue() + "' " + "id='"
					+ nameValueBean.getValue() + "'" + " checked" + disabled
					+ " onchange='isDataChanged();' ondblclick='changeValueForAllCheckBoxes(this);' onclick='changeValueForMultiSelectCheckBox(this);' /><img src='images/de/spacer.gif' width='2' height='2'>"+"<label for=\""+htmlComponentName+"\">"+nameValueBean.getName()+"</label> <img src='images/de/spacer.gif' width='3' height='3'>";
				}
				else
				{
					htmlString += "<input type='checkbox' class='" + this.cssClass + "' name='"
					+ htmlComponentName + "' checkedValue='"
					+ DynamicExtensionsUtility.getValueForCheckBox(true) + "' uncheckedValue='"
					+ DynamicExtensionsUtility.getValueForCheckBox(false) + "'" + " value='"
					+ DynamicExtensionsUtility.getValueForCheckBox(false) + "' "+ disabled + "id='"
					+ nameValueBean.getValue() + "'"
					+ " onchange='isDataChanged();' ondblclick='changeValueForAllCheckBoxes(this);' onclick='changeValueForMultiSelectCheckBox(this);' /><img src='images/de/spacer.gif' width='2' height='2'>"+"<label for=\""+htmlComponentName+"\">"+nameValueBean.getName()+"</label> <img src='images/de/spacer.gif' width='3' height='3'>";
				}
			}
		}
		return htmlString.toString();
	}

	/**
	 * This method generates the HTML code in view mode to display the MultiselectCheckBox Control on the form.
	 * @return HTML code for MultiselectCheckBox Control.
	 * @throws DynamicExtensionsSystemException
	 */
	@Override
	protected String generateViewModeHTML(Integer rowId) throws DynamicExtensionsSystemException
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
				selectedOptions = (List<String>) this.value;
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
	 * get all permissible values for this control
	 * @param rowId
	 * @return List of  all permissible values for this control
	 */
	@Override
	public List<String> getValueAsStrings(Integer rowId)
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
				values = (List<String>) this.value;
			}
		}
		else
		{
			getValueList(association, values);
		}
		if (!getIsSkipLogicTargetControl())
		{
			if (values == null || values.isEmpty())
			{
				String defaultValue = null;
				values = new ArrayList<String>();
	
				AttributeMetadataInterface attributeMetadata = this.getAttibuteMetadataInterface();
				if (attributeMetadata != null)
				{
					if (attributeMetadata instanceof CategoryAttributeInterface)
					{
						AbstractAttributeInterface abstractAttribute = ((CategoryAttributeInterface) attributeMetadata)
								.getAbstractAttribute();
						if (abstractAttribute instanceof AttributeInterface)
						{
							defaultValue = attributeMetadata.getDefaultValue();
						}
					}
					else
					{
						defaultValue = attributeMetadata.getDefaultValue();
					}
	
					if (defaultValue != null && defaultValue.trim().length() != 0)
					{
						values.add(defaultValue);
					}
				}
			}
		}
		return values;
	}

	/**
	 * setValues for a control
	 */
	@Override
	public void setValueAsStrings(List<String> listOfValues)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 *
	 * @return
	 */
	public AssociationInterface getBaseAbstractAttributeAssociation()
	{
		AssociationInterface associationInt = null;
		if (baseAbstractAttribute instanceof AssociationInterface)
		{
			associationInt = (AssociationInterface) baseAbstractAttribute;
		}
		else if (baseAbstractAttribute instanceof CategoryAttributeInterface)
		{
			CategoryAttributeInterface categoryAttr = (CategoryAttributeInterface) baseAbstractAttribute;
			AbstractAttributeInterface abstractAttr = categoryAttr.getAbstractAttribute();
			if (abstractAttr instanceof AssociationInterface)
			{
				associationInt = (AssociationInterface) abstractAttr;
			}
		}
		return associationInt;
	}
}
