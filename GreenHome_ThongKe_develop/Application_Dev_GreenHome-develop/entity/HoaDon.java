package entity;

import java.time.*;

public class HoaDon {
    private String maHD;
    private LocalDateTime ngayLap;
    private String phuongThucThanhToan;
    private double tongTienHang;
    private double tongTienGiamGia;
    private double tongTienThanhToan;

    public HoaDon() {}

    public HoaDon(String maHD, LocalDateTime ngayLap, String phuongThucThanhToan, double tongTienHang, double tongTienGiamGia, double tongTienThanhToan) {
        this.maHD = maHD;
        this.ngayLap = ngayLap;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.tongTienHang = tongTienHang;
        this.tongTienGiamGia = tongTienGiamGia;
        this.tongTienThanhToan = tongTienThanhToan;
    }

    public String getMaHD() { return maHD; }
    public void setMaHD(String maHD) { this.maHD = maHD; }

    public LocalDateTime getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDateTime ngayLap) { this.ngayLap = ngayLap; }

    public String getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public void setPhuongThucThanhToan(String phuongThucThanhToan) { this.phuongThucThanhToan = phuongThucThanhToan; }

    public double getTongTienHang() { return tongTienHang; }
    public void setTongTienHang(double tongTienHang) { this.tongTienHang = tongTienHang; }

    public double getTongTienGiamGia() { return tongTienGiamGia; }
    public void setTongTienGiamGia(double tongTienGiamGia) { this.tongTienGiamGia = tongTienGiamGia; }

    public double getTongTienThanhToan() { return tongTienThanhToan; }
    public void setTongTienThanhToan(double tongTienThanhToan) { this.tongTienThanhToan = tongTienThanhToan; }

    public double tinhTongTienThanhToan() {
        return tongTienThanhToan = tongTienHang - tongTienGiamGia;
    }

    @Override
    public String toString() {
        return "HoaDon{" + "maHD=" + maHD + ", " + "ngayLap=" + ngayLap + ", " + "phuongThucThanhToan=" + phuongThucThanhToan + ", " + "tongTienHang=" + tongTienHang + ", " + "tongTienGiamGia=" + tongTienGiamGia + ", " + "tongTienThanhToan=" + tongTienThanhToan + "}";
    }
}