package entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhieuDoiTra {

    private String maPhieuDoiTra;
    private LocalDateTime ngayLap;
    private TrangThaiDuyet trangThaiDuyet;

    // Quan he OOP
    private HoaDon hoaDon;
    private NhanVien nhanVien;
    private List<ChiTietPhieuDoiTra> danhSachChiTiet = new ArrayList<>();

    // Constructor khong tham so
    public PhieuDoiTra() {
    }

    // Constructor day du
    public PhieuDoiTra(String maPhieuDoiTra,
                       LocalDateTime ngayLap,
                       TrangThaiDuyet trangThaiDuyet,
                       HoaDon hoaDon,
                       NhanVien nhanVien) {

        if (hoaDon == null) {
            throw new IllegalArgumentException(
                    "Hoa don khong duoc null");
        }

        if (nhanVien == null) {
            throw new IllegalArgumentException(
                    "Nhan vien khong duoc null");
        }

        this.maPhieuDoiTra = maPhieuDoiTra;
        this.ngayLap = ngayLap;
        this.trangThaiDuyet = trangThaiDuyet;
        this.hoaDon = hoaDon;
        this.nhanVien = nhanVien;
    }

    // =========================
    // GETTER - SETTER
    // =========================

    public String getMaPhieuDoiTra() {
        return maPhieuDoiTra;
    }

    public void setMaPhieuDoiTra(String maPhieuDoiTra) {
        this.maPhieuDoiTra = maPhieuDoiTra;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public TrangThaiDuyet getTrangThaiDuyet() {
        return trangThaiDuyet;
    }

    public void setTrangThaiDuyet(TrangThaiDuyet trangThaiDuyet) {
        this.trangThaiDuyet = trangThaiDuyet;
    }

    // =========================
    // GETTER - SETTER QUAN HE
    // =========================

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        if (hoaDon == null) {
            throw new IllegalArgumentException(
                    "Hoa don khong duoc null");
        }

        this.hoaDon = hoaDon;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null) {
            throw new IllegalArgumentException(
                    "Nhan vien khong duoc null");
        }

        this.nhanVien = nhanVien;
    }

    public List<ChiTietPhieuDoiTra> getDanhSachChiTiet() {
        return Collections.unmodifiableList(danhSachChiTiet);
    }

    // =========================
    // QUAN LY CHI TIET PHIEU
    // =========================

    public void themChiTiet(ChiTietPhieuDoiTra chiTiet) {

        if (chiTiet == null) {
            throw new IllegalArgumentException(
                    "Chi tiet phieu doi tra khong duoc null");
        }

        if (chiTiet.getPhieuDoiTra() != this) {
            throw new IllegalArgumentException(
                    "Chi tiet khong thuoc phieu doi tra nay");
        }

        if (!danhSachChiTiet.contains(chiTiet)) {
            danhSachChiTiet.add(chiTiet);
        }
    }

    public void xoaChiTiet(ChiTietPhieuDoiTra chiTiet) {

        if (chiTiet == null) {
            throw new IllegalArgumentException(
                    "Chi tiet phieu doi tra khong duoc null");
        }

        if (danhSachChiTiet.remove(chiTiet)) {
            // Khong de chi tiet tiep tuc tham chieu den phieu
            // sau khi da bi xoa khoi danh sach.
            chiTiet.setPhieuDoiTra(null);
        }
    }

    // =========================
    // PHUONG THUC NGHIEP VU
    // =========================

    // Kiem tra thoi han doi tra toi da 7 ngay
    public boolean kiemTraTrongHan7Ngay(LocalDateTime ngayMua) {

        if (ngayMua == null || ngayLap == null) {
            return false;
        }

        return !ngayLap.isBefore(ngayMua)
                && !ngayLap.isAfter(ngayMua.plusDays(7));
    }

    // Tinh tong tien hoan
    public double tinhTongTienHoan() {

        double tongTienHoan = 0;

        for (ChiTietPhieuDoiTra chiTiet : danhSachChiTiet) {
            tongTienHoan += chiTiet.getThanhTienHoan();
        }

        return tongTienHoan;
    }

    // Kiem tra phieu co it nhat mot chi tiet
    public boolean coChiTiet() {
        return !danhSachChiTiet.isEmpty();
    }

    // =========================
    // TOSTRING
    // =========================

    @Override
    public String toString() {
        return "PhieuDoiTra{" +
                "maPhieuDoiTra='" + maPhieuDoiTra + '\'' +
                ", ngayLap=" + ngayLap +
                ", trangThaiDuyet=" + trangThaiDuyet +
                ", soLuongChiTiet=" + danhSachChiTiet.size() +
                '}';
    }
}