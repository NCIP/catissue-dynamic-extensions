package edu.common.dynamicextensions.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.userinterface.Container;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.QueryBuilderFactory;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationDirection;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.common.dynamicextensions.xmi.AnnotationUtil;
import edu.common.dynamicextensions.xmi.DynamicQueryList;
import edu.common.dynamicextensions.xmi.XMIUtilities;
import edu.common.dynamicextensions.xmi.importer.XMIImportValidator;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;

public class MetaDataIntegrator {

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	private static final Logger LOGGER = Logger.getCommonLogger(MetaDataIntegrator.class);
	private JDBCDAO jdbcdao = null;
	private HibernateDAO hibernatedao = null;

	/**
	 * It will create the instances of the hibernate & jdbc DAOs for furthure use.
	 * @throws DAOException exception
	 */
	private void intializeDao() throws DynamicExtensionsSystemException
	{

		jdbcdao = DynamicExtensionsUtility.getJDBCDAO();
		hibernatedao = DynamicExtensionsUtility.getHibernateDAO();

	}

	public void associateWithHokkEntity(
			Long containerInterfaceId, String hookingEntityId)
			 {

		try{

			intializeDao();
			String hookingEntityName= getHookingEntityName(hookingEntityId);
			//ContainerInterface containerInterface = EntityCache.getInstance().getContainerById(containerInterfaceId);
			ContainerInterface containerInterface=(ContainerInterface) hibernatedao.retrieveById(Container.class.getName(), containerInterfaceId);
			List<ContainerInterface> mainContainerList = new ArrayList<ContainerInterface>();

			mainContainerList.add(containerInterface);

			DynamicQueryList dynamicQueryList= new DynamicQueryList();

			//Step 3: associate with hook entity.
			integrateWithHookEntity(hookingEntityName,hibernatedao, dynamicQueryList,mainContainerList, false);
			//step 4: commit model & create DE Tables

			SaveEntityGroupAndDETablesUtil saveGroupandDETablesUtil = new SaveEntityGroupAndDETablesUtil();
			saveGroupandDETablesUtil.createDETablesAndSaveEntityGroup(hibernatedao, null, dynamicQueryList);
			//generateLogForHooking(assoWithHEstartTime);
			hibernatedao.commit();
			//TODO
			//step 5: add Query paths.
			QueryIntegrator queryPaths= new QueryIntegrator();
			queryPaths.addQueryPaths(true, hookingEntityName, hibernatedao, jdbcdao, false, mainContainerList);


		jdbcdao.commit();

		}catch (Exception e)
		{
			LOGGER.fatal("Fatal error reading XMI!!", e);
			//TODO
		/*	isImportSuccess = false;

			generateValidationLogs();
			if (!XMIImportValidator.errorList.isEmpty() && xmiConfiguration.isValidateXMI())
			{
				throw new RuntimeException(e);
			}*/
			throw new RuntimeException(e);
		}
		finally
		{
			closeResources();
		}
	}
	/**
	 * It will close daos.
	 */
	private void closeResources( )
	{

		try
		{
			DynamicExtensionsUtility.closeDAO(hibernatedao);
			DynamicExtensionsUtility.closeDAO(jdbcdao);

		}
		catch (Exception e)
		{
			LOGGER.fatal("Fatal error reading XMI!!", e);
		}
	}
	/**
	 * It will hook all the mainContainers with the Provided Hook entity.
	 * @param hibernatedao dao used for retrieving the hook entity.
	 * @param dynamicQueryList query list in which to add the Queries for adding the association column
	 * @param mainContainerList main containers which should be hooked.
	 * @param isEditedXmi is it the edit xmi case.
	 * @throws DAOException exception
	 * @throws DynamicExtensionsSystemException exception
	 * @throws BizLogicException exception
	 * @throws DynamicExtensionsApplicationException exception.
	 */
	public void integrateWithHookEntity(String hookEntityName,HibernateDAO hibernatedao,
			DynamicQueryList dynamicQueryList, List<ContainerInterface> mainContainerList,
			boolean isEditedXmi) throws DAOException, DynamicExtensionsSystemException,
			BizLogicException, DynamicExtensionsApplicationException
	{
		if (!hookEntityName.equalsIgnoreCase("None"))
		{
			// For CLINPORTAL, there is only one hook entity object i.e. RECORD ENTRY
			//LOGGER.info("Number of main containers = " + mainContainerList.size());
			LOGGER.info(" ");
			LOGGER.info("Now associating with hook entity -> " + hookEntityName + "....");
			LOGGER.info(" ");
			DynamicQueryList queryList = associateHookEntity(hookEntityName,mainContainerList, hookEntityName,
					isEditedXmi, hibernatedao);
			if (queryList != null)
			{
				if(dynamicQueryList.getQueryList()!=null){
					dynamicQueryList.getQueryList().addAll(queryList.getQueryList());
				}
				else
				{
					dynamicQueryList.setQueryList(queryList.getQueryList());
				}
				if(dynamicQueryList.getRevQueryList()!=null){
					dynamicQueryList.getRevQueryList().addAll(queryList.getRevQueryList());
				}
				else
				{
					dynamicQueryList.setRevQueryList(queryList.getRevQueryList());
				}
			}
		}
	}
	/**
	 * It will add the association between the provided hook entity & all the maincontainers.
	 * @param mainContainerList main container list.
	 * @param hookentity hook entity name
	 * @param isEditedXmi is edit xmi
	 * @param hibernatedao dao used to retrieve the hook entity
	 * @return the query list to add column .
	 * @throws DAOException exception.
	 * @throws DynamicExtensionsSystemException exception.
	 * @throws BizLogicException exception.
	 * @throws DynamicExtensionsApplicationException exception.
	 */
	private DynamicQueryList associateHookEntity(String hookEntityName,List<ContainerInterface> mainContainerList,
			String hookentity, boolean isEditedXmi, HibernateDAO hibernatedao) throws DAOException,
			DynamicExtensionsSystemException, BizLogicException,
			DynamicExtensionsApplicationException
	{
		//hooked with the record Entry
		DynamicQueryList queryList = null;
		EntityInterface hookEntity = XMIUtilities.getStaticEntity(hookEntityName, hibernatedao);
		if (isEditedXmi)
		{//Edit Case
			List<ContainerInterface> newContainers = new ArrayList<ContainerInterface>();
			List<ContainerInterface> existingContainers = new ArrayList<ContainerInterface>();
			separateNewAndExistingContainers(mainContainerList, hookEntity, newContainers,
					existingContainers);
			if (!newContainers.isEmpty())
			{
				queryList = addNewIntegrationObjects(hookEntity, newContainers, hibernatedao);
			}
		}
		else
		{//Add Case
			queryList = addNewIntegrationObjects(hookEntity, mainContainerList, hibernatedao);
		}
		return queryList;

	}
	/**
	 * It will separate the containers in new containers list & existing main container list
	 * according to weather they are already associated with hook entity or not.
	 * @param mainContainerList containers which are to be separated.
	 * @param staticEntity static entity.
	 * @param newContainers list in which the new containers are populated
	 * @param existingContainers list in which the existing containers are populated
	 */
	private void separateNewAndExistingContainers(List<ContainerInterface> mainContainerList,
			EntityInterface staticEntity, List<ContainerInterface> newContainers,
			List<ContainerInterface> existingContainers)
	{
		for (ContainerInterface mainContainer : mainContainerList)
		{
			boolean isAssonPresent = false;
			EntityInterface entity = (EntityInterface) mainContainer.getAbstractEntity();
			Collection<AssociationInterface> allAssociations = staticEntity.getAllAssociations();
			for (AssociationInterface association : allAssociations)
			{
				if (association.getTargetEntity().getId().compareTo(entity.getId()) == 0)
				{
					isAssonPresent = true;
					break;
				}
			}
			if (isAssonPresent)
			{
				existingContainers.add(mainContainer);
			}
			else
			{
				newContainers.add(mainContainer);
			}

		}
	}
	/**
	 * It will add the new association between static entity & each of the main containers entity.
	 * @param staticEntity source entity of the association to be added.
	 * @param mainContainerList main container list .
	 * @param hibernatedao dao
	 * @return query list
	 * @throws DynamicExtensionsSystemException exception.
	 * @throws DAOException exception.
	 * @throws BizLogicException exception.
	 * @throws DynamicExtensionsApplicationException exception.
	 */
	protected DynamicQueryList addNewIntegrationObjects(EntityInterface staticEntity,
			List<ContainerInterface> mainContainerList, HibernateDAO hibernatedao)
			throws DynamicExtensionsSystemException, DAOException, BizLogicException,
			DynamicExtensionsApplicationException
	{

		List<AssociationInterface> asso = new ArrayList<AssociationInterface>();
		for (Iterator<ContainerInterface> iterator = mainContainerList.iterator(); iterator
				.hasNext();)
		{

			ContainerInterface containerInterface = iterator.next();
			AssociationInterface association = addAssociationForEntities(staticEntity,
					(EntityInterface) containerInterface.getAbstractEntity());
			asso.add(association);
		}

			if (hibernatedao == null)
			{
				EntityManager.getInstance().persistEntityMetadataForAnnotation(staticEntity, true,
						false, null);
			}
			else
			{
				EntityManager.getInstance().persistEntityMetadataForAnnotation(staticEntity, true,
						false, null, hibernatedao);
			}


		List<String> queriesList = new ArrayList<String>();
		List<String> revQueryList = new ArrayList<String>();
		for (AssociationInterface associationInterface : asso)
		{
			queriesList.addAll(QueryBuilderFactory.getQueryBuilder().getQueryPartForAssociation(
					associationInterface, revQueryList, true));
		}
		DynamicQueryList dynamicQueryList = new DynamicQueryList();
		dynamicQueryList.setQueryList(queriesList);
		dynamicQueryList.setRevQueryList(revQueryList);
		return dynamicQueryList;
	}
	/**
	 *It will create a new association object between the staticEntity & dynamicEntity.
	 * @param staticEntity source entity
	 * @param dynamicEntity destination entity.
	 * @return newly added association
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public AssociationInterface addAssociationForEntities(EntityInterface staticEntity,
			EntityInterface dynamicEntity) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		//Create source role and target role for the association
		String roleName = staticEntity.getId().toString().concat("_").concat(
				dynamicEntity.getId().toString());
		RoleInterface sourceRole = getRole(AssociationType.CONTAINTMENT, roleName,
				Cardinality.ZERO, Cardinality.ONE);
		RoleInterface targetRole = getRole(AssociationType.CONTAINTMENT, roleName,
				Cardinality.ZERO, Cardinality.MANY);

		//Create association with the created source and target roles.
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		AssociationInterface association = factory.getAssociation(dynamicEntity,
				AssociationDirection.SRC_DESTINATION, roleName, sourceRole, targetRole);

		//Create constraint properties for the created association.
		ConstraintPropertiesInterface constProperts = AnnotationUtil.getConstraintProperties(
				staticEntity, dynamicEntity);
		association.setConstraintProperties(constProperts);

		//Add association to the static entity and save it.
		staticEntity.addAssociation(association);

		return association;

	}

	/**
	 * It will create a new Role object using given parameters
	 * @param associationType associationType
	 * @param name name
	 * @param minCard  minCard
	 * @param maxCard maxCard
	 * @return  RoleInterface
	 */
	private RoleInterface getRole(AssociationType associationType, String name,
			Cardinality minCard, Cardinality maxCard)
	{
		RoleInterface role = DomainObjectFactory.getInstance().createRole();
		role.setAssociationsType(associationType);
		role.setName(name);
		role.setMinimumCardinality(minCard);
		role.setMaximumCardinality(maxCard);
		return role;
	}

	public DynamicQueryList associateEntity(List<ContainerInterface>  mainContainerList,
			final String hookEntityName)
			throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, DAOException, BizLogicException {

		DynamicQueryList dynamicQueryList= new DynamicQueryList();

			MetaDataIntegrator associateHookEntityUtil= new MetaDataIntegrator();

			associateHookEntityUtil.integrateWithHookEntity(hookEntityName,null, dynamicQueryList,mainContainerList, false);
		return dynamicQueryList;
	}
	/**
	 * @param hookingEntityId
	 * @return
	 * @throws DAOException
	 */
	public String getHookingEntityName(String hookingEntityId) throws DAOException{
		EntityInterface staticEntity = EntityCache.getInstance().getEntityById(Long.valueOf(hookingEntityId));
		String hookingEntityName= null;
		if(staticEntity!=null){
			hookingEntityName=staticEntity.getName();
		}
		/*final String appName = DynamicExtensionDAO.getInstance().getAppName();
		DAO dao = null;

		try {
			dao = DAOConfigFactory.getInstance().getDAOFactory( appName ).getDAO();
			dao.openSession( null );

			EntityInterface staticEntity = (EntityInterface) dao.retrieveById( Entity.class.getName(),
					Long.valueOf( hookingEntityId ) );
			hookingEntityName=staticEntity.getName();
		} catch (DAOException e) {
			throw e;
		}
		finally{
			try
			{
				DynamicExtensionsUtility.closeDAO(dao);

			}
			catch (Exception e)
			{
				LOGGER.fatal("Fatal error reading XMI!!", e);
			}
		}*/
		return hookingEntityName;
	}

	//TODO
	public List<String>  validateContainer( ContainerInterface containerInterface)
	throws DynamicExtensionsSystemException {
		//start validation
		Collection<AttributeInterface> attributeColl=((EntityInterface)containerInterface.getAbstractEntity()).getAttributeCollection();
		if(attributeColl!=null){
			for (AttributeInterface attributeInterface : attributeColl) {

				XMIImportValidator.validateName(attributeInterface.getName(), "Attribute", containerInterface.getAbstractEntity().getName(), null);
			}
		}

		XMIImportValidator.validateClassName(containerInterface.getAbstractEntity().getName(), containerInterface.getAbstractEntity().getName());
		if (!XMIImportValidator.errorList.isEmpty())
		{
			/*ActionErrors actionErrors = new ActionErrors();
			actionErrors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage(
					"app.entitySaveSuccessMessage", XMIImportValidator.errorList));
			saveErrors(request, actionErrors);*/
			throw new DynamicExtensionsSystemException(
					"XMI need to be fixed for proper caCore generation. "
							+ "Please check the logs.");
		}
		return  XMIImportValidator.errorList;

		//end validation
	}
}
