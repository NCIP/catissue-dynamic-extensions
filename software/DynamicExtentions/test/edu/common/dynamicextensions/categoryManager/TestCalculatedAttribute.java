
package edu.common.dynamicextensions.categoryManager;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import edu.common.dynamicextensions.client.DataEntryClient;
import edu.common.dynamicextensions.dao.impl.DynamicExtensionDAO;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.Variables;
import edu.wustl.cab2b.server.cache.EntityCache;

/**
 *
 * @author mandar_shidhore
 *
 */
public class TestCalculatedAttribute extends DynamicExtensionsBaseTestCase
{

	/**
	 *
	 */
	public void testDataEntryForCalculatedAttribute()
	{
		try
		{
			InputStream stream = DynamicExtensionDAO.class.getClassLoader().getResourceAsStream(
					"DynamicExtensions.properties");
			Properties props = new Properties();
			// FIXME - getting a null pointer exception here.
			props.load(stream);
			System.out.println("testing load properies - " + props.getProperty("Application.url"));
			DynamicExtensionsUtility.initializeVariables(props);
			CategoryInterface categoryInterface = EntityCache.getInstance().getCategoryByName(
					"CalcAttribute_Formula_within_formula");
			ContainerInterface containerInterface = (ContainerInterface) categoryInterface
					.getRootCategoryElement().getContainerCollection().toArray()[0];

			Map<BaseAbstractAttributeInterface, Object> attributeToValueMap = mapGenerator.createDataValueMapForCategory(categoryInterface.getRootCategoryElement(),0);
			String entityGroupName = containerInterface.getAbstractEntity().getEntityGroup()
					.getName();

			Map<String, Object> clientmap = new HashMap<String, Object>();
			DataEntryClient dataEntryClient = new DataEntryClient();
			clientmap.put(WebUIManagerConstants.RECORD_ID, null);
			clientmap.put(WebUIManagerConstants.SESSION_DATA_BEAN, null);
			clientmap.put(WebUIManagerConstants.USER_ID, null);
			clientmap.put(WebUIManagerConstants.CONTAINER, containerInterface);
			clientmap.put(WebUIManagerConstants.DATA_VALUE_MAP, attributeToValueMap);
			try
			{
				dataEntryClient.setServerUrl(new URL(Variables.jbossUrl + entityGroupName + "/"));
			}
			catch (MalformedURLException e)
			{
				throw new DynamicExtensionsApplicationException("Invalid URL:" + Variables.jbossUrl
						+ entityGroupName + "/");
			}
			dataEntryClient.setParamaterObjectMap(clientmap);
			dataEntryClient.execute(null);

			Long recordIdentifier = (Long) dataEntryClient.getObject();
			assertNotNull(recordIdentifier);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Unknown exception occured " + e.getMessage());
		}
	}

}
