/**
 *
 */

package edu.common.dynamicextensions.action.core;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.owasp.stinger.Stinger;
import org.owasp.stinger.rules.RuleSet;

import edu.common.dynamicextensions.client.PermissibleValuesClient;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.permissiblevalue.PermissibleValuesProcessor;
import edu.common.dynamicextensions.permissiblevalue.version.CategoryPermissibleValuesProcessor;
import edu.common.dynamicextensions.util.CategoryGenerationUtil;
import edu.common.dynamicextensions.util.DirOperationsUtility;
import edu.common.dynamicextensions.util.DownloadUtility;
import edu.common.dynamicextensions.util.parser.ImportPermissibleValues;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * @author gaurav_mehta
 *
 */
public class ImportPVAction extends BaseDynamicExtensionsAction
{

	private static final String IMPORT_PV_METHOD_NAME = "importPvVersionValues";

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(ImportPVAction.class);

	/** The Constant XML. */
	private static final String XML = "xml";

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	{
		String pvDir = CommonServiceLocator.getInstance().getAppHome() + File.separator + "pvDir"
				+ EntityCache.getInstance().getNextIdForCategoryFileGeneration();
		try
		{
			DownloadUtility.downloadZipFile(request, pvDir, "pv.zip");
			LOGGER.info("Artifacts Downloaded");
			Map<String, Object> pvFileNameVsExcep = new HashMap<String, Object>();
			List<String> listOfFiles = getPVFileNames(request, pvDir);
			boolean isCategoryImport = false;
			if (request.getParameter(PermissibleValuesClient.categoryPVImport) != null
					&& "true".equalsIgnoreCase(request
							.getParameter(PermissibleValuesClient.categoryPVImport)))
			{
				LOGGER.info("PV import for category attributes versioning");
				isCategoryImport = true;
			}

			for (String file : listOfFiles)
			{
				importPVs(file, pvDir, pvFileNameVsExcep, isCategoryImport);
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
		return null;
	}

	/**
	 * Method will import permissible values mentioned in the file.
	 * @param file pv file name
	 * @param pvDir base directory from which the file path is mentioned.
	 * @param pvFileNameVsExcep map of file name vs exception occurred.
	 * @param isCategoryImport is PV import for category or entity
	 */
	protected void importPVs(String file, String pvDir, Map<String, Object> pvFileNameVsExcep,
			boolean isCategoryImport)
	{
		try
		{
			ServletContext servletContext = servlet.getServletContext();
			String config = servletContext.getRealPath("WEB-INF") + "/stinger.xml";
			Stinger stinger = new Stinger(new RuleSet(config, servletContext), servletContext);
			List<String> successMessage = null;
			if (isCategoryImport)
			{
				successMessage = importPvVersion(file, pvDir, stinger);
			}
			else
			{
				String fileExtension = checkForFileExtension(file);

				if (XML.equalsIgnoreCase(fileExtension))
				{
					PermissibleValuesProcessor permissibleValueXML = new PermissibleValuesProcessor(
							stinger);
					permissibleValueXML.importPermissibleValues(file, pvDir);
				}
				else
				{
					ImportPermissibleValues importPVs = new ImportPermissibleValues(file, pvDir,
							stinger);
					importPVs.importValues();
				}
			}

			LOGGER.info("Permissible values from file " + file + " uploaded successfully.");
			pvFileNameVsExcep.put(file, successMessage);
		}
		catch (Exception ex)
		{
			LOGGER.error("Error occured while importing permissible values", ex);
			pvFileNameVsExcep.put(file, ex);
		}
	}

	private List<String> importPvVersion(String file, String pvDir, Stinger stinger)
			throws DynamicExtensionsSystemException
	{
		if (ApplicationProperties.getValue("import.pv.version.implementation") != null)
		{
			return callHostImplementation(file, pvDir,stinger);
		}
		else
		{
			CategoryPermissibleValuesProcessor importPVs = new CategoryPermissibleValuesProcessor(
					stinger);
			return importPVs.importPvVersionValues(file, pvDir);
		}

	}

	@SuppressWarnings("unchecked")
	private List<String> callHostImplementation(String file, String pvDir,Stinger stinger)
			throws DynamicExtensionsSystemException
	{
		try
		{
			String hostClassName = ApplicationProperties
					.getValue("import.pv.version.implementation");
			Class hostClass = Class.forName(hostClassName);
			Object classInstance = hostClass.getConstructor(Stinger.class).newInstance(stinger);

			Method callableMethod = hostClass.getMethod(IMPORT_PV_METHOD_NAME, String.class,
					String.class);
			return (List<String>) callableMethod.invoke(classInstance, file, pvDir);

		}
		catch (InvocationTargetException e)
		{
			LOGGER.error(e.getTargetException().getLocalizedMessage(), e);
			throw new DynamicExtensionsSystemException(
					e.getTargetException().getLocalizedMessage(), e);
		}
		catch (Exception e)
		{
			LOGGER
					.error("Error occured while calling Host application implementation for Permissible Values Versioning");
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}
	}

	/**
	 * Check for file extension.
	 *
	 * @param file the file
	 *
	 * @return the Extension of the given file
	 */
	private String checkForFileExtension(String file)
	{
		int indexOfExtension = file.lastIndexOf('.');
		return file.substring(indexOfExtension + 1, file.length());
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
		String fileName = request.getParameter(PermissibleValuesClient.pvFileName);

		if (fileName == null || "".equals(fileName.trim()))
		{
			fileNameList = CategoryGenerationUtil.getPVFileListInDirectory(new File(tempDirName),
					"");
			//throw new DynamicExtensionsSystemException("Please provide names of the Category Files To be created");
		}
		else
		{
			File pvFile = new File(tempDirName + File.separator + fileName);
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
}
