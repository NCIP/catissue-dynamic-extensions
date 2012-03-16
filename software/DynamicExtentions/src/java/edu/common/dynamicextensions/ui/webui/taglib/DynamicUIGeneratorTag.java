
package edu.common.dynamicextensions.ui.webui.taglib;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.ContainerUtility;
import edu.common.dynamicextensions.ui.webui.util.FormCache;
import edu.common.dynamicextensions.ui.webui.util.UserInterfaceiUtility;
import edu.common.dynamicextensions.util.DataValueMapUtility;
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
			// TODO Auto-generated catch block
			Logger.out.debug("IOException. No response generated." + e.getMessage());
		}
		return EVAL_PAGE;
	}

	private void generateHTML() throws IOException, DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		ContainerUtility containerUtility = new ContainerUtility((HttpServletRequest) pageContext
				.getRequest(), formCache.getContainer());

		htmlGenerationPreProcess();

		final JspWriter out = pageContext.getOut();
		out.println(containerUtility.generateHTML());
	}

	private void htmlGenerationPreProcess()
	{
		formCache.getContainer().setPreviousValueMap(formCache.getValueMapStack().peek());
		final Set<AttributeInterface> attributes = new HashSet<AttributeInterface>();
		UserInterfaceiUtility.addPrecisionZeroes(formCache.getValueMapStack().peek(), attributes);
		DataValueMapUtility.updateDataValueMapDataLoading(formCache.getValueMapStack().peek(),
				formCache.getContainer());
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
