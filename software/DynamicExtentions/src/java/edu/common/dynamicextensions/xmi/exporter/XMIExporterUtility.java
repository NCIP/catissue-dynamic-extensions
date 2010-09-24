/*
 * Created on Sep 27, 2007
 * @author
 *
 */

package edu.common.dynamicextensions.xmi.exporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerConstantsInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.HibernateDAO;

/**
 * @author falguni_sachde
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XMIExporterUtility
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	private static final Logger LOGGER = Logger.getCommonLogger(XMIExporterUtility.class);

	/**
	 * @param hookEntityName
	 * @param entityGroup
	 * @param hibernatedao
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public static void addHookEntitiesToGroup(EntityInterface staticEntity,
			final EntityGroupInterface entityGroup) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		final Collection<ContainerInterface> mainContainers = entityGroup
				.getMainContainerCollection();
		LOGGER.info("mainContainers.size(): " + mainContainers.size());
		//EntityInterface xmiStaticEntity = null;
		EntityInterface xmiStaticEntity = getHookEntityDetailsForXMI(staticEntity);
		entityGroup.addEntity(xmiStaticEntity);
		xmiStaticEntity.setEntityGroup(entityGroup);
		for (final ContainerInterface mainContainer : mainContainers)
		{
			final AssociationInterface association = getHookEntityAssociation(staticEntity,
					(EntityInterface) mainContainer.getAbstractEntity());
			if (association == null)
			{
				throw new DynamicExtensionsApplicationException(staticEntity.getName()
						+ " Hook Entity is not associated with the "
						+ mainContainer.getAbstractEntity());
			}
			else
			{
				LOGGER.info("Association = " + association);
				xmiStaticEntity.addAssociation(association);
			}
		}

	}

	/**
	 * @param staticEntity
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	private static EntityInterface getHookEntityDetailsForXMI(final EntityInterface srcEntity)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		//For XMI : add only id , name and table properties
		final EntityInterface xmiEntity = new Entity();
		xmiEntity.setName(EntityManagerUtil.getHookEntityName(srcEntity.getName()));
		xmiEntity.setDescription(srcEntity.getDescription());
		xmiEntity.setTableProperties(srcEntity.getTableProperties());
		xmiEntity.setId(srcEntity.getId());
		xmiEntity.addAttribute(getIdAttribute(srcEntity));
		//	xmiEntity.addAssociation(getHookEntityAssociation(srcEntity,targetEntity));
		return xmiEntity;
	}

	/**
	 * @param srcEntity
	 * @param targetEntity
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	private static AssociationInterface getHookEntityAssociation(final EntityInterface srcEntity,
			final EntityInterface targetEntity) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		AssociationInterface association = null;
		final Collection<AssociationInterface> associations = srcEntity.getAllAssociations();
		for (final AssociationInterface staticAssociation : associations)
		{
			if (staticAssociation.getTargetEntity().equals(targetEntity))
			{
				final String srcEntityName = EntityManagerUtil.getHookEntityName(srcEntity
						.getName());
				//Change name of association
				staticAssociation.setName("Assoc_" + srcEntityName + "_" + targetEntity.getName());
				staticAssociation.getSourceRole().setName(
						EntityManagerUtil.getHookAssociationSrcRoleName(srcEntity, targetEntity));
				staticAssociation.getTargetRole().setName(targetEntity.getName() + "Collection");
				association = staticAssociation;
				break;
			}
		}

		return association;
	}

	/**
	 * @param entity
	 * @return
	 */
	public static AttributeInterface getIdAttribute(final EntityInterface entity)
	{
		if (entity != null)
		{
			final Collection<AttributeInterface> attributes = entity.getAllAttributes();
			if (attributes != null)
			{
				final Iterator attributesIter = attributes.iterator();
				while (attributesIter.hasNext())
				{
					final AttributeInterface attribute = (AttributeInterface) attributesIter.next();
					if ((attribute != null)
							&& (EntityManagerConstantsInterface.ID_ATTRIBUTE_NAME.equals(attribute
									.getName())))
					{
						return attribute;
					}
				}
			}
		}
		return null;
	}

	/**
	 * @param name
	 * @param hibernatedao
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	public static EntityInterface getHookEntityByName(final String name, HibernateDAO hibernatedao)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		final EntityInterface entity = EntityManager.getInstance().getEntityByName(name);
		if (entity == null)
		{
			throw new DynamicExtensionsApplicationException("Static Entity With Name " + name
					+ " Not Found");
		}
		return entity;
	}

	/**
	 * This method will automatically find the hook entity.
	 * @param entityGroup
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public static EntityInterface getHookEntityName(final EntityGroupInterface entityGroup)
			throws DynamicExtensionsSystemException
	{
		final EntityManagerInterface entityManager = EntityManager.getInstance();
		final List<ContainerInterface> mainContainerList = new ArrayList<ContainerInterface>(
				entityGroup.getMainContainerCollection());
		final EntityInterface targetEntity = (EntityInterface) mainContainerList.get(0)
				.getAbstractEntity();
		final Collection<AssociationInterface> associationCollection = entityManager
				.getIncomingAssociations(targetEntity);
		EntityInterface hookEntity = null;
		for (AssociationInterface associationInterface : associationCollection)
		{
			if (associationInterface.getEntity().getEntityGroup().getIsSystemGenerated())
			{
				hookEntity = associationInterface.getEntity();
				break;
			}
		}
		return hookEntity;
	}

}
