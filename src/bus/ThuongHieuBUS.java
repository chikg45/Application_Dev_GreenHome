package bus;

import dao.ThuongHieuDAO;
import entity.ThuongHieu;

import java.util.List;

/** Nghiep vu ThuongHieu: validation o day, JDBC o DAO. */
public class ThuongHieuBUS {
    private final ThuongHieuDAO dao;
    private final String maNVDangNhap;

    /** maNV lay tu phien dang nhap da xac thuc, KHONG lay tu o nhap cua nguoi dung. */
    public ThuongHieuBUS(String maNVDangNhap) {
        this(maNVDangNhap, new ThuongHieuDAO());
    }

    public ThuongHieuBUS(String maNVDangNhap, ThuongHieuDAO dao) {
        if (dao == null) throw new IllegalArgumentException("DAO không được null.");
        this.maNVDangNhap = maNVDangNhap;
        this.dao = dao;
    }

    public List<ThuongHieu> layTatCa() {
        return dao.layTatCa();
    }

    public ThuongHieu timTheoMa(String maTH) {
        return dao.timTheoMa(maTH);
    }

    public List<ThuongHieu> timKiem(String tuKhoa) {
        return dao.timKiem(tuKhoa);
    }

    public boolean themThuongHieu(ThuongHieu th) {
        kiemTraDuLieu(th);
        if (dao.timTheoMa(th.getMaThuongHieu()) != null) {
            throw new IllegalArgumentException("Mã thương hiệu đã tồn tại.");
        }
        return dao.them(maNVDangNhap, th);
    }

    public boolean capNhatThuongHieu(ThuongHieu th) {
        kiemTraDuLieu(th);
        return dao.capNhat(maNVDangNhap, th);
    }

    public boolean xoaThuongHieu(String maTH) {
        if (maTH == null || !maTH.matches("TH\\d{3}")) {
            throw new IllegalArgumentException("Mã thương hiệu phải có dạng TH001.");
        }
        return dao.xoa(maNVDangNhap, maTH);
    }

    private void kiemTraDuLieu(ThuongHieu th) {
        if (th == null) throw new IllegalArgumentException("Thương hiệu không được null.");
        if (th.getMaThuongHieu() == null || !th.getMaThuongHieu().matches("TH\\d{3}")) {
            throw new IllegalArgumentException("Mã thương hiệu phải có dạng TH001.");
        }
        String ten = th.getTenThuongHieu();
        if (ten == null || ten.trim().length() < 2 || ten.trim().length() > 50) {
            throw new IllegalArgumentException("Tên thương hiệu phải từ 2 đến 50 ký tự.");
        }
    }
}
