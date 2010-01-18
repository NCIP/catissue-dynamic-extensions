
package edu.common.dynamicextensions.category.enums;

import edu.common.dynamicextensions.domain.CategoryAttribute;
import edu.common.dynamicextensions.domain.userinterface.Control;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.parser.CategoryCSVConstants;

/**
 * This Enum sets all UIProperties that are common for all type of controls. 
 * @author rajesh_vyas
 *
 */
public enum ControlEnum {
	DEFAULTVALUE("DEFAULTVALUE") {

		/**
		 * Returns String representation of Default value for a control.
		 */
		public String getControlProperty(Control control)
		{
			CategoryAttribute categoryAttribute = (CategoryAttribute) control
					.getBaseAbstractAttribute();
			AttributeInterface attribute = categoryAttribute.getAttribute();
			AttributeTypeInformationInterface attributeTypeInformation = attribute
					.getAttributeTypeInformation();
			PermissibleValueInterface defaultValue2 = attributeTypeInformation.getDefaultValue();
			String defaultValue = null;
			if (defaultValue2 != null && defaultValue2.getValueAsObject() != null)
			{
				Object valueAsObject = defaultValue2.getValueAsObject();
				defaultValue = valueAsObject.toString();
			}
			return defaultValue;
		}

		/**
		 * Sets Control default value.
		 */
		public void setControlProperty(Control control, String propertyToBeSet)
		{
			CategoryAttribute categoryAttribute = (CategoryAttribute) control
					.getBaseAbstractAttribute();
			AttributeInterface attribute = categoryAttribute.getAttribute();
			attribute.getAttributeTypeInformation();
		}
	},
	PHI("PHI") {

		/**
		 * Returns String representation for PHI value. 
		 */
		public String getControlProperty(Control control)
		{
			CategoryAttribute categoryAttribute = (CategoryAttribute) control
					.getBaseAbstractAttribute();
			AttributeInterface attribute = categoryAttribute.getAttribute();
			Boolean isIdentified = attribute.getIsIdentified();
			String isPHIString = null;
			if (isIdentified != null)
			{
				isPHIString = String.valueOf(isIdentified);
			}
			return isPHIString;
		}

		/**
		 * Sets value for PHI.   
		 */
		public void setControlProperty(Control control, String propertyToBeSet)
		{
			CategoryAttribute categoryAttribute = (CategoryAttribute) control
					.getBaseAbstractAttribute();
			AttributeInterface attribute = categoryAttribute.getAttribute();
			attribute.setIsIdentified(Boolean.valueOf(propertyToBeSet));
		}
	},
	REQUIRED("REQUIRED") {

		/**
		 * Returns string represenation for Required rule of Control. 
		 */
		public String getControlProperty(Control control)
		{
			String ruleName = null;
			boolean result = ControlsUtility.isRuleDefined(CategoryCSVConstants.REQUIRED, control);

			if (result)
			{
				ruleName = "yes";
			}
			return ruleName;
		}

		/**
		 * Sets required Rule.
		 */
		public void setControlProperty(Control control, String propertyToBeSet)
		{
			if (propertyToBeSet.equalsIgnoreCase("yes"))
			{
				ControlsUtility.defineRule(CategoryCSVConstants.REQUIRED, control);
			}
		}
	};

	private String name;

	ControlEnum(String name)
	{
		this.name = name;
	}

	/** Abstract method for all enums to set control property */
	public abstract void setControlProperty(Control control, String propertyToBeSet);

	/** Abstract method for all enums to get control property */
	public abstract String getControlProperty(Control control);

	/**
	 * Returns name of Enum.
	 * @return
	 */
	public String getValue()
	{
		return name;
	}

	/**
	 * Returns Enum for a given String.
	 * @param nameToBeFound
	 * @return
	 */
	public static ControlEnum getValue(String nameToBeFound)
	{
		ControlEnum[] propertyTypes = ControlEnum.values();
		ControlEnum matchedEnum = null;
		for (ControlEnum propertyType : propertyTypes)
		{
			if (propertyType.getValue().equalsIgnoreCase(nameToBeFound))
			{
				matchedEnum = propertyType;
			}
		}
		return matchedEnum;
	}
}
