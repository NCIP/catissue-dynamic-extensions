
package edu.common.dynamicextensions.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AssociationMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintKeyPropertiesInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.global.DEConstants.InheritanceStrategy;
import edu.wustl.common.util.Utility;

/**
 * An entity is something that has a distinct, separate existence, though it need not be a material
 * existence. In particular, abstractions and legal fictions are usually regarded as entities.
 * An entity can either have many attributes or many associations.
 * @version 1.0
 * @created 28-Sep-2006 12:20:08 PM
 * @hibernate.joined-subclass table="DYEXTN_ENTITY"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 * @hibernate.cache  usage="read-write"
 */
public class Entity extends AbstractEntity implements EntityInterface
{

	/**
	 * Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = -552600540977483821L;

	/**
	 * Collection of attributes in this entity.
	 */
	protected Collection<AbstractAttributeInterface> abstractAttributeCollection = new HashSet<AbstractAttributeInterface>();

	/**
	 * Collection of attribute which are acting as a composite/Primary key for the entity
	 */
	protected List<AttributeInterface> primaryKeyAttributeCollection = new ArrayList<AttributeInterface>();

	/**
	 * Collection of EntityGroup.
	 */
	protected EntityGroupInterface entityGroup;

	/**
	 * state for data table creation
	 * 1 - data table created by DE
	 * 2 - data table is not created by DE and it does not exists
	 * 3 - data table is not created by DE but it is already present. calling application take the
	 *     responsibility of setting Table name and column name to the entity and attributes.
	 */
	protected int dataTableState = DEConstants.DATA_TABLE_STATE_NOT_CREATED;

	/**
	 * parent of this entity, null is no parent present.
	 */
	protected EntityInterface parentEntity = null;

	/**
	 * indicates if this entity is abstract or not.
	 */
	protected boolean isAbstract = false;

	/*
	 *
	 */
	protected int inheritStrategy = InheritanceStrategy.TABLE_PER_SUB_CLASS.getValue();

	/**
	 *
	 */
	protected String discriminatorColumn;

	/**
	 *
	 */
	protected String discriminatorValue;

	/**
	 * @hibernate.property name="dataTableState" type="int" column="DATA_TABLE_STATE"
	 * @return Returns the dataTableState.
	 */
	public int getDataTableState()
	{
		return dataTableState;
	}

	/**
	 * @param dataTableState The dataTableState to set.
	 */
	public void setDataTableState(int dataTableState)
	{
		this.dataTableState = dataTableState;
	}

	/**
	 * This method returns the Collection of primaryKeyAttributes.
	 * @hibernate.list name="PrimaryKeyAttributeCollection" table="DYEXTN_ENTIY_COMPOSITE_KEY_REL"
	 * cascade="save-update" inverse="false" lazy="false"
	 * @hibernate.collection-key column="ENTITY_ID"
	 * @hibernate.collection-index column="INSERTION_ORDER" type="long"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-many-to-many class="edu.common.dynamicextensions.domain.Attribute" column="ATTRIBUTE_ID"
	 * @return List of the composite/primary key attribute
	 */
	public List<AttributeInterface> getPrimaryKeyAttributeCollection()
	{
		return primaryKeyAttributeCollection;
	}

	/**
	 * It will set the primaryKeyAttributeCollection to the given collection
	 * @param PrimaryKeyAttributeCollection 
	 */
	public void setPrimaryKeyAttributeCollection(
			List<AttributeInterface> primaryKeyAttributeCollection)
	{
		this.primaryKeyAttributeCollection = primaryKeyAttributeCollection;
	}

	/**
	 * It will add the given argument in the composite/Primary key attribute collection
	 * @param primaryAttribute to be added in primaryKeyAttributeCollection
	 */
	public void addPrimaryKeyAttribute(AttributeInterface primaryAttribute)
	{
		if (primaryAttribute == null)
		{
			return;
		}

		if (primaryKeyAttributeCollection == null)
		{
			primaryKeyAttributeCollection = new ArrayList<AttributeInterface>();
		}
		primaryKeyAttributeCollection.add(primaryAttribute);
	}

	/**
	 * @hibernate.many-to-one column="ENTITY_GROUP_ID" cascade="save-update" class="edu.common.dynamicextensions.domain.EntityGroup"
	 * @return the entityGroup
	 */
	public EntityGroupInterface getEntityGroup()
	{
		return entityGroup;
	}

	/**
	 * @param entityGroup the entityGroup to set
	 */
	public void setEntityGroup(EntityGroupInterface entityGroup)
	{
		this.entityGroup = entityGroup;
	}

	/**
	 * This method adds an AbstractAttribute to the Entity's Collection of AbstractAttribute.
	 * @param abstractAttribute AbstractAttribute to be added.
	 */
	public void addAbstractAttribute(AbstractAttributeInterface abstractAttribute)
	{
		if (abstractAttribute == null)
		{
			return;
		}

		if (abstractAttributeCollection == null)
		{
			abstractAttributeCollection = new HashSet<AbstractAttributeInterface>();
		}
		abstractAttributeCollection.add(abstractAttribute);
		abstractAttribute.setEntity(this);

	}

	/**
	 * This method returns the Collection of AbstractAttribute.
	 * @hibernate.set name="abstractAttributeCollection" table="DYEXTN_ATTRIBUTE"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="ENTIY_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.AbstractAttribute"
	 * @return the Collection of AbstractAttribute.
	 */
	public Collection<AbstractAttributeInterface> getAbstractAttributeCollection()
	{
		return abstractAttributeCollection;
	}

	/**
	 * This method sets the abstractAttributeCollection to the given Collection of AbstractAttribute.
	 * @param abstractAttributeCollection The abstractAttributeCollection to set.
	 */
	public void setAbstractAttributeCollection(
			Collection<AbstractAttributeInterface> abstractAttributeCollection)
	{
		this.abstractAttributeCollection = abstractAttributeCollection;
	}

	/**
	 * This method return the Collection of Attributes.
	 * @return the Collection of Attributes.
	 */
	public Collection<AttributeInterface> getAttributeCollection()
	{
		Collection<AttributeInterface> attributeCollection = new HashSet<AttributeInterface>();
		if (abstractAttributeCollection != null && !abstractAttributeCollection.isEmpty())
		{
			Iterator attributeIterator = abstractAttributeCollection.iterator();
			while (attributeIterator.hasNext())
			{
				Object object = attributeIterator.next();
				if (object instanceof AttributeInterface)
				{
					AttributeInterface attribute = (AttributeInterface) object;
					if (!DynamicExtensionsUtility.isInheritedTaggPresent(attribute))
					{
						attributeCollection.add(attribute);
					}
				}
			}
		}
		return attributeCollection;
	}

	/**
	 * This method return the Collection of Attributes including the new Attributes which are 
	 * added because of inheritance which are its own local attributes.
	 * @return the Collection of Attributes.
	 */
	public Collection<AttributeInterface> getAttributeCollectionWithInheritedAttributes()
	{
		Collection<AttributeInterface> attributeCollection = new HashSet<AttributeInterface>();
		if (abstractAttributeCollection != null && !abstractAttributeCollection.isEmpty())
		{
			Iterator attributeIterator = abstractAttributeCollection.iterator();
			while (attributeIterator.hasNext())
			{
				Object object = attributeIterator.next();
				if (object instanceof AttributeInterface)
				{
					attributeCollection.add((AttributeInterface) object);

				}
			}
		}
		return attributeCollection;
	}

	/**
	 * This method return the Collection of Association.
	 * @return the Collection of Association.
	 */
	public Collection<AssociationInterface> getAssociationCollection()
	{
		Collection<AssociationInterface> associationCollection = new HashSet<AssociationInterface>();
		if (abstractAttributeCollection != null && !abstractAttributeCollection.isEmpty())
		{
			Iterator associationIterator = abstractAttributeCollection.iterator();
			while (associationIterator.hasNext())
			{
				Object object = associationIterator.next();
				if (object instanceof AssociationInterface)
				{
					associationCollection.add((AssociationInterface) object);
				}
			}
		}
		return associationCollection;
	}

	/**
	 * This method return the Collection of Association excluding collection attribute.
	 * Associations having both entities of same entity group will only return	
	 * @return the Collection of Association.
	 */
	public Collection<AssociationInterface> getAssociationCollectionExcludingCollectionAttributes()
	{
		Collection<AssociationInterface> AssociationsCollection = getAssociationCollection();
		Collection<AssociationInterface> entityAssociations = new ArrayList<AssociationInterface>();
		if (AssociationsCollection != null)
		{
			for (AssociationInterface association : AssociationsCollection)
			{
				if (association.getEntity().getEntityGroup().getName().equals(
						association.getTargetEntity().getEntityGroup().getName())
						&& !association.getIsCollection())
				{
					entityAssociations.add(association);
				}
			}
		}
		return entityAssociations;
	}

	/**
	 * This method removes an AbstractAttribute from the Entity's Collection of AbstractAttribute.
	 * @param abstractAttribute an AbstractAttribute to be removed.
	 */
	public void removeAbstractAttribute(AbstractAttributeInterface abstractAttribute)
	{
		if (abstractAttributeCollection != null
				&& abstractAttributeCollection.contains(abstractAttribute))
		{
			abstractAttributeCollection.remove(abstractAttribute);
			abstractAttribute.setEntity(null);
		}
	}

	/**
	 * This method removes all abstract attributes from the entity.
	 */
	public void removeAllAttributes()
	{
		if (abstractAttributeCollection != null)
		{
			abstractAttributeCollection.clear();
		}
	}

	/**
	 * This method returns the attribute for the given corresponding identifier.
	 * @param identifier identifier of the desired AbstractAttribute.
	 * @return the matched instance of AbstractAttribute.
	 */
	public AttributeInterface getAttributeByIdentifier(Long identifier)
	{
		AttributeInterface attribute = null;
		Collection<AttributeInterface> attributeCollection = getAttributeCollection();

		for (AttributeInterface attributeIterator : attributeCollection)
		{
			if (attributeIterator.getId() != null && attributeIterator.getId().equals(identifier))
			{
				attribute = attributeIterator;
				break;
			}
		}
		return attribute;
	}

	/**
	 * This method returns the attribute for the given corresponding identifier.
	 * @param identifier identifier of the desired AbstractAttribute.
	 * @return the matched instance of AbstractAttribute.
	 */
	public AssociationInterface getAssociationByIdentifier(Long identifier)
	{
		AssociationInterface association = null;
		Collection<AssociationInterface> associationCollection = getAssociationCollection();

		for (AssociationInterface associationIterator : associationCollection)
		{
			if (associationIterator.getId() != null
					&& associationIterator.getId().equals(identifier))
			{
				association = associationIterator;
				break;
			}
		}
		return association;
	}

	/**
	 * This method adds attribute interface to the abstract attribute collection.
	 * @param attributeInterface
	 */
	public void addAttribute(AttributeInterface attributeInterface)
	{
		addAbstractAttribute(attributeInterface);

	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.EntityInterface#removeAttribute(edu.common.dynamicextensions.domaininterface.AttributeInterface)
	 */
	public void removeAttribute(AttributeInterface attributeInterface)
	{
		removeAbstractAttribute(attributeInterface);

	}

	/**
	 * This method adds association interface to the abstract attribute collection.
	 * @param associationInterface
	 */
	public void addAssociation(AssociationInterface associationInterface)
	{
		addAbstractAttribute(associationInterface);
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.EntityInterface#removeAssociation(edu.common.dynamicextensions.domaininterface.AssociationInterface)
	 */
	public void removeAssociation(AssociationInterface associationInterface)
	{
		removeAbstractAttribute(associationInterface);

	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.EntityInterface#isAbstract()
	 *
	 * @hibernate.property name="isAbstract" type="boolean" column="IS_ABSTRACT"
	 */
	public boolean isAbstract()
	{
		return isAbstract;
	}

	/**
	 * This private method is for hibernate to set parent
	 * @see edu.common.dynamicextensions.domaininterface.EntityInterface#setAbstract(boolean)
	 */
	public void setAbstract(boolean isAbstract)
	{
		this.isAbstract = isAbstract;
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.EntityInterface#getParentEntity()
	 * @hibernate.many-to-one column="PARENT_ENTITY_ID" class="edu.common.dynamicextensions.domain.Entity" constrained="true"
	 *                        cascade="save-update"
	 */
	public EntityInterface getParentEntity()
	{
		return parentEntity;
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.EntityInterface#setParentEntity(edu.common.dynamicextensions.domaininterface.EntityInterface)
	 */
	public void setParentEntity(EntityInterface parentEntity)
	{
		this.parentEntity = parentEntity;
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.EntityInterface#getAllAssociations()
	 */
	public Collection<AssociationInterface> getAllAssociations()
	{
		Collection<AssociationInterface> associationCollection = new ArrayList<AssociationInterface>();
		associationCollection.addAll(getAssociationCollection());
		EntityInterface parentEntity = this.parentEntity;
		while (parentEntity != null)
		{
			associationCollection.addAll(parentEntity.getAssociationCollection());
			parentEntity = parentEntity.getParentEntity();
		}

		return associationCollection;
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.EntityInterface#getAllAttributes()
	 */
	public Collection<AttributeInterface> getAllAttributes()
	{

		Collection<AttributeInterface> attributeCollection = new ArrayList<AttributeInterface>();
		attributeCollection.addAll(getAttributeCollection());
		EntityInterface parentEntity = this.parentEntity;
		while (parentEntity != null)
		{
			attributeCollection.addAll(parentEntity.getAttributeCollection());
			parentEntity = parentEntity.getParentEntity();
		}

		return attributeCollection;
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.EntityInterface#getAllAbstractAttributes()
	 */
	public Collection<AbstractAttributeInterface> getAllAbstractAttributes()
	{
		Collection<AbstractAttributeInterface> abstractAttributeCollection = new ArrayList<AbstractAttributeInterface>();
		abstractAttributeCollection.addAll(getAllAssociations());
		abstractAttributeCollection.addAll(getAllAttributes());
		return abstractAttributeCollection;
	}

	/**
	 * @hibernate.property name="inheritStrategy" type="int" column="INHERITANCE_STRATEGY"
	 * @return Returns the inheritanceStrategy.
	 */
	private int getInheritStrategy()
	{
		return inheritStrategy;
	}

	/**
	 * @param inheritanceStrategy The inheritanceStrategy to set.
	 */
	private void setInheritStrategy(int inheritStrategy)
	{
		this.inheritStrategy = inheritStrategy;
	}

	/**
	 * @return Returns the inheritanceStrategy.
	 */
	public InheritanceStrategy getInheritanceStrategy()
	{
		return InheritanceStrategy.get(this.inheritStrategy);
	}

	/**
	 * @param inheritanceStrategy The inheritanceStrategy to set.
	 */
	public void setInheritanceStrategy(InheritanceStrategy inheritanceStrategy)
	{
		setInheritStrategy(inheritanceStrategy.getValue());
	}

	/**
	 * @hibernate.property name="discriminatorColumn" type="string" column="DISCRIMINATOR_COLUMN_NAME"
	 * @return Returns the discriminatorColumn.
	 */
	public String getDiscriminatorColumn()
	{
		return discriminatorColumn;
	}

	/**
	 * @param discriminatorColumn The discriminatorColumn to set.
	 */
	public void setDiscriminatorColumn(String discriminatorColumn)
	{
		this.discriminatorColumn = discriminatorColumn;
	}

	/**
	 * @hibernate.property name="discriminatorValue" type="string" column="DISCRIMINATOR_VALUE"
	 * @return Returns the discriminatorValue.
	 */
	public String getDiscriminatorValue()
	{
		return discriminatorValue;
	}

	/**
	 * @param discriminatorValue The discriminatorValue to set.
	 */
	public void setDiscriminatorValue(String discriminatorValue)
	{
		this.discriminatorValue = discriminatorValue;
	}

	/**
	 * getEntityAttributes.
	 */
	public Collection<AttributeInterface> getEntityAttributes()
	{
		return getAttributeCollection();
	}

	/**
	 * getEntityAttributesForQuery.
	 */
	public Collection<AttributeInterface> getEntityAttributesForQuery()
	{
		Collection<AttributeInterface> theAttributeCollection = new ArrayList<AttributeInterface>();
		Set<String> attributeNames = new HashSet<String>();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		Collection<AttributeInterface> attributeCollection = getAttributeCollection();
		theAttributeCollection.addAll(attributeCollection);
		for (AttributeInterface attribute : attributeCollection)
		{
			attributeNames.add(attribute.getName());
			attribute.setEntity(this);
		}
		EntityInterface parentEntity = this.parentEntity;
		while (parentEntity != null)
		{
			Collection<AttributeInterface> parentAttributeCollection = parentEntity
					.getAttributeCollection();
			for (AttributeInterface parentAttribute : parentAttributeCollection)
			{
				if (attributeNames.add(parentAttribute.getName()))
				{

					theAttributeCollection.add(parentAttribute);
					boolean isDerivedTagPresent = false;
					for (TaggedValueInterface tag : parentAttribute.getTaggedValueCollection())
					{
						if (tag.getKey().equals(edu.wustl.cab2b.common.util.Constants.TYPE_DERIVED))
						{
							isDerivedTagPresent = true;
						}
					}
					if (!isDerivedTagPresent)
					{
						TaggedValueInterface tag = factory.createTaggedValue();
						tag.setKey(edu.wustl.cab2b.common.util.Constants.TYPE_DERIVED);
						tag.setValue(edu.wustl.cab2b.common.util.Constants.TYPE_DERIVED);
						parentAttribute.addTaggedValue(tag);
					}
				}
			}
			parentEntity = parentEntity.getParentEntity();
		}
		return theAttributeCollection;

	}

	/**
	 * It will search the attribute in the same entity attributes without including inherited attributes. 
	 * if not found will search the attribute in the parent entity & so on
	 * @param attributeName
	 * @return
	 */
	public AttributeInterface getAttributeByName(String attributeName)
	{
		return searchAttributeByNameInCollection(this.getAllAbstractAttributes(), attributeName);
	}

	/**
	 * It will search the attribute in the entity attributes with including inherited attributes
	 * which are its own local attributes. 
	 * if not found will search the attribute in the parent entity & so on
	 * @param attributeName
	 * @return attribute found else null
	 */
	public AttributeInterface getAttributeByNameIncludingInheritedAttribute(String attributeName)
	{
		return searchAttributeByNameInCollection(this
				.getAllAbstractAttributesIncludingInheritedAttributes(), attributeName);
	}

	/**
	 * It will return the collection of abstract attributes of the entity including inheritedAttributes
	 * which are its own local attributes as well as of its parent & so on.
	 * @return Collection of abstractAttributes
	 */
	public Collection<AbstractAttributeInterface> getAllAbstractAttributesIncludingInheritedAttributes()
	{
		Collection<AbstractAttributeInterface> abstractAttributeCollection = new ArrayList<AbstractAttributeInterface>();
		abstractAttributeCollection.addAll(getAllAssociations());
		abstractAttributeCollection.addAll(getAllAttributesIncludingInheritedAttributes());
		return abstractAttributeCollection;
	}

	/**
	 * It will return the collection of attributes of the entity including inheritedAttributes
	 * which are its own local attributes , as well as of its parent & so on.
	 * @return Collection of Attributes
	 */
	public Collection<AttributeInterface> getAllAttributesIncludingInheritedAttributes()
	{

		Collection<AttributeInterface> attributeCollection = new ArrayList<AttributeInterface>();
		attributeCollection.addAll(getAttributeCollectionWithInheritedAttributes());
		EntityInterface parentEntity = this.parentEntity;
		while (parentEntity != null)
		{
			attributeCollection
					.addAll(parentEntity.getAttributeCollectionWithInheritedAttributes());
			parentEntity = parentEntity.getParentEntity();
		}

		return attributeCollection;
	}

	/**
	 * It will retrieve the Attribute with the given name in the All attributes of entity including its inherited Attributes
	 * Will return the matched attribute which is present in the same entity or null. 
	 * @param attributeName
	 * @return
	 */
	public AttributeInterface getEntityAttributeByName(String attributeName)
	{

		Collection<AbstractAttributeInterface> abstractAttributeCollection = new ArrayList<AbstractAttributeInterface>();
		abstractAttributeCollection
				.addAll(getAbstractAttributeCollectionIncludingInheritedAttribute());
		return searchAttributeByNameInCollection(abstractAttributeCollection, attributeName);
	}

	/**
	 * It will search the attribute in the given given abstractAttributeCollection parameter which is having the same name given in the 
	 * second attribute name parameter.
	 * @param abstractAttributeCollection
	 * @param attributeName
	 * @return
	 */
	private AttributeInterface searchAttributeByNameInCollection(
			Collection<AbstractAttributeInterface> abstractAttributeCollection, String attributeName)
	{
		AttributeInterface attribute = null;
		AbstractAttributeInterface abstractAttribute = null;
		for (AbstractAttributeInterface attr : abstractAttributeCollection)
		{
			if (attr.getName().trim().equals(attributeName))
			{
				abstractAttribute = attr;
				break;
			}
		}
		if (abstractAttribute instanceof AssociationInterface)
		{
			AssociationInterface association = (AssociationInterface) abstractAttribute;
			if (association.getIsCollection())
			{
				Collection<AbstractAttributeInterface> attributeCollection = association
						.getTargetEntity().getAllAbstractAttributes();
				Collection<AbstractAttributeInterface> filteredAttributeCollection = EntityManagerUtil
						.filterSystemAttributes(attributeCollection);
				List<AbstractAttributeInterface> attributesList = new ArrayList<AbstractAttributeInterface>(
						filteredAttributeCollection);
				attribute = (AttributeInterface) attributesList.get(0);
			}
		}
		else
		{
			attribute = (AttributeInterface) abstractAttribute;
		}
		return attribute;
	}

	/**
	 * This method return the Collection of Attributes including all its inherited attributes
	 * which are its own local attributes.
	 * @return the Collection of Attributes.
	 */
	private Collection<AbstractAttributeInterface> getAbstractAttributeCollectionIncludingInheritedAttribute()
	{
		Collection<AbstractAttributeInterface> attributeCollection = new HashSet<AbstractAttributeInterface>();
		if (abstractAttributeCollection != null && !abstractAttributeCollection.isEmpty())
		{
			Iterator attributeIterator = abstractAttributeCollection.iterator();
			while (attributeIterator.hasNext())
			{
				Object object = attributeIterator.next();
				attributeCollection.add((AbstractAttributeInterface) object);

			}
		}
		return attributeCollection;
	}

	/**
	 *
	 * @param attributeName
	 * @return
	 */
	public AbstractAttributeInterface getAbstractAttributeByName(String attributeName)
	{
		AbstractAttributeInterface abstractAttribute = null;
		for (AbstractAttributeInterface attr : this.getAllAbstractAttributes())
		{
			if (attr.getName().trim().equals(attributeName))
			{
				abstractAttribute = attr;
				break;
			}
		}
		return abstractAttribute;
	}

	/**
	 *
	 * @param attributeName
	 * @return
	 */
	public boolean isAttributePresent(String attributeName)
	{
		boolean isAttributePresent = false;
		for (AbstractAttributeInterface attr : this.getAbstractAttributeCollection())
		{
			if (attr.getName().trim().equals(attributeName))
			{
				isAttributePresent = true;
				break;
			}
		}
		return isAttributePresent;
	}

	/**
	 *
	 * @param attributeName
	 * @return
	 */
	public boolean isMultiselectAttributePresent(String attributeName)
	{
		boolean isAttributePresent = false;
		for (AbstractAttributeInterface abstractAttribute : this.getAllAbstractAttributes())
		{
			if (abstractAttribute.getName().trim().equals(attributeName)
					&& abstractAttribute instanceof AssociationInterface)
			{
				AssociationInterface association = (AssociationInterface) abstractAttribute;
				if (association.getIsCollection())
				{
					isAttributePresent = true;
					break;
				}
			}
		}
		return isAttributePresent;
	}

	/**
	 * This method will return the collection of the attribute which are primary key
	 * @return collection of attributes
	 */
	public Collection<AttributeInterface> getPrimarykeyAttributeCollectionInSameEntity()
	{
		Collection<AttributeInterface> primaryKeyAttributeCollection = new HashSet<AttributeInterface>();
		Collection<AttributeInterface> attributeCollection = getEntityAttributes();
		if (attributeCollection != null && !attributeCollection.isEmpty())
		{
			for (AttributeInterface attribute : attributeCollection)
			{
				if (attribute.getIsPrimaryKey() && !attribute.getIsNullable())
				{
					primaryKeyAttributeCollection.add(attribute);

				}
			}

		}
		return primaryKeyAttributeCollection;
	}

	/**
	 * This method will return the list of column names which are acting as primary key in this entity
	 * @return list of column names
	 * @throws DynamicExtensionsSystemException
	 */
	public List<String> getForeignKeyColumnsForInheritance()
			throws DynamicExtensionsSystemException
	{
		List<String> ColumnNames = new ArrayList<String>();
		Collection<ConstraintKeyPropertiesInterface> cnstrKeyPropColl = getConstraintProperties()
				.getSrcEntityConstraintKeyPropertiesCollection();
		for (ConstraintKeyPropertiesInterface foreignKeyProperties : cnstrKeyPropColl)
		{
			ColumnNames.add(foreignKeyProperties.getTgtForiegnKeyColumnProperties().getName());
		}

		return ColumnNames;
	}

	/**
	 * It will create the constraintProperties of the entity for inheritance
	 * @param isAddColumnForInheritance
	 * @throws DynamicExtensionsSystemException 
	 */
	public void populateEntityForConstraintProperties(boolean isAddColumnForInheritance)
			throws DynamicExtensionsSystemException
	{
		DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(this,
				isAddColumnForInheritance);
	}

	/**
	 * @param targetEntity
	 * @return
	 */
	public AssociationMetadataInterface getAssociation(AbstractEntityInterface targetEntity)
	{
		AssociationInterface association = null;
		for (AssociationInterface associationInterface : getAssociationCollection())
		{
			if (associationInterface.getTargetEntity().getName().equals(targetEntity.getName()))
			{
				association = associationInterface;
				break;
			}
		}

		return association;
	}

	/**
	 * For Entity, use camel case parser from common package.
	 */
	public String getCapitalizedName(String name)
	{
		return Utility.getDisplayLabel(name.trim());
	}

}