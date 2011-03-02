
package edu.common.dynamicextensions.dem;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.dao.exception.DAOException;

/**
 * @author pathik_sheth
 * Handles the edit functionality of DE record.
 */
public class DataEditHandler extends AbstractHandler
{
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostImpl(HttpServletRequest req, HttpServletResponse resp) throws DAOException, DynamicExtensionsApplicationException, DynamicExtensionsSystemException {

			Object object=dyanamicObjectProcessor.editObject(paramaterObjectMap);
			writeObjectToResopnce(object,resp);

	}


}
