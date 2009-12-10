package edu.common.dynamicextensions.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;


/**
 * This class contains the utility method to create zip, extract zip etc.
 * @author pavan_kalantri
 *
 */
public class ZipUtility
{
	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	private static final Logger LOGGER = Logger.getCommonLogger(ZipUtility.class);

	/**
	 * This method will extract the given zip file in filename argument to the destination folder.
	 * if destination folder is null or empty string then it will extract the zip to the current base directory
	 * @param filename zip file to be extracted.
	 * @param destination folder where the file should be extracted.
	 * @throws DynamicExtensionsSystemException exception.
	 * @throws IOException exception.
	 */
	public static void extractZipToDestination(String filename, String destination)
			throws DynamicExtensionsSystemException, IOException
	{
		ZipInputStream zipinputstream = null;
		try
		{
			String destinationPath = "";
			if (destination != null && !"".equals(destination))
			{
				destinationPath = destination + "/";
			}
			zipinputstream = new ZipInputStream(new FileInputStream(filename));
			ZipEntry zipentry = zipinputstream.getNextEntry();
			while (zipentry != null)
			{
				if (!zipentry.isDirectory())
				{
					String entryName = zipentry.getName();
					extractZipEntryToFile(destinationPath, zipinputstream, entryName);
					zipinputstream.closeEntry();
				}
				zipentry = zipinputstream.getNextEntry();
			}
			zipinputstream.close();
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException("Can not extract the zip, zip may be currupted", e);
		}
		finally
		{
			if (zipinputstream != null)
			{
				zipinputstream.close();
			}
		}
	}

	/**
	 * It will only extract the given zipEntry i.e. particular file in the zip file
	 * to the destination path
	 * @param destinationPath directory in which to extract the directory.
	 * @param zipinputstream input stream
	 * @param entryName name of the file which is to be extracted
	 * @throws IOException exception
	 * @throws DynamicExtensionsSystemException exception
	 */
	private static void extractZipEntryToFile(String destinationPath,
			ZipInputStream zipinputstream, String entryName) throws IOException,
			DynamicExtensionsSystemException
	{
		byte[] buf = new byte[1024];
		FileOutputStream fileoutputstream = null;
		try
		{
			File newFile = new File(destinationPath + entryName);
			File parentFile = newFile.getParentFile();
			if (parentFile != null && !parentFile.exists() && !parentFile.mkdirs())
			{
				// this is condition when mkdirs is failed to create the directories
				throw new DynamicExtensionsSystemException("Can not create directory " + parentFile);
			}
			fileoutputstream = new FileOutputStream(newFile);
			int bytesRead = zipinputstream.read(buf, 0, 1024);
			while (bytesRead > -1)
			{
				fileoutputstream.write(buf, 0, bytesRead);
				bytesRead = zipinputstream.read(buf, 0, 1024);
			}
		}
		finally
		{
			if (fileoutputstream != null)
			{
				fileoutputstream.close();
			}
		}
	}

	/**
	 *  It will zip the current folder & create the zip file with
	 *  the given  file name in second parameter.
	 * @param srcFolder folder to be zipped.
	 * @param destZipFile name of the zip file.
	 * @return the file object pointing to the Zip file.
	 * @throws DynamicExtensionsSystemException Exception.
	 * @throws IOException Exception.
	 */
	public static File zipFolder(String srcFolder, String destZipFile)
			throws DynamicExtensionsSystemException, IOException
	{
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;
		File destZip = null;
		try
		{

			destZip = new File(destZipFile);
			if (destZip.exists() && !destZip.delete())
			{
				LOGGER.error("can not delete " + destZipFile);
			}
			fileWriter = new FileOutputStream(destZip);
			zip = new ZipOutputStream(fileWriter);

			addFolderToZip("", srcFolder, zip);
			zip.flush();
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException("Error occured while reading the folder", e);
		}
		finally
		{
			if (zip != null)
			{
				zip.close();
			}
		}
		return destZip;
	}

	/**
	 * It will add the current file in the zip at the given path.
	 * @param path path where to add the file in zip.
	 * @param srcFile file to add in zip.
	 * @param zip zip output stream pointing to the zipped file.
	 * @throws IOException exception.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	private static void addFileToZip(String path, String srcFile, ZipOutputStream zip)
			throws IOException, DynamicExtensionsSystemException

	{

		File folder = new File(srcFile);
		if (folder.isDirectory())
		{
			addFolderToZip(path, srcFile, zip);
		}
		else
		{
			byte[] buf = new byte[1024];

			FileInputStream inputStream = null;
			try
			{
				inputStream = new FileInputStream(srcFile);
				zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
				int len = inputStream.read(buf);
				while (len > 0)
				{
					zip.write(buf, 0, len);
					len = inputStream.read(buf);
				}
			}
			finally
			{
				if (inputStream != null)
				{
					inputStream.close();
				}
			}
		}
	}

	/**
	 * This method will add the given srcFolder to the given zip
	 * output stream zip at the given path in the created zip file.
	 * @param path path where the folder should be added in the zip file.
	 * @param srcFolder folder to add in the zip.
	 * @param zip zip out put stream pointing to zip file.
	 * @throws DynamicExtensionsSystemException Exception.
	 * @throws IOException Exception.
	 */
	private static void addFolderToZip(String path, String srcFolder, ZipOutputStream zip)
			throws DynamicExtensionsSystemException, IOException

	{
		File folder = new File(srcFolder);
		if (!folder.exists() || !folder.isDirectory())
		{
			throw new DynamicExtensionsSystemException(srcFolder
					+ "does not exist. Please specify correct path");
		}
		for (String fileName : folder.list())
		{
			if ("".equals(path))
			{
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			}
			else
			{
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
			}
		}
	}
}
