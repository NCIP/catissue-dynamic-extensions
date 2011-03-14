package edu.common.dynamicextensions.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import edu.common.dynamicextensions.client.DEClient;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;

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
}
