
package edu.common.dynamicextensions.category.enums;

import edu.common.dynamicextensions.domain.AttributeTypeInformation;
import edu.common.dynamicextensions.domain.CategoryAttribute;
import edu.common.dynamicextensions.domain.DoubleAttributeTypeInformation;
import edu.common.dynamicextensions.domain.IntegerAttributeTypeInformation;
import edu.common.dynamicextensions.domain.StringAttributeTypeInformation;
import edu.common.dynamicextensions.domain.userinterface.TextField;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.parser.CategoryCSVConstants;

public enum TextFieldEnum {
	MAXLENGTH("MAXLENGTH") {

		public String getControlProperty(TextField control)
		{
			CategoryAttribute baseAbstractAttribute2 = (CategoryAttribute) control
					.getBaseAbstractAttribute();
			AttributeInterface attribute = baseAbstractAttribute2.getAttribute();

			AttributeTypeInformation attributeTypeInformation = (AttributeTypeInformation) attribute
					.getAttributeTypeInformation();

			Integer maxLength = null;
			if (attributeTypeInformation instanceof StringAttributeTypeInformation)
			{
				StringAttributeTypeInformation strAttributeTypeInformation = (StringAttributeTypeInformation) attributeTypeInformation;
				maxLength = strAttributeTypeInformation.getSize();
			}
			else if (attributeTypeInformation instanceof DoubleAttributeTypeInformation)
			{
				DoubleAttributeTypeInformation strAttributeTypeInformation = (DoubleAttributeTypeInformation) attributeTypeInformation;
				maxLength = strAttributeTypeInformation.getDigits();
			}
			else if (attributeTypeInformation instanceof IntegerAttributeTypeInformation)
			{
				IntegerAttributeTypeInformation strAttributeTypeInformation = (IntegerAttributeTypeInformation) attributeTypeInformation;
				maxLength = strAttributeTypeInformation.getDecimalPlaces();
			}

			return String.valueOf(maxLength);
		}

		public void setControlProperty(TextField control, String propertyToBeSet)
		{
			CategoryAttribute baseAbstractAttribute2 = (CategoryAttribute) control
					.getBaseAbstractAttribute();
			AttributeInterface attribute = baseAbstractAttribute2.getAttribute();
			AttributeTypeInformation attributeTypeInformation = (AttributeTypeInformation) attribute
					.getAttributeTypeInformation();
			if (attributeTypeInformation instanceof StringAttributeTypeInformation)
			{
				StringAttributeTypeInformation strAttributeTypeInformation = (StringAttributeTypeInformation) attributeTypeInformation;
				strAttributeTypeInformation.setSize(Integer.valueOf(propertyToBeSet));
			}
			else if (attributeTypeInformation instanceof DoubleAttributeTypeInformation)
			{
				DoubleAttributeTypeInformation strAttributeTypeInformation = (DoubleAttributeTypeInformation) attributeTypeInformation;
				strAttributeTypeInformation.setDigits(Integer.valueOf(propertyToBeSet));
			}
			else if (attributeTypeInformation instanceof IntegerAttributeTypeInformation)
			{
				IntegerAttributeTypeInformation strAttributeTypeInformation = (IntegerAttributeTypeInformation) attributeTypeInformation;
				strAttributeTypeInformation.setDecimalPlaces(Integer.valueOf(propertyToBeSet));
			}
		}
	},
	RANGEMIN("RANGEMIN") {

		public String getControlProperty(TextField control)
		{
			boolean result = ControlsUtility.isRuleDefined(CategoryCSVConstants.MIN, control);
			//ControlsUtility.
			String ruleName = null;
			if (result == true)
			{
				ruleName = CategoryCSVConstants.MIN;
			}
			return ruleName;
		}

		public void setControlProperty(TextField control, String propertyToBeSet)
		{
			ControlsUtility.defineRule(CategoryCSVConstants.MIN, control);
		}
	},
	RANGEMAX("RANGEMAX") {

		public String getControlProperty(TextField control)
		{
			boolean result = ControlsUtility.isRuleDefined(CategoryCSVConstants.MAX, control);
			String ruleName = null;
			if (result == true)
			{
				ruleName = CategoryCSVConstants.MAX;
			}
			return ruleName;
		}

		public void setControlProperty(TextField control, String propertyToBeSet)
		{
			ControlsUtility.defineRule(CategoryCSVConstants.MAX, control);
		}
	},
	ISPASSWORD("ISPASSWORD") {

		public String getControlProperty(TextField control)
		{
			if (control.getIsPassword() == null)
			{
				return null;
			}
			return String.valueOf(control.getIsPassword());
		}

		public void setControlProperty(TextField control, String propertyToBeSet)
		{
			control.setIsPassword(Boolean.valueOf(propertyToBeSet));
		}
	},
	ISURL("ISURL") {

		public String getControlProperty(TextField control)
		{
			if (control.getIsUrl() == null)
			{
				return null;
			}
			return String.valueOf(control.getIsUrl());
		}

		public void setControlProperty(TextField control, String propertyToBeSet)
		{
			control.setIsUrl(Boolean.valueOf(propertyToBeSet));
		}
	},
	COLUMNS("COLUMNS") {

		public String getControlProperty(TextField control)
		{
			if (control.getColumns() == null)
			{
				return null;
			}
			return String.valueOf(control.getColumns());
		}

		public void setControlProperty(TextField control, String propertyToBeSet)
		{
			control.setColumns(Integer.valueOf(propertyToBeSet));
		}
	};

	private String name;

	TextFieldEnum()
	{

	}

	TextFieldEnum(String name)
	{
		this.name = name;
	}

	public abstract void setControlProperty(TextField control, String propertyToBeSet);

	public abstract String getControlProperty(TextField control);

	public String getValue()
	{
		return name;
	}

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