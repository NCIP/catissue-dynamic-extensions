
package edu.common.dynamicextensions.action.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.bizlogic.FormObjectGridDataBizLogic;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.beans.SessionDataBean;

public class DisplayFormDataInGridAction extends BaseDynamicExtensionsAction
{

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		HttpSession session = request.getSession();
		SessionDataBean sessionDataBean = (SessionDataBean) session
				.getAttribute(DEConstants.SESSION_DATA);
		Long containerId = CategoryManager.getInstance().getContainerIdByFormContextId(
				(Long) request.getAttribute(DEConstants.FORM_CONTEXT_ID),
				sessionDataBean);
		
		final ContainerInterface containerInterface = EntityCache.getInstance().getContainerById(
				containerId);

		request
				.setAttribute(Constants.GRID_HEADERS, FormObjectGridDataBizLogic
						.getDisplayHeader((CategoryEntityInterface) containerInterface
								.getAbstractEntity()));
		return mapping.findForward(DEConstants.SUCCESS);
	}
}