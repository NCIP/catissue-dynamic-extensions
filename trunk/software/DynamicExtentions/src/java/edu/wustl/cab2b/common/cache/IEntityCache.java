
package edu.wustl.cab2b.common.cache;

import java.util.Collection;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.wustl.cab2b.common.beans.MatchedClass;

/**
 * @author gautam_shetty
 */
public interface IEntityCache
{

	/**
	 * Refreshes the entity cache.
	 */
	void refreshCache();

	/**
	 * Returns the Entity objects whose fields match with the respective not null 
	 * fields in the passed entity object.
	 * @param entity The entity object.
	 * @return the Entity objects whose fields match with the respective not null 
	 * fields in the passed entity object.
	 */
	MatchedClass getEntityOnEntityParameters(Collection<EntityInterface> entityCollection);

	/**
	 * Returns the Entity objects whose Attribute fields match with the respective not null 
	 * fields in the passed Attribute object.
	 * @param entity The entity object.
	 * @return the Entity objects whose Attribute fields match with the respective not null 
	 * fields in the passed Attribute object.
	 */
	MatchedClass getEntityOnAttributeParameters(
			Collection<AttributeInterface> attributeCollection);

	/**
	 * Returns the Entity objects whose Permissible Value fields match with the respective not null 
	 * fields in the passed Attribute object.
	 * @param entity The entity object.
	 * @return the Entity objects whose Permissible Value fields match with the respective not null 
	 * fields in the passed Permissible Value object.
	 */
	MatchedClass getEntityOnPermissibleValueParameters(
			Collection<PermissibleValueInterface> PVCollection);

	/**
	* Returns the Entity objects whose source classes fields match with the respective not null 
	* fields in the passed entity object.
	* @param entity The entity object.
	* @return the Entity objects whose source classes fields match with the respective not null 
	* fields in the passed entity object.
	*/
	MatchedClass getCategories(Collection<EntityInterface> entityCollection);

	/**
	 * Returns the Entity objects whose attributes's source classes fields match with the respective not null 
	 * fields in the passed entity object.
	 * @param entity The entity object.
	 * @return the Entity objects whose attributes's source classes fields match with the respective not null 
	 * fields in the passed entity object.
	 */
	MatchedClass getCategoriesAttributes(Collection<AttributeInterface> attributeCollection);

	/**
	 * This method adds entity and its other details like associaion and permissible values into the cache.
	 * 
	 * @param entity entity to add
	 */
	void addEntityToCache(EntityInterface entity);
	
	/**
	 * It will return all the categories present in the Database .
	 * @return Collection of the CategoryInterface in the database.
	 */
	Collection<CategoryInterface> getAllCategories(); 
		
    
    /**
     * It will return the Category with the id as given identifier in the parameter.
     * @param identifier.
     * @return category with given identifier.
     */
    CategoryInterface getCategoryById(Long identifier); 
    	
    
    /**
     * It will return the CategoryAttribute with the id as given identifier in the parameter.
     * @param identifier
     * @return categoryAttribute with given identifier
     */
    CategoryAttributeInterface getCategoryAttributeById(Long identifier);
    	
    
    /**
     * It will return the CategoryEntity with the id as given identifier in the parameter.
     * @param identifier
     * @return categoryEntity with given identifier
     */
    CategoryEntityInterface getCategoryEntityById(Long identifier);
    	
    
    /**
     * It will return the Container with the id as given identifier in the parameter.
     * @param identifier
     * @return Container with given identifier
     */
    ContainerInterface getContainerById(Long identifier);
    	
    
    
    /**
     * It will return the CategoryAssociation with the id as given identifier in the parameter.
     * @param identifier
     * @return CategoryAssociation with given identifier
     */
    CategoryAssociationInterface getCategoryAssociationById(Long identifier);
    	
    
    /**
     * It will return the Control with the id as given identifier in the parameter.
     * @param identifier
     * @return Control with given identifier
     */
    ControlInterface getControlById(Long identifier);
    	
    
}