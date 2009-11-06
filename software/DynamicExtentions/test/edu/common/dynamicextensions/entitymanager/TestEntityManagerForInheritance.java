
package edu.common.dynamicextensions.entitymanager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
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
	 *  PURPOSE: This method tests for inserting data for inheritance
	 *
	 *  EXPECTED BEHAVIOUR: parent data should be inserted before the child.
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

	public void testInsertDataForInheritance()
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
			specimen.setEntityGroup(entityGroup);
			entityGroup.addEntity(specimen);

			EntityInterface tissueSpecimen = createAndPopulateEntity();
			tissueSpecimen.setParentEntity(specimen);
			DynamicExtensionsUtility
					.getConstraintKeyPropertiesForInheritance(tissueSpecimen, false);
			tissueSpecimen.setEntityGroup(entityGroup);
			tissueSpecimen.setName("tissueSpecimen");
			entityGroup.addEntity(tissueSpecimen);

			specimen = entityManagerInterface.persistEntity(specimen);

			AttributeInterface quantityInCellCount = factory.createIntegerAttribute();
			quantityInCellCount.setName("quantityInCellCount");
			tissueSpecimen.addAbstractAttribute(quantityInCellCount);

			AttributeInterface arivalDate = factory.createDateAttribute();
			arivalDate.setName("arivalDate");
			tissueSpecimen.addAbstractAttribute(arivalDate);
			DateAttributeTypeInformation dateAttributeTypeInformation = new DateAttributeTypeInformation();
			dateAttributeTypeInformation.setFormat(ProcessorConstants.DATE_FORMAT_OPTION_DATEONLY);
			arivalDate.setAttributeTypeInformation(dateAttributeTypeInformation);
			tissueSpecimen = entityManagerInterface.persistEntity(tissueSpecimen);

			Map dataValue = new HashMap();
			dataValue.put(barcode, "123456");
			dataValue.put(label, "specimen parent label");
			dataValue.put(quantityInCellCount, "45");
			dataValue.put(arivalDate, "11"+ProcessorConstants.DATE_SEPARATOR+"12"+ProcessorConstants.DATE_SEPARATOR+"1982");

			Long recordId = entityManagerInterface.insertData(tissueSpecimen, dataValue);

			int columnValue = (Integer) executeQuery("select * from "
					+ specimen.getTableProperties().getName(), INT_TYPE, 2);
			assertEquals(1, columnValue);

			columnValue = (Integer) executeQuery("select * from "
					+ tissueSpecimen.getTableProperties().getName(), INT_TYPE, 2);
			assertEquals(1, columnValue);

			EntityInterface advanceTissueSpecimenA = createAndPopulateEntity();
			advanceTissueSpecimenA.setParentEntity(tissueSpecimen);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(
					advanceTissueSpecimenA, false);
			advanceTissueSpecimenA.setName("advanceTissueSpecimenA");

			AttributeInterface newAttribute = factory.createIntegerAttribute();
			newAttribute.setName("newAttributeA");
			advanceTissueSpecimenA.addAbstractAttribute(newAttribute);

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
			advanceTissueSpecimenA.setEntityGroup(entityGroup);
			entityGroup.addEntity(advanceTissueSpecimenA);

			advanceTissueSpecimenA = entityManagerInterface.persistEntity(advanceTissueSpecimenA);

			dataValue.clear();
			dataValue.put(barcode, "869");
			dataValue.put(label, "specimen parent label");
			dataValue.put(quantityInCellCount, "45");
			dataValue.put(arivalDate, "11"+ProcessorConstants.DATE_SEPARATOR+"12"+ProcessorConstants.DATE_SEPARATOR+"1982");
			dataValue.put(newAttribute, "12");

			recordId = entityManagerInterface.insertData(advanceTissueSpecimenA, dataValue);

			int rowCount = (Integer) executeQuery("select count(*) from "
					+ specimen.getTableProperties().getName(), INT_TYPE, 1);
			assertEquals(2, rowCount);

			rowCount = (Integer) executeQuery("select count(*) from "
					+ tissueSpecimen.getTableProperties().getName(), INT_TYPE, 1);
			assertEquals(2, rowCount);

			rowCount = (Integer) executeQuery("select count(*) from "
					+ advanceTissueSpecimenA.getTableProperties().getName(), INT_TYPE, 1);
			assertEquals(1, rowCount);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			Logger.out.debug(DynamicExtensionsUtility.getStackTrace(e));
			fail();
		}
	}

	/**
	 *  PURPOSE: This method tests for inserting data for inheritance where no values are specified for the parent
	 *
	 *  EXPECTED BEHAVIOUR: parent row  should be inserted with value for only identifier column.
	 *
	 *  TEST CASE FLOW: 1. Create following hierarchy
	 *                  2. persist leaf nodes
	 *
	 *                  Specimen (barcode and label)
	 *                    |
	 *                    |
	 *               tissueSpecimen (quantityInCellCount)
	 *
	 *               3. Insert value for tissueSpecimen with no values for specimen
	 *               4.  check that a row is inserted for specimen with only identifier
	 */

	public void testInsertDataForInheritanceParentNoData()
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

			EntityInterface tissueSpecimen = createAndPopulateEntity();
			tissueSpecimen.setParentEntity(specimen);
			DynamicExtensionsUtility
					.getConstraintKeyPropertiesForInheritance(tissueSpecimen, false);
			specimen.setEntityGroup(entityGroup);
			entityGroup.addEntity(specimen);
			tissueSpecimen.setEntityGroup(entityGroup);
			entityGroup.addEntity(tissueSpecimen);

			specimen = entityManagerInterface.persistEntity(specimen);

			tissueSpecimen.setName("tissueSpecimen");

			AttributeInterface quantityInCellCount = factory.createIntegerAttribute();
			quantityInCellCount.setName("quantityInCellCount");
			tissueSpecimen.addAbstractAttribute(quantityInCellCount);

			AttributeInterface arivalDate = factory.createDateAttribute();
			arivalDate.setName("arivalDate");
			tissueSpecimen.addAbstractAttribute(arivalDate);

			DateAttributeTypeInformation dateAttributeTypeInformation = new DateAttributeTypeInformation();
			dateAttributeTypeInformation.setFormat(ProcessorConstants.DATE_FORMAT_OPTION_DATEONLY);
			arivalDate.setAttributeTypeInformation(dateAttributeTypeInformation);

			tissueSpecimen = entityManagerInterface.persistEntity(tissueSpecimen);

			//step 2
			tissueSpecimen = entityManagerInterface.persistEntity(tissueSpecimen);

			Map dataValue = new HashMap();
			//dataValue.put(barcode, "123456");
			//dataValue.put(label, "specimen parent label");
			dataValue.put(quantityInCellCount, "45");
			dataValue.put(arivalDate, "11"+ProcessorConstants.DATE_SEPARATOR+"12"+ProcessorConstants.DATE_SEPARATOR+"1982");

			//step 3
			Long recordId = entityManagerInterface.insertData(tissueSpecimen, dataValue);

			//step 4
			int columnValue = (Integer) executeQuery("select * from "
					+ specimen.getTableProperties().getName(), INT_TYPE, 2);
			assertEquals(1, columnValue);

			columnValue = (Integer) executeQuery("select * from "
					+ tissueSpecimen.getTableProperties().getName(), INT_TYPE, 2);

			assertEquals(1, columnValue);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Logger.out.debug(DynamicExtensionsUtility.getStackTrace(e));
			fail();
		}
	}

	/**
	 *  PURPOSE: This method tests for retrieving data for inheritance
	 *
	 *  EXPECTED BEHAVIOUR: parent data should be inserted before the child.
	 *
	 *  TEST CASE FLOW: 1. Create following hierarchy
	 *                  2. persist leaf nodes
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
	 *
	 *        3. Insert data for tissueSpecimen
	 *        4. verify that values for specimen are also populated.
	 *
	 *        5. Insert data for advanceTissueSpecimenA
	 *        6. verify that values for specimen and tissueSpecimen are also populated.
	 *
	 *        7. Insert data for advanceTissueSpecimenB
	 *        8. verify that values for specimen and tissueSpecimen are also populated.
	 *
	 */

	public void testGetDataForInheritance()
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

			EntityInterface tissueSpecimen = createAndPopulateEntity();
			tissueSpecimen.setParentEntity(specimen);
			DynamicExtensionsUtility
					.getConstraintKeyPropertiesForInheritance(tissueSpecimen, false);
			entityGroup.addEntity(specimen);
			specimen.setEntityGroup(entityGroup);
			entityGroup.addEntity(tissueSpecimen);
			tissueSpecimen.setEntityGroup(entityGroup);
			specimen = entityManagerInterface.persistEntity(specimen);

			tissueSpecimen.setName("tissueSpecimen");

			AttributeInterface quantityInCellCount = factory.createIntegerAttribute();
			quantityInCellCount.setName("quantityInCellCount");
			tissueSpecimen.addAbstractAttribute(quantityInCellCount);

			AttributeInterface arivalDate = factory.createDateAttribute();
			arivalDate.setName("arivalDate");
			tissueSpecimen.addAbstractAttribute(arivalDate);

			DateAttributeTypeInformation dateAttributeTypeInformation = new DateAttributeTypeInformation();
			dateAttributeTypeInformation.setFormat(ProcessorConstants.DATE_FORMAT_OPTION_DATEONLY);
			arivalDate.setAttributeTypeInformation(dateAttributeTypeInformation);

			tissueSpecimen = entityManagerInterface.persistEntity(tissueSpecimen);
			//step 2
			tissueSpecimen = entityManagerInterface.persistEntity(tissueSpecimen);

			EntityInterface advanceTissueSpecimenA = createAndPopulateEntity();
			advanceTissueSpecimenA.setParentEntity(tissueSpecimen);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(
					advanceTissueSpecimenA, false);
			advanceTissueSpecimenA.setName("advanceTissueSpecimenA");
			entityGroup.addEntity(advanceTissueSpecimenA);
			advanceTissueSpecimenA.setEntityGroup(entityGroup);

			AttributeInterface newAttribute = factory.createIntegerAttribute();
			newAttribute.setName("newAttributeA");
			advanceTissueSpecimenA.addAbstractAttribute(newAttribute);

			EntityInterface advanceTissueSpecimenB = createAndPopulateEntity();
			advanceTissueSpecimenB.setParentEntity(tissueSpecimen);
			advanceTissueSpecimenB.setName("advanceTissueSpecimenB");
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(
					advanceTissueSpecimenB, false);
			entityGroup.addEntity(advanceTissueSpecimenB);
			advanceTissueSpecimenB.setEntityGroup(entityGroup);

			AttributeInterface newAttributeB = factory.createIntegerAttribute();
			newAttributeB.setName("newAttributeB");
			advanceTissueSpecimenB.addAbstractAttribute(newAttributeB);
			AttributeInterface newAttributeB2 = factory.createIntegerAttribute();
			newAttributeB2.setName("newAttributeB2");
			advanceTissueSpecimenB.addAbstractAttribute(newAttributeB2);

			advanceTissueSpecimenA = entityManagerInterface.persistEntity(advanceTissueSpecimenA);
			advanceTissueSpecimenB = entityManagerInterface.persistEntity(advanceTissueSpecimenB);

			/*
			 * Test getData method
			 */

			//step 3
			Map dataValue = new HashMap();
			dataValue.put(barcode, "123456");
			dataValue.put(label, "specimen parent label");
			dataValue.put(quantityInCellCount, "45");
			dataValue.put(arivalDate, "11"+ProcessorConstants.DATE_SEPARATOR+"12"+ProcessorConstants.DATE_SEPARATOR+"1982");

			Long recordId = entityManagerInterface.insertData(tissueSpecimen, dataValue);

			Map outputMap = entityManagerInterface.getRecordById(tissueSpecimen, recordId);

			//step 4
			assertEquals(4, outputMap.size());
			assertEquals("123456", outputMap.get(barcode));
			assertEquals("specimen parent label", outputMap.get(label));
			assertEquals("45", outputMap.get(quantityInCellCount));
			assertEquals("11"+ProcessorConstants.DATE_SEPARATOR+"12"+ProcessorConstants.DATE_SEPARATOR+"1982", outputMap.get(arivalDate));

			//step 5
			dataValue.clear();
			dataValue.put(barcode, "869");
			dataValue.put(label, "specimen parent label");
			dataValue.put(quantityInCellCount, "46");
			dataValue.put(arivalDate, "11"+ProcessorConstants.DATE_SEPARATOR+"11"+ProcessorConstants.DATE_SEPARATOR+"1982");
			dataValue.put(newAttribute, "12");

			recordId = entityManagerInterface.insertData(advanceTissueSpecimenA, dataValue);

			outputMap = entityManagerInterface.getRecordById(advanceTissueSpecimenA, recordId);
			//step 6
			assertEquals(5, outputMap.size());
			assertEquals("869", outputMap.get(barcode));
			assertEquals("specimen parent label", outputMap.get(label));
			assertEquals("46", outputMap.get(quantityInCellCount));
			assertEquals("11"+ProcessorConstants.DATE_SEPARATOR+"11"+ProcessorConstants.DATE_SEPARATOR+"1982", outputMap.get(arivalDate));

			//step 7
			dataValue.clear();
			dataValue.put(barcode, "1001");
			dataValue.put(label, "specimen parent label new");
			dataValue.put(quantityInCellCount, "411");
			dataValue.put(arivalDate, "01"+ProcessorConstants.DATE_SEPARATOR+"11"+ProcessorConstants.DATE_SEPARATOR+"1982");
			dataValue.put(newAttributeB, "40");
			dataValue.put(newAttributeB2, "41");

			recordId = entityManagerInterface.insertData(advanceTissueSpecimenB, dataValue);
			outputMap = entityManagerInterface.getRecordById(advanceTissueSpecimenB, recordId);
			//step 8
			assertEquals(6, outputMap.size());
			assertEquals("1001", outputMap.get(barcode));
			assertEquals("specimen parent label new", outputMap.get(label));
			assertEquals("411", outputMap.get(quantityInCellCount));
			assertEquals("01"+ProcessorConstants.DATE_SEPARATOR+"11"+ProcessorConstants.DATE_SEPARATOR+"1982", outputMap.get(arivalDate));
			assertEquals("40", outputMap.get(newAttributeB));
			assertEquals("41", outputMap.get(newAttributeB2));
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

			//step 4
			EntityInterface savedTissueSpecimen = entityManagerInterface
					.persistEntity(tissueSpecimen);

			//step 5
			Map dataValue = new HashMap();
			dataValue.put(barcode, "123456");
			dataValue.put(quantityInCellCount, "45");

			entityManagerInterface.insertData(tissueSpecimen, dataValue);

			//step 6
			tissueSpecimen.setParentEntity(null);
			DynamicExtensionsUtility
					.getConstraintKeyPropertiesForInheritance(tissueSpecimen, false);
			savedTissueSpecimen = entityManagerInterface.persistEntity(tissueSpecimen);

			//step 7
			fail();
		}
		catch (DynamicExtensionsApplicationException e)
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
	 *  PURPOSE: This method tests for meta data save of the inheritance when parent is set null and data is not present
	 *  for the child.
	 *
	 *  EXPECTED BEHAVIOUR: parent of the child should be set to null., since data is not present should not throw any exception.
	 *
	 *  in the database. Appropriate changes in the data tables should occur without any exception.
	 *
	 *  TEST CASE FLOW: 1. Create Specimen
	 *  				2. Persist Specimen
	 *                  3. Create TissueSpecimen and set parent as Specimen
	 *                  4. persist TissueSpecimen
	 *                  5. Verify TissueSpecimen.
	 *                  6. set apparent of TissueSpecimen as null
	 *                  7. verify this change reflect din database.
	 *
	 */

	public void testInheritanceEditMetadataForMakeParentNullNoData()
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

			//step 4
			EntityInterface savedTissueSpecimen = entityManagerInterface
					.persistEntity(tissueSpecimen);

			//step 5
			savedTissueSpecimen = entityManagerInterface.getEntityByIdentifier(savedTissueSpecimen
					.getId().toString());
			savedTissueSpecimen = entityManagerInterface.getEntityByIdentifier(savedTissueSpecimen
					.getId());
			assertEquals(savedTissueSpecimen.getParentEntity(), specimen);

			//step 6
			tissueSpecimen.setParentEntity(null);
			savedTissueSpecimen = entityManagerInterface.persistEntity(tissueSpecimen);

			//step 7
			assertTrue(true);
			savedTissueSpecimen = entityManagerInterface.getEntityByIdentifier(savedTissueSpecimen
					.getId());
			assertNull(savedTissueSpecimen.getParentEntity());

			Map dataValue = new HashMap();
			dataValue.put(quantityInCellCount, "45");

			entityManagerInterface.insertData(tissueSpecimen, dataValue);

			int rowCount = (Integer) executeQuery("select count(*) from "
					+ specimen.getTableProperties().getName(), INT_TYPE, 1);
			assertEquals(0, rowCount);

			rowCount = (Integer) executeQuery("select count(*) from "
					+ tissueSpecimen.getTableProperties().getName(), INT_TYPE, 1);
			assertEquals(1, rowCount);
		}
		catch (Exception e)
		{
			Logger.out.debug(DynamicExtensionsUtility.getStackTrace(e));
			fail();
		}
	}

	/**
	 *  PURPOSE: This method tests for metadata save of the inheritance when parent is changed and data is not present
	 *  for the child.
	 *
	 *  EXPECTED BEHAVIOUR: appropriate changes should be made,since data is not present should not throw any exception.
	 *
	 *  in the database. Appropriate changes in the data tables should occur without any exception.
	 *
	 *  TEST CASE FLOW: 1. Create Specimen
	 *  				2. Persist Specimen
	 *                  3. Create TissueSpecimen and set parent as Specimen
	 *                  4. persist TissueSpecimen
	 *                  5. Create NewSpecimen
	 *                  6. Persist NewSpecimen
	 *                  7. set parent of TissueSpecimen as NewSpecimen
	 *                  7. verify this change reflected in database.
	 */

	public void testInheritanceEditMetadataForChangeParentWithNoData()
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

			//step 4
			EntityInterface savedTissueSpecimen = entityManagerInterface
					.persistEntity(tissueSpecimen);

			//step 5
			EntityInterface newSpecimen = createAndPopulateEntity();
			newSpecimen.setName("newSpecimen");
			newSpecimen.setAbstract(true);
			AttributeInterface barcodeOfNewSpecimen = factory.createStringAttribute();
			barcodeOfNewSpecimen.setName("barcodeOfNewSpecimen");
			newSpecimen.addAbstractAttribute(barcodeOfNewSpecimen);
			entityGroup.addEntity(newSpecimen);
			newSpecimen.setEntityGroup(entityGroup);
			//step 6
			newSpecimen = entityManagerInterface.persistEntity(newSpecimen);

			//step 6
			tissueSpecimen.setParentEntity(newSpecimen);
			DynamicExtensionsUtility
					.getConstraintKeyPropertiesForInheritance(tissueSpecimen, false);
			savedTissueSpecimen = entityManagerInterface.persistEntity(tissueSpecimen);

			//step 7
			assertTrue(true);
			savedTissueSpecimen = entityManagerInterface.getEntityByIdentifier(savedTissueSpecimen
					.getId());
			assertEquals(savedTissueSpecimen.getParentEntity(), newSpecimen);

			Map dataValue = new HashMap();
			dataValue.put(barcodeOfNewSpecimen, "123456");
			dataValue.put(quantityInCellCount, "45");

			entityManagerInterface.insertData(tissueSpecimen, dataValue);

			int rowCount = (Integer) executeQuery("select count(*) from "
					+ tissueSpecimen.getTableProperties().getName(), INT_TYPE, 1);
			assertEquals(1, rowCount);

			rowCount = (Integer) executeQuery("select count(*) from "
					+ specimen.getTableProperties().getName(), INT_TYPE, 1);
			assertEquals(0, rowCount);

			rowCount = (Integer) executeQuery("select count(*) from "
					+ newSpecimen.getTableProperties().getName(), INT_TYPE, 1);
			assertEquals(1, rowCount);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			Logger.out.debug(DynamicExtensionsUtility.getStackTrace(e));
			fail();
		}
	}

	/**
	 *  PURPOSE: This method tests for inserting data for inheritance
	 *
	 *  EXPECTED BEHAVIOUR: parent data should be inserted before the child.
	 *
	 *  TEST CASE FLOW: 1. Create following hierarchy
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
	 *
	 *                   2. insert data for tissueSpecimen
	 *                   3. Edit Data for tissueSpecimen
	 *                   4. verify that same record is edited with proper values.
	 *                   5. insert data for advanceTissueSpecimenA
	 *                   6. Edit Data for advanceTissueSpecimenA
	 *                   7. verify that same record is edited with proper values.
	 */

	public void testEditDataForInheritance()
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
			entityGroup.addEntity(specimen);
			specimen.setEntityGroup(entityGroup);
			AttributeInterface label = factory.createStringAttribute();
			label.setName("label");
			specimen.addAbstractAttribute(label);

			EntityInterface tissueSpecimen = createAndPopulateEntity();
			tissueSpecimen.setParentEntity(specimen);
			DynamicExtensionsUtility
					.getConstraintKeyPropertiesForInheritance(tissueSpecimen, false);
			entityGroup.addEntity(tissueSpecimen);
			tissueSpecimen.setEntityGroup(entityGroup);
			specimen = entityManagerInterface.persistEntity(specimen);

			tissueSpecimen.setName("tissueSpecimen");

			AttributeInterface quantityInCellCount = factory.createIntegerAttribute();
			quantityInCellCount.setName("quantityInCellCount");
			tissueSpecimen.addAbstractAttribute(quantityInCellCount);

			AttributeInterface arivalDate = factory.createDateAttribute();
			arivalDate.setName("arivalDate");
			tissueSpecimen.addAbstractAttribute(arivalDate);

			DateAttributeTypeInformation dateAttributeTypeInformation = new DateAttributeTypeInformation();
			dateAttributeTypeInformation.setFormat(ProcessorConstants.DATE_FORMAT_OPTION_DATEONLY);
			arivalDate.setAttributeTypeInformation(dateAttributeTypeInformation);
			tissueSpecimen = entityManagerInterface.persistEntity(tissueSpecimen);

			tissueSpecimen = entityManagerInterface.persistEntity(tissueSpecimen);

			EntityInterface advanceTissueSpecimenA = createAndPopulateEntity();
			advanceTissueSpecimenA.setParentEntity(tissueSpecimen);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(
					advanceTissueSpecimenA, false);
			advanceTissueSpecimenA.setName("advanceTissueSpecimenA");
			entityGroup.addEntity(advanceTissueSpecimenA);
			advanceTissueSpecimenA.setEntityGroup(entityGroup);
			AttributeInterface newAttribute = factory.createIntegerAttribute();
			newAttribute.setName("newAttributeA");
			advanceTissueSpecimenA.addAbstractAttribute(newAttribute);

			EntityInterface advanceTissueSpecimenB = createAndPopulateEntity();
			advanceTissueSpecimenB.setParentEntity(tissueSpecimen);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(
					advanceTissueSpecimenB, false);
			advanceTissueSpecimenB.setName("advanceTissueSpecimenB");
			entityGroup.addEntity(advanceTissueSpecimenB);
			advanceTissueSpecimenB.setEntityGroup(entityGroup);

			AttributeInterface newAttributeB = factory.createIntegerAttribute();
			newAttributeB.setName("newAttributeB");
			advanceTissueSpecimenB.addAbstractAttribute(newAttributeB);
			AttributeInterface newAttributeB2 = factory.createIntegerAttribute();
			newAttributeB2.setName("newAttributeB2");
			advanceTissueSpecimenB.addAbstractAttribute(newAttributeB2);

			advanceTissueSpecimenA = entityManagerInterface.persistEntity(advanceTissueSpecimenA);

			//Step 2
			Map dataValue = new HashMap();
			dataValue.put(barcode, "123456");
			dataValue.put(label, "specimen parent label");
			dataValue.put(quantityInCellCount, "45");
			dataValue.put(arivalDate, "11"+ProcessorConstants.DATE_SEPARATOR+"12"+ProcessorConstants.DATE_SEPARATOR+"1982");

			Long recordId = entityManagerInterface.insertData(tissueSpecimen, dataValue);

			int rowCount = (Integer) executeQuery("select count(*)  from "
					+ specimen.getTableProperties().getName(), INT_TYPE, 1);
			assertEquals(1, rowCount);

			rowCount = (Integer) executeQuery("select count(*)  from "
					+ tissueSpecimen.getTableProperties().getName(), INT_TYPE, 1);
			assertEquals(1, rowCount);

			// Step 3
			dataValue.put(barcode, "870");
			dataValue.put(label, "specimen parent label123");
			dataValue.put(quantityInCellCount, "4546");
			dataValue.put(arivalDate, "11"+ProcessorConstants.DATE_SEPARATOR+"11"+ProcessorConstants.DATE_SEPARATOR+"1982");

			entityManagerInterface.editData(tissueSpecimen, dataValue, recordId);

			// step 4
			rowCount = (Integer) executeQuery("select count(*) from "
					+ specimen.getTableProperties().getName(), INT_TYPE, 1);
			assertEquals(1, rowCount);

			rowCount = (Integer) executeQuery("select count(*)  from "
					+ tissueSpecimen.getTableProperties().getName(), INT_TYPE, 1);
			assertEquals(1, rowCount);

			dataValue.clear();
			dataValue = entityManagerInterface.getRecordById(tissueSpecimen, recordId);

			assertEquals("870", dataValue.get(barcode));
			assertEquals("specimen parent label123", dataValue.get(label));
			assertEquals("4546", dataValue.get(quantityInCellCount));

			//Step 5
			dataValue.clear();
			dataValue.put(barcode, "869");
			dataValue.put(label, "specimen parent label");
			dataValue.put(quantityInCellCount, "45");
			dataValue.put(arivalDate, "11"+ProcessorConstants.DATE_SEPARATOR+"12"+ProcessorConstants.DATE_SEPARATOR+"1982");
			dataValue.put(newAttribute, "12");

			recordId = entityManagerInterface.insertData(advanceTissueSpecimenA, dataValue);

			rowCount = (Integer) executeQuery("select count(*) from "
					+ specimen.getTableProperties().getName(), INT_TYPE, 1);
			assertEquals(2, rowCount);

			rowCount = (Integer) executeQuery("select count(*) from "
					+ tissueSpecimen.getTableProperties().getName(), INT_TYPE, 1);
			assertEquals(2, rowCount);

			rowCount = (Integer) executeQuery("select count(*) from "
					+ advanceTissueSpecimenA.getTableProperties().getName(), INT_TYPE, 1);
			assertEquals(1, rowCount);

			//Step 6
			dataValue.clear();
			dataValue.put(barcode, "875");
			dataValue.put(label, "New Label");
			dataValue.put(quantityInCellCount, "454647");
			dataValue.put(arivalDate, "11"+ProcessorConstants.DATE_SEPARATOR+"11"+ProcessorConstants.DATE_SEPARATOR+"1982");
			dataValue.put(newAttribute, "1223");

			entityManagerInterface.editData(advanceTissueSpecimenA, dataValue, recordId);

			//Step 7
			dataValue.clear();
			dataValue = entityManagerInterface.getRecordById(advanceTissueSpecimenA, recordId);

			assertEquals("875", dataValue.get(barcode));
			assertEquals("New Label", dataValue.get(label));
			assertEquals("454647", dataValue.get(quantityInCellCount));

			rowCount = (Integer) executeQuery("select count(*) from "
					+ specimen.getTableProperties().getName(), INT_TYPE, 1);
			assertEquals(2, rowCount);

			rowCount = (Integer) executeQuery("select count(*) from "
					+ tissueSpecimen.getTableProperties().getName(), INT_TYPE, 1);
			assertEquals(2, rowCount);

			rowCount = (Integer) executeQuery("select count(*) from "
					+ advanceTissueSpecimenA.getTableProperties().getName(), INT_TYPE, 1);
			assertEquals(1, rowCount);
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
