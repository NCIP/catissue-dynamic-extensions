
package edu.common.dynamicextensions.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormGridObject implements Comparable<FormGridObject>
{

	private Long recordEntryId;
	private String formURL;
	private String deUrl;
	private List<String> headers = new ArrayList<String>();
	private Map<String, String> columns = new HashMap<String, String>();

	public Long getRecordEntryId()
	{
		return recordEntryId;
	}

	public void setRecordEntryId(Long recordEntryId)
	{
		this.recordEntryId = recordEntryId;
	}

	public Map<String, String> getColumns()
	{
		return columns;
	}

	public void setColumns(Map<String, String> map)
	{
		this.columns = map;
	}

	public String getFormURL()
	{
		return formURL;
	}

	public void setFormURL(String formURL)
	{
		this.formURL = formURL;
	}

	public String getDeUrl()
	{
		return deUrl;
	}

	public void setDeUrl(String deUrl)
	{
		this.deUrl = deUrl;
	}

	
	public List<String> getHeaders()
	{
		return headers;
	}

	
	public void setHeaders(List<String> headers)
	{
		this.headers = headers;
	}


	public int compareTo(FormGridObject formGridObject)
	{
		return (int) -(formGridObject.getRecordEntryId() - this.recordEntryId);
	}

}
