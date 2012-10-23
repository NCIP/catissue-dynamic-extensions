package edu.common.dynamicextensions.action.core;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.FormDataCollectionUtility;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.wustl.dao.newdao.ActionStatus;


/**
 * @author Kunal
 *	handle all ajax action like calculated attributes and the skip logic attribute
 */
public class UpdateServerStateServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7582057597503257144L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
			try
			{
				FormDataCollectionUtility collectionUtility = new FormDataCollectionUtility();
				collectionUtility.populateAndValidateValues(request,Boolean.TRUE.toString());
				request.setAttribute(ActionStatus.ACTIONSTAUS, ActionStatus.SUCCESSFUL);	
				forwardRequest(request, response,WebUIManagerConstants.LOAD_DATA_ENTRY_FORM_ACTION_URL);
			}
			catch (DynamicExtensionsSystemException e)
			{
				e.printStackTrace();
			}
			catch (DynamicExtensionsApplicationException e)
			{
				e.printStackTrace();
			}
			
	}

	private void forwardRequest(HttpServletRequest request, HttpServletResponse response, String forwardUrl)
			throws ServletException, IOException
	{
		
		RequestDispatcher rd = getServletContext().getRequestDispatcher(forwardUrl);
		rd.forward(request, response);
	}
	
	
}
