
package edu.common.dynamicextensions.bizlogic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import edu.common.dynamicextensions.domain.FormGridObject;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.SQLQueryManager;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

public class RecordEntryBizLogic
{

	@SuppressWarnings("unchecked")
	public List<FormGridObject> getFormDataForGrid(Long formContextId, SessionDataBean sessionDataBean)
			throws DynamicExtensionsSystemException, DAOException, JAXBException, SAXException
	{
		List<FormGridObject> gridObjectList = new ArrayList<FormGridObject>();
		FormGridObject gridObject = new FormGridObject();
		List<ColumnValueBean> columnValueBeans = new ArrayList<ColumnValueBean>();
		columnValueBeans.add(new ColumnValueBean(formContextId));
		List<?> objs = SQLQueryManager.executeQuery(DEConstants.RECORD_ID_FROM_FORM_CONTEXT_ID,
				columnValueBeans, sessionDataBean);
		Iterator i = objs.iterator();
		while (i.hasNext())
		{
			ArrayList<?> object = (ArrayList<?>) i.next();
			gridObject.setIdentifier((Long.valueOf((String)object.get(0))));
			gridObject.setUsername((String) object.get(1));
			gridObject.setLastUpdated((String)object.get(2));
			gridObjectList.add(gridObject);
		}
		return gridObjectList;
	}

}
