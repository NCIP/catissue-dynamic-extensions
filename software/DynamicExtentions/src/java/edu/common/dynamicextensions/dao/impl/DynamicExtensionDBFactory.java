
package edu.common.dynamicextensions.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.common.dynamicextensions.entitymanager.DynamicExtensionBaseQueryBuilder;
import edu.wustl.common.util.global.XMLParserUtility;
import edu.wustl.common.util.logger.Logger;

/**
 * @author ravi_kumar
 *
 */
public final class DynamicExtensionDBFactory
{

	/**
	 * Map to store DEServiceGroup for all database type.
	 */
	private static Map<String, DynamicExtensionDBGroup> mapDbTypeVsDbServiceGroup;

	/**
	 * instance of DEServiceLocator.
	 */
	private static final DynamicExtensionDBFactory DE_DB_FACTORY = new DynamicExtensionDBFactory();

	/**
	 * File name for DE utility configuration.
	 */
	private static final String DB_UTIL_FACTORY_FILE_NAME = "DynamicExtensionDBFactory.xml";

	/**
	 * Element tag name for 'dbType'.
	 */
	private static final String ELE_DB_TYPE = "dbType";

	/**
	 * Element tag name for 'QueryBuilder'.
	 */
	private static final String ELE_QUERY_BUILDER = "QueryBuilder";

	/**
	 * Element tag name for 'DBUtil'.
	 */
	private static final String ELE_DB_UTIL = "DBUtil";

	/**
	 * Element tag name for  'DataTypeMappingFile' .
	 */
	private static final String ELE_DATA_TYPE_MAPPING_FILE = "DataTypeMappingFile";

	/**
	 * Attribute name for 'type'.
	 */
	private static final String ATT_TYPE = "type";

	/**
	 * Attribute name for 'className'.
	 */
	private static final String ATT_CLASS_NAME = "className";

	/**
	 * Attribute name for 'fileName'.
	 */
	private static final String ATT_FILE_NAME = "fileName";

	/**
	 *
	 * @return instance of DEServiceLocator.
	 */
	public static DynamicExtensionDBFactory getInstance()
	{
		return DE_DB_FACTORY;
	}

	/**
	 * @param dbType database type.
	 * @return DynamicExtensionBaseQueryBuilder
	 */
	public DynamicExtensionBaseQueryBuilder getQueryBuilder(String dbType)
	{
		return (mapDbTypeVsDbServiceGroup.get(dbType.toLowerCase())).getQueryBuilder();
	}

	/**
	 *
	 * @param dbType database type.
	 * @return DEDBUtility object.
	 */
	public IDEDBUtility getDbUtility(String dbType)
	{
		return (mapDbTypeVsDbServiceGroup.get(dbType.toLowerCase())).getDbUtility();
	}

	/**
	 *
	 * @param dbType database type.
	 * @return String Data Type Mapping File
	 */
	public String getDataTypeMappingFile(String dbType)
	{
		return (mapDbTypeVsDbServiceGroup.get(dbType.toLowerCase()))
				.getDataTypeMappingFile();
	}

	/**
	 * Private constructor.
	 */
	private DynamicExtensionDBFactory()
	{
		try
		{
			init();
		}
		catch (Exception e)
		{
			Logger.out.debug(e.getMessage());
		}

	}

	/**
	 * Method to initialize map.
	 * @throws ParserConfigurationException throws this exception if DocumentBuilderFactory not created.
	 * @throws IOException throws this exception if file not found.
	 * @throws SAXException throws this exception if not able to parse XML file.
	 * @throws InstantiationException if not able to create instance.
	 * @throws IllegalAccessException if not able to create instance.
	 * @throws ClassNotFoundException if class not found.
	 */
	private void init() throws ParserConfigurationException, SAXException, IOException,
			InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(
				DB_UTIL_FACTORY_FILE_NAME);
		Document document = XMLParserUtility.getDocument(inputStream);
		NodeList dbUtilityNodeLst = document.getElementsByTagName(ELE_DB_TYPE);
		populateMap(dbUtilityNodeLst);
	}

	/**
	 * This method Populate map.
	 * @param dbUtilityNodeLst Node list for all database.
	 * @throws InstantiationException if not able to create instance.
	 * @throws IllegalAccessException if not able to create instance.
	 * @throws ClassNotFoundException if class not found.
	 */
	private void populateMap(NodeList dbUtilityNodeLst) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException
	{
		Node dbUtilityNode;
		int noOfNode=dbUtilityNodeLst.getLength();
		mapDbTypeVsDbServiceGroup = new HashMap<String, DynamicExtensionDBGroup>();
		for (int s = 0; s < noOfNode; s++)
		{
			dbUtilityNode = dbUtilityNodeLst.item(s);
			if (dbUtilityNode.getNodeType() == Node.ELEMENT_NODE)
			{
				addNewPrivilegeToMap(dbUtilityNode);
			}
		}
	}

	/**
	 * This method insert values in map.
	 * @param dbUtilityNode node for specific database.
	 * @throws InstantiationException if not able to create instance.
	 * @throws IllegalAccessException if not able to create instance.
	 * @throws ClassNotFoundException if class not found.
	 */
	private void addNewPrivilegeToMap(Node dbUtilityNode) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException
	{
		Element dbTypeNode = (Element) dbUtilityNode;
		String dbType = dbTypeNode.getAttribute(ATT_TYPE);
		String queryBuilderClassName = getClassName(dbTypeNode, ELE_QUERY_BUILDER, ATT_CLASS_NAME);
		String dbUtilityClasName = getClassName(dbTypeNode, ELE_DB_UTIL, ATT_CLASS_NAME);
		String dataTypeMappingFile = getClassName(dbTypeNode, ELE_DATA_TYPE_MAPPING_FILE,
				ATT_FILE_NAME);
		DynamicExtensionBaseQueryBuilder queryBuilder = (DynamicExtensionBaseQueryBuilder) Class
				.forName(queryBuilderClassName).newInstance();
		IDEDBUtility dbUtility = (IDEDBUtility) Class.forName(dbUtilityClasName).newInstance();
		DynamicExtensionDBGroup dEServiceGroup = new DynamicExtensionDBGroup(queryBuilder,
				dbUtility, dataTypeMappingFile);
		mapDbTypeVsDbServiceGroup.put(dbType.toLowerCase(), dEServiceGroup);
	}

	/**
	 * this method returns Attribute-value of an element.
	 * @param dbTypeNode node corresponding to an element
	 * @param eleName Element name
	 * @param attName Attribute name.
	 * @return Attribute-value of an element.
	 */
	private String getClassName(Element dbTypeNode, String eleName, String attName)
	{
		Node node = (dbTypeNode.getElementsByTagName(eleName)).item(0);
		Element nodeElement = (Element) node;
		return nodeElement.getAttribute(attName);

	}
}
