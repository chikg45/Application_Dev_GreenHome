package entity;

public class ChiTietHoaDon {

    private SanPham sanPham;
    private int soLuong;
    private double donGia;
    private String serialIMEI;
    private double soTienGiam;

    public ChiTietHoaDon() {}

    public ChiTietHoaDon(int soLuong, double donGia,
                        String serialIMEI, double soTienGiam) {
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.serialIMEI = serialIMEI;
        this.soTienGiam = soTienGiam;
    }

    // Quan hệ đã được ChiTietPhieuDoiTra sử dụng trong source nhóm.
    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) { this.sanPham = sanPham; }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
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
        this.soTienGiam = soTienGiam;
    }

    public double getThanhTien() {
        return soLuong * donGia - soTienGiam;
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                "soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", serialIMEI='" + serialIMEI + '\'' +
                ", soTienGiam=" + soTienGiam +
                ", thanhTien=" + getThanhTien() +
                '}';
    }
}