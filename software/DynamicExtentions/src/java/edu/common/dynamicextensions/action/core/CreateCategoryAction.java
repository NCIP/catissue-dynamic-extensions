
package edu.common.dynamicextensions.action.core;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.owasp.stinger.Stinger;
import org.owasp.stinger.rules.RuleSet;

import edu.common.dynamicextensions.category.creation.CategoryProcessor;
import edu.common.dynamicextensions.client.CategoryCreatorConstants;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.CategoryGenerationUtil;
import edu.common.dynamicextensions.util.DirOperationsUtility;
import edu.common.dynamicextensions.util.DownloadUtility;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * This action class is used by the create_category ant target.
 * that ant target tries to connect to this Action class on server & then uploads the
 * zip file which contains all the required stuff to create category.
 * This action will create the Category from these files & will also add that category
 * to cache.
 * @author pavan_kalantri
 *
 */
public class CreateCategoryAction extends BaseDynamicExtensionsAction
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	private static final Logger LOGGER = Logger.getCommonLogger(CreateCategoryAction.class);

	private static final String CAT_DIR_PREFIX = "CategoryDirectory";
	/**
	 * Prefix for template file path.
	 */
	private static final String MAPPING_PREFIX = "Mapping_";
	/**
	 * Prefix for XML file path.
	 */
	private static final String TEMPLATE_PREFIX = "Template_";

	/* (non-Javadoc)
	 * @see org.apache.struts.actions.DispatchAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	{
		LOGGER.info("In create category action");
		Map<String, Exception> catNameVsExcep = new HashMap<String, Exception>();
		String tempDirName = CommonServiceLocator.getInstance().getAppHome() + File.separator
				+ CAT_DIR_PREFIX + EntityCache.getInstance().getNextIdForCategoryFileGeneration();
		try
		{
			DownloadUtility.downloadZipFile(request, tempDirName, "categoryZip.zip");
			List<String> fileNamesList = getCategoryFileNames(request, tempDirName);

			ServletContext servletContext = this.servlet.getServletContext();
			String config = servletContext.getRealPath("WEB-INF") + "/stinger.xml";
			Stinger stinger = new Stinger(new RuleSet(config, servletContext), servletContext);

			StringBuffer templateFilePath = new StringBuffer("");
			StringBuffer mappingXML = new StringBuffer("");
			setTemplateFilePath(tempDirName, templateFilePath, mappingXML);
			CategoryProcessor processor = new CategoryProcessor(stinger);
			processor.setMappingXML(mappingXML.toString());
			processor.setTemplateFile(templateFilePath.toString());
			processor.setImportBulkTemplate(request.getParameter("importBulkTemplate"));
			for (String name : fileNamesList)
			{
				LOGGER.info("The .csv file path is:" + name);

				boolean isPersistMetadataOnly = ProcessorConstants.TRUE.equalsIgnoreCase(request
						.getParameter(CategoryCreatorConstants.METADATA_ONLY));
				processor.createCategory(name, tempDirName, isPersistMetadataOnly, catNameVsExcep);
			}
			sendResponse(response, catNameVsExcep);

			LOGGER.info("Create category action completed successfully");
		}
		catch (Exception e)
		{
			LOGGER.info("Exception occured while creating category", e);
			sendResponse(response, e);
		}
		finally
		{
			DirOperationsUtility.getInstance().deleteDirectory(new File(tempDirName));
		}
		return null;
	}

	/**
	 * This method sets the template file path.
	 * @param tempDirPath Temporary directory where both the files are stored.
	 * @param templateFilePath Hook entity file path.
	 * @param mappingXMLFile mapping XML file path.
	 */
	private void setTemplateFilePath(String tempDirPath, StringBuffer templateFilePath,
			StringBuffer mappingXMLFile)
	{
		File tempDir = new File(tempDirPath);
		for (File file : tempDir.listFiles())
		{
			if (file.isDirectory())
			{
				setTemplateFilePath(file.getPath(), templateFilePath, mappingXMLFile);
			}
			else
			{
				addFilePath(templateFilePath, mappingXMLFile, file);
			}
		}
	}

	/**
	 * This method adds the file path in buffer.
	 * @param templateFilePath add template file path.
	 * @param mappingXMLFile add mapping xml file path.
	 * @param file name of the file.
	 */
	private void addFilePath(StringBuffer templateFilePath, StringBuffer mappingXMLFile, File file)
	{
		if (file.getName().startsWith(MAPPING_PREFIX)
				&& file.getName().endsWith(DEConstants.XML_SUFFIX))
		{
			mappingXMLFile.append(file.getPath());
		}
		else if (file.getName().startsWith(TEMPLATE_PREFIX)
				&& file.getName().endsWith(DEConstants.XML_SUFFIX))
		{
			templateFilePath.append(file.getPath());
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
	private List<String> getCategoryFileNames(HttpServletRequest request, String tempDirName)
			throws DynamicExtensionsSystemException
	{
		List<String> fileNameList;
		String fileNameString = request.getParameter(CategoryCreatorConstants.CATEGORY_NAMES_FILE);
		if (fileNameString == null || "".equals(fileNameString.trim()))
		{
			fileNameList = CategoryGenerationUtil.getCategoryFileListInDirectory(new File(
					tempDirName), "");
			//throw new DynamicExtensionsSystemException("Please provide names of the Category Files To be created");
		}
		else
		{
			fileNameList = new ArrayList<String>();
			StringTokenizer tokenizer = new StringTokenizer(fileNameString,
					CategoryCreatorConstants.CAT_FILE_NAME_SEPARATOR);
			while (tokenizer.hasMoreTokens())
			{
				String token = tokenizer.nextToken();
				if (!"".equals(token))
				{
					fileNameList.add(token);
				}
			}
		}
		return fileNameList;
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
			LOGGER.info("Exception occured while sending response to java application");
		}

	}
}
