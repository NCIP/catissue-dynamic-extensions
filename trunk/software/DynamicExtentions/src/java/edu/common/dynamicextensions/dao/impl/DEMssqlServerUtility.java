package edu.common.dynamicextensions.dao.impl;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DatabaseCleaner;



/**
 * @author ravi_kumar
 *
 */
public class DEMssqlServerUtility extends AbstractDEDBUtility
{

	/**
	 * method to clean database.
	 * @param args argument from main method.
	 * @throws DynamicExtensionsSystemException if database clean up fails.
	 */
	@Override
	public void cleanDatabase(String []args)throws DynamicExtensionsSystemException
	{
		DatabaseCleaner.cleanMsSqlServer(args);
	}
}
