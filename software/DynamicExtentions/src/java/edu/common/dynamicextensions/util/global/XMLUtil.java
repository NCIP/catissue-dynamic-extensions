
package edu.common.dynamicextensions.util.global;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.common.dynamicextensions.category.CategoryCreator;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * This class merges given to xml
 * @author kunal_kamble
 */
public class XMLUtil
{

	private static final String MERGE_ERRROR_MESSAGE = "Error in merging xml files.";
	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	private static final Logger LOGGER = Logger.getCommonLogger(CategoryCreator.class);

	/**
	 * for writing merged file
	 */
	private static PrintWriter printWriter;

	public static void main(String[] args) throws Exception
	{
		File file1 = new File(args[0]);
		File file2 = new File(args[1]);
		XMLUtil.printWriter = new PrintWriter(args[2]);

		LOGGER.info("File 1:" + args[0]);
		LOGGER.info("File 2:" + args[1]);
		LOGGER.info("Merged file :" + args[2]);
		Document doc = merge("/AuditableMetaData", file1, file2);
		print(doc);
		printWriter.close();
	}

	/**
	 * @param expression
	 * @param files
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws Exception
	 */
	private static Document merge(String expression, File... files)
			throws DynamicExtensionsSystemException
	{
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		XPathExpression compiledExpression;
		try
		{
			compiledExpression = xpath.compile(expression);
		}
		catch (XPathExpressionException e)
		{
			throw new DynamicExtensionsSystemException(MERGE_ERRROR_MESSAGE, e);
		}
		return merge(compiledExpression, files);
	}

	/**
	 * @param expression
	 * @param files
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	private static Document merge(XPathExpression expression, File... files)
			throws DynamicExtensionsSystemException
	{
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder docBuilder;
		Document base;
		try
		{
			docBuilder = docBuilderFactory.newDocumentBuilder();
			base = docBuilder.parse(files[0]);
			Node results = (Node) expression.evaluate(base, XPathConstants.NODE);
			List<String> existingClassNames = getExistingClassNames(results);
			if (results == null)
			{
				throw new DynamicExtensionsSystemException(files[0]
						+ ": expression does not evaluate to node");
			}

			for (int i = 1; i < files.length; i++)
			{
				Document merge = docBuilder.parse(files[i]);
				Node nextResults = (Node) expression.evaluate(merge, XPathConstants.NODE);
				while (nextResults.hasChildNodes())
				{
					appendChild(base, results, existingClassNames, nextResults);

				}
			}
		}
		catch (XPathExpressionException e)
		{
			throw new DynamicExtensionsSystemException(MERGE_ERRROR_MESSAGE, e);
		}
		catch (SAXException e)
		{
			throw new DynamicExtensionsSystemException(MERGE_ERRROR_MESSAGE, e);
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException(MERGE_ERRROR_MESSAGE, e);
		}
		catch (ParserConfigurationException e)
		{
			throw new DynamicExtensionsSystemException(MERGE_ERRROR_MESSAGE, e);
		}
		return base;
	}

	/**
	 * @param base
	 * @param results
	 * @param existingClassNames
	 * @param nextResults
	 */
	private static void appendChild(Document base, Node results, List<String> existingClassNames,
			Node nextResults)
	{
		Node kid = nextResults.getFirstChild();
		String className = kid.getAttributes() == null ? "" : kid.getAttributes().getNamedItem(
				"className").getNodeValue();
		nextResults.removeChild(kid);
		kid = base.importNode(kid, true);
		if (!existingClassNames.contains(className))
		{
			results.appendChild(kid);
		}
	}

	/**
	 * @param base
	 * @return
	 */
	private static List<String> getExistingClassNames(Node base)
	{
		NodeList nodeList = base.getChildNodes();
		List<String> classNameList = new ArrayList<String>();
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Node node = nodeList.item(i);
			if ("AuditableClass".equals(node.getNodeName()))
			{
				classNameList.add(node.getAttributes().getNamedItem("className").getNodeValue());
			}
		}
		return classNameList;
	}

	/**
	 * @param doc
	 * @throws DynamicExtensionsSystemException
	 */
	private static void print(Document doc) throws DynamicExtensionsSystemException
	{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		try
		{
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			Result result = new StreamResult(printWriter);
			transformer.transform(source, result);
		}
		catch (TransformerConfigurationException e)
		{
			throw new DynamicExtensionsSystemException("Error writing merged file.", e);
		}
		catch (TransformerException e)
		{
			throw new DynamicExtensionsSystemException("Error writing merged file.", e);
		}
	}

}
