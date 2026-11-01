package enums;

/** Enum bổ sung theo Entity của nhóm; tên hằng theo quy ước Java. */
public enum HinhThucXuLy {
    DOI_SAN_PHAM_TUONG_DUONG("Đổi sản phẩm tương đương"),
    HOAN_TIEN("Hoàn tiền 100%");

    private final String nhan;
    HinhThucXuLy(String nhan) { this.nhan = nhan; }
    public String getNhan() { return nhan; }
    @Override public String toString() { return nhan; }

    /** Nhận tên enum, tên PascalCase cũ hoặc nhãn để dùng với các trường String hiện có. */
    public static HinhThucXuLy tuChuoi(String text) {
        if (text != null) {
            String key = text.trim().replace("_", "").replace(" ", "");
            for (HinhThucXuLy value : values()) {
                if (value.name().replace("_", "").equalsIgnoreCase(key)
                        || value.nhan.equalsIgnoreCase(text.trim())) return value;
            }
        }
        throw new IllegalArgumentException("Giá trị HinhThucXuLy không hợp lệ: " + text);
    }
}
