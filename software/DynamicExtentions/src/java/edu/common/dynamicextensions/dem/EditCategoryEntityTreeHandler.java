
package edu.common.dynamicextensions.dem;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.dao.exception.DAOException;

public class EditCategoryEntityTreeHandler extends AbstractHandler
{

	/*private final DyanamicObjectProcessor dyanamicObjectProcessor;

	public EditCategoryEntityTreeHandler() throws DAOException
	{
		 dyanamicObjectProcessor = new DyanamicObjectProcessor();
	}

	*//**
	 * Do post method of the servlet
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 * @throws ServletException servlet exception
	 * @throws IOException io exception
	 *//*
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,
		 	IOException
	{

		try {
			*//**
			 * 1. initialize the parameter
			 *//*
			ObjectInputStream inputFromServlet = null;
			try {
				inputFromServlet = new ObjectInputStream(req.getInputStream());
				Object object = null;
				while ((object = inputFromServlet.readObject()) != null) {
					if (object instanceof AbstractEntity) {
						entity = (EntityInterface) object;
					}
					if (object instanceof Map) {
						dataValue = (Map<AbstractAttributeInterface, Object>) object;
					}

				}
			} catch (ClassNotFoundException e) {
				throw new DynamicExtensionsApplicationException(
						"Error in reading objects from request", e);
			} catch (EOFException e) {
				System.out.println("End of file.");
			} catch (IOException e) {
				throw new DynamicExtensionsApplicationException(
						"Error in reading objects from request", e);
			} finally {
				try {
					inputFromServlet.close();
				} catch (IOException e) {
					throw new DynamicExtensionsApplicationException(
							"Error in reading objects from request", e);
				}
			}

			initAuditManager();
			initializeParamaterObjectMap(req);

			Object object = dyanamicObjectProcessor.editRecordsForCategoryEntityTree(paramaterObjectMap);
			writeObjectToResopnce(object,res);

		}
		catch (DAOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (DynamicExtensionsApplicationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (DynamicExtensionsSystemException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

	@Override
	protected void doPostImpl(HttpServletRequest req, HttpServletResponse resp)
			throws DAOException, DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException
	{

		initAuditManager();
		initializeParamaterObjectMap(req);

		Object object = dyanamicObjectProcessor.editRecordsForCategoryEntityTree(paramaterObjectMap);
		writeObjectToResopnce(object,resp);

	}

}
