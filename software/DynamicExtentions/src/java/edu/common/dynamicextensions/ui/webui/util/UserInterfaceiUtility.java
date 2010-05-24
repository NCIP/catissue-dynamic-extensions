/**
 *
 */

package edu.common.dynamicextensions.ui.webui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.upload.MultipartRequestWrapper;

import edu.common.dynamicextensions.domain.AbstractEntity;
import edu.common.dynamicextensions.domain.BaseAbstractAttribute;
import edu.common.dynamicextensions.domain.FileAttributeRecordValue;
import edu.common.dynamicextensions.domain.userinterface.AbstractContainmentControl;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AssociationMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.FormControlNotesInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.domaininterface.validationrules.RuleInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;

/**
 * The Class UserInterfaceiUtility.
 *
 * @author chetan_patil
 */

public final class UserInterfaceiUtility
{

	/** The user utility. */
	private UserInterfaceiUtility userUtility = null;

	/**
	 * Instantiates a new user interface utility.
	 */
	private UserInterfaceiUtility()
	{

	}

	/**
	 * Gets the single instance of UserInterfaceiUtility.
	 *
	 * @return single instance of UserInterfaceiUtility
	 */
	public UserInterfaceiUtility getInstance()
	{
		if (userUtility == null)
		{
			userUtility = new UserInterfaceiUtility();
		}
		return userUtility;
	}

	/**
	 * Generate htm lfor grid.
	 *
	 * @param subContainer the sub container
	 * @param valueMaps the value maps
	 * @param dataEntryOperation the data entry operation
	 * @param mainContainer the main container
	 *
	 * @return the string
	 *
	 * @throws DynamicExtensionsSystemException the dynamic extensions
	 *  system exception
	 */
	public static String generateHTMLforGrid(ContainerInterface subContainer,
			List<Map<BaseAbstractAttributeInterface, Object>> valueMaps, String dataEntryOperation,
			ContainerInterface mainContainer) throws DynamicExtensionsSystemException
	{
		StringBuffer htmlForGrid = new StringBuffer(1066);

		int rowCount = 0;
		if (valueMaps != null)
		{
			rowCount = valueMaps.size();
		}

		List<ControlInterface> controls = new ArrayList<ControlInterface>(subContainer
				.getAllControlsUnderSameDisplayLabel());

		// Do not sort the controls list;it jumbles up the attributes order.
		//Collections.sort(controlsList);

		htmlForGrid.append("<tr width='100%'><td colspan='3'" + "<div style='display:none' id='");
		htmlForGrid.append(subContainer.getId());
		htmlForGrid.append("_substitutionDiv'><table>");
		//empty hashmap to generate hidden row
		subContainer.setContainerValueMap(new HashMap<BaseAbstractAttributeInterface, Object>());
		htmlForGrid.append(getContainerHTMLAsARow(subContainer, -1, dataEntryOperation,
				mainContainer));
		htmlForGrid.append("</table></div><input type='hidden' name='");

		htmlForGrid.append(subContainer.getId());
		htmlForGrid.append("_rowCount' id= '");
		htmlForGrid.append(subContainer.getId());
		htmlForGrid.append("_rowCount' value='");
		htmlForGrid.append(rowCount);
		htmlForGrid.append("'/> </td></tr><tr width='100%'> <td "
				+ "class='formFieldContainer_withoutBorder' colspan='100'"
				+ " align='center'> <table cellpadding='3' cellspacing='0' "
				+ "align='center' width='100%'>");

		if (subContainer.getAddCaption())
		{
			htmlForGrid.append("<tr width='100%'><td class='td_color_6e81a6' "
					+ "colspan='3' align='left'>");
			htmlForGrid.append(((AbstractEntity) subContainer.getAbstractEntity())
					.getCapitalizedName(DynamicExtensionsUtility
							.replaceHTMLSpecialCharacters(subContainer.getCaption())));
			htmlForGrid.append("</td></tr>");
		}

		htmlForGrid
				.append("<tr> <td width='59'><input type='button' style='border: 0px; background-image:  ");
		htmlForGrid.append("url(images/de/b_paste.gif);height: 20px; width: 59px;'");
		htmlForGrid.append("align='middle'  id='paste_");
		htmlForGrid.append(subContainer.getId()).append("' ");
		htmlForGrid.append("onclick='pasteData(\"");
		htmlForGrid.append(subContainer.getId());
		htmlForGrid.append("\",\"many\")'/> </td><td class='formField_withoutBorder' ");
		htmlForGrid.append(" style='background-color:#E3E2E7;' width='100%'>&nbsp;</td>");
		htmlForGrid.append("</tr> <tr width='100%'><td colspan='3' width='100%'>");

		// For category attribute controls, if heading and/or notes are specified, then
		// render the UI that displays heading followed by notes for particular
		// category attribute controls.
		for (ControlInterface control : controls)
		{
			if (control.getHeading() != null && control.getHeading().length() != 0)
			{
				htmlForGrid.append("<div width=100% class='td_color_6e81a6' align='left'>"
						+ control.getHeading() + "</div>");
			}

			if (control.getFormNotes() != null && control.getFormNotes().size() != 0)
			{
				htmlForGrid.append("<div style='width:100%'>&nbsp</div>");

				for (FormControlNotesInterface fcNote : control.getFormNotes())
				{
					htmlForGrid.append("<div style='width:100%' class='notes' align='left'>"
							+ fcNote.getNote() + "</div>");
				}
			}
		}

		htmlForGrid.append("<table id='");

		htmlForGrid.append(subContainer.getId());

		htmlForGrid
				.append("_table' cellpadding='3' cellspacing='3' border='0' align='center' width='100%'><tr width='100%' class='formLabel_withoutBorder'><th width='1%'>&nbsp;</th>");

		for (ControlInterface control : controls)
		{
			boolean isControlRequired = false;
			/*if(control.getBaseAbstractAttribute() != null)
			{*/
			isControlRequired = isControlRequired(control);
			htmlForGrid.append("<th>");
			if (isControlRequired)
			{

				htmlForGrid.append("<span class='font_red'>");
				htmlForGrid.append(subContainer.getRequiredFieldIndicatior());
				htmlForGrid.append("</span>&nbsp;&nbsp;<span class='font_bl_nor'>");
				htmlForGrid.append(((BaseAbstractAttribute) control.getBaseAbstractAttribute())
						.getCapitalizedName(DynamicExtensionsUtility
								.replaceHTMLSpecialCharacters(control.getCaption())));
				htmlForGrid.append("</span>");
			}
			else
			{
				htmlForGrid.append("&nbsp;&nbsp;<span class='font_bl_nor'>");
				htmlForGrid.append(((BaseAbstractAttribute) control.getBaseAbstractAttribute())
						.getCapitalizedName(DynamicExtensionsUtility
								.replaceHTMLSpecialCharacters(control.getCaption())));
				htmlForGrid.append("</span>");
			}

			htmlForGrid.append("</th>");
		}

		htmlForGrid.append("</tr>");
		if (valueMaps != null)
		{
			int index = 1;
			for (Map<BaseAbstractAttributeInterface, Object> rowValueMap : valueMaps)
			{
				subContainer.setContainerValueMap(rowValueMap);
				htmlForGrid.append(getContainerHTMLAsARow(subContainer, index, dataEntryOperation,
						mainContainer));
				index++;
			}
		}

		htmlForGrid.append("</table> <div id='wrapper_div_");
		htmlForGrid.append(subContainer.getId());
		htmlForGrid.append("' > &nbsp;</div>");

		if (subContainer.getMode().equals("edit"))
		{
			htmlForGrid
					.append("<table cellpadding='3' cellspacing='0' align='center' width='100%' class='td_color_e3e2e7'><tr><td align='left'>"
							+ "<input type='button' style='border: 0px; background-image: url(images/de/b_delete.gif); height: 20px; width: 59px;' align='middle' onClick=\"removeCheckedRow('"
							+ subContainer.getId()
							+ "');"
							+ (subContainer.getIsSourceCalculatedAttributes()
									? "calculateAttributes();"
									: "")
							+ "\" id='btnDelete"
							+ subContainer.getId()
							+ "'/>"
							+ "</td><td align='right'>"
							+ "<input type='button' style='border: 0px; background-image: url(images/de/b_add_more.gif); height: 20px; width: 76px;' align='middle' onClick=\"addRow('"
							+ subContainer.getId()
							+ "')\" id='btnAddMore"
							+ subContainer.getId()
							+ "'/>" + "</td></tr></table>");

			//stringBuffer.append("<button type='button' class='actionButton' id='removeRow' onclick=\"removeCheckedRow('" + subContainer.getId()
			//		+ "')\">");
			//stringBuffer.append(ApplicationProperties.getValue("buttons.delete"));
			//stringBuffer.append("</button>");
			//stringBuffer.append("<button type='button' class='actionButton' id='addMore' onclick=\"addRow('" + subContainer.getId() + "')\">");
			//stringBuffer.append(ApplicationProperties.getValue("eav.button.AddRow"));
			//stringBuffer.append("</button>");
		}

		htmlForGrid.append("</td></tr>");

		return htmlForGrid.toString();
	}

	/**
	 * Checks if is control required.
	 *
	 * @param control the control
	 *
	 * @return true, if checks if is control required
	 */
	public static boolean isControlRequired(ControlInterface control)
	{
		boolean required = false;
		if (control.getBaseAbstractAttribute() instanceof AssociationMetadataInterface
				|| control.getBaseAbstractAttribute() == null)
		{
			required = false;
		}
		else
		{
			AttributeMetadataInterface attributeMetadata = (AttributeMetadataInterface) control
					.getBaseAbstractAttribute();
			Collection<RuleInterface> rules = attributeMetadata.getRuleCollection();

			if (rules != null && !rules.isEmpty())
			{
				for (RuleInterface attributeRule : rules)
				{
					if (attributeRule.getName().equals("required"))
					{
						required = true;
						break;
					}
				}
			}
		}
		return required;
	}

	/**
	 * Adds the container info.
	 *
	 * @param containers the containers
	 * @param container the container
	 * @param valueMaps the value maps
	 * @param valueMap the value map
	 */
	public static void addContainerInfo(Stack<ContainerInterface> containers,
			ContainerInterface container,
			Stack<Map<BaseAbstractAttributeInterface, Object>> valueMaps,
			Map<BaseAbstractAttributeInterface, Object> valueMap)
	{
		containers.push(container);
		valueMaps.push(valueMap);
	}

	/**
	 * Removes the container info.
	 *
	 * @param containers the containers
	 * @param valueMaps the value maps
	 */
	public static void removeContainerInfo(Stack<ContainerInterface> containers,
			Stack<Map<BaseAbstractAttributeInterface, Object>> valueMaps)
	{
		containers.pop();
		valueMaps.pop();
	}

	/**
	 * Clear container stack.
	 * @param request the request
	 */
	public static void clearContainerStack(final HttpServletRequest request)
	{
		ContainerInterface container = (ContainerInterface) CacheManager.getObjectFromCache(
				request, DEConstants.CONTAINER_INTERFACE);
		if (container != null && container.getId() != null)
		{
			request.setAttribute("containerIdentifier", container.getId().toString());
		}

		CacheManager.addObjectToCache(request, DEConstants.CONTAINER_STACK, null);
		CacheManager.addObjectToCache(request, DEConstants.VALUE_MAP_STACK, null);
		CacheManager.addObjectToCache(request, DEConstants.CONTAINER_INTERFACE, null);
		CacheManager.addObjectToCache(request, "rootRecordIdentifier", null);
	}

	/**
	 * Gets the container html as a row.
	 *
	 * @param container the container
	 * @param rowId the row id
	 * @param dataEntryOperation the data entry operation
	 * @param mainContainer the main container
	 *
	 * @return the container html as a row
	 *
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	public static String getContainerHTMLAsARow(ContainerInterface container, int rowId,
			String dataEntryOperation, ContainerInterface mainContainer)
			throws DynamicExtensionsSystemException
	{
		StringBuffer contHtmlAsARow = new StringBuffer(96);
		List<Object> values = new ArrayList<Object>();
		Map<BaseAbstractAttributeInterface, Object> containerValues = container
				.getContainerValueMap();
		List<ControlInterface> controls = new ArrayList<ControlInterface>(container
				.getAllControlsUnderSameDisplayLabel());

		// Do not sort the controls list; it jumbles up the attribute order
		//Collections.sort(controlsList);

		String rowClass = "formField_withoutBorder";
		if (rowId % 2 == 0)
		{
			rowClass = "td_color_f0f2f6";
		}
		contHtmlAsARow.append("<tr width='100%' class='");
		contHtmlAsARow.append(rowClass);
		contHtmlAsARow.append("'><td width='1%'>");

		if (container.getMode().equals("edit"))
		{
			contHtmlAsARow.append("<input type='checkbox' name='deleteRow' value='' "
					+ "id='checkBox_");
			contHtmlAsARow.append(container.getId());
			contHtmlAsARow.append('_');
			contHtmlAsARow.append(rowId);
			contHtmlAsARow.append("'/>");
		}
		else
		{
			contHtmlAsARow.append("&nbsp;");
		}

		contHtmlAsARow.append("</td>");
		for (ControlInterface control : controls)
		{
			generateHTMLforControl(rowId, dataEntryOperation, mainContainer, contHtmlAsARow,
					values, containerValues, control);
		}

		contHtmlAsARow.append("</tr>");

		return contHtmlAsARow.toString();
	}

	/**
	 * Generate html for control.
	 *
	 * @param rowId the row id
	 * @param dataEntryOperation the data entry operation
	 * @param mainContainer the main container
	 * @param contHtmlAsARow the cont html as a row
	 * @param values the values
	 * @param containerValues the container values
	 * @param control the control
	 *
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	private static void generateHTMLforControl(int rowId, String dataEntryOperation,
			ContainerInterface mainContainer, StringBuffer contHtmlAsARow, List<Object> values,
			Map<BaseAbstractAttributeInterface, Object> containerValues,
			final ControlInterface control) throws DynamicExtensionsSystemException
	{
		if (control.getIsSkipLogicTargetControl())
		{
			skipLogicControl(rowId, mainContainer, values, control);
		}
		String controlHTML = "";
		control.setDataEntryOperation(dataEntryOperation);
		control.setIsSubControl(true);

		if (control instanceof AbstractContainmentControlInterface)
		{
			controlHTML = ((AbstractContainmentControlInterface) control).generateLinkHTML();
		}
		else
		{
			if (containerValues != null)
			{
				Object value = containerValues.get(control.getBaseAbstractAttribute());
				control.setValue(value);
			}
			controlHTML = control.generateHTML(mainContainer);
			if (rowId != -1)
			{
				String oldName = control.getHTMLComponentName();
				String newName = oldName + "_" + rowId;
				controlHTML = controlHTML.replaceAll(oldName, newName);
			}
		}

		contHtmlAsARow.append("<td valign='middle' NOWRAP='true'>");
		contHtmlAsARow.append(controlHTML.replaceAll("style='float:left'", ""));
		contHtmlAsARow.append("</td>");
	}

	/**
	 * Skip logic control.
	 *
	 * @param rowId the row id
	 * @param mainContainer the main container
	 * @param values the values
	 * @param control the control
	 */
	private static void skipLogicControl(int rowId, ContainerInterface mainContainer,
			List<Object> values, ControlInterface control)
	{
		boolean isSameContainerControl;
		Object value = null;
		values.clear();
		if (control.getSourceSkipControl().getParentContainer()
				.equals(control.getParentContainer()))
		{
			isSameContainerControl = true;
		}
		else
		{
			isSameContainerControl = false;
		}
		ControlsUtility.getAttributeValueForSkipLogicAttributesFromValueMap(mainContainer
				.getContainerValueMap(), mainContainer.getContainerValueMap(), control
				.getSourceSkipControl().getBaseAbstractAttribute(), isSameContainerControl, values,
				Integer.valueOf(rowId), Integer.valueOf(rowId));
		if (!values.isEmpty())
		{
			value = values.get(0);
		}
		control.getSourceSkipControl().setValue(value);
		control.getSourceSkipControl().setSkipLogicControls();
	}

	/**
	 * This method returns the associationControl for a given Container and its child caintener id.
	 *
	 * @param container the container
	 * @param childContainerId the child container id
	 *
	 * @return the association control
	 */
	public static AbstractContainmentControlInterface getAssociationControl(
			ContainerInterface container, String childContainerId)
	{
		Collection<ControlInterface> controls = container.getAllControlsUnderSameDisplayLabel();
		AbstractContainmentControl abstrctCntnmntControl = null;
		for (ControlInterface control : controls)
		{
			if (control instanceof AbstractContainmentControlInterface)
			{
				abstrctCntnmntControl = (AbstractContainmentControl) control;
				Long containerId = abstrctCntnmntControl.getContainer().getId();
				if (containerId != null)
				{
					String associationControlId = containerId.toString();
					if (associationControlId.equals(childContainerId))
					{
						break;
					}
					else
					{
						abstrctCntnmntControl = (AbstractContainmentControl) getAssociationControl(
								abstrctCntnmntControl.getContainer(), childContainerId);
						if (abstrctCntnmntControl != null)
						{
							break;
						}
					}
				}
			}
		}
		return abstrctCntnmntControl;
	}

	/**
	 * Gets the control html as a row.
	 *
	 * @param control the control
	 * @param htmlString the html string
	 *
	 * @return the control html as a row
	 */
	public static String getControlHTMLAsARow(ControlInterface control, String htmlString)
	{
		boolean isControlRequired = UserInterfaceiUtility.isControlRequired(control);

		StringBuffer controlHtmlAsARow = new StringBuffer(166);
		controlHtmlAsARow.append("<tr><td class='formRequiredNotice_withoutBorder' width='2%'>");
		if (isControlRequired)
		{
			controlHtmlAsARow.append(control.getParentContainer().getRequiredFieldIndicatior());
			controlHtmlAsARow.append("&nbsp;</td><td class='formRequiredLabel_withoutBorder'>");
		}
		else
		{
			controlHtmlAsARow.append("&nbsp;</td><td class='formRequiredLabel_withoutBorder'>");
		}

		controlHtmlAsARow.append(((BaseAbstractAttribute) control.getBaseAbstractAttribute())
				.getCapitalizedName(DynamicExtensionsUtility.replaceHTMLSpecialCharacters(control
						.getCaption())));
		controlHtmlAsARow.append("</td><td class='formField_withoutBorder'>");
		controlHtmlAsARow.append(htmlString);
		controlHtmlAsARow.append("</td></tr>");

		return controlHtmlAsARow.toString();
	}

	/**
	 * This method returns true if the cardinality of the Containment Association is One to Many.
	 *
	 * @param control the control
	 *
	 * @return true if Caridnality is One to Many, false otherwise.
	 */
	public static boolean isCardinalityOneToMany(AbstractContainmentControlInterface control)
	{
		boolean isOneToMany = false;

		AssociationInterface association = (AssociationInterface) control
				.getBaseAbstractAttribute();
		RoleInterface targetRole = association.getTargetRole();
		if (targetRole.getMaximumCardinality() == Cardinality.MANY)
		{
			isOneToMany = true;
		}

		return isOneToMany;
	}

	/**
	 * Checks if is data present.
	 *
	 * @param valueMap the value map
	 *
	 * @return true, if checks if is data present
	 */
	public static boolean isDataPresent(Map<BaseAbstractAttributeInterface, Object> valueMap)
	{
		boolean isDataPresent = false;

		if (valueMap != null)
		{
			Set<Map.Entry<BaseAbstractAttributeInterface, Object>> mapEntrySet = valueMap
					.entrySet();
			for (Map.Entry<BaseAbstractAttributeInterface, Object> mapEntry : mapEntrySet)
			{
				Object value = mapEntry.getValue();
				if (value != null)
				{
					if ((value instanceof String) && (((String) value).length() > 0))
					{
						isDataPresent = true;
						break;
					}
					else if ((value instanceof FileAttributeRecordValue)
							&& (((FileAttributeRecordValue) value).getFileName().length() != 0))
					{
						isDataPresent = true;
						break;
					}
					else if ((value instanceof List) && (!((List) value).isEmpty()))
					{
						List valueList = (List) value;
						Object valueObject = valueList.get(0);

						if ((valueObject != null) && (valueObject instanceof Long))
						{
							isDataPresent = true;
							break;
						}
						else if ((valueObject != null) && (valueObject instanceof Map))
						{
							isDataPresent = isDataPresent((Map<BaseAbstractAttributeInterface, Object>) valueObject);
							break;
						}
					}
				}
			}
		}

		return isDataPresent;
	}

	/**
	 * This method resets request parameter map.
	 *
	 * @param request the request
	 */
	public static void resetRequestParameterMap(HttpServletRequest request)
	{
		if ((request.getParameter(WebUIManagerConstants.MODE_PARAM_NAME) != null)
				&& (request.getParameter(WebUIManagerConstants.MODE_PARAM_NAME).trim().length() > 0)
				&& (DEConstants.CANCEL.equalsIgnoreCase(request
						.getParameter(WebUIManagerConstants.MODE_PARAM_NAME)))
				&& request instanceof MultipartRequestWrapper)
		{
			MultipartRequestWrapper wrapper = (MultipartRequestWrapper) request;
			if (wrapper.getRequest() != null && wrapper.getRequest().getParameterMap() != null
					&& !wrapper.getRequest().getParameterMap().isEmpty())
			{
				wrapper.getRequest().getParameterMap().clear();
			}
		}
	}
}
