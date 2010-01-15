
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

	ListBoxEnum()
	{

	}

	ListBoxEnum(String name)
	{
		this.name = name;
	}

	public abstract void setControlProperty(ListBox control, String propertyToBeSet);

	public abstract String getControlProperty(ListBox control);

	public String getValue()
	{
		return name;
	}

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
