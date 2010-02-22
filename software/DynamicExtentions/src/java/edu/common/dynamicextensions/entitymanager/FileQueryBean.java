
package edu.common.dynamicextensions.entitymanager;

import java.util.List;

import edu.wustl.dao.query.generator.ColumnValueBean;

/**
 * This class is acting as a utility object for creating the queries which are used to insert the
 * file attributes in the database.
 * @author pavan_kalantri
 *
 */
public class FileQueryBean
{

	private String query;
	private List<ColumnValueBean> colValBeanList;

	/**
	 * Returns the query string
	 * @return query
	 */
	public String getQuery()
	{
		return query;
	}

	/**
	 * sets the query.
	 * @param query query string.
	 */
	public void setQuery(String query)
	{
		this.query = query;
	}

	/**
	 * Returns the column value bean list for that query object
	 * @return columnValueBeanList
	 */
	public List<ColumnValueBean> getColValBeanList()
	{
		return colValBeanList;
	}

	/**
	 * Sets the columnValue bean list.
	 * @param colValBean column value bean list used for executing the query.
	 */
	public void setColValBeanList(List<ColumnValueBean> colValBean)
	{
		colValBeanList = colValBean;
	}

}
