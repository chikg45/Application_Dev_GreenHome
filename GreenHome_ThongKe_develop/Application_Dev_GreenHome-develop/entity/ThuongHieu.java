package entity;

import java.util.ArrayList;
import java.util.List;

public class ThuongHieu {

    private String maTH;
    private String tenTH;
    private String moTaThuongHieu;
    private List<SanPham> danhSachSanPham = new ArrayList<>();

    public ThuongHieu() {
    }

    public ThuongHieu(String maTH, String tenTH) {
        this.maTH = maTH;
        this.tenTH = tenTH;
    }

    public String getMaTH() {
        return maTH;
    }

    public void setMaTH(String maTH) {
        this.maTH = maTH;
    }

    public String getTenTH() {
        return tenTH;
    }

    public void setTenTH(String tenTH) {
        this.tenTH = tenTH;
    }

    public List<SanPham> getDanhSachSanPham() {
        return new ArrayList<>(danhSachSanPham);
    }

    public void themSanPham(SanPham sanPham) {
        if (sanPham == null) {
            throw new IllegalArgumentException("San pham khong duoc null");
        }

        sanPham.setThuongHieu(this);
    }

    // Dùng để đồng bộ danh sách từ SanPham
    void themVaoDanhSach(SanPham sanPham) {
        if (!danhSachSanPham.contains(sanPham)) {
            danhSachSanPham.add(sanPham);
        }
    }

    void xoaKhoiDanhSach(SanPham sanPham) {
        danhSachSanPham.remove(sanPham);
    }
}