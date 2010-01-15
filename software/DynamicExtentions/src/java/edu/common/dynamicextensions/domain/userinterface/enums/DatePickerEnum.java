
package edu.common.dynamicextensions.domain.userinterface.enums;

import edu.common.dynamicextensions.domain.userinterface.DatePicker;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.parser.CategoryCSVConstants;

public enum DatePickerEnum {
	RANGEMIN("RANGEMIN") {

		public String getControlProperty(DatePicker control)
		{
			boolean result = ControlsUtility.isRuleDefined(CategoryCSVConstants.MIN, control);
			String ruleName = null;
			if (result == true)
			{
				ruleName = CategoryCSVConstants.MIN;
			}
			return ruleName;
		}

		public void setControlProperty(DatePicker control, String propertyToBeSet)
		{
			ControlsUtility.defineRule(CategoryCSVConstants.MIN, control);
		}
	},
	RANGEMAX("RANGEMAX") {

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

		public void setControlProperty(DatePicker control, String propertyToBeSet)
		{
			ControlsUtility.defineRule(CategoryCSVConstants.MAX, control);
		}
	},
	ALLOWFUTUREDATE("ALLOWFUTUREDATE") {

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

		public void setControlProperty(DatePicker control, String propertyToBeSet)
		{
			ControlsUtility.defineRule(CategoryCSVConstants.ALLOW_FUTURE_DATE, control);
		}
	};

	private String name;

	DatePickerEnum()
	{

	}

	DatePickerEnum(String name)
	{
		this.name = name;
	}

	public abstract void setControlProperty(DatePicker control, String propertyToBeSet);

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
				return propertyType;
			}
		}
		throw new IllegalArgumentException(nameToBeFound + ": is not a valid property");
	}
}
