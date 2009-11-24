
package edu.common.dynamicextensions;

/**
 *
 */

import junit.framework.Test;
import junit.framework.TestSuite;
import edu.common.dynamicextensions.categoryManager.TestCategoryManager;
import edu.common.dynamicextensions.entitymanager.TestEntityManager;
import edu.common.dynamicextensions.entitymanager.TestEntityManagerForAssociations;
import edu.common.dynamicextensions.entitymanager.TestEntityManagerForInheritance;
import edu.common.dynamicextensions.entitymanager.TestEntityManagerWithPrimaryKey;
import edu.common.dynamicextensions.entitymanager.TestEntityMangerForXMIImportExport;
import edu.common.dynamicextensions.entitymanager.TestImportPermissibleValues;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.XMIImporter;

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
		uploadStaticMetadata();
		suite.addTestSuite(TestEntityManagerWithPrimaryKey.class);
		suite.addTestSuite(TestEntityManager.class);
		suite.addTestSuite(TestEntityManagerForAssociations.class);
		suite.addTestSuite(TestEntityManagerForInheritance.class);
		suite.addTestSuite(TestEntityMangerForXMIImportExport.class);
		suite.addTestSuite(TestImportPermissibleValues.class);
		suite.addTestSuite(TestCategoryManager.class);
		return suite;
	}
	public static void uploadStaticMetadata()
	{
		System.out.println("uploadig static metadata");
		String[] args = {XMI_FILE_PATH+"TestStaticModel.xmi",CSV_FILE_PATH + "staticModelContainers.csv","staticModel","none","false"};
		XMIImporter.main(args);
	}
}
