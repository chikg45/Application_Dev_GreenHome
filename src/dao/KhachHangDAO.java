package dao;

import database.DatabaseConnection;
import entity.KhachHang;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Doc va ghi ho so khach hang bang JDBC; khong giu du lieu trong RAM. */
public class KhachHangDAO {
    public record KhachHangThongKe(KhachHang khachHang, String ngayMuaGanNhat,
            int soHoaDon, long tongChiTieu) {}

    public List<KhachHangThongKe> danhSach() throws SQLException {
        List<KhachHangThongKe> ds = new ArrayList<>();
        String sql = "SELECT k.maKH,k.tenKH,k.sdtKH,k.diaChi,k.diemTichLuy," +
            " MAX(h.ngayLap) AS ngayMuaGanNhat,COUNT(h.maHD) AS soHoaDon," +
            " COALESCE(SUM(t.tien),0) AS tongChiTieu" +
            " FROM dbo.KhachHang k LEFT JOIN dbo.HoaDon h ON h.maKH=k.maKH" +
            " LEFT JOIN (SELECT ct.maHD,SUM(ct.soLuong*ct.donGia-ct.soTienGiam) AS tien" +
            " FROM dbo.ChiTietHoaDon ct GROUP BY ct.maHD) t ON t.maHD=h.maHD" +
            " GROUP BY k.maKH,k.tenKH,k.sdtKH,k.diaChi,k.diemTichLuy ORDER BY k.tenKH,k.maKH";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                KhachHang kh = new KhachHang(r.getString("maKH"),r.getString("tenKH"),
                    r.getString("sdtKH"),r.getString("diaChi"),r.getInt("diemTichLuy"));
                Timestamp ngay = r.getTimestamp("ngayMuaGanNhat");
                ds.add(new KhachHangThongKe(kh,
                    ngay == null ? "—" : ngay.toLocalDateTime().toLocalDate()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    r.getInt("soHoaDon"),r.getBigDecimal("tongChiTieu").longValue()));
            }
        }
        return ds;
    }

    public List<String[]> lichSuHoaDon(String maKH) throws SQLException {
        List<String[]> ds = new ArrayList<>();
        String sql = "SELECT h.maHD,h.ngayLap AS ngay," +
            " COALESCE(SUM(ct.soLuong*ct.donGia-ct.soTienGiam),0) AS tien" +
            " FROM dbo.HoaDon h LEFT JOIN dbo.ChiTietHoaDon ct ON ct.maHD=h.maHD" +
            " WHERE h.maKH=? GROUP BY h.maHD,h.ngayLap ORDER BY h.ngayLap DESC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1,maKH);
            try (ResultSet r=p.executeQuery()) {
                while (r.next()) ds.add(new String[]{r.getString("maHD"),r.getTimestamp("ngay").toLocalDateTime().toString().replace("T"," "),
                    r.getBigDecimal("tien").toPlainString()});
            }
        }
        return ds;
    }

    public KhachHang them(String ten, String sdt, String diaChi) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);
            c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            try {
                kiemTraSDT(c,sdt,null);
                long stt;
                try (Statement st=c.createStatement(); ResultSet r=st.executeQuery(
                        "SELECT NEXT VALUE FOR dbo.Seq_GH_KH")) {
                    if(!r.next()) throw new SQLException("Không lấy được mã khách hàng mới.");
                    stt=r.getLong(1);
                }
                if(stt<1 || stt>99_999_999L)
                    throw new SQLException("Đã hết dải mã khách hàng KH + 8 chữ số.");
                KhachHang kh = new KhachHang("KH"+String.format(Locale.ROOT,"%08d",stt),
                    ten,sdt,diaChi,0);
                try (PreparedStatement p=c.prepareStatement(
                        "INSERT INTO dbo.KhachHang(maKH,tenKH,sdtKH,diaChi,diemTichLuy) VALUES (?,?,?,?,0)")) {
                    p.setString(1,kh.getMaKH());p.setString(2,kh.getTenKH());
                    p.setString(3,kh.getSdtKH());p.setString(4,kh.getDiaChi());
                    if(p.executeUpdate()!=1) throw new SQLException("Không lưu được hồ sơ khách hàng.");
                }
                c.commit();return kh;
            } catch (SQLException | RuntimeException ex) {
                c.rollback();throw ex;
            }
        }
    }

    public void capNhat(String ma, String ten, String sdt, String diaChi) throws SQLException {
        try (Connection c=DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);
            c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            try {
                kiemTraSDT(c,sdt,ma);
                try (PreparedStatement p=c.prepareStatement(
                    "UPDATE dbo.KhachHang SET tenKH=?,sdtKH=?,diaChi=? WHERE maKH=?")) {
                    p.setString(1,ten);p.setString(2,sdt);p.setString(3,diaChi);p.setString(4,ma);
                    if(p.executeUpdate()!=1)
                        throw new IllegalArgumentException("Không tìm thấy khách hàng mã " + ma + " để cập nhật.");
                }
                c.commit();
            } catch(SQLException | RuntimeException ex) {c.rollback();throw ex;}
        }
    }

    private void kiemTraSDT(Connection c,String sdt,String truMa) throws SQLException {
        String sql="SELECT maKH FROM dbo.KhachHang WITH (UPDLOCK,HOLDLOCK) WHERE sdtKH=?";
        try(PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1,sdt);
            try(ResultSet r=p.executeQuery()) {
                if(r.next() && !r.getString(1).equals(truMa))
                    throw new IllegalArgumentException("Số điện thoại " + sdt +
                        " đã tồn tại ở hồ sơ " + r.getString(1) + ". Vui lòng sử dụng số khác.");
            }
        }
    }
}
