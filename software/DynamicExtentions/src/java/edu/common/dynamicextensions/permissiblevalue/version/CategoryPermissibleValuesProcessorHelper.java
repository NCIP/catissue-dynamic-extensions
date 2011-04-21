/**
 *
 */

package edu.common.dynamicextensions.permissiblevalue.version;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.owasp.stinger.Stinger;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.helper.CategoryAttributeHelper;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.permissiblevalue.PermissibleValuesValidator;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.xml.PropertyType;
import edu.common.dynamicextensions.util.xml.PvSetType;
import edu.common.dynamicextensions.util.xml.PropertyType.Option;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * @author Gaurav_mehta
 *
 */
public class CategoryPermissibleValuesProcessorHelper
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	/**
	 * logger for information.
	 */
	private static final Logger LOGGER = Logger
			.getCommonLogger(CategoryPermissibleValuesProcessorHelper.class);

	/** The Constant ENTITY_CACHE. */
	private static final EntityCache ENTITY_CACHE = EntityCache.getInstance();

	/** The entity manager. */
	private static final EntityManagerInterface ENTITY_MANAGER = EntityManager.getInstance();

	/** The stinger validator. */
	private Stinger stingerValidator;

	public CategoryPermissibleValuesProcessorHelper(Stinger stinger)
	{
		stingerValidator = stinger;
	}

	/**
	 * Create Map of name vs entity.
	 * @param categoryEntityInterface category entity.
	 * @param categoryEntityMap category Entity Map.
	 */
	public static void populateCategoryEntityMap(CategoryEntityInterface categoryEntityInterface,
			Map<String, CategoryEntityInterface> categoryEntityMap)
	{
		Collection<CategoryAssociationInterface> assoColl = categoryEntityInterface
				.getCategoryAssociationCollection();

		for (CategoryAssociationInterface catAsso : assoColl)
		{
			// get target entity of each association
			CategoryEntityInterface targetEntity = catAsso.getTargetCategoryEntity();
			categoryEntityMap.put(targetEntity.getName(), targetEntity);
			populateCategoryEntityMap(targetEntity, categoryEntityMap);
		}
	}

	/**
	 * Check whether xml pv set is a subset of original pv set or not and returns the set of those PVs which are
	 * not present in Attribute level set of PVs.
	 * @param xmlPermissibleValuesList the xml permissible values list.
	 * @param attributeTypeInformation the attribute type information.
	 * @return true, if successful.
	 */
	public List<String> chkForxmlPVSetSubsetOfOriginalPVSet(List<String> xmlPermissibleValuesList,
			AttributeInterface attribute)
	{
		AttributeTypeInformationInterface attributeTypeInformation = attribute
				.getAttributeTypeInformation();
		UserDefinedDEInterface userDefinedDE = (UserDefinedDEInterface) attributeTypeInformation
				.getDataElement();
		List<String> cloneXmlPvList = new ArrayList<String>(xmlPermissibleValuesList);
		if (userDefinedDE == null)
		{
			userDefinedDE = DomainObjectFactory.getInstance().createUserDefinedDE();
			userDefinedDE.addAllPermissibleValues(new HashSet<PermissibleValueInterface>());
			attributeTypeInformation.setDataElement(userDefinedDE);
		}
		else
		{
			Collection<PermissibleValueInterface> originalPVSet = userDefinedDE
					.getPermissibleValueCollection();
			Collection<String> originalPV = new HashSet<String>();

			for (PermissibleValueInterface permissibleValue : originalPVSet)
			{
				originalPV.add(permissibleValue.getValueAsObject().toString());
			}
			cloneXmlPvList.removeAll(originalPV);
		}
		return cloneXmlPvList;
	}

	/**
	 * Gets the permissible values object list.
	 * @param xmlPermissibleValuesList the xml permissible values list.
	 * @param attributeTypeInformation the attribute type information.
	 * @return the permissible values object list.
	 */
	public List<PermissibleValueInterface> getPermissibleValuesObjectList(
			List<String> xmlPermissibleValuesList, AttributeInterface attribute)
	{
		AttributeTypeInformationInterface attributeTypeInformation = attribute
				.getAttributeTypeInformation();
		UserDefinedDEInterface userDefinedDE = (UserDefinedDEInterface) attributeTypeInformation
				.getDataElement();
		Collection<PermissibleValueInterface> originalPVSet = userDefinedDE
				.getPermissibleValueCollection();
		List<PermissibleValueInterface> permissibleValues = new ArrayList<PermissibleValueInterface>();

		for (PermissibleValueInterface permissibleValue : originalPVSet)
		{
			String pvValue = permissibleValue.getValueAsObject().toString();
			if (xmlPermissibleValuesList.contains(DynamicExtensionsUtility
					.getUnEscapedStringValue(pvValue)))
			{
				permissibleValues.add(permissibleValue);
			}
		}
		return permissibleValues;
	}

	/**
	 * Gets the activation date.
	 * @param properties the properties
	 * @param categoryAttribute category attribute.
	 * @return the activation date
	 * @throws ParseException the parse exception
	 * @throws DynamicExtensionsSystemException exception if date not found.
	 */
	public Object getActivationDate(PropertyType properties,
			CategoryAttributeInterface categoryAttribute) throws ParseException,
			DynamicExtensionsSystemException
	{
		List<Option> permValOptions = properties.getOption();
		for (Option option : permValOptions)
		{
			String key = option.getKey();
			if ("activationDate".equalsIgnoreCase(key))
			{
				Object activationDate = null;
				if ("ALL".equalsIgnoreCase(option.getValue()))
				{
					activationDate = option.getValue();
				}
				else
				{
					activationDate = formatDate(option.getValue());
				}
				return activationDate;
			}
		}
		LOGGER.error(ApplicationProperties.getValue("error.missing.activationdate",
				categoryAttribute.getName()));
		List<String> placeHolders = new ArrayList<String>();
		placeHolders.add(categoryAttribute.getName());
		placeHolders.add(categoryAttribute.getCategoryEntity().getName());
		throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(
				"error.missing.activationdate", placeHolders));

	}

	/**
	 * Perform category entity validations.
	 * @param categoryAttribute the category attribute
	 * @param allEncounteredDate the all encountered date
	 * @return the map< date, date>
	 */
	public Map<Date, Date> performCategoryEntityValidations(
			CategoryAttributeInterface categoryAttribute, Collection<Date> allEncounteredDate)
	{
		CategoryAttributeHelper categoryHelper = new CategoryAttributeHelper();
		Map<Date, Date> encounteredDateVsActDate = new HashMap<Date, Date>();
		List<Date> activationDate = categoryHelper
				.getAllActivationDateForCategoryAttribute(categoryAttribute);

		// Sorts the collection of activation date in ascending order.
		Collections.sort(activationDate, new SortDateCollection());

		// Get the activation for each encountered Date and save it in encounteredDateVsActDate Map.
		for (Date encounteredDate : allEncounteredDate)
		{
			Date actualActDate = null;
			for (Date date : activationDate)
			{
				if (encounteredDate.after(date))
				{
					actualActDate = date;
				}
			}
			encounteredDateVsActDate.put(encounteredDate, actualActDate);
		}
		return encounteredDateVsActDate;
	}

	public boolean checkForAddCaseOfVersion(Date xmlActivationDate,
			CategoryAttributeInterface categoryAttribute)
	{
		CategoryAttributeHelper categoryHelper = new CategoryAttributeHelper();
		List<Date> activationDate = categoryHelper
				.getAllActivationDateForCategoryAttribute(categoryAttribute);
		return !activationDate.contains(xmlActivationDate);
	}

	/**
	 * This method will return List of category Entity where xmlCatEntityName matches.
	 * @param xmlCatEntityName instance information.
	 * @param categoryEntityMap
	 * @param multipleMatches.
	 * @return list of category entities.
	 */
	public List<CategoryEntityInterface> getAllMatchingCategoryEntities(String xmlCatEntityName,
			Map<String, CategoryEntityInterface> categoryEntityMap, boolean multipleMatches)
	{
		List<CategoryEntityInterface> categoryEntities = new ArrayList<CategoryEntityInterface>();

		for (String categoryEntityName : categoryEntityMap.keySet())
		{
			if (multipleMatches)
			{
				String catNameWithoutInstance = categoryEntityName.substring(0, categoryEntityName
						.length() - 2);
				if (catNameWithoutInstance.endsWith(xmlCatEntityName))
				{
					CategoryEntityInterface categoryEntity = categoryEntityMap
							.get(categoryEntityName);
					categoryEntities.add(categoryEntity);
				}
			}
			else if (categoryEntityName.endsWith(xmlCatEntityName))
			{
				CategoryEntityInterface categoryEntity = categoryEntityMap.get(categoryEntityName);
				categoryEntities.add(categoryEntity);
				break;
			}
		}
		return categoryEntities;
	}

	/**
	 * Populate permissible value version information.
	 * @param permValues the perm values.
	 * @param attribute the attribute.
	 * @param categoryAttribute the category attribute.
	 * @param pvSet the pv set.
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception.
	 * @throws ParseException the parse exception.
	 */
	public void populatePermissibleValueVersionInformation(
			List<PermissibleValueInterface> permValues, AttributeInterface attribute,
			CategoryAttributeInterface categoryAttribute, PvSetType pvSet)
			throws DynamicExtensionsSystemException, ParseException
	{
		CategoryPermissibleValuesValidator pvValidator = new CategoryPermissibleValuesValidator();

		/** check whether a set of permissible values with given Activation Date already exists or not.
		 *  If exists then return the same object UserDefinedDe object or else create new UserDefinedDe object.
		 */
		UserDefinedDEInterface userDefinedDE = pvValidator.checkForExistingPvVersion(
				categoryAttribute, pvSet.getProperties());

		// Set PV options: isOrdered, activationDate etc
		setPermissibleValueOptions(pvSet.getProperties(), userDefinedDE);

		// set the PVs in UserDefinedDE
		userDefinedDE.clearPermissibleValues();
		userDefinedDE.addAllPermissibleValues(permValues);

		// Set default value
		handleDefaultValue(pvSet.getDefaultValue(), userDefinedDE, attribute);
	}

	/**
	 * Adds the missing pv to entity PV set.
	 * @param missingPvInOriginalSet the missing pv in original set
	 * @param attribute the attribute
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	public void addMissingPVInEntitySet(List<String> missingPvInOriginalSet,
			AttributeInterface attribute) throws DynamicExtensionsSystemException
	{
		AttributeTypeInformationInterface attributeTypeInfo = attribute
				.getAttributeTypeInformation();

		UserDefinedDEInterface userDefinedDE = (UserDefinedDEInterface) attributeTypeInfo
				.getDataElement();

		for (String pvValue : missingPvInOriginalSet)
		{
			//Validation of Stinger.
			PermissibleValuesValidator.validateStringForStinger(stingerValidator, pvValue);

			// Create an object of Permissible Value which is of Dynamic Extension type.
			String permissibleValueName = DynamicExtensionsUtility.getEscapedStringValue(pvValue);
			try
			{
				PermissibleValueInterface permissibleValue = attributeTypeInfo
						.getPermissibleValueForString(permissibleValueName);
				userDefinedDE.getPermissibleValueCollection().add(permissibleValue);
			}
			catch (ParseException e)
			{
				LOGGER.error(ApplicationProperties.getValue("pv.error"));
				throw new DynamicExtensionsSystemException(ApplicationProperties
						.getValue("pv.error"), e);
			}
		}

		ENTITY_MANAGER.updateAttributeTypeInfo(attributeTypeInfo);
		ENTITY_CACHE.updatePermissibleValues(attribute.getEntity(), attribute.getId(),
				attributeTypeInfo);
	}

	/**
	 * Sets the permissible value options.
	 * @param properties the properties.
	 * @param userDefinedDE the user defined de.
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception.
	 * @throws ParseException the parse exception.
	 */
	private void setPermissibleValueOptions(final PropertyType properties,
			UserDefinedDEInterface userDefinedDE) throws DynamicExtensionsSystemException,
			ParseException
	{
		List<Option> permValOptions = properties.getOption();
		for (Option option : permValOptions)
		{
			String key = option.getKey();
			if ("isOrdered".equalsIgnoreCase(key))
			{
				userDefinedDE.setIsOrdered(Boolean.valueOf(option.getValue()));
			}
			else if ("sort".equalsIgnoreCase(key))
			{
				userDefinedDE.setIsOrdered(Boolean.TRUE);
				userDefinedDE.setOrder(option.getValue());
			}
			else if ("newActivationDate".equalsIgnoreCase(key))
			{
				//set new activation date
				Date newActivationDate = formatDate(option.getValue());
				userDefinedDE.setActivationDate(newActivationDate);
				LOGGER.info("Modified activation date");
			}
			else if (!"activationDate".equalsIgnoreCase(key))
			{
				LOGGER.error(ApplicationProperties.getValue("error.pv.invalid.option", key));
				throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(
						"error.pv.invalid.option", key));
			}
		}
	}

	/**
	 * Format date.
	 *
	 * @param optionValue the option value
	 *
	 * @return the date
	 *
	 * @throws ParseException the parse exception
	 */
	private Date formatDate(String optionValue) throws ParseException
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", CommonServiceLocator
				.getInstance().getDefaultLocale());
		String dateValue = optionValue.replace('-', '/');
		return dateFormat.parse(dateValue);
	}

	/**
	 * Handle default value.
	 * @param defaultValue the default value.
	 * @param userDefinedDE the user defined de.
	 * @param attribute the attribute type information.
	 */
	private void handleDefaultValue(String defaultValue, UserDefinedDEInterface userDefinedDE,
			AttributeInterface attribute)
	{
		if (defaultValue != null && !"".equals(defaultValue.trim()))
		{
			List<String> defaultPVList = new ArrayList<String>();
			defaultPVList.add(defaultValue);

			List<PermissibleValueInterface> permValues = getPermissibleValuesObjectList(
					defaultPVList, attribute);

			Collection<PermissibleValueInterface> defaultPvColl = new HashSet<PermissibleValueInterface>();
			defaultPvColl.addAll(permValues);
			userDefinedDE.setDefaultPermissibleValues(defaultPvColl);
		}
	}
}
