
package edu.common.dynamicextensions.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;

import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ApplyDataEntryFormProcessor;
import edu.common.dynamicextensions.processor.ApplyDataEntryProcessorInterface;
import edu.common.dynamicextensions.processor.InContextApplyDataEntryProcessor;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.webui.actionform.DataEntryForm;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.ui.webui.util.FormDataCollectionUtility;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.global.Variables;
import edu.wustl.common.beans.SessionDataBean;

public class FormManager
{

	/**
	 * This method stores the container in the database. It updates the existing record or inserts a new record
	 * depending upon the availability of the record identifier variable.
	 * @param valueMapStack Stack storing the Map of Attributes and their corresponding values.
	 * @param containerStack Stack having Container at its top that is to be stored in database.
	 * @param request HttpServletRequest to store the operation message.
	 * @param recordIdentifier Identifier of the record in database that is to be updated.
	 * @return New identifier for a record if record is inserted otherwise the passed record identifier is returned.
	 * @throws NumberFormatException If record identifier is not a numeric value.
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 * @throws SQLException
	 * @throws MalformedURLException
	 */
	public String persistFormData(HttpServletRequest request) throws  DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		String recordIdentifier = request.getParameter(DEConstants.RECORD_IDENTIFIER);
		String isShowTemplateRecord = request.getParameter("isShowTemplateRecord");

		Stack<ContainerInterface> containerStack = (Stack<ContainerInterface>) CacheManager
				.getObjectFromCache(request, DEConstants.CONTAINER_STACK);
		Stack<Map<BaseAbstractAttributeInterface, Object>> valueMapStack = (Stack<Map<BaseAbstractAttributeInterface, Object>>) CacheManager
				.getObjectFromCache(request, DEConstants.VALUE_MAP_STACK);

		Map<BaseAbstractAttributeInterface, Object> rootValueMap = valueMapStack.firstElement();
		ContainerInterface rootContainerInterface = containerStack.firstElement();
		DataValueMapUtility.updateDataValueMapForDataEntry(rootValueMap, rootContainerInterface);
		ApplyDataEntryProcessorInterface applyDataEntryFormProcessor;
		if(Variables.jbossUrl.isEmpty())
		{
			
			applyDataEntryFormProcessor = ApplyDataEntryFormProcessor
			.getInstance();
		}
		
		applyDataEntryFormProcessor = new InContextApplyDataEntryProcessor();

		String userId = (String) CacheManager.getObjectFromCache(request,
				WebUIManagerConstants.USER_ID);
		if (userId != null)
		{
			applyDataEntryFormProcessor.setUserId(Long.parseLong(userId.trim()));
		}

		SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
				"sessionData");
		try{
			
		
		if (recordIdentifier != null
				&& !recordIdentifier.equals("")
				&& (isShowTemplateRecord == null || isShowTemplateRecord != null
						&& !isShowTemplateRecord.equals("true")))
		{
			Boolean edited = applyDataEntryFormProcessor.editDataEntryForm(rootContainerInterface,
					rootValueMap, Long.valueOf(recordIdentifier), sessionDataBean);

		}
		else
		{
			recordIdentifier = applyDataEntryFormProcessor.insertDataEntryForm(
					rootContainerInterface, rootValueMap, sessionDataBean);
		}
		}
		catch (NumberFormatException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(),e);
		}
		
		return recordIdentifier;
	}

	public void onBreadCrumbClick(HttpServletRequest request, String containerSize)
	{
		Stack<ContainerInterface> containerStack = (Stack<ContainerInterface>) CacheManager
				.getObjectFromCache(request, DEConstants.CONTAINER_STACK);
		Stack<Map<BaseAbstractAttributeInterface, Object>> valueMapStack = (Stack<Map<BaseAbstractAttributeInterface, Object>>) CacheManager
				.getObjectFromCache(request, DEConstants.VALUE_MAP_STACK);

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

	public void onDetailsLinkClicked(HttpServletRequest request) throws FileNotFoundException,
			DynamicExtensionsSystemException, IOException, DynamicExtensionsApplicationException
	{
		DataEntryForm dataEntryForm = DynamicExtensionsUtility.poulateDataEntryForm(request);
		updateRequestParameter(request, dataEntryForm);
		FormDataCollectionUtility collectionUtility = new FormDataCollectionUtility();
		collectionUtility.populateAndValidateValues(request);
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
	
	public boolean isDetailsLinkClicked(HttpServletRequest request)
	{
		return Constants.INSERT_CHILD_DATA.equals(request
				.getParameter(Constants.DATA_ENTRY_OPERATION));
	}
	
	public long submitMainFormData(HttpServletRequest request) throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		HashSet<String> errorList = null;
		DataEntryForm dataEntryForm = DynamicExtensionsUtility
				.poulateDataEntryForm(request);
		updateRequestParameter(request, dataEntryForm);

		FormDataCollectionUtility collectionUtility = new FormDataCollectionUtility();
		errorList = collectionUtility.populateAndValidateValues(request);
		
		String recordIdentifier = null;
		if (((errorList != null) && errorList.isEmpty()))
		{
			recordIdentifier = persistFormData(request);
			
		}else
		{
			request.setAttribute(DEConstants.ERRORS_LIST, errorList);
			throw new DynamicExtensionsApplicationException("Validation errors, please insert the values again");
		}
		return Long.valueOf(recordIdentifier);
	}

}
