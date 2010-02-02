
package edu.common.dynamicextensions.category;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.logger.Logger;

/**
 * Test class to test xml to csv conversion
 * @author rajesh_vyas
 *
 */
public class TestXMLToCSVConverter extends TestCase
{
	private static final Logger logger = Logger.getCommonLogger(TestXMLToCSVConverter.class);
	/**
	 *
	 */
	public void testTxXML()
	{
		XMLToCSV xmlToCSV = new XMLToCSV();
		try
		{
			URL resource = TestXMLToCSVConverter.class.getResource("Patient.xml");
			File outputDir = new File(System.getProperty("java.io.tmpdir"));
			xmlToCSV.convertXMLs(new File(resource.getFile()), outputDir, null);
		}
		catch (DynamicExtensionsSystemException e)
		{
			logger.error(e.getStackTrace());
		}
		catch (IOException e)
		{
			logger.error(e.getStackTrace());
		}
		catch (SAXException e)
		{
			logger.error(e.getStackTrace());
		}
		//final String sampleContents = readSampleCSV();
		//final XMLToCSVConverter xmlToCSVConverter = new XMLToCSVConverter();
	}
}
