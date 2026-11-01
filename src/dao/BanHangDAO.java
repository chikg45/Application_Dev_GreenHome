package dao;

import connectdb.ConnectDB;
import connectdb.NguonKetNoi;
import connectdb.Jdbc;

import entity.*;
import enums.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/** Chỉ đọc/ghi SQL Bán hàng. Tính tiền và điều kiện nghiệp vụ thuộc BanHangBUS. */
public final class BanHangDAO {
    private final NguonKetNoi nguon;

    // Các record chỉ là kết quả SELECT/lệnh ghi, không phải kho dữ liệu mẫu.
    public record ThongTinNhanVien(NhanVien nhanVien, String vaiTro, boolean hoatDong) { }
    public record ThongTinSanPham(SanPham sanPham, int soLuongLoi, boolean quanLySerial, long soLuongDaGiu) { }
    public record ThongTinKhuyenMai(KhuyenMai khuyenMai, long soTienGiam, boolean dangApDung) { }
    public record DongDon(ThongTinSanPham sanPham, ChiTietDonHang chiTiet) { }
    public record ThongTinDon(DonHang don, String maNV, String maKH, long phienBan, List<DongDon> chiTiet) {
        public ThongTinDon { chiTiet = List.copyOf(chiTiet); }
    }
    public record DongLuu(DongHoaDonBanHang dong, String loaiGiam, Number giaTriGiam) { }
    public record BienDongDiem(String loai, int bienDong, int soDuSau) { }
    public record LenhLuuHoaDon(HoaDonBanHang hoaDon, String maNV, String tenNV, String maKH,
            String phuongThucSQL, int diemTruoc, List<DongLuu> chiTiet,
            Map<String, List<String>> serials, List<BienDongDiem> lichSuDiem) { }

    public BanHangDAO() { this(new ConnectDB()); }
    public BanHangDAO(NguonKetNoi nguon) { this.nguon = Objects.requireNonNull(nguon); }
    @FunctionalInterface
    public interface CongViec<T> { T thucHien(PhienDuLieu phien) throws SQLException; }
    public <T> T doc(CongViec<T> congViec) {
        return Jdbc.doc(nguon, c -> congViec.thucHien(new PhienDuLieu(c, false)));
    }
    public <T> T giaoDich(CongViec<T> congViec) {
        return Jdbc.giaoDich(nguon, c -> congViec.thucHien(new PhienDuLieu(c, true)));
    }

    /** Connection sống trong callback. BUS không nhận Connection hoặc viết câu SQL. */
    public static final class PhienDuLieu {
        private final Connection c;
        private final boolean giaoDich;
        private PhienDuLieu(Connection c, boolean giaoDich) { this.c = c; this.giaoDich = giaoDich; }
        private String khoaDoc() { return giaoDich ? " WITH (HOLDLOCK)" : ""; }
        private String khoaSua() { return giaoDich ? " WITH (UPDLOCK,HOLDLOCK)" : ""; }
        private void canTransaction() {
            if (!giaoDich) throw new IllegalStateException("Thao tác ghi cần nằm trong transaction của BUS.");
        }
        public LocalDateTime bayGio() throws SQLException { return Jdbc.bayGio(c); }
        public String maHoaDonMoi() throws SQLException { canTransaction(); return Jdbc.maMoi(c, "HD"); }
        public ThongTinNhanVien docNhanVien(String ma) throws SQLException {
            String sql = "SELECT n.maNV,n.hoTen,t.vaiTro,t.dangHoatDong FROM dbo.NHAN_VIEN n"
                + khoaDoc() + " LEFT JOIN dbo.TAI_KHOAN t" + khoaDoc() + " ON t.maNV=n.maNV WHERE n.maNV=?";
            try (PreparedStatement s = c.prepareStatement(sql)) {
                s.setString(1, ma);
                try (ResultSet r = s.executeQuery()) {
                    if (!r.next()) return null;
                    NhanVien nv = new NhanVien(); nv.setMaNV(ma); nv.setHoTen(r.getString("hoTen"));
                    return new ThongTinNhanVien(nv, r.getString("vaiTro"), r.getBoolean("dangHoatDong"));
                }
            }
        }
        private static String cotGiuHang(String boQuaDon) {
            return ",COALESCE((SELECT SUM(CAST(ct.soLuong AS bigint)) FROM dbo.CHI_TIET_DON_HANG ct JOIN dbo.DON_HANG dh ON dh.maDH=ct.maDH WHERE ct.maSP=p.maSP AND dh.trangThaiDH=N'ChuaThanhToan'" + boQuaDon + "),0) AS soLuongDaGiu";
        }
        public List<ThongTinSanPham> docSanPhams() throws SQLException {
            List<ThongTinSanPham> ds = new ArrayList<>();
            try (PreparedStatement s = c.prepareStatement("SELECT p.*,t.tenThuongHieu" + cotGiuHang("") + " FROM dbo.SAN_PHAM p JOIN dbo.THUONG_HIEU t ON t.maThuongHieu=p.maThuongHieu ORDER BY p.tenSP,p.maSP"); ResultSet r = s.executeQuery()) {
                while (r.next()) ds.add(anhXaSanPham(r));
            }
            return ds;
        }
        public ThongTinSanPham docSanPham(String ma, boolean khoa, String maDon) throws SQLException {
            String sql = "SELECT p.*,t.tenThuongHieu" + cotGiuHang(" AND dh.maDH<>?") + " FROM dbo.SAN_PHAM p" + (khoa ? khoaSua() : "")
                + " JOIN dbo.THUONG_HIEU t ON t.maThuongHieu=p.maThuongHieu WHERE p.maSP=?";
            try (PreparedStatement s = c.prepareStatement(sql)) {
                Jdbc.bind(s, maDon == null ? "" : maDon, ma);
                try (ResultSet r = s.executeQuery()) { return r.next() ? anhXaSanPham(r) : null; }
            }
        }
        public List<KhachHang> docKhachHangs() throws SQLException {
            List<KhachHang> ds = new ArrayList<>();
            try (PreparedStatement s = c.prepareStatement("SELECT * FROM dbo.KHACH_HANG ORDER BY tenKH,maKH"); ResultSet r = s.executeQuery()) {
                while (r.next()) ds.add(anhXaKhach(r));
            }
            return ds;
        }
        public KhachHang docKhach(String ma, boolean khoa) throws SQLException {
            if (ma == null) return null;
            try (PreparedStatement s = c.prepareStatement("SELECT * FROM dbo.KHACH_HANG" + (khoa ? khoaSua() : "") + " WHERE maKH=?")) {
                s.setString(1, ma);
                try (ResultSet r = s.executeQuery()) { return r.next() ? anhXaKhach(r) : null; }
            }
        }
        public Map<String, List<ThongTinKhuyenMai>> docKhuyenMaiSanPham() throws SQLException {
            Map<String, List<ThongTinKhuyenMai>> ds = new HashMap<>();
            String sql = "SELECT p.maSP,k.* FROM dbo.KHUYEN_MAI_SAN_PHAM p" + khoaDoc()
                + " JOIN dbo.KHUYEN_MAI k" + khoaDoc() + " ON k.maKM=p.maKM ORDER BY p.maSP,k.maKM";
            try (PreparedStatement s = c.prepareStatement(sql); ResultSet r = s.executeQuery()) {
                while (r.next()) ds.computeIfAbsent(r.getString("maSP"), x -> new ArrayList<>()).add(anhXaKhuyenMai(r));
            }
            return ds;
        }
        public ThongTinKhuyenMai docUuDaiCaNhan(String ma, String maKH) throws SQLException {
            if (ma.isEmpty() || maKH == null) return null;
            String sql = "SELECT k.* FROM dbo.UU_DAI_CA_NHAN u" + khoaDoc()
                + " JOIN dbo.KHUYEN_MAI k" + khoaDoc() + " ON k.maKM=u.maKM WHERE u.maKM=? AND u.maKH=?";
            try (PreparedStatement s = c.prepareStatement(sql)) {
                Jdbc.bind(s, ma, maKH);
                try (ResultSet r = s.executeQuery()) { return r.next() ? anhXaKhuyenMai(r) : null; }
            }
        }
        public ThongTinDon docDon(String ma, boolean khoaSanPham) throws SQLException {
            DonHang don; String nv, kh; long phienBan;
            try (PreparedStatement s = c.prepareStatement("SELECT * FROM dbo.DON_HANG" + khoaSua() + " WHERE maDH=?")) {
                s.setString(1, ma);
                try (ResultSet r = s.executeQuery()) {
                    if (!r.next()) return null;
                    don = new DonHang(ma, r.getTimestamp("ngayDat").toLocalDateTime(), r.getString("trangThaiDH"), 0);
                    nv = r.getString("maNV"); kh = r.getString("maKH"); phienBan = r.getLong("phienBan");
                }
            }
            record Dong(String maSP, int soLuong, double donGia, String serial) { }
            List<Dong> doc = new ArrayList<>();
            try (PreparedStatement s = c.prepareStatement("SELECT * FROM dbo.CHI_TIET_DON_HANG WHERE maDH=? ORDER BY maSP")) {
                s.setString(1, ma);
                try (ResultSet r = s.executeQuery()) {
                    while (r.next()) doc.add(new Dong(r.getString("maSP"), r.getInt("soLuong"), r.getDouble("donGia"), Jdbc.chuoi(r, "serialIMEI")));
                }
            }
            List<DongDon> rows = new ArrayList<>();
            // Cùng thứ tự mã sản phẩm ở mọi quầy để giảm deadlock.
            for (Dong d : doc) rows.add(new DongDon(docSanPham(d.maSP(), khoaSanPham, ma), new ChiTietDonHang(d.soLuong(), d.donGia(), d.serial())));
            return new ThongTinDon(don, nv, kh, phienBan, rows);
        }
        public List<String> docMaDonNhap(String maNV) throws SQLException {
            List<String> ds = new ArrayList<>();
            try (PreparedStatement s = c.prepareStatement("SELECT maDH FROM dbo.DON_HANG WHERE maNV=? AND trangThaiDH=N'ChuaThanhToan' ORDER BY ngayDat DESC,maDH DESC")) {
                s.setString(1, maNV);
                try (ResultSet r = s.executeQuery()) { while (r.next()) ds.add(r.getString(1)); }
            }
            return ds;
        }
        public DonHang themDon(String maNV, LocalDateTime ngay) throws SQLException {
            canTransaction(); String ma = Jdbc.maMoi(c, "DH");
            Jdbc.capNhat(c, "INSERT dbo.DON_HANG(maDH,maNV,ngayDat,trangThaiDH) VALUES(?,?,?,N'ChuaThanhToan')", ma, maNV, ngay);
            return new DonHang(ma, ngay, "ChuaThanhToan", 0);
        }
        public void luuSoLuong(String maDH, String maSP, int soLuong, long gia) throws SQLException {
            canTransaction();
            if (soLuong == 0) Jdbc.capNhat(c, "DELETE dbo.CHI_TIET_DON_HANG WHERE maDH=? AND maSP=?", maDH, maSP);
            else if (Jdbc.capNhat(c, "UPDATE dbo.CHI_TIET_DON_HANG SET soLuong=?,donGia=? WHERE maDH=? AND maSP=?", soLuong, gia, maDH, maSP) == 0)
                Jdbc.capNhat(c, "INSERT dbo.CHI_TIET_DON_HANG(maDH,maSP,soLuong,donGia) VALUES(?,?,?,?)", maDH, maSP, soLuong, gia);
            Jdbc.capNhat(c, "UPDATE dbo.DON_HANG SET phienBan=phienBan+1 WHERE maDH=?", maDH);
        }
        public void ganKhach(String maDH, String maKH) throws SQLException {
            canTransaction(); Jdbc.capNhat(c, "UPDATE dbo.DON_HANG SET maKH=?,phienBan=phienBan+1 WHERE maDH=?", maKH, maDH);
        }
        public void luuHuyDon(String maDH) throws SQLException {
            canTransaction(); Jdbc.capNhat(c, "UPDATE dbo.DON_HANG SET trangThaiDH=N'DaHuy',phienBan=phienBan+1 WHERE maDH=?", maDH);
        }
        public List<String> docSerialSanBan(String maSP, int soLuong) throws SQLException {
            canTransaction(); List<String> ds = new ArrayList<>();
            try (PreparedStatement s = c.prepareStatement("SELECT TOP (?) serialIMEI FROM dbo.THIET_BI_SERIAL WITH (UPDLOCK,HOLDLOCK) WHERE maSP=? AND tinhTrang='SanBan' ORDER BY serialIMEI")) {
                Jdbc.bind(s, soLuong, maSP);
                try (ResultSet r = s.executeQuery()) { while (r.next()) ds.add(r.getString(1)); }
            }
            return ds;
        }
        /** BUS đã kiểm tra và tính xong toàn bộ lệnh trước khi gọi hàm này. */
        public void luuHoaDon(LenhLuuHoaDon lenh) throws SQLException {
            canTransaction(); HoaDonBanHang hd = lenh.hoaDon();
            Jdbc.capNhat(c, "INSERT dbo.HOA_DON(maHD,maDH,maNV,maKH,ngayLap,phuongThucThanhToan,tenKhachHangLucBan,sdtKhachHangLucBan,tenNhanVienLucBan,diemDaDung,diemDaCong,soDuDiemSauGD,tienKhachDua,tienThua) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                hd.getMaHD(), hd.getMaDH(), lenh.maNV(), lenh.maKH(), hd.getNgayLap(), lenh.phuongThucSQL(), hd.getTenKH(), hd.getSdtKH(), lenh.tenNV(), hd.getDiemDaDung(), hd.getDiemDaCong(), hd.getSoDuDiem(), hd.getTienKhachDua(), hd.getTienThua());
            for (DongLuu luu : lenh.chiTiet()) {
                DongHoaDonBanHang row = luu.dong(); List<String> serials = lenh.serials().getOrDefault(row.getMaSP(), List.of());
                Jdbc.capNhat(c, "INSERT dbo.CHI_TIET_HOA_DON(maHD,maSP,tenSP,soLuong,donGia,serialIMEI,soTienGiam,maKM,giamKhuyenMai,giamDiem,loaiGiamApDung,giaTriGiamApDung,giaChuaThue,tienVAT) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    hd.getMaHD(), row.getMaSP(), row.getTenSP(), row.getSoLuong(), row.getDonGia(), serials.size() == 1 ? serials.getFirst() : null, row.getSoTienGiam(), row.getMaKM().isEmpty() ? null : row.getMaKM(), row.getGiamKhuyenMai(), row.getGiamDiem(), luu.loaiGiam(), luu.giaTriGiam(), row.getGiaChuaThue(), row.getTienVAT());
                int capNhat = Jdbc.capNhat(c, "UPDATE dbo.SAN_PHAM SET soLuongTon=soLuongTon-?,tinhTrang=CASE WHEN soLuongTon-?=0 THEN N'HetHang' ELSE N'DangBan' END WHERE maSP=? AND tinhTrang=N'DangBan' AND soLuongTon>=?", row.getSoLuong(), row.getSoLuong(), row.getMaSP(), row.getSoLuong());
                if (capNhat != 1) throw new SQLException("Tồn kho thay đổi trong giao dịch.");
                for (String serial : serials) {
                    if (Jdbc.capNhat(c, "UPDATE dbo.THIET_BI_SERIAL SET tinhTrang='DaBan',maHD=? WHERE serialIMEI=? AND maSP=? AND tinhTrang='SanBan'", hd.getMaHD(), serial, row.getMaSP()) != 1)
                        throw new SQLException("Serial thay đổi trong giao dịch.");
                    Jdbc.capNhat(c, "INSERT dbo.SERIAL_HOA_DON(maHD,maSP,serialIMEI) VALUES(?,?,?)", hd.getMaHD(), row.getMaSP(), serial);
                }
            }
            if (lenh.maKH() != null) {
                if (Jdbc.capNhat(c, "UPDATE dbo.KHACH_HANG SET diemTichLuy=? WHERE maKH=? AND diemTichLuy=?", hd.getSoDuDiem(), lenh.maKH(), lenh.diemTruoc()) != 1)
                    throw new SQLException("Số dư điểm thay đổi trong giao dịch.");
                for (BienDongDiem diem : lenh.lichSuDiem())
                    Jdbc.capNhat(c, "INSERT dbo.LICH_SU_DIEM(maKH,maHD,thoiDiem,loaiGiaoDich,bienDong,soDuSau) VALUES(?,?,?,?,?,?)", lenh.maKH(), hd.getMaHD(), hd.getNgayLap(), diem.loai(), diem.bienDong(), diem.soDuSau());
            }
            if (Jdbc.capNhat(c, "UPDATE dbo.DON_HANG SET trangThaiDH=N'DaThanhToan',phienBan=phienBan+1 WHERE maDH=? AND trangThaiDH=N'ChuaThanhToan'", hd.getMaDH()) != 1)
                throw new SQLException("Đơn hàng thay đổi trong giao dịch.");
        }
        // Đọc hóa đơn snapshot; không tính lại theo giá/khuyến mãi hiện tại.
        public List<HoaDonBanHang> docHoaDons(String ma) throws SQLException {
            record Dau(String hd, String dh, String kh, String tenKH, String sdt, String nv, String tenNV,
                    LocalDateTime ngay, PhuongThucThanhToan pt, int dung, int cong, int soDu, long nhan) { }
            List<Dau> dau = new ArrayList<>();
            try (PreparedStatement s = c.prepareStatement("SELECT * FROM dbo.HOA_DON" + (ma == null ? "" : " WHERE maHD=?") + " ORDER BY ngayLap DESC,maHD DESC")) {
                if (ma != null) s.setString(1, ma);
                try (ResultSet r = s.executeQuery()) {
                    while (r.next()) dau.add(new Dau(r.getString("maHD"), r.getString("maDH"), r.getString("maKH"), r.getString("tenKhachHangLucBan"), Jdbc.chuoi(r, "sdtKhachHangLucBan"), r.getString("maNV"), r.getString("tenNhanVienLucBan"), r.getTimestamp("ngayLap").toLocalDateTime(), PhuongThucThanhToan.tuChuoi(r.getString("phuongThucThanhToan")), r.getInt("diemDaDung"), r.getInt("diemDaCong"), r.getInt("soDuDiemSauGD"), r.getBigDecimal("tienKhachDua").longValueExact()));
                }
            }
            List<HoaDonBanHang> ds = new ArrayList<>();
            for (Dau h : dau) {
                Map<String, List<String>> serials = new HashMap<>();
                try (PreparedStatement s = c.prepareStatement("SELECT maSP,serialIMEI FROM dbo.SERIAL_HOA_DON WHERE maHD=? ORDER BY maSP,serialIMEI")) {
                    s.setString(1, h.hd());
                    try (ResultSet r = s.executeQuery()) { while (r.next()) serials.computeIfAbsent(r.getString(1), x -> new ArrayList<>()).add(r.getString(2)); }
                }
                List<DongHoaDonBanHang> rows = new ArrayList<>();
                try (PreparedStatement s = c.prepareStatement("SELECT * FROM dbo.CHI_TIET_HOA_DON WHERE maHD=? ORDER BY maSP")) {
                    s.setString(1, h.hd());
                    try (ResultSet r = s.executeQuery()) {
                        while (r.next()) {
                            String sp = r.getString("maSP"); String serial = serials.containsKey(sp) ? String.join(", ", serials.get(sp)) : Jdbc.chuoi(r, "serialIMEI");
                            rows.add(new DongHoaDonBanHang(sp, r.getString("tenSP"), r.getInt("soLuong"), r.getBigDecimal("donGia").longValueExact(), serial, r.getBigDecimal("giamKhuyenMai").longValueExact(), r.getBigDecimal("giamDiem").longValueExact(), Jdbc.chuoi(r, "maKM")));
                        }
                    }
                }
                KhachHang kh = h.kh() == null ? null : new KhachHang(h.kh(), h.tenKH(), h.sdt(), "", h.soDu());
                NhanVien nv = new NhanVien(); nv.setMaNV(h.nv()); nv.setHoTen(h.tenNV());
                ds.add(new HoaDonBanHang(h.hd(), h.dh(), h.ngay(), kh, nv, h.pt(), rows, h.dung(), h.cong(), h.soDu(), h.nhan()));
            }
            return ds;
        }
    }
    private static ThongTinSanPham anhXaSanPham(ResultSet r) throws SQLException {
        SanPham sp = new SanPham(r.getString("maSP"), r.getString("tenSP"), DanhMuc.tuChuoi(r.getString("danhMuc")),
            new ThuongHieu(r.getString("maThuongHieu"), r.getString("tenThuongHieu")), Jdbc.chuoi(r, "thongSoKyThuat"),
            r.getBigDecimal("giaNiemYet").longValueExact(), Jdbc.chuoi(r, "model"), Jdbc.chuoi(r, "donViTinh"),
            Jdbc.chuoi(r, "moTaSP"), TinhTrangSP.tuChuoi(r.getString("tinhTrang")), r.getInt("soLuongTon"), r.getInt("soLuongTonToiThieu"), Jdbc.chuoi(r, "hinhAnh"));
        return new ThongTinSanPham(sp, r.getInt("soLuongLoi"), r.getBoolean("quanLySerial"), r.getLong("soLuongDaGiu"));
    }
    private static KhachHang anhXaKhach(ResultSet r) throws SQLException {
        return new KhachHang(r.getString("maKH"), r.getString("tenKH"), Jdbc.chuoi(r, "sdtKH"), Jdbc.chuoi(r, "diaChi"), r.getInt("diemTichLuy"));
    }
    private static ThongTinKhuyenMai anhXaKhuyenMai(ResultSet r) throws SQLException {
        KhuyenMai km = new KhuyenMai(r.getString("maKM"), r.getString("tenKM"), r.getDouble("phanTramGiam"), r.getTimestamp("ngayBatDau").toLocalDateTime(), r.getTimestamp("ngayKetThuc").toLocalDateTime(), Jdbc.chuoi(r, "moTaKM"), r.getString("loaiGiam"));
        return new ThongTinKhuyenMai(km, r.getBigDecimal("soTienGiam").longValueExact(), r.getBoolean("dangApDung"));
    }
}
