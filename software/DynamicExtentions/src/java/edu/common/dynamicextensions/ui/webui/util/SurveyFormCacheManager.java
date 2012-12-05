
package edu.common.dynamicextensions.ui.webui.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;

import edu.common.dynamicextensions.domain.Category;
import edu.common.dynamicextensions.domain.userinterface.Page;
import edu.common.dynamicextensions.domain.userinterface.SurveyModeLayout;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.LoadDataEntryFormProcessor;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.cab2b.server.util.DynamicExtensionUtility;
import edu.wustl.metadata.util.DyExtnObjectCloner;

/**
 * @author Kunal Kamble
 * This class provides methods for accessing metdata related to a survey form.
 */
public class SurveyFormCacheManager extends FormCache
{

	private int displayPage = -1;
	private CategoryInterface category;
	private String recordIdentifier;

	public SurveyFormCacheManager(HttpServletRequest request)
	{
		super(request);
		category = getCategory();
		recordIdentifier = request.getParameter(DEConstants.RECORD_IDENTIFIER);			
	}

	public SurveyFormCacheManager(HttpServletRequest request, Long containerIdentifier,
			String recordIdentifier) throws DynamicExtensionsCacheException
	{
		super(request);
		CacheManager.clearCache(request);
		this.recordIdentifier = recordIdentifier;
		category = getClonedObj(DynamicExtensionUtility.getCategoryByContainerId(containerIdentifier.toString()));
		request.getSession().setAttribute(DEConstants.SURVEY_CATEGORY, category);
	}

	public int controlsCount() throws DynamicExtensionsSystemException
	{
		int count = 0;
		Collection<Page> pageCollection = getPageCollection();
		for (Page page : pageCollection)
		{
			for (ControlInterface control : page.getControlCollection())
			{
				if (!control.getIsHidden() &&  !control.getIsReadOnly())
				{
					count++;
					
				}
			}
		}
		return count;
	}

	public int emptyControlsCount() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		int count = 0;
		int pageCount = 0;
		Collection<Page> pageCollection = getPageCollection();
		ContainerInterface container = getContainerWithValueMap();
		for (Page page : pageCollection)
		{
			for (ControlInterface control : page.getControlCollection())
			{
				setControlValue(container, control);
				if (control.isEmpty() && !control.getIsHidden() && !control.getIsReadOnly())
				{
					count++;
					if (this.displayPage == -1)
					{
						displayPage = pageCount;
					}
				}
			}
			pageCount++;
		}
		return count;
	}

	public Collection<Page> getPageCollection() throws DynamicExtensionsSystemException
	{
		Collection<Page> pageCollection = (Collection<Page>) CacheManager.getObjectFromCache(request, DEConstants.PAGE_COLLECTION);
		if (pageCollection == null) {
			CategoryInterface category = getCategory();
			SurveyModeLayout layout = (SurveyModeLayout) category.getLayout();
			List<Page> sortedPages = new ArrayList(layout.getPageCollection());
			Collections.sort(sortedPages);
			CacheManager.addObjectToCache(request, DEConstants.PAGE_COLLECTION, sortedPages);
			pageCollection = sortedPages;
		}
		return pageCollection;
	}

	public Page getPage(String pageId) throws NumberFormatException,
			DynamicExtensionsSystemException, IOException
	{
		Long longPageId = Long.valueOf(pageId);
		Collection<Page> pageCollection = getPageCollection();
		for (Page page : pageCollection)
		{
			if (page.getId().longValue() == longPageId.longValue())
			{
				return page;
			}
		}
		return null;
	}

	public ContainerInterface getContainer() throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		ContainerInterface container = (ContainerInterface) CacheManager.getObjectFromCache(
				request, DEConstants.CONTAINER);
		if (container == null)
		{
			container = (ContainerInterface) getCategory().getRootCategoryElement()
					.getContainerCollection().iterator().next();
			CacheManager.addObjectToCache(request, DEConstants.CONTAINER, container);
		}
		return container;
	}

	public ContainerInterface getContainerFromCategory() throws NumberFormatException,
			DynamicExtensionsSystemException
	{
		ContainerInterface container = (ContainerInterface) CacheManager.getObjectFromCache(
				request, DEConstants.CONTAINER);
		if (container == null)
		{
			CategoryInterface category = getCategory();
			container = (ContainerInterface) category.getRootCategoryElement()
					.getContainerCollection().iterator().next();
			CacheManager.addObjectToCache(request, DEConstants.CONTAINER, container);
		}
		return container;
	}

	public ContainerInterface getContainerWithValueMap() throws NumberFormatException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		ContainerInterface container = getContainer();
		if (recordIdentifier != null && !recordIdentifier.trim().isEmpty() && !recordIdentifier.equalsIgnoreCase("null"))
		{
			Map<BaseAbstractAttributeInterface, Object> containerValueMap = getValueMap();
			UserInterfaceiUtility.setContainerValueMap(container, containerValueMap);
			
		}
		ContainerUtility.evaluateSkipLogic(container);
		container.setMode("insertParentData");
		container.setMode("edit");
		return container;
	}

	public CategoryInterface getCategory()
	{
		try {
			if (category == null) {
				category = (Category) request.getSession().getAttribute(DEConstants.SURVEY_CATEGORY);
				if (category == null) {
					category = (Category) EntityCache.getInstance().getCategoryById(Long.parseLong(getCategoryId()));
					category  = getClonedObj(category);
					request.getSession().setAttribute(DEConstants.SURVEY_CATEGORY, category);
				}
			}
			return category;			
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining category", e);
		}
	}
	
	private Map<BaseAbstractAttributeInterface, Object> getValueMap() 
	throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException {
		Stack<Map<BaseAbstractAttributeInterface, Object>> valueMapStack = (Stack<Map<BaseAbstractAttributeInterface, Object>>) CacheManager
				.getObjectFromCache(request, DEConstants.VALUE_MAP_STACK);		
		Map<BaseAbstractAttributeInterface, Object> containerValueMap = null;
		
		if (valueMapStack != null) {
			containerValueMap = valueMapStack.peek();
		}
		
		if (containerValueMap == null) {
			containerValueMap = (Map<BaseAbstractAttributeInterface, Object>)CacheManager.getObjectFromCache(request, DEConstants.RECORD_MAP); 
		}
			
		if (containerValueMap == null) {
			ContainerInterface container = getContainer();
			containerValueMap = LoadDataEntryFormProcessor.getValueMapFromRecordId(container.getAbstractEntity(), recordIdentifier);
			CacheManager.addObjectToCache(request, DEConstants.RECORD_MAP, containerValueMap);
		}		
		
		return containerValueMap;
	}
	
	private void setControlValue(ContainerInterface container, ControlInterface control)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		Object value = container.getContainerValueMap().get(control.getBaseAbstractAttribute());
		if (value == null)
		{
			for (ContainerInterface ctr : container.getChildContainerCollection())
			{
				for (ControlInterface c : ctr.getControlCollection())
				{
					if (c.getId().longValue() == control.getId().longValue())
					{
						value = ctr.getContainerValueMap().get(control.getBaseAbstractAttribute());
						break;
					}
				}
			}
		}
		control.setValue(value);
	}

	public String getCategoryId()
	{
		return request.getParameter(DEConstants.CATEGORY_ID);
	}

	public String getPageId()
	{
		return request.getParameter(DEConstants.PAGE_ID);
	}

	public int getDisplayPage()
	{
		return displayPage;
	}

	public void setDisplayPage(int displayPage)
	{
		this.displayPage = displayPage;
	}

	public int getCompletionStatus()
			throws DynamicExtensionsCacheException, DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		ContainerUtility.evaluateSkipLogic(getContainerWithValueMap());
		int controlsCount = controlsCount();
		int percentage = Math.round(100 * (controlsCount - emptyControlsCount())
				/ controlsCount);
		return percentage;
	}
	
	public <T> T getClonedObj(T obj) {
		DyExtnObjectCloner cloner = new DyExtnObjectCloner();
		T retObj = cloner.clone(obj);
		return retObj;
	}
}
