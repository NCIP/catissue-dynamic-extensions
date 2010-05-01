
package edu.common.dynamicextensions.domaininterface.userinterface;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

public interface CategoryAssociationControlInterface extends AbstractContainmentControlInterface
{

	/**
	 * This method generates the HTML for this containment as a Link.
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	String generateLinkHTML() throws DynamicExtensionsSystemException;
}
