package edu.common.dynamicextensions.dem;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;

/**
 * @author pathik_sheth
 * Handles the insert functionality of DE record.
 */
public class DataEntryHandler extends AbstractHandler {
	private static final Logger LOGGER = Logger.getCommonLogger(DataEntryHandler.class);


	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostImpl(HttpServletRequest req, HttpServletResponse resp)
			throws DAOException, DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException {
		Object object = dyanamicObjectProcessor.insertDataEntryForm(paramaterObjectMap);
		writeObjectToResopnce(object, resp);

	}

}
