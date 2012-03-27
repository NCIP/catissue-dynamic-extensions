
package edu.common.dynamicextensions.ui.webui.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.FileAttributeRecordValue;
import edu.common.dynamicextensions.domain.FileAttributeTypeInformation;
import edu.common.dynamicextensions.domain.FileExtension;
import edu.common.dynamicextensions.domain.userinterface.AbstractContainmentControl;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AssociationMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAssociationInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.CheckBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ComboBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.FileUploadInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ListBoxInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.MultiSelectInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.SelectInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerUtil;
import edu.common.dynamicextensions.entitymanager.FileUploadManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.FormulaCalculator;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.dao.exception.DAOException;

public class FormDataCollectionUtility
{

	/**
	 *
	 * @param container
	 * @param request
	 * @param dataEntryForm
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws DynamicExtensionsSystemException
	 */
	private Map<BaseAbstractAttributeInterface, Object> generateAttributeValueMap(
			ContainerInterface containerInterface, HttpServletRequest request, String rowId,
			Map<BaseAbstractAttributeInterface, Object> attributeValueMap,
			Boolean processOneToMany, HashSet<String> errorList) throws FileNotFoundException,
			IOException, DynamicExtensionsSystemException
	{

		List<String> xssViolations = null;
		if (request.getAttribute(DEConstants.VIOLATING_PROPERTY_NAMES) instanceof List
				&& ((List<String>) request.getAttribute(DEConstants.VIOLATING_PROPERTY_NAMES))
						.size() > 0)
		{
			xssViolations = (List<String>) request
					.getAttribute(DEConstants.VIOLATING_PROPERTY_NAMES);
		}
		Collection<ControlInterface> controlCollection = containerInterface
				.getAllControlsUnderSameDisplayLabel();
		for (ControlInterface control : controlCollection)
		{
			if ((control != null) && (control.getBaseAbstractAttribute() != null))
			{
				Integer controlSequenceNumber = control.getSequenceNumber();
				if (controlSequenceNumber != null)
				{
					StringBuffer controlName = new StringBuffer(control.getHTMLComponentName());

					if ((rowId != null) && !rowId.equals(""))
					{
						controlName.append('_').append(rowId);
					}
					if (xssViolations != null && xssViolations.contains(controlName.toString()))
					{
						List<String> placeHolder = new ArrayList<String>();
						placeHolder.add(control.getCaption());
						errorList.add(ApplicationProperties.getValue(
								DEConstants.INVALID_CONTROL_VALUE, placeHolder));
					}
					BaseAbstractAttributeInterface abstractAttribute = control
							.getBaseAbstractAttribute();
					if (abstractAttribute instanceof AttributeMetadataInterface)
					{
						if (abstractAttribute instanceof CategoryAttributeInterface)
						{
							CategoryAttributeInterface categoryAttribute = (CategoryAttributeInterface) abstractAttribute;
							if (categoryAttribute.getAbstractAttribute() instanceof AssociationMetadataInterface)
							{
								collectAssociationValues(request, controlName.toString(), control,
										attributeValueMap, processOneToMany, errorList);
							}
							else
							{
								collectAttributeValues(request, controlName.toString(), control,
										attributeValueMap, errorList);
							}
						}
						else
						{
							collectAttributeValues(request, controlName.toString(), control,
									attributeValueMap, errorList);
						}
					}
					else if (abstractAttribute instanceof AssociationMetadataInterface)
					{
						collectAssociationValues(request, controlName.toString(), control,
								attributeValueMap, processOneToMany, errorList);
					}
				}
			}
		}

		return attributeValueMap;
	}

	/**
	 *
	 * @param request
	 * @param dataEntryForm
	 * @param controlName
	 * @param control
	 * @param attributeValueMap
	 * @throws DynamicExtensionsSystemException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void collectAssociationValues(HttpServletRequest request, String controlName,
			ControlInterface control,
			Map<BaseAbstractAttributeInterface, Object> attributeValueMap,
			Boolean processOneToMany, HashSet<String> errorList)
			throws DynamicExtensionsSystemException, FileNotFoundException, IOException
	{
		BaseAbstractAttributeInterface abstractAttribute = control.getBaseAbstractAttribute();
		List<Map<BaseAbstractAttributeInterface, Object>> associationValueMaps = (List<Map<BaseAbstractAttributeInterface, Object>>) attributeValueMap
				.get(abstractAttribute);

		if (associationValueMaps == null)
		{
			associationValueMaps = new ArrayList<Map<BaseAbstractAttributeInterface, Object>>();
		}

		if ((control instanceof AbstractContainmentControlInterface) && processOneToMany)
		{
			AbstractContainmentControlInterface associationControl = (AbstractContainmentControlInterface) control;
			ContainerInterface targetContainer = ((AbstractContainmentControlInterface) control)
					.getContainer();
			if (associationControl.isCardinalityOneToMany())
			{
				if (targetContainer.getId() != null)
				{
					associationValueMaps = collectOneToManyContainmentValues(request,
							targetContainer.getId().toString(), control, associationValueMaps,
							errorList);
				}
			}
			else
			{
				Map<BaseAbstractAttributeInterface, Object> oneToOneValueMap = null;

				if (associationValueMaps.isEmpty() || (associationValueMaps.get(0) == null))
				{
					oneToOneValueMap = new HashMap<BaseAbstractAttributeInterface, Object>();
					associationValueMaps.add(oneToOneValueMap);
				}
				else
				{
					oneToOneValueMap = associationValueMaps.get(0);
				}

				generateAttributeValueMap(targetContainer, request, "", oneToOneValueMap, false,
						errorList);
			}

			attributeValueMap.put(abstractAttribute, associationValueMaps);
		}
		else if ((control instanceof SelectInterface) || (control instanceof MultiSelectInterface))
		{
			AssociationInterface association = null;
			List valueList = new ArrayList();
			if (control instanceof MultiSelectInterface)
			{
				String[] selectedValues = request.getParameterValues(controlName);
				MultiSelectInterface multiSelectInt = (MultiSelectInterface) control;
				association = multiSelectInt.getBaseAbstractAttributeAssociation();
				if (association != null)
				{
					if (association.getIsCollection())
					{
						if (selectedValues == null)
						{
							valueList.add(new HashMap());

						}
						else
						{
							Collection<AbstractAttributeInterface> attributes = association
									.getTargetEntity().getAllAbstractAttributes();
							Collection<AbstractAttributeInterface> filteredAttributes = EntityManagerUtil
									.filterSystemAttributes(attributes);
							List<AbstractAttributeInterface> attributesList = new ArrayList<AbstractAttributeInterface>(
									filteredAttributes);
							for (String id : selectedValues)
							{
								Map dataMap = new HashMap();
								dataMap.put(attributesList.get(0), id);
								valueList.add(dataMap);
							}
						}
					}
					else
					{
						if (selectedValues != null)
						{
							for (String id : selectedValues)
							{
								Long identifier = Long.valueOf(id.trim());
								valueList.add(identifier);
							}
						}
					}
				}

			}
			else if (control instanceof ComboBoxInterface)
			{
				String selectedValue = request.getParameter(controlName);

				if ((selectedValue != null) && (!selectedValue.trim().equals("")))
				{
					valueList.add(Long.valueOf(selectedValue.trim()));
				}
			}
			if (!valueList.isEmpty())
			{
				attributeValueMap.put(abstractAttribute, valueList);
			}
		}
	}

	/**
	 *
	 * @param request
	 * @param dataEntryForm
	 * @param sequence
	 * @param control
	 * @param attributeValueMap
	 * @throws IOException
	 * @throws DynamicExtensionsSystemException
	 * @throws FileNotFoundException
	 */
	private List<Map<BaseAbstractAttributeInterface, Object>> collectOneToManyContainmentValues(
			HttpServletRequest request, String containerId, ControlInterface control,
			List<Map<BaseAbstractAttributeInterface, Object>> oneToManyContainmentValueList,
			HashSet<String> errorList) throws FileNotFoundException,
			DynamicExtensionsSystemException, IOException
	{
		AbstractContainmentControl containmentAssociationControl = (AbstractContainmentControl) control;
		int currentSize = oneToManyContainmentValueList.size();

		String parameterString = containerId + "_rowCount";
		String rowCountString = request.getParameter(parameterString);
		int rowCount = Integer.parseInt(rowCountString);

		for (int counter = 0; counter < rowCount; counter++)
		{
			Map<BaseAbstractAttributeInterface, Object> attributeValueMapForSingleRow = null;

			String counterStr = String.valueOf(counter + 1);
			if (counter < currentSize)
			{
				attributeValueMapForSingleRow = oneToManyContainmentValueList.get(counter);
			}
			else
			{
				attributeValueMapForSingleRow = new HashMap<BaseAbstractAttributeInterface, Object>();
				oneToManyContainmentValueList.add(attributeValueMapForSingleRow);
			}
			generateAttributeValueMap(containmentAssociationControl.getContainer(), request,
					counterStr, attributeValueMapForSingleRow, false, errorList);
		}

		return oneToManyContainmentValueList;
	}

	/**
	 *
	 * @param request
	 * @param dataEntryForm
	 * @param controlName
	 * @param control
	 * @param attributeValueMap
	 * @param errorList
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws DynamicExtensionsSystemException
	 */
	private void collectAttributeValues(HttpServletRequest request, String controlName,
			ControlInterface control,
			Map<BaseAbstractAttributeInterface, Object> attributeValueMap, HashSet<String> errorList)
			throws FileNotFoundException, IOException, DynamicExtensionsSystemException
	{
		BaseAbstractAttributeInterface abstractAttribute = control.getBaseAbstractAttribute();
		Object attributeValue = null;

		if (control instanceof ListBoxInterface)
		{
			String selectedListValue = request.getParameter(controlName);
			attributeValue = selectedListValue;
			attributeValueMap.put(abstractAttribute, attributeValue);
		}
		else if (control instanceof FileUploadInterface)
		{
			String fileName = "";
			String contentType = "";
			long fileId = 0;
			if (request.getParameter(controlName) != null
					&& !"".equals(request.getParameter(controlName)))
			{
				fileId = Long.valueOf(request.getParameter(controlName));
				fileName = request.getParameter(controlName + "_hidden");
				contentType = request.getParameter(controlName + "_contentType");
			}

			if (fileName != null && fileName.length() == 0 || contentType.length() == 0
					|| fileId == 0)
			{
				if (request.getParameter(controlName + "_hidden") == null)
				{
					attributeValueMap.put(abstractAttribute, null);
				}
				else
				{
					attributeValueMap.put(abstractAttribute, control.getValue());
				}
			}
			else
			{
				FileUploadManager fileUploadManager = new FileUploadManager();
				byte[] fileContent = null;
				try
				{
					fileContent = fileUploadManager.getFilefromDatabase(fileId);
					boolean isValidExtension = true;
					isValidExtension = checkValidFormat(control, fileName, errorList);
					if (isValidExtension && ((fileName != null) && !fileName.equals("")))
					{
						FileAttributeRecordValue fileAttributeRecordValue = new FileAttributeRecordValue();
						fileAttributeRecordValue.setFileContent(fileContent);
						fileAttributeRecordValue.setFileName(fileName);
						fileAttributeRecordValue.setContentType(contentType);
						attributeValue = fileAttributeRecordValue;
						attributeValueMap.put(abstractAttribute, attributeValue);
						fileUploadManager.deleteRecord(fileId);
					}
				}
				catch (SQLException e)
				{
					throw new DynamicExtensionsSystemException(
							"Error while fetching file from Database", e);
				}
				catch (DAOException e)
				{
					throw new DynamicExtensionsSystemException(
							"Error while fetching file from Database", e);
				}
			}
		}
		else if (control instanceof ComboBoxInterface)
		{
			String value = request.getParameter("combo" + controlName);
			if (value == null)
			{
				value = request.getParameter(controlName);
			}
			if ((value != null) && value.equalsIgnoreCase("undefined"))
			{
				value = "1";
			}
			else if (value == null)
			{
				value = ((AttributeMetadataInterface) abstractAttribute).getDefaultValue(null);
			}
			attributeValue = DynamicExtensionsUtility.getEscapedStringValue(value);
			attributeValueMap.put(abstractAttribute, attributeValue);
		}

		else
		{
			String value = request.getParameter(controlName);

			value = DynamicExtensionsUtility.getEscapedStringValue(value);

			if ((value != null) && value.equalsIgnoreCase("undefined"))
			{
				value = "1";
			}
			if ((value != null)
					&& ((value.equalsIgnoreCase(ProcessorConstants.DATE_ONLY_FORMAT))
							|| (value.equalsIgnoreCase(ProcessorConstants.DATE_TIME_FORMAT))
							|| (value.equalsIgnoreCase(ProcessorConstants.MONTH_YEAR_FORMAT)) || (value
							.equalsIgnoreCase(ProcessorConstants.YEAR_ONLY_FORMAT))))
			{
				value = "";
			}
			if (control instanceof CheckBoxInterface)
			{
				if (DynamicExtensionsUtility.isCheckBoxChecked(value))
				{
					//value = "unchecked";
					value = DynamicExtensionsUtility.getValueForCheckBox(true);
				}
				else
				{
					value = DynamicExtensionsUtility.getValueForCheckBox(false);
				}
			}

			attributeValue = value;
			attributeValueMap.put(abstractAttribute, attributeValue);
		}
	}

	/**
	 * This method is used to check for the valid File Extensions.
	 * @param dataEntryForm
	 * @param control
	 * @param selectedFile
	 * @param selectedFileSize
	 * @param errorList
	 * @return true if valid file format, false otherwise
	 */
	private boolean checkValidFormat(ControlInterface control, String selectedFile,
			HashSet<String> errorList)
	{

		String selectedfileExt = "";
		StringBuffer allFileExtension = new StringBuffer();;

		boolean isValidExtension = false;
		if (errorList == null)
		{
			errorList = new HashSet<String>();
		}

		AttributeTypeInformationInterface attributeTypeInformation = ((AttributeMetadataInterface) control
				.getBaseAbstractAttribute()).getAttributeTypeInformation();

		if (attributeTypeInformation instanceof FileAttributeTypeInformation)
		{
			FileAttributeTypeInformation fileAttibuteInformation = (FileAttributeTypeInformation) attributeTypeInformation;
			Collection<FileExtension> fileExtensionsCollection = fileAttibuteInformation
					.getFileExtensionCollection();

			if ((fileExtensionsCollection == null) || fileExtensionsCollection.isEmpty())
			{
				isValidExtension = true;
			}
			else
			{
				for (FileExtension fileExtensionsIterator : fileExtensionsCollection)
				{
					String validFileExtension = fileExtensionsIterator.getFileExtension();
					selectedfileExt = selectedFile.substring(selectedFile.lastIndexOf('.') + 1,
							selectedFile.length());
					if ("".equals(allFileExtension.toString()))
					{
						allFileExtension.append(validFileExtension);
					}
					else
					{
						allFileExtension.append(",").append(validFileExtension);
					}

					if (selectedfileExt.equalsIgnoreCase(validFileExtension))
					{
						isValidExtension = true;
						break;
					}

				}
			}
			if (!isValidExtension && !"".equals(allFileExtension.toString()))
			{
				List<String> parameterList = new ArrayList<String>();
				parameterList.add(allFileExtension.toString());
				parameterList.add(control.getCaption());
				errorList.add(ApplicationProperties.getValue("app.selectProperFormat",
						parameterList));
			}

		}
		return isValidExtension;
	}

	/**
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	public void populateAttributeValueMapForCalculatedAttributes(
			Map<BaseAbstractAttributeInterface, Object> fullValueMap,
			Map<BaseAbstractAttributeInterface, Object> valueMap,
			ContainerInterface rootContainerInterface, Integer rowNumber,
			ContainerInterface currentContainer) throws DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException
	{
		for (ControlInterface controlInterface : currentContainer
				.getAllControlsUnderSameDisplayLabel())
		{
			if (valueMap.get(controlInterface.getBaseAbstractAttribute()) != null)
			{
				BaseAbstractAttributeInterface attribute = controlInterface
						.getBaseAbstractAttribute();
				if (attribute instanceof CategoryAttributeInterface)
				{
					CategoryAttributeInterface categoryAttributeInterface = (CategoryAttributeInterface) attribute;
					Boolean isCalculatedAttribute = categoryAttributeInterface.getIsCalculated();
					if ((isCalculatedAttribute != null) && isCalculatedAttribute)
					{
						FormulaCalculator formulaCalculator = new FormulaCalculator();
						CategoryEntityInterface categoryEntityInterface = (CategoryEntityInterface) rootContainerInterface
								.getAbstractEntity();
						String formulaResultValue = formulaCalculator.evaluateFormula(fullValueMap,
								categoryAttributeInterface, categoryEntityInterface.getCategory(),
								rowNumber);
						if (formulaResultValue != null)
						{
							valueMap.put(attribute, formulaResultValue);
						}
					}
				}
				else if (attribute instanceof CategoryAssociationInterface)
				{
					AbstractContainmentControlInterface containmentControl = (AbstractContainmentControlInterface) controlInterface;
					List<Map<BaseAbstractAttributeInterface, Object>> attributeValueMapList = (List<Map<BaseAbstractAttributeInterface, Object>>) valueMap
							.get(controlInterface.getBaseAbstractAttribute());
					Integer entryNumber = 0;
					for (Map<BaseAbstractAttributeInterface, Object> map : attributeValueMapList)
					{
						entryNumber++;
						populateAttributeValueMapForCalculatedAttributes(fullValueMap, map,
								rootContainerInterface, entryNumber, containmentControl
										.getContainer());
					}
				}
			}
		}
	}

	public HashSet<String> populateAndValidateValues(HttpServletRequest request)
			throws FileNotFoundException, DynamicExtensionsSystemException, IOException,
			DynamicExtensionsApplicationException
	{
		return populateAndValidateValues(request, request
				.getParameter(WebUIManagerConstants.ISDRAFT));
	}

	/**
	 * This method gathers the values form the Dynamic UI and validate them using Validation framework
	 * @param containerInterface2 Stack of Container which has the current Container at its top.
	 * @param map Stack of Map of Attribute-Value pair which has Map for current Container at its top.
	 * @param request HttpServletRequest which is required to collect the values from UI form.
	 * @param dataEntryForm
	 * @param ERROR_LIST List to store the validation error/warning messages which will be displayed on the UI.
	 * @return 
	 * @throws FileNotFoundException if improper value is entered for FileUpload control.
	 * @throws DynamicExtensionsSystemException
	 * @throws IOException
	 * @throws DynamicExtensionsApplicationException
	 */
	public HashSet<String> populateAndValidateValues(HttpServletRequest request, String isDraft)
			throws FileNotFoundException, DynamicExtensionsSystemException, IOException,
			DynamicExtensionsApplicationException
	{

		Stack<ContainerInterface> containerStack = (Stack<ContainerInterface>) CacheManager
				.getObjectFromCache(request, DEConstants.CONTAINER_STACK);
		Stack<Map<BaseAbstractAttributeInterface, Object>> valueMapStack = (Stack<Map<BaseAbstractAttributeInterface, Object>>) CacheManager
				.getObjectFromCache(request, DEConstants.VALUE_MAP_STACK);

		ContainerInterface containerInterface = containerStack.peek();
		Map<BaseAbstractAttributeInterface, Object> valueMap = valueMapStack.peek();
		Date encounterDate = ControlsUtility.getFormattedDate(request
				.getParameter(Constants.ENCOUNTER_DATE));
		Map<String, Object> contextParameter = new HashMap<String, Object>();
		contextParameter.put(Constants.ENCOUNTER_DATE, encounterDate);
		containerInterface.setContextParameter(contextParameter);
		DynamicExtensionsUtility.setEncounterDateToChildContainer(containerInterface,
				contextParameter);
		List processedContainersList = new ArrayList<ContainerInterface>();
		DynamicExtensionsUtility.setAllInContextContainers(containerInterface,
				processedContainersList);

		HashSet<String> errorList = new HashSet<String>();

		valueMap = generateAttributeValueMap(containerInterface, request, "", valueMap, true,
				errorList);
		errorList = DomainObjectFactory.getInstance().getValidatorInstance(isDraft).validateEntity(
				valueMap, errorList, containerInterface, false);

		AbstractEntityInterface abstractEntityInterface = containerInterface.getAbstractEntity();
		if (abstractEntityInterface instanceof CategoryEntityInterface)
		{

			populateAttributeValueMapForCalculatedAttributes(valueMap, valueMap,
					containerInterface, 0, containerInterface);
		}

		//remove stack if data is submitted for the subform.
		if (isSubformSubmitted(request, errorList))
		{
			popStack(request);
		}
		if(!Boolean.valueOf(isDraft))
		{
			request.setAttribute(DEConstants.ERRORS_LIST, errorList);	
		}
		
		return errorList;
	}

	private boolean isSubformSubmitted(HttpServletRequest request, HashSet<String> errorList)
	{
		return !isAjaxAction(request)
				&& "calculateAttributes".equals(request
						.getParameter(Constants.DATA_ENTRY_OPERATION)) && errorList.isEmpty();
	}

	@SuppressWarnings("unchecked")
	public static void popStack(HttpServletRequest request)
	{
		Stack<ContainerInterface> containerStack = (Stack<ContainerInterface>) CacheManager
				.getObjectFromCache(request, DEConstants.CONTAINER_STACK);
		Stack<Map<BaseAbstractAttributeInterface, Object>> valueMapStack = (Stack<Map<BaseAbstractAttributeInterface, Object>>) CacheManager
				.getObjectFromCache(request, DEConstants.VALUE_MAP_STACK);

		Long scrollPos = 0L;
		Stack<Long> scrollTopStack = (Stack<Long>) CacheManager.getObjectFromCache(request,
				DEConstants.SCROLL_TOP_STACK);
		scrollPos = scrollTopStack.peek();
		request.setAttribute(DEConstants.SCROLL_POSITION, scrollPos);

		UserInterfaceiUtility.removeContainerInfo(containerStack, valueMapStack);
		scrollTopStack.pop();

	}

	public static boolean isAjaxAction(HttpServletRequest request)
	{
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}

}
