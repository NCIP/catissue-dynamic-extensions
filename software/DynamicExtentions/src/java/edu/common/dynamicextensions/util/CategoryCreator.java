
package edu.common.dynamicextensions.util;

import java.io.File;

import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.CategoryConstants;
import edu.common.dynamicextensions.util.parser.CategoryGenerator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 *
 * @author mandar_shidhore
 *
 */
public class CategoryCreator
{
	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	private static final Logger LOGGER = Logger.getCommonLogger(CategoryCreator.class);

	public static void main(String[] args)
	{
		try
		{
			if (args.length == 0)
			{
				throw new Exception("ERROR ---Please Specify the path for .csv file");
			}
			String filePath = args[0];
			boolean isPersistMetadataOnly = false;
			if(args.length >1 && CategoryConstants.TRUE.equalsIgnoreCase(args[1]))
			{
				isPersistMetadataOnly =true;

			}
			createCategory(filePath ,isPersistMetadataOnly);

		}
		catch (Exception ex)
		{
			LOGGER.info("Exception: " + ex.getMessage());
			throw new RuntimeException(ex.getMessage(),ex);
		}
	}

	/**
	 * @param args
	 * @return
	 */
	public static void createCategory(String filePath,boolean isPersistMetadataOnly)
	{
		try
		{
			LOGGER.info("The .csv file path is:" + filePath);
			validateFileExist(filePath);

			CategoryGenerator categoryGenerator = new CategoryGenerator(filePath);
			CategoryHelperInterface categoryHelper = new CategoryHelper();

			boolean isEdited = true;

			for (CategoryInterface category : categoryGenerator.getCategoryList())
			{
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
			}
			LOGGER.info("Form definition file " + filePath + " executed successfully.");

		}
		catch (Exception ex)
		{
			LOGGER.error("Error occured while creating category",ex);
			throw new RuntimeException(ex.getCause().getLocalizedMessage(),ex);

		}
	}

	/**
	 * @param csvFilePath
	 * @throws DynamicExtensionsSystemException
	 */
	private static void validateFileExist(String csvFilePath)
			throws DynamicExtensionsSystemException
	{
		File objFile = new File(csvFilePath);
		if (!objFile.exists())
		{
			throw new DynamicExtensionsSystemException(
					"Please verify that form definition file exist at path: " + csvFilePath);
		}
	}

}