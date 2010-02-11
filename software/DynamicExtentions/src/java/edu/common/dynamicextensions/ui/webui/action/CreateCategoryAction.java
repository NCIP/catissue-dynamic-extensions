
package edu.common.dynamicextensions.ui.webui.action;

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

import edu.common.dynamicextensions.category.CategoryCreatorConstants;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.CategoryGenerationUtil;
import edu.common.dynamicextensions.util.CategoryHelper;
import edu.common.dynamicextensions.util.CategoryHelperInterface;
import edu.common.dynamicextensions.util.DirOperationsUtility;
import edu.common.dynamicextensions.util.DownloadUtility;
import edu.common.dynamicextensions.util.parser.CategoryGenerator;
import edu.wustl.cab2b.server.cache.EntityCache;
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

	/* (non-Javadoc)
	 * @see org.apache.struts.actions.DispatchAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	{
		LOGGER.info("In create category action");
		Map<String, Exception> catNameVsExcep = new HashMap<String, Exception>();
		String tempDirName = CAT_DIR_PREFIX
				+ EntityCache.getInstance().getNextIdForCategoryFileGeneration();
		try
		{
			DownloadUtility.downloadZipFile(request, tempDirName, "categoryZip.zip");
			List<String> fileNamesList = getCategoryFileNames(request, tempDirName);

			for (String name : fileNamesList)
			{
				if (ProcessorConstants.TRUE.equalsIgnoreCase(request
						.getParameter(CategoryCreatorConstants.METADATA_ONLY)))
				{
					createCategory(name, tempDirName, true, catNameVsExcep);
				}
				else
				{
					createCategory(name, tempDirName, false, catNameVsExcep);
				}
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

	/**
	 * This method will create the category using the file specified in filePath which is
	 * present in the baseDirectory argument.
	 * @param filePath path of the file from which to create category.
	 * @param baseDirectory directory from which all the paths are mentioned.
	 * @param isPersistMetadataOnly if true saves only the metadata , does not create the dynamic tables for category
	 * @param catNameVsExcep this  is a report map in which the entry is made for each category
	 * the value will be null if category creation is successful else exception occured will be its value.
	 */
	public void createCategory(String filePath, String baseDirectory,
			boolean isPersistMetadataOnly, Map<String, Exception> catNameVsExcep)
	{
		CategoryInterface category = null;
		CategoryHelperInterface categoryHelper = new CategoryHelper();
		try
		{
			LOGGER.info("The .csv file path is:" + filePath);
			ServletContext servletContext = servlet.getServletContext();
			String config = servletContext.getRealPath("WEB-INF") + "/stinger.xml";
			Stinger stinger = new Stinger(new RuleSet(config, servletContext), servletContext);
			CategoryGenerator categoryGenerator = new CategoryGenerator(filePath, baseDirectory,
					stinger);

			boolean isEdited = true;

			category = categoryGenerator.generateCategory();

			if (category.getId() == null)
			{
				isEdited = false;
			}

			if (isPersistMetadataOnly)
			{
				categoryHelper.saveCategoryMetadata(category);
			}
			else
			{
				categoryHelper.saveCategory(category);
			}
			if (isEdited)
			{
				LOGGER.info("Edited category " + category.getName() + " successfully");
			}
			else
			{
				LOGGER.info("Saved category " + category.getName() + " successfully");
			}
			LOGGER.info("Form definition file " + filePath + " executed successfully.");
			catNameVsExcep.put(filePath, null);
		}
		catch (Exception ex)
		{
			LOGGER.error("Error occured while creating category", ex);
			catNameVsExcep.put(filePath, ex);
		}
		finally
		{
			categoryHelper.releaseLockOnCategory(category);
		}
	}
}
