package enums;

/** Enum bổ sung theo Entity của nhóm; tên hằng theo quy ước Java. */
public enum TinhTrangSP {
    DANG_BAN("Đang bán"),
    HET_HANG("Hết hàng"),
    NGUNG_BAN("Ngừng bán");

    private final String nhan;
    TinhTrangSP(String nhan) { this.nhan = nhan; }
    public String getNhan() { return nhan; }
    @Override public String toString() { return nhan; }

    /** Nhận tên enum, tên PascalCase cũ hoặc nhãn để dùng với các trường String hiện có. */
    public static TinhTrangSP tuChuoi(String text) {
        if (text != null) {
            String key = text.trim().replace("_", "").replace(" ", "");
            for (TinhTrangSP value : values()) {
                if (value.name().replace("_", "").equalsIgnoreCase(key)
                        || value.nhan.equalsIgnoreCase(text.trim())) return value;
            }
        }
        throw new IllegalArgumentException("Giá trị TinhTrangSP không hợp lệ: " + text);
    }
}
