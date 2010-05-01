
package edu.common.dynamicextensions.domaininterface;

import java.util.Collection;
import java.util.List;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.DEConstants.InheritanceStrategy;

/**
 * Entity object stores information of the entity.For each entity a dynamic table is generated using the metadata
 * information.
 * @author sujay_narkar
 *
 */
public interface EntityInterface extends AbstractEntityInterface
{

	/**
	 * This method returns the Collection of AbstractAttribute.
	 * @return the Collection of AbstractAttribute.
	 */
	Collection<AbstractAttributeInterface> getAbstractAttributeCollection();

	/**
	 * This method return the Collection of Attributes.
	 * @return the Collection of Attributes.
	 */
	Collection<AttributeInterface> getAttributeCollection();

	/**
	 * This method return the Collection of Association.
	 * @return the Collection of Association.
	 */
	Collection<AssociationInterface> getAssociationCollection();

	/**
	 * The abstractAttributeInterface to be added
	 * @param abstractAttributeInterface abstract attribute interface
	 */
	void addAbstractAttribute(AbstractAttributeInterface abstractAttribute);

	/**
	 * This method removes an AbstractAttribute from the Entity's Collection of AbstractAttribute.
	 * @param abstractAttribute an AbstractAttribute to be removed.
	 */
	void removeAbstractAttribute(AbstractAttributeInterface abstractAttribute);

	/**
	 * This method adds attribute interface to the abstract attribute collection.
	 * @param attributeInterface
	 */
	void addAttribute(AttributeInterface attributeInterface);

	/**
	 * This method adds attribute interface to the abstract attribute collection.
	 * @param attributeInterface
	 */
	void removeAttribute(AttributeInterface attributeInterface);

	/**
	 * This method removes association interface from the abstract attribute collection.
	 * @param associationInterface
	 */
	void addAssociation(AssociationInterface associationInterface);

	/**
	 * This method removes association interface from the abstract attribute collection.
	 * @param associationInterface
	 */
	void removeAssociation(AssociationInterface associationInterface);

	/**
	 * @param isAbstract The isAbstract to set.
	 */
	void setAbstract(boolean isAbstract);

	/**
	 * @return Returns the parentEntity.
	 */
	EntityInterface getParentEntity();

	/**
	 * @param parentEntity The parentEntity to set.
	 */
	void setParentEntity(EntityInterface parentEntity);

	/**
	 * This method returns association for all the hierarchy
	 * @return
	 */
	Collection<AssociationInterface> getAllAssociations();

	/**
	 * This method returns attributes for all the hierarchy.
	 * @return  Collection of AttributeInterface
	 */
	Collection<AttributeInterface> getAllAttributes();

	/**
	 * This method returns attributes ONLY for the entity. It does not traverse the heirarchy
	 * @return  Collection of AttributeInterface
	 */
	Collection<AttributeInterface> getEntityAttributes();

	/**
	 * This method returns all the attributes and associations for all the hierarchy.
	 * @return Collection of AbstractAttributeInterface
	 */
	Collection<AbstractAttributeInterface> getAllAbstractAttributes();

	/**
	 * Method returns  attribute based on the id passed.
	 * @param identifier Long identifier of the abstract attribute
	 * @return
	 */
	AttributeInterface getAttributeByIdentifier(Long identifier);

	/**
	 * Method returns  association based on the id passed.
	 * @param identifier Long identifier of the abstract attribute
	 * @return
	 */
	AssociationInterface getAssociationByIdentifier(Long identifier);

	/**
	 * @return Returns the inheritanceStrategy.
	 */
	InheritanceStrategy getInheritanceStrategy();

	/**
	 *@param inheritanceStrategy The inheritanceStrategy to set.
	 */
	void setInheritanceStrategy(InheritanceStrategy inheritanceStrategy);

	/**
	 *
	 *
	 */
	/*
		void removeAllAbstractAttributes();*/

	/**
	 * @return
	 */
	String getDiscriminatorColumn();

	/**
	 * @param discriminatorColumn
	 */
	void setDiscriminatorColumn(String discriminatorColumn);

	/**
	 * @return
	 */
	String getDiscriminatorValue();

	/**
	 * @param discriminatorValue
	 */
	void setDiscriminatorValue(String discriminatorValue);

	/**
	 * Get all attributes for query
	 */
	Collection<AttributeInterface> getEntityAttributesForQuery();

	/**
	 *
	 * @return
	 */
	int getDataTableState();

	/**
	 *
	 * @param dataTableState
	 */
	void setDataTableState(int dataTableState);

	/**
	 *
	 * @return
	 */
	EntityGroupInterface getEntityGroup();

	/**
	 *
	 * @param entityGroup
	 */
	void setEntityGroup(EntityGroupInterface entityGroup);

	/**
	 *
	 * @param attributeName
	 * @return
	 */
	AttributeInterface getAttributeByName(String attributeName);

	/**
	 *
	 * @param attributeName
	 * @return
	 */
	AbstractAttributeInterface getAbstractAttributeByName(String attributeName);

	/**
	 *
	 * @param attributeName
	 * @return
	 */
	boolean isAttributePresent(String attributeName);

	/**
	 *
	 * @param attributeName
	 * @return
	 */
	boolean isMultiselectAttributePresent(String attributeName);

	/**
	 * This method return the Collection of Association excluding collection attributes.
	 * @return the Collection of Association.
	 */
	Collection<AssociationInterface> getAssociationCollectionExcludingCollectionAttributes();

	/**
	 * This method will return the collection of attributes which are primary key for the entity
	 * @return
	 */
	Collection<AttributeInterface> getPrimarykeyAttributeCollectionInSameEntity();

	/**
	 * This method will populate the constraintProperties of the entity for inheritance
	 * @param isAddColumnForInheritance 
	 */
	void populateEntityForConstraintProperties(boolean isAddColumnForInheritance)
			throws DynamicExtensionsSystemException;

	/**
	 * It will add the given argument in the composite/Primary key attribute collection
	 * @param primaryAttribute to be added in compositeKeyAttributeCollection
	 */
	void addPrimaryKeyAttribute(AttributeInterface primaryAttribute);

	/**
	 * This method returns the Collection of primaryKeyAttributes.
	 */
	List<AttributeInterface> getPrimaryKeyAttributeCollection();

	/**
	 * It will set the primaryKeyAttributeCollection to the given collection
	 * @param primaryKeyAttributeCollection 
	 */
	void setPrimaryKeyAttributeCollection(List<AttributeInterface> primaryKeyAttributeCollection);

	/**
	 * It will retrieve the Attribute with the given name in the All attributes of entity including its inherited Attributes
	 * If not found will search in its parent attributes also 
	 * @param attributeName
	 * @return
	 */
	AttributeInterface getEntityAttributeByName(String attributeName);

	/**
	 * This method return the Collection of Attributes including the new Attributes which are 
	 * added because of inheritance which are its own local attributes.
	 * @return
	 */
	Collection<AttributeInterface> getAttributeCollectionWithInheritedAttributes();
	
	/**
	 * It will search the attribute in the entity attributes with including 
	 * inherited attributes which are its own local attributes. 
	 * if not found will search the attribute in the parent entity & so on
	 * @param attributeName
	 * @return attribute found else null
	 */
	AttributeInterface getAttributeByNameIncludingInheritedAttribute(String attributeName);
		
	/**
	 * It will return the collection of abstract attributes of the entity including inheritedAttributes 
	 * which are its own local attributes as well as of its parent & so on.
	 * @return Collection of abstractAttributes
	 */
	Collection<AbstractAttributeInterface> getAllAbstractAttributesIncludingInheritedAttributes();
	
	/**
	 * It will return the collection of attributes of the entity including inheritedAttributes, 
	 * which are its own local attributes as well as of its parent & so on.
	 * @return Collection of Attributes
	 */
	Collection<AttributeInterface> getAllAttributesIncludingInheritedAttributes();
	


}
