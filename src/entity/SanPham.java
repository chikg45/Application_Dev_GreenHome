package entity;

import java.util.ArrayList;
import java.util.List;
import enums.DanhMucSP;
import enums.TinhTrangSP;

/** Bang 4.3: SanPham. Luu thong tin va quan ly trang thai noi tai cua san pham. */
public class SanPham {
    private String maSP;
    private String tenSP;
    private DanhMucSP danhMuc;
    private ThuongHieu thuongHieu;
    private String thongSoKyThuat;
    private double giaNiemYet;  // Gia da bao gom VAT.
    private String model;
    private String donViTinh;
    private String moTaSP;
    private TinhTrangSP tinhTrang;
    private int soLuongTon;
    private int soLuongLoi;
    private String hinhAnh;

    // Thuoc tinh bo sung cua SQL nhom truong, khong thuoc Bang 4.3.
    private int soLuongTonToiThieu;
    // Quan he voi khuyen mai giu tu code Entity cu.
    private final List<KhuyenMai> khuyenMais = new ArrayList<>();

    public SanPham() {
    }

    // Giu thu tu tham so cua constructor cu; thay DanhMuc bang DanhMucSP enum.
    public SanPham(String maSP, String tenSP, DanhMucSP danhMuc,
                   ThuongHieu thuongHieu, String thongSoKyThuat,
                   double giaNiemYet, String model, String donViTinh,
                   String moTaSP, TinhTrangSP tinhTrang, int soLuongTon,
                   int soLuongTonToiThieu, String hinhAnh) {
        setMaSP(maSP);
        setTenSP(tenSP);
        setDanhMuc(danhMuc);
        setThongSoKyThuat(thongSoKyThuat);
        setGiaNiemYet(giaNiemYet);
        setModel(model);
        setDonViTinh(donViTinh);
        setMoTaSP(moTaSP);
        setSoLuongTon(soLuongTon);
        setSoLuongLoi(0);
        setSoLuongTonToiThieu(soLuongTonToiThieu);
        setHinhAnh(hinhAnh);
        setTinhTrang(tinhTrang);
        setThuongHieu(thuongHieu);
    }

    // Dung khi lay day du soLuongLoi tu JDBC.
    public SanPham(String maSP, String tenSP, DanhMucSP danhMuc,
                   ThuongHieu thuongHieu, String thongSoKyThuat,
                   double giaNiemYet, String model, String donViTinh,
                   String moTaSP, TinhTrangSP tinhTrang, int soLuongTon,
                   int soLuongLoi, int soLuongTonToiThieu, String hinhAnh) {
        this(maSP, tenSP, danhMuc, thuongHieu, thongSoKyThuat,
             giaNiemYet, model, donViTinh, moTaSP, tinhTrang,
             soLuongTon, soLuongTonToiThieu, hinhAnh);
        setSoLuongLoi(soLuongLoi);
    }

    /** Ban sao tach biet, khong tu them vao danh sach san pham cua ThuongHieu. */
    public SanPham(SanPham other) {
        if (other == null) {
            throw new IllegalArgumentException("Sản phẩm sao chép không được null.");
        }
        this.maSP = other.maSP;
        this.tenSP = other.tenSP;
        this.danhMuc = other.danhMuc;
        this.thuongHieu = other.thuongHieu;
        this.thongSoKyThuat = other.thongSoKyThuat;
        this.giaNiemYet = other.giaNiemYet;
        this.model = other.model;
        this.donViTinh = other.donViTinh;
        this.moTaSP = other.moTaSP;
        this.tinhTrang = other.tinhTrang;
        this.soLuongTon = other.soLuongTon;
        this.soLuongLoi = other.soLuongLoi;
        this.soLuongTonToiThieu = other.soLuongTonToiThieu;
        this.hinhAnh = other.hinhAnh;
        this.khuyenMais.addAll(other.khuyenMais);
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        if (maSP == null || maSP.isBlank() || maSP.length() > 20) {
            throw new IllegalArgumentException("Mã sản phẩm không hợp lệ.");
        }
        if (this.maSP != null && !this.maSP.equals(maSP)) {
            throw new IllegalArgumentException("Mã sản phẩm không được đổi sau khi tạo.");
        }
        this.maSP = maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        if (tenSP == null || tenSP.trim().length() < 2 || tenSP.trim().length() > 100) {
            throw new IllegalArgumentException("Tên sản phẩm phải từ 2 đến 100 ký tự.");
        }
        this.tenSP = tenSP.trim();
    }

    public DanhMucSP getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(DanhMucSP danhMuc) {
        if (danhMuc == null) {
            throw new IllegalArgumentException("Danh mục không hợp lệ.");
        }
        this.danhMuc = danhMuc;
    }

    public ThuongHieu getThuongHieu() {
        return thuongHieu;
    }

    public void setThuongHieu(ThuongHieu thuongHieu) {
        if (thuongHieu == null) {
            throw new IllegalArgumentException("Thương hiệu không được trống.");
        }
        if (this.thuongHieu == thuongHieu) {
            return;
        }
        if (this.thuongHieu != null) {
            this.thuongHieu.xoaKhoiDanhSach(this);
        }
        this.thuongHieu = thuongHieu;
        thuongHieu.themVaoDanhSach(this);
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
        if (!Double.isFinite(giaNiemYet) || giaNiemYet <= 0) {
            throw new IllegalArgumentException("Giá niêm yết phải lớn hơn 0.");
        }
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
        if (donViTinh == null || donViTinh.isBlank()) {
            throw new IllegalArgumentException("Đơn vị tính không được trống.");
        }
        this.donViTinh = donViTinh.trim();
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
        if (tinhTrang == null) {
            throw new IllegalArgumentException("Tình trạng không hợp lệ.");
        }
        this.tinhTrang = tinhTrang;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        if (soLuongTon < 0 || soLuongTon < soLuongLoi) {
            throw new IllegalArgumentException("Số lượng tồn không hợp lệ.");
        }
        this.soLuongTon = soLuongTon;
    }

    public int getSoLuongLoi() {
        return soLuongLoi;
    }

    public void setSoLuongLoi(int soLuongLoi) {
        if (soLuongLoi < 0 || soLuongLoi > soLuongTon) {
            throw new IllegalArgumentException("Số lượng lỗi không hợp lệ.");
        }
        this.soLuongLoi = soLuongLoi;
    }

    /** So co the ban = soLuongTon - soLuongLoi, theo Bang 4.3. */
    public int getSoLuongCoTheBan() {
        return soLuongTon - soLuongLoi;
    }

    /** Kiem tra ton co the ban, khong thay the kiem tra giao dich trong BUS/DAO. */
    public boolean kiemTraCoTheBan(int soLuongCan) {
        return soLuongCan > 0 && tinhTrang == TinhTrangSP.DangBan
                && getSoLuongCoTheBan() >= soLuongCan;
    }


    /** Khong tu dong mo ban neu quan ly da chuyen sang NgungBan. */
    public void capNhatTinhTrang() {
        if (tinhTrang == TinhTrangSP.NgungBan) {
            return;
        }
        tinhTrang = (getSoLuongCoTheBan() == 0)
                ? TinhTrangSP.HetHang : TinhTrangSP.DangBan;
    }

    public void capNhatSoLuongLoi(int delta) {
        long slMoi = (long) soLuongLoi + delta;
        if (slMoi < 0 || slMoi > soLuongTon) {
            throw new IllegalArgumentException("Cập nhật số lượng lỗi không hợp lệ.");
        }
        setSoLuongLoi((int) slMoi);
        capNhatTinhTrang();
    }

    public int getSoLuongTonToiThieu() {
        return soLuongTonToiThieu;
    }

    public void setSoLuongTonToiThieu(int soLuongTonToiThieu) {
        if (soLuongTonToiThieu < 0) {
            throw new IllegalArgumentException("Tồn tối thiểu không được âm.");
        }
        this.soLuongTonToiThieu = soLuongTonToiThieu;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    // Quan he giua san pham va cac chuong trinh khuyen mai.
    public void themKhuyenMai(KhuyenMai khuyenMai) {
        if (khuyenMai == null) {
            throw new IllegalArgumentException("Khuyến mãi không được null.");
        }
        if (!khuyenMais.contains(khuyenMai)) {
            khuyenMais.add(khuyenMai);
        }
    }

    public void xoaKhuyenMai(KhuyenMai khuyenMai) {
        khuyenMais.remove(khuyenMai);
    }

    public List<KhuyenMai> getKhuyenMais() {
        return new ArrayList<>(khuyenMais);
    }


    public void capNhatSoLuong(int thayDoi) {
        long slMoi = (long) soLuongTon + thayDoi;
        if (slMoi < 0 || slMoi > Integer.MAX_VALUE || slMoi < soLuongLoi) {
            throw new IllegalArgumentException("Cập nhật số lượng tồn không hợp lệ.");
        }
        setSoLuongTon((int) slMoi);
        capNhatTinhTrang();
    }

    @Override
    public String toString() {
        return "SanPham{" + "maSP='" + maSP + '\''
                + ", tenSP='" + tenSP + '\''
                + ", giaNiemYet=" + giaNiemYet
                + ", tinhTrang=" + tinhTrang + '}';
    }
}
