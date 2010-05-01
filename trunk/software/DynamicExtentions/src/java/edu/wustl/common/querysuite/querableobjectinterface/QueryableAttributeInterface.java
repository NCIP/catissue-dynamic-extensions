
package edu.wustl.common.querysuite.querableobjectinterface;

import java.io.Serializable;
import java.util.Collection;

import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ColumnPropertiesInterface;

/**
 * This interface is designed to create a wrapper on the CategoryAttribute & normal
 *  Attributes of Entity. This Interface will provide the methods which are required 
 *  to create IQUERY object from the Attribute as well as CategoryAttribute. In case of
 *  category it will internally do he operation on the attribute from which this category attribute 
 *  is created.   
 * @author pavan_kalantri
 *
 */
public interface QueryableAttributeInterface extends Serializable
{

	/**
	 * In case of Attribute it will return the QueryableObject in this QueryableAttribute is present.
	 * 
	 * In case of CategoryAttribute it will create the QueryableObject of the Entity from which the
	 * CategoryEntity of the CategoryAttribute is created & return it. 
	 * @return QueryableObjectInterface
	 */
	QueryableObjectInterface getQueryEntity();

	/**
	 * In case of Attribute it will return the columnProperties of the Attribute.
	 * In case of category attribute it will return the columnProperties of 
	 * the Attribute from which this attribute is created.
	 * @return columnProperties.
	 */
	ColumnPropertiesInterface getColumnProperties();

	/**
	 * In case of Attribute it will return the DataType of the Attribute.
	 * In case of category attribute it will return the DataType of 
	 * the Attribute from which this attribute is created.
	 * @return datatype.
	 */
	String getDataType();

	/**
	 * In case of Attribute it will check weather the Attribute is Identified.
	 * In case of category attribute it will check weather the Attribute from which
	 *  this attribute is created is identified.
	 *  If identified will return true else false.
	 * @return isIdentified.
	 */
	Boolean getIsIdentified();

	/**
	 * In case of Attribute it will check weather the Attribute is Nullable.
	 * In case of category attribute it will check weather the Attribute from which
	 *  this attribute is created is Nullable.
	 *  If Nullable will return true else false.
	 * @return isNullable.
	 */
	Boolean getIsNullable();

	/**
	 * In case of Attribute it will check weather the Attribute is isPrimarykey.
	 * In case of category attribute it will check weather the Attribute from which
	 *  this attribute is created is isPrimaryKey.
	 *  If primaryKey will return true else false.
	 * @return isNullable.
	 */
	Boolean getIsPrimaryKey();

	/**
	 * In case of Attribute it will create the QueryableObject of the Entity of that Attribute
	 * & return it.
	 * e.g If attribute is Id of Facility & if we call this method on it It will return the MED as Id is actually 
	 * present in MED not facility.
	 * In case of CategoryAttribute it will create the QueryableObject of the Entity from which the
	 * CategoryEntity of the CategoryAttribute is created & return it. 
	 * e.g If category contains Id of Facility & if we call this method on it It will return the MED as Id is actually 
	 * present in MED not facility.
	 * @return QueryableObject
	 */
	QueryableObjectInterface getActualEntity();

	/**
	 * It will return the Description of CategoryAttribute or Attribute.
	 * @return description
	 */
	String getDescription();

	/**
	 * It will return the id of the CategoryAttribute or Attribute.
	 * @return
	 */
	Long getId();

	/**
	 * It will return the name of the Attribute or CategoryAttribute.
	 * @return
	 */
	String getName();

	/**
	 * In case of Attribute it will return the tagged value collection of the 
	 * Attribute.
	 * In case of CategoryAttribute it will return the taggedValueCollection of the 
	 * Attribute from which this CategoryAttribute is created.
	 * @return
	 */
	Collection<TaggedValueInterface> getTaggedValueCollection();

	/**
	 * It will check weather the attribute or the Attribute from which the category Attribute is 
	 * created is local inherited copy of the Entity.(It will check weather the "inherited" tag
	 * is present on the Attribute) if present will return true else false. 
	 * @return true if inherited else false.
	 */
	boolean isInheritedAttribute();

	/**
	 * It will check weather the tag with given key is present on the Attribute or the Attribute
	 * from which the CategoryAttribute is created.
	 * @param key
	 * @return true if tag found else false.
	 */
	boolean isTagPresent(String key);

	/**
	 * It will return the AttributeTypeInformation of the Attribute or Attribute from which CategoryAttribute
	 * is created.
	 * @return attributeTypeInformation of atribute.
	 */
	AttributeTypeInformationInterface getAttributeTypeInformation();

	/**
	 * In case of QueryableEntity Attribute it will return the same QueryableAttribute on which this
	 * method is called.
	 * In case of QueryableCategoryAttribute it will create the QueryableEntityAttribute of the Attribute from which 
	 * this categoryAttribute is created.
	 * @return
	 */
	QueryableAttributeInterface getRootQueryableAttribte();

	/**
	 * In case of QueryableEntity Attribute it will return the taggedValue with the given key
	 * on the Attribute From which this QueryableAttribute is created
	 * In case of QueryableCategoryAttribute it will return the taggedValue with the given key 
	 * on the attribute from which the categoryAttribute is created.
	 * @param key
	 * @return value of the tag with the given key if found else empty string
	 */
	String getTaggedValue(String key);

	/**
	 * 
	 * In case of Entity it will return the normal name of Attribute as it is.
	 * In case of category it will return the name of attribute prepended with the 
	 * name of entity in which it is contained.
	 * 
	 */

	String getDisplayName();

}
