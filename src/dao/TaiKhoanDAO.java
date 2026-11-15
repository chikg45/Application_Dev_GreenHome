package dao;

import entity.TaiKhoan;
import entity.VaiTro;
import java.sql.*;

/** Cac thao tac tai khoan ben trong transaction cua NhanVienDAO. */
public class TaiKhoanDAO {
    public void them(Connection c, TaiKhoan tk, String maNV) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO dbo.TaiKhoan(maTK, tenDangNhap, matKhau, vaiTro, maNV, dangHoatDong) "
                + "VALUES(?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, tk.getMaTK()); ps.setString(2, tk.getTenDangNhap());
            ps.setString(3, tk.getMatKhau()); ps.setString(4, tk.getVaiTro().name());
            ps.setString(5, maNV); ps.setBoolean(6, tk.isDangHoatDong());
            ps.executeUpdate();
        }
    }
    public void capNhat(Connection c, TaiKhoan tk, String maNV) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE dbo.TaiKhoan SET tenDangNhap=?, vaiTro=?, dangHoatDong=? WHERE maNV=?")) {
            ps.setString(1, tk.getTenDangNhap()); ps.setString(2, tk.getVaiTro().name());
            ps.setBoolean(3, tk.isDangHoatDong()); ps.setString(4, maNV);
            if (ps.executeUpdate() != 1) throw new SQLException("Không tìm thấy tài khoản của nhân viên.");
        }
    }
    public void doiTrangThai(Connection c, String maNV, boolean mo) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE dbo.TaiKhoan SET dangHoatDong=? WHERE maNV=?")) {
            ps.setBoolean(1, mo); ps.setString(2, maNV);
            if (ps.executeUpdate() != 1) throw new SQLException("Không tìm thấy tài khoản.");
        }
    }
    static TaiKhoan doc(ResultSet rs) throws SQLException {
        String id = rs.getString("maTK");
        if (id == null) return null;
        TaiKhoan tk = new TaiKhoan(id, rs.getString("tenDangNhap"),
                rs.getString("matKhau"), VaiTro.valueOf(rs.getString("vaiTro")));
        tk.setDangHoatDong(rs.getBoolean("dangHoatDong"));
        return tk;
    }
}
