/**
 *
 */

package edu.common.dynamicextensions.permissiblevalue;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.owasp.stinger.Stinger;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.SemanticProperty;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.xml.AttributeType;
import edu.common.dynamicextensions.util.xml.QualifierType;
import edu.common.dynamicextensions.util.xml.SourceType;
import edu.common.dynamicextensions.util.xml.SourceType.QualifierDefinition;
import edu.common.dynamicextensions.util.xml.ValueType;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.logger.Logger;

/**
 * @author Gaurav_mehta
 */
public class PermissibleValueProcessorHelper
{

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger
			.getCommonLogger(PermissibleValueProcessorHelper.class);

	/** The stinger validator. */
	private final Stinger stingerValidator;

	/** The Constant ENTITY_CACHE. */
	private static final EntityCache ENTITY_CACHE = EntityCache.getInstance();

	/** The entity manager. */
	private static final EntityManagerInterface ENTITY_MANAGER = EntityManager.getInstance();

	/** The permissible value vs override value. */
	private final Map<String, String> permissibleValuesToOverride = new HashMap<String, String>();

	/** The attribute id. */
	private AttributeTypeInformationInterface attributeTypeInfo;

	/** The override. */
	private boolean override = false;

	/** The cannot override. */
	private boolean cannotOverride = false;

	/**
	 * Instantiates a new permissible value processor helper object.
	 * @param stinger the stinger.
	 * @throws DynamicExtensionsSystemException.
	 */
	public PermissibleValueProcessorHelper(Stinger stinger) throws DynamicExtensionsSystemException
	{
		if (stinger == null)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue("pv.stinger.null"));
		}
		stingerValidator = stinger;
	}

	/**
	 * Populate permissible values.
	 * @param attribute the attribute.
	 * @param attributeIdentifier the attribute id.
	 * @param attrTypeInfo.
	 * @return the collection< permissible value interface>.
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception.
	 */
	public Collection<PermissibleValueInterface> populatePermissibleValues(AttributeType attribute,
			AttributeTypeInformationInterface attrTypeInfo) throws DynamicExtensionsSystemException
	{
		try
		{
			attributeTypeInfo = attrTypeInfo;
			if (attribute.isOverride() != null)
			{
				override = attribute.isOverride();
			}
			return populatePermissibleValueAndConceptCode(attribute.getXmlPermissibleValue()
					.getValue());
		}
		catch (ParseException e)
		{
			LOGGER.error(ApplicationProperties.getValue("pv.error"));
			throw new DynamicExtensionsSystemException(ApplicationProperties.getValue("pv.error"),
					e);
		}
	}

	/**
	 * Update permissible value in cache and database.
	 * @param entity the entity.
	 * @param attributeId the attribute id.
	 * @param pvList the pv list.
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception.
	 */
	public void updatePermissibleValueInCacheAndDB(EntityInterface entity, Long attributeId,
			Collection<PermissibleValueInterface> pvList) throws DynamicExtensionsSystemException
	{
		UserDefinedDEInterface userDefinedDE = (UserDefinedDEInterface) attributeTypeInfo
				.getDataElement();

		if (userDefinedDE == null)
		{
			userDefinedDE = DomainObjectFactory.getInstance().createUserDefinedDE();
			attributeTypeInfo.setDataElement(userDefinedDE);
		}
		PermissibleValueUtility.checkAndUpdateDefaultPV(attributeTypeInfo, pvList);
		updateUserDefinedDEObject(pvList, userDefinedDE);
		ENTITY_MANAGER.updateAttributeTypeInfo(attributeTypeInfo);
		ENTITY_CACHE.updatePermissibleValues(entity, attributeId, attributeTypeInfo);
	}

	/**
	 * This method updates User Defined DE object depending on condition.
	 * @param pvList.
	 * @param userDefinedDE.
	 * @throws DynamicExtensionsSystemException.
	 */
	private void updateUserDefinedDEObject(Collection<PermissibleValueInterface> pvList,
			UserDefinedDEInterface userDefinedDE) throws DynamicExtensionsSystemException
	{
		try
		{
			if (override)
			{
				if (cannotOverride)
				{
					LOGGER.error(ApplicationProperties.getValue("incorrect.tag.combination"));
					LOGGER.info(ApplicationProperties.getValue("incorrect.tag.info"));
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue("incorrect.tag.combination"));
				}
				userDefinedDE.clearPermissibleValues();
				userDefinedDE.addAllPermissibleValues(pvList);
			}
			else
			{
				editAndUpdatePermissibleValues(userDefinedDE, pvList);
			}
		}
		catch (ParseException e)
		{
			LOGGER.error(ApplicationProperties.getValue("pv.parse.error"));
			LOGGER.info(ApplicationProperties.getValue("pv.parse.info"));
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue("pv.parse.error"), e);
		}
	}

	/**
	 * Update permissible values.
	 * @param oldPermissibleValues the user defined de.
	 * @param pvList the pv list.
	 * @param editCase.
	 * @throws ParseException.
	 * @throws DynamicExtensionsSystemException throws DE exception.
	 */
	private void editAndUpdatePermissibleValues(UserDefinedDEInterface userDefinedDE,
			Collection<PermissibleValueInterface> pvList) throws ParseException,
			DynamicExtensionsSystemException
	{

		Collection<PermissibleValueInterface> cloneNew = new LinkedHashSet<PermissibleValueInterface>(
				pvList);
		for (PermissibleValueInterface oldPermissibleValue : userDefinedDE
				.getPermissibleValueCollection())
		{

			String keyName = oldPermissibleValue.getValueAsObject().toString();
			if (permissibleValuesToOverride.containsKey(keyName))
			{

				keyName = permissibleValuesToOverride.get(keyName);

				for (PermissibleValueInterface newPermissibleValue : pvList)
				{
					String newPvName = newPermissibleValue.getValueAsObject().toString();
					if (newPvName.equals(keyName))
					{
						updateNumericCode(oldPermissibleValue, newPermissibleValue);
						Collection<SemanticPropertyInterface> oldPrimarySemnaticPropertyColl = oldPermissibleValue
								.getSemanticPropertyCollection();

						Collection<SemanticPropertyInterface> newPrimarySemnaticPropertyColl = newPermissibleValue
								.getSemanticPropertyCollection();

						oldPrimarySemnaticPropertyColl = setConceptCode(
								oldPrimarySemnaticPropertyColl, newPrimarySemnaticPropertyColl,
								false);
						if(oldPermissibleValue.getSemanticPropertyCollection() == null)
						{
							oldPermissibleValue
							.setSemanticPropertyCollection(oldPrimarySemnaticPropertyColl);	
						}else
						{
							oldPermissibleValue
							.getSemanticPropertyCollection().clear();
							oldPermissibleValue
							.getSemanticPropertyCollection().addAll(oldPrimarySemnaticPropertyColl);
						}
							
						
						if (attributeTypeInfo.getPermissibleValueForString(newPermissibleValue
								.getValueAsObject().toString()) == null)
						{
							oldPermissibleValue.setObjectValue(newPermissibleValue
									.getValueAsObject().toString());

							cloneNew.remove(newPermissibleValue);
							break;
						}
						else
						{
							LOGGER.error(ApplicationProperties.getValue("pv.same.name.used"));
						}
					}
				}
			}
		}
		userDefinedDE.addAllPermissibleValues(cloneNew);
		checkForSameConceptCode(userDefinedDE.getPermissibleValueCollection());
	}

	/**
	 * Check for same concept code.
	 * @param permissibleValueCollection the permissible value collection.
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception.
	 */
	private void checkForSameConceptCode(
			Collection<PermissibleValueInterface> permissibleValueCollection)
			throws DynamicExtensionsSystemException
	{
		for (PermissibleValueInterface permissibleValue : permissibleValueCollection)
		{
			Collection<SemanticPropertyInterface> semanticPropertyColl = permissibleValue
					.getSemanticPropertyCollection();
			if (semanticPropertyColl != null)
			{
				for (SemanticPropertyInterface primarysemanticProperty : semanticPropertyColl)
				{
					if (primarysemanticProperty.getListOfQualifier() != null
							&& !primarysemanticProperty.getListOfQualifier().isEmpty())
					{
						Collection<String> qualifierConceptCode = new LinkedHashSet<String>();
						for (SemanticPropertyInterface semanticProperty : primarysemanticProperty
								.getListOfQualifier())
						{
							if (!qualifierConceptCode.add(semanticProperty.getConceptCode()))
							{
								throw new DynamicExtensionsSystemException(ApplicationProperties
										.getValue("pv.edit.duplicate.concept.code"));
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Sets the concept code.
	 * @param oldSemanticPropertyColl the old permissible value.
	 * @param newSemanticPropertyColl the new permissible value.
	 * @param qualifierEdit the qualifier edit.
	 * @return the collection< semantic property interface>.
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception.
	 */
	private Collection<SemanticPropertyInterface> setConceptCode(
			Collection<SemanticPropertyInterface> oldSemanticPropertyColl,
			Collection<SemanticPropertyInterface> newSemanticPropertyColl,
			final boolean qualifierEdit) throws DynamicExtensionsSystemException
	{
		if (newSemanticPropertyColl == null)
		{
			return oldSemanticPropertyColl;
		}

		if (oldSemanticPropertyColl == null || oldSemanticPropertyColl.isEmpty())
		{
			return newSemanticPropertyColl;
		}

		for (SemanticPropertyInterface newSemanticProperty : newSemanticPropertyColl)
		{
			boolean addNewSemanticCode = true;
			for (SemanticPropertyInterface oldSemanticProperty : oldSemanticPropertyColl)
			{
				boolean edit = qualifierEdit
						? newSemanticProperty.getSequenceNumber() == oldSemanticProperty
								.getSequenceNumber()
						: newSemanticProperty.getConceptDefinitionSource().equalsIgnoreCase(
								oldSemanticProperty.getConceptDefinitionSource());
				if (edit)
				{
					editQualifierCode(newSemanticProperty, oldSemanticProperty);
					addNewSemanticCode = false;

					if (!qualifierEdit)
					{
						// primary
						Collection<SemanticPropertyInterface> newQualifierColl = newSemanticProperty
								.getListOfQualifier();
						Collection<SemanticPropertyInterface> oldQualifierColl = oldSemanticProperty
								.getListOfQualifier();

						// only one level of recursion
						oldSemanticProperty.setListOfQualifier(setConceptCode(oldQualifierColl,
								newQualifierColl, true));
					}
					break;
				}
			}
			if (addNewSemanticCode)
			{
				oldSemanticPropertyColl.add(newSemanticProperty);
			}
		}
		return oldSemanticPropertyColl;
	}

	/**
	 * Check for edit of qualifier code.
	 * @param newQualifierCode the new qualifier code.
	 * @param oldQualifierCode the old qualifier code.
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception.
	 */
	private void editQualifierCode(SemanticPropertyInterface newQualifierCode,
			SemanticPropertyInterface oldQualifierCode) throws DynamicExtensionsSystemException
	{
		if (!newQualifierCode.getConceptDefinitionSource().equals(
				oldQualifierCode.getConceptDefinitionSource()))
		{
			throw new DynamicExtensionsSystemException("source mismatch");
		}
		oldQualifierCode.setConceptCode(newQualifierCode.getConceptCode());
		oldQualifierCode.setConceptDefinition(newQualifierCode.getConceptDefinition());
		oldQualifierCode.setConceptPreferredName(newQualifierCode.getConceptPreferredName());
	}

	/**
	 * Update numeric code.
	 * @param oldPermissibleValue the old permissible value.
	 * @param newPermissibleValue the new permissible value.
	 */
	private void updateNumericCode(PermissibleValueInterface oldPermissibleValue,
			PermissibleValueInterface newPermissibleValue)
	{
		if (newPermissibleValue.getNumericCode() != null)
		{
			oldPermissibleValue.setNumericCode(newPermissibleValue.getNumericCode());
		}
	}

	/**
	 * Populate permissible value and concept code.
	 * @param pvValueList the pv value list
	 * @return the collection< permissible value interface>
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception.
	 * @throws ParseException.
	 */
	private Collection<PermissibleValueInterface> populatePermissibleValueAndConceptCode(
			Collection<ValueType> pvValueList) throws DynamicExtensionsSystemException,
			ParseException
	{
		PermissibleValuesValidator pvValidator = new PermissibleValuesValidator();
		Collection<PermissibleValueInterface> permissibleValues = new LinkedHashSet<PermissibleValueInterface>();
		for (ValueType value : pvValueList)
		{
			// Check for validating Permissible Value with Stinger Validation
			PermissibleValuesValidator.validateStringForStinger(stingerValidator, value.getName());

			// This method checks this PV needs to be edited or not
			checkForPVOverRide(value);

			// Create an object of Permissible Value which is of Dynamic Extension type.
			String permissibleValueName = DynamicExtensionsUtility.getEscapedStringValue(value
					.getName());
			PermissibleValueInterface permissibleValue = attributeTypeInfo
					.getPermissibleValueForString(permissibleValueName);

			// Validate Numeric Code for duplicate values and if it validates, then add it to Permissible Values.
			pvValidator.validateNumericCode(value.getNumericCode());
			permissibleValue.setNumericCode(value.getNumericCode());

			if (value.getSource() != null)
			{
				if(permissibleValue.getSemanticPropertyCollection() == null)
				{
					permissibleValue.setSemanticPropertyCollection(populateConceptDefinitionObject(
							value, pvValidator));	
				}else
				{
					permissibleValue.getSemanticPropertyCollection().clear();
					permissibleValue.getSemanticPropertyCollection().addAll(populateConceptDefinitionObject(
							value, pvValidator));
				}
				
			}
			permissibleValues.add(permissibleValue);
		}
		return permissibleValues;
	}

	/**
	 * Populate concept definition object.
	 * @param value the value.
	 * @param pvValidator.
	 * @return the collection< semantic property interface>.
	 * @throws DynamicExtensionsSystemException.
	 */
	private Collection<SemanticPropertyInterface> populateConceptDefinitionObject(ValueType value,
			PermissibleValuesValidator pvValidator) throws DynamicExtensionsSystemException
	{
		Collection<SemanticPropertyInterface> conceptDefinition = new LinkedHashSet<SemanticPropertyInterface>();
		Collection<SourceType> sourceList = value.getSource();
		for (SourceType source : sourceList)
		{
			// Validate Source values before adding.
			pvValidator.validateSourceValues(source);

			SemanticPropertyInterface conceptDef = new SemanticProperty(source
					.getPrimaryDefinition());

			Collection<SemanticPropertyInterface> qualifierDefinition = populateQualifierObject(
					source, pvValidator);
			if (qualifierDefinition.isEmpty())
			{
				conceptDef.setListOfQualifier(null);
			}
			else
			{
				conceptDef.setListOfQualifier(qualifierDefinition);
			}
			conceptDefinition.add(conceptDef);
		}
		return conceptDefinition;
	}

	/**
	 * Populate qualifier object.
	 * @param source the source.
	 * @param pvValidator.
	 * @return the collection< semantic property interface>.
	 * @throws DynamicExtensionsSystemException.
	 */
	private Collection<SemanticPropertyInterface> populateQualifierObject(SourceType source,
			PermissibleValuesValidator pvValidator) throws DynamicExtensionsSystemException
	{
		Collection<SemanticPropertyInterface> qualifierDefintion = new LinkedHashSet<SemanticPropertyInterface>();
		if (source.getQualifierDefinition() != null)
		{
			QualifierDefinition qualifierList = source.getQualifierDefinition();
			for (QualifierType qualifier : qualifierList.getQualifier())
			{
				// Validation for unique Qualifier Number and Qualifier Concept Code.
				pvValidator.validateQualifierValues(source, qualifier);

				// Creating Object of Qualifier
				SemanticPropertyInterface qualifierDef = new SemanticProperty(qualifier);
				qualifierDefintion.add(qualifierDef);
			}
		}
		return qualifierDefintion;
	}

	/**
	 * Check whether Permissible Values for this to be overridden.
	 * In this method a Map of oldName Vs newName is created. If oldName tag is not present,
	 * then newName is put as key instead of oldName. This is helpful while updating PV in Database and cache.
	 * To understand the logic better, see its usage in updatePermissibleValues() method.
	 * @param value the value.
	 * @throws DynamicExtensionsSystemException throws DE exception.
	 */
	private void checkForPVOverRide(ValueType value) throws DynamicExtensionsSystemException
	{
		String key = value.getName();
		if (value.getOldName() != null)
		{
			key = value.getOldName();
			cannotOverride = true;
		}
		if (permissibleValuesToOverride.put(key, value.getName()) != null)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue("pv.same.name.used"));
		}
	}
}
