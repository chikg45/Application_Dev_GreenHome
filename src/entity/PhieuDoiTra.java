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

        setMaPhieuDoiTra(maPhieuDoiTra);
        setNgayLap(ngayLap);
        setTrangThaiDuyet(trangThaiDuyet);
        setHoaDon(hoaDon);
        setNhanVien(nhanVien);
    }

    // =========================
    // GETTER - SETTER
    // =========================

    public String getMaPhieuDoiTra() {
        return maPhieuDoiTra;
    }

    public void setMaPhieuDoiTra(String maPhieuDoiTra) {
        // Bang 4.9: DT + 8 chu so; bat bien sau khi tao.
        if (maPhieuDoiTra == null || !maPhieuDoiTra.matches("DT[0-9]{8}"))
            throw new IllegalArgumentException("Mã phiếu đổi trả phải có dạng DT00000001 (DT + 8 chữ số).");
        if (this.maPhieuDoiTra != null && !this.maPhieuDoiTra.equals(maPhieuDoiTra))
            throw new IllegalArgumentException("Không được thay đổi mã phiếu đổi trả.");
        this.maPhieuDoiTra = maPhieuDoiTra;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        if (ngayLap == null || ngayLap.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("Ngày lập phiếu không hợp lệ.");
        this.ngayLap = ngayLap;
    }

    public TrangThaiDuyet getTrangThaiDuyet() {
        return trangThaiDuyet;
    }

    public void setTrangThaiDuyet(TrangThaiDuyet trangThaiDuyet) {
        if (trangThaiDuyet == null)
            throw new IllegalArgumentException("Trạng thái duyệt không được null.");
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
    // KIEM TRA DANH SACH CHI TIET
    // =========================

    // Kiem tra phieu co it nhat mot chi tiet
    public boolean coChiTiet() {
        return !danhSachChiTiet.isEmpty();
    }

    /** Xac dinh phieu duoc lap trong 7 ngay tinh tu ngay lap hoa don. */
    public boolean kiemTraTrongHan7Ngay() {
        if (hoaDon == null || hoaDon.getNgayLap() == null || ngayLap == null) return false;
        long giay = java.time.Duration.between(hoaDon.getNgayLap(), ngayLap).getSeconds();
        return giay >= 0 && giay <= 7L * 24 * 60 * 60;
    }

    /** Tong hoan tien cac dong co hinh thuc HOAN_TIEN. Chua tinh diem da su dung. */
    public double tinhTongTienHoan() {
        double tong = 0;
        for (ChiTietPhieuDoiTra ct : danhSachChiTiet)
            if (ct.getHinhThucXuLy() == HinhThucXuLy.HOAN_TIEN) tong += ct.getThanhTienHoan();
        return tong;
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