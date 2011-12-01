package edu.wustl.dynamicextensions.caching.data;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author Vinayak Pawar (vinayak.pawar@krishagni.com)
 */
public class DbUtil {
    private final static String SELECT_COLUMN_QUERY = "select %s from %s where %s = ?";
    
    public static String[] getColumnNames(ResultSetMetaData rsmd) {
        try {
            int columnCount = rsmd.getColumnCount();
            String[] columnNames = new String[columnCount];
            for (int i = 0; i < columnNames.length; ++i) {
                columnNames[i] = rsmd.getColumnName(i + 1);
            }

            return columnNames;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching column names", e);
        }
    }        
    
    public static Object[] fetchRow(ResultSet rs, int columnCount)
    throws Exception {
        Object[] columns = new Object[columnCount];
        for (int i = 0; i < columnCount; ++i) {            
            columns[i] = rs.getObject(i + 1); // TODO: replace this with correct get*
            if (columns[i] instanceof BigDecimal) {
                BigDecimal value = (BigDecimal)columns[i];
                try {
                    columns[i] = value.longValueExact();
                } catch (ArithmeticException ae) {
                    columns[i] = value.doubleValue();
                }                
            } 
        }
        return columns;
    }
        
    public static Object getColumnValue(SessionFactory sessionFactory, String tableName, String columnName, String idColumnName, Object id) {
        Session session = null;    
        Connection jdbcConn = null;        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            session = sessionFactory.openSession();
            jdbcConn = session.connection();
            stmt = jdbcConn.prepareStatement(String.format(SELECT_COLUMN_QUERY, columnName, tableName, idColumnName));
            stmt.setObject(1, id);
            rs = stmt.executeQuery();
            
            Object value = null;
            if (rs != null && rs.next()) {
                value = rs.getObject(1);
            }
            
            return value;            
        } catch (Exception e) {
            throw new RuntimeException("Error reading " + tableName + "." + columnName);
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (Exception e1) {    }
            }
            
            if (stmt != null) {
                try { stmt.close(); } catch (Exception e2) {  }
            }
            
            if (jdbcConn != null) {
                try { jdbcConn.close();    } catch (Exception e3) { }
            }
            
            if (session != null) {
                try { session.close(); } catch (Exception e4) {    }
            }
        }
    }    
}