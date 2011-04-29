
package edu.common.dynamicextensions.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.utility.HTTPSConnection;

public class DataEntryClient extends AbstractClient
{

	private Object object;
	Map<AbstractAttributeInterface, Object> dataValue;

	@Override
	protected void initializeResources(String[] args) throws DynamicExtensionsSystemException,
			IOException
	{
		serverUrl = new URL(serverUrl + "DataEntryHandler");
	}

	@Override
	protected void validate(String[] args) throws DynamicExtensionsSystemException
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void performAction(HTTPSConnection httpsConnection, URLConnection servletConnection)
			throws DynamicExtensionsSystemException, IOException
	{
		ObjectOutputStream outputToServlet = new ObjectOutputStream(servletConnection
				.getOutputStream());
		outputToServlet.writeObject(paramaterObjectMap);
		outputToServlet.flush();
		outputToServlet.close();

	}

	@Override
	protected void processResponse(URLConnection servletConnection)
			throws DynamicExtensionsSystemException
	{
		ObjectInputStream inputFromServlet = null;
		try
		{
			inputFromServlet = new ObjectInputStream(servletConnection.getInputStream());
			object = inputFromServlet.readObject();
		}
		catch (IOException e)
		{
			LOGGER.info("IO exception occured" + e.getMessage());
			throw new DynamicExtensionsSystemException("Error in reading objects from responce", e);
		}
		catch (ClassNotFoundException e)
		{
			LOGGER.info("Class not found exception occured" + e.getMessage());
			throw new DynamicExtensionsSystemException("Error in reading objects from responce", e);
		}
		finally
		{
			closeObjectInputStream(inputFromServlet);
		}
	}

	public Object getObject()
	{
		return object;
	}

	public void setObject(Object object)
	{
		this.object = object;
	}

	public Map<AbstractAttributeInterface, Object> getDataValue()
	{
		return dataValue;
	}

	public void setDataValue(Map<AbstractAttributeInterface, Object> dataValue)
	{
		this.dataValue = dataValue;
	}

}
