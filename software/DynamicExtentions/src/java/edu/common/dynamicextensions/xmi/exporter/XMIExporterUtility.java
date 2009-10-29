/*
 * Created on Sep 27, 2007
 * @author
 *
 */

package edu.common.dynamicextensions.xmi.exporter;

import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerConstantsInterface;
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
	public static void addHookEntitiesToGroup(String hookEntityName,
			EntityGroupInterface entityGroup) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		Collection<ContainerInterface> mainContainers = entityGroup.getMainContainerCollection();
		System.out.println("mainContainers.size(): " + mainContainers.size());
		EntityInterface xmiStaticEntity = null;
		EntityInterface staticEntity = getHookEntityByName(hookEntityName);
		xmiStaticEntity = getHookEntityDetailsForXMI(staticEntity);
		entityGroup.addEntity(xmiStaticEntity);
		xmiStaticEntity.setEntityGroup(entityGroup);
		for (ContainerInterface mainContainer : mainContainers)
		{
			AssociationInterface association = getHookEntityAssociation(staticEntity,
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
	private static EntityInterface getHookEntityDetailsForXMI(EntityInterface srcEntity)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		//For XMI : add only id , name and table properties
		EntityInterface xmiEntity = new Entity();
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
	private static String getHookEntityName(String name)
	{
		//Return last token from name
		String hookEntityname = null;
		StringTokenizer strTokenizer = new StringTokenizer(name, ".");
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
	private static AssociationInterface getHookEntityAssociation(EntityInterface srcEntity,
			EntityInterface targetEntity) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		AssociationInterface association = null;
		Collection<AssociationInterface> associations = srcEntity.getAllAssociations();
		for (AssociationInterface staticAssociation : associations)
		{
			if (staticAssociation.getTargetEntity().equals(targetEntity))
			{
				String srcEntityName = getHookEntityName(srcEntity.getName());
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
	public static AttributeInterface getIdAttribute(EntityInterface entity)
	{
		if (entity != null)
		{
			Collection<AttributeInterface> attributes = entity.getAllAttributes();
			if (attributes != null)
			{
				Iterator attributesIter = attributes.iterator();
				while (attributesIter.hasNext())
				{
					AttributeInterface attribute = (AttributeInterface) attributesIter.next();
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
	public static EntityInterface getHookEntityByName(String name)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		EntityInterface entity = EntityManager.getInstance().getEntityByName(name);
		if (entity == null)
		{
			throw new DynamicExtensionsApplicationException("Static Entity With Name " + name
					+ " Not Found");
		}
		return entity;
	}

}
