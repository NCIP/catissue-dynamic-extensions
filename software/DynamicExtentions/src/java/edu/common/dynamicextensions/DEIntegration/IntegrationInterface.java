/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author
 *@version 1.0
 */

package edu.common.dynamicextensions.DEIntegration;

import java.sql.SQLException;
import java.util.Collection;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.dao.exception.DAOException;

/**
 * This interface is acting as an interface for host application to communicate with DE
 * @author shital_lawhale
 *
 */

public interface IntegrationInterface
{

	/**
	 *
	 * @param hookEntityId
	 * @return the container Id of the DE entities that are associated with given static hook entity
	 */
	Collection getDynamicEntitiesContainerIdFromHookEntity(Long hookEntityId)
			throws DynamicExtensionsSystemException;

	/**
	 *
	 * @param hookEntityId
	 * @return  the container Id of the DE entities/categories that are associated with given static hook entity
	 * @throws DynamicExtensionsSystemException
	 */
	Collection getCategoriesContainerIdFromHookEntity(Long hookEntityId)
			throws DynamicExtensionsSystemException;

	/**
	 *
	 * @param hookEntityRecId
	 * @param containerId
	 * @param hookEntityId
	 * @return the category or form record id based on the containerId and hookentityRecId
	 */
	Collection getDynamicEntityRecordIdFromHookEntityRecordId(String hookEntityRecId,
			Long containerId, Long hookEntityId) throws DynamicExtensionsSystemException,
			SQLException;

	/**
	 *
	 * @param categoryContainerId
	 * @param staticRecId
	 * @param hookEntityId
	 * @return the record id of the category depending on hook entity record id.
	 */
	Collection getCategoryRecIdBasedOnHookEntityRecId(Long categoryContainerId,
			Long staticRecId, Long hookEntityId) throws DynamicExtensionsSystemException,
			SQLException,DAOException;

	/**
	 * This method associate the static hook entity with the Dynamic entity
	 * @param hookEntityId
	 * @param dynamicEntityId
	 * @param isEntityFromXmi
	 * @param isCategory
	 * @return
	 */
	Long addAssociation(Long hookEntityId, Long dynamicEntityId, boolean isEntityFromXmi,
			boolean isCategory) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException, BizLogicException;

	/**
	 *
	 * @param containerId
	 * @return whether this entity is simple DE form /category.
	 */
	boolean isCategory(Long containerId) throws DynamicExtensionsSystemException;

}
