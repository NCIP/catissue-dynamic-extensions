
package edu.common.dynamicextensions.ui.webui.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.ContainerUtility;
import edu.common.dynamicextensions.ui.webui.util.FormCache;
import edu.wustl.common.util.logger.Logger;

public class DynamicUIGeneratorTag extends TagSupport
{

	/**
	 *
	 */

	private static final long serialVersionUID = 1L;
	private Long recordIdentifier;
	private Long containerIdentifier;
	private String mode;
	private FormCache formCache;

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

		try
		{
			formCache = new FormCache((HttpServletRequest) pageContext.getRequest());
			formCache.onFormLoad();
			generateHTML();
		}
		catch (final DynamicExtensionsSystemException e)
		{
			Logger.out.debug("DynamicExtensionsSystemException. No response generated.");
		}
		catch (final IOException e)
		{
			Logger.out.debug("IOException. No response generated.");
		}
		catch (DynamicExtensionsApplicationException e)
		{
			Logger.out.debug("IOException. No response generated." + e.getMessage());
		}
		return EVAL_PAGE;
	}

	private void generateHTML() throws IOException, DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		ContainerInterface containerInterface = formCache.getContainer();
		containerInterface.updateMode(getMode());
		
		ContainerUtility containerUtility = new ContainerUtility((HttpServletRequest) pageContext
				.getRequest(), formCache.getContainer());


		final JspWriter out = pageContext.getOut();
		out.println(containerUtility.generateHTML());
	}

	public Long getRecordIdentifier()
	{
		return recordIdentifier;
	}

	public void setRecordIdentifier(Long formRecordId)
	{
		this.recordIdentifier = formRecordId;
	}

	public Long getContainerIdentifier()
	{
		return containerIdentifier;
	}

	public void setContainerIdentifier(Long containerId)
	{
		this.containerIdentifier = containerId;
	}

	public String getMode()
	{
		return mode;
	}

	public void setMode(String mode)
	{
		this.mode = mode;
	}

}
