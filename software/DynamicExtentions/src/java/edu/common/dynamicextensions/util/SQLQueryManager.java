
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
import edu.common.dynamicextensions.util.xml.XMLToObjectConverter;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

public class SQLQueryManager
{

	private static SQLQueryManager sqlQueryLoader;
	private static final Map<String, Query> sqlQueryMap = new HashMap<String, Query>();

	private SQLQueryManager() throws JAXBException, SAXException
	{
		URL xsdFileUrl = Thread.currentThread().getContextClassLoader().getResource("DESql.xsd");
//		URL xsdFileUrl = new URL("D:/Amol/workspace/DynamicExtension_1.7/software/DynamicExtentions/src/resources/jaxb/xsd/DESql.xsd");
		XMLToObjectConverter converter = new XMLToObjectConverter(SqlQuery.class.getPackage()
				.getName(), xsdFileUrl);
		SqlQuery sqlQuery = (SqlQuery)converter.getJavaObject(Thread.currentThread()
				.getContextClassLoader().getResourceAsStream("DESql.xml"));
		initializeSqlQueryMap(sqlQuery);
	}

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

	public static String getSQL(String key)
	{
		return sqlQueryMap.get(key).getValue();
	}

	public static List<?> executeQuery(String queryKey, List<ColumnValueBean> columnValueBeans,
			SessionDataBean sessionDataBean) throws DynamicExtensionsSystemException, JAXBException, SAXException, DAOException 
	{
		JDBCDAO jdbcDao = DynamicExtensionsUtility.getJDBCDAO(sessionDataBean);
		String query = SQLQueryManager.getInstance().getSQL(queryKey);
		return jdbcDao.executeQuery(query, columnValueBeans);
	}

}
