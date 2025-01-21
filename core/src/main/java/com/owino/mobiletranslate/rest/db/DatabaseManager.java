package com.owino.mobiletranslate.rest.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

@Slf4j
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:auth.db";
    private static HikariDataSource dataSource;

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");


            createUsersTable(stmt);
            createApiKeysTable(stmt);

            log.info("Database initialized successfully.");
        } catch (SQLException e) {
            log.error("Error initializing database: {}", e.getMessage(), e);
            throw new RuntimeException("Could not initialize database", e);
        }
    }

    private static void createUsersTable(Statement stmt) throws SQLException {
        stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE COLLATE NOCASE, " +
                "email TEXT UNIQUE COLLATE NOCASE, " +
                "password TEXT, " +
                "created_at TEXT DEFAULT (datetime('now')))");
    }

    private static void createApiKeysTable(Statement stmt) throws SQLException {
        stmt.execute("CREATE TABLE IF NOT EXISTS api_keys (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "api_key TEXT UNIQUE, " +
                "created_at TEXT DEFAULT (datetime('now')), " +
                "expires_at TEXT, " +
                "is_active INTEGER DEFAULT 1, " +
                "FOREIGN KEY (user_id) REFERENCES users(id))");

        stmt.execute("CREATE INDEX IF NOT EXISTS idx_api_keys_user_id ON api_keys(user_id)");
    }
   /*
    * The name can be a bit misleading
    */
    public static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (DatabaseManager.class) {
                if (dataSource == null) {
                    HikariConfig config = new HikariConfig();
                    config.setJdbcUrl(DB_URL);
                    config.setMaximumPoolSize(10);//actually this is same as default
                    config.setMinimumIdle(5);
                    config.setIdleTimeout(300000); // 5 minutes
                    config.setMaxLifetime(1800000); // 30 minutes
                    config.setConnectionTestQuery("SELECT 1");

                    dataSource = new HikariDataSource(config);
                }
            }
        }
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public static void deleteExpiredApiKeys() {
        String sql = "DELETE FROM api_keys WHERE expires_at < ? AND expires_at IS NOT NULL";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, LocalDateTime.now().toString());
            int deletedRows = pstmt.executeUpdate();
            log.info("Deleted {} expired API keys", deletedRows);
        } catch (SQLException e) {
            log.error("Error deleting expired API keys: {}", e.getMessage(), e);
        }
    }
}