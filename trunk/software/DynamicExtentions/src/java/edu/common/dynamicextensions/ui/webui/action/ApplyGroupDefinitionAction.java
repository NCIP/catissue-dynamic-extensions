/*
 * Created on Nov 15, 2006
 * @author
 *
 */

package edu.common.dynamicextensions.ui.webui.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.processor.ApplyGroupDefinitionProcessor;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.webui.actionform.GroupForm;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.ui.webui.util.WebUIManager;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;

/**
 * @author preeti_munot
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ApplyGroupDefinitionAction extends BaseDynamicExtensionsAction
{

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		ActionForward actionForward = null;

		GroupForm groupForm = (GroupForm) form;
		ApplyGroupDefinitionProcessor applyGroupDefinitionProcessor = ApplyGroupDefinitionProcessor
				.getInstance();

		String operationMode = groupForm.getOperationMode();
		EntityGroupInterface entityGroup = null;

		try
		{

			ContainerInterface container = (ContainerInterface) CacheManager.getObjectFromCache(
					request, DEConstants.CONTAINER_INTERFACE);
			//This is incase when dynamic exception shown on UI-eg. Group name already exist ,so in this case container object is not set in request
			//So get container object by Containercaption ,which is available in request.
			if (container == null && !(operationMode.equals(DEConstants.ADD_NEW_FORM)))
			{
				String containerCaption = (String) CacheManager.getObjectFromCache(request,
						DEConstants.CURRENT_CONTAINER_NAME);
				container = DynamicExtensionsUtility.getContainerByCaption(containerCaption);
			}

			entityGroup = applyGroupDefinitionProcessor.saveGroupDetails(groupForm, container,
					operationMode, request);

			if ((operationMode != null) && (operationMode.equals(DEConstants.EDIT_FORM)))
			{
				applyGroupDefinitionProcessor.updateEntityGroup(container, entityGroup, groupForm);
			}
		}
		catch (Exception e)
		{
			String target = catchException(e, request);
			if ((target == null) || (target.equals("")))
			{
				actionForward = mapping.getInputForward();
			}
		}

		if (entityGroup != null && actionForward == null)
		{
			//Add entity group to cache.This will be attached with the entity when the entity is created
			//Till then the object remains in cache
			CacheManager.addObjectToCache(request, DEConstants.ENTITYGROUP_INTERFACE, entityGroup);
		}

		//Redirection logic
		String operationPerformed = groupForm.getGroupOperation();
		boolean isCallbackURL = false;
		if (operationPerformed.equals(ProcessorConstants.SAVE_GROUP) && actionForward == null)
		{
			isCallbackURL = redirectCallbackURL(request, response, WebUIManagerConstants.SUCCESS);

		}
		if (actionForward == null)
		{
			actionForward = getNextPage(groupForm.getGroupOperation(), mapping, isCallbackURL);
		}
		return actionForward;
	}

	/**
	 * @param operationPerformed : Operation performed
	 * @return Action forward for redirection
	 */
	private ActionForward getNextPage(String operationPerformed, ActionMapping mapping,
			boolean isCallbackURL)
	{
		ActionForward actionForward = null;
		if (operationPerformed != null)
		{
			if (operationPerformed.equals(ProcessorConstants.SAVE_GROUP))
			{
				if (!isCallbackURL)
				{
					actionForward = mapping.findForward(DEConstants.SHOW_DYEXTN_HOMEPAGE);
				}
			}
			else
			{
				actionForward = mapping.findForward(DEConstants.SUCCESS);
			}
		}
		return actionForward;
	}

	/**
	 * This method gets the Callback URL from cache, reforms it and redirect the response to it.
	 * @param request HttpServletRequest to obtain session
	 * @param response HttpServletResponse to redirect the CallbackURL
	 * @param recordIdentifier Identifier of the record to reconstruct the CallbackURL
	 * @return true if CallbackURL is redirected, false otherwise
	 * @throws IOException
	 */
	private boolean redirectCallbackURL(HttpServletRequest request, HttpServletResponse response,
			String webUIManagerConstant) throws IOException
	{
		boolean isCallbackURL = false;
		String calllbackURL = (String) CacheManager.getObjectFromCache(request,
				DEConstants.CALLBACK_URL);
		if (calllbackURL != null && !calllbackURL.equals(""))
		{
			String associationIds = CacheManager.getAssociationIds(request);

			calllbackURL = calllbackURL + "?" + WebUIManager.getOperationStatusParameterName()
					+ "=" + webUIManagerConstant + "&"
					+ WebUIManagerConstants.DELETED_ASSOCIATION_IDS + "=" + associationIds;
			CacheManager.clearCache(request);
			response.sendRedirect(calllbackURL);
			isCallbackURL = true;
		}
		return isCallbackURL;
	}
}
