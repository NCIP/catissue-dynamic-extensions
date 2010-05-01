
package edu.common.dynamicextensions.domain.databaseproperties;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import edu.common.dynamicextensions.domain.DynamicExtensionBaseDomainObject;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ColumnPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintKeyPropertiesInterface;

/**
 * @version 1.0
 * @created 13-Nov-2008 12:20:07 PM
 * @hibernate.class table="DYEXTN_CONSTRAINTKEY_PROP"
 */
public class ConstraintKeyProperties extends DynamicExtensionBaseDomainObject
		implements
			ConstraintKeyPropertiesInterface
{

	/**
	 * It will hold the collection primary key attribute of the parent on which the tgtForiegnKeyColumnProperties depends
	 */
	private AttributeInterface srcPrimaryKeyAttribute;

	/**
	 * It will hold  the collection of tgtForiegnKeyColumnProperties which depends on the srcPrimaryKeyAttribute 
	 */
	private Collection<ColumnPropertiesInterface> tgtForiegnKeyColumnPropertiesCollection = new HashSet<ColumnPropertiesInterface>();

	/**
	 * @hibernate.id name="id" column="IDENTIFIER" type="long"
	 * length="30" unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="DYEXTN_CNSTRKEY_PROP_SEQ"
	 * @return identifier of ConstraintKeyProperties
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * It will return the Collection of target foriegn key ColumnProperties in entity
	 * @hibernate.set name="tgtForiegnKeyColumnPropertiesCollection" table="DYEXTN_COLUMN_PROPERTIES"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="CNSTR_KEY_PROP_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.databaseproperties.ColumnProperties"
	 * @return Returns the tgtForiegnKeyColumnPropertiesCollection
	 */
	private Collection<ColumnPropertiesInterface> getTgtForiegnKeyColumnPropertiesCollection()
	{
		return tgtForiegnKeyColumnPropertiesCollection;
	}

	/**
	 * Sets the tgtForiegnKeyColumnPropertiesCollection to foreign key column properties collection in parent entity
	 * @param tgtForiegnKeyColumnPropertiesCollection
	 */
	private void setTgtForiegnKeyColumnPropertiesCollection(
			Collection<ColumnPropertiesInterface> tgtForiegnKeyColumnPropertiesCollection)
	{
		this.tgtForiegnKeyColumnPropertiesCollection = tgtForiegnKeyColumnPropertiesCollection;
	}

	/**
	 * It will return the Primitive Attribute on which the Current column properties references
	 *@hibernate.many-to-one column="PRIMARY_ATTRIBUTE_ID" cascade="save-update" class="edu.common.dynamicextensions.domain.Attribute"
	 *@return srcPrimaryKeyAttribute
	 */
	public AttributeInterface getSrcPrimaryKeyAttribute()
	{
		return srcPrimaryKeyAttribute;
	}

	/**
	 * It will set the srcPrimaryKeyAttribute to the given argument
	 */
	public void setSrcPrimaryKeyAttribute(AttributeInterface srcPrimaryKeyAttribute)
	{
		this.srcPrimaryKeyAttribute = srcPrimaryKeyAttribute;
	}

	/**
	 *It will fetch the record from srcPrimaryKeyAttributeCollection & will return
	 * @return AttributeTypeInformationInterface
	 */
	public ColumnPropertiesInterface getTgtForiegnKeyColumnProperties()
	{
		ColumnPropertiesInterface columnProperties = null;
		if (tgtForiegnKeyColumnPropertiesCollection != null
				&& !tgtForiegnKeyColumnPropertiesCollection.isEmpty())
		{
			Iterator tgtForiegnKeyColumnPropertiesIterator = tgtForiegnKeyColumnPropertiesCollection
					.iterator();
			columnProperties = (ColumnPropertiesInterface) tgtForiegnKeyColumnPropertiesIterator
					.next();
		}
		return columnProperties;
	}

	/**
	 * It will add the foriegnKeyColumnProperties to the tgtForiegnKeyColumnPropertiesCollection
	 */
	public void setTgtForiegnKeyColumnProperties(
			ColumnPropertiesInterface foriegnKeyColumnProperties)
	{
		if (tgtForiegnKeyColumnPropertiesCollection == null)
		{
			tgtForiegnKeyColumnPropertiesCollection = new HashSet();
		}
		else
		{
			tgtForiegnKeyColumnPropertiesCollection.clear();
		}

		this.tgtForiegnKeyColumnPropertiesCollection.add(foriegnKeyColumnProperties);

	}

}