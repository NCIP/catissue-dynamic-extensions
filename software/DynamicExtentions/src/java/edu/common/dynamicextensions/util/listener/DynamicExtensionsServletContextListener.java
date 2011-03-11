
package edu.common.dynamicextensions.util.listener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;

import edu.common.dynamicextensions.dao.impl.DynamicExtensionDAO;
import edu.common.dynamicextensions.domain.userinterface.SelectControl;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.Variables;
import edu.common.dynamicextensions.validation.DateValidator;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.exception.AuditException;

/**
 *
 * @author sujay_narkar
 *
 * */
public class DynamicExtensionsServletContextListener implements ServletContextListener
{
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(DynamicExtensionsServletContextListener.class);

	/**
	 * @param sce : Servlet Context Event
	 */
	public void contextInitialized(ServletContextEvent sce)
	{

		try
		{
			AuditManager.init("DynamicExtensionsAuditMetadata.xml");
		}
		catch (AuditException ex)
		{
			LOGGER.error(ex.getMessage(), ex);
		}

		try
		{
			InputStream stream = DynamicExtensionDAO.class.getClassLoader().getResourceAsStream(
			"DynamicExtension.properties");
			Properties props = new Properties();
			props.load(stream);
			DynamicExtensionsUtility.initializeVariables(props);
			stream.close();
		}
		catch (IOException e)
		{
			LOGGER.error("Cound not found DynamicExtension.properties.", e);
		}

		String propDirPath = sce.getServletContext().getRealPath("WEB-INF")
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
			LOGGER.error(ex.getMessage(), ex);
		}
		String resourceBundleKey = sce.getServletContext().getInitParameter(
				"ResourceBundleParamName");
		if (resourceBundleKey == null || resourceBundleKey.trim().equals(""))
		{
			resourceBundleKey = "resourcebundleclass";
		}
		/**
		 * Getting Application Properties file path
		 */
		String applicationResourcesPath = propDirPath + System.getProperty("file.separator")
				+ sce.getServletContext().getInitParameter(resourceBundleKey) + ".properties";
		/**
		 * Initializing ApplicationProperties with the class
		 * corresponding to resource bundle of the application
		 */
		ApplicationProperties.initBundle(sce.getServletContext()
				.getInitParameter(resourceBundleKey));

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

		LOGGER.info(ApplicationProperties.getValue("dynamicExtensions.home")
				+ Variables.dynamicExtensionsHome);
		LOGGER.info(ApplicationProperties.getValue("logger.conf.filename")
				+ applicationResourcesPath);

		//QueryBizLogic.initializeQueryData();
		String comboQueryChar = ApplicationProperties
				.getValue("combo.minchar.query");
		if (!"".equals(comboQueryChar) && StringUtils.isNumeric(comboQueryChar))
		{
			SelectControl.minQueryChar = Integer.valueOf(comboQueryChar);
		}
		DynamicExtensionsUtility.initialiseApplicationVariables();
		DynamicExtensionsUtility.initialiseApplicationInfo();
		DateValidator.validateGivenDatePatterns();
		LOGGER.info("DynamicExtensionsServletContextListener before Initialising the Cache.");
		EntityCache.getInstance();
		EntityCache.getInstance().loadCategories();
		LOGGER.info("DynamicExtensionsServletContextListener after Initialising the Cache.");
	}

	/**
	 * @param sce Servlet Context Object
	 */
	public void contextDestroyed(ServletContextEvent sce)
	{
		//
	}
}