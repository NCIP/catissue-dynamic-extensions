package edu.wustl.dynamicextensions.caching.metadata;

/**
 *
 * @author Vinayak Pawar (vinayak.pawar@krishagni.com)
 */
public interface PropertyMetadata {
    public ClassMetadata getClassMetadata();
       
    public boolean isId();
    
    public String getPropertyName();
    
    public String getColumnTableName();
    
    public String getColumnName();
    
    public String getPropertyType();
    
    public boolean isCustomType();
    
    public boolean isAssociation();
    
    public boolean isCollection();
    
    public String getJoinTableName();
    
    public String getJoinColumnName();
    
    public String getAssociatedClassType();
            
    public String getElementColumnName();    
        
    public String getJoinType();
}
