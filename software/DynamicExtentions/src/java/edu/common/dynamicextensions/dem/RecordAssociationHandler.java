package edu.common.dynamicextensions.dem;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.wustl.dao.exception.DAOException;

public class RecordAssociationHandler extends AbstractHandler {

	private static final long serialVersionUID = 6447491182179628135L;

	@Override
	protected void doPostImpl(HttpServletRequest req, HttpServletResponse resp)
			throws DAOException, DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException {

		dyanamicObjectProcessor.associateRecord(paramaterObjectMap);
		writeObjectToResopnce(paramaterObjectMap.get(WebUIManagerConstants.ASSOCIATION),resp);
	}
}
