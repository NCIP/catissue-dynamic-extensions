
package edu.common.dynamicextensions.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 *
 * @author falguni_sachde
 *
 */
public class MultipleCategoryCreator
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	//Logger
	private static final Logger LOGGER = Logger.getCommonLogger(MultipleCategoryCreator.class);

	/**
	 * Main method which takes a command Line argument the name of the FormNames CSV file
	 * from which to create the Categories.
	 * @param args command line arguments.
	 */
	public static void main(String[] args)
	{
		try
		{
			if (args.length == 0)
			{
				throw new Exception("Please specify  path for  categoryFormNames.csv");
			}
			String filePath = args[0];
			LOGGER.info("---- The categoryFormNames.csv file path is " + filePath + " ----");
			File objFile = new File(filePath);
			BufferedReader bufRdr = null;
			ArrayList<String> fileNameList = new ArrayList<String>();
			if (objFile.exists())
			{
				try
				{
					bufRdr = new BufferedReader(new FileReader(filePath));
					String line = bufRdr.readLine();
					//read each line of text file
					while (line  != null)
					{
						fileNameList.add(line.trim());
						line = bufRdr.readLine();
					}
				}
				finally
				{
					bufRdr.close();
				}
			}

			for (String catFormDefFilePath : fileNameList)
			{
				try
				{
					CategoryCreator.createCategory(catFormDefFilePath,false);
				}
				catch (Exception e)
				{
					LOGGER.info("Error  while executing Category form definition file -"
							+ catFormDefFilePath);
				}

			}
		}
		catch (Exception ex)
		{
			LOGGER.info("Exception while creating  all categories : " + ex.getMessage());
			throw new RuntimeException(ex);
		}
	}
}