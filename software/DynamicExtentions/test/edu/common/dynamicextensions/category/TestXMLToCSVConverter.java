
package edu.common.dynamicextensions.category;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import edu.common.dynamicextensions.client.XMLToCSV;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.logger.Logger;

/**
 * Test xml to csv conversion
 * @author rajesh_vyas
 *
 */
public class TestXMLToCSVConverter extends TestCase
{

	private static final Logger LOGGER = Logger.getCommonLogger(TestXMLToCSVConverter.class);

	/**
	 *
	 */
	public void testTxXML()
	{
		XMLToCSV xmlToCSV = new XMLToCSV();
		try
		{
			File inputXMLFile = new File("src/resources/xml/Patient.xml");
			System.out.println("Input file: " + inputXMLFile.getAbsolutePath());
			File outputDir = new File(System.getProperty("java.io.tmpdir"));
			System.out.println(outputDir);
			xmlToCSV.convertXMLs(inputXMLFile, outputDir, null);
			File cSVFile = new File("src/resources/csv/test_Patient.csv");
			String expected = readXML(cSVFile);
			String actual = readXML(new File(outputDir, "Patient.csv"));

			assertEquals(expected, actual);
		}
		catch (DynamicExtensionsSystemException e)
		{
			LOGGER.error(e.getMessage());
		}
		catch (IOException e)
		{
			LOGGER.error(e.getMessage());
		}
		catch (SAXException e)
		{
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * @param expectedFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private String readXML(File expectedFile) throws FileNotFoundException, IOException
	{
		String textString = null;
		BufferedReader xmlBufferedReader = null;
		try
		{
			xmlBufferedReader = new BufferedReader(new FileReader(expectedFile));
			StringBuilder stringBuilder = new StringBuilder();
			char[] cbuf = new char[1024];
			int read = xmlBufferedReader.read(cbuf);

			while (read != -1)
			{
				stringBuilder.append(cbuf);
				read = xmlBufferedReader.read(cbuf);
			}
			textString = stringBuilder.toString();
		}
		finally
		{
			close(xmlBufferedReader);
		}

		return textString;
	}

	private void close(Reader reader) throws IOException
	{
		if (reader != null)
		{
			reader.close();
		}
	}
}
