package bus;

import dao.NhanVienDAO;
import entity.NhanVien;
import entity.TaiKhoan;
import entity.VaiTro;
import util.MatKhau;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;

/** Quy trinh 2.8, NV-01..NV-03; nghiep vu va xac thuc quyen ghi o DAO. */
public class NhanVienBUS {
    private final String maQuanLy;
    private final NhanVienDAO dao;
    public NhanVienBUS(String maQuanLy) { this(maQuanLy, new NhanVienDAO()); }
    public NhanVienBUS(String maQuanLy, NhanVienDAO dao) {
        this.maQuanLy = maQuanLy; this.dao = Objects.requireNonNull(dao);
    }
    public List<NhanVien> layTatCa() { return dao.layTatCa(maQuanLy); }
    public List<NhanVien> layNhanVienCoThePhanCong() { return dao.layNhanVienCoThePhanCong(maQuanLy); }
    public NhanVien timTheoMa(String maNV) { return dao.timTheoMa(maQuanLy, maNV); }

    private static void kiemTra(NhanVien nv) {
        if (nv == null || nv.getTaiKhoan() == null) throw new IllegalArgumentException("Thiếu hồ sơ hoặc tài khoản.");
        TaiKhoan tk = nv.getTaiKhoan();
        if (nv.getHoTen() == null || nv.getHoTen().trim().length() < 2
                || nv.getHoTen().trim().length() > 50) throw new IllegalArgumentException("Họ tên phải từ 2 đến 50 ký tự.");
        if (nv.getSdtNV() == null || !nv.getSdtNV().matches("0\\d{9}"))
            throw new IllegalArgumentException("Số điện thoại phải gồm 10 số, bắt đầu bằng 0.");
        if (nv.getSoCCCD() == null || !nv.getSoCCCD().matches("\\d{12}"))
            throw new IllegalArgumentException("CCCD phải gồm đúng 12 chữ số.");
        if (nv.getNgaySinh() == null || Period.between(nv.getNgaySinh(), LocalDate.now()).getYears()<18)
            throw new IllegalArgumentException("Nhân viên phải đủ 18 tuổi.");
        if (nv.getNgayVaoLam() == null || nv.getNgayVaoLam().isAfter(LocalDate.now())
                || nv.getNgayVaoLam().isBefore(nv.getNgaySinh()))
            throw new IllegalArgumentException("Ngày vào làm không hợp lệ.");
        if (tk.getTenDangNhap()==null || !tk.getTenDangNhap().matches("[a-zA-Z0-9_]{4,30}"))
            throw new IllegalArgumentException("Tên đăng nhập 4–30 ký tự, chỉ chữ/số/_.");
        if (tk.getVaiTro()==null) throw new IllegalArgumentException("Chưa chọn vai trò.");
        if (!nv.isDangLamViec()) tk.setDangHoatDong(false);
    }
    /** Mat khau 123456 chi la gia tri khoi tao DEMO, khong dung cho moi truong that. */
    public NhanVien themNhanVien(NhanVien nv) {
        kiemTra(nv);
        char[] password = "123456".toCharArray();
        try { nv.getTaiKhoan().setMatKhau(MatKhau.taoHash(password)); }
        finally { java.util.Arrays.fill(password, '\0'); }
        return dao.them(maQuanLy, nv, nv.getTaiKhoan());
    }
    public boolean capNhatNhanVien(NhanVien nv) {
        kiemTra(nv);
        if (nv.getMaNV()==null || nv.getMaNV().isBlank())
            throw new IllegalArgumentException("Thiếu mã nhân viên cần cập nhật.");
        return dao.capNhat(maQuanLy, nv);
    }
    public boolean khoaMoTaiKhoan(String maNV, boolean mo) {
        return dao.khoaMoTaiKhoan(maQuanLy,maNV,mo);
    }
}
