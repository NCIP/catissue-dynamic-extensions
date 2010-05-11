package edu.common.dynamicextensions.dao.impl;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;

/**
 * @author ravi_kumar
 *
 */
public abstract class AbstractDEDBUtility implements IDEDBUtility
{
    /**
     * The Constant CONST_EMPTY_STRING.
     */
    private static final String CONST_EMPTY_STRING = "";

    /**
     * This default implementation is used for all database(mssql
     * server,db2,postgresql) except mysql
     *
     * @param strDate
     *            date as string.
     * @param removeTime
     *            true if time part has to remove
     * @return formatted date.
     */
    public String formatMonthAndYearDate(String strDate, boolean removeTime)
    {
        String month = strDate.substring(0, 2);
        String year = strDate.substring(3, strDate.length());
        String formattedDate;
        if (ProcessorConstants.DATE_ONLY_FORMAT.substring(0, 2).equals("MM"))
        {
            formattedDate = month + ProcessorConstants.DATE_SEPARATOR + "01"
                    + ProcessorConstants.DATE_SEPARATOR + year + " 0:0";
        }
        else
        {
            formattedDate = "01" + ProcessorConstants.DATE_SEPARATOR + month
                    + ProcessorConstants.DATE_SEPARATOR + year + " 0:0";
        }

        return formattedDate;
    }

    /**
     * This default implementation is used for all database(mssql
     * server,db2,postgresql,oracle) except mysql
     *
     * @param strDate
     *            date as string.
     * @param removeTime
     *            true if time part has to remove
     * @return formatted date.
     */
    public String formatYearDate(String strDate, boolean removeTime)
    {
        return "01" + ProcessorConstants.DATE_SEPARATOR + "01"
                + ProcessorConstants.DATE_SEPARATOR + strDate + " 0:0";
    }

    /**
     * For mssql server and db2 database this method is not defined, hence
     * default implementation.
     *
     * @param ischecked
     *            return 0 or 1 depending on boolean value passed.
     * @return 0/1 or true/false for different databases.
     */
    public String getValueForCheckBox(boolean ischecked)
    {
        // default implementation
        return CONST_EMPTY_STRING;
    }

    /**
     * method to clean database.
     *
     * @param args
     *            argument from main method.
     * @throws DynamicExtensionsSystemException
     *             if database clean up fails.
     */
    abstract public void cleanDatabase(String[] args)
            throws DynamicExtensionsSystemException;
}
