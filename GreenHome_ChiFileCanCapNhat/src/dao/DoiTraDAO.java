package dao;

import connectdb.*;
import entity.*;
import entity.HoSoDoiTra.*;
import enums.TrangThaiDuyet;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/** Chỉ truy vấn/lưu SQL; điều kiện đổi trả và các phép tính thuộc BUS. */
public final class DoiTraDAO {
    private final NguonKetNoi nguon;
    public DoiTraDAO() { this(new ConnectDB()); }
    public DoiTraDAO(NguonKetNoi nguon) { this.nguon=Objects.requireNonNull(nguon); }
    @FunctionalInterface public interface CongViec<T> { T run(Phien p) throws SQLException; }
    public <T>T doc(CongViec<T> task) { return Jdbc.doc(nguon,c->task.run(new Phien(c,false))); }
    public <T>T giaoDich(CongViec<T> task) { return Jdbc.giaoDich(nguon,c->task.run(new Phien(c,true))); }
    public record Nguoi(NhanVien nhanVien,String vaiTro,boolean hoatDong) { }

    public static final class Phien {
        private final Connection c;
        private final boolean ghi;
        private Phien(Connection c,boolean ghi) { this.c=c;this.ghi=ghi; }
        private String khoa(boolean can) { return can ? " WITH (UPDLOCK,HOLDLOCK)" : ""; }
        private void canGhi() { if(!ghi)throw new IllegalStateException("Cần transaction để lưu đổi trả."); }
        public LocalDateTime bayGio() throws SQLException { return Jdbc.bayGio(c); }
        public Nguoi docNguoi(String ma) throws SQLException {
            try(var s=c.prepareStatement("SELECT n.maNV,n.hoTen,t.vaiTro,t.dangHoatDong FROM dbo.NHAN_VIEN n JOIN dbo.TAI_KHOAN t WITH (HOLDLOCK) ON t.maNV=n.maNV WHERE n.maNV=?")) {
                s.setString(1,ma);
                try(var r=s.executeQuery()) {
                    if(!r.next())return null;
                    NhanVien nv=new NhanVien();nv.setMaNV(r.getString(1));nv.setHoTen(r.getString(2));
                    return new Nguoi(nv,r.getString(3),r.getBoolean(4));
                }
            }
        }
        public HoaDonGoc hoaDon(String ma,boolean lock) throws SQLException {
            LocalDateTime ngay;String kh,ten,sdt;int dung,cong;
            try(var s=c.prepareStatement("SELECT * FROM dbo.HOA_DON"+khoa(lock)+" WHERE maHD=?")) {
                s.setString(1,ma);
                try(var r=s.executeQuery()) {
                    if(!r.next())return null;
                    ngay=r.getTimestamp("ngayLap").toLocalDateTime();kh=r.getString("maKH");
                    ten=r.getString("tenKhachHangLucBan");sdt=r.getString("sdtKhachHangLucBan");
                    dung=r.getInt("diemDaDung");cong=r.getInt("diemDaCong");
                }
            }
            List<DongGoc> rows=new ArrayList<>();
            try(var s=c.prepareStatement("SELECT * FROM dbo.CHI_TIET_HOA_DON WHERE maHD=? ORDER BY maSP")) {
                s.setString(1,ma);
                try(var r=s.executeQuery()) {
                    while(r.next()) {
                        String sp=r.getString("maSP");List<String> serials=new ArrayList<>();
                        try(var ss=c.prepareStatement("SELECT serialIMEI FROM dbo.SERIAL_HOA_DON WHERE maHD=? AND maSP=? ORDER BY serialIMEI")) {
                            Jdbc.bind(ss,ma,sp);try(var rr=ss.executeQuery()){while(rr.next())serials.add(rr.getString(1));}
                        }
                        long gia=r.getBigDecimal("donGia").longValueExact();int sl=r.getInt("soLuong");
                        rows.add(new DongGoc(sp,r.getString("tenSP"),sl,gia,
                            Math.subtractExact(Math.multiplyExact(gia,sl),r.getBigDecimal("soTienGiam").longValueExact()),List.copyOf(serials)));
                    }
                }
            }
            return new HoaDonGoc(ma,ngay,kh,ten,sdt,dung,cong,List.copyOf(rows));
        }
        public List<HoaDonGoc> hoaDons() throws SQLException {
            List<String> ids=new ArrayList<>();
            try(var s=c.createStatement();var r=s.executeQuery("SELECT maHD FROM dbo.HOA_DON WHERE ngayLap>=DATEADD(day,-7,CONVERT(date,SYSDATETIME())) ORDER BY ngayLap DESC,maHD")) {
                while(r.next())ids.add(r.getString(1));
            }
            List<HoaDonGoc> ds=new ArrayList<>();for(String id:ids)ds.add(hoaDon(id,false));return ds;
        }
        public boolean coYeuCau(String hd,String truPhieu) throws SQLException {
            try(var s=c.prepareStatement("SELECT 1 FROM dbo.PHIEU_DOI_TRA WITH (UPDLOCK,HOLDLOCK) WHERE maHD=? AND trangThaiDuyet<>'TuChoi' AND maPhieuDoiTra<>?")) {
                Jdbc.bind(s,hd,truPhieu==null?"":truPhieu);try(var r=s.executeQuery()){return r.next();}
            }
        }
        public Phieu phieu(String ma,boolean lock) throws SQLException {
            try(var s=c.prepareStatement("SELECT p.*,n.hoTen FROM dbo.PHIEU_DOI_TRA p"+khoa(lock)+" JOIN dbo.NHAN_VIEN n ON n.maNV=p.maNV WHERE maPhieuDoiTra=?")) {
                s.setString(1,ma);
                try(var r=s.executeQuery()) {
                    if(!r.next())return null;
                    var hd=hoaDon(r.getString("maHD"),false);
                    PhieuDoiTra ent=new PhieuDoiTra();ent.setMaPhieuDoiTra(ma);
                    ent.setNgayLap(r.getTimestamp("ngayLap").toLocalDateTime());
                    ent.setTrangThaiDuyet(TrangThaiDuyet.tuChuoi(r.getString("trangThaiDuyet")));
                    NhanVien nv=new NhanVien();nv.setMaNV(r.getString("maNV"));nv.setHoTen(r.getString("hoTen"));ent.setNhanVien(nv);
                    HoaDon h=new HoaDon();h.setMaHD(hd.maHD());h.setNgayLap(hd.ngay());ent.setHoaDon(h);
                    var yc=new YeuCau(hd.maHD(),r.getString("nguoiLienHe"),r.getString("sdtLienHe"),r.getString("lyDo"),r.getString("tinhTrangKhiDoiTra"),r.getString("phuKien"),
                        "DoiSanPham".equals(r.getString("hinhThucXuLy")),r.getBoolean("loiKyThuat"),r.getBoolean("nguyenVen"),r.getBoolean("duPhuKien"));
                    Map<String,String> doi=new LinkedHashMap<>();long tien=0;
                    try(var ss=c.prepareStatement("SELECT maSP,maSPDoi,thanhTienHoan FROM dbo.CHI_TIET_PHIEU_DOI_TRA WHERE maPhieuDoiTra=? ORDER BY maSP")) {
                        ss.setString(1,ma);try(var rr=ss.executeQuery()){while(rr.next()){
                            if(rr.getString(2)!=null)doi.put(rr.getString(1),rr.getString(2));
                            if(rr.getBigDecimal(3)!=null)tien=Math.addExact(tien,rr.getBigDecimal(3).longValueExact());
                        }}
                    }
                    return new Phieu(ent,hd,r.getString("hoTen"),r.getString("maNVDuyet"),time(r,"ngayDuyet"),time(r,"ngayHoanTat"),
                        r.getString("lyDoTuChoi"),yc,tien,Map.copyOf(doi),r.getString("phuongThucHoanTien"));
                }
            }
        }
        private static LocalDateTime time(ResultSet r,String col)throws SQLException { Timestamp t=r.getTimestamp(col);return t==null?null:t.toLocalDateTime(); }
        public List<Phieu> phieus(String maNV,boolean quanLy) throws SQLException {
            List<String> ids=new ArrayList<>();
            try(var s=c.prepareStatement("SELECT maPhieuDoiTra FROM dbo.PHIEU_DOI_TRA"+(quanLy?"":" WHERE maNV=?")+" ORDER BY ngayLap DESC,maPhieuDoiTra DESC")) {
                if(!quanLy)s.setString(1,maNV);try(var r=s.executeQuery()){while(r.next())ids.add(r.getString(1));}
            }
            List<Phieu> ds=new ArrayList<>();for(String id:ids)ds.add(phieu(id,false));return ds;
        }
        public SanPhamDoi sanPham(String ma,boolean lock) throws SQLException {
            String sql="SELECT p.*,COALESCE((SELECT SUM(CAST(ct.soLuong AS bigint)) FROM dbo.CHI_TIET_DON_HANG ct JOIN dbo.DON_HANG d ON d.maDH=ct.maDH WHERE ct.maSP=p.maSP AND d.trangThaiDH=N'ChuaThanhToan'),0) AS dangGiu FROM dbo.SAN_PHAM p"+khoa(lock)+" WHERE p.maSP=?";
            try(var s=c.prepareStatement(sql)) {
                s.setString(1,ma);try(var r=s.executeQuery()){
                    if(!r.next())return null;
                    long sl="DangBan".equals(r.getString("tinhTrang"))?r.getLong("soLuongTon")-r.getLong("dangGiu"):0;
                    return new SanPhamDoi(ma,r.getString("tenSP"),r.getString("danhMuc"),r.getBigDecimal("giaNiemYet").longValueExact(),Math.max(0,sl),r.getBoolean("quanLySerial"));
                }
            }
        }
        public List<SanPhamDoi> sanPhams() throws SQLException {
            List<String> ids=new ArrayList<>();try(var s=c.createStatement();var r=s.executeQuery("SELECT maSP FROM dbo.SAN_PHAM ORDER BY maSP")){while(r.next())ids.add(r.getString(1));}
            List<SanPhamDoi> ds=new ArrayList<>();for(String id:ids)ds.add(sanPham(id,false));return ds;
        }
        public List<String> serialGiao(String ma,int sl) throws SQLException {
            List<String> ids=new ArrayList<>();
            try(var s=c.prepareStatement("SELECT TOP (?) serialIMEI FROM dbo.THIET_BI_SERIAL WITH (UPDLOCK,HOLDLOCK) WHERE maSP=? AND tinhTrang='SanBan' ORDER BY serialIMEI")) {
                Jdbc.bind(s,sl,ma);try(var r=s.executeQuery()){while(r.next())ids.add(r.getString(1));}
            }return ids;
        }
        public boolean serialGocConDung(String serial,String sp,String hd) throws SQLException {
            try(var s=c.prepareStatement("SELECT 1 FROM dbo.THIET_BI_SERIAL WITH (UPDLOCK,HOLDLOCK) WHERE serialIMEI=? AND maSP=? AND maHD=? AND tinhTrang='DaBan'")) {
                Jdbc.bind(s,serial,sp,hd);try(var r=s.executeQuery()){return r.next();}
            }
        }
        public int diem(String kh) throws SQLException {
            try(var s=c.prepareStatement("SELECT diemTichLuy FROM dbo.KHACH_HANG WITH (UPDLOCK,HOLDLOCK) WHERE maKH=?")) {
                s.setString(1,kh);try(var r=s.executeQuery()){if(!r.next())throw new SQLException("Không tìm thấy khách hàng của hóa đơn.");return r.getInt(1);}
            }
        }
        public boolean duChiTiet(String ma,String hd) throws SQLException {
            String sql="SELECT CASE WHEN (SELECT COUNT(*) FROM dbo.CHI_TIET_PHIEU_DOI_TRA WHERE maPhieuDoiTra=?)=(SELECT COUNT(*) FROM dbo.CHI_TIET_HOA_DON WHERE maHD=?) AND NOT EXISTS(SELECT 1 FROM dbo.CHI_TIET_HOA_DON h LEFT JOIN dbo.CHI_TIET_PHIEU_DOI_TRA d ON d.maHD=h.maHD AND d.maSP=h.maSP AND d.maPhieuDoiTra=? WHERE h.maHD=? AND (d.maSP IS NULL OR d.soLuong<>h.soLuong OR d.donGia<>h.donGia)) THEN 1 ELSE 0 END";
            try(var s=c.prepareStatement(sql)){Jdbc.bind(s,ma,hd,ma,hd);try(var r=s.executeQuery()){r.next();return r.getBoolean(1);}}
        }
        public void luuYeuCau(String ma,String nv,YeuCau y,HoaDonGoc hd,boolean sua) throws SQLException {
            canGhi();
            if(sua) {
                Jdbc.capNhat(c,"DELETE dbo.CHI_TIET_PHIEU_DOI_TRA WHERE maPhieuDoiTra=?",ma);
                Jdbc.capNhat(c,"DELETE dbo.PHIEU_DOI_TRA WHERE maPhieuDoiTra=?",ma);
            }
            Jdbc.capNhat(c,"INSERT dbo.PHIEU_DOI_TRA(maPhieuDoiTra,maHD,maNV,lyDo,tinhTrangKhiDoiTra,hinhThucXuLy,nguoiLienHe,sdtLienHe,phuKien,loiKyThuat,nguyenVen,duPhuKien) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)",
                ma,hd.maHD(),nv,y.lyDo(),y.hienTrang(),y.doiHang()?"DoiSanPham":"HoanTien",y.lienHe(),y.sdt(),y.phuKien(),y.loiKyThuat(),y.nguyenVen(),y.duPhuKien());
            for(var d:hd.dongs())Jdbc.capNhat(c,"INSERT dbo.CHI_TIET_PHIEU_DOI_TRA(maPhieuDoiTra,maHD,maSP,soLuong,donGia,lyDo,tinhTrangKhiDoiTra,hinhThucXuLy,thanhTienHoan) VALUES(?,?,?,?,?,?,?,?,?)",
                ma,hd.maHD(),d.maSP(),d.soLuong(),d.donGia(),y.lyDo(),y.hienTrang(),y.doiHang()?"DoiSanPham":"HoanTien",y.doiHang()?0:d.thucThu());
        }
        public void duyet(String ma,String nv,boolean dongY,String lyDo) throws SQLException {
            canGhi();int n=Jdbc.capNhat(c,"UPDATE dbo.PHIEU_DOI_TRA SET trangThaiDuyet=?,maNVDuyet=?,ngayDuyet=SYSDATETIME(),lyDoTuChoi=? WHERE maPhieuDoiTra=? AND trangThaiDuyet='ChoDuyet'",dongY?"DaDuyet":"TuChoi",nv,lyDo,ma);
            if(n!=1)throw new SQLException("Trạng thái phiếu đã thay đổi.");
        }
        public void nhanHang(String ma,String hd,DongGoc d,boolean banTiep) throws SQLException {
            canGhi();
            Jdbc.capNhat(c,"UPDATE dbo.SAN_PHAM SET soLuongTon=soLuongTon+?,soLuongLoi=soLuongLoi+?,tinhTrang=CASE WHEN tinhTrang=N'NgungBan' THEN tinhTrang WHEN soLuongTon+?=0 THEN N'HetHang' ELSE N'DangBan' END WHERE maSP=?",banTiep?d.soLuong():0,banTiep?0:d.soLuong(),banTiep?d.soLuong():0,d.maSP());
            for(String serial:d.serials()) {
                int n=Jdbc.capNhat(c,"UPDATE dbo.THIET_BI_SERIAL SET tinhTrang=?,maHD=NULL,maPhieuDoiTra=NULL WHERE serialIMEI=? AND maSP=? AND maHD=? AND tinhTrang='DaBan'",banTiep?"SanBan":"Loi",serial,d.maSP(),hd);
                if(n!=1)throw new SQLException("Serial nhận lại đã thay đổi.");
                Jdbc.capNhat(c,"INSERT dbo.SERIAL_DOI_TRA VALUES(?,?, 'NhanLai')",ma,serial);
            }
        }
        public void giaoHang(String phieu,String goc,SanPhamDoi sp,int sl,List<String> serials) throws SQLException {
            canGhi();
            if(Jdbc.capNhat(c,"UPDATE dbo.SAN_PHAM SET soLuongTon=soLuongTon-?,tinhTrang=CASE WHEN soLuongTon-?=0 THEN N'HetHang' ELSE tinhTrang END WHERE maSP=? AND soLuongTon>=?",sl,sl,sp.maSP(),sl)!=1)throw new SQLException("Không đủ tồn giao đổi.");
            Jdbc.capNhat(c,"UPDATE dbo.CHI_TIET_PHIEU_DOI_TRA SET maSPDoi=?,tenSPDoi=? WHERE maPhieuDoiTra=? AND maSP=?",sp.maSP(),sp.tenSP(),phieu,goc);
            for(String serial:serials) {
                if(Jdbc.capNhat(c,"UPDATE dbo.THIET_BI_SERIAL SET tinhTrang='DaBan',maHD=NULL,maPhieuDoiTra=? WHERE serialIMEI=? AND tinhTrang='SanBan'",phieu,serial)!=1)throw new SQLException("Serial giao đổi đã thay đổi.");
                Jdbc.capNhat(c,"INSERT dbo.SERIAL_DOI_TRA VALUES(?,?, 'GiaoDoi')",phieu,serial);
            }
        }
        public void hoanDiem(HoaDonGoc hd,int sau,int bienDong) throws SQLException {
            canGhi();Jdbc.capNhat(c,"UPDATE dbo.KHACH_HANG SET diemTichLuy=? WHERE maKH=?",sau,hd.maKH());
            if(bienDong!=0)Jdbc.capNhat(c,"INSERT dbo.LICH_SU_DIEM(maKH,maHD,loaiGiaoDich,bienDong,soDuSau) VALUES(?,?,'DieuChinh',?,?)",hd.maKH(),hd.maHD(),bienDong,sau);
        }
        public void hoanTat(String ma,String nv,HoanTat h) throws SQLException {
            canGhi();if(Jdbc.capNhat(c,"UPDATE dbo.PHIEU_DOI_TRA SET trangThaiDuyet='HoanTat',ngayHoanTat=SYSDATETIME(),maNVHoanTat=?,banTiep=?,phuongThucHoanTien=? WHERE maPhieuDoiTra=? AND trangThaiDuyet='DaDuyet'",nv,h.banTiep(),h.phuongThuc(),ma)!=1)throw new SQLException("Phiếu không còn ở trạng thái đã duyệt.");
        }
    }
}
