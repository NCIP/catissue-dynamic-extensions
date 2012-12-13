/**
 *
 */
package edu.common.dynamicextensions.skiplogic;

import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;


/**
 * @author Gaurav_mehta
 *
 */
public class DisableAction implements Action
{

	/** The identifier. */
	private Long identifier;

	/** The control identifier. */
	private ControlInterface control;

	/** The default value. */
	private PermissibleValueInterface defaultValue;

	/**
	 * Perform action.
	 * @param container the container
	 */
	public Long getIdentifier()
	{
		return identifier;
	}

	/**
	 * Sets the identifier.
	 * @param identifier the new identifier
	 */
	public void setIdentifier(Long identifier)
	{
		this.identifier = identifier;
	}

	/**
	 * Gets the control identifier.
	 * @return the controlIdentifier
	 */
	public ControlInterface getControl()
	{
		return control;
	}

	/**
	 * Sets the control identifier.
	 * @param controlIdentifier the controlIdentifier to set
	 */
	public void setControl(ControlInterface control)
	{
		this.control = control;
	}

	/**
	 * Perform action.
	 * @param control dependent control
	 */
	public void performAction(ControlInterface controlInterface)
	{
		controlInterface.setValue(null);
		controlInterface.getParentContainer().getContainerValueMap().put(control.getBaseAbstractAttribute(), null);
		controlInterface.setIsReadOnly(true);
		if(defaultValue != null)
		{
			CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface)controlInterface.getAttibuteMetadataInterface();
			categoryAttribute.setDefaultSkipLogicValue(defaultValue);
		}
	}

	/**
	 * @param controlInterface
	 */
	public void resetAction(ControlInterface controlInterface)
	{
		controlInterface.setIsReadOnly(false);
		controlInterface.setValue(null);
		if(defaultValue != null)
		{
			CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface)controlInterface.getAttibuteMetadataInterface();
			categoryAttribute.setDefaultSkipLogicValue(null);
		}
	}

	/**
	 * Sets the default skip logic value.
	 * @param defaultSkipLogicValue the new default skip logic value
	 */
	public void setDefaultSkipLogicValue(PermissibleValueInterface defaultSkipLogicValue)
	{
		defaultValue = defaultSkipLogicValue;
	}

	/**
	 * Sets the default skip logic value.
	 * @param defaultSkipLogicValue the new default skip logic value
	 */
	public PermissibleValueInterface getDefaultSkipLogicValue()
	{
		return defaultValue;
	}


	/**
	 * Gets the default value.
	 * @return the defaultValue
	 */
	public PermissibleValueInterface getDefaultValue()
	{
		return defaultValue;
	}


	/**
	 * Sets the default value.
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(PermissibleValueInterface defaultValue)
	{
		this.defaultValue = defaultValue;
	}

}
