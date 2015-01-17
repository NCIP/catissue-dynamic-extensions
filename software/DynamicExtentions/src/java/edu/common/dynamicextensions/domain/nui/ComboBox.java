
package edu.common.dynamicextensions.domain.nui;

import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.napi.ControlValue;
import edu.common.dynamicextensions.napi.impl.FormRenderer.ContextParameter;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.global.Variables;

public class ComboBox extends SelectControl {

	private static final long serialVersionUID = 6117463441002075089L;

	private boolean lazyPvFetchingEnabled;

	private int noOfColumns;

	private int minQueryChar;

	public boolean isLazyPvFetchingEnabled() {
		return lazyPvFetchingEnabled;
	}

	public void setLazyPvFetchingEnabled(boolean lazyPvFetchingEnabled) {
		this.lazyPvFetchingEnabled = lazyPvFetchingEnabled;
	}

	public int getNoOfColumns() {
		return noOfColumns;
	}

	public void setNoOfColumns(int noOfColumns) {
		this.noOfColumns = noOfColumns;
	}
	
	public int getMinQueryChars() {
		return minQueryChar;
	}
	
	public void setMinQueryChars(int minQueryChar) {
		this.minQueryChar = minQueryChar;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (lazyPvFetchingEnabled ? 1231 : 1237);
		result = prime * result + noOfColumns;
		result = prime * result + minQueryChar;		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!super.equals(obj)) {
			return false;
		}

		ComboBox other = (ComboBox) obj;
		if (lazyPvFetchingEnabled != other.lazyPvFetchingEnabled ||
			noOfColumns != other.noOfColumns ||
			minQueryChar != other.minQueryChar) {
			return false;
		}
		
		return true;
	}

	@Override
	protected String render(String controlName, ControlValue controlValue,
			Map<ContextParameter, String> contextParameter) {
		
		Date activationDate = ControlsUtility.getFormattedDate(contextParameter.get(ContextParameter.ACTIVATION_DATE));
		String displayValue = getDisplayValue(controlValue, activationDate);

		int columnSize = 0;
		if (noOfColumns != 0) {
			columnSize = noOfColumns;
		}
		String isDisabled = "";
		StringBuffer htmlString = new StringBuffer("");
		if (controlValue.isReadOnly()) {
			isDisabled = ",disabled:'" + ProcessorConstants.TRUE + "'";
		}
	
		String textComponent = "combo" + controlName;
		htmlString.append("<div id='auto_complete_dropdown'><input type='text' onmouseover=\"showToolTip('")
				.append(controlName).append("')\" id='").append(controlName).append("' name='").append(controlName)
				.append("' value =\"").append(displayValue).append("\" ").append(" style='width:")
				.append((columnSize > 0 ? (columnSize + 1) : (Constants.DEFAULT_COLUMN_SIZE + 1))).append("ex' size='")
				.append((columnSize > 0 ? columnSize : Constants.DEFAULT_COLUMN_SIZE)).append("'/>");

		htmlString.append("<script>")
				.append(getDataSourceHtml(controlName, contextParameter,
						controlValue))
				.append("var combo = new Ext.form.ComboBox({store: ds,hiddenName: '").append(textComponent)
				.append("',id:'").append(textComponent).append("', displayField:'excerpt',valueField: 'id',")
				.append("typeAhead: 'false',forceSelection: 'true',queryParam : 'query',");
		if (isLazyPvFetchingEnabled() || isSkipLogicTargetControl()) {
			htmlString.append("pageSize:15,mode:'remote',");
		} else {
			htmlString.append("mode:'local',");

		}
		htmlString
				.append("triggerAction: 'all',minChars : ")
				.append(minQueryChar)
				.append(",queryDelay:500,lazyInit:true")
				.append(isDisabled)
				.append(",emptyText:\"")
				.append(displayValue)
				.append("\",hiddenValue:\"")
				.append(displayValue)
				.append("\",listWidth:240,width:165,tpl: getTpl(),valueNotFoundText:'',")
				.append("selectOnFocus:'true',applyTo: '")
				.append(controlName)
				.append("'});ds.setBaseParam('")
				.append(DEConstants.COMBOBOX_IDENTIFER)
				.append("',combo.getId());")
				.append("combo.setValue(combo.emptyText);")
				.append("combo.on('blur',function(comboBox){if(comboBox.getValue()==''){comboBox.setValue(comboBox.emptyText);}});")
				.append("combo.on('focus',function(comboBox){if(comboBox.getValue()==''){comboBox.setRawValue(comboBox.emptyText);}});")
				.append("combo.on(\"select\", function() {")
				.append(getOnchangeServerCall(controlName))
				.append("}); /*combo.on(\"expand\", function() {if(Ext.isIE || Ext.isIE7 || Ext.isSafari){combo.list.setStyle(\"width\", \"240\");combo.innerList.setStyle(\"width\", \"240\");}else{combo.list.setStyle(\"width\", \"240\");combo.innerList.setStyle(\"width\", \"240\");}}, {single: true});*/")
				.append("ds.on('load',function(storeObj){var count = storeObj.findExact('id',combo.emptyText);if(count!=-1){var tempVal = combo.emptyText;combo.reset();combo.setValue(tempVal);combo.emptyText='';}if (this.getAt(0) != null) {if (this.getAt(0).get('excerpt').toLowerCase().startsWith(combo.getRawValue().toLowerCase())) {combo.typeAheadDelay=50} else {combo.typeAheadDelay=60000}}});")
				.append("});</script>");

		return htmlString.toString();
	}

	private String getDataSourceHtml(String controlName,
			Map<ContextParameter, String> contextParameter, ControlValue controlValue) {
		Date encounterDate = ControlsUtility.getFormattedDate(contextParameter.get(ContextParameter.ACTIVATION_DATE));
		StringBuffer htmlString = new StringBuffer();

			if (isLazyPvFetchingEnabled() || isSkipLogicTargetControl()) {
				String EventHandler = "Ext.onReady(function(){ var myUrl= \"%s?%s=%s";
				String DE_AJAX_HANDLER = String.valueOf(Variables.resourceMapping.get(WebUIManagerConstants.DE_AJAX_HANDLER));
						//getAjaxHandler(contextParameter);
				htmlString.append(String.format(EventHandler, DE_AJAX_HANDLER, WebUIManagerConstants.AJAX_OPERATION,
						WebUIManagerConstants.NEW_DE_COMBO_DATA_ACTION));
				htmlString
						.append("\";var ds = new Ext.data.Store({proxy: new Ext.data.HttpProxy({url: myUrl}),baseParams:");
			String baseParams = "{controlName:'%s',name:'%s',encounterDate:'%s'}";

				htmlString.append(String.format(baseParams, controlName, getName(),
						ControlsUtility.convertDateToString(encounterDate, "yyyy-MM-dd")));
				htmlString
						.append(", reader: new Ext.data.JsonReader({root: 'row',totalProperty: 'totalCount',id: 'id'}, [{name: 'id', mapping: 'id'},{name: 'excerpt', mapping: 'field'}])});");
			} else {
				StringBuffer pvDataString = createPvDataString(encounterDate, controlValue);
				htmlString.append("Ext.onReady(function(){  var combodata = ").append(pvDataString)
						.append("; var ds = new Ext.data.SimpleStore({fields: ['id','excerpt'],data : combodata });");
			}

		return htmlString.toString();
	}

	/**
	 * FIXME missing sql based PV handling  
	 * @param encounterDate
	 * @return pvDataString
	 * 
	 * Bifurcated code to create extJS data store based on pvs retrieved from pvProcessor's query
	 */
	private StringBuffer createPvDataString(Date encounterDate, ControlValue controlValue) {
		StringBuffer pvDataString = new StringBuffer("[");

		List<PermissibleValue> permissibleValues = getPVList(encounterDate, controlValue);
		for (PermissibleValue pv : permissibleValues) {
			pvDataString.append("[\"").append(pv.getOptionName()).append("\",\"")
					.append(DynamicExtensionsUtility.replaceHTMLSpecialCharacters(pv.getValue())).append("\"],");
		}

		if (pvDataString.toString().endsWith(",")) {
			pvDataString.replace(pvDataString.length() - 1, pvDataString.length(), "");
		}
		pvDataString.append(']');

		return pvDataString;
	}



}
