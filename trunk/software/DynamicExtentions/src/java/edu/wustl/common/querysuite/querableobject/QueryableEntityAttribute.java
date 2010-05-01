
package edu.wustl.common.querysuite.querableobject;

import java.util.Collection;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ColumnPropertiesInterface;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.querysuite.querableobjectinterface.QueryableAttributeInterface;
import edu.wustl.common.querysuite.querableobjectinterface.QueryableObjectInterface;

/**
 * It will represent the Attribute in the Query Module. This class is written to bring the 
 * Attribute & CategoryAttribute at the same level.
 * @author pavan_kalantri
 *
 */
public class QueryableEntityAttribute extends AbstractQueryableAttribute
{

	AttributeInterface attribute;

	/**
	 * parameterized constructor to create QueryableAttribute from attribute. 
	 * @param attribute
	 * @param queryObject
	 */
	public QueryableEntityAttribute(AttributeInterface attribute,
			QueryableObjectInterface queryObject)
	{

		if (attribute == null)
		{
			throw new RuntimeException("Attribute can not be null");
		}
		this.attribute = attribute;
		this.queryObject = queryObject;
	}

	/**
	 * default constructor
	 */
	public QueryableEntityAttribute()
	{
		// TODO Auto-generated method stub
	}

	/**
	 * It will return the column Properties of the attribute from which this object is created
	 * @return columnProperties.
	 */
	public ColumnPropertiesInterface getColumnProperties()
	{
		return attribute.getColumnProperties();
	}

	/**
	 * It will return the data type of the attribute from which this object is created
	 * @return data type.
	 */
	public String getDataType()
	{
		return attribute.getDataType();
	}

	/**
	 * It will check weather the attribute from which this object is created is identified or not.
	 * @return true if identified else false.
	 */
	public Boolean getIsIdentified()
	{
		return attribute.getIsIdentified();
	}

	/**
	 * It will check weather the attribute from which this object is created is nullable or not.
	 * @return true if nullable else false.
	 */
	public Boolean getIsNullable()
	{
		return attribute.getIsNullable();
	}

	/**
	 * It will check weather the attribute from which this object is created is primary key or not.
	 * @return true if primary key else false.
	 */
	public Boolean getIsPrimaryKey()
	{
		return attribute.getIsPrimaryKey();
	}

	/**
	 * It will return the description of the attribute from which this object is created
	 * @return description.
	 */
	public String getDescription()
	{
		return attribute.getDescription();
	}

	/**
	 * It will return the description of the attribute from which this object is created
	 * @return description.
	 */
	public Long getId()
	{
		return attribute.getId();
	}

	/**
	 * It will return the name of the attribute from which this object is created
	 * @return name.
	 */
	public String getName()
	{
		return attribute.getName();
	}

	/**
	 * It will return the collection of the taggedValues of the attribute from which this object is created
	 * @return collection of taggedvalueInerface.
	 */
	public Collection<TaggedValueInterface> getTaggedValueCollection()
	{
		return attribute.getTaggedValueCollection();
	}

	/**
	 * It will check weather the attribute from which this object is created is inherited or not.
	 * @return true if inherited key else false.
	 */
	public boolean isInheritedAttribute()
	{
		return DynamicExtensionsUtility.isInheritedTaggPresent(attribute);
	}

	/**
	 * It will create a QueryableObject from the Entity of the Attribute from which this object is created.
	 * e.g If attribute is Id of Facility & if we call this method on it It will return the MED as Id is actually 
	 * present in MED not facility.
	 * @return QueryableObjectInterface
	 */
	public QueryableObjectInterface getActualEntity()
	{
		return new QueryableEntity(attribute.getEntity());
	}

	/**
	 * It will check weather the attribute from which this object is created has tag with the given key.
	 * @return true if tag present else false.
	 */
	public boolean isTagPresent(String key)
	{
		return QueryableObjectUtility.getTaggedValue(attribute, key).equals("") ? false : true;
	}

	/**
	 * It will return the AttributeTypeInformationInterface of the attribute from which this object is created
	 * @return AttributeTypeInformationInterface.
	 */
	public AttributeTypeInformationInterface getAttributeTypeInformation()
	{
		// TODO Auto-generated method stub
		return attribute.getAttributeTypeInformation();
	}

	/**
	 * It will return the same object on which this method is called.
	 * @return the same object QueryableAttributeInterface
	 */
	public QueryableAttributeInterface getRootQueryableAttribte()
	{
		return this;
	}

	/**
	 * It will return the tagged Value with given key if present on the Attribute
	 * from which this object is derived.
	 * @param key
	 * @return value of tag if found else empty string
	 */
	public String getTaggedValue(String key)
	{
		return QueryableObjectUtility.getTaggedValue(attribute, key);
	}

	/**
	 * It will return the QueryableObject in which this QueryableAttribute resides.
	 */
	public QueryableObjectInterface getQueryEntity()
	{
		return queryObject;
	}

	/**
	 * It will return the name of the Attribute.
	 * @return
	 */
	public String getDisplayName()
	{
		return getName();
	}
}
