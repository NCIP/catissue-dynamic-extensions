
package edu.common.dynamicextensions.dem;

import java.util.ArrayList;
import java.util.Map;

import edu.common.dynamicextensions.dao.impl.DynamicExtensionDAO;
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
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;

public class DyanamicObjectProcessor
{


	private final HibernateDAO hibernateDAO;

	public DyanamicObjectProcessor() throws DAOException
	{
		hibernateDAO = (HibernateDAO) DAOConfigFactory.getInstance().getDAOFactory("dem").getDAO();
		hibernateDAO.openSession(null);
		DynamicExtensionDAO.getInstance();
	}

	public Object editObject(Map<String, Object> paramaterObjectMap)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException,
			DAOException
	{
		Map<BaseAbstractAttributeInterface, Object> attributeValueMap = (Map<BaseAbstractAttributeInterface, Object>) paramaterObjectMap.get(WebUIManagerConstants.DATA_VALUE_MAP);
		Long recordIdentifier=(Long)paramaterObjectMap.get(WebUIManagerConstants.RECORD_ID);
		SessionDataBean sessionDataBean=(SessionDataBean)paramaterObjectMap.get(WebUIManagerConstants.SESSION_DATA_BEAN);
		Long userId=(Long)paramaterObjectMap.get(WebUIManagerConstants.USER_ID);
		ContainerInterface container=(ContainerInterface)paramaterObjectMap.get(WebUIManagerConstants.CONTAINER);
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
		return isEdited;
	}

	public Long insertDataEntryForm(Map<String, Object> paramaterObjectMap)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		Map<BaseAbstractAttributeInterface, Object> attributeValueMap = (Map<BaseAbstractAttributeInterface, Object>) paramaterObjectMap.get(WebUIManagerConstants.DATA_VALUE_MAP);
		SessionDataBean sessionDataBean=(SessionDataBean)paramaterObjectMap.get(WebUIManagerConstants.SESSION_DATA_BEAN);
		Long userId=(Long)paramaterObjectMap.get(WebUIManagerConstants.USER_ID);
		ContainerInterface container=(ContainerInterface)paramaterObjectMap.get(WebUIManagerConstants.CONTAINER);
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

		return recordIdentifier;
	}
}
