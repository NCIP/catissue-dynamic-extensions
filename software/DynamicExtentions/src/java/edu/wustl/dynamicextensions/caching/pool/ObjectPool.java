package edu.wustl.dynamicextensions.caching.pool;

import java.util.Collection;

/**
 *
 * @author Vinayak Pawar (vinayak.pawar@krishagni.com)
 */
public interface ObjectPool {  
    public String[] getObjectTypes();
	
    public Collection getObjects(String className);
    
    public void addObject(String className, Object objId, Object object);
    
    public Object getObject(String className, Object objId);
	
	public void removeObjects(String className);
    
    public void clear();
}
