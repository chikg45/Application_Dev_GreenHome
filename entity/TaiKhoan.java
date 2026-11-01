package entity;

public class TaiKhoan {

    private String maTK;
    private String tenDangNhap;
    private String matKhau;
    private VaiTro vaiTro;

    // Quan hệ 1-1 với NhanVien
    private NhanVien nhanVien;

    public TaiKhoan() {
    }

    public TaiKhoan(String maTK, String tenDangNhap,
                    String matKhau, VaiTro vaiTro) {
        this.maTK = maTK;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    // Getter, setter các thuộc tính còn lại
}