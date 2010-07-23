
package edu.common.dynamicextensions.xmi.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.jmi.model.ModelPackage;
import javax.jmi.model.MofPackage;
import javax.jmi.xmi.XmiReader;

import org.netbeans.api.mdr.CreationFailedException;
import org.netbeans.api.mdr.MDRepository;
import org.omg.uml.UmlPackage;
import org.openide.util.Lookup;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.QueryBuilderFactory;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.QueryIntegrator;
import edu.common.dynamicextensions.util.MetaDataIntegrator;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.SaveEntityGroupAndDETablesUtil;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationDirection;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.common.dynamicextensions.xmi.AnnotationUtil;
import edu.common.dynamicextensions.xmi.DynamicQueryList;
import edu.common.dynamicextensions.xmi.XMIConfiguration;
import edu.common.dynamicextensions.xmi.XMIConstants;
import edu.common.dynamicextensions.xmi.XMIUtilities;
import edu.common.dynamicextensions.xmi.exporter.XMIExporter;
import edu.common.dynamicextensions.xmi.exporter.XMIExporterUtility;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;

/**
 * This class is used for importing the Dynamic Models using the DE.
 * It will take care of importing model creating their tables associating with hook entity
 * & then adding the Query paths.
 * @author pavan_kalantri
 *
 */
public abstract class AbstractXMIImporter
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	private static final Logger LOGGER = Logger.getCommonLogger(AbstractXMIImporter.class);

	// name of a UML extent (instance of UML metamodel) that the UML models will be loaded into
	private static final String UML_INSTANCE = "UMLInstance";
	// name of a MOF extent that will contain definition of UML metamodel
	private static final String UML_MM = "UML";

	private final static String storageFileName = "importer";
	// repository
	private MDRepository rep;
	// UML extent
	private UmlPackage uml;

	// XMI reader
	private XmiReader reader;

	private XMIConfiguration xmiConfiguration = null;

	private String fileName = "";
	private String packageName = "";
	private String pathCsvFileName = "";
	private String coRecObjCsvFName = "";

	private String hookEntityName = "";
	private EntityInterface hookEntity;
	private boolean addQueryPaths = true;
	private boolean isEntGrpSysGented = false;
	private boolean isGenerateCacore = true;
	private HibernateDAO hibernatedao = null;
	private JDBCDAO jdbcdao = null;
	private String domainModelName = "";
	/**
	 * Step 1  : initialize all resources
	 * Step 2: Process XMI
	 * Step 3: Associate with Hook Entity
	 * step 4: Execute Queries & commit all model.
	 * Step 5: Add Query paths.
	 * step 6: Post process the model i.e whatever host application wants to do
	 * can be done here (e.g. Associate main containers with clinical Study).
	 * @param args command line arguments
	 */
	public void importXMI(String[] args)
	{
		FileInputStream fileInput = null;
		List<ContainerInterface> mainContainerList = null;
		boolean isImportSuccess = true;
		try
		{ //step 1: Initialize Resources
			long processStartTime = System.currentTimeMillis();
			initializeResources(args);
			File file = new File(fileName);
			fileInput = new FileInputStream(file);
			reader.read(fileInput, null, uml);
			List<String> containerNames = readFile(pathCsvFileName);
			//step 2: Process XMI
			XMIImportProcessor xmiImportProcessor = new XMIImportProcessor();
			xmiImportProcessor.setXmiConfigurationObject(xmiConfiguration);
			long processXMIStartTime = System.currentTimeMillis();
			DynamicQueryList dynamicQueryList = xmiImportProcessor.processXmi(uml, domainModelName,
					packageName, containerNames, hibernatedao);

			mainContainerList = xmiImportProcessor.getMainContainerList();
			Map<AssociationInterface, String> multiselectMigartionScripts = xmiImportProcessor
					.getMultiselectMigartionScripts();
			boolean isEditedXmi = xmiImportProcessor.isEditedXmi;
			generateLogForProcessXMI(processXMIStartTime, isEditedXmi);
			long assoWithHEstartTime = System.currentTimeMillis();
			//Step 3: associate with hook entity.
			MetaDataIntegrator associateHookEntityUtil= new MetaDataIntegrator();
			associateHookEntityUtil.integrateWithHookEntity(hookEntityName, hibernatedao, dynamicQueryList, mainContainerList, isEditedXmi);
			//integrateWithHookEntity(hibernatedao, dynamicQueryList, mainContainerList, isEditedXmi);
			//step 4: commit model & create DE Tables
			/*LOGGER.info("Now Creating DE Tables....");
			if (hibernatedao != null)
			{
				createDETablesAndSaveEntityGroup(hibernatedao,multiselectMigartionScripts, dynamicQueryList);
			}*/
			SaveEntityGroupAndDETablesUtil saveGroupandDETablesUtil = new SaveEntityGroupAndDETablesUtil();
			saveGroupandDETablesUtil.createDETablesAndSaveEntityGroup(hibernatedao, multiselectMigartionScripts, dynamicQueryList);
			hibernatedao.commit();
			XMIImporterUtil.generateLogForHooking(assoWithHEstartTime);
			long csrStartTime = System.currentTimeMillis();

			//step 5: add Query paths.
		/*	if (addQueryPaths)
			{
				LOGGER.info("Now Adding Query Paths ....");
				addQueryPathsForConatiners(hookEntityName,hibernatedao, jdbcdao, isEntGrpSysGented,
						mainContainerList);
			}*/
			QueryIntegrator queryPaths= new QueryIntegrator();
			queryPaths.addQueryPaths(addQueryPaths, hookEntityName, hibernatedao, jdbcdao, isEditedXmi, mainContainerList);
			jdbcdao.commit();
			//step 6: associate with clinical study.
			LOGGER.info("Now associating the clinical study to the main Containers");
			postProcess(isEditedXmi, coRecObjCsvFName, mainContainerList, domainModelName);
			generateValidationLogs();
			generateLogForCompleteProcess(processStartTime, csrStartTime);
		}
		catch (Exception e)
		{
			LOGGER.fatal("Fatal error reading XMI!!", e);
			isImportSuccess = false;

			generateValidationLogs();
			if (!XMIImportValidator.errorList.isEmpty() && xmiConfiguration.isValidateXMI())
			{
				throw new RuntimeException(e);
			}
		}
		finally
		{
			closeTransaction(fileInput, hibernatedao, jdbcdao);
		}
		if (isImportSuccess)
		{
			exportXmiForCacore(mainContainerList);
		}
	}

	/**
	 *
	 */
	private void generateValidationLogs()
	{
		if (!XMIImportValidator.errorList.isEmpty())
		{
			LOGGER.error("==========================================");
			LOGGER.error("Following ERRORS encountered in the XMI: ");
			LOGGER.error("==========================================");
			for (String error : XMIImportValidator.errorList)
			{
				LOGGER.error(error);
			}
		}
	}

	/**
	 * It will export the currently Imported model in XMI v 1.1. for generating cacore.
	 * @param mainContainerList main container list.
	 */
	private void exportXmiForCacore(List<ContainerInterface> mainContainerList)
	{
		if (mainContainerList == null)
		{
			LOGGER.info("Main container list is empty hence not exporting the XMI for cacore!");
		}
		else if (isGenerateCacore)
		{
			long processStartTime = System.currentTimeMillis();
			LOGGER.info("Now Exporting xmi for cacore !!");
			try
			{
				EntityGroupInterface entityGroup = ((EntityInterface) mainContainerList.get(0)
						.getAbstractEntity()).getEntityGroup();
				if (hookEntity != null)
				{
					XMIExporterUtility.addHookEntitiesToGroup(hookEntity, entityGroup);
				}
				XMIExporter exporter = new XMIExporter();
				String exportedXmiFilePath = "./temp_deaudit_related_files/temp_exported_xmi/";
				String[] arguments = {entityGroup.getName(),
						exportedXmiFilePath + domainModelName + ".xmi",
						XMIConstants.XMI_VERSION_1_1, hookEntityName};
				exporter.initilizeInstanceVariables(arguments);
				exporter.exportXMI(entityGroup, null);
				generateLogForExportXmi(processStartTime);
			}
			catch (Exception e)
			{
				LOGGER.fatal("Fatal error while exporting XMI for caCore!!", e);
			}
		}
	}

	/**
	 * It will log all the timing details for exporting the xmi for cacore.
	 * @param processStartTime
	 */
	private void generateLogForExportXmi(long processStartTime)
	{
		long processEndTime = System.currentTimeMillis();
		long totalTime = processEndTime - processStartTime;

		LOGGER.info(" ");
		LOGGER.info("#############################################");
		LOGGER.info("  IMPORT_XMI --> TASK : EXPORT_XMI_FOR_CACORE");
		LOGGER.info("  -----------------------------------------");
		LOGGER.info(DEConstants.TIME_TAKEN + ((totalTime / 1000) / 60) + " minutes "
				+ ((totalTime / 1000) % 60) + " seconds");
		LOGGER.info("#############################################");
	}

	/**
	 * It will create the logg statements for the time required to add the Query paths &
	 * complete xmiImportProcess.
	 * @param processStartTime complete process start time
	 * @param queryAddPaths query add paths start time
	 */
	private void generateLogForCompleteProcess(long processStartTime, long queryAddPaths)
	{
		long queryAddPathsEnd = System.currentTimeMillis();
		long totalTime = queryAddPathsEnd - queryAddPaths;

		LOGGER.info(" ");
		LOGGER.info("#############################################");
		LOGGER.info("  IMPORT_XMI --> TASK : ADD QUERY PATHS");
		LOGGER.info("  -----------------------------------------");
		LOGGER.info(DEConstants.TIME_TAKEN + ((totalTime / 1000) / 60) + " minutes "
				+ ((totalTime / 1000) % 60) + " seconds");
		LOGGER.info("#############################################");
		LOGGER.info(" ");
		LOGGER.info(" ");
		LOGGER.info(" ");
		LOGGER.info(" ");
		LOGGER.info(" ");
		LOGGER.info(" ");
		LOGGER.info("                    ##################                   ");
		LOGGER.info("------------------- ##   Finished   ## ------------------");
		LOGGER.info("                    ##################                   ");
		LOGGER.info(" ");

		totalTime = queryAddPathsEnd - processStartTime;

		LOGGER.info("#############################################");
		LOGGER.info("  IMPORT_XMI -->TOTAL TIME");
		LOGGER.info("  -----------------------------------------");
		LOGGER.info(DEConstants.TIME_TAKEN + ((totalTime / 1000) / 60) + " minutes "
				+ ((totalTime / 1000) % 60) + " seconds");
		LOGGER.info("#############################################");
		LOGGER.info(" ");
		LOGGER.info(" ");
	}

	/**
	 * It will create the Logg statement for timing required for importing the model.
	 * @param processXMIStartTime
	 * @param isEditedXmi
	 */
	private void generateLogForProcessXMI(long processXMIStartTime, boolean isEditedXmi)
	{
		long processXMIEndTime = System.currentTimeMillis();
		long processXMITotalTime = processXMIEndTime - processXMIStartTime;
		LOGGER.info(" ");
		LOGGER.info("##################################################");
		LOGGER.info("  IMPORT_XMI --> TASK : IMPORT DYNAMIC MODEL");
		LOGGER.info("  --------------------------------------");
		LOGGER.info("  Time taken = " + ((processXMITotalTime / 1000) / 60) + " minutes "
				+ ((processXMITotalTime / 1000) % 60) + "seconds");
		LOGGER.info("##################################################");
		LOGGER.info(" ");

		LOGGER.info(" ");
		LOGGER.info(" ");
		LOGGER.info("EDIT_XMI case = " + (isEditedXmi ? "Y" : "N"));
		LOGGER.info(" ");
		LOGGER.info(" ");
	}

	/**
	 * It will logg all the arguments passed to import the model.
	 */
	private void loggAllInstanceVariables()
	{
		LOGGER.info("************IMPORT XMI PARAMETERS*********************");
		LOGGER.info("File name = " + fileName);
		LOGGER.info("CSV File name = " + pathCsvFileName);
		LOGGER.info("Package name = " + packageName);
		LOGGER.info("Add query paths = " + addQueryPaths);
		LOGGER.info("Condition record object CSV File name = " + coRecObjCsvFName);
		LOGGER.info("Hook entity = " + hookEntityName);
		LOGGER.info("Domain model name = " + domainModelName);
		LOGGER.info("Generate caCore = " + isGenerateCacore);
		LOGGER.info("******************************************************");
	}

	/**
	 * It will validate all the arguments & initialize all the resources which
	 * are required for importing the model.
	 * @param args cammond line arguments.
	 * @throws CreationFailedException
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 * @throws Exception exception
	 */
	private void initializeResources(String[] args) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException, CreationFailedException
	{
		validate(args);
		intitializeInstanceVaribles(args);
		xmiConfiguration = getXMIConfigurationObject();
		domainModelName = getDomainModelName(fileName);
		XMIImportValidator.validatePackageName(packageName,domainModelName);
		if (hookEntityName.equalsIgnoreCase("None"))
		{
			xmiConfiguration.setEntityGroupSystemGenerated(true);
			isEntGrpSysGented = true;
		}
		// get the default repository
		rep = XMIUtilities.getRepository(storageFileName);
		// create an XMIReader
		reader = (XmiReader) Lookup.getDefault().lookup(XmiReader.class);
		init();
		// start a read-only transaction
		rep.beginTrans(true);
		intializeDao();
		loggAllInstanceVariables();
	}


	/**
	 * It will initialize all the instance variables with the given arguments.
	 * @param argscammond line arguments.
	 */
	private void intitializeInstanceVaribles(String[] args)
	{
		fileName = args[0];
		if (args.length > 1 && args[1].trim().length() > 0)
		{
			pathCsvFileName = args[1];
		}
		if (args.length > 2 && args[2].trim().length() > 0)
		{
			packageName = args[2];
		}
		if (args.length > 3 && args[3].trim().length() > 0)
		{
			hookEntityName = args[3];
		}
		if (args.length > 4 && args[4].trim().length() > 0)
		{
			addQueryPaths = Boolean.valueOf(args[4]);
		}
		if (args.length > 5 && args[5].trim().length() > 0)
		{
			coRecObjCsvFName = args[5];
		}
		if (args.length > 6 && DEConstants.FALSE.equalsIgnoreCase(args[6].trim()))
		{
			isGenerateCacore = false;
		}
	}

	/**
	 * It will validate weather the correct number of arguments are passed or not & then throw exception accordingly.
	 * @param args arguments
	 * @throws DynamicExtensionsApplicationException exception
	 */
	private static void validate(String[] args) throws DynamicExtensionsApplicationException
	{
		if (args.length == 0)
		{
			throw new DynamicExtensionsApplicationException(
					"Please Specify the file name to be imported");
		}
		if (args.length < 2)
		{
			throw new DynamicExtensionsApplicationException(
					"Please Specify the Main Container names CSV file name.");
		}
		if (args.length < 3)
		{
			throw new DynamicExtensionsApplicationException(
					"Please Specify the name of the Package to be imported");
		}
		if (args[0] != null && args[0].trim().length() == 0)
		{
			throw new DynamicExtensionsApplicationException(
					"Please Specify the file name to be imported");
		}
		if (args[1] != null && args[1].trim().length() == 0)
		{
			throw new DynamicExtensionsApplicationException(
					"Please Specify the Main Container names CSV file name.");
		}
		if (args[2] != null && args[2].trim().length() == 0)
		{
			throw new DynamicExtensionsApplicationException(
					"Please Specify the name of the Package to be imported");
		}
	}

	/**
	 * It will create the instances of the hibernate & jdbc DAOs for furthure use.
	 * @throws DAOException exception
	 */
	private void intializeDao() throws DynamicExtensionsSystemException
	{

		jdbcdao = DynamicExtensionsUtility.getJDBCDAO();
		hibernatedao = DynamicExtensionsUtility.getHibernateDAO();

	}

	/**
	 * It will close all the open resource like input stream & daos.
	 * @param fileInput file input stream.
	 * @param hibernatedao hibernate dao
	 * @param jdbcdao jdbcdao
	 */
	private void closeTransaction(FileInputStream fileInput, HibernateDAO hibernatedao,
			JDBCDAO jdbcdao)
	{
		rep.endTrans();
		rep.shutdown();
		//MDRManager.getDefault().shutdownAll();
		try
		{
			DynamicExtensionsUtility.closeDAO(hibernatedao);
			DynamicExtensionsUtility.closeDAO(jdbcdao);
			if (fileInput != null)
			{
				fileInput.close();
			}

		}
		catch (Exception e)
		{
			LOGGER.fatal("Fatal error reading XMI!!", e);
		}
		XMIUtilities.cleanUpRepository(storageFileName);
	}


	/**
	 * It will return the name of the domain model. i.e. it will return the name of the file
	 * which is used for importing the model without the extension part.
	 * @param fileName file name
	 * @return domain model name
	 */
	private String getDomainModelName(String fileName)
	{
		File file = new File(fileName);
		int indexOfExtension = file.getName().lastIndexOf(".");
		String modelName = "";

		if (indexOfExtension == -1)
		{
			modelName = file.getName();
		}
		else
		{
			modelName = file.getName().substring(0, indexOfExtension);
		}
		return modelName;
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
	private DynamicQueryList associateHookEntity(List<ContainerInterface> mainContainerList,
			boolean isEditedXmi, HibernateDAO hibernatedao) throws DAOException,
			DynamicExtensionsSystemException, BizLogicException,
			DynamicExtensionsApplicationException
	{
		//hooked with the record Entry
		DynamicQueryList queryList = null;
		hookEntity = XMIUtilities.getStaticEntity(hookEntityName, hibernatedao);
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
			//staticEntity.addAssociation(association);
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
		AssociationInterface association = getAssociation(dynamicEntity,
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
	 * It will create a new association & then set its name etc by using the given parameter.
	 * @param targetEntity target entity of the association
	 * @param assonDirectn direction of association
	 * @param assoName name of association
	 * @param sourceRole source role
	 * @param targetRole target role
	 * @return association object
	 * @throws DynamicExtensionsSystemException exception.
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

	/**
	 * It will read the file specified by given path & then create alist of names specified in that file
	 * which are comma separated.
	 * @param path file path
	 * @return list of container names
	 * @throws IOException exception
	 */
	protected static List<String> readFile(String path) throws IOException
	{
		List<String> containerNames = new ArrayList<String>();
		File file = new File(path);

		BufferedReader bufRdr = new BufferedReader(new FileReader(file));
		String line = bufRdr.readLine();
		try
		{
			//read each line of text file
			while (line != null)
			{
				StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
				while (stringTokenizer.hasMoreTokens())
				{
					//get next token and store it in the array
					containerNames.add(stringTokenizer.nextToken());
				}

				line = bufRdr.readLine();
			}
		}
		finally
		{
			bufRdr.close();
		}
		return containerNames;
	}

	/**
	 * Initializes the MOF repository.
	 * @throws Exception exception.
	 */
	private void init() throws DynamicExtensionsSystemException, CreationFailedException
	{
		uml = (UmlPackage) rep.getExtent(UML_INSTANCE);

		if (uml == null)
		{
			// UML extent does not exist -> create it (note that in case one want's
			// to instantiate a metamodel other than MOF, they need to provide the second
			// parameter of the createExtent
			// method which indicates the metamodel package that should be instantiated)
			uml = (UmlPackage) rep.createExtent(UML_INSTANCE, getUmlPackage());
		}
	}

	/**
	 * Finds "UML" package -> this is the topmost package of UML metamodel - that's the
	 * package that needs to be instantiated in order to create a UML extent
	 * @return Mof Package
	 * @throws Exception exception
	 */
	/**
	 * Finds "UML" package -> this is the topmost package of UML metamodel - that's the
	 * package that needs to be instantiated in order to create a UML extent
	 * @return Mof Package
	 * @throws DynamicExtensionsSystemException
	 * @throws Exception exception
	 */
	public MofPackage getUmlPackage() throws DynamicExtensionsSystemException
	{
		// get the MOF extent containing definition of UML metamodel
		ModelPackage umlMM = (ModelPackage) rep.getExtent(UML_MM);
		MofPackage result = null;
		try
		{
			if (umlMM == null)
			{
				// it is not present -> create it
				umlMM = (ModelPackage) rep.createExtent(UML_MM);
			}
			// find package named "UML" in this extent
			result = getUmlPackage(umlMM);
			reader.read(UmlPackage.class.getResource("resources/01-02-15_Diff.xml").toString(),
					umlMM);
			// try to find the "UML" package again
			result = getUmlPackage(umlMM);
			if (result == null)
			{
				// it cannot be found -> UML metamodel is not loaded -> load it from XMI
				reader.read(UmlPackage.class.getResource("resources/01-02-15_Diff.xml").toString(),
						umlMM);
				// try to find the "UML" package again
				result = getUmlPackage(umlMM);
			}
		}
		catch (Exception e)
		{
			throw new DynamicExtensionsSystemException(
					"Exception occured while Intializing UML Package", e);
		}
		return result;
	}

	/** Finds "UML" package in a given extent
	 * @param umlMM MOF extent that should be searched for "UML" package.
	 * @return Mof Package
	 */
	private static MofPackage getUmlPackage(ModelPackage umlMM)
	{
		// iterate through all instances of package
		MofPackage pkg = null;
		for (Iterator it = umlMM.getMofPackage().refAllOfClass().iterator(); it.hasNext();)
		{
			pkg = (MofPackage) it.next();
			LOGGER.info("\n\nName = " + pkg.getName());

			// is the package topmost and is it named "UML"?
			if (pkg.getContainer() == null && "UML".equals(pkg.getName()))
			{
				// yes -> return it
				break;
			}
		}
		// a topmost package named "UML" could not be found
		return pkg;
	}

	/**
	 * This method will return the List of Association which contains the
	 * association list up to the hook entity from some base entity
	 * so that indirect path from the base entity (i.e. the first entity
	 * which is source of the first association in the association list)
	 * to the main containers found in the model
	 * @param hibernatedao dao used for retrieving the associations
	 * @return the list of associations
	 * @throws DynamicExtensionsSystemException exception
	 * @throws DynamicExtensionsApplicationException exception
	 */
	protected abstract List<AssociationInterface> getAssociationListForCurratedPath(
			HibernateDAO hibernatedao) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException;

	/**
	 * It will return the xmi configuration object to be used while importing the model
	 * @return XMIConfiguration object
	 */
	protected abstract XMIConfiguration getXMIConfigurationObject();

	/**
	 * This method will be implemented by the host application & it can do whatever it wants after the
	 * Xmi Importer like(associate all the given mainConatainer to the events of given clinical
	 * Study Ids in the form of studyContextEvent) etc.
	 * @param isEditedXmi specifies weather the Edit XMI case or not.
	 * @param coRecObjCsvFName name of the file
	 * @param mainContainerList main container list.
	 * @param domainModelName domain model name.
	 * @throws BizLogicException exception.
	 * @throws DAOException exception.
	 * @throws DynamicExtensionsApplicationException exception.
	 */
	protected abstract void postProcess(boolean isEditedXmi, String coRecObjCsvFName,
			List<ContainerInterface> mainContainerList, String domainModelName)
			throws BizLogicException, DAOException, DynamicExtensionsApplicationException;

}
