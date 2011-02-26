
package edu.common.dynamicextensions.handler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.dem.AbstractHandler;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;

public class UpdateCategoryHandler extends AbstractHandler
{

	public UpdateCategoryHandler() throws DAOException
	{
		super();
	}

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	public void updateCategory(Map<String, Object> paramaterObjectMap)
			throws DynamicExtensionsSystemException, DAOException
	{
		HibernateDAO hibernateDao = DynamicExtensionsUtility.getHibernateDAO();
		hibernateDao.merge(paramaterObjectMap.get(WebUIManagerConstants.CATEGORY));
		hibernateDao.commit();
		hibernateDao.closeSession();
	}

	@Override
	protected void doPostImpl(HttpServletRequest req, HttpServletResponse resp)
			throws DAOException, DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException
	{
		updateCategory(paramaterObjectMap);

	}
}