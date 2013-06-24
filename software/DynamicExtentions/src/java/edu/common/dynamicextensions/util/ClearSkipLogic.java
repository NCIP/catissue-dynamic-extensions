/**
 * 
 */
package edu.common.dynamicextensions.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import edu.common.dynamicextensions.client.DataEditClient;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.LoadDataEntryFormProcessor;
import edu.common.dynamicextensions.skiplogic.Action;
import edu.common.dynamicextensions.skiplogic.Condition;
import edu.common.dynamicextensions.skiplogic.ConditionStatements;
import edu.common.dynamicextensions.skiplogic.DisableAction;
import edu.common.dynamicextensions.skiplogic.EnableAction;
import edu.common.dynamicextensions.skiplogic.HideAction;
import edu.common.dynamicextensions.skiplogic.ShowAction;
import edu.common.dynamicextensions.skiplogic.SkipLogic;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.global.Variables;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

/**
 * @author skhot
 * 
 */
public class ClearSkipLogic {

	private static final String SELECT = "--Select--";

	static 
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(ClearSkipLogic.class);
	
	/** All categories present in database */
	private static final Collection<CategoryInterface> categoryCollection = new HashSet<CategoryInterface>();
	
	/** Map of skipLogic category and its corresponding category */
	private static final Map<CategoryInterface, ContainerInterface> skipLogicContainerMap = new HashMap<CategoryInterface, ContainerInterface>();

	/** The new line. */
	private static final transient String newLine = System.getProperty("line.separator");
	
	private static transient boolean isMapUpdated = Boolean.FALSE;
	
	/** @param args */
	public static void main(String[] args) 
	{
		String outputFileName = "skipLogicOutput.txt";
		File outputFile = new File(outputFileName);
		System.out.println("SkipLogic output file location :: " + outputFile.getAbsolutePath());

		Properties props = new Properties();
//		props.setProperty("Application.url", "https://localhost:8443/clinportal");
//		props.setProperty("default.prifix.war", "1");
		props.setProperty("Application.url", args[0]);
		props.setProperty("default.prifix.war", args[0]);
		DynamicExtensionsUtility.initializeVariables(props);
		try
		{
			StringBuilder stringBuilder = new StringBuilder();
			Writer writer = new BufferedWriter(new FileWriter(outputFile));
			// Initialize entity Cache
			EntityCache entityCache = EntityCache.getInstance();
			try
			{
				//categoryCollection.add(CategoryManager.getInstance().getCategoryByName("Dystonia_Natural_Hx_Intake_3.0"));
				// Step 1. Get all forms == ~1336
				getAllForms(stringBuilder);
	
				// Step 2. Cache all SkipLogic
				entityCache.cacheSkipLogic();
	
				// Step 3. Filter for skipLogic forms.
				filterSkipLogicForms(stringBuilder);
	
				// Step 4. Make skipLogic forms correction.
				applyCorrectionOnSkipLogicForms(stringBuilder);
				
				// Write logs to file
				writer.write(stringBuilder.toString());
			}
			finally
			{
				if(writer != null)
				{
					writer.close();
				}
			}
			LOGGER.info("Upgrade completed sucessfully.");

		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
			LOGGER.error("Exception occured :: "+ex.getMessage());
		}
	}


	/**
	 * Fetch all categories from the dataBase
	 * @param stringBuilder
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private static void getAllForms(StringBuilder stringBuilder) 
				throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		if (categoryCollection == null || categoryCollection.isEmpty()) 
		{
			categoryCollection.addAll(CategoryManager.getInstance().getAllCategories());
			stringBuilder.append("************************************************"+newLine);
			stringBuilder.append("**  Total categories in database are :: "+ categoryCollection.size() + "  **"+newLine);
			stringBuilder.append("************************************************"+newLine);
			stringBuilder.append(newLine).append(newLine);
		}
	}

	/**
	 * This method will filter out skipLogic categories from the original categories set.
	 */
	private static void filterSkipLogicForms(StringBuilder stringBuilder) 
	{
		for (CategoryInterface categoryInterface : categoryCollection) 
		{
			// Get root category element
			CategoryEntityInterface categoryEntityInterface = categoryInterface.getRootCategoryElement();
			// Get container for the respective category
			ContainerInterface container = (ContainerInterface) categoryEntityInterface
							.getContainerCollection().iterator().next();
			// Check if that respective category has skipLogic
			SkipLogic skipLogic = EntityCache.getInstance().getSkipLogicByContainerIdentifier(container.getId());
			// If SkipLogic present, then add that category and respective container to map
			if (skipLogic != null)
			{
				skipLogicContainerMap.put(categoryInterface, container);
			} 
			else 
			{
				// Check skipLogic for child categories
				if (isSkipLogicPresentInChildCategories(categoryEntityInterface)) 
				{
					skipLogicContainerMap.put(categoryInterface, container);
				}
			}
		}
		stringBuilder.append("*********************************************************"+newLine);
		stringBuilder.append("**  Total SkipLogic categories in database are :: "
										+ skipLogicContainerMap.keySet().size() + "  **"+newLine);
		stringBuilder.append("*********************************************************"+newLine);
		stringBuilder.append(newLine).append(newLine);
	}
	
	/**
	 * Check is skipLogic present for Child categories.
	 * If present return true.
	 * 
	 * @param parentCategoryEntityInterface
	 * @return
	 */
	private static boolean isSkipLogicPresentInChildCategories(CategoryEntityInterface parentCategoryEntityInterface) 
	{
		boolean isSkiplogicPresent = false;
		// Get All child categories from the categoryEntity
		Collection<CategoryEntityInterface> childCategories = parentCategoryEntityInterface.getChildCategories();
		for (CategoryEntityInterface categoryEntityInterface : childCategories) 
		{
			if (!categoryEntityInterface.getContainerCollection().isEmpty()) 
			{
				// Get container for the respective child category
				ContainerInterface container = (ContainerInterface) categoryEntityInterface
						.getContainerCollection().iterator().next();
				// Check if that respective child category has skipLogic
				SkipLogic skipLogic = EntityCache.getInstance()
						.getSkipLogicByContainerIdentifier(container.getId());
				// If SkipLogic present, return true; else check skipLogic for child categories
				if (skipLogic != null)
				{
					isSkiplogicPresent = true;
					break;
				} 
				else if (categoryEntityInterface.getChildCategories() != null
						&& !categoryEntityInterface.getChildCategories().isEmpty()) 
				{
					isSkiplogicPresent = isSkipLogicPresentInChildCategories(categoryEntityInterface);
					if (isSkiplogicPresent)
					{
						break;
					}
				}
			}
		}
		return isSkiplogicPresent;
	}
	
	/**
	 * Make correction for skipLogic categories
	 * @param stringBuilder
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 * @throws DynamicExtensionsApplicationException
	 * @throws ParseException 
	 * @throws MalformedURLException 
	 */
	private static void applyCorrectionOnSkipLogicForms(StringBuilder stringBuilder)
			throws DynamicExtensionsSystemException, DAOException, DynamicExtensionsApplicationException, ParseException, MalformedURLException 
	{
		// Iterate on each filtered skipLogic category
		for (CategoryInterface skipLogicCategory : skipLogicContainerMap.keySet()) 
		{
			ContainerInterface containerInterface = skipLogicContainerMap.get(skipLogicCategory);
			// Get all records for the corresponding category
			Collection<Long> recIdList = getCategoryRecordIds(skipLogicCategory, containerInterface);
			if(recIdList!= null && !recIdList.isEmpty())
			{
				stringBuilder.append(newLine+"Form name :: "+skipLogicCategory.getName()+newLine);
				stringBuilder.append("Number of records present for form '"+skipLogicCategory.getName()+"' are :: "+recIdList.size()+newLine);
			
				// Apply skipLogic fixes for all records
				applySkipLogicCorrectionForRecords(stringBuilder, skipLogicCategory, containerInterface, recIdList);
			}
		}
	}

	/**
	 * Get all recordIds for the given category
	 * @param skipLogicCategory
	 * @param containerInterface
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 */
	private static Collection<Long> getCategoryRecordIds(CategoryInterface skipLogicCategory,
			ContainerInterface containerInterface) throws DynamicExtensionsSystemException, DAOException 
	{
		String catTableName = EntityManager.getInstance().getDynamicTableName(containerInterface.getId());
		LOGGER.info("Category Name :: "+skipLogicCategory.getName()+"; Table Name :: " + catTableName);
		String sql = "select RECORD_ID from " + catTableName;
		List<ColumnValueBean> queryDataList = new LinkedList<ColumnValueBean>();
		Collection<Long> recIdList = getResultCollection(sql, queryDataList);
		LOGGER.info("Number of records present for form '"+skipLogicCategory.getName()+"' are :: "+ recIdList.size());
		return recIdList;
	}
	
	/**
	 * Get record list
	 * @param catSql
	 * @param queryDataList
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DAOException
	 */
	@SuppressWarnings("deprecation")
	private static Collection<Long> getResultCollection(String catSql, List<ColumnValueBean> queryDataList)
			throws DynamicExtensionsSystemException, DAOException 
	{
		ResultSet resultSet = null;
		Collection<Long> recIdList = new HashSet<Long>();
		JDBCDAO jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
		try 
		{
			resultSet = jdbcDao.getResultSet(catSql, queryDataList, null);
			while (resultSet.next()) 
			{
				recIdList.add(resultSet.getLong(1));
			}
		} 
		catch (SQLException e) 
		{
			throw new DynamicExtensionsSystemException("Error while opening session", e);
		}
		finally 
		{
			jdbcDao.closeStatement(resultSet);
			DynamicExtensionsUtility.closeDAO(jdbcDao);
		}
		return recIdList;
	}
	
	/** 
	 * Apply SkipLogic Correction For Records
	 * @param stringBuilder
	 * @param skipLogicCategory
	 * @param containerInterface
	 * @param recIdList
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws ParseException 
	 * @throws MalformedURLException 
	 */
	private static void applySkipLogicCorrectionForRecords(StringBuilder stringBuilder, 
			CategoryInterface skipLogicCategory, ContainerInterface containerInterface, Collection<Long> recIdList) 
					throws DynamicExtensionsSystemException,DynamicExtensionsApplicationException, ParseException, MalformedURLException 
	{
		// Iterate on each record list to apply skipLogic correction
		final LoadDataEntryFormProcessor loadDataEntryFormProcessor = LoadDataEntryFormProcessor.getInstance();
		for (Long recordId : recIdList) 
		{
			isMapUpdated = Boolean.FALSE;
//			stringBuilder.append(newLine).append(newLine);
			final Map<BaseAbstractAttributeInterface, Object> recordMap = loadDataEntryFormProcessor
					.getValueMapFromRecordId(containerInterface.getAbstractEntity(), recordId.toString());
			SkipLogic skipLogic = EntityCache.getInstance().getSkipLogicByContainerIdentifier(
						containerInterface.getId());
			if (skipLogic != null) 
			{
				applySkipLogicCorrection(containerInterface, recordMap, skipLogic, stringBuilder);
			}
			// Check for child categories
			applySkipLogicCorrectionForChildCategories(skipLogicCategory.getRootCategoryElement(), recordMap, stringBuilder);
			editData(containerInterface, recordId, recordMap, stringBuilder);
		}
	}


	/**
	 * Updates record data in database
	 * @param containerInterface
	 * @param recordId
	 * @param recordMap
	 * @throws MalformedURLException
	 */
	private static void editData(ContainerInterface containerInterface,	Long recordId,
			final Map<BaseAbstractAttributeInterface, Object> recordMap, StringBuilder stringBuilder)
			throws MalformedURLException {
		if(isMapUpdated)
		{
			stringBuilder.append("Category Record Id :: "+recordId+newLine);
			String entityGroupName = containerInterface.getAbstractEntity().getEntityGroup().getName();
			Map<String, Object> clientmap = new HashMap<String, Object>();
			DataEditClient dataEditClient = new DataEditClient();
			clientmap.put(WebUIManagerConstants.RECORD_ID, recordId);
			clientmap.put(WebUIManagerConstants.SESSION_DATA_BEAN, null);
			clientmap.put(WebUIManagerConstants.USER_ID, null);
			clientmap.put(WebUIManagerConstants.CONTAINER, containerInterface);
			clientmap.put(WebUIManagerConstants.DATA_VALUE_MAP, recordMap);
			dataEditClient.setServerUrl(new URL(Variables.jbossUrl + entityGroupName + "/"));
			dataEditClient.setParamaterObjectMap(clientmap);
			dataEditClient.execute(null);
		}
	}

	/**
	 * Apply Skip Logic Correction
	 * @param containerInterface
	 * @param recordMap
	 * @param skipLogic
	 * @throws DynamicExtensionsSystemException
	 * @throws ParseException 
	 */
	private static void applySkipLogicCorrection(ContainerInterface containerInterface, Map<BaseAbstractAttributeInterface, Object> recordMap,
			SkipLogic skipLogic, StringBuilder stringBuilder) throws DynamicExtensionsSystemException, ParseException 
	{
		for (ConditionStatements conditionStatement : skipLogic.getListOfconditionStatements()) 
		{
			boolean conditionSatisfied = false;
			for (Condition condition : conditionStatement.getListOfConditions()) 
			{
				if (condition.checkCondition(recordMap,containerInterface))
				{
					conditionSatisfied = true;
					break;
				}
			}
			if (!conditionSatisfied)
			{
				Condition condition = conditionStatement.getListOfConditions().iterator().next();
				resetAction(condition, recordMap, stringBuilder);
			}
		}
	}

	/**
	 * Apply skipLogic correction for Child categories.
	 * 
	 * @param parentCategoryEntityInterface
	 * @return
	 * @throws DynamicExtensionsSystemException 
	 * @throws ParseException 
	 */
	private static void applySkipLogicCorrectionForChildCategories(CategoryEntityInterface parentCategoryEntityInterface, 
			Map<BaseAbstractAttributeInterface, Object> recordMap, StringBuilder stringBuilder) throws DynamicExtensionsSystemException, ParseException 
	{
		// Get All child categories for the categoryEntity
		Collection<CategoryEntityInterface> childCategories = parentCategoryEntityInterface.getChildCategories();
		//Iterate on each child category
		for (CategoryEntityInterface categoryEntityInterface : childCategories) 
		{
			if (!categoryEntityInterface.getContainerCollection().isEmpty()) 
			{
				// get container for that child category
				ContainerInterface container = (ContainerInterface) categoryEntityInterface
						.getContainerCollection().iterator().next();
				// Check is skipLogic present for corresponding child category.
				SkipLogic skipLogic = EntityCache.getInstance().getSkipLogicByContainerIdentifier(container.getId());
				if (skipLogic != null) 
				{
					applySkipLogicCorrection(container, recordMap, skipLogic, stringBuilder);
				} 
				if (categoryEntityInterface.getChildCategories() != null && !categoryEntityInterface.getChildCategories().isEmpty())
				{
					applySkipLogicCorrectionForChildCategories(categoryEntityInterface, recordMap, stringBuilder);
				}
			}
		}
	}

	/**
	 * This method will actual delete the unwanted data depending on action
	 * @param condition
	 * @param recordMap
	 * @throws DynamicExtensionsSystemException
	 * @throws ParseException 
	 */
	private static void resetAction(Condition condition, Map<BaseAbstractAttributeInterface, Object> recordMap, 
			StringBuilder stringBuilder) throws DynamicExtensionsSystemException, ParseException 
	{		
		Action action = condition.getAction();
		ControlInterface targetControl =action.getControl();
		if (action instanceof DisableAction || action instanceof EnableAction 
				|| action instanceof HideAction || action instanceof ShowAction)
		{
				CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface) targetControl
						.getAttibuteMetadataInterface();
				resetDependentAttributeValue(recordMap, categoryAttribute, stringBuilder);
		}
	}

	/**
	 * @param objectValueState
	 * @param categoryAttribute
	 * @param stringBuilder
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	private static void resetDependentAttributeValue(Map<BaseAbstractAttributeInterface, Object> objectValueState, 
			CategoryAttributeInterface categoryAttribute, StringBuilder stringBuilder) throws ParseException
	{
		if (objectValueState.get(categoryAttribute) == null)
		{
			Set<Entry<BaseAbstractAttributeInterface, Object>> entrySet = objectValueState.entrySet();
	        for (Entry<BaseAbstractAttributeInterface, Object> entry : entrySet)
	        {
	        	if (entry.getKey() instanceof CategoryAssociationInterface && entry.getValue() != null)
	        	{
	        		List list = (List) entry.getValue(); 
	        		if (!list.isEmpty())
	        		{
	        			final Iterator iterator = list.iterator();
	        			while(iterator.hasNext())
	        			{
	        				Map<BaseAbstractAttributeInterface, Object> objectValue = (Map<BaseAbstractAttributeInterface, Object>) iterator.next();
	        				resetDependentAttributeValue(objectValue, categoryAttribute, stringBuilder);
	        			}
	        		}
	        	}
	        }
		}
		else
		{
			Object targetValue = objectValueState.get(categoryAttribute);
			LOGGER.info("Dependent Attribute '"+categoryAttribute.getName()+"' value is :: "+ targetValue);
			if(targetValue != null)
			{
				if(targetValue instanceof String && !"".equals(targetValue) && !SELECT.equalsIgnoreCase(targetValue.toString()))
				{
					Object defaultValue = getDefaultValueForAttribute(categoryAttribute);
					if(!handleDatatype(categoryAttribute, targetValue).equals(defaultValue))
					{
						isMapUpdated = true;
						stringBuilder.append("Default value for dependent attribute '"+categoryAttribute.getName()+"' is '"+defaultValue+"'"+newLine);
						stringBuilder.append("Dependent attribute '"+categoryAttribute.getName()+"' value '"+targetValue+"' has removed successfuly.").append(newLine);
						objectValueState.put(categoryAttribute, defaultValue);
					}
				}
				else if(targetValue instanceof List)
				{
					List list = (List) targetValue;
					if (!list.isEmpty())
	        		{
	        			Map<BaseAbstractAttributeInterface, Object> objectValue = (Map<BaseAbstractAttributeInterface, Object>) list
                            .iterator().next();
	        			for(Object value : objectValue.keySet())
	        			{
	        				if(value instanceof String && !"".equals(value) && !SELECT.equalsIgnoreCase(value.toString()))
	        				{
	        					isMapUpdated = true;
	        					Object defaultValue = getDefaultValueForAttribute(categoryAttribute);
	        					if(!handleDatatype(categoryAttribute, targetValue).equals(defaultValue))
	        					{
	        						stringBuilder.append("Default value for dependent attribute(list) '"+categoryAttribute.getName()+"' is '"+defaultValue+"'"+newLine);
	        						stringBuilder.append("Dependent attribute(list) '"+categoryAttribute.getName()+"' value '"+targetValue+"' has removed successfuly.").append(newLine);;
	        					}
	        				}
	        			}
	        		}
				}				
			}
		}
	}

	/**
	 * Get Default value for attribute
	 * @param categoryAttribute
	 * @return
	 * @throws ParseException 
	 */
	private static Object getDefaultValueForAttribute(CategoryAttributeInterface categoryAttribute) throws ParseException 
	{
		Object defaultValue;
		if(categoryAttribute.getDefaultSkipLogicValue() != null)
		{
			defaultValue = categoryAttribute.getDefaultSkipLogicValue().getValueAsObject();
		}
		else
		{
			defaultValue = categoryAttribute.getDefaultValue(null);
		}
		if(defaultValue!=null && defaultValue instanceof String 
				&& !"".equals(defaultValue) && !SELECT.equalsIgnoreCase(defaultValue.toString()))
		{
			return handleDatatype(categoryAttribute, defaultValue);
		}
		return null;
		
	}
	
	/**
	 * @param attribute
	 * @param returnedObj
	 * @param value
	 * @param attrName
	 * @throws ParseException
	 */
	public static Object handleDatatype(CategoryAttributeInterface attribute, Object value) throws ParseException
	{
		String dataType = ((AttributeMetadataInterface) attribute).getAttributeTypeInformation().getDataType();
		Object dataValue = null;
		if ("Long".equals(dataType))
		{
			if (DynamicExtensionsUtility.isNotEmptyString(value))
			{
				dataValue = Long.valueOf(value.toString());
			}
		}
		else if ("Float".equals(dataType))
		{
			if (DynamicExtensionsUtility.isNotEmptyString(value))
			{
				dataValue = new Float(value.toString());
			}
		}
		else if ("Double".equals(dataType))
		{
			if (DynamicExtensionsUtility.isNotEmptyString(value))
			{
				dataValue = new Double(value.toString());
			}
		}
		else if ("Short".equals(dataType))
		{
			if (DynamicExtensionsUtility.isNotEmptyString(value))
			{
				dataValue = Short.valueOf(value.toString());
			}
		}
		else if ("Integer".equals(dataType))
		{
			if (DynamicExtensionsUtility.isNotEmptyString(value))
			{
				dataValue = Integer.valueOf(value.toString());
			}
		}
		else if ("Boolean".equals(dataType))
		{
			if (DynamicExtensionsUtility.isNotEmptyString(value))
			{
				String boolVal = ("1".equals(value) || DEConstants.TRUE.equalsIgnoreCase(value
						.toString())) ? DEConstants.TRUE : DEConstants.FALSE;
				dataValue = Boolean.valueOf(boolVal);
			}
		}
		else if ("Date".equals(dataType))
		{
			if (DynamicExtensionsUtility.isNotEmptyString(value))
			{
				dataValue = getDateValue(attribute, value);
			}
		}
		else
		{
			if (DynamicExtensionsUtility.isNotEmptyString(value))
			{
				dataValue = value.toString();
			}
		}
		return dataValue;
	}
	
	/**
	 * Gets the date value.
	 *
	 * @param attribute
	 *            the attribute
	 * @param value
	 *            the value
	 *
	 * @return the date value
	 *
	 * @throws ParseException
	 *             the parse exception
	 */
	private static Object getDateValue(final CategoryAttributeInterface attribute, final Object value)
			throws ParseException
	{
		Object dataValue;
		DateAttributeTypeInformation dateAttributeTypeInf = (DateAttributeTypeInformation)((AttributeMetadataInterface) attribute).getAttributeTypeInformation();

		String format = DynamicExtensionsUtility.getDateFormat(dateAttributeTypeInf.getFormat());

		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
		dataValue = formatter.parse(value.toString());
		return dataValue;
	}
	
}
