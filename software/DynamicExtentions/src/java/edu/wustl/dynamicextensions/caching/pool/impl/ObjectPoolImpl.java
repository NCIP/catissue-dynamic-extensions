package edu.wustl.dynamicextensions.caching.pool.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import edu.wustl.dynamicextensions.caching.pool.ObjectPool;
import java.util.Collections;

/**
 *
 * @author Vinayak Pawar (vinayak.pawar@krishagni.com)
 */
public class ObjectPoolImpl implements ObjectPool {
    private ConcurrentMap<String, ConcurrentMap<Object, Object>> objectsMap = new ConcurrentHashMap<String, ConcurrentMap<Object, Object>>();
    
    private ObjectPoolImpl() {
        
    }
    
    public static ObjectPool createObjectPool() {
        return new ObjectPoolImpl();
    }
    
    public String[] getObjectTypes() {
	    return objectsMap.keySet().toArray(new String[0]);
	}
	
	public Collection getObjects(String className) {
        Collection objects = null;
        
        Map<Object, Object> classObjMap = objectsMap.get(className);        
        if (classObjMap != null) {
            objects = Collections.unmodifiableCollection(classObjMap.values());
        }
        
        return objects;        
    }
    
    public void addObject(String className, Object objId, Object object) {
        ConcurrentMap<Object, Object> classObjMap = objectsMap.get(className);
        if (classObjMap == null) {
            classObjMap = new ConcurrentHashMap<Object, Object>();
            objectsMap.putIfAbsent(className, classObjMap);
            classObjMap = objectsMap.get(className); // this object map may not be same as above
        }        
        classObjMap.put(objId, object);
    }
    
    public Object getObject(String className, Object objId) {
        Object obj = null;
        
        ConcurrentMap<Object, Object> classObjMap = objectsMap.get(className);
        if (classObjMap != null) {
            obj = classObjMap.get(objId);
        }
        
        return obj;
    }    
    
    public void removeObjects(String className) {
	    ConcurrentMap<Object, Object> objects = objectsMap.remove(className);
		if (objects != null) {
		    objects.clear();
		}
	}
	
	public void clear() {
        for (ConcurrentMap<Object, Object> objects : objectsMap.values()) {
            objects.clear();
        }
        objectsMap.clear();
    }
}