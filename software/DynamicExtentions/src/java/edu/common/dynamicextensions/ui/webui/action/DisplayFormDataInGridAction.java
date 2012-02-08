
package edu.common.dynamicextensions.ui.webui.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.bizlogic.FormObjectGridDataBizLogic;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.util.global.DEConstants;

public class DisplayFormDataInGridAction extends BaseDynamicExtensionsAction
{

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		HttpSession session = request.getSession();

		session.setAttribute(DEConstants.FORM_CONTEXT_ID, request
				.getParameter(DEConstants.FORM_CONTEXT_ID));
		session.setAttribute(DEConstants.CONTAINER_ID, request
				.getParameter(DEConstants.CONTAINER_ID));
		session.setAttribute(DEConstants.RECORD_ENTRY_ENTITY_ID, request
				.getParameter(DEConstants.RECORD_ENTRY_ENTITY_ID));
		session.setAttribute(DEConstants.FORM_URL, request.getParameter(DEConstants.FORM_URL));
		session.setAttribute("treeViewKey", request.getParameter("treeViewKey"));
		final ContainerInterface containerInterface = LoadDisplayFormDataInGridAction
				.getContainerInterface(request, request.getParameter(DEConstants.CONTAINER_ID));
		request
				.setAttribute("gridHeaders", FormObjectGridDataBizLogic
						.getDisplayHeader((CategoryEntityInterface) containerInterface
								.getAbstractEntity()));
		return mapping.findForward(DEConstants.SUCCESS);
	}
}
