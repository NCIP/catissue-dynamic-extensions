/**
 *
 */

package edu.common.dynamicextensions.permissiblevalue.version;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.helper.CategoryAttributeHelper;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.DataElementInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.xml.PropertyType;
import edu.common.dynamicextensions.util.xml.PvSetType;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * @author Gaurav_mehta
 */
public class CategoryPermissibleValuesValidator
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	/**
	 * logger for information.
	 */
	protected static final Logger LOGGER = Logger
			.getCommonLogger(CategoryPermissibleValuesValidator.class);

	/**
	 * Check for existing pv version.
	 * @param categoryAttribute the category attribute.
	 * @param properties the properties.
	 * @return the user defined de interface.
	 * @throws ParseException the parse exception.
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception.
	 */
	public UserDefinedDEInterface checkForExistingPvVersion(
			CategoryAttributeInterface categoryAttribute, PropertyType properties)
			throws ParseException, DynamicExtensionsSystemException
	{
		CategoryPermissibleValuesProcessorHelper pvProcessorHelper = new CategoryPermissibleValuesProcessorHelper(null);
		Date activationDate = (Date)pvProcessorHelper.getActivationDate(properties, categoryAttribute);

		CategoryAttributeHelper categoryHelper = new CategoryAttributeHelper();
		UserDefinedDEInterface userDefinedDe = categoryHelper.getDataElementByActivationDate(
				categoryAttribute, activationDate);
		if (userDefinedDe == null)
		{
			Set<DataElementInterface> dataElementCollection = categoryAttribute
					.getDataElementCollection();
			userDefinedDe = DomainObjectFactory.getInstance().createUserDefinedDE();
			userDefinedDe.setActivationDate(activationDate);
			dataElementCollection.add(userDefinedDe);
		}
		return userDefinedDe;
	}

	/**
	 * Validate for appropriate activation date.
	 * @param xmlActivationDate the xml activation date.
	 * @param encounteredDateVsActDate the encountered date vs act date.
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception.
	 */
	public void validateForAppropriateActivationDate(Date xmlActivationDate,
			Map<Date, Date> encounteredDateVsActDate) throws DynamicExtensionsSystemException
	{
		Set<Date> allEncounteredDates = encounteredDateVsActDate.keySet();
		for (Date encounteredDate : allEncounteredDates)
		{
			if (xmlActivationDate.before(encounteredDate))
			{
				throw new DynamicExtensionsSystemException(
						"Permissible value version with activation date later than "
								+ encounteredDate
								+ " already exists and data entry has also been done for the same permissible value version");
			}
		}

	}

	/**
	 * Validate for increamental editing.
	 * @param permValues the perm values.
	 * @param userDefinedDE the user defined de.
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception.
	 */
	public void validateForIncreamentalEditing(List<PermissibleValueInterface> permValues,
			UserDefinedDEInterface userDefinedDE) throws DynamicExtensionsSystemException
	{
		Collection<PermissibleValueInterface> existingPermissibleValues = userDefinedDE
				.getPermissibleValueCollection();
		Collection<PermissibleValueInterface> cloneExitingPv = new HashSet<PermissibleValueInterface>(
				existingPermissibleValues);
		cloneExitingPv.removeAll(permValues);
		if (!cloneExitingPv.isEmpty())
		{
			throw new DynamicExtensionsSystemException(
					"Only incremental editing is allowed for permissible "
							+ "value version with activation date "
							+ userDefinedDE.getActivationDate());
		}
	}

	/**
	 * @param pvSet
	 * @param categoryAttribute
	 * @param encounteredDateVsActDate
	 * @param pvProcessorHelper
	 * @param permValues
	 * @throws ParseException
	 * @throws DynamicExtensionsSystemException
	 */
	public void checkForEditCaseOfVersion(PvSetType pvSet,
			CategoryAttributeInterface categoryAttribute, Map<Date, Date> encounteredDateVsActDate,
			CategoryPermissibleValuesProcessorHelper pvProcessorHelper,
			List<PermissibleValueInterface> permValues) throws ParseException,
			DynamicExtensionsSystemException
	{
		Date xmlActivationDate = (Date)pvProcessorHelper.getActivationDate(pvSet.getProperties(),
				categoryAttribute);

		boolean addCase = pvProcessorHelper.checkForAddCaseOfVersion(xmlActivationDate,
				categoryAttribute);
		if (addCase)
		{
			/** Validation 1. Check whether the activation date is not before any of the encountered date for
				which data entry has been done. If that is the case do not allow to create that version. */
			validateForAppropriateActivationDate(xmlActivationDate, encounteredDateVsActDate);
		}
		else if (encounteredDateVsActDate != null)
		{
			Date encounteredDate = getEncounteredDateForActivationDate(encounteredDateVsActDate,
					xmlActivationDate);
			if (encounteredDate != null)
			{
				/** check whether a set of permissible values with given Activation Date already exists or not.
				 *  If exists then return the same UserDefinedDe object or else create new UserDefinedDe object.
				 */
				UserDefinedDEInterface userDefinedDE = checkForExistingPvVersion(categoryAttribute,
						pvSet.getProperties());

				validateForIncreamentalEditing(permValues, userDefinedDE);
			}
		}
	}

	/**
	 * Gets the encountered date for activation date.
	 * @param encounteredDateVsActDate the encountered date vs act date
	 * @param xmlActivationDate the xml activation date
	 * @return the encountered date for activation date
	 */
	private Date getEncounteredDateForActivationDate(Map<Date, Date> encounteredDateVsActDate,
			Date xmlActivationDate)
	{
		Set<Date> allEncounteredDate = encounteredDateVsActDate.keySet();
		Date date = null;
		for (Date encounteredDate : allEncounteredDate)
		{
			if (encounteredDateVsActDate.get(encounteredDate)!=null && encounteredDateVsActDate.get(encounteredDate).compareTo(xmlActivationDate) == 0)
			{
				date = encounteredDate;
				break;
			}
		}
		return date;
	}

	/**
	 * Validates whether given object is null or not.
	 * @param object the object.
	 * @param errorMessageKey the error message key.
	 * @param objectName.
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception.
	 */
	public static void validateForNull(Object object, String errorMessageKey, String objectName)
			throws DynamicExtensionsSystemException
	{
		if (object == null)
		{
			LOGGER.error(ApplicationProperties.getValue(errorMessageKey, objectName));
			throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(
					errorMessageKey, objectName));
		}
	}

}
