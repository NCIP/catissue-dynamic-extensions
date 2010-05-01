
package edu.common.dynamicextensions.categoryManager;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.bizlogic.BizLogicFactory;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.DoubleAttributeTypeInformation;
import edu.common.dynamicextensions.domain.FileAttributeTypeInformation;
import edu.common.dynamicextensions.domain.FileExtension;
import edu.common.dynamicextensions.domain.PathAssociationRelationInterface;
import edu.common.dynamicextensions.domain.StringAttributeTypeInformation;
import edu.common.dynamicextensions.domain.userinterface.Container;
import edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PathInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.DatePickerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.FileUploadInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.TextFieldInterface;
import edu.common.dynamicextensions.entitymanager.EntityGroupManager;
import edu.common.dynamicextensions.entitymanager.EntityGroupManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationDirection;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;

/**
 *
 * @author mandar_shidhore
 *
 */
public class MockCategoryManager extends DynamicExtensionsBaseTestCase
{

	/**
	 *
	 * @return Category
	 */
	public CategoryInterface createCategoryFromModel1()
	{
		CategoryInterface category = null;
		try
		{
			EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();
			DomainObjectFactory factory = DomainObjectFactory.getInstance();

			// Create entity group.
			EntityGroupInterface entityGroup = factory.createEntityGroup();
			entityGroup.setName("Entity Group " + new Double(Math.random()).toString());
			entityGroup.setLongName("Long Name "
					+ String.valueOf(new Double(Math.random()).intValue()));
			entityGroup.setShortName("Short Name "
					+ String.valueOf(new Double(Math.random()).intValue()));

			// Create entity trialAgent.
			EntityInterface trialAgent = createAndPopulateEntity();
			trialAgent.setName("TrialAgent");

			// Create attribute(s) for trialAgent entity.
			AttributeInterface agent = factory.createStringAttribute();
			agent.setName("Agent");
			((StringAttributeTypeInformation) agent.getAttributeTypeInformation()).setSize(40);

			trialAgent.addAbstractAttribute(agent);

			// Create entity chemotherapyTrials.
			EntityInterface chemotherapyTrials = createAndPopulateEntity();
			chemotherapyTrials.setName("ChemotherapyTrials");
			chemotherapyTrials.setParentEntity(trialAgent);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(chemotherapyTrials,
					false);

			// Create attribute(s) for chemotherapyTrials entity.
			AttributeInterface trialsName = factory.createStringAttribute();
			trialsName.setName("TrialsName");
			((StringAttributeTypeInformation) trialsName.getAttributeTypeInformation()).setSize(40);

			chemotherapyTrials.addAbstractAttribute(trialsName);

			// Create entity testDetails.
			EntityInterface testDetails = createAndPopulateEntity();
			testDetails.setName("TestDetails");

			// Create attribute(s) for testDetails entity.
			AttributeInterface testName = factory.createStringAttribute();
			testName.setName("TestName");
			((StringAttributeTypeInformation) testName.getAttributeTypeInformation()).setSize(40);

			testDetails.addAbstractAttribute(testName);

			// Create entity agentDetails.
			EntityInterface agentDetails = createAndPopulateEntity();
			agentDetails.setName("agentDetails");

			// Create attribute(s) for agentDetails entity.
			AttributeInterface molecularFormula = factory.createStringAttribute();
			molecularFormula.setName("MolecularFormula");
			((StringAttributeTypeInformation) molecularFormula.getAttributeTypeInformation())
					.setSize(40);

			agentDetails.addAbstractAttribute(molecularFormula);

			// Associate chemotherapyTrials entity with testDetails entity : chemotherapyTrials (1)------ >(*) testDetails
			AssociationInterface association1 = factory.createAssociation();
			association1.setName("chemotherapyTrialsTestDetailsAssociation");
			association1.setTargetEntity(testDetails);
			association1.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

			// Create source role for chemotherapyTrials to testDetails association.
			RoleInterface sourceRole1 = getRole(AssociationType.ASSOCIATION,
					"chemotherapyTrialsTestDetailsAssociation Source Role", Cardinality.ONE,
					Cardinality.ONE);
			sourceRole1.setAssociationsType(AssociationType.CONTAINTMENT);

			association1.setSourceRole(sourceRole1);
			association1.setTargetRole(getRole(AssociationType.ASSOCIATION,
					"chemotherapyTrialsTestDetailsAssociation Target Role", Cardinality.ZERO,
					Cardinality.MANY));

			chemotherapyTrials.addAbstractAttribute(association1);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association1);

			// Associate chemotherapyTrials entity with agentDetails entity : chemotherapyTrials (1)------ >(*) agentDetails
			AssociationInterface association2 = factory.createAssociation();
			association2.setName("chemotherapyTrialsAgentDetailsAssociation");
			association2.setTargetEntity(agentDetails);
			association2.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

			// Create source role for chemotherapyTrials to agentDetails association.
			RoleInterface sourceRole2 = getRole(AssociationType.ASSOCIATION,
					"chemotherapyTrialsAgentDetailsAssociation Source Role", Cardinality.ONE,
					Cardinality.ONE);
			sourceRole2.setAssociationsType(AssociationType.CONTAINTMENT);

			association2.setSourceRole(sourceRole2);
			association2.setTargetRole(getRole(AssociationType.ASSOCIATION,
					"chemotherapyTrialsAgentDetailsAssociation Target Role", Cardinality.ZERO,
					Cardinality.MANY));

			chemotherapyTrials.addAbstractAttribute(association2);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association2);

			// Create entity testResults.
			EntityInterface testResults = createAndPopulateEntity();
			testResults.setName("testResults");

			// Create attribute(s) for testResults entity.
			AttributeInterface resultType = factory.createStringAttribute();
			resultType.setName("ResultType");
			((StringAttributeTypeInformation) resultType.getAttributeTypeInformation()).setSize(40);

			testResults.addAbstractAttribute(resultType);

			// Create entity testIteration.
			EntityInterface testIteration = createAndPopulateEntity();
			testIteration.setName("testIteration");

			// Create attribute(s) for testIteration entity.
			AttributeInterface testCycle = factory.createStringAttribute();
			testCycle.setName("TestCycle");
			((StringAttributeTypeInformation) testCycle.getAttributeTypeInformation()).setSize(40);

			testIteration.addAbstractAttribute(testCycle);

			// Associate testDetails entity with testResults entity : testDetails (1)------ >(*) testResults
			AssociationInterface association3 = factory.createAssociation();
			association3.setName("testDetailsTestResultsAssociation");
			association3.setTargetEntity(testResults);
			association3.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

			// Create source role for testDetails to testResults association.
			RoleInterface sourceRole3 = getRole(AssociationType.ASSOCIATION,
					"testDetailsTestResultsAssociation Source Role", Cardinality.ONE,
					Cardinality.ONE);
			sourceRole3.setAssociationsType(AssociationType.CONTAINTMENT);

			association3.setSourceRole(sourceRole3);
			association3.setTargetRole(getRole(AssociationType.ASSOCIATION,
					"testDetailsTestResultsAssociation Target Role", Cardinality.ZERO,
					Cardinality.MANY));

			testDetails.addAbstractAttribute(association3);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association3);

			// Associate testDetails entity with testIteration entity : testDetails (1)------ >(*) testIteration
			AssociationInterface association4 = factory.createAssociation();
			association4.setName("testDetailsTestIterationAssociation");
			association4.setTargetEntity(testIteration);
			association4.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

			// Create source role for testDetails to testIteration association.
			RoleInterface sourceRole4 = getRole(AssociationType.ASSOCIATION,
					"testDetailsTestIterationAssociation Source Role", Cardinality.ONE,
					Cardinality.ONE);
			sourceRole4.setAssociationsType(AssociationType.CONTAINTMENT);

			association4.setSourceRole(sourceRole4);
			association4.setTargetRole(getRole(AssociationType.ASSOCIATION,
					"testDetailsTestIterationAssociation Target Role", Cardinality.ZERO,
					Cardinality.MANY));

			testDetails.addAbstractAttribute(association4);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association4);

			// Create entity radioTherapyTrials.
			EntityInterface radioTherapyTrials = createAndPopulateEntity();
			radioTherapyTrials.setName("RadioTherapyTrials");

			// Create attribute(s) for radioTherapyTrials entity.
			AttributeInterface radiationWavelengthType = factory.createStringAttribute();
			radiationWavelengthType.setName("RadiationWavelengthType");
			((StringAttributeTypeInformation) radiationWavelengthType.getAttributeTypeInformation())
					.setSize(40);

			radioTherapyTrials.addAbstractAttribute(radiationWavelengthType);

			// Create entity treatmentDetails.
			EntityInterface treatmentDetails = createAndPopulateEntity();
			treatmentDetails.setName("TreatmentDetails");

			// Create attribute(s) for radioTherapyTrials entity.
			AttributeInterface targetOrgan = factory.createStringAttribute();
			targetOrgan.setName("TargetOrgan");
			((StringAttributeTypeInformation) targetOrgan.getAttributeTypeInformation())
					.setSize(40);

			treatmentDetails.addAbstractAttribute(targetOrgan);

			// Create entity labDetails.
			EntityInterface labDetails = createAndPopulateEntity();
			labDetails.setName("LabDetails");

			// Create attribute(s) for labDetails entity.
			AttributeInterface labName = factory.createStringAttribute();
			labName.setName("LabName");
			((StringAttributeTypeInformation) labName.getAttributeTypeInformation()).setSize(40);

			labDetails.addAbstractAttribute(labName);

			// Create entity tumorInfo.
			EntityInterface tumorInfo = createAndPopulateEntity();
			tumorInfo.setName("TumorInfo");

			// Create attribute(s) for tumorInfo entity.
			AttributeInterface site = factory.createStringAttribute();
			site.setName("Site");
			((StringAttributeTypeInformation) site.getAttributeTypeInformation()).setSize(40);

			tumorInfo.addAbstractAttribute(site);

			// Associate trialAgent entity with radioTherapyTrials entity : trialAgent (1)------ >(*) radioTherapyTrials
			AssociationInterface association5 = factory.createAssociation();
			association5.setName("trialAgentRadioTherapyTrialsAssociation");
			association5.setTargetEntity(radioTherapyTrials);
			association5.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

			// Create source role for trialAgent to radioTherapyTrials association.
			RoleInterface sourceRole5 = getRole(AssociationType.ASSOCIATION,
					"trialAgentRadioTherapyTrialsAssociation Source Role", Cardinality.ONE,
					Cardinality.ONE);
			sourceRole5.setAssociationsType(AssociationType.CONTAINTMENT);

			association5.setSourceRole(sourceRole5);
			association5.setTargetRole(getRole(AssociationType.ASSOCIATION,
					"trialAgentRadioTherapyTrialsAssociation Target Role", Cardinality.ZERO,
					Cardinality.MANY));

			trialAgent.addAbstractAttribute(association5);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association5);

			// Associate radioTherapyTrials entity with treatmentDetails entity : trialAgent (1)------ >(*) radioTherapyTrials
			AssociationInterface association6 = factory.createAssociation();
			association6.setName("radioTherapyTrialsTreatmentDetailsAssociation");
			association6.setTargetEntity(treatmentDetails);
			association6.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

			// Create source role for radioTherapyTrials to treatmentDetails association.
			RoleInterface sourceRole6 = getRole(AssociationType.ASSOCIATION,
					"radioTherapyTrialsTreatmentDetailsAssociation Source Role", Cardinality.ONE,
					Cardinality.ONE);
			sourceRole6.setAssociationsType(AssociationType.CONTAINTMENT);

			association6.setSourceRole(sourceRole6);
			association6.setTargetRole(getRole(AssociationType.ASSOCIATION,
					"radioTherapyTrialsTreatmentDetailsAssociation Target Role", Cardinality.ZERO,
					Cardinality.MANY));

			radioTherapyTrials.addAbstractAttribute(association6);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association6);

			// Associate radioTherapyTrials entity with tumorInfo entity : radioTherapyTrials (1)------ >(*) tumorInfo
			AssociationInterface association7 = factory.createAssociation();
			association7.setName("radioTherapyTrialsTumorInfoAssociation");
			association7.setTargetEntity(tumorInfo);
			association7.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

			// Create source role for radioTherapyTrials to tumorInfo association.
			RoleInterface sourceRole7 = getRole(AssociationType.ASSOCIATION,
					"radioTherapyTrialsTreatmentDetailsAssociation Source Role", Cardinality.ONE,
					Cardinality.ONE);
			sourceRole7.setAssociationsType(AssociationType.CONTAINTMENT);

			association7.setSourceRole(sourceRole7);
			association7.setTargetRole(getRole(AssociationType.ASSOCIATION,
					"radioTherapyTrialsTreatmentDetailsAssociation Target Role", Cardinality.ZERO,
					Cardinality.MANY));

			radioTherapyTrials.addAbstractAttribute(association7);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association7);

			// Associate treatmentDetails entity with labDetails entity : treatmentDetails (1)------ >(*) labDetails
			AssociationInterface association8 = factory.createAssociation();
			association8.setName("treatmentDetailsLabDetailsAssociation");
			association8.setTargetEntity(labDetails);
			association8.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

			// Create source role for treatmentDetails to labDetails association.
			RoleInterface sourceRole8 = getRole(AssociationType.ASSOCIATION,
					"treatmentDetailsLabDetailsAssociation Source Role", Cardinality.ONE,
					Cardinality.ONE);
			sourceRole8.setAssociationsType(AssociationType.CONTAINTMENT);

			association8.setSourceRole(sourceRole8);
			association8.setTargetRole(getRole(AssociationType.ASSOCIATION,
					"treatmentDetailsLabDetailsAssociation Target Role", Cardinality.ZERO,
					Cardinality.MANY));

			treatmentDetails.addAbstractAttribute(association8);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association8);

			entityGroup.addEntity(trialAgent);
			trialAgent.setEntityGroup(entityGroup);
			entityGroup.addEntity(chemotherapyTrials);
			chemotherapyTrials.setEntityGroup(entityGroup);
			entityGroup.addEntity(radioTherapyTrials);
			radioTherapyTrials.setEntityGroup(entityGroup);
			entityGroup.addEntity(testDetails);
			testDetails.setEntityGroup(entityGroup);
			entityGroup.addEntity(agentDetails);
			agentDetails.setEntityGroup(entityGroup);
			entityGroup.addEntity(treatmentDetails);
			treatmentDetails.setEntityGroup(entityGroup);
			entityGroup.addEntity(tumorInfo);
			tumorInfo.setEntityGroup(entityGroup);
			entityGroup.addEntity(testResults);
			testResults.setEntityGroup(entityGroup);
			entityGroup.addEntity(testIteration);
			testIteration.setEntityGroup(entityGroup);
			entityGroup.addEntity(labDetails);
			labDetails.setEntityGroup(entityGroup);

			// Save entity group.
			entityGroupManager.persistEntityGroup(entityGroup);

			// Create a category.
			category = factory.createCategory();
			category.setName("Category");

			// Create trialAgentCategoryEntity.
			CategoryEntityInterface trialAgentCategoryEntity = factory.createCategoryEntity();
			trialAgentCategoryEntity.setName("trialAgentCategoryEntity");
			trialAgentCategoryEntity.setEntity(trialAgent);
			trialAgentCategoryEntity.setNumberOfEntries(-1);

			// Create category attribute(s) for trialAgentCategoryEntity.
			CategoryAttributeInterface agentCategoryAttribute = factory.createCategoryAttribute();
			agentCategoryAttribute.setName("agentCategoryAttribute");
			agentCategoryAttribute.setAbstractAttribute(agent);

			trialAgentCategoryEntity.addCategoryAttribute(agentCategoryAttribute);
			agentCategoryAttribute.setCategoryEntity(trialAgentCategoryEntity);

			// Create chemotherapyTrialsCategoryEntity.
			CategoryEntityInterface chemotherapyTrialsCategoryEntity = factory
					.createCategoryEntity();
			chemotherapyTrialsCategoryEntity.setName("chemotherapyTrialsCategoryEntity");
			chemotherapyTrialsCategoryEntity.setEntity(chemotherapyTrials);
			chemotherapyTrialsCategoryEntity.setNumberOfEntries(-1);
			chemotherapyTrialsCategoryEntity.setParentCategoryEntity(trialAgentCategoryEntity);

			// Create category attribute(s) for chemotherapyTrialsCategoryEntity.
			CategoryAttributeInterface trialsNameCategoryAttribute = factory
					.createCategoryAttribute();
			trialsNameCategoryAttribute.setName("trialNameCategoryAttribute");
			trialsNameCategoryAttribute.setAbstractAttribute(trialsName);

			chemotherapyTrialsCategoryEntity.addCategoryAttribute(trialsNameCategoryAttribute);
			trialsNameCategoryAttribute.setCategoryEntity(chemotherapyTrialsCategoryEntity);

			// Create radioTherapyTrialsCategoryEntity.
			CategoryEntityInterface radioTherapyTrialsCategoryEntity = factory
					.createCategoryEntity();
			radioTherapyTrialsCategoryEntity.setName("radioTherapyTrialsCategoryEntity");
			radioTherapyTrialsCategoryEntity.setEntity(radioTherapyTrials);
			radioTherapyTrialsCategoryEntity.setNumberOfEntries(-1);

			// Create category attribute(s) for radioTherapyTrialsCategoryEntity.
			CategoryAttributeInterface radiationWavelengthTypeCategoryAttribute = factory
					.createCategoryAttribute();
			radiationWavelengthTypeCategoryAttribute
					.setName("radiationWavelengthTypeCategoryAttribute");
			radiationWavelengthTypeCategoryAttribute.setAbstractAttribute(radiationWavelengthType);

			radioTherapyTrialsCategoryEntity
					.addCategoryAttribute(radiationWavelengthTypeCategoryAttribute);
			radiationWavelengthTypeCategoryAttribute
					.setCategoryEntity(radioTherapyTrialsCategoryEntity);

			// Create testDetailsCategoryEntity.
			CategoryEntityInterface testDetailsCategoryEntity = factory.createCategoryEntity();
			testDetailsCategoryEntity.setName("testDetailsCategoryEntity");
			testDetailsCategoryEntity.setEntity(testDetails);
			testDetailsCategoryEntity.setNumberOfEntries(-1);

			// Create category attribute(s) for testDetailsCategoryEntity.
			CategoryAttributeInterface testNameCategoryAttribute = factory
					.createCategoryAttribute();
			testNameCategoryAttribute.setName("testNameCategoryAttribute");
			testNameCategoryAttribute.setAbstractAttribute(testName);

			testDetailsCategoryEntity.addCategoryAttribute(testNameCategoryAttribute);
			testNameCategoryAttribute.setCategoryEntity(testDetailsCategoryEntity);

			// Create agentDetailsCategoryEntity.
			CategoryEntityInterface agentDetailsCategoryEntity = factory.createCategoryEntity();
			agentDetailsCategoryEntity.setName("agentDetailsCategoryEntity");
			agentDetailsCategoryEntity.setEntity(agentDetails);
			agentDetailsCategoryEntity.setNumberOfEntries(-1);

			// Create category attribute(s) for agentDetailsCategoryEntity.
			CategoryAttributeInterface molecularFormulaCategoryAttribute = factory
					.createCategoryAttribute();
			molecularFormulaCategoryAttribute.setName("molecularFormulaCategoryAttribute");
			molecularFormulaCategoryAttribute.setAbstractAttribute(molecularFormula);

			agentDetailsCategoryEntity.addCategoryAttribute(molecularFormulaCategoryAttribute);
			molecularFormulaCategoryAttribute.setCategoryEntity(agentDetailsCategoryEntity);

			//			// Create treatmentDetailsCategoryEntity.
			//			CategoryEntityInterface treatmentDetailsCategoryEntity = factory.createCategoryEntity();
			//			treatmentDetailsCategoryEntity.setName("agentDetailsCategoryEntity");
			//			treatmentDetailsCategoryEntity.setEntity(treatmentDetails);
			//			treatmentDetailsCategoryEntity.setNumberOfEntries(-1);
			//
			//			// Create category attribute(s) for treatmentDetailsCategoryEntity.
			//			CategoryAttributeInterface targetOrganCategoryAttribute = factory.createCategoryAttribute();
			//			targetOrganCategoryAttribute.setName("targetOrganCategoryAttribute");
			//			targetOrganCategoryAttribute.setAttribute(targetOrgan);
			//
			//			treatmentDetailsCategoryEntity.addCategoryAttribute(targetOrganCategoryAttribute);
			//			targetOrganCategoryAttribute.setCategoryEntity(treatmentDetailsCategoryEntity);

			// Create tumorInfoCategoryEntity.
			CategoryEntityInterface tumorInfoCategoryEntity = factory.createCategoryEntity();
			tumorInfoCategoryEntity.setName("tumorInfoCategoryEntity");
			tumorInfoCategoryEntity.setEntity(tumorInfo);
			tumorInfoCategoryEntity.setNumberOfEntries(-1);

			// Create category attribute(s) for tumorInfoCategoryEntity.
			CategoryAttributeInterface siteCategoryAttribute = factory.createCategoryAttribute();
			siteCategoryAttribute.setName("siteCategoryAttribute");
			siteCategoryAttribute.setAbstractAttribute(site);

			tumorInfoCategoryEntity.addCategoryAttribute(siteCategoryAttribute);
			siteCategoryAttribute.setCategoryEntity(tumorInfoCategoryEntity);

			// Create testResultsCategoryEntity.
			CategoryEntityInterface testResultsCategoryEntity = factory.createCategoryEntity();
			testResultsCategoryEntity.setName("testResultsCategoryEntity");
			testResultsCategoryEntity.setEntity(testResults);
			testResultsCategoryEntity.setNumberOfEntries(-1);

			// Create category attribute(s) for testResultsCategoryEntity.
			CategoryAttributeInterface resultTypeCategoryAttribute = factory
					.createCategoryAttribute();
			resultTypeCategoryAttribute.setName("resultTypeCategoryAttribute");
			resultTypeCategoryAttribute.setAbstractAttribute(resultType);

			testResultsCategoryEntity.addCategoryAttribute(resultTypeCategoryAttribute);
			resultTypeCategoryAttribute.setCategoryEntity(testResultsCategoryEntity);

			// Create testIterationCategoryEntity.
			CategoryEntityInterface testIterationCategoryEntity = factory.createCategoryEntity();
			testIterationCategoryEntity.setName("testIterationCategoryEntity");
			testIterationCategoryEntity.setEntity(testIteration);
			testIterationCategoryEntity.setNumberOfEntries(-1);

			// Create category attribute(s) for testIterationCategoryEntity.
			CategoryAttributeInterface testCycleCategoryAttribute = factory
					.createCategoryAttribute();
			testCycleCategoryAttribute.setName("testCycleCategoryAttribute");
			testCycleCategoryAttribute.setAbstractAttribute(testCycle);

			testIterationCategoryEntity.addCategoryAttribute(testCycleCategoryAttribute);
			testCycleCategoryAttribute.setCategoryEntity(testIterationCategoryEntity);

			// Create labDetailsCategoryEntity.
			CategoryEntityInterface labDetailsCategoryEntity = factory.createCategoryEntity();
			labDetailsCategoryEntity.setName("labDetailsCategoryEntity");
			labDetailsCategoryEntity.setEntity(labDetails);
			labDetailsCategoryEntity.setNumberOfEntries(-1);

			// Create category attribute(s) for labDetailsCategoryEntity.
			CategoryAttributeInterface labNameCategoryAttribute = factory.createCategoryAttribute();
			labNameCategoryAttribute.setName("labNameCategoryAttribute");
			labNameCategoryAttribute.setAbstractAttribute(labName);

			labDetailsCategoryEntity.addCategoryAttribute(labNameCategoryAttribute);
			labNameCategoryAttribute.setCategoryEntity(labDetailsCategoryEntity);

			// Create a path between radioTherapyTrialsCategoryEntity and labDetailsCategoryEntity.
			PathInterface path1 = factory.createPath();

			PathAssociationRelationInterface pathAssociationRelation1ForPath1 = factory
					.createPathAssociationRelation();
			pathAssociationRelation1ForPath1.setAssociation(association6);
			pathAssociationRelation1ForPath1.setPathSequenceNumber(1);

			PathAssociationRelationInterface pathAssociationRelation2ForPath1 = factory
					.createPathAssociationRelation();
			pathAssociationRelation2ForPath1.setAssociation(association8);
			pathAssociationRelation2ForPath1.setPathSequenceNumber(2);

			pathAssociationRelation1ForPath1.setPath(path1);
			pathAssociationRelation2ForPath1.setPath(path1);

			path1.addPathAssociationRelation(pathAssociationRelation1ForPath1);
			path1.addPathAssociationRelation(pathAssociationRelation2ForPath1);

			// Add path information to the target category entity.
			labDetailsCategoryEntity.setPath(path1);

			// Create a path between radioTherapyTrialsCategoryEntity and tumorInfoCategoryEntity.
			PathInterface path2 = factory.createPath();

			PathAssociationRelationInterface pathAssociationRelationForPath2 = factory
					.createPathAssociationRelation();
			pathAssociationRelationForPath2.setAssociation(association7);
			pathAssociationRelationForPath2.setPathSequenceNumber(1);

			pathAssociationRelationForPath2.setPath(path2);
			path2.addPathAssociationRelation(pathAssociationRelationForPath2);

			// Add path information to the target category entity.
			tumorInfoCategoryEntity.setPath(path2);

			// Create a path between trialAgentCategoryEntity and radioTherapyTrialsCategoryEntity.
			PathInterface path3 = factory.createPath();

			PathAssociationRelationInterface pathAssociationRelationForPath3 = factory
					.createPathAssociationRelation();
			pathAssociationRelationForPath3.setAssociation(association5);
			pathAssociationRelationForPath3.setPathSequenceNumber(1);

			pathAssociationRelationForPath3.setPath(path3);
			path3.addPathAssociationRelation(pathAssociationRelationForPath3);

			// Add path information to the target category entity.
			radioTherapyTrialsCategoryEntity.setPath(path3);

			// Create a path between chemotherapyTrialsCategoryEntity and testDetailsCategoryEntity.
			PathInterface path4 = factory.createPath();

			PathAssociationRelationInterface pathAssociationRelationForPath4 = factory
					.createPathAssociationRelation();
			pathAssociationRelationForPath4.setAssociation(association1);
			pathAssociationRelationForPath4.setPathSequenceNumber(1);

			pathAssociationRelationForPath4.setPath(path4);
			path4.addPathAssociationRelation(pathAssociationRelationForPath4);

			// Add path information to the target category entity.
			testDetailsCategoryEntity.setPath(path4);

			// Create a path between chemotherapyTrialsCategoryEntity and agentDetailsCategoryEntity.
			PathInterface path5 = factory.createPath();

			PathAssociationRelationInterface pathAssociationRelationForPath5 = factory
					.createPathAssociationRelation();
			pathAssociationRelationForPath5.setAssociation(association2);
			pathAssociationRelationForPath5.setPathSequenceNumber(1);

			pathAssociationRelationForPath5.setPath(path5);
			path5.addPathAssociationRelation(pathAssociationRelationForPath5);

			// Add path information to the target category entity.
			agentDetailsCategoryEntity.setPath(path5);

			// Create a path between testDetailsCategoryEntity and testResultsCategoryEntity.
			PathInterface path6 = factory.createPath();

			PathAssociationRelationInterface pathAssociationRelationForPath6 = factory
					.createPathAssociationRelation();
			pathAssociationRelationForPath6.setAssociation(association3);
			pathAssociationRelationForPath6.setPathSequenceNumber(1);

			pathAssociationRelationForPath6.setPath(path6);
			path6.addPathAssociationRelation(pathAssociationRelationForPath6);

			// Add path information to the target category entity.
			testResultsCategoryEntity.setPath(path6);

			// Create a path between testDetailsCategoryEntity and testIterationCategoryEntity.
			PathInterface path7 = factory.createPath();

			PathAssociationRelationInterface pathAssociationRelationForPath7 = factory
					.createPathAssociationRelation();
			pathAssociationRelationForPath7.setAssociation(association4);
			pathAssociationRelationForPath7.setPathSequenceNumber(1);

			pathAssociationRelationForPath7.setPath(path7);
			path7.addPathAssociationRelation(pathAssociationRelationForPath7);

			// Add path information to the target category entity.
			testIterationCategoryEntity.setPath(path7);

			// Create a category association between chemotherapyTrialsCategoryEntity and testDetailsCategoryEntity
			// that corresponds to association between chemotherapyTrials and testDetails.
			CategoryAssociationInterface categoryAssociation1 = factory.createCategoryAssociation();
			categoryAssociation1
					.setName("chemotherapyTrialsCategoryEntity to testDetailsCategoryEntity association");
			categoryAssociation1.setCategoryEntity(chemotherapyTrialsCategoryEntity);
			categoryAssociation1.setTargetCategoryEntity(testDetailsCategoryEntity);
			chemotherapyTrialsCategoryEntity.getCategoryAssociationCollection().add(
					categoryAssociation1);

			// Create a category association between chemotherapyTrialsCategoryEntity and agentDetailsCategoryEntity
			// that corresponds to association between chemotherapyTrials and agentDetails.
			CategoryAssociationInterface categoryAssociation2 = factory.createCategoryAssociation();
			categoryAssociation2
					.setName("chemotherapyTrialsCategoryEntity to agentDetailsCategoryEntity association");
			categoryAssociation2.setCategoryEntity(chemotherapyTrialsCategoryEntity);
			categoryAssociation2.setTargetCategoryEntity(agentDetailsCategoryEntity);
			chemotherapyTrialsCategoryEntity.getCategoryAssociationCollection().add(
					categoryAssociation2);

			// Create a category association between testDetailsCategoryEntity and testResultsCategoryEntity
			// that corresponds to association between testDetails and testResults.
			CategoryAssociationInterface categoryAssociation3 = factory.createCategoryAssociation();
			categoryAssociation3
					.setName("testDetailsCategoryEntity to testResultsCategoryEntity association");
			categoryAssociation3.setCategoryEntity(testDetailsCategoryEntity);
			categoryAssociation3.setTargetCategoryEntity(testResultsCategoryEntity);
			testDetailsCategoryEntity.getCategoryAssociationCollection().add(categoryAssociation3);

			// Create a category association between testDetailsCategoryEntity and testIterationCategoryEntity
			// that corresponds to association between testDetails and testIteration.
			CategoryAssociationInterface categoryAssociation4 = factory.createCategoryAssociation();
			categoryAssociation4
					.setName("testDetailsCategoryEntity to testIterationCategoryEntity association");
			categoryAssociation4.setCategoryEntity(testDetailsCategoryEntity);
			categoryAssociation4.setTargetCategoryEntity(testIterationCategoryEntity);
			testDetailsCategoryEntity.getCategoryAssociationCollection().add(categoryAssociation4);

			// Create a category association between trialAgentCategoryEntity and radioTherapyTrialsCategoryEntity
			// that corresponds to association between trialAgent and radioTherapyTrials.
			CategoryAssociationInterface categoryAssociation5 = factory.createCategoryAssociation();
			categoryAssociation5
					.setName("trialAgentCategoryEntity to radioTherapyTrialsCategoryEntity association");
			categoryAssociation5.setCategoryEntity(trialAgentCategoryEntity);
			categoryAssociation5.setTargetCategoryEntity(radioTherapyTrialsCategoryEntity);
			trialAgentCategoryEntity.getCategoryAssociationCollection().add(categoryAssociation5);

			// Create a category association between radioTherapyTrialsCategoryEntity and labDetailsCategoryEntity
			// that corresponds to association between radioTherapyTrials and radioTherapyTrials.
			CategoryAssociationInterface categoryAssociation6 = factory.createCategoryAssociation();
			categoryAssociation6
					.setName("radioTherapyTrialsCategoryEntity to labDetailsCategoryEntity association");
			categoryAssociation6.setCategoryEntity(radioTherapyTrialsCategoryEntity);
			categoryAssociation6.setTargetCategoryEntity(labDetailsCategoryEntity);
			radioTherapyTrialsCategoryEntity.getCategoryAssociationCollection().add(
					categoryAssociation6);

			// Create a category association between radioTherapyTrialsCategoryEntity and tumorInfoCategoryEntity
			// that corresponds to association between radioTherapyTrials and tumorInfo.
			CategoryAssociationInterface categoryAssociation7 = factory.createCategoryAssociation();
			categoryAssociation7
					.setName("radioTherapyTrialsCategoryEntity to tumorInfoCategoryEntity association");
			categoryAssociation7.setCategoryEntity(radioTherapyTrialsCategoryEntity);
			categoryAssociation7.setTargetCategoryEntity(tumorInfoCategoryEntity);
			radioTherapyTrialsCategoryEntity.getCategoryAssociationCollection().add(
					categoryAssociation7);

			chemotherapyTrialsCategoryEntity.addChildCategory(testDetailsCategoryEntity);
			chemotherapyTrialsCategoryEntity.addChildCategory(agentDetailsCategoryEntity);
			testDetailsCategoryEntity.addChildCategory(testResultsCategoryEntity);
			testDetailsCategoryEntity.addChildCategory(testIterationCategoryEntity);
			trialAgentCategoryEntity.addChildCategory(radioTherapyTrialsCategoryEntity);
			radioTherapyTrialsCategoryEntity.addChildCategory(labDetailsCategoryEntity);
			radioTherapyTrialsCategoryEntity.addChildCategory(tumorInfoCategoryEntity);

			// Create containers for category entities.
			int sequenceNumber = 1;

			// Create a container for trialAgentCategoryEntity.
			ContainerInterface trialAgentContainer = createContainer(trialAgentCategoryEntity);
			trialAgentContainer.setCaption("trialAgentContainer");

			// Create a control for agentCategoryAttribute.
			TextFieldInterface agentControl = createTextFieldControl(agentCategoryAttribute,
					sequenceNumber++);
			agentControl.setParentContainer((Container) trialAgentContainer);

			trialAgentContainer.addControl(agentControl);

			// Create a container for chemotherapyTrialsCategoryEntity.
			ContainerInterface chemotherapyTrialsContainer = createContainer(chemotherapyTrialsCategoryEntity);
			chemotherapyTrialsContainer.setCaption("chemotherapyTrialsContainer");

			// Create a control for trialsNameCategoryAttribute.
			TextFieldInterface trialsNameControl = createTextFieldControl(
					trialsNameCategoryAttribute, sequenceNumber++);
			trialsNameControl.setParentContainer((Container) chemotherapyTrialsContainer);

			chemotherapyTrialsContainer.addControl(trialsNameControl);

			// Create a container for trialAgentCategoryEntity.
			ContainerInterface testDetailsContainer = createContainer(testDetailsCategoryEntity);
			testDetailsContainer.setCaption("testDetailsContainer");

			// Create a control for testNameCategoryAttribute.
			TextFieldInterface testNameControl = createTextFieldControl(testNameCategoryAttribute,
					sequenceNumber++);
			testNameControl.setParentContainer((Container) testDetailsContainer);

			testDetailsContainer.addControl(testNameControl);

			// Create a container for agentDetailsCategoryEntity.
			ContainerInterface agentDetailsContainer = createContainer(agentDetailsCategoryEntity);
			agentDetailsContainer.setCaption("agentDetailsContainer");

			// Create a control for molecularFormulaCategoryAttribute.
			TextFieldInterface molecularFormulaControl = createTextFieldControl(
					molecularFormulaCategoryAttribute, sequenceNumber++);
			molecularFormulaControl.setParentContainer((Container) agentDetailsContainer);

			agentDetailsContainer.addControl(molecularFormulaControl);

			// Create a container for testResultsCategoryEntity.
			ContainerInterface testResultsContainer = createContainer(testResultsCategoryEntity);
			testResultsContainer.setCaption("testResultsContainer");

			// Create a control for resultTypeCategoryAttribute.
			TextFieldInterface resultTypeControl = createTextFieldControl(
					resultTypeCategoryAttribute, sequenceNumber++);
			resultTypeControl.setParentContainer((Container) testResultsContainer);

			testResultsContainer.addControl(resultTypeControl);

			// Create a container for testIterationCategoryEntity.
			ContainerInterface testIterationContainer = createContainer(testIterationCategoryEntity);
			testIterationContainer.setCaption("testIterationContainer");

			// Create a control for testCycleCategoryAttribute.
			TextFieldInterface testCycleControl = createTextFieldControl(
					testCycleCategoryAttribute, sequenceNumber++);
			testCycleControl.setParentContainer((Container) testIterationContainer);

			testIterationContainer.addControl(testCycleControl);

			// Create a container for radioTherapyTrialsCategoryEntity.
			ContainerInterface radioTherapyTrialsContainer = createContainer(radioTherapyTrialsCategoryEntity);
			radioTherapyTrialsContainer.setCaption("radioTherapyTrialsContainer");

			// Create a control for radiationWavelengthTypeCategoryAttribute.
			TextFieldInterface radiationWavelengthTypeControl = createTextFieldControl(
					radiationWavelengthTypeCategoryAttribute, sequenceNumber++);
			radiationWavelengthTypeControl
					.setParentContainer((Container) radioTherapyTrialsContainer);

			radioTherapyTrialsContainer.addControl(radiationWavelengthTypeControl);

			// Create a container for labDetailsCategoryEntity.
			ContainerInterface labDetailsContainer = createContainer(labDetailsCategoryEntity);
			labDetailsContainer.setCaption("labDetailsContainer");

			// Create a control for labNameCategoryAttribute.
			TextFieldInterface labNameControl = createTextFieldControl(labNameCategoryAttribute,
					sequenceNumber++);
			labNameControl.setParentContainer((Container) labDetailsContainer);

			labDetailsContainer.addControl(labNameControl);

			// Create a container for tumorInfoCategoryEntity.
			ContainerInterface tumorInfoContainer = createContainer(tumorInfoCategoryEntity);
			tumorInfoContainer.setCaption("tumorInfoContainer");

			// Create a control for siteCategoryAttribute.
			TextFieldInterface siteControl = createTextFieldControl(siteCategoryAttribute,
					sequenceNumber++);
			siteControl.setParentContainer((Container) tumorInfoContainer);

			tumorInfoContainer.addControl(siteControl);

			// Create a containment control.
			AbstractContainmentControlInterface containmentControl1 = factory
					.createCategoryAssociationControl();
			containmentControl1.setBaseAbstractAttribute(categoryAssociation1);
			containmentControl1.setSequenceNumber(sequenceNumber++);
			containmentControl1.setCaption("containmentControl1");
			containmentControl1.setContainer(testDetailsContainer);
			containmentControl1.setParentContainer((Container) chemotherapyTrialsContainer);

			chemotherapyTrialsContainer.addControl(containmentControl1);

			// Create a containment control.
			AbstractContainmentControlInterface containmentControl2 = factory
					.createCategoryAssociationControl();
			containmentControl2.setBaseAbstractAttribute(categoryAssociation2);
			containmentControl2.setSequenceNumber(sequenceNumber++);
			containmentControl2.setCaption("containmentControl2");
			containmentControl2.setContainer(agentDetailsContainer);
			containmentControl2.setParentContainer((Container) chemotherapyTrialsContainer);

			chemotherapyTrialsContainer.addControl(containmentControl2);

			// Create a containment control.
			AbstractContainmentControlInterface containmentControl3 = factory
					.createCategoryAssociationControl();
			containmentControl3.setBaseAbstractAttribute(categoryAssociation3);
			containmentControl3.setSequenceNumber(sequenceNumber++);
			containmentControl3.setCaption("containmentControl3");
			containmentControl3.setContainer(testResultsContainer);
			containmentControl3.setParentContainer((Container) testDetailsContainer);

			testDetailsContainer.addControl(containmentControl3);

			// Create a containment control.
			AbstractContainmentControlInterface containmentControl4 = factory
					.createCategoryAssociationControl();
			containmentControl4.setBaseAbstractAttribute(categoryAssociation4);
			containmentControl4.setSequenceNumber(sequenceNumber++);
			containmentControl4.setCaption("containmentControl4");
			containmentControl4.setContainer(testIterationContainer);
			containmentControl4.setParentContainer((Container) testDetailsContainer);

			testDetailsContainer.addControl(containmentControl4);

			// Create a containment control.
			AbstractContainmentControlInterface containmentControl5 = factory
					.createCategoryAssociationControl();
			containmentControl5.setBaseAbstractAttribute(categoryAssociation5);
			containmentControl5.setSequenceNumber(sequenceNumber++);
			containmentControl5.setCaption("containmentControl5");
			containmentControl5.setContainer(radioTherapyTrialsContainer);
			containmentControl5.setParentContainer((Container) trialAgentContainer);

			trialAgentContainer.addControl(containmentControl5);

			// Create a containment control.
			AbstractContainmentControlInterface containmentControl6 = factory
					.createCategoryAssociationControl();
			containmentControl6.setBaseAbstractAttribute(categoryAssociation6);
			containmentControl6.setSequenceNumber(sequenceNumber++);
			containmentControl6.setCaption("containmentControl6");
			containmentControl6.setContainer(labDetailsContainer);
			containmentControl6.setParentContainer((Container) radioTherapyTrialsContainer);

			radioTherapyTrialsContainer.addControl(containmentControl6);

			// Create a containment control.
			AbstractContainmentControlInterface containmentControl7 = factory
					.createCategoryAssociationControl();
			containmentControl7.setBaseAbstractAttribute(categoryAssociation7);
			containmentControl7.setSequenceNumber(sequenceNumber++);
			containmentControl7.setCaption("containmentControl7");
			containmentControl7.setContainer(tumorInfoContainer);
			containmentControl7.setParentContainer((Container) radioTherapyTrialsContainer);

			radioTherapyTrialsContainer.addControl(containmentControl7);

			// Link containers.
			chemotherapyTrialsContainer.setBaseContainer(trialAgentContainer);

			// Set root category element.
			category.setRootCategoryElement(chemotherapyTrialsCategoryEntity);
			chemotherapyTrialsCategoryEntity.setCategory(category);

			// Create data value map for category data insertion.
			//			Map<BaseAbstractAttributeInterface, Object> categoryDataValueMap = new HashMap<BaseAbstractAttributeInterface, Object>();
			//			//categoryDataValueMap.put(trialsNameCategoryAttribute, trialsNameCategoryAttribute.getName());
			//			for (CategoryAttributeInterface ca: chemotherapyTrialsCategoryEntity.getAllCategoryAttributes())
			//			{
			//				categoryDataValueMap.put(ca, ca.getName());
			//			}
			//
			//			// For chemotherapyTrialsCategoryEntity.
			//			List<Map<BaseAbstractAttributeInterface, Object>> list1 = new ArrayList<Map<BaseAbstractAttributeInterface,Object>>();
			//			Map<BaseAbstractAttributeInterface, Object> map1 = new HashMap<BaseAbstractAttributeInterface, Object>();
			//			map1.put(testNameCategoryAttribute, testNameCategoryAttribute.getName());
			//
			//			// For testResultsCategoryEntity.
			//			List<Map<BaseAbstractAttributeInterface, Object>> list2 = new ArrayList<Map<BaseAbstractAttributeInterface,Object>>();
			//			Map<BaseAbstractAttributeInterface, Object> map2 = new HashMap<BaseAbstractAttributeInterface, Object>();
			//			map2.put(resultTypeCategoryAttribute, resultTypeCategoryAttribute.getName());
			//			list2.add(map2);
			//
			//			// For testIterationCategoryEntity.
			//			List<Map<BaseAbstractAttributeInterface, Object>> list3 = new ArrayList<Map<BaseAbstractAttributeInterface,Object>>();
			//			Map<BaseAbstractAttributeInterface, Object> map3 = new HashMap<BaseAbstractAttributeInterface, Object>();
			//			map3.put(testCycleCategoryAttribute, testCycleCategoryAttribute.getName());
			//			list3.add(map3);
			//
			//			map1.put(categoryAssociation3, list2);
			//			map1.put(categoryAssociation4, list3);
			//
			//			list1.add(map1);
			//
			//			categoryDataValueMap.put(categoryAssociation1, list1);
			//
			//			// For agentDetailsCategoryEntity.
			//			List<Map<BaseAbstractAttributeInterface, Object>> list4 = new ArrayList<Map<BaseAbstractAttributeInterface,Object>>();
			//			Map<BaseAbstractAttributeInterface, Object> map4 = new HashMap<BaseAbstractAttributeInterface, Object>();
			//			map4.put(molecularFormulaCategoryAttribute, molecularFormulaCategoryAttribute.getName());
			//			list4.add(map4);
			//
			//			categoryDataValueMap.put(categoryAssociation2, list4);
			//
			//			// For radioTherapyTrialsCategoryEntity.
			//			List<Map<BaseAbstractAttributeInterface, Object>> list5 = new ArrayList<Map<BaseAbstractAttributeInterface,Object>>();
			//			Map<BaseAbstractAttributeInterface, Object> map5 = new HashMap<BaseAbstractAttributeInterface, Object>();
			//			map5.put(radiationWavelengthTypeCategoryAttribute, radiationWavelengthTypeCategoryAttribute.getName());
			//
			//			// For labDetailsCategoryEntity.
			//			List<Map<BaseAbstractAttributeInterface, Object>> list6 = new ArrayList<Map<BaseAbstractAttributeInterface,Object>>();
			//			Map<BaseAbstractAttributeInterface, Object> map6 = new HashMap<BaseAbstractAttributeInterface, Object>();
			//			map6.put(labNameCategoryAttribute, labNameCategoryAttribute.getName());
			//			list6.add(map6);
			//
			//			map5.put(categoryAssociation6, list6);
			//
			//			// For tumorInfoCategoryEntity.
			//			List<Map<BaseAbstractAttributeInterface, Object>> list7 = new ArrayList<Map<BaseAbstractAttributeInterface,Object>>();
			//			Map<BaseAbstractAttributeInterface, Object> map7 = new HashMap<BaseAbstractAttributeInterface, Object>();
			//			map7.put(siteCategoryAttribute, siteCategoryAttribute.getName());
			//			list7.add(map7);
			//
			//			map5.put(categoryAssociation7, list7);
			//
			//			list5.add(map5);
			//
			//			categoryDataValueMap.put(categoryAssociation5, list5);
			//
			//			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			//			categoryManager.persistCategory(category);
			//			categoryManager.insertData(category, categoryDataValueMap);
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
		}

		return category;
	}

	/**
	 *
	 * @param category
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	public CategoryInterface addNewCategoryEntityToExistingCategory(CategoryInterface category)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();
		EntityGroupInterface entityGroup = category.getRootCategoryElement().getEntity()
				.getEntityGroup();

		// Create geneticInfo entity.
		EntityInterface geneticInfo = createAndPopulateEntity();
		geneticInfo.setName("GeneticInfo");

		// Create attribute(s) for geneticInfo entity.
		AttributeInterface chromosomeName = factory.createStringAttribute();
		chromosomeName.setName("chromosomeName");
		((StringAttributeTypeInformation) chromosomeName.getAttributeTypeInformation()).setSize(40);

		geneticInfo.addAbstractAttribute(chromosomeName);

		EntityInterface chemotherapyTrials = entityGroup.getEntityByName("ChemotherapyTrials");

		// Associate chemotherapyTrials entity with geneticInfo entity : chemotherapyTrials (1)------ >(*) geneticInfo
		AssociationInterface association = factory.createAssociation();
		association.setName("chemotherapyTrialsGeneticInfoAssociation");
		association.setTargetEntity(geneticInfo);
		association.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

		// Create source role for chemotherapyTrials to geneticInfo association.
		RoleInterface sourceRole = getRole(AssociationType.ASSOCIATION,
				"chemotherapyTrialsGeneticInfoAssociation Source Role", Cardinality.ONE,
				Cardinality.ONE);
		sourceRole.setAssociationsType(AssociationType.CONTAINTMENT);

		association.setSourceRole(sourceRole);
		association.setTargetRole(getRole(AssociationType.ASSOCIATION,
				"chemotherapyTrialsGeneticInfoAssociation Target Role", Cardinality.ZERO,
				Cardinality.MANY));

		chemotherapyTrials.addAbstractAttribute(association);
		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association);

		entityGroup.addEntity(geneticInfo);
		geneticInfo.setEntityGroup(entityGroup);

		// Save entity group.
		entityGroupManager.persistEntityGroup(entityGroup);

		// Create geneticInfoCategoryEntity.
		CategoryEntityInterface geneticInfoCategoryEntity = factory.createCategoryEntity();
		geneticInfoCategoryEntity.setName("geneticInfoCategoryEntity");
		geneticInfoCategoryEntity.setEntity(geneticInfo);
		geneticInfoCategoryEntity.setNumberOfEntries(-1);

		// Create category attribute(s) for geneticInfoCategoryEntity.
		CategoryAttributeInterface chromosomeNameCategoryAttribute = factory
				.createCategoryAttribute();
		chromosomeNameCategoryAttribute.setName("chromosomeNameCategoryAttribute");
		chromosomeNameCategoryAttribute.setAbstractAttribute(chromosomeName);

		geneticInfoCategoryEntity.addCategoryAttribute(chromosomeNameCategoryAttribute);
		chromosomeNameCategoryAttribute.setCategoryEntity(geneticInfoCategoryEntity);

		CategoryEntityInterface chemotherapyTrialsCategoryEntity = category
				.getRootCategoryElement();
		chemotherapyTrialsCategoryEntity.addChildCategory(geneticInfoCategoryEntity);
		List<ContainerInterface> containersList = new ArrayList<ContainerInterface>(
				chemotherapyTrialsCategoryEntity.getContainerCollection());

		// Create a path between chemotherapyTrialsCategoryEntity and geneticInfoCategoryEntity.
		PathInterface path = factory.createPath();

		PathAssociationRelationInterface pathAssociationRelation = factory
				.createPathAssociationRelation();
		pathAssociationRelation.setAssociation(association);
		pathAssociationRelation.setPathSequenceNumber(1);

		pathAssociationRelation.setPath(path);
		path.addPathAssociationRelation(pathAssociationRelation);

		// Add path information to the target category entity.
		geneticInfoCategoryEntity.setPath(path);

		// Create a category association between chemotherapyTrialsCategoryEntity and geneticInfoCategoryEntity
		// that corresponds to association between chemotherapyTrials and geneticInfo.
		CategoryAssociationInterface categoryAssociation = factory.createCategoryAssociation();
		categoryAssociation
				.setName("chemotherapyTrialsCategoryEntity to geneticInfoCategoryEntity association");
		categoryAssociation.setCategoryEntity(chemotherapyTrialsCategoryEntity);
		categoryAssociation.setTargetCategoryEntity(geneticInfoCategoryEntity);
		chemotherapyTrialsCategoryEntity.getCategoryAssociationCollection()
				.add(categoryAssociation);

		// Create a container for geneticInfoCategoryEntity.
		int sequenceNumber = 1;

		// Create a container for geneticInfoCategoryEntity.
		ContainerInterface geneticInfoContainer = createContainer(geneticInfoCategoryEntity);
		geneticInfoContainer.setCaption("geneticInfoContainer");

		// Create a control for chromosomeNameCategoryAttribute.
		TextFieldInterface chromosomeNameControl = createTextFieldControl(
				chromosomeNameCategoryAttribute, sequenceNumber++);
		chromosomeNameControl.setParentContainer((Container) geneticInfoContainer);

		geneticInfoContainer.addControl(chromosomeNameControl);

		// Create a containment control.
		AbstractContainmentControlInterface containmentControl = factory
				.createCategoryAssociationControl();
		containmentControl.setBaseAbstractAttribute(categoryAssociation);
		containmentControl.setSequenceNumber(sequenceNumber++);
		containmentControl.setCaption("containmentControl8");
		containmentControl.setContainer(geneticInfoContainer);
		containmentControl.setParentContainer((Container) containersList.get(0));

		containersList.get(0).addControl(containmentControl);

		return category;
	}

	/**
	 *
	 * @param category
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	public CategoryInterface addCategoryAttributetyToCategoryEntity(CategoryInterface category)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		DomainObjectFactory factory = DomainObjectFactory.getInstance();
		EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();
		EntityGroupInterface entityGroup = category.getRootCategoryElement().getEntity()
				.getEntityGroup();

		// Retrieve the entity to which we want to add an attribute.
		EntityInterface chemotherapyTrials = entityGroup.getEntityByName("ChemotherapyTrials");

		// Create attribute(s) for chemotherapyTrials entity.
		AttributeInterface trialsType = factory.createStringAttribute();
		trialsType.setName("trialsType");
		((StringAttributeTypeInformation) trialsType.getAttributeTypeInformation()).setSize(40);

		chemotherapyTrials.addAbstractAttribute(trialsType);

		// Save entity group.
		entityGroupManager.persistEntityGroup(entityGroup);

		// Retrieve the category entity to which we wish to add a category attribute.
		CategoryEntityInterface chemotherapyTrialsCategoryEntity = category
				.getRootCategoryElement();

		// Create category attribute(s) for chemotherapyTrialsCategoryEntity.
		CategoryAttributeInterface trialsTypeCategoryAttribute = factory.createCategoryAttribute();
		trialsTypeCategoryAttribute.setName("trialsTypeCategoryAttribute");
		trialsTypeCategoryAttribute.setAbstractAttribute(trialsType);

		chemotherapyTrialsCategoryEntity.addCategoryAttribute(trialsTypeCategoryAttribute);
		trialsTypeCategoryAttribute.setCategoryEntity(chemotherapyTrialsCategoryEntity);

		// Fetch container for chemotherapyTrialsCategoryEntity.
		List<ContainerInterface> containersList = new ArrayList<ContainerInterface>(
				chemotherapyTrialsCategoryEntity.getContainerCollection());

		int sequenceNumber = 1;
		ContainerInterface chemotherapyTrialsContainer = containersList.get(0);

		// Create a control for trialsTypeCategoryAttribute.
		TextFieldInterface trialsTypeControl = createTextFieldControl(trialsTypeCategoryAttribute,
				sequenceNumber++);
		trialsTypeControl.setParentContainer((Container) chemotherapyTrialsContainer);

		chemotherapyTrialsContainer.addControl(trialsTypeControl);

		return category;
	}

	/**
	 *
	 * @return Category
	 */
	public CategoryInterface createCategoryFromModel1WithDifferentAttributeTypes()
	{
		CategoryInterface category = null;
		try
		{
			EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();
			DomainObjectFactory factory = DomainObjectFactory.getInstance();

			// Create entity group.
			EntityGroupInterface entityGroup = factory.createEntityGroup();
			entityGroup.setName("Entity Group " + new Double(Math.random()).toString());
			entityGroup.setLongName("Long Name "
					+ String.valueOf(new Double(Math.random()).intValue()));
			entityGroup.setShortName("Short Name "
					+ String.valueOf(new Double(Math.random()).intValue()));

			// Create entity trialAgent.
			EntityInterface trialAgent = createAndPopulateEntity();
			trialAgent.setName("TrialAgent");

			// Create attribute(s) for trialAgent entity.
			AttributeInterface agentBirthDate = factory.createDateAttribute();
			agentBirthDate.setName("Agent Birth Date");
			((DateAttributeTypeInformation) agentBirthDate.getAttributeTypeInformation())
					.setFormat(ProcessorConstants.DATE_FORMAT_OPTION_DATEONLY);

			trialAgent.addAbstractAttribute(agentBirthDate);

			// Create entity chemotherapyTrials.
			EntityInterface chemotherapyTrials = createAndPopulateEntity();
			chemotherapyTrials.setName("ChemotherapyTrials");
			chemotherapyTrials.setParentEntity(trialAgent);
			DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(chemotherapyTrials,
					false);

			// Create attribute(s) for chemotherapyTrials entity.
			AttributeInterface trialsValue = factory.createDoubleAttribute();
			trialsValue.setName("TrialsValue");
			((DoubleAttributeTypeInformation) trialsValue.getAttributeTypeInformation())
					.setDecimalPlaces(2);

			chemotherapyTrials.addAbstractAttribute(trialsValue);

			// Create entity testDetails.
			EntityInterface testDetails = createAndPopulateEntity();
			testDetails.setName("TestDetails");

			// Create attribute(s) for testDetails entity.
			AttributeInterface testFile = factory.createFileAttribute();
			testFile.setName("testFile");

			FileExtension fileExtension = new FileExtension();
			fileExtension.setFileExtension("txt");
			((FileAttributeTypeInformation) testFile.getAttributeTypeInformation())
					.getFileExtensionCollection().add(fileExtension);

			testDetails.addAbstractAttribute(testFile);

			// Create entity agentDetails.
			EntityInterface agentDetails = createAndPopulateEntity();
			agentDetails.setName("agentDetails");

			// Create attribute(s) for agentDetails entity.
			AttributeInterface molecularFormula = factory.createStringAttribute();
			molecularFormula.setName("MolecularFormula");
			((StringAttributeTypeInformation) molecularFormula.getAttributeTypeInformation())
					.setSize(40);

			agentDetails.addAbstractAttribute(molecularFormula);

			// Associate chemotherapyTrials entity with testDetails entity : chemotherapyTrials (1)------ >(*) testDetails
			AssociationInterface association1 = factory.createAssociation();
			association1.setName("chemotherapyTrialsTestDetailsAssociation");
			association1.setTargetEntity(testDetails);
			association1.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

			// Create source role for chemotherapyTrials to testDetails association.
			RoleInterface sourceRole1 = getRole(AssociationType.ASSOCIATION,
					"chemotherapyTrialsTestDetailsAssociation Source Role", Cardinality.ONE,
					Cardinality.ONE);
			sourceRole1.setAssociationsType(AssociationType.CONTAINTMENT);

			association1.setSourceRole(sourceRole1);
			association1.setTargetRole(getRole(AssociationType.ASSOCIATION,
					"chemotherapyTrialsTestDetailsAssociation Target Role", Cardinality.ZERO,
					Cardinality.MANY));

			chemotherapyTrials.addAbstractAttribute(association1);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association1);

			// Associate chemotherapyTrials entity with agentDetails entity : chemotherapyTrials (1)------ >(*) agentDetails
			AssociationInterface association2 = factory.createAssociation();
			association2.setName("chemotherapyTrialsAgentDetailsAssociation");
			association2.setTargetEntity(agentDetails);
			association2.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

			// Create source role for chemotherapyTrials to agentDetails association.
			RoleInterface sourceRole2 = getRole(AssociationType.ASSOCIATION,
					"chemotherapyTrialsAgentDetailsAssociation Source Role", Cardinality.ONE,
					Cardinality.ONE);
			sourceRole2.setAssociationsType(AssociationType.CONTAINTMENT);

			association2.setSourceRole(sourceRole2);
			association2.setTargetRole(getRole(AssociationType.ASSOCIATION,
					"chemotherapyTrialsAgentDetailsAssociation Target Role", Cardinality.ZERO,
					Cardinality.MANY));

			chemotherapyTrials.addAbstractAttribute(association2);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association2);

			// Create entity testResults.
			EntityInterface testResults = createAndPopulateEntity();
			testResults.setName("testResults");

			// Create attribute(s) for testResults entity.
			AttributeInterface resultType = factory.createStringAttribute();
			resultType.setName("ResultType");
			((StringAttributeTypeInformation) resultType.getAttributeTypeInformation()).setSize(40);

			testResults.addAbstractAttribute(resultType);

			// Create entity testIteration.
			EntityInterface testIteration = createAndPopulateEntity();
			testIteration.setName("testIteration");

			// Create attribute(s) for testIteration entity.
			AttributeInterface testCycle = factory.createStringAttribute();
			testCycle.setName("TestCycle");
			((StringAttributeTypeInformation) testCycle.getAttributeTypeInformation()).setSize(40);

			testIteration.addAbstractAttribute(testCycle);

			// Associate testDetails entity with testResults entity : testDetails (1)------ >(*) testResults
			AssociationInterface association3 = factory.createAssociation();
			association3.setName("testDetailsTestResultsAssociation");
			association3.setTargetEntity(testResults);
			association3.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

			// Create source role for testDetails to testResults association.
			RoleInterface sourceRole3 = getRole(AssociationType.ASSOCIATION,
					"testDetailsTestResultsAssociation Source Role", Cardinality.ONE,
					Cardinality.ONE);
			sourceRole3.setAssociationsType(AssociationType.CONTAINTMENT);

			association3.setSourceRole(sourceRole3);
			association3.setTargetRole(getRole(AssociationType.ASSOCIATION,
					"testDetailsTestResultsAssociation Target Role", Cardinality.ZERO,
					Cardinality.MANY));

			testDetails.addAbstractAttribute(association3);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association3);

			// Associate testDetails entity with testIteration entity : testDetails (1)------ >(*) testIteration
			AssociationInterface association4 = factory.createAssociation();
			association4.setName("testDetailsTestIterationAssociation");
			association4.setTargetEntity(testIteration);
			association4.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

			// Create source role for testDetails to testIteration association.
			RoleInterface sourceRole4 = getRole(AssociationType.ASSOCIATION,
					"testDetailsTestIterationAssociation Source Role", Cardinality.ONE,
					Cardinality.ONE);
			sourceRole4.setAssociationsType(AssociationType.CONTAINTMENT);

			association4.setSourceRole(sourceRole4);
			association4.setTargetRole(getRole(AssociationType.ASSOCIATION,
					"testDetailsTestIterationAssociation Target Role", Cardinality.ZERO,
					Cardinality.MANY));

			testDetails.addAbstractAttribute(association4);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association4);

			// Create entity radioTherapyTrials.
			EntityInterface radioTherapyTrials = createAndPopulateEntity();
			radioTherapyTrials.setName("RadioTherapyTrials");

			// Create attribute(s) for radioTherapyTrials entity.
			AttributeInterface radiationWavelengthType = factory.createStringAttribute();
			radiationWavelengthType.setName("RadiationWavelengthType");
			((StringAttributeTypeInformation) radiationWavelengthType.getAttributeTypeInformation())
					.setSize(40);

			radioTherapyTrials.addAbstractAttribute(radiationWavelengthType);

			// Create entity treatmentDetails.
			EntityInterface treatmentDetails = createAndPopulateEntity();
			treatmentDetails.setName("TreatmentDetails");

			// Create attribute(s) for radioTherapyTrials entity.
			AttributeInterface targetOrgan = factory.createStringAttribute();
			targetOrgan.setName("TargetOrgan");
			((StringAttributeTypeInformation) targetOrgan.getAttributeTypeInformation())
					.setSize(40);

			treatmentDetails.addAbstractAttribute(targetOrgan);

			// Create entity labDetails.
			EntityInterface labDetails = createAndPopulateEntity();
			labDetails.setName("LabDetails");

			// Create attribute(s) for labDetails entity.
			AttributeInterface labName = factory.createStringAttribute();
			labName.setName("LabName");
			((StringAttributeTypeInformation) labName.getAttributeTypeInformation()).setSize(40);

			labDetails.addAbstractAttribute(labName);

			// Create entity tumorInfo.
			EntityInterface tumorInfo = createAndPopulateEntity();
			tumorInfo.setName("TumorInfo");

			// Create attribute(s) for tumorInfo entity.
			AttributeInterface site = factory.createStringAttribute();
			site.setName("Site");
			((StringAttributeTypeInformation) site.getAttributeTypeInformation()).setSize(40);

			tumorInfo.addAbstractAttribute(site);

			// Associate trialAgent entity with radioTherapyTrials entity : trialAgent (1)------ >(*) radioTherapyTrials
			AssociationInterface association5 = factory.createAssociation();
			association5.setName("trialAgentRadioTherapyTrialsAssociation");
			association5.setTargetEntity(radioTherapyTrials);
			association5.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

			// Create source role for trialAgent to radioTherapyTrials association.
			RoleInterface sourceRole5 = getRole(AssociationType.ASSOCIATION,
					"trialAgentRadioTherapyTrialsAssociation Source Role", Cardinality.ONE,
					Cardinality.ONE);
			sourceRole5.setAssociationsType(AssociationType.CONTAINTMENT);

			association5.setSourceRole(sourceRole5);
			association5.setTargetRole(getRole(AssociationType.ASSOCIATION,
					"trialAgentRadioTherapyTrialsAssociation Target Role", Cardinality.ZERO,
					Cardinality.MANY));

			trialAgent.addAbstractAttribute(association5);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association5);

			// Associate radioTherapyTrials entity with treatmentDetails entity : trialAgent (1)------ >(*) radioTherapyTrials
			AssociationInterface association6 = factory.createAssociation();
			association6.setName("radioTherapyTrialsTreatmentDetailsAssociation");
			association6.setTargetEntity(treatmentDetails);
			association6.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

			// Create source role for radioTherapyTrials to treatmentDetails association.
			RoleInterface sourceRole6 = getRole(AssociationType.ASSOCIATION,
					"radioTherapyTrialsTreatmentDetailsAssociation Source Role", Cardinality.ONE,
					Cardinality.ONE);
			sourceRole6.setAssociationsType(AssociationType.CONTAINTMENT);

			association6.setSourceRole(sourceRole6);
			association6.setTargetRole(getRole(AssociationType.ASSOCIATION,
					"radioTherapyTrialsTreatmentDetailsAssociation Target Role", Cardinality.ZERO,
					Cardinality.MANY));

			radioTherapyTrials.addAbstractAttribute(association6);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association6);

			// Associate radioTherapyTrials entity with tumorInfo entity : radioTherapyTrials (1)------ >(*) tumorInfo
			AssociationInterface association7 = factory.createAssociation();
			association7.setName("radioTherapyTrialsTumorInfoAssociation");
			association7.setTargetEntity(tumorInfo);
			association7.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

			// Create source role for radioTherapyTrials to tumorInfo association.
			RoleInterface sourceRole7 = getRole(AssociationType.ASSOCIATION,
					"radioTherapyTrialsTreatmentDetailsAssociation Source Role", Cardinality.ONE,
					Cardinality.ONE);
			sourceRole7.setAssociationsType(AssociationType.CONTAINTMENT);

			association7.setSourceRole(sourceRole7);
			association7.setTargetRole(getRole(AssociationType.ASSOCIATION,
					"radioTherapyTrialsTreatmentDetailsAssociation Target Role", Cardinality.ZERO,
					Cardinality.MANY));

			radioTherapyTrials.addAbstractAttribute(association7);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association7);

			// Associate treatmentDetails entity with labDetails entity : treatmentDetails (1)------ >(*) labDetails
			AssociationInterface association8 = factory.createAssociation();
			association8.setName("treatmentDetailsLabDetailsAssociation");
			association8.setTargetEntity(labDetails);
			association8.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

			// Create source role for treatmentDetails to labDetails association.
			RoleInterface sourceRole8 = getRole(AssociationType.ASSOCIATION,
					"treatmentDetailsLabDetailsAssociation Source Role", Cardinality.ONE,
					Cardinality.ONE);
			sourceRole8.setAssociationsType(AssociationType.CONTAINTMENT);

			association8.setSourceRole(sourceRole8);
			association8.setTargetRole(getRole(AssociationType.ASSOCIATION,
					"treatmentDetailsLabDetailsAssociation Target Role", Cardinality.ZERO,
					Cardinality.MANY));

			treatmentDetails.addAbstractAttribute(association8);
			DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association8);

			entityGroup.addEntity(trialAgent);
			trialAgent.setEntityGroup(entityGroup);
			entityGroup.addEntity(chemotherapyTrials);
			chemotherapyTrials.setEntityGroup(entityGroup);
			entityGroup.addEntity(radioTherapyTrials);
			radioTherapyTrials.setEntityGroup(entityGroup);
			entityGroup.addEntity(testDetails);
			testDetails.setEntityGroup(entityGroup);
			entityGroup.addEntity(agentDetails);
			agentDetails.setEntityGroup(entityGroup);
			entityGroup.addEntity(treatmentDetails);
			treatmentDetails.setEntityGroup(entityGroup);
			entityGroup.addEntity(tumorInfo);
			tumorInfo.setEntityGroup(entityGroup);
			entityGroup.addEntity(testResults);
			testResults.setEntityGroup(entityGroup);
			entityGroup.addEntity(testIteration);
			testIteration.setEntityGroup(entityGroup);
			entityGroup.addEntity(labDetails);
			labDetails.setEntityGroup(entityGroup);

			// Save entity group.
			entityGroupManager.persistEntityGroup(entityGroup);

			// Create a category.
			category = factory.createCategory();
			category.setName("Category");

			// Create trialAgentCategoryEntity.
			CategoryEntityInterface trialAgentCategoryEntity = factory.createCategoryEntity();
			trialAgentCategoryEntity.setName("trialAgentCategoryEntity");
			trialAgentCategoryEntity.setEntity(trialAgent);
			trialAgentCategoryEntity.setNumberOfEntries(-1);

			// Create category attribute(s) for trialAgentCategoryEntity.
			CategoryAttributeInterface agentCategoryAttribute = factory.createCategoryAttribute();
			agentCategoryAttribute.setName("agentCategoryAttribute");
			agentCategoryAttribute.setAbstractAttribute(agentBirthDate);

			trialAgentCategoryEntity.addCategoryAttribute(agentCategoryAttribute);
			agentCategoryAttribute.setCategoryEntity(trialAgentCategoryEntity);

			// Create chemotherapyTrialsCategoryEntity.
			CategoryEntityInterface chemotherapyTrialsCategoryEntity = factory
					.createCategoryEntity();
			chemotherapyTrialsCategoryEntity.setName("chemotherapyTrialsCategoryEntity");
			chemotherapyTrialsCategoryEntity.setEntity(chemotherapyTrials);
			chemotherapyTrialsCategoryEntity.setNumberOfEntries(-1);
			chemotherapyTrialsCategoryEntity.setParentCategoryEntity(trialAgentCategoryEntity);

			// Create category attribute(s) for chemotherapyTrialsCategoryEntity.
			CategoryAttributeInterface trialsValueCategoryAttribute = factory
					.createCategoryAttribute();
			trialsValueCategoryAttribute.setName("trialNameCategoryAttribute");
			trialsValueCategoryAttribute.setAbstractAttribute(trialsValue);

			chemotherapyTrialsCategoryEntity.addCategoryAttribute(trialsValueCategoryAttribute);
			trialsValueCategoryAttribute.setCategoryEntity(chemotherapyTrialsCategoryEntity);

			// Create radioTherapyTrialsCategoryEntity.
			CategoryEntityInterface radioTherapyTrialsCategoryEntity = factory
					.createCategoryEntity();
			radioTherapyTrialsCategoryEntity.setName("radioTherapyTrialsCategoryEntity");
			radioTherapyTrialsCategoryEntity.setEntity(radioTherapyTrials);
			radioTherapyTrialsCategoryEntity.setNumberOfEntries(-1);

			// Create category attribute(s) for radioTherapyTrialsCategoryEntity.
			CategoryAttributeInterface radiationWavelengthTypeCategoryAttribute = factory
					.createCategoryAttribute();
			radiationWavelengthTypeCategoryAttribute
					.setName("radiationWavelengthTypeCategoryAttribute");
			radiationWavelengthTypeCategoryAttribute.setAbstractAttribute(radiationWavelengthType);

			radioTherapyTrialsCategoryEntity
					.addCategoryAttribute(radiationWavelengthTypeCategoryAttribute);
			radiationWavelengthTypeCategoryAttribute
					.setCategoryEntity(radioTherapyTrialsCategoryEntity);

			// Create testDetailsCategoryEntity.
			CategoryEntityInterface testDetailsCategoryEntity = factory.createCategoryEntity();
			testDetailsCategoryEntity.setName("testDetailsCategoryEntity");
			testDetailsCategoryEntity.setEntity(testDetails);
			testDetailsCategoryEntity.setNumberOfEntries(-1);

			// Create category attribute(s) for testDetailsCategoryEntity.
			CategoryAttributeInterface testFileCategoryAttribute = factory
					.createCategoryAttribute();
			testFileCategoryAttribute.setName("testNameCategoryAttribute");
			testFileCategoryAttribute.setAbstractAttribute(testFile);

			testDetailsCategoryEntity.addCategoryAttribute(testFileCategoryAttribute);
			testFileCategoryAttribute.setCategoryEntity(testDetailsCategoryEntity);

			// Create agentDetailsCategoryEntity.
			CategoryEntityInterface agentDetailsCategoryEntity = factory.createCategoryEntity();
			agentDetailsCategoryEntity.setName("agentDetailsCategoryEntity");
			agentDetailsCategoryEntity.setEntity(agentDetails);
			agentDetailsCategoryEntity.setNumberOfEntries(-1);

			// Create category attribute(s) for agentDetailsCategoryEntity.
			CategoryAttributeInterface molecularFormulaCategoryAttribute = factory
					.createCategoryAttribute();
			molecularFormulaCategoryAttribute.setName("molecularFormulaCategoryAttribute");
			molecularFormulaCategoryAttribute.setAbstractAttribute(molecularFormula);

			agentDetailsCategoryEntity.addCategoryAttribute(molecularFormulaCategoryAttribute);
			molecularFormulaCategoryAttribute.setCategoryEntity(agentDetailsCategoryEntity);

			//			// Create treatmentDetailsCategoryEntity.
			//			CategoryEntityInterface treatmentDetailsCategoryEntity = factory.createCategoryEntity();
			//			treatmentDetailsCategoryEntity.setName("agentDetailsCategoryEntity");
			//			treatmentDetailsCategoryEntity.setEntity(treatmentDetails);
			//			treatmentDetailsCategoryEntity.setNumberOfEntries(-1);
			//
			//			// Create category attribute(s) for treatmentDetailsCategoryEntity.
			//			CategoryAttributeInterface targetOrganCategoryAttribute = factory.createCategoryAttribute();
			//			targetOrganCategoryAttribute.setName("targetOrganCategoryAttribute");
			//			targetOrganCategoryAttribute.setAttribute(targetOrgan);
			//
			//			treatmentDetailsCategoryEntity.addCategoryAttribute(targetOrganCategoryAttribute);
			//			targetOrganCategoryAttribute.setCategoryEntity(treatmentDetailsCategoryEntity);

			// Create tumorInfoCategoryEntity.
			CategoryEntityInterface tumorInfoCategoryEntity = factory.createCategoryEntity();
			tumorInfoCategoryEntity.setName("tumorInfoCategoryEntity");
			tumorInfoCategoryEntity.setEntity(tumorInfo);
			tumorInfoCategoryEntity.setNumberOfEntries(-1);

			// Create category attribute(s) for tumorInfoCategoryEntity.
			CategoryAttributeInterface siteCategoryAttribute = factory.createCategoryAttribute();
			siteCategoryAttribute.setName("siteCategoryAttribute");
			siteCategoryAttribute.setAbstractAttribute(site);

			tumorInfoCategoryEntity.addCategoryAttribute(siteCategoryAttribute);
			siteCategoryAttribute.setCategoryEntity(tumorInfoCategoryEntity);

			// Create testResultsCategoryEntity.
			CategoryEntityInterface testResultsCategoryEntity = factory.createCategoryEntity();
			testResultsCategoryEntity.setName("testResultsCategoryEntity");
			testResultsCategoryEntity.setEntity(testResults);
			testResultsCategoryEntity.setNumberOfEntries(-1);

			// Create category attribute(s) for testResultsCategoryEntity.
			CategoryAttributeInterface resultTypeCategoryAttribute = factory
					.createCategoryAttribute();
			resultTypeCategoryAttribute.setName("siteCategoryAttribute");
			resultTypeCategoryAttribute.setAbstractAttribute(resultType);

			testResultsCategoryEntity.addCategoryAttribute(resultTypeCategoryAttribute);
			resultTypeCategoryAttribute.setCategoryEntity(testResultsCategoryEntity);

			// Create testIterationCategoryEntity.
			CategoryEntityInterface testIterationCategoryEntity = factory.createCategoryEntity();
			testIterationCategoryEntity.setName("testIterationCategoryEntity");
			testIterationCategoryEntity.setEntity(testIteration);
			testIterationCategoryEntity.setNumberOfEntries(-1);

			// Create category attribute(s) for testIterationCategoryEntity.
			CategoryAttributeInterface testCycleCategoryAttribute = factory
					.createCategoryAttribute();
			testCycleCategoryAttribute.setName("testCycleCategoryAttribute");
			testCycleCategoryAttribute.setAbstractAttribute(testCycle);

			testIterationCategoryEntity.addCategoryAttribute(testCycleCategoryAttribute);
			testCycleCategoryAttribute.setCategoryEntity(testIterationCategoryEntity);

			// Create labDetailsCategoryEntity.
			CategoryEntityInterface labDetailsCategoryEntity = factory.createCategoryEntity();
			labDetailsCategoryEntity.setName("labDetailsCategoryEntity");
			labDetailsCategoryEntity.setEntity(labDetails);
			labDetailsCategoryEntity.setNumberOfEntries(-1);

			// Create category attribute(s) for labDetailsCategoryEntity.
			CategoryAttributeInterface labNameCategoryAttribute = factory.createCategoryAttribute();
			labNameCategoryAttribute.setName("labNameCategoryAttribute");
			labNameCategoryAttribute.setAbstractAttribute(labName);

			labDetailsCategoryEntity.addCategoryAttribute(labNameCategoryAttribute);
			labNameCategoryAttribute.setCategoryEntity(labDetailsCategoryEntity);

			// Create a path between radioTherapyTrialsCategoryEntity and labDetailsCategoryEntity.
			PathInterface path1 = factory.createPath();

			PathAssociationRelationInterface pathAssociationRelation1ForPath1 = factory
					.createPathAssociationRelation();
			pathAssociationRelation1ForPath1.setAssociation(association6);
			pathAssociationRelation1ForPath1.setPathSequenceNumber(1);

			PathAssociationRelationInterface pathAssociationRelation2ForPath1 = factory
					.createPathAssociationRelation();
			pathAssociationRelation2ForPath1.setAssociation(association8);
			pathAssociationRelation2ForPath1.setPathSequenceNumber(2);

			pathAssociationRelation1ForPath1.setPath(path1);
			pathAssociationRelation2ForPath1.setPath(path1);

			path1.addPathAssociationRelation(pathAssociationRelation1ForPath1);
			path1.addPathAssociationRelation(pathAssociationRelation2ForPath1);

			// Add path information to the target category entity.
			labDetailsCategoryEntity.setPath(path1);

			// Create a path between radioTherapyTrialsCategoryEntity and tumorInfoCategoryEntity.
			PathInterface path2 = factory.createPath();

			PathAssociationRelationInterface pathAssociationRelationForPath2 = factory
					.createPathAssociationRelation();
			pathAssociationRelationForPath2.setAssociation(association7);
			pathAssociationRelationForPath2.setPathSequenceNumber(1);

			pathAssociationRelationForPath2.setPath(path2);
			path2.addPathAssociationRelation(pathAssociationRelationForPath2);

			// Add path information to the target category entity.
			tumorInfoCategoryEntity.setPath(path2);

			// Create a path between trialAgentCategoryEntity and radioTherapyTrialsCategoryEntity.
			PathInterface path3 = factory.createPath();

			PathAssociationRelationInterface pathAssociationRelationForPath3 = factory
					.createPathAssociationRelation();
			pathAssociationRelationForPath3.setAssociation(association5);
			pathAssociationRelationForPath3.setPathSequenceNumber(1);

			pathAssociationRelationForPath3.setPath(path3);
			path3.addPathAssociationRelation(pathAssociationRelationForPath3);

			// Add path information to the target category entity.
			radioTherapyTrialsCategoryEntity.setPath(path3);

			// Create a path between chemotherapyTrialsCategoryEntity and testDetailsCategoryEntity.
			PathInterface path4 = factory.createPath();

			PathAssociationRelationInterface pathAssociationRelationForPath4 = factory
					.createPathAssociationRelation();
			pathAssociationRelationForPath4.setAssociation(association1);
			pathAssociationRelationForPath4.setPathSequenceNumber(1);

			pathAssociationRelationForPath4.setPath(path4);
			path4.addPathAssociationRelation(pathAssociationRelationForPath4);

			// Add path information to the target category entity.
			testDetailsCategoryEntity.setPath(path4);

			// Create a path between chemotherapyTrialsCategoryEntity and agentDetailsCategoryEntity.
			PathInterface path5 = factory.createPath();

			PathAssociationRelationInterface pathAssociationRelationForPath5 = factory
					.createPathAssociationRelation();
			pathAssociationRelationForPath5.setAssociation(association2);
			pathAssociationRelationForPath5.setPathSequenceNumber(1);

			pathAssociationRelationForPath5.setPath(path5);
			path5.addPathAssociationRelation(pathAssociationRelationForPath5);

			// Add path information to the target category entity.
			agentDetailsCategoryEntity.setPath(path5);

			// Create a path between testDetailsCategoryEntity and testResultsCategoryEntity.
			PathInterface path6 = factory.createPath();

			PathAssociationRelationInterface pathAssociationRelationForPath6 = factory
					.createPathAssociationRelation();
			pathAssociationRelationForPath6.setAssociation(association3);
			pathAssociationRelationForPath6.setPathSequenceNumber(1);

			pathAssociationRelationForPath6.setPath(path6);
			path6.addPathAssociationRelation(pathAssociationRelationForPath6);

			// Add path information to the target category entity.
			testResultsCategoryEntity.setPath(path6);

			// Create a path between testDetailsCategoryEntity and testIterationCategoryEntity.
			PathInterface path7 = factory.createPath();

			PathAssociationRelationInterface pathAssociationRelationForPath7 = factory
					.createPathAssociationRelation();
			pathAssociationRelationForPath7.setAssociation(association4);
			pathAssociationRelationForPath7.setPathSequenceNumber(1);

			pathAssociationRelationForPath7.setPath(path7);
			path7.addPathAssociationRelation(pathAssociationRelationForPath7);

			// Add path information to the target category entity.
			testIterationCategoryEntity.setPath(path7);

			// Create a category association between chemotherapyTrialsCategoryEntity and testDetailsCategoryEntity
			// that corresponds to association between chemotherapyTrials and testDetails.
			CategoryAssociationInterface categoryAssociation1 = factory.createCategoryAssociation();
			categoryAssociation1
					.setName("chemotherapyTrialsCategoryEntity to testDetailsCategoryEntity association");
			categoryAssociation1.setCategoryEntity(chemotherapyTrialsCategoryEntity);
			categoryAssociation1.setTargetCategoryEntity(testDetailsCategoryEntity);
			chemotherapyTrialsCategoryEntity.getCategoryAssociationCollection().add(
					categoryAssociation1);

			// Create a category association between chemotherapyTrialsCategoryEntity and agentDetailsCategoryEntity
			// that corresponds to association between chemotherapyTrials and agentDetails.
			CategoryAssociationInterface categoryAssociation2 = factory.createCategoryAssociation();
			categoryAssociation2
					.setName("chemotherapyTrialsCategoryEntity to agentDetailsCategoryEntity association");
			categoryAssociation2.setCategoryEntity(chemotherapyTrialsCategoryEntity);
			categoryAssociation2.setTargetCategoryEntity(agentDetailsCategoryEntity);
			chemotherapyTrialsCategoryEntity.getCategoryAssociationCollection().add(
					categoryAssociation2);

			// Create a category association between testDetailsCategoryEntity and testResultsCategoryEntity
			// that corresponds to association between testDetails and testResults.
			CategoryAssociationInterface categoryAssociation3 = factory.createCategoryAssociation();
			categoryAssociation3
					.setName("testDetailsCategoryEntity to testResultsCategoryEntity association");
			categoryAssociation3.setCategoryEntity(testDetailsCategoryEntity);
			categoryAssociation3.setTargetCategoryEntity(testResultsCategoryEntity);
			testDetailsCategoryEntity.getCategoryAssociationCollection().add(categoryAssociation3);

			// Create a category association between testDetailsCategoryEntity and testIterationCategoryEntity
			// that corresponds to association between testDetails and testIteration.
			CategoryAssociationInterface categoryAssociation4 = factory.createCategoryAssociation();
			categoryAssociation4
					.setName("testDetailsCategoryEntity to testIterationCategoryEntity association");
			categoryAssociation4.setCategoryEntity(testDetailsCategoryEntity);
			categoryAssociation4.setTargetCategoryEntity(testIterationCategoryEntity);
			testDetailsCategoryEntity.getCategoryAssociationCollection().add(categoryAssociation4);

			// Create a category association between trialAgentCategoryEntity and radioTherapyTrialsCategoryEntity
			// that corresponds to association between trialAgent and radioTherapyTrials.
			CategoryAssociationInterface categoryAssociation5 = factory.createCategoryAssociation();
			categoryAssociation5
					.setName("trialAgentCategoryEntity to radioTherapyTrialsCategoryEntity association");
			categoryAssociation5.setCategoryEntity(trialAgentCategoryEntity);
			categoryAssociation5.setTargetCategoryEntity(radioTherapyTrialsCategoryEntity);
			trialAgentCategoryEntity.getCategoryAssociationCollection().add(categoryAssociation5);

			// Create a category association between radioTherapyTrialsCategoryEntity and labDetailsCategoryEntity
			// that corresponds to association between radioTherapyTrials and radioTherapyTrials.
			CategoryAssociationInterface categoryAssociation6 = factory.createCategoryAssociation();
			categoryAssociation6
					.setName("radioTherapyTrialsCategoryEntity to labDetailsCategoryEntity association");
			categoryAssociation6.setCategoryEntity(radioTherapyTrialsCategoryEntity);
			categoryAssociation6.setTargetCategoryEntity(labDetailsCategoryEntity);
			radioTherapyTrialsCategoryEntity.getCategoryAssociationCollection().add(
					categoryAssociation6);

			// Create a category association between radioTherapyTrialsCategoryEntity and tumorInfoCategoryEntity
			// that corresponds to association between radioTherapyTrials and tumorInfo.
			CategoryAssociationInterface categoryAssociation7 = factory.createCategoryAssociation();
			categoryAssociation7
					.setName("radioTherapyTrialsCategoryEntity to tumorInfoCategoryEntity association");
			categoryAssociation7.setCategoryEntity(radioTherapyTrialsCategoryEntity);
			categoryAssociation7.setTargetCategoryEntity(tumorInfoCategoryEntity);
			radioTherapyTrialsCategoryEntity.getCategoryAssociationCollection().add(
					categoryAssociation7);

			chemotherapyTrialsCategoryEntity.addChildCategory(testDetailsCategoryEntity);
			chemotherapyTrialsCategoryEntity.addChildCategory(agentDetailsCategoryEntity);
			testDetailsCategoryEntity.addChildCategory(testResultsCategoryEntity);
			testDetailsCategoryEntity.addChildCategory(testIterationCategoryEntity);
			trialAgentCategoryEntity.addChildCategory(radioTherapyTrialsCategoryEntity);
			radioTherapyTrialsCategoryEntity.addChildCategory(labDetailsCategoryEntity);
			radioTherapyTrialsCategoryEntity.addChildCategory(tumorInfoCategoryEntity);

			// Create containers for category entities.
			int sequenceNumber = 1;

			// Create a container for trialAgentCategoryEntity.
			ContainerInterface trialAgentContainer = createContainer(trialAgentCategoryEntity);
			trialAgentContainer.setCaption("trialAgentContainer");

			// Create a control for agentCategoryAttribute.
			DatePickerInterface agentControl = createDateControl(agentCategoryAttribute,
					sequenceNumber++);
			agentControl.setParentContainer((Container) trialAgentContainer);

			trialAgentContainer.addControl(agentControl);

			// Create a container for chemotherapyTrialsCategoryEntity.
			ContainerInterface chemotherapyTrialsContainer = createContainer(chemotherapyTrialsCategoryEntity);
			chemotherapyTrialsContainer.setCaption("chemotherapyTrialsContainer");

			// Create a control for trialsNameCategoryAttribute.
			TextFieldInterface trialsValueControl = createTextFieldControl(
					trialsValueCategoryAttribute, sequenceNumber++);
			trialsValueControl.setParentContainer((Container) chemotherapyTrialsContainer);

			chemotherapyTrialsContainer.addControl(trialsValueControl);

			// Create a container for trialAgentCategoryEntity.
			ContainerInterface testDetailsContainer = createContainer(testDetailsCategoryEntity);
			testDetailsContainer.setCaption("testDetailsContainer");

			// Create a control for testNameCategoryAttribute.
			FileUploadInterface testFileControl = createFileControl(testFileCategoryAttribute,
					sequenceNumber++);
			testFileControl.setParentContainer((Container) testDetailsContainer);

			testDetailsContainer.addControl(testFileControl);

			// Create a container for agentDetailsCategoryEntity.
			ContainerInterface agentDetailsContainer = createContainer(agentDetailsCategoryEntity);
			agentDetailsContainer.setCaption("agentDetailsContainer");

			// Create a control for molecularFormulaCategoryAttribute.
			TextFieldInterface molecularFormulaControl = createTextFieldControl(
					molecularFormulaCategoryAttribute, sequenceNumber++);
			molecularFormulaControl.setParentContainer((Container) agentDetailsContainer);

			agentDetailsContainer.addControl(molecularFormulaControl);

			// Create a container for testResultsCategoryEntity.
			ContainerInterface testResultsContainer = createContainer(testResultsCategoryEntity);
			testResultsContainer.setCaption("testResultsContainer");

			// Create a control for resultTypeCategoryAttribute.
			TextFieldInterface resultTypeControl = createTextFieldControl(
					resultTypeCategoryAttribute, sequenceNumber++);
			resultTypeControl.setParentContainer((Container) testResultsContainer);

			testResultsContainer.addControl(resultTypeControl);

			// Create a container for testIterationCategoryEntity.
			ContainerInterface testIterationContainer = createContainer(testIterationCategoryEntity);
			testIterationContainer.setCaption("testIterationContainer");

			// Create a control for testCycleCategoryAttribute.
			TextFieldInterface testCycleControl = createTextFieldControl(
					testCycleCategoryAttribute, sequenceNumber++);
			testCycleControl.setParentContainer((Container) testIterationContainer);

			testIterationContainer.addControl(testCycleControl);

			// Create a container for radioTherapyTrialsCategoryEntity.
			ContainerInterface radioTherapyTrialsContainer = createContainer(radioTherapyTrialsCategoryEntity);
			radioTherapyTrialsContainer.setCaption("radioTherapyTrialsContainer");

			// Create a control for radiationWavelengthTypeCategoryAttribute.
			TextFieldInterface radiationWavelengthTypeControl = createTextFieldControl(
					radiationWavelengthTypeCategoryAttribute, sequenceNumber++);
			radiationWavelengthTypeControl
					.setParentContainer((Container) radioTherapyTrialsContainer);

			radioTherapyTrialsContainer.addControl(radiationWavelengthTypeControl);

			// Create a container for labDetailsCategoryEntity.
			ContainerInterface labDetailsContainer = createContainer(labDetailsCategoryEntity);
			labDetailsContainer.setCaption("labDetailsContainer");

			// Create a control for labNameCategoryAttribute.
			TextFieldInterface labNameControl = createTextFieldControl(labNameCategoryAttribute,
					sequenceNumber++);
			labNameControl.setParentContainer((Container) labDetailsContainer);

			labDetailsContainer.addControl(labNameControl);

			// Create a container for tumorInfoCategoryEntity.
			ContainerInterface tumorInfoContainer = createContainer(tumorInfoCategoryEntity);
			tumorInfoContainer.setCaption("tumorInfoContainer");

			// Create a control for siteCategoryAttribute.
			TextFieldInterface siteControl = createTextFieldControl(siteCategoryAttribute,
					sequenceNumber++);
			siteControl.setParentContainer((Container) tumorInfoContainer);

			tumorInfoContainer.addControl(siteControl);

			// Create a containment control.
			AbstractContainmentControlInterface containmentControl1 = factory
					.createCategoryAssociationControl();
			containmentControl1.setBaseAbstractAttribute(categoryAssociation1);
			containmentControl1.setSequenceNumber(sequenceNumber++);
			containmentControl1.setCaption("containmentControl1");
			containmentControl1.setContainer(testDetailsContainer);
			containmentControl1.setParentContainer((Container) chemotherapyTrialsContainer);

			chemotherapyTrialsContainer.addControl(containmentControl1);

			// Create a containment control.
			AbstractContainmentControlInterface containmentControl2 = factory
					.createCategoryAssociationControl();
			containmentControl2.setBaseAbstractAttribute(categoryAssociation2);
			containmentControl2.setSequenceNumber(sequenceNumber++);
			containmentControl2.setCaption("containmentControl2");
			containmentControl2.setContainer(agentDetailsContainer);
			containmentControl2.setParentContainer((Container) chemotherapyTrialsContainer);

			chemotherapyTrialsContainer.addControl(containmentControl2);

			// Create a containment control.
			AbstractContainmentControlInterface containmentControl3 = factory
					.createCategoryAssociationControl();
			containmentControl3.setBaseAbstractAttribute(categoryAssociation3);
			containmentControl3.setSequenceNumber(sequenceNumber++);
			containmentControl3.setCaption("containmentControl3");
			containmentControl3.setContainer(testResultsContainer);
			containmentControl3.setParentContainer((Container) testDetailsContainer);

			testDetailsContainer.addControl(containmentControl3);

			// Create a containment control.
			AbstractContainmentControlInterface containmentControl4 = factory
					.createCategoryAssociationControl();
			containmentControl4.setBaseAbstractAttribute(categoryAssociation4);
			containmentControl4.setSequenceNumber(sequenceNumber++);
			containmentControl4.setCaption("containmentControl4");
			containmentControl4.setContainer(testIterationContainer);
			containmentControl4.setParentContainer((Container) testDetailsContainer);

			testDetailsContainer.addControl(containmentControl4);

			// Create a containment control.
			AbstractContainmentControlInterface containmentControl5 = factory
					.createCategoryAssociationControl();
			containmentControl5.setBaseAbstractAttribute(categoryAssociation5);
			containmentControl5.setSequenceNumber(sequenceNumber++);
			containmentControl5.setCaption("containmentControl5");
			containmentControl5.setContainer(radioTherapyTrialsContainer);
			containmentControl5.setParentContainer((Container) trialAgentContainer);

			trialAgentContainer.addControl(containmentControl5);

			// Create a containment control.
			AbstractContainmentControlInterface containmentControl6 = factory
					.createCategoryAssociationControl();
			containmentControl6.setBaseAbstractAttribute(categoryAssociation6);
			containmentControl6.setSequenceNumber(sequenceNumber++);
			containmentControl6.setCaption("containmentControl6");
			containmentControl6.setContainer(labDetailsContainer);
			containmentControl6.setParentContainer((Container) radioTherapyTrialsContainer);

			radioTherapyTrialsContainer.addControl(containmentControl6);

			// Create a containment control.
			AbstractContainmentControlInterface containmentControl7 = factory
					.createCategoryAssociationControl();
			containmentControl7.setBaseAbstractAttribute(categoryAssociation7);
			containmentControl7.setSequenceNumber(sequenceNumber++);
			containmentControl7.setCaption("containmentControl7");
			containmentControl7.setContainer(tumorInfoContainer);
			containmentControl7.setParentContainer((Container) radioTherapyTrialsContainer);

			radioTherapyTrialsContainer.addControl(containmentControl7);

			// Link containers.
			chemotherapyTrialsContainer.setBaseContainer(trialAgentContainer);

			// Set root category element.
			category.setRootCategoryElement(chemotherapyTrialsCategoryEntity);
			chemotherapyTrialsCategoryEntity.setCategory(category);
		}
		catch (DynamicExtensionsSystemException e)
		{
			e.printStackTrace();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			e.printStackTrace();
		}

		return category;
	}

	/**
	 *
	 * @param objectClassName
	 * @param identifier
	 * @return
	 */
	protected AbstractMetadataInterface getObjectByIdentifier(String objectClassName, int identifier)
	{
		Object object = null;

		DefaultBizLogic defaultBizLogic = BizLogicFactory.getDefaultBizLogic();
		List objectList = new ArrayList();

		try
		{
			objectList = defaultBizLogic.retrieve(objectClassName, "id", identifier);
		}
		catch (BizLogicException e)
		{
			e.printStackTrace();
		}

		if (objectList != null && objectList.size() > 0)
		{
			object = (DynamicExtensionBaseDomainObjectInterface) objectList.get(0);
		}

		return (AbstractMetadataInterface) object;
	}

	/**
	 * @param associationType associationType
	 * @param name name
	 * @param minCard  minCard
	 * @param maxCard maxCard
	 * @return  RoleInterface
	 */
	protected RoleInterface getRole(AssociationType associationType, String name,
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
	 *
	 * @param categoryEntity
	 * @return ContainerInterface
	 */
	private ContainerInterface createContainer(CategoryEntityInterface categoryEntity)
	{
		ContainerInterface containerInterface = DomainObjectFactory.getInstance().createContainer();
		containerInterface.setAbstractEntity(categoryEntity);
		containerInterface.setMainTableCss("formRequiredLabel");
		containerInterface.setRequiredFieldIndicatior("*");
		containerInterface.setRequiredFieldWarningMessage("indicates mandatory fields.");
		categoryEntity.addContainer(containerInterface);

		return containerInterface;
	}

	/**
	 *
	 * @param categoryAttribute
	 * @param sequenceNumber
	 * @return
	 */
	private TextFieldInterface createTextFieldControl(CategoryAttributeInterface categoryAttribute,
			int sequenceNumber)
	{
		TextFieldInterface textFieldInterface = DomainObjectFactory.getInstance().createTextField();
		textFieldInterface.setBaseAbstractAttribute(categoryAttribute);
		textFieldInterface.setColumns(50);
		textFieldInterface.setCaption(categoryAttribute.getName());
		textFieldInterface.setSequenceNumber(sequenceNumber++);

		return textFieldInterface;
	}

	/**
	 *
	 * @param categoryAttribute
	 * @param sequenceNumber
	 * @return
	 */
	private DatePickerInterface createDateControl(CategoryAttributeInterface categoryAttribute,
			int sequenceNumber)
	{
		DatePickerInterface dateInterface = DomainObjectFactory.getInstance().createDatePicker();
		dateInterface.setBaseAbstractAttribute(categoryAttribute);
		dateInterface.setCaption(categoryAttribute.getName());
		dateInterface.setSequenceNumber(sequenceNumber++);

		return dateInterface;
	}

	/**
	 *
	 * @param categoryAttribute
	 * @param sequenceNumber
	 * @return
	 */
	private FileUploadInterface createFileControl(CategoryAttributeInterface categoryAttribute,
			int sequenceNumber)
	{
		FileUploadInterface fileInterface = DomainObjectFactory.getInstance()
				.createFileUploadControl();
		fileInterface.setBaseAbstractAttribute(categoryAttribute);
		fileInterface.setCaption(categoryAttribute.getName());
		fileInterface.setSequenceNumber(sequenceNumber++);

		return fileInterface;
	}

}