package dao;

import database.DatabaseConnection;
import entity.NhanVien;
import util.MatKhau;
import java.sql.*;

/** Dang nhap qua TaiKhoan va NhanVien cua schema QuanLyBanHang. */
public final class DangNhapDAO {
    public NhanVien dangNhap(String username, char[] password) throws SQLException {
        if (username == null || username.isBlank() || password == null || password.length == 0) {
            throw new IllegalArgumentException("Vui lòng nhập tài khoản và mật khẩu.");
        }
        String sql = "SELECT maNV,matKhau FROM dbo.TaiKhoan "
                + "WHERE tenDangNhap=? AND dangHoatDong=1 "
                + "AND EXISTS (SELECT 1 FROM dbo.NhanVien n WHERE n.maNV=TaiKhoan.maNV AND n.dangLamViec=1)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username.trim());
            String maNV;
            try (ResultSet r = p.executeQuery()) {
                if (!r.next() || !MatKhau.khop(password, r.getString("matKhau"))) {
                    throw new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu không đúng hoặc tài khoản đã khóa.");
                }
                maNV = r.getString("maNV");
            }
            return new BanHangDAO().nhanVien(c, maNV);
        }
    }
}
