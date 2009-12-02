
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
	protected final String baseDir;
	/**
	 * @param filePath file path
	 * @throws DynamicExtensionsSystemException
	 */
	public FileReader(String filePath) throws DynamicExtensionsSystemException
	{
		this.filePath = getSystemIndependantFilePath(filePath);
		baseDir="";
	}

	/**
	 * Overloaded constructor with base directory
	 * @param filePath file path
	 * @param baseDirectory base directory
	 * @throws DynamicExtensionsSystemException exception
	 */
	public FileReader(String filePath,String baseDirectory) throws DynamicExtensionsSystemException
	{
		baseDir=baseDirectory;
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
	public String getSystemIndependantFilePath(String path)
			throws DynamicExtensionsSystemException
	{
		if(baseDir!=null && !"".equals(baseDir))
		{
			path = baseDir+"/"+path;
		}
		File file = new File(path.replace(" ", "%20"));
		return file.getAbsolutePath();
	}

}
