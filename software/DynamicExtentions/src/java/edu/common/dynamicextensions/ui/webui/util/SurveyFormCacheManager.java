
package edu.common.dynamicextensions.ui.webui.util;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import edu.common.dynamicextensions.domain.Category;
import edu.common.dynamicextensions.domain.userinterface.Page;
import edu.common.dynamicextensions.domain.userinterface.SurveyModeLayout;
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
		category = (CategoryInterface) CacheManager.getObjectFromCache(request,
				DEConstants.CATEGORY);
		recordIdentifier = request.getParameter(DEConstants.RECORD_IDENTIFIER);
	}

	public SurveyFormCacheManager(HttpServletRequest request, Long containerIdentifier,
			String recordIdentifier) throws DynamicExtensionsCacheException
	{
		super(request);
		CacheManager.clearCache(request);
		this.recordIdentifier = recordIdentifier;
		category = DynamicExtensionUtility.getCategoryByContainerId(containerIdentifier.toString());

	}

	public int controlsCount() throws DynamicExtensionsSystemException
	{
		int count = 0;
		Collection<Page> pageCollection = getPageCollection();
		for (Page page : pageCollection)
		{
			for (ControlInterface control : page.getControlCollection())
			{
				if (!control.getIsHidden())
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
				if (control.isEmpty() && !control.getIsHidden())
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
		CategoryInterface category = getCategory();
		SurveyModeLayout layout = (SurveyModeLayout) category.getLayout();
		return layout.getPageCollection();
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
		if (recordIdentifier != null && !recordIdentifier.equalsIgnoreCase("null"))
		{
			UserInterfaceiUtility.setContainerValueMap(container, LoadDataEntryFormProcessor
					.getValueMapFromRecordId(container.getAbstractEntity(), recordIdentifier));
			ContainerUtility.evaluateSkipLogic(getContainer());
		}
		container.setMode("insertParentData");
		container.setMode("edit");
		return container;
	}

	public CategoryInterface getCategory() throws DynamicExtensionsSystemException
	{

		if (category == null)
		{
			category = (Category) EntityCache.getInstance().getCategoryById(Long.parseLong(getCategoryId()));
			if (category.getIsCacheable())
			{
				DyExtnObjectCloner cloner = new DyExtnObjectCloner();
				category = cloner.clone(category);
			}
			CacheManager.addObjectToCache(request, DEConstants.CATEGORY, category);
		}
		return category;
	}

	private void setControlValue(ContainerInterface container, ControlInterface control)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		/*SkipLogic skipLogic = EntityCache.getInstance().getSkipLogicByContainerIdentifier(
				container.getId());
		if (skipLogic != null)
		{
			skipLogic.evaluateSkipLogic(container, container.getContainerValueMap());
		}*/
		Object value = container.getContainerValueMap().get(control.getBaseAbstractAttribute());
		if (value == null)
		{
			for (ContainerInterface ctr : container.getChildContainerCollection())
			{
				/*skipLogic = EntityCache.getInstance()
						.getSkipLogicByContainerIdentifier(ctr.getId());
				if (skipLogic != null)
				{
					skipLogic.evaluateSkipLogic(ctr, ctr.getContainerValueMap());
				}*/
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
		ContainerUtility.evaluateSkipLogic(getContainer());
		int controlsCount = controlsCount();
		int percentage = Math.round(100 * (controlsCount - emptyControlsCount())
				/ controlsCount);
		return percentage;
	}
}
