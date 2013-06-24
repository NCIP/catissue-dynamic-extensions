
package edu.common.dynamicextensions.upgrade;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.common.dynamicextensions.domain.AttributeTypeInformation;
import edu.common.dynamicextensions.domain.DynamicExtensionBaseDomainObject;
import edu.common.dynamicextensions.domain.UserDefinedDE;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.entitymanager.DynamicExtensionsQueryBuilderConstantsInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

/**
 * The Class CategoryPVReferenceUpgrade.
 * @author deepali_ahirrao
 */
public class CategoryPVReferenceUpgrade implements DynamicExtensionsQueryBuilderConstantsInterface
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(CategoryPVReferenceUpgrade.class);

	/** The attr type info map. */
	private static Map<Long, Long> attrTypeInfoMap = new HashMap<Long, Long>();

	/** The data element id map. */
	private static Map<Long, Long> dataElementIdMap = new HashMap<Long, Long>();

	/** Map of entity attribute dataElement object. */
	private static List<String> errorList = new ArrayList<String>();

	/** The entity manager util. */
	private final EntityManagerUtil entityManagerUtil = new EntityManagerUtil();

	/**
	 * The main method.
	 *
	 * @param args arguments to main.
	 *
	 * @throws DAOException the DAO exception
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	public static void main(String[] args) throws DynamicExtensionsSystemException, DAOException
	{
		CategoryPVReferenceUpgrade pvUpgrade = new CategoryPVReferenceUpgrade();
		// Upgrade Category PV Reference.
		pvUpgrade.upgradeCategoryPVReference();

		// Upgrade Skip Logic PV Reference in category.
		pvUpgrade.upgradeCategorySkipLogicPVReference();

		LOGGER.info("Checking for errors---------");
		if (!errorList.isEmpty())
		{
			LOGGER
					.info("-----------Following missing PVs references are corrected----------------------------");
			LOGGER.info(errorList);
		}
		LOGGER.info("-------------Done--------------------------------");
	}

	/**
	 * Upgrade category pv reference.
	 *
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 * @throws DAOException the DAO exception
	 */
	private void upgradeCategoryPVReference() throws DynamicExtensionsSystemException, DAOException
	{
		// get all category attributes having PVs
		// Populate map: key: data element ID    value: category attribute ID
		StringBuffer allCategoryAttributePVsQuery = new StringBuffer(
				"select de.identifier,de.CATEGORY_ATTRIBUTE_ID from DYEXTN_DATA_ELEMENT de where de.CATEGORY_ATTRIBUTE_ID is not null");

		Map<Long, Long> idMap = getIdVsIdMap(allCategoryAttributePVsQuery.toString(), null);

		upgradePVReferences(idMap);
	}

	/**
	 * Upgrade category skip logic pv reference.
	 *
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 * @throws DAOException the DAO exception
	 */
	private void upgradeCategorySkipLogicPVReference() throws DynamicExtensionsSystemException,
			DAOException
	{
		StringBuffer fetchSkipLogicId = new StringBuffer(
				"select SKIP_LOGIC_ATTRIBUTE_ID,identifier from DYEXTN_DATA_ELEMENT where SKIP_LOGIC_ATTRIBUTE_ID is not null");

		StringBuffer fetchCategoryAttributeId = new StringBuffer(
				"select identifier,TARGET_SKIP_LOGIC_ID from DYEXTN_SKIP_LOGIC_ATTRIBUTE where identifier = ?");

		Map<Long, Long> idMap = new HashMap<Long, Long>();

		Map<Long, Long> skipLogicIdVsIdMap = getIdVsIdMap(fetchSkipLogicId.toString(), null);
		Set<Entry<Long, Long>> entrySet = skipLogicIdVsIdMap.entrySet();

		// Initializing ColumnValueBean with Skip Logic Attribute IDs.
		for (Entry<Long, Long> entry : entrySet)
		{
			List<ColumnValueBean> columnValueBean = new LinkedList<ColumnValueBean>();
			columnValueBean.add(new ColumnValueBean(IDENTIFIER, entry.getKey()));

			Map<Long, Long> IdVsCategoryAttirbuteId = getIdVsIdMap(fetchCategoryAttributeId.toString(),
					columnValueBean);

			entrySet = IdVsCategoryAttirbuteId.entrySet();

			// Populating idMap with DataElementIdVsCategoryAttributeId
			for (Entry<Long, Long> entry1 : entrySet)
			{
				idMap.put(skipLogicIdVsIdMap.get(entry1.getKey()), entry1.getValue());
			}
		}
		upgradePVReferences(idMap);
	}

	/**
	 * Upgrade pv references.
	 *
	 * @param idMap the id map
	 *
	 * @throws DAOException the DAO exception
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	private void upgradePVReferences(Map<Long, Long> idMap)
			throws DynamicExtensionsSystemException, DAOException
	{
		for (Entry<Long, Long> entryObject : idMap.entrySet())
		{
			Long categoryAttributeId = entryObject.getValue();
			processCategoryAttribute(categoryAttributeId, entryObject.getKey());
		}
	}

	/**
	 * Process category attribute.
	 *
	 * @param categoryAttributeId categoryAttributeId
	 * @param dataElementId dataElementId
	 *
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 * @throws DAOException the DAO exception
	 */
	private void processCategoryAttribute(Long categoryAttributeId, Long dataElementId)
			throws DynamicExtensionsSystemException, DAOException
	{
		// get associated abstract attribute
		Long abstractAttributeID = getAbstractAttributeId(categoryAttributeId);
		LOGGER.info("abstractAttributeID--->" + abstractAttributeID);
		// check if its attribute or association
		boolean isAttribute = isAttributeOrAssociation(abstractAttributeID);
		if (!isAttribute)
		{
			LOGGER.info("is ASSOCIATION--->" + abstractAttributeID);
			// for association -- get target entity attribute
			Long attributeId = getAttributeForAssociationId(abstractAttributeID);
			abstractAttributeID = attributeId;
		}

		HibernateDAO hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();

		try
		{
			// get category attribute data element
			UserDefinedDE catAttrUserDefinedDE = (UserDefinedDE) EntityManager
					.getObjectByIdentifier(UserDefinedDE.class.getName(), dataElementId.toString(),
							hibernateDAO);

			// get category attribute pv collection
			LOGGER.info("Category Data Element id : " + catAttrUserDefinedDE.getId());

			// get entity attribute data element
			Long attrTypeInfoID = getAttrTypeInfoId(abstractAttributeID);
			Long dataElementID = getDataElementId(attrTypeInfoID);
			Collection<PermissibleValueInterface> attrPVCollection = null;
			List<PermissibleValueInterface> missingPvCollection = new ArrayList<PermissibleValueInterface>();
			if (dataElementID == null)
			{
				errorList.add("Permissible values are not present for abstract attribute id:"
						+ abstractAttributeID);
				LOGGER.info("Permissible values are not present for abstract attribute id:"
						+ abstractAttributeID);
				attrPVCollection = addPVsToOriginalAttribute(attrTypeInfoID, catAttrUserDefinedDE
						.getPermissibleValueCollection(), hibernateDAO);

			}
			else
			{
				UserDefinedDE originalAttrUserDefinedDE = getEntityAttributeDataElement(
						dataElementID, hibernateDAO);

				// get entity attribute pv collection
				attrPVCollection = originalAttrUserDefinedDE.getPermissibleValueCollection();

				// get references of entity attribute PVs
				missingPvCollection.addAll(setPVReference(catAttrUserDefinedDE, attrPVCollection));

				//				attrPVCollection = addPVsToOriginalAttribute(attrTypeInfoID, missingPvCollection,
				//						hibernateDAO);
			}
			//set default value
			missingPvCollection.addAll(setDefValuePVReference(catAttrUserDefinedDE,
					attrPVCollection));

			if (!missingPvCollection.isEmpty())
			{
				attrPVCollection = addPVsToOriginalAttribute(attrTypeInfoID, missingPvCollection,
						hibernateDAO);
			}
			// save data element
			saveDataElement(catAttrUserDefinedDE, hibernateDAO);
			//}
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(hibernateDAO);
		}
	}

	/**
	 * Adds the p vs to original attribute.
	 *
	 * @param attrTypeInfoID the attr type info id
	 * @param catAttrPVCollection the cat attr pv collection
	 * @param hibernateDAO the hibernate dao
	 *
	 * @return the collection< permissible value interface>
	 *
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 * @throws DAOException the DAO exception
	 */
	private Collection<PermissibleValueInterface> addPVsToOriginalAttribute(Long attrTypeInfoID,
			Collection<PermissibleValueInterface> catAttrPVCollection, HibernateDAO hibernateDAO)
			throws DynamicExtensionsSystemException, DAOException
	{
		AttributeTypeInformation attributeTypeInformation = (AttributeTypeInformation) EntityManager
				.getObjectByIdentifier(AttributeTypeInformation.class.getName(), attrTypeInfoID
						.toString(), hibernateDAO);

		/*UserDefinedDE originalAttrUserDefinedDE = (UserDefinedDE) attributeTypeInformation
				.getDataElement();
		if (originalAttrUserDefinedDE == null)
		{
			originalAttrUserDefinedDE = new UserDefinedDE();
		}*/
		UserDefinedDE originalAttrUserDefinedDE = null;
		if (attributeTypeInformation.getDataElement() == null)
		{//if DataElement is missing
			originalAttrUserDefinedDE = new UserDefinedDE();
		}
		else
		{ // DataElement is present but PV is missing
			originalAttrUserDefinedDE = (UserDefinedDE) attributeTypeInformation.getDataElement();
		}

		originalAttrUserDefinedDE.getPermissibleValueCollection().addAll(catAttrPVCollection);

		//originalAttrUserDefinedDE.setPermissibleValueCollection(catAttrPVCollection);
		attributeTypeInformation.setDataElement(originalAttrUserDefinedDE);
		saveDataElement(attributeTypeInformation, hibernateDAO);
		return originalAttrUserDefinedDE.getPermissibleValueCollection();
	}

	/**
	 * Save data element.
	 *
	 * @param dynExtBaseDomainObject the dyn ext base domain object
	 * @param hibernateDAO the hibernate dao
	 *
	 * @throws DAOException exception
	 * @throws DynamicExtensionsSystemException exception
	 */
	private void saveDataElement(DynamicExtensionBaseDomainObject dynExtBaseDomainObject,
			HibernateDAO hibernateDAO) throws DynamicExtensionsSystemException, DAOException
	{
		CategoryManagerInterface categoryManagerInterface = CategoryManager.getInstance();
		// save dataElement
		((CategoryManager) categoryManagerInterface).saveDynamicExtensionBaseDomainObject(
				dynExtBaseDomainObject, hibernateDAO);
		hibernateDAO.commit();
	}

	/**
	 * Gets the attr type info id.
	 *
	 * @param abstractAttributeID abstractAttributeID
	 *
	 * @return attrTypeInfoID
	 *
	 * @throws DynamicExtensionsSystemException exception
	 */
	private Long getAttrTypeInfoId(Long abstractAttributeID)
			throws DynamicExtensionsSystemException
	{
		Long attrTypeInfoID;
		if (attrTypeInfoMap.get(Long.valueOf(abstractAttributeID)) == null)
		{
			String attrTypeInfoSql = "select IDENTIFIER from DYEXTN_ATTRIBUTE_TYPE_INFO where "
					+ "PRIMITIVE_ATTRIBUTE_ID=?";
			List<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
			queryDataList.add(new ColumnValueBean(abstractAttributeID));
			List<Long> attrTypeInfoIDList = entityManagerUtil.getResultInList(attrTypeInfoSql,
					queryDataList);
			attrTypeInfoID = attrTypeInfoIDList.get(0);
			attrTypeInfoMap.put(Long.valueOf(abstractAttributeID), attrTypeInfoID);
		}
		else
		{
			attrTypeInfoID = attrTypeInfoMap.get(Long.valueOf(abstractAttributeID));
		}
		return attrTypeInfoID;
	}

	/**
	 * Checks if is attribute or association.
	 *
	 * @param abstractAttributeID abstractAttributeID
	 *
	 * @return isAttribute attribute or association
	 *
	 * @throws DynamicExtensionsSystemException exception
	 */
	private boolean isAttributeOrAssociation(Long abstractAttributeID)
			throws DynamicExtensionsSystemException
	{
		boolean isAttribute = false;
		String attributeSql = "select IDENTIFIER from DYEXTN_PRIMITIVE_ATTRIBUTE where IDENTIFIER=?";
		List<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean(abstractAttributeID));

		List<Long> primitiveAttributeIDs = entityManagerUtil.getResultInList(attributeSql,
				queryDataList);

		if (primitiveAttributeIDs != null && !primitiveAttributeIDs.isEmpty())
		{
			isAttribute = true;
		}
		return isAttribute;
	}

	/**
	 * Gets the data element id.
	 *
	 * @param attrTypeInfoID attribute type info ID
	 *
	 * @return dataElementID dataElement ID
	 *
	 * @throws DynamicExtensionsSystemException exception
	 */
	private Long getDataElementId(Long attrTypeInfoID) throws DynamicExtensionsSystemException
	{
		Long dataElementID;
		if (dataElementIdMap.get(attrTypeInfoID) == null)
		{
			String query = "select identifier from DYEXTN_DATA_ELEMENT where "
					+ "ATTRIBUTE_TYPE_INFO_ID =?";
			List<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
			queryDataList.add(new ColumnValueBean(attrTypeInfoID));
			List<Long> dataElementIDList = entityManagerUtil.getResultInList(query, queryDataList);
			if (dataElementIDList.isEmpty())
			{
				dataElementID = null;
			}
			else
			{
				dataElementID = dataElementIDList.get(0);
			}
			dataElementIdMap.put(attrTypeInfoID, dataElementID);
		}
		else
		{
			dataElementID = dataElementIdMap.get(attrTypeInfoID);
		}
		return dataElementID;
	}

	/**
	 * Gets the entity attribute data element.
	 *
	 * @param dataElementID data element ID
	 * @param hibernateDAO the hibernate dao
	 *
	 * @return originalAttrDataElement entity attribute data element
	 *
	 * @throws DynamicExtensionsSystemException exception
	 */
	private UserDefinedDE getEntityAttributeDataElement(Long dataElementID,
			HibernateDAO hibernateDAO) throws DynamicExtensionsSystemException
	{
		UserDefinedDE originalAttrDataElement;
		originalAttrDataElement = (UserDefinedDE) EntityManager.getObjectByIdentifier(
				UserDefinedDE.class.getName(), dataElementID.toString(), hibernateDAO);
		LOGGER.info("Original Data Element id : " + originalAttrDataElement.getId());
		return originalAttrDataElement;
	}

	/**
	 * Gets the abstract attribute id.
	 *
	 * @param categoryAttributeId category attribute ID
	 *
	 * @return abstractAttributeID abstract attribute ID
	 *
	 * @throws DynamicExtensionsSystemException exception
	 */
	private Long getAbstractAttributeId(Long categoryAttributeId)
			throws DynamicExtensionsSystemException
	{
		String sql = "select ABSTRACT_ATTRIBUTE_ID from DYEXTN_CATEGORY_ATTRIBUTE where IDENTIFIER=?";
		List<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean(categoryAttributeId));

		List<Long> abstractAttributeIDs = entityManagerUtil.getResultInList(sql, queryDataList);

		return abstractAttributeIDs.get(0);
	}

	/**
	 * Gets the original pv references.
	 *
	 * @param catAttrPVCollection category attribute PV collection
	 * @param missingPvCollection missing PV collection
	 * @param originalPvCollection the original pv collection
	 *
	 * @return the original pv references
	 *
	 * @throws DynamicExtensionsSystemException exception
	 */
	private List<PermissibleValueInterface> getOriginalPVReferences(
			Collection<PermissibleValueInterface> catAttrPVCollection,
			Collection<PermissibleValueInterface> originalPvCollection,

			List<PermissibleValueInterface> missingPvCollection)
			throws DynamicExtensionsSystemException
	{
		List<PermissibleValueInterface> newPvCollection = new ArrayList<PermissibleValueInterface>();
		Map<Object, PermissibleValueInterface> pvMap = new HashMap<Object, PermissibleValueInterface>();
		for (PermissibleValueInterface pValueInterface : originalPvCollection)
		{
			pvMap.put(pValueInterface.getValueAsObject(), pValueInterface);
		}

		for (PermissibleValueInterface pvInterface : catAttrPVCollection)
		{
			if (pvInterface.getValueAsObject() != null)
			{
				PermissibleValueInterface pValue = pvMap.get(pvInterface.getValueAsObject());
				// this must not happen at all
				if (pValue == null)
				{
					/*throw new DynamicExtensionsSystemException("The permissible value : "
					+ pvInterface.getValueAsObject() + " is missing in the original set");*/
					errorList.add("Missing in original set pv id -- " + pvInterface.getId() + " : "
							+ pvInterface.getValueAsObject().toString());
					missingPvCollection.add(pvInterface);
				}
				else
				{
					newPvCollection.add(pValue);
					/*Logger.out.info("Category PV id : " + pvInterface.getId() + " ~ Original PV id : "
							+ pValue.getId());*/
				}
			}
		}
		return newPvCollection;
	}

	/**
	 * Gets the attribute for association id.
	 *
	 * @param associationId associationId
	 *
	 * @return metadata id
	 *
	 * @throws DynamicExtensionsSystemException exception
	 */
	private Long getAttributeForAssociationId(Long associationId)
			throws DynamicExtensionsSystemException
	{
		String query = "select meta.IDENTIFIER from DYEXTN_ATTRIBUTE attr, "
				+ "DYEXTN_ASSOCIATION asso,DYEXTN_ABSTRACT_METADATA meta "
				+ "where attr.IDENTIFIER=meta.IDENTIFIER and attr.ENTIY_ID=asso.TARGET_ENTITY_ID "
				+ "and meta.name != 'id' and asso.identifier=? ";

		List<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean(associationId));
		List<Long> idList = entityManagerUtil.getResultInList(query, queryDataList);

		return idList.get(0);
	}

	/**
	 * Gets the all category attributes having p vs.
	 *
	 * @param query the query
	 * @param columnValueBean the column value bean
	 *
	 * @return map
	 *
	 * @throws DynamicExtensionsSystemException exception
	 */
	private static Map<Long, Long> getIdVsIdMap(String query, List<ColumnValueBean> columnValueBean)
			throws DynamicExtensionsSystemException
	{
		JDBCDAO jdbcDao = null;
		ResultSet resultSet = null;

		Map<Long, Long> results = new HashMap<Long, Long>();
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			resultSet = jdbcDao.getResultSet(query, columnValueBean, null);
			while (resultSet.next())
			{
				Long identifier = resultSet.getLong(1);
				results.put(identifier, resultSet.getLong(2));
			}
		}
		catch (Exception e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}
		finally
		{
			try
			{
				jdbcDao.closeStatement(resultSet);
				DynamicExtensionsUtility.closeDAO(jdbcDao);
			}
			catch (DAOException e)
			{
				LOGGER.error(e.getMessage(), e);
			}
		}
		return results;
	}

	/**
	 * Sets the pv reference.
	 *
	 * @param catAttrUserDefinedDE catAttrUserDefinedDE
	 * @param attrPVCollection attrPVCollection
	 *
	 * @return the list< permissible value interface>
	 *
	 * @throws DynamicExtensionsSystemException exception
	 */
	private List<PermissibleValueInterface> setPVReference(UserDefinedDE catAttrUserDefinedDE,
			Collection<PermissibleValueInterface> attrPVCollection)
			throws DynamicExtensionsSystemException
	{
		Collection<PermissibleValueInterface> catAttrPVCollection = catAttrUserDefinedDE
				.getPermissibleValueCollection();
		//List<PermissibleValueInterface> newPvCollection = new ArrayList<PermissibleValueInterface>();
		List<PermissibleValueInterface> missingPvCollection = new ArrayList<PermissibleValueInterface>();
		List<PermissibleValueInterface> newPvCollection = getOriginalPVReferences(
				catAttrPVCollection, attrPVCollection, missingPvCollection);
		catAttrPVCollection.clear();
		catAttrPVCollection.addAll(newPvCollection);
		catAttrPVCollection.addAll(missingPvCollection);
		return missingPvCollection;
	}

	/**
	 * Sets the def value pv reference.
	 *
	 * @param catAttrUserDefinedDE catAttrUserDefinedDE
	 * @param attrPVCollection attrPVCollection
	 *
	 * @return the list< permissible value interface>
	 *
	 * @throws DynamicExtensionsSystemException exception
	 */
	private List<PermissibleValueInterface> setDefValuePVReference(
			UserDefinedDE catAttrUserDefinedDE,
			Collection<PermissibleValueInterface> attrPVCollection)
			throws DynamicExtensionsSystemException
	{
		List<PermissibleValueInterface> missingPvCollection = new ArrayList<PermissibleValueInterface>();
		Collection<PermissibleValueInterface> defaultValuesColl = catAttrUserDefinedDE
				.getDefaultPermissibleValues();
		if (defaultValuesColl != null && !defaultValuesColl.isEmpty())
		{
			//List<PermissibleValueInterface> newPvCollection = new ArrayList<PermissibleValueInterface>();

			List<PermissibleValueInterface> newPvCollection = getOriginalPVReferences(
					defaultValuesColl, attrPVCollection, missingPvCollection);
			defaultValuesColl.clear();
			defaultValuesColl.addAll(newPvCollection);
			defaultValuesColl.addAll(missingPvCollection);
			catAttrUserDefinedDE.getPermissibleValueCollection().addAll(newPvCollection);
			catAttrUserDefinedDE.getPermissibleValueCollection().addAll(missingPvCollection);
			LOGGER.info("Missing pv size : " + missingPvCollection.size());
		}
		return missingPvCollection;
	}
}
