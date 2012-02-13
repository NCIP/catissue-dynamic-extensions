
package edu.common.dynamicextensions.bizlogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import net.sf.ehcache.CacheException;

import org.xml.sax.SAXException;

import edu.common.dynamicextensions.DEIntegration.DEIntegration;
import edu.common.dynamicextensions.domain.FormGridObject;
import edu.common.dynamicextensions.domaininterface.AssociationMetadataInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.CategoryHelper;
import edu.common.dynamicextensions.util.global.CategoryConstants;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.dao.exception.DAOException;

public class FormObjectGridDataBizLogic extends DefaultBizLogic
{

	public List<FormGridObject> getFormDataForGrid(Long formContextId, Long containerId,
			String hookEntityId, SessionDataBean sessionDataBean, String formUrl, String deUrl,
			ContainerInterface containerInterface) throws DynamicExtensionsSystemException,
			DAOException, JAXBException, SAXException, DynamicExtensionsApplicationException,
			CacheException, BizLogicException
	{
		List<FormGridObject> gridObjectList = new ArrayList<FormGridObject>();

		RecordEntryBizLogic recordEntryBizLogic = (RecordEntryBizLogic) BizLogicFactory
				.getBizLogic(RecordEntryBizLogic.class.getName());

		DEIntegration deItegration = new DEIntegration();
		Long dynamicRecEntryId = null;

		List<?> recordEntryIds = recordEntryBizLogic.getRecordEntryId(formContextId, containerId,
				hookEntityId, sessionDataBean);

		for (Object recordEntryId : recordEntryIds)
		{
			ArrayList<?> object = (ArrayList<?>) recordEntryId;

			Long recordEntryIdValue = (Long.valueOf((String) object.get(0)));

			Collection<Long> map = deItegration.getDynamicRecordFromStaticId(recordEntryIdValue
					.toString(), containerId, hookEntityId);
			List<String> headers = getDisplayHeader((CategoryEntityInterface)containerInterface.getAbstractEntity());
			if (!map.isEmpty())
			{
				FormGridObject gridObject = new FormGridObject();
				gridObject.setRecordEntryId(recordEntryIdValue);
				dynamicRecEntryId = map.iterator().next();
				gridObject.setFormURL(formUrl + "&recordId=" + dynamicRecEntryId);
				gridObject.setDeUrl(deUrl + "&recordIdentifier=" + dynamicRecEntryId);
				//getDisplayValue(dynamicRecEntryId.toString(),containerInterface);
				
				gridObject.setColumns(getDisplayValue(dynamicRecEntryId.toString(),containerInterface));
				gridObject.setHeaders(headers);
				gridObjectList.add(gridObject);
			}
		}

		return gridObjectList;
	}

	public Map<String,String> getDisplayValue(String recordIdentifier,
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
		return this.getDisplayValue(recordMap);
	}

	@SuppressWarnings("unchecked")
	private Map<String,String> getDisplayValue(Map<BaseAbstractAttributeInterface, Object> recordMap)
	{
		Map<String,String> showInGridValues = new HashMap<String, String>();
		String header;
		//		List<String> showInGridValues = new ArrayList<String>();

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
						showInGridValues.putAll(getDisplayValue(record));
//						showInGridValues.addAll(getDisplayValue(record));
					}
				}
			}
			else if ((Boolean.TRUE.toString()).equalsIgnoreCase(baseAbstractAttributeInterface
					.getTaggedValue(CategoryConstants.SHOW_IN_GRID)))
			{
				
				String header1 =CategoryHelper.getControl(((ContainerInterface)((CategoryAttributeInterface)baseAbstractAttributeInterface).getCategoryEntity().getContainerCollection().iterator().next()), baseAbstractAttributeInterface).getCaption();
					//((ContainerInterface)((CategoryAttributeInterface)baseAbstractAttributeInterface).getCategoryEntity().getContainerCollection().iterator().next()).getControlCollection().iterator().next().getCaption();
				
//				header = ((ContainerInterface) (((CategoryAssociationInterface)baseAbstractAttributeInterface).getTargetCategoryEntity().getContainerCollection().iterator().next())).getControlCollection().iterator().next().getCaption();
/*				ControlInterface controlInterface = CategoryHelper
						.getControl(
								(ContainerInterface) ((CategoryAttributeInterface) baseAbstractAttributeInterface)
										.getCategoryEntity().getContainerCollection().iterator()
										.next(), baseAbstractAttributeInterface);
*/
				if (recordMap.get(baseAbstractAttributeInterface) instanceof ArrayList<?>)
				{
					Map<Object, Object> map = ((Map<Object, Object>) ((ArrayList) recordMap
							.get(baseAbstractAttributeInterface)).get(0));
					Object key = map.keySet().iterator().next();
					showInGridValues.put(header1,map.get(key).toString());
//					showInGridValues.add(map.get(key).toString());
					//orderedValues1.put(controlInterface,map.get(key).toString());
				}
				else
				{
					showInGridValues.put(header1,recordMap.get(baseAbstractAttributeInterface).toString());
				}
			}
		}
		return showInGridValues;
	}

	public static List<String> getDisplayHeader(CategoryEntityInterface categoryEntityInterface)
	{
		List<String> showInGridHeaders = new ArrayList<String>();

		for (ControlInterface controlInterface : ((ContainerInterface) categoryEntityInterface
				.getContainerCollection().iterator().next()).getControlCollection())
		{
			if ((Boolean.TRUE.toString()).equalsIgnoreCase(controlInterface.getBaseAbstractAttribute()
					.getTaggedValue(CategoryConstants.SHOW_IN_GRID)))
			{
				showInGridHeaders.add(controlInterface.getCaption());
			}
		}

		for (CategoryEntityInterface categoryEntity : categoryEntityInterface.getChildCategories())
		{
			showInGridHeaders.addAll(getDisplayHeader(categoryEntity));
		}
		return showInGridHeaders;
	}

}
