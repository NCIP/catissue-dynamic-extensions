
package edu.common.dynamicextensions.action.core;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author sujay_narkar, chetan_patil, suhas_khot
 *
 */
public class LoadDataEntryFormAction extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5063171858302339258L;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException
	{
		doPost(req, resp);
	}
	/* (non-Javadoc)
	 * @see org.apache.struts.actions.DispatchAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/pages/de/dataEntry.jsp");
		rd.forward(request, response);
	}
	
}
