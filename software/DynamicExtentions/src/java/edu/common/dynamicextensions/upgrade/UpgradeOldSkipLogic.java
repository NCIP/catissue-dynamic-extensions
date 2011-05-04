/**
 *
 */

package edu.common.dynamicextensions.upgrade;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.common.dynamicextensions.category.creation.HandleSkipLogic;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.SkipLogicAttributeInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.entitymanager.AbstractBaseMetadataManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.operations.CategoryOperations;
import edu.common.dynamicextensions.skiplogic.Action;
import edu.common.dynamicextensions.skiplogic.Condition;
import edu.common.dynamicextensions.skiplogic.ConditionStatements;
import edu.common.dynamicextensions.skiplogic.EnableAction;
import edu.common.dynamicextensions.skiplogic.PermissibleValueAction;
import edu.common.dynamicextensions.skiplogic.PrimitiveCondition;
import edu.common.dynamicextensions.skiplogic.RelationalOperator;
import edu.common.dynamicextensions.skiplogic.ShowAction;
import edu.common.dynamicextensions.skiplogic.SkipLogic;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;

/**
 * @author Gaurav_mehta
 *
 */
public class UpgradeOldSkipLogic
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(UpgradeOldSkipLogic.class);

	/** The Constant catAttrVsConditionStatements. */
	private static final Map<ControlInterface, ConditionStatements> catAttrVsConditionStatements = new HashMap<ControlInterface, ConditionStatements>();

	/** The all containers. */
	private static final Map<ContainerInterface, Collection<CategoryAttributeInterface>> allContainers = new HashMap<ContainerInterface, Collection<CategoryAttributeInterface>>();

	/**
	 * The main method.
	 * @param args the arguments
	 */
	public static void main(String[] args)
	{
		// This is based on Extract-Transform-Load approach.
		try
		{
			//Step 1. Extract the required information
			Collection<SkipLogicAttributeInterface> allSkipLogicAttributeObjects = extractInformation();

			//Step 2. Transform the data from old Objects into New Skip Logic POJO's
			transformIntoNewModel(allSkipLogicAttributeObjects);

			//Step 3. Load the new Skip Logic POJO's into database.
			loadNewObjectInDatabase();
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
			LOGGER.error("Error occured while updating old Skip Logic to New Skip Logic model");
		}
		catch (DAOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Extract information.
	 * @return the collection< skip logic attribute interface>
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	@SuppressWarnings("unchecked")
	private static Collection<SkipLogicAttributeInterface> extractInformation()
			throws DynamicExtensionsSystemException
	{
		Collection<SkipLogicAttributeInterface> allSkipLogic = AbstractBaseMetadataManager
				.executeHQL("getAllSkipLogicAttribute", new HashMap());

		for (SkipLogicAttributeInterface skipLogicAttribute : allSkipLogic)
		{
			CategoryAttributeInterface sourceCategoryAttribute = skipLogicAttribute
					.getSourceSkipLogicAttribute();

			ContainerInterface container = DynamicExtensionsUtility
					.getContainerForAbstractEntity(sourceCategoryAttribute.getCategoryEntity());
			if (allContainers.get(container) == null)
			{
				allContainers.put(container, new LinkedHashSet<CategoryAttributeInterface>());
			}
			allContainers.get(container).add(sourceCategoryAttribute);
		}
		return allSkipLogic;
	}

	/**
	 * Transform into new model.
	 * @param allSkipLogicAttributeObjects the all skip logic attribute objects
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	private static void transformIntoNewModel(
			Collection<SkipLogicAttributeInterface> allSkipLogicAttributeObjects)
			throws DynamicExtensionsSystemException
	{
		for (SkipLogicAttributeInterface skipLogicAttribute : allSkipLogicAttributeObjects)
		{
			CategoryAttributeInterface sourceCategoryAttribute = skipLogicAttribute
					.getSourceSkipLogicAttribute();

			ContainerInterface containerForCatAttr = getContainerForCatAttribute(sourceCategoryAttribute);

			if (containerForCatAttr != null)
			{
				Collection<CategoryAttributeInterface> allCategoryAttributes = allContainers
						.get(containerForCatAttr);
				for (CategoryAttributeInterface categoryAttribute : allCategoryAttributes)
				{
					Collection<PermissibleValueInterface> allpermissibleValues = categoryAttribute
							.getSkipLogicPermissibleValues();

					for (PermissibleValueInterface permissibleValue : allpermissibleValues)
					{
						Object conditionValue = permissibleValue.getValueAsObject().toString();

						//If this collection size is more than 1 then, this is the case when a controlling attribute has multiple dependents
						Collection<SkipLogicAttributeInterface> allSkipLogicAttributes = permissibleValue
								.getDependentSkipLogicAttributes();

						for (SkipLogicAttributeInterface dependentSkipLogicAttribute : allSkipLogicAttributes)
						{
							CategoryAttributeInterface targetCategoryAttribute = dependentSkipLogicAttribute
									.getTargetSkipLogicAttribute();

							final ContainerInterface container = DynamicExtensionsUtility
									.getContainerForAbstractEntity(targetCategoryAttribute
											.getCategoryEntity());

							final ControlInterface targetControl = DynamicExtensionsUtility
									.getControlForAbstractAttribute(
											(AttributeMetadataInterface) targetCategoryAttribute,
											container, null);

							if (!targetControl.getIsCalculated())
							{
								populateConditionObject(categoryAttribute, conditionValue,
										dependentSkipLogicAttribute, targetControl);
							}
						}
					}
				}
				allContainers.get(containerForCatAttr).clear();
			}
		}
	}

	/**
	 * Gets the container for cat attribute.
	 * @param sourceCategoryAttribute the source category attribute
	 * @return the container for cat attribute
	 */
	private static ContainerInterface getContainerForCatAttribute(
			CategoryAttributeInterface sourceCategoryAttribute)
	{
		Set<Entry<ContainerInterface, Collection<CategoryAttributeInterface>>> entrySet = allContainers
				.entrySet();
		for (Entry<ContainerInterface, Collection<CategoryAttributeInterface>> entry : entrySet)
		{
			Collection<CategoryAttributeInterface> allCategoryAttributes = entry.getValue();
			if (allCategoryAttributes.contains(sourceCategoryAttribute))
			{
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * @param sourceCategoryAttribute
	 * @param conditionValue
	 * @param dependentSkipLogicAttribute
	 * @param targetControl
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private static void populateConditionObject(CategoryAttributeInterface sourceCategoryAttribute,
			Object conditionValue, SkipLogicAttributeInterface dependentSkipLogicAttribute,
			final ControlInterface targetControl)
	{
		Action action = getActionBasedOnControl(targetControl);
		action.setControl(targetControl);
		if (action instanceof PermissibleValueAction)
		{
			UserDefinedDEInterface userDefinedDE = (UserDefinedDEInterface) dependentSkipLogicAttribute
					.getDataElement();
			if (userDefinedDE != null)
			{
				((PermissibleValueAction) action)
						.setListOfPermissibleValues((Set<PermissibleValueInterface>) userDefinedDE
								.getPermissibleValueCollection());
			}
		}
		if (dependentSkipLogicAttribute.getDefaultValuePermissibleValue() != null)
		{
			action.setDefaultSkipLogicValue(dependentSkipLogicAttribute
					.getDefaultValuePermissibleValue());
		}
		else if (dependentSkipLogicAttribute.getTargetSkipLogicAttribute()
				.getDefaultSkipLogicValue() != null)
		{
			action.setDefaultSkipLogicValue(dependentSkipLogicAttribute
					.getTargetSkipLogicAttribute().getDefaultValuePermissibleValue());
		}
		try
		{

			PrimitiveCondition condition = new PrimitiveCondition();
			condition.setCategoryAttribute(sourceCategoryAttribute);
			condition.setRelationalOperator(RelationalOperator.EQUALS);
			condition.setValue(conditionValue);
			condition.setAction(action);
			if (catAttrVsConditionStatements.get(targetControl) == null)
			{
				ConditionStatements switchStatements = new ConditionStatements();
				switchStatements.setControlIdentifier(targetControl.getId());
				switchStatements.setListOfConditions(new HashSet<Condition>());
				catAttrVsConditionStatements.put(targetControl, switchStatements);
			}
			ConditionStatements switchStatement = catAttrVsConditionStatements.get(targetControl);

			// Add case condition to existing Switch statement.
			switchStatement.getListOfConditions().add(condition);
		}
		catch (DynamicExtensionsSystemException e)
		{
			LOGGER
					.error("Error occured while populating new Skip Logic object from old Skip Logic objects");
			e.printStackTrace();
		}

	}

	/**
	 * Load new object in database.
	 * @throws DAOException
	 * @throws DynamicExtensionsSystemException
	 */
	private static void loadNewObjectInDatabase() throws DAOException, DynamicExtensionsSystemException
	{
		Map<ContainerInterface, SkipLogic> conditionStatements = HandleSkipLogic
				.populateControlIdentifierInSkipLogic(catAttrVsConditionStatements);

		Set<Entry<ContainerInterface, SkipLogic>> entrySet = conditionStatements.entrySet();
		System.out.println("Inside Saving");
		HibernateDAO hibernateDAO = null;
		for (Entry<ContainerInterface, SkipLogic> entry : entrySet)
		{
			try
			{
				// Insert new Skip Logic into database and cache
				hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();
				hibernateDAO.insert(entry.getValue());
				hibernateDAO.commit();

				LOGGER.info("Updated Skip Logic for Container Id :" + entry.getKey().getId());
			}
			catch (DynamicExtensionsSystemException e)
			{
				LOGGER.error("Error occured while inserting new Skip Logic object into database");
				e.printStackTrace();
				LOGGER.info("Container Id : " + entry.getKey().getId());
				//throw new DynamicExtensionsSystemException("Error occured while inserting new Skip Logic object into database");
			}
			catch (DAOException e)
			{
				hibernateDAO.rollback();
			}finally
			{
				DynamicExtensionsUtility.closeDAO(hibernateDAO);
			}
		}
	}

	/**
	 * Gets the action based on control.
	 * @param controlForAttribute the control for attribute
	 * @return the action based on control
	 */
	private static Action getActionBasedOnControl(ControlInterface controlForAttribute)
	{
		Action action;
		if (controlForAttribute.getIsShowHide() != null && controlForAttribute.getIsShowHide())
		{
			action = new ShowAction();
		}
		else if (controlForAttribute.getIsSelectiveReadOnly() != null
				&& controlForAttribute.getIsSelectiveReadOnly())
		{
			action = new EnableAction();
		}
		else
		{
			action = new PermissibleValueAction();
		}
		return action;
	}
}
