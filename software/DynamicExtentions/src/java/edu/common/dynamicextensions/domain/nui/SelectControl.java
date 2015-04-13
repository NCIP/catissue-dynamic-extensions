/**
 * 
 */
package edu.common.dynamicextensions.domain.nui;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.common.dynamicextensions.domain.nui.PvDataSource.Ordering;
import edu.common.dynamicextensions.napi.ControlValue;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.dynamicextensions.formdesigner.utility.PermissibleValueComparator;

public abstract class SelectControl extends Control {
	
	private static final long serialVersionUID = 2286060335229542875L;
	
	private PvDataSource pvDataSource;
	
	public PvDataSource getPvDataSource() {
		return pvDataSource;
	}

	public void setPvDataSource(PvDataSource pvDataSource) {
		this.pvDataSource = pvDataSource;
	}
	
	public List<PermissibleValue> getPvs() {
		return getPvs(Calendar.getInstance().getTime());
	}
	
	public List<PermissibleValue> getPvs(Date activationDate) {
		return pvDataSource != null ? pvDataSource.getPermissibleValues(activationDate) : null;
	}
	
	public PermissibleValue getDefaultValue() {
		return getDefaultValue(Calendar.getInstance().getTime());
	}
	
	public PermissibleValue getDefaultValue(Date activationDate) {
		return pvDataSource != null ? pvDataSource.getDefaultValue(activationDate) : null;
	}

	@Override
	public List<ColumnDef> getColumnDefs() {
		return Collections.singletonList(ColumnDef.get(getDbColumnName(), getDbType()));
	}
	
	@Override
	public DataType getDataType() {
		return pvDataSource.getDataType();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T fromString(String value) {
		if (value == null) {
			return null;
		}
		
		Object result = null;
		DataType pvType = pvDataSource.getDataType();
		if (pvType == DataType.STRING) {
			result = value;
		} else if (pvType == DataType.INTEGER || pvType == DataType.FLOAT) {
			result = new BigDecimal(value);
		} else if (pvType == DataType.BOOLEAN) {
			if (value.equals("true")) {
				result = 1;
			} else {
				result = 0;
			}
		} else if (pvType == DataType.DATE) {
			try {
				result = new SimpleDateFormat(pvDataSource.getDateFormat()).parse(value);
			} catch (Exception e) {
				throw new RuntimeException("Error parsing date string: " + value, e);
			}			
		}
		
		return (T)result;
	}
	
	@Override
	public String toString(Object value) {
		if (value == null) {
			return null;
		}
		
		String result = value.toString();
		DataType pvType = pvDataSource.getDataType();
		if (pvType == DataType.BOOLEAN && value instanceof Number) {
			Number number = (Number)value;
			if (number.intValue() == 1) {
				result = "true";
			} else {
				result = "false";
			}
		} else if (pvType == DataType.DATE && value instanceof Date) {
			try {
				result = new SimpleDateFormat(pvDataSource.getDateFormat()).format((Date)value);
			} catch (Exception e) {
				throw new RuntimeException("Error converting date to string: " + value, e);				
			}
		}
		
		return result;
	}
	
	protected String getDbType() {
		String dbType;
		switch (pvDataSource.getDataType()) {
			case STRING:
				dbType = "VARCHAR2(4000)";
				break;
				
			case INTEGER:
			case FLOAT:
			case BOOLEAN:
				dbType = "NUMBER";
				break;
				
			case DATE:
				dbType = "DATE";
				break;
				
			default:
				dbType = null;
				break;
		}
		
		return dbType;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result	+ ((pvDataSource == null) ? 0 : pvDataSource.hashCode());
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
		
		SelectControl other = (SelectControl) obj;
		if ((pvDataSource == null && other.pvDataSource != null) ||
			!pvDataSource.equals(other.pvDataSource)) {
			return false;
		}
		
		return true;
	}
	
		
	//////////////////////////////////////////////////////////////////////////
	//
	// Used by UI
	//
	//////////////////////////////////////////////////////////////////////////	
	protected String getDisplayValue(ControlValue controlValue, Date activationDate) {
		String value = (String) controlValue.getValue();
		if (value == null) {
			PermissibleValue defaultValue = getDefaultValue(activationDate); 
			value = defaultValue != null ? defaultValue.getValue() : "";
		}		
		return value;
	}

	public List<PermissibleValue> getPVList(Date encounterDate, ControlValue controlValue) {
		List<PermissibleValue> permissibleValues = controlValue.getPermissibleValues();
		if (permissibleValues == null || permissibleValues.isEmpty()) {
			permissibleValues = getPvDataSource().getPermissibleValues(encounterDate);
		}
		if (Ordering.ASC.equals(getPvDataSource().getOrdering()) ) {
			Collections.sort(permissibleValues, new PermissibleValueComparator());
		} 
		else if (Ordering.DESC.equals(getPvDataSource().getOrdering()) ) {
			Collections.sort(permissibleValues, new PermissibleValueComparator());
			Collections.reverse(permissibleValues);
		}
		return permissibleValues;
	}

	@Override
	public Set<String> getAllConceptCodes() {
		Set<String> conceptCodeCollection = new HashSet<String>();
		for (PermissibleValue permissibleValue : getPvs()) {
				conceptCodeCollection.add(permissibleValue.getConceptCode());
		}
		return conceptCodeCollection;
	}

	public String getPvAsStringByConceptCode(String conceptCode) {
		return getPvAsStringByConceptCode(conceptCode, Calendar.getInstance().getTime());

	}

	public String getPvAsStringByConceptCode(String conceptCode, Date activationDate) {

		String pvString = null;

		for (PermissibleValue permissibleValue : getPvs(activationDate)) {

			if (conceptCode.equals(permissibleValue.getConceptCode())) {
				pvString = permissibleValue.getValue();
				break;
			}
		}
		return pvString;

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
