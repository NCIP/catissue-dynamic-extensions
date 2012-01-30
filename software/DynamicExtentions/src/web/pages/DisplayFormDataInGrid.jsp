<%@ page language="java" isELIgnored="false"%>
<!-- configure isELIgnored in web.xml  -->
<html>
<head>
<title>Dynamic Extension</title>
<script type="text/javascript" src="dhtmlx_suite/js/dhtmlxcommon.js"></script>
<script type="text/javascript" src="dhtmlx_suite/js/dhtmlxgrid.js"></script>
<script type="text/javascript" src="dhtmlx_suite/js/dhtmlxgridcell.js"></script>


<link rel="STYLESHEET" type="text/css"
	href="dhtmlx_suite/css/dhtmlxgrid.css">
<link rel="STYLESHEET" type="text/css"
	href="dhtmlx_suite/skins/dhtmlxgrid_dhx_skyblue.css">
	
<script>
		function doOnLoad()
		{
			formDataGrid = new dhtmlXGridObject('displayFormDataGrid');
			formDataGrid.setImagePath("<%=request.getContextPath()%>/dhtmlx_suite/imgs/");
			formDataGrid.setHeader("Sr.No.,Username,Last Updated,Form Url");
			formDataGrid.setInitWidthsP("10,30,*,0");
			formDataGrid.setColAlign("center,center,center,center");
			formDataGrid.setColTypes("ro,ro,ro,ro");
			formDataGrid.enableMultiselect(true);
			formDataGrid.setSkin("dhx_skyblue");
			formDataGrid.init();
			formDataGrid.loadXML("LoadDisplayFormDataInGrid.do?formContextId=${param.formContextId}&recEntryEntityId=${param.recEntryEntityId}&containerId=${param.containerId}");
			formDataGrid.attachEvent("onRowDblClicked",showFormInEdit);
		}

		function showFormInEdit(id,ind)
		{
			var url = formDataGrid.cellById(id,3).getValue();
			// the url sent present at  formDataGrid.cellById(id,3) have '&' but when it's read through javascript it convert's it to &amp;
			// which will through an error when passed to 'document.location.href' so we are using regExp for replacing all the &amp; with &
			document.location.href = url.replace(new RegExp("&amp;", 'g'),"&");
		}
			
	</script>
</head>


<body onload="doOnLoad();">
<table border="0" height="100%" width="100%">
	<tr>
		<td style="vertical-align: top;">
		<div id="displayFormDataGrid" style="height: 100%; width: 100%;"></div>
		</td>
	</tr>
</table>

</body>
</html>