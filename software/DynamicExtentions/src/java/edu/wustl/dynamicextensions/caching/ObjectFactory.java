package edu.wustl.dynamicextensions.caching;

import java.util.Collection;

/**
 *
 * @author Vinayak Pawar (vinayak.pawar@krishagni.com)
 */
public interface ObjectFactory {
    public void createObjects();
    
    public void createObjects(String ... classNames);
        
    public Collection getObjects(String className);
    
    public void removeObjects(String className);
    
    public Object getPropertyValue(String className, String propertyName, Object id);
}
