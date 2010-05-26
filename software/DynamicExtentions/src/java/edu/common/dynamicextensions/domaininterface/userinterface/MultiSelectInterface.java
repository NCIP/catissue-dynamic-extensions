
package edu.common.dynamicextensions.domaininterface.userinterface;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;

public interface MultiSelectInterface
{

	/**
	 * This method returns whether the ListBox or multiselect  has a multiselect property or not.
	 * @hibernate.property name="isMultiSelect" type="boolean" column="MULTISELECT"
	 * @return whether the ListBox has a multiselect property or not.
	 */
	Boolean getIsMultiSelect();

	/**
	 * @param isMultiSelectCheckBox the isMultiSelectCheckBox to set
	 */
	void setIsMultiSelect(Boolean isMultiSelect);

	/**
	 *
	 * This method returns AssociationInterface
	 * @return association
	 */
	AssociationInterface getBaseAbstractAttributeAssociation();
}
