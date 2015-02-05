
package edu.common.dynamicextensions.nutility;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import edu.common.dynamicextensions.domain.nui.CheckBox;
import edu.common.dynamicextensions.domain.nui.ComboBox;
import edu.common.dynamicextensions.domain.nui.Control;
import edu.common.dynamicextensions.domain.nui.FileUploadControl;
import edu.common.dynamicextensions.domain.nui.MultiSelectCheckBox;
import edu.common.dynamicextensions.domain.nui.MultiSelectListBox;
import edu.common.dynamicextensions.domain.nui.NumberField;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.napi.ControlValue;
import edu.common.dynamicextensions.napi.FileControlValue;
import edu.common.dynamicextensions.napi.FormData;
import edu.common.dynamicextensions.nvalidator.DraftValidatorUtil;
import edu.common.dynamicextensions.nvalidator.ValidatorUtil;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;

/**
 * This class reads the form data from the HTTP request 
 * @author Kunal Kamble
 *
 */
public class HTTPFormDataCollector extends AbstractFormDataCollector {

	private static final String CONTENT_TYPE = "_contentType";
	private static final String HIDDEN = "_hidden";
	private final HttpServletRequest request;
	private final List<String> errorList = new ArrayList<String>();
	/**
	 * Used to form the key for retrieving total number rows for a sub form
	 */
	private static final String ROW_COUNT = "_rowCount";
	private ValidatorUtil validatorUtil;

	public HTTPFormDataCollector(HttpServletRequest request, ValidatorUtil validatorUtill) {
		this.request = request;
		this.validatorUtil = validatorUtill;
	}
	@Override
	public void collectControlValue(FormData formData, Control control, Integer rowId) {
		String controlName;
		ControlValue controlValue = null;
		
		if (rowId != null) {
			controlName = control.getControlName(rowId);
		} else {
			controlName = control.getControlName();

		}
		if (control instanceof MultiSelectListBox || control instanceof MultiSelectCheckBox) {
			controlValue = new ControlValue(control,
					request.getParameterValues(controlName));
		} else if (control instanceof FileUploadControl) {
			controlValue = getFileControlValue(formData, control, controlName);

		} else if (control instanceof ComboBox) {
			String value = getValueForComboBox(controlName);
			controlValue = new ControlValue(control, value);

		} else {
			String value = request.getParameter(controlName);

			value = DynamicExtensionsUtility.getEscapedStringValue(value);

			//@TODO this is to avoid date format value, which is displayed in empty text box date. 
			//This should be handled in jsp, so that such value does not get submitted
			if ((value != null)
					&& ((value.equalsIgnoreCase(ProcessorConstants.DATE_ONLY_FORMAT))
							|| (value.equalsIgnoreCase(ProcessorConstants.DATE_TIME_FORMAT))
							|| (value.equalsIgnoreCase(ProcessorConstants.MONTH_YEAR_FORMAT)) || (value
								.equalsIgnoreCase(ProcessorConstants.YEAR_ONLY_FORMAT)))) {
				value = "";
			}

			if (control instanceof CheckBox) {
				value = getValueForCheckbox(value);
			}
			controlValue = new ControlValue(control, value);
		}	
		
		ControlValue fieldValue = formData.getFieldValue(control.getName());

		if (!fieldValue.isHidden() && !fieldValue.isReadOnly()) {
			validatorUtil.validate(controlValue, errorList);
		} 
		formData.addFieldValue(controlValue);
	}

	private String getValueForComboBox(String controlName) {
		String value = request.getParameter("combo" + controlName);
		if (value == null) {
			value = request.getParameter(controlName);
		}
		value = DynamicExtensionsUtility.getEscapedStringValue(value);
		return value;
	}

	private String getValueForCheckbox(String value) {
		if (DynamicExtensionsUtility.isCheckBoxChecked(value)) {
			value = DynamicExtensionsUtility.getValueForCheckBox(true);
		} else {
			value = DynamicExtensionsUtility.getValueForCheckBox(false);
		}
		return value;
	}

	/**
	 * Collect the data for file type of the attribute.
	 * @param request
	 * @param formData
	 * @param control
	 * @param controlName
	 * @throws DynamicExtensionsSystemException
	 */
	private ControlValue getFileControlValue(FormData formData,
			Control control, String controlName) {
		String fileName = request.getParameter(controlName + HIDDEN);
		ControlValue controlValue = null;

		if (StringUtils.isEmpty(fileName)) {
			return new ControlValue(control, null);
		}
		String fileId = request.getParameter(controlName);

		if (StringUtils.isEmpty(fileId)) {
			return formData.getFieldValue(control.getName());
		}
		String contentType  = request.getParameter(controlName + CONTENT_TYPE);
		boolean isValidExtension = true;
		/*
		 * isValidExtension = checkValidFormat(control, fileName,
		 * errorList);
		 */
		if (isValidExtension && ((fileName != null) && !fileName.isEmpty())) {
			FileControlValue fileAttributeRecordValue = new FileControlValue();

			fileAttributeRecordValue.setFilePath(fileId);
			fileAttributeRecordValue.setFileName(fileName);
			fileAttributeRecordValue.setContentType(contentType);
			controlValue = new ControlValue(control, fileAttributeRecordValue);
		}

		return controlValue;

	}

	@Override
	public int getRowCount(String containerName) {
		String parameterString = containerName + ROW_COUNT;
		String rowCountString = request.getParameter(parameterString);
		int rowCount = 0;
		if (rowCountString != null) {
			rowCount = Integer.parseInt(rowCountString);
		}
		return rowCount;
	}

	public FormData collectFormData() throws DynamicExtensionsApplicationException {
		String updateResponseStr = request.getParameter(DEConstants.UPDATE_RESPONSE);
		boolean updateResponse = updateResponseStr != null && updateResponseStr.equals("true");

		Stack<FormData> formDataStack = (Stack<FormData>) CacheManager.getObjectFromCache(request,
				DEConstants.FORM_DATA_STACK);
		FormData formData = formDataStack.peek();
		collectFormData(formData, null, true);
		if (!getErrorList().isEmpty() && !updateResponse) {
			throw new DynamicExtensionsApplicationException("Error validating form data: " + errorList);
		}
		return formData;
	}
	@Override
	protected void evaluateFormula(Control control,FormData formData, Integer rowId){
		Stack<FormData> formDataStack = (Stack<FormData>) CacheManager.getObjectFromCache(request,
				DEConstants.FORM_DATA_STACK);
		FormData rootFormData = formDataStack.peek();
		FormulaCalculator formulaCalculator = new FormulaCalculator();
		String value = formulaCalculator.evaluateFormula(rootFormData, (NumberField) control, rowId);
		ControlValue controlValue = new ControlValue(control, value);
		validatorUtil.validate(controlValue, errorList);
		formData.addFieldValue(controlValue);
	}

	public List<String> getErrorList() {
		return errorList;
	}

}
