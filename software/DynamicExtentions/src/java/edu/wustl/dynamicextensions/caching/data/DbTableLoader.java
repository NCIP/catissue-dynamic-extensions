package edu.wustl.dynamicextensions.caching.data;

import java.sql.Connection;

/**
 *
 * @author Vinayak Pawar (vinayak.pawar@krishagni.com)
 */
public interface DbTableLoader {
    public DbTableData loadTable(String tableName);
    
    public DbTableData loadTable(String tableName, String keyColumnName, String keyColumnType);
    
    public void setJdbcConnection(Connection jdbcConn);
}
