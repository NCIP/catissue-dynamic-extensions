
package edu.common.dynamicextensions.action.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
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

/**
 * It populates the Attribute values entered in the dynamically generated controls.
 * @author chetan_patil, suhas_khot
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
		String mode = request.getParameter(WebUIManagerConstants.MODE_PARAM_NAME);
		try
		{
			if (mode.equals("cancel"))
			{
				handleCancel(request);
			}//handle all ajax action like calculated attributes and the skip logic attribute
			else if (FormDataCollectionUtility.isAjaxAction(request))
			{
				handleAjaxAction(request);
			}//handle details link clicks
			else if(Constants.INSERT_CHILD_DATA.equals(request.getParameter(Constants.DATA_ENTRY_OPERATION)))
			{
				DataEntryForm dataEntryForm = DynamicExtensionsUtility.poulateDataEntryForm(request);
				updateRequestParameter(request, dataEntryForm);
				if ((mode != null) && mode.equals("edit"))
				{
					FormDataCollectionUtility collectionUtility = new FormDataCollectionUtility();
					collectionUtility.populateAndValidateValues(request);
					
				}
				defaultForward(request, response);
				
			}else //form data submitted
			{
				//to check whether main form or subform submitted
				boolean isMainForm  = FormCache.isMainForm(request);
				
				HashSet<String> errorList = null;
				DataEntryForm dataEntryForm = DynamicExtensionsUtility.poulateDataEntryForm(request);
				updateRequestParameter(request, dataEntryForm);

				//value map updated only for edit mode
				if ((mode != null) && mode.equals("edit"))
				{
					FormDataCollectionUtility collectionUtility = new FormDataCollectionUtility();
					errorList = collectionUtility.populateAndValidateValues(request);
					
				}

				if ((errorList != null) && errorList.isEmpty())
				{
					
					if (isMainForm)
					{
						FormManager formManager = new FormManager();
						String recordIdentifier = formManager.persistFormData(request);
						response.sendRedirect(getCallbackURL(request, response, recordIdentifier,
								WebUIManagerConstants.SUCCESS, dataEntryForm.getContainerId()));
						// clear all session data on successful data submission
						CacheManager.clearCache(request);
					}else
					{
						defaultForward(request, response);
					}

				}
				else
				{
					defaultForward(request, response);
				}

				/* resets parameter map from the wrapper request object */
				UserInterfaceiUtility.resetRequestParameterMap(request);

			}
		}
		catch (Exception exception)
		{
			throw new ServletException(exception);
		}

	}

	/**
	 * used for managing calculated attributes and the skip logic
	 * @param request
	 * @throws FileNotFoundException
	 * @throws DynamicExtensionsSystemException
	 * @throws IOException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void handleAjaxAction(HttpServletRequest request) throws FileNotFoundException,
			DynamicExtensionsSystemException, IOException, DynamicExtensionsApplicationException
	{
		FormDataCollectionUtility collectionUtility = new FormDataCollectionUtility();
		collectionUtility.populateAndValidateValues(request);
	}

	private void defaultForward(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		String actionForward;
		actionForward = WebUIManagerConstants.LOAD_DATA_ENTRY_FORM_ACTION_URL;
		RequestDispatcher rd = getServletContext().getRequestDispatcher(actionForward);
		rd.forward(request, response);
	}

	private void handleCancel(HttpServletRequest request)
	{
		// TODO Auto-generated method stub

	}

	private void updateRequestParameter(HttpServletRequest request, DataEntryForm dataEntryForm)
	{
		request.setAttribute(Constants.DATA_ENTRY_FORM, dataEntryForm);
		if ((request.getParameter(DEConstants.IS_DIRTY) != null)
				&& request.getParameter(DEConstants.IS_DIRTY).equalsIgnoreCase(DEConstants.TRUE))
		{
			request.setAttribute(DEConstants.IS_DIRTY, DEConstants.TRUE);
		}
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

		}
		return calllbackURL;
	}
}