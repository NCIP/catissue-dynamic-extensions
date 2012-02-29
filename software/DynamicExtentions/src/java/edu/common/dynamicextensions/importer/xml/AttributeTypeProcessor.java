
package edu.common.dynamicextensions.importer.xml;

import java.util.Date;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.importer.jaxb.Attribute;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.wustl.dynamicextensions.caching.metadata.ClassMetadata;
import edu.wustl.dynamicextensions.caching.metadata.PropertyMetadata;

/**
 * @author Kunal
 * Processes the Attribute tag from simplifiedMetadataXml.xsd
 *
 */
public class AttributeTypeProcessor
{

	/**
	 * DE entity used for persisting metadata 
	 */
	private EntityInterface entity;

	private AttributeInterface attributeInterface;

	private PropertyMetadata propertyMetadata;

	/**
	 * Hibernate metadata of the class
	 */
	private ClassMetadata classMetadata;

	public AttributeTypeProcessor(EntityInterface entity, ClassMetadata classMetadata)
	{
		this.entity = entity;
		this.classMetadata = classMetadata;
	}

	public void process(Attribute attribute) throws DynamicExtensionsApplicationException
	{
		//Step 1: Creates if attribute with name is not found in the entity
		if(Constants.DISABLED.equals(attribute.getActivityStatus()))
		{
			attributeInterface = getAttribute(attribute);
			return;
		}else
		{
			attributeInterface = createAttribute(attribute);	
		}

		//Step 2: Update entity references
		entity.addAttribute(attributeInterface);
		attributeInterface.setEntity(entity);

		//Step 3: Update the name in the Attribute class
		attributeInterface.setName(attribute.getName());

		//Step 4: Set default values if any, these values are note provided in the xml
		setDefault(attribute);

		//Step 5: Set all the primitive attributes of the Attribute class
		setPrimitiveAttributes();

		//Step 6: Set the column name.
		setColumnProperties();

	}

	private AttributeInterface createAttribute(Attribute attribute)
			throws DynamicExtensionsApplicationException
	{
		propertyMetadata = classMetadata.getIdMetadata();
		if (propertyMetadata.getPropertyName().equals(attribute.getName()))
		{
			attributeInterface = getAttribute(attribute);
			entity.getPrimaryKeyAttributeCollection().clear();
			entity.addPrimaryKeyAttribute(attributeInterface);
			attributeInterface.setIsNullable(Boolean.FALSE);
			attributeInterface.setIsPrimaryKey(true);
		}
		else
		{

			propertyMetadata = classMetadata.getProperty(attribute.getName());
			if (propertyMetadata != null)
			{
				attributeInterface = getAttribute(attribute);
			}
			else
			{
				throw new DynamicExtensionsApplicationException("Attribute " + attribute.getName()
						+ " does not exist in hbm.");
			}
		}
		return attributeInterface;
	}

	private AttributeInterface getAttribute(Attribute attribute)
			throws DynamicExtensionsApplicationException
	{
		AttributeInterface attributeInterface = entity.getAttributeByName(attribute.getName());
		if (attributeInterface == null || !attributeInterface.getEntity().equals(entity))
		{
			attributeInterface = DomainObjectFactory.getInstance().createAttribute(
					propertyMetadata.getPropertyType());
		}else
		{
			if(entity.getEntityGroup().getIsSystemGenerated())
			{
				attributeInterface.setActivityStatus(attribute.getActivityStatus());
			}else
			{
				attributeInterface.setActivityStatus(Constants.ACTIVE);	
			}
		}

		return attributeInterface;
	}

	private void setColumnProperties()
	{
		attributeInterface.getColumnProperties().setName(propertyMetadata.getColumnName());
	}

	private void setPrimitiveAttributes()
	{

	}

	private void setDefault(Attribute attribute)
	{
		attributeInterface.setCreatedDate(new Date());
	}

}
