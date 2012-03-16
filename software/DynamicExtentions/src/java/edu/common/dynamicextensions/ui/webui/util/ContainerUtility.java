package edu.common.dynamicextensions.ui.webui.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.ui.webui.taglib.TagUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;


public class ContainerUtility
{

	private ContainerInterface container;
	private HttpServletRequest request;
	
	public ContainerUtility(HttpServletRequest request, ContainerInterface container)
	{
		this.request = request;
		this.container = container;
		
		//Used for PV versioning
		updateActivationDate();

		//Used by live validations for faster container search
		updateConatinerMap();
	}
	
	private void updateActivationDate()
	{
		final String encounterDate = request
				.getParameter(Constants.ENCOUNTER_DATE);
		Map<String, Object> contextParameter = new HashMap<String, Object>();
		if (container.getContextParameter(Constants.ENCOUNTER_DATE) == null)
		{
			contextParameter.put(Constants.ENCOUNTER_DATE, ControlsUtility
					.getFormattedDate(encounterDate));
			container.setContextParameter(contextParameter);
		}
		DynamicExtensionsUtility.setEncounterDateToChildContainer(container, contextParameter);
	}

	private void updateConatinerMap()
	{
		Map<Long, ContainerInterface> containerMap = new HashMap<Long, ContainerInterface>();
		TagUtility.setValidationMap(containerMap, container);
		request.getSession().setAttribute(Constants.MAP_FOR_VALIDATION, containerMap);
	}
	
	public String generateHTML() throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		this.container.setShowRequiredFieldWarningMessage(Boolean.valueOf(request.getParameter(Constants.MANDATORY_MESSAGE).toString()));
		final String caption = (String) request.getSession().getAttribute(
				WebUIManagerConstants.OVERRIDE_CAPTION);
		final String operation = request.getParameter(Constants.DATA_ENTRY_OPERATION);
		return this.container.generateContainerHTML(caption, operation);
	}
}
