
package edu.common.dynamicextensions.category.enums;

import edu.common.dynamicextensions.domain.userinterface.ListBox;

public enum ListBoxEnum {
	ISMULTILINE("ISMULTILINE") {

		public String getControlProperty(ListBox control)
		{
			if (control.getIsMultiSelect() == null)
			{
				return null;
			}
			return String.valueOf(control.getIsMultiSelect());
		}

		public void setControlProperty(ListBox control, String propertyToBeSet)
		{
			control.setIsMultiSelect(Boolean.valueOf(propertyToBeSet));
		}
	},
	NUMBEROFROWS("NUMBEROFROWS") {

		public String getControlProperty(ListBox control)
		{
			if (control.getNoOfRows() == null)
			{
				return null;
			}
			return String.valueOf(control.getNoOfRows());
		}

		public void setControlProperty(ListBox control, String propertyToBeSet)
		{
			control.setNoOfRows(Integer.valueOf(propertyToBeSet));
		}
	},
	ISAUTOCOMPLETE("ISAUTOCOMPLETE") {

		public String getControlProperty(ListBox control)
		{
			if (control.getIsUsingAutoCompleteDropdown() == null)
			{
				return null;
			}
			return String.valueOf(control.getIsUsingAutoCompleteDropdown());
		}

		public void setControlProperty(ListBox control, String propertyToBeSet)
		{
			control.setIsUsingAutoCompleteDropdown(Boolean.valueOf(propertyToBeSet));
		}
	};

	private String name;

	ListBoxEnum(String name)
	{
		this.name = name;
	}

	/** Abstract method for all enums to get control property */
	public abstract void setControlProperty(ListBox control, String propertyToBeSet);

	/** Abstract method for all enums to set control property */
	public abstract String getControlProperty(ListBox control);

	/**
	 * Returns value of Enum.
	 * @return
	 */
	public String getValue()
	{
		return name;
	}

	/**
	 * Returns Enum for given String.
	 * @param nameToBeFound
	 * @return
	 */
	public static ListBoxEnum getValue(String nameToBeFound)
	{

		ListBoxEnum[] propertyTypes = ListBoxEnum.values();
		for (ListBoxEnum propertyType : propertyTypes)
		{
			if (propertyType.getValue().equalsIgnoreCase(nameToBeFound))
			{
				return propertyType;
			}
		}
		throw new IllegalArgumentException(nameToBeFound + ": is not a valid property");
	}
}
