
package edu.wustl.cab2b.common.cache;

import java.util.Collection;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
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
	MatchedClass getEntityOnAttributeParameters(Collection<AttributeInterface> attributeCollection);

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
	 * This method adds entity and its other details like associaion and permissible values into the cache.
	 *
	 * @param entity entity to add
	 */
	void addEntityToCache(EntityInterface entity);

	/**
	 * It will return the Container with the id as given identifier in the parameter.
	 * @param identifier
	 * @return Container with given identifier
	 * @throws DynamicExceptionsCacheException
	 */
	ContainerInterface getContainerById(Long identifier) throws DynamicExtensionsCacheException;

	/**
	 * It will return the Control with the id as given identifier in the parameter.
	 * @param identifier
	 * @return Control with given identifier
	 */
	ControlInterface getControlById(Long identifier);

}