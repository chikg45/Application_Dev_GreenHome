package enums;

/** Enum bổ sung theo Entity của nhóm; tên hằng theo quy ước Java. */
public enum TrangThaiDuyet {
    CHO_DUYET("Chờ duyệt"),
    DA_DUYET("Đã duyệt"),
    TU_CHOI("Từ chối"),
    HOAN_TAT("Hoàn tất");

    private final String nhan;
    TrangThaiDuyet(String nhan) { this.nhan = nhan; }
    public String getNhan() { return nhan; }
    @Override public String toString() { return nhan; }

    /** Nhận tên enum, tên PascalCase cũ hoặc nhãn để dùng với các trường String hiện có. */
    public static TrangThaiDuyet tuChuoi(String text) {
        if (text != null) {
            String key = text.trim().replace("_", "").replace(" ", "");
            for (TrangThaiDuyet value : values()) {
                if (value.name().replace("_", "").equalsIgnoreCase(key)
                        || value.nhan.equalsIgnoreCase(text.trim())) return value;
            }
        }
        throw new IllegalArgumentException("Giá trị TrangThaiDuyet không hợp lệ: " + text);
    }
}
