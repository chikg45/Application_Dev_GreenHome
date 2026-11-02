package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private DatabaseConnection() {}

    // Điều chỉnh host, instance, cổng và thông tin đăng nhập theo máy của bạn.
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyBanHang;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "sapassword";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
	