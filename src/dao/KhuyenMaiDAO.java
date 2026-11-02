package dao;

import database.DatabaseConnection;
import entity.KhuyenMai;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** DAO thao tác bảng dbo.KhuyenMai trong SQL Server. */
public class KhuyenMaiDAO {
    private static final String COLUMNS = "maKM, tenKM, phanTramGiam, ngayBatDau, ngayKetThuc, moTaKM, loaiGiam, soTienGiam";

    private KhuyenMai map(ResultSet rs) throws SQLException {
        return new KhuyenMai(rs.getString("maKM"), rs.getString("tenKM"),
                rs.getDouble("phanTramGiam"), rs.getTimestamp("ngayBatDau").toLocalDateTime(),
                rs.getTimestamp("ngayKetThuc").toLocalDateTime(), rs.getString("moTaKM"),
                rs.getString("loaiGiam"), rs.getDouble("soTienGiam"));
    }

    private void validate(KhuyenMai km) {
        if (km == null || km.getMaKM() == null || km.getMaKM().trim().isEmpty() || km.getMaKM().length() > 20)
            throw new IllegalArgumentException("Mã khuyến mãi phải có 1–20 ký tự.");
        if (km.getTenKM() == null || km.getTenKM().trim().isEmpty() || km.getTenKM().length() > 150)
            throw new IllegalArgumentException("Tên khuyến mãi phải có 1–150 ký tự.");
        if (km.getMoTaKM() != null && km.getMoTaKM().length() > 500)
            throw new IllegalArgumentException("Mô tả tối đa 500 ký tự.");
        if (km.getNgayBatDau() == null || km.getNgayKetThuc() == null || km.getNgayBatDau().isAfter(km.getNgayKetThuc()))
            throw new IllegalArgumentException("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc.");
        if (!"PHAN_TRAM".equals(km.getLoaiGiam()) && !"SO_TIEN".equals(km.getLoaiGiam()))
            throw new IllegalArgumentException("Loại giảm phải là PHAN_TRAM hoặc SO_TIEN.");
        if (!Double.isFinite(km.getPhanTramGiam()) || !Double.isFinite(km.getSoTienGiam())
                || km.getPhanTramGiam() < 0 || km.getPhanTramGiam() > 100
                || km.getSoTienGiam() < 0)
            throw new IllegalArgumentException("Giá trị giảm không hợp lệ.");
        if (("PHAN_TRAM".equals(km.getLoaiGiam()) && km.getSoTienGiam() != 0)
                || ("SO_TIEN".equals(km.getLoaiGiam()) && km.getPhanTramGiam() != 0))
            throw new IllegalArgumentException("Chỉ nhập giá trị tương ứng với loại giảm.");
        // SQL Server: DECIMAL(5,2) và DECIMAL(18,2).
        if (BigDecimal.valueOf(km.getSoTienGiam()).setScale(2, java.math.RoundingMode.HALF_UP).precision() > 18)
            throw new IllegalArgumentException("Số tiền giảm vượt quá DECIMAL(18,2).");
    }

    private void bind(PreparedStatement ps, KhuyenMai km, boolean includeId) throws SQLException {
        int i = 1;
        if (includeId) ps.setString(i++, km.getMaKM());
        ps.setNString(i++, km.getTenKM());
        ps.setBigDecimal(i++, BigDecimal.valueOf(km.getPhanTramGiam()).setScale(2, java.math.RoundingMode.HALF_UP));
        ps.setTimestamp(i++, Timestamp.valueOf(km.getNgayBatDau()));
        ps.setTimestamp(i++, Timestamp.valueOf(km.getNgayKetThuc()));
        ps.setNString(i++, km.getMoTaKM());
        ps.setString(i++, km.getLoaiGiam());
        ps.setBigDecimal(i++, BigDecimal.valueOf(km.getSoTienGiam()).setScale(2, java.math.RoundingMode.HALF_UP));
    }

    public List<KhuyenMai> getAll() throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM dbo.KhuyenMai ORDER BY maKM";
        List<KhuyenMai> result = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(map(rs));
        }
        return result;
    }

    public KhuyenMai findById(String maKM) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM dbo.KhuyenMai WHERE maKM = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public boolean insert(KhuyenMai km) throws SQLException {
        validate(km);
        String sql = "INSERT INTO dbo.KhuyenMai (" + COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, km, true);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean update(KhuyenMai km) throws SQLException {
        validate(km);
        String sql = "UPDATE dbo.KhuyenMai SET tenKM=?, phanTramGiam=?, ngayBatDau=?, ngayKetThuc=?, moTaKM=?, loaiGiam=?, soTienGiam=? WHERE maKM=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, km, false);
            ps.setString(8, km.getMaKM());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean delete(String maKM) throws SQLException {
        String sql = "DELETE FROM dbo.KhuyenMai WHERE maKM=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKM);
            return ps.executeUpdate() == 1;
        }
    }

    public List<KhuyenMai> search(String keyword) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM dbo.KhuyenMai WHERE maKM LIKE ? OR tenKM LIKE ? ORDER BY maKM";
        List<KhuyenMai> result = new ArrayList<>();
        String pattern = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setNString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(map(rs));
            }
        }
        return result;
    }

    public int countAll() throws SQLException {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM dbo.KhuyenMai");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /** Trạng thái theo thời điểm hiện tại của SQL Server. */
    public int countByStatus(String status) throws SQLException {
        String condition;
        switch (status) {
            case "Sắp diễn ra": condition = "SYSDATETIME() < ngayBatDau"; break;
            case "Đang diễn ra": condition = "SYSDATETIME() >= ngayBatDau AND SYSDATETIME() <= ngayKetThuc"; break;
            case "Đã kết thúc": condition = "SYSDATETIME() > ngayKetThuc"; break;
            default: throw new IllegalArgumentException("Trạng thái không hợp lệ.");
        }
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM dbo.KhuyenMai WHERE " + condition);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
