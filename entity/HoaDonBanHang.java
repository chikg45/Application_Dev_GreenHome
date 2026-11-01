package entity;

import enums.PhuongThucThanhToan;

import java.time.LocalDateTime;
import java.util.*;

public final class HoaDonBanHang {
    private final String maHD,maDH,maKH,tenKH,sdtKH,nhanVien;
    private final LocalDateTime ngayLap;
    private final PhuongThucThanhToan phuongThucThanhToan;
    private final List<DongHoaDonBanHang> chiTiet;
    private final int diemDaDung,diemDaCong,soDuDiem;
    private final long tienKhachDua;
    private final HoaDon hoaDonEntity;
    public HoaDonBanHang(String maHD,String maDH,LocalDateTime ngay,KhachHang kh,NhanVien nv,
            PhuongThucThanhToan phuongThuc,List<DongHoaDonBanHang> chiTiet,int diemDung,
            int diemCong,int soDu,long tienNhan) {
        this.maHD=Objects.requireNonNull(maHD);
        this.maDH=Objects.requireNonNull(maDH);
        ngayLap=Objects.requireNonNull(ngay);
        phuongThucThanhToan=Objects.requireNonNull(phuongThuc);
        this.chiTiet=List.copyOf(chiTiet);
        if(chiTiet.isEmpty()) throw new IllegalArgumentException("Hóa đơn phải có sản phẩm.");
        maKH=kh==null?"":kh.getMaKH(); tenKH=kh==null?"Khách vãng lai":kh.getTenKH();
        sdtKH=kh==null?"":kh.getSdtKH(); nhanVien=Objects.requireNonNull(nv).getHoTen() + " - " + nv.getMaNV();
        diemDaDung=diemDung;
        diemDaCong=diemCong; soDuDiem=soDu;
        if (diemDung < 0 || diemCong < 0 || soDu < 0) throw new IllegalArgumentException("Điểm không được âm.");
        tienKhachDua=tienNhan;
        hoaDonEntity = new HoaDon(maHD, ngay, phuongThuc.name(), tinhTongTienHang(),
            tinhTongTienGiamGia(), tinhTongTienThanhToan());
        if(tienNhan<tinhTongTienThanhToan()) throw new IllegalArgumentException("Tiền nhận chưa đủ.");
    }
    public String getMaHD() { return maHD; }
    public String getMaDH() { return maDH; }
    public LocalDateTime getNgayLap() { return ngayLap; }
    public String getMaKH() { return maKH; }
    public String getTenKH() { return tenKH; }
    public String getSdtKH() { return sdtKH; }
    public String getNhanVien() { return nhanVien; }
    public PhuongThucThanhToan getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public List<DongHoaDonBanHang> getChiTietHoaDons() { return chiTiet; }
    public int getDiemDaDung() { return diemDaDung; }
    public int getDiemDaCong() { return diemDaCong; }
    public int getSoDuDiem() { return soDuDiem; }
    public long getTienKhachDua() { return tienKhachDua; }
    public long getTienThua() { return tienKhachDua-tinhTongTienThanhToan(); }
    public int getTongSoLuong() { return chiTiet.stream().mapToInt(DongHoaDonBanHang::getSoLuong).sum(); }
    public long tinhTongTienHang() { return chiTiet.stream().mapToLong(DongHoaDonBanHang::getThanhTien).sum(); }
    public long tinhTongTienGiamGia() { return chiTiet.stream().mapToLong(DongHoaDonBanHang::getSoTienGiam).sum(); }
    public long getGiamKhuyenMai() { return chiTiet.stream().mapToLong(DongHoaDonBanHang::getGiamKhuyenMai).sum(); }
    public long getGiamDiem() { return chiTiet.stream().mapToLong(DongHoaDonBanHang::getGiamDiem).sum(); }
    public long tinhTongTienThanhToan() { return tinhTongTienHang()-tinhTongTienGiamGia(); }
    public long getGiaChuaThue() { return chiTiet.stream().mapToLong(DongHoaDonBanHang::getGiaChuaThue).sum(); }
    public long getTienVAT() { return chiTiet.stream().mapToLong(DongHoaDonBanHang::getTienVAT).sum(); }
    /** Trả bản sao của Entity gốc để caller không sửa được lịch sử trong RAM. */
    public HoaDon getHoaDonEntity() {
        return new HoaDon(hoaDonEntity.getMaHD(), hoaDonEntity.getNgayLap(),
            hoaDonEntity.getPhuongThucThanhToan(), hoaDonEntity.getTongTienHang(),
            hoaDonEntity.getTongTienGiamGia(), hoaDonEntity.getTongTienThanhToan());
    }
    @Override public String toString() { return maHD+" - "+tenKH; }
}
