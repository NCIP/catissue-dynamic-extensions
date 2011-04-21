/**
 *
 */

package edu.common.dynamicextensions.permissiblevalue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.owasp.stinger.Stinger;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.xml.PrimaryDefinitionType;
import edu.common.dynamicextensions.util.xml.QualifierType;
import edu.common.dynamicextensions.util.xml.SourceType;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.logger.Logger;

/**
 * The Class PermissibleValuesValidation.
 * @author Gaurav_mehta
 */
public class PermissibleValuesValidator
{

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(PermissibleValuesValidator.class);

	/** The numeric values. */
	private final Set<Long> numericValues = new HashSet<Long>();

	/** The Source vs list of concept code. */
	private final Map<String, Set<String>> sourceVsListOfConceptCode = new HashMap<String, Set<String>>();

	/** The concept code vs (qualifier number vs qualifier concept code). */
	private final Map<String, Map<Long, String>> ccVsQualifierNoVsQualifierCC
								= new HashMap<String, Map<Long, String>>();
	/**
	 * Validate numeric code.
	 * @param numericCode the numeric code
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	public void validateNumericCode(Long numericCode) throws DynamicExtensionsSystemException
	{
		if (numericCode != null && !numericValues.add(numericCode))
		{
				LOGGER.error(ApplicationProperties.getValue("pv.duplicate.numeric.code",
						numericCode.toString()));
				throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(
						"pv.duplicate.numeric.code", numericCode.toString()));
		}
	}

	/**
	 * Validate source values.
	 * @param source the source
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	public void validateSourceValues(SourceType source) throws DynamicExtensionsSystemException
	{
		PrimaryDefinitionType primaryDefinition = source.getPrimaryDefinition();
		String sourceName = primaryDefinition.getConceptDefinitionSource();
		if (sourceVsListOfConceptCode.get(sourceName) == null)
		{
			Set<String> conceptCode = new HashSet<String>();
			conceptCode.add(primaryDefinition.getConceptCode());
			sourceVsListOfConceptCode.put(sourceName, conceptCode);
		}
		else
		{
			Set<String> existingConceptCode = sourceVsListOfConceptCode.get(sourceName);
			if (!existingConceptCode.add(primaryDefinition.getConceptCode()))
			{
				LOGGER.error(ApplicationProperties.getValue("pv.duplicate.concept.code",
														primaryDefinition.getConceptCode()));
				throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(
						"pv.duplicate.concept.code", primaryDefinition.getConceptCode()));
			}
		}
	}

	/**
	 * Validate qualifier values.
	 * @param source the source
	 * @param qualifier the qualifier
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	public void validateQualifierValues(SourceType source, QualifierType qualifier)
			throws DynamicExtensionsSystemException
	{
		PrimaryDefinitionType primaryDefinition = source.getPrimaryDefinition();
		if (ccVsQualifierNoVsQualifierCC.get(primaryDefinition.getConceptCode()) == null)
		{
			Map<Long, String> qualifierNoVsQualifierCC = new HashMap<Long, String>();
			qualifierNoVsQualifierCC
					.put(qualifier.getSequenceNumber(), qualifier.getConceptCode());
			ccVsQualifierNoVsQualifierCC.put(primaryDefinition.getConceptCode(),
					qualifierNoVsQualifierCC);
		}
		else
		{
			Map<Long, String> QualifierNoVsQualifierCC = ccVsQualifierNoVsQualifierCC.get(primaryDefinition
					.getConceptCode());
			if (QualifierNoVsQualifierCC.put(qualifier.getSequenceNumber(), qualifier.getConceptCode()) != null)
			{
				LOGGER.error(ApplicationProperties.getValue("pv.duplicate.qualifier.concept.code"));
				throw new DynamicExtensionsSystemException(ApplicationProperties
						.getValue("pv.duplicate.qualifier.concept.code"));
			}
		}
	}

	/**
	 * This method validates given string value with Stinger validation.
	 * @param value PV value.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	public static void validateStringForStinger(Stinger stingerValidator, String value)
			throws DynamicExtensionsSystemException
	{
		if (stingerValidator != null && !stingerValidator.validate(value))
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(
					"dynExtn.validation.unsafe.character", value));
		}
	}

	/**
	 * Validate for null.
	 * @param object the object.
	 * @param errorMessageKey the error message.
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception.
	 */
	public static void validateForNull(Object object, String errorMessageKey,
			String missingStingValue) throws DynamicExtensionsSystemException
	{
		if (object == null)
		{
			String errorMessage = ApplicationProperties.getValue(errorMessageKey, missingStingValue);
			LOGGER.error(errorMessage);
			LOGGER.info(ApplicationProperties.getValue("pv.info"));
			throw new DynamicExtensionsSystemException(errorMessage);
		}
	}
}
