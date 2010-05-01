
package edu.wustl.common.querysuite.querableobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.TablePropertiesInterface;
import edu.common.dynamicextensions.util.global.DEConstants.InheritanceStrategy;
import edu.wustl.common.querysuite.querableobjectinterface.QueryableAttributeInterface;
import edu.wustl.common.querysuite.querableobjectinterface.QueryableObjectInterface;

/**
 * This object is wrapper on the Entity.
 * @author pavan_kalantri.
 *
 */
public class QueryableEntity extends AbstractQueryableObject
{

	private EntityInterface entity;

	/**
	 * parameterized constructor.
	 * @param entity
	 */
	public QueryableEntity(EntityInterface entity)
	{
		super(entity);
		this.entity = entity;
		this.isCategory = false;
	}

	/**
	 * default constructor
	 */
	public QueryableEntity()
	{
		this.isCategory = false;
	}

	/**
	 * It will return Collection of the QueryableObject which contains only the same object on which 
	 * it is called.
	 * @return
	 */
	public Collection<QueryableObjectInterface> getEntityCollection()
	{
		Collection<QueryableObjectInterface> entityCollection = new ArrayList<QueryableObjectInterface>();
		entityCollection.add(this);
		return entityCollection;
	}

	/**
	 * It will return the collection of attributes of the entity.
	 * @return collection attribute
	 */
	public Collection<QueryableAttributeInterface> getAttributeCollection()
	{
		Collection<QueryableAttributeInterface> attributeCollection = getQueryableAttributeColl();
		Collection<QueryableAttributeInterface> queryableAttributeColl = new HashSet<QueryableAttributeInterface>();
		for (QueryableAttributeInterface attribute : attributeCollection)
		{
			QueryableEntityAttribute entityAttribute = (QueryableEntityAttribute) attribute;
			if (!entityAttribute.isInheritedAttribute()
					&& entityAttribute.getActualEntity().getId().equals(this.entity.getId()))
			{
				queryableAttributeColl.add(attribute);
			}

		}
		return queryableAttributeColl;
	}

	/**
	 * It will return the attribute with the name equal to attributeName in its own attribute collection.
	 * @return attribute with name attributeName.
	 */
	public QueryableAttributeInterface getEntityAttributeByName(String attributeName)
	{
		// TODO Auto-generated method stub 
		return getQueryableAttributeByName(attributeName);
	}

	/**
	 * It will return the collection of attributes of the entity.
	 * @return collection of attributes.
	 */
	public Collection<QueryableAttributeInterface> getEntityAttributes()
	{
		return getAttributeCollection();

	}

	/**
	 * It will return the collection of attributes of the entity & its parent Entity & so on.
	 * @return collection of attributes.
	 */
	public Collection<QueryableAttributeInterface> getEntityAttributesForQuery()
	{
		return getAllAttributes();

	}

	/**
	 * It will return the collection of QueryableAttribute (attributes which are of its own
	 * & the attributes which are local copy of the inherited attributes).
	 * @return
	 */
	private Collection<QueryableAttributeInterface> getAttributeCollectionWithInheritedAttributes()
	{
		Collection<QueryableAttributeInterface> attributeCollection = getQueryableAttributeColl();
		Collection<QueryableAttributeInterface> queryableAttributeColl = new HashSet<QueryableAttributeInterface>();
		for (QueryableAttributeInterface attribute : attributeCollection)
		{
			QueryableEntityAttribute entityAttribute = (QueryableEntityAttribute) attribute;
			if (entityAttribute.getActualEntity().getId().equals(this.entity.getId()))
			{
				queryableAttributeColl.add(attribute);
			}

		}
		return queryableAttributeColl;
	}

	/**
	 * It will search weather the attribute with the attributeName is present in the entity
	 * @return true if attribute present else false.
	 */
	public boolean isAttributePresent(String attributeName)
	{
		boolean isPresent = false;
		for (QueryableAttributeInterface queryableAttribute : getAttributeCollectionWithInheritedAttributes())
		{
			if (queryableAttribute.getName().equals(attributeName))
			{
				isPresent = true;
				break;
			}
		}
		return isPresent;
	}

	/**
	 * It will return the description of the entity.
	 * @return description
	 */
	public String getDescription()
	{
		return entity.getDescription();

	}

	/**
	 * It will return the id of the entity.
	 * @return id
	 */
	public Long getId()
	{
		return entity.getId();
	}

	/**
	 * It will return the name of the entity.
	 * @return name of the entity
	 */
	public String getName()
	{
		return entity.getName();
	}

	/**
	 * It will check weather the tag with the given key is present on the Entity from which this
	 * queryableObject is created
	 * @return true if tag present else false. 
	 */
	public boolean isTagPresent(String key)
	{

		return (QueryableObjectUtility.getTaggedValue(entity, key).equals("")) ? false : true;
	}

	/**
	 * It will return the taggedValue Collection of the Entity.
	 * @return taggedValueCollection of the entity
	 */
	public Collection<TaggedValueInterface> getTaggedValueCollection()
	{
		// TODO Auto-generated method stub
		return entity.getTaggedValueCollection();
	}

	/**
	 * It will return the Entity from which this QueryableEntity is created.
	 * @return entity 
	 */
	public EntityInterface getEntity()
	{
		// TODO Auto-generated method stub
		return entity;
	}

	/**
	 * It will create and return the queryableObject of the parent Entity of the entity from 
	 * which this queryableEntity is created.
	 * @return
	 */
	public QueryableObjectInterface getParentEntity()
	{
		QueryableEntity parentEntity = null;
		if (entity.getParentEntity() != null)
		{
			parentEntity = new QueryableEntity(entity.getParentEntity());
		}
		return parentEntity;
	}

	/**
	 * It will return the inheritance strategy of the entity.
	 * @return  
	 */
	public InheritanceStrategy getInheritanceStrategy()
	{
		// TODO Auto-generated method stub
		return entity.getInheritanceStrategy();
	}

	/**
	 * It will return the tableProperties of the Entity.
	 * @return
	 */
	public TablePropertiesInterface getTableProperties()
	{
		// TODO Auto-generated method stub
		return entity.getTableProperties();
	}

	/**
	 * It will return the taggedValue with the given key if present on the Entity from which this object is derived.
	 * else will return empty string.
	 * @return
	 */
	public String getTaggedValue(String key)
	{
		// TODO Auto-generated method stub
		return QueryableObjectUtility.getTaggedValue(entity, key);
	}

	/**
	 * It will return the discriminator column name for the entity
	 * @return 
	 */
	public String getDiscriminatorColumn()
	{

		return entity.getDiscriminatorColumn();
	}

	/**
	 * It will return the discriminator value name for the entity
	 * @return 
	 */
	public String getDiscriminatorValue()
	{
		return entity.getDiscriminatorValue();
	}

	/**
	 * It will return the weather the entity is abstract or not.
	 * @return 
	 */
	public boolean isAbstract()
	{
		return entity.isAbstract();
	}

	public QueryableObjectInterface getRootQueryableObject()
	{
		// TODO Auto-generated method stub
		return this;
	}

}
