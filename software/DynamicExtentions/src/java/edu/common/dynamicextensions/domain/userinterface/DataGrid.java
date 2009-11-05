
package edu.common.dynamicextensions.domain.userinterface;

import java.util.List;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.DataGridInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:07 PM
 * @hibernate.joined-subclass table="DYEXTN_DATA_GRID"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class DataGrid extends Control implements DataGridInterface
{

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface#setAttribute(edu.common.dynamicextensions.domaininterface.AttributeInterface)
	 */
	public void setAttribute(AbstractAttributeInterface attributeInterface)
	{
		// TODO Auto-generated method stub
	}

	protected String generateViewModeHTML(ContainerInterface container) throws DynamicExtensionsSystemException
	{
		// TODO Auto-generated method stub
		return "";
	}

	protected String generateEditModeHTML(ContainerInterface container) throws DynamicExtensionsSystemException
	{
		// TODO Auto-generated method stub
		return "&nbsp;";
	}

	/**
	 *
	 */
	public List<String> getValueAsStrings() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *
	 */
	public void setValueAsStrings(List<String> listOfValues)
	{
		// TODO Auto-generated method stub

	}
	/**
	 *
	 */
	public boolean getIsEnumeratedControl()
	{
		return false;
	}
}