
package edu.common.dynamicextensions.action.core;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import net.sf.ehcache.CacheException;

import org.xml.sax.SAXException;

import edu.common.dynamicextensions.bizlogic.BizLogicFactory;
import edu.common.dynamicextensions.bizlogic.FormObjectGridDataBizLogic;
import edu.common.dynamicextensions.domain.FormGridObject;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.common.dynamicextensions.velocity.VelocityManager;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;

/**
 * @author Amol Pujari
 *
 */
public class LoadDisplayFormDataInGridAction extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -898911816328917103L;

	//This is an ajax call for loading the data in FormDataGrid
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		Long formContextId = Long.valueOf(request.getParameter(DEConstants.FORM_CONTEXT_ID));
		String hookEntityId = (String) request.getParameter(DEConstants.RECORD_ENTRY_ENTITY_ID);
		String formUrl = (String) request.getParameter(DEConstants.FORM_URL);
		String deUrl = (String) request.getParameter(DEConstants.DE_URL);
		String hookObjectRecordIdParameter = request
				.getParameter(DEConstants.HOOK_OBJECT_RECORD_ID);
		Long hookObjectRecordId = null;
		if (!"".equalsIgnoreCase(hookObjectRecordIdParameter))
		{
			hookObjectRecordId = Long.valueOf(hookObjectRecordIdParameter);
		}

		FormObjectGridDataBizLogic displayFormDataInGridBizLogic = (FormObjectGridDataBizLogic) BizLogicFactory
				.getBizLogic(FormObjectGridDataBizLogic.class.getName());

		SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
				DEConstants.SESSION_DATA);

		List<FormGridObject> gridObjectList;
		String responseString = null ;
		try
		{
			System.out.println(":-------------------------------------------------Before fetching data for grid");
			gridObjectList = displayFormDataInGridBizLogic.getFormDataForGrid(
					formContextId, hookEntityId, sessionDataBean, formUrl, deUrl, hookObjectRecordId);
			System.out.println(":-------------------------------------------------After fetching data for grid");

			responseString = VelocityManager.getInstance().evaluate(gridObjectList,
					Constants.VM_TEMPLATE_FILENAME_FOR_FORM_DATA_GRID);
		}
		catch (DynamicExtensionsSystemException e)
		{
			Logger.out.error(e);
		}
		catch (DAOException e)
		{
			Logger.out.error(e);
		}
		catch (DynamicExtensionsApplicationException e)
		{
			Logger.out.error(e);
		}
		catch (BizLogicException e)
		{
			Logger.out.error(e);
		}
		catch (JAXBException e)
		{
			Logger.out.error(e);
		}
		catch (SAXException e)
		{
			Logger.out.error(e);
		}
		catch (CacheException e)
		{
			Logger.out.error(e);
		}
		catch (Exception e)
		{
			Logger.out.error(e);
		}
		
		response.setContentType(Constants.CONTENT_TYPE_XML);
		response.getWriter().write(responseString);
	}

}
