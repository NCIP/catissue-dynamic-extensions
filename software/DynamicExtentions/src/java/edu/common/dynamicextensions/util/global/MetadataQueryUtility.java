
package edu.common.dynamicextensions.util.global;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;

/**
 * @author kunal_kamble
 * This class is used for generating csv for the queries executed from metadataQuery.xml
 *
 */
public class MetadataQueryUtility
{

	/**
	 * start of the parameter list in the args[]
	 */
	private static final int PARAMETER_VALUE_START = 2;

	/**
	 * SLQ query
	 */
	private String query;

	/**
	 * Parameter values to be replaced in the query
	 */
	private List<String> paramValueList;

	/**
	 * metadataQuery.xml path
	 */
	private final String metadataQueryFilepath;

	/**
	 * Header to be added in the csv
	 */
	private List<String> header;

	/**
	 * Parameter name list from the sql
	 */
	private List<String> paramNameList;

	/**
	 * JDBC DAO for executing query
	 */
	private final JDBCDAO jdbcdao;

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	/**
	 * For logging
	 */
	private static final Logger LOGGER = Logger.getCommonLogger(MetadataQueryUtility.class);

	/**
	 * @return paramValueList paramValueList
	 */
	public List<String> getParamValueList()
	{
		return paramValueList;
	}

	/**
	 * @param paramValueList paramValueList
	 */
	public void setParamValueList(List<String> paramValueList)
	{
		this.paramValueList = paramValueList;
	}

	/**
	 * @return paramNameList
	 */
	public final List<String> getParamNameList()
	{
		return paramNameList;
	}

	/**
	 * @param paramNameList paramNameList
	 */
	public final void setParamNameList(List<String> paramNameList)
	{
		this.paramNameList = paramNameList;
	}

	/**
	 * @param queryXmlPath metadataQuery.xml path
	 * @param queryName query name
	 * @throws DynamicExtensionsSystemException
	 */
	public MetadataQueryUtility(String queryXmlPath, String queryName)
			throws DynamicExtensionsSystemException
	{
		metadataQueryFilepath = queryXmlPath;
		init(queryName);
		jdbcdao = DynamicExtensionsUtility.getJDBCDAO();
	}

	/**
	 * @param args
	 * @throws DAOException
	 * @throws FileNotFoundException
	 */
	public void generateCSV(String[] args) throws DAOException, FileNotFoundException
	{
		initParamValues(args);
		replaceTokens();
		LOGGER.info(query);

		List resultSet = jdbcdao.executeQuery(query);
		String fileName = "metaQuery.csv";
		PrintWriter printWriter = new PrintWriter(fileName);
		for (int i = 0; i < header.size(); i++)
		{
			printWriter.print(header.get(i));
			printWriter.print(",");
		}
		printWriter.print("\n");
		for (Object object : resultSet)
		{
			for (Object object2 : (List) object)
			{
				printWriter.print(object2.toString());
				printWriter.print(",");

			}
			printWriter.print("\n");

		}
		printWriter.close();
	}

	/**
	 * @param args args[0] - metadataQuery.xml path
	 * args[1] - query to be executed
	 * arguments 3rd onwards is the list of parameter value list to be rapleced in the query
	 * @throws DAOException
	 * @throws FileNotFoundException
	 * @throws DynamicExtensionsSystemException
	 */
	public static void main(String[] args) throws DAOException, FileNotFoundException,
			DynamicExtensionsSystemException

	{
		/*args = new String[]{"query1", "NeuroOncPeds", "NeuroOncPedsRelapseProgression"};*/
		/*for (String string : args)
		{
			System.out.println(string);
		}*/
		MetadataQueryUtility metadataQueryUtility = new MetadataQueryUtility(args[0], args[1]);
		metadataQueryUtility.generateCSV(args);
		/*String query = "select temp2.Table_Name ,temp2.Class_Name, temp2.attribute_name,db.name column_name "
				+ "from DYEXTN_DATABASE_PROPERTIES db join "
				+ "(select temp1.Table_Name ,temp1.Class_Name, cols.IDENTIFIER,temp1.attribute_name "
				+ "from DYEXTN_COLUMN_PROPERTIES cols join "
				+ "(select m3.Table_Name ,m3.Class_Name,m2.identifier attribute_Id,m1.NAME attribute_name "
				+ "from DYEXTN_ABSTRACT_METADATA m1 ,DYEXTN_ATTRIBUTE  m2, "
				+ "(select MT.identifier, PROP.name Table_Name, MT.name Class_Name "
				+ "from DYEXTN_DATABASE_PROPERTIES PROP, DYEXTN_TABLE_PROPERTIES tp, DYEXTN_ABSTRACT_METADATA MT "
				+ "where tp.ABSTRACT_ENTITY_ID = MT.IDENTIFIER and tp.IDENTIFIER = prop.IDENTIFIER "
				+ "and MT.identifier in (select identifier from dyextn_entity where ENTITY_GROUP_ID = "
				+ "(select identifier from DYEXTN_ENTITY_GROUP where  long_name = '@entityGroupName@')) "
				+ "and MT.name like '@entityName@') m3 "
				+ "where m1.IDENTIFIER =  m2.IDENTIFIER  and m2.ENTIY_ID = m3.identifier)temp1 "
				+ "on cols.PRIMITIVE_ATTRIBUTE_ID = temp1.attribute_Id "
				+ ") temp2 "
				+ "on db.identifier = temp2.identifier ";
		System.out.println(query);*/

	}

	/**
	 * Replaces the tokens from the query with the parameters passed through ant task
	 */
	private void replaceTokens()
	{
		for (int i = 0; i < paramNameList.size(); i++)
		{
			query = query.replace(paramNameList.get(i), paramValueList.get(i));
		}

	}

	/**
	 * @param strings parameter values to be set
	 * initializes the parameter values from arguments passed through  the ant task
	 */
	public final void initParamValues(String[] strings)
	{
		paramValueList = new ArrayList<String>();
		for (int i = PARAMETER_VALUE_START; i < strings.length; i++)
		{
			if (strings[i].startsWith("${"))
			{
				paramValueList.add("%%");

			}
			else
			{
				paramValueList.add(strings[i]);
			}
		}
	}

	/**
	 * @param parentNode query node
	 * initializes the parameter names list from the comma separated list given in the metadataQuery.xml
	 */
	private void initParamNames(Node parentNode)
	{
		paramNameList = new ArrayList<String>();
		populateList(paramNameList, parentNode, "paramNames");
	}

	/**
	 * @param parentNode query node
	 */
	private void initRowHeader(Node parentNode)
	{
		header = new ArrayList<String>();
		populateList(header, parentNode, "rowHeader");
	}

	/**
	 * @param header header list
	 * @param parentNode query node
	 * @param tagName header tag
	 */
	private void populateList(List<String> header, Node parentNode, String tagName)
	{
		for (String string : getValueByTagName((Element) parentNode, tagName).split(","))
		{
			header.add(string);
		}
	}

	/**
	 * @param queryName query to be executed
	 * @throws DynamicExtensionsSystemException
	 */
	private void init(String queryName) throws DynamicExtensionsSystemException
	{
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;

		try
		{
			docBuilder = docBuilderFactory.newDocumentBuilder();

			Document doc = docBuilder.parse(new File(metadataQueryFilepath));
			// normalize text representation
			doc.getDocumentElement().normalize();
			NodeList listOfTemplate = doc.getElementsByTagName("metadataQuery");
			for (int s = 0; s < listOfTemplate.getLength(); s++)
			{
				Node firstTemplateNode = listOfTemplate.item(s);
				if (firstTemplateNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element firstTemplateElement = (Element) firstTemplateNode;
					NodeList queryNodeList = firstTemplateElement.getElementsByTagName("query");
					for (int s1 = 0; s1 < queryNodeList.getLength(); s1++)
					{
						Node queryNode = queryNodeList.item(s1);
						((Element) queryNode).getAttribute("name");
						if (queryNode.getNodeType() == Node.ELEMENT_NODE
								&& queryName.equalsIgnoreCase(((Element) queryNode)
										.getAttribute("name")))
						{
							initQuery(queryNode);
							initRowHeader(queryNode);
							initParamNames(queryNode);

						}
					}

				}

			}
		}
		catch (ParserConfigurationException e)
		{
			throw new DynamicExtensionsSystemException("Error while metdataQuery.xml", e);
		}
		catch (SAXException e)
		{
			throw new DynamicExtensionsSystemException("Error while metdataQuery.xml", e);
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException("Error while metdataQuery.xml", e);
		}

	}

	/**
	 * @param queryNode query Node
	 */
	private void initQuery(Node queryNode)
	{
		query = getValueByTagName((Element) queryNode, "queryString");

	}

	/**
	 * @param firstTemplateElement tag element
	 * @param tagName name of the tag
	 * @return value for the given tag
	 */
	public static String getValueByTagName(Element firstTemplateElement, String tagName)
	{
		NodeList xcelFilePathList = firstTemplateElement.getElementsByTagName(tagName);
		Element xcelFileElement = (Element) xcelFilePathList.item(0);

		NodeList textFNList = xcelFileElement.getChildNodes();
		String nodeValue = null;
		if (textFNList.item(0) != null)
		{
			nodeValue = textFNList.item(0).getNodeValue();
			if (nodeValue != null)
			{
				nodeValue = nodeValue.trim();
			}
		}

		return nodeValue;

	}

}
