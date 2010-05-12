/**
 *
 */
package edu.common.dynamicextensions.util;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;


/**
 * @author gaurav_mehta
 *
 */
public class CategoryUtilTest extends DynamicExtensionsBaseTestCase
{

	public void testCategoryUtilForAddingCategoriestoCache()
	{
		try
		{
			String fileName = "src/resources/csv/cacheableCategories.csv";
			 CategoryUtil.main(new String[]{fileName});
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	public void testCategoryUtilForRemovingCategoriestoCache()
	{
		try
		{
			String fileName = "src/resources/csv/cacheableCategories.csv";
			 CategoryUtil.main(new String[]{fileName,"False"});
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	public void testCategoryUtilforWrongCategoryName()
	{
		try
		{
			String fileName = "src/resources/csv/wrongCacheableCategories.csv";
			CategoryUtil.main(new String[]{fileName});
		}
		catch (DynamicExtensionsSystemException e)
		{
			fail();
			e.printStackTrace();
		}
	}

	public void testCategoryUtilforWrongFileName()
	{
		try
		{
			String fileName = "src/resources/wrongCacheableCategories.csv";
			CategoryUtil.main(new String[]{fileName});
			fail();
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
		}
	}

}
