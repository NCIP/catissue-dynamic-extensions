
package edu.common.dynamicextensions.ui.webui.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.bizlogic.RecordEntryBizLogic;
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
		String formContextId = request.getParameter(DEConstants.FORM_CONTEXT_ID);
		SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
				DEConstants.SESSION_DATA);

		RecordEntryBizLogic displayFormDataInGridBizLogic = new RecordEntryBizLogic();
		List<FormGridObject> gridObjectList = displayFormDataInGridBizLogic.getFormDataForGrid(
				Long.valueOf(formContextId), sessionDataBean);

		String responseString = VelocityManager.getInstance().evaluate(gridObjectList,
				Constants.VM_TEMPLATE_FILENAME_FOR_FORM_DATA_GRID);
		response.setContentType(Constants.CONTENT_TYPE_XML);
		response.getWriter().write(responseString);
		return null;
	}

}
