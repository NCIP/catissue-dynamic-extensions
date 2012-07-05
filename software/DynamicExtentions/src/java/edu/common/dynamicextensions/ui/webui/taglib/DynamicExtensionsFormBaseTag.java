
package edu.common.dynamicextensions.ui.webui.taglib;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import edu.common.dynamicextensions.ui.webui.util.UserInterfaceiUtility;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.logger.Logger;

/**
 * @author Kunal
 * Base tag can be extended by any tag involved with form related functionality
 */
public abstract class DynamicExtensionsFormBaseTag extends TagSupport
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Long containerIdentifier;
	protected Long recordIdentifier;
	protected ContainerInterface container;
	protected Map<BaseAbstractAttributeInterface, Object> dataValueMap;
	protected EntityCache cache;
	private static final Logger LOGGER = Logger.getCommonLogger(DynamicExtensionsFormBaseTag.class);
	protected JspWriter jspWriter;

	@Override
	public int doStartTag() throws JspException
	{
		cache = EntityCache.getInstance();
		try
		{
			container = cache.getContainerById(containerIdentifier);
			dataValueMap = LoadDataEntryFormProcessor.getValueMapFromRecordId(container
					.getAbstractEntity(), recordIdentifier.toString());
			final Set<AttributeInterface> attributes = new HashSet<AttributeInterface>();
			UserInterfaceiUtility.addPrecisionZeroes(dataValueMap, attributes);
			UserInterfaceiUtility.setContainerValueMap(container, dataValueMap);
			edu.common.dynamicextensions.util.DataValueMapUtility.updateDataValueMapDataLoading(
					dataValueMap, container);
			jspWriter = pageContext.getOut();

		}
		catch (DynamicExtensionsCacheException e)
		{
			LOGGER.error(e.getMessage(), e);
		}
		catch (DynamicExtensionsSystemException e)
		{
			LOGGER.error(e.getMessage(), e);
		}
		catch (DynamicExtensionsApplicationException e)
		{
			LOGGER.error(e.getMessage(), e);
		}
		return super.doStartTag();
	}

	public Long getContainerIdentifier()
	{
		return containerIdentifier;
	}

	public void setContainerIdentifier(Long containerIdentifier)
	{
		this.containerIdentifier = containerIdentifier;
	}

	public Long getRecordIdentifier()
	{
		return recordIdentifier;
	}

	public void setRecordIdentifier(Long recordIdentifier)
	{
		this.recordIdentifier = recordIdentifier;
	}

	public ContainerInterface getContainer()
	{
		return container;
	}

	public Map<BaseAbstractAttributeInterface, Object> getDataValueMap()
	{
		return dataValueMap;
	}

}
