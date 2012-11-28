var edu = edu || {};
edu.wustl = edu.wustl || {};
edu.wustl.de = edu.wustl.de || {};
edu.wustl.de.sm = {};
edu.wustl.de.currentpage;
edu.wustl.de.defaultValues = {};

edu.wustl.de.initSurveyForm = function() {
	edu.wustl.de.surveyForm = new edu.wustl.de.CategorySurveyMode({ctx: $("#sm-category"),
		categoryid: $("#categoryId").val()});
	edu.wustl.de.surveyForm.load();
}

edu.wustl.de.CategorySurveyMode = function (args) {
	if (args.ctx == undefined) throw "ctx undefined";
	if (args.categoryid == undefined) throw "categoryid undefined";
	this.ctx = args.ctx;
	this.categoryid = args.categoryid;
	this.pages = new Array();
	edu.wustl.de.currentpage = 0;
	this.navbar = new edu.wustl.de.Navbar({ctx: $("#sm-navbar")});
	this.progressbar = new edu.wustl.de.ProgressBar({ctx: $("#sm-progressbar")});
};

edu.wustl.de.CategorySurveyMode.prototype.bind = function () {
	var sm = this;
	$("input:radio").live("click", function () {
		var defaultValue = edu.wustl.de.defaultValues[$(this).attr("name")];
		if (defaultValue == undefined) {
			edu.wustl.de.defaultValues[$(this).attr("name")] = $(this).val();
			$("#emptyControlsCount").val($("#emptyControlsCount").val() - 1);
			sm.updateProgress();
		}
	});	
	$("input:checkbox").live("click", function () {
		var defaultValue = edu.wustl.de.defaultValues[$(this).attr("name")];
		var controlName = $(this).attr("name");
		if ($('input[name='+controlName+']:checked').val() == undefined){
		$("#emptyControlsCount").val(parseInt($("#emptyControlsCount").val()) + 1);
			$(this).attr("defaultValue", "");
			edu.wustl.de.defaultValues[$(this).attr("name")] = "";
			sm.updateProgress();
		}else if( defaultValue == undefined ||  defaultValue == "")
		{
			edu.wustl.de.defaultValues[$(this).attr("name")] = $(this).val();
			$("#emptyControlsCount").val($("#emptyControlsCount").val() - 1);
			sm.updateProgress();
		}
	});	
	$("input:text").live("keyup", function () {
		if ($(this).attr("defaultValue") == "" && $(this).val() != "") {
			$(this).attr("defaultValue", $(this).val());
			edu.wustl.de.defaultValues[$(this).attr("name")] = $(this).val();
			$("#emptyControlsCount").val($("#emptyControlsCount").val() - 1);
			sm.updateProgress();
		}else if($(this).val() == "")
		{
		$("#emptyControlsCount").val(parseInt($("#emptyControlsCount").val()) + 1);
			$(this).attr("defaultValue", "");
			edu.wustl.de.defaultValues[$(this).attr("name")] = "";
			sm.updateProgress();
		}		
	});
	$("select").live("click", function () {
		var defaultValue = edu.wustl.de.defaultValues[$(this).attr("name")];
		if (defaultValue == undefined || defaultValue == "") {
			if($(this).val() != null)
			{
				edu.wustl.de.defaultValues[$(this).attr("name")] = $(this).val();
				$("#emptyControlsCount").val($("#emptyControlsCount").val() - 1);
				sm.updateProgress();
			}
		}else if($(this).val() == null)
		{
		$("#emptyControlsCount").val(parseInt($("#emptyControlsCount").val()) + 1);
			$(this).attr("defaultValue", "");
			edu.wustl.de.defaultValues[$(this).attr("name")] = "";
			sm.updateProgress();
		}		
	});
	$("textarea").live("keyup", function () {
		if ($(this).attr("defaultValue") == "" && $(this).val() != "") {
			$(this).attr("defaultValue", $(this).val());
			edu.wustl.de.defaultValues[$(this).attr("name")] = $(this).val();
			$("#emptyControlsCount").val($("#emptyControlsCount").val() - 1);
			sm.updateProgress();
		}else if($(this).val() == "")
		{
		$("#emptyControlsCount").val(parseInt($("#emptyControlsCount").val()) + 1);
			$(this).attr("defaultValue", "");
			edu.wustl.de.defaultValues[$(this).attr("name")] = "";
			sm.updateProgress();
		}		
	});
	
	this.navbar.register({type: "button",	label: "Previous",cssClass:'button-secondary',
		handler: function () {
			if(!sm.isErrorsExist({skipMandatory:"true"}))
			{
				var prevPage = edu.wustl.de.currentpage;
				while(true)
				{
					sm.hide();
					edu.wustl.de.currentpage -= 1;
					sm.show();
					
					 if($(this.ctx).find(".control_caption:visible").length > 0|| (edu.wustl.de.currentpage <= 0))
					{
						if(edu.wustl.de.currentpage <= 0 && $(this.ctx).find(".control_caption:visible").length < 0)
						{
							sm.hide();
							edu.wustl.de.currentpage = prevPage ;
							sm.show();
						}
						break;
						
					}
				}
				
			}
		}
	});
	this.navbar.register({type: "button",	label: "Save as Draft",cssClass:'button-secondary',
		handler: function () {
			if(!sm.isErrorsExist({skipMandatory:"true"}))
			{
				$("#isDraft").val("true");
				$(this).attr("disabled","disabled");
				$("#sm-form").submit();
			}
		}
	});
	this.navbar.register({type: "button",	label: "Save",cssClass:'button-primary',
		handler: function () {
			if(!sm.isErrorsExist({skipMandatory:"false"}))
			{
				$(this).attr("disabled","disabled");
				$("#sm-form").submit();
			}
		}
	});
	this.navbar.register({type: "button",	label: "Next",cssClass:'button-primary',
		handler: function () {
			if(!sm.isErrorsExist({skipMandatory:"false"}))
			{
				
				var nextPage = edu.wustl.de.currentpage;
				while(true)
				{
					sm.hide();
					edu.wustl.de.currentpage += 1;
					sm.show();
					
					if(($(this.ctx).find(".control_caption:visible").length > 0 && sm.isEmptyFieldsExist({curPage:this.ctx}))|| (edu.wustl.de.currentpage >= sm.pages.length -1))
					{
						if(edu.wustl.de.currentpage >= sm.pages.length -1 && $(this.ctx).find(".control_caption:visible").length < 0)
						{
							sm.hide();
							edu.wustl.de.currentpage = nextPage ;
							sm.show();
						}
						break;
						
					}
				}
			}
		}
	});
};
edu.wustl.de.CategorySurveyMode.prototype.loadAllPages = function () {
	$.each(this.pages, function (index, page) {
		page.load();
	});
};
edu.wustl.de.CategorySurveyMode.prototype.tidyNavbar = function () {
	
	if($("#updateResponse").val() == "true")
	{
		this.navbar.show({label:"Save"});
		this.navbar.hide({label: "Previous"});
		this.navbar.hide({label:"Next"});
		this.navbar.hide({label:"Save as Draft"});
	}else
	{
		if (edu.wustl.de.currentpage == 0) {
			this.navbar.hide({label: "Previous"});
			
		} else {
			this.navbar.show({label: "Previous"});
			
		}
		if (edu.wustl.de.currentpage == this.pages.length -1) {
			this.navbar.hide({label: "Next"});
			this.navbar.show({label:"Save"});
		} else {
			this.navbar.hide({label:"Save"});
			this.navbar.show({label: "Next"});
		}
	}
	if($("#ActivityStatus").val() == "Active")
	{
		this.navbar.hide({label:"Save as Draft"});
	}
	if($("#mode").val() == "view")
	{
		this.navbar.disable({label:"Save as Draft"});
		this.navbar.disable({label:"Save"});
	}
	
};
edu.wustl.de.CategorySurveyMode.prototype.load = function () {
	var sm = this;
	$("#sm-category-name").append($("#categoryName").val());
	this.init();
	this.bind();
	this.updateProgress();
	if($("#updateResponse").val() != "true")
	{
		edu.wustl.de.currentpage = parseInt($("#displayPage").val());
		if(edu.wustl.de.currentpage == -1)
		{
			edu.wustl.de.currentpage = 0;
		}
	}
	this.show();
	this.loadAllPages();
};

edu.wustl.de.CategorySurveyMode.prototype.init = function () {
	var createpages = function (categoryid, pages) {
		return function (i, e) {
			pages.push(new edu.wustl.de.Page({ctx: e, categoryid: categoryid,
				pageid: $(e).attr("id")}));			
			if($("#pageId").length > 0 && $("#pageId").val()==$(e).attr("id"))
			{	
				edu.wustl.de.currentpage = i;
			}
		};
	};
	$(".sm-page", this.ctx).each(createpages(this.categoryid, this.pages));
};
edu.wustl.de.CategorySurveyMode.prototype.hide = function () {
	this.pages[edu.wustl.de.currentpage].hide();
};
edu.wustl.de.CategorySurveyMode.prototype.show = function () {
	this.pages[edu.wustl.de.currentpage].show();
	if (edu.wustl.de.currentpage < this.pages.length -1) {
		this.pages[edu.wustl.de.currentpage + 1].load();
	}
	this.tidyNavbar();
};
edu.wustl.de.CategorySurveyMode.prototype.updateProgress = function () {
	var controlsCount = $("#controlsCount").val();
	var emptyControlsCount = $("#emptyControlsCount").val();
	var percentageComplete = Math.round(100*(controlsCount - emptyControlsCount)/controlsCount);
	this.progressbar.set({percentage: percentageComplete});
};
/**
 Used for verifying whether there are any validation errors exists on the form.
 If validation failed, displays the message.
**/
edu.wustl.de.CategorySurveyMode.prototype.isErrorsExist = function (args) {
	var errors = false;
	/** Check for fields marked by the live validations **/
	if($('.font_bl_nor_error').length > 0)
	{
		errors = true;
		alert("Cannot perform action, errors on the form.");
	}	
	/** Caller may choose to skip the mandatory field validation, like saving a form in draft mode **/
	if (args.skipMandatory != "true")
	{
		/** Mandatory fields are marked using 'required-field-marker' class **/
		$(".sm-page", this.ctx).each(function () {
			if($(this).is(":visible")) {		
				$(this).find(".required-field-marker").each(function() {
					var defaultValue = edu.wustl.de.defaultValues[$(this).attr("name")];
					var controlName = $(this).attr("name");
	
					if ($(this).is(":visible") && (defaultValue == "" || defaultValue == undefined))
					{
						if($(this).attr('type') == 'text' || $(this).attr('type') == 'select'
						|| $(this).attr('type') == 'select-multiple')
						{
							if ($(this).val()== "" || $(this).val() == undefined){
								errors = true;
								alert("Please enter values for the mandatory field(s).");
								return false; // break out of the each-loop
							}
						}
						if($(this).attr('type') == 'radio' || $(this).attr('type') == 'checkbox')
						{
	
							if ($('input[name='+controlName+']:checked').val() == undefined){
								errors = true;
								alert("Please enter values for the mandatory field(s).");
								return false; // break out of the each-loop
							}
						}
					}
				});
			}
		});
	}
	return errors;
};


edu.wustl.de.Page = function (args) {
	if (args.ctx == undefined) throw "ctx undefined";
	if (args.categoryid == undefined) throw "categoryid undefined";
	if (args.pageid == undefined) throw "pageid undefined";
	this.ctx = args.ctx;
	this.prettified = false;
	
};

edu.wustl.de.Page.prototype.load = function () {
	var p = this;
	p.pretty();
	$("input:radio", p.ctx).each(function() {
		if($(this).attr("checked") == true) {
			edu.wustl.de.defaultValues[$(this).attr("name")] = $(this).val();
		}
	});
	
	$("select", p.ctx).each(function() {
		if($(this).val() != null) {
			edu.wustl.de.defaultValues[$(this).attr("name")] = $(this).val();
		}
	});
};

edu.wustl.de.Page.prototype.pretty = function () {
	if (this.prettified == true) {
		return;
	}
	$(".formRequiredLabel_withoutBorder", this.ctx).each(function () {
		$(this).addClass("de_pagebreak");
	});
	$(".formField_withoutBorder", this.ctx).each(function () {
		$(this).addClass("de_pagebreak");
	});
	$("input+label", this.ctx).each(function() {
		$(this).addClass("de_pagebreak");
	});
	this.prettified = true;
};

edu.wustl.de.Page.prototype.show = function () {
	this.load();
	$(this.ctx).show();
};
edu.wustl.de.Page.prototype.hide = function () {
	$(this.ctx).hide();
};

edu.wustl.de.Navbar = function (args) {
	if (args.ctx == undefined) throw "ctx undefined";
	this.actions = {};
	this.ctx = args.ctx;
};
edu.wustl.de.Navbar.prototype.register = function (args) {
	if (args.type == undefined) throw "type undefined";
	if (args.label == undefined) throw "label undefined";
	if (args.handler == undefined) throw "handler undefined";
	
	var action = undefined;
	switch (args.type) {
		default:
			var action = $("<input type='button'></input>");
			$(action).attr("class", "navaction");
			$(action).addClass(args.cssClass);
			$(action).attr("id", args.label);
			$(action).attr("value", args.label);
			$(action).click(args.handler);
			$(this.ctx).append(action);
			this.actions[args.label] = action;
	}
};
edu.wustl.de.Navbar.prototype.get = function (args) {
	if (args.label == undefined) throw "label undefined";
	return this.actions[args.label];
};
edu.wustl.de.Navbar.prototype.disable = function (args) {
	if (args.label == undefined) throw "label undefined";
	this.actions[args.label].attr("disabled", "disabled");
};
edu.wustl.de.Navbar.prototype.hide = function (args) {
	if (args.label == undefined) throw "label undefined";
	this.actions[args.label].hide();
};
edu.wustl.de.Navbar.prototype.show = function (args) {
	if (args.label == undefined) throw "label undefined";
	this.actions[args.label].show();
};
edu.wustl.de.Navbar.prototype.enable = function (args) {
	if (args.label == undefined) throw "label undefined";
	this.actions[args.label].removeAttr("disabled");
};

edu.wustl.de.ProgressBar = function (args) {
	if (args.ctx == undefined) throw "ctx undefined";
	this.ctx = args.ctx;
	this.extpb = new Ext.ProgressBar({text: "0% complete"});
	this.extpb.render($(this.ctx)[0].id);
	this.currentProgress = 0;
};
edu.wustl.de.ProgressBar.prototype.set = function (args) {
	if (args.percentage == undefined) throw "float value undefined";
	this.currentProgress = args.percentage;
	this.extpb.updateProgress(this.currentProgress/100);
	this.extpb.updateText(this.currentProgress + "% complete");
};
edu.wustl.de.ProgressBar.prototype.increment = function (args) {
	if (args.percentage == undefined) throw "percentage undefined";
	this.set({percentage: this.currentProgress + args.percentage});
};

edu.wustl.de.Request = function (args) {
	if (args.url == undefined) throw "url undefined";
	if (args.onsuccess == undefined) throw "onsuccess undefined";
	var response = undefined;
	this.load = function () {
		if (response == undefined) {
			$.ajax({
				url: args.url,
				success: function (res) {
					response = res;
					args.onsuccess(res);
				},
				error: function (err) {
					console.log(err);
				}
			});
		} else {
			args.onsuccess(response);
		}	
	};
};

edu.wustl.de.CategorySurveyMode.prototype.isEmptyFieldsExist = function (args) {
	var emptyFields = false;
	$(args.curPage).find('[name^="Control_"]:visible').each(function() {
	var controlName = $(this).attr("name");
	if($(this).attr('type') == 'text' || $(this).attr('type') == 'select' || $(this).attr('type') == 'select-multiple')
	{
		if ($(this).val()== "" || $(this).val() == undefined){
			emptyFields = true;
			return; // break out of the each-loop
		}
	}
	if($(this).attr('type') == 'radio' || $(this).attr('type') == 'checkbox')
	{

		if ($('input[name='+controlName+']:checked').val() == undefined){
			emptyFields = true;	
			return; // break out of the each-loop
		}
	}
   });
  
   return emptyFields;
}