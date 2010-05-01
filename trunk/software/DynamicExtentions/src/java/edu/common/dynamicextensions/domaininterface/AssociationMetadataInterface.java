
package edu.common.dynamicextensions.domaininterface;

import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;

/**
 * @author kunal_kamble
 * This is a marker interface for the associations
 *
 */
public interface AssociationMetadataInterface extends BaseAbstractAttributeInterface
{

	/**
	 * @return type of the association
	 */
	AssociationType getAssociationType();
}
