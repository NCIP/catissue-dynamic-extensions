
package edu.common.dynamicextensions.ui.webui.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import edu.common.dynamicextensions.domain.userinterface.AbstractContainmentControl;
import edu.common.dynamicextensions.domaininterface.AssociationMetadataInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.LoadDataEntryFormProcessor;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.webui.actionform.DataEntryForm;
import edu.common.dynamicextensions.ui.webui.taglib.TagUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;

/**
 * This class is used for maintaining state of the form between the navigations
 * @author Kunal
 *
 */
public class FormCache
{

	protected HttpServletRequest request;
	private LoadDataEntryFormProcessor loadDataEntryFormProcessor;
	private DataEntryForm dataEntryForm;

	public FormCache(HttpServletRequest request)
	{
		this.request = request;
		if(request.getAttribute(Constants.DATA_ENTRY_OPERATION) == null)
		{
			dataEntryForm = new DataEntryForm();
		}else
		{
			dataEntryForm = (DataEntryForm) request.getAttribute(Constants.DATA_ENTRY_FORM);	
		}
		
		this.loadDataEntryFormProcessor = LoadDataEntryFormProcessor.getInstance();;
	}

	private Stack<ContainerInterface> containerStack;
	private Stack<Map<BaseAbstractAttributeInterface, Object>> valueMapStack;
	private Stack<Long> scrollTopStack;
	private Map<BaseAbstractAttributeInterface, Object> recordMap;
	private String mode;
	private ContainerInterface container;
	private String recordId;

	public Stack<ContainerInterface> getContainerStack()
	{
		return containerStack;
	}

	public void setContainerStack(Stack<ContainerInterface> containerStack)
	{
		this.containerStack = containerStack;
	}

	public Stack<Map<BaseAbstractAttributeInterface, Object>> getValueMapStack()
	{
		return valueMapStack;
	}

	public void setValueMapStack(Stack<Map<BaseAbstractAttributeInterface, Object>> valueMapStack)
	{
		this.valueMapStack = valueMapStack;
	}

	public Stack<Long> getScrollTopStack()
	{
		return scrollTopStack;
	}

	public void setScrollTopStack(Stack<Long> scrollTopStack)
	{
		this.scrollTopStack = scrollTopStack;
	}

	public Map<BaseAbstractAttributeInterface, Object> getRecordMap()
	{
		return recordMap;
	}

	public void setRecordMap(Map<BaseAbstractAttributeInterface, Object> recordMap)
	{
		this.recordMap = recordMap;
	}

	public String getMode()
	{
		return mode;
	}

	public void setMode(String mode)
	{
		this.mode = mode;
	}

	@SuppressWarnings("unchecked")
	public void onFormLoad() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		initContainer(request);
		initFormRecordId();
		updateSessionAttribute();
		cacheCallBackURL(request);

		containerStack = (Stack<ContainerInterface>) CacheManager.getObjectFromCache(request,
				DEConstants.CONTAINER_STACK);

		valueMapStack = (Stack<Map<BaseAbstractAttributeInterface, Object>>) CacheManager
				.getObjectFromCache(request, DEConstants.VALUE_MAP_STACK);

		scrollTopStack = (Stack<Long>) CacheManager.getObjectFromCache(request,
				DEConstants.SCROLL_TOP_STACK);

		if (containerStack == null || containerStack.isEmpty())
		{
			recordMap = LoadDataEntryFormProcessor.getValueMapFromRecordId(container
					.getAbstractEntity(), recordId);
			initStack();
		}else if(request.getAttribute(DEConstants.ERRORS_LIST) == null || ((HashSet)request.getAttribute(DEConstants.ERRORS_LIST)).isEmpty())
		{
			updateStacks(request, dataEntryForm, containerStack, valueMapStack, scrollTopStack);			
		}


		
		loadDataEntryFormProcessor.updateFormBean(dataEntryForm,
				containerStack.peek(), valueMapStack.peek(), recordId);
		
		if (dataEntryForm.getErrorList().isEmpty())
		{
			clearFormValues(dataEntryForm);
		}
		
		UserInterfaceiUtility.setContainerValueMap(containerStack.peek(), valueMapStack.peek());
		updateContainerMap(request, getContainer());
		updateApplicationErrorMsgs(request, dataEntryForm);
	}

	private void initStack()
	{
		containerStack = new Stack<ContainerInterface>();
		CacheManager.addObjectToCache(request, DEConstants.CONTAINER_STACK, containerStack);
		valueMapStack = new Stack<Map<BaseAbstractAttributeInterface, Object>>();
		CacheManager.addObjectToCache(request, DEConstants.VALUE_MAP_STACK, valueMapStack);
		scrollTopStack = new Stack<Long>();
		CacheManager.addObjectToCache(request, DEConstants.SCROLL_TOP_STACK, scrollTopStack);
		updateScrollTop(request, scrollTopStack);
		UserInterfaceiUtility.addContainerInfo(containerStack, container, valueMapStack, recordMap);
	}

	private void initFormRecordId()
	{
		recordId = request.getParameter(DEConstants.RECORD_IDENTIFIER);
		if (recordId == null)
		{
			recordId = "";
		}
	}

	private void updateSessionAttribute()
	{
		String onFormLoadMethodCall = request.getParameter(DEConstants.ON_FORM_LOAD);
		if (onFormLoadMethodCall != null)
		{
			request.getSession().setAttribute(DEConstants.ON_FORM_LOAD, onFormLoadMethodCall);
		}
	}

	/**
	 *
	 * @param request
	 */
	private void cacheCallBackURL(final HttpServletRequest request)
	{
		final String callBackURL = request
				.getParameter(WebUIManagerConstants.CALLBACK_URL_PARAM_NAME);
		if (callBackURL != null && !callBackURL.equals(""))
		{
			final String userId = request.getParameter(WebUIManagerConstants.USER_ID);
			CacheManager.clearCache(request);
			CacheManager.addObjectToCache(request, DEConstants.CALLBACK_URL, callBackURL);
			CacheManager.addObjectToCache(request, WebUIManagerConstants.USER_ID, userId);
		}
	}
	
	/**
	* @param request
	* @return
	* @throws DynamicExtensionsSystemException
	* @throws DynamicExtensionsApplicationException
	*/
	private void initContainer(final HttpServletRequest request)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		container = (ContainerInterface) CacheManager
				.getObjectFromCache(request, DEConstants.CONTAINER_INTERFACE);
		final String containerIdentifier = getContainerId(request);
		if (request.getParameter(WebUIManagerConstants.CALLBACK_URL_PARAM_NAME) != null)
		{
			CacheManager.clearCache(request);

			final Long containerId = Long.valueOf(containerIdentifier);

			if (containerId == -1)
			{
				container = (ContainerInterface) ((CategoryEntityInterface) request
						.getSession().getAttribute("categoryEntity")).getContainerCollection()
						.iterator().next();
			}
			else
			{
				container = DynamicExtensionsUtility
						.getClonedContainerFromCache(containerId.toString());
			}
			container.getContainerValueMap().clear();
			DynamicExtensionsUtility.cleanContainerControlsValue(container);

			CacheManager.addObjectToCache(request, DEConstants.CONTAINER_INTERFACE,
					container);
		}
		//Used by live validations for faster container search
		updateConatinerMap(container,request);

	}


	private void updateConatinerMap(ContainerInterface container, HttpServletRequest request)
	{
		//On error condition on the form the container is null
		if(container != null)
		{
			Map<Long, ContainerInterface> containerMap = new HashMap<Long, ContainerInterface>();
			TagUtility.setValidationMap(containerMap, container);
			request.getSession().setAttribute(Constants.MAP_FOR_VALIDATION, containerMap);
		}
	}

	/**
	 * This method returns the Container Identifier form the given request.
	 * @param request HttpServletRequest
	 * @return the Container Identifier
	 */
	private String getContainerId(final HttpServletRequest request)
	{
		String identifier = "";
		identifier = request.getParameter(Constants.CONTAINER_IDENTIFIER);
		if (identifier == null || identifier.equals("") || "null".equalsIgnoreCase(identifier))
		{
			identifier = (String) request.getAttribute(Constants.CONTAINER_IDENTIFIER);
		}
		return identifier;
	}

	/**
	* This method update scroll top value
	* @param request HttpServletRequest
	* @param scrollTopStack scroll stack
	*/
	private void updateScrollTop(final HttpServletRequest request, Stack<Long> scrollTopStack)
	{
		Long scrollTop = 0L;
		if (request.getParameter(DEConstants.SCROLL_TOP) != null
				&& !request.getParameter(DEConstants.SCROLL_TOP).equals(""))
		{
			scrollTop = Long.valueOf(request.getParameter(DEConstants.SCROLL_TOP));
		}
		scrollTopStack.push(scrollTop);
	}
	
	/**
	 *
	 * @param form
	 * @param containerStack
	 * @param valueMapStack
	 */
	@SuppressWarnings("unchecked")
	private void updateStacks(final HttpServletRequest request, final DataEntryForm dataEntryForm,
			final Stack<ContainerInterface> containerStack,
			final Stack<Map<BaseAbstractAttributeInterface, Object>> valueMapStack,
			final Stack<Long> scrollTopStack)
	{
		Long scrollPos = 0L;
		final String childContainerId = request.getParameter(Constants.CHILD_CONTAINER_ID);
		if (dataEntryForm.getErrorList().isEmpty() && !StringUtils.isEmpty(childContainerId))
       {
           request.setAttribute(DEConstants.SCROLL_POSITION, scrollPos);
           updateScrollTop(request, scrollTopStack);
           
           final AbstractContainmentControlInterface associationControl = UserInterfaceiUtility
                   .getAssociationControl(containerStack.peek(),
                           childContainerId);

           final Map<BaseAbstractAttributeInterface, Object> containerValueMap = valueMapStack
                   .peek();
           final AssociationMetadataInterface association = (AssociationMetadataInterface) associationControl
                   .getBaseAbstractAttribute();
           final List<Map<BaseAbstractAttributeInterface, Object>> childContainerValueMapList = (List<Map<BaseAbstractAttributeInterface, Object>>) containerValueMap
                   .get(association);

           Map<BaseAbstractAttributeInterface, Object> childContainerValueMap;
           if (associationControl.isCardinalityOneToMany())
           {
               childContainerValueMap = childContainerValueMapList.get(Integer
                       .parseInt(dataEntryForm.getChildRowId()) - 1);
           } else
           {
               childContainerValueMap = childContainerValueMapList.get(0);
           }

           final ContainerInterface childContainer = associationControl
                   .getContainer();
           UserInterfaceiUtility.addContainerInfo(containerStack,
                   childContainer, valueMapStack, childContainerValueMap);
       }
		
	}

	/**
	* @param scrollTopStack
	* @param errorList
	*/
	public static void removeScrollInfo(Stack<Long> scrollTopStack, final List<String> errorList)
	{
		if (errorList != null && errorList.isEmpty() && scrollTopStack != null
				&& !scrollTopStack.isEmpty())
		{
			scrollTopStack.pop();
		}
	}
	
	
	
	/**
	 * This method flushes the values of the DataEntryForm ActionForm.
	 * @param form DataEntryForm ActionForm
	 */
	private void clearFormValues(final DataEntryForm dataEntryForm)
	{
		dataEntryForm.setChildRowId("");
		dataEntryForm.setChildContainerId("");
	}

	/**
	 * @param request
	 * @param dataEntryForm
	 */
	private void updateApplicationErrorMsgs(HttpServletRequest request, DataEntryForm dataEntryForm)
	{
		if (request.getParameter(DEConstants.APPLICATION_ERROR_MSGS) != null
				&& !"".equals(request.getParameter(DEConstants.APPLICATION_ERROR_MSGS)))
		{
			List<String> applicationErrorMsgs = new ArrayList<String>();
			applicationErrorMsgs.add(request.getParameter(DEConstants.APPLICATION_ERROR_MSGS));
			dataEntryForm.setErrorList(applicationErrorMsgs);
		}
	}

	/**
	 * removes old container map and add the new one to the session
	 * @param request
	 * @param containerInterface
	 */
	private void updateContainerMap(final HttpServletRequest request,
			final ContainerInterface containerInterface)
	{
		request.getSession().removeAttribute(WebUIManagerConstants.CONTAINER_MAP);

		Map<String, ContainerInterface> containerMap = new HashMap<String, ContainerInterface>();
		addContainersToMap(containerMap, containerInterface);

		request.getSession().setAttribute(WebUIManagerConstants.CONTAINER_MAP, containerMap);
	}
	
	/**
	 * Populate the map with the all the containers
	 * @param containerMap
	 * @param mainContainer
	 */
	private void addContainersToMap(Map<String, ContainerInterface> containerMap,
			ContainerInterface mainContainer)
	{
		for (ControlInterface controlInterface : mainContainer.getAllControls())
		{
			if (controlInterface instanceof AbstractContainmentControl)
			{
				ContainerInterface container = ((AbstractContainmentControl) controlInterface)
						.getContainer();
				addContainersToMap(containerMap, container);
				containerMap.put(container.getId() == null ? null : container.getId().toString(),
						container);
			}
		}
		for (ContainerInterface childContainer : mainContainer.getChildContainerCollection())
		{
			addContainersToMap(containerMap, childContainer);
			containerMap.put(childContainer.getId().toString(), childContainer);
		}
	}

	public ContainerInterface getContainer() throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		return containerStack.peek();
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isMainForm(HttpServletRequest request)
	{
		if( ((Stack)CacheManager.getObjectFromCache(request,
				DEConstants.CONTAINER_STACK)).size() == 1)
		{
			return true;
		}
		return false;
				
	}
	
	/**
	 * @param request
	 * @return top of the container stack
	 */
	public static ContainerInterface getTopContainer(HttpServletRequest request)
	{
		return ((Stack<ContainerInterface>) CacheManager.getObjectFromCache(request,
				DEConstants.CONTAINER_STACK)).peek();
	}
	
	/**
	 * @param request
	 * @return top of the data value map stack
	 */
	public static Map<BaseAbstractAttributeInterface, Object> getTopDataValueMap(HttpServletRequest request)
	{
		return ((Stack<Map<BaseAbstractAttributeInterface, Object>>) CacheManager
				.getObjectFromCache(request, DEConstants.VALUE_MAP_STACK)).peek();
	}

}


