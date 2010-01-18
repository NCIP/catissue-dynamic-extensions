/**
 *
 */

package edu.common.dynamicextensions.ui.webui.action;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.util.DirOperationsUtility;
import edu.common.dynamicextensions.util.ZipUtility;
import edu.common.dynamicextensions.util.parser.DynamicallyImportPermissibleValues;
import edu.common.dynamicextensions.util.parser.ImportPermissibleValues;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * @author gaurav_mehta
 *
 */
public class ImportPVAction extends BaseDynamicExtensionsAction
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	private static final Logger LOGGER = Logger.getCommonLogger(ImportPVAction.class);

	private static String pvDir = System.getProperty("java.io.tmpdir");

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			ZipUtility.downloadZipFile(request, pvDir, "pv.zip");
			LOGGER.info("Artifacts Downloaded");

			String fileName = request.getParameter(DynamicallyImportPermissibleValues.pvFileName);
			ImportPermissibleValues importPVs = new ImportPermissibleValues(pvDir + File.separator + "tempPVDir" + File.separator
					+ fileName);
			importPVs.importValues();
			sendResponse(response, "PV Uploaded Successfully");

			LOGGER.info("Permissible Values uploaded successfully");
		}
		catch (Exception e)
		{
			LOGGER.info("Exception occured while creating category", e);
			sendResponse(response, e);
		}
		finally
		{
			DirOperationsUtility.getInstance().deleteDirectory(new File(pvDir + File.separator + "tempPVDir"));
		}
		return null;
	}

	/**
	 * This method will send the given responseObject to the caller of this servlet.
	 * @param response HttpServletResponse through which the responseObject should be send.
	 * @param responseObject object to be send.
	 */
	public void sendResponse(HttpServletResponse response, Object responseObject)
	{
		try
		{
			ObjectOutputStream outputToClient = new ObjectOutputStream(response.getOutputStream());
			outputToClient.writeObject(responseObject);
			outputToClient.flush();
			outputToClient.close();
		}
		catch (IOException e)
		{
			LOGGER.info("Exception occured while sending response to java application");
		}

	}
}
