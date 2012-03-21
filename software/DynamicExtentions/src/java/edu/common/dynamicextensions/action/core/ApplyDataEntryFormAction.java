
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

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.ui.webui.actionform.DataEntryForm;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.ui.webui.util.FormDataCollectionUtility;
import edu.common.dynamicextensions.ui.webui.util.UserInterfaceiUtility;
import edu.common.dynamicextensions.ui.webui.util.WebUIManager;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.FormSubmitManager;
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

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException
	{
		doPost(req, resp);
	}

	/* (non-Javadoc)
	 * @see org.apache.struts.actions.DispatchAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		FormDataCollectionUtility collectionUtility = new FormDataCollectionUtility();
		FormSubmitManager formSubmitManager = new FormSubmitManager();
		String actionForward = null;
		boolean isCallbackURL = false;
		List<String> errorList = null;
		if ((request.getParameter(DEConstants.IS_DIRTY) != null)
				&& request.getParameter(DEConstants.IS_DIRTY).equalsIgnoreCase(DEConstants.TRUE))
		{
			request.setAttribute(DEConstants.IS_DIRTY, DEConstants.TRUE);
		}
		Stack<ContainerInterface> containerStack = (Stack<ContainerInterface>) CacheManager
				.getObjectFromCache(request, DEConstants.CONTAINER_STACK);
		Stack<Map<BaseAbstractAttributeInterface, Object>> valueMapStack = (Stack<Map<BaseAbstractAttributeInterface, Object>>) CacheManager
				.getObjectFromCache(request, DEConstants.VALUE_MAP_STACK);
		String containerSize = request.getParameter(DEConstants.BREAD_CRUMB_POSITION);
		String encounterDate = request.getParameter(Constants.ENCOUNTER_DATE);
		if (((containerStack != null) && !containerStack.isEmpty())
				&& ((valueMapStack != null) && !valueMapStack.isEmpty()))
		{
			try
			{
				DataEntryForm dataEntryForm = poulateDataEntryForm(request);
				request.setAttribute("dataEntryForm", dataEntryForm);
				String mode = request.getParameter(WebUIManagerConstants.MODE_PARAM_NAME);
				
				if ((mode != null) && mode.equals("edit"))
				{
					collectionUtility.populateAndValidateValues(containerStack, valueMapStack, request,
							dataEntryForm, ControlsUtility.getFormattedDate(encounterDate));
					errorList = dataEntryForm.getErrorList();
				}
				if (!isAjaxAction(request)
						&& "calculateAttributes".equals(request
								.getParameter(Constants.DATA_ENTRY_OPERATION)))
				{
					errorList = updateStack(request, containerStack, valueMapStack, dataEntryForm);
				}
				actionForward = getMappingForwardAction(dataEntryForm, errorList, mode);
				if (((actionForward != null) && actionForward.equals(
						"/DynamicExtensionHomePage.do"))
						&& ((mode != null) && mode.equals("cancel")))
				{
					String recordIdentifier = dataEntryForm.getRecordIdentifier();
					isCallbackURL = redirectCallbackURL(request, response, recordIdentifier,
							WebUIManagerConstants.CANCELLED, dataEntryForm.getContainerId());
				}

				if ((actionForward == null) && (errorList != null) && errorList.isEmpty())
				{
					String recordIdentifier = formSubmitManager.storeParentContainer(valueMapStack, containerStack,
							request, dataEntryForm.getRecordIdentifier(), dataEntryForm
									.getIsShowTemplateRecord());
					isCallbackURL = redirectCallbackURL(request, response, recordIdentifier,
							WebUIManagerConstants.SUCCESS, dataEntryForm.getContainerId());
				}
				if ((containerSize != null) && (!containerSize.trim().equals("")))
				{
					handleBreadCrumbClick(request, containerStack, valueMapStack, containerSize);
				}
			}
			catch (Exception exception)
			{
				Logger.out.error(exception.getMessage());
				/*return getExceptionActionForward(exception, mapping, request);*/
			}
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
		}else
		{
			RequestDispatcher rd = getServletContext().getRequestDispatcher(actionForward);
			rd.forward(request, response);
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

	private void handleBreadCrumbClick(HttpServletRequest request,
			Stack<ContainerInterface> containerStack,
			Stack<Map<BaseAbstractAttributeInterface, Object>> valueMapStack, String containerSize)
	{
		long containerStackSize = Long.valueOf(containerSize);
		if ((request.getParameter(WebUIManagerConstants.MODE_PARAM_NAME) != null)
				&& (request.getParameter(WebUIManagerConstants.MODE_PARAM_NAME).trim().length() > 0)
				&& (DEConstants.CANCEL.equalsIgnoreCase(request
						.getParameter(WebUIManagerConstants.MODE_PARAM_NAME)) || WebUIManagerConstants.EDIT_MODE
						.equalsIgnoreCase(request
								.getParameter(WebUIManagerConstants.MODE_PARAM_NAME))))
		{
			containerStackSize = containerStackSize + 1;
		}
		while (containerStack.size() != containerStackSize)
		{
			containerStack.pop();
			valueMapStack.pop();
		}
	}

	private List<String> updateStack(HttpServletRequest request,
			Stack<ContainerInterface> containerStack,
			Stack<Map<BaseAbstractAttributeInterface, Object>> valueMapStack,
			DataEntryForm dataEntryForm)
	{
		List<String> errorList;
		errorList = dataEntryForm.getErrorList();
		Long scrollPos = 0L;
		Stack<Long> scrollTopStack = (Stack<Long>) CacheManager.getObjectFromCache(request,
				DEConstants.SCROLL_TOP_STACK);
		scrollPos = scrollTopStack.peek();
		request.setAttribute(DEConstants.SCROLL_POSITION, scrollPos);
		if (errorList != null && errorList.isEmpty() && containerStack != null
				&& !containerStack.isEmpty() && valueMapStack != null && !valueMapStack.isEmpty())
		{
			UserInterfaceiUtility.removeContainerInfo(containerStack, valueMapStack);
			if (errorList != null && errorList.isEmpty() && scrollTopStack != null
					&& !scrollTopStack.isEmpty())
			{
				scrollTopStack.pop();
			}
		}
		return errorList;
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
	private String getMappingForwardAction(
			DataEntryForm dataEntryForm, List<String> errorList, String mode)
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