
package edu.common.dynamicextensions.domaininterface;

import java.util.Collection;
import java.util.List;

import edu.common.dynamicextensions.domain.PathAssociationRelationInterface;

/**
 * 
 * @author mandar_shidhore
 *
 */
public interface PathInterface extends DynamicExtensionBaseDomainObjectInterface
{

	/**
	 * 
	 * @return
	 */
	Collection<PathAssociationRelationInterface> getPathAssociationRelationCollection();

	/**
	 * 
	 * @param pathAssociationRelationCollection
	 */
	void setPathAssociationRelationCollection(
			Collection<PathAssociationRelationInterface> pathAssociationRelationCollection);

	/**
	 * 
	 * @return
	 */
	List<PathAssociationRelationInterface> getSortedPathAssociationRelationCollection();

	/**
	 * 
	 * @param pathAssociationRelationInterface
	 */
	void addPathAssociationRelation(
			PathAssociationRelationInterface pathAssociationRelationInterface);

}
