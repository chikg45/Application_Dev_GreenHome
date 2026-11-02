package entity;

import java.util.Objects;

public class ChiTietDonHang {

    // ==================== THUỘC TÍNH ====================

    private int soLuong;
    private double donGia;
    private String serialIMEI;

    // Quan hệ với SanPham (1)
    private SanPham sanPham;

    // Quan hệ Composition với DonHang (1)
    private DonHang donHang;


    // ==================== CONSTRUCTOR ====================

    // Constructor không tham số
    public ChiTietDonHang() {
    }

    // Constructor đầy đủ
    public ChiTietDonHang(
            int soLuong,
            double donGia,
            String serialIMEI,
            SanPham sanPham,
            DonHang donHang) {

        setSoLuong(soLuong);
        setDonGia(donGia);
        setSerialIMEI(serialIMEI);
        setSanPham(sanPham);
        setDonHang(donHang);
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

        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {

        if (!Double.isFinite(donGia) || donGia < 0) {
            throw new IllegalArgumentException(
                "Đơn giá phải là số không âm và hữu hạn"
            );
        }

        this.donGia = donGia;
    }

    public String getSerialIMEI() {
        return serialIMEI;
    }

    public void setSerialIMEI(String serialIMEI) {
        this.serialIMEI = serialIMEI;
    }


    // ==================== QUAN HỆ VỚI SANPHAM ====================

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {

        this.sanPham = Objects.requireNonNull(
            sanPham,
            "Sản phẩm không được null"
        );
    }


    // ==================== QUAN HỆ VỚI DONHANG ====================

    public DonHang getDonHang() {
        return donHang;
    }

    public void setDonHang(DonHang donHang) {
        Objects.requireNonNull(donHang, "DonHang khong duoc null");
        if (this.donHang != null && this.donHang != donHang) {
            throw new IllegalStateException("Chi tiet da thuoc DonHang khac");
        }
        this.donHang = donHang;
        if (!donHang.getDanhSachChiTietDonHang().contains(this)) {
            donHang.themChiTietDonHang(this);
        }
    }

    // Chi duoc goi tu DonHang khi xoa chi tiet khoi danh sach
    void ganDonHangNoiBo(DonHang donHang) {
        this.donHang = donHang;
    }


    // ==================== PHƯƠNG THỨC NGHIỆP VỤ ====================

    /**
     * Cập nhật số lượng sản phẩm đặt hàng.
     *
     * @param slMoi Số lượng mới
     */
    public void capNhatSoLuong(int slMoi) {

        if (slMoi <= 0) {
            throw new IllegalArgumentException(
                "Số lượng mới phải lớn hơn 0"
            );
        }

        this.soLuong = slMoi;
    }

    /**
     * Tính thành tiền của chi tiết đơn hàng.
     *
     * Công thức:
     * Thành tiền = Số lượng * Đơn giá
     */
    public double getThanhTienDonHang() {

        return soLuong * donGia;
    }


    // ==================== TOSTRING ====================

    @Override
    public String toString() {
        return "ChiTietDonHang{" +
                "maDH='" + (
                    donHang != null
                        ? donHang.getMaDH()
                        : null
                ) + '\'' +
                ", maSP='" + (
                    sanPham != null
                        ? sanPham.getMaSP()
                        : null
                ) + '\'' +
                ", soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", serialIMEI='" + serialIMEI + '\'' +
                ", thanhTienDonHang=" + getThanhTienDonHang() +
                '}';
    }
}