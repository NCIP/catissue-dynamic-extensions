<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page isELIgnored ="false" %> 

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
</body>

<c:forEach var="url" items="${fn:split(param.formUrl, ',')}">
    <iframe src="${url}" width="100%" scrolling="no" height="100%" frameborder="0" ></iframe>
	<div style="page-break-before: always">&nbsp;</div>
</c:forEach>

<script type="text/javascript" defer="defer">
	window.print();
</script>