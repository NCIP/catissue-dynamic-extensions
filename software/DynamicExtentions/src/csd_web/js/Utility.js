var Utility = {
	constructBaseURL : function() {
		var baseURL = "";
		var currentURL = window.location + "";
		var tempCurrentURL = currentURL.split('/');
		for (cntr = 0; cntr < tempCurrentURL.length - 1; cntr++) {
			baseURL += tempCurrentURL[cntr] + "/"
		}
		return baseURL;
	},

	resetCarouselControlSelect : function() {
		var controlTypes = [ "stringTextField", "numericField", "textArea",
				"radioButton", "checkBox", "listBox", "multiselectBox",
				"multiselectCheckBox", "datePicker", "fileUpload", "note",
				"heading", "subForm", "label", "comboBox" ];

		for ( var cntr = 0; cntr < controlTypes.length; cntr++) {
			$('#' + controlTypes[cntr]).css('background-color', '#FFFFFF ');
		}

	},

	messageSpace : "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;",

	getControlIndexForCarousel : function(controlType) {

		switch (controlType) {

		case "stringTextField":
			return 1;
			break;

		case "numericField":
			return 2;
			break;

		case "textArea":
			return 3;
			break;

		case "radioButton":
			return 4;
			break;

		case "checkBox":
			return 5;
			break;

		case "comboBox":
			return 6;
			break;

		case "multiselectBox":
			return 7;
			break;

		case "listBox":
			return 8;
			break;

		case "multiselectCheckBox":
			return 9;
			break;

		case "datePicker":
			return 10;
			break;

		case "fileUpload":
			return 11;
			break;

		case "note":
			return 12;
			break;

		case "heading":
			return 13;
			break;

		case "subForm":
			return 14;
			break;

		case "label":
			return 15;
			break;

		case "pageBreak":
			return 16;
			break;

		default:
			return 1;
		}
	},

	getShortCode : function(type) {
		var shortCode = GlobalMemory.nodeCounter;

		switch (type) {

		case "stringTextField":
			shortCode = "ST" + GlobalMemory.nodeCounter;
			break;

		case "comboBox":
			shortCode = "DD" + GlobalMemory.nodeCounter;
			break;

		case "numericField":
			shortCode = "NT" + GlobalMemory.nodeCounter;
			break;

		case "textArea":
			shortCode = "TA" + GlobalMemory.nodeCounter;
			break;

		case "radioButton":
			shortCode = "RB" + GlobalMemory.nodeCounter;
			break;

		case "checkBox":
			shortCode = "CB" + GlobalMemory.nodeCounter;
			break;

		case "listBox":
			shortCode = "LB" + GlobalMemory.nodeCounter;
			break;

		case "multiselectBox":
			shortCode = "MLB" + GlobalMemory.nodeCounter;
			break;

		case "multiselectCheckBox":
			shortCode = "MCB" + GlobalMemory.nodeCounter;
			break;

		case "datePicker":
			shortCode = "DP" + GlobalMemory.nodeCounter;
			break;

		case "fileUpload":
			shortCode = "FU" + GlobalMemory.nodeCounter;
			break;

		case "note":
			shortCode = "N" + GlobalMemory.nodeCounter;
			break;

		case "heading":
			shortCode = "H" + GlobalMemory.nodeCounter;
			break;

		case "label":
			shortCode = "L" + GlobalMemory.nodeCounter;
			break;

		case "subForm":
			shortCode = "SF" + GlobalMemory.nodeCounter;
			break;

		case "pageBreak":
			shortCode = "PB" + GlobalMemory.nodeCounter;
			break;

		default:

		}

		return shortCode;
	},

	toCamelCase : function(str) {
		str = $.camelCase(str.replace(/[_ ]/g, '-')).replace(/-/g, '');
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	},

	split : function(val) {
		return val.split(/\s\s*/);
	},

	extractLast : function(term) {
		return this.split(term).pop();
	},

	trim : function(str) {
		if (str.trim)
			return str.trim();
		else
			return str.replace(/(^\s*)|(\s*$)/g, "");
	},

	addFieldHandlerMap : null,

	addStringTextField : function(controlModel, show, container) {
		controlModel.set({
			template : Templates.templateList['stringTextFieldTemplate']
					+ Templates.templateList['commonControlPropsTemplate']
					+ Templates.templateList['submitButtonTemplate']
		});

		if (show)
			return Views.showControl(container, controlModel);
		else
			return controlModel;
	},

	addNumericField : function(controlModel, show, container) {
		controlModel.set({
			template : Templates.templateList['numericFieldTemplate']
					+ Templates.templateList['commonControlPropsTemplate']
					+ Templates.templateList['submitButtonTemplate']
		});

		if (show)
			return Views.showControl(container, controlModel);
		else
			return controlModel;
	},

	addTextArea : function(controlModel, show, container) {
		controlModel.set({
			template : Templates.templateList['textAreaTemplate']
					+ Templates.templateList['commonControlPropsTemplate']
					+ Templates.templateList['submitButtonTemplate']
		});

		if (show)
			return Views.showControl(container, controlModel);
		else
			return controlModel;
	},

	addRadioButton : function(controlModel, show, container) {
		controlModel.set({
			template : Templates.templateList['radioButtonTemplate']
					+ Templates.templateList['commonControlPropsTemplate']
					+ Templates.templateList['pvTemplate']
					+ Templates.templateList['submitButtonTemplate']
		});

		if (show)
			return Views.showControl(container, controlModel);
		else
			return controlModel;
	},

	addCheckBox : function(controlModel, show, container) {
		controlModel.set({
			template : Templates.templateList['checkBoxTemplate']
					+ Templates.templateList['commonControlPropsTemplate']
					+ Templates.templateList['submitButtonTemplate']
		});

		if (show)
			return Views.showControl(container, controlModel);
		else
			return controlModel;
	},

	addDropDown : function(controlModel, show, container) {
		controlModel.set({
			template : Templates.templateList['singleSelectDropdownTemplate']
					+ Templates.templateList['commonControlPropsTemplate']
					+ Templates.templateList['pvTemplate']
					+ Templates.templateList['submitButtonTemplate']
		});

		if (show)
			return Views.showControl(container, controlModel);
		else
			return controlModel;
	},

	addMultiselectDropDown : function(controlModel, show, container) {
		controlModel.set({
			template : Templates.templateList['multiSelectDropdownTemplate']
					+ Templates.templateList['commonControlPropsTemplate']
					+ Templates.templateList['pvTemplate']
					+ Templates.templateList['submitButtonTemplate'],
			autoComplete : true
		});

		if (show)
			return Views.showControl(container, controlModel);
		else
			return controlModel;
	},

	addMultiselect : function(controlModel, show, container) {
		controlModel.set({
			template : Templates.templateList['multiSelectDropdownTemplate']
					+ Templates.templateList['commonControlPropsTemplate']
					+ Templates.templateList['pvTemplate']
					+ Templates.templateList['submitButtonTemplate']
		});

		if (show)
			return Views.showControl(container, controlModel);
		else
			return controlModel;
	},

	addMultiselectCheckBox : function(controlModel, show, container) {
		controlModel.set({
			template : Templates.templateList['multiSelectCheckBoxTemplate']
					+ Templates.templateList['commonControlPropsTemplate']
					+ Templates.templateList['pvTemplate']
					+ Templates.templateList['submitButtonTemplate']
		});

		if (show)
			return Views.showControl(container, controlModel);
		else
			return controlModel;
	},

	addDatePicker : function(controlModel, show, container) {
		controlModel.set({
			template : Templates.templateList['datePickerTemplate']
					+ Templates.templateList['commonControlPropsTemplate']
					+ Templates.templateList['submitButtonTemplate']
		});

		if (show)
			return Views.showControl(container, controlModel);
		else
			return controlModel;
	},

	addFileUpload : function(controlModel, show, container) {
		controlModel.set({
			template : Templates.templateList['fileUploadTemplate']
					+ Templates.templateList['commonControlPropsTemplate']
					+ Templates.templateList['submitButtonTemplate']
		});

		if (show)
			return Views.showControl(container, controlModel);
		else
			return controlModel;
	},

	addNote : function(controlModel, show, container) {
		controlModel.set({
			template : Templates.templateList['noteTemplate']
					+ Templates.templateList['commonControlPropsTemplate']
					+ Templates.templateList['submitButtonTemplate']
		});

		if (show)
			return Views.showControl(container, controlModel);
		else
			return controlModel;
	},

	addHeading : function(controlModel, show, container) {
		controlModel.set({
			template : Templates.templateList['headingTemplate']
					+ Templates.templateList['commonControlPropsTemplate']
					+ Templates.templateList['submitButtonTemplate']
		});

		if (show)
			return Views.showControl(container, controlModel);
		else
			return controlModel;
	},

	addLabel : function(controlModel, show, container) {
		controlModel.set({
			template : Templates.templateList['labelTemplate']
					+ Templates.templateList['commonControlPropsTemplate']
					+ Templates.templateList['submitButtonTemplate']
		});

		if (show)
			return Views.showControl(container, controlModel);
		else
			return controlModel;
	},

	addSubForm : function(controlModel, show, container) {
		var subFrm = new Models.Form();
		controlModel.set({
			template : Templates.templateList['subFormTemplate']
					+ Templates.templateList['commonControlPropsTemplate']
					+ Templates.templateList['submitButtonTemplate'],
			subForm : subFrm
		});

		if (show)
			return Views.showControl(container, controlModel);
		else
			return controlModel;
	},

	addPageBreak : function(controlModel, show, container) {
		controlModel.set({
			template : Templates.templateList['pageBreakTemplate']
					+ Templates.templateList['submitButtonTemplate']

		});

		if (controlModel.has('controlName')) {
		} else {
			controlModel.set({
				controlName : Utility.getShortCode("pageBreak")
			});
		}

		if (show)
			return Views.showControl(container, controlModel);
		else
			return controlModel;
	},

	initializeFieldHandlerMap : function() {
		this.addFieldHandlerMap = new Array();

		this.addFieldHandlerMap['stringTextField'] = this.addStringTextField;
		this.addFieldHandlerMap['numericField'] = this.addNumericField;
		this.addFieldHandlerMap['textArea'] = this.addTextArea;
		this.addFieldHandlerMap['radioButton'] = this.addRadioButton;
		this.addFieldHandlerMap['checkBox'] = this.addCheckBox;
		this.addFieldHandlerMap['comboBox'] = this.addDropDown;
		this.addFieldHandlerMap['listBox'] = this.addMultiselect;
		this.addFieldHandlerMap['multiselectBox'] = this.addMultiselectDropDown;
		this.addFieldHandlerMap['multiselectCheckBox'] = this.addMultiselectCheckBox;
		this.addFieldHandlerMap['datePicker'] = this.addDatePicker;
		this.addFieldHandlerMap['fileUpload'] = this.addFileUpload;
		this.addFieldHandlerMap['note'] = this.addNote;
		this.addFieldHandlerMap['heading'] = this.addHeading;
		this.addFieldHandlerMap['label'] = this.addLabel;
		this.addFieldHandlerMap['subForm'] = this.addSubForm;
		this.addFieldHandlerMap['pageBreak'] = this.addPageBreak;
	},

	getAddFieldHandler : function(controlType) {
		return this.addFieldHandlerMap[controlType];
	},

	checkNameForCorrectness : function(name) {
		var isCorrect = true;
		var charactersNotAllowed = [ "[", "+", "-", "/", "*", "(", ")", "{",
				"}", "%", "]", "." ];
		for ( var cntr = 0; cntr < charactersNotAllowed.length; cntr++) {
			if (name.indexOf(charactersNotAllowed[cntr]) >= 0) {
				isCorrect = false;
				break;
			}
		}
		return isCorrect;
	}

}