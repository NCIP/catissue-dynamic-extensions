
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
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.AuditException;
import edu.wustl.dao.exception.DAOException;

public abstract class AbstractHandler extends HttpServlet implements WebUIManagerConstants
{
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(AbstractHandler.class);
	protected Map<String, Object> paramaterObjectMap;
	protected DyanamicObjectProcessor dyanamicObjectProcessor;


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException
	{
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException
	{
		try {
			dyanamicObjectProcessor = new DyanamicObjectProcessor();
			initAuditManager();
			initializeParamaterObjectMap(req);
			doPostImpl(req, resp);
		} catch (DynamicExtensionsApplicationException e) {
			LOGGER.error(e);
		} catch (DAOException e) {
			LOGGER.error(e);
		} catch (DynamicExtensionsSystemException e) {
			LOGGER.error(e);
		}
	}

	protected abstract void doPostImpl(HttpServletRequest req, HttpServletResponse resp) throws DAOException, DynamicExtensionsApplicationException, DynamicExtensionsSystemException;

	protected void initAuditManager()
	{
		try
		{
			AuditManager.init("DynamicExtensionsAuditMetadata.xml");
		}
		catch (AuditException e1)
		{
			LOGGER.error("Error initializing audit manager",e1);
		}
	}

	protected void initializeParamaterObjectMap(HttpServletRequest req)
			throws DynamicExtensionsApplicationException
	{
		paramaterObjectMap = readParameterMapFromRequest(req);

	}

	public static Map<String, Object> readParameterMapFromRequest(HttpServletRequest req)
			throws DynamicExtensionsApplicationException
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
			throw new DynamicExtensionsApplicationException(
					"Error in reading objects from request", e);
		}
		catch (EOFException e)
		{
			System.out.println("End of file.");
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsApplicationException(
					"Error in reading objects from request", e);
		}
		finally
		{
			try
			{
				inputFromServlet.close();
			}
			catch (IOException e)
			{
				throw new DynamicExtensionsApplicationException(
						"Error in reading objects from request", e);
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
				throw new DynamicExtensionsApplicationException(
						"Error in writing object to the responce", e);
			}

		}
	}
}
