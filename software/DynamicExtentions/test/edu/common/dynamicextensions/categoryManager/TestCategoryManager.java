
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
import java.util.List;

import edu.common.dynamicextensions.category.CategoryCreator;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.CategoryGenerationUtil;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.parser.CategoryFileParser;
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
	private final String APPLICATIONURL = "http://10.88.199.44:28080/dynamicExtensions";
	private final String TEST_MODEL_DIR = "CPUML/TestModels/TestModel_withTags/edited";

	public void testCreateCategory1()
	{
		try
		{
			String[] args = {CATEGORY_FILE_DIR, APPLICATIONURL};//, TEST_MODEL_DIR + "/catNames.txt"};
			CategoryCreator.main(args);
			assertAllCategoriesCreatedInDir(CATEGORY_FILE_DIR);
			System.out.println("done categoryCreation");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

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

	private void assertAllCategoriesCreatedInDir(String catDir)
	{
		try
		{
			List<String> catFiles = CategoryGenerationUtil.getCategoryFileListInDirectory(new File(
					catDir), "");
			assertCategoriesFromFiles(catFiles);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

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

	private void assertCategoriesFromFiles(Collection<String> catFiles)
			throws DynamicExtensionsSystemException, FileNotFoundException, IOException,
			DAOException, SQLException
	{
		List<String> catNameInTestCase = new ArrayList<String>();
		for (String line : catFiles)
		{
			String catName;
			CategoryFileParser categoryFileParser = DomainObjectFactory.getInstance()
					.createCategoryFileParser(line, "");
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

	private Collection<String> getSavedCategoryNames() throws DAOException, SQLException
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
				DynamicExtensionsUtility.closeJDBCDAO(jdbcdao);
			}
		}
		return categoryNameCollection;
	}
}