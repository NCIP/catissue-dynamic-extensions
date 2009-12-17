
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
	long getUserId();
	/**
	 * To associate user with the study
	 * @param userId
	 */
	void setUserId(long userId);
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
}
