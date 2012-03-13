/**
 *
 */

package edu.common.dynamicextensions.action.core;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import edu.common.dynamicextensions.client.PermissibleValuesClient;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.permissiblevalue.version.ReadPermissibleValuesProcessor;
import edu.common.dynamicextensions.util.DirOperationsUtility;
import edu.common.dynamicextensions.util.DownloadUtility;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * @author gaurav_mehta
 *
 */
public class DownloadPVVersionAction extends HttpServlet
{


	/**
	 * 
	 */
	private static final long serialVersionUID = 7606212938202748026L;

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(DownloadPVVersionAction.class);


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		final String pvDir = CommonServiceLocator.getInstance().getAppHome() + File.separator + "pvDir"
				+ EntityCache.getInstance().getNextIdForCategoryFileGeneration();
		try
		{
			DownloadUtility.downloadZipFile(request, pvDir, "DownloadPVVersionDir.zip");
			LOGGER.info("Artifacts Downloaded");
			final Map<String, Object> pvFileNameVsExcep = new HashMap<String, Object>();
			final List<String> listOfFiles = getPVFileNames(request, pvDir);
			for (String file : listOfFiles)
			{
				downloadPVs(file, pvDir,request.getParameter("downLoadDirectory"),pvFileNameVsExcep);
			}
			sendResponse(response, pvFileNameVsExcep);
		}
		catch (Exception e)
		{
			LOGGER.info("Exception occured while importing Permissible Values", e);
			sendResponse(response, e);
		}
		finally
		{
			DirOperationsUtility.getInstance().deleteDirectory(new File(pvDir));
		}
	}

	/**
	 * Method will import permissible values mentioned in the file.
	 * @param file pv file name
	 * @param pvDir base directory from which the file path is mentioned.
	 * @param pvFileNameVsExcep map of file name vs exception occurred.
	 * @param isCategoryImport is PV import for category or entity
	 */
	protected void downloadPVs(final String file,final String pvDir,final String downloadToDir ,final Map<String, Object> pvFileNameVsExcep)
	{
		try
		{
			final List<String> successMessage = importPvVersion(file,pvDir,downloadToDir);
			LOGGER.info("Permissible values from file " + file + " uploaded successfully.");
			pvFileNameVsExcep.put(file, successMessage);
		}
		catch (Exception ex)
		{
			LOGGER.error("Error occured while importing permissible values", ex);
			pvFileNameVsExcep.put(file, ex);
		}
	}

	private List<String> importPvVersion(final String file,final String pvDir,final String downloadToDir) throws DynamicExtensionsSystemException, JAXBException, IOException
	{
			final ReadPermissibleValuesProcessor readPVs = new ReadPermissibleValuesProcessor();
			return readPVs.getPvVersionValues(file,pvDir,downloadToDir);
	}

	/**
	 * This method will send the given responseObject to the caller of this servlet.
	 * @param response HttpServletResponse through which the responseObject should be send.
	 * @param responseObject object to be send.
	 */
	public void sendResponse(final HttpServletResponse response,final Object responseObject)
	{
		try
		{
			final ObjectOutputStream outputToClient = new ObjectOutputStream(response.getOutputStream());
			outputToClient.writeObject(responseObject);
			outputToClient.flush();
			outputToClient.close();
		}
		catch (IOException e)
		{
			LOGGER.info("Exception occured while sending response back to java application");
		}
	}

	/**
	 * This method will retrieve the category names parameter provided in request
	 * and will return the List of names.
	 * @param request HttpServletRequest from which to retrieve the category names.
	 * @param tempDirName
	 * @return List of category names to be created.
	 * @throws DynamicExtensionsSystemException exception
	 */
	private List<String> getPVFileNames(HttpServletRequest request, String tempDirName)
			throws DynamicExtensionsSystemException
	{
		List<String> fileNameList;
		final String fileName = request.getParameter(PermissibleValuesClient.pvFileName);

		if (fileName == null || "".equals(fileName.trim()))
		{
			fileNameList = getPVFileListInDirectory(new File(tempDirName),"");
		}
		else
		{
			final File pvFile = new File(tempDirName + File.separator + fileName);
			if (!pvFile.exists())
			{
				throw new DynamicExtensionsSystemException("PV file " + fileName
						+ " does not exists");
			}
			fileNameList = new ArrayList<String>();
			fileNameList.add(fileName);
		}
		return fileNameList;
	}

	/**
	 * It will search in the given base directory & will find out all the Permissible Value
	 * files present in the given directory.
	 * @param baseDirectory directory in which to search for the files.
	 * @param relativePath path used to reach the category files.
	 * @return list of the file names relative to the given base directory.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	public static List<String> getPVFileListInDirectory(File baseDirectory, String relativePath)
			throws DynamicExtensionsSystemException
	{
		final List<String> fileNameList = new ArrayList<String>();
		try
		{
			for (File file : baseDirectory.listFiles())
			{
				if (file.isDirectory())
				{
					final String childDirPath = relativePath + file.getName() + "/";
					fileNameList.addAll(getPVFileListInDirectory(file, childDirPath));
				}
				else
				{
					if (file.getAbsolutePath().endsWith(".xml")
							|| file.getAbsolutePath().endsWith(".xml"))
					{
						fileNameList.add(relativePath + file.getName());

					}

				}
			}
		}
		catch (Exception e)
		{
			throw new DynamicExtensionsSystemException(
					"Exception occured while reading the Permissible Value file", e);
		}
		return fileNameList;
	}
}
