package edu.wustl.metadata.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
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

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;

/**
 *
 * @author mandar_shidhore
 *
 */
public class CacoreDEMetadataAppender {

    private static final String MAPPING = "mapping";

    private static final String SESSION_FACTORY = "session-factory";

    private static final String RESOURCE = "resource";

    /**
     * @param args
     * @throws DynamicExtensionsApplicationException
     */
    public static void main(final String[] args) throws DynamicExtensionsApplicationException {
        final CacoreDEMetadataAppender cacoreDEMetadata = new CacoreDEMetadataAppender();

        if (args.length != 2) {
            throw new DynamicExtensionsApplicationException(
                    "Please specify paths for hibernate configuration files!");
        }
        String tempFilePath = "";

        try {
            final String deHibCfgFile = args[0];
            tempFilePath = args[0];
            final Document deHibCfg = cacoreDEMetadata.getDocumentObjectForXML(deHibCfgFile);
            final NodeList deHibCfgNodes = deHibCfg.getElementsByTagName(SESSION_FACTORY);
            final Node deHibCfgNode = deHibCfgNodes.item(0);

            // Get the original resource mappings from 'DynamicExtensionsHibernate.cfg.xml' file.
            final List<String> deResrcMappings = cacoreDEMetadata.getOrigDEResourceMappings(deHibCfgNode);

            final String cacoreHibCfg = args[1];
            tempFilePath = args[1];
            final Document cacoreGenHibCfg = cacoreDEMetadata.getDocumentObjectForXML(cacoreHibCfg);
            final NodeList cacoreHibCfgNodes = cacoreGenHibCfg.getElementsByTagName(SESSION_FACTORY);
            final Node cacoreHibCfgNode = cacoreHibCfgNodes.item(0);

            final NodeList cacoreCfgChdNodes = cacoreHibCfgNode.getChildNodes();
            for (int i = 0; i < cacoreCfgChdNodes.getLength(); i++) {
                final Node node = cacoreCfgChdNodes.item(i);
                if (node.hasAttributes()) {
                    final Node innerNode = node.getAttributes().item(0);
                    if (MAPPING.equals(node.getNodeName())) {
                        cacoreDEMetadata.addNodes(deResrcMappings, deHibCfgNode, deHibCfg,
                                                  innerNode.getNodeValue());
                    }
                }
            }
            //printDEResourceMappings(deHibCfgNode);
            cacoreDEMetadata.writeConfigurationFile(deHibCfgFile, deHibCfg);
        } catch (SAXException e) {
            throw new DynamicExtensionsApplicationException("Exception while reading file " + tempFilePath, e);
        } catch (IOException e) {
            throw new DynamicExtensionsApplicationException("Exception while reading file " + tempFilePath, e);
        } catch (TransformerException e) {
            throw new DynamicExtensionsApplicationException("Exception while reading file " + tempFilePath, e);
        }
    }

    /**
     * @param existDEMappings
     * @param deHibCfgNode
     * @param deHibCfg
     * @param nodeValue
     */
    private void addNodes(final List<String> existDEMappings, final Node deHibCfgNode, final Document deHibCfg,
                          final String nodeValue) {
        if (!existDEMappings.contains(nodeValue)) {
            final Element mappingNode = deHibCfg.createElement(MAPPING);
            mappingNode.setAttribute(RESOURCE, nodeValue);
            deHibCfgNode.appendChild(mappingNode);
        }
    }

    /**
     * This method returns the list of original DE mappings.
     * @param node
     * @return
     */
    private List<String> getOrigDEResourceMappings(final Node node) {
        final List<String> resourceMappings = new ArrayList<String>();

        final NodeList propertyNodes = node.getChildNodes();
        for (int i = 0; i < propertyNodes.getLength(); i++) {
            final Node property = propertyNodes.item(i);
            if (property.hasAttributes()) {
                final Node innerNode = property.getAttributes().item(0);
                if (MAPPING.equals(property.getNodeName())) {
                    resourceMappings.add(innerNode.getNodeValue());
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
    private Document getDocumentObjectForXML(final String xmlFile) throws SAXException, IOException {
        final DOMParser parser = new DOMParser();

        // Ignore DTD URL : without this we get connection timeout
        // error as the application tries to access the URL.
        parser.setEntityResolver(new DtdResolver());
        parser.parse(new InputSource(xmlFile));

        final Document document = parser.getDocument();

        return document;
    }

    /**
     * Method writes the changes to the configuration file
     * @param cfgFile
     * @param cfgDocument
     * @throws TransformerException
     * @throws IOException
     */
    private void writeConfigurationFile(final String cfgFile, final Document cfgDocument)
            throws TransformerException, IOException {
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
                                      "-//Hibernate/Hibernate Configuration DTD 3.0//EN");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
                                      "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd");

        // Initialize StreamResult with file object to save to file.
        final StreamResult result = new StreamResult(new FileWriter(cfgFile));
        final DOMSource source = new DOMSource(cfgDocument);
        transformer.transform(source, result);
    }
}

class DtdResolver implements EntityResolver {

    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
        InputSource inputSource = null;

        final String hibernateConfig = "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd";
        final String hibernateMapping = "http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd";

        if (systemId.contains(hibernateConfig) || systemId.contains(hibernateMapping)) {
            inputSource = new InputSource(new StringReader(""));
        }
        return inputSource;
    }

}
