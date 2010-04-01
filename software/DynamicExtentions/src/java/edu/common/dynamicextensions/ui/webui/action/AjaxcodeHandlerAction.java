/*
 * Created on Dec 19, 2006
 * @author
 *
 */

package edu.common.dynamicextensions.ui.webui.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.domain.FileAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.AssociationMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.entitymanager.DynamicExtensionsQueryBuilderConstantsInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.exception.DynamicExtensionsValidationException;
import edu.common.dynamicextensions.processor.GroupProcessor;
import edu.common.dynamicextensions.processor.ProcessorConstants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.ui.util.SemanticPropertyBuilderUtil;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.ui.webui.util.UserInterfaceiUtility;
import edu.common.dynamicextensions.ui.webui.util.WebUIManager;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.validation.ValidatorUtil;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.logger.Logger;

/**
 * @author preeti_munot,suhas_khot
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AjaxcodeHandlerAction extends BaseDynamicExtensionsAction
{

	/**
	 *
	 */
	private static final String TABLE_END_STRING = "</td></tr></table>";

	/**
	 * path of breadCrumb image
	 */
	private static final String BREAD_CRUMB_IMAGE = "<img alt=\"\" src=\"images/uIEnhancementImages/breadcrumb_arrow.png\">";

	/**
	 * @param mapping ActionMapping mapping
	 * @param form ActionForm form
	 * @param  request HttpServletRequest request
	 * @param response HttpServletResponse response
	 * @return ActionForward forward to next action
	 * @throws DynamicExtensionsApplicationException
	 */
	@Override
	public ActionForward execute(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final HttpServletResponse response)
			throws DynamicExtensionsApplicationException
	{
		String returnXML = null;
		String containerId = request.getParameter("containerId");
		try
		{

			final String operation = request.getParameter("ajaxOperation");
			if (operation != null)
			{
				if (operation.trim().equals("selectFormNameFromTree"))
				{
					final String selectedFormName = request.getParameter("selectedFormName");
					final String rectifySelectName = rectifySelectedName(selectedFormName);
					if (rectifySelectName != null)
					{
						returnXML = getSelectedFormDetails(request, rectifySelectName);
					}
				}
				else if (operation.trim().equals("selectFormNameFromAssociationTree"))
				{
					final String selectedFormId = request.getParameter("selectedFormId");
					if (selectedFormId != null)
					{
						returnXML = getSelectedFormDetailsById(selectedFormId);
					}
				}
				else if (operation.trim().equals("selectGroup"))
				{
					final String selectedGroupName = request.getParameter("selectedGroupName");
					if (selectedGroupName != null)
					{
						returnXML = getSelectedGroupDetails(selectedGroupName);
					}
				}
				else if (operation.trim().equals("deleteRowsForContainment"))
				{
					final String deletedRowIds = request.getParameter("deletedRowIds");
					containerId = request.getParameter("containerId");
					returnXML = deleteRowsForContainment(request, deletedRowIds, containerId);
				}
				else if (operation.trim().equals("updateControlsSequence"))
				{
					final String gridControlsIds = request.getParameter("gridControlIds");
					returnXML = updateControlsSequence(request, gridControlsIds);
				}
				else if (operation.trim().equals("changeGroup"))
				{
					returnXML = changeGroup(request);
				}
				else if (operation.trim().equals("changeForm"))
				{
					returnXML = changeForm(request);
				}
				else if (operation.trim().equals("breadCrumbOperation")
						&& ((request.getParameter("breadCrumbOperation") != null) && request
								.getParameter("breadCrumbOperation").equalsIgnoreCase(
										"prepareBreadCrumbLink")))
				{
					returnXML = breadCrumbOperation(request);
				}
				else if (operation.trim().equals("pasteData"))
				{
					returnXML = pasteData(request);
				}

			}
			sendResponse(returnXML, response);
			return null;
		}
		catch (final Exception e)
		{
			Logger.out.error(e.getMessage());
			final String actionForwardString = catchException(e, request);
			if ((actionForwardString == null) || (actionForwardString.equals("")))
			{
				return mapping.getInputForward();
			}
			return null;
		}
	}

	/**
	 *
	 * @param selectedName
	 * @return
	 */
	private String rectifySelectedName(final String selectedName)
	{
		final String[] subStrings = selectedName.split("_");
		final StringBuffer rectifiedName = new StringBuffer();
		for (int i = 1; i < subStrings.length; i++)
		{
			rectifiedName.append(subStrings[i]);
		}

		if (rectifiedName.length() == 0)
		{
			rectifiedName.append(subStrings[0]);
		}
		return rectifiedName.toString();
	}

	/**
	 * @param selectedFormId
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	private String getSelectedFormDetailsById(final String selectedFormId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		String formName = "", formDescription = "", formConceptCode = "";
		boolean isAbstract = false;
		if (selectedFormId != null)
		{
			ContainerInterface containerForSelectedForm = DynamicExtensionsUtility
					.getContainerByIdentifier(selectedFormId);
			if (containerForSelectedForm != null)
			{
				formName = containerForSelectedForm.getCaption();
				final AbstractEntityInterface entity = containerForSelectedForm.getAbstractEntity();
				if (entity != null)
				{
					formDescription = entity.getDescription();
					if (formDescription == null)
					{
						formDescription = "";
					}
					formConceptCode = SemanticPropertyBuilderUtil.getConceptCodeString(entity);
					if (formConceptCode == null)
					{
						formConceptCode = "";
					}
					isAbstract = entity.isAbstract();
				}
			}
		}
		String formDetailsXML = createFormDetailsXML(formName, formDescription, formConceptCode,
				DEConstants.ADD_SUB_FORM_OPR, isAbstract);
		if (formDetailsXML == null)
		{
			formDetailsXML = "";
		}
		return formDetailsXML;
	}

	/**
	 * @param request
	 * @param controlsSeqNumbers
	 * @return
	 */
	private String updateControlsSequence(final HttpServletRequest request,
			final String controlsSeqNumbers)
	{
		final ContainerInterface container = WebUIManager.getCurrentContainer(request);
		if (container != null)
		{
			final Collection<ControlInterface> oldControlsCollection = container
					.getControlCollection();

			if (oldControlsCollection != null)
			{
				final Integer[] sequenceNumbers = DynamicExtensionsUtility.convertToIntegerArray(
						controlsSeqNumbers, ProcessorConstants.CONTROLS_SEQUENCE_NUMBER_SEPARATOR);
				final ControlInterface[] oldControls = oldControlsCollection
						.toArray(new ControlInterface[oldControlsCollection.size()]);

				// Adding id attribute to attribute collection.
				AttributeInterface idAttribute = null;
				final Collection<AttributeInterface> attributeCollection = ((EntityInterface) container
						.getAbstractEntity()).getAllAttributes();
				for (final AttributeInterface attributeIterator : attributeCollection)
				{
					if ((attributeIterator.getColumnProperties().getName() != null)
							&& attributeIterator.getColumnProperties().getName().equals(
									DynamicExtensionsQueryBuilderConstantsInterface.IDENTIFIER))
					{
						idAttribute = attributeIterator;
						break;
					}
				}

				// Remove old controls from collection.
				container.removeAllControls();
				(container.getAbstractEntity()).removeAllAttributes();

				if (sequenceNumbers != null)
				{
					for (final Integer sequenceNumber : sequenceNumbers)
					{
						ControlInterface control = DynamicExtensionsUtility
								.getControlBySequenceNumber(oldControls, sequenceNumber.intValue());
						if (control != null)
						{
							container.addControl(control);
							((EntityInterface) container.getAbstractEntity())
									.addAbstractAttribute((AbstractAttributeInterface) control
											.getBaseAbstractAttribute());
						}
					}
				}

				if (idAttribute != null)
				{
					((EntityInterface) container.getAbstractEntity())
							.addAbstractAttribute(idAttribute);
				}
				//Added by Rajesh for removing deleted associations and it's path from path tables.
				List<Long> deletedIdList = (List<Long>) CacheManager.getObjectFromCache(request,
						WebUIManagerConstants.DELETED_ASSOCIATION_IDS);
				final List<Long> listOfIds = DynamicExtensionsUtility.getDeletedAssociationIds(
						oldControls, sequenceNumbers);
				if (deletedIdList == null)
				{
					deletedIdList = listOfIds;
				}
				else
				{
					deletedIdList.addAll(listOfIds);
				}
				CacheManager.addObjectToCache(request,
						WebUIManagerConstants.DELETED_ASSOCIATION_IDS, deletedIdList);
				//				System.out.println("deletedIdList : " + deletedIdList.size());
			}
		}

		final Collection<ControlInterface> controlCollection = container.getControlCollection();
		for (final ControlInterface control : controlCollection)
		{
			Logger.out.info("[" + control.getSequenceNumber() + "] = [" + control.getCaption()
					+ "]");
		}

		return "";
	}

	/**
	 * @param request
	 * @param deletedRowIds
	 * @param childContainerId
	 * @return
	 */
	private String deleteRowsForContainment(final HttpServletRequest request,
			final String deletedRowIds, final String childContainerId)
	{
		final Stack containerStack = (Stack) CacheManager.getObjectFromCache(request,
				DEConstants.CONTAINER_STACK);
		final Stack valueMapStack = (Stack) CacheManager.getObjectFromCache(request,
				DEConstants.VALUE_MAP_STACK);

		final Map<AbstractAttributeInterface, Object> valueMap = (Map<AbstractAttributeInterface, Object>) valueMapStack
				.peek();
		final ContainerInterface containerInterface = (ContainerInterface) containerStack.peek();

		final AbstractContainmentControlInterface associationControl = UserInterfaceiUtility
				.getAssociationControl(containerInterface, childContainerId);

		final AssociationMetadataInterface association = (AssociationMetadataInterface) associationControl
				.getBaseAbstractAttribute();

		final List<Map<AbstractAttributeInterface, Object>> associationValueMapList = (List<Map<AbstractAttributeInterface, Object>>) valueMap
				.get(association);

		final String[] deletedRows = deletedRowIds.split(",");

		for (final String deletedRow : deletedRows)
		{
			final int removeIndex = Integer.valueOf(deletedRow) - 1;

			if (associationValueMapList.size() > removeIndex)
			{
				associationValueMapList.remove(removeIndex);
			}

		}

		return "";
	}

	/**
	 * @param selectedGroupName
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	private String getSelectedGroupDetails(final String selectedGroupName)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		EntityGroupInterface entityGroup = null;
		if ((selectedGroupName != null) && (!selectedGroupName.trim().equals("")))
		{
			final GroupProcessor groupProcessor = GroupProcessor.getInstance();
			entityGroup = groupProcessor.getEntityGroupByIdentifier(selectedGroupName);
		}
		return getGroupDetailsXML(entityGroup);
	}

	/**
	 * @param entityGroup
	 * @return
	 */
	private String getGroupDetailsXML(final EntityGroupInterface entityGroup)
	{
		String groupDescription = null;
		if (entityGroup != null)
		{
			groupDescription = entityGroup.getDescription();
		}
		if (groupDescription == null)
		{
			groupDescription = "";
		}
		String groupDetailsXML = createGroupDetailsXML(groupDescription);
		if (groupDetailsXML == null)
		{
			groupDetailsXML = "";
		}
		return groupDetailsXML;
	}

	/**
	 * @param groupDescription
	 * @return
	 */
	private String createGroupDetailsXML(final String groupDescription)
	{
		final StringBuffer responseXML = new StringBuffer(54);
		responseXML.append("<group><group-description>");
		responseXML.append(groupDescription);
		responseXML.append("</group-description></group>");
		return responseXML.toString();

	}

	/**
	 * @param request
	 * @param selectedFormName
	 */
	private String getSelectedFormDetails(final HttpServletRequest request,
			final String selectedFormName)
	{
		ContainerInterface containerForSelectedForm = null;
		if ((request != null) && (selectedFormName != null))
		{
			containerForSelectedForm = (ContainerInterface) CacheManager.getObjectFromCache(
					request, selectedFormName);
			if (containerForSelectedForm != null)
			{
				updateCacheRefernces(request, selectedFormName, containerForSelectedForm);
			}
		}
		final String formDetailsXML = getFormDetailsXML(request, selectedFormName,
				containerForSelectedForm);
		return formDetailsXML;
	}

	/**
	 * @param containerForSelectedForm
	 * @return
	 */
	private String getFormDetailsXML(final HttpServletRequest request,
			final String selectedFormName, final ContainerInterface containerForSelectedForm)
	{
		String formName = selectedFormName;
		String formDescription = "";
		String formConceptCode = "";
		boolean isAbstract = false;
		if (containerForSelectedForm != null)
		{
			formName = containerForSelectedForm.getCaption();
			final AbstractEntityInterface entity = containerForSelectedForm.getAbstractEntity();
			if (entity != null)
			{
				formDescription = entity.getDescription();
				formConceptCode = SemanticPropertyBuilderUtil.getConceptCodeString(entity);
				isAbstract = entity.isAbstract();
			}

		}
		String operationMode = getOperationMode(containerForSelectedForm, request);
		//If selected form container is null and cache container interface is also null,
		// it means that there is no container in cache and a new form is to be created.

		String formDetailsXML = createFormDetailsXML(formName, formDescription, formConceptCode,
				operationMode, isAbstract);
		if (formDetailsXML == null)
		{
			formDetailsXML = "";
		}
		return formDetailsXML;
	}

	/**
	 * It will return the current operation mode.
	 * @param containerForSelectedForm container
	 * @param request request
	 * @return operation mode.
	 */
	private String getOperationMode(ContainerInterface containerForSelectedForm,
			HttpServletRequest request)
	{
		String operationMode = DEConstants.ADD_SUB_FORM_OPR;
		if (containerForSelectedForm == null)
		{
			final ContainerInterface mainContainerInterface = (ContainerInterface) CacheManager
					.getObjectFromCache(request, DEConstants.CONTAINER_INTERFACE);
			if (mainContainerInterface == null)
			{
				operationMode = DEConstants.ADD_NEW_FORM;
			}
		}
		else
		{
			operationMode = DEConstants.EDIT_FORM;
		}
		return operationMode;
	}

	/**
	 * @param formName
	 * @param formDescription
	 * @param formConceptCode
	 * @return
	 */
	private String createFormDetailsXML(final String formName, final String formDescription,
			final String formConceptCode, final String operationMode, final boolean isAbstract)
	{
		final StringBuffer responseXML = new StringBuffer(166);
		responseXML.append("<form><form-name>");
		responseXML.append(formName);
		responseXML.append("</form-name><form-description>");
		responseXML.append(formDescription);
		responseXML.append("</form-description><form-conceptcode>");
		responseXML.append(formConceptCode);
		responseXML.append("</form-conceptcode><operationMode>");
		responseXML.append(operationMode);
		responseXML.append("</operationMode><isAbstract>");
		responseXML.append(isAbstract);
		responseXML.append("</isAbstract></form>");
		return responseXML.toString();
	}

	/**
	 * @param selectedFormName
	 */
	private void updateCacheRefernces(final HttpServletRequest request,
			final String selectedFormName, final ContainerInterface containerForSelectedForm)
	{
		CacheManager
				.addObjectToCache(request, DEConstants.CURRENT_CONTAINER_NAME, selectedFormName);
		CacheManager.addObjectToCache(request, selectedFormName, containerForSelectedForm);
	}

	/**
	 * @throws IOException
	 *
	 */
	private void sendResponse(final String responseXML, final HttpServletResponse response)
			throws IOException
	{
		final PrintWriter out = response.getWriter();
		response.setContentType("text/xml");
		out.write(responseXML);
	}

	/**
	 * @param request
	 * @return response xml string
	 * @throws IOException
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private String changeGroup(final HttpServletRequest request) throws IOException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		final List<NameValueBean> formNames = getFormNamesForGroup(request.getParameter("grpName"));
		DynamicExtensionsUtility.sortNameValueBeanListByName(formNames);
		final String xmlParentNode = "forms";
		final String xmlIdNode = "form-id";
		final String xmlNameNode = "form-name";
		return getResponseXMLString(xmlParentNode, xmlIdNode, xmlNameNode, formNames);
	}

	/**
	 * @param groupId group identifier
	 * @return list of name value beans of groups and forms in those groups.
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 */
	private List<NameValueBean> getFormNamesForGroup(final String groupId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		final ArrayList<NameValueBean> formNames = new ArrayList<NameValueBean>();

		if (groupId != null)
		{
			final EntityManagerInterface entityManager = EntityManager.getInstance();
			try
			{
				Long groupIdentifier = Long.parseLong(groupId);

				final Collection<ContainerInterface> containersCollection = entityManager
						.getAllContainersByEntityGroupId(groupIdentifier);
				formNames.addAll(getFormNamesList(containersCollection));
			}
			catch (final NumberFormatException e)
			{
				Logger.out.error("Group Id is null. Please provide correct Group Id");
			}
		}
		return formNames;
	}

	/**
	 * Will return the list of names of forms
	 * @param containersCollection collection
	 * @return form names list
	 */
	private List<NameValueBean> getFormNamesList(
			final Collection<ContainerInterface> containersCollection)
	{
		List<NameValueBean> formNames = new ArrayList<NameValueBean>();
		if (containersCollection != null)
		{
			for (final ContainerInterface container : containersCollection)
			{
				if (container != null)
				{
					NameValueBean formName = new NameValueBean(container.getCaption(), container
							.getId());
					formNames.add(formName);
				}
			}
		}
		return formNames;
	}

	/**
	 * @param xmlParentNode
	 * @param xmlNameNode
	 * @param listValues
	 * @return
	 */
	private String getResponseXMLString(final String xmlParentNode, final String xmlIdNode,
			final String xmlNameNode, final List<NameValueBean> listValues)
	{
		final StringBuffer responseXML = new StringBuffer();
		if ((xmlParentNode != null) && (xmlNameNode != null) && (listValues != null))
		{
			responseXML.append("<node>");
			final int noOfValues = listValues.size();
			for (int i = 0; i < noOfValues; i++)
			{
				NameValueBean bean = listValues.get(i);
				if (bean != null)
				{
					responseXML.append('<');
					responseXML.append(xmlParentNode);
					responseXML.append("><");
					responseXML.append(xmlIdNode);
					responseXML.append('>');
					responseXML.append(bean.getValue());
					responseXML.append("</");
					responseXML.append(xmlIdNode);
					responseXML.append("><");
					responseXML.append(xmlNameNode);
					responseXML.append('>');
					responseXML.append(bean.getName());
					responseXML.append("</");
					responseXML.append(xmlNameNode);
					responseXML.append("></");
					responseXML.append(xmlParentNode);
					responseXML.append('>');
				}
			}
			responseXML.append("</node>");

		}
		return responseXML.toString();
	}

	/**
	 * @param request
	 * @param actionForm
	 * @throws IOException
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	private String changeForm(final HttpServletRequest request) throws IOException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		final List<NameValueBean> formAttributes = getAttributesForForm(request
				.getParameter("frmName"));
		DynamicExtensionsUtility.sortNameValueBeanListByName(formAttributes);
		final String xmlParentNode = "formAttributes";
		final String xmlNodeId = "form-attribute-id";
		final String xmlNodeName = "form-attribute-name";
		final String responseXML = getResponseXMLString(xmlParentNode, xmlNodeId, xmlNodeName,
				formAttributes);
		return responseXML;
	}

	/**
	 * @param parameter
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	private List<NameValueBean> getAttributesForForm(final String formId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		final ArrayList<NameValueBean> formAttributesList = new ArrayList<NameValueBean>();
		if (formId != null)
		{
			Logger.out.debug("Fetching attributes for [" + formId + "]");
			final ContainerInterface container = DynamicExtensionsUtility
					.getContainerByIdentifier(formId);
			if (container != null)
			{
				final Collection<ControlInterface> controlCollection = container
						.getAllControlsUnderSameDisplayLabel();
				if (controlCollection != null)
				{
					for (final ControlInterface control : controlCollection)
					{
						if (control != null)
						{
							//if control contains Attribute interface object then only show on UI.
							//If control contains association objects do not show in attribute list
							AbstractAttributeInterface abstractAttribute = (AbstractAttributeInterface) control
									.getBaseAbstractAttribute();
							if ((abstractAttribute != null)
									&& (abstractAttribute instanceof AttributeInterface))
							{
								AttributeInterface attribute = (AttributeInterface) abstractAttribute;
								if (!(attribute.getAttributeTypeInformation() instanceof FileAttributeTypeInformation))
								{
									NameValueBean controlName = new NameValueBean(control
											.getCaption(), control.getId());
									formAttributesList.add(controlName);
								}
							}
						}
					}
				}
			}

		}

		return formAttributesList;
	}

	/**
	 * @param request object
	 * @param actionForm
	 * @throws IOException
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	private String breadCrumbOperation(final HttpServletRequest request) throws IOException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		String responseXML = null;
		long breadcrumbPosition = 1;
		if (request.getSession().getAttribute(DEConstants.BREAD_CRUMB_POSITION) != null)
		{
			breadcrumbPosition = Long.valueOf(request.getSession().getAttribute(
					DEConstants.BREAD_CRUMB_POSITION).toString()) + 1;
		}
		String breadCrumbURL = (String) request.getSession().getAttribute("breadCrumbURLString");
		final StringBuffer newBreadCrumbURL = new StringBuffer();
		final Stack<ContainerInterface> containerStack = (Stack<ContainerInterface>) CacheManager
				.getObjectFromCache(request, DEConstants.CONTAINER_STACK);
		if (breadCrumbURL.contains(TABLE_END_STRING))
		{
			final int position = breadCrumbURL.lastIndexOf(TABLE_END_STRING);
			breadCrumbURL = breadCrumbURL.substring(0, position);
		}
		newBreadCrumbURL.append(breadCrumbURL);

		String formName = breadCrumbURL.substring(breadCrumbURL.lastIndexOf(BREAD_CRUMB_IMAGE)
				+ (BREAD_CRUMB_IMAGE.length()), breadCrumbURL.length());
		if (formName.contains(DEConstants.HTML_SPACE))
		{
			formName = formName.replace(DEConstants.HTML_SPACE, "");
		}
		for (int i = 1; i < containerStack.size(); i++)
		{
			breadCrumbURL = breadCrumbURL.substring(0, breadCrumbURL.lastIndexOf(formName));
			breadCrumbURL = breadCrumbURL
					+ "<a onclick=\"javascript:checkForModifiedData("
					+ breadcrumbPosition
					+ ");\" style=\"color: #0000ff;font-family: arial;font-size: 12px;font-weight: normal;cursor:pointer;\">"
					+ formName + "</a>";
			breadCrumbURL = breadCrumbURL + DEConstants.HTML_SPACE + DEConstants.HTML_SPACE
					+ BREAD_CRUMB_IMAGE;
			breadCrumbURL = breadCrumbURL + DEConstants.HTML_SPACE + DEConstants.HTML_SPACE
					+ containerStack.get(i).getCaption();
			formName = containerStack.get(i).getCaption();
			breadcrumbPosition = breadcrumbPosition + 1;
		}
		breadCrumbURL = breadCrumbURL + TABLE_END_STRING;

		responseXML = breadCrumbURL;
		return responseXML;
	}

	/**
	 * @param request request object
	 * @return HTML output for clipBoard data
	 */
	private String pasteData(final HttpServletRequest request)
	{
		final StringBuffer returnString = new StringBuffer();
		String clipboardData = request.getParameter(DEConstants.CLIP_BOARD_DATA);
		clipboardData = clipboardData.replace("\r", "");
		int rwoIndex = Integer.parseInt(request.getParameter(DEConstants.INDEX));
		final String[] records = clipboardData.split(DEConstants.COMMA);
		final Set<String> errorList = new HashSet<String>();
		ContainerInterface containerInterface = null;
		try
		{
			containerInterface = DynamicExtensionsUtility.getContainerByIdentifier(request
					.getParameter(DEConstants.CONTAINER_ID));
			setContainerParameters(containerInterface, request);
			final List<ControlInterface> list = containerInterface
					.getAllControlsUnderSameDisplayLabel();

			for (final String record : records)
			{
				String[] cols = record.split("\t");
				rwoIndex = generateRow(returnString, rwoIndex, cols, errorList, containerInterface,
						list);
			}

		}
		catch (final Exception e)
		{
			Logger.out.error(e.getMessage());
		}
		finally
		{
			if (containerInterface != null)
			{
				resetContainerParameters(containerInterface);
			}
		}
		returnString.append("~ErrorList");
		if (!errorList.isEmpty())
		{
			returnString.append(errorList);
		}
		return returnString.toString();
	}

	/**
	 * setting container parameters
	 * @param containerInterface container whose parameter to be set
	 * @param request object to be set for container
	 */
	private void setContainerParameters(final ContainerInterface containerInterface,
			final HttpServletRequest request)
	{
		containerInterface.setAjaxRequest(true);
		containerInterface.setRequest(request);
	}

	/**
	 * resetting container parameter
	 * @param containerInterface container whose parameter to be set
	 */
	private void resetContainerParameters(final ContainerInterface containerInterface)
	{
		containerInterface.setAjaxRequest(false);
		containerInterface.setRequest(null);
	}

	/**
	 * @param returnString
	 * @param rwoIndex
	 * @param cols
	 * @param errorList
	 * @param containerInterface
	 * @param list
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsValidationException
	 * @throws DynamicExtensionsApplicationException
	 */
	private int generateRow(final StringBuffer returnString, int rwoIndex, final String[] cols,
			final Set<String> errorList, final ContainerInterface containerInterface,
			final List<ControlInterface> list) throws DynamicExtensionsSystemException,
			DynamicExtensionsValidationException, DynamicExtensionsApplicationException
	{
		if ((cols != null) && (cols.length > 0))
		{
			final Map<BaseAbstractAttributeInterface, Object> rowValueMap = new HashMap<BaseAbstractAttributeInterface, Object>();
			int columnCounter = 0;
			for (final ControlInterface control : list)
			{
				if ((columnCounter < cols.length)
						&& !(control.getBaseAbstractAttribute() instanceof AssociationMetadataInterface))
				{
					rowValueMap.put(control.getBaseAbstractAttribute(), cols[columnCounter++]);
				}
			}
			errorList.addAll(ValidatorUtil.validateEntity(rowValueMap, new ArrayList<String>(),
					containerInterface));
			updateMapForskipLogic(containerInterface, rowValueMap, rwoIndex);

			containerInterface.setContainerValueMap(rowValueMap);
			returnString.append(UserInterfaceiUtility.getContainerHTMLAsARow(containerInterface,
					rwoIndex, null, containerInterface));
			rwoIndex++;
		}
		return rwoIndex;
	}

	/**
	 * @param containerInterface
	 * @param rowValueMap
	 * @param rwoIndex
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	private void updateMapForskipLogic(final ContainerInterface containerInterface,
			final Map<BaseAbstractAttributeInterface, Object> rowValueMap, final int rwoIndex)
			throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		for (final ControlInterface control : containerInterface
				.getAllControlsUnderSameDisplayLabel())
		{
			if (control.getIsSkipLogic())
			{
				final String[] stringArray = {(String) rowValueMap.get(control
						.getAttibuteMetadataInterface())};
				final List<ControlInterface> targetSkipControlsList = control
						.setSkipLogicControls(stringArray);
				ControlsUtility.populateAttributeValueMapForSkipLogicAttributes(rowValueMap,
						rowValueMap, rwoIndex, true, control.getHTMLComponentName() + "_"
								+ rwoIndex, targetSkipControlsList, true);
			}
		}

	}

}
