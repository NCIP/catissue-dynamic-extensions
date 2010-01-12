package edu.common.dynamicextensions.util;

import java.util.Collection;

import edu.common.dynamicextensions.domaininterface.CalculatedAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

public class UpgradeCalculatedAttributes
{
	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			CategoryManagerInterface categoryManager = CategoryManager
					.getInstance();
			Collection<CategoryAttributeInterface> calculatedCatAttributeColl = categoryManager
					.getAllCalculatedCategoryAttributes();
			for (CategoryAttributeInterface calculatedAttribute : calculatedCatAttributeColl)
			{
				for (CalculatedAttributeInterface calculatedAttributeInterface : calculatedAttribute
						.getCalculatedCategoryAttributeCollection())
				{
					calculatedAttributeInterface
							.getSourceForCalculatedAttribute()
							.setIsSourceForCalculatedAttribute(Boolean.TRUE);
				}
			}
			categoryManager.updateCategoryAttributes(calculatedCatAttributeColl);
		}
		catch (DynamicExtensionsSystemException e)
		{
			Logger.out.info("Exception: ", e);
			throw new RuntimeException(e);
		}
		catch (DynamicExtensionsApplicationException e)
		{
			Logger.out.info("Exception: ", e);
			throw new RuntimeException(e);
		}
		Logger.out.info(" ");
		Logger.out.info("---------------------------------------");
		Logger.out.info("Upgraded calculated attributes successfully!!");
		Logger.out.info("---------------------------------------");
		Logger.out.info(" ");
	}

}
