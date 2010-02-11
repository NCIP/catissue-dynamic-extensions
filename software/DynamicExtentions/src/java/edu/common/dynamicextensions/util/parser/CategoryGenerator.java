
package edu.common.dynamicextensions.util.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.owasp.stinger.Stinger;

import edu.common.dynamicextensions.domain.CategoryEntity;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.NumericAttributeTypeInformation;
import edu.common.dynamicextensions.domain.UserDefinedDE;
import edu.common.dynamicextensions.domain.userinterface.CheckBox;
import edu.common.dynamicextensions.domain.userinterface.TextField;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.DataElementInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.FormControlNotesInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.SkipLogicAttributeInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.CategoryGenerationUtil;
import edu.common.dynamicextensions.util.CategoryHelper;
import edu.common.dynamicextensions.util.CategoryHelperInterface;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.CategoryHelperInterface.ControlEnum;
import edu.common.dynamicextensions.util.global.CategoryConstants;
import edu.common.dynamicextensions.validation.category.CategoryValidator;
import edu.wustl.common.util.global.ApplicationProperties;

/**
 * @author kunal_kamble
 * This class creates the category/categories defined in
 * the CSV file.
 */
public class CategoryGenerator
{

	private final CategoryFileParser categoryFileParser;

	private CategoryValidator categoryValidator;

	private final CategoryHelperInterface categoryHelper;

	private final List<String> mainFormList = new ArrayList<String>();

	private CategoryInterface category;
	private EntityGroupInterface entityGroup;
	private List<ContainerInterface> containerCollection;
	private Map<String, String> categoryEntityNameInstanceMap;
	private Map<String, List<AssociationInterface>> entityNameAssociationMap;

	public CategoryValidator getCategoryValidator()
	{
		return categoryValidator;
	}

	public void setCategoryValidator(final CategoryValidator categoryValidator)
	{
		this.categoryValidator = categoryValidator;
	}

	/**
	 * @param filePath the path of the category finle
	 * @param baseDir base directory from which the filepath is mentioned.
	 * @param stinger the stinger validator object which is used to validate the pv strings.
	 * @throws DynamicExtensionsSystemException
	 * @throws FileNotFoundException
	 */
	public CategoryGenerator(final String filePath, final String baseDir, Stinger stinger)
			throws DynamicExtensionsSystemException, FileNotFoundException
	{
		categoryFileParser = DomainObjectFactory.getInstance().createCategoryFileParser(filePath,
				baseDir, stinger);
		categoryValidator = new CategoryValidator((CategoryCSVFileParser) categoryFileParser);
		categoryHelper = new CategoryHelper(categoryFileParser);
	}

	/**
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws ParseException
	 */
	public CategoryInterface generateCategory() throws DynamicExtensionsSystemException
	{
		final CategoryHelperInterface categoryHelper = new CategoryHelper(categoryFileParser);
		ApplicationProperties.initBundle("ApplicationResources");

		try
		{
			while (categoryFileParser.readNext())
			{
				// First line in the category file is Category_Definition.
				if (categoryFileParser.hasFormDefination())
				{
					continue;
				}

				// Category definition in the file starts.
				// 1: Read the category name
				category = categoryHelper.getCategory(categoryFileParser.getCategoryName());

				// 2: Read the entity group.
				categoryFileParser.readNext();
				entityGroup = CategoryGenerationUtil.getEntityGroup(category, categoryFileParser
						.getEntityGroupName());

				CategoryValidator.checkForNullRefernce(entityGroup, ApplicationProperties
						.getValue(CategoryConstants.LINE_NUMBER)
						+ categoryFileParser.getLineNumber()
						+ ApplicationProperties.getValue("noEntityGroup")
						+ categoryFileParser.getEntityGroupName());

				categoryFileParser.getCategoryValidator().setEntityGroupId(entityGroup.getId());
				populateMainFormList(entityGroup);

				// 3: Get the path represented by ordered entity names.
				categoryFileParser.readNext();
				final Map<String, List<String>> paths = categoryFileParser.getPaths();

				// 4: Get the association names list.
				entityNameAssociationMap = CategoryGenerationUtil.getAssociationList(paths,
						entityGroup);

				containerCollection = new ArrayList<ContainerInterface>();

				ContainerInterface container = null;
				EntityInterface entityInterface = null;

				// 5: Get the selected attributes and create the controls for
				// them.
				List<String> categoryEntityName = null;
				ControlInterface lastControl = null;
				categoryEntityNameInstanceMap = new HashMap<String, String>();
				String previousEntityName = "";
				boolean firstTimeinDisplayLabel = false;
				// HashMap<String, List> sequenceMap = new HashMap<String,
				// List>();
				int controlXPosition = 1;
				int controlYPosition = 0;

				Map<String, String> commonControlOptions = null;

				while (categoryFileParser.readNext())
				{
					if (categoryFileParser.hasFormDefination())
					{
						break;
					}

					if (categoryFileParser.hasRelatedAttributes()
							|| categoryFileParser.hasSkipLogicAttributes())
					{
						break;
					}
					if (categoryFileParser.hasDisplayLable())
					{
						categoryEntityName = processDisplayLabel();

						firstTimeinDisplayLabel = true;
						previousEntityName = null;
						categoryFileParser.readNext();
						controlXPosition = 1;
						controlYPosition = 0;
					}

					if (categoryFileParser.hasSubcategory())
					{
						lastControl = processSubcategory(firstTimeinDisplayLabel, entityInterface,
								categoryEntityName);
					}
					else
					{
						// Add control to the container.
						if (categoryFileParser.isSingleLineDisplayStarted())
						{
							controlYPosition++;
						}
						if (categoryFileParser.isSingleLineDisplayEnd())
						{
							controlYPosition = 0;
							controlXPosition++;
							commonControlOptions = null;
							continue;
						}
						if (categoryFileParser.hasSeparator())
						{
							categoryHelper.addOrUpdateLabelControl(entityInterface, container,
									categoryFileParser.getSeparator(), categoryFileParser
											.getLineNumber(), controlXPosition, controlYPosition);

							continue;
						}
						if (categoryFileParser.hasCommonControlOptions())
						{
							commonControlOptions = categoryFileParser.getCommonControlOptions();
							categoryFileParser.readNext();
						}
						final String heading = categoryFileParser.getHeading();

						final List<FormControlNotesInterface> controlNotes = new LinkedList<FormControlNotesInterface>();
						categoryFileParser.getFormControlNotes(controlNotes);

						final Map<String, Collection<SemanticPropertyInterface>> permissibleValues = categoryFileParser
								.getPermissibleValues();

						final Map<String, String> permissibleValueOptions = categoryFileParser
								.getPermissibleValueOptions();

						final String attributeName = categoryFileParser.getAttributeName();

						entityInterface = entityGroup.getEntityByName(categoryFileParser
								.getEntityName());

						// Added for category inheritance, check if a given
						// attribute is parent category attribute.
						final boolean isAttributePresent = entityInterface
								.isAttributePresent(attributeName);

						CategoryValidator.checkForNullRefernce(getcategoryEntityName(
								categoryEntityNameInstanceMap, categoryFileParser.getEntityName()),
								ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
										+ categoryFileParser.getLineNumber()
										+ categoryEntityName
										+ ApplicationProperties
												.getValue("incorrectInstanceInformation"));

						container = CategoryGenerationUtil.getContainerWithCategoryEntityName(
								containerCollection, getCategoryEntityName(categoryEntityName,
										categoryFileParser.getEntityName()));

						final AttributeInterface attribute = entityInterface
								.getAttributeByName(attributeName);

						if ((attribute != null) && (permissibleValues != null))
						{
							final UserDefinedDE userDefinedDE = (UserDefinedDE) attribute
									.getAttributeTypeInformation().getDataElement();
							if (userDefinedDE == null)
							{
								throw new DynamicExtensionsSystemException(ApplicationProperties
										.getValue(CategoryConstants.CREATE_CAT_FAILS)
										+ ApplicationProperties
												.getValue(CategoryConstants.NO_PV_FOR_ATTR)
										+ attributeName);
							}
						}

						CategoryValidator.checkForNullRefernce(attribute, ApplicationProperties
								.getValue(CategoryConstants.LINE_NUMBER)
								+ categoryFileParser.getLineNumber()
								+ " "
								+ ApplicationProperties.getValue(CategoryConstants.ATTR)
								+ attributeName
								+ " "
								+ ApplicationProperties
										.getValue(CategoryConstants.ATTR_NOT_PRESENT)
								+ entityInterface.getName());

						if ((previousEntityName != null)
								&& !previousEntityName.equals(container.getAbstractEntity()
										.getName()))
						{
							previousEntityName = container.getAbstractEntity().getName();
						}
						CategoryEntityInterface categoryEntity = (CategoryEntityInterface) container
								.getAbstractEntity();
						// If this is a parent attribute and currently the
						// parent category entity is not created
						// for given category entity, create parent category
						// hierarchy up to where attribute is found.
						if (!isAttributePresent)
						{
							final EntityInterface parentEntity = entityInterface.getParentEntity();
							final EntityInterface childEntity = entityInterface;
							final CategoryEntityInterface parentCategoryEntity = categoryEntity
									.getParentCategoryEntity();

							categoryEntity = processInheritance(parentEntity, childEntity,
									parentCategoryEntity, categoryEntity, attributeName,
									containerCollection);

							entityInterface = categoryEntity.getEntity();
						}

						final Map<String, Object> rules = categoryFileParser.getRules(attribute
								.getName());
						if (rules != null)
						{
							CategoryValidator
									.checkRangeAgainstAttributeValueRange(attribute, rules);
							CategoryValidator.checkRequiredRule(attribute, rules);
						}
						CategoryValidator.checkIfFutureDateRuleSpecified(attribute, rules);
						CategoryValidator.validateCSVFutureDateValue(attribute, rules,
								categoryFileParser.getDefaultValue());

						final String controlType = categoryFileParser.getControlType();
						getCategoryValidator().isTextAreaForNumeric(controlType, attribute);
						/*if (categoryFileParser.isSingleLineDisplayStarted())
						{
							getCategoryValidator().validateControlInSingleLine(controlType,
									controlXPosition, container);
						}*/
						lastControl = categoryHelper.addOrUpdateControl(entityInterface,
								attributeName, container, ControlEnum.get(controlType),
								categoryFileParser.getControlCaption(), heading, controlNotes,
								rules, permissibleValueOptions, categoryFileParser.getLineNumber(),
								permissibleValues);

						// Set default value for attribute's IsRelatedAttribute
						// and IsVisible property.
						// This is required in case of edit of category entity.
						((CategoryAttributeInterface) lastControl.getAttibuteMetadataInterface())
								.setIsRelatedAttribute(false);
						((CategoryAttributeInterface) lastControl.getAttibuteMetadataInterface())
								.setIsVisible(true);

						// Set default value for control's option.
						// This is required in case of edit of category entity.
						categoryHelper.setDefaultControlsOptions(lastControl, ControlEnum
								.get(categoryFileParser.getControlType()));

						final Map<String, String> controlOptions = categoryFileParser
								.getControlOptions();

						if (commonControlOptions != null)
						{
							categoryHelper.setOptions(lastControl, commonControlOptions,
									categoryFileParser.getLineNumber());
						}
						categoryHelper.setOptions(lastControl, controlOptions, categoryFileParser
								.getLineNumber());

						setDefaultValue(lastControl);

						CategoryValidator.checkIsMultiSelectValid(entityInterface, attributeName,
								lastControl);

						// Clear category entity from related attribute
						// collection of root category entity
						// only if no category attribute in this category entity
						// is a related category attribute.
						if (DynamicExtensionsUtility
								.areNoRelatedCategoryAttributesPresent(container))
						{
							category
									.removeRelatedAttributeCategoryEntity((CategoryEntityInterface) container
											.getAbstractEntity());
						}

						// Check for isReadOnly option.
						if (lastControl.getIsReadOnly())
						{
							((CategoryAttributeInterface) lastControl
									.getAttibuteMetadataInterface()).setIsRelatedAttribute(true);
							((CategoryAttributeInterface) lastControl
									.getAttibuteMetadataInterface()).setIsVisible(true);
							category
									.addRelatedAttributeCategoryEntity((CategoryEntityInterface) container
											.getAbstractEntity());
						}
					}

					lastControl.setControlPosition(controlXPosition, controlYPosition);
					if (!categoryFileParser.isSingleLineDisplayStarted())
					{
						controlXPosition++;
					}

				}

				CategoryGenerationUtil.setRootContainer(category, container, containerCollection,
						entityNameAssociationMap, paths, categoryEntityNameInstanceMap);

				categoryValidator.isRootEntityUsedTwice(category.getRootCategoryElement(), category
						.getRootCategoryElement().getEntity());
				if (categoryFileParser.hasRelatedAttributes())
				{
					handleRelatedAttributes(entityGroup, category, entityNameAssociationMap,
							containerCollection);
					if (categoryFileParser.hasSkipLogicAttributes())
					{
						handleSkipLogicAttributes(entityGroup, category, entityNameAssociationMap,
								containerCollection);
					}
				}
				if (categoryFileParser.hasSkipLogicAttributes())
				{
					handleSkipLogicAttributes(entityGroup, category, entityNameAssociationMap,
							containerCollection);
					if (categoryFileParser.hasRelatedAttributes())
					{
						handleRelatedAttributes(entityGroup, category, entityNameAssociationMap,
								containerCollection);
					}
				}
				CategoryGenerationUtil.setDefaultValueForCalculatedAttributes(category, category
						.getRootCategoryElement(), categoryFileParser.getLineNumber());

				// Commented this code since the method is error prone
				// TODO change this logic to reset the sequnces.
				// rearrangeControlSequence((ContainerInterface)
				// category.getRootCategoryElement()
				// .getContainerCollection().iterator().next(), sequenceMap);

			}

		}
		catch (final Exception e)
		{
			categoryHelper.releaseLockOnCategory(category);
			if (!(e instanceof DynamicExtensionsSystemException))
			{
				throw new DynamicExtensionsSystemException(ApplicationProperties
						.getValue(CategoryConstants.CREATE_CAT_FAILS)
						+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
						+ categoryFileParser.getLineNumber()
						+ " "
						+ ApplicationProperties.getValue("readingFile")
						+ categoryFileParser.getFilePath(), e);
			}

			throw new DynamicExtensionsSystemException("", e);
		}
		finally
		{
			categoryFileParser.closeResources();
		}
		return category;
	}

	private ControlInterface processSubcategory(boolean firstTimeinDisplayLabel,
			final EntityInterface entityInterface, final List<String> categoryEntityName)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		// Set this flag when sub category is just after display label
		if (firstTimeinDisplayLabel)
		{
			firstTimeinDisplayLabel = false;
		}
		ContainerInterface sourceContainer = null;
		if (entityInterface == null)
		{
			sourceContainer = CategoryGenerationUtil.getContainer(containerCollection,
					categoryFileParser.getDisplyLable());
		}
		else
		{
			// Always add sub-category to the container.
			sourceContainer = CategoryGenerationUtil.getContainerWithCategoryEntityName(
					containerCollection, categoryEntityName.get(0));
		}

		final String targetContainerCaption = categoryFileParser.getTargetContainerCaption();
		final ContainerInterface targetContainer = CategoryGenerationUtil.getContainer(
				containerCollection, targetContainerCaption);

		CategoryValidator.checkForNullRefernce(targetContainer, ApplicationProperties
				.getValue(CategoryConstants.LINE_NUMBER)
				+ categoryFileParser.getLineNumber()
				+ ApplicationProperties.getValue("subcategoryNotFound") + targetContainerCaption);

		final String multiplicity = categoryFileParser.getMultiplicity();

		final String categoryEntName = ((CategoryEntityInterface) CategoryGenerationUtil
				.getContainer(containerCollection, targetContainerCaption).getAbstractEntity())
				.getName();

		final List<AssociationInterface> associationNameList = entityNameAssociationMap
				.get(CategoryGenerationUtil
						.getEntityNameForAssociationMap(categoryEntityNameInstanceMap
								.get(categoryEntName)));

		CategoryValidator.checkForNullRefernce(associationNameList, ApplicationProperties
				.getValue(CategoryConstants.LINE_NUMBER)
				+ categoryFileParser.getLineNumber()
				+ ApplicationProperties.getValue("pathNotFound") + targetContainerCaption);

		final CategoryHelper categoryHelper = new CategoryHelper(categoryFileParser);
		final ControlInterface lastControl = categoryHelper.associateCategoryContainers(category,
				entityGroup, sourceContainer, targetContainer, associationNameList,
				CategoryGenerationUtil.getMultiplicityInNumbers(multiplicity),
				categoryEntityNameInstanceMap.get(targetContainer.getAbstractEntity().getName()));

		return lastControl;

	}

	/**
	 * @param categoryEntityNameList
	 * @param entityName
	 * @return
	 */
	private String getcategoryEntityName(final Map<String, String> categoryEntityNameInstanceMap,
			final String entityName)
	{
		String categoryEntityName = null;
		for (final String name : categoryEntityNameInstanceMap.keySet())
		{
			if (entityName.equals(CategoryGenerationUtil
					.getEntityNameFromCategoryEntityInstancePath(categoryEntityNameInstanceMap
							.get(name))))
			{
				categoryEntityName = name;
			}
		}
		return categoryEntityName;
	}

	/**
	 * @param childCategoryEntity
	 * @param parentEntity
	 * @param entityGroup
	 * @param containerCollection
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private ContainerInterface createParentCategoryEntity(
			final CategoryEntityInterface childCategoryEntity, final EntityInterface parentEntity,
			final EntityGroupInterface entityGroup,
			final Collection<ContainerInterface> containerCollection)
			throws DynamicExtensionsSystemException
	{
		final String newCategoryEntityName = parentEntity.getName() + "[1]";
		for (final ContainerInterface objContainer : containerCollection)
		{
			if (objContainer.getCaption().equals(newCategoryEntityName))
			{
				return objContainer;
			}

		}

		final ContainerInterface parentContainer = createCategoryEntityAndContainer(entityGroup
				.getEntityByName(parentEntity.getName()), newCategoryEntityName,
				newCategoryEntityName, false, containerCollection, category);
		(childCategoryEntity).setParentCategoryEntity((CategoryEntityInterface) parentContainer
				.getAbstractEntity());
		final CategoryEntity parentCEntity = (CategoryEntity) parentContainer.getAbstractEntity();
		parentCEntity.addChildCategory(childCategoryEntity);

		final CategoryEntity parentCategoryEntity = (CategoryEntity) (childCategoryEntity)
				.getParentCategoryEntity();
		parentCategoryEntity.setCreateTable(false);
		return parentContainer;
	}

	/**
	 * @param entityGroup
	 * @param category
	 * @param entityNameAssociationMap
	 * @throws IOException
	 * @throws ParseException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void handleRelatedAttributes(final EntityGroupInterface entityGroup,
			final CategoryInterface category,
			final Map<String, List<AssociationInterface>> entityNameAssociationMap,
			final List<ContainerInterface> containerCollection) throws IOException, ParseException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		while (categoryFileParser.readNext() && !categoryFileParser.hasSkipLogicAttributes())
		{
			final String[] categoryPaths = categoryFileParser.getCategoryPaths();
			final String categoryEntityName = CategoryGenerationUtil.getCategoryEntityName(
					categoryPaths[0], entityNameAssociationMap);

			final String entityName = CategoryGenerationUtil
					.getEntityNameFromCategoryEntityInstancePath(categoryPaths[0]);

			final String entityNameForEntAssocMap = CategoryGenerationUtil
					.getEntityNameForAssociationMap(categoryPaths[0]);

			EntityInterface entity = entityGroup.getEntityByName(CategoryGenerationUtil
					.getEntityNameExcludingAssociationRoleName(entityName));

			categoryFileParser.readNext();
			final String attributeName = categoryFileParser.getRelatedAttributeName();
			final CategoryHelperInterface categoryHelper = new CategoryHelper(categoryFileParser);
			boolean newCategoryCreated = false;
			if (category.getCategoryEntityByName(categoryEntityName) == null)
			{
				newCategoryCreated = true;
			}
			CategoryEntityInterface categoryEntity = categoryHelper.createOrUpdateCategoryEntity(
					category, entity, categoryEntityName);

			if (newCategoryCreated)
			{
				final String associationName = category.getRootCategoryElement() + " to "
						+ categoryEntity.getName() + " association";
				categoryHelper.associateCategoryEntities(category.getRootCategoryElement(),
						categoryEntity, associationName, 1, entityGroup, entityNameAssociationMap
								.get(entityNameForEntAssocMap), categoryPaths[0]);
			}
			CategoryValidator.checkForNullRefernce(entity.getAttributeByName(attributeName),
					ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
							+ categoryFileParser.getLineNumber() + " "
							+ ApplicationProperties.getValue(CategoryConstants.ATTR)
							+ attributeName + " "
							+ ApplicationProperties.getValue(CategoryConstants.ATTR_NOT_PRESENT)
							+ entity.getName());

			// Added for category inheritance.
			final boolean isAttributePresent = entity.isAttributePresent(attributeName);

			// If this is the parent attribute and currently the parent category
			// entity is not created
			// for given category entity, create parent category hierarchy up to
			// where attribute is found.
			if (!isAttributePresent)
			{
				final EntityInterface parentEntity = entity.getParentEntity();
				final EntityInterface childEntity = entity;
				final CategoryEntityInterface parentCategoryEntity = categoryEntity
						.getParentCategoryEntity();

				categoryEntity = processInheritance(parentEntity, childEntity,
						parentCategoryEntity, categoryEntity, attributeName, containerCollection);
				entity = categoryEntity.getEntity();

			}

			final CategoryAttributeInterface categoryAttribute = categoryHelper
					.getCategoryAttribute(entity, attributeName, categoryEntity);
			final Map<String, String> controlOptions = categoryFileParser.getControlOptions();

			categoryHelper.setOptions(categoryAttribute, controlOptions, categoryFileParser
					.getLineNumber());

			final String defaultValue = categoryFileParser.getDefaultValueForRelatedAttribute();

			if ((categoryAttribute.getIsCalculated() != null)
					&& categoryAttribute.getIsCalculated())
			{
				setFormula(categoryAttribute, defaultValue);
			}
			else
			{
				categoryAttribute.setDefaultValue(entity.getAttributeByName(attributeName)
						.getAttributeTypeInformation().getPermissibleValueForString(
								DynamicExtensionsUtility.getEscapedStringValue(defaultValue)));
			}
			categoryAttribute.setIsVisible(false);
			categoryAttribute.setIsRelatedAttribute(true);
			category.addRelatedAttributeCategoryEntity(categoryEntity);

		}
	}

	/**
	 * @param entityGroup
	 * @param category
	 * @param entityNameAssociationMap
	 * @throws IOException
	 * @throws ParseException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void handleSkipLogicAttributes(final EntityGroupInterface entityGroup,
			final CategoryInterface category,
			final Map<String, List<AssociationInterface>> entityNameAssociationMap,
			final List<ContainerInterface> containerCollection) throws IOException, ParseException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		while (categoryFileParser.readNext() && !categoryFileParser.hasRelatedAttributes())
		{
			final String[] categoryPaths = categoryFileParser.getCategoryPaths();

			categoryFileParser.readNext();

			final String sourceAttributeClassName = categoryFileParser
					.getSkipLogicSourceAttributeClassName();

			final String sourceInstancePath = getInstancePath(categoryPaths,
					sourceAttributeClassName);

			final String sourceCategoryEntityName = CategoryGenerationUtil.getCategoryEntityName(
					sourceInstancePath, entityNameAssociationMap);

			final String sourceEntityName = CategoryGenerationUtil
					.getEntityNameFromCategoryEntityInstancePath(sourceInstancePath);

			EntityInterface sourceEntity = entityGroup.getEntityByName(CategoryGenerationUtil
					.getEntityNameExcludingAssociationRoleName(sourceEntityName));

			CategoryEntityInterface sourceCategoryEntity = categoryHelper
					.createOrUpdateCategoryEntity(category, sourceEntity, sourceCategoryEntityName);

			final String targetAttributeClassName = categoryFileParser
					.getSkipLogicTargetAttributeClassName();

			final String targetInstancePath = getInstancePath(categoryPaths,
					targetAttributeClassName);

			final String targetCategoryEntityName = CategoryGenerationUtil.getCategoryEntityName(
					targetInstancePath, entityNameAssociationMap);

			final String targetEntityName = CategoryGenerationUtil
					.getEntityNameFromCategoryEntityInstancePath(targetInstancePath);

			EntityInterface targetEntity = entityGroup.getEntityByName(CategoryGenerationUtil
					.getEntityNameExcludingAssociationRoleName(targetEntityName));

			CategoryEntityInterface targetCategoryEntity = categoryHelper
					.createOrUpdateCategoryEntity(category, targetEntity, targetCategoryEntityName);

			final String sourceAttributeName = categoryFileParser.getSkipLogicSourceAttributeName();

			CategoryValidator.checkForNullRefernce(sourceEntity
					.getAttributeByName(sourceAttributeName), ApplicationProperties
					.getValue(CategoryConstants.LINE_NUMBER)
					+ categoryFileParser.getLineNumber()
					+ " "
					+ ApplicationProperties.getValue(CategoryConstants.ATTR)
					+ sourceAttributeName
					+ " "
					+ ApplicationProperties.getValue(CategoryConstants.ATTR_NOT_PRESENT)
					+ sourceEntity.getName());
			boolean isAttributePresent = sourceEntity.isAttributePresent(sourceAttributeName);

			// If this is the parent attribute and currently the parent category
			// entity is not created
			// for given category entity, create parent category hierarchy up to
			// where attribute is found.
			if (!isAttributePresent)
			{
				final EntityInterface parentEntity = sourceEntity.getParentEntity();
				final EntityInterface childEntity = sourceEntity;
				final CategoryEntityInterface parentCategoryEntity = sourceCategoryEntity
						.getParentCategoryEntity();

				sourceCategoryEntity = processInheritance(parentEntity, childEntity,
						parentCategoryEntity, sourceCategoryEntity, sourceAttributeName,
						containerCollection);
				sourceEntity = sourceCategoryEntity.getEntity();

			}

			final CategoryAttributeInterface sourceCategoryAttribute = categoryHelper
					.getCategoryAttribute(sourceEntity, sourceAttributeName, sourceCategoryEntity);

			final ContainerInterface sourceContainerInterface = DynamicExtensionsUtility
					.getContainerForAbstractEntity(sourceCategoryAttribute.getCategoryEntity());

			final ControlInterface sourceControl = DynamicExtensionsUtility
					.getControlForAbstractAttribute(
							(AttributeMetadataInterface) sourceCategoryAttribute,
							sourceContainerInterface, sourceCategoryEntityName);

			boolean isEnumeratedSourceControl = true;
			if (sourceControl instanceof CheckBox)
			{
				isEnumeratedSourceControl = false;
			}
			sourceControl.setIsSkipLogic(Boolean.TRUE);
			sourceCategoryAttribute.setIsSkipLogic(Boolean.TRUE);
			PermissibleValueInterface permissibleValueInterface = null;
			if (isEnumeratedSourceControl)
			{
				final String permissibleValue = categoryFileParser
						.getSkipLogicPermissibleValueName();
				final DataElementInterface dataElementInterface = ((AttributeMetadataInterface) sourceCategoryAttribute)
						.getDataElement();
				final UserDefinedDEInterface userDefinedDEInterface = (UserDefinedDEInterface) dataElementInterface;

				permissibleValueInterface = categoryHelper.getPermissibleValue(
						userDefinedDEInterface.getPermissibleValueCollection(),
						DynamicExtensionsUtility.getEscapedStringValue(permissibleValue));

				sourceCategoryAttribute.addSkipLogicPermissibleValue(permissibleValueInterface);

				CategoryValidator.checkForNullRefernce(permissibleValueInterface,
						ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
								+ categoryFileParser.getLineNumber()
								+ " "
								+ ApplicationProperties.getValue(CategoryConstants.ATTR)
								+ permissibleValue
								+ " "
								+ ApplicationProperties
										.getValue(CategoryConstants.PV_ATTR_NOT_PRESENT)
								+ sourceAttributeName);
			}
			final ContainerInterface targetContainerInterface = DynamicExtensionsUtility
					.getContainerForAbstractEntity(targetCategoryEntity);

			final String targetAttributeName = categoryFileParser.getSkipLogicTargetAttributeName();

			if ((targetAttributeName != null) && targetAttributeName.equals(CategoryConstants.ALL))
			{
				//Setting control options
				final Map<String, String> controlOptions = categoryFileParser.getControlOptions();

				setSkipLogicForAllSubFormAttributes(targetCategoryEntity, sourceControl,
						sourceCategoryAttribute, permissibleValueInterface,
						isEnumeratedSourceControl, controlOptions);
			}
			else
			{
				CategoryValidator.checkForNullRefernce(targetEntity
						.getAttributeByName(targetAttributeName), ApplicationProperties
						.getValue(CategoryConstants.LINE_NUMBER)
						+ categoryFileParser.getLineNumber()
						+ " "
						+ ApplicationProperties.getValue(CategoryConstants.ATTR)
						+ targetAttributeName
						+ " "
						+ ApplicationProperties.getValue(CategoryConstants.ATTR_NOT_PRESENT)
						+ targetEntity.getName());

				isAttributePresent = targetEntity.isAttributePresent(targetAttributeName);

				// If this is the parent attribute and currently the parent category
				// entity is not created
				// for given category entity, create parent category hierarchy up to
				// where attribute is found.
				if (!isAttributePresent)
				{
					final EntityInterface parentEntity = targetEntity.getParentEntity();
					final EntityInterface childEntity = targetEntity;
					final CategoryEntityInterface parentCategoryEntity = targetCategoryEntity
							.getParentCategoryEntity();

					targetCategoryEntity = processInheritance(parentEntity, childEntity,
							parentCategoryEntity, targetCategoryEntity, targetAttributeName,
							containerCollection);
					targetEntity = targetCategoryEntity.getEntity();

				}

				final CategoryAttributeInterface targetCategoryAttribute = categoryHelper
						.getCategoryAttribute(targetEntity, targetAttributeName,
								targetCategoryEntity);

				final ControlInterface targetControl = DynamicExtensionsUtility
						.getControlForAbstractAttribute(
								(AttributeMetadataInterface) targetCategoryAttribute,
								targetContainerInterface, targetCategoryEntityName);

				targetControl.setIsSkipLogicTargetControl(Boolean.TRUE);
				targetControl.setSourceSkipControl(sourceControl);

				final SkipLogicAttributeInterface skipLogicAttributeInterface = DomainObjectFactory
						.getInstance().createSkipLogicAttribute();
				skipLogicAttributeInterface.setSourceSkipLogicAttribute(sourceCategoryAttribute);
				skipLogicAttributeInterface.setTargetSkipLogicAttribute(targetCategoryAttribute);

				final String defaultValue = categoryFileParser.getDefaultValue();
				if (defaultValue == null)
				{
					// Validation-If category attribute is of type Read-only its default
					// value must be specified
					if (((targetControl.getIsReadOnly() != null) && targetControl.getIsReadOnly())
							|| ((targetControl.getIsCalculated() != null) && targetControl
									.getIsCalculated()))
					{
						throw new DynamicExtensionsSystemException(ApplicationProperties
								.getValue(CategoryConstants.CREATE_CAT_FAILS)
								+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
								+ categoryFileParser.getLineNumber()
								+ " "
								+ ApplicationProperties.getValue("mandatoryDValueForRO")
								+ targetAttributeName);
					}
				}
				else
				{
					skipLogicAttributeInterface
							.setDefaultValue(((AttributeMetadataInterface) targetCategoryAttribute)
									.getAttributeTypeInformation().getPermissibleValueForString(
											DynamicExtensionsUtility
													.getEscapedStringValue(defaultValue)));
				}
				//Setting control options
				final Map<String, String> controlOptions = categoryFileParser.getControlOptions();

				categoryHelper.setOptions(targetControl, controlOptions, categoryFileParser
						.getLineNumber());

				if (isEnumeratedSourceControl)
				{
					permissibleValueInterface
							.addDependentSkipLogicAttribute(skipLogicAttributeInterface);
				}
				else
				{
					sourceCategoryAttribute
							.addDependentSkipLogicAttribute(skipLogicAttributeInterface);
				}
				final Map<String, Collection<SemanticPropertyInterface>> permissibleValues = categoryFileParser
						.getPermissibleValues();

				final Map<String, String> permissibleValueOptions = categoryFileParser
						.getPermissibleValueOptions();

				final List<PermissibleValueInterface> permissibleValuesList = categoryHelper
						.createPermissibleValuesList(targetAttributeName, targetAttributeClassName,
								skipLogicAttributeInterface, categoryFileParser.getLineNumber(),
								permissibleValues);

				skipLogicAttributeInterface.clearDataElementCollection();
				if ((permissibleValuesList != null) && !permissibleValuesList.isEmpty())
				{
					final UserDefinedDEInterface userDefinedDE = DomainObjectFactory.getInstance()
							.createUserDefinedDE();
					for (final PermissibleValueInterface pv : permissibleValuesList)
					{
						userDefinedDE.addPermissibleValue(pv);
					}

					//add new permissible values
					skipLogicAttributeInterface.setDataElement(userDefinedDE);

					categoryHelper.setOptions(userDefinedDE, permissibleValueOptions,
							categoryFileParser.getLineNumber());
				}
			}
		}
	}

	private String getInstancePath(final String[] categoryPaths, final String className)
	{
		String instancePathInformation = "";
		for (final String instancePath : categoryPaths)
		{
			if (instancePath.contains(className))
			{
				instancePathInformation = instancePath;
				break;
			}
		}
		return instancePathInformation;
	}

	/**
	 *
	 * @param targetCategoryEntity
	 * @param targetContainerInterface
	 * @param sourceControl
	 * @param sourceCategoryAttribute
	 * @param permissibleValueInterface
	 * @throws DynamicExtensionsSystemException
	 */
	private void setSkipLogicForAllSubFormAttributes(
			final CategoryEntityInterface targetCategoryEntity,
			final ControlInterface sourceControl,
			final CategoryAttributeInterface sourceCategoryAttribute,
			final PermissibleValueInterface permissibleValueInterface,
			final boolean isEnumeratedSourceControl, final Map<String, String> controlOptions)
			throws DynamicExtensionsSystemException
	{
		final ContainerInterface targetContainerInterface = DynamicExtensionsUtility
				.getContainerForAbstractEntity(targetCategoryEntity);

		for (final CategoryAttributeInterface targetCategoryAttribute : targetCategoryEntity
				.getAllCategoryAttributes())
		{
			final ControlInterface targetControl = DynamicExtensionsUtility
					.getControlForAbstractAttribute(
							(AttributeMetadataInterface) targetCategoryAttribute,
							targetContainerInterface, targetCategoryEntity.getName());

			targetControl.setIsSkipLogicTargetControl(Boolean.TRUE);
			targetControl.setSourceSkipControl(sourceControl);
			final SkipLogicAttributeInterface skipLogicAttributeInterface = DomainObjectFactory
					.getInstance().createSkipLogicAttribute();
			skipLogicAttributeInterface.setSourceSkipLogicAttribute(sourceCategoryAttribute);
			skipLogicAttributeInterface.setTargetSkipLogicAttribute(targetCategoryAttribute);
			if (isEnumeratedSourceControl)
			{
				permissibleValueInterface
						.addDependentSkipLogicAttribute(skipLogicAttributeInterface);
			}
			else
			{
				sourceCategoryAttribute.addDependentSkipLogicAttribute(skipLogicAttributeInterface);
			}
			categoryHelper.setOptions(targetControl, controlOptions, categoryFileParser
					.getLineNumber());
		}
		for (final CategoryAssociationInterface targetCategoryAssociation : targetCategoryEntity
				.getCategoryAssociationCollection())
		{
			setSkipLogicForAllSubFormAttributes(
					targetCategoryAssociation.getTargetCategoryEntity(), sourceControl,
					sourceCategoryAttribute, permissibleValueInterface, isEnumeratedSourceControl,
					controlOptions);
		}
	}

	/**
	 *
	 * @param categoryAttribute
	 * @param defaultValue
	 * @throws DynamicExtensionsApplicationException
	 */
	private void setFormula(final CategoryAttributeInterface categoryAttribute,
			final String defaultValue) throws DynamicExtensionsApplicationException
	{
		boolean isValidFormula = false;
		final FormulaParser formulaParser = new FormulaParser();
		try
		{
			isValidFormula = formulaParser.validateExpression(defaultValue);
		}
		catch (final DynamicExtensionsSystemException ex)
		{
			throw new DynamicExtensionsApplicationException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
					+ categoryFileParser.getLineNumber()
					+ categoryAttribute.getAbstractAttribute().getName()
					+ ApplicationProperties.getValue("incorrectFormulaCalculatedAttribute")
					+ defaultValue, ex);
		}
		if (isValidFormula)
		{
			categoryAttribute.setIsCalculated(true);
			if (categoryAttribute.getFormula() == null)
			{
				categoryAttribute.setFormula(DomainObjectFactory.getInstance().createFormula(
						defaultValue));
			}
			else
			{
				categoryAttribute.getFormula().setExpression(defaultValue);
			}
		}
	}

	/**
	 * @param parentEntity
	 * @param childEntity
	 * @param parentCategoryEntity
	 * @param childCategoryEntity
	 * @param attributeName
	 * @param containerCollection
	 * @throws DynamicExtensionsSystemException
	 */
	private CategoryEntityInterface processInheritance(EntityInterface parentEntity,
			EntityInterface childEntity, CategoryEntityInterface parentCategoryEntity,
			CategoryEntityInterface childCategoryEntity, final String attributeName,
			final List<ContainerInterface> containerCollection)
			throws DynamicExtensionsSystemException
	{
		boolean isAttributeCategoryMatched = false;
		while (childEntity.getParentEntity() != null)
		{
			parentEntity = childEntity.getParentEntity();

			// Check whether the given cat.entity's parent category entity is
			// created.
			// If not created, then create it.
			if (parentCategoryEntity == null)
			{
				final ContainerInterface parentContainer = createParentCategoryEntity(
						childCategoryEntity, parentEntity, childEntity.getEntityGroup(),
						containerCollection);
				parentCategoryEntity = (CategoryEntityInterface) parentContainer
						.getAbstractEntity();

				final ContainerInterface childcontainerInterface = CategoryGenerationUtil
						.getContainerWithCategoryEntityName(containerCollection,
								childCategoryEntity.getName());
				childcontainerInterface.setBaseContainer(parentContainer);
			}

			// Iterate over parent entity's attribute, check whether its present
			// in parent entity.
			isAttributeCategoryMatched = isAttributePresentInParent(parentEntity, attributeName);

			childEntity = childEntity.getParentEntity();
			childCategoryEntity = parentCategoryEntity;
			parentCategoryEntity = parentCategoryEntity.getParentCategoryEntity();

			// If attribute found in parent category entity, break out of loop.
			if (isAttributeCategoryMatched)
			{
				break;
			}
			addContainerInfo(childCategoryEntity);
		}
		return childCategoryEntity;
	}

	/**
	 * This will check weather the Attribute with the name attributeName is present in the parentEntity
	 * or not.
	 * @param parentEntity Entity
	 * @param attributeName Attribute to search in the parentEntity
	 * @return true if attribute present else returns false.
	 */
	private boolean isAttributePresentInParent(EntityInterface parentEntity,
			final String attributeName)
	{
		boolean isAttributeCategoryMatched = false;
		final Iterator<AbstractAttributeInterface> parentattrIterator = parentEntity
				.getAbstractAttributeCollection().iterator();

		while (parentattrIterator.hasNext())
		{
			final AbstractAttributeInterface objParentAttribute = parentattrIterator.next();
			if (attributeName.equals(objParentAttribute.getName()))
			{
				isAttributeCategoryMatched = true;
				break;
			}
		}
		return isAttributeCategoryMatched;
	}

	/**
	 * Adds containers to containerCollection
	 * @param childCategoryEntity
	 */
	private void addContainerInfo(final CategoryEntityInterface childCategoryEntity)
	{
		boolean isContainerPresent = false;
		if (childCategoryEntity.getContainerCollection() != null)
		{
			final ContainerInterface container = (ContainerInterface) childCategoryEntity
					.getContainerCollection().iterator().next();
			for (final ContainerInterface containerInterface : containerCollection)
			{
				if (container.getAbstractEntity().getName().equals(
						containerInterface.getAbstractEntity().getName()))
				{
					isContainerPresent = true;
					break;
				}
			}
			if (!isContainerPresent)
			{
				containerCollection.add(container);
			}
		}
	}

	/**
	 * @param categoryEntityNameList
	 * @param entityName
	 * @return
	 */
	private String getCategoryEntityName(final List<String> categoryEntityNameList,
			final String entityName)
	{
		String categoryEntityName = null;
		for (final String categoryEntName : categoryEntityNameList)
		{
			final String catEntityName = DynamicExtensionsUtility
					.getCategoryEntityName(categoryEntName);
			if (entityName.equals(catEntityName.substring(0, catEntityName.indexOf('['))))
			{
				categoryEntityName = categoryEntName;
				break;
			}
		}
		return categoryEntityName;
	}

	/**
	 * @param entityInterface
	 * @param categoryEntityName
	 * @param displayLable
	 * @param showCaption
	 * @param containerCollection
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private ContainerInterface createCategoryEntityAndContainer(
			final EntityInterface entityInterface, final String categoryEntityName,
			final String displayLable, final Boolean showCaption,
			final Collection<ContainerInterface> containerCollection,
			final CategoryInterface category) throws DynamicExtensionsSystemException
	{
		ContainerInterface containerInterface = null;
		final CategoryHelperInterface categoryHelper = new CategoryHelper(categoryFileParser);

		containerInterface = categoryHelper.createOrUpdateCategoryEntityAndContainer(
				entityInterface, displayLable, category, categoryEntityName);

		containerInterface.setAddCaption(showCaption);

		containerCollection.add(containerInterface);

		return containerInterface;
	}

	/**
	 * @param entityGroup
	 * @param containerCollection
	 * @param associationNamesMap
	 * @param category
	 * @param categoryEntityNameInstanceMap
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private List<String> processDisplayLabel() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{

		Boolean showCaption = categoryFileParser.isShowCaption();
		ContainerInterface mainContainer = null;
		final String displayLable = categoryFileParser.getDisplyLable();
		final List<String> categoryEntityName = new ArrayList<String>();
		String categoryEntName = null;
		try
		{
			categoryFileParser.readNext();
			List<String> categoryPaths = CategoryGenerationUtil
					.getCategoryEntityPath(categoryFileParser.getCategoryPaths());
			//categoryValidator.validateContainersUnderSameDisplayLabel(categoryPaths, showCaption);
			ContainerInterface temp = null;
			for (String categoryPath : categoryPaths)
			{
				categoryEntName = CategoryGenerationUtil.getCategoryEntityName(categoryPath,
						entityNameAssociationMap);
				temp = createForm(displayLable, categoryPath, categoryEntityName, categoryEntName,
						categoryPaths, showCaption);
				categoryHelper.removeAllSeprators(temp);
				if (mainContainer == null)
				{
					mainContainer = temp;
				}
				else
				{
					final List<AssociationInterface> associationNameList = entityNameAssociationMap
							.get(CategoryGenerationUtil
									.getEntityNameForAssociationMap(categoryEntityNameInstanceMap
											.get(categoryEntName)));

					new CategoryHelper(categoryFileParser).addChildContainers(category,
							entityGroup, mainContainer, temp, associationNameList, 1,
							categoryEntityNameInstanceMap.get(temp.getAbstractEntity().getName()));
				}

				showCaption = false;
			}
		}
		catch (final IOException exception)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
					+ categoryFileParser.getLineNumber()
					+ ApplicationProperties.getValue("errorReadingCategoryEntityPath"), exception);
		}

		return categoryEntityName;
	}

	private ContainerInterface createForm(String displayLable, String categoryPath,
			List<String> categoryEntityNameList, String categoryEntityName,
			List<String> categoryPaths, boolean showCaption)
			throws DynamicExtensionsSystemException
	{
		String entityName = null;
		String[] categoryEntitiesInPath;

		categoryEntitiesInPath = categoryPath.split("->");
		final String newCategoryEntityName = categoryEntitiesInPath[categoryEntitiesInPath.length - 1];
		entityName = CategoryGenerationUtil.getEntityName(newCategoryEntityName);

		// Check if instance information is wrong, i.e. entity mentioned
		// in
		// the instance information exists in the entity group.
		for (int i = 0; i < categoryEntitiesInPath.length; i++)
		{
			final String entName = CategoryGenerationUtil
					.getEntityNameExcludingAssociationRoleName(categoryEntitiesInPath[i].substring(
							0, categoryEntitiesInPath[i].indexOf('[')));

			if (i + 1 <= categoryEntitiesInPath.length - 1)
			{
				try
				{
					final String prevInstance = categoryEntitiesInPath[i].substring(
							categoryEntitiesInPath[i].indexOf('[') + 1, categoryEntitiesInPath[i]
									.indexOf(']'));
					final String nextInstance = categoryEntitiesInPath[i + 1].substring(
							categoryEntitiesInPath[i + 1].indexOf('[') + 1,
							categoryEntitiesInPath[i + 1].indexOf(']'));

					final Integer prevInstanceNo = Integer.valueOf(prevInstance);
					final Integer nextInstanceNo = Integer.valueOf(nextInstance);

					if (prevInstanceNo.compareTo(nextInstanceNo) > 0)
					{
						handleInstanceException(categoryEntitiesInPath[i],
								categoryEntitiesInPath[i + 1]);
					}
				}
				catch (final NumberFormatException eNumberFormatException)
				{
					handleInstanceException(categoryEntitiesInPath[i],
							categoryEntitiesInPath[i + 1]);
				}
				catch (final StringIndexOutOfBoundsException indexOutOfBoundsException)
				{
					handleInstanceException(categoryEntitiesInPath[i],
							categoryEntitiesInPath[i + 1]);
				}
			}
			if (entityGroup.getEntityByName(entName) == null)
			{
				throw new DynamicExtensionsSystemException(ApplicationProperties
						.getValue(CategoryConstants.CREATE_CAT_FAILS)
						+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
						+ categoryFileParser.getLineNumber()
						+ " "
						+ ApplicationProperties.getValue(CategoryConstants.WRONG_INST_INFO)
						+ entName);
			}
		}
		ContainerInterface container = null;
		if (!categoryEntityNameList.contains(categoryEntityName))
		{

			container = searchExistingCategoryEntityAndContainer(categoryEntityName,
					containerCollection);
			if (container == null)
			{
				container = createCategoryEntityAndContainer(entityGroup
						.getEntityByName(entityName), categoryEntityName, displayLable,
						showCaption, containerCollection, category);
			}

			categoryEntityNameInstanceMap.put(container.getAbstractEntity().getName(),
					getCategoryPath(categoryPaths, newCategoryEntityName));

			categoryEntityNameList.add(categoryEntityName);

		}
		return container;
	}

	/**
	 * handleInstanceException.
	 *
	 * @param sourceInstance
	 * @param targetInstance
	 * @throws DynamicExtensionsSystemException
	 */
	private void handleInstanceException(final String sourceInstance, final String targetInstance)
			throws DynamicExtensionsSystemException
	{
		throw new DynamicExtensionsSystemException(ApplicationProperties
				.getValue(CategoryConstants.CREATE_CAT_FAILS)
				+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
				+ categoryFileParser.getLineNumber()
				+ " "
				+ ApplicationProperties.getValue(CategoryConstants.INCORRECT_INST_INFO)
				+ sourceInstance + "->" + targetInstance);
	}

	/**
	 * @param categoryEntityName
	 * @param containerCollection
	 * @return
	 */
	private ContainerInterface searchExistingCategoryEntityAndContainer(
			final String categoryEntityName, final List<ContainerInterface> containerCollection)
	{
		// Check whether the container is already created for category entity
		// and return it if it exists.
		ContainerInterface existingContainer = null;
		final Iterator<ContainerInterface> containerIterator = containerCollection.iterator();
		while (containerIterator.hasNext())
		{
			final ContainerInterface container = containerIterator.next();
			if (container.getAbstractEntity().getName().equals(categoryEntityName))
			{
				existingContainer = container;
				break;
			}
		}

		return existingContainer;
	}

	/**
	 * @param categoryPaths
	 * @param newCategoryEntityName
	 * @return
	 */
	private String getCategoryPath(List<String> categoryPaths, String newCategoryEntityName)
	{
		String categoryPath = null;
		for (final String string : categoryPaths)
		{
			if (string.endsWith("->" + newCategoryEntityName))
			{
				categoryPath = string;
				break;
			}
		}
		return categoryPath;
	}

	/**
	 * @param control
	 * @throws ParseException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void setDefaultValue(final ControlInterface control) throws ParseException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		final String defaultValue = categoryFileParser.getDefaultValue();
		final String attributeName = ((CategoryAttributeInterface) control
				.getAttibuteMetadataInterface()).getAbstractAttribute().getName();
		if (defaultValue == null)
		{
			// Validation-If category attribute is of type Read-only its default
			// value must be specified
			if (((control.getIsReadOnly() != null) && control.getIsReadOnly())
					|| ((control.getIsCalculated() != null) && control.getIsCalculated()))
			{
				throw new DynamicExtensionsSystemException(ApplicationProperties
						.getValue(CategoryConstants.CREATE_CAT_FAILS)
						+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
						+ categoryFileParser.getLineNumber()
						+ " "
						+ ApplicationProperties.getValue("mandatoryDValueForRO") + attributeName);
			}
			return;
		}

		final CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface) control
				.getAttibuteMetadataInterface();
		if (!defaultValue.equals(categoryAttribute.getDefaultValue()))
		{
			final AttributeInterface attributeInterface = categoryAttribute.getAbstractAttribute()
					.getEntity().getAttributeByName(
							categoryAttribute.getAbstractAttribute().getName());
			if ((control.getIsCalculated() != null) && control.getIsCalculated())
			{
				if (control instanceof TextField)
				{
					if ((((AttributeMetadataInterface) categoryAttribute)
							.getAttributeTypeInformation() instanceof NumericAttributeTypeInformation)
							|| (((AttributeMetadataInterface) categoryAttribute)
									.getAttributeTypeInformation() instanceof DateAttributeTypeInformation))
					{
						setFormula(categoryAttribute, defaultValue);
					}
					else
					{
						throw new DynamicExtensionsSystemException(ApplicationProperties
								.getValue(CategoryConstants.CREATE_CAT_FAILS)
								+ " "
								+ ApplicationProperties
										.getValue("incorrectDataTypeCalculatedAttribute")
								+ attributeName);
					}
				}
				else
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
							+ categoryFileParser.getLineNumber()
							+ " "
							+ ApplicationProperties
									.getValue("incorrectControlTypeCalculatedAttribute")
							+ attributeName);
				}
			}
			else
			{
				categoryAttribute.setDefaultValue(attributeInterface.getAttributeTypeInformation()
						.getPermissibleValueForString(
								DynamicExtensionsUtility.getEscapedStringValue(defaultValue)));
			}
		}
	}

	/**
	 * This method populate the main form list for the given entity group
	 *
	 * @param entityGroup
	 */
	private void populateMainFormList(final EntityGroupInterface entityGroup)
	{
		for (final ContainerInterface containerInterface : entityGroup.getMainContainerCollection())
		{
			mainFormList.add(containerInterface.getAbstractEntity().getName());
		}
	}

}