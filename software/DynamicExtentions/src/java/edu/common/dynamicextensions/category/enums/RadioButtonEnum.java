
package edu.common.dynamicextensions.category.enums;

import edu.common.dynamicextensions.domain.userinterface.RadioButton;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;

public enum RadioButtonEnum {
	ISORDERED("IsOrdered") {

		/**
		 * Returns String representation of Default value for a control.
		 * @param control
		 * @return String
		 */
		public String getControlProperty(RadioButton control)
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
		public void setControlProperty(RadioButton control, String propertyToBeSet)
		{
			UserDefinedDEInterface userDefinedDEInterface = (UserDefinedDEInterface) (((AttributeMetadataInterface) control
					.getBaseAbstractAttribute()).getDataElement());
			userDefinedDEInterface.setIsOrdered(Boolean.valueOf(propertyToBeSet));
		}
	};

	private String name;

	RadioButtonEnum(String name)
	{
		this.name = name;
	}

	/** Abstract method for all enums to get control property */
	public abstract void setControlProperty(RadioButton control, String propertyToBeSet);

	/** Abstract method for all enums to set control property */
	public abstract String getControlProperty(RadioButton control);

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
	public static RadioButtonEnum getValue(String nameToBeFound)
	{

		RadioButtonEnum[] propertyTypes = RadioButtonEnum.values();
		for (RadioButtonEnum propertyType : propertyTypes)
		{
			if (propertyType.getValue().equalsIgnoreCase(nameToBeFound))
			{
				return propertyType;
			}
		}
		throw new IllegalArgumentException(nameToBeFound + ": is not a valid property");
	}
}
