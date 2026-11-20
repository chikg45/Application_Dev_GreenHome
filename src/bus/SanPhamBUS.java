package bus;

import dao.SanPhamDAO;
import dao.ThuongHieuDAO;
import entity.SanPham;
import enums.DanhMucSP;
import enums.TinhTrangSP;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.nio.file.Path;
import util.ExcelXlsx;

/** Nghiep vu UC002 va SP-01...SP-08, khong viet SQL trong BUS. */
public class SanPhamBUS {
    private final String maNVDangNhap;
    private final SanPhamDAO sanPhamDAO;
    private final ThuongHieuDAO thuongHieuDAO;

    /** Ma nhan vien phai den tu phien dang nhap, khong cho nguoi dung tu nhap. */
    public SanPhamBUS(String maNVDangNhap) {
        this(maNVDangNhap, new SanPhamDAO(), new ThuongHieuDAO());
    }

    public SanPhamBUS(String maNVDangNhap, SanPhamDAO sanPhamDAO,
                      ThuongHieuDAO thuongHieuDAO) {
        if (sanPhamDAO == null || thuongHieuDAO == null)
            throw new IllegalArgumentException("DAO không được null.");
        this.maNVDangNhap = maNVDangNhap;
        this.sanPhamDAO = sanPhamDAO;
        this.thuongHieuDAO = thuongHieuDAO;
    }

    /** Ca Nhan vien ban hang va Quan ly deu duoc xem/tim kiem. */
    public List<SanPham> layTatCa() {
        return sanPhamDAO.layTatCa();
    }

    public SanPham timTheoMa(String maSP) {
        return sanPhamDAO.timTheoMa(maSP);
    }

    public List<SanPham> timKiem(String tuKhoa) {
        return sanPhamDAO.timKiem(tuKhoa);
    }

    public List<SanPham> timKiemNangCao(String tuKhoa, DanhMucSP danhMuc,
                                       String maThuongHieu, TinhTrangSP tinhTrang) {
        return sanPhamDAO.timKiemNangCao(tuKhoa, danhMuc, maThuongHieu, tinhTrang);
    }

    public boolean themSanPham(SanPham sanPham) {
        kiemTraDuLieu(sanPham);
        if (sanPhamDAO.timTheoMa(sanPham.getMaSP()) != null) {
            throw new IllegalArgumentException("Mã sản phẩm đã tồn tại.");
        }
        kiemTraThuongHieu(sanPham);
        SanPham banLuu = new SanPham(sanPham);
        banLuu.capNhatTinhTrang(); // HetHang tu dong neu khong con so luong co the ban.
        return sanPhamDAO.them(maNVDangNhap, banLuu);
    }

    public boolean capNhatSanPham(SanPham sanPham) {
        kiemTraDuLieu(sanPham);
        kiemTraThuongHieu(sanPham);
        SanPham banLuu = new SanPham(sanPham);
        banLuu.capNhatTinhTrang();
        return sanPhamDAO.capNhat(maNVDangNhap, banLuu);
    }

    /** Cap nhat trang thai theo quy trinh 2.2: DangBan hoac NgungBan. */
    public boolean doiTinhTrang(String maSP, TinhTrangSP tinhTrangMoi) {
        if (tinhTrangMoi == null || tinhTrangMoi == TinhTrangSP.HetHang) {
            throw new IllegalArgumentException("Quản lý chỉ chọn Đang bán hoặc Ngừng bán.");
        }
        SanPham sp = layBatBuoc(maSP);
        if (tinhTrangMoi == TinhTrangSP.DangBan && sp.getSoLuongCoTheBan() == 0) {
            throw new IllegalArgumentException("Không thể mở bán sản phẩm đã hết hàng.");
        }
        sp.setTinhTrang(tinhTrangMoi);
        return capNhatSanPham(sp);
    }

    /** Thay doi so luong duoc quy dinh o SP-08, khong quan ly phieu nhap kho. */
    public boolean capNhatSoLuong(String maSP, int soLuongThayDoi) {
        SanPham sp = layBatBuoc(maSP);
        sp.capNhatSoLuong(soLuongThayDoi);
        return capNhatSanPham(sp);
    }

    /** Phuong thuc capNhatSoLuongLoi() giu o Entity theo Bang 4.3. */
    public boolean capNhatSoLuongLoi(String maSP, int delta) {
        SanPham sp = layBatBuoc(maSP);
        sp.capNhatSoLuongLoi(delta);
        return capNhatSanPham(sp);
    }

    /** UC002 co thao tac Xoa, SP-07 khong cho xoa san pham da co giao dich. */
    public boolean xoaSanPham(String maSP) {
        if (maSP == null || maSP.isBlank())
            throw new IllegalArgumentException("Mã sản phẩm không được trống.");
        return sanPhamDAO.xoa(maNVDangNhap, maSP);
    }

    /** Neu nguong chua cai dat (=0), dung nguong mac dinh 5 sp cho canh bao. */
    public static int nguongCanhBao(SanPham p) {
        return p.getSoLuongTonToiThieu() > 0 ? p.getSoLuongTonToiThieu() : 5;
    }

    public static boolean sapHetHang(SanPham p) {
        return p.getTinhTrang() == TinhTrangSP.DangBan
                && p.getSoLuongCoTheBan() > 0
                && p.getSoLuongCoTheBan() <= nguongCanhBao(p);
    }

    /** Xem truoc file Excel; chua ghi DB, moi dong sai bao ro so dong. */
    public List<SanPham> docExcel(Path file) {
        List<ExcelXlsx.Dong> rows = ExcelXlsx.doc(file,
                "maSP", "tenSP", "danhMuc", "maTH", "giaNiemYet", "soLuongTon");
        Map<String, entity.ThuongHieu> thuongHieu = new HashMap<>();
        for (entity.ThuongHieu th : thuongHieuDAO.layTatCa()) {
            thuongHieu.put(th.getMaThuongHieu().toUpperCase(java.util.Locale.ROOT), th);
        }
        Set<String> daCo = new HashSet<>();
        for (SanPham p : sanPhamDAO.layTatCa()) daCo.add(p.getMaSP().toUpperCase(java.util.Locale.ROOT));
        List<SanPham> result = new ArrayList<>();
        Set<String> trongFile = new HashSet<>();
        for (ExcelXlsx.Dong row : rows) {
            try {
                String ma = row.batBuoc("maSP").toUpperCase(java.util.Locale.ROOT);
                if (daCo.contains(ma)) throw new IllegalArgumentException("Mã sản phẩm " + ma + " đã có trong SQL Server.");
                if (!trongFile.add(ma)) throw new IllegalArgumentException("Mã sản phẩm " + ma + " bị lặp trong Excel.");
                String tenDM = row.batBuoc("danhMuc");
                DanhMucSP danhMuc;
                if (tenDM.matches("(?i)DM00[1-7]"))
                    danhMuc = DanhMucSP.values()[Integer.parseInt(tenDM.substring(2))-1];
                else danhMuc = DanhMucSP.tuTen(tenDM);
                entity.ThuongHieu th = thuongHieu.get(row.batBuoc("maTH").toUpperCase(java.util.Locale.ROOT));
                if (th == null) throw new IllegalArgumentException("Mã thương hiệu không tồn tại: " + row.lay("maTH"));
                SanPham p = new SanPham(ma, row.batBuoc("tenSP"), danhMuc, th,
                        row.lay("thongSoKyThuat"), ExcelXlsx.soThucDuong(row, "giaNiemYet"),
                        row.lay("model"), row.lay("donViTinh").isEmpty() ? "Cái" : row.lay("donViTinh"),
                        row.lay("moTaSP"), TinhTrangSP.DangBan,
                        ExcelXlsx.soNguyen(row, "soLuongTon", 0),
                        ExcelXlsx.soNguyen(row, "soLuongLoi", 0),
                        ExcelXlsx.soNguyen(row, "soLuongTonToiThieu", 5), row.lay("hinhAnh"));
                p.capNhatTinhTrang();
                kiemTraDuLieu(p);
                result.add(p);
            } catch (RuntimeException ex) {
                if (ex.getMessage()!=null && ex.getMessage().startsWith("Dòng ")) throw ex;
                throw new IllegalArgumentException("Dòng " + row.soDong() + ": " + ex.getMessage(), ex);
            }
        }
        return result;
    }

    /** Import them moi: tat ca thanh cong hoac tat ca rollback. */
    public int nhapExcel(List<SanPham> danhSach) {
        if (danhSach == null || danhSach.isEmpty())
            throw new IllegalArgumentException("Không có sản phẩm để import.");
        Set<String> ma = new HashSet<>();
        for (SanPham p : danhSach) {
            kiemTraDuLieu(p);
            if (!ma.add(p.getMaSP().toUpperCase(java.util.Locale.ROOT)))
                throw new IllegalArgumentException("Trùng mã sản phẩm trong file: " + p.getMaSP());
            p.capNhatTinhTrang();
        }
        return sanPhamDAO.themHangLoat(maNVDangNhap, danhSach);
    }

    /** Nhap them ton kho, khong ghi de so luong dang co. */
    public int boSungTonExcel(Path file) {
        List<ExcelXlsx.Dong> rows = ExcelXlsx.doc(file, "maSP", "soLuongNhap");
        Map<String, Integer> soLuong = new java.util.LinkedHashMap<>();
        for (ExcelXlsx.Dong row : rows) {
            String ma = row.batBuoc("maSP").toUpperCase(java.util.Locale.ROOT);
            int sl = ExcelXlsx.soNguyen(row, "soLuongNhap", 0);
            if (sl <= 0) throw new IllegalArgumentException("Dòng " + row.soDong()
                    + ": số lượng nhập phải là số nguyên dương.");
            if (soLuong.putIfAbsent(ma, sl) != null)
                throw new IllegalArgumentException("Dòng " + row.soDong() + ": mã " + ma + " bị trùng.");
        }
        return sanPhamDAO.congTonHangLoat(maNVDangNhap, soLuong);
    }

    private SanPham layBatBuoc(String maSP) {
        SanPham sp = sanPhamDAO.timTheoMa(maSP);
        if (sp == null) throw new IllegalArgumentException("Không tìm thấy sản phẩm.");
        return sp;
    }

    /** SQL cung co FK, nen du lieu phai ton tai trong dbo.ThuongHieu. */
    private void kiemTraThuongHieu(SanPham sp) {
        if (thuongHieuDAO.timTheoMa(sp.getThuongHieu().getMaThuongHieu()) == null) {
            throw new IllegalArgumentException("Thương hiệu chưa tồn tại trong CSDL.");
        }
    }

    private void kiemTraDuLieu(SanPham sp) {
        if (sp == null) throw new IllegalArgumentException("Sản phẩm không được null.");
        if (sp.getMaSP() == null || sp.getMaSP().isBlank() || sp.getMaSP().length() > 20)
            throw new IllegalArgumentException("Mã sản phẩm không hợp lệ.");
        if (sp.getTenSP() == null || sp.getTenSP().trim().length() < 2
                || sp.getTenSP().trim().length() > 100)
            throw new IllegalArgumentException("Tên sản phẩm phải từ 2 đến 100 ký tự.");
        if (sp.getDanhMuc() == null || sp.getThuongHieu() == null)
            throw new IllegalArgumentException("Danh mục và thương hiệu là bắt buộc.");
        if (sp.getDonViTinh() == null || sp.getDonViTinh().isBlank())
            throw new IllegalArgumentException("Đơn vị tính không được trống.");
        if (!Double.isFinite(sp.getGiaNiemYet()) || sp.getGiaNiemYet() <= 0)
            throw new IllegalArgumentException("Giá niêm yết phải lớn hơn 0.");
        if (sp.getTinhTrang() == null)
            throw new IllegalArgumentException("Tình trạng sản phẩm không hợp lệ.");
        if (sp.getSoLuongTon() < 0 || sp.getSoLuongLoi() < 0
                || sp.getSoLuongLoi() > sp.getSoLuongTon()
                || sp.getSoLuongTonToiThieu() < 0)
            throw new IllegalArgumentException("Số lượng tồn/hàng lỗi không hợp lệ.");
    }
}
