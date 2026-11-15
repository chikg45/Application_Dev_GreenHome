package entity;

public class TaiKhoan {

    private String maTK;
    private String tenDangNhap;
    private String matKhau;
    private VaiTro vaiTro;
    private boolean dangHoatDong = true;

    // Quan hệ 1-1 với NhanVien
    private NhanVien nhanVien;

    public TaiKhoan() {
    }

    public TaiKhoan(String maTK, String tenDangNhap,
                    String matKhau, VaiTro vaiTro) {
        setMaTK(maTK);
        setTenDangNhap(tenDangNhap);
        setMatKhau(matKhau);
        setVaiTro(vaiTro);
    }

    public boolean isDangHoatDong() { return dangHoatDong; }
    public void setDangHoatDong(boolean v) { this.dangHoatDong = v; }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        if (this.nhanVien == nhanVien) return;
        NhanVien cu = this.nhanVien;
        this.nhanVien = nhanVien;
        if (cu != null && cu.getTaiKhoan() == this) cu.setTaiKhoan(null);
        if (nhanVien != null && nhanVien.getTaiKhoan() != this) nhanVien.setTaiKhoan(this);
    }

    public String getMaTK() { return maTK; }
    public void setMaTK(String maTK) {
        if (maTK == null || !maTK.matches("TK[0-9]{4}"))
            throw new IllegalArgumentException("Mã tài khoản không hợp lệ.");
        if (this.maTK != null && !this.maTK.equals(maTK))
            throw new IllegalArgumentException("Không được đổi mã tài khoản.");
        this.maTK = maTK;
    }
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || !tenDangNhap.matches("[A-Za-z0-9_]{4,30}"))
            throw new IllegalArgumentException("Tên đăng nhập phải từ 4 đến 30 ký tự chữ/số/_.");
        this.tenDangNhap = tenDangNhap;
    }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) {
        // Entity chi nhan HASH; viec bam mat khau do util.MatKhau/BUS phu trach.
        if (matKhau == null || !matKhau.matches("PBKDF2\\$[0-9]{5,7}\\$[A-Za-z0-9+/=]+\\$[A-Za-z0-9+/=]+"))
            throw new IllegalArgumentException("Mật khẩu phải là hash PBKDF2 hợp lệ về định dạng.");
        this.matKhau = matKhau;
    }
    public VaiTro getVaiTro() { return vaiTro; }
    public void setVaiTro(VaiTro vaiTro) {
        if (vaiTro == null) throw new IllegalArgumentException("Vai trò không được null.");
        this.vaiTro = vaiTro;
    }

    @Override
    public String toString() {
        return "TaiKhoan{maTK='" + maTK + "', tenDangNhap='" + tenDangNhap
                + "', vaiTro=" + vaiTro + "}";
    }
}