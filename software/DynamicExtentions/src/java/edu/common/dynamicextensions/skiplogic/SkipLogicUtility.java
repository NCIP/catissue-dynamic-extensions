/**
 *
 */

package edu.common.dynamicextensions.skiplogic;

import java.text.ParseException;
import java.util.Collection;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.DataElementInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;

/**
 * @author Gaurav_mehta
 *
 */
public final class SkipLogicUtility
{

	private SkipLogicUtility()
	{
	}

	/**
	 * Gets the default value for control.
	 * @param value the value
	 * @param targetControl the target control
	 * @return the default value for control
	 * @throws ParseException the parse exception
	 */
	public static PermissibleValueInterface getDefaultValueForControl(String value,
			ControlInterface targetControl) throws ParseException
	{
		CategoryAttributeInterface attribute = (CategoryAttributeInterface) targetControl
				.getAttibuteMetadataInterface();
		AbstractAttributeInterface abstractAttribute = attribute.getAbstractAttribute();
		AttributeTypeInformationInterface attributeTypeInfo = DynamicExtensionsUtility
				.getAttributeTypeInformation(abstractAttribute);
		PermissibleValueInterface defaultValue = attributeTypeInfo
				.getPermissibleValueForString(value);

		for (DataElementInterface dataElement : attribute.getDataElementCollection())
		{
			UserDefinedDEInterface userDefinedDe = (UserDefinedDEInterface) dataElement;
			Collection<PermissibleValueInterface> allPermissibleValues = userDefinedDe
					.getPermissibleValueCollection();
			if (allPermissibleValues.contains(defaultValue))
			{
				for (PermissibleValueInterface permissibleValue : allPermissibleValues)
				{
					if (permissibleValue.equals(defaultValue))
					{
						defaultValue.setObjectValue(permissibleValue.getValueAsObject());
						break;
					}
				}
			}
		}
		return defaultValue;
	}

}
