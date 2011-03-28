
package edu.common.dynamicextensions.categoryManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.common.dynamicextensions.client.CategoryMetadataClient;
import edu.common.dynamicextensions.client.DataEditClient;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.Variables;
import edu.common.dynamicextensions.validation.ValidatorUtil;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;

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
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
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
		}
	}
	public void testValidateDataForSingleCategories()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Chemotherapy");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}
	}
	public void testEditDataForForCategoryRadiotherapy()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category Radiation Therapy");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForCategoryRadiotherapy()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category Radiation Therapy");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}

	}
	public void testEditDataForForCategoryLabInfo()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Lab Information");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForCategoryLabInfo()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Lab Information");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}
	}
	public void testEditDataForForCategoryDiagnosis()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Diagnosis");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForCategoryDiagnosis()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Diagnosis");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}
	}
	public void testEditDataForForCategoryAnnotation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Annotations");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForCategoryAnnotation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Annotations");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}

	}
	public void testEditDataForForPathAnnotation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Pathological Annotation");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForPathAnnotation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Pathological Annotation");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testEditDataForForAutocompleteDropDown()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test AutoComplete multiselect");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForAutocompleteDropDown()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test AutoComplete multiselect");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}

	}
	public void testEditDataForForSingleLineDisplayForAutomation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Category Single Line For Automation");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForDisplayForAutomation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Category Single Line For Automation");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}

	}
	public void testEditDataForCalculatedAttributeForAutomation1()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Calculated Attributes For Automation");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForCalculatedAttributeForAutomation1()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Calculated Attributes For Automation");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}

	}
	public void testEditDataForSkipLogicForAutomation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Skip logic for Automation");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForSkipLogicForAutomation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Skip logic for Automation");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}

	}
	public void testEditDataForSkipLogicForAutomation2()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Skip logic for Automation 2");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForSkipLogicForForAutomation2()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Skip logic for Automation 2");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}

	}
	public void testEditDataForSkipLogicForAutomation3()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Skip logic for Automation 3");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForSkipLogicForForAutomation3()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Skip logic for Automation 3");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}

	}
	public void testEditDataForCalculated_multiline_different_classes()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Multiline subcategory calculated attributes from different classes");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForCalculated_multiline_different_classes()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Multiline subcategory calculated attributes from different classes");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}

	}
	public void testEditDataForCalculated_multiline_different_classes_invisible_RA()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Multiline subcategory calculated attributes from different classes invisible RA");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForCalculated_multiline_different_classes_invisible_RA()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Multiline subcategory calculated attributes from different classes invisible RA");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}

	}
	public void testEditDataForCalculated_multiline_different_classes_simple_formula()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Multiline subcategory simple formula calculated attributes from different classes");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForCalculated_multiline_different_classes_simple_formula()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Multiline subcategory simple formula calculated attributes from different classes");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}

	}
	public void testEditDataForCalculated_multiline_different_classes_visible_RA()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Multiline subcategory calculated attributes from different classes visible RA");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForCalculated_multiline_different_classes_visible_RA()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Multiline subcategory calculated attributes from different classes visible RA");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}

	}
	public void testEditDataForCalculated_multiline_same_class()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Multiline simple 2");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForCalculated_multiline_same_class()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Multiline simple 2");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}

	}
	public void testEditDataForCalculated_multiline_same_class_invisible_RA()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Multiline subcategory calculated attributes from same class invisible RA");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForCalculated_multiline_same_class_invisible_RA()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Multiline subcategory calculated attributes from same class invisible RA");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}

	}
	public void testEditDataForCalculated_multiline_same_class_simple_formula()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Multiline subcategory simple formula calculated attributes from same class");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForCalculated_multiline_same_class_simple_formula()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Multiline subcategory simple formula calculated attributes from same class");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}

	}
	public void testEditDataForCalculated_multiline_same_class_visible_RA()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Multiline subcategory calculated attributes from same class visible RA");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForCalculated_multiline_same_class_visible_RA()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Multiline subcategory calculated attributes from same class visible RA");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}

	}
	public void testEditDataForCategoryLabReport()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Lab Report");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForCategoryLabReport()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Lab Report");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}

	}
	public void testEditDataForCategoryClinicalReport()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Category_Clinical Reports");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForCategoryClinicalReport()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Category_Clinical Reports");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}

	}
	public void testEditDataForCategoryCategoryPathReports()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Category_Pathology Reports");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForCategoryCategoryPathReports()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Category_Pathology Reports");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testEditDataForCategoryLabReportforAutomation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Lab Report for Automation");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForCategoryLabReportforAutomation()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Lab Report for Automation");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}

	}
	public void testEditDataForCategoryCalculated_MultipleTimes()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Calculated attribute multiple times");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataForCategoryCalculated_MultipleTimes()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Calculated attribute multiple times");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
		}

	}
	public void testEditDataForCategoryFormMultiSelectAddDetails()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Form Multiselect Add Details");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataFormMultiSelectAddDetails()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Form Multiselect Add Details");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
		}

	}
	public void testInsertDataForCategoryforautomationofbugs()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Automation form for bugs");
			assertNotNull(category);
			insertDataForCategory(category);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testEditDataForCategoryforautomationofbugs()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Automation form for bugs");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataFormCategoryforautomationofbugs()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Automation form for bugs");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
		}

	}
	public void testInsertDataForCategoryConfigurePasteNegative()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName(
					"Test Category_Negative Case for Paste");
			assertNotNull(category);
			insertDataForCategory(category);
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
			category = EntityCache.getInstance().getCategoryByName("Test Category_Negative Case for Paste");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
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
			category = EntityCache.getInstance().getCategoryByName("Test Category_Negative Case for Paste");
			testValidateDataForCategorie(category);
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
		}

	}
	public void testEditDataForCategoryConfigurePaste()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Paste Button Configuration");
			Long recordId = insertDataForCategory(category);
			System.out.println("Record inserted succesfully for " + category.getName()
					+ " RecordId " + recordId);
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}
	public void testValidateDataFormCategoryConfigurePaste()
	{
		CategoryInterface category = null;
		try
		{
			category = EntityCache.getInstance().getCategoryByName("Test Category_Paste Button Configuration");
			testValidateDataForCategorie(category);
		}
		catch (Exception e)
		{
			System.out.println("Record validation failed for Category " + category.getName());
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
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
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
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
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
			editDataForCategory(category,recordId);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
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
		}

	}
	private Long insertDataForCategory(CategoryInterface category) throws ParseException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		System.out.println("Inserting record for " + category.getName());

		CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
		Map<BaseAbstractAttributeInterface, Object> dataValue = mapGenerator
				.createDataValueMapForCategory(rootCatEntity, 0);
		Long recordId = null;

		ContainerInterface containerInterface = (ContainerInterface) category
				.getRootCategoryElement().getContainerCollection().toArray()[0];
		recordId = DynamicExtensionsUtility.insertDataUtility(recordId, containerInterface,
				dataValue);

		System.out.println("Record inserted succesfully for " + category.getName() + " RecordId "
				+ recordId);
		return recordId;
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
	public void testValidateDataForCategorie(CategoryInterface category) throws DynamicExtensionsSystemException, ParseException
	{
		System.out.println("Validating record for " + category.getName());
		Map<BaseAbstractAttributeInterface, Object> dataValue;
		CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
		dataValue = mapGenerator.createDataValueMapForCategory(rootCatEntity, 0);
		List<String> errorList = new ArrayList<String>();
		ValidatorUtil.validateEntity(dataValue, errorList,
				(ContainerInterface) rootCatEntity.getContainerCollection().iterator()
						.next(), true);
		if (errorList.isEmpty())
		{
			System.out.println("Record validated succesfully for category "
					+ category.getName());
		}
		else
		{
			System.out.println("Record validation failed for category "
					+ category.getName());
			for (String error : errorList)
			{
				System.out.println("error --> " + error);
			}
			fail("Record validation failed for category"+category.getName());
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
	public void editDataForCategory(CategoryInterface category,Long recordIdentifier) throws MalformedURLException
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
			clientmap.put(WebUIManagerConstants.SESSION_DATA_BEAN, null);
			clientmap.put(WebUIManagerConstants.USER_ID, null);
			clientmap.put(WebUIManagerConstants.CONTAINER, container);
			clientmap.put(WebUIManagerConstants.DATA_VALUE_MAP, editedDataValue);
			dataEditClient.setServerUrl(new URL(Variables.jbossUrl + entityGroupName + "/"));
			dataEditClient.setParamaterObjectMap(clientmap);
			dataEditClient.execute(null);
			System.out.println("Record Edited succesfully for " + category.getName() + " RecordId "
					+ recordIdentifier);
			editedDataValue = categoryManager.getRecordById(rootCatEntity, recordIdentifier);
			mapGenerator.validateRetrievedDataValueMap(editedDataValue, dataValue);
		}
		catch (Exception e)
		{
			System.out.println("Record Insertion failed for Category " + category.getName());
		}

	}

}