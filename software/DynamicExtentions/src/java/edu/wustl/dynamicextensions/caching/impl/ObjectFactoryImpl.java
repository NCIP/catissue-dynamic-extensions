package edu.wustl.dynamicextensions.caching.impl;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import edu.wustl.dynamicextensions.caching.ObjectFactory;
import edu.wustl.dynamicextensions.caching.ObjectFactoryCfg;
import edu.wustl.dynamicextensions.caching.data.DbTableData;
import edu.wustl.dynamicextensions.caching.data.DbTableLoader;
import edu.wustl.dynamicextensions.caching.data.DbTableMap;
import edu.wustl.dynamicextensions.caching.data.DbUtil;
import edu.wustl.dynamicextensions.caching.data.impl.DbTableLoaderImpl;
import edu.wustl.dynamicextensions.caching.data.impl.DbTableMapImpl;
import edu.wustl.dynamicextensions.caching.metadata.ClassMetadata;
import edu.wustl.dynamicextensions.caching.metadata.ClassMetadataMap;
import edu.wustl.dynamicextensions.caching.metadata.PropertyMetadata;
import edu.wustl.dynamicextensions.caching.metadata.impl.hbm.ClassMetadataMapImpl;
import edu.wustl.dynamicextensions.caching.pool.ObjectPool;
import edu.wustl.dynamicextensions.caching.pool.impl.ObjectPoolImpl;
import edu.wustl.dynamicextensions.caching.util.ReflectionUtil;

/**
 *
 * @author Vinayak Pawar (vinayak.pawar@krishagni.com)
 */
public class ObjectFactoryImpl implements ObjectFactory {
    private static final Logger logger = edu.wustl.common.util.logger.Logger.getLogger(ObjectFactoryImpl.class);

    private ObjectPool objectPool;

    private ClassMetadataMap classMetadataMap;

    private DbTableMap dbTableMap;

    private DbTableLoader dbTableLoader;
    
    private SessionFactory sessionFactory;

    private ObjectFactoryImpl(ObjectFactoryCfg cfg, SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        initialize(cfg);
    }

    public static ObjectFactory createObjectFactory(ObjectFactoryCfg cfg, SessionFactory sessionFactory) {
        return new ObjectFactoryImpl(cfg, sessionFactory);
    }

    public void createObjects() {
        lazyInstantiateObjects();
        hydrateObjects();
        dbTableMap.clear();
    }

    public void createObjects(String ... classNames) {
        createObjects();

        if (classNames == null) {
            return;
        }

        Set<String> requiredTypes = new HashSet<String>(Arrays.asList(classNames));
        for (String requiredType : requiredTypes) {
            logger.info("Required type: " + requiredType);
        }

        for (String objTyp : objectPool.getObjectTypes()) {
            if (requiredTypes.contains(objTyp)) {
                logger.info("Keeping the objects of type: " + objTyp);
                continue;
            }
            logger.info("Removing objects of type: " + objTyp);
            objectPool.removeObjects(objTyp);
        }
    }

    public Collection getObjects(String className) {
        Collection objects = null;
        if (objectPool != null) {
            objects = objectPool.getObjects(className);
        }

        return objects;
    }

    public void removeObjects(String className) {
        if (objectPool != null) {
            objectPool.removeObjects(className);
        }
    }
    
    public Object getPropertyValue(String className, String propertyName, Object id) {
        ClassMetadata classMetadata = classMetadataMap.getClassMetadata(className);
        if (classMetadata == null) {
            return null;
        }
        
        PropertyMetadata idMetadata = classMetadata.getIdMetadata();
        if (idMetadata == null) {
            return null;
        }
        
        String idColumnName = idMetadata.getColumnName();        
        String tableName = null, columnName = null;        
        Collection<PropertyMetadata> propertiesMetadata = classMetadata.getPropertiesMetadata();
        for (PropertyMetadata propertyMetadata : propertiesMetadata) {
            if (propertyMetadata.getPropertyName().equals(propertyName)) {
                tableName  = propertyMetadata.getColumnTableName();
                columnName = propertyMetadata.getColumnName();
                break;
            }
        }
        
        if (idColumnName == null || tableName == null || columnName == null) {
            return null;
        }
        
        return DbUtil.getColumnValue(sessionFactory, tableName, columnName, idColumnName, id);
    }
    
    private void initialize(ObjectFactoryCfg cfg) {
        Connection jdbcConn = null;
        Session session = null;

        try {
            classMetadataMap = ClassMetadataMapImpl.createClassMetadataMap(sessionFactory);

            session = sessionFactory.openSession();
            jdbcConn = session.connection();

            dbTableMap = DbTableMapImpl.createDbTableMap();

            dbTableLoader = DbTableLoaderImpl.createDbTableLoader();
            dbTableLoader.setJdbcConnection(jdbcConn);

            Set<String> excludeTableSet = cfg.getExcludeTableSet();
            for (String className : classMetadataMap.getClassNames()) {
                ClassMetadata classMetadata = classMetadataMap.getClassMetadata(className);
                String tableName = classMetadata.getTableName();
                if (excludeTableSet.contains(tableName.toUpperCase())) {
                    logger.info("Not loading data from table: " + tableName);
                    continue;
                }

                logger.info("Loading data from table: " + tableName);
                DbTableData dbTableData = dbTableLoader.loadTable(
                        tableName,
                        classMetadata.getIdMetadata().getColumnName(),
                        classMetadata.getIdMetadata().getPropertyType());
                dbTableMap.addDbTableData(dbTableData);
            }

            for (String tableName : classMetadataMap.getTableNames()) {
                if (dbTableMap.getDbTableData(tableName) == null && !excludeTableSet.contains(tableName.toUpperCase())) {
                    //
                    // These are basically tables created for many-to-many relations
                    // and as such are not owned by any one class
                    //
                    logger.info("Loading data from table: " + tableName);
                    DbTableData dbTableData = dbTableLoader.loadTable(tableName);
                    dbTableMap.addDbTableData(dbTableData);
                }
            }

            objectPool = ObjectPoolImpl.createObjectPool();
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (jdbcConn != null) {
                try {
                    jdbcConn.close();
                } catch (Exception e1) {

                }
            }

            if (session != null) {
                try {
                    session.close();
                } catch (Exception e2) {

                }
            }
        }
    }

    private void lazyInstantiateObjects() {
        for (String className : classMetadataMap.getClassNames()) {
            ClassMetadata classMetadata = classMetadataMap.getClassMetadata(className);
            if (classMetadata.isAbstractClass()) {
                continue;
            }
            lazyInstantiateObjects(classMetadata);
        }
    }

    private void lazyInstantiateObjects(ClassMetadata classMetadata) {
        Constructor ctor = ReflectionUtil.getDefaultConstructor(classMetadata.getClassName());
        if (ctor == null) {
            logger.warn("No default constructor for class: " + classMetadata.getClassName());
            return;
        }

        DbTableData dbTableData = dbTableMap.getDbTableData(classMetadata.getTableName());
        if (dbTableData == null) {
            logger.warn("No table data for " + classMetadata.getTableName());
            return;
        }

        for (Object key : dbTableData.getKeys()) {
            Object object = createObject(classMetadata, ctor, key);
            if (object != null) {
                objectPool.addObject(classMetadata.getClassName(), key, object);
            }
        }
    }

    private Object createObject(ClassMetadata classMetadata, Constructor ctor, Object key) {
        try {
            Object object = ctor.newInstance((Object[])null);
            setProperty(object, classMetadata.getIdMetadata().getPropertyName(), key);

            for (PropertyMetadata propertyMetadata : classMetadata.getPropertiesMetadata()) {
                if (propertyMetadata.isAssociation() || propertyMetadata.isCustomType()) {
                    continue;
                }

                if (propertyMetadata.getPropertyName().startsWith(("_")) &&
                    propertyMetadata.getPropertyName().endsWith("Backref")) {
                    continue;
                }
                setProperty(propertyMetadata, object, key);
            }

            return object;
        } catch (Exception e) {
            throw new RuntimeException("Error instantiating class: " + classMetadata.getClassName(), e);
        }
    }

    private void hydrateObjects() {
        for (String className : classMetadataMap.getClassNames()) {
            ClassMetadata classMetadata = classMetadataMap.getClassMetadata(className);
            if (classMetadata.isAbstractClass()) {
                continue;
            }

            hydrateManyToOneRefs(classMetadata);
            hydrateCustomTypeRefs(classMetadata);
        }

        Map<String, Set<String>> tableDependencies = getOneToManyTableClassNameDependencies();
        clearUnwantedDbTables(tableDependencies.keySet());
        for (Map.Entry<String, Set<String>> tableDependency : tableDependencies.entrySet()) {
            for (String className : tableDependency.getValue()) {
                ClassMetadata classMetadata = classMetadataMap.getClassMetadata(className);
                if (classMetadata.isAbstractClass()) {
                    continue;
                }
                hydrateOneToManyRefs(classMetadata);
            }

            DbTableData dbTableData = dbTableMap.getDbTableData(tableDependency.getKey());
            if (dbTableData != null) {
                dbTableData.clear();
            }
        }
    }

    private void hydrateManyToOneRefs(ClassMetadata classMetadata) {
        Collection objects = objectPool.getObjects(classMetadata.getClassName());
        if (objects == null) {
            return;
        }

        String idName = classMetadata.getIdMetadata().getPropertyName();
        for (Object object : objects) {
            Object id = getProperty(object, idName);

            for (PropertyMetadata propertyMetadata : classMetadata.getPropertiesMetadata()) {
                if (!propertyMetadata.isAssociation() || propertyMetadata.isCollection()) {
                    continue;
                }

                Object associatedObj = getAssociatedObject(propertyMetadata, id);
                if (associatedObj != null) {
                    setProperty(object, propertyMetadata.getPropertyName(), associatedObj, false);
                }
            }
        }
    }

    private Object getAssociatedObject(PropertyMetadata propertyMetadata, Object id) {
        DbTableData dbTableData = dbTableMap.getDbTableData(propertyMetadata.getColumnTableName());
        Object[] row = dbTableData.getRow(id);
        if (row == null) {
            return null;
        }

        String associatedType = propertyMetadata.getPropertyType();
        ClassMetadata associatedTypeMetadata = classMetadataMap.getClassMetadata(associatedType);
        if (associatedTypeMetadata == null) {
            return null;
        }

        String associatedIdType = associatedTypeMetadata.getIdMetadata().getPropertyType();
        Object associatedObjId = dbTableData.getColumnValue(row, propertyMetadata.getColumnName(), associatedIdType);
        if (associatedObjId == null) {
            return null;
        }

        Object associatedObj = null;
        for (String subClass : associatedTypeMetadata.getSubClassNames()) {
            associatedObj = objectPool.getObject(subClass, associatedObjId);
            if (associatedObj != null) {
                break;
            }
        }

        if (associatedObjId != null && associatedObj == null) {
            logger.error("There is no object corresponding to id " + associatedObjId + " in " + associatedType);
        }

        return associatedObj;
    }

    private void hydrateOneToManyRefs(ClassMetadata classMetadata) {
        Collection objects = objectPool.getObjects(classMetadata.getClassName());
        if (objects == null) {
            return;
        }

        String idName = classMetadata.getIdMetadata().getPropertyName();
        String idType = classMetadata.getIdMetadata().getPropertyType();
        for (PropertyMetadata propertyMetadata : classMetadata.getPropertiesMetadata()) {
            if (!propertyMetadata.isCollection()) {
                continue;
            }

            String joinTable  = propertyMetadata.getJoinTableName();
            DbTableData dbTableData = dbTableMap.getDbTableData(joinTable);
            if (dbTableData == null) {
                logger.error("There is no data for table " + joinTable);
                continue;
            }

            String joinColumn = propertyMetadata.getJoinColumnName();
            dbTableData.createIndexIfAbsent(joinColumn, idType);

            String associatedClassType = propertyMetadata.getAssociatedClassType();
            ClassMetadata associatedTypeMetadata = classMetadataMap.getClassMetadata(associatedClassType);
            Set<String> subClasses = null;
            if (associatedTypeMetadata != null) {
                subClasses = associatedTypeMetadata.getSubClassNames();
            }

            for (Object object : objects) {
                Object id = getProperty(object, idName);

                Collection<Object> fkIds = dbTableData.getKeys(joinColumn, id);
                if (fkIds == null) {
                    continue;
                }

                String elementName    = propertyMetadata.getElementColumnName();
                Collection collection = getCollection(propertyMetadata.getPropertyType());
                for (Object fkId : fkIds) {
                    Object[] row = dbTableData.getRow(fkId);
                    if (row == null || row.length == 0) {
                        logger.error("Key " + fkId + " refers to non-existent row in the table " + dbTableData.getTableName());
                        continue;
                    }

                    if (associatedTypeMetadata == null) {
                        Object elementId = dbTableData.getColumnValue(row, elementName, associatedClassType);
                        collection.add(elementId);
                    } else {
                        Object elementId = dbTableData.getColumnValue(row, elementName, associatedTypeMetadata.getIdMetadata().getPropertyType());
                        for (String subClass : subClasses) {
                            Object element = objectPool.getObject(subClass, elementId);
                            if (element != null) {
                                collection.add(element);
                                break;
                            }
                        }
                    }
                }

                setProperty(object, propertyMetadata.getPropertyName(), collection, false);
            }

            dbTableData.deleteIndex(joinColumn);
        }
    }

    private void hydrateCustomTypeRefs(ClassMetadata classMetadata) {
        String className = classMetadata.getClassName();
        String idName = classMetadata.getIdMetadata().getPropertyName();

        Collection<Object> objects = objectPool.getObjects(className);
        if (objects == null) {
            return;
        }

        for (PropertyMetadata propertyMetadata : classMetadata.getPropertiesMetadata()) {
            if (!propertyMetadata.isCustomType()) {
                continue;
            }

            DbTableData dbTableData = dbTableMap.getDbTableData(propertyMetadata.getColumnTableName());
            if (dbTableData == null) {
                continue;
            }

            String propertyType = propertyMetadata.getPropertyType();
            if (isEnumeration(propertyType)) {
                hydrateEnumRefs(objects, idName, propertyMetadata, dbTableData);
            } else {
                hydrateCustomRefs(objects, idName, propertyMetadata, dbTableData);
            }
        }
    }

    private void hydrateCustomRefs(
            Collection<Object> objects,
            String objIdName,
            PropertyMetadata propertyMetadata,
            DbTableData dbTableData) {
        String propertyType = propertyMetadata.getPropertyType();
        String columnName = propertyMetadata.getColumnName();
        String[] subClasses = getSubClassNames(propertyType);

        for (Object object : objects) {
            Object objId = getProperty(object, objIdName);
            Object[] row = dbTableData.getRow(objId);

            for (String subClass : subClasses) {
                ClassMetadata associatedTypeMetadata = classMetadataMap.getClassMetadata(subClass);
                if (associatedTypeMetadata == null) {
                    continue;
                }

                String associatedIdType = associatedTypeMetadata.getIdMetadata().getPropertyType();
                Object associatedObjId = dbTableData.getColumnValue(row, columnName, associatedIdType);
                Object associatedObj = objectPool.getObject(subClass, associatedObjId);
                if (associatedObj != null) {
                    setProperty(object, propertyMetadata.getPropertyName(), associatedObj, false);
                    break;
                }
            }
        }
    }

    private void hydrateEnumRefs(
            Collection<Object> objects,
            String objIdName,
            PropertyMetadata propertyMetadata,
            DbTableData dbTableData) {

        for (Object object : objects) {
            Object objId = getProperty(object, objIdName);
            Object[] row = dbTableData.getRow(objId);

            Object value = dbTableData.getColumnValue(row, propertyMetadata.getColumnName(), String.class.getName());
            Object enumObj = getEnumObject(propertyMetadata.getPropertyType(), value);
            if (enumObj != null) {
                setProperty(object, propertyMetadata.getPropertyName(), enumObj, false);
            }
        }
    }

    private Collection getCollection(String collectionType) {
        Collection collection = null;

        if (collectionType.equals("java.util.Set")) {
            collection = new HashSet();
        } else if (collectionType.equals("java.util.List")) {
            collection = new ArrayList();
        } else {
            logger.error("Unknown collection type: " + collectionType);
        }

        return collection;
    }

    private void setProperty(PropertyMetadata propMetadata, Object object, Object key) {
        String tableName = propMetadata.getColumnTableName();
        DbTableData dbTableData = dbTableMap.getDbTableData(tableName);
        if (dbTableData == null) {
            return;
        }

        String columnName = propMetadata.getColumnName();
        Object[] row = dbTableData.getRow(key);
        Object value = dbTableData.getColumnValue(row, columnName);

        String propertyName = propMetadata.getPropertyName();
        setProperty(object, propertyName, value);

        //
        // EXPERIMENTAL: Remove column after readings its value, as it is no longer
        // needed later.
        // Assumption: simple properties are not used as join columns
        //
        //dbTableData.setColumnValue(row, columnName, null);
    }

    private void setProperty(Object obj, String name, Object value) {
        setProperty(obj, name, value, true);
    }

    private void setProperty(Object obj, String name, Object value, boolean convertToTargetType) {
        //
        // TODO: check for null value (primitive and complex)
        //
        if (value == null) {
            return;
        }

        if (convertToTargetType) {
            value = convertToTargetType(obj, name, value);
        }

        try {
            ReflectionUtil.setProperty(obj, name, value);
        } catch (Exception e) {
            logger.error("Couldn't set the property: " + obj.getClass().getName() + "." + name + " = " + value.getClass().getName(), e);
        }
    }

    private Object getProperty(Object obj, String name) {
        return ReflectionUtil.getProperty(obj, name);
    }

    private Object convertToTargetType(Object object, String name, Object value) {
        if (value == null) {
            return null;
        }

        Class targetType = ReflectionUtil.getPropertyType(object, name);
        return ReflectionUtil.convert(value, targetType);
    }


    private String[] getSubClassNames(String rootClassName) {
        List<String> subClassNames = new ArrayList<String>();
        Class rootClass = ReflectionUtil.getClass(rootClassName);
        subClassNames.add(rootClassName);
        for (String subClassName : classMetadataMap.getClassNames()) {
            if (rootClass.isAssignableFrom(ReflectionUtil.getClass(subClassName))) {
                subClassNames.add(subClassName);
            }
        }

        return subClassNames.toArray(new String[0]);
    }

    private boolean isEnumeration(String type) {
        return ReflectionUtil.getClass(type).isEnum();
    }

    private Object getEnumObject(String enumType, Object value) {
        if (value == null) {
            return null;
        }

        Class enumTypeClass = ReflectionUtil.getClass(enumType);
        return ReflectionUtil.invokeMethod(enumTypeClass, "valueOf", value.toString());
    }

    private Map<String, Set<String>> getOneToManyTableClassNameDependencies() {
        Map<String, Set<String>> tableDependencies = new HashMap<String, Set<String>> ();

        for (String className : classMetadataMap.getClassNames()) {
            ClassMetadata classMetadata = classMetadataMap.getClassMetadata(className);
            if (classMetadata.isAbstractClass()) {
                continue;
            }

            for (PropertyMetadata property : classMetadata.getPropertiesMetadata()) {
                if (property.isCollection()) {
                    Set<String> classNames = tableDependencies.get(property.getJoinTableName());
                    if (classNames == null) {
                        classNames = new HashSet<String>();
                        tableDependencies.put(property.getJoinTableName(), classNames);
                    }
                    classNames.add(className);
                }
            }
        }

        return tableDependencies;
    }

    private void clearUnwantedDbTables(Set<String> requiredTables) {
        for (String dbTable : dbTableMap.getTableNames()) {
            if (requiredTables.contains(dbTable)) {
                continue;
            }
            DbTableData dbTableData = dbTableMap.getDbTableData(dbTable);
            if (dbTableData != null) {
                dbTableData.clear();
            }
        }
    }
}