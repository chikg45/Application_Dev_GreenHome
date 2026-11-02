package bus;

import dao.KhuyenMaiDAO;
import entity.KhuyenMai;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Kiểm tra nghiệp vụ, phối hợp DAO; không chứa JDBC hay Swing. */
public class KhuyenMaiBUS {
    private final KhuyenMaiDAO dao;

    public KhuyenMaiBUS() { this(new KhuyenMaiDAO()); }
    public KhuyenMaiBUS(KhuyenMaiDAO dao) { this.dao = Objects.requireNonNull(dao); }

    public List<KhuyenMai> getAll() throws SQLException { return dao.getAll(); }

    public KhuyenMai findById(String maKM) throws SQLException {
        validateMa(maKM);
        return dao.findById(maKM);
    }

    public String previewNextId() throws SQLException { return dao.previewNextId(); }

    public java.util.LinkedHashMap<String,String> laySanPham() throws SQLException { return dao.laySanPham(); }
    public java.util.LinkedHashMap<String, String> timSanPham(String tuKhoa, String maDM) throws SQLException {
        String key = tuKhoa == null ? "" : tuKhoa.trim();
        if (key.length() > 100) throw new IllegalArgumentException("Từ khóa quá dài");
        return dao.timSanPham(key, maDM == null || maDM.isEmpty() ? null : maDM);
    }

    public java.util.LinkedHashMap<String, String> layDanhMucSanPham() throws SQLException {
        return dao.layDanhMucSanPham();
    }


    public List<String> layMaSanPhamApDung(String maKM) throws SQLException { return dao.layMaSanPhamApDung(maKM); }

    public java.util.LinkedHashMap<String, String> laySanPhamApDung(String maKM) throws SQLException {
        validateMa(maKM);
        return dao.laySanPhamApDung(maKM);
    }

    public KhuyenMai themTuDong(KhuyenMai draft, List<String> maSPs) throws SQLException {
        validate(draft);
        return dao.insertAuto(draft, maSPs);
    }

    public boolean update(KhuyenMai km, List<String> maSPs) throws SQLException {
        validate(km);
        return dao.updateWithProducts(km, maSPs);
    }

    public KhuyenMai themTuDong(KhuyenMai draft) throws SQLException {
        validate(draft);
        return dao.insertAuto(draft);
    }

    public String trangThai(KhuyenMai km) {
        return statusAt(km, LocalDateTime.now());
    }

    public boolean insert(KhuyenMai km) throws SQLException {
        validate(km);
        if (dao.existsById(km.getMaKM()))
            throw new IllegalArgumentException("Mã khuyến mãi đã tồn tại");
        return dao.insert(km);
    }

    public boolean update(KhuyenMai km) throws SQLException {
        validate(km);
        if (!dao.existsById(km.getMaKM()))
            throw new IllegalArgumentException("Không tìm thấy khuyến mãi cần sửa");
        return dao.update(km);
    }

    public boolean delete(String maKM) throws SQLException {
        if (dao.daDuocSuDung(maKM)) {
            throw new IllegalStateException(
                "Không thể xóa khuyến mãi đã được sử dụng trong hóa đơn."
            );
        }

        return dao.delete(maKM);
    }

    public List<KhuyenMai> search(String keyword) throws SQLException {
        return keyword == null || keyword.trim().isEmpty()
            ? dao.getAll() : dao.search(keyword.trim());
    }

    public int countAll() throws SQLException { return dao.countAll(); }

    public List<KhuyenMai> filter(String keyword, String status) throws SQLException {
        List<KhuyenMai> list = search(keyword);
        if (status == null || status.trim().isEmpty() || "Tất cả trạng thái".equals(status))
            return list;
        validateStatus(status);
        List<KhuyenMai> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (KhuyenMai km : list) {
            if (status.equals(statusAt(km, now))) result.add(km);
        }
        return result;
    }

    public int countByStatus(String status) throws SQLException {
        validateStatus(status);
        return filter(null, status).size();
    }

    /** Áp dụng khuyến mãi cho tổng tiền do lớp nghiệp vụ khác tính ra. */
    public double tinhGiamGia(String maKM, double tongTienHang) throws SQLException {
        KhuyenMai km = findById(maKM);
        if (km == null) throw new IllegalArgumentException("Không tìm thấy khuyến mãi");
        if (!km.dieuKienKM()) throw new IllegalStateException("Khuyến mãi không còn hiệu lực");
        return km.tinhTienGiam(tongTienHang);
    }

    private String statusAt(KhuyenMai km, LocalDateTime now) {
        if (now.isBefore(km.getNgayBatDau())) return "Sắp diễn ra";
        if (now.isAfter(km.getNgayKetThuc())) return "Đã kết thúc";
        if (!now.plusDays(3).isBefore(km.getNgayKetThuc())) return "Sắp kết thúc";
        return "Đang diễn ra";
    }

    private void validateStatus(String status) {
        if (!"Sắp diễn ra".equals(status) && !"Đang diễn ra".equals(status)
                && !"Đã kết thúc".equals(status) && !"Sắp kết thúc".equals(status))
            throw new IllegalArgumentException("Trạng thái khuyến mãi không hợp lệ");
    }

    private void validateMa(String maKM) {
        if (maKM == null || !maKM.matches("KM\\d{3,8}"))
            throw new IllegalArgumentException("Mã KM phải có 8 chữ số; mã mới tự sinh đủ 8 chữ số");
    }

    private void validate(KhuyenMai km) {
        if (km == null) throw new IllegalArgumentException("Khuyến mãi không được null");
        validateMa(km.getMaKM());
        if (km.getTenKM() == null || km.getTenKM().trim().length() < 2
                || km.getTenKM().trim().length() > 100)
            throw new IllegalArgumentException("Tên khuyến mãi phải từ 2 đến 100 ký tự");
        if (km.getMoTaKM() != null && km.getMoTaKM().length() > 500)
            throw new IllegalArgumentException("Mô tả tối đa 500 ký tự theo database");
        if (km.getLoaiGiam() == null) throw new IllegalArgumentException("Loại giảm không được null");
        if (km.getNgayBatDau() == null || km.getNgayKetThuc() == null
                || km.getNgayBatDau().isAfter(km.getNgayKetThuc()))
            throw new IllegalArgumentException("Thời gian áp dụng không hợp lệ");
        double pt = km.getPhanTramGiam();
        double st = km.getSoTienGiam();
        if (!Double.isFinite(pt) || !Double.isFinite(st) || pt < 0 || pt > 100 || st < 0)
            throw new IllegalArgumentException("Mức giảm không hợp lệ");
        if ("PHAN_TRAM".equals(km.getLoaiGiam()) && (pt <= 0 || st != 0))
            throw new IllegalArgumentException("Giảm theo phần trăm: 0 < phần trăm <= 100, tiền giảm = 0");
        if ("SO_TIEN".equals(km.getLoaiGiam()) && pt != 0)
            throw new IllegalArgumentException("Giảm theo số tiền: phần trăm giảm phải bằng 0");
        if (Math.round(pt * 100.0) != pt * 100.0)
            throw new IllegalArgumentException("Phần trăm giảm chỉ có tối đa 2 chữ số thập phân");
        if (Math.round(st * 100.0) != st * 100.0)
            throw new IllegalArgumentException("Số tiền giảm chỉ có tối đa 2 chữ số thập phân");
    }
}
