/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author
 *@version 1.0
 */

package edu.common.dynamicextensions.xmi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domain.Attribute;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domain.userinterface.Container;
import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.operations.DatabaseOperations;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationDirection;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

/**
 * @author falguni_sachde
 *
 */
public class AnnotationUtil
{

	/** The Constant FIRST_ENTITY_ID. */
	private static final String FIRST_ENTITY_ID = "FIRST_ENTITY_ID";

	/** The Constant LAST_ENTITY_ID. */
	private static final String LAST_ENTITY_ID = "LAST_ENTITY_ID";

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	private static final transient Logger LOGGER = Logger.getCommonLogger(AnnotationUtil.class);

	//private static org.apache.log4j.Logger logger = Logger.getLogger(AnnotationUtil.class);

	private static Long hookEntityId;
	private static Map<Long, List<EntityInterface>> entyVsChldEnts = new HashMap<Long, List<EntityInterface>>();
	private static boolean isMapPopulated = false;
	private static String pathInsertStatement = "insert into PATH "
			+ "(PATH_ID, FIRST_ENTITY_ID,INTERMEDIATE_PATH, LAST_ENTITY_ID)" + " values (?,?,?,?)";

	/**
	 * It will add the Association between the staticEntityId & dynamicEntityId.
	 * @param staticEntityId association's source entity ID
	 * @param dynamicEntityId association's destination entity ID
	 * @param isEntityFromXmi is it from model or static model
	 * @return association id
	 * @throws DynamicExtensionsSystemException exception
	 * @throws BizLogicException exception
	 * @throws DynamicExtensionsApplicationException exception
	 */
	public static synchronized Long addAssociation(Long staticEntityId, Long dynamicEntityId,
			boolean isEntityFromXmi) throws DynamicExtensionsSystemException, BizLogicException,
			DynamicExtensionsApplicationException
	{

		AssociationInterface association = null;
		HibernateDAO dao = null;
		EntityInterface staticEntity = null;
		EntityInterface dynamicEntity = null;
		JDBCDAO jdbcdao = null;
		try
		{
			dao = DynamicExtensionsUtility.getHibernateDAO();
			staticEntity = (EntityInterface) dao.retrieveById(Entity.class.getName(),
					staticEntityId);
			dynamicEntity = (EntityInterface) ((Container) dao.retrieveById(Container.class
					.getName(), dynamicEntityId)).getAbstractEntity();

			association = addAssociationForEntities(staticEntity, dynamicEntity);

			staticEntity = EntityManager.getInstance().persistEntityMetadataForAnnotation(
					staticEntity, true, false, association, dao);

			//Add the column related to the association to the entity table of the associated entities.
			EntityManager.getInstance().addAssociationColumn(association);

			jdbcdao = DynamicExtensionsUtility.getJDBCDAO();
			addQueryPaths(staticEntity, dynamicEntity, isEntityFromXmi, association.getId(),
					jdbcdao, staticEntity);
			jdbcdao.commit();
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException(
					"DAOException occured While adding the Association Column.", e);

		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(dao);
			DynamicExtensionsUtility.closeDAO(jdbcdao);

		}

		return association.getId();
	}

	/**
	 *
	 * @param staticEntity
	 * @param dynamicEntity
	 * @param isEntityFromXmi
	 * @param associationId
	 * @param jdbcdao
	 * @param hookEntity
	 * @throws DynamicExtensionsSystemException exception
	 * @throws DynamicExtensionsApplicationException exception
	 * @throws BizLogicException exception
	 */
	public static void addQueryPaths(EntityInterface staticEntity, EntityInterface dynamicEntity,
			boolean isEntityFromXmi, Long associationId, JDBCDAO jdbcdao, EntityInterface hookEntity)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			BizLogicException
	{
		Set<PathObject> processedPathList = new HashSet<PathObject>();
		if (!isMapPopulated || !isEntityFromXmi)
		{
			EntityGroupInterface entityGroup = dynamicEntity.getEntityGroup();
			populateEntityVsChildEntitiesMap(entityGroup);
			//Retrieving the RecordEntry identifier
			setHookEntityId(hookEntity);
		}

		if (associationId == null)
		{
			Collection<Long> associationIds = EntityManager.getInstance().getAssociationIds(
					staticEntity.getId(), dynamicEntity.getId());
			for (Long dbAssociationId : associationIds)
			{
				addQueryPathsForAllAssociatedEntities(dynamicEntity, staticEntity, dbAssociationId,
						processedPathList, false, jdbcdao);
			}
		}
		else
		{
			addQueryPathsForAllAssociatedEntities(dynamicEntity, staticEntity, associationId,
					processedPathList, false, jdbcdao);
		}

	}

	/**
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @param isEntityFromXmi
	 * @param hookEntity
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 * @throws BizLogicException
	 */
	public static synchronized Long addNewPathsForExistingMainContainers(
			EntityInterface staticEntity, EntityInterface dynamicEntity, boolean isEntityFromXmi,
			JDBCDAO jdbcdao, EntityInterface hookEntity)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException,
			BizLogicException
	{
		AssociationInterface association = null;
		association = getAssociationForEntity(staticEntity, dynamicEntity);
		addQueryPaths(staticEntity, dynamicEntity, isEntityFromXmi, association.getId(), jdbcdao,
				hookEntity);
		return association.getId();
	}

	/**
	 * getAssociationForEntity.
	 * @param staticEntity
	 * @param dynamicEntity
	 * @return
	 */
	public static AssociationInterface getAssociationForEntity(EntityInterface staticEntity,
			AbstractEntityInterface dynamicEntity)
	{
		Collection<AssociationInterface> associationCollection = staticEntity
				.getAssociationCollection();
		AssociationInterface association = null;
		for (AssociationInterface associationInteface : associationCollection)
		{
			if (associationInteface.getTargetEntity() != null
					&& associationInteface.getTargetEntity().equals(dynamicEntity))
			{
				association = associationInteface;
				break;
			}
		}
		return association;
	}

	/**
	 *
	 * @param staticEntity
	 * @param dynamicEntity
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public static synchronized AssociationInterface addAssociationForEntities(
			EntityInterface staticEntity, EntityInterface dynamicEntity)
			throws DynamicExtensionsSystemException
	{
		AssociationInterface association = null;

		//Create source role and target role for the association
		String roleName = staticEntity.getId().toString().concat("_").concat(
				dynamicEntity.getId().toString());
		RoleInterface sourceRole = getRole(AssociationType.CONTAINTMENT, roleName,
				Cardinality.ZERO, Cardinality.ONE);
		RoleInterface targetRole = getRole(AssociationType.CONTAINTMENT, roleName,
				Cardinality.ZERO, Cardinality.MANY);

		//Create association with the created source and target roles.
		association = getAssociation(dynamicEntity, AssociationDirection.SRC_DESTINATION, roleName,
				sourceRole, targetRole);

		//Create constraint properties for the created association.
		ConstraintPropertiesInterface constProperts = getConstraintProperties(staticEntity,
				dynamicEntity);
		association.setConstraintProperties(constProperts);

		//Add association to the static entity and save it.
		staticEntity.addAssociation(association);

		return association;

	}

	/**
	 * This method populates the map which contains an entity as key and all its child entities as value
	 * @param entityGroup
	 */
	private static void populateEntityVsChildEntitiesMap(EntityGroupInterface entityGroup)
	{
		Collection<EntityInterface> entityCollection = entityGroup.getEntityCollection();

		for (EntityInterface entity : entityCollection)
		{
			if (entity.getParentEntity() != null)
			{
				List<EntityInterface> childEntites = null;
				if (entyVsChldEnts.get(entity.getParentEntity().getId()) == null)
				{
					childEntites = new ArrayList<EntityInterface>();
				}
				else
				{
					childEntites = entyVsChldEnts.get(entity.getParentEntity().getId());
				}
				if (!childEntites.contains(entity))
				{
					childEntites.add(entity);
				}
				entyVsChldEnts.put(entity.getParentEntity().getId(), childEntites);
			}
		}
		isMapPopulated = true;
	}

	/**
	 * @param dynamicEntity
	 * @param staticEntity
	 * @param associationId
	 * @param processedPathList
	 * @param isEntGrpSysGnrted
	 * @param jdbcdao
	 * @throws BizLogicException exception
	 * @throws DynamicExtensionsSystemException exception
	 */
	public static void addQueryPathsForAllAssociatedEntities(EntityInterface dynamicEntity,
			EntityInterface staticEntity, Long associationId, Set<PathObject> processedPathList,
			boolean isEntGrpSysGnrted, JDBCDAO jdbcdao) throws BizLogicException,
			DynamicExtensionsSystemException
	{
		if (staticEntity != null)
		{
			PathObject pathObject = new PathObject();
			pathObject.setSourceEntity(staticEntity);
			pathObject.setTargetEntity(dynamicEntity);

			if (processedPathList.contains(pathObject))
			{
				return;
			}
			else
			{
				processedPathList.add(pathObject);
			}

			boolean ispathAdded = isDirectPathAdded(staticEntity.getId(), dynamicEntity.getId(),
					jdbcdao);
			if (!ispathAdded)
			{
				LOGGER.info(" " + staticEntity.getName() + " --> " + dynamicEntity.getName());
				AnnotationUtil.addPathsForQuery(staticEntity.getId(), dynamicEntity, associationId,
						isEntGrpSysGnrted, jdbcdao);
			}
		}
		Collection<AssociationInterface> associations = dynamicEntity.getAssociationCollection();
		for (AssociationInterface association : associations)
		{
			addQueryPathsForAllAssociatedEntities(association.getTargetEntity(), dynamicEntity,
					association.getId(), processedPathList, isEntGrpSysGnrted, jdbcdao);
		}
	}

	/**
	 * @param dynamicEntity
	 * @throws DynamicExtensionsSystemException
	 */
	public static void addInheritancePathforSystemGenerated(EntityInterface dynamicEntity)
			throws DynamicExtensionsSystemException
	{
		Long maxPathId = getMaxId("path_id", "path");
		maxPathId += 1;
		addInheritancePaths(maxPathId, dynamicEntity);
	}

	/**
	 *
	 * @param staticEntityId staticEntityId
	 * @param dynamicEntityId dynamicEntityId
	 * @param jdbcdao jdbcdao
	 * @return
	 * @throws DynamicExtensionsSystemException exception
	 */
	public static boolean isPathAdded(Long staticEntityId, Long dynamicEntityId, JDBCDAO jdbcdao)
			throws DynamicExtensionsSystemException
	{
		boolean ispathAdded = false; // NOPMD - DD anomaly

		try
		{
			String checkForPathQuery = "select path_id from path where "
					+ "FIRST_ENTITY_ID = ? and LAST_ENTITY_ID = ?";
			LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
			queryDataList.add(new ColumnValueBean(FIRST_ENTITY_ID, staticEntityId));
			queryDataList.add(new ColumnValueBean(LAST_ENTITY_ID, dynamicEntityId));
			ispathAdded = isQueryReturnsResults(jdbcdao, checkForPathQuery, queryDataList);
		}
		catch (SQLException e)
		{
			throw new DynamicExtensionsSystemException("SQL Exception while adding paths.", e);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("DAOException in adding paths.", e);
		}

		return ispathAdded;
	}

	private static boolean isQueryReturnsResults(JDBCDAO jdbcdao, String checkForPathQuery,
			List<ColumnValueBean> queryDataList) throws DAOException, SQLException
	{
		boolean ispathAdded = false;
		ResultSet resultSet = null; // NOPMD - DD anomaly
		resultSet = jdbcdao.getResultSet(checkForPathQuery, queryDataList, null);
		if (resultSet != null)
		{
			while (resultSet.next())
			{
				ispathAdded = true;
				break;
			}
		}
		if (resultSet != null)
		{
			jdbcdao.closeStatement(resultSet);
		}
		return ispathAdded;
	}

	/**
	 * This method will check about Direct path between two entities.
	 * if intermediate paths does not concatenated then it will return true
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @param jdbcdao
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public static boolean isDirectPathAdded(Long staticEntityId, Long dynamicEntityId,
			JDBCDAO jdbcdao) throws DynamicExtensionsSystemException
	{
		boolean ispathAdded = false; // NOPMD - DD anomaly
		try
		{
			String checkForPathQuery = "select path_id from path where "
					+ "FIRST_ENTITY_ID = ? and LAST_ENTITY_ID = ? "
					+ "and intermediate_path  NOT like ?";
			LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
			queryDataList.add(new ColumnValueBean(FIRST_ENTITY_ID, staticEntityId));
			queryDataList.add(new ColumnValueBean(LAST_ENTITY_ID, dynamicEntityId));
			queryDataList.add(new ColumnValueBean("intermediate_path", "%\\_%"));
			ispathAdded = isQueryReturnsResults(jdbcdao, checkForPathQuery, queryDataList);
		}
		catch (SQLException e)
		{
			throw new DynamicExtensionsSystemException("SQL Exception while adding paths.", e);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException("DAOException in adding paths.", e);
		}

		return ispathAdded;
	}

	/**
	 * @param staticEntity
	 * @param dynamicEntity
	 * @return
	 */
	public static ConstraintPropertiesInterface getConstraintProperties(
			EntityInterface staticEntity, EntityInterface dynamicEntity)
	{
		ConstraintPropertiesInterface constprop = DomainObjectFactory.getInstance()
				.createConstraintProperties();
		constprop.setName(dynamicEntity.getTableProperties().getName());
		for (AttributeInterface attribute : staticEntity.getPrimaryKeyAttributeCollection())
		{
			constprop.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties()
					.setName(
							"DYEXTN_AS_" + staticEntity.getId().toString() + "_"
									+ dynamicEntity.getId().toString());

			constprop.getTgtEntityConstraintKeyProperties().setSrcPrimaryKeyAttribute(attribute);

		}
		constprop.getSrcEntityConstraintKeyPropertiesCollection().clear();
		return constprop;
	}

	/**
	 * @param staticEntity
	 * @param dynamicEntity
	 * @return
	 */
	public static ConstraintPropertiesInterface getDummyConstraintProperties(
			EntityInterface staticEntity, EntityInterface dynamicEntity)
	{
		ConstraintPropertiesInterface constprop = DomainObjectFactory.getInstance()
				.createConstraintProperties();
		constprop.setName(dynamicEntity.getTableProperties().getName());
		for (AttributeInterface attribute : staticEntity.getPrimaryKeyAttributeCollection())
		{
			constprop.getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties()
					.setName(
							"DYEXTN_AS_" + staticEntity.getId().toString() + "_"
									+ dynamicEntity.getId().toString());
			Attribute tempAttribute = new Attribute();
			tempAttribute.setId(attribute.getId());
			constprop.getTgtEntityConstraintKeyProperties().setSrcPrimaryKeyAttribute(tempAttribute);

		}
		constprop.getSrcEntityConstraintKeyPropertiesCollection().clear();
		return constprop;
	}
	/**
	 * @param targetEntity
	 * @param assonDirectn
	 * @param assoName
	 * @param sourceRole
	 * @param targetRole
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private static AssociationInterface getAssociation(EntityInterface targetEntity,
			AssociationDirection assonDirectn, String assoName, RoleInterface sourceRole,
			RoleInterface targetRole) throws DynamicExtensionsSystemException
	{
		AssociationInterface association = DomainObjectFactory.getInstance().createAssociation();
		association.setTargetEntity(targetEntity);
		association.setAssociationDirection(assonDirectn);
		association.setName(assoName);
		association.setSourceRole(sourceRole);
		association.setTargetRole(targetRole);
		return association;
	}

	/**
	 * @param associationType associationType
	 * @param name name
	 * @param minCard  minCard
	 * @param maxCard maxCard
	 * @return  RoleInterface
	 */
	private static RoleInterface getRole(AssociationType associationType, String name,
			Cardinality minCard, Cardinality maxCard)
	{
		RoleInterface role = DomainObjectFactory.getInstance().createRole();
		role.setAssociationsType(associationType);
		role.setName(name);
		role.setMinimumCardinality(minCard);
		role.setMaximumCardinality(maxCard);
		return role;
	}

	/**
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @param deAssociationID
	 * @param isEntGrpSysGenrtd
	 * @param jdbcdao
	 * @throws DynamicExtensionsSystemException
	 */
	public static void addPathsForQuery(Long staticEntityId, EntityInterface dynamicEntity,
			Long deAssociationID, boolean isEntGrpSysGenrtd, JDBCDAO jdbcdao)
			throws DynamicExtensionsSystemException
	{

		try
		{
			Long maxPathId = getMaxId("path_id", "path");
			maxPathId += 1;
			insertNewPaths(maxPathId, staticEntityId, dynamicEntity, deAssociationID,
					isEntGrpSysGenrtd, jdbcdao);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Exception occured while adding the Query paths", e);
		}
	}

	/**
	 * @param maxPathId
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @param deAssociationID
	 * @param isEntGrpSysGenrtd
	 * @throws DynamicExtensionsSystemException
	 */
	private static void insertNewPaths(Long maxPathId, Long staticEntityId,
			EntityInterface dynamicEntity, Long deAssociationID, boolean isEntGrpSysGenrtd,
			JDBCDAO jdbcdao) throws DynamicExtensionsSystemException, DAOException
	{
		Long intraModAssonId = getMaxId("ASSOCIATION_ID", "ASSOCIATION");
		intraModAssonId += 1;
		List<Map<String, LinkedList<ColumnValueBean>>> queryList = new ArrayList<Map<String, LinkedList<ColumnValueBean>>>();

		Map<String, LinkedList<ColumnValueBean>> assnQueryVsDataList = new HashMap<String, LinkedList<ColumnValueBean>>();
		String associationQuery = "insert into ASSOCIATION (ASSOCIATION_ID, "
				+ "ASSOCIATION_TYPE) values (?,?)";
		LinkedList<ColumnValueBean> assnQueryColValBeanList = new LinkedList<ColumnValueBean>();
		assnQueryColValBeanList.add(new ColumnValueBean("ASSOCIATION_ID", intraModAssonId));
		assnQueryColValBeanList.add(new ColumnValueBean("ASSOCIATION_TYPE", Integer
				.valueOf(edu.wustl.cab2b.server.path.AssociationType.INTRA_MODEL_ASSOCIATION
						.getValue())));
		assnQueryVsDataList.put(associationQuery, assnQueryColValBeanList);
		queryList.add(assnQueryVsDataList);

		Map<String, LinkedList<ColumnValueBean>> intraQueryVsDataList = new HashMap<String, LinkedList<ColumnValueBean>>();
		String intraModelQuery = "insert into INTRA_MODEL_ASSOCIATION (ASSOCIATION_ID, "
				+ "DE_ASSOCIATION_ID) values (?,?)";
		LinkedList<ColumnValueBean> intraQueryColValueBeanList = new LinkedList<ColumnValueBean>();
		intraQueryColValueBeanList.add(new ColumnValueBean("ASSOCIATION_ID", intraModAssonId));
		intraQueryColValueBeanList.add(new ColumnValueBean("DE_ASSOCIATION_ID", deAssociationID));
		intraQueryVsDataList.put(intraModelQuery, intraQueryColValueBeanList);
		queryList.add(intraQueryVsDataList);

		Map<String, LinkedList<ColumnValueBean>> pathQueryVsDataList = new HashMap<String, LinkedList<ColumnValueBean>>();
		LinkedList<ColumnValueBean> pathQueryColValBeanList = getcolumnvalueBeanListForPathQuery(
				maxPathId, staticEntityId, intraModAssonId.toString(), dynamicEntity.getId());
		pathQueryVsDataList.put(pathInsertStatement, pathQueryColValBeanList);
		queryList.add(pathQueryVsDataList);
		//adding paths for derived entities of static entity
		maxPathId = addChildPathsStaticEntity(maxPathId, staticEntityId, dynamicEntity.getId(),
				intraModAssonId.toString(), queryList, jdbcdao);

		//adding paths for derived entities of dynamic entity
		maxPathId = addChildPathsDynamicEntity(maxPathId, staticEntityId, dynamicEntity.getId(),
				intraModAssonId.toString(), queryList, jdbcdao);

		DatabaseOperations.executeDML(queryList);
		maxPathId += 1;
		if (!isEntGrpSysGenrtd)
		{
			maxPathId = addIndirectPaths(maxPathId, staticEntityId, dynamicEntity.getId(),
					intraModAssonId, jdbcdao);
		}

		addInheritancePaths(maxPathId, dynamicEntity);
	}

	/**
	 *
	 * @param maxPathId maxPathId
	 * @param staticEntityId staticEntityId
	 * @param intraModAssonId intraModAssonId
	 * @param dynamicEntityId dynamicEntityId
	 * @return pathQueryColValBeanList
	 */
	public static LinkedList<ColumnValueBean> getcolumnvalueBeanListForPathQuery(Long maxPathId,
			Long staticEntityId, String intraModAssonId, Long dynamicEntityId)
	{
		LinkedList<ColumnValueBean> pathQueryColValBeanList = new LinkedList<ColumnValueBean>();
		pathQueryColValBeanList.add(new ColumnValueBean("PATH_ID", maxPathId));
		pathQueryColValBeanList.add(new ColumnValueBean(FIRST_ENTITY_ID, staticEntityId));
		pathQueryColValBeanList.add(new ColumnValueBean("INTERMEDIATE_PATH", intraModAssonId));
		pathQueryColValBeanList.add(new ColumnValueBean(LAST_ENTITY_ID, dynamicEntityId));
		return pathQueryColValBeanList;
	}

	/**
	 * This method adds paths from derived entities of static entity to derived entities of dynamic entity
	 * @param maxPathId
	 * @param staticEntityId
	 * @param dynChldEntityId
	 * @param intraModAssonId
	 * @param queryList
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private static Long addPathsChildStaticChildDynamic(Long maxPathId, Long staticEntityId,
			Long dynChldEntityId, String intraModAssonId,
			List<Map<String, LinkedList<ColumnValueBean>>> queryList, JDBCDAO jdbcdao)
			throws DynamicExtensionsSystemException
	{
		Collection<EntityInterface> statChldEntes = entyVsChldEnts.get(staticEntityId);
		if (statChldEntes != null)
		{
			for (EntityInterface staticChildEntity : statChldEntes)
			{
				boolean ispathAdded = isPathAdded(staticChildEntity.getId(), dynChldEntityId,
						jdbcdao);
				if (!ispathAdded)
				{
					maxPathId = maxPathId + 1;
					Map<String, LinkedList<ColumnValueBean>> pathQueryVsDataList = new HashMap<String, LinkedList<ColumnValueBean>>();
					LinkedList<ColumnValueBean> pathQueryColValBeanList = getcolumnvalueBeanListForPathQuery(
							maxPathId, staticChildEntity.getId(), intraModAssonId, dynChldEntityId);
					pathQueryVsDataList.put(pathInsertStatement, pathQueryColValBeanList);
					queryList.add(pathQueryVsDataList);
				}
				maxPathId = addPathsChildStaticChildDynamic(maxPathId, staticChildEntity.getId(),
						dynChldEntityId, intraModAssonId, queryList, jdbcdao);

			}
		}
		return maxPathId;
	}

	/**
	 * This method adds paths for derived entities of dynamic entity
	 * @param maxPathId
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @param intraModlAssonId
	 * @param queryList
	 * @return maxPathId
	 * @throws DynamicExtensionsSystemException
	 */
	private static Long addChildPathsDynamicEntity(Long maxPathId, Long staticEntityId,
			Long dynamicEntityId, String intraModlAssonId,
			List<Map<String, LinkedList<ColumnValueBean>>> queryList, JDBCDAO jdbcdao)
			throws DynamicExtensionsSystemException
	{
		if (hookEntityId != null && staticEntityId.compareTo(hookEntityId) != 0)
		{
			Collection<EntityInterface> childEntities = entyVsChldEnts.get(dynamicEntityId);
			//boolean ispathAdded = false;
			if (childEntities != null)
			{
				for (EntityInterface childEntity : childEntities)
				{
					boolean ispathAdded = isPathAdded(staticEntityId, childEntity.getId(), jdbcdao);
					if (!ispathAdded)
					{
						maxPathId = maxPathId + 1;
						Map<String, LinkedList<ColumnValueBean>> pathQueryVsDataList = new HashMap<String, LinkedList<ColumnValueBean>>();
						LinkedList<ColumnValueBean> pathQueryColValBeanList = getcolumnvalueBeanListForPathQuery(
								maxPathId, staticEntityId, intraModlAssonId, childEntity.getId());
						pathQueryVsDataList.put(pathInsertStatement, pathQueryColValBeanList);
						queryList.add(pathQueryVsDataList);
					}
					// add paths from derived entities of static entity to derived entities of dynamic entity
					maxPathId = addPathsChildStaticChildDynamic(maxPathId, staticEntityId,
							childEntity.getId(), intraModlAssonId, queryList, jdbcdao);

					maxPathId = addChildPathsDynamicEntity(maxPathId, staticEntityId, childEntity
							.getId(), intraModlAssonId, queryList, jdbcdao);
				}
			}
		}

		return maxPathId;
	}

	/**
	 * This method adds paths for derived entities of static entity
	 * @param maxPathId
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @param intrModAssnId
	 * @param queryList
	 * @return maxPathId
	 * @throws DynamicExtensionsSystemException
	 */
	private static Long addChildPathsStaticEntity(Long maxPathId, Long staticEntityId,
			Long dynamicEntityId, String intrModAssnId,
			List<Map<String, LinkedList<ColumnValueBean>>> queryList, JDBCDAO jdbcdao)
			throws DynamicExtensionsSystemException
	{

		Collection<EntityInterface> childEntities = entyVsChldEnts.get(staticEntityId);
		//boolean ispathAdded = false;
		if (childEntities != null)
		{
			for (EntityInterface childEntity : childEntities)
			{
				boolean ispathAdded = isPathAdded(childEntity.getId(), dynamicEntityId, jdbcdao);
				if (!ispathAdded)
				{
					maxPathId++;
					Map<String, LinkedList<ColumnValueBean>> pathQueryVsDataList = new HashMap<String, LinkedList<ColumnValueBean>>();
					LinkedList<ColumnValueBean> pathQueryColValBeanList = getcolumnvalueBeanListForPathQuery(
							maxPathId, childEntity.getId(), intrModAssnId, dynamicEntityId);
					pathQueryVsDataList.put(pathInsertStatement, pathQueryColValBeanList);
					queryList.add(pathQueryVsDataList);
				}
				maxPathId = addChildPathsStaticEntity(maxPathId, childEntity.getId(),
						dynamicEntityId, intrModAssnId, queryList, jdbcdao);
			}
		}
		return maxPathId;
	}

	/**
	 * This method replicates paths of parent entity for derived entity
	 * @param maxPathId
	 * @param dynamicEntityId
	 * @param conn
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private static void addInheritancePaths(Long maxPathId, EntityInterface entity)
			throws DynamicExtensionsSystemException
	{
		ResultSet resultSet = null; // NOPMD - DD anomaly
		try
		{
			//This map is added because the following algo creates multiple paths between same entities
			//The map will contains only single unique path between entities
			Map<String, Object> mapQuery = new HashMap<String, Object>();
			List<Map<String, LinkedList<ColumnValueBean>>> queryList = new ArrayList<Map<String, LinkedList<ColumnValueBean>>>();
			String sql = "";
			String intermediatePath = "";
			Long last_entity_id = null;
			Long first_entity_id = null;
			boolean ispathAdded = false;
			JDBCDAO jdbcdao = null;

			while (entity.getParentEntity() != null)
			{
				try
				{
					jdbcdao = DynamicExtensionsUtility.getJDBCDAO();

					//replicate outgoing paths of parent entity (outgoing associations)
					Collection<AssociationInterface> allAssociations = entity.getParentEntity()
							.getAllAssociations();

					for (AssociationInterface association : allAssociations)
					{
						intermediatePath = "";
						sql = "select INTERMEDIATE_PATH,LAST_ENTITY_ID "
								+ "from path where FIRST_ENTITY_ID=?";
						LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
						queryDataList.add(new ColumnValueBean(FIRST_ENTITY_ID, association
								.getEntity().getId()));
						resultSet = jdbcdao.getResultSet(sql, queryDataList, null);
						List<ArrayList<String>> idlist = new ArrayList<ArrayList<String>>();
						while (resultSet.next())
						{
							ArrayList<String> temp = new ArrayList<String>();
							temp.add(0, resultSet.getString(1));
							temp.add(1, Long.toString(resultSet.getLong(2)));
							idlist.add(temp);
						}
						jdbcdao.closeStatement(resultSet);
						for (int cnt = 0; cnt < idlist.size(); cnt++)
						{
							ArrayList<String> temp = idlist.get(cnt);
							intermediatePath = temp.get(0);
							last_entity_id = Long.valueOf(temp.get(1));
							ispathAdded = isPathAdded(entity.getId(), last_entity_id, jdbcdao);
							if (!ispathAdded)
							{
								String uniquepathStr = entity.getId() + "_" + intermediatePath
										+ "_" + last_entity_id;
								if (!mapQuery.containsKey(uniquepathStr))
								{
									Map<String, LinkedList<ColumnValueBean>> queryVsDataList = new HashMap<String, LinkedList<ColumnValueBean>>();
									LinkedList<ColumnValueBean> pathQueryColValBeanList = getcolumnvalueBeanListForPathQuery(
											maxPathId, entity.getId(), intermediatePath,
											last_entity_id);
									mapQuery.put(uniquepathStr, null);
									queryVsDataList.put(pathInsertStatement,
											pathQueryColValBeanList);
									queryList.add(queryVsDataList);
									maxPathId++;
								}
							}
						}

					}

					// replicate incoming paths of parent entity (incoming associations)
					intermediatePath = "";
					sql = "select FIRST_ENTITY_ID,INTERMEDIATE_PATH "
							+ "from path where LAST_ENTITY_ID=?";

					LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
					queryDataList.add(new ColumnValueBean(LAST_ENTITY_ID, entity.getParentEntity()
							.getId()));
					resultSet = jdbcdao.getResultSet(sql, queryDataList, null);
					List<ArrayList<String>> idlist = new ArrayList<ArrayList<String>>();
					while (resultSet.next())
					{

						ArrayList<String> temp = new ArrayList<String>();
						temp.add(0, Long.toString(resultSet.getLong(1)));
						temp.add(1, resultSet.getString(2));
						idlist.add(temp);
					}
					jdbcdao.closeStatement(resultSet);
					for (int cnt = 0; cnt < idlist.size(); cnt++)
					{
						ArrayList<String> temp = idlist.get(cnt);
						first_entity_id = Long.valueOf(temp.get(0));
						intermediatePath = temp.get(1);
						if (first_entity_id.compareTo(hookEntityId) != 0)
						{

							ispathAdded = isPathAdded(first_entity_id, entity.getId(), jdbcdao);
							if (!ispathAdded)
							{
								Map<String, LinkedList<ColumnValueBean>> queryVsDataList = new HashMap<String, LinkedList<ColumnValueBean>>();
								LinkedList<ColumnValueBean> pathQueryColValBeanList = getcolumnvalueBeanListForPathQuery(
										maxPathId, first_entity_id, intermediatePath, entity
												.getId());
								String uniquepathStr = first_entity_id + "_" + intermediatePath
										+ "_" + entity.getId();

								if (!mapQuery.containsKey(uniquepathStr))
								{
									mapQuery.put(uniquepathStr, null);
									queryVsDataList.put(pathInsertStatement,
											pathQueryColValBeanList);
									queryList.add(queryVsDataList);
									maxPathId++;
								}
							}
						}
					}
				}
				finally
				{

					DynamicExtensionsUtility.closeDAO(jdbcdao);
				}
				entity = entity.getParentEntity();

			}//while

			DatabaseOperations.executeDML(queryList);

		}
		catch (SQLException e)
		{
			throw new DynamicExtensionsSystemException(
					"SQL Exception while adding paths for derived entity.", e);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException(
					"DAO Exception while adding paths for derived entity.", e);
		}
	}

	/**
	 * @param maxPathId
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @param intrModAssonId
	 * @param conn
	 * @throws DynamicExtensionsSystemException
	 */
	private static Long addIndirectPaths(Long maxPathId, Long staticEntityId, Long dynamicEntityId,
			Long intrModAssonId, JDBCDAO jdbcdao) throws DynamicExtensionsSystemException

	{
		ResultSet resultSet = null; // NOPMD - DD anomaly

		try
		{

			if (hookEntityId != null && staticEntityId.compareTo(hookEntityId) != 0)
			{
				List<Map<String, LinkedList<ColumnValueBean>>> queryList = new ArrayList<Map<String, LinkedList<ColumnValueBean>>>();
				String query = "select FIRST_ENTITY_ID,INTERMEDIATE_PATH from "
						+ "path where LAST_ENTITY_ID=?";

				LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
				queryDataList.add(new ColumnValueBean(LAST_ENTITY_ID, staticEntityId));
				resultSet = jdbcdao.getResultSet(query, queryDataList, null);
				while (resultSet.next())
				{

					Long firstEntityId = resultSet.getLong(1);
					String path = resultSet.getString(2);
					path = path.concat("_").concat(intrModAssonId.toString());

					LinkedList<ColumnValueBean> pathQueryColValBeanList = getcolumnvalueBeanListForPathQuery(
							maxPathId, firstEntityId, path, dynamicEntityId);
					Map<String, LinkedList<ColumnValueBean>> queryVsDataList = new HashMap<String, LinkedList<ColumnValueBean>>();
					queryVsDataList.put(pathInsertStatement, pathQueryColValBeanList);
					queryList.add(queryVsDataList);
					maxPathId = addChildPathsDynamicEntity(maxPathId, firstEntityId,
							dynamicEntityId, path, queryList, jdbcdao);
					maxPathId++;

				}
				jdbcdao.closeStatement(resultSet);
				DatabaseOperations.executeDML(queryList);
			}

		}
		catch (SQLException e)
		{
			throw new DynamicExtensionsSystemException(
					"SQL Exception while adding indirect paths.", e);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException(
					"DAO Exception while adding indirect paths.", e);
		}
		return maxPathId;
	}

	/**
	 * @param columnName
	 * @param tableName
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private static Long getMaxId(String columnName, String tableName)
			throws DynamicExtensionsSystemException
	{
		String query = "select max(" + columnName + ") from " + tableName;
		ResultSet resultSet = null;
		Long maxId = null;
		JDBCDAO jdbcdao = null;
		try
		{
			jdbcdao = DynamicExtensionsUtility.getJDBCDAO();
			resultSet = jdbcdao.getResultSet(query, null, null);
			resultSet.next();
			maxId = resultSet.getLong(1);
			jdbcdao.closeStatement(resultSet);
		}
		catch (Exception e)
		{
			throw new DynamicExtensionsSystemException("Exception while getting max id", e);

		}
		finally
		{
			DynamicExtensionsUtility.closeDAO(jdbcdao);
		}
		return maxId;
	}

	/**
	 *
	 * @param hookEntity hook Entity
	 */
	public static void setHookEntityId(EntityInterface hookEntity)
	{
		hookEntityId = Long.valueOf(0);
		if (hookEntity != null)
		{
			hookEntityId = hookEntity.getId();
		}
	}

	/**
	 * @param conditions
	 * @return
	 */
	public boolean checkForAll(String[] conditions)
	{
		boolean returnVal = false;
		if (conditions != null)
		{
			for (String condition : conditions)
			{
				if ("All".equals(condition))
				{
					returnVal = true;
					break;
				}
			}
		}

		return returnVal;
	}

	/**
	 * @param entityName
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	public static Long getEntityId(String entityName) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		Long entityId = Long.valueOf(0);
		if (entityName != null)
		{
			EntityManagerInterface entityManager = EntityManager.getInstance();
			entityId = entityManager.getEntityId(entityName);
		}
		return entityId;
	}

}