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

    public String getMaTK() { return maTK; }
    public void setMaTK(String maTK) { this.maTK = maTK; }
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public VaiTro getVaiTro() { return vaiTro; }
    public void setVaiTro(VaiTro vaiTro) { this.vaiTro = vaiTro; }

    @Override
    public String toString() {
        return "TaiKhoan{maTK='" + maTK + "', tenDangNhap='" + tenDangNhap
                + "', vaiTro=" + vaiTro + "}";
    }
}