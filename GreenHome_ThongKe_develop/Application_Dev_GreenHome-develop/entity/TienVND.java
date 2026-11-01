package entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Giữ API double của Entity nhóm, nhưng tính tiền bằng long theo đồng nguyên. */
public final class TienVND {
    private TienVND() { }
    public static long tuDouble(double value) {
        if (!Double.isFinite(value) || value < 0 || value > 1_000_000_000_000L)
            throw new IllegalArgumentException("Số tiền không hợp lệ.");
        try { return BigDecimal.valueOf(value).longValueExact(); }
        catch (ArithmeticException ex) { throw new IllegalArgumentException("Tiền VND phải là số đồng nguyên."); }
    }
    public static long giamPhanTram(long tien, double phanTram) {
        if (!Double.isFinite(phanTram) || phanTram <= 0 || phanTram > 100)
            throw new IllegalArgumentException("Phần trăm giảm phải lớn hơn 0 và không quá 100.");
        return BigDecimal.valueOf(tien).multiply(BigDecimal.valueOf(phanTram))
            .divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN).longValueExact();
    }
}
