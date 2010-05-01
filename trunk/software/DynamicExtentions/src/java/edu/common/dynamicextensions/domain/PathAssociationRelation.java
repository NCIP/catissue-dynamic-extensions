
package edu.common.dynamicextensions.domain;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.PathInterface;

/**
 * @hibernate.class table="DYEXTN_PATH_ASSO_REL"
 * @author mandar_shidhore
 *
 */
public class PathAssociationRelation extends DynamicExtensionBaseDomainObject
		implements
			PathAssociationRelationInterface,
			Comparable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 12345678L;
	/**
	 * 
	 */
	protected Path path;

	/**
	 * 
	 */
	protected Association association;

	/**
	 * 
	 */
	protected int pathSequenceNumber;

	/**
	 * 
	 */
	protected Long sourceInstanceId;

	/**
	 * 
	 */
	protected Long targetInstanceId;
	/**
	 * This method returns the unique identifier of the PathAssociationRelation.
	 * @hibernate.id name="id" column="IDENTIFIER" type="long"
	 * length="30" unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="DYEXTN_PATH_ASSO_REL_SEQ"
	 * @return the identifier of the Path.
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * @hibernate.many-to-one column="PATH_ID" class="edu.common.dynamicextensions.domain.Path" cascade="save-update"
	 * @return the path
	 */
	public Path getPath()
	{
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(PathInterface path)
	{
		this.path = (Path) path;
	}

	/**
	 * @hibernate.many-to-one column="ASSOCIATION_ID" class="edu.common.dynamicextensions.domain.Association" cascade="none"
	 * @return the association
	 */
	public AssociationInterface getAssociation()
	{
		return (AssociationInterface) association;
	}

	/**
	 * @param association the association to set
	 */
	public void setAssociation(AssociationInterface associationInterface)
	{
		this.association = (Association) associationInterface;
	}

	/**
	 * @hibernate.property name="pathSequenceNumber" type="int" column="PATH_SEQUENCE_NUMBER"
	 * @return the pathSequenceNumber
	 */
	public int getPathSequenceNumber()
	{
		return pathSequenceNumber;
	}

	/**
	 * @param pathSequenceNumber the pathSequenceNumber to set
	 */
	public void setPathSequenceNumber(int pathSequenceNumber)
	{
		this.pathSequenceNumber = pathSequenceNumber;
	}

	public int compareTo(Object object)
	{
		PathAssociationRelationInterface pathAssociationRelation = (PathAssociationRelation) object;
		Integer thisPathSequenceNumber = this.pathSequenceNumber;
		Integer otherPathSequenceNumber = pathAssociationRelation.getPathSequenceNumber();
		return thisPathSequenceNumber.compareTo(otherPathSequenceNumber);
	}

	/**
	 * @hibernate.property name="sourceInstanceId" type="long" column="SRC_INSTANCE_ID"
	 * @return the sourceInstanceId
	 */
	public Long getSourceInstanceId()
	{
		return sourceInstanceId;
	}

	/**
	 * @param sourceInstanceId the sourceInstanceId to set
	 */
	public void setSourceInstanceId(Long sourceInstanceId)
	{
		this.sourceInstanceId = sourceInstanceId;
	}

	/**
	 * @hibernate.property name="targetInstanceId" type="long" column="TGT_INSTANCE_ID"
	 * @return the targetInstanceId
	 */
	public Long getTargetInstanceId()
	{
		return targetInstanceId;
	}

	/**
	 * @param targetInstanceId the targetInstanceId to set
	 */
	public void setTargetInstanceId(Long targetInstanceId)
	{
		this.targetInstanceId = targetInstanceId;
	}

}