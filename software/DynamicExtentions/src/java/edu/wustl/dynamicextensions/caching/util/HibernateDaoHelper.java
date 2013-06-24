package edu.wustl.dynamicextensions.caching.util;

import java.lang.reflect.Method;

import org.hibernate.SessionFactory;

import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.connectionmanager.IConnectionManager;

public class HibernateDaoHelper {
    public static SessionFactory getSessionFactory(HibernateDAO dao) {
	    try {
		    Method method = ReflectionUtil.getMethod(dao.getClass(), "getConnectionManager", null);
			method.setAccessible(true);
			IConnectionManager connMgr = (IConnectionManager)method.invoke(dao, (Object[])null);
			return connMgr.getSessionFactory();
		} catch (Exception e) {
		    throw new RuntimeException("Error obtaining session factory", e);
		}
	}
}	