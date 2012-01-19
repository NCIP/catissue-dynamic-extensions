<%@ page language="java" isELIgnored="false"%> <!-- configure isELIgnored in web.xml  -->
<html>
<head>
<title>Dynamic Extension</title>
<script src="<%=request.getContextPath()%>/dhtml/de/jss/dhtmlXCommon.js"></script>
<script src="<%=request.getContextPath()%>/dhtml/de/jss/dhtmlXGrid.js"></script>
<script src="<%=request.getContextPath()%>/dhtml/de/jss/dhtmlXGridCell.js"></script>
<link rel="STYLESHEET" type="text/css"
	href="<%=request.getContextPath()%>/dhtml/de/css/dhtmlXGrid.css">
<script>
		function doOnLoad()
		{
			formDataGrid = new dhtmlXGridObject('displayFormDataGrid');
			formDataGrid.setImagePath("<%=request.getContextPath()%>/dhtml/de/imgs/");
			formDataGrid.setHeader("Sr.No.,Username,Last Updated");
			formDataGrid.setInitWidthsP("10,30,*");
			formDataGrid.setColAlign("center,center,center");
			formDataGrid.setColTypes("ro,ro,ro");
			formDataGrid.enableMultiselect(true);
			formDataGrid.init();
			formDataGrid.loadXML("LoadDisplayFormDataInGrid.do?formContextId=${param.formContextId}");
		}
	</script>
</head>


<body onload="doOnLoad();">
<table border="1" height="100%" width="100%">
	<tr>
		<td style="vertical-align:top;">
		<div id="displayFormDataGrid" style="height: 245px; width: 100%;"></div>
		</td>
	</tr>
</table>

</body>
</html>