package mg.hei.federation_agricole.config;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DatabaseConnection {

    private static final String URL =System.getenv("DB_URL");
    private static final String USERNAME =System.getenv("DB_USERNAME");
    private static final String PASSWORD =System.getenv("DB_PASSWORD");
    private static final String DRIVER = "org.postgresql.Driver";

    public Connection getConnection() throws SQLException {
        try {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
        throw new RuntimeException(e);

        }
    }
}