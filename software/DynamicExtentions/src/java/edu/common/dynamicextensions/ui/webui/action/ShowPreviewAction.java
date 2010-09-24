
package edu.common.dynamicextensions.ui.webui.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.ui.webui.actionform.ControlsForm;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.util.global.DEConstants;

public class ShowPreviewAction extends BaseDynamicExtensionsAction
{

	/**
	 * @param mapping ActionMapping mapping
	 * @param form ActionForm form
	 * @param  request HttpServletRequest request
	 * @param response HttpServletResponse response
	 * @return ActionForward forward to next action
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	{
		ContainerInterface containerInterface = (ContainerInterface) CacheManager
				.getObjectFromCache(request, DEConstants.CONTAINER_INTERFACE);
		ControlsForm controlsForm = (ControlsForm) form;

		if ((controlsForm != null) && (containerInterface != null))
		{
			ControlsUtility.reinitializeSequenceNumbers(containerInterface.getControlCollection(),
					controlsForm.getControlsSequenceNumbers());
			ControlsUtility.updateSequencesOfBaseContainers(containerInterface, 1);

		}

		return mapping.findForward("loadFormPreviewAction");
	}

}
