
package edu.common.dynamicextensions.upgrade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.common.dynamicextensions.entitymanager.DynamicExtensionBaseQueryBuilder;
import edu.common.dynamicextensions.entitymanager.QueryBuilderFactory;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationDirection;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.common.dynamicextensions.xmi.DynamicQueryList;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;

/**
 * The Class SQLGenerator.
 */
public class SQLGenerator
{

	/** The all entity group and association names. */
	protected static Collection<String> allEntityGroupAndAssociationNames = new HashSet<String>();

	/** The all attribute names. */
	protected static Collection<String> allAttributeNames = new HashSet<String>();

	/**
	 * Generate entity group name.
	 * @param entityGroup the entity group
	 * @param entityGroupNumber the entity group number
	 * @param consolidatedErrorList
	 * @return the string
	 */
	public static void updateEntityGroupName(EntityGroupInterface entityGroup,
			int entityGroupNumber, List<String> consolidatedErrorList)
	{
		String entityGroupName;
		if (entityGroup.getName() == null || entityGroup.getName().length() == 0)
		{
			entityGroupName = UpgradeConstants.ENTITY_GROUP_NAME + entityGroupNumber;
			consolidatedErrorList.add("Updating Entity Group name having id :"
					+ entityGroup.getId() + " with '" + entityGroupName + "'");
		}
		else
		{
			entityGroupName = entityGroup.getName().replaceAll("[\\W]", "") + "EntityGroup";
			consolidatedErrorList.add("Updating Entity Group name '" + entityGroup.getName()
					+ " with '" + entityGroupName + "'");
		}
		entityGroup.setName(entityGroupName);
	}

	/**
	 * Generate entity group short and long name.
	 * @param entityGroup the entity group
	 * @param entityGroupNumber the entity group number
	 * @param consolidatedErrorList the consolidated error list
	 * @return the string
	 */
	public static void generateEntityGroupShortAndLongName(EntityGroupInterface entityGroup,
			int entityGroupNumber, List<String> consolidatedErrorList)
	{
		String entityGroupName = UpgradeConstants.ENTITY_GROUP_NAME + entityGroupNumber;
		entityGroup.setShortName(entityGroup.getName());
		entityGroup.setLongName(entityGroup.getName());
		consolidatedErrorList.add("Updating Entity Group Long/Short name having id :"
				+ entityGroup.getId() + " with '" + entityGroup.getName() + "'");
	}

	/**
	 * Generate package name.
	 * @param entityGroup the entity group
	 * @param entityGroupNumber the entity group number
	 * @param consolidatedErrorList
	 */
	public static void generatePackageName(EntityGroupInterface entityGroup, int entityGroupNumber,
			List<String> consolidatedErrorList)
	{
		String entityGroupPackageName = UpgradeConstants.ENTITY_GROUP_PACKAGE_NAME
				+ entityGroupNumber;
		if (entityGroup.getTaggedValueCollection() == null)
		{
			Collection<TaggedValueInterface> taggedValueCollection = new HashSet<TaggedValueInterface>();
			entityGroup.setTaggedValueCollection(taggedValueCollection);
			TaggedValueInterface taggedValue = DomainObjectFactory.getInstance()
					.createTaggedValue();
			taggedValue.setKey(UpgradeConstants.PACKAGE_NAME_KEY);
			taggedValue.setValue(entityGroupPackageName);
			entityGroup.getTaggedValueCollection().add(taggedValue);
		}
		else
		{
			Boolean packageNameExists = Boolean.FALSE;
			for (TaggedValueInterface taggedValue : entityGroup.getTaggedValueCollection())
			{
				if (taggedValue.getKey().equalsIgnoreCase(UpgradeConstants.PACKAGE_NAME_KEY))
				{
					taggedValue.setValue(entityGroupPackageName);
					packageNameExists = Boolean.TRUE;
				}
			}
			if (!packageNameExists)
			{
				TaggedValueInterface taggedValue = DomainObjectFactory.getInstance()
						.createTaggedValue();
				taggedValue.setKey(UpgradeConstants.PACKAGE_NAME_KEY);
				taggedValue.setValue(entityGroupPackageName);
				entityGroup.getTaggedValueCollection().add(taggedValue);
			}
		}
		consolidatedErrorList.add("Updating Entity Group package name having id :"
				+ entityGroup.getId() + " to '" + entityGroupPackageName + "'");
	}

	/**
	 * Update package name.
	 * @param entityGroup the entity group
	 * @param packageName the package name
	 * @param consolidatedErrorList the consolidated error list
	 */
	public static void updatePackageName(EntityGroupInterface entityGroup, String packageName,
			List<String> consolidatedErrorList)
	{
		String entityGroupPackageName = packageName.replaceAll("[\\W]", "") + "PackageName";
		for (TaggedValueInterface taggedValue : entityGroup.getTaggedValueCollection())
		{
			if (taggedValue.getKey().equalsIgnoreCase(UpgradeConstants.PACKAGE_NAME_KEY))
			{
				taggedValue.setValue(entityGroupPackageName);
				break;
			}
		}
		consolidatedErrorList.add("Updating Entity Group package name having id :"
				+ entityGroup.getId() + " to '" + entityGroupPackageName + "'");
	}

	/**
	 * Update entity name.
	 * @param entity the entity
	 * @param entityNumber
	 * @param consolidatedErrorList
	 */
	public static void updateEntityName(EntityInterface entity, int entityNumber,
			List<String> consolidatedErrorList)
	{
		String oldEntityName = entity.getName();
		if (oldEntityName == null)
		{

			String updatedEntityName = UpgradeConstants.ENTITY_NAME + entityNumber;
			entity.setName(updatedEntityName);
			consolidatedErrorList.add("Updating Entity name having id:" + entity.getId() + " to '"
					+ updatedEntityName + "'");
		}
		else
		{
			String updatedEntityName = oldEntityName.replaceAll("[\\W]", "");
			while (true)
			{
				char character = updatedEntityName.charAt(0);
				if (Character.isDigit(character))
				{
					updatedEntityName = updatedEntityName.substring(1, updatedEntityName.length());
				}
				else
				{
					break;
				}
			}
			updatedEntityName = StringUtils.capitalize(updatedEntityName);
			entity.setName(updatedEntityName);
			consolidatedErrorList.add("Updating Entity Name '" + oldEntityName + "' to '"
					+ updatedEntityName + "'");
		}
	}

	/**
	 * Update attribute name.
	 * @param attribute the attribute
	 * @param attributeNumber
	 * @param consolidatedErrorList
	 */
	public static void updateAttributeName(AttributeInterface attribute, int attributeNumber,
			List<String> consolidatedErrorList)
	{
		String oldAttributeName = attribute.getName();
		if (oldAttributeName == null)
		{
			String updatedAttributeName = UpgradeConstants.ATTRIBUTE_NAME + attributeNumber;
			attribute.setName(updatedAttributeName);
			consolidatedErrorList.add("Updating Attribute Name having id:" + attribute.getId()
					+ " to '" + updatedAttributeName + "'");
		}
		else
		{
			String finalAttributeName = getUpdatedName(oldAttributeName);
			if (!allAttributeNames.add(finalAttributeName))
			{
				finalAttributeName = finalAttributeName + attributeNumber;
			}
			attribute.setName(finalAttributeName);
			consolidatedErrorList.add("Updating Attribute Name '" + oldAttributeName + "' to '"
					+ finalAttributeName + "'");
		}
	}

	/**
	 * Update association name.
	 * @param association the association
	 * @param associationNumber the association number
	 * @param appendNumber the append number
	 * @param consolidatedErrorList
	 */
	public static void updateAssociationName(AssociationInterface association,
			int associationNumber, boolean appendNumber, List<String> consolidatedErrorList)
	{
		String oldAssociationName = association.getName();
		if (oldAssociationName == null)
		{
			String updatedAttributeName = UpgradeConstants.ATTRIBUTE_NAME + associationNumber;
			association.setName(updatedAttributeName);
			consolidatedErrorList.add("Updating Association Name having id:" + association.getId()
					+ " to '" + updatedAttributeName + "'");
		}
		else
		{
			String finalAssociationName = getUpdatedName(oldAssociationName);
			if (appendNumber)
			{
				finalAssociationName = finalAssociationName + associationNumber;
			}
			association.setName(finalAssociationName.toString());
			consolidatedErrorList.add("Updating Association Name '" + oldAssociationName + "' to '"
					+ finalAssociationName + "'");
		}
	}

	/**
	 * Update association source role name.
	 * @param sourceRole the source role
	 * @param entity the entity
	 * @param string
	 * @param associationNumber
	 * @param consolidatedErrorList
	 */
	public static void updateAssociationSourceRoleName(RoleInterface sourceRole,
			EntityInterface entity, String associationName, int associationNumber,
			List<String> consolidatedErrorList)
	{
		String oldSourceRoleName = sourceRole.getName();
		if (oldSourceRoleName == null || oldSourceRoleName.length() == 0)
		{
			String updatedSourceRoleName = UpgradeConstants.SOURCE_ROLE_NAME + entity.getName()
					+ associationNumber;
			sourceRole.setName(updatedSourceRoleName);
			consolidatedErrorList.add("Updating source role name of association '"
					+ associationName + "' to '" + updatedSourceRoleName + "'");
		}
		else
		{
			String finalAssociationSourceRoleName = getUpdatedName(oldSourceRoleName);
			sourceRole.setName(finalAssociationSourceRoleName.toString());
			consolidatedErrorList.add("Updating source role name '" + oldSourceRoleName
					+ "' of association '" + associationName + "' to '"
					+ finalAssociationSourceRoleName + "'");
		}
	}

	/**
	 * Update association target role name.
	 * @param targetRole the target role
	 * @param targetEntity the target entity
	 * @param associationName the association name
	 * @param associationNumber the association number
	 * @param consolidatedErrorList
	 */
	public static void updateAssociationTargetRoleName(RoleInterface targetRole,
			EntityInterface targetEntity, String associationName, int associationNumber,
			List<String> consolidatedErrorList)
	{
		String oldTargetRoleName = targetRole.getName();
		if (oldTargetRoleName == null || oldTargetRoleName.length() == 0)
		{
			String updatedTargetRoleName = UpgradeConstants.TARGET_ROLE_NAME
					+ targetEntity.getName() + associationNumber;
			targetRole.setName(updatedTargetRoleName);
			consolidatedErrorList.add("Updating target role name of association '"
					+ associationName + "' to '" + updatedTargetRoleName + "'");
		}
		else
		{
			String finalAssociationTargetRoleName = getUpdatedName(oldTargetRoleName);
			targetRole.setName(finalAssociationTargetRoleName.toString());
			consolidatedErrorList.add("Updating target role name '" + oldTargetRoleName
					+ "' of association '" + associationName + "' to '"
					+ finalAssociationTargetRoleName + "'");
		}
	}

	/**
	 * Update missing association in model.
	 *
	 * @param allEntities the all entities
	 * @param staticHookEntity the static hook entity
	 * @param consolidatedErrorList the consolidated error list
	 * @param hibernateDao
	 * @throws DAOException
	 * @throws DynamicExtensionsSystemException
	 */
	public static void updateMissingAssociationAndAddPath(List<EntityInterface> allEntities,
			EntityInterface staticHookEntity, List<String> consolidatedErrorList,
			HibernateDAO hibernateDao) throws DAOException, DynamicExtensionsSystemException
	{
		for (AssociationInterface association : staticHookEntity.getAllAssociations())
		{
			if (allEntities.contains(association.getTargetEntity()))
			{
				allEntities.remove(association.getTargetEntity());
			}
		}
		if (!allEntities.isEmpty())
		{
			int associationNumber = 1;

			List<String> queriesList = new ArrayList<String>();
			List<String> revQueryList = new ArrayList<String>();
			DynamicQueryList dynamicQueryList = new DynamicQueryList();
			dynamicQueryList.setQueryList(queriesList);
			dynamicQueryList.setRevQueryList(revQueryList);
			Stack<String> rlbkQryStack = new Stack<String>();
			try
			{
				for (EntityInterface entityInterface : allEntities)
				{
					consolidatedErrorList.add("Adding association for " + entityInterface.getName()
							+ " with static entity " + staticHookEntity.getName());
					AssociationInterface association = DomainObjectFactory.getInstance()
							.createAssociation();
					association.setEntity(staticHookEntity);
					association.setTargetEntity(entityInterface);
					association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
					association.setCreatedDate(new Date());
					association.setName(UpgradeConstants.ASSOCIATION_NAME + associationNumber);

					RoleInterface sourceRole = DomainObjectFactory.getInstance().createRole();
					sourceRole.setAssociationsType(AssociationType.CONTAINTMENT);
					sourceRole.setMinimumCardinality(Cardinality.ZERO);
					sourceRole.setMaximumCardinality(Cardinality.ONE);
					sourceRole.setName(UpgradeConstants.SOURCE_ROLE_NAME
							+ staticHookEntity.getName() + associationNumber);

					RoleInterface targetRole = DomainObjectFactory.getInstance().createRole();
					targetRole.setAssociationsType(AssociationType.CONTAINTMENT);
					targetRole.setMinimumCardinality(Cardinality.ZERO);
					targetRole.setMaximumCardinality(Cardinality.MANY);
					targetRole.setName(UpgradeConstants.TARGET_ROLE_NAME
							+ entityInterface.getName() + associationNumber);

					ConstraintPropertiesInterface constraintProperty = DomainObjectFactory
							.getInstance().createConstraintProperties();
					constraintProperty.setName(entityInterface.getTableProperties().getName());

					for (AttributeInterface primaryAttr : staticHookEntity
							.getPrimaryKeyAttributeCollection())
					{
						constraintProperty.getTgtEntityConstraintKeyProperties()
								.getTgtForiegnKeyColumnProperties().setName(
										"DYEXTN_AS_" + staticHookEntity.getId().toString() + "_"
												+ entityInterface.getId().toString());
						constraintProperty.getTgtEntityConstraintKeyProperties()
								.setSrcPrimaryKeyAttribute(primaryAttr);
					}
					constraintProperty.getSrcEntityConstraintKeyPropertiesCollection().clear();
					association.setConstraintProperties(constraintProperty);
					association.setSourceRole(sourceRole);
					association.setTargetRole(targetRole);
					staticHookEntity.addAssociation(association);

					//Adding path for newly created association.
					queriesList.addAll(QueryBuilderFactory.getQueryBuilder()
							.getQueryPartForAssociation(association, revQueryList, true));
				}
				associationNumber++;
				hibernateDao.update(staticHookEntity);
				if (dynamicQueryList != null)
				{
					DynamicExtensionBaseQueryBuilder queryBuilder = QueryBuilderFactory
							.getQueryBuilder();
					queryBuilder.executeQueries(dynamicQueryList.getQueryList(), dynamicQueryList
							.getRevQueryList(), rlbkQryStack);
				}
			}
			catch (DynamicExtensionsSystemException e)
			{
				UpdateHelper.rollbackQueries(rlbkQryStack, e, hibernateDao);
				consolidatedErrorList
						.add("Error occured while adding association " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the updated name.
	 * @param oldName the old name
	 * @return the updated name
	 */
	private static String getUpdatedName(String oldName)
	{
		String updatedName = oldName.replaceAll("[\\W]", "");
		while (true)
		{
			char character = updatedName.charAt(0);
			if (Character.isDigit(character))
			{
				updatedName = updatedName.substring(1, updatedName.length());
			}
			else
			{
				break;
			}
		}
		char character = updatedName.charAt(0);
		char updatedFirstChar = Character.toLowerCase(character);
		character = updatedName.charAt(1);
		char updatedSecondChar = Character.toLowerCase(character);
		StringBuffer finalUpdatedName = new StringBuffer();
		finalUpdatedName.append(updatedFirstChar).append(updatedSecondChar).append(
				updatedName.substring(2, updatedName.length()));
		return finalUpdatedName.toString();
	}
}
