
package edu.common.dynamicextensions;

/**
 *
 */

import junit.framework.Test;
import junit.framework.TestSuite;
import edu.common.dynamicextensions.category.TestXMLToCSVConverter;
import edu.common.dynamicextensions.categoryManager.TestCategoryManager;
import edu.common.dynamicextensions.entitymanager.TestEntityManager;
import edu.common.dynamicextensions.entitymanager.TestEntityManagerForAssociations;
import edu.common.dynamicextensions.entitymanager.TestEntityManagerForInheritance;
import edu.common.dynamicextensions.entitymanager.TestEntityManagerWithPrimaryKey;
import edu.common.dynamicextensions.entitymanager.TestEntityMangerForXMIImportExport;
import edu.common.dynamicextensions.entitymanager.TestImportPermissibleValues;
import edu.common.dynamicextensions.host.csd.util.TestCSDUtility;
import edu.common.dynamicextensions.util.CategoryUtilTest;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.TestMetadataQueryUtil;
import edu.wustl.cab2b.client.metadatasearch.MetadataSearchTest;
import edu.wustl.cab2b.common.cache.CompareUtilTest;
import edu.wustl.cab2b.common.util.IdGeneratorTest;
import edu.wustl.cab2b.common.util.TreeNodeTest;
import edu.wustl.cab2b.common.util.UtilityTest;
import edu.wustl.cab2b.server.path.CuratedPathOperationsTest;
import edu.wustl.cab2b.server.path.CuratedPathTest;
import edu.wustl.cab2b.server.path.InterModelConnectionTest;
import edu.wustl.cab2b.server.path.PathFinderTest;
import edu.wustl.cab2b.server.path.PathRecordTest;
import edu.wustl.cab2b.server.util.ConnectionUtilTest;
import edu.wustl.cab2b.server.util.DataFileLoaderTest;
import edu.wustl.cab2b.server.util.DynamicExtensionUtilityTest;
import edu.wustl.cab2b.server.util.InheritanceUtilTest;
import edu.wustl.cab2b.server.util.SQLQueryUtilTest;

/**
 * Test Suite for testing all DE  related classes.
 */
public class TestAll extends DynamicExtensionsBaseTestCase
{

	/**
	 * @param args arg
	 */
	public static void main(String[] args)
	{
		junit.swingui.TestRunner.run(TestAll.class);
	}

	/**
	 * @return test suite
	 */
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test suite for Query Interface Classes");
		suite.addTestSuite(TestEntityManagerWithPrimaryKey.class);
		suite.addTestSuite(TestEntityManager.class);
		suite.addTestSuite(TestEntityManagerForAssociations.class);
		suite.addTestSuite(TestEntityManagerForInheritance.class);
		suite.addTestSuite(TestEntityMangerForXMIImportExport.class);
		suite.addTestSuite(TestImportPermissibleValues.class);
		suite.addTestSuite(TestCategoryManager.class);
		suite.addTestSuite(TestXMLToCSVConverter.class);
		suite.addTestSuite(TestCSDUtility.class);
		suite.addTestSuite(TestMetadataQueryUtil.class);

		suite.addTestSuite(MetadataSearchTest.class);
		suite.addTestSuite(UtilityTest.class);
		suite.addTestSuite(CompareUtilTest.class);
		suite.addTestSuite(IdGeneratorTest.class);
		suite.addTestSuite(TreeNodeTest.class);
		suite.addTestSuite(edu.wustl.cab2b.common.util.UtilityTest.class);
		suite.addTestSuite(CuratedPathOperationsTest.class);
		suite.addTestSuite(CuratedPathTest.class);
		suite.addTestSuite(InterModelConnectionTest.class);
		suite.addTestSuite(PathFinderTest.class);
		suite.addTestSuite(PathRecordTest.class);
		suite.addTestSuite(ConnectionUtilTest.class);
		suite.addTestSuite(DataFileLoaderTest.class);
		suite.addTestSuite(DynamicExtensionUtilityTest.class);
		suite.addTestSuite(InheritanceUtilTest.class);
		suite.addTestSuite(SQLQueryUtilTest.class);
		suite.addTestSuite(CategoryUtilTest.class);
		return suite;
	}
}