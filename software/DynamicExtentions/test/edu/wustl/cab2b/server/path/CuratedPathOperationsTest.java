
package edu.wustl.cab2b.server.path;

import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.cab2b.server.util.TestConnectionUtil;

/**
 * @author chandrakant_talele
 */
public class CuratedPathOperationsTest extends DynamicExtensionsBaseTestCase
{

	@Override
	protected void setUp() throws DynamicExtensionsCacheException
	{
		EntityCache.getInstance();
		PathFinder.getInstance(TestConnectionUtil.getConnection());
	}

	public void testGetAllCuratedPath()
	{
		CuratedPathOperations opr = new CuratedPathOperations();
		try
		{
			opr.getAllCuratedPath();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			fail("Exception in getAllCuratedPath()");
		}
	}

	public void testGetPathById()
	{
		try
		{
			new CuratedPathOperations().getPathById(1L);
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			fail("Exception in getPathById()");
		}
	}
}
