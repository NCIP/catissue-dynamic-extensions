package edu.common.dynamicextensions.domain.userinterface;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import edu.common.dynamicextensions.domain.DynamicExtensionBaseDomainObject;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.DynamicExtensionLayoutInterface;

public class Page extends DynamicExtensionBaseDomainObject implements
		Serializable, Comparable {

	private String description;
	private static final long serialVersionUID = 4876029224617194342L;
	private DynamicExtensionLayoutInterface layout;
	private Collection<ControlInterface> controlCollection = new HashSet<ControlInterface>();

	@Override
	public Long getId() {
		return id;
	}
	
	public void setDescription (String description) {
		this.description = description;
	}
	public String getDescription () {
		return this.description;
	}

	public DynamicExtensionLayoutInterface getLayout() {
		return layout;
	}

	public void setLayout(DynamicExtensionLayoutInterface layout) {
		this.layout = layout;
	}

	public Collection<ControlInterface> getControlCollection() {
		return controlCollection;
	}

	public void setControlCollection(
			final Collection<ControlInterface> controlCollection) {
		this.controlCollection = controlCollection;
	}
	
	public int compareTo(Object o) {
		Page otherPage = (Page)o;
		
		if (this.id == null && otherPage.id == null) {
			return 0;
		} else if (this.id == null) {
			return 1; // the new id will be greater than existing
		} else if (otherPage.id == null) {
			return -1;
		}
		
		return this.id.compareTo(otherPage.id);
	}
}