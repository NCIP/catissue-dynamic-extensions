
package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.StringTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.StringValueInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerConstantsInterface;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:08 PM
 * @hibernate.joined-subclass table="DYEXTN_STRING_TYPE_INFO"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class StringAttributeTypeInformation extends AttributeTypeInformation
		implements
			StringTypeInformationInterface
{

	/**
	 * Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = 8457915421460880246L;

	/**
	 * The size of the field.
	 */
	protected Integer size;

	/**
	 * This method returns the length of the string.
	 * @hibernate.property name="size" type="integer" column="MAX_SIZE"
	 * @return Returns the length of the string.
	 */
	public Integer getSize()
	{
		return size;
	}

	/**
	 * Sets the size.
	 *
	 * @param size The size to set.
	 */
	public void setSize(Integer size)
	{
		this.size = size;
	}

	/**
	 * Gets the data type.
	 *
	 * @return the data type
	 *
	 * @see edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface#getDataType()
	 */
	public String getDataType()
	{
		return EntityManagerConstantsInterface.STRING_ATTRIBUTE_TYPE;
	}

	/**
	 * Gets the permissible value for string.
	 *
	 * @param value the value
	 *
	 * @return the permissible value for string
	 *
	 * @see edu.common.dynamicextensions.domain.AttributeTypeInformation#getPermissibleValueForString(java.lang.String)
	 */
	public PermissibleValueInterface getPermissibleValueForString(String value)
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		StringValueInterface stringValue = factory.createStringValue();
		stringValue.setValue(value);

		return stringValue;
	}

	/**
	 * (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface#getAttributeDataType()
	 * @return Class type for attribute.
	 */
	public Class getAttributeDataType()
	{
		return String.class;
	}

}