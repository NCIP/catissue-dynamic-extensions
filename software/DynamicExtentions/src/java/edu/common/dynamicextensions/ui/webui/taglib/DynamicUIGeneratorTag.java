
package edu.common.dynamicextensions.ui.webui.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.logger.Logger;

public class DynamicUIGeneratorTag extends TagSupport
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */

	protected ContainerInterface containerInterface = null;

	/**
	 * 
	 * @return
	 */
	public ContainerInterface getContainerInterface()
	{
		return containerInterface;
	}

	/**
	 * 
	 * @param containerInterface
	 */
	public void setContainerInterface(ContainerInterface containerInterface)
	{
		this.containerInterface = containerInterface;
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
			String caption = (String) pageContext.getSession().getAttribute("OverrideCaption");
			this.containerInterface.setShowRequiredFieldWarningMessage(Boolean.valueOf(pageContext.getSession().getAttribute("mandatory_Message").toString()));
			String dataEntryOperation = pageContext.getRequest().getParameter("dataEntryOperation");
			JspWriter out = pageContext.getOut();
			out.println(this.containerInterface.generateContainerHTML(caption,dataEntryOperation));
		}
		catch (DynamicExtensionsSystemException e)
		{
			Logger.out.debug("DynamicExtensionsSystemException. No response generated.");
		}
		catch (IOException e)
		{
			Logger.out.debug("IOException. No response generated.");
		}
		return EVAL_PAGE;
	}

}
