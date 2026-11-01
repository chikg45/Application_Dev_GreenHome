package enums;

/** Enum bổ sung theo Entity của nhóm; tên hằng theo quy ước Java. */
public enum TinhTrangKhiDoiTra {
    NGUYEN_TEM("Nguyên tem"),
    TRAY_XUOC("Trầy xước"),
    DAY_DU_PHU_KIEN("Đầy đủ phụ kiện");

    private final String nhan;
    TinhTrangKhiDoiTra(String nhan) { this.nhan = nhan; }
    public String getNhan() { return nhan; }
    @Override public String toString() { return nhan; }

    /** Nhận tên enum, tên PascalCase cũ hoặc nhãn để dùng với các trường String hiện có. */
    public static TinhTrangKhiDoiTra tuChuoi(String text) {
        if (text != null) {
            String key = text.trim().replace("_", "").replace(" ", "");
            for (TinhTrangKhiDoiTra value : values()) {
                if (value.name().replace("_", "").equalsIgnoreCase(key)
                        || value.nhan.equalsIgnoreCase(text.trim())) return value;
            }
        }
        throw new IllegalArgumentException("Giá trị TinhTrangKhiDoiTra không hợp lệ: " + text);
    }
}
