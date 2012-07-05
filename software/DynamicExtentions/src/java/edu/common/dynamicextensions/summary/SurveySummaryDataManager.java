
package edu.common.dynamicextensions.summary;

import java.util.HashMap;
import java.util.Map;

import edu.common.dynamicextensions.domain.userinterface.Page;
import edu.common.dynamicextensions.domain.userinterface.SurveyModeLayout;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.server.util.DynamicExtensionUtility;

public class SurveySummaryDataManager extends AbstractSummaryDataManager
{

	private CategoryInterface category;
	private Long recordIdentifier;
	private Map<ControlInterface, Long> controlPageIdMap = new HashMap<ControlInterface, Long>();
	private String contextPath;

	public SurveySummaryDataManager(Long containerIdentifier, Long recordIdentifier, String contextPath)
			throws DynamicExtensionsCacheException
	{
		this.recordIdentifier = recordIdentifier;
		this.contextPath = contextPath;
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
		controlUrl.append(contextPath);
				controlUrl.append("/AjaxcodeHandlerAction?ajaxOperation=renderSurveyMode&&categoryId=");
		controlUrl.append(category.getId());
		controlUrl.append("&pageId=");
		controlUrl.append(controlPageIdMap.get(control));
		controlUrl.append("&controlName=");
		controlUrl.append(control.getHTMLComponentName());
		controlUrl.append("&containerIdentifier=");
		controlUrl.append(control.getParentContainer().getId());

		if (recordIdentifier != null)
		{
			controlUrl.append("&recordIdentifier=");
			controlUrl.append(recordIdentifier);

		}
		controlUrl.append("'>Edit</a>");
		return controlUrl.toString();

	}

}
