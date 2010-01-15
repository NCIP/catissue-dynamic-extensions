
package edu.common.dynamicextensions.domain.userinterface.enums;

public enum AttributeEnum {
	PERMISSIBLEVALUES("PERMISSIBLEVALUES"), CALCULATED("CALCULATED"), DEFAULTVALUE("DEFAULTVALUE");

	private AttributeEnum()
	{
		// TODO Auto-generated constructor stub
	}

	private AttributeEnum(String name)
	{
		this.name = name;
	}

	private String name;

	public String getName()
	{
		return name;
	}

	public static AttributeEnum getAttributeEnum(String attributeEnumName)
	{
		AttributeEnum[] values = AttributeEnum.values();

		for (AttributeEnum attributeEnum : values)
		{
			if (attributeEnum.getName().equalsIgnoreCase(attributeEnumName))
			{
				return attributeEnum;
			}
		}
		return null;
	}
}
