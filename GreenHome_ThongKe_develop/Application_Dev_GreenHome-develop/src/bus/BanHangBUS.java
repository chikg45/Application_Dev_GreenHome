package bus;

import dao.BanHangDAO;
import dao.BanHangDAO.*;
import entity.*;
import enums.*;
import java.math.BigInteger;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Bán hàng GreenHome: UI gọi BUS kiểm tra quyền, số lượng, tiền, ưu đãi và điểm rồi mới gọi DAO ghi.
 * Khi thanh toán, các kiểm tra dùng dữ liệu SQL vừa khóa trong cùng transaction.
 */
public final class BanHangBUS {
    private final BanHangDAO dao;
    private final String maNhanVien;
    // Cache kết quả SELECT để hiển thị, không phải nơi lưu đơn/hóa đơn.
    private Map<String, ThongTinSanPham> sanPhamHienThi = Map.of();
    private Map<String, List<ThongTinKhuyenMai>> khuyenMaiHienThi = Map.of();

    public BanHangBUS(NhanVien nv) { this(new BanHangDAO(), nv); }
    public BanHangBUS(BanHangDAO dao, NhanVien nv) {
        this.dao = Objects.requireNonNull(dao);
        if (nv == null || nv.getMaNV() == null || nv.getMaNV().isBlank())
            throw new IllegalArgumentException("Cần nhân viên từ phiên đăng nhập của nhóm.");
        maNhanVien = nv.getMaNV();
        lamMoiDuLieu();
    }
    private ThongTinNhanVien kiemTraQuyen(PhienDuLieu phien) throws SQLException {
        ThongTinNhanVien nv = phien.docNhanVien(maNhanVien);
        if (nv == null || !nv.hoatDong() || nv.vaiTro() == null
                || !Set.of("QuanLy", "NhanVienBanHang", "QUAN_LY", "NHAN_VIEN_BAN_HANG").contains(nv.vaiTro()))
            throw new IllegalStateException("Nhân viên không có quyền bán hàng hoặc tài khoản đã khóa.");
        return nv;
    }
    private ThongTinDon kiemTraDon(ThongTinDon d) {
        if (d == null) throw new IllegalArgumentException("Đơn hàng không còn tồn tại.");
        if (!maNhanVien.equals(d.maNV())) throw new IllegalStateException("Đơn hàng thuộc nhân viên khác.");
        if (TrangThaiDonHang.tuChuoi(d.don().getTrangThaiDH()) != TrangThaiDonHang.CHUA_THANH_TOAN)
            throw new IllegalStateException("Đơn hàng đã thanh toán hoặc đã hủy.");
        return d;
    }
    private static void kiemTraSanPham(ThongTinSanPham p, int sl) {
        if (p == null) throw new IllegalArgumentException("Sản phẩm không còn tồn tại.");
        if (sl <= 0 || sl > soLuongCoTheBan(p) || p.sanPham().getTinhTrang() != TinhTrangSP.DANG_BAN)
            throw new IllegalArgumentException("Không đủ hàng sẵn bán hoặc sản phẩm đã ngừng bán.");
        if (TienVND.tuDouble(p.sanPham().getGiaNiemYet()) <= 0)
            throw new IllegalArgumentException("Giá sản phẩm phải lớn hơn 0.");
    }
    private static int soLuongCoTheBan(ThongTinSanPham p) { return (int) Math.max(0, (long) p.sanPham().getSoLuongTon() - p.soLuongDaGiu()); }
    private static String maDon(DonHang don) {
        if (don == null || don.getMaDH() == null || don.getMaDH().isBlank())
            throw new IllegalArgumentException("Chưa có đơn hàng hợp lệ.");
        return don.getMaDH();
    }
    private static String maKhach(KhachHang kh) {
        if (kh == null) return null;
        if (kh.getMaKH() == null || kh.getMaKH().isBlank()) throw new IllegalArgumentException("Mã khách hàng không hợp lệ.");
        return kh.getMaKH();
    }
    private static KhachHang docKhach(PhienDuLieu p, String ma, boolean khoa) throws SQLException {
        KhachHang kh = p.docKhach(ma, khoa);
        if (ma != null && kh == null) throw new IllegalArgumentException("Khách hàng không còn tồn tại.");
        return kh;
    }
    public static String chuanHoaMa(String ma) { return ma == null ? "" : ma.trim().toUpperCase(Locale.ROOT); }
    private static boolean hieuLuc(ThongTinKhuyenMai k, LocalDateTime luc) {
        return k != null && k.dangApDung() && !luc.isBefore(k.khuyenMai().getNgayBatDau())
            && !luc.isAfter(k.khuyenMai().getNgayKetThuc());
    }
    private static void kiemTraUuDai(String ma, KhachHang kh, ThongTinKhuyenMai km, LocalDateTime luc) {
        if (ma.isEmpty()) return;
        if (kh == null) throw new IllegalArgumentException("Chọn khách có hồ sơ để dùng ưu đãi cá nhân.");
        if (km == null) throw new IllegalArgumentException("Mã ưu đãi không tồn tại hoặc không dành cho khách này.");
        if (!hieuLuc(km, luc)) throw new IllegalArgumentException("Ưu đãi cá nhân chưa hiệu lực, đã hết hạn hoặc đã ngừng.");
    }
    private static long mucGiam(ThongTinKhuyenMai k, long tien) {
        return LoaiGiam.tuChuoi(k.khuyenMai().getLoaiGiam()) == LoaiGiam.SO_TIEN
            ? Math.min(tien, k.soTienGiam()) : TienVND.giamPhanTram(tien, k.khuyenMai().getPhanTramGiam());
    }
    private static boolean duHoSoDiem(KhachHang kh) {
        return kh != null && kh.getTenKH() != null && !kh.getTenKH().isBlank()
            && kh.getSdtKH() != null && kh.getSdtKH().matches("0[0-9]{9}");
    }
    public void lamMoiDuLieu() {
        record KetQua(List<ThongTinSanPham> sp, Map<String, List<ThongTinKhuyenMai>> km) { }
        KetQua k = dao.doc(p -> { kiemTraQuyen(p); return new KetQua(p.docSanPhams(), p.docKhuyenMaiSanPham()); });
        Map<String, ThongTinSanPham> sp = new LinkedHashMap<>();
        k.sp().forEach(row -> sp.put(row.sanPham().getMaSP(), row));
        sanPhamHienThi = Collections.unmodifiableMap(sp); khuyenMaiHienThi = k.km();
    }
    public List<SanPham> getSanPhams() { return sanPhamHienThi.values().stream().map(ThongTinSanPham::sanPham).toList(); }
    public int getSoLuongCoTheBan(SanPham sp) {
        ThongTinSanPham p = sanPhamHienThi.get(sp.getMaSP()); return p == null ? 0 : soLuongCoTheBan(p);
    }
    public int getPhanTramGiam(SanPham sp) {
        LocalDateTime luc = LocalDateTime.now();
        return khuyenMaiHienThi.getOrDefault(sp.getMaSP(), List.of()).stream()
            .filter(k -> hieuLuc(k, luc) && LoaiGiam.tuChuoi(k.khuyenMai().getLoaiGiam()) == LoaiGiam.PHAN_TRAM)
            .mapToInt(k -> (int) k.khuyenMai().getPhanTramGiam()).max().orElse(0);
    }
    public List<KhachHang> getKhachHangs() { return dao.doc(p -> { kiemTraQuyen(p); return p.docKhachHangs(); }); }
    public void kiemTraUuDai(String ma, KhachHang kh) {
        String code = chuanHoaMa(ma), id = maKhach(kh);
        dao.doc(p -> { kiemTraQuyen(p); kiemTraUuDai(code, docKhach(p, id, false), p.docUuDaiCaNhan(code, id), p.bayGio()); return null; });
    }
    public DonHang taoDon() {
        return dao.giaoDich(p -> { kiemTraQuyen(p); return p.themDon(maNhanVien, p.bayGio()); });
    }
    /** Cho UI khôi phục hoặc hủy nháp còn giữ hàng sau khi mở lại ứng dụng. */
    public List<DonHang> getDonChuaThanhToan() {
        return dao.doc(p -> {
            kiemTraQuyen(p); List<DonHang> ds = new ArrayList<>();
            for (String ma : p.docMaDonNhap(maNhanVien)) {
                ThongTinDon d = p.docDon(ma, false);
                if (d != null && TrangThaiDonHang.tuChuoi(d.don().getTrangThaiDH()) == TrangThaiDonHang.CHUA_THANH_TOAN)
                    ds.add(donCoTongTien(kiemTraDon(d)));
            }
            return List.copyOf(ds);
        });
    }
    public DonHang timDonChuaThanhToan(String ma) {
        if (ma == null || ma.isBlank()) throw new IllegalArgumentException("Nhập mã đơn cần mở lại.");
        return dao.doc(p -> { kiemTraQuyen(p); return donCoTongTien(kiemTraDon(p.docDon(ma, false))); });
    }
    private static DonHang donCoTongTien(ThongTinDon d) {
        long tong = 0;
        for (DongDon row : d.chiTiet()) tong = Math.addExact(tong,
            Math.multiplyExact(TienVND.tuDouble(row.sanPham().sanPham().getGiaNiemYet()), row.chiTiet().getSoLuong()));
        d.don().setTongTienDonHang(tong); return d.don();
    }
    public Map<SanPham, Integer> getGioHang(DonHang don) {
        String ma = maDon(don);
        return dao.doc(p -> {
            kiemTraQuyen(p); ThongTinDon d = kiemTraDon(p.docDon(ma, false));
            Map<SanPham, Integer> map = new LinkedHashMap<>();
            for (DongDon row : d.chiTiet()) map.put(row.sanPham().sanPham(), row.chiTiet().getSoLuong());
            return Collections.unmodifiableMap(map);
        });
    }
    public int getSoLuong(DonHang don, String maSP) {
        return getGioHang(don).entrySet().stream().filter(e -> e.getKey().getMaSP().equals(maSP)).mapToInt(Map.Entry::getValue).findFirst().orElse(0);
    }
    public void datSoLuong(DonHang don, String maSP, int soLuong) {
        String ma = maDon(don);
        if (maSP == null || maSP.isBlank() || soLuong < 0) throw new IllegalArgumentException("Mã sản phẩm/số lượng không hợp lệ.");
        long tong = dao.giaoDich(p -> {
            kiemTraQuyen(p); ThongTinDon d = kiemTraDon(p.docDon(ma, true));
            ThongTinSanPham sp = p.docSanPham(maSP, true, ma);
            if (soLuong > 0) kiemTraSanPham(sp, soLuong);
            else if (sp == null) throw new IllegalArgumentException("Sản phẩm không còn tồn tại.");
            long gia = TienVND.tuDouble(sp.sanPham().getGiaNiemYet());
            long tongMoi = Math.multiplyExact(gia, soLuong);
            for (DongDon row : d.chiTiet()) if (!row.sanPham().sanPham().getMaSP().equals(maSP))
                tongMoi = Math.addExact(tongMoi, Math.multiplyExact(TienVND.tuDouble(row.sanPham().sanPham().getGiaNiemYet()), row.chiTiet().getSoLuong()));
            p.luuSoLuong(ma, maSP, soLuong, gia); // Chỉ ghi sau các kiểm tra trên.
            return tongMoi;
        });
        don.setTongTienDonHang(tong);
    }
    public void huyDon(DonHang don) {
        String ma = maDon(don);
        dao.giaoDich(p -> { kiemTraQuyen(p); kiemTraDon(p.docDon(ma, true)); p.luuHuyDon(ma); return null; });
        don.setTrangThaiDH("DaHuy");
    }

    private static long phanBo(long tien, long trongSo, long tong) {
        return tong == 0 ? 0 : BigInteger.valueOf(tien).multiply(BigInteger.valueOf(trongSo)).divide(BigInteger.valueOf(tong)).longValueExact();
    }
    /** Hàm nghiệp vụ thuần: không JDBC, không sửa tồn/điểm. */
    private static BaoGia tinhGia(ThongTinDon d, KhachHang kh, String ma, boolean dungDiem,
            Map<String, List<ThongTinKhuyenMai>> kms, ThongTinKhuyenMai caNhan, LocalDateTime luc) {
        kiemTraUuDai(ma, kh, caNhan, luc);
        boolean coHoSoDiem = duHoSoDiem(kh);
        if (dungDiem && !coHoSoDiem) throw new IllegalArgumentException("Chỉ khách có đủ họ tên và số điện thoại mới được dùng điểm.");
        long tong = 0;
        for (DongDon row : d.chiTiet()) {
            kiemTraSanPham(row.sanPham(), row.chiTiet().getSoLuong());
            tong = Math.addExact(tong, Math.multiplyExact(TienVND.tuDouble(row.sanPham().sanPham().getGiaNiemYet()), row.chiTiet().getSoLuong()));
        }
        long nganSach = caNhan == null ? 0 : mucGiam(caNhan, tong), tongCon = tong, tongKM = 0;
        List<DongHoaDonBanHang> tam = new ArrayList<>();
        for (DongDon row : d.chiTiet()) {
            SanPham sp = row.sanPham().sanPham(); int sl = row.chiTiet().getSoLuong();
            long gia = TienVND.tuDouble(sp.getGiaNiemYet()), tien = Math.multiplyExact(gia, sl), giam = 0;
            String maKM = "";
            for (ThongTinKhuyenMai k : kms.getOrDefault(sp.getMaSP(), List.of())) if (hieuLuc(k, luc)) {
                long giaTri = Math.multiplyExact(mucGiam(k, gia), sl);
                if (giaTri > giam) { giam = giaTri; maKM = k.khuyenMai().getMaKM(); }
            }
            long ca = phanBo(nganSach, tien, tongCon); nganSach -= ca; tongCon -= tien;
            if (ca > giam) { giam = ca; maKM = caNhan.khuyenMai().getMaKM(); }
            tam.add(new DongHoaDonBanHang(sp.getMaSP(), sp.getTenSP(), sl, gia, "", giam, 0, maKM));
            tongKM = Math.addExact(tongKM, giam);
        }
        long sauKM = tong - tongKM;
        int diem = dungDiem ? (int) Math.min(kh.getDiemTichLuy(), sauKM / 1000) : 0;
        long conDiem = diem * 1000L; tongCon = sauKM;
        List<DongHoaDonBanHang> result = new ArrayList<>();
        for (DongHoaDonBanHang row : tam) {
            long value = phanBo(conDiem, row.getThucTra(), tongCon); conDiem -= value; tongCon -= row.getThucTra();
            result.add(new DongHoaDonBanHang(row.getMaSP(), row.getTenSP(), row.getSoLuong(), row.getDonGia(), "", row.getGiamKhuyenMai(), value, row.getMaKM()));
        }
        long canTra = sauKM - diem * 1000L;
        return new BaoGia(d.phienBan(), d.don().getMaDH(), kh == null ? "" : kh.getMaKH(), ma, dungDiem,
            kh == null ? 0 : kh.getDiemTichLuy(), result, tong, tongKM, diem,
            coHoSoDiem ? Math.toIntExact(canTra / 100000) : 0, canTra);
    }
    public BaoGia baoGia(DonHang don, KhachHang kh, String ma, boolean dungDiem) {
        String id = maDon(don), maKH = maKhach(kh), code = chuanHoaMa(ma);
        BaoGia q = dao.giaoDich(p -> {
            kiemTraQuyen(p); ThongTinDon d = kiemTraDon(p.docDon(id, false));
            KhachHang current = docKhach(p, maKH, false);
            boolean doiKhach = !Objects.equals(d.maKH(), maKH);
            if (doiKhach) d = new ThongTinDon(d.don(), d.maNV(), maKH, Math.addExact(d.phienBan(), 1), d.chiTiet());
            BaoGia gia = tinhGia(d, current, code, dungDiem, p.docKhuyenMaiSanPham(), p.docUuDaiCaNhan(code, maKH), p.bayGio());
            if (doiKhach) p.ganKhach(id, maKH); // Báo giá hợp lệ mới ghi lựa chọn khách.
            return gia;
        });
        don.setTongTienDonHang(q.tongHang()); if (kh != null) kh.setDiemTichLuy(q.diemHienTai());
        return q;
    }

    public HoaDonBanHang thanhToan(DonHang don, KhachHang kh, String ma, boolean dungDiem, NhanVien nv,
            PhuongThucThanhToan pt, long tienNhan, boolean daNhanChuyenKhoan, BaoGia daXacNhan) {
        String id = maDon(don), maKH = maKhach(kh), code = chuanHoaMa(ma);
        if (nv == null || !maNhanVien.equals(nv.getMaNV())) throw new IllegalArgumentException("Nhân viên không trùng phiên đăng nhập.");
        if (pt == null || tienNhan < 0 || daXacNhan == null) throw new IllegalArgumentException("Thông tin thanh toán chưa hợp lệ.");
        if (pt == PhuongThucThanhToan.CHUYEN_KHOAN && !daNhanChuyenKhoan)
            throw new IllegalArgumentException("Chưa xác nhận tiền vào tài khoản.");
        HoaDonBanHang hd = dao.giaoDich(p -> {
            ThongTinNhanVien nguoiBan = kiemTraQuyen(p);
            ThongTinDon d = kiemTraDon(p.docDon(id, true));
            if (!Objects.equals(maKH, d.maKH())) throw new IllegalStateException("Khách hàng thay đổi. Hãy báo giá lại.");
            KhachHang current = docKhach(p, maKH, true);
            Map<String, List<ThongTinKhuyenMai>> kms = p.docKhuyenMaiSanPham();
            ThongTinKhuyenMai cn = p.docUuDaiCaNhan(code, maKH); LocalDateTime luc = p.bayGio();
            BaoGia gia = tinhGia(d, current, code, dungDiem, kms, cn, luc);
            if (gia.chiTiet().isEmpty()) throw new IllegalArgumentException("Giỏ hàng đang trống.");
            if (!daXacNhan.cungGia(gia)) throw new IllegalStateException("Giá, điểm, ưu đãi hoặc giỏ đã đổi. Hãy kiểm tra và xác nhận lại.");
            if (tienNhan < gia.canTra()) throw new IllegalArgumentException("Tiền nhận chưa đủ.");
            Map<String, List<String>> serials = new HashMap<>();
            for (DongDon row : d.chiTiet()) if (row.sanPham().quanLySerial()) {
                String sp = row.sanPham().sanPham().getMaSP(); int sl = row.chiTiet().getSoLuong();
                List<String> list = p.docSerialSanBan(sp, sl);
                if (list.size() != sl) throw new IllegalArgumentException("Không đủ Serial/IMEI sẵn bán.");
                serials.put(sp, List.copyOf(list));
            }
            List<DongHoaDonBanHang> rows = new ArrayList<>();
            List<DongLuu> dongLuu = new ArrayList<>(); Map<String, ThongTinKhuyenMai> kmDaDoc = new HashMap<>();
            kms.values().forEach(list -> list.forEach(k -> kmDaDoc.put(k.khuyenMai().getMaKM(), k)));
            if (cn != null) kmDaDoc.put(cn.khuyenMai().getMaKM(), cn);
            for (DongHoaDonBanHang row : gia.chiTiet()) {
                DongHoaDonBanHang luu = new DongHoaDonBanHang(row.getMaSP(), row.getTenSP(), row.getSoLuong(), row.getDonGia(), String.join(", ", serials.getOrDefault(row.getMaSP(), List.of())), row.getGiamKhuyenMai(), row.getGiamDiem(), row.getMaKM());
                rows.add(luu); ThongTinKhuyenMai k = kmDaDoc.get(row.getMaKM());
                LoaiGiam loai = k == null ? null : LoaiGiam.tuChuoi(k.khuyenMai().getLoaiGiam());
                dongLuu.add(new DongLuu(luu, loai == null ? null : loai == LoaiGiam.PHAN_TRAM ? "PhanTram" : "SoTien",
                    k == null ? null : loai == LoaiGiam.PHAN_TRAM ? k.khuyenMai().getPhanTramGiam() : k.soTienGiam()));
            }
            int diemTruoc = current == null ? 0 : current.getDiemTichLuy();
            int sauTru = diemTruoc - gia.diemDung(), diemSau = Math.addExact(sauTru, gia.diemCong());
            List<BienDongDiem> bienDong = new ArrayList<>();
            if (gia.diemDung() > 0) bienDong.add(new BienDongDiem("SuDung", -gia.diemDung(), sauTru));
            if (gia.diemCong() > 0) bienDong.add(new BienDongDiem("TichLuy", gia.diemCong(), diemSau));
            HoaDonBanHang moi = new HoaDonBanHang(p.maHoaDonMoi(), id, luc, current, nguoiBan.nhanVien(), pt, rows, gia.diemDung(), gia.diemCong(), diemSau, tienNhan);
            LenhLuuHoaDon lenh = new LenhLuuHoaDon(moi, maNhanVien, nguoiBan.nhanVien().getHoTen(), maKH,
                pt == PhuongThucThanhToan.TIEN_MAT ? "TienMat" : "ChuyenKhoan", diemTruoc, List.copyOf(dongLuu), Map.copyOf(serials), List.copyOf(bienDong));
            // Mọi điều kiện đã đạt: đến đây DAO mới ghi HD/CTHD/tồn/Serial/điểm.
            p.luuHoaDon(lenh);
            return moi;
        });
        don.setTrangThaiDH("DaThanhToan"); if (kh != null) kh.setDiemTichLuy(hd.getSoDuDiem());
        try { lamMoiDuLieu(); } catch (IllegalStateException ex) { System.err.println("Hóa đơn đã lưu; cần tải lại danh mục."); }
        return hd;
    }
    public List<HoaDonBanHang> getHoaDons() { return dao.doc(p -> { kiemTraQuyen(p); return p.docHoaDons(null); }); }
    public HoaDonBanHang timHoaDon(String ma) {
        if (ma == null || ma.isBlank()) throw new IllegalArgumentException("Nhập mã hóa đơn cần tìm.");
        return dao.doc(p -> { kiemTraQuyen(p); var ds = p.docHoaDons(ma); return ds.isEmpty() ? null : ds.getFirst(); });
    }
}
