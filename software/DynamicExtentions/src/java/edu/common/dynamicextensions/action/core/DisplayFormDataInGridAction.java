
package edu.common.dynamicextensions.action.core;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import edu.common.dynamicextensions.bizlogic.FormObjectGridDataBizLogic;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.dao.exception.DAOException;

public class DisplayFormDataInGridAction extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException
	{
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		HttpSession session = request.getSession();
		SessionDataBean sessionDataBean = (SessionDataBean) session
				.getAttribute(DEConstants.SESSION_DATA);
		Long containerId;
		try
		{
			containerId = CategoryManager.getInstance().getContainerIdByFormContextId(
					(Long) request.getAttribute(DEConstants.FORM_CONTEXT_ID), sessionDataBean);
			final ContainerInterface containerInterface = EntityCache.getInstance()
					.getContainerById(containerId);
			request.setAttribute(Constants.GRID_HEADERS, FormObjectGridDataBizLogic
					.getDisplayHeader((CategoryEntityInterface) containerInterface
							.getAbstractEntity()));

			String destination = "/pages/de/DisplayFormDataInGrid.jsp";

			RequestDispatcher rd = getServletContext().getRequestDispatcher(destination);
			rd.forward(request, response);

		}
		catch (DynamicExtensionsSystemException e)
		{
			throw new ServletException(e);
		}
		catch (DAOException e)
		{
			throw new ServletException(e);
		}
		catch (JAXBException e)
		{
			throw new ServletException(e);
		}
		catch (SAXException e)
		{
			throw new ServletException(e);
		}
		catch (DynamicExtensionsCacheException e)
		{
			throw new ServletException(e);
		}

	}
}