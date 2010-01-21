/**
 *
 */

package edu.common.dynamicextensions.ui.webui.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.CategoryGenerationUtil;
import edu.common.dynamicextensions.util.DirOperationsUtility;
import edu.common.dynamicextensions.util.DownloadUtility;
import edu.common.dynamicextensions.util.parser.DynamicallyImportPermissibleValues;
import edu.common.dynamicextensions.util.parser.ImportPermissibleValues;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * @author gaurav_mehta
 *
 */
public class ImportPVAction extends BaseDynamicExtensionsAction
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	private static final Logger LOGGER = Logger.getCommonLogger(ImportPVAction.class);

	private static String pvDir = "";

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	{
		String folder = request.getParameter(DynamicallyImportPermissibleValues.startFolder);
		try
		{
			DownloadUtility.downloadZipFile(request, pvDir, "pv.zip");
			LOGGER.info("Artifacts Downloaded");

			List<String> listOfFiles = getPVFileNames(request, pvDir + folder +"/");

			for (String file : listOfFiles)
			{
				importPVs(pvDir + file);
			}
			sendResponse(response, "PV Uploaded Successfully");
			LOGGER.info("Permissible Values uploaded successfully");
		}
		catch (Exception e)
		{
			LOGGER.info("Exception occured while importing Permissible Values", e);
			sendResponse(response, e);
		}
		finally
		{
			DirOperationsUtility.getInstance().deleteDirectory(
					new File(folder));
		}
		return null;
	}

	/**
	 * @param relativeFileName
	 * @param file
	 * @throws DynamicExtensionsSystemException
	 * @throws FileNotFoundException
	 * @throws DynamicExtensionsApplicationException
	 * @throws ParseException
	 */
	private void importPVs(String file)
			throws DynamicExtensionsSystemException, FileNotFoundException,
			DynamicExtensionsApplicationException, ParseException
	{
		ImportPermissibleValues importPVs = new ImportPermissibleValues(file);
		importPVs.importValues();
	}

	/**
	 * This method will send the given responseObject to the caller of this servlet.
	 * @param response HttpServletResponse through which the responseObject should be send.
	 * @param responseObject object to be send.
	 */
	public void sendResponse(HttpServletResponse response, Object responseObject)
	{
		try
		{
			ObjectOutputStream outputToClient = new ObjectOutputStream(response.getOutputStream());
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
		String fileName = request.getParameter(DynamicallyImportPermissibleValues.pvFileName);
		String folder = request.getParameter(DynamicallyImportPermissibleValues.startFolder);
		if (fileName == null || "".equals(fileName.trim()))
		{
			fileNameList = CategoryGenerationUtil.getPVFileListInDirectory(new File(tempDirName),
					folder +"/");
			//throw new DynamicExtensionsSystemException("Please provide names of the Category Files To be created");
		}
		else
		{
			fileNameList = new ArrayList<String>();
			int startPoint = fileName.indexOf(folder);
			fileNameList.add(fileName.substring(startPoint, fileName.length()));
		}
		return fileNameList;
	}
}
