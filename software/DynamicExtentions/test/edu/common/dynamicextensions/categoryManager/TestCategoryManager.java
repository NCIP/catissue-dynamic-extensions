
package edu.common.dynamicextensions.categoryManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.common.dynamicextensions.category.CategoryCreator;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.CategoryGenerationUtil;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.parser.CategoryFileParser;
import edu.common.dynamicextensions.validation.ValidatorUtil;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;

/**
 *
 * @author mandar_shidhore
 *
 */
public class TestCategoryManager extends DynamicExtensionsBaseTestCase
{

	private final String CATEGORY_FILE_DIR = "CPUML";
	private final String TEST_MODEL_DIR = "CPUML/TestModels/TestModel_withTags/edited";

	/**
	 * This test case will create all the categories present in the CPUML Folder.
	 * If one of the category creation is failed then this test case is also failed.
	 */
	public void testCreateCategory1()
	{
		try
		{
			String[] args = {CATEGORY_FILE_DIR, APPLICATIONURL};//, TEST_MODEL_DIR + "/catNames.txt"};
			CategoryCreator.main(args);
			System.out.println("done categoryCreation");
			assertAllCategoriesCreatedInDir(CATEGORY_FILE_DIR);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * This test case will create the categories present in the CPUML Folder & specified in the catNames2.txt.
	 * If one of the category creation is failed then this test case is also failed.
	 */
	public void testCreateCategory2()
	{
		try
		{
			String[] args = {CATEGORY_FILE_DIR, APPLICATIONURL, TEST_MODEL_DIR + "/catNames2.txt"};
			CategoryCreator.main(args);
			System.out.println("done categoryCreation");
			assertAllCategoriesCreatedInFile(TEST_MODEL_DIR + "/catNames2.txt");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * This method will find out the names of the categories present in the given catDir & will verify
	 * wether that categories are saved in DB or not.
	 * @param catDir category directory.
	 */
	private void assertAllCategoriesCreatedInDir(String catDir)
	{
		try
		{
			List<String> catFiles = CategoryGenerationUtil.getCategoryFileListInDirectory(new File(
					catDir), catDir + File.separator);
			assertCategoriesFromFiles(catFiles);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * This method will find out the names of the categories specified in the fileName & verify all these
	 * categories are created.
	 * @param fileName
	 * @throws IOException
	 */
	private void assertAllCategoriesCreatedInFile(String fileName) throws IOException
	{
		File objFile = new File(fileName);
		BufferedReader bufRdr = null;
		Collection<String> catFiles = new ArrayList<String>();;

		try
		{
			if (objFile.exists())
			{

				bufRdr = new BufferedReader(new FileReader(objFile));
				String line = bufRdr.readLine();
				//read each line of text file
				while (line != null)
				{
					catFiles.add(line);
					line = bufRdr.readLine();
				}
			}
			assertCategoriesFromFiles(catFiles);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
			//throw new DynamicExtensionsSystemException("Can not read from file ", e);
		}
		finally
		{
			if (bufRdr != null)
			{
				bufRdr.close();
			}
		}

	}

	/**
	 * This method will very that the category name given in the catFiles file is also present in the
	 * Db. if not present then the test case is failed.
	 * @param catFiles
	 * @throws DynamicExtensionsSystemException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws DAOException
	 * @throws SQLException
	 */
	private void assertCategoriesFromFiles(Collection<String> catFiles)
			throws DynamicExtensionsSystemException, FileNotFoundException, IOException,
			DAOException, SQLException
	{
		List<String> catNameInTestCase = new ArrayList<String>();
		for (String line : catFiles)
		{
			String catName;
			CategoryFileParser categoryFileParser = DomainObjectFactory.getInstance()
					.createCategoryFileParser(line, "", null);
			categoryFileParser.readNext();
			if (categoryFileParser.hasFormDefination())
			{
				categoryFileParser.readNext();
				catName = categoryFileParser.getCategoryName();
				catNameInTestCase.add(catName);
			}

		}
		Collection<String> categoryNameInDB = getSavedCategoryNames();

		boolean isFailed = false;
		for (String name : catNameInTestCase)
		{
			boolean isPresent = false;
			for (String cat : categoryNameInDB)
			{
				if (cat.equals(name))
				{
					isPresent = true;
					System.out.println("category :" + name + " \t Successfull.");
				}
			}
			if (!isPresent)
			{
				isFailed = true;
				System.out.println("category :" + name + " \t Failed");
			}
		}
		if (isFailed)
		{
			fail();
		}
	}

	/**
	 * This method finds out the names of all the categories present in the db .
	 * @return
	 * @throws DAOException
	 * @throws SQLException
	 * @throws DynamicExtensionsSystemException
	 */
	private Collection<String> getSavedCategoryNames() throws DAOException, SQLException,
			DynamicExtensionsSystemException
	{
		Collection<String> categoryNameCollection = new ArrayList<String>();
		String catNameSql = " select name from DYEXTN_ABSTRACT_METADATA where identifier in (select identifier from dyextn_category)";
		JDBCDAO jdbcdao = null;
		ResultSet resultSet = null;
		try
		{
			jdbcdao = DynamicExtensionsUtility.getJDBCDAO();
			resultSet = jdbcdao.getResultSet(catNameSql, null, null);
			while (resultSet.next())
			{
				categoryNameCollection.add(resultSet.getString(1));
			}
		}
		finally
		{
			if (jdbcdao != null)

			{
				jdbcdao.closeStatement(resultSet);
				DynamicExtensionsUtility.closeDAO(jdbcdao);
			}
		}
		return categoryNameCollection;
	}

	/**
	 * step 1 : This method will first insert the data for Test Category_Chemotherapy Category .
	 * step 2: retrieve the inserted record.
	 * step 3: edit some data & then try to edit the data.
	 */
	public void testEditDataForForAllCategories()
	{
		CategoryManagerInterface categoryManager = CategoryManager.getInstance();
		Map<CategoryInterface, Exception> failedCatVsException = new HashMap<CategoryInterface, Exception>();
		List<CategoryInterface> categoryList = getAllCategories();
		for (CategoryInterface category : categoryList)
		{
			try
			{
				System.out.println("Inserting record for " + category.getName());
				CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
				Map<BaseAbstractAttributeInterface, Object> dataValue = mapGenerator
						.createDataValueMapForCategory(rootCatEntity, 0);

				Long recordId = categoryManager.insertData(category, dataValue);
				System.out.println("Record inserted succesfully for " + category.getName()
						+ " RecordId " + recordId);
				Map<BaseAbstractAttributeInterface, Object> editedDataValue = categoryManager
						.getRecordById(rootCatEntity, recordId);
				mapGenerator.validateRetrievedDataValueMap(editedDataValue, dataValue);
				categoryManager.editData(rootCatEntity, editedDataValue, recordId);
				System.out.println("Record Edited succesfully for " + category.getName()
						+ " RecordId " + recordId);
				editedDataValue = categoryManager.getRecordById(rootCatEntity, recordId);
				mapGenerator.validateRetrievedDataValueMap(editedDataValue, dataValue);
			}
			catch (Exception e)
			{
				System.out.println("Record Insertion failed for Category " + category.getName());
				failedCatVsException.put(category, e);
			}
		}
		printFailedCategoryReport(failedCatVsException, "Record Insertion failed for Category ");

	}

	/**
	 * This test case will try to insert the data for Test Category_Lab Information category.
	 */
	public void testInsertDataForAllCategories()
	{
		Map<CategoryInterface, Exception> failedCatVsException = new HashMap<CategoryInterface, Exception>();
		CategoryManagerInterface categoryManager = CategoryManager.getInstance();
		List<CategoryInterface> categoryList = getAllCategories();
		for (CategoryInterface category : categoryList)
		{
			try
			{
				System.out.println("Inserting record for " + category.getName());
				Map<BaseAbstractAttributeInterface, Object> dataValue;
				CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
				dataValue = mapGenerator.createDataValueMapForCategory(rootCatEntity, 0);
				long recordId = categoryManager.insertData(category, dataValue);
				System.out.println("Record inserted succesfully for " + category.getName()
						+ " RecordId " + recordId);
			}
			catch (Exception e)
			{
				System.out.println("Record Insertion failed for Category " + category.getName());
				failedCatVsException.put(category, e);
			}
		}
		printFailedCategoryReport(failedCatVsException, "Record Insertion failed for Category ");
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

	/**
	 * This test case will try to validate the data for all categories present in DB.
	 */
	public void testValidateDataForAllCategories()
	{
		Map<CategoryInterface, Exception> failedCatVsException = new HashMap<CategoryInterface, Exception>();
		boolean isValidationFailed = false;
		List<CategoryInterface> categoryList = getAllCategories();
		for (CategoryInterface category : categoryList)
		{
			try
			{

				System.out.println("Validating record for " + category.getName());
				Map<BaseAbstractAttributeInterface, Object> dataValue;
				CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
				dataValue = mapGenerator.createDataValueMapForCategory(rootCatEntity, 0);
				List<String> errorList = new ArrayList<String>();
				ValidatorUtil.validateEntity(dataValue, errorList,
						(ContainerInterface) rootCatEntity.getContainerCollection().iterator()
								.next());
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
					isValidationFailed = true;
				}
			}
			catch (Exception e)
			{
				System.out.println("Record validation failed for category " + category.getName());
				failedCatVsException.put(category, e);
			}
		}
		printFailedCategoryReport(failedCatVsException, "Record validation failed for category ");
		if (isValidationFailed)
		{
			fail("Record validation failed for category");
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

}