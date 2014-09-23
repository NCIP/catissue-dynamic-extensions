
package edu.common.dynamicextensions.domain.nui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import edu.common.dynamicextensions.napi.ControlValue;
import edu.common.dynamicextensions.napi.impl.FormRenderer.ContextParameter;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;

public class StringTextField extends TextField {

	private static final long serialVersionUID = 7445421267574297134L;

	private boolean url;

	private boolean password;

	public boolean isUrl() {
		return url;
	}

	public void setUrl(boolean url)	{
		this.url = url;
	}

	public boolean isPassword()	{
		return password;
	}

	public void setPassword(boolean password) {
		this.password = password;
	}

	public int getMinLength() {
		int minLength = 0;		
		String min = getValidationRuleParam("textLength", "min");
		if (min != null && !min.trim().isEmpty()) {
			minLength = Integer.parseInt(min);
		}
		
		return minLength;
	}
	
	public void setMinLength(int minChars) {
		addValidationRule("textLength", Collections.singletonMap("min", String.valueOf(minChars)));
	}

	public int getMaxLength() {
		int maxLength = 0;		
		String max = getValidationRuleParam("textLength", "max");
		if (max != null && !max.trim().isEmpty()) {
			maxLength = Integer.parseInt(max);
		}
		
		return maxLength;		
	}
	
	public void setMaxLength(int maxChars) {
		addValidationRule("textLength", Collections.singletonMap("max", String.valueOf(maxChars)));
	}

	@Override
	public List<ColumnDef> getColumnDefs() {
		return Collections.singletonList(ColumnDef.get(getDbColumnName(), "VARCHAR2(4000)"));
	}
	
	@Override
	public DataType getDataType() {
		return DataType.STRING;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String fromString(String value) {
		return value;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (url ? 1231 : 1237);
		result = prime * result + (password ? 1231 : 1237);		
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
		
		StringTextField other = (StringTextField) obj;
		if (url != other.url || password != other.password) {
			return false;
		}
		
		return true;
	}

	@Override
	protected String render(String controlName, ControlValue controlValue, Map<ContextParameter, String> contextParameter)
	{
		String value = (controlValue.getValue()==null?DynamicExtensionsUtility.replaceHTMLSpecialCharacters(this
				.getDefaultValue()):(String)controlValue.getValue());
		if (value == null) {
			value = StringUtils.EMPTY;
		}
		StringBuilder htmlString = new StringBuilder("");

		if (url && !value.isEmpty()) {
			htmlString.append("<a href='javascript:void(0)' onclick=\"window.open('").append(value)
					.append("','','width=800,height=600,toolbar=yes,location=yes,directories=yes,status=yes,")
					.append("menubar=yes,scrollbars=yes,copyhistory=yes,resizable=yes')\">").append(value)
					.append("</a>").append("<INPUT  name='").append(controlName).append("' ").append("id='")
					.append(controlName).append("' value='")
					.append(DynamicExtensionsUtility.getEscapedStringValue(value)).append("' type='hidden'/> ");
		} else {
			htmlString.append("<INPUT  name='").append(controlName).append("' ").append("id='").append(controlName)
					.append("' onchange=\"").append(getOnchangeServerCall(controlName)).append(";\" value='")
					.append(DynamicExtensionsUtility.getEscapedStringValue(value)).append("' ");

			int columnSize = getNoOfColumns();
			if (columnSize > 0)
			{
				htmlString.append("size='").append(columnSize).append("' ");
				htmlString.append("style='width:").append((columnSize + 1)).append("ex' ");
			}
			else
			{
				htmlString.append("size='").append(Constants.DEFAULT_COLUMN_SIZE).append("' ");
			}

			if (password)
			{
				htmlString.append(" type='password' ");
			}
			else
			{
				htmlString.append(" type='text' ");
			}

			if (controlValue.isReadOnly())
			{
				htmlString.append(" readonly='").append(ProcessorConstants.TRUE).append("' ");
			}
			if (controlValue.getErrorMessage() != null) {
				htmlString.append(" title='").append(controlValue.getErrorMessage()).append("' ").append(" class='")
						.append("font_bl_nor_error").append("' ");
			}
			htmlString.append("/>");

		}

		return htmlString.toString();
	}
	
	public List<String> getConditions() {
		List<String> conditions = new ArrayList<String>();
		conditions.add(DEConstants.STARTS_WITH);
		conditions.add(DEConstants.ENDS_WITH);
		conditions.add(DEConstants.EQUALS);
		conditions.add(DEConstants.NOT_EQUALS);
		conditions.add(DEConstants.CONTAINS);
		conditions.add(DEConstants.IS_PRESENT);
		conditions.add(DEConstants.IS_NOT_PRESENT);
		return conditions;
	}
}
