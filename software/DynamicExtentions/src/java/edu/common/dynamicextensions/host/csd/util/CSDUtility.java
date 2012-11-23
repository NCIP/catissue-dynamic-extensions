
package edu.common.dynamicextensions.host.csd.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.common.dynamicextensions.entitymanager.AbstractBaseMetadataManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.dao.util.NamedQueryParam;

/**
 * This class is specific to the CSD specific utility methods.
 * @author pavan_kalantri
 *
 */
public class CSDUtility
{

	@SuppressWarnings("unchecked")
	public static Collection getAllCategoriesInfo() throws DynamicExtensionsSystemException
	{
		Collection categoryDetails = AbstractBaseMetadataManager.executeHQL("getAllCategoriesInfo",
				new HashMap<String, NamedQueryParam>());
		return categoryDetails;
	}
	
	@SuppressWarnings("unchecked")
	public static Collection getAllUsersCategoriesInfo(Map<String, NamedQueryParam> param) throws DynamicExtensionsSystemException
	{
		Collection categoryDetails = AbstractBaseMetadataManager.executeHQL("getAllUsersCategoriesInfo",param);
		return categoryDetails;
	}
}