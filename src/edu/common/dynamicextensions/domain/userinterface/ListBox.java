
package edu.common.dynamicextensions.domain.userinterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ListBoxInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.wustl.common.beans.NameValueBean;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:08 PM
 * @hibernate.joined-subclass table="DYEXTN_LIST_BOX"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class ListBox extends SelectControl implements ListBoxInterface
{

	/**
	 * Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Boolean indicating whether multi selects are allowed in the list box.
	 */
	private Boolean isMultiSelect = null;

	/**
	 * 
	 */
	private Boolean IsUsingAutoCompleteDropdown = null;

	/**
	 * Number of rows to be displayed on the UI for ListBox.
	 */
	private Integer noOfRows = null;

	/**
	 * The list of values to be displayed in the ListBox
	 */
	private List listOfValues = null;

	/**
	 * This method returns the Number of rows to be displayed on the UI for ListBox.
	 * @hibernate.property name="noOfRows" type="integer" column="NO_OF_ROWS"
	 * @return Returns the noOfRows.
	 */
	public Integer getNoOfRows()
	{
		return this.noOfRows;
	}

	/**
	 * This method sets the Number of rows to be displayed on the UI for ListBox.
	 * @param noOfRows the Number of rows to be set for ListBox.
	 */
	public void setNoOfRows(Integer noOfRows)
	{
		this.noOfRows = noOfRows;
	}

	/**
	 * This method returns whether the ListBox has a multiselect property or not.
	 * @hibernate.property name="isMultiSelect" type="boolean" column="MULTISELECT"
	 * @return whether the ListBox has a multiselect property or not.
	 */
	public Boolean getIsMultiSelect()
	{
		return isMultiSelect;
	}

	/**
	 * This method sets whether the ListBox has a multiselect property or not.
	 * @param isMultiSelect the Boolean value indicating whether the ListBox has a multiselect property or not.
	 */
	public void setIsMultiSelect(Boolean isMultiSelect)
	{
		this.isMultiSelect = isMultiSelect;
	}

	/**
	 * @hibernate.property name="IsUsingAutoCompleteDropdown" type="boolean" column="USE_AUTOCOMPLETE"
	 * @return
	 */
	public Boolean getIsUsingAutoCompleteDropdown()
	{
		return IsUsingAutoCompleteDropdown;
	}

	/**
	 * @param isUsingAutoCompleteDropdown
	 */
	public void setIsUsingAutoCompleteDropdown(Boolean isUsingAutoCompleteDropdown)
	{
		IsUsingAutoCompleteDropdown = isUsingAutoCompleteDropdown;
	}

	/**
	 * This method generates the HTML code to display the ListBox Control on the form.
	 * @return HTML code for ListBox Control.
	 * @throws DynamicExtensionsSystemException
	 */
	public String generateEditModeHTML() throws DynamicExtensionsSystemException
	{
		StringBuffer htmlString = new StringBuffer("");
		List<NameValueBean> nameValueBeans = null;
		List<String> values = getValueAsStrings();
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
		String htmlComponentName = getHTMLComponentName();
		String strMultiSelect = "";
		if ((isMultiSelect != null) && (isMultiSelect.booleanValue()))
		{
			strMultiSelect = "MULTIPLE ";
		}


		htmlString.append("<input type='hidden' name='skipLogicControl' id='skipLogicControl' value = '"
					+ getHTMLComponentName() + "_div' /><div id='"
					+ getHTMLComponentName() + "_div' name='"
					+ getHTMLComponentName() + "_div'>");

		htmlString.append("<SELECT ");
		htmlString.append(strMultiSelect).append(" size=")
				.append(this.noOfRows).append(" class='font_bl_s' name='")
				.append(getHTMLComponentName()).append(
						"' onchange='" + this.isSkipLogic != null
								&& this.isSkipLogic ? "getSkipLogicControl('"
								+ htmlComponentName + "','" + identifier
								+ "','" + parentContainerId + "');" : ""
								+ "isDataChanged();' id='").append(name)
				.append("' ");
		if ((this.isReadOnly != null && this.isReadOnly) || (this.isSkipLogicReadOnly != null && this.isSkipLogicReadOnly))
		{
			htmlString.append(" disabled='").append(ProcessorConstants.TRUE).append("' ");
		}
		htmlString.append('>');

		if (listOfValues == null)
		{
			nameValueBeans = ControlsUtility.populateListOfValues(this);
		}

		if (nameValueBeans != null && !nameValueBeans.isEmpty())
		{
			for (NameValueBean nameValueBean : nameValueBeans)
			{
				if (values != null && !values.isEmpty()
						&& values.contains(nameValueBean.getValue()))
				{
					htmlString.append("<OPTION VALUE='").append(nameValueBean.getValue()).append(
							"' SELECTED>").append(nameValueBean.getName());
				}
				else
				{
					htmlString.append("<OPTION VALUE='").append(nameValueBean.getValue()).append(
							"'>").append(nameValueBean.getName());
				}
			}
		}
		htmlString.append("</SELECT>");

		// generate this type of html if multiselect is to be implemented
		// using an auto complete drop down and list box together.
		if (IsUsingAutoCompleteDropdown != null && IsUsingAutoCompleteDropdown)
		{
			String coordId = "coord_" + getHTMLComponentName();
			String protocolCoordId = "protocolCoordId_" + getHTMLComponentName();

			StringBuffer multSelWithAutoCmpltHTML = new StringBuffer();
		
			multSelWithAutoCmpltHTML.append("<input type='hidden' name='skipLogicControl' id='skipLogicControl' value = '"
						+ getHTMLComponentName() + "_div' /><div id='"
						+ getHTMLComponentName() + "_div' name='"
						+ getHTMLComponentName() + "_div'>");
			
			multSelWithAutoCmpltHTML
					.append("<script defer='defer'>Ext.onReady(function(){var myUrl= 'DEComboDataAction.do?controlId= "
							+ identifier
							+ "~containerIdentifier="
							+ parentContainerId
							+ "';var ds = new Ext.data.Store({proxy: new Ext.data.HttpProxy({url: myUrl}),reader: new Ext.data.JsonReader({root: 'row',totalProperty: 'totalCount',id: 'id'}, [{name: 'id', mapping: 'id'},{name: 'excerpt', mapping: 'field'}])});var combo = new Ext.form.ComboBox({store: ds,hiddenName: 'CB_coord_"
							+ getHTMLComponentName()
							+ "',displayField:'excerpt',valueField: 'id',typeAhead: 'false',pageSize:15,forceSelection: 'true',queryParam : 'query',mode: 'remote',triggerAction: 'all',minChars : 3,queryDelay:500,lazyInit:true,emptyText:'--Select--',valueNotFoundText:'',selectOnFocus:'true',applyTo: '"
							+ coordId
							+ "'});combo.on(\"expand\", function() {if(Ext.isIE || Ext.isIE7){combo.list.setStyle(\"width\", \"210\");combo.innerList.setStyle(\"width\", \"210\");}else{combo.list.setStyle(\"width\", \"auto\");combo.innerList.setStyle(\"width\", \"auto\");}}, {single: true});ds.on('load',function(){if (this.getAt(0) != null && this.getAt(0).get('excerpt').toLowerCase().startsWith(combo.getRawValue().toLowerCase())) {combo.typeAheadDelay=50;} else {combo.typeAheadDelay=60000}});});</script>\n");

			multSelWithAutoCmpltHTML.append("<br><table border=\"0\" width=\"100%\">\n");
			multSelWithAutoCmpltHTML.append("\t<tr>\n");
			multSelWithAutoCmpltHTML
					.append("\t\t<td width=\"35%\" class=\"black_ar_new\" valign=\"TOP\">\n");
			multSelWithAutoCmpltHTML.append("\t\t\t<input type='text' id='" + coordId + "' name='"
					+ coordId + "' value =' ' size='20'/>\n");
			multSelWithAutoCmpltHTML.append("\t\t</td>\n\n");
			multSelWithAutoCmpltHTML
					.append("\t\t<td class=\"black_ar_new\" width=\"20%\" align=\"center\" valign=\"TOP\">\n");
			multSelWithAutoCmpltHTML
					.append("\t\t\t<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
			multSelWithAutoCmpltHTML.append("\t\t\t\t<tr>\n");
			multSelWithAutoCmpltHTML
					.append("\t\t\t\t\t<td height=\"22\" align=\"center\" valign=\"TOP\">\n");
			multSelWithAutoCmpltHTML.append("\t\t\t\t\t\t<div id=\"addLink\">\n");
			multSelWithAutoCmpltHTML.append("\t\t\t\t\t\t\t<a href=\"#\" onclick=\"isDataChanged();moveOptions('"
					+ coordId + "','" + protocolCoordId + "', 'add')\">\n");
			multSelWithAutoCmpltHTML
					.append("\t\t\t\t\t\t\t\t<img src=\"images/b_add_inact.gif\" alt=\"Add\" height=\"18\" border=\"0\" align=\"absmiddle\"/>\n");
			multSelWithAutoCmpltHTML.append("\t\t\t\t\t\t\t</a>\n");
			multSelWithAutoCmpltHTML.append("\t\t\t\t\t\t</div>\n");
			multSelWithAutoCmpltHTML.append("\t\t\t\t\t</td>\n");
			multSelWithAutoCmpltHTML.append("\t\t\t\t</tr>\n\n");
			multSelWithAutoCmpltHTML.append("\t\t\t\t<tr>\n");
			multSelWithAutoCmpltHTML.append("\t\t\t\t\t<td height=\"22\" align=\"center\">\n");
			multSelWithAutoCmpltHTML.append("\t\t\t\t\t\t<div id=\"removeLink\">\n");
			multSelWithAutoCmpltHTML.append("\t\t\t\t\t\t\t<a href=\"#\" onclick=\"isDataChanged();moveOptions('"
					+ protocolCoordId + "','" + coordId + "', 'edit')\">\n");
			multSelWithAutoCmpltHTML
					.append("\t\t\t\t\t\t\t\t<img src=\"images/b_remove_inact.gif\" alt=\"Remove\" height=\"18\" border=\"0\" align=\"absmiddle\"/>\n");
			multSelWithAutoCmpltHTML.append("\t\t\t\t\t\t\t</a>\n");
			multSelWithAutoCmpltHTML.append("\t\t\t\t\t\t</div>\n");
			multSelWithAutoCmpltHTML.append("\t\t\t\t\t</td>\n");
			multSelWithAutoCmpltHTML.append("\t\t\t\t</tr>\n");
			multSelWithAutoCmpltHTML.append("\t\t\t</table>\n");
			multSelWithAutoCmpltHTML.append("\t\t</td>\n\n");
			multSelWithAutoCmpltHTML
					.append("\t\t<td width=\"50%\" align=\"center\" class=\"black_ar_new\">\n");
			multSelWithAutoCmpltHTML.append("\t\t\t<SELECT id=\"" + protocolCoordId + "\" name=\""
					+ getHTMLComponentName()
					+ "\" size=\"4\" multiple=\"true\" style=\"width:170\">");

			if (nameValueBeans != null && !nameValueBeans.isEmpty())
			{
				for (NameValueBean nameValueBean : nameValueBeans)
				{
					if (values != null && !values.isEmpty()
							&& values.contains(nameValueBean.getValue()))
					{
						multSelWithAutoCmpltHTML.append("<OPTION VALUE='").append(
								nameValueBean.getValue()).append("' SELECTED>").append(
								nameValueBean.getName());
					}
				}
			}

			multSelWithAutoCmpltHTML.append("</SELECT>\n");
			multSelWithAutoCmpltHTML.append("\t\t</td>\n");
			multSelWithAutoCmpltHTML.append("\t</tr>\n");
			multSelWithAutoCmpltHTML.append("</table>");

			htmlString = multSelWithAutoCmpltHTML;
		}

			htmlString.append("</div>");

		return htmlString.toString();
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domain.userinterface.Control#generateViewModeHTML()
	 */
	protected String generateViewModeHTML() throws DynamicExtensionsSystemException
	{
		List<String> selectedOptions = new ArrayList<String>();

		AssociationInterface association = getBaseAbstractAttributeAssociation();
		if (association != null)
		{
			getValueList(association, selectedOptions);
		}
		else
		{
			if (!(value instanceof List) && value != null)
			{
				List<String> temp = new ArrayList<String>();
				temp.add((String) value);
				selectedOptions = temp;
			}
			else
			{
				selectedOptions = (List<String>) this.value;
			}
		}

		StringBuffer generatedHtml = new StringBuffer("&nbsp;");
		if (value != null)
		{
			generatedHtml = new StringBuffer();
			generatedHtml.append("<span class = 'font_bl_s'>");
			for (String string : selectedOptions)
			{
				generatedHtml.append(string);
				generatedHtml.append("<br>");
			}

			generatedHtml.append("</span>");
		}

		return generatedHtml.toString();
	}

	/**
	 * getValueList
	 * @param association
	 * @param valueList
	 */
	private void getValueList(AssociationInterface association, List<String> valueList)
	{
		if (association.getIsCollection())
		{
			Collection<AbstractAttributeInterface> attributes = association.getTargetEntity()
					.getAllAbstractAttributes();
			Collection<AbstractAttributeInterface> filteredAttributes = EntityManagerUtil
					.filterSystemAttributes(attributes);
			List<AbstractAttributeInterface> attributesList = new ArrayList<AbstractAttributeInterface>(
					filteredAttributes);
			List<Map> values = (List<Map>) this.value;
			if (values != null)
			{
				for (Map valueMap : values)
				{
					String value = (String) valueMap.get(attributesList.get(0));
					valueList.add(value);
				}
			}
		}
		else
		{
			if (!(value instanceof List) && value != null)
			{
				List<String> temp = new ArrayList<String>();
				temp.add((String) value);
				valueList = temp;
			}
			else
			{
				if (this.value != null)
				{
					for (Long obj : (List<Long>) this.value)
					{
						valueList.add(obj.toString());
					}
				}
			}
		}
		
	}

	/**
	 *
	 * @return
	 */
	public AssociationInterface getBaseAbstractAttributeAssociation()
	{
		AssociationInterface association = null;
		if (baseAbstractAttribute instanceof AssociationInterface)
		{
			association = (AssociationInterface) baseAbstractAttribute;
		}
		else if (baseAbstractAttribute instanceof CategoryAttributeInterface)
		{
			CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface) baseAbstractAttribute;
			AbstractAttributeInterface abstractAttribute = categoryAttribute.getAbstractAttribute();
			if (abstractAttribute instanceof AssociationInterface)
			{
				association = (AssociationInterface) abstractAttribute;
			}
		}

		return association;
	}
	/**
	 * 
	 */
	public List<String> getValueAsStrings() 
	{
		List<String> values = new ArrayList<String>();
		AssociationInterface association = getBaseAbstractAttributeAssociation();
		if (association != null)
		{
			getValueList(association, values);
		}
		else
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
		return values;
	}

	/**
	 * 
	 */
	public void setValueAsStrings(List<String> listOfValues) 
	{
		// TODO Auto-generated method stub
		
	}
}