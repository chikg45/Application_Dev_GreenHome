package enums;

/** Enum bổ sung theo Entity của nhóm; tên hằng theo quy ước Java. */
public enum TrangThaiDonHang {
    CHUA_THANH_TOAN("Chưa thanh toán"),
    DA_THANH_TOAN("Đã thanh toán"),
    DA_HUY("Đã hủy");

    private final String nhan;
    TrangThaiDonHang(String nhan) { this.nhan = nhan; }
    public String getNhan() { return nhan; }
    @Override public String toString() { return nhan; }

    /** Nhận tên enum, tên PascalCase cũ hoặc nhãn để dùng với các trường String hiện có. */
    public static TrangThaiDonHang tuChuoi(String text) {
        if (text != null) {
            String key = text.trim().replace("_", "").replace(" ", "");
            for (TrangThaiDonHang value : values()) {
                if (value.name().replace("_", "").equalsIgnoreCase(key)
                        || value.nhan.equalsIgnoreCase(text.trim())) return value;
            }
        }
        throw new IllegalArgumentException("Giá trị TrangThaiDonHang không hợp lệ: " + text);
    }
}
