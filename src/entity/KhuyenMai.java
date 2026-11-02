package entity;

import java.time.LocalDateTime;

public class KhuyenMai {
    private String maKM, tenKM, moTaKM, loaiGiam;
    private double phanTramGiam, soTienGiam;
    private LocalDateTime ngayBatDau, ngayKetThuc;

    public KhuyenMai(String maKM, String tenKM, double phanTramGiam,
                     LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc,
                     String moTaKM, String loaiGiam, double soTienGiam) {
        this.maKM = maKM;
        this.tenKM = tenKM;
        this.phanTramGiam = phanTramGiam;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.moTaKM = moTaKM;
        this.loaiGiam = loaiGiam;
        this.soTienGiam = soTienGiam;
    }
    public String getMaKM() { return maKM; }
    public String getTenKM() { return tenKM; }
    public double getPhanTramGiam() { return phanTramGiam; }
    public LocalDateTime getNgayBatDau() { return ngayBatDau; }
    public LocalDateTime getNgayKetThuc() { return ngayKetThuc; }
    public String getMoTaKM() { return moTaKM; }
    public String getLoaiGiam() { return loaiGiam; }
    public double getSoTienGiam() { return soTienGiam; }
    public String getTrangThai() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(ngayBatDau)) return "Sắp diễn ra";
        if (now.isAfter(ngayKetThuc)) return "Đã kết thúc";
        return "Đang diễn ra";
    }
}
