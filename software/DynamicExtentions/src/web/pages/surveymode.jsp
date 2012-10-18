<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page import="edu.common.dynamicextensions.util.global.DEConstants" %>
<%@ page isELIgnored ="false" %>

<%@ taglib uri="/WEB-INF/dynamicExtensions.tld" prefix="dynamicExtensions" %>

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/styleSheet.css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/de_style.css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/ext-all.css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/calanderComponent.css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/xtheme-gray.css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/de.css"/>

<script	src="${pageContext.request.contextPath}/dhtmlx_suite/js/dhtmlxcommon.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/dhtmlx_suite/skins/dhtmlxcalendar_dhx_skyblue.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/dhtmlx_suite/css/dhtmlxcalendar.css" />
<script src="${pageContext.request.contextPath}/dhtmlx_suite/js/dhtmlxcalendar.js"></script>

<!-- script src="<%=request.getContextPath()%>/javascripts/de/all_de.js"></script -->

<script>
	var imgsrc="<%=request.getContextPath()%>/images/de/";
</script>

<style>
.de_pagebreak {
	display: block;
	page-break-after: always;
}
</style>

<script src="<%=request.getContextPath()%>/javascripts/de/prototype.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/jquery-1.3.2.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/calendarComponent.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/script.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/form_plugin.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/dynamicExtensions.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/overlib_mini.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/calender.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/ajax.js"></script>
<!-- script src="<%=request.getContextPath()%>/javascripts/de/scriptaculous.js"></script -->
<script src="<%=request.getContextPath()%>/javascripts/de/scr.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/ext-base.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/ext-all.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/combos.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/ajaxupload.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/de.js"></script>
<table border="0" align="center" cellpadding="1" cellspacing="0" class="td_color_6e81a6">
	<tr>
		<td>
			<table width="100%" height="30"  border="0" cellpadding="4" cellspacing="4" class="td_color_FFFFCC">
				<c:forEach items="${errorList}" var="error">
					 <tr>
						<th align="center" class="font_bl_nor"><img src="<%=request.getContextPath()%>/images/de/ic_error.gif" alt="Error" width="28" height="25" hspace="3" align="absmiddle">
							<c:out value="${error}"/>
						</th>
					</tr>
				</c:forEach>
			</table>
		</td>
	</tr>
</table>
<body>
	<div>
		<table width="100%">
			<tr>
				<td width="75%">
					<div id="sm-category-name"></div>
				</td>
				<td width="25%" align="right">
					<div id="sm-progressbar"></div>
				</td>
			</tr>
		</table>
	</div>

	<div class="content">
		<div id="sm-category">
			<form id="sm-form" name="sm-form" method="post" action="${sessionScope.formSubmitUrl}">
				<div id="sm-hidden-inputs">
					<input type="hidden" id="mode" name="mode" value="edit"></input>
					<input type="hidden" id="isDirty" name="isDirty" value="false">
					<input type="hidden" name='<%=DEConstants.CATEGORY_ID%>' id='<%=DEConstants.CATEGORY_ID%>'
						value='<%=request.getParameter(DEConstants.CATEGORY_ID)%>'></input>
					<input type="hidden" name='<%=DEConstants.CALLBACK_URL%>' id='<%=DEConstants.CALLBACK_URL%>'
						value='<%=request.getParameter(DEConstants.CALLBACK_URL)%>'></input>
					<%
						String recordIdentifier = request.getParameter(DEConstants.RECORD_IDENTIFIER);
						String input = "<input type=\"hidden\" name=\"%s\" id=\"%s\" value=\"%s\"></input>";
						if (recordIdentifier != null) {
							out.println(String.format(input, DEConstants.RECORD_IDENTIFIER,
								DEConstants.RECORD_IDENTIFIER, recordIdentifier));
						}
					%>
				</div>
				<div class="box" id="sm-form-contents">
					<dynamicExtensions:surveyFormPages/>
				</div>
				<input type="hidden" value="${pageContext.request.contextPath}" id="contextPath"/>
				<c:if test='${not empty param.pageId}'>
					<input type="hidden" value="${param.pageId}" id="pageId"/>				
				</c:if>	
				<input type="hidden" id="isDraft" name="isDraft" value="false"/>
				<input type="hidden" id="dataEntryOperation" name="dataEntryOperation" value="insertChildData"/>
				<input type="hidden" id="containerIdentifier" name="containerIdentifier" value="${param.containerIdentifier}"/>
				<input type="hidden" id="formLabel" name="formLabel" value="${param.formLabel}"/>
				<input type="hidden" id="updateResponse" name="updateResponse" value="${param.updateResponse}"/>
			</form>
			<iframe style="display:none" src="about:blank" id="skipLogicIframe" name="skipLogicIframe" onload="">
				<dynamicExtensions:serverStateHtml/>
			</iframe>
			<script type="text/javascript" defer="defer">calculateDefaultAttributesValue();</script>
		</div>
	</div>
	
	<div id="sm-navbar" class="navbar" align="center">
	</div>
</body>

<c:if test='${empty sessionScope.formSubmitUrl}'>
	<script>
		document.getElementById('sm-form').action = "/clinportal/DEServlet";
	</script>
</c:if>	

<script type="text/javascript">
	edu.wustl.de.initSurveyForm();
</script>

