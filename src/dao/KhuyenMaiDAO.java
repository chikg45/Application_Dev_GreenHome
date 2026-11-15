package dao;

import database.DatabaseConnection;
import entity.KhuyenMai;
import entity.LoaiGiam;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Chỉ thực hiện truy vấn và ánh xạ dữ liệu, không xử lý nghiệp vụ. */
public class KhuyenMaiDAO {
    private static final String COLS = "maKM, tenKM, moTaKM, loaiGiam, phanTramGiam, soTienGiam, ngayBatDau, ngayKetThuc";

    private KhuyenMai map(ResultSet rs) throws SQLException {
        return new KhuyenMai(
            rs.getString("maKM"), rs.getString("tenKM"), rs.getDouble("phanTramGiam"),
            rs.getTimestamp("ngayBatDau").toLocalDateTime(),
            rs.getTimestamp("ngayKetThuc").toLocalDateTime(),
            rs.getString("moTaKM"), rs.getString("loaiGiam").trim().toUpperCase(java.util.Locale.ROOT),
            rs.getDouble("soTienGiam"));
    }

    private void bindData(PreparedStatement ps, KhuyenMai km, int offset) throws SQLException {
        ps.setNString(offset, km.getTenKM());
        ps.setNString(offset + 1, km.getMoTaKM());
        ps.setString(offset + 2, km.getLoaiGiam());
        ps.setBigDecimal(offset + 3, BigDecimal.valueOf(km.getPhanTramGiam()));
        ps.setBigDecimal(offset + 4, BigDecimal.valueOf(km.getSoTienGiam()));
        ps.setTimestamp(offset + 5, Timestamp.valueOf(km.getNgayBatDau()));
        ps.setTimestamp(offset + 6, Timestamp.valueOf(km.getNgayKetThuc()));
    }
    
    public boolean daDuocSuDung(String maKM) throws SQLException {
        String sql = "SELECT 1 FROM dbo.ChiTietHoaDon c "
                   + "JOIN dbo.SanPhamKhuyenMai sk "
                   + "ON c.maSP = sk.maSP AND c.maKM = sk.maKM "
                   + "WHERE sk.maKM = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKM);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<KhuyenMai> getAll() throws SQLException {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM dbo.KhuyenMai ORDER BY maKM";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public KhuyenMai findById(String maKM) throws SQLException {
        String sql = "SELECT " + COLS + " FROM dbo.KhuyenMai WHERE maKM = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public boolean existsById(String maKM) throws SQLException {
        String sql = "SELECT 1 FROM dbo.KhuyenMai WHERE maKM = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public boolean insert(KhuyenMai km) throws SQLException {
        String sql = "INSERT INTO dbo.KhuyenMai (" + COLS + ") VALUES (?,?,?,?,?,?,?,?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, km.getMaKM());
            bindData(ps, km, 2);
            return ps.executeUpdate() == 1;
        }
    }


    /** Danh sách sản phẩm để hiển thị trong hộp chọn. */
    /** Chỉ truy vấn sản phẩm theo mã, tên và danh mục; dùng PreparedStatement. */
    public java.util.LinkedHashMap<String, String> timSanPham(String tuKhoa, String maDM) throws SQLException {
        java.util.LinkedHashMap<String, String> result = new java.util.LinkedHashMap<>();
        String sql = "SELECT maSP, tenSP FROM dbo.SanPham WHERE (maSP LIKE ? OR tenSP LIKE ?) "
                   + "AND (? IS NULL OR maDM = ?) ORDER BY tenSP, maSP";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            String pattern = "%" + tuKhoa + "%";
            ps.setString(1, pattern); ps.setNString(2, pattern);
            ps.setString(3, maDM); ps.setString(4, maDM);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.put(rs.getString(1), rs.getString(2));
            }
        }
        return result;
    }

    public java.util.LinkedHashMap<String, String> layDanhMucSanPham() throws SQLException {
        java.util.LinkedHashMap<String, String> result = new java.util.LinkedHashMap<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT maDM, tenDM FROM dbo.DanhMuc ORDER BY tenDM");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.put(rs.getString(1), rs.getString(2));
        }
        return result;
    }

    public java.util.LinkedHashMap<String, String> laySanPham() throws SQLException {
        java.util.LinkedHashMap<String, String> result = new java.util.LinkedHashMap<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT maSP, tenSP FROM dbo.SanPham ORDER BY tenSP, maSP");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.put(rs.getString(1), rs.getString(2));
        }
        return result;
    }

    public List<String> layMaSanPhamApDung(String maKM) throws SQLException {
        List<String> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT maSP FROM dbo.SanPhamKhuyenMai WHERE maKM=? ORDER BY maSP")) {
            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(rs.getString(1));
            }
        }
        return result;
    }

    /** Tên và mã sản phẩm áp dụng để UI hiển thị trong Xem chi tiết. */
    public java.util.LinkedHashMap<String, String> laySanPhamApDung(String maKM) throws SQLException {
        java.util.LinkedHashMap<String, String> result = new java.util.LinkedHashMap<>();
        String sql = "SELECT s.maSP, s.tenSP FROM dbo.SanPham s "
                   + "JOIN dbo.SanPhamKhuyenMai sk ON sk.maSP=s.maSP "
                   + "WHERE sk.maKM=? ORDER BY s.tenSP,s.maSP";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.put(rs.getString(1), rs.getString(2));
            }
        }
        return result;
    }

    /** Đồng bộ đúng các sản phẩm được chọn, giữ lịch sử hóa đơn. */
    private void luuLienKet(Connection c, String maKM, List<String> maSPs) throws SQLException {
        if (maSPs == null) throw new IllegalArgumentException("Danh sách sản phẩm không được null");
        java.util.Set<String> selected = new java.util.LinkedHashSet<>();
        for (String maSP : maSPs) {
            if (maSP == null || maSP.trim().isEmpty())
                throw new IllegalArgumentException("Mã sản phẩm không hợp lệ");
            selected.add(maSP.trim());
        }

        java.util.Set<String> existing = new java.util.LinkedHashSet<>();
        String query = "SELECT maSP FROM dbo.SanPhamKhuyenMai WITH (UPDLOCK, HOLDLOCK) WHERE maKM=?";
        try (PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) existing.add(rs.getString(1));
            }
        }

        String check = "SELECT 1 FROM dbo.ChiTietHoaDon WHERE maKM=? AND maSP=?";
        String remove = "DELETE FROM dbo.SanPhamKhuyenMai WHERE maKM=? AND maSP=?";
        for (String maSP : existing) {
            if (selected.contains(maSP)) continue;
            try (PreparedStatement ps = c.prepareStatement(check)) {
                ps.setString(1, maKM);
                ps.setString(2, maSP);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) throw new IllegalStateException(
                        "Không thể bỏ sản phẩm " + maSP + " khỏi khuyến mãi " + maKM
                        + " vì đã có hóa đơn sử dụng.");
                }
            }
            try (PreparedStatement ps = c.prepareStatement(remove)) {
                ps.setString(1, maKM);
                ps.setString(2, maSP);
                ps.executeUpdate();
            }
        }

        String add = "INSERT INTO dbo.SanPhamKhuyenMai(maSP,maKM) VALUES (?,?)";
        try (PreparedStatement ps = c.prepareStatement(add)) {
            for (String maSP : selected) {
                if (existing.contains(maSP)) continue;
                ps.setString(1, maSP);
                ps.setString(2, maKM);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public boolean updateWithProducts(KhuyenMai km, List<String> maSPs) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            c.setAutoCommit(false);
            try {
                int count;
                try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE dbo.KhuyenMai SET tenKM=?,moTaKM=?,loaiGiam=?,phanTramGiam=?,soTienGiam=?,ngayBatDau=?,ngayKetThuc=? WHERE maKM=?")) {
                    bindData(ps, km, 1); ps.setString(8, km.getMaKM()); count=ps.executeUpdate();
                }
                if (count==1) luuLienKet(c, km.getMaKM(), maSPs);
                c.commit(); return count==1;
            } catch (SQLException | RuntimeException ex) {
                try { c.rollback(); } catch (SQLException rollback) { ex.addSuppressed(rollback); }
                throw ex;
            }
        }
    }

    /** Mã mới được cấp trong giao dịch có khóa bảng để tránh trùng khi nhiều máy cùng thêm. */
    public KhuyenMai insertAuto(KhuyenMai draft) throws SQLException {
        return insertAuto(draft, java.util.Collections.emptyList());
    }

    public KhuyenMai insertAuto(KhuyenMai draft, List<String> maSPs) throws SQLException {
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);
            con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            try {
                long next;
                // TABLOCKX giữ độc quyền bảng đến hết transaction; tránh 2 máy cùng cấp một mã.
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT ISNULL(MAX(CASE WHEN LEN(maKM) BETWEEN 5 AND 10 "
                      + "AND maKM LIKE 'KM%' AND SUBSTRING(maKM,3,8) NOT LIKE '%[^0-9]%' "
                      + "THEN TRY_CONVERT(BIGINT,SUBSTRING(maKM,3,8)) ELSE 0 END),0) "
                      + "FROM dbo.KhuyenMai WITH (TABLOCKX, HOLDLOCK)" );
                     ResultSet rs = ps.executeQuery()) {
                    rs.next(); next = rs.getLong(1) + 1;
                }
                if (next > 99999999L) throw new SQLException("Đã hết dải mã khuyến mãi 8 chữ số");
                String code = String.format(java.util.Locale.ROOT, "KM%08d", next);
                KhuyenMai km = new KhuyenMai(code, draft.getTenKM(), draft.getPhanTramGiam(),
                    draft.getNgayBatDau(), draft.getNgayKetThuc(), draft.getMoTaKM(),
                    draft.getLoaiGiam(), draft.getSoTienGiam());
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO dbo.KhuyenMai (" + COLS + ") VALUES (?,?,?,?,?,?,?,?)")) {
                    ps.setString(1, code); bindData(ps, km, 2); ps.executeUpdate();
                }
                luuLienKet(con, code, maSPs);
                con.commit(); return km;
            } catch (SQLException | RuntimeException ex) {
                try { con.rollback(); } catch (SQLException r) { ex.addSuppressed(r); }
                throw ex;
            }
        }
    }

    /** Chỉ để hiển thị mã dự kiến; mã thực tế được cấp lại khi INSERT. */
    public String previewNextId() throws SQLException {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                "SELECT ISNULL(MAX(CASE WHEN LEN(maKM) BETWEEN 5 AND 10 "
              + "AND maKM LIKE 'KM%' AND SUBSTRING(maKM,3,8) NOT LIKE '%[^0-9]%' "
              + "THEN TRY_CONVERT(BIGINT,SUBSTRING(maKM,3,8)) ELSE 0 END),0) FROM dbo.KhuyenMai");
             ResultSet rs = ps.executeQuery()) {
            rs.next(); long id = rs.getLong(1) + 1;
            if (id > 99999999L) throw new SQLException("Đã hết dải mã khuyến mãi");
            return String.format(java.util.Locale.ROOT, "KM%08d", id);
        }
    }

    public boolean update(KhuyenMai km) throws SQLException {
        String sql = "UPDATE dbo.KhuyenMai SET tenKM=?,moTaKM=?,loaiGiam=?,phanTramGiam=?,soTienGiam=?,ngayBatDau=?,ngayKetThuc=? WHERE maKM=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bindData(ps, km, 1);
            ps.setString(8, km.getMaKM());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean delete(String maKM) throws SQLException {
        String checkSql =
            "SELECT 1 FROM dbo.ChiTietHoaDon WHERE maKM = ?";

        String deleteLinkSql =
            "DELETE FROM dbo.SanPhamKhuyenMai WHERE maKM = ?";

        String deleteKmSql =
            "DELETE FROM dbo.KhuyenMai WHERE maKM = ?";

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                // Kiểm tra khuyến mãi đã xuất hiện trong hóa đơn chưa
                try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                    ps.setString(1, maKM);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            throw new IllegalStateException(
                                "Không thể xóa khuyến mãi đã được sử dụng trong hóa đơn."
                            );
                        }
                    }
                }

                // Xóa liên kết sản phẩm - khuyến mãi trước
                try (PreparedStatement ps = con.prepareStatement(deleteLinkSql)) {
                    ps.setString(1, maKM);
                    ps.executeUpdate();
                }

                // Xóa khuyến mãi
                int rows;
                try (PreparedStatement ps = con.prepareStatement(deleteKmSql)) {
                    ps.setString(1, maKM);
                    rows = ps.executeUpdate();
                }

                con.commit();
                return rows == 1;

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public List<KhuyenMai> search(String keyword) throws SQLException {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM dbo.KhuyenMai WHERE maKM LIKE ? OR tenKM LIKE ? ORDER BY maKM";
        String pattern = "%" + keyword + "%";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setNString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public int countAll() throws SQLException {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM dbo.KhuyenMai");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
