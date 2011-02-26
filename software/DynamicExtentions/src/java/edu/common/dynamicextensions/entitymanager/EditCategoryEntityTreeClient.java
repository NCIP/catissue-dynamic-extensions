package edu.common.dynamicextensions.entitymanager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;

import edu.common.dynamicextensions.client.AbstractClient;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.utility.HTTPSConnection;

public class EditCategoryEntityTreeClient extends AbstractClient {

	@Override
	protected void initializeResources(String[] args)
			throws DynamicExtensionsSystemException, IOException {
		serverUrl = new URL(serverUrl+"EditCategoryEntityTreeHandler");
	}
	private Object object;
	@Override
	protected void validate(String[] args)
			throws DynamicExtensionsSystemException {
		// TODO Auto-generated method stub

	}
	@Override
	protected void performAction(HTTPSConnection httpsConnection,
			URLConnection servletConnection)
			throws DynamicExtensionsSystemException, IOException {
		ObjectOutputStream outputToServlet = new ObjectOutputStream(servletConnection
				.getOutputStream());
		outputToServlet.writeObject(paramaterObjectMap);
		outputToServlet.flush();
		outputToServlet.close();

	}
	@Override
	protected void processResponse(URLConnection servletConnection)
			throws DynamicExtensionsSystemException{
		ObjectInputStream inputFromServlet = null;
		try
		{
			inputFromServlet = new ObjectInputStream(servletConnection
					.getInputStream());
			object = inputFromServlet.readObject();
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Error in reading objects from responce", e);
		} catch (ClassNotFoundException e) {
			throw new DynamicExtensionsSystemException(
					"Error in reading objects from responce", e);
		}finally
		{
			try {
				inputFromServlet.close();
			} catch (IOException e) {
				throw new DynamicExtensionsSystemException(
						"Error in reading objects from responce", e);
			}

		}
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}
}
