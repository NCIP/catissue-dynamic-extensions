
package edu.common.dynamicextensions.permissiblevalue.version;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.owasp.stinger.Stinger;
import org.xml.sax.SAXException;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.entitymanager.DynamicExtensionBaseQueryBuilder;
import edu.common.dynamicextensions.entitymanager.QueryBuilderFactory;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.XMLUtility;
import edu.common.dynamicextensions.util.xml.ClassType;
import edu.common.dynamicextensions.util.xml.InstanceType;
import edu.common.dynamicextensions.util.xml.PvSetType;
import edu.common.dynamicextensions.util.xml.PvVersion;
import edu.common.dynamicextensions.util.xml.XmlAttributeType;
import edu.common.dynamicextensions.util.xml.PvVersion.XmlCategory;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * @author deepali_ahirrao
 * This class the imports the permissible values for category attributes
 * from XML file into the database.
 *
 */
public class CategoryPermissibleValuesProcessor
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger
			.getCommonLogger(CategoryPermissibleValuesProcessor.class);

	/** The category manager. */
	protected static final CategoryManagerInterface CATEGORY_MANAGER = CategoryManager
			.getInstance();

	/** The Constant ERROR_PV_PARSE. */
	private static final String ERROR_PV_PARSE = "error.pv.parse";

	/** map of category name vs category entity object. */
	private final Map<String, CategoryEntityInterface> categoryEntityMap = new HashMap<String, CategoryEntityInterface>();

	/** The Constant ERROR_LIST. */
	private final List<String> ERROR_LIST = new ArrayList<String>();

	/** The WARNING MESSAGE list. */
	private final List<String> WARNING_MESSAGE_LIST = new ArrayList<String>();

	/** The cat data entry performed. */
	private boolean catDataEntryPerformed = false;

	/** The all encountered date. */
	protected final Collection<Date> allEncounteredDate = new HashSet<Date>();

	/** The pv processor helper. */
	private static CategoryPermissibleValuesProcessorHelper pvProcessorHelper;

	public CategoryPermissibleValuesProcessor(Stinger stinger)
	{
		pvProcessorHelper = new CategoryPermissibleValuesProcessorHelper(stinger);
	}

	/**
	 * This method will start importing the permissible values from XML file.
	 * @param dataEntryPerformed
	 * @throws DynamicExtensionsSystemException exception.
	 */
	public List<String> importPvVersionValues(final String filePath, final String baseDir)
			throws DynamicExtensionsSystemException
	{
		try
		{
			LOGGER.info("ImportCategoryPermissibleValues : importValues called");
			// This packageName is used for parsing XML.
			final String packageName = PvVersion.class.getPackage().getName();

			final PvVersion pvVersion = (PvVersion) XMLUtility.getJavaObjectForXML(filePath,
					baseDir, packageName, "PvVersion.xsd");

			// get list of categories present in XML
			final XmlCategory xmlCategory = pvVersion.getXmlCategory();

			return processCategoryList(xmlCategory);
		}
		catch (SAXException e)
		{
			LOGGER.error(ApplicationProperties.getValue(ERROR_PV_PARSE));
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(ERROR_PV_PARSE), e);
		}
		catch (DynamicExtensionsApplicationException e)
		{
			LOGGER.error(ApplicationProperties.getValue("error.saving.category"));
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue("error.saving.category"), e);
		}
	}

	/**
	 * Process category list.
	 * @param xmlCategory the xml category.
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception.
	 * @throws DynamicExtensionsApplicationException the dynamic extensions application exception.
	 */
	private List<String> processCategoryList(final XmlCategory xmlCategory)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		// Category Object of the category mentioned in XML file.
		final CategoryInterface category = CATEGORY_MANAGER
				.getCategoryByName(xmlCategory.getName());

		// validation to check whether category is present or not.
		CategoryPermissibleValuesValidator.validateForNull(category, "error.missing.category",
				xmlCategory.getName());

		final CategoryEntityInterface rootCategoryEntity = category.getRootCategoryElement();

		/** This boolean tells whether data entry has been done for this category or not. If data has been done then
		 *  many other validation needs to be performed else just carry on with normal versioning use case. */
		catDataEntryPerformed = validateForCategoryDateEntry(category);

		if (catDataEntryPerformed)
		{
			/** This method is to implemented in Host application. This method is supposed to populate a collection
			 *  of all the encountered Date for the given category Entity for which data entry has been done.
			 *  This map is used while performing validation. */
			getEncounteredDateForDataEnteredCategory(category);
		}

		// make a map of category entity name VS cat entity
		CategoryPermissibleValuesProcessorHelper.populateCategoryEntityMap(rootCategoryEntity,
				categoryEntityMap);

		final List<ClassType> classNames = xmlCategory.getClassName();

		// process each class
		processEntityList(classNames);

		// Messages to inform which all Permissible values have been added to Attribute Set
		if (!WARNING_MESSAGE_LIST.isEmpty())
		{
			for (String message : WARNING_MESSAGE_LIST)
			{
				LOGGER.info(message);
			}
		}

		if (!ERROR_LIST.isEmpty())
		{
			StringBuffer errorString = new StringBuffer();
			for (String errorMessage : ERROR_LIST)
			{
				errorString.append(errorMessage);
				errorString.append('\n');
				LOGGER.error(errorMessage);
			}
			throw new DynamicExtensionsSystemException(
					"One or more errors occured while importing PV version as follows.\n"
							+ errorString);
		}
		// persist category
		CATEGORY_MANAGER.persistCategory(category);

		return WARNING_MESSAGE_LIST;
	}

	/**
	 * Process entity list.
	 * @param classNames the class names.
	 */
	private void processEntityList(final List<ClassType> classNames)
	{
		for (ClassType className : classNames)
		{
			List<InstanceType> instances = className.getInstance();
			for (InstanceType instance : instances)
			{
				StringBuffer catEntityName = new StringBuffer(className.getName());
				catEntityName.append('[');
				List<XmlAttributeType> attributes = instance.getXmlAttribute();

				// if instance information is not given in XML
				if ("".equals(instance.getId()) || "ALL".equalsIgnoreCase(instance.getId()))
				{
					processVersioningForMultipleCategoryEntities(pvProcessorHelper, catEntityName,
							attributes);
				}
				else
				{
					// if instance information is given in XML
					catEntityName.append(instance.getId()).append(']');

					List<CategoryEntityInterface> catEntity = pvProcessorHelper
							.getAllMatchingCategoryEntities(catEntityName.toString(),
									categoryEntityMap, false);

					// add PVs to that category entity attribute
					processAttributes(attributes, catEntity.get(0));
				}
			}
		}
	}

	/**
	 * @param catEntityName
	 * @param attributes
	 */
	private void processVersioningForMultipleCategoryEntities(
			CategoryPermissibleValuesProcessorHelper pvProcessorHelper, StringBuffer catEntityName,
			List<XmlAttributeType> attributes)
	{
		// get all possible category entities
		List<CategoryEntityInterface> entities = pvProcessorHelper.getAllMatchingCategoryEntities(
				catEntityName.toString(), categoryEntityMap, true);

		// add PVs to the attribute in all the entities
		for (CategoryEntityInterface catEntity : entities)
		{
			processAttributes(attributes, catEntity);
		}
	}

	/**
	 * Process attributes.
	 * @param attributes the attributes.
	 * @param categoryEntity the category entity.
	 */
	private void processAttributes(final List<XmlAttributeType> attributes,
			final CategoryEntityInterface categoryEntity)
	{
		for (XmlAttributeType xmlAttribute : attributes)
		{
			String attributeName = xmlAttribute.getName();
			CategoryAttributeInterface categoryAttribute = categoryEntity
					.getAttributeByEntityAttributeName(attributeName);

			if (categoryAttribute == null)
			{
				/** if instance information is not given in XML, then, import version for all instances.
				Some instance might not have the attribute, so skip the loop. */
				List<String> placeHolders = new ArrayList<String>();
				placeHolders.add(attributeName);
				placeHolders.add(categoryEntity.getName());
				LOGGER.error(ApplicationProperties
						.getValue("error.missing.attribute", placeHolders));
				ERROR_LIST.add(ApplicationProperties.getValue("error.missing.attribute",
						placeHolders));
				continue;
			}
			try
			{
				Map<Date, Date> encounteredDateVsActDate = getAdditionalDataForValidationPurpose(
						categoryEntity, categoryAttribute);

				List<PvSetType> pvSetTypes = xmlAttribute.getPvSet();
				for (PvSetType pvSet : pvSetTypes)
				{
					List<String> missingPVsInOriSet = processPvSet(pvSet, categoryAttribute,
							encounteredDateVsActDate);
					if (!missingPVsInOriSet.isEmpty())
					{
						WARNING_MESSAGE_LIST
								.add("Following Permissible values are not present for Attribute "
										+ attributeName);
						WARNING_MESSAGE_LIST.add("Hence added them to the Attribute set");
						addMissingPvToErrorList(missingPVsInOriSet);
					}
				}
			}
			catch (Exception e)
			{
				ERROR_LIST.add(e.getMessage());
			}
		}
	}

	/**
	 * Gets the additional data for validation purpose.
	 * @param categoryEntity the category entity.
	 * @param categoryAttribute the category attribute.
	 * @param catEntityDataEntryPerformed the cat entity data entry performed.
	 * @return the additional data for validation purpose.
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception.
	 */
	private Map<Date, Date> getAdditionalDataForValidationPurpose(
			CategoryEntityInterface categoryEntity, CategoryAttributeInterface categoryAttribute)
			throws DynamicExtensionsSystemException
	{
		Map<Date, Date> encounteredDateVsActDate = null;
		if (catDataEntryPerformed)
		{
			/** Gets the table name of the category Entity. This is to check whether Data entry has been performed
			on this category entity or Not.class If data entry has been done then proceed with further validations. */
			String tableName = categoryEntity.getTableProperties().getName();

			DynamicExtensionBaseQueryBuilder queryBuilder = QueryBuilderFactory.getQueryBuilder();
			if (queryBuilder.isDataPresent(tableName))
			{
				encounteredDateVsActDate = pvProcessorHelper.performCategoryEntityValidations(
						categoryAttribute, allEncounteredDate);
			}
		}
		return encounteredDateVsActDate;
	}

	/**
	 * Adds the missing pv to error list.
	 * @param missingPVsInOriSet the missing permissible values in original set
	 */
	private void addMissingPvToErrorList(List<String> missingPVsInOriSet)
	{
		StringBuffer pvList = new StringBuffer("Permissible Values :");
		for (String pvName : missingPVsInOriSet)
		{
			pvList.append(pvName).append(',');
		}
		int length = pvList.length() - 1;
		pvList.setLength(length);
		WARNING_MESSAGE_LIST.add(pvList.toString());
	}

	/**
	 * Process pv set.
	 * @param pvSet the pv set.
	 * @param categoryAttribute the category attribute.
	 * @param attributeTypeInformation the attribute type information.
	 * @param encounteredDateVsActDate
	 * @return the list< string>.
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception.
	 * @throws ParseException the parse exception.
	 */
	private List<String> processPvSet(PvSetType pvSet,
			CategoryAttributeInterface categoryAttribute, Map<Date, Date> encounteredDateVsActDate)
			throws DynamicExtensionsSystemException, ParseException
	{
		AttributeInterface attribute = DynamicExtensionsUtility
				.getBaseAttributeOfcategoryAttribute(categoryAttribute);

		// get PVs from XML
		List<String> xmlPermissibleValuesList = pvSet.getXmlPermissibleValues().getValue();

		//Check whether xml PV set is present at attribute level.
		List<String> missingPvInOriginalSet = pvProcessorHelper
				.chkForxmlPVSetSubsetOfOriginalPVSet(xmlPermissibleValuesList, attribute);

		//If the list is not empty then it means that there are some PV which are not present at Attribute level.
		// According to requirement no #128, if Permissible values are not present at Entity level then add them to Entity.
		if (!missingPvInOriginalSet.isEmpty())
		{
			pvProcessorHelper.addMissingPVInEntitySet(missingPvInOriginalSet, attribute);
		}

		//Get Permissible Value Object reference.
		List<PermissibleValueInterface> permValues = pvProcessorHelper
				.getPermissibleValuesObjectList(xmlPermissibleValuesList, attribute);

		if (catDataEntryPerformed)
		{
			CategoryPermissibleValuesValidator pvValidator = new CategoryPermissibleValuesValidator();
			pvValidator.checkForEditCaseOfVersion(pvSet, categoryAttribute,
					encounteredDateVsActDate, pvProcessorHelper, permValues);
		}
		pvProcessorHelper.populatePermissibleValueVersionInformation(permValues, attribute,
				categoryAttribute, pvSet);
		return missingPvInOriginalSet;
	}

	/**
	 * Validate for date entry.
	 * @param category the category.
	 * @return true, if successful.
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	protected boolean validateForCategoryDateEntry(CategoryInterface category)
			throws DynamicExtensionsSystemException
	{
		return false;
	}

	/**
	 * Gets the encountered date for data entered category.
	 * @param category the category
	 * @return the encountered date for data entered category
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	protected Collection getEncounteredDateForDataEnteredCategory(CategoryInterface category)
			throws DynamicExtensionsSystemException
	{
		return null;
	}

	/**
	 * The main method.
	 * @param args the arguments
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	public static void main(String[] args) throws DynamicExtensionsSystemException
	{
		CategoryPermissibleValuesProcessor pvProcessor = new CategoryPermissibleValuesProcessor(
				null);
		pvProcessor.importPvVersionValues(args[0], args[1]);
	}

}