
package edu.common.dynamicextensions.domain.userinterface;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.common.dynamicextensions.category.beans.UIProperty;
import edu.common.dynamicextensions.category.enums.ComboBoxEnum;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ComboBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.beans.NameValueBean;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:07 PM
 * @hibernate.joined-subclass table="DYEXTN_COMBOBOX"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class ComboBox extends SelectControl implements ComboBoxInterface
{

	/** Serial Version Unique Identifier. */
	private static final long serialVersionUID = 3062212342005513616L;

	/**
	 * List of Choices.
	 */
	private List listOfValues = null;
	/**
	 * Date of encounter of visit.
	 */
	private Date encounterDate = null;
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
	 * @param columns
	 *            The columns to set.
	 */
	public void setColumns(Integer columns)
	{
		this.columns = columns;
	}

	/**
	 * This method generates the HTML code for ComboBox control on the HTML form
	 *
	 * @return HTML code for ComboBox
	 * @throws DynamicExtensionsSystemException
	 *             if HTMLComponentName() fails.
	 */
	@Override
    public String generateEditModeHTML(final ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		Date encounterDate = (Date) container.getContextParameter(Constants.ENCOUNTER_DATE);
		this.encounterDate = encounterDate;
		String defaultValue = getDefaultValueForControl();

		int columnSize = 0;
		if (columns != null)
		{
			columnSize = columns.intValue();
		}
		String isDisabled = "";
		String htmlString = "";
		if ((isReadOnly != null && isReadOnly)
				|| (isSkipLogicReadOnly != null && isSkipLogicReadOnly))
		{
			isDisabled = ",disabled:'" + ProcessorConstants.TRUE + "'";
		}
		String htmlComponentName = getHTMLComponentName();
		StringBuffer sourceHtmlComponentValues = null;
		if (getSourceSkipControl() == null)
		{
			sourceHtmlComponentValues = new StringBuffer("~");
		}
		else
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
		if (getIsSkipLogicTargetControl() || getParentContainer().isAjaxRequest())
		{
			htmlString += "<div id='" + getHTMLComponentName() + "_div' name='"
					+ getHTMLComponentName() + "_div'>";
		}
		/*
		 * Bug Id:9030 textComponent is the name of the text box. if default
		 * value is not empty loading the data store first, and then setting the
		 * value in combo box to default value.
		 */
		String textComponent = "combo" + htmlComponentName;
		String categoryEntityName = getParentContainer().getAbstractEntity().getName();
		String attributeName = getBaseAbstractAttribute().getName();
		try
		{
			if ((!getParentContainer().isAjaxRequest())
					&& (!getIsSkipLogicTargetControl() && !"skipLogicAttributes"
							.equals(getDataEntryOperation())))
			{
				htmlString += "<script "
						+ (((CategoryEntityInterface) getParentContainer().getAbstractEntity())
								.getParentCategoryEntity() == null ? "" : " id='subformExtScript' ")
						+ " defer='defer'>Ext.onReady(function(){ "
						+ "var myUrl= \"DEComboDataAction.do?controlId="
						+ identifier
						+ "~containerIdentifier="
						+ parentContainerId
						+ "~sourceControlValues="
						+ URLEncoder.encode(sourceHtmlComponentValues.toString(), "utf-8")
						+ "~categoryEntityName="
						+ categoryEntityName
						+ "~attributeName="
						+ attributeName
						+ "~encounterDate="
						+ ControlsUtility.convertDateToString(encounterDate, "yyyy-MM-dd")
						+ "\";"
						+ "var ds = new Ext.data.Store({"
						+ "proxy: new Ext.data.HttpProxy({url: myUrl}),"
						+ "reader: new Ext.data.JsonReader({root: 'row',totalProperty: 'totalCount',id: 'id'}, "
						+ "[{name: 'id', mapping: 'id'},{name: 'excerpt', mapping: 'field'}])});"
						+ "var combo = new Ext.form.ComboBox({store: ds,"
						+ "hiddenName: '"
						+ textComponent
						+ "',id:'"
						+ textComponent
						+ "', displayField:'excerpt',valueField: 'id',"
						+ "typeAhead: 'false',pageSize:15,forceSelection: 'true',queryParam : 'query',"
						+ "mode: 'remote',triggerAction: 'all',minChars : "
						+ minQueryChar
						+ ",queryDelay:500,lazyInit:true"
						+ isDisabled
						+ ",emptyText:\""
						+ defaultValue
						+ "\",hiddenValue:\""
						+ defaultValue
						+ "\","
						+ "listWidth:240,"
						+ "tpl: getTpl(),/*'<tpl for=\".\"><div title=\"{excerpt}\" class=\"x-combo-list-item\">{excerpt}</div></tpl>',*/"
						+ "valueNotFoundText:'',"
						+ "selectOnFocus:'true',applyTo: '"
						+ htmlComponentName
						+ "'});ds.setBaseParam('comboBoxId',combo.getId());"
						+ "combo.on('blur',function(comboBox){if(comboBox.getValue()==''){comboBox.setValue(comboBox.emptyText);}});"
						+ "combo.on('focus',function(comboBox){comboBox.setRawValue(comboBox.emptyText);});"
						+ "combo.on(\"select\", function() {"
						+ getOnchangeServerCall()
						+ "}); /*combo.on(\"expand\", function() {if(Ext.isIE || Ext.isIE7 || Ext.isSafar){combo.list.setStyle(\"width\", \"240\");combo.innerList.setStyle(\"width\", \"240\");}else{alert('in else');combo.list.setStyle(\"width\", \"240\");combo.innerList.setStyle(\"width\", \"240\");}}, {single: true});*/";

				htmlString = htmlString
				// +
						// "ds.on('load',function(){combo.emptyText='';var tempVal = combo.getRawValue();combo.reset();combo.setValue(tempVal);if (this.getAt(0) != null && this.getAt(0).get('excerpt').toLowerCase().startsWith(combo.getRawValue().toLowerCase())) {combo.typeAheadDelay=50;} else {combo.typeAheadDelay=60000}});";
						+ "ds.on('load',function(storeObj){var count = storeObj.findExact('id',combo.emptyText);if(count!=-1){var tempVal = combo.emptyText;combo.reset();combo.setValue(tempVal);combo.emptyText='';}if (this.getAt(0) != null && this.getAt(0).get('excerpt').toLowerCase().startsWith(combo.getRawValue().toLowerCase())) {combo.typeAheadDelay=50;} else {combo.typeAheadDelay=60000}});";

				htmlString = htmlString + "});</script>";

			}
			htmlString += "<div id='auto_complete_dropdown'>"
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
					+ "' style='width:"
					+ (columnSize > 0 ? (columnSize + 1) : (Constants.DEFAULT_COLUMN_SIZE + 1))
					+ "ex' size='"
					+ (columnSize > 0 ? columnSize : Constants.DEFAULT_COLUMN_SIZE)
					+ "'/>"
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
					+ URLEncoder.encode(sourceHtmlComponentValues.toString(), "utf-8")
					+ "~categoryEntityName="
					+ categoryEntityName
					+ "~attributeName="
					+ attributeName
					+ "~encounterDate="
					+ ControlsUtility.convertDateToString(encounterDate, "yyyy-MM-dd")
					+ "\";var ds = new Ext.data.Store({"
					+ "proxy: new Ext.data.HttpProxy({url: myUrl}),"
					+ "reader: new Ext.data.JsonReader({root: 'row',totalProperty: 'totalCount',id: 'id'}, "
					+ "[{name: 'id', mapping: 'id'},{name: 'excerpt', mapping: 'field'}])});"
					+ "var combo = new Ext.form.ComboBox({store: ds,"
					+ "hiddenName: '"
					+ textComponent
					+ "',id:'"
					+ textComponent
					+ "', displayField:'excerpt',valueField: 'id',"
					+ "typeAhead: 'false',pageSize:15,forceSelection: 'true',queryParam : 'query',"
					+ "mode: 'remote',triggerAction: 'all',minChars : "
					+ minQueryChar
					+ ",queryDelay:500,lazyInit:true"
					+ isDisabled
					+ ",emptyText:\""
					+ defaultValue
					+ "\",hiddenValue:\""
					+ defaultValue
					+ "\",listWidth:240,width:165,"
					+ "tpl: getTpl(),"
					+ "valueNotFoundText:'',"
					+ "selectOnFocus:'true',applyTo: '"
					+ htmlComponentName
					+ "'});ds.setBaseParam('comboBoxId',combo.getId());"
					+ "combo.on('blur',function(comboBox){if(comboBox.getValue()==''){comboBox.setValue(comboBox.emptyText);}});"
					+ "combo.on('focus',function(comboBox){comboBox.setRawValue(comboBox.emptyText);});"
					+ "combo.on(\"select\", function() {"
					+ getOnchangeServerCall()
					+ "}); /*combo.on(\"expand\", function() {if(Ext.isIE || Ext.isIE7 || Ext.isSafar){combo.list.setStyle(\"width\", \"240\");combo.innerList.setStyle(\"width\", \"240\");}else{combo.list.setStyle(\"width\", \"240\");combo.innerList.setStyle(\"width\", \"240\");}}, {single: true});*/"
					// +
					// "ds.on('load',function(){if (this.getAt(0) != null) {if (this.getAt(0).get('excerpt').toLowerCase().startsWith(combo.getRawValue().toLowerCase())) {combo.typeAheadDelay=50} else {combo.typeAheadDelay=60000}}});"
					+ "ds.on('load',function(storeObj){var count = storeObj.findExact('id',combo.emptyText);if(count!=-1){var tempVal = combo.emptyText;combo.reset();combo.setValue(tempVal);combo.emptyText='';}if (this.getAt(0) != null) {if (this.getAt(0).get('excerpt').toLowerCase().startsWith(combo.getRawValue().toLowerCase())) {combo.typeAheadDelay=50} else {combo.typeAheadDelay=60000}}});"
					+ "});"
					+ "</div>"
					+ "<div name=\"comboHtml\" id=\"comboHtml\" style='display:none'>"
					+ "<div>"
					+ "<input type='text' onmouseover=\"showToolTip('"
					+ htmlComponentName
					+ "')\" id='"
					+ htmlComponentName
					+ "' "
					+ " name='"
					+ htmlComponentName
					+ "' value =\""
					+ defaultValue
					+ "\" style='width:"
					+ (columnSize > 0 ? (columnSize + 1) : (Constants.DEFAULT_COLUMN_SIZE + 1))
					+ "ex' size='"
					+ (columnSize > 0 ? columnSize : Constants.DEFAULT_COLUMN_SIZE)
					+ "' class='font_bl_nor' />" + "</div>" + "</div>" + "</div>";

		}
		catch (UnsupportedEncodingException e)
		{
			throw new DynamicExtensionsSystemException("Error while encoding url.", e);
		}
		if (getIsSkipLogicTargetControl() || getParentContainer().isAjaxRequest())
		{
			htmlString += "<input type='hidden' name='skipLogicControl' id='skipLogicControl' value = '"
					+ getHTMLComponentName()
					+ "_div' />"
					+ "<input type='hidden' name='skipLogicControlScript' id='skipLogicControlScript' value = 'comboScript_"
					+ getHTMLComponentName() + "' />";
			htmlString += "</div>";
		}
		return htmlString;
	}

	/**
	 * This method returns the list of values that are displayed as choices.
	 *
	 * @return the list of values that are displayed as choices.
	 */
	public List getChoiceList()
	{
		return listOfValues;
	}

	/**
	 * This method sets the list of values that are displayed as choices.
	 *
	 * @param choiceList
	 *            the List of values that is to set as ChoiceList.
	 */
	public void setChoiceList(final List choiceList)
	{
		listOfValues = choiceList;
	}

	@Override
    protected String generateViewModeHTML(final ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		String htmlString = "&nbsp;";
		Date encounterDate = (Date) container.getContextParameter(Constants.ENCOUNTER_DATE);
		this.encounterDate = encounterDate;
		String defaultValue = "";
		if (value != null)
		{
			if (value instanceof String)
			{
				defaultValue = (String) value;
			}
			else if (value instanceof List)
			{
				List valueList = (List) value;
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
			if (getSourceSkipControl() != null)
			{
				sourceControlValues = getSourceSkipControl().getValueAsStrings();
			}
			nameValueBeanList = ControlsUtility.populateListOfValues(this, sourceControlValues,
					encounterDate);
		}

		if (nameValueBeanList != null && !nameValueBeanList.isEmpty())
		{
			for (NameValueBean nameValueBean : nameValueBeanList)
			{
				if (nameValueBean.getValue().equals(defaultValue))
				{
					htmlString = "<span class='font_bl_s'> "
							+ DynamicExtensionsUtility.getUnEscapedStringValue(nameValueBean
									.getName()) + "</span>";
					break;
				}
			}
		}

		return htmlString;
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
				defaultValue = getAttibuteMetadataInterface().getDefaultValue(encounterDate);
				if (defaultValue == null || defaultValue.length() == 0)
				{
					defaultValue = "";
				}
			}
		}
		else
		{
			defaultValue = getAppropriateValue(value);
		}
		if (isInvalidValue(defaultValue, encounterDate))
		{
			defaultValue = getAttibuteMetadataInterface().getDefaultValue(encounterDate);
			// Set default value to blank, if not set
			if (defaultValue == null)
			{
				defaultValue = "";
			}
			if(!"".equals(defaultValue))
			{
				StringBuilder errorMessage=new StringBuilder();
				errorMessage.append('\'');
				errorMessage.append(value);
				errorMessage.append("' is not a valid value for '");
				errorMessage.append(getAttibuteMetadataInterface().getAttribute().getName());
				errorMessage.append("' anymore. Please select a new value.");
				errorList.add(errorMessage.toString());
			}
		}
		defaultValue = DynamicExtensionsUtility.getUnEscapedStringValue(defaultValue);
		return defaultValue;
	}

	/**
	 * Gets the appropriate value.
	 * @param value the value
	 * @return the appropriate value
	 */
	@SuppressWarnings("unchecked")
	private String getAppropriateValue(Object value)
	{
		String defaultValue;
		if (value instanceof String)
		{
			defaultValue = value.toString();
		}
		else if (value instanceof List)
		{
			List valueList = (List) value;
			if (valueList.isEmpty())
			{
				defaultValue = "";
			}
			else
			{
				defaultValue = valueList.get(0).toString();
			}
		}
		else
		{
			defaultValue = "";
		}
		return defaultValue;
	}

	/**
	 * Gets the default skip logic value.
	 * @return the default skip logic value
	 */
	private String getDefaultSkipLogicValue()
	{
		CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface) getAttibuteMetadataInterface();
		Object defaultValue = categoryAttribute.getDefaultSkipLogicValue().getValueAsObject();
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
	 * @param value
	 * @return true if value is not present in the pv list
	 */
	private boolean isInvalidValue(String value, Date encounterDate)
	{
		List<NameValueBean> nameValueBeans = ControlsUtility.getListOfPermissibleValues(
				getAttibuteMetadataInterface(), encounterDate);
		boolean isInavlidVaue = true;
		for (NameValueBean bean : nameValueBeans)
		{
			if (bean.getValue().equals(value))
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
	@Override
    public List<String> getValueAsStrings()
	{
		List<String> values = new ArrayList<String>();
		values.add(getDefaultValueForControl());
		return values;
	}

	/**
	 *
	 */
	@Override
    public void setValueAsStrings(List<String> listOfValues)
	{
		if (!listOfValues.isEmpty())
		{
			setValue(listOfValues.get(0));
		}
	}

	/**
	 *
	 */
	@Override
    public boolean getIsEnumeratedControl()
	{
		return true;
	}

	/**
	 * Returns collection of key-value pairs.
	 */
	@Override
    public Collection<UIProperty> getControlTypeValues()
	{
		Collection<UIProperty> controlTypeValues = super.getControlTypeValues();
		ComboBoxEnum[] uiPropertyValues = ComboBoxEnum.values();
		for (ComboBoxEnum propertyType : uiPropertyValues)
		{
			String controlProperty = propertyType.getControlProperty(this, null);
			if (controlProperty != null)
			{
				controlTypeValues.add(new UIProperty(propertyType.getValue(), controlProperty));
			}
		}
		return controlTypeValues;
	}

	/**
	 * Set collection of key-value pairs for a control.
	 */
	@Override
    public void setControlTypeValues(Collection<UIProperty> uiProperties)
	{
		super.setControlTypeValues(uiProperties);
		for (UIProperty uiProperty : uiProperties)
		{
			ComboBoxEnum propertyType = ComboBoxEnum.getValue(uiProperty.getKey());
			propertyType.setControlProperty(this, uiProperty.getValue(), null);
		}
	}
}