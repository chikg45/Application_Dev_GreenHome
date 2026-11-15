package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ChiTietHoaDon {

    // ==================== THUỘC TÍNH ====================

    private int soLuong;
    private double donGia;
    private String serialIMEI;
    private double soTienGiam;
    // Snapshot ten san pham khi lap chi tiet, khong doi theo ten SP hien tai.
    private String tenSP;

    // Quan hệ với SanPham: mỗi chi tiết có đúng 1 sản phẩm
    private SanPham sanPham;

    // Composition: mỗi chi tiết thuộc đúng 1 hóa đơn
    private HoaDon hoaDon;

    // Quan hệ 0..1: mỗi chi tiết có tối đa 1 khuyến mãi
    private KhuyenMai khuyenMai;

    // Quan hệ 0..* với ChiTietPhieuDoiTra
    private final List<ChiTietPhieuDoiTra> danhSachChiTietPhieuDoiTra;


    // ==================== CONSTRUCTOR ====================

    public ChiTietHoaDon() {
        this.danhSachChiTietPhieuDoiTra = new ArrayList<>();
    }

    public ChiTietHoaDon(
            int soLuong,
            double donGia,
            String serialIMEI,
            double soTienGiam,
            SanPham sanPham,
            HoaDon hoaDon,
            KhuyenMai khuyenMai) {

        this();

        setSoLuong(soLuong);
        setDonGia(donGia);
        setSerialIMEI(serialIMEI);
        setSoTienGiam(soTienGiam);
        setSanPham(sanPham);
        setHoaDon(hoaDon);
        setKhuyenMai(khuyenMai);
    }


    // ==================== GETTER / SETTER ====================

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException(
                "Số lượng phải lớn hơn 0"
            );
        }

        if (soTienGiam > soLuong * donGia && donGia > 0)
            throw new IllegalArgumentException("Tiền giảm không được vượt thành tiền dòng.");
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        if (!Double.isFinite(donGia) || donGia <= 0) {
            throw new IllegalArgumentException(
                "Đơn giá phải lớn hơn 0 và hữu hạn"
            );
        }

        if (soLuong > 0 && soTienGiam > soLuong * donGia)
            throw new IllegalArgumentException("Tiền giảm không được vượt thành tiền dòng.");
        this.donGia = donGia;
    }

    public String getSerialIMEI() {
        return serialIMEI;
    }

    public void setSerialIMEI(String serialIMEI) {
        this.serialIMEI = serialIMEI;
    }

    public double getSoTienGiam() {
        return soTienGiam;
    }

    public void setSoTienGiam(double soTienGiam) {
        if (!Double.isFinite(soTienGiam) || soTienGiam < 0) {
            throw new IllegalArgumentException(
                "Số tiền giảm phải là số không âm và hữu hạn"
            );
        }

        if (soTienGiam > 0 && (soLuong <= 0 || donGia <= 0
                || soTienGiam > soLuong * donGia))
            throw new IllegalArgumentException("Tiền giảm chỉ được nhập sau số lượng/đơn giá hợp lệ và không vượt thành tiền dòng.");
        this.soTienGiam = soTienGiam;
    }


    // ==================== QUAN HỆ VỚI SANPHAM ====================

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = Objects.requireNonNull(sanPham, "Sản phẩm không được null");
        if (this.tenSP == null) this.tenSP = sanPham.getTenSP();
    }


    public String getTenSP() { return tenSP; }
    public void setTenSP(String tenSP) {
        if (tenSP == null || tenSP.isBlank())
            throw new IllegalArgumentException("Tên SP tại thời điểm bán không được trống.");
        this.tenSP = tenSP.trim();
    }

    /** Thanh tien dong da tru giam khuyen mai (chua tru diem tich luy). */
    public double getThanhTien() { return soLuong * donGia - soTienGiam; }

    // ==================== QUAN HỆ VỚI HOADON ====================

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        Objects.requireNonNull(hoaDon, "HoaDon khong duoc null");
        if (this.hoaDon != null && this.hoaDon != hoaDon) {
            throw new IllegalStateException("Chi tiet da thuoc HoaDon khac");
        }
        this.hoaDon = hoaDon;
        if (!hoaDon.getDanhSachChiTietHoaDon().contains(this)) {
            hoaDon.themChiTietHoaDon(this);
        }
    }

    // Chi duoc goi tu HoaDon khi xoa chi tiet khoi danh sach
    void ganHoaDonNoiBo(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }


    // ==================== QUAN HỆ VỚI KHUYENMAI ====================

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        // Quan hệ 0..1: được phép null
        this.khuyenMai = khuyenMai;
    }


    // ==================== QUAN HỆ VỚI CHITIETPHIEUDOITRA ====================

    public List<ChiTietPhieuDoiTra> getDanhSachChiTietPhieuDoiTra() {
        return Collections.unmodifiableList(
            danhSachChiTietPhieuDoiTra
        );
    }

    public void themChiTietPhieuDoiTra(
            ChiTietPhieuDoiTra chiTiet) {

        if (chiTiet == null) {
            throw new IllegalArgumentException(
                "Chi tiết phiếu đổi trả không được null"
            );
        }

        if (chiTiet.getChiTietHoaDon() != this)
            throw new IllegalArgumentException("Chi tiết đổi trả không thuộc dòng hóa đơn này.");
        if (!danhSachChiTietPhieuDoiTra.contains(chiTiet))
            danhSachChiTietPhieuDoiTra.add(chiTiet);
    }

    public void xoaChiTietPhieuDoiTra(
            ChiTietPhieuDoiTra chiTiet) {

        danhSachChiTietPhieuDoiTra.remove(chiTiet);
    }


    // ==================== TOSTRING ====================

    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                "maHD='" + (
                    hoaDon != null ? hoaDon.getMaHD() : null
                ) + '\'' +
                ", maSP='" + (
                    sanPham != null ? sanPham.getMaSP() : null
                ) + '\'' +
                ", soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", serialIMEI='" + serialIMEI + '\'' +
                ", soTienGiam=" + soTienGiam +
                ", maKM='" + (
                    khuyenMai != null
                        ? khuyenMai.getMaKM()
                        : null
                ) + '\'' +
                '}';
    }
}