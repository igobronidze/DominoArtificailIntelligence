package ge.ai.domino.server.dao.connection;

import ge.ai.domino.util.properties.DAIPropertiesUtil;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil {

    private static final Logger logger = Logger.getLogger(ConnectionUtil.class);

    private static String databaseDriver;

    private static String databaseURL;

    private static String databaseUsername;

    private static String databasePassword;

    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (databaseDriver == null) {
                initParams();
            }
            Class.forName(databaseDriver);
            connection = DriverManager.getConnection(databaseURL, databaseUsername, databasePassword);
        } catch (Exception ex) {
            logger.error("Error occurred while connect db", ex);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            logger.error("Error occurred while close db connection", ex);
        }
    }

    private static void initParams() {
        if (databaseDriver == null) {
            databaseDriver = DAIPropertiesUtil.getProperty("jdbc.driver");
            databaseURL = DAIPropertiesUtil.getProperty("jdbc.url");
            databaseUsername = DAIPropertiesUtil.getProperty("jdbc.username");
            databasePassword = DAIPropertiesUtil.getProperty("jdbc.password");
        }
    }
}
