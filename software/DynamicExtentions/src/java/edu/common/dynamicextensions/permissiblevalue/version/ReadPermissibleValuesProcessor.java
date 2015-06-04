
package edu.common.dynamicextensions.permissiblevalue.version;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import edu.common.dynamicextensions.domain.PathAssociationRelationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.DataElementInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.FileReader;
import edu.common.dynamicextensions.util.xml.ClassType;
import edu.common.dynamicextensions.util.xml.InstanceType;
import edu.common.dynamicextensions.util.xml.PropertyType;
import edu.common.dynamicextensions.util.xml.PropertyType.Option;
import edu.common.dynamicextensions.util.xml.PvSetType;
import edu.common.dynamicextensions.util.xml.PvSetType.XmlPermissibleValues;
import edu.common.dynamicextensions.util.xml.PvVersion;
import edu.common.dynamicextensions.util.xml.PvVersion.XmlCategory;
import edu.common.dynamicextensions.util.xml.XMLToObjectConverter;
import edu.common.dynamicextensions.util.xml.XmlAttributeType;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * @author deepali_ahirrao
 * This class the imports the permissible values for category attributes
 * from XML file into the database.
 *
 */
public class ReadPermissibleValuesProcessor
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger
			.getCommonLogger(ReadPermissibleValuesProcessor.class);

	/** The category manager. */
	protected static final CategoryManagerInterface CATEGORY_MANAGER = CategoryManager
			.getInstance();

	/** The Constant ERROR_PV_PARSE. */
	private static final String ERROR_PV_PARSE = "error.pv.parse";

	/** map of category name vs category entity object. */
	private static final Map<String, CategoryEntityInterface> CATEGORY_MAP = new HashMap<String, CategoryEntityInterface>();

	/** The Constant ERROR_LIST. */
	private static final List<String> ERROR_LIST = new ArrayList<String>();

	/** The WARNING MESSAGE list. */
	private static final List<String> WARNING_LIST = new ArrayList<String>();

	/** The cat data entry performed. */
	private XMLToObjectConverter converter = null;


	/**
	 * @param filePath
	 * @param baseDir
	 * @param downloadToDir
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws JAXBException
	 * @throws IOException
	 */
	public List<String> getPvVersionValues(final String filePath, final String baseDir,final String downloadToDir)
			throws DynamicExtensionsSystemException, JAXBException, IOException
	{
		LOGGER.info("ImportCategoryPermissibleValues : importValues called");
		final PvVersion outputPVVersion = new PvVersion();
		final PvVersion inputPVVersion = initialize(filePath, baseDir);

		// get list of categories present in XML
		final XmlCategory xmlCategory = inputPVVersion.getXmlCategory();

		try
		{
			processCategoryList(xmlCategory, outputPVVersion);
			converter.objectXMLConverter(outputPVVersion, outputPVVersion.getXmlCategory()
					.getName(), downloadToDir);
		}
		catch (DynamicExtensionsApplicationException e)
		{
			LOGGER.error(ApplicationProperties.getValue("error.saving.category"));
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue("error.saving.category"), e);
		}

		return WARNING_LIST;
	}


	/**
	 * @param filePath
	 * @param baseDir
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private PvVersion initialize(final String filePath,final String baseDir)
			throws DynamicExtensionsSystemException
	{
		try
		{
			// Creates URL of the XSD specified.
			final URL xsdFileUrl = Thread.currentThread().getContextClassLoader().getResource(
					"PvVersion.xsd");

			converter = new XMLToObjectConverter(PvVersion.class.getPackage().getName(), xsdFileUrl);

			// Populate the POJO from XML
			final FileReader fileReader = new FileReader(filePath, baseDir);

			// PvVersion represents the XML in object form
			return (PvVersion) converter
					.getJavaObject(new FileInputStream(fileReader.getFilePath()));
		}
		catch (JAXBException e)
		{
			LOGGER.error(ApplicationProperties.getValue(ERROR_PV_PARSE));
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(ERROR_PV_PARSE), e);
		}
		catch (SAXException e)
		{
			LOGGER.error(ApplicationProperties.getValue(ERROR_PV_PARSE));
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(ERROR_PV_PARSE), e);
		}
		catch (FileNotFoundException e)
		{
			LOGGER.error(ApplicationProperties.getValue("error.pv.filenotfound"));
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue("error.pv.filenotfound"), e);
		}
	}


	/**
	 * @param xmlCategory
	 * @param outputPVVersion
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private List<String> processCategoryList(final XmlCategory xmlCategory,
			final PvVersion outputPVVersion) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		// Category Object of the category mentioned in XML file.
		final CategoryInterface category = CATEGORY_MANAGER
				.getCategoryByName(xmlCategory.getName());
		final XmlCategory outputXmlCategory = new XmlCategory();
		outputXmlCategory.setName(category.getName());
		outputPVVersion.setXmlCategory(outputXmlCategory);

		// validation to check whether category is present or not.
		CategoryPermissibleValuesValidator.validateForNull(category, "error.missing.category",
				xmlCategory.getName());

		final CategoryEntityInterface rootEntity = category.getRootCategoryElement();

		// make a map of category entity name VS cat entity
		CategoryPermissibleValuesProcessorHelper.populateCategoryEntityMap(rootEntity,
				CATEGORY_MAP);

		final List<ClassType> classNames = xmlCategory.getClassName();

		// process each class
		processEntityList(classNames, outputXmlCategory);

		// Messages to inform which all Permissible values have been added to Attribute Set
		if (!WARNING_LIST.isEmpty())
		{
			for (String message : WARNING_LIST)
			{
				LOGGER.info(message);
			}
		}

		if (!ERROR_LIST.isEmpty())
		{
			final StringBuffer errorString = new StringBuffer();
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

		return WARNING_LIST;
	}


	/**
	 * @param classNames
	 * @param outputXmlCategory
	 */
	private void processEntityList(final List<ClassType> classNames,final XmlCategory outputXmlCategory)
	{
		//List<ClassType> outputClassNames=new ArrayList<ClassType>();
		//
		for (ClassType className : classNames)
		{
			final ClassType outputClassName = new ClassType();
			outputXmlCategory.getClassName().add(outputClassName);

			final List<InstanceType> instances = className.getInstance();
			for (InstanceType instance : instances)
			{
				outputClassName.setName(className.getName());

				final StringBuffer catEntityName = new StringBuffer(className.getName());
				catEntityName.append('[');
				final List<XmlAttributeType> attributes = instance.getXmlAttribute();

				final CategoryPermissibleValuesProcessorHelper pvProcessorHelper = new CategoryPermissibleValuesProcessorHelper(null);
				// if instance information is not given in XML
				if ("".equals(instance.getId()) || "ALL".equalsIgnoreCase(instance.getId()))
				{
					processVersioningForMultipleCategoryEntities(pvProcessorHelper, catEntityName,
							attributes, outputClassName);
				}
				else
				{
					processForSingleEntity(outputClassName, instance, catEntityName, attributes,
							pvProcessorHelper);
				}
			}
		}
	}

	/**
	 * @param outputClassName
	 * @param instance
	 * @param catEntityName
	 * @param attributes
	 * @param pvProcessorHelper
	 */
	private void processForSingleEntity(final ClassType outputClassName,final InstanceType instance,
			final StringBuffer catEntityName,final List<XmlAttributeType> attributes,
			final CategoryPermissibleValuesProcessorHelper pvProcessorHelper)
	{
		final InstanceType outputInstance = new InstanceType();
		outputClassName.getInstance().add(outputInstance);
		// if instance information is given in XML
		catEntityName.append(instance.getId()).append(']');
		outputInstance.setId(instance.getId());

		final List<CategoryEntityInterface> catEntity = pvProcessorHelper
				.getAllMatchingCategoryEntities(catEntityName.toString(),
						CATEGORY_MAP, false);

		// add PVs to that category entity attribute
		processAttributes(attributes, catEntity.get(0), outputInstance);
	}

	/**
	 * @param pvProcessorHelper
	 * @param catEntityName
	 * @param attributes
	 * @param outputClassName
	 */
	private void processVersioningForMultipleCategoryEntities(
			final CategoryPermissibleValuesProcessorHelper pvProcessorHelper,final StringBuffer catEntityName,
			final List<XmlAttributeType> attributes,final ClassType outputClassName)
	{
		// get all possible category entities
		final List<CategoryEntityInterface> entities = pvProcessorHelper.getAllMatchingCategoryEntities(
				catEntityName.toString(), CATEGORY_MAP, true);

		// add PVs to the attribute in all the entities
		for (CategoryEntityInterface catEntity : entities)
		{
			final InstanceType outputInstance = new InstanceType();
			outputClassName.getInstance().add(outputInstance);
			final Collection<PathAssociationRelationInterface> associationRelations=catEntity.getPath().getPathAssociationRelationCollection();
			for (PathAssociationRelationInterface pathAssociationRelationInterface : associationRelations)
			{
				if(outputClassName.getName().equalsIgnoreCase(pathAssociationRelationInterface.getAssociation().getName()))
				{
					outputInstance.setId(pathAssociationRelationInterface.getTargetInstanceId().toString());
				}
			}
			processAttributes(attributes, catEntity, outputInstance);
		}
	}


	/**
	 * @param attributes
	 * @param categoryEntity
	 * @param instanceType
	 */
	private void processAttributes(final List<XmlAttributeType> attributes,
			final CategoryEntityInterface categoryEntity, final InstanceType instanceType)
	{
		for (XmlAttributeType xmlAttribute : attributes)
		{

			final String attributeName = xmlAttribute.getName();
			if ("ALL".equalsIgnoreCase(attributeName))
			{

				processForAllAttributes(categoryEntity, instanceType, xmlAttribute);
			}
			else
			{
				final XmlAttributeType outputXmlAttribute = new XmlAttributeType();
				instanceType.getXmlAttribute().add(outputXmlAttribute);
				outputXmlAttribute.setName(xmlAttribute.getName());

				final CategoryAttributeInterface categoryAttribute = categoryEntity
						.getAttributeByEntityAttributeName(attributeName);

				if (categoryAttribute == null)
				{
					/** if instance information is not given in XML, then, import version for all instances.
					Some instance might not have the attribute, so skip the loop. */
					final List<String> placeHolders = new ArrayList<String>();
					placeHolders.add(attributeName);
					placeHolders.add(categoryEntity.getName());
					LOGGER.error(ApplicationProperties.getValue("error.missing.attribute",
							placeHolders));
					ERROR_LIST.add(ApplicationProperties.getValue("error.missing.attribute",
							placeHolders));
					continue;
				}
				try
				{
					processPvSet(categoryAttribute, xmlAttribute, outputXmlAttribute);
				}
				catch (Exception e)
				{
					ERROR_LIST.add(e.getMessage());
				}
			}
		}
	}


	/**
	 * @param categoryEntity
	 * @param instanceType
	 * @param xmlAttribute
	 */
	private void processForAllAttributes(final CategoryEntityInterface categoryEntity,
			final InstanceType instanceType,final XmlAttributeType xmlAttribute)
	{
		final Collection<CategoryAttributeInterface> attributeInterfaces = categoryEntity
				.getAllCategoryAttributes();
		for (CategoryAttributeInterface categoryAttributeInterface : attributeInterfaces)
		{
			final XmlAttributeType outputXmlAttribute = new XmlAttributeType();
			instanceType.getXmlAttribute().add(outputXmlAttribute);
			outputXmlAttribute.setName(categoryAttributeInterface.getName());

			try
			{
				processPvSet(categoryAttributeInterface, xmlAttribute, outputXmlAttribute);
			}
			catch (Exception e)
			{
				ERROR_LIST.add(e.getMessage());
			}
		}
	}

	/**
	 * @param categoryAttribute
	 * @param xmlAttribute
	 * @param outputXmlAttribute
	 * @throws DynamicExtensionsSystemException
	 * @throws ParseException
	 */
	private void processPvSet(final CategoryAttributeInterface categoryAttribute,
			final XmlAttributeType xmlAttribute,final XmlAttributeType outputXmlAttribute)
			throws DynamicExtensionsSystemException, ParseException
	{
		final CategoryPermissibleValuesProcessorHelper pvProcessorHelper = new CategoryPermissibleValuesProcessorHelper(null);

		final List<PvSetType> pvSetTypes = xmlAttribute.getPvSet();
		for (PvSetType pvSet : pvSetTypes)
		{
			final Object activationDate = pvProcessorHelper.getActivationDate(pvSet.getProperties(),
					categoryAttribute);
			if (activationDate instanceof Date)
			{
				processForSingleActivationDate(categoryAttribute, outputXmlAttribute, pvSet,
						activationDate);
			}
			else
			{
					processForAllActivationDate(categoryAttribute, outputXmlAttribute, pvSet);
			}
		}
	}

	/**
	 * @param categoryAttribute
	 * @param outputXmlAttribute
	 * @param pvSet
	 */
	private void processForAllActivationDate(final CategoryAttributeInterface categoryAttribute,
			final XmlAttributeType outputXmlAttribute,final PvSetType pvSet)
	{
		final Collection<DataElementInterface> dataElementInterfaces = categoryAttribute.getDataElementCollection();
		for (DataElementInterface dataElementInterface : dataElementInterfaces)
		{
			final PvSetType outputPvSetTypes = new PvSetType();
			outputXmlAttribute.getPvSet().add(outputPvSetTypes);
			final XmlPermissibleValues permissibleValue = new XmlPermissibleValues();

			outputPvSetTypes.setXmlPermissibleValues(permissibleValue);
			if (dataElementInterface instanceof UserDefinedDEInterface)
			{
				final UserDefinedDEInterface userDefinedDEInterface = (UserDefinedDEInterface) dataElementInterface;
				final Collection<PermissibleValueInterface> permissibleValueInterfaces = userDefinedDEInterface
						.getPermissibleValues();

				for (PermissibleValueInterface permissibleValueInterface : permissibleValueInterfaces)
				{
					if (permissibleValueInterface != null
							&& permissibleValueInterface.getValueAsObject() != null)
					{
						permissibleValue.getValue()
								.add(
										permissibleValueInterface.getValueAsObject()
												.toString());
					}
				}
				final DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy",Locale.getDefault());
				String formatedDate=null;
				if(userDefinedDEInterface.getActivationDate()==null)
				{
					formatedDate="NULL";
				}
				else
				{
					formatedDate = formatter.format(userDefinedDEInterface.getActivationDate());
				}
				final PropertyType outputPropertyType=new PropertyType();
				outputPvSetTypes.setProperties(outputPropertyType);
				final List<Option> permValOptions = pvSet.getProperties().getOption();
				processOptions(formatedDate, outputPropertyType, permValOptions);
			}
		}
	}

	/**
	 * @param formatedDate
	 * @param outputProperty
	 * @param permValOptions
	 */
	private void processOptions(final String formatedDate,final PropertyType outputProperty,
			final List<Option> permValOptions)
	{
		for (Option option : permValOptions)
		{
			final Option outpuOption=new Option();
			outputProperty.getOption().add(outpuOption);
			final String key = option.getKey();
			outpuOption.setKey(key);
			if ("activationDate".equalsIgnoreCase(key))
			{
				outpuOption.setValue(formatedDate);
			}
			else
			{

				outpuOption.setValue(option.getValue());
			}
		}
	}

	/**
	 * @param categoryAttribute
	 * @param outputXmlAttribute
	 * @param pvSet
	 * @param activationDate
	 */
	private void processForSingleActivationDate(final CategoryAttributeInterface categoryAttribute,
			final XmlAttributeType outputXmlAttribute,final PvSetType pvSet,final Object activationDate)
	{
		final PvSetType outputPvSetTypes = new PvSetType();
		outputXmlAttribute.getPvSet().add(outputPvSetTypes);
		final XmlPermissibleValues permissibleValue = new XmlPermissibleValues();
		outputPvSetTypes.setProperties(pvSet.getProperties());
		outputPvSetTypes.setXmlPermissibleValues(permissibleValue);
		final Collection<DataElementInterface> dataElementInterfaces = categoryAttribute.getDataElementCollection();
		for (DataElementInterface dataElementInterface : dataElementInterfaces)
		{
			if (dataElementInterface instanceof UserDefinedDEInterface)
			{
				final UserDefinedDEInterface userDefinedDEInterface = (UserDefinedDEInterface) dataElementInterface;

			   if(activationDate.equals(userDefinedDEInterface.getActivationDate()))
			   {
				final Collection<PermissibleValueInterface> permissibleValueInterfaces = userDefinedDEInterface
					.getPermissibleValues();
				for (PermissibleValueInterface permissibleValueInterface : permissibleValueInterfaces)
				{
					if (permissibleValueInterface != null
							&& permissibleValueInterface.getValueAsObject() != null)
					{
						permissibleValue.getValue().add(
								permissibleValueInterface.getValueAsObject().toString());
					}
				}
			  }
			}
		}
	}
}