
package edu.common.dynamicextensions.dem;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.entitymanager.AbstractBaseMetadataManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;

public class FileAttributesHandler extends AbstractHandler
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getCommonLogger(FileAttributesHandler.class);


	@Override
	protected void doPostImpl(HttpServletRequest req, HttpServletResponse resp)
			throws DAOException, DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException {
		try {

			Object objectFromRequest = paramaterObjectMap.get("object");
			EntityInterface entity = (EntityInterface) paramaterObjectMap.get(ENTITY);
			Map<AbstractAttributeInterface, Object> dataValue = (Map<AbstractAttributeInterface, Object>) paramaterObjectMap.get(DATA_VALUE_MAP);
			Object object = dyanamicObjectProcessor.getQueryListForFileAttributes(dataValue, entity,objectFromRequest);
			writeObjectToResopnce(AbstractBaseMetadataManager.getObjectId(object),resp);

		} catch (NoSuchMethodException e) {

			LOGGER.error(e);
		} catch (IllegalAccessException e) {

			LOGGER.error(e);
		} catch (InvocationTargetException e) {

			LOGGER.error(e);
		}

	}


}
