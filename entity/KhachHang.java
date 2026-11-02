package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KhachHang {

    // ==================== THUỘC TÍNH ====================

    private String maKH;
    private String tenKH;
    private String sdtKH;
    private String diaChi;
    private int diemTichLuy;

    // Quan hệ 1 - 0..* với HoaDon
    private List<HoaDon> danhSachHoaDon;

    // Quan hệ 1 - 0..* với DonHang
    private List<DonHang> danhSachDonHang;


    // ==================== CONSTRUCTOR ====================

    // Constructor không tham số
    public KhachHang() {
        this.danhSachHoaDon = new ArrayList<>();
        this.danhSachDonHang = new ArrayList<>();
    }

    // Constructor đầy đủ thuộc tính theo UML
    public KhachHang(String maKH, String tenKH,
                     String sdtKH, String diaChi,
                     int diemTichLuy) {

        this();

        setMaKH(maKH);
        setTenKH(tenKH);
        setSdtKH(sdtKH);
        setDiaChi(diaChi);
        setDiemTichLuy(diemTichLuy);
    }


    // ==================== GETTER / SETTER ====================

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Ma khach hang khong duoc rong"
            );
        }

        this.maKH = maKH.trim();
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        if (tenKH == null || tenKH.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Ten khach hang khong duoc rong"
            );
        }

        this.tenKH = tenKH.trim();
    }

    public String getSdtKH() {
        return sdtKH;
    }

    public void setSdtKH(String sdtKH) {
        if (sdtKH == null ||
                !sdtKH.matches("0\\d{9}")) {
            throw new IllegalArgumentException(
                "So dien thoai phai gom 10 chu so va bat dau bang 0"
            );
        }

        this.sdtKH = sdtKH;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public int getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(int diemTichLuy) {
        if (diemTichLuy < 0) {
            throw new IllegalArgumentException(
                "Diem tich luy khong duoc am"
            );
        }

        this.diemTichLuy = diemTichLuy;
    }


    // ==================== QUAN HỆ ====================

    // Lấy danh sách hóa đơn
    public List<HoaDon> getDanhSachHoaDon() {
        return Collections.unmodifiableList(danhSachHoaDon);
    }

    // Lấy danh sách đơn hàng
    public List<DonHang> getDanhSachDonHang() {
        return Collections.unmodifiableList(danhSachDonHang);
    }

    // Thêm hóa đơn
    public void themHoaDon(HoaDon hoaDon) {
        if (hoaDon == null) {
            throw new IllegalArgumentException(
                "Hoa don khong duoc null"
            );
        }

        if (!danhSachHoaDon.contains(hoaDon)) {
            danhSachHoaDon.add(hoaDon);
        }
    }

    // Xóa hóa đơn khỏi danh sách
    public void xoaHoaDon(HoaDon hoaDon) {
        danhSachHoaDon.remove(hoaDon);
    }

    // Thêm đơn hàng
    public void themDonHang(DonHang donHang) {
        if (donHang == null) {
            throw new IllegalArgumentException(
                "Don hang khong duoc null"
            );
        }

        if (!danhSachDonHang.contains(donHang)) {
            danhSachDonHang.add(donHang);
        }
    }

    // Xóa đơn hàng khỏi danh sách
    public void xoaDonHang(DonHang donHang) {
        danhSachDonHang.remove(donHang);
    }


    // ==================== PHƯƠNG THỨC NGHIỆP VỤ ====================

    // Cộng điểm tích lũy
    public void congDiem(int diem) {

        if (diem <= 0) {
            throw new IllegalArgumentException(
                "Diem cong phai lon hon 0"
            );
        }

        this.diemTichLuy = Math.addExact(this.diemTichLuy, diem);
    }

    // Trừ điểm tích lũy
    public void truDiem(int diem) {

        if (diem <= 0) {
            throw new IllegalArgumentException(
                "Diem tru phai lon hon 0"
            );
        }

        if (diem > this.diemTichLuy) {
            throw new IllegalArgumentException(
                "Khong du diem tich luy de tru"
            );
        }

        this.diemTichLuy -= diem;
    }


    // ==================== TOSTRING ====================

    @Override
    public String toString() {
        return "KhachHang{" +
                "maKH='" + maKH + '\'' +
                ", tenKH='" + tenKH + '\'' +
                ", sdtKH='" + sdtKH + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", diemTichLuy=" + diemTichLuy +
                '}';
    }
}