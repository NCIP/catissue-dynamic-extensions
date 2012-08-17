/**
 * 
 */
package edu.common.dynamicextensions.entitymanager.persister;

import java.util.LinkedList;
import java.util.List;

import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

/**
 * @author Vinayak Pawar
 *
 */
public class InsertStatement extends DmlStatement {
	
	private static final Logger logger = Logger.getCommonLogger(InsertStatement.class);
	
	private Long id;
	
	@Override
	public Long execute(JDBCDAO jdbcDao) {
		StringBuilder columnNames = new StringBuilder("IDENTIFIER");
		StringBuilder valueHolders = new StringBuilder("?");
		
		for (ColumnValueBean nameValue : columns) {
			columnNames.append(", ").append(nameValue.getColumnName());
			valueHolders.append(", ").append("?");			
		}
		
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("INSERT INTO ").append(tableName)
		         .append("(").append(columnNames.toString()).append(")")
		         .append(" VALUES (").append(valueHolders.toString()).append(")");
	
		logger.debug(insertSql.toString());
		
		try {
			if (id == null) {
				id = generateIdentifier(tableName, jdbcDao);
			}
			
			LinkedList<ColumnValueBean> columnValues = new LinkedList<ColumnValueBean>();
			columnValues.add(new ColumnValueBean("IDENTIFIER", id));
			columnValues.addAll(columns);
			
			DynamicExtensionsUtility.executeUpdateQuery(insertSql.toString(), null, jdbcDao, columnValues);
		} catch (Exception e) {
			throw new RuntimeException("Error executing query (id = " + id +") : " + insertSql.toString(), e);
		}
		
		return id;
	}
	
	
	public void setIdentifier(Long id) {
		this.id = id;
	}

	@SuppressWarnings("unchecked")
	protected Long generateIdentifier(String tabName, JDBCDAO jdbcDao) {
		try {
			StringBuilder sql = new StringBuilder("SELECT max(IDENTIFIER) FROM ").append(tabName);
			
			logger.debug(sql.toString());
			List<List<String>> result = jdbcDao.executeQuery(sql.toString(), null, null);
			
			//
			// first get to access row, second get to access column within row
			//
			Long maxId = 0L;
			if (!result.isEmpty() && !result.get(0).isEmpty() && !result.get(0).get(0).isEmpty()) {
				maxId = Long.parseLong(result.get(0).get(0)); 
			}
			
			return maxId + 1L; 			
		} catch (DAOException daoe) {
			throw new RuntimeException("Couldn't obtain identifier for " + tabName, daoe);
		} 
	}		
}