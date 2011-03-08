
package edu.common.dynamicextensions.domain;

import java.sql.Clob;

import edu.common.dynamicextensions.domaininterface.ByteArrayTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:07 PM
 * @hibernate.joined-subclass table="DYEXTN_BYTE_ARRAY_TYPE_INFO"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class ByteArrayAttributeTypeInformation extends AttributeTypeInformation
		implements
			ByteArrayTypeInformationInterface
{

	/**
	 * Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = 7925152800204580441L;

	/**
	 * Content type for this file.
	 */
	protected String contentType;

	/**
	 * This method returns the Content type of the binary data (or file), e.g. JPG, DOC etc..
	 * @hibernate.property name="contentType" type="string" column="CONTENT_TYPE"
	 * @return the Content type of the binary data (or file).
	 */
	public String getContentType()
	{
		return this.contentType;
	}

	/**
	 * This method sets the Content type of the binary data (or file) to be stored.
	 * @param contentType The contentType to be set.
	 */
	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	public String getDataType()
	{

		return null;
	}

	/**
	 *
	 */
	public PermissibleValueInterface getPermissibleValueForString(String value)
	{
		return null;
	}

	/**
	 * (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface#getAttributeDataType()
	 * @return Class type for attribute.
	 */
	public Class getAttributeDataType()
	{
		return Clob.class;
	}

	
}