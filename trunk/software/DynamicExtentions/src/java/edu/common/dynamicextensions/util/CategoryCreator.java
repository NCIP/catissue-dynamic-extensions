
package edu.common.dynamicextensions.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.util.global.CategoryConstants;
import edu.common.dynamicextensions.util.parser.CategoryGenerator;
import edu.wustl.common.util.logger.Logger;

/**
 * 
 * @author mandar_shidhore
 *
 */
public class CategoryCreator
{

	public static void main(String[] args)
	{
		createCategory(args);
	}

	/**
	 * @param args
	 * @return
	 */
	public static List<HashMap> createCategory(String[] args)
	{
		try
		{
			if (args.length == 0)
			{
				throw new Exception("PLEASE SPECIFY THE PATH FOR .csv FILE");
			}

			String filePath = args[0];
			Logger.out.info("The .csv file path is:" + filePath);
			boolean isPersistMetadataOnly=false;
			if(args.length >1 && CategoryConstants.TRUE.equalsIgnoreCase(args[1]))
			{
				isPersistMetadataOnly =true;
				
			}

			CategoryGenerator categoryGenerator = new CategoryGenerator(filePath);
			CategoryHelperInterface categoryHelper = new CategoryHelper();

			boolean isEdited = true;
			List<HashMap> categories = new ArrayList<HashMap>();
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

				HashMap<CategoryInterface, Boolean> objCategoryMap = new HashMap<CategoryInterface, Boolean>();
				objCategoryMap.put(category, Boolean.valueOf(isEdited));
				categories.add(objCategoryMap);
			}

			return categories;
		}
		catch (Exception ex)
		{
			Logger.out.info("Exception:",ex);
			throw new RuntimeException(ex.getCause().getLocalizedMessage(), ex);
		}
	}

}