
package edu.common.dynamicextensions.ui.webui.taglib;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.logger.Logger;

public class DynamicUIGeneratorTag extends TagSupport
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	protected transient ContainerInterface container = null;

	protected Map<BaseAbstractAttributeInterface, Object> previousDataMap;

	public Map<BaseAbstractAttributeInterface, Object> getPreviousDataMap()
	{
		return previousDataMap;
	}

	public void setPreviousDataMap(final Map<BaseAbstractAttributeInterface, Object> previousDataMap)
	{
		this.previousDataMap = previousDataMap;
	}

	/**
	 *
	 * @return
	 */
	public ContainerInterface getContainerInterface()
	{
		return container;
	}

	/**
	 *
	 * @param container
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
			final String caption = (String) pageContext.getSession()
			.getAttribute("OverrideCaption");
			if(pageContext
					.getSession().getAttribute("mandatory_Message")!=null)
			{
				this.container.setShowRequiredFieldWarningMessage(Boolean.valueOf(pageContext
					.getSession().getAttribute("mandatory_Message").toString()));
			}
			final String operation = pageContext.getRequest().getParameter("dataEntryOperation");
			final JspWriter out = pageContext.getOut();
			container.setPreviousValueMap(previousDataMap);
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

}
