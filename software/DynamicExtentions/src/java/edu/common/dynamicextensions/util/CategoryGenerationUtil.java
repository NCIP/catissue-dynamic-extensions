
package edu.common.dynamicextensions.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domain.CategoryEntity;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.NumericAttributeTypeInformation;
import edu.common.dynamicextensions.domain.PathAssociationRelationInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.CalculatedAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.CategoryConstants;
import edu.common.dynamicextensions.util.parser.CategoryCSVConstants;
import edu.common.dynamicextensions.util.parser.CategoryFileParser;
import edu.common.dynamicextensions.util.parser.FormulaParser;
import edu.common.dynamicextensions.validation.category.CategoryValidator;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.global.ApplicationProperties;

/**
 *
 * @author kunal_kamble
 *
 */
public class CategoryGenerationUtil
{

	public static CategoryHelperInterface categoryHelper = new CategoryHelper();

	/**
	 * This method returns the entity from the entity group.
	 * @param entityName
	 * @param entityGroup
	 * @return
	 */
	public static EntityInterface getEntity(String entityName, EntityGroupInterface entityGroup)
	{
		return entityGroup.getEntityByName(entityName);
	}

	/**
	 * Returns the multiplicity in number for the give string
	 * @param multiplicity
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public static int getMultiplicityInNumbers(String multiplicity)
			throws DynamicExtensionsSystemException
	{
		int multiplicityI = 1;
		if (multiplicity.equalsIgnoreCase(CategoryCSVConstants.MULTILINE))
		{
			multiplicityI = -1;
		}
		else if (multiplicity.equalsIgnoreCase(CategoryCSVConstants.SINGLE))
		{
			multiplicityI = 1;
		}
		else
		{
			throw new DynamicExtensionsSystemException(
					"ERROR: WRONG KEYWORD USED FOR MULTIPLICITY " + multiplicity
							+ ". VALID KEY WORDS ARE: 1- single 2-multiline");
		}
		return multiplicityI;
	}

	/**
	 * Sets the root category entity for the category.
	 * @param category
	 * @param rootContainerObject
	 * @param containerCollection
	 * @param paths
	 * @param absolutePath
	 * @param containerNameInstanceMap
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public static void setRootContainer(CategoryInterface category,
			ContainerInterface rootContainerObject, List<ContainerInterface> containerCollection,
			Map<String, List<AssociationInterface>> paths, Map<String, List<String>> absolutePath,
			Map<String, String> containerNameInstanceMap) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		ContainerInterface rootContainer = rootContainerObject;
		//		ContainerInterface rootContainer = null;
		//		for (ContainerInterface containerInterface : containerCollection)
		//		{
		//			CategoryEntityInterface categoryEntityInterface = (CategoryEntityInterface) containerInterface.getAbstractEntity();
		//			if (categoryEntityInterface.getTreeParentCategoryEntity() == null)
		//			{
		//				rootContainer = containerInterface;
		//				break;
		//			}
		//		}

		CategoryHelperInterface categoryHelper = new CategoryHelper();

		for (String entityName : absolutePath.keySet())
		{

			if (absolutePath.get(entityName).size() == 1 && rootContainer != null)
			{
				CategoryEntityInterface categoryEntityInterface = (CategoryEntityInterface) rootContainer
						.getAbstractEntity();
				String entityNameForAssociationMap = CategoryGenerationUtil
						.getEntityNameForAssociationMap(containerNameInstanceMap
								.get(categoryEntityInterface.getName()));
				if (!entityName.equals(entityNameForAssociationMap))
				{
					String entName = entityName;
					EntityInterface entity = CategoryGenerationUtil
							.getEntityFromEntityAssociationMap(paths.get(entityName));
					if (entity != null)
					{
						entName = entity.getName();
					}
					ContainerInterface containerInterface = getContainerWithEntityName(
							containerCollection, entName);
					if (containerInterface == null)
					{
						EntityGroupInterface entityGroup = categoryEntityInterface.getEntity()
								.getEntityGroup();
						entity = entityGroup.getEntityByName(entName);

						ContainerInterface newRootContainer = categoryHelper
								.createOrUpdateCategoryEntityAndContainer(entity, null, category,
										entName + "[1]" + entName + "[1]");
						newRootContainer.setAddCaption(false);

						categoryHelper.associateCategoryContainers(category, entityGroup,
								newRootContainer, rootContainer, paths
										.get(entityNameForAssociationMap), 1,
								containerNameInstanceMap.get(rootContainer.getAbstractEntity()
										.getName()));

						rootContainer = newRootContainer;

					}
					else
					{
						rootContainer = containerInterface;
					}

				}
			}
		}

		for (ContainerInterface containerInterface : containerCollection)
		{
			CategoryEntityInterface categoryEntity = ((CategoryEntityInterface) containerInterface
					.getAbstractEntity());
			boolean isTableCreated = ((CategoryEntity) categoryEntity).isCreateTable();
			if (rootContainer != containerInterface
					&& categoryEntity.getTreeParentCategoryEntity() == null && isTableCreated)
			{
				categoryHelper.associateCategoryContainers(category, categoryEntity.getEntity()
						.getEntityGroup(), rootContainer, containerInterface, paths
						.get(CategoryGenerationUtil
								.getEntityNameForAssociationMap(containerNameInstanceMap
										.get(categoryEntity.getName()))), 1,
						containerNameInstanceMap.get(containerInterface.getAbstractEntity()
								.getName()));
			}
		}
		//If category is edited and no attributes from the main form of the model are not selected
		//rootContainer will be null
		//keep the root container unchanged in this case. Just use the root entity of the category
		if (rootContainer == null)
		{
			EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
			rootContainer = entityManagerInterface.getContainerByEntityIdentifier(category
					.getRootCategoryElement().getId());
		}
		categoryHelper.setRootCategoryEntity(rootContainer, category);

		categoryHelper.setRootCategoryEntity(rootContainer, category);
	}

	/**
	 * Returns the container having container caption as one passed to this method.
	 * @param containerCollection
	 * @param containerCaption
	 * @return
	 */
	public static ContainerInterface getContainer(List<ContainerInterface> containerCollection,
			String containerCaption)
	{
		ContainerInterface container = null;
		for (ContainerInterface containerInterface : containerCollection)
		{
			if (containerCaption.equals(containerInterface.getCaption()))
			{
				container = containerInterface;
				break;
			}

		}
		return container;
	}

	/**
	 * @param containerCollection
	 * @param entityName
	 * @return
	 */
	public static ContainerInterface getContainerWithEntityName(
			List<ContainerInterface> containerCollection, String entityName)
	{
		ContainerInterface container = null;
		for (ContainerInterface containerInterface : containerCollection)
		{
			CategoryEntityInterface categoryEntity = (CategoryEntityInterface) containerInterface
					.getAbstractEntity();
			if (entityName.equals(categoryEntity.getEntity().getName()))
			{
				container = containerInterface;
				break;
			}

		}
		return container;
	}

	/**
	 * Returns the container with given category entity name.
	 * @param containerCollection
	 * @param categoryEntityName
	 * @return
	 */
	public static ContainerInterface getContainerWithCategoryEntityName(
			List<ContainerInterface> containerCollection, String categoryEntityName)
	{
		ContainerInterface container = null;

		for (ContainerInterface containerInterface : containerCollection)
		{
			if (categoryEntityName.equals(containerInterface.getAbstractEntity().getName()))
			{
				container = containerInterface;
				break;
			}

		}
		return container;
	}

	/**
	 * @param paths
	 * @param entityGroup
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public static Map<String, List<AssociationInterface>> getAssociationList(
			Map<String, List<String>> paths, EntityGroupInterface entityGroup)
			throws DynamicExtensionsSystemException
	{
		Map<String, List<AssociationInterface>> entityPaths = new HashMap<String, List<AssociationInterface>>();

		Set<String> entitiesNames = paths.keySet();
		for (String entityName : entitiesNames)
		{
			// Path stored is from the root.
			List<String> pathsForEntity = paths.get(entityName);

			List<AssociationInterface> associations = new ArrayList<AssociationInterface>();
			Iterator<String> entNamesIter = pathsForEntity.iterator();

			String srcEntityName = entNamesIter.next();
			String associationRoleName = getAssociationRoleName(srcEntityName);

			EntityInterface sourceEntity = entityGroup
					.getEntityByName(getEntityNameExcludingAssociationRoleName(srcEntityName));
			CategoryValidator.checkForNullRefernce(sourceEntity,
					"ERROR IN DEFINING PATH FOR THE ENTITY " + entityName + ": ENTITY WITH NAME "
							+ srcEntityName + " DOES NOT EXIST");

			while (entNamesIter.hasNext())
			{
				String targetEntityName = entNamesIter.next();
				int size = associations.size();
				EntityInterface targetEntity = entityGroup
						.getEntityByName(getEntityNameExcludingAssociationRoleName(targetEntityName));
				addAssociation(sourceEntity, targetEntity, associationRoleName, associations);

				// Add all parent entity associations also to the list.
				EntityInterface parentEntity = sourceEntity.getParentEntity();
				while (parentEntity != null)
				{
					addAssociation(parentEntity, targetEntity, associationRoleName, associations);

					parentEntity = parentEntity.getParentEntity();
				}
				if (associations.size() == size)
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ "Error in defining path for the entity "
							+ entityName
							+ ": "
							+ sourceEntity.getName()
							+ " --> "
							+ targetEntityName
							+ "  Association does not exist.");
				}
				// Source entity should now be target entity.
				sourceEntity = targetEntity;
				associationRoleName = getAssociationRoleName(targetEntityName);
			}

			entityPaths.put(entityName, associations);

			if (pathsForEntity.size() > 1 && associations.isEmpty())
			{
				CategoryValidator.checkForNullRefernce(null, "ERROR: PATH DEFINED FOR THE ENTITY "
						+ entityName + " IS NOT CORRECT");
			}
		}

		return entityPaths;
	}

	/**
	 * @throws DynamicExtensionsSystemException
	 *
	 */
	private static void addAssociation(EntityInterface sourceEntity, EntityInterface targetEntity,
			String associationRoleName, List<AssociationInterface> associations)
	{
		for (AssociationInterface association : sourceEntity.getAssociationCollection())
		{
			if (association.getTargetEntity().equals(targetEntity))
			{
				if (associationRoleName != null && associationRoleName.length() > 0
						&& associationRoleName.equals(association.getTargetRole().getName()))
				{
					associations.add(association);
					break;
				}
				else if (associationRoleName != null && associationRoleName.length() == 0)
				{
					associations.add(association);
					break;
				}
			}
		}
	}

	/**
	 *
	 * @return
	 */
	public static String getEntityNameExcludingAssociationRoleName(String entityName)
	{
		String newEntityName = entityName;
		String associationRoleName = getFullAssociationRoleName(entityName);
		if (associationRoleName != null && associationRoleName.length() > 0)
		{
			newEntityName = entityName.replace(associationRoleName, "");
		}
		return newEntityName;
	}

	/**
	 *
	 * @return
	 */
	public static String getAssociationRoleName(String entityName)
	{
		String associationRoleName = "";
		if (entityName != null && entityName.indexOf('(') != -1 && entityName.indexOf(')') != -1)
		{
			associationRoleName = entityName.substring(entityName.indexOf('(') + 1, entityName
					.indexOf(')'));
		}
		return associationRoleName;
	}

	/**
	 *
	 * @return
	 */
	public static String getFullAssociationRoleName(String entityName)
	{
		String associationRoleName = "";
		if (entityName != null && entityName.indexOf('(') != -1 && entityName.indexOf(')') != -1)
		{
			associationRoleName = entityName.substring(entityName.indexOf('('), entityName
					.indexOf(')') + 1);
		}
		return associationRoleName;
	}

	/**
	 * This method gets the relative path.
	 * @param entityNameList ordered entities names in the path
	 * @param pathMap
	 * @return
	 */
	public static List<String> getRelativePath(List<String> entityNameList,
			Map<String, List<String>> pathMap)
	{
		List<String> newEntityNameList = new ArrayList<String>();
		String lastEntityName = null;
		for (String entityName : entityNameList)
		{
			if (pathMap.get(entityName) == null)
			{
				newEntityNameList.add(entityName);
			}
			else
			{
				lastEntityName = entityName;
			}
		}
		if (lastEntityName != null && !newEntityNameList.contains(lastEntityName))
		{
			newEntityNameList.add(0, lastEntityName);
		}

		return newEntityNameList;
	}

	/**
	 * Returns the entity group used for careting this category
	 * @param category
	 * @param entityGroupName
	 * @return
	 */
	public static EntityGroupInterface getEntityGroup(CategoryInterface category,
			String entityGroupName)
	{
		EntityGroupInterface entityGroup;
		if (category.getRootCategoryElement() != null)
		{
			entityGroup = category.getRootCategoryElement().getEntity().getEntityGroup();
		}

		entityGroup = EntityCache.getInstance().getEntityGroupByName(entityGroupName);
		return entityGroup;

	}

	/**
	 * This method finds the main category entity.
	 * @param categoryPaths
	 * @return
	 */
	public static String getMainCategoryEntityName(String[] categoryPaths)
	{
		int minNumOfCategoryEntityNames = categoryPaths[0].split("->").length;
		for (String string : categoryPaths)
		{
			if (minNumOfCategoryEntityNames > string.split("->").length)
			{
				minNumOfCategoryEntityNames = string.split("->").length;
			}
		}
		String categoryEntityName = categoryPaths[0].split("->")[0];

		a : for (int i = 0; i < minNumOfCategoryEntityNames; i++)
		{
			String temp = categoryPaths[0].split("->")[i];
			for (String string : categoryPaths)
			{
				if (!string.split("->")[i].equals(temp))
				{
					break a;
				}
			}
			categoryEntityName = categoryPaths[0].split("->")[i];
		}

		return categoryEntityName;
	}

	/**
	 * category names in CSV are of format <entity_name>[instance_Number]
	 * @param categoryNameInCSV
	 * @return
	 */
	public static String getEntityName(String categoryNameInCSV)
	{
		return getEntityNameExcludingAssociationRoleName(categoryNameInCSV.substring(0,
				categoryNameInCSV.indexOf('[')));
	}

	/**
	 * category names in CSV are of format <entity_name>[instance_Number]
	 * @param categoryNameInCSV
	 * @return
	 */
	public static String getEntityNameForAssociationMap(String categoryEntityInstancePath)
	{
		StringBuffer entityNameForAssociationMap = new StringBuffer();
		String[] categoryEntityPathArray = categoryEntityInstancePath.split("->");

		for (String instancePath : categoryEntityPathArray)
		{
			entityNameForAssociationMap
					.append(instancePath.substring(0, instancePath.indexOf("[")));
		}
		return entityNameForAssociationMap.toString();
	}

	/**
	 * category names in CSV are of format <entity_name>[instance_Number]
	 * @param categoryNameInCSV
	 * @return
	 */
	public static String getCategoryEntityName(String categoryEntityInstancePath)
	{
		StringBuffer entityNameForAssociationMap = new StringBuffer();
		String[] categoryEntityPathArray = categoryEntityInstancePath.split("->");

		for (String instancePath : categoryEntityPathArray)
		{
			entityNameForAssociationMap.append(instancePath);
		}
		if (categoryEntityPathArray.length == 1)
		{
			entityNameForAssociationMap.append(categoryEntityPathArray[0]);
		}
		return entityNameForAssociationMap.toString();
	}

	/**
	 * The path which are used for Root Cat Entity path should always be
	 * NAME[1]->Name[1] but user can only specify Name[1] & thus this method modifies the instance
	 * information if only NAME[1] is given to NAME[1]->NAME[1].
	 * @param categoryEntityPath paths.
	 * @return list of modified category entity paths.
	 */
	public static List<String> getCategoryEntityPath(String[] categoryEntityPath)
	{
		List<String> categoryEntityPathList = new ArrayList<String>();
		for (String categoryPath : categoryEntityPath)
		{
			if (!categoryPath.contains("->"))
			{
				categoryPath = categoryPath.concat("->" + categoryPath);
			}
			categoryEntityPathList.add(categoryPath);
		}
		return categoryEntityPathList;
	}

	/**
	 * category names in CSV are of format <entity_name>[instance_Number]
	 * @param categoryNameInCSV
	 * @return
	 */
	public static String getEntityNameFromCategoryEntityInstancePath(
			String categoryEntityInstancePath)
	{
		String entityName = "";
		if (categoryEntityInstancePath != null)
		{
			String[] categoryEntitiesInPath = categoryEntityInstancePath.split("->");
			String newCatEntityName = categoryEntitiesInPath[categoryEntitiesInPath.length - 1];
			entityName = getEntityName(newCatEntityName);
		}
		return entityName;
	}

	/**
	 * category names in CSV are of format <entity_name>[instance_Number]
	 * @param categoryNameInCSV
	 * @return
	 */
	public static EntityInterface getEntityFromEntityAssociationMap(
			List<AssociationInterface> assoList)
	{
		EntityInterface entityInterface = null;
		if (assoList != null && !assoList.isEmpty())
		{
			AssociationInterface association = assoList.get(assoList.size() - 1);
			entityInterface = association.getTargetEntity();
		}
		return entityInterface;
	}

	/**
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsSystemException
	 *
	 */
	public static void setDefaultValueForCalculatedAttributes(CategoryInterface category,
			CategoryEntityInterface rootCatEntity, Long lineNumber)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{

		for (CategoryAttributeInterface categoryAttributeInterface : rootCatEntity
				.getAllCategoryAttributes())
		{
			Boolean isCalcAttr = categoryAttributeInterface.getIsCalculated();
			if (isCalcAttr != null && isCalcAttr)
			{
				setDefaultValue(categoryAttributeInterface, category);
				FormulaCalculator formulaCalculator = new FormulaCalculator();
				String message = formulaCalculator.setDefaultValueForCalculatedAttributes(
						categoryAttributeInterface, rootCatEntity.getCategory());
				if (message != null && message.length() > 0)
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
							+ lineNumber + " " + message);
				}
			}
		}
		for (CategoryAssociationInterface categoryAssociationInterface : rootCatEntity
				.getCategoryAssociationCollection())
		{
			setDefaultValueForCalculatedAttributes(category, categoryAssociationInterface
					.getTargetCategoryEntity(), lineNumber);
		}
	}

	/**
	 *
	 * @return
	 */
	public static void getCategoryAttribute(String entityName, Long instanceNumber,
			String attributeName, CategoryEntityInterface rootCatEntity,
			List<CategoryAttributeInterface> attributes)
	{
		if (rootCatEntity != null)
		{
			Long instanceId = getInstanceIdOfCategoryEntity(rootCatEntity);
			if (rootCatEntity.getEntity().getName().equals(entityName)
					&& instanceId.equals(instanceNumber))
			{
				for (CategoryAttributeInterface categoryAttributeInterface : rootCatEntity
						.getCategoryAttributeCollection())
				{
					if (categoryAttributeInterface.getAbstractAttribute().getName().equals(
							attributeName))
					{
						attributes.add(categoryAttributeInterface);
						return;
					}
				}
			}
			else
			{
				for (CategoryAssociationInterface categoryAssociationInterface : rootCatEntity
						.getCategoryAssociationCollection())
				{
					getCategoryAttribute(entityName, instanceNumber, attributeName,
							categoryAssociationInterface.getTargetCategoryEntity(), attributes);
				}
			}
		}
	}

	/**
	 * setDefaultValueforCalculatedAttributes.
	 * @param defaultValue
	 * @param control
	 * @param categoryAttribute
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	private static void setDefaultValue(CategoryAttributeInterface categoryAttribute,
			CategoryInterface categoryInterface) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException
	{
		FormulaParser formulaParser = new FormulaParser();
		if (formulaParser.validateExpression(categoryAttribute.getFormula().getExpression()))
		{
			categoryAttribute.removeAllCalculatedCategoryAttributes();
			List<String> symbols = formulaParser.getSymobols();
			List<CategoryAttributeInterface> attributes = new ArrayList<CategoryAttributeInterface>();
			for (String symbol : symbols)
			{
				String[] names = symbol.split("_");
				if (names.length == 3)
				{
					attributes.clear();
					try
					{
						Long.parseLong(names[1]);
					}
					catch (NumberFormatException e)
					{
						throw new DynamicExtensionsSystemException(ApplicationProperties
								.getValue(CategoryConstants.CREATE_CAT_FAILS)
								+ " "
								+ names
								+ ApplicationProperties
										.getValue("incorrectFormulaSyntaxCalculatedAttribute")
								+ categoryAttribute.getFormula().getExpression(), e);

					}
					CategoryGenerationUtil.getCategoryAttribute(names[0], Long.valueOf(names[1]),
							names[2], categoryInterface.getRootCategoryElement(), attributes);
					if (attributes.isEmpty())
					{
						throw new DynamicExtensionsSystemException(ApplicationProperties
								.getValue(CategoryConstants.CREATE_CAT_FAILS)
								+ " "
								+ names
								+ ApplicationProperties
										.getValue("incorrectFormulaSyntaxCalculatedAttribute")
								+ categoryAttribute.getFormula().getExpression());
					}
					else
					{
						if (((AttributeMetadataInterface) attributes.get(0))
								.getAttributeTypeInformation() instanceof NumericAttributeTypeInformation
								|| ((AttributeMetadataInterface) attributes.get(0))
										.getAttributeTypeInformation() instanceof DateAttributeTypeInformation)
						{
							CalculatedAttributeInterface calculatedAttribute = DomainObjectFactory
									.getInstance().createCalculatedAttribute();
							calculatedAttribute.setCalculatedAttribute(categoryAttribute);
							calculatedAttribute.setSourceForCalculatedAttribute(attributes.get(0));
							calculatedAttribute.getSourceForCalculatedAttribute()
									.setIsSourceForCalculatedAttribute(Boolean.TRUE);
							categoryAttribute.addCalculatedCategoryAttribute(calculatedAttribute);
						}
						else
						{
							throw new DynamicExtensionsSystemException(ApplicationProperties
									.getValue(CategoryConstants.CREATE_CAT_FAILS)
									+ " "
									+ names
									+ ApplicationProperties
											.getValue("incorrectDataTypeForCalculatedAttribute")
									+ categoryAttribute.getFormula().getExpression());
						}
					}
				}
				else
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ " "
							+ names
							+ ApplicationProperties
									.getValue("incorrectFormulaSyntaxCalculatedAttribute")
							+ categoryAttribute.getFormula().getExpression());
				}
			}
		}
	}

	/**
	 * category names in CSV are of format <entity_name>[instance_Number]
	 * This method will generate category entity names according to the path.
	 * @param categoryNameInCSV
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public static String getCategoryEntityName(String categoryEntityInstancePath,
			Map<String, List<AssociationInterface>> entityNameAssociationMap)
			throws DynamicExtensionsSystemException
	{
		StringBuffer categoryEntityName = new StringBuffer();
		String entityNameForEntityAssocMap = CategoryGenerationUtil
				.getEntityNameForAssociationMap(categoryEntityInstancePath);

		//e.g Annotations[1]->PhysicalExam[2]
		String[] pathWithInstance = categoryEntityInstancePath.split("->");

		int counter = 0;
		if (entityNameAssociationMap.get(entityNameForEntityAssocMap) != null)
		{
			for (AssociationInterface association : entityNameAssociationMap
					.get(entityNameForEntityAssocMap))
			{
				String sourceEntityName = pathWithInstance[counter];
				String targetEntityName = pathWithInstance[counter + 1];
				if (sourceEntityName.indexOf('[') == -1 || sourceEntityName.indexOf(']') == -1)
				{
					throw new DynamicExtensionsSystemException(
							"ERROR: INSTANCE INFORMATION IS NOT IN THE CORRECT FORMAT "
									+ association);

				}
				if (counter == 0)
				{
					categoryEntityName.append(association.getEntity().getName());
					categoryEntityName.append(formatInstance(categoryHelper
							.getInsatnce(sourceEntityName)));
				}

				categoryEntityName.append(association.getTargetEntity().getName());
				categoryEntityName.append(formatInstance(categoryHelper
						.getInsatnce(targetEntityName)));

				counter++;
			}
		}
		if (categoryEntityName.length() == 0)
		{
			categoryEntityName.append(pathWithInstance[0]);
			categoryEntityName.append(pathWithInstance[0]);
		}
		return categoryEntityName.toString();
	}

	/**
	 * @param insatnce
	 * @return
	 */
	private static Object formatInstance(Long insatnce)
	{
		StringBuffer categoryEntityName = new StringBuffer();
		categoryEntityName.append('[');
		categoryEntityName.append(insatnce);
		categoryEntityName.append(']');
		return categoryEntityName;
	}

	/**
	 * This method will verify weather all the values in the given collection are
	 * of Integer type or not.
	 * @param permissibleValueColl permisibleValueCollection
	 * @return true if all values are Integer else false
	 */
	public static boolean isAllPermissibleValuesInteger(
			Collection<PermissibleValueInterface> permissibleValueColl)
	{
		boolean allIntegerValues = false;
		Iterator<PermissibleValueInterface> itrPVInteger = permissibleValueColl.iterator();
		while (itrPVInteger.hasNext())
		{
			if (itrPVInteger.next() instanceof edu.common.dynamicextensions.domain.IntegerValue)
			{
				allIntegerValues = true;
			}
			else
			{
				allIntegerValues = false;
			}
		}
		return allIntegerValues;
	}

	/**
	 * This method will verify weather all the values in the given collection are
	 * of Double type or not.
	 * @param permissibleValueColl permisibleValueCollection
	 * @return true if all values are Double else false
	 */
	public static boolean isAllPermissibleValuesDouble(
			Collection<PermissibleValueInterface> permissibleValueColl)
	{
		boolean allDoubleValues = false;
		Iterator<PermissibleValueInterface> itrPVInteger = permissibleValueColl.iterator();
		while (itrPVInteger.hasNext())
		{
			if (itrPVInteger.next() instanceof edu.common.dynamicextensions.domain.DoubleValue)
			{
				allDoubleValues = true;
			}
			else
			{
				allDoubleValues = false;
			}
		}
		return allDoubleValues;
	}

	/**
	 * This method will verify weather all the values in the given collection are
	 * of Float type or not.
	 * @param permissibleValueColl permisibleValueCollection
	 * @return true if all values are Float else false
	 */
	public static boolean isAllPermissibleValuesFloat(
			Collection<PermissibleValueInterface> permissibleValueColl)
	{
		boolean allFloatValues = false;
		Iterator<PermissibleValueInterface> itrPVInteger = permissibleValueColl.iterator();
		while (itrPVInteger.hasNext())
		{
			if (itrPVInteger.next() instanceof edu.common.dynamicextensions.domain.FloatValue)
			{
				allFloatValues = true;
			}
			else
			{
				allFloatValues = false;
			}
		}
		return allFloatValues;
	}

	/**
	 * This method will verify weather all the values in the given collection are
	 * of Short type or not.
	 * @param permissibleValueColl permisibleValueCollection
	 * @return true if all values are Short else false
	 */
	public static boolean isAllPermissibleValuesShort(
			Collection<PermissibleValueInterface> permissibleValueColl)
	{
		boolean allShortValues = false;
		Iterator<PermissibleValueInterface> itrPVInteger = permissibleValueColl.iterator();
		while (itrPVInteger.hasNext())
		{
			if (itrPVInteger.next() instanceof edu.common.dynamicextensions.domain.ShortValue)
			{
				allShortValues = true;
			}
			else
			{
				allShortValues = false;
			}
		}
		return allShortValues;
	}

	/**
	 * This method will verify weather all the values in the given collection are
	 * of Long type or not.
	 * @param permissibleValueColl permisibleValueCollection
	 * @return true if all values are Long else false
	 */
	public static boolean isAllPermissibleValuesLong(
			Collection<PermissibleValueInterface> permissibleValueColl)
	{
		boolean allLongValues = false;
		Iterator<PermissibleValueInterface> itrPVInteger = permissibleValueColl.iterator();
		while (itrPVInteger.hasNext())
		{
			if (itrPVInteger.next() instanceof edu.common.dynamicextensions.domain.LongValue)
			{
				allLongValues = true;
			}
			else
			{
				allLongValues = false;
			}
		}
		return allLongValues;
	}

	/**
	 * It will search in the given base directory & will find out all the category
	 * files present in the given directory.
	 * @param baseDirectory directory in which to search for the files.
	 * @param relativePath path used to reach the category files.
	 * @return list of the file names relative to the given base directory.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	public static List<String> getCategoryFileListInDirectory(File baseDirectory,
			String relativePath) throws DynamicExtensionsSystemException
	{
		List<String> fileNameList = new ArrayList<String>();
		try
		{
			for (File file : baseDirectory.listFiles())
			{
				if (file.isDirectory())
				{
					String childDirPath = relativePath + file.getName() + "/";
					fileNameList.addAll(getCategoryFileListInDirectory(file, childDirPath));
				}
				else
				{
					if (file.getAbsolutePath().endsWith(".csv")
							|| file.getAbsolutePath().endsWith(".CSV"))
					{
						CategoryFileParser categoryFileParser = DomainObjectFactory.getInstance()
								.createCategoryFileParser(file.getAbsolutePath(), "", null);
						if (categoryFileParser != null)
						{
							if (categoryFileParser.isCategoryFile())
							{
								fileNameList.add(relativePath + file.getName());
							}
							categoryFileParser.closeResources();
						}
					}
				}
			}
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Exception occured while reading the category file names ", e);

		}
		return fileNameList;
	}

	/**
	 * It will search in the given base directory & will find out all the Permissible Value
	 * files present in the given directory.
	 * @param baseDirectory directory in which to search for the files.
	 * @param relativePath path used to reach the category files.
	 * @return list of the file names relative to the given base directory.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	public static List<String> getPVFileListInDirectory(File baseDirectory, String relativePath)
			throws DynamicExtensionsSystemException
	{
		List<String> fileNameList = new ArrayList<String>();
		try
		{
			for (File file : baseDirectory.listFiles())
			{
				if (file.isDirectory())
				{
					String childDirPath = relativePath + file.getName() + "/";
					fileNameList.addAll(getPVFileListInDirectory(file, childDirPath));
				}
				else
				{
					CategoryFileParser categoryFileParser = DomainObjectFactory.getInstance()
							.createCategoryFileParser(file.getAbsolutePath(), "", null);
					if (categoryFileParser != null && categoryFileParser.isPVFile())
					{
						fileNameList.add(relativePath + file.getName());
					}
					if (categoryFileParser != null)
					{
						categoryFileParser.closeResources();
					}

				}
			}
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Exception occured while reading the Permissible Value file", e);
		}
		return fileNameList;
	}

	/**
	 * This method will return the Instance no of the entity used by this category Entity.
	 * @param catEntity category Entity
	 * @return Instance Id.
	 */
	public static long getInstanceIdOfCategoryEntity(CategoryEntityInterface catEntity)
	{

		long instanceId = 1;
		if (catEntity.getPath() != null)
		{
			List<PathAssociationRelationInterface> pathAssociationColl = catEntity.getPath()
					.getSortedPathAssociationRelationCollection();
			PathAssociationRelationInterface pathAssociation = pathAssociationColl
					.get(pathAssociationColl.size() - 1);
			instanceId = pathAssociation.getTargetInstanceId();
		}
		return instanceId;

	}

}