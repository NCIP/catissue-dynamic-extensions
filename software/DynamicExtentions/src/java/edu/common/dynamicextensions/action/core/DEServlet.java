package edu.common.dynamicextensions.action.core;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.renderer.SurveyModeRenderer;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.ui.webui.util.FormDataCollectionUtility;
import edu.common.dynamicextensions.ui.webui.util.UserInterfaceiUtility;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.FormManager;
import edu.common.dynamicextensions.util.global.DEConstants;

public class DEServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		try {
			
			new SurveyModeRenderer(req).render();
			
		} catch (DynamicExtensionsSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DynamicExtensionsApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req,
			HttpServletResponse res) throws ServletException, IOException {
		long recordId = -1;

		try {

			FormDataCollectionUtility collectionUtility = new FormDataCollectionUtility();
			collectionUtility.populateAndValidateValues(req);

			FormManager formManager = new FormManager();
			recordId = formManager.submitMainFormData(req);
			
			String callbackUrl = req.getParameter(DEConstants.CALLBACK_URL);
			String containerIdentifier = req.getParameter("containerIdentifier");
			String redirectUrl = callbackUrl + 
				"?recordIdentifier=" + String.valueOf(recordId) +
				"&operationStatus=" + DEConstants.SUCCESS +
				"&"+WebUIManagerConstants.CONTAINER_IDENTIFIER+"="+containerIdentifier+"&"+WebUIManagerConstants.ISDRAFT+"="+
				req.getParameter(WebUIManagerConstants.ISDRAFT);

			res.sendRedirect(redirectUrl);
			CacheManager.clearCache(req);
			UserInterfaceiUtility.resetRequestParameterMap(req);

		} catch (DynamicExtensionsSystemException e) {
			e.printStackTrace();
		} catch (DynamicExtensionsApplicationException e) {
			String actionForward;
			actionForward = "/pages/de/dataEntry.jsp";
			RequestDispatcher rd = getServletContext().getRequestDispatcher(actionForward);
			rd.forward(req, res);
		}
	}

}
