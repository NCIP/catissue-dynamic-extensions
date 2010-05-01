package edu.common.dynamicextensions.dao.impl;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

/**
 *
 * @author ravi_kumar
 *
 */
public interface IDEDBUtility
{
	/**
	 * @param strDate date as string.
	 * @param removeTime true if time part has to remove
	 * @return formatted date.
	 */
	String formatMonthAndYearDate(String strDate,boolean removeTime);

	/**
	 * @param strDate date as string.
	 * @param removeTime true if time part has to remove
	 * @return formatted date.
	 */
	String formatYearDate(String strDate,boolean removeTime);

	/**
	 * @param ischecked  return 0 or 1 depending on boolean value passed.
	 * @return 0/1 or true/false for different databases.
	 */
	String getValueForCheckBox(boolean ischecked);
	
	
	/**
	 * method to clean database.
	 * @param args argument from main method.
	 * @throws DynamicExtensionsSystemException if database clean up fails.
	 */
	void cleanDatabase(String []args) throws DynamicExtensionsSystemException;
}
