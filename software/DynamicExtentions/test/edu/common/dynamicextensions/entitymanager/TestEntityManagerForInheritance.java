
package edu.common.dynamicextensions.entitymanager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.util.logger.Logger;

/**
 * @author Rahul Ner
 * @author Vishvesh Mulay
 */
public class TestEntityManagerForInheritance extends DynamicExtensionsBaseTestCase
{

	/**
	 *  PURPOSE: This method tests for getAllAttributes method of the entity
	 *
	 *  EXPECTED BEHAVIOUR: All the attributes from the hierarchy should be returned
	 *
	 *  TEST CASE FLOW: 1. Create following hierarchy
	 *                  3. persist leaf nodes
	 *                  2. check no of AllAttributes for each node.
	 *
	 *                  Specimen (barcode and label)
	 *                    |
	 *                    |
	 *               tissueSpecimen (quantityInCellCount)
	 *                    |
	 *                    |
	 *            ----------------------
	 *            |                     |
	 *            |                     |
	 *    advanceTissueSpecimenA      advanceTissueSpecimenB
	 *     (newAttributeA)             (newAttributeB1 and newAttributeB2)
	 */

	public void testInheritanceGetAllAttributes()
	{

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());
		try
		{
			// Step 1
			EntityInterface specimen = createAndPopulateEntity();
			specimen.setName("specimen");
			specimen.setAbstract(true);
			AttributeInterface barcode = factory.createStringAttribute();
			barcode.setName("barcode");
			specimen.addAbstractAttribute(barcode);

			AttributeInterface label = factory.createStringAttribute();
			label.setName("label");
			specimen.addAbstractAttribute(label);
			entityGroup.addEntity(specimen);
			specimen.setEntityGroup(entityGroup);
			EntityInterface tissueSpecimen = createAndPopulateEntity();
			tissueSpecimen.setParentEntity(specimen);
			DynamicExtensionsUtility
					.getConstraintKeyPropertiesForInheritance(tissueSpecimen, false);

			specimen = entityManagerInterface.persistEntity(specimen);

			tissueSpecimen.setName("tissueSpecimen");
			AttributeInterface quantityInCellCount = factory.createIntegerAttribute();
			quantityInCellCount.setName("quantityInCellCount");
			tissueSpecimen.addAbstractAttribute(quantityInCellCount);
			entityGroup.addEntity(tissueSpecimen);
			tissueSpecimen.setEntityGroup(entityGroup);
			tissueSpecimen = entityManagerInterface.persistEntity(tissueSpecimen);
			EntityInterface advanceTissueSpecimenA = createAndPopulateEntity();
			advanceTissueSpecimenA.setParentEntity(tissueSpecimen);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(
					advanceTissueSpecimenA, false);
			advanceTissueSpecimenA.setName("advanceTissueSpecimenA");
			AttributeInterface newAttribute = factory.createIntegerAttribute();
			newAttribute.setName("newAttributeA");
			advanceTissueSpecimenA.addAbstractAttribute(newAttribute);
			entityGroup.addEntity(advanceTissueSpecimenA);
			advanceTissueSpecimenA.setEntityGroup(entityGroup);

			EntityInterface advanceTissueSpecimenB = createAndPopulateEntity();
			advanceTissueSpecimenB.setParentEntity(tissueSpecimen);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(
					advanceTissueSpecimenB, false);
			advanceTissueSpecimenB.setName("advanceTissueSpecimenB");
			AttributeInterface newAttributeB = factory.createIntegerAttribute();
			newAttributeB.setName("newAttributeB");
			advanceTissueSpecimenB.addAbstractAttribute(newAttributeB);
			AttributeInterface newAttributeB2 = factory.createIntegerAttribute();
			newAttributeB2.setName("newAttributeB2");
			advanceTissueSpecimenB.addAbstractAttribute(newAttributeB2);
			entityGroup.addEntity(advanceTissueSpecimenB);
			advanceTissueSpecimenB.setEntityGroup(entityGroup);

			// Step 2
			entityManagerInterface.persistEntity(advanceTissueSpecimenA);
			entityManagerInterface.persistEntity(advanceTissueSpecimenB);

			// Step 3

			Collection<AttributeInterface> specimenAttributes = specimen.getAllAttributes();
			//2 user attributes + 1 system attribute for id
			assertEquals(3, specimenAttributes.size());

			//2 child Attributes + 1 parent Attribute + 2 system generated attributes for id
			Collection<AttributeInterface> tissueSpecimenAttributes = tissueSpecimen
					.getAllAttributes();
			assertEquals(5, tissueSpecimenAttributes.size());

			Collection<AttributeInterface> advanceTissueSpecimenAAttributes = advanceTissueSpecimenA
					.getAllAttributes();
			//1 child attribute + 2 parent Attribute + 1 grand parent attribute + 3 system generated attributes
			assertEquals(7, advanceTissueSpecimenAAttributes.size());

			Collection<AttributeInterface> advanceTissueSpecimenBAttributes = advanceTissueSpecimenB
					.getAllAttributes();
			//2 child attribute + 2 parent Attribute + 1 grand parent attribute + 3 system generated attributes
			assertEquals(8, advanceTissueSpecimenBAttributes.size());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			Logger.out.debug(DynamicExtensionsUtility.getStackTrace(e));
			fail();
		}
	}

	/**
	 *  PURPOSE: This method tests for getAllChildrenEntities method of the entity
	 *
	 *  EXPECTED BEHAVIOUR: All the attributes from the hierarchy should be returned
	 *
	 *  TEST CASE FLOW: 1. Create following hierarchy
	 *                  3. persist leaf nodes
	 *                  2. check no of getAllChildrenEntities for base node.
	 *
	 *                    A
	 *                    |
	 *                    |
	 *             ---------------------
	 * 	           |             		|
	 *             |             		|
	 *             B		            C
	 *			   |        		    |
	 * 		  ------------- 	 -------------
	 *       |            |      |      |      |
	 *       D             E     F      g      h
	 */

	public void testGetAllChildrenEntities()
	{

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());
		try
		{
			// Step 1
			EntityInterface entityA = createAndPopulateEntity();
			entityA.setName("entityA");
			entityGroup.addEntity(entityA);
			entityA.setEntityGroup(entityGroup);

			EntityInterface entityB = createAndPopulateEntity();
			entityB.setName("entityB");
			entityB.setParentEntity(entityA);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(entityB, false);
			entityGroup.addEntity(entityB);
			entityB.setEntityGroup(entityGroup);

			EntityInterface entityC = createAndPopulateEntity();
			entityC.setName("entityC");
			entityC.setParentEntity(entityA);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(entityC, false);
			entityGroup.addEntity(entityC);
			entityC.setEntityGroup(entityGroup);

			EntityInterface entityD = createAndPopulateEntity();
			entityD.setName("entityD");
			entityD.setParentEntity(entityB);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(entityD, false);
			entityGroup.addEntity(entityD);
			entityD.setEntityGroup(entityGroup);

			EntityInterface entityE = createAndPopulateEntity();
			entityE.setName("entityE");
			entityE.setParentEntity(entityB);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(entityE, false);
			entityGroup.addEntity(entityE);
			entityE.setEntityGroup(entityGroup);

			EntityInterface entityF = createAndPopulateEntity();
			entityF.setName("entityF");
			entityF.setParentEntity(entityC);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(entityF, false);
			entityGroup.addEntity(entityF);
			entityF.setEntityGroup(entityGroup);

			EntityInterface entityG = createAndPopulateEntity();
			entityG.setName("entityG");
			entityG.setParentEntity(entityC);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(entityG, false);
			entityGroup.addEntity(entityG);
			entityG.setEntityGroup(entityGroup);

			EntityInterface entityH = createAndPopulateEntity();
			entityH.setName("entityH");
			entityH.setParentEntity(entityC);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(entityH, false);
			entityGroup.addEntity(entityH);
			entityH.setEntityGroup(entityGroup);

			entityManagerInterface.persistEntity(entityA);
			entityManagerInterface.persistEntity(entityB);
			entityC = entityManagerInterface.persistEntity(entityC);

			entityManagerInterface.persistEntity(entityD);
			entityManagerInterface.persistEntity(entityE);
			entityManagerInterface.persistEntity(entityF);
			entityManagerInterface.persistEntity(entityG);
			entityManagerInterface.persistEntity(entityH);

			Collection<EntityInterface> cChildren = entityManagerInterface
					.getChildrenEntities(entityC);
			assertEquals(3, cChildren.size());

			Collection<EntityInterface> hChildren = entityManagerInterface
					.getChildrenEntities(entityH);
			assertEquals(0, hChildren.size());

			entityA = entityManagerInterface.getEntityByIdentifier(entityA.getId().toString());
			entityB = entityManagerInterface.getEntityByIdentifier(entityB.getId().toString());
			entityC = entityManagerInterface.getEntityByIdentifier(entityC.getId().toString());
			entityD = entityManagerInterface.getEntityByIdentifier(entityD.getId().toString());
			entityE = entityManagerInterface.getEntityByIdentifier(entityE.getId().toString());

			assertEquals(2, entityManagerInterface.getChildrenEntities(entityA).size());
			assertEquals(2, entityManagerInterface.getChildrenEntities(entityB).size());
			assertEquals(3, entityManagerInterface.getChildrenEntities(entityC).size());
			assertEquals(0, entityManagerInterface.getChildrenEntities(entityD).size());
			assertEquals(0, entityManagerInterface.getChildrenEntities(entityE).size());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			Logger.out.debug(DynamicExtensionsUtility.getStackTrace(e));
			fail();
		}
	}

	/**
	 *  PURPOSE: This method tests for metadata save of the inheritance.
	 *
	 *  EXPECTED BEHAVIOUR: All the hierarchy should get saved
	 *
	 *  TEST CASE FLOW: 1. Create Specimen
	 *                  2. Create TissueSpecimen and set parent as Specimen
	 *                  3. persist TissueSpecimen
	 *                  4. Check if the parent of TissueSpecimen and children of Specimen
	 */

	public void testInheritanceEditMetadataCreateChildWithParent()
	{

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());
		try
		{
			EntityInterface specimen = createAndPopulateEntity();
			specimen.setName("specimen");
			specimen.setAbstract(true);
			AttributeInterface barcode = factory.createStringAttribute();
			barcode.setName("barcode");
			specimen.addAbstractAttribute(barcode);
			entityGroup.addEntity(specimen);
			specimen.setEntityGroup(entityGroup);

			specimen = entityManagerInterface.persistEntity(specimen);

			EntityInterface tissueSpecimen = createAndPopulateEntity();
			tissueSpecimen.setName("tissueSpecimen");
			AttributeInterface quantityInCellCount = factory.createIntegerAttribute();
			quantityInCellCount.setName("quantityInCellCount");
			tissueSpecimen.addAbstractAttribute(quantityInCellCount);
			entityGroup.addEntity(tissueSpecimen);
			tissueSpecimen.setEntityGroup(entityGroup);
			tissueSpecimen.setParentEntity(specimen);
			DynamicExtensionsUtility
					.getConstraintKeyPropertiesForInheritance(tissueSpecimen, false);

			EntityInterface savedTissueSpecimen = entityManagerInterface
					.persistEntity(tissueSpecimen);

			savedTissueSpecimen = entityManagerInterface.getEntityByIdentifier(savedTissueSpecimen
					.getId().toString());
			assertEquals(savedTissueSpecimen.getParentEntity(), specimen);

			EntityInterface savedSpecimen = entityManagerInterface.getEntityByIdentifier(specimen
					.getId());
			Collection childCollection = entityManagerInterface.getChildrenEntities(savedSpecimen);
			assertEquals(childCollection.size(), 1);
			childCollection.contains(savedTissueSpecimen);

			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ specimen.getTableProperties().getName()));

			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ tissueSpecimen.getTableProperties().getName()));

		}
		catch (Exception e)
		{
			Logger.out.debug(DynamicExtensionsUtility.getStackTrace(e));
			e.printStackTrace();
			fail();
		}
	}

	/**
	 *  PURPOSE: This method tests for metadata save of the inheritance when child is created with edited parent.
	 *
	 *  EXPECTED BEHAVIOUR: All the hierarchy should get saved. Changes in the parent should get properly reflected
	 *  in the database. Appropriate changes in the data tables should occur without any exception.
	 *
	 *  TEST CASE FLOW: 1. Create Specimen
	 *  				2. Persist Specimen
	 *  				3. Add an attribute to the Specimen.
	 *                  4. Create TissueSpecimen and set parent as Specimen
	 *                  5. persist TissueSpecimen
	 *                  6. Verify TissueSpecimen.
	 *                  7. Check if the changes done in the specimen are reflected or not.
	 */

	public void testInheritanceEditMetadataCreateChildWithEditedParent()
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
			AttributeInterface barcode = factory.createStringAttribute();
			barcode.setName("barcode");
			specimen.addAbstractAttribute(barcode);
			entityGroup.addEntity(specimen);
			specimen.setEntityGroup(entityGroup);
			//Step 2
			EntityInterface savedSpecimen = entityManagerInterface.persistEntity(specimen);
			//Checking step 2

			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ specimen.getTableProperties().getName()));
			//Step 3
			AttributeInterface label = factory.createStringAttribute();
			label.setName("label");
			savedSpecimen.addAbstractAttribute(label);
			//Step 4
			EntityInterface tissueSpecimen = createAndPopulateEntity();
			tissueSpecimen.setName("tissueSpecimen");
			AttributeInterface quantityInCellCount = factory.createIntegerAttribute();
			quantityInCellCount.setName("quantityInCellCount");
			tissueSpecimen.addAbstractAttribute(quantityInCellCount);
			entityGroup.addEntity(tissueSpecimen);
			tissueSpecimen.setEntityGroup(entityGroup);
			//Step 5
			tissueSpecimen.setParentEntity(savedSpecimen);
			DynamicExtensionsUtility
					.getConstraintKeyPropertiesForInheritance(tissueSpecimen, false);
			//Step 6
			EntityInterface savedTissueSpecimen = entityManagerInterface
					.persistEntity(tissueSpecimen);
			assertEquals(savedTissueSpecimen.getParentEntity(), savedSpecimen);
			//Step 7

			assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "
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
	 *  PURPOSE: This method tests for metadata save of the inheritance when child is edited with unsaved parent.
	 *
	 *  EXPECTED BEHAVIOUR: Application exception should get thrown stating that parent should get saved first before
	 *  saving the child object.
	 *
	 *
	 *  TEST CASE FLOW: 1. Create Specimen
	 *                  2. Create TissueSpecimen.
	 *                  3. persist TissueSpecimen
	 *                  4. Set specimen as parent of tissue specimen.
	 *                  5. Persist TissueSpecimen.
	 *                  6. Verify Application Exception is thrown or not.
	 */

	public void testInheritanceEditMetadataEditChildWithUnsavedParent()
	{

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());
		EntityInterface specimen = null;
		try
		{
			//Step 1
			specimen = createAndPopulateEntity();
			specimen.setName("specimen");
			specimen.setAbstract(true);
			AttributeInterface barcode = factory.createStringAttribute();
			barcode.setName("barcode");
			specimen.addAbstractAttribute(barcode);
			entityGroup.addEntity(specimen);
			specimen.setEntityGroup(entityGroup);
			//Step 2
			EntityInterface tissueSpecimen = createAndPopulateEntity();
			tissueSpecimen.setName("tissueSpecimen");
			AttributeInterface quantityInCellCount = factory.createIntegerAttribute();
			quantityInCellCount.setName("quantityInCellCount");
			tissueSpecimen.addAbstractAttribute(quantityInCellCount);
			entityGroup.addEntity(tissueSpecimen);
			tissueSpecimen.setEntityGroup(entityGroup);
			//Step 3
			EntityInterface savedTissueSpecimen = entityManagerInterface
					.persistEntity(tissueSpecimen);
			//Step 4
			savedTissueSpecimen.setParentEntity(specimen);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(savedTissueSpecimen,
					false);
			//Step 5
			savedTissueSpecimen = entityManagerInterface.persistEntity(tissueSpecimen);

		}
		catch (DynamicExtensionsApplicationException e1)
		{
			Logger.out.info("Application exception is expected to be thrown here");
			assertNull(specimen.getTableProperties());
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
	 *
	 *  EXPECTED BEHAVIOUR: All the hierarchy should get saved. Changes in the parent should get properly reflected
	 *  in the database. Appropriate changes in the data tables should occur without any exception for both
	 *  the entities.
	 *
	 *  TEST CASE FLOW: 1. Create Specimen
	 *  				2. Persist Specimen
	 *  				3. Create TissueSpecimen and set parent as Specimen.
	 *                  4. persist TissueSpecimen
	 *                  5. Add an attribute to specimen
	 *                  6. Add an attribute to tissue specimen.
	 *                  7. Persist tissue specimen.
	 *                  8. Verify that the data table's column count is increased by 1 in both the entities.
	 */

	public void testInheritanceEditMetadataEditChildWithEditedParent()
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
			DynamicExtensionsUtility
					.getConstraintKeyPropertiesForInheritance(tissueSpecimen, false);
			entityGroup.addEntity(tissueSpecimen);
			tissueSpecimen.setEntityGroup(entityGroup);
			//Step 4
			EntityInterface savedTissueSpecimen = entityManagerInterface
					.persistEntity(tissueSpecimen);
			assertEquals(savedTissueSpecimen.getParentEntity(), savedSpecimen);
			//Check for step 4

			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ savedTissueSpecimen.getTableProperties().getName()));

			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ savedSpecimen.getTableProperties().getName()));

			//Step 5
			AttributeInterface label = factory.createStringAttribute();
			label.setName("label");
			savedSpecimen.addAbstractAttribute(label);
			//Step 6
			AttributeInterface tissueCode = factory.createStringAttribute();
			tissueCode.setName("Tissue code");
			savedTissueSpecimen.addAbstractAttribute(tissueCode);

			//Step 7
			savedTissueSpecimen = entityManagerInterface.persistEntity(tissueSpecimen);
			//Step 8

			//assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "+ savedTissueSpecimen.getTableProperties().getName()));

			assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "
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
	 *  PURPOSE: This method tests for meta data save of the inheritance when parent is set null and data is  present
	 *  for the child.
	 *
	 *  EXPECTED BEHAVIOUR: since data is present, should not allow change in the parent.ApplicationExcpetion is expected
	 *
	 *  in the database. Appropriate changes in the data tables should occur without any exception.
	 *
	 *  TEST CASE FLOW: 1. Create Specimen
	 *  				2. Persist Specimen
	 *                  3. Create TissueSpecimen and set parent as Specimen
	 *                  4. persist TissueSpecimen
	 *                  5. Insert data for TissueSpecimen
	 *                  6. set parent of TissueSpecimen as null
	 *                  7. persist TissueSpecimen -- > exception should be thrown.
	 *
	 */

	public void testInheritanceEditMetadataForMakeParentNullWithData()
	{

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());
		try
		{
			//step 1
			EntityInterface specimen = createAndPopulateEntity();
			specimen.setName("specimen");
			specimen.setAbstract(true);
			AttributeInterface barcode = factory.createStringAttribute();
			barcode.setName("barcode");
			specimen.addAbstractAttribute(barcode);
			entityGroup.addEntity(specimen);
			specimen.setEntityGroup(entityGroup);
			//step 2
			specimen = entityManagerInterface.persistEntity(specimen);

			//step 3
			EntityInterface tissueSpecimen = createAndPopulateEntity();
			tissueSpecimen.setName("tissueSpecimen");
			AttributeInterface quantityInCellCount = factory.createIntegerAttribute();
			quantityInCellCount.setName("quantityInCellCount");
			tissueSpecimen.addAbstractAttribute(quantityInCellCount);
			entityGroup.addEntity(tissueSpecimen);
			tissueSpecimen.setEntityGroup(entityGroup);
			tissueSpecimen.setParentEntity(specimen);
			DynamicExtensionsUtility
					.getConstraintKeyPropertiesForInheritance(tissueSpecimen, false);

			entityManagerInterface.persistEntity(tissueSpecimen);

			//step 5
			Map dataValue = new HashMap();
			dataValue.put(barcode, "123456");
			dataValue.put(quantityInCellCount, "45");

			entityManagerInterface.insertData(tissueSpecimen, dataValue, null, null);

			//step 6
			tissueSpecimen.setParentEntity(null);
			DynamicExtensionsUtility
					.getConstraintKeyPropertiesForInheritance(tissueSpecimen, false);
			entityManagerInterface.persistEntity(tissueSpecimen);

			//step 7
			fail();
		}
		catch (DynamicExtensionsSystemException e)
		{
			assertTrue(true);
			Logger.out.info("Application exception is expected to be thrown here");
		}
		catch (Exception e)
		{
			Logger.out.debug(DynamicExtensionsUtility.getStackTrace(e));
			fail();
		}
	}

	/**
	 *  PURPOSE: This method tests for discriminator values of entity
	 *
	 *  EXPECTED BEHAVIOUR: specified values of discriminator column and value should be saved.
	 */

	public void testDiscriminator()
	{

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());
		try
		{
			// Step 1
			EntityInterface specimen = createAndPopulateEntity();
			specimen.setName("specimen");
			specimen.setAbstract(true);
			entityGroup.addEntity(specimen);
			specimen.setEntityGroup(entityGroup);

			EntityInterface tissueSpecimen = createAndPopulateEntity();
			tissueSpecimen.setName("tissueSpecimen");
			tissueSpecimen.setParentEntity(specimen);
			DynamicExtensionsUtility
					.getConstraintKeyPropertiesForInheritance(tissueSpecimen, false);
			tissueSpecimen.setDiscriminatorColumn("SPECIMEN_CLASS");
			tissueSpecimen.setDiscriminatorValue("Tissue");
			entityGroup.addEntity(tissueSpecimen);
			tissueSpecimen.setEntityGroup(entityGroup);

			EntityInterface cellSpecimen = createAndPopulateEntity();
			cellSpecimen.setName("cellSpecimen");
			cellSpecimen.setParentEntity(specimen);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(cellSpecimen, false);
			cellSpecimen.setDiscriminatorColumn("SPECIMEN_CLASS");
			cellSpecimen.setDiscriminatorValue("Cell");
			entityGroup.addEntity(cellSpecimen);
			cellSpecimen.setEntityGroup(entityGroup);

			specimen = entityManagerInterface.persistEntity(specimen);
			tissueSpecimen = entityManagerInterface.persistEntity(tissueSpecimen);
			cellSpecimen = entityManagerInterface.persistEntity(cellSpecimen);
			cellSpecimen = entityManagerInterface.persistEntity(cellSpecimen);

			cellSpecimen = entityManagerInterface.getEntityByIdentifier(cellSpecimen.getId());
			tissueSpecimen = entityManagerInterface.getEntityByIdentifier(tissueSpecimen.getId());

			assertEquals("SPECIMEN_CLASS", cellSpecimen.getDiscriminatorColumn());
			assertEquals("Cell", cellSpecimen.getDiscriminatorValue());

			assertEquals("SPECIMEN_CLASS", tissueSpecimen.getDiscriminatorColumn());
			assertEquals("Tissue", tissueSpecimen.getDiscriminatorValue());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			Logger.out.debug(DynamicExtensionsUtility.getStackTrace(e));
			fail();
		}
	}

	/**
	 * Test case for issue no 3693
	 */
	public void testSetParentInEditMode()
	{

		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupInterface entityGroup = factory.createEntityGroup();
		entityGroup.setName("test_" + new Double(Math.random()).toString());
		try
		{
			// Step 1
			EntityInterface specimen = createAndPopulateEntity();
			specimen.setName("specimen");
			entityGroup.addEntity(specimen);
			specimen.setEntityGroup(entityGroup);
			specimen = entityManagerInterface.persistEntity(specimen);

			EntityInterface tissueSpecimen = createAndPopulateEntity();
			tissueSpecimen.setName("tissueSpecimen");
			entityGroup.addEntity(tissueSpecimen);
			tissueSpecimen.setEntityGroup(entityGroup);
			tissueSpecimen = entityManagerInterface.persistEntity(tissueSpecimen);

			tissueSpecimen.setParentEntity(specimen);
			DynamicExtensionsUtility
					.getConstraintKeyPropertiesForInheritance(tissueSpecimen, false);
			tissueSpecimen = entityManagerInterface.persistEntity(tissueSpecimen);

			assertTrue(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}

	}

}
