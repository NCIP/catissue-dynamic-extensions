<%-- Jsp Summary                                                                                 	--%>
<%-- ---------------------------------------------------------------------------------------------- --%>
<%-- @author : sujay narkar, chetan patil --%>


<%-- TagLibs --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page isELIgnored="false"%>


<%-- Imports --%>
<%@ page language="java" contentType="text/html;charset=iso-8859-1"%>
<%@ page import="edu.common.dynamicextensions.util.global.DEConstants"%>
<%@ page import="edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants" %>
<%@ page import="edu.common.dynamicextensions.util.global.Variables"  %>

<%@ page import="edu.common.dynamicextensions.domain.Category"%>
<%@ page import="edu.wustl.cab2b.server.util.DynamicExtensionUtility"%>
<%@ page import="edu.common.dynamicextensions.ui.webui.util.FormCache"%>


<%
	Category category;
	String destination = null;
	try
	{
		category = DynamicExtensionUtility.getCategoryByContainerId(request
				.getParameter(WebUIManagerConstants.CONTAINER_IDENTIFIER));
		if (category.getLayout() != null)
		{
			FormCache formCache = new FormCache(request);
			formCache.onFormLoad();
			request.getSession().setAttribute("formCache", formCache);
			request.getSession().setAttribute(DEConstants.CATEGORY, category);
			request.getSession().setAttribute(DEConstants.CONTAINER, null);
			String catgeoryId = String.valueOf(category.getId().longValue());
			destination = "/pages/de/surveymode.jsp?categoryId=" + catgeoryId;
		}else
		{
			destination = "/pages/de/dataEntry/dataEntry.jsp"; 
		}
	}
	catch (NumberFormatException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
%>


<script>
var UpdateServerStateGenerateHtml =
	"${pageContext.request.contextPath}/<%=Variables.resourceMapping.get("DE_AJAX_HANDLER")%>?ajaxOperation=updateServerStateGenerateHtml";
</script>

<jsp:include page="<%=destination%>"/>
