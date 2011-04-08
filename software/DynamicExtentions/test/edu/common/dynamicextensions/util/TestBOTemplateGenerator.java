/**
 *
 */

package edu.common.dynamicextensions.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.bulkoperator.util.BulkOperationException;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.logger.Logger;

/**
 * @author shrishail_kalshetty
 * This class tests the BO template files get successfully generated.
 *
 */
public class TestBOTemplateGenerator extends DynamicExtensionsBaseTestCase
{

	private static String participantXMLDir = System.getProperty("user.dir") + File.separator
			+ "src" + File.separator + "resources" + File.separator + "xml";
	/**
	 * Logger object.
	 */
	private static final Logger LOGGER = Logger.getCommonLogger(TestBOTemplateGenerator.class);

	/**
	 * Generate XML and CSV template for category object.
	 */
	public void testGenerateXMLAndCSVTemplate()
	{
		try
		{

			CategoryInterface category = CategoryManager.getInstance().getAllCategories()
					.iterator().next();
			if (category == null)
			{
				LOGGER.info("testGenerateXMLAndCSVTemplate() failed.");
				fail();
			}
			else
			{
				BOTemplateGenerator boTemplateGenerator = new BOTemplateGenerator(category);
				boTemplateGenerator.generateXMLAndCSVTemplate(System.getProperty("user.dir"),
						participantXMLDir + File.separator + "Participant.xml", participantXMLDir
								+ File.separator + "mapping.xml");
				LOGGER.info("testGenerateXMLAndCSVTemplate() executed successfully.");
			}
			deleteFiles(participantXMLDir);
		}
		catch (BulkOperationException boException)
		{
			LOGGER.error("BulkOperation Exception: ", boException);
			boException.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsSystemException deException)
		{
			LOGGER.error("DE System Exception: ", deException);
			deException.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsApplicationException deAppException)
		{
			LOGGER.error("DE System Exception: ", deAppException);
			deAppException.printStackTrace();
			fail();
		}
	}

	public void testGenerateXMLAndCSVTemplateForMultiSelect()
	{
		try
		{
			CategoryInterface category = EntityCache.getInstance().getCategoryByName(
					"Test AutoComplete multiselect");
			if (category == null)
			{
				LOGGER.info("testGenerateXMLAndCSVTemplate() failed.");
				fail();
			}
			else
			{
				BOTemplateGenerator boTemplateGenerator = new BOTemplateGenerator(category);
				boTemplateGenerator.generateXMLAndCSVTemplate(System.getProperty("user.dir"),
						participantXMLDir + File.separator + "Participant.xml", participantXMLDir
								+ File.separator + "mapping.xml");
				String preTestedXMLTemplateFilePath = System.getProperty("user.dir")
						+ "/XMLAndCSVTemplate/Test AutoComplete multiselect.xml";

				String generatedXMLTemplateFilePath = System.getProperty("user.dir")
						+ File.separator + "src" + File.separator + "resources" + File.separator
						+ "/XMLAndCSVTemplate/Tested_AutoComplete_multiselect.xml";

				compareFiles(preTestedXMLTemplateFilePath, generatedXMLTemplateFilePath);
				LOGGER.info("testGenerateXMLAndCSVTemplate() executed successfully.");
			}
		}
		catch (BulkOperationException boException)
		{
			LOGGER.error("BulkOperation Exception: ", boException);
			boException.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsSystemException deException)
		{
			LOGGER.error("DE System Exception: ", deException);
			deException.printStackTrace();
			fail();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void testGenerateXMLAndCSVTemplateForLiveValidation()
	{
		try
		{
			CategoryInterface category = EntityCache.getInstance().getCategoryByName(
					"Live Validation Form");
			if (category == null)
			{
				LOGGER.info("testGenerateXMLAndCSVTemplate() failed.");
				fail();
			}
			else
			{
				BOTemplateGenerator boTemplateGenerator = new BOTemplateGenerator(category);
				boTemplateGenerator.generateXMLAndCSVTemplate(System.getProperty("user.dir"),
						participantXMLDir + File.separator + "Participant.xml", participantXMLDir
								+ File.separator + "mapping.xml");
				String preTestedXMLTemplateFilePath = System.getProperty("user.dir")
						+ "/XMLAndCSVTemplate/Live Validation Form.xml";

				String generatedXMLTemplateFilePath = System.getProperty("user.dir")
						+ File.separator + "src" + File.separator + "resources" + File.separator
						+ "/XMLAndCSVTemplate/Tested Live Validation Form.xml";

				compareFiles(preTestedXMLTemplateFilePath, generatedXMLTemplateFilePath);
				LOGGER.info("testGenerateXMLAndCSVTemplate() executed successfully.");
			}
		}
		catch (BulkOperationException boException)
		{
			LOGGER.error("BulkOperation Exception: ", boException);
			boException.printStackTrace();
			fail();
		}
		catch (DynamicExtensionsSystemException deException)
		{
			LOGGER.error("DE System Exception: ", deException);
			deException.printStackTrace();
			fail();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void compareFiles(String preTestedXMLTemplateFilePath,
			String generatedXMLTemplateFilePath) throws IOException
	{

		StringBuffer strContentOutPut = new StringBuffer();
		String str = null;
		FileInputStream fstream = new FileInputStream(preTestedXMLTemplateFilePath);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		while ((str = br.readLine()) != null)
		{
			strContentOutPut.append(str);
		}

		FileInputStream fstream1 = new FileInputStream(generatedXMLTemplateFilePath);
		DataInputStream in1 = new DataInputStream(fstream1);
		BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));
		StringBuffer strContent = new StringBuffer();
		while ((str = br1.readLine()) != null)
		{
			strContent.append(str);
		}
		assertEquals(strContent.toString(), strContentOutPut.toString());
	}

	/**
	 * @param filePath Delete Files created.
	 */
	private void deleteFiles(String filePath)
	{
		File file = new File(filePath);
		if (file.isDirectory() && file.exists())
		{
			for (String fileName : file.list())
			{
				LOGGER.info("File " + fileName + " Deleted: " + new File(fileName).delete());
			}
		}
		LOGGER.info("Directory " + file.getName() + " Deleted: " + file.delete());
	}
}
