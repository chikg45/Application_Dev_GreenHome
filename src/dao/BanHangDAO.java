package dao;

import database.DatabaseConnection;
import entity.*;
import enums.DanhMucSP;
import enums.TinhTrangSP;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/** Chỉ đọc/ghi các bảng đã có; không tạo hay sửa cấu trúc CSDL. */
public class BanHangDAO {
    @FunctionalInterface
    public interface CongViec<T> { T thucHien(Connection c) throws SQLException; }

    public <T> T doc(CongViec<T> viec) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) { return viec.thucHien(c); }
    }

    public <T> T giaoDich(CongViec<T> viec) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            c.setAutoCommit(false);
            try {
                T result = viec.thucHien(c);
                c.commit();
                return result;
            } catch (SQLException | RuntimeException ex) {
                try { c.rollback(); } catch (SQLException rollback) { ex.addSuppressed(rollback); }
                throw ex;
            }
        }
    }

    public List<SanPham> sanPhams(Connection c, String ma, boolean khoa) throws SQLException {
        String sql = "SELECT s.*, d.tenDM, t.tenTH FROM dbo.SanPham s "
            + (khoa ? "WITH (UPDLOCK,HOLDLOCK) " : "")
            + "JOIN dbo.DanhMuc d ON s.maDM=d.maDM JOIN dbo.ThuongHieu t ON s.maTH=t.maTH "
            + (ma == null ? "" : "WHERE s.maSP=? ") + "ORDER BY s.maSP";
        List<SanPham> ds = new ArrayList<>();
        try (PreparedStatement p = c.prepareStatement(sql)) {
            if (ma != null) p.setString(1, ma);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    SanPham s = new SanPham();
                    s.setMaSP(r.getString("maSP")); s.setTenSP(r.getString("tenSP"));
                    s.setDanhMuc(DanhMucSP.tuTen(r.getString("tenDM")));
                    ThuongHieu th = new ThuongHieu();
                    th.setMaTH(r.getString("maTH")); th.setTenTH(r.getString("tenTH")); s.setThuongHieu(th);
                    s.setGiaNiemYet(r.getBigDecimal("giaNiemYet").doubleValue());
                    s.setTinhTrang(TinhTrangSP.tuCSDL(r.getString("tinhTrang")));
                    s.setSoLuongTon(r.getInt("soLuongTon"));
                    s.setSoLuongLoi(r.getInt("soLuongLoi"));
                    s.setSoLuongTonToiThieu(r.getInt("soLuongTonToiThieu"));
                    s.setModel(r.getString("model")); s.setDonViTinh(r.getString("donViTinh"));
                    s.setHinhAnh(r.getString("hinhAnh")); s.setMoTaSP(r.getString("moTaSP"));
                    ds.add(s);
                }
            }
        }
        for (SanPham s : ds) {
            try (PreparedStatement p = c.prepareStatement(
                    "SELECT k.* FROM dbo.KhuyenMai k JOIN dbo.SanPhamKhuyenMai sk ON sk.maKM=k.maKM WHERE sk.maSP=?")) {
                p.setString(1, s.getMaSP());
                try (ResultSet r = p.executeQuery()) {
                    while (r.next()) s.themKhuyenMai(new KhuyenMai(
                        r.getString("maKM"), r.getString("tenKM"), r.getDouble("phanTramGiam"),
                        r.getTimestamp("ngayBatDau").toLocalDateTime(), r.getTimestamp("ngayKetThuc").toLocalDateTime(),
                        r.getString("moTaKM"), r.getString("loaiGiam"), r.getDouble("soTienGiam")));
                }
            }
        }
        return ds;
    }

    public List<KhachHang> khachHangs(Connection c, String ma, boolean khoa) throws SQLException {
        String sql = "SELECT * FROM dbo.KhachHang " + (khoa ? "WITH (UPDLOCK,HOLDLOCK) " : "")
            + (ma == null ? "" : "WHERE maKH=? ") + "ORDER BY tenKH,maKH";
        List<KhachHang> ds = new ArrayList<>();
        try (PreparedStatement p = c.prepareStatement(sql)) {
            if (ma != null) p.setString(1, ma);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    // Dùng setter có kiểm tra của entity gốc, không tự đổi dữ liệu sai thành dữ liệu mẫu.
                    ds.add(new KhachHang(r.getString("maKH"), r.getString("tenKH"),
                        r.getString("sdtKH"), r.getString("diaChi"), r.getInt("diemTichLuy")));
                }
            }
        }
        return ds;
    }

/**
 * Lấy khách hàng vãng lai.
 * Nếu chưa tồn tại thì tự động tạo mới trong database.
 */
public KhachHang khachVangLai() throws SQLException {

    final String sdtVangLai = "0000000000";

    return giaoDich(c -> {

        // Kiểm tra khách vãng lai đã tồn tại chưa
        String sql = "SELECT maKH, tenKH, sdtKH, diaChi, diemTichLuy "
                   + "FROM dbo.KhachHang "
                   + "WITH (UPDLOCK,HOLDLOCK) "
                   + "WHERE sdtKH = ?";

        try (PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, sdtVangLai);

            try (ResultSet rs = p.executeQuery()) {

                if (rs.next()) {

                    String ma = rs.getString("maKH");
                    String diaChi = rs.getString("diaChi");

                    // Đảm bảo hồ sơ đúng tên và không có điểm
                    capNhat(c,
                        "UPDATE dbo.KhachHang "
                        + "SET tenKH = N'Vãng lai', diemTichLuy = 0 "
                        + "WHERE maKH = ?",
                        ma
                    );

                    return new KhachHang(
                        ma,
                        "Vãng lai",
                        sdtVangLai,
                        diaChi,
                        0
                    );
                }
            }
        }

        // Nếu chưa có thì tạo khách hàng mới
        KhachHang kh = new KhachHang(
            maMoi("KH"),
            "Vãng lai",
            sdtVangLai,
            "",
            0
        );

        String insert = "INSERT INTO dbo.KhachHang "
                      + "(maKH, tenKH, sdtKH, diaChi, diemTichLuy) "
                      + "VALUES (?, ?, ?, ?, 0)";

        capNhat(c, insert,
            kh.getMaKH(),
            kh.getTenKH(),
            kh.getSdtKH(),
            kh.getDiaChi()
        );

        return kh;
    });
}


    public KhachHang themKhach(String ten, String sdt, String diaChi) throws SQLException {
        KhachHang kh = new KhachHang(maMoi("KH"), ten, sdt, diaChi, 0);
        return giaoDich(c -> {
            try (PreparedStatement p = c.prepareStatement("SELECT maKH FROM dbo.KhachHang WITH (UPDLOCK,HOLDLOCK) WHERE sdtKH=?")) {
                p.setString(1, sdt);
                try (ResultSet r = p.executeQuery()) {
                    if (r.next()) throw new IllegalArgumentException("Số điện thoại đã có hồ sơ. Hãy tìm và chọn khách.");
                }
            }
            capNhat(c, "INSERT INTO dbo.KhachHang(maKH,tenKH,sdtKH,diaChi,diemTichLuy) VALUES (?,?,?,?,0)",
                kh.getMaKH(), ten, sdt, diaChi);
            return kh;
        });
    }

    public NhanVien nhanVien(Connection c, String ma) throws SQLException {
        try (PreparedStatement p = c.prepareStatement(
                "SELECT n.maNV,n.hoTen,t.maTK,t.vaiTro,t.tenDangNhap FROM dbo.NhanVien n JOIN dbo.TaiKhoan t ON t.maNV=n.maNV WHERE n.maNV=?")) {
            p.setString(1, ma);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) throw new IllegalArgumentException("Nhân viên không tồn tại hoặc chưa có tài khoản.");
                NhanVien nv = new NhanVien(); nv.setMaNV(r.getString("maNV")); nv.setHoTen(r.getString("hoTen"));
                TaiKhoan tk = new TaiKhoan(); tk.setMaTK(r.getString("maTK"));
                tk.setTenDangNhap(r.getString("tenDangNhap")); tk.setVaiTro(VaiTro.valueOf(r.getString("vaiTro").trim()));
                tk.setNhanVien(nv); nv.setTaiKhoan(tk); return nv;
            }
        }
    }

    public boolean daLuu(Connection c, String maHD, String maNV) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("SELECT maNV FROM dbo.HoaDon WITH (UPDLOCK,HOLDLOCK) WHERE maHD=?")) {
            p.setString(1, maHD);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) return false;
                if (!maNV.equals(r.getString(1))) throw new IllegalArgumentException("Mã giao dịch thuộc nhân viên khác.");
                return true;
            }
        }
    }

    /** Khi thử lại sau mất kết nối, chỉ chấp nhận đúng nội dung đã lưu với mã này. */
    public void kiemTraDaLuu(Connection c, String maHD, bus.BanHangBUS.BaoGia gia, PhuongThucThanhToan pt) throws SQLException {
        String message = "Mã " + maHD + " đã thanh toán. Hãy xem Lịch sử bán hàng rồi tạo Đơn mới cho giao dịch khác.";
        try (PreparedStatement p = c.prepareStatement("SELECT maKH,phuongThucThanhToan FROM dbo.HoaDon WHERE maHD=?")) {
            p.setString(1,maHD);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next() || !gia.maKH().equals(r.getString(1)) || !pt.name().equals(r.getString(2)))
                    throw new IllegalStateException(message);
            }
        }
        Map<String,bus.BanHangBUS.DongGia> ds = new HashMap<>();
        for (var d : gia.dong()) ds.put(d.maSP(),d);
        try (PreparedStatement p = c.prepareStatement("SELECT maSP,soLuong,donGia,soTienGiam FROM dbo.ChiTietHoaDon WHERE maHD=?")) {
            p.setString(1,maHD);
            try (ResultSet r=p.executeQuery()) {
                while (r.next()) {
                    var d=ds.remove(r.getString(1));
                    if (d==null || d.soLuong()!=r.getInt(2) || d.donGia().compareTo(r.getBigDecimal(3))!=0
                            || d.giamKM().add(d.giamDiem()).compareTo(r.getBigDecimal(4))!=0)
                        throw new IllegalStateException(message);
                }
            }
        }
        if (!ds.isEmpty()) throw new IllegalStateException(message);
    }

    public void luu(Connection c, HoaDon hd, int diemDung, int diemCong) throws SQLException {
        DonHang dh = hd.getDonHang();
        capNhat(c, "INSERT INTO dbo.DonHang(maDH,ngayDat,trangThaiDH,ghiChu,maKH,maNV) VALUES (?,?,?,?,?,?)",
            dh.getMaDH(), Timestamp.valueOf(dh.getNgayDat()), dh.getTrangThaiDH().name(), dh.getGhiChu(),
            hd.getKhachHang().getMaKH(), hd.getNhanVien().getMaNV());
        capNhat(c, "INSERT INTO dbo.HoaDon(maHD,ngayLap,phuongThucThanhToan,maKH,maNV,maDH) VALUES (?,?,?,?,?,?)",
            hd.getMaHD(), Timestamp.valueOf(hd.getNgayLap()), hd.getPhuongThucThanhToan().name(),
            hd.getKhachHang().getMaKH(), hd.getNhanVien().getMaNV(), dh.getMaDH());
        for (ChiTietHoaDon ct : hd.getDanhSachChiTietHoaDon()) {
            String sp = ct.getSanPham().getMaSP();
            capNhat(c, "INSERT INTO dbo.ChiTietDonHang(maDH,maSP,soLuong,donGia,serialIMEI) VALUES (?,?,?,?,?)",
                dh.getMaDH(), sp, ct.getSoLuong(), BigDecimal.valueOf(ct.getDonGia()), ct.getSerialIMEI());
            capNhat(c, "INSERT INTO dbo.ChiTietHoaDon(maHD,maSP,maKM,soLuong,donGia,serialIMEI,soTienGiam) VALUES (?,?,?,?,?,?,?)",
                hd.getMaHD(), sp, ct.getKhuyenMai() == null ? null : ct.getKhuyenMai().getMaKM(),
                ct.getSoLuong(), BigDecimal.valueOf(ct.getDonGia()), ct.getSerialIMEI(), BigDecimal.valueOf(ct.getSoTienGiam()));
            capNhat(c, "UPDATE dbo.SanPham SET soLuongTon=soLuongTon-? WHERE maSP=? AND soLuongTon-soLuongLoi>=? AND tinhTrang='DANG_KINH_DOANH'",
                ct.getSoLuong(), sp, ct.getSoLuong());
        }
        capNhat(c, "UPDATE dbo.KhachHang SET diemTichLuy=diemTichLuy-?+? WHERE maKH=? AND diemTichLuy>=?",
            diemDung, diemCong, hd.getKhachHang().getMaKH(), diemDung);
    }

    public static void capNhat(Connection c, String sql, Object... values) throws SQLException {
        try (PreparedStatement p = c.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) p.setObject(i + 1, values[i]);
            if (p.executeUpdate() != 1) throw new SQLException("Dữ liệu đã thay đổi. Giao dịch chưa được lưu đầy đủ.");
        }
    }

    /**
     * Ma tang dan va duy nhat KH/HD/DH theo dac ta (8 chu so).
     * Su dung SEQUENCE cua SQL Server 2012+, khong tao ma bang UUID.
     * Database can co cac SEQUENCE Seq_GH_KH / Seq_GH_HD / Seq_GH_DH
     * (script 02 cho DB moi, script 03 cho DB da co du lieu).
     */
    public static String maMoi(String prefix) {
        if (!"KH".equals(prefix) && !"HD".equals(prefix) && !"DH".equals(prefix))
            throw new IllegalArgumentException("Chỉ hỗ trợ mã KH, HD hoặc DH.");
        String sql = "SELECT NEXT VALUE FOR dbo.Seq_GH_" + prefix;
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet r = st.executeQuery(sql)) {
            if (!r.next()) throw new SQLException("Không đọc được SEQUENCE " + prefix);
            long stt = r.getLong(1);
            if (stt < 1 || stt > 99_999_999L)
                throw new SQLException("Đã hết khoảng mã " + prefix + " + 8 chữ số.");
            return prefix + String.format(java.util.Locale.ROOT, "%08d", stt);
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể phát sinh mã " + prefix
                + ". Cần chạy script SQL chuẩn hóa mã 02/03 trước khi bán hàng.", ex);
        }
    }
}
