
package edu.common.dynamicextensions.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.DataElementInterface;
import edu.common.dynamicextensions.domaininterface.FormulaInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.SkipLogicAttributeInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ColumnPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

/**
 *
 * @author mandar_shidhore
 * @hibernate.joined-subclass table="DYEXTN_CATEGORY_ATTRIBUTE"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class CategoryAttribute extends BaseAbstractAttribute
		implements
			CategoryAttributeInterface,
			AttributeMetadataInterface
{

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 12345235L;

	/**
	 *
	 */
	protected Set<DataElementInterface> dataElementCollection = new HashSet<DataElementInterface>();

	/**
	 *
	 */
	protected Set<ColumnPropertiesInterface> columnPropertiesCollection = new HashSet<ColumnPropertiesInterface>();

	/**
	 *
	 */
	protected Collection<RuleInterface> ruleCollection = new HashSet<RuleInterface>();

	/**
	 *
	 */
	protected AbstractAttributeInterface abstractAttribute;

	/**
	 *
	 */
	protected CategoryEntityInterface categoryEntity;

	/**
	 *
	 */
	protected Collection<PermissibleValueInterface> defaultPermissibleValuesCollection = new HashSet<PermissibleValueInterface>();
	/**
	 *
	 */
	protected Collection<PermissibleValueInterface> skipLogicpermissibleValuesCollection = new HashSet<PermissibleValueInterface>();

	/**
	 *
	 */
	protected Boolean isVisible;
	/**
	 *
	 */
	protected Boolean isRelatedAttribute;
	/**
	 *
	 */
	protected Boolean isCalculated;
	/**
	 *
	 */
	protected Boolean isSkipLogic;
	/**
	 * Collection of formulae.
	 */
	protected Collection<FormulaInterface> formulaCollection = new HashSet<FormulaInterface>();
	/**
	 * Collection of calculated CategoryAttribute Collection.
	 */
	protected Collection<CategoryAttributeInterface> calculatedCategoryAttributeCollection = new HashSet<CategoryAttributeInterface>();
	/**
	 * Collection of category attributes where in this category attribute is used in formula.
	 */
	protected Collection<CategoryAttributeInterface> calculatedDependentCategoryAttributes = new HashSet<CategoryAttributeInterface>();
	/**
	 * Collection of category attributes.
	 */
	protected Collection<SkipLogicAttributeInterface> dependentSkipLogicAttributes = new HashSet<SkipLogicAttributeInterface>();
	/**
	 * This method returns the Collection of Column Properties of the Attribute.
	 * @hibernate.set name="columnPropertiesCollection" table="DYEXTN_COLUMN_PROPERTIES" cascade="all" inverse="false" lazy="false"
	 * @hibernate.collection-key column="CATEGORY_ATTRIBUTE_ID"
	 * @hibernate.cache usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.databaseproperties.ColumnProperties"
	 * @return the Collection of Column Properties of the Attribute.
	 */
	private Set<ColumnPropertiesInterface> getColumnPropertiesCollection()
	{
		return columnPropertiesCollection;
	}

	/**
	 * This method sets the columnPropertiesCollection to given Collection of the ColumnProperties.
	 * @param columnPropertiesCollection the Collection of the ColumnProperties to be set.
	 */
	private void setColumnPropertiesCollection(
			Set<ColumnPropertiesInterface> columnPropertiesCollection)
	{
		this.columnPropertiesCollection = columnPropertiesCollection;
	}

	/**
	 * This method returns the ColumnProperties of the Attribute.
	 * @return the ColumnProperties of the Attribute.
	 */
	public ColumnPropertiesInterface getColumnProperties()
	{
		ColumnPropertiesInterface columnProperties = null;
		if (columnPropertiesCollection != null && !columnPropertiesCollection.isEmpty())
		{
			Iterator columnPropertiesIterator = columnPropertiesCollection.iterator();
			columnProperties = (ColumnPropertiesInterface) columnPropertiesIterator.next();
		}
		return columnProperties;
	}

	/**
	 * This method sets the ColumnProperties of the Attribute.
	 * @param columnProperties the ColumnProperties to be set.
	 */
	public void setColumnProperties(ColumnPropertiesInterface columnProperties)
	{
		if (columnPropertiesCollection == null)
		{
			columnPropertiesCollection = new HashSet<ColumnPropertiesInterface>();
		}
		else
		{
			columnPropertiesCollection.clear();
		}
		this.columnPropertiesCollection.add(columnProperties);
	}

	/**
	 * @hibernate.set name="dataElementCollection" table="DYEXTN_DATA_ELEMENT" cascade="save-update" inverse="false" lazy="false"
	 * @hibernate.collection-key column="CATEGORY_ATTRIBUTE_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.DataElement"
	 * @return Returns the dataElementCollection.
	 */
	private Set<DataElementInterface> getDataElementCollection()
	{
		return dataElementCollection;
	}

	/**
	 * @param dataElementCollection The dataElementCollection to set.
	 */
	private void setDataElementCollection(Set<DataElementInterface> dataElementCollection)
	{
		this.dataElementCollection = dataElementCollection;
	}

	/**
	 *
	 * @return
	 */
	public DataElementInterface getDataElement()
	{
		DataElementInterface dataElementInterface = null;
		if (dataElementCollection != null)
		{
			Iterator dataElementIterator = dataElementCollection.iterator();
			if (dataElementIterator.hasNext())
			{
				dataElementInterface = (DataElement) dataElementIterator.next();
			}
		}
		return dataElementInterface;
	}

	public void clearDataElementCollection()
	{
		dataElementCollection = null;
	}

	/**
	 *
	 * @param dataElementInterface
	 */
	public void setDataElement(DataElementInterface dataElementInterface)
	{
		if (dataElementCollection == null)
		{
			dataElementCollection = new HashSet();
		}
		this.dataElementCollection.add(dataElementInterface);
	}

	/**
	 * This method returns the Collection of rules.
	 * @hibernate.set name="ruleCollection" table="DYEXTN_RULE"
	 * cascade="save-update" inverse="false" lazy="false"
	 * @hibernate.collection-key column="CATEGORY_ATTR_ID"
	 * @hibernate.cache usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.validationrules.Rule"
	 * @return Collection the ruleCollection associated with the Attribute.
	 */
	public Collection<RuleInterface> getRuleCollection()
	{
		return ruleCollection;
	}

	/**
	 * @param ruleCollection the ruleCollection to set
	 */
	public void setRuleCollection(Collection<RuleInterface> ruleCollection)
	{
		this.ruleCollection = ruleCollection;
	}

	/**
	 * @hibernate.many-to-one column="ABSTRACT_ATTRIBUTE_ID" cascade="save-update" class="edu.common.dynamicextensions.domain.AbstractAttribute"
	 * @return the attribute
	 */
	public AbstractAttributeInterface getAbstractAttribute()
	{
		return abstractAttribute;
	}

	/**
	 * @param attribute the attribute to set
	 */
	public void setAbstractAttribute(AbstractAttributeInterface attribute)
	{
		this.abstractAttribute = attribute;
	}

	/**
	 * This method returns the Entity associated with this Attribute.
	 * @hibernate.many-to-one column="CATEGORY_ENTIY_ID" class="edu.common.dynamicextensions.domain.CategoryEntity" constrained="true"
	 * @return CategoryEntityInterface the Entity associated with the Attribute.
	 */
	public CategoryEntityInterface getCategoryEntity()
	{
		return categoryEntity;
	}

	/**
	 * This method sets the Entity associated with this Attribute.
	 * @param entityInterface The entity to be set.
	 */
	public void setCategoryEntity(CategoryEntityInterface categoryEntityInterface)
	{
		if (categoryEntityInterface != null)
		{
			this.categoryEntity = categoryEntityInterface;
		}
	}

	/**
	 * @hibernate.set name="defaultPermissibleValuesCollection" table="DYEXTN_PERMISSIBLE_VALUE"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="CATEGORY_ATTRIBUTE_ID"
	 * @hibernate.cache usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.PermissibleValue"
	 * @return Returns the dataElementCollection.
	 */
	private Collection<PermissibleValueInterface> getDefaultPermissibleValuesCollection()
	{
		return defaultPermissibleValuesCollection;
	}

	/**
	 * @param defaultPermissibleValuesCollection the defaultPermissibleValuesCollection to set
	 */
	private void setDefaultPermissibleValuesCollection(
			Collection<PermissibleValueInterface> defaultPermissibleValuesCollection)
	{
		this.defaultPermissibleValuesCollection = defaultPermissibleValuesCollection;
	}
	/**
	 *
	 * @return
	 */
	public PermissibleValueInterface getDefaultValuePermissibleValue()
	{
		PermissibleValueInterface permissibleValueInterface = null;
		if (defaultPermissibleValuesCollection != null
				&& !defaultPermissibleValuesCollection.isEmpty())
		{
			Iterator<PermissibleValueInterface> dataElementIter = defaultPermissibleValuesCollection
					.iterator();
			permissibleValueInterface = dataElementIter.next();
		}
		return permissibleValueInterface;
	}
	/**
	 * @hibernate.set name="skipLogicpermissibleValuesCollection" table="DYEXTN_PERMISSIBLE_VALUE"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="SKIP_LOGIC_CAT_ATTR_ID"
	 * @hibernate.cache usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.PermissibleValue"
	 * @return Returns the dataElementCollection.
	 */
	private Collection<PermissibleValueInterface> getSkipLogicPermissibleValuesCollection()
	{
		return skipLogicpermissibleValuesCollection;
	}

	/**
	 * @param defaultPermissibleValuesCollection the defaultPermissibleValuesCollection to set
	 */
	private void setSkipLogicPermissibleValuesCollection(
			Collection<PermissibleValueInterface> skipLogicpermissibleValuesCollection)
	{
		this.skipLogicpermissibleValuesCollection = skipLogicpermissibleValuesCollection;
	}
	/**
	 * @param sourceEntity
	 */
	public void addSkipLogicPermissibleValue(PermissibleValueInterface permissibleValue)
	{
		if (skipLogicpermissibleValuesCollection == null)
		{
			skipLogicpermissibleValuesCollection = new HashSet<PermissibleValueInterface>();
		}
		this.skipLogicpermissibleValuesCollection.add(permissibleValue);
	}
	/**
	 * @param sourceEntity
	 */
	public void removeAllSkipLogicPermissibleValues()
	{
		if (skipLogicpermissibleValuesCollection != null)
		{
			this.skipLogicpermissibleValuesCollection.clear();
		}
	}
	/**
	 *
	 * @return
	 */
	public Collection<PermissibleValueInterface> getSkipLogicPermissibleValues()
	{
		return skipLogicpermissibleValuesCollection;
	}

	/**
	 *
	 * @param permissibleValue
	 * @return
	 */
	public PermissibleValueInterface getSkipLogicPermissibleValue(PermissibleValueInterface permissibleValue)
	{
		PermissibleValueInterface permissibleValueInterface = null;
		if (skipLogicpermissibleValuesCollection != null
				&& !skipLogicpermissibleValuesCollection.isEmpty())
		{
			for (PermissibleValueInterface skipLogicPermissibleValue : skipLogicpermissibleValuesCollection)
			{
				if (skipLogicPermissibleValue.equals(permissibleValue))
				{
					permissibleValueInterface = skipLogicPermissibleValue;
					break;
				}
			}
		}
		return permissibleValueInterface;
	}
	/**
	 * @hibernate.set name="formulaCollection" table="DYEXTN_FORMULA"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="CATEGORY_ATTRIBUTE_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.Formula"
	 * @return Returns the controlCollection.
	 */
	private Collection<FormulaInterface> getFormulaCollection()
	{
		return formulaCollection;
	}

	/**
	 * @param setFormulaCollection The controlCollection to set.
	 */
	private void setFormulaCollection(Collection<FormulaInterface> formulaCollection)
	{
		this.formulaCollection = formulaCollection;
	}
	/**
	 * @hibernate.set name="calculatedCategoryAttributeCollection" table="DYEXTN_CATEGORY_ATTRIBUTE"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="CAL_CATEGORY_ATTR_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.CategoryAttribute"
	 * @return Returns the calculatedCategoryAttributeCollection.
	 */
	public Collection<CategoryAttributeInterface> getCalculatedCategoryAttributeCollection()
	{
		return calculatedCategoryAttributeCollection;
	}
	/**
	 *
	 * @param calculatedCategoryAttributeCollection
	 */
	public void setCalculatedCategoryAttributeCollection(
			Collection<CategoryAttributeInterface> calculatedCategoryAttributeCollection)
	{
		this.calculatedCategoryAttributeCollection = calculatedCategoryAttributeCollection;
	}
	/**
	 *
	 */
	public void addCalculatedCategoryAttribute(CategoryAttributeInterface categoryAttributeInterface)
	{
		if (this.calculatedCategoryAttributeCollection == null)
		{
			this.calculatedCategoryAttributeCollection = new HashSet<CategoryAttributeInterface>();
		}
		this.calculatedCategoryAttributeCollection.add(categoryAttributeInterface);
	}
	/**
	 * This method removes all Calculated Category Attributes.
	 */
	public void removeAllCalculatedCategoryAttributes()
	{
		calculatedCategoryAttributeCollection.clear();
	}
	/**
	 * @hibernate.set name="calculatedCategoryAttributeCollection" table="DYEXTN_CATEGORY_ATTRIBUTE"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="CAL_DEPENDENT_CATEGORY_ATTR_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.CategoryAttribute"
	 * @return Returns the calculatedDependentCategoryAttributes.
	 */
	public Collection<CategoryAttributeInterface> getCalculatedDependentCategoryAttributes()
	{
		return calculatedDependentCategoryAttributes;
	}
	/**
	 *
	 * @param calculatedDependentCategoryAttributes
	 */
	public void setCalculatedDependentCategoryAttributes(
			Collection<CategoryAttributeInterface> calculatedDependentCategoryAttributes)
	{
		this.calculatedDependentCategoryAttributes = calculatedDependentCategoryAttributes;
	}
	/**
	 * This method removes all Calculated Category Attributes.
	 */
	public void removeAllCalculatedDependentCategoryAttributes()
	{
		calculatedCategoryAttributeCollection.clear();
	}
	/**
	 *
	 */
	public void addCalculatedDependentCategoryAttribute(CategoryAttributeInterface categoryAttributeInterface)
	{
		if (this.calculatedDependentCategoryAttributes == null)
		{
			this.calculatedDependentCategoryAttributes = new HashSet<CategoryAttributeInterface>();
		}
		this.calculatedDependentCategoryAttributes.add(categoryAttributeInterface);
	}
	/**
	 * This method return the default value for the category attribute if set otherwise
	 * return the default value for the original attribute
	 * @return
	 */
	public String getDefaultValue()
	{
		String defaultValue = null;

		if (defaultPermissibleValuesCollection != null
				&& !defaultPermissibleValuesCollection.isEmpty())
		{
			Iterator<PermissibleValueInterface> dataElementIter = defaultPermissibleValuesCollection
					.iterator();
			Object nextPV = dataElementIter.next().getValueAsObject();
			if (nextPV != null)
			{
				defaultValue = String.valueOf(nextPV);
			}
		}
		else
		{
			defaultValue = getDefaultValueForAbstractAttribute();

		}

		return defaultValue;
	}

	/**
	 * It will check weather categoryAttribute's abstract attribute is attribute or association.
	 * If it is association will return null else will return the default value of the original attribute
	 * @return
	 */
	private String getDefaultValueForAbstractAttribute()
	{
		String defaultValue = null;
		AbstractAttributeInterface attribute = this.abstractAttribute;
		if (attribute instanceof Attribute)
		{
			defaultValue = ((AttributeMetadataInterface) this.abstractAttribute).getDefaultValue();
		}
		return defaultValue;
	}

	/**
	 * @param sourceEntity
	 */
	public void setDefaultValue(PermissibleValueInterface permissibleValue)
	{
		if (defaultPermissibleValuesCollection == null)
		{
			defaultPermissibleValuesCollection = new HashSet<PermissibleValueInterface>();
		}
		if (defaultPermissibleValuesCollection != null
				&& !defaultPermissibleValuesCollection.isEmpty())
		{
			Iterator<PermissibleValueInterface> iterator = defaultPermissibleValuesCollection
					.iterator();
			defaultPermissibleValuesCollection.remove(iterator.next());
		}

		this.defaultPermissibleValuesCollection.add(permissibleValue);
	}
	/**
	 *
	 * @param formulaInterface
	 */
	public void setFormula(FormulaInterface formulaInterface)
	{
		if (formulaCollection == null)
		{
			formulaCollection = new HashSet<FormulaInterface>();
		}
		if (formulaCollection != null
				&& !formulaCollection.isEmpty())
		{
			Iterator<FormulaInterface> iterator = formulaCollection
					.iterator();
			formulaCollection.remove(iterator.next());
		}

		this.formulaCollection.add(formulaInterface);
	}
	/**
	 * This method return the formula.
	 * @return
	 */
	public FormulaInterface getFormula()
	{
		FormulaInterface formula = null;
		if (formulaCollection != null
				&& !formulaCollection.isEmpty())
		{
			Iterator<FormulaInterface> iterator = formulaCollection
					.iterator();
			formula = iterator.next();
		}
		return formula;
	}
	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface#getMaxSize()
	 */
	public int getMaxSize()
	{
		return ((AttributeMetadataInterface) this.abstractAttribute).getMaxSize();
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface#getMeasurementUnit()
	 */
	public String getMeasurementUnit()
	{
		return ((AttributeMetadataInterface) this.abstractAttribute).getMeasurementUnit();
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface#getDecimalPlaces()
	 */
	public int getDecimalPlaces()
	{
		return ((AttributeMetadataInterface) this.abstractAttribute).getDecimalPlaces();
	}

	public AttributeTypeInformationInterface getAttributeTypeInformation()
	{
		return ((AttributeMetadataInterface) this.abstractAttribute).getAttributeTypeInformation();
	}

	/**
	* @hibernate.property name="isVisible" type="boolean" column="IS_VISIBLE"
	*/
	public Boolean getIsVisible()
	{
		return isVisible;
	}

	/**
	 * @param isVisible the isVisible to set
	 */
	public void setIsVisible(Boolean isVisible)
	{
		this.isVisible = isVisible;
	}

	/**
	* @hibernate.property name="isRelatedAttribute" type="boolean" column="IS_RELATTRIBUTE"
	*/
	public Boolean getIsRelatedAttribute()
	{
		return isRelatedAttribute;
	}

	/**
	 * @param isRelatedAttribute the isRelatedAttribute to set
	 */
	public void setIsRelatedAttribute(Boolean isRelatedAttribute)
	{
		this.isRelatedAttribute = isRelatedAttribute;
	}
	/**
	* @hibernate.property name="isCalculated" type="boolean" column="IS_CAL_ATTRIBUTE"
	*/
	public Boolean getIsCalculated()
	{
		return isCalculated;
	}
	/**
	 *
	 * @param isCalculatedAttribute
	 */
	public void setIsCalculated(Boolean isCalculatedAttribute)
	{
		this.isCalculated = isCalculatedAttribute;
	}
	/**
	* @hibernate.property name="isSkipLogic" type="boolean" column="IS_SKIP_LOGIC"
	*/
	public Boolean getIsSkipLogic()
	{
		return isSkipLogic;
	}
	/**
	 *
	 * @param isSkipLogic
	 */
	public void setIsSkipLogic(Boolean isSkipLogic)
	{
		this.isSkipLogic = isSkipLogic;
	}

	/**
	 *
	 */
	public boolean isValuePresent(Object value) throws DynamicExtensionsSystemException
	{
		return ((AttributeMetadataInterface) this.getAbstractAttribute()).isValuePresent(value);
	}
	/**
	 * @hibernate.set name="dependentSkipLogicAttributes" table="DYEXTN_SKIP_LOGIC_ATTRIBUTE"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="CAT_ATTR_ID"
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
	 * @return attributeInterface
	 */
	public AttributeInterface getAttribute()
	{
		AttributeInterface attribute = (AttributeInterface) this.getAbstractAttribute();
		return attribute;
	}

}