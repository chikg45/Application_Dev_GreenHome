package database;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.Properties;

public final class DatabaseConnection {
    private DatabaseConnection() {}
    public static Connection getConnection() throws SQLException {
        Properties p = new Properties();
        p.setProperty("url", "jdbc:sqlserver://localhost:1433;databaseName=QuanLyBanHang;encrypt=true;trustServerCertificate=true;loginTimeout=5;socketTimeout=15000");
        p.setProperty("user", ""); p.setProperty("password", "");
        Path file = Path.of(System.getProperty("greenhome.config", "config/db.properties"));
        if (Files.exists(file)) {
            try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) { p.load(r); }
            catch (IOException e) { throw new SQLException("Không đọc được cấu hình kết nối: " + file,e); }
        }
        try { Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); }
        catch (ClassNotFoundException e) { throw new SQLException("Thiếu Microsoft JDBC Driver. Thêm mssql-jdbc-*.jre11.jar vào Java Build Path hoặc thư mục lib.",e); }
        return DriverManager.getConnection(p.getProperty("url"),p.getProperty("user"),p.getProperty("password"));
    }
}
