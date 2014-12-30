
package edu.wustl.dynamicextensions.formdesigner.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import edu.common.dynamicextensions.domain.nui.Container;
import edu.common.dynamicextensions.domain.nui.UserContext;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dynamicextensions.formdesigner.mapper.Properties;
import edu.wustl.dynamicextensions.formdesigner.resource.facade.ContainerFacade;
import edu.wustl.dynamicextensions.formdesigner.usercontext.AppUserContextProvider;
import edu.wustl.dynamicextensions.formdesigner.usercontext.CSDProperties;
import edu.wustl.dynamicextensions.formdesigner.utility.CSDConstants;
import edu.wustl.dynamicextensions.formdesigner.utility.Utility;

@Path("/form")
public class Form {

	private static final String CONTAINER_SESSION_ATTR = "sessionContainer";

	private JDBCDAO jdbcDao;

	private HibernateDAO hibernateDao;

	private static AtomicInteger formCnt = new AtomicInteger();

	/**
	 * Create a new Form
	 * @param formJson
	 * @param request
	 * @param response
	 * @return
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createForm(String formJson, final @Context HttpServletRequest request,
			@Context HttpServletResponse response) throws JSONException {

		JSONObject formJSON = new JSONObject();
		String jsonResponseString = "";

		try {
			UserContext userData = CSDProperties.getInstance().getUserContextProvider().getUserContext(request);
			Properties formProps = new Properties(new ObjectMapper().readValue(formJson, HashMap.class));
			ContainerFacade containerFacade = ContainerFacade.createContainer(formProps, userData);
			request.getSession().removeAttribute(CONTAINER_SESSION_ATTR);
			request.getSession().setAttribute(CONTAINER_SESSION_ATTR, containerFacade);
			String save = formProps.getString("save");

			if (save.equalsIgnoreCase("yes")) {
				intializeDao();
				containerFacade.persistContainer();
				commitDao();
			}

			formProps = containerFacade.getProperties();
			formProps.setProperty(CSDConstants.STATUS, CSDConstants.STATUS_SAVED);
			Writer strWriter = new StringWriter();
			new ObjectMapper().writeValue(strWriter, formProps.getAllProperties());
			jsonResponseString = strWriter.toString();

		} catch (Exception ex) {
			ex.printStackTrace();
			formJSON.put("status", "error");
			jsonResponseString = formJSON.toString();
		} finally {
			closeDao();
		}

		return jsonResponseString;

	}

	/**
	 * Retrieve a Form
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 * @throws JSONException
	 */
	@Path("/{id}/{edit}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getForm(@PathParam("id") String id, @PathParam("edit") boolean edit,
			final @Context HttpServletRequest request, @Context HttpServletResponse response) throws JSONException {
		System.out.println(id);
		try {
			intializeDao();
			UserContext userData = CSDProperties.getInstance().getUserContextProvider().getUserContext(request);
			ContainerFacade container = ContainerFacade.loadContainer(Long.valueOf(id), userData, edit);

			request.getSession().setAttribute(CONTAINER_SESSION_ATTR, container);
			Properties containerProps = container.getProperties();
			if (container.getCreatedBy() != null) {
				if (container.getCreatedBy().equals(userData.getUserId())) {
					edit = true;
				}
			}
			if (edit) {
				containerProps.setProperty(CSDConstants.STATUS, CSDConstants.STATUS_SAVED);
			} else {
				containerProps.setProperty(CSDConstants.STATUS, CSDConstants.STATUS_NEW);
			}

			Writer strWriter = new StringWriter();
			new ObjectMapper().writeValue(strWriter, containerProps.getAllProperties());
			return strWriter.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return "{'status' : 'error'}";
		} finally {
			closeDao();
		}

	}

	/**
	 * Renders preview
	 * @param request
	 * @param response
	 * @return
	 * @throws JSONException
	 */
	@Path("/preview")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getPreview(final @Context HttpServletRequest request, @Context HttpServletResponse response)
			throws JSONException {
		try {
			ContainerFacade containerFacade = (ContainerFacade) request.getSession().getAttribute(
					CONTAINER_SESSION_ATTR);

			File previewFile = new File(request.getSession().getServletContext().getRealPath(File.separator)
					+ "csd_web" + File.separator + "pages" + File.separator + "preview.html");

			FileInputStream previewFileInputStream = new FileInputStream(previewFile);

			String previewString = IOUtils.toString(previewFileInputStream, "UTF-8");

			return previewString.replace("{{content}}",
					containerFacade.getHTML(request).replaceAll("images/", "../../images/"));
		} catch (Exception ex) {
			ex.printStackTrace();
			return "Error";
		} finally {
			//request.getSession().removeAttribute(CONTAINER_SESSION_ATTR);
		}
	}

	/**
	 * Edit a Form
	 * @param formJson
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String editForm(String formJson, final @Context HttpServletRequest request,
			@Context HttpServletResponse response) {
		try {
			Properties formProps = new Properties(new ObjectMapper().readValue(formJson, HashMap.class));
			String save = formProps.getString("save");
			ContainerFacade container = (ContainerFacade) request.getSession().getAttribute(CONTAINER_SESSION_ATTR);
			container.updateContainer(formProps);
			if (save.equalsIgnoreCase("yes")) {
				intializeDao();
				container.persistContainer();
				commitDao();
			}
			Writer strWriter = new StringWriter();
			new ObjectMapper().writeValue(strWriter, container.getProperties().getAllProperties());
			return strWriter.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return "{'status' : 'error'}";
		} finally {
			closeDao();
		}

	}

	/**
	 * upload permissible values
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	@POST
	@Path("/permissibleValues")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public String uploadFile(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		String output = "{\"status\" : \"error\"}";
		try {
			// get temp location programatically.
			if (fileDetail.getFileName() != null) {
				String uploadedFileLocation = "/tmp/" + new Date().getTime() + fileDetail.getFileName();
				Utility.saveStreamToFileInTemp(uploadedInputStream, uploadedFileLocation);
				output = "{\"status\": \"saved\", \"file\" : \"" + uploadedFileLocation + "\"}";
			}
		} catch (Exception ex) {
			return output;
		}

		return output;

	}

	@GET
	@Path("/currentuser")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCurrentUser(@Context HttpServletRequest request, @Context HttpServletResponse response)
			throws IOException, JSONException {
		AppUserContextProvider contextProvider = CSDProperties.getInstance().getUserContextProvider();
		JSONObject responseJSON = new JSONObject();
		responseJSON.put("userName",
				contextProvider.getUserNameById(contextProvider.getUserContext(request).getUserId()));

		return responseJSON.toString();

	}

	@GET
	@Path("/permissibleValues/{controlName}")
	@Produces("text/plain")
	public Response getPermissibleValues(@PathParam("controlName") String controlName,
			@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {

		ContainerFacade container = (ContainerFacade) request.getSession().getAttribute(CONTAINER_SESSION_ATTR);
		File pvFile = container.getPvFile(controlName);
		if (pvFile == null) {
			return Response.serverError().build();
		} else {
			ResponseBuilder fileResponse = Response.ok((Object) pvFile);
			fileResponse.header("Content-Disposition", "attachment; filename=\"" + pvFile.getName() + "\"");
			return fileResponse.build();
		}

	}

	@POST
	@Path("/formxml")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public String importForm(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail, final @Context HttpServletRequest request) throws JSONException {
		JSONObject output = new JSONObject();

		String tmpDirName = getTmpDirName();
		Boolean createTables = false;
		try {

			Utility.downloadFile(uploadedInputStream, tmpDirName, "forms.xml", false);

			//
			// Once the zip is extracted, following will be directory layout
			// temp-dir
			//   |___ form-dir (created from zip)
			//           |____ form1.xml
			//           |____ form2.xml
			//           |____ pvs
			//
			File tmpDir = new File(tmpDirName);
			//for (File formDir : tmpDir.listFiles()) {

			String[] formFileNames = tmpDir.list(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".xml");
				}
			});

			String formDirPath = new StringBuilder(tmpDir.getAbsolutePath()).append(File.separator).toString();
			String pvDirPath = new StringBuilder(formDirPath).append("pvs").toString();
			List<Long> containerIds = new ArrayList<Long>();
			intializeDao();
			UserContext userData = CSDProperties.getInstance().getUserContextProvider().getUserContext(request);
			for (String formFile : formFileNames) {
				String formFilePath = new StringBuilder(formDirPath).append(formFile).toString();

				Long containerId = Container.createContainer(formFilePath, pvDirPath, createTables, userData);
				
				containerIds.add(containerId);
			}
			commitDao();
			output.put("status", "success");
			output.put("containerIds", containerIds);

			//	}

		} catch (Exception ex) {
			output.put("status", "error");
		} finally {
			closeDao();
		}

		return output.toString();
	}

	/**
	 * @throws DAOException
	 */
	private void commitDao() throws DAOException {
		try {
			jdbcDao.commit();
			hibernateDao.commit();
		} finally {
			closeDao();
		}
	}

	/**
	 * 
	 */
	private void closeDao() {
		DynamicExtensionsUtility.closeDAO(jdbcDao, false);
		DynamicExtensionsUtility.closeDAO(hibernateDao, false);
	}

	/**
	 * @throws DynamicExtensionsSystemException
	 */
	@SuppressWarnings("deprecation")
	private void intializeDao() throws DynamicExtensionsSystemException {
		jdbcDao = DynamicExtensionsUtility.getJDBCDAO();
		hibernateDao = DynamicExtensionsUtility.getHibernateDAO();
	}

	private String getTmpDirName() {
		return new StringBuilder().append(System.getProperty("java.io.tmpdir")).append(File.separator)
				.append(System.currentTimeMillis()).append(formCnt.incrementAndGet()).append("create").toString();
	}
}
