/*
 * Created on Oct 18, 2006
 * @author
 *
 */

package edu.common.dynamicextensions.ui.webui.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
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
import edu.wustl.cab2b.server.cache.EntityCache;

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
				EntityGroupInterface  entityGroupInterface= ((EntityInterface) containerInterface.getAbstractEntity()).getEntityGroup();
				List<EntityInterface> newEntities =	new ArrayList<EntityInterface>();
					getNewEntitiesName(newEntities,entityGroupInterface);
			((EntityInterface) containerInterface.getAbstractEntity()).getEntityGroup()
					.addMainContainer(containerInterface);
			EntityGroupManager.getInstance().persistEntityGroup(
					entityGroupInterface);
			EntityCache.getInstance().updateEntityGroup(entityGroupInterface);

			List<Long> newEntitiesId =new ArrayList<Long>();
				getNewEntitiesIds(newEntitiesId,newEntities);

			addHooking((String) request.getSession().getAttribute("selectedStaticEntityId" ), containerInterface,newEntitiesId);

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



	private void getNewEntitiesIds(List<Long> newEntitiesIds,List<EntityInterface> newEntities) {

		for (EntityInterface entity : newEntities) {

				newEntitiesIds.add(entity.getId());

		}
	}

	private void getNewEntitiesName(List<EntityInterface> newEntities,EntityGroupInterface entityGroupInterface) {
		for (EntityInterface entity : entityGroupInterface.getEntityCollection()) {
			if(entity.getId()==null){
				newEntities.add(entity);
			}
		}
	}



	/**
	 * @param staticEntityId
	 * @param containerInterface
	 * @param newEntitiesId
	 */
	private void addHooking(String staticEntityId,
			ContainerInterface containerInterface, List<Long> newEntitiesId) {

		MetaDataIntegrator associateHookEntityUtil= new MetaDataIntegrator();
		associateHookEntityUtil.associateWithHokkEntity(
				containerInterface.getId(), staticEntityId,newEntitiesId);
		EntityCache.getInstance().updateEntityGroup(associateHookEntityUtil.getHookEntity().getEntityGroup());
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