
package edu.common.dynamicextensions.entitymanager;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.bind.JAXBException;

import net.sf.ehcache.CacheException;

import org.xml.sax.SAXException;

import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;

/**
 * The Interface CategoryManagerInterface.
 *
 * @author rajesh_patil
 */

public interface CategoryManagerInterface
{

	/**
	 * Persist category.
	 * @param category
	 *            the category
	 * @return the category interface
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 */
	void persistCategory(CategoryInterface category)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;


	/**
	 * Persist category metadata.
	 * @param category the category
	 * @param hibernateDAO
	 * @return the category interface
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 */
	CategoryInterface persistCategoryMetadata(CategoryInterface category,HibernateDAO hibernateDAO)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException, DynamicExtensionsApplicationException, DAOException;

	/**
	 * It will return the Category with the id as given identifier in the
	 * parameter.
	 *
	 * @param identifier
	 *            .
	 * @return category with given identifier.
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 */

	CategoryInterface getCategoryById(final Long identifier)
			throws DynamicExtensionsSystemException;

	/**
	 * It will return the CategoryAttribute with the id as given identifier in
	 * the parameter.
	 *
	 * @param identifier
	 *            .
	 * @return category with given identifier.
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 */

	CategoryAttributeInterface getCategoryAttributeById(final Long identifier)
			throws DynamicExtensionsSystemException;

	/**
	 * It will return the Category Association with the id as given identifier
	 * in the parameter.
	 *
	 * @param identifier
	 *            .
	 * @return category with given identifier.
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 */

	CategoryAssociationInterface getCategoryAssociationById(final Long identifier)
			throws DynamicExtensionsSystemException;

	/**
	 * It will return the Category with the name as given name in the parameter.
	 *
	 * @param name
	 *            the name
	 * @return category with given identifier.
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 */

	CategoryInterface getCategoryByName(final String name) throws DynamicExtensionsSystemException;

	/**
	 * Insert data.
	 *
	 * @param category
	 *            the category
	 * @param dataValue
	 *            the data value
	 * @param userId
	 *            the user id
	 * @return the long
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @return
	 * @deprecated Use {@link #insertData(CategoryInterface,Map<BaseAbstractAttributeInterface, Object>,SessionDataBean,Long...)} instead
	 */
	Long insertData(CategoryInterface category,
			Map<BaseAbstractAttributeInterface, Object> dataValue, Long... userId)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException;

	/**
	 * Insert data.
	 *
	 * @param category
	 *            the category
	 * @param dataValue
	 *            the data value
	 * @param sessionDataBean TODO
	 * @param userId
	 *            the user id
	 * @return the long
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @return
	 */
	Long insertData(CategoryInterface category,
			Map<BaseAbstractAttributeInterface, Object> dataValue, SessionDataBean sessionDataBean, Long... userId)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException;

	/**
	 * Insert data.
	 *
	 * @param category
	 *            the category
	 * @param dataValueMaps
	 *            the data value maps
	 * @param userId
	 *            the user id
	 * @return the list
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @return
	 * @deprecated Use {@link #insertData(CategoryInterface,List<Map<BaseAbstractAttributeInterface, Object>>,SessionDataBean,Long...)} instead
	 */
	List<Long> insertData(CategoryInterface category,
			List<Map<BaseAbstractAttributeInterface, Object>> dataValueMaps, Long... userId)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException;

	/**
	 * Insert data.
	 *
	 * @param category
	 *            the category
	 * @param dataValueMaps
	 *            the data value maps
	 * @param sessionDataBean TODO
	 * @param userId
	 *            the user id
	 * @return the list
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @return
	 */
	List<Long> insertData(CategoryInterface category,
			List<Map<BaseAbstractAttributeInterface, Object>> dataValueMaps, SessionDataBean sessionDataBean, Long... userId)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException;

	/**
	 * Insert data to category given category id and BaseAbstractInterface id to
	 * value map.
	 *
	 * @param categoryId
	 *            category id to which data entry should be done.
	 * @param dataValue
	 *            attribute id to value map.
	 * @return record entry identifier.
	 * @throws DynamicExtensionsApplicationException
	 *             thrown if error while inserting data.
	 * @throws DynamicExtensionsSystemException
	 *             thrown if attribute id does not match or system exception.
	 * @throws ParseException
	 *             thrown if fails to pars the date.
	 * @deprecated Use {@link #insertData(CategoryInterface,Map<String, Object>,SessionDataBean)} instead
	 */
	Long insertData(CategoryInterface categoryInterface, Map<String, Object> dataValue)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException,
			ParseException;

	/**
	 * Insert data to category given category id and BaseAbstractInterface id to
	 * value map.
	 * @param dataValue
	 *            attribute id to value map.
	 * @param sessionDataBean TODO
	 * @param categoryId
	 *            category id to which data entry should be done.
	 *
	 * @return record entry identifier.
	 * @throws DynamicExtensionsApplicationException
	 *             thrown if error while inserting data.
	 * @throws DynamicExtensionsSystemException
	 *             thrown if attribute id does not match or system exception.
	 * @throws ParseException
	 *             thrown if fails to pars the date.
	 */
	Long insertData(CategoryInterface categoryInterface, Map<String, Object> dataValue, SessionDataBean sessionDataBean)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException,
			ParseException;

	/**
	 * Gets the record by id.
	 *
	 * @param rootCatEntity
	 *            the root cat entity
	 * @param recordId
	 *            the record id
	 * @return the record by id
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 * @return
	 */
	Map<BaseAbstractAttributeInterface, Object> getRecordById(
			CategoryEntityInterface rootCatEntity, Long recordId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * Edits the data.
	 *
	 * @param catEntity
	 *            the cat entity
	 * @param attributeValues
	 *            the attribute values
	 * @param recordId
	 *            the record id
	 * @param userId
	 *            the user id
	 * @return true, if successful
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @throws SQLException
	 *             the SQL exception
	 * @return
	 * @deprecated Use {@link #editData(CategoryEntityInterface,Map<BaseAbstractAttributeInterface, Object>,Long,SessionDataBean,Long...)} instead
	 */
	boolean editData(CategoryEntityInterface catEntity,
			Map<BaseAbstractAttributeInterface, Object> attributeValues, Long recordId,
			Long... userId) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException;

	/**
	 * Edits the data.
	 *
	 * @param catEntity
	 *            the cat entity
	 * @param attributeValues
	 *            the attribute values
	 * @param recordId
	 *            the record id
	 * @param sessionDataBean TODO
	 * @param userId
	 *            the user id
	 * @return true, if successful
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @throws SQLException
	 *             the SQL exception
	 * @return
	 */
	boolean editData(CategoryEntityInterface catEntity,
			Map<BaseAbstractAttributeInterface, Object> attributeValues, Long recordId,
			SessionDataBean sessionDataBean, Long... userId) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException;

	/**
	 * Check if the subset of permissible values passed is valid.
	 *
	 * @param userDefinedDE
	 *            the user defined de
	 * @param desiredPVs
	 *            the desired p vs
	 * @return true or false depending on valid permissible values subset
	 */
	boolean isPermissibleValuesSubsetValid(UserDefinedDEInterface userDefinedDE,
			Map<String, Collection<SemanticPropertyInterface>> desiredPVs);

	/**
	 * getEntityRecordIdByRootCategoryEntityRecordId.
	 *
	 * @param rootCategoryEntityRecordId
	 *            the root category entity record id
	 * @param rootCategoryTableName
	 *            the root category table name
	 * @return the entity record id by root category entity record id
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 */
	Long getEntityRecordIdByRootCategoryEntityRecordId(Long rootCategoryEntityRecordId,
			String rootCategoryTableName) throws DynamicExtensionsSystemException;

	/**
	 * getEntityRecordIdByRootCategoryEntityRecordId.
	 *
	 * @param rootCategoryEntityRecordId
	 *            the root category entity record id
	 * @param rootCategoryTableName
	 *            the root category table name
	 * @return the root category entity record id by entity record id
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 */
	Long getRootCategoryEntityRecordIdByEntityRecordId(Long rootCategoryEntityRecordId,
			String rootCategoryTableName) throws DynamicExtensionsSystemException;

	/**
	 * It will fetch all the categories present.
	 *
	 * @return will return the collection of categories.
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 */
	Collection<CategoryInterface> getAllCategories() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException;

	/**
	 * Gets the related attribute values.
	 *
	 * @param rootCatEntity
	 *            the root cat entity
	 * @param recordId
	 *            the record id
	 * @return the related attribute values
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	Map<String, Map<String, Object>> getRelatedAttributeValues(
			CategoryEntityInterface rootCatEntity, Long recordId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			SQLException;

	/**
	 * Gets the all calculated category attributes.
	 *
	 * @return the all calculated category attributes
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 */
	Collection<CategoryAttributeInterface> getAllCalculatedCategoryAttributes()
			throws DynamicExtensionsSystemException;

	/**
	 * Update category attributes.
	 *
	 * @param categoryAttributeCollection
	 *            the category attribute collection
	 * @return the collection
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 * @deprecated Use {@link #updateCategoryAttributes(Collection<CategoryAttributeInterface>,SessionDataBean)} instead
	 */
	Collection<CategoryAttributeInterface> updateCategoryAttributes(
			Collection<CategoryAttributeInterface> categoryAttributeCollection)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * Update category attributes.
	 *
	 * @param categoryAttributeCollection
	 *            the category attribute collection
	 * @param sessionDataBean TODO
	 * @return the collection
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 */
	Collection<CategoryAttributeInterface> updateCategoryAttributes(
			Collection<CategoryAttributeInterface> categoryAttributeCollection, SessionDataBean sessionDataBean)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * Gets the record by record entry id.
	 *
	 * @param formId
	 *            the form id
	 * @param recordEntryIdList
	 *            the record entry id list
	 * @param recordEntryStaticId
	 *            the record entry static id
	 * @return the record by record entry id
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException
	 *             the dynamic extensions application exception
	 * @throws CacheException
	 *             the cache exception
	 * @throws DAOException
	 *             the DAO exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	List<Map<BaseAbstractAttributeInterface, Object>> getRecordByRecordEntryId(Long formId,
			List<Long> recordEntryIdList, Long recordEntryStaticId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			CacheException, DAOException, SQLException;

	List<Map<BaseAbstractAttributeInterface, Object>> getRecordByRecordEntryId(ContainerInterface form,
			List<Long> recordEntryIdList, Long recordEntryStaticId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			CacheException, DAOException, SQLException;	
	/**
	 * This method will found out the record Entry id associated with the
	 * deRecordId which is of Entity & entered for the dynEntContainerId
	 * container of the category of whose static Entity id is
	 * recordEntryStaticId.
	 *
	 * @param dynEntContainerId
	 *            category root container Id.
	 * @param deRecordId
	 *            dynamicRecordId of the Entity from which that category Entity
	 *            is created.
	 * @param recordEntryStaticId
	 *            Entity id of the static entity which is hooked to the DE
	 *            model.
	 * @return recordEntry Id associated with this record.
	 * @throws DynamicExtensionsSystemException
	 *             the dynamic extensions system exception
	 * @exception DynamicExtensionsSystemException
	 *                Exception.
	 * @throws DynamicExtensionsCacheException
	 */
	long getRecordEntryIdByEntityRecordId(Long dynEntContainerId, Long deRecordId,
			Long recordEntryStaticId) throws DynamicExtensionsSystemException, DynamicExtensionsCacheException;

	/**
	 * This method will return the identifier of the category record from given recordEntry Identifier
	 * & its static entity identifier.
	 * @param formId form identifier.
	 * @param recordEntryId record entry id
	 * @param recordEntryStaticId entity id of the static entity.
	 * @return dynamic record id of the category.
	 * @throws DynamicExtensionsApplicationException exception.
	 * @throws DynamicExtensionsSystemException exception.
	 * @throws DAOException exception.
	 */
	Long getCategoryRecordIdByRecordEntryId(Long formId, Long recordEntryId,
			Long recordEntryStaticId) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException, DAOException;

	/**
	 * It will persist dynamic extension object.
	 * @param categoryInterface Category to persist.
	 * @param hibernateDAO
	 * @return CategoryInterface category.
	 * @throws DynamicExtensionsSystemException exception.
	 * @throws DynamicExtensionsApplicationException exception.
	 */
	Stack<String> persistDynamicExtensionObjectForCategory(CategoryInterface categoryInterface,HibernateDAO hibernateDAO)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * Gets the all static category beans.
	 *
	 * @return  the container Id of the DE entities/categories that are associated with given static hook entity
	 *
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	Collection<NameValueBean> getAllStaticCategoryBeans() throws DynamicExtensionsSystemException;

	String getCategoryProcessorClass(Long rootContainerId)throws DynamicExtensionsSystemException;


	Long getContainerIdByFormContextId(Long formContextId, SessionDataBean sessionDataBean) throws DynamicExtensionsSystemException, DAOException, JAXBException, SAXException;


}
