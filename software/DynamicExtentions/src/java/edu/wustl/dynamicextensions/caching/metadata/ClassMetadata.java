package edu.wustl.dynamicextensions.caching.metadata;

import java.util.Collection;
import java.util.Set;

/**
 *
 * @author Vinayak Pawar (vinayak.pawar@krishagni.com)
 */
public interface ClassMetadata {
    public String getClassName();
    
    public Set<String> getSubClassNames();
        
    public String getTableName();
    
    public PropertyMetadata getIdMetadata();
    
    public boolean isAbstractClass();
    
    public String getParentClassName();
    
    public Collection<PropertyMetadata> getPropertiesMetadata();
    
    public PropertyMetadata getProperty(String name);

	public PropertyMetadata getAssociation(String targetEntityName);
}