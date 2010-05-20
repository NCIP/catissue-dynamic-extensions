
package edu.common.dynamicextensions.ui.webui.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.JSONException;
import org.json.JSONObject;

import edu.common.dynamicextensions.entitymanager.FileUploadManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.logger.Logger;

/**
 * The Class UploadFileToDB.
 */
public class UploadFileToDB extends BaseDynamicExtensionsAction
{

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
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	{
		long identifier;

		try
		{
			DiskFileItemFactory factory = new DiskFileItemFactory();

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
		return null;
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
