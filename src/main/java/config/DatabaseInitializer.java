package config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {
    private DatabaseInitializer() {
    }

    public static void initialize() throws SQLException {
        SQLException lastException = null;
        for (int attempt = 1; attempt <= 20; attempt++) {
            try {
                createTables();
                return;
            } catch (SQLException exception) {
                lastException = exception;
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw exception;
                }
            }
        }
        throw lastException;
    }

    private static void createTables() throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS contacts (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(120) NOT NULL,
                        email VARCHAR(160) NOT NULL UNIQUE,
                        phone VARCHAR(40) NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS appointments (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        contact_id BIGINT NOT NULL,
                        title VARCHAR(140) NOT NULL,
                        description TEXT,
                        starts_at DATETIME NOT NULL,
                        ends_at DATETIME NOT NULL,
                        status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_appointments_contact
                            FOREIGN KEY (contact_id) REFERENCES contacts(id)
                            ON DELETE CASCADE
                    )
                    """);
        }
    }
}
