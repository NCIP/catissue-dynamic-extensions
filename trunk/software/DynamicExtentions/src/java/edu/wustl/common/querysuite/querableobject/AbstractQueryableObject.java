
package edu.wustl.common.querysuite.querableobject;

import java.util.Collection;
import java.util.HashSet;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.querysuite.querableobjectinterface.QueryableAttributeInterface;
import edu.wustl.common.querysuite.querableobjectinterface.QueryableObjectInterface;

/**
 * At first there is no common interface in Entity & category, So to provide the 
 * one this class is used as wrapper on the Entity or category.
 * Instance of this class may refer to Entity or category . 
 * @author pavan_kalantri.
 *
 */
public abstract class AbstractQueryableObject implements QueryableObjectInterface
{

	/**
	 * Serial Version Unique Identifier
	 */
	protected Collection<QueryableAttributeInterface> queryableAttributeColl = new HashSet<QueryableAttributeInterface>();
	protected boolean isCategory;

	/**
	 * Parameterized constructor to create QueryableObject From the Entity
	 * It will also populate the querableAttributeColl of the QueryableObject.
	 * @param entity
	 */
	public AbstractQueryableObject(EntityInterface entity)
	{
		if (entity == null)
		{
			throw new RuntimeException("Entity can not be null");
		}
		for (AttributeInterface attribute : entity.getAllAttributesIncludingInheritedAttributes())
		{
			queryableAttributeColl.add(new QueryableEntityAttribute(attribute, this));
		}
	}

	/**
	 * default contructor
	 */
	public AbstractQueryableObject()
	{
		// TODO Auto-generated method stub 
	}

	/**
	 * Parameterized constructor to create QueryableObject From the Category
	 * It will also populate the querableAttributeColl of the QueryableObject.
	 * @param category
	 */
	public AbstractQueryableObject(CategoryInterface category)
	{
		if (category == null)
		{
			throw new RuntimeException("categoryEntity can not be null");
		}
		for (CategoryAttributeInterface attribute : QueryableObjectUtility
				.getCategoryAttributesFromCategory(category))
		{
			queryableAttributeColl.add(new QueryableCategoryAttribute(attribute, this));
		}

	}

	/**
	 * It will return the QueryableAttribute whose attribute or CategoryAttributes name is 
	 * equal to the given parameter name
	 * @param name
	 * @return
	 */
	protected QueryableAttributeInterface getQueryableAttributeByName(String name)
	{
		QueryableAttributeInterface attribute = null;
		for (QueryableAttributeInterface queryableAttribute : getAllAttributes())
		{
			if (queryableAttribute.getName().equals(name))
			{
				attribute = queryableAttribute;
				break;
			}
		}
		return attribute;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.common.querysuite.querableobjectInterface.QueryableObjectInterface#getQuerableAttributeColl()
	 */
	public Collection<QueryableAttributeInterface> getQueryableAttributeColl()
	{
		return queryableAttributeColl;
	}

	/**
	 * It will return the collection of queryableAttributes created from  the 
	 * Attributes of the entity , its parent entity & so on.
	 * @return collection of QueryableAttribueInterface.
	 */
	public Collection<QueryableAttributeInterface> getAllAttributes()
	{
		Collection<QueryableAttributeInterface> attributeCollection = getQueryableAttributeColl();
		Collection<QueryableAttributeInterface> queryableAttributeColl = new HashSet<QueryableAttributeInterface>();
		for (QueryableAttributeInterface attribute : attributeCollection)
		{
			if (!attribute.isInheritedAttribute())
			{
				queryableAttributeColl.add(attribute);
			}

		}
		return queryableAttributeColl;
	}

	/**
	 * It will search the attribute with the given identifier in the QueryableAttributeCollection of 
	 * the QueryableObject
	 * @param identifier
	 * @return QueryableAttribute whose attribute has id equal to identifier
	 */
	public QueryableAttributeInterface getAttributeByIdentifier(Long identifier)
	{
		QueryableAttributeInterface attribute = null;
		for (QueryableAttributeInterface queryableAttribute : queryableAttributeColl)
		{
			if (queryableAttribute.getId().equals(identifier))
			{
				attribute = queryableAttribute;
				break;
			}
		}
		return attribute;
	}

	/**
	 * It will check weather the given QueryableObject is Derived on the basis of category.
	 * If object is derived from category then it will return true else false.
	 * @return
	 */
	public boolean isCategoryObject()
	{
		return this.isCategory;
	}

	/**
	 * It will search the attribute with name equal to the given attributeName in the 
	 * QueryableAttribute Collection of the QueryableObject
	 * @param attributeName
	 * @return QueryableAttribute whose attribute has name equal to attributeName
	 */
	public QueryableAttributeInterface getAttributeByName(String attributeName)
	{
		return getQueryableAttributeByName(attributeName);
	}

	/**
	 * It will check on the basis of id of the Entity or category From which
	 * the QueryableObject is Created.
	 * @return true if id are equals else false.
	 */
	public boolean equals(Object queryableObject)
	{
		boolean isEqual = false;
		if (queryableObject instanceof QueryableObjectInterface
				&& ((QueryableObjectInterface) queryableObject).getId().equals(this.getId()))
		{
			isEqual = true;
		}
		return isEqual;
	}

	@Override
	public int hashCode()
	{
		return 1;
	}
}
