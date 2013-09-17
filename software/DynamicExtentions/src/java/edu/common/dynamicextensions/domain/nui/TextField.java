/**
 * 
 */
package edu.common.dynamicextensions.domain.nui;

import org.apache.commons.lang.StringUtils;

public abstract class TextField extends Control {

	private static final long serialVersionUID = -6827748743355556283L;

	private int noOfColumns;
	
	private String defaultValue = "";
	
	public int getNoOfColumns() {
		return noOfColumns;
	}

	public void setNoOfColumns(int noOfColumns) {
		this.noOfColumns = noOfColumns;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + noOfColumns;
		result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!super.equals(obj)) {
			return false;
		}
		
		TextField other = (TextField) obj;
		if (noOfColumns != other.noOfColumns ||
			!StringUtils.equals(defaultValue, other.defaultValue)) {
			return false;
		} 
		
		return true;
	}		
}