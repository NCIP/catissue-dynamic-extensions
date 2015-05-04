
package edu.common.dynamicextensions.domain.nui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.napi.ControlValue;
import edu.common.dynamicextensions.napi.impl.FormRenderer.ContextParameter;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;

public class RadioButton extends SelectControl {

	private static final long serialVersionUID = -4795437046819612617L;
	
	private int optionsPerRow = 3;

	public int getOptionsPerRow() {
		return optionsPerRow;
	}

	public void setOptionsPerRow(int optionsPerRow) {
		this.optionsPerRow = optionsPerRow;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + optionsPerRow;
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
		
		RadioButton other = (RadioButton) obj;
		if (optionsPerRow != other.optionsPerRow) {
			return false;
		}
		return true;
	}

	@Override
	public List<ColumnDef> getColumnDefs() {
		return Collections.singletonList(ColumnDef.get(getDbColumnName(), getDbType()));
	}

	@Override
	protected String render(String controlName, ControlValue controlValue, Map<ContextParameter, String> contextParameter) {
		StringBuilder htmlString = new StringBuilder("");

		
		Date activationDate = ControlsUtility.getFormattedDate(contextParameter.get(ContextParameter.ACTIVATION_DATE));
		final String displayValue = getDisplayValue(controlValue, activationDate);
		String disabled = "";
		if (controlValue.isReadOnly()) {
			disabled = " disabled='true' ";
		}
		final String htmlComponentName = controlName;
		htmlString.append("<table cellspacing='3'>");
		List<PermissibleValue> permissibleValues = getPVList(activationDate, controlValue);
		if (permissibleValues != null && !permissibleValues.isEmpty()) {
			int columnNum = 0;

			for (PermissibleValue nameValueBean : permissibleValues) {
				if (columnNum % optionsPerRow == 0) {
					columnNum = 0;
					htmlString.append("<tr>");
				}
				final String optionName = DynamicExtensionsUtility.replaceHTMLSpecialCharacters(nameValueBean.getValue());
				final String optionValue = nameValueBean.getValue();
				if (optionValue.equals(displayValue)) {
					htmlString.append("<td><input type='radio' onClick=\"").append(getOnchangeServerCall(controlName)).append("\""
							).append("name='").append(htmlComponentName
									).append("' " ).append( "value='" ).append(optionValue).append("' ").append("id='").append(optionName).append("' checked ").append(disabled
											).append("  " ).append("/></td>");
				} else {
					htmlString.append("<td><input type='radio' onClick=\""
							).append(getOnchangeServerCall(controlName)
									).append("\" name='"
											).append(htmlComponentName
													).append("' " ).append("value='" ).append(optionValue ).append("' " ).append("id='" ).append(optionName ).append("' " ).append(disabled

															).append(" /></td>");
				}
				/*htmlString.append("<td class='formRequiredLabel_withoutBorder'>  <label for=\"" ).append(htmlComponentName
						).append("\" onClick=\"selectRadioButton('" ).append(controlName ).append("','" ).append(optionValue
								).append("')\">" ).append(optionName ).append("</label></td>");*/
				htmlString.append("<td class='formRequiredLabel_withoutBorder'>  <label for=\"" ).append(htmlComponentName
						).append("\">" ).append(optionName ).append("</label></td>");
				if (columnNum % optionsPerRow == optionsPerRow - 1) {
					htmlString.append("</tr>");
				}
				columnNum++;

			}
			if (columnNum % optionsPerRow < optionsPerRow - 1) {
				htmlString.append("</tr>");
			}

		}
		htmlString.append("</table>");

		return htmlString.toString();
	}
	
	@Override
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
