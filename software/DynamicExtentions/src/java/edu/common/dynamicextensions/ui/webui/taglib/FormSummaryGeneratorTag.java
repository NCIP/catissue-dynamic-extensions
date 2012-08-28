
package edu.common.dynamicextensions.ui.webui.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;

import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.summary.AbstractSummaryDataManager;
import edu.common.dynamicextensions.summary.ColumnFormatter;
import edu.common.dynamicextensions.summary.DefaultSummaryDataManager;
import edu.common.dynamicextensions.summary.SurveySummaryDataManager;
import edu.common.dynamicextensions.ui.webui.util.ContainerUtility;
import edu.common.dynamicextensions.util.DELayoutEnum;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
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
	/**
	 * Comma separated list of columns to be excluded
	 */
	private String exclude;

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
		if (DELayoutEnum.SURVEY == DynamicExtensionsUtility.getLayout(containerIdentifier))
		{
			dataManager = new SurveySummaryDataManager(containerIdentifier,recordIdentifier,pageContext);
		}
		else
		{
			dataManager = new DefaultSummaryDataManager();
		}
		if(exclude != null && !"".equals(exclude)){
			dataManager.setExcludeColumns(exclude.split(","));
		}
		dataManager.populateTable(controlValueCollection);

	}

	private void generateTable()
	{
		StringBuffer tableString = new StringBuffer();
		try
		{
			tableString.append("<table cellspacing='3' cellpadding='3' border='0' width='100%' align='center'>");
			tableString.append("<tr>");
			for (ColumnFormatter columnFmt : dataManager.getHeaderList())
			{
				tableString.append("<td class='td_color_6e81a6' align='center'>");
				tableString.append(columnFmt.getHeader());
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
				for (ColumnFormatter columnFmt : dataManager.getHeaderList())
				{
					String td = "<td %s>";
					tableString.append(String.format(td, columnFmt.getAttributeAsString()));
					tableString.append(map.get(columnFmt.getKey()));
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

	
	public String getExclude()
	{
		return exclude;
	}

	
	public void setExclude(String exclude)
	{
		this.exclude = exclude;
	}
	
}
