package entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DonHang {

    // ==================== THUỘC TÍNH ====================

    private String maDH;
    private LocalDateTime ngayDat;
    private TrangThaiDonHang trangThaiDH;
    private String ghiChu;

    // Quan hệ với KhachHang: mỗi đơn hàng có 1 khách hàng
    private KhachHang khachHang;

    // Quan hệ với NhanVien: mỗi đơn hàng có 1 nhân viên
    private NhanVien nhanVien;

    // Quan hệ với HoaDon: một đơn hàng có tối đa 1 hóa đơn
    private HoaDon hoaDon;

    // Composition: một đơn hàng có 1 hoặc nhiều chi tiết
    private final List<ChiTietDonHang> danhSachChiTietDonHang;


    // ==================== CONSTRUCTOR ====================

    // Constructor không tham số
    public DonHang() {
        this.danhSachChiTietDonHang = new ArrayList<>();
    }

    // Constructor có đầy đủ thông tin cơ bản
    public DonHang(
            String maDH,
            LocalDateTime ngayDat,
            TrangThaiDonHang trangThaiDH,
            String ghiChu,
            KhachHang khachHang,
            NhanVien nhanVien) {

        this();

        setMaDH(maDH);
        setNgayDat(ngayDat);
        setTrangThaiDH(trangThaiDH);
        setGhiChu(ghiChu);
        setKhachHang(khachHang);
        setNhanVien(nhanVien);
    }


    // ==================== GETTER / SETTER ====================

    public String getMaDH() {
        return maDH;
    }

    public void setMaDH(String maDH) {
        // Bang 4.6: DH + 8 chu so; bat bien sau khi tao.
        if (maDH == null || !maDH.matches("DH[0-9]{8}"))
            throw new IllegalArgumentException("Mã đơn hàng phải có dạng DH00000001 (DH + 8 chữ số).");
        if (this.maDH != null && !this.maDH.equals(maDH))
            throw new IllegalArgumentException("Không được thay đổi mã đơn hàng.");
        this.maDH = maDH;
    }

    public LocalDateTime getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(LocalDateTime ngayDat) {
        if (ngayDat == null || ngayDat.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("Ngày đặt không được null hoặc trong tương lai.");
        this.ngayDat = ngayDat;
    }

    public TrangThaiDonHang getTrangThaiDH() {
        return trangThaiDH;
    }

    public void setTrangThaiDH(TrangThaiDonHang trangThaiDH) {
        this.trangThaiDH = Objects.requireNonNull(
            trangThaiDH, "Trạng thái đơn hàng không được null"
        );
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = Objects.requireNonNull(
            khachHang, "Khách hàng không được null"
        );
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = Objects.requireNonNull(
            nhanVien, "Nhân viên không được null"
        );
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        // Quan hệ 0..1: được phép null
        this.hoaDon = hoaDon;
    }


    // ==================== QUAN HỆ CHI TIẾT ĐƠN HÀNG ====================

    public List<ChiTietDonHang> getDanhSachChiTietDonHang() {
        return Collections.unmodifiableList(
            danhSachChiTietDonHang
        );
    }

    /**
     * Thêm một chi tiết đơn hàng.
     */
    public void themChiTietDonHang(ChiTietDonHang chiTiet) {

        if (chiTiet == null) {
            throw new IllegalArgumentException(
                "Chi tiết đơn hàng không được null"
            );
        }

        if (chiTiet.getDonHang() != null && chiTiet.getDonHang() != this) {
            throw new IllegalArgumentException("Chi tiet da thuoc " + "DonHang khac");
        }
        if (!danhSachChiTietDonHang.contains(chiTiet)) {
            chiTiet.ganDonHangNoiBo(this);
            danhSachChiTietDonHang.add(chiTiet);
        }
    }

    /**
     * Xóa một chi tiết đơn hàng.
     */
    public void xoaChiTietDonHang(ChiTietDonHang chiTiet) {

        if (chiTiet == null) {
            return;
        }

        if (!danhSachChiTietDonHang.contains(chiTiet)) {
            return;
        }

        if (danhSachChiTietDonHang.size() <= 1) {
            throw new IllegalStateException(
                "Đơn hàng phải có ít nhất một chi tiết"
            );
        }

        danhSachChiTietDonHang.remove(chiTiet);
        chiTiet.ganDonHangNoiBo(null);
    }



    /** Tong tien hang truoc giam gia, cong cac chi tiet don hang. */
    public double tinhTongTienDonHang() {
        double tong = 0;
        for (ChiTietDonHang ct : danhSachChiTietDonHang) tong += ct.getThanhTienDonHang();
        return tong;
    }

    // ==================== TOSTRING ====================

    @Override
    public String toString() {
        return "DonHang{" +
                "maDH='" + maDH + '\'' +
                ", ngayDat=" + ngayDat +
                ", trangThaiDH=" + trangThaiDH +
                ", ghiChu='" + ghiChu + '\'' +
                ", maKH=" + (
                    khachHang != null
                        ? khachHang.getMaKH()
                        : null
                ) +
                ", soChiTiet=" + danhSachChiTietDonHang.size() +
                '}';
    }
}