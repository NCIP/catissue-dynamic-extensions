
package edu.common.dynamicextensions.ui.webui.action;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

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
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.ApplyDataEntryFormProcessor;
import edu.common.dynamicextensions.processor.DeleteRecordProcessor;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.ui.webui.actionform.DataEntryForm;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.ui.webui.util.UserInterfaceiUtility;
import edu.common.dynamicextensions.ui.webui.util.WebUIManager;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DataValueMapUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.FormulaCalculator;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.validation.ValidatorUtil;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.logger.Logger;

/**
 * It populates the Attribute values entered in the dynamically generated controls.
 * @author chetan_patil
 */
public class ApplyDataEntryFormAction extends BaseDynamicExtensionsAction
{

	/*
	 * (non-Javadoc)
	 * @see org.apache.struts.actions.DispatchAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	{
		ActionForward actionForward = null;
		boolean isCallbackURL = false;
		List<String> errorList = null;
		if ((request.getParameter(DEConstants.IS_DIRTY) != null)
				&& request.getParameter(DEConstants.IS_DIRTY).equalsIgnoreCase(DEConstants.TRUE))
		{
			request.setAttribute(DEConstants.IS_DIRTY, DEConstants.TRUE);
		}
		Stack<ContainerInterface> containerStack = (Stack<ContainerInterface>) CacheManager
				.getObjectFromCache(request, DEConstants.CONTAINER_STACK);
		Stack<Map<BaseAbstractAttributeInterface, Object>> valueMapStack = (Stack<Map<BaseAbstractAttributeInterface, Object>>) CacheManager
				.getObjectFromCache(request, DEConstants.VALUE_MAP_STACK);
		String containerSize = request.getParameter(DEConstants.BREAD_CRUMB_POSITION);
		if (((containerStack != null) && !containerStack.isEmpty())
				&& ((valueMapStack != null) || !valueMapStack.isEmpty()))
		{
			//removeExtraAttribtes(containerStack.peek(), valueMapStack);
			try
			{
				DataEntryForm dataEntryForm = (DataEntryForm) form;
				String mode = dataEntryForm.getMode();
				if ((mode != null) && (mode.equals("edit")))
				{
					populateAndValidateValues(containerStack, valueMapStack, request, dataEntryForm);
					errorList = dataEntryForm.getErrorList();
				}

				actionForward = getMappingForwardAction(mapping, dataEntryForm, errorList, mode);
				if (((actionForward != null) && actionForward.getName().equals(
						"showDynamicExtensionsHomePage"))
						&& ((mode != null) && mode.equals("cancel")))
				{
					String recordIdentifier = dataEntryForm.getRecordIdentifier();
					isCallbackURL = redirectCallbackURL(request, response, recordIdentifier,
							WebUIManagerConstants.CANCELLED, dataEntryForm.getContainerId());
				}

				if (((actionForward != null) && actionForward.getName().equals(
						"showDynamicExtensionsHomePage"))
						&& ((mode != null) && mode.equals("delete")))
				{
					String recordIdentifier = dataEntryForm.getRecordIdentifier();
					deleteRecord(recordIdentifier, containerStack.firstElement());
					isCallbackURL = redirectCallbackURL(request, response, recordIdentifier,
							WebUIManagerConstants.DELETED, dataEntryForm.getContainerId());
				}

				else if ((actionForward == null) && (errorList != null) && errorList.isEmpty())
				{
					String recordIdentifier = storeParentContainer(valueMapStack, containerStack,
							request, dataEntryForm.getRecordIdentifier());
					isCallbackURL = redirectCallbackURL(request, response, recordIdentifier,
							WebUIManagerConstants.SUCCESS, dataEntryForm.getContainerId());
				}
				if ((containerSize != null) && (containerSize.trim().length() > 0))
				{
					long containerStackSize = Long.valueOf(containerSize);
					if ((request.getParameter(WebUIManagerConstants.MODE_PARAM_NAME) != null)
							&& (request.getParameter(WebUIManagerConstants.MODE_PARAM_NAME).trim()
									.length() > 0)
							&& (DEConstants.CANCEl.equalsIgnoreCase(request
									.getParameter(WebUIManagerConstants.MODE_PARAM_NAME)) || WebUIManagerConstants.EDIT_MODE
									.equalsIgnoreCase(request
											.getParameter(WebUIManagerConstants.MODE_PARAM_NAME))))
					{
						containerStackSize = containerStackSize + 1;
					}
					while (containerStack.size() != containerStackSize)
					{
						containerStack.pop();
						valueMapStack.pop();
					}
				}
			}
			catch (Exception exception)
			{
				Logger.out.error(exception.getMessage());
				return getExceptionActionForward(exception, mapping, request);
			}
		}

		if (isCallbackURL)
		{
			actionForward = null;
		}
		else if (actionForward == null)
		{
			if ((errorList != null) && errorList.isEmpty())
			{
				UserInterfaceiUtility.clearContainerStack(request);
			}
			actionForward = mapping.findForward(DEConstants.SUCCESS);
		}
		return actionForward;
	}

	/**
	 * @throws ParseException
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsSystemException
	 *
	 */
	public void populateAttributeValueMapForCalculatedAttributes(
			Map<BaseAbstractAttributeInterface, Object> fullValueMap,
			Map<BaseAbstractAttributeInterface, Object> valueMap,
			ContainerInterface containerInterface, Integer rowNumber)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		for (Map.Entry<BaseAbstractAttributeInterface, Object> entry : valueMap.entrySet())
		{
			BaseAbstractAttributeInterface attribute = entry.getKey();
			if (attribute instanceof CategoryAttributeInterface)
			{
				CategoryAttributeInterface categoryAttributeInterface = (CategoryAttributeInterface) attribute;
				Boolean isCalculatedAttribute = categoryAttributeInterface.getIsCalculated();
				if ((isCalculatedAttribute != null) && isCalculatedAttribute)
				{
					FormulaCalculator formulaCalculator = new FormulaCalculator();
					CategoryEntityInterface categoryEntityInterface = (CategoryEntityInterface) containerInterface
							.getAbstractEntity();
					String formulaResultValue = formulaCalculator.evaluateFormula(fullValueMap,
							categoryAttributeInterface, categoryEntityInterface.getCategory(),
							rowNumber);
					if (formulaResultValue != null)
					{
						entry.setValue(formulaResultValue);
					}
				}
			}
			else if (attribute instanceof CategoryAssociationInterface)
			{
				List<Map<BaseAbstractAttributeInterface, Object>> attributeValueMapList = (List<Map<BaseAbstractAttributeInterface, Object>>) entry
						.getValue();
				Integer entryNumber = 0;
				for (Map<BaseAbstractAttributeInterface, Object> map : attributeValueMapList)
				{
					entryNumber++;
					populateAttributeValueMapForCalculatedAttributes(fullValueMap, map,
							containerInterface, entryNumber);
				}
			}
		}
	}

	/**
	 *
	 * @param recordIdentfier
	 * @param containerInterface
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void deleteRecord(String recordIdentfier, ContainerInterface containerInterface)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		Long recordNumber = Long.valueOf(recordIdentfier);
		DeleteRecordProcessor.getInstance().deleteRecord(containerInterface, recordNumber);

	}

	/**
	 * This method gets the Callback URL from cache, reforms it and redirect the response to it.
	 * @param request HttpServletRequest to obtain session
	 * @param response HttpServletResponse to redirect the CallbackURL
	 * @param recordIdentifier Identifier of the record to reconstruct the CallbackURL
	 * @return true if CallbackURL is redirected, false otherwise
	 * @throws IOException
	 */
	private boolean redirectCallbackURL(HttpServletRequest request, HttpServletResponse response,
			String recordIdentifier, String webUIManagerConstant, String containerId)
			throws IOException
	{
		boolean isCallbackURL = false;
		String calllbackURL = (String) CacheManager.getObjectFromCache(request,
				DEConstants.CALLBACK_URL);
		if ((calllbackURL != null) && !calllbackURL.equals(""))
		{
			if (calllbackURL.contains("?"))
			{
				calllbackURL = calllbackURL + "&" + WebUIManager.getRecordIdentifierParameterName()
						+ "=" + recordIdentifier + "&"
						+ WebUIManager.getOperationStatusParameterName() + "="
						+ webUIManagerConstant + "&containerId=" + containerId;
			}
			else
			{
				calllbackURL = calllbackURL + "?" + WebUIManager.getRecordIdentifierParameterName()
						+ "=" + recordIdentifier + "&"
						+ WebUIManager.getOperationStatusParameterName() + "="
						+ webUIManagerConstant + "&containerId=" + containerId;
			}

			CacheManager.clearCache(request);
			response.sendRedirect(calllbackURL);
			isCallbackURL = true;
		}
		return isCallbackURL;
	}

	/**
	 * This method gets the ActionForward on the Exception.
	 * @param exception Exception instance
	 * @param mapping ActionMapping to get ActionForward
	 * @param request HttpServletRequest to save error messages in.
	 * @return Appropriate ActionForward.
	 */
	private ActionForward getExceptionActionForward(Exception exception, ActionMapping mapping,
			HttpServletRequest request)
	{
		ActionForward exceptionActionForward = null;
		String actionForwardString = catchException(exception, request);
		if ((actionForwardString == null) || (actionForwardString.equals("")))
		{
			exceptionActionForward = mapping.getInputForward();
		}
		else
		{
			exceptionActionForward = mapping.findForward(actionForwardString);
		}
		return exceptionActionForward;
	}

	/**
	 * This method sets dataentry operations parameters and returns the appropriate
	 * ActionForward depending on the "mode" of the operation and validation errors.
	 * @param mapping ActionMapping to get the ActionForward
	 * @param dataEntryForm ActionForm
	 * @param errorList List of validation error messages generated.
	 * @param mode Mode of the operation viz., edit, view, cancel
	 * @return ActionForward
	 */
	private ActionForward getMappingForwardAction(ActionMapping mapping,
			DataEntryForm dataEntryForm, List<String> errorList, String mode)
	{
		ActionForward actionForward = null;
		String dataEntryOperation = dataEntryForm.getDataEntryOperation();
		if (dataEntryOperation != null)
		{
			if (errorList == null)
			{
				dataEntryForm.setErrorList(new ArrayList<String>());
			}

			if ("insertChildData".equals(dataEntryOperation))
			{
				if ((errorList != null) && !(errorList.isEmpty()))
				{
					dataEntryForm.setDataEntryOperation("insertParentData");
					actionForward = mapping.findForward("loadParentContainer");
				}
				else if ((mode != null) && (mode.equals("cancel")))
				{
					dataEntryForm.setMode("edit");
					dataEntryForm.setDataEntryOperation("insertParentData");
					actionForward = mapping.findForward("loadParentContainer");
				}
				else
				{
					actionForward = mapping.findForward("loadChildContainer");
				}
			}
			else if ("insertParentData".equals(dataEntryOperation))
			{
				if ((errorList != null) && !(errorList.isEmpty()))
				{
					dataEntryForm.setDataEntryOperation("insertChildData");
					actionForward = mapping.findForward("loadChildContainer");
				}
				else if ((mode != null) && (mode.equals("cancel")))
				{
					actionForward = mapping.findForward("showDynamicExtensionsHomePage");
				}

				else
				{
					actionForward = mapping.findForward("loadParentContainer");
				}
			}
			else if ("calculateAttributes".equals(dataEntryOperation))
			{
				dataEntryForm.setDataEntryOperation("calculateAttributes");
				actionForward = mapping.findForward(DEConstants.SUCCESS);
			}
			else if ("skipLogicAttributes".equals(dataEntryOperation))
			{
				dataEntryForm.setDataEntryOperation("skipLogicAttributes");
				actionForward = mapping.findForward(DEConstants.SUCCESS);
			}
		}
		return actionForward;
	}

	/**
	 * This method returns messages on successful saving of an Entity
	 * @return ActionMessages ActionMessages
	 */
	private ActionMessages getMessageString(String messageKey)
	{
		ActionMessages actionMessages = new ActionMessages();
		actionMessages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(messageKey));
		return actionMessages;
	}

	/**
	 * This method gathers the values form the Dynamic UI and validate them using Validation framework
	 * @param containerStack Stack of Container which has the current Container at its top.
	 * @param valueMapStack Stack of Map of Attribute-Value pair which has Map for current Container at its top.
	 * @param request HttpServletRequest which is required to collect the values from UI form.
	 * @param dataEntryForm
	 * @param errorList List to store the validation error/warning messages which will be displayed on the UI.
	 * @throws FileNotFoundException if improper value is entered for FileUpload control.
	 * @throws DynamicExtensionsSystemException
	 * @throws IOException
	 * @throws ParseException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void populateAndValidateValues(Stack<ContainerInterface> containerStack,
			Stack<Map<BaseAbstractAttributeInterface, Object>> valueMapStack,
			HttpServletRequest request, DataEntryForm dataEntryForm) throws FileNotFoundException,
			DynamicExtensionsSystemException, IOException, DynamicExtensionsApplicationException
	{
		ContainerInterface containerInterface = containerStack.peek();
		List processedContainersList = new ArrayList<ContainerInterface>();
		DynamicExtensionsUtility.setAllInContextContainers(containerInterface,
				processedContainersList);
		Map<BaseAbstractAttributeInterface, Object> valueMap = valueMapStack.peek();

		valueMap = generateAttributeValueMap(containerInterface, request, dataEntryForm, "",
				valueMap, true);

		List<String> errorList = ValidatorUtil.validateEntity(valueMap, dataEntryForm
				.getErrorList(), containerInterface);

		AbstractEntityInterface abstractEntityInterface = containerInterface.getAbstractEntity();
		if (abstractEntityInterface instanceof CategoryEntityInterface)
		{
			if ("skipLogicAttributes".equals(dataEntryForm.getDataEntryOperation()))
			{
				String containerId = request.getParameter("containerId");
				String controlId = request.getParameter("controlId");
				String[] controlValue = request.getParameterValues("controlValue");
				String controlName = request.getParameter("controlName");
				ContainerInterface skipLogicContainer = DynamicExtensionsUtility
						.getContainerByIdentifier(containerId, containerInterface);
				ControlInterface skipLogicControl = DynamicExtensionsUtility
						.getControlByIdentifier(controlId, skipLogicContainer);
				List<ControlInterface> targetSkipControlsList = skipLogicControl
						.setSkipLogicControls(controlValue);
				ControlsUtility
						.populateAttributeValueMapForSkipLogicAttributes(
								valueMap, valueMap, -1, false, controlName,
								targetSkipControlsList, false);
			}
			populateAttributeValueMapForCalculatedAttributes(valueMap, valueMap,
					containerInterface, 0);
		}
		//Remove duplicate error messages by converting an error message list to hashset.
		HashSet<String> hashSet = new HashSet<String>(errorList);

		dataEntryForm.setErrorList(new LinkedList<String>(hashSet));
	}

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
			ContainerInterface containerInterface, HttpServletRequest request,
			DataEntryForm dataEntryForm, String rowId,
			Map<BaseAbstractAttributeInterface, Object> attributeValueMap, Boolean processOneToMany)
			throws FileNotFoundException, IOException, DynamicExtensionsSystemException
	{

		Collection<ControlInterface> controlCollection = containerInterface
				.getAllControlsUnderSameDisplayLabel();
		for (ControlInterface control : controlCollection)
		{
			if ((control != null) && (control.getBaseAbstractAttribute() != null))
			{
				Integer controlSequenceNumber = control.getSequenceNumber();
				if (controlSequenceNumber != null)
				{
					String controlName = control.getHTMLComponentName();

					if ((rowId != null) && !rowId.equals(""))
					{
						controlName = controlName + "_" + rowId;
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
								collectAssociationValues(request, dataEntryForm, controlName,
										control, attributeValueMap, processOneToMany, rowId);
							}
							else
							{
								collectAttributeValues(request, dataEntryForm, controlName,
										control, attributeValueMap, rowId);
							}
						}
						else
						{
							collectAttributeValues(request, dataEntryForm, controlName, control,
									attributeValueMap, rowId);
						}
					}
					else if (abstractAttribute instanceof AssociationMetadataInterface)
					{
						collectAssociationValues(request, dataEntryForm, controlName, control,
								attributeValueMap, processOneToMany, rowId);
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
	private void collectAssociationValues(HttpServletRequest request, DataEntryForm dataEntryForm,
			String controlName, ControlInterface control,
			Map<BaseAbstractAttributeInterface, Object> attributeValueMap,
			Boolean processOneToMany, String rowId) throws DynamicExtensionsSystemException,
			FileNotFoundException, IOException
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
				associationValueMaps = collectOneToManyContainmentValues(request, dataEntryForm,
						targetContainer.getId().toString(), control, associationValueMaps);
			}
			else
			{
				Map<BaseAbstractAttributeInterface, Object> oneToOneValueMap = null;

				if (!associationValueMaps.isEmpty() && (associationValueMaps.get(0) != null))
				{
					oneToOneValueMap = associationValueMaps.get(0);
				}
				else
				{
					oneToOneValueMap = new HashMap<BaseAbstractAttributeInterface, Object>();
					associationValueMaps.add(oneToOneValueMap);
				}

				generateAttributeValueMap(targetContainer, request, dataEntryForm, "",
						oneToOneValueMap, false);
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
						if (selectedValues != null)
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
						else
						{
							valueList.add(new HashMap());
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

				if ((selectedValue != null) && (selectedValue.trim().length() != 0))
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
			HttpServletRequest request, DataEntryForm dataEntryForm, String containerId,
			ControlInterface control,
			List<Map<BaseAbstractAttributeInterface, Object>> oneToManyContainmentValueList)
			throws FileNotFoundException, DynamicExtensionsSystemException, IOException
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
					dataEntryForm, counterStr, attributeValueMapForSingleRow, false);
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
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws DynamicExtensionsSystemException
	 */
	private void collectAttributeValues(HttpServletRequest request, DataEntryForm dataEntryForm,
			String controlName, ControlInterface control,
			Map<BaseAbstractAttributeInterface, Object> attributeValueMap, String rowId)
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
			FormFile formFile = null;
			formFile = (FormFile) dataEntryForm.getValue(controlName);
			boolean isValidExtension = true;
			if (!formFile.getFileName().equals(""))
			{
				isValidExtension = checkValidFormat(dataEntryForm, control, formFile.getFileName(),
						formFile.getFileSize());
			}
			else
			{
				attributeValueMap.put(abstractAttribute, control.getValue());
			}
			if (isValidExtension
					&& ((formFile.getFileName() != null) && !formFile.getFileName().equals("")))
			{
				FileAttributeRecordValue fileAttributeRecordValue = new FileAttributeRecordValue();
				fileAttributeRecordValue.setFileContent(formFile.getFileData());
				fileAttributeRecordValue.setFileName(formFile.getFileName());
				fileAttributeRecordValue.setContentType(formFile.getContentType());
				attributeValue = fileAttributeRecordValue;
				attributeValueMap.put(abstractAttribute, attributeValue);
			}

		}
		else if (control instanceof ComboBoxInterface)
		{
			String value = request.getParameter("combo" + controlName);
			if ((value != null) && value.equalsIgnoreCase("undefined"))
			{
				value = "1";
			}
			attributeValue = value;
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
	 * This method stores the container in the database. It updates the existing record or inserts a new record
	 * depending upon the availability of the record identifier variable.
	 * @param valueMapStack Stack storing the Map of Attributes and their corresponding values.
	 * @param containerStack Stack having Container at its top that is to be stored in database.
	 * @param request HttpServletRequest to store the operation message.
	 * @param recordIdentifier Identifier of the record in database that is to be updated.
	 * @return New identifier for a record if record is inserted otherwise the passed record identifier is returned.
	 * @throws NumberFormatException If record identifier is not a numeric value.
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 * @throws SQLException
	 */
	private String storeParentContainer(
			Stack<Map<BaseAbstractAttributeInterface, Object>> valueMapStack,
			Stack<ContainerInterface> containerStack, HttpServletRequest request,
			String recordIdentifier) throws NumberFormatException,
			DynamicExtensionsApplicationException, DynamicExtensionsSystemException, SQLException
	{
		String identifier = recordIdentifier;
		Map<BaseAbstractAttributeInterface, Object> rootValueMap = valueMapStack.firstElement();
		ContainerInterface rootContainerInterface = containerStack.firstElement();
		DataValueMapUtility.updateDataValueMapForDataEntry(rootValueMap, rootContainerInterface);
		ApplyDataEntryFormProcessor applyDataEntryFormProcessor = ApplyDataEntryFormProcessor
				.getInstance();

		String userId = (String) CacheManager.getObjectFromCache(request,
				WebUIManagerConstants.USER_ID);
		if (userId != null)
		{
			applyDataEntryFormProcessor.setUserId(Long.parseLong(userId.trim()));
		}

		String messageKey = "app.successfulDataInsertionMessage";
		if ((identifier != null) && !identifier.equals(""))
		{
			Boolean edited = applyDataEntryFormProcessor.editDataEntryForm(rootContainerInterface,
					rootValueMap, Long.valueOf(identifier));
			if (edited.booleanValue())
			{
				saveMessages(request, getMessageString(messageKey));
			}
		}
		else
		{
			identifier = applyDataEntryFormProcessor.insertDataEntryForm(rootContainerInterface,
					rootValueMap);
			saveMessages(request, getMessageString(messageKey));
		}

		return identifier;
	}

	/**
	 * This method is used to check for the valid File Extensions.
	 * @param dataEntryForm
	 * @param control
	 * @param selectedFile
	 * @param selectedFileSize
	 * @return true if valid file format, false otherwise
	 */
	private boolean checkValidFormat(DataEntryForm dataEntryForm, ControlInterface control,
			String selectedFile, int selectedFileSize)
	{
		String validFileExtension = "";
		String selectedfileExt = "";
		String allFileExtension = "";

		boolean isValidExtension = false;
		List<String> errorList = dataEntryForm.getErrorList();
		if (errorList == null)
		{
			errorList = new ArrayList<String>();
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
					validFileExtension = fileExtensionsIterator.getFileExtension();
					selectedfileExt = selectedFile.substring(selectedFile.lastIndexOf('.') + 1,
							selectedFile.length());
					allFileExtension = validFileExtension + "," + allFileExtension;

					if (selectedfileExt.equalsIgnoreCase(validFileExtension))
					{
						isValidExtension = true;
						break;
					}

				}
			}
			if (allFileExtension.length() > 0)
			{
				allFileExtension = allFileExtension.substring(0, allFileExtension.length() - 1);
			}
			if (!isValidExtension && !"".equals(allFileExtension))
			{
				List<String> parameterList = new ArrayList<String>();
				parameterList.add(allFileExtension);
				parameterList.add(control.getCaption());
				errorList.add(ApplicationProperties.getValue("app.selectProperFormat",
						parameterList));
			}

		}

		dataEntryForm.setErrorList(errorList);
		return isValidExtension;
	}



}