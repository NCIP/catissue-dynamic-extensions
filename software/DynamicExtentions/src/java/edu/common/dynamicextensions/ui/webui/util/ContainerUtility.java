
package edu.common.dynamicextensions.ui.webui.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import edu.common.dynamicextensions.domain.userinterface.AbstractContainmentControl;
import edu.common.dynamicextensions.domain.userinterface.CategoryAssociationControl;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.skiplogic.SkipLogic;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.util.DataValueMapUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.cab2b.server.cache.EntityCache;

public class ContainerUtility
{

	private ContainerInterface container;
	private HttpServletRequest request;
	private int controlCount = 0;
	private int emptyControlCount = 0;

	public ContainerUtility(HttpServletRequest request, ContainerInterface container)
	{
		this.request = request;
		this.container = container;

		//Used for PV versioning
		updateActivationDate();

	}

	private void updateActivationDate()
	{
		final String encounterDate = request.getParameter(Constants.ENCOUNTER_DATE);
		Map<String, Object> contextParameter = new HashMap<String, Object>();
		if (container.getContextParameter(Constants.ENCOUNTER_DATE) == null)
		{
			contextParameter.put(Constants.ENCOUNTER_DATE, ControlsUtility
					.getFormattedDate(encounterDate));
			container.setContextParameter(contextParameter);
		}
		DynamicExtensionsUtility.setEncounterDateToChildContainer(container, contextParameter);
	}

	private void htmlGenerationPreProcess()
	{
		ContainerInterface container = FormCache.getTopContainer(request);
		Map<BaseAbstractAttributeInterface, Object> dataValueMap = FormCache
				.getTopDataValueMap(request);
		container.setPreviousValueMap(dataValueMap);
		final Set<AttributeInterface> attributes = new HashSet<AttributeInterface>();
		UserInterfaceiUtility.addPrecisionZeroes(dataValueMap, attributes);
		DataValueMapUtility.updateDataValueMapDataLoading(dataValueMap, container);
	}

	public String generateHTML() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		htmlGenerationPreProcess();
		if (request.getParameter(Constants.MANDATORY_MESSAGE) != null)
		{
			this.container.setShowRequiredFieldWarningMessage(Boolean.valueOf(request.getParameter(
					Constants.MANDATORY_MESSAGE).toString()));
		}
		final String caption = (String) request.getSession().getAttribute(
				WebUIManagerConstants.OVERRIDE_CAPTION);
		final String operation = request.getParameter(Constants.DATA_ENTRY_OPERATION);
		return this.container.generateContainerHTML(caption, operation);
	}

	public static void populateControlValueCollection(
			List<Map<ControlInterface, Object>> controlValueCollection,
			ContainerInterface container, Map<BaseAbstractAttributeInterface, Object> dataValueMap)
	{
		for (ControlInterface control : container.getAllControlsUnderSameDisplayLabel())
		{
			if (control instanceof AbstractContainmentControl)
			{
				if (((AbstractContainmentControl) control).isCardinalityOneToMany())
				{
					//TODO:One to many case yet to handled	
				}
				else
				{
					List<Map<BaseAbstractAttributeInterface, Object>> list = (List<Map<BaseAbstractAttributeInterface, Object>>) dataValueMap
							.get(control.getBaseAbstractAttribute());
					AbstractContainmentControlInterface abstractContainmentControl = (AbstractContainmentControlInterface) control;
					if (list.isEmpty())
					{
						list.add(new HashMap<BaseAbstractAttributeInterface, Object>());
					}
					populateControlValueCollection(controlValueCollection,
							abstractContainmentControl.getContainer(), list.get(0));

				}
			}
			else
			{
				Map<ControlInterface, Object> map = new HashMap<ControlInterface, Object>();
				map.put(control, dataValueMap.get(control.getBaseAbstractAttribute()));
				controlValueCollection.add(map);
			}

		}
	}

	/**
	 * @param containerInterface with data value map updated
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 */
	public void initCountrolCounts(ContainerInterface containerInterface)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		for (ControlInterface controlInterface : containerInterface.getControlCollection())
		{
			if (controlInterface instanceof AbstractContainmentControlInterface)
			{
				CategoryAssociationControl containmentControl = (CategoryAssociationControl) controlInterface;
				if (!containmentControl.isCardinalityOneToMany())
				{
					List<Map<BaseAbstractAttributeInterface, Object>> maps = (List<Map<BaseAbstractAttributeInterface, Object>>) containerInterface
							.getContainerValueMap().get(controlInterface);
					containmentControl.getContainer().setContainerValueMap(maps.get(0));
					initCountrolCounts(containmentControl.getContainer());
				}
			}
			else
			{
				if (!controlInterface.getIsHidden())
				{
					controlInterface.setValue(containerInterface.getContainerValueMap().get(
							controlInterface.getBaseAbstractAttribute()));
					controlCount++;
					if (controlInterface.isEmpty())
					{
						emptyControlCount++;
					}
				}

			}
		}
	}

	public int getControlCount()
	{
		return controlCount;
	}

	public int getEmptyControlCount()
	{
		return emptyControlCount;
	}

	public static void evaluateSkipLogic(ContainerInterface container)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		SkipLogic skipLogic = EntityCache.getInstance().getSkipLogicByContainerIdentifier(container.getId());
		if (skipLogic != null)
		{
			skipLogic.evaluateSkipLogic(container, container.getContainerValueMap());
		}
	}
}
