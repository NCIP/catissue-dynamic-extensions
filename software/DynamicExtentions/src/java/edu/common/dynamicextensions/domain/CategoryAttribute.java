
package edu.common.dynamicextensions.domain;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.CalculatedAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.DataElementInterface;
import edu.common.dynamicextensions.domaininterface.FormulaInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.SkipLogicAttributeInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ColumnPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.util.Constants;

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

	/** The is populate from xml. */
	protected boolean isPopulateFromXml = false;

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 12345235L;

	/** The data element collection. */
	protected Set<DataElementInterface> dataElementCollection = new HashSet<DataElementInterface>();

	/** The column properties collection. */
	protected Set<ColumnPropertiesInterface> columnPropertiesCollection = new HashSet<ColumnPropertiesInterface>();

	/** The rule collection. */
	protected Collection<RuleInterface> ruleCollection = new HashSet<RuleInterface>();

	/** The abstract attribute. */
	protected AbstractAttributeInterface abstractAttribute;

	/** The category entity. */
	protected CategoryEntityInterface categoryEntity;

	/** The default permissible values collection. */
	protected Collection<PermissibleValueInterface> defaultPermissibleValuesCollection = new HashSet<PermissibleValueInterface>();

	/**
	 * 100,101,102
	 * The permissible value used by sourceSkipLogicAttribute, depending on which
	 * the targetSkipLogicAttribute works. PV is a Reference of Cat attr PV
	 */
	protected Collection<PermissibleValueInterface> skipLogicpermissibleValuesCollection = new HashSet<PermissibleValueInterface>();

	/** The is visible. */
	protected Boolean isVisible;

	/** The is related attribute. */
	protected Boolean isRelatedAttribute;

	/** The is calculated. */
	protected Boolean isCalculated;

	/** The is source for calculated attribute. */
	protected Boolean isSourceForCalculatedAttribute = false;

	/**
	 * Set for sourceSkipLogicAttribute
	 */
	protected Boolean isSkipLogic;
	/**
	 * Collection of formulae.
	 */
	protected Collection<FormulaInterface> formulaCollection = new HashSet<FormulaInterface>();
	/**
	 * Collection of calculated CategoryAttribute Collection.
	 */
	protected Collection<CalculatedAttributeInterface> calculatedCategoryAttributeCollection = new HashSet<CalculatedAttributeInterface>();
	/**
	 * Collection of calculated CategoryAttribute Collection.
	 */
	protected Collection<CategoryAttributeInterface> calculatedAttributeCollection = new HashSet<CategoryAttributeInterface>();
	/**
	 * Collection of category attributes where in this category attribute is used in formula.
	 */
	protected Collection<CategoryAttributeInterface> calculatedDependentCategoryAttributes = new HashSet<CategoryAttributeInterface>();
	/**
	 * Collection of category attributes.
	 */
	protected Collection<SkipLogicAttributeInterface> dependentSkipLogicAttributes = new HashSet<SkipLogicAttributeInterface>();

	/** The default skip logic value. */
	protected PermissibleValueInterface defaultSkipLogicValue;

	/**
	 * Gets the default skip logic value.
	 * @return the defaultSkipLogicValue
	 */
	public PermissibleValueInterface getDefaultSkipLogicValue()
	{
		return defaultSkipLogicValue;
	}

	/**
	 * Sets the default skip logic value.
	 * @param defaultSkipLogicValue the defaultSkipLogicValue to set
	 */
	public void setDefaultSkipLogicValue(PermissibleValueInterface defaultSkipLogicValue)
	{
		this.defaultSkipLogicValue = defaultSkipLogicValue;
	}

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
		columnPropertiesCollection.add(columnProperties);
	}

	/**
	 * @hibernate.set name="dataElementCollection" table="DYEXTN_DATA_ELEMENT" cascade="save-update" inverse="false" lazy="false"
	 * @hibernate.collection-key column="CATEGORY_ATTRIBUTE_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.DataElement"
	 * @return Returns the dataElementCollection.
	 */
	public Set<DataElementInterface> getDataElementCollection()
	{
		return dataElementCollection;
	}

	/**
	 * @param dataElementCollection The dataElementCollection to set.
	 */
	public void setDataElementCollection(Set<DataElementInterface> dataElementCollection)
	{
		this.dataElementCollection = dataElementCollection;
	}

	/**
	 * This method returns data element interface for attribute
	 * based on PV version.
	 * @param encounterDate date of encounter of visit.
	 * @return dataElementInterface based on PV version.
	 */
	public DataElementInterface getDataElement(Date encounterDate)
	{
		DataElementInterface dataElementInterface = null;
		if (dataElementCollection != null)
		{
			Iterator<DataElementInterface> dataElementIterator = dataElementCollection.iterator();
			dataElementInterface = getDataElement(dataElementIterator, encounterDate);

		}
		return dataElementInterface;
	}

	/**
	 * This method returns the nearest past date of encounter date.
	 * @param dataElementIterator Data element collection to be process.
	 * @param encounterDate Encounter date of visit.
	 * @return DataElementInterface data element with the activation date is the nearest
	 * past date of encounter date.
	 */
	private DataElementInterface getDataElement(Iterator<DataElementInterface> dataElementIterator,
			Date encounterDate)
	{
		DataElementInterface dataElementInterface = null;
		Map<String, UserDefinedDEInterface> dateToUDDEInterface = new HashMap<String, UserDefinedDEInterface>();
		while (dataElementIterator.hasNext())
		{
			dataElementInterface = dataElementIterator.next();
			if (dataElementInterface instanceof UserDefinedDEInterface)
			{
				UserDefinedDEInterface userDefinedDEInterface = (UserDefinedDEInterface) dataElementInterface;
				getPVMap(encounterDate, dateToUDDEInterface, userDefinedDEInterface);
			}
		}
		if (dateToUDDEInterface.get(Constants.PV_With_Nearest_Past_Date) == null)
		{
			dataElementInterface = dateToUDDEInterface.get("Default");
		}
		else
		{
			dataElementInterface = dateToUDDEInterface.get(Constants.PV_With_Nearest_Past_Date);
		}
		return dataElementInterface;
	}

	/**
	 * This method will set a map for the default PV version and
	 * PV version with the nearest past date then encounter date.
	 * @param encounterDate Encounter date of visit.
	 * @param dateToUDDEInterface Which contains default UserDefinedDEInterface
	 * and UserDefinedDEInterface with the nearest activation date from encounter date.
	 * @param userDefinedDEInterface Whose activation date need to be compare with the
	 * current nearest date UserDefinedDEInterface.
	 */
	private void getPVMap(Date encounterDate,
			Map<String, UserDefinedDEInterface> dateToUDDEInterface,
			UserDefinedDEInterface userDefinedDEInterface)
	{
		if (userDefinedDEInterface.getActivationDate() == null)
		{
			dateToUDDEInterface.put("Default", userDefinedDEInterface);
		}
		else if (encounterDate != null
				&& userDefinedDEInterface.getActivationDate().before(encounterDate))
		{

			getPVWithNearestPastDate(dateToUDDEInterface, userDefinedDEInterface);
		}
	}

	/**
	 * Check whether the activation date of the UserDefinedDEInterface is after the
	 * current nearest value or not.And if its after the current nearest value then replace with
	 * the current UserDefinedDEInterface.
	 * @param dateToUDDEInterface Which contains default UserDefinedDEInterface
	 * and UserDefinedDEInterface with the nearest activation date from encounter date.
	 * @param userDefinedDEInterface Whose activation date need to be compare with the
	 * current nearest date UserDefinedDEInterface.
	 */
	private void getPVWithNearestPastDate(Map<String, UserDefinedDEInterface> dateToUDDEInterface,
			UserDefinedDEInterface userDefinedDEInterface)
	{
		if (dateToUDDEInterface.get(Constants.PV_With_Nearest_Past_Date) == null)
		{
			dateToUDDEInterface.put(Constants.PV_With_Nearest_Past_Date, userDefinedDEInterface);
		}
		else
		{
			Date PVWithNearestPastDate = dateToUDDEInterface.get(
					Constants.PV_With_Nearest_Past_Date).getActivationDate();
			if (userDefinedDEInterface.getActivationDate().after(PVWithNearestPastDate))
			{
				dateToUDDEInterface
						.put(Constants.PV_With_Nearest_Past_Date, userDefinedDEInterface);
			}
		}
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
		dataElementCollection.add(dataElementInterface);
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
		abstractAttribute = attribute;
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
			categoryEntity = categoryEntityInterface;
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
	public Collection<PermissibleValueInterface> getSkipLogicPermissibleValuesCollection()
	{
		return skipLogicpermissibleValuesCollection;
	}

	/**
	 * @param defaultPermissibleValuesColl the defaultPermissibleValuesCollection to set
	 */
	public void setSkipLogicPermissibleValuesCollection(
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
		skipLogicpermissibleValuesCollection.add(permissibleValue);
	}

	/**
	 * @param sourceEntity
	 */
	public void removeAllSkipLogicPermissibleValues()
	{
		if (skipLogicpermissibleValuesCollection != null)
		{
			skipLogicpermissibleValuesCollection.clear();
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
	public PermissibleValueInterface getSkipLogicPermissibleValue(
			PermissibleValueInterface permissibleValue)
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
	 * @hibernate.set name="calculatedAttributeCollection" table="DYEXTN_CATEGORY_ATTRIBUTE"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="CAL_CATEGORY_ATTR_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.CategoryAttribute"
	 * @return Returns the calculatedAttributeCollection.
	 */
	public Collection<CategoryAttributeInterface> getCalculatedAttributeCollection()
	{
		return calculatedAttributeCollection;
	}

	/**
	 *
	 * @param calculatedAttributeCollection
	 */
	public void setCalculatedAttributeCollection(
			Collection<CategoryAttributeInterface> calculatedAttributeCollection)
	{
		this.calculatedAttributeCollection = calculatedAttributeCollection;
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
	 *
	 */
	public void addCalculatedDependentCategoryAttribute(
			CategoryAttributeInterface categoryAttributeInterface)
	{
		if (calculatedDependentCategoryAttributes == null)
		{
			calculatedDependentCategoryAttributes = new HashSet<CategoryAttributeInterface>();
		}
		calculatedDependentCategoryAttributes.add(categoryAttributeInterface);
	}

	/**
	 * @hibernate.set name="calculatedCategoryAttributeCollection" table="DYEXTN_CALCULATED_ATTRIBUTE"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="CAL_CATEGORY_ATTR_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.CalculatedAttribute"
	 * @return Returns the calculatedCategoryAttributeCollection.
	 */
	public Collection<CalculatedAttributeInterface> getCalculatedCategoryAttributeCollection()
	{
		return calculatedCategoryAttributeCollection;
	}

	/**
	 *
	 * @param calculatedCategoryAttributeCollection
	 */
	public void setCalculatedCategoryAttributeCollection(
			Collection<CalculatedAttributeInterface> calculatedCategoryAttributeCollection)
	{
		this.calculatedCategoryAttributeCollection = calculatedCategoryAttributeCollection;
	}

	/**
	 *
	 */
	public void addCalculatedCategoryAttribute(
			CalculatedAttributeInterface calculatedAttributeInterface)
	{
		if (calculatedCategoryAttributeCollection == null)
		{
			calculatedCategoryAttributeCollection = new HashSet<CalculatedAttributeInterface>();
		}
		calculatedCategoryAttributeCollection.add(calculatedAttributeInterface);
	}

	/**
	 * This method removes all Calculated Category Attributes.
	 */
	public void removeAllCalculatedCategoryAttributes()
	{
		calculatedCategoryAttributeCollection.clear();
	}

	/**
	 * This method return the default value for the category attribute if set otherwise
	 * return the default value for the original attribute
	 * @return
	 */
	private String getDefaultValue()
	{
		String defaultValue = null;

		if (defaultPermissibleValuesCollection == null
				|| defaultPermissibleValuesCollection.isEmpty())
		{
			defaultValue = getDefaultValueForAbstractAttribute();
		}
		else
		{
			Iterator<PermissibleValueInterface> dataElementIter = defaultPermissibleValuesCollection
					.iterator();
			Object nextPV = dataElementIter.next().getValueAsObject();
			if (nextPV != null)
			{
				defaultValue = String.valueOf(nextPV);
			}
		}

		return defaultValue;
	}

	public String getDefaultValue(Date encounterDate)
	{
		String defaultValue = null;

		if (dataElementCollection != null && !dataElementCollection.isEmpty())
		{
			DataElementInterface dataElement = getDataElement(encounterDate);
			Collection<PermissibleValueInterface> defValues = ((UserDefinedDEInterface) dataElement)
					.getDefaultPermissibleValues();
			if (defValues != null && !defValues.isEmpty())
			{
				Object pValue = defValues.iterator().next().getValueAsObject();
				if (pValue != null)
				{
					defaultValue = String.valueOf(pValue);
				}
			}
		}
		else
		{
			defaultValue = getDefaultValue();
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
		AbstractAttributeInterface attribute = abstractAttribute;
		if (attribute instanceof Attribute)
		{
			defaultValue = ((AttributeMetadataInterface) abstractAttribute).getDefaultValue(null);
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

		defaultPermissibleValuesCollection.add(permissibleValue);
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
		if (formulaCollection != null && !formulaCollection.isEmpty())
		{
			Iterator<FormulaInterface> iterator = formulaCollection.iterator();
			formulaCollection.remove(iterator.next());
		}

		formulaCollection.add(formulaInterface);
	}

	/**
	 * This method return the formula.
	 * @return
	 */
	public FormulaInterface getFormula()
	{
		FormulaInterface formula = null;
		if (formulaCollection != null && !formulaCollection.isEmpty())
		{
			Iterator<FormulaInterface> iterator = formulaCollection.iterator();
			formula = iterator.next();
		}
		return formula;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface#getMaxSize()
	 */
	public int getMaxSize()
	{
		return ((AttributeMetadataInterface) abstractAttribute).getMaxSize();
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface#getMeasurementUnit()
	 */
	public String getMeasurementUnit()
	{
		return ((AttributeMetadataInterface) abstractAttribute).getMeasurementUnit();
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface#getDecimalPlaces()
	 */
	public int getDecimalPlaces()
	{
		return ((AttributeMetadataInterface) abstractAttribute).getDecimalPlaces();
	}

	public AttributeTypeInformationInterface getAttributeTypeInformation()
	{
		return ((AttributeMetadataInterface) abstractAttribute).getAttributeTypeInformation();
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
		isCalculated = isCalculatedAttribute;
	}

	/**
	 * @hibernate.property name="isSourceForCalculatedAttribute" type="boolean" column="IS_SRC_FOR_CAL_ATTR"
	 * @return Returns the isHidden.
	 */
	public Boolean getIsSourceForCalculatedAttribute()
	{
		return isSourceForCalculatedAttribute;
	}

	/**
	 *
	 * @param isSourceForCalculatedAttribute
	 */
	public void setIsSourceForCalculatedAttribute(Boolean isSourceForCalculatedAttribute)
	{
		this.isSourceForCalculatedAttribute = isSourceForCalculatedAttribute;
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
		return ((AttributeMetadataInterface) getAbstractAttribute()).isValuePresent(value);
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
	public void addDependentSkipLogicAttribute(
			SkipLogicAttributeInterface skipLogicAttributeInterface)
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
	public void removeDependentSkipLogicAttribute(
			SkipLogicAttributeInterface skipLogicAttributeInterface)
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
	 * return the underlying attribute.
	 * @return attributeInterface
	 */
	public AttributeInterface getAttribute()
	{
		return (AttributeInterface) getAbstractAttribute();

	}

	/**
	 * This method will retrun all the permissible values of the category Attribtue.
	 * @return Collection of permissible values.
	 */
	public Collection<PermissibleValueInterface> getAllPermissibleValues()
	{
		Collection<PermissibleValueInterface> permissibleValues = new HashSet<PermissibleValueInterface>();
		for (DataElementInterface dataElement : getDataElementCollection())
		{
			permissibleValues.addAll(((UserDefinedDEInterface) dataElement)
					.getPermissibleValueCollection());
		}
		return permissibleValues;
	}

	/**
	 * @hibernate.property name="isPopulateFromXml" type="boolean" column="POPULATE_FROM_XML"
	 * @return Returns the isAutoPopulate.
	 */
	public boolean getIsPopulateFromXml()
	{
		return isPopulateFromXml;
	}

	/**
	 *	sets isPopulateFromXml.
	 * @return returns isPopulateFromXml.
	 */
	public void setIsPopulateFromXml(boolean isPopulateFromXml)
	{
		this.isPopulateFromXml = isPopulateFromXml;
	}
	
	@Override
	public ContainerInterface getContainer()
	{
		return (ContainerInterface) categoryEntity.getContainerCollection().iterator().next();
	}
}