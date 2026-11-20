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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;
import util.ExcelXlsx;

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
    private static final DateTimeFormatter EXCEL_DATE = DateTimeFormatter
            .ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    /** Xem truoc import nhan vien: ma NV/TK va mat khau se tu sinh khi luu. */
    public List<NhanVien> docExcel(Path file) {
        List<ExcelXlsx.Dong> rows = ExcelXlsx.doc(file, "hoTen", "sdtNV", "soCCCD",
                "ngaySinh", "ngayVaoLam", "tenDangNhap", "vaiTro");
        if (rows.size() > 200)
            throw new IllegalArgumentException("Mỗi lần chỉ nhập tối đa 200 nhân viên để tránh tạo quá nhiều mật khẩu demo.");
        Set<String> usernames = new HashSet<>(), phones = new HashSet<>(), cccds = new HashSet<>();
        for (NhanVien nv : dao.layTatCa(maQuanLy)) {
            phones.add(nv.getSdtNV()); cccds.add(nv.getSoCCCD());
            if (nv.getTaiKhoan() != null)
                usernames.add(nv.getTaiKhoan().getTenDangNhap().toLowerCase(Locale.ROOT));
        }
        List<NhanVien> result = new ArrayList<>();
        for (ExcelXlsx.Dong row : rows) {
            try {
                NhanVien nv = new NhanVien();
                nv.setHoTen(row.batBuoc("hoTen"));
                nv.setSdtNV(row.batBuoc("sdtNV"));
                nv.setSoCCCD(row.batBuoc("soCCCD"));
                nv.setNgaySinh(docNgay(row, "ngaySinh"));
                nv.setNgayVaoLam(docNgay(row, "ngayVaoLam"));
                nv.setDiaChi(row.lay("diaChi"));
                nv.setDangLamViec(true);
                TaiKhoan tk = new TaiKhoan();
                tk.setTenDangNhap(row.batBuoc("tenDangNhap").toLowerCase(Locale.ROOT));
                String role = row.batBuoc("vaiTro").toUpperCase(Locale.ROOT);
                if (role.equals("QUAN_LY")) tk.setVaiTro(VaiTro.QUAN_LY);
                else if (role.equals("NHAN_VIEN_BAN_HANG")) tk.setVaiTro(VaiTro.NHAN_VIEN_BAN_HANG);
                else throw new IllegalArgumentException("Vai trò phải là QUAN_LY hoặc NHAN_VIEN_BAN_HANG.");
                tk.setDangHoatDong(true);
                nv.setTaiKhoan(tk);
                kiemTra(nv);
                if (!usernames.add(tk.getTenDangNhap())) throw new IllegalArgumentException("Tên đăng nhập bị trùng: " + tk.getTenDangNhap());
                if (!phones.add(nv.getSdtNV())) throw new IllegalArgumentException("SĐT đã tồn tại: " + nv.getSdtNV());
                if (!cccds.add(nv.getSoCCCD())) throw new IllegalArgumentException("CCCD đã tồn tại: " + nv.getSoCCCD());
                result.add(nv);
            } catch (RuntimeException ex) {
                if (ex.getMessage()!=null && ex.getMessage().startsWith("Dòng ")) throw ex;
                throw new IllegalArgumentException("Dòng " + row.soDong() + ": " + ex.getMessage(), ex);
            }
        }
        return result;
    }

    private static LocalDate docNgay(ExcelXlsx.Dong row, String cot) {
        try { return LocalDate.parse(row.batBuoc(cot), EXCEL_DATE); }
        catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException("'" + cot + "' phải đúng dd/MM/yyyy, ví dụ 01/11/2026.");
        }
    }

    /** Hash moi mat khau rieng, ghi 2 bang trong mot transaction duy nhat. */
    public int nhapExcel(List<NhanVien> danhSach) {
        if (danhSach == null || danhSach.isEmpty())
            throw new IllegalArgumentException("Không có nhân viên để import.");
        for (NhanVien nv : danhSach) {
            kiemTra(nv);
            char[] password = "123456".toCharArray();
            try { nv.getTaiKhoan().setMatKhau(MatKhau.taoHash(password)); }
            finally { java.util.Arrays.fill(password, '\0'); }
        }
        return dao.themHangLoat(maQuanLy, danhSach);
    }

    public boolean khoaMoTaiKhoan(String maNV, boolean mo) {
        return dao.khoaMoTaiKhoan(maQuanLy,maNV,mo);
    }
}
