package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/it_asset_helpdesk";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = readConfig("DB_URL", "db.url", DEFAULT_URL);
        String user = readConfig("DB_USER", "db.user", DEFAULT_USER);
        String password = readConfig("DB_PASSWORD", "db.password", DEFAULT_PASSWORD);

        return DriverManager.getConnection(url, user, password);
    }

    private static String readConfig(String envName, String propertyName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.trim().isEmpty()) {
            return propertyValue;
        }

        String envValue = System.getenv(envName);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue;
        }

        return defaultValue;
    }
}
