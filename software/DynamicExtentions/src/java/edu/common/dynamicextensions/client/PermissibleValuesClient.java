/**
 *
 */

package edu.common.dynamicextensions.client;

import java.io.IOException;
import java.net.URL;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DirOperationsUtility;
import edu.common.dynamicextensions.util.ZipUtility;
import edu.common.dynamicextensions.utility.HTTPSConnection;

/**
 * @author gaurav_mehta
 *
 */
public class PermissibleValuesClient extends AbstractClient
{

	public static String pvFileName = "pvFileName";

	public static String startFolder = "startFolder";

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		PermissibleValuesClient dynamicPVImport = new PermissibleValuesClient();
		dynamicPVImport.execute(args);
	}

	protected void initializeResources(String[] args) throws DynamicExtensionsSystemException,
			IOException
	{
		String pvDir = args[1];
		LOGGER.info("PV Dir path:" + pvDir);
		String folderName = pvDir.substring((pvDir.lastIndexOf('/') + 1), pvDir.length());
		//validate size of the folder is less than 500MB
		DirOperationsUtility.validateFolderSizeForUpload(pvDir, 500000000L);
		zipFile = ZipUtility.zipFolder(pvDir, "ImportPVDir.zip");

		StringBuffer url = new StringBuffer(HTTPSConnection.getCorrectedApplicationURL(args[2])
				+ "/ImportPVAction.do?");
		url.append(startFolder);
		url.append('=');
		url.append(folderName);
		if (args[0] != null && !"".equals(args[0]))
		{
			url.append('&');
			url.append(PermissibleValuesClient.pvFileName);
			url.append('=');
			url.append(args[0]);
		}

		//System.out.println(url.toString());
		serverUrl = new URL(url.toString());
	}

	/**
	 * It will validate weather the correct number of arguments are passed or not & then throw exception accordingly.
	 * @param args arguments
	 * @throws DynamicExtensionsSystemException exception
	 */
	protected void validate(String args[]) throws DynamicExtensionsSystemException
	{
		if (args.length == 0)
		{
			throw new DynamicExtensionsSystemException("Please specify PV files folder path.");
		}
		if (args.length < 2)
		{
			throw new DynamicExtensionsSystemException("Please specify the AppplicationURL.");
		}
		boolean isPathUnavalable = (args[0] != null && args[0].trim().length() == 0);
		if (isPathUnavalable)
		{
			throw new DynamicExtensionsSystemException("Please specify PV files folder path.");
		}
		boolean isUrlUnavalable = (args[1] != null && args[1].trim().length() == 0);
		if (isUrlUnavalable)
		{
			throw new DynamicExtensionsSystemException("Please specify the  AppplicationURL.");
		}
	}
}
