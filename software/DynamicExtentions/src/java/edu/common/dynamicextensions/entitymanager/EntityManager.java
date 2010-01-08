
package edu.common.dynamicextensions.entitymanager;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import edu.common.dynamicextensions.domain.AbstractAttribute;
import edu.common.dynamicextensions.domain.Attribute;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domain.EntityGroup;
import edu.common.dynamicextensions.domain.FileAttributeRecordValue;
import edu.common.dynamicextensions.domain.FileAttributeTypeInformation;
import edu.common.dynamicextensions.domain.ObjectAttributeTypeInformation;
import edu.common.dynamicextensions.domain.userinterface.SelectControl;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AssociationDisplayAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AssociationControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.AssociationTreeObject;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.dao.query.generator.DBTypes;
import edu.wustl.dao.util.NamedQueryParam;

/**
 *
 * @author mandar_shidhore
 * @author kunal_kamble
 * @author rajesh_patil
 */
public class EntityManager extends AbstractMetadataManager implements EntityManagerInterface
{

    /**
     * Static instance of the entity manager.
     */
    private static EntityManagerInterface manager = null;

    /**
     * Static instance of the queryBuilder.
     */
    private static DynamicExtensionBaseQueryBuilder queryBuilder = null;

    /**
     * Instance of entity manager util class
     */
    EntityManagerUtil entityManagerUtil = new EntityManagerUtil();

    /**
     * Empty Constructor.
     */
    protected EntityManager()
    {
        super();
    }

    /**
     * Returns the instance of the Entity Manager.
     * @return entityManager singleton instance of the Entity Manager.
     */
    public static synchronized EntityManagerInterface getInstance()
    {
        if (manager == null)
        {
            manager = new EntityManager();
            DynamicExtensionsUtility.initialiseApplicationVariables();
            queryBuilder = QueryBuilderFactory.getQueryBuilder();
        }

        return manager;
    }

    /**
     * Mock entity manager can be placed in the entity manager using this method.
     * @param entityManagerIntf
     */
    public static void setInstance(EntityManagerInterface entManager)
    {
        EntityManager.manager = entManager;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.AbstractMetadataManager#getQueryBuilderInstance()
     */
    @Override
    protected DynamicExtensionBaseQueryBuilder getQueryBuilderInstance()
    {
        return queryBuilder;
    }

    /**
     * Persists the entity to the database. Also creates the dynamic tables and associations
     * between those tables using the meta data information in the entity object.
     * @param entity entity to be persisted
     * @return entity persisted entity
     * @throws DynamicExtensionsSystemException
     * @throws DynamicExtensionsApplicationException
     */
    public EntityInterface persistEntity(EntityInterface entity)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        List<String> revQueries = new LinkedList<String>();
        List<String> queries = new ArrayList<String>();
        Stack<String> rlbkQryStack = new Stack<String>();
        HibernateDAO hibernateDAO = null;

        try
        {
            hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();
            preProcess(entity, revQueries, queries);
            if (entity.getId() == null)
            {
                hibernateDAO.insert(entity);
            }
            else
            {
                hibernateDAO.update(entity);
            }

            postProcess(queries, revQueries, rlbkQryStack);

            hibernateDAO.commit();

        }
        catch (DAOException e)
        {
            rollbackQueries(rlbkQryStack, entity, e, hibernateDAO);
            throw new DynamicExtensionsSystemException(e.getMessage(), e, DYEXTN_S_003);
        }
        finally
        {
            try
            {
                DynamicExtensionsUtility.closeHibernateDAO(hibernateDAO);
            }
            catch (DAOException e)
            {
                throw new DynamicExtensionsSystemException(e.getMessage(), e, DYEXTN_S_003);
            }
        }

        return entity;
    }

    /**
     * This method is used to save the meta data information
     * of the given entity without creating its data table.
     * @param entityInterface entity to be persisted
     * @return entity persisted entity
     */
    public EntityInterface persistEntityMetadata(EntityInterface entity)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        Stack<String> rlbkQryStack = new Stack<String>();
        HibernateDAO hibernateDAO = null;
        try
        {
            hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();
            if (entity.getId() == null)
            {
                hibernateDAO.insert(entity);
            }
            else
            {
                hibernateDAO.update(entity);
            }

            hibernateDAO.commit();
        }
        catch (DAOException e)
        {
            rollbackQueries(rlbkQryStack, entity, e, hibernateDAO);
            throw new DynamicExtensionsSystemException(e.getMessage(), e, DYEXTN_S_003);
        }
        finally
        {
            try
            {
                DynamicExtensionsUtility.closeHibernateDAO(hibernateDAO);
            }
            catch (DAOException e)
            {
                throw new DynamicExtensionsSystemException(e.getMessage(), e, DYEXTN_S_003);
            }
        }

        return entity;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.AbstractMetadataManager#preProcess(edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface, java.util.List, java.util.List)
     */
    @Override
    protected void preProcess(DynamicExtensionBaseDomainObjectInterface dyExtBsDmnObj,
            List<String> revQueries, List<String> queries) throws DynamicExtensionsSystemException,
            DynamicExtensionsApplicationException
    {
        EntityInterface entityObj = (EntityInterface) dyExtBsDmnObj;
        createDynamicQueries(entityObj, revQueries, queries);
    }

    /**
     * @param entity
     * @throws DynamicExtensionsSystemException
     * @throws DynamicExtensionsApplicationException
     */
    private void checkParentChangeAllowed(Entity entity) throws DynamicExtensionsSystemException,
            DynamicExtensionsApplicationException
    {
        String tableName = entity.getTableProperties().getName();
        if (queryBuilder.isDataPresent(tableName))
        {
            throw new DynamicExtensionsApplicationException(
                    "Can not change the data type of the attribute", null, DYEXTN_A_010);
        }
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.AbstractMetadataManager#postProcess(java.util.List, java.util.List, java.util.Stack)
     */
    @Override
    protected void postProcess(List<String> queries, List<String> revQueries,
            Stack<String> rlbkQryStack) throws DynamicExtensionsSystemException
    {
        queryBuilder.executeQueries(queries, revQueries, rlbkQryStack);
    }

    /**
     * This method creates dynamic queries.
     * @param entity
     * @param revQueries
     * @param queries
     * @return
     * @throws DynamicExtensionsSystemException
     * @throws DynamicExtensionsApplicationException
     */
    private List<String> createDynamicQueries(EntityInterface entity, List<String> revQueries,
            List<String> queries) throws DynamicExtensionsSystemException,
            DynamicExtensionsApplicationException
    {
        return getDynamicQueryList(entity.getEntityGroup(), revQueries, queries);
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.AbstractMetadataManager#LogFatalError(java.lang.Exception, edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface)
     */
    @Override
    protected void logFatalError(Exception exception, AbstractMetadataInterface abstrMetadata)
    {
        String table = "";
        String name = "";
        if (abstrMetadata != null)
        {
            EntityInterface entity = (EntityInterface) abstrMetadata;
            entity.getTableProperties().getName();
            name = entity.getName();
        }

        Logger.out
                .error("***Fatal Error.. Inconsistent data table and metadata information for the entity -"
                        + name + "***");
        Logger.out.error("Please check the table -" + table);
        Logger.out.error("The cause of the exception is - " + exception.getMessage());
        Logger.out.error("The detailed log is : ");

    }

    /**
     * Returns a collection of association objects given the source entity id and
     * target entity id.
     * @param srcEntityId source entity Id
     * @param tgtEntityId target entity Id
     * @return associations collection of association
     * @throws DynamicExtensionsSystemException
     * @throws DynamicExtensionsApplicationException
     */
    public Collection<AssociationInterface> getAssociations(Long srcEntityId, Long tgtEntityId)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, srcEntityId));
        substParams.put("1", new NamedQueryParam(DBTypes.LONG, tgtEntityId));

        // Following method is called to execute the stored HQL, the name of which is given as
        // the first parameter. The second parameter is the map which contains the actual values
        // that are replaced for the place holders.
        Collection<AssociationInterface> associations = executeHQL("getAssociations", substParams);

        return associations;
    }

    /**
     * Returns a collection of association objects given the source entity id and
     * target entity id.
     * @param srcEntityId
     * @param tgtEntityId
     * @param hibernatedao
     * @return
     * @throws DynamicExtensionsSystemException
     * @throws DynamicExtensionsApplicationException
     */
    public Collection<AssociationInterface> getAssociations(Long srcEntityId, Long tgtEntityId,
            HibernateDAO hibernatedao) throws DynamicExtensionsSystemException,
            DynamicExtensionsApplicationException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, srcEntityId));
        substParams.put("1", new NamedQueryParam(DBTypes.LONG, tgtEntityId));

        // Following method is called to execute the stored HQL, the name of which is given as
        // the first parameter. The second parameter is the map which contains the actual values
        // that are replaced for the place holders.
        Collection<AssociationInterface> associations = executeHQL(hibernatedao, "getAssociations",
                substParams);

        return associations;
    }

    /**
     * Returns a collection of association identifiers given the source entity id and
     * target entity id.
     * @param srcEntityId source entity Id
     * @param tgtEntityId target entity Id
     * @return associations collection of association
     * @throws DynamicExtensionsSystemException
     * @throws DynamicExtensionsApplicationException
     */
    public Collection<Long> getAssociationIds(Long srcEntityId, Long tgtEntityId)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, srcEntityId));
        substParams.put("1", new NamedQueryParam(DBTypes.LONG, tgtEntityId));

        // Following method is called to execute the stored HQL, the name of which is given as
        // the first parameter. The second parameter is the map which contains the actual values
        // that are replaced for the place holders.
        Collection<Long> associationIds = executeHQL("getAssociationIds", substParams);

        return associationIds;
    }

    /**
     * Returns an entity object given the entity name;
     * @param entityName entity name
     * @return entity
     * @throws DynamicExtensionsSystemException
     * @throws DynamicExtensionsApplicationException
     */
    public EntityInterface getEntityByName(String entityName)
            throws DynamicExtensionsSystemException
    {
        EntityInterface entity = (EntityInterface) getObjectByName(Entity.class.getName(),
                entityName);

        return entity;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getEntityByName(java.lang.String, edu.wustl.dao.HibernateDAO)
     */
    public EntityInterface getEntityByName(String entityName, HibernateDAO hibernateDAO)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        EntityInterface entity = (EntityInterface) getObjectByName(Entity.class.getName(),
                entityName, hibernateDAO);

        return entity;
    }

    /**
     * Returns an association object given the entity name and source role name.
     * @param entityName
     * @param srcRoleName
     * @return associations collection of association
     * @throws DynamicExtensionsSystemException
     * @throws DynamicExtensionsApplicationException
     */
    public Collection<AssociationInterface> getAssociation(String entityName, String srcRoleName)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.STRING, entityName));
        substParams.put("1", new NamedQueryParam(DBTypes.STRING, srcRoleName));

        // Following method is called to execute the stored HQL, the name of which is given as
        // the first parameter. The second parameter is the map which contains the actual values
        // that are replaced for the place holders.
        Collection<AssociationInterface> associations = executeHQL("getAssociation", substParams);

        return associations;
    }

    /**
     * Returns an association object given the association name.
     * @param assoName name of association
     * @return association
     * @throws DynamicExtensionsSystemException
     * @throws DynamicExtensionsApplicationException
     */
    public AssociationInterface getAssociationByName(String assoName)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.STRING, assoName));

        // Following method is called to execute the stored HQL, the name of which is given as
        // the first parameter. The second parameter is the map which contains the actual values
        // that are replaced for the place holders.
        Collection<AssociationInterface> associations = executeHQL("getAssociationByName",
                substParams);
        AssociationInterface association = null;
        if (associations != null && !associations.isEmpty())
        {
            association = associations.iterator().next();
        }

        return association;
    }

    /**
     * @param srcEntName source entity name
     * @param assoName association name
     * @param tgtEntName target entity name
     * @return association
     * @throws DynamicExtensionsSystemException
     * @throws DynamicExtensionsApplicationException
     */
    public AssociationInterface getAssociation(String srcEntName, String assoName, String tgtEntName)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.STRING, srcEntName));
        substParams.put("1", new NamedQueryParam(DBTypes.STRING, assoName));
        substParams.put("2", new NamedQueryParam(DBTypes.STRING, tgtEntName));

        // Following method is called to execute the stored HQL, the name of which is given as
        // the first parameter. The second parameter is the map which contains the actual values
        // that are replaced for the place holders.
        Collection<AssociationInterface> associations = executeHQL(
                "getAssociationBySourceTargetEntity", substParams);

        AssociationInterface association = null;
        if (associations != null && !associations.isEmpty())
        {
            association = associations.iterator().next();
        }

        return association;
    }

    /**
     * Returns a collection of entities given the entity concept code.
     * @param conceptCode
     * @return entities collection of entities
     * @throws DynamicExtensionsSystemException
     * @throws DynamicExtensionsApplicationException
     */
    public Collection<EntityInterface> getEntitiesByConceptCode(String conceptCode)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.STRING, conceptCode));

        // Following method is called to execute the stored HQL, the name of which is given as
        // the first parameter. The second parameter is the map which contains the actual values
        // that are replaced for the place holders.
        Collection<EntityInterface> entities = executeHQL("getEntitiesByConceptCode", substParams);

        return entities;
    }

    /**
     * Returns all entities in the whole system
     * @return collection of entities
     * @throws DynamicExtensionsSystemException
     * @throws DynamicExtensionsApplicationException
     */
    public Collection<EntityInterface> getAllEntities() throws DynamicExtensionsSystemException,
            DynamicExtensionsApplicationException
    {
        // Calling generic method to return all entities.
        return getAllObjects(EntityInterface.class.getName());
    }

    /**
     * Returns a single  entity for given identifier
     * @param identifier id of entity
     * @return entity
     * @throws DynamicExtensionsSystemException
     * @throws DynamicExtensionsApplicationException
     */
    public EntityInterface getEntityByIdentifier(Long identifier)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        // Calling generic method to return entity with a particular identifier.
        return (EntityInterface) getObjectByIdentifier(EntityInterface.class.getName(), identifier
                .toString());
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getEntityByIdentifier(java.lang.String)
     */
    public EntityInterface getEntityByIdentifier(String identifier)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        // Calling generic method to return entity with a particular identifier.
        return (EntityInterface) getObjectByIdentifier(EntityInterface.class.getName(), identifier);
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getAllContainers()
     */
    public Collection<ContainerInterface> getAllContainers()
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        // Calling generic method to return all containers.
        return getAllObjects(ContainerInterface.class.getName());
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getAllContainersByEntityGroupId(java.lang.Long)
     */
    public Collection<ContainerInterface> getAllContainersByEntityGroupId(Long entGroupId)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, entGroupId));

        return executeHQL("getAllContainersByEntityGroupId", substParams);
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#insertData(edu.common.dynamicextensions.domaininterface.EntityInterface, java.util.Map, java.lang.Long[])
     */
    public Long insertData(EntityInterface entity,
            Map<AbstractAttributeInterface, Object> dataValue, HibernateDAO hibernateDao,
            Long... userId) throws DynamicExtensionsApplicationException,
            DynamicExtensionsSystemException
    {
        List<Map<AbstractAttributeInterface, Object>> dataValMaps = new ArrayList<Map<AbstractAttributeInterface, Object>>();
        dataValMaps.add(dataValue);

//        Long usrId = ((userId != null && userId.length != 0) ? userId[0] : null);

        Set<Object> auditableDEObjects = new HashSet<Object>();

        Long identifier = null;
        HibernateDAO hibernateDAO = hibernateDao;
        try
        {
            if (hibernateDAO == null)
            {
                hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();
            }
            Object newObject = createObject(entity, dataValue, auditableDEObjects);
            hibernateDAO.insert(newObject);
            Method method = newObject.getClass().getMethod("getId");
            Object object = method.invoke(newObject);
            identifier = Long.valueOf(object.toString());

            /*// Get audit manager from DefaultBizLogic.
            DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
            SessionDataBean sessionDataBean = new SessionDataBean();
            AuditManager auditManager = defaultBizLogic.getAuditManager(sessionDataBean);

            // Audit the objects being inserted.
            for (Object obj : auditableDEObjects)
            {
                System.out.println(obj);
                sessionDataBean.setUserId(usrId);

                auditManager.insertAudit(hibernateDAO, obj);
            }*/

            if (hibernateDao == null)
            {
                hibernateDAO.commit();
            }
        }
        catch (Exception e)
        {
            throw (DynamicExtensionsSystemException) handleRollback(e,
                    DATA_INSERTION_ERROR_MESSAGE, hibernateDAO, true);
        }
        finally
        {
            if (hibernateDao == null && hibernateDAO != null)
            {
                try
                {
                    DynamicExtensionsUtility.closeHibernateDAO(hibernateDAO);
                }
                catch (DAOException e)
                {
                    throw (DynamicExtensionsSystemException) handleRollback(e,
                            "Error while closing", hibernateDAO, true);
                }
            }
        }

        return identifier;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#editData(edu.common.dynamicextensions.domaininterface.EntityInterface, java.util.Map, java.lang.Long, java.lang.Long[])
     */
    public boolean editData(EntityInterface entity, Map<AbstractAttributeInterface, ?> dataValue,
            Long recordId, HibernateDAO hibernateDao, Long... userId)
            throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
    {
        boolean isSuccess = false;
//        Long usrId = ((userId != null && userId.length != 0) ? userId[0] : null);

        HibernateDAO hibernateDAO = hibernateDao;

        try
        {
            if (hibernateDAO == null)
            {
                hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();
            }

            Map<AbstractAttributeInterface, Object> dataVal = (Map<AbstractAttributeInterface, Object>) dataValue;

            String packageName = null;
            packageName = getPackageName(entity, packageName);

            String className = packageName + "." + entity.getName();

            Object oldObject = null;
            if (hibernateDao == null)
            {
                List retrievedObjects = hibernateDAO.retrieve(className, "id", recordId);
                oldObject = retrievedObjects.get(0);
            }
            else
            {
                Object obj = hibernateDAO.retrieveById(className, recordId);
                oldObject = obj;
            }

            //Object originalTopLevelObject = copyOriginalObjectState(oldObject);

            Set<Map<String, Object>> auditableObjects = new HashSet<Map<String, Object>>();

            Object updatedObject = updateObject(entity, dataVal, oldObject, hibernateDAO,
                    auditableObjects);
            hibernateDAO.update(updatedObject);

            isSuccess = true;

            /*// Get audit manager from DefaultBizLogic.
            DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
            SessionDataBean sessionDataBean = new SessionDataBean();

            AuditManager auditManager = defaultBizLogic.getAuditManager(sessionDataBean);

            for (Map<String, Object> mapofAuditableObjects : auditableObjects)
            {
                Object oldObj = mapofAuditableObjects
                        .get(edu.common.dynamicextensions.ui.util.Constants.OLD_OBJECT);
                Object updatedObj = mapofAuditableObjects
                        .get(edu.common.dynamicextensions.ui.util.Constants.UPDATED_OBJECT);

                auditManager.updateAudit(hibernateDAO, updatedObj, oldObj);
            }*/

            if (hibernateDao == null)
            {
                hibernateDAO.commit();
            }
        }
        catch (DynamicExtensionsApplicationException e)
        {
            throw (DynamicExtensionsApplicationException) handleRollback(e,
                    DATA_INSERTION_ERROR_MESSAGE, hibernateDAO, false);
        }
        catch (DAOException e)
        {
            throw new DynamicExtensionsSystemException("Error while updating records!", e);
        }
        /*catch (AuditException e)
        {
            throw new DynamicExtensionsSystemException("Error while updating records!", e);
        }*/
        finally
        {
            if (hibernateDao == null && hibernateDAO != null)
            {
                try
                {
                    DynamicExtensionsUtility.closeHibernateDAO(hibernateDAO);
                }
                catch (DAOException e)
                {
                    throw (DynamicExtensionsSystemException) handleRollback(e,
                            "Error while closing", hibernateDAO, true);
                }
            }
        }

        return isSuccess;
    }

    /**
     * This constructs a new object from the data in the map.
     * @param entity
     * @param dataValue
     * @param auditableDEObjects
     * @return
     * @throws DynamicExtensionsApplicationException
     */
    private Object createObject(EntityInterface entity,
            Map<AbstractAttributeInterface, Object> dataValue, Set<Object> auditableDEObjects)
            throws DynamicExtensionsApplicationException
    {
        String packageName = null;
        packageName = getPackageName(entity, packageName);

        Object newObject = null;

        try
        {
            // Create a new instance.
            Class newObjectClass = Class.forName(packageName + "." + entity.getName());
            Constructor constructor = newObjectClass.getConstructor();
            newObject = constructor.newInstance();

            // If empty, insert row with only identifier column value.
            if (dataValue == null)
            {
                dataValue = new HashMap();
            }

            Set uiColumnSet = dataValue.keySet();
            Iterator uiColumnSetIter = uiColumnSet.iterator();
            while (uiColumnSetIter.hasNext())
            {
                AbstractAttribute attribute = (AbstractAttribute) uiColumnSetIter.next();

                if (attribute instanceof AssociationInterface)
                {
                    AssociationInterface association = (AssociationInterface) attribute;

                    EntityInterface baseEntity = association.getEntity();
                    String baseEntClassName = packageName + "." + baseEntity.getName();
                    EntityInterface targetEntity = association.getTargetEntity();

                    Cardinality targetMaxCardinality = association.getTargetRole()
                            .getMaximumCardinality();
                    String targetRole = association.getTargetRole().getName();
                    targetRole = targetRole.substring(0, 1).toUpperCase()
                            + targetRole.substring(1, targetRole.length());

                    Set<Object> containedObjects = new HashSet<Object>();

                    Object value = dataValue.get(attribute);

                    List<Map> listOfMapsForContainedEntity = (List) value;
                    for (Map valueMapForContainedEntity : listOfMapsForContainedEntity)
                    {
                        Object associatedObject = createObject(targetEntity,
                                valueMapForContainedEntity, auditableDEObjects);

                        Class assoObjectClass = associatedObject.getClass();

                        String source = association.getSourceRole().getName();
                        source = source.substring(0, 1).toUpperCase()
                                + source.substring(1, source.length());

                        invokeSetterMethod(assoObjectClass, source,
                                Class.forName(baseEntClassName), associatedObject, newObject);

                        if (targetMaxCardinality == Cardinality.ONE)
                        {
                            // Add the object.
                            invokeSetterMethod(newObjectClass, targetRole, associatedObject
                                    .getClass(), newObject, associatedObject);
                            break;
                        }
                        else
                        {
                            containedObjects.add(associatedObject);
                        }
                    }

                    if (targetMaxCardinality == Cardinality.MANY)
                    {
                        // Add the collection.
                        invokeSetterMethod(newObjectClass, targetRole, Class
                                .forName("java.util.Collection"), newObject, containedObjects);
                    }
                }
                else
                {
                    String dataType = ((AttributeMetadataInterface) attribute)
                            .getAttributeTypeInformation().getDataType();

                    newObject = setObjectProperty(attribute, dataType, newObjectClass, dataValue,
                            newObject);
                }
            }
        }
        catch (ClassNotFoundException exception)
        {
            throw new DynamicExtensionsApplicationException(
                    "Exception encountered during data entry!", exception);
        }
        catch (Exception exception)
        {
            throw new DynamicExtensionsApplicationException(
                    "Exception encountered during data entry!", exception);
        }

        // Add the object to the set of auditable DE objects, for auditing.
        //auditableDEObjects.add(newObject);

        return newObject;
    }

    /**
     * This updates the existing object with data in the map.
     * @param entity
     * @param dataValue
     * @param oldObject
     * @param hibernateDAO
     * @param auditableObjects
     * @param auditableDEObjects
     * @return
     * @throws DynamicExtensionsApplicationException
     */
    private Object updateObject(EntityInterface entity,
            Map<AbstractAttributeInterface, Object> dataValue, Object oldObject,
            HibernateDAO hibernateDAO, Set<Map<String, Object>> auditableObjects)
            throws DynamicExtensionsApplicationException
    {
        String packageName = null;
        packageName = getPackageName(entity, packageName);

        try
        {
            Class oldObjectClass = oldObject.getClass();

            // If empty, insert row with only identifier column value.
            if (dataValue == null)
            {
                dataValue = new HashMap();
            }

            Set uiColumnSet = dataValue.keySet();
            Iterator uiColumnSetIter = uiColumnSet.iterator();
            while (uiColumnSetIter.hasNext())
            {
                AbstractAttribute attribute = (AbstractAttribute) uiColumnSetIter.next();

                if (attribute instanceof AssociationInterface)
                {
                    AssociationInterface association = (AssociationInterface) attribute;

                    EntityInterface baseEntity = association.getEntity();
                    String baseEntityClassName = packageName + "." + baseEntity.getName();

                    EntityInterface targetEntity = association.getTargetEntity();
                    Cardinality targetMaxCardinality = association.getTargetRole()
                            .getMaximumCardinality();
                    String targetRole = association.getTargetRole().getName();
                    targetRole = targetRole.substring(0, 1).toUpperCase()
                            + targetRole.substring(1, targetRole.length());

                    Collection<Object> containedObjects = null;

                    // Get the associated object(s).
                    Object associatedObjects = invokeGetterMethod(oldObjectClass, targetRole,
                            oldObject);

                    if (targetMaxCardinality != Cardinality.ONE)
                    {
                        if (associatedObjects != null)
                        {
                            containedObjects = (Collection) associatedObjects;
                        }
                        else
                        {
                            containedObjects = new HashSet<Object>();
                        }
                    }

                    Set<Object> objectsToBeRetained = new HashSet<Object>();

                    Object value = dataValue.get(attribute);
                    List<Map> listOfMapsForContainedEntity = (List) value;

                    for (Map valueMapForContainedEntity : listOfMapsForContainedEntity)
                    {
                        boolean isNew = false;
                        Object objForUpdate = null;

                        if (targetMaxCardinality == Cardinality.ONE)
                        {
                            objForUpdate = associatedObjects;
                        }
                        else
                        {
                            edu.common.dynamicextensions.domain.EntityRecord entityRecord = new edu.common.dynamicextensions.domain.EntityRecord();
                            Long recordId = (Long) valueMapForContainedEntity.get(entityRecord);

                            if (recordId != null)
                            {
                                for (Object obj : containedObjects)
                                {
                                    Method method = obj.getClass().getMethod("getId");
                                    Object object = method.invoke(obj);
                                    Long identifier = Long.valueOf(object.toString());

                                    if (identifier.intValue() == recordId.intValue())
                                    {
                                        objForUpdate = obj;
                                        objectsToBeRetained.add(objForUpdate);

                                        break;
                                    }
                                }
                            }
                        }

                        if (objForUpdate == null)
                        {
                            String targetEntityClassName = packageName + "."
                                    + targetEntity.getName();
                            Class associatedClass = Class.forName(targetEntityClassName);

                            Constructor constructor = associatedClass.getConstructor();
                            objForUpdate = constructor.newInstance();
                            isNew = true;
                        }

                        //Map<String, Object> audtblObjects = new HashMap<String, Object>();
                        //audtblObjects.put(edu.common.dynamicextensions.ui.util.Constants.OLD_OBJECT, objForUpdate);

                        objForUpdate = updateObject(targetEntity, valueMapForContainedEntity,
                                objForUpdate, hibernateDAO, auditableObjects);

                        //audtblObjects.put(edu.common.dynamicextensions.ui.util.Constants.UPDATED_OBJECT, objForUpdate);
                        //auditableObjects.add(audtblObjects);

                        Class assoObjectClass = objForUpdate.getClass();

                        String source = association.getSourceRole().getName();
                        source = source.substring(0, 1).toUpperCase()
                                + source.substring(1, source.length());

                        invokeSetterMethod(assoObjectClass, source, Class
                                .forName(baseEntityClassName), objForUpdate, oldObject);

                        if (targetMaxCardinality == Cardinality.ONE)
                        {
                            invokeSetterMethod(oldObjectClass, targetRole, objForUpdate.getClass(),
                                    oldObject, objForUpdate);
                            break;
                        }
                        else
                        {
                            if (isNew)
                            {
                                containedObjects.add(objForUpdate);
                                objectsToBeRetained.add(objForUpdate);
                            }
                        }
                    }

                    if (containedObjects != null)
                    {
                        List objectsToBeRemoved = new ArrayList();
                        Iterator iterator = containedObjects.iterator();
                        while (iterator.hasNext())
                        {
                            objectsToBeRemoved.add(iterator.next());
                        }

                        containedObjects.removeAll(objectsToBeRemoved);
                        containedObjects.addAll(objectsToBeRetained);
                    }

                    if (targetMaxCardinality == Cardinality.MANY)
                    {
                        invokeSetterMethod(oldObjectClass, targetRole, Class
                                .forName("java.util.Collection"), oldObject, containedObjects);
                    }
                }
                else if (attribute instanceof AttributeInterface)
                {
                    String dataType = ((AttributeMetadataInterface) attribute)
                            .getAttributeTypeInformation().getDataType();
                    oldObject = setObjectProperty(attribute, dataType, oldObjectClass, dataValue,
                            oldObject);
                }
            }
        }
        catch (Exception exception)
        {
            throw new DynamicExtensionsApplicationException(
                    "Exception encountered during editing data!", exception);
        }

        return oldObject;
    }






    /**
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getRecordById(edu.common.dynamicextensions.domaininterface.EntityInterface, java.lang.Long)
     * Value in the map depends on the type of the attribute as explained below.<br>
     * Map
     *    key    - Attribute Name
     *    Value  - List<String> --           multi select attribute.
     *             FileAttributeRecordValue  File attribute.
     *             List<Long>                Association
     *                  if One-One   |____   List will contain only 1 record id that is of target entity's record
     *                     Many-One  |
     *                  otherwise it will contains one or more record identifiers.
     *
     *             String                    Other attribute type.
     */
    public Map<AbstractAttributeInterface, Object> getEntityRecordById(EntityInterface entity,
            Long recordId, JDBCDAO... dao) throws DynamicExtensionsSystemException,
            DynamicExtensionsApplicationException
    {
        Map<AbstractAttributeInterface, Object> recordValues = new HashMap<AbstractAttributeInterface, Object>();

        Collection attributes = entity.getAttributeCollection();
        attributes = entityManagerUtil.filterSystemAttributes(attributes);

        String tableName = entity.getTableProperties().getName();

        List<String> selColNames = new ArrayList<String>();

        Map<String, AttributeInterface> colNames = new HashMap<String, AttributeInterface>();

        Iterator attrIter = attributes.iterator();
        while (attrIter.hasNext())
        {
            AttributeInterface attribute = (AttributeInterface) attrIter.next();
            String dbColumnName = null;

            // For other attributes, create select query.
            if (attribute.getAttributeTypeInformation() instanceof FileAttributeTypeInformation)
            {
                dbColumnName = attribute.getColumnProperties().getName() + UNDERSCORE + FILE_NAME;
            }
            else
            {
                dbColumnName = attribute.getColumnProperties().getName();
            }

            selColNames.add(dbColumnName);
            colNames.put(dbColumnName, attribute);
        }

        // Get association values.
        recordValues.putAll(queryBuilder.getAssociationGetRecordQueryList(entity, recordId, dao));

        try
        {
            if (!selColNames.isEmpty())
            {
                StringBuffer query = new StringBuffer();
                query.append(SELECT_KEYWORD).append(WHITESPACE);

                for (int i = 0; i < selColNames.size(); i++)
                {
                    if (i != 0)
                    {
                        query.append(" , ");
                    }

                    query.append(selColNames.get(i));
                }

                query.append(WHITESPACE).append(FROM_KEYWORD).append(WHITESPACE).append(tableName)
                        .append(WHITESPACE).append(WHERE_KEYWORD).append(WHITESPACE).append(
                                IDENTIFIER).append(EQUAL).append(QUESTION_MARK);
                LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
                queryDataList.add(new ColumnValueBean(IDENTIFIER, recordId));
                recordValues
                        .putAll(getAttributeValues(selColNames, query.toString(),queryDataList, colNames, dao));
            }
        }
        catch (DAOException e)
        {
            throw new DynamicExtensionsSystemException("Error while retrieving the data", e);
        }

        return recordValues;
    }

   /**
     * @param selColNames
     * @param query
     * @param columnNames
     * @return
     * @throws DynamicExtensionsSystemException
     * @throws SQLException
     * @throws DAOException
     */
    private Map<AbstractAttributeInterface, Object> getAttributeValues(List<String> selColNames,
            String query, LinkedList<ColumnValueBean> queryDataList , Map<String, AttributeInterface> columnNames, JDBCDAO... dao)
            throws DynamicExtensionsSystemException, DAOException
    {
        Map<AbstractAttributeInterface, Object> records = new HashMap<AbstractAttributeInterface, Object>();

        ResultSet resultSet = null;
        JDBCDAO jdbcDao = null;
        try
        {
            if (dao != null && dao.length > 0)
            {
                jdbcDao = dao[0];
            }
            else
            {
                jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
            }

            resultSet = jdbcDao.getResultSet(query,queryDataList,null);

            if (resultSet.next())
            {
                for (int i = 0; i < selColNames.size(); i++)
                {
                    String dbColumnName = selColNames.get(i);
                    Object value = getValueFromResultSet(resultSet, columnNames, dbColumnName, i);
                    Attribute attribute = (Attribute) columnNames.get(dbColumnName);
                    records.put(attribute, value);
                }
            }
        }
        catch (SQLException e)
        {
            throw new DynamicExtensionsSystemException(e.getMessage(), e);
        }
        catch (IOException e)
        {
            throw new DynamicExtensionsSystemException(e.getMessage(), e);
        }
        catch (ClassNotFoundException e)
        {
            throw new DynamicExtensionsSystemException(e.getMessage(), e);
        }
        finally
        {
            try
            {
                jdbcDao.closeStatement(resultSet);

                if (dao != null && dao.length > 0)
                {
                    Logger.out.info("Dao passed by calling method....");
                }
                else
                {
                    DynamicExtensionsUtility.closeJDBCDAO(jdbcDao);
                }
            }
            catch (DAOException e)
            {
                throw new DynamicExtensionsSystemException(e.getMessage(), e);
            }
        }

        return records;
    }

    /**
     * @param resultSet
     * @param columnNames
     * @param dbColumnName
     * @param index
     * @return
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private Object getValueFromResultSet(ResultSet resultSet,
            Map<String, AttributeInterface> columnNames, String dbColumnName, int index)
            throws SQLException, IOException, ClassNotFoundException,
            DynamicExtensionsSystemException
    {
        Attribute attribute = (Attribute) columnNames.get(dbColumnName);

        Object valueObj = resultSet.getObject(index + 1);
        Object value = "";

        if (valueObj != null)
        {
            if (valueObj instanceof java.util.Date)
            {
                DateAttributeTypeInformation dateAttrTypInfo = (DateAttributeTypeInformation) attribute
                        .getAttributeTypeInformation();

                String format = DynamicExtensionsUtility.getDateFormat(dateAttrTypInfo.getFormat());

                valueObj = resultSet.getTimestamp(index + 1);

                SimpleDateFormat sdFormatter = new SimpleDateFormat(format, CommonServiceLocator
                        .getInstance().getDefaultLocale());
                value = sdFormatter.format((java.util.Date) valueObj);
            }
            else
            {
                if (attribute.getAttributeTypeInformation() instanceof ObjectAttributeTypeInformation)
                {
                    value = queryBuilder.convertValueToObject(valueObj);
                }
                else
                {
                    value = valueObj;
                }
            }
        }

        // All objects on the UI are handled as String, so string values
        // of objects need to be stored in the map.
        if (!(((AttributeInterface) attribute).getAttributeTypeInformation() instanceof FileAttributeTypeInformation)
                && !(((AttributeInterface) attribute).getAttributeTypeInformation() instanceof ObjectAttributeTypeInformation))
        {
            value = value.toString();
        }

        return value;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getRecordById(edu.common.dynamicextensions.domaininterface.EntityInterface, java.lang.Long)
     */
    public Map<AbstractAttributeInterface, Object> getRecordById(EntityInterface entity,
            Long recordId) throws DynamicExtensionsSystemException,
            DynamicExtensionsApplicationException
    {
        Map<AbstractAttributeInterface, Object> recordValues;
        HibernateDAO hibernateDAO = null;
        try
        {
            hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();
            if (entity == null || entity.getId() == null || recordId == null)
            {
                throw new DynamicExtensionsSystemException("Invalid Input");
            }
            recordValues = getRecordForSingleEntity(entity, recordId);
            hibernateDAO.commit();
        }
        catch (DynamicExtensionsApplicationException e)
        {
            throw (DynamicExtensionsApplicationException) handleRollback(e,
                    "Error while Retrieving  data", hibernateDAO, false);
        }
        catch (DAOException e)
        {
            throw (DynamicExtensionsSystemException) handleRollback(e,
                    "Error while Retrieving  data", hibernateDAO, true);
        }
        finally
        {
            try
            {
                DynamicExtensionsUtility.closeHibernateDAO(hibernateDAO);
            }
            catch (DAOException e)
            {
                throw (DynamicExtensionsSystemException) handleRollback(e, "Error while closing",
                        hibernateDAO, true);
            }
        }
        return recordValues;
    }

    /**
     * This method return all the records based on entity
     * @param entity Identifier of entity
     * @param recordId recordId of entity
     * @return recordValues for the entity
     * @throws DynamicExtensionsSystemException fails to get entity record
     * @throws DynamicExtensionsApplicationException fails to get entity record
     */
    public Map<AbstractAttributeInterface, Object> getRecordForSingleEntity(EntityInterface entity,
            Long recordId) throws DynamicExtensionsSystemException,
            DynamicExtensionsApplicationException
    {
        Map<AbstractAttributeInterface, Object> recordValues = new HashMap<AbstractAttributeInterface, Object>();
        EntityInterface parentEntity = entity;
        do
        {
            Map<AbstractAttributeInterface, Object> recForSingleEnt = getEntityRecordById(
                    parentEntity, recordId);
            recordValues.putAll(recForSingleEnt);
            parentEntity = parentEntity.getParentEntity();
        }
        while (parentEntity != null);

        return recordValues;
    }

   /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getRecordsForAssociationControl(edu.common.dynamicextensions.domaininterface.userinterface.AssociationControlInterface)
     */
    public Map<Long, List<String>> getRecordsForAssociationControl(
            AssociationControlInterface assoControl) throws DynamicExtensionsSystemException
    {
        Map<Long, List<String>> assoRecords = new HashMap<Long, List<String>>();
        List<String> tableNames = new ArrayList<String>();
        String tableName;
        String tgtEntityTable = "";
        String columnName;
        String onClause = ON_KEYWORD;

        int counter = 0;
        boolean areMultipleAttr = false;

        Collection<AssociationDisplayAttributeInterface> assoAttributes = assoControl
                .getAssociationDisplayAttributeCollection();
        if (assoAttributes != null && !assoAttributes.isEmpty())
        {

            if (assoControl instanceof SelectControl)
            {
                tgtEntityTable = ((AssociationInterface) ((SelectControl) assoControl)
                        .getBaseAbstractAttribute()).getTargetEntity().getTableProperties()
                        .getName();
            }
            String selectClause = SELECT_KEYWORD + tgtEntityTable + "." + IDENTIFIER;
            String fromClause = FROM_KEYWORD + tgtEntityTable + ", ";
            String whereClause = WHERE_KEYWORD;
            // Clause for multiple columns.
            String multipleColClause = SELECT_KEYWORD + tgtEntityTable + "." + IDENTIFIER + ", ";

            List associationAttr = new ArrayList(assoAttributes);
            Collections.sort(associationAttr);

            Iterator<AssociationDisplayAttributeInterface> attrIter = assoAttributes.iterator();
            AssociationDisplayAttributeInterface displayAttribute = null;

            while (attrIter.hasNext())
            {
                displayAttribute = attrIter.next();
                columnName = displayAttribute.getAttribute().getColumnProperties().getName();
                tableName = displayAttribute.getAttribute().getEntity().getTableProperties()
                        .getName();

                if (assoControl instanceof SelectControl
                        && ((AssociationInterface) ((SelectControl) assoControl)
                                .getBaseAbstractAttribute()).getTargetEntity().getParentEntity() != null)
                {
                    selectClause = selectClause + ", " + tableName + "." + columnName;

                    if (!(fromClause.contains(tableName)))
                    {
                        fromClause = fromClause + tableName + ", ";
                    }
                    if (counter == 0 && assoAttributes.size() > 1)
                    {
                        whereClause = whereClause + tableName
                                + ".ACTIVITY_STATUS <> 'Disabled' AND ";
                        whereClause = whereClause + tableName + "." + IDENTIFIER + " = ";
                    }
                    else if (counter > 0 && assoAttributes.size() > 1)
                    {
                        whereClause = whereClause + tableName + "." + IDENTIFIER + " AND "
                                + tableName + ".ACTIVITY_STATUS <> 'Disabled' AND " + tableName
                                + "." + IDENTIFIER + " = ";
                    }
                    else if (assoAttributes.size() == 1)
                    {
                        if (!(fromClause.contains(tgtEntityTable)))
                        {
                            fromClause = fromClause + tgtEntityTable + ", ";
                        }
                        whereClause = whereClause + tgtEntityTable
                                + ".ACTIVITY_STATUS <> 'Disabled' AND ";
                        whereClause = whereClause + tableName + "." + IDENTIFIER + " = "
                                + tgtEntityTable + "." + IDENTIFIER + " AND " + tgtEntityTable
                                + "." + IDENTIFIER + " = ";
                    }

                    counter++;

                    tableNames.add(tableName);
                }
                else
                {
                    areMultipleAttr = true;
                    multipleColClause += columnName + ", ";
                    tableNames.add(tableName);
                }

                if (tableNames.isEmpty() && !(assoControl instanceof SelectControl))
                {
                    selectClause = selectClause + tableName + "." + IDENTIFIER;
                    fromClause = fromClause + tableName;
                    onClause = onClause + tableName + "." + IDENTIFIER;
                    tableNames.add(tableName);
                }
                else
                {
                    if (tableNames.indexOf(tableName) == -1)
                    {
                        tableNames.add(tableName);
                        fromClause = fromClause + JOIN_KEYWORD + tableName;
                        onClause = onClause + EQUAL + tableName + "." + IDENTIFIER;
                    }
                }
            }

            if (!areMultipleAttr)
            {
                int lastIndexOfAND = whereClause.lastIndexOf("AND");
                whereClause = whereClause.substring(0, lastIndexOfAND);
                fromClause = fromClause.substring(0, fromClause.length() - 2);
            }

            if (((AssociationInterface) ((SelectControl) assoControl).getBaseAbstractAttribute())
                    .getTargetEntity().getParentEntity() == null)
            {
                multipleColClause = multipleColClause.substring(0, multipleColClause.length() - 2)
                        + FROM_KEYWORD + tgtEntityTable;
            }

            StringBuffer query = new StringBuffer();

            if (!areMultipleAttr)
            {
                query.append(selectClause + fromClause + whereClause);
            }
            else
            {
                query.append(multipleColClause);
                query.append(WHERE_KEYWORD
                        + queryBuilder.getRemoveDisbledRecordsQuery(tableNames.get(0)));
            }

            JDBCDAO jdbcDao = null;
            try
            {
                jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
                List results = jdbcDao.executeQuery(query.toString());

                if (results != null)
                {
                    if (!areMultipleAttr)
                    {
                        for (int i = 0; i < results.size(); i++)
                        {
                            List innerList = (List) results.get(i);
                            Long recordId = Long.parseLong((String) innerList.get(0));
                            innerList.remove(0);
                            assoRecords.put(recordId, innerList);
                        }
                    }
                    else
                    {
                        for (int i = 0; i < results.size(); i++)
                        {
                            List innerList = (List) results.get(i);
                            Long recordId = Long.parseLong((String) innerList.get(0));

                            if (assoRecords.containsKey(recordId))
                            {
                                List<String> tempStringList = new ArrayList<String>();

                                String existingString = assoRecords.get(recordId).toString()
                                        .replace("[", " ");
                                existingString = existingString.replace("]", " ");

                                tempStringList.add(existingString.trim()
                                        + assoControl.getSeparator() + (String) innerList.get(1));
                                assoRecords.put(recordId, tempStringList);
                            }
                            else
                            {
                                innerList.remove(0);
                                assoRecords.put(recordId, innerList);
                            }
                        }
                    }
                }
            }

            catch (DAOException e)
            {
                throw new DynamicExtensionsSystemException("Error while retrieving the data", e);
            }
            finally
            {
                try
                {
                    DynamicExtensionsUtility.closeJDBCDAO(jdbcDao);
                }
                catch (DAOException e)
                {
                    throw new DynamicExtensionsSystemException("Error while retrieving the data", e);
                }
            }
        }
        return assoRecords;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getAllContainerBeans()
     */
    public List<NameValueBean> getAllContainerBeans() throws DynamicExtensionsSystemException,
            DynamicExtensionsApplicationException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        Collection containerBeans = executeHQL("getAllContainerBeans", substParams);

        Object[] contBeans;
        List<NameValueBean> nameValueBeans = new ArrayList<NameValueBean>();

        Iterator contBeansIter = containerBeans.iterator();
        while (contBeansIter.hasNext())
        {
            contBeans = (Object[]) contBeansIter.next();

            // In case of category creation form caption is optional.
            if ((String) contBeans[1] != null)
            {
                nameValueBeans.add(new NameValueBean(contBeans[1], contBeans[0]));
            }
        }

        return nameValueBeans;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getAllContainerBeansByEntityGroupId(java.lang.Long)
     */
    public List<NameValueBean> getAllContainerBeansByEntityGroupId(Long entityGroupId)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, entityGroupId));

        Collection containerBeans = executeHQL("getAllContainersBeansByEntityGroupId", substParams);

        Object[] contBeans;
        List<NameValueBean> nameValueBeans = new ArrayList<NameValueBean>();

        Iterator contBeansIter = containerBeans.iterator();
        while (contBeansIter.hasNext())
        {
            contBeans = (Object[]) contBeansIter.next();
            // In case of category creation form caption is optional.
            if ((String) contBeans[1] != null)
            {
                nameValueBeans.add(new NameValueBean(contBeans[1], contBeans[0]));
            }
        }

        return nameValueBeans;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getAllContainerInformationObjects()
     */
    public List<ContainerInformationObject> getAllContainerInformationObjects()
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        Collection cntInfoObjects = executeHQL("getAllContainerInformationObjects", substParams);

        Object[] contInfoObj;
        List<ContainerInformationObject> contInfObjects = new ArrayList<ContainerInformationObject>();

        Iterator cntInfoObjIter = cntInfoObjects.iterator();
        while (cntInfoObjIter.hasNext())
        {
            contInfoObj = (Object[]) cntInfoObjIter.next();
            contInfObjects.add(new ContainerInformationObject((String) contInfoObj[1],
                    ((Long) contInfoObj[0]).toString(), (String) contInfoObj[2]));
        }

        return contInfObjects;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getAllContainerBeansMap()
     */
    public Map<String, String> getAllContainerBeansMap() throws DynamicExtensionsSystemException,
            DynamicExtensionsApplicationException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        Object[] cntBeans;
        Map<String, String> contBeans = new HashMap<String, String>();
        String containerId;
        // Container caption.
        String contCaption;

        Collection containerBeans = executeHQL("getAllContainerBeans", substParams);
        Iterator contBeansIter = containerBeans.iterator();
        while (contBeansIter.hasNext())
        {
            cntBeans = (Object[]) contBeansIter.next();
            contCaption = (String) cntBeans[1];
            containerId = ((Long) cntBeans[0]).toString();
            contBeans.put(containerId, contCaption);
        }

        return contBeans;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getChildrenEntities(edu.common.dynamicextensions.domaininterface.EntityInterface)
     */
    public Collection<EntityInterface> getChildrenEntities(EntityInterface entity)
            throws DynamicExtensionsSystemException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, entity.getId()));

        return executeHQL("getChildrenEntities", substParams);
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getAssociationByIdentifier(java.lang.Long)
     */
    public AssociationInterface getAssociationByIdentifier(Long assoId)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, assoId));

        Collection assocations = executeHQL("getAssociationByIdentifier", substParams);
        if (assocations.isEmpty())
        {
            throw new DynamicExtensionsApplicationException("Object Not Found : id" + assoId, null,
                    DYEXTN_A_008);
        }

        return (AssociationInterface) assocations.iterator().next();
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getIncomingAssociations(edu.common.dynamicextensions.domaininterface.EntityInterface)
     */
    public Collection<AssociationInterface> getIncomingAssociations(EntityInterface entity)
            throws DynamicExtensionsSystemException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, entity.getId()));

        Collection<AssociationInterface> assocations = executeHQL("getAssociationsForTargetEntity",
                substParams);

        return assocations;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getIncomingAssociationIds(edu.common.dynamicextensions.domaininterface.EntityInterface)
     */
    public Collection<Long> getIncomingAssociationIds(EntityInterface entity)
            throws DynamicExtensionsSystemException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, entity.getId()));

        Collection<Long> assocations = executeHQL("getAssociationIdsForTargetEntity", substParams);

        return assocations;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getContainerCaption(java.lang.Long)
     */
    public String getContainerCaption(Long containerId) throws DynamicExtensionsSystemException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, containerId));

        Collection contCaptions = executeHQL("getContainerCaption", substParams);

        return contCaptions.iterator().next().toString();
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getCategoryCaption(java.lang.Long)
     */
    public String getCategoryCaption(Long categoryId) throws DynamicExtensionsSystemException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, categoryId));

        Collection catCaptions = executeHQL("getRootCategoryEntityCaptionById", substParams);

        return catCaptions.iterator().next().toString();
    }

    public Long getRootCategoryEntityIdByCategoryName(String categoryName)
            throws DynamicExtensionsSystemException
    {
        Long identifier = null;
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.STRING, categoryName));
        Collection<Long> rootCategoryEntityId = null;
        rootCategoryEntityId = executeHQL("getRootCategoryEntityId", substParams);
        if (rootCategoryEntityId != null && !rootCategoryEntityId.isEmpty())
        {
            identifier = rootCategoryEntityId.iterator().next();
        }
        return identifier;
    }

    public Long getContainerIdFromEntityId(Long entityId) throws DynamicExtensionsSystemException
    {
        Long identifier = null;
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, entityId));
        Collection<Long> containerIds = null;
        containerIds = executeHQL("getContainerIdFromEntityId", substParams);
        if (containerIds != null && !containerIds.isEmpty())
        {
            identifier = containerIds.iterator().next();
        }
        return identifier;
    }

    /**
     * @param entityId
     * @return
     * @throws DynamicExtensionsSystemException
     */
    public String getContainerCaptionFromEntityId(Long entityId)
            throws DynamicExtensionsSystemException
    {
        String identifier = null;
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.STRING, entityId));
        Collection<String> containerIds = null;
        containerIds = executeHQL("getContainerCaptionFromEntityId", substParams);
        if (containerIds != null && !containerIds.isEmpty())
        {
            identifier = containerIds.iterator().next();
        }
        return identifier;
    }

    public Collection<Long> getAllEntityIdsForEntityGroup(Long entityGroupId)
            throws DynamicExtensionsSystemException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, entityGroupId));

        Collection<Long> entityIds = executeHQL("getAllEntityIdsForEntityGroup", substParams);

        return entityIds;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#addAssociationColumn(edu.common.dynamicextensions.domaininterface.AssociationInterface)
     */
    public void addAssociationColumn(AssociationInterface association)
            throws DynamicExtensionsSystemException
    {
        List<String> revQueries = new ArrayList<String>();
        Stack<String> rlbkQryStack = null;

        try
        {
            List<String> queries = new ArrayList<String>();
            queries.addAll(queryBuilder.getQueryPartForAssociation(association, revQueries, true));
            rlbkQryStack = queryBuilder.executeQueries(queries, revQueries, rlbkQryStack);
        }
        catch (DynamicExtensionsSystemException e)
        {
            if (rlbkQryStack != null && !rlbkQryStack.isEmpty())
            {
                rollbackQueries(rlbkQryStack, association.getEntity(), e, null);
            }
        }
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#associateEntityRecords(edu.common.dynamicextensions.domaininterface.AssociationInterface, java.lang.Long, java.lang.Long)
     */
  public void associateEntityRecords(AssociationInterface association, Long srcEntRecId,
            Long tgtEntRecId) throws DynamicExtensionsSystemException
    {
        queryBuilder.associateRecords(association, srcEntRecId, tgtEntRecId);
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getEntityIdByContainerId(java.lang.Long)
     */
    public Long getEntityIdByContainerId(Long containerId) throws DynamicExtensionsSystemException
    {
        Long identifier = null;
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, containerId));

        // The following method takes the name of the query and
        // the actual values for the place holders as the parameters.
        Collection records = null;
        records = executeHQL("getEntityIdForContainerId", substParams);
        if (records != null && !records.isEmpty())
        {
            identifier = (Long) records.iterator().next();
        }

        return identifier;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getEntityCreatedDateByContainerId()
     */
    public Map<Long, Date> getEntityCreatedDateByContainerId()
            throws DynamicExtensionsSystemException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        Map<Long, Date> records = new HashMap<Long, Date>();

        Collection containers;
        containers = executeHQL("getAllEntityCreatedDateByContainerId", substParams);

        if (containers != null && !containers.isEmpty())
        {
            Iterator iter = containers.iterator();

            while (iter.hasNext())
            {
                Object[] objects = (Object[]) iter.next();
                records.put((Long) objects[0], (Date) objects[1]);
            }
        }

        return records;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#checkContainerForAbstractEntity(java.lang.Long, boolean)
     */
    public Long checkContainerForAbstractEntity(Long entityId, boolean isAbstarct)
            throws DynamicExtensionsSystemException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, entityId));
        substParams.put("1", new NamedQueryParam(DBTypes.BOOLEAN, isAbstarct));

        Collection containers = executeHQL("checkContainerForAbstractEntity", substParams);

        Long contId = null;

        if (containers != null && !containers.isEmpty())
        {
            contId = (Long) containers.iterator().next();

        }

        return contId;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#checkContainerForAbstractCategoryEntity(java.lang.Long)
     */
    public Long checkContainerForAbstractCategoryEntity(Long entityId)
            throws DynamicExtensionsSystemException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, entityId));

        Collection containers = executeHQL("checkContainerForAbstractCategoryEntity", substParams);

        Long contId = null;

        if (containers != null && !containers.isEmpty())
        {
            contId = (Long) containers.iterator().next();

        }

        return contId;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getEntityId(java.lang.String)
     */
    public Long getEntityId(String entityName) throws DynamicExtensionsSystemException
    {
        Long entityId = null;

        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.STRING, entityName));

        Collection entityIds = executeHQL("getEntityIdentifier", substParams);
        if (entityIds != null && !entityIds.isEmpty())
        {
            entityId = (Long) entityIds.iterator().next();
        }

        return entityId;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getContainerIdForEntity(java.lang.Long)
     */
    public Long getContainerIdForEntity(Long entityId) throws DynamicExtensionsSystemException
    {
        Long identifier = null;
        String tableName = "dyextn_container";

        StringBuffer query = new StringBuffer();
        query.append(SELECT_KEYWORD + WHITESPACE + IDENTIFIER);
        query.append(WHITESPACE + FROM_KEYWORD + WHITESPACE + tableName + WHITESPACE);
        query.append(WHERE_KEYWORD + WHITESPACE + "ENTITY_ID" + WHITESPACE + EQUAL +QUESTION_MARK );

        LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
        queryDataList.add(new ColumnValueBean("ENTITY_ID", entityId.toString()));
        ResultSet resultSet = null;
        JDBCDAO jdbcDao = null;
        try
        {
            jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
            resultSet = jdbcDao.getResultSet(query.toString(),queryDataList,null);
            if (resultSet != null)
            {
                resultSet.next();
                identifier = resultSet.getLong(IDENTIFIER);
            }
        }
        catch (DAOException e)
        {
            Logger.out.debug(e.getMessage());
        }
        catch (SQLException e)
        {
            Logger.out.debug(e.getMessage());
        }
        finally
        {
            if (resultSet != null)
            {
                try
                {
                    jdbcDao.closeStatement(resultSet);
                    DynamicExtensionsUtility.closeJDBCDAO(jdbcDao);
                }
                catch (DAOException e)
                {
                    throw new DynamicExtensionsSystemException(e.getMessage(), e);
                }
            }
        }

        return identifier;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getNextIdentifierForEntity(java.lang.String)
     */
    public Long getNextIdentifierForEntity(String entityName)
            throws DynamicExtensionsSystemException
    {
        String tableName = "dyextn_database_properties";
        String NAME = "NAME";

        StringBuffer query = new StringBuffer(80);
        query.append(SELECT_KEYWORD + WHITESPACE + NAME);
        query.append(WHITESPACE + FROM_KEYWORD + WHITESPACE + tableName + WHITESPACE);
        query.append(WHERE_KEYWORD + WHITESPACE + IDENTIFIER + WHITESPACE + EQUAL);
        query.append(OPENING_BRACKET);
        query.append(SELECT_KEYWORD + WHITESPACE + IDENTIFIER);
        query.append(WHITESPACE + FROM_KEYWORD + WHITESPACE + "dyextn_table_properties"
                + WHITESPACE);
        query.append(WHERE_KEYWORD + WHITESPACE + "ABSTRACT_ENTITY_ID" + WHITESPACE + EQUAL);
        query.append(OPENING_BRACKET);
        query.append(SELECT_KEYWORD + WHITESPACE + IDENTIFIER);
        query.append(WHITESPACE + FROM_KEYWORD + WHITESPACE + "dyextn_abstract_metadata"
                + WHITESPACE);
        query.append(WHERE_KEYWORD + WHITESPACE + "NAME" + WHITESPACE + EQUAL + "'" + entityName
                + "'");
        query.append(CLOSING_BRACKET);
        query.append(CLOSING_BRACKET);
        Logger.out.info("Query = " + query.toString());

        ResultSet resultSet = null;
        JDBCDAO jdbcDao = null;
        Long identifier = null;
        try
        {
            Logger.out.info("Query = " + query.toString());
            jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
            resultSet = jdbcDao.getQueryResultSet(query.toString());
            if (resultSet != null)
            {
                resultSet.next();
                String entTableName = resultSet.getString(NAME);
                if (entTableName != null)
                {
                    identifier = EntityManagerUtil.getNextIdentifier(entTableName);
                }
            }
        }
        catch (DAOException e)
        {
            Logger.out.debug(e.getMessage());
        }
        catch (SQLException e)
        {
            Logger.out.debug(e.getMessage());
        }
        finally
        {
            if (resultSet != null)
            {
                try
                {
                    jdbcDao.closeStatement(resultSet);
                    DynamicExtensionsUtility.closeJDBCDAO(jdbcDao);
                }
                catch (DAOException e)
                {
                    throw new DynamicExtensionsSystemException(e.getMessage(), e);
                }
            }
        }

        return identifier;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getAttribute(java.lang.String, java.lang.String)
     */
    public AttributeInterface getAttribute(String entityName, String attributeName)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        AttributeInterface attribute = null;
        AbstractAttributeInterface abstractAttribute;
        String name;
        if (entityName == null || entityName.equals("") || attributeName == null
                || attributeName.equals(""))
        {
            return attribute;
        }

        EntityInterface entity = getEntityByName(entityName);
        if (entity != null)
        {
            Collection<AbstractAttributeInterface> abstrAttributes = entity
                    .getAbstractAttributeCollection();
            if (abstrAttributes != null)
            {
                Iterator<AbstractAttributeInterface> abstrAttrIter = abstrAttributes.iterator();
                while (abstrAttrIter.hasNext())
                {
                    abstractAttribute = abstrAttrIter.next();
                    if (abstractAttribute instanceof AttributeInterface)
                    {
                        attribute = (AttributeInterface) abstractAttribute;
                        name = attribute.getName();
                        if (name != null && name.equals(attributeName))
                        {
                            return attribute;
                        }
                    }
                }
            }
        }

        return attribute;
    }

    /**
     * @param entGroupId
     * @return
     * @throws DynamicExtensionsSystemException
     */
    public Collection<NameValueBean> getEntityGroupBeanById(Long entGroupId)
            throws DynamicExtensionsSystemException
    {
        Collection<NameValueBean> entGroupBeans = new ArrayList<NameValueBean>();
        Object[] objects;

        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, entGroupId));

        Collection groupBeans = executeHQL("getEntityGroupBeanById", substParams);
        Iterator grpBeansIter = groupBeans.iterator();
        while (grpBeansIter.hasNext())
        {
            objects = (Object[]) grpBeansIter.next();

            NameValueBean nameValueBean = new NameValueBean();
            nameValueBean.setName(objects[0]);
            nameValueBean.setValue(objects[1]);

            entGroupBeans.add(nameValueBean);
        }

        return entGroupBeans;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getAllEntityGroupBeans()
     */
    public Collection<NameValueBean> getAllEntityGroupBeans()
            throws DynamicExtensionsSystemException
    {
        Collection<NameValueBean> entGroupBeans = new ArrayList<NameValueBean>();
        Object[] objects;

        Collection groupBeans = executeHQL("getAllGroupBeans", new HashMap());
        Iterator grpBeansIter = groupBeans.iterator();
        while (grpBeansIter.hasNext())
        {
            objects = (Object[]) grpBeansIter.next();

            NameValueBean nameValueBean = new NameValueBean();
            nameValueBean.setName(objects[0]);
            nameValueBean.setValue(objects[1]);

            entGroupBeans.add(nameValueBean);
        }

        return entGroupBeans;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#validateEntity(edu.common.dynamicextensions.domaininterface.EntityInterface)
     */
    public boolean validateEntity(EntityInterface ent)
            throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
    {
        Collection<EntityInterface> entities = ent.getEntityGroup().getEntityCollection();
        for (EntityInterface entity : entities)
        {
            Entity entityObject = (Entity) ent;
            if (entity.getId() == null)
            {
                DynamicExtensionsUtility.validateEntity(entity);
            }
            else
            {
                Entity dbaseCopy = null;
                try
                {
                    dbaseCopy = (Entity) DynamicExtensionsUtility.getCleanObject(Entity.class
                            .getCanonicalName(), entity.getId());
                }
                catch (DAOException e)
                {
                    throw new DynamicExtensionsSystemException(e.getMessage(), e);
                }
                if (EntityManagerUtil.isParentChanged((Entity) entity, dbaseCopy))
                {
                    checkParentChangeAllowed(entityObject);
                }
            }
        }

        return true;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getAttributeRecordsCount(java.lang.Long, java.lang.Long)
     */
    public Collection<Integer> getAttributeRecordsCount(Long entityId, Long attributeId)
            throws DynamicExtensionsSystemException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, entityId));
        substParams.put("1", new NamedQueryParam(DBTypes.LONG, attributeId));

        // The following method takes the name of the query and
        // the actual values for the place holders as the parameters.
        Collection records = executeHQLWithCleanSession("getAttributeRecords", substParams);

        return records;
    }

    /**
     * This method executes the HQL query in a clean session.
     * @param queryName
     * @param substParams
     * @return
     * @throws DynamicExtensionsSystemException
     */
    private Collection executeHQLWithCleanSession(String queryName,
            Map<String, NamedQueryParam> substParams) throws DynamicExtensionsSystemException
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
            try
            {
                DynamicExtensionsUtility.closeHibernateDAO(hibernateDAO);
            }
            catch (DAOException e)
            {
                throw new DynamicExtensionsSystemException(
                        "Exception occured while closing the session", e, DYEXTN_S_001);
            }
        }

        return objects;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getContainerByEntityIdentifier(java.lang.Long)
     */
    public ContainerInterface getContainerByEntityIdentifier(Long entityId)
            throws DynamicExtensionsSystemException
    {
        ContainerInterface container = null;

        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, entityId));

        Collection containers = executeHQL("getContainerOfEntity", substParams);
        if (containers != null && !containers.isEmpty())
        {
            container = (ContainerInterface) containers.iterator().next();
        }

        return container;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getAssociationTree(java.lang.Long)
     */
    public Collection<AssociationTreeObject> getAssociationTree(Long entGroupId)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        Collection<AssociationTreeObject> assoTreeObjs = new HashSet<AssociationTreeObject>();
        AssociationTreeObject assoTreeObject;

        Collection<NameValueBean> groupBeans = getEntityGroupBeanById(entGroupId);
        Iterator<NameValueBean> grpBeansIter = groupBeans.iterator();
        while (grpBeansIter.hasNext())
        {
            assoTreeObject = processGroupBean(grpBeansIter.next());
            assoTreeObjs.add(assoTreeObject);
        }

        return assoTreeObjs;
    }

    /**
     * @param groupBean
     * @return
     * @throws DynamicExtensionsSystemException
     * @throws DynamicExtensionsApplicationException
     */
    private AssociationTreeObject processGroupBean(NameValueBean groupBean)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        AssociationTreeObject assoTreeObject = new AssociationTreeObject(Long.valueOf(groupBean
                .getValue()), groupBean.getName());

        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, assoTreeObject.getId()));

        Object[] containerBeans;
        AssociationTreeObject asTreeObjForCont;

        Collection contBeans = executeHQL("getAllContainersBeansByEntityGroupId", substParams);
        Iterator contBeansIter = contBeans.iterator();
        while (contBeansIter.hasNext())
        {
            containerBeans = (Object[]) contBeansIter.next();
            asTreeObjForCont = new AssociationTreeObject((Long) containerBeans[0],
                    (String) containerBeans[1]);
            assoTreeObject.addAssociationTreeObject(asTreeObjForCont);
        }

        return assoTreeObject;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getFileAttributeRecordValueByRecordId(edu.common.dynamicextensions.domaininterface.AttributeInterface, java.lang.Long)
     */
    public FileAttributeRecordValue getFileAttributeRecordValueByRecordId(
            AttributeInterface attribute, Long recordId) throws DynamicExtensionsSystemException,
            DynamicExtensionsApplicationException, DAOException, SQLException, IOException
    {
        EntityInterface entity = attribute.getEntity();
        FileAttributeRecordValue fileRecordValue = new FileAttributeRecordValue();

        String query = SELECT_KEYWORD + attribute.getColumnProperties().getName() + UNDERSCORE
                + FILE_NAME + COMMA + attribute.getColumnProperties().getName() + UNDERSCORE
                + CONTENT_TYPE + COMMA + attribute.getColumnProperties().getName() + FROM_KEYWORD
                + entity.getTableProperties().getName() + WHITESPACE + WHERE_KEYWORD + IDENTIFIER
                + EQUAL + QUESTION_MARK;
        LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean(IDENTIFIER, recordId));
        ResultSet resultSet = null;
        JDBCDAO jdbcDAO = DynamicExtensionsUtility.getJDBCDAO();
        try
        {
            resultSet = jdbcDAO.getResultSet(query,queryDataList,null);
            while (resultSet.next())
            {
                fileRecordValue.setFileName(resultSet.getString(attribute.getColumnProperties()
                        .getName()
                        + UNDERSCORE + FILE_NAME));
                fileRecordValue.setContentType(resultSet.getString(attribute.getColumnProperties()
                        .getName()
                        + UNDERSCORE + CONTENT_TYPE));

                Blob blob = resultSet.getBlob(attribute.getColumnProperties().getName());
                byte[] byteArray = blob.getBytes(1, (int) blob.length());

                fileRecordValue.setFileContent(byteArray);
            }
        }
        finally
        {
            jdbcDAO.closeStatement(resultSet);
            DynamicExtensionsUtility.closeJDBCDAO(jdbcDAO);
        }

        return fileRecordValue;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getCategoriesContainerIdFromHookEntity(java.lang.Long)
     */
    public Collection<ContainerInterface> getCategoriesContainerIdFromHookEntity(Long hookEntityId)
            throws DynamicExtensionsSystemException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, hookEntityId));

        Collection containers = executeHQL("getCategoryContainerIdFromHookEntiy", substParams);

        return containers;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getDynamicTableName(java.lang.Long)
     */
    public String getDynamicTableName(Long containerId) throws DynamicExtensionsSystemException
    {
        String tableName = "";

        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, containerId));

        Collection containers = executeHQL("getDynamicTableName", substParams);
        if (containers != null && !containers.isEmpty())
        {
            tableName = (String) containers.iterator().next();
        }

        return tableName;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getDynamicEntitiesContainerIdFromHookEntity(java.lang.Long)
     */
    public Collection<ContainerInterface> getDynamicEntitiesContainerIdFromHookEntity(
            Long hookEntityId) throws DynamicExtensionsSystemException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, hookEntityId));

        Collection containers = executeHQL("getFormsContainerIdFromHookEntiy", substParams);

        return containers;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#isCategory(java.lang.Long)
     */
    public Long isCategory(Long containerId) throws DynamicExtensionsSystemException
    {
        Long contIdentifier = null;

        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, containerId));

        Collection containers = executeHQL("isCategory", substParams);
        if (containers != null && !containers.isEmpty())
        {
            contIdentifier = (Long) containers.iterator().next();
        }

        return contIdentifier;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getCategoryRootContainerId(java.lang.Long)
     */
    public Long getCategoryRootContainerId(Long containerId)
            throws DynamicExtensionsSystemException
    {
        Long contIdentifier = null;

        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, containerId));

        Collection containers = executeHQL("getCategoryRootContainerId", substParams);
        if (containers != null && !containers.isEmpty())
        {
            contIdentifier = (Long) containers.iterator().next();
        }

        return contIdentifier;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getColumnNameForAssociation(java.lang.Long, java.lang.Long)
     */
    public String getColumnNameForAssociation(Long hookEntityId, Long containerId)
            throws DynamicExtensionsSystemException
    {
        String colName = null;

        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, hookEntityId));
        substParams.put("1", new NamedQueryParam(DBTypes.LONG, containerId));

        Collection colNames = executeHQL("getColumnNameForAssociation", substParams);

        if (colNames != null && !colNames.isEmpty())
        {
            colName = (String) colNames.iterator().next();
        }

        return colName;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#persistEntityMetadataForAnnotation(edu.common.dynamicextensions.domaininterface.EntityInterface, boolean, boolean, edu.common.dynamicextensions.domaininterface.AssociationInterface)
     */
    public EntityInterface persistEntityMetadataForAnnotation(EntityInterface entityObj,
            boolean isDataTblPresent, boolean cpyDataTblState, AssociationInterface association)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {

        HibernateDAO hibernateDAO = null;
        try
        {
            hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();
            //Use an overloaded method for update the object
            this.persistEntityMetadataForAnnotation(entityObj, isDataTblPresent, cpyDataTblState,
                    association, hibernateDAO);

            // Committing the changes done in the hibernate session to the database.
            hibernateDAO.commit();
        }
        catch (Exception e)
        {
            // If there is any exception while storing the meta data,
            // we need to roll back the queries that were fired.
            // So calling the following method to do that.
            //rollbackQueries(stack, entity, e, hibernateDAO);

            if (e instanceof DynamicExtensionsApplicationException)
            {
                throw (DynamicExtensionsApplicationException) e;
            }
            else
            {
                throw new DynamicExtensionsSystemException(e.getMessage(), e);
            }
        }
        finally
        {
            try
            {
                // In any case, after all the operations, hibernate session needs to be closed.
                // So this call has been added in the finally clause.
                DynamicExtensionsUtility.closeHibernateDAO(hibernateDAO);
            }
            catch (DAOException e)
            {
                // If there is any exception while storing the meta data,
                // we need to roll back the queries that were fired. So calling the
                // following method to do that.
                //rollbackQueries(stack, entity, e, hibernateDAO);
                Logger.out.error("The cause of the exception is - " + e.getMessage());
            }
        }

        logDebug("persistEntity", "exiting the method");

        return entityObj;
    }

    /**
     * This method is overloaded to take hibernatedao as argument ,it will be called from importxmi
     * @param entityObj
     * @param isDataTblPresent
     * @param cpyDataTblState
     * @param association
     * @param hibernateDAO
     * @return
     * @throws DynamicExtensionsSystemException
     * @throws DynamicExtensionsApplicationException
     */
    public EntityInterface persistEntityMetadataForAnnotation(EntityInterface entityObj,
            boolean isDataTblPresent, boolean cpyDataTblState, AssociationInterface association,
            HibernateDAO hibernateDAO) throws DynamicExtensionsSystemException,
            DynamicExtensionsApplicationException
    {
        Entity entity = (Entity) entityObj;
        if (isDataTblPresent)
        {
            ((Entity) entityObj).setDataTableState(DATA_TABLE_STATE_ALREADY_PRESENT);
        }
        else
        {
            ((Entity) entityObj).setDataTableState(DATA_TABLE_STATE_NOT_CREATED);
        }
        try
        {
            hibernateDAO.update(entity);

        }
        catch (Exception e)
        {
            // If there is any exception while storing the meta data,
            // we need to roll back the queries that were fired.
            // So calling the following method to do that.
            //rollbackQueries(stack, entity, e, hibernateDAO);

            if (e instanceof DynamicExtensionsApplicationException)
            {
                throw (DynamicExtensionsApplicationException) e;
            }
            else
            {
                throw new DynamicExtensionsSystemException(e.getMessage(), e);
            }
        }

        logDebug("persistEntity", "exiting the method");

        return entityObj;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getMainContainer(java.lang.Long)
     */
    public Collection<NameValueBean> getMainContainer(Long entGroupId)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, entGroupId));

        return executeHQL("getMainContainers", substParams);
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getEntityGroupByName(java.lang.String)
     */
    public EntityGroupInterface getEntityGroupByName(String entGroupName)
            throws DynamicExtensionsSystemException
    {
        EntityGroupInterface entityGroup = (EntityGroupInterface) getObjectByName(EntityGroup.class
                .getName(), entGroupName);

        return entityGroup;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getAllEntitiyGroups()
     */
    public Collection<EntityGroupInterface> getAllEntitiyGroups()
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        return getAllObjects(EntityGroupInterface.class.getName());
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getControlByAbstractAttributeIdentifier(java.lang.Long)
     */
    public ControlInterface getControlByAbstractAttributeIdentifier(Long abstrAttrId)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        ControlInterface control = null;

        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, abstrAttrId));

        Collection controls = executeHQL("getControlOfAbstractAttribute", substParams);
        if (controls != null && !controls.isEmpty())
        {
            control = (ControlInterface) controls.iterator().next();
        }

        return control;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#updateAttributeTypeInfo(edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface)
     */
    public AttributeTypeInformationInterface updateAttributeTypeInfo(
            AttributeTypeInformationInterface attrTypeInfo)
            throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
    {
        HibernateDAO hibernateDAO = null;
        try
        {
            hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();

            hibernateDAO.update(attrTypeInfo);

            hibernateDAO.commit();
        }
        catch (DAOException e)
        {
            throw new DynamicExtensionsSystemException(e.getMessage(), e, DYEXTN_S_003);
        }
        finally
        {
            try
            {
                DynamicExtensionsUtility.closeHibernateDAO(hibernateDAO);
            }
            catch (DAOException e)
            {
                throw new DynamicExtensionsSystemException(e.getMessage(), e, DYEXTN_S_003);
            }
        }

        return attrTypeInfo;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getEntityGroupId(java.lang.String)
     */
    public Long getEntityGroupId(String entGroupName) throws DynamicExtensionsSystemException
    {
        Long entGroupId = null;

        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.STRING, entGroupName));

        Collection entGrpIds = executeHQL("getEntityGroupId", substParams);
        if (entGrpIds != null && !entGrpIds.isEmpty())
        {
            entGroupId = (Long) entGrpIds.iterator().next();
        }

        return entGroupId;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getEntityId(java.lang.String, java.lang.Long)
     */
    public Long getEntityId(String entityName, Long entGroupId)
            throws DynamicExtensionsSystemException
    {
        Long entityId = null;

        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, entGroupId));
        substParams.put("1", new NamedQueryParam(DBTypes.STRING, entityName));

        Collection entityIds = executeHQL("getEntityId", substParams);
        if (entityIds != null && !entityIds.isEmpty())
        {
            entityId = (Long) entityIds.iterator().next();
        }

        return entityId;
    }

    /**
     * @param attrName name of attribute
     * @param entityId identifier of entity
     * @return attributeId form entity based on attribute name
     * @throws DynamicExtensionsSystemException fails to get attributeId form entity based on attribute name
     */
    public Long getAttributeId(String attrName, Long entityId)
            throws DynamicExtensionsSystemException
    {
        Long attrId = null;

        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, entityId));
        substParams.put("1", new NamedQueryParam(DBTypes.STRING, attrName));

        Collection attrIds = executeHQL("getAttributeId", substParams);
        if (attrIds != null && !attrIds.isEmpty())
        {
            attrId = (Long) attrIds.iterator().next();
        }

        return attrId;
    }

    /**
     * @param attrId identifier of an attribute
     * @return AttributeTypeInformationInterface based on attribute id
     * @throws DynamicExtensionsSystemException fails to get AttributeTypeInformationInterface based on attribute Id
     */
    public AttributeTypeInformationInterface getAttributeTypeInformation(Long attrId)
            throws DynamicExtensionsSystemException
    {
        AttributeTypeInformationInterface attrTypeInfo = null;

        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, attrId));

        Collection attrTypeInfos = executeHQL("getAttributeTypeObject", substParams);
        if (attrTypeInfos != null && !attrTypeInfos.isEmpty())
        {
            attrTypeInfo = (AttributeTypeInformationInterface) attrTypeInfos.iterator().next();
        }

        return attrTypeInfo;
    }

    /**
     * @param contCaption Caption of container
     * @return containerId container id
     * @throws DynamicExtensionsSystemException fails to get containerId based on container caption
     */
    public Long getContainerIdByCaption(String contCaption) throws DynamicExtensionsSystemException
    {
        Long containerId = null;

        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.STRING, contCaption));

        Collection<Long> containerIds = executeHQL("getContainerIdByName", substParams);
        if (containerIds != null && !containerIds.isEmpty())
        {
            containerId = containerIds.iterator().next();
        }

        return containerId;
    }

    /* (non-Javadoc)
     * @see edu.common.dynamicextensions.entitymanager.EntityManagerInterface#getAssociationAttributeId(java.lang.Long)
     */
    public Long getAssociationAttributeId(Long attrId) throws DynamicExtensionsSystemException
    {
        Long assoAttrId = null;

        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, attrId));
        substParams.put("1", new NamedQueryParam(DBTypes.BOOLEAN, true));

        Collection tgtEntityIds = executeHQL("getTargetEntityIdForCollAttribute", substParams);
        if (tgtEntityIds != null && !tgtEntityIds.isEmpty())
        {
            Long targetEntityId = (Long) tgtEntityIds.iterator().next();
            if (targetEntityId != null)
            {
                Map<String, NamedQueryParam> subParams = new HashMap<String, NamedQueryParam>();
                subParams.put("0", new NamedQueryParam(DBTypes.LONG, targetEntityId));
                subParams.put("1", new NamedQueryParam(DBTypes.STRING,
                        DEConstants.COLLECTIONATTRIBUTE + "%"));
                subParams.put("2", new NamedQueryParam(DBTypes.STRING,
                        DEConstants.COLLECTIONATTRIBUTE_OLD + "%"));

                Collection attributeIds = executeHQL("getMultiSelAttrId", subParams);
                if (attributeIds != null && !attributeIds.isEmpty())
                {
                    assoAttrId = (Long) attributeIds.iterator().next();
                }
            }
        }

        return assoAttrId;
    }

    /**
     * @param categoryEntityId
     * @return
     * @throws DynamicExtensionsSystemException
     */
    public Long getEntityIdByCategorEntityId(Long categoryEntityId)
            throws DynamicExtensionsSystemException
    {
        Long containerId = null;
        Map<String, NamedQueryParam> substitutionParameterMap = new HashMap<String, NamedQueryParam>();
        substitutionParameterMap.put("0", new NamedQueryParam(DBTypes.LONG, categoryEntityId));
        Collection<Long> containerIdCollection = executeHQL("getEntityIdByCategoryEntityId",
                substitutionParameterMap);
        if (containerIdCollection != null && !containerIdCollection.isEmpty())
        {
            containerId = containerIdCollection.iterator().next();
        }

        return containerId;

    }

    /**
     * @return SystemGenerated EntityGroup beans
     * @throws DynamicExtensionsSystemException
     */
    public Collection<NameValueBean> getAllSystemGenEntityGroupBeans()
            throws DynamicExtensionsSystemException
    {
        Collection<NameValueBean> entGroupBeans = new ArrayList<NameValueBean>();
        Object[] objects;

        Collection groupBeans = executeHQL("getAllSystemGenGroupBeans", new HashMap());
        Iterator grpBeansIter = groupBeans.iterator();
        while (grpBeansIter.hasNext())
        {
            objects = (Object[]) grpBeansIter.next();

            NameValueBean nameValueBean = new NameValueBean();
            nameValueBean.setName(objects[0]);
            nameValueBean.setValue(objects[1]);

            entGroupBeans.add(nameValueBean);
        }

        return entGroupBeans;
    }


    /**
     * @param entityName to get entityGroup
     * @return entityGroupName of a particular entity
     * @throws DynamicExtensionsSystemException
     */
    public String getEntityGroupNameByEntityName(String entityName, Long containerId)
            throws DynamicExtensionsSystemException
    {
        String entityGroupName = null;
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.STRING, entityName));
        substParams.put("1", new NamedQueryParam(DBTypes.LONG, containerId));

        // The following method takes the name of the query and
        // the actual values for the place holders as the parameters.
        Collection groupName = executeHQL("getEntityGroupNameByEntityName", substParams);
        if (groupName != null && !groupName.isEmpty())
        {
            entityGroupName = groupName.iterator().next().toString();
        }
        return entityGroupName;
    }

    /**
     * @param pathAssociationRelationId  identifier of pathAssociationRelationId
     * @return association Id based on pathAssociationRelationId
     * @throws DynamicExtensionsSystemException
     */
    public Long getAssociationIdFrmPathAssoRelationId(Long pathAssociationRelationId)
            throws DynamicExtensionsSystemException
    {
        Long identifier = null;
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, pathAssociationRelationId));
        Collection<Long> associationColl = null;
        associationColl = executeHQL("getAssoIdFrmPathAssoRelationId", substParams);
        if (associationColl != null && !associationColl.isEmpty())
        {
            identifier = associationColl.iterator().next();
        }
        return identifier;
    }

    /**
     * @param pathId identifier of Path
     * @return collection of PathAssociationRelationIds
     * @throws DynamicExtensionsSystemException
     */
    public Collection<Long> getPathAssociationRelationIds(Long pathId)
            throws DynamicExtensionsSystemException
    {
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, pathId));
        Collection<Long> pathAssoRelationIdCollection = null;
        pathAssoRelationIdCollection = executeHQL("getPathAssociationRelationIdCollection",
                substParams);
        return pathAssoRelationIdCollection;
    }

    /**
     * @param associationId  identifier of association
     * @return source entity name
     * @throws DynamicExtensionsSystemException
     */
    public String getSrcEntityNameFromAssociationId(Long associationId)
            throws DynamicExtensionsSystemException
    {
        String srcEntityName = null;
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, associationId));
        Collection<String> srcEntityNameColl = null;
        srcEntityNameColl = executeHQL("getSrcEntityNameFromAssociationId", substParams);
        if (srcEntityNameColl != null && !srcEntityNameColl.isEmpty())
        {
            srcEntityName = srcEntityNameColl.iterator().next();
        }
        return srcEntityName;
    }

    /**
     * @param associationId  identifier of association
     * @return target entity name
     * @throws DynamicExtensionsSystemException
     */
    public String getTgtEntityNameFromAssociationId(Long associationId)
            throws DynamicExtensionsSystemException
    {
        String tgtEntityName = null;
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, associationId));
        Collection<String> tgtEntityNameColl = null;
        tgtEntityNameColl = executeHQL("getTgtEntityNameFromAssociationId", substParams);
        if (tgtEntityNameColl != null && !tgtEntityNameColl.isEmpty())
        {
            tgtEntityName = tgtEntityNameColl.iterator().next();
        }
        return tgtEntityName;
    }

    /**
     * @param associationRelationId  PathAssociationRelationId
     * @return instanceId of the source categoryEntity
     * @throws DynamicExtensionsSystemException
     */
    public Long getSrcInstanceIdFromAssociationRelationId(Long associationRelationId)
            throws DynamicExtensionsSystemException
    {
        Long identifier = null;
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, associationRelationId));
        Collection<Long> srcInstanceIdColl = null;
        srcInstanceIdColl = executeHQL("getSrcInstanceIdFromAssociationRelationId", substParams);
        if (srcInstanceIdColl != null && !srcInstanceIdColl.isEmpty())
        {
            identifier = srcInstanceIdColl.iterator().next();
        }
        return identifier;
    }

    /**
     * @param associationRelationId PathAssociationRelationId
     * @return instanceId of the target categoryEntity
     * @throws DynamicExtensionsSystemException
     */
    public Long getTgtInstanceIdFromAssociationRelationId(Long associationRelationId)
            throws DynamicExtensionsSystemException
    {
        Long identifier = null;
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, associationRelationId));
        Collection<Long> tgtInstanceIdColl = null;
        tgtInstanceIdColl = executeHQL("getTgtInstanceIdFromAssociationRelationId", substParams);
        if (tgtInstanceIdColl != null && !tgtInstanceIdColl.isEmpty())
        {
            identifier = tgtInstanceIdColl.iterator().next();
        }
        return identifier;
    }

    /**
     * @return categoryEntityId collection
     * @throws DynamicExtensionsSystemException
     */
    public Collection<Long> getAllCategoryEntityId() throws DynamicExtensionsSystemException
    {
        Collection<Long> caegoryEntityIdCollection = null;
        caegoryEntityIdCollection = executeHQL("getAllCategoryEntityId", new HashMap());
        return caegoryEntityIdCollection;
    }

    /**
     * @param categoryId identifier of category entity
     * @return name of category
     * @throws DynamicExtensionsSystemException
     */
    public String getCategoryEntityNameByCategoryEntityId(Long categoryId)
            throws DynamicExtensionsSystemException
    {
        String name = null;
        // Create a map of substitution parameters.
        Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
        substParams.put("0", new NamedQueryParam(DBTypes.LONG, categoryId));
        Collection<String> rootCategoryEntityName = null;
        rootCategoryEntityName = executeHQL("getCategoryEntityNameByCategoryEntityId", substParams);
        if (rootCategoryEntityName != null && !rootCategoryEntityName.isEmpty())
        {
            name = rootCategoryEntityName.iterator().next();
        }
        return name;
    }

}