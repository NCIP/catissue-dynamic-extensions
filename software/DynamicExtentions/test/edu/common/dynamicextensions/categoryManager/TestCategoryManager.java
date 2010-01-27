
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

import edu.common.dynamicextensions.category.CategoryCreator;
import edu.common.dynamicextensions.domain.BooleanAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.StringAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.NumericTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.CategoryGenerationUtil;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.parser.CategoryCSVConstants;
import edu.common.dynamicextensions.util.parser.CategoryFileParser;
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
			assertAllCategoriesCreatedInDir(CATEGORY_FILE_DIR);
			System.out.println("done categoryCreation");
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
					catDir), "");
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
	public void testEditDataForTestChemotheropyCat()
	{
		CategoryManagerInterface categoryManager = CategoryManager.getInstance();
		try
		{

			CategoryInterface category = retriveCategoryByName("Test Category_Chemotherapy");
			Map<BaseAbstractAttributeInterface, Object> dataValue;
			CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
			dataValue = createDataValueMap(rootCatEntity);
			Long recordId = categoryManager.insertData(category, dataValue);
			dataValue = categoryManager.getRecordById(rootCatEntity, recordId);
			categoryManager.editData(rootCatEntity, dataValue, recordId);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * This test case will try to insert the data for Test Category_Lab Information category.
	 */
	public void testInsertDataForTestLabInfoCat()
	{
		CategoryManagerInterface categoryManager = CategoryManager.getInstance();
		try
		{

			CategoryInterface category = retriveCategoryByName("Test Category_Lab Information");
			Map<BaseAbstractAttributeInterface, Object> dataValue;
			CategoryEntityInterface rootCatEntity = category.getRootCategoryElement();
			dataValue = createDataValueMap(rootCatEntity);
			categoryManager.insertData(category, dataValue);
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
		try
		{

			List<CategoryInterface> categoryList = getAllCategories();
			for (CategoryInterface category : categoryList)
			{
				for (Object container : category.getRootCategoryElement().getContainerCollection())
				{
					((ContainerInterface) container).generateContainerHTML(category.getName(),
							WebUIManagerConstants.EDIT_MODE);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * This test case will try to generate the html for each of the category in present in the DB in view mode.
	 * Test case is failed if the exception is occured in generating the html for any of the Category.
	 *
	 */
	public void testGenerateHtmlForContainerInViewMode()
	{
		try
		{

			List<CategoryInterface> categoryList = getAllCategories();
			for (CategoryInterface category : categoryList)
			{

				for (Object container : category.getRootCategoryElement().getContainerCollection())
				{
					((ContainerInterface) container).generateContainerHTML(category.getName(),
							WebUIManagerConstants.VIEW_MODE);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
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
		CategoryInterface category = categoryList.get(0);
		return category;
	}

	/**
	 * This method will retrieve all the categories in the DB.
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 */
	private List<CategoryInterface> getAllCategories() throws DynamicExtensionsSystemException,
			DAOException
	{
		HibernateDAO hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();
		List<CategoryInterface> categoryList = hibernateDAO.retrieve(CategoryInterface.class
				.getName());
		DynamicExtensionsUtility.closeDAO(hibernateDAO);
		return categoryList;
	}

	/**
	 * This method will create the Data Value map for the Given Category Entity .
	 * @param rootCatEntity
	 * @return
	 */
	private Map<BaseAbstractAttributeInterface, Object> createDataValueMap(
			CategoryEntityInterface rootCatEntity)
	{
		Map<BaseAbstractAttributeInterface, Object> dataValue = new HashMap<BaseAbstractAttributeInterface, Object>();
		for (CategoryAttributeInterface catAtt : rootCatEntity.getAllCategoryAttributes())
		{
			// put the different value for diff attribute type
			if (catAtt.getAbstractAttribute() instanceof AttributeInterface)
			{
				AttributeInterface attribute = (AttributeInterface) catAtt.getAbstractAttribute();
				if (attribute.getAttributeTypeInformation() instanceof DateAttributeTypeInformation)
				{
					String format = ((DateAttributeTypeInformation) attribute
							.getAttributeTypeInformation()).getFormat();
					String value = "";
					if (format.equals(ProcessorConstants.DATE_FORMAT_OPTION_DATEANDTIME))
					{
						value = "11" + ProcessorConstants.DATE_SEPARATOR + "20"
								+ ProcessorConstants.DATE_SEPARATOR + "1998 10:50";
					}
					else if (format.equals(ProcessorConstants.DATE_FORMAT_OPTION_DATEONLY))
					{
						value = "11" + ProcessorConstants.DATE_SEPARATOR + "20"
								+ ProcessorConstants.DATE_SEPARATOR + "1998";
					}
					else if (format.equals(ProcessorConstants.DATE_FORMAT_OPTION_MONTHANDYEAR))
					{
						value = "11" + ProcessorConstants.DATE_SEPARATOR + "1998";
					}
					else if (format.equals(ProcessorConstants.DATE_FORMAT_OPTION_YEARONLY))
					{
						value = "1998";
					}
					dataValue.put(catAtt, value);
				}
				else if (attribute.getAttributeTypeInformation() instanceof NumericTypeInformationInterface)
				{
					dataValue.put(catAtt, "10");
				}
				else if (attribute.getAttributeTypeInformation() instanceof BooleanAttributeTypeInformation)
				{
					dataValue.put(catAtt, "true");
				}
				else if (attribute.getAttributeTypeInformation() instanceof StringAttributeTypeInformation)
				{
					dataValue.put(catAtt, "test String");
				}
				else
				{
					dataValue.put(catAtt, "test String for other data type");
				}
			}
		}
		for (CategoryAssociationInterface catAssociation : rootCatEntity
				.getCategoryAssociationCollection())
		{
			List dataList = new ArrayList();
			CategoryEntityInterface targetCaEntity = catAssociation.getTargetCategoryEntity();
			dataList.add(createDataValueMap(targetCaEntity));
			if (targetCaEntity.getNumberOfEntries().equals(CategoryCSVConstants.MULTILINE))
			{
				dataList.add(createDataValueMap(targetCaEntity));
			}
			dataValue.put(catAssociation, dataList);
		}
		return dataValue;
	}
}