
package edu.common.dynamicextensions.ui.webui.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.category.CategoryCreatorConstants;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.CategoryHelper;
import edu.common.dynamicextensions.util.CategoryHelperInterface;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.parser.CategoryFileParser;
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

	private static final String CATEGORY_DIR_PREFIX = "CategoryDirectory";

	/* (non-Javadoc)
	 * @see org.apache.struts.actions.DispatchAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	{
		LOGGER.info("In create category action");
		Map<String, Exception> catNameVsException = new HashMap<String, Exception>();
		String tempDirName = CATEGORY_DIR_PREFIX
				+ EntityCache.getInstance().getNextIdForCategoryFileGeneration();
		try
		{
			downloadZipFile(request, tempDirName);
			List<String> fileNamesList = getCategoryFileNames(request, tempDirName);

			for (String name : fileNamesList)
			{
				if (ProcessorConstants.TRUE.equalsIgnoreCase(request
						.getParameter(CategoryCreatorConstants.METADATA_ONLY)))
				{
					createCategory(name, tempDirName, true, catNameVsException);
				}
				else
				{
					createCategory(name, tempDirName, false, catNameVsException);
				}
			}
			sendResponse(response, catNameVsException);

			LOGGER.info("Create category action completed successfully");
		}
		catch (Exception e)
		{
			LOGGER.info("Exception occured while creating category", e);
			sendResponse(response, e);
		}
		finally
		{
			deleteDirectory(new File(tempDirName));
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
			fileNameList = getCategoryFileListInDirectory(new File(tempDirName),"");
			//throw new DynamicExtensionsSystemException("Please provide names of the Category Files To be created");
		}
		else
		{
			fileNameList = new ArrayList<String>();
			StringTokenizer tokenizer = new StringTokenizer(fileNameString, CategoryCreatorConstants.CAT_FILE_NAME_SEPARATOR);
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
	 * It will search in the given base directory & will find out all the category
	 * files present in the given directory.
	 * @param baseDirectory directory in which to search for the files.
	 * @param relativePath path used to reach the category files.
	 * @return list of the file names relative to the given base directory.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	private List<String> getCategoryFileListInDirectory(File baseDirectory,String relativePath)
			throws DynamicExtensionsSystemException
	{
		List<String> fileNameList = new ArrayList<String>();
		try
		{
			for (File file : baseDirectory.listFiles())
			{
				if (file.isDirectory())
				{
					String childDirPath = relativePath + file.getName() + "/";
					fileNameList.addAll(getCategoryFileListInDirectory(file,childDirPath));
				}
				else
				{
					CategoryFileParser categoryFileParser = DomainObjectFactory.getInstance().createCategoryFileParser(file.getAbsolutePath(),"");
					 if (categoryFileParser!=null && categoryFileParser.isCategoryFile())
					 {
						 fileNameList.add(relativePath+file.getName());
						 categoryFileParser.closeResources();
					 }
				}
			}
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Exception occured while reading the category file names ", e);

		}
		return fileNameList;
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

	/**
	 * This method will download the Zip file usin the outputStream in request in the provided tempDirName.
	 * If tempDirName dir does not exists then it will create it first & then download the zip in that folder.
	 * @param request from which to download the Zip file.
	 * @param tempDirName directory name in which to download it.
	 * @throws IOException Exception.
	 * @throws DynamicExtensionsSystemException Exception
	 */
	private void downloadZipFile(HttpServletRequest request, String tempDirName)
			throws IOException, DynamicExtensionsSystemException
	{
		BufferedInputStream reader = null;
		BufferedOutputStream fileWriter = null;
		createNewTempDirectory(tempDirName);
		String fileName = tempDirName + "/categoryZip.zip";
		try
		{
			reader = new BufferedInputStream(request.getInputStream());
			File file = new File(fileName);
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
		DynamicExtensionsUtility.extractZipToDestination(fileName, tempDirName);
	}

	/**
	 * This method will delete the directory with the name tempDirName if present & then will
	 * create the new one for use.
	 * @param tempDirName name of the directory to be created.
	 * @throws DynamicExtensionsSystemException Exception.
	 */
	private void createNewTempDirectory(String tempDirName) throws DynamicExtensionsSystemException
	{
		File tempDir = new File(tempDirName);
		if (tempDir.exists() && !tempDir.delete())
		{
			LOGGER.error("Unable to delete directory " + tempDirName);
		}
		if (!tempDir.mkdirs())
		{
			throw new DynamicExtensionsSystemException("Unable to create tempDirectory");
		}
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
	 * @param catNameVsException this  is a report map in which the entry is made for each category
	 * the value will be null if category creation is successful else exception occured will be its value.
	 */
	public void createCategory(String filePath, String baseDirectory,
			boolean isPersistMetadataOnly, Map<String, Exception> catNameVsException)
	{
		CategoryInterface category = null;
		CategoryHelperInterface categoryHelper = new CategoryHelper();
		try
		{
			LOGGER.info("The .csv file path is:" + filePath);
			CategoryGenerator categoryGenerator = new CategoryGenerator(filePath, baseDirectory);

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
			catNameVsException.put(filePath, null);
		}
		catch (Exception ex)
		{
			LOGGER.error("Error occured while creating category", ex);
			catNameVsException.put(filePath, ex);
		}
		finally
		{
			categoryHelper.releaseLockOnCategory(category);
		}
	}
}
