
package edu.common.dynamicextensions.entitymanager;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Hibernate;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

public class FileUploadManager implements DynamicExtensionsQueryBuilderConstantsInterface
{

	private final static String FILETABLE = "DYEXTN_FILE";

	private static final Logger LOGGER = Logger.getCommonLogger(FileUploadManager.class);

	public long uploadFile(byte[] fileContent) throws DynamicExtensionsSystemException
	{
		long identifier = 0;
		try
		{
			JDBCDAO jdbcDao = DynamicExtensionsUtility.getJDBCDAO();

			StringBuffer insertQuery = new StringBuffer(INSERT_INTO_KEYWORD);
			insertQuery.append(FILETABLE);
			insertQuery.append(" (Identifier,file_record) values (?,?)");
			identifier = getMaxIdentifierValue(jdbcDao);
			identifier += 1;
			final LinkedList<ColumnValueBean> colValBeanList = (LinkedList<ColumnValueBean>) createColumnValueBeanListForDataEntry(
					fileContent, identifier);
			final List<LinkedList<ColumnValueBean>> colValueBean = new ArrayList<LinkedList<ColumnValueBean>>();
			colValueBean.add(colValBeanList);

			jdbcDao.executeUpdate(insertQuery.toString(), colValueBean);
			jdbcDao.commit();
			DynamicExtensionsUtility.closeDAO(jdbcDao);
		}
		catch (DAOException e)
		{
			LOGGER.info("Error occured while connecting to database.", e);
			throw new DynamicExtensionsSystemException(e.getMessage());
		}
		catch (SQLException e)
		{
			LOGGER.info("Error occured while fetching results from database.", e);
			throw new DynamicExtensionsSystemException(e.getMessage());
		}
		return identifier;
	}

	private long getMaxIdentifierValue(JDBCDAO jdbcDao) throws DAOException, SQLException
	{
		final LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();

		ResultSet resultSet = null;
		Object value = null;
		StringBuffer query = new StringBuffer(SELECT_KEYWORD);
		query.append(" max(IDENTIFIER) as ID from ");
		query.append(FILETABLE);
		try
		{
			resultSet = jdbcDao.getResultSet(query.toString(), queryDataList, null);

			while (resultSet.next())
			{
				value = resultSet.getObject("ID");
				if (value == null)
				{
					value = 0;
				}
			}
		}
		finally
		{
			if (resultSet != null)
			{
				resultSet.close();
			}
		}

		return Long.valueOf(value.toString());
	}

	private AbstractList<ColumnValueBean> createColumnValueBeanListForDataEntry(byte[] fileContent,
			long identifier) throws SQLException
	{

		Blob blobFile = Hibernate.createBlob(fileContent);
		InputStream inputStream = blobFile.getBinaryStream();
		final ColumnValueBean bean1 = new ColumnValueBean(IDENTIFIER, identifier);
		final ColumnValueBean bean2 = new ColumnValueBean("FILE_RECORD", inputStream);
		final LinkedList<ColumnValueBean> colValBeanList = new LinkedList<ColumnValueBean>();
		colValBeanList.add(bean1);
		colValBeanList.add(bean2);
		return colValBeanList;
	}

	public void deleteRecord(long identifier)
			throws DynamicExtensionsSystemException
	{
		JDBCDAO jdbcDao = DynamicExtensionsUtility.getJDBCDAO();

		StringBuffer deleteQuery = new StringBuffer(DELETE_KEYWORD);
		deleteQuery.append(FILETABLE);
		deleteQuery.append(WHERE_KEYWORD);
		deleteQuery.append(IDENTIFIER);
		deleteQuery.append(EQUAL);
		deleteQuery.append(QUESTION_MARK);

		final ColumnValueBean identifierValueBean = new ColumnValueBean(IDENTIFIER, identifier);
		final LinkedList<ColumnValueBean> colValBeanList = new LinkedList<ColumnValueBean>();
		colValBeanList.add(identifierValueBean);

		DynamicExtensionsUtility.executeUpdateQuery(deleteQuery.toString(), null, jdbcDao,
				colValBeanList);
		DynamicExtensionsUtility.closeDAO(jdbcDao);
	}

	/**
	 * @param identifier
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws SQLException
	 * @throws DAOException
	 */
	public byte[] getFilefromDatabase(long identifier) throws DynamicExtensionsSystemException,
			SQLException, DAOException
	{
		ResultSet resultSet = null;
		Blob fileValue = null;
		byte[] fileStream = null;
		final LinkedList<ColumnValueBean> colValBeanList = new LinkedList<ColumnValueBean>();
		JDBCDAO jdbcDao = DynamicExtensionsUtility.getJDBCDAO();

		StringBuffer fetchQuery = new StringBuffer(SELECT_KEYWORD);
		fetchQuery.append("FILE_RECORD");
		fetchQuery.append(FROM_KEYWORD);
		fetchQuery.append(FILETABLE);
		fetchQuery.append(WHERE_KEYWORD);
		fetchQuery.append(IDENTIFIER);
		fetchQuery.append(EQUAL);
		fetchQuery.append(QUESTION_MARK);

		final ColumnValueBean identifierValueBean = new ColumnValueBean(identifier);
		colValBeanList.add(identifierValueBean);

		try
		{
			resultSet = jdbcDao.getResultSet(fetchQuery.toString(), colValBeanList, null);
			while (resultSet.next())
			{
				fileValue = resultSet.getBlob("FILE_RECORD");
				if (fileValue != null)
				{
					int length = (int) fileValue.length();
					fileStream = fileValue.getBytes(1L, length);
				}
			}

		}
		catch (DAOException e)
		{
			LOGGER.info("Error occured while retrieving file database.", e);
			throw new DynamicExtensionsSystemException(e.getMessage());
		}
		finally
		{
			jdbcDao.closeStatement(resultSet);
			resultSet.close();
			DynamicExtensionsUtility.closeDAO(jdbcDao);
		}

		return fileStream;
	}
}
