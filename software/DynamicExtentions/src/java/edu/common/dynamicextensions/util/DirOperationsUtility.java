/**
 *
 */

package edu.common.dynamicextensions.util;

import java.io.File;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * @author gaurav_mehta
 *
 */
public final class DirOperationsUtility
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	private static final Logger LOGGER = Logger.getCommonLogger(DirOperationsUtility.class);

	private static DirOperationsUtility dirOperations;

	public static DirOperationsUtility getInstance()
	{
		if (dirOperations == null)
		{
			dirOperations = new DirOperationsUtility();
		}
		return dirOperations;
	}

	private DirOperationsUtility()
	{

	}

	/**
	 * This method will delete the directory with the name tempDirName if present & then will
	 * create the new one for use.
	 * @param tempDirName name of the directory to be created.
	 * @throws DynamicExtensionsSystemException Exception.
	 */
	public void createNewTempDirectory(String tempDirName) throws DynamicExtensionsSystemException
	{
		if ((tempDirName.length()!=0) && tempDirName != null)
		{
			File tempDir = new File(tempDirName);
			if (tempDir.exists())
			{
				deleteDirectory(new File(tempDirName));
			}
			if (!tempDir.mkdirs())
			{
				throw new DynamicExtensionsSystemException("Unable to create tempDirectory");
			}
		}
		else
		{
			LOGGER.info("Given temp directory is empty");
		}
	}

	/**
	 * This method will first of all delete all the files & folders
	 * present in the given file object & then will delete the given Directory.
	 * @param path directory which is to be deleted.
	 * @return true if deletion is succesfull.
	 */
	public boolean deleteDirectory(File path)
	{
		if (path.exists())
		{
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isDirectory())
				{
					deleteDirectory(files[i]);
				}
				else if (!files[i].delete())
				{
					LOGGER.error("Can not delete file " + files[i]);
				}
			}
		}
		return path.delete();
	}
}
