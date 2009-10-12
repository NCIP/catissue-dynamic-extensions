
package edu.common.dynamicextensions.domaininterface.userinterface;

import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domain.userinterface.Container;
import edu.common.dynamicextensions.domain.userinterface.Control;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface;
import edu.common.dynamicextensions.domaininterface.FormControlNotesInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

/**
 * This interface stores necessary informations about the control that gets added
 * to the Container on dynamically generated User Interface.
 * @author geetika_bangard
 */
public interface ControlInterface extends DynamicExtensionBaseDomainObjectInterface, Comparable
{

	/**
	 * Id of the control
	 * @return id
	 */
	Long getId();

	/**
	 * This can be a primitive type or derived type.
	 * @return Returns the attribute.
	 */
	BaseAbstractAttributeInterface getBaseAbstractAttribute();

	/**
	 * @param abstractAttributeInterface The attribute to set.
	 */
	void setBaseAbstractAttribute(BaseAbstractAttributeInterface abstractAttributeInterface);

	/**
	 * @param abstractAttributeInterface The attribute to set.
	 */
	AttributeMetadataInterface getAttibuteMetadataInterface();

	/**
	 * Caption/Title for the control.
	 * @return Returns the caption.
	 */
	String getCaption();

	/**
	 * @param caption The caption to set.
	 */
	void setCaption(String caption);

	/**
	 * The css specified for the control by user.
	 * @return Returns the cssClass.
	 */
	String getCssClass();

	/**
	 * @param cssClass The cssClass to set.
	 */
	void setCssClass(String cssClass);

	/**
	 * If user has chosen it to be kept hidden.
	 * @return Returns the isHidden.
	 */
	Boolean getIsHidden();

	/**
	 * @param isHidden The isHidden to set.
	 */
	void setIsHidden(Boolean isHidden);

	/**
	 * Name of the control.
	 * @return Returns the name.
	 */
	String getName();

	/**
	 * @param name The name to set.
	 */
	void setName(String name);

	/**
	 * The sequence Number for setting it at the desired place in the tree and so in the UI.
	 * @return Returns the sequenceNumber.
	 */
	Integer getSequenceNumber();

	/**
	 * @param sequenceNumber The sequenceNumber to set.
	 */
	void setSequenceNumber(Integer sequenceNumber);

	/**
	 * Tool tip for the control.
	 * @return Returns the tooltip.
	 */
	String getTooltip();

	/**
	 * @param tooltip The tooltip to set.
	 */
	void setTooltip(String tooltip);

	/**
	 * @return return the HTML string for this type of a object
	 * @throws DynamicExtensionsSystemException
	 */
	String generateHTML(Integer rowId,ContainerInterface container) throws DynamicExtensionsSystemException;

	/**
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	String getHTMLComponentName() throws DynamicExtensionsSystemException;

	/**
	 * @return
	 */
	Object getValue();

	/**
	 * @param value
	 */
	void setValue(Object value);

	/**
	 * @return
	 */
	Boolean getSequenceNumberChanged();

	/**
	 * @param sequenceNumberChanged
	 */
	void setSequenceNumberChanged(Boolean sequenceNumberChanged);

	/**
	 * @return parentContainer
	 */
	Container getParentContainer();

	/**
	 * @param parentContainer parentContainer
	 */
	void setParentContainer(Container parentContainer);

	/**
	 * @return the isSubControl
	 */
	boolean getIsSubControl();

	/**
	 * @param isSubControl the isSubControl to set
	 */
	void setIsSubControl(boolean isSubControl);

	/**
	 * @return 
	 */
	Boolean getIsReadOnly();

	/**
	 * @param isReadOnly
	 */
	void setIsReadOnly(Boolean isReadOnly);

	/**
	 * @return
	 */
	String getHeading();

	/**
	 * @param heading
	 */
	void setHeading(String heading);

	/**
	 * @return
	 */
	List<FormControlNotesInterface> getFormNotes();

	/**
	 * @param formNotes
	 */
	void setFormNotes(List<FormControlNotesInterface> formNotes);

	/**
	 * @param xPosition
	 * @param yPosition
	 */
	void setControlPosition(int xPosition, int yPosition);

	/**
	 * @return
	 */
	Integer getYPosition();

	/**
	 * @param position
	 */
	void setYPosition(Integer position);
	/**
	 * 
	 * @return
	 */
	Boolean getIsCalculated();
	/**
	 * 
	 * @param isCalculated
	 */
	void setIsCalculated(Boolean isCalculated);
	/**
	 * 
	 * @return
	 */
	Boolean getIsSkipLogic();
	/**
	 * 
	 * @param isSkipLogic
	 */
	void setIsSkipLogic(Boolean isSkipLogic);
	/**
	 * 
	 * @return
	 */
	Boolean getIsSkipLogicReadOnly();
	/**
	 * 
	 * @param isSkipLogicReadOnly
	 */
	void setIsSkipLogicReadOnly(Boolean isSkipLogicReadOnly);
	/**
	 * 
	 * @param selectedPermissibleValues
	 * @return
	 */
	List<ControlInterface> getSkipLogicControls(
			List<PermissibleValueInterface> selectedPermissibleValues,
			Integer rowId, List<String> values);
	/**
	 * 
	 * @param selectedPermissibleValue
	 * @return
	 */
	void setSkipLogicControls(Integer rowId);
	/**
	 * 
	 * @param rowId
	 * @param valueArray
	 */
	List<ControlInterface> setSkipLogicControls(Integer rowId,String[] valueArray);
	/**
	 * 
	 * @param isSkipLogicTargetControl
	 */
	void setIsSkipLogicTargetControl(Boolean isSkipLogicTargetControl);
	/**
	 * 
	 * @return
	 */
	 Boolean getIsSkipLogicTargetControl();
	 /**
	  * 
	  * @param listOfValues
	  */
	 void setValueAsStrings(List<String> listOfValues);
	 /**
	  * 
	  * @return
	  */
	 List<String> getValueAsStrings(Integer rowId);
	 /**
	  * 
	  * @return
	  */
	 ControlInterface getSourceSkipControl();
	 /**
	  * 
	  * @param sourceSkipControl
	  */
	 void setSourceSkipControl(ControlInterface sourceSkipControl);
	 /**
	  * 
	  * @return
	  */
	 Boolean getIsSkipLogicLoadPermValues();
	 /**
	  * 
	  * @param isSkipLogicLoadPermValues
	  */
	 void setIsSkipLogicLoadPermValues(Boolean isSkipLogicLoadPermValues);
	 /**
	  * 
	  * @param dataEntryOperation
	  */
	 void setDataEntryOperation(String dataEntryOperation);
	 /**
	  * 
	  */
	 String getDataEntryOperation();
	 /**
	  * 
	  * @param rowId
	  * @param values
	  */
	 void addSourceSkipControlValue(Integer rowId,List<String> values);
	 /**
	  * 
	  * @param rowId
	  * @return
	  */
	 List<String> getSourceSkipControlValue(Integer rowId);
	 /**
	  * 
	  * @param isSkipLogicShowHideTargetControl
	  */
	 void setIsSkipLogicShowHideTargetControl(
			Boolean isSkipLogicShowHideTargetControl);
	 /**
	  * 
	  * @return
	  */
	Boolean getIsSkipLogicShowHideTargetControl();
	/**
	 * 
	 * @return
	 */
	Boolean getIsShowHide();
	/**
	 * 
	 * @param isShowHide
	 */
	void setIsShowHide(Boolean isShowHide);
	/**
	 * 
	 * @return
	 */
	Boolean getIsSelectiveReadOnly();
	/**
	 * 
	 * @param isSelectiveReadOnly
	 */
	void setIsSelectiveReadOnly(Boolean isSelectiveReadOnly);
}