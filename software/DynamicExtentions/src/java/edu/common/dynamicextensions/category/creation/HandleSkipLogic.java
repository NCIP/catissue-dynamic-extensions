/**
 *
 */

package edu.common.dynamicextensions.category.creation;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.CategoryAssociationControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.skiplogic.Action;
import edu.common.dynamicextensions.skiplogic.Condition;
import edu.common.dynamicextensions.skiplogic.ConditionStatements;
import edu.common.dynamicextensions.skiplogic.DisableAction;
import edu.common.dynamicextensions.skiplogic.EnableAction;
import edu.common.dynamicextensions.skiplogic.HideAction;
import edu.common.dynamicextensions.skiplogic.PrimitiveCondition;
import edu.common.dynamicextensions.skiplogic.RelationalOperator;
import edu.common.dynamicextensions.skiplogic.ShowAction;
import edu.common.dynamicextensions.skiplogic.SkipLogic;
import edu.common.dynamicextensions.skiplogic.SkipLogicUtility;
import edu.common.dynamicextensions.util.global.CategoryConstants;
import edu.common.dynamicextensions.util.parser.CategoryFileParser;
import edu.wustl.common.util.logger.Logger;

/**
 * The Class HandleSkipLogic.
 * @author Gaurav_mehta
 */
public class HandleSkipLogic
{

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(HandleSkipLogic.class);

	/** The file parser. */
	private final CategoryFileParser fileParser;

	/** The entity name vs list of asso. */
	private final Map<String, List<AssociationInterface>> entityNameVsListOfAsso;

	/** The skip logic helper. */
	private final PopulateSkipLogicHelper skipLogicHelper;

	/**
	 * Instantiates a new handle skip logic.
	 * @param entityGroup the entity group
	 * @param category the category
	 * @param entityNameAssociationMap the entity name association map
	 * @param categoryFileParser the category file parser
	 */
	public HandleSkipLogic(EntityGroupInterface entityGroup, CategoryInterface category,
			Map<String, List<AssociationInterface>> entityNameAssociationMap,
			CategoryFileParser categoryFileParser)
	{
		fileParser = categoryFileParser;

		entityNameVsListOfAsso = entityNameAssociationMap;

		skipLogicHelper = new PopulateSkipLogicHelper(category, entityGroup);
	}

	/**
	 * Populate skip logic. This method creates Skip Logic defined in the
	 * category. Skip logic is created every time. In case it is an edit case of
	 * category, in that case, it first deletes all the Skip logic associated
	 * for that control and then creates new Skip Logic.
	 * @return the map< skip logic, control interface>
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Map<ControlInterface, ConditionStatements> populateSkipLogic()
			throws DynamicExtensionsSystemException, IOException
	{
		/**
		 * This is a local Map maintained for re-using the same Skip Logic in
		 * case of 1. Single control and multiple dependents 2. A control acting
		 * as dependent as well as controlling
		 */
		Map<ControlInterface, ConditionStatements> catAttrVsConditionStatements = new HashMap<ControlInterface, ConditionStatements>();

		Set<ContainerInterface> setOfAbstractContainmentCtrl = new HashSet<ContainerInterface>();

		Map<ControlInterface, Action> controlVsAction = new HashMap<ControlInterface, Action>();

		// DO following steps for each Skip Logic
		while (fileParser.readNext() && !fileParser.hasRelatedAttributes())
		{
			// Step 1. Get all paths defined for Skip Logic
			String[] categoryPathInformation = fileParser.getCategoryPaths();

			// Read next line.
			fileParser.readNext();

			// Step 2. Get Source Category Attribute
			final CategoryAttributeInterface conditionCategoryAttribute = getconditionCategoryAttribute(
					categoryPathInformation, skipLogicHelper);

			// Step 4. Get the value of the condition //Create Source Skip Logic
			 String conditionValue =fileParser.getConditionValue();

			// Step 5. Get control corresponding to Action
			final List<ControlInterface> targetControls = getTargetControl(categoryPathInformation,
					skipLogicHelper);

			for (ControlInterface targetControl : targetControls)
			{
				// Step 6. Get type of Action to be performed and accordingly create
				// Action object
				Action action = getActionObjectDependingOnActionType(targetControl);
				targetControl.setIsSkipLogicTargetControl(Boolean.TRUE);

				// Step 7. Populate the condition object
				PrimitiveCondition condition = new PrimitiveCondition();
				condition.setCategoryAttribute(conditionCategoryAttribute);
				condition.setRelationalOperator(RelationalOperator.EQUALS);
				condition.setValue(conditionValue);
				condition.setAction(action);

				// Step 8. Combine different cases for a single Switch statement
				// If There is no switch for the control then create a new Switch
				// statement i.e ConditionStatements Object.
				if (catAttrVsConditionStatements.get(targetControl) == null)
				{
					ConditionStatements switchStatements = new ConditionStatements();
					switchStatements.setControlIdentifier(targetControl.getId());
					switchStatements.setListOfConditions(new HashSet<Condition>());
					catAttrVsConditionStatements.put(targetControl, switchStatements);
				}
				ConditionStatements switchStatement = catAttrVsConditionStatements
						.get(targetControl);

				// Add case condition to existing Switch statement.
				switchStatement.getListOfConditions().add(condition);

				setOfAbstractContainmentCtrl.add(targetControl.getParentContainer());

				controlVsAction.put(targetControl, action);
			}
		}
		updateSkipLogicForAbstractContainmentControl(catAttrVsConditionStatements,
				setOfAbstractContainmentCtrl, controlVsAction);

		return catAttrVsConditionStatements;
	}

	private void updateSkipLogicForAbstractContainmentControl(
			Map<ControlInterface, ConditionStatements> catAttrVsConditionStatements,
			Set<ContainerInterface> setOfAbstractContainmentCtrl,
			Map<ControlInterface, Action> controlVsAction)
	{
		for (ContainerInterface container : setOfAbstractContainmentCtrl)
		{
			int showAction = 0;
			int hideAction = 0;
			for (ControlInterface control : container.getAllControlsUnderSameDisplayLabel())
			{
				Action action = controlVsAction.get(control);
				if (action instanceof ShowAction)
				{
					showAction++;
				}
				else if (action instanceof HideAction)
				{
					hideAction++;
				}
			}
			int numberOfControls = container.getAllControlsUnderSameDisplayLabel().size();
			CategoryEntityInterface parentCategoryEntity = ((CategoryEntityInterface) container
					.getAbstractEntity()).getTreeParentCategoryEntity();
			if ((showAction == numberOfControls || hideAction == numberOfControls)
					&& parentCategoryEntity.getTreeParentCategoryEntity() == null)
			{
				createSkipLogicForContainmentControl(catAttrVsConditionStatements, container);
			}
		}
	}

	private void createSkipLogicForContainmentControl(
			Map<ControlInterface, ConditionStatements> catAttrVsConditionStatements,
			ContainerInterface container)
	{
		ControlInterface control = container.getAllControlsUnderSameDisplayLabel().iterator()
				.next();
		ContainerInterface parentContainer = container.getParentContainer(container);
		for (ControlInterface controlInterface : parentContainer
				.getAllControlsUnderSameDisplayLabel())
		{
			if (controlInterface instanceof CategoryAssociationControlInterface)
			{
				CategoryAssociationControlInterface assoControl = (CategoryAssociationControlInterface) controlInterface;
				if (assoControl.getContainer().getAbstractEntity().equals(
						container.getAbstractEntity()))
				{
					catAttrVsConditionStatements.put(controlInterface, catAttrVsConditionStatements
							.get(control));
					controlInterface.setIsSkipLogicTargetControl(Boolean.TRUE);
					break;
				}
			}
		}
	}

	/**
	 * Populate control identifier in skip logic.
	 * @param controlVsConditionStatements the skip logic vs control
	 * @return the map< container interface, skip logic>
	 */
	public static Map<ContainerInterface, SkipLogic> populateControlIdentifierInSkipLogic(
			Map<ControlInterface, ConditionStatements> controlVsConditionStatements)
	{
		Map<ContainerInterface, SkipLogic> containerVsSkipLogic = new HashMap<ContainerInterface, SkipLogic>();
		if (controlVsConditionStatements != null)
		{
			Set<Entry<ControlInterface, ConditionStatements>> entrySetObject = controlVsConditionStatements
					.entrySet();
			for (Entry<ControlInterface, ConditionStatements> entry : entrySetObject)
			{
				ContainerInterface currentContainer = entry.getKey().getParentContainer();
				if (containerVsSkipLogic.get(currentContainer) == null)
				{
					SkipLogic skipLogic = new SkipLogic();
					skipLogic.setListOfconditionStatements(new HashSet<ConditionStatements>());
					skipLogic.setContainerIdentifier(currentContainer.getId());
					containerVsSkipLogic.put(currentContainer, skipLogic);
				}
				ConditionStatements statement = entry.getValue();
				statement.setControlIdentifier(entry.getKey().getId());
				containerVsSkipLogic.get(currentContainer).getListOfconditionStatements().add(
						statement);
			}
		}
		return containerVsSkipLogic;
	}

	/**
	 * Gets the action object depending on action type.
	 * @param targetControl the target control
	 * @return the action object depending on action type
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	private Action getActionObjectDependingOnActionType(ControlInterface targetControl)
			throws DynamicExtensionsSystemException
	{
		Action action;
		if (fileParser.checkPermissibleValuePresent())
		{
			Set<String> allPVs = fileParser.getPermissibleValuesOfDependentAttribute();
			action = skipLogicHelper.getPermissibleValueAction(allPVs, targetControl);
		}
		else
		{
			try
			{
				action = setAction(targetControl);
			}
			catch (ParseException e)
			{
				LOGGER.error("Error occured while Parsing default value");
				throw new DynamicExtensionsSystemException(
						"Error occured while Parsing default value", e);
			}
		}
		action.setControl(targetControl);
		return action;
	}

	/**
	 * This method sets the action to be performed by the dependent as it is
	 * mentioned in the CSV file.
	 * @param targetControl
	 * @return the action
	 * @throws DynamicExtensionsSystemException
	 * @throws ParseException
	 */
	private Action setAction(ControlInterface targetControl) throws ParseException
	{
		Action action = null;
		Map<String, String> options = fileParser.getControlOptions();
		Set<Entry<String, String>> entryObject = options.entrySet();

		for (Entry<String, String> entry : entryObject)
		{
			action = defineAction(entry);
		}

		if (fileParser.getDefaultValue() != null)
		{
			PermissibleValueInterface defaultValue = SkipLogicUtility.getDefaultValueForControl(
					fileParser.getDefaultValue(), targetControl);
			action.setDefaultSkipLogicValue(defaultValue);
		}
		return action;
	}

	/**
	 * Define action.
	 * @param entry the entry
	 * @return the action
	 */
	private Action defineAction(Entry<String, String> entry)
	{
		Action action;
		if (CategoryConstants.SELECTIVE_READ_ONLY.equalsIgnoreCase(entry.getKey()))
		{
			if (CategoryConstants.TRUE.equalsIgnoreCase(entry.getValue()))
			{
				action = new EnableAction();
			}
			else
			{
				action = new DisableAction();
			}
		}
		// else it has to be Show hide case.("IsShowHide".equalsIgnoreCase(entry.getKey()))
		else
		{
			if (CategoryConstants.TRUE.equalsIgnoreCase(entry.getValue()))
			{
				action = new ShowAction();
			}
			else
			{
				action = new HideAction();
			}
		}
		return action;
	}

	/**
	 * This method gets the Control object for the target attribute.
	 * @param categoryPathInformation the category path information
	 * @param skipLogicHelper the skip logic helper
	 * @return the target control
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	private List<ControlInterface> getTargetControl(String[] categoryPathInformation,
			PopulateSkipLogicHelper skipLogicHelper) throws DynamicExtensionsSystemException
	{
		final String targetAttributeClassName = fileParser.getSkipLogicTargetAttributeClassName();

		final String targetAttributeName = fileParser.getSkipLogicTargetAttributeName();

		final String targetCategoryAttributePath = getCategoryAttributePath(
				categoryPathInformation, targetAttributeClassName);

		return skipLogicHelper.getCategoryAttributeControl(targetCategoryAttributePath,
				targetAttributeName, entityNameVsListOfAsso);
	}

	/**
	 * This method returns the Category Attribute object for Source/Controlling
	 * attribute.
	 * @param categoryPathInformation the category path information
	 * @param skipLogicHelper the skip logic helper
	 * @return the source control
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	private CategoryAttributeInterface getconditionCategoryAttribute(
			String[] categoryPathInformation, PopulateSkipLogicHelper skipLogicHelper)
			throws DynamicExtensionsSystemException
	{
		final String sourceAttributeClassName = fileParser.getSkipLogicSourceAttributeClassName();

		final String sourceAttributeName = fileParser.getSkipLogicSourceAttributeName();

		final String sourceCategoryAttributePath = getCategoryAttributePath(
				categoryPathInformation, sourceAttributeClassName);

		// set the property isSkipLogic of control for Controlling attribute.
		final List<ControlInterface> allControls = skipLogicHelper.getCategoryAttributeControl(
				sourceCategoryAttributePath, sourceAttributeName, entityNameVsListOfAsso);
		final ControlInterface control = allControls.get(0);

		control.setIsSkipLogic(Boolean.TRUE);

		return skipLogicHelper.getConditionCategoryAttribute(sourceCategoryAttributePath,
				sourceAttributeName, entityNameVsListOfAsso);
	}

	/**
	 * This method returns the complete path defined for the category attribute.
	 * @param categoryPaths the category paths
	 * @param className the class name
	 * @return the category attribute path
	 */
	private String getCategoryAttributePath(final String[] categoryPaths, final String className)
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
}
