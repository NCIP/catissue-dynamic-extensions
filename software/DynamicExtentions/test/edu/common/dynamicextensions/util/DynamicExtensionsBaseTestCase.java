/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author Vishvesh Mulay
 *@version 1.0
 */

package edu.common.dynamicextensions.util;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import edu.common.dynamicextensions.domain.BooleanAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.StringAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.NumericTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerExceptionConstantsInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.global.Variables;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.common.dynamicextensions.util.parser.CategoryCSVConstants;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.query.generator.ColumnValueBean;

public class DynamicExtensionsBaseTestCase extends TestCase
		implements
			EntityManagerExceptionConstantsInterface
{

	static
	{
		System.setProperty("app.propertiesFile", System.getProperty("user.dir") + "/build.xml");
		LoggerConfig.configureLogger(System.getProperty("user.dir") + "/src/java/");
		try
		{
			ErrorKey.init("~");
		}
		catch (final IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected final static String XMI_FILE_PATH = "./src/resources/xmi/";
	protected final static String CSV_FILE_PATH = "./src/resources/csv/";
	protected final static String PV_FILE_PATH = "./src/resources/pvs/";
	protected final static String EDITED_XMI_FILE_PATH = "./src/resources/edited_xmi/";
	protected final static String JBOSS_PATH = "http://10.88.199.44:46210/dynamicExtensions";
	protected final static String TEST_ENTITYGROUP_NAME = "test";
	protected int noOfDefaultColumns = 2;

	//1:ACTIVITY_STATUS 2:IDENTIFIER 3:FILE NAME 4:CONTENTE_TYPE 5:ACTUAL_CONTENTS
	protected int noOfDefaultColumnsForfile = 5;

	protected final static String STRING_TYPE = "string";
	protected final static String INT_TYPE = "int";
	protected final String APPLICATIONURL = "http://10.88.199.44:28080/dynamicExtensions";

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
	public DynamicExtensionsBaseTestCase(final String arg0)
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
	protected Object executeQuery(final String query, final String returnType,
			final int columnNumber, final LinkedList<ColumnValueBean> queryDataList)
	{
		ResultSet resultSet = null;
		Object ans = null;
		JDBCDAO jdbcDao = null;
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			resultSet = jdbcDao.getResultSet(query, queryDataList, null);
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
		catch (final Exception e)
		{
			e.printStackTrace();
			fail();
		}
		finally
		{
			try
			{
				DynamicExtensionsUtility.closeDAO(jdbcDao);
			}
			catch (final Exception e)
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
	protected int getColumnCount(final String query)
	{
		ResultSetMetaData metadata = null;
		int count = 0;
		ResultSet result = null;
		JDBCDAO jdbcDao = null;
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			result = jdbcDao.getResultSet(query, null, null);
			metadata = result.getMetaData();
			count = metadata.getColumnCount();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			fail();
		}
		finally
		{
			try
			{
				jdbcDao.closeStatement(result);
				DynamicExtensionsUtility.closeDAO(jdbcDao);
			}
			catch (final Exception e)
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
	protected int getColumntype(final String query, final int columnNumber)
	{
		ResultSetMetaData metadata = null;
		ResultSet result = null;
		int type = 0;
		JDBCDAO jdbcDao = null;
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			result = jdbcDao.getResultSet(query, null, null);
			metadata = result.getMetaData();
			type = metadata.getColumnType(columnNumber);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			fail();
		}
		finally
		{
			try
			{
				jdbcDao.closeStatement(result);
				DynamicExtensionsUtility.closeDAO(jdbcDao);
			}
			catch (final Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e.getCause());
			}
		}

		return type;
	}

	/**
	* @param query query to be executed
	* @return  ResultSetMetaData
	* @throws DynamicExtensionsSystemException
	*/
	protected ResultSetMetaData executeQueryDDL(final String query)
			throws DynamicExtensionsSystemException
	{
		//      Checking whether the data table is created properly or not.
		JDBCDAO jdbcDao = null;
		java.sql.PreparedStatement statement = null;
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			statement = jdbcDao.getPreparedStatement(query);
			statement.execute();
			jdbcDao.commit();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			fail();
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(jdbcDao);
		}

		return null;
	}

	/**
	 * @param tableName
	 * @return
	 */
	protected boolean isTablePresent(final String tableName)
	{
		final String query = "select * from " + tableName;
		JDBCDAO jdbcDao = null;

		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			jdbcDao.getResultSet(query, null, null);

		}
		catch (final Exception e)
		{
			return false;
		}
		finally
		{
			try
			{
				DynamicExtensionsUtility.closeDAO(jdbcDao);
			}
			catch (final Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e.getCause());
			}
		}
		return true;
	}

	protected String getActivityStatus(final EntityInterface entity, final Long recordId)
			throws Exception
	{
		final StringBuffer query = new StringBuffer();
		query.append("select " + Constants.ACTIVITY_STATUS_COLUMN + " from"
				+ entity.getTableProperties().getName() + " where identifier = ?");
		final LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean(Constants.IDENTIFIER, recordId));
		return (String) executeQuery(query.toString(), STRING_TYPE, 1, queryDataList);

	}

	/**
	 * @param associationType associationType
	 * @param name name
	 * @param minCard  minCard
	 * @param maxCard maxCard
	 * @return  RoleInterface
	 */
	protected RoleInterface getRole(final AssociationType associationType, final String name,
			final Cardinality minCard, final Cardinality maxCard)
	{
		final RoleInterface role = DomainObjectFactory.getInstance().createRole();
		role.setAssociationsType(associationType);
		role.setName(name);
		role.setMinimumCardinality(minCard);
		role.setMaximumCardinality(maxCard);
		return role;
	}

	/**
	 * @param xmi
	 * @param mainContainerList
	 * @param packageName
	 */
	protected void importModel(final String xmi, final String mainContainerList,
			final String packageName)
	{
		final String[] args1 = {xmi, mainContainerList, packageName, " "};
		XMIImporter.main(args1);

	}

	/**
	 * This method will create the Data Value map for the Given Category Entity .
	 * @param rootCatEntity
	 * @return
	 */
	public Map<BaseAbstractAttributeInterface, Object> createDataValueMapForCategory(
			CategoryEntityInterface rootCatEntity)
	{
		Map<BaseAbstractAttributeInterface, Object> dataValue = new HashMap<BaseAbstractAttributeInterface, Object>();
		for (CategoryAttributeInterface catAtt : rootCatEntity.getAllCategoryAttributes())
		{
			// put the different value for diff attribute type
			if (catAtt.getAbstractAttribute() instanceof AttributeInterface)
			{
				AttributeInterface attribute = (AttributeInterface) catAtt.getAbstractAttribute();
				if (attribute.getAttributeTypeInformation() instanceof DateAttributeTypeInformation)
				{
					String format = ((DateAttributeTypeInformation) attribute
							.getAttributeTypeInformation()).getFormat();
					String value = "";
					if (format.equals(ProcessorConstants.DATE_FORMAT_OPTION_DATEANDTIME))
					{
						value = "11" + ProcessorConstants.DATE_SEPARATOR + "20"
								+ ProcessorConstants.DATE_SEPARATOR + "1998 10:50";
					}
					else if (format.equals(ProcessorConstants.DATE_FORMAT_OPTION_DATEONLY))
					{
						value = "11" + ProcessorConstants.DATE_SEPARATOR + "20"
								+ ProcessorConstants.DATE_SEPARATOR + "1998";
					}
					else if (format.equals(ProcessorConstants.DATE_FORMAT_OPTION_MONTHANDYEAR))
					{
						value = "11" + ProcessorConstants.DATE_SEPARATOR + "1998";
					}
					else if (format.equals(ProcessorConstants.DATE_FORMAT_OPTION_YEARONLY))
					{
						value = "1998";
					}
					dataValue.put(catAtt, value);
				}
				else if (attribute.getAttributeTypeInformation() instanceof NumericTypeInformationInterface)
				{
					dataValue.put(catAtt, "10");
				}
				else if (attribute.getAttributeTypeInformation() instanceof BooleanAttributeTypeInformation)
				{
					dataValue.put(catAtt, "true");
				}
				else if (attribute.getAttributeTypeInformation() instanceof StringAttributeTypeInformation)
				{
					dataValue.put(catAtt, "test String");
				}
				else
				{
					dataValue.put(catAtt, "test String for other data type");
				}
			}
		}
		for (CategoryAssociationInterface catAssociation : rootCatEntity
				.getCategoryAssociationCollection())
		{
			List dataList = new ArrayList();
			CategoryEntityInterface targetCaEntity = catAssociation.getTargetCategoryEntity();
			dataList.add(createDataValueMapForCategory(targetCaEntity));
			if (targetCaEntity.getNumberOfEntries().equals(CategoryCSVConstants.MULTILINE))
			{
				dataList.add(createDataValueMapForCategory(targetCaEntity));
			}
			dataValue.put(catAssociation, dataList);
		}
		return dataValue;
	}

	/**
	 * This method will create the Data Value map for the Given Category Entity .
	 * @param rootEntity
	 * @return
	 */
	public Map<BaseAbstractAttributeInterface, Object> createDataValueMapForEntity(
			EntityInterface rootEntity)
	{
		Map<BaseAbstractAttributeInterface, Object> dataValue = new HashMap<BaseAbstractAttributeInterface, Object>();
		for (AbstractAttributeInterface abstractAttribute : rootEntity.getAttributeCollection())
		{
			// put the different value for diff attribute type
			if (abstractAttribute instanceof AttributeInterface)
			{
				AttributeInterface attribute = (AttributeInterface) abstractAttribute;
				if (attribute.getAttributeTypeInformation() instanceof DateAttributeTypeInformation)
				{
					String format = ((DateAttributeTypeInformation) attribute
							.getAttributeTypeInformation()).getFormat();
					String value = "";
					if (format.equals(ProcessorConstants.DATE_FORMAT_OPTION_DATEANDTIME))
					{
						value = "01" + ProcessorConstants.DATE_SEPARATOR + "20"
								+ ProcessorConstants.DATE_SEPARATOR + "2009 10:50";
					}
					else if (format.equals(ProcessorConstants.DATE_FORMAT_OPTION_DATEONLY))
					{
						value = "01" + ProcessorConstants.DATE_SEPARATOR + "20"
								+ ProcessorConstants.DATE_SEPARATOR + "2009";
					}
					else if (format.equals(ProcessorConstants.DATE_FORMAT_OPTION_MONTHANDYEAR))
					{
						value = "02" + ProcessorConstants.DATE_SEPARATOR + "2009";
					}
					else if (format.equals(ProcessorConstants.DATE_FORMAT_OPTION_YEARONLY))
					{
						value = "2009";
					}
					dataValue.put(abstractAttribute, value);
				}
				else if (attribute.getAttributeTypeInformation() instanceof NumericTypeInformationInterface)
				{
					dataValue.put(abstractAttribute, "10");
				}
				else if (attribute.getAttributeTypeInformation() instanceof BooleanAttributeTypeInformation)
				{
					dataValue.put(abstractAttribute, "true");
				}
				else if (attribute.getAttributeTypeInformation() instanceof StringAttributeTypeInformation)
				{
					dataValue.put(abstractAttribute, "test String");
				}
				else
				{
					dataValue.put(abstractAttribute, "test String for other data type");
				}
			}
		}
		for (AssociationInterface association : rootEntity.getAssociationCollection())
		{
			List dataList = new ArrayList();
			EntityInterface targetCaEntity = association.getTargetEntity();
			dataList.add(createDataValueMapForEntity(targetCaEntity));
			if (association.getTargetRole().getMaximumCardinality().equals(
					DEConstants.Cardinality.MANY.getValue()))
			{
				dataList.add(createDataValueMapForEntity(targetCaEntity));
			}
			dataValue.put(association, dataList);
		}
		return dataValue;
	}
}
