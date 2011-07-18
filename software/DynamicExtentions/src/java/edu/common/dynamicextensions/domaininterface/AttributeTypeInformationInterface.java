/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author Vishvesh Mulay
 *@version 1.0
 */

package edu.common.dynamicextensions.domaininterface;

import java.text.ParseException;
import java.util.List;

public interface AttributeTypeInformationInterface
		extends
			DynamicExtensionBaseDomainObjectInterface
{

	/**
	 * Returns the data element associated with the attribute.The data element specify the
	 * source of permissible values.
	 * @return DataElementInterface
	 *
	 */
	DataElementInterface getDataElement();

	/**
	 * Sets the data element for this attribute type info.
	 * @param dataElementInterface data element interface
	 */
	void setDataElement(DataElementInterface dataElementInterface);

	/**
	 * removes the given data element from the data element collection.
	 * @param dataElementInterface data element interface
	 */
	void removeDataElement(DataElementInterface dataElementInterface);

	/**
	 * returns the default permissible value.
	 * @return return the default value for this attribute type.
	 */
	PermissibleValueInterface getDefaultValue();

	/**
	 * sets the default value for this attribute type.
	 * @param permissibleValueInterface  default value
	 */
	void setDefaultValue(PermissibleValueInterface permissibleValueInterface);

	/**
	 * The method returns the attribute type of the attribute based on it's attributeTypeInformation object.
	 * @return String attribute type
	 */
	String getDataType();

	/**
	 * This method returns the class type of the attribute based on it's attributeTypeInformation object.
	 * @return Class attribute type.
	 */
	Class getAttributeDataType();

	/**
	 *Return the permissible value object for the given string.
	 * @param value string value from which to create the object
	 * @return permissible value object from the given Value.
	 * @throws ParseException exception.
	 */
	PermissibleValueInterface getPermissibleValueForString(String value) throws ParseException;

	/**
	 * @return default value converted to string
	 *
	 */
	String getDefaultValueAsString();
	
	List<String> getConditions();
}
