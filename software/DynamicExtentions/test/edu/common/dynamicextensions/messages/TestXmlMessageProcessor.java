
package edu.common.dynamicextensions.messages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.global.ApplicationProperties;

public class TestXmlMessageProcessor extends DynamicExtensionsBaseTestCase
{

	/**
	 *
	 * This test case will try to insert the data into the Generic form from the message & also edits the same
	 * record later.
	 */
	public void testInsertDataFromMessageGenericForm()
	{
		ApplicationProperties.initBundle("ApplicationResources");
		try
		{
			EntityCache.getInstance().refreshCache();
			CategoryManagerInterface catManager = CategoryManager.getInstance();
			CategoryInterface category = catManager
					.getCategoryByName("Generic Form to load all lab tests");
			XmlMessageProcessor messagePrcocessor = XmlMessageProcessor.getInstance();
			ContainerInterface container = (ContainerInterface) category.getRootCategoryElement()
					.getContainerCollection().iterator().next();
			String xmlString = readXmlFile(TEST_MODEL_DIR + File.separator + "Labxml_message.xml");
			Long recordId = messagePrcocessor.insertOrEditDataFromMessage(xmlString, null, container
					.getId(), null, sessionDataBean);
			if (recordId == null)
			{
				fail("testInsertDataFromMessageGenericForm--> data insertion failed. RecordId is null");
			}
			System.out
					.println("testInsertDataFromMessageGenericForm-->Data Inserted successfully for the Generic Form to load all lab tests. Record Id = "
							+ recordId);
			Long catRecordId = catManager.getRootCategoryEntityRecordIdByEntityRecordId(recordId,
					category.getRootCategoryElement().getTableProperties().getName());
			catManager.getRecordById(category.getRootCategoryElement(), catRecordId);
			messagePrcocessor
					.insertOrEditDataFromMessage(xmlString, xmlString, container.getId(), catRecordId, sessionDataBean);
			System.out
					.println("testInsertDataFromMessageGenericForm-->Data Edited successfully for the Generic Form to load all lab tests. Record Id = "
							+ recordId);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("testInsertDataFromMessageGenericForm --> Failed");
		}
	}

	/**
	 *
	 * This test case will try to insert the data into the Instance based form from the message & also edits the same
	 * record later.
	 */
	public void testInsertDataFromMessageInstanceBasedForm()
	{
		ApplicationProperties.initBundle("ApplicationResources");
		try
		{
			EntityCache.getInstance().refreshCache();
			System.out.println("In testInsertDataFromMessageInstanceBasedForm");
			CategoryManagerInterface catManager = CategoryManager.getInstance();
			CategoryInterface category = catManager
					.getCategoryByName("Instance-based single message form");
			XmlMessageProcessor messagePrcocessor = XmlMessageProcessor.getInstance();
			ContainerInterface container = (ContainerInterface) category.getRootCategoryElement()
					.getContainerCollection().iterator().next();
			String xmlString = readXmlFile(TEST_MODEL_DIR + File.separator + "Labxml_message.xml");
			Long recordId = messagePrcocessor.insertOrEditDataFromMessage(xmlString, null, container
					.getId(), null);
			if (recordId == null)
			{
				fail("testInsertDataFromMessageInstanceBasedForm--> data insertion failed.");
			}
			System.out
					.println("testInsertDataFromMessageInstanceBasedForm-->Data Inserted successfully for the Instance based Form  to load all lab tests. Record Id = "
							+ recordId);
			Long catRecordId = catManager.getRootCategoryEntityRecordIdByEntityRecordId(recordId,
					category.getRootCategoryElement().getTableProperties().getName());
			catManager.getRecordById(category.getRootCategoryElement(), catRecordId);
			messagePrcocessor.insertOrEditDataFromMessage(xmlString, xmlString, container.getId(), recordId);
			System.out
					.println("testInsertDataFromMessageInstanceBasedForm-->Data Edited successfully for the Instance based Form to load all lab tests. Record Id = "
							+ recordId);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("testInsertDataFromMessageInstanceBasedForm --> Failed");
		}
	}

	/**
	 * This method will create the dummy form context objects from all the categories present in the database & will
	 * try to search the form context for which the message is applicable.
	 * At least one of the form should get qualified for the message & if not then test case is failed.
	 * This test case then will also try to insert the data in all the forms which are qualified for
	 * the message.
	 */
	public void testSearchMatchingFormContextForMessage()
	{
		try
		{
			Collection<CategoryInterface> catColl = CategoryManager.getInstance()
					.getAllCategories();
			Collection<Long> containerIdColl = new HashSet<Long>();

			for (CategoryInterface category : catColl)
			{
				ContainerInterface container = (ContainerInterface) category
						.getRootCategoryElement().getContainerCollection().iterator().next();
				containerIdColl.add(container.getId());

			}
			String xmlString = readXmlFile(TEST_MODEL_DIR + File.separator + "Labxml_message.xml");
			XmlMessageProcessor messageProcessor = XmlMessageProcessor.getInstance();
			Collection<Long> matchedContainerIdColl = messageProcessor
					.getContainerIdCollectionForMessage(xmlString, containerIdColl);
			if (matchedContainerIdColl.isEmpty())
			{
				fail("testSearchMatchingFormContextForMessage-->failed, No matching form context found.");
			}
			for (Long containerId : matchedContainerIdColl)
			{

				System.out
						.println("testSearchMatchingFormContextForMessage--> Inserting data for container Id :"
								+ containerId);

				Long recordId = messageProcessor.insertOrEditDataFromMessage(xmlString, null,
						containerId, null);
				if (recordId == null)
				{
					fail("testSearchMatchingFormContextForMessage--> data insertion failed.");
				}
				System.out
						.println("testSearchMatchingFormContextForMessage-->Data Inserted successfully for container "
								+ containerId + " Record Id = " + recordId);

			}
			System.out
					.println("testSearchMatchingFormContextForMessage--> Successfully completed.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("testSearchMatchingFormContextForMessage-->failed");
		}
	}

	/**
	 * This method will read the file & convert it into the String message which will be returned from this
	 * message.
	 * @param fileName
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws IOException
	 */
	private String readXmlFile(String fileName) throws DynamicExtensionsSystemException,
			IOException
	{
		StringBuffer xmlString = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		try
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				xmlString.append(line);
			}
		}
		catch (Exception e)
		{
			throw new DynamicExtensionsSystemException("Error while reading the xml message file",
					e);
		}
		finally
		{
			reader.close();
		}
		return xmlString.toString();
	}

}
