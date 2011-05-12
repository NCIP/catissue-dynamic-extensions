
package edu.common.dynamicextensions.categoryManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.common.dynamicextensions.client.CategoryMetadataClient;
import edu.common.dynamicextensions.client.DataEditClient;
import edu.common.dynamicextensions.client.DataEntryClient;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.DataElementInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.Variables;
import edu.common.dynamicextensions.validation.ValidatorUtil;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

/**
 *
 * @author mandar_shidhore
 *
 */
public class TestCategoryManager extends DynamicExtensionsBaseTestCase
{

	static
	{
		Variables.jbossUrl = APPLICATIONURLFORWAR;
	}

	/**
	 * This test case will create the csv file which
	 * contains the Conatiner name & container identifier's used for bulk operations.
	 * Test case will fail if metadata file does not exists after completing.
	 */
	public void testCreateCategoryMetadataFile()
	{
		try
		{
			String[] args = {RESOURCE_DIR_PATH + "categoryNamesMetadata.txt", APPLICATIONURL};

			CategoryMetadataClient.main(args);
			System.out.println("done category metadata Creation");
			File recievedFile = new File("catMetadataFile.csv");
			if (!recievedFile.exists())
			{
				fail("catMetadataFile.csv does not exist.");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	public void testEditDataForSingleCategories()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Chemotherapy");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	/**
	 * This test case will try to insert the data for Test Category_Lab Information category.
	 */
	public void testInsertDataForCategoryChemotherapy()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Chemotherapy");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}
	}

	public void testValidateDataForSingleCategories()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Chemotherapy");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCategoryRadiotherapy()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Test Category Radiation Therapy");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}
	}

	public void testEditDataForForCategoryRadiotherapy()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Test Category Radiation Therapy");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForCategoryRadiotherapy()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Test Category Radiation Therapy");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCategoryLabInfo()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Lab Information");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForForCategoryLabInfo()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Lab Information");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForCategoryLabInfo()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Lab Information");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCategoryDiagnosis()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Diagnosis");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}
	}

	public void testEditDataForForCategoryDiagnosis()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Diagnosis");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForCategoryDiagnosis()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Diagnosis");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCategoryAnnotation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Annotations");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}
	}

	public void testEditDataForForCategoryAnnotation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Annotations");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForCategoryAnnotation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Annotations");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCategoryPathAnnotation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Test Category_Pathological Annotation");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForForPathAnnotation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Test Category_Pathological Annotation");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForPathAnnotation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Test Category_Pathological Annotation");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCategoryAutocompleteDropDown()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test AutoComplete multiselect");
			assertNotNull(category);
			insertDataForCategory(category);
			System.out.println("Data inserted sucessfully for Test AutoComplete multiselect"
					+ sessionDataBean.getFirstName());
			//Audit Insert data
			assertAudit("test.annotations.CollectionAttributeClasshospitals%");

		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForForAutocompleteDropDown()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test AutoComplete multiselect");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForAutocompleteDropDown()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test AutoComplete multiselect");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidatePVsWithApostrophe()
	{
		try
		{
			Set<String> desiredPermissibleValues = new HashSet<String>();
			desiredPermissibleValues.add(DynamicExtensionsUtility
					.getEscapedStringValue("Prems' Eye Clinic"));
			desiredPermissibleValues.add(DynamicExtensionsUtility
					.getEscapedStringValue("Jeevan's Jyoti Nursing Home"));
			desiredPermissibleValues.add(DynamicExtensionsUtility
					.getEscapedStringValue("St. John's Medical College Hospital"));
			desiredPermissibleValues.add(DynamicExtensionsUtility
					.getEscapedStringValue("St. Martha's Hospital"));

			EntityManagerInterface entityManager = EntityManager.getInstance();

			EntityInterface entity = entityManager.getEntityByIdentifier(entityManager.getEntityId(
					"PhysicianInformation", EntityCache.getInstance().getEntityGroupByName("test")
							.getId()));
			if (entity != null)
			{
				AttributeInterface attribute = entity.getAttributeByName("hospitals");
				if (attribute != null)
				{
					DataElementInterface dataElement = attribute.getAttributeTypeInformation()
							.getDataElement();
					if (dataElement instanceof UserDefinedDEInterface)
					{
						UserDefinedDEInterface userDefinedDEInterface = (UserDefinedDEInterface) dataElement;
						for (PermissibleValueInterface pv : userDefinedDEInterface
								.getPermissibleValues())
						{
							if (desiredPermissibleValues.contains(pv.getValueAsObject().toString()))
							{
								Logger.out
										.info("Permissible value with apostropy is present in model.");
								break;
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			Logger.out.info("Cannot find any Permissible value with apostropy in model.");
			fail();
		}

	}

	public void testInsertDataForSingleLineDisplayForAutomation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Category Single Line For Automation");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForForSingleLineDisplayForAutomation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Category Single Line For Automation");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForDisplayForAutomation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Category Single Line For Automation");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCalculatedAttributeForAutomation1()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Calculated Attributes For Automation");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForCalculatedAttributeForAutomation1()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Calculated Attributes For Automation");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForCalculatedAttributeForAutomation1()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Calculated Attributes For Automation");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForSkipLogicForAutomation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Skip logic for Automation");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForSkipLogicForAutomation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Skip logic for Automation");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForSkipLogicForAutomation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Skip logic for Automation");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForSkipLogicForAutomation2()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Skip logic for Automation 2");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForSkipLogicForAutomation2()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Skip logic for Automation 2");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForSkipLogicForForAutomation2()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Skip logic for Automation 2");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForSkipLogicForAutomation3()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Skip logic for Automation 3");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForSkipLogicForAutomation3()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Skip logic for Automation 3");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForSkipLogicForForAutomation3()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Skip logic for Automation 3");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCalculated_multiline_different_classes()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Multiline subcategory calculated attributes from different classes");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForCalculated_multiline_different_classes()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Multiline subcategory calculated attributes from different classes");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForCalculated_multiline_different_classes()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Multiline subcategory calculated attributes from different classes");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCalculated_multiline_different_classes_invisible_RA()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache
					.getInstance()
					.getCategoryByName(
							"Multiline subcategory calculated attributes from different classes invisible RA");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForCalculated_multiline_different_classes_invisible_RA()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache
					.getInstance()
					.getCategoryByName(
							"Multiline subcategory calculated attributes from different classes invisible RA");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForCalculated_multiline_different_classes_invisible_RA()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache
					.getInstance()
					.getCategoryByName(
							"Multiline subcategory calculated attributes from different classes invisible RA");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCalculated_multiline_different_classes_simple_formula()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache
					.getInstance()
					.getCategoryByName(
							"Multiline subcategory simple formula calculated attributes from different classes");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForCalculated_multiline_different_classes_simple_formula()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache
					.getInstance()
					.getCategoryByName(
							"Multiline subcategory simple formula calculated attributes from different classes");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForCalculated_multiline_different_classes_simple_formula()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache
					.getInstance()
					.getCategoryByName(
							"Multiline subcategory simple formula calculated attributes from different classes");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCalculated_multiline_different_classes_visible_RA()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache
					.getInstance()
					.getCategoryByName(
							"Multiline subcategory calculated attributes from different classes visible RA");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForCalculated_multiline_different_classes_visible_RA()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache
					.getInstance()
					.getCategoryByName(
							"Multiline subcategory calculated attributes from different classes visible RA");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForCalculated_multiline_different_classes_visible_RA()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache
					.getInstance()
					.getCategoryByName(
							"Multiline subcategory calculated attributes from different classes visible RA");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCalculated_multiline_same_class()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Multiline simple 2");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForCalculated_multiline_same_class()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Multiline simple 2");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForCalculated_multiline_same_class()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Multiline simple 2");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCalculated_multiline_same_class_invisible_RA()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Multiline subcategory calculated attributes from same class invisible RA");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForCalculated_multiline_same_class_invisible_RA()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Multiline subcategory calculated attributes from same class invisible RA");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForCalculated_multiline_same_class_invisible_RA()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Multiline subcategory calculated attributes from same class invisible RA");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCalculated_multiline_same_class_simple_formula()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Multiline subcategory simple formula calculated attributes from same class");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForCalculated_multiline_same_class_simple_formula()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Multiline subcategory simple formula calculated attributes from same class");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForCalculated_multiline_same_class_simple_formula()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Multiline subcategory simple formula calculated attributes from same class");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCalculated_multiline_same_class_visible_RA()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Multiline subcategory calculated attributes from same class visible RA");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForCalculated_multiline_same_class_visible_RA()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Multiline subcategory calculated attributes from same class visible RA");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForCalculated_multiline_same_class_visible_RA()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Multiline subcategory calculated attributes from same class visible RA");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCategoryLabReport()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Lab Report");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForCategoryLabReport()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Lab Report");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForCategoryLabReport()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Lab Report");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCategoryClinicalReport()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Category_Clinical Reports");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForCategoryClinicalReport()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Category_Clinical Reports");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForCategoryClinicalReport()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Category_Clinical Reports");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCategoryPathReports()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Category_Pathology Reports");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForCategoryCategoryPathReports()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Category_Pathology Reports");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForCategoryCategoryPathReports()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Category_Pathology Reports");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCategoryLabReportforAutomation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Test Category_Lab Report for Automation");
			assertNotNull(category);
			insertDataForCategory(category);
			assertAudit("%_FILE_NAME");
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForCategoryLabReportforAutomation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Test Category_Lab Report for Automation");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForCategoryLabReportforAutomation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Test Category_Lab Report for Automation");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCategoryCalculated_MultipleTimes()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Calculated attribute multiple times");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForCategoryCalculated_MultipleTimes()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Calculated attribute multiple times");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataForCategoryCalculated_MultipleTimes()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Calculated attribute multiple times");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCategoryFormMultiSelectAddDetails()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Form Multiselect Add Details");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForCategoryFormMultiSelectAddDetails()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Form Multiselect Add Details");
			assertNotNull(category);
			Long recordId = insertDataForCategory(category);
			assertNotNull(recordId);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataFormMultiSelectAddDetails()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Form Multiselect Add Details");
			assertNotNull(category);
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCategoryConfigurePasteNegative()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Test Category_Negative Case for Paste");
			assertNull(
					"testInsertDataForCategoryConfigurePasteNegative: Expecting null as category Test Category_Negative Case for Paste should not have been created.",
					category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}

	public void testEditDataForCategoryConfigurePasteNegative()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("c");
			assertNull(
					"testInsertDataForCategoryConfigurePasteNegative: Expecting null as category Test Category_Negative Case for Paste should not have been created.",
					category);

		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}

	public void testValidateDataFormCategoryConfigurePasteNegative()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Test Category_Negative Case for Paste");
			assertNull(
					"testInsertDataForCategoryConfigurePasteNegative: Expecting null as category Test Category_Negative Case for Paste should not have been created.",
					category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
		}

	}

	public void testInsertDataForCategoryConfigurePaste()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Test Category_Paste Button Configuration");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForCategoryConfigurePaste()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Test Category_Paste Button Configuration");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataFormCategoryConfigurePaste()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Test Category_Paste Button Configuration");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			// FIXME - Getting the validation message - error --> TestDate edited must be of equal to or lesser than 12-12-2010.
			e.printStackTrace();
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCategory_TestCase79()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("TestCase79");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForCategory_TestCase79()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("TestCase79");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataFormCategory_TestCase79()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("TestCase79");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCategory_TestCase80()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("TestCase80");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForCategory_TestCase80()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("TestCase80");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataFormCategory_TestCase80()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("TestCase80");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	public void testInsertDataForCategory_TestCase81()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("TestCase81");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testEditDataForCategory_TestCase81()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("TestCase81");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category, recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	public void testValidateDataFormCategory_TestCase81()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("TestCase81");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
			fail();
		}

	}

	private Long insertDataForCategory(CategoryInterface category) throws ParseException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		System.out.println("Inserting record for " + category.getName());

		CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
		Map<BaseAbstractAttributeInterface, Object> dataValue = mapGenerator
				.createDataValueMapForCategory(rootCatEntity, 0);
		Long recordIdentifier = null;

		ContainerInterface containerInterface = (ContainerInterface) category
				.getRootCategoryElement().getContainerCollection().toArray()[0];
		String entityGroupName = containerInterface.getAbstractEntity().getEntityGroup().getName();

		Map<String, Object> clientmap = new HashMap<String, Object>();
		DataEntryClient dataEntryClient = new DataEntryClient();
		clientmap.put(WebUIManagerConstants.RECORD_ID, recordIdentifier);
		clientmap.put(WebUIManagerConstants.SESSION_DATA_BEAN, sessionDataBean);
		clientmap.put(WebUIManagerConstants.USER_ID, null);
		clientmap.put(WebUIManagerConstants.CONTAINER, containerInterface);
		clientmap.put(WebUIManagerConstants.DATA_VALUE_MAP, dataValue);
		try
		{
			dataEntryClient.setServerUrl(new URL(Variables.jbossUrl + entityGroupName + "/"));
		}
		catch (MalformedURLException e)
		{
			throw new DynamicExtensionsApplicationException("Invalid URL:" + Variables.jbossUrl
					+ entityGroupName + "/");
		}
		dataEntryClient.setParamaterObjectMap(clientmap);
		dataEntryClient.execute(null);

		recordIdentifier = (Long) dataEntryClient.getObject();

		assertNotNull(recordIdentifier);

		System.out.println("Record inserted succesfully for " + category.getName() + " RecordId "
				+ recordIdentifier);
		return recordIdentifier;
	}

	private void printFailedCategoryReport(Map<CategoryInterface, Exception> failedCatVsException,
			String message)
	{
		for (Entry<CategoryInterface, Exception> entryObject : failedCatVsException.entrySet())
		{
			CategoryInterface category = entryObject.getKey();
			Exception exception = entryObject.getValue();
			System.out.println(message + category.getName());
			System.out.println("Exception :");
			exception.printStackTrace();
		}
		if (!failedCatVsException.isEmpty())
		{
			fail();
		}

	}

	/**
	 * This test case will try to generate the html for each of the category in present in the DB in Edit mode.
	 * Test case is failed if the exception is occured in generating the html for any of the Category.
	 *
	 */
	public void testGenerateHtmlForContainerInEditMode()
	{
		Map<CategoryInterface, Exception> failedCatVsException = new HashMap<CategoryInterface, Exception>();

		List<CategoryInterface> categoryList = getAllCategories();
		for (CategoryInterface category : categoryList)
		{
			try
			{
				for (Object container : category.getRootCategoryElement().getContainerCollection())
				{
					((ContainerInterface) container).generateContainerHTML(category.getName(),
							WebUIManagerConstants.EDIT_MODE);
				}
			}
			catch (Exception e)
			{
				System.out.println("Html generation failed for Category " + category.getName());
				failedCatVsException.put(category, e);
			}
		}
		printFailedCategoryReport(failedCatVsException, "Html generation failed for Category ");

	}

	/**
	 * This test case will try to generate the html for each of the category in present in the DB in view mode.
	 * Test case is failed if the exception is occured in generating the html for any of the Category.
	 *
	 */
	public void testGenerateHtmlForContainerInViewMode()
	{
		Map<CategoryInterface, Exception> failedCatVsException = new HashMap<CategoryInterface, Exception>();
		List<CategoryInterface> categoryList = getAllCategories();
		for (CategoryInterface category : categoryList)
		{
			try
			{

				for (Object container : category.getRootCategoryElement().getContainerCollection())
				{
					((ContainerInterface) container).generateContainerHTML(category.getName(),
							WebUIManagerConstants.VIEW_MODE);
				}
			}
			catch (Exception e)
			{
				System.out.println("Html generation failed for Category " + category.getName());
				failedCatVsException.put(category, e);
			}
		}
		printFailedCategoryReport(failedCatVsException, "Html generation failed for Category ");
	}

	public void testValidateDataForCategorie(CategoryInterface category)
			throws DynamicExtensionsSystemException, ParseException
	{
		System.out.println("Validating record for " + category.getName());
		Map<BaseAbstractAttributeInterface, Object> dataValue;
		CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
		dataValue = mapGenerator.createDataValueMapForCategory(rootCatEntity, 0);
		List<String> errorList = new ArrayList<String>();
		ValidatorUtil.validateEntity(dataValue, errorList, (ContainerInterface) rootCatEntity
				.getContainerCollection().iterator().next(), true);
		if (errorList.isEmpty())
		{
			System.out.println("Record validated succesfully for category " + category.getName());
		}
		else
		{
			System.out.println("Record validation failed for category " + category.getName());
			for (String error : errorList)
			{
				System.out.println("error --> " + error);
			}
			fail("Record validation failed for category" + category.getName());
		}
	}

	/**
	 * This method will retrieve the category with the given name from DB.
	 * @param name name of the category
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 */
	private CategoryInterface retriveCategoryByName(String name)
			throws DynamicExtensionsSystemException, DAOException
	{
		List<CategoryInterface> categoryList = getAllCategories();
		CategoryInterface categoryByName = null;
		for (CategoryInterface category : categoryList)
		{
			if (category.getName().equals(name))
			{
				categoryByName = category;
				break;
			}
		}
		if (categoryByName == null)
		{
			throw new DynamicExtensionsSystemException("Category with name " + name + " not found");
		}
		return categoryByName;
	}

	/**
	 * This method will retrieve all the categories in the DB.
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 */
	private List<CategoryInterface> getAllCategories()
	{
		HibernateDAO hibernateDAO;
		List<CategoryInterface> categoryList = null;
		try
		{
			hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();

			categoryList = hibernateDAO.retrieve(CategoryInterface.class.getName());
			DynamicExtensionsUtility.closeDAO(hibernateDAO);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
		return categoryList;
	}

	/*public void testInsertDataForAllCategoriesForBO()
	{
		Map<CategoryInterface, Exception> failedCatVsException = new HashMap<CategoryInterface, Exception>();
		CategoryManagerInterface categoryManager = CategoryManager.getInstance();
		List<CategoryInterface> categoryList = getAllCategories();
		for (CategoryInterface category : categoryList)
		{
			try
			{
				System.out.println("testInsertDataForAllCategoriesForBO:Inserting record for "
						+ category.getName());
				Map<Long, Object> dataValue;
				CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
				dataValue = mapGenerator.createIdToValueMapForCategory(rootCatEntity, 0);
				long recordId = categoryManager.insertData(category, dataValue);
				System.out
						.println("testInsertDataForAllCategoriesForBO: Record inserted succesfully for "
								+ category.getName() + " RecordId " + recordId);
			}
			catch (Exception e)
			{
				System.out
						.println("testInsertDataForAllCategoriesForBO: Record Insertion failed for Category "
								+ category.getName());
				failedCatVsException.put(category, e);
			}
		}
		printFailedCategoryReport(failedCatVsException,
				"testInsertDataForAllCategoriesForBO: Record Insertion failed for Category ");
	}*/
	public void editDataForCategory(CategoryInterface category, Long recordIdentifier)
			throws MalformedURLException
	{
		try
		{
			ContainerInterface container = (ContainerInterface) category.getRootCategoryElement()
					.getContainerCollection().toArray()[0];
			CategoryManagerInterface categoryManager = CategoryManager.getInstance();
			CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
			Map<BaseAbstractAttributeInterface, Object> dataValue = mapGenerator
					.createDataValueMapForCategory(rootCatEntity, 0);
			Map<BaseAbstractAttributeInterface, Object> editedDataValue = categoryManager
					.getRecordById(rootCatEntity, recordIdentifier);
			mapGenerator.validateRetrievedDataValueMap(editedDataValue, dataValue);
			String entityGroupName = container.getAbstractEntity().getEntityGroup().getName();
			Map<String, Object> clientmap = new HashMap<String, Object>();
			DataEditClient dataEditClient = new DataEditClient();
			clientmap.put(WebUIManagerConstants.RECORD_ID, recordIdentifier);
			clientmap.put(WebUIManagerConstants.SESSION_DATA_BEAN, sessionDataBean);
			clientmap.put(WebUIManagerConstants.USER_ID, null);
			clientmap.put(WebUIManagerConstants.CONTAINER, container);
			clientmap.put(WebUIManagerConstants.DATA_VALUE_MAP, editedDataValue);
			dataEditClient.setServerUrl(new URL(Variables.jbossUrl + entityGroupName + "/"));
			assertTrue("Jboss url is " + Variables.jbossUrl, true);
			dataEditClient.setParamaterObjectMap(clientmap);
			dataEditClient.execute(null);
			System.out.println("Record Edited succesfully for " + category.getName() + " RecordId "
					+ recordIdentifier);
			editedDataValue = categoryManager.getRecordById(rootCatEntity, recordIdentifier);
			mapGenerator.validateRetrievedDataValueMap(editedDataValue, dataValue);
			assertTrue("Record edited sucessfully for categoey " + category.getName(), true);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
			fail();
		}

	}

	private void assertAudit(String elementName) throws DAOException,
			DynamicExtensionsSystemException, SQLException
	{
		JDBCDAO jdbcDao = null;
		ResultSet resultSet = null;
		try
		{
			String selectQuery = "Select * from CATISSUE_AUDIT_EVENT_DETAILS where ELEMENT_NAME like ?";
			final LinkedList<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
			queryDataList.add(new ColumnValueBean("ELEMENT_NAME", elementName));
			jdbcDao = DynamicExtensionsUtility.getJDBCDAO(null);
			resultSet = jdbcDao.getResultSet(selectQuery, queryDataList, null);

			if (resultSet.next())
			{
				System.out.println("Data Audited sucessfully.");
			}
			else
			{
				fail();
			}
		}
		finally
		{
			jdbcDao.closeStatement(resultSet);
			DynamicExtensionsUtility.closeDAO(jdbcDao);
		}

	}

}