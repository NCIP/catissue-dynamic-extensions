package edu.wustl.dynamicextensions.caching.util;

import oracle.sql.TIMESTAMP;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.DateConverter;

/**
 *
 * @author Vinayak Pawar (vinayak.pawar@krishagni.com)
 */
public class CustomDateConverter implements Converter { 
    private DateConverter converter = new DateConverter();
    
    public Object convert(Class type, Object value) {
        try {
            if (value!=null && value instanceof TIMESTAMP) {
                value = ((TIMESTAMP)value).dateValue();
            }
            
            return converter.convert(type, value);            
        } catch (Exception e) {
            StringBuilder msg = new StringBuilder();
            msg.append("Can't convert ").append(value.toString()).append(" ( ").append(value.getClass().getName())
               .append(") to ").append(type.getName());
            throw new RuntimeException(msg.toString(), e);
        }
    }    
}