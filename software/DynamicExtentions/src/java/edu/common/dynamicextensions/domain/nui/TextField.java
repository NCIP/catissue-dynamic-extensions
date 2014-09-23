/**
 * 
 */
package edu.common.dynamicextensions.domain.nui;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import edu.common.dynamicextensions.util.global.DEConstants;

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
	
	public List<String> getConditions() {
		List<String> conditions = new ArrayList<String>();
		conditions.add(DEConstants.STARTS_WITH);
		conditions.add(DEConstants.ENDS_WITH);
		conditions.add(DEConstants.EQUALS);
		conditions.add(DEConstants.NOT_EQUALS);
		conditions.add(DEConstants.CONTAINS);
		conditions.add(DEConstants.IS_PRESENT);
		conditions.add(DEConstants.IS_NOT_PRESENT);
		return conditions;
	}
}
