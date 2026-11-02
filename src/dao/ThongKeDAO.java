package dao;

import database.DatabaseConnection;
import entity.VaiTro;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/** Báo cáo từ chi tiết hóa đơn thực; lọc [đầu ngày, đầu ngày kế tiếp). */
public class ThongKeDAO {
    public record DongBan(String maHD, LocalDate ngay, String maKH, String tenKH,
                         String maSP, String tenSP, int soLuong, BigDecimal tien) {}
    public record PhieuTra(String ma, LocalDate ngay, String trangThai, BigDecimal tienHoan) {}
    public record DuLieu(List<DongBan> ban, List<PhieuTra> tra) {
        public DuLieu { ban = List.copyOf(ban); tra = List.copyOf(tra); }
    }
    public DuLieu tai(String maNV, LocalDate tu, LocalDate den) throws SQLException {
        if (tu == null || den == null || tu.isAfter(den)) throw new IllegalArgumentException("Khoảng ngày không hợp lệ.");
        try (Connection c = DatabaseConnection.getConnection()) {
            c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); c.setAutoCommit(false);
            try {
                if (new BanHangDAO().nhanVien(c, maNV).getTaiKhoan().getVaiTro() != VaiTro.QUAN_LY)
                    throw new IllegalStateException("Chỉ Quản lý được xem thống kê.");
                List<DongBan> ban = new ArrayList<>(); List<PhieuTra> tra = new ArrayList<>();
                try (PreparedStatement p = c.prepareStatement(
                    "SELECT h.maHD,h.ngayLap,h.maKH,k.tenKH,ct.maSP,s.tenSP,ct.soLuong,"
                    + "ct.soLuong*ct.donGia-ct.soTienGiam AS tien FROM dbo.HoaDon h "
                    + "JOIN dbo.ChiTietHoaDon ct ON ct.maHD=h.maHD JOIN dbo.KhachHang k ON k.maKH=h.maKH "
                    + "JOIN dbo.SanPham s ON s.maSP=ct.maSP WHERE h.ngayLap>=? AND h.ngayLap<? ORDER BY h.ngayLap,h.maHD,ct.maSP")) {
                    khoang(p, tu, den);
                    try (ResultSet r = p.executeQuery()) {
                        while (r.next()) ban.add(new DongBan(r.getString("maHD"), r.getTimestamp("ngayLap").toLocalDateTime().toLocalDate(),
                            r.getString("maKH"), r.getString("tenKH"), r.getString("maSP"), r.getString("tenSP"), r.getInt("soLuong"), r.getBigDecimal("tien")));
                    }
                }
                try (PreparedStatement p = c.prepareStatement(
                    "SELECT p.maPhieuDoiTra,p.ngayLap,p.trangThaiDuyet,"
                    + "COALESCE(SUM(CASE WHEN dt.hinhThucXuLy='HOAN_TIEN' THEN dt.soLuong*dt.donGia ELSE 0 END),0) AS tien "
                    + "FROM dbo.PhieuDoiTra p LEFT JOIN dbo.ChiTietPhieuDoiTra dt ON dt.maPhieuDoiTra=p.maPhieuDoiTra "
                    + "WHERE p.ngayLap>=? AND p.ngayLap<? GROUP BY p.maPhieuDoiTra,p.ngayLap,p.trangThaiDuyet ORDER BY p.ngayLap,p.maPhieuDoiTra")) {
                    khoang(p, tu, den);
                    try (ResultSet r = p.executeQuery()) {
                        while (r.next()) tra.add(new PhieuTra(r.getString(1), r.getTimestamp(2).toLocalDateTime().toLocalDate(), r.getString(3).trim(), r.getBigDecimal(4)));
                    }
                }
                c.commit(); return new DuLieu(ban, tra);
            } catch (SQLException | RuntimeException e) { c.rollback(); throw e; }
        }
    }
    private static void khoang(PreparedStatement p, LocalDate tu, LocalDate den) throws SQLException {
        p.setTimestamp(1, Timestamp.valueOf(tu.atStartOfDay()));
        p.setTimestamp(2, Timestamp.valueOf(den.plusDays(1).atStartOfDay()));
    }
}
