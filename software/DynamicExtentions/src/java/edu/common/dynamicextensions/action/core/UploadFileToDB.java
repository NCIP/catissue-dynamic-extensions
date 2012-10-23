
package edu.common.dynamicextensions.action.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONException;
import org.json.JSONObject;

import edu.common.dynamicextensions.entitymanager.FileUploadManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.newdao.ActionStatus;

/**
 * The Class UploadFileToDB.
 */
public class UploadFileToDB  extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -331996445389885677L;
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(UploadFileToDB.class);

	/**
	 * Execute.
	 *
	 * @param mapping the mapping
	 * @param form the form
	 * @param request the request
	 * @param response the response
	 *
	 * @return the action forward
	 *
	 * @see org.apache.struts.actions.DispatchAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		long identifier;

		try
		{
			DiskFileItemFactory factory = new DiskFileItemFactory();
			response.setContentType("text/html");
			ServletFileUpload upload = new ServletFileUpload(factory);

			List<FileItem> items = upload.parseRequest(request);
			Iterator<FileItem> iter = items.iterator();
			FileUploadManager fileUploadManager = new FileUploadManager();
			List<JSONObject> responseList = new ArrayList<JSONObject>();

			FileItem item = iter.next();
			String contentType = item.getContentType();
			byte[] fileContent = item.get();

			identifier = fileUploadManager.uploadFile(fileContent);
			updateResponse(response, identifier, responseList, contentType);
			request.setAttribute(ActionStatus.ACTIONSTAUS, ActionStatus.SUCCESSFUL);
		}
		catch (FileUploadException e)
		{
			LOGGER.info("Error occured while uploading file to database");
			try
			{
				throw new DynamicExtensionsSystemException("Exception occured while uploading file.", e);
			}
			catch (DynamicExtensionsSystemException e1)
			{
				LOGGER.info("Error occured while uploading file to database", e);
			}
		}
		catch (DynamicExtensionsSystemException e)
		{
			LOGGER.info("Error occured while uploading file to database", e);
		}
	}

	/**
	 * Update response.
	 *
	 * @param response the response
	 * @param identifier the identifier
	 * @param responseList the response list
	 * @param contentType the content type
	 * @throws DynamicExtensionsSystemException
	 *
	 */
	private void updateResponse(HttpServletResponse response, long identifier,
			List<JSONObject> responseList, String contentType) throws DynamicExtensionsSystemException
	{
		try
		{
			response.flushBuffer();

			responseList.add(new JSONObject().put("uploadedFileId", identifier));
			responseList.add(new JSONObject().put("contentType", contentType));

			response.getWriter().write(
					new JSONObject().put("uploadedFile", responseList).toString());
		}
		catch (IOException e)
		{
			LOGGER.info("Error occured while writing response object");
			throw new DynamicExtensionsSystemException("Exception occured while uploading file.", e);
		}
		catch (JSONException e)
		{
			LOGGER.info("Error occured while writing JSON object in response");
			throw new DynamicExtensionsSystemException("Exception occured while uploading file.", e);
		}
	}
}
