/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright://TODO</p>
 *@author Ashish Gupta
 *@version 1.0
 */

package edu.common.dynamicextensions.entitymanager;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.EntityGroup;
import edu.common.dynamicextensions.domain.userinterface.Container;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.XMIImporter;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationDirection;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.common.dynamicextensions.xmi.exporter.XMIExporter;
import edu.wustl.common.util.logger.Logger;

public class TestEntityMangerForXMIImportExport extends DynamicExtensionsBaseTestCase
{

	/**
	 * Specify the name of the domain model xmi file to import. This file must be present at the path test/ModelXML under the project root directory
	 */
	private final String XMIFileName = XMI_FILE_PATH + "newsurgery.xmi";
	private final String ClinicalAnnotationXMI = XMI_FILE_PATH + "ClinicalAnnotations.xmi";

	/**
	 *
	 */
	public void testXMIImport()
	{
		try
		{
			String[] args = {XMIFileName, CSV_FILE_PATH + "surgery.csv", "Surgery", "  "};
			XMIImporter.main(args);
			//			DomainModelParser parser = getParser(XMIFileName);
			//			XMIImportProcessor processor = new XMIImportProcessor(
			//					parser, "Application1");
			System.out.println("--------------- Test Case to import XMI successful ------------");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Exception occured");
		}
	}

	/**
	 *
	 */
	public void testXMIImportEditCase()
	{
		testXMIImport();
	}

	/**
	 *
	 */
	/*public void testImportClinicalAnnotationsXMI()
	{
		try
		{
			String[] args = {ClinicalAnnotationXMI, CSV_FILE_PATH + "AnnotationsMainContainer.csv",
					"edu.wustl.catissuecore.domain.ClinicalAnnotations", " "};
			XMIImporter.main(args);

			System.out
					.println("--------------- Importing clinical annotations XMI successful ---------------");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Could not import clinical annotations XMI.");
		}
	}*/

	/**
	 *
	 */
	public void testEditClinicalAnnotationsXMI()
	{
		testXMIImport();
	}

	public void testXMIExport()
	{

		//EntityGroupInterface entityGroup = EntityManager.getInstance().getEntityGroupByName("grp1");
		//XMIExporter xmiExporter = new XMIExporter();
		//xmiExporter.exportXMI("D:\\DEXMI.xmi", entityGroup, null);
		String args[] = {"newsurgery", XMI_FILE_PATH + "exp_newsurgery.xmi", "1.1",
				"edu.wustl.catissuecore.domain.RecordEntry"};
		XMIExporter.main(args);
		File file = new File(XMI_FILE_PATH + "exp_newsurgery.xmi");
		if (file.exists())
		{

		}

	}

	/**
	 *
	 */
	public void testUniqueCase1ForXMIImport()//E1 associated with E2 extends E3 extends E1
	{
		DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		try
		{
			EntityGroup entityGroup = (EntityGroup) new MockEntityManager().initializeEntityGroup();
			Collection<EntityInterface> entityColl = entityGroup.getEntityCollection();
			//			Container1
			EntityInterface entity1 = null;
			for (EntityInterface e : entityColl)
			{
				entity1 = e;
			}

			Container container1 = (Container) new MockEntityManager()
					.createContainerForGivenEntity("For Inheritance - Parent", entity1);
			container1.setAbstractEntity(entity1);

			EntityInterface entity2 = new MockEntityManager().initializeEntity(entityGroup);
			entity2.setName("Entity2");
			EntityInterface entity3 = new MockEntityManager().initializeEntity(entityGroup);
			entity3.setName("Entity3");

			AssociationInterface association1_2 = domainObjectFactory.createAssociation();
			association1_2.setTargetEntity(entity2);
			association1_2.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
			association1_2.setName("E1_E2");
			association1_2.setSourceRole(getRole(AssociationType.ASSOCIATION, "Entity1",
					Cardinality.ONE, Cardinality.ONE));
			association1_2.setTargetRole(getRole(AssociationType.ASSOCIATION, "Entity2",
					Cardinality.ONE, Cardinality.MANY));
			entity1.addAssociation(association1_2);

			entity2.setParentEntity(entity3);
			entity3.setParentEntity(entity1);
			entityGroup.addEntity(entity2);
			entityGroup.addEntity(entity3);

			Collection containerColl = new HashSet();
			containerColl.add(container1);
			//EntityManager.getInstance().persistEntityGroupWithAllContainers(entityGroup, containerColl);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			Logger.out.debug(e.getMessage());
			fail("Exception occured");
		}
	}

	/**
	 * @param role
	 * @param maxCardinality
	 * @param minCardinality
	 * @param roleName
	 * @return
	 */
	private RoleInterface getRole(RoleInterface role, int maxCardinality, int minCardinality,
			String roleName)
	{
		role.setAssociationsType(DEConstants.AssociationType.ASSOCIATION);
		role.setName(roleName);
		role.setMaximumCardinality(getCardinality(maxCardinality));
		role.setMinimumCardinality(getCardinality(minCardinality));
		return role;
	}

	/**
	 * @param cardinality
	 * @return
	 */
	private DEConstants.Cardinality getCardinality(int cardinality)
	{
		if (cardinality == 0)
		{
			return DEConstants.Cardinality.ZERO;
		}
		if (cardinality == 1)
		{
			return DEConstants.Cardinality.ONE;
		}
		return DEConstants.Cardinality.MANY;
	}

	public void testXMIImportValidation()
	{
		try
		{
			String[] args = {XMI_FILE_PATH + "cacoreValidation.xmi",
					CSV_FILE_PATH + "cacoreValidation.csv", "test", "  "};
			XMIImporter.main(args);
			fail("No Exception thrown for validations");
		}
		catch (Exception e)
		{
			System.out.println("Error thrown as validation messages present");
		}
	}

}
