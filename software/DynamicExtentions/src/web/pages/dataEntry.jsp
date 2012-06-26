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
<%@ page import="edu.common.dynamicextensions.domain.Category"%>
<%@ page import="edu.wustl.cab2b.server.util.DynamicExtensionUtility"%>

<%
	Category category;
	String destination = null;
	try
	{
		category = DynamicExtensionUtility.getCategoryByContainerId(request
				.getParameter(DEConstants.CONTAINER_IDENTIFIER));
		if (category.getLayout() != null)
		{
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
<jsp:include page="<%=destination%>"></jsp:include>