<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ page import="edu.common.dynamicextensions.ui.util.Constants"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<br>
<table border="0" width="65%">
	<tr>
		<td width="35%" valign="top" align="left" style="padding-left:0px">
			<input type='text' id='coord' name='coord'  value =' ' size='20'/>
			<c:choose>
				<c:when test="${addNewUserActionName != null}">
					<html:link href="#" styleId="newUser" styleClass="black_ar_new" onclick="selectPresentCoordinators();addNewAction('ClinicalStudyAddNew.do?addNewForwardTo=protocolCoordinator&forwardTo=clinicalStudy&addNewFor=protocolCoordinator')">
						<bean:message key="buttons.addNew" />
					</html:link>
				</c:when>
				<c:otherwise></c:otherwise>
			</c:choose>
		</td>

		<td width="20%" align="center" valign="top">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="22" align="center" valign="top">
						<div id="addLink"> 
							<a href="#" onclick="moveOptions('coord','protocolCoordinatorIds', 'add')">
								<img src="images/b_add_inact.gif" alt="Add" height="18" border="0" align="absmiddle" />
							</a>
						</div>
					</td>
				</tr>

				<tr>							
					<td height="22" align="center">
						<div id="removeLink">
							<a href="#" onclick="moveOptions('protocolCoordinatorIds','coord', 'edit')">
								<img src="images/b_remove_inact.gif" alt="Remove" height="18" border="0" align="absmiddle" />
							</a>
						</div>
					</td>							
				</tr>
			</table>
		</td>
				
		<td width="50%" align="center" class="black_ar_new">
			<html:select property="protocolCoordinatorIds" styleId="protocolCoordinatorIds" size="4" multiple="true" style="width:170"><html:options collection="<%=Constants.SELECTED_VALUES%>" labelProperty="name" property="value"/></html:select>
		</td>
	</tr>
</table>