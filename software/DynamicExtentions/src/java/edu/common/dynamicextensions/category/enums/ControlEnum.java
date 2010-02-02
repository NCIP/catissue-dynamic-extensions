
package edu.common.dynamicextensions.category.enums;

import edu.common.dynamicextensions.domain.userinterface.Control;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.parser.CategoryCSVConstants;

/**
 * This Enum sets all UIProperties that are common for all type of controls. 
 * @author rajesh_vyas
 *
 */
public enum ControlEnum {

	REQUIRED("required") {

		/**
		 * Returns string represenation for Required rule of Control. 
		 */
		public String getControlProperty(Control control)
		{
			String ruleName = null;
			boolean result = ControlsUtility.isRuleDefined(CategoryCSVConstants.REQUIRED, control);

			if (result)
			{
				ruleName = "yes";
			}
			return ruleName;
		}

		/**
		 * Sets required Rule.
		 */
		public void setControlProperty(Control control, String propertyToBeSet)
		{
			if (propertyToBeSet.equalsIgnoreCase("yes"))
			{
				ControlsUtility.defineRule(CategoryCSVConstants.REQUIRED, control);
			}
		}
	};

	private String name;

	ControlEnum(String name)
	{
		this.name = name;
	}

	/** Abstract method for all enums to set control property */
	public abstract void setControlProperty(Control control, String propertyToBeSet);

	/** Abstract method for all enums to get control property */
	public abstract String getControlProperty(Control control);

	/**
	 * Returns name of Enum.
	 * @return
	 */
	public String getValue()
	{
		return name;
	}

	/**
	 * Returns Enum for a given String.
	 * @param nameToBeFound
	 * @return
	 */
	public static ControlEnum getValue(String nameToBeFound)
	{
		ControlEnum[] propertyTypes = ControlEnum.values();
		ControlEnum matchedEnum = null;
		for (ControlEnum propertyType : propertyTypes)
		{
			if (propertyType.getValue().equalsIgnoreCase(nameToBeFound))
			{
				matchedEnum = propertyType;
			}
		}
		return matchedEnum;
	}
}
