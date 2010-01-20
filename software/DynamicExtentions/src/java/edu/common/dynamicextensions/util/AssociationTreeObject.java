
package edu.common.dynamicextensions.util;

import java.util.Collection;
import java.util.HashSet;

public class AssociationTreeObject
{

	/**
	 *
	 */
	private Long identifier;

	/**
	 *
	 */

	private String label;

	/**
	 *
	 * @param identifier
	 * @param label
	 */
	public AssociationTreeObject(Long identifier, String label)
	{
		this.identifier = identifier;
		this.label = label;
	}

	/**
	 *
	 *
	 */
	public AssociationTreeObject()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 *
	 */
	private Collection associationTreeObjCollection;

	/**
	 *
	 * @return
	 */
	public Collection getAssociationTreeObjectCollection()
	{
		return associationTreeObjCollection;
	}

	/**
	 *
	 * @param associationTreeObjCollection
	 */
	public void setAssociationTreeObjectCollection(Collection associationTreeObjCollection)
	{
		this.associationTreeObjCollection = associationTreeObjCollection;
	}

	/**
	 *
	 * @param associationTreeObject
	 */
	public void addAssociationTreeObject(AssociationTreeObject associationTreeObject)
	{
		if (associationTreeObjCollection == null)
		{
			associationTreeObjCollection = new HashSet();
		}
		associationTreeObjCollection.add(associationTreeObject);
	}

	/**
	 *
	 * @return
	 */
	public Long getId()
	{
		return identifier;
	}

	/**
	 *
	 * @param identifier
	 */
	public void setId(Long identifier)
	{
		this.identifier = identifier;
	}

	/**
	 *
	 * @return
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 *
	 * @param label
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

}
