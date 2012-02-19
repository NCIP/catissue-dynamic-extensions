package edu.common.dynamicextensions.ui.webui.taglib;

import java.util.Map;

import edu.common.dynamicextensions.domain.userinterface.AbstractContainmentControl;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;


public class TagUtility
{
	/**
	 *
	 * @param containerMap
	 * @param container
	 */
	public static void setValidationMap(Map<Long, ContainerInterface> containerMap,
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
