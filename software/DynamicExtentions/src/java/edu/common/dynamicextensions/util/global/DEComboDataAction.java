
package edu.common.dynamicextensions.util.global;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.ui.webui.action.BaseDynamicExtensionsAction;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.global.CommonServiceLocator;

/**
 * @author kunal_kamble
 * This action is called in the auto complete dropdown combo box
 *
 */
public class DEComboDataAction extends BaseDynamicExtensionsAction
{

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String limit = request.getParameter("limit");
		String query = request.getParameter("query");
		String start = request.getParameter("start");
		String controlId = request.getParameter("controlId").split("~")[0].trim();
		String containerId = request.getParameter("controlId").split("~")[1].split("=")[1].trim();
		String rowId = request.getParameter("controlId").split("~")[2].split("=")[1].trim();
		String[] sourceValues = request.getParameter("controlId").split("~")[3].split("=");
		String[] sourceHtmlComponentValues = null;
		if (sourceValues.length > 1)
		{
			sourceHtmlComponentValues = sourceValues[1].split("~");
		}
		List<String> sourceControlValues = new ArrayList<String>();
		if (sourceHtmlComponentValues != null)
		{
			for (String sourceValue : sourceHtmlComponentValues)
			{
				if (sourceValue != null && sourceValue.length() > 0)
				{
					sourceControlValues.add(sourceValue);
				}
			}
		}
		if (rowId == null)
		{
			rowId = "-1";
		}
		Integer limitFetch = Integer.parseInt(limit);
		Integer startFetch = Integer.parseInt(start);

		JSONArray jsonArray = new JSONArray();
		JSONObject mainJsonObject = new JSONObject();

		ContainerInterface container =  EntityCache.getInstance().getContainerById(Long.valueOf(containerId));

		List<NameValueBean> nameValueBeans = null;
		for (ControlInterface control : container.getControlCollection())
		{
			if (Long.parseLong(controlId) == control.getId())
			{
				nameValueBeans = ControlsUtility.populateListOfValues(control,Integer.valueOf(rowId),sourceControlValues);
			}
		}

		Integer total = limitFetch + startFetch;

		List<NameValueBean> querySpecificNVBeans = new ArrayList<NameValueBean>();
		populateQuerySpecificNameValueBeansList(querySpecificNVBeans, nameValueBeans, query);
		mainJsonObject.put("totalCount", querySpecificNVBeans.size());

		for (int i = startFetch; i < total && i < querySpecificNVBeans.size(); i++)
		{
			JSONObject jsonObject = new JSONObject();
			Locale locale=CommonServiceLocator.getInstance().getDefaultLocale();
			if (query == null
					|| querySpecificNVBeans.get(i).getName().toLowerCase(locale).contains(
							query.toLowerCase(locale)) || query.length() == 0)
			{
				jsonObject.put("id", querySpecificNVBeans.get(i).getValue());
				jsonObject.put("field", querySpecificNVBeans.get(i).getName());
				jsonArray.put(jsonObject);
			}
		}

		mainJsonObject.put("row", jsonArray);
		response.flushBuffer();
		PrintWriter out = response.getWriter();
		out.write(mainJsonObject.toString());

		return null;
	}

	/**
	 * This method populates name value beans list as per query,
	 * i.e. word typed into the auto-complete drop-down text field.
	 * @param querySpecificNVBeans
	 * @param nameValueBeans
	 * @param query
	 */
	private void populateQuerySpecificNameValueBeansList(List<NameValueBean> querySpecificNVBeans,
			List<NameValueBean> nameValueBeans, String query)
	{
		Locale locale=CommonServiceLocator.getInstance().getDefaultLocale();
		for (NameValueBean nvb : nameValueBeans)
		{
			if (nvb.getName().toLowerCase(locale).contains(query.toLowerCase(locale)))
			{
				querySpecificNVBeans.add(nvb);
			}
		}
	}

}
