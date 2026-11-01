package entity;

import java.time.LocalDate;
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

        this.maNV = maNV;
        this.hoTen = hoTen;
        this.sdtNV = sdtNV;
        this.soCCCD = soCCCD;
        this.ngaySinh = ngaySinh;
        this.ngayVaoLam = ngayVaoLam;
        this.diaChi = diaChi;
        this.taiKhoan = taiKhoan;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSdtNV() {
        return sdtNV;
    }

    public void setSdtNV(String sdtNV) {
        this.sdtNV = sdtNV;
    }

    public String getSoCCCD() {
        return soCCCD;
    }

    public void setSoCCCD(String soCCCD) {
        this.soCCCD = soCCCD;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public LocalDate getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(LocalDate ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam;
    }

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
        this.taiKhoan = taiKhoan;
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

    // Dem tong so ca trong tuan
    // ngayDauTuan la ngay thu Hai
    public int demSoCaTrongTuan(LocalDate ngayDauTuan) {

        if (ngayDauTuan == null) {
            throw new IllegalArgumentException(
                    "Ngay dau tuan khong duoc null");
        }

        LocalDate ngayCuoiTuan = ngayDauTuan.plusDays(6);
        int soCa = 0;

        for (PhanCongCa pc : danhSachPhanCong) {

            LocalDate ngayLam = pc.getNgayLam();

            if (ngayLam != null
                    && !ngayLam.isBefore(ngayDauTuan)
                    && !ngayLam.isAfter(ngayCuoiTuan)) {

                soCa++;
            }
        }

        return soCa;
    }

    // Tinh tong so ca da duoc phan cong
    public int demTongSoCa() {
        return danhSachPhanCong.size();
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
                ", tongSoCa=" + demTongSoCa() +
                '}';
    }
}