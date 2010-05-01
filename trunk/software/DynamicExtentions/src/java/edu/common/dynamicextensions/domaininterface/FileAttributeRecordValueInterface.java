
package edu.common.dynamicextensions.domaininterface;

public interface FileAttributeRecordValueInterface
		extends
			DynamicExtensionBaseDomainObjectInterface
{

	/**
	 * 
	 * @return
	 */
	Long getId();

	/**
	 * @return Returns the contentType.
	 */
	String getContentType();

	/**
	 * @param contentType The contentType to set.
	 */
	void setContentType(String contentType);

	/**
	 * @return Returns the fileContent.
	 */
	byte[] getFileContent();

	/**
	 * @param fileContent The fileContent to set.
	 */
	void setFileContent(byte[] fileContent);

	/**
	 * @return Returns the fileName.
	 */
	String getFileName();

	/**
	 * @param fileName The fileName to set.
	 */
	void setFileName(String fileName);

}
