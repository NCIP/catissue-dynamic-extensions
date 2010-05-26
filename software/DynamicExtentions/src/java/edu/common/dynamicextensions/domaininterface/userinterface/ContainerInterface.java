
package edu.common.dynamicextensions.domaininterface.userinterface;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

/**
 * This interface stores the necessary information about the container on dynamically generated user interface.
 *
 * @author geetika_bangard
 */
public interface ContainerInterface extends DynamicExtensionBaseDomainObjectInterface
{

	/**
	 * @return Long id
	 */
	Long getId();

	/**
	 * The css style defined for button.
	 * @return Returns the buttonCss.
	 */
	String getButtonCss();

	/**
	 * @param buttonCss The buttonCss to set.
	 */
	void setButtonCss(String buttonCss);

	/**
	 * caption for the container.
	 * @return Returns the caption.
	 */
	String getCaption();

	/**
	 * @param caption The caption to set.
	 */
	void setCaption(String caption);

	/**
	 * The list of user selected controls.
	 * @return Returns the controlCollection.
	 */
	Collection<ControlInterface> getControlCollection();

	/**
	 * @param controlInterface The controlInterface to be added.
	 */
	void addControl(ControlInterface controlInterface);

	/**
	 * Entity Interface which is added to the container.
	 * @return Returns the entity.
	 */
	AbstractEntityInterface getAbstractEntity();

	/**
	 * @param entityInterface The entity to set.
	 */
	void setAbstractEntity(AbstractEntityInterface abstractEntityInterface);

	/**
	 * css style for the main table.
	 * @return Returns the mainTableCss.
	 */
	String getMainTableCss();

	/**
	 * @param mainTableCss The mainTableCss to set.
	 */
	void setMainTableCss(String mainTableCss);

	/**
	 * @return Returns the requiredFieldIndicatior.
	 */
	String getRequiredFieldIndicatior();

	/**
	 * @param requiredFieldIndicatior The requiredFieldIndicatior to set.
	 */
	void setRequiredFieldIndicatior(String requiredFieldIndicatior);

	/**
	 * @return Returns the requiredFieldWarningMessage.
	 */
	String getRequiredFieldWarningMessage();

	/**
	 * @param requiredFieldWarningMessage The requiredFieldWarningMessage to set.
	 */
	void setRequiredFieldWarningMessage(String requiredFieldWarningMessage);

	/**
	 * css style for the Title.
	 * @return Returns the titleCss.
	 */
	String getTitleCss();

	/**
	 * @param titleCss The titleCss to set.
	 */
	void setTitleCss(String titleCss);

	/**
	 *
	 * @param sequenceNumber the Sequence Number of the control
	 * @return the Control Interface
	 */
	ControlInterface getControlInterfaceBySequenceNumber(String sequenceNumber);

	/**
	 *
	 * @param controlInterface : control interface object to be removed
	 */
	void removeControl(ControlInterface controlInterface);

	/**
	 * Remove all controls
	 *
	 */
	void removeAllControls();

	/**
	 * @return
	 */
	String getMode();

	/**
	 * @param mode
	 */
	void setMode(String mode);

	/**
	 * @param caption
	 * @return return the HTML string for this type of a object
	 * @throws DynamicExtensionsSystemException
	 */
	String generateContainerHTML(String caption, String dataEntryOperation)
			throws DynamicExtensionsSystemException;

	/**
	 * @param caption
	 * @return return the HTML string for this type of a object
	 * @throws DynamicExtensionsSystemException
	 */
	String generateControlsHTML(String caption, String dataEntryOperation,
			ContainerInterface container) throws DynamicExtensionsSystemException;

	/**
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	String generateControlsHTMLAsGrid(List<Map<BaseAbstractAttributeInterface, Object>> valueMap,
			String dataEntryOperation, ContainerInterface container)
			throws DynamicExtensionsSystemException;

	/**
	 * @return
	 */
	Map<BaseAbstractAttributeInterface, Object> getContainerValueMap();

	/**
	 * @param containerValueMap
	 */
	void setContainerValueMap(Map<BaseAbstractAttributeInterface, Object> containerValueMap);

	/**
	 *
	 * @return
	 */
	Boolean getShowAssociationControlsAsLink();

	/**
	 *
	 * @param showAssociationControlsAsLink
	 */
	void setShowAssociationControlsAsLink(Boolean showAssociationControlsAsLink);

	/**
	 *
	 * @return
	 */
	List<ControlInterface> getAllControls();

	/**
	 *
	 * @return
	 */
	ContainerInterface getBaseContainer();

	/**
	 *
	 * @param baseContainer
	 */
	void setBaseContainer(ContainerInterface baseContainer);

	/**
	 * @return the incontextContainer
	 */
	ContainerInterface getIncontextContainer();

	/**
	 * @param incontextContainer the incontextContainer to set
	 */
	void setIncontextContainer(ContainerInterface incontextContainer);

	/**
	 * This method is used to decide where caption is to be added to the table or not
	 * @return
	 */
	Boolean getAddCaption();

	/**
	 * @param addCaption
	 */
	void setAddCaption(Boolean addCaption);

	/**
	 * @see edu.common.dynamicextensions.domaininterface.userinterface.ContainmentAssociationControlInterface#generateLinkHTML()
	 */
	String generateLink(ContainerInterface containerInterface)
			throws DynamicExtensionsSystemException;

	/**
	 * @param xPosition
	 * @param yPosition
	 * @return
	 */
	ControlInterface getControlByPosition(Integer xPosition, Integer yPosition);

	/**
	 * @return
	 */
	List<ControlInterface> getAllControlsUnderSameDisplayLabel();

	/**
	 * @return
	 */
	Collection<ContainerInterface> getChildContainerCollection();

	/**
	 * @return
	 */
	boolean isAjaxRequest();

	/**
	 * @param isAjaxRequest
	 */
	void setAjaxRequest(boolean isAjaxRequest);

	/**
	 * @param request the request to set
	 */
	void setRequest(HttpServletRequest request);

	/**
	 * @return the request
	 */
	HttpServletRequest getRequest();

	/**
	 * @return true if required field warning has to be shown
	 */
	Boolean isShowRequiredFieldWarningMessage();

	/**
	 * @param showRequiredFieldWarningMessage the showRequiredFieldWarningMessage to set
	 */
	void setShowRequiredFieldWarningMessage(Boolean showRequiredFieldWarningMessage);

	/**
	 *
	 * @return
	 */
	boolean isAllControlsSkipLogicTargetControlsForShowHide();

	/**
	 * @param set previous page data value map
	 */
	void setPreviousValueMap(Map<BaseAbstractAttributeInterface, Object> peek);

	/**
	 * @return previous page data value map
	 */
	Map<BaseAbstractAttributeInterface, Object> getPreviousValueMap();

	/**
	 *
	 * @return
	 */
	boolean getIsSourceCalculatedAttributes();
}
