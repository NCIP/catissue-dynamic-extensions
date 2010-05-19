
package edu.common.dynamicextensions.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.utility.HTTPSConnection;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * This class should be the parent of all the classes which needs to communicate to the server from
 * the standalone java programms, eg. categoryCreator, permissibleValuesCreator etc.
 * For using this class child class only have to implement the validate & initializeResources
 * method according to their need & call the method execute to start the process.
 * @author pavan_kalantri
 *
 */
public abstract class AbstractClient
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	protected static final Logger LOGGER = Logger.getCommonLogger(AbstractClient.class);

	protected File zipFile;
	protected URL serverUrl;

	/**
	 * This method will initiate the task on server by the URl specified.
	 * @param args The list of arguments this method should be as the child expects
	 * according to is initializeResources method.
	 *
	 */
	public void execute(String[] args)
	{
		HTTPSConnection httpsConnection = HTTPSConnection.getInstance();
		try
		{
			// trust all the https connections
			httpsConnection.acceptAllHttpsConnections();
			//validate all the arguments
			validate(args);
			// Initialize all instance variables
			initializeResources(args);
			// open the servlet connection
			URLConnection servletConnection = httpsConnection.openServletConnection(serverUrl);
			LOGGER.info("Connection established");
			// upload the Zip file to server
			httpsConnection.uploadFileToServer(servletConnection, zipFile);
			LOGGER.info("Artifacts uploaded");
			// read the response from server
			processResponse(servletConnection);
			LOGGER.info("Target completed successfully");
		}
		catch (IOException e)
		{
			LOGGER.error("Exception : " + e.getLocalizedMessage());
			LOGGER.info("For more information please check :/log/dynamicExtentionsError.log");
			LOGGER.debug("Exception occured is as follows : ", e);
		}
		catch (DynamicExtensionsSystemException e)
		{
			LOGGER.error("Exception : " + e.getLocalizedMessage());
			LOGGER.info("For more information please check :/log/dynamicExtentionsError.log");
			LOGGER.debug("Exception occured is as follows : ", e);
		}
	}

	/**
	 * This method will initialize all the instance veriabls required for the process to
	 * complete.
	 * @param args arguments array from to initilize the variables.
	 * @throws DynamicExtensionsSystemException exception.
	 * @throws IOException exception.
	 */
	protected abstract void initializeResources(String[] args)
			throws DynamicExtensionsSystemException, IOException;

	/**
	 * It will validate weather the correct number of arguments are passed or not & then throw exception accordingly.
	 * @param args arguments
	 * @throws DynamicExtensionsSystemException exception
	 */
	protected abstract void validate(String args[]) throws DynamicExtensionsSystemException;

	/**
	 * This method will process the response recieved from the server .
	 * @param servletConnection connection by which connected to server
	 * @throws DynamicExtensionsSystemException exception.
	 * @throws IOException exception.
	 */
	protected void processResponse(URLConnection servletConnection)
			throws DynamicExtensionsSystemException, IOException
	{
		HTTPSConnection.getInstance().processResponse(servletConnection);
	}

	/**
	 * This method will read the names of the category files mentioned in the file "categoryListFileName"
	 * given in the arguments & create a string of category file names separated with the '!=!' token.
	 * @param listCatFileName name of the file in which category files path are mentioned.
	 * @return the string formed from category file names with'!=!' in between.
	 * @throws DynamicExtensionsSystemException Exception.
	 * @throws IOException Exception.
	 */
	protected String getCategoryFilenameString(String listCatFileName)
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
}
