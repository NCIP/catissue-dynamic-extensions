package edu.wustl.cab2b.common.util;

import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * This class handles fetching properties from cab2b.properties file
 *
 * @author Chandrakant_Talele
 * @author lalit_chand
 */
public final class CommonPropertyLoader
{

    /**
     * Instantiates a new common property loader.
     */
    private CommonPropertyLoader()
    {
        // private constructor
    }

    /**
     * The Constant LOGGER.
     */
    private static final Logger LOGGER = edu.wustl.common.util.logger.Logger
            .getLogger(CommonPropertyLoader.class);

    /**
     * The Constant PROPERTIES_FILE_NAME.
     */
    private static final String PROPERTIES_FILE_NAME = "cab2b.properties";

    /**
     * The props.
     */
    private static Properties props = Utility
            .getPropertiesFromFile(PROPERTIES_FILE_NAME);

    /**
     * Returns the Path of domain model XML file.
     *
     * @param applicationName
     *            Name of the application
     * @return Returns the File Path
     */
    public static String getModelPath(String applicationName)
    {
        String path = props.getProperty(applicationName + ".ModelPath");
        if (path == null || path.length() == 0)
        {
            LOGGER.error("Model path for application : " + applicationName
                    + " is not configured in " + PROPERTIES_FILE_NAME);
        }
        return path;
    }

    /**
     * Returns names of all application for which caB2B is configured.
     *
     * @return Returns the Application Names
     */
    public static String[] getAllApplications()
    {
        String[] allApplications = props.getProperty("all.applications").split(
                ",");
        if (allApplications == null || allApplications.length == 0)
        {
            LOGGER.error("No value for key 'all.applications' is found in "
                    + PROPERTIES_FILE_NAME);
        }

        return allApplications;
    }

    /**
     * Gets the jndi url.
     *
     * @return The URL of JNDI service running on caB2B server
     */
    public static String getJndiUrl()
    {
        String serverIP = props.getProperty("caB2B.server.ip");
        String jndiPort = props.getProperty("caB2B.server.port");
        return "jnp://" + serverIP + ":" + jndiPort;
    }

    /**
     * Gets the index service urls.
     *
     * @return all the index urls used to get the service information
     */
    public static String[] getIndexServiceUrls()
    {
        String allUrls = props.getProperty("indexurls");
        return allUrls.split(",");
    }

    /**
     * Gets the sync des file.
     *
     * @param gridType
     *            the grid type
     * @return returns the sys-description file for GTS
     */

    public static String getSyncDesFile(String gridType)
    {
        return props.getProperty(gridType + "_sync_description_file");
    }

    /**
     * Gets the signing policy.
     *
     * @param gridType
     *            the grid type
     * @return signing policy for given idP
     */
    public static String getSigningPolicy(String gridType)
    {
        return props.getProperty(gridType + "_signing_policy");
    }

    /**
     * Gets the certificate.
     *
     * @param gridType
     *            the grid type
     * @return certificate for given idP
     */
    public static String getCertificate(String gridType)
    {
        return props.getProperty(gridType + "_certificate");
    }

}
