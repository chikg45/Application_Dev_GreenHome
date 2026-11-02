package dao;

import database.DatabaseConnection;
import connectDB.Jdbc;
import connectDB.NguonKetNoi;
import entity.SanPham;
import entity.ThuongHieu;
import enums.DanhMucSP;
import enums.TinhTrangSP;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** JDBC cho dbo.SanPham; danh muc Java la enum, SQL la FK dbo.DanhMuc. */
public class SanPhamDAO {
    private final NguonKetNoi nguon;

    private static final String COT_SP = "sp.maSP, sp.tenSP, sp.maDM, dm.tenDM, "
            + "sp.maTH, th.tenTH, th.moTaThuongHieu, sp.thongSoKyThuat, "
            + "sp.giaNiemYet, sp.model, sp.donViTinh, sp.moTaSP, sp.tinhTrang, "
            + "sp.soLuongTon, sp.soLuongLoi, sp.soLuongTonToiThieu, sp.hinhAnh ";

    private static final String TU_BANG = " FROM dbo.SanPham sp "
            + "JOIN dbo.DanhMuc dm ON dm.maDM = sp.maDM "
            + "JOIN dbo.ThuongHieu th ON th.maTH = sp.maTH ";

    public SanPhamDAO() {
        this(DatabaseConnection::getConnection);
    }

    public SanPhamDAO(NguonKetNoi nguon) {
        if (nguon == null) throw new IllegalArgumentException("Nguồn kết nối không được null.");
        this.nguon = nguon;
    }

    private SanPham docSanPham(ResultSet rs) throws SQLException {
        ThuongHieu th = new ThuongHieu(rs.getString("maTH"), rs.getString("tenTH"));
        th.setMoTaThuongHieu(rs.getString("moTaThuongHieu"));
        return new SanPham(
                rs.getString("maSP"), rs.getString("tenSP"),
                DanhMucSP.tuTen(rs.getString("tenDM")), th,
                rs.getString("thongSoKyThuat"), rs.getBigDecimal("giaNiemYet").doubleValue(),
                rs.getString("model"), rs.getString("donViTinh"), rs.getString("moTaSP"),
                TinhTrangSP.tuCSDL(rs.getString("tinhTrang")),
                rs.getInt("soLuongTon"), rs.getInt("soLuongLoi"),
                rs.getInt("soLuongTonToiThieu"), rs.getString("hinhAnh")
        );
    }

    public List<SanPham> layTatCa() {
        String sql = "SELECT " + COT_SP + TU_BANG + " ORDER BY sp.maSP";
        return Jdbc.doc(nguon, c -> {
            List<SanPham> ketQua = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ketQua.add(docSanPham(rs));
            }
            return ketQua;
        });
    }

    public SanPham timTheoMa(String maSP) {
        String sql = "SELECT " + COT_SP + TU_BANG + " WHERE sp.maSP = ?";
        return Jdbc.doc(nguon, c -> {
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, maSP);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? docSanPham(rs) : null;
                }
            }
        });
    }

    /** Tim don gian theo ma, ten hoac model. */
    public List<SanPham> timKiem(String tuKhoa) {
        return timKiemNangCao(tuKhoa, null, null, null);
    }

    /** Bo loc nang cao; null nghia la khong loc theo tieu chi do. */
    public List<SanPham> timKiemNangCao(String tuKhoa, DanhMucSP danhMuc,
                                       String maThuongHieu, TinhTrangSP tinhTrang) {
        StringBuilder sql = new StringBuilder("SELECT ").append(COT_SP).append(TU_BANG);
        sql.append(" WHERE 1 = 1");
        List<Object> thamSo = new ArrayList<>();

        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append(" AND (LOWER(sp.maSP) LIKE LOWER(?) OR LOWER(sp.tenSP) LIKE LOWER(?) OR LOWER(sp.model) LIKE LOWER(?))");
            String mau = "%" + tuKhoa.trim() + "%";
            thamSo.add(mau);
            thamSo.add(mau);
            thamSo.add(mau);
        }
        if (danhMuc != null) {
            sql.append(" AND sp.maDM = ?");
            thamSo.add(maDanhMuc(danhMuc));
        }
        if (maThuongHieu != null && !maThuongHieu.isBlank()) {
            sql.append(" AND sp.maTH = ?");
            thamSo.add(maThuongHieu.trim());
        }
        if (tinhTrang != null) {
            sql.append(" AND sp.tinhTrang = ?");
            thamSo.add(tinhTrang.getGiaTriCSDL());
        }
        sql.append(" ORDER BY sp.maSP");

        return Jdbc.doc(nguon, c -> {
            List<SanPham> ketQua = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(sql.toString())) {
                Jdbc.bind(ps, thamSo.toArray());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) ketQua.add(docSanPham(rs));
                }
            }
            return ketQua;
        });
    }

    /** Them san pham; maSP khong tu phat sinh vi UC002 yeu cau Quan ly nhap ma. */
    public boolean them(String maNVDangNhap, SanPham sp) {
        return Jdbc.giaoDich(nguon, c -> {
            yeuCauQuanLy(c, maNVDangNhap);
            ganNguoiThaoTac(c, maNVDangNhap);
            String sql = "INSERT INTO dbo.SanPham "
                    + "(maSP, tenSP, maDM, maTH, thongSoKyThuat, giaNiemYet, model, "
                    + "donViTinh, moTaSP, tinhTrang, soLuongTon, soLuongLoi, "
                    + "soLuongTonToiThieu, hinhAnh) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            return Jdbc.capNhat(c, sql, sp.getMaSP(), sp.getTenSP(), maDanhMuc(sp.getDanhMuc()),
                    sp.getThuongHieu().getMaThuongHieu(), sp.getThongSoKyThuat(),
                    BigDecimal.valueOf(sp.getGiaNiemYet()), sp.getModel(), sp.getDonViTinh(),
                    sp.getMoTaSP(), sp.getTinhTrang().getGiaTriCSDL(), sp.getSoLuongTon(),
                    sp.getSoLuongLoi(), sp.getSoLuongTonToiThieu(), sp.getHinhAnh()) == 1;
        });
    }

    /** Khong sua maSP; hoa don cu giu nguyen gia da ban trong ChiTietHoaDon. */
    public boolean capNhat(String maNVDangNhap, SanPham sp) {
        return Jdbc.giaoDich(nguon, c -> {
            yeuCauQuanLy(c, maNVDangNhap);
            ganNguoiThaoTac(c, maNVDangNhap);
            String sql = "UPDATE dbo.SanPham SET tenSP = ?, maDM = ?, maTH = ?, "
                    + "thongSoKyThuat = ?, giaNiemYet = ?, model = ?, donViTinh = ?, "
                    + "moTaSP = ?, tinhTrang = ?, soLuongTon = ?, soLuongLoi = ?, "
                    + "soLuongTonToiThieu = ?, hinhAnh = ? WHERE maSP = ?";
            return Jdbc.capNhat(c, sql, sp.getTenSP(), maDanhMuc(sp.getDanhMuc()),
                    sp.getThuongHieu().getMaThuongHieu(), sp.getThongSoKyThuat(),
                    BigDecimal.valueOf(sp.getGiaNiemYet()), sp.getModel(), sp.getDonViTinh(),
                    sp.getMoTaSP(), sp.getTinhTrang().getGiaTriCSDL(), sp.getSoLuongTon(),
                    sp.getSoLuongLoi(), sp.getSoLuongTonToiThieu(), sp.getHinhAnh(),
                    sp.getMaSP()) == 1;
        });
    }

    /** SP-07: khong xoa vinh vien neu da co giao dich ban/dat hang. */
    public boolean xoa(String maNVDangNhap, String maSP) {
        return Jdbc.giaoDich(nguon, c -> {
            yeuCauQuanLy(c, maNVDangNhap);
            ganNguoiThaoTac(c, maNVDangNhap);
            // Khoa dong san pham trong transaction; neu khong ton tai thi tra false.
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT maSP FROM dbo.SanPham WITH (UPDLOCK, HOLDLOCK) WHERE maSP = ?")) {
                ps.setString(1, maSP);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return false;
                }
            }
            String kiemTra = "SELECT 1 WHERE EXISTS (SELECT 1 FROM dbo.ChiTietDonHang WHERE maSP = ?) "
                    + "OR EXISTS (SELECT 1 FROM dbo.ChiTietHoaDon WHERE maSP = ?)";
            try (PreparedStatement ps = c.prepareStatement(kiemTra)) {
                ps.setString(1, maSP);
                ps.setString(2, maSP);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        throw new IllegalStateException(
                                "Sản phẩm đã phát sinh giao dịch; chỉ được ngừng kinh doanh, không được xóa.");
                    }
                }
            }
            // Xoa bang lien ket khuyen mai truoc; neu thao tac sau loi, transaction rollback.
            Jdbc.capNhat(c, "DELETE FROM dbo.SanPhamKhuyenMai WHERE maSP = ?", maSP);
            return Jdbc.capNhat(c, "DELETE FROM dbo.SanPham WHERE maSP = ?", maSP) == 1;
        });
    }

    /** Ma cua 7 danh muc co dinh theo 02_Du_Lieu_Demo.sql. */
    public static String maDanhMuc(DanhMucSP dm) {
        if (dm == null) throw new IllegalArgumentException("Danh mục không được null.");
        return switch (dm) {
            case DIEN_THOAI -> "DM001";
            case LAPTOP -> "DM002";
            case TIVI -> "DM003";
            case TU_LANH -> "DM004";
            case MAY_GIAT -> "DM005";
            case MAY_LANH -> "DM006";
            case PHU_KIEN_DIEN_TU -> "DM007";
        };
    }

    /** SQL Server 2012+: truyen ma nhan vien qua CONTEXT_INFO de trigger ghi nhat ky. */
    private void ganNguoiThaoTac(Connection c, String maNV) throws SQLException {
        // CHAR(20) dem theo khoang trang, de SQL trigger doc lai an toan (RTRIM).
        // Dung tren chinh Connection cua transaction them/sua/xoa san pham.
        String sql = "DECLARE @actor CHAR(20) = CONVERT(CHAR(20), ?); "
                + "DECLARE @ctx VARBINARY(128) = CONVERT(VARBINARY(128), @actor); "
                + "SET CONTEXT_INFO @ctx;";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ps.execute();
        }
    }

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
                if (!rs.next()) throw new SecurityException("Chỉ Quản lý được thay đổi sản phẩm.");
            }
        }
    }
}
