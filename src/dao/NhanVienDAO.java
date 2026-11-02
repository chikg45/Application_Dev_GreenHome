package dao;

import connectDB.Jdbc;
import connectDB.NguonKetNoi;
import database.DatabaseConnection;
import entity.NhanVien;
import entity.TaiKhoan;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/** SQL Server dbo.NhanVien + dbo.TaiKhoan; khong xoa ho so da co giao dich. */
public class NhanVienDAO {
    private final NguonKetNoi nguon;
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private static final String SELECT = "SELECT nv.maNV,nv.hoTen,nv.sdtNV,nv.soCCCD,nv.ngaySinh,"
            + "nv.ngayVaoLam,nv.diaChi,nv.dangLamViec,tk.maTK,tk.tenDangNhap,tk.matKhau,"
            + "tk.vaiTro,tk.dangHoatDong FROM dbo.NhanVien nv LEFT JOIN dbo.TaiKhoan tk ON nv.maNV=tk.maNV ";

    public NhanVienDAO() { this(DatabaseConnection::getConnection); }
    public NhanVienDAO(NguonKetNoi nguon) { this.nguon = Objects.requireNonNull(nguon); }

    public static void yeuCauQuanLy(Connection c, String maDangNhap) throws SQLException {
        if (maDangNhap == null || maDangNhap.isBlank()) throw new SecurityException("Chưa đăng nhập.");
        String sql = "SELECT 1 FROM dbo.NhanVien nv JOIN dbo.TaiKhoan tk ON nv.maNV=tk.maNV "
                + "WHERE nv.maNV=? AND nv.dangLamViec=1 AND tk.dangHoatDong=1 AND tk.vaiTro='QUAN_LY'";
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, maDangNhap);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) throw new SecurityException("Chỉ Quản lý đang hoạt động được thao tác.");
            }
        }
    }

    private static NhanVien doc(ResultSet rs) throws SQLException {
        NhanVien nv = new NhanVien();
        nv.setMaNV(rs.getString("maNV")); nv.setHoTen(rs.getString("hoTen"));
        nv.setSdtNV(rs.getString("sdtNV")); nv.setSoCCCD(rs.getString("soCCCD"));
        nv.setNgaySinh(rs.getDate("ngaySinh").toLocalDate());
        nv.setNgayVaoLam(rs.getDate("ngayVaoLam").toLocalDate());
        nv.setDiaChi(rs.getString("diaChi")); nv.setDangLamViec(rs.getBoolean("dangLamViec"));
        TaiKhoan tk = TaiKhoanDAO.doc(rs);
        if (tk != null) { nv.setTaiKhoan(tk); tk.setNhanVien(nv); }
        return nv;
    }

    public List<NhanVien> layTatCa(String maDangNhap) {
        return Jdbc.doc(nguon, c -> {
            yeuCauQuanLy(c, maDangNhap);
            List<NhanVien> list = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(SELECT + " ORDER BY nv.maNV");
                 ResultSet r = ps.executeQuery()) { while (r.next()) list.add(doc(r)); }
            return list;
        });
    }
    public NhanVien timTheoMa(String maDangNhap, String maCanXem) {
        return Jdbc.doc(nguon, c -> {
            if (!Objects.equals(maDangNhap, maCanXem)) yeuCauQuanLy(c, maDangNhap);
            try (PreparedStatement p = c.prepareStatement(SELECT + " WHERE nv.maNV=?")) {
                p.setString(1, maCanXem);
                try (ResultSet r = p.executeQuery()) { return r.next() ? doc(r) : null; }
            }
        });
    }
    /** Ho so con lam va co tai khoan hoat dong moi duoc phan cong. */
    public List<NhanVien> layNhanVienCoThePhanCong(String maQuanLy) {
        return Jdbc.doc(nguon, c -> {
            yeuCauQuanLy(c, maQuanLy);
            List<NhanVien> list = new ArrayList<>();
            try (PreparedStatement p = c.prepareStatement(SELECT
                    + " WHERE nv.dangLamViec=1 AND tk.dangHoatDong=1 ORDER BY nv.maNV");
                 ResultSet r = p.executeQuery()) { while (r.next()) list.add(doc(r)); }
            return list;
        });
    }
    /** Cap ma trong transaction voi khoa, demo NV0001..NV0005 -> NV0006. */
    public static String maMoi(Connection c, String table, String col, String prefix) throws SQLException {
        // table/column/prefix chi duoc truyen bang hang so noi bo, khong tu nguoi dung.
        String query = "SELECT MAX(TRY_CONVERT(INT, SUBSTRING(" + col + "," + (prefix.length()+1)
                + ",20))) FROM dbo." + table + " WITH (UPDLOCK,HOLDLOCK)";
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(query)) {
            rs.next(); int next = rs.getInt(1) + 1;
            if (next > 9999) throw new SQLException("Đã hết khoảng mã " + prefix + ".");
            return prefix + String.format(Locale.ROOT, "%04d", next);
        }
    }

    public NhanVien them(String maQuanLy, NhanVien nv, TaiKhoan tk) {
        return Jdbc.giaoDich(nguon, c -> {
            yeuCauQuanLy(c, maQuanLy);
            c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            String maNV = maMoi(c, "NhanVien", "maNV", "NV");
            String maTK = maMoi(c, "TaiKhoan", "maTK", "TK");
            try (PreparedStatement p = c.prepareStatement("INSERT INTO dbo.NhanVien "
                    + "(maNV,hoTen,sdtNV,soCCCD,ngaySinh,ngayVaoLam,diaChi,dangLamViec) "
                    + "VALUES(?,?,?,?,?,?,?,?)")) {
                p.setString(1, maNV); p.setString(2, nv.getHoTen()); p.setString(3, nv.getSdtNV());
                p.setString(4, nv.getSoCCCD()); p.setDate(5, java.sql.Date.valueOf(nv.getNgaySinh()));
                p.setDate(6, java.sql.Date.valueOf(nv.getNgayVaoLam())); p.setString(7, nv.getDiaChi());
                p.setBoolean(8, nv.isDangLamViec()); p.executeUpdate();
            }
            tk.setMaTK(maTK);
            tk.setDangHoatDong(nv.isDangLamViec() && tk.isDangHoatDong());
            taiKhoanDAO.them(c, tk, maNV);
            nv.setMaNV(maNV); nv.setTaiKhoan(tk); tk.setNhanVien(nv);
            return nv;
        });
    }
    public boolean capNhat(String maQuanLy, NhanVien nv) {
        return Jdbc.giaoDich(nguon, c -> {
            yeuCauQuanLy(c, maQuanLy);
            if (maQuanLy.equals(nv.getMaNV()) && (!nv.isDangLamViec()
                    || !nv.getTaiKhoan().isDangHoatDong()
                    || nv.getTaiKhoan().getVaiTro() != entity.VaiTro.QUAN_LY)) {
                throw new IllegalArgumentException("Không thể tự khóa, nghỉ việc hoặc bỏ quyền Quản lý của chính mình.");
            }
            try (PreparedStatement p = c.prepareStatement("UPDATE dbo.NhanVien SET hoTen=?,sdtNV=?,"
                    + "soCCCD=?,ngaySinh=?,ngayVaoLam=?,diaChi=?,dangLamViec=? WHERE maNV=?")) {
                p.setString(1,nv.getHoTen()); p.setString(2,nv.getSdtNV()); p.setString(3,nv.getSoCCCD());
                p.setDate(4,java.sql.Date.valueOf(nv.getNgaySinh())); p.setDate(5,java.sql.Date.valueOf(nv.getNgayVaoLam()));
                p.setString(6,nv.getDiaChi()); p.setBoolean(7,nv.isDangLamViec()); p.setString(8,nv.getMaNV());
                if (p.executeUpdate()!=1) return false;
            }
            nv.getTaiKhoan().setDangHoatDong(nv.isDangLamViec() && nv.getTaiKhoan().isDangHoatDong());
            taiKhoanDAO.capNhat(c, nv.getTaiKhoan(),nv.getMaNV());
            return true;
        });
    }
    public boolean khoaMoTaiKhoan(String maQuanLy, String maNV, boolean mo) {
        return Jdbc.giaoDich(nguon, c -> {
            yeuCauQuanLy(c, maQuanLy);
            if (maQuanLy.equals(maNV)) throw new IllegalArgumentException("Không thể tự khóa tài khoản.");
            try (PreparedStatement p = c.prepareStatement("SELECT dangLamViec FROM dbo.NhanVien WHERE maNV=?")) {
                p.setString(1, maNV);
                try (ResultSet r = p.executeQuery()) {
                    if (!r.next()) return false;
                    if (mo && !r.getBoolean(1)) throw new IllegalArgumentException("Nhân viên đã nghỉ không thể mở tài khoản.");
                }
            }
            taiKhoanDAO.doiTrangThai(c, maNV, mo);
            return true;
        });
    }
}
