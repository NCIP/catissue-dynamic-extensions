/**
 *
 */

package edu.common.dynamicextensions.util.parser;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DirOperationsUtility;
import edu.common.dynamicextensions.util.ZipUtility;
import edu.common.dynamicextensions.utility.HTTPSConnection;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * @author gaurav_mehta
 *
 */
public class DynamicallyImportPermissibleValues
{

	private static File pvZip;

	private static URL serverConnURL;

	public static String pvFileName = "pvFileName";

	public static String startFolder = "startFolder";

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	private static final Logger LOGGER = Logger
			.getCommonLogger(DynamicallyImportPermissibleValues.class);

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		DynamicallyImportPermissibleValues dynamicPVImport = new DynamicallyImportPermissibleValues();
		dynamicPVImport.importPermissibleValues(args);
	}

	private void importPermissibleValues(String[] args)
	{
		HTTPSConnection httpsConnection = HTTPSConnection.getInstance();

		try
		{
			// trust all the https connections
			httpsConnection.acceptAllHttpsConnections();
			LOGGER.info("Connections trusted");

			//System.out.println("Connection Successfull");

			//create required artifacts to be uploaded to server
			createArtifacts(args);
			LOGGER.info("Artifacts Created");

			// open the servlet connection
			URLConnection servletConnection = httpsConnection.openServletConnection(serverConnURL);
			LOGGER.info("Connections Established");

			// upload the Zip file to server
			httpsConnection.uploadFileToServer(servletConnection, pvZip);
			LOGGER.info("Artifacts uploaded");

			// read the response from server
			httpsConnection.processResponse(servletConnection);
		}
		catch (DynamicExtensionsSystemException e)
		{
			LOGGER.error("Exception : " + e.getLocalizedMessage());
			LOGGER.info("For more information please check :/log/dynamicExtentions.log");
			LOGGER.debug("Exception occured is as follows : ", e);
		}
		catch (IOException e)
		{
			LOGGER.error("Exception : " + e.getLocalizedMessage());
			LOGGER.info("For more information please check :/log/dynamicExtentions.log");
			LOGGER.debug("Exception occured is as follows : ", e);
		}

	}

	private void createArtifacts(String[] args) throws DynamicExtensionsSystemException,
			IOException, MalformedURLException
	{
		String pvFileName = args[0];
		boolean fileNameNotPResent = false;
		if (pvFileName == null || pvFileName.length() == 0)
		{
			fileNameNotPResent = true;
		}
		String pvDir = args[1];
		LOGGER.info("PV Dir path:" + pvDir);
		String folderName = pvDir.substring((pvDir.lastIndexOf('/') + 1), pvDir.length());
		//validate size of the folder is less than 500MB
		DirOperationsUtility.validateFolderSizeForUpload(pvDir, 500000000L);
		pvZip = ZipUtility.zipFolder(pvDir, "ImportPVDir.zip");

		StringBuffer url = new StringBuffer(args[2] + "/ImportPVAction.do?");
		url.append(startFolder);
		url.append('=');
		url.append(folderName);
		if (!fileNameNotPResent)
		{
			url.append('&');
			url.append(DynamicallyImportPermissibleValues.pvFileName);
			url.append('=');
			url.append(pvFileName);
		}

		//System.out.println(url.toString());
		serverConnURL = new URL(url.toString());
	}
}
