
package edu.wustl.common.querysuite.metadata.path;

import java.io.Serializable;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.EntityInterface;

/**
 * @author Chandrakant Talele
 */
public interface ICuratedPath extends Serializable
{

	/**
	 * @return Returns the curatedPathId.
	 */
	Long getCuratedPathId();

	/**
	 * @return Returns the entitySet.
	 */
	Set<EntityInterface> getEntitySet();

	/**
	 * @return Returns the isSelected.
	 */
	boolean isSelected();

	/**
	 * @return Returns the paths.
	 */
	Set<IPath> getPaths();
}
