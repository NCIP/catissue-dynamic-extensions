/**
 * 
 */

package edu.common.dynamicextensions.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;

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
	/**
	 * This method adds the DE object.
	 * @param object Object
	 * @return Object object
	 * @throws DynamicExtensionsApplicationException DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException DynamicExtensionsSystemException
	 */
	public Object insertData(Object object) throws DynamicExtensionsApplicationException,
	DynamicExtensionsSystemException
	{
		HibernateDAO hibernateDAO = null;
		try
		{
			hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();
			hibernateDAO.insert(object);
			hibernateDAO.commit();
		}
		catch (Exception e)
		{
			DynamicExtensionsUtility.rollBackDAO(hibernateDAO);
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(hibernateDAO);
		}
		return object;
	}
	/**
	 * This method updates the DE object.
	 * @param object Object
	 * @return Object object
	 * @throws DynamicExtensionsApplicationException DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException DynamicExtensionsSystemException
	 */
	public Object updateData(Object object) throws DynamicExtensionsApplicationException,
	DynamicExtensionsSystemException
	{
		HibernateDAO hibernateDAO = null;
		try
		{
			hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();
			hibernateDAO.update(object);
			hibernateDAO.commit();
		}
		catch (Exception e)
		{
			DynamicExtensionsUtility.rollBackDAO(hibernateDAO);
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(hibernateDAO);
		}
		return object;
	}
}
