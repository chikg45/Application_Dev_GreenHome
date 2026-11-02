package util;

import entity.NhanVien;
import entity.VaiTro;

/** Nhân viên được lấy từ kết quả đăng nhập CSDL, không chọn ngẫu nhiên nhân viên bán. */
public final class PhienDangNhap {
    private static NhanVien nhanVien;
    private PhienDangNhap() {}
    public static void batDau(NhanVien nv) { nhanVien = nv; }
    public static void ketThuc() { nhanVien = null; }
    public static NhanVien hienTai() {
        if (nhanVien == null) throw new IllegalStateException("Vui lòng chạy ui.LoginFrame và đăng nhập trước.");
        return nhanVien;
    }
    public static boolean laQuanLy() { return hienTai().getTaiKhoan().getVaiTro() == VaiTro.QUAN_LY; }
}
