<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page isELIgnored ="false" %> 

<%-- 1:Get all form URLs --%>
<%-- 2:Create multiple iframes for loading DE forms --%>

<c:forEach var="url" items="${fn:split(param.formUrl, ',')}">
    <iframe src="${url}" width="100%" scrolling="no" height="100%" frameborder="0" ></iframe>
	<div style="page-break-before: always">&nbsp;</div>
</c:forEach>

<script type="text/javascript" defer="defer">
	window.print();
</script>