package dao;

import connectDB.Jdbc;
import connectDB.NguonKetNoi;
import database.DatabaseConnection;
import entity.CaLamViec;
import entity.NhanVien;
import entity.PhanCongCa;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/** Phan cong theo ngay; doc ca nhan theo maNV xac thuc (CL-02). */
public class PhanCongCaDAO {
    private final NguonKetNoi nguon;
    private static final String SELECT = "SELECT pc.maPhanCong,pc.ngayLam,pc.maNV,nv.hoTen, "
            + "pc.maCa,ca.tenCa,ca.gioBatDau,ca.gioKetThuc FROM dbo.PhanCongCa pc "
            + "JOIN dbo.NhanVien nv ON pc.maNV=nv.maNV "
            + "JOIN dbo.CaLamViec ca ON pc.maCa=ca.maCa ";

    public PhanCongCaDAO() { this(DatabaseConnection::getConnection); }
    public PhanCongCaDAO(NguonKetNoi nguon) { this.nguon = Objects.requireNonNull(nguon); }

    public List<PhanCongCa> layLich(String maDangNhap, LocalDate dau, LocalDate cuoi, String nhanVienLoc) {
        if (dau == null || cuoi == null || cuoi.isBefore(dau)) throw new IllegalArgumentException("Khoảng ngày không hợp lệ.");
        return Jdbc.doc(nguon, c -> {
            boolean quanLy = coQuyenQuanLy(c, maDangNhap);
            if (!quanLy && nhanVienLoc != null && !nhanVienLoc.equals(maDangNhap))
                throw new SecurityException("Nhân viên chỉ được xem lịch của mình.");
            String sql = SELECT + " WHERE pc.trangThai='DA_PHAN_CONG' AND pc.ngayLam BETWEEN ? AND ? "
                    + ((!quanLy || (nhanVienLoc != null && !nhanVienLoc.isBlank())) ? " AND pc.maNV=? " : "")
                    + " ORDER BY pc.ngayLam,ca.gioBatDau,nv.maNV";
            List<PhanCongCa> result = new ArrayList<>();
            try (PreparedStatement p = c.prepareStatement(sql)) {
                p.setDate(1,java.sql.Date.valueOf(dau)); p.setDate(2,java.sql.Date.valueOf(cuoi));
                if (!quanLy || (nhanVienLoc != null && !nhanVienLoc.isBlank()))
                    p.setString(3,quanLy ? nhanVienLoc : maDangNhap);
                try (ResultSet r = p.executeQuery()) {
                    while(r.next()) {
                        NhanVien nv = new NhanVien();
                        nv.setMaNV(r.getString("maNV")); nv.setHoTen(r.getString("hoTen"));
                        CaLamViec ca = new CaLamViec(r.getString("maCa"),r.getString("tenCa"),
                                r.getTime("gioBatDau").toLocalTime(),r.getTime("gioKetThuc").toLocalTime());
                        result.add(new PhanCongCa(r.getString("maPhanCong"),
                                r.getDate("ngayLam").toLocalDate(),nv,ca));
                    }
                }
            }
            return result;
        });
    }
    private boolean coQuyenQuanLy(Connection c, String maNV) throws SQLException {
        if (maNV == null || maNV.isBlank()) throw new SecurityException("Chưa đăng nhập.");
        String sql = "SELECT tk.vaiTro FROM dbo.TaiKhoan tk JOIN dbo.NhanVien nv ON tk.maNV=nv.maNV "
                + "WHERE tk.maNV=? AND tk.dangHoatDong=1 AND nv.dangLamViec=1";
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1,maNV);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) throw new SecurityException("Tài khoản không hoạt động.");
                return "QUAN_LY".equals(r.getString(1));
            }
        }
    }
    private void kiemTraKhongTrung(Connection c, String maNhanVien, LocalDate ngay,
            String maCa, String maPhanCongDangSua) throws SQLException {
        String sql = "SELECT 1 FROM dbo.PhanCongCa pc JOIN dbo.CaLamViec caCu ON pc.maCa=caCu.maCa "
                + "JOIN dbo.CaLamViec caMoi ON caMoi.maCa=? "
                + "WHERE pc.maNV=? AND pc.ngayLam=? AND pc.trangThai='DA_PHAN_CONG' "
                + "AND pc.maPhanCong<>? AND caMoi.gioBatDau<caCu.gioKetThuc "
                + "AND caCu.gioBatDau<caMoi.gioKetThuc";
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1,maCa);p.setString(2,maNhanVien);p.setDate(3,java.sql.Date.valueOf(ngay));
            p.setString(4,maPhanCongDangSua==null ? "" : maPhanCongDangSua);
            try(ResultSet r=p.executeQuery()) {
                if(r.next()) throw new IllegalArgumentException("Nhân viên đã có ca trùng thời gian.");
            }
        }
    }
    private void kiemTraNhanVien(Connection c, String maNV) throws SQLException {
        String sql = "SELECT 1 FROM dbo.NhanVien nv JOIN dbo.TaiKhoan tk ON nv.maNV=tk.maNV "
                + "WHERE nv.maNV=? AND nv.dangLamViec=1 AND tk.dangHoatDong=1";
        try(PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1,maNV);
            try(ResultSet r=p.executeQuery()) {
                if(!r.next()) throw new IllegalArgumentException("Nhân viên đã nghỉ, bị khóa, hoặc không tồn tại.");
            }
        }
    }
    public String them(String maQuanLy,String maNV,LocalDate ngay,String maCa) {
        return Jdbc.giaoDich(nguon,c -> {
            NhanVienDAO.yeuCauQuanLy(c,maQuanLy);
            c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            kiemTraNhanVien(c,maNV);
            kiemTraKhongTrung(c,maNV,ngay,maCa,null);
            String id = NhanVienDAO.maMoi(c,"PhanCongCa","maPhanCong","PC");
            Jdbc.capNhat(c,"INSERT INTO dbo.PhanCongCa(maPhanCong,ngayLam,maNV,maCa,trangThai) "
                    + "VALUES(?,?,?,?, 'DA_PHAN_CONG')",id,java.sql.Date.valueOf(ngay),maNV,maCa);
            return id;
        });
    }
    public boolean capNhat(String maQuanLy,String id,String maNV,LocalDate ngay,String maCa) {
        return Jdbc.giaoDich(nguon,c -> {
            NhanVienDAO.yeuCauQuanLy(c,maQuanLy);
            c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            kiemTraNhanVien(c,maNV);
            kiemTraKhongTrung(c,maNV,ngay,maCa,id);
            return Jdbc.capNhat(c,"UPDATE dbo.PhanCongCa SET maNV=?,ngayLam=?,maCa=? "
                    + "WHERE maPhanCong=? AND trangThai='DA_PHAN_CONG'",maNV,java.sql.Date.valueOf(ngay),maCa,id)==1;
        });
    }
    /** Huy mem: giu ban ghi de truy vet lich su, dung trangThai DA_HUY. */
    public boolean huy(String maQuanLy,String id) {
        return Jdbc.giaoDich(nguon,c -> {
            NhanVienDAO.yeuCauQuanLy(c,maQuanLy);
            return Jdbc.capNhat(c,"UPDATE dbo.PhanCongCa SET trangThai='DA_HUY' "
                    + "WHERE maPhanCong=? AND trangThai='DA_PHAN_CONG'",id)==1;
        });
    }
}
