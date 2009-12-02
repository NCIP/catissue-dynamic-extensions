
package edu.common.dynamicextensions.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domain.CategoryAssociation;
import edu.common.dynamicextensions.domain.CategoryAttribute;
import edu.common.dynamicextensions.domain.CategoryEntity;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.PathAssociationRelationInterface;
import edu.common.dynamicextensions.domain.PermissibleValue;
import edu.common.dynamicextensions.domain.UserDefinedDE;
import edu.common.dynamicextensions.domain.userinterface.ComboBox;
import edu.common.dynamicextensions.domain.userinterface.Container;
import edu.common.dynamicextensions.domain.userinterface.Label;
import edu.common.dynamicextensions.domain.userinterface.ListBox;
import edu.common.dynamicextensions.domain.userinterface.MultiSelectCheckBox;
import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.DataElementInterface;
import edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.FormControlNotesInterface;
import edu.common.dynamicextensions.domaininterface.PathInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.SkipLogicAttributeInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.CategoryAssociationControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.CheckBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.DatePickerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.FileUploadInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.MultiSelectCheckBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.RadioButtonInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.SelectInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextAreaInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextFieldInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleParameterInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.global.CategoryConstants;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.parser.CategoryCSVConstants;
import edu.common.dynamicextensions.util.parser.CategoryFileParser;
import edu.common.dynamicextensions.validation.ValidatorUtil;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.metadata.util.DyExtnObjectCloner;

/**
 * @author kunal_kamble
 * @author mandar_shidhore
 *
 */
public class CategoryHelper implements CategoryHelperInterface
{

	CategoryManagerInterface categoryManager = CategoryManager.getInstance();
	private CategoryFileParser categoryFileParser;
	private static final String CATEGORY_ATTRIBUTE_NAME_POSTFIX = "Category Attribute";

	/**
	 * Default constructor
	 */
	public CategoryHelper()
	{
		// TODO Auto-generated constructor

	}

	/**
	 * @param categoryFileParser
	 */
	public CategoryHelper(CategoryFileParser categoryFileParser)
	{
		super();
		this.categoryFileParser = categoryFileParser;
	}

	/**
	 * This method will retrieve the category from the cache with the given name.
	 * If the category is find then it will check weather it is already in use or not.
	 * If it is already in use it will throw exception.
	 * else will add the category to inUse set & return the category for furthure operations.
	 * After completing the operation user has to release the lock on category explicitly.
	 * @param name name of the category to retrieve.
	 * @return category with given name from cache else new category.
	 */
	public CategoryInterface getCategory(String name) throws DynamicExtensionsSystemException
	{
		CategoryInterface category = getCategoryFromCache(name);
		if (category == null)
		{
			category = DomainObjectFactory.getInstance().createCategory();
			category.setName(name);
		}
		else
		{
			synchronized (category)
			{
				if(EntityCache.getInstance().isCategoryInUse(category))
				{
					throw new DynamicExtensionsSystemException("Category In Use Please Try Agian after some time");
				}
				else
				{
					EntityCache.getInstance().markCategoryInUse(category);
					DyExtnObjectCloner cloner = new DyExtnObjectCloner();
					CategoryInterface clonedCategory = cloner.clone(category);
					category = clonedCategory;
				}
			}
		}

		return category;
	}

	/**
	 * This method will search for the categort with given name in Cache.
	 * @param name name of the category
	 * @return category with given name if found else will return null.
	 */
	private CategoryInterface getCategoryFromCache(String name)
	{
		CategoryInterface category=null;
		for(CategoryInterface cat : EntityCache.getInstance().getAllCategories())
		{
			if (name.equals(cat.getName()))
			{
				category = cat;
				break;
			}

		}
		return category;
	}

	/**
	 * This method will release the lock on the category so that other users can use it
	 * for furthure.
	 * @param category category on which the lock is released.
	 */
	public void releaseLockOnCategory(CategoryInterface category)
	{
		if(category!=null && category.getId()!=null)
		{
			synchronized (category)
			{
				EntityCache.getInstance().releaseCategoryFromUse(category);
			}
		}
	}
	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.categoryManager.CategoryHelperInterface#saveCategory(edu.common.dynamicextensions.domaininterface.CategoryInterface)
	 */
	public void saveCategory(CategoryInterface category) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		try
		{
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			categoryManager.persistCategory(category);
		}
		catch (DynamicExtensionsSystemException e)
		{
			throw new DynamicExtensionsSystemException("ERROR WHILE SAVING A CATEGORY", e);
		}
		catch (DynamicExtensionsApplicationException e)
		{
			throw new DynamicExtensionsApplicationException("ERROR WHILE SAVING A CATEGORY", e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.util.CategoryHelperInterface#saveCategoryMetadata(edu.common.dynamicextensions.domaininterface.CategoryInterface)
	 */
	public void saveCategoryMetadata(CategoryInterface category)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		try
		{
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			categoryManager.persistCategoryMetadata(category);
		}
		catch (DynamicExtensionsSystemException e)
		{
			throw new DynamicExtensionsSystemException("ERROR WHILE SAVING A CATEGORY", e);
		}
		catch (DynamicExtensionsApplicationException e)
		{

			throw new DynamicExtensionsApplicationException("ERROR WHILE SAVING A CATEGORY", e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.categoryManager.CategoryHelperInterface#createCategoryEntity(java.lang.String, edu.common.dynamicextensions.domaininterface.CategoryInterface[])
	 */
	public ContainerInterface createOrUpdateCategoryEntityAndContainer(EntityInterface entity,
			String containerCaptionValue, CategoryInterface category, String... categoryEntityName)
	{
		String newCategoryEntityName = (categoryEntityName.length > 0
				? categoryEntityName[0]
				: null);
		CategoryEntityInterface categoryEntity = createOrUpdateCategoryEntity(category, entity,
				newCategoryEntityName);
		String containerCaption = containerCaptionValue;
		if (containerCaption == null)
		{
			containerCaption = entity.getName() + "_category_entity_container";

		}
		ContainerInterface container = createContainer(categoryEntity, containerCaption);

		return container;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.util.CategoryHelperInterface#createOrUpdateCategoryEntity(edu.common.dynamicextensions.domaininterface.CategoryInterface, edu.common.dynamicextensions.domaininterface.EntityInterface, java.lang.String)
	 */
	public CategoryEntityInterface createOrUpdateCategoryEntity(CategoryInterface category,
			EntityInterface entity, String categoryEntityName)
	{
		CategoryEntityInterface categoryEntity = null;
		if (categoryEntityName != null)
		{
			categoryEntity = category.getCategoryEntityByName(categoryEntityName);
		}
		if (categoryEntity == null)
		{
			categoryEntity = DomainObjectFactory.getInstance().createCategoryEntity();
			categoryEntity.setName(categoryEntityName);
			categoryEntity.setEntity(entity);
		}

		return categoryEntity;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.categoryManager.CategoryHelperInterface#setRootCategoryEntity(edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface, edu.common.dynamicextensions.domaininterface.CategoryInterface)
	 */
	public void setRootCategoryEntity(ContainerInterface container, CategoryInterface category)
	{
		category.setRootCategoryElement((CategoryEntityInterface) container.getAbstractEntity());
		((CategoryEntityInterface) container.getAbstractEntity()).setCategory(category);
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.categoryManager.CategoryHelperInterface#addControl(edu.common.dynamicextensions.domaininterface.AttributeInterface, edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface, edu.common.dynamicextensions.categoryManager.CategoryHelperInterface.ControlEnum, java.util.List<edu.common.dynamicextensions.domaininterface.PermissibleValueInterface>[])
	 */
	public ControlInterface addOrUpdateControl(EntityInterface entity, String attributeName,
			ContainerInterface container, ControlEnum controlType, String controlCaption,
			String heading, List<FormControlNotesInterface> controlNotes,
			Map<String, Object> rulesMap, Map<String, String> permValueOptions, long lineNumber,
			Map<String, Collection<SemanticPropertyInterface>>... permissibleValueList)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		if (controlType == null)
		{
			throw new DynamicExtensionsSystemException("INVALID CONTROL TYPE FOR : "
					+ controlCaption);
		}

		CategoryAttributeInterface categoryAttribute = createOrupdateCategoryAttribute(entity,
				attributeName, container);

		//removing all dependent skip Logic Attributes.
		categoryAttribute.removeAllDependentSkipLogicAttributes();

		AttributeInterface attribute = entity.getAttributeByName(attributeName);

		applyRulesInCSVFile(categoryAttribute, attribute, rulesMap);

		ControlInterface control = null;
		Map<String, Collection<SemanticPropertyInterface>> permissibleValueNameList = (permissibleValueList.length == 0
				? null
				: permissibleValueList[0]);
		control = createOrUpdateControl(controlType, controlCaption, heading, controlNotes,
				container, categoryAttribute, permValueOptions, lineNumber,
				permissibleValueNameList);
		return control;
	}

	/**
	 * This method applies the explicit rules mentioned in CSV file to a category attribute.
	 * @param categoryAttribute
	 * @param attribute
	 * @param rulesMap
	 * @throws DynamicExtensionsSystemException
	 */
	private void applyRulesInCSVFile(CategoryAttributeInterface categoryAttribute,
			AttributeInterface attribute, Map<String, Object> rulesMap)
			throws DynamicExtensionsSystemException
	{
		Set<RuleInterface> rules = new HashSet<RuleInterface>();

		addImplicitRules(rules, attribute.getRuleCollection());
		addExplicitRules(rules, rulesMap);
		ValidatorUtil.removeConflictingRules(rules);
		categoryAttribute.setRuleCollection(rules);
	}

	/**
	 * Add any implicit rules of attribute to a category attribute.
	 * @param implicitRules
	 * @param rules
	 * @param attributeRules
	 */
	private void addImplicitRules(Set<RuleInterface> rules, Collection<RuleInterface> attributeRules)
	{
		if (attributeRules != null)
		{
			for (RuleInterface rule : attributeRules)
			{
				if ((rule.getIsImplicitRule() != null)
						&& (rule.getIsImplicitRule() || rule.getName().equals(
								CategoryCSVConstants.REQUIRED)))
				{
					rules.add(rule);
				}
			}
		}
	}

	/**
	 * @param rules
	 * @param rulesMap
	 */
	private void addExplicitRules(Set<RuleInterface> rules, Map<String, Object> rulesMap)
	{
		if ((rulesMap != null) && !rulesMap.isEmpty())
		{
			Iterator<String> rulesIterator = rulesMap.keySet().iterator();

			while (rulesIterator.hasNext())
			{
				Object obj = rulesIterator.next();
				RuleInterface rule = null;
				DomainObjectFactory factory = DomainObjectFactory.getInstance();
				String ruleName = obj.toString();

				if (ruleName.equalsIgnoreCase(CategoryCSVConstants.UNIQUE)
						|| ruleName.equalsIgnoreCase(CategoryCSVConstants.REQUIRED))
				{
					rule = factory.createRule();
					rule.setName(ruleName);
					rule.setIsImplicitRule(false);
				}
				else if (ruleName.equalsIgnoreCase(CategoryCSVConstants.RANGE)
						|| ruleName.equalsIgnoreCase(CategoryCSVConstants.DATE_RANGE))
				{
					Map<String, Object> valuesMap = (Map<String, Object>) rulesMap.get(obj);

					rule = factory.createRule();
					rule.setName(ruleName);
					rule.setIsImplicitRule(false);

					RuleParameterInterface minValue = factory.createRuleParameter();
					minValue.setName(CategoryCSVConstants.MIN);
					minValue.setValue((String) valuesMap.get(CategoryCSVConstants.MIN));

					RuleParameterInterface maxValue = factory.createRuleParameter();
					maxValue.setName(CategoryCSVConstants.MAX);
					maxValue.setValue((String) valuesMap.get(CategoryCSVConstants.MAX));

					rule.getRuleParameterCollection().add(minValue);
					rule.getRuleParameterCollection().add(maxValue);
				}
				else if (ruleName.equalsIgnoreCase(CategoryCSVConstants.ALLOW_FUTURE_DATE))
				{
					rule = factory.createRule();
					rule.setName(ruleName);
					rule.setIsImplicitRule(false);
				}

				if (rule != null)
				{
					rules.add(rule);
				}
			}
		}
	}

	/**
	 * @param entity
	 * @param attributeName
	 * @param container
	 * @return
	 */
	public CategoryAttributeInterface createOrupdateCategoryAttribute(EntityInterface entity,
			String attributeName, ContainerInterface container)
	{
		CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface) getCategoryAttribute(
				attributeName, container);
		if (categoryAttribute == null)
		{
			CategoryEntity categoryEntity = (CategoryEntity) container.getAbstractEntity();
			categoryAttribute = createCategoryAttribute(entity, attributeName, categoryEntity);
		}

		return categoryAttribute;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.util.CategoryHelperInterface#createCategoryAttribute(edu.common.dynamicextensions.domaininterface.EntityInterface, java.lang.String, edu.common.dynamicextensions.domaininterface.CategoryEntityInterface)
	 */
	public CategoryAttributeInterface createCategoryAttribute(EntityInterface entity,
			String attributeName, CategoryEntityInterface categoryEntity)
	{
		CategoryAttributeInterface categoryAttribute = DomainObjectFactory.getInstance()
				.createCategoryAttribute();
		categoryAttribute.setName(attributeName + " Category Attribute");
		categoryAttribute.setAbstractAttribute(entity.getAbstractAttributeByName(attributeName));

		categoryEntity.addCategoryAttribute(categoryAttribute);
		categoryAttribute.setCategoryEntity(categoryEntity);

		categoryEntity.addCategoryAttribute(categoryAttribute);
		categoryAttribute.setCategoryEntity(categoryEntity);

		return categoryAttribute;
	}

	/**
	 * @param entity
	 * @param attributeName
	 * @param categoryEntity
	 * @return
	 */
	public CategoryAttributeInterface getCategoryAttribute(EntityInterface entity,
			String attributeName, CategoryEntityInterface categoryEntity)
	{
		CategoryAttributeInterface categoryAttribute = categoryEntity
				.getAttributeByName(attributeName + " " + CATEGORY_ATTRIBUTE_NAME_POSTFIX);
		if (categoryAttribute == null)
		{
			categoryAttribute = createCategoryAttribute(entity, attributeName, categoryEntity);
		}
		return categoryAttribute;
	}

	/**
	 * @param controlType
	 * @param controlCaption
	 * @param container
	 * @param categoryAttribute
	 * @param permValueNames
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	private ControlInterface createOrUpdateControl(ControlEnum controlType, String controlCaption,
			String heading, List<FormControlNotesInterface> controlNotes,
			ContainerInterface container, CategoryAttributeInterface categoryAttribute,
			Map<String, String> permValueOptions, long lineNumber,
			Map<String, Collection<SemanticPropertyInterface>> permissibleValueNameList)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		ControlInterface control = null;
		EntityInterface entity = categoryAttribute.getAbstractAttribute().getEntity();
		String attributeName = categoryAttribute.getAbstractAttribute().getName();
		switch (controlType)
		{
			case TEXT_FIELD_CONTROL :
				control = createOrUpdateTextFieldControl(container, categoryAttribute);
				break;
			case LIST_BOX_CONTROL :
				control = createOrUpdateSelectControl(container, categoryAttribute,
						createPermissibleValuesList(entity, attributeName, lineNumber,
								permissibleValueNameList), controlType, permValueOptions,
						lineNumber);
				break;
			case COMBO_BOX_CONTROL :
				control = createOrUpdateSelectControl(container, categoryAttribute,
						createPermissibleValuesList(entity, attributeName, lineNumber,
								permissibleValueNameList), controlType, permValueOptions,
						lineNumber);
				break;
			case DATE_PICKER_CONTROL :
				control = createOrUpdateDatePickerControl(container, categoryAttribute);
				break;
			case FILE_UPLOAD_CONTROL :
				control = createOrUpdateFileUploadControl(container, categoryAttribute);
				break;
			case TEXT_AREA_CONTROL :
				control = createOrUpdateTextAreaControl(container, categoryAttribute);
				break;
			case RADIO_BUTTON_CONTROL :
				control = createOrUpdateRadioButtonControl(container, categoryAttribute,
						createPermissibleValuesList(entity, attributeName, lineNumber,
								permissibleValueNameList), permValueOptions, lineNumber);
				break;
			case CHECK_BOX_CONTROL :
				control = createOrUpdateCheckBoxControl(container, categoryAttribute);
				break;
			case LABEL_CONTROL :
				control = control;
				break;
			case MULTISELECT_CHECKBOX_CONTROL :
				control = createOrUpdateSelectControl(container, categoryAttribute,
						createPermissibleValuesList(entity, attributeName, lineNumber,
								permissibleValueNameList), controlType, permValueOptions,
						lineNumber);
				break;
			default :
				throw new DynamicExtensionsSystemException("ERROR: INCORRECT CONTROL TYPE");
		}
		updateContainerAndControl(container, control, categoryAttribute, controlCaption);

		if (heading.length() != 0)
		{
			control.setHeading(heading);
		}
		if (!controlNotes.isEmpty())
		{
			control.setFormNotes(controlNotes);
		}

		return control;
	}

	/**
	 * @param attributeName
	 * @param container
	 * @return
	 */
	private BaseAbstractAttributeInterface getCategoryAttribute(String attributeName,
			ContainerInterface container)
	{
		BaseAbstractAttributeInterface categoryAttribute = null;
		for (ControlInterface control : container.getControlCollection())
		{
			if (control.getBaseAbstractAttribute() == null)
			{
				continue;
			}
			if (!(control.getBaseAbstractAttribute() instanceof CategoryAssociation)
					&& (((CategoryAttributeInterface) control.getBaseAbstractAttribute())
							.getAbstractAttribute().getName().equals(attributeName)))
			{
				categoryAttribute = control.getBaseAbstractAttribute();
				break;
			}
			else if (!(control.getBaseAbstractAttribute() instanceof CategoryAssociation)
					&& ((CategoryAttributeInterface) control.getBaseAbstractAttribute())
							.getAbstractAttribute().getName().startsWith(DEConstants.DEPRECATED))
			{
				categoryAttribute = control.getBaseAbstractAttribute();
				((CategoryAttributeInterface) categoryAttribute)
						.setAbstractAttribute(((CategoryEntityInterface) container
								.getAbstractEntity()).getEntity().getAbstractAttributeByName(
								attributeName));
				break;
			}
		}

		return categoryAttribute;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.categoryManager.CategoryHelperInterface#setParentCategoryEntity(edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface, edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface)
	 */
	public void setParentContainer(ContainerInterface parentContainer,
			ContainerInterface childContainer)
	{
		CategoryEntityInterface parentCategoryEntity = null;
		CategoryEntityInterface childCategoryEntity = null;
		if (parentContainer != null)
		{
			parentCategoryEntity = (CategoryEntity) parentContainer.getAbstractEntity();
			if (childContainer != null)
			{
				childCategoryEntity = (CategoryEntity) childContainer.getAbstractEntity();
			}
		}
		if (childCategoryEntity != null)
		{
			childCategoryEntity.setParentCategoryEntity(parentCategoryEntity);
		}
		if (childContainer != null)
		{
			childContainer.setBaseContainer(parentContainer);
		}
	}

	/* (non-Javadoc)
	 * @see edu.wustl.catissuecore.test.CategoryHelperInterface#associateCategoryContainers(edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface, edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface, java.util.List, int)
	 */
	public CategoryAssociationControlInterface associateCategoryContainers(
			CategoryInterface category, EntityGroupInterface entityGroup,
			ContainerInterface sourceContainer, ContainerInterface targetContainer,
			List<AssociationInterface> associationList, int noOfEntries, String instance)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		CategoryAssociationControlInterface associationControl = null;
		CategoryAssociationInterface oldAssociation = null;

		ContainerInterface rootContainer = null;

		if (category.getRootCategoryElement() != null)
		{
			rootContainer = new ArrayList<ContainerInterface>(category.getRootCategoryElement()
					.getContainerCollection()).get(0);
		}

		associationControl = (CategoryAssociationControlInterface) getAssociationControl(
				rootContainer, targetContainer.getId());

		CategoryEntityInterface sourceCategoryEntity = (CategoryEntityInterface) sourceContainer
				.getAbstractEntity();
		CategoryEntityInterface targetCategoryEntity = (CategoryEntityInterface) targetContainer
				.getAbstractEntity();
		if (associationControl != null)
		{
			if (associationControl.getParentContainer().equals(sourceContainer))
			{
				return associationControl;
			}
			else
			{
				removeControl(associationControl.getParentContainer(), associationControl);
				oldAssociation = (CategoryAssociationInterface) associationControl
						.getBaseAbstractAttribute();
				removeCategoryAssociation(oldAssociation);
				associationControl.setBaseAbstractAttribute(null);

			}
		}

		CategoryAssociationInterface categoryAssociation = associateCategoryEntities(
				sourceCategoryEntity, targetCategoryEntity, sourceCategoryEntity.getName() + " to "
						+ targetCategoryEntity.getName() + " category association", noOfEntries,
				entityGroup, associationList, instance);

		CategoryAssociationControlInterface categoryAssociationControl = createCategoryAssociationControl(
				sourceContainer, targetContainer, categoryAssociation, targetContainer.getCaption());
		categoryAssociationControl.setCaption(targetContainer.getCaption());
		return categoryAssociationControl;
	}

	/**
	 * @param path
	 * @param associationList
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void updatePath(PathInterface path, List<AssociationInterface> associationList)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		//clear old path
		path.setPathAssociationRelationCollection(null);

		int pathSequenceNumber = 1;

		for (AssociationInterface association : associationList)
		{
			PathAssociationRelationInterface pathAssociationRelation = factory
					.createPathAssociationRelation();
			pathAssociationRelation.setPathSequenceNumber(pathSequenceNumber++);
			pathAssociationRelation.setAssociation(association);

			pathAssociationRelation.setPath(path);
			path.addPathAssociationRelation(pathAssociationRelation);
		}
	}

	/**
	 * @param path
	 * @param instance
	 * @throws DynamicExtensionsSystemException
	 */
	public void addInstanceInformationToPath(PathInterface path, String instance)
			throws DynamicExtensionsSystemException
	{
		String[] entityArray = instance.split("->");

		int counter = 0;
		if (path.getPathAssociationRelationCollection() != null)
		{
			for (PathAssociationRelationInterface associationRelation : path
					.getSortedPathAssociationRelationCollection())
			{
				String sourceEntity = entityArray[counter];
				String targetEntity = entityArray[counter + 1];
				if ((sourceEntity.indexOf('[') == -1) || (sourceEntity.indexOf(']') == -1))
				{
					throw new DynamicExtensionsSystemException(
							"ERROR: INSTANCE INFORMATION IS NOT IN THE CORRECT FORMAT " + instance);

				}
				associationRelation.setSourceInstanceId(getInsatnce(sourceEntity));
				associationRelation.setTargetInstanceId(getInsatnce(targetEntity));
				counter++;
			}
		}
	}

	/**
	 * Separates the instance information form the string of the format entityName[instance]
	 * @param categoryEntityName
	 * @return
	 */
	public Long getInsatnce(String categoryEntityName)
	{
		return Long.parseLong(categoryEntityName.substring(categoryEntityName.indexOf('[') + 1,
				categoryEntityName.indexOf(']')));
	}

	/* (non-Javadoc)
	 * @see edu.wustl.catissuecore.test.CategoryHelperInterface#getNextSequenceNumber(edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface)
	 */
	public int getNextSequenceNumber(ContainerInterface container)
	{
		return DynamicExtensionsUtility.getSequenceNumberForNextControl(container);
	}

	/**
	 * @param sourceCategoryEntity source category entity
	 * @param targetCategoryEntity target category entity
	 * @param path path information between the category entities
	 */
	private PathInterface addPathBetweenCategoryEntities(
			CategoryEntityInterface sourceCategoryEntity,
			CategoryEntityInterface targetCategoryEntity)
	{
		PathInterface path = DomainObjectFactory.getInstance().createPath();
		targetCategoryEntity.setPath(path);
		targetCategoryEntity.setTreeParentCategoryEntity(sourceCategoryEntity);

		return path;
	}

	/**
	 * Method associates the source and the target category entity
	 * @param sourceCategoryEntity source category entity
	 * @param targetCategoryEntity target category entity
	 * @param name name of the category association
	 * @return CategoryAssociationInterface category association object
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	public CategoryAssociationInterface associateCategoryEntities(
			CategoryEntityInterface sourceCategoryEntity,
			CategoryEntityInterface targetCategoryEntity, String name, int numberOfentries,
			EntityGroupInterface entityGroup, List<AssociationInterface> associationList,
			String instance) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{

		PathInterface path = addPathBetweenCategoryEntities(sourceCategoryEntity,
				targetCategoryEntity);
		updatePath(path, associationList);
		targetCategoryEntity.setNumberOfEntries(numberOfentries);

		CategoryAssociationInterface categoryAssociation = DomainObjectFactory.getInstance()
				.createCategoryAssociation();
		categoryAssociation.setName(name);

		sourceCategoryEntity.addChildCategory(targetCategoryEntity);

		categoryAssociation.setCategoryEntity(sourceCategoryEntity);
		categoryAssociation.setTargetCategoryEntity(targetCategoryEntity);

		sourceCategoryEntity.getCategoryAssociationCollection().add(categoryAssociation);

		addInstanceInformationToPath(path, instance);

		return categoryAssociation;
	}

	/**
	 * Method creates the association between the given parent and the target container
	 * @param parentContainer main form
	 * @param targetContainer sub form
	 * @param categoryAssociation association between category entities
	 * @param caption name to be displayed on UI
	 * @return CategoryAssociationControlInterface category association control object
	 */
	private CategoryAssociationControlInterface createCategoryAssociationControl(
			ContainerInterface parentContainer, ContainerInterface targetContainer,
			CategoryAssociationInterface categoryAssociation, String caption)
	{

		CategoryAssociationControlInterface categoryAssociationControl = DomainObjectFactory
				.getInstance().createCategoryAssociationControl();
		categoryAssociationControl.setCaption(caption);
		categoryAssociationControl.setContainer(targetContainer);
		categoryAssociationControl.setBaseAbstractAttribute(categoryAssociation);

		int sequenceNumber = getNextSequenceNumber(parentContainer);
		categoryAssociationControl.setSequenceNumber(sequenceNumber);

		parentContainer.addControl(categoryAssociationControl);
		categoryAssociationControl.setParentContainer((Container) parentContainer);

		return categoryAssociationControl;
	}

	/**
	 * @param captionValue
	 * @param abstractEntity category entity
	 * @return container object for category entity
	 */
	private ContainerInterface createContainer(AbstractEntityInterface abstractEntity,
			String captionValue)
	{
		ContainerInterface container = null;
		if (abstractEntity.getContainerCollection().size() > 0)
		{
			container = new ArrayList<ContainerInterface>(abstractEntity.getContainerCollection())
					.get(0);
		}
		if (container == null)
		{
			container = DomainObjectFactory.getInstance().createContainer();
			container.setMainTableCss("formRequiredLabel");
			container.setRequiredFieldIndicatior("*");
			container.setRequiredFieldWarningMessage("indicates mandatory fields.");
			container.setAbstractEntity(abstractEntity);
			abstractEntity.addContainer(container);
		}
		String caption = captionValue;
		if (caption == null)
		{
			caption = abstractEntity.getName() + " category container";
		}

		container.setCaption(caption);

		return container;
	}

	/**
	 *
	 * @param container category entity container
	 * @param baseAbstractAttribute category attribute
	 * @return text field object
	 */
	private TextFieldInterface createOrUpdateTextFieldControl(ContainerInterface container,
			BaseAbstractAttributeInterface baseAbstractAttribute)
	{
		ControlInterface control = getControl(container, baseAbstractAttribute);
		TextFieldInterface textField = null;
		if ((control != null) && !(control instanceof TextFieldInterface))
		{
			removeControl(container, control);
		}
		else
		{
			textField = (TextFieldInterface) control;
		}

		if (textField == null)
		{
			textField = DomainObjectFactory.getInstance().createTextField();
			textField.setColumns(50);
		}

		return textField;
	}

	private ControlInterface getControl(ContainerInterface container,
			BaseAbstractAttributeInterface baseAbstractAttribute)
	{
		ControlInterface controlInterface = null;
		for (ControlInterface control : container.getControlCollection())
		{
			if (baseAbstractAttribute.equals(control.getBaseAbstractAttribute()))
			{
				controlInterface = control;
			}
		}

		return controlInterface;
	}

	/**
	 * @param container category entity container
	 * @param baseAbstractAttribute category attribute
	 * @param permissibleValues list of permissible values
	 * @return list box object
	 * @throws DynamicExtensionsSystemException
	 */
	private SelectInterface createOrUpdateSelectControl(ContainerInterface container,
			BaseAbstractAttributeInterface baseAbstractAttribute,
			List<PermissibleValueInterface> permissibleValues, ControlEnum controlType,
			Map<String, String> permValueOptions, long lineNumber)
			throws DynamicExtensionsSystemException
	{
		CategoryAttribute categoryAttribute = (CategoryAttribute) baseAbstractAttribute;
		ControlInterface control = getControl(container, baseAbstractAttribute);
		SelectInterface selectControl = null;
		if ((control != null) && !(control instanceof SelectInterface))
		{
			removeControl(container, control);
		}
		else
		{
			if (((control instanceof ComboBox) && controlType.equals(controlType.COMBO_BOX_CONTROL))
					|| ((control instanceof ListBox) && controlType
							.equals(controlType.LIST_BOX_CONTROL))
					|| ((control instanceof MultiSelectCheckBox) && controlType
							.equals(controlType.MULTISELECT_CHECKBOX_CONTROL)))

			{
				selectControl = (SelectInterface) control;
			}
			else if (control != null)
			{
				removeControl(container, control);
			}

		}

		if (selectControl == null)
		{
			if (controlType.equals(controlType.LIST_BOX_CONTROL))
			{
				if (categoryAttribute.getAbstractAttribute() instanceof AssociationInterface)
				{
					selectControl = DomainObjectFactory.getInstance().createListBox();
				}
				else
				{
					selectControl = DomainObjectFactory.getInstance().createComboBox();
				}
			}
			else if (controlType.equals(controlType.MULTISELECT_CHECKBOX_CONTROL)
					&& (categoryAttribute.getAbstractAttribute() instanceof AssociationInterface))
			{
				selectControl = DomainObjectFactory.getInstance().createMultiSelectCheckBox();
			}
			else if (controlType.equals(controlType.COMBO_BOX_CONTROL))
			{
				selectControl = DomainObjectFactory.getInstance().createComboBox();
			}
		}

		checkForRemovedPermissibleValues(categoryAttribute, permissibleValues);
		if ((categoryAttribute.getId() != null)
				&& isDataEntered(categoryAttribute.getCategoryEntity()))
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.PV_EDIT));
		}

		//clear old permissible values
		categoryAttribute.clearDataElementCollection();

		UserDefinedDEInterface userDefinedDE = DomainObjectFactory.getInstance()
				.createUserDefinedDE();
		for (PermissibleValueInterface pv : permissibleValues)
		{
			//remove all dependent skip Logic Attributes.
			pv.removeAllDependentSkipLogicAttributes();

			userDefinedDE.addPermissibleValue(pv);
		}

		//add new permissible values
		categoryAttribute.setDataElement(userDefinedDE);

		setOptions(userDefinedDE, permValueOptions, lineNumber);

		AttributeInterface attribute = categoryAttribute.getAbstractAttribute().getEntity()
				.getAttributeByName(categoryAttribute.getAbstractAttribute().getName());
		AttributeTypeInformationInterface attributeTypeInformation = attribute
				.getAttributeTypeInformation();

		if (attributeTypeInformation.getDefaultValue() != null)
		{
			categoryAttribute.setDefaultValue(attributeTypeInformation.getDefaultValue());
		}
		return selectControl;
	}

	/**
	 * @param categoryEntity
	 * @return true if the data is entered for the category
	 * @throws DynamicExtensionsSystemException
	 */
	public static boolean isDataEntered(AbstractEntityInterface abstractEntity)
			throws DynamicExtensionsSystemException
	{

		boolean isDataEntered = false;
		String rootTableName = abstractEntity.getTableProperties().getName();
		if (EntityManagerUtil.getNoOfRecordInTable(rootTableName) > 0)
		{
			isDataEntered = true;
		}

		return isDataEntered;
	}

	/**
	 * This method returns true if the existing permissible value is not present
	 * in the incoming permissible value set
	 * @param categoryAttribute
	 * @param permissibleValues
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private void checkForRemovedPermissibleValues(CategoryAttribute categoryAttribute,
			List<PermissibleValueInterface> permissibleValues)
			throws DynamicExtensionsSystemException
	{
		boolean isPermissibleValueRemoved = false;
		Collection<PermissibleValueInterface> existingPv = null;
		if (categoryAttribute.getDataElement() != null)
		{
			existingPv = ((UserDefinedDEInterface) categoryAttribute.getDataElement())
					.getPermissibleValueCollection();
			for (PermissibleValueInterface pv : existingPv)
			{
				if (!permissibleValues.contains(pv))
				{
					isPermissibleValueRemoved = true;
					break;
				}

			}
		}

		if (isPermissibleValueRemoved)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue("ERROR AT LINE NO:" + categoryFileParser.getLineNumber() + " "
							+ CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.PV_EDIT)
					+ "\nORIGINAL PV SET FOR CATEGORY ATTRIBUE "
					+ categoryAttribute.getName()
					+ ":" + existingPv);
		}
	}

	/**
	 * @param control
	 * @param nextLine
	 * @throws DynamicExtensionsSystemException
	 */
	public void setOptions(DynamicExtensionBaseDomainObjectInterface dyextnBaseDomainObject,
			Map<String, String> options, long lineNumber) throws DynamicExtensionsSystemException
	{
		try
		{
			if (options.isEmpty())
			{
				return;
			}
			for (String optionString : options.keySet())
			{
				String methodName = CategoryConstants.SET + optionString;

				Class[] types = getParameterType(methodName, dyextnBaseDomainObject);
				if (types.length < 1)
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
							+ lineNumber
							+ ApplicationProperties.getValue("incorrectControlOption")
							+ optionString);
				}
				List<Object> values = new ArrayList<Object>();
				values.add(getFormattedValues(types[0], options.get(optionString)));

				Method method;

				method = dyextnBaseDomainObject.getClass().getMethod(methodName, types);

				method.invoke(dyextnBaseDomainObject, values.toArray());
			}
		}
		catch (SecurityException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.CONTACT_ADMIN), e);
		}
		catch (NoSuchMethodException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
					+ lineNumber
					+ ApplicationProperties.getValue("incorrectOption"), e);
		}
		catch (IllegalArgumentException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.CONTACT_ADMIN), e);
		}
		catch (IllegalAccessException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.CONTACT_ADMIN), e);
		}
		catch (InvocationTargetException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.CONTACT_ADMIN), e);
		}
		catch (InstantiationException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.CONTACT_ADMIN), e);
		}
	}

	/**
	 * This meth
	 * @param methodName
	 * @param object
	 * @return
	 */
	private Class[] getParameterType(String methodName, Object object)
	{
		Class[] parameterTypes = new Class[0];
		for (Method method : object.getClass().getMethods())
		{
			if (methodName.equals(method.getName()))
			{
				parameterTypes = method.getParameterTypes();
			}
		}

		return parameterTypes;
	}

	/**
	 * @param type
	 * @param string
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	private Object getFormattedValues(Class type, String string) throws SecurityException,
			NoSuchMethodException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException
	{
		Method method = type.getMethod("valueOf", new Class[]{String.class});
		return method.invoke(type, new Object[]{string});
	}

	/**
	 * @param container
	 * @param control
	 */
	private void removeControl(ContainerInterface container, ControlInterface control)
	{
		control.setParentContainer(null);
		container.getControlCollection().remove(control);
	}

	/**
	 * @param categoryAssociation
	 */
	private void removeCategoryAssociation(CategoryAssociationInterface categoryAssociation)
	{
		CategoryEntityInterface sourceCategoryEntity = categoryAssociation.getCategoryEntity();
		CategoryEntityInterface targetCategoryEntity = categoryAssociation
				.getTargetCategoryEntity();

		targetCategoryEntity.setPath(null);
		sourceCategoryEntity.getChildCategories().remove(targetCategoryEntity);
		sourceCategoryEntity.getCategoryAssociationCollection().remove(categoryAssociation);
		categoryAssociation.setCategoryEntity(null);

		targetCategoryEntity.setParentCategoryEntity(null);
	}

	/**
	 * @param container
	 * @param control
	 * @param baseAbstractAttribute
	 * @param controlCaption
	 */
	private void updateContainerAndControl(ContainerInterface container, ControlInterface control,
			BaseAbstractAttributeInterface baseAbstractAttribute, String controlCaption)
	{
		container.addControl(control);
		control.setParentContainer((Container) container);
		control.setBaseAbstractAttribute(baseAbstractAttribute);
		control.setCaption(controlCaption);
	}

	/**
	 * @param container category entity container
	 * @param baseAbstractAttribute category attribute
	 * @return date picker object
	 */
	private DatePickerInterface createOrUpdateDatePickerControl(ContainerInterface container,
			BaseAbstractAttributeInterface baseAbstractAttribute)
	{
		ControlInterface control = getControl(container, baseAbstractAttribute);
		DatePickerInterface datePicker = null;
		if ((control != null) && !(control instanceof DatePickerInterface))
		{
			removeControl(container, control);
		}
		else
		{
			datePicker = (DatePickerInterface) control;
		}

		if (datePicker == null)
		{
			datePicker = DomainObjectFactory.getInstance().createDatePicker();

		}

		datePicker.setDateValueType(ProcessorConstants.DATE_ONLY_FORMAT);

		return datePicker;
	}

	/**
	 * @param container category entity container
	 * @param baseAbstractAttribute category attribute
	 * @return file upload object
	 */
	private FileUploadInterface createOrUpdateFileUploadControl(ContainerInterface container,
			BaseAbstractAttributeInterface baseAbstractAttribute)
	{
		ControlInterface control = getControl(container, baseAbstractAttribute);
		FileUploadInterface fileUpload = null;
		if ((control != null) && !(control instanceof FileUploadInterface))
		{
			removeControl(container, control);
		}
		else
		{
			fileUpload = (FileUploadInterface) control;
		}

		if (fileUpload == null)
		{
			fileUpload = DomainObjectFactory.getInstance().createFileUploadControl();
		}

		return fileUpload;
	}

	/**
	 * @param container category entity container
	 * @param baseAbstractAttribute category attribute
	 * @return text area object
	 */
	private TextAreaInterface createOrUpdateTextAreaControl(ContainerInterface container,
			BaseAbstractAttributeInterface baseAbstractAttribute)
	{
		ControlInterface control = getControl(container, baseAbstractAttribute);
		TextAreaInterface textArea = null;
		if ((control != null) && !(control instanceof TextAreaInterface))
		{
			removeControl(container, control);
		}
		else
		{
			textArea = (TextAreaInterface) control;
		}

		if (textArea == null)
		{
			textArea = DomainObjectFactory.getInstance().createTextArea();
			textArea.setColumns(50);
			textArea.setRows(5);
		}

		return textArea;
	}

	/**
	 * @param container category entity container
	 * @param baseAbstractAttribute category attribute
	 * @param permissibleValues list of permissible values
	 * @return RadioButtonInterface radio button object
	 * @throws DynamicExtensionsSystemException
	 */
	private RadioButtonInterface createOrUpdateRadioButtonControl(ContainerInterface container,
			BaseAbstractAttributeInterface baseAbstractAttribute,
			List<PermissibleValueInterface> permissibleValues,
			Map<String, String> permValueOptions, long lineNumber)
			throws DynamicExtensionsSystemException
	{
		ControlInterface control = getControl(container, baseAbstractAttribute);
		RadioButtonInterface radioButton = null;
		if ((control != null) && !(control instanceof RadioButtonInterface))
		{
			removeControl(container, control);
		}
		else
		{
			radioButton = (RadioButtonInterface) control;
		}

		if (radioButton == null)
		{
			radioButton = DomainObjectFactory.getInstance().createRadioButton();
		}

		checkForRemovedPermissibleValues(((CategoryAttribute) baseAbstractAttribute),
				permissibleValues);
		if ((baseAbstractAttribute.getId() != null)
				&& isDataEntered(((CategoryAttribute) baseAbstractAttribute).getCategoryEntity()))
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.CREATE_CAT_FAILS)
					+ ApplicationProperties.getValue(CategoryConstants.PV_EDIT));
		}

		UserDefinedDEInterface userDefinedDE = DomainObjectFactory.getInstance()
				.createUserDefinedDE();
		for (PermissibleValueInterface pv : permissibleValues)
		{
			//remove all dependent skip Logic Attributes.
			pv.removeAllDependentSkipLogicAttributes();

			userDefinedDE.addPermissibleValue(pv);
		}

		((CategoryAttribute) baseAbstractAttribute).setDataElement(userDefinedDE);

		setOptions(userDefinedDE, permValueOptions, lineNumber);

		return radioButton;
	}

	/**
	 * @param container category entity container
	 * @param baseAbstractAttribute category attribute
	 * @return check box object
	 */
	private CheckBoxInterface createOrUpdateCheckBoxControl(ContainerInterface container,
			BaseAbstractAttributeInterface baseAbstractAttribute)
	{
		ControlInterface control = getControl(container, baseAbstractAttribute);
		CheckBoxInterface checkBox = null;
		if ((control != null) && !(control instanceof CheckBoxInterface))
		{
			removeControl(container, control);
		}
		else
		{
			checkBox = (CheckBoxInterface) control;
		}

		if (checkBox == null)
		{
			checkBox = DomainObjectFactory.getInstance().createCheckBox();
		}

		return checkBox;
	}

	/**
	 * This method creates a list of permissible values for a category attribute
	 * @param entity entity which contains attribute by the given name
	 * @param attributeName name of the attribute
	 * @param desiredPermissibleValues subset of permissible values for this category attribute
	 * @return list of permissible values for category attribute
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	public List<PermissibleValueInterface> createPermissibleValuesList(EntityInterface entity,
			String attributeName, Long lineNo,
			Map<String, Collection<SemanticPropertyInterface>> desiredPermissibleValues)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		List<PermissibleValueInterface> permissibleValues = null;

		try
		{
			AttributeInterface attribute = entity.getAttributeByName(attributeName);
			AttributeTypeInformationInterface attributeTypeInformation = attribute
					.getAttributeTypeInformation();
			UserDefinedDEInterface userDefinedDE = (UserDefinedDE) attributeTypeInformation
					.getDataElement();

			if ((userDefinedDE == null) || (userDefinedDE.getPermissibleValueCollection() == null)
					|| (userDefinedDE.getPermissibleValueCollection().size() == 0))
			{
				permissibleValues = getPermissibleValueList(attributeTypeInformation,
						desiredPermissibleValues);
			}
			else
			{
				permissibleValues = getSubsetOfPermissibleValues(attributeName, entity.getName(),
						userDefinedDE, lineNo, desiredPermissibleValues, true);
			}
		}
		catch (ParseException parseException)
		{
			throw new DynamicExtensionsSystemException("PARSE EXCEPTION", parseException);
		}

		return permissibleValues;
	}

	/**
	 *
	 * @param attributeName
	 * @param entityName
	 * @param skipLogicAttributeInterface
	 * @param lineNo
	 * @param desiredPermissibleValues
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	public List<PermissibleValueInterface> createPermissibleValuesList(String attributeName,
			String entityName, SkipLogicAttributeInterface skipLogicAttributeInterface,
			Long lineNo, Map<String, Collection<SemanticPropertyInterface>> desiredPermissibleValues)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		UserDefinedDEInterface userDefinedDE = null;
		List<PermissibleValueInterface> permissibleValues = null;
		AttributeMetadataInterface attributeMetadataInterface = (AttributeMetadataInterface) skipLogicAttributeInterface
				.getTargetSkipLogicAttribute();
		if (attributeMetadataInterface != null)
		{
			DataElementInterface dataElementInterface = attributeMetadataInterface.getDataElement();
			if (dataElementInterface instanceof UserDefinedDEInterface)
			{
				userDefinedDE = (UserDefinedDEInterface) dataElementInterface;
			}
		}
		if ((desiredPermissibleValues != null) && !desiredPermissibleValues.isEmpty()
				&& (userDefinedDE != null))
		{
			permissibleValues = getSubsetOfPermissibleValues(attributeName, entityName,
					userDefinedDE, lineNo, desiredPermissibleValues, false);
		}
		return permissibleValues;
	}

	/**
	 *
	 * @param attributeTypeInformation
	 * @param desiredPermissibleValues
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 */
	private List<PermissibleValueInterface> getSubsetOfPermissibleValues(String attributeName,
			String entityName, UserDefinedDEInterface userDefinedDE, Long lineNo,
			Map<String, Collection<SemanticPropertyInterface>> desiredPermissibleValues,
			boolean isClonePermissibleValues) throws DynamicExtensionsApplicationException
	{
		List<PermissibleValueInterface> permissibleValues = new ArrayList<PermissibleValueInterface>();

		CategoryManagerInterface categoryManager = CategoryManager.getInstance();

		//if no permissible values are defined, copy  the all the permissible values
		//of the original attribute
		if (desiredPermissibleValues == null)
		{
			permissibleValues.addAll(userDefinedDE.getPermissibleValues());
		}
		else if (categoryManager.isPermissibleValuesSubsetValid(userDefinedDE,
				desiredPermissibleValues))
		{
			Collection<PermissibleValueInterface> permissibleValueCollection = userDefinedDE
					.getPermissibleValueCollection();
			boolean allDoubleValues = CategoryGenerationUtil
					.isAllPermissibleValuesDouble(permissibleValueCollection);
			boolean allIntegerValues = CategoryGenerationUtil
					.isAllPermissibleValuesInteger(permissibleValueCollection);
			boolean allFloatValues = CategoryGenerationUtil
					.isAllPermissibleValuesFloat(permissibleValueCollection);
			boolean allLongValues = CategoryGenerationUtil
					.isAllPermissibleValuesLong(permissibleValueCollection);
			boolean allShortValues = CategoryGenerationUtil
					.isAllPermissibleValuesShort(permissibleValueCollection);

			if (allFloatValues && (desiredPermissibleValues != null))
			{
				Set<String> permissibleValueString = desiredPermissibleValues.keySet();
				for (String permValue : permissibleValueString)
				{
					for (PermissibleValueInterface pv : userDefinedDE
							.getPermissibleValueCollection())
					{
						if (((Float) pv.getValueAsObject()).floatValue() == Float
								.parseFloat(permValue))
						{
							permissibleValues.add(pv);
							break;
						}
					}
				}

			}
			else if (allDoubleValues && (desiredPermissibleValues != null))
			{
				Set<String> permissibleValueString = desiredPermissibleValues.keySet();
				for (String permValue : permissibleValueString)
				{
					for (PermissibleValueInterface pv : userDefinedDE
							.getPermissibleValueCollection())
					{
						if (((Double) pv.getValueAsObject()).doubleValue() == Double
								.parseDouble(permValue))
						{
							permissibleValues.add(pv);
							break;
						}
					}
				}

			}
			else if (allIntegerValues && (desiredPermissibleValues != null))
			{
				Set<String> permissibleValueString = desiredPermissibleValues.keySet();
				for (String permValue : permissibleValueString)
				{
					for (PermissibleValueInterface pv : permissibleValueCollection)
					{
						if (((Integer) pv.getValueAsObject()).intValue() == Integer
								.parseInt(permValue))
						{
							permissibleValues.add(pv);
							break;
						}
					}

				}
			}

			else if (allLongValues && (desiredPermissibleValues != null))
			{
				Set<String> permissibleValueString = desiredPermissibleValues.keySet();
				for (String permValue : permissibleValueString)
				{
					for (PermissibleValueInterface pv : permissibleValueCollection)
					{
						if (((Integer) pv.getValueAsObject()).longValue() == Long
								.parseLong(permValue))
						{
							permissibleValues.add(pv);
							break;
						}
					}

				}
			}

			else if (allShortValues && (desiredPermissibleValues != null))
			{
				Set<String> permissibleValueString = desiredPermissibleValues.keySet();
				for (String permValue : permissibleValueString)
				{
					for (PermissibleValueInterface pv : permissibleValueCollection)
					{
						if (((Integer) pv.getValueAsObject()).shortValue() == Short
								.parseShort(permValue))
						{
							permissibleValues.add(pv);
							break;
						}
					}

				}
			}

			else
			{
				if (desiredPermissibleValues != null)
				{
					Set<String> permissibleValueString = desiredPermissibleValues.keySet();
					for (String permValue : permissibleValueString)
					{
						for (PermissibleValueInterface pv : permissibleValueCollection)
						{
							if (permValue.equalsIgnoreCase(pv.getValueAsObject().toString()))
							{
								permissibleValues.add(pv);
								break;
							}
						}
					}
				}
			}
		}
		else
		{
			throw new DynamicExtensionsApplicationException(
					"ERROR AT LINE NO "
							+ lineNo
							+ " : INVALID SUBSET OF PERMISSIBLE VALUES. ORIGINAL SET OF PERMISSIBLE VALUES FOR THE ATTRIBUTE "
							+ attributeName + " OF THE ENTITY " + entityName + " IS DIFFERENT.");
		}

		return isClonePermissibleValues
				? clonePermissibleValueList(permissibleValues)
				: permissibleValues;
	}

	/**
	 * clonePermissibleValueList.
	 * @param permissibleValues
	 * @return
	 */
	private List<PermissibleValueInterface> clonePermissibleValueList(
			List<PermissibleValueInterface> permissibleValues)
	{
		List<PermissibleValueInterface> permissibleValueList = new ArrayList<PermissibleValueInterface>();
		for (PermissibleValueInterface permissibleValueInterface : permissibleValues)
		{
			permissibleValueList.add(permissibleValueInterface.clone());
		}
		return permissibleValueList;
	}

	/**
	 *
	 * @param attributeTypeInformation
	 * @param desiredPermissibleValues
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws ParseException
	 */
	public List<PermissibleValueInterface> getPermissibleValueList(
			AttributeTypeInformationInterface attributeTypeInformation,
			Map<String, Collection<SemanticPropertyInterface>> desiredPermissibleValues)
			throws DynamicExtensionsSystemException, ParseException
	{
		List<PermissibleValueInterface> permissibleValues = new ArrayList<PermissibleValueInterface>();
		PermissibleValue permissibleValueInterface = null;
		if (desiredPermissibleValues != null)
		{
			Set<String> permissibleValuString = desiredPermissibleValues.keySet();

			for (String value : permissibleValuString)
			{
				permissibleValueInterface = (PermissibleValue) attributeTypeInformation
						.getPermissibleValueForString(DynamicExtensionsUtility
								.getEscapedStringValue(value));

				permissibleValueInterface.setSemanticPropertyCollection(desiredPermissibleValues
						.get(value));

				permissibleValues.add(permissibleValueInterface);
			}
		}

		return permissibleValues;
	}

	/**
	 *
	 * @param permissibleValueCollection
	 * @param value
	 * @return
	 */
	public PermissibleValueInterface getPermissibleValue(
			Collection<PermissibleValueInterface> permissibleValueCollection, String value)
	{
		PermissibleValueInterface permissibleValueInterface = null;
		if ((permissibleValueCollection != null) && (value != null))
		{
			for (PermissibleValueInterface permissibleValue : permissibleValueCollection)
			{
				Object permissibleValueObject = permissibleValue.getValueAsObject();
				if ((permissibleValueObject != null)
						&& value.equals(String.valueOf(permissibleValueObject)))
				{
					permissibleValueInterface = permissibleValue;
					break;
				}
			}
		}
		return permissibleValueInterface;
	}

	/**
	 * @param rootContainer
	 * @param associationName
	 * @return
	 */
	private AbstractContainmentControlInterface getAssociationControl(
			ContainerInterface rootContainer, Long associationContainerId)
	{
		AbstractContainmentControlInterface associationControl = null;
		if ((rootContainer == null) || (associationContainerId == null))
		{
			return associationControl;
		}

		for (ControlInterface controlInterface : rootContainer.getControlCollection())
		{
			if (controlInterface instanceof AbstractContainmentControlInterface)
			{
				if (((AbstractContainmentControlInterface) controlInterface).getContainer().getId()
						.equals(associationContainerId))
				{
					associationControl = (AbstractContainmentControlInterface) controlInterface;
					return associationControl;
				}

				associationControl = getAssociationControl(
						((AbstractContainmentControlInterface) controlInterface).getContainer(),
						associationContainerId);
				if (associationControl != null)
				{
					return associationControl;

				}
			}
		}

		return associationControl;
	}

	/**
	 * @param control
	 * @param controlType
	 * @throws DynamicExtensionsSystemException
	 */
	public void setDefaultControlsOptions(ControlInterface control, ControlEnum controlType)
			throws DynamicExtensionsSystemException
	{
		try
		{
			switch (controlType)
			{
				case TEXT_FIELD_CONTROL :
					TextFieldInterface textField = (TextFieldInterface) control;
					textField.setIsHidden(false);
					textField.setIsPassword(false);
					textField.setIsReadOnly(false);
					textField.setIsUrl(false);
					break;
				case LIST_BOX_CONTROL :
					SelectInterface selectControl = (SelectInterface) control;
					selectControl.setIsHidden(false);
					selectControl.setIsReadOnly(false);
					break;
				case COMBO_BOX_CONTROL :
					SelectInterface comboControl = (SelectInterface) control;
					comboControl.setIsHidden(false);
					comboControl.setIsReadOnly(false);
					break;
				case DATE_PICKER_CONTROL :
					DatePickerInterface datePickerControl = (DatePickerInterface) control;
					datePickerControl.setIsHidden(false);
					datePickerControl.setIsReadOnly(false);
					datePickerControl.setDateValueType(null);
					break;
				case FILE_UPLOAD_CONTROL :
					FileUploadInterface fileUploadControl = (FileUploadInterface) control;
					fileUploadControl.setIsHidden(false);
					fileUploadControl.setIsReadOnly(false);
					break;
				case TEXT_AREA_CONTROL :
					TextAreaInterface textAreaControl = (TextAreaInterface) control;
					textAreaControl.setIsHidden(false);
					textAreaControl.setIsReadOnly(false);
					break;
				case RADIO_BUTTON_CONTROL :
					RadioButtonInterface radioButtonControl = (RadioButtonInterface) control;
					radioButtonControl.setIsReadOnly(false);
					radioButtonControl.setIsHidden(false);
					break;
				case CHECK_BOX_CONTROL :
					CheckBoxInterface checkboxControl = (CheckBoxInterface) control;
					checkboxControl.setIsReadOnly(false);
					checkboxControl.setIsHidden(false);
					break;
				case LABEL_CONTROL :
					control = control;
					break;
				case MULTISELECT_CHECKBOX_CONTROL :
					MultiSelectCheckBoxInterface multiSelectControl = (MultiSelectCheckBoxInterface) control;
					multiSelectControl.setIsHidden(false);
					multiSelectControl.setIsReadOnly(false);
					break;
				default :
					throw new DynamicExtensionsSystemException("ERROR: INCORRECT CONTROL TYPE");
			}
		}
		catch (Exception e)
		{
			throw new DynamicExtensionsSystemException("PLEASE CONTACT ADMINISTRATOR", e);
		}
	}

	public ControlInterface addOrUpdateLabelControl(EntityInterface entity,
			ContainerInterface container, String controlCaption, long lineNumber, int xPosition,
			int yPosition)
	{
		ControlInterface controlInterface = container.getControlByPosition(xPosition, yPosition);
		if (controlInterface == null)
		{
			controlInterface = DomainObjectFactory.getInstance().createLabelControl();
		}
		controlInterface.setControlPosition(xPosition, yPosition);
		updateContainerAndControl(container, controlInterface, null, controlCaption);
		return controlInterface;
	}

	/**
	 * @param category
	 * @param entityGroup
	 * @param sourceContainer
	 * @param targetContainer
	 * @param associationList
	 * @param noOfEntries
	 * @param instance
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public void addChildContainers(CategoryInterface category, EntityGroupInterface entityGroup,
			ContainerInterface sourceContainer, ContainerInterface targetContainer,
			List<AssociationInterface> associationList, int noOfEntries, String instance)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{

		if (!sourceContainer.getChildContainerCollection().contains(targetContainer))
		{
			CategoryEntityInterface sourceCategoryEntity = (CategoryEntityInterface) sourceContainer
					.getAbstractEntity();
			CategoryEntityInterface targetCategoryEntity = (CategoryEntityInterface) targetContainer
					.getAbstractEntity();

			associateCategoryEntities(sourceCategoryEntity, targetCategoryEntity,
					sourceCategoryEntity.getName() + " to " + targetCategoryEntity.getName()
							+ " category association", 1, entityGroup, associationList, instance);

			sourceContainer.getChildContainerCollection().add(targetContainer);
		}
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.util.CategoryHelperInterface#removeAllSeprators(edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface)
	 */
	public void removeAllSeprators(ContainerInterface temp)
	{
		Collection<ControlInterface> collection = temp.getAllControls();
		Iterator<ControlInterface> iterator = collection.iterator();
		while (iterator.hasNext())
		{
			ControlInterface controlInterface = iterator.next();
			if (controlInterface instanceof Label)
			{
				controlInterface.setParentContainer(null);
				temp.getControlCollection().remove(controlInterface);
			}
		}

	}

	public CategoryFileParser getCategoryFileParser()
	{
		return categoryFileParser;
	}

	public void setCategoryFileParser(CategoryFileParser categoryFileParser)
	{
		this.categoryFileParser = categoryFileParser;
	}

}