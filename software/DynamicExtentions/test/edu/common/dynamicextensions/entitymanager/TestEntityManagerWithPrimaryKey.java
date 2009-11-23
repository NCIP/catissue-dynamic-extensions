
package edu.common.dynamicextensions.entitymanager;

import java.util.Collection;
import java.util.List;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domain.EntityGroup;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.CiderXMIImporter;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationDirection;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.wustl.common.util.logger.Logger;

/**
 * test cases to verify primary key functionality in the entity
 * @author pavan_kalantri
 *
 */
public class TestEntityManagerWithPrimaryKey extends DynamicExtensionsBaseTestCase
{

	/**
	 * Purpose : To test weather it create the Entity with the attribute set as primary key
	 * 		step 1: create the entity & add the primary key attribute
	 * 		step 2: save entity and verify the data
	 */
	public void testCreateEntityWithPrimaryKey()
	{
		EntityGroupInterface entityGroup = DomainObjectFactory.getInstance().createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());
		//Step 1
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		Entity entity = (Entity) factory.createEntity();
		entity.setName("Stock Quote");
		entity.setEntityGroup(entityGroup);
		entityGroup.addEntity(entity);

		AttributeInterface longAtribute = factory.createLongAttribute();
		longAtribute.setName("primaryKey");
		longAtribute.setIsPrimaryKey(new Boolean(true));
		longAtribute.setIsNullable(new Boolean(false));
		entity.addAbstractAttribute(longAtribute);

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();

		try
		{
			//Step 2
			entityManagerInterface.persistEntity(entity);
			assertEquals(noOfDefaultColumns, getColumnCount("select * from "
					+ entity.getTableProperties().getName()));

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	* PURPOSE : To test the whether editing of an existing entity with primary key works properly or not
	* EXPECTED BEHAVIOR : Changes in the existing entity should be stored properly and the changes made to the
	* attributes of the entity should get reflected properly in the data tables.
	* TEST CASE FLOW :
	* 1. Create entity with some attributes as primary key
	* 2. Save entity using entity manager.
	* 3. Add an extra attribute to the entity as primary key
	* 4. Save the entity again. then add normal attribute again
	* 5. Check whether a column is newly added or not to the data table of the entity.
	*/
	public void testEditEntityAddPrimarykey()
	{
		EntityGroupInterface entityGroup = DomainObjectFactory.getInstance().createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());
		//Step 1
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		Entity entity = (Entity) factory.createEntity();
		entity.setName("Stock Quote");
		entity.setEntityGroup(entityGroup);
		entityGroup.addEntity(entity);

		AttributeInterface longAtribute = factory.createLongAttribute();
		longAtribute.setName("primaryKey");
		longAtribute.setIsPrimaryKey(new Boolean(true));
		longAtribute.setIsNullable(new Boolean(false));
		entity.addAbstractAttribute(longAtribute);

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();

		try
		{
			//Step 2
			EntityInterface savedEntity = entityManagerInterface.persistEntity(entity);

			//Step 3 Edit entity-- Add extra attribute
			AttributeInterface floatAtribute = factory.createFloatAttribute();
			floatAtribute.setName("Price");
			floatAtribute.setIsNullable(new Boolean(false));
			floatAtribute.setIsPrimaryKey(new Boolean(true));
			savedEntity.addAbstractAttribute(floatAtribute);
			//Step 4
			EntityInterface editedEntity = entityManagerInterface.persistEntity(savedEntity);

			//Edit entity
			AttributeInterface floatAtribute1 = factory.createFloatAttribute();
			floatAtribute1.setName("NewPrice");
			editedEntity.addAbstractAttribute(floatAtribute1);

			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ editedEntity.getTableProperties().getName()));

			//Step 5
			editedEntity = entityManagerInterface.persistEntity(editedEntity);
			assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "
					+ editedEntity.getTableProperties().getName()));

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
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Purpose : it will create two entities both without specifying any primary key tag
	 * 				and inheritance in between them and then verify that it does not add new column
	 * 				to the child entity for foreign key referencing to the Identifier in parent
	 * 		step 1 : create two Entities
	 *  	step 2 : save entities
	 *  	step 3: verify column counts
	 *
	 */
	public void testinheritanceBothWithIdPrimaryKey()
	{
		EntityGroup entityGroup = (EntityGroup) DomainObjectFactory.getInstance()
				.createEntityGroup();
		entityGroup.setName("testGroup" + new Double(Math.random()).toString());
		//Step 1
		Entity entity = (Entity) createAndPopulateEntity();
		entity.setName("test");
		entityGroup.addEntity(entity);
		entity.setEntityGroup(entityGroup);

		Entity childEntity = (Entity) createAndPopulateEntity();
		childEntity.setName("test");
		entityGroup.addEntity(childEntity);
		childEntity.setEntityGroup(entityGroup);
		childEntity.setParentEntity(entity);
		try
		{
			childEntity.populateEntityForConstraintProperties(true);
			//step 2
			EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
			entityManagerInterface.persistEntity(childEntity);

			//step 3
			assertEquals(noOfDefaultColumns, getColumnCount("select * from "
					+ entity.getTableProperties().getName()));
			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ childEntity.getTableProperties().getName()));
		}
		catch (Exception e)
		{
			fail();
			e.printStackTrace();
		}

	}

	/**
	 * Purpose : Create entity with composite primary key save it then remove one of the primary key
	 * 			 Then verify the column and data
	 *
	 * 		step 1: create Entity with 2 attributes as primary key
	 * 		step 2: save entity
	 * 		step 3: verify column count
	 * 		step 4: remove one of the primary key
	 * 		step 5: save entity again
	 * 		step 6: verify column count
	 */
	/*public void testEditEntityRemovePrimaryKeyAttribute()
	{
		EntityGroupInterface entityGroup = DomainObjectFactory.getInstance().createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());
		//Step 1
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		Entity entity = (Entity) factory.createEntity();
		entity.setName("Stock Quote");
		entity.setEntityGroup(entityGroup);
		entityGroup.addEntity(entity);

		AttributeInterface longAtribute = factory.createLongAttribute();
		longAtribute.setName("primaryKey");
		longAtribute.setIsPrimaryKey(new Boolean(true));
		longAtribute.setIsNullable(new Boolean(false));
		entity.addAbstractAttribute(longAtribute);

		AttributeInterface floatAtribute = factory.createFloatAttribute();
		floatAtribute.setName("Price");
		floatAtribute.setIsNullable(new Boolean(false));
		floatAtribute.setIsPrimaryKey(new Boolean(true));
		entity.addAbstractAttribute(floatAtribute);
		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		try
		{
			//step 2
			EntityInterface savedEntity = entityManagerInterface.persistEntity(entity);

			//step 3
			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ savedEntity.getTableProperties().getName()));

			//step 4
			AttributeInterface attribute = savedEntity.getAttributeByName("primaryKey");
			savedEntity.removeAttribute(attribute);

			//step 5
			savedEntity = entityManagerInterface.persistEntity(entity);
			//step 6
			assertEquals(noOfDefaultColumns, getColumnCount("select * from "
					+ savedEntity.getTableProperties().getName()));

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}

	}*/

	/**
	 * purpose: Import xmi which contains primary key tags.
	 * This test case is hanged in case of mysql & thus commented for temporary purpose.
	 */
	/*public void testXMIImportWithPrimaryKeyTag()
	{
		try
		{
			String[] args = {XMI_FILE_PATH + "test_primaryKey.xmi", CSV_FILE_PATH + "test_primaryKey.csv", "test_id","none","false"};
			try
			{
				CiderXMIImporter.main(args);

				EntityGroupInterface entityGroup = EntityGroupManager.getInstance()
						.getEntityGroupByName("test_primaryKey");
				EntityInterface entity = entityGroup.getEntityByName("child");
				assertEquals(noOfDefaultColumns + 3, getColumnCount("select * from "
						+ entity.getTableProperties().getName()));

				System.out
						.println("--------------- Test Case to import XMI successful ------------");

			}
			catch (Exception e)
			{
				fail();

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Exception occured");
		}
	}*/

	public void testXMIImportWithPrimaryKeyInDifferentEntity()
	{
		try
		{
			String[] args = {XMI_FILE_PATH + "testcider.xmi", CSV_FILE_PATH + "testcider.csv","test","none","false" };
			try
			{
				CiderXMIImporter.main(args);

				EntityGroupInterface entityGroup = EntityGroupManager.getInstance()
						.getEntityGroupByName("testcider");
				EntityInterface entity = entityGroup.getEntityByName("child1");
				//assertEquals(noOfDefaultColumns + 4, getColumnCount("select * from "
				//	+ entity.getTableProperties().getName()));

				if (entity.getConstraintProperties()
						.getSrcEntityConstraintKeyPropertiesCollection().size() != 1)
				{
					fail();
				}
				if (entity.getPrimaryKeyAttributeCollection().size() != 0)
				{
					fail();
				}
				Collection<AttributeInterface> att = entity.getEntityAttributes();

				EntityInterface patientEntity = entityGroup.getEntityByName("patient");
				if (patientEntity.getPrimaryKeyAttributeCollection().size() != 3)
				{
					fail();
				}
				AssociationInterface associaion = (AssociationInterface) patientEntity
						.getAbstractAttributeByName("one2manu");
				if (associaion.getConstraintProperties()
						.getTgtEntityConstraintKeyPropertiesCollection().size() != 3)
				{
					fail();
				}

				System.out
						.println("--------------- Test Case to import XMI successful ------------");

			}
			catch (Exception e)
			{
				e.printStackTrace();
				fail();

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Exception occured");
		}
	}

	public void testEditXMIImportWithPrimaryKeyInDifferentEntity()
	{
		try
		{
			String[] args = {EDITED_XMI_FILE_PATH + "testcider.xmi", CSV_FILE_PATH + "testcider.csv","test","none", "false"};
			try
			{
				CiderXMIImporter.main(args);

				EntityGroupInterface entityGroup = EntityGroupManager.getInstance()
						.getEntityGroupByName("testcider");
				EntityInterface entity = entityGroup.getEntityByName("child1");
				if (entity.getConstraintProperties()
						.getSrcEntityConstraintKeyPropertiesCollection().size() != 1)
				{
					fail();
				}
				EntityInterface patientEntity = entityGroup.getEntityByName("patient");
				if (patientEntity.getPrimaryKeyAttributeCollection().size() != 1)
				{
					fail();
				}
				AssociationInterface associaion = (AssociationInterface) patientEntity
						.getAbstractAttributeByName("one2manu");
				if (associaion.getConstraintProperties()
						.getTgtEntityConstraintKeyPropertiesCollection().size() != 1)
				{
					fail();
				}

				System.out
						.println("--------------- Test Case to import XMI successful ------------");

			}
			catch (Exception e)
			{
				fail();

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Exception occured");
		}
	}

	public void testXMIImportWithoutPrimaryKeyForAssociation()
	{
		try
		{
			String[] args = {XMI_FILE_PATH + "test_association_without_primaryKey.xmi",
					CSV_FILE_PATH + "test_association_without_primaryKey.csv","test_primaryKey","none","false"};
			try
			{
				CiderXMIImporter.main(args);

				EntityGroupInterface entityGroup = EntityGroupManager.getInstance()
						.getEntityGroupByName("test_association_without_primaryKey");
				EntityInterface entity = entityGroup.getEntityByName("Source");
				AssociationInterface associaion = (AssociationInterface) entity
						.getAbstractAttributeByName("src-tgt-bi");
				if (!(associaion.getConstraintProperties()
						.getSrcEntityConstraintKeyPropertiesCollection().isEmpty() && associaion
						.getConstraintProperties().getTgtEntityConstraintKeyPropertiesCollection()
						.isEmpty()))
				{
					fail();
				}

				System.out
						.println("--------------- Test Case to import XMI successful ------------");

			}
			catch (Exception e)
			{
				fail();

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Exception occured");
		}
	}

	/**
	 * purpose  : import the xmi which is already imported and modified to add a
	 * 				new attribute as a primary key
	 */
	public void testEditXMIImportToAddPrimaryKey()
	{

		try
		{

			String[] args = {EDITED_XMI_FILE_PATH + "test_primaryKey.xmi",
					CSV_FILE_PATH + "test_primaryKey.csv","test_id","none","false"};
			try
			{
				CiderXMIImporter.main(args);
				EntityGroupInterface entityGroup = EntityGroupManager.getInstance()
						.getEntityGroupByName("test_primaryKey");
				/*EntityInterface entity = entityGroup.getEntityByName("child");
				assertEquals(noOfDefaultColumns + 3, getColumnCount("select * from "
						+ entity.getTableProperties().getName()));*/
				System.out
						.println("--------------- Test Case to import XMI successful ------------");

			}
			catch (Exception e)
			{
				fail();

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Exception occured");
		}
	}

	/**
	 * Import xmi which contains primary key Tag
	 */
	public void testXMIImport1WithPrimaryKeyTag()
	{

		try
		{

			String[] args = {XMI_FILE_PATH + "test_primaryKey1.xmi", CSV_FILE_PATH + "test_primaryKey.csv","test_id", "none","false"};
			try
			{
				CiderXMIImporter.main(args);
				EntityGroupInterface entityGroup = EntityGroupManager.getInstance()
						.getEntityGroupByName("test_primaryKey1");
				/*EntityInterface entity = entityGroup.getEntityByName("child");
				assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "
						+ entity.getTableProperties().getName()));*/
				System.out
						.println("--------------- Test Case to import XMI successful ------------");

			}
			catch (Exception e)
			{
				fail();

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Exception occured");
		}
	}

	/**
	 * Import xmi which is already imported and is edited and which does not contains primary key tag
	 *
	 * Expected behaviour : should add id as primary key
	 */
	public void testEditXMIImport1RemoveAllPrimaryKeyAttribute()
	{

		try
		{
			String[] args = {EDITED_XMI_FILE_PATH + "test_primaryKey1.xmi",
					CSV_FILE_PATH + "test_primaryKey.csv","test_id","none","false"};
			try
			{
				CiderXMIImporter.main(args);
				EntityGroupInterface entityGroup = EntityGroupManager.getInstance()
						.getEntityGroupByName("test_primaryKey1");
			/*	EntityInterface entity = entityGroup.getEntityByName("child");
				assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "
						+ entity.getTableProperties().getName()));*/
				System.out
						.println("--------------- Test Case to import XMI successful ------------");

			}
			catch (Exception e)
			{
				fail();

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Exception occured");
		}
	}

	/**
	 * Import xmi which contains string attribute as primary key
	 * Expected Behaviour : should throw exception telling Invalid DataType
	 */
	public void testXMIImportInvalidPrimaryKeyDataType()
	{
		//String[] args = {"F:/SCGModel/scg1.xmi","edu.wustl.catissuecore.domain.PathAnnotation_SCG", "F:/SCGModel/scg.csv"};
			String[] args = {XMI_FILE_PATH+"test_primaryKey_InvalidDataType.xmi",
					CSV_FILE_PATH+"test_primaryKey.csv","test_id","none","false"};
			try
			{
				CiderXMIImporter.main(args);

				EntityGroupInterface entityGroup = EntityGroupManager.getInstance()
						.getEntityGroupByName("test_primaryKey_InvalidDataType");
				if(entityGroup!=null)
				{
					fail();
				}

			}
			catch (Exception e)
			{fail();
				System.out
						.println("--------------- Test Case to import XMI successful ------------");
			}

		}

	/**
	 * Import xmi which contains primary key tag as a attribute which is not present in the entity
	 * Expected Behaviour : should throw exception saying attribute not present
	 */
	public void testXMIImportInvalidPrimaryKeyAttributeTagName()
	{
		try
		{
			String[] args = {XMI_FILE_PATH + "test_primaryKey_InvalidAttribute.xmi",
					CSV_FILE_PATH + "test_primaryKey.csv","test_id","none","false"};
			CiderXMIImporter.main(args);

			EntityGroupInterface entityGroup = EntityGroupManager.getInstance()
					.getEntityGroupByName("test_primaryKey_InvalidAttribute");
			EntityInterface entity = entityGroup.getEntityByName("child");
			assertEquals(noOfDefaultColumns, getColumnCount("select * from "
					+ entity.getTableProperties().getName()));
			//Exception should occur
			fail();

		}
		catch (Exception e)
		{
			System.out.println("--------------- Test Case to import XMI successful ------------");

		}
	}


	/**
	 * Import xmi which contains primary key tag as a attribute which is not present in the entity
	 * Expected Behaviour : should throw exception saying attribute not present
	 */
	public void testXMIImportWithSrcPrimaryKeyAssociation()
	{
		try
		{

			String[] args = {XMI_FILE_PATH + "test_primaryKey_association.xmi",
					CSV_FILE_PATH + "test_primaryKey.csv","test_id","none","false"};

			CiderXMIImporter.main(args);
			EntityGroupInterface entityGroup = EntityGroupManager.getInstance()
					.getEntityGroupByName("test_primaryKey_association");
			/*EntityInterface entity = entityGroup.getEntityByName("child");
			assertEquals(noOfDefaultColumns + 3, getColumnCount("select * from "
					+ entity.getTableProperties().getName()));*/
			//Exception should occur

		}
		catch (Exception e)
		{
			fail();
			System.out.println("--------------- Test Case to import XMI successful ------------");

		}
	}

	/**
	 * Import xmi which contains primary key tag as a attribute which is not present in the entity
	 * Expected Behaviour : should throw exception saying attribute not present
	 */
	public void testEditXMIImportChangeSrcPrimaryKeyAssociation()
	{
		try
		{
			String[] args = {EDITED_XMI_FILE_PATH + "test_primaryKey_association.xmi",
					CSV_FILE_PATH + "test_primaryKey.csv","test_id","none","false"};
			CiderXMIImporter.main(args);

			EntityGroupInterface entityGroup = EntityGroupManager.getInstance()
					.getEntityGroupByName("test_primaryKey_association");
			/*EntityInterface entity = entityGroup.getEntityByName("child");
			assertEquals(noOfDefaultColumns + 3, getColumnCount("select * from "
					+ entity.getTableProperties().getName()));*/
			//Exception should occur

		}
		catch (Exception e)
		{
			fail();
			System.out.println("--------------- Test Case to import XMI successful ------------");

		}
	}

	/**
	 * Purpose : Create 2 entities parent with composite key and inheritance in them , then remove the one of the primary key
	 * 			 and save the entity again
	 *
	 * 		step 1: create specimen with composite key , create tissue specimen which inherite from specimen
	 * 		step 2: save entities
	 * 		step 3: verify column count
	 * 		step 4: remove one of the primary key form specimen
	 * 		step 5: save entities again
	 * 		step 6: verify column counts
	 */

	public void testInheritanceRemovePrimaryKeyFromParent()
	{
		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());
		try
		{
			//Step 1
			EntityInterface specimen = factory.createEntity();
			specimen.setName("specimen");
			specimen.setAbstract(true);
			specimen.setEntityGroup(entityGroup);
			entityGroup.addEntity(specimen);

			AttributeInterface doubleAttribute = factory.createDoubleAttribute();
			doubleAttribute.setName("primaryKey");
			doubleAttribute.setIsPrimaryKey(new Boolean(true));
			doubleAttribute.setIsNullable(new Boolean(false));
			specimen.addAbstractAttribute(doubleAttribute);
			specimen.addPrimaryKeyAttribute(doubleAttribute);

			AttributeInterface primaryKey3 = factory.createDoubleAttribute();
			primaryKey3.setName("primaryKey3");
			primaryKey3.setIsNullable(false);
			primaryKey3.setIsPrimaryKey(true);
			specimen.addAbstractAttribute(primaryKey3);
			specimen.addPrimaryKeyAttribute(primaryKey3);

			EntityInterface tissueSpecimen = createAndPopulateEntity();
			tissueSpecimen.setName("tissueSpecimen");

			AttributeInterface quantityInCellCount = factory.createIntegerAttribute();
			quantityInCellCount.setName("quantityInCellCount");
			tissueSpecimen.addAbstractAttribute(quantityInCellCount);
			tissueSpecimen.setParentEntity(specimen);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(tissueSpecimen, true);
			entityGroup.addEntity(tissueSpecimen);
			tissueSpecimen.setEntityGroup(entityGroup);
			// step 2
			EntityInterface savedTissueSpecimen = entityManagerInterface
					.persistEntity(tissueSpecimen);
			EntityInterface savedSpecimen = savedTissueSpecimen.getParentEntity();

			//step 3
			assertEquals(noOfDefaultColumns + 3, getColumnCount("select * from "
					+ savedTissueSpecimen.getTableProperties().getName()));

			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ savedSpecimen.getTableProperties().getName()));

			//step 4
			AttributeInterface savedPrimaryKey = savedSpecimen.getAttributeByName("primaryKey");
			savedPrimaryKey.setIsNullable(true);
			savedPrimaryKey.setIsPrimaryKey(false);

			List<AttributeInterface> compositeKeyColl = specimen.getPrimaryKeyAttributeCollection();
			for (AttributeInterface attr : compositeKeyColl)
			{
				if (attr.getName().equals(savedPrimaryKey.getName()))
				{
					compositeKeyColl.remove(savedPrimaryKey);
				}
			}
			savedSpecimen.addAbstractAttribute(savedPrimaryKey);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(savedTissueSpecimen,
					true);

			// step 5
			entityManagerInterface.persistEntity(savedTissueSpecimen);
			// step 6
			assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "
					+ savedTissueSpecimen.getTableProperties().getName()));
			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ savedSpecimen.getTableProperties().getName()));

		}
		catch (Exception e)
		{
			Logger.out.debug(DynamicExtensionsUtility.getStackTrace(e));
			e.printStackTrace();
			fail();
		}

	}

	/**
	 * Purpose is to test the self referencing of the entity with one attribute as primary Key.
	 * Scenario - user(*)------>(*)User
	 *                   creator
	 */
	public void testCreateEntityWithprimaryKeyAndSelfReferencingBidirectionManyToManyAssociation()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());

		// create user
		EntityInterface user = factory.createEntity();
		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		AttributeInterface primaryKey = factory.createDoubleAttribute();
		primaryKey.setIsPrimaryKey(true);
		user.addPrimaryKeyAttribute(primaryKey);
		primaryKey.setName("primaryKey");
		primaryKey.setName("user");
		user.addAbstractAttribute(primaryKey);

		// Associate user (*)------ >(1)user
		AssociationInterface association = factory.createAssociation();
		association.setEntity(user);
		try
		{
			association.setTargetEntity(user);
			association.setAssociationDirection(AssociationDirection.BI_DIRECTIONAL);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "subpi",
					Cardinality.ONE, Cardinality.MANY));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "pi", Cardinality.ONE,
					Cardinality.MANY));

			user.addAbstractAttribute(association);
			association.populateAssociationForConstraintProperties();
			entityGroup.addEntity(user);
			user.setEntityGroup(entityGroup);

			EntityInterface savedUser = entityManager.persistEntity(user);

			String tableName = user.getTableProperties().getName();

			assertNotNull(tableName);

			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from " + tableName));

			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ association.getConstraintProperties().getName()));
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

	//
	/**
	 * This test case test for removing an association bet 2 existing entities
	 * Before- SRC-DESTINATION
	 * After - BIDIRECTIONAL
	 */
	public void testdeleteAssociationAfterSave()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());

		// create user
		EntityInterface user = createAndPopulateEntity();
		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		// create study
		EntityInterface study = createAndPopulateEntity();
		AttributeInterface studyNameAttribute = factory.createStringAttribute();
		studyNameAttribute.setName("study name");
		study.setName("study");
		study.addAbstractAttribute(studyNameAttribute);
		try
		{
			// Associate user (1)------ >(*)study
			RoleInterface sourceRole = getRole(AssociationType.ASSOCIATION, "association1",
					Cardinality.ZERO, Cardinality.MANY);
			RoleInterface targetRole = getRole(AssociationType.ASSOCIATION, "association1",
					Cardinality.ZERO, Cardinality.ONE);
			AssociationInterface association = factory.createAssociation();
			association.setTargetEntity(study);
			association.setEntity(user);
			association.setName("prim");
			association.setSourceRole(sourceRole);
			association.setTargetRole(targetRole);
			association.setAssociationDirection(AssociationDirection.BI_DIRECTIONAL);
			association.populateAssociationForConstraintProperties();

			user.addAbstractAttribute(association);
			entityGroup.addEntity(user);
			user.setEntityGroup(entityGroup);
			entityGroup.addEntity(study);
			study.setEntityGroup(entityGroup);

			user = entityManager.persistEntity(user);

			assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "
					+ user.getTableProperties().getName()));

			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ study.getTableProperties().getName()));
			user.removeAssociation(association);

			user = entityManager.persistEntity(user);

		}
		catch (Exception e)
		{
			fail();
			e.printStackTrace();
		}
	}

	/**
	 *   To create 2 level inheritance
	 *
	 */
	public void testInheritanceTwoLevelWithPrimaryKey()
	{
		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());
		try
		{
			//Step 1
			EntityInterface specimen = factory.createEntity();
			specimen.setName("specimen");
			specimen.setAbstract(true);
			specimen.setEntityGroup(entityGroup);
			entityGroup.addEntity(specimen);

			AttributeInterface doubleAttribute = factory.createDoubleAttribute();
			doubleAttribute.setName("primaryKey");
			doubleAttribute.setIsPrimaryKey(new Boolean(true));
			doubleAttribute.setIsNullable(new Boolean(false));
			specimen.addAbstractAttribute(doubleAttribute);
			specimen.addPrimaryKeyAttribute(doubleAttribute);

			AttributeInterface primaryKey3 = factory.createDoubleAttribute();
			primaryKey3.setName("primaryKey3");
			primaryKey3.setIsNullable(false);
			primaryKey3.setIsPrimaryKey(true);
			specimen.addAbstractAttribute(primaryKey3);
			specimen.addPrimaryKeyAttribute(primaryKey3);

			EntityInterface tissueSpecimen = factory.createEntity();
			tissueSpecimen.setName("tissueSpecimen");

			AttributeInterface quantityInCellCount = factory.createIntegerAttribute();
			quantityInCellCount.setName("quantityInCellCount");
			quantityInCellCount.setIsPrimaryKey(new Boolean(true));
			quantityInCellCount.setIsNullable(new Boolean(false));
			tissueSpecimen.addAbstractAttribute(quantityInCellCount);
			tissueSpecimen.addPrimaryKeyAttribute(quantityInCellCount);

			tissueSpecimen.setParentEntity(specimen);

			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(tissueSpecimen, true);
			entityGroup.addEntity(tissueSpecimen);
			tissueSpecimen.setEntityGroup(entityGroup);

			EntityInterface advancedTissueSpecimen = createAndPopulateEntity();
			advancedTissueSpecimen.setName("AdvancedtissueSpecimen");
			entityGroup.addEntity(advancedTissueSpecimen);
			advancedTissueSpecimen.setEntityGroup(entityGroup);
			advancedTissueSpecimen.setParentEntity(tissueSpecimen);
			advancedTissueSpecimen.populateEntityForConstraintProperties(true);

			entityManagerInterface.persistEntity(advancedTissueSpecimen);
			// step 6
			assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "
					+ tissueSpecimen.getTableProperties().getName()));
			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ advancedTissueSpecimen.getTableProperties().getName()));

		}
		catch (Exception e)
		{
			Logger.out.debug(DynamicExtensionsUtility.getStackTrace(e));
			e.printStackTrace();
			fail();
		}
	}

	/**
	 *  PURPOSE: This method tests for metadata save of the inheritance when child is edited with edited parent.
	 *  and both have different primary key attribute other than identifier then set he parent of tissue specimen to null
	 *
	 *  EXPECTED BEHAVIOUR: All the hierarchy should get saved. Changes in the parent should get properly reflected
	 *  in the database. A new column is added in child for primary key of parent.
	 *  Appropriate changes in the data tables should occur without any exception for both
	 *  the entities.
	 *
	 *  TEST CASE FLOW: 1. Create Specimen with composite primary key
	 *  				2. Persist Specimen
	 *  				3. Create TissueSpecimen and set parent as Specimen.
	 *                  4. persist TissueSpecimen
	 *                  5. Add an attribute to specimen
	 *                  6. Add an attribute to tissue specimen.
	 *                  7. Persist tissue specimen.
	 *                  8. Verify that the data table's column count is increased by 1 in both the entities.
	 *                  9. set the parent of the tissue specimen to null
	 *                  10. verify column count
	 */

	public void testInheritanceSetParentNull()
	{

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());
		try
		{
			//Step 1
			EntityInterface specimen = factory.createEntity();
			specimen.setName("specimen");
			specimen.setAbstract(true);

			AttributeInterface doubleAttribute = factory.createDoubleAttribute();
			doubleAttribute.setName("primaryKey");
			doubleAttribute.setIsPrimaryKey(new Boolean(true));
			doubleAttribute.setIsNullable(new Boolean(false));
			specimen.addAbstractAttribute(doubleAttribute);
			specimen.addPrimaryKeyAttribute(doubleAttribute);

			AttributeInterface longAttribute = factory.createLongAttribute();
			longAttribute.setName("primaryKey2");
			longAttribute.setIsPrimaryKey(new Boolean(true));
			longAttribute.setIsNullable(new Boolean(false));
			specimen.addAbstractAttribute(longAttribute);
			specimen.addPrimaryKeyAttribute(longAttribute);

			AttributeInterface barcode = factory.createStringAttribute();
			barcode.setName("barcode");
			specimen.addAbstractAttribute(barcode);
			entityGroup.addEntity(specimen);
			specimen.setEntityGroup(entityGroup);
			//Step 2
			EntityInterface savedSpecimen = entityManagerInterface.persistEntity(specimen);

			//Step 3
			EntityInterface tissueSpecimen = createAndPopulateEntity();
			tissueSpecimen.setName("tissueSpecimen");

			AttributeInterface quantityInCellCount = factory.createIntegerAttribute();
			quantityInCellCount.setName("quantityInCellCount");
			tissueSpecimen.addAbstractAttribute(quantityInCellCount);
			tissueSpecimen.setParentEntity(savedSpecimen);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(tissueSpecimen, true);
			entityGroup.addEntity(tissueSpecimen);
			tissueSpecimen.setEntityGroup(entityGroup);
			//Step 4
			EntityInterface savedTissueSpecimen = entityManagerInterface
					.persistEntity(tissueSpecimen);
			assertEquals(savedTissueSpecimen.getParentEntity(), savedSpecimen);
			//Check for step 4

			assertEquals(noOfDefaultColumns + 3, getColumnCount("select * from "
					+ savedTissueSpecimen.getTableProperties().getName()));

			assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "
					+ savedSpecimen.getTableProperties().getName()));

			//Step 5

			//Step 6
			AttributeInterface tissueCode = factory.createStringAttribute();
			tissueCode.setName("Tissue code");
			savedTissueSpecimen.addAbstractAttribute(tissueCode);

			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(savedTissueSpecimen,
					true);
			//Step 7
			savedTissueSpecimen = entityManagerInterface.persistEntity(tissueSpecimen);
			savedSpecimen = entityManagerInterface.persistEntity(savedSpecimen);
			//Step 8

			assertEquals(noOfDefaultColumns + 4, getColumnCount("select * from "
					+ savedTissueSpecimen.getTableProperties().getName()));

			assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "
					+ savedTissueSpecimen.getParentEntity().getTableProperties().getName()));

			// Step 9

			AttributeInterface primaryKey3 = factory.createDoubleAttribute();
			primaryKey3.setName("primaryKey3");
			primaryKey3.setIsNullable(false);
			primaryKey3.setIsPrimaryKey(true);
			savedSpecimen.addAbstractAttribute(primaryKey3);
			savedSpecimen.addPrimaryKeyAttribute(primaryKey3);

			savedSpecimen.addAbstractAttribute(primaryKey3);
			savedTissueSpecimen.setParentEntity(null);
			savedTissueSpecimen.populateEntityForConstraintProperties(true);
			entityManagerInterface.persistEntity(savedTissueSpecimen);
			// Step 10
			assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "
					+ savedTissueSpecimen.getTableProperties().getName()));
			assertEquals(noOfDefaultColumns + 3, getColumnCount("select * from "
					+ savedSpecimen.getTableProperties().getName()));

		}
		catch (Exception e)
		{
			Logger.out.debug(DynamicExtensionsUtility.getStackTrace(e));
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * purpose: create two entities specimen without specifying any primary key attribute and tissue specimen with primary key attribute
	 * 			and tissue specimen inherite from specimen
	 *
	 * 		step 1: create two entities
	 * 		step 2: save enities
	 * 		step 3: verify columnCounts
	 *
	 */
	public void testInheritanceChildWithPrimaryKey()
	{

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());
		try
		{
			//Step 1
			EntityInterface specimen = createAndPopulateEntity();
			specimen.setName("specimen");
			specimen.setAbstract(true);
			entityGroup.addEntity(specimen);
			specimen.setEntityGroup(entityGroup);


			EntityInterface tissueSpecimen = factory.createEntity();
			tissueSpecimen.setName("tissueSpecimen");
			entityGroup.addEntity(tissueSpecimen);
			tissueSpecimen.setEntityGroup(entityGroup);
			AttributeInterface doubleAttribute = factory.createDoubleAttribute();
			doubleAttribute.setName("primaryKey");
			doubleAttribute.setIsPrimaryKey(new Boolean(true));
			doubleAttribute.setIsNullable(new Boolean(false));
			tissueSpecimen.addAbstractAttribute(doubleAttribute);


			AttributeInterface quantityInCellCount = factory.createIntegerAttribute();
			quantityInCellCount.setName("quantityInCellCount");
			tissueSpecimen.addAbstractAttribute(quantityInCellCount);
			tissueSpecimen.setParentEntity(specimen);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(tissueSpecimen, true);
			//step 2
			EntityInterface savedTissueSpecimen = entityManagerInterface
					.persistEntity(tissueSpecimen);
			//step 3

			assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "
					+ savedTissueSpecimen.getTableProperties().getName()));

			assertEquals(noOfDefaultColumns, getColumnCount("select * from "
					+ savedTissueSpecimen.getParentEntity().getTableProperties().getName()));
		}
		catch (Exception e)
		{
			Logger.out.debug(DynamicExtensionsUtility.getStackTrace(e));
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * purpose : Create 2 entities with primary keys specified
	 */
	public void testInheritanceBothWithPrimaryKey()
	{

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());
		try
		{
			//Step 1
			EntityInterface specimen = factory.createEntity();
			specimen.setName("specimen");
			specimen.setEntityGroup(entityGroup);
			entityGroup.addEntity(specimen);

			AttributeInterface longAttribute = factory.createLongAttribute();
			longAttribute.setName("primaryKey");
			longAttribute.setIsPrimaryKey(new Boolean(true));
			longAttribute.setIsNullable(new Boolean(false));
			specimen.addAbstractAttribute(longAttribute);
			specimen.addPrimaryKeyAttribute(longAttribute);

			EntityInterface tissueSpecimen = factory.createEntity();
			tissueSpecimen.setName("tissueSpecimen");
			tissueSpecimen.setEntityGroup(entityGroup);
			entityGroup.addEntity(tissueSpecimen);

			AttributeInterface doubleAttribute = factory.createDoubleAttribute();
			doubleAttribute.setName("primaryKey1");
			doubleAttribute.setIsPrimaryKey(new Boolean(true));
			doubleAttribute.setIsNullable(new Boolean(false));
			tissueSpecimen.addAbstractAttribute(doubleAttribute);
			tissueSpecimen.addPrimaryKeyAttribute(doubleAttribute);

			AttributeInterface quantityInCellCount = factory.createIntegerAttribute();
			quantityInCellCount.setName("quantityInCellCount");
			tissueSpecimen.addAbstractAttribute(quantityInCellCount);
			tissueSpecimen.setParentEntity(specimen);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(tissueSpecimen, true);

			EntityInterface savedTissueSpecimen = entityManagerInterface
					.persistEntity(tissueSpecimen);

			assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "
					+ savedTissueSpecimen.getTableProperties().getName()));

			assertEquals(noOfDefaultColumns, getColumnCount("select * from "
					+ savedTissueSpecimen.getParentEntity().getTableProperties().getName()));
		}
		catch (Exception e)
		{
			Logger.out.debug(DynamicExtensionsUtility.getStackTrace(e));
			e.printStackTrace();
			fail();
		}
	}

	/**
	 *	step 1: create entity user with on1 attribute as primary key
	 *	step 2: create study entity without specifying any primary key
	 *  step 3: Associate user (1)------ >(*)study
	 *  step 5: save entity group
	 *  step 6: verify column count
	 *
	 */
	/*public void testCreateEntityWithOneToManyAssociationWithSrcPrimaryKey()
	{
		EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());

		// step 1 :create user

		entityGroup.setName("User" + new Double(Math.random()).toString());
		EntityInterface user = factory.createEntity();
		AttributeInterface primaryKey = factory.createLongAttribute();
		primaryKey.setName("primaryKey");
		primaryKey.setIsNullable(false);
		primaryKey.setIsPrimaryKey(true);

		user.addAbstractAttribute(primaryKey);

		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user_name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		// step 2 :create study
		EntityInterface study = createAndPopulateEntity();

		Container container = new Container();
		container.setCaption("testcontainer");
		Collection<ContainerInterface> listOfContainers = new HashSet<ContainerInterface>();
		listOfContainers.add(container);

		study.setContainerCollection(listOfContainers);

		AttributeInterface studyNameAttribute = factory.createStringAttribute();
		studyNameAttribute.setName("study_name");
		study.setName("study");
		study.addAbstractAttribute(studyNameAttribute);

		//step 3:  Associate user (1)------ >(*)study
		AssociationInterface association = factory.createAssociation();

		association.setTargetEntity(study);
		association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
		association.setName("primaryInvestigator");
		association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
				Cardinality.ONE, Cardinality.ONE));
		association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study", Cardinality.ZERO,
				Cardinality.MANY));

		user.addAbstractAttribute(association);
		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);

		try
		{
			//entityManager.createEntity(study);
			entityGroup.addEntity(user);
			user.setEntityGroup(entityGroup);
			entityGroup.addEntity(study);
			study.setEntityGroup(entityGroup);
			//step 4 save group
			entityGroupManager.persistEntityGroup(entityGroup);
			// step 5: verify column count
			assertEquals(getColumnCount("select * from " + study.getTableProperties().getName()),
					noOfDefaultColumns + 2);
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

	}*/

	/**
	 *  step 1:create user
	 *  step 2: create study
	 *  step 3  Associate user (1)------ >(*)study
	 *  step 4: save group
	 *  step 5 : verify
	 *  step 6 : remove one primary key attribute from user
	 *   step 7 : save group again
	 *   step 8 :verify column count
	 *   step 9 : verify
	 */
	/*public void testCreateEntityWithOneToManyAssociationEditSrcPrimaryKeyRemoved()
	{
		EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());

		// step 1:create user

		entityGroup.setName("User" + new Double(Math.random()).toString());
		EntityInterface user = factory.createEntity();
		AttributeInterface primaryKey = factory.createLongAttribute();
		primaryKey.setName("primaryKey");
		primaryKey.setIsNullable(false);
		primaryKey.setIsPrimaryKey(true);

		user.addAbstractAttribute(primaryKey);

		AttributeInterface doubleAttribute = factory.createDoubleAttribute();
		doubleAttribute.setName("primaryKey1");
		doubleAttribute.setIsPrimaryKey(new Boolean(true));
		doubleAttribute.setIsNullable(new Boolean(false));
		user.addAbstractAttribute(doubleAttribute);

		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user_name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		//step 2: create study
		EntityInterface study = createAndPopulateEntity();

		Container container = new Container();
		container.setCaption("testcontainer");
		Collection<ContainerInterface> listOfContainers = new HashSet<ContainerInterface>();
		listOfContainers.add(container);

		study.setContainerCollection(listOfContainers);

		AttributeInterface studyNameAttribute = factory.createStringAttribute();
		studyNameAttribute.setName("study_name");
		study.setName("study");
		study.addAbstractAttribute(studyNameAttribute);

		//step 3  Associate user (1)------ >(*)study
		AssociationInterface association = factory.createAssociation();

		association.setTargetEntity(study);
		association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
		association.setName("primaryInvestigator");
		association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
				Cardinality.ONE, Cardinality.ONE));
		association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study", Cardinality.ZERO,
				Cardinality.MANY));

		user.addAbstractAttribute(association);
		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);

		try
		{
			//entityManager.createEntity(study);
			entityGroup.addEntity(user);
			user.setEntityGroup(entityGroup);
			entityGroup.addEntity(study);
			study.setEntityGroup(entityGroup);
			//step 4: save group
			EntityGroupInterface savedEntityGroup = entityGroupManager
					.persistEntityGroup(entityGroup);

			//step 5 : verify
			assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "
					+ user.getTableProperties().getName()));

			assertEquals(noOfDefaultColumns + 3, getColumnCount("select * from "
					+ study.getTableProperties().getName()));

			//step 6 : remove one primary key attribute from user
			EntityInterface savedUser = savedEntityGroup.getEntityByName("User");
			AttributeInterface savedPrimaryAttr = savedUser.getAttributeByName("primaryKey");
			savedUser.removeAttribute(savedPrimaryAttr);

			DynamicExtensionsUtility
					.getConstraintPropertiesForAssociation((AssociationInterface) savedUser
							.getAbstractAttributeByName("primaryInvestigator"));
			// step 7 : save group again
			entityGroupManager.persistEntityGroup(savedEntityGroup);

			//step 8 :verify column count
			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ user.getTableProperties().getName()));

			assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "
					+ study.getTableProperties().getName()));

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

	}*/

	/**
	 * This test case test for associating three entities with  many to one to one
	 * user contains different primary key study also contains other primary key
	 *
	 * User(*) ---- >(1)Study(1) ------>(1)institute
	 */
	public void testCreateEntityWithCascadeManyToOneAssociationWithPrimaryKey()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());

		// create user
		EntityInterface user = factory.createEntity();

		AttributeInterface primaryKey = factory.createLongAttribute();
		primaryKey.setName("primaryKey");
		primaryKey.setIsNullable(false);
		primaryKey.setIsPrimaryKey(true);

		user.addAbstractAttribute(primaryKey);
		user.addPrimaryKeyAttribute(primaryKey);

		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		// create study
		EntityInterface study = factory.createEntity();

		AttributeInterface primaryKey1 = factory.createDoubleAttribute();
		primaryKey1.setName("primaryKey1");
		primaryKey1.setIsNullable(false);
		primaryKey1.setIsPrimaryKey(true);

		study.addAbstractAttribute(primaryKey1);
		study.addPrimaryKeyAttribute(primaryKey1);

		AttributeInterface studyNameAttribute = factory.createStringAttribute();
		studyNameAttribute.setName("study name");
		study.setName("study");
		study.addAbstractAttribute(studyNameAttribute);

		// create institution
		EntityInterface institution = createAndPopulateEntity();
		AttributeInterface institutionNameAttribute = factory.createStringAttribute();
		institutionNameAttribute.setName("institution name");
		institution.setName("institution");
		institution.addAbstractAttribute(institutionNameAttribute);
		try
		{
			// Associate user (*)------ >(1)study
			AssociationInterface association = factory.createAssociation();

			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ZERO, Cardinality.MANY));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.ONE));

			user.addAbstractAttribute(association);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);

			// Associate study(1) ------> (1) institution
			AssociationInterface studInstAssociation = factory.createAssociation();

			studInstAssociation.setTargetEntity(institution);
			studInstAssociation.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			studInstAssociation.setName("studyLocation");
			studInstAssociation.setSourceRole(getRole(AssociationType.ASSOCIATION,
					"studyPerformed", Cardinality.ZERO, Cardinality.ONE));
			studInstAssociation.setTargetRole(getRole(AssociationType.ASSOCIATION, "studyLocation",
					Cardinality.ZERO, Cardinality.ONE));

			study.addAbstractAttribute(studInstAssociation);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(studInstAssociation);
			entityGroup.addEntity(user);
			user.setEntityGroup(entityGroup);
			entityGroup.addEntity(study);
			study.setEntityGroup(entityGroup);
			entityGroup.addEntity(institution);
			institution.setEntityGroup(entityGroup);

			//entityManager.createEntity(study);

			entityManager.persistEntity(user);

			assertEquals(getColumnCount("select * from " + user.getTableProperties().getName()),
					noOfDefaultColumns + 2);

			assertEquals(getColumnCount("select * from " + study.getTableProperties().getName()),
					noOfDefaultColumns + 1);

			assertEquals(getColumnCount("select * from "
					+ institution.getTableProperties().getName()), noOfDefaultColumns + 2);

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

}
