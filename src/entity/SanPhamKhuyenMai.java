package entity;

public class SanPhamKhuyenMai {

    private SanPham sanPham;
    private KhuyenMai khuyenMai;

    // Constructor không tham số
    public SanPhamKhuyenMai() {
    }

    // Constructor đầy đủ
    public SanPhamKhuyenMai(
            SanPham sanPham,
            KhuyenMai khuyenMai) {

        setSanPham(sanPham);
        setKhuyenMai(khuyenMai);
    }

    // Getter và Setter
    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {

        if (sanPham == null) {
            throw new IllegalArgumentException(
                "San pham khong duoc null"
            );
        }

        this.sanPham = sanPham;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {

        if (khuyenMai == null) {
            throw new IllegalArgumentException(
                "Khuyen mai khong duoc null"
            );
        }

        this.khuyenMai = khuyenMai;
    }

    // Lấy mã sản phẩm
    public String getMaSP() {
        return sanPham == null
                ? null
                : sanPham.getMaSP();
    }

    // Lấy mã khuyến mãi
    public String getMaKM() {
        return khuyenMai == null
                ? null
                : khuyenMai.getMaKM();
    }

    @Override
    public String toString() {
        return "SanPhamKhuyenMai{" +
                "maSP='" + getMaSP() + '\'' +
                ", maKM='" + getMaKM() + '\'' +
                '}';
    }
}