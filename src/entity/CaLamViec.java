package entity;

import java.time.*;

public class CaLamViec {
    private String maCa;
    private String tenCa;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;

    public CaLamViec() {}

    public CaLamViec(String maCa, String tenCa, LocalTime gioBatDau, LocalTime gioKetThuc) {
        setMaCa(maCa);
        setTenCa(tenCa);
        setGioBatDau(gioBatDau);
        setGioKetThuc(gioKetThuc);
    }

    public String getMaCa() { return maCa; }
    public void setMaCa(String maCa) {
        if (maCa == null || !maCa.matches("CA[0-9]{3,8}"))
            throw new IllegalArgumentException("Mã ca phải có dạng CA001.");
        if (this.maCa != null && !this.maCa.equals(maCa))
            throw new IllegalArgumentException("Không được đổi mã ca.");
        this.maCa = maCa;
    }

    public String getTenCa() { return tenCa; }
    public void setTenCa(String tenCa) {
        if (tenCa == null || tenCa.trim().length() < 2 || tenCa.trim().length() > 50)
            throw new IllegalArgumentException("Tên ca phải từ 2 đến 50 ký tự.");
        this.tenCa = tenCa.trim();
    }

    public LocalTime getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(LocalTime gioBatDau) {
        if (gioBatDau == null || (gioKetThuc != null && !gioBatDau.isBefore(gioKetThuc)))
            throw new IllegalArgumentException("Giờ bắt đầu phải trước giờ kết thúc.");
        this.gioBatDau = gioBatDau;
    }

    public LocalTime getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(LocalTime gioKetThuc) {
        if (gioKetThuc == null || (gioBatDau != null && !gioKetThuc.isAfter(gioBatDau)))
            throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu.");
        this.gioKetThuc = gioKetThuc;
    }

    @Override
    public String toString() {
        return "CaLamViec{" + "maCa=" + maCa + ", " + "tenCa=" + tenCa + ", " + "gioBatDau=" + gioBatDau + ", " + "gioKetThuc=" + gioKetThuc + "}";
    }
}