
package edu.common.dynamicextensions.categoryManager;

import edu.common.dynamicextensions.domain.Category;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;

/**
 *
 * @author mandar_shidhore
 *
 */
public class TestCategoryManager extends DynamicExtensionsBaseTestCase
{

	//	/**
	//	 *
	//	 */
	//	public TestCategoryManager()
	//	{
	//		super();
	//	}
	//
	//	/**
	//	 * @param arg0 name
	//	 */
	//	public TestCategoryManager(final String arg0)
	//	{
	//		super(arg0);
	//	}
	//
	//	/**
	//	 * @see edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCaseUtility#setUp()
	//	 */
	//	@Override
	//	protected void setUp()
	//	{
	//		super.setUp();
	//	}
	//
	//	/**
	//	 * @see edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCaseUtility#tearDown()
	//	 */
	//	@Override
	//	protected void tearDown()
	//	{
	//		super.tearDown();
	//	}
	//
	//	/**
	//	 *
	//	 *
	//	 */
	//	public void testSaveEntityGroup1()
	//	{
	//		final EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();
	//
	//		try
	//		{
	//			final EntityGroupInterface entityGroup = createEntityGroup1();
	//			// Save the entity group.
	//			entityGroupManager.persistEntityGroup(entityGroup);
	//		}
	//		catch (final DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (final DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 *
	//	 *
	//	 */
	//	public void testSaveEntityGroup2()
	//	{
	//		final EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();
	//
	//		try
	//		{
	//			final EntityGroupInterface entityGroup = createEntityGroup2();
	//			// Save the entity group.
	//			entityGroupManager.persistEntityGroup(entityGroup);
	//		}
	//		catch (final DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (final DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 *
	//	 *
	//	 */
	//	public void testSaveEntityGroup3()
	//	{
	//		final EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();
	//
	//		try
	//		{
	//			final EntityGroupInterface entityGroup = createEntityGroup3();
	//			// Save the entity group.
	//			entityGroupManager.persistEntityGroup(entityGroup);
	//		}
	//		catch (final DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (final DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Retrieve entity group by its name from database.
	//	 * @param name name of category
	//	 * @return entity group
	//	 */
	//	private EntityGroupInterface retrieveEntityGroup(final String name)
	//	{
	//		final DefaultBizLogic bizlogic = new DefaultBizLogic();
	//		Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
	//		EntityGroupInterface entityGroup = null;
	//
	//		try
	//		{
	//			// Fetch the entity group from the database.
	//			entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName",
	//					name);
	//
	//			if ((entityGroupCollection != null) && (entityGroupCollection.size() > 0))
	//			{
	//				entityGroup = entityGroupCollection.iterator().next();
	//			}
	//		}
	//		catch (final BizLogicException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//
	//		return entityGroup;
	//	}
	//
	//	public void testImportInvalidPVForCategory()
	//	{
	//		ApplicationProperties.initBundle(CategoryCSVConstants.DYEXTN_ERROR_MESSAGES_FILE);
	//
	//		try
	//		{
	//			final String[] args1 = {XMI_FILE_PATH+"newsurgery.xmi",CSV_FILE_PATH+"surgery.csv","Surgery","  "};
	//			XMIImporter.main(args1);
	//
	//			final String[] args2 = {CSV_FILE_PATH+"cat_RULES.csv"};
	//			final CategoryCreator categoryCreator = new CategoryCreator();
	//			categoryCreator.main(args2);
	//
	//			fail();
	//		}
	//		catch (final Exception e)
	//		{
	//			assertTrue(e.getMessage().indexOf(
	//					ApplicationProperties.getValue(CategoryConstants.NO_PV_FOR_ATTR)) != -1);
	//		}
	//	}
	//
	//	/**
	//	 * Create a category from vitals entity and its attributes.
	//	 *
	//	 */
	//	/*public void testCreateVitalsCategory()
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
	//	}*/
	//
	//	/**
	//	 * Entities : user (1)------>(*) study
	//	 *
	//	 * Category: Make 2 category entities, choosing attributes from user and study.
	//	 * Persist the category.
	//	 */
	//	/*public void testCreateAndSaveCategoryWithAttributesFromTwoEntities()
	//	 {
	//	 try
	//	 {
	//	 // Fetch the entity group from the database.
	//	 DefaultBizLogic bizlogic = new DefaultBizLogic();
	//	 Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
	//	 EntityGroupInterface entityGroup = null;
	//
	//	 entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName", "User-Study EG1");
	//
	//	 if (entityGroupCollection != null && entityGroupCollection.size() > 0)
	//	 {
	//	 entityGroup = (EntityGroupInterface) entityGroupCollection.iterator().next();
	//	 }
	//
	//	 // Fetch the user and study entities
	//	 EntityInterface user = entityGroup.getEntityByName("User Entity");
	//	 EntityInterface study = entityGroup.getEntityByName("Study Entity");
	//
	//	 ResultSetMetaData metaData = executeQueryForMetadata("select * from " + study.getTableProperties().getName());
	//	 assertEquals(metaData.getColumnCount(), noOfDefaultColumns + 4);
	//
	//	 CategoryHelper categoryHelper = new CategoryHelper();
	//
	//	 CategoryInterface category = categoryHelper.createCategory("Category From User and Study Entities");
	//
	//	 // Create category entity from user entity.
	//	 ContainerInterface userCategoryContainer = categoryHelper.createCategoryEntityAndContainer(user, "User");
	//	 categoryHelper.setRootCategoryEntity(userCategoryContainer, category);
	//
	//	 // Create category attribute(s) for user category entity.
	//	 //categoryHelper.addControl(user, "User Name", userCategoryContainer, ControlEnum.TEXT_FIELD_CONTROL);
	//
	//	 // Permissible values.
	//	 List<String> pvList = new ArrayList<String>();
	//	 pvList.add("Permissible Value 1");
	//	 pvList.add("Permissible Value 2");
	//	 pvList.add("Permissible Value 3");
	//
	//	 // Create category attribute(s) for user category entity.
	//	 categoryHelper.addControl(user, "User Name", userCategoryContainer, ControlEnum.LIST_BOX_CONTROL, "User Name", pvList);
	//
	//	 // Create category entity from study entity.
	//	 ContainerInterface studyCategoryContainer = categoryHelper.createCategoryEntityAndContainer(study, "Study");
	//	 studyCategoryContainer.setAddCaption(false);
	//
	//	 // Create category attribute(s) for user category entity.
	//	 categoryHelper.addControl(study, "Study Name", studyCategoryContainer, ControlEnum.TEXT_FIELD_CONTROL, "Study Name");
	//
	//	 List<String> list = new ArrayList<String>();
	//	 list.add("primaryInvestigator");
	//
	//	 categoryHelper.associateCategoryContainers(userCategoryContainer, studyCategoryContainer, list, -1);
	//
	//	 // Save the category.
	//	 categoryHelper.saveCategory(category);
	//	 }
	//	 catch (DynamicExtensionsSystemException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 catch (DynamicExtensionsApplicationException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 catch (SQLException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 catch (DAOException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 }
	//
	//	 public void testCreateCategoryFromModel()
	//	 {
	//	 try
	//	 {
	//	 // Fetch the entity group from the database.
	//	 DefaultBizLogic bizlogic = new DefaultBizLogic();
	//	 Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
	//	 EntityGroupInterface entityGroup = null;
	//
	//	 entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName", "Needle Biopsy EG");
	//
	//	 if (entityGroupCollection != null && entityGroupCollection.size() > 0)
	//	 {
	//	 entityGroup = (EntityGroupInterface) entityGroupCollection.iterator().next();
	//	 }
	//
	//	 // Fetch the user and study entities
	//	 EntityInterface baseAnnotation = entityGroup.getEntityByName("BaseAnnotation");
	//	 EntityInterface baseTissuePathoAnno = entityGroup.getEntityByName("BaseTissuePathologyAnnotation");
	//	 EntityInterface prostateAnnotation = entityGroup.getEntityByName("ProstateAnnotation");
	//	 EntityInterface histology = entityGroup.getEntityByName("Histology");
	//	 EntityInterface additionalFinding = entityGroup.getEntityByName("AdditionalFinding");
	//	 EntityInterface invasion = entityGroup.getEntityByName("Invasion");
	//	 EntityInterface gleasonScore = entityGroup.getEntityByName("GleasonScore");
	//	 EntityInterface tumorQuant = entityGroup.getEntityByName("TumorQuantitation");
	//	 EntityInterface varHistoType = entityGroup.getEntityByName("VariantHistologicType");
	//
	//	 // Create category.
	//	 CategoryHelper categoryHelper = new CategoryHelper();
	//
	//	 CategoryInterface category = categoryHelper.createCategory("Needle Biopsy Pathology Annotation");
	//
	//	 List<String> pvList = new ArrayList<String>();
	//	 pvList.add("Permissible Value 1");
	//	 pvList.add("Permissible Value 2");
	//	 pvList.add("Permissible Value 3");
	//
	//	 // Create category entity from user entity.
	//	 ContainerInterface baseAnnotationContainer = categoryHelper.createCategoryEntityAndContainer(baseAnnotation, "Base Annotation");
	//
	//	 // Create category entity from user entity.
	//	 ContainerInterface baseTissuePathoAnnoContainer = categoryHelper.createCategoryEntityAndContainer(baseTissuePathoAnno, "Base Tissue Patholgy Annotation");
	//
	//	 // Create category entity from user entity.
	//	 ContainerInterface prostateAnnotationContainer = categoryHelper.createCategoryEntityAndContainer(prostateAnnotation, "Prostate Annotation");
	//	 categoryHelper.setRootCategoryEntity(prostateAnnotationContainer, category);
	//
	//	 // Create category attribute(s) for needleBioProPathAnno category entity.
	//	 categoryHelper.addControl(prostateAnnotation, "specimenProcedoure", prostateAnnotationContainer, ControlEnum.LIST_BOX_CONTROL, "specimenProcedoure", pvList);
	//	 categoryHelper.addControl(prostateAnnotation, "procedureDate", prostateAnnotationContainer, ControlEnum.DATE_PICKER_CONTROL, "procedureDate");
	//	 categoryHelper.addControl(prostateAnnotation, "procedureDetailsDocument", prostateAnnotationContainer, ControlEnum.TEXT_FIELD_CONTROL, "procedureDetailsDocument");
	//	 categoryHelper.addControl(prostateAnnotation, "comments", prostateAnnotationContainer, ControlEnum.TEXT_AREA_CONTROL, "comments");
	//
	//	 // Create category entity from  entity.
	//	 ContainerInterface histologyContainer = categoryHelper.createCategoryEntityAndContainer(histology, "Histology");
	//
	//	 // Create category attribute(s) for histology category entity.
	//	 categoryHelper.addControl(histology, "type", histologyContainer, ControlEnum.LIST_BOX_CONTROL, "type", pvList);
	//
	//	 // Create category entity from  entity.
	//	 ContainerInterface variantHistologicContainer = categoryHelper.createCategoryEntityAndContainer(varHistoType, "Variant Histology Type");
	//
	//	 //  Create category attribute(s) for varHistoType category entity.
	//	 categoryHelper.addControl(varHistoType, "variantType", variantHistologicContainer, ControlEnum.TEXT_FIELD_CONTROL, "variantType");
	//
	//	 // Create category entity from  entity.
	//	 ContainerInterface additionalFindingContainer = categoryHelper.createCategoryEntityAndContainer(additionalFinding, "Additional Finding");
	//
	//	 // Create category attribute(s) for histology category entity.
	//	 categoryHelper.addControl(additionalFinding, "Detail", additionalFindingContainer, ControlEnum.LIST_BOX_CONTROL, "Detail", pvList);
	//
	//	 // Create category entity from  entity.
	//	 ContainerInterface invasionContainer = categoryHelper.createCategoryEntityAndContainer(invasion, "Invasion");
	//	 // Create category attribute(s) for histology category entity.
	//
	//	 List<String> lymInvValues = new ArrayList<String>();
	//	 lymInvValues.add("Absent");
	//	 lymInvValues.add("Indereminate");
	//	 lymInvValues.add("Not Specified");
	//	 lymInvValues.add("Present");
	//	 lymInvValues.add("Present External");
	//	 lymInvValues.add("Present Internal");
	//
	//	 categoryHelper.addControl(invasion, "lymphaticInvasion", invasionContainer, ControlEnum.RADIO_BUTTON_CONTROL, "lymphaticInvasion", lymInvValues);
	//
	//	 List<String> perInvValues = new ArrayList<String>();
	//	 perInvValues.add("Absent");
	//	 perInvValues.add("Indereminate");
	//	 perInvValues.add("Not Specified");
	//	 perInvValues.add("Present");
	//	 categoryHelper.addControl(invasion, "perneuralInvasion", invasionContainer, ControlEnum.RADIO_BUTTON_CONTROL, "perneuralInvasion", perInvValues);
	//
	//	 // Create category entity from  entity.
	//	 ContainerInterface gleasonContainer = categoryHelper.createCategoryEntityAndContainer(gleasonScore, "Gleason Score");
	//
	//	 // Create category attribute(s) for histology category entity.
	//	 categoryHelper.addControl(gleasonScore, "primaryPattern", gleasonContainer, ControlEnum.TEXT_FIELD_CONTROL, "primaryPattern");
	//	 categoryHelper.addControl(gleasonScore, "secondaryPattern", gleasonContainer, ControlEnum.TEXT_FIELD_CONTROL, "secondaryPattern");
	//	 categoryHelper.addControl(gleasonScore, "tertiaryPattern", gleasonContainer, ControlEnum.TEXT_FIELD_CONTROL, "tertiaryPattern");
	//
	//	 // Create category entity from  entity.
	//	 ContainerInterface tumorQuantContainer = categoryHelper.createCategoryEntityAndContainer(tumorQuant, "Tumor Quantitation");
	//
	//	 // Create category attribute(s) for histology category entity.
	//	 categoryHelper.addControl(tumorQuant, "totalLengthOfCarcinomaInMilimeters", tumorQuantContainer, ControlEnum.TEXT_FIELD_CONTROL, "totalLengthOfCarcinomaInMilimeters");
	//	 categoryHelper.addControl(tumorQuant, "totalLengthOfCoresInMilimeters", tumorQuantContainer, ControlEnum.TEXT_FIELD_CONTROL, "totalLengthOfCoresInMilimeters");
	//	 categoryHelper.addControl(tumorQuant, "totalNumberOfPositiveCores", tumorQuantContainer, ControlEnum.TEXT_FIELD_CONTROL, "totalNumberOfPositiveCores");
	//	 categoryHelper.addControl(tumorQuant, "totalNumberOfCores", tumorQuantContainer, ControlEnum.TEXT_FIELD_CONTROL, "totalNumberOfCores");
	//
	//	 // Set appropriate child category entities.
	//	 categoryHelper.setParentContainer(baseAnnotationContainer, baseTissuePathoAnnoContainer);
	//	 categoryHelper.setParentContainer(baseTissuePathoAnnoContainer, prostateAnnotationContainer);
	//
	//	 List<String> list = new ArrayList<String>();
	//	 list.add("histology");
	//	 CategoryAssociationControlInterface associationControlInterface = categoryHelper.associateCategoryContainers(baseAnnotationContainer,
	//	 histologyContainer, list, -1);
	//	 associationControlInterface.setSequenceNumber(categoryHelper.getNextSequenceNumber(prostateAnnotationContainer));
	//
	//	 list = new ArrayList<String>();
	//	 list.add("additionalFinding");
	//	 CategoryAssociationControlInterface associationControlInterface2 = categoryHelper.associateCategoryContainers(baseAnnotationContainer,
	//	 additionalFindingContainer, list, -1);
	//	 associationControlInterface2.setSequenceNumber(categoryHelper.getNextSequenceNumber(prostateAnnotationContainer));
	//
	//	 list = new ArrayList<String>();
	//	 list.add("invasion");
	//	 CategoryAssociationControlInterface associationControlInterface3 = categoryHelper.associateCategoryContainers(
	//	 baseTissuePathoAnnoContainer, invasionContainer, list, 1);
	//	 associationControlInterface3.setSequenceNumber(categoryHelper.getNextSequenceNumber(prostateAnnotationContainer));
	//
	//	 list = new ArrayList<String>();
	//	 list.add("gleasonScore");
	//	 categoryHelper.associateCategoryContainers(prostateAnnotationContainer, gleasonContainer, list, 1);
	//
	//	 list = new ArrayList<String>();
	//	 list.add("tumorQuantitation");
	//	 categoryHelper.associateCategoryContainers(prostateAnnotationContainer, tumorQuantContainer, list, 1);
	//
	//	 list = new ArrayList<String>();
	//	 list.add("variantHistology");
	//	 categoryHelper.associateCategoryContainers(histologyContainer, variantHistologicContainer, list, 1);
	//
	//	 // Save the category.
	//	 categoryHelper.saveCategory(category);
	//	 }
	//	 catch (DynamicExtensionsSystemException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 catch (DynamicExtensionsApplicationException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 catch (DAOException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 }
	//
	//	 public void testCreateBodyCompositionCategory()
	//	 {
	//	 try
	//	 {
	//	 // Fetch the entity group from the database.
	//	 DefaultBizLogic bizlogic = new DefaultBizLogic();
	//	 Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
	//	 EntityGroupInterface entityGroup = null;
	//
	//	 entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName", "EG");
	//
	//	 if (entityGroupCollection != null && entityGroupCollection.size() > 0)
	//	 {
	//	 entityGroup = (EntityGroupInterface) entityGroupCollection.iterator().next();
	//	 }
	//
	//	 // Fetch the vitals entity.
	//	 EntityInterface visit = entityGroup.getEntityByName("visit");
	//	 EntityInterface bodyComposition = entityGroup.getEntityByName("BodyComposition");
	//
	//	 // Create category.
	//	 CategoryHelper categoryHelper = new CategoryHelper();
	//
	//	 CategoryInterface category = categoryHelper.createCategory("Body Composition Category");
	//
	//	 // Create category entity from vitals entity.
	//	 ContainerInterface visitContainer = categoryHelper.createCategoryEntityAndContainer(visit, "Visit");
	//
	//	 // Create category entity from bodyComposition entity.
	//	 ContainerInterface bodyCompositionContainer = categoryHelper.createCategoryEntityAndContainer(bodyComposition, "Body Composition");
	//
	//	 List<String> permissibleValuesList1 = new ArrayList<String>();
	//	 permissibleValuesList1.add("BMI");
	//	 permissibleValuesList1.add("Body fat(%)");
	//	 permissibleValuesList1.add("Body fat(kg)");
	//	 permissibleValuesList1.add("Fat free mass(kg)");
	//	 permissibleValuesList1.add("Liver fat(%)");
	//	 permissibleValuesList1.add("Muscle fat(kg)");
	//	 permissibleValuesList1.add("Subcu Abdom fat(cubic cm)");
	//	 permissibleValuesList1.add("Intra Abdom fat(cubic cm)");
	//
	//	 List<String> permissibleValuesList2 = new ArrayList<String>();
	//	 permissibleValuesList2.add("DEXA");
	//	 permissibleValuesList2.add("MRI");
	//
	//	 // Create category attribute(s) for bodyComposition category entity.
	//	 categoryHelper.addControl(bodyComposition, "result", bodyCompositionContainer, ControlEnum.LIST_BOX_CONTROL, "result", permissibleValuesList1);
	//	 categoryHelper.addControl(bodyComposition, "source", bodyCompositionContainer, ControlEnum.TEXT_FIELD_CONTROL, "source");
	//	 categoryHelper.addControl(bodyComposition, "testName", bodyCompositionContainer, ControlEnum.LIST_BOX_CONTROL, "testName", permissibleValuesList2);
	//
	//	 // Set appropriate child category entities.
	//	 categoryHelper.setParentContainer(visitContainer, bodyCompositionContainer);
	//
	//	 List<String> list = new ArrayList<String>();
	//	 list.add("bodyComposition");
	//	 categoryHelper.associateCategoryContainers(visitContainer, bodyCompositionContainer, list, -1);
	//
	//	 // Save the category.
	//	 categoryHelper.saveCategory(category);
	//	 }
	//	 catch (DynamicExtensionsSystemException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 catch (DynamicExtensionsApplicationException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 catch (DAOException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 }
	//
	//	 public void testCreateLipidCMPCategory()
	//	 {
	//	 try
	//	 {
	//	 // Fetch the entity group from the database.
	//	 DefaultBizLogic bizlogic = new DefaultBizLogic();
	//	 Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
	//	 EntityGroupInterface entityGroup = null;
	//
	//	 entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName", "EG");
	//
	//	 if (entityGroupCollection != null && entityGroupCollection.size() > 0)
	//	 {
	//	 entityGroup = (EntityGroupInterface) entityGroupCollection.iterator().next();
	//	 }
	//
	//	 // Fetch the vitals entity.
	//	 EntityInterface visit = entityGroup.getEntityByName("visit");
	//	 EntityInterface ClinicalAnnotationLabAnnotation = entityGroup.getEntityByName("ClinicalAnnotationLabAnnotation");
	//
	//	 // Create category.
	//	 CategoryHelper categoryHelper = new CategoryHelper();
	//
	//	 CategoryInterface category = categoryHelper.createCategory("LipidCMP Category");
	//
	//	 // Create category entity from ClinicalAnnotationLabAnnotation entity.
	//	 ContainerInterface visitContainer = categoryHelper.createCategoryEntityAndContainer(visit, "Visit");
	//	 ContainerInterface clinAnnoLabAnnoContainer1 = categoryHelper.createCategoryEntityAndContainer(ClinicalAnnotationLabAnnotation, "Clinical Annotation Lab Annotation");
	//	 ContainerInterface clinAnnoLabAnnoContainer2 = categoryHelper.createCategoryEntityAndContainer(ClinicalAnnotationLabAnnotation, "Clinical Annotation Lab Annotation");
	//
	//	 List<String> permissibleValuesList1 = new ArrayList<String>();
	//	 permissibleValuesList1.add("Cholesterol");
	//	 permissibleValuesList1.add("HDL cholesterol");
	//	 permissibleValuesList1.add("LDL cholesterol");
	//	 permissibleValuesList1.add("Triglycerides");
	//
	//	 List<String> permissibleValuesList2 = new ArrayList<String>();
	//	 permissibleValuesList2.add("Glu");
	//	 permissibleValuesList2.add("Na");
	//	 permissibleValuesList2.add("K");
	//	 permissibleValuesList2.add("Cl");
	//	 permissibleValuesList2.add("Crt");
	//	 permissibleValuesList2.add("BUN");
	//	 permissibleValuesList2.add("Tot Prot");
	//	 permissibleValuesList2.add("Tot Bili");
	//	 permissibleValuesList2.add("Alk Phos");
	//	 permissibleValuesList2.add("AST");
	//	 permissibleValuesList2.add("ALT");
	//	 permissibleValuesList2.add("Ca");
	//
	//	 // Create category attribute(s) for bodyComposition category entity.
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "labTestName", clinAnnoLabAnnoContainer1, ControlEnum.LIST_BOX_CONTROL, "", permissibleValuesList1);
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "quantitativeResult", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "resultUnits", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "permissibleLowerLimit", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "permissibleUpperLimit", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	//
	//	 // Create category attribute(s) for bodyComposition category entity.
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "labTestName", clinAnnoLabAnnoContainer2, ControlEnum.LIST_BOX_CONTROL, "", permissibleValuesList2);
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "quantitativeResult", clinAnnoLabAnnoContainer2, ControlEnum.TEXT_FIELD_CONTROL, "");
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "resultUnits", clinAnnoLabAnnoContainer2, ControlEnum.TEXT_FIELD_CONTROL, "");
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "permissibleLowerLimit", clinAnnoLabAnnoContainer2, ControlEnum.TEXT_FIELD_CONTROL, "");
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "permissibleUpperLimit", clinAnnoLabAnnoContainer2, ControlEnum.TEXT_FIELD_CONTROL, "");
	//
	//	 // Set appropriate child category entities.
	//	 categoryHelper.setParentContainer(visitContainer, clinAnnoLabAnnoContainer1);
	//	 categoryHelper.setParentContainer(visitContainer, clinAnnoLabAnnoContainer2);
	//
	//	 List<String> list = new ArrayList<String>();
	//	 list.add("lipidPanel");
	//	 categoryHelper.associateCategoryContainers(visitContainer, clinAnnoLabAnnoContainer1, list, -1);
	//
	//	 list = new ArrayList<String>();
	//	 list.add("CMP");
	//	 categoryHelper.associateCategoryContainers(visitContainer, clinAnnoLabAnnoContainer2, list, -1);
	//
	//	 // Save the category.
	//	 categoryHelper.saveCategory(category);
	//	 }
	//	 catch (DynamicExtensionsSystemException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 catch (DynamicExtensionsApplicationException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 catch (DAOException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 }
	//
	//	 public void testCreateCMPCBCCategory()
	//	 {
	//	 try
	//	 {
	//	 // Fetch the entity group from the database.
	//	 DefaultBizLogic bizlogic = new DefaultBizLogic();
	//	 Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
	//	 EntityGroupInterface entityGroup = null;
	//
	//	 entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName", "EG");
	//
	//	 if (entityGroupCollection != null && entityGroupCollection.size() > 0)
	//	 {
	//	 entityGroup = (EntityGroupInterface) entityGroupCollection.iterator().next();
	//	 }
	//
	//	 // Fetch the vitals entity.
	//	 EntityInterface visit = entityGroup.getEntityByName("visit");
	//	 EntityInterface ClinicalAnnotationLabAnnotation = entityGroup.getEntityByName("ClinicalAnnotationLabAnnotation");
	//
	//	 // Create category.
	//	 CategoryHelper categoryHelper = new CategoryHelper();
	//
	//	 CategoryInterface category = categoryHelper.createCategory("CMPCBC Category");
	//
	//	 // Create category entity from ClinicalAnnotationLabAnnotation entity.
	//	 ContainerInterface visitContainer = categoryHelper.createCategoryEntityAndContainer(visit, "Visit");
	//	 ContainerInterface clinAnnoLabAnnoContainer1 = categoryHelper.createCategoryEntityAndContainer(ClinicalAnnotationLabAnnotation, "Clinical Annotation Lab Annotation");
	//	 ContainerInterface clinAnnoLabAnnoContainer2 = categoryHelper.createCategoryEntityAndContainer(ClinicalAnnotationLabAnnotation, "Clinical Annotation Lab Annotation");
	//
	//	 List<String> permissibleValuesList1 = new ArrayList<String>();
	//	 permissibleValuesList1.add("Glu");
	//	 permissibleValuesList1.add("Na");
	//	 permissibleValuesList1.add("K");
	//	 permissibleValuesList1.add("Cl");
	//	 permissibleValuesList1.add("Crt");
	//	 permissibleValuesList1.add("BUN");
	//	 permissibleValuesList1.add("Tot Prot");
	//	 permissibleValuesList1.add("Tot Bili");
	//	 permissibleValuesList1.add("Alk Phos");
	//	 permissibleValuesList1.add("AST");
	//	 permissibleValuesList1.add("ALT");
	//	 permissibleValuesList1.add("Ca");
	//
	//	 List<String> permissibleValuesList2 = new ArrayList<String>();
	//	 permissibleValuesList2.add("Hct");
	//	 permissibleValuesList2.add("Hgb");
	//	 permissibleValuesList2.add("MCHC");
	//	 permissibleValuesList2.add("MCV");
	//	 permissibleValuesList2.add("RBC");
	//	 permissibleValuesList2.add("WBC");
	//
	//	 // Create category attribute(s) for bodyComposition category entity.
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "labTestName", clinAnnoLabAnnoContainer1, ControlEnum.LIST_BOX_CONTROL, "", permissibleValuesList1);
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "quantitativeResult", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "resultUnits", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "permissibleLowerLimit", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "permissibleUpperLimit", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	//
	//	 // Create category attribute(s) for bodyComposition category entity.
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "labTestName", clinAnnoLabAnnoContainer2, ControlEnum.LIST_BOX_CONTROL, "", permissibleValuesList2);
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "quantitativeResult", clinAnnoLabAnnoContainer2, ControlEnum.TEXT_FIELD_CONTROL, "");
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "resultUnits", clinAnnoLabAnnoContainer2, ControlEnum.TEXT_FIELD_CONTROL, "");
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "permissibleLowerLimit", clinAnnoLabAnnoContainer2, ControlEnum.TEXT_FIELD_CONTROL, "");
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "permissibleUpperLimit", clinAnnoLabAnnoContainer2, ControlEnum.TEXT_FIELD_CONTROL, "");
	//
	//	 // Set appropriate child category entities.
	//	 categoryHelper.setParentContainer(visitContainer, clinAnnoLabAnnoContainer1);
	//	 categoryHelper.setParentContainer(visitContainer, clinAnnoLabAnnoContainer2);
	//
	//	 List<String> list = new ArrayList<String>();
	//	 list.add("CMP");
	//	 categoryHelper.associateCategoryContainers(visitContainer, clinAnnoLabAnnoContainer1, list, -1);
	//
	//	 list = new ArrayList<String>();
	//	 list.add("CBC");
	//	 categoryHelper.associateCategoryContainers(visitContainer, clinAnnoLabAnnoContainer2, list, -1);
	//
	//	 // Save the category.
	//	 categoryHelper.saveCategory(category);
	//	 }
	//	 catch (DynamicExtensionsSystemException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 catch (DynamicExtensionsApplicationException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 catch (DAOException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 }
	//
	//	 public void testCreateMetabTestCategory()
	//	 {
	//	 try
	//	 {
	//	 // Fetch the entity group from the database.
	//	 DefaultBizLogic bizlogic = new DefaultBizLogic();
	//	 Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
	//	 EntityGroupInterface entityGroup = null;
	//
	//	 entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName", "EG");
	//
	//	 if (entityGroupCollection != null && entityGroupCollection.size() > 0)
	//	 {
	//	 entityGroup = (EntityGroupInterface) entityGroupCollection.iterator().next();
	//	 }
	//
	//	 // Fetch the vitals entity.
	//	 EntityInterface visit = entityGroup.getEntityByName("visit");
	//	 EntityInterface ClinicalAnnotationLabAnnotation = entityGroup.getEntityByName("ClinicalAnnotationLabAnnotation");
	//
	//	 // Create category.
	//	 CategoryHelper categoryHelper = new CategoryHelper();
	//
	//	 CategoryInterface category = categoryHelper.createCategory("MetabTest Category");
	//
	//	 // Create category entity from ClinicalAnnotationLabAnnotation entity.
	//	 ContainerInterface visitContainer = categoryHelper.createCategoryEntityAndContainer(visit, "Visit");
	//	 ContainerInterface clinAnnoLabAnnoContainer1 = categoryHelper.createCategoryEntityAndContainer(ClinicalAnnotationLabAnnotation, "Clinical Annotation Lab Annotation");
	//	 ContainerInterface clinAnnoLabAnnoContainer2 = categoryHelper.createCategoryEntityAndContainer(ClinicalAnnotationLabAnnotation, "Clinical Annotation Lab Annotation");
	//
	//	 List<String> permissibleValuesList1 = new ArrayList<String>();
	//	 permissibleValuesList1.add("Glucose conc-fasting");
	//	 permissibleValuesList1.add("Glucose conc-2 hr fasting");
	//	 permissibleValuesList1.add("FFA conc-fasting");
	//	 permissibleValuesList1.add("Insulin conc-fasting");
	//	 permissibleValuesList1.add("HOMA-IR");
	//
	//
	//	 List<String> permissibleValuesList2 = new ArrayList<String>();
	//	 permissibleValuesList2.add("hour 0 - Baseline");
	//	 permissibleValuesList2.add("hour 1");
	//	 permissibleValuesList2.add("hour 2");
	//	 permissibleValuesList2.add("hour 3");
	//	 permissibleValuesList2.add("hour 4");
	//
	//	 // Create category attribute(s) for bodyComposition category entity.
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "labTestName", clinAnnoLabAnnoContainer1, ControlEnum.LIST_BOX_CONTROL, "", permissibleValuesList1);
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "quantitativeResult", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "resultUnits", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "permissibleLowerLimit", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "permissibleUpperLimit", clinAnnoLabAnnoContainer1, ControlEnum.TEXT_FIELD_CONTROL, "");
	//
	//	 // Create category attribute(s) for bodyComposition category entity.
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "labTestName", clinAnnoLabAnnoContainer2, ControlEnum.LIST_BOX_CONTROL, "", permissibleValuesList2);
	//	 categoryHelper.addControl(ClinicalAnnotationLabAnnotation, "quantitativeResult", clinAnnoLabAnnoContainer2, ControlEnum.TEXT_FIELD_CONTROL, "");
	//
	//	 // Set appropriate child category entities.
	//	 categoryHelper.setParentContainer(visitContainer, clinAnnoLabAnnoContainer1);
	//	 categoryHelper.setParentContainer(visitContainer, clinAnnoLabAnnoContainer2);
	//
	//	 List<String> list = new ArrayList<String>();
	//	 list.add("CMP");
	//	 categoryHelper.associateCategoryContainers(visitContainer, clinAnnoLabAnnoContainer1, list, -1);
	//
	//	 list = new ArrayList<String>();
	//	 list.add("CBC");
	//	 categoryHelper.associateCategoryContainers(visitContainer, clinAnnoLabAnnoContainer2, list, -1);
	//
	//	 // Save the category.
	//	 categoryHelper.saveCategory(category);
	//	 }
	//	 catch (DynamicExtensionsSystemException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 catch (DynamicExtensionsApplicationException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 catch (DAOException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 }
	//
	//	 public void testCreateClinicalDXCategory()
	//	 {
	//	 try
	//	 {
	//	 // Fetch the entity group from the database.
	//	 DefaultBizLogic bizlogic = new DefaultBizLogic();
	//	 Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
	//	 EntityGroupInterface entityGroup = null;
	//
	//	 entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName", "EG");
	//
	//	 if (entityGroupCollection != null && entityGroupCollection.size() > 0)
	//	 {
	//	 entityGroup = (EntityGroupInterface) entityGroupCollection.iterator().next();
	//	 }
	//
	//	 // Fetch the vitals entity.
	//	 EntityInterface visit = entityGroup.getEntityByName("visit");
	//	 EntityInterface clinicalDiagnosis = entityGroup.getEntityByName("ClinicalDiagnosis");
	//
	//	 // Create category.
	//	 CategoryHelper categoryHelper = new CategoryHelper();
	//
	//	 CategoryInterface category = categoryHelper.createCategory("ClinicalDX Category");
	//
	//	 // Create category entity from ClinicalAnnotationLabAnnotation entity.
	//	 ContainerInterface visitContainer = categoryHelper.createCategoryEntityAndContainer(visit, "Visit");
	//	 ContainerInterface frequentDiagnosisContainer = categoryHelper.createCategoryEntityAndContainer(clinicalDiagnosis, "Clinical Diagnosis");
	//	 ContainerInterface otherDiagnosisContainer = categoryHelper.createCategoryEntityAndContainer(clinicalDiagnosis, "Clinical Diagnosis");
	//
	//	 // Create category attribute(s) for bodyComposition category entity.
	//	 categoryHelper.addControl(clinicalDiagnosis, "value", frequentDiagnosisContainer, ControlEnum.CHECK_BOX_CONTROL, "");
	//	 categoryHelper.addControl(clinicalDiagnosis, "value", frequentDiagnosisContainer, ControlEnum.CHECK_BOX_CONTROL, "");
	//	 categoryHelper.addControl(clinicalDiagnosis, "value", frequentDiagnosisContainer, ControlEnum.CHECK_BOX_CONTROL, "");
	//	 categoryHelper.addControl(clinicalDiagnosis, "value", frequentDiagnosisContainer, ControlEnum.CHECK_BOX_CONTROL, "");
	//	 categoryHelper.addControl(clinicalDiagnosis, "value", frequentDiagnosisContainer, ControlEnum.CHECK_BOX_CONTROL, "");
	//
	//	 // Create category attribute(s) for bodyComposition category entity.
	//	 categoryHelper.addControl(clinicalDiagnosis, "value", otherDiagnosisContainer, ControlEnum.TEXT_FIELD_CONTROL, "");
	//
	//	 // Set appropriate child category entities.
	//	 categoryHelper.setParentContainer(visitContainer, frequentDiagnosisContainer);
	//	 categoryHelper.setParentContainer(visitContainer, otherDiagnosisContainer);
	//
	//	 List<String> list = new ArrayList<String>();
	//	 list.add("otherDiagnosis");
	//	 categoryHelper.associateCategoryContainers(visitContainer, otherDiagnosisContainer, list, -1);
	//
	//	 // Save the category.
	//	 categoryHelper.saveCategory(category);
	//	 }
	//	 catch (DynamicExtensionsSystemException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 catch (DynamicExtensionsApplicationException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 catch (DAOException e)
	//	 {
	//	 e.printStackTrace();
	//	 fail();
	//	 }
	//	 }*/
	//
	//	/**
	//	 * Entities : user (1)------>(*) study (1) ------>(*) experiment
	//	 *
	//	 * Category: Make 2 category entities, choosing attributes from user and experiment, do not
	//	 * choose attributes from study.
	//	 * Persist the category.
	//	 */
	//	public void testCreateAndSaveCategoryWithAttributesFromTwoOutOfThreeEntities()
	//	{
	//		final DomainObjectFactory factory = DomainObjectFactory.getInstance();
	//		try
	//		{
	//			// Fetch the entity group from the database.
	//			final DefaultBizLogic bizlogic = new DefaultBizLogic();
	//			Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
	//			EntityGroupInterface entityGroup = null;
	//
	//			entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName",
	//			"User-Study-Experiment EG1");
	//
	//			if ((entityGroupCollection != null) && (entityGroupCollection.size() > 0))
	//			{
	//				entityGroup = entityGroupCollection.iterator().next();
	//			}
	//
	//			// Fetch the user, study and experiment entities.
	//			final EntityInterface user = entityGroup.getEntityByName("User Entity");
	//			final EntityInterface study = entityGroup.getEntityByName("Study Entity");
	//			final EntityInterface experiment = entityGroup.getEntityByName("Experiment Entity");
	//
	//			assertEquals(getColumnCount("select * from " + study.getTableProperties().getName()),
	//					noOfDefaultColumns + 4);
	//
	//			// Create a category from user and experiment entities.
	//			final CategoryInterface category = factory.createCategory();
	//			category.setName("Category From Study and Experiment Entities");
	//
	//			// Create category entity from user entity.
	//			final CategoryEntityInterface userCategoryEntity = factory.createCategoryEntity();
	//			userCategoryEntity.setName("User Category Entity");
	//			userCategoryEntity.setNumberOfEntries(-1);
	//			userCategoryEntity.setEntity(user);
	//			userCategoryEntity.setCategory(category);
	//
	//			// Set the root category element of the category.
	//			category.setRootCategoryElement(userCategoryEntity);
	//
	//			// Create category attribute(s) for user category entity.
	//			final CategoryAttributeInterface userCategoryAttribute = factory.createCategoryAttribute();
	//			userCategoryAttribute.setName("User Category Attribute");
	//			userCategoryAttribute.setAbstractAttribute(user.getAttributeByName("User Name"));
	//
	//			userCategoryEntity.addCategoryAttribute(userCategoryAttribute);
	//			userCategoryAttribute.setCategoryEntity(userCategoryEntity);
	//
	//			// Create category entity from experiment entity.
	//			final CategoryEntityInterface experimentCategoryEntity = factory.createCategoryEntity();
	//			experimentCategoryEntity.setName("Experiment Category Entity");
	//			experimentCategoryEntity.setNumberOfEntries(-1);
	//			experimentCategoryEntity.setEntity(experiment);
	//
	//			// Create category attribute(s) for experiment category entity.
	//			final CategoryAttributeInterface experimentCategoryAttribute = factory
	//			.createCategoryAttribute();
	//			experimentCategoryAttribute.setName("Experiment Category Attribute");
	//			experimentCategoryAttribute.setAbstractAttribute(experiment
	//					.getAttributeByName("Experiment Name"));
	//
	//			experimentCategoryEntity.addCategoryAttribute(experimentCategoryAttribute);
	//			experimentCategoryAttribute.setCategoryEntity(experimentCategoryEntity);
	//
	//			// Fetch the user's associations list.
	//			final List<AssociationInterface> userAssociationsList = new ArrayList<AssociationInterface>(
	//					user.getAssociationCollection());
	//
	//			// Fetch the study's associations list.
	//			final List<AssociationInterface> studyAssociationsList = new ArrayList<AssociationInterface>(
	//					study.getAssociationCollection());
	//
	//			// Create a path between user category entity and experiment category entity.
	//			final PathInterface path = factory.createPath();
	//
	//			final PathAssociationRelationInterface pathAssociationRelation1 = factory
	//			.createPathAssociationRelation();
	//			pathAssociationRelation1.setAssociation(userAssociationsList.get(0));
	//			pathAssociationRelation1.setPathSequenceNumber(1);
	//
	//			final PathAssociationRelationInterface pathAssociationRelation2 = factory
	//			.createPathAssociationRelation();
	//			pathAssociationRelation2.setAssociation(studyAssociationsList.get(0));
	//			pathAssociationRelation2.setPathSequenceNumber(2);
	//
	//			pathAssociationRelation1.setPath(path);
	//			pathAssociationRelation2.setPath(path);
	//
	//			path.addPathAssociationRelation(pathAssociationRelation1);
	//			path.addPathAssociationRelation(pathAssociationRelation2);
	//
	//			// Add path information to the target category entity.
	//			experimentCategoryEntity.setPath(path);
	//
	//			// Create a category association between user category entity and experiment category entity
	//			// that corresponds to association between user and experiment entities.
	//			final CategoryAssociationInterface categoryAssociation = factory.createCategoryAssociation();
	//			categoryAssociation
	//			.setName("User Category Entity To Experiment Category Entity Category Association");
	//			categoryAssociation.setCategoryEntity(userCategoryEntity);
	//			categoryAssociation.setTargetCategoryEntity(experimentCategoryEntity);
	//
	//			userCategoryEntity.getCategoryAssociationCollection().add(categoryAssociation);
	//
	//			// Make experiment category entity a child of user category entity.
	//			userCategoryEntity.addChildCategory(experimentCategoryEntity);
	//
	//			// Create containers for category entities.
	//			int sequenceNumber = 1;
	//
	//			// Create a container for user category entity.
	//			final ContainerInterface userContainer = createContainer(userCategoryEntity);
	//
	//			// Create a control for user category attribute.
	//			final TextFieldInterface userControl = createTextFieldControl(userCategoryAttribute,
	//					sequenceNumber++);
	//			userControl.setParentContainer((Container) userContainer);
	//
	//			userContainer.addControl(userControl);
	//
	//			// Create a container for experiment category entity.
	//			final ContainerInterface experimentContainer = createContainer(experimentCategoryEntity);
	//			experimentContainer.setAddCaption(false);
	//
	//			// Create a control for experiment category attribute.
	//			final TextFieldInterface experimentControl = createTextFieldControl(
	//					experimentCategoryAttribute, sequenceNumber++);
	//			experimentControl.setParentContainer((Container) experimentContainer);
	//
	//			experimentContainer.addControl(experimentControl);
	//
	//			// Create a control for category association.
	//			final CategoryAssociationControlInterface categoryAssociationControl = factory
	//			.createCategoryAssociationControl();
	//			categoryAssociationControl.setCaption("UserToExperimentCategoryAssociationControl");
	//			categoryAssociationControl.setContainer(experimentContainer);
	//			categoryAssociationControl.setBaseAbstractAttribute(categoryAssociation);
	//			categoryAssociationControl.setSequenceNumber(sequenceNumber++);
	//			categoryAssociationControl.setParentContainer((Container) userContainer);
	//
	//			userContainer.addControl(categoryAssociationControl);
	//
	//			// Save the category.
	//			final CategoryManagerInterface categoryManager = CategoryManager.getInstance();
	//			categoryManager.persistCategory(category);
	//		}
	//		catch (final DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (final DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (final BizLogicException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Entities : user (1)------>(*) study (1) ------>(*) experiment
	//	 *
	//	 * Category: Make 3 category entities, choosing attributes from all entities.
	//	 * Insert data for category.
	//	 */
	//	public void testCreateAndSaveCategoryWithAttributesFromThreeOutOfThreeEntities()
	//	{
	//		final DomainObjectFactory factory = DomainObjectFactory.getInstance();
	//		try
	//		{
	//			// Fetch the entity group from the database.
	//			final DefaultBizLogic bizlogic = new DefaultBizLogic();
	//			Collection<EntityGroupInterface> entityGroupCollection = new HashSet<EntityGroupInterface>();
	//			EntityGroupInterface entityGroup = null;
	//
	//			entityGroupCollection = bizlogic.retrieve(EntityGroup.class.getName(), "shortName",
	//			"User-Study-Experiment EG1");
	//
	//			if ((entityGroupCollection != null) && (entityGroupCollection.size() > 0))
	//			{
	//				entityGroup = entityGroupCollection.iterator().next();
	//			}
	//
	//			// Fetch the user, study and experiment entities.
	//			final EntityInterface user = entityGroup.getEntityByName("User Entity");
	//			final EntityInterface study = entityGroup.getEntityByName("Study Entity");
	//			final EntityInterface experiment = entityGroup.getEntityByName("Experiment Entity");
	//			assertEquals(getColumnCount("select * from " + study.getTableProperties().getName()),
	//					noOfDefaultColumns + 4);
	//			// Create a category from user, study and experiment entities.
	//			final CategoryInterface category = factory.createCategory();
	//			category.setName("Category From User, Study and Experiment Entities");
	//
	//			// Create category entity from user entity.
	//			final CategoryEntityInterface userCategoryEntity = factory.createCategoryEntity();
	//			userCategoryEntity.setName("User Category Entity");
	//			userCategoryEntity.setNumberOfEntries(-1);
	//			userCategoryEntity.setEntity(user);
	//			userCategoryEntity.setCategory(category);
	//
	//			// Set the root category element of the category.
	//			category.setRootCategoryElement(userCategoryEntity);
	//
	//			// Create category attribute(s) for user category entity.
	//			final CategoryAttributeInterface userCategoryAttribute = factory.createCategoryAttribute();
	//			userCategoryAttribute.setName("User Category Attribute");
	//			userCategoryAttribute.setAbstractAttribute(user.getAttributeByName("User Name"));
	//
	//			userCategoryEntity.addCategoryAttribute(userCategoryAttribute);
	//			userCategoryAttribute.setCategoryEntity(userCategoryEntity);
	//
	//			// Create category entity from study entity.
	//			final CategoryEntityInterface studyCategoryEntity = factory.createCategoryEntity();
	//			studyCategoryEntity.setName("Study Category Entity");
	//			studyCategoryEntity.setNumberOfEntries(-1);
	//			studyCategoryEntity.setEntity(study);
	//
	//			// Create category attribute(s) for study category entity.
	//			final CategoryAttributeInterface studyCategoryAttribute = factory.createCategoryAttribute();
	//			studyCategoryAttribute.setName("Study Category Attribute");
	//			studyCategoryAttribute.setAbstractAttribute(study.getAttributeByName("Study Name"));
	//
	//			studyCategoryEntity.addCategoryAttribute(studyCategoryAttribute);
	//			studyCategoryAttribute.setCategoryEntity(studyCategoryEntity);
	//
	//			// Create category entity from experiment entity.
	//			final CategoryEntityInterface experimentCategoryEntity = factory.createCategoryEntity();
	//			experimentCategoryEntity.setName("Experiment Category Entity");
	//			experimentCategoryEntity.setNumberOfEntries(-1);
	//			experimentCategoryEntity.setEntity(experiment);
	//
	//			// Create category attribute(s) for experiment category entity.
	//			final CategoryAttributeInterface experimentCategoryAttribute = factory
	//			.createCategoryAttribute();
	//			experimentCategoryAttribute.setName("Experiment Category Attribute");
	//			experimentCategoryAttribute.setAbstractAttribute(experiment
	//					.getAttributeByName("Experiment Name"));
	//
	//			experimentCategoryEntity.addCategoryAttribute(experimentCategoryAttribute);
	//			experimentCategoryAttribute.setCategoryEntity(experimentCategoryEntity);
	//
	//			// Fetch the user's associations list.
	//			final List<AssociationInterface> userAssociationsList = new ArrayList<AssociationInterface>(
	//					user.getAssociationCollection());
	//
	//			// Fetch the study's associations list.
	//			final List<AssociationInterface> studyAssociationsList = new ArrayList<AssociationInterface>(
	//					study.getAssociationCollection());
	//
	//			// Create a path between user category entity and study category entity.
	//			final PathInterface path1 = factory.createPath();
	//
	//			final PathAssociationRelationInterface pathAssociationRelation1 = factory
	//			.createPathAssociationRelation();
	//			pathAssociationRelation1.setAssociation(userAssociationsList.get(0));
	//			pathAssociationRelation1.setPathSequenceNumber(1);
	//			pathAssociationRelation1.setPath(path1);
	//
	//			// Create a path between study category entity and experiment category entity.
	//			final PathInterface path2 = factory.createPath();
	//
	//			final PathAssociationRelationInterface pathAssociationRelation2 = factory
	//			.createPathAssociationRelation();
	//			pathAssociationRelation2.setAssociation(studyAssociationsList.get(0));
	//			pathAssociationRelation2.setPathSequenceNumber(2);
	//			pathAssociationRelation2.setPath(path2);
	//
	//			path1.addPathAssociationRelation(pathAssociationRelation1);
	//			path2.addPathAssociationRelation(pathAssociationRelation2);
	//
	//			// Add path information to the target category entities.
	//			studyCategoryEntity.setPath(path1);
	//			experimentCategoryEntity.setPath(path2);
	//
	//			// Create a category association between user category entity and study category entity
	//			// that corresponds to association between user and study entities.
	//			final CategoryAssociationInterface categoryAssociation1 = factory.createCategoryAssociation();
	//			categoryAssociation1
	//			.setName("User Category Entity To Study Category Entity Category Association");
	//			categoryAssociation1.setCategoryEntity(userCategoryEntity);
	//			categoryAssociation1.setTargetCategoryEntity(studyCategoryEntity);
	//
	//			userCategoryEntity.getCategoryAssociationCollection().add(categoryAssociation1);
	//
	//			// Create a category association between study category entity and experiment category entity
	//			// that corresponds to association between study and experiment entities.
	//			final CategoryAssociationInterface categoryAssociation2 = factory.createCategoryAssociation();
	//			categoryAssociation2
	//			.setName("Study Category Entity To Experiment Category Entity Category Association");
	//			categoryAssociation2.setCategoryEntity(studyCategoryEntity);
	//			categoryAssociation2.setTargetCategoryEntity(experimentCategoryEntity);
	//
	//			studyCategoryEntity.getCategoryAssociationCollection().add(categoryAssociation2);
	//
	//			// Make study category entity a child of user category entity
	//			// and experiment category entity a child of study category entity.
	//			userCategoryEntity.addChildCategory(studyCategoryEntity);
	//			studyCategoryEntity.addChildCategory(experimentCategoryEntity);
	//
	//			// Create containers for category entities.
	//			int sequenceNumber = 1;
	//
	//			// Create a container for user category entity.
	//			final ContainerInterface userContainer = createContainer(userCategoryEntity);
	//
	//			// Create a control for user category attribute.
	//			final TextFieldInterface userControl = createTextFieldControl(userCategoryAttribute,
	//					sequenceNumber++);
	//			userControl.setParentContainer((Container) userContainer);
	//
	//			userContainer.addControl(userControl);
	//
	//			// Create a container for study category entity.
	//			final ContainerInterface studyContainer = createContainer(studyCategoryEntity);
	//			studyContainer.setAddCaption(false);
	//
	//			// Create a control for study category attribute.
	//			final TextFieldInterface studyControl = createTextFieldControl(studyCategoryAttribute,
	//					sequenceNumber++);
	//			studyControl.setParentContainer((Container) studyContainer);
	//
	//			studyContainer.addControl(studyControl);
	//
	//			// Create a container for experiment category entity.
	//			final ContainerInterface experimentContainer = createContainer(experimentCategoryEntity);
	//			experimentContainer.setAddCaption(false);
	//
	//			// Create a control for experiment category attribute.
	//			final TextFieldInterface experimentControl = createTextFieldControl(
	//					experimentCategoryAttribute, sequenceNumber++);
	//			experimentControl.setParentContainer((Container) experimentContainer);
	//
	//			experimentContainer.addControl(experimentControl);
	//
	//			// Create a control for category association between user category entity and study category entity.
	//			final CategoryAssociationControlInterface categoryAssociationControl1 = factory
	//			.createCategoryAssociationControl();
	//			categoryAssociationControl1.setCaption("UserToStudyCategoryAssociationControl");
	//			categoryAssociationControl1.setBaseAbstractAttribute(categoryAssociation1);
	//			categoryAssociationControl1.setSequenceNumber(sequenceNumber++);
	//			categoryAssociationControl1.setContainer(studyContainer);
	//			categoryAssociationControl1.setParentContainer((Container) userContainer);
	//
	//			userContainer.addControl(categoryAssociationControl1);
	//
	//			// Create a control for category association between study category entity and experiment category entity.
	//			final CategoryAssociationControlInterface categoryAssociationControl2 = factory
	//			.createCategoryAssociationControl();
	//			categoryAssociationControl2.setCaption("StudyToExperimentCategoryAssociationControl");
	//			categoryAssociationControl2.setBaseAbstractAttribute(categoryAssociation2);
	//			categoryAssociationControl2.setSequenceNumber(sequenceNumber++);
	//			categoryAssociationControl2.setContainer(experimentContainer);
	//			categoryAssociationControl2.setParentContainer((Container) studyContainer);
	//
	//			studyContainer.addControl(categoryAssociationControl2);
	//
	//			// Save the category.
	//			final CategoryManagerInterface categoryManager = CategoryManager.getInstance();
	//			categoryManager.persistCategory(category);
	//		}
	//		catch (final DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (final DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (final BizLogicException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Create category from SCG pathology annotation model.
	//	 * Refer to SCG pathology annotation model in cvs.
	//	 */
	//	public void testCreateRadicalProstatectomyCategoryFromSCGPathologyAnnotationModel()
	//	{
	//		CategoryInterface category = null;
	//		try
	//		{
	//			final String[] args1 = {XMI_FILE_PATH +"scg.xmi",CSV_FILE_PATH+"SCG.csv","edu.wustl.catissuecore.domain.PathAnnotation_SCG"," "};
	//			XMIImporter.main(args1);
	//
	//			final String[] args2 = {CSV_FILE_PATH+ "Category_PathAnnoModel.csv"};
	//
	//			CategoryCreator.main(args2);
	//
	//			final CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
	//			category = (CategoryInterface) categoryManager.getObjectByName(
	//					Category.class.getName(),
	//			"RadicalProstatectomyPathologyAnnotation_TestCaseCategory");
	//
	//			assertNotNull(category.getId());
	//			assertNotNull(category.getRootCategoryElement());
	//			assertEquals(DynamicExtensionsUtility.getCategoryEntityName(category
	//					.getRootCategoryElement().getName()),
	//			"RadicalProstatectomyPathologyAnnotation[1]");
	//
	//			final CategoryEntityInterface prostatePathologyAnnotationCategoryEntity = category
	//			.getRootCategoryElement().getParentCategoryEntity();
	//			assertNotNull(prostatePathologyAnnotationCategoryEntity);
	//			assertEquals(DynamicExtensionsUtility
	//					.getCategoryEntityName(prostatePathologyAnnotationCategoryEntity.getName()),
	//			"ProstatePathologyAnnotation[1]");
	//
	//			final CategoryEntityInterface baseSolidTissuePathologyAnnotation = prostatePathologyAnnotationCategoryEntity
	//			.getParentCategoryEntity();
	//			assertNotNull(baseSolidTissuePathologyAnnotation);
	//			assertEquals(DynamicExtensionsUtility
	//					.getCategoryEntityName(baseSolidTissuePathologyAnnotation.getName()),
	//			"BaseSolidTissuePathologyAnnotation[1]");
	//
	//			final CategoryEntityInterface basePathologyAnnotation = baseSolidTissuePathologyAnnotation
	//			.getParentCategoryEntity();
	//			assertNotNull(basePathologyAnnotation);
	//			assertEquals(DynamicExtensionsUtility.getCategoryEntityName(basePathologyAnnotation
	//					.getName()), "BasePathologyAnnotation[1]");
	//
	//			assertNull(basePathologyAnnotation.getParentCategoryEntity());
	//		}
	//		catch (final Exception e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Create category where attributes from a particular class are not chosen,
	//	 * i.e. not selecting attributes from GleasonScore entity.
	//	 * Refer to pathology annotation model.
	//	 */
	//	public void testCreateNeedleBiopsyCategoryFromSCGPathologyAnnotationModel()
	//	{
	//		CategoryInterface category = null;
	//		try
	//		{
	//			final String[] args1 = {XMI_FILE_PATH+"scg.xmi",CSV_FILE_PATH+"SCG.csv", "edu.wustl.catissuecore.domain.PathAnnotation_SCG","  "};
	//			XMIImporter.main(args1);
	//
	//			final String[] args2 = {CSV_FILE_PATH + "Category_NeedleBiopsy.csv"};
	//
	//			CategoryCreator.main(args2);
	//
	//			final CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
	//			category = (CategoryInterface) categoryManager.getObjectByName(
	//					Category.class.getName(), "NeedleBiopsy_TestCaseCategory");
	//
	//			assertNotNull(category.getId());
	//			assertNotNull(category.getRootCategoryElement());
	//			assertEquals(category.getName(), "NeedleBiopsy_TestCaseCategory");
	//			assertEquals(DynamicExtensionsUtility.getCategoryEntityName(category
	//					.getRootCategoryElement().getName()),
	//			"NeedleBiopsyProstatePathologyAnnotation[1]");
	//
	//			final CategoryEntityInterface prostatePathologyAnnotationCategoryEntity = category
	//			.getRootCategoryElement().getParentCategoryEntity();
	//			assertNotNull(prostatePathologyAnnotationCategoryEntity);
	//			assertEquals(DynamicExtensionsUtility
	//					.getCategoryEntityName(prostatePathologyAnnotationCategoryEntity.getName()),
	//			"ProstatePathologyAnnotation[1]");
	//
	//			final CategoryEntityInterface baseSolidTissuePathologyAnnotation = prostatePathologyAnnotationCategoryEntity
	//			.getParentCategoryEntity();
	//			assertNotNull(baseSolidTissuePathologyAnnotation);
	//			assertEquals(DynamicExtensionsUtility
	//					.getCategoryEntityName(baseSolidTissuePathologyAnnotation.getName()),
	//			"BaseSolidTissuePathologyAnnotation[1]");
	//
	//			final CategoryEntityInterface basePathologyAnnotation = baseSolidTissuePathologyAnnotation
	//			.getParentCategoryEntity();
	//			assertNotNull(basePathologyAnnotation);
	//			assertEquals(DynamicExtensionsUtility.getCategoryEntityName(basePathologyAnnotation
	//					.getName()), "BasePathologyAnnotation[1]");
	//
	//			assertNull(basePathologyAnnotation.getParentCategoryEntity());
	//		}
	//		catch (final Exception e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Create category where attributes from a particular class are not chosen,
	//	 * i.e. not selecting attributes from GleasonScore entity.
	//	 * Refer to pathology annotation model.
	//	 */
	//	public void testCIDERModel()
	//	{
	//		CategoryInterface category = null;
	//		try
	//		{
	//
	//
	//
	//			final String[] args = {XMI_FILE_PATH+"cider.xmi", CSV_FILE_PATH+"cider.csv","edu.wustl.cider.domain","none"};
	//			CiderXMIImporter.main(args);
	//
	//			final String[] args2 = {CSV_FILE_PATH+"Lab_category.csv", "true"};
	//
	//			CategoryCreator.main(args2);
	//
	//			final CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
	//			category = (CategoryInterface) categoryManager.getObjectByName(
	//					Category.class.getName(), "CIDER Test Category");
	//
	//			assertNotNull(category.getId());
	//			assertNotNull(category.getRootCategoryElement());
	//			assertEquals(category.getName(), "CIDER Test Category");
	//
	//		}
	//		catch (final Exception e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Create category where attributes from a particular class are not chosen,
	//	 * i.e. not selecting attributes from GleasonScore entity.
	//	 * Refer to pathology annotation model.
	//	 */
	//	public void testBMIASCalculatedAttribute()
	//	{
	//		CategoryInterface category = null;
	//		try
	//		{
	//			final String[] args = {XMI_FILE_PATH+"BMI.xmi", CSV_FILE_PATH+"BMIMainContainer.csv","BMI","  "};
	//			XMIImporter.main(args);
	//
	//			final String[] args2 = {CSV_FILE_PATH + "BMI.csv"};
	//			CategoryCreator.main(args2);
	//
	//			final CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
	//			category = (CategoryInterface) categoryManager.getObjectByName(
	//					Category.class.getName(), "BMI Calculation");
	//
	//			assertNotNull(category.getId());
	//			assertNotNull(category.getRootCategoryElement());
	//			assertEquals(category.getName(), "BMI Calculation");
	//
	//		}
	//		catch (final Exception e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Create category where attributes from a particular class are not chosen,
	//	 * i.e. not selecting attributes from GleasonScore entity.
	//	 * Refer to pathology annotation model.
	//	 */
	//	public void testBMIASRACalculatedAttribute()
	//	{
	//		CategoryInterface category = null;
	//		try
	//		{
	//			final String[] args = {XMI_FILE_PATH + "BMI.xmi", CSV_FILE_PATH+"BMIMainContainer.csv", "BMI"," "};
	//			XMIImporter.main(args);
	//
	//			final String[] args2 = {CSV_FILE_PATH+"BMIRelatedAttribute.csv"};
	//
	//			CategoryCreator.main(args2);
	//
	//			final CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
	//			category = (CategoryInterface) categoryManager.getObjectByName(
	//					Category.class.getName(), "BMI RA Calculation");
	//
	//			assertNotNull(category.getId());
	//			assertNotNull(category.getRootCategoryElement());
	//			assertEquals(category.getName(), "BMI RA Calculation");
	//
	//		}
	//		catch (final Exception e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Create category where attributes from a particular class are not chosen,
	//	 * i.e. not selecting attributes from GleasonScore entity.
	//	 * Refer to pathology annotation model.
	//	 */
	//	public void testBMIInDifferentClassInstanceAsCalculatedAttribute()
	//	{
	//		CategoryInterface category = null;
	//		try
	//		{
	//			final String[] args = {XMI_FILE_PATH+"BMI.xmi", CSV_FILE_PATH+"BMIMainContainer.csv","BMI" , "  "};
	//			XMIImporter.main(args);
	//
	//			final String[] args2 = {CSV_FILE_PATH + "BMI_DifferentClass.csv"};
	//
	//			CategoryCreator.main(args2);
	//
	//			final CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
	//			category = (CategoryInterface) categoryManager.getObjectByName(
	//					Category.class.getName(), "BMI Different Class Calculation");
	//
	//			assertNotNull(category.getId());
	//			assertNotNull(category.getRootCategoryElement());
	//			assertEquals(category.getName(), "BMI Different Class Calculation");
	//
	//		}
	//		catch (final Exception e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Create category where attributes from a particular class are not chosen,
	//	 * i.e. not selecting attributes from GleasonScore entity.
	//	 * Refer to pathology annotation model.
	//	 */
	//	public void testCalculatedAttributeForDates()
	//	{
	//		CategoryInterface category = null;
	//		try
	//		{
	//			final String[] args = {XMI_FILE_PATH + "BMIDate.xmi",CSV_FILE_PATH + "BMIMainContainer.csv", "BMI", " "};
	//			XMIImporter.main(args);
	//
	//			final String[] args2 = {CSV_FILE_PATH + "BMI_Date.csv"};
	//
	//			CategoryCreator.main(args2);
	//
	//			final CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
	//			category = (CategoryInterface) categoryManager.getObjectByName(
	//					Category.class.getName(), "Date Calculation");
	//
	//			assertNotNull(category.getId());
	//			assertNotNull(category.getRootCategoryElement());
	//			assertEquals(category.getName(), "Date Calculation");
	//
	//		}
	//		catch (final Exception e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//	/**
	//	 * Create category where attributes from a particular class are not chosen,
	//	 * i.e. not selecting attributes from GleasonScore entity.
	//	 * Refer to pathology annotation model.
	//	 */
	//	public void testSkipLogicAttributes()
	//	{
	//		CategoryInterface category = null;
	//		try
	//		{
	//			final String[] args = {XMI_FILE_PATH + "test.xmi",CSV_FILE_PATH + "Skip_Logic_Main.csv", "annotations", "  "};
	//			XMIImporter.main(args);
	//
	//			final ImportPermissibleValues importPermissibleValues = new ImportPermissibleValues(CSV_FILE_PATH + "TestModel_pv.csv");
	//			importPermissibleValues.importValues();
	//
	//			final String[] args2 = {CSV_FILE_PATH + "SkipLogic.csv"};
	//
	//			CategoryCreator.main(args2);
	//
	//			final CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
	//
	//			category = (CategoryInterface) categoryManager.getObjectByName(
	//					Category.class.getName(), "SkipLogic_Test");
	//
	//			assertNotNull(category.getId());
	//			assertNotNull(category.getRootCategoryElement());
	//			assertEquals(category.getName(), "SkipLogic_Test");
	//
	//		}
	//		catch (final Exception e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//	/**
	//	 * Create entity group from pathology annotation model.
	//	 * Check if a correct subset of values for tumourTissueSiteCategoryAttribute is displayed.
	//	 */
	//	public void testSelectivePermissibleValuesForCategory()
	//	{
	//		final CategoryManagerInterface categoryManager = CategoryManager.getInstance();
	//		final DomainObjectFactory factory = DomainObjectFactory.getInstance();
	//
	//		try
	//		{
	//			// Create and fetch the entity group.
	//			final EntityGroupInterface entityGroup = createEntityGroup4();
	//
	//			// Fetch the entities.
	//			final EntityInterface baseSolidTissuePathologyAnnotation = entityGroup
	//			.getEntityByName("BaseSolidTissuePathologyAnnotation");
	//			final EntityInterface prostatePathologyAnnotation = entityGroup
	//			.getEntityByName("ProstatePathologyAnnotation");
	//			final EntityInterface gleasonScore = entityGroup.getEntityByName("GleasonScore");
	//			final EntityInterface radicalProstatectomyPathologyAnnotation = entityGroup
	//			.getEntityByName("RadicalProstatectomyPathologyAnnotation");
	//			final EntityInterface melanomaMargin = entityGroup.getEntityByName("melanomaMargin");
	//			final EntityInterface radicalProstatectomyMargin = entityGroup
	//			.getEntityByName("RadicalProstatectomyMargin");
	//
	//			// Create a category.
	//			final CategoryInterface category = factory.createCategory();
	//			category.setName("Category");
	//
	//			// Create category entity from baseSolidTissuePathologyAnnotation entity.
	//			final CategoryEntityInterface baseSolidTissuePathologyAnnotationCategoryEntity = factory
	//			.createCategoryEntity();
	//			baseSolidTissuePathologyAnnotationCategoryEntity
	//			.setName("baseSolidTissuePathologyAnnotationCategoryEntity");
	//			baseSolidTissuePathologyAnnotationCategoryEntity
	//			.setEntity(baseSolidTissuePathologyAnnotation);
	//			baseSolidTissuePathologyAnnotationCategoryEntity.setNumberOfEntries(-1);
	//
	//			// Fetch the baseSolidTissuePathologyAnnotation's attributes' list.
	//			final List<AttributeInterface> attributesList1 = new ArrayList<AttributeInterface>(
	//					baseSolidTissuePathologyAnnotation.getAttributeCollection());
	//
	//			// Create category attribute(s) for baseSolidTissuePathologyAnnotation category entity.
	//			final CategoryAttributeInterface tissueSlideCategoryAttribute = factory
	//			.createCategoryAttribute();
	//			tissueSlideCategoryAttribute.setName("tissueSlideCategoryAttribute");
	//			tissueSlideCategoryAttribute.setAbstractAttribute(attributesList1.get(0));
	//			baseSolidTissuePathologyAnnotationCategoryEntity
	//			.addCategoryAttribute(tissueSlideCategoryAttribute);
	//			tissueSlideCategoryAttribute
	//			.setCategoryEntity(baseSolidTissuePathologyAnnotationCategoryEntity);
	//
	//			final CategoryAttributeInterface tumourTissueSiteCategoryAttribute = factory
	//			.createCategoryAttribute();
	//			tumourTissueSiteCategoryAttribute.setName("tumourTissueSiteCategoryAttribute");
	//			tumourTissueSiteCategoryAttribute.setAbstractAttribute(attributesList1.get(1));
	//			baseSolidTissuePathologyAnnotationCategoryEntity
	//			.addCategoryAttribute(tumourTissueSiteCategoryAttribute);
	//			tumourTissueSiteCategoryAttribute
	//			.setCategoryEntity(baseSolidTissuePathologyAnnotationCategoryEntity);
	//
	//			// Create User defined DE for tumourTissueSiteCategoryAttribute.
	//			final UserDefinedDEInterface tumourTissueSiteCategoryAttributeUserDefinedDE = factory
	//			.createUserDefinedDE();
	//
	//			final UserDefinedDEInterface attributeUserDefinedDE = (UserDefinedDE) attributesList1.get(2)
	//			.getAttributeTypeInformation().getDataElement();
	//			final List<PermissibleValueInterface> permissibleValueCollection = new ArrayList<PermissibleValueInterface>(
	//					attributeUserDefinedDE.getPermissibleValueCollection());
	//
	//			tumourTissueSiteCategoryAttributeUserDefinedDE
	//			.addPermissibleValue(permissibleValueCollection.get(0));
	//			tumourTissueSiteCategoryAttributeUserDefinedDE
	//			.addPermissibleValue(permissibleValueCollection.get(1));
	//			tumourTissueSiteCategoryAttributeUserDefinedDE
	//			.addPermissibleValue(permissibleValueCollection.get(2));
	//
	//			tumourTissueSiteCategoryAttribute
	//			.setDataElement(tumourTissueSiteCategoryAttributeUserDefinedDE);
	//			tumourTissueSiteCategoryAttribute.setDefaultValue(permissibleValueCollection.get(1));
	//
	//			// Create category entity from prostatePathologyAnnotation entity.
	//			final CategoryEntityInterface prostatePathologyAnnotationCategoryEntity = factory
	//			.createCategoryEntity();
	//			prostatePathologyAnnotationCategoryEntity
	//			.setName("prostatePathologyAnnotationCategoryEntity");
	//			prostatePathologyAnnotationCategoryEntity.setEntity(prostatePathologyAnnotation);
	//			prostatePathologyAnnotationCategoryEntity.setNumberOfEntries(-1);
	//			prostatePathologyAnnotationCategoryEntity
	//			.setParentCategoryEntity(baseSolidTissuePathologyAnnotationCategoryEntity);
	//
	//			// Fetch the prostatePathologyAnnotation attributes' list.
	//			final List<AttributeInterface> attributesList2 = new ArrayList<AttributeInterface>(
	//					prostatePathologyAnnotation.getAttributeCollection());
	//
	//			// Create category attribute(s) for prostatePathologyAnnotationCategoryEntity category entity.
	//			final CategoryAttributeInterface seminalVesicleInvasionCategoryAttribute = factory
	//			.createCategoryAttribute();
	//			seminalVesicleInvasionCategoryAttribute
	//			.setName("seminalVesicleInvasionCategoryAttribute");
	//			seminalVesicleInvasionCategoryAttribute.setAbstractAttribute(attributesList2.get(0));
	//			prostatePathologyAnnotationCategoryEntity
	//			.addCategoryAttribute(seminalVesicleInvasionCategoryAttribute);
	//			seminalVesicleInvasionCategoryAttribute
	//			.setCategoryEntity(prostatePathologyAnnotationCategoryEntity);
	//
	//			final CategoryAttributeInterface periprostaticFatInvasionCategoryAttribute = factory
	//			.createCategoryAttribute();
	//			periprostaticFatInvasionCategoryAttribute
	//			.setName("periprostaticFatInvasionCategoryAttribute");
	//			periprostaticFatInvasionCategoryAttribute.setAbstractAttribute(attributesList2.get(1));
	//			prostatePathologyAnnotationCategoryEntity
	//			.addCategoryAttribute(periprostaticFatInvasionCategoryAttribute);
	//			periprostaticFatInvasionCategoryAttribute
	//			.setCategoryEntity(prostatePathologyAnnotationCategoryEntity);
	//
	//			// Create category entity from gleasonScore entity.
	//			final CategoryEntityInterface gleasonScoreCategoryEntity = factory.createCategoryEntity();
	//			gleasonScoreCategoryEntity.setName("gleasonScoreCategoryEntity");
	//			gleasonScoreCategoryEntity.setEntity(gleasonScore);
	//			gleasonScoreCategoryEntity.setNumberOfEntries(-1);
	//
	//			// Fetch the gleasonScore's attributes' list.
	//			final List<AttributeInterface> attributesList3 = new ArrayList<AttributeInterface>(
	//					gleasonScore.getAttributeCollection());
	//
	//			// Create category attribute(s) for gleasonScoreCategoryEntity.
	//			final CategoryAttributeInterface primaryPatternCategoryAttribute = factory
	//			.createCategoryAttribute();
	//			primaryPatternCategoryAttribute.setName("primaryPatternCategoryAttribute");
	//			primaryPatternCategoryAttribute.setAbstractAttribute(attributesList3.get(0));
	//			gleasonScoreCategoryEntity.addCategoryAttribute(primaryPatternCategoryAttribute);
	//			primaryPatternCategoryAttribute.setCategoryEntity(gleasonScoreCategoryEntity);
	//
	//			final CategoryAttributeInterface secondaryPatternCategoryAttribute = factory
	//			.createCategoryAttribute();
	//			secondaryPatternCategoryAttribute.setName("secondaryPatternCategoryAttribute");
	//			secondaryPatternCategoryAttribute.setAbstractAttribute(attributesList3.get(1));
	//			gleasonScoreCategoryEntity.addCategoryAttribute(secondaryPatternCategoryAttribute);
	//			secondaryPatternCategoryAttribute.setCategoryEntity(gleasonScoreCategoryEntity);
	//
	//			// Fetch the prostatePathologyAnnotation's associations' list.
	//			final List<AssociationInterface> associationsList1 = new ArrayList<AssociationInterface>(
	//					prostatePathologyAnnotation.getAssociationCollection());
	//
	//			// Create a path between prostate pathology annotation category entity and gleason score category entity.
	//			final PathInterface path1 = factory.createPath();
	//
	//			final PathAssociationRelationInterface pathAssociationRelationForPath1 = factory
	//			.createPathAssociationRelation();
	//			pathAssociationRelationForPath1.setAssociation(associationsList1.get(0));
	//			pathAssociationRelationForPath1.setPathSequenceNumber(1);
	//			pathAssociationRelationForPath1.setPath(path1);
	//			path1.addPathAssociationRelation(pathAssociationRelationForPath1);
	//
	//			// Add path information to the target category entity.
	//			gleasonScoreCategoryEntity.setPath(path1);
	//
	//			// Create a category association between prostate pathology annotation category entity
	//			// and gleason score category entity that corresponds to association between prostate pathology annotation
	//			// and gleason score.
	//			final CategoryAssociationInterface prostateGleasonCategoryAssociation = factory
	//			.createCategoryAssociation();
	//			prostateGleasonCategoryAssociation
	//			.setName("prostateGleasonAssociationCategoryAssociation");
	//			prostateGleasonCategoryAssociation
	//			.setCategoryEntity(prostatePathologyAnnotationCategoryEntity);
	//			prostateGleasonCategoryAssociation.setTargetCategoryEntity(gleasonScoreCategoryEntity);
	//
	//			prostatePathologyAnnotationCategoryEntity.getCategoryAssociationCollection().add(
	//					prostateGleasonCategoryAssociation);
	//
	//			// Make gleason score category entity a child of prostate pathology annotation category entity.
	//			prostatePathologyAnnotationCategoryEntity.addChildCategory(gleasonScoreCategoryEntity);
	//
	//			// Create category entity from radicalProstatectomyPathologyAnnotation entity.
	//			final CategoryEntityInterface radicalProstatectomyPathologyAnnotationCategoryEntity = factory
	//			.createCategoryEntity();
	//			radicalProstatectomyPathologyAnnotationCategoryEntity
	//			.setName("radicalProstatectomyPathologyAnnotationCategoryEntity");
	//			radicalProstatectomyPathologyAnnotationCategoryEntity
	//			.setEntity(radicalProstatectomyPathologyAnnotation);
	//			radicalProstatectomyPathologyAnnotationCategoryEntity.setNumberOfEntries(-1);
	//			radicalProstatectomyPathologyAnnotationCategoryEntity
	//			.setParentCategoryEntity(prostatePathologyAnnotationCategoryEntity);
	//
	//			// Fetch the radicalProstatectomyPathologyAnnotation's attributes' list.
	//			final List<AttributeInterface> attributesList4 = new ArrayList<AttributeInterface>(
	//					radicalProstatectomyPathologyAnnotation.getAttributeCollection());
	//
	//			// Create category attribute(s) for radicalProstatectomyPathologyAnnotationCategoryEntity.
	//			final CategoryAttributeInterface radicalProstateNameCategoryAttribute = factory
	//			.createCategoryAttribute();
	//			radicalProstateNameCategoryAttribute.setName("radicalProstateNameCategoryAttribute");
	//			radicalProstateNameCategoryAttribute.setAbstractAttribute(attributesList4.get(0));
	//			radicalProstatectomyPathologyAnnotationCategoryEntity
	//			.addCategoryAttribute(radicalProstateNameCategoryAttribute);
	//			radicalProstateNameCategoryAttribute
	//			.setCategoryEntity(radicalProstatectomyPathologyAnnotationCategoryEntity);
	//
	//			final CategoryAttributeInterface radicalProstateTypeCategoryAttribute = factory
	//			.createCategoryAttribute();
	//			radicalProstateTypeCategoryAttribute.setName("radicalProstateTypeCategoryAttribute");
	//			radicalProstateTypeCategoryAttribute.setAbstractAttribute(attributesList4.get(1));
	//			radicalProstatectomyPathologyAnnotationCategoryEntity
	//			.addCategoryAttribute(radicalProstateTypeCategoryAttribute);
	//			radicalProstateTypeCategoryAttribute
	//			.setCategoryEntity(radicalProstatectomyPathologyAnnotationCategoryEntity);
	//
	//			// Create category entity from melanomaMargin entity.
	//			final CategoryEntityInterface melanomaMarginCategoryEntity = factory.createCategoryEntity();
	//			melanomaMarginCategoryEntity.setName("melanomaMarginCategoryEntity");
	//			melanomaMarginCategoryEntity.setEntity(melanomaMargin);
	//			melanomaMarginCategoryEntity.setNumberOfEntries(-1);
	//
	//			// Fetch the melanomaMargin's attributes' list.
	//			final List<AttributeInterface> attributesList5 = new ArrayList<AttributeInterface>(
	//					melanomaMargin.getAttributeCollection());
	//
	//			// Create category attribute(s) for melanomaMarginCategoryEntity.
	//			final CategoryAttributeInterface melanomaMarginNameCategoryAttribute = factory
	//			.createCategoryAttribute();
	//			melanomaMarginNameCategoryAttribute.setName("melanomaMarginNameCategoryAttribute");
	//			melanomaMarginNameCategoryAttribute.setAbstractAttribute(attributesList5.get(0));
	//			melanomaMarginCategoryEntity.addCategoryAttribute(melanomaMarginNameCategoryAttribute);
	//			melanomaMarginNameCategoryAttribute.setCategoryEntity(melanomaMarginCategoryEntity);
	//
	//			final CategoryAttributeInterface melanomaMarginTypeCategoryAttribute = factory
	//			.createCategoryAttribute();
	//			melanomaMarginTypeCategoryAttribute.setName("melanomaMarginTypeCategoryAttribute");
	//			melanomaMarginTypeCategoryAttribute.setAbstractAttribute(attributesList5.get(1));
	//			melanomaMarginCategoryEntity.addCategoryAttribute(melanomaMarginTypeCategoryAttribute);
	//			melanomaMarginTypeCategoryAttribute.setCategoryEntity(melanomaMarginCategoryEntity);
	//
	//			// Make melanoma margin category entity a child of radical prostatectomy pathology annotation category entity.
	//			radicalProstatectomyPathologyAnnotationCategoryEntity
	//			.addChildCategory(melanomaMarginCategoryEntity);
	//
	//			// Create a category association between radical prostatectomy pathology annotation category entity
	//			// and melanoma margin category entity that corresponds to association between radical prostatectomy pathology annotation
	//			// and melanoma margin.
	//			final CategoryAssociationInterface prostatePathologyMelanomaMarginCategoryAssociation = factory
	//			.createCategoryAssociation();
	//			prostatePathologyMelanomaMarginCategoryAssociation
	//			.setName("prostatePathologymelanomaMarginCategoryAssociation");
	//			prostatePathologyMelanomaMarginCategoryAssociation
	//			.setCategoryEntity(radicalProstatectomyPathologyAnnotationCategoryEntity);
	//			prostatePathologyMelanomaMarginCategoryAssociation
	//			.setTargetCategoryEntity(melanomaMarginCategoryEntity);
	//			radicalProstatectomyPathologyAnnotationCategoryEntity
	//			.getCategoryAssociationCollection().add(
	//					prostatePathologyMelanomaMarginCategoryAssociation);
	//
	//			// Fetch the radicalProstatectomyPathologyAnnotation's associations' list.
	//			final List<AssociationInterface> associationsList2 = new ArrayList<AssociationInterface>(
	//					radicalProstatectomyPathologyAnnotation.getAssociationCollection());
	//
	//			// Fetch the radicalProstatectomyMargin's associations' list.
	//			final List<AssociationInterface> associationsList3 = new ArrayList<AssociationInterface>(
	//					radicalProstatectomyMargin.getAssociationCollection());
	//
	//			// Create a path between radical prostatectomy pathology annotation category entity and melanoma margin category entity.
	//			final PathInterface path2 = factory.createPath();
	//
	//			final PathAssociationRelationInterface pathAssociationRelation1ForPath2 = factory
	//			.createPathAssociationRelation();
	//			pathAssociationRelation1ForPath2.setAssociation(associationsList2.get(0));
	//			pathAssociationRelation1ForPath2.setPathSequenceNumber(1);
	//
	//			final PathAssociationRelationInterface pathAssociationRelation2ForPath2 = factory
	//			.createPathAssociationRelation();
	//			pathAssociationRelation2ForPath2.setAssociation(associationsList3.get(0));
	//			pathAssociationRelation2ForPath2.setPathSequenceNumber(2);
	//
	//			pathAssociationRelation1ForPath2.setPath(path2);
	//			pathAssociationRelation2ForPath2.setPath(path2);
	//
	//			path2.addPathAssociationRelation(pathAssociationRelation1ForPath2);
	//			path2.addPathAssociationRelation(pathAssociationRelation2ForPath2);
	//
	//			// Add path information to the target category entity.
	//			melanomaMarginCategoryEntity.setPath(path2);
	//
	//			// Set the root category element of the category.
	//			category.setRootCategoryElement(radicalProstatectomyPathologyAnnotationCategoryEntity);
	//			radicalProstatectomyPathologyAnnotationCategoryEntity.setCategory(category);
	//
	//			// Create containers for category entities.
	//			int sequenceNumber = 1;
	//
	//			// Create a container for baseSolidTissuePathologyAnnotationCategoryEntity.
	//			final ContainerInterface baseSolidTissuePathologyAnnotationContainer = createContainer(baseSolidTissuePathologyAnnotationCategoryEntity);
	//			baseSolidTissuePathologyAnnotationContainer.setCaption("Base Solid Tissue Pathology");
	//
	//			// Create a control for tissueSlideCategoryAttribute.
	//			final TextFieldInterface tissueSlideControl = createTextFieldControl(
	//					tissueSlideCategoryAttribute, sequenceNumber++);
	//			// Create a control for tumourTissueSiteCategoryAttribute.
	//			final ComboBoxInterface tumourTissueSiteControl = createComboBoxControl(
	//					tumourTissueSiteCategoryAttribute, sequenceNumber++);
	//
	//			tissueSlideControl
	//			.setParentContainer((Container) baseSolidTissuePathologyAnnotationContainer);
	//			baseSolidTissuePathologyAnnotationContainer.addControl(tissueSlideControl);
	//
	//			tumourTissueSiteControl
	//			.setParentContainer((Container) baseSolidTissuePathologyAnnotationContainer);
	//			baseSolidTissuePathologyAnnotationContainer.addControl(tumourTissueSiteControl);
	//
	//			// Create a container for prostatePathologyAnnotationCategoryContainer.
	//			final ContainerInterface prostatePathologyAnnotationCategoryContainer = createContainer(prostatePathologyAnnotationCategoryEntity);
	//			prostatePathologyAnnotationCategoryContainer
	//			.setCaption("Prostate Pathology Annotation");
	//
	//			// Create a control for seminalVesicleInvasionCategoryAttribute.
	//			final TextFieldInterface seminalVesicleInvasionControl = createTextFieldControl(
	//					seminalVesicleInvasionCategoryAttribute, sequenceNumber++);
	//			// Create a control for periprostaticFatInvasionCategoryAttribute.
	//			final TextFieldInterface periprostaticFatInvasionControl = createTextFieldControl(
	//					periprostaticFatInvasionCategoryAttribute, sequenceNumber++);
	//
	//			seminalVesicleInvasionControl
	//			.setParentContainer((Container) prostatePathologyAnnotationCategoryContainer);
	//			prostatePathologyAnnotationCategoryContainer.addControl(seminalVesicleInvasionControl);
	//
	//			periprostaticFatInvasionControl
	//			.setParentContainer((Container) prostatePathologyAnnotationCategoryContainer);
	//			prostatePathologyAnnotationCategoryContainer
	//			.addControl(periprostaticFatInvasionControl);
	//
	//			// Create a container for gleasonScoreCategoryEntity.
	//			final ContainerInterface gleasonScoreContainer = createContainer(gleasonScoreCategoryEntity);
	//			gleasonScoreContainer.setCaption("Gleason Score");
	//
	//			// Create a control for primaryPatternCategoryAttribute.
	//			final TextFieldInterface primaryPatternControl = createTextFieldControl(
	//					primaryPatternCategoryAttribute, sequenceNumber++);
	//			// Create a control for secondaryPatternCategoryAttribute.
	//			final TextFieldInterface secondaryPatternControl = createTextFieldControl(
	//					secondaryPatternCategoryAttribute, sequenceNumber++);
	//
	//			primaryPatternControl.setParentContainer((Container) gleasonScoreContainer);
	//			gleasonScoreContainer.addControl(primaryPatternControl);
	//
	//			secondaryPatternControl.setParentContainer((Container) gleasonScoreContainer);
	//			gleasonScoreContainer.addControl(secondaryPatternControl);
	//
	//			final AbstractContainmentControlInterface prostateGleasonCategoryContainmentControl = factory
	//			.createCategoryAssociationControl();
	//			prostateGleasonCategoryContainmentControl
	//			.setCaption("prostateGleasonCategory association");
	//			prostateGleasonCategoryContainmentControl
	//			.setBaseAbstractAttribute(prostateGleasonCategoryAssociation);
	//			prostateGleasonCategoryContainmentControl.setSequenceNumber(sequenceNumber++);
	//			prostateGleasonCategoryContainmentControl.setContainer(gleasonScoreContainer);
	//			prostateGleasonCategoryContainmentControl
	//			.setParentContainer((Container) prostatePathologyAnnotationCategoryContainer);
	//
	//			prostatePathologyAnnotationCategoryContainer
	//			.addControl(prostateGleasonCategoryContainmentControl);
	//
	//			// Create a container for radicalProstatectomyPathologyAnnotationCategoryEntity.
	//			final ContainerInterface radicalProstatectomyPathologyAnnotationContainer = createContainer(radicalProstatectomyPathologyAnnotationCategoryEntity);
	//			radicalProstatectomyPathologyAnnotationContainer
	//			.setCaption("Radical Prostatectomy Pathology Annotation");
	//
	//			// Create a control for radicalProstateNameCategoryAttribute.
	//			final TextFieldInterface radicalProstateNameControl = createTextFieldControl(
	//					radicalProstateNameCategoryAttribute, sequenceNumber++);
	//			// Create a control for radicalProstateTypeCategoryAttribute.
	//			final TextFieldInterface radicalProstateTypeControl = createTextFieldControl(
	//					radicalProstateTypeCategoryAttribute, sequenceNumber++);
	//
	//			radicalProstateNameControl
	//			.setParentContainer((Container) radicalProstatectomyPathologyAnnotationContainer);
	//			radicalProstatectomyPathologyAnnotationContainer.addControl(radicalProstateNameControl);
	//
	//			radicalProstateTypeControl
	//			.setParentContainer((Container) radicalProstatectomyPathologyAnnotationContainer);
	//			radicalProstatectomyPathologyAnnotationContainer.addControl(radicalProstateTypeControl);
	//
	//			// Create a container for melanomaMarginCategoryEntity.
	//			final ContainerInterface melanomaMarginContainer = createContainer(melanomaMarginCategoryEntity);
	//			melanomaMarginContainer.setCaption("Melanoma Margin");
	//
	//			// Create a control for melanomaMarginNameCategoryAttribute.
	//			final TextFieldInterface melanomaMarginNameControl = createTextFieldControl(
	//					melanomaMarginNameCategoryAttribute, sequenceNumber++);
	//			// Create a control for melanomaMarginTypeCategoryAttribute.
	//			final TextFieldInterface melanomaMarginTypeControl = createTextFieldControl(
	//					melanomaMarginTypeCategoryAttribute, sequenceNumber++);
	//
	//			melanomaMarginNameControl.setParentContainer((Container) melanomaMarginContainer);
	//			melanomaMarginContainer.addControl(melanomaMarginNameControl);
	//
	//			melanomaMarginTypeControl.setParentContainer((Container) melanomaMarginContainer);
	//			melanomaMarginContainer.addControl(melanomaMarginTypeControl);
	//
	//			// Create a containment control.
	//			final AbstractContainmentControlInterface prostatePathologyMelanomaMarginCategoryContainmentControl = factory
	//			.createCategoryAssociationControl();
	//			prostatePathologyMelanomaMarginCategoryContainmentControl
	//			.setBaseAbstractAttribute(prostatePathologyMelanomaMarginCategoryAssociation);
	//			prostatePathologyMelanomaMarginCategoryContainmentControl
	//			.setSequenceNumber(sequenceNumber++);
	//			prostatePathologyMelanomaMarginCategoryContainmentControl
	//			.setCaption("prostatePathologyMelanomaMargin association");
	//			prostatePathologyMelanomaMarginCategoryContainmentControl
	//			.setContainer(melanomaMarginContainer);
	//			prostatePathologyMelanomaMarginCategoryContainmentControl
	//			.setParentContainer((Container) radicalProstatectomyPathologyAnnotationContainer);
	//
	//			radicalProstatectomyPathologyAnnotationContainer
	//			.addControl(prostatePathologyMelanomaMarginCategoryContainmentControl);
	//
	//			// Link containers.
	//			prostatePathologyAnnotationCategoryContainer
	//			.setBaseContainer(baseSolidTissuePathologyAnnotationContainer);
	//			radicalProstatectomyPathologyAnnotationContainer
	//			.setBaseContainer(prostatePathologyAnnotationCategoryContainer);
	//
	//			// Save category.
	//			categoryManager.persistCategory(category);
	//
	//			// Create data insertion map for category
	//			//						Map<BaseAbstractAttributeInterface, Object> radicalProstateDataCategoryMap = new HashMap<BaseAbstractAttributeInterface, Object>();
	//			//
	//			//						radicalProstateDataCategoryMap.put(tissueSlideCategoryAttribute, "tissueSlideCategoryAttribute");
	//			//						radicalProstateDataCategoryMap.put(tumourTissueSiteCategoryAttribute, "tumourTissueSiteCategoryAttribute");
	//			//						radicalProstateDataCategoryMap.put(seminalVesicleInvasionCategoryAttribute, "seminalVesicleInvasionCategoryAttribute");
	//			//						radicalProstateDataCategoryMap.put(periprostaticFatInvasionCategoryAttribute, "periprostaticFatInvasionCategoryAttribute");
	//			//
	//			//						List<Map<BaseAbstractAttributeInterface, Object>> prostateGleasonCategoryAssociationDataList = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
	//			//
	//			//						Map<BaseAbstractAttributeInterface, Object> prostateGleasonAssociationCategoryAssociationDataMap = new HashMap<BaseAbstractAttributeInterface, Object>();
	//			//						prostateGleasonAssociationCategoryAssociationDataMap.put(primaryPatternCategoryAttribute, "primaryPatternCategoryAttribute");
	//			//						prostateGleasonAssociationCategoryAssociationDataMap.put(secondaryPatternCategoryAttribute, "secondaryPatternCategoryAttribute");
	//			//						prostateGleasonCategoryAssociationDataList.add(prostateGleasonAssociationCategoryAssociationDataMap);
	//			//
	//			//						radicalProstateDataCategoryMap.put(prostateGleasonCategoryAssociation, prostateGleasonCategoryAssociationDataList);
	//			//
	//			//						radicalProstateDataCategoryMap.put(radicalProstateNameCategoryAttribute, "radicalProstateNameCategoryAttribute");
	//			//						radicalProstateDataCategoryMap.put(radicalProstateTypeCategoryAttribute, "radicalProstateTypeCategoryAttribute");
	//			//
	//			//						List<Map<BaseAbstractAttributeInterface, Object>> prostatePathologyMelanomaMarginCategoryAssociationDataList = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
	//			//						Map<BaseAbstractAttributeInterface, Object> prostatePathologyMelanomaMarginCategoryAssociationDataMap = new HashMap<BaseAbstractAttributeInterface, Object>();
	//			//						prostatePathologyMelanomaMarginCategoryAssociationDataMap.put(melanomaMarginNameCategoryAttribute, "melanomaMarginNameCategoryAttribute");
	//			//						prostatePathologyMelanomaMarginCategoryAssociationDataMap.put(melanomaMarginTypeCategoryAttribute, "melanomaMarginTypeCategoryAttribute");
	//			//						prostatePathologyMelanomaMarginCategoryAssociationDataList.add(prostatePathologyMelanomaMarginCategoryAssociationDataMap);
	//			//
	//			//						radicalProstateDataCategoryMap.put(prostatePathologyMelanomaMarginCategoryAssociation,
	//			//								prostatePathologyMelanomaMarginCategoryAssociationDataList);
	//			//
	//			//						categoryManager.insertData(category, radicalProstateDataCategoryMap);
	//		}
	//		catch (final Exception e)
	//		{
	//			e.printStackTrace();
	//			Logger.out.debug(DynamicExtensionsUtility.getStackTrace(e));
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Create a non-linear category tree from non-linear entity tree.
	//	 * Save the category.
	//	 */
	//	public void testCreateCategoryFromNonLinearEntityTree()
	//	{
	//		final MockCategoryManager mockCategoryManager = new MockCategoryManager();
	//		final CategoryManagerInterface categoryManager = CategoryManager.getInstance();
	//		final CategoryInterface category = mockCategoryManager.createCategoryFromModel1();
	//		try
	//		{
	//			// Save category.
	//			categoryManager.persistCategory(category);
	//		}
	//		catch (final DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (final DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Add a category entity to a category.
	//	 * Save the category. This should not result in new tables creation,
	//	 * just one more category entity should be added to category.
	//	 */
	//	public void testAddCategoryEntityToCategoryFromNonLinearEntityTree()
	//	{
	//		final MockCategoryManager mockCategoryManager = new MockCategoryManager();
	//		final CategoryManagerInterface categoryManager = CategoryManager.getInstance();
	//		CategoryInterface category = mockCategoryManager.createCategoryFromModel1();
	//
	//		try
	//		{
	//			// Save the category.
	//			categoryManager.persistCategory(category);
	//
	//			// Add another category entity to present category.
	//			category = mockCategoryManager.addNewCategoryEntityToExistingCategory(category);
	//
	//			// Again save the category.
	//			categoryManager.persistCategory(category);
	//		}
	//		catch (final DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (final DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Edit metadata for a category entity in a category.
	//	 * Save the category. This should not result in new tables creation,
	//	 * just the category entity metadata information should get updated.
	//	 */
	//	public void testEditCategoryEntityMetadataFromCategoryFromNonLinearEntityTree()
	//	{
	//		final MockCategoryManager mockCategoryManager = new MockCategoryManager();
	//		final CategoryManagerInterface categoryManager = CategoryManager.getInstance();
	//		final CategoryInterface category = mockCategoryManager.createCategoryFromModel1();
	//
	//		try
	//		{
	//			// Save the category.
	//			categoryManager.persistCategory(category);
	//
	//			// Edit category entity metadata.
	//			category.getRootCategoryElement().setName(
	//			"chemotherapyTrialsCategoryEntity name changed");
	//
	//			// Again save the category. This should not result in new tables creation,
	//			// just the category entity metadata information should get updated.
	//			categoryManager.persistCategory(category);
	//		}
	//		catch (final DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (final DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Add a category attribute to a category entity.
	//	 * Save the category. This should not result in new tables creation,
	//	 * just one more category attribute should be added to category entity.
	//	 */
	//	public void testAddCategoryAttributeToCategoryFromNonLinearEntityTree()
	//	{
	//		final MockCategoryManager mockCategoryManager = new MockCategoryManager();
	//		final CategoryManagerInterface categoryManager = CategoryManager.getInstance();
	//		CategoryInterface category = mockCategoryManager.createCategoryFromModel1();
	//
	//		try
	//		{
	//			// Save the category.
	//			categoryManager.persistCategory(category);
	//
	//			// Add category attribute to category entity.
	//			category = mockCategoryManager.addCategoryAttributetyToCategoryEntity(category);
	//
	//			// Again save the category. This should not result in new tables creation,
	//			// just one more category attribute should be added to category entity.
	//			categoryManager.persistCategory(category);
	//		}
	//		catch (final DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (final DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Edit metadata for a category attribute in a category entity.
	//	 * Save the category. This should not result in new tables creation,
	//	 * just the category attribute metadata information should get updated.
	//	 */
	//	public void testEditCategoryAttributeMetadataFromCategoryFromNonLinearEntityTree()
	//	{
	//		final MockCategoryManager mockCategoryManager = new MockCategoryManager();
	//		final CategoryManagerInterface categoryManager = CategoryManager.getInstance();
	//		final CategoryInterface category = mockCategoryManager.createCategoryFromModel1();
	//
	//		try
	//		{
	//			// Save the category.
	//			categoryManager.persistCategory(category);
	//
	//			// Edit category attribute metadata.
	//			final CategoryEntityInterface rootCategoryEntity = category.getRootCategoryElement();
	//
	//			for (final CategoryAttributeInterface ca : rootCategoryEntity
	//					.getCategoryAttributeCollection())
	//			{
	//				ca.setName(ca.getName() + String.valueOf(new Double(Math.random()).intValue()));
	//			}
	//
	//			// Again save the category. This should not result in new tables creation,
	//			// just the category attribute metadata information should get updated.
	//			categoryManager.persistCategory(category);
	//		}
	//		catch (final DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (final DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Test data entry for a category entity with related attributes.
	//	 */
	//	public void testDataEntryForCategoryEntityWithRelatedAttributes()
	//	{
	//		final String filePath = CSV_FILE_PATH + "catMedicalHistoryRadiologicalDiagnosis.csv";
	//
	//		try
	//		{
	//			final CategoryManagerInterface categoryManager = CategoryManager.getInstance();
	//			final CategoryGenerator categoryGenerator = new CategoryGenerator(filePath,"");
	//
	//			CategoryInterface category = categoryGenerator.generateCategory();
	//
	//			// Save the category.
	//			category = categoryManager.persistCategory(category);
	//
	//			// populate the values map.
	//			final Map<BaseAbstractAttributeInterface, Object> attributeValues = populateMap(category);
	//
	//			final JDBCDAO dao = DynamicExtensionsUtility.getJDBCDAO();
	//
	//			// retrieve category entity of the category entity 'Annotations[1]LabAnnotation[2]'
	//			final CategoryEntityInterface catEnt2 = category
	//			.getCategoryEntityByName("Annotations[1]LabAnnotation[2]");
	//
	//			// insert values for the category.
	//			categoryManager.insertData(category, attributeValues);
	//
	//			final String result = dao.executeQuery(
	//					"select MAX(identifier) from "
	//					+ catEnt2.getEntity().getTableProperties().getName()).get(0).toString();
	//			final int maxId = Integer.parseInt(result.substring(1, result.length() - 1));
	//
	//			// verify that the records for related category attribute 'labTestName' have been inserted.
	//			final CategoryAttributeInterface catAttrLabTestName = catEnt2
	//			.getAttributeByName("labTestName Category Attribute");
	//			final AttributeInterface attrLabTestName = (AttributeInterface) catAttrLabTestName
	//			.getAbstractAttribute();
	//
	//			// get the column name for lab test name attribute.
	//			final String colNameForAttrLabTestName = attrLabTestName.getColumnProperties().getName();
	//			// get the table name for LabAnnotation entity.
	//			final String tblNameForlabAnnotation = catEnt2.getEntity().getTableProperties().getName();
	//
	//			// verify that for all records inserted, the value for attribute 'labTestName' is 'Bone Density'
	//			for (int j = maxId; j > 0; j--)
	//			{
	//				String boneDensity = dao.executeQuery(
	//						"select " + colNameForAttrLabTestName + " from " + tblNameForlabAnnotation
	//						+ " where identifier = " + maxId).get(0).toString();
	//				boneDensity = boneDensity.substring(1, boneDensity.length() - 1);
	//				assertEquals("Bone Density", boneDensity);
	//			}
	//
	//			DynamicExtensionsUtility.closeJDBCDAO(dao);
	//		}
	//		catch (final DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (final DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (final FileNotFoundException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (final ParseException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (final DAOException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Create category where attributes from a particular class are not chosen,
	//	 * i.e. not selecting attributes from GleasonScore entity.
	//	 * Refer to pathology annotation model.
	//	 */
	//	public void testCreateCategoryWithMultiselectCheckBox()
	//	{
	//		CategoryInterface category = null;
	//		try
	//		{
	//			final String[] args1 = {XMI_FILE_PATH + "test.xmi",CSV_FILE_PATH + "Skip_Logic_Main.csv", "annotations", "  "};
	//			XMIImporter.main(args1);
	//
	//
	//			final String[] args2 = {CSV_FILE_PATH + "category_multiselectCheckBox.csv"};
	//			CategoryCreator.main(args2);
	//
	//			final CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
	//			category = (CategoryInterface) categoryManager.getObjectByName(
	//					Category.class.getName(), "Test_Cat_Rad_therapy_multiselectCheckbox");
	//
	//			assertNotNull(category.getId());
	//			assertNotNull(category.getRootCategoryElement());
	//			assertEquals(category.getName(), "Test_Cat_Rad_therapy_multiselectCheckbox");
	//
	//		}
	//		catch (final Exception e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//	/**
	//	 * Make a entity group from following entities:
	//	 * Entities : user (1)------>(*) study
	//	 * @return EntityGroupInterface
	//	 * @throws DynamicExtensionsSystemException
	//	 */
	//	private EntityGroupInterface createEntityGroup1() throws DynamicExtensionsSystemException
	//	{
	//		final DomainObjectFactory factory = DomainObjectFactory.getInstance();
	//
	//		// Create entity group.
	//		final EntityGroupInterface entityGroup = factory.createEntityGroup();
	//		entityGroup.setName("Entity Group " + new Double(Math.random()).toString());
	//		entityGroup.setShortName("User-Study EG1");
	//
	//		// Create user entity.
	//		final EntityInterface user = createAndPopulateEntity();
	//		user.setName("User Entity");
	//
	//		// Create attribute(s) for user entity.
	//		final AttributeInterface userName = factory.createStringAttribute();
	//		userName.setName("User Name");
	//		((StringAttributeTypeInformation) userName.getAttributeTypeInformation()).setSize(40);
	//
	//		final AttributeInterface userGender = factory.createStringAttribute();
	//		userGender.setName("User Gender");
	//		((StringAttributeTypeInformation) userGender.getAttributeTypeInformation()).setSize(40);
	//
	//		final AttributeInterface userAddress = factory.createStringAttribute();
	//		userAddress.setName("User Address");
	//		((StringAttributeTypeInformation) userAddress.getAttributeTypeInformation()).setSize(40);
	//
	//		// Add attribute(s) to user entity.
	//		user.addAbstractAttribute(userName);
	//		user.addAbstractAttribute(userGender);
	//		user.addAbstractAttribute(userAddress);
	//
	//		// Create study entity.
	//		final EntityInterface study = createAndPopulateEntity();
	//		study.setName("Study Entity");
	//
	//		// Create attribute(s) for study entity.
	//		final AttributeInterface studyName = factory.createStringAttribute();
	//		studyName.setName("Study Name");
	//		((StringAttributeTypeInformation) studyName.getAttributeTypeInformation()).setSize(40);
	//
	//		final AttributeInterface studyType = factory.createStringAttribute();
	//		studyType.setName("Study Type");
	//		((StringAttributeTypeInformation) studyType.getAttributeTypeInformation()).setSize(40);
	//
	//		final AttributeInterface studyDescription = factory.createStringAttribute();
	//		studyDescription.setName("Study Description");
	//		((StringAttributeTypeInformation) studyDescription.getAttributeTypeInformation())
	//		.setSize(40);
	//
	//		// Add attribute(s) to study entity.
	//		study.addAbstractAttribute(studyName);
	//		study.addAbstractAttribute(studyType);
	//		study.addAbstractAttribute(studyDescription);
	//
	//		// Associate user entity with study entity : user (1)------ >(*) study
	//		final AssociationInterface userToStudyAssociation = factory.createAssociation();
	//		userToStudyAssociation.setName("User To Study Association");
	//		userToStudyAssociation.setTargetEntity(study);
	//		userToStudyAssociation.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
	//
	//		// Create source role for user to study association.
	//		final RoleInterface sourceRole = getRole(AssociationType.ASSOCIATION, "primaryInvestigator",
	//				Cardinality.ONE, Cardinality.ONE);
	//		sourceRole.setAssociationsType(AssociationType.CONTAINTMENT);
	//
	//		userToStudyAssociation.setSourceRole(sourceRole);
	//		userToStudyAssociation.setTargetRole(getRole(AssociationType.ASSOCIATION, "study",
	//				Cardinality.ZERO, Cardinality.MANY));
	//
	//		user.addAbstractAttribute(userToStudyAssociation);
	//		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(userToStudyAssociation);
	//
	//		// Create containers for user and study entities.
	//		int sequenceNumber = 0;
	//
	//		final ContainerInterface userEntityContainer = createContainerForEntity(user);
	//		final ContainerInterface studyEntityContainer = createContainerForEntity(study);
	//
	//		// Create text field controls for attributes of user and study entities.
	//		createTextFieldControlForEntity(userEntityContainer, user, sequenceNumber);
	//		sequenceNumber = userEntityContainer.getControlCollection().size();
	//		createTextFieldControlForEntity(studyEntityContainer, study, sequenceNumber);
	//		sequenceNumber = sequenceNumber + studyEntityContainer.getControlCollection().size();
	//
	//		// Create a contaiment control.
	//		final AbstractContainmentControlInterface containmentControl = factory
	//		.createContainmentAssociationControl();
	//		containmentControl.setCaption("UserToStudyAbstractContainmentControl");
	//		containmentControl.setContainer(studyEntityContainer);
	//		containmentControl.setBaseAbstractAttribute(userToStudyAssociation);
	//		containmentControl.setSequenceNumber(++sequenceNumber);
	//
	//		containmentControl.setParentContainer((Container) userEntityContainer);
	//		userEntityContainer.addControl(containmentControl);
	//
	//		entityGroup.addEntity(user);
	//		user.setEntityGroup(entityGroup);
	//		entityGroup.addEntity(study);
	//		study.setEntityGroup(entityGroup);
	//
	//		return entityGroup;
	//	}
	//
	//	/**
	//	 * Make a entity group from following entities:
	//	 * Entities : user (1)------>(*) study (1) ------>(*) experiment
	//	 * @return EntityGroupInterface
	//	 * @throws DynamicExtensionsSystemException
	//	 */
	//	private EntityGroupInterface createEntityGroup2() throws DynamicExtensionsSystemException
	//	{
	//		final DomainObjectFactory factory = DomainObjectFactory.getInstance();
	//
	//		// Create entity group.
	//		final EntityGroupInterface entityGroup = factory.createEntityGroup();
	//		entityGroup.setName("Entity Group " + new Double(Math.random()).toString());
	//		entityGroup.setShortName("User-Study-Experiment EG1");
	//
	//		// Create user entity.
	//		final EntityInterface user = createAndPopulateEntity();
	//		user.setName("User Entity");
	//
	//		// Create attribute(s) for user entity.
	//		final AttributeInterface userName = factory.createStringAttribute();
	//		userName.setName("User Name");
	//		((StringAttributeTypeInformation) userName.getAttributeTypeInformation()).setSize(40);
	//
	//		final AttributeInterface userGender = factory.createStringAttribute();
	//		userGender.setName("User Gender");
	//		((StringAttributeTypeInformation) userGender.getAttributeTypeInformation()).setSize(40);
	//
	//		final AttributeInterface userAddress = factory.createStringAttribute();
	//		userAddress.setName("User Address");
	//		((StringAttributeTypeInformation) userAddress.getAttributeTypeInformation()).setSize(40);
	//
	//		// Add attribute(s) to user entity.
	//		user.addAbstractAttribute(userName);
	//		user.addAbstractAttribute(userGender);
	//		user.addAbstractAttribute(userAddress);
	//
	//		// Create study entity.
	//		final EntityInterface study = createAndPopulateEntity();
	//		study.setName("Study Entity");
	//
	//		// Create attribute(s) for study entity.
	//		final AttributeInterface studyName = factory.createStringAttribute();
	//		studyName.setName("Study Name");
	//		((StringAttributeTypeInformation) studyName.getAttributeTypeInformation()).setSize(40);
	//
	//		final AttributeInterface studyType = factory.createStringAttribute();
	//		studyType.setName("Study Type");
	//		((StringAttributeTypeInformation) studyType.getAttributeTypeInformation()).setSize(40);
	//
	//		final AttributeInterface studyDescription = factory.createStringAttribute();
	//		studyDescription.setName("Study Description");
	//		((StringAttributeTypeInformation) studyDescription.getAttributeTypeInformation())
	//		.setSize(40);
	//
	//		// Add attribute(s) to study entity.
	//		study.addAbstractAttribute(studyName);
	//		study.addAbstractAttribute(studyType);
	//		study.addAbstractAttribute(studyDescription);
	//
	//		// Create experiment entity.
	//		final EntityInterface experiment = createAndPopulateEntity();
	//		experiment.setName("Experiment Entity");
	//
	//		final AttributeInterface experimentName = factory.createStringAttribute();
	//		experimentName.setName("Experiment Name");
	//		((StringAttributeTypeInformation) experimentName.getAttributeTypeInformation()).setSize(40);
	//
	//		final AttributeInterface experimentType = factory.createStringAttribute();
	//		experimentType.setName("Experiment Type");
	//		((StringAttributeTypeInformation) experimentType.getAttributeTypeInformation()).setSize(40);
	//
	//		final AttributeInterface experimentDescription = factory.createStringAttribute();
	//		experimentDescription.setName("Experiment Description");
	//		((StringAttributeTypeInformation) experimentDescription.getAttributeTypeInformation())
	//		.setSize(40);
	//
	//		// Add attribute to experiment entity.
	//		experiment.addAbstractAttribute(experimentName);
	//		experiment.addAbstractAttribute(experimentType);
	//		experiment.addAbstractAttribute(experimentDescription);
	//
	//		// Associate user entity with study entity : user (1)------ >(*) study
	//		final AssociationInterface userToStudyAssociation = factory.createAssociation();
	//		userToStudyAssociation.setName("User To Study Association");
	//		userToStudyAssociation.setTargetEntity(study);
	//		userToStudyAssociation.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
	//
	//		// Create source role for user to study association.
	//		final RoleInterface sourceRole1 = getRole(AssociationType.ASSOCIATION,
	//				"User To Study Association Source Role", Cardinality.ONE, Cardinality.ONE);
	//		sourceRole1.setAssociationsType(AssociationType.CONTAINTMENT);
	//
	//		userToStudyAssociation.setSourceRole(sourceRole1);
	//		userToStudyAssociation.setTargetRole(getRole(AssociationType.ASSOCIATION,
	//				"User To Study Association Target Role", Cardinality.ZERO, Cardinality.MANY));
	//
	//		user.addAbstractAttribute(userToStudyAssociation);
	//		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(userToStudyAssociation);
	//
	//		// Associate study entity with experiment entity : study (1)------ >(*) experiment
	//		final AssociationInterface studyToExperimentAssociation = factory.createAssociation();
	//		studyToExperimentAssociation.setName("Study To Experiment Association");
	//		studyToExperimentAssociation.setTargetEntity(experiment);
	//		studyToExperimentAssociation.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
	//
	//		// Create source role for study to experiment association.
	//		final RoleInterface sourceRole2 = getRole(AssociationType.ASSOCIATION,
	//				"Study To Experiment Association Source Role", Cardinality.ONE, Cardinality.ONE);
	//		sourceRole2.setAssociationsType(AssociationType.CONTAINTMENT);
	//
	//		studyToExperimentAssociation.setSourceRole(sourceRole2);
	//		studyToExperimentAssociation.setTargetRole(getRole(AssociationType.ASSOCIATION,
	//				"Study To Experiment Association Target Role", Cardinality.ZERO, Cardinality.MANY));
	//
	//		study.addAbstractAttribute(studyToExperimentAssociation);
	//		DynamicExtensionsUtility
	//		.getConstraintPropertiesForAssociation(studyToExperimentAssociation);
	//
	//		// Create containers for user, study and experiment entities.
	//		int sequenceNumber = 0;
	//
	//		final ContainerInterface userEntityContainer = createContainerForEntity(user);
	//		final ContainerInterface studyEntityContainer = createContainerForEntity(study);
	//		final ContainerInterface experimentEntityContainer = createContainerForEntity(experiment);
	//
	//		// Create text field controls for attributes of user, study and experiment entities.
	//		createTextFieldControlForEntity(userEntityContainer, user, sequenceNumber);
	//		sequenceNumber = userEntityContainer.getControlCollection().size();
	//		createTextFieldControlForEntity(studyEntityContainer, study, sequenceNumber);
	//		sequenceNumber = sequenceNumber + studyEntityContainer.getControlCollection().size();
	//		createTextFieldControlForEntity(experimentEntityContainer, experiment, sequenceNumber);
	//		sequenceNumber = sequenceNumber + experimentEntityContainer.getControlCollection().size();
	//
	//		// Create contaiment controls.
	//		final AbstractContainmentControlInterface containmentControl1 = factory
	//		.createContainmentAssociationControl();
	//		containmentControl1.setCaption("UserToStudyContainmentControl");
	//		containmentControl1.setContainer(studyEntityContainer);
	//		containmentControl1.setBaseAbstractAttribute(userToStudyAssociation);
	//		containmentControl1.setSequenceNumber(++sequenceNumber);
	//
	//		containmentControl1.setParentContainer((Container) userEntityContainer);
	//		userEntityContainer.addControl(containmentControl1);
	//
	//		final AbstractContainmentControlInterface containmentControl2 = factory
	//		.createContainmentAssociationControl();
	//		containmentControl2.setCaption("StudyToExperimentContainmentControl");
	//		containmentControl2.setContainer(experimentEntityContainer);
	//		containmentControl2.setBaseAbstractAttribute(studyToExperimentAssociation);
	//		containmentControl2.setSequenceNumber(++sequenceNumber);
	//
	//		containmentControl2.setParentContainer((Container) studyEntityContainer);
	//		studyEntityContainer.addControl(containmentControl2);
	//
	//		entityGroup.addEntity(user);
	//		user.setEntityGroup(entityGroup);
	//		entityGroup.addEntity(study);
	//		study.setEntityGroup(entityGroup);
	//		entityGroup.addEntity(experiment);
	//		experiment.setEntityGroup(entityGroup);
	//
	//		return entityGroup;
	//	}
	//
	//	/**
	//	 * Create entity group from pathology annotation model.
	//	 * @return EntityGroupInterface
	//	 * @throws DynamicExtensionsSystemException
	//	 */
	//	private EntityGroupInterface createEntityGroup3() throws DynamicExtensionsSystemException
	//	{
	//		final DomainObjectFactory factory = DomainObjectFactory.getInstance();
	//
	//		final EntityGroupInterface entityGroup = factory.createEntityGroup();
	//		entityGroup.setName("Entity Group " + new Double(Math.random()).toString());
	//		entityGroup.setShortName("PathologyModel EG1");
	//
	//		// Create a baseSolidTissuePathologyAnnotation entity.
	//		final EntityInterface baseSolidTissuePathologyAnnotation = createAndPopulateEntity();
	//		baseSolidTissuePathologyAnnotation.setName("BaseSolidTissuePathologyAnnotation");
	//		baseSolidTissuePathologyAnnotation.setAbstract(true);
	//
	//		// Create attribute(s) for baseSolidTissuePathologyAnnotation entity.
	//		final AttributeInterface tissueSlide = factory.createStringAttribute();
	//		tissueSlide.setName("tissueSlide");
	//		((StringAttributeTypeInformation) tissueSlide.getAttributeTypeInformation()).setSize(40);
	//
	//		final AttributeInterface tumourTissueSite = factory.createStringAttribute();
	//		tumourTissueSite.setName("tumourTissueSite");
	//		((StringAttributeTypeInformation) tumourTissueSite.getAttributeTypeInformation())
	//		.setSize(40);
	//
	//		baseSolidTissuePathologyAnnotation.addAbstractAttribute(tissueSlide);
	//		baseSolidTissuePathologyAnnotation.addAbstractAttribute(tumourTissueSite);
	//
	//		// Create a prostatePathologyAnnotation entity.
	//		final EntityInterface prostatePathologyAnnotation = createAndPopulateEntity();
	//		prostatePathologyAnnotation.setName("ProstatePathologyAnnotation");
	//		prostatePathologyAnnotation.setParentEntity(baseSolidTissuePathologyAnnotation);
	//		DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(
	//				prostatePathologyAnnotation, false);
	//
	//		// Create attribute(s) for prostatePathologyAnnotation entity.
	//		final AttributeInterface seminalVesicleInvasion = factory.createStringAttribute();
	//		seminalVesicleInvasion.setName("seminalVesicleInvasion");
	//		((StringAttributeTypeInformation) seminalVesicleInvasion.getAttributeTypeInformation())
	//		.setSize(40);
	//
	//		final AttributeInterface periprostaticFatInvasion = factory.createStringAttribute();
	//		periprostaticFatInvasion.setName("periprostaticFatInvasion");
	//		((StringAttributeTypeInformation) periprostaticFatInvasion.getAttributeTypeInformation())
	//		.setSize(40);
	//
	//		prostatePathologyAnnotation.addAbstractAttribute(seminalVesicleInvasion);
	//		prostatePathologyAnnotation.addAbstractAttribute(periprostaticFatInvasion);
	//
	//		// Create a gleasonScore entity.
	//		final EntityInterface gleasonScore = createAndPopulateEntity();
	//		gleasonScore.setName("GleasonScore");
	//
	//		// Create attribute(s) for gleasonScore entity.
	//		final AttributeInterface primaryPattern = factory.createStringAttribute();
	//		primaryPattern.setName("primaryPattern");
	//		((StringAttributeTypeInformation) primaryPattern.getAttributeTypeInformation()).setSize(40);
	//
	//		final AttributeInterface secondaryPattern = factory.createStringAttribute();
	//		secondaryPattern.setName("secondaryPattern");
	//		((StringAttributeTypeInformation) secondaryPattern.getAttributeTypeInformation())
	//		.setSize(40);
	//
	//		gleasonScore.addAbstractAttribute(primaryPattern);
	//		gleasonScore.addAbstractAttribute(secondaryPattern);
	//
	//		// Associate prostatePathologyAnnotation entity with gleasonScore entity : prostatePathologyAnnotation (1)------ >(*) gleasonScore
	//		final AssociationInterface association1 = factory.createAssociation();
	//		association1.setName("prostatePathologyAnnotationToGleasonScoreAssociation");
	//		association1.setTargetEntity(gleasonScore);
	//		association1.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
	//
	//		// Create source role for prostatePathologyAnnotation to gleasonScore association.
	//		final RoleInterface sourceRole1 = getRole(AssociationType.ASSOCIATION,
	//				"prostatePathologyAnnotation", Cardinality.ONE, Cardinality.ONE);
	//		sourceRole1.setAssociationsType(AssociationType.CONTAINTMENT);
	//
	//		association1.setSourceRole(sourceRole1);
	//		association1.setTargetRole(getRole(AssociationType.ASSOCIATION, "gleasonScore",
	//				Cardinality.ZERO, Cardinality.MANY));
	//
	//		prostatePathologyAnnotation.addAbstractAttribute(association1);
	//		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association1);
	//
	//		// Create a radicalProstatectomyPathologyAnnotation entity.
	//		final EntityInterface radicalProstatectomyPathologyAnnotation = createAndPopulateEntity();
	//		radicalProstatectomyPathologyAnnotation.setName("RadicalProstatectomyPathologyAnnotation");
	//		radicalProstatectomyPathologyAnnotation.setParentEntity(prostatePathologyAnnotation);
	//		DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(
	//				radicalProstatectomyPathologyAnnotation, false);
	//
	//		// Create attribute(s) for radicalProstatectomyPathologyAnnotation entity.
	//		final AttributeInterface radicalProstateName = factory.createStringAttribute();
	//		radicalProstateName.setName("radicalProstateName");
	//		((StringAttributeTypeInformation) radicalProstateName.getAttributeTypeInformation())
	//		.setSize(40);
	//
	//		final AttributeInterface radicalProstateType = factory.createStringAttribute();
	//		radicalProstateType.setName("radicalProstateType");
	//		((StringAttributeTypeInformation) radicalProstateType.getAttributeTypeInformation())
	//		.setSize(40);
	//
	//		radicalProstatectomyPathologyAnnotation.addAttribute(radicalProstateName);
	//		radicalProstatectomyPathologyAnnotation.addAttribute(radicalProstateType);
	//
	//		// Create a radicalProstatectomyMargin entity.
	//		final EntityInterface radicalProstatectomyMargin = createAndPopulateEntity();
	//		radicalProstatectomyMargin.setName("RadicalProstatectomyMargin");
	//		radicalProstatectomyMargin.setAbstract(true);
	//
	//		// Create attribute(s) for radicalProstatectomyMargin entity.
	//		final AttributeInterface focality = factory.createStringAttribute();
	//		focality.setName("focality");
	//		((StringAttributeTypeInformation) focality.getAttributeTypeInformation()).setSize(40);
	//
	//		final AttributeInterface marginalStatus = factory.createStringAttribute();
	//		marginalStatus.setName("marginalStatus");
	//		((StringAttributeTypeInformation) marginalStatus.getAttributeTypeInformation()).setSize(40);
	//
	//		radicalProstatectomyMargin.addAbstractAttribute(focality);
	//		radicalProstatectomyMargin.addAbstractAttribute(marginalStatus);
	//
	//		// Associate radicalProstatectomyPathologyAnnotation entity with radicalProstatectomyMargin entity : radicalProstatectomyPathologyAnnotation (1)------ >(*) radicalProstatectomyMargin
	//		final AssociationInterface association2 = factory.createAssociation();
	//		association2
	//		.setName("radicalProstatectomyPathologyAnnotationToRadicalProstatectomyMarginAssociation");
	//		association2.setTargetEntity(radicalProstatectomyMargin);
	//		association2.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
	//
	//		final RoleInterface sourceRole2 = getRole(AssociationType.ASSOCIATION,
	//				"radicalProstatectomyPathologyAnnotation", Cardinality.ONE, Cardinality.ONE);
	//		sourceRole2.setAssociationsType(AssociationType.CONTAINTMENT);
	//
	//		association2.setSourceRole(sourceRole2);
	//		association2.setTargetRole(getRole(AssociationType.ASSOCIATION,
	//				"radicalProstatectomyMargin", Cardinality.ZERO, Cardinality.MANY));
	//
	//		radicalProstatectomyPathologyAnnotation.addAbstractAttribute(association2);
	//		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association2);
	//
	//		// Create a melanomaMargin entity.
	//		final EntityInterface melanomaMargin = createAndPopulateEntity();
	//		melanomaMargin.setName("MelanomaMargin");
	//
	//		// Create attribute(s) for melanomaMargin entity.
	//		final AttributeInterface melanomaMarginName = factory.createStringAttribute();
	//		melanomaMarginName.setName("melanomaMarginName");
	//		((StringAttributeTypeInformation) melanomaMarginName.getAttributeTypeInformation())
	//		.setSize(40);
	//
	//		final AttributeInterface melanomaMarginType = factory.createStringAttribute();
	//		melanomaMarginType.setName("melanomaMarginType");
	//		((StringAttributeTypeInformation) melanomaMarginType.getAttributeTypeInformation())
	//		.setSize(40);
	//
	//		melanomaMargin.addAbstractAttribute(melanomaMarginName);
	//		melanomaMargin.addAbstractAttribute(melanomaMarginType);
	//
	//		// Associate radicalProstatectomyMargin entity with melanomaMargin entity : radicalProstatectomyMargin (1)------ >(*) melanomaMargin
	//		final AssociationInterface association3 = factory.createAssociation();
	//		association3.setName("radicalProstatectomyMarginToMelanomaMarginAssociation");
	//		association3.setTargetEntity(melanomaMargin);
	//		association3.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
	//
	//		final RoleInterface sourceRole3 = getRole(AssociationType.ASSOCIATION,
	//				"radicalProstatectomyMargin", Cardinality.ONE, Cardinality.ONE);
	//		sourceRole3.setAssociationsType(AssociationType.CONTAINTMENT);
	//
	//		association3.setSourceRole(sourceRole3);
	//		association3.setTargetRole(getRole(AssociationType.ASSOCIATION, "melanomaMargin",
	//				Cardinality.ZERO, Cardinality.MANY));
	//
	//		radicalProstatectomyMargin.addAbstractAttribute(association3);
	//		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association3);
	//
	//		// Create containers for baseSolidTissuePathologyAnnotation, prostatePathologyAnnotation,
	//		// gleasonScore, radicalProstatectomyPathologyAnnotation, radicalProstatectomyMargin
	//		//and melanomaMargin entities.
	//		int sequenceNumber = 0;
	//
	//		final ContainerInterface baseSolidTissuePathologyAnnotationEntityContainer = createContainerForEntity(baseSolidTissuePathologyAnnotation);
	//		final ContainerInterface prostatePathologyAnnotationEntityContainer = createContainerForEntity(prostatePathologyAnnotation);
	//		final ContainerInterface gleasonScoreEntityContainer = createContainerForEntity(gleasonScore);
	//		final ContainerInterface radicalProstatectomyPathologyAnnotationrEntityContainer = createContainerForEntity(radicalProstatectomyPathologyAnnotation);
	//		final ContainerInterface radicalProstatectomyMarginEntityContainer = createContainerForEntity(radicalProstatectomyMargin);
	//		final ContainerInterface melanomaMarginEntityContainer = createContainerForEntity(melanomaMargin);
	//
	//		// Create text field controls for attributes baseSolidTissuePathologyAnnotation,
	//		// prostatePathologyAnnotation, gleasonScore, radicalProstatectomyPathologyAnnotation,
	//		// radicalProstatectomyMargin and melanomaMargin entities.
	//		createTextFieldControlForEntity(baseSolidTissuePathologyAnnotationEntityContainer,
	//				baseSolidTissuePathologyAnnotation, sequenceNumber);
	//		sequenceNumber = baseSolidTissuePathologyAnnotationEntityContainer.getControlCollection()
	//		.size();
	//		createTextFieldControlForEntity(prostatePathologyAnnotationEntityContainer,
	//				prostatePathologyAnnotation, sequenceNumber);
	//		sequenceNumber = sequenceNumber
	//		+ prostatePathologyAnnotationEntityContainer.getControlCollection().size();
	//		createTextFieldControlForEntity(gleasonScoreEntityContainer, gleasonScore, sequenceNumber);
	//		sequenceNumber = sequenceNumber + gleasonScoreEntityContainer.getControlCollection().size();
	//		createTextFieldControlForEntity(radicalProstatectomyPathologyAnnotationrEntityContainer,
	//				radicalProstatectomyPathologyAnnotation, sequenceNumber);
	//		sequenceNumber = sequenceNumber
	//		+ radicalProstatectomyPathologyAnnotationrEntityContainer.getControlCollection()
	//		.size();
	//		createTextFieldControlForEntity(radicalProstatectomyMarginEntityContainer,
	//				radicalProstatectomyMargin, sequenceNumber);
	//		sequenceNumber = sequenceNumber
	//		+ radicalProstatectomyMarginEntityContainer.getControlCollection().size();
	//		createTextFieldControlForEntity(melanomaMarginEntityContainer, melanomaMargin,
	//				sequenceNumber);
	//		sequenceNumber = sequenceNumber
	//		+ melanomaMarginEntityContainer.getControlCollection().size();
	//
	//		// Create contaiment controls.
	//		final AbstractContainmentControlInterface containmentControl1 = factory
	//		.createContainmentAssociationControl();
	//		containmentControl1
	//		.setCaption("ProstatePathologyAnnotationToGleasonScoreContainmentControl");
	//		containmentControl1.setContainer(gleasonScoreEntityContainer);
	//		containmentControl1.setBaseAbstractAttribute(association1);
	//		containmentControl1.setSequenceNumber(++sequenceNumber);
	//
	//		containmentControl1
	//		.setParentContainer((Container) prostatePathologyAnnotationEntityContainer);
	//		prostatePathologyAnnotationEntityContainer.addControl(containmentControl1);
	//
	//		final AbstractContainmentControlInterface containmentControl2 = factory
	//		.createContainmentAssociationControl();
	//		containmentControl2
	//		.setCaption("RadicalProstatectomyPathologyAnnotationToRadicalProstatectomyMarginContainmentControl");
	//		containmentControl2.setContainer(radicalProstatectomyMarginEntityContainer);
	//		containmentControl2.setBaseAbstractAttribute(association2);
	//		containmentControl2.setSequenceNumber(++sequenceNumber);
	//
	//		containmentControl2
	//		.setParentContainer((Container) radicalProstatectomyPathologyAnnotationrEntityContainer);
	//		radicalProstatectomyPathologyAnnotationrEntityContainer.addControl(containmentControl2);
	//
	//		final AbstractContainmentControlInterface containmentControl3 = factory
	//		.createContainmentAssociationControl();
	//		containmentControl3
	//		.setCaption("RadicalProstatectomyMarginToMelanomaMarginAssociationContainmentControl");
	//		containmentControl3.setContainer(melanomaMarginEntityContainer);
	//		containmentControl3.setBaseAbstractAttribute(association3);
	//		containmentControl3.setSequenceNumber(++sequenceNumber);
	//
	//		containmentControl3
	//		.setParentContainer((Container) radicalProstatectomyMarginEntityContainer);
	//		radicalProstatectomyMarginEntityContainer.addControl(containmentControl3);
	//
	//		entityGroup.addEntity(baseSolidTissuePathologyAnnotation);
	//		baseSolidTissuePathologyAnnotation.setEntityGroup(entityGroup);
	//		entityGroup.addEntity(prostatePathologyAnnotation);
	//		prostatePathologyAnnotation.setEntityGroup(entityGroup);
	//		entityGroup.addEntity(gleasonScore);
	//		gleasonScore.setEntityGroup(entityGroup);
	//		entityGroup.addEntity(radicalProstatectomyPathologyAnnotation);
	//		radicalProstatectomyPathologyAnnotation.setEntityGroup(entityGroup);
	//		entityGroup.addEntity(radicalProstatectomyMargin);
	//		radicalProstatectomyMargin.setEntityGroup(entityGroup);
	//		entityGroup.addEntity(melanomaMargin);
	//		melanomaMargin.setEntityGroup(entityGroup);
	//
	//		return entityGroup;
	//	}
	//
	//	/**
	//	 * Create entity group from pathology annotation model.
	//	 * Add permissible values to tumourTissueSite attribute.
	//	 * @return EntityGroupInterface
	//	 * @throws DynamicExtensionsSystemException
	//	 */
	//	private EntityGroupInterface createEntityGroup4() throws DynamicExtensionsSystemException
	//	{
	//		final EntityGroupManagerInterface entityGroupManager = EntityGroupManager.getInstance();
	//		final DomainObjectFactory factory = DomainObjectFactory.getInstance();
	//
	//		final EntityGroupInterface entityGroup = factory.createEntityGroup();
	//		entityGroup.setName("Entity Group " + new Double(Math.random()).toString());
	//
	//		// Create a baseSolidTissuePathologyAnnotation entity.
	//		final EntityInterface baseSolidTissuePathologyAnnotation = createAndPopulateEntity();
	//		baseSolidTissuePathologyAnnotation.setName("BaseSolidTissuePathologyAnnotation");
	//		baseSolidTissuePathologyAnnotation.setAbstract(true);
	//
	//		// Create attribute(s) for baseSolidTissuePathologyAnnotation entity.
	//		final AttributeInterface tissueSlide = factory.createStringAttribute();
	//		tissueSlide.setName("tissueSlide");
	//		((StringAttributeTypeInformation) tissueSlide.getAttributeTypeInformation()).setSize(40);
	//
	//		final AttributeInterface tumourTissueSite = factory.createStringAttribute();
	//		tumourTissueSite.setName("tumourTissueSite");
	//		((StringAttributeTypeInformation) tumourTissueSite.getAttributeTypeInformation())
	//		.setSize(40);
	//
	//		baseSolidTissuePathologyAnnotation.addAbstractAttribute(tissueSlide);
	//		baseSolidTissuePathologyAnnotation.addAbstractAttribute(tumourTissueSite);
	//
	//		// Create permissible values for tumourTissueSite.
	//		final UserDefinedDEInterface userDefinedDE = factory.createUserDefinedDE();
	//
	//		final PermissibleValueInterface permissibleValue1 = factory.createStringValue();
	//		((StringValue) permissibleValue1).setValue("Permissible Value 1");
	//
	//		final PermissibleValueInterface permissibleValue2 = factory.createStringValue();
	//		((StringValue) permissibleValue2).setValue("Permissible Value 2");
	//
	//		final PermissibleValueInterface permissibleValue3 = factory.createStringValue();
	//		((StringValue) permissibleValue3).setValue("Permissible Value 3");
	//
	//		final PermissibleValueInterface permissibleValue4 = factory.createStringValue();
	//		((StringValue) permissibleValue4).setValue("Permissible Value 4");
	//
	//		final PermissibleValueInterface permissibleValue5 = factory.createStringValue();
	//		((StringValue) permissibleValue5).setValue("Permissible Value 5");
	//
	//		userDefinedDE.addPermissibleValue(permissibleValue1);
	//		userDefinedDE.addPermissibleValue(permissibleValue2);
	//		userDefinedDE.addPermissibleValue(permissibleValue3);
	//		userDefinedDE.addPermissibleValue(permissibleValue4);
	//		userDefinedDE.addPermissibleValue(permissibleValue5);
	//
	//		final StringAttributeTypeInformation tumourTissueSiteTypeInfo = (StringAttributeTypeInformation) tumourTissueSite
	//		.getAttributeTypeInformation();
	//
	//		tumourTissueSiteTypeInfo.setDataElement(userDefinedDE);
	//
	//		// Create a prostatePathologyAnnotation entity.
	//		final EntityInterface prostatePathologyAnnotation = createAndPopulateEntity();
	//		prostatePathologyAnnotation.setParentEntity(baseSolidTissuePathologyAnnotation);
	//		DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(
	//				prostatePathologyAnnotation, false);
	//		prostatePathologyAnnotation.setName("ProstatePathologyAnnotation");
	//
	//		// Create attribute(s) for prostatePathologyAnnotation entity.
	//		final AttributeInterface seminalVesicleInvasion = factory.createStringAttribute();
	//		seminalVesicleInvasion.setName("seminalVesicleInvasion");
	//		((StringAttributeTypeInformation) seminalVesicleInvasion.getAttributeTypeInformation())
	//		.setSize(40);
	//
	//		final AttributeInterface periprostaticFatInvasion = factory.createStringAttribute();
	//		periprostaticFatInvasion.setName("periprostaticFatInvasion");
	//		((StringAttributeTypeInformation) periprostaticFatInvasion.getAttributeTypeInformation())
	//		.setSize(40);
	//
	//		prostatePathologyAnnotation.addAbstractAttribute(seminalVesicleInvasion);
	//		prostatePathologyAnnotation.addAbstractAttribute(periprostaticFatInvasion);
	//
	//		// Create a gleasonScore entity.
	//		final EntityInterface gleasonScore = createAndPopulateEntity();
	//		gleasonScore.setName("GleasonScore");
	//
	//		// Create attribute(s) for gleasonScore entity.
	//		final AttributeInterface primaryPattern = factory.createStringAttribute();
	//		primaryPattern.setName("primaryPattern");
	//		((StringAttributeTypeInformation) primaryPattern.getAttributeTypeInformation()).setSize(40);
	//
	//		final AttributeInterface secondaryPattern = factory.createStringAttribute();
	//		secondaryPattern.setName("secondaryPattern");
	//		((StringAttributeTypeInformation) secondaryPattern.getAttributeTypeInformation())
	//		.setSize(40);
	//
	//		gleasonScore.addAbstractAttribute(primaryPattern);
	//		gleasonScore.addAbstractAttribute(secondaryPattern);
	//
	//		// Associate prostatePathologyAnnotation entity with gleasonScore entity : prostatePathologyAnnotation (1)------ >(*) gleasonScore
	//		final AssociationInterface association1 = factory.createAssociation();
	//		association1.setName("prostatePathologyAnnotationToGleasonScoreAssociation");
	//		association1.setTargetEntity(gleasonScore);
	//		association1.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
	//
	//		final RoleInterface sourceRole1 = getRole(AssociationType.ASSOCIATION,
	//				"prostatePathologyAnnotation", Cardinality.ONE, Cardinality.ONE);
	//		sourceRole1.setAssociationsType(AssociationType.CONTAINTMENT);
	//
	//		association1.setSourceRole(sourceRole1);
	//		association1.setTargetRole(getRole(AssociationType.ASSOCIATION, "gleasonScore",
	//				Cardinality.ZERO, Cardinality.MANY));
	//
	//		prostatePathologyAnnotation.addAbstractAttribute(association1);
	//		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association1);
	//
	//		// Create a radicalProstatectomyPathologyAnnotation entity.
	//		final EntityInterface radicalProstatectomyPathologyAnnotation = createAndPopulateEntity();
	//		radicalProstatectomyPathologyAnnotation.setName("RadicalProstatectomyPathologyAnnotation");
	//		radicalProstatectomyPathologyAnnotation.setParentEntity(prostatePathologyAnnotation);
	//		DynamicExtensionsUtility.getConstraintKeyPropertiesForInheritance(
	//				radicalProstatectomyPathologyAnnotation, false);
	//
	//		// Create attribute(s) for radicalProstatectomyPathologyAnnotation entity.
	//		final AttributeInterface radicalProstateName = factory.createStringAttribute();
	//		radicalProstateName.setName("radicalProstateName");
	//		((StringAttributeTypeInformation) radicalProstateName.getAttributeTypeInformation())
	//		.setSize(40);
	//
	//		final AttributeInterface radicalProstateType = factory.createStringAttribute();
	//		radicalProstateType.setName("radicalProstateType");
	//		((StringAttributeTypeInformation) radicalProstateType.getAttributeTypeInformation())
	//		.setSize(40);
	//
	//		radicalProstatectomyPathologyAnnotation.addAttribute(radicalProstateName);
	//		radicalProstatectomyPathologyAnnotation.addAttribute(radicalProstateType);
	//
	//		// Create a radicalProstatectomyMargin entity.
	//		final EntityInterface radicalProstatectomyMargin = createAndPopulateEntity();
	//		radicalProstatectomyMargin.setName("RadicalProstatectomyMargin");
	//		radicalProstatectomyMargin.setAbstract(true);
	//
	//		// Create attribute(s) for radicalProstatectomyMargin entity.
	//		final AttributeInterface focality = factory.createStringAttribute();
	//		focality.setName("focality");
	//		((StringAttributeTypeInformation) focality.getAttributeTypeInformation()).setSize(40);
	//
	//		final AttributeInterface marginalStatus = factory.createStringAttribute();
	//		marginalStatus.setName("marginalStatus");
	//		((StringAttributeTypeInformation) marginalStatus.getAttributeTypeInformation()).setSize(40);
	//
	//		radicalProstatectomyMargin.addAbstractAttribute(focality);
	//		radicalProstatectomyMargin.addAbstractAttribute(marginalStatus);
	//
	//		// Associate radicalProstatectomyPathologyAnnotation entity with radicalProstatectomyMargin entity : radicalProstatectomyPathologyAnnotation (1)------ >(*) radicalProstatectomyMargin
	//		final AssociationInterface association2 = factory.createAssociation();
	//		association2
	//		.setName("radicalProstatectomyPathologyAnnotationToRadicalProstatectomyMarginAssociation");
	//		association2.setTargetEntity(radicalProstatectomyMargin);
	//		association2.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
	//
	//		// Create source role for radicalProstatectomyPathologyAnnotation to radicalProstatectomyMargin association.
	//		final RoleInterface sourceRole2 = getRole(AssociationType.ASSOCIATION,
	//				"radicalProstatectomyPathologyAnnotation", Cardinality.ONE, Cardinality.ONE);
	//		sourceRole2.setAssociationsType(AssociationType.CONTAINTMENT);
	//
	//		association2.setSourceRole(sourceRole2);
	//		association2.setTargetRole(getRole(AssociationType.ASSOCIATION,
	//				"radicalProstatectomyMargin", Cardinality.ZERO, Cardinality.MANY));
	//
	//		radicalProstatectomyPathologyAnnotation.addAbstractAttribute(association2);
	//		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association2);
	//
	//		// Create a melanomaMargin entity.
	//		final EntityInterface melanomaMargin = createAndPopulateEntity();
	//		melanomaMargin.setName("MelanomaMargin");
	//
	//		// Create attribute(s) for melanomaMargin entity.
	//		final AttributeInterface melanomaMarginName = factory.createStringAttribute();
	//		melanomaMarginName.setName("melanomaMarginName");
	//		((StringAttributeTypeInformation) melanomaMarginName.getAttributeTypeInformation())
	//		.setSize(40);
	//
	//		final AttributeInterface melanomaMarginType = factory.createStringAttribute();
	//		melanomaMarginType.setName("melanomaMarginType");
	//		((StringAttributeTypeInformation) melanomaMarginType.getAttributeTypeInformation())
	//		.setSize(40);
	//
	//		melanomaMargin.addAbstractAttribute(melanomaMarginName);
	//		melanomaMargin.addAbstractAttribute(melanomaMarginType);
	//
	//		// Associate radicalProstatectomyMargin entity with melanomaMargin entity : radicalProstatectomyMargin (1)------ >(*) melanomaMargin
	//		final AssociationInterface association3 = factory.createAssociation();
	//		association3.setName("radicalProstatectomyMarginToMelanomaMarginAssociation");
	//		association3.setTargetEntity(melanomaMargin);
	//		association3.setAssociationDirection(AssociationDirection.SRC_DESTINATION);
	//
	//		final RoleInterface sourceRole3 = getRole(AssociationType.ASSOCIATION,
	//				"radicalProstatectomyMargin", Cardinality.ONE, Cardinality.ONE);
	//		sourceRole3.setAssociationsType(AssociationType.CONTAINTMENT);
	//
	//		association3.setSourceRole(sourceRole3);
	//		association3.setTargetRole(getRole(AssociationType.ASSOCIATION, "melanomaMargin",
	//				Cardinality.ZERO, Cardinality.MANY));
	//
	//		radicalProstatectomyMargin.addAbstractAttribute(association3);
	//		DynamicExtensionsUtility.getConstraintPropertiesForAssociation(association3);
	//
	//		entityGroup.addEntity(baseSolidTissuePathologyAnnotation);
	//		baseSolidTissuePathologyAnnotation.setEntityGroup(entityGroup);
	//		entityGroup.addEntity(prostatePathologyAnnotation);
	//		prostatePathologyAnnotation.setEntityGroup(entityGroup);
	//		entityGroup.addEntity(gleasonScore);
	//		gleasonScore.setEntityGroup(entityGroup);
	//		entityGroup.addEntity(radicalProstatectomyPathologyAnnotation);
	//		radicalProstatectomyPathologyAnnotation.setEntityGroup(entityGroup);
	//		entityGroup.addEntity(radicalProstatectomyMargin);
	//		radicalProstatectomyMargin.setEntityGroup(entityGroup);
	//		entityGroup.addEntity(melanomaMargin);
	//		melanomaMargin.setEntityGroup(entityGroup);
	//
	//		try
	//		{
	//			// Save the entity group.
	//			entityGroupManager.persistEntityGroup(entityGroup);
	//		}
	//		catch (final DynamicExtensionsSystemException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//		catch (final DynamicExtensionsApplicationException e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//
	//		return entityGroup;
	//	}
	//
	//	/**
	//	 *
	//	 * @param categoryEntity
	//	 * @return ContainerInterface
	//	 */
	//	private ContainerInterface createContainer(final AbstractEntityInterface abstractEntity)
	//	{
	//		final ContainerInterface container = DomainObjectFactory.getInstance().createContainer();
	//		container.setCaption(abstractEntity.getName() + "_container");
	//		container.setAbstractEntity(abstractEntity);
	//		container.setMainTableCss("formRequiredLabel");
	//		container.setRequiredFieldIndicatior("*");
	//		container.setRequiredFieldWarningMessage("indicates mandatory fields.");
	//		abstractEntity.addContainer(container);
	//
	//		return container;
	//	}
	//
	//	/**
	//	 *
	//	 * @param categoryAttribute
	//	 * @param sequenceNumber
	//	 * @return
	//	 */
	//	private TextFieldInterface createTextFieldControl(
	//			final BaseAbstractAttributeInterface baseAbstractAttribute, final int sequenceNumber)
	//	{
	//		final TextFieldInterface textFieldInterface = DomainObjectFactory.getInstance().createTextField();
	//		textFieldInterface.setCaption(baseAbstractAttribute.getName());
	//		textFieldInterface.setBaseAbstractAttribute(baseAbstractAttribute);
	//		textFieldInterface.setColumns(50);
	//		textFieldInterface.setSequenceNumber(sequenceNumber);
	//
	//		return textFieldInterface;
	//	}
	//
	//	/**
	//	 *
	//	 * @param categoryAttribute
	//	 * @param sequenceNumber
	//	 * @return ComboBoxInterface
	//	 */
	//	private ComboBoxInterface createComboBoxControl(final CategoryAttributeInterface categoryAttribute,
	//			final int sequenceNumber)
	//	{
	//		final ComboBoxInterface comboBox = DomainObjectFactory.getInstance().createComboBox();
	//		comboBox.setCaption(categoryAttribute.getName());
	//		comboBox.setBaseAbstractAttribute(categoryAttribute);
	//		comboBox.setTooltip(categoryAttribute.getName());
	//		return comboBox;
	//	}
	//
	//	/**
	//	 *
	//	 * @param entity
	//	 * @param sequenceNumber
	//	 * @return
	//	 */
	//	private ContainerInterface createContainerForEntity(final EntityInterface entity)
	//	{
	//		final ContainerInterface container = createContainer(entity);
	//		return container;
	//	}
	//
	//	/**
	//	 *
	//	 * @param container
	//	 * @param entity
	//	 * @param sequenceNumber
	//	 */
	//	private void createTextFieldControlForEntity(final ContainerInterface container,
	//			final EntityInterface entity, int sequenceNumber)
	//	{
	//		for (final AttributeInterface attribute : entity.getAttributeCollection())
	//		{
	//			final TextFieldInterface textField = createTextFieldControl(attribute, ++sequenceNumber);
	//			textField.setParentContainer((Container) container);
	//			container.addControl(textField);
	//		}
	//	}

	/**
	 * use case: Display of controls in a single line for the attributes of the same class
	 */
	public void testSingleLineDisplay1()
	{
		CategoryInterface category = null;
		try
		{
			importModel(XMI_FILE_PATH + "scg.xmi", CSV_FILE_PATH + "SCG.csv",
			"edu.wustl.catissuecore.domain.PathAnnotation_SCG");

			createCaegory(CSV_FILE_PATH);

			final CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
			category = (CategoryInterface) categoryManager.getObjectByName(
					Category.class.getName(), "singleLineDisplaySameClass1");

			assertNotNull(category.getId());

		}
		catch (final Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	//	/**
	//	 * use case: Single line display controls validations
	//	 * Two controls on the sinlge line should of the same type
	//	 */
	//	public void testSingleLineDisplay2()
	//	{
	//		CategoryInterface category = null;
	//		try
	//		{
	//			importModel(XMI_FILE_PATH + "scg.xmi", CSV_FILE_PATH + "SCG.csv",
	//			"edu.wustl.catissuecore.domain.PathAnnotation_SCG");
	//
	//			createCaegory(CSV_FILE_PATH);
	//
	//			final CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
	//			category = (CategoryInterface) categoryManager.getObjectByName(
	//					Category.class.getName(), "singleLineDisplaySameClass2");
	//			assertNotNull(category.getId());
	//
	//		}
	//		catch (final Exception e)
	//		{
	//			fail();
	//			e.printStackTrace();
	//
	//		}
	//	}
	//
	//	/**
	//	 * use case: Single line display controls validations
	//	 * Below category has follwing combinations of controls in single line
	//	 * 1. combo/text
	//	 * 2. text/text
	//	 * 3. combo/combo
	//	 * Two controls on the sinlge line should of the same type
	//	 */
	//	public void testSingleLineDisplayValidControlTypes1()
	//	{
	//		CategoryInterface category = null;
	//		try
	//		{
	//			importModel(XMI_FILE_PATH + "scg.xmi", CSV_FILE_PATH + "SCG.csv",
	//			"edu.wustl.catissuecore.domain.PathAnnotation_SCG");
	//
	//			createCaegory(CSV_FILE_PATH);
	//
	//			final CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
	//			category = (CategoryInterface) categoryManager.getObjectByName(
	//					Category.class.getName(), "singleLineDisplaySameClass4");
	//			assertNotNull(category.getId());
	//
	//		}
	//		catch (final Exception e)
	//		{
	//			fail();
	//			e.printStackTrace();
	//
	//		}
	//	}
	//
	//	/**
	//	 * use case: Single line display controls validations
	//	 * Below category has follwing combinations of controls in single line
	//	 * 1. list/combo
	//	 * 2. list/list
	//	 * 3. list/text
	//	 * Two controls on the sinlge line should of the same type
	//	 */
	//	public void testSingleLineDisplayValidControlTypes2()
	//	{
	//		CategoryInterface category = null;
	//		try
	//		{
	//			importModel(XMI_FILE_PATH + "scg.xmi", CSV_FILE_PATH + "SCG.csv",
	//			"edu.wustl.catissuecore.domain.PathAnnotation_SCG");
	//
	//			createCaegory(CSV_FILE_PATH + "singleLineDsiplaySameClass5.csv");
	//
	//			final CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
	//			category = (CategoryInterface) categoryManager.getObjectByName(
	//					Category.class.getName(), "singleLineDisplaySameClass5");
	//			assertNotNull(category.getId());
	//
	//		}
	//		catch (final Exception e)
	//		{
	//			fail();
	//			e.printStackTrace();
	//
	//		}
	//	}
	//
	//	/**
	//	 * use case: Single line display controls validations
	//	 * Two controls on the sinlge line should of the same type
	//	 * (Now it is supported) so changed the test case & no error will be thrown.
	//	 */
	//	public void testSingleLineDisplay3()
	//	{
	//		try
	//		{
	//			importModel(XMI_FILE_PATH + "scg.xmi", CSV_FILE_PATH + "SCG.csv",
	//			"edu.wustl.catissuecore.domain.PathAnnotation_SCG");
	//
	//			createCaegory(CSV_FILE_PATH + "singleLineDsiplaySameClass3.csv");
	//
	//			final CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
	//			(CategoryInterface) categoryManager.getObjectByName(
	//					Category.class.getName(), "singleLineDisplaySameClass3");
	//
	//
	//		}
	//		catch (final Exception e)
	//		{
	//			fail();
	//			e.printStackTrace();
	//		}
	//	}
	//
	//	/**
	//	 * Use case: Single line display for the attibutes of the differnt class
	//	 */
	//	public void testSingleLineDisplayDiffrentClass1()
	//	{
	//		CategoryInterface category = null;
	//		try
	//		{
	//			importModel(XMI_FILE_PATH + "scg.xmi", CSV_FILE_PATH + "SCG.csv",
	//			"edu.wustl.catissuecore.domain.PathAnnotation_SCG");
	//
	//			createCaegory(CSV_FILE_PATH + "singleLineDsiplayDifferentClassl.csv");
	//
	//			final CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
	//			category = (CategoryInterface) categoryManager.getObjectByName(
	//					Category.class.getName(), "singleLineDisplayDifferentClass1");
	//			assertNotNull(category.getId());
	//
	//		}
	//		catch (final Exception e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Negative Use case: Single line display for the attibutes of the differnt class
	//	 * subcategory used has multiple entities under same diplay label and
	//	 * show=false
	//	 */
	//	public void testSingleLineDisplayDiffrentClass2()
	//	{
	//		try
	//		{
	//			importModel(XMI_FILE_PATH + "scg.xmi", CSV_FILE_PATH + "SCG.csv",
	//			"edu.wustl.catissuecore.domain.PathAnnotation_SCG");
	//
	//			createCaegory(CSV_FILE_PATH + "singleLineDsiplayDifferentClass2.csv");
	//
	//			final CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
	//			(CategoryInterface) categoryManager.getObjectByName(
	//					Category.class.getName(), "singleLineDisplayDifferentClass2");
	//
	//		}
	//		catch (final Exception e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//
	//		}
	//	}
	//
	//	/**
	//	 * Populate the map with values for data entry.
	//	 * @param category
	//	 * @return
	//	 */
	//	private Map<BaseAbstractAttributeInterface, Object> populateMap(final CategoryInterface category)
	//	{
	//		// category association : Annotations[1] to PastMedicalHistory[1]
	//		final String catAssociationMain = "Annotations[1]Annotations[1] to Annotations[1]PastMedicalHistory[1] category association";
	//
	//		// category association : PastMedicalHistory[1] to LabAnnotation[1] category association
	//		final String catAssociation1 = "Annotations[1]PastMedicalHistory[1] to Annotations[1]LabAnnotation[1] category association";
	//
	//		// category association : PastMedicalHistory[1] to LabAnnotation[2] category association
	//		final String catAssociation2 = "Annotations[1]PastMedicalHistory[1] to Annotations[1]LabAnnotation[2] category association";
	//
	//		// category association : PastMedicalHistory[1] to LabAnnotation[3] category association
	//		final String catAssociation3 = "Annotations[1]PastMedicalHistory[1] to Annotations[1]LabAnnotation[3] category association";
	//
	//		// category association : PastMedicalHistory[1] to LabAnnotation[3] category association
	//		final String catAssociation4 = "Annotations[1]PastMedicalHistory[1] to Annotations[1]HealthExaminationAnnotation[1] category association";
	//
	//		// category entity Annotations[1]Annotations[1]
	//		final String categoryEntAnnotation = "Annotations[1]Annotations[1]";
	//
	//		// category entity Annotations[1]PastMedicalHistory[1]
	//		final String categoryEntMain = "Annotations[1]PastMedicalHistory[1]";
	//
	//		// category entity Annotations[1]LabAnnotation[1]
	//		final String categoryEnt1 = "Annotations[1]LabAnnotation[1]";
	//
	//		// category entity Annotations[1]LabAnnotation[2]
	//		final String categoryEnt2 = "Annotations[1]LabAnnotation[2]";
	//
	//		// category entity Annotations[1]LabAnnotation[3]
	//		final String categoryEnt3 = "Annotations[1]LabAnnotation[3]";
	//
	//		// category entity Annotations[1]HealthExaminationAnnotation[1]
	//		final String categoryEnt4 = "Annotations[1]HealthExaminationAnnotation[1]";
	//
	//		final Map<BaseAbstractAttributeInterface, Object> attributeValues = new HashMap<BaseAbstractAttributeInterface, Object>();
	//		final List<Map<BaseAbstractAttributeInterface, Object>> mainValueMaps = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
	//
	//		// retrieve category entity of the category entity 'Annotations[1]LabAnnotation[1]'
	//		final CategoryEntityInterface catEntAnnotation = category
	//		.getCategoryEntityByName(categoryEntAnnotation);
	//
	//		// retrieve category entity of the category entity 'Annotations[1]LabAnnotation[1]'
	//		final CategoryEntityInterface catEntMain = category.getCategoryEntityByName(categoryEntMain);
	//		final CategoryAssociationInterface catAssoMain = catEntAnnotation
	//		.getAssociationByName(catAssociationMain);
	//		final CategoryAttributeInterface catAttr1 = catEntMain
	//		.getAttributeByName("comment Category Attribute");
	//		final CategoryAttributeInterface catAttr2 = catEntMain
	//		.getAttributeByName("dateApproximate Category Attribute");
	//		final CategoryAttributeInterface catAttr3 = catEntMain
	//		.getAttributeByName("dateOfDiagnosis Category Attribute");
	//		final CategoryAttributeInterface catAttr4 = catEntMain
	//		.getAttributeByName("laterality Category Attribute");
	//		final CategoryAttributeInterface catAttr5 = catEntMain
	//		.getAttributeByName("otherDiagnosis Category Attribute");
	//		final CategoryAttributeInterface catAttr6 = catEntMain
	//		.getAttributeByName("diagnosis Category Attribute");
	//
	//		// retrieve category entity of the category entity 'Annotations[1]LabAnnotation[1]'
	//		final CategoryEntityInterface catEnt1 = category.getCategoryEntityByName(categoryEnt1);
	//		final CategoryAssociationInterface catAsso1 = catEntMain.getAssociationByName(catAssociation1);
	//		final CategoryAttributeInterface catAttr7 = catEnt1
	//		.getAttributeByName("quantitativeResult Category Attribute");
	//
	//		// retrieve category entity of the category entity 'Annotations[1]LabAnnotation[2]'
	//		final CategoryEntityInterface catEnt2 = category.getCategoryEntityByName(categoryEnt2);
	//		final CategoryAssociationInterface catAsso2 = catEntMain.getAssociationByName(catAssociation2);
	//		final CategoryAttributeInterface catAttr8 = catEnt2
	//		.getAttributeByName("quantitativeResult Category Attribute");
	//
	//		// retrieve category entity of the category entity 'Annotations[1]LabAnnotation[2]'
	//		final CategoryEntityInterface catEnt3 = category.getCategoryEntityByName(categoryEnt3);
	//		final CategoryAssociationInterface catAsso3 = catEntMain.getAssociationByName(catAssociation3);
	//		final CategoryAttributeInterface catAttr9 = catEnt3
	//		.getAttributeByName("quantitativeResult Category Attribute");
	//
	//		// retrieve category entity of the category entity 'Annotations[1]LabAnnotation[2]'
	//		final CategoryEntityInterface catEnt4 = category.getCategoryEntityByName(categoryEnt4);
	//		final CategoryAssociationInterface catAsso4 = catEntMain.getAssociationByName(catAssociation4);
	//		final CategoryAttributeInterface catAttr10 = catEnt4
	//		.getAttributeByName("nameOfProcedure Category Attribute");
	//		final CategoryAttributeInterface catAttr11 = catEnt4
	//		.getAttributeByName("otherProcedure Category Attribute");
	//
	//		final Map<BaseAbstractAttributeInterface, Object> valueMap1 = new HashMap<BaseAbstractAttributeInterface, Object>();
	//		valueMap1.put(catAttr7, "22");
	//		final List<Map<BaseAbstractAttributeInterface, Object>> list1 = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
	//		list1.add(valueMap1);
	//
	//		final Map<BaseAbstractAttributeInterface, Object> valueMap2 = new HashMap<BaseAbstractAttributeInterface, Object>();
	//		valueMap2.put(catAttr8, "11");
	//		final List<Map<BaseAbstractAttributeInterface, Object>> list2 = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
	//		list2.add(valueMap2);
	//
	//		final Map<BaseAbstractAttributeInterface, Object> valueMap3 = new HashMap<BaseAbstractAttributeInterface, Object>();
	//		valueMap3.put(catAttr9, "33");
	//		final List<Map<BaseAbstractAttributeInterface, Object>> list3 = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
	//		list3.add(valueMap3);
	//
	//		final Map<BaseAbstractAttributeInterface, Object> valueMap4 = new HashMap<BaseAbstractAttributeInterface, Object>();
	//		valueMap4.put(catAttr10, "nameOfProcedureTC");
	//		valueMap4.put(catAttr11, "otherProcedureTC");
	//		final List<Map<BaseAbstractAttributeInterface, Object>> list4 = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
	//		list4.add(valueMap4);
	//
	//		final Map<BaseAbstractAttributeInterface, Object> valueMapMain = new HashMap<BaseAbstractAttributeInterface, Object>();
	//
	//		// put all category attributes of the category entity 'PastMedicalHistory[1]' in data value map
	//		valueMapMain.put(catAttr1, "CommentTC");
	//		valueMapMain.put(catAttr2, "1");
	//		valueMapMain.put(catAttr3, "07"+ProcessorConstants.DATE_SEPARATOR+"01"+ProcessorConstants.DATE_SEPARATOR+"2009");
	//		valueMapMain.put(catAttr4, "LeftTC");
	//		valueMapMain.put(catAttr5, "123");
	//		valueMapMain.put(catAttr6, "HydronephrosisTC");
	//
	//		valueMapMain.put(catAsso1, list1);
	//		valueMapMain.put(catAsso2, list2);
	//		valueMapMain.put(catAsso3, list3);
	//		valueMapMain.put(catAsso4, list4);
	//
	//		mainValueMaps.add(valueMapMain);
	//
	//		// put all values in the main map
	//		attributeValues.put(catAssoMain, mainValueMaps);
	//
	//		return attributeValues;
	//	}
	//
	//	/**
	//	 * Create category with one of the attribute specified with allowfuturedate rule.
	//	 * Check whether this rule is present for the created category attribute
	//	 */
	//	public void testFutureDateRule()
	//	{
	//		CategoryInterface category = null;
	//		try
	//		{
	//			importModel(XMI_FILE_PATH + "test_date.xmi", CSV_FILE_PATH + "test_date.csv", "TestAnnotations");
	//
	//			createCaegory(CSV_FILE_PATH + "categoryFutureDate.csv");
	//
	//			final CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
	//			category = (CategoryInterface) categoryManager.getObjectByName(
	//					Category.class.getName(), "futuredate_cat");
	//
	//			assertNotNull(category.getId());
	//			final CategoryEntityInterface rootCategoryEntity = category.getRootCategoryElement();
	//
	//			final CategoryAttributeInterface categoryAttribute = rootCategoryEntity
	//			.getAttributeByName("visitDate Category Attribute");
	//			final Collection<RuleInterface> ruleCollection = categoryAttribute.getRuleCollection();
	//			if (ruleCollection.isEmpty())
	//			{
	//				fail();
	//			}
	//			boolean isFutureDateRulePresent = false;
	//			for (final RuleInterface rule : ruleCollection)
	//			{
	//				if ("allowfuturedate".equalsIgnoreCase(rule.getName()))
	//				{
	//					isFutureDateRulePresent = true;
	//				}
	//
	//			}
	//			assertTrue(isFutureDateRulePresent);
	//
	//		}
	//		catch (final Exception e)
	//		{
	//			e.printStackTrace();
	//		}
	//	}
	//
	//	/**
	//	 * Negative usecase :-Create category having date type attribute specified with "allowfuturedate" and "daterange" rules.
	//	 * Error message must thrown for these conflicting rules
	//	 */
	//	public void testForConflictingDateRule()
	//	{
	//		CategoryInterface category = null;
	//		try
	//		{
	//			final CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
	//			try
	//			{
	//				importModel(XMI_FILE_PATH + "test_date.xmi", CSV_FILE_PATH + "test_date.csv", "TestAnnotations");
	//				createCaegory(CSV_FILE_PATH + "categoryFutureDate1.csv");
	//				category = (CategoryInterface) categoryManager.getObjectByName(Category.class
	//						.getName(), "Test category - future_date");
	//			}
	//			catch (final Exception e)
	//			{
	//				Logger.out.info("Could not create category due to conficting rules....");
	//			}
	//
	//			assertNull(category);
	//		}
	//		catch (final Exception e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
	//
	//	/**
	//	 * Negative usecase :-Create category having date type attribute having allowfuturedate at model level.
	//	 * Error message must thrown for these conflicting rules
	//	 */
	//	public void testForOverridingDateRule()
	//	{
	//		CategoryInterface category = null;
	//		try
	//		{
	//			final CategoryManager categoryManager = (CategoryManager) CategoryManager.getInstance();
	//			try
	//			{
	//				importModel(XMI_FILE_PATH + "test_date.xmi", CSV_FILE_PATH + "test_date.csv", "TestAnnotations");
	//				createCaegory(CSV_FILE_PATH + "categoryFutureDate3.csv");
	//				category = (CategoryInterface) categoryManager.getObjectByName(Category.class
	//						.getName(), "Category_Lab Information");
	//			}
	//			catch (final Exception e1)
	//			{
	//				Logger.out
	//				.info("Could not create category due to overriding allowfuturedate rule....");
	//			}
	//
	//			assertNull(category);
	//		}
	//		catch (final Exception e)
	//		{
	//			e.printStackTrace();
	//			fail();
	//		}
	//	}
}