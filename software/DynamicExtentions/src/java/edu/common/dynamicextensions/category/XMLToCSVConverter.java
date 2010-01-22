
package edu.common.dynamicextensions.category;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

/**
 * @author rajesh_vyas
 *
 */
public class XMLToCSVConverter
{

	private static final Logger LOGGER = Logger.getLogger(XMLToCSVConverter.class.getName());

	private static final String CAPTION2 = "caption";

	private static final String INSTANCE = "instance";

	private static final String INSTANCES = "instances";

	private static final String VALUE = "value";

	private static final String KEY = "key";

	private static final String UI_PROPERTY = "UIProperty";

	private static final String UI_CONTROL = "uiControl";

	private static final String ATTRIBUTE_NAME = "attributeName";

	private static final String CLASS_NAME = "className";

	private static final String SINGLE_LINE_DISPLAY = "SingleLineDisplay";

	private static final String ATTRIBUTE = "attribute";

	private static final String FORM_NAME = "name";

	private static final String SKIP_LOGIC = "SkipLogic";

	private static final String RELATED_ATTRIBUTE = "RelatedAttribute";

	private static final String FORM = "form";

	private static final String ENTITY_GROUP_NAME = "entityGroup";

	private static final String FORM_DEFINITION_NAME = FORM_NAME;

	private static final String FORM_DEFINITION = "FormDefinition";

	private static int formCount = 0;

	private Document document;

	private final Writer writer;

	private final InputSource inputSource;

	private final String newLine = System.getProperty("line.separator");

	public XMLToCSVConverter(final File xmlFile, final File csvFile) throws IOException
	{
		writer = new BufferedWriter(new FileWriter(csvFile));
		inputSource = new InputSource(new FileReader(xmlFile));
	}

	public void txXML() throws SAXException, IOException
	{
		final DOMParser domParser = new DOMParser();
		try
		{
			domParser.parse(inputSource);
			document = domParser.getDocument();
			txFormDefinition();
		}
		finally
		{
			writer.close();
		}
	}

	private void txFormDefinition() throws IOException
	{
		final NodeList formDefinitionTag = document.getElementsByTagName(FORM_DEFINITION);
		final int length = formDefinitionTag.getLength();

		for (int i = 0; i < length; i++)
		{
			final Node item = formDefinitionTag.item(i);
			writer.write("Form_Definition" + newLine + newLine);
			final NamedNodeMap formDefinitionProperties = item.getAttributes();

			final Node formDefinitionName = formDefinitionProperties
					.getNamedItem(FORM_DEFINITION_NAME);
			writer.write(formDefinitionName.getNodeValue() + newLine + newLine);

			final Node entityGroupName = formDefinitionProperties.getNamedItem(ENTITY_GROUP_NAME);
			writer.write(entityGroupName.getNodeValue() + newLine + newLine);

			txFormDefinitionChildren(item);
			//txSkiplogic(item);
		}
	}

	/**
	 * @param item
	 * @throws IOException
	 */
	private void txFormDefinitionChildren(final Node item) throws IOException
	{
		final NodeList childNodes = item.getChildNodes();

		final int length2 = childNodes.getLength();

		for (int j = 0; j < length2; j++)
		{

			final Node item2 = childNodes.item(j);
			final String nodeName = item2.getNodeName();

			if (nodeName.equals(FORM))
			{
				formCount++;
				writer.write("Display_Label:" + formCount + " ");
				txForm(item2);
			}
			/*else if (nodeName.equals(SKIP_LOGIC))
			{
				//txSkiplogic(item2);
			}*/
			else if (nodeName.equals(RELATED_ATTRIBUTE))
			{
				txRelatedAttributes(item2);
			}
		}
	}

	private void txForm(final Node item) throws IOException
	{

		final NamedNodeMap formProperties = item.getAttributes();

		final Node formName = formProperties.getNamedItem(FORM_NAME);
		writer.write(formName.getNodeValue() + newLine);

		final NodeList childNodes = item.getChildNodes();

		final int length = childNodes.getLength();

		for (int i = 0; i < length; i++)
		{
			final Node item2 = childNodes.item(i);
			final String nodeName = item2.getNodeName();

			if (nodeName.equals(INSTANCES))
			{
				txInstances(item2);
			}
			else if (nodeName.equals(ATTRIBUTE))
			{
				txAttribute(item2);
			}
			else if (nodeName.equals(SINGLE_LINE_DISPLAY))
			{
				txSingleLineDisplay(item2);
			}

		}
	}

	private void txSingleLineDisplay(final Node item) throws IOException
	{
		final NodeList childNodes = item.getChildNodes();
		final int length = childNodes.getLength();
		writer.write("SingleLineDisplay:start" + newLine);
		for (int i = 0; i < length; i++)
		{
			final Node item2 = childNodes.item(i);

			final String nodeName = item2.getNodeName();
			if (nodeName.equals(ATTRIBUTE))
			{
				txAttribute(item2);
			}
		}
		writer.write("SingleLineDisplay:end" + newLine);
	}

	private void txInstances(final Node item2) throws IOException
	{
		writer.write("instance:");

		final NodeList childNodes = item2.getChildNodes();
		final int length = childNodes.getLength();

		for (int i = 0; i < length; i++)
		{
			final Node item = childNodes.item(i);
			final String nodeName = item.getNodeName();

			if (nodeName.equals(INSTANCE))
			{
				txInstance(item);
			}
		}
	}

	private void txInstance(final Node item2) throws IOException
	{
		final String instance = item2.getFirstChild().getNodeValue();

		writer.write(instance + newLine);
	}

	private void txAttribute(final Node item) throws IOException
	{

		final NamedNodeMap controlProperties = item.getAttributes();

		final Node className = controlProperties.getNamedItem(CLASS_NAME);
		writer.write(className.getNodeValue() + ":");

		final Node attributeName = controlProperties.getNamedItem(ATTRIBUTE_NAME);
		writer.write(attributeName.getNodeValue() + ",");

		final Node uiControlName = controlProperties.getNamedItem(UI_CONTROL);
		writer.write(uiControlName.getNodeValue() + ",");

		final Node controlCaption = controlProperties.getNamedItem(CAPTION2);
		writer.write(controlCaption.getNodeValue() + ",options~");

		final NodeList childNodes = item.getChildNodes();

		final int length = childNodes.getLength();

		for (int j = 0; j < length; j++)
		{
			final Node item2 = childNodes.item(j);
			final String nodeName = item2.getNodeName();

			if (nodeName.equals(UI_PROPERTY))
			{
				txUIProperties(item2);
			}
		}
		writer.write(newLine);
	}

	private void txUIProperties(final Node item) throws DOMException, IOException
	{
		final NamedNodeMap controlProperties = item.getAttributes();

		final Node className = controlProperties.getNamedItem(KEY);
		writer.write(className.getNodeValue() + "=");

		final Node attributeName = controlProperties.getNamedItem(VALUE);
		writer.write(attributeName.getNodeValue() + ",");
	}

	private void txRelatedAttributes(final Node item) throws IOException
	{

		Node instanceNode = null;
		Node uiPropertyNode = null;
		writer.write("RelatedAttribute:" + newLine);

		final NamedNodeMap attributes = item.getAttributes();

		final NodeList childNodes = item.getChildNodes();
		final int length2 = childNodes.getLength();

		for (int i = 0; i < length2; i++)
		{
			final Node item3 = childNodes.item(i);
			final String nodeName = item3.getNodeName();

			if (nodeName.equals(INSTANCES))
			{
				instanceNode = item3;
			}
			if (nodeName.equals(UI_PROPERTY))
			{
				uiPropertyNode = item3;
			}
		}

		txRAInRequiredCSVOrder(instanceNode, uiPropertyNode, attributes);
	}

	/**
	 * @param instanceNode
	 * @param uiPropertyNode
	 * @param attributes
	 * @throws IOException
	 * @throws DOMException
	 */
	private void txRAInRequiredCSVOrder(final Node instanceNode, final Node uiPropertyNode,
			final NamedNodeMap attributes) throws IOException, DOMException
	{
		if (instanceNode != null)
		{
			txInstances(instanceNode);
		}

		final Node classeName = attributes.getNamedItem(CLASS_NAME);
		writer.write(classeName.getNodeValue() + ":");

		final Node attributeName = attributes.getNamedItem(ATTRIBUTE_NAME);
		writer.write(attributeName.getNodeValue() + "=");

		final Node relatedAttributeValue = attributes.getNamedItem(VALUE);
		writer.write(relatedAttributeValue.getNodeValue());

		if (uiPropertyNode != null)
		{
			writer.write(",options~");
			txUIProperties(uiPropertyNode);
		}
	}

	public static void main(final String[] args)
	{
		try
		{
			validateArguments(args);
			final File input = new File(args[0].trim());
			final File outputDir = new File(args[1].trim());
			convertXMLs(input, outputDir);
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

	private static void validateArguments(final String[] args)
			throws DynamicExtensionsSystemException
	{
		if (args.length < 2)
		{
			throw new DynamicExtensionsSystemException(
					"Not enough arguments passed. At least 2 arguments expected as input and output diretory.");
		}
	}

	private static void convertXMLs(final File input, final File outputDir) throws IOException,
			SAXException, DynamicExtensionsSystemException
	{
		final String name = input.getName();
		final int lastIndexOf = name.lastIndexOf(".xml");
		if (!outputDir.isDirectory())
		{
			throw new DynamicExtensionsSystemException(
					"output dir path does not represent a directory.");
		}
		if (lastIndexOf != -1)
		{
			writeXML(input, outputDir);
		}
		else if (input.isDirectory())
		{
			convertListOfXMLs(input, outputDir);
		}
		else
		{
			throw new DynamicExtensionsSystemException(
					"Supplied argument does not represent a xml file or a directory.");
		}
	}

	/**
	 * @param input
	 * @param outputDir
	 * @throws IOException
	 * @throws SAXException
	 */
	private static void writeXML(final File inputXML, final File outputDir) throws IOException,
			SAXException
	{
		final String csvFileName = getCSVFileName(inputXML);
		final XMLToCSVConverter xmlToCSVConverter = new XMLToCSVConverter(inputXML, new File(
				outputDir, csvFileName));
		xmlToCSVConverter.txXML();
	}

	/**
	 * @param input
	 * @param outputDir
	 * @throws IOException
	 * @throws SAXException
	 */
	private static void convertListOfXMLs(final File input, final File outputDir)
			throws IOException, SAXException
	{
		final File[] listFiles = input.listFiles();
		for (int i = 0; i < listFiles.length; i++)
		{
			final File xmlFile = listFiles[i];
			writeXML(xmlFile, outputDir);
		}
	}

	/**
	 * @param xmlFile
	 * @param name
	 * @return
	 */
	private static String getCSVFileName(final File xmlFile)
	{
		final String name = xmlFile.getName();
		final String csvFileName = xmlFile.getName().substring(0, name.lastIndexOf(".xml"))
				+ ".csv";
		return csvFileName;
	}
}
