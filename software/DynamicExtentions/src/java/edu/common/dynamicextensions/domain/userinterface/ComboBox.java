
package edu.common.dynamicextensions.domain.userinterface;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ComboBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.wustl.common.beans.NameValueBean;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:07 PM
 * @hibernate.joined-subclass table="DYEXTN_COMBOBOX"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class ComboBox extends SelectControl implements ComboBoxInterface
{

	/**
	 * Serial Version Unique Identifier.
	 */
	private static final long serialVersionUID = 3062212342005513616L;

	/**
	 * List of Choices.
	 */
	List listOfValues = null;

	/**
	 * Size of the text field to be shown on UI.
	 */
	protected Integer columns;

	/**
	 * @hibernate.property name="columns" type="integer" column="NO_OF_COLUMNS"
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
	 * This method generates the HTML code for ComboBox control on the HTML form
	 * @return HTML code for ComboBox
	 * @throws DynamicExtensionsSystemException if HTMLComponentName() fails.
	 */
	public String generateEditModeHTML(ContainerInterface container) throws DynamicExtensionsSystemException
	{
		String defaultValue = getDefaultValueForControl();
		int columnSize = 0;
		if(columns!=null)
		{
			columnSize = columns.intValue();
		}
		String isDisabled = "";
		String htmlString = "";
		if ((this.isReadOnly != null && this.isReadOnly) || (this.isSkipLogicReadOnly != null && this.isSkipLogicReadOnly))
		{
			isDisabled = ",disabled:'" + ProcessorConstants.TRUE + "'";
		}
		String htmlComponentName = getHTMLComponentName();
		StringBuffer sourceHtmlComponentValues = null;
		if (this.getSourceSkipControl()!= null)
		{
			sourceHtmlComponentValues = new StringBuffer();
			List<String> sourceControlValues = this.getSourceSkipControl().getValueAsStrings();
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
			sourceHtmlComponentValues = new StringBuffer("~");
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
		if (getIsSkipLogicTargetControl() || this.getParentContainer().isAjaxRequest())
		{
			htmlString += "<input type='hidden' name='skipLogicControl' id='skipLogicControl' value = '"
					+ getHTMLComponentName()
					+ "_div' />"
					+ "<input type='hidden' name='skipLogicControlScript' id='skipLogicControlScript' value = 'comboScript_"
					+ getHTMLComponentName()
					+ "' /><div id='"
					+ getHTMLComponentName()
					+ "_div' name='"
					+ getHTMLComponentName() + "_div'>";
		}
		/* Bug Id:9030
		 * textComponent is the name of the text box.
		 * if default value is not empty loading the data store first, and then setting the value in
		 * combo box to default value.
		 */
		String textComponent = "combo" + htmlComponentName;
		if((!this.getParentContainer().isAjaxRequest()) && (!getIsSkipLogicTargetControl() && !"skipLogicAttributes".equals(getDataEntryOperation())))
		{
			htmlString += "<script defer='defer'>Ext.onReady(function(){ "
					+ "var myUrl= \"DEComboDataAction.do?controlId="
					+ identifier
					+ "~containerIdentifier="
					+ parentContainerId
					+ "~sourceControlValues="
					+ sourceHtmlComponentValues.toString()
					+ "\";"
					+ "var ds = new Ext.data.Store({"
					+ "proxy: new Ext.data.HttpProxy({url: myUrl}),"
					+ "reader: new Ext.data.JsonReader({root: 'row',totalProperty: 'totalCount',id: 'id'}, "
					+ "[{name: 'id', mapping: 'id'},{name: 'excerpt', mapping: 'field'}])});"
					+ "var combo = new Ext.form.ComboBox({store: ds,"
					+ "hiddenName: '"
					+ textComponent
					+ "',displayField:'excerpt',valueField: 'id',"
					+ "typeAhead: 'false',pageSize:15,forceSelection: 'true',queryParam : 'query',"
					+ "mode: 'remote',triggerAction: 'all',minChars : 3,queryDelay:500,lazyInit:true"
					+ isDisabled
					+ ",emptyText:\""
					+ defaultValue
					+ "\",valueNotFoundText:'',"
					+ "selectOnFocus:'true',applyTo: '"
					+ htmlComponentName
					+ "'});combo.on(\"select\", function() {"
					+ (this.isSkipLogic ? "getSkipLogicControl('"
							+ htmlComponentName + "','" + identifier + "','"
							+ parentContainerId + "');isDataChanged();"
							: "isDataChanged();")
					+ "}), combo.on(\"expand\", function() {if(Ext.isIE || Ext.isIE7){combo.list.setStyle(\"width\", \"210\");combo.innerList.setStyle(\"width\", \"210\");}else{combo.list.setStyle(\"width\", \"auto\");combo.innerList.setStyle(\"width\", \"auto\");}}, {single: true});";

			htmlString = htmlString
					+ "ds.on('load',function(){if (this.getAt(0) != null && this.getAt(0).get('excerpt').toLowerCase().startsWith(combo.getRawValue().toLowerCase())) {combo.typeAheadDelay=50;} else {combo.typeAheadDelay=60000}});";

			htmlString = htmlString + "});</script>";
		}
		htmlString += "<div style='float:left' id='auto_complete_dropdown'>"
				+ "<input type='text' onmouseover=\"showToolTip('"
				+ htmlComponentName
				+ "')\" id='"
				+ htmlComponentName
				+ "' "
				+ " name='"
				+ htmlComponentName
				+ "' value =\""
				+ defaultValue
				+ "\" "
				+ "' style='width:"+(columnSize > 0?(columnSize+1):(Constants.DEFAULT_COLUMN_SIZE+1))+"ex' size='"+(columnSize > 0?columnSize:Constants.DEFAULT_COLUMN_SIZE)+"'/>"
				+ "<div id='comboScript_"
				+ getHTMLComponentName()
				+ "' name='comboScript_"
				+ getHTMLComponentName()
				+ "' style='display:none'>"
				+ "Ext.onReady(function(){ "
				+ "var myUrl=\"DEComboDataAction.do?controlId="
				+ identifier
				+ "~containerIdentifier="
				+ parentContainerId
				+ "~sourceControlValues="
				+ sourceHtmlComponentValues.toString()
				+ "\";var ds = new Ext.data.Store({"
				+ "proxy: new Ext.data.HttpProxy({url: myUrl}),"
				+ "reader: new Ext.data.JsonReader({root: 'row',totalProperty: 'totalCount',id: 'id'}, "
				+ "[{name: 'id', mapping: 'id'},{name: 'excerpt', mapping: 'field'}])});"
				+ "var combo = new Ext.form.ComboBox({store: ds,"
				+ "hiddenName: '"
				+ textComponent
				+ "',displayField:'excerpt',valueField: 'id',"
				+ "typeAhead: 'false',pageSize:15,forceSelection: 'true',queryParam : 'query',"
				+ "mode: 'remote',triggerAction: 'all',minChars : 3,queryDelay:500,lazyInit:true"
				+ isDisabled
				+ ",emptyText:\""
				+ defaultValue
				+ "\",valueNotFoundText:'',"
				+ "selectOnFocus:'true',applyTo: '"
				+ htmlComponentName
				+ "'});combo.on(\"select\", function() {"
				+ (this.isSkipLogic ? "getSkipLogicControl('"
						+ htmlComponentName + "','" + identifier + "','"
						+ parentContainerId + "');isDataChanged();"
						: "isDataChanged();")
				+ "}), combo.on(\"expand\", function() {if(Ext.isIE || Ext.isIE7){combo.list.setStyle(\"width\", \"210\");combo.innerList.setStyle(\"width\", \"210\");}else{combo.list.setStyle(\"width\", \"auto\");combo.innerList.setStyle(\"width\", \"auto\");}}, {single: true});"
				+ "ds.on('load',function(){if (this.getAt(0) != null) {if (this.getAt(0).get('excerpt').toLowerCase().startsWith(combo.getRawValue().toLowerCase())) {combo.typeAheadDelay=50} else {combo.typeAheadDelay=60000}}});"
				+ "});"
				+ "</div>"
				+ "<div name=\"comboHtml\" style='display:none'>"
				+ "<div style='float:left'>"
				+ "<input type='text' onmouseover=\"showToolTip('"
				+ htmlComponentName
				+ "')\" id='"
				+ htmlComponentName
				+ "' "
				+ " name='"
				+ htmlComponentName
				+ "' value =\""
				+ defaultValue
				+ "\" style='width:"+(columnSize > 0?(columnSize+1):(Constants.DEFAULT_COLUMN_SIZE+1))+"ex' size='"+(columnSize > 0?columnSize:Constants.DEFAULT_COLUMN_SIZE)+"' class='font_bl_nor' />"
				+ "</div>"
				+ "</div>"
				+ "</div>";
		if (getIsSkipLogicTargetControl() || this.getParentContainer().isAjaxRequest())
		{
			htmlString += "</div>";
		}
		return htmlString;
	}



	/**
	 * This method returns the list of values that are displayed as choices.
	 * @return the list of values that are displayed as choices.
	 */
	public List getChoiceList()
	{
		return listOfValues;
	}

	/**
	 * This method sets the list of values that are displayed as choices.
	 * @param choiceList the List of values that is to set as ChoiceList.
	 */
	public void setChoiceList(List choiceList)
	{
		listOfValues = choiceList;
	}

	protected String generateViewModeHTML(ContainerInterface container) throws DynamicExtensionsSystemException
	{
		String htmlString = "&nbsp;";

		String defaultValue = "";
		if (this.value != null)
		{
			if (this.value instanceof String)
			{
				defaultValue = (String) this.value;
			}
			else if (this.value instanceof List)
			{
				List valueList = (List) this.value;
				if (!valueList.isEmpty())
				{
					defaultValue = valueList.get(0).toString();
				}
			}
		}

		List<NameValueBean> nameValueBeanList = null;
		if (listOfValues == null)
		{
			List<String> sourceControlValues = null;
			if (this.getSourceSkipControl() != null)
			{
				sourceControlValues = this.getSourceSkipControl().getValueAsStrings();
			}
			nameValueBeanList = ControlsUtility.populateListOfValues(this,sourceControlValues);
		}

		if (nameValueBeanList != null && !nameValueBeanList.isEmpty())
		{
			for (NameValueBean nameValueBean : nameValueBeanList)
			{
				if (nameValueBean.getValue().equals(defaultValue))
				{
					htmlString = "<span class='font_bl_s'> " + nameValueBean.getName() + "</span>";
					break;
				}
			}
		}

		return htmlString;
	}
	/**
	 *
	 * @return
	 */
	private String getDefaultValueForControl()
	{
		String defaultValue = "";
		if (!getIsSkipLogicDefaultValue())
		{
			if (this.value == null)
			{
				AttributeMetadataInterface attributeMetadataInterface = this
						.getAttibuteMetadataInterface();
				if (attributeMetadataInterface != null)
				{
					this.value = this.getAttibuteMetadataInterface().getDefaultValue();
				}
			}
			if (this.value != null)
			{
				if (this.value instanceof String)
				{
					defaultValue = (String) this.value;
				}
				else if (this.value instanceof List)
				{
					List valueList = (List) this.value;
					if (!valueList.isEmpty())
					{
						defaultValue = valueList.get(0).toString();
					}
				}
			}
			else
			{
				defaultValue = "";
			}
		}
		else
		{
			String controlvalue = null;
			if (this.value != null)
			{
				if (this.value instanceof String)
				{
					controlvalue = (String) this.value;
				}
				else if (this.value instanceof List)
				{
					List valueList = (List) this.value;
					if (!valueList.isEmpty())
					{
						controlvalue = valueList.get(0).toString();
					}
				}
			}
			if (controlvalue == null || controlvalue.length() == 0)
			{
				defaultValue = getSkipLogicDefaultValue();
			}
			else
			{
				defaultValue = controlvalue;
			}
		}
		if(isInavlidVaue(defaultValue))
		{
			defaultValue = this.getAttibuteMetadataInterface().getDefaultValue();
			// Set default value to blank, if not set
			if(defaultValue == null)
			{
				defaultValue = "";
			}
		}
		return defaultValue;
	}

	/**
	 * @param value
	 * @return true if value is not present in the pv list
	 */
	private boolean isInavlidVaue(String value)
	{
		List<NameValueBean> nameValueBeans = new ArrayList<NameValueBean>();
		nameValueBeans = ControlsUtility.getListOfPermissibleValues(this.getAttibuteMetadataInterface());
		boolean isInavlidVaue = true;
		for(NameValueBean bean: nameValueBeans)
		{
			if(bean.getValue().equals(value))
			{
				isInavlidVaue = false;
				break;
			}
		}
		return isInavlidVaue;
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
		if (!listOfValues.isEmpty())
		{
			setValue(listOfValues.get(0));
		}
	}

}