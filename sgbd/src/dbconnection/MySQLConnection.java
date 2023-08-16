package dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {

    private Connection connection;

    public MySQLConnection(String host, String port, String database, String user, String password) {
        try {
            String connectionURL = String.format("jdbc:mysql://%s:%s/%s", host, port, database);
            connection = DriverManager.getConnection(connectionURL, user, password);
        } catch (SQLException e) {
            connection = null;
        }
    }

    public boolean isValid()
    {
        return connection != null;
    }

}
