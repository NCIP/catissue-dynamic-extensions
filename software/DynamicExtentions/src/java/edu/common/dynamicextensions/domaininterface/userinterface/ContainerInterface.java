
package edu.common.dynamicextensions.domaininterface.userinterface;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import edu.common.dynamicextensions.domaininterface.AbstractEntityInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.DynamicExtensionBaseDomainObjectInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
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
	 * @param abstractEntityInterface The entity to set.
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
	 * Remove all controls.
	 *
	 */
	void removeAllControls();

	/**
	 * @return Return the mode.
	 */
	String getMode();

	/**
	 * @param mode
	 */
	void setMode(String mode);

	/**
	 * This method generates HTML for container.
	 * @param dataEntryOperation Operation being performed.
	 * @return return the HTML string for this type of a object
	 * @throws DynamicExtensionsSystemException  the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException 
	 */
	String generateContainerHTML(String caption, String dataEntryOperation)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * This method generates HTML for container.
	 * @param dataEntryOperation Operation being performed.
	 * @return return the HTML string for this type of a object
	 * @throws DynamicExtensionsSystemException  the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException 
	 */
	String generateControlsHTML(String caption, String dataEntryOperation,
			ContainerInterface container) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * This method generates HTML for control as grid.
	 * @param valueMap attribute to value map.
	 * @param dataEntryOperation Operation being performed.
	 * @param container Container.
	 * @param isPasteEnable Whether past is enable or not.
	 * @return Generated HTML string.
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 * @throws DynamicExtensionsApplicationException 
	 */
	String generateControlsHTMLAsGrid(List<Map<BaseAbstractAttributeInterface, Object>> valueMap,
			String dataEntryOperation, ContainerInterface container, boolean isPasteEnable,List<String> errorList)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException;

	/**
	 * This method gets attribute to value map for container.
	 * @return attribute to value map.
	 */
	Map<BaseAbstractAttributeInterface, Object> getContainerValueMap();

	/**
	 * This method sets attribute to value map for container.
	 * @param containerValueMap attribute to value map.
	 */
	void setContainerValueMap(Map<BaseAbstractAttributeInterface, Object> containerValueMap);

	/**
	 * This method return whether to show association control as link for the container.
	 * @return return whether to show association control as link.
	 */
	Boolean getShowAssociationControlsAsLink();

	/**
	 * This method sets whether to show association control as link for the container.
	 * @param showAssociationControlsAsLink whether to show association control as link.
	 */
	void setShowAssociationControlsAsLink(Boolean showAssociationControlsAsLink);

	/**
	 * returns all controls.
	 * @return List of all controls.
	 */
	List<ControlInterface> getAllControls();

	/**
	 * Returns the base container.
	 * @return Base Container
	 */
	ContainerInterface getBaseContainer();

	/**
	 * Sets the base container for the control.
	 * @param baseContainer Base container.
	 */
	void setBaseContainer(ContainerInterface baseContainer);

	/**
	 * @return the incontextContainer
	 */
	ContainerInterface getIncontextContainer();

	/**
	 * @param incontextContainer the incontextContainer to set.
	 */
	void setIncontextContainer(ContainerInterface incontextContainer);

	/**
	 * This method is used to decide where caption is to be added to the table or not.
	 * @return whether to add caption or not.
	 */
	Boolean getAddCaption();

	/**
	 * This method Sets whether to add caption or not.
	 * @param addCaption whether to add caption or not.
	 */
	void setAddCaption(Boolean addCaption);

	/**
	 * @see edu.common.dynamicextensions.domaininterface.userinterface.ContainmentAssociationControlInterface#generateLinkHTML()
	 */
	String generateLink(ContainerInterface containerInterface)
			throws DynamicExtensionsSystemException;

	/**
	 * This method returns the position of the control of the container.
	 * @param xPosition
	 * @param yPosition
	 * @return
	 */
	ControlInterface getControlByPosition(Integer xPosition, Integer yPosition);

	/**
	 * This method return all the controls under same display label of container.
	 * @return List of controls.
	 */
	List<ControlInterface> getAllControlsUnderSameDisplayLabel();

	/**
	 * This method returns child containers of container.
	 * @return collection of child container.
	 */
	Collection<ContainerInterface> getChildContainerCollection();

	/**
	 * This method return whether its an Ajax request.
	 * @return whether its an Ajax request.
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
	 * this method return whether to show required field warning or not.
	 * @return true if required field warning has to be shown
	 */
	Boolean isShowRequiredFieldWarningMessage();

	/**
	 * @param showRequiredFieldWarningMessage the showRequiredFieldWarningMessage to set
	 */
	void setShowRequiredFieldWarningMessage(Boolean showRequiredFieldWarningMessage);

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

	/**
	 * @return
	 */
	Object getContextParameter(String key);

	/**
	 * @param contextParameter
	 */
	void setContextParameter(Map<String, Object> contextParameter);

	/**
	 * @param id control id
	 * @return control object
	 */
	ControlInterface getControlById(Long id);

	/**
	 * Gets the parent container.
	 * @param container the container
	 * @return the parent container
	 */
	ContainerInterface getParentContainer(ContainerInterface container);
	List<String> getErrorList();

	void updateMode(String mode);
	
	public Map<String, Object> getContextParameter();
}
