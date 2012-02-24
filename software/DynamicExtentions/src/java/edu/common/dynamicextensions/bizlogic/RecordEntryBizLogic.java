
package edu.common.dynamicextensions.bizlogic;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import net.sf.ehcache.CacheException;

import org.xml.sax.SAXException;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.SQLQueryManager;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

public class RecordEntryBizLogic extends DefaultBizLogic
{

	public List<?> getRecordEntryId(Long formContextId, SessionDataBean sessionDataBean)
			throws DynamicExtensionsSystemException, DAOException, JAXBException, SAXException,
			DynamicExtensionsApplicationException, CacheException
	{

		List<ColumnValueBean> columnValueBeans = new ArrayList<ColumnValueBean>();
		columnValueBeans.add(new ColumnValueBean(formContextId));
		columnValueBeans.add(new ColumnValueBean("Disabled"));
		List<?> recordEntryIds = SQLQueryManager.executeQuery(
				DEConstants.RECORD_ID_FROM_FORM_CONTEXT_ID, columnValueBeans, sessionDataBean);

		return recordEntryIds;
	}

}
