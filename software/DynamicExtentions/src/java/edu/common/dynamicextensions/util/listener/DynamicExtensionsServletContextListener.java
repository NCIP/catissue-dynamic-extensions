
package edu.common.dynamicextensions.util.listener;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.Variables;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 *
 * @author sujay_narkar
 *
 * */
public class DynamicExtensionsServletContextListener implements ServletContextListener
{

	/**
	 * @param sce : Servlet Context Event
	 */
	public void contextInitialized(ServletContextEvent sce)
	{

		String propDirPath=sce.getServletContext().getRealPath("WEB-INF")
		+ System.getProperty("file.separator") + "classes";



		/**
		 * Configuring the Logger class so that it can be utilized by
		 * the entire application
		 */

		LoggerConfig.configureLogger(propDirPath);
		try
		{
			ErrorKey.init("~");

		}
		catch (Exception ex)
		{
			Logger.out.error(ex.getMessage(), ex);
		}
		String resourceBundleKey = sce.getServletContext().getInitParameter(
		"ResourceBundleParamName");
		if(resourceBundleKey==null || resourceBundleKey.trim().equals(""))
		{
			resourceBundleKey="resourcebundleclass";
		}
		/**
		 * Getting Application Properties file path
		 */
		String applicationResourcesPath = propDirPath+ System.getProperty("file.separator")
				+ sce.getServletContext().getInitParameter(resourceBundleKey)+".properties";
		/**
		 * Initializing ApplicationProperties with the class
		 * corresponding to resource bundle of the application
		 */
		ApplicationProperties.initBundle(sce.getServletContext().getInitParameter(resourceBundleKey));

		/**
		 * Getting and storing Home path for the application
		 */
		Variables.dynamicExtensionsHome = sce.getServletContext().getRealPath("");
		CommonServiceLocator.getInstance().setAppHome(sce.getServletContext().getRealPath(""));

		/**
		 * Creating Logs Folder inside catissue home
		 */
		File logfolder = null;
		logfolder = new File(Variables.dynamicExtensionsHome + "/Logs");
		if (!logfolder.exists())
		{
			logfolder.mkdir();
		}

		/**
		 * setting system property catissue.home which can be utilized
		 * by the Logger for creating log file
		 */
		System.setProperty("dynamicExtensions.home", Variables.dynamicExtensionsHome + "/Logs");

		Logger.out.info(ApplicationProperties.getValue("dynamicExtensions.home")
				+ Variables.dynamicExtensionsHome);
		Logger.out.info(ApplicationProperties.getValue("logger.conf.filename")
				+ applicationResourcesPath);

		//QueryBizLogic.initializeQueryData();

		DynamicExtensionsUtility.initialiseApplicationVariables();
		DynamicExtensionsUtility.validateGivenDatePatterns();
		DynamicExtensionsUtility.initialiseApplicationInfo();
		Logger.out.info("DynamicExtensionsServletContextListener before Initialising the Cache.");
		EntityCache.getInstance();
		Logger.out.info("DynamicExtensionsServletContextListener after Initialising the Cache.");
	}

	/**
	 * @param sce Servlet Context Object
	 */
	public void contextDestroyed(ServletContextEvent sce)
	{
		//
	}
}