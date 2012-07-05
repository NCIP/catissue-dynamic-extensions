
package edu.common.dynamicextensions.summary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.SummaryControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

public abstract class AbstractSummaryDataManager
{

	protected List<String> headerList = new ArrayList<String>();
	protected List<Map<String, String>> rowData = new ArrayList<Map<String, String>>();
	protected final String SR_NO = "#";
	protected final String QUESTION = "QUESTION";
	protected final String RESPONSE = "RESPONSE";
	protected final String EDIT = "EDIT";

	protected abstract void populateHeaderList();

	protected abstract void populateRow(ControlInterface control,
			Map<ControlInterface, Object> map, Map<String, String> data)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	public void populateTable(List<Map<ControlInterface, Object>> controlValueCollection)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		populateHeaderList();
		int rowCounter = 1;
		for (Map<ControlInterface, Object> map : controlValueCollection)
		{
			ControlInterface control = map.keySet().iterator().next();
			if (control instanceof SummaryControlInterface)
			{
				Map<String, String> data = new HashMap<String, String>();
				data.put(SR_NO, String.valueOf(rowCounter++));
				populateRow(control, map, data);
				rowData.add(data);
			}
		}
	}

	public List<String> getHeaderList()
	{
		return headerList;
	}

	public List<Map<String, String>> getRowData()
	{
		return rowData;
	}

	public void setRowData(List<Map<String, String>> rowData)
	{
		this.rowData = rowData;
	}

	public void setHeaderList(List<String> headerList)
	{
		this.headerList = headerList;
	}

	protected String getValueAsString(ControlInterface control, Object object)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		control.setValue(object);
		List<String> values = control.getValueAsStrings();
		StringBuffer value = new StringBuffer();
		for (String string : values)
		{
			value.append(string);
			value.append(',');
		}
		if(value.length() > 0)
		{
			value.deleteCharAt(value.length() - 1);	
		}
		
		return value.toString();
	}
}
