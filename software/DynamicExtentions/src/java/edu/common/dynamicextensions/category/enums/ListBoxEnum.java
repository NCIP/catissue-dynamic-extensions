
package edu.common.dynamicextensions.category.enums;

import edu.common.dynamicextensions.domain.userinterface.ListBox;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;

public enum ListBoxEnum {

	ISORDERED("IsOrdered") {

		/**
		 * Returns String representation of Default value for a control.
		 * @param control
		 * @return String
		 */
		public String getControlProperty(ListBox control)
		{
			String isOrderedString = null;
			UserDefinedDEInterface userDefinedDEInterface = (UserDefinedDEInterface) (((AttributeMetadataInterface) control
					.getBaseAbstractAttribute()).getDataElement());
			Boolean isOrdered2 = userDefinedDEInterface.getIsOrdered();
			if (isOrdered2 != null)
			{
				isOrderedString = String.valueOf(isOrdered2);
			}
			return isOrderedString;
		}

		/**
		 * Sets Control default value.
		 * @param control
		 * @param propertyToBeSet
		 */
		public void setControlProperty(ListBox control, String propertyToBeSet)
		{
			UserDefinedDEInterface userDefinedDEInterface = (UserDefinedDEInterface) (((AttributeMetadataInterface) control
					.getBaseAbstractAttribute()).getDataElement());
			userDefinedDEInterface.setIsOrdered(Boolean.valueOf(propertyToBeSet));
		}
	},

	ISMULTILINE("ISMULTILINE") {

		/**
		 * Returns String representation of multiline value for a control.
		 * @param control
		 * @return String
		 */
		public String getControlProperty(ListBox control)
		{
			String multiSelectString = null;
			if (control.getIsMultiSelect() != null)
			{
				multiSelectString = String.valueOf(control.getIsMultiSelect());
			}
			return multiSelectString;
		}

		/**
		 * Sets String representation of multiline value for a control.
		 * @param control
		 * @param propertyToBeSet
		 */
		public void setControlProperty(ListBox control, String propertyToBeSet)
		{
			control.setIsMultiSelect(Boolean.valueOf(propertyToBeSet));
		}
	},
	NUMBEROFROWS("NUMBEROFROWS") {

		/**
		 * Returns String representation of Number of rows value for a control.
		 * @param control
		 * @return String
		 */
		public String getControlProperty(ListBox control)
		{
			String numberOfRowsString = null;
			if (control.getNoOfRows() != null)
			{
				numberOfRowsString = String.valueOf(control.getNoOfRows());
			}
			return numberOfRowsString;
		}

		/**
		 * Sets String representation of number of rows value for a control.
		 * @param control
		 * @param propertyToBeSet
		 */
		public void setControlProperty(ListBox control, String propertyToBeSet)
		{
			control.setNoOfRows(Integer.valueOf(propertyToBeSet));
		}
	},
	ISAUTOCOMPLETE("IsUsingAutoCompleteDropdown") {

		/**
		 * Returns String representation of Autocomplete value for a control.
		 * @param control
		 * @return String
		 */
		public String getControlProperty(ListBox control)
		{
			String autoSelectString = null;
			if (control.getIsUsingAutoCompleteDropdown() != null)
			{
				autoSelectString = String.valueOf(control.getIsUsingAutoCompleteDropdown());
			}
			return autoSelectString;
		}

		/**
		 * Sets String representation of Autocomplete value for a control.
		 * @param control
		 * @param propertyToBeSet
		 */
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
