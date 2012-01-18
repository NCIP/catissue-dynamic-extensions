
package edu.common.dynamicextensions.util;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.jaxb.sql.Query;
import edu.common.dynamicextensions.jaxb.sql.SqlQuery;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.xml.XMLToObjectConverter;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

/**
 * This class contains method for query execution.Gets query from xml file. 
 * @author amol pujari
 *
 */

public class SQLQueryManager
{

	private static SQLQueryManager sqlQueryLoader;
	private static final Map<String, Query> sqlQueryMap = new HashMap<String, Query>();

	private SQLQueryManager() throws JAXBException, SAXException
	{
		URL xsdFileUrl = Thread.currentThread().getContextClassLoader().getResource(DEConstants.DESQL_XSD_FILENAME);
		XMLToObjectConverter converter = new XMLToObjectConverter(SqlQuery.class.getPackage()
				.getName(), xsdFileUrl);
		SqlQuery sqlQuery = (SqlQuery)converter.getJavaObject(Thread.currentThread()
				.getContextClassLoader().getResourceAsStream(DEConstants.DESQL_XML_FILENAME));
		initializeSqlQueryMap(sqlQuery);
	}

	/**
	 * @param sqlQuery
	 */
	private static void initializeSqlQueryMap(SqlQuery sqlQuery)
	{
		for (Query query : sqlQuery.getQuery())
		{
			sqlQueryMap.put(query.getName(), query);
		}
	}

	public static SQLQueryManager getInstance() throws JAXBException, SAXException
	{
		if (sqlQueryLoader == null)
		{
			sqlQueryLoader = new SQLQueryManager();
		}
		return sqlQueryLoader;
	}

	/**
	 * Gets query from the xml depending on the key provided
	 * @param key
	 * @return
	 */
	public String getSQL(String key)
	{
		return sqlQueryMap.get(key).getValue();
	}

	/**
	 * 
	 * Replaces values in the query and execute it
	 * @param queryKey
	 * @param columnValueBeans
	 * @param sessionDataBean
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws DAOException
	 */
	public static List<?> executeQuery(String queryKey, List<ColumnValueBean> columnValueBeans,
			SessionDataBean sessionDataBean) throws DynamicExtensionsSystemException, JAXBException, SAXException, DAOException 
	{
		JDBCDAO jdbcDao = DynamicExtensionsUtility.getJDBCDAO(sessionDataBean);
		String query = SQLQueryManager.getInstance().getSQL(queryKey);
		return jdbcDao.executeQuery(query, columnValueBeans);
	}

}
