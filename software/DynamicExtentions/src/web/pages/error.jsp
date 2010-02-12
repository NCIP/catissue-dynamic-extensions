<%-- Jsp Summary                                                                                 	--%>
<%-- ---------------------------------------------------------------------------------------------- --%>

<%-- TagLibs --%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/dynamicExtensions.tld" prefix="dynamicExtensions" %>

<%-- Imports --%>
<%@page import="java.lang.Exception"%>
<c:set var="errorList" value="${dataEntryForm.errorList}"/>
<jsp:useBean id="errorList" type="java.util.List"/>

<html>
	<head>
		<title>Dynamic Extensions</title>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/styleSheet.css"/>
	</head>
	<body onload = "setContextParameter('<%=request.getContextPath()%>')"; class='bodyStyle'>
	<table width="100%" align="center">
		<c:forEach items="${errorList}" var="error">
			<jsp:useBean id="error" type="java.lang.String"/>
			<tr>
				<td class="formMessage" align="left">
					<font color="red"><c:out value="${error}"/></font>
				</td>
			</tr>
		</c:forEach>
	</table>
	</body>
</html>
