
package edu.wustl.cab2b.server.cache;

import edu.wustl.cab2b.common.cache.AbstractEntityCache;

/**
 * This class is used to cache the Entity and its Attribute objects.
 *
 * @author Chandrakant Talele
 * @author gautam_shetty
 * @author Rahul Ner
 */
public class EntityCache extends AbstractEntityCache
{

	private static final long serialVersionUID = 1234567890L;

	private EntityCache()
	{
		super();
	}

	/**
	 * @return the singleton instance of the EntityCache class.
	 */
	public static synchronized EntityCache getInstance()
	{
		if (entityCache == null)
		{
			entityCache = new EntityCache();
		}
		return (EntityCache) entityCache;
	}

}
