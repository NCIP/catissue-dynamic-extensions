
package edu.common.dynamicextensions.summary;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ColumnFormatter
{

	private String header;
	private String key;
	private Map<String, String> dataFormatter = new HashMap<String, String>();

	public ColumnFormatter(String key, String header)
	{
		this.header = header;
		this.key = key;
	}
	public ColumnFormatter(String key)
	{
		this.key = key;
	}
	public String getHeader()
	{
		return header;
	}

	public void setHeader(String header)
	{
		this.header = header;
	}

	public Map<String, String> getDataFormatter()
	{
		return dataFormatter;
	}

	public void setDataFormatter(Map<String, String> dataFormatter)
	{
		this.dataFormatter = dataFormatter;
	}

	@Override
	public int hashCode()
	{
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public boolean equals(Object arg0)
	{
		boolean equals = false;
		if (arg0 instanceof ColumnFormatter && ((ColumnFormatter) arg0).getKey() != null
				&& this.key != null)
		{
			equals = this.key.equals(((ColumnFormatter) arg0).getKey());
		}
		return equals;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}
	public String getAttributeAsString()
	{

		StringBuffer buffer = new StringBuffer();
		for(Entry<String, String> entry:dataFormatter.entrySet())
		{
			buffer.append(" ");
			buffer.append(entry.getKey());
			buffer.append("=");
			buffer.append(entry.getValue());
		}
		return buffer.toString();
	}

}
