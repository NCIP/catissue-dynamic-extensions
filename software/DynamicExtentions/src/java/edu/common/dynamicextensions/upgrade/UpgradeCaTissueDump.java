
package edu.common.dynamicextensions.upgrade;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.cab2b.common.cache.AbstractEntityCache;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * The Class UpgradeCaTissueDump.
 */
public class UpgradeCaTissueDump extends AbstractEntityCache
{

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(UpgradeCaTissueDump.class);

	/** The hibernate dao. */
	private static HibernateDAO hibernateDao;

	/** The ERRORS. */
	private static List<String> ERRORS = new ArrayList<String>();

	/**
	 * The main method.
	 * @param args the arguments
	 * @throws ApplicationException
	 */
	public static void main(String[] args) throws ApplicationException
	{
		try
		{
			//Step 1. Initialize HibernateDAO
			hibernateDao = DynamicExtensionsUtility.getHibernateDAO();

			//Step 2. Fetch all Entity Groups
			Collection<EntityGroupInterface> allEntityGroups = EntityCache.getInstance()
					.getEntityGroups();

			//Step 3. Validate each entity group for caCore validations.
			int entityGroupNumber = 1;
			for (EntityGroupInterface entityGroup : allEntityGroups)
			{
				if (!entityGroup.getIsSystemGenerated())
				{
					ERRORS
							.add("*************** upgrading for Entity Group id: "
									+ entityGroup.getId()
									+ " ***********************************************************************");
					validateAndUpdateEntityGroup(entityGroup, entityGroupNumber++);

					//Step 4. Update the corresponding Entity Group
					hibernateDao.update(entityGroup);
					ERRORS
							.add("*************** upgrade complete ***********************************************************************");
					ERRORS.add("");
				}
			}

			//Step 5. Committing the updated models.
			hibernateDao.commit();

			//Step 6. Write error log generated for each entity group to Log file
			writeToFile(ERRORS);
		}
		catch (DynamicExtensionsSystemException dese)
		{
			LOGGER.error("Error occured while creating hibernate dao object. " + dese.getMessage());
			throw new ApplicationException("Error occured while creating hibernate dao object.",
					dese);
		}
		catch (DAOException daoException)
		{
			LOGGER.error("Error occured while updating Entity Group objects. "
					+ daoException.getMessage());
			throw new ApplicationException("Error occured while updating Entity Group object.",
					daoException);
		}
		finally
		{
			try
			{
				DynamicExtensionsUtility.closeDAO(hibernateDao);
			}
			catch (final DynamicExtensionsSystemException e)
			{
				LOGGER.error("Exception encountered while closing session In EntityCache."
						+ e.getMessage());
			}
		}
	}

	/**
	 * Validate entity group.
	 * @param entityGroup the entity group
	 * @param entityGroupNumber
	 * @return the string builder
	 * @throws DAOException
	 * @throws DynamicExtensionsSystemException
	 */
	private static void validateAndUpdateEntityGroup(EntityGroupInterface entityGroup,
			int entityGroupNumber) throws DAOException, DynamicExtensionsSystemException
	{
		// Clearing list so that validations are not done across Entity Groups
		SQLGenerator.allEntityGroupAndAssociationNames.clear();

		//Step 1. Check for Entity Group Name
		XMIImportValidator.validateEntityGroupname(entityGroup);
		if (!XMIImportValidator.ERROR_LIST.isEmpty())
		{
			UpdateHelper.printErrorMessages(XMIImportValidator.ERROR_LIST, ERRORS);
			SQLGenerator.updateEntityGroupName(entityGroup, entityGroupNumber, ERRORS);
			ERRORS.add("");
		}
		SQLGenerator.allEntityGroupAndAssociationNames.add(entityGroup.getName());

		//Step 2. Check for Entity Group Short and Long Name
		XMIImportValidator.validateEntityGroupShortAndLongname(entityGroup);
		if (!XMIImportValidator.ERROR_LIST.isEmpty())
		{
			UpdateHelper.printErrorMessages(XMIImportValidator.ERROR_LIST, ERRORS);
			SQLGenerator
					.generateEntityGroupShortAndLongName(entityGroup, entityGroupNumber, ERRORS);
			ERRORS.add("");
		}
		SQLGenerator.allEntityGroupAndAssociationNames.add(entityGroup.getLongName());

		//Step 3. Validate for Cyclic associations in model
		XMIImportValidator.validateForCycleInEntityGroup(entityGroup);
		if (!XMIImportValidator.ERROR_LIST.isEmpty())
		{
			ERRORS.add("Cyclic Assocaition have been found in Entity Group "
					+ entityGroup.getName() + " and id:" + entityGroup.getId());
			ERRORS
					.add("This cannot be updated programatically. Please export the model using export XMI and correct the cyclic association");
			UpdateHelper.printErrorMessages(XMIImportValidator.ERROR_LIST, ERRORS);
		}

		//Step 4. Validate Entity Group Package Name
		String packageName = UpdateHelper.getPackageName(entityGroup);
		if (packageName == null)
		{
			ERRORS.add("Package Name not present for Entity Group :" + entityGroup.getLongName()
					+ " and id :" + entityGroup.getId());
			SQLGenerator.generatePackageName(entityGroup, entityGroupNumber, ERRORS);
			ERRORS.add("");
		}
		else
		{
			//Step 5. Validate Entity Group Package Name for duplicate package name.
			XMIImportValidator.validateForDuplicatePackageName(packageName);
			if (!XMIImportValidator.ERROR_LIST.isEmpty())
			{
				UpdateHelper.printErrorMessages(XMIImportValidator.ERROR_LIST, ERRORS);
				SQLGenerator.generatePackageName(entityGroup, entityGroupNumber, ERRORS);
				ERRORS.add("");
			}

			//Step 6. Validate whether package name is proper or not.
			XMIImportValidator.validateCorrectnessOfPackageName(packageName);
			if (!XMIImportValidator.ERROR_LIST.isEmpty())
			{
				UpdateHelper.printErrorMessages(XMIImportValidator.ERROR_LIST, ERRORS);
				SQLGenerator.updatePackageName(entityGroup, packageName, ERRORS);
				ERRORS.add("");
			}
		}

		int entityNumber = 1;
		Collection<ContainerInterface> mainContainerCollection = new HashSet<ContainerInterface>();
		for (EntityInterface entity : entityGroup.getEntityCollection())
		{
			// This collection is used to associate all the containers to the entity group.
			mainContainerCollection.addAll(entity.getContainerCollection());

			//Step 7. Validate Whether entity name starts with upper case.
			XMIImportValidator.validateClassName(entity);
			if (!XMIImportValidator.ERROR_LIST.isEmpty())
			{
				UpdateHelper.printErrorMessages(XMIImportValidator.ERROR_LIST, ERRORS);
				SQLGenerator.updateEntityName(entity, entityNumber++, ERRORS);
				ERRORS.add("");
			}
			SQLGenerator.allEntityGroupAndAssociationNames.add(entity.getName());

			// To make sure we have unique attributes in each entity
			int attributeNumber = 1;
			SQLGenerator.allAttributeNames.clear();
			for (AttributeInterface attribute : entity.getAttributeCollection())
			{
				//Step 8. Validate whether Attribute name starts with lower case (first 2 letters)
				XMIImportValidator.validateAttributeName(attribute);
				if (!XMIImportValidator.ERROR_LIST.isEmpty())
				{
					UpdateHelper.printErrorMessages(XMIImportValidator.ERROR_LIST, ERRORS);
					SQLGenerator.updateAttributeName(attribute, attributeNumber++, ERRORS);
					ERRORS.add("");
				}
				else if (!SQLGenerator.allAttributeNames.add(attribute.getName()))
				{
					SQLGenerator.updateAttributeName(attribute, attributeNumber++, ERRORS);
					ERRORS.add("");
				}

			}
			int associationNumber = 1;
			for (AssociationInterface association : entity.getAllAssociations())
			{
				//Step 9. Validate whether association name is present or not and starts with lower case (first 2 letters).
				XMIImportValidator.validateAssociationName(association);
				if (!XMIImportValidator.ERROR_LIST.isEmpty())
				{
					UpdateHelper.printErrorMessages(XMIImportValidator.ERROR_LIST, ERRORS);
					SQLGenerator.updateAssociationName(association, associationNumber,
							Boolean.FALSE, ERRORS);
					ERRORS.add("");
				}

				//Step 10. Validate whether association name is not same as any attribute name.
				XMIImportValidator.validateForAssociationNameSameAsAttributeName(association,
						entity.getAllAttributes());
				if (!XMIImportValidator.ERROR_LIST.isEmpty())
				{
					UpdateHelper.printErrorMessages(XMIImportValidator.ERROR_LIST, ERRORS);
					SQLGenerator.updateAssociationName(association, associationNumber,
							Boolean.TRUE, ERRORS);
					ERRORS.add("");
				}
				else if (!SQLGenerator.allEntityGroupAndAssociationNames.add(association.getName()))
				{
					SQLGenerator.updateAssociationName(association, associationNumber,
							Boolean.TRUE, ERRORS);
					ERRORS.add("");
				}

				//Step 11. Validate whether association Source role name is present or not and starts with lower case (first 2 lettes).
				XMIImportValidator.validateAssociationSourceRoleName(association.getSourceRole(),
						association);
				if (!XMIImportValidator.ERROR_LIST.isEmpty())
				{
					UpdateHelper.printErrorMessages(XMIImportValidator.ERROR_LIST, ERRORS);
					SQLGenerator.updateAssociationSourceRoleName(association.getSourceRole(),
							association.getEntity(), association.getName(), associationNumber,
							ERRORS);
					ERRORS.add("");
				}
				SQLGenerator.allEntityGroupAndAssociationNames.add(association.getSourceRole()
						.getName());

				//Step 12. Validate whether association Target role name is present or not and starts with lower case (first 2 lettes).
				XMIImportValidator.validateAssociationTargetRoleName(association.getTargetRole(),
						association);
				if (!XMIImportValidator.ERROR_LIST.isEmpty())
				{
					UpdateHelper.printErrorMessages(XMIImportValidator.ERROR_LIST, ERRORS);
					SQLGenerator.updateAssociationTargetRoleName(association.getTargetRole(),
							association.getTargetEntity(), association.getName(),
							associationNumber, ERRORS);
					ERRORS.add("");
				}
				SQLGenerator.allEntityGroupAndAssociationNames.add(association.getTargetRole()
						.getName());

				//Step 13. Validate for many-many association.
				XMIImportValidator.validateCardinality(association.getSourceRole(), association
						.getTargetRole(), association.getEntity().getName(), association
						.getTargetEntity().getName());
				if (!XMIImportValidator.ERROR_LIST.isEmpty())
				{
					ERRORS.add("Many-Many association have been found between entities "
							+ association.getEntity().getName() + " and "
							+ association.getTargetEntity().getName());
					ERRORS
							.add("This cannot be updated programatically. "
									+ "Please export the model using export XMI and correct the many-many association");
					UpdateHelper.printErrorMessages(XMIImportValidator.ERROR_LIST, ERRORS);
					ERRORS.add("");
				}
				associationNumber++;
			}
		}
		//Step 14. Check for missing links in Containers.
		entityGroup.setMainContainerCollection(mainContainerCollection);

		//Step 15. Check for missing association
		List<EntityInterface> allEntities = new ArrayList<EntityInterface>();
		for (ContainerInterface container : mainContainerCollection)
		{
			allEntities.add((EntityInterface) container.getAbstractEntity());
		}
		EntityInterface hookEntity = XMIImportValidator.validateForMissingAssociation(allEntities,
				ERRORS);
		UpdateHelper.printErrorMessages(XMIImportValidator.ERROR_LIST, ERRORS);
		if (hookEntity != null)
		{
			//Step 16. Add missing association and add paths for those associations.
			SQLGenerator.updateMissingAssociationAndAddPath(allEntities, hookEntity, ERRORS,
					hibernateDao);
		}
	}

	/**
	 * Write to file.
	 * @param erroList the error list
	 */

	private static void writeToFile(List<String> erroList)
	{
		try
		{
			File logFile = new File("UpgradeLog.txt");
			System.out.println("Directory of Log FIle is : " + logFile.getAbsolutePath());
			FileWriter fileWriter = new FileWriter(logFile);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			for (String error : erroList)
			{
				writer.write(error);
				writer.write("\n");
			}
			writer.close();
		}
		catch (Exception e)
		{
			LOGGER.error("Error occured while writing log file" + e.getMessage());
		}
	}
}
