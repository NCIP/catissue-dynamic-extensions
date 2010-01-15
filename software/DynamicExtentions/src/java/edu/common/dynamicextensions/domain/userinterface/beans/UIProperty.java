
package edu.common.dynamicextensions.domain.userinterface.beans;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(AccessType.FIELD)
public class UIProperty
{

	@XmlAttribute
	private String value;

	@XmlAttribute
	private String key;

	private Collection<UIProperty> uiProperty;

	public UIProperty()
	{
		uiProperty = new ArrayList<UIProperty>();
	}

	public UIProperty(String key, String value)
	{
		super();
		this.key = key;
		this.value = value;
		uiProperty = new ArrayList<UIProperty>();
	}

	public UIProperty(String key, String value, Collection<UIProperty> uiProperty)
	{
		super();
		this.key = key;
		this.value = value;
		this.uiProperty = uiProperty;
	}

	public void add(UIProperty propertyToBeAdd)
	{
		uiProperty.add(propertyToBeAdd);
	}

	public Collection<UIProperty> getUiProperty()
	{
		return uiProperty;
	}

	public void setUiProperty(Collection<UIProperty> uiProperty)
	{
		this.uiProperty = uiProperty;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		return "UIProperty [key=" + key + ", value=" + value + "]";
	}

}
