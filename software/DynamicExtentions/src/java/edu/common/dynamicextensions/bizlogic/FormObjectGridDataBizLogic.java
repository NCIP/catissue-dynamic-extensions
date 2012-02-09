
package edu.common.dynamicextensions.bizlogic;

import java.util.ArrayList;
import java.util.Collection;
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
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.CategoryHelper;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.dao.exception.DAOException;

public class FormObjectGridDataBizLogic extends DefaultBizLogic
{

	public List<FormGridObject> getFormDataForGrid(Long formContextId, Long containerId,
			String hookEntityId, SessionDataBean sessionDataBean, String formUrl,String deUrl, 
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
			if (!map.isEmpty())
			{
				FormGridObject gridObject = new FormGridObject();
				gridObject.setRecordEntryId(recordEntryIdValue);
				dynamicRecEntryId = map.iterator().next();
				gridObject.setFormURL(formUrl + "&recordId=" + dynamicRecEntryId);
				gridObject.setDeUrl(deUrl+"&recordIdentifier="+dynamicRecEntryId);
				gridObject.setColumns(getDisplayValue(dynamicRecEntryId.toString(),
						containerInterface));
				gridObjectList.add(gridObject);
			}
		}

		return gridObjectList;
	}

	public List<String> getDisplayValue(String recordIdentifier,
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

		return getDisplayValue(recordMap);
	}

	private List<String> getDisplayValue(Map<BaseAbstractAttributeInterface, Object> recordMap)
	{
		List<String> showInGridValues = new ArrayList<String>();
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
						showInGridValues.addAll(getDisplayValue(record));
					}
				}
			}
			else if (("true").equalsIgnoreCase(baseAbstractAttributeInterface
					.getTaggedValue("ShowInGrid")))
			{
				showInGridValues.add(recordMap.get(baseAbstractAttributeInterface).toString());
			}
		}
		return showInGridValues;
	}

	public static List<String> getDisplayHeader(CategoryEntityInterface categoryEntityInterface)
	{
		List<String> showInGridValues = new ArrayList<String>();

		for (CategoryAttributeInterface categoryAttributeInterface : categoryEntityInterface
				.getCategoryAttributeCollection())
		{
			if (("true").equalsIgnoreCase(categoryAttributeInterface.getTaggedValue("ShowInGrid")))
			{
				showInGridValues.add(CategoryHelper.getControl(
						(ContainerInterface) categoryAttributeInterface.getCategoryEntity()
								.getContainerCollection().iterator().next(),
						(BaseAbstractAttributeInterface) categoryAttributeInterface).getCaption());
			}
		}

		for (CategoryEntityInterface categoryEntity : categoryEntityInterface.getChildCategories())
		{
			showInGridValues.addAll(getDisplayHeader(categoryEntity));
		}
		return showInGridValues;
	}

}
