
package edu.common.dynamicextensions.category.enums;

import edu.common.dynamicextensions.domain.userinterface.TextField;

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