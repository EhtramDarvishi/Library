package library.Entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionMysql {

    String jdbcURL = "jdbc:mysql://localhost:3306/Gamification";
    String userName = "test";
    String password = "mssql@hq";
    private static String jdbcDriver = Config.getJdbcDriver();
    private static String connectionString = Config.getConnectionString();
    private static String dbUserName = Config.getDbUserName();
    private static String dbPassword = Config.getDbPassword();

    public void connection() {
        try {
            Connection conn = DriverManager.getConnection(jdbcURL, userName, password);
            if (conn != null) {
                System.out.println("Connect To DataBase.");
            }
        } catch (Exception e) {
            System.out.println(" Exception Connect To DataBase.");
        }

    }
}
