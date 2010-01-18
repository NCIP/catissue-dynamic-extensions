
package edu.common.dynamicextensions.category.enums;

import edu.common.dynamicextensions.domain.CategoryAttribute;
import edu.common.dynamicextensions.domain.StringAttributeTypeInformation;
import edu.common.dynamicextensions.domain.userinterface.TextArea;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;

public enum TextAreaEnum {
	WIDTH("WIDTH") {

		public String getControlProperty(TextArea control)
		{
			Integer rows2 = control.getRows();
			return String.valueOf(rows2);
		}

		public void setControlProperty(TextArea control, String propertyToBeSet)
		{
			control.setRows(Integer.valueOf(propertyToBeSet));
		}
	},
	HEIGHT("HEIGHT") {

		public String getControlProperty(TextArea control)
		{
			Integer columns2 = control.getColumns();
			return String.valueOf(columns2);
		}

		public void setControlProperty(TextArea control, String propertyToBeSet)
		{
			control.setColumns(Integer.valueOf(propertyToBeSet));
		}
	},
	MAXLENGTH("MAXLENGTH") {

		public String getControlProperty(TextArea control)
		{
			CategoryAttribute baseAbstractAttribute2 = (CategoryAttribute) control
					.getBaseAbstractAttribute();
			AttributeInterface attribute = baseAbstractAttribute2.getAttribute();
			StringAttributeTypeInformation attributeTypeInformation = (StringAttributeTypeInformation) attribute
					.getAttributeTypeInformation();
			Integer maxLength = attributeTypeInformation.getSize();
			return String.valueOf(maxLength);
		}

		public void setControlProperty(TextArea control, String propertyToBeSet)
		{
			CategoryAttribute baseAbstractAttribute2 = (CategoryAttribute) control
					.getBaseAbstractAttribute();
			AttributeInterface attribute = baseAbstractAttribute2.getAttribute();
			StringAttributeTypeInformation attributeTypeInformation = (StringAttributeTypeInformation) attribute
					.getAttributeTypeInformation();
			attributeTypeInformation.setSize(Integer.valueOf(propertyToBeSet));
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
		for (TextAreaEnum propertyType : propertyTypes)
		{
			if (propertyType.getValue().equalsIgnoreCase(nameToBeFound))
			{
				return propertyType;
			}
		}
		return null;
	}
}