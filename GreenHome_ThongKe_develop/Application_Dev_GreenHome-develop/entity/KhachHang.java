package entity;

import java.time.*;

public class KhachHang {
    private String maKH;
    private String tenKH;
    private String sdtKH;
    private String diaChi;
    private int diemTichLuy;

    public KhachHang() {}

    public KhachHang(String maKH, String tenKH, String sdtKH, String diaChi, int diemTichLuy) {
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.sdtKH = sdtKH;
        this.diaChi = diaChi;
        this.diemTichLuy = diemTichLuy;
    }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public String getTenKH() { return tenKH; }
    public void setTenKH(String tenKH) { this.tenKH = tenKH; }

    public String getSdtKH() { return sdtKH; }
    public void setSdtKH(String sdtKH) { this.sdtKH = sdtKH; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public int getDiemTichLuy() { return diemTichLuy; }
    public void setDiemTichLuy(int diemTichLuy) { this.diemTichLuy = diemTichLuy; }

    public void congDiem(int diem) {
        if (diem < 0) throw new IllegalArgumentException("Diem phai khong am");
        diemTichLuy += diem;
    }
    public void truDiem(int diem) {
        if (diem < 0 || diem > diemTichLuy) throw new IllegalArgumentException("Diem khong hop le");
        diemTichLuy -= diem;
    }

    @Override
    public String toString() {
        return "KhachHang{" + "maKH=" + maKH + ", " + "tenKH=" + tenKH + ", " + "sdtKH=" + sdtKH + ", " + "diaChi=" + diaChi + ", " + "diemTichLuy=" + diemTichLuy + "}";
    }
}