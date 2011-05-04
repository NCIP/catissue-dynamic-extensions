
package edu.wustl.common.querysuite.querableobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.common.dynamicextensions.domain.PathAssociationRelationInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PathInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.TablePropertiesInterface;
import edu.common.dynamicextensions.util.global.DEConstants.InheritanceStrategy;
import edu.wustl.common.querysuite.metadata.associations.IAssociation;
import edu.wustl.common.querysuite.metadata.associations.impl.IntraModelAssociation;
import edu.wustl.common.querysuite.metadata.path.IPath;
import edu.wustl.common.querysuite.metadata.path.Path;
import edu.wustl.common.querysuite.querableobjectinterface.QueryableAttributeInterface;
import edu.wustl.common.querysuite.querableobjectinterface.QueryableObjectInterface;

/**
 * This class is wrapper on the Category.
 * @author pavan_kalantri.
 *
 */
public class QueryableCategory extends AbstractQueryableObject
{

	private CategoryInterface category;
	private final static String NOT_SUPPORTED_MSG = "Operation not supported on category";

	/**
	 * parameterized constructor.
	 * @param category
	 */
	public QueryableCategory(CategoryInterface category)
	{
		super(category);
		this.category = category;
		this.isCategory = true;
	}

	/**
	 * default constructor
	 */
	public QueryableCategory()
	{
		this.isCategory = true;
	}

	/**
	 * It will create the QueryableObject for each entity in the Category & return the Collection of that
	 * QueryableEntity's
	 * @return QueryableObjectInterface Collection
	 */
	public Collection<QueryableObjectInterface> getEntityCollection()
	{
		Collection<QueryableObjectInterface> queryableEntityCollection = new ArrayList<QueryableObjectInterface>();
		for (EntityInterface entity : QueryableObjectUtility.getAllEntityFromCategory(category))
		{
			queryableEntityCollection.add(new QueryableEntity(entity));
		}
		return queryableEntityCollection;

	}

	/**
	 * This method returns the Collection of QueryableAttributes which are
	 * created from the CategoryAttributes which are present in the
	 * CategoryEntities of the category present in the QuerableCategory.
	 * @return the Collection of QueryableAttributesAttribute.
	 */
	public Collection<QueryableAttributeInterface> getAllAttributes()
	{
		return queryableAttributeColl;
	}

	/**
	 * This method returns the Collection of Attributes which are present in the
	 * CategoryEntities of the category present in the QuerableCategory.
	 * @return the Collection of Attribute.
	 */
	public Collection<QueryableAttributeInterface> getAttributeCollection()
	{
		return queryableAttributeColl;
	}

	/**
	 * It will search the Attribute with given identifier in all the Attributes which are present in the
	 * category.
	 * @param attributeName
	 * @return attribute if found else null;
	 */
	public QueryableAttributeInterface getEntityAttributeByName(String attributeName)
	{
		return getAttributeByName(attributeName);
	}

	/**
	 * This method returns the Collection of Attributes which are present in the
	 * CategoryEntities of the category present in the QuerableCategory.
	 * @return the Collection of Attribute.
	 */
	public Collection<QueryableAttributeInterface> getEntityAttributes()
	{
		return queryableAttributeColl;
	}

	/**
	 * This method returns the Collection of Attributes which are present in the
	 * CategoryEntities of the category present in the QuerableCategory.
	 * @return the Collection of Attribute.
	 */
	public Collection<QueryableAttributeInterface> getEntityAttributesForQuery()
	{
		return queryableAttributeColl;
	}

	/**
	 * It will search the Attribute in the AttributeCollection present in the category with
	 * the given attributeName.
	 * @param attributeName
	 * @return true if found else false.
	 */
	public boolean isAttributePresent(String attributeName)
	{
		boolean isPresent = false;
		for (QueryableAttributeInterface queryableAttribute : getAttributeCollection())
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
	 * It will return the description of the Category.
	 * @return description of category.
	 */
	public String getDescription()
	{
		return category.getDescription();
	}

	/**
	 * It will return the id of the category
	 * @return category id.
	 */
	public Long getId()
	{
		return category.getId();
	}

	/**
	 * It will return the Name of the category
	 * @return name of category.
	 */
	public String getName()
	{
		return category.getName();
	}

	/**
	 * It will return the relatedAtributeCategoryEntityCollection of the category.
	 * @return
	 */
	public Collection<CategoryEntityInterface> getRelatedAttributeCategoryEntityCollection()
	{
		// TODO Auto-generated method stub
		return category.getRelatedAttributeCategoryEntityCollection();
	}

	/**
	 * It will return the root categoryElement of the category.
	 * @return root category element ie. CategoryEntity.
	 */
	public CategoryEntityInterface getRootCategoryElement()
	{
		// TODO Auto-generated method stub
		return category.getRootCategoryElement();
	}

	/**
	 * It will check weather the tag with the given key is present on the Category from
	 * which this Object is derived.
	 * @return true if tag present else false.
	 */
	public boolean isTagPresent(String key)
	{
		// TODO Auto-generated method stub
		return (QueryableObjectUtility.getTaggedValue(category, key).equals("")) ? false : true;
	}

	/**
	 * It will return the taggedValueCollection of the category.
	 * @return collection of taggedValues.
	 */
	public Collection<TaggedValueInterface> getTaggedValueCollection()
	{
		// TODO Auto-generated method stub
		return category.getTaggedValueCollection();
	}

	/**
	 * In case of Entity it will throw exception as this operation is not supported on the
	 * category. It will work only in case of Entity.
	 * @return
	 */
	public EntityInterface getEntity()
	{
		// TODO Auto-generated method stub
		throw new RuntimeException();
	}

	/**
	 * In case of Entity it will throw exception as this operation is not supported on the
	 * category. It will work only in case of Entity.
	 * @return
	 */
	public QueryableObjectInterface getParentEntity()
	{
		// TODO Auto-generated method stub
		throw new RuntimeException(NOT_SUPPORTED_MSG);
	}

	/**
	 * In case of Entity it will throw exception as this operation is not supported on the
	 * category. It will work only in case of Entity.
	 * @return
	 */
	public InheritanceStrategy getInheritanceStrategy()
	{
		// TODO Auto-generated method stub
		throw new RuntimeException(NOT_SUPPORTED_MSG);
	}

	/**
	 * In case of Entity it will throw exception as this operation is not supported on the
	 * category. It will work only in case of Entity.
	 * @return
	 */
	public TablePropertiesInterface getTableProperties()
	{
		// TODO Auto-generated method stub
		throw new RuntimeException(NOT_SUPPORTED_MSG);
	}

	/**
	 * It will return the Value of the tag with the Key equal to the given Key present on the
	 * Category, if not present will return empty string.
	 * @return value of the tag if tag present else empty string.
	 */
	public String getTaggedValue(String key)
	{
		// TODO Auto-generated method stub
		return QueryableObjectUtility.getTaggedValue(category, key);
	}

	/**
	 * It will return the collection of CategoryEntities present in the Category
	 * of this Object.
	 * @return collection of category Entities.
	 */
	public Collection<CategoryEntityInterface> getAllCategoryEntities()
	{
		return QueryableObjectUtility.getAllCategoryEntityFromCategory(category);
	}

	/*	public IPath getPathOfCategoryEntityFromRoot(CategoryEntityInterface categoryEntity)
		{
			List<IAssociation> intermediateAssociations = new ArrayList<IAssociation>();
			PathInterface dePath = categoryEntity.getPath();
			for( PathAssociationRelationInterface pathAssociation : dePath.getSortedPathAssociationRelationCollection())
			{
				 IntraModelAssociation association = new IntraModelAssociation(pathAssociation.getAssociation());
				 intermediateAssociations.add(association);
			}
			return new Path(category.getRootCategoryElement().getEntity(),categoryEntity.getEntity(),intermediateAssociations);
		}*/

	/**
	 * It will search the path in the two category Entities present in the category & specified in the
	 * CSV file of Category. If the path present will return that else will return null.
	 * @param srcCaregoryEntity
	 * @param tgtCategoryEntity
	 * @return IPath present in the  srcCaregoryEntity & tgtCaregoryEntity, if path not present will return null
	 */
	public IPath getPath(CategoryEntityInterface srcCaregoryEntity,
			CategoryEntityInterface tgtCategoryEntity)
	{
		List<IAssociation> associationList = new ArrayList<IAssociation>();
		Path path = null;
		PathInterface dePath = tgtCategoryEntity.getPath();
		for (PathAssociationRelationInterface pathAssociation : dePath
				.getSortedPathAssociationRelationCollection())
		{
			AssociationInterface association = pathAssociation.getAssociation();
			IntraModelAssociation intraModelAssociation = new IntraModelAssociation(association);
			associationList.add(intraModelAssociation);
		}
		path = new Path(srcCaregoryEntity.getEntity(), tgtCategoryEntity.getEntity(),
					associationList);

		return path;
	}

	/**
	 * In case of Entity it will throw exception as this operation is not supported on the
	 * category. It will work only in case of Entity.
	 * @return
	 */
	public String getDiscriminatorColumn()
	{
		// TODO Auto-generated method stub
		throw new RuntimeException(NOT_SUPPORTED_MSG);
	}

	/**
	 * In case of Entity it will throw exception as this operation is not supported on the
	 * category. It will work only in case of Entity.
	 * @return
	 */
	public String getDiscriminatorValue()
	{
		// TODO Auto-generated method stub
		throw new RuntimeException(NOT_SUPPORTED_MSG);
	}

	/**
	 * In case of Entity it will throw exception as this operation is not supported on the
	 * category. It will work only in case of Entity.
	 * @return
	 */
	public boolean isAbstract()
	{
		// TODO Auto-generated method stub
		throw new RuntimeException(NOT_SUPPORTED_MSG);
	}

	/**
	 * It will create and return the QueryableEntity which is created from the Entity from which the
	 * RootCategoryEntity of the Category is created.
	 * @return QueryableObjectInerface
	 */
	public QueryableObjectInterface getRootQueryableObject()
	{
		return QueryableObjectUtility.createQueryableObject(category.getRootCategoryElement()
				.getEntity());
	}

}
