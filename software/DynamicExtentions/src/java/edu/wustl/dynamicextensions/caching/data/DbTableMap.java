package edu.wustl.dynamicextensions.caching.data;

/**
 *
 * @author Vinayak Pawar (vinayak.pawar@krishagni.com)
 */
public interface DbTableMap {
    public DbTableData getDbTableData(String tableName);
    
    public void addDbTableData(DbTableData dbTableData);
    
    public int getTableCount();
    
    public String[] getTableNames();
    
    public void clear();
}
