
package edu.common.dynamicextensions.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.AssociationMetadataInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.TablePropertiesInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;

/**
 * This is an abstract class extended by Entity, Entity group, Attribute.
 * This class stores basic information needed for metadata objects.
 * @version 1.0
 * @created 28-Sep-2006 12:20:06 PM
 * @hibernate.joined-subclass table="DYEXTN_ABSTRACT_ENTITY"
 * @hibernate.joined-subclass-key column="id"
 * @hibernate.cache usage="read-write"
 */
public abstract class AbstractEntity extends AbstractMetadata implements AbstractEntityInterface
{

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1234523890L;
	/**
	 *
	 */
	protected Set<TablePropertiesInterface> tablePropertiesCollection = new HashSet<TablePropertiesInterface>();

	/**
	 * containerCollection.
	 */
	protected Collection<ContainerInterface> containerCollection = new HashSet<ContainerInterface>();

	/**
	 * It will hold the collection of constraint properties on that entity like foreign key constraint
	 * its referencing primary key & own foreign key properties , constraint name etc.
	 */
	protected Collection<ConstraintPropertiesInterface> constraintPropertiesCollection = new HashSet<ConstraintPropertiesInterface>();

	/**
	 * This method returns the Collection of constraintProperties of this Entity.
	 * @hibernate.set name="constraintPropertiesCollection" table="DYEXTN_CONSTRAINT_PROPERTIES"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="ABSTRACT_ENTITY_ID"
	 * @hibernate.cache usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.databaseproperties.ConstraintProperties"
	 * @return the Collection of TableProperties of this Entity.
	 */
	private Collection<ConstraintPropertiesInterface> getConstraintPropertiesCollection()
	{
		return constraintPropertiesCollection;
	}

	/**
	 * It will set the ConstraintPropertiesCollection
	 * @param constraintPropertiesCollection
	 */
	private void setConstraintPropertiesCollection(
			final Collection<ConstraintPropertiesInterface> constraintPropertiesCollection)
	{
		this.constraintPropertiesCollection = constraintPropertiesCollection;
	}

	/**
	* This method returns the constraintProperties of the Entity.
	* @return the TableProperties of the Entity.
	*/
	public ConstraintPropertiesInterface getConstraintProperties()
	{
		ConstraintPropertiesInterface constraintProperties = null;
		if (constraintPropertiesCollection != null && !constraintPropertiesCollection.isEmpty())
		{
			Iterator constraintPropertiesIterator = constraintPropertiesCollection.iterator();
			constraintProperties = (ConstraintPropertiesInterface) constraintPropertiesIterator
					.next();
		}
		return constraintProperties;
	}

	/**
	 * This method sets the constraintProperties of the Entity to the given TableProperties.
	 * @param tableProperties the TableProperties to be set.
	 */
	public void setConsraintProperties(final ConstraintPropertiesInterface constraintProperties)
	{
		if (constraintPropertiesCollection == null)
		{
			constraintPropertiesCollection = new HashSet<ConstraintPropertiesInterface>();
		}
		else
		{
			this.constraintPropertiesCollection.clear();
		}
		this.constraintPropertiesCollection.add(constraintProperties);
	}

	/**
	 * This method returns the Collection of TableProperties of this Entity.
	 * @hibernate.set name="tablePropertiesColletion" table="DYEXTN_TABLE_PROPERTIES"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="ABSTRACT_ENTITY_ID"
	 * @hibernate.cache usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.databaseproperties.TableProperties"
	 * @return the Collection of TableProperties of this Entity.
	 */
	public Set<TablePropertiesInterface> getTablePropertiesCollection()
	{
		return tablePropertiesCollection;
	}

	/**
	 * @param tablePropertiesCollection the tablePropertiesCollection to set
	 */
	public void setTablePropertiesCollection(Set<TablePropertiesInterface> tablePropertiesCollection)
	{
		this.tablePropertiesCollection = tablePropertiesCollection;
	}

	/**
	 * This method returns the TableProperties of the Entity.
	 * @return the TableProperties of the Entity.
	 */
	public TablePropertiesInterface getTableProperties()
	{
		TablePropertiesInterface tableProperties = null;
		if (tablePropertiesCollection != null && !tablePropertiesCollection.isEmpty())
		{
			Iterator tabletPropertiesIterator = tablePropertiesCollection.iterator();
			tableProperties = (TablePropertiesInterface) tabletPropertiesIterator.next();
		}
		return tableProperties;
	}

	/**
	 * This method sets the TableProperties of the Entity to the given TableProperties.
	 * @param tableProperties the TableProperties to be set.
	 */
	public void setTableProperties(TablePropertiesInterface tableProperties)
	{
		if (tablePropertiesCollection == null)
		{
			tablePropertiesCollection = new HashSet<TablePropertiesInterface>();
		}
		else
		{
			this.tablePropertiesCollection.clear();
		}
		this.tablePropertiesCollection.add(tableProperties);
	}

	/**
	 * @hibernate.set name="containerCollection" table="DYEXTN_CONTAINER"
	 * cascade="save-update" inverse="false" lazy="false"
	 * @hibernate.collection-key column="ABSTRACT_ENTITY_ID"
	 * @hibernate.cache usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.userinterface.Container"
	 * @return Returns the containerCollection.
	 */
	public Collection getContainerCollection()
	{
		return containerCollection;
	}

	/**
	 * @param containerCollection the containerCollection to set
	 */
	public void setContainerCollection(Collection containerCollection)
	{
		this.containerCollection = containerCollection;
	}

	/**
	 * 
	 * @param containerInterface
	 */
	public void addContainer(ContainerInterface containerInterface)
	{
		containerCollection.add(containerInterface);
	}
	
	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.AbstractEntityInterface#getAssociation(edu.common.dynamicextensions.domaininterface.AbstractEntityInterface)
	 */
	public abstract AssociationMetadataInterface getAssociation(AbstractEntityInterface targetEntity);

}
