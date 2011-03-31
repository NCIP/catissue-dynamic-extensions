/**
 *
 */

package edu.common.dynamicextensions.category.creation;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.owasp.stinger.Stinger;

import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.operations.CategoryOperations;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.skiplogic.SkipLogic;
import edu.common.dynamicextensions.util.BOTemplateGenerator;
import edu.common.dynamicextensions.util.CategoryHelper;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.parser.CategoryGenerator;
import edu.wustl.bulkoperator.templateImport.ImportBulkOperationUsingDAO;
import edu.wustl.bulkoperator.util.BulkOperationConstants;
import edu.wustl.bulkoperator.util.BulkOperationException;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * @author Gaurav_mehta
 *
 */
public class CategoryProcessor
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(CategoryProcessor.class);

	/** The stinger. */
	private final Stinger stinger;
	/**
	 * template file path.
	 */
	private transient String templateFile;

	/**
	 * Mapping XML file path.
	 */
	private transient String mappingXML;
	/**
	 * Specifies whether to create and import bulk templates for this category.
	 */
	private transient String importBulkTemplate;

	/**
	 * Instantiates a new category processor.
	 * @param stinger the stinger
	 */
	public CategoryProcessor(Stinger stinger)
	{
		this.stinger = stinger;
	}

	/**
	 * This method will create the category using the file specified in filePath which is
	 * present in the baseDirectory argument.
	 * @param filePath path of the file from which to create category.
	 * @param baseDirectory directory from which all the paths are mentioned.
	 * @param isPersistMetadataOnly if true saves only the metadata , does not create the dynamic tables for category
	 * @param catNameVsExcep this  is a report map in which the entry is made for each category
	 * the value will be null if category creation is successful else exception occured will be its value.
	 * @param mappingXML
	 * @param templateFile
	 */
	public void createCategory(String filePath, String baseDirectory,
			boolean isPersistMetadataOnly, Map<String, Exception> catNameVsExcep)
	{
		CategoryInterface category = null;
		try
		{
			CategoryGenerator categoryGenerator = new CategoryGenerator(filePath, baseDirectory,
					this.stinger);
			category = categoryGenerator.generateCategory();

			CategoryOperations operations = new CategoryOperations();
			operations.saveCategory(isPersistMetadataOnly, category);

			Collection<ContainerInterface> allContainers = categoryGenerator
					.getAllContainersForCategory(category);
			for (ContainerInterface containerInterface : allContainers)
			{
				// Delete existing Skip Logic from database and cache
				operations.deleteSkipLogic(containerInterface.getId());
				EntityCache.getInstance().deleteSkipLogicFromCache(containerInterface.getId());
			}
			Map<ContainerInterface, SkipLogic> conditionStatements = categoryGenerator
					.getContainerVsSkipLogicMap();
			Set<Entry<ContainerInterface, SkipLogic>> entrySet = conditionStatements.entrySet();
			for (Entry<ContainerInterface, SkipLogic> entry : entrySet)
			{
				// Insert new Skip Logic into database and cache
				operations.persistSkipLogic(entry.getValue());
				EntityCache.getInstance().updateSkipLogicInCache(entry.getValue());
			}
			if (ProcessorConstants.TRUE.equalsIgnoreCase(this.importBulkTemplate))
			{
				createAndImportBOTemplate(baseDirectory, category); //create and import bulk template.
			}
			LOGGER.info("Saved category " + category.getName() + " successfully");
			LOGGER.info("Form definition file " + filePath + " executed successfully.");
			catNameVsExcep.put(filePath, null);
		}
		catch (Exception ex)
		{
			LOGGER.error("Error occured while creating category", ex);
			catNameVsExcep.put(filePath, ex);
		}
		finally
		{
			new CategoryHelper().releaseLockOnCategory(category);
		}
	}

	/**
	 * This method creates and imports the BO template for category.
	 * @param baseDirectory Base directory where BO file exists.
	 * @param category category object.
	 * @throws DynamicExtensionsSystemException throw DESystemException.
	 * @throws BulkOperationException throw BOException.
	 */
	private void createAndImportBOTemplate(String baseDirectory, CategoryInterface category)
			throws DynamicExtensionsSystemException, BulkOperationException
	{
		File file = new File(baseDirectory);
		String tempDirPath = file.getParentFile().getPath();
		//create category XML and CSV template.
		new BOTemplateGenerator(category).generateXMLAndCSVTemplate(tempDirPath, this.templateFile,
				this.mappingXML);
		LOGGER.info("Bulk Template for Category " + category.getName() + " created successfully.");
		String csvFile = tempDirPath + File.separator + DEConstants.TEMPLATE_DIR + File.separator
				+ category.getName() + DEConstants.CSV_SUFFIX;
		String xmlFile = tempDirPath + File.separator + DEConstants.TEMPLATE_DIR + File.separator
				+ category.getName() + DEConstants.XML_SUFFIX;
		System.setProperty(BulkOperationConstants.CONFIG_DIR, "clinportal-properties");

		//import category XML and CSV template created above.
		try
		{
			new ImportBulkOperationUsingDAO(category.getName(), category.getName(), csvFile,
					xmlFile, DynamicExtensionsUtility.getJDBCDAO(null));
		}
		catch (Exception exception)
		{
			LOGGER.error("Error occured while creating BO tempalte", exception);
			throw new DynamicExtensionsSystemException(
					"Error occured while creating BO tempalte: ", exception);
		}
		LOGGER.info("Bulk Template for Category " + category.getName() + " imported successfully.");

	}

	/**
	 * @return the templateFile.
	 */
	public String getTemplateFile()
	{
		return this.templateFile;
	}

	/**
	 * @param templateFile the templateFile to set.
	 */
	public void setTemplateFile(String templateFile)
	{
		this.templateFile = templateFile;
	}

	/**
	 * @return the mappingXML.
	 */
	public String getMappingXML()
	{
		return this.mappingXML;
	}

	/**
	 * @param mappingXML the mappingXML to set.
	 */
	public void setMappingXML(String mappingXML)
	{
		this.mappingXML = mappingXML;
	}

	/**
	 * @return the importBulkTemplate.
	 */
	public String getImportBulkTemplate()
	{
		return this.importBulkTemplate;
	}

	/**
	 * @param importBulkTemplate the importBulkTemplate to set.
	 */
	public void setImportBulkTemplate(String importBulkTemplate)
	{
		this.importBulkTemplate = importBulkTemplate;
	}
}
