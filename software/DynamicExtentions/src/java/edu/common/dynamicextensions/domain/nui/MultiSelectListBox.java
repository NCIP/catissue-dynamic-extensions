
package edu.common.dynamicextensions.domain.nui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import edu.common.dynamicextensions.napi.ControlValue;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;

public class MultiSelectListBox extends ListBox implements MultiSelectControl {

	private static final long serialVersionUID = 3003089628345200684L;

	private String tableName;
	
	private String parentKeyColumn  = "IDENTIFIER";
	
	private String foreignKeyColumn = "RECORD_ID";
	
	public MultiSelectListBox() {
		setDbColumnName("VALUE");
	}
	
	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public List<ColumnDef> getColumnDefs() {
		List<ColumnDef> columnDefs = new ArrayList<ColumnDef>();
		columnDefs.add(ColumnDef.get(getDbColumnName(), getDbType()));
		columnDefs.add(ColumnDef.get(foreignKeyColumn, "NUMBER"));

		return columnDefs;
	}
	
	@Override
	public String getParentKey() {
		return parentKeyColumn;
	}

	@Override
	public void setParentKey(String parentKeyColumn) {
		if (parentKeyColumn == null) {
			parentKeyColumn = "IDENTIFIER";
		}
		
		this.parentKeyColumn = parentKeyColumn;		
	}
	
	@Override
	public String getForeignKey() {
		return foreignKeyColumn;
	}

	@Override
	public void setForeignKey(String foreignKeyColumn) {
		if (foreignKeyColumn == null) {
			foreignKeyColumn = "RECORD_ID";
		}
		
		this.foreignKeyColumn = foreignKeyColumn;		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result	+ ((tableName == null) ? 0 : tableName.hashCode());
		result = prime * result	+ ((parentKeyColumn == null) ? 0 : parentKeyColumn.hashCode());
		result = prime * result	+ ((foreignKeyColumn == null) ? 0 : foreignKeyColumn.hashCode());
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
		
		MultiSelectListBox other = (MultiSelectListBox) obj;
		if (!StringUtils.equals(tableName, other.tableName) ||
			!StringUtils.equals(parentKeyColumn, other.parentKeyColumn) ||
			!StringUtils.equals(foreignKeyColumn, other.foreignKeyColumn)) {
			return false;
		}

		return true;
	}

	@Override
	protected boolean isPVSelected(ControlValue controlValue, PermissibleValue pv) {
		boolean isPVSelected = false;
		
		if (controlValue.getValue() != null) {
			List<String> values = Arrays.asList((String[]) controlValue.getValue());
			
			if (values.contains(DynamicExtensionsUtility.replaceHTMLSpecialCharacters(pv.getValue()))) {
					isPVSelected = true;
			}
		} else if (getDefaultValue() != null) {
			
			if (getDefaultValue().getValue().equals(DynamicExtensionsUtility.replaceHTMLSpecialCharacters(pv.getValue()))) {
				isPVSelected = true;
			}
		}
		return isPVSelected;
	}

	@Override
	protected String getMultiselectString() {
		return "MULTIPLE";
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
