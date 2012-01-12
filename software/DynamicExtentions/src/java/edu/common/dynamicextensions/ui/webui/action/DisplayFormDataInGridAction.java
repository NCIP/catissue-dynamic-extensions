package edu.common.dynamicextensions.ui.webui.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.querysuite.metadata.Utility;


public class DisplayFormDataInGridAction extends BaseDynamicExtensionsAction
{
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long formContextId = Long.valueOf(request.getParameter("formContextId"));
		SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute("sessionData");
		List<Long> dynamicRecIds = Utility.getDynamicRecIdByFormContextId(formContextId, sessionDataBean);
		
		return mapping.findForward(DEConstants.SUCCESS);
	}
	
}
