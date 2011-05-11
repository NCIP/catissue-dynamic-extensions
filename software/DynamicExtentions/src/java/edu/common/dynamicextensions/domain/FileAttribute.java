/**
 *
 */

package edu.common.dynamicextensions.domain;

import edu.wustl.common.audit.AuditableObject;

/**
 * @author suhas_khot
 *
 */
public class FileAttribute extends AuditableObject
{

	/**
	 * added default serial version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * this is the name of the file
	 */
	private String fileName;

	/**
	 * file content e.g. MIME text etc.
	 */
	private String contentType;

	/**
	 * The record id.
	 */
	private Long recordId;

	/**
	 *
	 * The entity id.
	 */
	private Long entityId;

	/**
	 * @return the fileName
	 */
	public String getFileName()
	{
		return fileName;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType()
	{
		return contentType;
	}

	/**
	 * @return the recordId
	 */
	public Long getRecordId()
	{
		return recordId;
	}

	/**
	 * @return the entityId
	 */
	public Long getEntityId()
	{
		return entityId;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	/**
	 * @param recordId the recordId to set
	 */
	public void setRecordId(Long recordId)
	{
		this.recordId = recordId;
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(Long entityId)
	{
		this.entityId = entityId;
	}

}
