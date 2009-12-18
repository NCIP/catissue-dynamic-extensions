
package edu.common.dynamicextensions.ui.webui.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 *
 * @author Rahul Ner
 */
public class DeleteRecordAction extends BaseDynamicExtensionsAction
{

	/*
	 * (non-Javadoc)
	 * @see org.apache.struts.actions.DispatchAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	{

		/*try
		{String target = null;
			String containerIdentifier = request.getParameter("containerIdentifier");
			Long recordIdentifier = Long.valueOf(request.getParameter("recordIdentifier"));

			ContainerInterface container = DynamicExtensionsUtility
					.getContainerByIdentifier(containerIdentifier);

			DeleteRecordProcessor.getInstance().deleteRecord(container, recordIdentifier);

			saveMessages(request, getSuccessMessage(container.getCaption(), recordIdentifier
					.toString()));

			target = "success";
		}
		catch (Exception e)
		{
			target = catchException(e, request);
			if ((target == null) || (target.equals("")))
			{
				return mapping.getInputForward();
			}
		}*/
		return mapping.findForward("");
	}

	

}
