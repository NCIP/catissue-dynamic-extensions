<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="/WEB-INF/dynamicExtensions.tld"
	prefix="dynamicExtensions"%>
<%@ page import="edu.common.dynamicextensions.util.global.DEConstants" %>
<%--CSS and Java Scripts --%>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/de_style.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/styleSheet.css" />
<%--CSS and Java Scripts --%>

<dynamicExtensions:formSummaryGenerator
	containerIdentifier="${param.containerIdentifier}"
	recordIdentifier="${param.recordIdentifier}"/>
