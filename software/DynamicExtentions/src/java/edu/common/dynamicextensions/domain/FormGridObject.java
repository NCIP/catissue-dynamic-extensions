
package edu.common.dynamicextensions.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FormGridObject
{

	private Long identifier;
	private String username;
	private String lastUpdated;
	private List<String> columns = new ArrayList<String>();

	public Long getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(Long identifier)
	{
		this.identifier = identifier;
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

}
