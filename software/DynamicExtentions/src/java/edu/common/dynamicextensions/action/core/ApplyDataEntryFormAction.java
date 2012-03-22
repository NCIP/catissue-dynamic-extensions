
package edu.common.dynamicextensions.action.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.webui.actionform.DataEntryForm;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.ui.webui.util.FormDataCollectionUtility;
import edu.common.dynamicextensions.ui.webui.util.UserInterfaceiUtility;
import edu.common.dynamicextensions.ui.webui.util.WebUIManager;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.FormManager;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.common.util.logger.Logger;

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

		String actionForward = null;
		boolean isCallbackURL = false;
		List<String> errorList = null;

		DataEntryForm dataEntryForm = poulateDataEntryForm(request);
		updateRequestParameter(request, dataEntryForm);

		try
		{

			String mode = request.getParameter(WebUIManagerConstants.MODE_PARAM_NAME);

			if ((mode != null) && mode.equals("edit"))
			{
				FormDataCollectionUtility collectionUtility = new FormDataCollectionUtility();
				errorList = collectionUtility.populateAndValidateValues(request);
			}
			
			//remove stack if data is submitted for the subform.
			if (!isAjaxAction(request)
					&& "calculateAttributes".equals(request
							.getParameter(Constants.DATA_ENTRY_OPERATION)) && errorList.isEmpty())
			{
				updateStack(request, dataEntryForm);
			}
			actionForward = getMappingForwardAction(dataEntryForm, errorList, mode);
			if (((actionForward != null) && actionForward.equals("/DynamicExtensionHomePage.do"))
					&& ((mode != null) && mode.equals("cancel")))
			{
				String recordIdentifier = dataEntryForm.getRecordIdentifier();
				isCallbackURL = redirectCallbackURL(request, response, recordIdentifier,
						WebUIManagerConstants.CANCELLED, dataEntryForm.getContainerId());
			}

			FormManager formManager = new FormManager();
			if ((actionForward == null) && (errorList != null) && errorList.isEmpty())
			{
				String recordIdentifier = formManager.persistFormData(request);
				isCallbackURL = redirectCallbackURL(request, response, recordIdentifier,
						WebUIManagerConstants.SUCCESS, dataEntryForm.getContainerId());
			}
			String breadCrumbPosition = request.getParameter(DEConstants.BREAD_CRUMB_POSITION);
			if (!StringUtils.isEmpty(breadCrumbPosition))
			{
				formManager.onBreadCrumbClick(request, breadCrumbPosition);
			}
		}
		catch (Exception exception)
		{
			Logger.out.error(exception.getMessage());
			/*return getExceptionActionForward(exception, mapping, request);*/
		}
		/* resets parameter map from the wrapper request object */
		UserInterfaceiUtility.resetRequestParameterMap(request);

		if (isCallbackURL)
		{
			actionForward = null;
		}
		else if (actionForward == null)
		{
			if ((errorList != null) && errorList.isEmpty())
			{
				UserInterfaceiUtility.clearContainerStack(request);
			}
			actionForward = WebUIManagerConstants.LOAD_DATA_ENTRY_FORM_ACTION_URL;

			RequestDispatcher rd = getServletContext().getRequestDispatcher(actionForward);
			rd.forward(request, response);
		}
		else
		{
			RequestDispatcher rd = getServletContext().getRequestDispatcher(actionForward);
			rd.forward(request, response);
		}

	}

	private void updateRequestParameter(HttpServletRequest request, DataEntryForm dataEntryForm)
	{
		request.setAttribute("dataEntryForm", dataEntryForm);
		if ((request.getParameter(DEConstants.IS_DIRTY) != null)
				&& request.getParameter(DEConstants.IS_DIRTY).equalsIgnoreCase(DEConstants.TRUE))
		{
			request.setAttribute(DEConstants.IS_DIRTY, DEConstants.TRUE);
		}
	}

	private DataEntryForm poulateDataEntryForm(HttpServletRequest request)
	{
		DataEntryForm dataEntryForm = new DataEntryForm();
		dataEntryForm.setContainerId(request.getParameter(DEConstants.CONTAINER_ID));
		dataEntryForm.setRecordIdentifier(request.getParameter(DEConstants.RECORD_IDENTIFIER));
		dataEntryForm.setMode(request.getParameter(WebUIManagerConstants.MODE_PARAM_NAME));
		dataEntryForm.setDataEntryOperation(request.getParameter(Constants.DATA_ENTRY_OPERATION));
		return dataEntryForm;
	}

	@SuppressWarnings("unchecked")
	private void updateStack(HttpServletRequest request, DataEntryForm dataEntryForm)
	{
		Stack<ContainerInterface> containerStack = (Stack<ContainerInterface>) CacheManager
				.getObjectFromCache(request, DEConstants.CONTAINER_STACK);
		Stack<Map<BaseAbstractAttributeInterface, Object>> valueMapStack = (Stack<Map<BaseAbstractAttributeInterface, Object>>) CacheManager
				.getObjectFromCache(request, DEConstants.VALUE_MAP_STACK);

		Long scrollPos = 0L;
		Stack<Long> scrollTopStack = (Stack<Long>) CacheManager.getObjectFromCache(request,
				DEConstants.SCROLL_TOP_STACK);
		scrollPos = scrollTopStack.peek();
		request.setAttribute(DEConstants.SCROLL_POSITION, scrollPos);

		UserInterfaceiUtility.removeContainerInfo(containerStack, valueMapStack);
		scrollTopStack.pop();

	}

	private boolean isAjaxAction(HttpServletRequest request)
	{
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
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
			String recordIdentifier, String webUIManagerConstant, String containerId)
			throws IOException
	{
		boolean isCallbackURL = false;
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

			CacheManager.clearCache(request);
			response.sendRedirect(calllbackURL);
			isCallbackURL = true;
		}
		return isCallbackURL;
	}

	/**
	 * This method gets the ActionForward on the Exception.
	 * @param exception Exception instance
	 * @param mapping ActionMapping to get ActionForward
	 * @param request HttpServletRequest to save error messages in.
	 * @return Appropriate ActionForward.
	 */
	private ActionForward getExceptionActionForward(Exception exception, ActionMapping mapping,
			HttpServletRequest request)
	{
		ActionForward exceptionActionForward = null;
		String actionForwardString = catchException(exception, request);
		if ((actionForwardString == null) || (actionForwardString.equals("")))
		{
			exceptionActionForward = mapping.getInputForward();
		}
		else
		{
			exceptionActionForward = mapping.findForward(actionForwardString);
		}
		return exceptionActionForward;
	}

	private String catchException(Exception exception, HttpServletRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * This method sets dataentry operations parameters and returns the appropriate
	 * ActionForward depending on the "mode" of the operation and validation errors.
	 * @param mapping ActionMapping to get the ActionForward
	 * @param dataEntryForm ActionForm
	 * @param errorList List of validation error messages generated.
	 * @param mode Mode of the operation viz., edit, view, cancel
	 * @return ActionForward
	 */
	private String getMappingForwardAction(DataEntryForm dataEntryForm, List<String> errorList,
			String mode)
	{
		String dataEntryOperation = dataEntryForm.getDataEntryOperation();
		String actionForward = null;
		if (dataEntryOperation != null)
		{
			if (errorList == null)
			{
				dataEntryForm.setErrorList(new ArrayList<String>());
			}

			if ("insertChildData".equals(dataEntryOperation))
			{
				if ((errorList != null) && !(errorList.isEmpty()))
				{
					actionForward = WebUIManagerConstants.LOAD_DATA_ENTRY_FORM_ACTION_URL;
				}
				else if ((mode != null) && (mode.equals("cancel")))
				{
					actionForward = WebUIManagerConstants.LOAD_DATA_ENTRY_FORM_ACTION_URL;
				}
				else
				{
					actionForward = WebUIManagerConstants.LOAD_DATA_ENTRY_FORM_ACTION_URL;
				}
			}
			else if ("insertParentData".equals(dataEntryOperation))
			{
				if ((errorList != null) && !(errorList.isEmpty()))
				{
					actionForward = WebUIManagerConstants.LOAD_DATA_ENTRY_FORM_ACTION_URL;
				}
				else if ((mode != null) && (mode.equals("cancel")))
				{
					actionForward = "/DynamicExtensionHomePage.do";
				}

				else
				{
					actionForward = WebUIManagerConstants.LOAD_DATA_ENTRY_FORM_ACTION_URL;
				}
			}
			else if ("calculateAttributes".equals(dataEntryOperation))
			{
				actionForward = WebUIManagerConstants.LOAD_DATA_ENTRY_FORM_ACTION_URL;
			}
			else if ("skipLogicAttributes".equals(dataEntryOperation))
			{
				actionForward = WebUIManagerConstants.LOAD_DATA_ENTRY_FORM_ACTION_URL;
			}
		}
		return actionForward;
	}

}