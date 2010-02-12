/**
 *
 */

package edu.common.dynamicextensions.entitymanager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.hibernate.HibernateException;

import edu.common.dynamicextensions.bizlogic.BizLogicFactory;
import edu.common.dynamicextensions.dao.impl.DynamicExtensionDAO;
import edu.common.dynamicextensions.domain.AbstractAttribute;
import edu.common.dynamicextensions.domain.DynamicExtensionBaseDomainObject;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface;
import edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.common.bizlogic.AbstractBizLogic;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.NamedQueryParam;

/**
 * @author gaurav_sawant
 *
 */
public abstract class AbstractBaseMetadataManager
		implements
			EntityManagerExceptionConstantsInterface,
			DynamicExtensionsQueryBuilderConstantsInterface
{

	/**
	 * The abstract metadata manager helper.
	 */
	protected final transient AbstractMetadataManagerHelper abstractMetadataManagerHelper = AbstractMetadataManagerHelper
			.getInstance();

	/**
	 * This method is called when exception occurs while executing the roll back queries
	 * or reverse queries. When this method is called, it signifies that the database state
	 * and the meta data state for the entity are not in synchronization and administrator
	 * needs some database correction.
	 * @param exception The exception that took place.
	 * @param abstrMetadata Entity for which data tables are out of sync.
	 */
	protected abstract void logFatalError(Exception exception,
			AbstractMetadataInterface abstrMetadata);

	/**
	 * This method is called when there any exception occurs while generating
	 * the data table queries for the entity. Valid scenario is that if we need
	 * to fire Q1 Q2 and Q3 in order to create the data tables and Q1 Q2 get
	 * fired successfully and exception occurs while executing query Q3 then
	 * this method receives the query list which holds the set of queries which
	 * negate the effect of the queries which were generated successfully so
	 * that the meta data information and database are in synchronization.
	 *
	 * @param revQryStack
	 *            the rev qry stack
	 * @param abstrMetadata
	 *            the abstr metadata
	 * @param exception
	 *            the exception
	 * @param dao
	 *            the dao
	 *
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 */
	protected void rollbackQueries(Stack<String> revQryStack,
			AbstractMetadataInterface abstrMetadata, Exception exception, DAO dao)
			throws DynamicExtensionsSystemException
	{
		abstractMetadataManagerHelper.rollbackDao(dao);
		if (revQryStack != null && !revQryStack.isEmpty())
		{
			String message = "";
			JDBCDAO jdbcDao = null;
			try
			{
				jdbcDao = executeRevertQueryStack(revQryStack);
			}
			catch (HibernateException e)
			{
				message = e.getMessage();
			}
			catch (DAOException exc)
			{
				message = exc.getMessage();
				logFatalError(exc, abstrMetadata);
			}
			finally
			{
				sendError(exception, message, jdbcDao);
			}
		}
	}

	/**
	 * Execute revert query stack.
	 *
	 * @param revQryStack
	 *            the rev qry stack
	 *
	 * @return the JDBCDAO
	 *
	 * @throws DAOException
	 *             the DAO exception
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 */
	protected JDBCDAO executeRevertQueryStack(Stack<String> revQryStack) throws DAOException,
			DynamicExtensionsSystemException
	{
		JDBCDAO jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
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
						"Exception occured while executing rollback queries.", e, DYEXTN_S_002);
			}
		}
		jdbcDao.commit();
		return jdbcDao;
	}

	/**
	 * Send error.
	 *
	 * @param exception
	 *            the exception
	 * @param message
	 *            the message
	 * @param jdbcDao
	 *            the jdbc dao
	 *
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 */
	protected void sendError(Exception exception, String message, JDBCDAO jdbcDao)
			throws DynamicExtensionsSystemException
	{
		DynamicExtensionsUtility.closeDAO(jdbcDao);
		logDebug("rollbackQueries", DynamicExtensionsUtility.getStackTrace(exception));
		DynamicExtensionsSystemException xception = new DynamicExtensionsSystemException(message,
				exception);

		xception.setErrorCode(DYEXTN_S_000);
		throw xception;
	}

	/**
	 * This method is used to log the messages in a uniform manner. The method
	 * takes the string method name and string message. Using these parameters
	 * the method formats the message and logs it.
	 *
	 * @param methodName
	 *            Name of the method for which the message needs to be logged.
	 * @param message
	 *            The message that needs to be logged.
	 */
	protected void logDebug(String methodName, String message)
	{
		Logger.out.debug("[AbstractMetadataManager.]" + methodName + "() -- " + message);
	}

	/**
	 * Returns all instances in the whole system for a given type of the object.
	 *
	 * @param objectName
	 *            the object name
	 *
	 * @return the all objects
	 *
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 */
	protected Collection getAllObjects(String objectName) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		AbstractBizLogic bizLogic = BizLogicFactory.getDefaultBizLogic();
		Collection objects = null;

		try
		{
			objects = bizLogic.retrieve(objectName);
			if (objects == null)
			{
				objects = new HashSet();
			}
		}
		catch (BizLogicException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}

		return objects;
	}

	/**
	 * This method returns object for a given class name and identifier.
	 *
	 * @param objectName
	 *            the object name
	 * @param identifier
	 *            the identifier
	 *
	 * @return the object by identifier
	 *
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 */
	protected DynamicExtensionBaseDomainObject getObjectByIdentifier(String objectName,
			String identifier) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		AbstractBizLogic bizLogic = BizLogicFactory.getDefaultBizLogic();
		DynamicExtensionBaseDomainObject dyExtBsDmnObj;
		try
		{
			// After moving to MYSQL 5.2, the type checking is strict so changing the identifier to Long.
			List objects = bizLogic.retrieve(objectName, DEConstants.OBJ_IDENTIFIER, Long
					.valueOf(identifier));

			if (objects == null || objects.isEmpty())
			{
				Logger.out.debug("Required Obejct not found: Object Name*" + objectName
						+ "*   identifier  *" + identifier + "*");
				throw new DynamicExtensionsApplicationException("OBJECT_NOT_FOUND");
			}

			dyExtBsDmnObj = (DynamicExtensionBaseDomainObject) objects.get(0);
		}
		catch (BizLogicException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}

		return dyExtBsDmnObj;
	}

	/**
	 * This method executes the HQL query given the query name and query
	 * parameters. The queries are specified in the EntityManagerHQL.hbm.xml
	 * file. For each query, a name is given. Each query is replaced with
	 * parameters before execution.The parameters are given by each calling
	 * method.
	 *
	 * @param queryName
	 *            the query name.
	 * @param substParams
	 *            the subst params.
	 *
	 * @return the collection.
	 *
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception.
	 */
	public static Collection executeHQL(String queryName, Map<String, NamedQueryParam> substParams)
			throws DynamicExtensionsSystemException
	{
		Collection objects = null;
		HibernateDAO hibernateDAO = null;
		try
		{
			hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();
			objects = hibernateDAO.executeNamedQuery(queryName, substParams);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("Error while rolling back the session", e);
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(hibernateDAO);
		}

		return objects;
	}

	/**
	 * This method sets the object properties. It invokes appropriate setter
	 * method depending on the data type of argument.
	 *
	 * @param attribute
	 *            the attribute
	 * @param dataType
	 *            the data type
	 * @param klass
	 *            the klass
	 * @param dataValue
	 *            the data value
	 * @param returnedObj
	 *            the returned obj
	 *
	 * @return the object
	 *
	 * @throws ParseException
	 *             the parse exception.
	 * @throws ClassNotFoundException
	 *             the class not found exception.
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws NoSuchMethodException
	 *             the no such method exception
	 */
	protected Object setObjectProperty(AbstractAttribute attribute, String dataType, Class klass,
			Map<AbstractAttributeInterface, Object> dataValue, Object returnedObj)
			throws ClassNotFoundException, ParseException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException
	{
		Object value = null;

		String attrName = attribute.getName();
		attrName = attrName.substring(0, 1).toUpperCase()
				+ attrName.substring(1, attrName.length());
		value = dataValue.get(attribute);
		abstractMetadataManagerHelper.handleDatatype(attribute, dataType, klass, returnedObj,
				value, attrName);
		return returnedObj;
	}

	/**
	 * This method sets the object properties. It invokes appropriate setter
	 * method depending on the data type of argument.
	 *
	 * @param attribute
	 *            the attribute
	 * @param dataType
	 *            the data type
	 * @param klass
	 *            the klass
	 * @param returnedObj
	 *            the returned obj
	 * @param walue
	 *            the walue
	 *
	 * @return the object
	 *
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws NoSuchMethodException
	 *             the no such method exception
	 * @throws ParseException
	 *             the parse exception
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 */
	protected Object setObjectProperty(AbstractAttribute attribute, String dataType, Class klass,
			Object walue, Object returnedObj) throws ClassNotFoundException, ParseException,
			NoSuchMethodException, IllegalAccessException, InvocationTargetException

	{
		Object value = null;

		String attrName = attribute.getName();
		attrName = attrName.substring(0, 1).toUpperCase()
				+ attrName.substring(1, attrName.length());
		value = walue;
		abstractMetadataManagerHelper.handleDatatype(attribute, dataType, klass, returnedObj,
				value, attrName);
		return returnedObj;
	}

	/**
	 * Invokes the setter method on the given <code>invokeOnObject</code> with
	 * the given <code>argument</code> of type <code>argumentType</code> of
	 * class <code>klass</code> using the given <code>property</code> .
	 *
	 * @param klass
	 *            the klass
	 * @param property
	 *            the property
	 * @param argumentType
	 *            the argument type
	 * @param invokeOnObject
	 *            the invoke on object
	 * @param argument
	 *            the argument
	 *
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws NoSuchMethodException
	 *             the no such method exception
	 */
	protected void invokeSetterMethod(Class klass, String property, Class argumentType,
			Object invokeOnObject, Object argument) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException

	{
		// Method setter = klass.getMethod("set" + property, argumentType);
		// setter.invoke(invokeOnObject, argument);
		abstractMetadataManagerHelper.invokeSetterMethod(klass, property, argumentType,
				invokeOnObject, argument);
	}

	/**
	 * Invokes the getter method on the given <code>invokeOnObject</code> of
	 * class <code>klass</code> using the given <code>property</code>.
	 *
	 * @param klass
	 *            the klass.
	 * @param property
	 *            the property.
	 * @param invokeOnObject
	 *            the invoke on object.
	 *
	 * @return the returned object.
	 *
	 * @throws NoSuchMethodException
	 *             the no such method exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 */
	protected Object invokeGetterMethod(Class klass, String property, Object invokeOnObject)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Method getter = klass.getMethod("get" + property);
		Object returnedObject = getter.invoke(invokeOnObject);

		return returnedObject;
	}

	/**
	 * This method takes the class name, object name and returns the object.
	 *
	 * @param className
	 *            class name
	 * @param objectName
	 *            objectName
	 *
	 * @return DynamicExtensionBaseDomainObjectInterface
	 *
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 */
	public DynamicExtensionBaseDomainObjectInterface getObjectByName(String className,
			String objectName) throws DynamicExtensionsSystemException
	{
		DynamicExtensionBaseDomainObjectInterface dyExtBsDmnObj = null;

		if (abstractMetadataManagerHelper.isNotEmptyString(objectName))
		{
			// Get the instance of the default biz logic.
			DefaultBizLogic defaultBizLogic = BizLogicFactory.getDefaultBizLogic();
			String deAppName = DynamicExtensionDAO.getInstance().getAppName();
			defaultBizLogic.setAppName(deAppName);

			try
			{
				List objects = defaultBizLogic.retrieve(className, "name", objectName);
				if (objects != null && !objects.isEmpty())
				{
					dyExtBsDmnObj = (DynamicExtensionBaseDomainObjectInterface) objects.get(0);
				}
			}
			catch (BizLogicException e)
			{
				throw new DynamicExtensionsSystemException(e.getMessage(), e);
			}
		}
		return dyExtBsDmnObj;
	}

	/**
	 * This method takes the class name, object name and returns the object.
	 *
	 * @param className
	 *            class name.
	 * @param objectName
	 *            objectName.
	 * @param hibernateDao
	 *            the hibernate dao.
	 *
	 * @return DynamicExtensionBaseDomainObjectInterface.
	 *
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception.
	 */
	public DynamicExtensionBaseDomainObjectInterface getObjectByName(String className,
			String objectName, HibernateDAO hibernateDao) throws DynamicExtensionsSystemException
	{
		DynamicExtensionBaseDomainObjectInterface dyExtBsDmnObj = null;
		if (abstractMetadataManagerHelper.isNotEmptyString(objectName))
		{
			try
			{
				List objects = hibernateDao.retrieve(className, "name", objectName);
				if (objects != null && !objects.isEmpty())
				{
					dyExtBsDmnObj = (DynamicExtensionBaseDomainObjectInterface) objects.get(0);
				}
			}
			catch (DAOException e)
			{
				throw new DynamicExtensionsSystemException(e.getMessage(), e);
			}
		}
		return dyExtBsDmnObj;
	}

	/**
	 * Handle rollback.
	 *
	 * @param exception
	 *            the exception
	 * @param excMessage
	 *            the exc message
	 * @param dao
	 *            the dao
	 * @throws DynamicExtensionsSystemException
	 */
	protected void handleRollback(String excMessage, DAO dao)
			throws DynamicExtensionsSystemException
	{
		try
		{
			dao.rollback();
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException(excMessage);
		}

	}

}
