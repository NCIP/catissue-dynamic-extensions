<%@ page language="java" isELIgnored="false"%>
<!-- configure isELIgnored in web.xml  -->
<html>
<head>
<title>Dynamic Extension</title>
<script type="text/javascript" src="dhtmlx_suite/js/dhtmlxcommon.js"></script>
<script type="text/javascript" src="dhtmlx_suite/js/dhtmlxgrid.js"></script>
<script type="text/javascript" src="dhtmlx_suite/js/dhtmlxgridcell.js"></script>
<script type="text/javascript"
	src="dhtmlx_suite/export/dhtmlxgrid_export.js"></script>
<script type="text/javascript"
	src="dhtmlx_suite/gridexcells/dhtmlxgrid_excell_grid.js"></script>
<script type="text/javascript" src="dhtmlx_suite/ext/dhtmlxgrid_mcol.js"></script>
<script type="text/javascript"
	src="dhtmlx_suite/ext/dhtmlxgrid_filter.js"></script>
<script
	src="<%=request.getContextPath()%>/javascripts/de/dynamicExtensions.js"
	type="text/javascript"></script>

<link rel="STYLESHEET" type="text/css"
	href="dhtmlx_suite/css/dhtmlxgrid.css">
<link rel="STYLESHEET" type="text/css"
	href="dhtmlx_suite/skins/dhtmlxgrid_dhx_skyblue.css">

<script>
var formDataGrid;
var header = "#master_checkbox,,,";
		function addColumnsToGrid()
		{
			var gridHeaders = '${requestScope.gridHeaders}';
			var arr = gridHeaders.substring(1,gridHeaders.length-1).split(",");
			if(arr != "")
			{
				var numberOfCol = formDataGrid.getColumnsNum();
				for(i=0;i<arr.length;i++)
				{
					gridHeaderObject = arr[i].split("=");
					formDataGrid.insertColumn(numberOfCol+i,gridHeaderObject[0],"ro",10,getDataType(gridHeaderObject[1]),"center","top",null,null);
					header = getFilterType(gridHeaderObject[1],header);
				}
				//if it's an daily assigments grid for the health street study in that case the formUrl will be empty and filters won't be added
				if('${requestScope.formUrl}' != "")
				{
					formDataGrid.attachHeader(header.substring(0,header.length-1),["text-align:center;padding-right:10px;"]);
				}
			}
			formUrl = encodeURIComponent('${requestScope.formUrl}');
			deUrl = encodeURIComponent('${requestScope.deUrl}');
			formDataGrid.loadXML("LoadDisplayFormDataInGrid.de?formContextId=${requestScope.formContextId}&recEntryEntityId=${requestScope.recEntryEntityId}&formUrl="+formUrl+"&deUrl="+deUrl+"&hookObjectRecordId=${requestScope.hookObjectRecordId}",filter);
		}

		function filter()
		{
			if('${requestScope.formUrl}' == "")
			{
				var today = new Date(); 
				var dd = today.getDate();
				var mm = today.getMonth()+1;//January is 0!
				var yyyy = today.getFullYear(); 
				if(dd<10){dd='0'+dd} 
				if(mm<10){mm='0'+mm} 
				var today = mm+'-'+dd+'-'+yyyy; 
				formDataGrid.filterBy(6,today,true);
			}
		}
		
		function getFilterType(dataType,header)
		{
			if(dataType.match("pv"))
			{
				header = header + "#select_filter,";
			}
			else
			{
				header =  header + "#text_filter,";
			}
			return header;
		}
		
		function date_custom(a,b,order){ 
            a=a.split("-")
            b=b.split("-")
            if (a[2]==b[2]){
                if (a[1]==b[1])
                    return (a[0]>b[0]?1:-1)*(order=="asc"?1:-1);
                else
                    return (a[1]>b[1]?1:-1)*(order=="asc"?1:-1);
            } else
                 return (a[2]>b[2]?1:-1)*(order=="asc"?1:-1);
        }

		
		function getDataType(dataType)
		{
			if(dataType.match("Date"))
			{
				return "date_custom";
			}
			else if(dataType.match("Integer") || dataType.match("Long") || dataType.match("Double") || dataType.match("Short") || dataType.match("Float"))
			{
				return "int";
			}
			else
			{
				return "str";
			}

		}
		
		function doOnLoad()
		{
			if(navigator.appName.match("Microsoft Internet Explorer"))
			{
				document.getElementById("displayFormDataGrid").style.height = document.getElementById('table').clientHeight-30;
			}
			formDataGrid = new dhtmlXGridObject('displayFormDataGrid');
			formDataGrid.setImagePath("<%=request.getContextPath()%>/dhtmlx_suite/imgs/");
			formDataGrid.setHeader("SELECT,OPERATION,deUrl",null,["text-align:center;","text-align:center;","text-align:center"]);
			formDataGrid.setColumnIds("select,operation,deUrl");
			formDataGrid.setInitWidthsP("10,10,0");
			formDataGrid.setColAlign("center,center,center");
			formDataGrid.setColTypes("ch,ro,ro");
			formDataGrid.enableStableSorting(true);
			formDataGrid.setDateFormat('%m-%d-%Y');
			formDataGrid.enableMultiselect(true);
			formDataGrid.setSkin("dhx_skyblue");
			formDataGrid.init();
			addColumnsToGrid();
			if('${requestScope.formUrl}' == "")
			{
				document.getElementById("addRecord").style.display = "none";
				formDataGrid.setColumnHidden(1,true);
			}
		}

		//bug fix for 23442
		function editRecord(editUrl)
		{
			window.parent.editClickFromMultiGrid = true;
			window.location = editUrl;
		}
	</script>
</head>

<form name="formGrid" target="_blank">

<body onload="doOnLoad();">
<table border="0" id="table" height="98%" width="100%">
	<tr>
		<td><input type="button" value="Add Record" id="addRecord"
			onclick="javascript:document.location.href = '${requestScope.formUrl}';" />
		<input type="button" align="left" value="Print" onclick="printForm();" />
		</td>
	</tr>
	<tr>
		<td style="vertical-align: top;">
		<div id="displayFormDataGrid" style="height: 98%; width: 100%;"></div>
		</td>
	</tr>

</table>
<input type="hidden" id="formUrl" name="formUrl" />
</body>

</form>
</html>