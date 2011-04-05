
package edu.common.dynamicextensions.messages;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.common.dynamicextensions.domain.AutoLoadXpath;
import edu.common.dynamicextensions.domain.CategoryEntity;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.Variables;
import edu.common.dynamicextensions.xmi.XMIConstants;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.exception.DAOException;

/**
 * The class is responsible for finding the Forms in which the data from XMLMessage (Lab message) will go.
 * Also actually inserting the data in the Choosen form also.
 * @author pavan_kalantri
 *
 */
public class XmlMessageProcessor
{

	/**
	 *contsructor
	 */
	protected XmlMessageProcessor()
	{
		super();
	}

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	/**
	 * logger object
	 */
	private static final Logger LOGGER = Logger.getCommonLogger(XmlMessageProcessor.class);

	/**
	 * self object to maintain the singleton class
	 */
	private static XmlMessageProcessor processor;

	/**
	 * Properties used during the processing loaded at instantiation time.
	 */

	/**
	 * Returns the instance of the XmlMessageProcessor.
	 * @return processor singleton instance of the XmlMessageProcessor.
	 */
	public static synchronized XmlMessageProcessor getInstance()

	{
		if (processor == null)
		{
			processor = new XmlMessageProcessor();
		}
		return processor;
	}

	/**
	 * This method will find out the subset of the container id collection from the given collection
	 * in which the data from the given document object will go depending on the concept codes.
	 * @param messageXml collection of the concept codes for which to search form Context.
	 * @param containerIdColl original container id collection from which to find out matching containers.
	 * @return collection of container ids in which the data entry is possible for this document.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	public Collection<Long> getContainerIdCollectionForMessage(String messageXml,
			Collection<Long> containerIdColl) throws DynamicExtensionsSystemException
	{
		Collection<Long> matchedContainerIdColl = new HashSet<Long>();
		try
		{
			Document xmlDoc = getDocumentObject(messageXml);
			for (Long containerId : containerIdColl)
			{
				List<AutoLoadXpath> autoLoadXpathColl = getAutoloadXpathCollForContainerId(containerId);

				//Get the document from the XmlMessage String

				for (AutoLoadXpath autoLoadXpath : autoLoadXpathColl)
				{
					//get the Identifying  XPath for message
					String identifyingXPath = autoLoadXpath.getXpath();
					//Get the concept codes from Messages
					Collection<String> messageConceptCodeColl = getAllValuesForXpath(xmlDoc,
							identifyingXPath);
					if (!messageConceptCodeColl.isEmpty())
					{
						//If a match is found/ or form associated concept collection is empty, it's a valid form for data entry

						//Get the Associated Concept code collection
						Collection<String> conceptCodeColl = autoLoadXpath
								.getConceptCodeCollection();
						if (conceptCodeColl.isEmpty())
						{
							//Form is valid
							matchedContainerIdColl.add(containerId);
						}
						else
						{
							//check if  any of the concept code is matching, if none then form is not
							//valid for data entry
							for (String conceptCode : conceptCodeColl)
							{
								if (messageConceptCodeColl.contains(conceptCode))
								{
									//If concept code is matched ,thats' mean form is valid for data entry
									matchedContainerIdColl.add(containerId);
									break;
								}
							}
						}
					}
				}
			}

		}
		catch (DAOException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue("message.loading.insertdata.error"), e);
		}

		return matchedContainerIdColl;
	}

	/**
	 * This method will search the nodes from the given document at the given xpath & returns it as nodeList.
	 * @param document document in which to search for the xpath.
	 * @param xpath xpath expression to search for.
	 * @return nodelist on that xpath.
	 * @throws XPathExpressionException exception.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	private NodeList getNodeListForXpath(Document document, String xpath)
			throws XPathExpressionException, DynamicExtensionsSystemException
	{
		// returns the node list for the given xpath.
		if (xpath == null || "".equals(xpath.trim()))
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue("message.loading.noxpath.error"));
		}
		XPath xPath = XPathFactory.newInstance().newXPath();
		XPathExpression xPathExpression = xPath.compile(xpath);
		NodeList xpathNodeList = (NodeList) xPathExpression.evaluate(document,
				XPathConstants.NODESET);
		if (xpathNodeList == null || xpathNodeList.getLength() == 0)
		{
			LOGGER.info("No node found for the given Xpath " + xpath);
		}
		return xpathNodeList;
	}

	/**
	 * This method will return the collection of values in the txt of the node mentioned by the xpath.
	 * @param document document in which to search for the xpath.
	 * @param xpath xpath expression.
	 * @return collection of values at the xpath.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	private Collection<String> getAllValuesForXpath(Document document, String xpath)
			throws DynamicExtensionsSystemException
	{
		Collection<String> xpathValueCollection = new HashSet<String>();

		// search all the nodes for the given xpath
		NodeList xpathNodeList;
		try
		{
			xpathNodeList = getNodeListForXpath(document, xpath);
		}
		catch (XPathExpressionException e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(
					"message.loading.xpath.evaluation.error", xpath), e);
		}
		// collect the values of each node.
		for (int i = 0; i < xpathNodeList.getLength(); i++)
		{
			Node aNode = xpathNodeList.item(i);
			addChildNodeValue(xpathValueCollection, aNode);

		}
		return xpathValueCollection;

	}

	/**
	 * This method will verify whether the child node have some value & if it does then
	 * add this value to the xpathValueCollection.
	 * @param xpathValueCollection collection in which to add the value
	 * @param aNode node whose child value is to be read.
	 */
	private void addChildNodeValue(Collection<String> xpathValueCollection, Node aNode)
	{
		if (aNode.getNodeType() == Node.ELEMENT_NODE)
		{
			NodeList childNodes = aNode.getChildNodes();
			if (childNodes.getLength() > 0 && aNode.getChildNodes().item(0).getNodeValue() != null)
			{
				xpathValueCollection.add(aNode.getChildNodes().item(0).getNodeValue());
			}
		}
	}

	/**
	 * Method will search the autoLoadXpath of the given container (Root catEntity container of category)
	 * whose category is are marked as populateFromXml.
	 * @param containerId the container id from which to get the autoloadxpath collection.
	 * @return collection AutoLoadXpath of this container if its marked as populateFromXML.
	 * @throws DynamicExtensionsSystemException exception.
	 * @throws DAOException exception.
	 */
	public List<AutoLoadXpath> getAutoloadXpathCollForContainerId(Long containerId)
			throws DynamicExtensionsSystemException, DAOException
	{
		// fire a query for selecting those containers which have these test/ids as concept codes.
		StringBuffer hqlForConceptCode = new StringBuffer(300);
		hqlForConceptCode.append("select elements(category.autoLoadXpathCollection) from "
				+ "edu.common.dynamicextensions.domain.Category category where "
				+ "category.rootCategoryElement.containerCollection.id =");
		hqlForConceptCode.append(containerId).append(" and category.isPopulateFromXml =true");
		return DynamicExtensionsUtility.executeQuery(hqlForConceptCode.toString());
	}

	/**
	 * This method will prepare the map from the given xmlMessage & insert that data in the given containerId.
	 * If the record is to be edited then the previous dataValueMap will be passed to this method.
	 * @param xmlMessage XML message string.
	 * @param rootContainerId container id in which to insert the data
	 * @param catRecordId should be passed if need to edit the same record.
	 * @param insertXmlMessage the insert xml message
	 * @param sessionDataBean
	 * @return record identifier in which the data is inserted.
	 * @throws DynamicExtensionsSystemException exception.
	 * @throws DynamicExtensionsApplicationException exception.
	 * @deprecated Use {@link #insertOrEditDataFromMessage(String,String,Long,Long,SessionDataBean)} instead
	 */
	public Long insertOrEditDataFromMessage(String xmlMessage, String insertXmlMessage,
			Long rootContainerId, Long catRecordId) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		return insertOrEditDataFromMessage(xmlMessage, insertXmlMessage, rootContainerId,
				catRecordId, null);
	}

	/**
	 * This method will prepare the map from the given xmlMessage & insert that data in the given containerId.
	 * If the record is to be edited then the previous dataValueMap will be passed to this method.
	 * @param xmlMessage XML message string.
	 * @param insertXmlMessage the insert xml message
	 * @param rootContainerId container id in which to insert the data
	 * @param catRecordId should be passed if need to edit the same record.
	 * @param sessionDataBean
	 * @return record identifier in which the data is inserted.
	 * @throws DynamicExtensionsSystemException exception.
	 * @throws DynamicExtensionsApplicationException exception.
	 */
	public Long insertOrEditDataFromMessage(String xmlMessage, String insertXmlMessage,
			Long rootContainerId, Long catRecordId, SessionDataBean sessionDataBean) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		// retrieve the given container
		CategoryEntity rootCatEntity = (CategoryEntity) EntityCache.getInstance().getContainerById(
				rootContainerId).getAbstractEntity();
		CategoryInterface category = rootCatEntity.getCategory();
		if (!category.getIsPopulateFromXml())
		{// if container is not marked as populate from XML then throw exception.
			throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(
					"message.loading.category.not.populateFromXml.error", category.getName()));
		}

		Document updateXmlDocument = getDocumentObject(xmlMessage);

		// This object will be populated only in case if Edit scenario.
		Document insertXmlDocument = getDocumentObject(insertXmlMessage);
		/**
		 * Baljeet's changes begin
		 */

		//Create a Map of category entity V/s  category entity XPath , by iterating over category object
		Map<CategoryEntityInterface, String> categoryEntityXPathMap = new HashMap<CategoryEntityInterface, String>();

		populateCategoryEntityXpathMap(category.getRootCategoryElement(), categoryEntityXPathMap);

		// get the record Map given parameter if passes else create the new record map
		Map<BaseAbstractAttributeInterface, Object> recordMap = getRecordMapForCategory(
				rootCatEntity, catRecordId);

		//From category, get the AutoLoadXPath collection
		Collection<AutoLoadXpath> autoLoadXpathColl = category.getAutoLoadXpathCollection();
		for (AutoLoadXpath autoLoadXpath : autoLoadXpathColl)
		{
			Collection<String> conceptCodeColl = autoLoadXpath.getConceptCodeCollection();

			//From Identifying XPath, get the concept code collection from XML Message
			String identifyingXPath = autoLoadXpath.getXpath();

			//Get the concept codes from Messages
			Collection<String> messageConceptCodeColl = getAllValuesForXpath(updateXmlDocument,
					identifyingXPath);

			if (insertXmlDocument == null)
			{
				for (String conceptCode : messageConceptCodeColl)
				{
					if (conceptCodeColl.contains(conceptCode) || conceptCodeColl.isEmpty())
					{
						populateMapForConceptNode(updateXmlDocument, categoryEntityXPathMap, category
								.getRootCategoryElement(), recordMap, conceptCode, identifyingXPath);
					}
				}
			}
			else
			{
				Collection<String> insertMessageConceptCode = getAllValuesForXpath(
						insertXmlDocument, identifyingXPath);

				messageConceptCodeColl.removeAll(insertMessageConceptCode);
				if(!messageConceptCodeColl.isEmpty())
				{
					insertMessageConceptCode.addAll(messageConceptCodeColl);
				}

				for (String conceptCode : insertMessageConceptCode)
				{
					if (conceptCodeColl.contains(conceptCode) || conceptCodeColl.isEmpty())
					{
						populateMapForConceptNode(updateXmlDocument, categoryEntityXPathMap, category
								.getRootCategoryElement(), recordMap, conceptCode, identifyingXPath);
					}
				}
			}
		}

		Long editedRecordId = persistData(rootCatEntity, recordMap, catRecordId ,sessionDataBean);

		return editedRecordId;
	}

	/**
	 * Populate category entity XPath map.
	 *
	 * @param categoryEntity the category entity
	 * @param categoryEntityXPathMap the category entity XPath map
	 */
	private void populateCategoryEntityXpathMap(CategoryEntityInterface categoryEntity,
			Map<CategoryEntityInterface, String> categoryEntityXPathMap)
	{
		Collection<CategoryAttributeInterface> categoryAttrColl = categoryEntity
				.getAllCategoryAttributes();
		for (CategoryAttributeInterface categoryAttr : categoryAttrColl)
		{
			boolean isPopulateFromXxml = categoryAttr.getIsPopulateFromXml();
			if (isPopulateFromXxml)
			{
				//Then get the identifying XPath
				//Here String in getTaggedValue method needs to be replaced with some constant
				String identifyingXpath = categoryAttr.getAbstractAttribute().getTaggedValue(
						XMIConstants.TAGGED_NAME_IDENTIFYING_XPATH);
				if (!"".equals(identifyingXpath))
				{
					categoryEntityXPathMap.put(categoryEntity, identifyingXpath);
					//Add this XPath to Map and break as there will be same XPath for all attributes of a category entity
					break;
				}
			}
		}
		Collection<CategoryAssociationInterface> categotyEntityAssociationColl = categoryEntity
				.getCategoryAssociationCollection();
		if (categotyEntityAssociationColl != null)
		{
			for (CategoryAssociationInterface categoryAssociationInterface : categotyEntityAssociationColl)
			{
				CategoryEntityInterface targetCategoryEntity = categoryAssociationInterface
						.getTargetCategoryEntity();
				populateCategoryEntityXpathMap(targetCategoryEntity, categoryEntityXPathMap);
			}
		}
	}

	/**
	 * This method will actually save the data to the database by calling the API provided by category manager.
	 * It will insert the data in the rootCatEntity from recordMap.
	 * It will identify weather the record is to be edited depending on whether the recordMap contains the
	 * record id or not.
	 * @param rootCatEntity root category entity in which to insert the data.
	 * @param recordMap record map.
	 * @param recordID record id to be edited , null if need to insert the new record
	 * @param sessionDataBean
	 * @return record id inserted or edited.
	 * @throws DynamicExtensionsSystemException exception thrown.
	 * @throws DynamicExtensionsApplicationException exception thrown.
	 */
	private Long persistData(CategoryEntity rootCatEntity,
			Map<BaseAbstractAttributeInterface, Object> recordMap, Long recordID, SessionDataBean sessionDataBean)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException

	{
		CategoryManagerInterface categoryManager = CategoryManager.getInstance();
		Long insrtedRecordId=null;
	   try
	   {


			ContainerInterface 	containerInterface = (ContainerInterface)rootCatEntity.getContainerCollection().toArray()[0];
			if (recordID == null)
			{// new data insertion.
				//editedRecordId = categoryManager.insertData(rootCatEntity.getCategory(), recordMap, sessionDataBean);
				insrtedRecordId =DynamicExtensionsUtility.insertDataUtility(null, containerInterface,recordMap);
				LOGGER.info("Record inserted successfully with id " + insrtedRecordId);
			}
			else
			{// Edit data.
				//categoryManager.editData(rootCatEntity, recordMap, recordID, sessionDataBean);
				DynamicExtensionsUtility.editDataUtility(recordID, containerInterface, recordMap, sessionDataBean,sessionDataBean.getUserId());
				insrtedRecordId = recordID;
				LOGGER.info("Record edited successfully with id  " + insrtedRecordId);
			}
	   }catch(MalformedURLException malformedURLException)
	   {
		   throw new DynamicExtensionsSystemException("invalid application URL: "+Variables.jbossUrl, malformedURLException);
	   }
		return categoryManager.getEntityRecordIdByRootCategoryEntityRecordId(insrtedRecordId,
				rootCatEntity.getTableProperties().getName());
	}

	/**
	 * This method will prepare the document object using the xmlMessage string.
	 * @param xmlMessage string from which to form the object.
	 * @return document object created from the given string.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	private Document getDocumentObject(String xmlMessage) throws DynamicExtensionsSystemException
	{
		Document document = null;
		try
		{
			if (xmlMessage != null)
			{
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
				document = documentBuilder.parse(new InputSource(new StringReader(xmlMessage)));

			}
		}
		catch (Exception e)
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue("message.loading.xmlparsing.error."), e);
		}
		return document;
	}

	/**
	 * returns the record Map for category if passed, else created the new one & returns that.
	 * @param rootCatEntity root category Entity.
	 * @param recordId record identifier which is to be fetched.
	 * @return map of the record if recordId is not null else new map.
	 * @throws DynamicExtensionsSystemException exception.
	 * @throws DynamicExtensionsApplicationException exception.
	 */
	private Map<BaseAbstractAttributeInterface, Object> getRecordMapForCategory(
			CategoryEntityInterface rootCatEntity, Long recordId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		Map<BaseAbstractAttributeInterface, Object> recordMap;
		if (recordId == null)
		{
			recordMap = new HashMap<BaseAbstractAttributeInterface, Object>();
		}
		else
		{
			recordMap = CategoryManager.getInstance().getRecordById(rootCatEntity, recordId);
		}
		return recordMap;

	}

	/**
	 * It will populate the map for the category attributes of the categoryEntity, using the document.
	 * @param document xml document from which to get the values.
	 * @param categoryEntityXPathMap map of category Entity with its Identifying xpaths.
	 * @param categoryEntity category entity whose attributes are to be processed.
	 * @param recordValueMap record value map in which the values to be populated.
	 * @param conceptCode concept code for which the data should be populated.
	 * @param identifyingXPath condition of the concept code.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	private void populateMapForConceptNode(Document document,
			Map<CategoryEntityInterface, String> categoryEntityXPathMap,
			CategoryEntityInterface categoryEntity,
			Map<BaseAbstractAttributeInterface, Object> recordValueMap, String conceptCode,
			String identifyingXPath) throws DynamicExtensionsSystemException
	{
		String categoryEntityXpath = categoryEntityXPathMap.get(categoryEntity);
		if (identifyingXPath.equalsIgnoreCase(categoryEntityXpath))
		{
			// get the concept codes for the test Attribute in this category Entity or
			// the attributes for which concept code is defined.
			Collection<String> conceptCodeColl = getIdentifyingConceptCodesForCatEntity(categoryEntity);
			// if the incoming test id is in this conceptCodeColl then only do the the data entry for
			//the category attributes.
			if (conceptCodeColl.contains(conceptCode) || conceptCodeColl.isEmpty())
			{
				for (CategoryAttributeInterface catAttribute : categoryEntity
						.getAllCategoryAttributes())
				{
					if (isValidCategoryAttributeForDataEntity(catAttribute, conceptCode))
					{
						// get the values for the cat Attribute using the XPath.
						String valueXpath = catAttribute.getAbstractAttribute().getTaggedValue(
								XMIConstants.TAGGED_NAME_VALUE_XPATH);
						if(valueXpath == null)
						{
							recordValueMap.put(catAttribute, null);
						}
						else
						{
							//Here again get the value Xpath, predicateCondition same like Idenfying Xpath
							String predicateCondition = catAttribute.getAbstractAttribute()
							.getTaggedValue(XMIConstants.TAGGED_NAME_PREDICATE_XPATH);
							if (predicateCondition != null && !"".equals(predicateCondition))
							{
								predicateCondition = predicateCondition.replace("?", conceptCode);
								//replace "?" from concept code
								valueXpath = valueXpath + predicateCondition;
							}
							String value = getValueForAttributeFromXml(document, valueXpath,
									catAttribute);
							if (value != null)
							{
								recordValueMap.put(catAttribute, value);
							}
							// This is for update case, to avoid inserting extra column every time update is called.
							else if(recordValueMap.get(catAttribute) != null)
							{
								recordValueMap.put(catAttribute, "");
							}
						}
					}
				}
			}
		}
		generateDataMapForCategoryAssociation(document, recordValueMap, conceptCode,
				categoryEntity, identifyingXPath, categoryEntityXPathMap);
	}

	/**
	 * This method will create the data value map for each category association of categoryEntity.
	 * @param document xml document from which to retrieve the values.
	 * @param recordValueMap record value map which is to be updated for values for associations.
	 * @param conceptCode concept code for which to search the values.
	 * @param categoryEntity category entity whose category associations should be processed.
	 * @param identifyingXPath identifying xpath for which the data entry is going on currently.
	 * @param categoryEntityXPathMap category vs its identifying xpath map.
	 * @throws DynamicExtensionsSystemException exception
	 */
	private void generateDataMapForCategoryAssociation(Document document,
			Map<BaseAbstractAttributeInterface, Object> recordValueMap, String conceptCode,
			CategoryEntityInterface categoryEntity, String identifyingXPath,
			Map<CategoryEntityInterface, String> categoryEntityXPathMap)
			throws DynamicExtensionsSystemException
	{
		// iterate for each category association.
		for (CategoryAssociationInterface catAssociation : categoryEntity
				.getCategoryAssociationCollection())
		{
			List associationRecordList = getValueForBaseAbstractAttribute(recordValueMap,
					catAssociation);

			// retrieve the recordMap from list for particular test.
			//as the same map should be edited.

			//Get the XPath from categoryEntityXPathMap for category
			CategoryEntityInterface targetCategoryEntity = catAssociation.getTargetCategoryEntity();

			Map<BaseAbstractAttributeInterface, Object> recordMap = getMapForGivenConceptId(
					targetCategoryEntity, associationRecordList, identifyingXPath, conceptCode);
			//Map<BaseAbstractAttributeInterface, Object> associatedRecordMap = recordMap;
			/*if (recordMap == null)
			{// no data has been entered yet so insert a new record.
				associatedRecordMap = new HashMap<BaseAbstractAttributeInterface, Object>();
			}*/
			populateMapForConceptNode(document, categoryEntityXPathMap, targetCategoryEntity,
					recordMap, conceptCode, identifyingXPath);
			// if map is empty then no data was present for this subform so do not add it.
			if (!associationRecordList.contains(recordMap) && !recordMap.isEmpty())
			{
				associationRecordList.add(recordMap);
			}
			// if list is empty then no data was present for this subform so do not put it in map.
			if (!associationRecordList.isEmpty())
			{
				recordValueMap.put(catAssociation, associationRecordList);
			}
		}
	}

	/**
	 * Will retieve the value for the given catAssociation from the recordValueMap.
	 * @param recordValueMap map from which to find the value.
	 * @param catAssociation category association for which to search the value.
	 * @return value found if present else will create a new list & return that.
	 */
	private List getValueForBaseAbstractAttribute(
			Map<BaseAbstractAttributeInterface, Object> recordValueMap,
			BaseAbstractAttributeInterface catAssociation)
	{
		List associationRecordList;
		if (recordValueMap.get(catAssociation) == null)
		{
			associationRecordList = new ArrayList();
		}
		else
		{
			associationRecordList = (List) recordValueMap.get(catAssociation);
		}
		return associationRecordList;
	}

	/**
	 * This method will find out the Value for the catAttribute from the document xml using the xpath on the
	 * underlying attribute & using the predicate condition.
	 * @param document xml document from which to take the value.
	 * @param predicateCondition predicate condition to be met while retrieving the value.
	 * @param catAttribute category attribute for which value is to be searched.
	 * @return value found from xml.
	 * @throws DynamicExtensionsSystemException exception
	 */
	private String getValueForAttributeFromXml(Document document, String xpath,
			CategoryAttributeInterface catAttribute) throws DynamicExtensionsSystemException
	{
		String value = null;
		Collection<String> valueCollection = getAllValuesForXpath(document, xpath);
		if (valueCollection != null && !valueCollection.isEmpty())
		{
			//error check for multiple values
			value = valueCollection.iterator().next();
			if (!catAttribute.getAllPermissibleValues().isEmpty())
			{
				// permissible values exist for this category attribute so find the PV for the given concept code
				// and insert that PV in the Map.
				value = getPermissibleValueForConceptCode(catAttribute.getAllPermissibleValues(),
						value, catAttribute.getName());
			}
		}
		return value;
	}

	/**
	 * Thid method will verify whether this categoryAttribute is marked as populateFromXml & the passed
	 * concept code is in its permissible value collection & if it does not have any pvs then
	 * the same concept code is present on its underlying base attribute.
	 * @param catAttribute category attribute to verify.
	 * @param conceptCode concept code to be verified for category attribute.
	 * @return true if above condition met else false.
	 */
	private boolean isValidCategoryAttributeForDataEntity(CategoryAttributeInterface catAttribute,
			String conceptCode)
	{
		boolean isValid = false;
		if (catAttribute.getIsPopulateFromXml() && !catAttribute.getIsRelatedAttribute())
		{
			// if category attribtue is marked as populate from XML & is not related then only insert data
			//for it.
			//for this search weather this catAttribtues attribtue contains the concept code
			// if it contains then the incoming test id then only data entry should be done
			// else the data entry should be done if no concept code is assigned with the attribute.
			AbstractAttributeInterface abstractAttribute = catAttribute.getAbstractAttribute();
			if (XMIConstants.CONCEPT_CODE_LOC_ATTRIBUTE.equalsIgnoreCase(abstractAttribute
					.getTaggedValue(XMIConstants.TAGGED_NAME_CONCEPT_LOCATION)))
			{
				Collection<String> attributeSemanticColl = DynamicExtensionsUtility
						.getConceptCodes(abstractAttribute.getSemanticPropertyCollection());
				if (attributeSemanticColl.isEmpty() || attributeSemanticColl.contains(conceptCode))
				{
					isValid = true;
				}
			}
			else
			{
				isValid = true;
			}
		}
		return isValid;
	}

	/**
	 * This method will search for the Map for the given concept code in the associationRecordList.
	 * @param targetCategoryEntity category entity.
	 * @param associationRecordList recordList from which to search the map.
	 * @param startingXpath identifying xpath for the given category Entity.
	 * @param conceptCode conceptCode for which the map is to be searched.
	 * @return map if found else empty new Map.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	private Map<BaseAbstractAttributeInterface, Object> getMapForGivenConceptId(
			CategoryEntityInterface targetCategoryEntity, List associationRecordList,
			String startingXpath, String conceptCode) throws DynamicExtensionsSystemException
	{
		//search the map for which this test data has been entered already.
		Map<BaseAbstractAttributeInterface, Object> recordValueMap = new HashMap<BaseAbstractAttributeInterface, Object>();
		if (targetCategoryEntity.getNumberOfEntries() == -1)
		{
			// for multiline subcategory (generalised) which would have testId as combobox
			for (Object dataValueObject : associationRecordList)
			{
				Map<BaseAbstractAttributeInterface, Object> dataValueMap = (Map<BaseAbstractAttributeInterface, Object>) dataValueObject;
				boolean isDataMapForConceptCode = isDataMapForGivenConceptCode(startingXpath,
						conceptCode, dataValueMap);
				if (isDataMapForConceptCode)
				{
					recordValueMap = dataValueMap;
					break;
				}
			}
		}
		else
		{
			// single line subcategory.
			if (!associationRecordList.isEmpty())
			{
				recordValueMap = (Map<BaseAbstractAttributeInterface, Object>) associationRecordList
						.get(0);
			}
		}
		return recordValueMap;
	}

	/**
	 * This method will verify whether the given datavalue map is for the passed concept code or not depending
	 * on the value associated with the category attribute whose baseattribtue has xpath as given
	 * in the startingXpath.
	 * @param startingXpath xpath of the attribute which should be used for comparision.
	 * @param conceptCode concept code which is to be checked.
	 * @param dataValueMap data value map which is to be tested for the concept code.
	 * @return true if the dataValueMap is for the given concept code else false.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	private boolean isDataMapForGivenConceptCode(String startingXpath, String conceptCode,
			Map<BaseAbstractAttributeInterface, Object> dataValueMap)
			throws DynamicExtensionsSystemException
	{
		boolean isDataMapForConceptId = false;
		for (Entry<BaseAbstractAttributeInterface, Object> entryObject : dataValueMap.entrySet())
		{
			if (entryObject.getKey() instanceof CategoryAttributeInterface)
			{
				CategoryAttributeInterface catAttribute = (CategoryAttributeInterface) entryObject
						.getKey();
				if (catAttribute.getIsPopulateFromXml())//bug 19469
				{
					String xpath = catAttribute.getAbstractAttribute().getTaggedValue(
							XMIConstants.TAGGED_NAME_VALUE_XPATH);

					if (startingXpath.equals(xpath))
					{
						String value = getPermissibleValueForConceptCode(catAttribute
								.getAllPermissibleValues(), conceptCode, catAttribute.getName());
						if (entryObject.getValue().equals(value))
						{
							isDataMapForConceptId = true;
							break;
						}
					}
				}
			}
		}
		return isDataMapForConceptId;
	}

	/**
	 * It will return the concept codes associated with the category attributes in that category entity
	 * for the Attribute which has xpath as starting xpath from xml else
	 * uses the concept code associated with that attribute.
	 * @param categoryEntity category entity whose associated concept codes to be searched.
	 * @return collection of cocnept codes for that catgeory entity.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	private Collection<String> getIdentifyingConceptCodesForCatEntity(
			CategoryEntityInterface categoryEntity) throws DynamicExtensionsSystemException
	{
		Collection<String> conceptCodeCollection = new HashSet<String>();
		for (CategoryAttributeInterface catAttribtue : categoryEntity.getAllCategoryAttributes())
		{
			if (catAttribtue.getIsPopulateFromXml())
			{
				// test attribute with or without PV can be related attribute
				if (catAttribtue.getIsRelatedAttribute())
				{
					// related category attribute , so find the conceptCode assigned with the default value for
					//the related attribute.
					conceptCodeCollection.add(DynamicExtensionsUtility.getConceptCodeForValue(
							catAttribtue, catAttribtue.getDefaultValue(null)));
				}
				else
				{
					//Get the concept code location tag from the category attribute
					String conceptCodeLocation = catAttribtue.getAbstractAttribute()
							.getTaggedValue(XMIConstants.TAGGED_NAME_CONCEPT_LOCATION);

					if (conceptCodeLocation != null && !conceptCodeLocation.equals(""))
					{
						if (conceptCodeLocation.equals(XMIConstants.CONCEPT_CODE_LOC_PV))
						{
							//This is executed for PV
							//if PV
							for (PermissibleValueInterface permissibleValue : catAttribtue
									.getAllPermissibleValues())
							{
								conceptCodeCollection.addAll(DynamicExtensionsUtility
										.getConceptCodes(permissibleValue
												.getSemanticPropertyCollection()));
							}
						}
						else if (conceptCodeLocation
								.equals(XMIConstants.CONCEPT_CODE_LOC_ATTRIBUTE))
						{
							//if attribute
							conceptCodeCollection.addAll(DynamicExtensionsUtility
									.getConceptCodes(catAttribtue.getAbstractAttribute()
											.getSemanticPropertyCollection()));
						}
					}
				}
			}
		}
		return conceptCodeCollection;
	}

	/**
	 * This method will search for the value associated with the given concept code in the given
	 * pvCollection.
	 * @param pvCollection collection in which to search for the value.
	 * @param conceptCode concept code for which to search the value.
	 * @param attributeName name of the attribute.
	 * @return value associated with that concept code.
	 * @throws DynamicExtensionsSystemException exception.
	 */
	private String getPermissibleValueForConceptCode(
			Collection<PermissibleValueInterface> pvCollection, String conceptCode,
			String attributeName) throws DynamicExtensionsSystemException
	{
		String value = null;
		for (PermissibleValueInterface permissibleValue : pvCollection)
		{
			for (SemanticPropertyInterface semanticProp : permissibleValue
					.getOrderedSemanticPropertyCollection())
			{
				if (conceptCode.equals(semanticProp.getConceptCode()))
				{
					value = permissibleValue.getValueAsObject().toString();
					break;
				}
			}
		}
		if (value == null)
		{
			List<String> placeHolders = new ArrayList<String>();
			placeHolders.add(conceptCode);
			placeHolders.add(attributeName);
			throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(
					"message.loading.nopv.conceptcode.error", placeHolders));
		}
		return value;
	}
}
