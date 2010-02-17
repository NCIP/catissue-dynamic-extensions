
package edu.common.dynamicextensions.category.enums;

import edu.common.dynamicextensions.domain.userinterface.TextArea;

public enum TextAreaEnum {
	WIDTH("Columns") {

		/**
		 * Returns String representation of width value for a control.
		 */
		public String getControlProperty(TextArea control)
		{
			Integer columns2 = control.getColumns();
			return String.valueOf(columns2);
		}

		/**
		 * Sets String representation of width value for a control.
		 */
		public void setControlProperty(TextArea control, String propertyToBeSet)
		{
			control.setRows(Integer.valueOf(propertyToBeSet));
		}
	},
	HEIGHT("Rows") {

		/**
		 * Returns String representation of height value for a control.
		 */
		public String getControlProperty(TextArea control)
		{
			Integer rows2 = control.getRows();
			return String.valueOf(rows2);
		}

		/**
		 * Sets String representation of height value for a control.
		 */
		public void setControlProperty(TextArea control, String propertyToBeSet)
		{
			control.setColumns(Integer.valueOf(propertyToBeSet));
		}
	};

	private String name;

	TextAreaEnum(String name)
	{
		this.name = name;
	}

	/** Abstract method for all enums to get control property */
	public abstract void setControlProperty(TextArea control, String propertyToBeSet);

	/** Abstract method for all enums to set control property */
	public abstract String getControlProperty(TextArea control);

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
	public static TextAreaEnum getValue(String nameToBeFound)
	{
		TextAreaEnum[] propertyTypes = TextAreaEnum.values();
		TextAreaEnum matchedEnum = null;
		for (TextAreaEnum propertyType : propertyTypes)
		{
			if (propertyType.getValue().equalsIgnoreCase(nameToBeFound))
			{
				matchedEnum = propertyType;
			}
		}
		return matchedEnum;
	}
}