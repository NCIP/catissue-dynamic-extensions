
package edu.common.dynamicextensions.domain;

import java.util.ArrayList;
import java.util.List;

public class FormGridObject
{

	private Long recordEntryId;
	private String username;
	private String lastUpdated;
	private String formURL;
	private String deUrl;
	private List<String> columns = new ArrayList<String>();

	public Long getRecordEntryId()
	{
		return recordEntryId;
	}

	public void setRecordEntryId(Long recordEntryId)
	{
		this.recordEntryId = recordEntryId;
	}

	public List<String> getColumns()
	{
		return columns;
	}

	public void setColumns(List<String> columns)
	{
		this.columns = columns;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getLastUpdated()
	{
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated)
	{
		this.lastUpdated = lastUpdated;
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

}
