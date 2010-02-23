
package edu.wustl.cab2b.common.cache;

import org.apache.log4j.Logger;

import edu.common.dynamicextensions.domain.EntityGroup;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;

/**
 *
 * @author gaurav_mehta
 * This class fetches the Entity Group of given Entity Group ID and using
 * given Hibernate DAO. It then updates Entity Cache with the fetched
 * Entity Group.
 */
public class EntityGroupFetcher implements ThreadPoolFactory
{

	private static final Logger LOGGER = edu.wustl.common.util.logger.Logger
			.getLogger(EntityGroupFetcher.class);

	private final transient HibernateDAO hibernateDao;

	private final transient Long entityGroupId;

	public EntityGroupFetcher(HibernateDAO hibernateDAO, Long entityGroupID)
	{
		hibernateDao = hibernateDAO;
		entityGroupId = entityGroupID;
	}

	public void run()
	{
		Long startTime = System.currentTimeMillis();
		try
		{
			EntityGroupInterface entityGroup = (EntityGroupInterface) hibernateDao.retrieveById(
					EntityGroup.class.getName(), entityGroupId);
			AbstractEntityCache abstractEntityCache = AbstractEntityCache.getCache();
			abstractEntityCache.createEntityGroupCache(entityGroup);
			Long endTime = System.currentTimeMillis();
			Long totalTime = (endTime - startTime) / 1000;
			LOGGER.info("Entity Group " + entityGroup.getName() + " fetched in " + totalTime
					+ " seconds...");
		}
		catch (DAOException e)
		{
			LOGGER.error("Error while fetching Entity Groups from database. Error: "
					+ e.getMessage());
			throw new RuntimeException("Exception encountered while creating EntityCache!!", e);
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
