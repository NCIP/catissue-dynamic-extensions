/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author
 *@version 1.0
 */

package edu.common.dynamicextensions.categoryManager;

import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;

public class TestCategory extends DynamicExtensionsBaseTestCase
{

	//	public TestCategory()
	//	{
	//		super();
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
	//			((StringValue) permissibleValue1).setValue("Underweight: 18.5 or below");
	//
	//			PermissibleValueInterface permissibleValue2 = factory.createStringValue();
	//			((StringValue) permissibleValue2).setValue("Healthy Weight: 18.5 - 24.9");
	//
	//			PermissibleValueInterface permissibleValue3 = factory.createStringValue();
	//			((StringValue) permissibleValue3).setValue("Overweight: 25.0 - 29.9");
	//
	//			PermissibleValueInterface permissibleValue4 = factory.createStringValue();
	//			((StringValue) permissibleValue4).setValue("Obese: 30.0 and above");
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
	//			userDefinedDE2.addPermissibleValue(permissibleValue4);
	//			userDefinedDE2.addPermissibleValue(permissibleValue5);
	//			userDefinedDE2.addPermissibleValue(permissibleValue6);
	//			userDefinedDE2.addPermissibleValue(permissibleValue7);
	//			userDefinedDE2.addPermissibleValue(permissibleValue8);
	//			userDefinedDE2.addPermissibleValue(permissibleValue9);
	//			userDefinedDE2.addPermissibleValue(permissibleValue10);
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
	//			((StringValue) permissibleValue10).setValue("DATA");
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
	//	//	private UserDefinedDEInterface createUserDefinedDE(AttributeInterface attribute, List<String> permissibleValues)
	//	//	{
	//	//		DomainObjectFactory factory = DomainObjectFactory.getInstance();
	//	//
	//	//		// Add permissible values.
	//	//		UserDefinedDEInterface userDefinedDE = factory.createUserDefinedDE();
	//	//
	//	//		for (String value: permissibleValues)
	//	//		{
	//	//			PermissibleValueInterface permissibleValue = factory.createStringValue();
	//	//			((StringValue) permissibleValue).setValue(value);
	//	//			userDefinedDE.addPermissibleValue(permissibleValue);
	//	//		}
	//	//
	//	//		StringAttributeTypeInformation attributeTypeInfo = (StringAttributeTypeInformation) attribute.getAttributeTypeInformation();
	//	//		attributeTypeInfo.setDataElement(userDefinedDE);
	//	//
	//	//		return userDefinedDE;
	//	//	}
	//
	//	/**
	//	 * Add bmi attribute to vitals entity and add permissible values to this attribute.
	//	 *
	//	 */
	//	public void testAddPermissibleValuesToNeedleBiopsyModel()
	//	{
	//		try
	//		{
	//			DomainObjectFactory factory = DomainObjectFactory.getInstance();
	//
	//			EntityGroupInterface entityGroup = retrieveEntityGroup("Needle Biopsy EG");
	//
	//			// Get the prostateAnnotation and specimenProcedoure entity from entity group.
	//			EntityInterface prostateAnnotation = entityGroup.getEntityByName("ProstateAnnotation");
	//			AttributeInterface specimenProcedoure = prostateAnnotation.getAttributeByName("specimenProcedoure");
	//
	//			// Add permissible values.
	//			UserDefinedDEInterface userDefinedDE1 = factory.createUserDefinedDE();
	//
	//			PermissibleValueInterface permissibleValue1 = factory.createStringValue();
	//			((StringValue) permissibleValue1).setValue("Prostate Gland Needle Biopsy");
	//
	//			PermissibleValueInterface permissibleValue2 = factory.createStringValue();
	//			((StringValue) permissibleValue2).setValue("Tumor Biopsy");
	//
	//			userDefinedDE1.addPermissibleValue(permissibleValue1);
	//			userDefinedDE1.addPermissibleValue(permissibleValue2);
	//
	//			StringAttributeTypeInformation specimenProcedoureTypeInfo = (StringAttributeTypeInformation) specimenProcedoure
	//					.getAttributeTypeInformation();
	//			specimenProcedoureTypeInfo.setDataElement(userDefinedDE1);
	//			specimenProcedoureTypeInfo.setDefaultValue(permissibleValue1);
	//
	//			// Get the histology entity from entity group.
	//			EntityInterface histology = entityGroup.getEntityByName("Histology");
	//			AttributeInterface type = histology.getAttributeByName("type");
	//
	//			// Add permissible values.
	//			UserDefinedDEInterface userDefinedDE2 = factory.createUserDefinedDE();
	//
	//			PermissibleValueInterface permissibleValue3 = factory.createStringValue();
	//			((StringValue) permissibleValue3).setValue("Type 0");
	//
	//			PermissibleValueInterface permissibleValue4 = factory.createStringValue();
	//			((StringValue) permissibleValue4).setValue("Type 1");
	//
	//			PermissibleValueInterface permissibleValue5 = factory.createStringValue();
	//			((StringValue) permissibleValue5).setValue("Type 2");
	//
	//			userDefinedDE2.addPermissibleValue(permissibleValue3);
	//			userDefinedDE2.addPermissibleValue(permissibleValue4);
	//			userDefinedDE2.addPermissibleValue(permissibleValue5);
	//
	//			StringAttributeTypeInformation typeTypeInfo = (StringAttributeTypeInformation) type.getAttributeTypeInformation();
	//			typeTypeInfo.setDataElement(userDefinedDE2);
	//			typeTypeInfo.setDefaultValue(permissibleValue4);
	//
	//			// Get the additionalFinding entity from entity group.
	//			EntityInterface additionalFinding = entityGroup.getEntityByName("AdditionalFinding");
	//			AttributeInterface detail = additionalFinding.getAttributeByName("Detail");
	//
	//			// Add permissible values.
	//			UserDefinedDEInterface userDefinedDE3 = factory.createUserDefinedDE();
	//
	//			PermissibleValueInterface permissibleValue6 = factory.createStringValue();
	//			((StringValue) permissibleValue6).setValue("A typical adenomatous hyperplasia (adenosis)");
	//
	//			PermissibleValueInterface permissibleValue7 = factory.createStringValue();
	//			((StringValue) permissibleValue7).setValue("Others");
	//
	//			userDefinedDE3.addPermissibleValue(permissibleValue6);
	//			userDefinedDE3.addPermissibleValue(permissibleValue7);
	//
	//			StringAttributeTypeInformation detailTypeInfo = (StringAttributeTypeInformation) detail.getAttributeTypeInformation();
	//			detailTypeInfo.setDataElement(userDefinedDE3);
	//			detailTypeInfo.setDefaultValue(permissibleValue7);
	//
	//			// Get the invasion entity from entity group.
	//			EntityInterface invasion = entityGroup.getEntityByName("Invasion");
	//			AttributeInterface lymphaticInvasion = invasion.getAttributeByName("lymphaticInvasion");
	//
	//			// Add permissible values.
	//			UserDefinedDEInterface userDefinedDE4 = factory.createUserDefinedDE();
	//
	//			PermissibleValueInterface permissibleValue8 = factory.createStringValue();
	//			((StringValue) permissibleValue8).setValue("Absent");
	//
	//			PermissibleValueInterface permissibleValue9 = factory.createStringValue();
	//			((StringValue) permissibleValue9).setValue("Indereminate");
	//
	//			PermissibleValueInterface permissibleValue10 = factory.createStringValue();
	//			((StringValue) permissibleValue10).setValue("Not Specified");
	//
	//			PermissibleValueInterface permissibleValue11 = factory.createStringValue();
	//			((StringValue) permissibleValue11).setValue("Present");
	//
	//			PermissibleValueInterface permissibleValue12 = factory.createStringValue();
	//			((StringValue) permissibleValue12).setValue("Present External");
	//
	//			PermissibleValueInterface permissibleValue13 = factory.createStringValue();
	//			((StringValue) permissibleValue13).setValue("Present Internal");
	//
	//			PermissibleValueInterface permissibleValue14 = factory.createStringValue();
	//			((StringValue) permissibleValue14).setValue("Others");
	//
	//			userDefinedDE4.addPermissibleValue(permissibleValue8);
	//			userDefinedDE4.addPermissibleValue(permissibleValue9);
	//			userDefinedDE4.addPermissibleValue(permissibleValue10);
	//			userDefinedDE4.addPermissibleValue(permissibleValue11);
	//			userDefinedDE4.addPermissibleValue(permissibleValue12);
	//			userDefinedDE4.addPermissibleValue(permissibleValue13);
	//			userDefinedDE4.addPermissibleValue(permissibleValue14);
	//
	//			StringAttributeTypeInformation lymphaticInvasionTypeInfo = (StringAttributeTypeInformation) lymphaticInvasion
	//					.getAttributeTypeInformation();
	//			lymphaticInvasionTypeInfo.setDataElement(userDefinedDE4);
	//			lymphaticInvasionTypeInfo.setDefaultValue(permissibleValue10);
	//
	//			// Get the additionalFinding entity from entity group.
	//			AttributeInterface perneuralInvasion = invasion.getAttributeByName("perneuralInvasion");
	//
	//			// Add permissible values.
	//			UserDefinedDEInterface userDefinedDE5 = factory.createUserDefinedDE();
	//
	//			PermissibleValueInterface permissibleValue15 = factory.createStringValue();
	//			((StringValue) permissibleValue15).setValue("Absent");
	//
	//			PermissibleValueInterface permissibleValue16 = factory.createStringValue();
	//			((StringValue) permissibleValue16).setValue("Indereminate");
	//
	//			PermissibleValueInterface permissibleValue17 = factory.createStringValue();
	//			((StringValue) permissibleValue17).setValue("Not Specified");
	//
	//			PermissibleValueInterface permissibleValue18 = factory.createStringValue();
	//			((StringValue) permissibleValue18).setValue("Present");
	//
	//			PermissibleValueInterface permissibleValue19 = factory.createStringValue();
	//			((StringValue) permissibleValue19).setValue("Others");
	//
	//			userDefinedDE5.addPermissibleValue(permissibleValue15);
	//			userDefinedDE5.addPermissibleValue(permissibleValue16);
	//			userDefinedDE5.addPermissibleValue(permissibleValue17);
	//			userDefinedDE5.addPermissibleValue(permissibleValue18);
	//			userDefinedDE5.addPermissibleValue(permissibleValue19);
	//
	//			StringAttributeTypeInformation perneuralInvasionTypeInfo = (StringAttributeTypeInformation) perneuralInvasion
	//					.getAttributeTypeInformation();
	//			perneuralInvasionTypeInfo.setDataElement(userDefinedDE5);
	//			perneuralInvasionTypeInfo.setDefaultValue(permissibleValue17);
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
	//
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
	//			List<AssociationInterface> associationNames = new ArrayList<AssociationInterface>();
	//			associationNames.add(EntityManager.getInstance().getAssociationByName("visit-clinicalDiagnosis"));
	//			categoryHelper.associateCategoryContainers(category, entityGroup, visitContainer, otherDiagnosisContainer, associationNames, -1);
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
	//			List<String> permissibleValues1 = new ArrayList<String>();
	//			permissibleValues1.add("BMI");
	//			permissibleValues1.add("Body fat(%)");
	//			permissibleValues1.add("Body fat(kg)");
	//			permissibleValues1.add("Fat free mass(kg)");
	//			permissibleValues1.add("Liver fat(%)");
	//			permissibleValues1.add("Muscle fat(kg)");
	//			permissibleValues1.add("Subcu Abdom fat(cubic cm)");
	//			permissibleValues1.add("Intra Abdom fat(cubic cm)");
	//
	//			List<String> permissibleValues2 = new ArrayList<String>();
	//			permissibleValues2.add("DATA");
	//			permissibleValues2.add("MRI");
	//
	//			// Create category attribute(s) for bodyComposition category entity.
	//			categoryHelper.addOrUpdateControl(bodyComposition, "result", bodyCompositionContainer, ControlEnum.LIST_BOX_CONTROL, "result",
	//					permissibleValues1);
	//			categoryHelper.addOrUpdateControl(bodyComposition, "source", bodyCompositionContainer, ControlEnum.TEXT_FIELD_CONTROL, "source");
	//			categoryHelper.addOrUpdateControl(bodyComposition, "testName", bodyCompositionContainer, ControlEnum.LIST_BOX_CONTROL, "testName",
	//					permissibleValues2);
	//
	//			List<AssociationInterface> associationNames = new ArrayList<AssociationInterface>();
	//			associationNames.add(EntityManager.getInstance().getAssociationByName("visit-bodyComposition"));
	//			categoryHelper.associateCategoryContainers(category, entityGroup, visitContainer, bodyCompositionContainer, associationNames, -1);
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
	//			List<String> gender = new ArrayList<String>();
	//			gender.add("Female");
	//			gender.add("Male");
	//
	//			categoryHelper.addOrUpdateControl(patient, "gender", patientContainer, ControlEnum.RADIO_BUTTON_CONTROL, "Gender", gender);
	//
	//			List<String> ethnicity = new ArrayList<String>();
	//			ethnicity.add("asian");
	//			ethnicity.add("black");
	//			ethnicity.add("hispanic");
	//			ethnicity.add("native american");
	//			ethnicity.add("other");
	//			ethnicity.add("white");
	//
	//			categoryHelper.addOrUpdateControl(patient, "ethnicity", patientContainer, ControlEnum.LIST_BOX_CONTROL, "Ethnicity", ethnicity);
	//
	//			List<String> genotype = new ArrayList<String>();
	//			genotype.add("X");
	//			genotype.add("XX");
	//			genotype.add("XXY");
	//			genotype.add("XY");
	//
	//			categoryHelper.addOrUpdateControl(patient, "genotype", patientContainer, ControlEnum.COMBO_BOX_CONTROL, "Genotype", genotype);
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
	//			List<AssociationInterface> associationNames = new ArrayList<AssociationInterface>();
	//			associationNames.add(EntityManager.getInstance().getAssociationByName("patient-medicalCond"));
	//
	//			categoryHelper.associateCategoryContainers(category, entityGroup, patientContainer, medicalConditionsContainer, associationNames, -1);
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
	//			List<AssociationInterface> associationNames = new ArrayList<AssociationInterface>();
	//			associationNames.add(EntityManager.getInstance().getAssociationByName("patient-medications"));
	//
	//			categoryHelper.associateCategoryContainers(category, entityGroup, patientContainer, medicationsContainer, associationNames, -1);
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
	//	 * Create a category from contactInformation, address and phoneNumbers entities and their attributes.
	//	 */
	//	public void testCreateContactInfoCategory()
	//	{
	//		try
	//		{
	//			EntityGroupInterface entityGroup = retrieveEntityGroup("gcrc");
	//
	//			// Get the VitalSigns entity from entity group.
	//			EntityInterface contactInfo = entityGroup.getEntityByName("contactInformation");
	//			EntityInterface address = entityGroup.getEntityByName("address");
	//			EntityInterface phoneNumbers = entityGroup.getEntityByName("phoneNumbers");
	//
	//			// Create category.
	//			CategoryHelperInterface categoryHelper = new CategoryHelper();
	//
	//			CategoryInterface category = categoryHelper.getCategory("Contact details Category");
	//
	//			// Create category entity from contactInfo, address and phoneNumbers entities.
	//			ContainerInterface contactInfoContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(contactInfo, "ContactInfo", category);
	//			ContainerInterface addressContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(address, "Address", category);
	//			ContainerInterface phoneNumbersContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(phoneNumbers, "Phones", category);
	//
	//			// Set the root category entity.
	//			categoryHelper.setRootCategoryEntity(contactInfoContainer, category);
	//
	//			// Create category attribute(s) for address and phoneNumbers category entity.
	//			categoryHelper.addOrUpdateControl(address, "address2", addressContainer, ControlEnum.TEXT_FIELD_CONTROL, "Address2");
	//			categoryHelper.addOrUpdateControl(address, "address1", addressContainer, ControlEnum.TEXT_FIELD_CONTROL, "Address1");
	//			categoryHelper.addOrUpdateControl(address, "city", addressContainer, ControlEnum.TEXT_FIELD_CONTROL, "City");
	//			categoryHelper.addOrUpdateControl(address, "state", addressContainer, ControlEnum.TEXT_FIELD_CONTROL, "State");
	//			categoryHelper.addOrUpdateControl(address, "zipcode", addressContainer, ControlEnum.TEXT_FIELD_CONTROL, "Zip Code");
	//			categoryHelper.addOrUpdateControl(phoneNumbers, "phoneNumber", phoneNumbersContainer, ControlEnum.TEXT_FIELD_CONTROL, "Phone");
	//
	//			List<AssociationInterface> associationNames1 = new ArrayList<AssociationInterface>();
	//			associationNames1.add(EntityManager.getInstance().getAssociationByName("contact-address"));
	//
	//			categoryHelper.associateCategoryContainers(category, entityGroup, contactInfoContainer, addressContainer, associationNames1, 1);
	//
	//			List<AssociationInterface> associationNames2 = new ArrayList<AssociationInterface>();
	//			associationNames2.add(EntityManager.getInstance().getAssociationByName("contact-phone"));
	//
	//			categoryHelper.associateCategoryContainers(category, entityGroup, contactInfoContainer, phoneNumbersContainer, associationNames2, -1);
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
	//	public void testCreateCategoryFromNeedleBiopsyModel()
	//	{
	//		try
	//		{
	//			// Fetch the entity group from the database.
	//			EntityGroupInterface entityGroup = retrieveEntityGroup("Needle Biopsy EG");
	//
	//			// Fetch the entities.
	//			EntityInterface baseAnnotation = entityGroup.getEntityByName("BaseAnnotation");
	//			EntityInterface baseTissuePathoAnno = entityGroup.getEntityByName("BaseTissuePathologyAnnotation");
	//			EntityInterface prostateAnnotation = entityGroup.getEntityByName("ProstateAnnotation");
	//			EntityInterface histology = entityGroup.getEntityByName("Histology");
	//			EntityInterface additionalFinding = entityGroup.getEntityByName("AdditionalFinding");
	//			EntityInterface invasion = entityGroup.getEntityByName("Invasion");
	//			EntityInterface gleasonScore = entityGroup.getEntityByName("GleasonScore");
	//			EntityInterface tumorQuant = entityGroup.getEntityByName("TumorQuantitation");
	//			EntityInterface varHistoType = entityGroup.getEntityByName("VariantHistologicType");
	//
	//			CategoryHelper categoryHelper = new CategoryHelper();
	//
	//			// Create category.
	//			CategoryInterface category = categoryHelper.getCategory("Needle Biopsy Pathology Category");
	//
	//			List<String> desiredValues1 = new ArrayList<String>();
	//			desiredValues1.add("Prostate Gland Needle Biopsy");
	//
	//			// Create category entity from baseAnnotation entity.
	//			ContainerInterface baseAnnotationContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(baseAnnotation, "Base Annotation",
	//					category);
	//
	//			// Create category entity from baseTissuePathoAnno entity.
	//			ContainerInterface baseTissuePathoAnnoContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(baseTissuePathoAnno,
	//					"Base Tissue Patholgy Annotation", category);
	//
	//			// Create category entity from prostateAnnotation entity.
	//			ContainerInterface prostateAnnotationContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(prostateAnnotation,
	//					"Prostate Annotation", category);
	//			categoryHelper.setRootCategoryEntity(prostateAnnotationContainer, category);
	//
	//			// Create category attribute(s) for needleBioProPathAnno category entity.
	//			categoryHelper.addOrUpdateControl(prostateAnnotation, "specimenProcedoure", prostateAnnotationContainer, ControlEnum.LIST_BOX_CONTROL,
	//					"specimenProcedoure", desiredValues1);
	//			categoryHelper.addOrUpdateControl(prostateAnnotation, "procedureDate", prostateAnnotationContainer, ControlEnum.DATE_PICKER_CONTROL,
	//					"procedureDate");
	//			categoryHelper.addOrUpdateControl(prostateAnnotation, "procedureDetailsDocument", prostateAnnotationContainer,
	//					ControlEnum.TEXT_FIELD_CONTROL, "procedureDetailsDocument");
	//			categoryHelper.addOrUpdateControl(prostateAnnotation, "comments", prostateAnnotationContainer, ControlEnum.TEXT_AREA_CONTROL, "comments");
	//
	//			// Create category entity from histology entity.
	//			ContainerInterface histologyContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(histology, "Histology", category);
	//
	//			List<String> desiredValues2 = new ArrayList<String>();
	//			desiredValues2.add("Type 0");
	//			desiredValues2.add("Type 1");
	//
	//			// Create category attribute(s) for histology category entity.
	//			categoryHelper.addOrUpdateControl(histology, "type", histologyContainer, ControlEnum.LIST_BOX_CONTROL, "type", desiredValues2);
	//
	//			// Create category entity from varHistoType entity.
	//			ContainerInterface variantHistologicContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(varHistoType,
	//					"Variant Histology Type", category);
	//
	//			//  Create category attribute(s) for varHistoType category entity.
	//			categoryHelper.addOrUpdateControl(varHistoType, "variantType", variantHistologicContainer, ControlEnum.TEXT_FIELD_CONTROL, "variantType");
	//
	//			// Create category entity from additionalFinding entity.
	//			ContainerInterface additionalFindingContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(additionalFinding,
	//					"Additional Finding", category);
	//
	//			List<String> desiredValues3 = new ArrayList<String>();
	//			desiredValues3.add("A typical adenomatous hyperplasia (adenosis)");
	//
	//			// Create category attribute(s) for additionalFinding category entity.
	//			categoryHelper.addOrUpdateControl(additionalFinding, "Detail", additionalFindingContainer, ControlEnum.LIST_BOX_CONTROL, "Detail",
	//					desiredValues3);
	//
	//			// Create category entity from invasion entity.
	//			ContainerInterface invasionContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(invasion, "Invasion", category);
	//
	//			// Create category attribute(s) for invasion category entity.
	//			List<String> desiredValues4 = new ArrayList<String>();
	//			desiredValues4.add("Absent");
	//			desiredValues4.add("Indereminate");
	//			desiredValues4.add("Not Specified");
	//			desiredValues4.add("Present");
	//			desiredValues4.add("Present External");
	//			desiredValues4.add("Present Internal");
	//
	//			categoryHelper.addOrUpdateControl(invasion, "lymphaticInvasion", invasionContainer, ControlEnum.RADIO_BUTTON_CONTROL,
	//					"lymphaticInvasion", desiredValues4);
	//
	//			List<String> desiredValues5 = new ArrayList<String>();
	//			desiredValues5.add("Absent");
	//			desiredValues5.add("Indereminate");
	//			desiredValues5.add("Not Specified");
	//			desiredValues5.add("Present");
	//
	//			categoryHelper.addOrUpdateControl(invasion, "perneuralInvasion", invasionContainer, ControlEnum.RADIO_BUTTON_CONTROL,
	//					"perneuralInvasion", desiredValues5);
	//
	//			// Create category entity from gleasonScore entity.
	//			ContainerInterface gleasonContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(gleasonScore, "Gleason Score", category);
	//
	//			// Create category attribute(s) for gleasonScore category entity.
	//			categoryHelper.addOrUpdateControl(gleasonScore, "primaryPattern", gleasonContainer, ControlEnum.TEXT_FIELD_CONTROL, "primaryPattern");
	//			categoryHelper.addOrUpdateControl(gleasonScore, "secondaryPattern", gleasonContainer, ControlEnum.TEXT_FIELD_CONTROL, "secondaryPattern");
	//			categoryHelper.addOrUpdateControl(gleasonScore, "tertiaryPattern", gleasonContainer, ControlEnum.TEXT_FIELD_CONTROL, "tertiaryPattern");
	//
	//			// Create category entity from tumorQuant entity.
	//			ContainerInterface tumorQuantContainer = categoryHelper.createOrUpdateCategoryEntityAndContainer(tumorQuant, "Tumor Quantitation",
	//					category);
	//
	//			// Create category attribute(s) for tumorQuant category entity.
	//			categoryHelper.addOrUpdateControl(tumorQuant, "totalLengthOfCarcinomaInMilimeters", tumorQuantContainer, ControlEnum.TEXT_FIELD_CONTROL,
	//					"totalLengthOfCarcinomaInMilimeters");
	//			categoryHelper.addOrUpdateControl(tumorQuant, "totalLengthOfCoresInMilimeters", tumorQuantContainer, ControlEnum.TEXT_FIELD_CONTROL,
	//					"totalLengthOfCoresInMilimeters");
	//			categoryHelper.addOrUpdateControl(tumorQuant, "totalNumberOfPositiveCores", tumorQuantContainer, ControlEnum.TEXT_FIELD_CONTROL,
	//					"totalNumberOfPositiveCores");
	//			categoryHelper.addOrUpdateControl(tumorQuant, "totalNumberOfCores", tumorQuantContainer, ControlEnum.TEXT_FIELD_CONTROL,
	//					"totalNumberOfCores");
	//
	//			// Set appropriate child category entities.
	//			categoryHelper.setParentContainer(baseAnnotationContainer, baseTissuePathoAnnoContainer);
	//			categoryHelper.setParentContainer(baseTissuePathoAnnoContainer, prostateAnnotationContainer);
	//
	//			List<AssociationInterface> list = new ArrayList<AssociationInterface>();
	//			list.add(EntityManager.getInstance().getAssociationByName("base-histology"));
	//			CategoryAssociationControlInterface associationControlInterface = categoryHelper.associateCategoryContainers(category, entityGroup,
	//					baseAnnotationContainer, histologyContainer, list, -1);
	//			associationControlInterface.setSequenceNumber(categoryHelper.getNextSequenceNumber(prostateAnnotationContainer));
	//
	//			list = new ArrayList<AssociationInterface>();
	//			list.add(EntityManager.getInstance().getAssociationByName("base-add"));
	//			CategoryAssociationControlInterface associationControlInterface2 = categoryHelper.associateCategoryContainers(category, entityGroup,
	//					baseAnnotationContainer, additionalFindingContainer, list, -1);
	//			associationControlInterface2.setSequenceNumber(categoryHelper.getNextSequenceNumber(prostateAnnotationContainer));
	//
	//			list = new ArrayList<AssociationInterface>();
	//			list.add(EntityManager.getInstance().getAssociationByName("btissue-invasion"));
	//			CategoryAssociationControlInterface associationControlInterface3 = categoryHelper.associateCategoryContainers(category, entityGroup,
	//					baseTissuePathoAnnoContainer, invasionContainer, list, 1);
	//			associationControlInterface3.setSequenceNumber(categoryHelper.getNextSequenceNumber(prostateAnnotationContainer));
	//
	//			list = new ArrayList<AssociationInterface>();
	//			list.add(EntityManager.getInstance().getAssociationByName("pro-gle"));
	//			categoryHelper.associateCategoryContainers(category, entityGroup, prostateAnnotationContainer, gleasonContainer, list, 1);
	//
	//			list = new ArrayList<AssociationInterface>();
	//			list.add(EntityManager.getInstance().getAssociationByName("pro-tum"));
	//			categoryHelper.associateCategoryContainers(category, entityGroup, prostateAnnotationContainer, tumorQuantContainer, list, 1);
	//
	//			list = new ArrayList<AssociationInterface>();
	//			list.add(EntityManager.getInstance().getAssociationByName("hist-varHist"));
	//			categoryHelper.associateCategoryContainers(category, entityGroup, histologyContainer, variantHistologicContainer, list, -1);
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
	//	 *
	//	 */
	//	public void importXMI(String XMIFileName, String mainContainerList)
	//	{
	//		try
	//		{
	//			String[] args = {XMIFileName, mainContainerList};
	//			XMIImporter.main(args);
	//			System.out.println("--------------- Test Case to import XMI successful ------------");
	//		}
	//		catch (Exception exception)
	//		{
	//			exception.printStackTrace();
	//			fail();
	//		}
	//
	//	}
	//
	//	/**
	//	 * @throws DynamicExtensionsApplicationException
	//	 * @throws DynamicExtensionsSystemException
	//	 *
	//	 */
	//	public void importPermissibleValues(String PVFile)
	//	{
	//		try
	//		{
	//			String[] args = {PVFile};
	//			ImportPermissibleValues.main(args);
	//		}
	//		catch (Exception exception)
	//		{
	//			exception.printStackTrace();
	//			fail();
	//		}
	//
	//	}
	//
	//	/**
	//	 * @throws DynamicExtensionsApplicationException
	//	 * @throws DynamicExtensionsSystemException
	//	 * @throws FileNotFoundException
	//	 *
	//	 */
	//	public void createCategory(String categoryFile)
	//	{
	//		try
	//		{
	//			CategoryGenerator categoryGenerator = new CategoryGenerator(categoryFile);
	//			List<CategoryInterface> list = categoryGenerator.getCategoryList();
	//
	//			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
	//			for (CategoryInterface categoryInterface : list)
	//			{
	//				categoryManager.persistCategory(categoryInterface);
	//			}
	//		}
	//		catch (Exception exception)
	//		{
	//			exception.printStackTrace();
	//			fail();
	//		}
	//
	//	}
	//
	//	public void testCreateCategoryReferringInfo()
	//	{
	//		importXMI("E:\\Projects\\ClinPortal\\models\\from jahangeer 27-3-08\\Referring Info\\referringinfo.xmi",
	//				"E:\\Projects\\ClinPortal\\models\\from jahangeer 27-3-08\\Referring Info\\referringinfo.csv");
	//
	//		importPermissibleValues("E:/Projects/ClinPortal/models/from jahangeer 27-3-08/Referring Info/referringinfo_pv.csv");
	//		createCategory("E:/Projects/ClinPortal/models/from jahangeer 27-3-08/Referring Info/category_referringInfo_ver2_UI_single_entity.csv");
	//
	//	}
	//
}
