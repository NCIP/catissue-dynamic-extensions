package edu.common.dynamicextensions.util;


public class DynamicQueryBean
{
	private StringBuffer fromPart = new StringBuffer();
	private StringBuffer wherePart = new StringBuffer();
	private String controlCaption = "";
	private Object value = new Object();
	private String recordEntryParamName = "";
	private Long staticEntityId = 0L;
	
	public StringBuffer getFromPart()
	{
		return fromPart;
	}
	
	public void setFromPart(StringBuffer fromPart)
	{
		this.fromPart = fromPart;
	}
	
	public StringBuffer getWherePart()
	{
		return wherePart;
	}
	
	public void setWherePart(StringBuffer wherePart)
	{
		this.wherePart = wherePart;
	}
	
	public String getControlCaption()
	{
		return controlCaption;
	}
	
	public void setControlCaption(String controlCaption)
	{
		this.controlCaption = controlCaption;
	}
	
	public Object getValue()
	{
		return value;
	}
	
	public void setValue(Object value)
	{
		this.value = value;
	}

	
	public String getRecordEntryParamName()
	{
		return recordEntryParamName;
	}

	
	public void setRecordEntryParamName(String recordEntryParamName)
	{
		this.recordEntryParamName = recordEntryParamName;
	}

	
	public Long getStaticEntityId()
	{
		return staticEntityId;
	}

	
	public void setStaticEntityId(Long staticEntityId)
	{
		this.staticEntityId = staticEntityId;
	}
	
	
	
	

}
