
package edu.common.dynamicextensions.xmi.importer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.entitymanager.AbstractMetadataManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerConstantsInterface;
import edu.common.dynamicextensions.exception.DataTypeFactoryInitializationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.exception.DynamicExtensionsValidationException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.interfaces.AbstractAttributeUIBeanInterface;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.common.dynamicextensions.validation.DateRangeValidator;
import edu.common.dynamicextensions.validation.RangeValidator;
import edu.common.dynamicextensions.xmi.XMIConstants;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.query.generator.DBTypes;
import edu.wustl.dao.util.NamedQueryParam;

/**
 * @author falguni_sachde
 *
 */
public class XMIImportValidator
{

	/**
	 * List to maintain the validation errors
	 */
	public final static List<String> ERROR_LIST = new ArrayList<String>();
	static
	{
		ApplicationProperties.initBundle("ApplicationResources");
	}

	/**
	 * This method verify that default value specified is within given range for numeric type attribute
	 * @param attribute
	 * @param controlsForm
	 * @param defaultValue
	 * @throws DynamicExtensionsApplicationException
	 */
	public static void verifyDefaultValueIsInRange(AbstractAttributeInterface attribute,
			AbstractAttributeUIBeanInterface controlsForm, Number defaultValue)
			throws DynamicExtensionsApplicationException
	{

		if (controlsForm.getMin() != null && controlsForm.getMin().length() != 0
				&& controlsForm.getMax() != null && controlsForm.getMax().length() != 0
				&& defaultValue != null)
		{
			String min = controlsForm.getMin();
			String max = controlsForm.getMax();

			try
			{
				verifyDefaultValueIsInRange(attribute, String.valueOf(defaultValue), min, max,
						attribute.getName());
			}
			catch (DynamicExtensionsValidationException e)
			{
				//ApplicationProperties.initBundle("ApplicationResources");
				List<String> placeHolders = new ArrayList<String>();
				placeHolders.add(defaultValue.toString());
				placeHolders.add(attribute.getName());
				placeHolders.add(min);
				placeHolders.add(max);
				Logger.out.info(ApplicationProperties.getValue("validationError")
						+ ApplicationProperties.getValue("defValueOORange", placeHolders));

				throw new DynamicExtensionsApplicationException(e.getMessage(), e);
			}
			catch (DataTypeFactoryInitializationException e)
			{
				throw new DynamicExtensionsApplicationException(e.getMessage(), e);
			}
		}
	}

	/**
	 * This method verify that default value specified is within given range for
	 * date type attribute only using date range validator
	 * @param attribute
	 * @param controlsForm
	 * @param defaultValue
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	public static void verifyDefaultValueForDateIsInRange(AbstractAttributeInterface attribute,
			AbstractAttributeUIBeanInterface controlsForm, String defaultValue)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{

		if (controlsForm.getMin() != null && controlsForm.getMin().length() != 0
				&& controlsForm.getMax() != null && controlsForm.getMax().length() != 0
				&& defaultValue != null)
		{
			String min = controlsForm.getMin();
			String max = controlsForm.getMax();
			Map<String, String> values = new HashMap<String, String>();
			values.put("min", min);
			values.put("max", max);

			DateRangeValidator dateRangeValidator = new DateRangeValidator();
			try
			{
				dateRangeValidator.validate((AttributeMetadataInterface) attribute, defaultValue,
						values, attribute.getName());

			}
			catch (DynamicExtensionsValidationException e)
			{
				//ApplicationProperties.initBundle("ApplicationResources");
				Logger.out.info(ApplicationProperties.getValue("validationError")
						+ ApplicationProperties.getValue("defValueFor")
						+ ApplicationProperties.getValue(e.getErrorCode(), e.getPlaceHolderList()));
				throw new DynamicExtensionsSystemException(e.getMessage(), e);

			}
		}
	}

	/**
	 * @param attribute
	 * @param defaultValue
	 * @param min
	 * @param max
	 * @param attributeName
	 * @throws DynamicExtensionsValidationException
	 * @throws DataTypeFactoryInitializationException
	 */
	private static void verifyDefaultValueIsInRange(AbstractAttributeInterface attribute,
			String defaultValue, String min, String max, String attributeName)
			throws DynamicExtensionsValidationException, DataTypeFactoryInitializationException
	{
		if (defaultValue != null && min != null && max != null)
		{
			Map<String, String> parameterMap = new HashMap<String, String>();
			parameterMap.put("min", min);
			parameterMap.put("max", max);

			RangeValidator rangeValidator = new RangeValidator();
			rangeValidator.validate((AttributeMetadataInterface) attribute, defaultValue,
					parameterMap, attributeName);
		}
	}

	/**
	 * @param attribute
	 * @param booleanVal
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	public static String validateDefBooleanValue(AbstractAttributeInterface attribute,
			String booleanVal) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException
	{

		if (!(booleanVal.trim().equalsIgnoreCase(ProcessorConstants.TRUE) || booleanVal.trim()
				.equalsIgnoreCase(ProcessorConstants.FALSE)))
		{
			//ApplicationProperties.initBundle("ApplicationResources");
			List<String> placeHolders = new ArrayList<String>();
			placeHolders.add(booleanVal);
			placeHolders.add(attribute.getName());
			Logger.out.info(ApplicationProperties.getValue("validationError")
					+ ApplicationProperties.getValue("defValueBoolInvalid", placeHolders));

			throw new DynamicExtensionsApplicationException("Validation failed");

		}
		return booleanVal;

	}

	/**
	 * It will validate whether the data type of the attribute is valid for the
	 * primary key of the entity
	 * @param primaryAttribute
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 */
	public static boolean validateDataTypeForPrimaryKey(AttributeInterface primaryAttribute)
			throws DynamicExtensionsApplicationException
	{
		AttributeTypeInformationInterface attrInfo = primaryAttribute.getAttributeTypeInformation();
		String attributeDataType = attrInfo.getDataType();

		if (EntityManagerConstantsInterface.FILE_ATTRIBUTE_TYPE.equals(attributeDataType)
				|| EntityManagerConstantsInterface.BOOLEAN_ATTRIBUTE_TYPE.equals(attributeDataType)
				|| EntityManagerConstantsInterface.OBJECT_ATTRIBUTE_TYPE.equals(attributeDataType))
		{
			ERROR_LIST.add("Data Type of the primaryKey Attribute " + primaryAttribute.getName()
					+ " is not acceptable for " + primaryAttribute.getEntity().getName());
		}

		return true;
	}

	/**
	 * @param entityNameVsAttributeNames Map of entityName and corresponding attribute set
	 * @param umlAssociationName Name of UML association
	 * @param sourceEntityName Name of source entity
	 * @param targetEntityName
	 * @param parentIdVsChildrenIds Map of parentId and its corresponding childIds
	 * @param umlClassIdVsEntity Map of uml class and entity
	 * @param sourceRole
	 * @param targetRole
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	public static void validateAssociations(Map<String, Set<String>> entityNameVsAttributeNames,
			String umlAssociationName, String sourceEntityName, String targetEntityName,
			Map<String, List<String>> parentIdVsChildrenIds,
			Map<String, EntityInterface> umlClassIdVsEntity, RoleInterface sourceRole,
			RoleInterface targetRole) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException
	{
		Set<String> attributeCollection = entityNameVsAttributeNames.get(sourceEntityName);
		validateName(umlAssociationName, "Association", sourceEntityName, targetEntityName);
		validateAssociationName(attributeCollection, umlAssociationName, sourceEntityName);
		validateAssociationNameInParent(entityNameVsAttributeNames, umlAssociationName,
				sourceEntityName, parentIdVsChildrenIds, umlClassIdVsEntity);
		validateCardinality(sourceRole, targetRole, sourceEntityName, targetEntityName);
	}

	/**
	 * @param entityNameVsAttributeNames Map of entityName and corresponding attribute set
	 * @param umlAssociationName Name of UML association
	 * @param childEntityName Name of child entity
	 * @param parentIdVsChildrenIds Map of parentId and its corresponding childIds
	 * @param umlClassIdVsEntity Map of uml class and entity
	 * @throws DynamicExtensionsSystemException
	 */
	private static void validateAssociationNameInParent(
			Map<String, Set<String>> entityNameVsAttributeNames, String umlAssociationName,
			String childEntityName, Map<String, List<String>> parentIdVsChildrenIds,
			Map<String, EntityInterface> umlClassIdVsEntity)
			throws DynamicExtensionsSystemException
	{
		for (Entry<String, List<String>> entry : parentIdVsChildrenIds.entrySet())
		{
			for (String childId : entry.getValue())
			{
				String childName = umlClassIdVsEntity.get(childId).getName();
				if (childName.equalsIgnoreCase(childEntityName))
				{
					String parentName = umlClassIdVsEntity.get(entry.getKey()).getName();
					if (parentName != null && !"".equals(parentName.trim()))
					{
						Set<String> attributeCollection = entityNameVsAttributeNames
								.get(parentName);
						validateAssociationName(attributeCollection, umlAssociationName, parentName);
						validateAssociationNameInParent(entityNameVsAttributeNames,
								umlAssociationName, parentName, parentIdVsChildrenIds,
								umlClassIdVsEntity);
					}
				}
			}
		}
	}

	/**
	 * @param attributeCollection collection of attributes of base class
	 * @param umlAssociationName  Name of UML association
	 * @param entityName Name of entity
	 * @throws DynamicExtensionsSystemException
	 */
	private static void validateAssociationName(Set<String> attributeCollection,
			String umlAssociationName, String sourceEntityName)
			throws DynamicExtensionsSystemException
	{
		if (attributeCollection != null && !attributeCollection.isEmpty()
				&& umlAssociationName != null && !"".equals(umlAssociationName.trim()))
		{
			for (String attributeName : attributeCollection)
			{
				if (umlAssociationName.equalsIgnoreCase(attributeName))
				{
					ERROR_LIST.add("Association name and attribute name cannot be same. "
							+ "[Entity name:" + sourceEntityName + ", Attribute name:"
							+ attributeName + "]");
				}
			}
		}
	}

	/**
	 * @param entityInterface
	 * @param sourceEntityCollection
	 * @throws DynamicExtensionsSystemException
	 */
	private static void checkForCycleStartsAtEntity(EntityInterface entityInterface,
			List<String> sourceEntityCollection) throws DynamicExtensionsSystemException
	{
		for (AssociationInterface associationInterface : entityInterface.getAssociationCollection())
		{
			if (associationInterface.getIsSystemGenerated())
			{
				continue;
			}
			if (sourceEntityCollection.contains(entityInterface.getName()))
			{
				ERROR_LIST.add("CYCLE CREATED IN THE MODEL: "
						+ getEntityNameAsPath(sourceEntityCollection));
				break;
				/*throw new DynamicExtensionsSystemException("CYCLE CREATED IN THE MODEL:-"
						+ getEntityNameAsPath(sourceEntityCollection));*/
			}
			else
			{
				sourceEntityCollection.add(entityInterface.getName());
				checkForCycleStartsAtEntity(associationInterface.getTargetEntity(),
						sourceEntityCollection);
			}
			if (!sourceEntityCollection.isEmpty())
			{
				sourceEntityCollection.remove(sourceEntityCollection.get(sourceEntityCollection
						.size() - 1));
			}
		}

	}

	/**
	 * @param sourceEntityCollection
	 * @return
	 */
	private static String getEntityNameAsPath(List<String> sourceEntityCollection)
	{
		StringBuffer path = new StringBuffer();
		for (String entity : sourceEntityCollection)
		{
			path.append(entity).append("->");
		}
		path.append(new ArrayList<String>(sourceEntityCollection).get(0));
		return path.toString();
	}

	/**
	 * @param entityGroupInterface
	 * @throws DynamicExtensionsSystemException
	 */
	public static void validateForCycleInEntityGroup(EntityGroupInterface entityGroupInterface)
			throws DynamicExtensionsSystemException
	{

		for (EntityInterface entityInterface : entityGroupInterface.getEntityCollection())
		{
			List<String> sourceEntityCollection = new ArrayList<String>();
			checkForCycleStartsAtEntity(entityInterface, sourceEntityCollection);

		}

	}

	/**
	 * Validate the name for cacore compatibility
	 * @param name
	 * @param artifact
	 * @param sourceEntityName
	 * @param targetEntityName
	 */
	public static void validateName(String name, String artifact, String sourceEntityName,
			String targetEntityName)
	{
		String artifactDetails = " [Entity:" + sourceEntityName + ", " + artifact + ":" + name
				+ "]";
		if (targetEntityName != null)
		{
			artifactDetails = " [Source Entity:" + sourceEntityName + ", Target Entity:"
					+ targetEntityName + ", " + artifact + ":" + name + "]";
		}
		if (name == null)
		{
			ERROR_LIST.add(artifact + " name is null." + artifactDetails);
		}
		else
		{
			if (!name.matches(ApplicationProperties.getValue("regEx.StartWIthTwoLowerCaseLetters")))
			{
				ERROR_LIST.add(artifact + " name does not have first two letters in lower case."
						+ artifactDetails);
			}
			validateNameForSpecialCharacterAndNumericStart(name, artifact, artifactDetails);
		}
	}

	/**
	 * Validate the class name for cacore compatibility
	 * @param name
	 * @param entityName
	 */
	public static void validateClassName(String name, String entityName)
	{
		String artifactDetails = " [Entity:" + entityName + "]";
		if (!name.matches(ApplicationProperties.getValue("regEx.StartWIthUpperCase")))
		{
			ERROR_LIST
					.add("Class name does not start with upper case character." + artifactDetails);
		}

		validateNameForSpecialCharacterAndNumericStart(name, "Class", artifactDetails);
	}

	/**
	 * Validate the name for cacore compatibility.
	 * @param name
	 * @param artifact
	 * @param artifactDetails
	 */
	public static void validateNameForSpecialCharacterAndNumericStart(String name, String artifact,
			String artifactDetails)
	{
		if (name.matches(ApplicationProperties.getValue("regEx.SpecialCharacters")))
		{
			ERROR_LIST.add(artifact + " name has special characters." + artifactDetails);
		}
		if (name.matches(ApplicationProperties.getValue("regEx.StartWithNumber")))
		{
			ERROR_LIST.add(artifact + " name starts with numeric character." + artifactDetails);
		}
	}

	/**
	 * Validate the cardinality for cacore compatibility
	 * @param sourceRole
	 * @param targetRole
	 * @param sourceEntityName
	 * @param targetEntityName
	 */
	public static void validateCardinality(RoleInterface sourceRole, RoleInterface targetRole,
			String sourceEntityName, String targetEntityName)
	{
		String sourceMaxCardinality = sourceRole.getMaximumCardinality().name();
		String targetMaxCardinality = targetRole.getMaximumCardinality().name();

		if (Cardinality.MANY.toString().equals(sourceMaxCardinality)
				&& Cardinality.MANY.toString().equals(targetMaxCardinality))
		{
			ERROR_LIST.add("Either Many-to-Many association present OR Target Role is empty. "
					+ "[Source Entity:" + sourceEntityName + ", Target Entity: " + targetEntityName
					+ "]");
		}
	}

	/**
	 * Check if the associations from a class have same names
	 * @param entity
	 */
	public static void validateDuplicateAssociationName(EntityInterface entity)
	{
		Collection<AssociationInterface> assoCollection = entity.getAssociationCollection();
		List<String> associationNames = new ArrayList<String>();
		for (AssociationInterface association : assoCollection)
		{
			if (associationNames.contains(association.getName()))
			{
				ERROR_LIST.add("Associations from the class have same names. [Entity:"
						+ entity.getName() + ", Association name: " + association.getName() + "]");
			}
			else
			{
				associationNames.add(association.getName());
			}
		}
	}

	/**
	 * @param packageName package name
	 * @param domainModelName domainModelName
	 * @throws DynamicExtensionsSystemException if problem occurred in executing HQL
	 * @throws DynamicExtensionsApplicationException if entity group names does not matches
	 */
	public static void validatePackageName(String packageName, String domainModelName)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		Map<String, NamedQueryParam> substParams = new HashMap<String, NamedQueryParam>();
		substParams.put("0", new NamedQueryParam(DBTypes.STRING, packageName));

		List<String> names = new ArrayList<String>(AbstractMetadataManager.executeHQL(
				"getEntityGroupNameByPackageName", substParams));
		if (!names.isEmpty() && !domainModelName.equals(names.get(0)))
		{
			ERROR_LIST.add("Change the package name, model " + names.get(0)
					+ " has the same package name.");
		}
	}

	/**
	 * This method will verify the tags associated with the attribtues related with
	 * Auto loading cider data.
	 * @param attribute attribute for which to validate the tags.
	 * @param attrVsMapTagValues attrVs Tag value map.
	 * @throws DynamicExtensionsApplicationException exception if improper.
	 */
	public static void validateTagsForAutoloadingCiderData(AttributeInterface attribute,
			Map<AttributeInterface, Map<String, String>> attrVsMapTagValues)
			throws DynamicExtensionsApplicationException
	{
		// validate all the tags which are present on the attribute.
		Map<String, String> tagValueMap = attrVsMapTagValues.get(attribute);
		if (tagValueMap != null
				&& tagValueMap.keySet().contains(XMIConstants.TAGGED_NAME_CONCEPT_LOCATION))
		{
			String valueXpath = tagValueMap.get(XMIConstants.TAGGED_NAME_VALUE_XPATH);
			if (StringUtils.isBlank(valueXpath))
			{
				List<String> placeHolders = new ArrayList<String>();
				placeHolders.add(attribute.getName());
				placeHolders.add(attribute.getEntity().getName());
				throw new DynamicExtensionsApplicationException(ApplicationProperties.getValue(
						"xmi.invalid.valuexpath.tag", placeHolders));
			}
			String conceptLocation = tagValueMap.get(XMIConstants.TAGGED_NAME_CONCEPT_LOCATION);
			String identifyingXpath = tagValueMap.get(XMIConstants.TAGGED_NAME_IDENTIFYING_XPATH);
			String predicate = tagValueMap.get(XMIConstants.TAGGED_NAME_PREDICATE_XPATH);
			validateConceptLocationTag(conceptLocation, identifyingXpath, predicate, attribute);

		}
	}

	/**
	 * This method will validate the tagged value of the CONCEPT_LOCATION tag
	 * and also validate the other required thats on depending the value of this tag.
	 * @param conceptLocation value of the tag
	 * @param identifyingXpath value of identifying _xpath tag
	 * @param predicate value of predicate condition tag
	 * @param attribute attribute for which the validation is going on.
	 * @throws DynamicExtensionsApplicationException exception.
	 */
	private static void validateConceptLocationTag(String conceptLocation, String identifyingXpath,
			String predicate, AttributeInterface attribute)
			throws DynamicExtensionsApplicationException
	{
		if (XMIConstants.NONE.equalsIgnoreCase(conceptLocation))
		{
			// identifying xpath  & predicate xpath are not mandatory using exor true true = false, false false =false
			//but if one of them is present then other should also be
			if (StringUtils.isBlank(identifyingXpath) ^ StringUtils.isBlank(predicate))
			{
				List<String> placeHolders = new ArrayList<String>();
				placeHolders.add(attribute.getName());
				placeHolders.add(attribute.getEntity().getName());
				throw new DynamicExtensionsApplicationException(ApplicationProperties.getValue(
						"xmi.invalid.identifying.predicate.xpath.tag", placeHolders));
			}

		}// both identifying as well as  predicate conditions are mandatory
		else if (XMIConstants.CONCEPT_CODE_LOC_ATTRIBUTE.equalsIgnoreCase(conceptLocation)
				|| XMIConstants.CONCEPT_CODE_LOC_PV.equalsIgnoreCase(conceptLocation))
		{
			if (StringUtils.isBlank(identifyingXpath) || StringUtils.isBlank(predicate))
			{
				List<String> placeHolders = new ArrayList<String>();
				placeHolders.add(attribute.getName());
				placeHolders.add(attribute.getEntity().getName());
				throw new DynamicExtensionsApplicationException(ApplicationProperties.getValue(
						"xmi.invalid.identifying.predicate.xpath.tag", placeHolders));
			}
		}
		else
		{
			List<String> placeHolders = new ArrayList<String>();
			placeHolders.add(attribute.getName());
			placeHolders.add(attribute.getEntity().getName());
			throw new DynamicExtensionsApplicationException(ApplicationProperties.getValue(
					"xmi.invalid.conceptlocation.tag", placeHolders));
		}
	}
}
