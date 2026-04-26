package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConfig {
    private DatabaseConfig() {
    }

    public static Connection getConnection() throws SQLException {
        String host = System.getenv().getOrDefault("DB_HOST", "localhost");
        String port = System.getenv().getOrDefault("DB_PORT", "3306");
        String database = System.getenv().getOrDefault("DB_NAME", "agenda_db");
        String user = System.getenv().getOrDefault("DB_USER", "agenda_user");
        String password = System.getenv().getOrDefault("DB_PASSWORD", "agenda_password");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        return DriverManager.getConnection(url, user, password);
    }
}
