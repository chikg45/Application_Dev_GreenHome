package enums;

/** Bay danh muc san pham theo Bang 4.3 trong bao cao Green Home. */
public enum DanhMucSP {
    DIEN_THOAI("Điện thoại"),
    LAPTOP("Laptop"),
    TIVI("Tivi"),
    TU_LANH("Tủ lạnh"),
    MAY_GIAT("Máy giặt"),
    MAY_LANH("Máy lạnh"),
    PHU_KIEN_DIEN_TU("Phụ kiện điện tử");

    private final String tenHienThi;

    DanhMucSP(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }

    @Override
    public String toString() {
        return tenHienThi;
    }

    /** Su dung khi DAO doc ten danh muc tu bang DanhMuc. */
    public static DanhMucSP tuTen(String ten) {
        if (ten != null) {
            for (DanhMucSP dm : values()) {
                if (dm.tenHienThi.equalsIgnoreCase(ten.trim())
                        || dm.name().equalsIgnoreCase(ten.trim())) {
                    return dm;
                }
            }
        }
        throw new IllegalArgumentException("Danh mục không thuộc 7 nhóm trong đặc tả: " + ten);
    }
}
