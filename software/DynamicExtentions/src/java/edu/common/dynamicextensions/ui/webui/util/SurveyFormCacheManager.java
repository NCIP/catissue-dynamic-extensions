
package edu.common.dynamicextensions.ui.webui.util;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import edu.common.dynamicextensions.domain.Category;
import edu.common.dynamicextensions.domain.userinterface.Page;
import edu.common.dynamicextensions.domain.userinterface.SurveyModeLayout;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.LoadDataEntryFormProcessor;
import edu.common.dynamicextensions.skiplogic.SkipLogic;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.metadata.util.DyExtnObjectCloner;

/**
 * @author Kunal Kamble
 * This class provides methods for accessing metdata related to a survey form.
 */
public class SurveyFormCacheManager extends FormCache
{

	private int displayPage = -1;

	public SurveyFormCacheManager(HttpServletRequest request)
	{
		super(request);
	}

	public int controlsCount() throws NumberFormatException, DynamicExtensionsSystemException
	{
		int count = 0;
		Collection<Page> pageCollection = getPageCollection();
		for (Page page : pageCollection)
		{
			count += page.getControlCollection().size();
		}
		return count;
	}

	public int emptyControlsCount() throws NumberFormatException, DynamicExtensionsSystemException,
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
				if (control.isEmpty())
				{
					count++;
					if (this.displayPage == -1 && !control.getIsHidden())
					{
						displayPage = pageCount;
					}
				}
			}
			pageCount++;
		}
		return count;
	}

	public Collection<Page> getPageCollection() throws NumberFormatException,
			DynamicExtensionsSystemException
	{
		Category category = getCategory();
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

	public ContainerInterface getContainer() throws DynamicExtensionsSystemException
	{
		ContainerInterface container = (ContainerInterface) getCached(DEConstants.CONTAINER);
		if (container == null)
		{
			container = (ContainerInterface) getCategory().getRootCategoryElement()
					.getContainerCollection().iterator().next();
			cache(DEConstants.CONTAINER, container);
		}
		return container;
	}

	public ContainerInterface getContainerFromCategory() throws NumberFormatException,
			DynamicExtensionsSystemException
	{
		ContainerInterface container = (ContainerInterface) getCached(DEConstants.CONTAINER);
		if (container == null)
		{
			Category category = getCategory();
			container = (ContainerInterface) category.getRootCategoryElement()
					.getContainerCollection().iterator().next();
			cache(DEConstants.CONTAINER, container);
		}
		return container;
	}

	public ContainerInterface getContainerWithValueMap() throws NumberFormatException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		ContainerInterface container = getContainer();
		String recordIdentifier = getRecordIdentifier();
		if (recordIdentifier != null && !recordIdentifier.equalsIgnoreCase("null"))
		{
			UserInterfaceiUtility.setContainerValueMap(container, LoadDataEntryFormProcessor
					.getValueMapFromRecordId(container.getAbstractEntity(), recordIdentifier));
		}
		container.setMode("insertParentData");
		container.setMode("edit");
		return container;
	}

	private String getRecordIdentifier()
	{
		return request.getParameter(DEConstants.RECORD_IDENTIFIER);
	}

	public Category getCategory() throws DynamicExtensionsSystemException
	{
		Category category = (Category) getCached(DEConstants.CATEGORY);
		if (category == null)
		{
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			category = (Category) categoryManager.getCategoryById(Long.valueOf(getCategoryId()));
			if (category.getIsCacheable())
			{
				DyExtnObjectCloner cloner = new DyExtnObjectCloner();
				category = cloner.clone(category);
			}
			cache(DEConstants.CATEGORY, category);
		}
		return category;
	}

	private Object getCached(String key)
	{
		return request.getSession().getAttribute(key);
	}

	private void cache(String key, Object object)
	{
		request.getSession().setAttribute(key, object);
	}

	private void setControlValue(ContainerInterface container, ControlInterface control)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		SkipLogic skipLogic = EntityCache.getInstance().getSkipLogicByContainerIdentifier(
				container.getId());
		if (skipLogic != null)
		{
			skipLogic.evaluateSkipLogic(container, container.getContainerValueMap());
		}
		Object value = container.getContainerValueMap().get(control.getBaseAbstractAttribute());
		if (value == null)
		{
			for (ContainerInterface ctr : container.getChildContainerCollection())
			{
				skipLogic = EntityCache.getInstance()
						.getSkipLogicByContainerIdentifier(ctr.getId());
				if (skipLogic != null)
				{
					skipLogic.evaluateSkipLogic(ctr, ctr.getContainerValueMap());
				}
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

}
