
package edu.common.dynamicextensions.bizlogic;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import net.sf.ehcache.CacheException;

import org.xml.sax.SAXException;

import edu.common.dynamicextensions.DEIntegration.DEIntegration;
import edu.common.dynamicextensions.domain.FormGridObject;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.dao.exception.DAOException;

public class FormObjectGridDataBizLogic extends DefaultBizLogic
{

	public List<FormGridObject> getFormDataForGrid(Long formContextId, Long containerId,
			String hookEntityId, SessionDataBean sessionDataBean)
			throws DynamicExtensionsSystemException, DAOException, JAXBException, SAXException,
			DynamicExtensionsApplicationException, CacheException, BizLogicException
	{
		List<FormGridObject> gridObjectList = new ArrayList<FormGridObject>();
		
		RecordEntryBizLogic recordEntryBizLogic = (RecordEntryBizLogic) BizLogicFactory
				.getBizLogic(RecordEntryBizLogic.class.getName());
		
		DEIntegration deItegration = new DEIntegration();
		Long dynamicRecEntryId;

		List<?> recordEntryIds = recordEntryBizLogic.getRecordEntryId(formContextId, containerId,
				hookEntityId, sessionDataBean);

		for (Object recordEntryId : recordEntryIds)
		{
			ArrayList<?> object = (ArrayList<?>) recordEntryId;
			FormGridObject gridObject = new FormGridObject();
			gridObject.setRecordEntryId((Long.valueOf((String) object.get(0))));
			dynamicRecEntryId = deItegration.getDynamicRecordFromStaticId(
					gridObject.getRecordEntryId().toString(), containerId, hookEntityId).iterator()
					.next();
			gridObject
					.setFormURL("LoadDataEntryFormAction.do?dataEntryOperation=insertParentData&containerIdentifier="
							+ containerId + "&recordIdentifier=" + dynamicRecEntryId);
			if(object.size() > 1)
			{
				gridObject.setUsername((String) object.get(1));
				gridObject.setLastUpdated((String) object.get(2));
			}
			gridObjectList.add(gridObject);
		}

		return gridObjectList;
	}

}
