package entity;

import java.time.LocalDateTime;

public class KhuyenMai {

    private String maKM;
    private String tenKM;
    private double phanTramGiam;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private String moTaKM;
    private LoaiGiam loaiGiam;
    private double soTienGiam;

    // Constructor không tham số
    public KhuyenMai() {
    }

    // Constructor đầy đủ
    public KhuyenMai(
            String maKM,
            String tenKM,
            double phanTramGiam,
            LocalDateTime ngayBatDau,
            LocalDateTime ngayKetThuc,
            String moTaKM,
            LoaiGiam loaiGiam,
            double soTienGiam) {

        this.maKM = maKM;
        this.tenKM = tenKM;
        setPhanTramGiam(phanTramGiam);
        setNgayBatDau(ngayBatDau);
        setNgayKetThuc(ngayKetThuc);
        this.moTaKM = moTaKM;
        this.loaiGiam = loaiGiam;
        setSoTienGiam(soTienGiam);
    }

    // Getter và Setter
    public String getMaKM() {
        return maKM;
    }

    public void setMaKM(String maKM) {
        this.maKM = maKM;
    }

    public String getTenKM() {
        return tenKM;
    }

    public void setTenKM(String tenKM) {
        this.tenKM = tenKM;
    }

    public double getPhanTramGiam() {
        return phanTramGiam;
    }

    public void setPhanTramGiam(double phanTramGiam) {
        if (phanTramGiam < 0 || phanTramGiam > 100) {
            throw new IllegalArgumentException(
                "Phan tram giam phai tu 0 den 100"
            );
        }

        this.phanTramGiam = phanTramGiam;
    }

    public LocalDateTime getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDateTime ngayBatDau) {
        if (ngayBatDau != null
                && ngayKetThuc != null
                && ngayBatDau.isAfter(ngayKetThuc)) {
            throw new IllegalArgumentException(
                "Ngay bat dau khong duoc sau ngay ket thuc"
            );
        }

        this.ngayBatDau = ngayBatDau;
    }

    public LocalDateTime getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDateTime ngayKetThuc) {
        if (ngayBatDau != null
                && ngayKetThuc != null
                && ngayKetThuc.isBefore(ngayBatDau)) {
            throw new IllegalArgumentException(
                "Ngay ket thuc khong duoc truoc ngay bat dau"
            );
        }

        this.ngayKetThuc = ngayKetThuc;
    }

    public String getMoTaKM() {
        return moTaKM;
    }

    public void setMoTaKM(String moTaKM) {
        this.moTaKM = moTaKM;
    }

    public LoaiGiam getLoaiGiam() {
        return loaiGiam;
    }

    public void setLoaiGiam(LoaiGiam loaiGiam) {
        this.loaiGiam = loaiGiam;
    }

    public double getSoTienGiam() {
        return soTienGiam;
    }

    public void setSoTienGiam(double soTienGiam) {
        if (soTienGiam < 0) {
            throw new IllegalArgumentException(
                "So tien giam khong duoc am"
            );
        }

        this.soTienGiam = soTienGiam;
    }

    // Kiểm tra khuyến mãi còn hiệu lực
    public boolean dieuKienKM() {

        if (ngayBatDau == null || ngayKetThuc == null) {
            return false;
        }

        LocalDateTime hienTai = LocalDateTime.now();

        return !hienTai.isBefore(ngayBatDau)
                && !hienTai.isAfter(ngayKetThuc);
    }

    // Tính số tiền giảm
    public double tinhTienGiam(double tongTienHang) {

        if (tongTienHang <= 0 || !dieuKienKM()) {
            return 0;
        }

        if (loaiGiam == null) {
            return 0;
        }

        double tienGiam;

        switch (loaiGiam) {

            case PHAN_TRAM:
                tienGiam = tongTienHang * phanTramGiam / 100;
                break;

            case SO_TIEN:
                tienGiam = soTienGiam;
                break;

            default:
                tienGiam = 0;
        }

        // Không cho phép giảm vượt quá tiền hàng
        return Math.min(tienGiam, tongTienHang);
    }

    @Override
    public String toString() {
        return "KhuyenMai{" +
                "maKM='" + maKM + '\'' +
                ", tenKM='" + tenKM + '\'' +
                ", phanTramGiam=" + phanTramGiam +
                ", ngayBatDau=" + ngayBatDau +
                ", ngayKetThuc=" + ngayKetThuc +
                ", moTaKM='" + moTaKM + '\'' +
                ", loaiGiam=" + loaiGiam +
                ", soTienGiam=" + soTienGiam +
                '}';
    }
}