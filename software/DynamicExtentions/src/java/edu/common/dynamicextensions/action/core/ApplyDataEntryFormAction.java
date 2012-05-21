
package edu.common.dynamicextensions.action.core;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.webui.actionform.DataEntryForm;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.ui.webui.util.FormCache;
import edu.common.dynamicextensions.ui.webui.util.FormDataCollectionUtility;
import edu.common.dynamicextensions.ui.webui.util.UserInterfaceiUtility;
import edu.common.dynamicextensions.ui.webui.util.WebUIManager;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.FormManager;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.common.util.logger.Logger;

/**
 * It populates the Attribute values entered in the dynamically generated controls.
 * @author chetan_patil, suhas_khot, Kunal
 */
public class ApplyDataEntryFormAction extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8750223990174751784L;

	/* (non-Javadoc)
	 * @see org.apache.struts.actions.DispatchAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * 1: collect values from UI
	 * 2: Update stack
	 * 3: forward 
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		
		FormManager formManager = new FormManager();
		try
		{
			if (isDetailsLinkClicked(request))
			{
				
				formManager.onDetailsLinkClicked(request);
				defaultForward(request, response);

			}
			else
			//form data submitted
			{
				//to check whether main form or subform submitted
				boolean isMainForm = FormCache.isMainForm(request);

				FormDataCollectionUtility collectionUtility = new FormDataCollectionUtility();
				collectionUtility.populateAndValidateValues(request);


				if(isMainForm)
				{
				
					DataEntryForm dataEntryForm = DynamicExtensionsUtility
							.poulateDataEntryForm(request);
					long recordId = formManager.submitMainFormData(request);
				
					response.sendRedirect(getCallbackURL(request, response, String
							.valueOf(recordId), WebUIManagerConstants.SUCCESS, dataEntryForm
							.getContainerId()));
					// clear all session data on successful data submission
					CacheManager.clearCache(request);
				}
				else
				{
					defaultForward(request, response);
				}

				/* resets parameter map from the wrapper request object */
				UserInterfaceiUtility.resetRequestParameterMap(request);

			}
		}
		catch (DynamicExtensionsApplicationException e) {
			Logger.out.error(e.getMessage());
			defaultForward(request, response);
		}
		catch (Exception exception)
		{
			throw new ServletException(exception);
		}

	}



	private boolean isDetailsLinkClicked(HttpServletRequest request)
	{
		return Constants.INSERT_CHILD_DATA.equals(request
				.getParameter(Constants.DATA_ENTRY_OPERATION));
	}

	

	private void defaultForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		String actionForward;
		actionForward = WebUIManagerConstants.LOAD_DATA_ENTRY_FORM_ACTION_URL;
		RequestDispatcher rd = getServletContext().getRequestDispatcher(actionForward);
		rd.forward(request, response);
	}

	

	/**
	 * This method gets the Callback URL from cache, reforms it and redirect the response to it.
	 * @param request HttpServletRequest to obtain session
	 * @param response HttpServletResponse to redirect the CallbackURL
	 * @param recordIdentifier Identifier of the record to reconstruct the CallbackURL
	 * @return true if CallbackURL is redirected, false otherwise
	 * @throws IOException
	 */
	private String getCallbackURL(HttpServletRequest request, HttpServletResponse response,
			String recordIdentifier, String webUIManagerConstant, String containerId)
			throws IOException
	{
		String calllbackURL = (String) CacheManager.getObjectFromCache(request,
				DEConstants.CALLBACK_URL);
		if ((calllbackURL != null) && !calllbackURL.equals(""))
		{
			if (calllbackURL.contains("?"))
			{
				calllbackURL = calllbackURL + "&" + WebUIManager.getRecordIdentifierParameterName()
						+ "=" + recordIdentifier + "&"
						+ WebUIManager.getOperationStatusParameterName() + "="
						+ webUIManagerConstant + "&containerId=" + containerId;
			}
			else
			{
				calllbackURL = calllbackURL + "?" + WebUIManager.getRecordIdentifierParameterName()
						+ "=" + recordIdentifier + "&"
						+ WebUIManager.getOperationStatusParameterName() + "="
						+ webUIManagerConstant + "&containerId=" + containerId;
			}

			if (Boolean.parseBoolean(request.getParameter(WebUIManagerConstants.ISDRAFT)))
			{
				calllbackURL = calllbackURL + "&" + WebUIManagerConstants.ISDRAFT + "="
						+ request.getParameter(WebUIManagerConstants.ISDRAFT);
			}
			else if (Boolean.parseBoolean(request.getParameter("isFormLocked")))
			{
				calllbackURL = calllbackURL + "&isFormLocked="
						+ request.getParameter("isFormLocked");
			}
		}
		return calllbackURL;
	}
}