
package edu.common.dynamicextensions.action.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.ui.webui.util.WebUIManager;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.dao.newdao.ActionStatus;

// TODO: Auto-generated Javadoc
/**
 * The Class DeleteRecordEntryAction.
 * This Action Class is Called when a delete button on a DE form is clicked.
 */
public class DeleteRecordEntryAction extends BaseServletAction
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9089768030439023652L;

	/*
	 * (non-Javadoc)
	 * @see org.apache.struts.actions.DispatchAction#execute
	 * (org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
	 *  javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		String operation = request.getParameter("operation");

		boolean isCallBack = redirectCallbackURL(request, response, operation);

		if (!isCallBack)
		{
			forward(request, response, WebUIManagerConstants.LOAD_DATA_ENTRY_FORM_ACTION_URL);
		}
		request.setAttribute(ActionStatus.ACTIONSTAUS, ActionStatus.SUCCESSFUL);

		
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
