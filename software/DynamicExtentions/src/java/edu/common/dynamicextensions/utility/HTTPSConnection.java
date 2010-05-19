
package edu.common.dynamicextensions.utility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import edu.common.dynamicextensions.client.CategoryClient.DerivedTrustManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.logger.Logger;

public final class HTTPSConnection
{

	private static final Logger LOGGER = Logger.getCommonLogger(HTTPSConnection.class);

	private static HTTPSConnection httpsConn;

	private static Boolean isFirst = false;

	public static HTTPSConnection getInstance()
	{
		synchronized (isFirst)
		{
			if (httpsConn == null)
			{
				isFirst = true;
				httpsConn = new HTTPSConnection();
			}
			return httpsConn;
		}
	}

	private HTTPSConnection()
	{

	}

	/**
	 * This method will force the client to accept the all server certificates as trusted.
	 * @throws DynamicExtensionsSystemException
	 * @throws Exception
	 */
	public void acceptAllHttpsConnections() throws DynamicExtensionsSystemException
	{
		// Now telling the JRE to trust any https server.
		// If you know the URL that you are connecting to then this should not be a problem
		trustAllHttpsCertificates();
		HttpsURLConnection.setDefaultHostnameVerifier(hostVerifier);
	}

	private void trustAllHttpsCertificates() throws DynamicExtensionsSystemException
	{
		//  Create a trust manager that does not validate certificate chains:
		try
		{
			javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];

			trustAllCerts[0] = new DerivedTrustManager();

			javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("SSL");

			sslContext.init(null, trustAllCerts, null);

			javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
					.getSocketFactory());
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

	/**
	 * This method will open the connection to the URL given in the argument.
	 * It will also set the parameter to the connection like don't use the cached version
	 * & program will write something to the URL & also wait for response.
	 * @param serverUrl URL to which connection is required
	 * @return Urlconnection object for the given serverUrl.
	 * @throws IOException exception
	 */
	public URLConnection openServletConnection(URL serverUrl)
			throws DynamicExtensionsSystemException
	{
		URLConnection servletConnection;
		try
		{
			servletConnection = serverUrl.openConnection();

			// I m going to write something to servlet & also will be waiting to read from servlet
			servletConnection.setDoInput(true);
			servletConnection.setDoOutput(true);

			// Don't use a cached version of URL connection.
			servletConnection.setUseCaches(false);
			servletConnection.setDefaultUseCaches(false);

			// Specify the content type that we will send binary data
			servletConnection.setRequestProperty("Content-Type", "multipart/form-data");
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Please verify the ApplicationURL and also verify that the server is running.",
					e);
		}
		return servletConnection;
	}

	/**
	 * This method will upload the given file to the given URLConnection.
	 * @param servletConnection url on which to upload the file.
	 * @param file file to be uploaded.
	 * @throws IOException Exception.
	 * @throws DynamicExtensionsSystemException Exception.
	 */
	public void uploadFileToServer(URLConnection servletConnection, File file) throws IOException,
			DynamicExtensionsSystemException
	{
		if (file != null)
		{

			BufferedInputStream csvReader = new BufferedInputStream(new FileInputStream(file));
			BufferedOutputStream servletWriter = getServletWriter(servletConnection);
			try
			{
				copyFileContents(csvReader, servletWriter);
			}
			catch (IOException e)
			{
				throw new DynamicExtensionsSystemException(
						"Exception occured while uploading file", e);
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
	}

	/**
	 * This method will read the contents from csvReader & writes it to the servletWriter.
	 * @param csvReader reader
	 * @param servletWriter writter
	 * @throws IOException exception.
	 */
	private void copyFileContents(BufferedInputStream csvReader, BufferedOutputStream servletWriter)
			throws IOException
	{
		byte[] buffer = new byte[1024];
		int len = csvReader.read(buffer);
		while (len >= 0)
		{
			servletWriter.write(buffer, 0, len);
			len = csvReader.read(buffer);
		}
		servletWriter.flush();
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
		try
		{
			return new BufferedOutputStream(servletConnection.getOutputStream());
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Please verify the Correct URL is specified & the server is running", e);
		}
	}

	/**
	 * This method will wait for accepting the Response From the Server.
	 * In response server will send the Exception object if anything wrong happened else will
	 * return null.
	 * @param servletConnection Connection from which
	 * @throws IOException
	 * @throws DynamicExtensionsSystemException
	 */
	@SuppressWarnings("unchecked")
	public void processResponse(URLConnection servletConnection) throws IOException,
			DynamicExtensionsSystemException
	{
		ObjectInputStream inputFromServlet = new ObjectInputStream(servletConnection
				.getInputStream());
		try
		{
			Object exceptionOccured = inputFromServlet.readObject();
			printExceptionLog(exceptionOccured);
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Can not connect to server, please verify server is running", e);
		}
		catch (ClassNotFoundException e)
		{

			throw new DynamicExtensionsSystemException(
					"Class not found " + e.getLocalizedMessage(), e);
		}
		finally
		{
			if (inputFromServlet != null)
			{
				inputFromServlet.close();
			}
		}
	}

	/**
	 * It will display the cause of exception on the console.
	 * @param exceptionOccured exception to be displayed.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	private void printExceptionLog(Object exceptionOccured) throws DynamicExtensionsSystemException
	{
		if (exceptionOccured instanceof Exception)
		{
			LOGGER.info("exception occured");
			Exception serverException = (Exception) exceptionOccured;
			throw new DynamicExtensionsSystemException(serverException.getLocalizedMessage(),
					serverException);
		}
		else if (exceptionOccured instanceof Map)
		{
			printReport((Map<String, Exception>) exceptionOccured);
		}
	}

	private void printReport(Map<String, Exception> exceptionOccured)
	{
		for (Entry<String, Exception> entry : exceptionOccured.entrySet())
		{
			if (entry.getValue() == null)
			{
				LOGGER.info("Operation successfull for file :" + entry.getKey());
			}
			else
			{
				LOGGER.error("This operation failed for file : " + entry.getKey());
				// if the actual exception occured was DE exception then the localised message will be wmpty
				// & actual message will be in its cause, in other case like if Null pointer exception then the
				//Actual message is in the wrapped exception itself.
				if (entry.getValue().getCause() == null
						|| !"".equals(entry.getValue().getLocalizedMessage()))
				{
					LOGGER.error("Exception : " + entry.getValue().getLocalizedMessage());
				}
				else
				{
					LOGGER
							.error("Exception : "
									+ entry.getValue().getCause().getLocalizedMessage());
				}
				LOGGER.error("For more details please check ./log/dynamicExtentionsError.log");
				LOGGER.debug("Exception : ", entry.getValue());
			}
		}
	}

	// Now you are tell the JRE to ignore the hostname
	private final transient HostnameVerifier hostVerifier = new HostnameVerifier()
	{

		/* (non-Javadoc)
		 * @see javax.net.ssl.HostnameVerifier#verify(java.lang.String, javax.net.ssl.SSLSession)
		 */
		public boolean verify(String urlHostName, SSLSession session)
		{
			return true;
		}
	};

	/**
	 * this method will remove the one '/' if there are two in the end.
	 * @param appUrl url to correct.
	 * @return corrected url.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	public static String getCorrectedApplicationURL(String appUrl)
			throws DynamicExtensionsSystemException
	{
		if (appUrl.contains("\\"))
		{
			throw new DynamicExtensionsSystemException("In Application.url replace '\\' with '/'.");
		}
		if ('/' == appUrl.charAt(appUrl.length() - 1))
		{
			appUrl = appUrl.substring(0, appUrl.length() - 1);

		}
		return appUrl;

	}
}
