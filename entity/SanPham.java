package entity;

import enums.DanhMuc;
import enums.TinhTrangSP;

import java.util.ArrayList;
import java.util.List;

public class SanPham {

    private final List<KhuyenMai> khuyenMais = new ArrayList<>();
    private String maSP;
    private String tenSP;
    private DanhMuc danhMuc;
    private ThuongHieu thuongHieu;
    private String thongSoKyThuat;
    private double giaNiemYet;
    private String model;
    private String donViTinh;
    private String moTaSP;
    private TinhTrangSP tinhTrang;
    private int soLuongTon;
    private int soLuongTonToiThieu;
    private String hinhAnh;

    public SanPham() {
     
    }

    public SanPham(String maSP, String tenSP, DanhMuc danhMuc,
                   ThuongHieu thuongHieu, String thongSoKyThuat,
                   double giaNiemYet, String model, String donViTinh,
                   String moTaSP, TinhTrangSP tinhTrang, int soLuongTon,
                   int soLuongTonToiThieu, String hinhAnh) {

        this.maSP = maSP;
        this.tenSP = tenSP;
        this.danhMuc = danhMuc;
        this.thuongHieu = thuongHieu;
        this.thongSoKyThuat = thongSoKyThuat;
        this.giaNiemYet = giaNiemYet;
        this.model = model;
        this.donViTinh = donViTinh;
        this.moTaSP = moTaSP;
        this.tinhTrang = tinhTrang;
        this.soLuongTon = soLuongTon;
        this.soLuongTonToiThieu = soLuongTonToiThieu;
        this.hinhAnh = hinhAnh;

 
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public DanhMuc getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(DanhMuc danhMuc) {
        this.danhMuc = danhMuc;
    }

    public ThuongHieu getThuongHieu() {
        return thuongHieu;
    }

    public void setThuongHieu(ThuongHieu thuongHieu) {
        this.thuongHieu = thuongHieu;
    }

    public String getThongSoKyThuat() {
        return thongSoKyThuat;
    }

    public void setThongSoKyThuat(String thongSoKyThuat) {
        this.thongSoKyThuat = thongSoKyThuat;
    }

    public double getGiaNiemYet() {
        return giaNiemYet;
    }

    public void setGiaNiemYet(double giaNiemYet) {
        this.giaNiemYet = giaNiemYet;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public String getMoTaSP() {
        return moTaSP;
    }

    public void setMoTaSP(String moTaSP) {
        this.moTaSP = moTaSP;
    }

    public TinhTrangSP getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(TinhTrangSP tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public int getSoLuongTonToiThieu() {
        return soLuongTonToiThieu;
    }

    public void setSoLuongTonToiThieu(int soLuongTonToiThieu) {
        this.soLuongTonToiThieu = soLuongTonToiThieu;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    // ==============================
    // QUAN HỆ VỚI KHUYẾN MÃI
    // ==============================

    public List<KhuyenMai> getKhuyenMais() {
        return new ArrayList<>(khuyenMais);
    }

  

    // ==============================
    // CÁC PHƯƠNG THỨC NGHIỆP VỤ
    // ==============================

    public double tinhGiaSauGiam(double phanTramGiam) {
        if (phanTramGiam < 0 || phanTramGiam > 100) {
            throw new IllegalArgumentException(
                "Phan tram giam khong hop le"
            );
        }

        return giaNiemYet * (1 - phanTramGiam / 100);
    }

    public boolean kiemTraConHang(int soLuongCan) {
        return soLuongCan > 0 && soLuongTon >= soLuongCan;
    }

    public void capNhatSoLuong(int thayDoi) {
        if (soLuongTon + thayDoi < 0) {
            throw new IllegalArgumentException("So luong ton am");
        }

        soLuongTon += thayDoi;
    }

    @Override
    public String toString() {
        return "SanPham{" +
                "maSP='" + maSP + '\'' +
                ", tenSP='" + tenSP + '\'' +
                ", danhMuc=" + danhMuc +
                ", thuongHieu=" + thuongHieu +
                ", giaNiemYet=" + giaNiemYet +
                ", model='" + model + '\'' +
                ", donViTinh='" + donViTinh + '\'' +
                ", moTaSP='" + moTaSP + '\'' +
                ", tinhTrang=" + tinhTrang +
                ", soLuongTon=" + soLuongTon +
                ", soLuongTonToiThieu=" + soLuongTonToiThieu +
                ", hinhAnh='" + hinhAnh + '\'' +
                '}';
    }
}