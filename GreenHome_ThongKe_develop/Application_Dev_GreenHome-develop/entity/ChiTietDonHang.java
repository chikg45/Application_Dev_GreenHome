package entity;

public class ChiTietDonHang {

    private int soLuong;
    private double donGia;
    private String serialIMEI;

    public ChiTietDonHang() {}

    public ChiTietDonHang(int soLuong, double donGia, String serialIMEI) {
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.serialIMEI = serialIMEI;
    }

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

    public double getThanhTien() {
        return soLuong * donGia;
    }

    @Override
    public String toString() {
        return "ChiTietDonHang{" +
                "soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", serialIMEI='" + serialIMEI + '\'' +
                ", thanhTien=" + getThanhTien() +
                '}';
    }
}