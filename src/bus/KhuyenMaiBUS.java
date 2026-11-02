package bus;

import dao.KhuyenMaiDAO;
import entity.KhuyenMai;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Xu ly nghiep vu khuyen mai; khong phu thuoc Swing. */
public class KhuyenMaiBUS {
    private final KhuyenMaiDAO dao;

    public KhuyenMaiBUS() {
        this(new KhuyenMaiDAO());
    }

    public KhuyenMaiBUS(KhuyenMaiDAO dao) {
        if (dao == null) throw new IllegalArgumentException("DAO không được null.");
        this.dao = dao;
    }

    public List<KhuyenMai> getAll() throws SQLException {
        return dao.getAll();
    }

    public KhuyenMai findById(String maKM) throws SQLException {
        kiemTraMa(maKM);
        return dao.findById(maKM.trim());
    }

    public boolean insert(KhuyenMai km) throws SQLException {
        validate(km);
        if (dao.findById(km.getMaKM().trim()) != null) {
            throw new IllegalArgumentException("Mã khuyến mãi đã tồn tại.");
        }
        return dao.insert(km);
    }

    public boolean update(KhuyenMai km) throws SQLException {
        validate(km);
        if (dao.findById(km.getMaKM().trim()) == null) {
            throw new IllegalArgumentException("Không tìm thấy mã khuyến mãi cần sửa.");
        }
        return dao.update(km);
    }

    public boolean delete(String maKM) throws SQLException {
        kiemTraMa(maKM);
        if (dao.findById(maKM.trim()) == null) {
            throw new IllegalArgumentException("Không tìm thấy mã khuyến mãi cần xóa.");
        }
        // SQL Server se chan xoa neu co rang buoc khoa ngoai.
        return dao.delete(maKM.trim());
    }

    public List<KhuyenMai> search(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) return dao.getAll();
        return dao.search(keyword.trim());
    }

    public List<KhuyenMai> filter(String keyword, String status) throws SQLException {
        List<KhuyenMai> items = search(keyword);
        if (status == null || status.trim().isEmpty()
                || "Tất cả trạng thái".equals(status)) return items;
        kiemTraTrangThai(status);
        List<KhuyenMai> result = new ArrayList<>();
        for (KhuyenMai km : items) {
            if (status.equals(km.getTrangThai())) result.add(km);
        }
        return result;
    }

    public int countAll() throws SQLException {
        return dao.countAll();
    }

    public int countByStatus(String status) throws SQLException {
        kiemTraTrangThai(status);
        return dao.countByStatus(status);
    }

    private void kiemTraMa(String maKM) {
        if (maKM == null || maKM.trim().isEmpty())
            throw new IllegalArgumentException("Mã khuyến mãi không được để trống.");
        if (maKM.trim().length() > 20)
            throw new IllegalArgumentException("Mã khuyến mãi tối đa 20 ký tự.");
    }

    private void kiemTraTrangThai(String status) {
        if (!"Đang diễn ra".equals(status) && !"Sắp diễn ra".equals(status)
                && !"Đã kết thúc".equals(status)) {
            throw new IllegalArgumentException("Trạng thái khuyến mãi không hợp lệ.");
        }
    }

    private void validate(KhuyenMai km) {
        if (km == null) throw new IllegalArgumentException("Khuyến mãi không được null.");
        kiemTraMa(km.getMaKM());
        if (km.getTenKM() == null || km.getTenKM().trim().isEmpty())
            throw new IllegalArgumentException("Tên khuyến mãi không được để trống.");
        if (km.getTenKM().length() > 150)
            throw new IllegalArgumentException("Tên khuyến mãi tối đa 150 ký tự.");
        if (km.getMoTaKM() != null && km.getMoTaKM().length() > 500)
            throw new IllegalArgumentException("Mô tả tối đa 500 ký tự.");
        LocalDateTime start = km.getNgayBatDau();
        LocalDateTime end = km.getNgayKetThuc();
        if (start == null || end == null)
            throw new IllegalArgumentException("Ngày bắt đầu và kết thúc không được trống.");
        if (start.isAfter(end))
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc.");
        double percent = km.getPhanTramGiam();
        double amount = km.getSoTienGiam();
        if (!Double.isFinite(percent) || percent < 0 || percent > 100)
            throw new IllegalArgumentException("Phần trăm giảm phải từ 0 đến 100.");
        if (!Double.isFinite(amount) || amount < 0 || amount > 9999999999999999.0)
            throw new IllegalArgumentException("Số tiền giảm không hợp lệ.");
        String type = km.getLoaiGiam();
        if (!"PHAN_TRAM".equals(type) && !"SO_TIEN".equals(type))
            throw new IllegalArgumentException("Loại giảm phải là PHAN_TRAM hoặc SO_TIEN.");
        if ("PHAN_TRAM".equals(type) && amount != 0)
            throw new IllegalArgumentException("Giảm phần trăm thì số tiền giảm phải bằng 0.");
        if ("SO_TIEN".equals(type) && percent != 0)
            throw new IllegalArgumentException("Giảm số tiền thì phần trăm giảm phải bằng 0.");
    }
}
