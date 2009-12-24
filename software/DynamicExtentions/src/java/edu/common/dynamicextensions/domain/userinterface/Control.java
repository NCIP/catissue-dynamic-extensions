
package edu.common.dynamicextensions.domain.userinterface;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.common.dynamicextensions.domain.BaseAbstractAttribute;
import edu.common.dynamicextensions.domain.DynamicExtensionBaseDomainObject;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.DataElementInterface;
import edu.common.dynamicextensions.domaininterface.FormControlNotesInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.SkipLogicAttributeInterface;
import edu.common.dynamicextensions.domaininterface.UserDefinedDEInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.ui.webui.util.UserInterfaceiUtility;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.util.logger.Logger;

/**
 * @version 1.0
 * @created 28-Sep-2006 12:20:07 PM
 * @hibernate.class table="DYEXTN_CONTROL"
 */
public abstract class Control extends DynamicExtensionBaseDomainObject
		implements
			Serializable,
			ControlInterface
{

	/**
	 * @return
	 * @hibernate.id name="id" column="IDENTIFIER" type="long"
	 * length="30" unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="DYEXTN_CONTROL_SEQ"
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * The caption of the control.
	 */
	protected String caption = null;

	/**
	 * The css class that is to be used for this control.
	 */
	protected String cssClass = null;

	/**
	 * whether this attribute should be displayed on screen.
	 */
	protected Boolean isHidden = null;

	/**
	 * Decides whether the control should be disabled or not
	 */
	protected Boolean isReadOnly = false;
	/**
	 * Decides whether the control should be disabled or not
	 */
	protected Boolean isSkipLogicReadOnly = false;
	/**
	 * Decides whether the control should be disabled or not
	 */
	protected Boolean isSelectiveReadOnly = false;
	/**
	 * Decides whether the control should be autocalculated
	 */
	protected Boolean isCalculated = false;
	/**
	 * Decides whether the control is Source For CalculatedAttribute.
	 */
	protected Boolean isSourceForCalculatedAttribute = false;
	/**
	 * Decides whether the control should use skip logic
	 */
	protected Boolean isSkipLogic = false;
	/**
	 * Decides whether the control should use skip logic
	 */
	protected Boolean isSkipLogicTargetControl = false;
	/**
	 * Decides whether the control should use skip logic
	 */
	protected Boolean isSkipLogicShowHideTargetControl = false;
	/**
	 * Decides whether the control should use skip logic
	 */
	protected Boolean isShowHide = false;
	/**
	 * Decides whether the control should use skip logic
	 */
	protected Boolean isSkipLogicLoadPermValues = false;
	/**
	 * Decides whether the control should use skip logic
	 */
	protected Boolean isSkipLogicDefaultValue = false;
	/**
	 *
	 */
	protected ControlInterface sourceSkipControl = null;
	/**
	 *
	 */
	protected String dataEntryOperation = null;

	/**
	 * Name of the control.
	 */
	protected String name = null;

	/**
	 * Sequence number of the control.This governs in which order it will be shown on the UI.
	 */
	protected Integer sequenceNumber = 1;

	/**
	 * Sequence number of the control.This governs in which order it will be shown on the UI.
	 */
	protected Integer yPosition = 1;

	/**
	 * Tool tip message for the control.
	 */
	protected String tooltip = null;

	/**
	 * Value to be shown in the control
	 */
	protected Object value = null;

	/**
	 * Attribute to which this control is associated.
	 */
	protected BaseAbstractAttribute baseAbstractAttribute;
	/**
	 *
	 */
	protected Boolean sequenceNumberChanged = false;
	/**
	 *
	 */
	protected Container parentContainer;

	/**
	 *
	 */
	protected boolean isSubControl = false;

	/**
	 *
	 */
	protected String heading;

	/**
	 * Decides whether the control should be disabled or not
	 */
	protected Boolean showLabel = true;

	/**
	 *
	 * @return
	 */
	public abstract boolean getIsEnumeratedControl();

	/**
	 * @hibernate.property name="showLabel" type="boolean" column="SHOW_LABEL"
	 * @return Returns the isHidden.
	 */
	public Boolean getShowLabel()
	{
		return showLabel;
	}

	public void setShowLabel(Boolean showLabel)
	{
		this.showLabel = showLabel;
	}

	/**
	 *
	 */
	protected List<FormControlNotesInterface> formNotes = new LinkedList<FormControlNotesInterface>();

	/**
	 * @hibernate.property name="caption" type="string" column="CAPTION" length="800"
	 * @return Returns the caption.
	 */
	public String getCaption()
	{
		return caption;
	}

	/**
	 * @hibernate.property name="isCalculated" type="boolean" column="IS_CALCULATED"
	 * @return Returns the isHidden.
	 */
	public Boolean getIsCalculated()
	{
		return isCalculated;
	}

	/**
	 *
	 * @param isCalculated
	 */
	public void setIsCalculated(Boolean isCalculated)
	{
		this.isCalculated = isCalculated;
	}
	/**
	 * @hibernate.property name="isSourceForCalculatedAttribute" type="boolean" column="IS_SRC_FOR_CAL_ATTR"
	 * @return Returns the isHidden.
	 */
	public Boolean getIsSourceForCalculatedAttribute()
	{
		return isSourceForCalculatedAttribute;
	}
	/**
	 *
	 * @param isSourceForCalculatedAttribute
	 */
	public void setIsSourceForCalculatedAttribute(
			Boolean isSourceForCalculatedAttribute)
	{
		this.isSourceForCalculatedAttribute = isSourceForCalculatedAttribute;
	}
	/**
	 * @param caption The caption to set.
	 */
	public void setCaption(String caption)
	{
		this.caption = caption;
	}

	/**
	 * @hibernate.property name="cssClass" type="string" column="CSS_CLASS"
	 * @return Returns the cssClass.
	 */
	public String getCssClass()
	{
		return cssClass;
	}

	/**
	 * @param cssClass The cssClass to set.
	 */
	public void setCssClass(String cssClass)
	{
		this.cssClass = cssClass;
	}

	/**
	 * @hibernate.property name="isHidden" type="boolean" column="HIDDEN"
	 * @return Returns the isHidden.
	 */
	public Boolean getIsHidden()
	{
		return isHidden;
	}

	/**
	 * @param isHidden The isHidden to set.
	 */
	public void setIsHidden(Boolean isHidden)
	{
		this.isHidden = isHidden;
	}

	/**
	 * @hibernate.property name="name" type="string" column="NAME"
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @hibernate.property name="sequenceNumber" type="integer" column="SEQUENCE_NUMBER"
	 * @return Returns the sequenceNumber.
	 */
	public Integer getSequenceNumber()
	{
		return sequenceNumber;
	}

	/**
	 * @param sequenceNumber The sequenceNumber to set.
	 */
	public void setSequenceNumber(Integer sequenceNumber)
	{
		this.sequenceNumber = sequenceNumber;
	}

	/**
	 * @hibernate.property name="tooltip" type="string" column="TOOLTIP"
	 * @return Returns the tooltip.
	 */
	public String getTooltip()
	{
		return tooltip;
	}

	/**
	 * @param tooltip The tooltip to set.
	 */
	public void setTooltip(String tooltip)
	{
		this.tooltip = tooltip;
	}

	/**
	 * @return return the HTML string for this type of a object
	 * @throws DynamicExtensionsSystemException  exception
	 */
	public final String generateHTML(ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		String htmlString = "";

		String innerHTML = "";
		if ((getParentContainer().getMode() != null)
				&& getParentContainer().getMode().equalsIgnoreCase(WebUIManagerConstants.VIEW_MODE))
		{
			innerHTML = generateViewModeHTML(container);
		}
		else
		{
			innerHTML = generateEditModeHTML(container);
		}

		if (isSubControl)
		{
			htmlString = innerHTML;
		}
		else
		{
			if (baseAbstractAttribute != null)
			{
				htmlString = getControlHTML(innerHTML);
			}
			else
			{
				htmlString = htmlString.concat("<td class='formRequiredLabel_withoutBorder'>");
				htmlString = htmlString.concat(innerHTML);
				htmlString = htmlString.concat("</td>");
			}
		}

		isSubControl = false;

		return htmlString;
	}

	protected String getControlHTML(String htmlString) throws DynamicExtensionsSystemException
	{
		boolean isControlRequired = UserInterfaceiUtility.isControlRequired(this);
		StringBuffer controlHTML = new StringBuffer();
		// For category attribute controls, if heading and/or notes are specified, then
		// render the UI that displays heading followed by notes for particular
		// category attribute controls.
		if ((heading != null) || ((getFormNotes() != null) && (getFormNotes().size() != 0)))
		{
			controlHTML.append("<tr><td width='100%' colspan='3' align='left'>");

			if ((heading != null) && (heading.length() != 0))
			{
				controlHTML.append("<div style='width:100%' class='td_color_6e81a6'>"
						+ getHeading() + "</div>");
			}

			if ((getFormNotes() != null) && (getFormNotes().size() != 0))
			{

				for (FormControlNotesInterface fcNote : getFormNotes())
				{
					controlHTML.append("<div style='width:100%' class='notes'>"
							+ DynamicExtensionsUtility
									.replaceHTMLSpecialCharacters(fcNote
											.getNote()) + "</div>");
				}
			}

			controlHTML.append("</td></tr>");
		}
		if ((yPosition != null) && (yPosition <= 1))
		{

			controlHTML.append("<td class='formRequiredNotice_withoutBorder' width='2%'>");

			if (isControlRequired)
			{
				controlHTML.append("<span class='font_red'>");
				controlHTML.append(getParentContainer().getRequiredFieldIndicatior());
				controlHTML.append("&nbsp; </span> </td> ");
			}
			else
			{
				controlHTML.append("&nbsp; </td> ");
			}
			//			if (this instanceof ComboBox)
			//			{
			//				stringBuffer.append("<br/>");
			//			}

		}

		controlHTML.append("<td class='formRequiredLabel_withoutBorder'>");
		if ((this.showLabel != null) && this.showLabel)
		{
			controlHTML.append(((BaseAbstractAttribute) this.getBaseAbstractAttribute())
					.getCapitalizedName(DynamicExtensionsUtility
							.replaceHTMLSpecialCharacters(this.getCaption())));
		}
		controlHTML.append("</td><td class='formField_withoutBorder' valign='center'>");

		if (getYPosition() <= 1)
		{
			controlHTML.append("<table>");
		}
		controlHTML.append("<td class='formRequiredLabel_withoutBorder'>");
		controlHTML.append(htmlString);
		controlHTML.append("</td>");
		return controlHTML.toString();
	}

	/**
	 * @return String html
	 * @throws DynamicExtensionsSystemException exception
	 */
	protected abstract String generateViewModeHTML(ContainerInterface container)
			throws DynamicExtensionsSystemException;

	/**
	 * @return String html
	 * @throws DynamicExtensionsSystemException exception
	 */
	protected abstract String generateEditModeHTML(ContainerInterface container)
			throws DynamicExtensionsSystemException;

	/**
	 * @return
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 *
	 * @return
	 */
	public abstract List<String> getValueAsStrings();

	/**
	 *
	 * @return
	 */
	public abstract void setValueAsStrings(List<String> listOfValues);

	/**
	 * @param value
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

	/**
	 * @return String
	 * @throws DynamicExtensionsSystemException
	 */
	public String getHTMLComponentName() throws DynamicExtensionsSystemException
	{
		StringBuffer htmlComponentName = new StringBuffer();
		ContainerInterface parentContainer = getParentContainer();
		if (getSequenceNumber() != null)
		{
			htmlComponentName.append("Control_" + parentContainer.getIncontextContainer().getId()
					+ "_" + parentContainer.getId() + "_" + getSequenceNumber());
			if (yPosition != null)
			{
				htmlComponentName.append("_" + getYPosition());
			}
		}

		return htmlComponentName.toString();
	}

	/**
	 *
	 */
	public Boolean getSequenceNumberChanged()
	{
		return sequenceNumberChanged;
	}

	/**
	 *
	 * @param sequenceNumberChanged
	 */
	public void setSequenceNumberChanged(Boolean sequenceNumberChanged)
	{
		this.sequenceNumberChanged = sequenceNumberChanged;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object object)
	{
		Control control = (Control) object;
		Integer thisSequenceNumber = sequenceNumber;
		Integer otherSequenceNumber = control.getSequenceNumber();
		if (thisSequenceNumber.equals(otherSequenceNumber) && (yPosition != null)
				&& (control.yPosition != null))
		{
			return control.yPosition.compareTo(yPosition);
		}

		return otherSequenceNumber.compareTo(thisSequenceNumber);
	}

	/**
	 *@hibernate.many-to-one column="CONTAINER_ID" class="edu.common.dynamicextensions.domain.userinterface.Container" constrained="true"
	 * @return parentContainer
	 */
	public Container getParentContainer()
	{
		return parentContainer;
	}

	/**
	 * @param parentContainer parentContainer
	 */
	public void setParentContainer(Container parentContainer)
	{
		this.parentContainer = parentContainer;
	}

	/**
	 * This method returns boolean indicating whether the control is a part of Sub-Form or not.
	 * true value indicates the control is a part of Sub-Form, false value indicates its not.
	 * @return the isSubControl
	 */
	public boolean getIsSubControl()
	{
		return isSubControl;
	}

	/**
	 * This method sets whether the control is a part of Sub-Form or not.
	 * true value indicates the control is a part of Sub-Form, false value indicates its not.
	 * @param isSubControl the isSubControl to set
	 */
	public void setIsSubControl(boolean isSubControl)
	{
		this.isSubControl = isSubControl;
	}

	/**
	 * @hibernate.many-to-one  cascade="save-update" column="BASE_ABST_ATR_ID" class="edu.common.dynamicextensions.domain.BaseAbstractAttribute" constrained="true" outer-join="false"
	 */
	public BaseAbstractAttributeInterface getBaseAbstractAttribute()
	{
		return baseAbstractAttribute;
	}

	/**
	 *
	 * @param baseAbstractAttribute
	 */
	public void setBaseAbstractAttribute(BaseAbstractAttributeInterface baseAbstractAttribute)
	{
		this.baseAbstractAttribute = (BaseAbstractAttribute) baseAbstractAttribute;
	}

	public AttributeMetadataInterface getAttibuteMetadataInterface()
	{
		AttributeMetadataInterface attributeMetadata = null;

		if (baseAbstractAttribute instanceof AttributeMetadataInterface)
		{
			attributeMetadata = (AttributeMetadataInterface) baseAbstractAttribute;
		}

		return attributeMetadata;
	}

	/**
	 * @hibernate.property name="isReadOnly" type="boolean" column="READ_ONLY"
	 * @return
	 */
	public Boolean getIsReadOnly()
	{
		return isReadOnly;
	}

	/**
	 *
	 * @param isReadOnly
	 */
	public void setIsReadOnly(Boolean isReadOnly)
	{
		this.isReadOnly = isReadOnly;
	}

	/**
	 *
	 */
	public Boolean getIsSkipLogicReadOnly()
	{
		return isSkipLogicReadOnly;
	}

	/**
	 *
	 * @param isReadOnly
	 */
	public void setIsSkipLogicReadOnly(Boolean isSkipLogicReadOnly)
	{
		this.isSkipLogicReadOnly = isSkipLogicReadOnly;
	}

	/**
	 * @hibernate.property name="isSkipLogic" type="boolean" column="SKIP_LOGIC"
	 * @return
	 */
	public Boolean getIsSkipLogic()
	{
		return isSkipLogic;
	}

	/**
	 *
	 * @param isSkipLogic
	 */
	public void setIsSkipLogic(Boolean isSkipLogic)
	{
		this.isSkipLogic = isSkipLogic;
	}

	/**
	 * @hibernate.property name="heading" type="string" column="HEADING"
	 * @return Returns the caption.
	 */
	public String getHeading()
	{
		return heading;
	}

	/**
	 * @param heading the heading to set
	 */
	public void setHeading(String heading)
	{
		this.heading = heading;
	}

	/**
	 * This method Returns the formNotes.
	 * @hibernate.list name="formNotes" table="DYEXTN_FORM_CTRL_NOTES"
	 * cascade="all-delete-orphan" inverse="false" lazy="false"
	 * @hibernate.collection-key column="FORM_CONTROL_ID"
	 * @hibernate.collection-index column="INSERTION_ORDER" type="long"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.FormControlNotes"
	 * @return Returns the formNotes.
	 */
	public List<FormControlNotesInterface> getFormNotes()
	{
		return formNotes;
	}

	/**
	 * @param formNotes the formNotes to set
	 */
	public void setFormNotes(List<FormControlNotesInterface> formNotes)
	{
		this.formNotes = formNotes;
	}

	/**
	 * @param xPosition
	 * @param yPosition
	 */
	public void setControlPosition(int xPosition, int yPosition)
	{
		sequenceNumber = xPosition;
		this.yPosition = yPosition;
	}

	/**
	 * @hibernate.property name="yPosition" type="integer" column="yPosition"
	 * @return
	 */
	public Integer getYPosition()
	{
		return yPosition;
	}

	/**
	 * @param position
	 */
	public void setYPosition(Integer position)
	{
		yPosition = position;
	}

	/**
	 *
	 */
	private List<ControlInterface> setSkipLogicControlValues(List<String> values)
	{
		List<ControlInterface> controlList = null;
		try
		{
			if (values == null)
			{
				values = getValueAsStrings();
			}
			List<PermissibleValueInterface> permissibleValueList = new ArrayList<PermissibleValueInterface>();
			if (values != null)
			{
				for (String controlValue : values)
				{
					PermissibleValueInterface selectedPermissibleValue = null;
					AttributeMetadataInterface attributeMetadataInterface = ControlsUtility
							.getAttributeMetadataInterface(getBaseAbstractAttribute());
					if (attributeMetadataInterface != null)
					{
						if ((controlValue != null) && (controlValue.length() > 0))
						{
							selectedPermissibleValue = attributeMetadataInterface
									.getAttributeTypeInformation().getPermissibleValueForString(
											controlValue.toString());
						}
						permissibleValueList.add(selectedPermissibleValue);
					}
				}
				controlList = getSkipLogicControls(permissibleValueList, values);
			}

		}
		catch (ParseException e)
		{
			Logger.out.error(e.getMessage());
		}
		return controlList;
	}

	/**
	 *
	 */
	public void setSkipLogicControls()
	{
		setSkipLogicControlValues(null);
	}

	/**
	 *
	 */
	public List<ControlInterface> setSkipLogicControls(String[] valueArray)
	{
		List<ControlInterface> controlList = null;
		List<String> values = new ArrayList<String>();
		for (String controlValue : valueArray)
		{
			values.add(controlValue);
		}
		controlList = setSkipLogicControlValues(values);
		return controlList;
	}

	/**
	 *
	 */
	public List<ControlInterface> setSkipLogicControls(List<String> valueList)
	{
		List<ControlInterface> controlList = null;
		controlList = setSkipLogicControlValues(valueList);
		return controlList;
	}

	/**
	 *
	 */
	public List<ControlInterface> getSkipLogicControls(
			List<PermissibleValueInterface> selectedPermissibleValues, List<String> values)
	{
		List<ControlInterface> skipLogicControls = new ArrayList<ControlInterface>();
		if (isSkipLogic)
		{
			AttributeMetadataInterface attributeMetadataInterface = ControlsUtility
					.getAttributeMetadataInterface(getBaseAbstractAttribute());
			List<SkipLogicAttributeInterface> readOnlySkipLogicAttributes = getReadOnlySkipLogicAttributes(
					selectedPermissibleValues, attributeMetadataInterface);
			for (SkipLogicAttributeInterface skipLogicAttributeInterface : readOnlySkipLogicAttributes)
			{
				ContainerInterface targetContainerInterface = DynamicExtensionsUtility
						.getContainerForAbstractEntity(skipLogicAttributeInterface
								.getTargetSkipLogicAttribute().getCategoryEntity());
				ControlInterface targetControl = DynamicExtensionsUtility
						.getControlForAbstractAttribute(
								(AttributeMetadataInterface) skipLogicAttributeInterface
										.getTargetSkipLogicAttribute(), targetContainerInterface);
				if (targetControl.getIsSelectiveReadOnly())
				{
					targetControl.setIsSkipLogicReadOnly(Boolean.TRUE);
				}
				if (targetControl.getIsShowHide())
				{
					targetControl.setIsSkipLogicShowHideTargetControl(Boolean.TRUE);
					targetControl.setIsSkipLogicReadOnly(Boolean.TRUE);
				}
				targetControl.setIsSkipLogicLoadPermValues(Boolean.FALSE);
				targetControl.setIsSkipLogicDefaultValue(Boolean.FALSE);
				skipLogicControls.add(targetControl);
			}
			List<SkipLogicAttributeInterface> nonReadOnlySkipLogicAttributes = getNonReadOnlySkipLogicAttributes(
					selectedPermissibleValues, attributeMetadataInterface);
			for (SkipLogicAttributeInterface skipLogicAttributeInterface : nonReadOnlySkipLogicAttributes)
			{
				ContainerInterface targetContainerInterface = DynamicExtensionsUtility
						.getContainerForAbstractEntity(skipLogicAttributeInterface
								.getTargetSkipLogicAttribute().getCategoryEntity());
				ControlInterface targetControl = DynamicExtensionsUtility
						.getControlForAbstractAttribute(
								(AttributeMetadataInterface) skipLogicAttributeInterface
										.getTargetSkipLogicAttribute(), targetContainerInterface);
				targetControl.setIsSkipLogicDefaultValue(Boolean.TRUE);
				targetControl.setIsSkipLogicReadOnly(Boolean.FALSE);
				targetControl.setIsSkipLogicShowHideTargetControl(Boolean.FALSE);
				DataElementInterface dataElementInterface = skipLogicAttributeInterface
						.getDataElement();
				UserDefinedDEInterface userDefinedDEInterface = null;
				if (dataElementInterface instanceof UserDefinedDEInterface)
				{
					userDefinedDEInterface = (UserDefinedDEInterface) dataElementInterface;
				}
				if ((userDefinedDEInterface != null)
						&& (userDefinedDEInterface.getPermissibleValueCollection() != null)
						&& !userDefinedDEInterface.getPermissibleValueCollection().isEmpty())
				{
					targetControl.setIsSkipLogicLoadPermValues(Boolean.TRUE);
				}
				targetControl.setIsSkipLogicTargetControl(Boolean.TRUE);
				skipLogicControls.add(targetControl);
			}
		}
		return skipLogicControls;
	}

	/**
	 *
	 * @param selectedPermissibleValues
	 * @return
	 */
	public List<SkipLogicAttributeInterface> getNonReadOnlySkipLogicAttributes(
			List<PermissibleValueInterface> selectedPermissibleValues,
			AttributeMetadataInterface attributeMetadataInterface)
	{
		List<SkipLogicAttributeInterface> skipLogicAttributes = new ArrayList<SkipLogicAttributeInterface>();
		for (PermissibleValueInterface selectedPermissibleValue : selectedPermissibleValues)
		{
			Collection<PermissibleValueInterface> skipLogicPermissibleValues = attributeMetadataInterface
					.getSkipLogicPermissibleValues();
			if (skipLogicPermissibleValues != null)
			{
				for (PermissibleValueInterface skipLogicValue : skipLogicPermissibleValues)
				{
					if (skipLogicValue.equals(selectedPermissibleValue))
					{
						skipLogicAttributes
								.addAll(skipLogicValue.getDependentSkipLogicAttributes());
					}
				}
			}
		}
		return skipLogicAttributes;
	}

	/**
	 *
	 * @param selectedPermissibleValues
	 * @return
	 */
	public List<SkipLogicAttributeInterface> getReadOnlySkipLogicAttributes(
			List<PermissibleValueInterface> selectedPermissibleValues,
			AttributeMetadataInterface attributeMetadataInterface)
	{
		List<SkipLogicAttributeInterface> skipLogicAttributes = new ArrayList<SkipLogicAttributeInterface>();
		for (PermissibleValueInterface selectedPermissibleValue : selectedPermissibleValues)
		{
			Collection<PermissibleValueInterface> skipLogicPermissibleValues = attributeMetadataInterface
					.getSkipLogicPermissibleValues();
			if (skipLogicPermissibleValues != null)
			{
				for (PermissibleValueInterface skipLogicValue : skipLogicPermissibleValues)
				{
					if (!skipLogicValue.equals(selectedPermissibleValue))
					{
						skipLogicAttributes
								.addAll(skipLogicValue.getDependentSkipLogicAttributes());
					}
				}
			}
		}
		return skipLogicAttributes;
	}

	/**
	 *
	 * @param rowId
	 * @return
	 */
	protected String getSkipLogicDefaultValue()
	{
		String defaultValue = "";
		List<String> values = getSourceSkipControl().getValueAsStrings();
		List<PermissibleValueInterface> permissibleValueList = new ArrayList<PermissibleValueInterface>();
		if (values != null)
		{
			for (String controlValue : values)
			{
				PermissibleValueInterface selectedPermissibleValue = null;
				AttributeMetadataInterface sourceAttributeMetadataInterface = ControlsUtility
						.getAttributeMetadataInterface(sourceSkipControl.getBaseAbstractAttribute());
				if (sourceAttributeMetadataInterface != null)
				{
					if ((controlValue != null) && (controlValue.length() > 0))
					{
						try
						{
							selectedPermissibleValue = sourceAttributeMetadataInterface
									.getAttributeTypeInformation().getPermissibleValueForString(
											controlValue.toString());
						}
						catch (ParseException e)
						{
							Logger.out.error(e.getMessage());
						}
					}
					permissibleValueList.add(selectedPermissibleValue);
				}
			}
			SkipLogicAttributeInterface skipLogicAttributeInterface = ControlsUtility
					.getSkipLogicAttributeForAttribute(permissibleValueList,
							(AttributeMetadataInterface) sourceSkipControl
									.getBaseAbstractAttribute(),
							(AttributeMetadataInterface) getBaseAbstractAttribute());
			if (!getIsSkipLogicReadOnly() && (skipLogicAttributeInterface != null)
					&& (skipLogicAttributeInterface.getDefaultValue() != null))
			{
				defaultValue = skipLogicAttributeInterface.getDefaultValue();
			}
		}
		return defaultValue;
	}

	/**
	 * @hibernate.property name="isSkipLogicTargetControl" type="boolean" column="SKIP_LOGIC_TARGET_CONTROL"
	 * @return
	 */
	public Boolean getIsSkipLogicTargetControl()
	{
		return isSkipLogicTargetControl;
	}

	/**
	 *
	 * @param isSkipLogicTargetControl
	 */
	public void setIsSkipLogicTargetControl(Boolean isSkipLogicTargetControl)
	{
		this.isSkipLogicTargetControl = isSkipLogicTargetControl;
	}

	/**
	 * @hibernate.many-to-one column="SOURCE_CONTROL_ID" class="edu.common.dynamicextensions.domain.userinterface.Control" constrained="true"
	 *                        cascade="save-update"
	 */
	public ControlInterface getSourceSkipControl()
	{
		return sourceSkipControl;
	}

	/**
	 *
	 * @param sourceSkipControl
	 */
	public void setSourceSkipControl(ControlInterface sourceSkipControl)
	{
		this.sourceSkipControl = sourceSkipControl;
	}

	/**
	 *
	 * @return
	 */
	public Boolean getIsSkipLogicLoadPermValues()
	{
		return isSkipLogicLoadPermValues;
	}

	/**
	 *
	 * @param isSkipLogicLoadPermValues
	 */
	public void setIsSkipLogicLoadPermValues(Boolean isSkipLogicLoadPermValues)
	{
		this.isSkipLogicLoadPermValues = isSkipLogicLoadPermValues;
	}

	/**
	 *
	 * @return
	 */
	public String getDataEntryOperation()
	{
		return dataEntryOperation;
	}

	/**
	 *
	 * @param dataEntryOperation
	 */
	public void setDataEntryOperation(String dataEntryOperation)
	{
		this.dataEntryOperation = dataEntryOperation;
	}

	/**
	 *
	 * @return
	 */
	public Boolean getIsSkipLogicShowHideTargetControl()
	{
		return isSkipLogicShowHideTargetControl;
	}

	/**
	 *
	 * @param isSkipLogicShowHideTargetControl
	 */
	public void setIsSkipLogicShowHideTargetControl(Boolean isSkipLogicShowHideTargetControl)
	{
		this.isSkipLogicShowHideTargetControl = isSkipLogicShowHideTargetControl;
	}

	/**
	 * @hibernate.property name="isShowHide" type="boolean" column="SHOW_HIDE"
	 * @return
	 */
	public Boolean getIsShowHide()
	{
		return isShowHide;
	}

	/**
	 *
	 * @param isShowHide
	 */
	public void setIsShowHide(Boolean isShowHide)
	{
		this.isShowHide = isShowHide;
	}

	/**
	 * @hibernate.property name="isSelectiveReadOnly" type="boolean" column="SELECTIVE_READ_ONLY"
	 * @return
	 */
	public Boolean getIsSelectiveReadOnly()
	{
		return isSelectiveReadOnly;
	}

	/**
	 *
	 * @param isSelectiveReadOnly
	 */
	public void setIsSelectiveReadOnly(Boolean isSelectiveReadOnly)
	{
		this.isSelectiveReadOnly = isSelectiveReadOnly;
	}

	/**
	 *
	 * @return
	 */
	public Boolean getIsSkipLogicDefaultValue()
	{
		return isSkipLogicDefaultValue;
	}

	/**
	 *
	 * @param isSkipLogicDefaultValue
	 */
	public void setIsSkipLogicDefaultValue(Boolean isSkipLogicDefaultValue)
	{
		this.isSkipLogicDefaultValue = isSkipLogicDefaultValue;
	}
}