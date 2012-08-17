/**
 * 
 */
package edu.common.dynamicextensions.entitymanager.persister;

import java.util.LinkedList;

import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.query.generator.ColumnValueBean;

/**
 * @author Vinayak Pawar
 *
 */
public class UpdateStatement extends DmlStatement {
	private static final Logger logger = Logger.getCommonLogger(UpdateStatement.class);

	@Override
	public Long execute(JDBCDAO jdbcDao) {
		if (columns.isEmpty()) {
			return 0L;
		}
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("UPDATE ").append(tableName).append(" SET ");
		
		for (ColumnValueBean column : columns) {
			updateSql.append(column.getColumnName()).append(" = ?").append(", ");
		}
			
		updateSql.delete(updateSql.length() - 2, updateSql.length()); // remove trailing comma
		
		if (!conditions.isEmpty()) {
			updateSql.append(" WHERE ");
		}
		
		for (ColumnValueBean column : conditions) {
			updateSql.append(column.getColumnName()).append(" = ?").append(" AND ");
		}
		
		updateSql.delete(updateSql.length() - 4, updateSql.length()); // remove trailing AND
		logger.debug(updateSql.toString());
		
		try {
			LinkedList<ColumnValueBean> columnValues = new LinkedList<ColumnValueBean>(columns);
			columnValues.addAll(conditions);
			DynamicExtensionsUtility.executeUpdateQuery(updateSql.toString(), null, jdbcDao, columnValues);
		} catch (Exception e) {
			throw new RuntimeException("Error executing query: " + updateSql.toString(), e);
		}	
		
		return 0L; // unfortunately we don't get rows updated from DAO API
	}	
}