
package edu.common.dynamicextensions.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.utility.HTTPSConnection;

/**
 * THis class will create the CSV file which contains the name name of the container
 * ,control & their identifiers used for the bulk operations .
 * This class will connet to the Server & on server metadata file will be generated which
 * will then be accepted in the response by this class & saved on the local machine.
 * @author pavan_kalantri
 *
 */
public class CategoryMetadataClient extends AbstractClient
{

	/**
	 * This will initiate the process
	 * @param args arguments array
	 */
	public static void main(String[] args)
	{
		CategoryMetadataClient metadataCreator = new CategoryMetadataClient();
		metadataCreator.execute(args);
	}

	/**
	 * This method will accept the response given by the server & will write that into a
	 * file , which will be the identifier & names of the attributes of the given
	 * category name.
	 * If nothing is accepted then it will thorw an error.
	 * @param servletConnection connection from which to process the response.
	 * @exception IOException exception.
	 * @exception DynamicExtensionsSystemException exception.
	 */
	protected void processResponse(URLConnection servletConnection) throws IOException,
			DynamicExtensionsSystemException
	{

		BufferedInputStream inputStream = null;
		BufferedOutputStream outputStream = null;
		try
		{
			inputStream = new BufferedInputStream(servletConnection.getInputStream());
			outputStream = new BufferedOutputStream(new FileOutputStream("catMetadataFile.csv"));
			copyContents(inputStream, outputStream);
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException("Error occured while processing response", e);
		}
		finally
		{
			if (inputStream != null)
			{
				inputStream.close();
			}
			if (outputStream != null)
			{
				outputStream.close();
			}
		}

	}

	/**
	 * This method will read the byte from inputStream & writes it to the outputStream
	 * @param inputStream stream from where to read the contents
	 * @param outputStream stream to which the contents should be written.
	 * @throws IOException
	 * @throws DynamicExtensionsSystemException
	 */
	private void copyContents(BufferedInputStream inputStream, BufferedOutputStream outputStream)
			throws IOException, DynamicExtensionsSystemException
	{
		byte[] buffer = new byte[1024];
		int len = inputStream.read(buffer);
		if (len <= 0)
		{
			throw new DynamicExtensionsSystemException("Error occured while processing response");
		}
		else
		{

			while (len > 0)
			{
				outputStream.write(buffer, 0, len);
				len = inputStream.read(buffer);
			}
			outputStream.flush();
			LOGGER.info("Metadata file written at catMetadataFile.csv");
		}
	}

	/**
	 * This method will intialize all the instance variables required.
	 * @param args the arguments which are to be used for initialize instance varibles
	 * @throws IOException exception.
	 * @throws Exception exception.
	 */
	protected void initializeResources(String[] args) throws DynamicExtensionsSystemException,
			IOException
	{
		try
		{
			String url = HTTPSConnection.getCorrectedApplicationURL(args[1])
					+ "/CreateCategoryMetadataAction.do?";
			String catName = URLEncoder.encode(getCategoryFilenameString(args[0]), "UTF-8");
			serverUrl = new URL(url + "categoryName=" + catName);
		}
		catch (MalformedURLException e)
		{
			throw new DynamicExtensionsSystemException("Please provide correct ApplicationURL", e);
		}
	}

	/**
	 * It will validate weather the correct number of arguments are passed or not & then throw
	 * exception accordingly.
	 * @param args arguments
	 * @throws DynamicExtensionsSystemException exception
	 */
	protected void validate(String args[]) throws DynamicExtensionsSystemException
	{
		if (args.length == 0)
		{
			throw new DynamicExtensionsSystemException("Please specify category Name.");
		}
		if (args.length < 2)
		{
			throw new DynamicExtensionsSystemException("Please specify the AppplicationURL.");
		}
		boolean isNameUnavalable = (args[0] != null && args[0].trim().length() == 0);
		if (isNameUnavalable)
		{
			throw new DynamicExtensionsSystemException("Please specify category Name.");
		}
		boolean isUrlUnavalable = (args[1] != null && args[1].trim().length() == 0);
		if (isUrlUnavalable)
		{
			throw new DynamicExtensionsSystemException("Please specify the  AppplicationURL.");
		}
	}
}
