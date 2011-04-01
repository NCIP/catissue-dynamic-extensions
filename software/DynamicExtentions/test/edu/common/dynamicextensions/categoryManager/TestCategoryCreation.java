/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author
 *@version 1.0
 */

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.common.dynamicextensions.client.CategoryClient;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domaininterface.StaticCategoryInterface;
import edu.common.dynamicextensions.entitymanager.StaticCategoryManager;
import edu.common.dynamicextensions.entitymanager.StaticCategoryManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.parser.CategoryFileParser;
import edu.common.dynamicextensions.util.parser.StaticCategoryGenerator;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;

public class TestCategoryCreation extends DynamicExtensionsBaseTestCase
{

	private final String CATEGORY_FILE_DIR = "CPUML";

	/**
	 * This test case will create all the categories present in the CPUML Folder.
	 * If one of the category creation is failed then this test case is also failed.
	 */
	public void testCreateCategory1()
	{
		try
		{
			String[] args = {CATEGORY_FILE_DIR, APPLICATIONURL, TEST_MODEL_DIR + "/cat_list_for_test_edited1.txt"};
			CategoryClient.main(args);
			System.out.println("done categoryCreation");
			assertAllCategoriesCreatedInFile(TEST_MODEL_DIR + "/cat_list_for_test_edited1.txt", false);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Unknown exception occured - " + e.getMessage());
		}
	}

	/**
	 * This test case will create all the categories present in the CPUML Folder.
	 * If one of the category creation is failed then this test case is also failed.
	 */
	public void testCreateCategory2()
	{
		try
		{
			String[] args = {CATEGORY_FILE_DIR, APPLICATIONURL, TEST_MODEL_DIR + "/cat_list_for_test_edited2.txt"};
			CategoryClient.main(args);
			System.out.println("done categoryCreation");
			assertAllCategoriesCreatedInFile(TEST_MODEL_DIR + "/cat_list_for_test_edited2.txt", false);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Unknown exception occured - " + e.getMessage());
		}
	}

	/**
	 * This test case will create all the categories present in the CPUML Folder.
	 * If one of the category creation is failed then this test case is also failed.
	 */
	public void testCreateCategory3()
	{
		try
		{
			String[] args = {CATEGORY_FILE_DIR, APPLICATIONURL, TEST_MODEL_DIR + "/cat_list_for_test_edited3.txt"};
			CategoryClient.main(args);
			System.out.println("done categoryCreation");
			assertAllCategoriesCreatedInFile(TEST_MODEL_DIR + "/cat_list_for_test_edited3.txt", false);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Unknown exception occured - " + e.getMessage());
		}
	}

	/**
	 * This test case will create all the categories present in the CPUML Folder.
	 * If one of the category creation is failed then this test case is also failed.
	 */
	public void testCreateNegativeCategory()
	{
		try
		{
			String[] args = {CATEGORY_FILE_DIR, APPLICATIONURL, TEST_MODEL_DIR + "/cat_list_for_test_edited4.txt"};
			CategoryClient.main(args);
			System.out.println("done categoryCreation");
			assertAllCategoriesCreatedInFile(TEST_MODEL_DIR + "/cat_list_for_test_edited4.txt", true);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Unknown exception occured - " + e.getMessage());
		}
	}

	/**
	 * This method will find out the names of the categories specified in the fileName & verify all these
	 * categories are created.
	 * @param fileName
	 * @param isNegativeTest TODO
	 * @throws IOException
	 */
	private void assertAllCategoriesCreatedInFile(String fileName, boolean isNegativeTest) throws IOException
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
			if (isNegativeTest)
			{
				assertFailedCategoriesFromFiles(catFiles);
			}
			else
			{
				assertCategoriesFromFiles(catFiles);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Unknown exception occured - " + e.getMessage());
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
		Set<String> successFulCategories = new HashSet<String>();
		Set<String> FailedCategories = new HashSet<String>();
		for (String name : catNameInTestCase)
		{
			boolean isPresent = false;
			for (String cat : categoryNameInDB)
			{
				if (cat.equals(name))
				{
					successFulCategories.add(name);
					isPresent = true;

				}
			}
			if (!isPresent)
			{
				FailedCategories.add(name);
			}
		}
		printCategoryCreationReport(successFulCategories, FailedCategories);
		if (!FailedCategories.isEmpty())
		{
			//System.out.println("Following categores has failed - ");
			fail("One or more category imports failed. hence failing testcase");
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
	private void assertFailedCategoriesFromFiles(Collection<String> catFiles)
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
		Set<String> successFulCategories = new HashSet<String>();
		Set<String> FailedCategories = new HashSet<String>();
		for (String name : catNameInTestCase)
		{
			boolean isPresent = false;
			for (String cat : categoryNameInDB)
			{
				if (cat.equals(name))
				{
					successFulCategories.add(name);
					isPresent = true;

				}
			}
			if (!isPresent)
			{
				FailedCategories.add(name);
			}
		}
		printCategoryCreationReport(successFulCategories, FailedCategories);
		if (!successFulCategories.isEmpty())
		{
			System.out.println("Following negative categores are imported succesfully - ");
			for (String catName : successFulCategories)
			{
				System.out.println(catName);
			}
			fail("One or more negative category imports is successful. Hence failing testcase");
		}
	}

	private void printCategoryCreationReport(Set<String> successFulCategories,
			Set<String> failedCategories)
	{
		System.out.println("Categories created successfully for following category names ");
		for (String name : successFulCategories)
		{
			System.out.println(name);
		}
		System.out.println("=========== successful category creation list over =================");
		System.out.println();
		System.out.println("Categories creation failed for following category names ");
		for (String name : failedCategories)
		{
			System.out.println(name);
		}
		System.out.println("=========== falied category creation list over =================");

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
	 * This test case will create the static category present in the XML Folder & specified in the patientContactInfo.xml.
	 * If one of the category creation is failed then this test case is also failed.
	 */
	public void testStaticCategoryGenerator()
	{
		try
		{
			String[] args = {"src/resources/xml/patientContactInfo.xml"};
			StaticCategoryGenerator.main(args);
			System.out.println("Static category generator done.");
			StaticCategoryManagerInterface staticCategoryManager = StaticCategoryManager
					.getInstance();
			StaticCategoryInterface categoryInterface = staticCategoryManager
					.getStaticCategoryByName("Static Form 2");
			assertNotNull(categoryInterface);
			assertEquals(categoryInterface.getName(), "Static Form 2");
			assertNotNull(categoryInterface.getId());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Unknown exception occured - " + e.getMessage());
		}
	}


}
