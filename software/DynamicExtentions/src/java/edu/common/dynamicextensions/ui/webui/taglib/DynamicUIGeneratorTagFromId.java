
package edu.common.dynamicextensions.ui.webui.taglib;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.processor.LoadDataEntryFormProcessor;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.ui.webui.util.UserInterfaceiUtility;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants;

public class DynamicUIGeneratorTagFromId extends TagSupport
{

	private static final long serialVersionUID = 1L;
	private Long formRecordId;
	private Long containerId;
	private String mode;
	private ContainerInterface containerInterface;
	private HttpServletRequest request;
	private Map<BaseAbstractAttributeInterface, Object> valueMapStack;
	private Map<String, Object> contextParameter = new HashMap<String, Object>();


	public Long getFormRecordId()
	{
		return formRecordId;
	}

	public void setFormRecordId(Long formRecordId)
	{
		this.formRecordId = formRecordId;
	}

	public Long getContainerId()
	{
		return containerId;
	}

	public void setContainerId(Long containerId)
	{
		this.containerId = containerId;
	}

	public String getMode()
	{
		return mode;
	}

	public void setMode(String mode)
	{
		this.mode = mode;
	}

	@Override
	public int doEndTag() throws JspException
	{
		try
		{
			initContainer();
			final Set<AttributeInterface> attributes = new HashSet<AttributeInterface>();
			valueMapStack = LoadDataEntryFormProcessor.getValueMapFromRecordId(containerInterface
					.getAbstractEntity(), formRecordId.toString());
			UserInterfaceiUtility.addPrecisionZeroes(valueMapStack, attributes);
			UserInterfaceiUtility.setContainerValueMap(containerInterface, valueMapStack);
			generateHTML();
		}
		catch (DynamicExtensionsSystemException e)
		{
			throw new JspException(e);
		}
		catch (DynamicExtensionsApplicationException e)
		{
			throw new JspException(e);
		}
		catch (IOException e)
		{
			throw new JspException(e);
		}

		return super.doEndTag();
	}

	private void generateHTML() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, IOException
	{
		final String caption = (String) pageContext.getSession().getAttribute(WebUIManagerConstants.OVERRIDE_CAPTION);

		DynamicExtensionsUtility.setEncounterDateToChildContainer(containerInterface,
				contextParameter);
		Map<Long, ContainerInterface> containerMap = new HashMap<Long, ContainerInterface>();
		TagUtility.setValidationMap(containerMap, containerInterface);
		pageContext.getSession().setAttribute(Constants.MAP_FOR_VALIDATION, containerMap);
		final JspWriter out = pageContext.getOut();
		containerInterface.setPreviousValueMap(valueMapStack);
		containerInterface.setMode(getMode());
		out.println(this.containerInterface.generateContainerHTML(caption, mode));

		//This a temporary solution to resolve alignment issue when more than one forms are displayed in  a single page.
		out.println("</td></tr></table>");
	}

	private void initContainer() throws JspException
	{
		try
		{
			{
				request = (HttpServletRequest) pageContext.getRequest();
				containerInterface = DynamicExtensionsUtility
						.getClonedContainerFromCache(containerId.toString());
			}
			containerInterface.getContainerValueMap().clear();
			DynamicExtensionsUtility.cleanContainerControlsValue(containerInterface);

			containerInterface.setMode(mode);
			CacheManager.addObjectToCache(request, DEConstants.CONTAINER_INTERFACE,
					containerInterface);

		}
		catch (DynamicExtensionsCacheException cacheException)
		{
			throw new JspException(cacheException);
		}
	}

}
