
package edu.common.dynamicextensions.domaininterface.userinterface;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
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
	String generateEditModeHTML(ContainerInterface container)
			throws DynamicExtensionsSystemException,DynamicExtensionsApplicationException;

	/**
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	String generateViewModeHTML(ContainerInterface container)
			throws DynamicExtensionsSystemException,DynamicExtensionsApplicationException;

	/**
	 * @see edu.common.dynamicextensions.domaininterface.userinterface.ContainmentAssociationControlInterface#generateLinkHTML()
	 */
	String generateLinkHTML() throws DynamicExtensionsSystemException;

	/**
	 * Checks if is paste button is enabled or not.
	 *
	 * @return true, if is paste enable
	 */
	boolean getIsPasteEnable();

	/**
	 * Sets the value for paste button to be enabled or not.
	 *
	 * @param isPasteEnable the new paste enable
	 */
	void setIsPasteEnable(boolean isPasteEnable);
}
