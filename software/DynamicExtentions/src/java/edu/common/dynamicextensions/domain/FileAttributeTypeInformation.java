
package edu.common.dynamicextensions.domain;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import edu.common.dynamicextensions.domaininterface.FileTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerConstantsInterface;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:06 PM
 * @hibernate.joined-subclass table="DYEXTN_FILE_TYPE_INFO"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class FileAttributeTypeInformation extends AttributeTypeInformation
		implements
			FileTypeInformationInterface
{

	/**
	 * maximum file size (in MB)
	 */
	private Float maxFileSize;

	/**
	 * allowed file types for this attribute
	 */
	private Collection<FileExtension> fileExtensionCollection = new HashSet<FileExtension>();

	/**
	 * @hibernate.set name="fileExtensionCollection" table="DYEXTN_FILE_EXTENSIONS"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="ATTRIBUTE_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.FileExtension"
	 * @return Returns the fileExtensionCollection.
	 */

	public Collection<FileExtension> getFileExtensionCollection()
	{
		return fileExtensionCollection;
	}

	/**
	 * @param fileExtensionCollection The fileExtensionCollection to set.
	 */
	public void setFileExtensionCollection(Collection<FileExtension> fileExtensionCollection)
	{
		this.fileExtensionCollection = fileExtensionCollection;
	}

	/**
	 * @return Returns the maxFileSize.
	 * @hibernate.property name="maxFileSize" column="MAX_FILE_SIZE" type="float"
	 */

	public Float getMaxFileSize()
	{
		return maxFileSize;
	}

	/**
	 * @param maxFileSize The maxFileSize to set.
	 */
	public void setMaxFileSize(Float maxFileSize)
	{
		this.maxFileSize = maxFileSize;
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.FileTypeInformationInterface#addFileExtension(edu.common.dynamicextensions.domain.FileExtension)
	 */
	public void addFileExtension(FileExtension fileExtension)
	{
		if (fileExtensionCollection == null)
		{
			fileExtensionCollection = new HashSet<FileExtension>();
		}
		fileExtensionCollection.add(fileExtension);
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.FileTypeInformationInterface#removeFileExtension(edu.common.dynamicextensions.domain.FileExtension)
	 */
	public void removeFileExtension(FileExtension fileExtension)
	{
		if (fileExtensionCollection != null)
		{
			fileExtensionCollection.remove(fileExtension);
		}
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.FileTypeInformationInterface#removeAllFileExtensions()
	 */
	public void removeAllFileExtensions()
	{
		if (fileExtensionCollection != null)
		{
			fileExtensionCollection.clear();
		}
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface#getDataType()
	 */
	public String getDataType()
	{
		return EntityManagerConstantsInterface.FILE_ATTRIBUTE_TYPE;
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
		return File.class;
	}
}