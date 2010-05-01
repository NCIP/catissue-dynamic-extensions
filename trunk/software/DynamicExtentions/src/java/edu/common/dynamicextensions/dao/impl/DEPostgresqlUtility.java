package edu.common.dynamicextensions.dao.impl;

import edu.wustl.common.util.logger.Logger;


/**
 * @author ravi_kumar
 *
 */
public class DEPostgresqlUtility extends AbstractDEDBUtility
{

	/**
	 * @param ischecked  return true or false depending on boolean value passed.
	 * @return isChecked true/false
	 */
	@Override
	public String getValueForCheckBox(boolean ischecked)
	{
		return ischecked?"true":"false";
	}
	
	/**
	 * method to clean database.
	 * @param args argument from main method.
	 */
	@Override
	public void cleanDatabase(String []args)
	{
		Logger.out.info("No implementation for this method for postgresql database.");
	}
}
