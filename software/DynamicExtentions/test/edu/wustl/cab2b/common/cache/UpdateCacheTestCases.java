/**
 *
 */

package edu.wustl.cab2b.common.cache;

import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.EntityGroupManagerUtil;
import edu.wustl.cab2b.server.cache.EntityCache;

/**
 * @author suhas_khot
 *
 */
public class UpdateCacheTestCases extends DynamicExtensionsBaseTestCase
{

//	public void testLockAndReleaseEntity()
//	{
//		String entityGroupName = "test";
//		try
//		{
//			//Step 1. Fetch a Entity Group.
//			EntityGroupInterface entityGroupInterface = EntityManager.getInstance()
//					.getEntityGroupByName(entityGroupName);
//			if (entityGroupInterface != null)
//			{
//				EntityCache cache = EntityCache.getInstance();
//				//Step 2. Refresh EntityCache for the Entity Group.
//				cache.refreshCache(entityGroupInterface);
//
//				//Step 3. Lock all Entities for the Entity Group.
//				cache.lockAllEntities(entityGroupInterface.getEntityCollection());
//
//				//step 4. Lock all containers for the Entity Group
//				cache.lockAllContainer(EntityGroupManagerUtil
//						.getAssociatedFormId(entityGroupInterface));
//				if (cache.entitiesInUse.isEmpty())
//				{
//					fail();
//				}
//
//				//Step 5. Release all Entities for the Entity Group.
//				cache.releaseAllEntities(entityGroupInterface.getEntityCollection());
//
//				//Step 5. Release all Containers for the Entity Group.
//				cache.releaseAllContainer(EntityGroupManagerUtil
//						.getAssociatedFormId(entityGroupInterface));
//				if (!cache.entitiesInUse.isEmpty())
//				{
//					fail();
//				}
//			}
//		}
//		catch (DynamicExtensionsSystemException e)
//		{
//			e.printStackTrace();
//			fail();
//		}
//		catch (DynamicExtensionsApplicationException e)
//		{
//			e.printStackTrace();
//			fail();
//		}
//	}

}
