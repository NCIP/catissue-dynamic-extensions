
package edu.common.dynamicextensions.action.core;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.ui.webui.util.WebUIManager;
import edu.common.dynamicextensions.util.global.DEConstants;

// TODO: Auto-generated Javadoc
/**
 * The Class DeleteRecordEntryAction.
 * This Action Class is Called when a delete button on a DE form is clicked.
 */
public class DeleteRecordEntryAction extends BaseDynamicExtensionsAction
{

	/*
	 * (non-Javadoc)
	 * @see org.apache.struts.actions.DispatchAction#execute
	 * (org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
	 *  javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		ActionForward actionForward = null;
		String operation = request.getParameter("operation");

		//Perform DE related Stuff here

		//callback the host url
		boolean isCallBack = redirectCallbackURL(request, response, operation);

		if (!isCallBack)
		{
			actionForward = mapping.findForward(DEConstants.SUCCESS);
		}

		return actionForward;
	}

	/**
	 * Redirect callback url.
	 *
	 * @param request the request
	 * @param response the response
	 * @param webUIManagerConstant the web ui manager constant
	 *
	 * @return true, if successful
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private boolean redirectCallbackURL(HttpServletRequest request, HttpServletResponse response,
			String webUIManagerConstant) throws IOException
	{

		String calllbackUrl= (String) CacheManager.getObjectFromCache(request,
				DEConstants.CALLBACK_URL);
		boolean isCallbackURL = false;
		if ((calllbackUrl != null) && !calllbackUrl.equals(""))
		{
			isCallbackURL = true;
			if (calllbackUrl.contains("?"))
			{
				calllbackUrl = calllbackUrl + "&" + WebUIManager.getOperationStatusParameterName()
						+ "=" + webUIManagerConstant;
			}
			else
			{
				calllbackUrl = calllbackUrl + "?" + WebUIManager.getOperationStatusParameterName()
						+ "=" + webUIManagerConstant;
			}

			CacheManager.clearCache(request);
			response.sendRedirect(calllbackUrl);
		}
		return isCallbackURL;
	}

}
