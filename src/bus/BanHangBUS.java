package bus;

import dao.BanHangDAO;
import entity.*;
import enums.TinhTrangSP;
import java.math.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

/** Kiểm tra nghiệp vụ, tính tiền bằng BigDecimal; entity gốc được giữ nguyên. */
public class BanHangBUS {
    private final BanHangDAO dao = new BanHangDAO();

    public record DongGia(String maSP, String tenSP, int soLuong, BigDecimal donGia,
                          BigDecimal giamKM, BigDecimal giamDiem, String maKM) {
        public BigDecimal tongHang() { return donGia.multiply(BigDecimal.valueOf(soLuong)); }
        public BigDecimal thucTra() { return tongHang().subtract(giamKM).subtract(giamDiem); }
    }
    public record BaoGia(String maKH, int diemHienTai, String maKM, boolean dungDiem,
                         List<DongGia> dong, BigDecimal tongHang, BigDecimal giamKM,
                         int diemDung, int diemCong, BigDecimal canTra) {
        public BaoGia { dong = List.copyOf(dong); }
    }
    public List<SanPham> getSanPhams() throws SQLException {
        return dao.doc(c -> dao.sanPhams(c, null, false));
    }

    public List<KhachHang> getKhachHangs() throws SQLException {
        return dao.doc(c -> dao.khachHangs(c, null, false));
    }

    public KhachHang getKhachVangLai() throws SQLException {
        return dao.khachVangLai();
    }

    // ==================== NGHIEP VU SAN PHAM ====================

    /**
     * Tinh gia san pham sau khi giam theo phan tram.
     */
    public static BigDecimal tinhGiaSauGiam(
            SanPham sp, double phanTramGiam) {

        if (sp == null) {
            throw new IllegalArgumentException("Sản phẩm không được null.");
        }
        if (!Double.isFinite(phanTramGiam)
                || phanTramGiam < 0 || phanTramGiam > 100) {
            throw new IllegalArgumentException("Phần trăm giảm không hợp lệ.");
        }

        return tien(sp.getGiaNiemYet())
                .multiply(BigDecimal.valueOf(100 - phanTramGiam))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * Kiem tra san pham co the ban va du so luong ton hay khong.
     */
    public static boolean kiemTraConHang(SanPham sp, int soLuongCan) {
        return sp != null
                && soLuongCan > 0
                && sp.getTinhTrang() == TinhTrangSP.DangBan
                && sp.getSoLuongCoTheBan() >= soLuongCan;
    }

    // ==================== NGHIEP VU DON HANG ====================

    public static boolean coTheXacNhanDonHang(DonHang donHang) {
        return donHang != null
                && donHang.getMaDH() != null
                && !donHang.getMaDH().isBlank()
                && donHang.getNgayDat() != null
                && donHang.getTrangThaiDH() != null
                && donHang.getKhachHang() != null
                && donHang.getNhanVien() != null
                && !donHang.getDanhSachChiTietDonHang().isEmpty();
    }

    public static BigDecimal tinhThanhTienDonHang(ChiTietDonHang chiTiet) {
        if (chiTiet == null) {
            throw new IllegalArgumentException("Chi tiết đơn hàng không được null.");
        }
        return BigDecimal.valueOf(chiTiet.getDonGia())
                .multiply(BigDecimal.valueOf(chiTiet.getSoLuong()))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal tinhTongTienDonHang(DonHang donHang) {
        if (donHang == null) {
            throw new IllegalArgumentException("Đơn hàng không được null.");
        }

        BigDecimal tong = BigDecimal.ZERO;
        for (ChiTietDonHang chiTiet : donHang.getDanhSachChiTietDonHang()) {
            tong = tong.add(tinhThanhTienDonHang(chiTiet));
        }
        return tong.setScale(2, RoundingMode.HALF_UP);
    }

    // ==================== NGHIEP VU HOA DON ====================

    public static BigDecimal tinhThanhTienChiTietHoaDon(ChiTietHoaDon chiTiet) {
        if (chiTiet == null) {
            throw new IllegalArgumentException("Chi tiết hóa đơn không được null.");
        }

        BigDecimal tienHang = BigDecimal.valueOf(chiTiet.getDonGia())
                .multiply(BigDecimal.valueOf(chiTiet.getSoLuong()));
        BigDecimal thanhTien = tienHang
                .subtract(BigDecimal.valueOf(chiTiet.getSoTienGiam()));

        return thanhTien.max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal tinhTongTienHang(HoaDon hoaDon) {
        if (hoaDon == null) {
            throw new IllegalArgumentException("Hóa đơn không được null.");
        }

        BigDecimal tong = BigDecimal.ZERO;
        for (ChiTietHoaDon chiTiet : hoaDon.getDanhSachChiTietHoaDon()) {
            tong = tong.add(BigDecimal.valueOf(chiTiet.getDonGia())
                    .multiply(BigDecimal.valueOf(chiTiet.getSoLuong())));
        }
        return tong.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal tinhTongTienGiamGia(HoaDon hoaDon) {
        if (hoaDon == null) {
            throw new IllegalArgumentException("Hóa đơn không được null.");
        }

        BigDecimal tongGiam = BigDecimal.ZERO;
        for (ChiTietHoaDon chiTiet : hoaDon.getDanhSachChiTietHoaDon()) {
            tongGiam = tongGiam.add(BigDecimal.valueOf(chiTiet.getSoTienGiam()));
        }
        return tongGiam.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal tinhTongTienThanhToan(HoaDon hoaDon) {
        BigDecimal thanhToan = tinhTongTienHang(hoaDon)
                .subtract(tinhTongTienGiamGia(hoaDon));
        return thanhToan.max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }


    public KhachHang themKhach(String ten, String sdt, String diaChi) throws SQLException {
        return dao.themKhach(ten.trim(), sdt.trim(), diaChi.trim());
    }
    public static BigDecimal tien(double n) {
        if (!Double.isFinite(n) || n < 0) throw new IllegalArgumentException("Giá trị tiền không hợp lệ.");
        return BigDecimal.valueOf(n).setScale(2, RoundingMode.HALF_UP);
    }
    public static BigDecimal giam(KhuyenMai k, BigDecimal gia, LocalDateTime luc) {
        if (k.getNgayBatDau() == null || k.getNgayKetThuc() == null
                || luc.isBefore(k.getNgayBatDau()) || luc.isAfter(k.getNgayKetThuc())) return tien(0);
        BigDecimal n;
        if (LoaiGiam.PHAN_TRAM.name().equals(k.getLoaiGiam())) {
            if (!Double.isFinite(k.getPhanTramGiam()) || k.getPhanTramGiam() < 0 || k.getPhanTramGiam() > 100)
                throw new IllegalArgumentException("Phần trăm khuyến mãi không hợp lệ.");
            n = gia.multiply(BigDecimal.valueOf(k.getPhanTramGiam())).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (LoaiGiam.SO_TIEN.name().equals(k.getLoaiGiam())) n = tien(k.getSoTienGiam());
        else throw new IllegalArgumentException("Loại khuyến mãi không hợp lệ.");
        return n.min(gia);
    }

    /** Mỗi dòng chọn một khuyến mãi tốt nhất; mã nhập tay chỉ áp dụng SP có trong bảng liên kết. */
    public static BaoGia tinhGia(List<SanPham> sanPhams, Map<String, Integer> gio, KhachHang kh,
                                String code, boolean dungDiem, LocalDateTime luc) {
        if (gio.isEmpty()) throw new IllegalArgumentException("Giỏ hàng đang trống.");
        code = code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
        List<DongGia> ds = new ArrayList<>();
        BigDecimal tong = tien(0), tongGiam = tien(0);
        boolean codeHopLe = code.isEmpty();
        Map<String, SanPham> spMap = new HashMap<>();
        for (SanPham s : sanPhams) spMap.put(s.getMaSP(), s);
        for (String ma : new TreeSet<>(gio.keySet())) {
            SanPham s = spMap.get(ma);
            Integer sl = gio.get(ma);
            if (s == null || sl == null || sl <= 0) throw new IllegalArgumentException("Sản phẩm/số lượng không hợp lệ.");
            if (!kiemTraConHang(s, sl))
                throw new IllegalArgumentException("Sản phẩm " + ma + " ngừng bán hoặc không đủ tồn kho.");
            BigDecimal gia = tien(s.getGiaNiemYet()), best = tien(0); String maKM = null;
            for (KhuyenMai k : s.getKhuyenMais()) {
                boolean hieuLuc = !luc.isBefore(k.getNgayBatDau()) && !luc.isAfter(k.getNgayKetThuc());
                if (hieuLuc && k.getMaKM().equalsIgnoreCase(code)) codeHopLe = true;
                BigDecimal g = giam(k, gia, luc);
                if (g.signum() > 0 && (g.compareTo(best) > 0 || (g.compareTo(best) == 0
                        && (maKM == null || k.getMaKM().compareTo(maKM) < 0)))) { best = g; maKM = k.getMaKM(); }
            }
            BigDecimal giamDong = best.multiply(BigDecimal.valueOf(sl));
            DongGia row = new DongGia(ma, s.getTenSP(), sl, gia, giamDong, tien(0), maKM);
            ds.add(row); tong = tong.add(row.tongHang()); tongGiam = tongGiam.add(giamDong);
        }

if (!codeHopLe) {
    throw new IllegalArgumentException(
        "Mã khuyến mãi hết hạn hoặc không áp dụng cho sản phẩm trong giỏ."
    );
}

// Kiểm tra khách vãng lai
boolean khachVangLai = kh != null
        && "0000000000".equals(kh.getSdtKH());

// Khách vãng lai không được sử dụng điểm
if (dungDiem && (kh == null || khachVangLai)) {
    throw new IllegalArgumentException(
        "Khách vãng lai không sử dụng điểm tích lũy."
    );
}

BigDecimal sauKM = tong.subtract(tongGiam);

int diem = dungDiem
        ? Math.min(
            kh.getDiemTichLuy(),
            sauKM.divideToIntegralValue(BigDecimal.valueOf(1000))
                 .min(BigDecimal.valueOf(kh.getDiemTichLuy()))
                 .intValueExact()
          )
        : 0;

BigDecimal conDiem = BigDecimal.valueOf(diem)
        .multiply(BigDecimal.valueOf(1000))
        .setScale(2);

BigDecimal trongSoCon = sauKM;

List<DongGia> chia = new ArrayList<>();

for (DongGia d : ds) {

    BigDecimal trongSo = d.thucTra();

    BigDecimal g = trongSoCon.signum() == 0
            ? tien(0)
            : conDiem.multiply(trongSo)
                .divide(trongSoCon, 2, RoundingMode.DOWN);

    chia.add(new DongGia(
        d.maSP(),
        d.tenSP(),
        d.soLuong(),
        d.donGia(),
        d.giamKM(),
        g,
        d.maKM()
    ));

    conDiem = conDiem.subtract(g);
    trongSoCon = trongSoCon.subtract(trongSo);
}

BigDecimal canTra = sauKM.subtract(
    BigDecimal.valueOf(diem).multiply(BigDecimal.valueOf(1000))
);

// Khách vãng lai không được cộng điểm
int cong = (kh == null || khachVangLai)
        ? 0
        : canTra.divideToIntegralValue(
            BigDecimal.valueOf(100000)
          ).intValueExact();

return new BaoGia(
    kh == null ? "" : kh.getMaKH(),
    kh == null || khachVangLai ? 0 : kh.getDiemTichLuy(),
    code,
    dungDiem,
    chia,
    tong,
    tongGiam,
    diem,
    cong,
    canTra
);
  }

    public BaoGia baoGia(Map<String, Integer> gio, String maKH, String code, boolean diem) throws SQLException {
        return dao.doc(c -> {
            List<KhachHang> kh = dao.khachHangs(c, maKH, false);
            if (maKH == null || kh.isEmpty()) throw new IllegalArgumentException("Cần chọn hoặc thêm hồ sơ khách hàng trước khi thanh toán.");
            List<SanPham> sp = new ArrayList<>();
            for (String ma : new TreeSet<>(gio.keySet())) sp.addAll(dao.sanPhams(c, ma, false));
            return tinhGia(sp, gio, kh.get(0), code, diem, LocalDateTime.now());
        });
    }

    public String thanhToan(String maHD, String maNV, Map<String, Integer> gio, BaoGia xacNhan,
                            PhuongThucThanhToan pt, BigDecimal tienNhan, boolean daNhanCK,
                            Map<String, String> serials) throws SQLException {
        if (xacNhan == null || pt == null || maNV == null) throw new IllegalArgumentException("Thiếu phiên đăng nhập hoặc báo giá.");

if (maHD == null || !maHD.matches("^HD[0-9]{8}$")) {
    throw new IllegalArgumentException(
        "Mã giao dịch không hợp lệ."
    );
}

        if (pt != PhuongThucThanhToan.TIEN_MAT && pt != PhuongThucThanhToan.CHUYEN_KHOAN)
            throw new IllegalArgumentException("Chỉ hỗ trợ tiền mặt hoặc chuyển khoản.");
        if (pt == PhuongThucThanhToan.CHUYEN_KHOAN && !daNhanCK) throw new IllegalArgumentException("Chưa xác nhận nhận chuyển khoản.");
        if (tienNhan == null || tienNhan.signum() < 0 || tienNhan.scale() > 2) throw new IllegalArgumentException("Tiền nhận không hợp lệ.");
        Map<String, Integer> gioLuu = Map.copyOf(gio);
        return dao.giaoDich(c -> {
            NhanVien nv = dao.nhanVien(c, maNV);
            if (dao.daLuu(c, maHD, maNV)) { dao.kiemTraDaLuu(c,maHD,xacNhan,pt); return maHD; } // Cùng mã: không ghi hóa đơn và trừ tồn lần hai.
            List<KhachHang> khs = dao.khachHangs(c, xacNhan.maKH(), true);
            if (khs.isEmpty()) throw new IllegalArgumentException("Khách hàng không còn tồn tại.");
            KhachHang kh = khs.get(0);
            List<SanPham> sp = new ArrayList<>();
            for (String ma : new TreeSet<>(gioLuu.keySet())) sp.addAll(dao.sanPhams(c, ma, true));
            LocalDateTime ngay = LocalDateTime.now();
            BaoGia gia = tinhGia(sp, gioLuu, kh, xacNhan.maKM(), xacNhan.dungDiem(), ngay);
            if (!gia.equals(xacNhan)) throw new IllegalArgumentException("Giá, khuyến mãi, điểm hoặc giỏ đã thay đổi. Hãy mở lại thanh toán để xác nhận giá mới.");
            if (tienNhan.compareTo(gia.canTra()) < 0) throw new IllegalArgumentException("Tiền nhận chưa đủ.");
            Math.addExact(kh.getDiemTichLuy() - gia.diemDung(), gia.diemCong());

DonHang dh = new DonHang();

// Phát sinh mã đơn hàng riêng, không dùng lại mã hóa đơn
dh.setMaDH(BanHangDAO.maMoi("DH"));

            dh.setNgayDat(ngay); dh.setTrangThaiDH(TrangThaiDonHang.HOAN_THANH);
            dh.setKhachHang(kh); dh.setNhanVien(nv); dh.setGhiChu("Bán tại quầy");
            HoaDon hd = new HoaDon(maHD, ngay, pt, kh, nv, dh);
            Map<String, SanPham> map = new HashMap<>(); for (SanPham s : sp) map.put(s.getMaSP(), s);
            for (DongGia d : gia.dong()) {
                SanPham s = map.get(d.maSP());
                KhuyenMai km = s.getKhuyenMais().stream().filter(k -> k.getMaKM().equals(d.maKM())).findFirst().orElse(null);
                String serial = serials.getOrDefault(d.maSP(), "").trim();
                if (serial.length() > 100) throw new IllegalArgumentException("Serial/IMEI tối đa 100 ký tự cho mỗi dòng.");
                new ChiTietDonHang(d.soLuong(), d.donGia().doubleValue(), serial, s, dh);
                new ChiTietHoaDon(d.soLuong(), d.donGia().doubleValue(), serial,
                    d.giamKM().add(d.giamDiem()).doubleValue(), s, hd, km);
            }
            dao.luu(c, hd, gia.diemDung(), gia.diemCong());
            return hd.getMaHD();
        });
    }
}
