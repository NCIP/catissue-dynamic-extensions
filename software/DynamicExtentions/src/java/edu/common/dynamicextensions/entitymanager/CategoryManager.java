
package edu.common.dynamicextensions.entitymanager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import edu.common.dynamicextensions.DEIntegration.DEIntegration;
import edu.common.dynamicextensions.dao.impl.DynamicExtensionDAO;
import edu.common.dynamicextensions.domain.AbstractAttribute;
import edu.common.dynamicextensions.domain.BaseAbstractAttribute;
import edu.common.dynamicextensions.domain.Category;
import edu.common.dynamicextensions.domain.CategoryAttribute;
import edu.common.dynamicextensions.domain.CategoryEntity;
import edu.common.dynamicextensions.domain.CategoryEntityRecord;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.PathAssociationRelationInterface;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PathInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.CategoryGenerationUtil;
import edu.common.dynamicextensions.util.DataValueMapUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.FormulaCalculator;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.global.ErrorConstants;
import edu.common.dynamicextensions.validation.ValidatorUtil;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.metadata.util.DyExtnObjectCloner;

/**
 * @author rajesh_patil
 * @author mandar_shidhore
 * @author kunal_kamble
 */
public class CategoryManager extends AbstractMetadataManager implements CategoryManagerInterface
{

	/**
	 * Static instance of the CategoryManager.
	 */
	private static CategoryManagerInterface categoryManager = null;

	/**
	 * Static instance of the queryBuilder.
	 */
	private static DynamicExtensionBaseQueryBuilder queryBuilder = null;

	/**
	 * Instance of entity manager utility class
	 */
	private final EntityManagerUtil entityManagerUtil = new EntityManagerUtil();

	/**
	 * Used for cloning objects.
	 */
	private final DyExtnObjectCloner cloner = new DyExtnObjectCloner();

	/**
	 * Empty Constructor.
	 */
	protected CategoryManager()
	{
		super();
	}

	/**
	 * Returns the instance of the Entity Manager.
	 * @return entityManager singleton instance of the Entity Manager.
	 */
	public static synchronized CategoryManagerInterface getInstance()
	{
		if (categoryManager == null)
		{
			categoryManager = new CategoryManager();
			DynamicExtensionsUtility.initialiseApplicationVariables();
			queryBuilder = QueryBuilderFactory.getQueryBuilder();
		}

		return categoryManager;
	}

	/**
	 * It will return the Category with the id as given identifier in the parameter.
	 * @param identifier.
	 * @return category with given identifier.
	 * @throws DynamicExtensionsSystemException
	 */

	public CategoryInterface getCategoryById(final Long identifier)
			throws DynamicExtensionsSystemException
	{
		CategoryInterface category = (CategoryInterface) getObjectByIdentifier(Category.class
				.getName(), identifier.toString());
		return category;
	}

	/**
	 * It will return the CategoryAttribute with the id as given identifier in the parameter.
	 * @param identifier.
	 * @return category with given identifier.
	 * @throws DynamicExtensionsSystemException
	 */

	public CategoryAttributeInterface getCategoryAttributeById(final Long identifier)
			throws DynamicExtensionsSystemException
	{
		CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface) getObjectByIdentifier(
				CategoryAttributeInterface.class.getName(), identifier.toString());
		return categoryAttribute;
	}

	/**
	 * It will return the Category with the name as given name in the parameter.
	 * @param identifier.
	 * @return category with given identifier.
	 * @throws DynamicExtensionsSystemException
	 */

	public CategoryInterface getCategoryByName(final String name)
			throws DynamicExtensionsSystemException
	{
		CategoryInterface category = (CategoryInterface) getObjectByName(CategoryInterface.class
				.getName(), name);
		return category;
	}

	/**
	 * It will return the Category Association with the id as given identifier in the parameter.
	 * @param identifier.
	 * @return category with given identifier.
	 * @throws DynamicExtensionsSystemException
	 */

	public CategoryAssociationInterface getCategoryAssociationById(final Long identifier)
			throws DynamicExtensionsSystemException
	{
		CategoryAssociationInterface categoryAssociation = (CategoryAssociationInterface) getObjectByIdentifier(
				CategoryAssociationInterface.class.getName(), identifier.toString());
		return categoryAssociation;
	}

	/**
	 * LogFatalError.
	 */
	@Override
	protected void logFatalError(final Exception exception,
			final AbstractMetadataInterface abstractMetadata)
	{
		// TODO Auto-generated method stub
	}

	/**
	 * Method to persist a category.
	 * @param categry interface for Category
	 * @throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	 */
	public CategoryInterface persistCategory(final CategoryInterface categry)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		persistDynamicExtensionObject(categry);
		EntityCache.getInstance().addCategoryToCache(categry);
		return categry;
	}

	/**
	 * Method to persist category meta-data.
	 * @param category interface for Category
	 * @throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	 */
	public CategoryInterface persistCategoryMetadata(final CategoryInterface category)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		final CategoryInterface savedCategory = (CategoryInterface) persistDynamicExtensionObjectMetdata(category);
		EntityCache.getInstance().addCategoryToCache(savedCategory);
		return savedCategory;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.entitymanager.AbstractMetadataManager#preProcess(edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface, java.util.List, edu.wustl.common.dao.HibernateDAO, java.util.List)
	 */
	@Override
	protected void preProcess(final DynamicExtensionBaseDomainObjectInterface dyExtBsDmnObj,
			final List<String> revQueries, final List<String> queries)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		final CategoryInterface category = (CategoryInterface) dyExtBsDmnObj;

		getDynamicQueryList(category, revQueries, queries);
	}

	/**
	 * This method gets a list of dynamic tables creation queries.
	 * @param category
	 * @param revQueries
	 * @param queries
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	protected List<String> getDynamicQueryList(final CategoryInterface category,
			final List<String> revQueries, final List<String> queries)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		final List<CategoryEntityInterface> catEntities = new ArrayList<CategoryEntityInterface>();

		// Use HashMap instead of List to ensure entity list contains unique entity only.
		HashMap<String, CategoryEntityInterface> catEntObjects = new HashMap<String, CategoryEntityInterface>();
		DynamicExtensionsUtility.getUnsavedCategoryEntityList(category.getRootCategoryElement(),
				catEntObjects);
		Iterator<String> keySetIter = catEntObjects.keySet().iterator();
		while (keySetIter.hasNext())
		{
			catEntities.add(catEntObjects.get(keySetIter.next()));
		}

		for (final CategoryEntityInterface categoryEntity : catEntities)
		{
			final List<String> createQueries = queryBuilder.getCreateCategoryQueryList(
					categoryEntity, revQueries);
			if ((createQueries != null) && !createQueries.isEmpty())
			{
				queries.addAll(createQueries);
			}
		}

		for (final CategoryEntityInterface categoryEntity : catEntities)
		{
			final List<String> createQueries = queryBuilder.getUpdateCategoryEntityQueryList(
					categoryEntity, revQueries);
			if ((createQueries != null) && !createQueries.isEmpty())
			{
				queries.addAll(createQueries);
			}
		}

		// Use HashMap instead of List to ensure entity list contains unique entity only.
		final List<CategoryEntityInterface> savedCatEntities = new ArrayList<CategoryEntityInterface>();
		catEntObjects = new HashMap<String, CategoryEntityInterface>();
		DynamicExtensionsUtility.getSavedCategoryEntityList(category.getRootCategoryElement(),
				catEntObjects);
		keySetIter = catEntObjects.keySet().iterator();
		while (keySetIter.hasNext())
		{
			savedCatEntities.add(catEntObjects.get(keySetIter.next()));
		}

		HibernateDAO hibernateDAO = null;
		try
		{
			hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();

			for (final CategoryEntityInterface categoryEntity : savedCatEntities)
			{
				final CategoryEntity dbaseCopy = (CategoryEntity) hibernateDAO.retrieveById(
						CategoryEntity.class.getCanonicalName(), categoryEntity.getId());

				// Only for category entity for which table is getting created.
				if (dbaseCopy.isCreateTable())
				{
					final List<String> updateQueries = queryBuilder.getUpdateEntityQueryList(
							(CategoryEntity) categoryEntity, dbaseCopy, revQueries);

					if ((updateQueries != null) && !updateQueries.isEmpty())
					{
						queries.addAll(updateQueries);
					}
				}
			}
		}
		catch (final DAOException exception)
		{
			throw new DynamicExtensionsSystemException("Not able to retrieve Object.", exception);
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(hibernateDAO);
		}

		return queries;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.entitymanager.AbstractMetadataManager#postProcess(java.util.List, java.util.List, java.util.Stack)
	 */
	@Override
	protected void postProcess(final List<String> queries, final List<String> revQueries,
			final Stack<String> rlbkQryStack) throws DynamicExtensionsSystemException
	{
		queryBuilder.executeQueries(queries, revQueries, rlbkQryStack);
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.entitymanager.CategoryManagerInterface#insertData(edu.common.dynamicextensions.domaininterface.CategoryInterface, java.util.Map)
	 */
	public Long insertData(final CategoryInterface category,
			final Map<BaseAbstractAttributeInterface, Object> dataValue, final Long... userId)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		final List<Map<BaseAbstractAttributeInterface, Object>> dataValMaps = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
		dataValMaps.add(dataValue);
		final Long identifier = (((userId != null) && (userId.length > 0)) ? userId[0] : null);
		final List<Long> recordIds = insertData(category, dataValMaps, identifier);

		return recordIds.get(0);
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.entitymanager.CategoryManagerInterface#insertData(edu.common.dynamicextensions.domaininterface.CategoryInterface, java.util.List)
	 */
	public List<Long> insertData(final CategoryInterface category,
			final List<Map<BaseAbstractAttributeInterface, Object>> dataValMaps,
			final Long... userId) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException
	{
		final List<Long> recordIds = new ArrayList<Long>();

		JDBCDAO jdbcDao = null;
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			final Long identifier = (((userId != null) && (userId.length > 0)) ? userId[0] : null);

			for (final Map<BaseAbstractAttributeInterface, Object> dataValMap : dataValMaps)
			{
				final Long recordId = insertDataForHierarchy(category.getRootCategoryElement(),
						dataValMap, jdbcDao, identifier);
				recordIds.add(recordId);
			}
			jdbcDao.commit();
		}
		catch (final DAOException e)
		{
			DynamicExtensionsUtility.rollBackDAO(jdbcDao);
			throw new DynamicExtensionsSystemException("Error while inserting data", e);
		}
		catch (final SQLException e)
		{
			DynamicExtensionsUtility.rollBackDAO(jdbcDao);
			throw new DynamicExtensionsSystemException("Error while inserting data", e);
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(jdbcDao);
		}

		return recordIds;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.entitymanager.CategoryManagerInterface#insertData(java.lang.Long, java.util.Map)
	 */
	public Long insertData(final Long categoryId, final Map<Long, Object> dataValue)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException,
			ParseException
	{
		Long recordIdentifier = null;
		CategoryInterface category = categoryManager.getCategoryById(categoryId);
		ContainerInterface containerInterface = (ContainerInterface) category
				.getRootCategoryElement().getContainerCollection().toArray()[0];

		Map<Long, BaseAbstractAttributeInterface> idToAttributeMap = DataValueMapUtility
				.retriveIdToAttributeMap(category);
		Map<BaseAbstractAttributeInterface, Object> attributeToValueMap = DataValueMapUtility
				.getAttributeToValueMap(dataValue, idToAttributeMap);
		List<String> errorList = ValidatorUtil.validateEntity(attributeToValueMap,
				new ArrayList<String>(), containerInterface, true);
		if (errorList.isEmpty())
		{
			Long recordId = insertData(category, attributeToValueMap);
			recordIdentifier = categoryManager.getEntityRecordIdByRootCategoryEntityRecordId(
					recordId, category.getRootCategoryElement().getTableProperties().getName());
		}
		else
		{
			StringBuffer buffer = new StringBuffer();
			int count = 1;
			Iterator<String> errorListIterator = errorList.iterator();
			while (errorListIterator.hasNext())
			{
				buffer.append(count).append(')').append(errorListIterator.next());
				count++;
			}
			throw new DynamicExtensionsApplicationException(buffer.toString());
		}
		return recordIdentifier;
	}

	/**
	 * This method inserts the data for hierarchy of category entities one by one.
	 * @param catEntity
	 * @param dataValue
	 * @param jdbcDao
	 * @param userId
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws SQLException
	 */
	private Long insertDataForHierarchy(final CategoryEntityInterface catEntity,
			final Map<BaseAbstractAttributeInterface, Object> dataValue, final JDBCDAO jdbcDao,
			final Long... userId) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, SQLException
	{
		final List<CategoryEntityInterface> catEntities = getParentEntityList(catEntity);
		final Map<CategoryEntityInterface, Map<BaseAbstractAttributeInterface, Object>> entityValMap = initialiseEntityValueMap(dataValue);
		Long parentRecId = null;
		Long parntCatRecId = null;
		final Long identifier = (((userId != null) && (userId.length > 0)) ? userId[0] : null);

		for (final CategoryEntityInterface categoryEntity : catEntities)
		{
			final Map<BaseAbstractAttributeInterface, Object> valueMap = entityValMap
					.get(categoryEntity);

			// If parent category entity table not created, then add its attribute map to value map.
			CategoryEntity parntCatEntity = (CategoryEntity) categoryEntity
					.getParentCategoryEntity();
			while ((parntCatEntity != null) && !parntCatEntity.isCreateTable())
			{
				final Map<BaseAbstractAttributeInterface, Object> innerValMap = entityValMap
						.get(parntCatEntity);

				if (innerValMap != null)
				{
					valueMap.putAll(innerValMap);
				}

				parntCatEntity = (CategoryEntity) parntCatEntity.getParentCategoryEntity();
			}

			parentRecId = insertDataForSingleCategoryEntity(categoryEntity, valueMap, dataValue,
					jdbcDao, parentRecId, identifier);
			parntCatRecId = getRootCategoryRecordId(categoryEntity, parentRecId, jdbcDao);
		}

		return parntCatRecId;
	}

	/**
	 * @param catEntity
	 * @param parentRecId
	 * @return
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 */
	private Long getRootCategoryRecordId(final CategoryEntityInterface catEntity,
			final Long parentRecId, final JDBCDAO jdbcDao) throws SQLException,
			DynamicExtensionsSystemException
	{
		final CategoryInterface category = catEntity.getCategory();
		final CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();

		final StringBuffer query = new StringBuffer(SELECT_KEYWORD);
		query.append(WHITESPACE);
		query.append(IDENTIFIER);
		query.append(WHITESPACE);
		query.append(FROM_KEYWORD);
		query.append(WHITESPACE);
		query.append(rootCatEntity.getTableProperties().getName());
		query.append(WHITESPACE);
		query.append(WHERE_KEYWORD);
		query.append(WHITESPACE);
		query.append(RECORD_ID);
		query.append(EQUAL);
		query.append(QUESTION_MARK);
		final LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean(RECORD_ID, parentRecId));
		Long rootCERecId = null;

		final List<Long> results = getResultIDList(query.toString(), IDENTIFIER, jdbcDao,
				queryDataList);
		if (!results.isEmpty())
		{
			rootCERecId = results.get(0);
		}

		return rootCERecId;
	}

	/**
	 * This method inserts data for a single category entity.
	 * @param catEntity
	 * @param dataValue
	 * @param jdbcDao
	 * @param userId
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws SQLException
	 */
	private Long insertDataForSingleCategoryEntity(CategoryEntityInterface catEntity,
			final Map<BaseAbstractAttributeInterface, Object> dataValue,
			final Map<BaseAbstractAttributeInterface, Object> fullDataValue, final JDBCDAO jdbcDao,
			final Long parentRecId, final Long... userId) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, SQLException
	{

		final Map<String, Long> keyMap = new HashMap<String, Long>();
		final Map<String, Long> fullKeyMap = new HashMap<String, Long>();
		final Map<String, List<Long>> records = new HashMap<String, List<Long>>();
		Long identifier = (((userId != null) && (userId.length > 0)) ? userId[0] : null);

		HibernateDAO hibernateDao = null;
		List<FileQueryBean> fileAttrQueryList = new ArrayList<FileQueryBean>();
		try
		{
			hibernateDao = DynamicExtensionsUtility.getHibernateDAO();
			if (catEntity == null)
			{
				throw new DynamicExtensionsSystemException("Input to insert data is null");
			}
			final CategoryInterface category = catEntity.getCategory();
			final CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();

			// If root category entity does not have any attribute, or all its category attributes
			// are related attributes, then explicitly insert identifier into entity table and
			// insert this identifier as record identifier in category entity table.
			if ((rootCatEntity.getCategoryAttributeCollection().size() == 0)
					|| isAllRelatedCategoryAttributesCollection(rootCatEntity))
			{
				// Insert blank record in all parent entity tables of root category entity so use insertDataForHeirarchy and add all keys to keymap, recordmap, fullkeymap.
				final Map<AbstractAttributeInterface, Object> attributes = new HashMap<AbstractAttributeInterface, Object>();
				final EntityManagerInterface entityManager = EntityManager.getInstance();
				EntityInterface rootEntity = catEntity.getEntity();
				//Long entityId = entityManager.insertDataForHeirarchy(catEntity.getEntity(), attributes,
				//jdbcDao, identifier);
				final Long entityId = entityManager.insertData(rootEntity, attributes,
						hibernateDao, fileAttrQueryList, identifier);

				final Long catEntId = entityManagerUtil.getNextIdentifier(rootCatEntity
						.getTableProperties().getName());

				putRecordIdsInMap(catEntity, entityId, fullKeyMap, keyMap, records);
				// Query to insert record into category entity table.
				final String insertQuery = INSERT_INTO_KEYWORD
						+ rootCatEntity.getTableProperties().getName()
						+ " (IDENTIFIER, ACTIVITY_STATUS, " + RECORD_ID + ") VALUES (?,?,?)";
				//creating the column value beans according to DAO1.1.5
				final LinkedList<ColumnValueBean> colValBeanList = createColumnValueBeanListForDataEntry(
						catEntId, entityId);
				DynamicExtensionsUtility.executeUpdateQuery(insertQuery, identifier, jdbcDao,
						colValBeanList);
				logDebug("insertData", "categoryEntityTableInsertQuery is : " + insertQuery);
			}
			String rootCatEntName = DynamicExtensionsUtility.getCategoryEntityName(catEntity
					.getName());
			final boolean areMultplRecrds = false;
			final boolean isNoCatAttrPrsnt = false;

			final String entityFKColName = null;
			final String catEntFKColName = null;
			final Long srcCatEntityId = null;
			final Long srcEntityId = null;

			// Separate out category attribute and category association. The map coming from UI can contain both in any order,
			// but we require to insert record for category attribute first for root category entity.
			final Map<CategoryAttributeInterface, Object> catAttributes = new HashMap<CategoryAttributeInterface, Object>();
			final Map<CategoryAssociationInterface, Object> catAssociations = new HashMap<CategoryAssociationInterface, Object>();

			for (Entry<BaseAbstractAttributeInterface, Object> dataValueEntry : dataValue
					.entrySet())
			{
				final BaseAbstractAttributeInterface obj = dataValueEntry.getKey();
				if (obj instanceof CategoryAttributeInterface)
				{
					catAttributes.put((CategoryAttributeInterface) obj, dataValueEntry.getValue());
				}
				else
				{
					catAssociations.put((CategoryAssociationInterface) obj, dataValueEntry
							.getValue());
				}
			}

			// Insert records for category attributes.
			insertRecordsForCategoryEntityTree(entityFKColName, catEntFKColName, srcCatEntityId,
					srcEntityId, catEntity, catAttributes, keyMap, fullKeyMap, records,
					areMultplRecrds, isNoCatAttrPrsnt, jdbcDao, hibernateDao, false, null,
					identifier, fileAttrQueryList);

			// Insert records for category associations.
			insertRecordsForCategoryEntityTree(entityFKColName, catEntFKColName, srcCatEntityId,
					srcEntityId, catEntity, catAssociations, keyMap, fullKeyMap, records,
					areMultplRecrds, isNoCatAttrPrsnt, jdbcDao, hibernateDao, false, null,
					identifier, fileAttrQueryList);

			final Long rootCERecId = getRootCategoryEntityRecordId(category
					.getRootCategoryElement(), fullKeyMap.get(rootCatEntName), jdbcDao);

			insertRecordsForRelatedAttributes(fullDataValue, rootCERecId, category
					.getRootCategoryElement(), records, jdbcDao, hibernateDao, identifier);
			hibernateDao.commit();
			executeFileRecordQueryList(fileAttrQueryList);
			if (parentRecId == null)
			{
				identifier = keyMap.get(rootCatEntName);
			}
			else
			{
				identifier = parentRecId;
			}
		}
		catch (final DAOException e)
		{
			throw new DynamicExtensionsApplicationException(
					"Exception encountered while inserting records!", e);
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(hibernateDao);
		}

		return identifier;
	}

	/**
	 * This method will add the catEntity & all its parent entitys reocrd identifier in the
	 * fullKeyMap,keyMap,records.
	 * @param catEntity Category Entity whose entry to be added in maps.
	 * @param entityId entity's record id
	 * @param fullKeyMap map of entity vs record id
	 * @param keyMap map of entity vs record id
	 * @param records map of entity vs List of record id
	 */
	private void putRecordIdsInMap(CategoryEntityInterface catEntity, Long entityId,
			final Map<String, Long> fullKeyMap, final Map<String, Long> keyMap,
			Map<String, List<Long>> records)
	{

		EntityInterface rootEntity = catEntity.getEntity();
		long instanceId = CategoryGenerationUtil.getInstanceIdOfCategoryEntity(catEntity);
		while (rootEntity != null)
		{
			String entityName = rootEntity.getName() + "[" + instanceId + "]";
			keyMap.put(entityName, entityId);
			fullKeyMap.put(entityName, entityId);
			List<Long> recordIds = records.get(entityName);
			if (recordIds == null)
			{
				recordIds = new ArrayList<Long>();
			}
			recordIds.add(entityId);
			records.put(entityName, recordIds);
			rootEntity = rootEntity.getParentEntity();
		}
	}

	/**
	 * This method checks whether category attributes in a category entity's
	 * parent are related attributes or not. It true, it inserts records for the same first.
	 * @param rootCatEntity
	 * @param records
	 * @param jdbcDAO
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void insertAllParentRelatedCategoryAttributesCollection(
			final Map<BaseAbstractAttributeInterface, Object> valueMap,
			final CategoryEntityInterface rootCatEntity, final Map<String, List<Long>> records,
			final HibernateDAO hibernateDao) throws SQLException, DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		try
		{
			final Map<BaseAbstractAttribute, Object> attrVsValues = new HashMap<BaseAbstractAttribute, Object>();
			// Get all attributes including parent's attributes.
			final Collection<CategoryAttributeInterface> catAttributes = rootCatEntity
					.getAllCategoryAttributes();
			for (final CategoryAttributeInterface catAttribute : catAttributes)
			{
				if (((catAttribute.getIsRelatedAttribute() != null) && catAttribute
						.getIsRelatedAttribute())
						&& (!catAttribute.getAbstractAttribute().getEntity().equals(
								rootCatEntity.getEntity())))
				{
					// In case of related attributes, check if it is parent's attribute.
					final AttributeInterface attribute = catAttribute.getAbstractAttribute()
							.getEntity().getAttributeByName(
									catAttribute.getAbstractAttribute().getName());

					// Fetch column names and column values for related category attributes.
					populateColumnNamesAndValues(valueMap, attribute, catAttribute, attrVsValues);

					//String entTableName = attribute.getEntity().getTableProperties().getName();
					final List<Long> recordIds = records.get(DynamicExtensionsUtility
							.getCategoryEntityName(rootCatEntity.getName()));
					if ((recordIds != null))
					{
						for (final Long identifer : recordIds)
						{
							String packageName = null;
							packageName = getPackageName(rootCatEntity.getEntity(), packageName);

							final String entityClassName = packageName + "."
									+ rootCatEntity.getEntity().getName();
							final Object object = hibernateDao.retrieveById(entityClassName,
									identifer);
							Object clonedObject = cloner.clone(object);
							for (Entry<BaseAbstractAttribute, Object> attrValueEntry : attrVsValues
									.entrySet())
							{
								setRelatedAttributeValues(entityClassName, attrValueEntry.getKey(),
										attrValueEntry.getValue(), object);
							}
							hibernateDao.update(object, clonedObject);
						}
					}
				}
			}
		}
		catch (final Exception e)
		{
			throw new DynamicExtensionsApplicationException(
					ErrorConstants.ERROR_ENCNTR_INSERTING_REC, e);
		}
	}

	private void setRelatedAttributeValues(final String entityClassName,
			final BaseAbstractAttribute attr, final Object value, final Object object)
			throws DynamicExtensionsApplicationException
	{
		final String dataType = ((AttributeMetadataInterface) attr).getAttributeTypeInformation()
				.getDataType();
		try
		{
			setObjectProperty((AbstractAttribute) attr, dataType, Class.forName(entityClassName),
					value.toString(), object);
		}
		catch (final Exception e)
		{
			throw new DynamicExtensionsApplicationException(
					ErrorConstants.ERROR_ENCNTR_INSERTING_REC, e);
		}
	}

	/**
	 * This method checks if all category attributes in a category entity are related attributes.
	 * @param catEntity
	 * @return true if all category attributes are related attributes, false otherwise
	 */
	private boolean isAllRelatedCategoryAttributesCollection(final CategoryEntityInterface catEntity)
	{
		final Collection<CategoryAttributeInterface> catAttributes = catEntity
				.getAllCategoryAttributes();
		boolean flag = true;
		for (final CategoryAttributeInterface catAttribute : catAttributes)
		{
			if ((catAttribute.getIsRelatedAttribute() == null)
					|| !catAttribute.getIsRelatedAttribute())
			{
				flag = false;
				break;
			}
		}

		return flag;
	}

	/**
	 * This method checks if all category attributes in a category entity are related attributes.
	 * @param catEntity
	 * @return true if all category attributes are related attributes, false otherwise
	 */
	private boolean isAllRelatedCategoryAttributesCollection(
			final Map<CategoryAttributeInterface, Object> catAttributes)
	{
		boolean flag = true;
		for (final CategoryAttributeInterface catAttribute : catAttributes.keySet())
		{
			if ((catAttribute.getIsRelatedAttribute() == null)
					|| !catAttribute.getIsRelatedAttribute())
			{
				flag = false;
				break;
			}
		}

		return flag;
	}

	/**
	 * This method checks whether all attributes are invisible type related attributes
	 * or visible type related attributes or all are normal attribute.
	 * @param catEntity
	 * @return
	 */
	private boolean isAllRelatedInvisibleCategoryAttributesCollection(
			final CategoryEntityInterface catEntity)
	{
		boolean returnValue = true;
		final Collection<CategoryAttributeInterface> catAttributes = catEntity
				.getAllCategoryAttributes();
		if ((catAttributes == null) || catAttributes.isEmpty())
		{
			returnValue = false;
		}
		else
		{
			for (final CategoryAttributeInterface catAttribute : catAttributes)
			{
				if ((catAttribute.getIsRelatedAttribute() == null)
						|| !catAttribute.getIsRelatedAttribute())
				{
					returnValue = false;
				}
				else if (catAttribute.getIsRelatedAttribute()
						&& ((catAttribute.getIsVisible() != null) && catAttribute.getIsVisible()))
				{
					returnValue = false;
				}
			}
		}
		return returnValue;
	}

	/**
	 * Insert records for related attributes in each category entity.
	 * @param rootRecordId
	 * @param rootCatEntity
	 * @param records
	 * @param jdbcDAO
	 * @param identifier
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void insertRecordsForRelatedAttributes(
			final Map<BaseAbstractAttributeInterface, Object> valueMap, final Long rootRecordId,
			final CategoryEntityInterface rootCatEntity, final Map<String, List<Long>> records,
			final JDBCDAO jdbcDao, final HibernateDAO hibernateDao, final Long identifier)
			throws SQLException, DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		final CategoryInterface category = rootCatEntity.getCategory();

		// Get a collection of category entities having a collection of related attributes.
		final Collection<CategoryEntityInterface> catEntWithRA = category
				.getRelatedAttributeCategoryEntityCollection();

		// Call this method for root category entity's parent's related attributes.
		insertAllParentRelatedCategoryAttributesCollection(valueMap, rootCatEntity, records,
				hibernateDao);

		for (final CategoryEntityInterface categoryEntity : catEntWithRA)
		{
			final Map<BaseAbstractAttribute, Object> attrVsValues = new HashMap<BaseAbstractAttribute, Object>();
			// Fetch column names and column values for related category attributes.
			getColumnNamesAndValuesForRelatedCategoryAttributes(valueMap, categoryEntity, records,
					hibernateDao, attrVsValues);

			final CategoryAssociationInterface catAssociation = getCategoryAssociationWithTreeParentCategoryEntity(
					categoryEntity.getTreeParentCategoryEntity(), categoryEntity);

			if ((catAssociation == null) && categoryEntity.equals(rootCatEntity))
			{
				// insert records for related category attributes of root category entity.
				insertRelatedAttributeRecordsForRootCategoryEntity(categoryEntity, records,
						hibernateDao, attrVsValues);
			}
			else if (catAssociation != null)
			{
				insertRelatedAttributeRecordsForCategoryEntity(categoryEntity, catAssociation,
						rootRecordId, records, jdbcDao, identifier, attrVsValues, hibernateDao);
			}
		}
	}

	/**
	 * This method clubs column names and column values for related category attributes.
	 * @param valueMap data value map
	 * @param catEntity category entity
	 * @param records records Map
	 * @param hibernateDao dao
	 * @param attrVsValues attribute values map
	 * @throws DynamicExtensionsSystemException exception
	 * @throws SQLException exception
	 * @throws DynamicExtensionsApplicationException exception.
	 */
	private void getColumnNamesAndValuesForRelatedCategoryAttributes(
			final Map<BaseAbstractAttributeInterface, Object> valueMap,
			final CategoryEntityInterface catEntity, final Map<String, List<Long>> records,
			final HibernateDAO hibernateDao, final Map<BaseAbstractAttribute, Object> attrVsValues)
			throws DynamicExtensionsSystemException, SQLException,
			DynamicExtensionsApplicationException
	{
		final Collection<CategoryAttributeInterface> catAttributes = catEntity
				.getCategoryAttributeCollection();
		for (final CategoryAttributeInterface catAttribute : catAttributes)
		{
			if ((catAttribute.getIsRelatedAttribute() != null)
					&& catAttribute.getIsRelatedAttribute())
			{
				final AttributeInterface attribute = catAttribute.getAbstractAttribute()
						.getEntity().getAttributeByName(
								catAttribute.getAbstractAttribute().getName());

				if (catAttribute.getAbstractAttribute() instanceof AssociationInterface)
				{
					populateAndInsertRecordForRelatedMultiSelectCategoryAttribute(catAttribute,
							attribute, records, hibernateDao);
				}
				else
				{
					populateColumnNamesAndValues(valueMap, attribute, catAttribute, attrVsValues);
				}
			}
		}
	}

	/**
	 * This method populates and inserts records for related multiselect category attribute.
	 * @param catAttribute
	 * @param attribute
	 * @param records
	 * @param hibernateDao
	 * @param jdbcDAO
	 * @throws DynamicExtensionsSystemException
	 * @throws SQLException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void populateAndInsertRecordForRelatedMultiSelectCategoryAttribute(
			final CategoryAttributeInterface catAttribute, final AttributeInterface attribute,
			final Map<String, List<Long>> records, final HibernateDAO hibernateDao)
			throws DynamicExtensionsSystemException, SQLException,
			DynamicExtensionsApplicationException
	{
		try
		{
			final AssociationInterface association = (AssociationInterface) catAttribute
					.getAbstractAttribute();
			final EntityInterface targetEntity = association.getTargetEntity();

			final List<Long> ids = records.get(DynamicExtensionsUtility
					.getCategoryEntityName(catAttribute.getCategoryEntity().getName()));

			if (ids != null)
			{
				for (final Long id : ids)
				{
					String packageName = null;
					packageName = getPackageName(association.getEntity(), packageName);

					final String sourceObjectClassName = packageName + "."
							+ association.getEntity().getName();
					final String targetObjectClassName = packageName + "."
							+ association.getTargetEntity().getName();

					final Object sourceObject = hibernateDao
							.retrieveById(sourceObjectClassName, id);
					Object clonedSourceObject = cloner.clone(sourceObject);
					Object targetObject = null;

					// Create a new instance.
					targetObject = createObjectForClass(packageName + "." + targetEntity.getName());

					final Object defaultvalue = catAttribute.getDefaultValue();
					setRelatedAttributeValues(targetObjectClassName,
							(BaseAbstractAttribute) attribute, defaultvalue, targetObject);

					hibernateDao.insert(targetObject);

					addTargetObject(sourceObject, targetObject, targetObjectClassName, association);
					addSourceObject(sourceObject, targetObject, sourceObjectClassName, association);
					hibernateDao.update(sourceObject, clonedSourceObject);

					hibernateDao.update(targetObject);
				}
			}
		}
		catch (final Exception e)
		{
			throw new DynamicExtensionsApplicationException(
					ErrorConstants.ERROR_ENCNTR_INSERTING_REC, e);
		}
	}

	/**
	 * This method will update the attrVsValues with the related attribtue values.
	 * @param valueMap value map
	 * @param attribute attribute
	 * @param catAttribute category attribute
	 * @param attrVsValues attribute value map in which values are updated.
	 * @throws DynamicExtensionsSystemException exception.
	 * @throws DynamicExtensionsApplicationException exception.
	 */
	private void populateColumnNamesAndValues(
			final Map<BaseAbstractAttributeInterface, Object> valueMap,
			final AttributeInterface attribute, final CategoryAttributeInterface catAttribute,
			final Map<BaseAbstractAttribute, Object> attrVsValues)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		String defaultValue = null;
		final AttributeTypeInformationInterface attrTypeInfo = attribute
				.getAttributeTypeInformation();

		// If attribute type information is date type whose default value is not getting set
		// as if it is read only then skip it, do not set in query.
		if (!((attrTypeInfo instanceof DateAttributeTypeInformation) && (catAttribute
				.getDefaultValue() == null)))
		{
			defaultValue = catAttribute.getDefaultValue();
			//Setting the default value to empty string since null value not updated in db for numeric and string values.
			if (defaultValue == null)
			{
				defaultValue = "";
			}
			if ((catAttribute.getIsCalculated() != null) && catAttribute.getIsCalculated())
			{
				final FormulaCalculator formulaCalculator = new FormulaCalculator();
				final String formulaResultValue = formulaCalculator.evaluateFormula(valueMap,
						catAttribute, catAttribute.getCategoryEntity().getCategory(), 1);
				if (formulaResultValue != null)
				{
					defaultValue = formulaResultValue;
				}
			}

			// Replace any single and double quotes value with a proper escape character.
			defaultValue = DynamicExtensionsUtility.getEscapedStringValue(defaultValue);

			final String dbType = DAOConfigFactory.getInstance().getDAOFactory(
					DynamicExtensionDAO.getInstance().getAppName()).getDataBaseType();
			if (dbType.equals(DEConstants.ORACLE_DATABASE)
					&& (attrTypeInfo instanceof DateAttributeTypeInformation))
			{

				defaultValue = DynamicExtensionsUtility.getDefaultDateForRelatedCategoryAttribute(
						attribute, catAttribute.getDefaultValue());
			}
		}
		attrVsValues.put((BaseAbstractAttribute) attribute, defaultValue);
	}

	/**
	 * This method returns a category association between root category entity
	 * and category entity passed to this method.
	 * @param treeParent
	 * @param catEntity
	 * @return category association between root category entity and category entity passed
	 */
	private CategoryAssociationInterface getCategoryAssociationWithTreeParentCategoryEntity(
			final CategoryEntityInterface treeParent, final CategoryEntityInterface catEntity)
	{
		CategoryAssociationInterface categoryAssociation = null;
		// for root category entity, its tree parent category entity will be null.
		if (treeParent != null)
		{
			final Collection<CategoryAssociationInterface> catAssociations = treeParent
					.getCategoryAssociationCollection();

			for (final CategoryAssociationInterface catAssociation : catAssociations)
			{
				if ((catAssociation.getTargetCategoryEntity() != null)
						&& catAssociation.getTargetCategoryEntity().equals(catEntity))
				{
					categoryAssociation = catAssociation;
					break;
				}
			}
		}
		return categoryAssociation;
	}

	/**
	 * This method inserts records for related category attributes of root category entity.
	 * @param rootCatEntity
	 * @param colNamesValues
	 * @param records
	 * @param hibernateDao
	 * @param attrVsValues
	 * @param jdbcDAO
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void insertRelatedAttributeRecordsForRootCategoryEntity(
			final CategoryEntityInterface rootCatEntity, final Map<String, List<Long>> records,
			final HibernateDAO hibernateDao, final Map<BaseAbstractAttribute, Object> attrVsValues)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		try
		{
			final List<Long> recordIds = records.get(DynamicExtensionsUtility
					.getCategoryEntityName(rootCatEntity.getName()));
			if (recordIds != null)
			{
				for (final Long identifer : recordIds)
				{
					String packageName = null;
					packageName = getPackageName(rootCatEntity.getEntity(), packageName);
					// Update entity query.
					final String entityClassName = packageName + "."
							+ rootCatEntity.getEntity().getName();

					final Object object = hibernateDao.retrieveById(entityClassName, identifer);
					Object colnedObject = cloner.clone(object);
					for (Entry<BaseAbstractAttribute, Object> attrValueEntry : attrVsValues
							.entrySet())
					{
						setRelatedAttributeValues(entityClassName, attrValueEntry.getKey(),
								attrValueEntry.getValue(), object);
					}
					hibernateDao.update(object, colnedObject);
				}
			}
		}
		catch (final Exception e)
		{
			throw new DynamicExtensionsApplicationException(
					ErrorConstants.ERROR_ENCNTR_INSERTING_REC, e);
		}
	}

	/**
	 * This method inserts records for related category attributes of category entities
	 * other than root category entity.
	 * @param catEntity
	 * @param catAssociation
	 * @param columnNames
	 * @param columnValues
	 * @param colNamesValues
	 * @param rootRecId
	 * @param records
	 * @param jdbcDAO
	 * @param userId
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 */
	private void insertRelatedAttributeRecordsForCategoryEntity(
			final CategoryEntityInterface catEntity,
			final CategoryAssociationInterface catAssociation, final Long rootRecId,
			final Map<String, List<Long>> records, final JDBCDAO jdbcDao, final Long userId,
			final Map<BaseAbstractAttribute, Object> attrVsValues, final HibernateDAO hibernateDao)
			throws SQLException, DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		try
		{
			final String catEntTblName = catEntity.getTableProperties().getName();
			final String catEntFK = catAssociation.getConstraintProperties()
					.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties()
					.getName();

			List<Long> srcEntityId = records.get(DynamicExtensionsUtility
					.getCategoryEntityName(catAssociation.getCategoryEntity().getName()));
			final String catEntityName = DynamicExtensionsUtility.getCategoryEntityName(catEntity
					.getName());
			if (records.get(catEntityName) != null)
			{
				final List<Long> recordIds = records.get(catEntityName);
				for (final Long id : recordIds)
				{
					String packageName = null;
					packageName = getPackageName(catEntity.getEntity(), packageName);

					final String entityClassName = packageName + "."
							+ catEntity.getEntity().getName();

					final Object object = hibernateDao.retrieveById(entityClassName, id);
					Object clonedObject = cloner.clone(object);

					for (Entry<BaseAbstractAttribute, Object> attrValueEntry : attrVsValues
							.entrySet())
					{
						setRelatedAttributeValues(entityClassName, attrValueEntry.getKey(),
								attrValueEntry.getValue(), object);
					}

					hibernateDao.update(object, clonedObject);

					final String selectQuery = SELECT_KEYWORD + IDENTIFIER + FROM_KEYWORD
							+ catEntTblName + WHERE_KEYWORD + RECORD_ID + EQUAL + QUESTION_MARK;
					final LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
					queryDataList.add(new ColumnValueBean(RECORD_ID, id));
					final List<Long> resultIds = getResultIDList(selectQuery, IDENTIFIER, jdbcDao,
							queryDataList);
					if (resultIds.isEmpty())
					{
						final Long catEntId = entityManagerUtil.getNextIdentifier(catEntTblName);

						// Insert query for category entity table.
						final String insertQuery = INSERT_INTO_KEYWORD + catEntTblName
								+ " (IDENTIFIER, ACTIVITY_STATUS, " + RECORD_ID + ", " + catEntFK
								+ ") VALUES (?,?,?,?)";

						final LinkedList<ColumnValueBean> colValBeanList = createColumnValueBeanListForDataEntry(
								catEntId, id);
						final ColumnValueBean bean4 = new ColumnValueBean(catEntFK, rootRecId);
						colValBeanList.add(bean4);
						DynamicExtensionsUtility.executeUpdateQuery(insertQuery, userId, jdbcDao,
								colValBeanList);
					}
				}

			}
			// If all attributes are invisible type related attributes, then only insert explicitly,
			// in all other cases the category entity name must available in map of records.
			else if ((records.get(catEntityName) == null)
					&& isAllRelatedInvisibleCategoryAttributesCollection(catEntity))
			{
				final PathInterface path = catEntity.getPath();

				final Collection<PathAssociationRelationInterface> pathAssoRel = path
						.getSortedPathAssociationRelationCollection();
				for (final PathAssociationRelationInterface par : pathAssoRel)
				{
					final AssociationInterface association = par.getAssociation();

					if (association.getTargetEntity().getId() != null
							&& (association.getTargetEntity().getId()).equals(catEntity.getEntity()
									.getId()))
					{
						for (final Long sourceId : srcEntityId)
						{
							String packageName = null;
							packageName = getPackageName(catEntity.getEntity(), packageName);

							final String sourceObjectClassName = packageName + "."
									+ association.getEntity().getName();
							final String targetObjectClassName = packageName + "."
									+ catEntity.getEntity().getName();
							final Object sourceObject = hibernateDao.retrieveById(
									sourceObjectClassName, sourceId);
							Object clonedSourceObject = cloner.clone(sourceObject);
							// Create a new instance.
							Object targetObject = createObjectForClass(targetObjectClassName);

							for (Entry<BaseAbstractAttribute, Object> attrValueEntry : attrVsValues
									.entrySet())
							{
								setRelatedAttributeValues(targetObjectClassName, attrValueEntry
										.getKey(), attrValueEntry.getValue(), targetObject);
							}

							// Put the target object in a collection and
							// save it as a collection of the source object.

							addTargetObject(sourceObject, targetObject, targetObjectClassName,
									association);

							hibernateDao.insert(targetObject);
							hibernateDao.update(sourceObject, clonedSourceObject);
							final Long entityId = getObjectId(targetObject);
							final Long catEntId = entityManagerUtil
									.getNextIdentifier(catEntTblName);

							// Insert query for category entity table.
							final String insQryForCatEnt = INSERT_INTO_KEYWORD + catEntTblName
									+ " (IDENTIFIER, ACTIVITY_STATUS, " + RECORD_ID + ", "
									+ catEntFK + ") VALUES (?,?,?,?)";
							final LinkedList<ColumnValueBean> colValBeanList = createColumnValueBeanListForDataEntry(
									catEntId, entityId);
							final ColumnValueBean bean4 = new ColumnValueBean(catEntFK, rootRecId);
							colValBeanList.add(bean4);
							DynamicExtensionsUtility.executeUpdateQuery(insQryForCatEnt, userId,
									jdbcDao, colValBeanList);
						}

					}
					else
					{
						if (records.get(association.getTargetEntity().getName() + "["
								+ par.getTargetInstanceId() + "]") == null)
						{
							srcEntityId = new ArrayList<Long>();
							for (final Long sourceId : srcEntityId)
							{
								String packageName = null;
								packageName = getPackageName(association.getEntity(), packageName);

								final String sourceObjectClassName = packageName + "."
										+ association.getEntity().getName();
								final String targetObjectClassName = packageName + "."
										+ association.getTargetEntity().getName();

								final Object sourceObject = hibernateDao.retrieveById(
										sourceObjectClassName, sourceId);
								Object clonedSourceObject = cloner.clone(sourceObject);
								Object targetObject = null;
								// Create a new instance.
								targetObject = createObjectForClass(targetObjectClassName);

								for (Entry<BaseAbstractAttribute, Object> attrValueEntry : attrVsValues
										.entrySet())
								{
									setRelatedAttributeValues(targetObjectClassName, attrValueEntry
											.getKey(), attrValueEntry.getValue(), targetObject);
								}

								// Put the target object in a collection and
								// save it as a collection of the source object.

								addTargetObject(sourceObject, targetObject, targetObjectClassName,
										association);

								hibernateDao.insert(targetObject);
								hibernateDao.update(sourceObject, clonedSourceObject);
								final Long entityId = getObjectId(targetObject);
								srcEntityId.add(entityId);
							}

							records.put(association.getTargetEntity().getName() + "["
									+ par.getTargetInstanceId() + "]", srcEntityId);
						}
						else
						{
							srcEntityId = records.get(association.getTargetEntity().getName() + "["
									+ par.getTargetInstanceId() + "]");
						}
					}
				}
			}
		}
		catch (final Exception e)
		{
			throw new DynamicExtensionsApplicationException(
					ErrorConstants.ERROR_ENCNTR_INSERTING_REC, e);
		}
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.entitymanager.CategoryManagerInterface#editData(edu.common.dynamicextensions.domaininterface.CategoryEntityInterface, java.util.Map, java.lang.Long)
	 */
	public boolean editData(final CategoryEntityInterface rootCatEntity,
			final Map<BaseAbstractAttributeInterface, Object> dataValue, final Long recordId,
			final Long... userId) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException, SQLException
	{
		final CategoryInterface category = rootCatEntity.getCategory();
		JDBCDAO jdbcDao = null;
		Boolean isEdited = false;
		HibernateDAO hibernateDao = null;
		Long prevEntityId = null;
		List<FileQueryBean> fileAttrQueryList = new ArrayList<FileQueryBean>();
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			hibernateDao = DynamicExtensionsUtility.getHibernateDAO();
			final Long entityRecId = getRootCategoryEntityRecordId(rootCatEntity, recordId, jdbcDao);
			final Long identifier = (((userId != null) && (userId.length > 0)) ? userId[0] : null);
			final List<Long> entityRecIds = new ArrayList<Long>();
			entityRecIds.add(entityRecId);

			final Map<AbstractAttributeInterface, Object> rootCERecords = new HashMap<AbstractAttributeInterface, Object>();
			populateRootEntityRecordMap(rootCatEntity, rootCERecords, dataValue);

			final CategoryEntityRecord entityRecord = new CategoryEntityRecord(rootCatEntity
					.getId(), rootCatEntity.getName());
			prevEntityId = (Long) dataValue.get(entityRecord);
			// Roll back queries for category entity records.
			final Stack<String> rlbkQryStack = new Stack<String>();

			// Clear all records from entity table.
			final EntityManagerInterface entityManager = EntityManager.getInstance();
			isEdited = entityManager.editData(rootCatEntity.getEntity(), rootCERecords,
					entityRecId, hibernateDao, fileAttrQueryList, userId);

			// Clear all records from category entity table.
			clearCategoryEntityData(rootCatEntity, recordId, rlbkQryStack, identifier, jdbcDao);

			if (isEdited)
			{
				final Map<String, Long> keyMap = new HashMap<String, Long>();
				final Map<String, Long> fullKeyMap = new HashMap<String, Long>();
				final Map<String, List<Long>> recordsMap = new HashMap<String, List<Long>>();
				final String catEntityName = DynamicExtensionsUtility
						.getCategoryEntityName(rootCatEntity.getName());
				putRecordIdsInMap(rootCatEntity, entityRecId, fullKeyMap, keyMap, recordsMap);

				for (final CategoryAttributeInterface catAttribute : rootCatEntity
						.getAllCategoryAttributes())
				{
					dataValue.remove(catAttribute);
				}

				final boolean areMultplRecrds = false;
				final boolean isNoCatAttrPrsnt = false;

				//String entityFKColName = null;
				final String catEntFKColName = null;
				final Long srcCatEntityId = null;
				final Long srcEntityId = null;
				isEdited = false;

				editRecordsForCategoryEntityTree(catEntFKColName, srcCatEntityId, srcEntityId,
						prevEntityId, rootCatEntity, dataValue, keyMap, fullKeyMap, recordsMap,
						areMultplRecrds, isNoCatAttrPrsnt, jdbcDao, hibernateDao, isEdited,
						identifier, fileAttrQueryList);

				final Long rootCatEntId = getRootCategoryEntityRecordId(category
						.getRootCategoryElement(), fullKeyMap.get(catEntityName), jdbcDao);

				insertRecordsForRelatedAttributes(dataValue, rootCatEntId, category
						.getRootCategoryElement(), recordsMap, jdbcDao, hibernateDao, identifier);
				jdbcDao.commit();
				hibernateDao.commit();
				executeFileRecordQueryList(fileAttrQueryList);
			}
		}
		catch (final Exception e)
		{
			DynamicExtensionsUtility.rollBackDAO(jdbcDao);
			throw new DynamicExtensionsSystemException("Error while editing data", e);
		}
		finally
		{

			DynamicExtensionsUtility.closeDAO(jdbcDao);
			DynamicExtensionsUtility.closeDAO(hibernateDao);
		}

		return isEdited;
	}

	/**
	 * @param catEntFKColName
	 * @param srcCatEntityId
	 * @param srcEntityId
	 * @param previousEntityId
	 * @param categoryEnt
	 * @param dataValue
	 * @param keyMap
	 * @param fullKeyMap
	 * @param records
	 * @param areMultplRecrds
	 * @param isNoCatAttrPrsnt
	 * @param jdbcDao
	 * @param hibernateDao
	 * @param isNewRecord
	 * @param lastAsso
	 * @param userId
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void editRecordsForCategoryEntityTree(final String catEntFKColName,
			final Long srcCatEntityId, Long srcEntityId, Long previousEntityId,
			final CategoryEntityInterface categoryEnt, final Map dataValue,
			final Map<String, Long> keyMap, final Map<String, Long> fullKeyMap,
			final Map<String, List<Long>> records, boolean areMultplRecrds,
			boolean isNoCatAttrPrsnt, final JDBCDAO jdbcDao, final HibernateDAO hibernateDao,
			final Boolean isNewRecord, final Long userId, List<FileQueryBean> fileAttrQueryList)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		try
		{
			Object value = null;

			// Variable to check if record has been inserted for category entity.
			boolean isCatEntRecIns = false;
			Map<AbstractAttributeInterface, Object> attributes = null;

			final Set uiColumnSet = dataValue.keySet();
			final Iterator uiColumnSetIter = uiColumnSet.iterator();

			while (uiColumnSetIter.hasNext())
			{
				final BaseAbstractAttribute attribute = (BaseAbstractAttribute) uiColumnSetIter
						.next();
				value = dataValue.get(attribute);

				if (value == null)
				{
					continue;
				}

				Long entityId = null;

				if ((attribute instanceof CategoryAttributeInterface) && !isCatEntRecIns)
				{
					final String catEntTblName = categoryEnt.getTableProperties().getName();

					final EntityManagerInterface entityManager = EntityManager.getInstance();

					if (isNoCatAttrPrsnt)
					{
						attributes = null;
					}
					else
					{
						attributes = createAttributeMap(dataValue);
					}

					if (srcEntityId == null)
					{
						// Insert data for entity.
						entityId = entityManager.insertData(categoryEnt.getEntity(), attributes,
								hibernateDao, fileAttrQueryList, userId);

						srcEntityId = entityId;

					}
					else
					{
						entityId = srcEntityId;

						// Edit data for entity.
						entityManager.editData(categoryEnt.getEntity(), attributes, entityId,
								hibernateDao, fileAttrQueryList, userId);
					}

					Long catEntId = null;

					// Check whether table is created for category entity.
					if (((CategoryEntity) categoryEnt).isCreateTable())
					{
						catEntId = entityManagerUtil.getNextIdentifier(categoryEnt
								.getTableProperties().getName());

						// Insert query for category entity table.
						final String insertQuery = INSERT_INTO_KEYWORD + catEntTblName + " ("
								+ IDENTIFIER + ", ACTIVITY_STATUS, " + RECORD_ID
								+ ") VALUES (?,?,?)";
						final LinkedList<ColumnValueBean> colValBeanList = createColumnValueBeanListForDataEntry(
								catEntId, entityId);
						DynamicExtensionsUtility.executeUpdateQuery(insertQuery, userId, jdbcDao,
								colValBeanList);
					}

					if (catEntFKColName != null)
					{
						if (((CategoryEntity) categoryEnt).isCreateTable())
						{
							// Update query for category entity table.
							final String updateQuery = UPDATE_KEYWORD + catEntTblName + " SET "
									+ catEntFKColName + EQUAL + QUESTION_MARK + WHERE_KEYWORD
									+ IDENTIFIER + EQUAL + QUESTION_MARK;
							final ColumnValueBean bean1 = new ColumnValueBean("catEntFKColName",
									srcCatEntityId);
							final ColumnValueBean bean2 = new ColumnValueBean(IDENTIFIER, catEntId);
							final LinkedList<ColumnValueBean> colValBeanList = new LinkedList<ColumnValueBean>();
							colValBeanList.add(bean1);
							colValBeanList.add(bean2);
							DynamicExtensionsUtility.executeUpdateQuery(updateQuery, userId,
									jdbcDao, colValBeanList);
						}

						final PathInterface paths = categoryEnt.getPath();
						final List<PathAssociationRelationInterface> allPathAsso = paths
								.getSortedPathAssociationRelationCollection();
						for (final PathAssociationRelationInterface pathAssociation : allPathAsso)
						{
							final StringBuffer entityName = new StringBuffer(pathAssociation
									.getAssociation().getEntity().getName());
							final String catEntityName = "["
									+ pathAssociation.getSourceInstanceId() + "]";
							entityName.append(catEntityName);

							final StringBuffer targetEntName = new StringBuffer(pathAssociation
									.getAssociation().getTargetEntity().getName());
							final String targetcatEntityName = "["
									+ pathAssociation.getTargetInstanceId() + "]";
							targetEntName.append(targetcatEntityName);

							final Long sourceEntityId = fullKeyMap.get(entityName.toString());
							Long targetEntityId = fullKeyMap.get(targetEntName.toString());
							if (sourceEntityId != null)
							{
								previousEntityId = sourceEntityId;
							}
							if (targetEntityId == null
									|| pathAssociation.getAssociation().getTargetEntity().getId() == categoryEnt
											.getEntity().getId())
							{
								targetEntityId = entityId;
							}

							final AssociationInterface asso = pathAssociation.getAssociation();
							String packageName = null;
							packageName = getPackageName(asso.getEntity(), packageName);

							final StringBuffer sourceObjectClassName = new StringBuffer(packageName);
							final EntityInterface sourceEntity = asso.getEntity();
							sourceObjectClassName.append('.').append(sourceEntity.getName());

							final StringBuffer targetObjectClassName = new StringBuffer(packageName);
							final EntityInterface targetEntity = asso.getTargetEntity();
							targetObjectClassName.append('.').append(targetEntity.getName());

							final Object sourceObject = hibernateDao.retrieveById(
									sourceObjectClassName.toString(), previousEntityId);
							Object clonedSourceObject = cloner.clone(sourceObject);

							final Object targetObject = hibernateDao.retrieveById(
									targetObjectClassName.toString(), targetEntityId);
							Object clonedTargetObject = cloner.clone(targetObject);

							addSourceObject(sourceObject, targetObject, sourceObjectClassName
									.toString(), asso);

							hibernateDao.update(targetObject, clonedTargetObject);
							// Get the associated object(s).
							addTargetObject(sourceObject, targetObject, targetObjectClassName
									.toString(), asso);
							hibernateDao.update(sourceObject, clonedSourceObject);
						}
					}

					CategoryEntityInterface catEntity = categoryEnt;
					putRecordIdsInMap(catEntity, entityId, fullKeyMap, keyMap, records);

					isCatEntRecIns = true;
				}
				else if (attribute instanceof CategoryAssociationInterface)
				{
					final CategoryAssociationInterface catAssociation = (CategoryAssociationInterface) attribute;

					final PathInterface path = catAssociation.getTargetCategoryEntity().getPath();
					final Collection<PathAssociationRelationInterface> pathAssoRel = path
							.getSortedPathAssociationRelationCollection();

					// Foreign key column name.
					String fKeyColName = "";

					final List<Map<BaseAbstractAttributeInterface, Object>> mapsOfCntaindEnt = (List) value;

					final Map<CategoryAttributeInterface, Object> catAttributes = new HashMap<CategoryAttributeInterface, Object>();
					final Map<CategoryAssociationInterface, Object> catAssociations = new HashMap<CategoryAssociationInterface, Object>();

					for (final Map<BaseAbstractAttributeInterface, Object> valueMap : mapsOfCntaindEnt)
					{
						//isNewRecord = false;

						Long currentEntityId = null;

						final Set<BaseAbstractAttributeInterface> keySet = valueMap.keySet();
						final Iterator<BaseAbstractAttributeInterface> iter = keySet.iterator();
						while (iter.hasNext())
						{
							final Object obj = iter.next();
							if (obj instanceof CategoryAttributeInterface)
							{
								catAttributes.put((CategoryAttributeInterface) obj, valueMap
										.get(obj));
							}
							else if (obj instanceof CategoryEntityRecord)
							{
								currentEntityId = (Long) valueMap.get(obj);
							}
							else if (obj instanceof CategoryAssociationInterface)
							{
								catAssociations.put((CategoryAssociationInterface) obj, valueMap
										.get(obj));
							}
						}

						final EntityInterface entity = catAssociation.getTargetCategoryEntity()
								.getEntity();

						boolean areIntermediateRecords = false;
						Long intermediateEntityId = null;

						for (final PathAssociationRelationInterface par : pathAssoRel)
						{
							final AssociationInterface association = par.getAssociation();
							fKeyColName = association.getConstraintProperties()
									.getTgtEntityConstraintKeyProperties()
									.getTgtForiegnKeyColumnProperties().getName();

							if (association.getTargetEntity().getId() == entity.getId())
							{
								if ((intermediateEntityId == null) && (currentEntityId != null))
								{
									final String query = SELECT_KEYWORD
											+ fKeyColName
											+ FROM_KEYWORD
											+ association.getTargetEntity().getTableProperties()
													.getName() + WHERE_KEYWORD + IDENTIFIER + EQUAL
											+ QUESTION_MARK;

									final LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
									queryDataList.add(new ColumnValueBean(IDENTIFIER,
											currentEntityId));
									final List<Long> ids = getResultIDList(query, fKeyColName,
											jdbcDao, queryDataList);
									if ((ids != null) && !ids.isEmpty())
									{
										intermediateEntityId = ids.get(0);
									}

									fullKeyMap.put(association.getTargetEntity().getName() + "["
											+ par.getTargetInstanceId() + "]", currentEntityId);

									keyMap.put(association.getTargetEntity().getName() + "["
											+ par.getTargetInstanceId() + "]", currentEntityId);

									fullKeyMap
											.put(association.getEntity().getName() + "["
													+ par.getSourceInstanceId() + "]",
													intermediateEntityId);

									keyMap
											.put(association.getEntity().getName() + "["
													+ par.getSourceInstanceId() + "]",
													intermediateEntityId);
								}

							}
							else
							{
								areIntermediateRecords = true;

								// This means new record needs to be inserted.
								if (currentEntityId == null)
								{
									// Get the record identifier from the intermediate table.
									intermediateEntityId = fullKeyMap.get(association
											.getTargetEntity().getName()
											+ "[" + par.getTargetInstanceId() + "]");

									// The add more button could open up a link that contains an entity
									// in the path. In such case record will not be present in this table.
									// Therefore, the record needs to be first inserted in this intermediate
									// table and then the identifier from this table should be used as a FK
									// in usual way.
									if (intermediateEntityId == null)
									{
										String packageName = null;
										packageName = getPackageName(association.getEntity(),
												packageName);

										String sourceObjectClassName = packageName + "."
												+ association.getEntity().getName();

										final String targetObjectClassName = packageName + "."
												+ association.getTargetEntity().getName();

										Long sourceObjId = fullKeyMap.get(association.getEntity()
												.getName()
												+ "[" + par.getSourceInstanceId() + "]");

										// In case of inherited association, the 'fullKeyMap' will not
										// contain the record id of the parent to whom this association
										// belongs. Hence go up the hierarchy till the parent is found
										// and use its identifier
										if (sourceObjId == null)
										{
											EntityInterface sourceEntity = catAssociation
													.getCategoryEntity().getEntity();

											while (sourceEntity.getParentEntity() != null)
											{
												final EntityInterface parentEntity = sourceEntity
														.getParentEntity();
												if (parentEntity.getId() == association.getEntity()
														.getId())
												{
													sourceObjId = fullKeyMap
															.get(catAssociation.getCategoryEntity()
																	.getEntity().getName()
																	+ "["
																	+ par.getSourceInstanceId()
																	+ "]");
													break;
												}
												else
												{
													sourceEntity = parentEntity;
												}
											}

											sourceObjectClassName = packageName
													+ "."
													+ catAssociation.getCategoryEntity()
															.getEntity().getName();
										}

										final Object sourceObject = hibernateDao.retrieveById(
												sourceObjectClassName, sourceObjId);
										Object clonedSourceObject = cloner.clone(sourceObject);

										Object targetObject = null;

										String sourceRoleName = association.getSourceRole()
												.getName();
										sourceRoleName = sourceRoleName.substring(0, 1)
												.toUpperCase()
												+ sourceRoleName.substring(1, sourceRoleName
														.length());

										String targetRoleName = association.getTargetRole()
												.getName();
										targetRoleName = targetRoleName.substring(0, 1)
												.toUpperCase()
												+ targetRoleName.substring(1, targetRoleName
														.length());

										// Create a new instance.
										targetObject = createObjectForClass(targetObjectClassName);
										hibernateDao.insert(targetObject);
										Object clonedTargetObject = cloner.clone(targetObject);

										addTargetObject(sourceObject, targetObject,
												targetObjectClassName, association);

										hibernateDao.update(sourceObject, clonedSourceObject);
										addSourceObject(sourceObject, targetObject,
												sourceObjectClassName, association);

										hibernateDao.update(targetObject, clonedTargetObject);
										final Long insertedObjectId = getObjectId(targetObject);

										intermediateEntityId = insertedObjectId;

										fullKeyMap.put(association.getTargetEntity().getName()
												+ "[" + par.getTargetInstanceId() + "]",
												intermediateEntityId);
									}
								}
								else
								{

									final Long fKeyColData = fullKeyMap.get(association.getEntity()
											.getName()
											+ "[" + par.getSourceInstanceId() + "]");
									final String query = SELECT_KEYWORD
											+ IDENTIFIER
											+ FROM_KEYWORD
											+ association.getTargetEntity().getTableProperties()
													.getName() + WHERE_KEYWORD + fKeyColName
											+ EQUAL + QUESTION_MARK;
									final LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
									queryDataList
											.add(new ColumnValueBean(fKeyColName, fKeyColData));
									final List<Long> ids = getResultIDList(query, IDENTIFIER,
											jdbcDao, queryDataList);
									if ((ids != null) && !ids.isEmpty())
									{
										if (ids.size() == 1)
										{
											intermediateEntityId = ids.get(0);
										}
										else
										{
											intermediateEntityId = fullKeyMap.get(association
													.getTargetEntity().getName()
													+ "[" + par.getTargetInstanceId() + "]");
										}
									}

									if (intermediateEntityId != null)
									{
										fullKeyMap.put(association.getTargetEntity().getName()
												+ "[" + par.getTargetInstanceId() + "]",
												intermediateEntityId);

										keyMap.put(association.getTargetEntity().getName() + "["
												+ par.getTargetInstanceId() + "]",
												intermediateEntityId);
									}
								}
							}
						}

						final String selectQuery = SELECT_KEYWORD + IDENTIFIER + FROM_KEYWORD
								+ catAssociation.getCategoryEntity().getTableProperties().getName()
								+ WHERE_KEYWORD + RECORD_ID + EQUAL + QUESTION_MARK;
						final LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
						queryDataList.add(new ColumnValueBean(RECORD_ID, previousEntityId));
						final List<Long> identifiers = getResultIDList(selectQuery, IDENTIFIER,
								jdbcDao, queryDataList);

						Long resultId = null;
						if ((identifiers != null) && !identifiers.isEmpty())
						{
							resultId = identifiers.get(0);
						}

						final Long srcCategoryEntId = resultId;

						final String catEntFKClmnName = catAssociation.getConstraintProperties()
								.getTgtEntityConstraintKeyProperties()
								.getTgtForiegnKeyColumnProperties().getName();

						if (mapsOfCntaindEnt.size() > 1)
						{
							areMultplRecrds = true;
						}
						else
						{
							areMultplRecrds = false;
						}

						// Insert data for category attributes. If there are no category attributes in a
						// particular instance, or all are related category attributes, then create a dummy
						// category attribute so as to link the records.
						if (catAttributes.isEmpty()
								|| isAllRelatedCategoryAttributesCollection(catAttributes))
						{
							isNoCatAttrPrsnt = true;
							final CategoryAttributeInterface dummyCatAttr = DomainObjectFactory
									.getInstance().createCategoryAttribute();
							dummyCatAttr
									.setCategoryEntity(catAssociation.getTargetCategoryEntity());
							catAttributes.put(dummyCatAttr, "");

							if (areIntermediateRecords)
							{
								editRecordsForCategoryEntityTree(catEntFKClmnName,
										srcCategoryEntId, currentEntityId, intermediateEntityId,
										catAssociation.getTargetCategoryEntity(), catAttributes,
										keyMap, fullKeyMap, records, areMultplRecrds,
										isNoCatAttrPrsnt, jdbcDao, hibernateDao, isNewRecord,
										userId, fileAttrQueryList);
							}
							else
							{
								editRecordsForCategoryEntityTree(catEntFKClmnName,
										srcCategoryEntId, currentEntityId, previousEntityId,
										catAssociation.getTargetCategoryEntity(), catAttributes,
										keyMap, fullKeyMap, records, areMultplRecrds,
										isNoCatAttrPrsnt, jdbcDao, hibernateDao, isNewRecord,
										userId, fileAttrQueryList);
							}

							isNoCatAttrPrsnt = false;
						}
						else
						{
							if (areIntermediateRecords)
							{
								editRecordsForCategoryEntityTree(catEntFKClmnName,
										srcCategoryEntId, currentEntityId, intermediateEntityId,
										catAssociation.getTargetCategoryEntity(), catAttributes,
										keyMap, fullKeyMap, records, areMultplRecrds,
										isNoCatAttrPrsnt, jdbcDao, hibernateDao, isNewRecord,
										userId, fileAttrQueryList);
							}
							else
							{
								editRecordsForCategoryEntityTree(catEntFKClmnName,
										srcCategoryEntId, currentEntityId, previousEntityId,
										catAssociation.getTargetCategoryEntity(), catAttributes,
										keyMap, fullKeyMap, records, areMultplRecrds,
										isNoCatAttrPrsnt, jdbcDao, hibernateDao, isNewRecord,
										userId, fileAttrQueryList);
							}
						}

						catAttributes.clear();

						// Insert data for category associations.
						if (currentEntityId == null)
						{
							currentEntityId = fullKeyMap.get(DynamicExtensionsUtility
									.getCategoryEntityName(catAssociation.getTargetCategoryEntity()
											.getName()));
						}

						// The previous entity id (prevEntityId) will now be equal to
						// the present entity id (currentEntityId) to do the data entry
						// for category associations of current entity.
						editRecordsForCategoryEntityTree(catEntFKClmnName, srcCategoryEntId,
								currentEntityId, currentEntityId, catAssociation
										.getTargetCategoryEntity(), catAssociations, keyMap,
								fullKeyMap, records, areMultplRecrds, isNoCatAttrPrsnt, jdbcDao,
								hibernateDao, isNewRecord, userId, fileAttrQueryList);
						catAssociations.clear();

						fullKeyMap.putAll(keyMap);
						keyMap.remove(DynamicExtensionsUtility.getCategoryEntityName(catAssociation
								.getTargetCategoryEntity().getName()));
					}
				}
			}
		}
		catch (final Exception e)
		{
			throw new DynamicExtensionsApplicationException(
					"Exception encountered while inserting records!", e);
		}
	}

	/**
	 * This helper method recursively inserts records for a single category entity
	 * and all its category associations i.e. in turn for a whole category entity tree.
	 * @param entityFKColName
	 * @param catEntFKColName
	 * @param srcCatEntityId
	 * @param srcEntityId
	 * @param categoryEnt
	 * @param dataValue
	 * @param keyMap
	 * @param fullKeyMap
	 * @param records
	 * @param areMultplRecrds
	 * @param isNoCatAttrPrsnt
	 * @param jdbcDao
	 * @param userId
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void insertRecordsForCategoryEntityTree(final String entityFKColName,
			final String catEntFKColName, final Long srcCatEntityId, final Long srcEntityId,
			final CategoryEntityInterface categoryEnt, final Map dataValue,
			final Map<String, Long> keyMap, final Map<String, Long> fullKeyMap,
			final Map<String, List<Long>> records, boolean areMultplRecrds,
			boolean isNoCatAttrPrsnt, final JDBCDAO jdbcDao, final HibernateDAO hibernateDao,
			final boolean isEditMode, final AssociationInterface lastAsso, final Long userId,
			List<FileQueryBean> fileAttrQueryList) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		try
		{
			Object value = null;

			// Variable to check if record has been inserted for category entity.
			boolean isCatEntRecIns = false;
			Map<AbstractAttributeInterface, Object> attributes = null;

			final Set uiColumnSet = dataValue.keySet();
			final Iterator uiColumnSetIter = uiColumnSet.iterator();

			while (uiColumnSetIter.hasNext())
			{
				final BaseAbstractAttribute attribute = (BaseAbstractAttribute) uiColumnSetIter
						.next();
				value = dataValue.get(attribute);

				if (value == null)
				{
					continue;
				}

				if ((attribute instanceof CategoryAttributeInterface) && !isCatEntRecIns)
				{
					final String catEntTblName = categoryEnt.getTableProperties().getName();

					Long entityId = null;
					final EntityManagerInterface entityManager = EntityManager.getInstance();

					if (isNoCatAttrPrsnt)
					{
						attributes = null;
					}
					else
					{
						attributes = createAttributeMap(dataValue);
					}
					final String catEntName = DynamicExtensionsUtility
							.getCategoryEntityName(((CategoryAttribute) attribute)
									.getCategoryEntity().getName());
					if ((keyMap.get(catEntName) != null) && !areMultplRecrds)
					{
						entityId = keyMap.get(catEntName);

						// Edit data for entity hierarchy.
						entityManager.editData(categoryEnt.getEntity(), attributes, entityId,
								hibernateDao, fileAttrQueryList, userId);
					}
					else
					{
						entityId = entityManager.insertData(categoryEnt.getEntity(), attributes,
								hibernateDao, fileAttrQueryList, userId);
					}

					Long catEntId = null;

					// Check whether table is created for category entity.
					if (((CategoryEntity) categoryEnt).isCreateTable())
					{
						catEntId = entityManagerUtil.getNextIdentifier(categoryEnt
								.getTableProperties().getName());

						// Insert query for category entity table.
						final String insertQuery = INSERT_INTO_KEYWORD + catEntTblName + " ("
								+ IDENTIFIER + ", ACTIVITY_STATUS, " + RECORD_ID
								+ ") VALUES (?,?,?)";

						final LinkedList<ColumnValueBean> colValBeanList = createColumnValueBeanListForDataEntry(
								catEntId, entityId);
						DynamicExtensionsUtility.executeUpdateQuery(insertQuery, userId, jdbcDao,
								colValBeanList);
					}

					if ((catEntFKColName != null) && (entityFKColName != null))
					{
						if (((CategoryEntity) categoryEnt).isCreateTable())
						{
							// Update query for category entity table.
							final String updateQuery = UPDATE_KEYWORD + catEntTblName + " SET "
									+ catEntFKColName + EQUAL + QUESTION_MARK + WHERE_KEYWORD
									+ IDENTIFIER + EQUAL + QUESTION_MARK;
							final ColumnValueBean bean1 = new ColumnValueBean(catEntFKColName,
									srcCatEntityId);
							final ColumnValueBean bean2 = new ColumnValueBean(IDENTIFIER, catEntId);
							final LinkedList<ColumnValueBean> colValBeanList = new LinkedList<ColumnValueBean>();
							colValBeanList.add(bean1);
							colValBeanList.add(bean2);
							DynamicExtensionsUtility.executeUpdateQuery(updateQuery, userId,
									jdbcDao, colValBeanList);
						}

						String packageName = null;
						packageName = getPackageName(lastAsso.getEntity(), packageName);
						final String sourceObjectClassName = packageName + "."
								+ lastAsso.getEntity().getName();
						final String targetObjectClassName = packageName + "."
								+ lastAsso.getTargetEntity().getName();
						final Object sourceObject = hibernateDao.retrieveById(
								sourceObjectClassName, srcEntityId);
						final Object targetObject = hibernateDao.retrieveById(
								targetObjectClassName, entityId);
						Object clonedTarget = cloner.clone(targetObject);
						addSourceObject(sourceObject, targetObject, sourceObjectClassName, lastAsso);

						hibernateDao.update(targetObject, clonedTarget);

					}

					CategoryEntityInterface catEntity = categoryEnt;
					putRecordIdsInMap(catEntity, entityId, fullKeyMap, keyMap, records);

					isCatEntRecIns = true;
				}
				else if (attribute instanceof CategoryAssociationInterface)
				{
					final CategoryAssociationInterface catAssociation = (CategoryAssociationInterface) attribute;

					final PathInterface path = catAssociation.getTargetCategoryEntity().getPath();
					final Collection<PathAssociationRelationInterface> pathAssoRel = path
							.getSortedPathAssociationRelationCollection();

					Long sourceEntityId = fullKeyMap.get(DynamicExtensionsUtility
							.getCategoryEntityName(catAssociation.getCategoryEntity().getName()));

					// Foreign key column name.
					String fKeyColName = "";

					final String selectQuery = SELECT_KEYWORD + IDENTIFIER + FROM_KEYWORD
							+ catAssociation.getCategoryEntity().getTableProperties().getName()
							+ WHERE_KEYWORD + RECORD_ID + EQUAL + QUESTION_MARK;
					final LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
					queryDataList.add(new ColumnValueBean(RECORD_ID, sourceEntityId));
					final List<Long> identifiers = getResultIDList(selectQuery, IDENTIFIER,
							jdbcDao, queryDataList);

					Long resultId = null;
					if ((identifiers != null) && !identifiers.isEmpty())
					{
						resultId = identifiers.get(0);
					}

					final Long srcCategoryEntId = resultId;

					final String catEntFKClmnName = catAssociation.getConstraintProperties()
							.getTgtEntityConstraintKeyProperties()
							.getTgtForiegnKeyColumnProperties().getName();

					final EntityInterface entity = catAssociation.getTargetCategoryEntity()
							.getEntity();

					AssociationInterface lastAssociation = null;
					for (final PathAssociationRelationInterface par : pathAssoRel)
					{
						final AssociationInterface association = par.getAssociation();
						lastAssociation = par.getAssociation();

						fKeyColName = association.getConstraintProperties()
								.getTgtEntityConstraintKeyProperties()
								.getTgtForiegnKeyColumnProperties().getName();

						if (association.getTargetEntity().getId() == entity.getId())
						{
							sourceEntityId = fullKeyMap.get(association.getEntity().getName() + "["
									+ par.getSourceInstanceId() + "]");

							if (sourceEntityId == null)
							{
								EntityInterface sourceEntity = catAssociation.getCategoryEntity()
										.getEntity();
								while (sourceEntity.getParentEntity() != null)
								{
									final EntityInterface parentEntity = sourceEntity
											.getParentEntity();
									if (parentEntity.getId() == association.getEntity().getId())
									{
										sourceEntityId = fullKeyMap.get(catAssociation
												.getCategoryEntity().getEntity().getName()
												+ "[" + par.getSourceInstanceId() + "]");
										break;
									}
									else
									{
										sourceEntity = parentEntity;
									}
								}
							}
						}
						else
						{
							if (fullKeyMap.get(association.getTargetEntity().getName() + "["
									+ par.getTargetInstanceId() + "]") == null)
							{
								String packageName = null;
								packageName = getPackageName(association.getEntity(), packageName);
								final String sourceObjectClassName = packageName + "."
										+ association.getEntity().getName();
								final String targetObjectClassName = packageName + "."
										+ association.getTargetEntity().getName();
								Long Identifier = fullKeyMap.get(association.getEntity().getName()
										+ "[" + par.getSourceInstanceId() + "]");
								final Object sourceObject = hibernateDao.retrieveById(
										sourceObjectClassName, Identifier);
								Object clonedSourceObject = cloner.clone(sourceObject);
								Object targetObject = createObjectForClass(targetObjectClassName);

								hibernateDao.insert(targetObject);
								Object clonedTargetObject = cloner.clone(targetObject);
								addTargetObject(sourceObject, targetObject, targetObjectClassName,
										association);

								hibernateDao.update(sourceObject, clonedSourceObject);
								addSourceObject(sourceObject, targetObject, sourceObjectClassName,
										association);

								hibernateDao.update(targetObject, clonedTargetObject);

								final Long entityId = getObjectId(targetObject);
								sourceEntityId = entityId;

								fullKeyMap.put(association.getTargetEntity().getName() + "["
										+ par.getTargetInstanceId() + "]", sourceEntityId);
								keyMap.put(association.getTargetEntity().getName() + "["
										+ par.getTargetInstanceId() + "]", sourceEntityId);

								List<Long> recIds = records.get(association.getTargetEntity()
										.getName()
										+ "[" + par.getTargetInstanceId() + "]");
								if (recIds == null)
								{
									recIds = new ArrayList<Long>();
								}
								recIds.add(entityId);
								records.put(association.getTargetEntity().getName() + "["
										+ par.getTargetInstanceId() + "]", recIds);
							}
							else
							{
								sourceEntityId = fullKeyMap.get(association.getTargetEntity()
										.getName()
										+ "[" + par.getTargetInstanceId() + "]");
							}
						}
					}

					final List<Map<BaseAbstractAttributeInterface, Object>> mapsOfCntaindEnt = (List) value;

					final Map<CategoryAttributeInterface, Object> catAttributes = new HashMap<CategoryAttributeInterface, Object>();
					final Map<CategoryAssociationInterface, Object> catAssociations = new HashMap<CategoryAssociationInterface, Object>();

					for (final Map<BaseAbstractAttributeInterface, Object> valueMap : mapsOfCntaindEnt)
					{
						final Set<BaseAbstractAttributeInterface> keySet = valueMap.keySet();
						final Iterator<BaseAbstractAttributeInterface> iter = keySet.iterator();
						while (iter.hasNext())
						{
							final Object obj = iter.next();
							if (obj instanceof CategoryAttributeInterface)
							{
								catAttributes.put((CategoryAttributeInterface) obj, valueMap
										.get(obj));
							}
							else
							{
								catAssociations.put((CategoryAssociationInterface) obj, valueMap
										.get(obj));
							}
						}

						if (mapsOfCntaindEnt.size() > 1)
						{
							areMultplRecrds = true;
						}
						else
						{
							areMultplRecrds = false;
						}

						// Insert data for category attributes. If there are no category attributes in a
						// particular instance, or all are related category attributes, then create a dummy
						// category attribute so as to link the records.
						if (catAttributes.isEmpty()
								|| isAllRelatedCategoryAttributesCollection(catAttributes))
						{
							isNoCatAttrPrsnt = true;
							final CategoryAttributeInterface dummyCatAttr = DomainObjectFactory
									.getInstance().createCategoryAttribute();
							dummyCatAttr
									.setCategoryEntity(catAssociation.getTargetCategoryEntity());
							catAttributes.put(dummyCatAttr, "");

							insertRecordsForCategoryEntityTree(fKeyColName, catEntFKClmnName,
									srcCategoryEntId, sourceEntityId, catAssociation
											.getTargetCategoryEntity(), catAttributes, keyMap,
									fullKeyMap, records, areMultplRecrds, isNoCatAttrPrsnt,
									jdbcDao, hibernateDao, isEditMode, lastAssociation, userId,
									fileAttrQueryList);

							isNoCatAttrPrsnt = false;
						}
						else
						{
							insertRecordsForCategoryEntityTree(fKeyColName, catEntFKClmnName,
									srcCategoryEntId, sourceEntityId, catAssociation
											.getTargetCategoryEntity(), catAttributes, keyMap,
									fullKeyMap, records, areMultplRecrds, isNoCatAttrPrsnt,
									jdbcDao, hibernateDao, isEditMode, lastAssociation, userId,
									fileAttrQueryList);
						}
						catAttributes.clear();

						// Insert data for category associations.
						insertRecordsForCategoryEntityTree(fKeyColName, catEntFKClmnName,
								srcCategoryEntId, sourceEntityId, catAssociation
										.getTargetCategoryEntity(), catAssociations, keyMap,
								fullKeyMap, records, areMultplRecrds, isNoCatAttrPrsnt, jdbcDao,
								hibernateDao, isEditMode, lastAssociation, userId,
								fileAttrQueryList);
						catAssociations.clear();

						fullKeyMap.putAll(keyMap);
						keyMap.remove(DynamicExtensionsUtility.getCategoryEntityName(catAssociation
								.getTargetCategoryEntity().getName()));
					}
				}
			}
		}
		catch (final Exception e)
		{
			throw new DynamicExtensionsApplicationException(
					"Exception encountered while inserting records!", e);
		}
	}

	/**
	 * @param formId
	 * @param recordEntryIdList
	 * @param recordEntryStaticId
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws DAOException
	 * @throws SQLException
	 */
	public List<Map<BaseAbstractAttributeInterface, Object>> getRecordByRecordEntryId(Long formId,
			List<Long> recordEntryIdList, Long recordEntryStaticId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			DAOException, SQLException
	{
		List<Map<BaseAbstractAttributeInterface, Object>> recordMapList = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
		DEIntegration deIntegration = new DEIntegration();
		JDBCDAO jdbcDao = null;
		CategoryEntityInterface rootCatEntity = (CategoryEntityInterface) DynamicExtensionsUtility
				.getContainerByIdentifier(formId.toString()).getAbstractEntity();
		try
		{
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
			for (Long recordEntryId : recordEntryIdList)
			{
				Collection recIdList = deIntegration.getCategoryRecIdBasedOnHookEntityRecId(formId,
						recordEntryId, recordEntryStaticId);
				if (recIdList != null && !recIdList.isEmpty())
				{
					Long dynamicRecId = (Long) recIdList.iterator().next();
					Map<BaseAbstractAttributeInterface, Object> dataValue = new HashMap<BaseAbstractAttributeInterface, Object>();
					Long categoryRecordId = getRootCategoryEntityRecordIdByEntityRecordId(
							dynamicRecId, rootCatEntity.getTableProperties().getName());
					if (categoryRecordId == null)
					{
						throw new DynamicExtensionsSystemException(
								"Exception in execution query :: " + "select IDENTIFIER from "
										+ rootCatEntity.getTableProperties().getName()
										+ " where record_Id = " + dynamicRecId);
					}
					retrieveRecords(rootCatEntity, dataValue, categoryRecordId, jdbcDao);
					recordMapList.add(dataValue);
				}
				else
				{
					throw new DynamicExtensionsSystemException("Exception in execution query :: "
							+ "Unhooked data present in database for recordEntryId "
							+ recordEntryId + " formId :" + formId + " recordEntryStaticId : "
							+ recordEntryStaticId);
				}
			}
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(jdbcDao);
		}
		return recordMapList;
	}

	/**
	 * This method returns the category data value map for the given root category entity.
	 * @param rootCatEntity
	 * @param recordId
	 * @return map of category entity data
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws SQLException
	 */
	public Map<BaseAbstractAttributeInterface, Object> getRecordById(
			final CategoryEntityInterface rootCatEntity, final Long recordId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		Map<BaseAbstractAttributeInterface, Object> dataValue = new HashMap<BaseAbstractAttributeInterface, Object>();

		JDBCDAO jdbcDAO = null;

		try
		{
			jdbcDAO = DynamicExtensionsUtility.getJDBCDAO();
			retrieveRecords(rootCatEntity, dataValue, recordId, jdbcDAO);
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(jdbcDAO);
		}

		final Map<BaseAbstractAttributeInterface, Object> curatedRecords = new HashMap<BaseAbstractAttributeInterface, Object>();
		curateMapForRelatedAttributes(curatedRecords, dataValue);

		dataValue = curatedRecords;

		return dataValue;
	}

	/**
	 * @param rootCatEntity
	 * @param recordId
	 * @param dao
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws SQLException
	 */
	public Map<BaseAbstractAttributeInterface, Object> getRecordById(
			final CategoryEntityInterface rootCatEntity, final Long recordId, JDBCDAO dao)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			SQLException
	{
		Map<BaseAbstractAttributeInterface, Object> dataValue = new HashMap<BaseAbstractAttributeInterface, Object>();

		retrieveRecords(rootCatEntity, dataValue, recordId, dao);

		final Map<BaseAbstractAttributeInterface, Object> curatedRecords = new HashMap<BaseAbstractAttributeInterface, Object>();
		curateMapForRelatedAttributes(curatedRecords, dataValue);

		dataValue = curatedRecords;

		return dataValue;
	}

	/**
	 * @param rootCatEntity
	 * @param recordId
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws SQLException
	 */
	public Map<String, Map<String, Object>> getRelatedAttributeValues(
			final CategoryEntityInterface rootCatEntity, final Long recordId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			SQLException
	{
		final CategoryManager categoreyManager = (CategoryManager) CategoryManager.getInstance();

		final Map<BaseAbstractAttributeInterface, Object> recordMap = categoreyManager
				.getRecordById(rootCatEntity, recordId);

		final Map<String, Map<String, Object>> records = new HashMap<String, Map<String, Object>>();

		getMapOfRelatedAttributesValues(records, recordMap);

		return records;
	}

	/**
	 * This method keeps only the related invisible category attributes in the
	 * map.
	 *
	 * @param records
	 * @param dataValue
	 */
	private void getMapOfRelatedAttributesValues(final Map<String, Map<String, Object>> records,
			final Map<BaseAbstractAttributeInterface, Object> dataValue)
	{
		dataValue.keySet().iterator();
		for (Entry<BaseAbstractAttributeInterface, Object> dataValueEntry : dataValue.entrySet())
		{
			BaseAbstractAttributeInterface obj = dataValueEntry.getKey();

			if (obj instanceof CategoryAttributeInterface)
			{
				final CategoryAttributeInterface catAttr = (CategoryAttributeInterface) obj;

				if ((catAttr.getIsRelatedAttribute() != null) && catAttr.getIsRelatedAttribute()
						&& (catAttr.getIsVisible() != null) && !catAttr.getIsVisible())
				{
					Map<String, Object> innerMap;

					if (records.get(catAttr.getCategoryEntity().getName()) == null)
					{
						innerMap = new HashMap<String, Object>();
						innerMap.put(catAttr.getName(), dataValueEntry.getValue());
					}
					else
					{
						innerMap = records.get(catAttr.getCategoryEntity().getName());
						innerMap.put(catAttr.getName(), dataValueEntry.getValue());
					}

					records.put(catAttr.getCategoryEntity().getName(), innerMap);
				}
			}
			else
			{
				final CategoryAssociationInterface catAssociation = (CategoryAssociationInterface) obj;

				final List<Map<BaseAbstractAttributeInterface, Object>> mapsOfCntdRec = (List<Map<BaseAbstractAttributeInterface, Object>>) dataValue
						.get(catAssociation);

				for (final Map<BaseAbstractAttributeInterface, Object> map : mapsOfCntdRec)
				{
					getMapOfRelatedAttributesValues(records, map);
				}
			}
		}
	}

	/**
	 * This method removes related invisible category attributes from the map.
	 * @param curatedRecords
	 * @param dataValue
	 */
	private void curateMapForRelatedAttributes(
			final Map<BaseAbstractAttributeInterface, Object> curatedRecords,
			final Map<BaseAbstractAttributeInterface, Object> dataValue)
	{
		final Iterator<Map.Entry<BaseAbstractAttributeInterface, Object>> iter = dataValue
				.entrySet().iterator();
		while (iter.hasNext())
		{
			final Entry<BaseAbstractAttributeInterface, Object> nextEntryObject = iter.next();
			final Object obj = nextEntryObject.getKey();

			if (obj instanceof CategoryAttributeInterface)
			{
				final CategoryAttributeInterface catAttr = (CategoryAttributeInterface) obj;

				if ((catAttr.getIsRelatedAttribute() != null) && catAttr.getIsRelatedAttribute()
						&& (catAttr.getIsVisible() != null) && catAttr.getIsVisible())
				{
					curatedRecords.put((BaseAbstractAttributeInterface) obj, nextEntryObject
							.getValue());
				}
				else if ((catAttr.getIsRelatedAttribute() != null)
						&& !catAttr.getIsRelatedAttribute())
				{
					curatedRecords.put((BaseAbstractAttributeInterface) obj, nextEntryObject
							.getValue());
				}
			}
			else if (obj instanceof CategoryEntityRecord)
			{
				final CategoryEntityRecord entityRecord = (CategoryEntityRecord) obj;
				curatedRecords.put(entityRecord, nextEntryObject.getValue());
			}
			else
			{
				final CategoryAssociationInterface catAssociation = (CategoryAssociationInterface) obj;

				final List<Map<BaseAbstractAttributeInterface, Object>> mapsOfCntdRec = (List) nextEntryObject
						.getValue();
				final List<Map<BaseAbstractAttributeInterface, Object>> innerRecList = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();

				for (final Map<BaseAbstractAttributeInterface, Object> map : mapsOfCntdRec)
				{
					final Map<BaseAbstractAttributeInterface, Object> innerRecords = new HashMap<BaseAbstractAttributeInterface, Object>();
					curateMapForRelatedAttributes(innerRecords, map);
					innerRecList.add(innerRecords);
				}

				curatedRecords.put(catAssociation, innerRecList);
			}
		}
	}

	/**
	 * @param catEntity
	 * @param dataValue
	 * @param rootCatEntRecId
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void retrieveRecords(final CategoryEntityInterface catEntity,
			final Map<BaseAbstractAttributeInterface, Object> dataValue, long rootCatEntRecId,
			final JDBCDAO jdbcDAO) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		Long recordId = null;
		String catEntTblName = "";
		String selRecIdQuery = "";

		boolean isRecordIdNull = false;

		// Only the root category entity has the category object set in it.
		if (catEntity.getCategory() != null)
		{
			catEntTblName = catEntity.getTableProperties().getName();

			selRecIdQuery = SELECT_KEYWORD + RECORD_ID + FROM_KEYWORD + catEntTblName
					+ WHERE_KEYWORD + IDENTIFIER + EQUAL + QUESTION_MARK;
			final LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
			queryDataList.add(new ColumnValueBean(IDENTIFIER, rootCatEntRecId));
			final List<Long> identifiers = getResultIDList(selRecIdQuery, RECORD_ID, jdbcDAO,
					queryDataList);

			if ((identifiers != null) && !identifiers.isEmpty())
			{
				recordId = identifiers.get(0);
			}
		}

		// If entity model is different than category model then entity data is inserted
		// according to entity model and category data is inserted according to category model.
		// In this case recordId can be NULL in category entity table.
		if (recordId == null)
		{
			isRecordIdNull = true;
			recordId = rootCatEntRecId;
		}

		final Map<AbstractAttributeInterface, Object> entityRecords = new HashMap<AbstractAttributeInterface, Object>();
		entityRecords.putAll(EntityManager.getInstance().getEntityRecordById(catEntity.getEntity(),
				recordId, jdbcDAO));

		// If root category entity has parent entity, then get data from parent entity table
		// with same record id.
		CategoryEntityInterface parentCatEnt = catEntity.getParentCategoryEntity();
		while (parentCatEnt != null)
		{
			final Map<AbstractAttributeInterface, Object> innerValues = EntityManager.getInstance()
					.getEntityRecordById(parentCatEnt.getEntity(), recordId, jdbcDAO);
			if (innerValues != null)
			{
				entityRecords.putAll(innerValues);
			}

			parentCatEnt = parentCatEnt.getParentCategoryEntity();
		}

		if (!isAllRelatedInvisibleCategoryAttributesCollection(catEntity))
		{
			for (final CategoryAttributeInterface catAttribute : catEntity
					.getAllCategoryAttributes())
			{
				dataValue.put(catAttribute, entityRecords.get(catAttribute.getAbstractAttribute()));
			}
			final CategoryEntityRecord catEntityRecord = new CategoryEntityRecord(
					catEntity.getId(), catEntity.getName());
			dataValue.put(catEntityRecord, recordId);

		}

		final Collection<CategoryAssociationInterface> catAssociations = new ArrayList<CategoryAssociationInterface>(
				catEntity.getCategoryAssociationCollection());
		for (final CategoryAssociationInterface catAssociation : catAssociations)
		{
			final CategoryEntityInterface targetCatEnt = catAssociation.getTargetCategoryEntity();
			if ((targetCatEnt != null) && (((CategoryEntity) targetCatEnt).isCreateTable()))
			{
				catEntTblName = targetCatEnt.getTableProperties().getName();

				if (isRecordIdNull)
				{
					final String selectQuery = SELECT_KEYWORD + IDENTIFIER + FROM_KEYWORD
							+ catAssociation.getCategoryEntity().getTableProperties().getName()
							+ WHERE_KEYWORD + RECORD_ID + EQUAL + QUESTION_MARK;
					final LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
					queryDataList.add(new ColumnValueBean(RECORD_ID, recordId));

					final List<Long> identifiers = getResultIDList(selectQuery, IDENTIFIER,
							jdbcDAO, queryDataList);

					if ((identifiers != null) && !identifiers.isEmpty())
					{
						rootCatEntRecId = identifiers.get(0);
					}
				}

				final String tgtFkColName = catAssociation.getConstraintProperties()
						.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties()
						.getName();
				selRecIdQuery = SELECT_KEYWORD + RECORD_ID + FROM_KEYWORD + catEntTblName
						+ WHERE_KEYWORD + tgtFkColName + EQUAL + QUESTION_MARK;

				final LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
				queryDataList.add(new ColumnValueBean(tgtFkColName, rootCatEntRecId));

				final List<Map<BaseAbstractAttributeInterface, Object>> innerRecords = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
				dataValue.put(catAssociation, innerRecords);

				final List<Long> recordIds = getResultIDList(selRecIdQuery, RECORD_ID, jdbcDAO,
						queryDataList);
				for (final Long recId : recordIds)
				{
					final Map<BaseAbstractAttributeInterface, Object> innerRecord = new HashMap<BaseAbstractAttributeInterface, Object>();
					innerRecords.add(innerRecord);

					retrieveRecords(targetCatEnt, innerRecord, recId, jdbcDAO);
				}
			}
		}
	}

	/**
	 * @param entity
	 * @param dataValue
	 * @return
	 */
	private Map<CategoryEntityInterface, Map<BaseAbstractAttributeInterface, Object>> initialiseEntityValueMap(
			final Map<BaseAbstractAttributeInterface, ?> dataValue)
	{
		final Map<CategoryEntityInterface, Map<BaseAbstractAttributeInterface, Object>> catEntRecords = new HashMap<CategoryEntityInterface, Map<BaseAbstractAttributeInterface, Object>>();

		for (Entry<BaseAbstractAttributeInterface, ?> dataValueEntry : dataValue.entrySet())
		{
			CategoryEntityInterface categoryEntity = null;
			BaseAbstractAttributeInterface baseAbstrAttr = dataValueEntry.getKey();
			if (baseAbstrAttr instanceof CategoryAttributeInterface)
			{
				categoryEntity = ((CategoryAttributeInterface) baseAbstrAttr).getCategoryEntity();
			}
			else
			{
				categoryEntity = ((CategoryAssociationInterface) baseAbstrAttr).getCategoryEntity();
			}
			final Object value = dataValueEntry.getValue();

			Map<BaseAbstractAttributeInterface, Object> entDataValues = catEntRecords
					.get(categoryEntity);
			if (entDataValues == null)
			{
				entDataValues = new HashMap<BaseAbstractAttributeInterface, Object>();
				catEntRecords.put(categoryEntity, entDataValues);
			}
			entDataValues.put(baseAbstrAttr, value);
		}

		return catEntRecords;
	}

	/**
	 * @param catEntity
	 * @return
	 */
	private List<CategoryEntityInterface> getParentEntityList(
			final CategoryEntityInterface catEntity)
	{
		// As here the parent category entity whose table is not created is blocked so it is not added in list.
		final List<CategoryEntityInterface> catEntities = new ArrayList<CategoryEntityInterface>();
		catEntities.add(catEntity);

		// Bug # 10265 - modified as per code review comment.
		// Reviewer name - Rajesh Patil
		CategoryEntityInterface categoryEnt = catEntity.getParentCategoryEntity();
		while ((categoryEnt != null) && ((CategoryEntity) categoryEnt).isCreateTable())
		{
			catEntities.add(0, categoryEnt);
			categoryEnt = categoryEnt.getParentCategoryEntity();
		}

		return catEntities;
	}

	/**
	 * This method populates record map for entity which belongs to root category entity.
	 * @param rootCatEntity
	 * @param rootEntRecords
	 * @param attributeValues
	 */
	private void populateRootEntityRecordMap(final CategoryEntityInterface rootCatEntity,
			final Map<AbstractAttributeInterface, Object> rootEntRecords,
			final Map<BaseAbstractAttributeInterface, Object> attributeValues)
	{
		// Set of category data map entries.
		final Set<Entry<BaseAbstractAttributeInterface, Object>> dataMapEntry = attributeValues
				.entrySet();

		AbstractAttributeInterface abstrAttribute = null;
		Object entityValue = null;
		CategoryAttributeInterface catAttribute;
		BaseAbstractAttributeInterface baseAbstrAttr;
		Object categoryValue;

		for (final Entry<BaseAbstractAttributeInterface, Object> entry : dataMapEntry)
		{
			baseAbstrAttr = entry.getKey();
			categoryValue = entry.getValue();
			if (baseAbstrAttr instanceof CategoryAttributeInterface)
			{
				catAttribute = (CategoryAttributeInterface) baseAbstrAttr;
				abstrAttribute = catAttribute.getAbstractAttribute();
				entityValue = categoryValue;

				// Add root cat entity and its parent category entity's attribute.
				for (final CategoryAttributeInterface rootCECatAttr : rootCatEntity
						.getAllCategoryAttributes())
				{
					if ((catAttribute == rootCECatAttr)
							&& ((catAttribute.getIsRelatedAttribute() == null) || !catAttribute
									.getIsRelatedAttribute()))
					{
						rootEntRecords.put(abstrAttribute, entityValue);
					}
				}
			}
		}

		for (final CategoryAssociationInterface catAssociation : rootCatEntity
				.getCategoryAssociationCollection())
		{
			// Add all root category entity's associations.
			for (final AssociationInterface association : catAssociation.getCategoryEntity()
					.getEntity().getAssociationCollection())
			{
				if (!rootEntRecords.containsKey(association))
				{
					rootEntRecords.put(association, new ArrayList());
				}
			}
			// Also add any associations which are related to parent entity's associations
			// i.e. associations between root category entity's parent entity and another class
			// whose category association is created.
			EntityInterface entity = catAssociation.getCategoryEntity().getEntity();
			while (entity.getParentEntity() != null)
			{
				for (final AssociationInterface association : entity.getParentEntity()
						.getAssociationCollection())
				{
					if ((association.getTargetEntity() == catAssociation.getTargetCategoryEntity()
							.getEntity())
							&& !rootEntRecords.containsKey(association))
					{
						rootEntRecords.put(association, new ArrayList());
					}
				}

				entity = entity.getParentEntity();
			}
		}
	}

	/**
	 * @param categoryEnt
	 * @param recordId
	 * @param rlbkQryStack
	 * @param userId
	 * @param jdbcDAO
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 */
	private void clearCategoryEntityData(final CategoryEntityInterface categoryEnt,
			final Long recordId, final Stack<String> rlbkQryStack, final Long userId,
			final JDBCDAO jdbcDao) throws DynamicExtensionsSystemException
	{
		final CategoryEntityInterface catEntity = categoryEnt;

		for (final CategoryAssociationInterface catAssociation : catEntity
				.getCategoryAssociationCollection())
		{
			final String colName = catAssociation.getConstraintProperties()
					.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties()
					.getName();
			if (catAssociation.getTargetCategoryEntity().getChildCategories().size() == 0)
			{
				final String deleteQuery = "DELETE FROM "
						+ catAssociation.getTargetCategoryEntity().getTableProperties().getName()
						+ " WHERE " + colName + EQUAL + QUESTION_MARK;
				final ColumnValueBean bean1 = new ColumnValueBean(colName, recordId);
				final LinkedList<ColumnValueBean> colValBeanList = new LinkedList<ColumnValueBean>();
				colValBeanList.add(bean1);
				DynamicExtensionsUtility.executeUpdateQuery(deleteQuery, userId, jdbcDao,
						colValBeanList);
				rlbkQryStack.push(deleteQuery);
			}
			else
			{
				final String selectQuery = "SELECT IDENTIFIER FROM "
						+ catAssociation.getTargetCategoryEntity().getTableProperties().getName()
						+ " WHERE " + colName + EQUAL + QUESTION_MARK;
				final LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
				queryDataList.add(new ColumnValueBean(colName, recordId));
				final List<Long> recordIds = getResultIDList(selectQuery, IDENTIFIER, jdbcDao,
						queryDataList);
				for (final Long recId : recordIds)
				{
					clearCategoryEntityData(catAssociation.getTargetCategoryEntity(), recId,
							rlbkQryStack, userId, jdbcDao);

					final String deleteQuery = "DELETE FROM "
							+ catAssociation.getTargetCategoryEntity().getTableProperties()
									.getName() + " WHERE IDENTIFIER = ?";
					final ColumnValueBean bean1 = new ColumnValueBean(IDENTIFIER, recId);
					final LinkedList<ColumnValueBean> colValBeanList = new LinkedList<ColumnValueBean>();
					colValBeanList.add(bean1);
					DynamicExtensionsUtility.executeUpdateQuery(deleteQuery, userId, jdbcDao,
							colValBeanList);
					rlbkQryStack.push(deleteQuery);
				}
			}
		}
	}

	/**
	 *
	 */
	@Override
	protected DynamicExtensionBaseQueryBuilder getQueryBuilderInstance()
	{
		return queryBuilder;
	}

	/**
	 * @param rootCatEntity
	 * @param recordId
	 * @return the record id of the hook entity
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 */
	private Long getRootCategoryEntityRecordId(final CategoryEntityInterface rootCatEntity,
			final Long recordId, final JDBCDAO jdbcDao) throws SQLException,
			DynamicExtensionsSystemException
	{
		final StringBuffer query = new StringBuffer(SELECT_KEYWORD);
		query.append(WHITESPACE);
		query.append(RECORD_ID);
		query.append(WHITESPACE);
		query.append(FROM_KEYWORD);
		query.append(WHITESPACE);
		query.append(rootCatEntity.getTableProperties().getName());
		query.append(WHITESPACE);
		query.append(WHERE_KEYWORD);
		query.append(WHITESPACE);
		query.append(IDENTIFIER);
		query.append(EQUAL);
		query.append(QUESTION_MARK);

		Long rootCatEntRecId = null;
		final LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean(IDENTIFIER, recordId));
		final List<Long> results = getResultIDList(query.toString(), RECORD_ID, jdbcDao,
				queryDataList);
		if (!results.isEmpty())
		{
			rootCatEntRecId = results.get(0);
		}

		return rootCatEntRecId;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.entitymanager.CategoryManagerInterface#isPermissibleValuesSubsetValid(edu.common.dynamicextensions.domaininterface.AttributeInterface, java.util.List)
	 */
	public boolean isPermissibleValuesSubsetValid(final UserDefinedDEInterface userDefinedDE,
			final Map<String, Collection<SemanticPropertyInterface>> desiredPVs)
	{
		boolean arePVsValid = true;

		if (userDefinedDE != null)
		{
			final List<Object> attributePVs = new ArrayList<Object>();

			final Collection<PermissibleValueInterface> permissibleValueCollection = userDefinedDE
					.getPermissibleValueCollection();
			for (final PermissibleValueInterface pv : permissibleValueCollection)
			{
				attributePVs.add(pv.getValueAsObject());
			}

			final boolean allDoubleValues = CategoryGenerationUtil
					.isAllPermissibleValuesDouble(permissibleValueCollection);

			final boolean allFloatValues = CategoryGenerationUtil
					.isAllPermissibleValuesFloat(permissibleValueCollection);

			final boolean allIntegerValues = CategoryGenerationUtil
					.isAllPermissibleValuesInteger(permissibleValueCollection);

			final boolean allShortValues = CategoryGenerationUtil
					.isAllPermissibleValuesShort(permissibleValueCollection);

			final boolean allLongValues = CategoryGenerationUtil
					.isAllPermissibleValuesLong(permissibleValueCollection);

			if (allFloatValues && (desiredPVs != null))
			{
				final Set<String> permissibleValues = desiredPVs.keySet();
				for (final String value : permissibleValues)
				{
					if (!attributePVs.contains(Float.parseFloat(value)))
					{
						arePVsValid = false;
					}
				}
			}
			else if (allDoubleValues && (desiredPVs != null))
			{
				final Set<String> permissibleValues = desiredPVs.keySet();
				for (final String value : permissibleValues)
				{
					if (!attributePVs.contains(Double.parseDouble(value)))
					{
						arePVsValid = false;
					}
				}
			}
			else if (allIntegerValues && (desiredPVs != null))
			{
				final Set<String> permissibleValues = desiredPVs.keySet();
				for (final String value : permissibleValues)
				{
					if (!attributePVs.contains(Integer.parseInt(value)))
					{
						arePVsValid = false;
					}
				}
			}
			else if (allShortValues && (desiredPVs != null))
			{
				final Set<String> permissibleValues = desiredPVs.keySet();
				for (final String value : permissibleValues)
				{
					if (!attributePVs.contains(Short.parseShort(value)))
					{
						arePVsValid = false;
					}
				}
			}
			else if (allLongValues && (desiredPVs != null))
			{
				final Set<String> permissibleValues = desiredPVs.keySet();
				for (final String value : permissibleValues)
				{
					if (!attributePVs.contains(Long.parseLong(value)))
					{
						arePVsValid = false;
					}
				}
			}
			else if (desiredPVs != null)
			{
				final Set<String> permissibleValues = desiredPVs.keySet();
				for (final String value : permissibleValues)
				{
					if (!attributePVs.contains(value))
					{
						arePVsValid = false;
					}
				}
			}
		}

		return arePVsValid;
	}

	/**
	 * This method executes a SQL query and returns a list of identifier, record identifier
	 * depending upon column name passed.
	 * @param query
	 * @param columnName
	 * @param queryDataList
	 * @return a list of identifier, record identifier depending upon column name passed.
	 * @throws SQLException
	 */
	private List<Long> getResultIDList(final String query, final String columnName,
			final JDBCDAO jdbcDao, final List<ColumnValueBean> queryDataList)
			throws DynamicExtensionsSystemException
	{
		final List<Long> recordIds = new ArrayList<Long>();
		ResultSet resultSet = null;
		Object value = null;
		try
		{
			resultSet = jdbcDao.getResultSet(query, queryDataList, null);

			while (resultSet.next())
			{
				value = resultSet.getObject(columnName);
				recordIds.add(Long.valueOf(value.toString()));
			}
		}
		catch (final DAOException e)
		{
			throw new DynamicExtensionsSystemException(ErrorConstants.ERROR_EXECUTING_QUERY, e);
		}
		catch (final NumberFormatException e)
		{
			throw new DynamicExtensionsSystemException(ErrorConstants.ERROR_EXECUTING_QUERY, e);
		}
		catch (final SQLException e)
		{
			throw new DynamicExtensionsSystemException(ErrorConstants.ERROR_EXECUTING_QUERY, e);
		}
		finally
		{
			try
			{
				jdbcDao.closeStatement(resultSet);
			}
			catch (final DAOException e)
			{
				throw new DynamicExtensionsSystemException(ErrorConstants.ERROR_EXECUTING_QUERY, e);
			}
		}
		return recordIds;
	}

	/**
	 * getEntityRecordIdByRootCategoryEntityRecordId.
	 * @throws DynamicExtensionsSystemException
	 */
	public Long getEntityRecordIdByRootCategoryEntityRecordId(
			final Long rootCategoryEntityRecordId, final String rootCategoryTableName)
			throws DynamicExtensionsSystemException
	{
		final String query = "select record_Id from " + rootCategoryTableName
				+ " where IDENTIFIER = ? ";
		final LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean(IDENTIFIER, rootCategoryEntityRecordId));
		return getEntityRecordId(query, queryDataList);
	}

	/**
	 * getEntityRecordIdByRootCategoryEntityRecordId.
	 * @throws DynamicExtensionsSystemException
	 */
	public Long getRootCategoryEntityRecordIdByEntityRecordId(
			final Long rootCategoryEntityRecordId, final String rootCategoryTableName)
			throws DynamicExtensionsSystemException
	{
		final String query = "select IDENTIFIER from " + rootCategoryTableName
				+ " where record_Id =?";

		final List<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean(RECORD_ID, rootCategoryEntityRecordId));
		return getEntityRecordId(query, queryDataList);
	}

	/**
	 * This method will found out the record Entry id associated with the deRecordId
	 * which is of Entity & entered for the dynEntContainerId container of the category
	 * of whose static Entity id is recordEntryStaticId.
	 * @param dynEntContainerId category root container Id.
	 * @param deRecordId dynamicRecordId of the Entity from which that category Entity is created.
	 * @param recordEntryStaticId Entity id of the static entity which is hooked to the DE model.
	 * @return recordEntry Id associated with this record.
	 * @exception DynamicExtensionsSystemException Exception.
	 *
	 */
	public long getRecordEntryIdByEntityRecordId(Long dynEntContainerId, Long deRecordId,
			Long recordEntryStaticId) throws DynamicExtensionsSystemException
	{

		EntityInterface entity = EntityManager.getInstance().getCategoryRootEntityByContainerId(
				dynEntContainerId);
		EntityInterface staticEntity = EntityCache.getInstance().getEntityById(recordEntryStaticId);
		AssociationInterface hookAssociation = null;
		for (AssociationInterface association : staticEntity.getAssociationCollection())
		{
			if (association.getTargetEntity().equals(entity))
			{
				hookAssociation = association;
				break;
			}
		}
		if (hookAssociation == null)
		{
			throw new DynamicExtensionsSystemException(entity.getName()
					+ " is not hooked with the given static entity id" + recordEntryStaticId);
		}
		String hookColName = hookAssociation.getConstraintProperties()
				.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().getName();
		String query = "select " + hookColName + " from " + entity.getTableProperties().getName()
				+ " where identifier = ?";
		List<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		queryDataList.add(new ColumnValueBean("identifier", deRecordId));
		return getEntityRecordId(query, queryDataList);
	}

	/**
	 *
	 * @param query
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private Long getEntityRecordId(final String query, final List<ColumnValueBean> queryDataList)
			throws DynamicExtensionsSystemException
	{
		ResultSet resultSet = null;
		Long entityRecordId = null;
		JDBCDAO jdbcDAO = null;
		try
		{
			jdbcDAO = DynamicExtensionsUtility.getJDBCDAO();
			resultSet = jdbcDAO.getResultSet(query, queryDataList, null);
			if (resultSet.next())
			{
				entityRecordId = resultSet.getLong(1);
			}
		}
		catch (final DAOException e)
		{
			throw new DynamicExtensionsSystemException("Exception in query execution", e);
		}
		catch (final SQLException e)
		{
			throw new DynamicExtensionsSystemException("Exception in execution query :: " + query
					+ " identifier :: " + queryDataList.get(0).getColumnValue(), e);
		}
		finally
		{
			try
			{
				jdbcDAO.closeStatement(resultSet);
				DynamicExtensionsUtility.closeDAO(jdbcDAO);
			}
			catch (final DAOException e)
			{
				throw new DynamicExtensionsSystemException("Exception in query execution", e);
			}
		}
		return entityRecordId;
	}

	private LinkedList<ColumnValueBean> createColumnValueBeanListForDataEntry(
			final long identifier, final long recordId)
	{
		final ColumnValueBean bean1 = new ColumnValueBean(IDENTIFIER, identifier);
		final ColumnValueBean bean2 = new ColumnValueBean("ACTIVITY_STATUS", "ACTIVE");
		final ColumnValueBean bean3 = new ColumnValueBean(RECORD_ID, recordId);
		final LinkedList<ColumnValueBean> colValBeanList = new LinkedList<ColumnValueBean>();
		colValBeanList.add(bean1);
		colValBeanList.add(bean2);
		colValBeanList.add(bean3);
		return colValBeanList;
	}

	/**
	 *
	 * @param dataValue
	 * @return
	 */
	private Map<AbstractAttributeInterface, Object> createAttributeMap(
			final Map<BaseAbstractAttributeInterface, Object> dataValue)
	{
		final Map<AbstractAttributeInterface, Object> attributes = new HashMap<AbstractAttributeInterface, Object>();

		final Iterator<Map.Entry<BaseAbstractAttributeInterface, Object>> mapEntrySetIter = dataValue
				.entrySet().iterator();
		while (mapEntrySetIter.hasNext())
		{
			final Entry<BaseAbstractAttributeInterface, Object> nextObject = mapEntrySetIter.next();
			final BaseAbstractAttributeInterface baseAbstractAttr = nextObject.getKey();
			if (baseAbstractAttr instanceof CategoryAttributeInterface)
			{
				attributes.put(((CategoryAttributeInterface) baseAbstractAttr)
						.getAbstractAttribute(), nextObject.getValue());
			}
		}

		return attributes;
	}

	/**
	 * It will fetch all the categories present
	 * @return will return the collection of categories.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public Collection<CategoryInterface> getAllCategories()
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		return getAllObjects(CategoryInterface.class.getName());
	}

	/**
	 *
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public Collection<CategoryAttributeInterface> getAllCalculatedCategoryAttributes()
			throws DynamicExtensionsSystemException
	{
		final Collection<CategoryAttributeInterface> categoryAttributes = executeHQL(
				"getAllCalculatedCategoryAttributes", new HashMap());
		return categoryAttributes;
	}

	/**
	 *
	 * @param categoryAttributeCollection
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public Collection<CategoryAttributeInterface> updateCategoryAttributes(
			final Collection<CategoryAttributeInterface> categoryAttributeCollection)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		HibernateDAO hibernateDAO = null;
		try
		{
			hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();
			for (final CategoryAttributeInterface categoryAttributeInterface : categoryAttributeCollection)
			{
				hibernateDAO.update(categoryAttributeInterface);
			}
			hibernateDAO.commit();
		}
		catch (final DAOException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e, DYEXTN_S_003);
		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(hibernateDAO);
		}
		return categoryAttributeCollection;
	}
}