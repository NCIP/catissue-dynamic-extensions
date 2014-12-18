var Models = {

	Form : Backbone.Model
			.extend({
				defaults : {
					status : "new",
					save : "yes",
					id : null,
					skipRules : {}
				},

				url : function() {
					return "csdApi/form";
				},

				initialize : function() {
					console.log("Form Model created");
					this.set({
						// controlCollection : new Array(),
						controlObjectCollection : {},
						controlsOrder : new Array()
					});
				},

				getControl : function(name) {
					var controlName = name.split(".");
					if (controlName.length == 1) {
						return this.get('controlObjectCollection')[name];
					} else {
						return this.get('controlObjectCollection')[controlName[0]]
								.get('subForm').get('controlObjectCollection')[controlName[1]];
					}
				},

				addControl : function(controlName, control) {
					control.set({
						editName : controlName
					});
					var cntrlNames = controlName.split(".");

					if (cntrlNames.length == 1) {
						this.get('controlObjectCollection')[controlName] = control;
						if (this.get('controlsOrder').indexOf(controlName) < 0) {
							this.get('controlsOrder').push(controlName);
						}
					} else {
						this.get('controlObjectCollection')[cntrlNames[0]].get(
								'subForm').get('controlObjectCollection')[cntrlNames[1]] = control;
						if (this.get('controlObjectCollection')[cntrlNames[0]]
								.get('subForm').get('controlsOrder').indexOf(
										cntrlNames[1]) < 0) {
							this.get('controlObjectCollection')[cntrlNames[0]]
									.get('subForm').get('controlsOrder').push(
											cntrlNames[1]);
						}

					}
				},

				setControlId : function(controlName, controlId) {
					var cntrlNames = controlName.split(".");
					if (cntrlNames.length == 1) {
						this.get('controlObjectCollection')[controlName].set({
							id : controlId
						});
					} else {
						this.get('controlObjectCollection')[cntrlNames[0]].get(
								'subForm').get('controlObjectCollection')[cntrlNames[1]]
								.set({
									id : controlId
								});
					}
				},

				setSubFormId : function(controlName, controlId) {
					this.get('controlObjectCollection')[controlName].get(
							'subForm').set({
						id : controlId
					});
				},

				editControl : function(controlName, control) {
					this.deleteControl(controlName);
					this.addControl(controlName, control);
				},

				deleteControl : function(controlName) {
					var cntrlName = controlName.split(".");
					if (cntrlName.length == 1) {

						delete this.get('controlObjectCollection')[cntrlName[0]];

					} else {

						delete this.get('controlObjectCollection')[cntrlName[0]]
								.get('subForm').get('controlObjectCollection')[cntrlName[1]];

					}

				},

				markControlAsDeleted : function(controlName) {
					var cntrlName = controlName.split(".");
					var control = this.getControl(controlName);
					if (cntrlName.length == 1) {
						if (control != undefined) {
							delete this.get('controlObjectCollection')[cntrlName[0]];
						}
					} else {
						if (control != undefined) {
							delete this.get('controlObjectCollection')[cntrlName[0]]
									.get('subForm').get(
											'controlObjectCollection')[cntrlName[1]];
						}
					}

				},

				setFormInformation : function(formInfo) {
					this.set({
						formName : formInfo.get('formName'),
						caption : formInfo.get('caption')
					});
				},

				getFormInformation : function() {
					var formInfo = new Models.FormInfo();
					var fName = this.get('formName');
					var fCaption = this.get('caption');
					var fCreatedBy = this.get('createdBy');
					var fLastModifiedBy = this.get('lastModifiedBy');
					var fCreatedOn = this.get('createdOn');
					var fLastModifiedOn = this.get('lastModifiedOn');
					formInfo.set({
						formName : fName,
						caption : fCaption,
						createdBy : fCreatedBy,
						lastModifiedBy : fLastModifiedBy,
						createdOn : fCreatedOn,
						lastModifiedOn : fLastModifiedOn
					});
					return formInfo;
				}

			}),

	// Form Information

	FormInfo : Backbone.Model.extend({

		defaults : {
			formName : "",
			caption : ""
		},

		initialize : function() {
			console.log("Form Info Model created");
		}

	}),

	// Field
	Field : Backbone.Model
			.extend({

				defaults : {
					status : "new",
					xPos : 1,
					sequenceNumber : 0,
					id : null,
					width : 25,
					noOfRows : 4,
					noOfDigits : 6,// based on migration code
					noOfDigitsAfterDecimal : 2, // based on migration code
					pvs : {},
					lazyLoad : true,
					isCalculatedSourceControl : false
				},

				addPermissibleValue : function(id, pv) {
					this.get('pvs')[id] = pv;
				},

				deletePermissibleValue : function(id) {
					delete this.get('pvs')[id];
				},

				editPermissibleValue : function(id, pv) {
					delete this.get('pvs')[id];
					this.get('pvs')[id] = pv;
				},

				url : function() {
					return "csdApi/form/control";
				},

				initialize : function() {
					console.log("FieldControl Model created");
				},

				addCalculationFormula : function(cFormula) {
					if (this.get('type') == "numericField") {
						this.set({
							isCalculated : true,
							formula : cFormula
						});
					}
				},

				validate : function(attrs) {
					var errors = [];
					if (!attrs.userDefinedName) {
						errors.push({
							name : 'userDefinedName',
							message : 'Attribute name is required.'
						});
					}

					if (!attrs.maximumValue) {
					} else if (attrs.maximumValue != ""
							&& isNaN(attrs.maximumValue)) {
						errors.push({
							name : 'maximumValue',
							message : 'Maximum value should be numeric.'
						});
					}

					if (attrs.maximumValue && attrs.minimumValue) {
						if (parseInt(attrs.maximumValue) < parseInt(attrs.minimumValue)) {
							errors
									.push({
										name : 'maximumValue',
										message : 'Maximum value should be greater than Minimum value.'
									});
						}
					}

					if (!attrs.minimumValue) {
					} else if (attrs.minimumValue != ""
							&& isNaN(attrs.minimumValue)) {
						errors.push({
							name : 'minimumValue',
							message : 'Minimum value should be numeric.'
						});
					}
					if (!attrs.width) {
					} else if (attrs.width != "" && isNaN(attrs.width)) {
						errors.push({
							name : 'width',
							message : 'Width should be numeric.'
						});
					}

					if (!attrs.optionsPerRow) {
					} else if (attrs.optionsPerRow != ""
							&& isNaN(attrs.optionsPerRow)) {
						errors
								.push({
									name : 'optionsPerRow',
									message : 'Number of Values Per Row should be numeric.'
								});
					}

					if (!attrs.noOfRows) {
					} else if (attrs.noOfRows != "" && isNaN(attrs.noOfRows)) {
						errors.push({
							name : 'noOfRows',
							message : 'Number of rows should be numeric.'
						});
					}

					switch (attrs.type) {
					case "radioButton":

					case "listBox":

					case "multiselectBox":

					case "comboBox":

					case "multiselectCheckBox":

						if (Object.keys(attrs.pvs).length == 0 && !attrs.pvFile) {
							errors.push({
								name : 'pvs',
								message : 'Permissible values are required.'
							});
						}
						break;
					case "numericField":
						if (!attrs.noOfDigits || isNaN(attrs.noOfDigits)) {
							errors.push({
								name : 'noOfDigits',
								message : 'Number of Digits should be numeric.'
							});
						}

						if (!attrs.noOfDigitsAfterDecimal
								|| isNaN(attrs.noOfDigitsAfterDecimal)) {
							errors
									.push({
										name : 'noOfDigitsAfterDecimal',
										message : 'Number of Digits After Decimal should be numeric.'
									});
						}

						if (!attrs.defaultValue) {
						} else if (attrs.defaultValue != ""
								&& isNaN(attrs.defaultValue)) {
							errors.push({
								name : 'defaultValue',
								message : 'Default Value should be numeric.'
							});
						}

						break;

					default:
					}
					return errors;
				}
			})

}