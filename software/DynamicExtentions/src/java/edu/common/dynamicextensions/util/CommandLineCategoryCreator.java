
package edu.common.dynamicextensions.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.parser.CategoryCSVConstants;
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
public class CommandLineCategoryCreator
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	private static final Logger LOGGER = Logger.getCommonLogger(CommandLineCategoryCreator.class);

	private File zipFile;
	private URL serverUrl;
	private boolean isMetadataOnly = false;

	/**
	 * This method will internally call the category creation on server.
	 * @param args The list of arguments this method expects is as follows.
	 * args[0]=Zip file Path, args[1] =file path which contains categories to be created,
	 * args[2] =Application Url on which category creation should be triggered
	 * args[3] = is persist metadata only.
	 */
	public static void main(String[] args)
	{
		CommandLineCategoryCreator refresher = new CommandLineCategoryCreator();
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
		try
		{
			// trust all the https connections
			acceptAllHttpsConnections();
			// Initialize all instance variables
			initializeResources(args);

			// open the servlet connection
			URLConnection servletConnection = openServletConnection(serverUrl);

			// upload the Zip file to server
			uploadFileToServer(servletConnection, zipFile);

			// read the response from server
			processResponse(servletConnection);
		}
		catch (IOException e)
		{
			LOGGER.info("Exception occured while Creating the category", e);

		}
		catch (DynamicExtensionsSystemException e)
		{
			LOGGER.info("Exception occured while Creating the category", e);
		}
	}

	/**
	 * This method will upload the given file to the given URLConnection.
	 * @param servletConnection url on which to upload the file.
	 * @param file file to be uploaded.
	 * @throws IOException Exception.
	 * @throws DynamicExtensionsSystemException Exception.
	 */
	private void uploadFileToServer(URLConnection servletConnection, File file) throws IOException,
			DynamicExtensionsSystemException
	{
		BufferedInputStream csvReader = null;
		BufferedOutputStream servletWriter = null;
		byte[] buffer = new byte[1024];
		try
		{
			csvReader = new BufferedInputStream(new FileInputStream(file));
			servletWriter = getServletWriter(servletConnection);
			int len = csvReader.read(buffer);
			while (len >= 0)
			{
				servletWriter.write(buffer, 0, len);
				len = csvReader.read(buffer);
			}
			servletWriter.flush();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			throw new DynamicExtensionsSystemException(
					"File Not Found, please specify correct file path.", e);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			throw new DynamicExtensionsSystemException("Exception Occured While Creating category",
					e);
		}
		finally
		{
			if (servletWriter != null)
			{
				servletWriter.close();
			}
			if (csvReader != null)
			{
				csvReader.close();
			}

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
			zipFile = DynamicExtensionsUtility.zipFolder(args[0], "tempCategoryDir.zip");
			String url = args[1] + "/CreateCategoryAction.do?";
			String categoryFilenameString ="";
			if (args.length > 2 && !"".equals(args[2].trim()))
			{
				categoryFilenameString = getCategoryFilenameString(args[2]);

			}
			if (args.length > 3 && args[3].equalsIgnoreCase("true"))
			{
				isMetadataOnly = true;
			}
			serverUrl = new URL(url + CategoryCSVConstants.METADATA_ONLY + "=" + isMetadataOnly
					+ "&" + CategoryCSVConstants.CATEGORY_NAMES_FILE + "=" + categoryFilenameString);
		}
		catch (MalformedURLException e)
		{
			throw new DynamicExtensionsSystemException("Please provide correct server URL", e);
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
			throw new DynamicExtensionsSystemException(
					"Please Specify the folder where category Files are.");
		}
		if (args.length < 2)
		{
			throw new DynamicExtensionsSystemException(
					"Please Specify the Server URL on which the Application is running.");
		}
		if (args[0] != null && args[0].trim().length() == 0)
		{
			throw new DynamicExtensionsSystemException(
					"Please Specify the folder where category Files are.");
		}
		if (args[1] != null && args[1].trim().length() == 0)
		{
			throw new DynamicExtensionsSystemException(
					"Please Specify the Server URL on which the Application is running.");
		}
	}

	/**
	 * This method will read the names of the category files mentioned in the file "categoryListFileName"
	 * given in the arguments & create a string of category file names separated with the '!=!' token.
	 * @param categoryListFileName name of the file in which category files path are mentioned.
	 * @return the string formed from category file names with'!=!' in between.
	 * @throws DynamicExtensionsSystemException Exception.
	 * @throws IOException Exception.
	 */
	private String getCategoryFilenameString(String categoryListFileName)
			throws DynamicExtensionsSystemException, IOException
	{
		File objFile = new File(categoryListFileName);
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
					catFileNameString.append(line.trim()).append(CategoryCSVConstants.CAT_FILE_NAME_SEPARATOR);
					line = bufRdr.readLine();
				}
			}
			catch (IOException e)
			{
				throw new DynamicExtensionsSystemException("Can not read from file "
						+ categoryListFileName, e);
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
			throw new DynamicExtensionsSystemException("Category Names File not Found at "
					+ categoryListFileName);
		}
		return catFileNameString.toString();
	}

	/**
	 * This method will get the ouput stream from the given  servletConnection.
	 * @param servletConnection url connection from which to get the output Stream.
	 * @return BufferedOutputStream for writing to the URL.
	 * @throws DynamicExtensionsSystemException Exception.
	 */
	private BufferedOutputStream getServletWriter(URLConnection servletConnection)
			throws DynamicExtensionsSystemException
	{
		BufferedOutputStream servletWriter;

		try
		{
			servletWriter = new BufferedOutputStream(servletConnection.getOutputStream());
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Please Verify the Correct URL is specified & the Server is Running", e);
		}
		return servletWriter;
	}

	/**
	 * This method will wait for accepting the Response From the Server.
	 * In response server will send the Exception object if anything wrong happened else will
	 * return null.
	 * @param servletConnection Connection from which
	 * @throws IOException
	 * @throws DynamicExtensionsSystemException
	 */
	private void processResponse(URLConnection servletConnection) throws IOException,
			DynamicExtensionsSystemException
	{
		ObjectInputStream inputFromServlet = null;
		try
		{
			inputFromServlet = new ObjectInputStream(servletConnection.getInputStream());

			Object exceptionOccured = inputFromServlet.readObject();
			if (exceptionOccured instanceof Exception)
			{
				LOGGER.info("exception occured");
				throw new DynamicExtensionsSystemException("", (Exception) exceptionOccured);
			}
			else if (exceptionOccured instanceof Map)
			{
				printReport((Map<String, Exception>) exceptionOccured);
			}
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Exception occured while creating category, Please verify Server is Running", e);
		}
		catch (ClassNotFoundException e)
		{

			throw new DynamicExtensionsSystemException("Exception Occured While Creating category",
					e);
		}
		finally
		{
			if (inputFromServlet != null)
			{
				inputFromServlet.close();
			}
		}
	}

	private void printReport(Map<String, Exception> exceptionOccured)
	{
		for (Entry<String, Exception> entry : exceptionOccured.entrySet())
		{
			if (entry.getValue() == null)
			{
				LOGGER.info("Category File :" + entry.getKey() + "\n\tExecuted Succesfully");
			}
			else
			{
				LOGGER.error("Category creation failed for file : " + entry.getKey());
				LOGGER.error("Exception Occured is as Follows : "+ entry.getValue().getCause().getLocalizedMessage());
				LOGGER.debug("Exception Occured is as Follows : ", entry.getValue());
			}
		}

	}

	/**
	 * This method will open the connection to the URL given in the argument.
	 * It will also set the parameter to the connection like don't use the cached version
	 * & program will write something to the URL & also wait for response.
	 * @param serverUrl URL to which connection is required
	 * @return Urlconnection object for the given serverUrl.
	 * @throws IOException exception
	 */
	private URLConnection openServletConnection(URL serverUrl) throws IOException
	{
		URLConnection servletConnection = serverUrl.openConnection();

		// I m going to write something to servlet & also will be waiting to read from servlet
		servletConnection.setDoInput(true);
		servletConnection.setDoOutput(true);

		// Don't use a cached version of URL connection.
		servletConnection.setUseCaches(false);
		servletConnection.setDefaultUseCaches(false);

		// Specify the content type that we will send binary data
		servletConnection.setRequestProperty("Content-Type", "application/octet-stream");
		return servletConnection;
	}

	/**
	 * This method will force the client to accept the all server certificates as trusted.
	 * @throws DynamicExtensionsSystemException
	 * @throws Exception
	 */
	public void acceptAllHttpsConnections() throws DynamicExtensionsSystemException
	{

		// Now you are tell the JRE to ignore the hostname
		HostnameVerifier hostVerifier = new HostnameVerifier()
		{

			/* (non-Javadoc)
			 * @see javax.net.ssl.HostnameVerifier#verify(java.lang.String, javax.net.ssl.SSLSession)
			 */
			public boolean verify(String urlHostName, SSLSession session)
			{
				return true;
			}
		};
		// Now telling the JRE to trust any https server.
		// If you know the URL that you are connecting to then this should not be a problem
		trustAllHttpsCertificates();
		HttpsURLConnection.setDefaultHostnameVerifier(hostVerifier);

	}

	/**
	 * This is the overrided version of trust manager which will trust all the https connections
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

	/**
	 * This will set the default trust manager to the one which we just created so that
	 * it will accept all the connections.
	 * @throws DynamicExtensionsSystemException
	 * @throws Exception
	 */
	private static void trustAllHttpsCertificates() throws DynamicExtensionsSystemException
	{

		//  Create a trust manager that does not validate certificate chains:
		try
		{
			javax.net.ssl.TrustManager[] trustAllCerts =

			new javax.net.ssl.TrustManager[1];

			javax.net.ssl.TrustManager trustMnger = new DerivedTrustManager();

			trustAllCerts[0] = trustMnger;

			javax.net.ssl.SSLContext sslContext;

			sslContext = javax.net.ssl.SSLContext.getInstance("SSL");

			sslContext.init(null, trustAllCerts, null);

			javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(

			sslContext.getSocketFactory());
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new DynamicExtensionsSystemException(
					"Error occured while accepting the server certificates", e);
		}
		catch (KeyManagementException e)
		{
			throw new DynamicExtensionsSystemException(
					"Error occured while accepting the server certificates", e);

		}
	}


}
