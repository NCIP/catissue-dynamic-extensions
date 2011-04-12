
package edu.common.dynamicextensions.upgrade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.AbstractBaseMetadataManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.dao.query.generator.DBTypes;
import edu.wustl.dao.util.NamedQueryParam;

/**
 * @author gaurav_mehta
 * The Class XMIImportValidator.
 *
 */
public class XMIImportValidator
{

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(XMIImportValidator.class);

	/** List to maintain the validation errors. */
	public final static List<String> ERROR_LIST = new ArrayList<String>();

	/**
	 * Validate entity groupname.
	 * @param entityGroup the entity group
	 */
	protected static void validateEntityGroupname(EntityGroupInterface entityGroup)
	{
		ERROR_LIST.clear();
		StringBuilder errorMessage = new StringBuilder();
		if (entityGroup.getName() == null || entityGroup.getName().length() == 0 )
		{
			errorMessage.append("Entity Group Name is null for entity group Id : ")
					.append(entityGroup.getId());
			ERROR_LIST.add(errorMessage.toString());
		}
		if (entityGroup.getName().matches(UpgradeConstants.SPECIAL_CHARACTERS_REG_EX))
		{
			ERROR_LIST.add("Entity Group name '" + entityGroup.getName() + "' has special characters.");
		}
	}

	/**
	 * Validate entity groupname.
	 * @param entityGroup the entity group
	 */
	protected static void validateEntityGroupShortAndLongname(EntityGroupInterface entityGroup)
	{
		ERROR_LIST.clear();
		StringBuilder errorMessage = new StringBuilder();
		if (entityGroup.getLongName() == null || entityGroup.getShortName() == null)
		{
			errorMessage.append("Entity Group Long/Short Name is null for entity group Id : ")
					.append(entityGroup.getId());
			ERROR_LIST.add(errorMessage.toString());
		}
	}

	/**
	 * Validate for cycle in entity group.
	 * @param entityGroupInterface the entity group interface
	 */
	protected static void validateForCycleInEntityGroup(EntityGroupInterface entityGroupInterface)
	{
		ERROR_LIST.clear();
		for (EntityInterface entityInterface : entityGroupInterface.getEntityCollection())
		{
			List<String> sourceEntityCollection = new ArrayList<String>();
			checkForCycleStartsAtEntity(entityInterface, sourceEntityCollection);
		}
	}

	/**
	 * Validate package name.
	 * @param packageName package name
	 */
	@SuppressWarnings("unchecked")
	protected static void validateForDuplicatePackageName(String packageName)
	{
		ERROR_LIST.clear();
		try
		{
			Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
			substParams.put("0", new NamedQueryParam(DBTypes.STRING, packageName));

			List<String> names = (List<String>) AbstractBaseMetadataManager.executeHQL(
					"getEntityGroupNameByPackageName", substParams);
			if (!names.isEmpty() && names.size() > 1)
			{
				ERROR_LIST
						.add("Change the package name, as more than one model has the same package name.");
			}
		}
		catch (DynamicExtensionsSystemException dese)
		{
			LOGGER.error("Error occured while performing package name validations"
					+ dese.getMessage());
		}
	}

	/**
	 * Validate correctness of package name.
	 * @param packageName the package name
	 */
	protected static void validateCorrectnessOfPackageName(String packageName)
	{
		ERROR_LIST.clear();
		validateForNameStartingWithSpecialCharacterAndNumericCharacter(packageName, "Package Name");
	}

	/**
	 * Validate the class name for cacore compatibility.
	 * @param entity the name
	 */
	public static void validateClassName(EntityInterface entity)
	{
		ERROR_LIST.clear();
		String name = entity.getName();
		if (name == null)
		{
			ERROR_LIST.add("Entity name for entity id:" + entity.getId() + " is null.");
		}
		else
		{
			if (!name.matches(UpgradeConstants.START_WITH_UPPER_CASE_REG_EX))
			{
				ERROR_LIST.add("Entity name '" + entity
						+ "' does not start with upper case character.");
			}
			validateForNameStartingWithSpecialCharacterAndNumericCharacter(name, "Class");
		}
	}

	/**
	 * Validate attribute name.
	 * @param attribute the name
	 * @param artifact the artifact
	 */
	public static void validateAttributeName(AttributeInterface attribute)
	{
		ERROR_LIST.clear();
		String name = attribute.getName();
		if (name == null)
		{
			ERROR_LIST.add("Attribute name for attribute id:" + attribute.getId() + " is null.");
		}
		else
		{
			if (!name.matches(UpgradeConstants.START_WITH_TWO_LOWER_CASE_LETTERS_REG_EX))
			{
				ERROR_LIST.add("Attribute name '" + name
						+ "'does not have first two letters in lower case.");
			}
			validateForNameStartingWithSpecialCharacterAndNumericCharacter(name, "Attribute");
		}
	}

	/**
	 * Validate association name.
	 * @param association the association
	 */
	public static void validateAssociationName(AssociationInterface association)
	{
		ERROR_LIST.clear();
		String name = association.getName();
		if (name == null)
		{
			ERROR_LIST.add("Association name for association id:" + association.getId()
					+ " is null");
		}
		else
		{
			if (!name.matches(UpgradeConstants.START_WITH_TWO_LOWER_CASE_LETTERS_REG_EX))
			{
				ERROR_LIST.add("Association name '" + name
						+ "'does not have first two letters in lower case.");
			}
			validateForNameStartingWithSpecialCharacterAndNumericCharacter(name, "Association");
		}
	}

	/**
	 * Validate association name.
	 * @param association the association
	 * @param collection
	 */
	public static void validateForAssociationNameSameAsAttributeName(
			AssociationInterface association, Collection<AttributeInterface> allAttributes)
	{
		ERROR_LIST.clear();
		String name = association.getName();
		for (AttributeInterface attribute : allAttributes)
		{
			if (attribute.getName().equalsIgnoreCase(name))
			{
				ERROR_LIST.add("Association name '" + name + "' matches with attribute name '"
						+ attribute.getName());
			}
		}
	}

	/**
	 * Validate association source role name.
	 * @param sourceRole the source role
	 * @param association the association
	 */
	public static void validateAssociationSourceRoleName(RoleInterface sourceRole,
			AssociationInterface association)
	{
		ERROR_LIST.clear();
		String name = sourceRole.getName();
		if (name == null)
		{
			ERROR_LIST.add("Assocaition " + association.getName() + ",source Role name is null");
		}
		else
		{
			if (!name.matches(UpgradeConstants.START_WITH_TWO_LOWER_CASE_LETTERS_REG_EX))
			{
				ERROR_LIST
						.add("Source role name '" + name + "' for association '"
								+ association.getName()
								+ "'does not have first two letters in lower case.");
			}
			validateForNameStartingWithSpecialCharacterAndNumericCharacter(name, association
					.getName()
					+ " association's source role");
		}
	}

	/**
	 * Validate association target role name.
	 * @param targetRole the source role
	 * @param association the association
	 */
	public static void validateAssociationTargetRoleName(RoleInterface targetRole,
			AssociationInterface association)
	{
		ERROR_LIST.clear();
		String name = targetRole.getName();
		if (name == null)
		{
			ERROR_LIST.add("Assocaition " + association.getName() + ",target Role name is null");
		}
		else
		{
			if (!name.matches(UpgradeConstants.START_WITH_TWO_LOWER_CASE_LETTERS_REG_EX))
			{
				ERROR_LIST
						.add("Target role name '" + name + "' for association '"
								+ association.getName()
								+ "'does not have first two letters in lower case.");
			}
			validateForNameStartingWithSpecialCharacterAndNumericCharacter(name, association
					.getName()
					+ " association's target role");
		}
	}

	/**
	 * Validate the cardinality for cacore compatibility.
	 *
	 * @param sourceRole the source role
	 * @param targetRole the target role
	 * @param sourceEntityName the source entity name
	 * @param targetEntityName the target entity name
	 */
	public static void validateCardinality(RoleInterface sourceRole, RoleInterface targetRole,
			String sourceEntityName, String targetEntityName)
	{
		ERROR_LIST.clear();
		Cardinality sourceMaxCardinality = sourceRole.getMaximumCardinality();
		Cardinality targetMaxCardinality = targetRole.getMaximumCardinality();
		if (sourceMaxCardinality == null || targetMaxCardinality == null)
		{
			ERROR_LIST.add("Either Source Role or Target Role max cardinality is null for "
					+ " [Source Entity:" + sourceEntityName + ", Target Entity: "
					+ targetEntityName + "]");
		}
		else
		{
			String sourceMaxCardinalityName = sourceMaxCardinality.name();
			String targetMaxCardinalityName = targetMaxCardinality.name();

			if (Cardinality.MANY.toString().equals(sourceMaxCardinalityName)
					&& Cardinality.MANY.toString().equals(targetMaxCardinalityName))
			{
				ERROR_LIST.add("Either Many-to-Many association present OR Target Role is empty. "
						+ "[Source Entity:" + sourceEntityName + ", Target Entity: "
						+ targetEntityName + "]");
			}
		}
	}

	/**
	 * Validate container collection for entity group.
	 * @param mainContainerCollection the main container collection
	 * @param entityGroup the entity group
	 */
	public static void validateContainerCollectionForEntityGroup(
			Collection<ContainerInterface> mainContainerCollection, EntityGroupInterface entityGroup)
	{
		Iterator<ContainerInterface> containerIterator = mainContainerCollection.iterator();
		while (containerIterator != null)
		{
			ContainerInterface container = containerIterator.next();
			EntityGroupInterface fetchedEntityGroup = ((EntityInterface) (container
					.getAbstractEntity())).getEntityGroup();
			if (!fetchedEntityGroup.equals(entityGroup))
			{
				containerIterator.remove();
			}
		}
	}

	/**
	 * Validate the name for cacore compatibility.
	 * @param name the name
	 * @param artifact the artifact
	 */
	private static void validateForNameStartingWithSpecialCharacterAndNumericCharacter(String name,
			String artifact)
	{
		if (name.matches(UpgradeConstants.SPECIAL_CHARACTERS_REG_EX))
		{
			ERROR_LIST.add(artifact + " name '" + name + "' has special characters.");
		}
		if (name.matches(UpgradeConstants.START_WITH_NUMBER_REG_EX))
		{
			ERROR_LIST.add(artifact + " name '" + name + "' starts with numeric character.");
		}
	}

	/**
	 * Check for cycle starts at entity.
	 * @param entityInterface the entity interface
	 * @param sourceEntityCollection the source entity collection
	 * @throws DynamicExtensionsSystemException 	 */
	private static void checkForCycleStartsAtEntity(EntityInterface entityInterface,
			List<String> sourceEntityCollection)
	{
		ERROR_LIST.clear();
		for (AssociationInterface associationInterface : entityInterface.getAssociationCollection())
		{
			if (associationInterface.getIsSystemGenerated())
			{
				continue;
			}
			if (sourceEntityCollection.contains(entityInterface.getName()))
			{
				ERROR_LIST.add("CYCLE CREATED IN THE MODEL: "
						+ getEntityNameAsPath(sourceEntityCollection));
				break;
				/*throw new DynamicExtensionsSystemException("CYCLE CREATED IN THE MODEL:-"
						+ getEntityNameAsPath(sourceEntityCollection));*/
			}
			else
			{
				sourceEntityCollection.add(entityInterface.getName());
				checkForCycleStartsAtEntity(associationInterface.getTargetEntity(),
						sourceEntityCollection);
			}
			if (!sourceEntityCollection.isEmpty())
			{
				sourceEntityCollection.remove(sourceEntityCollection.get(sourceEntityCollection
						.size() - 1));
			}
		}
	}

	/**
	 * Gets the entity name as path.
	 *
	 * @param sourceEntityCollection the source entity collection
	 *
	 * @return the entity name as path
	 */
	private static String getEntityNameAsPath(List<String> sourceEntityCollection)
	{
		StringBuffer path = new StringBuffer();
		for (String entity : sourceEntityCollection)
		{
			path.append(entity).append("->");
		}
		path.append(new ArrayList<String>(sourceEntityCollection).get(0));
		return path.toString();
	}

	/**
	 * Validate for missing association.
	 * @param mainContainerCollection the main container collection
	 * @param errorList
	 */
	public static EntityInterface validateForMissingAssociation(List<EntityInterface> allEntities,
			List<String> errorList)
	{
		ERROR_LIST.clear();

		EntityCache cache = EntityCache.getInstance();
		EntityInterface scgRecordEntryEntity = cache.getEntityById(2183L);
		EntityInterface participantRecordEntryEntity = cache.getEntityById(2179L);
		EntityInterface specimenRecordEntryEntity = cache.getEntityById(2181L);
		Boolean staticHookEntityFound = false;
		EntityInterface staticHookEntity = null;

		for (AssociationInterface association : scgRecordEntryEntity.getAllAssociations())
		{
			if (allEntities.contains(association.getTargetEntity()))
			{
				staticHookEntity = scgRecordEntryEntity;
				staticHookEntityFound = true;
				break;
			}
		}

		if (!staticHookEntityFound)
		{
			for (AssociationInterface association : participantRecordEntryEntity.getAllAssociations())
			{
				if (allEntities.contains(association.getTargetEntity()))
				{
					staticHookEntity = participantRecordEntryEntity;
					staticHookEntityFound = true;
					break;
				}
			}

		}

		if (!staticHookEntityFound)
		{
			for (AssociationInterface association : specimenRecordEntryEntity.getAllAssociations())
			{
				if (allEntities.contains(association.getTargetEntity()))
				{
					staticHookEntity = specimenRecordEntryEntity;
					staticHookEntityFound = true;
					break;
				}
			}
		}
		if (staticHookEntity == null)
		{
			ERROR_LIST.add("None of the Entities have been hooked to any of the Static Entity.");
			ERROR_LIST.add("This cannot be done programatically.");
		}
		else
		{
			ERROR_LIST.add("Static Entity found is " + staticHookEntity.getName());
			ERROR_LIST.add("Associations missing :" + allEntities.isEmpty());
		}
		return staticHookEntity;
	}
}
