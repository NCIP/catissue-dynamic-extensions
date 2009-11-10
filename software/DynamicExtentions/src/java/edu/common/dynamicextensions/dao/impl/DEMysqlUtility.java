package edu.common.dynamicextensions.dao.impl;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.DatabaseCleaner;



/**
 * @author ravi_kumar
 *
 */
public class DEMysqlUtility extends AbstractDEDBUtility
{
	/**
	 * @param strDate date as string.
	 * @param removeTime true if time part has to remove
	 * @return formatted date.
	 */
	@Override
	public String formatMonthAndYearDate(String strDate,boolean removeTime)
	{
		String month = strDate.substring(0, 2);
		String year = strDate.substring(3, strDate.length());
		String formattedDate;
		if(ProcessorConstants.DATE_ONLY_FORMAT.substring(0, 2).equals("MM"))
		{
			formattedDate = month + ProcessorConstants.DATE_SEPARATOR+"01"+ProcessorConstants.DATE_SEPARATOR + year;
		}
		else
		{
			formattedDate = "01"+ProcessorConstants.DATE_SEPARATOR + month + ProcessorConstants.DATE_SEPARATOR + year;
		}
		return formattedDate;
	}
	/**
	 * @param strDate date as string.
	 * @param removeTime true if time part has to remove
	 * @return formatted date.
	 */
	@Override
	public String formatYearDate(String strDate,boolean removeTime)
	{
		return "01" + ProcessorConstants.DATE_SEPARATOR+ "01" + ProcessorConstants.DATE_SEPARATOR + strDate;
	}

	/**
	 * @param ischecked  return 0 or 1 depending on boolean value passed.
	 * @return formatted date.
	 */
	@Override
	public String getValueForCheckBox(boolean ischecked)
	{
		return ischecked?"1":"0";
	}

	/**
	 * method to clean database.
	 * @param args argument from main method.
	 * @throws DynamicExtensionsSystemException if database clean up fails.
	 */
	@Override
	public void cleanDatabase(String []args)throws DynamicExtensionsSystemException
	{
		DatabaseCleaner.cleanMysql(args);
	}
}
