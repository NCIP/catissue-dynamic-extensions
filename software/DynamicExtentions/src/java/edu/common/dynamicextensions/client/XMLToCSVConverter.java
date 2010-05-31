
package edu.common.dynamicextensions.client;

import java.io.BufferedReader;
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

// TODO: Auto-generated Javadoc
/**
 * The Class XMLToCSVConverter.
 *
 * @author rajesh_vyas
 */
public class XMLToCSVConverter
{

	/** The Constant SHOW2. */
	private static final String SHOW2 = "show";

	/** The Constant DISPLAY_LABEL. */
	private static final String DISPLAY_LABEL = "Display_Label:";

	/** The Constant PERMISSIBLE_VALUE_FILE. */
	private static final String PERMISSIBLE_VALUE_FILE = "permissibleValueFile";

	/** The Constant SKIP_LOGIC_ATTRIBUTE. */
	//private static final String SKIP_LOGIC_ATTRIBUTE = "SkipLogicAttribute";

	/** The Constant DEPENDENT_ATTRIBUTE. */
	private static final String DEPENDENT_ATTRIBUTE = "DependentAttribute";

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(XMLToCSVConverter.class.getName());

	/** The Constant CAPTION2. */
	private static final String CAPTION2 = "caption";

	/** The Constant INSTANCE. */
	private static final String INSTANCE = "Instance";

	/** The Constant INSTANCES. */
	private static final String INSTANCES = "Instances";

	/** The Constant VALUE. */
	private static final String VALUE = "value";

	/** The Constant KEY. */
	private static final String KEY = "key";

	/** The Constant UI_PROPERTY. */
	private static final String UI_PROPERTY = "Property";

	/** The Constant UI_CONTROL. */
	private static final String UI_CONTROL = "uiControl";

	/** The Constant ATTRIBUTE_NAME. */
	private static final String ATTRIBUTE_NAME = "attributeName";

	/** The Constant CLASS_NAME. */
	private static final String CLASS_NAME = "className";

	/** The Constant SINGLE_LINE_DISPLAY. */
	private static final String SINGLE_LINE_DISPLAY = "SingleLineDisplay";

	/** The Constant ATTRIBUTE. */
	private static final String ATTRIBUTE = "Attribute";

	/** The Constant FORM_NAME. */
	private static final String FORM_NAME = "name";

	/** The Constant SKIP_LOGIC. */
	private static final String SKIP_LOGIC = "SkipLogicAttribute";

	/** The Constant RELATED_ATTRIBUTE. */
	private static final String RELATED_ATTRIBUTE = "RelatedAttribute";

	/** The Constant FORM. */
	private static final String FORM = "Form";

	/** The Constant ENTITY_GROUP_NAME. */
	private static final String ENTITY_GROUP_NAME = "entityGroup";

	/** The Constant FORM_DEFINITION_NAME. */
	private static final String FORM_DEFINITION_NAME = FORM_NAME;

	/** The Constant FORM_DEFINITION. */
	private static final String FORM_DEFINITION = "FormDefinition";

	/** The document. */
	private transient Document document;

	/** The writer. */
	private transient final Writer writer;

	/** The input source. */
	private transient final InputSource inputSource;

	/** The new line. */
	private transient final String newLine = System.getProperty("line.separator");

	/** The string builder. */
	private transient final StringBuilder stringBuilder;

	/** The rules required string. */
	private transient String rulesRequiredString;

	/** The rules required string. */
	private transient String defaultValueString;

	/** The main container. */
	private transient String mainContainer;

	/** The entity group name. */
	private transient String entityGroupName;

	private transient String permValueOptionsString;

	private transient boolean isFirstUIProperty;

	private transient boolean isSecondUIProperty;

	/**
	 * Instantiates a new xML to csv converter.
	 *
	 * @param xmlFile the xml file
	 * @param csvFile the csv file
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public XMLToCSVConverter(final File xmlFile, final File csvFile) throws IOException
	{
		LOGGER.info("XML file:" + xmlFile.getAbsolutePath());
		LOGGER.info("CSV file:" + csvFile.getAbsolutePath());
		writer = new BufferedWriter(new FileWriter(csvFile));
		inputSource = new InputSource(new FileReader(xmlFile));
		stringBuilder = new StringBuilder();
	}

	/**
	 * Tx xml.
	 *
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void txXML() throws SAXException, IOException
	{
		final DOMParser domParser = new DOMParser();
		try
		{
			domParser.parse(inputSource);
			document = domParser.getDocument();
			txFormDefinition();
			writer.write(stringBuilder.toString());
		}
		finally
		{
			writer.close();
		}
	}

	/**
	 * Append to string builder.
	 *
	 * @param stringToBeAppend the string to be append
	 */
	private void appendToStringBuilder(String stringToBeAppend)
	{
		LOGGER.debug("Appending: " + stringToBeAppend);
		stringBuilder.append(stringToBeAppend);
	}

	/**
	 * Tx form definition.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void txFormDefinition() throws IOException
	{
		final NodeList formDefinitionTag = document.getElementsByTagName(FORM_DEFINITION);
		final int length = formDefinitionTag.getLength();

		for (int i = 0; i < length; i++)
		{
			final Node item = formDefinitionTag.item(i);
			appendToStringBuilder("Form_Definition" + newLine + newLine);
			final NamedNodeMap formDefinitionProperties = item.getAttributes();

			final Node formDefinitionName = formDefinitionProperties
			.getNamedItem(FORM_DEFINITION_NAME);
			appendToStringBuilder(formDefinitionName.getNodeValue() + newLine + newLine);

			final Node entityGroupNameNode = formDefinitionProperties
			.getNamedItem(ENTITY_GROUP_NAME);
			entityGroupName = entityGroupNameNode.getNodeValue();
			appendToStringBuilder(entityGroupName + newLine + newLine);

			txFormDefinitionChildren(item);
		}
	}

	/**
	 * Tx form definition children.
	 *
	 * @param item the item
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
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
				txForm(item2);
			}
			else if (nodeName.equals(SKIP_LOGIC))
			{
				txSkiplogic(item2);
			}
			else if (nodeName.equals(RELATED_ATTRIBUTE))
			{
				txRelatedAttributes(item2);
			}
		}
	}

	/**
	 * Tx skiplogic.
	 *
	 * @param item the item
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void txSkiplogic(Node item) throws IOException
	{
		appendToStringBuilder(SKIP_LOGIC + "Attribute"+":" + newLine);
		NodeList controllingAttributes = item.getChildNodes();

		int length = controllingAttributes.getLength();

		for (int i = 0; i < length; i++)
		{
			Node controllingAttributeNode = controllingAttributes.item(i);
			processControllingAttribute(controllingAttributeNode);
		}
	}

	/**
	 * Process controlling attribute.
	 *
	 * @param controllingAttributeNode the controlling attribute node
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void processControllingAttribute(Node controllingAttributeNode) throws IOException
	{
		NodeList childNodes = controllingAttributeNode.getChildNodes();
		String txInstance = null;
		int length = childNodes.getLength();

		Node instance = null;
		Node dependentNode = null;
		for (int i = 0; i < length; i++)
		{
			Node item = childNodes.item(i);
			if (item.getNodeName().equals(INSTANCE))
			{
				instance = item;
			}
			else if (item.getNodeName().equals(DEPENDENT_ATTRIBUTE))
			{
				dependentNode = item;
			}
		}

		for (int i = 0; i < length; i++)
		{
			Node item = childNodes.item(i);
			if (item.getNodeName().equals(ATTRIBUTE))
			{
				appendToStringBuilder(newLine);
				appendToStringBuilder("instance:");
				txInstance = txInstance(instance);
				appendToStringBuilder(",");
				appendToStringBuilder(txInstance);
				appendToStringBuilder(newLine);
				appendToStringBuilder(txInstance + ":");
				txSkipLogicAttribute(item);
				appendToStringBuilder(",dependentAttribute~");
				txSkipLogicDependent(dependentNode);
				appendToStringBuilder(newLine);
			}
		}
	}

	/**
	 * Tx skip logic dependent.
	 *
	 * @param dependentAttributeNode the dependent attribute node
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void txSkipLogicDependent(Node dependentAttributeNode) throws IOException
	{
		NodeList childNodes = dependentAttributeNode.getChildNodes();

		int length = childNodes.getLength();
		for (int i = 0; i < length; i++)
		{
			Node item = childNodes.item(i);
			if (item.getNodeName().equals(INSTANCE))
			{
				txInstance(item);
			}
			else if (item.getNodeName().equals(ATTRIBUTE))
			{
				txSkipLogicDependentAttribute(item);
			}
		}
	}

	/**
	 * Tx skip logic dependent attribute.
	 *
	 * @param item the item
	 *
	 * @throws DOMException the DOM exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void txSkipLogicDependentAttribute(Node item) throws DOMException, IOException
	{
		final String TRUE = "true";
		NamedNodeMap attributes = item.getAttributes();
		Node attributeName = attributes.getNamedItem("attributeName");
		appendToStringBuilder(":" + attributeName.getNodeValue());

		Node attributeName1 = attributes.getNamedItem("isSelectiveReadOnly");
		Node attributeName2 = attributes.getNamedItem("isShowHide");

		if (attributeName1.getNodeValue().equals(TRUE)
				|| attributeName2.getNodeValue().equals(TRUE))
		{
			appendToStringBuilder(",options~");

			if (attributeName1.getNodeValue().equals(TRUE))
			{
				appendToStringBuilder("IsSelectiveReadOnly" + ":" + attributeName1.getNodeValue());
			}
			else if (attributeName2.getNodeValue().equals(TRUE))
			{
				appendToStringBuilder("IsShowHide" + ":" + attributeName2.getNodeValue());
			}
		}

		Node subset = getSubset(item);

		if (subset != null)
		{
			NamedNodeMap attributes2 = subset.getAttributes();
			Node permissibleValueFile = attributes2.getNamedItem(PERMISSIBLE_VALUE_FILE);
			appendToStringBuilder(",Permissible_Values_File~");
			//	String readPermissibleValues = readPermissibleValues(permissibleValueFile
			//			.getNodeValue());
			appendToStringBuilder(permissibleValueFile.getNodeValue());
		}
	}

	/**
	 * Read permissible values.
	 *
	 * @param nodeValue the node value
	 *
	 * @return the string
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String readPermissibleValues(String nodeValue) throws IOException
	{
		StringBuilder permissibleValues = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(nodeValue));
		try
		{
			String readLine = bufferedReader.readLine();
			permissibleValues.append(readLine);
			while (readLine != null)
			{
				permissibleValues.append(":" + readLine);
				readLine = bufferedReader.readLine();
			}
		}
		finally
		{
			bufferedReader.close();
		}
		return permissibleValues.toString();
	}

	/**
	 * Gets the subset.
	 *
	 * @param item the item
	 *
	 * @return the subset
	 */
	private Node getSubset(Node item)
	{
		Node subset = null;
		NodeList childNodes = item.getChildNodes();
		int length = childNodes.getLength();

		for (int i = 0; i < length; i++)
		{
			Node item2 = childNodes.item(i);
			if (item2.getNodeName().equals("subset"))
			{
				subset = item2;
			}
		}
		return subset;
	}

	/**
	 * Tx skip logic attribute.
	 *
	 * @param skipLogicAttribute the skip logic attribute
	 */
	private void txSkipLogicAttribute(Node skipLogicAttribute)
	{
		NamedNodeMap attributes = skipLogicAttribute.getAttributes();
		Node attributeName = attributes.getNamedItem(ATTRIBUTE_NAME);
		Node value = attributes.getNamedItem(VALUE);
		appendToStringBuilder(attributeName.getNodeValue() + ":" + value.getNodeValue());
		/*else
		{
			Node subset = skipLogicAttribute.getFirstChild();
			if (subset != null)
			{
				NamedNodeMap skipLogicAttributeSubset = subset.getAttributes();

				Node permissibleValueFileAttribute = skipLogicAttributeSubset
						.getNamedItem(PERMISSIBLE_VALUE_FILE);
				String permissibleFileLocation = permissibleValueFileAttribute.getNodeValue();
				appendToStringBuilder(permissibleFileLocation);
			}
		}*/
	}

	/**
	 * Tx form.
	 *
	 * @param item the item
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void txForm(final Node item) throws IOException
	{

		final NamedNodeMap formProperties = item.getAttributes();

		final Node formName = formProperties.getNamedItem(FORM_NAME);
		final Node show = formProperties.getNamedItem(SHOW2);

		appendToStringBuilder(DISPLAY_LABEL + formName.getNodeValue() + ", show="
				+ show.getNodeValue() + newLine);

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
		appendToStringBuilder(newLine);
	}

	/**
	 * Tx single line display.
	 *
	 * @param item the item
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void txSingleLineDisplay(final Node item) throws IOException
	{
		final NodeList childNodes = item.getChildNodes();
		final int length = childNodes.getLength();
		appendToStringBuilder("SingleLineDisplay:start" + newLine);
		for (int i = 0; i < length; i++)
		{
			final Node item2 = childNodes.item(i);

			final String nodeName = item2.getNodeName();
			if (nodeName.equals(ATTRIBUTE))
			{
				txAttribute(item2);
			}
		}
		appendToStringBuilder("SingleLineDisplay:end" + newLine);
	}

	/**
	 * Tx instances.
	 *
	 * @param item2 the item2
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void txInstances(final Node item2) throws IOException
	{
		appendToStringBuilder("instance:");

		final NodeList childNodes = item2.getChildNodes();
		final int length = childNodes.getLength();

		for (int i = 0; i < length; i++)
		{
			final Node item = childNodes.item(i);
			final String nodeName = item.getNodeName();

			if (nodeName.equals(INSTANCE))
			{
				txInstance(item);
				appendToStringBuilder(newLine);
			}
		}
	}

	/**
	 * Tx instance.
	 *
	 * @param item2 the item2
	 *
	 * @return the string
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String txInstance(final Node item2) throws IOException
	{
		final String instance = item2.getFirstChild().getNodeValue();
		appendMainContainer(instance);
		appendToStringBuilder(instance);

		return instance;
	}

	/**
	 * Append main container.
	 *
	 * @param instance the instance
	 */
	private void appendMainContainer(final String instance)
	{
		if (mainContainer == null)
		{
			mainContainer = instance.substring(0, instance.lastIndexOf('['));
			int lastIndexOfentityGroupName = stringBuilder.lastIndexOf(newLine + entityGroupName
					+ newLine)
					+ entityGroupName.length() + 2;
			stringBuilder.insert(lastIndexOfentityGroupName, newLine + mainContainer + "~"
					+ mainContainer);
		}
	}

	/**
	 * Tx attribute.
	 *
	 * @param item the item
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void txAttribute(final Node item) throws IOException
	{

		//boolean isFirstUIProperty = false;

		final NamedNodeMap controlProperties = item.getAttributes();

		final Node className = controlProperties.getNamedItem(CLASS_NAME);
		appendToStringBuilder(className.getNodeValue() + ":");

		final Node attributeName = controlProperties.getNamedItem(ATTRIBUTE_NAME);
		appendToStringBuilder(attributeName.getNodeValue() + ",");

		final Node uiControlName = controlProperties.getNamedItem(UI_CONTROL);
		appendToStringBuilder(uiControlName.getNodeValue() + ",");

		final Node controlCaption = controlProperties.getNamedItem(CAPTION2);
		appendToStringBuilder(controlCaption.getNodeValue());

		final NodeList childNodes = item.getChildNodes();

		final int length = childNodes.getLength();

		for (int j = 0; j < length; j++)
		{
			final Node item2 = childNodes.item(j);
			final String nodeName = item2.getNodeName();

			appendUIProperty(item2, nodeName);
		}
		//isFirstUIProperty = false;
		isSecondUIProperty = false;
		appendSeparators();
		appendToStringBuilder(newLine);
	}

	/**
	 * Append ui property.
	 *
	 * @param isFirstUIProperty the is first ui property
	 * @param item2 the item2
	 * @param nodeName the node name
	 *
	 * @return true, if append ui property
	 *
	 * @throws DOMException the DOM exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void appendUIProperty(final Node item2, final String nodeName) throws DOMException,
	IOException
	{
		if (nodeName.equals(UI_PROPERTY))
		{
			txUIProperties(item2);
		}
	}

	/**
	 * Append separators.
	 *
	 * @param isFirstUIProperty the is first ui property
	 */
	private void appendSeparators()
	{
		//remove last ":"
		char charAt = stringBuilder.charAt(stringBuilder.length() - 1);
		if (charAt == ':')
		{
			stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
		}
		appendRequiredString();
		appendPermValueString();
		appendDefaultValueString();
	}

	/**
	 *
	 */
	private void appendRequiredString()
	{
		if (rulesRequiredString != null)
		{
			String localRequiredString = rulesRequiredString;
			rulesRequiredString = null;
			appendToStringBuilder(localRequiredString);
		}
	}

	/**
	 *
	 */
	private void appendDefaultValueString()
	{
		if (defaultValueString != null)
		{
			String localDefaultValueString = defaultValueString;
			defaultValueString = null;
			appendToStringBuilder(localDefaultValueString);
		}
	}

	/**
	 *
	 */
	private void appendPermValueString()
	{
		if (permValueOptionsString != null)
		{
			String localPermValueOptionsString = permValueOptionsString;
			permValueOptionsString = null;
			appendToStringBuilder(localPermValueOptionsString);
		}
	}

	/**
	 * Tx ui properties.
	 *
	 * @param item the item
	 *
	 * @throws DOMException the DOM exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void txUIProperties(final Node item) throws DOMException, IOException
	{
		if (!isFirstUIProperty)
		{
			isFirstUIProperty = true;
		}
		final NamedNodeMap controlProperties = item.getAttributes();

		final Node keyNode = controlProperties.getNamedItem(KEY);
		String nodeValue = keyNode.getNodeValue();

		if ("required".equals(nodeValue))
		{
			rulesRequiredString = ",Rules~required";
		}
		else if ("IsOrdered".equals(nodeValue))
		{
			final Node valueNode = controlProperties.getNamedItem(VALUE);
			permValueOptionsString = ",PermVal_Options~IsOrdered=" + valueNode.getNodeValue();
		}
		else if ("defaultValue".equals(nodeValue))
		{
			final Node valueNode = controlProperties.getNamedItem(VALUE);
			permValueOptionsString = ",defaultValue=" + valueNode.getNodeValue();
		}
		else
		{
			if (isFirstUIProperty && !isSecondUIProperty)
			{
				appendToStringBuilder(",options~");
				isSecondUIProperty = true;
			}
			appendToStringBuilder(nodeValue + "=");
			final Node valueNode = controlProperties.getNamedItem(VALUE);
			appendToStringBuilder(valueNode.getNodeValue());
			appendToStringBuilder(":");
		}
	}

	/**
	 * Tx related attributes.
	 *
	 * @param item the item
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void txRelatedAttributes(final Node item) throws IOException
	{
		Node instanceNode = null;
		Node uiPropertyNode = null;
		appendToStringBuilder("RelatedAttribute:" + newLine);

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
	 * Tx ra in required csv order.
	 *
	 * @param instanceNode the instance node
	 * @param uiPropertyNode the ui property node
	 * @param attributes the attributes
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws DOMException the DOM exception
	 */
	private void txRAInRequiredCSVOrder(final Node instanceNode, final Node uiPropertyNode,
			final NamedNodeMap attributes) throws IOException, DOMException
			{
		if (instanceNode != null)
		{
			txInstances(instanceNode);
		}

		final Node classeName = attributes.getNamedItem(CLASS_NAME);
		appendToStringBuilder(classeName.getNodeValue() + ":");

		final Node attributeName = attributes.getNamedItem(ATTRIBUTE_NAME);
		appendToStringBuilder(attributeName.getNodeValue() + "=");

		final Node relatedAttributeValue = attributes.getNamedItem(VALUE);
		appendToStringBuilder(relatedAttributeValue.getNodeValue());

		if (uiPropertyNode != null)
		{
			appendToStringBuilder(",options~");
			isFirstUIProperty = false;
			txUIProperties(uiPropertyNode);
		}
			}
}