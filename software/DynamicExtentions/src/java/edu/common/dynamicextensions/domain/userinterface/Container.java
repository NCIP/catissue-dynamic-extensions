
package edu.common.dynamicextensions.domain.userinterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import edu.common.dynamicextensions.domain.AbstractEntity;
import edu.common.dynamicextensions.domain.DynamicExtensionBaseDomainObject;
import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.skiplogic.SkipLogic;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.webui.util.UserInterfaceiUtility;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.global.ApplicationProperties;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:07 PM
 * @hibernate.class table="DYEXTN_CONTAINER"
 * @hibernate.cache  usage="read-write"
 */
public class Container extends DynamicExtensionBaseDomainObject
		implements
			Serializable,
			ContainerInterface
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8092366994778601914L;

	/**
	 * @return
	 * @hibernate.id name="id" column="IDENTIFIER" type="long"
	 * length="30" unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="DYEXTN_CONTAINER_SEQ"
	 */
	@Override
	public Long getId()
	{
		return id;
	}

	/**
	 * css for the buttons on the container.
	 */
	protected String buttonCss;
	/**
	 * Caption to be displayed on the container.
	 */
	protected String caption;
	/**
	 * css for the main table in the container.
	 */
	protected String mainTableCss;
	/**
	 * Specifies the indicator symbol that will be used to denote a required field.
	 */
	protected String requiredFieldIndicatior;
	/**
	 * Specifies the warning mesaage to be displayed in case required fields are not entered by the user.
	 */
	protected String requiredFieldWarningMessage;
	/**
	 * css of the title in the container.
	 */
	protected String titleCss;
	/**
	 * Collection of controls that are in this container.
	 */
	protected Collection<ControlInterface> controlCollection = new HashSet<ControlInterface>();
	/**
	 *
	 */
	protected Map containerValueMap = new HashMap<BaseAbstractAttributeInterface, Object>();

	protected Map<BaseAbstractAttributeInterface, Object> previousValueMap;
	/**
	 * Entity to which this container is associated.
	 */
	protected AbstractEntity abstractEntity;
	/**
	 *
	 */
	protected String mode = WebUIManagerConstants.EDIT_MODE;
	/**
	 *
	 */
	protected Boolean showAssociationControlsAsLink = false;

	/**
	 * parent of this entity, null is no parent present.
	 */
	protected ContainerInterface baseContainer = null;
	/**
	 * ContainerInterface object.
	 */
	protected ContainerInterface incontextContainer = this;
	/**
	 * boolean value.
	 */
	private Boolean addCaption = true;
	/**
	 * Collection object.
	 */
	private Collection<ContainerInterface> childContainerCollection = new HashSet<ContainerInterface>();
	/**
	 * Map object.
	 */
	public Map<String, Object> contextParameter = new HashMap<String, Object>();
	/**
	 * Is Ajax Request.
	 */
	private boolean isAjaxRequest = false;

	/**
	 * HTTP request Object.
	 */
	private HttpServletRequest request;

	/**
	 * Show required field warning message.
	 */
	private Boolean showRequiredFieldWarningMessage = true;

	public final List<String> errorList=new ArrayList<String>();

	/**
	 * @hibernate.set name="childContainerCollection" table="DYEXTN_CONTAINER"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="PARENT_CONTAINER_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.userinterface.Container"
	 * @return the childCategories
	 */
	public Collection<ContainerInterface> getChildContainerCollection()
	{
		return childContainerCollection;
	}

	public void setChildContainerCollection(
			final Collection<ContainerInterface> childContainerCollection)
	{
		this.childContainerCollection = childContainerCollection;
	}

	/**
	 * @return
	 */
	public String getMode()
	{
		return mode;
	}

	/**
	 * @param mode
	 */
	public void setMode(final String mode)
	{
		this.mode = mode;
	}

	/**
	 * @hibernate.property name="buttonCss" type="string" column="BUTTON_CSS"
	 * @return Returns the buttonCss.
	 */
	public String getButtonCss()
	{
		return buttonCss;
	}

	/**
	 * @param buttonCss The buttonCss to set.
	 */
	public void setButtonCss(final String buttonCss)
	{
		this.buttonCss = buttonCss;
	}

	/**
	 * @hibernate.property name="caption" type="string" column="CAPTION" length="800"
	 * @return Returns the caption.
	 */
	public String getCaption()
	{
		return caption;
	}

	/**
	 * @param caption The caption to set.
	 */
	public void setCaption(final String caption)
	{
		this.caption = caption;
	}

	/**
	 * @hibernate.set name="controlCollection" table="DYEXTN_CONTROL"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="CONTAINER_ID"
	 * @hibernate.cache  usage="read-write"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.userinterface.Control"
	 * @return Returns the controlCollection.
	 */
	public Collection<ControlInterface> getControlCollection()
	{
		return controlCollection;
	}

	/**
	 * @param controlCollection The controlCollection to set.
	 */
	public void setControlCollection(final Collection<ControlInterface> controlCollection)
	{
		this.controlCollection = controlCollection;
	}

	/**
	 * @hibernate.many-to-one column ="ABSTRACT_ENTITY_ID" class="edu.common.dynamicextensions.domain.AbstractEntity"
	 * cascade="save-update"
	 * @return Returns the entity.
	 */
	public AbstractEntityInterface getAbstractEntity()
	{
		return abstractEntity;
	}

	/**
	 * @hibernate.property name="mainTableCss" type="string" column="MAIN_TABLE_CSS"
	 * @return Returns the mainTableCss.
	 */
	public String getMainTableCss()
	{
		return mainTableCss;
	}

	/**
	 * @param mainTableCss The mainTableCss to set.
	 */
	public void setMainTableCss(final String mainTableCss)
	{
		this.mainTableCss = mainTableCss;
	}

	/**
	 * @hibernate.property name="requiredFieldIndicatior" type="string" column="REQUIRED_FIELD_INDICATOR"
	 * @return Returns the requiredFieldIndicatior.
	 */
	public String getRequiredFieldIndicatior()
	{
		return requiredFieldIndicatior;
	}

	/**
	 * @param requiredFieldIndicatior The requiredFieldIndicatior to set.
	 */
	public void setRequiredFieldIndicatior(final String requiredFieldIndicatior)
	{
		this.requiredFieldIndicatior = requiredFieldIndicatior;
	}

	/**
	 * @hibernate.property name="requiredFieldWarningMessage" type="string" column="REQUIRED_FIELD_WARNING_MESSAGE"
	 * @return Returns the requiredFieldWarningMessage.
	 */
	public String getRequiredFieldWarningMessage()
	{
		return requiredFieldWarningMessage;
	}

	/**
	 * @param requiredFieldWarningMessage The requiredFieldWarningMessage to set.
	 */
	public void setRequiredFieldWarningMessage(final String requiredFieldWarningMessage)
	{
		this.requiredFieldWarningMessage = requiredFieldWarningMessage;
	}

	/**
	 * @hibernate.property name="titleCss" type="string" column="TITLE_CSS"
	 * @return Returns the titleCss.
	 */
	public String getTitleCss()
	{
		return titleCss;
	}

	/**
	 * @param titleCss The titleCss to set.
	 */
	public void setTitleCss(final String titleCss)
	{
		this.titleCss = titleCss;
	}

	/**
	 *
	 */
	public void addControl(final ControlInterface controlInterface)
	{
		if (controlCollection == null)
		{
			controlCollection = new HashSet<ControlInterface>();
		}

		controlCollection.add(controlInterface);
		controlInterface.setParentContainer(this);
	}

	/**
	 *
	 */
	public void setAbstractEntity(final AbstractEntityInterface abstractEntityInterface)
	{
		abstractEntity = (AbstractEntity) abstractEntityInterface;
	}

	/**
	 *
	 * @param sequenceNumber
	 * @return
	 */
	public ControlInterface getControlInterfaceBySequenceNumber(final String sequenceNumber)
	{
		boolean found = false;
		ControlInterface control = null;
		final Collection<ControlInterface> controls = getControlCollection();
		if (controls != null)
		{
			final Iterator<ControlInterface> controlsIter = controls.iterator();
			while (controlsIter.hasNext())
			{
				control = controlsIter.next();
				if (control.getSequenceNumber().equals(Integer.valueOf(sequenceNumber)))
				{
					found = true;
					break;
				}
			}
		}

		return found ? control : null;
	}

	/**
	 *
	 */
	public void removeControl(final ControlInterface control)
	{
		if (control != null && controlCollection != null && controlCollection.contains(control))
		{
			controlCollection.remove(control);
		}
	}

	/**
	 * remove all controls from the controls collection
	 */
	public void removeAllControls()
	{
		if (controlCollection != null)
		{
			controlCollection.clear();
		}
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.EntityInterface#getAllAttributes()
	 */
	public List<ControlInterface> getAllControls()
	{
		final List<ControlInterface> controls = new ArrayList<ControlInterface>(
				getControlCollection());
		Collections.sort(controls);

		ContainerInterface baseContainer = this.baseContainer;
		while (baseContainer != null)
		{
			List<ControlInterface> baseControls = new ArrayList(baseContainer
					.getControlCollection());
			Collections.sort(baseControls);
			Collections.reverse(baseControls);

			controls.addAll(baseControls);

			baseContainer.setIncontextContainer(this);
			baseContainer = baseContainer.getBaseContainer();
		}

		Collections.reverse(controls);

		return controls;
	}

	/**
	 * @return
	 */
	public List<ControlInterface> getAllControlsUnderSameDisplayLabel()
	{
		final List<ControlInterface> controls = new ArrayList<ControlInterface>(
				getControlCollection());

		for (final ContainerInterface container : childContainerCollection)
		{
			controls.addAll(container.getAllControls());
		}

		ContainerInterface baseContainer = this.baseContainer;
		while (baseContainer != null)
		{
			List<ControlInterface> baseControls = new ArrayList(baseContainer
					.getControlCollection());
			Collections.reverse(baseControls);

			controls.addAll(baseControls);

			baseContainer.setIncontextContainer(this);
			baseContainer = baseContainer.getBaseContainer();
		}

		Collections.sort(controls);
		Collections.reverse(controls);

		return controls;
	}

	/**
	 * @return return the HTML string for this type of a object
	 * @throws DynamicExtensionsSystemException
	 */
	public String generateContainerHTML(final String caption, final String dataEntryOperation)
			throws DynamicExtensionsSystemException
	{
		final StringBuffer containerHTML = new StringBuffer(128);

		containerHTML
				.append("<table summary='' cellpadding='3' cellspacing='0' align='center' width='100%'>");
		String allControlHTML=generateControlsHTML(caption, dataEntryOperation, this);
		if (getMode() != null && getMode().equalsIgnoreCase(WebUIManagerConstants.EDIT_MODE)
				&& isShowRequiredFieldWarningMessage())
		{
			if(!errorList.isEmpty())
			{
                containerHTML
                        .append("<tr><td  class='formMessage' colspan='3'><table width='100%' height='30'  border='0' cellpadding='4' cellspacing='4' class='td_color_FFFFCC'>");
				for (String error : errorList)
				{
					containerHTML.append("<tr><th align='center' class='font_bl_nor'>");
					containerHTML.append(error);
					containerHTML.append("<br/></th></tr>");
				}
				containerHTML.append("</table></td></tr>");
			}
			containerHTML.append("<tr><td class='formMessage' colspan='3'><span class='font_red'>");
			containerHTML.append(getRequiredFieldIndicatior());
			containerHTML.append("&nbsp;</span><span class='font_gr_s'>");
			containerHTML.append(getRequiredFieldWarningMessage());
			containerHTML.append("</span></td></tr><tr><td height='5'/></tr>");
		}
		else
		{
			//Changed by : Kunal
			//Reviewed by: Sujay
			//Container hierarchy can be n level
			//So, mode of the n containers in the hierarchy need to be same.

			ContainerInterface tempContainer = baseContainer;
			while (tempContainer != null)
			{
				tempContainer.setMode(mode);
				tempContainer = tempContainer.getBaseContainer();
			}
		}

		containerHTML.append(allControlHTML);
		containerHTML.append("</table>");

		return containerHTML.toString();
	}

	private boolean doAllControlsHaveHideAction()
	{
		boolean isAlltargetControls = true;
		final List<ControlInterface> controls = getAllControlsUnderSameDisplayLabel();
		for (final ControlInterface control : controls)
		{
			if (!control.getIsSkipLogicTargetControl() || !control.getIsHidden())
			{
				isAlltargetControls = false;
				break;
			}
		}
		return isAlltargetControls;
	}

	/**
	 * @return return the HTML string for this type of a object
	 * @throws DynamicExtensionsSystemException
	 */
	public String generateControlsHTML(final String caption, final String dataEntryOperation,
			final ContainerInterface container) throws DynamicExtensionsSystemException
	{
		final StringBuffer controlHTML = new StringBuffer(108);
		final List<Object> values = new ArrayList<Object>();
		final List<ControlInterface> controls = getAllControlsUnderSameDisplayLabel(); //UnderSameDisplayLabel();
		int lastRow = 0;
		int cntr = 0;
		// This is to avoid over-riding values set by skip logic.
		for (final ControlInterface control : controls)
		{
			control.setDataEntryOperation(dataEntryOperation);
			final Object value = containerValueMap.get(control.getBaseAbstractAttribute());
			if(containerValueMap.get(control.getBaseAbstractAttribute())==null && control.getBaseAbstractAttribute() instanceof AttributeMetadataInterface)
			{
				containerValueMap.put(control.getBaseAbstractAttribute(),((AttributeMetadataInterface)control.getBaseAbstractAttribute()).getDefaultValue((Date)this.getContextParameter(Constants.ENCOUNTER_DATE)));
			}
			control.setValue(value);
		}
		SkipLogic skipLogic = EntityCache.getInstance().getSkipLogicByContainerIdentifier(id);
		if (skipLogic != null)
		{
			skipLogic.evaluateSkipLogic(this, container.getContainerValueMap());
		}

		// This is the case of Single Line Display. In this case the Skip Logic is associated with child container.
		if (!getChildContainerCollection().isEmpty())
		{
			for (ContainerInterface childContainer : getChildContainerCollection())
			{
				SkipLogic childSkipLogic = EntityCache.getInstance()
						.getSkipLogicByContainerIdentifier(childContainer.getId());
				if (childSkipLogic != null)
				{
					childSkipLogic.evaluateSkipLogic(childContainer, container.getContainerValueMap());
				}
			}
		}
		addCaptionHTML(controlHTML, caption, container, values);
		for (final ControlInterface control : controls)
		{
			if (lastRow != control.getSequenceNumber())
			{
				if (cntr != 0)
				{
					controlHTML.append("</table></td></tr><tr><td height='7'></td></tr>");
				}
				controlHTML.append("<tr valign='center' ");
				if (control.getIsSkipLogicTargetControl())
				{
					controlHTML.append("id='" + control.getHTMLComponentName() + "_row_div' name='"
							+ control.getHTMLComponentName() + "_row_div'");

				}
				if (control.getIsHidden() != null && control.getIsHidden())
				{
					controlHTML.append(" style='display:none'");
				}
				else
				{
					controlHTML.append(" style='display:row'");
				}
				controlHTML.append('>');
				if (control.getIsSkipLogicTargetControl())
				{
					controlHTML
							.append("<input type='hidden' name='skipLogicHideControls' id='skipLogicHideControls' value = '"
									+ control.getHTMLComponentName() + "_row_div' />");
				}
			}
			String controlHTMLs = control.generateHTML(container);
			controlHTML.append(controlHTMLs);
			cntr++;
			lastRow = control.getSequenceNumber();
			errorList.addAll(control.getErrorList());
		}
		controlHTML.append("</td></tr>");
		showAssociationControlsAsLink = false;

		return controlHTML.toString();
	}

	/**
	 * @param captionHTML
	 * @param caption in the format -- NewCaption:Main ContainerId
	 * @throws DynamicExtensionsSystemException
	 */
	private void addCaptionHTML(final StringBuffer captionHTML, final String caption,
			final ContainerInterface container, final List<Object> values)
			throws DynamicExtensionsSystemException
	{
		// Check added for CSD project to ensure that HTML is generated for mock
		// container with NULL id.
		boolean isIdNull = false;
		if (id == null)
		{
			isIdNull = true;
			id = -1L;
		}

		//check if Id in caption matches current id - if yes then it is main form, so replace caption
		if (caption == null || !caption.endsWith(id.toString()))
		{
			if (addCaption) // for subform
			{
				captionHTML.append("<tr ");
				addDisplayOptionForRow(container, values, captionHTML, "_caption");
				captionHTML.append("<td class='td_color_6e81a6' colspan='100' align='left'>");
				captionHTML.append(((AbstractEntity) getAbstractEntity())
						.getCapitalizedName(DynamicExtensionsUtility
								.replaceHTMLSpecialCharacters(getCaption())));
				captionHTML.append("<tr ");
				addDisplayOptionForRow(container, values, captionHTML, "_emptyrow");
				captionHTML.append("<td height='5'></td></tr>");
			}
		}
		else
		{ // for Main form
			captionHTML.append("<tr><td class='td_color_6e81a6_big' colspan='100' align='left'>");
			captionHTML.append(caption
					.substring(0, (caption.length() - 1 - id.toString().length())));
			captionHTML.append("<tr><td height='5'></td></tr>");
		}

		if (isIdNull)
		{
			id = null;
		}
	}

	/**
	 *
	 * @param container
	 * @param values
	 * @param captionHTML
	 * @throws DynamicExtensionsSystemException
	 */
	private void addDisplayOptionForRow(final ContainerInterface container,
			final List<Object> values, final StringBuffer captionHTML, final String identifier)
			throws DynamicExtensionsSystemException
	{
		//if (isAllControlsSkipLogicTargetControlsForShowHide())
		{
			final List<ControlInterface> controls = getAllControlsUnderSameDisplayLabel();
			if (!controls.isEmpty())
			{
				final ControlInterface control = controls.get(0);
				captionHTML.append("id='" + control.getHTMLComponentName() + identifier
						+ "_container_div' name='" + control.getHTMLComponentName() + identifier
						+ "_container_div'");

				//Object value = null;
				values.clear();

				/*ControlsUtility.getAttributeValueForSkipLogicAttributesFromValueMap(container
						.getContainerValueMap(), container.getContainerValueMap(), control
						.getSourceSkipControl().getBaseAbstractAttribute(), false, values, Integer
						.valueOf(-1), Integer.valueOf(-1));*/
				/*if (!values.isEmpty())
				{
					value = values.get(0);
				}
				control.getSourceSkipControl().setValue(value);
				control.getSourceSkipControl().setSkipLogicControls();*/

				if (doAllControlsHaveHideAction())
				{
					captionHTML.append(" style='display:none' >");
				}
				else
				{
					captionHTML.append(" style='display:row' >");
				}
				captionHTML
						.append("<input type='hidden' name='skipLogicHideControls' id='skipLogicHideControls' value = '"
								+ control.getHTMLComponentName()
								+ identifier
								+ "_container_div' />");
			}
		}
		/*else
		{
			captionHTML.append(" style='display:row' >");
		}*/
	}

	/**
	 * @param valueMaps List object.
	 * @param dataEntryOperation String value.
	 * @param container Container object.
	 * @param isPasteEnable boolean value.
	 * @return String value.
	 * @throws DynamicExtensionsSystemException throw exception.
	 * @see edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface
	 * #generateControlsHTMLAsGrid(java.util.List, java.lang.String,
	 * edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface, boolean)
	 */
	public String generateControlsHTMLAsGrid(
			final List<Map<BaseAbstractAttributeInterface, Object>> valueMaps,
			final String dataEntryOperation, final ContainerInterface container,
			boolean isPasteEnable,final List<String> errorList) throws DynamicExtensionsSystemException
	{
		return UserInterfaceiUtility.generateHTMLforGrid(this, valueMaps, dataEntryOperation,
				container, isPasteEnable,errorList);
	}

	/**
	 * @return Map.
	 */
	public Map<BaseAbstractAttributeInterface, Object> getContainerValueMap()
	{
		return containerValueMap;
	}

	/**
	 * @param containerValueMap Map object.
	 */
	public void setContainerValueMap(
			final Map<BaseAbstractAttributeInterface, Object> containerValueMap)
	{
		this.containerValueMap = containerValueMap;
	}

	/**
	 *
	 * @return
	 */
	public Boolean getShowAssociationControlsAsLink()
	{
		return showAssociationControlsAsLink;
	}

	/**
	 *
	 * @param showAssociationControlsAsLink
	 */
	public void setShowAssociationControlsAsLink(final Boolean showAssociationControlsAsLink)
	{
		this.showAssociationControlsAsLink = showAssociationControlsAsLink;
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.userinterface.ContainmentAssociationControlInterface#generateLinkHTML()
	 */
	public String generateLink(final ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		String details = "";
		final boolean isDataPresent = UserInterfaceiUtility.isDataPresent(container
				.getContainerValueMap());
		if (isDataPresent)
		{
			if (mode.equals(WebUIManagerConstants.EDIT_MODE))
			{
				details = ApplicationProperties.getValue("eav.att.EditDetails");
			}
			else if (mode.equals(WebUIManagerConstants.VIEW_MODE))
			{
				details = ApplicationProperties.getValue("eav.att.ViewDetails");
			}
		}
		else
		{
			if (mode.equals(WebUIManagerConstants.EDIT_MODE))
			{
				details = ApplicationProperties.getValue("eav.att.EnterDetails");
			}
			else if (mode.equals(WebUIManagerConstants.VIEW_MODE))
			{
				details = ApplicationProperties.getValue("eav.att.NoDataToView");
			}
		}

		final StringBuffer linkHTML = new StringBuffer(244);
		linkHTML
				.append("<img src='images/de/ic_det.gif' alt='Details' width='12' height='12' hspace='3' border='0' align='absmiddle'><a href='#' style='cursor:hand' class='set1' id='lnkDetails27' onclick='showChildContainerInsertDataPage(");
		linkHTML.append(container.getId());
		linkHTML.append(",this)'>");
		linkHTML.append(details);
		linkHTML.append("</a><tr><td></td></tr>");

		return "<table>" + linkHTML.toString();
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.EntityInterface#getParentEntity()
	 * @hibernate.many-to-one column="BASE_CONTAINER_ID" class="edu.common.dynamicextensions.domain.userinterface.Container" constrained="true"
	 *                        cascade="save-update"
	 */
	public ContainerInterface getBaseContainer()
	{
		return baseContainer;
	}

	/**
	 *
	 * @param baseContainer
	 */
	public void setBaseContainer(final ContainerInterface baseContainer)
	{
		this.baseContainer = baseContainer;
	}

	/**
	 * @return the incontextContainer
	 */
	public ContainerInterface getIncontextContainer()
	{
		return incontextContainer;
	}

	/**
	 * @param incontextContainer the incontextContainer to set
	 */
	public void setIncontextContainer(final ContainerInterface incontextContainer)
	{
		this.incontextContainer = incontextContainer;
	}

	/**
	 * @hibernate.property name="addCaption" type="boolean" column="ADD_CAPTION"
	 * @return Returns the addCaption.
	 */
	public Boolean getAddCaption()
	{
		return addCaption;
	}

	public void setAddCaption(final Boolean addCaption)
	{
		this.addCaption = addCaption;
	}

	/**
	 * @param xPosition
	 * @param yPosition
	 * @return
	 */
	public ControlInterface getControlByPosition(final Integer xPosition, final Integer yPosition)
	{
		ControlInterface control = null;

		for (final ControlInterface cntrl : controlCollection)
		{
			if (cntrl.getSequenceNumber() != null && cntrl.getSequenceNumber().equals(xPosition)
					&& cntrl.getSequenceNumber() != null && cntrl.getYPosition().equals(yPosition))
			{
				control = cntrl;
			}
		}

		return control;
	}

	public boolean isAjaxRequest()
	{
		return isAjaxRequest;
	}

	public void setAjaxRequest(final boolean isAjaxRequest)
	{
		this.isAjaxRequest = isAjaxRequest;
	}

	/**
	 * @return the request
	 */
	public HttpServletRequest getRequest()
	{
		return request;
	}

	/**
	 * @param request the request to set
	 */
	public void setRequest(final HttpServletRequest request)
	{
		this.request = request;
	}

	/**
	 * @return the showRequiredFieldWarningMessage
	 */
	public Boolean isShowRequiredFieldWarningMessage()
	{
		return showRequiredFieldWarningMessage;
	}

	/**
	 * @param showRequiredFieldWarningMessage the showRequiredFieldWarningMessage to set
	 */
	public void setShowRequiredFieldWarningMessage(final Boolean showRequiredFieldWarningMessage)
	{
		this.showRequiredFieldWarningMessage = showRequiredFieldWarningMessage;
	}

	/**
	 * @return Map object.
	 */
	public Map<BaseAbstractAttributeInterface, Object> getPreviousValueMap()
	{
		return previousValueMap;
	}

	/**
	 * @param previousValueMap Map object.
	 */
	public void setPreviousValueMap(
			final Map<BaseAbstractAttributeInterface, Object> previousValueMap)
	{
		this.previousValueMap = previousValueMap;
	}

	/**
	 * @param key String value.
	 * @return Object based on key.
	 */
	public Object getContextParameter(String key)
	{
		return contextParameter.get(key);
	}

	/**
	 This parameter will be used to keep context information
	 * for container.
	 * @param contextParameter
	 */
	public void setContextParameter(Map<String, Object> contextParameter)
	{
		this.contextParameter = contextParameter;
	}

	/**
	 *
	 * @return boolean value
	 */
	public boolean getIsSourceCalculatedAttributes()
	{
		boolean hasSourceCalculatedAttributes = false;
		final AbstractEntityInterface abstractEntityInterface = getAbstractEntity();
		if (abstractEntityInterface instanceof CategoryEntityInterface)
		{
			final CategoryEntityInterface categoryEntityInterface = (CategoryEntityInterface) abstractEntityInterface;
			for (final CategoryAttributeInterface categoryAttributeInterface : categoryEntityInterface
					.getCategoryAttributeCollection())
			{
				if (categoryAttributeInterface.getIsSourceForCalculatedAttribute() != null
						&& categoryAttributeInterface.getIsSourceForCalculatedAttribute())
				{
					hasSourceCalculatedAttributes = true;
					break;
				}
			}
		}
		return hasSourceCalculatedAttributes;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface#getControlById(java.lang.Long)
	 */
	public ControlInterface getControlById(Long id)
	{
		ControlInterface controlInterface = null;
		for (ControlInterface control : getAllControlsUnderSameDisplayLabel())
		{
			if (controlInterface == null && control instanceof AbstractContainmentControlInterface)
			{
				if (control.getId().equals(id))
				{
					controlInterface = control;
				}
				else
				{
					controlInterface = ((AbstractContainmentControlInterface) control)
							.getContainer().getControlById(id);
				}
			}
			else
			{
				if (control.getId().equals(id))
				{
					controlInterface = control;
					break;
				}
			}
		}
		return controlInterface;
	}

	/**
	 * Gets the parent container for given container.
	 * @param container the container
	 * @return the parent container
	 */
	public ContainerInterface getParentContainer(ContainerInterface container)
	{
		ContainerInterface parentContainer = null;
		CategoryEntityInterface categoryEntity = (CategoryEntityInterface) container
				.getAbstractEntity();
		if (categoryEntity.getTreeParentCategoryEntity() != null)
		{
			parentContainer = (ContainerInterface) categoryEntity.getTreeParentCategoryEntity()
					.getContainerCollection().iterator().next();
		}
		return parentContainer;
	}
	public List<String> getErrorList()
	{
		return errorList;
	}
}