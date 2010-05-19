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

	/**
	 * Constant
	 */
	public static String pvFileName = "pvFileName";

	/**
	 * Constant
	 */
	public static String startFolder = "startFolder";

	/**
	 * amin method
	 * @param args arguments array.
	 */
	public static void main(String[] args)
	{
		PermissibleValuesClient dynamicPVImport = new PermissibleValuesClient();
		dynamicPVImport.execute(args);
	}

	/**
	 * It will validate all the necessary parameters are provided & are valid.
	 * If all are valid it will also intialize all the instance variables also.
	 * @param args the arguments which are to be validated & initialize instance varibles from
	 * these arguments
	 * @throws IOException exception
	 * @throws Exception exception
	 */
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
	 * It will validate weather the correct number of arguments are passed or
	 * not & then throw exception accordingly.
	 * @param args arguments
	 * @throws DynamicExtensionsSystemException exception
	 */
	protected void validate(String[] args) throws DynamicExtensionsSystemException
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
