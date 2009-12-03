
package edu.common.dynamicextensions.entitymanager;

import java.util.Collection;

import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.AssociationTreeObject;
import edu.common.dynamicextensions.xmi.DynamicQueryList;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.dao.HibernateDAO;

/**
*
* @author rajesh_patil
*
*/
public interface EntityGroupManagerInterface
{

	/**
	 * This method persists an entity group and the associated entities and also creates the data table
	 * for the entities only if the hibernateDao is not provided.
	 * if HibernateDao is provided then its the responsibility of the caller to execute all
	 * the Queries which are returned from this method just before commiting the hibernate Dao.
	 * @param entityGroup entity group to be save
	 * @param hibernatedao dao which should be used (optional).
	 * @return queryList to be executed.
	 * @throws DynamicExtensionsSystemException exception
	 * @throws DynamicExtensionsApplicationException exception
	 */
	DynamicQueryList persistEntityGroup(EntityGroupInterface group, HibernateDAO... hibernateDao)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * This method persists an entity group and the associated entities without creating the data table
	 * for the entities.
	 * @param entityGroup entity group to be save
	 * @param hibernatedao dao which should be used (optional).
	 * @return queryList to be executed
	 * @throws DynamicExtensionsSystemException exception
	 * @throws DynamicExtensionsApplicationException exception
	 */
	DynamicQueryList persistEntityGroupMetadata(EntityGroupInterface entityGroup,
			HibernateDAO... hibernateDao) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException;

	/**
	 * This method returns the entity group given the short name for the same.
	 * @param shortName short name for entity group
	 * @return entityGroupInterface entity group interface
	 * @throws DynamicExtensionsSystemException
	 */
	EntityGroupInterface getEntityGroupByShortName(String shortName)
			throws DynamicExtensionsSystemException;

	/**
	 * @param name
	 * @return EntityGroupInterface EntityGroupInterface
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	EntityGroupInterface getEntityGroupByName(String name) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException;

	/**
	 * @param identifier
	 * @return
	 */
	Collection<NameValueBean> getMainContainer(Long identifier)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * getAllEntityGroupBeans.
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	Collection<NameValueBean> getAllEntityGroupBeans() throws DynamicExtensionsSystemException;

	/**
	*
	* @param entityGroupInterface
	* @return
	* @throws DynamicExtensionsApplicationException
	*/
	Collection<AssociationTreeObject> getAssociationTree() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException;

	/**
	 * validateEntityGroup.
	 * @param entityGroup
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	boolean validateEntityGroup(EntityGroupInterface entityGroup)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException;

	/**
	 * This method checks if the entity group can be created with the given name or not.
	 * @param entityGroup
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	void checkForDuplicateEntityGroupName(EntityGroupInterface entityGroup)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException;

	/**
	 * @param entityGroup
	 * @param tagKey
	 * @return
	 */
	String getTaggedValue(EntityGroupInterface entityGroup, String tagKey);
}
