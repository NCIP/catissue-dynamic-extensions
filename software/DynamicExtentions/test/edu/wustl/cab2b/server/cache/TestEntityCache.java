package edu.wustl.cab2b.server.cache;

import java.util.Set;

import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.entitymanager.EntityGroupManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.EntityGroupManagerUtil;


public class TestEntityCache extends DynamicExtensionsBaseTestCase
{

	private EntityCache cache;
	public TestEntityCache()
	{
		cache = EntityCache.getInstance();
	}

	public void testLockAllContainer() throws DynamicExtensionsSystemException
	{
		EntityGroupInterface entityGroupInterface = EntityGroupManager.getInstance().getEntityGroupByName(
				TEST_ENTITYGROUP_NAME);
		Set<Long> associatedFormId =
			EntityGroupManagerUtil.getAssociatedFormId(entityGroupInterface);
		cache.lockAllContainer(associatedFormId);
		Long lockedForm = associatedFormId.iterator().next();
		try
		{
			cache.getContainerById(lockedForm);
		}
		catch (DynamicExtensionsCacheException e)
		{
			assertTrue(true);
			return;
		}
		fail();
	}

	public void testReleaseAllContainer() throws DynamicExtensionsSystemException
	{
		EntityGroupInterface entityGroupInterface = EntityGroupManager.getInstance().getEntityGroupByName(
				TEST_ENTITYGROUP_NAME);
		Set<Long> associatedFormId =
			EntityGroupManagerUtil.getAssociatedFormId(entityGroupInterface);
		cache.lockAllContainer(associatedFormId);

		cache.releaseAllContainer(associatedFormId);
		Long lockedForm = associatedFormId.iterator().next();
		try
		{
			cache.getContainerById(lockedForm);
		}
		catch (DynamicExtensionsCacheException e)
		{
			fail();
			return;
		}
		assertTrue(true);
	}

	public void testLockAllEntities() throws DynamicExtensionsSystemException
	{
		EntityGroupInterface entityGroupInterface = EntityGroupManager.getInstance().getEntityGroupByName(
				TEST_ENTITYGROUP_NAME);
		cache.lockAllEntities(entityGroupInterface.getEntityCollection());

		Long lockeEntity= entityGroupInterface.getEntityCollection().iterator().next().getId();
		try
		{
			cache.getEntityById(lockeEntity);
		}
		catch (DynamicExtensionsCacheException e)
		{
			assertTrue(true);
			return;
		}
		fail();

	}
	public void testReleaseAllEntities() throws DynamicExtensionsSystemException
	{
		EntityGroupInterface entityGroupInterface = EntityGroupManager.getInstance().getEntityGroupByName(
				TEST_ENTITYGROUP_NAME);
		cache.lockAllEntities(entityGroupInterface.getEntityCollection());
		cache.releaseAllEntities(entityGroupInterface.getEntityCollection());

		Long lockeEntity= entityGroupInterface.getEntityCollection().iterator().next().getId();
		try
		{
			cache.getEntityById(lockeEntity);
		}
		catch (DynamicExtensionsCacheException e)
		{
			fail();
			return;
		}
		assertTrue(true);
	}

	public void testGetContainerByIdNegative() throws DynamicExtensionsSystemException
	{
		try
		{
			cache.getContainerById(-1L);
		}
		catch (DynamicExtensionsCacheException e)
		{
			System.out.println(e.getMessage());
			assertTrue(true);
			return;
		}
		fail();
	}

	public void testGetContainerById() throws DynamicExtensionsSystemException
	{
		EntityGroupInterface entityGroupInterface = EntityGroupManager.getInstance().getEntityGroupByName(
				TEST_ENTITYGROUP_NAME);
		Set<Long> associatedFormId =
			EntityGroupManagerUtil.getAssociatedFormId(entityGroupInterface);
		try
		{
			cache.getContainerById(associatedFormId.iterator().next());
		}
		catch (DynamicExtensionsCacheException e)
		{
			e.printStackTrace();
			return;
		}
		assertTrue(true);

	}
}
