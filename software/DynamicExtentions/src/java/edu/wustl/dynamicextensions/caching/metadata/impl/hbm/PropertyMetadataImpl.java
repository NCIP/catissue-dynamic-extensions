package edu.wustl.dynamicextensions.caching.metadata.impl.hbm;

import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.type.AssociationType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.CustomType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.Type;

import edu.wustl.dynamicextensions.caching.metadata.ClassMetadata;
import edu.wustl.dynamicextensions.caching.metadata.PropertyMetadata;

/**
 *
 * @author Vinayak Pawar (vinayak.pawar@krishagni.com)
 */
public class PropertyMetadataImpl implements PropertyMetadata {    
    private ClassMetadata classMetadata;
    
    private boolean isId;
    
    private String propertyName;
        
    private AbstractEntityPersister persister;
    
    public PropertyMetadataImpl(ClassMetadata classMetadata, String propertyName, AbstractEntityPersister persister) {
        this(classMetadata, propertyName, persister, false);
    }
    
    public PropertyMetadataImpl(ClassMetadata classMetadata, String propertyName, AbstractEntityPersister persister, boolean isId) {
        this.classMetadata = classMetadata;
        this.propertyName = propertyName;
        this.persister = persister;
        this.isId = isId;
    }
    
    public ClassMetadata getClassMetadata() {
        return classMetadata;
    }
        
    public boolean isId() {
        return isId;
    }
    
    public String getPropertyName() {
        return propertyName;
    }
    
    public String getColumnTableName() {
        if (isId) {
            return persister.getTableName();
        }
        return persister.getPropertyTableName(propertyName);
    }
    
    //
    // NOTE: At present, we handle only one column mapped to a property
    //
    public String getColumnName() {
        String[] columnNames = persister.getPropertyColumnNames(propertyName);
        return (columnNames != null && columnNames.length == 1) ? columnNames[0] : null;
    }
    
    public String getPropertyType() {
        Type type = persister.getPropertyType(propertyName);
        return type.getReturnedClass().getName();
    }
        
    public boolean isCustomType() {
        Type type = persister.getPropertyType(propertyName);
        return type instanceof CustomType;
    }
    
    public boolean isAssociation() {
        Type type = persister.getPropertyType(propertyName);
        return type.isAssociationType();
    }
    
    public boolean isCollection() {
        Type type = persister.getPropertyType(propertyName);
        return type.isCollectionType();
    }
    
    public String getJoinTableName() {
        Type type = persister.getPropertyType(propertyName);
        String tableName = null;
        if (type.isAssociationType()) {
            AssociationType associationType = (AssociationType)type;
            tableName = associationType.getAssociatedJoinable(persister.getFactory()).getTableName();
        }
        
        return tableName;
    }
    
    public String getJoinColumnName() {
        Type type = persister.getPropertyType(propertyName);
        String columnName = null;
        if (type.isAssociationType()) {
            AssociationType associationType = (AssociationType)type;
            String[] columnNames = associationType.getAssociatedJoinable(persister.getFactory()).getKeyColumnNames();
            columnName = (columnNames != null && columnNames.length == 1) ? columnNames[0] : null;
        }
        return columnName;                
    }
    
    public String getAssociatedClassType() {
        Type type = persister.getPropertyType(propertyName);
        String classType = null;
        if (type.isAssociationType() && !type.isCollectionType()) {
            AssociationType associationType = (AssociationType)type;
            classType = associationType.getAssociatedEntityName(persister.getFactory());
        } else if (type.isCollectionType()) {
            CollectionType collType = (CollectionType)type;
            classType = collType.getElementType(persister.getFactory()).getReturnedClass().getName();            
        }
        
        return classType;
    }
        
    public String getElementColumnName() {
        Type type = persister.getPropertyType(propertyName);
        String elementName = null;
        if (type.isCollectionType()) {
            CollectionType collType = (CollectionType)type;
            Joinable joinable = collType.getAssociatedJoinable(persister.getFactory());
            if (joinable instanceof AbstractCollectionPersister) {
                String[] elementNames = ((AbstractCollectionPersister)joinable).getElementColumnNames();
                elementName = (elementNames != null && elementNames.length == 1) ? elementNames[0] : null;
            }
        }
        
        return elementName;
    }            
        
    public String getJoinType() {
        String joinType = null;
        Type type = persister.getPropertyType(propertyName);        
        if (type.isAssociationType()) {
            if (type instanceof CollectionType) {
                joinType = "OneToMany";
            } else if (type instanceof ManyToOneType) {
                joinType = "ManyToOne";
            } else if (type instanceof OneToOneType) {
                joinType = "OneToOne";
            } 
        }
        return joinType;
    }
}