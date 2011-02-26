package edu.common.dynamicextensions.dem;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.dao.exception.DAOException;

public class RelatedAttributeHandler extends AbstractHandler {

	private static final long serialVersionUID = 1072946699572853891L;

	@Override
	protected void doPostImpl(HttpServletRequest req, HttpServletResponse resp)
			throws DAOException, DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException {
		dyanamicObjectProcessor.insertRelatedAttribute(paramaterObjectMap);

	}
}
