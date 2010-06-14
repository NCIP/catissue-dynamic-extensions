
package edu.common.dynamicextensions.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

/**
 * This class is used for category related ant tasks
 * @author kunal_kamble *
 */
public class CategoryUtil
{

	/**
	 * For executing sqls
	 */
	private JDBCDAO jdbcdao;

	private final List<String> categoryNameList = new ArrayList<String>();

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(CategoryUtil.class);

	/**
	 * @param categoriesFilePath csv file containing category names
	 * @param isCacheble 1 = true, 0=false
	 * @throws DynamicExtensionsSystemException if error handling file
	 */
	private void markCategoriesCacheable(String categoriesFilePath, int isCacheble)
			throws DynamicExtensionsSystemException
	{
		try
		{
			CSVReader csvReader = new CSVReader(new FileReader(categoriesFilePath));
			String[] line = csvReader.readNext();

			jdbcdao = DynamicExtensionsUtility.getJDBCDAO();
			String categoryIdList = getCategoryIdList(line);
			if (categoryIdList.length() != 0)
			{
				StringBuffer updateQuery = new StringBuffer(
						"update dyextn_category set IS_CACHEABLE=");
				updateQuery.append(isCacheble);
				updateQuery.append(" where identifier in (");
				updateQuery.append(categoryIdList);
				updateQuery.append(')');

				jdbcdao.executeUpdate(updateQuery.toString());

				for (String category : categoryNameList)
				{
					if (isCacheble == 0)
					{
						LOGGER.info("Category " + category + " removed from caching");
					}
					else
					{
						LOGGER.info("Category " + category + " marked for caching");
					}
				}
			}
			jdbcdao.commit();
			jdbcdao.closeSession();

		}
		catch (FileNotFoundException e)
		{
			throw new DynamicExtensionsSystemException("Error opening file " + categoriesFilePath,
					e);
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException("Error parsing file  " + categoriesFilePath,
					e);
		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Error occured while marking categories for caching", e);
		}
	}

	/**
	 * Gets the category id list.
	 *
	 * @param line the line
	 *
	 * @return the category id list
	 *
	 * @throws DAOException the DAO exception
	 */
	private String getCategoryIdList(String[] line) throws DAOException
	{
		StringBuffer categoryIdList = new StringBuffer();
		for (String string : line)
		{

			String value = getCategoryIdFromDatabase(string);
			if (value != null)
			{
				categoryIdList.append('\'');
				categoryIdList.append(value);
				categoryIdList.append("',");
			}
		}
		if (categoryIdList.length() != 0)
		{
			categoryIdList.replace(categoryIdList.length() - 1, categoryIdList.length(), "");
		}
		return categoryIdList.toString();
	}

	/**
	 * @param categoryName category name list
	 * @return category id list
	 * @throws DAOException if could error occurred in executing query
	 */
	@SuppressWarnings("unchecked")
	private String getCategoryIdFromDatabase(String categoryName) throws DAOException
	{
		String idList = null;

		StringBuffer selectQuery = new StringBuffer(
				"select t1.identifier from dyextn_abstract_metadata t1,dyextn_category t2 where name in (?) and t1.identifier=t2.identifier");

		ColumnValueBean nameColValueBean = new ColumnValueBean("NAME", categoryName);
		List<ColumnValueBean> colValueBean = new LinkedList<ColumnValueBean>();
		colValueBean.add(nameColValueBean);

		List<List<Object>> list = jdbcdao.executeQuery(selectQuery.toString(), colValueBean);

		if (list.isEmpty())
		{
			LOGGER.info("Category " + categoryName + " is not present in database");
		}
		else
		{
			for (List<Object> id : list)
			{
				idList = id.get(0).toString();
				categoryNameList.add(categoryName);
			}
		}
		return idList;
	}

	/**
	 * @param args from ant task
	 * @throws DynamicExtensionsSystemException if problem creating jdbc dao
	 */
	public static void main(String[] args) throws DynamicExtensionsSystemException
	{
		//args = new String[]{"src/resources/csv/cacheableCategories.csv"};
		if (args.length < 1)
		{
			throw new RuntimeException("Please provide category file path.");
		}
		int isCacheble = 1;
		if (args.length > 1
				&& ("F".equalsIgnoreCase(args[1]) || "FALSE".equalsIgnoreCase(args[1]) || "0"
						.equalsIgnoreCase(args[1])))
		{
			isCacheble = 0;
		}
		CategoryUtil categoryUtil = new CategoryUtil();

		if (args[0].endsWith("${categoryFilePath}"))
		{
			categoryUtil.markCategoriesCacheable("./cacheableCategories.csv", isCacheble);
		}
		else
		{
			categoryUtil.markCategoriesCacheable(args[0], isCacheble);
		}
		LOGGER.info("Categories marked successfully.");

	}
}
