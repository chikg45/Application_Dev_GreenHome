package dao;

import database.DatabaseConnection;
import connectDB.Jdbc;
import connectDB.NguonKetNoi;
import entity.ThuongHieu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** JDBC cho bang dbo.ThuongHieu trong QuanLyBanHang. */
public class ThuongHieuDAO {
    private final NguonKetNoi nguon;

    public ThuongHieuDAO() {
        this(DatabaseConnection::getConnection);
    }

    public ThuongHieuDAO(NguonKetNoi nguon) {
        if (nguon == null) throw new IllegalArgumentException("Nguồn kết nối không được null.");
        this.nguon = nguon;
    }

    private ThuongHieu docThuongHieu(ResultSet rs) throws SQLException {
        ThuongHieu th = new ThuongHieu(rs.getString("maTH"), rs.getString("tenTH"));
        th.setMoTaThuongHieu(rs.getString("moTaThuongHieu"));
        return th;
    }

    public List<ThuongHieu> layTatCa() {
        String sql = "SELECT maTH, tenTH, moTaThuongHieu FROM dbo.ThuongHieu ORDER BY maTH";
        return Jdbc.doc(nguon, c -> {
            List<ThuongHieu> ketQua = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ketQua.add(docThuongHieu(rs));
            }
            return ketQua;
        });
    }

    public ThuongHieu timTheoMa(String maTH) {
        String sql = "SELECT maTH, tenTH, moTaThuongHieu FROM dbo.ThuongHieu WHERE maTH = ?";
        return Jdbc.doc(nguon, c -> {
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, maTH);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? docThuongHieu(rs) : null;
                }
            }
        });
    }

    /** Tim theo ma hoac ten; xuat ra List rong neu khong co ket qua. */
    public List<ThuongHieu> timKiem(String tuKhoa) {
        if (tuKhoa == null || tuKhoa.isBlank()) return layTatCa();
        String sql = "SELECT maTH, tenTH, moTaThuongHieu FROM dbo.ThuongHieu "
                + "WHERE maTH LIKE ? OR tenTH LIKE ? ORDER BY maTH";
        String mau = "%" + tuKhoa.trim() + "%";
        return Jdbc.doc(nguon, c -> {
            List<ThuongHieu> ketQua = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, mau);
                ps.setString(2, mau);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) ketQua.add(docThuongHieu(rs));
                }
            }
            return ketQua;
        });
    }

    /** Ma/ten trung da co UNIQUE tai database. */
    public boolean them(String maNVDangNhap, ThuongHieu th) {
        return Jdbc.giaoDich(nguon, c -> {
            yeuCauQuanLy(c, maNVDangNhap);
            String sql = "INSERT INTO dbo.ThuongHieu(maTH, tenTH, moTaThuongHieu) VALUES(?, ?, ?)";
            return Jdbc.capNhat(c, sql, th.getMaThuongHieu(), th.getTenThuongHieu(),
                    th.getMoTaThuongHieu()) == 1;
        });
    }

    /** Ma thuong hieu la khoa chinh, khong cap nhat. */
    public boolean capNhat(String maNVDangNhap, ThuongHieu th) {
        return Jdbc.giaoDich(nguon, c -> {
            yeuCauQuanLy(c, maNVDangNhap);
            String sql = "UPDATE dbo.ThuongHieu SET tenTH = ?, moTaThuongHieu = ? WHERE maTH = ?";
            return Jdbc.capNhat(c, sql, th.getTenThuongHieu(),
                    th.getMoTaThuongHieu(), th.getMaThuongHieu()) == 1;
        });
    }

    /** Khong xoa thuong hieu dang co san pham tham chieu (FK). */
    public boolean xoa(String maNVDangNhap, String maTH) {
        return Jdbc.giaoDich(nguon, c -> {
            yeuCauQuanLy(c, maNVDangNhap);
            String sqlCheck = "SELECT 1 FROM dbo.SanPham WHERE maTH = ?";
            try (PreparedStatement ps = c.prepareStatement(sqlCheck)) {
                ps.setString(1, maTH);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        throw new IllegalStateException(
                                "Không thể xóa thương hiệu đang có sản phẩm.");
                    }
                }
            }
            return Jdbc.capNhat(c, "DELETE FROM dbo.ThuongHieu WHERE maTH = ?", maTH) == 1;
        });
    }

    /** TaiKhoan.vaiTro va trang thai lay tu SQL, khong tin co quyen tren UI. */
    private void yeuCauQuanLy(Connection c, String maNV) throws SQLException {
        if (maNV == null || maNV.isBlank()) {
            throw new SecurityException("Bạn chưa đăng nhập bằng tài khoản Quản lý.");
        }
        String sql = "SELECT 1 FROM dbo.TaiKhoan tk JOIN dbo.NhanVien nv "
                + "ON tk.maNV = nv.maNV WHERE tk.maNV = ? "
                + "AND tk.vaiTro = 'QUAN_LY' AND tk.dangHoatDong = 1 AND nv.dangLamViec = 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SecurityException("Chỉ Quản lý được thay đổi thương hiệu.");
            }
        }
    }
}
