/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author Vishvesh Mulay
 *@version 1.0
 */

package edu.common.dynamicextensions.util;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import junit.framework.TestCase;
import edu.common.dynamicextensions.dao.impl.DynamicExtensionDAO;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerExceptionConstantsInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.util.global.Variables;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;

public class DynamicExtensionsBaseTestCase extends TestCase
		implements
			EntityManagerExceptionConstantsInterface
{

	static
	{
		System.setProperty("app.propertiesFile", System.getProperty("user.dir") + "/build.xml");
		LoggerConfig.configureLogger(System.getProperty("user.dir") + "/src/");
		try
		{
			ErrorKey.init("~");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected final static String XMI_FILE_PATH="./src/resources/xmi/";
	protected final static String CSV_FILE_PATH="./src/resources/csv/";
	protected final static String PV_FILE_PATH="./src/resources/pvs/";
	protected final static String EDITED_XMI_FILE_PATH="./src/resources/edited_xmi/";
	protected int noOfDefaultColumns = 2;

	//1:ACTIVITY_STATUS 2:IDENTIFIER 3:FILE NAME 4:CONTENTE_TYPE 5:ACTUAL_CONTENTS
	protected int noOfDefaultColumnsForfile = 5;

	protected final static String STRING_TYPE = "string";
	protected final static String INT_TYPE = "int";

	JDBCDAO dao;

	/**
	 *
	 */
	public DynamicExtensionsBaseTestCase()
	{
		super();

	}

	/**
	 * @param arg0 name
	 */
	public DynamicExtensionsBaseTestCase(String arg0)
	{
		super(arg0);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp()
	{

		Logger.out = org.apache.log4j.Logger.getLogger("dynamicExtensions.logger");
		ApplicationProperties.initBundle("ApplicationResources");
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown()
	{
		Variables.containerFlag = true;
	}

	/**
	 * It will execute query passed as parameter & will return value of the
	 * Column at columnNumber of returnType.
	 * @param query Query to be executed
	 * @param returnType Returntype or DataType of the column
	 * @param columnNumber of which value is to be retrieved
	 * @return Object of the value
	 */
	protected Object executeQuery(String query, String returnType, int columnNumber)
	{
		ResultSet resultSet = null;
		Object ans = null;
		JDBCDAO jdbcDao = getJDBCDAO();
		try
		{
			resultSet = jdbcDao.getQueryResultSet(query);
			resultSet.next();
			if (STRING_TYPE.equals(returnType))
			{
				ans = resultSet.getString(columnNumber);
			}
			if (INT_TYPE.equals(returnType))
			{
				ans = resultSet.getInt(columnNumber);
			}
			resultSet.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
		finally
		{
			try
			{
				closeJDBCDAO(jdbcDao);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e.getCause());
			}
		}
		return ans;
	}

	/**
	 *
	 * @return
	 */
	public EntityInterface createAndPopulateEntity()
	{
		final DomainObjectFactory factory = DomainObjectFactory.getInstance();
		final EntityInterface entity = factory.createEntity();
		EntityManagerUtil.addIdAttribute(entity);
		return entity;
	}

	/**
	 * @param query query to be executed
	 * @return
	 */
	/*	protected ResultSet executeQuery(String query)
		{
			//      Checking whether the data table is created properly or not.
			Connection conn = null;
			java.sql.PreparedStatement statement = null;
			java.sql.ResultSet resultSet=null;
			try
			{
				conn = DBUtil.getConnection();
			}
			catch (HibernateException e)
			{
				e.printStackTrace();
			}

			try
			{
				statement = conn.prepareStatement(query);
				resultSet= statement.executeQuery();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				fail();
			}
			/*finally
			{
				if(conn!=null)
				{
					DBUtil.closeConnection();
				}
			}
			return resultSet;
		}*/

	/**
	 *  It will execute query & will retrieve the total columncount in that queried table.
	 * @param query to be executed for metadata
	 * @return number of columns
	 */
	protected int getColumnCount(String query)
	{
		ResultSetMetaData metadata = null;
		int count = 0;
		JDBCDAO jdbcDao = getJDBCDAO();
		PreparedStatement statement = null;
		try
		{
			statement = jdbcDao.getPreparedStatement(query);
			metadata = statement.executeQuery().getMetaData();
			count = metadata.getColumnCount();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
		finally
		{
			try
			{
				statement.close();
				closeJDBCDAO(jdbcDao);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e.getCause());
			}
		}

		return count;
	}

	/**
	 * It will retrieve the DataType of the column specified in the columnNumber
	 * @param query To be executed
	 * @param columnNumber Of which dataType is required
	 * @return Data type of the column
	 */
	protected int getColumntype(String query, int columnNumber)
	{
		ResultSetMetaData metadata = null;
		int type = 0;
		JDBCDAO jdbcDao = null;
		PreparedStatement statement = null;
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			statement = jdbcDao.getPreparedStatement(query);
			metadata = statement.executeQuery().getMetaData();
			type = metadata.getColumnType(columnNumber);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
		finally
		{
			try
			{
				statement.close();
				DynamicExtensionsUtility.closeJDBCDAO(jdbcDao);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e.getCause());
			}
		}

		return type;
	}

	/**
	 * Close the connection
	 * @param conn
	 */
	private void closeJDBCDAO(JDBCDAO jdbcDao)
	{
		try
		{
			jdbcDao.closeSession();
		}
		catch (DAOException e)
		{
			throw new RuntimeException(e.getCause());
		}
	}

	/**
	 * It will execute actual query passed.
	 * @param conn connection to be used
	 * @param query to be executed
	 * @param statement
	 * @param metadata Object to be used for metadata
	 * @return
	 * @throws SQLException
	 */
	private ResultSetMetaData executeQueryForMetadata(JDBCDAO jdbcDao, String query,
			ResultSetMetaData metadata) throws DAOException, SQLException
	{
		PreparedStatement statement = jdbcDao.getPreparedStatement(query);
		metadata = statement.executeQuery().getMetaData();
		return metadata;
	}

	/**
	 * Open the connection for use
	 * @return connection
	 */
	private JDBCDAO getJDBCDAO()
	{
		JDBCDAO jdbcDao = null;
		try
		{
			String appName = DynamicExtensionDAO.getInstance().getAppName();
			jdbcDao = DAOConfigFactory.getInstance().getDAOFactory(appName).getJDBCDAO();
			jdbcDao.openSession(null);
		}
		catch (DAOException e)
		{
			e.printStackTrace();
		}
		return jdbcDao;
	}

	/**
	 * @param query query to be executed
	 * @return  ResultSetMetaData
	 */
	protected ResultSetMetaData executeQueryDDL(String query)
	{
		//      Checking whether the data table is created properly or not.
		JDBCDAO jdbcDao = getJDBCDAO();
		java.sql.PreparedStatement statement = null;
		try
		{
			statement = jdbcDao.getPreparedStatement(query);
			statement.execute();
			jdbcDao.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
		finally
		{
			closeJDBCDAO(jdbcDao);
		}

		return null;
	}

	/**
	 * @param tableName
	 * @return
	 */
	protected boolean isTablePresent(String tableName)
	{
		String query = "select * from " + tableName;
		JDBCDAO jdbcDao = getJDBCDAO();
		java.sql.PreparedStatement statement = null;
		try
		{
			statement = jdbcDao.getPreparedStatement(query);
			statement.executeQuery();
		}
		catch (Exception e)
		{
			return false;
		}
		finally
		{
			try
			{
				statement.close();
				jdbcDao.closeSession();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e.getCause());
			}
		}
		return true;
	}

	protected String getActivityStatus(EntityInterface entity, Long recordId) throws Exception
	{
		return (String) executeQuery("select " + Constants.ACTIVITY_STATUS_COLUMN + " from "
				+ entity.getTableProperties().getName() + " where identifier = " + recordId,
				STRING_TYPE, 1);

	}

	/**
	 * @param associationType associationType
	 * @param name name
	 * @param minCard  minCard
	 * @param maxCard maxCard
	 * @return  RoleInterface
	 */
	protected RoleInterface getRole(AssociationType associationType, String name,
			Cardinality minCard, Cardinality maxCard)
	{
		RoleInterface role = DomainObjectFactory.getInstance().createRole();
		role.setAssociationsType(associationType);
		role.setName(name);
		role.setMinimumCardinality(minCard);
		role.setMaximumCardinality(maxCard);
		return role;
	}

	/**
	 * @param categoryFilePath
	 */
	protected void createCaegory(String categoryFilePath)
	{
		String[] args2 = {categoryFilePath};
		CategoryCreator categoryCreator = new CategoryCreator();
		categoryCreator.main(args2);

	}

	/**
	 * @param xmi
	 * @param mainContainerList
	 * @param packageName
	 */
	protected void importModel(String xmi, String mainContainerList, String packageName)
	{
		String[] args1 = {xmi,mainContainerList,packageName," "};
		XMIImporter.main(args1);

	}
}
