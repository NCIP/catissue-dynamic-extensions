
package edu.common.dynamicextensions.ui.webui.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.ui.webui.actionform.DataEntryForm;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.ui.webui.util.UserInterfaceiUtility;
import edu.common.dynamicextensions.util.global.DEConstants;

/**
 * This Action Class is responsible for displaying the Preview Forms of the Dynamic UI.
 * @author sujay_narkar, chetan_patil
 */
public class LoadFormPreviewAction extends BaseDynamicExtensionsAction
{

	/* (non-Javadoc)
	 * @see org.apache.struts.actions.DispatchAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		ContainerInterface containerInterface = (ContainerInterface) CacheManager
				.getObjectFromCache(request, DEConstants.CONTAINER_INTERFACE);

		Stack<ContainerInterface> containerStack = (Stack<ContainerInterface>) CacheManager
				.getObjectFromCache(request, DEConstants.CONTAINER_STACK);
		Stack<Map<BaseAbstractAttributeInterface, Object>> valueMapStack = (Stack<Map<BaseAbstractAttributeInterface, Object>>) CacheManager
				.getObjectFromCache(request, DEConstants.VALUE_MAP_STACK);

		DataEntryForm dataEntryForm = (DataEntryForm) form;

		String dataEntryOperation = dataEntryForm.getDataEntryOperation();

		if (containerStack == null)
		{
			containerStack = new Stack<ContainerInterface>();
			CacheManager.addObjectToCache(request, DEConstants.CONTAINER_STACK, containerStack);

			valueMapStack = new Stack<Map<BaseAbstractAttributeInterface, Object>>();
			CacheManager.addObjectToCache(request, DEConstants.VALUE_MAP_STACK, valueMapStack);

			Map<BaseAbstractAttributeInterface, Object> recordMap = new HashMap<BaseAbstractAttributeInterface, Object>();
			UserInterfaceiUtility.addContainerInfo(containerStack, containerInterface,
					valueMapStack, recordMap);
			dataEntryForm.setContainerInterface(containerInterface);
		}
		else if (dataEntryOperation != null
				&& dataEntryOperation.equalsIgnoreCase("insertChildData"))
		{
			String childContainerId = dataEntryForm.getChildContainerId();
			AbstractContainmentControlInterface associationControl = UserInterfaceiUtility
					.getAssociationControl(containerStack.peek(), childContainerId);
			ContainerInterface childContainer = associationControl.getContainer();

			Map<BaseAbstractAttributeInterface, Object> childContainerValueMap = new HashMap<BaseAbstractAttributeInterface, Object>();
			UserInterfaceiUtility.addContainerInfo(containerStack, childContainer, valueMapStack,
					childContainerValueMap);
			dataEntryForm.setContainerInterface(childContainer);

		}
		else if (dataEntryOperation != null
				&& dataEntryOperation.equalsIgnoreCase("insertParentData"))
		{
			UserInterfaceiUtility.removeContainerInfo(containerStack, valueMapStack);
			if (!containerStack.isEmpty())
			{
				dataEntryForm.setContainerInterface(containerStack.peek());
			}
		}

		ActionForward forwardTo;
		if (containerStack.isEmpty())
		{
			CacheManager.addObjectToCache(request, DEConstants.CONTAINER_STACK, null);
			CacheManager.addObjectToCache(request, DEConstants.VALUE_MAP_STACK, null);
			forwardTo = mapping.findForward("LoadFormControls");
		}
		else
		{
			forwardTo = mapping.findForward(DEConstants.SUCCESS);
		}

		return forwardTo;
	}

}
