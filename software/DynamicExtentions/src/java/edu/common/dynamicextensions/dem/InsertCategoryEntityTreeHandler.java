
package edu.common.dynamicextensions.dem;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.entitymanager.AbstractBaseMetadataManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.dao.exception.DAOException;

public class InsertCategoryEntityTreeHandler extends AbstractHandler
{
	private static final long serialVersionUID = 8372781754099488321L;



	@Override
	protected void doPostImpl(HttpServletRequest req, HttpServletResponse resp)
			throws DAOException, DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException {
		try {
			Object object = dyanamicObjectProcessor.insertRecordsForCategoryEntityTree(paramaterObjectMap);
			writeObjectToResopnce(AbstractBaseMetadataManager.getObjectId(object),resp);
		} catch (SecurityException e) {
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

	}



}
