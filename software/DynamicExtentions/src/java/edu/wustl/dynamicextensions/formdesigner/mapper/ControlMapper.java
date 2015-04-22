
package edu.wustl.dynamicextensions.formdesigner.mapper;

import java.util.HashMap;
import java.util.Map;

import edu.common.dynamicextensions.domain.nui.CheckBox;
import edu.common.dynamicextensions.domain.nui.ComboBox;
import edu.common.dynamicextensions.domain.nui.Container;
import edu.common.dynamicextensions.domain.nui.Control;
import edu.common.dynamicextensions.domain.nui.DatePicker;
import edu.common.dynamicextensions.domain.nui.FileUploadControl;
import edu.common.dynamicextensions.domain.nui.Label;
//import edu.common.dynamicextensions.domain.nui.ListBox;
import edu.common.dynamicextensions.domain.nui.MultiSelectCheckBox;
import edu.common.dynamicextensions.domain.nui.MultiSelectListBox;
import edu.common.dynamicextensions.domain.nui.NumberField;
import edu.common.dynamicextensions.domain.nui.PageBreak;
import edu.common.dynamicextensions.domain.nui.RadioButton;
import edu.common.dynamicextensions.domain.nui.StringTextField;
import edu.common.dynamicextensions.domain.nui.SubFormControl;
import edu.common.dynamicextensions.domain.nui.TextArea;
import edu.common.dynamicextensions.domain.nui.TextField;
import edu.common.dynamicextensions.domain.nui.ValidationRule;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.wustl.dynamicextensions.formdesigner.utility.CSDConstants;

/**
 * @author Sanjay
 *
 */
public class ControlMapper {

	protected Container rootContainer = null;

	public Container getRootContainer() {
		return rootContainer;
	}

	public void setRootContainer(Container rootContainer) {
		this.rootContainer = rootContainer;
	}

	private final Map<String, DefaultControlMapper> CONTROL_MAPPER_MAP = new HashMap<String, DefaultControlMapper>() {

		private static final long serialVersionUID = 1252410916139427174L;

		{
			put(CSDConstants.STRING_TEXT_FIELD, new StringTextFieldMapper());
			put(CSDConstants.NUMERIC_FIELD, new NumericFieldMapper());
			put(CSDConstants.TEXT_AREA, new TextAreaMapper());
			put(CSDConstants.RADIO_BUTTON, new RadioButtonMapper());
			put(CSDConstants.CHECK_BOX, new CheckBoxMapper());
			put(CSDConstants.LIST_BOX, new MultiSelectBoxMapper());
			put(CSDConstants.MULTISELECT_BOX, new MultiSelectBoxMapper());
			put(CSDConstants.MULTISELECT_CHECK_BOX, new MultiSelectCheckBoxMapper());
			put(CSDConstants.DATE_PICKER, new DatePickerMapper());
			put(CSDConstants.FILE_UPLOAD, new FileUploadMapper());
			put(CSDConstants.NOTE, new NoteMapper());
			put(CSDConstants.HEADING, new HeadingMapper());
			put(CSDConstants.SUB_FORM, new SubFormMapper());
			put(CSDConstants.LABEL, new LabelMapper());
			put(CSDConstants.PAGE_BREAK, new PageBreakMapper());
			put(CSDConstants.COMBO_BOX, new ComboBoxMapper());
		}
	};

	/**
	 * derives the type of control from the instance
	 * @param control
	 * @return
	 */
	private String getControlType(Control control) {
		String type = "";
		if (control instanceof StringTextField) {
			type = CSDConstants.STRING_TEXT_FIELD;
		} else if (control instanceof NumberField) {
			type = CSDConstants.NUMERIC_FIELD;
		} else if (control instanceof DatePicker) {
			type = CSDConstants.DATE_PICKER;
		} else if (control instanceof TextArea) {
			type = CSDConstants.TEXT_AREA;
		} else if (control instanceof RadioButton) {
			type = CSDConstants.RADIO_BUTTON;
		} else if (control instanceof CheckBox) {
			type = CSDConstants.CHECK_BOX;
		} else if (control instanceof MultiSelectListBox) {
			type = CSDConstants.MULTISELECT_BOX;
		} else if (control instanceof MultiSelectCheckBox) {
			type = CSDConstants.MULTISELECT_CHECK_BOX;
		} else if (control instanceof FileUploadControl) {
			type = CSDConstants.FILE_UPLOAD;
		} else if (control instanceof ComboBox) {
			type = CSDConstants.COMBO_BOX;
		} else if (control instanceof Label) {
			if (((Label) control).isHeading()) {
				type = CSDConstants.HEADING;
			} else if (((Label) control).isNote()) {
				type = CSDConstants.NOTE;
			} else {
				type = CSDConstants.LABEL;
			}
		} else if (control instanceof SubFormControl) {
			type = CSDConstants.SUB_FORM;
		} else if (control instanceof PageBreak) {
			type = CSDConstants.PAGE_BREAK;
		}
		return type;
	}

	public Control propertiesToControl(Properties controlProps, Container container) throws Exception {

		String type = controlProps.getString(CSDConstants.CONTROL_TYPE);
		return ((DefaultControlMapper) CONTROL_MAPPER_MAP.get(type)).propertiesToControl(controlProps, container);
	}

	public Properties controlToProperties(Control control, Container container) {
		return CONTROL_MAPPER_MAP.get(getControlType(control)).controlToProperties(control, container);
	}

	/**
	 * Mapper for String Text Field
	 */
	private class StringTextFieldMapper extends TextFieldControlMapper {

		@Override
		public Control propertiesToControl(Properties controlProps, Container container) {
			StringTextField control = new StringTextField();
			setTextFieldProperties(controlProps, control);
			return control;
		}

		@Override
		public Properties controlToProperties(Control control, Container container) {
			Properties controlProps = new Properties();
			controlProps.setProperty(CSDConstants.CONTROL_TYPE, CSDConstants.STRING_TEXT_FIELD);
			getTextFieldProperties(controlProps, (TextField) control);
			return controlProps;
		}
	}

	/**
	 * Mapper for Numeric field
	 *
	 */
	private class NumericFieldMapper extends TextFieldControlMapper {

		@Override
		public Control propertiesToControl(Properties controlProps, Container container) {
			NumberField control = new NumberField();
			setTextFieldProperties(controlProps, control);

			Integer noOfDigits = controlProps.getInteger("noOfDigits");
			if (noOfDigits != null) {
				control.setNoOfDigits(noOfDigits);
			}

			Integer noOfDigitsAfterDecimal = controlProps.getInteger("noOfDigitsAfterDecimal");
			if (noOfDigitsAfterDecimal != null) {
				control.setNoOfDigitsAfterDecimal(noOfDigitsAfterDecimal);
			}

			String minimumValue = controlProps.getString("minimumValue");
			if (minimumValue != null) {
				control.setMinValue(minimumValue);
			}

			String maximumValue = controlProps.getString("maximumValue");
			if (maximumValue != null) {
				control.setMaxValue(maximumValue);
			}

			control.setCalculatedSourceControl(controlProps.getBoolean("isCalculatedSourceControl"));

			control.setCalculated(controlProps.getBoolean("isCalculated"));

			return control;
		}

		@Override
		public Properties controlToProperties(Control control, Container container) {
			NumberField numericControl = (NumberField) control;
			Properties controlProps = new Properties();
			controlProps.setProperty(CSDConstants.CONTROL_TYPE, CSDConstants.NUMERIC_FIELD);
			getTextFieldProperties(controlProps, (TextField) control);
			controlProps.setProperty("noOfDigits", numericControl.getNoOfDigits());
			controlProps.setProperty("noOfDigitsAfterDecimal", numericControl.getNoOfDigitsAfterDecimal());
			controlProps.setProperty("isCalculated", numericControl.isCalculated());
			controlProps.setProperty("isCalculatedSourceControl", numericControl.isCalculatedSourceControl());
			if (numericControl.isCalculated()) {
				String formula = numericControl.getFormula();
				if (formula != null) {
					controlProps.setProperty("formula", rootContainer.getUdnFormula(formula));
				}

			}
			if(numericControl.getValidationRules()!=null && !numericControl.getValidationRules().isEmpty())
			{
				ValidationRule vr=numericControl.getValidationRule("range");
				Map values=vr.getParams();
				controlProps.setProperty("minimumValue", values.get("min"));
				controlProps.setProperty("maximumValue", values.get("max"));
			}

			return controlProps;
		}
	}

	/**
	 * Mapper for Text area
	 *
	 */
	private class TextAreaMapper extends TextFieldControlMapper {

		@Override
		public Control propertiesToControl(Properties controlProps, Container container) {
			TextArea control = new TextArea();
			setTextFieldProperties(controlProps, (TextField) control);
			controlProps.setProperty(CSDConstants.CONTROL_TYPE, CSDConstants.TEXT_AREA);
			Integer noOfRows = controlProps.getInteger("noOfRows");
			if (noOfRows != null) {
				control.setNoOfRows(noOfRows);
			}
			return control;
		}

		@Override
		public Properties controlToProperties(Control control, Container container) {
			Properties controlProps = new Properties();
			controlProps.setProperty(CSDConstants.CONTROL_TYPE, CSDConstants.TEXT_AREA);
			getCommonProperties(controlProps, control);
			getTextFieldProperties(controlProps, (TextField) control);
			controlProps.setProperty("noOfRows", ((TextArea) control).getNoOfRows());
			return controlProps;
		}
	}

	/**
	 * Mapper for Radio Button Control
	 *
	 */
	private class RadioButtonMapper extends DefaultControlMapper {

		@Override
		public Control propertiesToControl(Properties controlProps, Container container) throws Exception {
			RadioButton control = new RadioButton();
			setCommonProperties(controlProps, control);
			control.setPvDataSource(PvMapper.propertiesToPvDataSource(controlProps));
			Integer optionsPerRow = controlProps.getInteger("optionsPerRow");
			if (optionsPerRow != null) {
				control.setOptionsPerRow(optionsPerRow);
			}
			return control;
		}

		@Override
		public Properties controlToProperties(Control control, Container container) {
			Properties controlProps = new Properties();
			controlProps.setProperty(CSDConstants.CONTROL_TYPE, CSDConstants.RADIO_BUTTON);
			getCommonProperties(controlProps, control);
			PvMapper.pVDataSourcetoProperties(((RadioButton) control).getPvDataSource(), controlProps);
			controlProps.setProperty("optionsPerRow", ((RadioButton) control).getOptionsPerRow());
			return controlProps;
		}
	}

	/**
	 * Mapper for check box control
	 *
	 */
	private class CheckBoxMapper extends DefaultControlMapper {

		@Override
		public Control propertiesToControl(Properties controlProps, Container container) {
			CheckBox control = new CheckBox();
			setCommonProperties(controlProps, control);
			control.setDefaultValueChecked(controlProps.getBoolean("isChecked"));
			return control;
		}

		@Override
		public Properties controlToProperties(Control control, Container container) {
			Properties controlProps = new Properties();
			controlProps.setProperty(CSDConstants.CONTROL_TYPE, CSDConstants.CHECK_BOX);
			getCommonProperties(controlProps, control);
			controlProps.setProperty("isChecked", ((CheckBox) control).isDefaultValueChecked());
			return controlProps;
		}
	}

	/**
	 * Mapper for list box
	 *
	 */
	/*private class ListBoxMapper extends DefaultControlMapper {

		@Override
		public Control propertiesToControl(Properties controlProps, Container container) throws Exception {
			ListBox control = new ListBox();
			setCommonProperties(controlProps, control);
			//control.setAutoCompleteDropdownEnabled(true);
			control.setPvDataSource(PvMapper.propertiesToPvDataSource(controlProps));
			return control;
		}

		@Override
		public Properties controlToProperties(Control control, Container container) {
			Properties controlProps = new Properties();
			controlProps.setProperty(CSDConstants.CONTROL_TYPE, CSDConstants.LIST_BOX);
			getCommonProperties(controlProps, control);
			//controlProps.setProperty("autoComplete", true);
			PvMapper.pVDataSourcetoProperties(((ListBox) control).getPvDataSource(), controlProps);
			return controlProps;
		}
	}*/

	/**
	 * Mapper for multi select box
	 *
	 */
	private class MultiSelectBoxMapper extends DefaultControlMapper {

		@Override
		public Control propertiesToControl(Properties controlProps, Container container) throws Exception {
			MultiSelectListBox control = new MultiSelectListBox();
			setCommonProperties(controlProps, control);
			control.setPvDataSource(PvMapper.propertiesToPvDataSource(controlProps));
			control.setAutoCompleteDropdownEnabled(controlProps.getBoolean("autoComplete"));
			return control;
		}

		@Override
		public Properties controlToProperties(Control control, Container container) {
			Properties controlProps = new Properties();
			boolean autoComplete = ((MultiSelectListBox) control).isAutoCompleteDropdownEnabled();

			if (autoComplete) {
				controlProps.setProperty(CSDConstants.CONTROL_TYPE, CSDConstants.LIST_BOX);
			} else {
				controlProps.setProperty(CSDConstants.CONTROL_TYPE, CSDConstants.MULTISELECT_BOX);
			}

			getCommonProperties(controlProps, control);
			PvMapper.pVDataSourcetoProperties(((MultiSelectListBox) control).getPvDataSource(), controlProps);
			controlProps.setProperty("autoComplete", autoComplete);
			return controlProps;
		}
	}

	/**
	 * Mapper for multi select check box
	 *
	 */
	private class MultiSelectCheckBoxMapper extends DefaultControlMapper {

		@Override
		public Control propertiesToControl(Properties controlProps, Container container) throws Exception {
			MultiSelectCheckBox control = new MultiSelectCheckBox();
			setCommonProperties(controlProps, control);
			control.setPvDataSource(PvMapper.propertiesToPvDataSource(controlProps));
			Integer optionsPerRow = controlProps.getInteger("optionsPerRow");
			if (optionsPerRow != null) {
				control.setOptionsPerRow(optionsPerRow);
			}
			return control;
		}

		@Override
		public Properties controlToProperties(Control control, Container container) {
			Properties controlProps = new Properties();
			controlProps.setProperty(CSDConstants.CONTROL_TYPE, CSDConstants.MULTISELECT_CHECK_BOX);
			getCommonProperties(controlProps, control);
			PvMapper.pVDataSourcetoProperties(((MultiSelectCheckBox) control).getPvDataSource(), controlProps);
			controlProps.setProperty("optionsPerRow", ((MultiSelectCheckBox) control).getOptionsPerRow());
			return controlProps;
		}
	}

	/**
	 * Mapper for date picker
	 *
	 */
	private class DatePickerMapper extends DefaultControlMapper {

		@Override
		public Control propertiesToControl(Properties controlProps, Container container) {
			DatePicker control = new DatePicker();
			setCommonProperties(controlProps, control);
			String format = controlProps.getString("format");
			if (format != null) {
				control.setFormat(displayFormatToActualFormat(format));
			}
			return control;
		}

		@Override
		public Properties controlToProperties(Control control, Container container) {
			Properties controlProps = new Properties();
			controlProps.setProperty(CSDConstants.CONTROL_TYPE, CSDConstants.DATE_PICKER);
			controlProps.setProperty("format", actualFormatToDisplayFormat(((DatePicker) control).getFormat()));
			getCommonProperties(controlProps, control);
			return controlProps;
		}

		/**
		 * @param actualFormat
		 * @return
		 */
		private String displayFormatToActualFormat(String displayFormat) {
			String format = "";
			if (displayFormat.equalsIgnoreCase("DATE")) {
				format = ProcessorConstants.DATE_ONLY_FORMAT;
			} else if (displayFormat.equalsIgnoreCase("DATE AND TIME")) {
				format = ProcessorConstants.DATE_TIME_FORMAT;
			} else if (displayFormat.equalsIgnoreCase("MONTH AND YEAR")) {
				format = ProcessorConstants.MONTH_YEAR_FORMAT;
			} else if (displayFormat.equalsIgnoreCase("YEAR ONLY")) {
				format = ProcessorConstants.YEAR_ONLY_FORMAT;
			}

			return format;
		}

		/**
		 * @param displayFormat
		 * @return
		 */
		private String actualFormatToDisplayFormat(String actualFormat) {
			String format = "";
			if (actualFormat.equalsIgnoreCase(ProcessorConstants.DATE_ONLY_FORMAT)) {
				format = "DATE";
			} else if (actualFormat.equalsIgnoreCase(ProcessorConstants.DATE_TIME_FORMAT)) {
				format = "DATE AND TIME";
			} else if (actualFormat.equalsIgnoreCase(ProcessorConstants.MONTH_YEAR_FORMAT)) {
				format = "MONTH AND YEAR";
			} else if (actualFormat.equalsIgnoreCase(ProcessorConstants.YEAR_ONLY_FORMAT)) {
				format = "YEAR ONLY";
			}

			return format;
		}
	}

	/**
	 * Mapper for file upload control
	 *
	 */
	private class FileUploadMapper extends DefaultControlMapper {

		@Override
		public Control propertiesToControl(Properties controlProps, Container container) {
			FileUploadControl control = new FileUploadControl();
			setCommonProperties(controlProps, control);
			return control;
		}

		@Override
		public Properties controlToProperties(Control control, Container container) {
			Properties controlProps = new Properties();
			controlProps.setProperty(CSDConstants.CONTROL_TYPE, CSDConstants.FILE_UPLOAD);
			getCommonProperties(controlProps, control);
			return controlProps;
		}
	}

	/**
	 * Mapper for Note control
	 *
	 */
	private class NoteMapper extends DefaultControlMapper {

		@Override
		public Control propertiesToControl(Properties controlProps, Container container) throws Exception {
			Label control = new Label();
			setCommonProperties(controlProps, control);
			control.setNote(true);
			return control;
		}

		@Override
		public Properties controlToProperties(Control control, Container container) {
			Properties controlProps = new Properties();
			controlProps.setProperty(CSDConstants.CONTROL_TYPE, CSDConstants.NOTE);
			getCommonProperties(controlProps, control);
			return controlProps;
		}
	}

	/**
	 * Mapper for heading control
	 *
	 */
	private class HeadingMapper extends DefaultControlMapper {

		@Override
		public Control propertiesToControl(Properties controlProps, Container container) throws Exception {
			Label control = new Label();
			setCommonProperties(controlProps, control);
			control.setHeading(true);
			return control;
		}

		@Override
		public Properties controlToProperties(Control control, Container container) {
			Properties controlProps = new Properties();
			controlProps.setProperty(CSDConstants.CONTROL_TYPE, CSDConstants.HEADING);
			getCommonProperties(controlProps, control);
			return controlProps;
		}
	}

	/**
	 * Mapper for Sub Form control
	 *
	 */
	private class SubFormMapper extends DefaultControlMapper {

		@Override
		public Control propertiesToControl(Properties controlProps, Container container) throws Exception {
			SubFormControl control = new SubFormControl();
			control.setNoOfEntries(-1);
			setCommonProperties(controlProps, control);
			//populate sub container.
			Map<String, Object> subForm = controlProps.getMap("subForm");
			subForm.put("formName", controlProps.getString(CSDConstants.CONTROL_NAME));
			subForm.put("caption", controlProps.getString(CSDConstants.CONTROL_CAPTION));
			if (subForm != null) {
				ContainerMapper containerMapper = new RegularContainerMapper();
				containerMapper.setRootContainer(container);

				Container subContainer = containerMapper.propertiesToContainer(new Properties(subForm), null);
				control.setSubContainer(subContainer);
			}

			return control;
		}

		@Override
		public Properties controlToProperties(Control control, Container container) {
			Properties controlProps = new Properties();
			controlProps.setProperty(CSDConstants.CONTROL_TYPE, CSDConstants.SUB_FORM);
			getCommonProperties(controlProps, control);
			Container subContainer = ((SubFormControl) control).getSubContainer();
			controlProps.setProperty("subFormName", subContainer.getName());
			controlProps.setProperty("subForm", new RegularContainerMapper().containerToProperties(subContainer)
					.getAllProperties());
			return controlProps;
		}
	}

	/**
	 * Mapper for Label control
	 *
	 */
	private class LabelMapper extends DefaultControlMapper {

		@Override
		public Control propertiesToControl(Properties controlProps, Container container) throws Exception {
			Label control = new Label();
			setCommonProperties(controlProps, control);
			return control;
		}

		@Override
		public Properties controlToProperties(Control control, Container container) {
			Properties controlProps = new Properties();
			controlProps.setProperty(CSDConstants.CONTROL_TYPE, CSDConstants.LABEL);
			getCommonProperties(controlProps, control);
			return controlProps;
		}
	}

	/**
	 * Mapper for PAge Break control
	 *
	 */
	private class PageBreakMapper extends DefaultControlMapper {

		@Override
		public Control propertiesToControl(Properties controlProps, Container container) throws Exception {
			PageBreak control = new PageBreak();
			setCommonProperties(controlProps, control);
			return control;
		}

		@Override
		public Properties controlToProperties(Control control, Container container) {
			Properties controlProps = new Properties();
			controlProps.setProperty(CSDConstants.CONTROL_TYPE, CSDConstants.PAGE_BREAK);
			getCommonProperties(controlProps, control);
			return controlProps;
		}
	}

	/**
	 * Mapper for list box
	 *
	 */
	private class ComboBoxMapper extends DefaultControlMapper {

		@Override
		public Control propertiesToControl(Properties controlProps, Container container) throws Exception {
			ComboBox control = new ComboBox();
			setCommonProperties(controlProps, control);
			control.setPvDataSource(PvMapper.propertiesToPvDataSource(controlProps));
			control.setLazyPvFetchingEnabled(controlProps.getBoolean("lazyLoad"));
			return control;
		}

		@Override
		public Properties controlToProperties(Control control, Container container) {
			Properties controlProps = new Properties();
			controlProps.setProperty(CSDConstants.CONTROL_TYPE, CSDConstants.COMBO_BOX);
			getCommonProperties(controlProps, control);
			controlProps.setProperty("lazyLoad", ((ComboBox) control).isLazyPvFetchingEnabled());
			PvMapper.pVDataSourcetoProperties(((ComboBox) control).getPvDataSource(), controlProps);
			return controlProps;
		}
	}
}
