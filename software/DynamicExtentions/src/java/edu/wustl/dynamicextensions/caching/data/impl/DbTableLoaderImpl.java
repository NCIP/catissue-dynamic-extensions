package edu.wustl.dynamicextensions.caching.data.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import edu.wustl.dynamicextensions.caching.data.DbTableData;
import edu.wustl.dynamicextensions.caching.data.DbTableLoader;
import edu.wustl.dynamicextensions.caching.data.DbUtil;

/**
 *
 * @author Vinayak Pawar (vinayak.pawar@krishagni.com)
 */
public class DbTableLoaderImpl implements DbTableLoader {
    private static final String SELECT_ALL_QUERY = "SELECT * FROM %s";
    
    private Connection jdbcConn;
    
    public DbTableLoaderImpl() {
    }
    
    public void setJdbcConnection(Connection jdbcConn) {
        this.jdbcConn = jdbcConn;
    }
    
    public static DbTableLoader createDbTableLoader() {
        return new DbTableLoaderImpl();
    }
    
    public DbTableData loadTable(String tableName) {
        return loadTable(tableName, null, null);
        
    }
    
    public DbTableData loadTable(String tableName, String keyColumnName, String keyColumnType) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = jdbcConn.prepareStatement(String.format(SELECT_ALL_QUERY, tableName));
            rs = stmt.executeQuery();
            
            String[] columnNames = DbUtil.getColumnNames(rs.getMetaData());
            DbTableData dbTableData = 
                    DbTableDataImpl.createDbTableData(tableName, keyColumnName, keyColumnType, columnNames);
            
            while (rs.next()) {
                dbTableData.addRow(DbUtil.fetchRow(rs, columnNames.length));
            }
            
            return dbTableData;
        } catch (Exception e) {
            throw new RuntimeException("Error loading table: " + tableName, e);
        } finally {
            if (rs != null) {
                try { 
                    rs.close();
                } catch (Exception e) {
                    // log an error
                }
            }
            
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {
                    // log an error
                }
            }
        }                
    }       
}
