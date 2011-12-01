package edu.wustl.dynamicextensions.caching.data.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import edu.wustl.dynamicextensions.caching.data.DbTableData;
import edu.wustl.dynamicextensions.caching.data.DbTableMap;

/**
 *
 * @author Vinayak Pawar (vinayak.pawar@krishagni.com)
 */
public class DbTableMapImpl implements DbTableMap {
    private ConcurrentMap<String, DbTableData> dbTablesMap = new ConcurrentHashMap<String, DbTableData>();
    
    public static DbTableMap createDbTableMap() {
        return new DbTableMapImpl();
    }
    
    public DbTableData getDbTableData(String tableName) {
        return dbTablesMap.get(tableName.trim().toUpperCase());
    }
    
    public void addDbTableData(DbTableData dbTableData) {
        dbTablesMap.put(dbTableData.getTableName().trim().toUpperCase(), dbTableData);
    }
    
    public int getTableCount() {
        return dbTablesMap.size();
    }
    
    public String[] getTableNames() {
        return dbTablesMap.keySet().toArray(new String[0]);
    }    
    
    public void clear() {
        dbTablesMap.clear();
    }
}