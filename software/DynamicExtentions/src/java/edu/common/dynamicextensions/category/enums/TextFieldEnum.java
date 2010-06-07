
package edu.common.dynamicextensions.category.enums;

import java.util.Collection;
import java.util.HashSet;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.userinterface.TextField;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleParameterInterface;
import edu.common.dynamicextensions.util.parser.CategoryCSVConstants;

public enum TextFieldEnum {

	ISPASSWORD("isPassword") {

		/**
		 * Returns String representation of password value for a control.
		 */
		public String getControlProperty(TextField control)
		{
			String passwordString = null;
			if (control.getIsPassword() != null)
			{
				passwordString = String.valueOf(control.getIsPassword());
			}
			return passwordString;
		}

		/**
		 * Sets String representation of password value for a control.
		 */
		public void setControlProperty(TextField control, String propertyToBeSet)
		{
			control.setIsPassword(Boolean.valueOf(propertyToBeSet));
		}
	},
	ISURL("isURL") {

		/**
		 * Returns String representation of url value for a control.
		 */
		public String getControlProperty(TextField control)
		{
			String urlString = null;
			if (control.getIsUrl() != null)
			{
				urlString = String.valueOf(control.getIsUrl());
			}
			return urlString;
		}

		/**
		 * Sets String representation of url value for a control.
		 */
		public void setControlProperty(TextField control, String propertyToBeSet)
		{
			control.setIsUrl(Boolean.valueOf(propertyToBeSet));
		}
	},
	COLUMNS("Columns") {

		/**
		 * Returns String representation of columns value for a control.
		 */
		public String getControlProperty(TextField control)
		{
			String culumsString = null;
			if (control.getColumns() != null)
			{
				culumsString = String.valueOf(control.getColumns());
			}
			return culumsString;
		}

		/**
		 * Sets String representation of columns value for a control.
		 */
		public void setControlProperty(TextField control, String propertyToBeSet)
		{
			control.setColumns(Integer.valueOf(propertyToBeSet));
		}
	},
	MAX("Max") {

		/**
		 * Returns String representation of max value for a control.
		 */
		public String getControlProperty(TextField control)
		{
			String max = null;
			Collection<RuleInterface> rules = ((CategoryAttributeInterface) control
					.getBaseAbstractAttribute()).getAbstractAttribute().getRuleCollection();
			for (RuleInterface rule : rules)
			{
				if (rule.getName().equals(CategoryCSVConstants.RANGE))
				{
					Collection<RuleParameterInterface> ruleParameterCollection = rule
							.getRuleParameterCollection();
					for (RuleParameterInterface ruleParameter : ruleParameterCollection)
					{
						if (CategoryCSVConstants.MAX.equals(ruleParameter.getName()))
						{
							max = ruleParameter.getValue();
						}
					}
				}
			}
			return max;
		}

		/**
		 * Sets String representation of max value for a control.
		 */
		public void setControlProperty(TextField control, String propertyToBeSet)
		{
			RuleInterface rule = DomainObjectFactory.getInstance().createRule();
			rule.setName(CategoryCSVConstants.RANGE);
			Collection<RuleParameterInterface> ruleParameterCollection = new HashSet<RuleParameterInterface>();
			RuleParameterInterface ruleParameter = DomainObjectFactory.getInstance()
					.createRuleParameter();
			ruleParameter.setName(CategoryCSVConstants.MAX);
			ruleParameter.setValue(propertyToBeSet);
			ruleParameterCollection.add(ruleParameter);
			rule.setRuleParameterCollection(ruleParameterCollection);
			AbstractAttributeInterface attribute = ((CategoryAttributeInterface) control
					.getBaseAbstractAttribute()).getAbstractAttribute();
			Collection<RuleInterface> rules = attribute.getRuleCollection() == null
					? new HashSet<RuleInterface>()
					: attribute.getRuleCollection();
			rules.add(rule);
			attribute.setRuleCollection(rules);
		}
	},
	MIN("Min") {

		/**
		 * Returns String representation of min value for a control.
		 */
		public String getControlProperty(TextField control)
		{
			String min = null;
			Collection<RuleInterface> rules = ((CategoryAttributeInterface) control
					.getBaseAbstractAttribute()).getRuleCollection();
			for (RuleInterface rule : rules)
			{
				if (rule.getName().equals(CategoryCSVConstants.RANGE))
				{
					Collection<RuleParameterInterface> ruleParameterCollection = rule
							.getRuleParameterCollection();
					for (RuleParameterInterface ruleParameter : ruleParameterCollection)
					{
						if (CategoryCSVConstants.MIN.equals(ruleParameter.getName()))
						{
							min = ruleParameter.getValue();
						}
					}
				}
			}
			return min;
		}

		/**
		 * Sets String representation of min value for a control.
		 */
		public void setControlProperty(TextField control, String propertyToBeSet)
		{
			RuleInterface rule = DomainObjectFactory.getInstance().createRule();
			rule.setName(CategoryCSVConstants.RANGE);
			Collection<RuleParameterInterface> ruleParameterCollection = new HashSet<RuleParameterInterface>();
			RuleParameterInterface ruleParameter = DomainObjectFactory.getInstance()
					.createRuleParameter();
			ruleParameter.setName(CategoryCSVConstants.MIN);
			ruleParameter.setValue(propertyToBeSet);
			ruleParameterCollection.add(ruleParameter);
			rule.setRuleParameterCollection(ruleParameterCollection);
			AbstractAttributeInterface attribute = ((CategoryAttributeInterface) control
					.getBaseAbstractAttribute()).getAbstractAttribute();
			Collection<RuleInterface> rules = attribute.getRuleCollection() == null
					? new HashSet<RuleInterface>()
					: attribute.getRuleCollection();
			rules.add(rule);
			attribute.setRuleCollection(rules);
		}
	};

	private String name;

	TextFieldEnum(String name)
	{
		this.name = name;
	}

	/** Abstract method for all enums to get control property */
	public abstract void setControlProperty(TextField control, String propertyToBeSet);

	/** Abstract method for all enums to set control property */
	public abstract String getControlProperty(TextField control);

	/**
	 * Returns name of Enum.
	 * @return
	 */
	public String getValue()
	{
		return name;
	}

	/**
	 * Returns Enum for given string.
	 * @param nameToBeFound
	 * @return
	 */
	public static TextFieldEnum getValue(String nameToBeFound)
	{
		TextFieldEnum[] propertyTypes = TextFieldEnum.values();
		for (TextFieldEnum propertyType : propertyTypes)
		{
			if (propertyType.getValue().equalsIgnoreCase(nameToBeFound))
			{
				return propertyType;
			}
		}
		throw new IllegalArgumentException(nameToBeFound + ": is not a valid property");
	}
}