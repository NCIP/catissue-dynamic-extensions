package edu.common.dynamicextensions.dem;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.dao.exception.DAOException;

public class RelatedAttributeHandler extends AbstractHandler {

	private final DyanamicObjectProcessor dyanamicObjectProcessor;

	public RelatedAttributeHandler() throws DAOException
	{
		 dyanamicObjectProcessor = new DyanamicObjectProcessor();
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		try {

			initAuditManager();
			initializeParamaterObjectMap(req);
			dyanamicObjectProcessor.insertRelatedAttribute(paramaterObjectMap);

		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DynamicExtensionsApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DynamicExtensionsSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (NoSuchMethodException e)
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

	}
}
