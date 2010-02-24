/**
 *
 */

package edu.wustl.metadata.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

import edu.wustl.cab2b.common.cache.AbstractEntityCache;

/**
 * @author gaurav_mehta
 *
 */
@SuppressWarnings("deprecation")
public final class JbossPropertyLoader
{

	private static final long serialVersionUID = 1234567890L;

	private static final Logger LOGGER = edu.wustl.common.util.logger.Logger
			.getLogger(AbstractEntityCache.class);

	private static JbossPropertyLoader propertyLoader;

	private JbossPropertyLoader()
	{
	}

	public synchronized static JbossPropertyLoader getInstance()
	{
		if (propertyLoader == null)
		{
			propertyLoader = new JbossPropertyLoader();
		}
		return propertyLoader;
	}

	/**
	 * @param propertyfile
	 * @return Property
	 */
	public static Properties getPropertiesFromFile(String propertyfile)
	{
		Properties properties = new Properties();
		String jbossConfLocation = System.getProperty("jboss.server.config.url");
		try
		{
			URL url = new URL(jbossConfLocation + File.separator + propertyfile);
			InputStream input = url.openStream();
			if (input == null)
			{
				LOGGER.error("Unable fo find property file : " + propertyfile
						+ "\n please put this file in JBoss Conf Folder");
			}
			properties.load(input);
		}
		catch (IOException e)
		{
			LOGGER.error("Unable to load properties from : " + propertyfile);
		}
		return properties;
	}
}
