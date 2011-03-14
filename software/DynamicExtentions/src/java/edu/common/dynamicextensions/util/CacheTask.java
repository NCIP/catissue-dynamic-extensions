package edu.common.dynamicextensions.util;

import java.io.EOFException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import edu.common.dynamicextensions.client.DEClient;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.utility.HTTPSConnection;
import edu.wustl.common.util.logger.Logger;

/**
 * @author kunal_kamble
 *
 */
public class CacheTask {
	public static void main(String[] args) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException {
		releaseForms(args);
	}

	private static void releaseForms(String[] args) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException {
		String entityGroupName = args[0];
		EntityGroupInterface entityGroup = EntityManager.getInstance().getEntityGroupByName(entityGroupName);
		if(entityGroup!= null)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(WebUIManagerConstants.ENTITY_GROUP,entityGroup);
			map.put(WebUIManagerConstants.OPERATION,WebUIManagerConstants.RELEASE_FORMS);
			DEClient client = new DEClient();
			client.setParamaterObjectMap(map);
			try {
				client.setServerUrl(new URL(args[1]+"/UpdateCache"));
			} catch (MalformedURLException e) {
			throw new DynamicExtensionsApplicationException("Error in releasing forms on the server cache",e);
			}
			client.execute(null);


		}else
		{
			System.out.println("Entity group " +entityGroupName+" not found");
		}


	}
	/**
	 * This method will process the response recieved from the server .
	 * @param servletConnection connection by which connected to server
	 * @throws DynamicExtensionsSystemException exception.
	 * @throws IOException exception.
	 */
	protected void processResponse(URLConnection servletConnection)
			throws DynamicExtensionsSystemException, IOException
	{
	   try
	   {
		   HTTPSConnection.getInstance().processResponse(servletConnection);
	   }
	   catch(EOFException eofException)
	   {
		   Logger.out.info("EOF.");
	   }
	}

}
