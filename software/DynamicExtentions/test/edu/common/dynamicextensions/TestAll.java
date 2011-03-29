
package edu.common.dynamicextensions;

/**
 *
 */

import junit.framework.Test;
import junit.framework.TestSuite;
import edu.common.dynamicextensions.category.TestXMLToCSVConverter;
import edu.common.dynamicextensions.categoryManager.TestCalculatedAttribute;
import edu.common.dynamicextensions.categoryManager.TestCategoryCreation;
import edu.common.dynamicextensions.categoryManager.TestCategoryManager;
import edu.common.dynamicextensions.categoryManager.TestDEIntegration;
import edu.common.dynamicextensions.categoryManager.TestPvVersion;
import edu.common.dynamicextensions.client.TestValidateAgainstSchema;
import edu.common.dynamicextensions.entitymanager.TestEntityGroupManager;
import edu.common.dynamicextensions.entitymanager.TestEntityManager;
import edu.common.dynamicextensions.entitymanager.TestEntityManagerForAssociations;
import edu.common.dynamicextensions.entitymanager.TestEntityManagerForInheritance;
import edu.common.dynamicextensions.entitymanager.TestEntityManagerHQL;
import edu.common.dynamicextensions.entitymanager.TestEntityManagerWithPrimaryKey;
import edu.common.dynamicextensions.entitymanager.TestEntityMangerForXMIImportExport;
import edu.common.dynamicextensions.entitymanager.TestImportPermissibleValues;
import edu.common.dynamicextensions.entitymanager.TestReadPermissibleValueProcessor;
import edu.common.dynamicextensions.host.csd.util.TestCSDUtility;
import edu.common.dynamicextensions.messages.TestXmlMessageProcessor;
import edu.common.dynamicextensions.ui.util.TestControlConfigurationsFactory;
import edu.common.dynamicextensions.ui.util.TestControlsUtility;
import edu.common.dynamicextensions.util.CategoryUtilTest;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.DynamicExtensionsUtilityTest;
import edu.common.dynamicextensions.util.TestBOTemplateGenerator;
import edu.common.dynamicextensions.util.TestMetadataQueryUtil;
import edu.common.dynamicextensions.util.xml.TestObjectFactory;
import edu.common.dynamicextensions.util.xml.TestXMLToJavaObjectConverter;
import edu.common.dynamicextensions.xmi.TestAnnotationUtil;
import edu.common.dynamicextensions.xmi.TestUpdateCSRToEntityPath;
import edu.wustl.cab2b.client.metadatasearch.MetadataSearchTest;
import edu.wustl.cab2b.common.cache.CompareUtilTest;
import edu.wustl.cab2b.common.util.IdGeneratorTest;
import edu.wustl.cab2b.common.util.TreeNodeTest;
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
import edu.wustl.cab2b.server.util.TestServerProperties;
import edu.wustl.common.querysuite.metadata.TestUtility;
import edu.wustl.common.querysuite.querableobject.TestAbstractQueryableObject;
import edu.wustl.common.querysuite.querableobject.TestQueryableCategory;
import edu.wustl.common.querysuite.querableobject.TestQueryableCategoryAttribute;
import edu.wustl.common.querysuite.querableobject.TestQueryableEntity;
import edu.wustl.common.querysuite.querableobject.TestQueryableEntityAttribute;
import edu.wustl.common.querysuite.querableobject.TestQueryableObjectUtility;
import edu.wustl.metadata.util.TestCacoreDEMetadataAppender;
import edu.wustl.metadata.util.TestDyExtnObjectCloner;
import edu.wustl.metadata.util.TestEntityGroupName;
import edu.wustl.metadata.util.TestPackageName;

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
		suite.addTestSuite(TestEntityMangerForXMIImportExport.class);
		suite.addTestSuite(TestImportPermissibleValues.class);
		suite.addTestSuite(TestCategoryCreation.class);
		suite.addTestSuite(TestCategoryManager.class);
		suite.addTestSuite(TestCalculatedAttribute.class);

		suite.addTestSuite(TestEntityManager.class);
		suite.addTestSuite(TestEntityManagerForAssociations.class);
		suite.addTestSuite(TestEntityManagerForInheritance.class);
		suite.addTestSuite(TestXMLToJavaObjectConverter.class);
		suite.addTestSuite(TestObjectFactory.class);
		suite.addTestSuite(TestPvVersion.class);
		suite.addTestSuite(TestCSDUtility.class);
		suite.addTestSuite(TestMetadataQueryUtil.class);
		suite.addTestSuite(MetadataSearchTest.class);
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
		suite.addTestSuite(TestControlsUtility.class);
		suite.addTestSuite(CategoryUtilTest.class);
		//suite.addTestSuite(TestCategoryUtil.class);
		suite.addTestSuite(TestQueryableObjectUtility.class);
		suite.addTestSuite(TestQueryableEntity.class);
		suite.addTestSuite(TestQueryableCategory.class);
		suite.addTestSuite(TestQueryableCategoryAttribute.class);
		suite.addTestSuite(TestQueryableEntityAttribute.class);
		suite.addTestSuite(TestAbstractQueryableObject.class);
		suite.addTestSuite(TestXmlMessageProcessor.class);
		suite.addTestSuite(TestValidateAgainstSchema.class);
		suite.addTestSuite(TestDEIntegration.class);
		suite.addTestSuite(TestXMLToCSVConverter.class);
		suite.addTestSuite(TestEntityGroupManager.class);
		suite.addTestSuite(DynamicExtensionsUtilityTest.class);
		suite.addTestSuite(TestEntityManagerHQL.class);
		suite.addTestSuite(TestControlConfigurationsFactory.class);
		suite.addTestSuite(TestPackageName.class);
		suite.addTestSuite(TestEntityGroupName.class);
		suite.addTestSuite(TestUpdateCSRToEntityPath.class);
		suite.addTestSuite(TestAnnotationUtil.class);
		suite.addTestSuite(TestCacoreDEMetadataAppender.class);
		suite.addTestSuite(TestUtility.class);
		suite.addTestSuite(TestDyExtnObjectCloner.class);
		suite.addTestSuite(TestServerProperties.class);
		suite.addTestSuite(TestReadPermissibleValueProcessor.class);
		suite.addTestSuite(TestBOTemplateGenerator.class);
		return suite;
	}
}