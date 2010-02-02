
package edu.common.dynamicextensions.category.enums;

import edu.common.dynamicextensions.domain.userinterface.DatePicker;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.parser.CategoryCSVConstants;

public enum DatePickerEnum {
	RANGEMIN("range-min") {

		/**
		 * Returns String representation of Range min rule.
		 */
		public String getControlProperty(DatePicker control)
		{
			boolean result = ControlsUtility.isRuleDefined(CategoryCSVConstants.MIN, control);
			String ruleName = null;
			if (result)
			{
				ruleName = CategoryCSVConstants.MIN;
			}
			return ruleName;
		}

		/**
		 * Sets Range min Rule for a given String.
		 */
		public void setControlProperty(DatePicker control, String propertyToBeSet)
		{
			ControlsUtility.defineRule(CategoryCSVConstants.MIN, control);
		}
	},
	RANGEMAX("range-max") {

		/**
		 * Returns String representation of Range max Rule. 
		 */
		public String getControlProperty(DatePicker control)
		{
			boolean result = ControlsUtility.isRuleDefined(CategoryCSVConstants.MAX, control);
			String ruleName = null;
			if (result == true)
			{
				ruleName = CategoryCSVConstants.MAX;
			}
			return ruleName;
		}

		/**
		 * Sets Range max rule for given rule.
		 */
		public void setControlProperty(DatePicker control, String propertyToBeSet)
		{
			ControlsUtility.defineRule(CategoryCSVConstants.MAX, control);
		}
	},
	ALLOWFUTUREDATE("allowfuturedate") {

		/**
		 * Returns String representation of rule Allow future date. 
		 */
		public String getControlProperty(DatePicker control)
		{
			boolean result = ControlsUtility.isRuleDefined(CategoryCSVConstants.ALLOW_FUTURE_DATE,
					control);
			String ruleName = null;
			if (result == true)
			{
				ruleName = CategoryCSVConstants.ALLOW_FUTURE_DATE;
			}
			return ruleName;
		}

		/**
		 * Sets rule Allow future date. 
		 */
		public void setControlProperty(DatePicker control, String propertyToBeSet)
		{
			ControlsUtility.defineRule(CategoryCSVConstants.ALLOW_FUTURE_DATE, control);
		}
	};

	private String name;

	DatePickerEnum(String name)
	{
		this.name = name;
	}

	/** Abstract method for all enums to get control property */
	public abstract void setControlProperty(DatePicker control, String propertyToBeSet);

	/** Abstract method for all enums to set control property */
	public abstract String getControlProperty(DatePicker control);

	public String getValue()
	{
		return name;
	}

	public static DatePickerEnum getValue(String nameToBeFound)
	{
		DatePickerEnum[] propertyTypes = DatePickerEnum.values();
		for (DatePickerEnum propertyType : propertyTypes)
		{
			if (propertyType.getValue().equalsIgnoreCase(nameToBeFound))
			{
			}
		}
		throw new IllegalArgumentException(nameToBeFound + ": is not a valid property");
	}
}
