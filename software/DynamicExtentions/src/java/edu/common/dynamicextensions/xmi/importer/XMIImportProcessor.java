
package edu.common.dynamicextensions.xmi.importer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.omg.uml.UmlPackage;
import org.omg.uml.foundation.core.AssociationEnd;
import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.Dependency;
import org.omg.uml.foundation.core.Generalization;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.UmlAssociation;
import org.omg.uml.foundation.core.UmlClass;
import org.omg.uml.foundation.datatypes.Multiplicity;
import org.omg.uml.foundation.datatypes.MultiplicityRange;
import org.omg.uml.modelmanagement.Model;
import org.omg.uml.modelmanagement.ModelClass;
import org.omg.uml.modelmanagement.ModelManagementPackage;

import edu.common.dynamicextensions.domain.BooleanAttributeTypeInformation;
import edu.common.dynamicextensions.domain.ByteArrayAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.DoubleAttributeTypeInformation;
import edu.common.dynamicextensions.domain.FileAttributeTypeInformation;
import edu.common.dynamicextensions.domain.FloatAttributeTypeInformation;
import edu.common.dynamicextensions.domain.IntegerAttributeTypeInformation;
import edu.common.dynamicextensions.domain.LongAttributeTypeInformation;
import edu.common.dynamicextensions.domain.StringAttributeTypeInformation;
import edu.common.dynamicextensions.domain.userinterface.Container;
import edu.common.dynamicextensions.domain.userinterface.ContainmentAssociationControl;
import edu.common.dynamicextensions.domain.userinterface.SelectControl;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AssociationDisplayAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BooleanValueInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ComboBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.FileUploadInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ListBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextFieldInterface;
import edu.common.dynamicextensions.entitymanager.EntityGroupManager;
import edu.common.dynamicextensions.entitymanager.EntityGroupManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ApplyFormControlsProcessor;
import edu.common.dynamicextensions.processor.AttributeProcessor;
import edu.common.dynamicextensions.processor.ContainerProcessor;
import edu.common.dynamicextensions.processor.ControlProcessor;
import edu.common.dynamicextensions.processor.EntityProcessor;
import edu.common.dynamicextensions.processor.LoadFormControlsProcessor;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.EntityGroupManagerUtil;
import edu.common.dynamicextensions.util.IdGeneratorUtil;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationDirection;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.common.dynamicextensions.xmi.DynamicQueryList;
import edu.common.dynamicextensions.xmi.XMIConfiguration;
import edu.common.dynamicextensions.xmi.XMIConstants;
import edu.common.dynamicextensions.xmi.XMIUtilities;
import edu.common.dynamicextensions.xmi.exporter.DatatypeMappings;
import edu.common.dynamicextensions.xmi.model.ContainerModel;
import edu.common.dynamicextensions.xmi.model.ControlsModel;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.HibernateDAO;

/**
 *
 * @author sujay_narkar
 * @author ashish_gupta
 * @author pavan_kalantri
 * @author falguni_sachde
 */

public class XMIImportProcessor
{

	/**
	 * XMI to be edited or not
	 */
	public boolean isEditedXmi = false;

	/**
	 * Package present or not
	 */
	public boolean isPackagePresent = false;

	private XMIConfiguration xmiConfigurationObject;

	/**
	 * Instance of Domain object factory, which will be used to create  dynamic extension's objects.
	 */
	protected final static DomainObjectFactory DE_FACTORY = DomainObjectFactory.getInstance();

	/**
	 * Map with KEY : UML id of a class(coming from domain model) VALUE : dynamic extension Entity created for this UML class.
	 */
	protected Map<String, EntityInterface> umlClassIdVsEntity;

	/**
	 * Map with KEY :  VALUE : AssociationInterface
	 */
	protected Map<String, AssociationInterface> umlAssociaionIdVsAssociation;

	/**
	 * Saved entity group created by this class
	 */
	private EntityGroupInterface entityGroup;

	/**
	 * Skip entity group provided by end user
	 */
	private EntityGroupInterface skipentityGroup;

	/**
	 * Map for storing containers corresponding to entities
	 */
	protected Map<String, List<ContainerInterface>> entityNameVsContainers = new HashMap<String, List<ContainerInterface>>();
	/**
	 * List for retrieved containers corresponding to entity group.
	 */
	private final Collection<ContainerInterface> retrievedContainerList = new ArrayList<ContainerInterface>();

	/**
	 * It will store the list of primary key attribute names of the entity which are belonging to the another entity
	 */
	private final Map<EntityInterface, List<String>> entityVsPrimaryKeyNameList = new HashMap<EntityInterface, List<String>>();

	private final List<ContainerInterface> mainContainerList = new ArrayList<ContainerInterface>();

	private final Map<AttributeInterface, Map<String, String>> attrVsMapTagValues = new HashMap<AttributeInterface, Map<String, String>>();

	private final Map<EntityInterface, Map<String, String>> entityVsMapTagValues = new HashMap<EntityInterface, Map<String, String>>();

	private final Map<AssociationInterface, Map<String, String>> associationVsMapTagValues = new HashMap<AssociationInterface, Map<String, String>>();

	private final Map<String, Map<String, String>> columnNameVsMapTagValues = new HashMap<String, Map<String, String>>();

	private final Map<AssociationInterface, String> multiselectMigartionScripts = new HashMap<AssociationInterface, String>();

	private final Map<String, Set<String>> entityNameVsAttributeNames = new HashMap<String, Set<String>>();

	/**
	 *
	 * @return
	 */
	public Map<AssociationInterface, String> getMultiselectMigartionScripts()
	{
		return multiselectMigartionScripts;
	}

	/**
	 *
	 * @return
	 */
	public List<ContainerInterface> getMainContainerList()
	{
		return mainContainerList;
	}

	/**
	 * @return
	 */
	public XMIConfiguration getXmiConfigurationObject()
	{
		return xmiConfigurationObject;
	}

	/**
	 * This will set the xmiConfiguration Object to given argument
	 * @param xmiConfigurationObject configuration Object to use
	 */
	public void setXmiConfigurationObject(final XMIConfiguration xmiConfigurationObject)
	{
		this.xmiConfigurationObject = xmiConfigurationObject;
	}

	/**
	 * It will import the given xmi & create the DynamicExtensions Accordingly.
	 * If hibernateDao is passed as argument then it will only save the model using that DAO & will return the
	 * QueryList outSide to the Caller.
	 * Its the responsibility of the caller to create all the tables just before committing the Work.
	 * @param umlPackage umlPackage
	 * @param entityGroupName the Name of the group which is to be Created For DynamicExtensions
	 * @param packageName name of the package which is to be imported From EA Model
	 * @param containerNames list of the names of Entities which are to be processed
	 * @param hibernatedao dao
	 * @return dynamic QueryList for table creation & their rollback queries
	 * @throws DynamicExtensionsSystemException exception
	 * @throws DynamicExtensionsApplicationException exception
	 */
	public DynamicQueryList processXmi(final UmlPackage umlPackage, final String entityGroupName,
			final String packageName, final List<String> containerNames,
			final HibernateDAO... hibernatedao) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{

		final List<UmlClass> umlClassColl = new ArrayList<UmlClass>();
		final List<UmlAssociation> umlAssociationColl = new ArrayList<UmlAssociation>();
		final List<Generalization> umlGeneralisationColl = new ArrayList<Generalization>();
		final List<EntityInterface> newEntities = new ArrayList<EntityInterface>();
		if (xmiConfigurationObject == null)
		{
			throw new DynamicExtensionsSystemException(
					"Please set the XMIConfiguration object first ");
		}

		// process for uml model
		processUmlPackage(umlPackage, packageName, umlClassColl, umlAssociationColl,
				umlGeneralisationColl);

		
		/**
		 * Let's not do a DB call, as this will be a time consuming with prod data dump
		 * Let's check existence of entity group through entity cache
		 */
		
		entityGroup = EntityGroupManagerUtil.retrieveEntityGroup(entityGroupName);
		
		if (entityGroup == null) {// Add
			entityGroup = EntityGroupManagerUtil.createEntityGroup(entityGroupName,entityGroup);
			entityGroup.setIsSystemGenerated(xmiConfigurationObject.isEntityGroupSystemGenerated());
		} else {
			/**
			 * Edit case
			 * When we get entity group object from cache, we can not modify it directly
			 * We need to clone it or create a copy of it and then work on the copy
			 * This way the cache data will remain sane until the import is fully done
			 */
			entityGroup = (EntityGroupInterface) DynamicExtensionsUtility.cloneObject(entityGroup);
			isEditedXmi = true;
		}
		//Static models of caTissue and Clinportal are system generated entity groups
		entityGroup.setIsSystemGenerated(xmiConfigurationObject.isEntityGroupSystemGenerated());
		
		String logicalPackString = XMIConstants.PACKAGE_NAME_LOGICAL_VIEW
		+ XMIConstants.DOT_SEPARATOR + XMIConstants.PACKAGE_NAME_LOGICAL_MODEL
		+ XMIConstants.DOT_SEPARATOR;
		String updatedPackageName = packageName.replaceFirst(logicalPackString, "");

		EntityGroupManagerUtil.addTaggedValue(updatedPackageName, entityGroup);

		
		final int noOfClasses = umlClassColl.size();
		umlClassIdVsEntity = new HashMap<String, EntityInterface>(noOfClasses);

		//Creating entities and entity group.
		for (final UmlClass umlClass : umlClassColl) {
			XMIImportValidator.validateClassName(umlClass.getName(), umlClass.getName());
			if (xmiConfigurationObject.isEntityGroupSystemGenerated()
					&& !umlClass.getName().startsWith(
							xmiConfigurationObject.getDefaultPackagePrefix())
					&& xmiConfigurationObject.isDefaultPackage())
			{
				umlClass.setName(xmiConfigurationObject.getDefaultPackagePrefix()
						+ umlClass.getName());

			}
			
			EntityInterface entity = null;
			//If umlclass name is among  the skip entity names ,then it means that the entity is a part of default catissuepackage.
			//so get it from the skip entity group,do not create the new entity

			if (!xmiConfigurationObject.getSkipEntityNames().isEmpty()
					&& isSkipEntity(umlClass.getName(),
							xmiConfigurationObject.getSkipEntityNames(), xmiConfigurationObject
									.getDefaultPackagePrefix())
					&& xmiConfigurationObject.getSkipEntityGroup() != null)
			{

				/**
				 *  Get it from cache, copy, and then work on copy
				 */
				skipentityGroup = (EntityGroupInterface)DynamicExtensionsUtility.cloneObject(
								EntityCache.getInstance().getEntityGroupByName(xmiConfigurationObject.getSkipEntityGroup()));	

				entity = skipentityGroup.getEntityByName(xmiConfigurationObject
						.getDefaultPackagePrefix()
						+ umlClass.getName());
				retrievedContainerList.addAll(entity.getContainerCollection());

			}
			else
			{
				entity = entityGroup.getEntityByName(umlClass.getName());
			}

			if (entity == null)
			{//Add
				entity = createEntity(umlClass);
				entity.setEntityGroup(entityGroup);
				entityGroup.addEntity(entity);
				newEntities.add(entity);
			}
			else
			{//Edit
				populateAttributes(umlClass, entity);
			}
			
			populateEntityProperties(entity, umlClass, entityVsMapTagValues);

			//For System generated models ,which are not of CATISSUE default package set isDefaultPackage =False
			//This is the case when we import the exported Catissue dynamic model ,there package is not  CATISSUE default package,but they are sysgenerated
			//By default this flag will be true.

			//			For static models
			
			if (xmiConfigurationObject.isEntityGroupSystemGenerated()
					&& !entity.getName().startsWith(
							xmiConfigurationObject.getDefaultPackagePrefix())
					&& xmiConfigurationObject.isDefaultPackage())
			{
				entity.setName(xmiConfigurationObject.getDefaultPackagePrefix() + entity.getName());

			}
			umlClassIdVsEntity.put(umlClass.refMofId(), entity);
		}
		Map<String, List<String>> parentIdVsChildrenIds = new HashMap<String, List<String>>();

		if (!umlGeneralisationColl.isEmpty())
		{
			parentIdVsChildrenIds = getParentVsChildrenMap(umlGeneralisationColl);
		}
		umlAssociaionIdVsAssociation = new HashMap<String, AssociationInterface>();
		if (!umlGeneralisationColl.isEmpty())
		{
			processInheritance(parentIdVsChildrenIds);
			//			markInheritedAttributes(entityGroup);
		}
		// process composite PrimaryKey including inherited primary keys also
		for (final EntityInterface entity : entityGroup.getEntityCollection())
		{
			//addPrimaryKeyOfParentToChild(entity);
			processCompositeKey(entity);
			populateMultiselectAttribute(entity);
		}

		// Add associations.
		addAssociations(umlAssociationColl, parentIdVsChildrenIds);

		//TODO Uncomment check about processinheritance method call
		if (!umlGeneralisationColl.isEmpty())
		{
			processInheritance(parentIdVsChildrenIds);
			//			markInheritedAttributes(entityGroup);
		}
		
		//
		// TODO: Check this method for DB access
		//
		
		// Populate entity for generating constraint properties if it has any parent set.
		XMIImporterUtil.populateEntityForConstraintProperties(entityGroup, xmiConfigurationObject);

		//Retrieving  all containers corresponding to the given entity group.
		if (entityGroup.getId() != null)
		{
			//retrievedContainerList populated by containerCollection of each entity
			final Collection<EntityInterface> entityCollection = entityGroup.getEntityCollection();
			for (final EntityInterface entity : entityCollection)
			{
				retrievedContainerList.addAll(entity.getContainerCollection());
			}
		}

		for (final UmlClass umlClass : umlClassColl)
		{
			final EntityInterface entity = umlClassIdVsEntity.get(umlClass.refMofId());

			// check if entity has duplicate association names
			XMIImportValidator.validateDuplicateAssociationName(entity);
			//In memory operation
			createContainer(entity);
			//to retrieve primary key properties of the attribute of entity
		}

		if (!umlGeneralisationColl.isEmpty())
		{//setting base container in child container.
			postProcessInheritence(parentIdVsChildrenIds);
		}
		if (!umlAssociationColl.isEmpty())
		{//Adding container for containment control
			postProcessAssociation();
		}
		processDataModel(umlPackage, packageName, xmiConfigurationObject.getSkipEntityNames(),
				xmiConfigurationObject.getDefaultPackagePrefix());

		if (!XMIImportValidator.ERROR_LIST.isEmpty() && xmiConfigurationObject.isValidateXMI())
		{
			throw new DynamicExtensionsSystemException(
					"XMI need to be fixed for importing XMI "
							+ "Please check the logs.");
		}

		// Persist container in DB.
		Logger.out.info("Now SAVING DYNAMIC MODEL....");
		Logger.out.info(" ");
		final DynamicQueryList dynamicQueryList = processPersistence(containerNames,
				xmiConfigurationObject.isEntityGroupSystemGenerated(), xmiConfigurationObject
						.isCreateTable(), xmiConfigurationObject.isDefaultPackage(),
				xmiConfigurationObject.getDefaultPackagePrefix(), hibernatedao);

		final List<Long> newEntitiesIds = xmiConfigurationObject.getNewEntitiesIds();
		
		for (final EntityInterface newEntity : newEntities)
		{
			newEntitiesIds.add(newEntity.getId());
		}
		// Execute data migration scripts for attributes that were changed from a normal attribute to
		// a multi select attribute.IF dao is provided then its the responsibility of the host to execute these Queries.
		if (hibernatedao == null || hibernatedao.length == 0)
		{
			final List<String> multiSelMigrationQueries = EntityManagerUtil
					.updateSqlScriptToMigrateOldDataForMultiselectAttribute(multiselectMigartionScripts);
			XMIImporterUtil.executeDML(multiSelMigrationQueries);
		}
		return dynamicQueryList;
	}

	/**
	 * This method will check if the model contains the given package& will collect
	 * all the model elements in the corresponding collections.
	 * @param umlPackage uml package.
	 * @param packageName package to be searched in the model.
	 * @param umlClassColl collection which collects all classes.
	 * @param umlAssociationColl collection which collects all associations.
	 * @param umlGeneralisationColl collection which collects all generalisations.
	 * @throws DynamicExtensionsSystemException exception if package is not present
	 */
	private void processUmlPackage(final UmlPackage umlPackage, final String packageName,
			final List<UmlClass> umlClassColl, final List<UmlAssociation> umlAssociationColl,
			final List<Generalization> umlGeneralisationColl)
			throws DynamicExtensionsSystemException
	{
		processModel(umlPackage, umlClassColl, umlAssociationColl, umlGeneralisationColl, null,
				packageName, false);
		if (!isPackagePresent)
		{
			// if package not present then if package name is not starting with logical view.logical model
			//then append the same & then try again.
			String newPackage = XMIConstants.PACKAGE_NAME_LOGICAL_VIEW + XMIConstants.DOT_SEPARATOR
					+ XMIConstants.PACKAGE_NAME_LOGICAL_MODEL + XMIConstants.DOT_SEPARATOR;
			if (!packageName.startsWith(newPackage))
			{
				String updatedPackageName = newPackage + packageName;
				processModel(umlPackage, umlClassColl, umlAssociationColl, umlGeneralisationColl,
						null, updatedPackageName, false);
			}
			if (!isPackagePresent)
			{
				throw new DynamicExtensionsSystemException(
						"Specified package is not present in the XMI.");
			}
		}
	}

	/**
	 * @param umlAssociationColl
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void addAssociations(final List<UmlAssociation> umlAssociationColl,
			final Map<String, List<String>> parentIdVsChildrenIds)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
			if (umlAssociationColl != null)
			{
				for (final UmlAssociation umlAssociation : umlAssociationColl)
				{
					addAssociation(umlAssociation,parentIdVsChildrenIds);
				}
			}
	}

	/**
	 * It will search the primary key attribute which is in another entity and will add it to own
	 * composite collection
	 * @param entity whose composite key is to be processed
	 * @throws DynamicExtensionsSystemException
	 */
	private void processCompositeKey(final EntityInterface entity)
			throws DynamicExtensionsSystemException
	{
		final List<String> primaryKeyList = entityVsPrimaryKeyNameList.get(entity);
		if (primaryKeyList != null)
		{
			for (final String primaryKeyName : primaryKeyList)
			{
				final StringTokenizer tokenizer = new StringTokenizer(primaryKeyName,
						XMIConstants.DOT_SEPARATOR);
				final String entityName = getNextToken(tokenizer);
				final EntityInterface targetEntity = entityGroup.getEntityByName(entityName);
				if (targetEntity == null)
				{
					throw new DynamicExtensionsSystemException("Given entity not found");
				}
				else
				{
					final String attributeName = getNextToken(tokenizer);
					final AttributeInterface attribute = targetEntity
							.getEntityAttributeByName(attributeName);
					if (attribute != null)
					{
						entity.addPrimaryKeyAttribute(attribute);
					}
				}
			}
		}

	}

	/**
	 * It will populate the UIproperties semantic properties and primary key properties
	 * @param entity
	 * @param umlClass
	 * @param entityVsMapTagValues2
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	private void populateEntityProperties(final EntityInterface entity, final UmlClass umlClass,
			final Map<EntityInterface, Map<String, String>> entityVsMapTagValues)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		populateEntityUIProperties(entity, umlClass.getTaggedValue());
		addSemanticPropertyForEntities(entity, entityVsMapTagValues.get(entity));
		processPrimaryKey(entity, entityVsMapTagValues.get(entity));
	}

	/**
	 * It will generate constraint properties for the association which is created due to the
	 * multiselect attribute in the entity
	 * @param entity in which to search for multiselct attributes associaion
	 * @throws DynamicExtensionsSystemException
	 */
	private void populateMultiselectAttribute(final EntityInterface entity)
			throws DynamicExtensionsSystemException
	{
		final Collection<AssociationInterface> associationColl = entity.getAllAssociations();
		for (final AssociationInterface association : associationColl)
		{
			final Map<String, String> taggedValueMap = associationVsMapTagValues.get(association);
			if (isMultiselectTagValue(taggedValueMap))
			{
				association.populateAssociationForConstraintProperties();
			}
		}
	}

	/**
	 * It will check the tagValue & depending on it will make the corresponding
	 * attribute as primary key if it is in the same entity and if it is in different entity
	 * it will save its name in the list which is stored in the entityVsPrimaryKeyNameList map
	 * which will be processed when processing the composite key for the entity
	 * @param entity whose primary key is to be processed
	 * @param taggedValueMap map of tagKey and tagValue of the entity
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	private void processPrimaryKey(final EntityInterface entity,
			final Map<String, String> taggedValueMap) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		final Collection<AttributeInterface> primKeyAttrColl = entity
				.getPrimaryKeyAttributeCollection();
		resetPrimaryKeyAttributes(primKeyAttrColl);
		//Deleting primary key collection only in case of edited xmi true and isAddIdAttribute=false
		//This is specific to CIDER only.As inside Clinportal and Catissue the primarykey will never going to be edited,
		//so keep the primary key collection  same ,do not clear it.

		if (!xmiConfigurationObject.isAddIdAttribute() && isEditedXmi)
		{
			primKeyAttrColl.clear();
		}

		String primaryKey = taggedValueMap.get(XMIConstants.TAGGED_VALUE_PRIMARYKEY);
		final List<String> primaryKeyAttributeNameList = new ArrayList<String>();
		//This is modification because cider does not requires Id attribute at all they req. only metadata
		if (xmiConfigurationObject.isAddIdAttribute())
		{
			EntityManagerUtil.addIdAttribute(entity);
		}
		else if (primaryKey != null && !"".equals(primaryKey))
		{
			//removeIdAttributeFromEntity(entity);
			final String primaryAttributeName[] = primaryKey.split(XMIConstants.COMMA);
			for (final String attributeName : primaryAttributeName)
			{
				final StringTokenizer tokenizer = new StringTokenizer(attributeName,
						XMIConstants.DOT_SEPARATOR);
				final String entityName = getNextToken(tokenizer);
				if (entity.getName().equals(entityName))
				{
					final String attribute = getNextToken(tokenizer);
					AttributeInterface primaryAttribute = entity
							.getEntityAttributeByName(attribute);
					if (primaryAttribute == null)
					{
						throw new DynamicExtensionsSystemException("primary key attribute "
								+ attributeName + "not found in entity " + entityName);
					}
					else
					{
						XMIImportValidator.validateDataTypeForPrimaryKey(primaryAttribute);
						primaryAttribute.setIsPrimaryKey(true);
						entity.addPrimaryKeyAttribute(primaryAttribute);
					}
				}
				else
				{
					primaryKeyAttributeNameList.add(attributeName);
				}
			}
		}
		entityVsPrimaryKeyNameList.put(entity, primaryKeyAttributeNameList);
	}

	/**
	 * It will retrieve the next token from the given tokenizer if present else
	 * will return empty ("") string
	 * @param tokenizer from which to retrieve next token
	 * @return next token
	 */
	private String getNextToken(final StringTokenizer tokenizer)
	{
		String token = "";
		if (tokenizer.hasMoreTokens())
		{
			token = tokenizer.nextToken();
		}
		return token;
	}

	/**
	 * It will set the isPrimaryKey to false of the each attribute in the given collection
	 * @param primKeyAttrColl
	 */
	private void resetPrimaryKeyAttributes(final Collection<AttributeInterface> primKeyAttrColl)
	{
		for (final AttributeInterface attribute : primKeyAttrColl)
		{
			if (!attribute.getName().equals(XMIConstants.ID_ATTRIBUTE_NAME))
			{
				attribute.setIsPrimaryKey(false);
				attribute.setIsNullable(true);
			}

		}

	}

	/**
	 * This method checks the entity name if it matches with the data type names like Integer, String etc.
	 * @param umlClassName
	 * @return
	 */
	private boolean checkEntityWithDataTypeEntities(final String umlClassName)
	{
		final DatatypeMappings dataType = DatatypeMappings.get(umlClassName);
		boolean flag = false;
		if (dataType != null
				|| umlClassName
						.equalsIgnoreCase(edu.common.dynamicextensions.ui.util.Constants.COLLECTION)
				|| umlClassName
						.equalsIgnoreCase(edu.common.dynamicextensions.ui.util.Constants.DATE)
				|| umlClassName
						.equalsIgnoreCase(edu.common.dynamicextensions.ui.util.Constants.TIME))
		{
			flag = true;
		}
		return flag;
	}

	/**
	 * @param umlPackage
	 * @param umlClassColl
	 * @param umlAssociationColl
	 * @param umlGeneralisationColl
	 */
	private void processModel(final UmlPackage umlPackage, final List<UmlClass> umlClassColl,
			final List<UmlAssociation> umlAssociationColl,
			final List<Generalization> umlGeneralisationColl,
			final List<Dependency> umlDependencyColl, final String packageName,
			final boolean isDataModel)
	{
		final ModelManagementPackage modelManagementPackage = umlPackage.getModelManagement();
		final ModelClass modelClass = modelManagementPackage.getModel();
		final Collection<Model> modelColl = modelClass.refAllOfClass();

		for (final Model model : modelColl)
		{
			/*final Collection ownedElementColl = model.getOwnedElement();
			Logger.out.info(" ");
			Logger.out.info("MODEL OWNED ELEMENT COLLECTION SIZE = " + ownedElementColl.size());
			Logger.out.info(" ");
			Logger.out.info(" ");
			final Iterator iter = ownedElementColl.iterator();*/

			final StringTokenizer tokens = new StringTokenizer(packageName,
					XMIConstants.DOT_SEPARATOR);
			String token = "";
			if (tokens.hasMoreTokens())
			{
				token = tokens.nextToken();
			}
			if (token.trim().equalsIgnoreCase(XMIConstants.DEFAULT_PACKAGE))
			{
				processPackageForModel(model, umlClassColl, umlAssociationColl,
						umlGeneralisationColl, umlDependencyColl, isDataModel);
			}
			else
			{
				final Collection ownedElementColl = model.getOwnedElement();
				Logger.out.info(" ");
				Logger.out.info("MODEL OWNED ELEMENT COLLECTION SIZE = " + ownedElementColl.size());
				Logger.out.info(" ");
				Logger.out.info(" ");
				final Iterator iter = ownedElementColl.iterator();
				final StringTokenizer initializedTokens = new StringTokenizer(packageName,
						XMIConstants.DOT_SEPARATOR);
				token = "";
				if (initializedTokens.hasMoreTokens())
				{
					token = initializedTokens.nextToken();
				}
				while (iter.hasNext())
				{
					final Object obj = iter.next();
					if (obj instanceof org.omg.uml.modelmanagement.UmlPackage)
					{
						final org.omg.uml.modelmanagement.UmlPackage umlPackageObj = (org.omg.uml.modelmanagement.UmlPackage) obj;
						if (token.equalsIgnoreCase(umlPackageObj.getName()))
						{
							processSelectedPackage(umlPackageObj, initializedTokens, umlClassColl,
									umlAssociationColl, umlGeneralisationColl, umlDependencyColl,
									isDataModel);

							//					processPackage(umlPackageObj, umlClassColl, umlAssociationColl,
							//					umlGeneralisationColl , packageName);
						}
					}
				}
			}

		}
	}

	/**
	 * @param parentPkg
	 * @param tokens
	 * @param umlClassColl
	 * @param umlAssociationColl
	 * @param umlGeneralisationColl
	 */
	private void processSelectedPackage(final org.omg.uml.modelmanagement.UmlPackage parentPkg,
			final StringTokenizer tokens, final List<UmlClass> umlClassColl,
			final List<UmlAssociation> umlAssociationColl,
			final List<Generalization> umlGeneralisationColl,
			final List<Dependency> dependencyColl, final boolean isDataModel)
	{
		String token = "";
		int temp = 0;
		if (tokens.hasMoreTokens())
		{
			token = tokens.nextToken();
		}

		//If no package is present in the XMI take package name as "Default"
		//		if(token.trim().equalsIgnoreCase(XMIConstants.DEFAULT_PACKAGE))
		//		{
		//			processPackage(parentPkg,umlClassColl,umlAssociationColl,umlGeneralisationColl);
		//		}
		//		else
		for (final Iterator i = parentPkg.getOwnedElement().iterator(); i.hasNext();)
		{
			final Object object = i.next();//
			if (object instanceof org.omg.uml.modelmanagement.UmlPackage)
			{
				final org.omg.uml.modelmanagement.UmlPackage subPkg = (org.omg.uml.modelmanagement.UmlPackage) object;
				if (token.equalsIgnoreCase(subPkg.getName()))
				{
					processSelectedPackage(subPkg, tokens, umlClassColl, umlAssociationColl,
							umlGeneralisationColl, dependencyColl, isDataModel);
					temp++;
				}
			}
		}
		if (temp == 0)
		{//if package name is present, import only that package.
			processPackage(parentPkg, umlClassColl, umlAssociationColl, umlGeneralisationColl,
					dependencyColl, isDataModel);
		}
	}

	/**
	* @param parentPkg
	* @param umlClasses
	* @param associations
	* @param generalizations
	* @param dependencyColl
	* @param isDataModel
	*/

	private void processPackageForModel(final Model parentPkg, final List<UmlClass> umlClasses,
			final List<UmlAssociation> associations, final List<Generalization> generalizations,
			final List<Dependency> dependencyColl, final boolean isDataModel)
	{
		isPackagePresent = true;
		for (final Iterator i = parentPkg.getOwnedElement().iterator(); i.hasNext();)
		{
			final Object obj = i.next();
			/*	if (o instanceof org.omg.uml.modelmanagement.UmlPackage && !(packageName.equals(parentPkg.getName())))
			 {
			 org.omg.uml.modelmanagement.UmlPackage subPkg = (org.omg.uml.modelmanagement.UmlPackage) o;
			 processPackage(subPkg, umlClasses, associations, generalizations,packageName);
			 }
			 else*/
			if (isDataModel)
			{
				if (obj instanceof Dependency)
				{
					dependencyColl.add((Dependency) obj);
				}
			}
			else
			{
				if (obj instanceof UmlAssociation)
				{
					associations.add((UmlAssociation) obj);
				}
				else if (obj instanceof Generalization)
				{
					generalizations.add((Generalization) obj);
				}
				else if (obj instanceof UmlClass)
				{
					final UmlClass umlClass = (UmlClass) obj;
					final boolean isEntityADatatype = checkEntityWithDataTypeEntities(umlClass
							.getName());
					if (isEntityADatatype)
					{//Skipping classes having datatype names eg Integer,String etc.
						continue;
					}
					final Collection<Generalization> generalizationColl = umlClass
							.getGeneralization();
					if (generalizationColl != null)
					{
						generalizations.addAll(generalizationColl);
					}
					umlClasses.add(umlClass);
				}
			}
		}

	}

	/**
	 * @param parentPkg
	 * @param pkgName
	 * @param umlClasses
	 * @param associations
	 * @param generalizations
	 * @param dependencyColl
	 * @param isDataModel
	 */
	private void processPackage(final org.omg.uml.modelmanagement.UmlPackage parentPkg,
			final List<UmlClass> umlClasses, final List<UmlAssociation> associations,
			final List<Generalization> generalizations, final List<Dependency> dependencyColl,
			final boolean isDataModel)
	{
		isPackagePresent = true;
		for (final Iterator i = parentPkg.getOwnedElement().iterator(); i.hasNext();)
		{
			final Object obj = i.next();
			/*	if (o instanceof org.omg.uml.modelmanagement.UmlPackage && !(packageName.equals(parentPkg.getName())))
			 {
			 org.omg.uml.modelmanagement.UmlPackage subPkg = (org.omg.uml.modelmanagement.UmlPackage) o;
			 processPackage(subPkg, umlClasses, associations, generalizations,packageName);
			 }
			 else*/

			if (isDataModel)
			{
				if (obj instanceof Dependency)
				{
					dependencyColl.add((Dependency) obj);
				}
			}
			else
			{
				if (obj instanceof UmlAssociation)
				{
					associations.add((UmlAssociation) obj);
				}
				else if (obj instanceof Generalization)
				{
					generalizations.add((Generalization) obj);
				}
				else if (obj instanceof UmlClass)
				{
					final UmlClass umlClass = (UmlClass) obj;
					final boolean isEntityADatatype = checkEntityWithDataTypeEntities(umlClass
							.getName());
					if (isEntityADatatype)
					{//Skipping classes having datatype names eg Integer,String etc.
						continue;
					}

					final Collection<Generalization> generalizationColl = umlClass
							.getGeneralization();
					if (generalizationColl != null)
					{
						generalizations.addAll(generalizationColl);
					}
					umlClasses.add(umlClass);
				}
			}
		}
	}

	/**
	 * Creates a Dynamic Extension Entity from given UMLClass.<br>
	 * It also assigns all the attributes of the UMLClass to the Entity as the
	 * Dynamic Extension Primitive Attributes.Then stores the input UML class,
	 * adds the Dynamic Extension's PrimitiveAttributes to the Collection.
	 * Properties which are copied from UMLAttribute to DE Attribute are
	 * name,description,semanticMetadata,permissible values
	 * @param umlClass
	 *            The UMLClass from which to form the Dynamic Extension Entity
	 * @param umlPackage
	 * @return the unsaved entity for given UML class
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private EntityInterface createEntity(final UmlClass umlClass)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		final String name = umlClass.getName();
		//EntityInterface entity = deFactory.createEntity();
		// calling createEntity of EntityProcessor as it generates id attibute for that entity
		final EntityProcessor entityProcessor = EntityProcessor.getInstance();

//		TODO :: Send hibernate object.
		final EntityInterface entity = entityProcessor.createEntity();
		entity.setName(name);
		entity.setDescription(entityGroup.getName() + "--" + umlClass.getName());
		entity.setAbstract(umlClass.isAbstract());
		populateAttributes(umlClass, entity);
		//populateAttributes(umlClass, entity);

		//		setSemanticMetadata(entity, umlClass.getSemanticMetadata());
		return entity;
	}

	/**
	 * @param attrColl
	 * @param entity
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private AttributeInterface createAttribute(final Attribute umlAttribute,
			final EntityInterface entity) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		//Not showing id attribute on UI if Id attribute is to be added by DE which is specified in xmiConfiguration Object

		if ((umlAttribute.getName().equalsIgnoreCase(DEConstants.OBJ_IDENTIFIER) || umlAttribute
				.getName().equalsIgnoreCase(Constants.IDENTIFIER))
				&& xmiConfigurationObject.isAddIdAttribute())
		{
			//If id attribute is system generated then don't create attribute for user given Id attribute
			return null;
		}
		final DataType dataType = DataType.get(umlAttribute.getType().getName());
		AttributeInterface originalAttribute = null;
		if (dataType != null)
		{//Temporary solution for unsupported datatypes. Not adding attributes having unsupported datatypes.

//			TODO :: Send hibernate object.
			originalAttribute = entity.getAttributeByNameIncludingInheritedAttribute(umlAttribute
					.getName());
			if (originalAttribute == null)
			{//New attribute has been created
				final AttributeInterface attribute = dataType.createAttribute(umlAttribute);
				if (attribute != null)
				{ // to bypass attributes of invalid datatypes
					attribute.setName(umlAttribute.getName());
					//					attribute.setDescription(umlAttribute.getTaggedValue().getDescription());
					//					setSemanticMetadata(attribute, umlAttribute.getSemanticMetadata());
					final Collection<TaggedValue> taggedValueColl = umlAttribute.getTaggedValue();
					populateAttributeUIProperties(attribute, taggedValueColl);
					final Map<String, String> taggedValueMap = attrVsMapTagValues.get(attribute);
					addSemanticPropertyForAttributes(attribute, taggedValueMap);

					if (isMultiselectTagValue(taggedValueMap))
					{
						addMultiselectAttribute(attribute, umlAttribute, taggedValueMap, entity);
					}
					else
					{
						entity.addAttribute(attribute);
					}
				}
				originalAttribute = attribute;
			}
			else
			{//Attribute has been edited
				final Collection<TaggedValue> taggedValueColl = umlAttribute.getTaggedValue();
				populateAttributeUIProperties(originalAttribute, taggedValueColl);
				final Map<String, String> taggedValueMap = attrVsMapTagValues
						.get(originalAttribute);
				if (isMultiselectTagValue(taggedValueMap)
						&& !entity.isMultiselectAttributePresent(umlAttribute.getName()))
				{
					removeAttribute(entity, originalAttribute, taggedValueMap, dataType,
							umlAttribute);
				}
				else
				{
					addSemanticPropertyForAttributes(originalAttribute, attrVsMapTagValues
							.get(originalAttribute));
				}

			}
			XMIImportValidator.validateTagsForAutoloadingCiderData(originalAttribute,
					attrVsMapTagValues);
		}
		return originalAttribute;
	}

	/**
	 * This method will add the attributes to the entity for the given Umlclass from XMI.
	 * boolean includeInherited - Specifies whether inherited attributes should be included or not.
	 * @param klass
	 * @param entity in which to add the attributes
	 * @return collection of attributes names
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public Collection<String> addAttributes(final UmlClass klass, final EntityInterface entity)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		final Collection atts = new ArrayList();
		final Collection<String> attributeNames = new HashSet<String>();
		for (final Iterator i = klass.getFeature().iterator(); i.hasNext();)
		{
			final Object object = i.next();
			if (object instanceof Attribute)
			{
				atts.add(object);
				final Attribute att = (Attribute) object;
				createAttribute(att, entity);
				attributeNames.add(att.getName());
				XMIImportValidator.validateName(att.getName(), "Attribute", entity.getName(), null);
			}
		}

		if (xmiConfigurationObject.isAddInheritedAttribute())
		{
			final Map attsMap = new HashMap();
			UmlClass superClass = XMIUtilities.getSuperClass(klass);
			AttributeInterface attribute;
			while (superClass != null)
			{
				for (final Iterator i = superClass.getFeature().iterator(); i.hasNext();)
				{
					final Object object = i.next();
					if (object instanceof Attribute)
					{
						final Attribute att = (Attribute) object;
						if (attsMap.get(att.getName()) == null)
						{
							attsMap.put(att.getName(), att);
							attribute = createAttribute(att, entity);
							if (attribute != null)
							{
								attributeNames.add(attribute.getName());
								DynamicExtensionsUtility.addInheritedTaggedValue(attribute);
							}
						}
					}
				}
				superClass = XMIUtilities.getSuperClass(superClass);
			}
		}
		return attributeNames;
	}

	/**
	 * @param entity
	 * @param originalAttribute
	 * @param taggedValueMap
	 * @param dataType
	 * @param umlAttribute
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void removeAttribute(final EntityInterface entity,
			final AttributeInterface originalAttribute, final Map<String, String> taggedValueMap,
			final DataType dataType, final Attribute umlAttribute)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		originalAttribute.setName(DEConstants.DEPRECATED + originalAttribute.getName());
		final EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		final ControlInterface controlInterface = entityManagerInterface
				.getControlByAbstractAttributeIdentifier(originalAttribute.getId());

		final AttributeInterface attribute = dataType.createAttribute(umlAttribute);
		addSemanticPropertyForAttributes(attribute, attrVsMapTagValues.get(originalAttribute));
		final AssociationInterface association = addMultiselectAttribute(attribute, umlAttribute,
				taggedValueMap, entity);
		controlInterface.setBaseAbstractAttribute(association);
		multiselectMigartionScripts.put(association, EntityManagerUtil
				.getSqlScriptToMigrateOldDataForMultiselectAttribute(entity, association,
						attribute, originalAttribute));
	}

	/**
	 * addMultiselectAttribute.
	 * @param attribute
	 * @param umlAttribute
	 * @throws DynamicExtensionsSystemException
	 */
	private AssociationInterface addMultiselectAttribute(final AttributeInterface attribute,
			final Attribute umlAttribute, final Map<String, String> taggedValueMap,
			final EntityInterface entity) throws DynamicExtensionsSystemException
	{
		final DomainObjectFactory factory = DomainObjectFactory.getInstance();
		final AssociationInterface association = createAssociation();
		association.setIsCollection(Boolean.TRUE);

		final EntityInterface targetEntity = factory.createEntity();
		EntityManagerUtil.addIdAttribute(targetEntity);
		targetEntity.setName(DEConstants.COLLECTIONATTRIBUTECLASS + umlAttribute.getName()
				+ IdGeneratorUtil.getInstance().getNextUniqeId());
		final String multiSelectTName = taggedValueMap
				.get(XMIConstants.TAGGED_VALUE_MULTISELECT_TABLE_NAME);
		//In case of catissue metadata integration donot change table name of multiselect entity
		if (multiSelectTName != null && xmiConfigurationObject.getSkipEntityGroup() != null)
		{
			targetEntity.getTableProperties().setName(multiSelectTName);

		}
		attribute.setName(DEConstants.COLLECTIONATTRIBUTE
				+ IdGeneratorUtil.getInstance().getNextUniqeId());
		targetEntity.addAbstractAttribute(attribute);
		entityGroup.addEntity(targetEntity);
		targetEntity.setEntityGroup(entityGroup);

		entityVsPrimaryKeyNameList.put(targetEntity, new ArrayList<String>());

		association.setTargetEntity(targetEntity);
		association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
		association.setName(umlAttribute.getName());
		association.setSourceRole(EntityManagerUtil.getRole(AssociationType.CONTAINTMENT,
				DEConstants.COLLECTIONATTRIBUTEROLE + entity.getName(), Cardinality.ONE,
				Cardinality.ONE));
		association.setTargetRole(EntityManagerUtil.getRole(AssociationType.CONTAINTMENT,
				DEConstants.COLLECTIONATTRIBUTEROLE + targetEntity.getName(), Cardinality.ONE,
				Cardinality.MANY));

		entity.addAbstractAttribute(association);
		// Commented the line as it does not set constraint properties -- because the primarykey attribute
		// collection is empty at this stage
		//association.populateAssociationForConstraintProperties();
		final Map<String, String> valueMap = new HashMap<String, String>();
		valueMap.put(XMIConstants.TAGGED_VALUE_MULTISELECT, getMultiselectTagValue(taggedValueMap));
		taggedValueMap.remove(XMIConstants.TAGGED_VALUE_MULTISELECT);
		associationVsMapTagValues.put(association, valueMap);
		return association;
	}

	/**
	 * @param attribute
	 * @param taggedValueColl
	 */
	private void populateAttributeUIProperties(final AttributeInterface attribute,
			final Collection<TaggedValue> taggedValueColl)
	{
		final Map<String, String> tagNameVsTagValue = populateTagValueMap(taggedValueColl,
				attribute);
		attrVsMapTagValues.put(attribute, tagNameVsTagValue);
	}

	/**
	 * @param entity
	 * @param taggedValueColl
	 */
	private void populateEntityUIProperties(final EntityInterface entity,
			final Collection<TaggedValue> taggedValueColl)
	{
		final Map<String, String> tagNameVsTagValue = populateTagValueMap(taggedValueColl, entity);
		entityVsMapTagValues.put(entity, tagNameVsTagValue);
	}

	/**
	 * @param association
	 * @param taggedValueColl
	 */
	private void populateAssociationUIProperties(final AssociationInterface association,
			final Collection<TaggedValue> taggedValueColl)
	{
		final Map<String, String> tagNameVsTagValue = populateTagValueMap(taggedValueColl,
				association);
		associationVsMapTagValues.put(association, tagNameVsTagValue);
	}

	/**
	 * @param taggedValueColl
	 * @return
	 */
	private Map<String, String> populateTagValueMap(final Collection<TaggedValue> taggedValueColl,
			final AbstractMetadataInterface abstrMetaDataObj)
	{
		final Map<String, String> tagNameVsTagValue = new HashMap<String, String>();
		//String tagName;
		final Collection<TaggedValueInterface> deTaggedValueCollection = new HashSet<TaggedValueInterface>();
		final DomainObjectFactory factory = DomainObjectFactory.getInstance();
		TaggedValueInterface tag;
		for (final TaggedValue taggedValue : taggedValueColl)
		{
			if (taggedValue.getType() != null)
			{
				final Collection<String> dataValueColl = taggedValue.getDataValue();
				String tagName = taggedValue.getType().getName();
				for (final String value : dataValueColl)
				{
					if (tagName.startsWith(XMIConstants.TAGGED_NAME_PREFIX))
					{
						tagName = tagName.replaceFirst(XMIConstants.TAGGED_NAME_PREFIX, "");
						// it will retrieve the tag which is already present on the  abstrMetaDataObj in case of edit xmi
						
						tag = DynamicExtensionsUtility.updatePackageName(abstrMetaDataObj.getTaggedValueCollection(),
								tagName);
						//if tag not found then create the new one
						if (tag == null)
						{
							tag = factory.createTaggedValue();
							tag.setKey(tagName);
							tag.setValue(value);
						}
						// if tag found then only change the value of the tag with current value.
						else
						{
							tag.setValue(value);
						}
						deTaggedValueCollection.add(tag);
					}
					tagNameVsTagValue.put(tagName, value);
				}

			}
		}
		//it will clear the old taggedValue collection & add the new tagged value collection in abstrMetaDataObj.
		abstrMetaDataObj.getTaggedValueCollection().clear();
		abstrMetaDataObj.getTaggedValueCollection().addAll(deTaggedValueCollection);
		return tagNameVsTagValue;
	}
	/**
	 * @param entityInterface
	 * @param taggedValueMap
	 */
	private void addSemanticPropertyForEntities(final EntityInterface entityInterface,
			final Map<String, String> taggedValueMap)
	{
		final Collection<SemanticPropertyInterface> semanticPropertyColl = new HashSet<SemanticPropertyInterface>();
		//		Concept codes
		String conceptCode = taggedValueMap.get(XMIConstants.TV_OBJECT_CLASS_CONCEPT_CODE);
		if (conceptCode != null)
		{
			String conceptDefinition = taggedValueMap.get(XMIConstants.TV_OBJECT_CLASS_CONCEPT_DEF);
			String term = taggedValueMap
					.get(XMIConstants.TAGGED_VALUE_OBJECT_CLASS_CONCEPT_DEFINITION_SOURCE);
			String thesarausName = taggedValueMap
					.get(XMIConstants.TAGGED_VALUE_OBJECT_CLASS_CONCEPT_PREFERRED_NAME);
			SemanticPropertyInterface semanticPropertyInterface = getSemanticProperty(conceptCode,
					conceptDefinition, term, thesarausName, 0);
			semanticPropertyColl.add(semanticPropertyInterface);

			final Set<String> tagNames = taggedValueMap.keySet();
			for (final String tagName : tagNames)
			{
				if (tagName
						.startsWith(XMIConstants.TAGGED_VALUE_OBJECT_CLASS_QUALIFIER_CONCEPT_CODE))
				{
					final int beginIndex = XMIConstants.TAGGED_VALUE_OBJECT_CLASS_QUALIFIER_CONCEPT_CODE
							.length();
					final String qualifierNumber = tagName.substring(beginIndex);

					conceptCode = taggedValueMap
							.get(XMIConstants.TAGGED_VALUE_OBJECT_CLASS_QUALIFIER_CONCEPT_CODE
									+ qualifierNumber);
					conceptDefinition = taggedValueMap
							.get(XMIConstants.TAGGED_VALUE_OBJECT_CLASS_QUALIFIER_CONCEPT_DEFINITION
									+ qualifierNumber);
					term = taggedValueMap
							.get(XMIConstants.TAGGED_VALUE_OBJECT_CLASS_QUALIFIER_CONCEPT_DEFINITION_SOURCE
									+ qualifierNumber);
					thesarausName = taggedValueMap
							.get(XMIConstants.TAGGED_VALUE_OBJECT_CLASS_QUALIFIER_CONCEPT_PREFERRED_NAME
									+ qualifierNumber);

					semanticPropertyInterface = getSemanticProperty(conceptCode, conceptDefinition,
							term, thesarausName, Integer.parseInt(qualifierNumber));
					semanticPropertyColl.add(semanticPropertyInterface);
				}
			}
		}

		entityInterface.setSemanticPropertyCollection(semanticPropertyColl);
	}

	/**
	 * @param conceptCode
	 * @param conceptDefinition
	 * @param term
	 * @param thesaurasName
	 * @param seqNo
	 * @return
	 */
	private SemanticPropertyInterface getSemanticProperty(final String conceptCode,
			final String conceptDefinition, final String term, final String thesaurasName,
			final int seqNo)
	{
		final SemanticPropertyInterface semanticProperty = DomainObjectFactory.getInstance()
				.createSemanticProperty();
		semanticProperty.setConceptCode(conceptCode);
		semanticProperty.setConceptDefinition(conceptDefinition);
		semanticProperty.setConceptPreferredName(term);
		semanticProperty.setConceptDefinitionSource(thesaurasName);
		semanticProperty.setSequenceNumber(seqNo);

		return semanticProperty;
	}

	/**
	 * @param abstractMetadataInterface
	 * @param taggedValueMap
	 * @param tag
	 */
	private void addSemanticPropertyForAttributes(final AttributeInterface attributeInterface,
			final Map<String, String> taggedValueMap)
	{
		final Collection<SemanticPropertyInterface> semanticPropertyColl = new HashSet<SemanticPropertyInterface>();
		//		Concept codes
		String conceptCode = taggedValueMap.get(XMIConstants.TAGGED_VALUE_PROPERTY_CONCEPT_CODE);
		if (conceptCode != null)
		{
			String conceptDefinition = taggedValueMap
					.get(XMIConstants.TAGGED_VALUE_PROPERTY_CONCEPT_DEFINITION);
			String term = taggedValueMap
					.get(XMIConstants.TAGGED_VALUE_PROPERTY_CONCEPT_DEFINITION_SOURCE);
			String thesarausName = taggedValueMap
					.get(XMIConstants.TAGGED_VALUE_PROPERTY_CONCEPT_PREFERRED_NAME);
			SemanticPropertyInterface semanticPropertyInterface = getSemanticProperty(conceptCode,
					conceptDefinition, term, thesarausName, 0);
			semanticPropertyColl.add(semanticPropertyInterface);

			final Set<String> tagNames = taggedValueMap.keySet();
			for (final String tagName : tagNames)
			{
				if (tagName.startsWith(XMIConstants.TAGGED_VALUE_PROPERTY_QUALIFIER_CONCEPT_CODE))
				{
					final int beginIndex = XMIConstants.TAGGED_VALUE_PROPERTY_QUALIFIER_CONCEPT_CODE
							.length();
					final String qualifierNumber = tagName.substring(beginIndex);

					conceptCode = taggedValueMap
							.get(XMIConstants.TAGGED_VALUE_PROPERTY_QUALIFIER_CONCEPT_CODE
									+ qualifierNumber);
					conceptDefinition = taggedValueMap
							.get(XMIConstants.TAGGED_VALUE_PROPERTY_QUALIFIER_CONCEPT_DEFINITION
									+ qualifierNumber);
					term = taggedValueMap
							.get(XMIConstants.TAGGED_VALUE_PROPERTY_QUALIFIER_CONCEPT_DEFINITION_SOURCE
									+ qualifierNumber);
					thesarausName = taggedValueMap
							.get(XMIConstants.TAGGED_VALUE_PROPERTY_QUALIFIER_CONCEPT_PREFERRED_NAME
									+ qualifierNumber);

					semanticPropertyInterface = getSemanticProperty(conceptCode, conceptDefinition,
							term, thesarausName, Integer.parseInt(qualifierNumber));
					semanticPropertyColl.add(semanticPropertyInterface);
				}
			}
		}

		attributeInterface.setSemanticPropertyCollection(semanticPropertyColl);
	}

	/**
	 * Gives a map having parent child information.
	 * @return Map with key as UML-id of parent class and value as list of UML-id of all children classes.
	 */
	private Map<String, List<String>> getParentVsChildrenMap(
			final List<Generalization> umlGeneralisationColl)
	{
		HashMap<String, List<String>> parentIdVsChildrenIds;
		if (umlGeneralisationColl == null)
		{
			parentIdVsChildrenIds = new HashMap<String, List<String>>(0);
		}
		else
		{
			parentIdVsChildrenIds = new HashMap<String, List<String>>(umlGeneralisationColl.size());
			for (final Generalization umlGeneralization : umlGeneralisationColl)
			{
				final String childClass = umlGeneralization.getChild().refMofId();
				final String parentClass = umlGeneralization.getParent().refMofId();
				List<String> children = parentIdVsChildrenIds.get(parentClass);
				if (children == null)
				{
					children = new ArrayList<String>();
					parentIdVsChildrenIds.put(parentClass, children);
				}
				children.add(childClass);
			}
		}

		return parentIdVsChildrenIds;
	}

	/**
	 * Converts the UML association to dynamic Extension Association.Adds it to the entity group.
	 * It replicates this association in all children of source and all children of target class.
	 * It taggs replicated association to identify them later on and mark them inherited.
	 * Also a back pointer is added to replicated association go get original association.
	 * @param umlAssociation umlAssociation to process
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void addAssociation(final UmlAssociation umlAssociation,
			final Map<String, List<String>> parentIdVsChildrenIds)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		final List<AssociationEnd> associationEnds = umlAssociation.getConnection();
		AssociationEnd sourceAssociationEnd = null;
		AssociationEnd targetAssociationEnd = null;

		final Collection<TaggedValue> taggedValueColl = umlAssociation.getTaggedValue();
		String direction = getTaggedValue(taggedValueColl, XMIConstants.TAGGED_NAME_ASSOC_DIRECTION);

		int nullAssoEnd = 0;
		for (final AssociationEnd assoEnd : associationEnds)
		{
			if (assoEnd.getName() == null)
			{
				nullAssoEnd++;
			}
		}

		if (nullAssoEnd > 0 && nullAssoEnd < 2)
		{//Only 1 name is present hence unidirectional
			for (final AssociationEnd assoEnd : associationEnds)
			{
				if (assoEnd.getName() == null)
				{
					sourceAssociationEnd = assoEnd;
				}
				else
				{
					targetAssociationEnd = assoEnd;
				}
			}
			if ("".equals(direction)
					|| direction.equalsIgnoreCase(XMIConstants.TAGGED_VALUE_ASSOC_DEST_SRC)
					|| direction.equalsIgnoreCase(XMIConstants.TAGGED_VALUE_ASSOC_SRC_DEST)
					|| direction
							.equalsIgnoreCase(XMIConstants.TAGGED_VALUE_CONTAINMENT_UNSPECIFIED))
			{
				direction = DEConstants.AssociationDirection.SRC_DESTINATION.toString();
			}
		}
		else
		{//bidirectional
			sourceAssociationEnd = associationEnds.get(0);
			targetAssociationEnd = associationEnds.get(1);
			if ("".equals(direction)
					|| direction.equalsIgnoreCase(XMIConstants.TAGGED_VALUE_ASSOC_BIDIRECTIONAL))
			{
				direction = DEConstants.AssociationDirection.BI_DIRECTIONAL.toString();
			}
			else
			{
				direction = DEConstants.AssociationDirection.SRC_DESTINATION.toString();
			}
		}
		//This block is added  because association ends are setting wrong due to above algorithm in case of
		//Importing  the exported xmi from catissue

		for (final AssociationEnd assoEnd : associationEnds)
		{
			final String assTagEntityType = getTaggedValue(assoEnd.getTaggedValue(),
					XMIConstants.TAGGED_VALUE_ASSN_ENTITY);
			if (assTagEntityType != null
					&& assTagEntityType.equalsIgnoreCase(XMIConstants.ASSN_SRC_ENTITY))
			{
				sourceAssociationEnd = assoEnd;
			}
			else if (assTagEntityType != null
					&& assTagEntityType.equalsIgnoreCase(XMIConstants.ASSN_TGT_ENTITY))
			{
				targetAssociationEnd = assoEnd;
			}
		}
		//		getAssociationEnds(sourceAssociationEnd,targetAssociationEnd,associationEnds,direction);

		final String sourceAssoTypeTV = getAssociationTypeTV(sourceAssociationEnd.getTaggedValue());

		final String destinationAssoTypeTV = getAssociationTypeTV(targetAssociationEnd
				.getTaggedValue());

		final String srcId = sourceAssociationEnd.getParticipant().refMofId();
		final EntityInterface srcEntity = umlClassIdVsEntity.get(srcId);

		final String tgtId = targetAssociationEnd.getParticipant().refMofId();
		final EntityInterface tgtEntity = umlClassIdVsEntity.get(tgtId);
		final Multiplicity srcMultiplicity = sourceAssociationEnd.getMultiplicity();
		final String sourceRoleName = sourceAssociationEnd.getName();
		XMIImportValidator.validateName(sourceRoleName, "Source Role", srcEntity.getName(),
				tgtEntity.getName());
		final RoleInterface sourceRole = getRole(srcMultiplicity, sourceRoleName, sourceAssoTypeTV);

		final Multiplicity tgtMultiplicity = targetAssociationEnd.getMultiplicity();
		final String tgtRoleName = targetAssociationEnd.getName();
		XMIImportValidator.validateName(tgtRoleName, "Target Role", srcEntity.getName(), tgtEntity
				.getName());
		final RoleInterface targetRole = getRole(tgtMultiplicity, tgtRoleName,
				destinationAssoTypeTV);

		AssociationInterface association = null;
		final Collection<AssociationInterface> existingAssociationColl = srcEntity
				.getAssociationCollection();

		XMIImportValidator.validateAssociations(entityNameVsAttributeNames, umlAssociation
				.getName(), srcEntity.getName(), tgtEntity.getName(), parentIdVsChildrenIds,
				umlClassIdVsEntity, sourceRole, targetRole);

		if (existingAssociationColl != null && !existingAssociationColl.isEmpty())
		{//EDIT Case
			association = isAssociationPresent(umlAssociation.getName(), existingAssociationColl,
					srcEntity.getName(), tgtEntity.getName(), direction, sourceRole, targetRole);
		}

		//Adding association to entity
		if (association == null)
		{//Make new Association
			association = getAssociation(srcEntity, umlAssociation.getName());
		}

		association.setSourceRole(sourceRole);
		association.setTargetEntity(tgtEntity);
		association.setTargetRole(targetRole);
		association.setConstraintProperties(DynamicExtensionsUtility
				.populateConstraintPropertiesForAssociation(association));
		//association.populateAssociationForConstraintProperties();
		if (direction.equalsIgnoreCase(DEConstants.AssociationDirection.BI_DIRECTIONAL.toString()))
		{
			association.setAssociationDirection(DEConstants.AssociationDirection.BI_DIRECTIONAL);
		}
		else
		{
			association.setAssociationDirection(DEConstants.AssociationDirection.SRC_DESTINATION);
		}

		umlAssociaionIdVsAssociation.put(umlAssociation.refMofId(), association);
		populateAssociationUIProperties(association, taggedValueColl);
	}

	/**
	 * Logic:
	 * 1. If association name is present and matches with an existing association name, edit that association
	 * 2. If association name is not present, check for other matching parameters like  source entity name, target entity name,
	 * 	  direction, source role and target role.
	 * 		a. If any one parameter does not match, make a new association
	 * 		b. If all parameters match, association has not been edited.
	 *
	 * @param umlAssociationName
	 * @param existingAssociationColl
	 * @param srcEntityName
	 * @param tgtEntityName
	 * @param direction
	 * @param sourceRole
	 * @param targetRole
	 * @return
	 */
	private AssociationInterface isAssociationPresent(final String umlAssociationName,
			final Collection<AssociationInterface> existingAssociationColl,
			final String srcEntityName, final String tgtEntityName, final String direction,
			final RoleInterface sourceRole, final RoleInterface targetRole)
	{
		AssociationInterface existingAssociation = null;
		for (final AssociationInterface existingAsso : existingAssociationColl)
		{
			if (umlAssociationName != null
					&& umlAssociationName.equalsIgnoreCase(existingAsso.getName())
					&& existingAsso.getTargetEntity().getName().equalsIgnoreCase(tgtEntityName))
			{//Since name is present, & its between the same two entities so edit this association.
				existingAssociation = existingAsso;
				break;
			}
			//If even 1 condition does not match, goto next association
			if (!existingAsso.getEntity().getName().equalsIgnoreCase(srcEntityName))
			{
				continue;
			}
			if (!existingAsso.getTargetEntity().getName().equalsIgnoreCase(tgtEntityName))
			{
				continue;
			}
			if (!existingAsso.getAssociationDirection().toString().equalsIgnoreCase(direction))
			{
				continue;
			}
			//SourecRole
			if (!existingAsso.getSourceRole().getAssociationsType().equals(
					sourceRole.getAssociationsType()))
			{
				if (direction.equalsIgnoreCase(DEConstants.AssociationDirection.BI_DIRECTIONAL
						.toString()))
				{//For bi directional association, reversing the association ends and comparing
					if (!existingAsso.getSourceRole().getAssociationsType().equals(
							targetRole.getAssociationsType()))
					{
						continue;
					}
				}
				else
				{
					continue;
				}
			}
			if (!existingAsso.getSourceRole().getMaximumCardinality().equals(
					sourceRole.getMaximumCardinality()))
			{
				if (direction.equalsIgnoreCase(DEConstants.AssociationDirection.BI_DIRECTIONAL
						.toString()))
				{//For bi directional association, reversing the association ends and comparing
					if (!existingAsso.getSourceRole().getMaximumCardinality().equals(
							targetRole.getMaximumCardinality()))
					{
						continue;
					}
				}
				else
				{
					continue;
				}
			}
			if (!existingAsso.getSourceRole().getMinimumCardinality().equals(
					sourceRole.getMinimumCardinality()))
			{
				if (direction.equalsIgnoreCase(DEConstants.AssociationDirection.BI_DIRECTIONAL
						.toString()))
				{//For bi directional association, reversing the association ends and comparing
					if (!existingAsso.getSourceRole().getMinimumCardinality().equals(
							targetRole.getMinimumCardinality()))
					{
						continue;
					}
				}
				else
				{
					continue;
				}
			}
			if (existingAsso.getSourceRole().getName() != null
					&& sourceRole.getName() != null
					&& !existingAsso.getSourceRole().getName().equalsIgnoreCase(
							sourceRole.getName()))
			{
				if (direction.equalsIgnoreCase(DEConstants.AssociationDirection.BI_DIRECTIONAL
						.toString()))
				{//For bi directional association, reversing the association ends and comparing
					if (existingAsso.getSourceRole().getName() != null
							&& targetRole.getName() != null
							&& !existingAsso.getSourceRole().getName().equalsIgnoreCase(
									targetRole.getName()))
					{
						continue;
					}
				}
				else
				{
					continue;
				}
			}
			//			TargetRole
			if (!existingAsso.getTargetRole().getAssociationsType().equals(
					targetRole.getAssociationsType()))
			{
				if (direction.equalsIgnoreCase(DEConstants.AssociationDirection.BI_DIRECTIONAL
						.toString()))
				{//For bi directional association, reversing the association ends and comparing
					if (!existingAsso.getTargetRole().getAssociationsType().equals(
							sourceRole.getAssociationsType()))
					{
						continue;
					}
				}
				else
				{
					continue;
				}
			}
			if (!existingAsso.getTargetRole().getMaximumCardinality().equals(
					targetRole.getMaximumCardinality()))
			{
				if (direction.equalsIgnoreCase(DEConstants.AssociationDirection.BI_DIRECTIONAL
						.toString()))
				{//For bi directional association, reversing the association ends and comparing
					if (!existingAsso.getTargetRole().getMaximumCardinality().equals(
							sourceRole.getMaximumCardinality()))
					{
						continue;
					}
				}
				else
				{
					continue;
				}
			}
			if (!existingAsso.getTargetRole().getMinimumCardinality().equals(
					targetRole.getMinimumCardinality()))
			{//For bi directional association, reversing the association ends and comparing
				if (direction.equalsIgnoreCase(DEConstants.AssociationDirection.BI_DIRECTIONAL
						.toString()))
				{
					if (!existingAsso.getTargetRole().getMinimumCardinality().equals(
							sourceRole.getMinimumCardinality()))
					{
						continue;
					}
				}
				else
				{
					continue;
				}
			}
			if (existingAsso.getTargetRole().getName() != null
					&& targetRole.getName() != null
					&& !existingAsso.getTargetRole().getName().equalsIgnoreCase(
							targetRole.getName()))
			{
				if (direction.equalsIgnoreCase(DEConstants.AssociationDirection.BI_DIRECTIONAL
						.toString()))
				{//For bi directional association, reversing the association ends and comparing
					if (existingAsso.getTargetRole().getName() != null
							&& sourceRole.getName() != null
							&& !existingAsso.getTargetRole().getName().equalsIgnoreCase(
									sourceRole.getName()))
					{
						continue;
					}
				}
				else
				{
					continue;
				}
			}
			//All parameters match. Hence this Association has not been edited.
			existingAssociation = existingAsso;
			break;
		}
		return existingAssociation;
	}

	/**
	 * @param taggedValueColl
	 * @param tagName
	 * @return
	 */
	private String getTaggedValue(final Collection<TaggedValue> taggedValueColl,
			final String tagName)
	{
		String val = "";
		for (final TaggedValue taggedValue : taggedValueColl)
		{
			if (taggedValue.getType() != null
					&& taggedValue.getType().getName().equalsIgnoreCase(tagName))
			{
				final Collection<String> dataValueColl = taggedValue.getDataValue();
				for (final String value : dataValueColl)
				{
					val = value;
					break;

				}

			}
		}
		return val;
	}

	/**
	 * Processes inheritance relation ship present in domain model
	 * @param parentIdVsChildrenIds Map with key as UML-id of parent class and value as list of UML-id of all children classes.
	 * @throws DynamicExtensionsSystemException
	 */
	private void processInheritance(final Map<String, List<String>> parentIdVsChildrenIds)
			throws DynamicExtensionsSystemException
	{
		for (final Entry<String, List<String>> entry : parentIdVsChildrenIds.entrySet())
		{
			final EntityInterface parent = umlClassIdVsEntity.get(entry.getKey());
			for (final String childId : entry.getValue())
			{
				final EntityInterface child = umlClassIdVsEntity.get(childId);
				child.setParentEntity(parent);
			}
		}
	}

	/**
	 * @param sourceEntity Entity to which a association is to be attached
	 * @return A association attached to given entity.
	 */
	private AssociationInterface getAssociation(final EntityInterface sourceEntity,
			final String associationName)
	{
		final AssociationInterface association = DE_FACTORY.createAssociation();
		//remove it after getting DE fix,association name should not be compulsory
		if (associationName == null || associationName.equals(""))
		{
			association.setName("AssociationName_"
					+ (sourceEntity.getAssociationCollection().size() + 1));
		}
		else
		{
			association.setName(associationName);
		}
		association.setEntity(sourceEntity);
		sourceEntity.addAssociation(association);
		return association;
	}

	/**
	 * Creates Role for the input UMLAssociationEdge
	 * @param edge UML Association Edge to process
	 * @return the Role for given UML Association Edge
	 */
	private RoleInterface getRole(final Multiplicity srcMultiplicity, final String sourceRoleName,
			final String associationType)
	{
		final Collection<MultiplicityRange> rangeColl = srcMultiplicity.getRange();
		int minCardinality = 0;
		int maxCardinality = 0;
		for (final MultiplicityRange range : rangeColl)
		{
			minCardinality = range.getLower();
			maxCardinality = range.getUpper();
		}

		final RoleInterface role = DE_FACTORY.createRole();
		if (associationType != null
				&& (associationType
						.equalsIgnoreCase(XMIConstants.TAGGED_VALUE_CONTAINMENT_UNSPECIFIED) || associationType
						.equalsIgnoreCase(XMIConstants.TAGGED_VALUE_CONTAINMENT_NOTSPECIFIED)))
		{
			role.setAssociationsType(DEConstants.AssociationType.ASSOCIATION);
		}
		else
		{
			role.setAssociationsType(DEConstants.AssociationType.CONTAINTMENT);
		}
		role.setName(sourceRoleName);
		role.setMaximumCardinality(getCardinality(maxCardinality));
		role.setMinimumCardinality(getCardinality(minCardinality));
		return role;
	}

	/**
	 * Gets dynamic extension's Cardinality enumeration for passed integer value.
	 * @param cardinality integer value of cardinality.
	 * @return Dynamic Extension's Cardinality enumeration
	 */
	private DEConstants.Cardinality getCardinality(final int cardinality)
	{
		DEConstants.Cardinality miltiplicity;
		if (cardinality == 0)
		{
			miltiplicity = DEConstants.Cardinality.ZERO;
		}
		else if (cardinality == 1)
		{
			miltiplicity = DEConstants.Cardinality.ONE;
		}
		else
		{
			miltiplicity = DEConstants.Cardinality.MANY;
		}
		return miltiplicity;
	}

	/**
	 * @param entityInterface
	 * @param controlModel
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private ContainerInterface createNewContainer(final EntityInterface entityInterface)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		final ContainerInterface containerInterface = DE_FACTORY.createContainer();
		containerInterface.setCaption(entityInterface.getName());
		containerInterface.setAbstractEntity(entityInterface);

		//Adding Required field indicator
		containerInterface.setRequiredFieldIndicatior("*");
		containerInterface.setRequiredFieldWarningMessage("indicates required fields.");

		final Collection<AbstractAttributeInterface> abstractAttributeCollection = entityInterface
				.getAbstractAttributeCollection();
		Integer sequenceNumber = Integer.valueOf(0);
		ControlInterface controlInterface;
		for (final AbstractAttributeInterface abstractAttributeInterface : abstractAttributeCollection)
		{
			final ControlsModel controlModel = new ControlsModel();
			controlInterface = getControlForAttribute(abstractAttributeInterface, controlModel);
			if (controlInterface != null) //no control created for id attribute
			{
				sequenceNumber++;
				controlInterface.setSequenceNumber(sequenceNumber);
				containerInterface.addControl(controlInterface);
				controlInterface.setParentContainer((Container) containerInterface);
			}
		}
		return containerInterface;
	}

	/**
	 * @param entityName
	 * @return
	 */
	private ContainerInterface getContainer(final String entityName)
	{
		ContainerInterface entityContainer = null;
		if (retrievedContainerList != null && !retrievedContainerList.isEmpty())
		{
			for (final ContainerInterface container : retrievedContainerList)
			{
				if (container.getCaption().equals(entityName))
				{
					entityContainer = container;
					break;
				}
			}
		}
		return entityContainer;
	}

	/**
	 * @param entityName
	 * @return
	 */
	private EntityInterface getEntity(final String entityName)
	{
		EntityInterface entity = null;
		if (retrievedContainerList != null && !retrievedContainerList.isEmpty())
		{
			for (final ContainerInterface container : retrievedContainerList)
			{
				if (container.getAbstractEntity().getName().equals(entityName))
				{
					entity = (EntityInterface) container.getAbstractEntity();
					break;
				}
			}
		}
		return entity;
	}

	/**
	 * This method creates a container object.
	 * @param entityInterface
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	protected void createContainer(final EntityInterface entityInterface)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		ContainerInterface containerInterface = getContainer(entityInterface.getName());

		/*DynamicExtensionsUtility.getContainerByCaption(entityInterface.getName()); */
		if (containerInterface == null)//Add
		{

			containerInterface = createNewContainer(entityInterface);
		}
		else
		{//Edit
			editEntityAndContainer(containerInterface, entityInterface);

			//Populating Attributes and Controls
			//			Collection<AbstractAttributeInterface> editedAttributeColl = entityInterface
			//				.getAbstractAttributeCollection();
			//			Collection<AbstractAttributeInterface> originalAttributeColl = ((EntityInterface) containerInterface
			//					.getAbstractEntity()).getAbstractAttributeCollection();

			/* Bug Id: 7209
			 * editedAttributeColl - contains new attribute that needs to be added
			 * originalAttributeColl - contains only original attributes from database
			 * As both these objects point to the same entity object hence both collections have same attributes.
			 * Hence a new object for editedAttributeColl is created and the
			 * new attribute is removed from the originalAttributeColl on the basis of objects having id as null
			 */
			final Collection<AbstractAttributeInterface> savedAssociation = new ArrayList<AbstractAttributeInterface>();
			final Collection<AbstractAttributeInterface> editedAttributeColl = new ArrayList<AbstractAttributeInterface>();
			editedAttributeColl.addAll(entityInterface.getAbstractAttributeCollection());

			final Collection<AbstractAttributeInterface> originalAttributeColl = ((EntityInterface) containerInterface
					.getAbstractEntity()).getAbstractAttributeCollection();

			final Iterator<AbstractAttributeInterface> abstrAttrIter = originalAttributeColl
					.iterator();
			while (abstrAttrIter.hasNext())
			{
				final AbstractAttributeInterface originalAttr = abstrAttrIter.next();
				if (originalAttr.getId() == null)
				{
					/*
					 * Bug Id:7316
					 * Here the new associations were also getting deleted from the entity object
					 * Hence, new association objects are saved in a list and then removed from entity
					 * so that they can be added again to the entity object
					 */
					if (originalAttr instanceof AssociationInterface)
					{
						savedAssociation.add(originalAttr);
					}

					abstrAttrIter.remove();
				}
			}
			final Collection<AbstractAttributeInterface> attributesToRemove = new HashSet<AbstractAttributeInterface>();
			for (final AbstractAttributeInterface editedAttribute : editedAttributeColl)
			{
				if (editedAttribute.getName().equalsIgnoreCase(Constants.SYSTEM_IDENTIFIER)
						&& xmiConfigurationObject.isAddIdAttribute())
				{
					// We dont edit "id" attribute as it is the system identifier.
					continue;
				}
				if (editedAttribute instanceof AssociationInterface)
				{
					//When association direction is changed from bi-directional to src-destination, this method removes
					//the redundant association.
					removeRedundantAssociation(editedAttribute, attributesToRemove);
				}
				final ControlsModel controlModel = new ControlsModel();
				final boolean isAttrPresent = getAttrToEdit(originalAttributeColl, editedAttribute);

				if (isAttrPresent)
				{//Edit
					editAttributeAndControl(controlModel, editedAttribute, containerInterface);
				}
				else
				{//Add Attribute
					addAttributeAndControl(controlModel, editedAttribute, containerInterface);
					//					Duplicate attributes have been created since we have created attribute in the method create attributes also
					//Do not create attributes above but create them here.
					if (!(editedAttribute instanceof AssociationInterface))
					{
						attributesToRemove.add(editedAttribute);
					}
				}
				controlModel.setCaption(editedAttribute.getName());
				controlModel.setName(editedAttribute.getName());
				setTaggedValue(controlModel, editedAttribute);

				// If original attribute's default value is "--Select--", and attribute default value of
				// control model is "" since no tag has been specified, then keep the
				// attribute default value as "--Select--"
				setDefaultValueForAttribute(controlModel, originalAttributeColl, editedAttribute);

				//Not for Containment Association Control
				if (!(editedAttribute instanceof AssociationInterface))
				{
					final AttributeInterface attribute = (AttributeInterface) editedAttribute;
					controlModel.setIsPrimaryKey(attribute.getIsPrimaryKey());
					controlModel.setIsNullable(attribute.getIsNullable());
					controlModel.setColumnName(attribute.getColumnProperties().getName());

					final ApplyFormControlsProcessor applyFormControlsProcessor = ApplyFormControlsProcessor
							.getInstance();
					applyFormControlsProcessor.addControlToForm(containerInterface, controlModel,
							controlModel, entityInterface.getEntityGroup());
					populateAttributeForPrimaryKey(entityInterface.getAttributeByName(controlModel
							.getName()), controlModel);

				}
			}
			/*Bug id:7316
			 * new associations are added again to the entity
			 */
			for (final AbstractAttributeInterface savedAssoc : savedAssociation)
			{
				entityInterface.addAbstractAttribute(savedAssoc);
			}
			//Since we are creating attributes in createAttributes method and also in applyFormControlsProcessor.addControlToForm method
			//duplicate attributes have been created. Hence removing the duplicate attributes.
			editedAttributeColl.removeAll(attributesToRemove);
		}
		entityInterface.addContainer(containerInterface);
		final List<ContainerInterface> containerList = new ArrayList<ContainerInterface>();
		containerList.add(containerInterface);
		entityNameVsContainers.put(entityInterface.getName(), containerList);
	}

	/**
	 * This method sets the default value of attribute in following scenario. If original attribute's
	 * default value is "--Select--", and attribute default value of control model is ""
	 * since no tag has been specified, then keep the attribute default value as "--Select--"
	 * @param controlModel
	 * @param originalAttributes
	 * @param editedAttribute
	 */
	private void setDefaultValueForAttribute(final ControlsModel controlModel,
			final Collection<AbstractAttributeInterface> originalAttributes,
			final AbstractAttributeInterface editedAttribute)
	{
		final String ctrlModelAttrDefaultVal = controlModel.getAttributeDefaultValue();

		if (ctrlModelAttrDefaultVal != null && ctrlModelAttrDefaultVal.equalsIgnoreCase(""))
		{
			for (final AbstractAttributeInterface attribute : originalAttributes)
			{
				if (attribute instanceof AttributeInterface
						&& attribute.getName().equalsIgnoreCase(editedAttribute.getName()))
				{
					final AttributeTypeInformationInterface attrTypeInfo = ((AttributeInterface) attribute)
							.getAttributeTypeInformation();
					final PermissibleValueInterface defaultPermissibleValue = attrTypeInfo
							.getDefaultValue();

					if (defaultPermissibleValue != null
							&& defaultPermissibleValue.getValueAsObject() != null)
					{

						final String defaultValueAsString = defaultPermissibleValue
								.getValueAsObject().toString();
						if (defaultValueAsString != null
								&& !defaultValueAsString.equalsIgnoreCase(""))
						{
							controlModel.setAttributeDefaultValue(defaultValueAsString);
						}

					}
					break;
				}
			}
		}
	}

	/**
	 *
	 * @param attribute
	 * @param controlModel
	 */
	private void populateAttributeForPrimaryKey(final AttributeInterface attribute,
			final ControlsModel controlModel)
	{
		if (attribute != null)
		{
			attribute.setIsPrimaryKey(controlModel.getIsPrimaryKey());
			attribute.setIsNullable(controlModel.getIsNullable());
			attribute.getColumnProperties().setName(controlModel.getColumnName());
		}

	}

	/**
	 * @param editedAttribute
	 * @param attributesToRemove
	 */
	private void removeRedundantAssociation(final AbstractAttributeInterface editedAttribute,
			final Collection<AbstractAttributeInterface> attributesToRemove)
	{
		final AssociationInterface association = (AssociationInterface) editedAttribute;
		final EntityInterface originalTargetEntity = getEntity(association.getTargetEntity()
				.getName());
		if (originalTargetEntity != null)
		{
			final Collection<AssociationInterface> targetEntityAssociationColl = association
					.getTargetEntity().getAssociationCollection();
			//Removing redundant association
			for (final AssociationInterface targetAsso : targetEntityAssociationColl)
			{
				if (targetAsso.getTargetEntity().getName().equalsIgnoreCase(
						association.getEntity().getName()))
				{
					AssociationInterface originalTargetAssociation = null;
					for (final AssociationInterface originalTgtAsso : originalTargetEntity
							.getAssociationCollection())
					{
						if (targetAsso.getName().equalsIgnoreCase(originalTgtAsso.getName()))
						{
							originalTargetAssociation = originalTgtAsso;
							break;
						}
					}

					if (targetAsso.getAssociationDirection().equals(
							DEConstants.AssociationDirection.SRC_DESTINATION)
							&& originalTargetAssociation != null
							&& originalTargetAssociation.getAssociationDirection().equals(
									DEConstants.AssociationDirection.BI_DIRECTIONAL))
					{//We need to remove system generated association if direction has been changed from bi directional to source destination
						attributesToRemove.add(editedAttribute);
					}
				}
			}
		}
	}

	/**
	 * @param controlModel
	 * @param editedAttribute
	 * @param containerInterface
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void addAttributeAndControl(final ControlsModel controlModel,
			final AbstractAttributeInterface editedAttribute,
			final ContainerInterface containerInterface) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		controlModel.setControlOperation(ProcessorConstants.OPERATION_ADD);
		final ControlInterface newcontrol = getControlForAttribute(editedAttribute, controlModel);
		if (newcontrol != null)
		{ //no control created for id attribute
			final int sequenceNumber = DynamicExtensionsUtility
					.getSequenceNumberForNextControl(containerInterface);
			newcontrol.setSequenceNumber(sequenceNumber);
			//containerInterface.addControl(newcontrol);
			newcontrol.setParentContainer((Container) containerInterface);

			final String userSelectedTool = DynamicExtensionsUtility.getControlName(newcontrol);
			controlModel.setUserSelectedTool(userSelectedTool);
			//For Text Control
			if (newcontrol instanceof TextFieldInterface)
			{
				controlModel.setColumns(Integer.valueOf(0));
			}
			//For creating Association or Attribute
			populateControlModel(controlModel, editedAttribute);

			//if(controlModel.getUserSelectedTool().equalsIgnoreCase(ProcessorConstants.ADD_SUBFORM_CONTROL))
			if (editedAttribute instanceof AssociationInterface)
			{
				containerInterface.addControl(newcontrol);
			}
		}
	}

	/**
	 * @param originalAttributeColl
	 * @param controlModel
	 * @param editedAttribute
	 * @param containerInterface
	 * @param loadFormControlsProcessor
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void editAttributeAndControl(final ControlsModel controlModel,
			final AbstractAttributeInterface editedAttribute,
			final ContainerInterface containerInterface) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		controlModel.setControlOperation(ProcessorConstants.OPERATION_EDIT);

		final Collection<ControlInterface> originalControlColl = containerInterface
				.getControlCollection();
		ControlInterface originalControlObj = null;
		for (final ControlInterface originalcontrol : originalControlColl)
		{
			if (originalcontrol.getBaseAbstractAttribute().getName().equalsIgnoreCase(
					editedAttribute.getName()))
			{
				originalControlObj = originalcontrol;
				break;
			}
		}

		if (originalControlObj != null)
		{
			originalControlObj.setBaseAbstractAttribute(editedAttribute);
			//This method wil give us populated ControlUIBean and AttributeUIBean with original control object corresponding to edited attribute
			if (!(editedAttribute instanceof AssociationInterface))
			{
				final LoadFormControlsProcessor loadFormControlsProcessor = LoadFormControlsProcessor
						.getInstance();
				loadFormControlsProcessor.editControl(originalControlObj, controlModel,
						controlModel);

				controlModel
						.setSelectedControlId(originalControlObj.getSequenceNumber().toString());

			}
			//controlModel.setCaption(originalControlObj.getCaption());
		}
	}

	/**
	 * Method to set tagged values(max length, precision, date format) in control model
	 * @throws DynamicExtensionsSystemException
	 */
	private void setTaggedValue(final ControlsModel controlModel,
			final AbstractAttributeInterface editedAttribute)
			throws DynamicExtensionsSystemException
	{
		if (!(editedAttribute instanceof AssociationInterface))
		{
			final Map<String, String> taggedValueMap = attrVsMapTagValues.get(editedAttribute);
			if (taggedValueMap != null)
			{
				// max length of string from tagged value
				final String maxLen = getMaxLengthTagValue(taggedValueMap);
				controlModel.setAttributeSize(maxLen);

				//date format tagged value
				final String format = getDateFormatTagValue(taggedValueMap);
				controlModel.setFormat(format);
				final String dateFormat = DynamicExtensionsUtility.getDateFormat(format);
				controlModel.setDateValueType(dateFormat);

				// precision tagged value
				final Integer precision = getPrecisionTagValue(taggedValueMap,
						((AttributeInterface) editedAttribute).getAttributeTypeInformation());
				controlModel.setAttributeDecimalPlaces(precision.toString());

				//Added by Prashant
				//password tagged value
				final String password = getPasswordTagValue(taggedValueMap);
				controlModel.setIsPassword(Boolean.parseBoolean(password));

				//Single/Multiline(Number of Lines) tagged Value
				setMultilineTaggedValue(taggedValueMap, controlModel);

				//NoOfColumns tagged Value
				final String width = getDisplayWidthTagValue(taggedValueMap);
				controlModel.setColumns(Integer.parseInt(width));

				//defaultValue tagged Value
				final String defaultValue = getDefaultValueTagValue(taggedValueMap);
				controlModel.setAttributeDefaultValue(defaultValue);

				if (defaultValue != null && !defaultValue.equalsIgnoreCase(""))
				{
					//dateValueType  value
					controlModel.setDateValueType(ProcessorConstants.DATE_VALUE_SELECT);
				}
				//URL tagged value
				final String url = getUrlTagValue(taggedValueMap);
				controlModel.setIsUrl(Boolean.parseBoolean(url));

				//PHI Attribute tagged value
				final String PHIAttribute = getPHIAttributeTagValue(taggedValueMap);
				controlModel.setAttributeIdentified(PHIAttribute);

				//FileFormats tagged value
				final String[] fileFormats = getFileFormatsTagValue(taggedValueMap);
				controlModel.setFileFormats(fileFormats);

				//For list box or combo box
				setMultiselectTaggedValue(taggedValueMap, controlModel);

				//For combo box,separator tagged value
				final String separator = getSeparatorTagValue(taggedValueMap);
				controlModel.setSeparator(separator);

				//set Explicit validation Rules
				setExplicitValidationRules(taggedValueMap, controlModel);
			}

		}
	}

	/**
	 *
	 * @param taggedValueMap
	 * @param controlModel
	 */
	private void setMultiselectTaggedValue(final Map<String, String> taggedValueMap,
			final ControlsModel controlModel)
	{
		if (taggedValueMap.containsKey(XMIConstants.TAGGED_VALUE_MULTISELECT))
		{
			controlModel.setIsMultiSelect(true);
			final String listBoxHeight = getListBoxHeightTagValue(taggedValueMap);
			controlModel.setAttributeNoOfRows(listBoxHeight);
		}

	}

	/**
	 *
	 * @param taggedValueMap
	 * @return
	 */
	private String getListBoxHeightTagValue(final Map<String, String> taggedValueMap)
	{
		String listBoxHeight = taggedValueMap.get(XMIConstants.TAGGED_VALUE_MULTISELECT);
		if (listBoxHeight == null || listBoxHeight.trim().equals(""))
		{
			listBoxHeight = Integer
					.toString(edu.common.dynamicextensions.ui.util.Constants.DEFAULT_ROW_SIZE);
		}
		return listBoxHeight;
	}

	/**
	 *
	 * @param taggedValueMap
	 * @return
	 */
	private String getSeparatorTagValue(final Map<String, String> taggedValueMap)
	{
		String separator = taggedValueMap.get(XMIConstants.TAGGED_VALUE_SEPARATOR);
		if (separator == null || separator.trim().equals(""))
		{
			separator = null;
		}
		return separator;
	}

	/**
	 *
	 * @param taggedValueMap
	 * @param controlModel
	 */
	private void setMultilineTaggedValue(final Map<String, String> taggedValueMap,
			final ControlsModel controlModel)
	{
		if (taggedValueMap != null
				&& taggedValueMap.containsKey(XMIConstants.TAGGED_VALUE_MULTILINE))
		{
			controlModel.setLinesType(XMIConstants.MULTILINE);
			final String noOFLines = getNoOfRowsTagValue(taggedValueMap);
			controlModel.setRows(Integer.parseInt(noOFLines));
		}
	}

	/**
	 *
	 * @param taggedValueMap
	 * @return
	 */
	private String[] getFileFormatsTagValue(final Map<String, String> taggedValueMap)
	{
		final String fileFormat = taggedValueMap.get(XMIConstants.TAGGED_VALUE_FILE_FORMATS);
		String[] fileformats = null;
		if (fileFormat != null)
		{
			final StringTokenizer stringTokenizer = new StringTokenizer(fileFormat, ",");
			stringTokenizer.countTokens();
			fileformats = fileFormat.split(",");
		}
		return fileformats;
	}

	/**
	 * @param taggedValueMap
	 * @return
	 */
	private boolean isMultiselectTagValue(final Map<String, String> taggedValueMap)
	{
		boolean isMultiselect = false;
		if (taggedValueMap != null
				&& taggedValueMap.containsKey(XMIConstants.TAGGED_VALUE_MULTISELECT))
		{
			isMultiselect = true;
		}
		return isMultiselect;
	}

	/**
	 * @param taggedValueMap
	 * @return
	 */
	private String getMultiselectTagValue(final Map<String, String> taggedValueMap)
	{
		String tagValue = null;
		if (taggedValueMap != null
				&& taggedValueMap.containsKey(XMIConstants.TAGGED_VALUE_MULTISELECT))
		{
			tagValue = taggedValueMap.get(XMIConstants.TAGGED_VALUE_MULTISELECT);
		}
		return tagValue;
	}

	/**
	 *
	 * @param taggedValueMap
	 * @return
	 */
	private String getPHIAttributeTagValue(final Map<String, String> taggedValueMap)
	{
		String PHIAttribute = null;
		if (taggedValueMap == null)
		{
			PHIAttribute = "false";
		}
		else
		{
			PHIAttribute = taggedValueMap.get(XMIConstants.TAGGED_VALUE_PHI_ATTRIBUTE);
			if (PHIAttribute == null || PHIAttribute.trim().equals(""))
			{
				PHIAttribute = "false";
			}
		}
		return PHIAttribute;
	}

	/**
	 *
	 * @param taggedValueMap
	 * @return
	 */
	private String getDisplayWidthTagValue(final Map<String, String> taggedValueMap)
	{
		String width = "";
		if (taggedValueMap != null)
		{
			taggedValueMap.get(XMIConstants.TAGGED_VALUE_DISPLAY_WIDTH);
		}
		if (width == null || width.trim().equals(""))
		{
			width = Integer
					.toString(edu.common.dynamicextensions.ui.util.Constants.DEFAULT_COLUMN_SIZE);
		}
		if (width != null && Integer.parseInt(width) > 999)
		{
			width = XMIConstants.MAX_DISPLAY_LENGTH_LIMIT;
		}
		return width;
	}

	/**
	 * @param taggedValueMap
	 * @return
	 */
	private String getMaxLengthTagValue(final Map<String, String> taggedValueMap)
	{

		String maxLen = "";
		if (taggedValueMap != null)
		{
			maxLen = taggedValueMap.get(XMIConstants.TAGGED_VALUE_MAX_LENGTH);
		}
		if (maxLen == null || maxLen.trim().equals(""))
		{
			maxLen = XMIConstants.DEFAULT_TEXT_FIELD_MAX_LENGTH;
		}
		if (maxLen != null
				&& Integer.parseInt(maxLen) > Integer.valueOf(XMIConstants.MAX_LENGTH_LIMIT))
		{
			maxLen = XMIConstants.MAX_LENGTH_LIMIT;
		}
		return maxLen;
	}

	/**
	 * @param taggedValueMap
	 * @return
	 */
	private String getDateFormatTagValue(final Map<String, String> taggedValueMap)
	{
		String format = taggedValueMap.get(XMIConstants.TAGGED_VALUE_DATE_FORMAT);
		if (format == null || format.trim().equals(""))
		{
			format = ProcessorConstants.DATE_FORMAT_OPTION_DATEONLY;
		}

		return format;
	}

	/**
	 *
	 * @param taggedValueMap
	 * @return
	 */
	private String getNoOfRowsTagValue(final Map<String, String> taggedValueMap)
	{
		String noOFRows = taggedValueMap.get(XMIConstants.TAGGED_VALUE_MULTILINE);
		if (noOFRows == null || noOFRows.trim().equals(""))
		{
			noOFRows = Integer
					.toString(edu.common.dynamicextensions.ui.util.Constants.DEFAULT_ROW_SIZE);
		}
		return noOFRows;
	}

	/**
	 *
	 * @param taggedValueMap
	 * @return
	 */
	private String getDefaultValueTagValue(final Map<String, String> taggedValueMap)
	{
		String defaultValue = "";
		if (taggedValueMap != null)
		{
			defaultValue = taggedValueMap.get(XMIConstants.TAGGED_VALUE_DEFAULT_VALUE);
			if (defaultValue == null || defaultValue.trim().equals(""))
			{
				defaultValue = "";
			}
		}
		return defaultValue;
	}

	/**
	 * @param attributeInterface
	 * @param taggedValueMap
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private String getDefaultValueForBooleanTagValue(
			final AbstractAttributeInterface abstractAttributeInterface,
			final Map<String, String> taggedValueMap) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException
	{

		final String defaultValue = taggedValueMap.get(XMIConstants.TAGGED_VALUE_DEFAULT_VALUE);
		String boolValue = ProcessorConstants.FALSE;
		if (defaultValue != null && !defaultValue.trim().equals(""))
		{
			XMIImportValidator.validateDefBooleanValue(abstractAttributeInterface, defaultValue);
			boolValue = defaultValue;
		}
		return boolValue;
	}

	/**
	 * By Prashant
	 * @param taggedValueMap
	 * @param attributeTypeInformation
	 * @return
	 */
	private String getPasswordTagValue(final Map<String, String> taggedValueMap)
	{
		String password = "";
		if (taggedValueMap != null)
		{
			taggedValueMap.get(XMIConstants.TAGGED_VALUE_PASSWORD);
		}
		if (password == null || password.trim().equals(""))
		{
			password = "false";
		}
		return password;
	}

	/**
	 *
	 * @param taggedValueMap
	 * @return
	 */
	private String getUrlTagValue(final Map<String, String> taggedValueMap)
	{
		String url = "";
		if (taggedValueMap != null)
		{
			taggedValueMap.get(XMIConstants.TAGGED_VALUE_URL);
		}
		if (url == null || url.trim().equals(""))
		{
			url = "false";
		}
		return url;
	}

	/**
	 * @param precision
	 * @param editedAttribute
	 * @throws DynamicExtensionsSystemException
	 */
	private Integer getPrecisionTagValue(final Map<String, String> taggedValueMap,
			final AttributeTypeInformationInterface attrTypeInfo)
			throws DynamicExtensionsSystemException
	{

		String precision = "";
		if (taggedValueMap != null)
		{
			precision = taggedValueMap.get(XMIConstants.TAGGED_VALUE_PRECISION);
		}

		Integer precisionDigits = null;

		if (precision == null || precision.trim().equals(""))
		{
			if (attrTypeInfo instanceof FloatAttributeTypeInformation)
			{
				precisionDigits = Integer
						.valueOf(edu.common.dynamicextensions.ui.util.Constants.FLOAT_PRECISION);
			}
			else if (attrTypeInfo instanceof DoubleAttributeTypeInformation)
			{
				precisionDigits = Integer
						.valueOf(edu.common.dynamicextensions.ui.util.Constants.DOUBLE_PRECISION);
			}
			else
			{
				precisionDigits = Integer
						.valueOf(edu.common.dynamicextensions.ui.util.Constants.ZERO);
			}
		}
		else
		{
			precisionDigits = Integer.parseInt(precision);
			if (precisionDigits.intValue() > edu.common.dynamicextensions.ui.util.Constants.DOUBLE_PRECISION)
			{
				throw new DynamicExtensionsSystemException(
						"Precision can at maximum be 15 owing to database constraints.");
			}
		}

		return precisionDigits;
	}

	/**
	 * @param containerInterface
	 * @param entityInterface
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void editEntityAndContainer(final ContainerInterface containerInterface,
			final EntityInterface entityInterface) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		final ContainerProcessor containerProcessor = ContainerProcessor.getInstance();
		final EntityProcessor entityProcessor = EntityProcessor.getInstance();
		final ContainerModel containerModel = new ContainerModel();

		//Setting Edited entity name as caption for container.
		//Also not setting parentform to avoid unnecessary DB call as base container is already present in the container object
		containerModel.setFormName(entityInterface.getName());
		if (entityInterface.isAbstract())
		{
			containerModel.setIsAbstract("true");
		}
		//Container Object is now populated
		containerProcessor.populateContainerInterface(containerInterface, containerModel);

		containerModel.setFormDescription(entityInterface.getDescription());
		//Entity Object is now populated
		entityProcessor.addEntity(containerModel, (EntityInterface) containerInterface
				.getAbstractEntity());
	}

	/**
	 * @param originalAttributeColl
	 * @param editedAttribute
	 * @return
	 */
	private boolean getAttrToEdit(
			final Collection<AbstractAttributeInterface> originalAttributeColl,
			final AbstractAttributeInterface editedAttribute)
	{
		boolean isPresent = false;
		for (final AbstractAttributeInterface originalAttribute : originalAttributeColl)
		{
			if (editedAttribute.getName().equalsIgnoreCase(originalAttribute.getName()))
			{
				isPresent = true;
				break;
			}
		}
		return isPresent;
	}

	/**
	 * @param controlModel
	 * @param editedAttribute
	 */
	private void populateControlModel(final ControlsModel controlModel,
			final AbstractAttributeInterface editedAttribute)
	{
		if (editedAttribute instanceof AssociationInterface)
		{
			controlModel.setDisplayChoice(ProcessorConstants.DISPLAY_CHOICE_LOOKUP);
		}
		else
		{
			final AttributeInterface attribute = (AttributeInterface) editedAttribute;

			final AttributeTypeInformationInterface attributeTypeInfo = attribute
					.getAttributeTypeInformation();
			if (attributeTypeInfo instanceof DateAttributeTypeInformation)
			{
				controlModel.setDataType(ProcessorConstants.DATATYPE_DATE);
			}
			else if (attributeTypeInfo instanceof StringAttributeTypeInformation)
			{
				controlModel.setDataType(ProcessorConstants.DATATYPE_STRING);
			}
			else if (attributeTypeInfo instanceof ByteArrayAttributeTypeInformation)
			{
				controlModel.setDataType(ProcessorConstants.DATATYPE_BYTEARRAY);
			}
			else if (attributeTypeInfo instanceof BooleanAttributeTypeInformation)
			{
				controlModel.setDataType(ProcessorConstants.DATATYPE_BOOLEAN);
			}
			else if (attributeTypeInfo instanceof IntegerAttributeTypeInformation)
			{
				controlModel.setDataType(ProcessorConstants.DATATYPE_INTEGER);
			}
			else if (attributeTypeInfo instanceof LongAttributeTypeInformation)
			{
				controlModel.setDataType(ProcessorConstants.DATATYPE_LONG);
			}
			else if (attributeTypeInfo instanceof FloatAttributeTypeInformation)
			{
				controlModel.setDataType(ProcessorConstants.DATATYPE_FLOAT);
			}
			else if (attributeTypeInfo instanceof DoubleAttributeTypeInformation)
			{
				controlModel.setDataType(ProcessorConstants.DATATYPE_DOUBLE);
			}
		}
	}

	/**
	 * This method add the parent container to the child container for Generalization.
	 * @param parentIdVsChildrenIds
	 */
	protected void postProcessInheritence(final Map<String, List<String>> parentIdVsChildrenIds)
	{
		for (final Entry<String, List<String>> entry : parentIdVsChildrenIds.entrySet())
		{
			final EntityInterface parent = umlClassIdVsEntity.get(entry.getKey());

			final List parentContainerList = entityNameVsContainers.get(parent.getName());
			ContainerInterface parentContainer;
			if (parentContainerList == null || parentContainerList.isEmpty())
			{
				parentContainer = getContainer(parent.getName());
			}
			else
			{
				parentContainer = (ContainerInterface) parentContainerList.get(0);
			}
			for (final String childId : entry.getValue())
			{
				final EntityInterface child = umlClassIdVsEntity.get(childId);

				final List childContainerList = entityNameVsContainers.get(child.getName());
				ContainerInterface childContainer;
				if (childContainerList == null || childContainerList.isEmpty())
				{
					childContainer = getContainer(child.getName());
				}
				else
				{
					childContainer = (ContainerInterface) childContainerList.get(0);
				}

				childContainer.setBaseContainer(parentContainer);
			}
		}
	}

	/**
	 * This method adds the target container to the containment association control
	 */
	protected void addControlsForAssociation()
	{
		final Set<String> entityIdKeySet = entityNameVsContainers.keySet();
		for (final String entityId : entityIdKeySet)
		{
			final List containerList = entityNameVsContainers.get(entityId);
			final ContainerInterface containerInterface = (ContainerInterface) containerList.get(0);
			final Collection<ControlInterface> controlCollection = containerInterface
					.getControlCollection();

			for (final ControlInterface controlInterface : controlCollection)
			{
				if (controlInterface instanceof ContainmentAssociationControl)
				{
					final ContainmentAssociationControl containmentAssociationControl = (ContainmentAssociationControl) controlInterface;
					final AssociationInterface associationInterface = (AssociationInterface) controlInterface
							.getBaseAbstractAttribute();

					final String targetEntityId = associationInterface.getTargetEntity().getName();

					final List targetContainerInterfaceList = entityNameVsContainers
							.get(targetEntityId);

					//					TODO remove this condition to delete association with deleted or renamed entities.
					//getting container corresponding to renamed or deleted entity which is associated with some association from the retrieved entity group
					ContainerInterface targetContainerInterface;
					if (targetContainerInterfaceList == null
							|| targetContainerInterfaceList.isEmpty())
					{
						targetContainerInterface = getContainer(targetEntityId);
					}
					else
					{
						targetContainerInterface = (ContainerInterface) targetContainerInterfaceList
								.get(0);
					}
					containmentAssociationControl.setContainer(targetContainerInterface);
				}
			}
		}
	}

	/**
	 *
	 * @param abstractAttributeInterface
	 * @param controlModel
	 * @return
	 * This method creates a control for the attribute.
	 * @throws DynamicExtensionsApplicationException
	 */
	private ControlInterface getControlForAttribute(
			final AbstractAttributeInterface abstractAttributeInterface,
			final ControlsModel controlModel) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		ControlInterface controlInterface = null;
		final AttributeProcessor attributeProcessor = AttributeProcessor.getInstance();
		final ControlProcessor controlProcessor = ControlProcessor.getInstance();

		if (abstractAttributeInterface instanceof AssociationInterface)
		{

			final AssociationInterface associationInterface = (AssociationInterface) abstractAttributeInterface;
			final Map<String, String> taggedValueMap = associationVsMapTagValues
					.get(associationInterface);
			//If association is system generated,its taggedvaluemap is null ,so return null instead of processing further.
			//This scenario occurs while importing static model's xmi,like catissuesuite through DE
			if (associationInterface.getIsSystemGenerated())
			{
				return null;

			}
			if (associationInterface.getSourceRole().getAssociationsType().compareTo(
					AssociationType.CONTAINTMENT) == 0)
			{
				// This line is for containment association.
				if (isMultiselectTagValue(taggedValueMap))
				{//List box for 1 to many or many to many relationship
					controlInterface = DE_FACTORY.createListBox();
					setMultiselectTaggedValue(taggedValueMap, controlModel);
					((ListBoxInterface) controlInterface).setIsMultiSelect(controlModel
							.getIsMultiSelect());
					((ListBoxInterface) controlInterface).setNoOfRows(controlModel.getRows());
				}
				else
				{
					controlInterface = DE_FACTORY.createContainmentAssociationControl();
					associationInterface.getSourceRole().setAssociationsType(
							AssociationType.CONTAINTMENT);
					associationInterface.getTargetRole().setAssociationsType(
						AssociationType.CONTAINTMENT);
				}
			}
			else
			{
				// this is for Linking Association
				// if source maxcardinality or target  maxcardinality or both == -1, then control is listbox.
				// int  sourceMaxCardinality = associationInterface.getSourceRole().getMaximumCardinality().getValue().intValue();
				controlModel.setDisplayChoice(ProcessorConstants.DISPLAY_CHOICE_LOOKUP);
				String userSelectedControlName;
				//				int targetMaxCardinality = 0;
				//				if (associationInterface.getTargetRole() != null && associationInterface.getTargetRole().getMaximumCardinality() != null)
				//				{
				//					targetMaxCardinality = associationInterface.getTargetRole().getMaximumCardinality().getValue().intValue();
				//				}
				//if (targetMaxCardinality == -1)

				if (isMultiselectTagValue(taggedValueMap))
				{//List box for 1 to many or many to many relationship
					userSelectedControlName = ProcessorConstants.LISTBOX_CONTROL;
					controlInterface = DE_FACTORY.createListBox();
					setMultiselectTaggedValue(taggedValueMap, controlModel);
					((ListBoxInterface) controlInterface).setIsMultiSelect(controlModel
							.getIsMultiSelect());
					((ListBoxInterface) controlInterface).setNoOfRows(controlModel.getRows());
				}
				else
				{//Combo box for the rest
					userSelectedControlName = ProcessorConstants.COMBOBOX_CONTROL;
					controlInterface = DE_FACTORY.createComboBox();
				}
				final String separator = getSeparatorTagValue(taggedValueMap);
				((SelectControl) controlInterface).setSeparator(separator);
				addAssociationDisplayAttributes(associationInterface, taggedValueMap,
						controlInterface);
				//Set Explicit Validation Rules
				setExplicitValidationRules(taggedValueMap, controlModel);
				//populate rules
				attributeProcessor.populateRules(userSelectedControlName, associationInterface,
						controlModel);
				final String[] ruleNamesString = new String[0];
				controlModel.setValidationRules(ruleNamesString);
				controlModel.setTempValidationRules(ruleNamesString);
			}
		}
		else
		{
			final AttributeInterface attributeInterface = (AttributeInterface) abstractAttributeInterface;
			final AttributeTypeInformationInterface attributeTypeInformation = attributeInterface
					.getAttributeTypeInformation();
			final UserDefinedDEInterface userDefinedDEInterface = (UserDefinedDEInterface) attributeTypeInformation
					.getDataElement();
			if (!(attributeInterface.getName().equalsIgnoreCase(Constants.SYSTEM_IDENTIFIER) && xmiConfigurationObject
					.isAddIdAttribute()))
			{
				final Map<String, String> taggedValueMap = attrVsMapTagValues
						.get(attributeInterface);
				//PHI
				final String strIsIdentified = getPHIAttributeTagValue(taggedValueMap);
				if (strIsIdentified != null && !strIsIdentified.equalsIgnoreCase(""))
				{
					attributeProcessor
							.populateIsIdentifiedInfo(attributeInterface, strIsIdentified);
				}
				if (userDefinedDEInterface != null
						&& userDefinedDEInterface.getPermissibleValueCollection() != null
						&& userDefinedDEInterface.getPermissibleValueCollection().size() > 0)
				{
					String userSelectedControlName;
					controlModel.setDataType(ProcessorConstants.DATATYPE_STRING);
					// multiselect for permisible values
					//attributeInterface.setIsCollection(new Boolean(true));
					if (isMultiselectTagValue(taggedValueMap))
					{
						userSelectedControlName = ProcessorConstants.LISTBOX_CONTROL;
						controlInterface = DE_FACTORY.createListBox();
						setMultiselectTaggedValue(taggedValueMap, controlModel);
					}
					else
					{//Combo box for the rest
						userSelectedControlName = ProcessorConstants.COMBOBOX_CONTROL;
						controlInterface = DE_FACTORY.createComboBox();
						((ComboBoxInterface) controlInterface)
								.setColumns(DEConstants.CONTROL_DEFAULT_VALUE);
					}
					if (controlModel.getIsMultiSelect() != null
							&& controlModel.getIsMultiSelect().booleanValue())
					{
						controlInterface = controlProcessor.getListBoxControl(controlInterface,
								controlModel);
					}
					else
					{
						controlInterface = controlProcessor.getComboBoxControl(controlInterface,
								controlModel, entityGroup);
					}
					//Set Explicit Validation Rules
					setExplicitValidationRules(taggedValueMap, controlModel);
					//populate rules
					attributeProcessor.populateRules(userSelectedControlName, attributeInterface,
							controlModel);
					final String[] ruleNamesString = new String[0];
					controlModel.setValidationRules(ruleNamesString);
					controlModel.setTempValidationRules(ruleNamesString);
				}
				else if (attributeTypeInformation instanceof DateAttributeTypeInformation)
				{
					controlModel.setDataType(ProcessorConstants.DATATYPE_DATE);
					final String userSelectedControlName = ProcessorConstants.DATEPICKER_CONTROL;
					final String format = getDateFormatTagValue(taggedValueMap);
					controlModel.setFormat(format);
					controlInterface = DE_FACTORY.createDatePicker();
					final String defaultValue = getDefaultValueTagValue(taggedValueMap);
					controlModel.setAttributeDefaultValue(defaultValue);
					if (defaultValue != null && !defaultValue.equalsIgnoreCase(""))
					{
						controlModel.setDateValueType(ProcessorConstants.DATE_VALUE_SELECT);
					}
					//Set Explicit Validation Rules
					setExplicitValidationRules(taggedValueMap, controlModel);

					attributeProcessor.populateDateAttributeInterface(attributeInterface,
							(DateAttributeTypeInformation) attributeTypeInformation, controlModel);
					//					//populate rules
					attributeProcessor.populateRules(userSelectedControlName, attributeInterface,
							controlModel);
					final String[] ruleNamesString = new String[0];
					controlModel.setValidationRules(ruleNamesString);
					controlModel.setTempValidationRules(ruleNamesString);

				}
				//Creating check box for boolean attributes
				else if (attributeTypeInformation instanceof BooleanAttributeTypeInformation)
				{
					final String userSelectedControlName = ProcessorConstants.CHECKBOX_CONTROL;
					controlModel.setDataType(ProcessorConstants.DATATYPE_BOOLEAN);
					controlInterface = DE_FACTORY.createCheckBox();
					final String defaultValue = getDefaultValueForBooleanTagValue(
							attributeInterface, taggedValueMap);

					final BooleanValueInterface booleanValue = DomainObjectFactory.getInstance()
							.createBooleanValue();
					booleanValue.setValue(Boolean.valueOf(defaultValue));

					((BooleanAttributeTypeInformation) attributeTypeInformation)
							.setDefaultValue(booleanValue);
					//Set Explicit Validation Rules
					setExplicitValidationRules(taggedValueMap, controlModel);
					//populate rules
					attributeProcessor.populateRules(userSelectedControlName, attributeInterface,
							controlModel);
					final String[] ruleNamesString = new String[0];
					controlModel.setValidationRules(ruleNamesString);
					controlModel.setTempValidationRules(ruleNamesString);
				}
				//Creating File upload for byte array attributes
				else if (attributeTypeInformation instanceof FileAttributeTypeInformation)
				{
					controlModel.setDataType(ProcessorConstants.DATATYPE_FILE);
					final String userSelectedControlName = ProcessorConstants.FILEUPLOAD_CONTROL;
					controlInterface = DE_FACTORY.createFileUploadControl();
					((FileUploadInterface) controlInterface).setColumns(10);
					//Setting MaxLength
					final String maxLen = getMaxLengthTagValue(taggedValueMap);
					controlModel.setAttributeSize(maxLen);
					//Setting fileformats
					final String[] fileFormats = getFileFormatsTagValue(taggedValueMap);
					controlModel.setFileFormats(fileFormats);
					attributeProcessor.populateFileAttributeInterface(
							(FileAttributeTypeInformation) attributeTypeInformation, controlModel);

					//Set Explicite validation Rules
					setExplicitValidationRules(taggedValueMap, controlModel);
					//populate rules
					attributeProcessor.populateRules(userSelectedControlName, attributeInterface,
							controlModel);
					final String[] ruleNamesString = new String[0];
					controlModel.setValidationRules(ruleNamesString);
					controlModel.setTempValidationRules(ruleNamesString);
				}
				else
				{
					final String userSelectedControlName = ProcessorConstants.DEFAULT_SELECTED_CONTROL;
					controlInterface = DE_FACTORY.createTextField();
					((TextFieldInterface) controlInterface).setColumns(10);
					//Creating Text Control
					if (attributeTypeInformation instanceof StringAttributeTypeInformation)
					{
						controlModel.setDataType(ProcessorConstants.DATATYPE_STRING);
						final String defaultValue = getDefaultValueTagValue(taggedValueMap);
						controlModel.setAttributeDefaultValue(defaultValue);
						final String maxLen = getMaxLengthTagValue(taggedValueMap);
						controlModel.setAttributeSize(maxLen);
						attributeProcessor.populateStringAttributeInterface(
								(StringAttributeTypeInformation) attributeTypeInformation,
								controlModel);
						//Password
						final String password = getPasswordTagValue(taggedValueMap);
						controlModel.setIsPassword(Boolean.parseBoolean(password));
						//URL
						final String url = getUrlTagValue(taggedValueMap);
						controlModel.setIsUrl(Boolean.parseBoolean(url));

						//NoOfColumns
						final String width = getDisplayWidthTagValue(taggedValueMap);
						controlModel.setColumns(Integer.parseInt(width));

						// Single/Multiline(Number of Lines) tagged Value
						setMultilineTaggedValue(taggedValueMap, controlModel);
						if (controlModel.getLinesType() != null
								&& controlModel.getLinesType().equalsIgnoreCase(
										XMIConstants.MULTILINE))
						{
							controlInterface = controlProcessor.getMultiLineControl(
									controlInterface, controlModel);
						}
						else
						{
							controlInterface = controlProcessor.getTextControl(controlInterface,
									controlModel);
						}
						//Set Explicit validation Rules
						setExplicitValidationRules(taggedValueMap, controlModel);
						//populate rules
						attributeProcessor.populateRules(userSelectedControlName,
								attributeInterface, controlModel);
						final String[] ruleNamesString = new String[0];
						controlModel.setValidationRules(ruleNamesString);
						controlModel.setTempValidationRules(ruleNamesString);
					}
					//Number Attribute
					else
					{
						controlModel.setDataType(ProcessorConstants.DATATYPE_NUMBER);

						final String defaultValue = getDefaultValueTagValue(taggedValueMap);
						controlModel.setAttributeDefaultValue(defaultValue);

						final Integer precision = getPrecisionTagValue(taggedValueMap,
								attributeTypeInformation);
						controlModel.setAttributeDecimalPlaces(precision.toString());

						//Set Explicit validation Rules.
						setExplicitValidationRules(taggedValueMap, controlModel);

						if (attributeTypeInformation instanceof LongAttributeTypeInformation)
						{
							attributeProcessor.populateLongAttributeInterface(attributeInterface,
									(LongAttributeTypeInformation) attributeTypeInformation,
									controlModel);
						}
						else if (attributeTypeInformation instanceof IntegerAttributeTypeInformation)
						{
							attributeProcessor.populateIntegerAttributeInterface(
									attributeInterface,
									(IntegerAttributeTypeInformation) attributeTypeInformation,
									controlModel);
						}
						else if (attributeTypeInformation instanceof FloatAttributeTypeInformation)
						{
							attributeProcessor.populateFloatAttributeInterface(attributeInterface,
									(FloatAttributeTypeInformation) attributeTypeInformation,
									controlModel);
						}
						else if (attributeTypeInformation instanceof DoubleAttributeTypeInformation)
						{
							attributeProcessor.populateDoubleAttributeInterface(attributeInterface,
									(DoubleAttributeTypeInformation) attributeTypeInformation,
									controlModel);
						}
						//populate rules
						attributeProcessor.populateRules(userSelectedControlName,
								attributeInterface, controlModel);
						final String[] ruleNamesString = new String[0];
						controlModel.setValidationRules(ruleNamesString);
						controlModel.setTempValidationRules(ruleNamesString);
					}
				}
			}
			else
			{
				return null;
			}
		}
		controlInterface.setName(abstractAttributeInterface.getName());
		controlInterface.setCaption(abstractAttributeInterface.getName());
		controlInterface.setBaseAbstractAttribute(abstractAttributeInterface);
		return controlInterface;
	}

	/**
	 * @param associationInterface
	 * @param controlInterface
	 * In case of linking association, this method adds the association display attributes.
	 */
	private void addAssociationDisplayAttributes(final AssociationInterface associationInterface,
			final Map<String, String> taggedValueMap, final ControlInterface controlInterface)
	{
		final EntityInterface targetEntity = associationInterface.getTargetEntity();
		final DomainObjectFactory domainObjectFactory = DomainObjectFactory.getInstance();
		//		This method returns all attributes and not associations
		final Collection<AttributeInterface> targetEntityAttrColl = targetEntity
				.getAttributeCollection();
		int seqNo = 1;
		final String attrInAssocDropDownTagValue = taggedValueMap
				.get(XMIConstants.TAGGED_VALUE_ATTRIBUTES_IN_ASSOCIATION_DROP_DOWN);
		if (attrInAssocDropDownTagValue != null)
		{
			final StringTokenizer stringTokenizer = new StringTokenizer(
					attrInAssocDropDownTagValue, ",");
			while (stringTokenizer.hasMoreTokens())
			{
				final String attributeName = stringTokenizer.nextToken();
				for (final AttributeInterface attr : targetEntityAttrColl)
				{
					if (attributeName.equals(attr.getName()))
					{
						final AssociationDisplayAttributeInterface associationDisplayAttribute = domainObjectFactory
								.createAssociationDisplayAttribute();
						associationDisplayAttribute.setSequenceNumber(seqNo);
						associationDisplayAttribute.setAttribute(attr);
						//This method adds to the associationDisplayAttributeCollection
						((SelectControl) controlInterface)
								.addAssociationDisplayAttribute(associationDisplayAttribute);
						seqNo++;
					}
				}
			}
		}
	}

	/**
	 *
	 * @param taggedValueMap
	 * @param attributeInterface
	 * @param controlModel
	 * @throws DynamicExtensionsSystemException
	 */
	private void setExplicitValidationRules(final Map<String, String> taggedValueMap,
			final ControlsModel controlModel) throws DynamicExtensionsSystemException
	{
		final Map<String, String> taggedValueRuleMap = new HashMap<String, String>();
		if (taggedValueMap != null)
		{
			Set<Entry<String, String>> keySetForTaggedValue = taggedValueMap.entrySet();
			//Grouping Rule Tagged Values
			for (Entry<String, String> entryKey : keySetForTaggedValue)
			{
				if (entryKey.getKey().startsWith(
						XMIConstants.TAGGED_VALUE_RULE + XMIConstants.SEPARATOR))
				{
					taggedValueRuleMap.put(entryKey.getKey(), entryKey.getValue());
				}
			}
			//Seting Rule Tagged Values to ControlsModel
			if (taggedValueRuleMap != null && !taggedValueRuleMap.isEmpty())
			{
				populateValidationRule(taggedValueRuleMap, controlModel);
			}
		}
	}

	/**
	 *
	 * @param taggedValueMapRule
	 * @param attributeInterface
	 * @param controlModel
	 * @throws DynamicExtensionsSystemException
	 */
	private void populateValidationRule(final Map<String, String> taggedValueRuleMap,
			final ControlsModel controlModel) throws DynamicExtensionsSystemException
	{
		final ArrayList<String> ruleNames = new ArrayList<String>();
		String ruleName = "";
		int counter = 0;

		final Set<Entry<String, String>> keySetForRuleTaggedValueMap = taggedValueRuleMap
				.entrySet();
		for (final Entry<String, String> entryKey : keySetForRuleTaggedValueMap)
		{
			final StringTokenizer stringTokenizer = new StringTokenizer(entryKey.getKey(),
					XMIConstants.SEPARATOR);
			int tokenNumber = 0;
			final int count = stringTokenizer.countTokens();

			// Seting Rule Tagged Values and parameter values to ControlsModel.
			if (count <= 3)
			{
				while (stringTokenizer.hasMoreTokens())
				{
					tokenNumber++;
					final String tokenName = stringTokenizer.nextToken();
					// Finding Rule name.
					if (tokenNumber == 2)
					{
						ruleName = tokenName;
					}
					// Setting Parameter values.
					if (tokenNumber == 3)
					{
						if (tokenName.equalsIgnoreCase("min"))
						{
							controlModel.setMin(entryKey.getValue());
							controlModel.setMinTemp(entryKey.getValue());
						}
						else if (tokenName.equalsIgnoreCase("max"))
						{
							controlModel.setMax(entryKey.getValue());
							controlModel.setMaxTemp(entryKey.getValue());
						}
					}
					if (!ruleNames.contains(ruleName))
					{
						ruleNames.add(ruleName);
					}
				}
			}
		}

		final String[] ruleNamesString = new String[ruleNames.size()];
		for (final String ruleStringName : ruleNames)
		{
			ruleNamesString[counter++] = ruleStringName;
		}

		controlModel.setValidationRules(ruleNamesString);
		controlModel.setTempValidationRules(ruleNamesString);
	}

	/**
	 * This method removes inherited attributes.
	 * @param entity
	 * @param duplicateAttributeCollection
	 */
	protected void removeInheritedAttributes(final EntityInterface entity,
			final List duplicateAttributeCollection)
	{
		if (duplicateAttributeCollection != null)
		{
			entity.getAbstractAttributeCollection().removeAll(duplicateAttributeCollection);
		}
	}

	/**
	 * this method persists changes to database
	 * @param containerNames
	 * @param isEntityGroupSystemGenerated
	 * @param isCreateTable
	 * @param isDefaultPackage
	 * @param defaultPackagePrefix
	 * @param hibernatedao
	 * @param hibernatedao
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	protected DynamicQueryList processPersistence(final List<String> containerNames,
			final boolean isEntityGroupSystemGenerated, final boolean isCreateTable,
			final boolean isDefaultPackage, final String defaultPackagePrefix,
			final HibernateDAO... hibernatedao) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		//Collection<ContainerInterface> containerColl = new HashSet<ContainerInterface>();

		//		Set<String> entityIdKeySet = entityNameVsContainers.keySet();

		for (String containerName : containerNames)
		{
			//		For static models
			if (xmiConfigurationObject.isEntityGroupSystemGenerated() && isDefaultPackage
					&& !containerName.startsWith(defaultPackagePrefix))
			{
				containerName = defaultPackagePrefix + containerName;

			}
			final List containerList = entityNameVsContainers.get(containerName);
			if (containerList == null || containerList.size() < 1)
			{
				throw new DynamicExtensionsApplicationException("The container name "
						+ containerName + " does "
						+ "not match with the container name in the Model.");
			}
			final ContainerInterface containerInterface = (ContainerInterface) containerList.get(0);
			mainContainerList.add(containerInterface);
		}

		DynamicQueryList dynamicQueryList;
		final EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();
		try
		{
			//	entityManagerInterface.persistEntityGroupWithAllContainers(entityGroup, mainContainerList);
			for (final ContainerInterface container : mainContainerList)
			{
				entityGroup.addMainContainer(container);
			}


			// Check for potential cycles in the model. Cycle in the model leads to the UI error in the form
			//rendering and the while firing the query.
			if (xmiConfigurationObject.isCreateTable())
			{
				XMIImportValidator.validateForCycleInEntityGroup(entityGroup);
			}


			//Do not create database table if entity group is system generated or the isCreateTable is set to false explicitly
			if (xmiConfigurationObject.isEntityGroupSystemGenerated()
					|| !xmiConfigurationObject.isCreateTable())
			{//Static Model. Hence saving only metadata
				dynamicQueryList = entityGroupManager.persistEntityGroupMetadata(entityGroup,
						hibernatedao);
				if (skipentityGroup != null)
				{
					dynamicQueryList = entityGroupManager.persistEntityGroupMetadata(
							skipentityGroup, hibernatedao);
				}
			}
			else
			{//Dynamic model
				dynamicQueryList = entityGroupManager.persistEntityGroup(entityGroup, hibernatedao);
			}
		}
		catch (final DynamicExtensionsApplicationException e)
		{
			throw new DynamicExtensionsApplicationException(e.getMessage(), e);
		}
		catch (final DynamicExtensionsSystemException e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}
		return dynamicQueryList;
	}

	/**
	 * @return
	 */
	public AssociationInterface createAssociation()
	{
		final AssociationInterface associationInterface = DomainObjectFactory.getInstance()
				.createAssociation();
		return associationInterface;
	}

	/**
	 */
	protected void postProcessAssociation()
	{
		addControlsForAssociation();
	}

	/**
	 * @param entityName
	 * @param skipEntityNames
	 * @param defaultPackagePrefix
	 * @return
	 */
	protected boolean isSkipEntity(final String entityName, final List<String> skipEntityNames,
			final String defaultPackagePrefix)
	{
		boolean present = false;
		String enName = entityName;
		for (final String skipEntityName : skipEntityNames)

		{
			if (entityName.startsWith(defaultPackagePrefix))
			{
				enName = entityName.substring(defaultPackagePrefix.length(), entityName.length());
			}
			if (enName.equalsIgnoreCase(skipEntityName))
			{
				present = true;
				break;

			}
		}
		return present;
	}

	/**
	 * @param umlPackage
	 * @param packageName
	 * @param skipEntityNames
	 * @throws DynamicExtensionsSystemException
	 */
	private void processDataModel(final UmlPackage umlPackage, final String packageName,
			final List<String> skipEntityNames, final String defaultPackagePrefix)
			throws DynamicExtensionsSystemException
	{

		final List<Dependency> sqlDependencyColl = new ArrayList<Dependency>();
		final List<UmlClass> sqlClassColl = new ArrayList<UmlClass>();
		final List<UmlAssociation> sqlAssociationColl = new ArrayList<UmlAssociation>();
		final List<Generalization> sqlGeneralisationColl = new ArrayList<Generalization>();

		//process for data model, not want to process association and generalization so passing null
		processModel(umlPackage, sqlClassColl, sqlAssociationColl, sqlGeneralisationColl,
				sqlDependencyColl, XMIConstants.PACKAGE_NAME_LOGICAL_VIEW
						+ XMIConstants.DOT_SEPARATOR + XMIConstants.PACKAGE_NAME_DATA_MODEL, true);
		if (isPackagePresent)
		{
			final List<String> associationNames = new ArrayList<String>();
			final List<EntityInterface> supplierEntities = new ArrayList<EntityInterface>();
			// process dependency collection to change name of the table and columns of the entity
			if (sqlDependencyColl != null)
			{
				for (final Dependency umlDependency : sqlDependencyColl)
				{
					final EntityInterface supplierEntity = processDependency(umlDependency,
							packageName, skipEntityNames, associationNames, defaultPackagePrefix);
					if (supplierEntity != null)
					{
						supplierEntities.add(supplierEntity);
					}

				}
				// process association names collection to change name of the target entity column of Asson name of each supplierentity
				processAssociation(supplierEntities, associationNames, defaultPackagePrefix);

			}
		}
	}

	/**
	 * @param supplierEntities
	 * @param associationNames
	 */
	private void processAssociation(final List<EntityInterface> supplierEntities,
			final List<String> associationNames, final String defaultPackagePrefix)
	{
		for (final EntityInterface supplierEntity : supplierEntities)
		{

			for (final AssociationInterface asson : supplierEntity.getAssociationCollection())
			{

				for (final String assoName : associationNames)
				{
					final StringTokenizer assnToken = new StringTokenizer(assoName,
							XMIConstants.COLON_SEPARATOR);

					final ArrayList<String> str = new ArrayList<String>();

					while (assnToken.hasMoreTokens())
					{
						str.add(assnToken.nextToken());
					}

					final String assonColName = str.get(str.size() - 2);
					final String assonSourceEntityName = str.get(str.size() - 1);
					final String assonTargetEntityName = str.get(1);
					String srcEntity = asson.getEntity().getName();

					final String supEntityTargetEntityName = asson.getTargetEntity().getName();
					if (asson.getEntity().getName().contains(defaultPackagePrefix))
					{
						srcEntity = getLastToken(new StringTokenizer(asson.getEntity().getName(),
								XMIConstants.DOT_SEPARATOR));
					}
					if (supEntityTargetEntityName.equalsIgnoreCase(assonTargetEntityName)
							&& srcEntity.equalsIgnoreCase(assonSourceEntityName))
					{
						//TO DO falguni see how to set constraint properties
						asson.getConstraintProperties().getTgtEntityConstraintKeyProperties()
								.getTgtForiegnKeyColumnProperties().setName(assonColName);
						break;
					}

				}

			}

		}

	}

	/**
	 * It will process the dependencies that are present in the table (in data model) & corresponding
	 * umlClasses (in the logical model) and update the TableProperties and ColumnProperties of the
	 * corresponding entity as given in the data model table class.
	 * @param umlDependency
	 * @param packageName
	 * @throws DynamicExtensionsSystemException
	 */
	private EntityInterface processDependency(final Dependency umlDependency,
			final String packageName, final List<String> skipEntityNames,
			final List<String> associationName, final String defaultPackagePrefix)
			throws DynamicExtensionsSystemException
	{
		// here client means the dataclass which is src of dependency
		// & supplier means the umlclass on which this dataclass depends
		//set asson foregin key colum in updateattributecolname function.

		final Collection<UmlClass> clientColl = umlDependency.getClient();
		final Collection<UmlClass> supplierColl = umlDependency.getSupplier();
		String clientId = null;
		String supplierId = null;
		EntityInterface supplierEntity = null;
		EntityInterface entity = null;
		UmlClass clientClass = null;
		Map<String, String> tagNameVsTagValue;
		for (final UmlClass client : clientColl)
		{
			clientId = client.refMofId();
			clientClass = client;
		}
		for (final UmlClass supplier : supplierColl)
		{
			supplierId = supplier.refMofId();
		}
		if (clientId != null && supplierId != null)
		{

			//TO DO --association tagged value
			supplierEntity = umlClassIdVsEntity.get(supplierId);

			if (!isSkipEntity(supplierEntity.getName(), skipEntityNames, defaultPackagePrefix))
			{
				umlClassIdVsEntity.put(clientId, supplierEntity);
				supplierEntity.getTableProperties().setName(clientClass.getName());
				final Collection<Attribute> umlAttributeCollection = XMIUtilities.getAttributes(
						clientClass, false);
				for (final Attribute attr : umlAttributeCollection)
				{
					tagNameVsTagValue = populateUMLTagValueMap(attr.getTaggedValue());
					columnNameVsMapTagValues.put(attr.getName(), tagNameVsTagValue);
					updateAttributeColumnName(supplierEntity, tagNameVsTagValue, packageName, attr);
					final String assnName = tagNameVsTagValue
							.get(XMIConstants.TAGGED_VALUE_IMPLEMENTS_ASSOCIATION);
					if (assnName != null)
					{
						associationName.add(assnName);

					}

				}

			}
			entity = supplierEntity;

		}
		return entity;
	}

	/**
	 * @param taggedValueColl
	 * @return
	 */
	private String getAssociationTypeTV(final Collection taggedValueColl)
	{
		//check for associationtype tag ,first check whether containment type tag is present
		//If not then check whether containment unspecified type tag is present
		String assnType = getTaggedValue(taggedValueColl, XMIConstants.TAGGED_VALUE_CONTAINMENT);
		if (assnType != null && assnType.equals(""))
		{

			assnType = getTaggedValue(taggedValueColl,
					XMIConstants.TAGGED_VALUE_CONTAINMENT_UNSPECIFIED);
		}

		return assnType;
	}

	/**
	 * @param tokenizer
	 * @return
	 */
	private String getLastToken(final StringTokenizer tokenizer)
	{
		String token = "";
		while (tokenizer.hasMoreTokens())
		{
			token = tokenizer.nextToken();
		}
		return token;
	}

	/**
	 * @param tokenizer
	 * @return
	 */
	private String getSecondLastToken(final StringTokenizer tokenizer)
	{
		String token = "";
		String secondLast = "";
		while (tokenizer.hasMoreTokens())
		{
			secondLast = token;
			token = tokenizer.nextToken();

		}
		return secondLast;
	}

	/**
	 * It will verify weather the mapped-attributes tag is present on the newColumn Attribute
	 * if present it will update the given attribute which is given in the mapped-attribute tag as a value
	 * It will  first verify given tagValue is valid, if not will throw the exception
	 * @param supplierEntity the entity on which the dependency depends
	 * @param tagNameVsTagValue map of taggedvalues of newColumn attribute
	 * @param packageName package name of the current model
	 * @param newColumn umlAttribute in the Data Model table class
	 * @throws DynamicExtensionsSystemException
	 */

	private void updateAttributeColumnName(final EntityInterface supplierEntity,
			final Map<String, String> tagNameVsTagValue, final String packageName,
			final Attribute newColumn) throws DynamicExtensionsSystemException
	{
		final String mappedAttribute = tagNameVsTagValue
				.get(XMIConstants.TAGGED_VALUE_MAPPED_ATTRIBUTES);

		supplierEntity.getAssociationCollection();
		supplierEntity.getAttributeCollection();
		if (mappedAttribute != null && !"".equals(mappedAttribute.trim()))
		{
			StringTokenizer tokens = new StringTokenizer(mappedAttribute,
					XMIConstants.DOT_SEPARATOR);

			final StringTokenizer packageTokenizer = new StringTokenizer(packageName,
					XMIConstants.DOT_SEPARATOR);

			validatePackageName(tokens, packageTokenizer);
			tokens = new StringTokenizer(mappedAttribute, XMIConstants.DOT_SEPARATOR);
			final String umlEntityName = getSecondLastToken(tokens);
			final String umlAttriName = getLastToken(new StringTokenizer(mappedAttribute,
					XMIConstants.DOT_SEPARATOR));
			final String entityName = getLastToken(new StringTokenizer(supplierEntity.getName(),
					XMIConstants.DOT_SEPARATOR));
			//take second last from tokens string it gives class name
			validateEntityName(umlEntityName, entityName);
			updateAttributeColumnProperties(umlAttriName, supplierEntity, newColumn);

		}

	}

	/**
	 * It will retrieve the attribute with the name given in token from the supplierEntity and will
	 * update the columnProperties.
	 * @param token attribute name
	 * @param supplierEntity entity in which the attribute is to be searched
	 * @param newColumn umlAttribute corresponding to the token attribute
	 */
	private void updateAttributeColumnProperties(final String token,
			final EntityInterface supplierEntity, final Attribute newColumn)
			throws DynamicExtensionsSystemException
	{

		final AttributeInterface attribute = supplierEntity.getAttributeByName(token);
		if (attribute == null)
		{
			throw new DynamicExtensionsSystemException("Attribute " + token
					+ "not found in Entity which is specified in mapped-attribute taggedValue ");
		}
		else
		{
			attribute.getColumnProperties().setName(newColumn.getName());
		}

	}

	/**
	 * It will verify weather the token and the name are same or not.
	 * if not will throw the exception
	 * @param token
	 * @param name
	 * @throws DynamicExtensionsSystemException
	 */
	private void validateEntityName(final String token, final String name)
			throws DynamicExtensionsSystemException
	{

		if (!token.equalsIgnoreCase(name))
		{
			throw new DynamicExtensionsSystemException(
					"Entity name of the mappped attribute does not match with uml model entity name");
		}
	}

	/**
	 * It will verify weather the tokens present in the packageTokenizer and tokens are same
	 * if not will throw the exception
	 * @param tokens
	 * @param packageTokenizer
	 * @throws DynamicExtensionsSystemException
	 */
	private void validatePackageName(final StringTokenizer tokens,
			final StringTokenizer packageTokenizer) throws DynamicExtensionsSystemException
	{

		String subPackageName = "";
		String token = "";
		while (packageTokenizer.hasMoreTokens())
		{
			subPackageName = packageTokenizer.nextToken();
			token = getNextToken(tokens);
			if (!token.equalsIgnoreCase(subPackageName))
			{
				throw new DynamicExtensionsSystemException(
						"PackageName of the mappped attribute does not match with uml model package name");
			}
		}

	}

	/**
	 * @param taggedValueColl
	 * @return
	 */
	private Map<String, String> populateUMLTagValueMap(final Collection<TaggedValue> taggedValueColl)
	{
		final Map<String, String> tagNameVsTagValue = new HashMap<String, String>();
		for (final TaggedValue taggedValue : taggedValueColl)
		{
			if (taggedValue.getType() != null)
			{
				final Collection<String> dataValueColl = taggedValue.getDataValue();
				for (final String value : dataValueColl)
				{
					tagNameVsTagValue.put(taggedValue.getType().getName(), value);
				}
			}
		}

		return tagNameVsTagValue;
	}

	/**
	 * this method populate attribute for the entity
	 * @param umlClass UML class
	 * @param entity entity object
	 * @throws DynamicExtensionsSystemException fails to add attribute
	 * @throws DynamicExtensionsApplicationException fails to add attribute
	 */
	private void populateAttributes(final UmlClass umlClass, final EntityInterface entity)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		final Set<String> attributeColl = (Set<String>) addAttributes(umlClass, entity);
		entityNameVsAttributeNames.put(entity.getName(), attributeColl);
	}

	/**
	 * Gets the entity group.
	 * @return the entity group
	 */
	public EntityGroupInterface getEntityGroup()
	{
		return entityGroup;
	}

}
