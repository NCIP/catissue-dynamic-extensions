/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright://TODO</p>
 *@author Vishvesh Mulay
 *@author Rahul Ner
 *@version 1.0
 */

package edu.common.dynamicextensions.entitymanager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.common.dynamicextensions.client.DataEditClient;
import edu.common.dynamicextensions.dao.impl.DynamicExtensionDAO;
import edu.common.dynamicextensions.domain.Attribute;
import edu.common.dynamicextensions.domain.AttributeTypeInformation;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domain.EntityGroup;
import edu.common.dynamicextensions.domain.FileAttributeTypeInformation;
import edu.common.dynamicextensions.domain.FileExtension;
import edu.common.dynamicextensions.domain.StringAttributeTypeInformation;
import edu.common.dynamicextensions.domain.StringValue;
import edu.common.dynamicextensions.domain.databaseproperties.TableProperties;
import edu.common.dynamicextensions.domain.userinterface.Container;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CaDSRValueDomainInfoInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleParameterInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.exception.DynamicExtensionsValidationException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.ControlConfigurationsFactory;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.Variables;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationDirection;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.common.dynamicextensions.util.global.DEConstants.ValueDomainType;
import edu.common.dynamicextensions.validation.ValidatorRuleInterface;
import edu.common.dynamicextensions.validation.ValidatorUtil;
import edu.hostApp.src.java.RecordEntry;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;

public class TestEntityManager extends DynamicExtensionsBaseTestCase
{

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(TestEntityManager.class);

	/** The Constant entityManager. */
	private static final EntityManagerInterface entityManager = EntityManager.getInstance();

	/**
	 * Instantiates a new test entity manager.
	 */
	public TestEntityManager()
	{
		super();
	}

	/**
	 * @param arg0
	 *            name
	 */
	public TestEntityManager(String arg0)
	{
		super(arg0);
	}

	/**
	 * @throws DynamicExtensionsCacheException
	 * @see edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCaseUtility#setUp()
	 */
	@Override
	protected void setUp() throws DynamicExtensionsCacheException
	{
		super.setUp();
	}

	/**
	 * @see edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCaseUtility#tearDown()
	 */
	@Override
	protected void tearDown()
	{
		super.tearDown();
	}

	/**
	 * PURPOSE : To test the whether editing of an existing entity (removing an
	 * attribute) works properly or not. EXPECTED BEHAVIOR : Changes in the
	 * existing entity should be stored properly and the changes made to the
	 * attributes of the entity should get reflected properly in the data
	 * tables. TEST CASE FLOW : 1. Create entity with some attributes 2. Save
	 * entity using entity manager. 3. Remove an attribute from the entity 4.
	 * Save the entity again. 5. Check whether the removed attribute is not
	 * present in the entity 6. Check whether a column is removed or not from
	 * the data table of the entity.
	 */
	public void testEditEntityRemoveAttribute()
	{
		EntityGroupInterface entityGroup = DomainObjectFactory.getInstance().createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());
		// Step 1
		Entity entity = (Entity) createAndPopulateEntity();
		entity.setName("Stock Quote");
		// EntityManagerInterface entityManagerInterface =
		// EntityManager.getInstance();
		EntityManagerInterface EntityManagerInterface = EntityManager.getInstance();

		try
		{
			AttributeInterface floatAtribute = DomainObjectFactory.getInstance()
					.createFloatAttribute();
			floatAtribute.setName("Price");
			AttributeInterface commentsAttributes = DomainObjectFactory.getInstance()
					.createStringAttribute();
			commentsAttributes.setName("comments");

			entity.addAbstractAttribute(floatAtribute);
			entity.addAbstractAttribute(commentsAttributes);
			entityGroup.addEntity(entity);
			entity.setEntityGroup(entityGroup);
			// Step 2
			EntityInterface savedEntity = EntityManagerInterface.persistEntity(entity);

			// Map dataValue = new HashMap();
			// dataValue.put(floatAtribute, "15.90");
			// entityManagerInterface.insertData(savedEntity, dataValue);

			assertEquals(getColumnCount("select * from "
					+ savedEntity.getTableProperties().getName()), noOfDefaultColumns + 2);

			AttributeInterface savedFloatAtribute = null;
			// Collection collection = savedEntity.getAttributeCollection();
			// collection =
			// EntityManagerUtil.filterSystemAttributes(collection);
			// Iterator itr = collection.iterator();

			savedFloatAtribute = savedEntity.getAttributeByIdentifier(floatAtribute.getId());
			System.out.println("id is: " + savedFloatAtribute.getId());

			// remove attribute
			// Step 3
			savedEntity.removeAbstractAttribute(savedFloatAtribute);
			// Step 4
			EntityInterface editedEntity = EntityManagerInterface.persistEntity(savedEntity);
			// Step 6

			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ editedEntity.getTableProperties().getName()));
		}
		catch (DynamicExtensionsSystemException e)
		{
			fail();
			Logger.out.debug(e.getStackTrace());
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			Logger.out.debug(e.getStackTrace());
			fail();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
			Logger.out.debug(e.getStackTrace());
		}
	}

	/**
	 * PURPOSE : To test the behavior of entity manager when an entity with null
	 * attribute is tried for saving. EXPECTED BEHAVIOR : Entity manager should
	 * throw an application exception with proper error code. TEST CASE FLOW :
	 * 1. Create entity with null attribute 2. Save entity using entity manager.
	 * 3. Check whether the application exception is thrown or not.
	 */
	public void testCreateEnityWithNullAttribute()
	{
		try
		{
			// Step 1
			Entity entity = (Entity) createAndPopulateEntity();
			EntityGroup entityGroup = (EntityGroup) DomainObjectFactory.getInstance()
					.createEntityGroup();
			entityGroup.setName("testGroup" + new Double(Math.random()).toString());
			entity.addAbstractAttribute(null);
			EntityManagerUtil.addIdAttribute(entity);
			entity.setEntityGroup(entityGroup);
			entityGroup.addEntity(entity);
			// EntityManagerInterface entityManagerInterface =
			// EntityManager.getInstance();
			EntityManagerInterface EntityManagerInterface = EntityManager.getInstance();
			// Step 2
			EntityManagerInterface.validateEntity(entity);
			fail();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			// Step 3
			Logger.out
					.debug("Exception should occur coz the attribute is null.. validation should fail");
		}
		catch (DynamicExtensionsSystemException e)
		{
			fail();
			Logger.out.debug(e.getStackTrace());
		}
	}

	/**
	 * PURPOSE : To test whether the created date is set on saving of entity or
	 * not. EXPECTED BEHAVIOR : Before saving the created date is null. After
	 * the entity is saved created date should be set. TEST CASE FLOW : 1.
	 * Create entity with null created date 2. Save entity using entity manager.
	 * 3. Check whether the created date is set or not.
	 */
	public void testCreateEntityNull()
	{
		EntityGroup entityGroup = (EntityGroup) DomainObjectFactory.getInstance()
				.createEntityGroup();
		entityGroup.setName("testGroup" + new Double(Math.random()).toString());
		// Step 1
		Entity entity = (Entity) createAndPopulateEntity();
		entity.setName("test");
		entityGroup.addEntity(entity);
		entity.setEntityGroup(entityGroup);
		EntityManagerInterface EntityManagerInterface = EntityManager.getInstance();

		// EntityManagerInterface entityManagerInterface =
		// EntityManager.getInstance();

		try
		{
			// Step 2
			EntityInterface savedEntity = EntityManagerInterface.persistEntity(entity);
			assertEquals(savedEntity.getName(), entity.getName());

			String tableName = entity.getTableProperties().getName();
			String query = "Select * from " + tableName;
			getColumnCount(query);

			// Step 3
			assertNotNull(savedEntity.getCreatedDate());
		}
		catch (DynamicExtensionsSystemException e)
		{
			fail();
			Logger.out.debug(e.getStackTrace());
		}
		catch (DynamicExtensionsApplicationException e)
		{
			fail();
			Logger.out.debug(e.getStackTrace());
		}
	}

	/**
	 * PURPOSE : To test the method getEntityGroupByName EXPECTED BEHAVIOR : The
	 * new entity group should be stored correctly and should be retrieved back
	 * correctly if the name of that entity group is given. TEST CASE FLOW : 1.
	 * Create entity group. 2. Save entityGroup using entity manager. 3. Check
	 * whether the saved entity group is retrieved back properly or not given
	 * it's name.
	 */
	public void testGetEntityGroupByName()
	{
		try
		{
			// Step 1
			EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();
			// Step 2
			EntityGroup entityGroup = (EntityGroup) new MockEntityManager().initializeEntityGroup();
			entityGroupManager.persistEntityGroup(entityGroup);
			// Step 3
			EntityGroupInterface entityGroupInterface = entityGroupManager
					.getEntityGroupByName(entityGroup.getName());
			assertNotNull(entityGroupInterface);
		}
		catch (Exception e)
		{
			Logger.out.debug(e.getMessage());
			e.printStackTrace();
			fail("Exception occured");
		}
	}

	/**
	 * PURPOSE : To test the method getEntitiesByConceptCode EXPECTED BEHAVIOR :
	 * All the entities that are associated with the given concept code should
	 * be retrieved back correctly TEST CASE FLOW : 1. Create entity with some
	 * concept code 2. Save entity using entity manager. 3. Check whether the
	 * saved entity is retrieved back properly or not given it's concept code.
	 */
	public void testGetEntitiesByConceptCode()
	{
		EntityGroup entityGroup = (EntityGroup) DomainObjectFactory.getInstance()
				.createEntityGroup();
		entityGroup.setName("testGroup" + new Double(Math.random()).toString());
		EntityManagerInterface EntityManagerInterface = EntityManager.getInstance();
		try
		{
			// Step 1
			Entity entity = (Entity) new MockEntityManager().initializeEntity(entityGroup);
			SemanticPropertyInterface semanticPropertyInterface = new MockEntityManager()
					.initializeSemanticProperty();
			entity.addSemanticProperty(semanticPropertyInterface);
			// Step 2
			entity = (Entity) EntityManagerInterface.persistEntity(entity);

			// Step 3
			Collection entityCollection = EntityManagerInterface
					.getEntitiesByConceptCode(semanticPropertyInterface.getConceptCode());
			assertTrue(entityCollection != null && entityCollection.size() > 0);
		}
		catch (Exception e)
		{
			Logger.out.debug(e.getMessage());
			fail("Exception occured");
		}
	}

	/**
	 * PURPOSE : To test the method getEntityGroupByName EXPECTED BEHAVIOR : The
	 * new entity group should be stored correctly and should be retrieved back
	 * correctly if the name of that entity group is given. TEST CASE FLOW : 1.
	 * Create entity group. 2. Save entityGroup using entity manager. 3. Check
	 * whether the saved entity group is retrieved back properly or not given
	 * it's name.
	 */
	public void testGetEntityByName()
	{
		EntityManagerInterface EntityManagerInterface = EntityManager.getInstance();
		try
		{
			EntityGroup entityGroup = (EntityGroup) DomainObjectFactory.getInstance()
					.createEntityGroup();
			entityGroup.setName("testGroup" + new Double(Math.random()).toString());
			// Step 1
			Entity entity = (Entity) new MockEntityManager().initializeEntity(entityGroup);
			// Step 2
			entity = (Entity) EntityManagerInterface.persistEntity(entity);
			// Step 3
			EntityInterface entityInterface = EntityManagerInterface.getEntityByName(entity
					.getName());
			assertNotNull(entityInterface);
		}
		catch (Exception e)
		{
			Logger.out.debug(e.getMessage());
			fail("Exception occured");
		}
	}

	/**
	 * This method tests GetAttribute method.
	 *
	 */
	public void testGetAttribute()
	{
		EntityManagerInterface EntityManagerInterface = EntityManager.getInstance();
		try
		{
			DomainObjectFactory factory = DomainObjectFactory.getInstance();
			EntityGroup entityGroup = (EntityGroup) factory.createEntityGroup();
			entityGroup.setName("testGroup" + new Double(Math.random()).toString());
			// create user
			EntityInterface user = createAndPopulateEntity();
			AttributeInterface userNameAttribute = factory.createStringAttribute();
			userNameAttribute.setName("user name");
			user.setName("user");
			user.addAbstractAttribute(userNameAttribute);
			entityGroup.addEntity(user);
			user.setEntityGroup(entityGroup);
			user = EntityManagerInterface.persistEntity(user);

			AttributeInterface attributeInterface = EntityManagerInterface.getAttribute("user",
					"user name");
			assertNotNull(attributeInterface);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Logger.out.debug(e.getMessage());
			fail("Exception occured");
		}

	}

	/**
	 * This method test for updating record for an entity.
	 */
	public void testEditRecord()
	{
		try
		{
			EntityGroupInterface testModel = EntityGroupManager.getInstance().getEntityGroupByName(
					TEST_ENTITYGROUP_NAME);
			EntityInterface clinicalAnnotations = testModel.getEntityByName("Chemotherapy");
			AttributeInterface startDate = (AttributeInterface) clinicalAnnotations
					.getAbstractAttributeByName("startDate");
			AttributeInterface endDate = (AttributeInterface) clinicalAnnotations
					.getAbstractAttributeByName("endDate");

			Map dataValue = new HashMap();

			// dataValue.put(commentsAttributes, "this is not default comment");
			dataValue.put(startDate, "02-2009");
			dataValue.put(endDate, "2009");

			ContainerInterface containerInterface = (ContainerInterface) clinicalAnnotations
					.getContainerCollection().toArray()[0];
			Long recordId = DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);

			assertNotNull(recordId);
			Map map = EntityManager.getInstance().getRecordById(clinicalAnnotations, recordId);

			assertEquals("02-2009", map.get(startDate));
			assertEquals("2009", map.get(endDate));

			dataValue = new HashMap();

			dataValue.put(startDate, "08-2010");
			dataValue.put(endDate, "2010");
			editData(containerInterface, recordId, dataValue);

			map = EntityManager.getInstance().getRecordById(clinicalAnnotations, recordId);
			assertEquals("08-2010", map.get(startDate));
			assertEquals("2010", map.get(endDate));

		}
		catch (DynamicExtensionsSystemException e)
		{
			fail();
			e.printStackTrace();
			Logger.out.debug(e.getStackTrace());
		}
		catch (DynamicExtensionsApplicationException e)
		{
			fail();
			e.printStackTrace();
			Logger.out.debug(e.getStackTrace());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
			Logger.out.debug(e.getStackTrace());
		}
	}

	public void editData(ContainerInterface container, Long recordId, Map dataValue)
			throws MalformedURLException
	{
		String entityGroupName = null;
		if (container.getAbstractEntity() instanceof EntityInterface)
		{
			entityGroupName = ((Entity) container.getAbstractEntity()).getEntityGroup().getName();
		}
		else
		{
			entityGroupName = ((CategoryEntityInterface) container.getAbstractEntity()).getEntity()
					.getEntityGroup().getName();
		}
		Map<String, Object> clientmap = new HashMap<String, Object>();
		DataEditClient dataEditClient = new DataEditClient();
		clientmap.put(WebUIManagerConstants.RECORD_ID, recordId);
		clientmap.put(WebUIManagerConstants.SESSION_DATA_BEAN, null);
		clientmap.put(WebUIManagerConstants.USER_ID, null);
		clientmap.put(WebUIManagerConstants.CONTAINER, container);
		clientmap.put(WebUIManagerConstants.DATA_VALUE_MAP, dataValue);
		dataEditClient.setServerUrl(new URL(Variables.jbossUrl + entityGroupName + "/"));
		dataEditClient.setParamaterObjectMap(clientmap);
		dataEditClient.execute(null);

	}

	/**
	 * This method associates static entity record Id with dynamic entity record
	 * Id
	 *
	 * @param clinicalAnnotations
	 * @param recordId
	 */
	private void associateHookEntity(EntityInterface clinicalAnnotations, Long recordId)
	{
		try
		{
			EntityManagerInterface manager = EntityManager.getInstance();

			// Create static entity record object
			RecordEntry recordEntry = new RecordEntry();

			HibernateDAO hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();

			// Insert record for static entity record object
			hibernateDAO.insert(recordEntry);

			if (hibernateDAO != null)
			{
				hibernateDAO.commit();
				DynamicExtensionsUtility.closeDAO(hibernateDAO);
			}
			// get Id for inserted record entry
			Long staticEntityRecId = recordEntry.getId();

			// Get static entity from metadata id of recordEntry
			EntityInterface staticEntity = manager
					.getEntityByName("edu.wustl.catissuecore.domain.RecordEntry");

			// search for association between static and dynamic entities for
			// associating these two entities
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
				// associate static entity record Id with dynamic entity record
				// Id
				manager.associateEntityRecords(asntInterface, staticEntityRecId, recordId);
			}

		}
		catch (Exception e)
		{
			Logger.out.debug(e.getMessage());
			e.printStackTrace();
			fail("Exception occured");
		}
	}

	/**
	 * Creates Object using reflection
	 *
	 * @param className
	 * @return
	 */
	static Object createObject(String className)
	{
		Object object = null;
		try
		{
			Class classDefinition = Class.forName(className);
			object = classDefinition.newInstance();
		}
		catch (InstantiationException e)
		{
			Logger.out.error(e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			Logger.out.error(e.getMessage());
		}
		catch (ClassNotFoundException e)
		{
			Logger.out.error(e.getMessage());
		}
		return object;
	}

	/**
	 * This method edits an existing attribute to a file type attribute.
	 */
	public void testEditFileAttribute()
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroup entityGroup = (EntityGroup) factory.createEntityGroup();
		entityGroup.setName("testGroup" + new Double(Math.random()).toString());
		EntityManagerInterface EntityManagerInterface = EntityManager.getInstance();

		// create user
		EntityInterface user = createAndPopulateEntity();
		user.setEntityGroup(entityGroup);
		entityGroup.addEntity(user);
		user.setName("User");
		AttributeInterface resume = factory.createStringAttribute();
		resume.setName("Resume");
		user.addAbstractAttribute(resume);

		user.setEntityGroup(entityGroup);
		entityGroup.addEntity(user);

		try
		{
			int rowCount = (Integer) executeQuery("select count(*) from dyextn_file_extensions",
					INT_TYPE, 1, null);
			System.out.println(rowCount);

			// save the entity
			user = EntityManagerInterface.persistEntity(user);

			assertEquals(getColumnCount("select * from " + user.getTableProperties().getName()),
					noOfDefaultColumns + 1);

			// Edit attribute: change attribute type to file attribiute type.
			Collection<FileExtension> allowedExtn = new HashSet<FileExtension>();

			FileExtension txtExtn = new FileExtension();
			txtExtn.setFileExtension("txt");
			allowedExtn.add(txtExtn);

			FileExtension pdfExtn = new FileExtension();
			pdfExtn.setFileExtension("pdf");
			allowedExtn.add(pdfExtn);

			allowedExtn.add(txtExtn);
			allowedExtn.add(pdfExtn);

			AttributeTypeInformation fileTypeInformation = factory
					.createFileAttributeTypeInformation();

			((FileAttributeTypeInformation) fileTypeInformation).setMaxFileSize(20F);
			((FileAttributeTypeInformation) fileTypeInformation)
					.setFileExtensionCollection(allowedExtn);

			resume.setAttributeTypeInformation(fileTypeInformation);

			user = EntityManagerInterface.persistEntity(user);

			// DBUtil.closeConnection();
			// DBUtil.Connection();

			assertEquals(getColumnCount("select * from " + user.getTableProperties().getName()),
					noOfDefaultColumnsForfile);

			// executeQuery("select * from dyextn_file_extensions");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * This method tests for creating a entity with file attribute.
	 */
	public void testCreateFileAttribute()
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroup entityGroup = (EntityGroup) factory.createEntityGroup();
		entityGroup.setName("testGroup" + new Double(Math.random()).toString());
		EntityManagerInterface EntityManagerInterface = EntityManager.getInstance();

		// create user
		EntityInterface user = createAndPopulateEntity();
		user.setName("User");
		AttributeInterface resume = factory.createFileAttribute();
		resume.setName("Resume");
		user.addAbstractAttribute(resume);
		user.setEntityGroup(entityGroup);
		entityGroup.addEntity(user);

		Collection<FileExtension> allowedExtn = new HashSet<FileExtension>();

		FileExtension txtExtn = new FileExtension();
		txtExtn.setFileExtension("txt");
		allowedExtn.add(txtExtn);

		FileExtension pdfExtn = new FileExtension();
		pdfExtn.setFileExtension("pdf");
		allowedExtn.add(pdfExtn);

		allowedExtn.add(txtExtn);
		allowedExtn.add(pdfExtn);

		((FileAttributeTypeInformation) resume.getAttributeTypeInformation()).setMaxFileSize(20F);
		((FileAttributeTypeInformation) resume.getAttributeTypeInformation())
				.setFileExtensionCollection(allowedExtn);

		try
		{
			executeQuery("select count(*) from dyextn_file_extensions", INT_TYPE, 1, null);
			user = EntityManagerInterface.persistEntity(user);
			assertEquals(getColumnCount("select * from " + user.getTableProperties().getName()),
					noOfDefaultColumnsForfile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 *
	 */
	public void testCreateEntity()
	{
		EntityManagerInterface EntityManagerInterface = EntityManager.getInstance();

		try
		{
			EntityGroup entityGroup = (EntityGroup) DomainObjectFactory.getInstance()
					.createEntityGroup();
			entityGroup.setName("testGroup" + new Double(Math.random()).toString());
			Entity entity = (Entity) new MockEntityManager().initializeEntity(entityGroup);
			TaggedValueInterface taggedValue = DomainObjectFactory.getInstance()
					.createTaggedValue();
			taggedValue.setKey("a");
			taggedValue.setValue("b");
			entity.addTaggedValue(taggedValue);
			entity = (Entity) EntityManagerInterface.persistEntity(entity);

			Entity newEntity = (Entity) EntityManagerInterface.getEntityByIdentifier(entity.getId()
					.toString());
			assertEquals(entity.getName(), newEntity.getName());

			String tableName = entity.getTableProperties().getName();
			String query = "Select * from " + tableName;
			getColumnCount(query);
		}
		catch (Exception e)
		{
			Logger.out.debug(e.getMessage());
			e.printStackTrace();
			fail("Exception occured");
		}
	}

	/**
	 *
	 */
	public void testCreateEntityWithEntityGroup()
	{
		EntityManagerInterface EntityManagerInterface = EntityManager.getInstance();

		try
		{
			EntityGroup entityGroup = (EntityGroup) DomainObjectFactory.getInstance()
					.createEntityGroup();
			entityGroup.setName("testGroup" + new Double(Math.random()).toString());
			Entity entity = (Entity) new MockEntityManager().initializeEntity(entityGroup);
			entity = (Entity) EntityManagerInterface.persistEntity(entity);
			Entity newEntity = (Entity) EntityManagerInterface.getEntityByIdentifier(entity.getId()
					.toString());
			// Checking whether metadata information is saved properly or not.
			assertEquals(entity.getName(), newEntity.getName());
			// Collection collection = newEntity.getEntityGroupCollection();
			// Iterator iter = collection.iterator();
			EntityGroup eg = (EntityGroup) newEntity.getEntityGroup();
			assertEquals(eg.getId(), entityGroup.getId());
		}
		catch (Exception e)
		{
			Logger.out.debug(e.getMessage());
			fail("Exception occured");
		}
	}

	/**
	 * This method tests GetRecordById method for the condition where record and
	 * entity does exists
	 */
	public void testGetRecordById()
	{
		try
		{
			EntityGroupInterface testModel = EntityGroupManager.getInstance().getEntityGroupByName(
					TEST_ENTITYGROUP_NAME);
			EntityInterface clinicalAnnotations = testModel.getEntityByName("Chemotherapy");
			AttributeInterface startDate = (AttributeInterface) clinicalAnnotations
					.getAbstractAttributeByName("startDate");
			AttributeInterface endDate = (AttributeInterface) clinicalAnnotations
					.getAbstractAttributeByName("endDate");

			Map dataValue = new HashMap();

			// dataValue.put(commentsAttributes, "this is not default comment");
			dataValue.put(startDate, "02-2009");
			dataValue.put(endDate, "2009");

			ContainerInterface containerInterface = (ContainerInterface) clinicalAnnotations
					.getContainerCollection().toArray()[0];
			Long recordId = DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);
			assertNotNull(recordId);
			Map map = EntityManager.getInstance().getRecordById(clinicalAnnotations, recordId);

			assertEquals("02-2009", map.get(startDate));
			assertEquals("2009", map.get(endDate));

		}
		catch (DynamicExtensionsSystemException e)
		{
			fail();
			e.printStackTrace();
			Logger.out.debug(e.getStackTrace());
		}
		catch (DynamicExtensionsApplicationException e)
		{
			fail();
			e.printStackTrace();
			Logger.out.debug(e.getStackTrace());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
			Logger.out.debug(e.getStackTrace());
		}
	}

	/**
	 * This method tests GetRecordById method for the condition wheer record
	 * does not exists for given id.
	 */
	public void testGetRecordByIdNoRecord()
	{
		EntityManagerInterface EntityManagerInterface = EntityManager.getInstance();

		try
		{
			EntityGroup entityGroup = (EntityGroup) DomainObjectFactory.getInstance()
					.createEntityGroup();
			entityGroup.setName("testGroup" + new Double(Math.random()).toString());
			Entity entity = (Entity) new MockEntityManager().initializeEntity(entityGroup);
			entity = (Entity) EntityManagerInterface.persistEntity(entity);

			Map map = EntityManager.getInstance().getRecordById(entity, new Long(1));
			assertEquals(0, map.size());
		}
		catch (DynamicExtensionsSystemException e)
		{
			Logger.out.debug(e.getMessage());
			fail("Exception occured");
		}
		catch (DynamicExtensionsApplicationException e)
		{
			Logger.out.debug(e.getMessage());
			fail("Exception occured");
		}
	}

	/**
	 * This method tests GetRecordById method for the condition where entity is
	 * not saved , entity is null or record id id null
	 */
	public void testGetRecordByIdEntityNotSaved()
	{
		EntityManagerInterface EntityManagerInterface = EntityManager.getInstance();
		Entity entity = null;

		try
		{
			EntityGroup entityGroup = (EntityGroup) DomainObjectFactory.getInstance()
					.createEntityGroup();
			entityGroup.setName("testGroup" + new Double(Math.random()).toString());
			entity = (Entity) new MockEntityManager().initializeEntity(entityGroup);

			EntityManagerInterface.getRecordById(entity, new Long(1));
			fail("Exception should have be thrown since entity is not saved");
		}
		catch (DynamicExtensionsSystemException e)
		{
			assertTrue(true);
		}
		catch (DynamicExtensionsApplicationException e)
		{
			Logger.out.debug(e.getMessage());
			fail("Unexpected Exception occured");
		}

		try
		{
			EntityManagerInterface.getRecordById(null, new Long(1));
			fail("Exception should have be thrown since entity is not saved");
		}
		catch (DynamicExtensionsSystemException e)
		{
			assertTrue(true);
		}
		catch (DynamicExtensionsApplicationException e)
		{
			Logger.out.debug(e.getMessage());
		}

		try
		{
			entity = (Entity) EntityManagerInterface.persistEntity(entity);

			EntityManagerInterface.getRecordById(entity, null);
			fail("Exception should have be thrown since entity is not saved");
		}
		catch (DynamicExtensionsSystemException e)
		{
			assertTrue(true);
		}
		catch (DynamicExtensionsApplicationException e)
		{
			Logger.out.debug(e.getMessage());
		}
	}

	/**
	 *
	 */
	public void testCreateContainer()
	{
		EntityManagerInterface EntityManagerInterface = EntityManager.getInstance();

		try
		{
			Container container = (Container) new MockEntityManager().getContainer("abc");
			EntityManagerInterface.persistEntity((EntityInterface) container.getAbstractEntity());
			Collection list = EntityManagerInterface.getAllContainers();
			assertNotNull(list);
			Iterator iter = list.iterator();
			boolean flag = false;
			while (iter.hasNext())
			{
				Container cont = (Container) iter.next();
				if (cont.getId().equals(container.getId()))
				{
					flag = true;
					break;
				}
			}
			assertTrue(flag);
		}
		catch (Exception e)
		{
			Logger.out.debug(e.getMessage());
			fail("Exception occured");
		}
	}

	/**
	 *
	 */
	public void testCreateContainerForContainerWithoutEntity()
	{
		EntityManagerInterface EntityManagerInterface = EntityManager.getInstance();

		try
		{
			EntityGroup entityGroup = (EntityGroup) DomainObjectFactory.getInstance()
					.createEntityGroup();
			entityGroup.setName("testGroup" + new Double(Math.random()).toString());
			Container container = new Container();
			container.setCaption("testcontainer");
			Entity entityInterface = (Entity) new MockEntityManager().initializeEntity(entityGroup);
			EntityManagerUtil.addIdAttribute(entityInterface);
			container.setAbstractEntity(entityInterface);
			Collection<ContainerInterface> listOfContainers = new HashSet<ContainerInterface>();
			listOfContainers.add(container);
			entityInterface.setContainerCollection(listOfContainers);
			EntityManagerInterface.persistEntity((EntityInterface) container.getAbstractEntity());
			Collection list = EntityManagerInterface.getAllContainers();
			assertNotNull(list);
			Iterator iter = list.iterator();
			boolean flag = false;
			while (iter.hasNext())
			{
				Container cont = (Container) iter.next();
				if (cont.getId().equals(container.getId()))
				{
					flag = true;
					break;
				}
			}
			assertTrue(flag);
		}
		catch (Exception e)
		{
			Logger.out.debug(e.getMessage());
			fail("Exception occured");
		}
	}

	/**
	 *
	 */
	public void testEditEntityForNewAddedAttribute()
	{
		EntityManagerInterface EntityManagerInterface = EntityManager.getInstance();

		try
		{
			EntityGroup entityGroup = (EntityGroup) DomainObjectFactory.getInstance()
					.createEntityGroup();
			entityGroup.setName("testGroup" + new Double(Math.random()).toString());
			Entity entity = (Entity) new MockEntityManager().initializeEntity(entityGroup);
			entity = (Entity) EntityManagerInterface.persistEntity(entity);
			AttributeInterface attr = new MockEntityManager().initializeStringAttribute("na", "ab");
			attr.setEntity(entity);
			entity.addAbstractAttribute(attr);
			entity = (Entity) EntityManagerInterface.persistEntity(entity);

			entity.getTableProperties().getName();
		}
		catch (Exception e)
		{
			Logger.out.debug(e.getMessage());
			fail("Exception occured");
		}
	}

	/**
	 *
	 */
	public void testEditEntityForModifiedIsNullableAttribute()
	{
		EntityManagerInterface EntityManagerInterface = EntityManager.getInstance();

		try
		{
			EntityGroup entityGroup = (EntityGroup) DomainObjectFactory.getInstance()
					.createEntityGroup();
			entityGroup.setName("testGroup" + new Double(Math.random()).toString());
			Entity entity = (Entity) new MockEntityManager().initializeEntity(entityGroup);
			AttributeInterface attr = new MockEntityManager().initializeStringAttribute("na", "ab");
			attr.setEntity(entity);
			entity.addAbstractAttribute(attr);
			entity = (Entity) EntityManagerInterface.persistEntity(entity);
			attr = entity.getAttributeByIdentifier(attr.getId());
			attr.setIsNullable(new Boolean(false));
			entity = (Entity) EntityManagerInterface.persistEntity(entity);
			Attribute savedAttribute = (Attribute) entity.getAttributeByIdentifier(attr.getId());
			assertFalse(savedAttribute.getIsNullable());
			entity.getTableProperties().getName();
		}
		catch (Exception e)
		{
			Logger.out.debug(e.getMessage());
			fail("Exception occured");
		}
	}

	/**
	 * PURPOSE : to test the method persistEntityMetadata. EXPECTED BEHAVIOR :
	 * It should only save the metadata information of the entity and not create
	 * the data table for the entity. TEST CASE FLOW : 1.Create an entity
	 * 2.Populate the entity. 3.Test that the metadata information is properly
	 * saved or not. 4.Check that the data table is not created.
	 */
	public void testPersistEntityMetadata()
	{
		EntityManagerInterface EntityManagerInterface = EntityManager.getInstance();

		try
		{
			EntityGroup entityGroup = (EntityGroup) DomainObjectFactory.getInstance()
					.createEntityGroup();
			entityGroup.setName("testGroup" + new Double(Math.random()).toString());
			// Step 1
			Entity entity = (Entity) new MockEntityManager().initializeEntity(entityGroup);
			EntityManagerUtil.addIdAttribute(entity);
			TaggedValueInterface taggedValue = DomainObjectFactory.getInstance()
					.createTaggedValue();
			// Step 2
			taggedValue.setKey("a");
			taggedValue.setValue("b");
			entity.addTaggedValue(taggedValue);
			entity = (Entity) EntityManagerInterface.persistEntityMetadata(entity);

			// Step 3
			Entity newEntity = (Entity) EntityManagerInterface.getEntityByIdentifier(entity.getId()
					.toString());
			assertEquals(entity.getName(), newEntity.getName());
			// Step 4
			String tableName = entity.getTableProperties().getName();
			assertFalse(isTablePresent(tableName));
		}
		catch (Exception e)
		{
			Logger.out.debug(e.getMessage());
			e.printStackTrace();
			fail("Exception occured");
		}
	}

	/**
	 * This method tests the creation of entity group
	 */
	public void testCreateEntityGroupForRollback()
	{
		EntityGroup entityGroup = null;
		EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();

		try
		{
			MockEntityManager mock = new MockEntityManager();
			entityGroup = (EntityGroup) mock.initializeEntityGroup();
			for (int i = 0; i <= 11; i++)
			{
				Entity entity = (Entity) mock.initializeEntity(entityGroup);
				entityGroup.addEntity(entity);
				if (i == 10)
				{
					entity = (Entity) mock.initializeEntity(entityGroup);
					TableProperties tableProperties = new TableProperties();
					tableProperties.setName("@#$%@$%$^");
					entity.setTableProperties(tableProperties);
					entityGroup.addEntity(entity);
				}
			}
			entityGroupManager.persistEntityGroup(entityGroup);
			fail();
		}
		catch (Exception e)
		{
			boolean isTablePresent = isTablePresent(entityGroup.getEntityCollection().iterator()
					.next().getTableProperties().getName());
			assertFalse(isTablePresent);
			// e.printStackTrace();
		}
	}

	/**
	 * This method tests the creation of entity group
	 */
	public void testCreateEntityGroupForMultipleEntities()
	{
		EntityGroup entityGroup = null;
		EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();

		try
		{
			MockEntityManager mock = new MockEntityManager();
			entityGroup = (EntityGroup) mock.initializeEntityGroup();
			for (int i = 0; i <= 11; i++)
			{
				Entity entity = (Entity) mock.initializeEntity(entityGroup);
				entityGroup.addEntity(entity);
			}
			entityGroupManager.persistEntityGroup(entityGroup);
			Iterator iterator = entityGroup.getEntityCollection().iterator();
			while (iterator.hasNext())
			{
				Entity entity = (Entity) iterator.next();
				boolean isTablePresent = isTablePresent(entity.getTableProperties().getName());
				assertTrue(isTablePresent);
			}
		}
		catch (Exception e)
		{
			fail();
			e.printStackTrace();
		}
	}

	/**
	 * PURPOSE : To test the method persistEntityGroupMetadata EXPECTED BEHAVIOR
	 * : Entity group metadata should be stored properly without creating the
	 * data tables for the associated entities. TEST CASE FLOW : 1. Create
	 * entity group 2. Create entities and add them to the entity group. 3. Call
	 * the method persistEntityGroupMetadata to store the metdata information.
	 * 4. Check whether the metadata is stored correctly. 5. Check that the data
	 * tables are not created for the associated entities.
	 */
	public void testPersistEntityGroupMetadataForMultipleEntities()
	{
		EntityGroup entityGroup = null;
		EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();

		try
		{
			// Step 1
			MockEntityManager mock = new MockEntityManager();
			entityGroup = (EntityGroup) mock.initializeEntityGroup();
			entityGroup.setEntityCollection(new HashSet());
			// Step 2
			for (int i = 0; i <= 9; i++)
			{
				Entity entity = (Entity) mock.initializeEntity(entityGroup);
				entityGroup.addEntity(entity);
			}
			// Step 3
			entityGroupManager.persistEntityGroupMetadata(entityGroup);
			// Step 4
			Collection entityCollection = entityGroup.getEntityCollection();
			assertEquals(10, entityCollection.size());
			Iterator iterator = entityCollection.iterator();
			while (iterator.hasNext())
			{
				Entity entity = (Entity) iterator.next();
				// Step 5
				boolean isTablePresent = isTablePresent(entity.getTableProperties().getName());
				assertFalse(isTablePresent);
			}
		}
		catch (Exception e)
		{
			fail();
			e.printStackTrace();
		}
	}

	/**
	 *
	 */
	public void testgetAllContainersByEntityGroupId()
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();

		// ContainerInterface userContainer = factory.createContainer();
		// userContainer.setCaption("userContainer");
		// EntityInterface user = createAndPopulateEntity();
		// user.setName("User");
		// userContainer.setEntity(user);
		//
		// ContainerInterface managerContainer = factory.createContainer();
		// managerContainer.setCaption("managerContainer");
		// EntityInterface manager = createAndPopulateEntity();
		// manager.setName("Manager");
		// managerContainer.setEntity(manager);
		//
		// EntityGroupInterface userGroup = factory.createEntityGroup();
		// userGroup.setName("test_" + new Double(Math.random()).toString());
		// userGroup.addEntity(user);
		// userGroup.addEntity(manager);
		//
		// user.setEntityGroup(userGroup);
		// manager.setEntityGroup(userGroup);

		ContainerInterface studyContainer = factory.createContainer();
		studyContainer.setCaption("newstudyContainer");
		EntityInterface study = createAndPopulateEntity();
		study.setName("study");
		studyContainer.setAbstractEntity(study);
		Collection<ContainerInterface> containerCollection = new HashSet<ContainerInterface>();
		containerCollection.add(studyContainer);
		study.setContainerCollection(containerCollection);

		ContainerInterface javaStudyContainer = factory.createContainer();
		javaStudyContainer.setCaption("javaStudyContainer");
		EntityInterface javaStudy = createAndPopulateEntity();
		javaStudy.setName("javaStudy");
		javaStudyContainer.setAbstractEntity(javaStudy);
		Collection<ContainerInterface> containers = new HashSet<ContainerInterface>();
		containers.add(javaStudyContainer);
		javaStudy.setContainerCollection(containers);

		EntityGroupInterface studyGroup = factory.createEntityGroup();
		studyGroup.setName("test_" + new Double(Math.random()).toString());
		studyGroup.addEntity(study);
		studyGroup.addEntity(javaStudy);

		study.setEntityGroup(studyGroup);
		javaStudy.setEntityGroup(studyGroup);

		try
		{
			// userContainer = entityManager.persistContainer(userContainer);
			// managerContainer =
			// entityManager.persistContainer(managerContainer);
			// studyContainer = entityManager.persistContainer(studyContainer);
			// javaStudyContainer =
			// entityManager.persistContainer(javaStudyContainer);
			entityGroupManager.persistEntityGroup(studyGroup);

			Collection studyGroupContainerCollection = studyGroup.getEntityCollection();

			assertEquals(2, studyGroupContainerCollection.size());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 *
	 *
	 */
	public void testGetContainerByEntityIdentifier()
	{
		EntityManagerInterface EntityManagerInterface = EntityManager.getInstance();
		try
		{
			ContainerInterface containerInterface = new MockEntityManager().getContainer("abc");
			EntityInterface entityInterface = (EntityInterface) containerInterface
					.getAbstractEntity();

			EntityManagerInterface.persistEntity(entityInterface);
			assertTrue(EntityManagerInterface.getEntityByIdentifier(entityInterface.getId())
					.getContainerCollection().contains(containerInterface));
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * PURPOSE : to test the fix for bug 3209.
	 *
	 *
	 * EXPECTED BEHAVIOR : If file attribute is added during edit,its column
	 * should not be getting added
	 *
	 * TEST CASE FLOW : 1.Create an entity 2.Persist the entity. 3.edit entity
	 * adding a file attribute 4. persist again 5. chk for no of column for that
	 * entity
	 */
	public void testEditEntityToAddFileAttribute()
	{
		EntityManagerInterface EntityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroup entityGroup = (EntityGroup) factory.createEntityGroup();
		entityGroup.setName("testGroup" + new Double(Math.random()).toString());

		try
		{
			// Step 1.
			EntityInterface user = createAndPopulateEntity();
			user.setEntityGroup(entityGroup);
			entityGroup.addEntity(user);
			user.setName("user");

			AttributeInterface commentsAttributes = factory.createStringAttribute();
			commentsAttributes.setName("comments");
			user.addAbstractAttribute(commentsAttributes);

			// Step 2.
			EntityInterface savedEntity = EntityManagerInterface.persistEntity(user);

			// assertEquals(getColumnCount("select * from " +
			// savedEntity.getTableProperties().getName()), noOfDefaultColumns +
			// 1);

			// Step 3.
			AttributeInterface resume = factory.createFileAttribute();
			resume.setName("resume");
			user.addAbstractAttribute(resume);

			// Step 4.
			savedEntity = EntityManagerInterface.persistEntity(user);

			// Step 5.

			assertEquals(getColumnCount("select * from "
					+ savedEntity.getTableProperties().getName()), noOfDefaultColumnsForfile + 1);
		}
		catch (DynamicExtensionsSystemException e)
		{
			fail();
			Logger.out.debug(e.getStackTrace());
		}
		catch (DynamicExtensionsApplicationException e)
		{
			fail();
			Logger.out.debug(e.getStackTrace());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
			Logger.out.debug(e.getStackTrace());
		}
	}

	/**
	 * Create a date only attribute, and try to insert a future date value Data
	 * should not get inserted and a validation message with cause should be
	 * shown to the user.
	 * @throws DynamicExtensionsSystemException
	 */
	public void testInsertFutureDateForDateOnlyFormat() throws DynamicExtensionsSystemException
	{
		// Step 1
		EntityGroupInterface testGroup = EntityGroupManager.getInstance().getEntityGroupByName(
				TEST_ENTITYGROUP_NAME);
		//Step 2
		EntityInterface pathAnnotatopnChild = testGroup.getEntityByName("PathAnnotationChild");
		AttributeInterface dateOnlyAtt = pathAnnotatopnChild
				.getAttributeByName("detectionDateChild");

		EntityManagerInterface entityManager = EntityManager.getInstance();

		Long recordId = null;
		try
		{
			// Step 3
			Map dataValue = new HashMap();
			String testDate = "11" + ProcessorConstants.DATE_SEPARATOR + "16"
					+ ProcessorConstants.DATE_SEPARATOR + "2020";

			dataValue.put(dateOnlyAtt, testDate);

			ContainerInterface containerInterface = (ContainerInterface) pathAnnotatopnChild
					.getContainerCollection().toArray()[0];

			recordId = DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);

			assertNotNull(
					"Record for test case testInsertFutureDateForDateOnlyFormat() inserted successfully.",
					recordId);

			// Step 4.
			dataValue = EntityManager.getInstance().getRecordById(pathAnnotatopnChild, recordId);
			assertEquals(testDate, dataValue.get(dateOnlyAtt));
		}
		catch (DynamicExtensionsValidationException e)
		{
			System.out.println("Unknown exception occured " + e.getMessage());
			//LOGGER.error(e.printStackTrace());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Insert data for dates with different date formats.
	 */
	public void testInsertDataForDateWithDifferentFormats()
	{
		// For Date Only Format && For Date - Time Format
		try
		{

			EntityGroupInterface testGroup = EntityGroupManager.getInstance().getEntityGroupByName(
					TEST_ENTITYGROUP_NAME);
			// Step 1. Retrieve the Chemotheropy entity in which "duration"
			// attribute have the Default value of 6;
			EntityInterface diagnosis = testGroup.getEntityByName("Diagnosis");
			AttributeInterface diagnosisAtt = diagnosis.getAttributeByName("diagnosis");
			AttributeInterface dateTimeAtt = diagnosis.getAttributeByName("diagnosisDate");
			AttributeInterface dateOnlyAtt = diagnosis.getAttributeByName("followupDiagnosisDate");

			// Step 2.
			Map dataValue = new HashMap();
			// dataValue.put(commentsAttributes, "this is not default comment");
			String testDateTime = "10" + ProcessorConstants.DATE_SEPARATOR + "08"
					+ ProcessorConstants.DATE_SEPARATOR + "1998 11:30";
			String testDateValue = "10" + ProcessorConstants.DATE_SEPARATOR + "08"
					+ ProcessorConstants.DATE_SEPARATOR + "1998";

			dataValue.put(diagnosisAtt, "true");
			dataValue.put(dateTimeAtt, testDateTime);
			dataValue.put(dateOnlyAtt, testDateValue);

			ContainerInterface containerInterface = (ContainerInterface) diagnosis
					.getContainerCollection().toArray()[0];
			Long recordId = DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);
			// Step 3.
			dataValue = EntityManager.getInstance().getRecordById(diagnosis, recordId);

			assertEquals(testDateTime, dataValue.get(dateTimeAtt));
			assertEquals(testDateValue, dataValue.get(dateOnlyAtt));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
			Logger.out.debug(e.getStackTrace());
		}

		// For Month Year && Year Only Format
		try
		{

			EntityGroupInterface testGroup = EntityGroupManager.getInstance().getEntityGroupByName(
					TEST_ENTITYGROUP_NAME);
			// Step 1. Retrieve the Chemotheropy entity in which "duration"
			// attribute have the Default value of 6;
			EntityInterface chemoTheropyEntity = testGroup.getEntityByName("Chemotherapy");
			AttributeInterface durationAtt = chemoTheropyEntity.getAttributeByName("duration");
			AttributeInterface monthYearAtt = chemoTheropyEntity.getAttributeByName("startDate");
			AttributeInterface yearOnlyAtt = chemoTheropyEntity.getAttributeByName("endDate");

			// Step 2.
			Map dataValue = new HashMap();
			// dataValue.put(commentsAttributes, "this is not default comment");
			String monthYearValue = "10" + ProcessorConstants.DATE_SEPARATOR + "1998";
			String yearValue = "1998";
			dataValue.put(durationAtt, "15");
			dataValue.put(monthYearAtt, monthYearValue);
			dataValue.put(yearOnlyAtt, yearValue);

			ContainerInterface containerInterface = (ContainerInterface) chemoTheropyEntity
					.getContainerCollection().toArray()[0];
			Long recordId = DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);

			// Step 3.
			dataValue = EntityManager.getInstance().getRecordById(chemoTheropyEntity, recordId);

			assertEquals("15", dataValue.get(durationAtt));
			assertEquals("15", dataValue.get(durationAtt));
			assertEquals(yearValue, dataValue.get(yearOnlyAtt));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
			Logger.out.debug(e.getStackTrace());
		}

	}

	/**
	 *
	 */
	public void testInsertDataForDate()
	{
		try
		{
			EntityGroupInterface testGroup = EntityGroupManager.getInstance().getEntityGroupByName(
					TEST_ENTITYGROUP_NAME);
			// Step 1. Retrieve the Chemotheropy entity in which "duration"
			// attribute have the Default value of 6;
			EntityInterface labTestEntity = testGroup.getEntityByName("LabTest");
			AttributeInterface quantResult = labTestEntity.getAttributeByName("quantResult");
			AttributeInterface testAgent = labTestEntity.getAttributeByName("testAgent");
			AttributeInterface testDate = labTestEntity.getAttributeByName("testDate");

			// Step 2. insert data
			Map dataValue = new HashMap();
			// dataValue.put(commentsAttributes, "this is not default comment");
			dataValue.put(quantResult, "15.90");
			dataValue.put(testAgent, "test Agent");
			String testDateValue = "10" + ProcessorConstants.DATE_SEPARATOR + "08"
					+ ProcessorConstants.DATE_SEPARATOR + "1998";
			dataValue.put(testDate, testDateValue);

			ContainerInterface containerInterface = (ContainerInterface) labTestEntity
					.getContainerCollection().toArray()[0];
			Long recordId = DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);

			// Step 3. Validate data
			dataValue = EntityManager.getInstance().getRecordById(labTestEntity, recordId);

			assertEquals("test Agent", dataValue.get(testAgent));
			assertEquals(testDateValue, dataValue.get(testDate));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
			Logger.out.debug(e.getStackTrace());
		}
	}

	/**
	 * Create a date attribute, specify range for the attribute, try to insert a
	 * date value which is out of the range. Data should not get inserted and a
	 * validation message with cause should be shown to the user.
	 */
	public void testInsertDataForDateWithRange()
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("test_date_range");

		// Step 1
		Entity entity = (Entity) createAndPopulateEntity();
		entity.setName("Stock Quote");

		EntityManagerInterface entityManager = EntityManager.getInstance();

		Long recordId = null;
		try
		{
			Attribute date = (Attribute) factory.createDateAttribute();
			date.setName("DateRange");
			((DateAttributeTypeInformation) date.getAttributeTypeInformation())
					.setFormat(ProcessorConstants.SQL_DATE_ONLY_FORMAT);

			RuleInterface dateRange = factory.createRule();
			dateRange.setName("dateRange");

			RuleParameterInterface minValue = factory.createRuleParameter();
			minValue.setName("min");
			minValue.setValue("11" + ProcessorConstants.DATE_SEPARATOR + "12"
					+ ProcessorConstants.DATE_SEPARATOR + "1982");
			dateRange.getRuleParameterCollection().add(minValue);

			date.getRuleCollection().add(dateRange);

			RuleParameterInterface maxValue = factory.createRuleParameter();
			maxValue.setName("max");
			maxValue.setValue("11" + ProcessorConstants.DATE_SEPARATOR + "15"
					+ ProcessorConstants.DATE_SEPARATOR + "1982");
			dateRange.getRuleParameterCollection().add(maxValue);

			entity.addAbstractAttribute(date);
			entityGroup.addEntity(entity);
			entity.setEntityGroup(entityGroup);

			// Step 2
			EntityInterface savedEntity = entityManager.persistEntity(entity);

			Map dataValue = new HashMap();
			dataValue.put("date", "11" + ProcessorConstants.DATE_SEPARATOR + "15"
					+ ProcessorConstants.DATE_SEPARATOR + "1982");

			Map<String, String> rulesMap = new HashMap<String, String>();
			rulesMap.put("min", "11" + ProcessorConstants.DATE_SEPARATOR + "12"
					+ ProcessorConstants.DATE_SEPARATOR + "1982");
			rulesMap.put("max", "11" + ProcessorConstants.DATE_SEPARATOR + "15"
					+ ProcessorConstants.DATE_SEPARATOR + "1982");

			for (RuleInterface rule : date.getRuleCollection())
			{
				ValidatorRuleInterface validatorRule = ControlConfigurationsFactory.getInstance()
						.getValidatorRule(rule.getName());
				validatorRule.validate(date, "11" + ProcessorConstants.DATE_SEPARATOR + "16"
						+ ProcessorConstants.DATE_SEPARATOR + "1982", rulesMap, "LeapYear");
			}
			ContainerInterface containerInterface = (ContainerInterface) entity
					.getContainerCollection().toArray()[0];
			recordId = DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);

			assertEquals(recordId, null);
		}
		catch (DynamicExtensionsValidationException e)
		{
			System.out.println("Could not insert data....");
			System.out
					.println("Validation failed. Input date should be lesser than or equal to 11-15-1982");
			assertEquals("Validation failed", e.getMessage());
			assertEquals(recordId, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Create a date only attribute, create and set allowfuturedate rule to this
	 * attribute. And try to insert a future date, valueData should get
	 * inserted.
	 */
	public void testDataForFutureDateRule()
	{
		Long recordId = null;
		try
		{
			EntityGroupInterface testEntityGroup = EntityGroupManager.getInstance()
					.getEntityGroupByName(TEST_ENTITYGROUP_NAME);
			Entity pathAnnotationChildEntity = (Entity) testEntityGroup
					.getEntityByName("PathAnnotationChild");
			edu.common.dynamicextensions.domain.Attribute detectionDateChild = (Attribute) pathAnnotationChildEntity
					.getAttributeByName("detectionDateChild");

			for (RuleInterface rule : detectionDateChild.getRuleCollection())
			{
				ValidatorRuleInterface validatorRule = ControlConfigurationsFactory.getInstance()
						.getValidatorRule(rule.getName());
				validatorRule.validate(detectionDateChild, "08" + ProcessorConstants.DATE_SEPARATOR
						+ "16" + ProcessorConstants.DATE_SEPARATOR + "2020", null, "Date");
			}

			Map dataValue = new HashMap();
			dataValue.put(detectionDateChild, "05" + ProcessorConstants.DATE_SEPARATOR + "05"
					+ ProcessorConstants.DATE_SEPARATOR + "2018");

			ContainerInterface containerInterface = (ContainerInterface) pathAnnotationChildEntity
					.getContainerCollection().toArray()[0];
			recordId = DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);

			dataValue = entityManager.getRecordById(pathAnnotationChildEntity, recordId);

			assertEquals("05" + ProcessorConstants.DATE_SEPARATOR + "05"
					+ ProcessorConstants.DATE_SEPARATOR + "2018", dataValue.get(detectionDateChild));
		}
		catch (DynamicExtensionsValidationException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Create a date only attribute, create and set allowfuturedate rule to this
	 * attribute. And try to insert a date less than or equal to today's date,
	 * validation error must be thrown
	 */
	public void testInvalidDateForFutureDateRule()
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("test_future_date");

		// Step 1
		Entity entity = (Entity) createAndPopulateEntity();
		entity.setName("ExpiryDate");

		EntityManagerInterface entityManager = EntityManager.getInstance();

		Long recordId = null;
		try
		{
			Attribute date = (Attribute) factory.createDateAttribute();
			date.setName("FutureDate");
			((DateAttributeTypeInformation) date.getAttributeTypeInformation())
					.setFormat(ProcessorConstants.DATE_FORMAT_OPTION_DATEONLY);

			RuleInterface allowfuturedate = factory.createRule();
			allowfuturedate.setName("allowfuturedate");
			allowfuturedate.setIsImplicitRule(false);
			date.getRuleCollection().add(allowfuturedate);

			entity.addAbstractAttribute(date);
			entityGroup.addEntity(entity);
			entity.setEntityGroup(entityGroup);

			// Step 2 save entity
			EntityInterface savedEntity = entityManager.persistEntity(entity);

			Map dataValue = new HashMap();
			dataValue.put(date, "11" + ProcessorConstants.DATE_SEPARATOR + "16"
					+ ProcessorConstants.DATE_SEPARATOR + "2000");

			for (RuleInterface rule : date.getRuleCollection())
			{
				ValidatorRuleInterface validatorRule = ControlConfigurationsFactory.getInstance()
						.getValidatorRule(rule.getName());
				validatorRule.validate(date, "11" + ProcessorConstants.DATE_SEPARATOR + "16"
						+ ProcessorConstants.DATE_SEPARATOR + "2000", null, "Date");
			}
			ContainerInterface containerInterface = (ContainerInterface) savedEntity
					.getContainerCollection().toArray()[0];
			recordId = DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);

		}
		catch (DynamicExtensionsValidationException e)
		{
			System.out.println("Could not insert data....");
			System.out
					.println("Validation failed. Input date should be greater than today's date when future date rule is used.");
			assertEquals("Validation failed", e.getMessage());
			assertEquals(recordId, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * fix for bug 4075
	 */
	public void testInsertValueWithQuote()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		Long recordId = null;
		try
		{
			// Step 1
			EntityGroupInterface testEntityGroup = EntityGroupManager.getInstance()
					.getEntityGroupByName(TEST_ENTITYGROUP_NAME);
			Entity labTestEntity = (Entity) testEntityGroup.getEntityByName("LabTest");
			edu.common.dynamicextensions.domain.Attribute qualResult = (Attribute) labTestEntity
					.getAttributeByName("qualResult");

			Map dataValue = new HashMap();
			dataValue.put(qualResult, "123'456");

			ContainerInterface containerInterface = (ContainerInterface) labTestEntity
					.getContainerCollection().toArray()[0];
			recordId = DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);

			assertTrue(recordId != null);
		}
		catch (Exception e)
		{
			System.out.println("Unknown exception occured " + e.getMessage());
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * PURPOSE: This method tests for creating value domain information object
	 *
	 * EXPECTED BEHAVIOUR: Attribute should be created successfully along with
	 * value domain information object. TEST CASE FLOW: 1.create test entity
	 * 2.create cadsr value domain information object
	 *
	 * @throws Exception
	 */
	public void testCreateEntityAttributeWithValueDomain()
	{
		EntityManagerInterface EntityManagerInterface = EntityManager.getInstance();

		try
		{
			DomainObjectFactory factory = DomainObjectFactory.getInstance();
			EntityGroupInterface entityGroup = factory.createEntityGroup();
			entityGroup.setName("test_" + new Double(Math.random()).toString());

			// Step 1
			EntityInterface specimen = createAndPopulateEntity();
			specimen.setName("specimen");
			specimen.setAbstract(true);
			AttributeInterface barcode = factory.createStringAttribute();
			barcode.setName("barcode");

			// Step 2
			CaDSRValueDomainInfoInterface caDSRValueDomainInfoInterface = factory
					.createCaDSRValueDomainInfo();
			caDSRValueDomainInfoInterface.setValueDomainType(ValueDomainType.ENUMERATED);
			caDSRValueDomainInfoInterface.setDatatype("java.lang.String");
			caDSRValueDomainInfoInterface.setName("java.lang.String");
			barcode.setCaDSRValueDomainInfo(caDSRValueDomainInfoInterface);

			SemanticPropertyInterface semanticPropertyInterface = factory.createSemanticProperty();
			semanticPropertyInterface.setConceptDefinition("definition");
			semanticPropertyInterface.setConceptCode("C1123");
			semanticPropertyInterface.setSequenceNumber(1);
			semanticPropertyInterface.setConceptPreferredName("term");
			semanticPropertyInterface.setConceptDefinitionSource("thesaurasName");

			barcode.addSemanticProperty(semanticPropertyInterface);

			specimen.addAbstractAttribute(barcode);

			AttributeInterface label = factory.createStringAttribute();
			label.setName("label");
			label.setPublicId("public id 1");
			specimen.addAbstractAttribute(label);

			EntityInterface tissueSpecimen = createAndPopulateEntity();
			tissueSpecimen.setParentEntity(specimen);
			tissueSpecimen.populateEntityForConstraintProperties(false);
			entityGroup.addEntity(tissueSpecimen);
			tissueSpecimen.setEntityGroup(entityGroup);
			entityGroup.addEntity(specimen);
			specimen.setEntityGroup(entityGroup);
			specimen = EntityManagerInterface.persistEntity(specimen);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 *
	 *
	 */
	public void testGetAllGroupBeans()
	{
		try
		{
			EntityManager.getInstance().getAllEntityGroupBeans();
		}
		catch (Exception e)
		{
			fail();
			e.printStackTrace();
		}
	}

	/**
	 * Create one entity group, add one entity to it and save it.
	 *
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public void testSaveEntityGroupWithIDGenerator() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		EntityGroupManagerInterface EntityManager = EntityGroupManager.getInstance();
		EntityGroupInterface entityGroup = DomainObjectFactory.getInstance().createEntityGroup();
		EntityInterface e1 = new MockEntityManager().initializeEntity3();
		entityGroup.addEntity(e1);
		e1.setEntityGroup(entityGroup);
		try
		{
			EntityManager.persistEntityGroup(entityGroup);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test case to create entity with one attribute having permissible values.
	 */
	public void testCreateEntityWithAttributeHavingPermissibleValues()
	{
		try
		{
			DomainObjectFactory factory = DomainObjectFactory.getInstance();

			EntityGroupInterface entityGroup = factory.createEntityGroup();
			entityGroup.setName("Entity Group 1");

			// Create vitals entity.
			EntityInterface vitalsEntity = createAndPopulateEntity();
			vitalsEntity.setName("vitalsEntity");

			AttributeInterface bmi = factory.createStringAttribute();
			bmi.setName("BMI");
			((StringAttributeTypeInformation) bmi.getAttributeTypeInformation()).setSize(40);

			vitalsEntity.addAbstractAttribute(bmi);

			// Add permissible values.
			UserDefinedDEInterface userDefinedDE = factory.createUserDefinedDE();

			PermissibleValueInterface permissibleValue1 = factory.createStringValue();
			((StringValue) permissibleValue1).setValue("Underweight: 18.5 or below");

			PermissibleValueInterface permissibleValue2 = factory.createStringValue();
			((StringValue) permissibleValue2).setValue("Healthy Weight: 18.5 - 24.9");

			PermissibleValueInterface permissibleValue3 = factory.createStringValue();
			((StringValue) permissibleValue3).setValue("Overweight: 25.0 - 29.9");

			PermissibleValueInterface permissibleValue4 = factory.createStringValue();
			((StringValue) permissibleValue4).setValue("Obese: 30.0 and above");

			userDefinedDE.addPermissibleValue(permissibleValue1);
			userDefinedDE.addPermissibleValue(permissibleValue2);
			userDefinedDE.addPermissibleValue(permissibleValue3);
			userDefinedDE.addPermissibleValue(permissibleValue4);

			StringAttributeTypeInformation bmiTypeInfo = (StringAttributeTypeInformation) bmi
					.getAttributeTypeInformation();
			bmiTypeInfo.setDataElement(userDefinedDE);
			bmiTypeInfo.setDefaultValue(permissibleValue2);

			entityGroup.addEntity(vitalsEntity);

			EntityGroupManager.getInstance().persistEntityGroup(entityGroup);
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * this test cases is not working properly in case of decimal attributes
	 * because of bug in mysql please refrer :
	 * http://bugs.mysql.com/bug.php?id=9528 Thus this test case will always be
	 * success on MYSQL unique rule
	 */
	public void testUniqueRule()
	{
		String appName = DynamicExtensionDAO.getInstance().getAppName();
		String dbType = DAOConfigFactory.getInstance().getDAOFactory(appName).getDataBaseType();

		// this test case is failing for mysql db.
		// please refer http://bugs.mysql.com/bug.php?id=9528
		if (!dbType.equalsIgnoreCase("mysql"))
		{
			Long recordId2 = null;
			try
			{
				EntityGroupInterface testEntityGroup = EntityGroupManager.getInstance()
						.getEntityGroupByName(TEST_ENTITYGROUP_NAME);
				Entity phyContactInfoEntity = (Entity) testEntityGroup
						.getEntityByName("PhyContactInfo");
				edu.common.dynamicextensions.domain.Attribute phoneNumber = (Attribute) phyContactInfoEntity
						.getAttributeByName("phoneNumber");

				Map dataValue = new HashMap();
				dataValue.put(phoneNumber, "123456789");

				ContainerInterface containerInterface = (ContainerInterface) phyContactInfoEntity
						.getContainerCollection().toArray()[0];
				Long recordId1 = DynamicExtensionsUtility.insertDataUtility(null,
						containerInterface, dataValue);

				dataValue = entityManager.getRecordById(phyContactInfoEntity, recordId1);

				for (RuleInterface rule : phoneNumber.getRuleCollection())
				{
					ValidatorRuleInterface validatorRule = ControlConfigurationsFactory
							.getInstance().getValidatorRule(rule.getName());
					validatorRule.validate(phoneNumber, "123456789", null, "Phone Number");
				}

				Map dataValue2 = new HashMap();
				dataValue2.put(phoneNumber, "1234567890");

				recordId2 = DynamicExtensionsUtility.editDataUtility(recordId1, containerInterface,dataValue2);

				assertNull("No new record should be added.",recordId2);
			}
			catch (DynamicExtensionsValidationException e)
			{
				System.out.println("Could not insert data....");
				System.out.println("Validation failed. Input data should be unique");
                assertNull("No new record should be added.",recordId2);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				fail("testUniqueRule() : unkonwn exception occured " + e.getMessage());
			}
		}
	}

	/**
	 * numeric data types implicit rule.
	 */
	public void testImplicitRuleForFloatDataType()
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("NumberValidations");

		// Step 1
		Entity entity = (Entity) createAndPopulateEntity();
		entity.setName("Numeric");
		EntityManagerInterface entityManager = EntityManager.getInstance();

		Long recordId = null;

		try
		{
			Attribute floatTypeAttribute = (Attribute) factory.createFloatAttribute();
			floatTypeAttribute.setName("floatTypeAttribute");

			RuleInterface rule1 = factory.createRule();
			rule1.setName("number");
			rule1.setIsImplicitRule(true);
			floatTypeAttribute.getRuleCollection().add(rule1);

			entity.addAbstractAttribute(floatTypeAttribute);
			entityGroup.addEntity(entity);
			entity.setEntityGroup(entityGroup);

			// Step 2
			EntityInterface savedEntity = entityManager.persistEntity(entity);

			String incorrectValue = "ABC";

			Map dataValue = new HashMap();
			dataValue.put(floatTypeAttribute, incorrectValue);

			for (RuleInterface rule : floatTypeAttribute.getRuleCollection())
			{
				ValidatorRuleInterface validatorRule = ControlConfigurationsFactory.getInstance()
						.getValidatorRule(rule.getName());
				validatorRule.validate(floatTypeAttribute, incorrectValue, null, floatTypeAttribute
						.getName());
			}
			ContainerInterface containerInterface = (ContainerInterface) savedEntity
					.getContainerCollection().toArray()[0];
			recordId = DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					dataValue);
			assertEquals(recordId, null);
		}
		catch (DynamicExtensionsValidationException e)
		{
			System.out.println("Could not insert data....");
			System.out.println(e.getMessage());
			assertEquals(recordId, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test duplicate form name validations.
	 */
	public void testDuplicateFormName()
	{
		EntityInterface mainForm = null;
		EntityInterface subForm = null;
		ContainerInterface mainFormContainer = null;
		ContainerInterface subFormContainer = null;
		AssociationInterface association = null;

		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityManagerInterface entityManager = EntityManager.getInstance();

		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("DuplicateFormsValidations");

		// Create an entity.
		mainForm = createAndPopulateEntity();
		mainForm.setName("mainForm");

		entityGroup.addEntity(mainForm);
		mainForm.setEntityGroup(entityGroup);

		mainFormContainer = factory.createContainer();
		mainFormContainer.setCaption("mainForm");

		mainFormContainer.setAbstractEntity(mainForm);
		mainForm.getContainerCollection().add(mainFormContainer);

		subForm = createAndPopulateEntity();
		subForm.setName("mainForm");

		subFormContainer = factory.createContainer();
		subFormContainer.setCaption("mainForm");

		subFormContainer.setAbstractEntity(subForm);
		subForm.getContainerCollection().add(subFormContainer);

		try
		{
			try
			{
				DynamicExtensionsUtility.checkIfEntityPreExists(entityGroup, subFormContainer,
						subFormContainer.getCaption(), mainFormContainer);

				// Association should not get created as sub form's name is same
				// as that of main form.
				association = factory.createAssociation();
				association.setTargetEntity(subForm);
				association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
				association.setName("mainForm-->subForm association");
				association.setSourceRole(getRole(AssociationType.CONTAINTMENT, mainForm.getName(),
						Cardinality.ONE, Cardinality.ONE));
				association.setTargetRole(getRole(AssociationType.CONTAINTMENT, subForm.getName(),
						Cardinality.ONE, Cardinality.MANY));
				ConstraintPropertiesInterface constraintProperties = DynamicExtensionsUtility
						.getConstraintPropertiesForAssociation(association);
				association.setConstraintProperties(constraintProperties);
				mainForm.addAssociation(association);
				DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);

				entityManager.persistEntity(mainForm);
			}
			catch (DynamicExtensionsApplicationException e)
			{
				assertEquals(e.getMessage(), "Duplicate form name within same entity group!");
				assertEquals(e.getErrorCode(),
						EntityManagerExceptionConstantsInterface.DYEXTN_A_019);
				assertEquals(mainForm.getId(), null);
				assertEquals(association, null);
			}

			try
			{
				entityManager.persistEntity(mainForm);

				DynamicExtensionsUtility.checkIfEntityPreExists(entityGroup, subFormContainer,
						subFormContainer.getCaption());

				// Entity group here must not contain sub form as its name is
				// same as that of main form.
				entityGroup.addEntity(subForm);
				subForm.setEntityGroup(entityGroup);
			}
			catch (DynamicExtensionsApplicationException e)
			{
				assertEquals(e.getMessage(), "Duplicate form name within same entity group!");
				assertEquals(e.getErrorCode(),
						EntityManagerExceptionConstantsInterface.DYEXTN_A_019);
				assertNotNull(mainForm.getId());
				assertEquals(mainForm.getAssociationCollection().size(), 0);
				assertNotNull(entityGroup.getEntityByName(subForm.getName()));
			}

			try
			{
				subForm.setName("subForm");
				subFormContainer.setCaption("subForm");

				DynamicExtensionsUtility.checkIfEntityPreExists(entityGroup, subFormContainer,
						subFormContainer.getCaption());

				// Since sub form name is different from that of main form now,
				// entity group must contain this sub form.
				entityGroup.addEntity(subForm);
				subForm.setEntityGroup(entityGroup);

				// Likewise, association should get created between main form
				// and sub form.
				association = factory.createAssociation();
				association.setTargetEntity(subForm);
				association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
				association.setName("mainForm-->subForm association");
				association.setSourceRole(getRole(AssociationType.CONTAINTMENT, mainForm.getName(),
						Cardinality.ONE, Cardinality.ONE));
				association.setTargetRole(getRole(AssociationType.CONTAINTMENT, subForm.getName(),
						Cardinality.ONE, Cardinality.MANY));
				mainForm.addAssociation(association);
				ConstraintPropertiesInterface constraintProperties = DynamicExtensionsUtility
						.getConstraintPropertiesForAssociation(association);
				association.setConstraintProperties(constraintProperties);

				entityManager.persistEntity(mainForm);

				// sub form should now be a persistent object, and the main
				// form's association
				// collection must now contain an association whose target
				// entity is sub form.
				assertNotNull(mainForm.getId());
				assertNotNull(subForm.getId());
				assertNotNull(association);
				assertNotNull(association.getId());
				assertNotNull(entityGroup.getEntityByName(subForm.getName()));
				assertNotSame(mainForm.getAssociationCollection().size(), 0);
			}
			catch (DynamicExtensionsApplicationException e)
			{
				e.printStackTrace();
				fail();
			}
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * This test case will try to insert the data for all the entities present
	 * in the test model .
	 *
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 *
	 */
	public void testInsertDataForAllEntities() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		Map<EntityInterface, Exception> failedEntityVsException = new HashMap<EntityInterface, Exception>();
		EntityGroupInterface testModel = EntityGroupManager.getInstance().getEntityGroupByName(
				TEST_ENTITYGROUP_NAME);
		EntityManagerInterface entityManager = EntityManager.getInstance();
		for (ContainerInterface container : testModel.getMainContainerCollection())
		{
			EntityInterface entity = (EntityInterface) container.getAbstractEntity();
			try
			{
				System.out.println("Inserting record for entity " + entity);
				Map<BaseAbstractAttributeInterface, Object> dataValueMap = mapGenerator
						.createDataValueMapForEntity(entity, 0);
				Map map = dataValueMap;
				List<String> errorList = new ArrayList<String>();
				ValidatorUtil.validateEntity(dataValueMap, errorList, container, true);
				ContainerInterface containerInterface = (ContainerInterface) entity
						.getContainerCollection().toArray()[0];
				Long recordId = DynamicExtensionsUtility.insertDataUtility(null,
						containerInterface, map);

				System.out.println("Record inserted succesfully for entity " + entity
						+ " recordId " + recordId);
			}
			catch (Exception e)
			{
				System.out.println("Record Insertion failed for  entity " + entity.getName());
				failedEntityVsException.put(entity, e);
			}
		}
		printFailedCategoryReport(failedEntityVsException, "Record Insertion failed for  entity ");
	}

	private void printFailedCategoryReport(Map<EntityInterface, Exception> failedEntityVsException,
			String message)
	{
		for (Entry<EntityInterface, Exception> entryObject : failedEntityVsException.entrySet())
		{
			EntityInterface category = entryObject.getKey();
			Exception exception = entryObject.getValue();
			System.out.println(message + category.getName());
			System.out.println("Exception :");
			exception.printStackTrace();
		}
		if (!failedEntityVsException.isEmpty())
		{
			fail();
		}

	}

	/**
	 * This test case will try to insert the data for all the entities present
	 * in the test model .
	 *
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 *
	 */
	public void testEditDataForAllEntities() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		Map<EntityInterface, Exception> failedEntityVsException = new HashMap<EntityInterface, Exception>();
		EntityGroupInterface testModel = EntityGroupManager.getInstance().getEntityGroupByName(
				TEST_ENTITYGROUP_NAME);
		for (ContainerInterface container : testModel.getMainContainerCollection())
		{
			EntityInterface entity = (EntityInterface) container.getAbstractEntity();
			try
			{
                System.out.println("--------------------------------------------------");
				System.out.println("Inserting record for " + entity.getName());
				Map<BaseAbstractAttributeInterface, Object> dataValueMap = mapGenerator
						.createDataValueMapForEntity(entity, 0);
				ContainerInterface containerInterface = (ContainerInterface) entity
						.getContainerCollection().toArray()[0];
				Long recordId = DynamicExtensionsUtility.insertDataUtility(null,
						containerInterface, dataValueMap);
				System.out.println("Record inserted succesfully for " + entity + " recordId "
						+ recordId);
				Map editedDataValueMap = entityManager.getRecordById(entity, recordId);
                Long editRecordId = DynamicExtensionsUtility.insertDataUtility(recordId,
                        containerInterface, editedDataValueMap);
//				entityManager.editData(entity, editedDataValueMap, recordId, null, null);
				System.out.println("Record Edited Successfully for " + entity + " recordId "
						+ editRecordId);
			}
			catch (Exception e)
			{
				System.out.println("Record Insertion/Updation failed for  entity " + entity.getName());
				failedEntityVsException.put(entity, e);
			}
		}
		printFailedCategoryReport(failedEntityVsException, "Record Insertion failed for  entity ");

	}

	/**
	 * This test case will try to Validate all the rules applied on that
	 * attribute in the test model.
	 *
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 *
	 */
	public void testValidateDataForAllEntities() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		Map<EntityInterface, Exception> failedEntityVsException = new HashMap<EntityInterface, Exception>();
		boolean isValidationFailed = false;

		EntityGroupInterface testModel = EntityGroupManager.getInstance().getEntityGroupByName(
				TEST_ENTITYGROUP_NAME);
		EntityManager.getInstance();
		for (ContainerInterface container : testModel.getMainContainerCollection())
		{
			EntityInterface entity = (EntityInterface) container.getAbstractEntity();
			try
			{
				System.out.println("Validating record for entity " + entity);
				Map<BaseAbstractAttributeInterface, Object> dataValueMap = mapGenerator
						.createDataValueMapForEntity(entity, 0);
				List<String> errorList = new ArrayList<String>();
				ValidatorUtil.validateEntity(dataValueMap, errorList, container, true);
				if (errorList.isEmpty())
				{
					System.out.println("Record validated succesfully for entity " + entity);
				}
				else
				{
					System.out.println("Record validation failed for entity " + entity);
					for (String error : errorList)
					{
						System.out.println("error --> " + error);
					}
					isValidationFailed = true;
				}
			}
			catch (Exception e)
			{
				System.out.println("Record validation failed for Entity " + entity.getName());
				failedEntityVsException.put(entity, e);
			}
		}
		printFailedCategoryReport(failedEntityVsException, "Record validation failed for category ");
		if (isValidationFailed)
		{
			fail("Record validation failed for entity");
		}

	}

}