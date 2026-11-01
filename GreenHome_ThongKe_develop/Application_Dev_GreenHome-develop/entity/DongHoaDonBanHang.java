package entity;

import java.math.*;
import java.util.*;

/** Snapshot bất biến: đổi tên/giá sản phẩm sau này không làm đổi hóa đơn cũ. */
public final class DongHoaDonBanHang {
    private final String maSP,tenSP,serialIMEI,maKM;
    private final int soLuong;
    private final long donGia,soTienGiam,giamKhuyenMai,giamDiem;
    public DongHoaDonBanHang(String maSP,String tenSP,int sl,double gia,String serial,
            long giamKM,long giamDiem,String maKM) {
        this.maSP=Objects.requireNonNull(maSP);
        this.tenSP=Objects.requireNonNull(tenSP);
        if(sl<=0) throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        soLuong=sl; donGia=TienVND.tuDouble(gia);
        if(donGia<=0||giamKM<0||giamDiem<0) throw new IllegalArgumentException("Tiền dòng không hợp lệ.");
        serialIMEI=serial==null?"":serial;
        this.maKM=maKM==null?"":maKM;
        giamKhuyenMai=giamKM; this.giamDiem=giamDiem; soTienGiam=Math.addExact(giamKM,giamDiem);
        if(soTienGiam>getThanhTien()) throw new IllegalArgumentException("Giảm giá vượt tiền dòng.");
    }
    public String getMaSP() { return maSP; }
    public String getTenSP() { return tenSP; }
    public String getSerialIMEI() { return serialIMEI; }
    public String getMaKM() { return maKM; }
    public int getSoLuong() { return soLuong; }
    public long getDonGia() { return donGia; }
    public long getSoTienGiam() { return soTienGiam; }
    public long getGiamKhuyenMai() { return giamKhuyenMai; }
    public long getGiamDiem() { return giamDiem; }
    public long getThanhTien() { return Math.multiplyExact(donGia,soLuong); }
    public long getThucTra() { return getThanhTien()-soTienGiam; }
    public long getGiaChuaThue() {
        return BigDecimal.valueOf(getThanhTien()).divide(new BigDecimal("1.1"),0,RoundingMode.HALF_UP).longValueExact();
    }
    public long getTienVAT() { return getThanhTien()-getGiaChuaThue(); }
    /** Chuyển snapshot sang đúng lớp ChiTietHoaDon nhóm đã viết. */
    public ChiTietHoaDon getChiTietEntity() {
        ChiTietHoaDon ct = new ChiTietHoaDon(soLuong, donGia, serialIMEI, soTienGiam);
        SanPham sp = new SanPham(); sp.setMaSP(maSP); sp.setTenSP(tenSP); sp.setGiaNiemYet(donGia);
        ct.setSanPham(sp);
        return ct;
    }
    @Override public String toString() { return tenSP+" x "+soLuong; }
}
