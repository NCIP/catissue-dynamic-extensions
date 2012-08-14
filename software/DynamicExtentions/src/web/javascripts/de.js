var edu = edu || {};
edu.wustl = edu.wustl || {};
edu.wustl.de = edu.wustl.de || {};
edu.wustl.de.sm = {};
edu.wustl.de.smurl;
edu.wustl.de.currentpage;
edu.wustl.de.defaultValues = {};

$(document).ready(function () {
edu.wustl.de.smurl = $("#contextPath").val()+"/AjaxcodeHandlerAction?ajaxOperation=renderSurveyMode&formLabel="+$("#formLabel").val();
	var form = new edu.wustl.de.CategorySurveyMode({ctx: $("#sm-category"),
		categoryid: $("#categoryId").val()});
	form.load();
});

edu.wustl.de.CategorySurveyMode = function (args) {
	if (args.ctx == undefined) throw "ctx undefined";
	if (args.categoryid == undefined) throw "categoryid undefined";
	this.ctx = args.ctx;
	this.categoryid = args.categoryid;
	this.pages = new Array();
	edu.wustl.de.currentpage = 0;
	this.navbar = new edu.wustl.de.Navbar({ctx: $("#sm-navbar")});
	this.progressbar = new edu.wustl.de.ProgressBar({ctx: $("#sm-progressbar")});
	this.url = edu.wustl.de.smurl +
		"&categoryId=" + args.categoryid +
		"&containerIdentifier=" + $("#containerIdentifier").val();
	
	if ($("#recordIdentifier").length > 0) {
		this.url = this.url +"&recordIdentifier=" + $("#recordIdentifier").val();
	}
};
edu.wustl.de.CategorySurveyMode.prototype.bind = function () {
	var sm = this;
	$("input:radio").live("change", function () {
		var defaultValue = edu.wustl.de.defaultValues[$(this).attr("name")];
		if (defaultValue == undefined) {
			edu.wustl.de.defaultValues[$(this).attr("name")] = $(this).val();
			$("#emptyControlsCount").val($("#emptyControlsCount").val() - 1);
			sm.updateProgress();
		}
	});	
	$("input").live("change", function () {
		if ($(this).attr("defaultValue") == "" && $(this).val() != "") {
			$(this).attr("defaultValue", $(this).val());
			$("#emptyControlsCount").val($("#emptyControlsCount").val() - 1);
			sm.updateProgress();
		}else if($(this).val() == "")
		{
		$("#emptyControlsCount").val(parseInt($("#emptyControlsCount").val()) + 1);
			$(this).attr("defaultValue", "");
			sm.updateProgress();
		}		
	});
	$("select").live("click", function () {
		var defaultValue = edu.wustl.de.defaultValues[$(this).attr("name")];
		if (defaultValue == undefined) {
			edu.wustl.de.defaultValues[$(this).attr("name")] = $(this).val();
			$("#emptyControlsCount").val($("#emptyControlsCount").val() - 1);
			sm.updateProgress();
		}
	});
	$("textarea").live("change", function () {
		if ($(this).attr("defaultValue") == "" && $(this).val() != "") {
			$(this).attr("defaultValue", $(this).val());
			$("#emptyControlsCount").val($("#emptyControlsCount").val() - 1);
			sm.updateProgress();
		}else if($(this).val() == "")
		{
		$("#emptyControlsCount").val(parseInt($("#emptyControlsCount").val()) + 1);
			$(this).attr("defaultValue", "");
			sm.updateProgress();
		}		
	});
	
	this.navbar.register({type: "button",	label: "Previous",cssClass:'sm-previous-button',
		handler: function () {
			sm.hide();
			edu.wustl.de.currentpage -= 1;	
			sm.show();
		}
	});
	this.navbar.register({type: "button",	label: "Save as Draft",cssClass:'sm-draft-button',
		handler: function () {
			$("#isDraft").val("true");
			$("#sm-form").submit();
		}
	});
	this.navbar.register({type: "button",	label: "Save",cssClass:'sm-save-button',
		handler: function () {
			$("#sm-form").submit();
		}
	});
	this.navbar.register({type: "button",	label: "Next",cssClass:'sm-next-button',
		handler: function () {
			sm.hide();
			edu.wustl.de.currentpage += 1;
			sm.show();
		}
	});
};
edu.wustl.de.CategorySurveyMode.prototype.loadAllPages = function () {
	$.each(this.pages, function (index, page) {
		page.load();
	});
};
edu.wustl.de.CategorySurveyMode.prototype.tidyNavbar = function () {
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
};
edu.wustl.de.CategorySurveyMode.prototype.load = function () {
	if (this.request == undefined) {
		var sm = this;
		this.request = new edu.wustl.de.Request({
			url:this.url,
			onsuccess: function (res) {
				$("#sm-form-contents", sm.ctx).append(res);
				$("#sm-category-name").append($("#categoryName").val());
				sm.init();
				sm.bind();
				sm.show();
				sm.updateProgress();
				sm.loadAllPages();
			}
		});
		this.request.load();
	}
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

edu.wustl.de.Page = function (args) {
	if (args.ctx == undefined) throw "ctx undefined";
	if (args.categoryid == undefined) throw "categoryid undefined";
	if (args.pageid == undefined) throw "pageid undefined";
	this.ctx = args.ctx;
	
	this.url = edu.wustl.de.smurl +
		"&categoryId=" + args.categoryid +
		"&pageId=" + args.pageid +
		"&containerIdentifier=" + $("#containerIdentifier").val(); 
	if ($("#recordIdentifier").length > 0) {
		this.url = this.url +"&recordIdentifier=" + $("#recordIdentifier").val();
	}
};
edu.wustl.de.Page.prototype.load = function () {
	if (this.request == undefined) {
		var p = this;
		this.request = new edu.wustl.de.Request({
			url:this.url,
			onsuccess: function(res) {
				$(p.ctx).append(res);
				$(".formRequiredLabel_withoutBorder", p.ctx).wrap('<div class="sm_control_label"/>');
				$(".formField_withoutBorder", p.ctx).wrap('<div class="sm_control_td"/>');
				$("input+label", p.ctx).each(function() {
					$(this).prev().andSelf().wrapAll('<div>');
				});
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
			}
		});
		this.request.load();
	}
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