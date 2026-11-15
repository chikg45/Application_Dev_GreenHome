package dao;

import database.DatabaseConnection;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/** JDBC cho quy trinh phieu doi/tra: tiep nhan, gui duyet, duyet/tu choi.
 * KHONG thanh toan hoan tien, cap nhat ton kho hay diem khi duyet. */
public class DoiTraDAO {
    public record HoaDonInfo(String ma, String khach, String sdt, LocalDate ngay,
            List<DongHoaDonInfo> dongs) {}
    public record DongHoaDonInfo(int id, String maSP, String tenSP,
            int soLuong, BigDecimal giaThucTra) {}
    public record DongTraInfo(int idChiTietHD, int soLuong) {}
    public record PhieuInfo(String ma, String maHD, String maNV, String tenNguoiLap,
            String tenNguoiDuyet, String tenLienHe, String sdtLienHe,
            String lyDo, String hienTrang, String phuKien, String huongXuLy,
            String tinhTrangSP, String trangThai, LocalDateTime ngayLap,
            List<DongTraInfo> chiTiet) {}

    public List<HoaDonInfo> hoaDon() throws SQLException {
        Map<String,HoaDonInfo> ds=new LinkedHashMap<>();
        String sql="SELECT h.maHD,h.ngayLap,k.tenKH,k.sdtKH,"+
            " ct.idChiTietHD,ct.maSP,s.tenSP,ct.soLuong,ct.donGia,ct.soTienGiam"+
            " FROM dbo.HoaDon h JOIN dbo.KhachHang k ON k.maKH=h.maKH"+
            " JOIN dbo.ChiTietHoaDon ct ON ct.maHD=h.maHD"+
            " JOIN dbo.SanPham s ON s.maSP=ct.maSP"+
            " ORDER BY h.ngayLap DESC,h.maHD,ct.idChiTietHD";
        try(Connection c=DatabaseConnection.getConnection();
            PreparedStatement p=c.prepareStatement(sql);ResultSet r=p.executeQuery()) {
            while(r.next()) {
                String ma=r.getString("maHD");
                HoaDonInfo hd=ds.get(ma);
                if(hd==null) {
                    hd=new HoaDonInfo(ma,r.getString("tenKH"),r.getString("sdtKH"),
                        r.getTimestamp("ngayLap").toLocalDateTime().toLocalDate(),new ArrayList<>());
                    ds.put(ma,hd);
                }
                BigDecimal goc=r.getBigDecimal("donGia");
                BigDecimal giam=r.getBigDecimal("soTienGiam");
                int qty=r.getInt("soLuong");
                BigDecimal perUnit=goc.subtract(giam.divide(BigDecimal.valueOf(qty),2,RoundingMode.HALF_UP));
                hd.dongs().add(new DongHoaDonInfo(r.getInt("idChiTietHD"),
                    r.getString("maSP"),r.getString("tenSP"),qty,perUnit));
            }
        }
        return new ArrayList<>(ds.values());
    }

    public List<PhieuInfo> phieu() throws SQLException {
        Map<String,MutablePhieu> ds=new LinkedHashMap<>();
        String sql="SELECT p.maPhieuDoiTra,p.maHD,p.maNV,p.ngayLap,p.trangThaiDuyet,"+
            " p.tenLienHe,p.sdtLienHe,p.hienTrang,p.phuKien,p.maNVDuyet,"+
            " n.hoTen AS tenNguoiLap,duyet.hoTen AS tenNguoiDuyet,"+
            " ct.idChiTietHD,ct.soLuong,ct.lyDo,ct.hinhThucXuLy,ct.tinhTrangKhiDoiTra,"+
            " k.tenKH AS tenKhachHang,k.sdtKH AS soDTKhachHang"+
            " FROM dbo.PhieuDoiTra p JOIN dbo.HoaDon h ON h.maHD=p.maHD"+
            " JOIN dbo.KhachHang k ON k.maKH=h.maKH JOIN dbo.NhanVien n ON n.maNV=p.maNV"+
            " LEFT JOIN dbo.NhanVien duyet ON duyet.maNV=p.maNVDuyet"+
            " LEFT JOIN dbo.ChiTietPhieuDoiTra ct ON ct.maPhieuDoiTra=p.maPhieuDoiTra"+
            " ORDER BY p.ngayLap DESC,p.maPhieuDoiTra,ct.idChiTietDoiTra";
        try(Connection c=DatabaseConnection.getConnection();
            PreparedStatement p=c.prepareStatement(sql);ResultSet r=p.executeQuery()) {
            while(r.next()) {
                String ma=r.getString("maPhieuDoiTra");
                MutablePhieu m=ds.get(ma);
                if(m==null) {
                    m=new MutablePhieu();m.ma=ma;m.maHD=r.getString("maHD");
                    m.maNV=r.getString("maNV");m.tenNguoiLap=r.getString("tenNguoiLap");
                    m.tenNguoiDuyet=Objects.toString(r.getString("tenNguoiDuyet"),"");
                    m.tenLienHe=Objects.toString(r.getString("tenLienHe"),r.getString("tenKhachHang"));
                    m.sdtLienHe=Objects.toString(r.getString("sdtLienHe"),r.getString("soDTKhachHang"));
                    m.hienTrang=Objects.toString(r.getString("hienTrang"),"");
                    m.phuKien=Objects.toString(r.getString("phuKien"),"");
                    m.trangThai=r.getString("trangThaiDuyet");
                    m.ngayLap=r.getTimestamp("ngayLap").toLocalDateTime();
                    ds.put(ma,m);
                }
                int id=r.getInt("idChiTietHD");
                if(!r.wasNull()) {
                    m.chiTiet.add(new DongTraInfo(id,r.getInt("soLuong")));
                    if(m.lyDo==null) m.lyDo=Objects.toString(r.getString("lyDo"),"");
                    if(m.huongXuLy==null) m.huongXuLy=r.getString("hinhThucXuLy");
                    if(m.tinhTrangSP==null) m.tinhTrangSP=r.getString("tinhTrangKhiDoiTra");
                }
            }
        }
        List<PhieuInfo> rs=new ArrayList<>();
        for(MutablePhieu m:ds.values()) rs.add(new PhieuInfo(m.ma,m.maHD,m.maNV,m.tenNguoiLap,
            m.tenNguoiDuyet,m.tenLienHe,m.sdtLienHe,Objects.toString(m.lyDo,""),m.hienTrang,
            m.phuKien,Objects.toString(m.huongXuLy,"HOAN_TIEN"),
            Objects.toString(m.tinhTrangSP,"CON_NGUYEN_VEN"),m.trangThai,m.ngayLap,m.chiTiet));
        return rs;
    }
    private static class MutablePhieu {
        String ma,maHD,maNV,tenNguoiLap,tenNguoiDuyet,tenLienHe,sdtLienHe,
            lyDo,hienTrang,phuKien,huongXuLy,tinhTrangSP,trangThai;
        LocalDateTime ngayLap;List<DongTraInfo> chiTiet=new ArrayList<>();
    }

    public String luu(String maPhieuCu,String maHD,String maNV,String tenLienHe,
            String sdtLienHe,String lyDo,String hienTrang,String phuKien,
            String huongXuLy,String tinhTrangSP,List<DongTraInfo> chiTiet) throws SQLException {
        try(Connection c=DatabaseConnection.getConnection()) {
            c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            c.setAutoCommit(false);
            try {
                yeuCauVaiTro(c,maNV,false);
                try(PreparedStatement p=c.prepareStatement(
                        "SELECT ngayLap FROM dbo.HoaDon WITH (UPDLOCK,HOLDLOCK) WHERE maHD=?")) {
                    p.setString(1,maHD);
                    try(ResultSet r=p.executeQuery()) {
                        if(!r.next()) throw new IllegalArgumentException("Hóa đơn gốc "+maHD+" không tồn tại.");
                        LocalDate ngayMua=r.getTimestamp(1).toLocalDateTime().toLocalDate();
                        long age=java.time.temporal.ChronoUnit.DAYS.between(ngayMua,LocalDate.now());
                        if(age<0 || age>7) throw new IllegalArgumentException(
                            "Hóa đơn "+maHD+" đã ngoài hạn đổi/trả 7 ngày (ngày mua: "+ngayMua+").");
                    }
                }
                String ma=maPhieuCu;
                if(ma==null) {
                    long lonNhat=0;
                    try(Statement st=c.createStatement();ResultSet r=st.executeQuery(
                            "SELECT maPhieuDoiTra FROM dbo.PhieuDoiTra WITH (TABLOCKX,HOLDLOCK)")) {
                        while(r.next()) {
                            String m=r.getString(1);
                            if(m!=null && m.matches("DT[0-9]{8}"))
                                lonNhat=Math.max(lonNhat,Long.parseLong(m.substring(2)));
                        }
                    }
                    if(lonNhat>=99999999L) throw new SQLException("Đã hết dải mã phiếu DT + 8 chữ số.");
                    ma="DT"+String.format(Locale.ROOT,"%08d",lonNhat+1);
                    try(PreparedStatement p=c.prepareStatement(
                        "INSERT INTO dbo.PhieuDoiTra(maPhieuDoiTra,ngayLap,trangThaiDuyet,maHD,maNV,"+
                        "tenLienHe,sdtLienHe,hienTrang,phuKien) VALUES (?,SYSDATETIME(),'CHO_DUYET',?,?,?,?,?,?)")) {
                        p.setString(1,ma);p.setString(2,maHD);p.setString(3,maNV);
                        p.setString(4,tenLienHe);p.setString(5,sdtLienHe);
                        p.setString(6,hienTrang);p.setString(7,phuKien);
                        if(p.executeUpdate()!=1) throw new SQLException("Không tạo được phiếu đổi/trả.");
                    }
                } else {
                    // Chỉ chính nhân viên lập phiếu được gửi lại phiếu chờ duyệt/từ chối.
                    try(PreparedStatement p=c.prepareStatement(
                        "UPDATE dbo.PhieuDoiTra SET trangThaiDuyet='CHO_DUYET',"+
                        " tenLienHe=?,sdtLienHe=?,hienTrang=?,phuKien=?,maNVDuyet=NULL"+
                        " WHERE maPhieuDoiTra=? AND maHD=? AND maNV=? AND trangThaiDuyet IN ('CHO_DUYET','TU_CHOI')")) {
                        p.setString(1,tenLienHe);p.setString(2,sdtLienHe);
                        p.setString(3,hienTrang);p.setString(4,phuKien);
                        p.setString(5,ma);p.setString(6,maHD);p.setString(7,maNV);
                        if(p.executeUpdate()!=1) throw new IllegalArgumentException(
                            "Không sửa được phiếu: chỉ người lập được sửa phiếu Chờ duyệt hoặc Từ chối.");
                    }
                    try(PreparedStatement p=c.prepareStatement(
                            "DELETE FROM dbo.ChiTietPhieuDoiTra WHERE maPhieuDoiTra=?")) {
                        p.setString(1,ma);p.executeUpdate();
                    }
                }
                // Khoa dong hoa don; chi dem phieu dang giu so luong (CHO_DUYET/DA_DUYET).
                Set<Integer> ids=new HashSet<>();
                for(DongTraInfo dong:chiTiet) {
                    if(dong.soLuong()<=0 || !ids.add(dong.idChiTietHD()))
                        throw new IllegalArgumentException("Số lượng đổi/trả phải > 0, mỗi dòng hóa đơn chỉ được chọn một lần.");
                    String maSP; BigDecimal donGia;
                    int daMua;
                    try(PreparedStatement p=c.prepareStatement(
                        "SELECT maSP,soLuong,donGia,soTienGiam FROM dbo.ChiTietHoaDon WITH (UPDLOCK,HOLDLOCK)"+
                        " WHERE idChiTietHD=? AND maHD=?")) {
                        p.setInt(1,dong.idChiTietHD());p.setString(2,maHD);
                        try(ResultSet r=p.executeQuery()) {
                            if(!r.next()) throw new IllegalArgumentException(
                                "Dòng hóa đơn "+dong.idChiTietHD()+" không thuộc hóa đơn "+maHD+".");
                            maSP=r.getString("maSP");daMua=r.getInt("soLuong");
                            donGia=r.getBigDecimal("donGia").subtract(r.getBigDecimal("soTienGiam")
                                .divide(BigDecimal.valueOf(daMua),2,RoundingMode.HALF_UP));
                        }
                    }
                    int daGiu=0;
                    try(PreparedStatement p=c.prepareStatement(
                        "SELECT COALESCE(SUM(dt.soLuong),0) FROM dbo.ChiTietPhieuDoiTra dt"+
                        " JOIN dbo.PhieuDoiTra ph ON ph.maPhieuDoiTra=dt.maPhieuDoiTra"+
                        " WHERE dt.idChiTietHD=? AND ph.trangThaiDuyet IN ('CHO_DUYET','DA_DUYET')")) {
                        p.setInt(1,dong.idChiTietHD());
                        try(ResultSet r=p.executeQuery()){if(r.next()) daGiu=r.getInt(1);}
                    }
                    if(dong.soLuong()>daMua-daGiu) throw new IllegalArgumentException(
                        "Sản phẩm "+maSP+": đã mua "+daMua+", đã yêu cầu "+daGiu+
                        ", chỉ được đổi/trả thêm tối đa "+(daMua-daGiu)+" sản phẩm.");
                    try(PreparedStatement p=c.prepareStatement(
                        "INSERT INTO dbo.ChiTietPhieuDoiTra"+
                        "(maPhieuDoiTra,maHD,idChiTietHD,maSP,soLuong,donGia,lyDo,tinhTrangKhiDoiTra,hinhThucXuLy)"+
                        " VALUES (?,?,?,?,?,?,?,?,?)")) {
                        p.setString(1,ma);p.setString(2,maHD);p.setInt(3,dong.idChiTietHD());
                        p.setString(4,maSP);p.setInt(5,dong.soLuong());p.setBigDecimal(6,donGia);
                        p.setString(7,lyDo);p.setString(8,tinhTrangSP);p.setString(9,huongXuLy);
                        if(p.executeUpdate()!=1) throw new SQLException("Không lưu được chi tiết phiếu đổi/trả.");
                    }
                }
                c.commit();return ma;
            } catch(SQLException | RuntimeException ex) {c.rollback();throw ex;}
        }
    }

    public void duyet(String maPhieu,String maNVQuanLy,boolean dongY) throws SQLException {
        try(Connection c=DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                yeuCauVaiTro(c,maNVQuanLy,true);
                try(PreparedStatement p=c.prepareStatement(
                    "UPDATE dbo.PhieuDoiTra SET trangThaiDuyet=?,maNVDuyet=?"+
                    " WHERE maPhieuDoiTra=? AND trangThaiDuyet='CHO_DUYET'")) {
                    p.setString(1,dongY?"DA_DUYET":"TU_CHOI");
                    p.setString(2,maNVQuanLy);p.setString(3,maPhieu);
                    if(p.executeUpdate()!=1) throw new IllegalArgumentException(
                        "Phiếu "+maPhieu+" không còn ở trạng thái Chờ duyệt; hãy tải lại danh sách.");
                }
                c.commit();
            } catch(SQLException | RuntimeException ex) {c.rollback();throw ex;}
        }
    }

    private void yeuCauVaiTro(Connection c,String maNV,boolean quanLy) throws SQLException {
        try(PreparedStatement p=c.prepareStatement(
                "SELECT t.vaiTro FROM dbo.TaiKhoan t JOIN dbo.NhanVien n ON n.maNV=t.maNV"+
                " WHERE t.maNV=? AND t.dangHoatDong=1 AND n.dangLamViec=1")) {
            p.setString(1,maNV);
            try(ResultSet r=p.executeQuery()) {
                if(!r.next()) throw new IllegalArgumentException("Tài khoản hiện tại đã bị khóa/nghỉ việc hoặc không tồn tại.");
                String role=r.getString(1);
                if(quanLy && !"QUAN_LY".equals(role))
                    throw new IllegalArgumentException("Chỉ Quản lý mới được phê duyệt/từ chối phiếu.");
                if(!quanLy && !"NHAN_VIEN_BAN_HANG".equals(role))
                    throw new IllegalArgumentException("Chỉ Nhân viên bán hàng mới được tiếp nhận/sửa phiếu.");
            }
        }
    }
}
