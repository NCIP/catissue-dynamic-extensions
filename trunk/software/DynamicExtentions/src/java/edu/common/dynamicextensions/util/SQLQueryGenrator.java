package edu.common.dynamicextensions.util;

import java.util.List;
import java.util.Map;


/**
 * This class is to generate frequently used sql queries
 * @author pavan_kalantri
 * @author kunal_kamble
 *
 */
public class SQLQueryGenrator
{

	/**Single instance variable */
	private SQLQueryGenrator queryGenrator;
	
	/**
	 * private constructor to make class singleton
	 */
	private SQLQueryGenrator()
	{
		queryGenrator = new SQLQueryGenrator();
	}
	
	/**
	 * @return returns the single instance of the class every time invoked
	 */
	public SQLQueryGenrator getInstance()
	{
		if(queryGenrator == null)
		{
			queryGenrator = new SQLQueryGenrator();
			
		}
		return queryGenrator; 
	}
	
	/**
	 * @param tableName
	 * @param projection
	 * @param columnValueMap
	 * @return
	 */
	public String getSelectQuery(String tableName, List<String> projection, Map<String, Object>...columnValueMap)
	{
		return null;
		
	}
	
	/**
	 * @param tableName
	 * @param setColumnValueMap
	 * @param columnValueMap
	 * @return
	 */
	public String getUpdateQuery(String tableName, Map<String, Object> setColumnValueMap, Map<String, Object>...columnValueMap)
	{
		return null;
	}
	
	/**
	 * @param tableName
	 * @param columnValueMap
	 * @return
	 */
	public String getDeleteQuery(String tableName, Map<String, Object>...columnValueMap )
	{
	return null;
	}
	
	
}
