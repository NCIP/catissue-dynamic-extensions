package edu.common.dynamicextensions.ui.renderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import edu.common.dynamicextensions.domain.Category;
import edu.common.dynamicextensions.domain.userinterface.Page;
import edu.common.dynamicextensions.domain.userinterface.SurveyModeLayout;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.LoadDataEntryFormProcessor;
import edu.common.dynamicextensions.ui.webui.util.UserInterfaceiUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;

public class SurveyModeRenderer extends LayoutRenderer {

	public SurveyModeRenderer(HttpServletRequest req) {
		this.req = req;
	}

	private String getContainerIdentifier() {
		return req.getParameter(DEConstants.CONTAINER_IDENTIFIER);
	}

	private String getRecordIdentifier() {
		return req.getParameter(DEConstants.RECORD_IDENTIFIER);
	}

	private String getCategoryId() {
		return req.getParameter(DEConstants.CATEGORY_ID);
	}

	private String getPageId() {
		return req.getParameter(DEConstants.PAGE_ID);
	}

	private Object getCached(String key) {
		return req.getSession().getAttribute(key);
	}

	private void cache(String key, Object object) {
		req.getSession().setAttribute(key, object);
	}

	private ContainerInterface getContainer() throws NumberFormatException,
			DynamicExtensionsSystemException, DynamicExtensionsCacheException {
		ContainerInterface container = (ContainerInterface) getCached(DEConstants.CONTAINER);
		if (container == null) {
			container = DynamicExtensionsUtility
					.getClonedContainerFromCache(getContainerIdentifier());
			cache(DEConstants.CONTAINER, container);
		}
		return container;
	}

	private ContainerInterface getContainerFromCategory()
			throws NumberFormatException, DynamicExtensionsSystemException {
		ContainerInterface container = (ContainerInterface) getCached(DEConstants.CONTAINER);
		if (container == null) {
			Category category = getCategory();
			container = (ContainerInterface) category.getRootCategoryElement()
					.getContainerCollection().iterator().next();
			cache(DEConstants.CONTAINER, container);
		}
		return container;
	}

	private ContainerInterface getContainerWithValueMap()
			throws NumberFormatException, DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException {
		ContainerInterface container = getContainer();
		String recordIdentifier = getRecordIdentifier();
		if (recordIdentifier != null
				&& !recordIdentifier.equalsIgnoreCase("null")) {
			UserInterfaceiUtility.setContainerValueMap(container,
					LoadDataEntryFormProcessor.getValueMapFromRecordId(
							container.getAbstractEntity(), recordIdentifier));
		}
		container.setMode("insertParentData");
		container.setMode("edit");
		return container;
	}

	private int controlsCount() throws NumberFormatException,
			DynamicExtensionsSystemException {
		int count = 0;
		Collection<Page> pageCollection = getPageCollection();
		for (Page page : pageCollection) {
			count += page.getControlCollection().size();
		}
		return count;
	}

	private int emptyControlsCount() throws NumberFormatException,
			DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException {
		int count = 0;
		ContainerInterface container = getContainerWithValueMap();
		Collection<Page> pageCollection = getPageCollection();
		for (Page page : pageCollection) {
			for (ControlInterface control : page.getControlCollection()) {
				setControlValue(container, control);
				if (control.isEmpty()) {
					count++;
				} 
			}
		}
		return count;
	}

	private Category getCategory() throws NumberFormatException,
			DynamicExtensionsSystemException {
		Category category = (Category) getCached(DEConstants.CATEGORY);
		if (category == null) {
			CategoryManagerInterface categoryManager = CategoryManager
					.getInstance();
			category = (Category) categoryManager.getCategoryById(Long
					.valueOf(getCategoryId()));
			cache(DEConstants.CATEGORY, category);
		}
		return category;
	}

	private Collection<Page> getPageCollection() throws NumberFormatException,
			DynamicExtensionsSystemException {
		Category category = getCategory();
		SurveyModeLayout layout = (SurveyModeLayout) category.getLayout();
		return layout.getPageCollection();
	}

	private Page getPage(String pageId) throws NumberFormatException,
			DynamicExtensionsSystemException, IOException {
		Long longPageId = Long.valueOf(pageId);
		Collection<Page> pageCollection = getPageCollection();
		for (Page page : pageCollection) {
			if (page.getId().longValue() == longPageId.longValue()) {
				return page;
			}
		}
		return null;
	}

	private String renderCategory(String categoryId) throws IOException,
			NumberFormatException, DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException {
		String categorydiv = "<div><div id='sm-pages'>%s</div></div>";
		String pagediv = "<div class='sm-page' id='%d' style='display:none'></div>";
		StringBuffer html = new StringBuffer();
		for (Page p : getPageCollection()) {
			html.append(String.format(pagediv, p.getId().longValue()));
		}
		return renderHiddenInputs() + String.format(categorydiv, html.toString());
	}

	private String renderHiddenInputs() throws NumberFormatException,
			DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException {
		String containerIdentifier = "<input type='hidden' id='containerIdentifier' name='containerIdentifier'  value='%d'></input>";
		String categoryName = "<input type='hidden' id='categoryName' value='%s'></input>";
		String controlsCount = "<input type='hidden' id='controlsCount' value='%d'></input>";
		String emptyControlsCount = "<input type='hidden' id='emptyControlsCount' value='%d'></input>";
		ContainerInterface container = getContainerFromCategory();
		Category category = getCategory();
		return String.format(containerIdentifier, container.getId())
				+ String.format(categoryName, category.getName())
				+ String.format(controlsCount, controlsCount())
				+ String.format(emptyControlsCount, emptyControlsCount());
	}

	private String renderPage(String pageId)
			throws DynamicExtensionsSystemException, IOException,
			NumberFormatException, DynamicExtensionsApplicationException {
		String pageHtml = "<div class='sm-page-contents'>%s</div>";
		String htmlWrapper = "<table class='sm-page-table'>%s%s</table>";
		String pageTitle = "<tr><th><div class='sm-page-title'>&nbsp;<div></th><th colspan='10'><div class='sm-page-title'>%s</div></th></tr>";
		Page p = getPage(pageId);
				
		if (p == null) {
			return renderError("page not found!");
		} else {
			StringBuffer html = new StringBuffer();
			pageTitle = String.format(pageTitle,p.getDescription());
			List<ControlInterface> controlList = new ArrayList<ControlInterface>(
					p.getControlCollection());
			Collections.sort(controlList);
			Collections.reverse(controlList);
			for (ControlInterface control : controlList) {
				control.getParentContainer().getContextParameter().put(DEConstants.CONTEXT_PATH, req.getContextPath());
				if (control.getValue() != null) {
					control.setDataEntryOperation("insertParentData");
				}
				html
						.append(getControlHTML(control));
			}
			
			htmlWrapper = String.format(htmlWrapper, pageTitle,html.toString());
			return String.format(pageHtml,htmlWrapper);
		}
	}

	private void setControlValue(ContainerInterface container,
			ControlInterface control) {
		Object value = container.getContainerValueMap().get(
				control.getBaseAbstractAttribute());
		if (value == null)
		{
			for (ContainerInterface ctr : container.getChildContainerCollection())
			{
				for (ControlInterface c : ctr.getControlCollection())
				{
					if (c.getId().longValue() == control.getId().longValue())
					{
						value = ctr.getContainerValueMap().get(control.getBaseAbstractAttribute());
					}
				}
			}
		}
		control.setValue(value);
	}

	protected String getControlHTML(ControlInterface control)
			throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException {
		String htmlWrapper = "<tr>%s</table></td></tr>";
		return String.format(htmlWrapper, control.generateHTML(control
				.getParentContainer()));
	}

	private String renderError(String message){
		return message;
	}

	public String render() throws DynamicExtensionsSystemException, IOException,
			NumberFormatException, DynamicExtensionsApplicationException {
		String categoryId = getCategoryId();
		String responseString;
		String pageId = getPageId();
		if (categoryId != null) {
			if (pageId != null) {
				responseString = renderPage(pageId);
			} else {
				responseString = renderCategory(categoryId);
			}
		} else {
			responseString =renderError("categoryId cannot be null!");
		}
		return responseString;
	}
}
