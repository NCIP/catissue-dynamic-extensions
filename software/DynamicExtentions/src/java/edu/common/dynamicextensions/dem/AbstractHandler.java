
package edu.common.dynamicextensions.dem;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;

public abstract class AbstractHandler extends HttpServlet implements WebUIManagerConstants
{

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 1L;

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(AbstractHandler.class);
	protected Map<String, Object> paramaterObjectMap;
	public static final String GET_SERVER_CALL = "get.server.call";
	public static final String INIT_PARAMETER_MAP = "init.parameter.map";
	protected DyanamicObjectProcessor dyanamicObjectProcessor;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException
	{
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		try
		{
			dyanamicObjectProcessor = new DyanamicObjectProcessor();
			AuditManager.init("DynamicExtensionsAuditMetadata.xml");
			paramaterObjectMap = readParameterMapFromRequest(req);
			doPostImpl(req, resp);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	protected abstract void doPostImpl(HttpServletRequest req, HttpServletResponse resp)
			throws DAOException, DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException;




	public static Map<String, Object> readParameterMapFromRequest(HttpServletRequest req)
			throws DynamicExtensionsApplicationException, IOException
	{
		ObjectInputStream inputFromServlet = null;
		Map<String, Object> paramaterObjectMap = null;
		try
		{
			inputFromServlet = new ObjectInputStream(req.getInputStream());
			Object object = null;
			while ((object = inputFromServlet.readObject()) != null)
			{
				if (object instanceof Map)
				{
					paramaterObjectMap = (Map<String, Object>) object;
				}
			}
		}
		catch (ClassNotFoundException e)
		{
			LOGGER.info("Exception occured while reading parameter map from request :: "
					+ e.getMessage());
			LOGGER.info("Class Not Found Exception occured :: " + e.getLocalizedMessage());
			throw new DynamicExtensionsApplicationException(
					"Error in reading objects from request", e);
		}
		catch (EOFException e)
		{
			System.out.println("End of file.");
		}
		catch (IOException e)
		{
			LOGGER.info("Exception occured while reading parameter map from request :: "
					+ e.getMessage());
			LOGGER.info("IO Exception occured :: " + e.getLocalizedMessage());
			throw new DynamicExtensionsApplicationException(
					"Error in reading objects from request", e);
		}
		finally
		{
			if (inputFromServlet != null)
			{
				inputFromServlet.close();
			}
		}
		return paramaterObjectMap;
	}

	protected void insertObject(Object object) throws DynamicExtensionsSystemException
	{
		HibernateDAO hibernateDAO = getHibernateDAO();
		try
		{
			hibernateDAO.insert(object);
			hibernateDAO.commit();
		}
		catch (DAOException e)
		{
			LOGGER.info("Exception occured while reading parameter map from request :: "
					+ e.getMessage());
			LOGGER.info("IO Exception occured :: " + e.getCustomizedMsg());
			throw new DynamicExtensionsSystemException("Error occured while inserting object", e);
		}
		finally
		{
			try
			{
				hibernateDAO.closeSession();
			}
			catch (DAOException e)
			{
				LOGGER.info("Exception occured in insert object :: " + e.getMessage());
				LOGGER.info("DAO Exception occured :: " + e.getCustomizedMsg());
				throw new DynamicExtensionsSystemException(
						"Error occured while closing the DAO session", e);
			}
		}

	}

	protected HibernateDAO getHibernateDAO() throws DynamicExtensionsSystemException
	{
		HibernateDAO hibernateDao = null;
		try
		{
			hibernateDao = (HibernateDAO) DAOConfigFactory.getInstance().getDAOFactory("dem")
					.getDAO();
			hibernateDao.openSession(null);
		}
		catch (DAOException e)
		{
			LOGGER.info("Exception occured while getting hibernate DAO :: " + e.getMessage());
			LOGGER.info("DAO Exception occured :: " + e.getCustomizedMsg());
			throw new DynamicExtensionsSystemException(
					"Error occured while opening the DAO session", e);
		}
		return hibernateDao;
	}

	protected void writeObjectToResopnce(Object object, HttpServletResponse res)
			throws DynamicExtensionsApplicationException
	{
		ObjectOutputStream objectOutputStream = null;
		try
		{
			objectOutputStream = new ObjectOutputStream(res.getOutputStream());
			objectOutputStream.writeObject(object);
		}
		catch (IOException e)
		{
			LOGGER.info("Exception occured while writing object to response :: " + e.getMessage());
			LOGGER.info("IO Exception occured :: " + e.getLocalizedMessage());
			throw new DynamicExtensionsApplicationException(
					"Error in writing object to the responce", e);
		}

		finally
		{
			try
			{
				objectOutputStream.flush();
				objectOutputStream.close();
			}
			catch (IOException e)
			{
				LOGGER
						.info("Exception occured while writing object to response, objectOutputStream already closed :: "
								+ e.getMessage());
				LOGGER.info("IO Exception occured :: " + e.getLocalizedMessage());
				throw new DynamicExtensionsApplicationException(
						"Error in writing object to the responce", e);
			}

		}
	}


}
