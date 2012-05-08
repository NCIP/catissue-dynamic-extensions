
package edu.common.dynamicextensions.processor;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.beans.SessionDataBean;

public interface ApplyDataEntryProcessorInterface
{

	public void setUserId(Long userId);

	public Long getUserId();

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
			Map<BaseAbstractAttributeInterface, Object> attributeValueMap,
			SessionDataBean sessionDataBean) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException;

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
			DynamicExtensionsSystemException;

}
