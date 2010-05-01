/**
 * This is the test class for creating the categories from various models.
 */

package edu.common.dynamicextensions.categoryManager;

import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;

/**
 *
 * @author mandar_shidhore
 * @author kunal_kamble
 *
 */
public class TestCategoryDemo extends DynamicExtensionsBaseTestCase
{

	/**
	 *
	 */
	public TestCategoryDemo()
	{
		super();
	}
	//
	//	/**
	//	 * @param arg0 name
	//	 */
	//	public TestCategoryDemo(String arg0)
	//	{
	//		super(arg0);
	//	}
	//
	//	/**
	//	 * @see edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase#setUp()
	//	 */
	//	protected void setUp()
	//	{
	//		super.setUp();
	//	}
	//
	//	/**
	//	 * @see edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase#tearDown()
	//	 */
	//	protected void tearDown()
	//	{
	//		super.tearDown();
	//	}
	//
	//	/**
	//	 * Retrieve entity group by its name from database.
	//	 * @param name name of category
	//	 * @return entity group
	//	 */
	//	private EntityGroupInterface retrieveEntityGroup(String name)
	//	{
	//		DefaultBizLogic bizlogic = new DefaultBizLogic();
	//		Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
	//		EntityGroupInterface entityGroup = null;
	//
	//		try
	//		{
	//			// Fetch the entity group from the database.
	//			entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName", name);
	//
	//			if (entityGroupCollection != null && entityGroupCollection.size() > 0)
	//			{
	//				entityGroup = (EntityGroupInterface) entityGroupCollection.iterator().next();
	//			}
	//		}
	//		catch (DAOException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//
	//		return entityGroup;
	//	}
	//
	//	/**
	//	 * Add bmi attribute to vitals entity and add permissible values to this attribute.
	//	 *
	//	 */
	//	public void testAddPermissibleValuesToVitalsEntityModel()
	//	{
	//		try
	//		{
	//			DomainObjectFactory factory = DomainObjectFactory.getInstance();
	//
	//			EntityGroupInterface entityGroup = retrieveEntityGroup("gcrc");
	//
	//			// Get the VitalSigns entity from entity group.
	//			EntityInterface vitals = entityGroup.getEntityByName("VitalSigns");
	//
	//			AttributeInterface bmi = factory.createStringAttribute();
	//			bmi.setName("BMI");
	//			((StringAttributeTypeInformation) bmi.getAttributeTypeInformation()).setSize(40);
	//
	//			vitals.addAbstractAttribute(bmi);
	//
	//			// Add permissible values.
	//			UserDefinedDEInterface userDefinedDE = factory.createUserDefinedDE();
	//
	//			PermissibleValueInterface permissibleValue1 = factory.createStringValue();
	//			((StringValue) permissibleValue1).setValue("Underweight 18.5 or below");
	//
	//			PermissibleValueInterface permissibleValue2 = factory.createStringValue();
	//			((StringValue) permissibleValue2).setValue("Healthy Weight 18.5 - 24.9");
	//
	//			PermissibleValueInterface permissibleValue3 = factory.createStringValue();
	//			((StringValue) permissibleValue3).setValue("Overweight 25.0 - 29.9");
	//
	//			PermissibleValueInterface permissibleValue4 = factory.createStringValue();
	//			((StringValue) permissibleValue4).setValue("Obese 30.0 and above");
	//
	//			userDefinedDE.addPermissibleValue(permissibleValue1);
	//			userDefinedDE.addPermissibleValue(permissibleValue2);
	//			userDefinedDE.addPermissibleValue(permissibleValue3);
	//			userDefinedDE.addPermissibleValue(permissibleValue4);
	//
	//			StringAttributeTypeInformation bmiTypeInfo = (StringAttributeTypeInformation) bmi.getAttributeTypeInformation();
	//			bmiTypeInfo.setDataElement(userDefinedDE);
	//			bmiTypeInfo.setDefaultValue(permissibleValue2);
	//
	//			EntityGroupManager.getInstance().persistEntityGroup(entityGroup);
	//		}
	//		catch (DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Add bmi attribute to vitals entity and add permissible values to this attribute.
	//	 *
	//	 */
	//	public void testAddPermissibleValuesToAPIDemoGraphicsModel()
	//	{
	//		try
	//		{
	//			DomainObjectFactory factory = DomainObjectFactory.getInstance();
	//
	//			EntityGroupInterface entityGroup = retrieveEntityGroup("api_demo");
	//
	//			// Get the Patient entity from entity group.
	//			EntityInterface patient = entityGroup.getEntityByName("Patient");
	//
	//			AttributeInterface gender = patient.getAttributeByName("gender");
	//
	//			// Add permissible values.
	//			UserDefinedDEInterface userDefinedDE1 = factory.createUserDefinedDE();
	//
	//			PermissibleValueInterface permissibleValue1 = factory.createStringValue();
	//			((StringValue) permissibleValue1).setValue("Male");
	//
	//			PermissibleValueInterface permissibleValue2 = factory.createStringValue();
	//			((StringValue) permissibleValue2).setValue("Female");
	//
	//			PermissibleValueInterface permissibleValue3 = factory.createStringValue();
	//			((StringValue) permissibleValue3).setValue("Not specified");
	//
	//			userDefinedDE1.addPermissibleValue(permissibleValue1);
	//			userDefinedDE1.addPermissibleValue(permissibleValue2);
	//			userDefinedDE1.addPermissibleValue(permissibleValue3);
	//
	//			StringAttributeTypeInformation genderTypeInfo = (StringAttributeTypeInformation) gender.getAttributeTypeInformation();
	//			genderTypeInfo.setDataElement(userDefinedDE1);
	//			genderTypeInfo.setDefaultValue(permissibleValue1);
	//
	//			AttributeInterface ethnicity = patient.getAttributeByName("ethnicity");
	//
	//			// Add permissible values.
	//			UserDefinedDEInterface userDefinedDE2 = factory.createUserDefinedDE();
	//
	//			PermissibleValueInterface permissibleValue4 = factory.createStringValue();
	//			((StringValue) permissibleValue4).setValue("asian");
	//
	//			PermissibleValueInterface permissibleValue5 = factory.createStringValue();
	//			((StringValue) permissibleValue5).setValue("black");
	//
	//			PermissibleValueInterface permissibleValue6 = factory.createStringValue();
	//			((StringValue) permissibleValue6).setValue("hispanic");
	//
	//			PermissibleValueInterface permissibleValue7 = factory.createStringValue();
	//			((StringValue) permissibleValue7).setValue("native american");
	//
	//			PermissibleValueInterface permissibleValue8 = factory.createStringValue();
	//			((StringValue) permissibleValue8).setValue("mexican");
	//
	//			PermissibleValueInterface permissibleValue9 = factory.createStringValue();
	//			((StringValue) permissibleValue9).setValue("white");
	//
	//			PermissibleValueInterface permissibleValue10 = factory.createStringValue();
	//			((StringValue) permissibleValue10).setValue("other");
	//
	//			PermissibleValueInterface permissibleValue21 = factory.createStringValue();
	//			((StringValue) permissibleValue21).setValue("African-American");
	//
	//			PermissibleValueInterface permissibleValue22 = factory.createStringValue();
	//			((StringValue) permissibleValue22).setValue("Caucasian");
	//
	//			userDefinedDE2.addPermissibleValue(permissibleValue4);
	//			userDefinedDE2.addPermissibleValue(permissibleValue5);
	//			userDefinedDE2.addPermissibleValue(permissibleValue6);
	//			userDefinedDE2.addPermissibleValue(permissibleValue7);
	//			userDefinedDE2.addPermissibleValue(permissibleValue8);
	//			userDefinedDE2.addPermissibleValue(permissibleValue9);
	//			userDefinedDE2.addPermissibleValue(permissibleValue10);
	//			userDefinedDE2.addPermissibleValue(permissibleValue21);
	//			userDefinedDE2.addPermissibleValue(permissibleValue22);
	//
	//			StringAttributeTypeInformation ethnicityTypeInfo = (StringAttributeTypeInformation) ethnicity.getAttributeTypeInformation();
	//			ethnicityTypeInfo.setDataElement(userDefinedDE2);
	//			ethnicityTypeInfo.setDefaultValue(permissibleValue7);
	//
	//			AttributeInterface genotype = patient.getAttributeByName("genotype");
	//
	//			// Add permissible values.
	//			UserDefinedDEInterface userDefinedDE3 = factory.createUserDefinedDE();
	//
	//			PermissibleValueInterface permissibleValue11 = factory.createStringValue();
	//			((StringValue) permissibleValue11).setValue("X");
	//
	//			PermissibleValueInterface permissibleValue12 = factory.createStringValue();
	//			((StringValue) permissibleValue12).setValue("XX");
	//
	//			PermissibleValueInterface permissibleValue13 = factory.createStringValue();
	//			((StringValue) permissibleValue13).setValue("XXY");
	//
	//			PermissibleValueInterface permissibleValue14 = factory.createStringValue();
	//			((StringValue) permissibleValue14).setValue("XY");
	//
	//			PermissibleValueInterface permissibleValue15 = factory.createStringValue();
	//			((StringValue) permissibleValue15).setValue("Not specified");
	//
	//			userDefinedDE3.addPermissibleValue(permissibleValue11);
	//			userDefinedDE3.addPermissibleValue(permissibleValue12);
	//			userDefinedDE3.addPermissibleValue(permissibleValue13);
	//			userDefinedDE3.addPermissibleValue(permissibleValue14);
	//			userDefinedDE3.addPermissibleValue(permissibleValue15);
	//
	//			StringAttributeTypeInformation genotypeInfo = (StringAttributeTypeInformation) genotype.getAttributeTypeInformation();
	//			genotypeInfo.setDataElement(userDefinedDE3);
	//			genotypeInfo.setDefaultValue(permissibleValue12);
	//
	//			EntityGroupManager.getInstance().persistEntityGroup(entityGroup);
	//		}
	//		catch (DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Add bmi attribute to vitals entity and add permissible values to this attribute.
	//	 *
	//	 */
	//	public void testAddPermissibleValuesToBodyCompositionModel()
	//	{
	//		try
	//		{
	//			DomainObjectFactory factory = DomainObjectFactory.getInstance();
	//
	//			EntityGroupInterface entityGroup = retrieveEntityGroup("gcrc");
	//
	//			// Get the BodyComposition entity from entity group.
	//			EntityInterface bodyComposition = entityGroup.getEntityByName("BodyComposition");
	//
	//			AttributeInterface result = bodyComposition.getAttributeByName("result");
	//
	//			// Add permissible values.
	//			UserDefinedDEInterface userDefinedDE1 = factory.createUserDefinedDE();
	//
	//			PermissibleValueInterface permissibleValue1 = factory.createStringValue();
	//			((StringValue) permissibleValue1).setValue("BMI");
	//
	//			PermissibleValueInterface permissibleValue2 = factory.createStringValue();
	//			((StringValue) permissibleValue2).setValue("Body fat(%)");
	//
	//			PermissibleValueInterface permissibleValue3 = factory.createStringValue();
	//			((StringValue) permissibleValue3).setValue("Body fat(kg)");
	//
	//			PermissibleValueInterface permissibleValue4 = factory.createStringValue();
	//			((StringValue) permissibleValue4).setValue("Fat free mass(kg)");
	//
	//			PermissibleValueInterface permissibleValue5 = factory.createStringValue();
	//			((StringValue) permissibleValue5).setValue("Liver fat(%)");
	//
	//			PermissibleValueInterface permissibleValue6 = factory.createStringValue();
	//			((StringValue) permissibleValue6).setValue("Muscle fat(kg)");
	//
	//			PermissibleValueInterface permissibleValue7 = factory.createStringValue();
	//			((StringValue) permissibleValue7).setValue("Subcu Abdom fat(cubic cm)");
	//
	//			PermissibleValueInterface permissibleValue8 = factory.createStringValue();
	//			((StringValue) permissibleValue8).setValue("Intra Abdom fat(cubic cm)");
	//
	//			PermissibleValueInterface permissibleValue9 = factory.createStringValue();
	//			((StringValue) permissibleValue9).setValue("Other");
	//
	//			userDefinedDE1.addPermissibleValue(permissibleValue1);
	//			userDefinedDE1.addPermissibleValue(permissibleValue2);
	//			userDefinedDE1.addPermissibleValue(permissibleValue3);
	//			userDefinedDE1.addPermissibleValue(permissibleValue4);
	//			userDefinedDE1.addPermissibleValue(permissibleValue5);
	//			userDefinedDE1.addPermissibleValue(permissibleValue6);
	//			userDefinedDE1.addPermissibleValue(permissibleValue7);
	//			userDefinedDE1.addPermissibleValue(permissibleValue8);
	//			userDefinedDE1.addPermissibleValue(permissibleValue9);
	//
	//			StringAttributeTypeInformation resultTypeInfo = (StringAttributeTypeInformation) result.getAttributeTypeInformation();
	//			resultTypeInfo.setDataElement(userDefinedDE1);
	//			resultTypeInfo.setDefaultValue(permissibleValue5);
	//
	//			AttributeInterface testName = bodyComposition.getAttributeByName("testName");
	//
	//			// Add permissible values.
	//			UserDefinedDEInterface userDefinedDE2 = factory.createUserDefinedDE();
	//
	//			PermissibleValueInterface permissibleValue10 = factory.createStringValue();
	//			((StringValue) permissibleValue10).setValue("DEXA");
	//
	//			PermissibleValueInterface permissibleValue11 = factory.createStringValue();
	//			((StringValue) permissibleValue11).setValue("MRI");
	//
	//			PermissibleValueInterface permissibleValue12 = factory.createStringValue();
	//			((StringValue) permissibleValue12).setValue("Others");
	//
	//			userDefinedDE2.addPermissibleValue(permissibleValue10);
	//			userDefinedDE2.addPermissibleValue(permissibleValue11);
	//			userDefinedDE2.addPermissibleValue(permissibleValue12);
	//
	//			StringAttributeTypeInformation testNameTypeInfo = (StringAttributeTypeInformation) testName.getAttributeTypeInformation();
	//			testNameTypeInfo.setDataElement(userDefinedDE2);
	//			testNameTypeInfo.setDefaultValue(permissibleValue11);
	//
	//			EntityGroupManager.getInstance().persistEntityGroup(entityGroup);
	//		}
	//		catch (DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Create a category from vitals entity and its attributes.
	//	 * Case 7 a
	//	 */
	//	public void testCreateVitalsCategory()
	//	{
	//		try
	//		{
	//			EntityGroupInterface entityGroup = retrieveEntityGroup("gcrc");
	//
	//			// Get the VitalSigns entity from entity group.
	//			EntityInterface vitals = entityGroup.getEntityByName("VitalSigns");
	//
	//			// Create category.
	//			CategoryHelperInterface categoryHelper = new CategoryHelper();
	//
	//			CategoryInterface category = categoryHelper.getCategory("Vitals Category");
	//
	//			// Create category entity from VitalSigns entity.
	//			ContainerInterface vitalsContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(vitals, "Vitals", category);
	//
	//			// Set the root category entity.
	//			categoryHelper.setRootCategoryEntity(vitalsContainer, category);
	//
	//			// Create category attribute(s) for VitalSigns category entity.
	//			categoryHelper.addOrUpdateControl(vitals, "heartRate", vitalsContainer, ControlEnum.TEXT_FIELD_CONTROL, "Pulse");
	//			categoryHelper.addOrUpdateControl(vitals, "diastolicBloodPressure", vitalsContainer, ControlEnum.TEXT_FIELD_CONTROL, "Diastolic BP");
	//			categoryHelper.addOrUpdateControl(vitals, "systolicBloodPressure", vitalsContainer, ControlEnum.TEXT_FIELD_CONTROL, "Systolic BP");
	//			categoryHelper.addOrUpdateControl(vitals, "weight", vitalsContainer, ControlEnum.TEXT_FIELD_CONTROL, "Weight (kg)");
	//			categoryHelper.addOrUpdateControl(vitals, "height", vitalsContainer, ControlEnum.TEXT_FIELD_CONTROL, "Height (cm)");
	//			categoryHelper.addOrUpdateControl(vitals, "respiratoryRate", vitalsContainer, ControlEnum.TEXT_FIELD_CONTROL, "Respiration");
	//
	//			// Save the category.
	//			categoryHelper.saveCategory(category);
	//		}
	//		catch (DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Create a category from vitals entity and its attributes.
	//	 * Select a subset of permissible values for one of its category attributes.
	//	 * Case 7 b
	//	 */
	//	public void testCreateVitalsCategoryWithAttributeHavingPermissibleValues()
	//	{
	//		try
	//		{
	//			EntityGroupInterface entityGroup = retrieveEntityGroup("gcrc");
	//
	//			// Get the VitalSigns entity from entity group.
	//			EntityInterface vitals = entityGroup.getEntityByName("VitalSigns");
	//
	//			// Create category.
	//			CategoryHelperInterface categoryHelper = new CategoryHelper();
	//
	//			CategoryInterface category = categoryHelper.getCategory("Vitals Category with permissible values");
	//
	//			// Create category entity from VitalSigns entity.
	//			ContainerInterface vitalsContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(vitals, "Vitals", category);
	//
	//			// Set the root category entity.
	//			categoryHelper.setRootCategoryEntity(vitalsContainer, category);
	//
	//			// Pass a sub set of original permissible values.
	//			List<String> desiredPermissibleValues = new ArrayList<String>();
	//			desiredPermissibleValues.add("Healthy Weight: 18.5 - 24.9");
	//			desiredPermissibleValues.add("Overweight: 25.0 - 29.9");
	//			desiredPermissibleValues.add("Obese: 30.0 and above");
	//
	//			// Create category attribute(s) for VitalSigns category entity.
	//			categoryHelper.addOrUpdateControl(vitals, "heartRate", vitalsContainer, ControlEnum.TEXT_FIELD_CONTROL, "Pulse");
	//			categoryHelper.addOrUpdateControl(vitals, "diastolicBloodPressure", vitalsContainer, ControlEnum.TEXT_FIELD_CONTROL, "Diastolic BP");
	//			categoryHelper.addOrUpdateControl(vitals, "systolicBloodPressure", vitalsContainer, ControlEnum.TEXT_FIELD_CONTROL, "Systolic BP");
	//			categoryHelper.addOrUpdateControl(vitals, "weight", vitalsContainer, ControlEnum.TEXT_FIELD_CONTROL, "Weight (kg)");
	//			categoryHelper.addOrUpdateControl(vitals, "height", vitalsContainer, ControlEnum.TEXT_FIELD_CONTROL, "Height (cm)");
	//			categoryHelper.addOrUpdateControl(vitals, "respiratoryRate", vitalsContainer, ControlEnum.TEXT_FIELD_CONTROL, "Respiration");
	//			categoryHelper.addOrUpdateControl(vitals, "BMI", vitalsContainer, ControlEnum.LIST_BOX_CONTROL, "BMI", desiredPermissibleValues);
	//
	//			// Save the category.
	//			categoryHelper.saveCategory(category);
	//		}
	//		catch (DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Create a category from visit, ClinicalDiagnosis entity and its attributes.
	//	 * Case 2
	//	 */
	//	public void testCreateClinicalDXCategory()
	//	{
	//		try
	//		{
	//			EntityGroupInterface entityGroup = retrieveEntityGroup("gcrc");
	//
	//			// Fetch the visit and ClinicalDiagnosis entity.
	//			EntityInterface visit = entityGroup.getEntityByName("visit");
	//			EntityInterface clinicalDiagnosis = entityGroup.getEntityByName("ClinicalDiagnosis");
	//
	//			// Create category.
	//			CategoryHelper categoryHelper = new CategoryHelper();
	//
	//			CategoryInterface category = categoryHelper.getCategory("ClinicalDX Category");
	//
	//			// Create category entity from visit entity.
	//			ContainerInterface visitContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(visit, "Clinical Diagnosis", category);
	//
	//			// Set the root category entity of the category.
	//			categoryHelper.setRootCategoryEntity(visitContainer, category);
	//
	//			// Create category entity from clinicalDiagnosis entity.
	//			ContainerInterface otherDiagnosisContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(clinicalDiagnosis,
	//					"Other diagnosis", category);
	//
	//			// Create category attribute(s) for clinicalDiagnosis category entity.
	//			categoryHelper.addOrUpdateControl(clinicalDiagnosis, "value", otherDiagnosisContainer, ControlEnum.TEXT_FIELD_CONTROL, "Value");
	//
	//			List<AssociationInterface> list = new ArrayList<AssociationInterface>();
	//			list.add(EntityManager.getInstance().getAssociationByName("visit-clinicalDiagnosis"));
	//			categoryHelper.associateCategoryContainers(category, entityGroup, visitContainer, otherDiagnosisContainer, list, -1);
	//
	//			// Save the category.
	//			categoryHelper.saveCategory(category);
	//		}
	//		catch (DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Create a category from visit, BodyComposition entity and its attributes.
	//	 * Case 1
	//	 */
	//	public void testCreateBodyCompositionCategory()
	//	{
	//		try
	//		{
	//			EntityGroupInterface entityGroup = retrieveEntityGroup("gcrc");
	//
	//			// Fetch the visit and BodyComposition entity.
	//			EntityInterface visit = entityGroup.getEntityByName("visit");
	//			EntityInterface bodyComposition = entityGroup.getEntityByName("BodyComposition");
	//
	//			// Create category.
	//			CategoryHelper categoryHelper = new CategoryHelper();
	//
	//			CategoryInterface category = categoryHelper.getCategory("Body Composition Category");
	//
	//			// Create category entity from visit entity.
	//			ContainerInterface visitContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(visit, "BodyComp", category);
	//
	//			// Set the root category entity of the category.
	//			categoryHelper.setRootCategoryEntity(visitContainer, category);
	//
	//			// Create category entity from bodyComposition entity.
	//			ContainerInterface bodyCompositionContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(bodyComposition,
	//					"Body composition", category);
	//
	//			List<String> permissibleValuesList1 = new ArrayList<String>();
	//			permissibleValuesList1.add("BMI");
	//			permissibleValuesList1.add("Body fat(%)");
	//			permissibleValuesList1.add("Body fat(kg)");
	//			permissibleValuesList1.add("Fat free mass(kg)");
	//			permissibleValuesList1.add("Liver fat(%)");
	//			permissibleValuesList1.add("Muscle fat(kg)");
	//			permissibleValuesList1.add("Subcu Abdom fat(cubic cm)");
	//			permissibleValuesList1.add("Intra Abdom fat(cubic cm)");
	//
	//			List<String> permissibleValuesList2 = new ArrayList<String>();
	//			permissibleValuesList2.add("DEXA");
	//			permissibleValuesList2.add("MRI");
	//
	//			// Create category attribute(s) for bodyComposition category entity.
	//			categoryHelper.addOrUpdateControl(bodyComposition, "result", bodyCompositionContainer, ControlEnum.LIST_BOX_CONTROL, "result",
	//					permissibleValuesList1);
	//			categoryHelper.addOrUpdateControl(bodyComposition, "source", bodyCompositionContainer, ControlEnum.TEXT_FIELD_CONTROL, "source");
	//			categoryHelper.addOrUpdateControl(bodyComposition, "testName", bodyCompositionContainer, ControlEnum.LIST_BOX_CONTROL, "testName",
	//					permissibleValuesList2);
	//
	//			List<AssociationInterface> list = new ArrayList<AssociationInterface>();
	//			list.add(EntityManager.getInstance().getAssociationByName("visit-bodyComposition"));
	//			categoryHelper.associateCategoryContainers(category, entityGroup, visitContainer, bodyCompositionContainer, list, -1);
	//
	//			// Save the category.
	//			categoryHelper.saveCategory(category);
	//		}
	//		catch (DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Create a category from patient entity and its attributes.
	//	 * Case 9
	//	 */
	//	public void testCreateAPIDemoGraphicsCategory()
	//	{
	//		try
	//		{
	//			EntityGroupInterface entityGroup = retrieveEntityGroup("api_demo");
	//
	//			// Fetch the patient entity.
	//			EntityInterface patient = entityGroup.getEntityByName("Patient");
	//
	//			// Create category.
	//			CategoryHelperInterface categoryHelper = new CategoryHelper();
	//
	//			CategoryInterface category = categoryHelper.getCategory("API demographics");
	//
	//			// Create category entity from patient entity.
	//			ContainerInterface patientContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(patient, "API2 demographics", category);
	//
	//			// Set the root category entity.
	//			categoryHelper.setRootCategoryEntity(patientContainer, category);
	//
	//			// Create category attribute(s) for patient category entity.
	//			categoryHelper.addOrUpdateControl(patient, "firstName", patientContainer, ControlEnum.TEXT_FIELD_CONTROL, "First name");
	//			categoryHelper.addOrUpdateControl(patient, "lastName", patientContainer, ControlEnum.TEXT_FIELD_CONTROL, "Last name");
	//			categoryHelper.addOrUpdateControl(patient, "dateOfBirth", patientContainer, ControlEnum.DATE_PICKER_CONTROL, "Date of birth");
	//
	//			List<String> genderList = new ArrayList<String>();
	//			genderList.add("Female");
	//			genderList.add("Male");
	//
	//			categoryHelper.addOrUpdateControl(patient, "gender", patientContainer, ControlEnum.RADIO_BUTTON_CONTROL, "Gender", genderList);
	//
	//			List<String> ethnicityList = new ArrayList<String>();
	//			ethnicityList.add("asian");
	//			ethnicityList.add("black");
	//			ethnicityList.add("hispanic");
	//			ethnicityList.add("native american");
	//			ethnicityList.add("other");
	//			ethnicityList.add("white");
	//
	//			categoryHelper.addOrUpdateControl(patient, "ethnicity", patientContainer, ControlEnum.LIST_BOX_CONTROL, "Ethnicity", ethnicityList);
	//
	//			List<String> genotypeList = new ArrayList<String>();
	//			genotypeList.add("X");
	//			genotypeList.add("XX");
	//			genotypeList.add("XXY");
	//			genotypeList.add("XY");
	//
	//			categoryHelper.addOrUpdateControl(patient, "genotype", patientContainer, ControlEnum.COMBO_BOX_CONTROL, "Genotype", genotypeList);
	//
	//			// Save the category.
	//			categoryHelper.saveCategory(category);
	//		}
	//		catch (DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Create a category from patient entity and its attributes.
	//	 * Case 10
	//	 */
	//	public void testCreateAPIDemoMedicalHistory()
	//	{
	//		try
	//		{
	//			EntityGroupInterface entityGroup = retrieveEntityGroup("api_demo");
	//
	//			//Fetch the patient entity.
	//			EntityInterface patient = entityGroup.getEntityByName("Patient");
	//
	//			// Create category.
	//			CategoryHelperInterface categoryHelper = new CategoryHelper();
	//
	//			CategoryInterface category = categoryHelper.getCategory("API medical history");
	//
	//			// Create category entity from patient entity.
	//			ContainerInterface patientContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(patient, "API medical history", category);
	//
	//			//Fetch the Medical_Conditions entity.
	//			EntityInterface medicalConditions = entityGroup.getEntityByName("Medical_Conditions");
	//
	//			// Create category entity from Medical_Conditions entity.
	//			ContainerInterface medicalConditionsContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(medicalConditions,
	//					"Medical Conditions", category);
	//
	//			// Set the root category entity.
	//			categoryHelper.setRootCategoryEntity(patientContainer, category);
	//
	//			// Create category attribute(s) for medicalConditions category entity.
	//			categoryHelper.addOrUpdateControl(medicalConditions, "medicalCondition", medicalConditionsContainer, ControlEnum.TEXT_FIELD_CONTROL,
	//					"Medical Condition");
	//			categoryHelper.addOrUpdateControl(medicalConditions, "yearOfOnset", medicalConditionsContainer, ControlEnum.DATE_PICKER_CONTROL,
	//					"Year of onset");
	//
	//			List<AssociationInterface> associationNameList = new ArrayList<AssociationInterface>();
	//			associationNameList.add(EntityManager.getInstance().getAssociationByName("patient-medicalCond"));
	//
	//			categoryHelper.associateCategoryContainers(category, entityGroup, patientContainer, medicalConditionsContainer, associationNameList, -1);
	//
	//			// Save the category.
	//			categoryHelper.saveCategory(category);
	//		}
	//		catch (DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Create a category from patient entity and its attributes.
	//	 * Case 10
	//	 */
	//	public void testCreateAPIDemoMedications()
	//	{
	//		try
	//		{
	//			EntityGroupInterface entityGroup = retrieveEntityGroup("api_demo");
	//
	//			//Fetch the patient entity.
	//			EntityInterface patient = entityGroup.getEntityByName("Patient");
	//
	//			// Create category.
	//			CategoryHelperInterface categoryHelper = new CategoryHelper();
	//
	//			CategoryInterface category = categoryHelper.getCategory("API demo medications");
	//
	//			// Create category entity from patient entity.
	//			ContainerInterface patientContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(patient, "API demo medications", category);
	//
	//			// Fetch the Medications entity.
	//			EntityInterface medications = entityGroup.getEntityByName("Medications");
	//
	//			// Create category entity from Medications entity.
	//			ContainerInterface medicationsContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(medications, "Medical Conditions",
	//					category);
	//
	//			// Set the root category entity.
	//			categoryHelper.setRootCategoryEntity(patientContainer, category);
	//
	//			categoryHelper.addOrUpdateControl(medications, "medication", medicationsContainer, ControlEnum.TEXT_FIELD_CONTROL, "Mediaction");
	//			categoryHelper.addOrUpdateControl(medications, "dose", medicationsContainer, ControlEnum.TEXT_FIELD_CONTROL, "Dose");
	//
	//			List<AssociationInterface> associationNameList = new ArrayList<AssociationInterface>();
	//			associationNameList.add(EntityManager.getInstance().getAssociationByName("patient-medications"));
	//			categoryHelper.associateCategoryContainers(category, entityGroup, patientContainer, medicationsContainer, associationNameList, -1);
	//
	//			// Save the category.
	//			categoryHelper.saveCategory(category);
	//		}
	//		catch (DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
}
