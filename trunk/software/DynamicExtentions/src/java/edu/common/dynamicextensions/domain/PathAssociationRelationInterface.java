
package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface;
import edu.common.dynamicextensions.domaininterface.PathInterface;

/**
 * 
 * @author mandar_shidhore
 *
 */
public interface PathAssociationRelationInterface
		extends
			DynamicExtensionBaseDomainObjectInterface,
			Comparable
{

	/**
	 * 
	 * @return
	 */
	PathInterface getPath();

	/**
	 * 
	 * @param path
	 */
	void setPath(PathInterface path);

	/**
	 * 
	 * @return
	 */
	AssociationInterface getAssociation();

	/**
	 * 
	 * @param association
	 */
	void setAssociation(AssociationInterface association);

	/**
	 * 
	 * @return
	 */
	int getPathSequenceNumber();

	/**
	 * 
	 * @param pathSequenceNumber
	 */
	void setPathSequenceNumber(int pathSequenceNumber);

	/**
	 * 
	 * @return
	 */
	Long getSourceInstanceId();

	/**
	 * 
	 * @param sourceInstanceId
	 */
	void setSourceInstanceId(Long sourceInstanceId);

	/**
	 * 
	 * @return
	 */
	Long getTargetInstanceId();

	/**
	 * 
	 * @param targetInstanceId
	 */
	void setTargetInstanceId(Long targetInstanceId);

}
