/**
 *
 */
package edu.common.dynamicextensions.skiplogic;

import java.util.Set;

import edu.common.dynamicextensions.domain.userinterface.SelectControl;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.ui.util.ControlsUtility;


/**
 * @author Gaurav_mehta
 *
 */
public class PermissibleValueAction implements Action
{

	/** The identifier. */
	private Long identifier;

	/** The list of permissible values. */
	private Set<PermissibleValueInterface> listOfPermissibleValues;

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
	 * @param container the container
	 */
	public void performAction(ControlInterface control)
	{
		SelectControl selectControl = (SelectControl) control;
		selectControl.setOptionList(ControlsUtility.getPermissibleValues(listOfPermissibleValues, control.getAttibuteMetadataInterface()));
		boolean valuePresent = false;
		for (PermissibleValueInterface pvs : listOfPermissibleValues)
		{
			if ((selectControl.getValue() != null )&&(pvs.getValueAsObject().toString().endsWith(selectControl.getValue().toString())))
			{
				valuePresent = true;
				break;
			}
		}
		if(!valuePresent)
		{
			selectControl.setValue("");
		}
	}

	/**
	 * Gets the list of permissible values.
	 * @return the listOfPermissibleValues
	 */
	public Set<PermissibleValueInterface> getListOfPermissibleValues()
	{
		return listOfPermissibleValues;
	}


	/**
	 * Sets the list of permissible values.
	 * @param listOfPermissibleValues the listOfPermissibleValues to set
	 */
	public void setListOfPermissibleValues(Set<PermissibleValueInterface> listOfPermissibleValues)
	{
		this.listOfPermissibleValues = listOfPermissibleValues;
	}

	public void resetAction(ControlInterface controlById)
	{
		SelectControl selectControl = (SelectControl) controlById;
		selectControl.getOptionList().clear();
		if(defaultValue != null)
		{
			CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface)controlById.getAttibuteMetadataInterface();
			categoryAttribute.setDefaultSkipLogicValue(defaultValue);
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
