package edu.wustl.dynamicextensions.caching.data.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import edu.wustl.dynamicextensions.caching.data.DbTableData;
import edu.wustl.dynamicextensions.caching.util.ReflectionUtil;

/**
 *
 * @author Vinayak Pawar (vinayak.pawar@krishagni.com)
 */
public class DbTableDataImpl implements DbTableData {
    private String tableName;
    
    private String keyColumnName;
    
    private Class keyColumnType;
    
    private ConcurrentMap<String, Integer> columnMap = new ConcurrentHashMap<String, Integer>();
    
    private ConcurrentMap<Object, Object[]> rows = new ConcurrentHashMap<Object, Object[]>();
    
    private ConcurrentMap<String, DbTableIndexMap> indices = new ConcurrentHashMap<String, DbTableIndexMap>();
        
    public DbTableDataImpl(
            String tableName, 
            String keyColumnName, 
            String keyColumnType, 
            String[] columnNames) {
        this.tableName = tableName;
        
        if (keyColumnName != null && keyColumnType != null) {
            this.keyColumnName = keyColumnName;
            this.keyColumnType = ReflectionUtil.getClass(keyColumnType);
        }
        
        for (int i = 0; i < columnNames.length; ++i) {
            columnMap.put(columnNames[i].trim().toUpperCase(), i);
        }        
    }
    
    public static DbTableData createDbTableData(
            String tableName, 
            String keyColumnName, 
            String keyColumnType, 
            String[] columnNames) {
        return new DbTableDataImpl(tableName, keyColumnName, keyColumnType, columnNames);
    }
        
    public String getTableName() {
        return tableName;
    }
    
    public String getKeyColumnName() {
        return keyColumnName;
    }
    
    public String[] getColumnNames() {
        String[] columnNames = new String[columnMap.size()];
        for (Map.Entry<String, Integer> entry : columnMap.entrySet()) {
            columnNames[entry.getValue()] = entry.getKey();
        }
        
        return columnNames;
    }
    
    public Object getColumnValue(Object[] row, String columnName) {
        if (row == null || row.length == 0) {
            return null;            
        }
        
        Integer columnIdx = columnMap.get(columnName.toUpperCase());
        if (columnIdx == null) {
            throw new RuntimeException("Invalid column name " + tableName + "." + columnName);
        }
        
        if (columnIdx < 0 || columnIdx >= row.length) {
            throw new RuntimeException("Invalid column index " + tableName + "." + columnIdx);
        }
        
        return row[columnIdx];
    }
    
    public Object getColumnValue(Object[] row, String columnName, String columnType) {
        Object columnValue = getColumnValue(row, columnName);
        if (columnValue != null) {
            columnValue = ReflectionUtil.convert(columnValue, columnType);
        }
        return columnValue;
    }
    
    public Object getColumnValue(Object[] row, String columnName, Class columnType) {
        Object columnValue = getColumnValue(row, columnName);
        if (columnValue != null) {
            columnValue = ReflectionUtil.convert(columnValue, columnType);
        }
        return columnValue;
    }
    
    public void setColumnValue(Object[] row, String columnName, Object value) {
        if (row == null || row.length == 0) {
            return;            
        }
        
        Integer columnIdx = columnMap.get(columnName.toUpperCase());
        if (columnIdx == null) {
            throw new RuntimeException("Invalid column name " + tableName + "." + columnName);
        }
        
        if (columnIdx < 0 || columnIdx >= row.length) {
            throw new RuntimeException("Invalid column index " + tableName + "." + columnIdx);
        }
        
        row[columnIdx] = value;
    }
    
    public int getColumnCount() {
        return columnMap.size();
    }
    
    public int getRowCount() {
        return rows.size();
    }
    
    public Object[] getRow(Object key) {
        if (keyColumnType != null) {
            key = ReflectionUtil.convert(key, keyColumnType);
        }        
        return rows.get(key);
    }

    public Collection<Object[]> getRows() {
        return Collections.unmodifiableCollection(rows.values());
    }
    
    public void addRow(Object[] row) {
        if (row == null || row.length == 0) {
            return;
        }
        
        Object idValue = row;
        if (keyColumnName != null && keyColumnType != null) {
            idValue = getColumnValue(row, keyColumnName, keyColumnType);       
        }

        if (idValue != null) {
            rows.put(idValue, row);
        }
    }
    
    public Collection<Object> getKeys() {
        return Collections.unmodifiableCollection(rows.keySet());
    }
    
    public Collection<Object> getKeys(String columnName, Object fk) {
        DbTableIndexMap indexMap = indices.get(columnName);
        if (indexMap == null) {
            return null;
        }
        
        return indexMap.getKeys(fk);
    }
       
    public void createIndex(String columnName, String columnType) {
        DbTableIndexMap indexMap = DbTableIndexMap.createDbTableIndexMap(this, columnName, columnType);
        indices.put(columnName, indexMap);       // overwrite the earlier index                
    }
    
    public void createIndexIfAbsent(String columnName, String columnType) {
        if (indices.containsKey(columnName)) {
            return;
        }

        createIndex(columnName, columnType);
    }
    
    public void deleteIndex(String columnName) {
        DbTableIndexMap index = indices.get(columnName);
        if (index != null) {
            index.clear();
            indices.remove(columnName);
        }        
    }
    
    public void clear() {
        columnMap.clear();
        rows.clear();
        for (DbTableIndexMap index : indices.values()) {
            index.clear();
        }
        indices.clear();
    }
            
    public Set<Map.Entry<Object, Object[]>> getRowSet() {
        return Collections.unmodifiableSet(rows.entrySet());
    }
}
