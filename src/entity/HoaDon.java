package entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HoaDon {

    // ==================== THUỘC TÍNH ====================

    private String maHD;
    private LocalDateTime ngayLap;
    private PhuongThucThanhToan phuongThucThanhToan;

    // Quan hệ với KhachHang: mỗi hóa đơn có 1 khách hàng
    private KhachHang khachHang;

    // Quan hệ với NhanVien: mỗi hóa đơn có 1 nhân viên
    private NhanVien nhanVien;

    // Quan hệ với DonHang: mỗi hóa đơn có thể thuộc 0 hoặc 1 đơn hàng
    private DonHang donHang;

    // Composition: một hóa đơn có 1 hoặc nhiều chi tiết hóa đơn
    private final List<ChiTietHoaDon> danhSachChiTietHoaDon;

    // Một hóa đơn có thể có nhiều phiếu đổi trả
    private final List<PhieuDoiTra> danhSachPhieuDoiTra;


    // ==================== CONSTRUCTOR ====================

    // Constructor không tham số
    public HoaDon() {
        this.danhSachChiTietHoaDon = new ArrayList<>();
        this.danhSachPhieuDoiTra = new ArrayList<>();
    }

    // Constructor đầy đủ thông tin cơ bản
    public HoaDon(
            String maHD,
            LocalDateTime ngayLap,
            PhuongThucThanhToan phuongThucThanhToan,
            KhachHang khachHang,
            NhanVien nhanVien,
            DonHang donHang) {

        this();

        setMaHD(maHD);
        setNgayLap(ngayLap);
        setPhuongThucThanhToan(phuongThucThanhToan);
        setKhachHang(khachHang);
        setNhanVien(nhanVien);
        setDonHang(donHang);
    }


    // ==================== GETTER / SETTER ====================

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        // Bang 4.4: HD + 8 chu so; khong doi ma sau khi tao.
        if (maHD == null || !maHD.matches("HD[0-9]{8}"))
            throw new IllegalArgumentException("Mã hóa đơn phải có dạng HD00000001 (HD + 8 chữ số).");
        if (this.maHD != null && !this.maHD.equals(maHD))
            throw new IllegalArgumentException("Không được thay đổi mã hóa đơn.");
        this.maHD = maHD;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        if (ngayLap == null || ngayLap.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("Ngày lập hóa đơn không được null hoặc trong tương lai.");
        this.ngayLap = ngayLap;
    }

    public PhuongThucThanhToan getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(
            PhuongThucThanhToan phuongThucThanhToan) {

        this.phuongThucThanhToan = Objects.requireNonNull(
            phuongThucThanhToan,
            "Phương thức thanh toán không được null"
        );
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

    public DonHang getDonHang() {
        return donHang;
    }

    public void setDonHang(DonHang donHang) {
        // Quan hệ 0..1 nên được phép null
        this.donHang = donHang;
    }


    // ==================== QUAN HỆ CHI TIẾT HÓA ĐƠN ====================

    public List<ChiTietHoaDon> getDanhSachChiTietHoaDon() {
        return Collections.unmodifiableList(
            danhSachChiTietHoaDon
        );
    }

    public void themChiTietHoaDon(ChiTietHoaDon chiTiet) {

        if (chiTiet == null) {
            throw new IllegalArgumentException(
                "Chi tiết hóa đơn không được null"
            );
        }

        if (chiTiet.getHoaDon() != null && chiTiet.getHoaDon() != this) {
            throw new IllegalArgumentException("Chi tiet da thuoc " + "HoaDon khac");
        }
        if (!danhSachChiTietHoaDon.contains(chiTiet)) {
            chiTiet.ganHoaDonNoiBo(this);
            danhSachChiTietHoaDon.add(chiTiet);
        }
    }

    public void xoaChiTietHoaDon(ChiTietHoaDon chiTiet) {

        if (chiTiet == null) {
            return;
        }

        if (!danhSachChiTietHoaDon.contains(chiTiet)) {
            return;
        }

        // Theo UML, hóa đơn phải có ít nhất 1 chi tiết
        if (danhSachChiTietHoaDon.size() <= 1) {
            throw new IllegalStateException(
                "Hóa đơn phải có ít nhất một chi tiết"
            );
        }

        danhSachChiTietHoaDon.remove(chiTiet);
        chiTiet.ganHoaDonNoiBo(null);
    }


    // ==================== QUAN HỆ PHIẾU ĐỔI TRẢ ====================

    public List<PhieuDoiTra> getDanhSachPhieuDoiTra() {
        return Collections.unmodifiableList(
            danhSachPhieuDoiTra
        );
    }

    public void themPhieuDoiTra(PhieuDoiTra phieu) {

        if (phieu == null) {
            throw new IllegalArgumentException(
                "Phiếu đổi trả không được null"
            );
        }

        if (phieu.getHoaDon() != this)
            throw new IllegalArgumentException("Phiếu đổi trả không thuộc hóa đơn này.");
        if (!danhSachPhieuDoiTra.contains(phieu)) danhSachPhieuDoiTra.add(phieu);
    }

    public void xoaPhieuDoiTra(PhieuDoiTra phieu) {
        danhSachPhieuDoiTra.remove(phieu);
    }


    /** Tong gia tri hang (gia da VAT) chua tru khuyen mai. */
    public double tinhTongTienHang() {
        double tong = 0;
        for (ChiTietHoaDon ct : danhSachChiTietHoaDon) tong += ct.getSoLuong() * ct.getDonGia();
        return tong;
    }

    /** Tong soTienGiam da phan bo tren cac dong hoa don. */
    public double tinhTongTienGiamGia() {
        double tong = 0;
        for (ChiTietHoaDon ct : danhSachChiTietHoaDon) tong += ct.getSoTienGiam();
        return tong;
    }

    /** Chua tru diem tich luy: Entity hien chua co cot diem phan bo. */
    public double tinhTongTienThanhToan() {
        return Math.max(0, tinhTongTienHang() - tinhTongTienGiamGia());
    }

    // ==================== TOSTRING ====================

    @Override
    public String toString() {
        return "HoaDon{" +
                "maHD='" + maHD + '\'' +
                ", ngayLap=" + ngayLap +
                ", phuongThucThanhToan=" + phuongThucThanhToan +
                ", maKH=" + (
                    khachHang != null
                        ? khachHang.getMaKH()
                        : null
                ) +
                ", soChiTiet=" + danhSachChiTietHoaDon.size() +
                '}';
    }
}