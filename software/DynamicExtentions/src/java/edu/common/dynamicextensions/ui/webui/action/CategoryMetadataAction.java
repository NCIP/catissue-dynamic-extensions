
package edu.common.dynamicextensions.ui.webui.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.client.CategoryCreatorConstants;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * This action class is used by the create_category_meatadata ant target.
 * that ant target tries to connect to this Action class on server & pass the name of the
 * category files.
 * This action will create the Category metadata file for the given category name
 * & will pass that file in response to the class which has connected to this url.
 *
 * @author pavan_kalantri
 *
 */
public class CategoryMetadataAction extends BaseDynamicExtensionsAction
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	/**
	 * logger.
	 */
	private static final Logger LOGGER = Logger.getCommonLogger(CategoryMetadataAction.class);

	/**
	 * This method will initiate a process to create a csv file which contains the
	 * metatata information required for category bulk opearation.
	 * @param mapping mapping
	 * @param form action form
	 * @param request request object
	 * @param response response object
	 * @return ActionForward action forward null.
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	{
		LOGGER.info("In create category Metadata action");
		try
		{
			List<String> catNameList = getCategoryNameList(request);
			File metadataFile = new File(CommonServiceLocator.getInstance().getAppHome()
					+ File.separator + "metadata"
					+ EntityCache.getInstance().getNextIdForCategoryFileGeneration() + ".csv");
			createCategoryMetadataFile(catNameList, metadataFile);
			sendResponse(response, metadataFile);

			LOGGER.info("Create category metadata action completed successfully");
		}
		catch (Exception e)
		{
			LOGGER.error("Exception occured while creating category", e);
		}
		return null;
	}

	/**
	 * This method will write the metadata information of the category with name given in
	 * catNameList parameter to the given metadataFile.
	 * @param catNameList category name whose metadata needed.
	 * @param metadataFile file to which the metadata should be written.
	 * @throws IOException exception.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	private void createCategoryMetadataFile(List<String> catNameList, File metadataFile)
			throws IOException, DynamicExtensionsSystemException
	{
		BufferedWriter writter = null;
		try
		{
			writter = new BufferedWriter(new FileWriter(metadataFile));
			for (String catName : catNameList)
			{

				CategoryInterface category = CategoryManager.getInstance().getCategoryByName(
						catName);
				if (category == null)
				{
					writter.newLine();
					writter.write("==============================================================");
					writter.newLine();
					writter.write("Category " + catName + " not found.");
					writter.newLine();
					writter.write("==============================================================");
					writter.newLine();
					continue;
				}
				writeHeaderInfo(writter, catName, category);
				List<CategoryEntityInterface> catEntityList = new ArrayList<CategoryEntityInterface>();
				DynamicExtensionsUtility.populateAllCategoryEntityList(catEntityList, category
						.getRootCategoryElement());

				writeMetadataInfo(writter, catEntityList);

			}
			writter.flush();
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Exception encountered while writing metadata", e);
		}
		finally
		{
			if (writter != null)
			{
				writter.close();
			}
		}
	}

	/**
	 * This method will iterate over the list of catEntityList & write the information
	 * about all the attributes of that category entity to the writter.
	 * @param writter writter to which the info should be written.
	 * @param catEntityList category entity list.
	 * @throws IOException exception.
	 */
	private void writeMetadataInfo(BufferedWriter writter,
			List<CategoryEntityInterface> catEntityList) throws IOException
	{
		for (CategoryEntityInterface catEntity : catEntityList)
		{
			ContainerInterface container = (ContainerInterface) catEntity.getContainerCollection()
					.iterator().next();
			boolean isControlPresent = false;
			for (ControlInterface catAttributeControl : container.getAllControls())
			{
				boolean isValidControl = !(catAttributeControl instanceof AbstractContainmentControlInterface)
						&& catAttributeControl.getBaseAbstractAttribute() != null;
				if (isValidControl)
				{
					writter.newLine();
					writter.write(container.getCaption());
					writter.write(DEConstants.COMMA);
					writter.write(container.getId().toString());
					writter.write(DEConstants.COMMA);
					writter.write(catAttributeControl.getCaption());
					writter.write(DEConstants.COMMA);
					writter
							.write(catAttributeControl.getBaseAbstractAttribute().getId()
									.toString());
					isControlPresent = true;

				}
			}
			if (!isControlPresent)
			{
				writter.newLine();
				writter.write(container.getCaption());
				writter.write(DEConstants.COMMA);
				writter.write(container.getId().toString());
			}

		}
	}

	/**
	 * It will write the headers to the writter.
	 * @param writter writtter.
	 * @param categoryName name of the category
	 * @param category category.
	 * @throws IOException exception.
	 */
	private void writeHeaderInfo(BufferedWriter writter, String categoryName,
			CategoryInterface category) throws IOException
	{
		writter.newLine();
		writter.write("==============================================================");
		writter.newLine();
		writter.write("Category Name : ");
		writter.write(categoryName);
		ContainerInterface container = (ContainerInterface) category.getRootCategoryElement()
				.getContainerCollection().iterator().next();
		writter.newLine();
		writter.write("Root Container Name : ");
		writter.write(container.getCaption());
		writter.newLine();
		writter.write("Root Container Identifier : ");
		writter.write(container.getId().toString());
		writter.newLine();
		writter.write("==============================================================");
		writter.newLine();
		writter.write("Container Name");
		writter.write(DEConstants.COMMA);
		writter.write("Container Identifier");
		writter.write(DEConstants.COMMA);
		writter.write("Control Name");
		writter.write(DEConstants.COMMA);
		writter.write("Attribute Identifier");

	}

	/**
	 * It will retireve the name of the category from the request & will return that
	 * if name not found will throw the exception.
	 * @param request request from which to find category name.
	 * @return name of the category
	 * @throws DynamicExtensionsSystemException if request does not contain the categoryName parameter.
	 */
	private List<String> getCategoryNameList(HttpServletRequest request)
			throws DynamicExtensionsSystemException
	{
		String catName = request.getParameter("categoryName");
		if (catName == null || "".equals(catName))
		{
			throw new DynamicExtensionsSystemException("Please specify category Name.");
		}
		List<String> nameList = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(catName,
				CategoryCreatorConstants.CAT_FILE_NAME_SEPARATOR);
		while (tokenizer.hasMoreTokens())
		{
			String token = tokenizer.nextToken();
			if (!"".equals(token))
			{
				nameList.add(token);
			}
		}
		return nameList;
	}

	/**
	 * This method will send the given responseObject file to the caller of this servlet.
	 * @param response HttpServletResponse through which the responseObject should be send.
	 * @param responseObject file to be send.
	 * @throws IOException exception
	 */
	private void sendResponse(HttpServletResponse response, File responseObject) throws IOException
	{
		BufferedInputStream inputStream = null;
		BufferedOutputStream outputStream = null;
		try
		{
			inputStream = new BufferedInputStream(new FileInputStream(responseObject));
			outputStream = new BufferedOutputStream(response.getOutputStream());
			byte[] buffer = new byte[1024];
			int len = inputStream.read(buffer);
			while (len > 0)
			{
				outputStream.write(buffer, 0, len);
				len = inputStream.read(buffer);
			}
			outputStream.flush();
			outputStream.close();
		}
		catch (IOException e)
		{
			LOGGER.info("Exception occured while sending response to java application", e);
		}
		finally
		{

			if (inputStream != null)
			{
				inputStream.close();
			}
			if (outputStream != null)
			{
				outputStream.close();
			}
			responseObject.delete();
		}

	}
}
