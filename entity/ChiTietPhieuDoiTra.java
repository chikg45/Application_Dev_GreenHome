package entity;

import enums.HinhThucXuLy;
import enums.TinhTrangKhiDoiTra;

public class ChiTietPhieuDoiTra {

    private int soLuong;
    private double donGia;
    private String serialIMEI;
    private String lyDo;
    private TinhTrangKhiDoiTra tinhTrangKhiDoiTra;
    private HinhThucXuLy hinhThucXuLy;

    // Quan hệ OOP
    private SanPham sanPham;
    private PhieuDoiTra phieuDoiTra;
    private ChiTietHoaDon chiTietHoaDon;

    // Constructor không tham số
    public ChiTietPhieuDoiTra() {
    }

    // Constructor đầy đủ
    public ChiTietPhieuDoiTra(
            int soLuong,
            double donGia,
            String serialIMEI,
            String lyDo,
            TinhTrangKhiDoiTra tinhTrangKhiDoiTra,
            HinhThucXuLy hinhThucXuLy,
            SanPham sanPham,
            PhieuDoiTra phieuDoiTra,
            ChiTietHoaDon chiTietHoaDon) {

        if (soLuong <= 0) {
            throw new IllegalArgumentException(
                    "So luong phai lon hon 0");
        }

        if (donGia < 0) {
            throw new IllegalArgumentException(
                    "Don gia khong duoc am");
        }

        if (sanPham == null || phieuDoiTra == null
                || chiTietHoaDon == null) {
            throw new IllegalArgumentException(
                    "Thong tin quan he khong duoc null");
        }

        if (chiTietHoaDon.getSanPham() != sanPham) {
            throw new IllegalArgumentException(
                    "San pham khong khop voi chi tiet hoa don");
        }

        this.soLuong = soLuong;
        this.donGia = donGia;
        this.serialIMEI = serialIMEI;
        this.lyDo = lyDo;
        this.tinhTrangKhiDoiTra = tinhTrangKhiDoiTra;
        this.hinhThucXuLy = hinhThucXuLy;

        this.sanPham = sanPham;
        this.phieuDoiTra = phieuDoiTra;
        this.chiTietHoaDon = chiTietHoaDon;
    }

    // =========================
    // GETTER - SETTER
    // =========================

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException(
                    "So luong phai lon hon 0");
        }
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        if (donGia < 0) {
            throw new IllegalArgumentException(
                    "Don gia khong duoc am");
        }
        this.donGia = donGia;
    }

    public String getSerialIMEI() {
        return serialIMEI;
    }

    public void setSerialIMEI(String serialIMEI) {
        this.serialIMEI = serialIMEI;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public TinhTrangKhiDoiTra getTinhTrangKhiDoiTra() {
        return tinhTrangKhiDoiTra;
    }

    public void setTinhTrangKhiDoiTra(
            TinhTrangKhiDoiTra tinhTrangKhiDoiTra) {
        this.tinhTrangKhiDoiTra = tinhTrangKhiDoiTra;
    }

    public HinhThucXuLy getHinhThucXuLy() {
        return hinhThucXuLy;
    }

    public void setHinhThucXuLy(HinhThucXuLy hinhThucXuLy) {
        this.hinhThucXuLy = hinhThucXuLy;
    }

    // =========================
    // GETTER - SETTER QUAN HỆ
    // =========================

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        if (sanPham == null) {
            throw new IllegalArgumentException(
                    "San pham khong duoc null");
        }

        if (chiTietHoaDon != null
                && chiTietHoaDon.getSanPham() != sanPham) {
            throw new IllegalArgumentException(
                    "San pham khong khop voi chi tiet hoa don");
        }

        this.sanPham = sanPham;
    }

    public PhieuDoiTra getPhieuDoiTra() {
        return phieuDoiTra;
    }

    public void setPhieuDoiTra(PhieuDoiTra phieuDoiTra) {
        if (phieuDoiTra == null) {
            throw new IllegalArgumentException(
                    "Phieu doi tra khong duoc null");
        }

        this.phieuDoiTra = phieuDoiTra;
    }

    public ChiTietHoaDon getChiTietHoaDon() {
        return chiTietHoaDon;
    }

    public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) {
        if (chiTietHoaDon == null) {
            throw new IllegalArgumentException(
                    "Chi tiet hoa don khong duoc null");
        }

        if (sanPham != null
                && chiTietHoaDon.getSanPham() != sanPham) {
            throw new IllegalArgumentException(
                    "Chi tiet hoa don khong khop voi san pham");
        }

        this.chiTietHoaDon = chiTietHoaDon;
    }

    // =========================
    // PHƯƠNG THỨC NGHIỆP VỤ
    // =========================

    public double getThanhTienHoan() {

        if (hinhThucXuLy == null) {
            throw new IllegalStateException(
                    "Chua chon hinh thuc xu ly");
        }

        if (hinhThucXuLy == HinhThucXuLy.HOAN_TIEN) {
            return soLuong * donGia;
        }

        return 0;
    }

    // =========================
    // TOSTRING
    // =========================

    @Override
    public String toString() {
        return "ChiTietPhieuDoiTra{" +
                "soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", serialIMEI='" + serialIMEI + '\'' +
                ", lyDo='" + lyDo + '\'' +
                ", tinhTrangKhiDoiTra=" + tinhTrangKhiDoiTra +
                ", hinhThucXuLy=" + hinhThucXuLy +
                ", sanPham=" +
                (sanPham != null ? sanPham.getMaSP() : "null") +
                ", phieuDoiTra=" +
                (phieuDoiTra != null
                        ? phieuDoiTra.getMaPhieuDoiTra()
                        : "null") +
                ", thanhTienHoan=" + getThanhTienHoan() +
                '}';
    }
}