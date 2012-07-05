
package edu.common.dynamicextensions.ui.webui.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.summary.AbstractSummaryDataManager;
import edu.common.dynamicextensions.summary.DefaultSummaryDataManager;
import edu.common.dynamicextensions.summary.SurveySummaryDataManager;
import edu.common.dynamicextensions.ui.webui.util.ContainerUtility;
import edu.common.dynamicextensions.util.DELayoutEnum;
import edu.wustl.cab2b.server.util.DynamicExtensionUtility;
import edu.wustl.common.util.logger.Logger;

public class FormSummaryGeneratorTag extends DynamicExtensionsFormBaseTag
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1855464217946194360L;
	private static final Logger LOGGER = Logger.getCommonLogger(FormSummaryGeneratorTag.class);
	private AbstractSummaryDataManager dataManager;
	private List<Map<ControlInterface, Object>> controlValueCollection;

	@Override
	public int doEndTag() throws JspException
	{

		controlValueCollection = new ArrayList<Map<ControlInterface, Object>>();
		ContainerUtility.populateControlValueCollection(controlValueCollection, container,
				dataValueMap);
		try
		{
			populateTable();
			generateTable();

		}
		catch (DynamicExtensionsCacheException e)
		{
			LOGGER.error("Error generating form summary.", e);
		}
		catch (DynamicExtensionsSystemException e)
		{
			LOGGER.error("Error generating form summary.", e);
		}
		catch (DynamicExtensionsApplicationException e)
		{
			LOGGER.error("Error generating form summary.", e);
		}
		return super.doEndTag();
	}

	private void populateTable() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		if (DELayoutEnum.SURVEY == DynamicExtensionUtility.getLayout(containerIdentifier))
		{
			dataManager = new SurveySummaryDataManager(containerIdentifier,recordIdentifier,((HttpServletRequest)pageContext.getRequest()).getContextPath());
		}
		else
		{
			dataManager = new DefaultSummaryDataManager();
		}
		dataManager.populateTable(controlValueCollection);

	}

	private void generateTable()
	{
		StringBuffer tableString = new StringBuffer();
		try
		{
			tableString.append("<table cellspacing='3' cellpadding='3' border='0' align='center'>");
			tableString.append("<tr>");
			for (String headerString : dataManager.getHeaderList())
			{
				tableString.append("<td class='td_color_6e81a6'>");
				tableString.append(headerString);
				tableString.append("</td>");
			}
			tableString.append("</tr>");

			int rowcount = 0;
			for (Map<String, String> map : dataManager.getRowData())
			{
				if(rowcount %2 == 0)
				{
					tableString.append("<tr class='td_color_f0f2f6'>");	
				}else
				{
					tableString.append("<tr class='formField_withoutBorder'>");	
				}
				rowcount++;
				for (String headerString : dataManager.getHeaderList())
				{
					tableString.append("<td>");
					tableString.append(map.get(headerString));
					tableString.append("</td>");
				}
				tableString.append("</tr>");
			}

			tableString.append("</table>");
			jspWriter.append(tableString);
		}
		catch (IOException e)
		{
			LOGGER.error("Error generating table.", e);
		}
	}
}
