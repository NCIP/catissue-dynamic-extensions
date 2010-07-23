package edu.common.dynamicextensions.util;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.entitymanager.DynamicExtensionBaseQueryBuilder;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.entitymanager.QueryBuilderFactory;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.xmi.DynamicQueryList;
import edu.common.dynamicextensions.xmi.importer.XMIImporterUtil;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;

public class SaveEntityGroupAndDETablesUtil {

	private static final Logger LOGGER = Logger.getCommonLogger(SaveEntityGroupAndDETablesUtil.class);

	/**
	 * It will execute all the queries present in the dynamicQueryList object &
	 * multiselectMigartionScripts then will commit the hibernatedao.
	 * If some problem occurs during this then it will roll back the dao &
	 * also execute the revrese queries present in the dynamicQueryList.
	 * @param multiselectMigartionScripts queries to be fired to migrate multiselect attribute.
	 * @param dynamicQueryList queries to be fired to create all DE tables.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	public void createDETablesAndSaveEntityGroup(HibernateDAO hibernatedao,
			Map<AssociationInterface, String> multiselectMigartionScripts,
			DynamicQueryList dynamicQueryList) throws DynamicExtensionsSystemException
	{
		LOGGER.info("Now Creating DE Tables....");
		if(hibernatedao!=null){

			MetaDataIntegrator associateHookEntityUtil = new MetaDataIntegrator();
			executeDynamicQueryList(hibernatedao,dynamicQueryList);
			// Execute data migration scripts for attributes that were changed from a normal attribute to
			// a multiselect attribute.
			if(multiselectMigartionScripts!=null){

				List<String> multiSelMigrationQueries = EntityManagerUtil
				.updateSqlScriptToMigrateOldDataForMultiselectAttribute(multiselectMigartionScripts);
				XMIImporterUtil.executeDML(multiSelMigrationQueries);
			}
		}

	}

	public void executeDynamicQueryList(HibernateDAO hibernatedao,
			DynamicQueryList dynamicQueryList)
	throws DynamicExtensionsSystemException {
	Stack<String> rlbkQryStack = new Stack<String>();
	try
	{
		DynamicExtensionBaseQueryBuilder queryBuilder = QueryBuilderFactory.getQueryBuilder();
		if (dynamicQueryList != null)
		{
			queryBuilder.executeQueries(dynamicQueryList.getQueryList(), dynamicQueryList
					.getRevQueryList(), rlbkQryStack);
		}
	}
	catch (Exception e)
	{
		rollbackQueries(rlbkQryStack, e, hibernatedao);
		throw new DynamicExtensionsSystemException(e.getMessage(), e);
	}
	}
	/**
	 * It will call rollback on the provided DAO & then will execute the
	 * Queries which are present int the revQryStack to restore the original state.
	 * @param revQryStack stack which contains the Queries to be fired to restore the state.
	 * @param exception exception occurred because of which rollback is called.
	 * @param dao dao which is to be rollback.
	 * @throws DynamicExtensionsSystemException exception
	 */
	protected void rollbackQueries(Stack<String> revQryStack, Exception exception, DAO dao)
			throws DynamicExtensionsSystemException
	{
		String message = "";
		rollbackDao(dao);
		if (revQryStack != null && !revQryStack.isEmpty())
		{
			JDBCDAO jdbcDao = null;
			try
			{
				jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
				executeQuriesInStack(revQryStack, jdbcDao);
				jdbcDao.commit();
			}
			catch (DAOException exc)
			{
				message = exc.getMessage();
				//logFatalError(exc, abstrMetadata);
			}
			finally
			{

				DynamicExtensionsUtility.closeDAO(jdbcDao);
				//logDebug("rollbackQueries", DynamicExtensionsUtility.getStackTrace(exception));
				DynamicExtensionsSystemException xception = new DynamicExtensionsSystemException(
						message, exception);

				//xception.setErrorCode(DYEXTN_S_000);
				throw xception;
			}
		}
	}
	/**
	 * It will execute all the Queries present into the stack using the provided dao
	 * @param revQryStack which contains queries
	 * @param jdbcDao dao
	 * @throws DynamicExtensionsSystemException exception.
	 */
	private void executeQuriesInStack(Stack<String> revQryStack, JDBCDAO jdbcDao)
			throws DynamicExtensionsSystemException
	{
		while (!revQryStack.empty())
		{
			String query = revQryStack.pop();
			try
			{
				jdbcDao.executeUpdate(query);
			}
			catch (DAOException e)
			{
				throw new DynamicExtensionsSystemException(
						"Exception occured while executing rollback queries.", e);
			}
		}
	}

	/**
	 * It will call rollback on the given dao.
	 * @param dao dao which is to be rollbacked.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	private void rollbackDao(DAO dao) throws DynamicExtensionsSystemException
	{
		if (dao != null)
		{
			try
			{
				dao.rollback();
			}
			catch (DAOException excep)
			{
				throw new DynamicExtensionsSystemException("Not able to rollback the transaction.",
						excep);
			}
		}
	}


}
