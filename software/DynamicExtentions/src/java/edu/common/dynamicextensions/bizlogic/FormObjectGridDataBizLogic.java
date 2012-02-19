
package edu.common.dynamicextensions.bizlogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import net.sf.ehcache.CacheException;

import org.xml.sax.SAXException;

import edu.common.dynamicextensions.DEIntegration.DEIntegration;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.FormGridObject;
import edu.common.dynamicextensions.domaininterface.AssociationMetadataInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.CategoryHelper;
import edu.common.dynamicextensions.util.global.CategoryConstants;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.dao.exception.DAOException;

public class FormObjectGridDataBizLogic extends DefaultBizLogic
{

	/**
	 * This method will populate list of FormGridObject and is used to create response XML string for the grid
	 * @param formContextId
	 * @param hookEntityId
	 * @param sessionDataBean
	 * @param formUrl
	 * @param deUrl
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws DynamicExtensionsApplicationException
	 * @throws CacheException
	 * @throws BizLogicException
	 */
	public List<FormGridObject> getFormDataForGrid(Long formContextId, String hookEntityId,
			SessionDataBean sessionDataBean, String formUrl, String deUrl)
			throws DynamicExtensionsSystemException, DAOException, JAXBException, SAXException,
			DynamicExtensionsApplicationException, CacheException, BizLogicException
	{
		List<FormGridObject> gridObjectList = new ArrayList<FormGridObject>();
		DomainObjectFactory factory = DomainObjectFactory.getInstance();

		RecordEntryBizLogic recordEntryBizLogic = (RecordEntryBizLogic) BizLogicFactory
				.getBizLogic(RecordEntryBizLogic.class.getName());

		DEIntegration deItegration = new DEIntegration();
		Long dynamicRecEntryId = null;

		Long containerId = CategoryManager.getInstance().getContainerIdByFormContextId(
				formContextId, sessionDataBean);
		final ContainerInterface containerInterface = EntityCache.getInstance().getContainerById(
				containerId);

		List<?> recordEntryIds = recordEntryBizLogic.getRecordEntryId(formContextId,
				sessionDataBean);

		for (Object recordEntryId : recordEntryIds)
		{
			ArrayList<?> object = (ArrayList<?>) recordEntryId;

			Long recordEntryIdValue = (Long.valueOf((String) object.get(0)));

			Collection<Long> map = deItegration.getDynamicRecordFromStaticId(recordEntryIdValue
					.toString(), containerId, hookEntityId);
			Map<String, String> headers = getDisplayHeader((CategoryEntityInterface) containerInterface
					.getAbstractEntity());
			if (!map.isEmpty())
			{
				FormGridObject gridObject = factory.createFormGridObject();
				gridObject.setRecordEntryId(recordEntryIdValue);
				dynamicRecEntryId = map.iterator().next();
				gridObject
						.setFormURL(formUrl + DEConstants.RECORD_ID_URL_PARAM + dynamicRecEntryId);//formUrl is used when we need to open the form in edit mode
				gridObject.setDeUrl(containerId+","+dynamicRecEntryId);//deUrl is used when we want to print the form
				gridObject.setColumns(getDisplayValue(dynamicRecEntryId.toString(),
						containerInterface));
				gridObject.setHeaders(headers);
				gridObjectList.add(gridObject);
			}
		}
		Collections.sort(gridObjectList);
		return gridObjectList;
	}

	/**
	 * This method returns
	 * @param recordIdentifier
	 * @param containerInterface
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public Map<String, String> getDisplayValue(String recordIdentifier,
			ContainerInterface containerInterface) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		CategoryEntityInterface categoryEntityInterface = (CategoryEntityInterface) containerInterface
				.getAbstractEntity();
		CategoryManagerInterface categoryManagerInterface = CategoryManager.getInstance();
		Long recordId = categoryManagerInterface.getRootCategoryEntityRecordIdByEntityRecordId(Long
				.valueOf(recordIdentifier), categoryEntityInterface.getTableProperties().getName());
		Map<BaseAbstractAttributeInterface, Object> recordMap = categoryManagerInterface
				.getRecordById(categoryEntityInterface, recordId);
		return this.populateDisplayValue(recordMap);
	}

	/**
	 * populates the map with respective attribute name and it's value. Map<AttributeName,value>
	 * @param recordMap
	 * @return
	 */

	@SuppressWarnings("unchecked")
	private Map<String, String> populateDisplayValue(
			Map<BaseAbstractAttributeInterface, Object> recordMap)
	{
		Map<String, String> showInGridValues = new HashMap<String, String>();

		for (BaseAbstractAttributeInterface baseAbstractAttributeInterface : recordMap.keySet())
		{

			if (baseAbstractAttributeInterface instanceof AssociationMetadataInterface)
			{
				CategoryAssociationInterface categoryAssociationInterface = (CategoryAssociationInterface) baseAbstractAttributeInterface;
				if (categoryAssociationInterface.getTargetCategoryEntity().getNumberOfEntries() == 1)
				{
					for (Map<BaseAbstractAttributeInterface, Object> record : (List<Map<BaseAbstractAttributeInterface, Object>>) recordMap
							.get(baseAbstractAttributeInterface))
					{
						showInGridValues.putAll(populateDisplayValue(record));
					}
				}
			}
			else if ((Boolean.TRUE.toString()).equalsIgnoreCase(baseAbstractAttributeInterface
					.getTaggedValue(CategoryConstants.SHOW_IN_GRID)))
			{

				String header = CategoryHelper
						.getControl(
								((ContainerInterface) ((CategoryAttributeInterface) baseAbstractAttributeInterface)
										.getCategoryEntity().getContainerCollection().iterator()
										.next()), baseAbstractAttributeInterface).getCaption();
				if (recordMap.get(baseAbstractAttributeInterface) instanceof ArrayList<?>)
				{
					Map<Object, Object> map = ((Map<Object, Object>) ((ArrayList) recordMap
							.get(baseAbstractAttributeInterface)).get(0));
					Object key = map.keySet().iterator().next();
					showInGridValues.put(header, map.get(key).toString());
				}
				else
				{
					showInGridValues.put(header, recordMap.get(baseAbstractAttributeInterface)
							.toString());
				}
			}
		}
		return showInGridValues;
	}

	/**
	 * This will get the attribute name which are marked as showInGrid and will show them as header in the grid
	 * @param categoryEntityInterface
	 * @return
	 */
	public static Map<String, String> getDisplayHeader(
			CategoryEntityInterface categoryEntityInterface)
	{
		Map<String, String> showInGridHeaders = new HashMap<String, String>();

		String dataType;
		for (ControlInterface controlInterface : ((ContainerInterface) categoryEntityInterface
				.getContainerCollection().iterator().next()).getControlCollection())
		{
			if ((Boolean.TRUE.toString()).equalsIgnoreCase(controlInterface
					.getBaseAbstractAttribute().getTaggedValue(CategoryConstants.SHOW_IN_GRID)))
			{
				dataType = controlInterface.getAttibuteMetadataInterface()
						.getAttributeTypeInformation().getDataType();
				UserDefinedDEInterface userDefinedDEInterface = ((UserDefinedDEInterface) (controlInterface
						.getAttibuteMetadataInterface().getDataElement(null)));
				if (userDefinedDEInterface != null)
				{
					if (!userDefinedDEInterface.getPermissibleValues().isEmpty())
					{
						dataType = dataType + DEConstants.PV_POSTFIX;
					}
				}
				showInGridHeaders.put(controlInterface.getCaption(), dataType);
			}
		}

		for (CategoryEntityInterface categoryEntity : categoryEntityInterface.getChildCategories())
		{
			showInGridHeaders.putAll(getDisplayHeader(categoryEntity));
		}
		return showInGridHeaders;
	}

}
