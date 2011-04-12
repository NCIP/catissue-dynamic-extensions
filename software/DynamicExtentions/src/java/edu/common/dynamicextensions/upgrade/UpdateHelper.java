
package edu.common.dynamicextensions.upgrade;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;

import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.entitymanager.AbstractBaseMetadataManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerExceptionConstantsInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.dao.DAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.DBTypes;
import edu.wustl.dao.util.NamedQueryParam;

public class UpdateHelper implements EntityManagerExceptionConstantsInterface
{

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(UpdateHelper.class);

	/**
	 * Gets the package name.
	 * @param entityGroup the entity group
	 * @return the package name
	 */
	public static String getPackageName(EntityGroupInterface entityGroup)
	{
		String packageName = null;
		Collection<TaggedValueInterface> allTaggedValues = entityGroup.getTaggedValueCollection();
		for (TaggedValueInterface taggedValue : allTaggedValues)
		{
			if (taggedValue.getKey().equalsIgnoreCase("PackageName"))
			{
				packageName = taggedValue.getValue();
			}
		}
		return packageName;
	}

	/**
	 * Validate package name.
	 * @param packageName package name
	 * @throws DynamicExtensionsSystemException
	 */
	@SuppressWarnings("unchecked")
	protected static Collection fetchContainersCollection(long entityGroupId)
			throws DynamicExtensionsSystemException
	{
		Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
		substParams.put("0", new NamedQueryParam(DBTypes.LONG, entityGroupId));
		return AbstractBaseMetadataManager.executeHQL("getAllContainersOfSameEntityGroup",
				substParams);
	}

	/**
	 * Prints the error messages.
	 * @param errorList the error list
	 * @param eRRORS
	 */
	protected static void printErrorMessages(List<String> errorList,
			List<String> consolidatedErrorList)
	{
		for (String error : errorList)
		{
			consolidatedErrorList.add(error);
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
	protected static void rollbackQueries(Stack<String> revQryStack, Exception exception, DAO dao)
			throws DynamicExtensionsSystemException
	{
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
				LOGGER.error("Error occured while updating paths for the missing assocaition."
						+ exception.getMessage());
			}
			finally
			{
				DynamicExtensionsUtility.closeDAO(jdbcDao);
			}
		}
	}

	/**
	 * It will call rollback on the given dao.
	 * @param dao dao which is to be rollbacked.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	private static void rollbackDao(DAO dao) throws DynamicExtensionsSystemException
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

	/**
	 * It will execute all the Queries present into the stack using the provided dao
	 * @param revQryStack which contains queries
	 * @param jdbcDao dao
	 * @throws DynamicExtensionsSystemException exception.
	 */
	private static void executeQuriesInStack(Stack<String> revQryStack, JDBCDAO jdbcDao)
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
}
