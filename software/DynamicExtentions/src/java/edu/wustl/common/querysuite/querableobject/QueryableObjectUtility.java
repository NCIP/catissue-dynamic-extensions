
package edu.wustl.common.querysuite.querableobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.querysuite.querableobjectinterface.QueryableAttributeInterface;
import edu.wustl.common.querysuite.querableobjectinterface.QueryableObjectInterface;

/**
 * @author pavan_kalantri
 *
 */
public class QueryableObjectUtility
{

	/**
	 * It will return the list of All CategoryEntities present in the category.
	 * @param category
	 * @return List of category Entities.
	 */
	public static List<CategoryEntityInterface> getAllCategoryEntityFromCategory(
			CategoryInterface category)
	{
		List<CategoryEntityInterface> catEntList = new ArrayList<CategoryEntityInterface>();
		populateAllCategoryEntityList(catEntList, category.getRootCategoryElement());
		return catEntList;

	}

	/**
	 * It will add the childCategory (CateegoryEntity ) in the list & will call itself
	 * for the childCategory also to populate the list with all the child category Entities.
	 * @param list in which all the child Category Entities are added.
	 * @param categoryEntity
	 */
	private static void populateAllCategoryEntityList(List<CategoryEntityInterface> list,
			CategoryEntityInterface categoryEntity)
	{
		if (categoryEntity != null)
		{
			list.add(categoryEntity);
			for (CategoryEntityInterface categoryEntityInterface : categoryEntity
					.getChildCategories())
			{
				populateAllCategoryEntityList(list, categoryEntityInterface);
			}
		}
	}

	/**
	 * It will return the collection of the Entities which are present in all the category entities
	 * of the Category.
	 * @param category
	 * @return collection of the Entity present in the category.
	 */
	public static Collection<EntityInterface> getAllEntityFromCategory(CategoryInterface category)
	{
		List<CategoryEntityInterface> catEntList = getAllCategoryEntityFromCategory(category);
		Collection<EntityInterface> entitySet = new HashSet<EntityInterface>();
		for (CategoryEntityInterface categoryEntity : catEntList)
		{
			entitySet.add(categoryEntity.getEntity());
		}
		return entitySet;
	}

	/**
	 * It will return the collection of the All CategoryAttributes which are added in the category
	 * & which present in the its respective category entity.
	 * @param category
	 * @return collection of category attributes.
	 */
	public static Collection<CategoryAttributeInterface> getCategoryAttributesFromCategory(
			CategoryInterface category)
	{
		List<CategoryEntityInterface> catEntList = getAllCategoryEntityFromCategory(category);
		Collection<CategoryAttributeInterface> categoryAttributeColl = new HashSet<CategoryAttributeInterface>();
		for (CategoryEntityInterface categoryEntity : catEntList)
		{
			categoryAttributeColl.addAll(categoryEntity.getCategoryAttributeCollection());
		}
		return categoryAttributeColl;
	}

	/**
	 * It will return the collection of the All abstractAttributes which are added in the category
	 * & which present in the its respective category Attributes of the category.
	 * @param category
	 * @return collection of abstract attributes.
	 */
	public static Collection<AbstractAttributeInterface> getAbstractAttributesCollectionFromCategory(
			CategoryInterface category)
	{
		List<CategoryEntityInterface> catEntList = getAllCategoryEntityFromCategory(category);
		Collection<AbstractAttributeInterface> abstractAttributeCollection = new HashSet<AbstractAttributeInterface>();
		for (CategoryEntityInterface categoryEntity : catEntList)
		{
			for (CategoryAttributeInterface categoryAttribute : categoryEntity
					.getCategoryAttributeCollection())
			{
				abstractAttributeCollection.add(categoryAttribute.getAbstractAttribute());
			}
		}
		return abstractAttributeCollection;
	}

	/**
	 * It will return the collection of the Attributes which are added as a CategoryAttribute in the given
	 * parameter category
	 * @param category
	 * @return collection of the Attributes present in the category.
	 */
	public static Collection<AttributeInterface> getAttributeCollectionFromCategory(
			CategoryInterface category)
	{
		List<CategoryEntityInterface> catEntList = getAllCategoryEntityFromCategory(category);
		Collection<AttributeInterface> attributeCollection = new HashSet<AttributeInterface>();
		for (CategoryEntityInterface categoryEntity : catEntList)
		{
			for (CategoryAttributeInterface categoryAttribute : categoryEntity
					.getCategoryAttributeCollection())
			{
				attributeCollection.add(getAttributeFromCategoryAttribute(categoryAttribute));
			}
		}
		return attributeCollection;
	}

	/**
	 * It will return the Collection of attributes which are added in the CategoryEntity
	 * @param categoryEntity
	 * @return
	 */
	public static Collection<AttributeInterface> getAttributeCollectionFromCategoryEntity(
			CategoryEntityInterface categoryEntity)
	{
		Collection<AttributeInterface> attributeCollection = new HashSet<AttributeInterface>();
		for (CategoryAttributeInterface categoryAttribute : categoryEntity
				.getCategoryAttributeCollection())
		{
			attributeCollection.add(getAttributeFromCategoryAttribute(categoryAttribute));
		}
		return attributeCollection;
	}

	/**
	 * It will return the original Atribute From which the CategoryAttribute is created.
	 * @param categoryAttribute
	 * @return
	 */
	public static AttributeInterface getAttributeFromCategoryAttribute(
			CategoryAttributeInterface categoryAttribute)
	{
		AttributeInterface attribute = null;
		if (categoryAttribute.getAbstractAttribute() instanceof AttributeInterface)
		{
			attribute = (AttributeInterface) categoryAttribute.getAbstractAttribute();
		}
		else
		{
			AssociationInterface association = (AssociationInterface) categoryAttribute
					.getAbstractAttribute();
			if (association.getIsCollection())
			{
				Collection<AbstractAttributeInterface> abstractAttrColl = association
						.getTargetEntity().getAllAbstractAttributes();
				Collection<AbstractAttributeInterface> filteredAttributeCollection = EntityManagerUtil
						.filterSystemAttributes(abstractAttrColl);
				List<AbstractAttributeInterface> attributesList = new ArrayList<AbstractAttributeInterface>(
						filteredAttributeCollection);
				attribute = (AttributeInterface) attributesList.get(0);
			}
		}
		return attribute;
	}

	/**
	 * It will check weather the tag with the key s present in the abstractMetadata or not.
	 * @param abstractMetadata
	 * @param key
	 * @return
	 */
	public static String getTaggedValue(AbstractMetadataInterface abstractMetadata, String key)
	{
		Collection<TaggedValueInterface> taggedValueColl = abstractMetadata
				.getTaggedValueCollection();
		String taggedValue = "";
		for (TaggedValueInterface tag : taggedValueColl)
		{
			if (tag.getKey().equalsIgnoreCase(key))
			{
				taggedValue = tag.getValue();
				break;
			}
		}
		return taggedValue;
	}

	/**
	 * It will create the Queryable Object from the given Entity.
	 * @param entity
	 * @return QueryableObjectInterface created from the entity.
	 */
	public static QueryableObjectInterface createQueryableObject(EntityInterface entity)
	{
		return new QueryableEntity(entity);
	}

	/**
	 * It will create the QueryableObject from the Category.
	 * @param category
	 * @return
	 */
	public static QueryableObjectInterface createQueryableObject(CategoryInterface category)
	{
		return new QueryableCategory(category);
	}

	/**
	 * It will create the QueryableAttribute from the Attrbute.
	 * @param attribute
	 * @param entity
	 * @return
	 */
	public static QueryableAttributeInterface createQueryableAttribute(
			AttributeInterface attribute, EntityInterface entity)
	{
		QueryableObjectInterface queryableObject = createQueryableObject(entity);
		return queryableObject.getAttributeByIdentifier(attribute.getId());
	}

	/**
	 * It will create the QueryableAttribute from the CategoryAttribute.
	 * @param attribute
	 * @param category
	 * @return
	 */
	public static QueryableAttributeInterface createQueryableAttribute(
			CategoryAttributeInterface attribute, CategoryInterface category)
	{
		QueryableObjectInterface queryableObject = createQueryableObject(category);
		return queryableObject.getAttributeByIdentifier(attribute.getId());
	}

	public static CategoryInterface getCategoryFromCategoryAttribute(
			CategoryAttributeInterface categoryAttribute)
	{
		CategoryEntityInterface entity = categoryAttribute.getCategoryEntity();
		CategoryInterface category = entity.getCategory();

		while (category == null)
		{
			entity = entity.getTreeParentCategoryEntity();
			category = entity.getCategory();

		}
		return category;
	}

	/**
	* It will return the QueryableObject of the Entity or category with the Identifier as given
	* present in cache.
	* @return QueryableObjectInterface
	* @throws DynamicExtensionsApplicationException
	* @throws DynamicExtensionsSystemException
	*/
	public static QueryableObjectInterface getQueryableObjectFromCache(Long identifier)
	{
		QueryableObjectInterface queryableObject = null;
		try
		{
			EntityInterface entity = EntityCache.getInstance().getEntityById(identifier);
			queryableObject = QueryableObjectUtility.createQueryableObject(entity);
		}
		catch (Exception e)
		{
			queryableObject = getQueryableCategory(identifier);
		}
		return queryableObject;
	}

	private static QueryableObjectInterface getQueryableCategory(Long identifier)
	{
		QueryableObjectInterface queryableObject;
		CategoryInterface category;
		try
		{
			category = CategoryManager.getInstance().getCategoryById(identifier);
		}
		catch (DynamicExtensionsSystemException e)
		{
			throw new RuntimeException("QueryableObject with given id is not present in cache : "
					+ identifier, e);
		}
		queryableObject = QueryableObjectUtility.createQueryableObject(category);
		return queryableObject;
	}

	/**
	 * It will return the QueryableAtribute of the Attribute or categoryAttribute with the Identifier as given
	 * present in cache.
	 * @return QueryableObjectInterface
	 */
	public static QueryableAttributeInterface getQueryableAttributeFromCache(Long identifier)
	{
		QueryableAttributeInterface queryableAttribute = null;
		try
		{
			AttributeInterface attribute = EntityCache.getInstance().getAttributeById(identifier);
			queryableAttribute = QueryableObjectUtility.createQueryableAttribute(attribute,
					attribute.getEntity());
		}
		catch (Exception e)
		{
			// TODO: handle exception
			CategoryAttributeInterface categoryAttribute = getCategoryAttribtueById(identifier);
			CategoryInterface category = QueryableObjectUtility
					.getCategoryFromCategoryAttribute(categoryAttribute);
			queryableAttribute = QueryableObjectUtility.createQueryableAttribute(categoryAttribute,
					category);
		}
		return queryableAttribute;
	}

	private static CategoryAttributeInterface getCategoryAttribtueById(Long identifier)
	{
		CategoryAttributeInterface catAttr = null;
		try
		{
			catAttr = CategoryManager.getInstance().getCategoryAttributeById(identifier);
		}
		catch (DynamicExtensionsSystemException e)
		{
			throw new RuntimeException("Error while fetching Category Attribute With id"
					+ identifier);
		}
		return catAttr;
	}

	/**
	* This method trims out package name form the entity name
	*
	* @param entity
	* @return
	*/
	public static String getQueryableObjectName(QueryableObjectInterface queryObject)
	{
		String name = queryObject.getName();
		return name.substring(name.lastIndexOf('.') + 1, name.length());
	}

	/**
	 * It will search the control associated with the baseAbstractAttribute in the containers
	 * controlCollection & will return the caption of that control if found else
	 * will return the empty string.
	 * @param container
	 * @param baseAbstractAttribute
	 * @return caption or display name of the baseAbstractAttribute.
	 */
	public static String getControlCaption(ContainerInterface container,
			BaseAbstractAttributeInterface baseAbstractAttribute)
	{
		String caption = "";
		for (ControlInterface control : container.getControlCollection())
		{
			if (baseAbstractAttribute.equals(control.getBaseAbstractAttribute()))
			{
				caption = control.getCaption();
				break;
			}
		}

		return caption;
	}

}
