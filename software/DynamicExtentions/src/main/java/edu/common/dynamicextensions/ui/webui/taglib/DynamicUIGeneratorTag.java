
package edu.common.dynamicextensions.ui.webui.taglib;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import edu.common.dynamicextensions.domain.userinterface.AbstractContainmentControl;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.util.logger.Logger;

public class DynamicUIGeneratorTag extends TagSupport
{

	/**
	 *
	 */
	public Map<String, Object> contextParameter = new HashMap<String, Object>();
	private static final long serialVersionUID = 1L;

	protected transient ContainerInterface container = null;

	protected Map<BaseAbstractAttributeInterface, Object> previousDataMap;

	/**
	 * Returns the previous data map.
	 * @return Map of BaseAbstractAttributeInterface to Object value.
	 */
	public Map<BaseAbstractAttributeInterface, Object> getPreviousDataMap()
	{
		return previousDataMap;
	}

	/**
	 * Set the old BaseAbstractAttributeInterface to Object value.
	 * @param previousDataMap Map of BaseAbstractAttributeInterface to Object value.
	 */
	public void setPreviousDataMap(final Map<BaseAbstractAttributeInterface, Object> previousDataMap)
	{
		this.previousDataMap = previousDataMap;
	}

	/**
	 * Returns the ContainerInterface.
	 * @return ContainerInterface
	 */
	public ContainerInterface getContainerInterface()
	{
		return container;
	}

	/**
	 * Set ContainerInterface.
	 * @param container ContainerInterface
	 */
	public void setContainerInterface(final ContainerInterface container)
	{
		this.container = container;
	}

	/**
	 * Validates all the attributes passed to the tag
	 * @return boolean - true if all the attributes passed to the tag are valid
	 * @since TODO
	 */
	private boolean isDataValid()
	{
		boolean isDataValid = true;
		if (this.getContainerInterface() == null)
		{
			Logger.out.debug("Container interface is null");
			isDataValid = false;
		}
		return isDataValid;
	}

	/**
	 * This method contains no operations.
	 * @return int SKIP_BODY
	 * @since TODO
	 */
	public int doStartTag()
	{
		Logger.out.debug("Entering Selector List Tag ...");
		return SKIP_BODY;
	}

	/**
	 *
	 */
	public int doEndTag()
	{
		if (!isDataValid())
		{
			return EVAL_PAGE;
		}
		try
		{
			String caption = getCaptionValue();

			if (pageContext.getSession().getAttribute("mandatory_Message") != null)
			{
				this.container.setShowRequiredFieldWarningMessage(Boolean.valueOf(pageContext
						.getSession().getAttribute("mandatory_Message").toString()));
			}
			final String operation = pageContext.getRequest().getParameter("dataEntryOperation");

			handleEncounteredDate();

			final String formContextId = handleFormContext();

			handleDataValueMap(formContextId);

			final JspWriter out = pageContext.getOut();
			out.println(this.container.generateContainerHTML(caption, operation));
		}
		catch (final DynamicExtensionsSystemException e)
		{
			Logger.out.debug("DynamicExtensionsSystemException. No response generated.");
		}
		catch (final IOException e)
		{
			Logger.out.debug("IOException. No response generated.");
		}
		return EVAL_PAGE;
	}

	/**
	 * @return
	 */
	private String handleFormContext()
	{
		final String formContextId = pageContext.getRequest().getParameter(
				WebUIManagerConstants.FORM_CONTEXT_IDENTIFIER);
		contextParameter.put(WebUIManagerConstants.FORM_CONTEXT_IDENTIFIER, formContextId);
		container.setContextParameter(contextParameter);
		DynamicExtensionsUtility.setEncounterDateToChildContainer(container, contextParameter);
		return formContextId;
	}

	/**
	 * Handle data value map.
	 * @param formContextId
	 */
	private void handleDataValueMap(final String formContextId)
	{
		Map<Long, ContainerInterface> containerMap = new HashMap<Long, ContainerInterface>();
		setValidationMap(containerMap, container);
		String key = "MapForValidation" + (formContextId == null ?"":formContextId);
		pageContext.getSession().setAttribute(key, containerMap);
		container.setPreviousValueMap(previousDataMap);
	}

	/**
	 * Handle encountered date.
	 */
	private void handleEncounteredDate()
	{
		final String encounterDate = pageContext.getRequest().getParameter(
				Constants.ENCOUNTER_DATE);
		if (container.getContextParameter(Constants.ENCOUNTER_DATE) == null)
		{
			contextParameter.put(Constants.ENCOUNTER_DATE, ControlsUtility
					.getFormattedDate(encounterDate));
			container.setContextParameter(contextParameter);
		}
		DynamicExtensionsUtility.setEncounterDateToChildContainer(container, contextParameter);
	}

	/**
	 * Gets the caption value.
	 * @return the caption value
	 */
	private String getCaptionValue()
	{
		String caption = pageContext.getRequest().getParameter("OverrideCaption");
		if (caption == null)
		{
			caption = (String) pageContext.getSession().getAttribute("OverrideCaption");
		}
		return caption;
	}

	/**
	 *
	 * @param containerMap
	 * @param container
	 */
	private void setValidationMap(Map<Long, ContainerInterface> containerMap,
			ContainerInterface container)
	{
		containerMap.put(container.getId(), container);
		for (ControlInterface control : container.getAllControlsUnderSameDisplayLabel())
		{
			if (control instanceof AbstractContainmentControlInterface)
			{
				final ContainerInterface containmentContainer = ((AbstractContainmentControl) control)
						.getContainer();
				setValidationMap(containerMap, containmentContainer);
			}
		}
		for (ContainerInterface childContainer : container.getChildContainerCollection())
		{
			setValidationMap(containerMap, childContainer);
		}
	}

}
