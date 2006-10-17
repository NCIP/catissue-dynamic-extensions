<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/dynamicExtensions.tld" prefix="dynamicExtensions" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>

<link rel="stylesheet" type="text/css" href="css/stylesheet.css" />
<script src="jss/dynamicExtensions.js" type="text/javascript"></script>
<script src="jss/overlib_mini.js" type="text/javascript"></script>
<script src="jss/calender.js" type="text/javascript"></script>

<c:set var="dataTypeList" value="${controlsForm.dataTypeList}"/>
<jsp:useBean id="dataTypeList" type="java.util.List"/>	
<table summary="" cellpadding="3" cellspacing="0" border="1" align = 'center' width='100%'>
  <tr>
    <td>

	<table summary="" cellpadding="3" cellspacing="0" border="1" align = 'center' width='100%'>
		<jsp:include page="/pages/toolTypeHTML/CommonControlAttributes.jsp" />
	
<tr>
<td class="formRequiredLabel">
					<bean:message key="eav.control.type"/>
			</td>
<td>
<input type='radio' name='linesType' value='SingleLine' onclick='radioButtonClicked(this)'>SingleLine
<input type='radio' name='linesType' value='MultiLine' onclick='radioButtonClicked(this)'>MultiLine
</td>
</tr>
		<tr>
<td class="formRequiredLabel">
					<bean:message key="eav.text.noOfLines"/>
			</td>
	<td class="formField">
					<html:text styleClass="formDateSized"  maxlength="100" size="60"  property="attributeNoOfRows" />
			</td>
</tr>
		
			<tr>

			<td class="formRequiredLabel">
					<bean:message key="eav.att.TextFieldWidth"/>
			</td>

			<td class="formField">
					<html:text styleClass="formDateSized"  maxlength="100" size="60"  property="attributenoOfCols" />
			</td>

		</tr>
	 </table>
	</td>
 </tr>

	<tr>
		<td>
			<div id="substitutionDiv">
			</div>

		</td>
	</tr>
</table>
<jsp:include page="/pages/toolTypeHTML/Datatypes.jsp" />














