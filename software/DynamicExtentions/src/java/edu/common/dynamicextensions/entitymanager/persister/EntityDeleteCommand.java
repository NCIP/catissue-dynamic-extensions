/**
 * 
 */
package edu.common.dynamicextensions.entitymanager.persister;

import java.util.LinkedList;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.query.generator.ColumnValueBean;

/**
 * @author Vinayak Pawar
 *
 */
public class EntityDeleteCommand {
	private EntityInterface entity;
	
	private Long id;
	
	public EntityDeleteCommand(EntityInterface entity, Long id) {
		this.entity = entity;
		this.id = id;
	}
	
	public Long execute(JDBCDAO jdbcDao) {		
		StringBuilder deleteSql = new StringBuilder();
		deleteSql.append("DELETE FROM ").append(entity.getTableProperties().getName()).append(" WHERE IDENTIFIER = ?");
				
		try {
			LinkedList<ColumnValueBean> columnValues = new LinkedList<ColumnValueBean>();
			columnValues.add(new ColumnValueBean("IDENTIFIER", id));
			DynamicExtensionsUtility.executeUpdateQuery(deleteSql.toString(), null, jdbcDao, columnValues);
			return 0L;
		} catch (Exception e) {
			throw new RuntimeException("Error executing query (id = " + id +") : " + deleteSql.toString(), e);
		} 	
	}	
}