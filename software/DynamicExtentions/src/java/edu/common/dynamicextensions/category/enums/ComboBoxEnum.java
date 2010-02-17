
package edu.common.dynamicextensions.category.enums;

import edu.common.dynamicextensions.domain.userinterface.ComboBox;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;

public enum ComboBoxEnum {
	ISORDERED("IsOrdered") {

		public String getControlProperty(ComboBox control)
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

		public void setControlProperty(ComboBox control, String propertyToBeSet)
		{
			UserDefinedDEInterface userDefinedDEInterface = (UserDefinedDEInterface) (((AttributeMetadataInterface) control
					.getBaseAbstractAttribute()).getDataElement());
			userDefinedDEInterface.setIsOrdered(Boolean.valueOf(propertyToBeSet));
		}
	};

	private String name;

	ComboBoxEnum(String name)
	{
		this.name = name;
	}

	/** Abstract method for all enums to get control property */
	public abstract void setControlProperty(ComboBox control, String propertyToBeSet);

	/** Abstract method for all enums to set control property */
	public abstract String getControlProperty(ComboBox control);

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
	public static ComboBoxEnum getValue(String nameToBeFound)
	{

		ComboBoxEnum[] propertyTypes = ComboBoxEnum.values();
		for (ComboBoxEnum propertyType : propertyTypes)
		{
			if (propertyType.getValue().equalsIgnoreCase(nameToBeFound))
			{
				return propertyType;
			}
		}
		throw new IllegalArgumentException(nameToBeFound + ": is not a valid property");
	}
}
