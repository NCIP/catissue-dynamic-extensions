
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
	MAXLENGTH("width") {

		/**
		 * Returns String representation of max-length value for a control.
		 */
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

		/**
		 * Sets String representation of maxlength value for a control.
		 */
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

		/**
		 * Returns String representation of range-min value for a control.
		 */
		public String getControlProperty(TextField control)
		{
			boolean result = ControlsUtility.isRuleDefined(CategoryCSVConstants.MIN, control);
			//ControlsUtility.
			String ruleName = null;
			if (result)
			{
				ruleName = CategoryCSVConstants.MIN;
			}
			return ruleName;
		}

		/**
		 * Sets String representation of range-min value for a control.
		 */
		public void setControlProperty(TextField control, String propertyToBeSet)
		{
			ControlsUtility.defineRule(CategoryCSVConstants.MIN, control);
		}
	},
	RANGEMAX("RANGEMAX") {

		/**
		 * Returns String representation of range-max value for a control.
		 */
		public String getControlProperty(TextField control)
		{
			boolean result = ControlsUtility.isRuleDefined(CategoryCSVConstants.MAX, control);
			String ruleName = null;
			if (result)
			{
				ruleName = CategoryCSVConstants.MAX;
			}
			return ruleName;
		}

		/**
		 * Sets String representation of range-max value for a control.
		 */
		public void setControlProperty(TextField control, String propertyToBeSet)
		{
			ControlsUtility.defineRule(CategoryCSVConstants.MAX, control);
		}
	},
	ISPASSWORD("ISPASSWORD") {

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
	ISURL("ISURL") {

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