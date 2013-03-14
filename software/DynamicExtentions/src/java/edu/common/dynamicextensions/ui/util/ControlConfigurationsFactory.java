
package edu.common.dynamicextensions.ui.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.xml.Controls;
import edu.common.dynamicextensions.util.xml.Controls.Control;
import edu.common.dynamicextensions.util.xml.Controls.Control.CommonValidation;
import edu.common.dynamicextensions.util.xml.Controls.Control.CommonValidation.CommonValidationRule;
import edu.common.dynamicextensions.util.xml.DataTypeClass;
import edu.common.dynamicextensions.util.xml.DataTypeClass.DataType;
import edu.common.dynamicextensions.util.xml.DataTypeClass.DataType.Validations;
import edu.common.dynamicextensions.util.xml.ValidatorRules;
import edu.common.dynamicextensions.util.xml.ValidatorRules.ValidationRule;
import edu.common.dynamicextensions.util.xml.ValidatorRules.ValidationRule.Param;
import edu.common.dynamicextensions.util.xml.XMLToObjectConverter;
import edu.common.dynamicextensions.validation.ValidatorRuleInterface;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.global.ApplicationProperties;

/**
 * @author preeti_munot
 * @author deepti_shelar
 * @version 1.0
 */
public final class ControlConfigurationsFactory
{

	private static ControlConfigurationsFactory configurationFactory = null;
	private final Map controlsConfigurationMap;
	private final Map<String, RuleConfigurationObject> rulesConfigurationMap;

	/**
	 * private constructor for ControlConfigurationsFactory.
	 * This will initialize maps for controls and their appropriate rules.
	 * The call to parseXML will parse two configuration files in order to
	 * fill the data in their objects.
	 * @throws DynamicExtensionsSystemException  dynamicExtensionsSystemException
	 */
	private ControlConfigurationsFactory() throws DynamicExtensionsSystemException
	{
		controlsConfigurationMap = new HashMap();
		rulesConfigurationMap = new HashMap<String, RuleConfigurationObject>();
		loadRuleConfigurations();
		loadControlConfigurations();
	}

	/**
	 *
	 * @return ControlConfigurationsFactory instance of ControlConfigurationsFactory
	 * @throws DynamicExtensionsSystemException dynamicExtensionsSystemException
	 */
	public static synchronized ControlConfigurationsFactory getInstance()
			throws DynamicExtensionsSystemException
	{
		if (configurationFactory == null)
		{
			configurationFactory = new ControlConfigurationsFactory();
		}
		return configurationFactory;
	}

	/**
	 * Initialize the Jaxb object converter from given schema file to convert the xml file to java object.
	 * @param xsdFileName schema defination file.
	 * @param xmlFileName xml file path.
	 * @param packageName package name in which the corresponding java class resides.
	 * @return object created from the given xml.
	 * @throws DynamicExtensionsSystemException
	 */
	private Object initialize(String xsdFileName, String xmlFileName, String packageName)
			throws DynamicExtensionsSystemException
	{
		try
		{
			// Creates URL of the XSD specified.
			URL xsdFileUrl = Thread.currentThread().getContextClassLoader()
					.getResource(xsdFileName);

			XMLToObjectConverter converter = new XMLToObjectConverter(packageName, xsdFileUrl);

			// PvVersion represents the XML in object form
			return converter.getJavaObject(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(xmlFileName));
		}
		catch (JAXBException e)
		{

			throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(""), e);
		}
		catch (SAXException e)
		{

			throw new DynamicExtensionsSystemException(ApplicationProperties.getValue(""), e);
		}
	}

	/**
	 * This method initializes the ruleConfiguration map from the RuleConfigurations.xml.
	 * @throws DynamicExtensionsSystemException exception during xml parsing.
	 */
	private void loadRuleConfigurations() throws DynamicExtensionsSystemException
	{
		ValidatorRules rules = (ValidatorRules) initialize("RuleConfigurations.xsd",
				"RuleConfigurations.xml", ValidatorRules.class.getPackage().getName());
		rules.getValidationRule();
		for (ValidationRule valRule : rules.getValidationRule())
		{
			RuleConfigurationObject ruleConfigurationObjectNew = new RuleConfigurationObject();
			ruleConfigurationObjectNew.setDisplayLabel(valRule.getDisplayLabel());
			ruleConfigurationObjectNew.setRuleName(valRule.getRuleName());
			ruleConfigurationObjectNew.setRuleClassName(valRule.getRuleClass());

			List ruleParametersAttributes = new ArrayList<NameValueBean>();
			for (Param parameter : valRule.getParam())
			{
				NameValueBean bean = new NameValueBean(parameter.getParamName(), parameter
						.getParamLabel());
				ruleParametersAttributes.add(bean);
			}

			ruleConfigurationObjectNew.setRuleParametersList(ruleParametersAttributes);

			ruleConfigurationObjectNew.setErrorKey(valRule.getErrorKey().getName());
			rulesConfigurationMap.put(ruleConfigurationObjectNew.getRuleName(),
					ruleConfigurationObjectNew);

		}
	}

	/**
	 * This method initializes the controlsConfiguration  map from the ControlConfigurations.xml.
	 * @throws DynamicExtensionsSystemException exception during xml parsing.
	 */
	private void loadControlConfigurations() throws DynamicExtensionsSystemException
	{
		Controls controlNodeList = (Controls) initialize("ControlConfigurations.xsd",
				"ControlConfigurations.xml", Controls.class.getPackage().getName());
		List<String> controlNameList = new ArrayList<String>();
		for (Control controlNode : controlNodeList.getControl())
		{
			controlNameList.add(controlNode.getName());
			ControlsConfigurationObject controlsConfigurationObject = new ControlsConfigurationObject();
			controlsConfigurationObject.setImageFilePath(controlNode.getImagePath());
			controlsConfigurationObject.setJspName(controlNode.getJspName());
			controlsConfigurationObject.setDisplayLabel(controlNode.getDisplayLabel());
			controlsConfigurationObject.setControlName(controlNode.getName());
			updateCommonValidatioNRules(controlNode, controlsConfigurationObject);
			DataTypeClass datatypeClass = controlNode.getDatatypes();

			updateDataTypeValidationRules(controlsConfigurationObject, datatypeClass);

			controlsConfigurationMap.put(controlNode.getName(), controlsConfigurationObject);
		}
		controlsConfigurationMap.put("ListOfControls", controlNameList);
	}

	/**
	 * This method will update the controlsConfigurationObject for thr validation rules corresponding to
	 * each data type specified in the xml.
	 * @param controlsConfigurationObject object to be updated for data type validation.
	 * @param datatypeClass data type node class object.
	 */
	private void updateDataTypeValidationRules(
			ControlsConfigurationObject controlsConfigurationObject, DataTypeClass datatypeClass)
	{
		Map dataTypeRulesMap = new HashMap();
		Map<String, List<String>> dataTypeImplicitRulesMap = new HashMap<String, List<String>>();
		Map<String, List<String>> dataTypeExplicitRulesMap = new HashMap<String, List<String>>();
		if (datatypeClass != null)
		{
			for (DataType type : datatypeClass.getDataType())
			{
				Validations validation = type.getValidations();
				List<String> implicitRuleNameList = new ArrayList<String>();
				List<String> explciteRuleNameList = new ArrayList<String>();
				for (edu.common.dynamicextensions.util.xml.DataTypeClass.DataType.Validations.ValidationRule validationRule : validation
						.getValidationRule())
				{
					if (validationRule.isIsImplicit())
					{
						implicitRuleNameList.add(validationRule.getName());
					}
					else
					{
						explciteRuleNameList.add(validationRule.getName());
					}
				}

				dataTypeImplicitRulesMap.put(type.getName(), implicitRuleNameList);
				dataTypeExplicitRulesMap.put(type.getName(), explciteRuleNameList);
				List<String> ruleNameList = new ArrayList<String>();
				ruleNameList.addAll(explciteRuleNameList);
				ruleNameList.addAll(implicitRuleNameList);

				dataTypeRulesMap.put(type.getName(), getRuleObjectsList(ruleNameList));
			}
		}
		controlsConfigurationObject.setDataTypeValidationRules(dataTypeRulesMap);
		controlsConfigurationObject.setDataTypesList(getNameValueBeansList(new ArrayList(
				dataTypeRulesMap.keySet())));
		controlsConfigurationObject.setDataTypeExplicitRules(dataTypeExplicitRulesMap);
		controlsConfigurationObject.setDataTypeImplicitRules(dataTypeImplicitRulesMap);
	}

	/**
	 * This method will update the commonValidation rules of given controlsConfigurationObject from the
	 * given controlNode.
	 * @param controlNode common validation node class object.
	 * @param controlsConfigurationObject object to be updated.
	 */
	private void updateCommonValidatioNRules(Control controlNode,
			ControlsConfigurationObject controlsConfigurationObject)
	{
		if (controlNode.getCommonValidation() != null)
		{
			List commonImplicitRules = new ArrayList();
			List commonExplicitRules = new ArrayList();
			List<String> ruleNameList = new ArrayList<String>();
			CommonValidation commonValidations = controlNode.getCommonValidation();
			for (CommonValidationRule valRule : commonValidations.getCommonValidationRule())
			{
				ruleNameList.add(valRule.getName());
				if (valRule.isIsImplicit())
				{
					commonImplicitRules.add(valRule.getName());
				}
				else
				{
					commonExplicitRules.add(valRule.getName());
				}

			}
			controlsConfigurationObject.setCommonValidationRules(getRuleObjectsList(ruleNameList));
			controlsConfigurationObject.setCommonExplicitRules(commonExplicitRules);
			controlsConfigurationObject.setCommonImplicitRules(commonImplicitRules);
		}
	}

	/**
	 * returns the implicit rule for given control & for given data type.
	 * @param controlName name of the control.
	 * @param dataType data type.
	 * @return list of rules for given control for given data type.
	 */
	public List<String> getAllImplicitRules(String controlName, String dataType)
	{
		Set<String> allImplicitRules = new HashSet<String>();

		ControlsConfigurationObject controlsConfiguration = (ControlsConfigurationObject) controlsConfigurationMap
				.get(controlName);
		if (controlsConfiguration != null)
		{
			Map dataTypeImplicitRulesMap = controlsConfiguration.getDataTypeImplicitRules();
			List<String> dataTypeImplicitRuleList = (List) dataTypeImplicitRulesMap.get(dataType);
			if (dataTypeImplicitRuleList != null)
			{
				allImplicitRules.addAll(dataTypeImplicitRuleList);
			}
			if(DynamicExtensionsUtility.isDataTypeNumeric(dataType))
			{
				allImplicitRules.addAll((List) dataTypeImplicitRulesMap.get(ProcessorConstants.DATATYPE_NUMBER));
			}
			List<String> commonImplicitRuleList = controlsConfiguration.getCommonImplicitRules();
			if (commonImplicitRuleList != null)
			{
				allImplicitRules.addAll(commonImplicitRuleList);
			}
		}
		return new ArrayList<String>(allImplicitRules);
	}

	/**
	 * It will return the list of RuleConfigurationObject for all the rules mentioned in the ruleValidationsList.
	 * @param ruleValidationsList list of ruleNames
	 * @return List of ruleObjects.
	 */
	private List<RuleConfigurationObject> getRuleObjectsList(List ruleValidationsList)
	{
		List<RuleConfigurationObject> ruleObjectsList = new ArrayList<RuleConfigurationObject>();
		Iterator rulesIterator = ruleValidationsList.iterator();
		while (rulesIterator.hasNext())
		{
			String ruleName = (String) rulesIterator.next();
			RuleConfigurationObject ruleObject = rulesConfigurationMap.get(ruleName);
			ruleObjectsList.add(ruleObject);
		}
		return ruleObjectsList;
	}

	/**
	 * This method converts the the passed list into namevalue beans objects list in order to help in rendering Ui.
	 * @param dataTypesList list Of all dataTypes
	 * @return List NameValueBeansList
	 */
	private List getNameValueBeansList(List dataTypesList)
	{
		List<NameValueBean> nameValueBeansList = new ArrayList<NameValueBean>();
		Iterator iter = dataTypesList.iterator();
		NameValueBean nameValueBeanAttribute;
		while (iter.hasNext())
		{
			String nodeName = (String) iter.next();
			nameValueBeanAttribute = new NameValueBean(nodeName, nodeName);
			nameValueBeansList.add(nameValueBeanAttribute);
		}

		return nameValueBeansList;
	}

	/**
	 *It will return the list of data types associated with the given control.
	 * @param controlName name of the control selected by user
	 * @return List dataTypes associated with the selected control
	 */
	public List getControlsDataTypes(String controlName)
	{
		ArrayList dataTypesList = null;
		if ((controlName != null) && (controlsConfigurationMap != null))
		{
			ControlsConfigurationObject controlsConfigurationObject = (ControlsConfigurationObject) controlsConfigurationMap
					.get(controlName);
			if (controlsConfigurationObject != null)
			{
				dataTypesList = (ArrayList) controlsConfigurationObject.getDataTypesList();
			}
		}
		return dataTypesList;
	}

	/**
	 * Returns the jspname for the selected control by user.
	 * @param controlName name of the control selected by user
	 * @return String jspname associated with the control
	 */
	public String getControlJspName(String controlName)
	{
		String jspName = null;
		if ((controlName != null) && (controlsConfigurationMap != null))
		{
			ControlsConfigurationObject controlsConfigurationObject = (ControlsConfigurationObject) controlsConfigurationMap
					.get(controlName);
			if (controlsConfigurationObject != null)
			{
				jspName = controlsConfigurationObject.getJspName();
			}
		}
		return jspName;
	}

	/**
	 * Returns the jspname for the selected control by user.
	 * @param controlName name of the control selected by user
	 * @return String jspname associated with the control
	 */
	public String getControlImagePath(String controlName)
	{
		String imagepath = null;
		if ((controlName != null) && (controlsConfigurationMap != null))
		{
			ControlsConfigurationObject controlsConfigurationObject = (ControlsConfigurationObject) controlsConfigurationMap
					.get(controlName);
			if (controlsConfigurationObject != null)
			{
				imagepath = controlsConfigurationObject.getImageFilePath();
			}
		}
		return imagepath;
	}

	/**
	 *It will return the display label for given control name.
	 * @param controlName name of the control selected by user
	 * @return ControlDisplayLabel DisplayLabel for the control selected by user
	 */
	public String getControlDisplayLabel(String controlName)
	{
		String displayLabel = null;
		if ((controlName != null) && (controlsConfigurationMap != null))
		{
			ControlsConfigurationObject controlsConfigurationObject = (ControlsConfigurationObject) controlsConfigurationMap
					.get(controlName);
			if (controlsConfigurationObject != null)
			{
				displayLabel = controlsConfigurationObject.getDisplayLabel();
			}
		}
		return displayLabel;
	}

	/**
	 * Returns ImplicitRules : The rules which are not going to be shown on ui.
	 * @param controlName name of the control selected by user
	 * @param dataType dataType selected by user.
	 * @return ArrayList list of all the rules related to control and its dataType selected.
	 */
	public List getImplicitRules(String controlName, String dataType)
	{
		List<String> implicitRules = new ArrayList<String>();
		if ((controlName != null) && (controlsConfigurationMap != null))
		{
			ControlsConfigurationObject controlsConfigurationObject = (ControlsConfigurationObject) controlsConfigurationMap
					.get(controlName);
			if (controlsConfigurationObject != null)
			{
				implicitRules.addAll(getListOfRules(controlsConfigurationObject
						.getCommonImplicitRules()));
				Map dataTypeRuleMap = controlsConfigurationObject.getDataTypeImplicitRules();
				implicitRules.addAll(getRulesForDataType(dataType, dataTypeRuleMap));
			}
		}
		return implicitRules;
	}

	/**
	 * Returns ExplicitRules : The rules which are will be shown on ui.
	 * @param controlName selected by user
	 * @param dataType selected by user
	 * @return ArrayList list of all the Explicit rules related to control and its dataType selected.
	 */
	public List getExplicitRules(String controlName, String dataType)
	{
		List<String> explicitRules = new ArrayList<String>();
		if ((controlName != null) && (controlsConfigurationMap != null))
		{
			ControlsConfigurationObject controlsConfigurationObject = (ControlsConfigurationObject) controlsConfigurationMap
					.get(controlName);

			if (controlsConfigurationObject != null)
			{
				Map dataTypeRuleMap = controlsConfigurationObject.getDataTypeExplicitRules();
				explicitRules.addAll(getRulesForDataType(dataType, dataTypeRuleMap));
			}
		}
		return explicitRules;
	}

	/**
	 * This method will return the list of rules for the given data type from the  dataTypeRuleMap.
	 * @param dataType data type name.
	 * @param dataTypeRuleMap map of data type vs rules list.
	 * @return
	 */
	private List<String> getRulesForDataType(String dataType, Map dataTypeRuleMap)
	{
		List<String> ruleList = new ArrayList<String>();
		List rulesList = (ArrayList) dataTypeRuleMap.get(dataType);

		if (rulesList != null)
		{
			Iterator iter = rulesList.iterator();
			while (iter.hasNext())
			{
				String ruleName = iter.next().toString();
				if (!ruleName.equalsIgnoreCase(""))
				{
					ruleList.add(ruleName);
				}
			}
		}
		return ruleList;
	}

	/**
	 * Returns the list in String format
	 * @param rules list of rules
	 * @return listOfRules
	 */
	private List getListOfRules(List rules)
	{
		ArrayList<String> listOfRules = new ArrayList<String>();

		Iterator iter = rules.iterator();
		while (iter.hasNext())
		{
			listOfRules.add(iter.next().toString());
		}
		return listOfRules;
	}

	/**
	 * gets the RuleConfigurationObject given a name of the rule.
	 * @param ruleName name  of the rule
	 * @return RuleConfigurationObject
	 */
	public RuleConfigurationObject getRuleObject(String ruleName)
	{
		RuleConfigurationObject ruleConfigurationObject = null;
		if (ruleName != null && rulesConfigurationMap != null)
		{
			ruleConfigurationObject = rulesConfigurationMap.get(ruleName);
		}

		return ruleConfigurationObject;
	}

	/**
	 * Gets all the controls required at the time of loading
	 * @return List of controls
	 */
	public List getListOfControls()
	{
		ArrayList listOfControls = null;
		if (controlsConfigurationMap != null)
		{
			listOfControls = (ArrayList) controlsConfigurationMap.get("ListOfControls");
		}
		return listOfControls;
	}

	/**
	 * Gets the DisplayLabels of the rules.
	 * @param ruleNamesList list of rules
	 * @return List list of DisplayLabels
	 */
	public List getRuleDisplayLabels(List ruleNamesList)
	{
		List<String> listOfDisplayLabels = new ArrayList<String>();
		Iterator iter = ruleNamesList.iterator();
		while (iter.hasNext())
		{
			String ruleName = iter.next().toString();
			RuleConfigurationObject ruleConfigurationObject = rulesConfigurationMap.get(ruleName);
			listOfDisplayLabels.add(ruleConfigurationObject.getDisplayLabel());
		}
		return listOfDisplayLabels;
	}

	/**
	 * This method returns rule instance for given rule name
	 * @param ruleName name of the rule
	 * @return ValidatorRuleInterface class instance of the rulename passed.
	 * @throws DynamicExtensionsSystemException dynamicExtensionsSystemException
	 */
	public ValidatorRuleInterface getValidatorRule(String ruleName)
			throws DynamicExtensionsSystemException
	{
		RuleConfigurationObject ruleConfiguration = rulesConfigurationMap.get(ruleName);
		try
		{
			Class ruleClass = Class.forName(ruleConfiguration.getRuleClassName());
			return (ValidatorRuleInterface) ruleClass.newInstance();
		}
		catch (Exception e)
		{
			throw new DynamicExtensionsSystemException(e.getMessage(), e);
		}

	}

	/**
	 *
	 * @param controlName selected by user
	 * @return Map of rules
	 */
	public Map getRulesMap(String controlName)
	{
		Map<String, List<RuleConfigurationObject>> rulesMap = new HashMap<String, List<RuleConfigurationObject>>();
		ControlsConfigurationObject ccf = (ControlsConfigurationObject) controlsConfigurationMap
				.get(controlName);
		List dataTypes = ccf.getDataTypesList();
		Iterator iter1;
		Iterator iter = dataTypes.iterator();
		while (iter.hasNext())
		{
			String dataType = ((NameValueBean) iter.next()).getName();
			List rules = getExplicitRules(controlName, dataType);
			iter1 = rules.iterator();
			List<RuleConfigurationObject> ruleObjects = new ArrayList<RuleConfigurationObject>();
			while (iter1.hasNext())
			{

				String ruleName = (String) iter1.next();
				ruleObjects.add(getRuleObject(ruleName));

			}
			rulesMap.put(dataType, ruleObjects);
		}

		List commonRules = getCommonExplicitRules(controlName);
		iter1 = commonRules.iterator();
		List<RuleConfigurationObject> commonRuleObjects = new ArrayList<RuleConfigurationObject>();
		while (iter1.hasNext())
		{
			String ruleName = (String) iter1.next();
			commonRuleObjects.add(getRuleObject(ruleName));
		}
		rulesMap.put("commons", commonRuleObjects);
		return rulesMap;
	}

	/**
	 * Returns ExplicitRules : The rules which are will be shown on ui.
	 * @param controlName selected by user
	 * @param dataType selected by user
	 * @return ArrayList list of all the Explicit rules related to control and its dataType selected.
	 */
	public List getCommonExplicitRules(String controlName)
	{
		ArrayList<String> explicitRules = new ArrayList<String>();
		if ((controlName != null) && (controlsConfigurationMap != null))
		{
			ControlsConfigurationObject controlsConfigurationObject = (ControlsConfigurationObject) controlsConfigurationMap
					.get(controlName);
			if (controlsConfigurationObject != null)
			{
				explicitRules.addAll(getListOfRules(controlsConfigurationObject
						.getCommonExplicitRules()));
			}
		}
		return explicitRules;
	}
}
