
package edu.common.dynamicextensions.domain.userinterface.enums;

import edu.common.dynamicextensions.domain.CategoryAttribute;
import edu.common.dynamicextensions.domain.userinterface.Control;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.parser.CategoryCSVConstants;
import edu.wustl.common.util.logger.Logger;

/**
 * @author rajesh_vyas
 *
 */
public enum ControlEnum {
	DEFAULTVALUE("DEFAULTVALUE") {

		/**
		 * Returns Default value for a control.
		 * @param Control 
		 */
		public String getControlProperty(Control control)
		{
			CategoryAttribute categoryAttribute = (CategoryAttribute) control
					.getBaseAbstractAttribute();
			AttributeInterface attribute = categoryAttribute.getAttribute();
			AttributeTypeInformationInterface attributeTypeInformation = attribute
					.getAttributeTypeInformation();
			PermissibleValueInterface defaultValue2 = attributeTypeInformation.getDefaultValue();

			if (defaultValue2 != null && defaultValue2.getValueAsObject() != null)
			{
				Object valueAsObject = defaultValue2.getValueAsObject();
				return valueAsObject.toString();
			}
			return null;
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

		public String getControlProperty(Control control)
		{
			CategoryAttribute categoryAttribute = (CategoryAttribute) control
					.getBaseAbstractAttribute();
			AttributeInterface attribute = categoryAttribute.getAttribute();
			Boolean isIdentified = attribute.getIsIdentified();
			if (isIdentified == null)
			{
				return null;
			}
			return String.valueOf(isIdentified);
		}

		public void setControlProperty(Control control, String propertyToBeSet)
		{
			CategoryAttribute categoryAttribute = (CategoryAttribute) control
					.getBaseAbstractAttribute();
			AttributeInterface attribute = categoryAttribute.getAttribute();
			attribute.setIsIdentified(Boolean.valueOf(propertyToBeSet));
		}
	},
	REQUIRED("REQUIRED") {

		public String getControlProperty(Control control)
		{

			String ruleName = null;
			boolean result = ControlsUtility.isRuleDefined(CategoryCSVConstants.REQUIRED, control);

			if (result == true)
			{
				ruleName = "yes";
			}
			return ruleName;
		}

		public void setControlProperty(Control control, String propertyToBeSet)
		{
			if (propertyToBeSet.equalsIgnoreCase("yes"))
			{
				ControlsUtility.defineRule(CategoryCSVConstants.REQUIRED, control);
			}
		}
	};

	private static final Logger LOGGER = Logger.getCommonLogger(ControlEnum.class);

	private String name;

	ControlEnum()
	{

	}

	ControlEnum(String name)
	{
		this.name = name;
	}

	public abstract void setControlProperty(Control control, String propertyToBeSet);

	public abstract String getControlProperty(Control control);

	public String getValue()
	{
		return name;
	}

	public static ControlEnum getValue(String nameToBeFound)
	{

		ControlEnum[] propertyTypes = ControlEnum.values();
		for (ControlEnum propertyType : propertyTypes)
		{
			if (propertyType.getValue().equalsIgnoreCase(nameToBeFound))
			{
				return propertyType;
			}
		}
		return null;
	}
}
