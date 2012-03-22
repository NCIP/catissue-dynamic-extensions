package edu.common.dynamicextensions.action.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.util.FormManager;
import edu.common.dynamicextensions.util.global.DEConstants;


public class BreadCrumbServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException,
			IOException
	{
		FormManager formManager = new FormManager();
		String breadCrumbPosition = request.getParameter(DEConstants.BREAD_CRUMB_POSITION);
		formManager.onBreadCrumbClick(request, breadCrumbPosition);
		
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse resp)
			throws ServletException, IOException
	{
		doGet(request, resp);
	}

}
