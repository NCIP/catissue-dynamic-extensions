
package edu.common.dynamicextensions.dem;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.dao.exception.DAOException;

public class DataEditHandler extends AbstractHandler
{
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostImpl(HttpServletRequest req, HttpServletResponse resp) throws DAOException, DynamicExtensionsApplicationException, DynamicExtensionsSystemException {

			EntityInterface entity = (EntityInterface) paramaterObjectMap.get(ENTITY);
			Map<AbstractAttributeInterface, Object> dataValue = (Map<AbstractAttributeInterface, Object>) paramaterObjectMap.get(DATA_VALUE_MAP);
			Long recordId=(Long)paramaterObjectMap.get("recordId");
			Object object = dyanamicObjectProcessor.editObject(entity, dataValue,recordId);
			writeObjectToResopnce(object,resp);

	}


}
