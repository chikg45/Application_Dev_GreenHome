package entity;

import java.time.LocalDate;

public class PhanCongCa {

    private String maPhanCong;
    private LocalDate ngayLam;

    // Quan he voi NhanVien va CaLamViec
    private NhanVien nhanVien;
    private CaLamViec caLamViec;

    public PhanCongCa() {
    }

    public PhanCongCa(String maPhanCong,
                      LocalDate ngayLam,
                      NhanVien nhanVien,
                      CaLamViec caLamViec) {

        if (nhanVien == null || caLamViec == null) {
            throw new IllegalArgumentException(
                    "Nhan vien va ca lam viec khong duoc null");
        }

        this.maPhanCong = maPhanCong;
        this.ngayLam = ngayLam;
        this.caLamViec = caLamViec;

        // Gan quan he hai chieu voi NhanVien
        setNhanVien(nhanVien);
    }

    public String getMaPhanCong() {
        return maPhanCong;
    }

    public void setMaPhanCong(String maPhanCong) {
        this.maPhanCong = maPhanCong;
    }

    public LocalDate getNgayLam() {
        return ngayLam;
    }

    public void setNgayLam(LocalDate ngayLam) {
        this.ngayLam = ngayLam;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {

        if (nhanVien == null) {
            throw new IllegalArgumentException(
                    "Nhan vien khong duoc null");
        }

        if (this.nhanVien == nhanVien) {
            return;
        }

        // Xoa khoi danh sach nhan vien cu
        if (this.nhanVien != null) {
            this.nhanVien.xoaPhanCongNoiBo(this);
        }

        this.nhanVien = nhanVien;

        // Them vao danh sach nhan vien moi
        nhanVien.themPhanCongCa(this);
    }

    public CaLamViec getCaLamViec() {
        return caLamViec;
    }

    public void setCaLamViec(CaLamViec caLamViec) {

        if (caLamViec == null) {
            throw new IllegalArgumentException(
                    "Ca lam viec khong duoc null");
        }

        this.caLamViec = caLamViec;
    }

    @Override
    public String toString() {
        return "PhanCongCa{" +
                "maPhanCong='" + maPhanCong + '\'' +
                ", ngayLam=" + ngayLam +
                ", nhanVien=" +
                (nhanVien != null ? nhanVien.getMaNV() : "null") +
                ", caLamViec=" +
                (caLamViec != null ? caLamViec.getMaCa() : "null") +
                '}';
    }
}