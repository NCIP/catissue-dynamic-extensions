package edu.wustl.dynamicextensions.caching.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.beanutils.BeanUtilsBean;

/**
 *
 * @author Vinayak Pawar (vinayak.pawar@krishagni.com)
 */
public class ReflectionUtil {
    private static ConcurrentMap<String, Class> classCache = new ConcurrentHashMap<String, Class>();
    
    private static final BeanUtilsBean beanUtils = BeanUtilsBean.getInstance();
    
    static {
        beanUtils.getConvertUtils().register(new CustomDateConverter(), Date.class);
    }    
    
    public static Class getClass(String className) {
        Class klass = classCache.get(className);
        if (klass == null) {
            try {
                klass = Class.forName(className);
                classCache.put(className, klass);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        return klass;
    }
    
    public static Object convert(Object value, String type) {
        Class klass = getClass(type);
        return convert(value, klass);
    }
    
    public static Object convert(Object value, Class type) {
        return beanUtils.getConvertUtils().convert(value, type);
    }
    
    public static boolean isAbstract(String className) {
        try {
            Class klass = getClass(className);
            return Modifier.isAbstract(klass.getModifiers());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Constructor getDefaultConstructor(String className) {
        try {
            Class klass = getClass(className);
            return klass.getConstructor(null);
        } catch (NoSuchMethodException nsme) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Field getField(Class klass, String name) {
        if (klass == null) {
            return null;
        }
        
        Field field = null;
        while (klass != null && field == null) {            
            try {
                field = klass.getDeclaredField(name);
            } catch (NoSuchFieldException nsfe) {
                // ok, fine
            }
            
            klass = klass.getSuperclass();            
        }
        
        return field;
    }    
    
    public static Method getMethod(Class klass, String name, Class[] parameterTypes) {
        if (klass == null) {
            return null;
        }
        
        Method method = null;
        while (klass != null && method == null) {
            try {
                method = klass.getDeclaredMethod(name, parameterTypes);
            } catch (NoSuchMethodException nsme) {
                // swallow... burp! burp!! burp!!!
            }
            
            klass = klass.getSuperclass();
        }
        
        return method;
    }
    
    public static Class getPropertyType(Object object, String name) {    
        try {
            Class targetType = beanUtils.getPropertyUtils().getPropertyType(object, name);
            if (targetType == null) {
                Field field = getField(object.getClass(), name);
                if (field != null) {
                    targetType = field.getType();
                }
            }
            
            return targetType;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }            
    
    public static Object getProperty(Object obj, String name) {
        try {
            return beanUtils.getPropertyUtils().getProperty(obj, name);
        } catch (Exception e) {
            if (e instanceof NoSuchMethodException || e instanceof IllegalAccessException) {
                Field field = getField(obj.getClass(), name);
                if (field == null) {
                    throw new RuntimeException(e);
                }
                
                field.setAccessible(true);
                try {
                    return field.get(obj);
                } catch (Exception e1) {
                    throw new RuntimeException(e1);
                }                
            }
            
            throw new RuntimeException(e);
        }
    }
    
    public static void setProperty(Object obj, String name, Object value) {
        try {
            beanUtils.getPropertyUtils().setProperty(obj, name, value);
        } catch (Exception e) {
            if (e instanceof NoSuchMethodException || e instanceof IllegalAccessException) {                                
                Field field = getField(obj.getClass(), name);
                if (field == null) {
                    throw new RuntimeException(e);
                }
                
                field.setAccessible(true);
                try {
                    field.set(obj, value);
                    return;
                } catch (Exception e1) {
                    throw new RuntimeException(e1);
                }                
            }
            
            throw new RuntimeException(e);
        }
    }    
    
    public static Object invokeMethod(Class klass, String methodName, Object ... args) {
        Class[] parameterTypes = null;        
        if (args != null) {
            parameterTypes = new Class[args.length];            
            for (int i = 0; i < args.length; ++i) {
                parameterTypes[i] = args[i].getClass();
            }
        }
        
        try {
            Method method = klass.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(klass, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }                
    }         
}