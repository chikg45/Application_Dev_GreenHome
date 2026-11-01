package entity;

import java.time.*;

public class DonHang {
    private String maDH;
    private LocalDateTime ngayDat;
    private String trangThaiDH;
    private String ghiChu;
    private double tongTienDonHang;
    

    public DonHang() {}

    public DonHang(String maDH, LocalDateTime ngayDat, String trangThaiDH,  double tongTienDonHang) {
        this.maDH = maDH;
        this.ngayDat = ngayDat;
        this.trangThaiDH = trangThaiDH;
 
        this.tongTienDonHang = tongTienDonHang;
    }

    public String getMaDH() { return maDH; }
    public void setMaDH(String maDH) { this.maDH = maDH; }

    public LocalDateTime getNgayDat() { return ngayDat; }
    public void setNgayDat(LocalDateTime ngayDat) { this.ngayDat = ngayDat; }

    public String getTrangThaiDH() { return trangThaiDH; }
    public void setTrangThaiDH(String trangThaiDH) { this.trangThaiDH = trangThaiDH; }


    public double getTongTienDonHang() { return tongTienDonHang; }
    public void setTongTienDonHang(double tongTienDonHang) { this.tongTienDonHang = tongTienDonHang; }

    public double tinhTongTienDonHang(double tongTienHang) {
        return tongTienDonHang = tongTienHang ;
    }

    @Override
    public String toString() {
        return "DonHang{" + "maDH=" + maDH + ", " + "ngayDat=" + ngayDat + ", " + "trangThaiDH=" + trangThaiDH + ", " + "tongTienDonHang=" + tongTienDonHang + "}";
    }
}