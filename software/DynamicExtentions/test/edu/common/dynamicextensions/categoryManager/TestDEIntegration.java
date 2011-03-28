
package edu.common.dynamicextensions.categoryManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.DEIntegration.DEIntegration;
import edu.common.dynamicextensions.domain.Category;
import edu.common.dynamicextensions.domain.userinterface.Container;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityGroupManager;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.hostApp.src.java.RecordEntry;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;


/**
 *
 * @author mandar_shidhore
 *
 */
public class TestDEIntegration extends DynamicExtensionsBaseTestCase
{

	private final String CATEGORY_FILE_DIR = "CPUML";
	private final String ENTITY_BY_NAME = "ClinicalAnnotations";
	private final String CATEGORY_NAME="Test Category_Chemotherapy";
	/**
	 * This test case will create all the categories present in the CPUML Folder.
	 * If one of the category creation is failed then this test case is also failed.
	 */
	public void testGetCategoriesContainerIdFromHookEntity()
	{
		try
		{
			EntityManagerInterface manager = EntityManager.getInstance();
			DEIntegration deIntegration = new DEIntegration();
			Collection collection=deIntegration.getCategoriesContainerIdFromHookEntity(manager.getEntityByName("edu.wustl.catissuecore.domain.RecordEntry").getId());
			assertTrue(collection.size()>0);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
	public void testGetDynamicEntityRecordIdFromHookEntityRecordIdWithJDBCDAO()
	{
		try
		{
			EntityManagerInterface manager = EntityManager.getInstance();
			DEIntegration deIntegration = new DEIntegration();
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			getCategoryIdentifier();
			Category category = (Category) categoryManager.getCategoryByName(CATEGORY_NAME);
			Collection<Container> containers=category.getRootCategoryElement().getEntity().getContainerCollection();
			Container container=containers.iterator().next();

			CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
			Map<BaseAbstractAttributeInterface, Object> dataValue = mapGenerator.createDataValueMapForCategory(rootCatEntity, 0);

			ContainerInterface 	containerInterface = (ContainerInterface)category.getRootCategoryElement().getContainerCollection().toArray()[0];
			Long recordId =DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);

			List<Long> recordEntryIdList=new ArrayList<Long>();

			Long recordIdentifier = categoryManager.getEntityRecordIdByRootCategoryEntityRecordId(
					recordId, category.getRootCategoryElement().getTableProperties().getName());

			EntityGroupInterface testModel = EntityGroupManager.getInstance().getEntityGroupByName(
					TEST_ENTITYGROUP_NAME);
			EntityInterface clinicalAnnotations = testModel.getEntityByName(ENTITY_BY_NAME);
			Long staticEntityRecId=associateHookEntity(clinicalAnnotations,recordIdentifier,manager);
			recordEntryIdList.add(staticEntityRecId);
			JDBCDAO jdbcDao = DynamicExtensionsUtility.getJDBCDAO();

			Collection longs=deIntegration.getDynamicEntityRecordIdFromHookEntityRecordId(staticEntityRecId.toString(),container.getId(),manager.getEntityByName("edu.wustl.catissuecore.domain.RecordEntry").getId(),jdbcDao);
			DynamicExtensionsUtility.closeDAO(jdbcDao);
			assertNotNull(longs);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
	public void testGetCategoryRecIdBasedOnHookEntityRecIdWithStaticIdList()
	{


		try
		{
			EntityManagerInterface manager = EntityManager.getInstance();
			DEIntegration deIntegration = new DEIntegration();
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			Category category = (Category) categoryManager.getCategoryByName(CATEGORY_NAME);
			Collection<Container> containers=category.getRootCategoryElement().getContainerCollection();
			Container container=containers.iterator().next();

			CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
			Map<BaseAbstractAttributeInterface, Object> dataValue = mapGenerator.createDataValueMapForCategory(rootCatEntity, 0);
			ContainerInterface 	containerInterface = (ContainerInterface)category.getRootCategoryElement().getContainerCollection().toArray()[0];
			Long recordId =DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);

			EntityGroupInterface testModel = EntityGroupManager.getInstance().getEntityGroupByName(
					TEST_ENTITYGROUP_NAME);
			EntityInterface clinicalAnnotations = testModel.getEntityByName(ENTITY_BY_NAME);
			List<Long> recordEntryIdList=new ArrayList<Long>();
			Long recordIdentifier = categoryManager.getEntityRecordIdByRootCategoryEntityRecordId(
					recordId, category.getRootCategoryElement().getTableProperties().getName());
			Long staticEntityRecId=associateHookEntity(clinicalAnnotations,recordIdentifier,manager);
			recordEntryIdList.add(staticEntityRecId);
			Collection longs=deIntegration.getCategoryRecIdBasedOnHookEntityRecId(container.getId(),recordEntryIdList,manager.getEntityByName("edu.wustl.catissuecore.domain.RecordEntry").getId());
			assertNotNull(longs);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
	public void testGetCategoryRecIdBasedOnHookEntityRecIdWithStaticId()
	{


		try
		{
			EntityManagerInterface manager = EntityManager.getInstance();
			DEIntegration deIntegration = new DEIntegration();
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			getCategoryIdentifier();
			Category category = (Category) categoryManager.getCategoryByName(CATEGORY_NAME);
			Collection<Container> containers=category.getRootCategoryElement().getContainerCollection();
			Container container=containers.iterator().next();

			CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
			Map<BaseAbstractAttributeInterface, Object> dataValue = mapGenerator.createDataValueMapForCategory(rootCatEntity, 0);

			ContainerInterface 	containerInterface = (ContainerInterface)category.getRootCategoryElement().getContainerCollection().toArray()[0];
			Long recordId =DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);

			Long recordIdentifier = categoryManager.getEntityRecordIdByRootCategoryEntityRecordId(
					recordId, category.getRootCategoryElement().getTableProperties().getName());

			EntityGroupInterface testModel = EntityGroupManager.getInstance().getEntityGroupByName(
					TEST_ENTITYGROUP_NAME);
			EntityInterface clinicalAnnotations = testModel.getEntityByName(ENTITY_BY_NAME);
			Long staticEntityRecId=associateHookEntity(clinicalAnnotations,recordIdentifier,manager);

			DynamicExtensionsUtility.getJDBCDAO();
			Collection longs=deIntegration.getCategoryRecIdBasedOnHookEntityRecId(container.getId(),staticEntityRecId,manager.getEntityByName("edu.wustl.catissuecore.domain.RecordEntry").getId());
			assertNotNull(longs);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
	public void testGetCategoryRecIdBasedOnHookEntityRecIdWithJDBCDAO()
	{


		try
		{
			EntityManagerInterface manager = EntityManager.getInstance();
			DEIntegration deIntegration = new DEIntegration();
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			getCategoryIdentifier();
			Category category = (Category) categoryManager.getCategoryByName(CATEGORY_NAME);
			Collection<Container> containers=category.getRootCategoryElement().getContainerCollection();
			Container container=containers.iterator().next();

			CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
			Map<BaseAbstractAttributeInterface, Object> dataValue = mapGenerator.createDataValueMapForCategory(rootCatEntity, 0);

			ContainerInterface 	containerInterface = (ContainerInterface)category.getRootCategoryElement().getContainerCollection().toArray()[0];
			Long recordId =DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);

			Long recordIdentifier = categoryManager.getEntityRecordIdByRootCategoryEntityRecordId(
					recordId, category.getRootCategoryElement().getTableProperties().getName());

			EntityGroupInterface testModel = EntityGroupManager.getInstance().getEntityGroupByName(
					TEST_ENTITYGROUP_NAME);
			EntityInterface clinicalAnnotations = testModel.getEntityByName(ENTITY_BY_NAME);
			Long staticEntityRecId=associateHookEntity(clinicalAnnotations,recordIdentifier,manager);

			JDBCDAO jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			Collection<Long> longs=deIntegration.getCategoryRecIdBasedOnHookEntityRecId(container.getId(),staticEntityRecId,manager.getEntityByName("edu.wustl.catissuecore.domain.RecordEntry").getId(),jdbcDao);
			assertNotNull(longs);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
	private Long associateHookEntity(EntityInterface clinicalAnnotations, Long recordId,EntityManagerInterface manager)
	{
		Long staticEntityRecId=null;
		try
		{
			//Create static entity record object
			RecordEntry recordEntry = new RecordEntry();

			HibernateDAO hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();

			//Insert record for static entity record object
			hibernateDAO.insert(recordEntry);

			if (hibernateDAO != null)
			{
				hibernateDAO.commit();
				DynamicExtensionsUtility.closeDAO(hibernateDAO);
			}
			//get Id for inserted record entry
			staticEntityRecId = recordEntry.getId();

			//Get static entity from metadata id of recordEntry
			EntityInterface staticEntity = manager.getEntityByName("edu.wustl.catissuecore.domain.RecordEntry");

			//search for association between static and dynamic entities for associating these two entities
			Collection<AssociationInterface> asntCollection = staticEntity
					.getAssociationCollection();
			AssociationInterface asntInterface = null;
			for (AssociationInterface association : asntCollection)
			{
				if (association.getTargetEntity().equals(clinicalAnnotations))
				{
					asntInterface = association;
					break;
				}
			}
			if (asntInterface != null)
			{
				//associate static entity record Id with dynamic entity record Id
				manager.associateEntityRecords(asntInterface, staticEntityRecId, recordId);
			}

		}
		catch (Exception e)
		{
			Logger.out.debug(e.getMessage());
			e.printStackTrace();
			fail("Exception occured");
		}
		return staticEntityRecId;
	}
	public void testGetDynamicEntityRecordIdFromHookEntityRecordId()
	{
		try
		{
			EntityManagerInterface manager = EntityManager.getInstance();
			DEIntegration deIntegration = new DEIntegration();
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			getCategoryIdentifier();
			Category category = (Category) categoryManager.getCategoryByName(CATEGORY_NAME);
			Collection<Container> containers=category.getRootCategoryElement().getEntity().getContainerCollection();
			Container container=containers.iterator().next();

			CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
			Map<BaseAbstractAttributeInterface, Object> dataValue = mapGenerator.createDataValueMapForCategory(rootCatEntity, 0);

			ContainerInterface 	containerInterface = (ContainerInterface)category.getRootCategoryElement().getContainerCollection().toArray()[0];
			Long recordId =DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);

			List<Long> recordEntryIdList=new ArrayList<Long>();

			Long recordIdentifier = categoryManager.getEntityRecordIdByRootCategoryEntityRecordId(
					recordId, category.getRootCategoryElement().getTableProperties().getName());

			EntityGroupInterface testModel = EntityGroupManager.getInstance().getEntityGroupByName(
					TEST_ENTITYGROUP_NAME);
			EntityInterface clinicalAnnotations = testModel.getEntityByName(ENTITY_BY_NAME);
			Long staticEntityRecId=associateHookEntity(clinicalAnnotations,recordIdentifier,manager);
			recordEntryIdList.add(staticEntityRecId);
			JDBCDAO jdbcDao = DynamicExtensionsUtility.getJDBCDAO();

			Collection longs=deIntegration.getDynamicEntityRecordIdFromHookEntityRecordId(recordEntryIdList,container.getId(),manager.getEntityByName("edu.wustl.catissuecore.domain.RecordEntry").getId(),jdbcDao);
			DynamicExtensionsUtility.closeDAO(jdbcDao);
			assertNotNull(longs);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
	public void testIsDataHooked()
	{
		try
		{
			EntityManagerInterface manager = EntityManager.getInstance();
			DEIntegration deIntegration = new DEIntegration();
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			Category category = (Category) categoryManager.getCategoryByName(CATEGORY_NAME);
			Collection<Container> containers=category.getRootCategoryElement().getContainerCollection();
			Container container=containers.iterator().next();

			CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
			Map<BaseAbstractAttributeInterface, Object> dataValue = mapGenerator.createDataValueMapForCategory(rootCatEntity, 0);

			ContainerInterface 	containerInterface = (ContainerInterface)category.getRootCategoryElement().getContainerCollection().toArray()[0];
			Long recordId =DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);

			Long recordIdentifier = categoryManager.getEntityRecordIdByRootCategoryEntityRecordId(
					recordId, category.getRootCategoryElement().getTableProperties().getName());

			EntityGroupInterface testModel = EntityGroupManager.getInstance().getEntityGroupByName(
					TEST_ENTITYGROUP_NAME);
			EntityInterface clinicalAnnotations = testModel.getEntityByName(ENTITY_BY_NAME);
			Long staticEntityRecId=associateHookEntity(clinicalAnnotations,recordIdentifier,manager);

			boolean isdataHooked=deIntegration.isDataHooked(container.getId(),staticEntityRecId,manager.getEntityByName("edu.wustl.catissuecore.domain.RecordEntry").getId());
     		assertEquals(true, isdataHooked);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
	public void testIsDataHookedForDynRecordId()
	{
		try
		{
			EntityManagerInterface manager = EntityManager.getInstance();
			DEIntegration deIntegration = new DEIntegration();
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			getCategoryIdentifier();
			Category category = (Category) categoryManager.getCategoryByName(CATEGORY_NAME);
			Collection<Container> containers=category.getRootCategoryElement().getContainerCollection();
			Container container=containers.iterator().next();

			CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
			Map<BaseAbstractAttributeInterface, Object> dataValue = mapGenerator.createDataValueMapForCategory(rootCatEntity, 0);

			ContainerInterface 	containerInterface = (ContainerInterface)category.getRootCategoryElement().getContainerCollection().toArray()[0];
			Long recordId =DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);

			Long recordIdentifier = categoryManager.getEntityRecordIdByRootCategoryEntityRecordId(
					recordId, category.getRootCategoryElement().getTableProperties().getName());

			EntityGroupInterface testModel = EntityGroupManager.getInstance().getEntityGroupByName(
					TEST_ENTITYGROUP_NAME);
			EntityInterface clinicalAnnotations = testModel.getEntityByName(ENTITY_BY_NAME);
			associateHookEntity(clinicalAnnotations,recordIdentifier,manager);

			boolean isdataHooked=deIntegration.isDataHooked(container.getId(),recordIdentifier);
     		assertEquals(true, isdataHooked);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
	public void testGetCategoryRecordIdBasedOnCategoryId()
	{
		try
		{
			EntityManagerInterface manager = EntityManager.getInstance();
			DEIntegration deIntegration = new DEIntegration();
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			getCategoryIdentifier();
			Category category = (Category) categoryManager.getCategoryByName(CATEGORY_NAME);
			Collection<Container> containers=category.getRootCategoryElement().getContainerCollection();
			Container container=containers.iterator().next();

			CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
			Map<BaseAbstractAttributeInterface, Object> dataValue = mapGenerator.createDataValueMapForCategory(rootCatEntity, 0);

			ContainerInterface 	containerInterface = (ContainerInterface)category.getRootCategoryElement().getContainerCollection().toArray()[0];
			Long recordId =DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);

			Long recordIdentifier = categoryManager.getEntityRecordIdByRootCategoryEntityRecordId(
					recordId, category.getRootCategoryElement().getTableProperties().getName());
			EntityGroupInterface testModel = EntityGroupManager.getInstance().getEntityGroupByName(
					TEST_ENTITYGROUP_NAME);
			EntityInterface clinicalAnnotations = testModel.getEntityByName(ENTITY_BY_NAME);
			associateHookEntity(clinicalAnnotations,recordIdentifier,manager);
			Collection<Long> recIdList=deIntegration.getCategoryRecordIdBasedOnCategoryId(container.getId(),recordId);
			assertNotNull(recIdList);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
	public void testGetDynamicRecordForCategoryFromStaticId()
	{
		try
		{
			EntityManagerInterface manager = EntityManager.getInstance();
			DEIntegration deIntegration = new DEIntegration();
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			getCategoryIdentifier();
			Category category = (Category) categoryManager.getCategoryByName(CATEGORY_NAME);
			Collection<Container> containers=category.getRootCategoryElement().getContainerCollection();
			Container container=containers.iterator().next();

			CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
			Map<BaseAbstractAttributeInterface, Object> dataValue = mapGenerator.createDataValueMapForCategory(rootCatEntity, 0);

			ContainerInterface 	containerInterface = (ContainerInterface)category.getRootCategoryElement().getContainerCollection().toArray()[0];
			Long recordId =DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);

			Long recordIdentifier = categoryManager.getEntityRecordIdByRootCategoryEntityRecordId(
					recordId, category.getRootCategoryElement().getTableProperties().getName());

			EntityGroupInterface testModel = EntityGroupManager.getInstance().getEntityGroupByName(
					TEST_ENTITYGROUP_NAME);
			EntityInterface clinicalAnnotations = testModel.getEntityByName(ENTITY_BY_NAME);
			Long staticEntityRecId=associateHookEntity(clinicalAnnotations,recordIdentifier,manager);

			JDBCDAO jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			Collection<Long> recIdList=deIntegration.getDynamicRecordForCategoryFromStaticId(staticEntityRecId.toString(),container.getId(),manager.getEntityByName("edu.wustl.catissuecore.domain.RecordEntry").getId().toString(),jdbcDao);
			DynamicExtensionsUtility.closeDAO(jdbcDao);
			assertNotNull(recIdList);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
	public void testGetDynamicRecordFromStaticId()
	{
		try
		{
			EntityManagerInterface manager = EntityManager.getInstance();
			DEIntegration deIntegration = new DEIntegration();
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();

			getCategoryIdentifier();
			Category category = (Category) categoryManager.getCategoryByName(CATEGORY_NAME);
			Collection<Container> containers=category.getRootCategoryElement().getContainerCollection();
			Container container=containers.iterator().next();

			CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
			Map<BaseAbstractAttributeInterface, Object> dataValue = mapGenerator.createDataValueMapForCategory(rootCatEntity, 0);

			ContainerInterface 	containerInterface = (ContainerInterface)category.getRootCategoryElement().getContainerCollection().toArray()[0];
			Long recordId =DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);

			Long recordIdentifier = categoryManager.getEntityRecordIdByRootCategoryEntityRecordId(
					recordId, category.getRootCategoryElement().getTableProperties().getName());

			EntityGroupInterface testModel = EntityGroupManager.getInstance().getEntityGroupByName(
					TEST_ENTITYGROUP_NAME);
			EntityInterface clinicalAnnotations = testModel.getEntityByName(ENTITY_BY_NAME);
			Long staticEntityRecId=associateHookEntity(clinicalAnnotations,recordIdentifier,manager);

			JDBCDAO jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			Collection<Long> recIdList=deIntegration.getDynamicRecordFromStaticId(staticEntityRecId.toString(),container.getId(),manager.getEntityByName("edu.wustl.catissuecore.domain.RecordEntry").getId().toString());
			DynamicExtensionsUtility.closeDAO(jdbcDao);
			assertNotNull(recIdList);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
	public void testIsCategory()
	{
		try
		{
			DEIntegration deIntegration = new DEIntegration();
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			Long catId=getCategoryIdentifier();
			Category category = (Category) categoryManager.getCategoryById(catId);
			Collection<Container> containers=category.getRootCategoryElement().getContainerCollection();
			Container container=containers.iterator().next();
			boolean isCategory=deIntegration.isCategory(container.getId());
			assertEquals(true,isCategory);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
	public void testGetDynamicEntitiesContainerIdFromHookEntity()
	{
	   try
	   {
		EntityManagerInterface manager = EntityManager.getInstance();
		DEIntegration deIntegration = new DEIntegration();
		Collection collection=deIntegration.getDynamicEntitiesContainerIdFromHookEntity(manager.getEntityByName("edu.wustl.catissuecore.domain.RecordEntry").getId());
		assertTrue(collection.size()>0);
	   }
	   catch(Exception exception)
	   {
		   exception.printStackTrace();
		   fail();
	   }
	}
	public void testGetDynamicEntityRecordIdFromHookEntityRecordIdWithStringRecId()
	{
		try
		{
			EntityManagerInterface manager = EntityManager.getInstance();
			DEIntegration deIntegration = new DEIntegration();
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			getCategoryIdentifier();
			Category category = (Category) categoryManager.getCategoryByName(CATEGORY_NAME);
			Collection<Container> containers=category.getRootCategoryElement().getEntity().getContainerCollection();
			Container container=containers.iterator().next();

			CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
			Map<BaseAbstractAttributeInterface, Object> dataValue = mapGenerator.createDataValueMapForCategory(rootCatEntity, 0);

			ContainerInterface 	containerInterface = (ContainerInterface)category.getRootCategoryElement().getContainerCollection().toArray()[0];
			Long recordId =DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);

			List<Long> recordEntryIdList=new ArrayList<Long>();

			Long recordIdentifier = categoryManager.getEntityRecordIdByRootCategoryEntityRecordId(
					recordId, category.getRootCategoryElement().getTableProperties().getName());

			EntityGroupInterface testModel = EntityGroupManager.getInstance().getEntityGroupByName(
					TEST_ENTITYGROUP_NAME);
			EntityInterface clinicalAnnotations = testModel.getEntityByName(ENTITY_BY_NAME);
			Long staticEntityRecId=associateHookEntity(clinicalAnnotations,recordIdentifier,manager);
			recordEntryIdList.add(staticEntityRecId);
			Collection longs=deIntegration.getDynamicEntityRecordIdFromHookEntityRecordId(staticEntityRecId.toString(),container.getId(),manager.getEntityByName("edu.wustl.catissuecore.domain.RecordEntry").getId());
			assertNotNull(longs);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	public void testGetDynamicRecordForCategoryFromStaticIdWithStaticIdList()
	{
		try
		{
			EntityManagerInterface manager = EntityManager.getInstance();
			DEIntegration deIntegration = new DEIntegration();
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			getCategoryIdentifier();
			Category category = (Category) categoryManager.getCategoryByName(CATEGORY_NAME);
			Collection<Container> containers=category.getRootCategoryElement().getContainerCollection();
			Container container=containers.iterator().next();

			CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
			Map<BaseAbstractAttributeInterface, Object> dataValue = mapGenerator.createDataValueMapForCategory(rootCatEntity, 0);

			ContainerInterface 	containerInterface = (ContainerInterface)category.getRootCategoryElement().getContainerCollection().toArray()[0];
			Long recordId =DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);

			Long recordIdentifier = categoryManager.getEntityRecordIdByRootCategoryEntityRecordId(
					recordId, category.getRootCategoryElement().getTableProperties().getName());

			EntityGroupInterface testModel = EntityGroupManager.getInstance().getEntityGroupByName(
					TEST_ENTITYGROUP_NAME);
			EntityInterface clinicalAnnotations = testModel.getEntityByName(ENTITY_BY_NAME);
			Long staticEntityRecId=associateHookEntity(clinicalAnnotations,recordIdentifier,manager);
			List<Long> recordEntryIdList=new ArrayList<Long>();
			recordEntryIdList.add(staticEntityRecId);
			JDBCDAO jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			Collection<Long> recIdList=deIntegration.getDynamicRecordForCategoryFromStaticId(recordEntryIdList,container.getId(),manager.getEntityByName("edu.wustl.catissuecore.domain.RecordEntry").getId().toString(),jdbcDao);
			DynamicExtensionsUtility.closeDAO(jdbcDao);
			assertNotNull(recIdList);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
}