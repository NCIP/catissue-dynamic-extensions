<%@ page language="java" isELIgnored="false"%>
<!-- configure isELIgnored in web.xml  -->
<html>
<head>
<title>Dynamic Extension</title>
<script type="text/javascript" src="dhtmlx_suite/js/dhtmlxcommon.js"></script>
<script type="text/javascript" src="dhtmlx_suite/js/dhtmlxgrid.js"></script>
<script type="text/javascript" src="dhtmlx_suite/js/dhtmlxgridcell.js"></script>
<script type="text/javascript" src="dhtmlx_suite/gridexcells/dhtmlxgrid_excell_grid.js"></script>
<script type="text/javascript" src="dhtmlx_suite/ext/dhtmlxgrid_mcol.js"></script>
<script type="text/javascript" src="dhtmlx_suite/ext/dhtmlxgrid_filter.js"></script>

<link rel="STYLESHEET" type="text/css"
	href="dhtmlx_suite/css/dhtmlxgrid.css">
<link rel="STYLESHEET" type="text/css"
	href="dhtmlx_suite/skins/dhtmlxgrid_dhx_skyblue.css">

<script>
var formDataGrid;

		function addColumnsToGrid()
		{
			var gridHeaders = '${requestScope.gridHeaders}';
			var arr = gridHeaders.substring(1,gridHeaders.length-1).split(",");
			var numberOfCol = formDataGrid.getColumnsNum();
			for(i=0;i<arr.length;i++)
			{
				formDataGrid.insertColumn(numberOfCol+i,arr[i],"ro",10,"na","left","top",null,null);
			}
			formDataGrid.loadXML("LoadDisplayFormDataInGrid.do");
		}

		function doOnLoad()
		{
			formDataGrid = new dhtmlXGridObject('displayFormDataGrid');
			formDataGrid.setImagePath("<%=request.getContextPath()%>/dhtmlx_suite/imgs/");
			formDataGrid.setHeader("Select,Sr.No.,Operation,Username,Last Updated");
			formDataGrid.setInitWidthsP("5,5,10,10,10");
			formDataGrid.setColAlign("center,center,center,center,center");
			formDataGrid.setColTypes("ch,ro,ro,ro,ro");
			formDataGrid.attachHeader("#master_checkbox,,,,");
			formDataGrid.enableMultiselect(true);
			formDataGrid.setSkin("dhx_skyblue");
			formDataGrid.init();
			addColumnsToGrid();
		}
		
	</script>
</head>


<body onload="doOnLoad();">
<table border="0" height="100%" width="100%">
	<tr>
		<td style="vertical-align: top;">
		<div id="displayFormDataGrid" style="height: 95%; width: 100%;"></div>
		</td>
	</tr>
	<tr>
		<td>
			<input type="button" value="Add Record" onclick="javascript:document.location.href = '${sessionScope.formUrl}';"/>
		</td>
	</tr>
</table>

</body>
</html>