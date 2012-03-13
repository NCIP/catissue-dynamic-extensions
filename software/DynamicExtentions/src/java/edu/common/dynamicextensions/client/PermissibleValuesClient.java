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

	/** The Constant pvFileName. */
	public final static String pvFileName = "pvFileName";

	/** The Constant categoryPVImport. */
	public final static String categoryPVImport = "categoryPVImport";

	/** The Constant startFolder. */
	public final static String startFolder = "startFolder";

	/**
	 * main method
	 * @param args arguments array.
	 */
	public static void main(String[] args)
	{
		PermissibleValuesClient dynamicPVImport = new PermissibleValuesClient();
		dynamicPVImport.execute(args);
	}

	/**
	 * It will validate all the necessary parameters are provided & are valid.
	 * If all are valid it will also initialize all the instance variables also.
	 * @param args the arguments which are to be validated & initialize instance variables from
	 * these arguments
	 * @throws IOException exception
	 * @throws Exception exception
	 */
	protected void initializeResources(String[] args) throws DynamicExtensionsSystemException, IOException
	{
		String pvDir = args[0];
		LOGGER.info("PV Dir path:" + pvDir);
		String folderName = pvDir.substring((pvDir.lastIndexOf('/') + 1), pvDir.length());
		//validate size of the folder is less than 500MB
		DirOperationsUtility.validateFolderSizeForUpload(pvDir, 500000000L);
		zipFile = ZipUtility.zipFolder(pvDir, "ImportPVDir.zip");

		StringBuffer url = new StringBuffer(35);

		url.append(HTTPSConnection.getCorrectedApplicationURL(args[1]));
		url.append("/ImportPVAction?");
		url.append(startFolder);
		url.append('=');
		url.append(folderName);
		if (args.length > 2 && args[2] != null && !"".equals(args[2]))
		{
			url.append('&');
			url.append(PermissibleValuesClient.pvFileName);
			url.append('=');
			url.append(args[2]);
		}
		if (args.length > 3 && args[3] != null && !"".equals(args[3]))
		{
			url.append('&');
			url.append(PermissibleValuesClient.categoryPVImport);
			url.append('=');
			url.append(args[3]);
		}
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
		boolean isPathUnavalable = args[0] != null && args[0].trim().length() == 0;
		if (isPathUnavalable)
		{
			throw new DynamicExtensionsSystemException("Please specify PV files folder path.");
		}
		boolean isUrlUnavalable = args[1] != null && args[1].trim().length() == 0;
		if (isUrlUnavalable)
		{
			throw new DynamicExtensionsSystemException("Please specify the  AppplicationURL.");
		}
	}
}
