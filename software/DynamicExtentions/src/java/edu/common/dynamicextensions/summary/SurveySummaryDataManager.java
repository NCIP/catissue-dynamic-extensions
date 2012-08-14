
package edu.common.dynamicextensions.summary;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import edu.common.dynamicextensions.domain.userinterface.Page;
import edu.common.dynamicextensions.domain.userinterface.SurveyModeLayout;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.global.Variables;
import edu.wustl.cab2b.server.util.DynamicExtensionUtility;

public class SurveySummaryDataManager extends AbstractSummaryDataManager
{

	private CategoryInterface category;
	private Long recordIdentifier;
	private Map<ControlInterface, Long> controlPageIdMap = new HashMap<ControlInterface, Long>();
	private PageContext pageContext;
	private Long containerIdentifier;

	public SurveySummaryDataManager(Long containerIdentifier, Long recordIdentifier, PageContext pageContext)
			throws DynamicExtensionsCacheException
	{
		this.recordIdentifier = recordIdentifier;
		this.containerIdentifier = containerIdentifier;
		this.pageContext = pageContext;
		category = DynamicExtensionUtility.getCategoryByContainerId(containerIdentifier.toString());
		for (Page page : ((SurveyModeLayout) category.getLayout()).getPageCollection())
		{
			for (ControlInterface control : page.getControlCollection())
			{
				controlPageIdMap.put(control, page.getId());
			}
		}
	}

	@Override
	protected void populateHeaderList()
	{
		headerList.add(SR_NO);
		headerList.add(QUESTION);
		headerList.add(RESPONSE);
		headerList.add(EDIT);

	}

	@Override
	protected void populateRow(ControlInterface control, Map<ControlInterface, Object> map,
			Map<String, String> data) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		data.put(QUESTION, control.getCaption());
		data.put(RESPONSE, getValueAsString(control, map.get(control)));
		data.put(EDIT, getURL(control));

	}

	private String getURL(ControlInterface control) throws DynamicExtensionsSystemException
	{
		StringBuffer controlUrl = new StringBuffer();
		controlUrl
				.append("<a href='");
		controlUrl.append(((HttpServletRequest)pageContext.getRequest()).getContextPath());
				controlUrl.append(Variables.resourceMapping.get(WebUIManagerConstants.SURVEY_MODE_JSP)+"?categoryId=");
		controlUrl.append(category.getId());
		controlUrl.append("&pageId=");
		controlUrl.append(controlPageIdMap.get(control));
		controlUrl.append("&controlName=");
		controlUrl.append(control.getHTMLComponentName());
		controlUrl.append("&containerIdentifier=");
		controlUrl.append(containerIdentifier);
		controlUrl.append("&");
		controlUrl.append(DEConstants.UPDATE_RESPONSE);
		controlUrl.append("=true");
		controlUrl.append("&"+DEConstants.CALLBACK_URL+"=");
		controlUrl.append(pageContext.getRequest().getParameter(DEConstants.CALLBACK_URL));

		if (recordIdentifier != null)
		{
			controlUrl.append("&recordIdentifier=");
			controlUrl.append(recordIdentifier);

		}
		controlUrl.append("'>Change</a>");
		return controlUrl.toString();

	}

}
