
package edu.common.dynamicextensions.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.SkipLogicAttributeInterface;

/**
 * @author sujay_narkar
 * @hibernate.class table="DYEXTN_PERMISSIBLE_VALUE" 
 *
 */
public abstract class PermissibleValue extends DynamicExtensionBaseDomainObject
		implements
			PermissibleValueInterface
{

	/**
	 * 
	 */
	protected String description;
	/**
	 * Semantic property collection.
	 */

	protected Collection<SemanticPropertyInterface> semanticPropertyCollection = new HashSet<SemanticPropertyInterface>();
	/**
	 * Collection of category attributes.
	 */
	protected Collection<SkipLogicAttributeInterface> dependentSkipLogicAttributes = new HashSet<SkipLogicAttributeInterface>();

	/**
	 * @hibernate.property name="description" type="string" column="DESCRIPTION" 
	 * @return Returns the buttonCss.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * setter method for description
	 * @param description description of the value
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.PermissibleValueInterface#getValueAsObject()
	 */
	public abstract Object getValueAsObject();

	/**
	 * @hibernate.id name="id" column="IDENTIFIER" type="long"
	 * length="30" unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="DYEXTN_PERMISSIBLEVAL_SEQ" 
	 * @return Returns the id.
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * @hibernate.set name="semanticPropertyCollection" cascade="save-update"
	 * inverse="false" lazy="false"
	 * @hibernate.collection-key column="ABSTRACT_VALUE_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.SemanticProperty"
	 * @return Returns the semanticPropertyCollection.
	 */
	public Collection getSemanticPropertyCollection()
	{
		return semanticPropertyCollection;
	}

	/**
	 * @param semanticPropertyCollection The semanticPropertyCollection to set.
	 */
	public void setSemanticPropertyCollection(Collection semanticPropertyCollection)
	{
		this.semanticPropertyCollection = semanticPropertyCollection;
	}

	/**
	 * This method adds a SemanticProperty to the AbstractMetadata.
	 * @param semanticPropertyInterface A SemanticProperty to be added.
	 */
	public void addSemanticProperty(SemanticPropertyInterface semanticPropertyInterface)
	{
		if (semanticPropertyCollection == null)
		{
			semanticPropertyCollection = new HashSet<SemanticPropertyInterface>();
		}
		semanticPropertyCollection.add(semanticPropertyInterface);
	}

	/**
	 * This method removes a SemanticProperty from the AbstractMetadata.
	 * @param semanticPropertyInterface A SemanticProperty to be removed.
	 */
	public void removeSemanticProperty(SemanticPropertyInterface semanticPropertyInterface)
	{
		if ((semanticPropertyCollection != null)
				&& (semanticPropertyCollection.contains(semanticPropertyInterface)))
		{
			semanticPropertyCollection.remove(semanticPropertyInterface);
		}
	}

	/**
	 * This method removes all SemanticProperties from AbstractMetadata.
	 */
	public void removeAllSemanticProperties()
	{
		if (semanticPropertyCollection != null)
		{
			semanticPropertyCollection.clear();
		}
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface#getOrderedSemanticPropertyCollection()
	 */
	public List<SemanticPropertyInterface> getOrderedSemanticPropertyCollection()
	{
		List<SemanticPropertyInterface> semanticPropertyList = new ArrayList<SemanticPropertyInterface>();

		if (this.semanticPropertyCollection != null && !this.semanticPropertyCollection.isEmpty())
		{
			semanticPropertyList.addAll(this.semanticPropertyCollection);
			Collections.sort(semanticPropertyList);
		}
		return semanticPropertyList;
	}
	/**
	 * @hibernate.set name="dependentSkipLogicAttributes" table="DYEXTN_SKIP_LOGIC_ATTRIBUTE"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="PERM_VALUE_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.SkipLogicAttribute"
	 * @return Returns the dependentSkipLogicAttributes.
	 */
	public Collection<SkipLogicAttributeInterface> getDependentSkipLogicAttributes()
	{
		return dependentSkipLogicAttributes;
	}
	/**
	 * 
	 * @param dependentSkipLogicAttributes
	 */
	public void setDependentSkipLogicAttributes(
			Collection<SkipLogicAttributeInterface> dependentSkipLogicAttributes)
	{
		this.dependentSkipLogicAttributes = dependentSkipLogicAttributes;
	}
	/**
	 * This method adds a skip logic attribute.
	 * @param skipLogicAttributeInterface
	 */
	public void addDependentSkipLogicAttribute(SkipLogicAttributeInterface skipLogicAttributeInterface)
	{
		if (dependentSkipLogicAttributes == null)
		{
			dependentSkipLogicAttributes = new HashSet<SkipLogicAttributeInterface>();
		}
		dependentSkipLogicAttributes.add(skipLogicAttributeInterface);
	}
	/**
	 * This method removes a SkipLogic Attribute.
	 * @param skipLogicAttributeInterface.
	 */
	public void removeDependentSkipLogicAttribute(SkipLogicAttributeInterface skipLogicAttributeInterface)
	{
		if ((dependentSkipLogicAttributes != null)
				&& (dependentSkipLogicAttributes.contains(skipLogicAttributeInterface)))
		{
			dependentSkipLogicAttributes.remove(skipLogicAttributeInterface);
		}
	}

	/**
	 * This method removes all SkipLogic Attributes.
	 */
	public void removeAllDependentSkipLogicAttributes()
	{
		if (dependentSkipLogicAttributes != null)
		{
			dependentSkipLogicAttributes.clear();
		}
	}
	/**
	 * 
	 * @param stringValue
	 * @return
	 */
	public abstract PermissibleValueInterface clone();
}
