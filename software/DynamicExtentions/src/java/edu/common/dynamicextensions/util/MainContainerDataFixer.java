
package edu.common.dynamicextensions.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.server.util.DynamicExtensionUtility;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;

public class MainContainerDataFixer
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	private static final Logger LOGGER = Logger.getCommonLogger(MainContainerDataFixer.class);

	private final Map<String, EntityGroupInterface> staticEntityGroupMap = new HashMap<String, EntityGroupInterface>();
	JDBCDAO jdbcDao = DynamicExtensionsUtility.getJDBCDAO();

	public MainContainerDataFixer() throws DynamicExtensionsSystemException
	{
		jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
	}

	public void fixData() throws DynamicExtensionsSystemException
	{
		LOGGER.info("Now Fixing the Data.");
		HibernateDAO hibernateDao = DynamicExtensionsUtility.getHibernateDAO();
		try
		{

			Collection<EntityGroupInterface> entityGroupColl = DynamicExtensionUtility
					.getSystemGeneratedEntityGroups(hibernateDao);

			Set<EntityGroupInterface> dynamicEntityGroups = new HashSet<EntityGroupInterface>();
			for (EntityGroupInterface entityGroup : entityGroupColl)
			{
				if (entityGroup.getIsSystemGenerated())
				{
					staticEntityGroupMap.put(entityGroup.getName(), entityGroup);
				}
				else
				{
					dynamicEntityGroups.add(entityGroup);
				}
			}
			for (EntityGroupInterface entityGroup : dynamicEntityGroups)
			{
				for (ContainerInterface container : entityGroup.getMainContainerCollection())
				{
					EntityInterface entity = (EntityInterface) container.getAbstractEntity();
					fixTheDataForMainContainer(entity, entityGroup);
				}
			}
			jdbcDao.commit();
		}
		catch (DAOException e)
		{
			LOGGER.error("Data fixing failed.");
			throw new DynamicExtensionsSystemException("Error while updating the data", e);
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(hibernateDao);
			DynamicExtensionsUtility.closeDAO(hibernateDao);
		}
		LOGGER.info("Data fixing completed successfully.");
	}

	private void fixTheDataForMainContainer(EntityInterface entity, EntityGroupInterface entityGroup)
			throws DAOException
	{
		EntityInterface parentEntity = entity.getParentEntity();
		if (parentEntity != null)
		{
			for (Object object : parentEntity.getContainerCollection())
			{
				ContainerInterface parentContainer = (ContainerInterface) object;
				if (entityGroup.getMainContainerCollection().contains(parentContainer))
				{
					LOGGER.debug("Child entity " + entity + " parent entity " + parentEntity);
					// disassociate the data.
					AssociationInterface hookedAssociation = getHookedAssociation(parentEntity);
					//get the association with the static model
					String columnName = getAssociationColumnName(hookedAssociation);
					String sql = "update " + parentEntity.getTableProperties().getName() + " set "
							+ columnName + " = null where identifier in (select identifier from "
							+ entity.getTableProperties().getName() + " )";
					executeQuery(sql);
				}
			}

			fixTheDataForMainContainer(parentEntity, entityGroup);
		}
	}

	private void executeQuery(String sql) throws DAOException
	{
		LOGGER.debug(sql);
		jdbcDao.executeUpdate(sql);
	}

	private String getAssociationColumnName(AssociationInterface hookedAssociation)
	{
		return hookedAssociation.getConstraintProperties().getTgtEntityConstraintKeyProperties()
				.getTgtForiegnKeyColumnProperties().getName();
	}

	private AssociationInterface getHookedAssociation(EntityInterface entity)
	{
		AssociationInterface hookAssociation = null;
		for (EntityGroupInterface entityGroup : staticEntityGroupMap.values())
		{
			for (EntityInterface staticEntity : entityGroup.getEntityCollection())
			{
				for (AssociationInterface association : staticEntity.getAssociationCollection())
				{
					if (association.getTargetEntity().equals(entity))
					{
						hookAssociation = association;
						break;
					}
				}
			}
		}
		return hookAssociation;
	}

	public static void main(String[] args) throws DynamicExtensionsSystemException
	{
		MainContainerDataFixer dataFixer = new MainContainerDataFixer();

		dataFixer.fixData();
	}
}
