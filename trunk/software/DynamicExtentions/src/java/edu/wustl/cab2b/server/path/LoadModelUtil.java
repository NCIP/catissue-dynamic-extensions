package edu.wustl.cab2b.server.path;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import edu.wustl.common.util.logger.Logger;

/**
 * 
 * @author chandrakant_talele
 *
 */
public class LoadModelUtil {

    /* Format of properties file
    db.driver=com.mysql.jdbc.Driver;
    db.server=10.99.88.100
    db.port=8080
    db.name=test_database
    db.userName=root
    db.password=password
    max.path.length=6
    #   If you want to load metadata for applications say "app1" and "app2" then 
    #   entity.group.names=app1,app2
    #   app1.file=<Model path for app1>
    #   app2.file=<Model path for app2>
    #
    #   For example, to load metadata for caArray and caTissue, the cab2b.properties will look like
    #   entity.group.names=caArray,caTissue
    #   caArray.file=c:/caArrayModel.xml
    #   caTissue.file=c:/caTissueModel.xml
    entity.group.names=model1,model2
    model1.file=file1.xml
    model2.file=file2.xml 
     */
	/**
	 * Main method to load load model
	 * @param args command line args
	 * @throws Exception
	 */
    public static void main(String[] args) throws Exception {
        if (args.length != 1 || args[0].trim().equals("")) {
        	Logger.out.info("Please provide a property file");
            return;
        }
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(args[0])));

        String driver = properties.getProperty("db.driver");
        String server = properties.getProperty("db.server");
        String port = properties.getProperty("db.port");
        String dbName = properties.getProperty("db.name");
        String userName = properties.getProperty("db.userName");
        String password = properties.getProperty("db.password");
        int maxPathlength = Integer.parseInt(properties.getProperty("max.path.length"));
        String[] entityGroupNames = properties.getProperty("entity.group.names").split(",");

        String url = "jdbc:mysql://" + server + ":" + port + "/" + dbName;
        Connection con = null;
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url, userName, password);
            for (String entityGroupName : entityGroupNames) {
                String xmlFilePath = properties.getProperty(entityGroupName + ".file").trim();
                Logger.out.info("Loading model : " + xmlFilePath);
                PathBuilder.loadSingleModel(con, xmlFilePath, entityGroupName, maxPathlength);
            }
        } finally {
            if (con != null) {
                con.close();
            }
        }
    }
}