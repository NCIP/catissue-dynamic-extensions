package edu.common.dynamicextensions.util;

import java.util.Collection;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domaininterface.CalculatedAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

public class CalculatedAttributeCreator
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
			Collection<CategoryAttributeInterface> calculatedCategoryAttrColl = categoryManager
					.getAllCalculatedCategoryAttributes();
			DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
			for (CategoryAttributeInterface sourceCategoryAttribute : calculatedCategoryAttrColl)
			{
				for (CategoryAttributeInterface targetCategoryAttribute : sourceCategoryAttribute
						.getCalculatedAttributeCollection())
				{
					CalculatedAttributeInterface calculatedAttributeInterface = domainObjectFactory
							.createCalculatedAttribute();
					calculatedAttributeInterface
							.setCalculatedAttribute(sourceCategoryAttribute);
					calculatedAttributeInterface
							.setSourceForCalculatedAttribute(targetCategoryAttribute);
					sourceCategoryAttribute.addCalculatedCategoryAttribute(calculatedAttributeInterface);
				}
			}
			categoryManager.updateCategoryAttributes(calculatedCategoryAttrColl);
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
