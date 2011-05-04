/**
 *
 */

package edu.common.dynamicextensions.operations;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.entitymanager.AbstractMetadataManager;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.skiplogic.SkipLogic;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.DBTypes;
import edu.wustl.dao.util.NamedQueryParam;

/**
 * @author Gaurav_mehta
 *
 */
public class CategoryOperations
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(CategoryOperations.class);

	/**
	 * This method will save the Category to DB.
	 * @param isPersistMetadataOnly the is persist metadata only
	 * @param category the category
	 * @return
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException the dynamic extensions application exception
	 * @throws DAOException
	 */
	public Stack<String> saveCategory(boolean isPersistMetadataOnly, CategoryInterface category,HibernateDAO hibernateDAO)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException, DAOException
	{
		CategoryManagerInterface categoryManager = CategoryManager.getInstance();
		Stack<String> revQueries = new Stack<String>();
		if (isPersistMetadataOnly)
		{
			try
			{
				categoryManager.persistCategoryMetadata(category,hibernateDAO);
			}
			catch (DynamicExtensionsSystemException e)
			{
				LOGGER.error(e.getMessage());
				throw new DynamicExtensionsSystemException("ERROR WHILE SAVING A CATEGORY", e);
			}
			catch (DynamicExtensionsApplicationException e)
			{
				LOGGER.error(e.getMessage());
				throw new DynamicExtensionsApplicationException("ERROR WHILE SAVING A CATEGORY", e);
			}
		}
		else
		{
			revQueries = categoryManager.persistDynamicExtensionObjectForCategory(category,hibernateDAO);
		}
		return revQueries;
	}

	/**
	 * Fetch existing skip logic for control.
	 * @param identifier the identifier
	 * @return the collection< skip logic>
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public void deleteSkipLogic(Long containerIdentifier,HibernateDAO hibernateDAO) throws DynamicExtensionsSystemException, DAOException
	{
		Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
		substParams.put("0", new NamedQueryParam(DBTypes.LONG, containerIdentifier));
		Collection<SkipLogic> skipLogics = AbstractMetadataManager.executeHQL("getSkipLogicByContainerId", substParams);
		if(!skipLogics.isEmpty())
		{
			hibernateDAO.delete((skipLogics.iterator().next()));
		}
	}


}
