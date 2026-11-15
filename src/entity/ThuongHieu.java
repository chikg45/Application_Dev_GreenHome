package entity;

import java.util.ArrayList;
import java.util.List;

/** Bang 4.12: ThuongHieu, ma = TH + 3 chu so, ten tu 2 den 50 ky tu. */
public class ThuongHieu {
    private String maThuongHieu;
    private String tenThuongHieu;

    // Phan mo rong da co trong code cu va SQL cua nhom.
    private String moTaThuongHieu;
    private final List<SanPham> danhSachSanPham = new ArrayList<>();

    public ThuongHieu() {
    }

    public ThuongHieu(String maThuongHieu, String tenThuongHieu) {
        setMaThuongHieu(maThuongHieu);
        setTenThuongHieu(tenThuongHieu);
    }

    public String getMaThuongHieu() {
        return maThuongHieu;
    }

    public void setMaThuongHieu(String maThuongHieu) {
        if (maThuongHieu == null || !maThuongHieu.matches("TH\\d{3}")) {
            throw new IllegalArgumentException("Mã thương hiệu phải có dạng TH001.");
        }
        if (this.maThuongHieu != null && !this.maThuongHieu.equals(maThuongHieu)) {
            throw new IllegalArgumentException("Mã thương hiệu không được thay đổi.");
        }
        this.maThuongHieu = maThuongHieu;
    }

    public String getTenThuongHieu() {
        return tenThuongHieu;
    }

    public void setTenThuongHieu(String tenThuongHieu) {
        if (tenThuongHieu == null || tenThuongHieu.trim().length() < 2
                || tenThuongHieu.trim().length() > 50) {
            throw new IllegalArgumentException("Tên thương hiệu không hợp lệ.");
        }
        this.tenThuongHieu = tenThuongHieu.trim();
    }

    // Getter/setter bi danh: giu tuong thich voi Entity cu va cot SQL maTH, tenTH.
    public String getMaTH() {
        return getMaThuongHieu();
    }

    public void setMaTH(String maTH) {
        setMaThuongHieu(maTH);
    }

    public String getTenTH() {
        return getTenThuongHieu();
    }

    public void setTenTH(String tenTH) {
        setTenThuongHieu(tenTH);
    }

    public String getMoTaThuongHieu() {
        return moTaThuongHieu;
    }

    public void setMoTaThuongHieu(String moTaThuongHieu) {
        this.moTaThuongHieu = moTaThuongHieu;
    }

    public List<SanPham> getDanhSachSanPham() {
        return new ArrayList<>(danhSachSanPham);
    }

    public void themSanPham(SanPham sanPham) {
        if (sanPham == null) {
            throw new IllegalArgumentException("Sản phẩm không được null.");
        }
        sanPham.setThuongHieu(this);
    }

    // Goi tu SanPham de giu dong bo quan he hai chieu.
    void themVaoDanhSach(SanPham sanPham) {
        if (!danhSachSanPham.contains(sanPham)) {
            danhSachSanPham.add(sanPham);
        }
    }

    void xoaKhoiDanhSach(SanPham sanPham) {
        danhSachSanPham.remove(sanPham);
    }

    @Override
    public String toString() {
        return maThuongHieu + " - " + tenThuongHieu;
    }
}
