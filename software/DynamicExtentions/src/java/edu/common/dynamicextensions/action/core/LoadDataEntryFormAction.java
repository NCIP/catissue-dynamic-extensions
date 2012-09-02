
package edu.common.dynamicextensions.action.core;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.domain.Category;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.FormCache;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.cab2b.server.util.DynamicExtensionUtility;

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
		
		/*try
		{
		
		}
		catch (DynamicExtensionsCacheException cacheException)
		{
			List<String> list = new ArrayList<String>();
			list.add(cacheException.getMessage());
			dataEntryForm.setErrorList(list);
			return mapping.findForward(WebUIManagerConstants.CACHE_ERROR);
		}
*/
		String destination = "/pages/de/dataEntry/dataEntry.jsp";

		Category category;
		try {
			category = getCategory(request);
			if (category.getLayout() != null) {
				FormCache formCache = new FormCache(request);
				formCache.onFormLoad();
				request.getSession().setAttribute(DEConstants.CATEGORY, category);
				request.getSession().setAttribute(DEConstants.CONTAINER, null);
				destination = "/pages/de/surveymode.jsp?categoryId=" + String.valueOf(category.getId().longValue());
			}
		} catch (DynamicExtensionsCacheException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DynamicExtensionsSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DynamicExtensionsApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				 
		RequestDispatcher rd = getServletContext().getRequestDispatcher(destination);
		rd.forward(request, response);
	}
	
	private Category getCategory (HttpServletRequest request) throws DynamicExtensionsCacheException, NumberFormatException {
		String containerId = request.getParameter(DEConstants.CONTAINER_IDENTIFIER);
		return DynamicExtensionUtility.getCategoryByContainerId(containerId);
	}

	
}
