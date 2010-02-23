/**
 *
 */

package edu.wustl.cab2b.common.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.cab2b.server.util.DynamicExtensionUtility;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.metadata.util.JbossPropertyLoader;

/**
 * @author gaurav_mehta
 * This class initiates the process of refreshing the Entity cache. It first
 * fetches all Entity Group Id's and depending on whether any category is
 * created or not fetches either Categories or fetches Entity Group
 */
public class RefreshCacheInitializer implements ThreadPoolFactory
{

	private static final Logger LOGGER = edu.wustl.common.util.logger.Logger
			.getLogger(RefreshCacheInitializer.class);

	private static final Integer NUMBER_OF_CONNECTIONS = 5;

	public void run()
	{
		try
		{
			List<Long> entityGroupIDs = getAllEntityGroupIDs();
			startCacheRefresh(entityGroupIDs);
		}
		catch (DAOException e)
		{
			LOGGER.error("Error while fetching categories from database. Error: " + e.getMessage());
			throw new RuntimeException("Exception encountered while creating EntityCache!!", e);
		}
		catch (DynamicExtensionsSystemException e)
		{
			LOGGER.error("Error while Creating Cache. Error: " + e.getMessage());
			throw new RuntimeException("Exception encountered while creating Cache!!", e);
		}
		catch (InterruptedException e)
		{
			LOGGER.error("Exception encountered while creating Cache!! Error: " + e.getMessage());
			throw new RuntimeException("Exception encountered while creating Cache!!", e);
		}
	}

	/**
	 * This method returns the list of Entity Group Id's present in database
	 * It sorts them in descending order (latest first)
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 */
	private List<Long> getAllEntityGroupIDs() throws DynamicExtensionsSystemException, DAOException
	{
		final HibernateDAO hibDAO = DynamicExtensionsUtility.getHibernateDAO();

		List<Long> entityGroupIDs = DynamicExtensionUtility.getAllEntityGroupIds(hibDAO);
		LOGGER.info("Number of Entity Groups in database :" + entityGroupIDs.size());

		Collections.sort(entityGroupIDs);
		Collections.reverse(entityGroupIDs);
		DynamicExtensionsUtility.closeDAO(hibDAO);

		return entityGroupIDs;
	}

	/**
	 * This method starts the process of fetching categories and Entity Groups
	 * from database. Depending on number of database connection available it
	 * spawns those many thread to fetch either Categories / Entity Group
	 * @param entityGroupIDs
	 * @throws InterruptedException
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 */
	private void startCacheRefresh(List<Long> entityGroupIDs) throws InterruptedException,
			DynamicExtensionsSystemException, DAOException
	{
		int numberOfThreads = getDatabaseConnectionCount();
		LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		CacheThreadPool cacheInitilizer = new CacheThreadPool(numberOfThreads, numberOfThreads, 1,
				TimeUnit.SECONDS, queue);

		for (Long entGrpId : entityGroupIDs)
		{
			while ((cacheInitilizer.getActiveCount() >= numberOfThreads))
			{
				Thread.sleep(3000L);
			}

			HibernateDAO hibernateDao = DynamicExtensionsUtility.getHibernateDAO();
			List<Long> catIdList = getAllCatIdsForEntityGroupId(hibernateDao, entGrpId);
			if (catIdList.isEmpty())
			{
				fetchEntityGroup(cacheInitilizer, entGrpId, hibernateDao);
			}
			else
			{
				fetchCategory(cacheInitilizer, hibernateDao, catIdList);
			}
		}
		while (!cacheInitilizer.allProcessCompleted())
		{
			Thread.sleep(30000L);
		}
		LOGGER.info("Initializing cache Done!!!");
		cacheInitilizer.shutdown();
	}

	/**
	 * This method reads the number of database connection from
	 * performance.properties and returns the number of connections which
	 * should be used in creating cache.
	 * @return number of database connection for cache
	 */
	private int getDatabaseConnectionCount()
	{
		Properties properties = JbossPropertyLoader.getPropertiesFromFile("performance.properties");
		int numberOfConnections;
		try
		{
			numberOfConnections = Integer.parseInt(properties
					.getProperty("number.of.database.connections"));
			numberOfConnections = numberOfConnections / 2;
		}
		catch (NumberFormatException nfe)
		{
			LOGGER.info("************* Performance.properties is not present or property not defined properly ***************");
			LOGGER.info("************* Initializing Number of Database Connection with default value 5 ***************");
			numberOfConnections = NUMBER_OF_CONNECTIONS;
		}
		return numberOfConnections;
	}

	/**
	 * This method fetches the list of all categories Id for given Entity Group Id.
	 * It also sorts them in descending order (latest first)
	 * @param dao
	 * @param entityGroupId
	 * @return List<Long> of category Id's
	 * @throws DAOException
	 * @throws DynamicExtensionsSystemException
	 */
	private List<Long> getAllCatIdsForEntityGroupId(HibernateDAO dao, Long entityGroupId)
			throws DAOException, DynamicExtensionsSystemException
	{
		List<Long> catIdList = new ArrayList<Long>();
		catIdList.addAll(DynamicExtensionUtility.getAllCategoryIdsForEntityGroupId(dao,
				entityGroupId));
		Collections.sort(catIdList);
		Collections.reverse(catIdList);

		return catIdList;
	}

	/**
	 * This method initialize Thread which fetches all the Categories of given
	 * category Ids. All these categories belong to a single Entity Group
	 * @param cacheInitilizer
	 * @param hibernateDao
	 * @param catIdList
	 */
	private void fetchCategory(CacheThreadPool cacheInitilizer, HibernateDAO hibernateDao,
			List<Long> catIdList)
	{
		Thread categoryFetcher = new Thread(new CategoryFetcher(hibernateDao, catIdList));
		cacheInitilizer.execute(categoryFetcher);
	}

	/**
	 * This method initialize the Thread which fetches the Entity Group of
	 * given Entity Group Id
	 * @param cacheInitilizer
	 * @param entGrpId
	 * @param hibernateDao
	 */
	private void fetchEntityGroup(CacheThreadPool cacheInitilizer, Long entGrpId,
			HibernateDAO hibernateDao)
	{
		Thread entityGroupFetcher = new Thread(new EntityGroupFetcher(hibernateDao, entGrpId));
		cacheInitilizer.execute(entityGroupFetcher);
	}
}
