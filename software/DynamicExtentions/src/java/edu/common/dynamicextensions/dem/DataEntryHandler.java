package edu.common.dynamicextensions.dem;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.entitymanager.AbstractBaseMetadataManager;
import edu.common.dynamicextensions.entitymanager.FileQueryBean;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;

public class DataEntryHandler extends AbstractHandler {
	private static final Logger LOGGER = Logger.getCommonLogger(DataEntryHandler.class);


	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostImpl(HttpServletRequest req, HttpServletResponse resp)
			throws DAOException, DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException {

		try {

			EntityInterface entity = (EntityInterface) paramaterObjectMap
					.get(ENTITY);
			Map<AbstractAttributeInterface, Object> dataValue = (Map<AbstractAttributeInterface, Object>) paramaterObjectMap
					.get(DATA_VALUE_MAP);

			Object object = dyanamicObjectProcessor.createObject(entity,
					dataValue);
			insertObject(object);
			List<FileQueryBean> queryListForFile = dyanamicObjectProcessor
					.getQueryListForFileAttributes(dataValue, entity, object);
			dyanamicObjectProcessor.executeQuery(queryListForFile,
					(List<FileQueryBean>) paramaterObjectMap
							.get(FILE_RECORD_QUERY_LIST));
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(DEConstants.IDENTIFIER, AbstractBaseMetadataManager
					.getObjectId(object));
			map.put(FILE_RECORD_QUERY_LIST, paramaterObjectMap
					.get(FILE_RECORD_QUERY_LIST));

			writeObjectToResopnce(map, resp);

		} catch (NoSuchMethodException e) {

			LOGGER.error(e);
		} catch (IllegalAccessException e) {

			LOGGER.error(e);
		} catch (InvocationTargetException e) {

			LOGGER.error(e);
		}

	}

}
