
package edu.common.dynamicextensions.action.core;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseServletAction extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1834335248861555109L;

	protected void  forward(HttpServletRequest request, HttpServletResponse response,String url)
			throws ServletException, IOException
	{
		String actionForward;
		actionForward = url;
		RequestDispatcher rd = getServletContext().getRequestDispatcher(actionForward);
		rd.forward(request, response);
	}
}
