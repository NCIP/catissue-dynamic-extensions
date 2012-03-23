<%-- Jsp Summary                                                                                 	--%>
<%-- ---------------------------------------------------------------------------------------------- --%>
<%-- @author : sujay narkar, chetan patil --%>


<%-- TagLibs --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page isELIgnored ="false" %> 

<%@ taglib uri="/WEB-INF/dynamicExtensions.tld" prefix="dynamicExtensions" %>

<%-- Imports --%>
<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>
<%@ page import="edu.common.dynamicextensions.util.global.DEConstants" %>

<%-- Stylesheet --%>
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
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/combobox.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/ext-base.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/ext-all.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/combos.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=request.getContextPath()%>/javascripts/de/ajaxupload.js"></script>




<c:if test="${sessionScope.onFormLoad != null }">
 <script>
 var strFun = "window.parent."+'<%=request.getSession().getAttribute(DEConstants.ON_FORM_LOAD)%>';
 var deRecId = "${param.recordIdentifier123}";
 var strParam = "${requestScope.isTopLevelEntity}" + "," + deRecId;

 onFormLoad(strFun,strParam);
</script>
 </c:if>

<script language="JavaScript" >
		resetTimeoutCounter();
</script>

<script type="text/javascript">
jQuery.noConflict();
jQuery(document).ready(
	function()
	{
		jQuery('input:file', dataEntryForm).each(
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
									htmlComponent = htmlComponent + "<img src='" +imageSrc+ "' title='File uploaded'/>&nbsp;&nbsp;";
									htmlComponent = htmlComponent + "<img src='" +deleteImageSrc+ "' style='cursor:pointer' onClick='updateFileControl(\"" +controlId+ "\");' title='Delete file'/>";
									htmlComponent = htmlComponent + "<input type='hidden' name='" +controlId+ "' id='" +controlId+ "' value='" +fileId+ "'/>";
									htmlComponent = htmlComponent + "<input type='hidden' name='" +controlId+ "_hidden' id='" +controlId+ "_hidden' value='" +file+ "'/>";
									htmlComponent = htmlComponent + "<input type='hidden' name='" +controlId+ "_contentType' id='" +controlId+ "_contentType' value='" +contentType+ "'/>";
									spanElement.innerHTML = htmlComponent;
								}
								else
								{
									htmlComponent = "<input type='file' name='" +controlId+ "' id='" +controlId+ "'/>&nbsp;&nbsp;";
									htmlComponent = htmlComponent + "<span class='font_red'>Error occured .Please try again.</span>";
									spanElement.innerHTML = htmlComponent;
									updateFileControl(controlId);
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

	<body onload="loadPreviewForm('<%=request.getContextPath()%>');executeComboScriptsForSkipLogic();insertBreadCrumbForSubForm(${param.containerIdentifier},'<%=request.getSession().getAttribute("application_name")%>');setFocusOnLoad('<%=request.getAttribute("scrollPostion")%>');" onclick="window.parent.parent.detectApplicationUsageActivity()" onkeydown="window.parent.parent.detectApplicationUsageActivity()">
		<form id="dataEntryForm" action="ApplyDataEntryFormAction" method="post">

		 
		<input type="hidden" id="mode" name="mode" value="${param.mode}"/>
		<c:set var="mode" value="${param.mode}"/>
		<c:if test='${empty param.mode}'>
			<script>
				document.getElementById('mode').value = "edit";
				<c:set var="mode" value="edit"/>
			</script>
		</c:if>
			
		<c:if test='${param.showInDiv == "false"}'>
			<div id="dataEntryFormDiv" style="position:absolute;overflow:auto;height:100%;width:100%;">
			<div id="overDiv" style="position:absolute; visibility:hidden;"></div>
		</c:if>
		<c:if test='${param.showInDiv == "null" || showInDiv == "true"}'>
			<div id="dataEntryFormDiv" style="position:absolute;overflow:auto;height:100%;width:100%;z-index:1000;">
			<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>
		</c:if>

			<c:choose>
				<c:when test='${param.showFormPreview == "true"}'>
					<table valign="top" style="border-right:1px" align='center' width='100%' height="100%" border='0' cellspacing="0" cellpadding="0" class="tbBorders1" >
				</c:when>
				<c:otherwise>
					<table valign="top" align='center' width='100%' height="100%" border='0' cellspacing="0" cellpadding="0">
					<%String successMessage = request.getParameter("ApplicationSuccessMsgs");
				if(successMessage != null && !"null".equals(successMessage) && !"".equals(successMessage))
				{
			%>
						<tr valign="top" height="4%">
							<td>
								<table>
									<tr>
										<td><img src="images/uIEnhancementImages/error-green.gif"
											alt="successful messages" width="16" height="16">
										</td>
										 <td class="messagetextsuccess" nowrap="true"><font color="blue"><%=successMessage%></font></td>
									</tr>
								</table>
							</td>
						</tr>
			<%
				}
			%>
				</c:otherwise>
			</c:choose>
				<!-- Main Page heading -->
				<c:if test='${param.showFormPreview == "true"}'>
				<tr height="7%">
					<td class="formFieldNoBorders">
							<bean:message key="app.title.MainPageTitle" />
					</td>
				</tr>
				</c:if>

				<tr valign="top" height="93%">
					<td>
						<table valign="top" summary="" align='center' width='100%' cellspacing="0" cellpadding="0" border="0">
							<c:if test='${param.showFormPreview == "true"}'>
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
									<div id ='error_div' style="display:none">${errorList}</div>
									<table  align='center' width='100%'>
										<tr>
											<td>
												<table border="0" align="center" cellpadding="1" cellspacing="0" class="td_color_6e81a6">
													<tr>
														<td>
															<table width="100%" height="30"  border="0" cellpadding="4" cellspacing="4" class="td_color_FFFFCC">
																<c:forEach items="${errorList}" var="error">
																	 <tr>
																		<th align="center" class="font_bl_nor"><img src="<%=request.getContextPath()%>/images/de/ic_error.gif" alt="Error" width="28" height="25" hspace="3" align="absmiddle">
																			<c:out value="${error}"/>
																		</th>
																	</tr>
																</c:forEach>
															</table>
														</td>
													</tr>
												</table>
											</td>
                                          </tr>

										<tr>
											<td valign="top">
												<dynamicExtensions:dynamicUIGenerator
												containerIdentifier="${param.containerIdentifier}"
												recordIdentifier="${param.recordIdentifier}"
												mode="${mode}"/>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr ><td><div id = "navigationControls"> </div> </td></tr>
							<tr class="td_bg_bottom" >
								<td valign="top" colspan="7" id="submit_cancel_td">
									<table cellpadding="0" cellspacing="5" border="0" align='center'>
										<tr height="5"><td colspan="2"></tr>
										<tr align='center'>
												<c:choose>
													<c:when test='${showFormPreview=="true"}'>
														<td align='center'>
														<html:submit styleClass="actionButton" onclick="showParentContainerInsertDataPage('false')">
															<bean:message key="buttons.back" />
														</html:submit>
														</td>
													</c:when>
													<c:otherwise>
														<html:hidden styleId='isEdit' property="isEdit" value=""/>
														
														
														<c:choose>
															<c:when test="${fn:length(sessionScope.cacheMap['containerStack']) gt 1}">
																<c:set var="isTopLevelEntity" value="false" scope="request"/>
																
															</c:when>
															<c:otherwise>
																<c:set var="isTopLevelEntity" value="true" scope="request"/>
																<script>															
														</script>
															</c:otherwise>	
														</c:choose>
														
														<c:if test='${(isTopLevelEntity=="false")}'>
															<td align='center'>
																<input type="image" id="btnDESubmit" src="<%=request.getContextPath()%>/images/de/b_submit.gif" width="62" height="21" align="middle" onClick="return showParentContainerInsertDataPage('false')"/>
															</td>
															<td align='center'>
																<input type="image" id="btnDEDraft" src="<%=request.getContextPath()%>/images/de/b_calculate.gif" width="62" height="21" align="middle" onClick="return showParentContainerInsertDataPage('true');"/>
															</td>
														</c:if>

														<c:if test='${(mode=="edit") && (isTopLevelEntity=="true")}'>
															<td align='center'>
																<input type="image" id="btnDESubmit" src="<%=request.getContextPath()%>/images/de/b_submit.gif" width="62" height="21" align="middle" onClick="return setInsertDataOperation('false')"/>
															</td>
															<td align='center'>
																<input type="image" id="btnDEDraft" src="<%=request.getContextPath()%>/images/de/b_calculate.gif" width="62" height="21" align="middle" onClick="return setInsertDataOperation('true')"/>
															</td>
														<!-- BUG 7662 FIXED. Each Cancel should take you one level up in the containment hierarchy and finally the Cancel on Main Class should take you to the Add Records page.-->
														</c:if>
														
														<input type="hidden" id="dataEntryOperation" name="dataEntryOperation" value="insertChildData"/>
														<c:if test='${(isTopLevelEntity=="true")}'>
															<script>
															document.getElementById('dataEntryOperation').value = "insertParentData";
															</script>
														</c:if>
														<c:if test='${!((param.mode=="view") && (isTopLevelEntity=="false"))}'>
															<td align='center'>
																<input type="button" id="btnDECancel" style="border: 0px; background-image: url(<%=request.getContextPath()%>/images/de/b_cancel.gif); height: 21px; width: 62px;" align="middle" onClick="cancelInsertData()"/>
															</td>
														</c:if>
														<c:if test='${(param.recordIdentifier123 != "") && (param.recordIdentifier123 != "null") && (isTopLevelEntity == "true") }'>
														 <td><input type="button" id="btnDEDelete" style="border: 0px; background-image: url(<%=request.getContextPath()%>/images/de/b_delete2.gif); height: 21px; width: 62px;" align="middle" onClick="return deleteRecordEntry()"/></td>


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


			
			<input type="hidden" id='recordIdentifier' name='recordIdentifier'  value="${param.recordIdentifier}"/>
			<c:if test='${empty param.recordIdentifier}'>
				<script>
					document.getElementById('recordIdentifier').value = "${requestScope.dataEntryForm.recordIdentifier}";
				</script>
			</c:if>
			
			<input type="hidden" id='entitySaved' />
			<input type="hidden" id="containerIdentifier" name="containerIdentifier" value="${param.containerIdentifier}"/>
			<input type="hidden" id="childContainerId" name="childContainerId" value=""/>
			<input type="hidden" id="isDraft" name="isDraft" value="false"/>
			<input type="hidden" id="recordIdentifier123" name="recordIdentifier123" value="{param.recordIdentifier123}"/>
			<input type="hidden" id="childRowId" name="childRowId" value=""/>
			<input type="hidden" id="isShowTemplateRecord" name="isShowTemplateRecord" value="${param.isShowTemplateRecord}"/>
			<input type="hidden" id="showFormPreview" name="showFormPreview" value="${param.showFormPreview}"/>
			<input type="hidden" id="breadCrumbPosition" name="breadCrumbPosition" value=""/>
			<input type="hidden" id="isDirty" name="isDirty" value="false"/>
			<input type="hidden" id="isShowInDiv" name="isShowInDiv" value="${param.isShowInDiv}"/>
			<input type="hidden" id="scrollTop" name="scrollTop" value="${param.scrollTop}"/>
			<input type="hidden" id="encounterDate" name="encounterDate" value="${param.encounterDate}"/>
			<input type="hidden" id="mandatoryMessage" name="mandatoryMessage" value="${param.mandatoryMessage}"/>
			<input type="hidden" id="showInDiv" name="showInDiv" value="${param.showInDiv}"/>
			<input type="hidden" id="operation" name="operation" value=""/>
			
														
			</div>
			<c:if test="${requestScope.isDirty == 'true' || requestScope.isDirty == true}">
				<script>
					document.getElementById('isDirty').value = true;
				</script>
			</c:if>
			<c:if test='${(param.showInDiv=="false")}'>
				<script>
					document.getElementById('submit_cancel_td').style.display="none";
					document.getElementById('isShowInDiv').value = false;
				</script>
			</c:if>

		</form>
		<iframe style="display:none" src="about:blank" id="skipLogicIframe" name="skipLogicIframe" onload=""></iframe>
		<script type="text/javascript" defer="defer">
			calculateDefaultAttributesValue();
		</script>
	</body>
</html>

