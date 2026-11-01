package enums;

/** Enum bổ sung theo Entity của nhóm; tên hằng theo quy ước Java. */
public enum PhuongThucThanhToan {
    TIEN_MAT("Tiền mặt"),
    CHUYEN_KHOAN("Chuyển khoản");

    private final String nhan;
    PhuongThucThanhToan(String nhan) { this.nhan = nhan; }
    public String getNhan() { return nhan; }
    @Override public String toString() { return nhan; }

    /** Nhận tên enum, tên PascalCase cũ hoặc nhãn để dùng với các trường String hiện có. */
    public static PhuongThucThanhToan tuChuoi(String text) {
        if (text != null) {
            String key = text.trim().replace("_", "").replace(" ", "");
            for (PhuongThucThanhToan value : values()) {
                if (value.name().replace("_", "").equalsIgnoreCase(key)
                        || value.nhan.equalsIgnoreCase(text.trim())) return value;
            }
        }
        throw new IllegalArgumentException("Giá trị PhuongThucThanhToan không hợp lệ: " + text);
    }
}
