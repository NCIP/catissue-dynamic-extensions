/*
 * Created on Oct 18, 2006
 * @author
 *
 */

package edu.common.dynamicextensions.ui.webui.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityGroupManager;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.ui.webui.actionform.ControlsForm;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.ui.webui.util.WebUIManager;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.MetaDataIntegrator;
import edu.common.dynamicextensions.util.global.DEConstants;

/**
 * @author preeti_munot
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SaveEntityAction extends BaseDynamicExtensionsAction
{

	/**
	 * @param mapping ActionMapping mapping
	 * @param form ActionForm form
	 * @param  request HttpServletRequest request
	 * @param response HttpServletResponse response
	 * @return ActionForward forward to next action
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	{
		ActionForward actionForward = null;
		try
		{
			//Get container interface from cache
			ContainerInterface containerInterface = (ContainerInterface) CacheManager
					.getObjectFromCache(request, DEConstants.CONTAINER_INTERFACE);
			ControlsForm controlsForm = (ControlsForm) form;
			ContainerInterface currentContainerInterface = WebUIManager
					.getCurrentContainer(request);
			if ((controlsForm != null) && (currentContainerInterface != null))
			{
				ControlsUtility.reinitializeSequenceNumbers(currentContainerInterface
						.getControlCollection(), controlsForm.getControlsSequenceNumbers());
			}
				String formName = "";
			((EntityInterface) containerInterface.getAbstractEntity()).getEntityGroup()
					.addMainContainer(containerInterface);
			EntityGroupManager.getInstance().persistEntityGroup(
					((EntityInterface) containerInterface.getAbstractEntity()).getEntityGroup());

			addHooking((String) request.getSession().getAttribute("selectedStaticEntityId" ), containerInterface);

			if ((containerInterface != null) && (containerInterface.getAbstractEntity() != null))
			{
				formName = containerInterface.getAbstractEntity().getName();
			}
			saveMessages(request, getSuccessMessage(formName));
			String callbackURL = (String) CacheManager.getObjectFromCache(request,
					DEConstants.CALLBACK_URL);
			if (callbackURL != null && !callbackURL.equals(""))
			{
				String associationIds = CacheManager.getAssociationIds(request);
				callbackURL = callbackURL + "?" + WebUIManager.getOperationStatusParameterName()
						+ "=" + WebUIManagerConstants.SUCCESS + "&"
						+ WebUIManager.getContainerIdentifierParameterName() + "="
						+ containerInterface.getId().toString() + "&"
						+ WebUIManagerConstants.DELETED_ASSOCIATION_IDS + "=" + associationIds;

				CacheManager.clearCache(request);
				response.sendRedirect(callbackURL);
				return null;
			}
			actionForward = mapping.findForward(DEConstants.SUCCESS);
		}
		catch (Exception e)
		{
			String actionForwardString = catchException(e, request);
			if ((actionForwardString == null) || (actionForwardString.equals("")))
			{
				return mapping.getInputForward();
			}
			actionForward = mapping.findForward(actionForwardString);
		}
		return actionForward;
	}



	/**
	 * @param staticEntityId
	 * @param containerInterface
	 */
	private void addHooking(String staticEntityId,
			ContainerInterface containerInterface) {

		MetaDataIntegrator associateHookEntityUtil= new MetaDataIntegrator();
		associateHookEntityUtil.associateWithHokkEntity(
				containerInterface.getId(), staticEntityId);
	}



	/**
	 * Get messages for successful save of entity
	 * @param formName formname
	 * @return ActionMessages messages
	 */
	private ActionMessages getSuccessMessage(String formName)
	{
		ActionMessages actionMessages = new ActionMessages();
		actionMessages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
				"app.entitySaveSuccessMessage", formName));
		return actionMessages;
	}
}