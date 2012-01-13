
package edu.common.dynamicextensions.ui.webui.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.util.SQLQueryManager;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.dao.query.generator.ColumnValueBean;

public class DisplayFormDataInGridAction extends BaseDynamicExtensionsAction
{

	@SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long formContextId = Long.valueOf(request.getParameter(DEConstants.FORM_CONTEXT_ID));
		SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
				DEConstants.SESSION_DATA);
		List<ColumnValueBean> columnValueBeans = new ArrayList<ColumnValueBean>();
		columnValueBeans.add(new ColumnValueBean(formContextId));
		List<Long> dynamicRecIds = (List<Long>) SQLQueryManager.executeQuery(
				DEConstants.RECORD_ID_FROM_FORM_CONTEXT_ID, columnValueBeans, sessionDataBean);

		return mapping.findForward(DEConstants.SUCCESS);
	}

}
