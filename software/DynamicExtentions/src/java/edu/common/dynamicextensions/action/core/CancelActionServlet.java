
package edu.common.dynamicextensions.action.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.ui.webui.util.FormDataCollectionUtility;
import edu.wustl.dao.newdao.ActionStatus;

/**
 * @author Kunal
 *
 */
public class CancelActionServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3915459855176385892L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException
	{
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		FormDataCollectionUtility.popStack(req);
		req.setAttribute(ActionStatus.ACTIONSTAUS, ActionStatus.SUCCESSFUL);
	}
}
