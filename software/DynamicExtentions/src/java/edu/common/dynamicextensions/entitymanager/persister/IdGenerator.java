/**
 * 
 */
package edu.common.dynamicextensions.entitymanager.persister;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.transaction.Transaction;

import org.hibernate.Session;

import edu.common.dynamicextensions.dao.impl.DynamicExtensionDAO;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.connectionmanager.IConnectionManager;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.util.DAOUtility;

/**
 * @author Vinayak Pawar
 *
 */
public class IdGenerator {
	private class IdRange {
		private Long nextId;
		
		private Long maxId;
		
		public IdRange(Long nextId, Long maxId) {
			this.nextId = nextId;
			this.maxId = maxId;
		}
	}
	
	private Map<String, IdRange> identifiers = new HashMap<String, IdRange>();
		
	private Long rangeSize = 25L;
	
	private static final IdGenerator instance = new IdGenerator();
	
	private static final Logger LOGGER = Logger.getCommonLogger(IdGenerator.class);
	
	private static final String SELECT_LAST_ID = "SELECT LAST_ID FROM DYEXTN_ID_SEQ WHERE TABLE_NAME = '%s' FOR UPDATE";
	
	private static final String UPDATE_LAST_ID = "UPDATE DYEXTN_ID_SEQ SET LAST_ID = %d WHERE TABLE_NAME = '%s'";
	
	private static final String INSERT_LAST_ID = "INSERT INTO DYEXTN_ID_SEQ VALUES('%s', %d)";
	
	private static final String SELECT_MAX_ID = "SELECT MAX(IDENTIFIER) FROM %s";
		
	private IdGenerator() {		
	}
	
	public static IdGenerator getInstance() {
		return instance;
	}
	
	public synchronized Long getNextId(String tableName) {
		Transaction txn = null;
		
		try {
			IdRange idRange = identifiers.get(tableName);			
			if (idRange == null || idRange.nextId > idRange.maxId) {
				txn = suspendTxn();
				idRange = getIdSliceFromDb(tableName);
								
				if (idRange == null) {
					throw new RuntimeException("Couldn't obtain ID slice DB for table " + tableName);
				}
				identifiers.put(tableName, idRange);
			}
			
			Long id = idRange.nextId;
			idRange.nextId++;
			return id;			
		} catch (Exception e) {
			LOGGER.error("Error obtaining next ID for " + tableName, e);
			throw new RuntimeException("Error obtaining next ID for " + tableName, e);
		} finally {
			if (txn != null) {
				resumeTxn(txn);
			}
		}
	}
	
	private IdRange getIdSliceFromDb(String tableName) {
		Session session = null;
		Connection connection = null;
				
		try {
			session = getSession();
			connection = session.connection();
			connection.setAutoCommit(false);
			
			IdRange idRange = getIdSliceFromDb0(tableName, connection);
			if (idRange == null) {
				idRange = getFreshIdSliceFromDb(tableName, connection);
			}			
			
			connection.commit();			
			return idRange;
		} catch (Exception e) {
			LOGGER.error("Error obtaining ID slice from DB", e);
			throw new RuntimeException("Error obtaining ID slice from DB", e); 
		} finally {
			closeConnection(connection);
			closeSession(session);
		}
	}

	private IdRange getIdSliceFromDb0(String tableName, Connection connection) {
		Long lastId = getLastId(connection, tableName);
		if (lastId == null) {
			return null;
		}

		updateLastId(connection, tableName, lastId + rangeSize);
		return new IdRange(lastId + 1, lastId + rangeSize);
	}

	private IdRange getFreshIdSliceFromDb(String tableName, Connection connection) {
		Long maxId = getMaxId(connection, tableName);
		if (maxId == null) {
			maxId = 0L;
		}
		
		insertLastId(connection, tableName, maxId);		
		return getIdSliceFromDb0(tableName, connection);
	}
			
	private Transaction suspendTxn() {		
		try {
			return DAOUtility.getInstance().suspendTransaction();			
		} catch (NamingException ne) {
			
		} catch (Exception e) {
			LOGGER.error("Error suspending main transaction", e);
			throw new RuntimeException("Error suspending main transaction", e);
		}
		
		return null;
	}
	
	private void resumeTxn(Transaction txn) {
		try {
			if (txn != null) {
				DAOUtility.getInstance().resumeTransaction(txn);
			}			
		} catch (Exception e) {
			LOGGER.error("Error resuming main transaction", e);
			if (txn != null) {
				try {
					txn.rollback();
				} catch (Exception e1) {
					
				}
			}
			throw new RuntimeException("Error resuming main transaction", e);
		}
	}
	
	private Session getSession() {
		try {
			String appName = DynamicExtensionDAO.getInstance().getAppName();
			IDAOFactory df = DAOConfigFactory.getInstance().getDAOFactory(appName);
			IConnectionManager connMgr = df.getDAO().getConnectionManager();
			return connMgr.getSessionFactory().openSession();			
		} catch (Exception e) {
			throw new RuntimeException("Error encountered opening new db session", e);
		}		
	}
	
	private Long getLastId(Connection connection, String tableName) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String sql = String.format(SELECT_LAST_ID, tableName);
			pstmt = connection.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if (rs == null || !rs.next()) {
				return null;
			}
			
			return rs.getLong(1);
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining last seen ID for " + tableName, e);			
		} finally {
			closeResultSet(rs);
			closeStatement(pstmt);
		}		
	}
	
	private Long getMaxId(Connection connection, String tableName) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String sql = String.format(SELECT_MAX_ID, tableName);
			pstmt = connection.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if (rs == null || !rs.next()) {
				return null;
			}
			
			return rs.getLong(1);
		} catch (Exception e) {
			throw new RuntimeException("Error obtaining max ID for " + tableName, e);			
		} finally {
			closeResultSet(rs);
			closeStatement(pstmt);
		}		
		
	}
	
	private void insertLastId(Connection connection, String tableName, Long lastId) {
		updateLastId(connection, tableName, lastId, true);
	}
	
	private void updateLastId(Connection connection, String tableName, Long lastId) {
		updateLastId(connection, tableName, lastId, false);
	}
	
	private void updateLastId(Connection connection, String tableName, Long lastId, boolean insert) {		
		PreparedStatement pstmt = null;
		
		try {
			String sql = null;
			if (insert) {
				sql = String.format(INSERT_LAST_ID, tableName, lastId);
			} else {
				sql = String.format(UPDATE_LAST_ID, lastId, tableName);
			}

			pstmt = connection.prepareStatement(sql);
			pstmt.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException("Error updating last seend ID for " + tableName, e);
		} finally {
			closeStatement(pstmt);
		}		
	}
		
	private void closeStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				LOGGER.error("Error closing statement", e);
			}
		}
	}
	
	private void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				LOGGER.error("Error closing result set", e);
			}
		}
	}	
	
	private void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				LOGGER.error("Error closing connection", e);
			}
		}
	}
	
	private void closeSession(Session session) {
		if (session != null) {
			try {
				session.close();
			} catch (Exception e) {
				LOGGER.error("Error closing session", e);
			}
		}
	}
}