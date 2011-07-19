
package edu.common.dynamicextensions.domain;

import java.util.Collection;
import java.util.HashSet;

import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;

/**
 *
 * @author mandar_shidhore
 * @hibernate.joined-subclass table="DYEXTN_CATEGORY"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class Category extends AbstractCategory implements CategoryInterface
{
	private String processorClass;

	/**
	 * is to be populated from XML (Lab Loading).
	 */
	private boolean isPopulateFromXml = false;

	/**
	 * Collection of concept codes for which this category is to be populated (Lab Loading).
	 */
	private Collection<AutoLoadXpath> autoLoadXpathCollection = new HashSet<AutoLoadXpath>();

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 4234527890L;

	/**
	 * User id
	 */
	protected Long userId;

	/**
	 * rootCategoryElement.
	 */
	protected CategoryEntity rootCategoryElement;

	/**
	 * true if category is to be cached
	 */
	private Boolean isCacheable = false;

	/**
	 *
	 */
	protected Collection<CategoryEntityInterface> relatedAttributeCategoryEntityCollection = new HashSet<CategoryEntityInterface>();

	/**
	 *
	 * @return
	 * @hibernate.property name="userId" type="long" length="19" column="USER_ID"
	 */
	public Long getUserId()
	{
		return userId;
	}

	/**
	 *
	 * @param userId user identifier
	 */
	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	/**
	 * @hibernate.many-to-one column="ROOT_CATEGORY_ELEMENT" cascade="all" class="edu.common.dynamicextensions.domain.CategoryEntity"
	 * @return the rootCategoryElement.
	 */
	public CategoryEntityInterface getRootCategoryElement()
	{
		return rootCategoryElement;
	}

	/**
	 * @param rootCategoryElement the rootCategoryElement to set
	 */
	public void setRootCategoryElement(CategoryEntityInterface rootCategoryElement)
	{
		this.rootCategoryElement = (CategoryEntity) rootCategoryElement;
	}

	/**
	 * @param categoryEntityName category entityName
	 * @return the category entity
	 */
	public CategoryEntityInterface getCategoryEntityByName(String categoryEntityName)
	{
		return getCategoryEntity(getRootCategoryElement(), categoryEntityName);
	}

	/**
	 * @param categoryEntity category entity
	 * @param categoryEntityName category entityName
	 * @return the category entity
	 */
	private CategoryEntityInterface getCategoryEntity(CategoryEntityInterface categoryEntity,
			String categoryEntityName)
	{
		CategoryEntityInterface searchedCategoryEntity = null;
		if (categoryEntity == null)
		{
			return searchedCategoryEntity;
		}
		if (categoryEntity.getName().equals(categoryEntityName))
		{
			return categoryEntity;
		}
		for (CategoryEntityInterface categoryEntityInterface : categoryEntity.getChildCategories())
		{
			if (categoryEntityInterface.getName().equals(categoryEntityName))
			{
				searchedCategoryEntity = categoryEntityInterface;
				break;
			}

			if (categoryEntityInterface.getChildCategories().size() > 0)
			{
				searchedCategoryEntity = getCategoryEntity(categoryEntityInterface,
						categoryEntityName);
				if (searchedCategoryEntity != null)
				{
					break;
				}

			}

		}

		return searchedCategoryEntity;
	}

	/**
	 * @hibernate.set name="relAttrCategoryEntity" table="DYEXTN_CATEGORY_ENTITY"
	 * cascade="all" inverse="false" lazy="false"
	 * @hibernate.collection-key column="REL_ATTR_CAT_ENTITY_ID"
	 * @hibernate.cache usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.CategoryEntity"
	 * @return the relatedAttributeCategoryEntityCollection
	 */
	public Collection<CategoryEntityInterface> getRelatedAttributeCategoryEntityCollection()
	{
		return relatedAttributeCategoryEntityCollection;
	}

	/**
	 * @param relatedAttributeCategoryEntityCollection the relatedAttributeCategoryEntityCollection to set
	 */
	public void setRelatedAttributeCategoryEntityCollection(
			Collection<CategoryEntityInterface> relatedAttributeCategoryEntityCollection)
	{
		this.relatedAttributeCategoryEntityCollection = relatedAttributeCategoryEntityCollection;
	}

	/**
	 * @param categoryEntity category entity
	 */
	public void addRelatedAttributeCategoryEntity(CategoryEntityInterface categoryEntity)
	{
		relatedAttributeCategoryEntityCollection.add(categoryEntity);
	}

	/**
	 * @param categoryEntity category entity
	 */
	public void removeRelatedAttributeCategoryEntity(CategoryEntityInterface categoryEntity)
	{
		relatedAttributeCategoryEntityCollection.remove(categoryEntity);
	}

	/**
	 * @hibernate.property name="isCacheable" type="boolean" column="IS_CACHEABLE"
	 * @return true if category is to be cached
	 */
	public Boolean getIsCacheable()
	{
		return isCacheable;
	}

	/**
	 * @param isCacheable true if category is to be cached
	 */
	public void setIsCacheable(Boolean isCacheable)
	{
		this.isCacheable = isCacheable;
	}

	/**
	 * @hibernate.set name="autoLoadXpathCollection" table="DYEXTN_AUTO_LOADXPATH"
	 * cascade="save-update" inverse="false" lazy="false"
	 * @hibernate.collection-key column="CATEGORY_ID"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.AutoLoadXpath"
	 * @hibernate.cache  usage="read-write"
	 * @return Returns the concept code collection.
	 */
	public Collection<AutoLoadXpath> getAutoLoadXpathCollection()
	{
		return autoLoadXpathCollection;
	}

	/**
	 * It will return the autoloadxpath object with the given xpath if found else will return null.
	 * @param xpath the xpath of which the object is to be found,
	 * @return the object with given xpath.
	 */
	public AutoLoadXpath getAutoLoadXpath(String xpath)
	{
		AutoLoadXpath catXpath = null;
		for (AutoLoadXpath autoXpath : autoLoadXpathCollection)
		{
			if (autoXpath.getXpath().equals(xpath))
			{
				catXpath = autoXpath;
				break;
			}
		}
		return catXpath;
	}

	/**
	 * sets the concept code collection.
	 * @param conceptCodeCollection concept code collection to be set.
	 */
	public void setAutoLoadXpathCollection(Collection<AutoLoadXpath> autoLoadXpathCollection)
	{
		this.autoLoadXpathCollection = autoLoadXpathCollection;
	}

	/**
	 * @hibernate.property name="isPopulateFromXml" type="boolean" column="POPULATE_FROM_XML"
	 * @return Returns the isPopulateFromXml.
	 */
	public boolean getIsPopulateFromXml()
	{
		return isPopulateFromXml;
	}

	/**
	 * sets the isPopulateFromXml.
	 * @param isPopulateFromXml populate from XML.
	 */
	public void setIsPopulateFromXml(boolean isPopulateFromXml)
	{
		this.isPopulateFromXml = isPopulateFromXml;
	}

	public String getProcessorClass()
	{
		// TODO Auto-generated method stub
		return processorClass;
	}

	public void setProcessorClass(String processorClass)
	{
		this.processorClass=processorClass;

	}
}