/*
 * Created on Dec 19, 2006
 * @author
 *
 */

package edu.common.dynamicextensions.action.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.wustl.dao.newdao.ActionStatus;

/**
 * @author preeti_munot,suhas_khot
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AjaxcodeHandlerAction extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8401969881356504210L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException
	{
		doPost(req, resp);
	}
	/**
	 * @param mapping ActionMapping mapping
	 * @param form ActionForm form
	 * @param  request HttpServletRequest request
	 * @param response HttpServletResponse response
	 * @return ActionForward forward to next action
	 * @throws DynamicExtensionsApplicationException
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		DEAjaxActionManager actionManager = new DEAjaxActionManager();
		actionManager.manage(request, response);
		
		request.setAttribute(ActionStatus.ACTIONSTAUS, ActionStatus.SUCCESSFUL);
	}
}
