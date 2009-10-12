
package edu.common.dynamicextensions.domaininterface.userinterface;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

public interface AbstractContainmentControlInterface extends ControlInterface
{

	/**
	 * @return container
	 */
	ContainerInterface getContainer();

	/**
	 * @param container The container to set.
	 */
	void setContainer(ContainerInterface container);

	/**
	 * @return
	 */
	boolean isCardinalityOneToMany();

	/**
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	String generateEditModeHTML(Integer rowId,ContainerInterface container) throws DynamicExtensionsSystemException;

	/**
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	String generateViewModeHTML(Integer rowId,ContainerInterface container) throws DynamicExtensionsSystemException;

	/**
	 * @see edu.common.dynamicextensions.domaininterface.userinterface.ContainmentAssociationControlInterface#generateLinkHTML()
	 */
	String generateLinkHTML() throws DynamicExtensionsSystemException;

}
