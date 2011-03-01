
package edu.common.dynamicextensions.processor;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.DataEditClient;
import edu.common.dynamicextensions.entitymanager.DataEntryClient;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.global.Variables;
import edu.wustl.common.beans.SessionDataBean;

/**
 * This Class populates the DataEntryForm and saves the same into the Database.
 * @author chetan_patil
 */
public class ApplyDataEntryFormProcessor extends BaseDynamicExtensionsProcessor
{

	/**
	 * Default Constructor
	 */

	private Long userId;

	/**
	 * This method returns the instance of ApplyDataEntryFormProcessor.
	 * @return ApplyDataEntryFormProcessor Instance of ApplyDataEntryFormProcessor
	 */
	public static ApplyDataEntryFormProcessor getInstance()
	{
		return new ApplyDataEntryFormProcessor();
	}

	/**
	 *
	 * @param attributeValueMap
	 * @return
	 */
	public Map<BaseAbstractAttributeInterface, Object> removeNullValueEntriesFormMap(
			Map<BaseAbstractAttributeInterface, Object> attributeValueMap)
	{
		Set<Map.Entry<BaseAbstractAttributeInterface, Object>> attributeValueSet = attributeValueMap
				.entrySet();
		Iterator attributeValueSetIterator = attributeValueSet.iterator();
		while (attributeValueSetIterator.hasNext())
		{
			Map.Entry<BaseAbstractAttributeInterface, Object> attributeValueEntry = (Map.Entry<BaseAbstractAttributeInterface, Object>) attributeValueSetIterator
					.next();

			Object value = attributeValueEntry.getValue();
			if (value == null)
			{
				attributeValueSetIterator.remove();
			}
			else if (value instanceof List && ((List) value).isEmpty())
			{
				attributeValueSetIterator.remove();

			}
		}
		return attributeValueMap;
	}

	/**
	 * This method will pass the values entered into the controls to EntityManager to insert them in Database.
	 * @param containerInterface The container of who's value of Control are to be populated.
	 * @param attributeValueMap The Map of Attribute and their corresponding values from controls.
	 * @throws DynamicExtensionsApplicationException on Application exception
	 * @throws DynamicExtensionsSystemException on System exception
	 * @return recordIdentifier Record identifier of the last saved record.
	 * @throws MalformedURLException
	 * @deprecated Use {@link #insertDataEntryForm(ContainerInterface,Map<BaseAbstractAttributeInterface, Object>,SessionDataBean)} instead
	 */
	public String insertDataEntryForm(ContainerInterface container,
			Map<BaseAbstractAttributeInterface, Object> attributeValueMap)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException, MalformedURLException
	{
		return insertDataEntryForm(container, attributeValueMap, null);
	}

	/**
	 * This method will pass the values entered into the controls to EntityManager to insert them in Database.
	 * @param attributeValueMap The Map of Attribute and their corresponding values from controls.
	 * @param sessionDataBean
	 * @param containerInterface The container of who's value of Control are to be populated.
	 * @throws DynamicExtensionsApplicationException on Application exception
	 * @throws DynamicExtensionsSystemException on System exception
	 * @return recordIdentifier Record identifier of the last saved record.
	 * @throws MalformedURLException
	 */
	public String insertDataEntryForm(ContainerInterface container,
			Map<BaseAbstractAttributeInterface, Object> attributeValueMap, SessionDataBean sessionDataBean)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException, MalformedURLException
	{
		Long recordIdentifier = null;
		String entityGroupName=null;
		if(container.getAbstractEntity() instanceof EntityInterface)
		{
			entityGroupName=((Entity)container.getAbstractEntity()).getEntityGroup().getName();
		}
		else
		{
			entityGroupName=((CategoryEntityInterface) container.getAbstractEntity()).getEntity().getEntityGroup().getName();
		}
		Map<String, Object> clientmap = new HashMap<String, Object>();
		DataEntryClient dataEntryClient=new DataEntryClient();
		clientmap.put(WebUIManagerConstants.RECORD_ID, recordIdentifier);
		clientmap.put(WebUIManagerConstants.SESSION_DATA_BEAN, null);
		clientmap.put(WebUIManagerConstants.USER_ID, userId);
		clientmap.put(WebUIManagerConstants.CONTAINER, container);
		clientmap.put(WebUIManagerConstants.DATA_VALUE_MAP, attributeValueMap);
		dataEntryClient.setServerUrl(new URL(Variables.jbossUrl+entityGroupName+"/"));
		dataEntryClient.setParamaterObjectMap(clientmap);
		dataEntryClient.execute(null);

		recordIdentifier=(Long)dataEntryClient.getObject();
		return recordIdentifier.toString();
	}

	/**
	 * This method will pass the changed (modified) values entered into the controls to EntityManager to update them in Database.
	 * @param container
	 * @param attributeValueMap
	 * @param recordIdentifier
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 * @throws SQLException
	 * @throws MalformedURLException
	 * @deprecated Use {@link #editDataEntryForm(ContainerInterface,Map<BaseAbstractAttributeInterface, Object>,Long,SessionDataBean)} instead
	 */
	public Boolean editDataEntryForm(ContainerInterface container,
			Map<BaseAbstractAttributeInterface, Object> attributeValueMap, Long recordIdentifier)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException,
			SQLException, MalformedURLException
	{
		return editDataEntryForm(container, attributeValueMap, recordIdentifier, null);
	}

	/**
	 * This method will pass the changed (modified) values entered into the controls to EntityManager to update them in Database.
	 *
	 * @param container the container
	 * @param attributeValueMap the attribute value map
	 * @param recordIdentifier the record identifier
	 * @param sessionDataBean the session data bean
	 * @return true, if edits the data entry form
	 * @throws DynamicExtensionsApplicationException the dynamic extensions application exception
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 * @throws SQLException the SQL exception
	 * @throws MalformedURLException
	 */
	public Boolean editDataEntryForm(ContainerInterface container,
			Map<BaseAbstractAttributeInterface, Object> attributeValueMap, Long recordIdentifier,
			SessionDataBean sessionDataBean) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException, SQLException, MalformedURLException
	{
		String entityGroupName=null;
		if(container.getAbstractEntity() instanceof EntityInterface)
		{
			entityGroupName=((Entity)container.getAbstractEntity()).getEntityGroup().getName();
		}
		else
		{
			entityGroupName=((CategoryEntityInterface) container.getAbstractEntity()).getEntity().getEntityGroup().getName();
		}
		Map<String, Object> clientmap = new HashMap<String, Object>();
		DataEditClient dataEditClient=new DataEditClient();
		clientmap.put(WebUIManagerConstants.RECORD_ID, recordIdentifier);
		clientmap.put(WebUIManagerConstants.SESSION_DATA_BEAN, null);
		clientmap.put(WebUIManagerConstants.SESSION_DATA_BEAN, userId);
		clientmap.put(WebUIManagerConstants.CONTAINER, container);
		clientmap.put(WebUIManagerConstants.DATA_VALUE_MAP, attributeValueMap);
		dataEditClient.setServerUrl(new URL(Variables.jbossUrl+entityGroupName+"/"));
		dataEditClient.setParamaterObjectMap(clientmap);
		dataEditClient.execute(null);

		return true;
	}

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

}
