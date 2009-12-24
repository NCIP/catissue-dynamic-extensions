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
import java.util.StringTokenizer;

import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerConstantsInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

/**
 * @author falguni_sachde
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XMIExporterUtility
{

	/**
	 * @param hookEntityName
	 * @param entityGroup
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public static void addHookEntitiesToGroup(final String hookEntityName,
			final EntityGroupInterface entityGroup) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
			{
		final Collection<ContainerInterface> mainContainers = entityGroup.getMainContainerCollection();
		System.out.println("mainContainers.size(): " + mainContainers.size());
		EntityInterface xmiStaticEntity = null;
		final EntityInterface staticEntity = getHookEntityByName(hookEntityName);
		xmiStaticEntity = getHookEntityDetailsForXMI(staticEntity);
		entityGroup.addEntity(xmiStaticEntity);
		xmiStaticEntity.setEntityGroup(entityGroup);
		for (final ContainerInterface mainContainer : mainContainers)
		{
			final AssociationInterface association = getHookEntityAssociation(staticEntity,
					(EntityInterface) mainContainer.getAbstractEntity());
			if (association == null)
			{
				throw new DynamicExtensionsApplicationException(hookEntityName
						+ " Hook Entity is not associated with the "
						+ mainContainer.getAbstractEntity());
			}
			else
			{
				System.out.println("Association = " + association);
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
		xmiEntity.setName(getHookEntityName(srcEntity.getName()));
		xmiEntity.setDescription(srcEntity.getDescription());
		xmiEntity.setTableProperties(srcEntity.getTableProperties());
		xmiEntity.setId(srcEntity.getId());
		xmiEntity.addAttribute(getIdAttribute(srcEntity));
		//	xmiEntity.addAssociation(getHookEntityAssociation(srcEntity,targetEntity));
		return xmiEntity;
	}

	/**
	 * @param name
	 * @return
	 */
	private static String getHookEntityName(final String name)
	{
		//Return last token from name
		String hookEntityname = null;
		final StringTokenizer strTokenizer = new StringTokenizer(name, ".");
		while (strTokenizer.hasMoreElements())
		{
			hookEntityname = strTokenizer.nextToken();
		}
		return hookEntityname;
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
				final String srcEntityName = getHookEntityName(srcEntity.getName());
				//Change name of association
				staticAssociation.setName("Assoc_" + srcEntityName + "_" + targetEntity.getName());
				staticAssociation.getSourceRole().setName(srcEntityName);
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
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	public static EntityInterface getHookEntityByName(final String name)
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
	public static String getHookEntityName(final EntityGroupInterface entityGroup)
			throws DynamicExtensionsSystemException
	{
		final EntityManagerInterface entityManager = EntityManager.getInstance();
		final List<ContainerInterface> mainContainerList = new ArrayList<ContainerInterface>(entityGroup.getMainContainerCollection());
		final EntityInterface targetEntity = (EntityInterface) mainContainerList.get(0).getAbstractEntity();
		final Collection<AssociationInterface> associationCollection = entityManager.getIncomingAssociations(targetEntity);
		String hookEntity = null;
		for(final AssociationInterface associationInterface: associationCollection)
		{
			if(associationInterface.getEntity().getEntityGroup().getIsSystemGenerated())
			{
				hookEntity = associationInterface.getEntity().getName();
				break;
			}
		}
		return hookEntity;
	}

}
