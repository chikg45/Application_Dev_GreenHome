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
        if (maDH == null || maDH.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Mã đơn hàng không được rỗng"
            );
        }

        this.maDH = maDH.trim();
    }

    public LocalDateTime getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(LocalDateTime ngayDat) {
        this.ngayDat = Objects.requireNonNull(
            ngayDat, "Ngày đặt không được null"
        );
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


    // ==================== PHƯƠNG THỨC NGHIỆP VỤ ====================

    /**
     * Tính tổng tiền đơn hàng từ các chi tiết đơn hàng.
     *
     * Tổng tiền = Tổng thành tiền của từng chi tiết.
     */
    public double tinhTongTienDonHang() {

        double tongTien = 0;

        for (ChiTietDonHang chiTiet : danhSachChiTietDonHang) {
            tongTien += chiTiet.getThanhTienDonHang();
        }

        return tongTien;
    }

    /**
     * Kiểm tra đơn hàng có đủ thông tin để xác nhận.
     *
     * Đây là phương thức bổ sung để đảm bảo bội số 1..*
     * giữa DonHang và ChiTietDonHang.
     */
    public boolean coTheXacNhan() {
        return maDH != null
                && ngayDat != null
                && trangThaiDH != null
                && khachHang != null
                && nhanVien != null
                && !danhSachChiTietDonHang.isEmpty();
    }


    // ==================== THUỘC TÍNH DẪN XUẤT ====================

    public double getTongTienDonHang() {
        return tinhTongTienDonHang();
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
                ", tongTienDonHang=" + tinhTongTienDonHang() +
                '}';
    }
}