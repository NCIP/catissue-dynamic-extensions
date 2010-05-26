
package edu.common.dynamicextensions.domaininterface;

import java.util.Collection;

public interface CategoryInterface extends AbstractMetadataInterface
{

	/**
	 *
	 * @return
	 */
	CategoryEntityInterface getRootCategoryElement();

	/**
	 *
	 * @param rootCategoryElement
	 */
	void setRootCategoryElement(CategoryEntityInterface rootCategoryElement);

	/**
	 * @param categoryEntityName
	 * @return
	 */
	CategoryEntityInterface getCategoryEntityByName(String categoryEntityName);

	/**
	 * @return
	 */
	Collection<CategoryEntityInterface> getRelatedAttributeCategoryEntityCollection();

	/**
	 *
	 * @return
	 */
	Long getUserId();

	/**
	 * To associate user with the study
	 * @param userId
	 */
	void setUserId(Long userId);

	/**
	 * @param relatedAttributeCategoryEntityCollection
	 */
	void setRelatedAttributeCategoryEntityCollection(
			Collection<CategoryEntityInterface> relatedAttributeCategoryEntityCollection);

	/**
	 * @param categoryEntity
	 */
	void addRelatedAttributeCategoryEntity(CategoryEntityInterface categoryEntity);

	void removeRelatedAttributeCategoryEntity(CategoryEntityInterface categoryEntity);

	/**
	 * @return true if category is to be cached
	 */
	Boolean getIsCacheable();

	/**
	 * @param isCacheable true if category is to be cached
	 */
	void setIsCacheable(Boolean isCacheable);
}
