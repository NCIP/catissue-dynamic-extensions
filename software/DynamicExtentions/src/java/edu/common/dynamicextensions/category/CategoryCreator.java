
package edu.common.dynamicextensions.category;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.ZipUtility;
import edu.common.dynamicextensions.utility.HTTPSConnection;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * This class is used to create category by using ant task, which will not require to
 * restart the Jboss server to view those categories.
 * This class will upload all the necessary data to the jboss server & then will
 * trigger the category creation on server side itself.
 * @author pavan_kalantri
 *
 */
public class CategoryCreator
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	private static final Logger LOGGER = Logger.getCommonLogger(CategoryCreator.class);

	private static File zipFile;
	private static URL serverUrl;
	private static boolean isMetadataOnly = false;

	/**
	 * This method will internally call the category creation on server.
	 * @param args The list of arguments this method expects is as follows.
	 * args[0]=Zip file Path, args[1] =file path which contains categories to be created,
	 * args[2] =Application Url on which category creation should be triggered
	 * args[3] = is persist metadata only.
	 */
	public static void main(String[] args)
	{
		CategoryCreator refresher = new CategoryCreator();
		refresher.createCategory(args);
	}

	/**
	 * This method will initiate the category creation on server so that server restart is not needed to
	 * refresh the Entity Cache.
	 * @param args The list of arguments this method expects is as follows.
	 * args[0]=Zip file Path, args[1] =file path which contains categories to be created,
	 * args[2] =Application Url on which category creation should be triggered
	 * args[3] = is persist metadata only.
	 */
	public void createCategory(String[] args)
	{
		HTTPSConnection httpsConnection = HTTPSConnection.getInstance();
		try
		{
			// trust all the https connections
			httpsConnection.acceptAllHttpsConnections();
			// Initialize all instance variables
			initializeResources(args);

			// open the servlet connection
			URLConnection servletConnection = httpsConnection.openServletConnection(serverUrl);

			// upload the Zip file to server
			httpsConnection.uploadFileToServer(servletConnection, zipFile);

			// read the response from server
			httpsConnection.processResponse(servletConnection);
		}
		catch (IOException e)
		{
			LOGGER.error("Exception : " + e.getLocalizedMessage());
			LOGGER.info("For more information please check :/log/dynamicExtentions.log");
			LOGGER.debug("Exception occured is as follows : ", e);
		}
		catch (DynamicExtensionsSystemException e)
		{
			LOGGER.error("Exception : " + e.getLocalizedMessage());
			LOGGER.info("For more information please check :/log/dynamicExtentions.log");
			LOGGER.debug("Exception occured is as follows : ", e);
		}
	}



	/**
	 * It will validate all the necessary parameters are provided & are valid.
	 * If all are valid it will also intialize all the instance variables also
	 * @param args the arguments which are to be validated & initialize instance varibles from
	 * these arguments
	 * @throws IOException
	 * @throws Exception
	 */
	private void initializeResources(String[] args) throws DynamicExtensionsSystemException,
			IOException
	{
		try
		{
			validate(args);
			zipFile = ZipUtility.zipFolder(args[0], "tempCategoryDir.zip");
			String url = args[1] + "/CreateCategoryAction.do?";
			String catFilename = "";
			if (args.length > 2 && !"".equals(args[2].trim()))
			{
				catFilename = getCategoryFilenameString(args[2]);

			}
			if (args.length > 3 && args[3].equalsIgnoreCase("true"))
			{
				isMetadataOnly = true;
			}
			serverUrl = new URL(url + CategoryCreatorConstants.METADATA_ONLY + "=" + isMetadataOnly
					+ "&" + CategoryCreatorConstants.CATEGORY_NAMES_FILE + "="
					+ catFilename);
		}
		catch (MalformedURLException e)
		{
			throw new DynamicExtensionsSystemException("Please provide correct ApplicationURL", e);
		}
	}

	/**
	 * It will validate weather the correct number of arguments are passed or not & then throw exception accordingly.
	 * @param args arguments
	 * @throws DynamicExtensionsSystemException exception
	 */
	private static void validate(String args[]) throws DynamicExtensionsSystemException
	{
		if (args.length == 0)
		{
			throw new DynamicExtensionsSystemException("Please specify category files folder path.");
		}
		if (args.length < 2)
		{
			throw new DynamicExtensionsSystemException("Please specify the AppplicationURL.");
		}
		if (args[0] != null && args[0].trim().length() == 0)
		{
			throw new DynamicExtensionsSystemException("Please specify category files folder path.");
		}
		if (args[1] != null && args[1].trim().length() == 0)
		{
			throw new DynamicExtensionsSystemException("Please specify the  AppplicationURL.");
		}
	}

	/**
	 * This method will read the names of the category files mentioned in the file "categoryListFileName"
	 * given in the arguments & create a string of category file names separated with the '!=!' token.
	 * @param listCatFileName name of the file in which category files path are mentioned.
	 * @return the string formed from category file names with'!=!' in between.
	 * @throws DynamicExtensionsSystemException Exception.
	 * @throws IOException Exception.
	 */
	private String getCategoryFilenameString(String listCatFileName)
			throws DynamicExtensionsSystemException, IOException
	{
		File objFile = new File(listCatFileName);
		BufferedReader bufRdr = null;
		StringBuffer catFileNameString = new StringBuffer();
		if (objFile.exists())
		{
			try
			{
				bufRdr = new BufferedReader(new FileReader(objFile));
				String line = bufRdr.readLine();
				//read each line of text file
				while (line != null)
				{
					catFileNameString.append(line.trim()).append(
							CategoryCreatorConstants.CAT_FILE_NAME_SEPARATOR);
					line = bufRdr.readLine();
				}
			}
			catch (IOException e)
			{
				throw new DynamicExtensionsSystemException("Can not read from file "
						+ listCatFileName, e);
			}
			finally
			{
				if (bufRdr != null)
				{
					bufRdr.close();
				}
			}
		}
		else
		{
			throw new DynamicExtensionsSystemException("Category names file not found at "
					+ listCatFileName);
		}
		return catFileNameString.toString();
	}

	/**
	 * This is the overridden version of trust manager which will trust all the https connections
	 * @author pavan_kalantri
	 *
	 */
	public static class DerivedTrustManager
			implements
				javax.net.ssl.TrustManager,
				javax.net.ssl.X509TrustManager
	{

		/* (non-Javadoc)
		 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
		 */
		public java.security.cert.X509Certificate[] getAcceptedIssuers()
		{
			return new java.security.cert.X509Certificate[0];
		}

		/**
		 * This method will always return true so that any url is considered to be trusted.
		 * & no certification validation is done.
		 * @param certs certificates.
		 * @return true if certificate is trusted
		 */
		public boolean isServerTrusted(java.security.cert.X509Certificate[] certs)
		{
			return true;
		}

		/**
		 * This method will always return true so that any url is considered to be trusted.
		 * & no certification validation is done.
		 * @param certs certificates.
		 * @return true if certificate is trusted
		 */
		public boolean isClientTrusted(java.security.cert.X509Certificate[] certs)
		{
			return true;
		}

		/* (non-Javadoc)
		 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
		 */
		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException
		{
			// TODO Auto-generated catch block
		}

		/* (non-Javadoc)
		 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
		 */
		public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException
		{
			// TODO Auto-generated catch block
		}
	}
}
