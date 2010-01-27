/**
 *
 */

package edu.common.dynamicextensions.xmi.model;

import edu.common.dynamicextensions.ui.interfaces.AbstractAttributeUIBeanInterface;
import edu.common.dynamicextensions.ui.interfaces.ControlUIBeanInterface;
import edu.common.dynamicextensions.ui.webui.util.CommonControlModel;

/**
 * @author ashish_gupta
 *
 */
public class ControlsModel extends CommonControlModel
		implements
			ControlUIBeanInterface,
			AbstractAttributeUIBeanInterface
{

	/**
	 *
	 */
	protected Boolean isPrimaryKey = false;

	/**
	 *
	 */
	protected Boolean isNullable = true;

	/**
	 *
	 */
	protected String columnName;

	/**
	 *
	 * @return boolean is primary key
	 */
	public Boolean getIsPrimaryKey()
	{
		return isPrimaryKey;
	}

	/**
	 * Sets the isPrimaryKey To the given parameter
	 * @param isPrimaryKey
	 */
	public void setIsPrimaryKey(Boolean isPrimaryKey)
	{
		this.isPrimaryKey = isPrimaryKey;
	}

	/**
	 *
	 * @return boolean isNullable
	 */
	public Boolean getIsNullable()
	{
		return isNullable;
	}

	/**
	 * Sets the isNullable
	 * @param isNullable
	 */
	public void setIsNullable(Boolean isNullable)
	{
		this.isNullable = isNullable;
	}

	/**
	 *
	 */
	public String getColumnName()
	{
		return columnName;
	}

	/**
	 *
	 * @param columnName
	 */
	public void setColumnName(String columnName)
	{
		this.columnName = columnName;
	}

}
