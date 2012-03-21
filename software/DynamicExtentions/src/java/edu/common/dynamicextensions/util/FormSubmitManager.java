
package edu.common.dynamicextensions.util;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ApplyDataEntryFormProcessor;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.wustl.common.beans.SessionDataBean;

public class FormSubmitManager
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
	public String storeParentContainer(
			Stack<Map<BaseAbstractAttributeInterface, Object>> valueMapStack,
			Stack<ContainerInterface> containerStack, HttpServletRequest request,
			String recordIdentifier, String isShowTemplateRecord) throws NumberFormatException,
			DynamicExtensionsApplicationException, DynamicExtensionsSystemException, SQLException,
			MalformedURLException
	{
		String identifier = recordIdentifier;
		Map<BaseAbstractAttributeInterface, Object> rootValueMap = valueMapStack.firstElement();
		ContainerInterface rootContainerInterface = containerStack.firstElement();
		DataValueMapUtility.updateDataValueMapForDataEntry(rootValueMap, rootContainerInterface);
		ApplyDataEntryFormProcessor applyDataEntryFormProcessor = ApplyDataEntryFormProcessor
				.getInstance();

		String userId = (String) CacheManager.getObjectFromCache(request,
				WebUIManagerConstants.USER_ID);
		if (userId != null)
		{
			applyDataEntryFormProcessor.setUserId(Long.parseLong(userId.trim()));
		}

		String messageKey = "app.successfulDataInsertionMessage";
		SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
				"sessionData");
		if (identifier != null
				&& !identifier.equals("")
				&& (isShowTemplateRecord == null || isShowTemplateRecord != null
						&& !isShowTemplateRecord.equals("true")))
		{
			Boolean edited = applyDataEntryFormProcessor.editDataEntryForm(rootContainerInterface,
					rootValueMap, Long.valueOf(identifier), sessionDataBean);
			if (edited.booleanValue())
			{
				saveMessages(request, getMessageString(messageKey));
			}
		}
		else
		{
			identifier = applyDataEntryFormProcessor.insertDataEntryForm(rootContainerInterface,
					rootValueMap, sessionDataBean);
			saveMessages(request, getMessageString(messageKey));
		}

		return identifier;
	}

	/**
	 * This method returns messages on successful saving of an Entity
	 * @return ActionMessages ActionMessages
	 */
	private ActionMessages getMessageString(String messageKey)
	{
		ActionMessages actionMessages = new ActionMessages();
		actionMessages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(messageKey));
		return actionMessages;
	}

	private void saveMessages(HttpServletRequest request, ActionMessages messageString)
	{
		// TODO Auto-generated method stub

	}
}
