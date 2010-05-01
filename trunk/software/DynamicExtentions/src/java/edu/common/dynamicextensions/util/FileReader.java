
package edu.common.dynamicextensions.util;

import java.io.File;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

/**
 * @author kunal_kamble
 *
 */
public class FileReader
{

	private String filePath;

	/**
	 * @param filePath file path
	 * @throws DynamicExtensionsSystemException
	 */
	public FileReader(String filePath) throws DynamicExtensionsSystemException
	{
		this.filePath = getSystemIndependantFilePath(filePath);
	}

	/**
	 * @return file path by replacing %20 by space 
	 * sinces spaces are replaced by %20 
	 */
	public String getFilePath()
	{
		return filePath.replace("%20", " ");
	}

	/**
	 * @param filePath 
	 */
	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}

	/**
	 * This method creates the system independent file path
	 * @param path
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public static String getSystemIndependantFilePath(String path)
			throws DynamicExtensionsSystemException
	{
		File file = new File(path.replace(" ", "%20"));
		return file.getAbsolutePath();
	}

}
