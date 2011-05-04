/**
 *
 */

package edu.common.dynamicextensions.entitymanager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.hibernate.HibernateException;

import edu.common.dynamicextensions.bizlogic.BizLogicFactory;
import edu.common.dynamicextensions.dao.impl.DynamicExtensionDAO;
import edu.common.dynamicextensions.domain.AbstractAttribute;
import edu.common.dynamicextensions.domain.DynamicExtensionBaseDomainObject;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.wustl.common.bizlogic.AbstractBizLogic;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
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
	protected final static AbstractMetadataManagerHelper abstractMetadataManagerHelper = AbstractMetadataManagerHelper
			.getInstance();

	/**
	 * This method is called when exception occurs while executing the roll back queries
	 * or reverse queries. When this method is called, it signifies that the database state
	 * and the meta data state for the entity are not in synchronization and administrator
	 * needs some database correction.
	 * @param exception The exception that took place.
	 * @param abstrMetadata Entity for which data tables are out of sync.
	 */
	protected void logFatalError(Exception exception,
			AbstractMetadataInterface abstrMetadata)
	{

	}

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
	public void rollbackQueries(Stack<String> revQryStack,
			AbstractMetadataInterface abstrMetadata, Exception exception, DAO dao)
			throws DynamicExtensionsSystemException
	{
		abstractMetadataManagerHelper.rollbackDao(dao);
		executeRollbackQueries(revQryStack, abstrMetadata, exception);
	}

	protected void executeRollbackQueries(Stack<String> revQryStack,
			AbstractMetadataInterface abstrMetadata, Exception exception)
			throws DynamicExtensionsSystemException
	{
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
	 * @param dao Hibernate dao.
	 * @return the object by identifier
	 *
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 */
	public static DynamicExtensionBaseDomainObject getObjectByIdentifier(String objectName,
			String identifier, HibernateDAO... dao) throws DynamicExtensionsSystemException
	{
		AbstractBizLogic bizLogic = BizLogicFactory.getDefaultBizLogic();
		DynamicExtensionBaseDomainObject dyExtBsDmnObj = null;
		try
		{
			if (dao != null && dao.length > 0)
			{
				if (abstractMetadataManagerHelper.isNotEmptyString(identifier))
				{
					HibernateDAO hibernateDAO = dao[0];
					List<ColumnValueBean> valBeanList = new ArrayList<ColumnValueBean>();
					ColumnValueBean colValueBean = new ColumnValueBean(DEConstants.OBJ_IDENTIFIER,
							Long.parseLong(identifier));
					valBeanList.add(colValueBean);

					//List objects = hibernateDao.retrieve(className, "name", objectName);

					List objects = hibernateDAO.retrieve(objectName, colValueBean);
					if (objects != null && !objects.isEmpty())
					{
						dyExtBsDmnObj = (DynamicExtensionBaseDomainObject) objects.get(0);
					}

				}
			}
			// After moving to MYSQL 5.2, the type checking is strict so changing the identifier to Long.
			else
			{
				List objects = bizLogic.retrieve(objectName, DEConstants.OBJ_IDENTIFIER, Long
						.valueOf(identifier));

				if (objects == null || objects.isEmpty())
				{
					Logger.out.debug("Required Obejct not found: Object Name*" + objectName
							+ "*   identifier  *" + identifier + "*");
					throw new DynamicExtensionsSystemException("OBJECT_NOT_FOUND");
				}

				dyExtBsDmnObj = (DynamicExtensionBaseDomainObject) objects.get(0);
			}
		}
		catch (BizLogicException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}
		catch (DAOException e)
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
	 * @throws DynamicExtensionsSystemException

	 */
	protected void invokeSetterMethod(Class klass, String property, Class argumentType,
			Object invokeOnObject, Object argument) throws DynamicExtensionsSystemException

	{
		// Method setter = klass.getMethod("set" + property, argumentType);
		// setter.invoke(invokeOnObject, argument);
		try
		{
			abstractMetadataManagerHelper.invokeSetterMethod(klass, property, argumentType,
					invokeOnObject, argument);
		}
		catch (NoSuchMethodException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}
		catch (IllegalAccessException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}
		catch (InvocationTargetException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}
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
	 * @throws DynamicExtensionsSystemException
	 *
	 * @throws NoSuchMethodException
	 *             the no such method exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 */
	protected Object invokeGetterMethod(Class klass, String property, Object invokeOnObject)
			throws DynamicExtensionsSystemException
	{
		Object returnedObject;
		try
		{
			Method getter = klass.getMethod("get" + property);
			returnedObject = getter.invoke(invokeOnObject);
		}
		catch (SecurityException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}
		catch (NoSuchMethodException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}
		catch (IllegalArgumentException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}
		catch (IllegalAccessException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}
		catch (InvocationTargetException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}

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
	public static DynamicExtensionBaseDomainObjectInterface getObjectByName(String className,
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
	public static DynamicExtensionBaseDomainObjectInterface getObjectByName(String className,
			String objectName, HibernateDAO hibernateDao) throws DynamicExtensionsSystemException
	{
		DynamicExtensionBaseDomainObjectInterface dyExtBsDmnObj = null;
		if (abstractMetadataManagerHelper.isNotEmptyString(objectName))
		{
			try
			{
				List<ColumnValueBean> valBeanList = new ArrayList<ColumnValueBean>();
				ColumnValueBean colValueBean = new ColumnValueBean("name", objectName);
				valBeanList.add(colValueBean);

				//List objects = hibernateDao.retrieve(className, "name", objectName);

				List objects = hibernateDao.retrieve(className, colValueBean);
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
	 * This method will return the name of the TargetRole for the given association
	 * with its first letter capitalized.
	 * @param association association whose target role is needed.
	 * @return target role name with first letter capital
	 */
	protected String getTargetRoleNameForMethodInvocation(AssociationInterface association)
	{
		String targetRole = association.getTargetRole().getName();
		targetRole = targetRole.substring(0, 1).toUpperCase()
				+ targetRole.substring(1, targetRole.length());
		return targetRole;
	}

	/**
	 * This method will return the name of the Source for the given association
	 * with its first letter capitalized.
	 * @param association association whose Source role is needed.
	 * @return source role name with first letter capital
	 */
	protected String getSourceRoleNameForMethodInvocation(AssociationInterface association)
	{
		String source = association.getSourceRole().getName();
		source = source.substring(0, 1).toUpperCase() + source.substring(1, source.length());
		return source;
	}

	/**
	 * This method will find out the identifier of the given object
	 * by calling the getId() method on it.
	 * @param newObject object whose id is required.
	 * @return identifier.
	 * @throws NoSuchMethodException exception.
	 * @throws IllegalAccessException exception.
	 * @throws InvocationTargetException exception.
	 */
	public static Long getObjectId(Object newObject) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException
	{
		Long identifier = null;
		Method method = newObject.getClass().getMethod("getId");
		Object object = method.invoke(newObject);
		if (object != null)
		{
			identifier = Long.valueOf(object.toString());
		}
		return identifier;
	}

	/**
	 * This method will create the object of the given class name.
	 * @param className class whose instance is to be created.
	 * @return created object of given class name
	 * @throws ClassNotFoundException exception.
	 * @throws SecurityException exception.
	 * @throws NoSuchMethodException exception.
	 * @throws IllegalArgumentException exception.
	 * @throws InstantiationException exception.
	 * @throws IllegalAccessException exception.
	 * @throws InvocationTargetException exception.
	 */
	protected Object createObjectForClass(String className) throws ClassNotFoundException,
			SecurityException, NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException, InvocationTargetException
	{
		Class newObjectClass = Class.forName(className);
		Constructor constructor = newObjectClass.getConstructor();
		return constructor.newInstance();
	}

	/**
	 * This method will add the TargetObject in the SourceObject where these two objects are
	 * associated by the given association parameter.
	 * It will search the method in the sourceObject having the name of target role of association
	 * & having the targetClassName as its argument type. If association is 1--* then in
	 * that case it will create the new object & will add it to the collection of these
	 * object in the Source object.
	 * @param sourceObject source object in which to add the target object.
	 * @param targetObject object to be added.
	 * @param targetClassName target objects class name.
	 * @param association association between the source & target object.
	 * @throws DynamicExtensionsSystemException
	 * @throws NoSuchMethodException targetClassName
	 * @throws IllegalAccessException targetClassName
	 * @throws InvocationTargetException targetClassName
	 * @throws ClassNotFoundException targetClassName
	 */
	protected void addTargetObject(Object sourceObject, Object targetObject,
			String targetClassName, AssociationInterface association)
			throws DynamicExtensionsSystemException
	{

		try
		{
			Class srcObjectClass = sourceObject.getClass();
			String targetRoleName = getTargetRoleNameForMethodInvocation(association);
			Cardinality targetMaxCardinality = association.getTargetRole().getMaximumCardinality();
			if (targetMaxCardinality == Cardinality.ONE)
			{
				Class tgtObjectClass = Class.forName(targetClassName);
				invokeSetterMethod(srcObjectClass, targetRoleName, tgtObjectClass, sourceObject,
						targetObject);
			}
			else
			{
				Collection<Object> containedObjects = null;
				Object associatedObjects = invokeGetterMethod(srcObjectClass, targetRoleName,
						sourceObject);
				if (associatedObjects == null)
				{
					containedObjects = new HashSet<Object>();
				}
				else
				{
					containedObjects = (Collection) associatedObjects;
				}
				if (!containedObjects.contains(targetObject))
				{
					containedObjects.add(targetObject);
				}
				invokeSetterMethod(srcObjectClass, targetRoleName, Class
						.forName("java.util.Collection"), sourceObject, containedObjects);

			}
		}
		catch (ClassNotFoundException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}

	}

	/**
	 * This method will add the SourceObject in the TargetObject where these two objects are
	 * associated by the given association parameter.
	 * It will search the method in the targetObject having the name of source role of association
	 * & having the sourceClassName as its argument type.
	 * @param sourceObject source object which is to be added in targetObject
	 * @param targetObject target object in which to add
	 * @param sourceClassName class name of the source object
	 * @param association association
	 * @throws DynamicExtensionsSystemException
	 * @throws NoSuchMethodException exception.
	 * @throws IllegalAccessException exception.
	 * @throws InvocationTargetException exception.
	 * @throws ClassNotFoundException exception.
	 */
	protected void addSourceObject(Object sourceObject, Object targetObject,
			String sourceClassName, AssociationInterface association)
			throws DynamicExtensionsSystemException
	{
		try
		{
			Class srcObjectClass = Class.forName(sourceClassName);
			String sourceRoleName = getSourceRoleNameForMethodInvocation(association);
			Class tgtObjectClass = targetObject.getClass();
			invokeSetterMethod(tgtObjectClass, sourceRoleName, srcObjectClass, targetObject,
					sourceObject);
		}
		catch (ClassNotFoundException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}

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
