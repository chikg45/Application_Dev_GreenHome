package entity;

public class ChiTietPhieuDoiTra {

    private int soLuong;
    private double donGia;
    private String serialIMEI;
    private String lyDo;
    private TinhTrangKhiDoiTra tinhTrangKhiDoiTra;
    private HinhThucXuLy hinhThucXuLy;

    // Quan hệ OOP
    private SanPham sanPham;
    private PhieuDoiTra phieuDoiTra;
    private ChiTietHoaDon chiTietHoaDon;

    // Constructor không tham số
    public ChiTietPhieuDoiTra() {
    }

    // Constructor đầy đủ
    public ChiTietPhieuDoiTra(
            int soLuong,
            double donGia,
            String serialIMEI,
            String lyDo,
            TinhTrangKhiDoiTra tinhTrangKhiDoiTra,
            HinhThucXuLy hinhThucXuLy,
            SanPham sanPham,
            PhieuDoiTra phieuDoiTra,
            ChiTietHoaDon chiTietHoaDon) {

        if (soLuong <= 0) {
            throw new IllegalArgumentException(
                    "So luong phai lon hon 0");
        }

        if (!Double.isFinite(donGia) || donGia <= 0) {
            throw new IllegalArgumentException("Đơn giá phải lớn hơn 0 và hữu hạn.");
        }

        if (sanPham == null || phieuDoiTra == null
                || chiTietHoaDon == null) {
            throw new IllegalArgumentException(
                    "Thong tin quan he khong duoc null");
        }

        if (chiTietHoaDon.getSanPham() != sanPham) {
            throw new IllegalArgumentException(
                    "San pham khong khop voi chi tiet hoa don");
        }
        if (chiTietHoaDon.getHoaDon() != null && phieuDoiTra.getHoaDon() != null
                && chiTietHoaDon.getHoaDon() != phieuDoiTra.getHoaDon())
            throw new IllegalArgumentException("Phiếu trả và dòng gốc không cùng hóa đơn.");

        if (soLuong > chiTietHoaDon.getSoLuong())
            throw new IllegalArgumentException("Số lượng trả vượt số lượng mua của dòng gốc.");
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.serialIMEI = serialIMEI;
        this.lyDo = lyDo;
        this.tinhTrangKhiDoiTra = tinhTrangKhiDoiTra;
        this.hinhThucXuLy = hinhThucXuLy;

        this.sanPham = sanPham;
        this.phieuDoiTra = phieuDoiTra;
        this.chiTietHoaDon = chiTietHoaDon;
    }

    // =========================
    // GETTER - SETTER
    // =========================

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException(
                    "So luong phai lon hon 0");
        }
        if (chiTietHoaDon != null && soLuong > chiTietHoaDon.getSoLuong())
            throw new IllegalArgumentException("Số lượng trả vượt số lượng dòng gốc.");
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        if (!Double.isFinite(donGia) || donGia <= 0) {
            throw new IllegalArgumentException("Đơn giá phải lớn hơn 0 và hữu hạn.");
        }
        this.donGia = donGia;
    }

    public String getSerialIMEI() {
        return serialIMEI;
    }

    public void setSerialIMEI(String serialIMEI) {
        this.serialIMEI = serialIMEI;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public TinhTrangKhiDoiTra getTinhTrangKhiDoiTra() {
        return tinhTrangKhiDoiTra;
    }

    public void setTinhTrangKhiDoiTra(
            TinhTrangKhiDoiTra tinhTrangKhiDoiTra) {
        this.tinhTrangKhiDoiTra = tinhTrangKhiDoiTra;
    }

    public HinhThucXuLy getHinhThucXuLy() {
        return hinhThucXuLy;
    }

    public void setHinhThucXuLy(HinhThucXuLy hinhThucXuLy) {
        this.hinhThucXuLy = hinhThucXuLy;
    }

    // =========================
    // GETTER - SETTER QUAN HỆ
    // =========================

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        if (sanPham == null) {
            throw new IllegalArgumentException(
                    "San pham khong duoc null");
        }

        if (chiTietHoaDon != null
                && chiTietHoaDon.getSanPham() != sanPham) {
            throw new IllegalArgumentException(
                    "San pham khong khop voi chi tiet hoa don");
        }

        this.sanPham = sanPham;
    }

    public PhieuDoiTra getPhieuDoiTra() {
        return phieuDoiTra;
    }

    public void setPhieuDoiTra(PhieuDoiTra phieuDoiTra) {
        if (phieuDoiTra != null && chiTietHoaDon != null
                && phieuDoiTra.getHoaDon() != null && chiTietHoaDon.getHoaDon() != null
                && phieuDoiTra.getHoaDon() != chiTietHoaDon.getHoaDon())
            throw new IllegalArgumentException("Phiếu trả và dòng gốc không cùng hóa đơn.");
        this.phieuDoiTra = phieuDoiTra;
    }

    public ChiTietHoaDon getChiTietHoaDon() {
        return chiTietHoaDon;
    }

    public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) {
        if (chiTietHoaDon == null) {
            throw new IllegalArgumentException(
                    "Chi tiet hoa don khong duoc null");
        }

        if (sanPham != null
                && chiTietHoaDon.getSanPham() != sanPham) {
            throw new IllegalArgumentException(
                    "Chi tiet hoa don khong khop voi san pham");
        }

        if (phieuDoiTra != null && phieuDoiTra.getHoaDon() != null
                && chiTietHoaDon.getHoaDon() != null
                && phieuDoiTra.getHoaDon() != chiTietHoaDon.getHoaDon())
            throw new IllegalArgumentException("Phiếu trả và dòng gốc không cùng hóa đơn.");
        if (soLuong > 0 && chiTietHoaDon.getSoLuong() > 0 && soLuong > chiTietHoaDon.getSoLuong())
            throw new IllegalArgumentException("Số lượng trả vượt số lượng mua.");
        this.chiTietHoaDon = chiTietHoaDon;
    }


    /**
     * Tien hoan theo don gia thuc tra sau khi phan bo soTienGiam tren dong goc.
     * Diem tich luy chua co cot phan bo trong Entity: BUS can xu ly khi du lieu day du.
     */
    public double getThanhTienHoan() {
        if (chiTietHoaDon == null || chiTietHoaDon.getSoLuong() <= 0)
            throw new IllegalStateException("Chưa có chi tiết hóa đơn gốc.");
        double donGiaThucTra = chiTietHoaDon.getDonGia()
                - chiTietHoaDon.getSoTienGiam() / chiTietHoaDon.getSoLuong();
        return Math.max(0, soLuong * donGiaThucTra);
    }

    // =========================
    // TOSTRING
    // =========================

    @Override
    public String toString() {
        return "ChiTietPhieuDoiTra{" +
                "soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", serialIMEI='" + serialIMEI + '\'' +
                ", lyDo='" + lyDo + '\'' +
                ", tinhTrangKhiDoiTra=" + tinhTrangKhiDoiTra +
                ", hinhThucXuLy=" + hinhThucXuLy +
                ", sanPham=" +
                (sanPham != null ? sanPham.getMaSP() : "null") +
                ", phieuDoiTra=" +
                (phieuDoiTra != null
                        ? phieuDoiTra.getMaPhieuDoiTra()
                        : "null") +
                '}';
    }
}