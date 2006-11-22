<%-- Jsp Summary                                                                                 	--%>
<%-- ---------------------------------------------------------------------------------------------- --%>
<%-- @author : Deepti Shelar--%>
<%-- @author : Vishvesh Mulay :)--%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/dynamicExtensions.tld" prefix="dynamicExtensions" %>
<%@ page import="edu.common.dynamicextensions.ui.webui.util.TreeData"%>
<html>
<head>
	<title>Dynamic Extensions</title>
	<link rel="stylesheet" type="text/css" href="css/styleSheet.css" />
	<script src="jss/dynamicExtensions.js" type="text/javascript"></script>
	<script src="jss/script.js" type="text/javascript"></script>
</head>

<html:form styleId = "formDefinitionForm" action="/ApplyFormDefinitionAction" >
  <body>

<!--<c:set var="groupName" value="${formDefinitionForm.groupName}"/>
<jsp:useBean id="groupName" type="java.lang.String"/>-->

<c:set var="treeData" value="${formDefinitionForm.treeData}"/>
<jsp:useBean id="treeData" type="edu.common.dynamicextensions.ui.webui.util.TreeData"/>

     <table style = "border-right:0px" border = 1 valign="top"  align='right' width='90%' height="100%" border='0' cellspacing="0" cellpadding="0" class="tbBorders1" >
         <!-- Main Page heading -->
         <tr style = "border-bottom:0px">
         	<td style = "border-right:0px;border-bottom:0px" width = '30px'>&nbsp;</td>
         	<td style = "border-left:0px;border-bottom:0px" class="formFieldSized1" >
         		<bean:message key="app.title.MainPageTitle" />
         	</td>
         </tr>
	     <tr>
            <td style = "border-top:0px;border-right:0px" width = '10px'>&nbsp;</td>
    		<td style = "border-left:0px;border-top:0px;border-bottom:0px" valign="top" >
	     		 <table valign="top" summary="" align='left' width='90%' height = '90%' cellspacing="0" cellpadding="3" class = "tbBordersAllbordersBlack" >
					<tr >
					   <td height="20" class="tabMenuItem" onmouseover="changeMenuStyle(this,'tabMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'tabMenuItem'),hideCursor()" onclick="showDefineGroupPage('formDefinitionForm')">
						 <bean:message key="app.title.DefineGroupTabTitle" />
					   </td>

					   <td height="20" class="tabMenuItemSelected" >
						 <bean:message key="app.title.DefineFormTabTitle" />
					   </td>

					   <td height="20" class="tabMenuItem" onmouseover="changeMenuStyle(this,'tabMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'tabMenuItem'),hideCursor()" onClick="showBuildFormJSP()">
						 <bean:message key="app.title.BuildFormTabTitle" />
					   </td>

					   <td height="20" class="tabMenuItem" onmouseover="changeMenuStyle(this,'tabMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'tabMenuItem'),hideCursor()"  >
						 <bean:message key="app.title.PreviewTabTitle" />
					   </td>
					   <td width="50%" class="tabMenuSeparator" colspan="3">&nbsp;</td>
					</tr>

					<tr  valign="top" >
						<td colspan="7" class = "tbBordersAllbordersNone"  >
						<table align = "top" width="100%" height = '100%' cellspacing="0">
							<tr valign="top"  >
								<!--  Tree display -->
								<td width="30%" valign="top" >
									<table width="100%" height = '100%' class = "tbBordersAllbordersBlack" >
										<tr valign="top"  width="100%">
											<td  align="left" class= "formLabelLeftBorderless" >
												<input  type="button" value="Add Form" disabled >
											</td>
										</tr>
										<tr>
										<td >
										<dynamicExtensions:tree treeDataObject="<%=treeData%>" />
										</td>
										</tr>
										<tr height = 100%> <td> &nbsp;</td>
									</table>
								</td>
								<td width="80%">
									<table cellspacing="0" cellpadding="3"  align="left" width="100%" height = '100%' class = "tbBordersAllbordersBlack"  >
										<tr valign = "top">
											 <td class="formFieldWithNoTopBorder" colspan="3">
												 <bean:message key="app.CreateFormTitle"/>
											 </td>
										</tr>
										<tr valign = "top">
											 <td class="formMessage" colspan="3">
												 <bean:message key="app.requiredMessage"/>
											 </td>
										</tr>
										<tr valign = "top">
											 <td class="formMessage" colspan="3">
												&nbsp;
											 </td>
										</tr>
										<tr valign = "top">
											 <td class="formMessage" colspan="3">
												 <html:errors/>
											 </td>
										</tr>
										<tr valign = "top">
											<td class="formRequiredNoticeWithoutBorder" width="2%">*</td>
											<td class="formRequiredLabelWithoutBorder"  width="20%">
												<bean:message key="eav.form.title"/> :
											</td>
											<td class="formFieldWithoutBorder" >
												<html:text styleClass="formDateSized"  maxlength="100" size="60"  property="formName" />
											</td>
										</tr>
										<tr valign = "top">
											<td class="formRequiredNoticeWithoutBorder" width="2%" >*</td>
											<td class="formRequiredLabelWithoutBorder" width="20%">
												<bean:message key="eav.form.conceptCode"/> :
											</td>
											<td class="formFieldWithoutBorder">
												<html:text styleClass="formDateSized"  maxlength="20" size="40"  property="conceptCode" />
											</td>
										</tr>
										<tr valign = "top">
											<td class="formRequiredNoticeWithoutBorder" width="2%" >&nbsp;</td>
											<td class="formRequiredLabelWithoutBorder" width="20%">
													<bean:message key="eav.form.description"/> :
											</td>
											<td class="formFieldWithoutBorder">
													<html:textarea styleClass="formFieldSmallSized"  rows = "3" cols="28"  property="formDescription" />
											</td>
										 </tr>
										 <tr valign = "top">
											<td class="formRequiredNoticeWithoutBorder" width="2%">*</td>
											 <td class="formRequiredLabelWithoutBorder" width="20%">
												<bean:message key="eav.form.createAs"/> :
											 </td>
											 <td class="formFieldWithoutBorder">
												<table border='0'>
													<tr class="formMessage">
														 <td >
															<html:radio property="createAs" value="NewForm">
																<bean:message key="eav.createnewentity.title"/>
															</html:radio>
															<html:radio property="createAs" value="ExistingForm" disabled="true">
																<bean:message key="eav.existingentity.title"/>
															</html:radio>
														</td>
													</tr>
												</table>
											</td>
										 </tr>
										 <tr height = '100%' valign = "top">
											<td colspan="3">&nbsp;</td>
										 </tr>
									</table>
								</td>
							</tr>
							<tr height="5%" valign = "top"   class= "formLabelBorderless"  >
								<td align="left" >
									<html:submit styleClass="actionButton">
										<bean:message  key="buttons.save" />
									</html:submit>
									<html:button styleClass="actionButton" property="cancelButton" onclick="showHomePageFromCreateForm()">
														<bean:message  key="buttons.cancel" />
									</html:button>
								</td>

								<td  align="right"   >
									<html:button styleClass="actionButton" property="nextButton" onclick="showBuildFormJSP()" >
														<bean:message  key="buttons.next" />
									</html:button>
								</td>
							</tr>
					</table>
				</td>
			 </tr>
		</table>
	</td>
</tr>
</table>

  </body>
<html:hidden styleId = 'operation' property="operation" value=""/>
<html:hidden styleId = 'entityIdentifier'  property="entityIdentifier" value=""/>
</html:form>
</html>
