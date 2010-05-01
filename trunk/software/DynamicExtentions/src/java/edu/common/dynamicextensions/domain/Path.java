
package edu.common.dynamicextensions.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.PathInterface;

/**
 * @hibernate.class table="DYEXTN_PATH"
 * @author rajesh_patil
 *
 */
public class Path extends DynamicExtensionBaseDomainObject implements PathInterface
{

	/**
	 * Serial Version Unique Identifier
	 */
	protected static final long serialVersionUID = 1234567890L;

	/**
	 *
	 */
	protected Collection<PathAssociationRelationInterface> pathAssociationRelationCollection = new HashSet<PathAssociationRelationInterface>();

	/**
	 * This method returns the unique identifier of the Path.
	 * @hibernate.id name="id" column="IDENTIFIER" type="long"
	 * length="30" unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="DYEXTN_PATH_SEQ"
	 * @return the identifier of the Path.
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * @hibernate.set name="pathAssociationRelationCollection" table="DYEXTN_PATH_ASSO_REL"
	 * cascade="save-update" inverse="false" lazy="false" order-by="PATH_SEQUENCE_NUMBER desc"
	 * @hibernate.collection-key column="PATH_ID"
	 * @hibernate.cache usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.PathAssociationRelation"
	 * @return the pathAssociationRelation
	 */
	public Collection<PathAssociationRelationInterface> getPathAssociationRelationCollection()
	{
		return pathAssociationRelationCollection;
	}

	/**
	 * @param pathAssociationRelation the pathAssociationRelation to set
	 */
	public void setPathAssociationRelationCollection(
			Collection<PathAssociationRelationInterface> pathAssociationRelationCollection)
	{
		this.pathAssociationRelationCollection = pathAssociationRelationCollection;
	}

	/**
	 * 
	 * @param pathAssociationRelationInterface
	 */
	public void addPathAssociationRelation(
			PathAssociationRelationInterface pathAssociationRelationInterface)
	{
		if (this.pathAssociationRelationCollection == null)
		{
			pathAssociationRelationCollection = new HashSet<PathAssociationRelationInterface>();

		}
		pathAssociationRelationCollection.add(pathAssociationRelationInterface);
	}

	/**
	 * 
	 */
	public List<PathAssociationRelationInterface> getSortedPathAssociationRelationCollection()
	{
		List PathAssociationRelationList = new ArrayList(getPathAssociationRelationCollection());
		Collections.sort(PathAssociationRelationList);
		return PathAssociationRelationList;
	}

}