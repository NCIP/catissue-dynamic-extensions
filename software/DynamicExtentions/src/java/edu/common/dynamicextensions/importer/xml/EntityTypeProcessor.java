
package edu.common.dynamicextensions.importer.xml;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.importer.jaxb.Attribute;
import edu.common.dynamicextensions.importer.jaxb.Entity;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.wustl.dynamicextensions.caching.metadata.ClassMetadataMap;

/**
 * @author Kunal
 * Process the entity tag from simplifiedMetadatXml.xsd, with the metadata information coming from the hbm files
 *
 */
public class EntityTypeProcessor
{

	private ClassMetadataMap classMetadataMap;
	private EntityGroupInterface entityGroup;

	public EntityTypeProcessor(ClassMetadataMap classMetadataMap, EntityGroupInterface entityGroup)
	{
		this.classMetadataMap = classMetadataMap;
		this.entityGroup = entityGroup;

	}

	public void process(Collection<Entity> entities) throws DynamicExtensionsApplicationException
	{
		for (Entity entityType : entities)
		{
			//Step 1: create if entity with same name does not exist.
			EntityInterface entity = getEntity(entityType.getName());
			if(Constants.DISABLED.equals(entityType.getActivityStatus()))
			{
				processDisabledEntity(entityType, entity);
				return;
			}
			
			entityGroup.addEntity(entity);
			entity.setEntityGroup(entityGroup);
			
			//Step 2: Set default values 
			setDefaultValues(entity);
			
			//Step 3: Set primitive property of the 
			processPrimitiveProperties(entity);

			//Step 4: Set the table properties, from hbm file
			processTableProperties(entity);

			//Step 5: Process Attribute collection
			processAttributes(entity, entityType.getAttribute());
		}
	}

	private void processDisabledEntity(Entity entityType, EntityInterface entity)
	{
		if(entityGroup.getIsSystemGenerated())
		{
			entity.setActivityStatus(entityType.getActivityStatus());	
		}else
		{
			entity.setActivityStatus(Constants.ACTIVE);	
		}
	}

	private EntityInterface getEntity(String entityName)
	{
		EntityInterface entity = entityGroup.getEntityByName(entityName);
		if (entity == null)
		{
			entity = DomainObjectFactory.getInstance().createEntity();
			entity.setName(entityName);
		}

		return entity;
	}

	private void setDefaultValues(EntityInterface entity)
	{
		/*entity.setInheritanceStrategy(InheritanceStrategy.)*/
		
		entity.setCreatedDate(new Date());
		entity.setParentEntity(entityGroup.getEntityByName(classMetadataMap.getClassMetadata(entity.getName()).getParentClassName()));
	}

	private void processTableProperties(EntityInterface entity)
	{

		entity.getTableProperties().setName(classMetadataMap.getClassMetadata(entity.getName()).getTableName());

	}

	private void processPrimitiveProperties(EntityInterface entity)
	{
		entity.setAbstract(classMetadataMap.getClassMetadata(entity.getName()).isAbstractClass());

	}

	private void processAttributes(EntityInterface entity, List<Attribute> attributeList) throws DynamicExtensionsApplicationException
	{
		AttributeTypeProcessor attributeTypeProcessor = new AttributeTypeProcessor(entity,
				classMetadataMap.getClassMetadata(entity.getName()));
		for (Attribute attribute : attributeList)
		{
			attributeTypeProcessor.process(attribute);
		}
	}
}
