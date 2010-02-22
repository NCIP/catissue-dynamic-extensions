
package edu.common.dynamicextensions.entitymanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.hibernate.Query;

import edu.common.dynamicextensions.dao.impl.DynamicExtensionDAO;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domain.HQLPlaceHolderObject;
import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface;
import edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.TablePropertiesInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.xmi.DynamicQueryList;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.Status;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.QueryWhereClause;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.dao.util.NamedQueryParam;

/**
 *
 * @author mandar_shidhore
 * @author rajesh_patil
 *
 */
public abstract class AbstractMetadataManager extends AbstractBaseMetadataManager
		implements
			EntityManagerExceptionConstantsInterface,
			DynamicExtensionsQueryBuilderConstantsInterface
{

	/**
	 * This method creates dynamic table queries for the entities within a
	 * group.
	 *
	 * @param dyExtBsDmnObj
	 *            the dy ext bs dmn obj
	 * @param revQueries
	 *            the rev queries
	 * @param queries
	 *            the queries
	 *
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 */
	protected abstract void preProcess(DynamicExtensionBaseDomainObjectInterface dyExtBsDmnObj,
			List<String> revQueries, List<String> queries) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException;

	/**
	 * This method executes dynamic table queries created for all the entities
	 * within a group.
	 *
	 * @param queries
	 *            List of queries to be executed to created dynamic tables.
	 * @param revQueries
	 *            List of queries to be executed in case any problem occurs at
	 *            DB level.
	 * @param rlbkQryStack
	 *            Stack to undo any changes done beforehand at DB level.
	 *
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 */
	protected abstract void postProcess(List<String> queries, List<String> revQueries,
			Stack<String> rlbkQryStack) throws DynamicExtensionsSystemException;

	/**
	 * Gets the query builder instance.
	 *
	 * @return the query builder instance
	 */
	protected abstract DynamicExtensionBaseQueryBuilder getQueryBuilderInstance();

	/**
	 * This method substitutes the parameters from substitution parameters map
	 * into the input query.
	 *
	 * @param substParams
	 *            the subst params.
	 * @param query
	 *            the query.
	 *
	 * @return the query.
	 */
	protected Query substitutionParameterForQuery(Query query,
			Map<String, HQLPlaceHolderObject> substParams)
	{
		for (int counter = 0; counter < substParams.size(); counter++)
		{
			HQLPlaceHolderObject plcHolderObj = substParams.get(Integer.toBinaryString(counter));
			String objectType = plcHolderObj.getType();
			abstractMetadataManagerHelper.setParametersOnQuery(query, counter, plcHolderObj,
					objectType);
		}
		return query;
	}

	/**
	 * Execute hql.
	 *
	 * @param hibernateDAO
	 *            the hibernate dao
	 * @param queryName
	 *            the query name
	 * @param substParams
	 *            the subst params
	 *
	 * @return the collection
	 *
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 */
	protected Collection executeHQL(HibernateDAO hibernateDAO, String queryName,
			Map<String, NamedQueryParam> substParams) throws DynamicExtensionsSystemException
	{
		try
		{
			return hibernateDAO.executeNamedQuery(queryName, substParams);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e, DYEXTN_S_001);
		}
	}

	/**
	 * This method persists an entity group and the associated entities and also
	 * creates the data table for the entities only if the hibernateDao is not
	 * provided. if HibernateDao is provided then its the responsibility of the
	 * caller to execute all the Queries which are returned from this method
	 * just before commiting the hibernate Dao.
	 *
	 * @param abstrMetadata
	 *            object to be save
	 * @param hibernateDAO
	 *            dao which should be used (optional).
	 *
	 * @return queryList to be executed.
	 *
	 * @throws DynamicExtensionsSystemException
	 *             exception
	 * @throws DynamicExtensionsApplicationException
	 *             exception
	 */
	protected DynamicQueryList persistDynamicExtensionObject(
			AbstractMetadataInterface abstrMetadata, HibernateDAO... hibernateDAO)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		List<String> revQueries = new LinkedList<String>();
		List<String> queries = new ArrayList<String>();
		Stack<String> rlbkQryStack = new Stack<String>();
		DynamicQueryList dynamicQueryList = new DynamicQueryList();
		if (hibernateDAO != null && hibernateDAO.length > 0)
		{
			preProcess(abstrMetadata, revQueries, queries);

			saveDynamicExtensionObject(abstrMetadata, hibernateDAO[0], rlbkQryStack);
			dynamicQueryList.setQueryList(queries);
			dynamicQueryList.setRevQueryList(revQueries);
		}
		else
		{
			dynamicQueryList = persistDynamicExtensionObject(abstrMetadata);
		}

		return dynamicQueryList;
	}

	/**
	 * This method persists an entity group and the associated entities and also
	 * creates the data table for the entities.
	 *
	 * @param abstrMetadata
	 *            the abstr metadata
	 *
	 * @return the dynamic query list
	 *
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 */
	private DynamicQueryList persistDynamicExtensionObject(AbstractMetadataInterface abstrMetadata)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		List<String> revQueries = new LinkedList<String>();
		List<String> queries = new ArrayList<String>();
		Stack<String> rlbkQryStack = new Stack<String>();
		HibernateDAO hibernateDAO = null;
		try
		{
			hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();

			preProcess(abstrMetadata, revQueries, queries);

			saveDynamicExtensionObject(abstrMetadata, hibernateDAO, rlbkQryStack);

			postProcess(queries, revQueries, rlbkQryStack);

			hibernateDAO.commit();
		}
		catch (DAOException e)
		{
			rollbackQueries(rlbkQryStack, null, e, hibernateDAO);
			throw new DynamicExtensionsSystemException(e.getMessage(), e, DYEXTN_S_003);
		}
		catch (DynamicExtensionsSystemException e)
		{
			rollbackQueries(rlbkQryStack, null, e, hibernateDAO);
			Logger.out.error(e.getMessage());
			throw e;
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(hibernateDAO);

		}

		return null;
	}

	/**
	 * This method persists an entity group and the associated entities without
	 * creating the data table for the entities.
	 *
	 * @param abstrMetadata
	 *            the abstr metadata
	 * @param hibernateDAO
	 *            the hibernate dao
	 *
	 * @return queryList to be executed
	 *
	 * @throws DynamicExtensionsSystemException
	 *             exception
	 * @throws DynamicExtensionsApplicationException
	 *             exception
	 */
	public DynamicQueryList persistDynamicExtensionObjectMetdata(
			AbstractMetadataInterface abstrMetadata, HibernateDAO... hibernateDAO)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		Stack<String> rlbkQryStack = new Stack<String>();
		HibernateDAO newHibernateDAO = null;
		if (hibernateDAO != null && hibernateDAO.length > 0)
		{
			newHibernateDAO = hibernateDAO[0];
			saveDynamicExtensionObject(abstrMetadata, newHibernateDAO, rlbkQryStack);
		}
		else
		{
			try
			{
				newHibernateDAO = DynamicExtensionsUtility.getHibernateDAO();
				saveDynamicExtensionObject(abstrMetadata, newHibernateDAO, rlbkQryStack);
				newHibernateDAO.commit();
			}
			catch (DAOException e)
			{
				rollbackQueries(rlbkQryStack, null, e, newHibernateDAO);
				throw new DynamicExtensionsSystemException(e.getMessage(), e, DYEXTN_S_003);
			}
			finally
			{
				DynamicExtensionsUtility.closeDAO(newHibernateDAO);
			}
		}
		return null;
	}

	/**
	 * Save dynamic extension object.
	 *
	 * @param abstrMetadata
	 *            the abstr metadata
	 * @param hibernateDAO
	 *            the hibernate dao
	 * @param rlbkQryStack
	 *            the rlbk qry stack
	 *
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 */
	private void saveDynamicExtensionObject(AbstractMetadataInterface abstrMetadata,
			HibernateDAO hibernateDAO, Stack<String> rlbkQryStack)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		try
		{
			if (abstrMetadata.getId() == null)
			{
				hibernateDAO.insert(abstrMetadata);
			}
			else
			{
				hibernateDAO.update(abstrMetadata);
			}
		}
		catch (DAOException e)
		{
			rollbackQueries(rlbkQryStack, null, e, hibernateDAO);
			throw new DynamicExtensionsSystemException(e.getMessage(), e, DYEXTN_S_003);
		}
	}

	/**
	 * Gets the dynamic query list.
	 *
	 * @param entityGroup
	 *            the entity group
	 * @param revQueries
	 *            the rev queries
	 * @param queries
	 *            the queries
	 *
	 * @return the dynamic query list
	 *
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 */
	protected List<String> getDynamicQueryList(EntityGroupInterface entityGroup,
			List<String> revQueries, List<String> queries) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		List<EntityInterface> entities = DynamicExtensionsUtility.getUnsavedEntities(entityGroup);

		getCreateQueries(revQueries, queries, entities);
		getUpdateQueries(revQueries, queries, entities);

		List<EntityInterface> savedEntities = DynamicExtensionsUtility
				.getSavedEntities(entityGroup);

		HibernateDAO hibernateDAO = null;
		try
		{
			String appName = DynamicExtensionDAO.getInstance().getAppName();
			hibernateDAO = (HibernateDAO) DAOConfigFactory.getInstance().getDAOFactory(appName)
					.getDAO();
			hibernateDAO.openSession(null);
			for (EntityInterface savedEntity : savedEntities)
			{
				Entity dbaseCopy = (Entity) hibernateDAO.retrieveById(Entity.class
						.getCanonicalName(), savedEntity.getId());

				List<String> updateQueries = getQueryBuilderInstance().getUpdateEntityQueryList(
						(Entity) savedEntity, dbaseCopy, revQueries, hibernateDAO);
				if (updateQueries != null && !updateQueries.isEmpty())
				{
					queries.addAll(updateQueries);
				}
			}
		}
		catch (DAOException exception)
		{
			throw new DynamicExtensionsSystemException("Not able to retrieve the object.",
					exception);
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(hibernateDAO);
		}

		return queries;
	}

	/**
	 * Gets the creates the queries.
	 *
	 * @param revertBackQueries
	 *            the revert back queries
	 * @param actualRevQueries
	 *            the actual rev queries
	 * @param entities
	 *            the entities
	*
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 */
	private void getCreateQueries(List<String> revertBackQueries, List<String> actualRevQueries,
			List<EntityInterface> entities) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		for (EntityInterface entity : entities)
		{
			List<String> createQueries = getQueryBuilderInstance().getCreateEntityQueryList(
					(Entity) entity, revertBackQueries);

			if (createQueries != null && !createQueries.isEmpty())
			{
				actualRevQueries.addAll(createQueries);
			}
		}
	}

	/**
	 * Gets the update queries.
	 *
	 * @param revQueries
	 *            the rev queries.
	 * @param queries
	 *            the queries.
	 * @param entities
	 *            the entities.
	 *
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception.
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception.
	 */
	private void getUpdateQueries(List<String> revQueries, List<String> queries,
			List<EntityInterface> entities) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		for (EntityInterface entity : entities)
		{
			List<String> updateQueries = getQueryBuilderInstance().getUpdateEntityQueryList(
					(Entity) entity, revQueries);

			if (updateQueries != null && !updateQueries.isEmpty())
			{
				queries.addAll(updateQueries);
			}
		}
	}

	/**
	 * Gets the all records.
	 *
	 * @param entity
	 *            the entity
	 *
	 * @return the all records
	 *
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 */
	public List<EntityRecord> getAllRecords(AbstractEntityInterface entity)
			throws DynamicExtensionsSystemException
	{
		List<EntityRecord> records;
		JDBCDAO jdbcDao = null;
		List<List> results;
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			TablePropertiesInterface tblProperties = entity.getTableProperties();
			String tableName = tblProperties.getName();
			String[] selectColName = {IDENTIFIER};
			String[] whereColName = {Constants.ACTIVITY_STATUS_COLUMN};
			String[] whereColCndtn = {EQUAL};
			Object[] whereColValue = {Status.ACTIVITY_STATUS_ACTIVE.toString()};
			QueryWhereClause queryWhereClause = new QueryWhereClause(tableName);
			queryWhereClause.getWhereCondition(whereColName, whereColCndtn, whereColValue,
					Constants.AND_JOIN_CONDITION);
			results = jdbcDao.retrieve(tableName, selectColName, queryWhereClause);
			/*results = jdbcDao.retrieve(tableName, selectColName, whereColName, whereColCndtn,
			        whereColValue, null);*/
			records = getRecordList(results);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("Error while retrieving the data", e);
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(jdbcDao);
		}

		return records;
	}

	/**
	 * Gets the record list.
	 *
	 * @param results
	 *            the results.
	 *
	 * @return the record list.
	 */
	protected List<EntityRecord> getRecordList(List<List> results)
	{
		List<EntityRecord> records = new ArrayList<EntityRecord>();
		EntityRecord entityRecord;
		String identifier;

		for (List innnerList : results)
		{
			if (innnerList != null && !innnerList.isEmpty())
			{
				identifier = (String) innnerList.get(0);
				if (identifier != null)
				{
					entityRecord = abstractMetadataManagerHelper.getNewEntityRecord(identifier);
					records.add(entityRecord);
				}
			}
		}

		return records;
	}

	/**
	 * Gets the package name from the tagged values from within the entity
	 * groups.
	 *
	 * @param entity
	 *            the entity.
	 * @param packageName
	 *            the package name.
	 *
	 * @return the package name.
	 */
	protected String getPackageName(EntityInterface entity, String packageName)
	{
		Set<TaggedValueInterface> taggedValues = (Set<TaggedValueInterface>) entity
				.getEntityGroup().getTaggedValueCollection();
		Iterator<TaggedValueInterface> taggedValuesIter = taggedValues.iterator();
		String tmpPackageName = packageName;
		while (taggedValuesIter.hasNext())
		{
			TaggedValueInterface taggedValue = taggedValuesIter.next();
			if (taggedValue.getKey().equals("PackageName"))
			{
				tmpPackageName = taggedValue.getValue();
				break;
			}
		}

		return tmpPackageName;
	}

	/**
	 * This method will execute all the queries present in the queryListForFile list.
	 * @param queryListForFile list of queries to be executed.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	protected void executeFileRecordQueryList(List<FileQueryBean> queryListForFile)
			throws DynamicExtensionsSystemException
	{
		JDBCDAO dao = null;
		try
		{
			dao = DynamicExtensionsUtility.getJDBCDAO();

			for (FileQueryBean queryBean : queryListForFile)
			{
				final LinkedList<LinkedList<ColumnValueBean>> valueBeanList = new LinkedList<LinkedList<ColumnValueBean>>();
				valueBeanList.add(new LinkedList<ColumnValueBean>(queryBean.getColValBeanList()));
				dao.executeUpdate(queryBean.getQuery(), valueBeanList);
			}
			dao.commit();
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("Error occured while inserting records.", e);
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(dao);
		}

	}
}
