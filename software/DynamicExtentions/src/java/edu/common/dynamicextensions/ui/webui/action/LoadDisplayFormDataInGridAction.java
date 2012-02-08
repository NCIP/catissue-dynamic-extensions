
package edu.common.dynamicextensions.ui.webui.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.bizlogic.BizLogicFactory;
import edu.common.dynamicextensions.bizlogic.FormObjectGridDataBizLogic;
import edu.common.dynamicextensions.domain.FormGridObject;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.ui.webui.util.UserInterfaceiUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
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
		HttpSession session = request.getSession();
		String formContextId = (String) session.getAttribute(DEConstants.FORM_CONTEXT_ID);
		String hookEntityId = (String) session.getAttribute(DEConstants.RECORD_ENTRY_ENTITY_ID);
		String containerId = (String) session.getAttribute(DEConstants.CONTAINER_ID);
		String formUrl = (String) session.getAttribute(DEConstants.FORM_URL);

		FormObjectGridDataBizLogic displayFormDataInGridBizLogic = (FormObjectGridDataBizLogic) BizLogicFactory
				.getBizLogic(FormObjectGridDataBizLogic.class.getName());

		SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
				DEConstants.SESSION_DATA);

		final ContainerInterface containerInterface = getContainerInterface(request,containerId);
		
		List<String> headersList = FormObjectGridDataBizLogic.getDisplayHeader((CategoryEntityInterface)containerInterface.getAbstractEntity());
		session.setAttribute("gridHeaders", headersList);
		
		List<FormGridObject> gridObjectList = displayFormDataInGridBizLogic.getFormDataForGrid(Long
				.valueOf(formContextId), Long.valueOf(containerId), hookEntityId, sessionDataBean,
				formUrl,containerInterface);
		
		String responseString = VelocityManager.getInstance().evaluate(gridObjectList,
				Constants.VM_TEMPLATE_FILENAME_FOR_FORM_DATA_GRID);
		response.setContentType(Constants.CONTENT_TYPE_XML);
		response.getWriter().write(responseString);
		return null;
	}

	
	public static ContainerInterface getContainerInterface(final HttpServletRequest request,String containerIdentifier)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		ContainerInterface containerInterface = (ContainerInterface) CacheManager
				.getObjectFromCache(request, DEConstants.CONTAINER_INTERFACE);
		if (containerIdentifier != null || containerInterface == null)
		{
			UserInterfaceiUtility.clearContainerStack(request);

			final Long containerId = Long.valueOf(containerIdentifier);

			if (containerId == -1)
			{
				containerInterface = (ContainerInterface) ((CategoryEntityInterface) request
						.getSession().getAttribute("categoryEntity")).getContainerCollection()
						.iterator().next();
			}
			else
			{
				containerInterface = DynamicExtensionsUtility
						.getClonedContainerFromCache(containerId.toString());
			}
			containerInterface.getContainerValueMap().clear();
			DynamicExtensionsUtility.cleanContainerControlsValue(containerInterface);

			CacheManager.addObjectToCache(request, DEConstants.CONTAINER_INTERFACE,
					containerInterface);
		}

		return containerInterface;
	}

}
