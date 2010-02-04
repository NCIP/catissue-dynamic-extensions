
package edu.common.dynamicextensions.category;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

/**
 * Create Category through xml files. This is required in CSD application.
 * @author rajesh_vyas
 *
 */
public class XMLToCSV
{

	private static final Logger LOGGER = Logger.getLogger(XMLToCSVConverter.class.getName());

	/**
	 * This method is called when called from an ant target to create category from xml.
	 * This is used in CSD application.
	 * @param args dir names
	 */
	public static void main(final String[] args)
	{
		try
		{
			XMLToCSV xmlToCSV = new XMLToCSV();

			xmlToCSV.validateArguments(args);
			final File input = new File(args[0]);
			final File outputDir = new File(args[1]);
			xmlToCSV.convertXMLs(input, outputDir, args.length > 2 ? new File(args[2]) : null);
		}
		catch (final DynamicExtensionsSystemException e)
		{
			LOGGER.error(e.getMessage());
		}
		catch (final IOException e)
		{
			LOGGER.error(e.getMessage());
		}
		catch (final SAXException e)
		{
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * 
	 * @param args
	 * @throws DynamicExtensionsSystemException
	 */
	private void validateArguments(final String[] args) throws DynamicExtensionsSystemException
	{
		if (args.length < 2)
		{
			throw new DynamicExtensionsSystemException(
					"Not enough arguments passed. At least 3 arguments expected as input and output diretory. e.g.\n "
							+ "XMLToCSVConverter input.xml output/ schema.xsd");
		}
	}

	/**
	 * Converts the xml files into csv files
	 * @param input input dir of xml files
	 * @param outputDir dir to store csv files
	 * @param schemaFile schema file to vaidate xml
	 * @throws IOException exception IOrelated
	 * @throws SAXException exception on parsing
	 * @throws DynamicExtensionsSystemException exception DE
	 */
	public void convertXMLs(final File input, final File outputDir, final File schemaFile)
			throws IOException, SAXException, DynamicExtensionsSystemException
	{
		validateOutputDir(outputDir);
		validateSchemaFile(schemaFile);
		if (input.isFile())
		{
			writeXML(input, outputDir, schemaFile);
		}
		else if (input.isDirectory())
		{
			convertListOfXMLs(input, outputDir, schemaFile);
		}
	}

	/**
	 * @param outputDir
	 * @throws DynamicExtensionsSystemException
	 */
	private void validateOutputDir(final File outputDir) throws DynamicExtensionsSystemException
	{
		if (!outputDir.isDirectory())
		{
			throw new DynamicExtensionsSystemException(
					"output dir path does not represent a directory.");
		}
	}

	/**
	 * @param schemaFile
	 * @throws DynamicExtensionsSystemException
	 */
	private void validateSchemaFile(final File schemaFile) throws DynamicExtensionsSystemException
	{
		if (schemaFile == null || !schemaFile.isFile())
		{
			LOGGER.warn("No schema file found:");
		}
	}

	/**
	 * @param input
	 * @param outputDir
	 * @throws IOException
	 * @throws SAXException
	 */
	private void writeXML(final File schemaFile, final File inputXML, final File outputDir)
			throws IOException, SAXException
	{
		final String csvFileName = getCSVFileName(inputXML);
		final XMLToCSVConverter xmlToCSVConverter = new XMLToCSVConverter(inputXML, new File(
				outputDir, csvFileName));
		if (schemaFile != null && schemaFile.isFile())
		{
			new SchemaValidator().validateAgainstSchema(schemaFile, inputXML);
		}
		xmlToCSVConverter.txXML();
	}

	/**
	 * @param input
	 * @param outputDir
	 * @throws IOException
	 * @throws SAXException
	 */
	private void convertListOfXMLs(final File input, final File outputDir, final File schemaFile)
			throws IOException, SAXException
	{
		final File[] listFiles = input.listFiles();
		for (int i = 0; i < listFiles.length; i++)
		{
			final File xmlFile = listFiles[i];
			writeXML(schemaFile, xmlFile, outputDir);
		}
	}

	/**
	 * @param xmlFile
	 * @param name
	 * @return
	 */
	private String getCSVFileName(final File xmlFile)
	{
		String csvFileName = xmlFile.getName();
		int lastIndexOf = csvFileName.lastIndexOf(".xml");
		if (lastIndexOf != -1)
		{
			csvFileName = xmlFile.getName().substring(0, lastIndexOf) + ".csv";
		}
		return csvFileName;
	}
}