package entity;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NhanVien {

    private String maNV;
    private String hoTen;
    private String sdtNV;
    private String soCCCD;
    private LocalDate ngaySinh;
    private LocalDate ngayVaoLam;
    private String diaChi;
    private boolean dangLamViec = true;

    // Quan he 1 - 1 voi TaiKhoan
    private TaiKhoan taiKhoan;

    // Quan he 1 - N voi PhanCongCa
    private List<PhanCongCa> danhSachPhanCong = new ArrayList<>();

    public NhanVien() {
    }

    public NhanVien(String maNV, String hoTen, String sdtNV,
                    String soCCCD, LocalDate ngaySinh,
                    LocalDate ngayVaoLam, String diaChi,
                    TaiKhoan taiKhoan) {

        setMaNV(maNV);
        setHoTen(hoTen);
        setSdtNV(sdtNV);
        setSoCCCD(soCCCD);
        setNgaySinh(ngaySinh);
        setNgayVaoLam(ngayVaoLam);
        setDiaChi(diaChi);
        if (taiKhoan != null) setTaiKhoan(taiKhoan);
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        // Bang 4.1: NV + dung 4 chu so.
        if (maNV == null || !maNV.matches("NV[0-9]{4}"))
            throw new IllegalArgumentException("Mã nhân viên phải có dạng NV0001 (NV + 4 chữ số).");
        if (this.maNV != null && !this.maNV.equals(maNV))
            throw new IllegalArgumentException("Mã nhân viên không được thay đổi.");
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().length() < 2 || hoTen.trim().length() > 50)
            throw new IllegalArgumentException("Họ tên nhân viên phải từ 2 đến 50 ký tự.");
        this.hoTen = hoTen.trim();
    }

    public String getSdtNV() {
        return sdtNV;
    }

    public void setSdtNV(String sdtNV) {
        if (sdtNV == null || !sdtNV.matches("0\\d{9}"))
            throw new IllegalArgumentException("SĐT nhân viên phải có 10 chữ số, bắt đầu bằng 0.");
        this.sdtNV = sdtNV;
    }

    public String getSoCCCD() {
        return soCCCD;
    }

    public void setSoCCCD(String soCCCD) {
        if (soCCCD == null || !soCCCD.matches("\\d{12}"))
            throw new IllegalArgumentException("CCCD phải gồm đúng 12 chữ số.");
        this.soCCCD = soCCCD;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        if (ngaySinh == null || ngaySinh.isAfter(LocalDate.now().minusYears(18)))
            throw new IllegalArgumentException("Nhân viên phải đủ 18 tuổi.");
        if (ngayVaoLam != null && ngayVaoLam.isBefore(ngaySinh))
            throw new IllegalArgumentException("Ngày sinh không được sau ngày vào làm.");
        this.ngaySinh = ngaySinh;
    }

    public LocalDate getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(LocalDate ngayVaoLam) {
        if (ngayVaoLam == null || ngayVaoLam.isAfter(LocalDate.now())
                || (ngaySinh != null && ngayVaoLam.isBefore(ngaySinh)))
            throw new IllegalArgumentException("Ngày vào làm không hợp lệ.");
        this.ngayVaoLam = ngayVaoLam;
    }

    public boolean isDangLamViec() { return dangLamViec; }
    public void setDangLamViec(boolean v) { this.dangLamViec = v; }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        if (this.taiKhoan == taiKhoan) return;
        TaiKhoan cu = this.taiKhoan;
        this.taiKhoan = taiKhoan;
        if (cu != null && cu.getNhanVien() == this) cu.setNhanVien(null);
        if (taiKhoan != null && taiKhoan.getNhanVien() != this) taiKhoan.setNhanVien(this);
    }

    // Lay danh sach phan cong ca
    public List<PhanCongCa> getDanhSachPhanCong() {
        return Collections.unmodifiableList(danhSachPhanCong);
    }

    // Them phan cong ca cho nhan vien
    public void themPhanCongCa(PhanCongCa phanCong) {

        if (phanCong == null) {
            throw new IllegalArgumentException(
                    "Phan cong ca khong duoc null");
        }

        if (phanCong.getNhanVien() != this) {
            throw new IllegalArgumentException(
                    "Phan cong khong thuoc nhan vien nay");
        }

        if (!danhSachPhanCong.contains(phanCong)) {
            danhSachPhanCong.add(phanCong);
        }
    }

    // Dong bo quan he khi chuyen phan cong sang nhan vien khac
    void xoaPhanCongNoiBo(PhanCongCa phanCong) {
        danhSachPhanCong.remove(phanCong);
    }


    @Override
    public String toString() {
        return "NhanVien{" +
                "maNV='" + maNV + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", sdtNV='" + sdtNV + '\'' +
                ", soCCCD='" + soCCCD + '\'' +
                ", ngaySinh=" + ngaySinh +
                ", ngayVaoLam=" + ngayVaoLam +
                ", diaChi='" + diaChi + '\'' +
                '}';
    }
}