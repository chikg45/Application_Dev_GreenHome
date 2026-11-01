package enums;

/** Enum bổ sung theo Entity của nhóm; tên hằng theo quy ước Java. */
public enum DanhMuc {
    DIEN_THOAI("Điện thoại"),
    LAPTOP("Laptop"),
    TIVI("Tivi"),
    TU_LANH("Tủ lạnh"),
    MAY_GIAT("Máy giặt"),
    MAY_LANH("Máy lạnh"),
    PHU_KIEN_DIEN_TU("Phụ kiện");

    private final String nhan;
    DanhMuc(String nhan) { this.nhan = nhan; }
    public String getNhan() { return nhan; }
    @Override public String toString() { return nhan; }

    /** Nhận tên enum, tên PascalCase cũ hoặc nhãn để dùng với các trường String hiện có. */
    public static DanhMuc tuChuoi(String text) {
        if (text != null) {
            String key = text.trim().replace("_", "").replace(" ", "");
            for (DanhMuc value : values()) {
                if (value.name().replace("_", "").equalsIgnoreCase(key)
                        || value.nhan.equalsIgnoreCase(text.trim())) return value;
            }
        }
        throw new IllegalArgumentException("Giá trị DanhMuc không hợp lệ: " + text);
    }
}
