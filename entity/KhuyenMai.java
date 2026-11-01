package entity;

import java.time.*;

public class KhuyenMai {
    private String maKM;
    private String tenKM;
    private double phanTramGiam;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private String moTaKM;
    private String loaiGiam;

    public KhuyenMai() {}

    public KhuyenMai(String maKM, String tenKM, double phanTramGiam, LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc, String moTaKM, String loaiGiam) {
        this.maKM = maKM;
        this.tenKM = tenKM;
        this.phanTramGiam = phanTramGiam;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.moTaKM = moTaKM;
        this.loaiGiam = loaiGiam;
    }

    public String getMaKM() { return maKM; }
    public void setMaKM(String maKM) { this.maKM = maKM; }

    public String getTenKM() { return tenKM; }
    public void setTenKM(String tenKM) { this.tenKM = tenKM; }

    public double getPhanTramGiam() { return phanTramGiam; }
    public void setPhanTramGiam(double phanTramGiam) { this.phanTramGiam = phanTramGiam; }

    public LocalDateTime getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(LocalDateTime ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    public LocalDateTime getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(LocalDateTime ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }

    public String getMoTaKM() { return moTaKM; }
    public void setMoTaKM(String moTaKM) { this.moTaKM = moTaKM; }

    public String getLoaiGiam() { return loaiGiam; }
    public void setLoaiGiam(String loaiGiam) { this.loaiGiam = loaiGiam; }

    @Override
    public String toString() {
        return "KhuyenMai{" + "maKM=" + maKM + ", " + "tenKM=" + tenKM + ", " + "phanTramGiam=" + phanTramGiam + ", " + "ngayBatDau=" + ngayBatDau + ", " + "ngayKetThuc=" + ngayKetThuc + ", " + "moTaKM=" + moTaKM + ", " + "loaiGiam=" + loaiGiam + "}";
    }
}