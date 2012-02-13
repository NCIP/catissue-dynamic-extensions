/**
 *
 */
package edu.common.dynamicextensions.skiplogic;

import java.util.Collection;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;


/**
 * @author Gaurav_mehta
 *
 */
public class SkipLogic
{

	/** The identifier. */
	private Long identifier;

	/** The container identifier. */
	private Long containerIdentifier;

	/** The list of actions. */
	private Collection<ConditionStatements> listOfconditionStatements;

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
	 * Evaluate skip logic.
	 * @param controllingContainer the container
	 * @param mainContainer
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException 
	 */
	public void evaluateSkipLogic(ContainerInterface controllingContainer, Map<BaseAbstractAttributeInterface, Object> dataValueMap) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		for (ConditionStatements conditionStatement : listOfconditionStatements)
		{
			conditionStatement.evaluateConditions(dataValueMap,controllingContainer);
		}
	}

	/**
	 * @param containerIdentifier the containerIdentifier to set
	 */
	public void setContainerIdentifier(Long containerIdentifier)
	{
		this.containerIdentifier = containerIdentifier;
	}

	/**
	 * @return the containerIdentifier
	 */
	public Long getContainerIdentifier()
	{
		return containerIdentifier;
	}

	/**
	 * @param listOfconditionStatements the listOfconditionStatements to set
	 */
	public void setListOfconditionStatements(Collection<ConditionStatements> listOfconditionStatements)
	{
		this.listOfconditionStatements = listOfconditionStatements;
	}

	/**
	 * @return the listOfconditionStatements
	 */
	public Collection<ConditionStatements> getListOfconditionStatements()
	{
		return listOfconditionStatements;
	}
}
