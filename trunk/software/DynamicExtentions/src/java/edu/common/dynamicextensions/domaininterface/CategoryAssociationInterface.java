
package edu.common.dynamicextensions.domaininterface;

import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;

/**
 *
 * @author sujay_narkar
 *
 */
public interface CategoryAssociationInterface extends BaseAbstractAttributeInterface,AssociationMetadataInterface
{

	/**
	 *
	 * @return
	 */
	CategoryEntityInterface getCategoryEntity();

	/**
	 *
	 * @param categoryEntity
	 */
	void setCategoryEntity(CategoryEntityInterface categoryEntity);

	/**
	 *
	 * @return
	 */
	CategoryEntityInterface getTargetCategoryEntity();

	/**
	 *
	 * @param targetCategoryEntity
	 */
	void setTargetCategoryEntity(CategoryEntityInterface targetCategoryEntity);

	/**
	 * This method returns the ConstraintProperties of the Association.
	 * @return the ConstraintProperties of the Association.
	 */
	ConstraintPropertiesInterface getConstraintProperties();

	/**
	 * This method sets the constraintProperties to the given ContraintProperties.
	 * @param constraintProperties the constraintProperties to be set.
	 */
	void setConstraintProperties(ConstraintPropertiesInterface constraintProperties);

}
