package enums;

/** Ten gia tri theo Bang 4.3: DangBan, HetHang, NgungBan. */
public enum TinhTrangSP {
    DangBan("Đang bán", "DANG_KINH_DOANH"),
    HetHang("Hết hàng", "HET_HANG"),
    NgungBan("Ngừng bán", "NGUNG_KINH_DOANH");

    private final String tenHienThi;
    private final String giaTriCSDL;

    TinhTrangSP(String tenHienThi, String giaTriCSDL) {
        this.tenHienThi = tenHienThi;
        this.giaTriCSDL = giaTriCSDL;
    }

    public String getGiaTriCSDL() {
        return giaTriCSDL;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }

    /** Chuyen gia tri VARCHAR trong SQL thanh enum cua Class Diagram. */
    public static TinhTrangSP tuCSDL(String giaTri) {
        if (giaTri != null) {
            for (TinhTrangSP tt : values()) {
                if (tt.giaTriCSDL.equalsIgnoreCase(giaTri.trim())
                        || tt.name().equalsIgnoreCase(giaTri.trim())) {
                    return tt;
                }
            }
        }
        throw new IllegalArgumentException("Tình trạng sản phẩm không hợp lệ: " + giaTri);
    }

    @Override
    public String toString() {
        return tenHienThi;
    }
}
