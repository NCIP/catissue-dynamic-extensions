
package edu.common.dynamicextensions.domain.nui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import edu.common.dynamicextensions.domain.DynamicExtensionBaseDomainObject;
import edu.common.dynamicextensions.napi.ControlValue;
import edu.common.dynamicextensions.napi.impl.FormRenderer.ContextParameter;
import edu.common.dynamicextensions.nui.ValidationRuleNames;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.Variables;

public abstract class Control extends DynamicExtensionBaseDomainObject implements Comparable<Control> {
	public static enum LabelPosition {
		LEFT_SIDE, TOP
	};
	
	private static final long serialVersionUID = -6394740140841823297L;

	private String name;
	
	private String userDefinedName;

	private String caption;

	private String customLabel;

	private LabelPosition labelPosition = LabelPosition.LEFT_SIDE;

	private String toolTip = StringUtils.EMPTY;

	private String dbColumnName;

	private boolean phi;

	private int sequenceNumber;

	private int xPos;

	private boolean showLabel = true;

	private boolean skipLogicSourceControl;

	private boolean calculatedSourceControl;
	
	private boolean skipLogicTargetControl;
	
	private boolean showInGrid;

	private String conceptCode;

	private String conceptPreferredName;

	private String conceptDefinitionSource;

	private String conceptDefinition;

	private String activityStatus = Constants.ACTIVE;

	private Set<ValidationRule> validationRules = new HashSet<ValidationRule>();

	private Container container;
	
	// temporary fix. should go away after 10/Jun/2013	
	private transient boolean mandatory;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getUserDefinedName() {
		return userDefinedName;
	}

	public void setUserDefinedName(String userDefinedName) {
		this.userDefinedName = userDefinedName;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getCustomLabel() {
		return customLabel;
	}

	public void setCustomLabel(String customLabel) {
		this.customLabel = customLabel;
	}

	public LabelPosition getLabelPosition() {
		return labelPosition;
	}

	public void setLabelPosition(LabelPosition labelPosition) {
		this.labelPosition = labelPosition;
	}

	public String getToolTip() {
		return toolTip;
	}

	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}

	public boolean isPhi() {
		return phi;
	}

	public void setPhi(boolean phi) {
		this.phi = phi;
	}

	public boolean isMandatory() {
		ValidationRule rule = getValidationRule(ValidationRuleNames.REQUIRED);
		return rule != null;
	}

	public void setMandatory(boolean mandatory) {
		if (mandatory) {
			addValidationRule(ValidationRuleNames.REQUIRED, null);
		} else {
			removeValidationRule(ValidationRuleNames.REQUIRED);
		}
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public boolean showLabel() {
		return showLabel;
	}

	public void setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
	}

	public boolean isSkipLogicSourceControl() {
		return skipLogicSourceControl;
	}

	public void setSkipLogicSourceControl(boolean skipLogicSourceControl) {
		this.skipLogicSourceControl = skipLogicSourceControl;
	}

	public boolean isCalculatedSourceControl() {
		return calculatedSourceControl;
	}

	public void setCalculatedSourceControl(boolean calculatedSourceControl) {
		this.calculatedSourceControl = calculatedSourceControl;
	}
	
	public boolean isSkipLogicTargetControl() {
		return skipLogicTargetControl;
	}

	public void setSkipLogicTargetControl(boolean skipLogicTargetControl) {
		this.skipLogicTargetControl = skipLogicTargetControl;
	}
	
	public boolean showInGrid() {
		return showInGrid;
	}

	public void setShowInGrid(boolean showInGrid) {
		this.showInGrid = showInGrid;
	}

	public String getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(String activityStatus) {
		this.activityStatus = activityStatus;
	}

	public String getDbColumnName() {
		return dbColumnName;
	}

	public void setDbColumnName(String dbColumnName) {
		this.dbColumnName = dbColumnName;
	}

	public String getConceptCode() {
		return conceptCode;
	}

	public void setConceptCode(String conceptCode) {
		this.conceptCode = conceptCode;
	}

	public String getConceptPreferredName() {
		return conceptPreferredName;
	}

	public void setConceptPreferredName(String conceptPreferredName) {
		this.conceptPreferredName = conceptPreferredName;
	}

	public String getConceptDefinitionSource() {
		return conceptDefinitionSource;
	}

	public void setConceptDefinitionSource(String conceptDefinitionSource) {
		this.conceptDefinitionSource = conceptDefinitionSource;
	}

	public String getConceptDefinition() {
		return conceptDefinition;
	}

	public void setConceptDefinition(String conceptDefinition) {
		this.conceptDefinition = conceptDefinition;
	}

	public Set<ValidationRule> getValidationRules() {
		return validationRules;
	}
	
	public void setValidationRules(Set<ValidationRule> validationRules) {
		this.validationRules = validationRules;
	}
	
	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	public void addValidationRule(String ruleName, Map<String, String> ruleParams) {
		ValidationRule reqRule = getValidationRule(ruleName);
		
		if (reqRule == null) {
			reqRule = new ValidationRule();
			reqRule.setName(ruleName);
			validationRules.add(reqRule);
		}

		if (ruleParams != null) {
			reqRule.getParams().putAll(ruleParams);
		}
	}
	
	public ValidationRule getValidationRule(String ruleName) {
		ValidationRule result = null;
		
		for (ValidationRule rule : validationRules) {
			if (rule.getName().equals(ruleName)) {
				result = rule;
				break;
			}
		}
		
		return result;
	}
	
	public String getValidationRuleParam(String ruleName, String param) {
		ValidationRule rule = getValidationRule(ruleName);
		String result = null;
		
		if (rule != null && rule.getParams() != null) {
			result = rule.getParams().get(param);
		}
		
		return result;
	}
	
	public void removeValidationRule(String ruleName) {
		Iterator<ValidationRule> ruleIterator = validationRules.iterator();
		
		while (ruleIterator.hasNext()) {
			if (ruleIterator.next().getName().equals(ruleName)) {
				ruleIterator.remove();
				break;
			}
		}
	}

	public abstract DataType getDataType();
	
	public abstract List<ColumnDef> getColumnDefs();

	public abstract <T> T fromString(String value);

	public String toString(Object value) {
		if (value == null) {
			return null;
		}

		return value.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((userDefinedName == null) ? 0 : userDefinedName.hashCode());
		result = prime * result + ((caption == null) ? 0 : caption.hashCode());
		result = prime * result	+ ((customLabel == null) ? 0 : customLabel.hashCode());
		result = prime * result	+ ((labelPosition == null) ? 0 : labelPosition.hashCode());
		result = prime * result + ((toolTip == null) ? 0 : toolTip.hashCode());
		result = prime * result	+ ((dbColumnName == null) ? 0 : dbColumnName.hashCode());
		result = prime * result + (phi ? 1231 : 1237);
		result = prime * result + sequenceNumber;
		result = prime * result + xPos;
		result = prime * result + (showLabel ? 1231 : 1237);
		result = prime * result + (skipLogicSourceControl ? 1231 : 1237);
		result = prime * result + (calculatedSourceControl ? 1231 : 1237);
		result = prime * result + (skipLogicTargetControl ? 1231 : 1237);
		result = prime * result + (showInGrid ? 1231 : 1237);
		result = prime * result + ((conceptCode == null) ? 0 : conceptCode.hashCode());
		result = prime * result	+ ((conceptPreferredName == null) ? 0 : conceptPreferredName.hashCode());
		result = prime * result	+ ((conceptDefinitionSource == null) ? 0 : conceptDefinitionSource.hashCode());
		result = prime * result	+ ((conceptDefinition == null) ? 0 : conceptDefinition.hashCode());		
		result = prime * result + ((activityStatus == null) ? 0 : activityStatus.hashCode());
		result = prime * result	+ ((validationRules == null) ? 0 : validationRules.hashCode());		
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		
		if (getClass() != other.getClass()) {
			return false;
		}
		
		if (this == other) {
			return true;
		}
		
		Control ctrl = (Control)other;
		if (!StringUtils.equals(name, ctrl.name) ||
			!StringUtils.equals(userDefinedName, ctrl.userDefinedName) ||
			!StringUtils.equals(caption, ctrl.caption) ||
			!StringUtils.equals(customLabel, ctrl.customLabel) ||
			labelPosition != ctrl.labelPosition ||
			!StringUtils.equals(toolTip, ctrl.toolTip) ||
			!StringUtils.equals(dbColumnName, ctrl.dbColumnName) ||
			phi != ctrl.phi ||
			sequenceNumber != ctrl.sequenceNumber ||
			xPos != ctrl.xPos ||
			showLabel != ctrl.showLabel ||
			skipLogicSourceControl != ctrl.skipLogicSourceControl ||
			calculatedSourceControl != ctrl.calculatedSourceControl ||
			skipLogicTargetControl != ctrl.skipLogicTargetControl ||
			showInGrid != ctrl.showInGrid ||
			!StringUtils.equals(conceptCode, ctrl.conceptCode) ||
			!StringUtils.equals(conceptPreferredName, ctrl.conceptPreferredName) ||
			!StringUtils.equals(conceptDefinitionSource, ctrl.conceptDefinitionSource) ||
			!StringUtils.equals(conceptDefinition, ctrl.conceptDefinition) ||
			!StringUtils.equals(activityStatus, ctrl.activityStatus)) {
			return false;
		}
		
		if (validationRules == null && ctrl.validationRules == null) {
			return true;
		}
		
		if (validationRules.size() != ctrl.validationRules.size()) {
			return false;
		}
		
		return validationRules.containsAll(ctrl.validationRules);
	}	
	
	
	//
	// Below is all rendering mess - should go away in v40
	//
	/**
	 * Format for the control name
	 */
	private static final String CONTROL_NAME_FORMAT = "Control-%d-%s-%d-%d";

	public String getControlName(Integer rowNumber) {
		String controlName = getControlName();
		if (rowNumber != -1) {
			controlName += "-" + rowNumber;
		}
		return controlName;
	}

	public String getControlName() {
		Container parentContainer = getContainer();
		String controlName = String.format(CONTROL_NAME_FORMAT, getId(), parentContainer.getName(), getxPos(),
				getSequenceNumber());
		return StringUtils.deleteWhitespace(controlName);
	}

	public static Integer getRowNumber(String controlName)
	{
		Integer rowNumber = null;
		if (controlName.matches("Control-(\\d+)-(\\S+)-(\\d+)-(\\d+)-(\\d+)")) {
			rowNumber = Integer.valueOf(controlName.substring(controlName.lastIndexOf("-") + 1));
		}
		return rowNumber;
	}

	public static String getContainerName(String controlName) {
		return controlName.split("-")[2];
	}

	public static int getXpos(String controlName) {
		return Integer.valueOf(controlName.split("-")[3]);
	}

	public static int getSequenceNumber(String controlName) {
		return Integer.valueOf(controlName.split("-")[4]);
	}

	/**
	 * @return return the HTML string for this type of a object
	 */
	public final String render(ControlValue controlValue, Map<ContextParameter, String> contextParameter) {
		StringBuilder htmlString = new StringBuilder();
		String controlName = getControlName();
		String controlHTML = StringUtils.EMPTY;

		StringBuilder innerHTML = new StringBuilder();
		innerHTML.append(render(controlName, controlValue, contextParameter));

		if (isMandatory()) {
			if (controlValue.getControl() instanceof SelectControl) {
				innerHTML.append("<script defer='defer'>$('#").append(controlName)
				.append("_tbody').addClass('required-field-marker');</script>");
			} else {
				innerHTML.append("<script defer='defer'>$('#").append(controlName)
				.append("').addClass('required-field-marker');</script>");
			}
		}
		controlHTML = attachLabel(innerHTML.toString());

		htmlString.append(controlHTML);

		return htmlString.toString();
	}

	protected abstract String render(String controlName, ControlValue controlValue,
			Map<ContextParameter, String> contextParameter);

	public String renderInGrid(Integer rowNumber, ControlValue controlValue,
			Map<ContextParameter, String> contextParameter) {
		String controlName = getControlName(rowNumber);
		String controlHTML = render(controlName, controlValue, contextParameter);
		StringBuilder htmlString = new StringBuilder();

		if (isDynamic()) {
			htmlString.append("<tbody id='").append(controlName).append("_tbody' name='").append(controlName)
					.append("_tbody'>");
			htmlString.append(controlHTML);
			htmlString.append("<input type='hidden' name='dynamicControl'  value = '")
					.append(controlName).append("_tbody' /></tbody>");
		}

		return controlHTML;
	}

	/**
	 * Returns string for skipLogic or live validation.
	 * @return String.
	 */
	public String getOnchangeServerCall(String controlName) {
		StringBuilder serverCallString = new StringBuilder("");
		if (isSkipLogicSourceControl()) {
			serverCallString.append("updateHTML();");
		} else {
			serverCallString.append(updateServerState(controlName));
		}
		return serverCallString.toString();
	}

	/**
	 * Returns string for live validation.
	 * @return String.
	 */
	private String updateServerState(String controlName) {
		StringBuilder updateServerState = new StringBuilder().append("updateServerState('").append(controlName)
				.append("','").append(id).append("','").append(getContainer().getId()).append("');");
		return updateServerState.toString();
	}

	protected String getAjaxHandler(Map<ContextParameter, String> contextParameter) {
		String ajaxPath = "%s/%s";
		if (contextParameter.get(ContextParameter.CONTEXT_PATH) != null) {
			ajaxPath = String.format(ajaxPath, contextParameter.get(ContextParameter.CONTEXT_PATH),
					String.valueOf(Variables.resourceMapping.get(WebUIManagerConstants.DE_AJAX_HANDLER)));
		} else {
			ajaxPath = String.valueOf(Variables.resourceMapping.get(WebUIManagerConstants.DE_AJAX_HANDLER));
		}
		return ajaxPath;
	}

	protected String attachLabel(String htmlString) {
		StringBuilder controlHTML = new StringBuilder(434);
		String toolTip = (this.toolTip != null) ? this.toolTip.trim() : StringUtils.EMPTY;

		if (LabelPosition.TOP == labelPosition) {
			controlHTML.append("<td title='").append(toolTip).append("'><table><tr>");
			updateRequiredFieldIndicator(isMandatory(), controlHTML);
			controlHTML.append("<td class='formRequiredLabel_withoutBorder'><div class='control_caption'>");
		} else if (getxPos() == 1) {
			updateRequiredFieldIndicator(isMandatory(), controlHTML);
			controlHTML.append("<td class='formRequiredLabel_withoutBorder' width='40%' title='").append(toolTip)
					.append("'><div class='control_caption'>");
		} else {
			updateRequiredFieldIndicator(isMandatory(), controlHTML);
			controlHTML.append("<td class='formRequiredLabel_withoutBorder'  title='").append(toolTip)
					.append("'><div class='control_caption'>");
		}

		if (showLabel) {
			if (customLabel != null) {
				controlHTML.append(customLabel);
			} else {
				controlHTML.append(DynamicExtensionsUtility.replaceHTMLSpecialCharacters(getCaption()));
			}

		}

		if (LabelPosition.TOP == labelPosition) {
			controlHTML.append("</div></td></tr><tr><td width='2%'></td>");
		} else {
			controlHTML.append("</div></td>");
		}

		if (getxPos() <= 1) {
			controlHTML.append("<td><table><tr>");
		}
		controlHTML.append("<td class='formRequiredLabel_withoutBorder' id='").append(getControlName())
				.append("_tdtbody' >").append(htmlString).append("</td>");


		if (LabelPosition.TOP == labelPosition) {
			controlHTML.append("</td></td></tr></table>");
		}

		return controlHTML.toString();
	}

	private void updateRequiredFieldIndicator(boolean isControlRequired, StringBuilder controlHTML) {
		if (getxPos() <= 1) {

			controlHTML
					.append("<td class='formRequiredNotice_withoutBorder' width='2%' valign='center' align='right' >");
		} else {
			controlHTML
					.append("<td class='formRequiredNotice_withoutBorder' valign='center' width='2%' align='right' >");
		}

		if (isControlRequired) {
			controlHTML.append("<span class='font_red'>").append(getContainer().getRequiredFieldIndicatior())
					.append("&nbsp; </span> </td> ");
		} else {
			controlHTML.append("&nbsp; </td> ");
		}

	}

	@Override
	public int compareTo(Control o) {
		int result;
		if (this.getSequenceNumber() < o.sequenceNumber) {
			result = -1;
		} else if (this.sequenceNumber == o.sequenceNumber) {
			if (this.xPos < o.xPos) {
				result = -1;
			} else {
				result = 1;
			}
		} else {
			result = 1;
		}

		return result;
	}

	public boolean isDynamic() {
		return isSkipLogicTargetControl();
	}

	public List<String> validate(ControlValue controlValue) {
		return Collections.emptyList();
	}

	public Set<String> getAllConceptCodes() {
		if (conceptCode != null && !conceptCode.isEmpty()) {
			return Collections.singleton(conceptCode);
		}
		return Collections.emptySet();
	}
	
	public List<String> getConditions() {
		List<String> conditions = new ArrayList<String>();
		return conditions;
	}
	
}
