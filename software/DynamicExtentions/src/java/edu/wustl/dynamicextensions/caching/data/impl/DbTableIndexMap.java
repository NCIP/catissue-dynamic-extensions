package edu.wustl.dynamicextensions.caching.data.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Vinayak Pawar (vinayak.pawar@krishagni.com)
 */
public class DbTableIndexMap {
    private final Map<Object, Set<Object>> indexMap = new HashMap<Object, Set<Object>>();
       
    public static DbTableIndexMap createDbTableIndexMap(DbTableDataImpl dbTableData, String columnName, String columnType) {
        DbTableIndexMap tableIdx = new DbTableIndexMap();

        for (Map.Entry<Object, Object[]> row : dbTableData.getRowSet()) {            
            Object columnValue = dbTableData.getColumnValue(row.getValue(), columnName, columnType);
            if (columnValue == null) {
                continue;
            }
            tableIdx.addKey(columnValue, row.getKey());
        }
        
        return tableIdx;        
    }
       
    private void addKey(Object fk, Object key) {        
        Set<Object> keys = indexMap.get(fk);
        if (keys == null) {
            keys = new HashSet<Object>();            
            indexMap.put(fk, keys);
        }        
        keys.add(key);
    }
    
    public Set<Object> getKeys(Object fk) {        
        Set<Object> keys = indexMap.get(fk);
        if (keys == null) {
            return null;
        }
        return Collections.unmodifiableSet(keys);
    }  
    
    public void clear() {
        indexMap.clear();
    }
}