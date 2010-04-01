<%-- Jsp Summary                                                                                 	--%>
<%-- ---------------------------------------------------------------------------------------------- --%>
<%-- @author : sujay narkar, chetan patil --%>

<%-- TagLibs --%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/dynamicExtensions.tld" prefix="dynamicExtensions" %>

<%-- Imports --%>
<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>
<%@ page import="org.apache.struts.action.ActionErrors" %>
<%@ page import="org.apache.struts.action.ActionMessages" %>

<%-- Stylesheet --%>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/styleSheet.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/de_style.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/ext-all.css" />
<link href="<%=request.getContextPath()%>/stylesheets/de/calanderComponent.css" type=text/css rel=stylesheet />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/stylesheets/de/xtheme-gray.css" />

<script src="<%=request.getContextPath()%>/javascripts/de/calendarComponent.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/script.js" type="text/javascript"></script>

<script>var imgsrc="<%=request.getContextPath()%>/images/de/";</script>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/prototype.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/jquery-1.3.2.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/form_plugin.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/dynamicExtensions.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/overlib_mini.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/calender.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/calendarComponent.js"></script>
<script src="<%=request.getContextPath()%>/javascripts/de/ajax.js"></script>

<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/scriptaculous.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/scr.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/combobox.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/ext-base.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/ext-all.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/combos.js"></script>


<c:set var="containerInterface" value="${dataEntryForm.containerInterface}"/>
<jsp:useBean id="containerInterface" type="edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface"/>

<c:set var="previousDataMap" value="${dataEntryForm.previousDataMap}"/>
<jsp:useBean id="previousDataMap" type="java.util.Map"/>

<c:set var="showFormPreview" value="${dataEntryForm.showFormPreview}"/>
<jsp:useBean id="showFormPreview" type="java.lang.String"/>

<c:set var="errorList" value="${dataEntryForm.errorList}"/>
<jsp:useBean id="errorList" type="java.util.List"/>

<c:set var="dataEntryOperation" value="${dataEntryForm.dataEntryOperation}"/>
<jsp:useBean id="dataEntryOperation" type="java.lang.String"/>

<c:set var="isShowTemplateRecord" value="${dataEntryForm.isShowTemplateRecord}"/>
<jsp:useBean id="isShowTemplateRecord" type="java.lang.String"/>

<c:set var="recordIdentifier123" value="${dataEntryForm.recordIdentifier}" />
<jsp:useBean id="recordIdentifier123" type="java.lang.String"/>

<c:set var="mode" value="${dataEntryForm.mode}" />
<jsp:useBean id="mode" type="java.lang.String"/>

<c:set var="isTopLevelEntity" value="${dataEntryForm.isTopLevelEntity}" />
<jsp:useBean id="isTopLevelEntity" type="java.lang.Boolean"/>

<c:if test="${param.application == 'clinportal'}">
	<c:set var="application_name" value="${param.application}" scope="session"/>
</c:if>

<c:if test="${param.showInDiv == 'false'}">
	<c:set var="showInDiv" value="${param.showInDiv}" scope="session"/>
</c:if>
<c:if test="${showInDiv == 'null' || showInDiv == null || param.showInDiv == 'true'}">
	<c:set var="showInDiv" value="true" scope="session"/>
</c:if>

<c:if test="${param.mandatoryMessage == 'false'}">
	<c:set var="mandatory_Message" value="${param.mandatoryMessage}" scope="session"/>
</c:if>
<c:if test="${mandatory_Message == 'null' || mandatory_Message == null || param.mandatoryMessage == 'true'}">
	<c:set var="mandatory_Message" value="true" scope="session"/>
</c:if>
<script language="JavaScript" >
		resetTimeoutCounter();
</script>

<script type="text/javascript">
$(document).ready(
	function()
	{
		$('input:file', dataEntryForm).each(
			function()
			{
				var controlId = this.id;
				new AjaxUpload(this,
					{
					   action: 'UploadFile.do',
					   name: 'upload',
					   responseType: 'json',
					   onSubmit : function(file,extension)
							{
								var submitButton = document.getElementById('btnDESubmit');
								var imageSrc = "<%=request.getContextPath()%>/images/de/waiting.gif";
								var buttonName = controlId + "_button";
								var spanElement = document.getElementById(buttonName);
								var htmlComponent = spanElement.innerHTML;
								htmlComponent = htmlComponent + "&nbsp;&nbsp;<img src='" +imageSrc+ "'/>";
								spanElement.innerHTML = htmlComponent;

							},
					   onComplete : function(file, response)
							{
								var jsonResponse = response;
								var fileId = "1";
								var contentType = "";
								var htmlComponent = "";
								var buttonName = controlId + "_button";
								var spanElement = document.getElementById(buttonName);

								if(jsonResponse.uploadedFile!=null)
								{
									fileId = jsonResponse.uploadedFile[0].uploadedFileId;
									contentType = jsonResponse.uploadedFile[0].contentType;
									var imageSrc = "<%=request.getContextPath()%>/images/uIEnhancementImages/error-green.gif";
									var deleteImageSrc = "<%=request.getContextPath()%>/images/de/deleteIcon.jpg";

									htmlComponent = "<input type='text' disabled name='" +controlId+ "'_1 id='" +controlId+ "_1' value='" +file+ "'/>&nbsp;&nbsp;";
									htmlComponent = htmlComponent + "<img src='" +imageSrc+ "' />&nbsp;&nbsp;";
									htmlComponent = htmlComponent + "<img src='" +deleteImageSrc+ "' style='cursor:pointer' onClick='updateFileControl(" +controlId+ ");' />";
									htmlComponent = htmlComponent + "<input type='hidden' name='" +controlId+ "' id='" +controlId+ "' value='" +fileId+ "'/>";
									htmlComponent = htmlComponent + "<input type='hidden' name='" +controlId+ "_hidden' id='" +controlId+ "_hidden' value='" +file+ "'/>";
									htmlComponent = htmlComponent + "<input type='hidden' name='" +controlId+ "_contentType' id='" +controlId+ "_contentType' value='" +contentType+ "'/>";
									spanElement.innerHTML = htmlComponent;
								}
								else
								{
									updateFileControl(controlId);
									//htmlComponent = "<input type='file' name='" +controlId+ "' id='" +controlId+ "'/>&nbsp;&nbsp;";
									//htmlComponent = htmlComponent + "<span class='font_red'>Error occured .Please try again.</span>";
									//spanElement.innerHTML = htmlComponent;
									//window.location.reload();
								}
							}
					} );
	 		} );
	 } );
</script>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
		<title><bean:message key="table.heading" /></title>
		<script>
		function setFocusToFirstControl()
		{
			var elements=document.dataEntryForm.elements;
			for(var i = 0; i < elements.length; i++)
			{
				if (elements[i].type != "hidden" && elements[i].disabled != true)
		        {
					try
					{
						elements[i].focus();
						break;
					}
					catch(E)
					{
						continue;
					}
				}
			}
		}
		</script>
	</head>

	<body onload="document.getElementById('dataEntryFormDiv').scrollTop='<%=request.getAttribute("scrollPostion")%>';executeComboScriptsForSkipLogic();loadPreviewForm('<%=request.getContextPath()%>');insertBreadCrumbForSubForm(<%=containerInterface.getId()%>,'<%=request.getSession().getAttribute("application_name")%>')" onclick="window.parent.parent.detectApplicationUsageActivity()" onkeydown="window.parent.parent.detectApplicationUsageActivity()">
		<html:form styleId="dataEntryForm" action="/ApplyDataEntryFormAction" enctype="multipart/form-data" method="post">
		<c:if test='${showInDiv == "false"}'>
			<div id="dataEntryFormDiv" style="position:absolute;overflow:auto;height:100%;width:100%;">
			<div id="overDiv" style="position:absolute; visibility:hidden;"></div>
		</c:if>
		<c:if test='${showInDiv == "null" || showInDiv == "true"}'>
			<div id="dataEntryFormDiv" style="position:absolute;overflow:auto;height:100%;width:100%;z-index:1000;">
			<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>
		</c:if>

			<c:choose>
				<c:when test='${showFormPreview == "true"}'>
					<table valign="top" style="border-right:1px" align='center' width='100%' height="100%" border='0' cellspacing="0" cellpadding="0" class="tbBorders1" >
				</c:when>
				<c:otherwise>
					<table valign="top" align='center' width='100%' height="100%" border='0' cellspacing="0" cellpadding="0">
				</c:otherwise>
			</c:choose>
				<!-- Main Page heading -->
				<c:if test='${showFormPreview == "true"}'>
				<tr height="7%">
					<td class="formFieldNoBorders">
							<bean:message key="app.title.MainPageTitle" />
					</td>
				</tr>
				</c:if>

				<tr valign="top" height="93%">
					<td>
						<table valign="top" summary="" align='center' width='100%' cellspacing="0" cellpadding="0" border="0">
							<c:if test='${showFormPreview == "true"}'>
								<tr valign="top">
									<td height="20" class="tabMenuItem" onmouseover="changeMenuStyle(this,'tabMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'tabMenuItem'),hideCursor()" onclick="alert('This page is still under construction and will be available in the next release');">
										<bean:message key="app.title.DefineGroupTabTitle" />
									</td>
									<td height="20" class="tabMenuItem" onmouseover="changeMenuStyle(this,'tabMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'tabMenuItem'),hideCursor()" onclick="showFormDefinitionPage()">
										<bean:message key="app.title.DefineFormTabTitle" />
									</td>
									<td height="20" class="tabMenuItem" onmouseover="changeMenuStyle(this,'tabMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'tabMenuItem'),hideCursor()" onclick="backToControlForm()">
										<bean:message key="app.title.BuildFormTabTitle" />
									</td>
									<td height="20" class="tabMenuItemSelected"  >
										<bean:message key="app.title.PreviewTabTitle" />
									</td>
									<td width="50%" class="tabMenuSeparator" colspan="3">&nbsp;</td>
								</tr>

							</c:if>
							<tr valign="top">
								<td colspan="7">
									<div id ='error_div' style="display:none"><%=errorList.isEmpty()?"":errorList%></div>
									<table  align='center' width='100%'>
										<tr>
											<%
												if(errorList.size() != 0)
												{
											%>
												<table border="0" align="center" cellpadding="1" cellspacing="0" class="td_color_6e81a6">
                                              		<tr>
													<td>
													<table width="100%" height="30"  border="0" cellpadding="4" cellspacing="4" class="td_color_FFFFCC">
													<c:forEach items="${errorList}" var="error">
														<jsp:useBean id="error" type="java.lang.String"/>
														 <tr>
														<th align="center" class="font_bl_nor"><img src="<%=request.getContextPath()%>/images/de/ic_error.gif" alt="Error" width="28" height="25" hspace="3" align="absmiddle">
															<c:out value="${error}"/><br />
													</c:forEach>
														</th>
                                                    	</tr>
                                                	</table>
													</td>
													</tr>
												</table>
											<%
												} else {


											%>		<logic:messagesPresent message="true">
													<table border="0" align="center" cellpadding="1" cellspacing="0" class="td_color_6e81a6">
														<tr>
															<td><table width="100%" height="30"  border="0"		cellpadding="4" cellspacing="4"			class="td_color_FFFFCC">
																	<tr>
																		<th align="left" class="font_bl_nor">
																		<ul>
																			<html:messages id="msg" message="true">
																			<img src="<%=request.getContextPath()%>/images/de/ic_success.gif" alt="Success" width="28" height="25" hspace="3" align="absmiddle"><bean:write name="msg"/>
																			</html:messages>
																		</ul>
																		</th>
																	</tr>
																 </table>
																</td>
															</tr>
														</table>
													    </logic:messagesPresent>
													<%

												}
											%>
                                          </tr>

										<tr>
											<td valign="top">
												<dynamicExtensions:dynamicUIGenerator
												containerInterface="<%=containerInterface%>"
												previousDataMap="<%=previousDataMap%>"/>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr class="td_bg_bottom">
								<td valign="top" colspan="7" id="submit_cancel_td">
									<table cellpadding="0" cellspacing="5" border="0" align='center'>
										<tr height="5"><td colspan="2"></tr>
										<tr align='center'>
												<c:choose>
													<c:when test='${showFormPreview=="true"}'>
														<td align='center'>
														<html:submit styleClass="actionButton" onclick="showParentContainerInsertDataPage()">
															<bean:message key="buttons.back" />
														</html:submit>
														</td>
													</c:when>
													<c:otherwise>
														<html:hidden styleId='isEdit' property="isEdit" value=""/>

														<c:if test='${(isTopLevelEntity=="false") && ((showInDiv=="null") || (showInDiv=="true"))}'>
															<td align='center'>
																<input type="image" id="btnDESubmit" src="<%=request.getContextPath()%>/images/de/b_submit.gif" width="62" height="21" align="middle" onClick="return showParentContainerInsertDataPage()"/>
															</td>
														</c:if>

														<c:if test='${(mode=="edit") && (isTopLevelEntity=="true") && ((showInDiv=="null") || (showInDiv=="true"))}'>
															<td align='center'>
																<input type="image" id="btnDESubmit" src="<%=request.getContextPath()%>/images/de/b_submit.gif" width="62" height="21" align="middle" onClick="return setInsertDataOperation()"/>
															</td>
														<!-- BUG 7662 FIXED. Each Cancel should take you one level up in the containment hierarchy and finally the Cancel on Main Class should take you to the Add Records page.-->
														</c:if>
															<input type="hidden" id="operation_mode" value="insertChildData"/>
														<c:if test='${(isTopLevelEntity=="true")}'>
															<script>
															document.getElementById('operation_mode').value = "insertParentData";
															</script>
														</c:if>

														<c:if test='${!((mode=="view") && (isTopLevelEntity=="false"))  && ((showInDiv=="null") || (showInDiv=="true"))}'>
															<td align='center'>
																<input type="button" id="btnDECancel" style="border: 0px; background-image: url(<%=request.getContextPath()%>/images/de/b_cancel.gif); height: 21px; width: 62px;" align="middle" onClick="cancelInsertData()"/>
															</td>
														</c:if>
													</c:otherwise>
												</c:choose>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>


			<html:hidden styleId='recordIdentifier' property="recordIdentifier"/>
			<html:hidden styleId='entitySaved' property="entitySaved"/>
			<html:hidden property="containerId" styleId="dataEntryForm" value="<%=containerInterface.getId().toString()%>"/>
			<input type="hidden" id="childContainerId" name="childContainerId" value=""/>
			<input type="hidden" id="childRowId" name="childRowId" value=""/>
			<input type="hidden" id="dataEntryOperation" name="dataEntryOperation" value="<%=dataEntryOperation%>"/>
			<input type="hidden" id="isShowTemplateRecord" name="isShowTemplateRecord" value="<%=isShowTemplateRecord%>"/>
			<input type="hidden" id="showFormPreview" name="showFormPreview" value="<%=showFormPreview%>"/>
			<input type="hidden" id="mode" name="mode" value="<%=mode%>"/>
			<input type="hidden" id="breadCrumbPosition" name="breadCrumbPosition" value=""/>
			<input type="hidden" id="isDirty" name="isDirty" value="false"/>
			<input type="hidden" id="isShowInDiv" name="isShowInDiv" value="true"/>
			<input type="hidden" id="scrollTop" name="scrollTop" value="null"/>
			</div>
			<c:if test="${requestScope.isDirty == 'true' || requestScope.isDirty == true}">
				<script>
					document.getElementById('isDirty').value = true;
				</script>
			</c:if>
			<c:if test='${(showInDiv=="false")}'>
				<script>
					document.getElementById('submit_cancel_td').style.display="none";
					document.getElementById('isShowInDiv').value = false;
				</script>
			</c:if>
		</html:form>
		<iframe style="display:none" src="about:blank" id="skipLogicIframe" name="skipLogicIframe" onload=""></iframe>
	</body>
</html>