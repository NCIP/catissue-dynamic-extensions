<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page isELIgnored ="false" %> 
<%@ taglib uri="/WEB-INF/dynamicExtensions.tld" prefix="dynamicExtensions" %>

<%--CSS and Java Scripts --%>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/prototype.js"></script>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/styleSheet.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/de_style.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/ext-all.css" />
<link href="<%=request.getContextPath()%>/stylesheets/de/calanderComponent.css" type=text/css rel=stylesheet />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/xtheme-gray.css" />

<script src="<%=request.getContextPath()%>/javascripts/de/calendarComponent.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/script.js" type="text/javascript"></script>

<script>var imgsrc="<%=request.getContextPath()%>/images/de/";</script>
<script src="<%=request.getContextPath()%>/javascripts/de/jquery-1.3.2.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/form_plugin.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/dynamicExtensions.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/overlib_mini.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/calender.js" type="text/javascript"></script>

<script src="<%=request.getContextPath()%>/javascripts/de/calendarComponent.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/ajax.js"></script>


<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/scriptaculous.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/scr.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/ext-base.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/ext-all.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/combos.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/ajaxupload.js"></script>
<%--CSS and Java Scripts --%>

<%-- 1:Get all form URLs --%>
<%-- 2:Create multiple iframes for loading DE forms --%>

<style type="text/css" media="print">
.NonPrintable
{
	display: none;
}
</style>

<%-- For non printable controls the display styles needs to provided seprately, 
can not be clubbed together with style class specified for the print media --%>
<body>
  <label class="NonPrintable" style='font-family: arial,helvetica,verdana,sans-serif;font-size: 1em;font-weight: bold;'>Print Preview</label>
	<br/>
		<div id="header"></div>
	<br />
  <script>
	document.getElementById("header").innerHTML = decodeURI("${param.printDetails}");
  </script>
</body>

<c:forEach var="url" items="${fn:split(param.formUrl, ':')}" varStatus="status">
    	<dynamicExtensions:dynamicUIGeneratorFromId
		containerId="${fn:split(url,',')[0]}"
		formRecordId="${fn:split(url,',')[1]}"
		mode="view"/>
	<c:if test="${not status.last}"><div style="page-break-before: always">&nbsp;</div></c:if>

</c:forEach>

<script type="text/javascript" defer="defer">
	window.print();
</script>