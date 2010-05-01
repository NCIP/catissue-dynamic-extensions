
package edu.wustl.common.querysuite.querableobject;

import java.util.Collection;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ColumnPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.querysuite.querableobjectinterface.QueryableAttributeInterface;
import edu.wustl.common.querysuite.querableobjectinterface.QueryableObjectInterface;

/**
 * It will represent the CategoryAttribute in the Query Module. This class is written to bring the 
 * Attribute & CategoryAttribute at the same level.
 * @author pavan_kalantri
 *
 */
public class QueryableCategoryAttribute extends AbstractQueryableAttribute

{

	private CategoryAttributeInterface categoryAttribute;

	/**
	 * Parameterized constructor for creating the queryableAttribute derived from the attribute
	 * @param attribute
	 * @param queryObject
	 */
	public QueryableCategoryAttribute(CategoryAttributeInterface attribute,
			QueryableObjectInterface queryObject)
	{
		if (attribute == null)
		{
			throw new RuntimeException("Attribute can not be null");
		}
		this.categoryAttribute = attribute;
		this.queryObject = queryObject;
	}

	/**
	 * default constructor
	 */
	public QueryableCategoryAttribute()
	{
		// TODO Auto-generated method stub
	}

	/**
	 * It will return the columnProperties of the actual Attribute from which the CategoryAttribue
	 * is created.
	 * @return columnProperties 
	 */
	public ColumnPropertiesInterface getColumnProperties()
	{
		// TODO Auto-generated method stub
		return QueryableObjectUtility.getAttributeFromCategoryAttribute(categoryAttribute)
				.getColumnProperties();
	}

	/**
	 * It will return the dataType of the actual Attribute from which the CategoryAttribue
	 * is created
	 * @return dataType 
	 */
	public String getDataType()
	{
		return QueryableObjectUtility.getAttributeFromCategoryAttribute(categoryAttribute)
				.getDataType();
	}

	/**
	 * It will check weather the actual Attribute from which the CategoryAttribue created is Identified 
	 * or not.
	 * @return true if attribute is Identified else false. 
	 */
	public Boolean getIsIdentified()
	{
		return QueryableObjectUtility.getAttributeFromCategoryAttribute(categoryAttribute)
				.getIsIdentified();
	}

	/**
	 * It will check weather the actual Attribute from which the CategoryAttribue created is Nullable 
	 * or not.
	 * @return true if attribute is Nullable else false. 
	 */
	public Boolean getIsNullable()
	{
		return QueryableObjectUtility.getAttributeFromCategoryAttribute(categoryAttribute)
				.getIsNullable();
	}

	/**
	 * It will check weather the actual Attribute from which the CategoryAttribue created is primary key 
	 * or not.
	 * @return true if attribute is primary key else false. 
	 */
	public Boolean getIsPrimaryKey()
	{
		return QueryableObjectUtility.getAttributeFromCategoryAttribute(categoryAttribute)
				.getIsPrimaryKey();
	}

	/**
	 * It will return the QueryableObject Created from the Entity in which the Attribute from which the Category 
	 * Attribute is created present.
	 * e.g If category contains Id of Facility & if we call this method on it It will return the MED as Id is actually 
	 * present in MED not facility.
	 * @return QueryableObjectInterface
	 */
	public QueryableObjectInterface getActualEntity()
	{
		return new QueryableEntity(QueryableObjectUtility.getAttributeFromCategoryAttribute(
				categoryAttribute).getEntity());

	}

	/**
	 * it will return the description of the categoryAttribute
	 * @return description
	 */
	public String getDescription()
	{
		return categoryAttribute.getDescription();
	}

	/**
	 * it will return the id of the categoryAttribute
	 * @return id
	 */
	public Long getId()
	{
		return categoryAttribute.getId();
	}

	/**
	 * It will return the name of the categoryAttribute.
	 * it will internally remove the last appended "Category Attribute" From the name of Attribute.
	 * @return name
	 */
	public String getName()
	{
		String name = categoryAttribute.getName();
		int index = name.lastIndexOf(" Category Attribute");
		if (index != -1)
		{
			name = name.substring(0, index);
		}
		return name;
	}

	/**
	 * It will return the taggedValueCollection of the actual attribute from which the categoryAttribute
	 * is created.
	 * @return taggedValueCollection
	 */
	public Collection<TaggedValueInterface> getTaggedValueCollection()
	{
		return QueryableObjectUtility.getAttributeFromCategoryAttribute(categoryAttribute)
				.getTaggedValueCollection();
	}

	/**
	 * It will return the categoryEntity in which the categoryAtribute is present.
	 * @return categoryEntity of the categoryAttribute 
	 */
	public CategoryEntityInterface getCategoryEntity()
	{// TODO Auto-generated method stub 
		return categoryAttribute.getCategoryEntity();
	}

	/**
	 * It will check weather the categoryAttribute is relatedAttribute or not.
	 * @return true if categoryAtribute is relatedAttribute else false.
	 */
	public Boolean getIsRelatedAttribute()
	{// TODO Auto-generated method stub 
		return categoryAttribute.getIsRelatedAttribute();
	}

	/**
	 * It will return weather the categoryAttribute is visible or not.
	 * @return true if visible else false.
	 */
	public Boolean getIsVisible()
	{
		// TODO Auto-generated method stub 
		return categoryAttribute.getIsVisible();
	}

	/**
	 * It will check weather the Attribute from which this categoryAttribute is created is inherited or not.
	 * @return
	 */
	public boolean isInheritedAttribute()
	{
		return DynamicExtensionsUtility.isInheritedTaggPresent(QueryableObjectUtility
				.getAttributeFromCategoryAttribute(categoryAttribute));

	}

	/**
	 * It checks weather the tag with given key is present on the Attribute From which this
	 * CategoryAttribute is created.
	 * @return true if tag present else false.
	 */
	public boolean isTagPresent(String key)
	{
		AttributeInterface atribute = QueryableObjectUtility
				.getAttributeFromCategoryAttribute(categoryAttribute);
		return QueryableObjectUtility.getTaggedValue(atribute, key).equals("") ? false : true;
	}

	/**
	 * It will return the attribute type information of the Attribute from which the Category Attribute is
	 * created
	 * @return AttributeTypeInformationInterface
	 */
	public AttributeTypeInformationInterface getAttributeTypeInformation()
	{
		// TODO Auto-generated method stub
		return QueryableObjectUtility.getAttributeFromCategoryAttribute(categoryAttribute)
				.getAttributeTypeInformation();
	}

	/**
	 * It will create the QueryableEntityAttribute for the attribute from which the categoryAttribute of
	 * this Object is created & will return that.
	 * @return QueryableAttributeInterface.
	 */
	public QueryableAttributeInterface getRootQueryableAttribte()
	{

		AttributeInterface attribute = QueryableObjectUtility
				.getAttributeFromCategoryAttribute(categoryAttribute);
		return QueryableObjectUtility.createQueryableAttribute(attribute, attribute.getEntity());
	}

	/**
	 * It will return the taggedValue with the given key in the attribute from which this categoryAttribute is derived.
	 * @param key
	 * @return taggedValue if found else empty string.
	 */
	public String getTaggedValue(String key)
	{
		AttributeInterface atribute = QueryableObjectUtility
				.getAttributeFromCategoryAttribute(categoryAttribute);
		return QueryableObjectUtility.getTaggedValue(atribute, key);
	}

	/**
	 * It will create and return the queryableEntity for the Entity from which the categoryEntity of the
	 * categoryAttribute is created.
	 * @return QueryableObejctInterface
	 */
	public QueryableObjectInterface getQueryEntity()
	{
		return new QueryableEntity(categoryAttribute.getCategoryEntity().getEntity());
	}

	/**
	 * It will return the name of attribute which user has provided in csv file as its display name
	 * (i.e. its caption).
	 * @return 
	 */
	public String getDisplayName()
	{
		ContainerInterface container = (ContainerInterface) categoryAttribute.getCategoryEntity()
				.getContainerCollection().iterator().next();
		return QueryableObjectUtility.getControlCaption(container, categoryAttribute);

	}

}
