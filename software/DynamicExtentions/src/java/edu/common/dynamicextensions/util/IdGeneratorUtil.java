
package edu.common.dynamicextensions.util;

import java.util.List;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.IdGenerator;
import edu.common.dynamicextensions.domaininterface.IdGeneratorInterface;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;

/**
 * This is singleton util class is used to generate unique ids.
 * 
 * @author kunal_kamble
 * @version 1.0
 */
public class IdGeneratorUtil
{

	/**
	 * Static instance of the id generator.
	 */
	private static IdGeneratorUtil identGeneratorUtil;

	/**
	 * Private constructor used for making this class singleton
	 */
	private IdGeneratorUtil()
	{
	}

	/**
	 * Returns the instance of the IdGeneratorUtil class.
	 * 
	 * @return idGeneratorUtil singleton instance of the IdGeneratorUtil.
	 */
	public static synchronized IdGeneratorUtil getInstance()
	{
		if (identGeneratorUtil == null)
		{
			identGeneratorUtil = new IdGeneratorUtil();
		}
		return identGeneratorUtil;
	}

	/**
	 * @return next available id
	 * @throws Exception
	 *             any exception occured
	 */
	public static synchronized Long getNextUniqeId()
	{
		Long nextAvailableId = null;
		HibernateDAO hibernateDAO=null;

		try
		{
			hibernateDAO = DynamicExtensionsUtility.getHibernateDAO();
			List list = hibernateDAO.executeQuery("from " + IdGenerator.class.getName());
			if (list.isEmpty())
			{
				nextAvailableId = Long.valueOf(1);
				IdGeneratorInterface idGeneratorObject = DomainObjectFactory.getInstance()
						.createIdGenerator();

				idGeneratorObject.setNextAvailableId(Long.valueOf(2));
				idGeneratorObject.setId(Long.valueOf(1));
				hibernateDAO.insert(idGeneratorObject);				
				hibernateDAO.commit();
				return nextAvailableId;
			}

			IdGeneratorInterface idGenerator = (IdGeneratorInterface) list.get(0);

			nextAvailableId = idGenerator.getNextAvailableId();
			idGenerator.setNextAvailableId(nextAvailableId + 1);

			// Remove idGenerator instance from the session cache.
			//session.evict(idGenerator);
			hibernateDAO.update(idGenerator);
			hibernateDAO.commit();
		}
		catch (Exception orgExp)
		{
			try
			{
				hibernateDAO.rollback();
			}
			catch (DAOException rte)
			{
				Logger.out.debug("Could not rollback transaction", rte);
			}
		}
		finally
		{
			try
			{
				DynamicExtensionsUtility.closeHibernateDAO(hibernateDAO);
			}
			catch(DAOException exception)
			{
				Logger.out.debug("Could not close session.", exception);
			}
		}

		return nextAvailableId;
	}
}