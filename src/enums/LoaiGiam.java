package enums;

/** Enum bổ sung theo Entity của nhóm; tên hằng theo quy ước Java. */
public enum LoaiGiam {
    PHAN_TRAM("Phần trăm"),
    SO_TIEN("Số tiền");

    private final String nhan;
    LoaiGiam(String nhan) { this.nhan = nhan; }
    public String getNhan() { return nhan; }
    @Override public String toString() { return nhan; }

    /** Nhận tên enum, tên PascalCase cũ hoặc nhãn để dùng với các trường String hiện có. */
    public static LoaiGiam tuChuoi(String text) {
        if (text != null) {
            String key = text.trim().replace("_", "").replace(" ", "");
            for (LoaiGiam value : values()) {
                if (value.name().replace("_", "").equalsIgnoreCase(key)
                        || value.nhan.equalsIgnoreCase(text.trim())) return value;
            }
        }
        throw new IllegalArgumentException("Giá trị LoaiGiam không hợp lệ: " + text);
    }
}
