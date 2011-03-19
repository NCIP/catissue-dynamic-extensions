/**
 *
 */

package edu.common.dynamicextensions.skiplogic;

import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

/**
 * The Class OperatorCondition.
 *
 * @author Gaurav_mehta
 */
public class GroupCondition implements Condition
{

	/** The identifier. */
	private Long identifier;

	/** The list of conditions. */
	private Set<Condition> listOfConditions;

	/** The operator condition. */
	private LogicalOperator logicalOperator;

	/** The action. */
	private Action action;

	/**
	 * Sets the identifier.
	 * @param identifier the new identifier
	 */
	public void setIdentifier(Long identifier)
	{
		this.identifier = identifier;
	}

	/**
	 * Gets the identifier.
	 * @return the identifier
	 */
	public Long getIdentifier()
	{
		return identifier;
	}

	/**
	 * Check condition.
	 * @param objectValueState the object value state
	 * @return true, if check condition
	 * @throws DynamicExtensionsSystemException
	 */
	public boolean checkCondition(Map<BaseAbstractAttributeInterface, Object> objectValueState,
			ContainerInterface controllingContainer) throws DynamicExtensionsSystemException
	{
		boolean result = false;
		LogicalOperator and = getLogicalOperator();
		for (Condition condition : listOfConditions)
		{
			if (and.equals(LogicalOperator.AND))
			{
				if (Boolean.FALSE.equals(condition.checkCondition(objectValueState,
						controllingContainer)))
				{
					break;
				}
			}
			else
			{
				if (Boolean.TRUE.equals(condition.checkCondition(objectValueState,
						controllingContainer)))
				{
					result = true;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Gets the list of conditions.
	 * @return the listOfConditions
	 */
	public Set<Condition> getListOfConditions()
	{
		return listOfConditions;
	}

	/**
	 * Sets the list of conditions.
	 * @param listOfConditions the listOfConditions to set
	 */
	public void setListOfConditions(Set<Condition> listOfConditions)
	{
		this.listOfConditions = listOfConditions;
	}

	/**
	 * Gets the operator condition.
	 * @return the operatorCondition
	 */
	public LogicalOperator getLogicalOperator()
	{
		return logicalOperator;
	}

	/**
	 * Sets the operator condition.
	 * @param operatorCondition the operatorCondition to set
	 */
	public void setLogicalOperator(LogicalOperator logicalOperator)
	{
		this.logicalOperator = logicalOperator;
	}

	/**
	 * Sets the action.
	 * @param action the new action
	 */
	public void setAction(Action action)
	{
		this.action = action;
	}

	/**
	 * Gets the action.
	 * @return the action
	 */
	public Action getAction()
	{
		return action;
	}

}
