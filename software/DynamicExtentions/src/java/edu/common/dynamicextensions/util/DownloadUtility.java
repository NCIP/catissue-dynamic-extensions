/**
 *
 */
package edu.common.dynamicextensions.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;


/**
 * @author gaurav_mehta
 *
 */
public class DownloadUtility
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	private static final Logger LOGGER = Logger.getCommonLogger(ZipUtility.class);


	/**
	 * This method will download the Zip file usin the outputStream in request in the provided tempDirName.
	 * If tempDirName dir does not exists then it will create it first & then download the zip in that folder.
	 * @param request from which to download the Zip file.
	 * @param tempDirName directory name in which to download it.
	 * @throws IOException Exception.
	 * @throws DynamicExtensionsSystemException Exception
	 */
	public static void downloadZipFile(HttpServletRequest request, String tempDirName,
			String fileName) throws IOException, DynamicExtensionsSystemException
	{
		BufferedInputStream reader = null;
		BufferedOutputStream fileWriter = null;
		DirOperationsUtility.getInstance().createNewTempDirectory(tempDirName);
		String completeFileName = tempDirName + File.separator + fileName;
		try
		{
			reader = new BufferedInputStream(request.getInputStream());
			File file = new File(completeFileName);
			if (file.exists() && !file.delete())
			{
				LOGGER.error("Can not delete file : " + file);
			}
			fileWriter = new BufferedOutputStream(new FileOutputStream(file));

			byte[] buffer = new byte[1024];
			int len = reader.read(buffer);
			while (len >= 0)
			{
				fileWriter.write(buffer, 0, len);
				len = reader.read(buffer);
			}
			fileWriter.flush();

		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Exception occured while downloading the zip on server", e);

		}
		finally
		{
			if (fileWriter != null)
			{
				fileWriter.close();
			}
			if (reader != null)
			{
				reader.close();
			}
		}
		ZipUtility.extractZipToDestination(completeFileName, tempDirName);
	}
}
