/**
 *<p>Description:  </p>
 *<p>Copyright://TODO</p>
 *@author Vishvesh Mulay
 *@version 1.0
 */

package edu.common.dynamicextensions.entitymanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domain.AbstractAttribute;
import edu.common.dynamicextensions.domain.Association;
import edu.common.dynamicextensions.domain.Attribute;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domain.userinterface.Container;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationDirection;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.wustl.common.util.logger.Logger;

public class TestEntityManagerForAssociations extends DynamicExtensionsBaseTestCase
{

	/**
	 *
	 */
	public TestEntityManagerForAssociations()
	{
		super();
		//TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0 name
	 */
	public TestEntityManagerForAssociations(String arg0)
	{
		super(arg0);
		//TODO Auto-generated constructor stub
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
	 * This test case test for associating two entities with one to many association
	 *
	 * for oracle it should throw exception.
	 * for mysql  it works.
	 */
	public void testCreateEntityWithOneToManyAssociation()
	{
		EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		// create user
		EntityGroupInterface entityGroup = DomainObjectFactory.getInstance().createEntityGroup();
		entityGroup.setName("User" + new Double(Math.random()).toString());
		EntityInterface user = createAndPopulateEntity();
		AttributeInterface userNameAttribute = factory.createStringAttribute();
		userNameAttribute.setName("user_name");
		user.setName("user");
		user.addAbstractAttribute(userNameAttribute);

		// create study
		EntityInterface study = createAndPopulateEntity();

		Container container = new Container();
		container.setCaption("testcontainer");
		Collection<ContainerInterface> listOfContainers = new HashSet<ContainerInterface>();
		listOfContainers.add(container);

		study.setContainerCollection(listOfContainers);
		try
		{
			AttributeInterface studyNameAttribute = factory.createStringAttribute();
			studyNameAttribute.setName("study_name");
			study.setName("study");
			study.addAbstractAttribute(studyNameAttribute);

			// Associate user (1)------ >(*)study
			AssociationInterface association = factory.createAssociation();

			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ONE, Cardinality.ONE));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.MANY));

			user.addAbstractAttribute(association);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);

			//entityManager.createEntity(study);
			entityGroup.addEntity(user);
			user.setEntityGroup(entityGroup);
			entityGroup.addEntity(study);
			study.setEntityGroup(entityGroup);
			new EntityManager();

			entityGroupManager.persistEntityGroup(entityGroup);

			Collection<Container> coll = study.getContainerCollection();

			for (ContainerInterface cont : coll)
			{
				cont.getId();
			}
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

	}

	/**
	 * This test case test for associating two entities with one to many association
	 *
	 * for oracle it should throw exception.
	 * for mysql  it works.
	 */
	public void testCreateEntityWithManyToOneAssociation()
	{
		EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		// create user
		EntityGroupInterface entityGroupInterface = factory.getInstance().createEntityGroup();
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

		// Associate user (1)------ >(*)study
		AssociationInterface association = factory.createAssociation();

		try
		{
			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ZERO, Cardinality.MANY));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.ONE));

			user.addAbstractAttribute(association);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);

			//entityManager.createEntity(study);

			entityGroupInterface.getEntityCollection().add(user);
			entityGroupManager.persistEntityGroup(entityGroupInterface);
			assertEquals(getColumnCount("select * from " + user.getTableProperties().getName()),
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

	}

	/**
	 * This test case tries to modify data type of the attribute,when data is present for that column.
	 * for oracle it should throw exception.
	 * for mysql  it works.
	 */
	public void testCreateEntityWithAssociationWithUnsavedTargetEntity()
	{
		try
		{
			DomainObjectFactory factory = DomainObjectFactory.getInstance();
			EntityGroupInterface entityGroup = factory.createEntityGroup();
			entityGroup.setName("test_" + new Double(Math.random()).toString());

			Entity srcEntity = (Entity) new MockEntityManager().initializeEntity(entityGroup);
			srcEntity.setName("study");
			//Entity savedSrcEntity = (Entity) EntityManager.getInstance().createEntity(srcEntity);
			Entity targetEntity = (Entity) new MockEntityManager().initializeEntity(entityGroup);
			targetEntity.setName("user");
			//Entity savedTargetEntity = (Entity) EntityManager.getInstance().createEntity(targetEntity);
			Association association = (Association) DomainObjectFactory.getInstance()
					.createAssociation();
			association.setEntity(srcEntity);
			association.setTargetEntity(targetEntity);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ONE, Cardinality.ONE));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ONE, Cardinality.ONE));
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);
			srcEntity.addAbstractAttribute(association);
			// association.sets

			EntityManager.getInstance().persistEntity(srcEntity);

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			fail();
			e.printStackTrace();
		}

	}

	/**
	 * This test case test for associating two entities with many to many association  and direction is src_destination.
	 *
	 * for oracle it should throw exception.
	 * for mysql  it works.
	 */
	public void testCreateEntityWithManyToManyAssociation()
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

		// Associate user (1)------ >(*)study
		AssociationInterface association = factory.createAssociation();
		try
		{
			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ONE, Cardinality.MANY));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.MANY));

			user.addAbstractAttribute(association);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);
			entityGroup.addEntity(user);
			user.setEntityGroup(entityGroup);
			entityGroup.addEntity(study);
			study.setEntityGroup(entityGroup);

			entityManager.persistEntity(user);

			String middleTableName = association.getConstraintProperties().getName();

			assertNotNull(middleTableName);

			assertEquals(getColumnCount("select * from " + middleTableName), noOfDefaultColumns + 1);
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
	 * Purpose is to test the self referencing of the entity.
	 * Scenario - user(*)------>(1)User
	 *                   creator
	 */
	public void testEditEntityWithSelfReferencingBiDirectionalManyToManyAssociation()
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
		entityGroup.addEntity(user);
		user.setEntityGroup(entityGroup);
		try
		{
			user = entityManager.persistEntity(user);

			// Associate user (*)------ >(1)user
			AssociationInterface association = factory.createAssociation();
			association.setName("testassociation");
			association.setEntity(user);
			association.setTargetEntity(user);
			association.setAssociationDirection(AssociationDirection.BI_DIRECTIONAL);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "subpi",
					Cardinality.ONE, Cardinality.ONE));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "pi", Cardinality.ONE,
					Cardinality.MANY));

			user.addAbstractAttribute(association);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);

			entityManager.persistEntity(user);

			String tableName = user.getTableProperties().getName();

			assertNotNull(tableName);

			assertEquals(getColumnCount("select * from " + tableName), noOfDefaultColumns + 2);
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
	 * @param targetEntity
	 * @param associationDirection
	 * @param assoName
	 * @param sourceRole
	 * @param targetRole
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private AssociationInterface getAssociation(EntityInterface targetEntity,
			AssociationDirection associationDirection, String assoName, RoleInterface sourceRole,
			RoleInterface targetRole) throws DynamicExtensionsSystemException
	{
		AssociationInterface association = DomainObjectFactory.getInstance().createAssociation();
		association.setTargetEntity(targetEntity);
		association.setAssociationDirection(associationDirection);
		association.setName(assoName);
		association.setSourceRole(sourceRole);
		association.setTargetRole(targetRole);
		return association;
	}

	/**
	 * This test case test for associating three entities with  many to one to one
	 *
	 * User(*) ---- >(1)Study(1) ------>(1)institute
	 */
	public void testCreateEntityWithCascadeManyToOneAssociation()
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

		// create institution
		EntityInterface institution = createAndPopulateEntity();
		AttributeInterface institutionNameAttribute = factory.createStringAttribute();
		institutionNameAttribute.setName("institution name");
		institution.setName("institution");
		institution.addAbstractAttribute(institutionNameAttribute);

		// Associate user (*)------ >(1)study
		AssociationInterface association = factory.createAssociation();
		try
		{
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

	/**
	 * This test case test for associating three entities with  many to one to many to many
	 *
	 *        User(*) ---- >(1)Study(1) ------>(1)institute(*)-- (*)User
	 *
	 */
	public void testCreateEntityWithCyclicCascadeManyToOneAssociation()
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

		// create institution
		EntityInterface institution = createAndPopulateEntity();
		AttributeInterface institutionNameAttribute = factory.createStringAttribute();
		institutionNameAttribute.setName("institution name");
		institution.setName("institution");
		institution.addAbstractAttribute(institutionNameAttribute);

		// Associate user (*)------ >(1)study
		AssociationInterface association = factory.createAssociation();
		try
		{
			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ZERO, Cardinality.MANY));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.ONE));

			user.addAbstractAttribute(association);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);
			EntityManagerUtil.addIdAttribute(user);
			entityGroup.addEntity(user);
			user.setEntityGroup(entityGroup);
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
			EntityManagerUtil.addIdAttribute(study);
			entityGroup.addEntity(study);
			study.setEntityGroup(entityGroup);
			// Associate institution (*)----->(*) user
			AssociationInterface instUserAssociation = factory.createAssociation();

			instUserAssociation.setTargetEntity(user);
			instUserAssociation.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			instUserAssociation.setName("lecturers");
			instUserAssociation.setSourceRole(getRole(AssociationType.ASSOCIATION, "university",
					Cardinality.ONE, Cardinality.MANY));
			instUserAssociation.setTargetRole(getRole(AssociationType.ASSOCIATION, "lecturer",
					Cardinality.ONE, Cardinality.MANY));

			institution.addAbstractAttribute(instUserAssociation);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(instUserAssociation);
			entityGroup.addEntity(institution);
			institution.setEntityGroup(entityGroup);

			entityManager.persistEntity(user);

			assertEquals(getColumnCount("select * from " + user.getTableProperties().getName()),
					noOfDefaultColumns + 2);

			assertEquals(getColumnCount("select * from " + study.getTableProperties().getName()),
					noOfDefaultColumns + 1);

			assertEquals(getColumnCount("select * from "
					+ institution.getTableProperties().getName()), noOfDefaultColumns + 2);

			assertNotNull(instUserAssociation.getConstraintProperties().getName());

			assertEquals(getColumnCount("select * from "
					+ instUserAssociation.getConstraintProperties().getName()),
					noOfDefaultColumns + 1);

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
	 * Purpose is to test the self referencing of the entity.
	 * Scenario - user(*)------>(1)User
	 *                   creator
	 */
	public void testCreateEntityWithSelfReferencingManyToManyAssociation()
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

		// Associate user (*)------ >(1)user
		AssociationInterface association = factory.createAssociation();

		try
		{
			association.setTargetEntity(user);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "subpi",
					Cardinality.ONE, Cardinality.MANY));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "pi", Cardinality.ONE,
					Cardinality.MANY));

			user.addAbstractAttribute(association);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);
			entityGroup.addEntity(user);
			user.setEntityGroup(entityGroup);

			entityManager.persistEntity(user);

			String tableName = user.getTableProperties().getName();

			assertNotNull(tableName);

			assertEquals(getColumnCount("select * from " + tableName), noOfDefaultColumns + 1);

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

	/**
	 * Purpose is to test the multiple self referencing of the entity.
	 * Scenario -
	 * user(*)------>(*)User
	 *       childUsers
	 * user(*)------>(1)User
	 *        creator
	 */
	public void testCreateEntityWithSelfReferencingMultipleAssociations()
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

		// Associate user (*)------ >(*)user
		AssociationInterface association = factory.createAssociation();
		try
		{

			association.setTargetEntity(user);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("children");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "subpi",
					Cardinality.ONE, Cardinality.MANY));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "pi", Cardinality.ONE,
					Cardinality.MANY));

			user.addAbstractAttribute(association);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);

			// Associate user (*)------ >(1)user
			AssociationInterface creatorAssociation = factory.createAssociation();

			creatorAssociation.setTargetEntity(user);
			creatorAssociation.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			creatorAssociation.setName("creator");
			creatorAssociation.setSourceRole(getRole(AssociationType.ASSOCIATION, "user",
					Cardinality.ONE, Cardinality.MANY));
			creatorAssociation.setTargetRole(getRole(AssociationType.ASSOCIATION, "creator",
					Cardinality.ONE, Cardinality.ONE));

			user.addAbstractAttribute(creatorAssociation);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(creatorAssociation);
			entityGroup.addEntity(user);
			user.setEntityGroup(entityGroup);

			entityManager.persistEntity(user);

			String tableName = user.getTableProperties().getName();

			assertNotNull(tableName);

			assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from " + tableName));

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

	/**
	 * Purpose is to test the multiple self referencing of the entity.
	 * Scenario -
	 * user(*)------>(*)User
	 *       childUsers
	 * user(*)------>(*)User
	 *        creators
	 */
	public void testCreateEntityWithSelfReferencingMultipleManyToManyAssociations()
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

		// Associate user (*)------ >(*)user
		AssociationInterface association = factory.createAssociation();
		try
		{
			association.setTargetEntity(user);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("children");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "subpi",
					Cardinality.ONE, Cardinality.MANY));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "pi", Cardinality.ONE,
					Cardinality.MANY));

			user.addAbstractAttribute(association);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);

			// Associate user (*)------ >(*)user
			AssociationInterface creatorAssociation = factory.createAssociation();

			creatorAssociation.setTargetEntity(user);
			creatorAssociation.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			creatorAssociation.setName("parentUSers");
			creatorAssociation.setSourceRole(getRole(AssociationType.ASSOCIATION, "user",
					Cardinality.ONE, Cardinality.MANY));
			creatorAssociation.setTargetRole(getRole(AssociationType.ASSOCIATION, "creator",
					Cardinality.ONE, Cardinality.MANY));

			user.addAbstractAttribute(creatorAssociation);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(creatorAssociation);
			entityGroup.addEntity(user);
			user.setEntityGroup(entityGroup);

			entityManager.persistEntity(user);

			String tableName = user.getTableProperties().getName();

			assertNotNull(tableName);

			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from " + tableName));

			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ association.getConstraintProperties().getName()));

			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ creatorAssociation.getConstraintProperties().getName()));

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
	 * This test case test for associating two entities with one to many association  and bidirectional
	 * It tests for internal system generated association for the bidirectional.
	 * User(1) <---->(*)Study
	 */
	public void testCreateEntityBidirectionalOneToManyAssociation()
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
		EntityManagerUtil.addIdAttribute(user);
		// create study
		EntityInterface study = createAndPopulateEntity();
		study.setName("Study");

		// Associate  User(1) <---->(*)Study
		AssociationInterface association = factory.createAssociation();
		association.setEntity(user);
		try
		{
			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.BI_DIRECTIONAL);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ONE, Cardinality.ONE));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.MANY));
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);

			user.addAbstractAttribute(association);
			entityGroup.addEntity(user);
			entityGroup.addEntity(study);
			user.setEntityGroup(entityGroup);
			study.setEntityGroup(entityGroup);

			entityManager.persistEntity(user);

			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ study.getTableProperties().getName()));
			//1 user attribute + 1 system generated attribute
			assertEquals(2, study.getAbstractAttributeCollection().size());

			Association systemGeneratedAssociation = (Association) study
					.getAbstractAttributeCollection().toArray()[0];

			assertTrue(systemGeneratedAssociation.getIsSystemGenerated());

			assertEquals(user, systemGeneratedAssociation.getTargetEntity());

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
	 *
	 *
	 */
	public void testGetAssociation()
	{
		try
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

			// create institution
			EntityInterface institution = createAndPopulateEntity();
			AttributeInterface institutionNameAttribute = factory.createStringAttribute();
			institutionNameAttribute.setName("institution name");
			institution.setName("institution");
			institution.addAbstractAttribute(institutionNameAttribute);

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
			entityGroup.addEntity(study);
			user.setEntityGroup(entityGroup);
			study.setEntityGroup(entityGroup);
			entityGroup.addEntity(institution);
			institution.setEntityGroup(entityGroup);
			user = entityManager.persistEntity(user);
			Collection<AssociationInterface> associationInterface = entityManager.getAssociation(
					"user", "primaryInvestigator");
			assertNotNull(associationInterface);

		}

		catch (Exception e)
		{
			//TODO Auto-generated catch block
			e.printStackTrace();
			Logger.out.debug(e.getMessage());
			fail("Exception occured");
		}

	}

	public void testGetAssociations()
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
		study.setName("study name");

		// Associate  User(1) <---->(*)Study
		AssociationInterface association = factory.createAssociation();
		association.setEntity(user);
		try
		{
			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.BI_DIRECTIONAL);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ONE, Cardinality.ONE));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.MANY));

			user.addAbstractAttribute(association);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);
			entityGroup.addEntity(user);
			user.setEntityGroup(entityGroup);
			entityGroup.addEntity(study);
			study.setEntityGroup(entityGroup);

			EntityInterface savedUser = entityManager.persistEntity(user);
			Collection associationCollection = entityManager.getAssociations(savedUser.getId(),
					study.getId());
			assertTrue(associationCollection != null && associationCollection.size() > 0);

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
	 * Purpose is to test the self referencing of the entity.
	 * Scenario - user(*)------>(1)User
	 *                   creator
	 */
	public void testCreateEntityWithSelfReferencingBidirectionManyToManyAssociation()
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
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);
			entityGroup.addEntity(user);
			user.setEntityGroup(entityGroup);

			entityManager.persistEntity(user);

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

	/**
	 * This test case test for adding a new association bet 2 entities
	 *
	 * for oracle it should throw exception.
	 * for mysql  it works.
	 */
	public void testAddAssociationAfterSave()
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
		entityGroup.addEntity(user);
		user.setEntityGroup(entityGroup);
		try
		{
			user = entityManager.persistEntity(user);

			// create study
			EntityInterface study = createAndPopulateEntity();
			AttributeInterface studyNameAttribute = factory.createStringAttribute();
			studyNameAttribute.setName("study name");
			study.setName("study");
			study.addAbstractAttribute(studyNameAttribute);
			entityGroup.addEntity(study);
			study.setEntityGroup(entityGroup);

			// Associate user (1)------ >(*)study
			AssociationInterface association = factory.createAssociation();
			association.setEntity(user);
			association.setTargetEntity(study);

			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ZERO, Cardinality.MANY));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.ONE));
			association.setAssociationDirection(AssociationDirection.BI_DIRECTIONAL);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);
			user.addAbstractAttribute(association);

			EntityInterface savedUser = entityManager.persistEntity(user);

			assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "
					+ user.getTableProperties().getName()));

			//			 Associate user (1) <------>(*)study
			AssociationInterface association1 = factory.createAssociation();
			association1.setEntity(user);
			association1.setTargetEntity(study);

			association1.setName("primaryInvestigator");
			association1.setSourceRole(getRole(AssociationType.ASSOCIATION, "association1",
					Cardinality.ZERO, Cardinality.ONE));
			association1.setTargetRole(getRole(AssociationType.ASSOCIATION, "association1",
					Cardinality.ZERO, Cardinality.MANY));
			association1.setAssociationDirection(AssociationDirection.BI_DIRECTIONAL);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association1);
			savedUser.addAbstractAttribute(association1);

			savedUser = entityManager.persistEntity(savedUser);

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

	}

	/**
	 * This test case test for removing an association bet 2 existing entities
	 */
	public void testRemoveAssociationAfterSave()
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
		entityGroup.addEntity(user);
		user.setEntityGroup(entityGroup);

		try
		{
			user = entityManager.persistEntity(user);

			// create study
			EntityInterface study = createAndPopulateEntity();
			AttributeInterface studyNameAttribute = factory.createStringAttribute();
			studyNameAttribute.setName("study name");
			study.setName("study");
			study.addAbstractAttribute(studyNameAttribute);
			entityGroup.addEntity(study);
			study.setEntityGroup(entityGroup);
			// Associate user (1)------ >(*)study
			RoleInterface sourceRole = getRole(AssociationType.ASSOCIATION, "association1",
					Cardinality.ZERO, Cardinality.MANY);
			RoleInterface targetRole = getRole(AssociationType.ASSOCIATION, "association1",
					Cardinality.ZERO, Cardinality.ONE);
			AssociationInterface association = getAssociation(study, user,
					AssociationDirection.SRC_DESTINATION, "prim", sourceRole, targetRole);

			user.addAbstractAttribute(association);
			user = entityManager.persistEntity(user);

			assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "
					+ user.getTableProperties().getName()));

			user.removeAssociation(association);
			user = entityManager.persistEntity(user);

			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ user.getTableProperties().getName()));

			sourceRole = getRole(AssociationType.ASSOCIATION, "association1", Cardinality.ZERO,
					Cardinality.MANY);
			targetRole = getRole(AssociationType.ASSOCIATION, "association1", Cardinality.ZERO,
					Cardinality.MANY);
			association = getAssociation(study, user, AssociationDirection.SRC_DESTINATION, "prim",
					sourceRole, targetRole);

			user.addAbstractAttribute(association);
			user = entityManager.persistEntity(user);

			assertTrue(isTablePresent(association.getConstraintProperties().getName()));
			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ association.getConstraintProperties().getName()));

			user.removeAssociation(association);
			user = entityManager.persistEntity(user);

			assertFalse(isTablePresent(association.getConstraintProperties().getName()));
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
	 * This test case test for removing an association bet 2 existing entities
	 * Before- SRC-DESTINATION
	 * After - BIDIRECTIONAL
	 */
	public void testEditAssociationDirection1AfterSave()
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
			AssociationInterface association = getAssociation(user, study,
					AssociationDirection.SRC_DESTINATION, "prim", sourceRole, targetRole);

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

			association.getSourceRole().setMaximumCardinality(Cardinality.ONE);
			association.getTargetRole().setMaximumCardinality(Cardinality.MANY);
			association.setAssociationDirection(AssociationDirection.BI_DIRECTIONAL);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);
			user = entityManager.persistEntity(user);

			assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "
					+ study.getTableProperties().getName()));
			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ user.getTableProperties().getName()));
			assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "
					+ study.getTableProperties().getName()));

			EntityInterface savedUser = entityManager
					.getEntityByIdentifier(user.getId().toString());
			assertEquals(1, savedUser.getAssociationCollection().size());

			EntityInterface savedStudy = entityManager.getEntityByIdentifier(study.getId()
					.toString());
			assertEquals(1, savedStudy.getAssociationCollection().size());
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
	 * This test case test for removing an association bet 2 existing entities
	 * Before-  BIDIRECTIONAL
	 * After -  SRC-DESTINATION
	 * system generated association should get removed.
	 */
	public void testEditAssociationDirectionAfterSave()
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

		// Associate user (1)------ >(*)study
		try
		{
			RoleInterface sourceRole = getRole(AssociationType.ASSOCIATION, "association1",
					Cardinality.ZERO, Cardinality.MANY);
			RoleInterface targetRole = getRole(AssociationType.ASSOCIATION, "association1",
					Cardinality.ZERO, Cardinality.ONE);
			AssociationInterface association = getAssociation(study, user,
					AssociationDirection.BI_DIRECTIONAL, "prim", sourceRole, targetRole);

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
			association.getSourceRole().setMaximumCardinality(Cardinality.ONE);
			association.getTargetRole().setMaximumCardinality(Cardinality.MANY);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);

			user = entityManager.persistEntity(user);

			assertEquals(noOfDefaultColumns + 2, getColumnCount("select * from "
					+ user.getTableProperties().getName()));

			assertEquals(noOfDefaultColumns + 1, getColumnCount("select * from "
					+ study.getTableProperties().getName()));

			EntityInterface savedUser = entityManager
					.getEntityByIdentifier(user.getId().toString());
			assertEquals(1, savedUser.getAssociationCollection().size());

			EntityInterface savedStudy = entityManager.getEntityByIdentifier(study.getId()
					.toString());
			assertEquals(0, savedStudy.getAssociationCollection().size());
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
	 *
	 */
	public void testGetAssociationById()
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

		// Associate user (1)------ >(*)study
		AssociationInterface association = factory.createAssociation();
		try
		{
			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ZERO, Cardinality.MANY));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.ONE));

			user.addAbstractAttribute(association);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);
			entityGroup.addEntity(user);
			user.setEntityGroup(entityGroup);
			entityGroup.addEntity(study);
			study.setEntityGroup(entityGroup);

			//entityManager.createEntity(study);

			entityManager.persistEntity(user);

			System.out.println();

			AssociationInterface saveAssociation = entityManager
					.getAssociationByIdentifier(association.getId());

			assertEquals(association.getName(), saveAssociation.getName());

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
	 *
	 */
	public void testGetAssociationByIdNotPresent()
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		DomainObjectFactory.getInstance();

		try
		{
			entityManager.getAssociationByIdentifier(123456L);
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
			assertTrue(true);
		}
	}

	/**
	 *  PURPOSE: This method tests for editing the data for the case when multiple level containment is
	 *           present.    (fix for the bug : 3289)
	 *
	 *  EXPECTED BEHAVIOUR: The data should be properly edited..
	 *  TEST CASE FLOW: 1. create User
	 *                  2. Create Institute
	 *                  3. Create address
	 *                  4. Add Association with      User(1) ------->(*) Institutes with containment association
	 *                  5. Add Association with      Institutes(1) ------->(*) Address containment association
	 *                  6. Add data for user  rahul -> Verizon --> Pune
	 *                  7. edit data for the user  rahul --> PSPL -->Pune
	 *                  8. Address should have only one record.. previous one should get deleted and new one should get added
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	public void testEditDataWithContainmentForMultipleLevel()
	{
		try
		{
			EntityManagerInterface entityManager = EntityManager.getInstance();

			// Step 1. Fetch all required entities
			EntityGroupInterface testEntityGroup = entityManager
					.getEntityGroupByName(TEST_ENTITYGROUP_NAME);

			Entity patientInformationEntity = (Entity) testEntityGroup
					.getEntityByName("PatientInformation");

			Entity physicianInformationEntity = (Entity) testEntityGroup
					.getEntityByName("PhysicianInformation");

			Entity physicianContactInformationEntity = (Entity) testEntityGroup
					.getEntityByName("PhyContactInfo");

			// Step 2. Start populating datavalue map from lowest(second) hierarchy entity
			Map<BaseAbstractAttributeInterface, Object> phyContactInfoMap = new HashMap<BaseAbstractAttributeInterface, Object>();
			List<Map<BaseAbstractAttributeInterface, Object>> phyContactInfoList = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
			edu.common.dynamicextensions.domain.Attribute phoneType = (Attribute) physicianContactInformationEntity
					.getAttributeByName("phoneType");
			phyContactInfoMap.put(phoneType, "Home");
			phyContactInfoList.add(phyContactInfoMap);

			// Step 3. Populate datavalue map for first entity in hierarchy
			Map<BaseAbstractAttributeInterface, Object> phyInformationMap = new HashMap<BaseAbstractAttributeInterface, Object>();
			List<Map<BaseAbstractAttributeInterface, Object>> phyInfoList = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
			edu.common.dynamicextensions.domain.Attribute physicianName = (Attribute) physicianInformationEntity
					.getAttributeByName("physicianName");
			AbstractAttribute physicianContactInformation = (AbstractAttribute) physicianInformationEntity
					.getAttributeByName("physicianContact");
			phyContactInfoMap.put(physicianName, "Gaurav Mehta");
			phyContactInfoMap.put(physicianContactInformation, phyContactInfoList);
			phyInfoList.add(phyInformationMap);

			//Step 4. Populate datavalue map for teop level entity.
			Map<BaseAbstractAttributeInterface, Object> patientInformationMap = new HashMap<BaseAbstractAttributeInterface, Object>();
			edu.common.dynamicextensions.domain.Attribute age = (Attribute) patientInformationEntity
					.getAttributeByName("age");
			AbstractAttribute physicianInformation = (AbstractAttribute) patientInformationEntity
					.getAttributeByName("pi");
			phyContactInfoMap.put(age, "25");
			phyContactInfoMap.put(physicianInformation, phyInfoList);

			ContainerInterface containerInterface = (ContainerInterface) patientInformationEntity
					.getContainerCollection().toArray()[0];
			Long recordId = DynamicExtensionsUtility.insertDataUtility(null, containerInterface,
					patientInformationMap);

			assertNotNull(recordId);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("testEditDataWithContainmentForMultipleLevel() : unknown exception occured " + e.getMessage());
		}

	}

	/**
	 * This test case test for associating two entities with one to many association
	 *
	 * for oracle it should throw exception.
	 * for mysql  it works.
	 */
	public void testGetIncomingAssociationsForEntity()
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
		entityGroup.addEntity(user);
		user.setEntityGroup(entityGroup);
		// create study
		EntityInterface study = createAndPopulateEntity();
		AttributeInterface studyNameAttribute = factory.createStringAttribute();
		studyNameAttribute.setName("study name");
		study.setName("study");
		study.addAbstractAttribute(studyNameAttribute);
		entityGroup.addEntity(study);
		study.setEntityGroup(entityGroup);
		// Associate user (1)------ >(*)study
		AssociationInterface association = factory.createAssociation();
		try
		{
			association.setTargetEntity(study);
			association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association.setName("primaryInvestigator");
			association.setSourceRole(getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
					Cardinality.ONE, Cardinality.ONE));
			association.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.MANY));

			user.addAbstractAttribute(association);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);

			// create site
			EntityInterface site = createAndPopulateEntity();
			AttributeInterface siteNameAttribute = factory.createStringAttribute();
			siteNameAttribute.setName("site name");
			site.setName("site");
			site.addAbstractAttribute(siteNameAttribute);
			entityGroup.addEntity(site);
			site.setEntityGroup(entityGroup);
			// Associate site (1)------ >(*)study
			AssociationInterface associationSite = factory.createAssociation();

			associationSite.setTargetEntity(study);
			associationSite.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			associationSite.setName("site_study");
			associationSite.setSourceRole(getRole(AssociationType.ASSOCIATION, "site",
					Cardinality.ONE, Cardinality.ONE));
			associationSite.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
					Cardinality.ZERO, Cardinality.MANY));

			site.addAbstractAttribute(associationSite);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(associationSite);

			//entityManager.createEntity(study);

			entityManager.persistEntity(user);
			site = entityManager.persistEntity(site);

			Collection<AssociationInterface> coll = entityManager.getIncomingAssociations(study);
			assertEquals(2, coll.size());

			coll = entityManager.getIncomingAssociations(user);
			assertEquals(0, coll.size());

			coll = entityManager.getIncomingAssociations(site);
			assertEquals(0, coll.size());
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
	 * @param targetEntity
	 * @param associationDirection
	 * @param assoName
	 * @param sourceRole
	 * @param targetRole
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private AssociationInterface getAssociation(EntityInterface sourceEntity,
			EntityInterface targetEntity, AssociationDirection associationDirection,
			String assoName, RoleInterface sourceRole, RoleInterface targetRole)
			throws DynamicExtensionsSystemException
	{
		AssociationInterface association = DomainObjectFactory.getInstance().createAssociation();
		association.setTargetEntity(targetEntity);
		association.setEntity(sourceEntity);
		association.setName(assoName);
		association.setSourceRole(sourceRole);
		association.setTargetRole(targetRole);
		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);
		association.setAssociationDirection(associationDirection);
		return association;
	}

}
