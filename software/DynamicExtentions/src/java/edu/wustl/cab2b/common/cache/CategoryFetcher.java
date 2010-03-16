
package edu.wustl.cab2b.common.cache;

import java.util.List;

import org.apache.log4j.Logger;

import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;

/**
 *
 * @author gaurav_mehta
 * This class fetches all the categories whose Id's are given.
 * All these categories belong to same Entity Group.
 * Once categories are fetches it updates the Category Cache and also the Entity
 * Group is updated in Entity Cache
 */
public class CategoryFetcher implements ThreadPoolFactory
{

	private static final Logger LOGGER = edu.wustl.common.util.logger.Logger
			.getLogger(CategoryFetcher.class);

	private final transient HibernateDAO hibernateDao;

	private final transient List<Long> categoryIDList;

	public CategoryFetcher(HibernateDAO hibernateDAO, List<Long> catIDs)
	{
		hibernateDao = hibernateDAO;
		categoryIDList = catIDs;
	}

	public void run()
	{
		try
		{
			Long startTime = System.currentTimeMillis();
			EntityGroupInterface entityGroup = null;
			AbstractEntityCache abstractEntityCache = AbstractEntityCache.getCache();
			for (Long categoryID : categoryIDList)
			{
				CategoryInterface cat = (CategoryInterface) hibernateDao.retrieveById(
						edu.common.dynamicextensions.domain.Category.class.getName(), categoryID);

				CategoryEntityInterface rootCategory = cat.getRootCategoryElement();
				entityGroup = rootCategory.getEntity().getEntityGroup();
				//abstractEntityCache.createCategoryCache(cat);
			}
			abstractEntityCache.createEntityGroupCache(entityGroup);
			Long endTime = System.currentTimeMillis();
			Long totalTime = (endTime - startTime) / 1000;
			LOGGER.info(categoryIDList.size() + " Categories for Entity Group "
					+ entityGroup.getName() + " loaded in " + totalTime + " seconds...");
		}
		catch (DAOException e)
		{
			LOGGER.error("Error while fetching categories from database. Error: " + e.getMessage());
			throw new RuntimeException("Exception encountered while creating category cache!!", e);
		}
		finally
		{
			try
			{
				DynamicExtensionsUtility.closeDAO(hibernateDao);
			}
			catch (DynamicExtensionsSystemException e)
			{
				LOGGER.error("Error while closing hibernate DAO. Error: " + e.getMessage());
				throw new RuntimeException("Exception encountered while creating EntityCache!!", e);
			}
		}
	}
}