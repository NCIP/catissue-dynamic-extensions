/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author Vishvesh Mulay
 *@version 1.0
 */

package edu.common.dynamicextensions.domaininterface.userinterface;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

public interface ContainmentAssociationControlInterface extends AbstractContainmentControlInterface
{

	/**
	 * This method returns true if the cardinality of the Containment Association is One to Many.
	 * @return true if Cardinality is One to Many, false otherwise.
	 */
	boolean isCardinalityOneToMany();

	/**
	 * This method generates the HTML for this containment as a Link.
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	String generateLinkHTML() throws DynamicExtensionsSystemException;

}
