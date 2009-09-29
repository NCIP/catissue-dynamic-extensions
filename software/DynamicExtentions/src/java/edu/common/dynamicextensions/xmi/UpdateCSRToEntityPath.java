
package edu.common.dynamicextensions.xmi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;

/**
 * @author falguni_sachde
 *
 */
public class UpdateCSRToEntityPath
{

	private static Long firstEntityId;
	private static Long lastEntityId;

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	//Logger
	private static final Logger LOGGER = Logger.getCommonLogger(UpdateCSRToEntityPath.class);


	/**
	 * @param entityGroupId
	 * @param newEntitiesIds
	 */
	public static void addCuratedPathsFromToAllEntities(List<AssociationInterface> startAssociationList,List<Long> newEntitiesIds)
	{
		String staticEntAssnId;

		try
		{
			int associationListSize = startAssociationList.size();
			if(associationListSize >= 1)
			{
				firstEntityId = startAssociationList.get(0).getEntity().getId();
				lastEntityId = startAssociationList.get(associationListSize-1).getEntity().getId();

				staticEntAssnId = getStaticEntityAssnIds(startAssociationList);
				addPathForEntityGroup(staticEntAssnId, newEntitiesIds);
			}

		}
		catch (Exception e)
		{
			LOGGER.info("error in addCuratedPaths To All Entities" + e.getMessage());
		}
	}

	/**
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws DAOException
	 * @throws SQLException
	 */
	private static String getStaticEntityAssnIds(List<AssociationInterface> associationList) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, DAOException, SQLException
	{
		StringBuffer strIntramoelAssnId=new StringBuffer();
		for(AssociationInterface association : associationList)
		{
			strIntramoelAssnId.append(getIntraModelAssonId(association.getId()).toString());
			strIntramoelAssnId.append('_');

		}
		removeLastUnderscore(strIntramoelAssnId);

		return strIntramoelAssnId.toString();
	}

	private static void removeLastUnderscore(StringBuffer strIntramoelAssnId)
	{
		int lastIndex = strIntramoelAssnId.length()-1;
		if(strIntramoelAssnId.charAt(lastIndex)=='-')
		{
			strIntramoelAssnId.deleteCharAt(lastIndex);
		}
	}


	/**
	 * @param strDEAssnId
	 * @return
	 * @throws DAOException
	 * @throws SQLException
	 */
	private static Long getIntraModelAssonId(Long deAssnId) throws DAOException, SQLException
	{
		DAOConfigFactory daocConfigFactory = DAOConfigFactory.getInstance();
		IDAOFactory daoFactory = daocConfigFactory.getDAOFactory("dynamicExtention");
		JDBCDAO jdbcdao = null;
		jdbcdao = daoFactory.getJDBCDAO();
		jdbcdao.openSession(null);
		String sql = "select ASSOCIATION_ID from  intra_model_association where DE_ASSOCIATION_ID='"
				+ deAssnId + "'";
		ResultSet resultSet = jdbcdao.getQueryResultSet(sql);
		long intramodelId = 0;
		if (resultSet.next())
		{
			intramodelId = resultSet.getLong(1);

		}
		jdbcdao.closeStatement(resultSet);
		jdbcdao.closeSession();
		return intramodelId;
	}

	/**
	 * @param strAssnId
	 * @param entityGroupId
	 * @param newEntitiesIds
	 * @throws SQLException
	 */
	private static void addPathForEntityGroup(String strAssnId, List<Long> newEntitiesIds)
	{
		try
		{
			if (newEntitiesIds == null)
			{
				//This will add path from CSR to all entities of all entity group
				//This is one-time task ,called from upgrade ant task
				Collection<NameValueBean> entityGrpLst = EntityManager.getInstance()
						.getAllEntityGroupBeans();
				NameValueBean groupBean = null;
				Iterator<NameValueBean> iterator = entityGrpLst.iterator();
				while (iterator.hasNext())
				{
					groupBean = iterator.next();
					if (groupBean != null)
					{
						LOGGER.info("Setting path for entity group==" + groupBean.getValue() + "=="
								+ groupBean.getName());
					}
					insertPathsForEntityGroup(strAssnId, Long.valueOf(groupBean.getValue()), null);
				}
			}
			else
			{
				insertPathsForEntityGroup(strAssnId, null, newEntitiesIds);
			}
		}
		catch (Exception e)
		{
			LOGGER.info("error addPathForEntityGroup" + e.getMessage());
		}

	}

	/**
	 * @param strAssnId
	 * @param entityGroupId
	 * @param newEntitiesIds
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 * @throws SQLException
	 */
	private static void insertPathsForEntityGroup(String strAssnId, Long entityGroupId,
			List<Long> newEntitiesIds) throws DynamicExtensionsSystemException, DAOException,
			SQLException
	{
		Collection<Long> entityIds = null;
		if (newEntitiesIds == null)
		{
			//If newEntitiesIds is not given,then add paths from CSR to all entities within e.group
			entityIds = EntityManager.getInstance().getAllEntityIdsForEntityGroup(entityGroupId);
			LOGGER.info("Adding paths for all entities of group.size of list: " + entityIds.size());
		}
		else
		{
			entityIds = newEntitiesIds;
		}
		insertPathsForEntities(strAssnId, entityIds);

	}

	/**
	 * @param strAssnId
	 * @param entityIds
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 * @throws SQLException
	 */
	private static void insertPathsForEntities(String strAssnId, Collection<Long> entityIds)
			throws DynamicExtensionsSystemException, DAOException, SQLException
	{
		LOGGER.info("Adding paths for " + entityIds.size() + " entities....");
		DAOConfigFactory daocConfigFactory = DAOConfigFactory.getInstance();
		IDAOFactory daoFactory = daocConfigFactory.getDAOFactory("dynamicExtention");
		JDBCDAO jdbcdao = null;
		jdbcdao = daoFactory.getJDBCDAO();
		jdbcdao.openSession(null);
		for (Long entityId : entityIds)
		{
			if (!AnnotationUtil.isPathAdded(firstEntityId, entityId, jdbcdao))
			{
				// It is an intermediate path from record entry to given entity.
				String interPathAssn = getExistingInterMediatePath(lastEntityId, entityId,
						jdbcdao);
				if (interPathAssn != null)
				{
					String curatePathString = strAssnId + "_" + interPathAssn;
					// Add a path between csr_deform.
					insertPath(firstEntityId, entityId, curatePathString, jdbcdao);
				}
			}
		}

		jdbcdao.commit();
		jdbcdao.closeSession();
	}

	/**
	 * @param recEntryEntityId
	 * @param entityId
	 * @param jdbcdao
	 * @return
	 * @throws DAOException
	 * @throws SQLException
	 */
	private static String getExistingInterMediatePath(Long recEntryEntityId, Long entityId,
			JDBCDAO jdbcdao) throws DAOException, SQLException
	{
		String selSQL = "select intermediate_path  from path where first_entity_id ='"
				+ recEntryEntityId + "' and last_entity_id = '" + entityId + "'";
		ResultSet resultSet = jdbcdao.getQueryResultSet(selSQL);
		String interPathid = null;
		if (resultSet.next())
		{
			interPathid = resultSet.getString(1);
		}
		jdbcdao.closeStatement(resultSet);
		return interPathid;
	}

	/**
	 * @param firstEntityId
	 * @param secondEntityId
	 * @param newinterPathid
	 * @throws DAOException
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 */
	private static void insertPath(Long firstEntityId, Long secondEntityId, String newinterPathid,
			JDBCDAO jdbcdao) throws DAOException, SQLException, DynamicExtensionsSystemException
	{
		if (!AnnotationUtil.isPathAdded(firstEntityId, secondEntityId, jdbcdao))
		{
			String sql;
			long nextIdPath = getNextId("path", "PATH_ID", jdbcdao);
			sql = "insert into PATH (PATH_ID, FIRST_ENTITY_ID,"
			+ "INTERMEDIATE_PATH, LAST_ENTITY_ID) values " + nextIdPath + "," + firstEntityId + ",'"
					+ newinterPathid + "'," + secondEntityId + ")";
			jdbcdao.executeUpdate(sql);

		}
	}

	/**
	 * @param tablename
	 * @param coloumn
	 * @param jdbcdao
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	private static Long getNextId(String tablename, String coloumn, JDBCDAO jdbcdao)
			throws SQLException, DAOException
	{
		String sql = "select max(" + coloumn + ") from " + tablename;
		ResultSet resultSet = jdbcdao.getQueryResultSet(sql);

		long nextId = 0;
		if (resultSet.next())
		{
			long maxId = resultSet.getLong(1);
			nextId = maxId + 1;
		}
		jdbcdao.closeStatement(resultSet);
		return nextId;
	}
}