
package edu.common.dynamicextensions.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;

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

	/**
	 * @param categoriesFilePath csv file containing category names
	 * @param isCacheble 1 = true, 0=false
	 * @throws DynamicExtensionsSystemException if error handling file
	 */
	private void markCategoriesCacheable(String categoriesFilePath, int isCacheble)
			throws DynamicExtensionsSystemException
	{
		StringBuffer formList = new StringBuffer();
		try
		{
			CSVReader csvReader = new CSVReader(new FileReader(categoriesFilePath));
			String[] line = csvReader.readNext();

			for (String string : line)
			{
				formList.append('\'');
				formList.append(string);
				formList.append("',");

			}
			formList.replace(formList.length() - 1, formList.length(), "");
			jdbcdao = DynamicExtensionsUtility.getJDBCDAO();
			String idList = getCategoryIds(formList);
			jdbcdao.executeUpdate("update dyextn_category set IS_CACHEABLE=" + isCacheble
					+ " where identifier in (" + idList + ")");
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
			throw new DynamicExtensionsSystemException("Error executing query "
					+ "update dyextn_category set IS_CACHEABLE=1 where identifier in ("
					+ formList.toString() + ")", e);

		}
	}

	/**
	 * @param categoryNameList category name list
	 * @return category id list
	 * @throws DAOException if could error occurred in executing query
	 */
	private String getCategoryIds(StringBuffer categoryNameList) throws DAOException
	{
		List<List<Object>> list = jdbcdao
				.executeQuery("select t1.identifier from dyextn_abstract_metadata t1,dyextn_category t2 "
						+ "where name in ("
						+ categoryNameList.toString()
						+ ") and t1.identifier=t2.identifier");

		StringBuffer idList = new StringBuffer();
		for (List<Object> id : list)
		{
			idList.append('\'');
			idList.append(id.get(0).toString());
			idList.append("',");

		}
		idList.replace(idList.length() - 1, idList.length(), "");

		return idList.toString();
	}

	/**
	 * @param args from ant task
	 * @throws DynamicExtensionsSystemException if problem creating jdbc dao
	 */
	public static void main(String[] args) throws DynamicExtensionsSystemException
	{
		/*args = new String[]{"src/resources/csv/cacheableCategories.csv"};*/
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
		Logger.out.info("Categories marked successfully.");

	}
}
