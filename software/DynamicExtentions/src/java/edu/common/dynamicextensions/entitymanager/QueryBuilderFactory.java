
package edu.common.dynamicextensions.entitymanager;

import edu.common.dynamicextensions.dao.impl.DynamicExtensionDAO;
import edu.common.dynamicextensions.dao.impl.DynamicExtensionDBFactory;
import edu.wustl.dao.daofactory.DAOConfigFactory;

/**
 * This class returns the queryBuilder depending on the database
 * @author Rahul Ner
 *
 */
public class QueryBuilderFactory
{

	/**
	 * Instance of database specific query builder.
	 */
	private static DynamicExtensionBaseQueryBuilder queryBuilder = null;

	/**
	 * This method returns the query builder depending on the database
	 * @return
	 */
	public static synchronized DynamicExtensionBaseQueryBuilder getQueryBuilder()
	{
		if (queryBuilder == null)
		{
			String appName = DynamicExtensionDAO.getInstance().getAppName();
			String dbType = DAOConfigFactory.getInstance().getDAOFactory(appName).getDataBaseType();
			queryBuilder = DynamicExtensionDBFactory.getInstance().getQueryBuilder(dbType);
		}
		return queryBuilder;
	}

}
