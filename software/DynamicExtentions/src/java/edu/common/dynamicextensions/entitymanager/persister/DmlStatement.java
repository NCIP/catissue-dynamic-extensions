/**
 * 
 */
package edu.common.dynamicextensions.entitymanager.persister;

import java.util.ArrayList;
import java.util.List;

import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.query.generator.ColumnValueBean;

/**
 * @author Vinayak Pawar
 *
 */
public abstract class DmlStatement {
	protected String tableName;
	
	protected List<ColumnValueBean> columns = new ArrayList<ColumnValueBean>();
		
	protected List<ColumnValueBean> conditions = new ArrayList<ColumnValueBean>();
	
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	/**
	 * @return the columns
	 */
	public List<ColumnValueBean> getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(List<ColumnValueBean> columns) {
		this.columns = columns;
	}
	
	public void setColumn(String name, Object value) {
		columns.add(new ColumnValueBean(name, value));		
	}

	/**
	 * @return the conditions
	 */
	public List<ColumnValueBean> getConditions() {
		return conditions;
	}

	/**
	 * @param conditions the conditions to set
	 */
	public void setConditions(List<ColumnValueBean> conditions) {
		this.conditions = conditions;
	}
	
	public void setCondition(String name, Object value) {
		conditions.add(new ColumnValueBean(name, value));		
	}
		
	public abstract Long execute(JDBCDAO jdbcDao);
}
