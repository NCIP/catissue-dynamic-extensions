/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author
 *@version 1.0
 */

package edu.common.dynamicextensions.ui.webui.action;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.domain.FileAttributeRecordValue;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeMetadataInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;

/**
 * @author sandeep_chinta
 *
 */

public class DownloadFileAction extends HttpServlet
{

	/**
	 *
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException,
			IOException
	{
		doPost(req, res);
	}

	/**
	 * This method is used to download files saved in database.
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,
			IOException
	{

		String attributeIdentifier = req.getParameter(DEConstants.ATTRIBUTE_IDENTIFIER);
		EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		String recordIdentifier = req.getParameter(DEConstants.RECORD_IDENTIFIER);
		FileAttributeRecordValue fileAttributeRecordValue;
		BaseAbstractAttributeInterface baseAbstractAttribute = EntityCache.getInstance()
				.getBaseAbstractAttributeById(Long.valueOf(attributeIdentifier));
		AttributeInterface attributeInterface = ((AttributeMetadataInterface) baseAbstractAttribute)
				.getAttribute();
		try
		{
			fileAttributeRecordValue = entityManagerInterface
					.getFileAttributeRecordValueByRecordId(attributeInterface, Long
							.valueOf(recordIdentifier));
			byte[] filedata = fileAttributeRecordValue.getFileContent();
			String filename = fileAttributeRecordValue.getFileName();

			//      set the header information in the response.
			res.setHeader("Content-Disposition", "attachment; filename=" + filename + ";");
			res.setContentType("application/x-unknown");
			ByteArrayInputStream byteStream = new ByteArrayInputStream(filedata);
			BufferedInputStream bufStream = new BufferedInputStream(byteStream);

			ServletOutputStream responseOutputStream = null;
			responseOutputStream = res.getOutputStream();
			int data;
			while ((data = bufStream.read()) != -1)
			{
				responseOutputStream.write(data);
			}

			bufStream.close();
			responseOutputStream.close();

		}
		catch (DynamicExtensionsSystemException e)
		{
			Logger.out.error(e.getMessage());
		}
		catch (DynamicExtensionsApplicationException e)
		{
			Logger.out.error(e.getMessage());
		}
		catch (NumberFormatException e)
		{
			Logger.out.error(e.getMessage());
		}
		catch (DAOException e)
		{
			Logger.out.error(e.getMessage());
		}
		catch (SQLException e)
		{
			Logger.out.error(e.getMessage());
		}
	}

}
