
package edu.wustl.metadata.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author mandar_shidhore
 *
 */
public class CacoreDEMetadataAppender
{

	private static final String MAPPING = "mapping";
	private static final String SESSION_FACTORY = "session-factory";
	private static final String RESOURCE = "resource";

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String deHibernateCfgFilePath = null;
		String cacoreHibernateCfgFilePath = null;
		String tempFilePath = null;

		if (args.length != 2)
		{
			throw new RuntimeException("Please specify paths for hibernate configuration files!");
		}

		System.out.println("\nDynamicExtensionsHibernate.cfg.xml file path = " + args[0]);
		System.out.println("caCORE generated orm1.cfg.xml file path = " + args[1] +"\n");

		try
		{
			deHibernateCfgFilePath = args[0];
			tempFilePath = args[0];
			Document deHibCfg = getDocumentObjectForXML(deHibernateCfgFilePath);
			NodeList deHibCfgNodes = deHibCfg.getElementsByTagName(SESSION_FACTORY);
			Node deHibCfgNode = deHibCfgNodes.item(0);

			// Get the original resource mappings from 'DynamicExtensionsHibernate.cfg.xml' file.
			List<String> origDEResrcMappings = getOrigDEResourceMappings(deHibCfgNode);

			cacoreHibernateCfgFilePath = args[1];
			tempFilePath = args[1];
			Document cacoreGenHibCfg = getDocumentObjectForXML(cacoreHibernateCfgFilePath);
			NodeList cacoreGenHibCfgNodes = cacoreGenHibCfg.getElementsByTagName(SESSION_FACTORY);
			Node cacoreGenHibCfgNode = cacoreGenHibCfgNodes.item(0);

			NodeList cacoreGenHibCfgChildNodes = cacoreGenHibCfgNode.getChildNodes();
			for (int i = 0; i < cacoreGenHibCfgChildNodes.getLength(); i++)
			{
				Node node = cacoreGenHibCfgChildNodes.item(i);
				if (node.hasAttributes())
				{
					Node innerNode = node.getAttributes().item(0);
					if (MAPPING.equals(node.getNodeName()))
					{
						addNodes(origDEResrcMappings, deHibCfgNode, deHibCfg, innerNode
								.getNodeValue());
					}
				}
			}

			//printDEResourceMappings(deHibCfgNode);

			writeConfigurationFile(deHibernateCfgFilePath, deHibCfg);
		}
		catch (SAXException e)
		{
			throw new RuntimeException("Exception while reading file " + tempFilePath);
		}
		catch (IOException e)
		{
			throw new RuntimeException("Exception while reading file " + tempFilePath);
		}
	}

	/**
	 * @param existingDEResourceMappings
	 * @param deHibCfgNode
	 * @param deHibCfg
	 * @param nodeValue
	 */
	private static void addNodes(List<String> existingDEResourceMappings, Node deHibCfgNode,
			Document deHibCfg, String nodeValue)
	{
		if (!existingDEResourceMappings.contains(nodeValue))
		{
			Element mappingNode = deHibCfg.createElement(MAPPING);
			mappingNode.setAttribute(RESOURCE, nodeValue);
			deHibCfgNode.appendChild(mappingNode);
		}
	}

	/**
	 * This method returns the list of original DE mappings.
	 * @param node
	 * @return
	 */
	private static List<String> getOrigDEResourceMappings(Node node)
	{
		List<String> resourceMappings = new ArrayList<String>();

		NodeList propertyNodes = node.getChildNodes();
		for (int i = 0; i < propertyNodes.getLength(); i++)
		{
			Node property = propertyNodes.item(i);
			if (property.hasAttributes())
			{
				Node inner = property.getAttributes().item(0);
				if (MAPPING.equals(property.getNodeName()))
				{
					resourceMappings.add(inner.getNodeValue());
				}
			}
		}

		return resourceMappings;
	}

	/**
	 * @param xmlFile
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	private static Document getDocumentObjectForXML(String xmlFile) throws SAXException,
			IOException
	{
		DOMParser parser = new DOMParser();

		// Ignore DTD URL : without this we get connection timeout
		// error as the application tries to access the URL.
		parser.setEntityResolver(new DtdResolver());
		parser.parse(new InputSource(xmlFile.toString()));

		Document document = parser.getDocument();

		return document;
	}

	/**
	 * Method writes the changes to the configuration file
	 * @param cfgFile
	 * @param cfgDocument
	 */
	private static void writeConfigurationFile(String cfgFile, Document cfgDocument)
	{
		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
					"-//Hibernate/Hibernate Configuration DTD 3.0//EN");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
					"http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd");

			// Initialize StreamResult with file object to save to file.
			StreamResult result = new StreamResult(new FileWriter(cfgFile));
			DOMSource source = new DOMSource(cfgDocument);

			transformer.transform(source, result);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}

class DtdResolver implements EntityResolver
{

	public InputSource resolveEntity(String publicId, String systemId) throws SAXException,
			IOException
	{
		if (systemId.contains("http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd"))
		{
			return new InputSource(new StringReader(""));
		}
		else if (systemId.contains("http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd"))
		{
			return new InputSource(new StringReader(""));
		}
		else
		{
			return null;
		}
	}

}
