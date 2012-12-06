
package edu.common.dynamicextensions.processor;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import edu.common.dynamicextensions.dao.impl.FormAuditManager;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.entitymanager.FileQueryBean;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.beans.SessionDataBean;

public class InContextApplyDataEntryProcessor extends BaseDynamicExtensionsProcessor implements ApplyDataEntryProcessorInterface 
{

	private Long userId;

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
			DynamicExtensionsSystemException
	{
		Long recordIdentifier = null;

		if (container.getAbstractEntity() instanceof CategoryEntityInterface)
		{
			CategoryInterface categoryInterface = ((CategoryEntityInterface) container
					.getAbstractEntity()).getCategory();
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			Long categoryRecordId = categoryManager.insertData(categoryInterface,
					attributeValueMap, sessionDataBean, userId);
			recordIdentifier = categoryManager.getEntityRecordIdByRootCategoryEntityRecordId(
					categoryRecordId, categoryInterface.getRootCategoryElement()
							.getTableProperties().getName());
		}
		else
		{
			Map map = attributeValueMap;
			EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
			recordIdentifier = entityManagerInterface.insertData((EntityInterface) container
					.getAbstractEntity(), map, null, new ArrayList<FileQueryBean>(),
					sessionDataBean, userId);
		}

		audit(sessionDataBean, container, attributeValueMap, recordIdentifier, "INSERT");
		return recordIdentifier.toString();
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
			DynamicExtensionsSystemException
	{
		boolean isEdited;

		if (container.getAbstractEntity() instanceof EntityInterface)
		{
			EntityManagerInterface entityManager = EntityManager.getInstance();
			EntityInterface entity = (Entity) container.getAbstractEntity();
			//Correct this:
			Map map = attributeValueMap;

			isEdited = entityManager.editData(entity, map, recordIdentifier, null,
					new ArrayList<FileQueryBean>(), sessionDataBean, userId);
		}
		else
		{
			CategoryInterface categoryInterface = ((CategoryEntityInterface) container
					.getAbstractEntity()).getCategory();
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			Long categoryRecordId = categoryManager.getRootCategoryEntityRecordIdByEntityRecordId(
					recordIdentifier, categoryInterface.getRootCategoryElement()
							.getTableProperties().getName());
			isEdited = CategoryManager.getInstance().editData(
					(CategoryEntityInterface) container.getAbstractEntity(), attributeValueMap,
					categoryRecordId, sessionDataBean, userId);
		}
		
		audit(sessionDataBean, container, attributeValueMap, recordIdentifier, "UPDATE");		
		return isEdited;
	}

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}	
	
	private void audit(SessionDataBean sdb, ContainerInterface container, Map<BaseAbstractAttributeInterface, Object> valueMap, Long recId, String operation) {
		try {
			FormAuditManager.getInstance().audit(sdb, container, valueMap, recId, operation);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
