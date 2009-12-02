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

import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.CategoryHelper;
import edu.common.dynamicextensions.util.CategoryHelperInterface;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.parser.CategoryCSVConstants;
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

	private final Map<String, Exception> catNameVsException = new HashMap<String, Exception>();
	/* (non-Javadoc)
	 * @see org.apache.struts.actions.DispatchAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	{
		LOGGER.info("In create category action");
		String tempDirName =CATEGORY_DIR_PREFIX+EntityCache.getInstance().getNextIdForCategoryFileGeneration();
		try
		{
			downloadZipFile(request,tempDirName);
			List<String>fileNamesList = getCategoryFileNames(request);

			for(String name : fileNamesList)
			{
				if(ProcessorConstants.TRUE.equalsIgnoreCase(request.getParameter(CategoryCSVConstants.METADATA_ONLY)))
				{
					createCategory(name,tempDirName, true);
				}
				else
				{
					createCategory(name,tempDirName,false);
				}
			}
			sendResponse(response,catNameVsException);

			LOGGER.info("Create category action Completed Successfully");
		}
		catch (Exception e)
		{
			LOGGER.info("Exception Occured While creating category",e);
			sendResponse(response,e);
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
	 * @return List of category names to be created.
	 * @throws DynamicExtensionsSystemException exception
	 */
	private List<String> getCategoryFileNames(HttpServletRequest request) throws DynamicExtensionsSystemException
	{
		List<String> fileNameList = new ArrayList<String>();// TODO Auto-generated method stub
		String fileNameString = request.getParameter(CategoryCSVConstants.CATEGORY_NAMES_FILE);
		if(fileNameString == null || "".equals(fileNameString.trim()))
		{
			throw new DynamicExtensionsSystemException("Please provide names of the Category Files To be created");
		}
		StringTokenizer tokenizer = new StringTokenizer(fileNameString,"!=!");
		while(tokenizer.hasMoreTokens())
		{
			String token = tokenizer.nextToken();
			if(!"".equals(token))
			{
				fileNameList.add(token);
			}
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
				else if(!files[i].delete())
				{
					LOGGER.error("Can not delete File "+files[i]);
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
	private void downloadZipFile(HttpServletRequest request,String tempDirName) throws IOException, DynamicExtensionsSystemException
	{
		BufferedInputStream reader =null;
		BufferedOutputStream fileWriter= null;
		createNewTempDirectory(tempDirName);
		String fileName = tempDirName +"/categoryZip.zip";
		try
		{
			reader = new BufferedInputStream(request.getInputStream());
			File file = new File(fileName);
			if(file.exists() && !file.delete())
			{
				LOGGER.error("Can not delete File : "+file);
			}
			fileWriter =new BufferedOutputStream(new FileOutputStream(file));

			byte[] buffer = new byte[1024];
			int len =  reader.read(buffer);
			while( len  >=0)
			{
				fileWriter.write(buffer,0,len);
				len =  reader.read(buffer);
			}
			fileWriter.flush();

		}
	    catch (IOException e)
		{
			// TODO Auto-generated catch block
			throw new DynamicExtensionsSystemException("Exception occured while Downloading the Zip",e);

		}
	    finally
	    {
	    	if(fileWriter!=null)
			{
				fileWriter.close();
			}
	    	if(reader!=null)
			{
				reader.close();
			}
	    }
		DynamicExtensionsUtility.extractZipToDestination(fileName,tempDirName);
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
		if(tempDir.exists() && tempDir.delete())
		{
			LOGGER.error("Unable to delete Directory "+ tempDirName);
		}
		if(!tempDir.mkdirs())
		{
			throw new DynamicExtensionsSystemException("Unable to create tempDirectory");
		}
	}

	/**
	 * This method will send the given responseObject to the caller of this servlet.
	 * @param response HttpServletResponse through which the responseObject should be send.
	 * @param responseObject object to be send.
	 */
	public void sendResponse(HttpServletResponse response,Object responseObject)
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
	       LOGGER.info("Exception occured while sending Response to java Application");
	     }

	}


	/**
	 * This method will create the category using the file specified in filePath which is
	 * present in the baseDirectory argument.
	 * @param filePath path of the file from which to create category.
	 * @param baseDirectory directory from which all the paths are mentioned.
	 * @param isPersistMetadataOnly if true saves only the metadata , does not create the dynamic tables for category
	 */
	public void createCategory(String filePath, String baseDirectory , boolean isPersistMetadataOnly)
	{
		CategoryInterface category=null;
		CategoryHelperInterface categoryHelper = new CategoryHelper();
		try
		{
			LOGGER.info("The .csv file path is:" + filePath);
			CategoryGenerator categoryGenerator = new CategoryGenerator(filePath,baseDirectory);

			boolean isEdited = true;

			category = categoryGenerator.generateCategory();

				if (category.getId() == null)
				{
					isEdited = false;
				}

				if(isPersistMetadataOnly)
				{
					categoryHelper.saveCategoryMetadata(category);
				}
				else
				{
					categoryHelper.saveCategory(category);
				}
				if (isEdited)
				{
					Logger.out.info("Edited category " + category.getName() + " successfully");
				}
				else
				{
					Logger.out.info("Saved category " + category.getName() + " successfully");
				}
		LOGGER.info("Form definition file " + filePath + " executed successfully.");
		catNameVsException.put(filePath,null);
		}
		catch (Exception ex)
		{
			LOGGER.error("Error occured while creating category",ex);
			catNameVsException.put(filePath,ex);
		}
		finally
		{
			categoryHelper.releaseLockOnCategory(category);
		}
	}
}
