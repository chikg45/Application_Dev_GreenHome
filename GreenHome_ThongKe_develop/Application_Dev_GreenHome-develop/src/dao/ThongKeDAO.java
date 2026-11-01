package dao;

import connectdb.Jdbc;
import connectdb.NguonKetNoi;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Truy vấn dữ liệu báo cáo thống kê từ SQL Server. */
public class ThongKeDAO {
    private final NguonKetNoi nguon;

    public ThongKeDAO(NguonKetNoi nguon) {
        if (nguon == null) throw new IllegalArgumentException("Nguồn kết nối không được null.");
        this.nguon = nguon;
    }

    /** Mỗi dòng là một sản phẩm thuộc một hóa đơn đã thanh toán. */
    public List<HoaDonThongKe> layChiTietHoaDon(LocalDate tuNgay, LocalDate denNgay) {
        String sql = "SELECT hd.maHD, hd.ngayLap, hd.maKH, "
            + "COALESCE(NULLIF(LTRIM(RTRIM(hd.tenKhachHangLucBan)), N''), N'Khách vãng lai') AS tenKhach, "
            + "ct.tenSP, ct.soLuong, "
            + "(ct.giaChuaThue + ct.tienVAT - ct.soTienGiam) AS thanhTien "
            + "FROM dbo.HOA_DON hd "
            + "INNER JOIN dbo.DON_HANG dh ON dh.maDH = hd.maDH "
            + "INNER JOIN dbo.CHI_TIET_HOA_DON ct ON ct.maHD = hd.maHD "
            + "WHERE dh.trangThaiDH = N'DaThanhToan' "
            + "AND hd.ngayLap >= ? AND hd.ngayLap < DATEADD(day, 1, ?) "
            + "ORDER BY hd.ngayLap DESC, hd.maHD, ct.tenSP";

        return Jdbc.doc(nguon, c -> {
            List<HoaDonThongKe> ds = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setDate(1, Date.valueOf(tuNgay));
                ps.setDate(2, Date.valueOf(denNgay));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        HoaDonThongKe h = new HoaDonThongKe();
                        h.maHD = rs.getString("maHD");
                        h.ngay = rs.getTimestamp("ngayLap").toLocalDateTime().toLocalDate();
                        h.maKH = rs.getString("maKH");
                        h.khachHang = rs.getString("tenKhach");
                        h.sanPham = rs.getString("tenSP");
                        h.soLuong = rs.getInt("soLuong");
                        h.thanhTien = tien(rs.getBigDecimal("thanhTien"));
                        ds.add(h);
                    }
                }
            }
            return ds;
        });
    }

    /** Tiền hoàn lấy từ thanhTienHoan; NULL được xem là chưa ghi nhận tiền hoàn. */
    public List<PhieuTraThongKe> layPhieuDoiTra(LocalDate tuNgay, LocalDate denNgay) {
        String sql = "SELECT p.maPhieuDoiTra, p.maHD, p.ngayLap, p.trangThaiDuyet, "
            + "COALESCE(SUM(ct.thanhTienHoan), 0) AS tienHoan "
            + "FROM dbo.PHIEU_DOI_TRA p "
            + "LEFT JOIN dbo.CHI_TIET_PHIEU_DOI_TRA ct "
            + "ON ct.maPhieuDoiTra = p.maPhieuDoiTra AND ct.maHD = p.maHD "
            + "WHERE p.ngayLap >= ? AND p.ngayLap < DATEADD(day, 1, ?) "
            + "GROUP BY p.maPhieuDoiTra, p.maHD, p.ngayLap, p.trangThaiDuyet "
            + "ORDER BY p.ngayLap DESC, p.maPhieuDoiTra";

        return Jdbc.doc(nguon, c -> {
            List<PhieuTraThongKe> ds = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setDate(1, Date.valueOf(tuNgay));
                ps.setDate(2, Date.valueOf(denNgay));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        PhieuTraThongKe p = new PhieuTraThongKe();
                        p.maPhieu = rs.getString("maPhieuDoiTra");
                        p.maHD = rs.getString("maHD");
                        p.ngay = rs.getTimestamp("ngayLap").toLocalDateTime().toLocalDate();
                        p.trangThai = doiTrangThai(rs.getString("trangThaiDuyet"));
                        p.tienHoan = tien(rs.getBigDecimal("tienHoan"));
                        ds.add(p);
                    }
                }
            }
            return ds;
        });
    }

    private static long tien(BigDecimal value) {
        return value == null ? 0L : value.longValue();
    }

    private static String doiTrangThai(String status) {
        if (status == null) return "Không xác định";
        switch (status) {
            case "ChoDuyet": return "Chờ duyệt";
            case "DaDuyet": return "Đã duyệt";
            case "TuChoi": return "Từ chối";
            case "HoanTat": return "Hoàn tất";
            default: return status;
        }
    }

    public static class HoaDonThongKe {
        public String maHD, maKH, khachHang, sanPham;
        public LocalDate ngay;
        public int soLuong;
        public long thanhTien;
    }

    public static class PhieuTraThongKe {
        public String maPhieu, maHD, trangThai;
        public LocalDate ngay;
        public long tienHoan;
    }
}
