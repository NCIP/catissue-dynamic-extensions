
package edu.wustl.dynamicextensions.formdesigner.mapper;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domain.nui.Container;
import edu.common.dynamicextensions.domain.nui.Control;
import edu.common.dynamicextensions.domain.nui.DisableAction;
import edu.common.dynamicextensions.domain.nui.EnableAction;
import edu.common.dynamicextensions.domain.nui.HideAction;
import edu.common.dynamicextensions.domain.nui.PermissibleValue;
import edu.common.dynamicextensions.domain.nui.SelectControl;
import edu.common.dynamicextensions.domain.nui.ShowAction;
import edu.common.dynamicextensions.domain.nui.ShowPvAction;
import edu.common.dynamicextensions.domain.nui.SkipAction;
import edu.common.dynamicextensions.domain.nui.SkipCondition;
import edu.common.dynamicextensions.domain.nui.SkipCondition.RelationalOp;
import edu.common.dynamicextensions.domain.nui.SkipRule;
import edu.common.dynamicextensions.domain.nui.SkipRule.LogicalOp;

public class SkipRuleBuilder {

	private Container container;
	private SkipRule skipRule;

	public SkipRuleBuilder(Container container) {
		this.container = container;
		skipRule = new SkipRule();
	}

	public SkipRule getSkipRule() {
		return skipRule;
	}

	public SkipRuleBuilder anyCondition() {
		skipRule.setLogicalOp(LogicalOp.OR);
		return this;
	}

	public SkipRuleBuilder allConditions() {
		skipRule.setLogicalOp(LogicalOp.AND);
		return this;
	}

	public SkipRuleBuilder equalsCondition(String fieldName, String fieldValue) {
		SkipCondition skipCondition = new SkipCondition();

		setSkipConditionDetails(fieldName, skipCondition, fieldValue);
		skipCondition.setRelationalOp(RelationalOp.EQ);

		skipRule.getConditions().add(skipCondition);
		return this;
	}

	public SkipRuleBuilder greaterThanCondition(String fieldName, String fieldValue) {
		SkipCondition skipCondition = new SkipCondition();

		setSkipConditionDetails(fieldName, skipCondition, fieldValue);
		skipCondition.setRelationalOp(RelationalOp.GT);

		skipRule.getConditions().add(skipCondition);
		return this;
	}

	public SkipRuleBuilder lessThanCondition(String fieldName, String fieldValue) {
		SkipCondition skipCondition = new SkipCondition();

		setSkipConditionDetails(fieldName, skipCondition, fieldValue);
		skipCondition.setRelationalOp(RelationalOp.LT);

		skipRule.getConditions().add(skipCondition);
		return this;
	}

	public SkipRuleBuilder greaterThanEqualsCondition(String fieldName, String fieldValue) {
		SkipCondition skipCondition = new SkipCondition();

		setSkipConditionDetails(fieldName, skipCondition, fieldValue);
		skipCondition.setRelationalOp(RelationalOp.GE);

		skipRule.getConditions().add(skipCondition);
		return this;
	}

	public SkipRuleBuilder lessThanEqualsCondition(String fieldName, String fieldValue) {
		SkipCondition skipCondition = new SkipCondition();

		setSkipConditionDetails(fieldName, skipCondition, fieldValue);
		skipCondition.setRelationalOp(RelationalOp.LE);

		skipRule.getConditions().add(skipCondition);
		return this;
	}

	public void perform(String action, String... actionPerformedOnFields) {

		for (String fieldName : actionPerformedOnFields) {
			addAction(action, fieldName.trim());
		}
	}

	public void createPvSubSet(String fieldName, String defVal, String... pvs) {
		Control tgtCtrl = container.getControl(fieldName.trim(), "\\.");
		if (tgtCtrl == null) {
			throw new RuntimeException("Invalid field name: " + fieldName);
		}

		ShowPvAction pvAction = new ShowPvAction();

		List<PermissibleValue> subSet = getPvsFromValues((SelectControl) tgtCtrl, pvs);
		pvAction.setListOfPvs(subSet);
		PermissibleValue defaultPv = new PermissibleValue();
		defaultPv.setValue(defVal);

		int subSetPvIndex = subSet.indexOf(defaultPv);
		if (subSetPvIndex >= 0) {
			pvAction.setDefaultPv(subSet.get(subSetPvIndex));
		}

		pvAction.setTargetCtrl(tgtCtrl);

		skipRule.getActions().add(pvAction);
	}

	public void createPvSubSet(String fieldName, String defVal, List<PermissibleValue> pvSubSet) {
		Control tgtCtrl = container.getControl(fieldName.trim(), "\\.");
		if (tgtCtrl == null) {
			throw new RuntimeException("Invalid field name: " + fieldName);
		}

		ShowPvAction pvAction = new ShowPvAction();

		List<PermissibleValue> subSet = pvSubSet;
		pvAction.setListOfPvs(subSet);
		PermissibleValue defaultPv = new PermissibleValue();
		defaultPv.setValue(defVal);

		pvAction.setDefaultPv(subSet.get(subSet.indexOf(defaultPv)));
		pvAction.setTargetCtrl(tgtCtrl);

		skipRule.getActions().add(pvAction);
	}

	public List<PermissibleValue> getPvsFromValues(SelectControl control, String... values) {
		List<PermissibleValue> pvSubSet = new ArrayList<PermissibleValue>();
		List<PermissibleValue> pvSuperSet = control.getPvs();

		for (String value : values) {
			pvSubSet.add(getPvFromPvList(value.trim(), pvSuperSet));
		}

		return pvSubSet;
	}

	private PermissibleValue getPvFromPvList(String pvValue, List<PermissibleValue> pvs) {
		PermissibleValue permValue = null;
		for (PermissibleValue pv : pvs) {
			if (pv.getValue().trim().equalsIgnoreCase(pvValue)) {
				permValue = pv;
				break;
			}
		}

		return permValue;
	}

	private void addAction(String actionName, String fieldName) {
		Control tgtCtrl = container.getControl(fieldName.trim(), "\\.");
		if (tgtCtrl == null) {
			throw new RuntimeException("Invalid field name:  " + fieldName);
		}

		SkipAction action = null;
		if (actionName.equals("show")) {
			action = new ShowAction();
		} else if (actionName.equals("hide")) {
			action = new HideAction();
		} else if (actionName.equals("enable")) {
			action = new EnableAction();
		} else if (actionName.equals("disable")) {
			action = new DisableAction();
		}

		action.setTargetCtrl(tgtCtrl);
		skipRule.getActions().add(action);
	}

	/**
	 * @param fieldName
	 * @param skipCondition
	 * @throws RuntimeException
	 */
	private void setSkipConditionDetails(String fieldName, SkipCondition skipCondition, String fieldValue)
			throws RuntimeException {
		Control ctrl = container.getControl(fieldName.trim(), "\\.");

		if (ctrl == null) {
			throw new RuntimeException("Invalid field name: " + fieldName);
		}

		skipCondition.setSourceControl(ctrl);
		skipCondition.setValue(fieldValue);
	}

}
