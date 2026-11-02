package entity;

import java.time.LocalDateTime;

public class KhuyenMai {
    private String maKM, tenKM, moTaKM, loaiGiam;
    private double phanTramGiam, soTienGiam;
    private LocalDateTime ngayBatDau, ngayKetThuc;

    public KhuyenMai(String maKM, String tenKM, double phanTramGiam,
                     LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc,
                     String moTaKM, String loaiGiam, double soTienGiam) {
        // Bang 4.8: KM + 8 chu so.
        if (maKM == null || !maKM.matches("KM[0-9]{8}"))
            throw new IllegalArgumentException("Mã KM cũ có 8 chữ số; mã mới được sinh đủ 8 chữ số.");
        if (tenKM == null || tenKM.isBlank() || tenKM.length() > 150)
            throw new IllegalArgumentException("Tên khuyến mãi phải có 1-150 ký tự.");
        if (moTaKM != null && moTaKM.length() > 500)
            throw new IllegalArgumentException("Mô tả khuyến mãi tối đa 500 ký tự.");
        if (ngayBatDau == null || ngayKetThuc == null || ngayKetThuc.isBefore(ngayBatDau))
            throw new IllegalArgumentException("Khoảng ngày khuyến mãi không hợp lệ.");
        if (!"PHAN_TRAM".equals(loaiGiam) && !"SO_TIEN".equals(loaiGiam))
            throw new IllegalArgumentException("Loại giảm phải là PHAN_TRAM hoặc SO_TIEN.");
        if (!Double.isFinite(phanTramGiam) || !Double.isFinite(soTienGiam)
                || phanTramGiam < 0 || phanTramGiam > 100 || soTienGiam < 0)
            throw new IllegalArgumentException("Mức giảm không hợp lệ.");
        if (("PHAN_TRAM".equals(loaiGiam) && (phanTramGiam <= 0 || soTienGiam != 0))
                || ("SO_TIEN".equals(loaiGiam) && (soTienGiam <= 0 || phanTramGiam != 0)))
            throw new IllegalArgumentException("Chỉ nhập mức giảm dương theo loại giảm đã chọn.");
        this.maKM = maKM.trim();
        this.tenKM = tenKM.trim();
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

    /** Giu String API cho DAO/UI, bo sung enum de doi chieu dac ta UML. */
    public LoaiGiam getLoaiGiamEnum() { return LoaiGiam.valueOf(loaiGiam); }

    /** Khuyen mai co hieu luc tai thoi diem hien tai. */
    public boolean dieuKienKM() {
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(ngayBatDau) && !now.isAfter(ngayKetThuc);
    }

    /** Muc giam cho tong tien, khong vuot so tien hang. Quyen ap dung o BUS. */
    public double tinhTienGiam(double tongTienHang) {
        if (!Double.isFinite(tongTienHang) || tongTienHang < 0)
            throw new IllegalArgumentException("Tổng tiền hàng phải là số không âm hữu hạn.");
        double mucGiam = "PHAN_TRAM".equals(loaiGiam)
                ? tongTienHang * phanTramGiam / 100 : soTienGiam;
        return Math.min(tongTienHang, mucGiam);
    }
}
