
package edu.common.dynamicextensions.action.core;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.bizlogic.BizLogicFactory;
import edu.common.dynamicextensions.bizlogic.FormObjectGridDataBizLogic;
import edu.common.dynamicextensions.domain.FormGridObject;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.velocity.VelocityManager;
import edu.wustl.common.beans.SessionDataBean;

/**
 * @author Amol Pujari
 *
 */
public class LoadDisplayFormDataInGridAction extends BaseDynamicExtensionsAction
{

	//This is an ajax call for loading the data in FormDataGrid
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long formContextId = Long.valueOf(request.getParameter(DEConstants.FORM_CONTEXT_ID));
		String hookEntityId = (String) request.getParameter(DEConstants.RECORD_ENTRY_ENTITY_ID);
		String formUrl = (String) request.getParameter(DEConstants.FORM_URL);
		String deUrl = (String) request.getParameter(DEConstants.DE_URL);
		String hookObjectRecordIdParameter = request
				.getParameter(DEConstants.HOOK_OBJECT_RECORD_ID);
		Long hookObjectRecordId = null;
		if (!"".equalsIgnoreCase(hookObjectRecordIdParameter))
		{
			hookObjectRecordId = Long.valueOf(hookObjectRecordIdParameter);
		}

		FormObjectGridDataBizLogic displayFormDataInGridBizLogic = (FormObjectGridDataBizLogic) BizLogicFactory
				.getBizLogic(FormObjectGridDataBizLogic.class.getName());

		SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
				DEConstants.SESSION_DATA);

		List<FormGridObject> gridObjectList = displayFormDataInGridBizLogic.getFormDataForGrid(
				formContextId, hookEntityId, sessionDataBean, formUrl, deUrl, hookObjectRecordId);
		
		String responseString = VelocityManager.getInstance().evaluate(gridObjectList,
				Constants.VM_TEMPLATE_FILENAME_FOR_FORM_DATA_GRID);
		response.setContentType(Constants.CONTENT_TYPE_XML);
		response.getWriter().write(responseString);
		return null;
	}

}
