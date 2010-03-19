
package edu.common.dynamicextensions.category.enums;

/**
 * Enum for attribute specific rules.
 * @author rajesh_vyas
 *
 */
public enum AttributeEnum {
	CALCULATED("IsCalculated"), DEFAULTVALUE("defaultValue");

	private AttributeEnum(String name)
	{
		this.name = name;
	}

	private String name;

	/**
	 * Returns name of a Enum. 
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns AttributeEnum for a given String.
	 * @param attributeEnumName
	 * @return
	 */
	public static AttributeEnum getAttributeEnum(String attributeEnumName)
	{
		AttributeEnum[] values = AttributeEnum.values();
		AttributeEnum attributeEnumMatched = null;
		for (AttributeEnum attributeEnum : values)
		{
			if (attributeEnum.getName().equalsIgnoreCase(attributeEnumName))
			{
				attributeEnumMatched = attributeEnum;
			}
		}
		return attributeEnumMatched;
	}
}
