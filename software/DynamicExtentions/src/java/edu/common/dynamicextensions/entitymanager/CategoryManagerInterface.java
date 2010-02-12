
package edu.common.dynamicextensions.entitymanager;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.CacheException;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.dao.exception.DAOException;

/**
 *
 * @author rajesh_patil
 *
 */
public interface CategoryManagerInterface
{

	/**
	 *
	 * @param category
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	CategoryInterface persistCategory(CategoryInterface category)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 *
	 * @param category
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	CategoryInterface persistCategoryMetadata(CategoryInterface category)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 *
	 * @param category
	 * @param dataValue
	 * @param userId
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	Long insertData(CategoryInterface category,
			Map<BaseAbstractAttributeInterface, Object> dataValue, Long... userId)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException;

	/**
	 *
	 * @param category
	 * @param dataValueMaps
	 * @param userId
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	List<Long> insertData(CategoryInterface category,
			List<Map<BaseAbstractAttributeInterface, Object>> dataValueMaps, Long... userId)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException;

	/**
	 * @param rootCatEntity
	 * @param recordId
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws SQLException
	 */
	Map<BaseAbstractAttributeInterface, Object> getRecordById(
			CategoryEntityInterface rootCatEntity, Long recordId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * @param catEntity
	 * @param attributeValues
	 * @param recordId
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 * @throws SQLException
	 */
	boolean editData(CategoryEntityInterface catEntity,
			Map<BaseAbstractAttributeInterface, Object> attributeValues, Long recordId,
			Long... userId) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException, SQLException;

	/**
	 * Check if the subset of permissible values passed is valid.
	 * @param userDefinedDE
	 * @param desiredPVs
	 * @return true or false depending on valid permissible values subset
	 */
	boolean isPermissibleValuesSubsetValid(UserDefinedDEInterface userDefinedDE,
			Map<String, Collection<SemanticPropertyInterface>> desiredPVs);

	/**
	 * getEntityRecordIdByRootCategoryEntityRecordId.
	 * @throws DynamicExtensionsSystemException
	 */
	Long getEntityRecordIdByRootCategoryEntityRecordId(Long rootCategoryEntityRecordId,
			String rootCategoryTableName) throws DynamicExtensionsSystemException;

	/**
	 * getEntityRecordIdByRootCategoryEntityRecordId.
	 * @throws DynamicExtensionsSystemException
	 */
	Long getRootCategoryEntityRecordIdByEntityRecordId(Long rootCategoryEntityRecordId,
			String rootCategoryTableName) throws DynamicExtensionsSystemException;

	/**
	 * It will fetch all the categories present
	 * @return will return the collection of categories.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	Collection<CategoryInterface> getAllCategories() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException;

	/**
	 * @param rootCatEntity
	 * @param recordId
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws SQLException
	 */
	Map<String, Map<String, Object>> getRelatedAttributeValues(
			CategoryEntityInterface rootCatEntity, Long recordId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			SQLException;

	/**
	 *
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	Collection<CategoryAttributeInterface> getAllCalculatedCategoryAttributes()
			throws DynamicExtensionsSystemException;

	/**
	 *
	 * @param categoryAttributeCollection
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	Collection<CategoryAttributeInterface> updateCategoryAttributes(
			Collection<CategoryAttributeInterface> categoryAttributeCollection)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * @param formId
	 * @param recordEntryIdList
	 * @param recordEntryStaticId
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws CacheException
	 * @throws DAOException
	 * @throws SQLException
	 */
	public List<Map<BaseAbstractAttributeInterface, Object>> getRecordByRecordEntryId(Long formId,
			List<Long> recordEntryIdList, Long recordEntryStaticId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			CacheException, DAOException, SQLException;

}
