/**
 * 
 */

package edu.common.dynamicextensions.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import edu.wustl.common.util.logger.Logger;

/**
 * This class is a used to set different parameter like application name,
 *  configure in DynamicExtensionDAO.properties file.
 * @author suhas_khot
 *
 */
public final class DynamicExtensionDAO
{

	/**
	 * object of class DynamicExtensionDAO.
	 */
	private static DynamicExtensionDAO dynamicExtnDAO = new DynamicExtensionDAO();

	/**
	 * Application Name used for getting DAO.
	 */
	private String appName;

	/**
	 *No argument constructor.
	 *Here all the properties are set
	 */
	private DynamicExtensionDAO()
	{
		initProps();
	}

	/**
	 * This method return object of the class CommonServiceLocator.
	 * @return object of the class CommonServiceLocator.
	 */
	public static DynamicExtensionDAO getInstance()
	{
		return dynamicExtnDAO;
	}

	/**
	 * This method loads properties file.
	 */
	private void initProps()
	{
		InputStream stream = DynamicExtensionDAO.class.getClassLoader().getResourceAsStream(
				"DynamicExtensionDAO.properties");
		try
		{
			Properties props = new Properties();
			props.load(stream);
			setAppName(props);
			stream.close();
		}
		catch (IOException exception)
		{
			Logger.out.error("Not able to load properties file", exception);
		}
	}

	/**
	 * @return the application name.
	 */
	public String getAppName()
	{
		return appName;
	}

	/**
	 * Set the application name.
	 * @param props Object of Properties
	 */
	private void setAppName(Properties props)
	{
		appName = props.getProperty("app.name");
	}

}
