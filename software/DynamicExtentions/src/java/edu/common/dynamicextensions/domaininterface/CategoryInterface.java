
package edu.common.dynamicextensions.domaininterface;

import java.util.Collection;

import edu.common.dynamicextensions.domain.AutoLoadXpath;

public interface CategoryInterface extends AbstractMetadataInterface
{

	/**
	 * returns the root category entity for this category.
	 * @return root category element.
	 */
	CategoryEntityInterface getRootCategoryElement();

	/**
	 *Sets the root category element.
	 * @param rootCategoryElement root category element to be set.
	 */
	void setRootCategoryElement(CategoryEntityInterface rootCategoryElement);

	/**
	 * searched the category entity with the given name & returns it if found else returns
	 * null.
	 * @param categoryEntityName name of the category entity to be searched.
	 * @return category entity with given name.
	 */
	CategoryEntityInterface getCategoryEntityByName(String categoryEntityName);

	/**
	 * Return the collection of category entities which contains the related attribtues.
	 * @return collection of category entities.
	 */
	Collection<CategoryEntityInterface> getRelatedAttributeCategoryEntityCollection();

	/**
	 * Returns the user id , which user has created this category.
	 * @return user id.
	 */
	Long getUserId();

	/**
	 * To associate user with the study
	 * @param userId
	 */
	void setUserId(Long userId);

	/**
	 * set the category entity collection which contains the related attributes.
	 * @param relatedAttributeCategoryEntityCollection collection category entity.
	 */
	void setRelatedAttributeCategoryEntityCollection(
			Collection<CategoryEntityInterface> relatedAttributeCategoryEntityCollection);

	/**
	 * @param categoryEntity
	 */
	void addRelatedAttributeCategoryEntity(CategoryEntityInterface categoryEntity);

	void removeRelatedAttributeCategoryEntity(CategoryEntityInterface categoryEntity);

	/**
	 * checks whether category is cacheable or not.
	 * @return true if category is to be cached
	 */
	Boolean getIsCacheable();

	/**
	 * sets the isCacheable.
	 * @param isCacheable true if category is to be cached
	 */
	void setIsCacheable(Boolean isCacheable);

	/**
	 * This method will return whether this category is to be populated from XML or not.
	 * @return is to be populated from XML or not.
	 */
	boolean getIsPopulateFromXml();

	/**
	 * Sets the isPopulateFromXml.
	 * @param isPopulateFromXml isPopulateFromXml
	 */
	void setIsPopulateFromXml(boolean isPopulateFromXml);

	/**
	 * Return the concept codes assigned with this category for which to do the population using XML.
	 * @return collection of concept codes.
	 */
	Collection<AutoLoadXpath> getAutoLoadXpathCollection();

	/**
	 * It will return the autoloadxpath object with the given xpath if found else will return null.
	 * @param xpath the xpath of which the object is to be found,
	 * @return the object with given xpath.
	 */
	AutoLoadXpath getAutoLoadXpath(String xpath);

	void setProcessorClass(String processorClass);
	String getProcessorClass();

}
