package edu.common.dynamicextensions.domaininterface.userinterface;

import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.wustl.cab2b.server.cache.EntityCache;


public class TestContainer extends DynamicExtensionsBaseTestCase
{

	/**
	 * This test case will try to generate the html for each of the category "Skip logic for Automation 3" in Edit mode.
	 * Test case is failed if the exception is occured in generating the html for any of the Category.
	 *
	 */
	public void testGenerateHtmlForSkipLogicForAutomation3InEditMode()
	{
		try
		{

			EntityCache cache = EntityCache.getInstance();
			CategoryInterface category = CategoryManager.getInstance().getCategoryByName("Skip logic for Automation 3");
			cache.cacheSkipLogic();
			ContainerInterface  container = (ContainerInterface) category.getRootCategoryElement().getContainerCollection().iterator().next();
			container.generateContainerHTML(category.getName(), WebUIManagerConstants.EDIT_MODE);
		}
		catch (Exception e)
		{
			System.out.println("Html generation failed for Category Skip logic for Automation");
			e.printStackTrace();
			fail();
		}

	}

	/**
	 * This test case will try to generate the html for each of the category "Skip logic for Automation 3" in view mode.
	 * Test case is failed if the exception is occured in generating the html for any of the Category.
	 *
	 */
	public void testGenerateHtmlForSkipLogicForAutomation3InViewMode()
	{
		try
		{
			EntityCache cache = EntityCache.getInstance();
			CategoryInterface category = CategoryManager.getInstance().getCategoryByName("Skip logic for Automation 3");
			cache.cacheSkipLogic();
			ContainerInterface  container = (ContainerInterface) category.getRootCategoryElement().getContainerCollection().iterator().next();
			container.generateContainerHTML(category.getName(), WebUIManagerConstants.VIEW_MODE);
		}
		catch (Exception e)
		{
			System.out.println("Html generation failed for Category Skip logic for Automation 3");
			e.printStackTrace();
			fail();
		}

	}


}
