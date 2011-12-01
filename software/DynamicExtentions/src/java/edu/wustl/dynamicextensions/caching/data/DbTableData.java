package edu.wustl.dynamicextensions.caching.data;

import java.util.Collection;

/**
 *
 * @author Vinayak Pawar (vinayak.pawar@krishagni.com)
 */
public interface DbTableData {
    public String getTableName();
    
    public String getKeyColumnName();
    
    public String[] getColumnNames();
    
    public Object getColumnValue(Object[] row, String columnName);
    
    public Object getColumnValue(Object[] row, String columnName, String columnType);
    
    public void setColumnValue(Object[] row, String columnName, Object value);
        
    public int getColumnCount();
    
    public int getRowCount();
    
    public Object[] getRow(Object identifier);
    
    public Collection<Object[]> getRows();    
    
    public void addRow(Object[] row);
            
    public Collection<Object> getKeys();
    
    public Collection<Object> getKeys(String fkColumnName, Object fkColumnValue);
            
    public void createIndex(String columnName, String columnType);
    
    public void createIndexIfAbsent(String columnName, String columnType);  
    
    public void deleteIndex(String columnName);
    
    public void clear();
}