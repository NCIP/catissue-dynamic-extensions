
package edu.common.dynamicextensions.domain.userinterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.common.dynamicextensions.category.beans.UIProperty;
import edu.common.dynamicextensions.category.enums.ListBoxEnum;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ListBoxInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
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
	private final List listOfValues = null;

	/**
	 * This method returns the Number of rows to be displayed on the UI for ListBox.
	 * @hibernate.property name="noOfRows" type="integer" column="NO_OF_ROWS"
	 * @return Returns the noOfRows.
	 */
	public Integer getNoOfRows()
	{
		return noOfRows;
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
	public String generateEditModeHTML(ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		StringBuffer htmlString = new StringBuffer("");
		List<NameValueBean> nameValueBeans = null;
		List<String> values = getValueAsStrings();
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

		String htmlComponentName = getHTMLComponentName();
		StringBuffer sourceHtmlComponentValues = null;
		if (getSourceSkipControl() != null)
		{
			sourceHtmlComponentValues = new StringBuffer();
			List<String> sourceControlValues = getSourceSkipControl().getValueAsStrings();
			if (sourceControlValues != null)
			{
				for (String value : sourceControlValues)
				{
					sourceHtmlComponentValues.append(value);
					sourceHtmlComponentValues.append('~');
				}
			}
		}
		else
		{
			sourceHtmlComponentValues = new StringBuffer('~');
		}

		String strMultiSelect = "";
		if ((isMultiSelect != null) && (isMultiSelect.booleanValue()))
		{
			strMultiSelect = "MULTIPLE ";
		}

		if (getIsSkipLogicTargetControl())
		{
			htmlString.append("<div id='" + getHTMLComponentName() + "_div' name='"
					+ getHTMLComponentName() + "_div'>");
		}
		htmlString.append("<SELECT ");
		htmlString.append(strMultiSelect).append(" size=").append(noOfRows).append(
				" class='font_bl_s' name='").append(getHTMLComponentName()).append("' onchange=\"");
		if (isSkipLogic != null && isSkipLogic)
		{
			htmlString.append("getSkipLogicControl('");
			htmlString.append(htmlComponentName);
			htmlString.append("','");
			htmlString.append(identifier);
			htmlString.append("','");
			htmlString.append(parentContainerId);
			htmlString.append("');");
		}
		htmlString.append("isDataChanged();\" id='").append(htmlComponentName).append("' ");

		if ((isReadOnly != null && isReadOnly)
				|| (isSkipLogicReadOnly != null && isSkipLogicReadOnly))
		{
			htmlString.append(" disabled='").append(ProcessorConstants.TRUE).append("' ");
		}
		htmlString.append('>');

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
				if (values != null
						&& !values.isEmpty()
						&& (values.contains(nameValueBean.getValue()) || values
								.contains(DynamicExtensionsUtility
										.getUnEscapedStringValue(nameValueBean.getValue()))))
				{
					htmlString.append("<OPTION VALUE='").append(nameValueBean.getValue()).append(
							"' SELECTED>").append(
							DynamicExtensionsUtility.getUnEscapedStringValue(nameValueBean
									.getName()));
				}
				else
				{
					htmlString.append("<OPTION VALUE='").append(nameValueBean.getValue()).append(
							"'>").append(
							DynamicExtensionsUtility.getUnEscapedStringValue(nameValueBean
									.getName()));
				}
			}
		}
		htmlString.append("</SELECT>");

		String attributeName = ((Control) this).getCaption();

		// generate this type of html if multiselect is to be implemented
		// using an auto complete drop down and list box together.
		if (IsUsingAutoCompleteDropdown != null && IsUsingAutoCompleteDropdown)
		{
			String coordId = "coord_" + getHTMLComponentName();
			String protocolCoordId = "protocolCoordId_" + getHTMLComponentName();

			StringBuffer multSelWithAutoCmpltHTML = new StringBuffer();
			if (getIsSkipLogicTargetControl())
			{
				multSelWithAutoCmpltHTML.append("<div id='" + getHTMLComponentName()
						+ "_div' name='" + getHTMLComponentName() + "_div'>");
			}
			multSelWithAutoCmpltHTML
					.append("<script defer='defer'>Ext.onReady(function(){var myUrl= \"DEComboDataAction.do?controlId= "
							+ identifier
							+ "~containerIdentifier="
							+ parentContainerId
							+ "~sourceControlValues="
							+ sourceHtmlComponentValues.toString()
							+ "~attributeName="
							+ attributeName
							+ "\";var ds = new Ext.data.Store({proxy: new Ext.data.HttpProxy({url: myUrl}),reader: new Ext.data.JsonReader({root: 'row',totalProperty: 'totalCount',id: 'id'}, [{name: 'id', mapping: 'id'},{name: 'excerpt', mapping: 'field'}])});var combo = new Ext.form.ComboBox({store: ds,hiddenName: 'CB_coord_"
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
			multSelWithAutoCmpltHTML
					.append("\t\t\t\t\t\t\t<a href=\"#\" onclick=\"isDataChanged();moveOptions('"
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
			multSelWithAutoCmpltHTML
					.append("\t\t\t\t\t\t\t<a href=\"#\" onclick=\"isDataChanged();moveOptions('"
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
		if (getIsSkipLogicTargetControl())
		{
			htmlString
					.append("<input type='hidden' name='skipLogicControl' id='skipLogicControl' value = '"
							+ getHTMLComponentName() + "_div' />");
			htmlString.append("</div>");
		}
		return htmlString.toString();
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domain.userinterface.Control#generateViewModeHTML()
	 */
	protected String generateViewModeHTML(ContainerInterface container)
			throws DynamicExtensionsSystemException
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
				selectedOptions = (List<String>) value;
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
				values = (List<String>) value;
			}
		}
		if (!getIsSkipLogicDefaultValue())
		{
			if (values == null || values.isEmpty())
			{
				String defaultValue = null;
				values = new ArrayList<String>();

				AttributeMetadataInterface attributeMetadata = getAttibuteMetadataInterface();
				if (attributeMetadata != null)
				{
//					if (attributeMetadata instanceof CategoryAttributeInterface)
					{
						defaultValue = attributeMetadata.getDefaultValue();
					}
//					else
//					{
//						defaultValue = attributeMetadata.getDefaultValue();
//					}

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
		return true;
	}

	/**
	 * Returns collection of key-value pairs.
	 */
	public Collection<UIProperty> getControlTypeValues()
	{
		Collection<UIProperty> uiProperties = super.getControlTypeValues();
		ListBoxEnum[] uiPropertyValues = ListBoxEnum.values();

		for (ListBoxEnum propertyType : uiPropertyValues)
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
			ListBoxEnum propertyType = ListBoxEnum.getValue(uiProperty.getKey());
			propertyType.setControlProperty(this, uiProperty.getValue());
		}
	}
}