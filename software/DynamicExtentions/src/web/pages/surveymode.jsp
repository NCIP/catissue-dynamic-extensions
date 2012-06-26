<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page import="edu.common.dynamicextensions.util.global.DEConstants" %>
<%@ page isELIgnored ="false" %>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/styleSheet.css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/de_style.css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/ext-all.css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/calanderComponent.css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/xtheme-gray.css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/de.css"/>

<script>
	var imgsrc="<%=request.getContextPath()%>/images/de/";
</script>

<script src="<%=request.getContextPath()%>/javascripts/de/prototype.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/jquery-1.3.2.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/calendarComponent.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/script.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/form_plugin.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/dynamicExtensions.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/overlib_mini.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/calender.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/ajax.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/scriptaculous.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/scr.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/ext-base.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/ext-all.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/combos.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/ajaxupload.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/de.js"></script>

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
			<form id="sm-form" name="sm-form" method="post" action="${param.formSubmitUrl}">
				<div id="sm-hidden-inputs">
					<input type="hidden" id="mode" name="mode" value="edit"></input>
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
				</div>
			</form>
		</div>
	</div>
	
	<div id="sm-navbar" class="navbar" align="center">
	</div>
</body>


<c:if test='${empty param.formSubmitUrl}'>
	<script>
		document.getElementById('sm-form').action = "/clinportal/DEServlet";
	</script>
</c:if>	
