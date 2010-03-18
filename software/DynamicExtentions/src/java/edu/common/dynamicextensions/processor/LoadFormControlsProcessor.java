
package edu.common.dynamicextensions.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.interfaces.AbstractAttributeUIBeanInterface;
import edu.common.dynamicextensions.ui.interfaces.ControlUIBeanInterface;
import edu.common.dynamicextensions.ui.util.ControlConfigurationsFactory;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.ui.webui.actionform.ControlsForm;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.global.ApplicationProperties;

/**
 * @author sujay_narkar
 * @author mandar_shidhore
 *
 */
public class LoadFormControlsProcessor
{

	/**
	 * Protected constructor for entity processor
	 */
	protected LoadFormControlsProcessor()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * this method gets the new instance of the entity processor to the caller.
	 * @return LoadFormControlsProcessor LoadFormControlsProcessor instance
	 */
	public static LoadFormControlsProcessor getInstance()
	{
		return new LoadFormControlsProcessor();
	}

	/**
	 *
	 * @param controlsForm ControlsForm
	 * @param container ContainerInterface
	 * @return redirection page path
	 * @throws DynamicExtensionsSystemException dynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public void loadFormControls(ControlsForm controlsForm, ContainerInterface container)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		if ((container != null) && (controlsForm != null))
		{
			String controlOperation = controlsForm.getControlOperation();

			if (controlOperation == null || controlOperation.equals(""))
			{
				controlOperation = ProcessorConstants.OPERATION_ADD;
				controlsForm.setControlOperation(controlOperation);
			}

			if (controlOperation.equalsIgnoreCase(ProcessorConstants.OPERATION_ADD))
			{
				addControl(controlsForm);
			}
			else if (controlOperation.equalsIgnoreCase(ProcessorConstants.OPERATION_EDIT))
			{
				ControlInterface selectedControl = getSelectedControl(controlsForm, container);
				editControl(selectedControl, controlsForm, controlsForm);
			}

			// Initialize default values for controls.
			initializeControlDefaultValues(controlsForm, container);

			// initialize form attribute values.
			initializeFormAttributeValues(controlsForm, container);
		}
	}

	/**
	 * @param controlsForm
	 * @param container
	 * @throws DynamicExtensionsSystemException
	 */
	private void initializeFormAttributeValues(ControlsForm controlsForm,
			ContainerInterface container) throws DynamicExtensionsSystemException
	{
		ControlConfigurationsFactory ctrlCfgFact = ControlConfigurationsFactory.getInstance();
		String userSelectedTool = controlsForm.getUserSelectedTool();

		// List of tools/controls.
		controlsForm.setToolsList(ctrlCfgFact.getListOfControls());
		controlsForm.setSelectedControlCaption(ControlsUtility.getControlCaption(ctrlCfgFact
				.getControlDisplayLabel(userSelectedTool)));
		String jspName = ctrlCfgFact.getControlJspName(userSelectedTool);
		if (jspName == null)
		{
			jspName = "";
		}
		controlsForm.setHtmlFile(jspName);

		// Data types for selected control.
		controlsForm.setDataTypeList(ctrlCfgFact.getControlsDataTypes(userSelectedTool));

		// Set Entity Name as root.
		EntityInterface entity = (EntityInterface) container.getAbstractEntity();
		if (entity == null)
		{
			controlsForm.setRootName(entity.getName());
		}
		else
		{
			controlsForm.setRootName("");
		}
		controlsForm.setCurrentContainerName(container.getCaption());
		controlsForm.setChildList(ControlsUtility.getChildList(container));
		controlsForm.setControlRuleMap(getControlRulesMap(userSelectedTool));
	}

	/**
	 * @param controlsForm
	 */
	private void addControl(ControlsForm controlsForm)
	{
		String userSelectedTool = controlsForm.getUserSelectedTool();
		if (userSelectedTool == null || userSelectedTool.equals(""))
		{
			userSelectedTool = ProcessorConstants.DEFAULT_SELECTED_CONTROL;
		}

		controlsForm.setUserSelectedTool(userSelectedTool);
	}

	/**
	 * @param control
	 * @param controlUIBean
	 * @param attributeUIBean
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	public void editControl(ControlInterface control, ControlUIBeanInterface controlUIBean,
			AbstractAttributeUIBeanInterface attributeUIBean)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		ControlProcessor controlProcessor = ControlProcessor.getInstance();
		controlProcessor.populateControlUIBeanInterface(control, controlUIBean);

		AttributeProcessor attributeProcessor = AttributeProcessor.getInstance();
		if (control != null)
		{
			attributeProcessor.populateAttributeUIBeanInterface(
					(AbstractAttributeInterface) control.getBaseAbstractAttribute(),
					attributeUIBean);
		}

		String userSelectedTool = DynamicExtensionsUtility.getControlName(control);
		if (userSelectedTool == null || userSelectedTool.equals(""))
		{
			userSelectedTool = ProcessorConstants.DEFAULT_SELECTED_CONTROL;
		}

		controlUIBean.setUserSelectedTool(userSelectedTool);
	}

	/**
	 * @param controlsForm
	 * @param container
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void initializeControlDefaultValues(ControlsForm controlsForm,
			ContainerInterface container) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		String userSelectedTool = controlsForm.getUserSelectedTool();
		if (userSelectedTool != null)
		{
			if (userSelectedTool.equals(ProcessorConstants.TEXT_CONTROL))
			{
				initializeTextControlDefaultValues(controlsForm);
			}
			else if (userSelectedTool.equals(ProcessorConstants.COMBOBOX_CONTROL))
			{
				initializeComboboxControlDefaultValues(controlsForm, container);
			}
			else if (userSelectedTool.equals(ProcessorConstants.DATEPICKER_CONTROL))
			{
				initializeDatePickerControlDefaultValues(controlsForm);
			}
			else if (userSelectedTool.equals(ProcessorConstants.CHECKBOX_CONTROL))
			{
				initializeCheckBoxControlDefaultValues(controlsForm);
			}
			else if (userSelectedTool.equals(ProcessorConstants.RADIOBUTTON_CONTROL))
			{
				initializeOptionButtonControlDefaultValues(controlsForm, container);
			}
			else if (userSelectedTool.equals(ProcessorConstants.FILEUPLOAD_CONTROL))
			{
				initializeFileUploadControlDefaultValues(controlsForm);
			}
		}
	}

	/**
	 * @param controlsForm
	 */
	private void initializeFileUploadControlDefaultValues(ControlsForm controlsForm)
	{
		if (controlsForm.getSupportedFileFormatsList() == null)
		{
			controlsForm.setSupportedFileFormatsList(getFileFormatsList());
		}
		initializeFileFormats(controlsForm);
	}

	/**
	 * This method will separate out the file formats explicitly
	 * specified by the user from the supported file format list.
	 * @param controlsForm
	 */
	private void initializeFileFormats(ControlsForm controlsForm)
	{
		String unsupFileFrmts = null;
		List<String> supFileFrmts = controlsForm.getSupportedFileFormatsList();
		String[] usrSelFileFrmts = controlsForm.getFileFormats();
		if (usrSelFileFrmts != null)
		{
			int noOfSelFrmts = usrSelFileFrmts.length;
			for (int i = 0; i < noOfSelFrmts; i++)
			{
				String selFileFrmts = usrSelFileFrmts[i];
				if (!DynamicExtensionsUtility.isStringInList(selFileFrmts, supFileFrmts))
				{
					if ((unsupFileFrmts == null) || (unsupFileFrmts.equals("")))
					{
						unsupFileFrmts = selFileFrmts;
					}
					else
					{
						unsupFileFrmts = unsupFileFrmts + ProcessorConstants.FILE_FORMATS_SEPARATOR
								+ selFileFrmts;
					}
				}
			}
		}

		controlsForm.setFormat(unsupFileFrmts);
	}

	/**
	 * @return List<String> list of file formats available for file control.
	 */
	private List<String> getFileFormatsList()
	{
		// Initialize the DynamicExtensionsFileFormats.properties file.
		ApplicationProperties.initBundle("DynamicExtensionsFileFormats");
		ArrayList<String> fileFormats = new ArrayList<String>();
		String fileExtns = ApplicationProperties.getValue("fileExtensions");
		StringTokenizer strTokenizer = new StringTokenizer(fileExtns, ",");
		while (strTokenizer.hasMoreTokens())
		{
			fileFormats.add(strTokenizer.nextToken());
		}

		// Initialize the ApplicationResources.properties file once the purpose of file formats is complete.
		ApplicationProperties.initBundle("ApplicationResources");

		return fileFormats;
	}

	/**
	 * @param controlsForm
	 * @param container
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void initializeOptionButtonControlDefaultValues(ControlsForm controlsForm,
			ContainerInterface container) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		// Set default display choice.
		if ((controlsForm.getDisplayChoice() == null)
				|| (controlsForm.getDisplayChoice()
						.equals(ProcessorConstants.DISPLAY_CHOICE_LOOKUP)))
		{
			controlsForm.setDisplayChoice(ProcessorConstants.DEFAULT_DISPLAY_CHOICE_TYPE);
		}

		controlsForm.setGroupNames(getGroupNamesList(container));
		controlsForm.setSeparatorList(getSeparatorsList());

		if (controlsForm.getSelectedAttributes() == null)
		{
			controlsForm.setSelectedAttributes(new ArrayList<NameValueBean>());
		}

		if (controlsForm.getOptionDetails() == null)
		{
			controlsForm.setOptionDetails(new ArrayList());
		}
	}

	/**
	 * @param controlsForm
	 */
	private void initializeCheckBoxControlDefaultValues(ControlsForm controlsForm)
	{
		if (controlsForm.getAttributeDefaultValue() == null
				|| controlsForm.getAttributeDefaultValue().trim().equals(""))
		{
			controlsForm.setAttributeDefaultValue(ProcessorConstants.DEFAULT_CHECKBOX_VALUE);
		}
	}

	/**
	 * @param controlsForm
	 */
	private void initializeDatePickerControlDefaultValues(ControlsForm controlsForm)
	{
		// Date value type.
		if (controlsForm.getDateValueType() == null)
		{
			controlsForm.setDateValueType(ProcessorConstants.DEFAULT_DATE_VALUE);
		}
		// Date format.
		if (controlsForm.getFormat() == null)
		{
			controlsForm.setFormat(ProcessorConstants.DEFAULT_DATE_FORMAT);
		}
	}

	/**
	 * @param controlsForm
	 * @param container
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private void initializeComboboxControlDefaultValues(ControlsForm controlsForm,
			ContainerInterface container) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		// Set default display choice.
		if (controlsForm.getDisplayChoice() == null)
		{
			controlsForm.setDisplayChoice(ProcessorConstants.DEFAULT_DISPLAY_CHOICE_TYPE);
		}

		// Default list type.
		if (controlsForm.getAttributeMultiSelect() == null)
		{
			controlsForm.setAttributeMultiSelect(ProcessorConstants.DEFAULT_LIST_TYPE);
		}

		if (controlsForm.getFormTypeForLookup() == null)
		{
			controlsForm.setFormTypeForLookup(ProcessorConstants.DEFAULT_LOOKUP_TYPE);
		}

		controlsForm.setGroupNames(getGroupNamesList(container));
		controlsForm.setSeparatorList(getSeparatorsList());

		if (controlsForm.getSelectedAttributes() == null)
		{
			controlsForm.setSelectedAttributes(new ArrayList<NameValueBean>());
		}
		if (controlsForm.getOptionDetails() == null)
		{
			controlsForm.setOptionDetails(new ArrayList());
		}
	}

	/**
	 * @return List<NameValueBean> list of name value beans
	 */
	private List<NameValueBean> getSeparatorsList()
	{
		ArrayList<NameValueBean> sprtrs = new ArrayList<NameValueBean>();
		sprtrs.add(new NameValueBean("Comma", ","));
		sprtrs.add(new NameValueBean("Colon", ":"));
		sprtrs.add(new NameValueBean("Space", " "));
		sprtrs.add(new NameValueBean("Dot", "."));
		DynamicExtensionsUtility.sortNameValueBeanListByName(sprtrs);
		return sprtrs;
	}

	/**
	 * @param container
	 * @return List<NameValueBean> list of name value beans
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private List<NameValueBean> getGroupNamesList(ContainerInterface container)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		ArrayList<NameValueBean> groupNames = new ArrayList<NameValueBean>();
		EntityGroupInterface entityGroup = ((Entity) container.getAbstractEntity())
				.getEntityGroup();
		if (entityGroup.getId() != null)
		{
			NameValueBean groupName = new NameValueBean(entityGroup.getName(), entityGroup.getId());
			groupNames.add(groupName);
		}

		DynamicExtensionsUtility.sortNameValueBeanListByName(groupNames);
		return groupNames;
	}

	/**
	 * @param controlsForm
	 */
	private void initializeTextControlDefaultValues(ControlsForm controlsForm)
	{
		// Default Data type.
		if ((controlsForm.getDataType() == null) || (controlsForm.getDataType().equals("")))
		{
			controlsForm.setDataType(ProcessorConstants.DEFAULT_DATA_TYPE);
		}
		// Default single line type.
		if (controlsForm.getLinesType() == null)
		{
			controlsForm.setLinesType(ProcessorConstants.DEFAULT_LINE_TYPE);
		}
		// Measurement units list.
		if (controlsForm.getMeasurementUnitsList() == null)
		{
			controlsForm.setMeasurementUnitsList(getListOfMeasurementUnits());
		}
	}

	/**
	 * Get a List of measurement units.
	 * @return List<String>
	 */
	private List<String> getListOfMeasurementUnits()
	{
		List<String> measurementUnits = new ArrayList<String>();
		measurementUnits.add("none");
		measurementUnits.add("inches");
		measurementUnits.add("cm");
		measurementUnits.add("gms");
		measurementUnits.add("kms");
		measurementUnits.add("kgs");
		measurementUnits.add(ProcessorConstants.MEASUREMENT_UNIT_OTHER);
		return measurementUnits;
	}

	/**
	 * @param controlName Name of the control
	 * @return Map ControlRulesMap
	 * @throws DynamicExtensionsSystemException
	 */
	private Map getControlRulesMap(String controlName) throws DynamicExtensionsSystemException
	{
		ControlConfigurationsFactory ctrlCfgFact = ControlConfigurationsFactory.getInstance();
		return ctrlCfgFact.getRulesMap(controlName);
	}

	/**
	 * @param controlsForm
	 * @param container
	 * @return ControlInterface selected control.
	 */
	public ControlInterface getSelectedControl(ControlsForm controlsForm,
			ContainerInterface container)
	{
		ControlInterface selectedControl = null;
		if ((container != null) && (controlsForm != null))
		{
			String selectedControlId = controlsForm.getSelectedControlId();
			if ((selectedControlId != null) && (!selectedControlId.trim().equals("")))
			{
				selectedControl = container.getControlInterfaceBySequenceNumber(selectedControlId);
			}
		}

		return selectedControl;
	}
}