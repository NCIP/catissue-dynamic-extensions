
package edu.common.dynamicextensions.action.core;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.ui.renderer.DEComboDataRenderer;
import edu.wustl.dao.newdao.ActionStatus;

/**
 * @author kunal_kamble
 * This action is called in the auto complete dropdown combo box
 *
 */
public class DEComboDataAction  extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2453832389303155156L;
	/** The Constant CONTROL_ID. */
	private static final String CONTROL_ID = "controlId";

	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		String responseStr = new DEComboDataRenderer().render(request);
		response.flushBuffer();
		PrintWriter out = response.getWriter();
		out.write(responseStr);
		request.setAttribute(ActionStatus.ACTIONSTAUS, ActionStatus.SUCCESSFUL);
	}
}