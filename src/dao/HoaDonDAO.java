package dao;

import database.DatabaseConnection;
import entity.*;
import java.sql.*;
import java.util.*;

/** Đọc hóa đơn nhiều sản phẩm từ CSDL, không dựa vào danh sách hóa đơn mẫu. */
public class HoaDonDAO {
    public List<HoaDon> layDanhSach(String maHD) throws SQLException {
        List<HoaDon> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection()) {
            // Giữ cùng ảnh dữ liệu cho header và chi tiết trong một lần đọc.
            c.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ); c.setAutoCommit(false);
            try {
                try (PreparedStatement p = c.prepareStatement(
                    "SELECT h.*,k.tenKH,k.sdtKH,k.diaChi,k.diemTichLuy,n.hoTen FROM dbo.HoaDon h "
                    + "JOIN dbo.KhachHang k ON k.maKH=h.maKH JOIN dbo.NhanVien n ON n.maNV=h.maNV "
                    + (maHD == null ? "" : "WHERE h.maHD=? ") + "ORDER BY h.ngayLap DESC,h.maHD")) {
                    if (maHD != null) p.setString(1, maHD);
                    try (ResultSet r = p.executeQuery()) {
                        while (r.next()) {
                            NhanVien nv = new NhanVien(); nv.setMaNV(r.getString("maNV")); nv.setHoTen(r.getString("hoTen"));
                            KhachHang kh = new KhachHang(r.getString("maKH"), r.getString("tenKH"), r.getString("sdtKH"), r.getString("diaChi"), r.getInt("diemTichLuy"));
                            list.add(new HoaDon(r.getString("maHD"), r.getTimestamp("ngayLap").toLocalDateTime(),
                                PhuongThucThanhToan.valueOf(r.getString("phuongThucThanhToan").trim()), kh, nv, null));
                        }
                    }
                }
                for (HoaDon h : list) {
                    try (PreparedStatement p = c.prepareStatement(
                        "SELECT ct.*,s.tenSP FROM dbo.ChiTietHoaDon ct JOIN dbo.SanPham s ON s.maSP=ct.maSP WHERE ct.maHD=? ORDER BY ct.idChiTietHD")) {
                        p.setString(1, h.getMaHD());
                        try (ResultSet r = p.executeQuery()) {
                            while (r.next()) {
                                SanPham sp = new SanPham(); sp.setMaSP(r.getString("maSP")); sp.setTenSP(r.getString("tenSP"));
                                new ChiTietHoaDon(r.getInt("soLuong"), r.getDouble("donGia"), r.getString("serialIMEI"), r.getDouble("soTienGiam"), sp, h, null);
                            }
                        }
                    }
                }
                c.commit(); return list;
            } catch (SQLException | RuntimeException e) { c.rollback(); throw e; }
        }
    }
    public List<String> phieuLienQuan(String maHD) throws SQLException {
        List<String> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement p = c.prepareStatement(
                "SELECT maPhieuDoiTra,trangThaiDuyet FROM dbo.PhieuDoiTra WHERE maHD=? ORDER BY ngayLap DESC")) {
            p.setString(1, maHD);
            try (ResultSet r = p.executeQuery()) { while (r.next()) list.add(r.getString(1) + " · " + r.getString(2)); }
        }
        return list;
    }
}
