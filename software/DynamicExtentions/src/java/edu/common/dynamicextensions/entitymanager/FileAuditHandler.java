
package edu.common.dynamicextensions.entitymanager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import edu.common.dynamicextensions.domain.AbstractAttribute;
import edu.common.dynamicextensions.domain.FileAttribute;
import edu.common.dynamicextensions.domain.FileAttributeRecordValue;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.AuditException;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

/**
 * The Class FileAuditHandler.
 */
public final class FileAuditHandler implements DynamicExtensionsQueryBuilderConstantsInterface
{

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(FileAuditHandler.class);

	/** The file handler. */
	private static FileAuditHandler fileHandler;

	/**
	 * Instantiates a new file audit handler.
	 */
	private FileAuditHandler()
	{
		// SINGLETON CLASS
	}

	/**
	 * Gets the single instance of FileAuditHandler.
	 *
	 * @return single instance of FileAuditHandler
	 */
	public static synchronized FileAuditHandler getInstance()
	{
		if (fileHandler == null)
		{
			fileHandler = new FileAuditHandler();
		}
		return fileHandler;
	}

	/**
	 * Audit file.
	 *
	 * @param sessionDataBean the session data bean
	 * @param hibernateDAO the hibernate dao
	 * @param updatedObject the updated object
	 * @param eventType the event type
	 * @param dataValue the data value
	 */
	public void auditFile(SessionDataBean sessionDataBean, HibernateDAO hibernateDAO,
			Object updatedObject, final String eventType,
			Map<AbstractAttributeInterface, Object> dataValue)
	{
		try
		{
			Long identifier = EntityManager.getObjectId(updatedObject);
			FileAttribute currentFileObject = populateFileObject(dataValue, identifier);

			//retrieve old file object information.
			FileAttribute oldFileObject = populateOldFileObject(sessionDataBean, identifier,
					currentFileObject);

			// Audit File Object
			auditFileObject(oldFileObject, currentFileObject, hibernateDAO, sessionDataBean, eventType);
		}
		catch (Exception e)
		{
			LOGGER.info("Exception occured while auditing file attribute, root cause ::: "
					+ e.getCause());
			LOGGER.info("Message ::: " + e.getMessage());
		}
	}

	/**
	 * Audit file object.
	 *
	 * @param previousFAttribute the previous f attribute
	 * @param currentFAttribute the current f attribute
	 * @param hibernateDAO the hibernate dao
	 * @param sessionDataBean the session data bean
	 * @param eventType the event type
	 *
	 * @throws AuditException the audit exception
	 * @throws DAOException the DAO exception
	 */
	private void auditFileObject(FileAttribute previousFAttribute, FileAttribute currentFAttribute,
			HibernateDAO hibernateDAO, SessionDataBean sessionDataBean, String eventType)
			throws AuditException, DAOException
	{
		AuditManager auditManager = new AuditManager(sessionDataBean, hibernateDAO
				.getHibernateMetaData());
		auditManager.audit(currentFAttribute, previousFAttribute, eventType);
		hibernateDAO.insert(auditManager.getAuditEvent());
	}

	/**
	 * Gets the file attribute.
	 *
	 * @param dataValue the data value
	 * @param object the object
	 *
	 * @return the file attribute
	 */
	private FileAttribute populateFileObject(Map<AbstractAttributeInterface, Object> dataValue,
			Long recordId)
	{
		FileAttribute fileAttribute = new FileAttribute();
		if (dataValue != null)
		{
			for (Entry<AbstractAttributeInterface, Object> recordEntry : dataValue.entrySet())
			{
				AbstractAttribute attribute = (AbstractAttribute) recordEntry.getKey();
				Object value = recordEntry.getValue();
				if (attribute instanceof AttributeInterface
						&& value instanceof FileAttributeRecordValue)
				{
					populateFileAttribute(fileAttribute, attribute,
							(FileAttributeRecordValue) value, recordId);
				}
			}
		}
		return fileAttribute;
	}

	/**
	 * Populate file attribute.
	 *
	 * @param fileAttribute the file attribute
	 * @param attribute the attribute
	 * @param recordValue the record value
	 */
	private void populateFileAttribute(FileAttribute fileAttribute, AbstractAttribute attribute,
			FileAttributeRecordValue recordValue, Long recordId)
	{
		AttributeInterface attributeInterface = (AttributeInterface) attribute;
		String columnName = attributeInterface.getColumnProperties().getName();
		fileAttribute.setContentType(recordValue.getContentType());
		fileAttribute.setFileName(recordValue.getFileName());
		fileAttribute.setEntityId(attributeInterface.getEntity().getId());
		fileAttribute.setRecordId(recordId);
		fileAttribute.setTableName(attributeInterface.getEntity().getTableProperties().getName());
		fileAttribute.setAttributeColumnNameMap(getColumnMapping(columnName));
	}

	/**
	 * Gets the column mapping.
	 *
	 * @param columnName the column name
	 *
	 * @return the column mapping
	 */
	private Map<String, String> getColumnMapping(String columnName)
	{
		Map<String, String> columnInfoMap = new HashMap<String, String>();
		columnInfoMap.put(DEConstants.FILE_NAME, columnName + DEConstants.SUFFIX_FILENAME);
		columnInfoMap.put(DEConstants.CONTENT_TYPE, columnName + DEConstants.SUFFIX_CONTENT_TYPE);
		columnInfoMap.put(DEConstants.ENTITY_ID, DEConstants.ENTITY_ID_COL_NAME);
		columnInfoMap.put(DEConstants.RECORD_ID, DEConstants.IDENTIFIER_COL_NAME);
		return columnInfoMap;
	}

	/**
	 * Populate old file attribute object.
	 *
	 * @param sessionDataBean the session data bean
	 * @param identifier the identifier
	 * @param fileAttribute the file attribute
	 *
	 * @return the file attribute
	 *
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 * @throws DAOException the DAO exception
	 * @throws SQLException the SQL exception
	 */
	private FileAttribute populateOldFileObject(SessionDataBean sessionDataBean, Long identifier,
			FileAttribute fileAttribute) throws DynamicExtensionsSystemException, DAOException,
			SQLException
	{
		FileAttribute oldFileObject = new FileAttribute();
		JDBCDAO jdbcDao = null;
		ResultSet resultSet = null;
		try
		{
			Map<String, String> columnNameMapping = fileAttribute.getAttributeColumnNameMap();
			oldFileObject.setAttributeColumnNameMap(columnNameMapping);
			oldFileObject.setTableName(fileAttribute.getTableName());

			jdbcDao = DynamicExtensionsUtility.getJDBCDAO(sessionDataBean);
			resultSet = retrieveOldFileObject(identifier, fileAttribute.getTableName(), jdbcDao,
					columnNameMapping);
			while (resultSet.next())
			{
				oldFileObject.setFileName(resultSet.getObject(
						columnNameMapping.get(DEConstants.FILE_NAME)).toString());
				oldFileObject.setContentType(resultSet.getObject(
						columnNameMapping.get(DEConstants.CONTENT_TYPE)).toString());
				oldFileObject.setRecordId(identifier);
				oldFileObject.setEntityId(fileAttribute.getEntityId());
			}
		}
		finally
		{
			if (resultSet != null)
			{
				resultSet.close();
			}
			DynamicExtensionsUtility.closeDAO(jdbcDao);
		}
		return oldFileObject;
	}

	/**
	 * Retrieve old file object.
	 *
	 * @param identifier the identifier
	 * @param tableName the table name
	 * @param jdbcDao the jdbc dao
	 * @param columnNameMapping the column name mapping
	 *
	 * @return the result set
	 *
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 * @throws DAOException the DAO exception
	 */
	private ResultSet retrieveOldFileObject(Long identifier, String tableName, JDBCDAO jdbcDao,
			Map<String, String> columnNameMapping) throws DynamicExtensionsSystemException,
			DAOException
	{
		final String query = SELECT_KEYWORD + columnNameMapping.get(DEConstants.FILE_NAME) + COMMA
				+ columnNameMapping.get(DEConstants.CONTENT_TYPE) + FROM_KEYWORD + tableName
				+ WHERE_KEYWORD + IDENTIFIER + EQUAL + QUESTION_MARK;

		final LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean(IDENTIFIER, identifier));

		return jdbcDao.getResultSet(query, queryDataList, null);

	}

}
