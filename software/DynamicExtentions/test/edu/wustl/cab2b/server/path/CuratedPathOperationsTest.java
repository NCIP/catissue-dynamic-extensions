package edu.wustl.cab2b.server.path;

import java.util.List;

import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.cab2b.server.util.TestConnectionUtil;
import edu.wustl.common.querysuite.metadata.path.ICuratedPath;
import edu.wustl.common.querysuite.metadata.path.IPath;

/**
 * @author chandrakant_talele
 */
public class CuratedPathOperationsTest extends DynamicExtensionsBaseTestCase {
    @Override
    protected void setUp() {
        EntityCache.getInstance();
        PathFinder.getInstance(TestConnectionUtil.getConnection());
    }

    public void testGetAllCuratedPath() {
        CuratedPathOperations opr = new CuratedPathOperations();
        try {
            List<ICuratedPath> list = opr.getAllCuratedPath();
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Exception in getAllCuratedPath()");
        }
    }

//    public void testIsDuplicate() {
//        CuratedPathOperations opr = new CuratedPathOperations();
//
//        List<ICuratedPath> list = opr.getAllCuratedPath();
//        if (!list.isEmpty()) {
//            boolean res = opr.isDuplicate(list.get(0));
//            assertTrue(res);
//
//        }
//    }

    public void testGetPathById() {
        CuratedPathOperations opr = new CuratedPathOperations();
        try {
            IPath path = opr.getPathById(1L);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Exception in getPathById()");
        }
    }
}
