package bus;

import dao.DoiTraDAO;
import dao.DoiTraDAO.Phien;
import entity.NhanVien;
import entity.HoSoDoiTra.*;
import enums.VaiTro;
import java.util.*;

/** Đổi/trả toàn bộ hóa đơn, không sửa hóa đơn gốc và không ghi SQL trong BUS. */
public final class DoiTraBUS {
    private final DoiTraDAO dao;
    private final String maNV;
    public DoiTraBUS(NhanVien nv) { this(new DoiTraDAO(),nv); }
    public DoiTraBUS(DoiTraDAO dao,NhanVien nv) {
        this.dao=Objects.requireNonNull(dao);
        if(nv==null || nv.getMaNV()==null || nv.getMaNV().isBlank())throw new IllegalArgumentException("Cần nhân viên đã đăng nhập.");
        maNV=nv.getMaNV();
    }
    private boolean quyen(Phien p,boolean canQuanLy)throws java.sql.SQLException {
        var n=p.docNguoi(maNV);
        if(n==null || !n.hoatDong())throw new IllegalStateException("Tài khoản không còn hoạt động.");
        boolean ql=VaiTro.tuChuoi(n.vaiTro())==VaiTro.QUAN_LY;
        if(canQuanLy && !ql)throw new IllegalStateException("Chỉ quản lý được phê duyệt hoặc từ chối.");
        return ql;
    }
    private Phieu layPhieu(Phien p,String ma,boolean khoa)throws java.sql.SQLException {
        var f=p.phieu(text(ma,"Mã phiếu",20),khoa);
        if(f==null)throw new IllegalArgumentException("Không tìm thấy phiếu.");
        return f;
    }
    private void duocXem(Phieu f,boolean ql) {
        if(!ql && !maNV.equals(f.entity().getNhanVien().getMaNV()))throw new IllegalStateException("Phiếu thuộc nhân viên khác.");
    }
    public boolean laQuanLy() { return dao.doc(p->quyen(p,false)); }
    public List<Phieu> danhSach() { return dao.doc(p->p.phieus(maNV,quyen(p,false))); }
    public List<HoaDonGoc> traCuuHoaDon() { return dao.doc(p->{quyen(p,false);return p.hoaDons();}); }
    public HoaDonGoc timHoaDon(String ma) {
        return dao.doc(p->{quyen(p,false);var h=p.hoaDon(text(ma,"Mã hóa đơn",20),false);if(h==null)throw new IllegalArgumentException("Không tìm thấy hóa đơn đã thanh toán.");return h;});
    }
    public Phieu chiTiet(String ma) {return dao.doc(p->{boolean ql=quyen(p,false);var f=layPhieu(p,ma,false);duocXem(f,ql);return f;});}
    public List<SanPhamDoi> sanPhamDoi() {return dao.doc(p->{quyen(p,false);return p.sanPhams();});}
    private void kiemTraToanBo(Phien p,Phieu f)throws java.sql.SQLException {
        if(!p.duChiTiet(f.ma(),f.hoaDon().maHD()) || f.tienHoan()!=(f.yeuCau().doiHang()?0:f.hoaDon().thucThu()))
            throw new IllegalStateException("Chi tiết phiếu không khớp toàn bộ hóa đơn. Cần kiểm tra/lập lại phiếu trước khi xử lý.");
    }
    public String guiYeuCau(YeuCau y,String maCu) {
        Objects.requireNonNull(y,"Cần thông tin yêu cầu.");
        var input=new YeuCau(text(y.maHD(),"Mã hóa đơn",20),text(y.lienHe(),"Người liên hệ",100),text(y.sdt(),"Số điện thoại",15),
            text(y.lyDo(),"Lý do",255),text(y.hienTrang(),"Hiện trạng",255),text(y.phuKien(),"Phụ kiện",255),y.doiHang(),y.loiKyThuat(),y.nguyenVen(),y.duPhuKien());
        if(!input.sdt().matches("0\\d{9}"))throw new IllegalArgumentException("Số điện thoại phải gồm 10 chữ số, bắt đầu bằng 0.");
        if(!input.loiKyThuat() && !(input.nguyenVen() && input.duPhuKien()))throw new IllegalArgumentException("Chỉ tiếp nhận lỗi kỹ thuật không do khách hoặc hàng nguyên vẹn, đủ phụ kiện.");
        return dao.giaoDich(p->{
            boolean ql=quyen(p,false);
            var hd=p.hoaDon(input.maHD(),true);
            if(hd==null || hd.dongs().isEmpty())throw new IllegalArgumentException("Không có hóa đơn hợp lệ.");
            var ngay=p.bayGio().toLocalDate();
            if(ngay.isBefore(hd.ngay().toLocalDate()) || ngay.isAfter(hd.ngay().toLocalDate().plusDays(7)))throw new IllegalArgumentException("Hóa đơn đã quá thời hạn đổi/trả 7 ngày.");
            String nguoiLap=maNV;
            if(maCu!=null) {
                var f=layPhieu(p,maCu,true);duocXem(f,ql);nguoiLap=f.entity().getNhanVien().getMaNV();
                if(!hd.maHD().equals(f.hoaDon().maHD()) || !Set.of("Chờ duyệt","Từ chối").contains(f.trangThai()))throw new IllegalStateException("Không được sửa phiếu đã duyệt/hoàn tất hoặc đổi hóa đơn gốc.");
            }
            if(p.coYeuCau(hd.maHD(),maCu))throw new IllegalStateException("Hóa đơn đã có phiếu đang xử lý hoặc đã hoàn tất.");
            String id=maCu==null?"DT"+UUID.randomUUID().toString().replace("-","").substring(0,18).toUpperCase(Locale.ROOT):maCu;
            p.luuYeuCau(id,nguoiLap,input,hd,maCu!=null);return id;
        });
    }
    public void xetDuyet(String ma,boolean dongY,String lyDo) {
        String reason=dongY?null:text(lyDo,"Lý do từ chối",255);
        dao.giaoDich(p->{
            quyen(p,true);var f=layPhieu(p,ma,true);
            if(!"Chờ duyệt".equals(f.trangThai()))throw new IllegalStateException("Chỉ được xét duyệt phiếu đang chờ.");
            if(dongY)kiemTraToanBo(p,f);
            if(dongY && !f.yeuCau().loiKyThuat() && !(f.yeuCau().nguyenVen() && f.yeuCau().duPhuKien()))throw new IllegalStateException("Phiếu chưa đủ điều kiện đổi/trả.");
            p.duyet(ma,maNV,dongY,reason);return null;
        });
    }
    public void hoanTat(HoanTat h) {
        Objects.requireNonNull(h);
        if(!h.daXacNhan())throw new IllegalArgumentException("Cần xác nhận đã nhận hàng và đã hoàn tiền/giao hàng đổi.");
        dao.giaoDich(p->{
            boolean ql=quyen(p,false);var f=layPhieu(p,h.maPhieu(),true);duocXem(f,ql);
            if(!"Đã duyệt".equals(f.trangThai()))throw new IllegalStateException("Chỉ hoàn tất phiếu đã được quản lý duyệt.");
            kiemTraToanBo(p,f);
            var y=f.yeuCau();var hd=f.hoaDon();
            if(h.banTiep() && (y.loiKyThuat() || !y.nguyenVen() || !y.duPhuKien()))throw new IllegalArgumentException("Hàng lỗi hoặc chưa đủ điều kiện phải nhập vào hàng lỗi, không nhập sẵn bán.");
            if(!y.doiHang() && !Set.of("TienMat","ChuyenKhoan").contains(h.phuongThuc()))throw new IllegalArgumentException("Chọn phương thức hoàn tiền mặt/chuyển khoản.");
            Map<String,String> mapping=h.sanPhamDoi()==null?Map.of():Map.copyOf(h.sanPhamDoi());
            if(y.doiHang() && (mapping.size()!=hd.dongs().size() || !mapping.keySet().equals(hd.dongs().stream().map(DongGoc::maSP).collect(java.util.stream.Collectors.toSet()))))throw new IllegalArgumentException("Phải chọn hàng đổi cho toàn bộ hóa đơn.");
            SortedSet<String> ids=new TreeSet<>();for(var d:hd.dongs())ids.add(d.maSP());if(y.doiHang())ids.addAll(mapping.values());
            Map<String,SanPhamDoi> stocks=new TreeMap<>();
            for(String id:ids){var sp=p.sanPham(id,true);if(sp==null)throw new IllegalStateException("Sản phẩm không còn tồn tại: "+id);stocks.put(id,sp);}
            Map<String,Integer> canGiao=new TreeMap<>();
            if(y.doiHang())for(var d:hd.dongs()) {
                var old=stocks.get(d.maSP());var sp=stocks.get(mapping.get(d.maSP()));
                if(!d.maSP().equals(sp.maSP()) && old.khaDung()>=d.soLuong())throw new IllegalArgumentException("Còn đủ hàng cùng mã "+d.maSP()+", cần ưu tiên đổi cùng mã.");
                if(!d.maSP().equals(sp.maSP()) && (!old.danhMuc().equals(sp.danhMuc()) || sp.gia()!=d.donGia()))throw new IllegalArgumentException("Hàng tương đương phải cùng danh mục và bằng đơn giá gốc; chưa hỗ trợ bù chênh lệch.");
                canGiao.merge(sp.maSP(),d.soLuong(),Math::addExact);
            }
            Map<String,Deque<String>> serialGiao=new HashMap<>();
            for(var e:canGiao.entrySet()) {
                var sp=stocks.get(e.getKey());if(sp.khaDung()<e.getValue())throw new IllegalArgumentException("Không đủ hàng sẵn bán để đổi: "+sp.maSP());
                var serials=sp.quanLySerial()?p.serialGiao(sp.maSP(),e.getValue()):List.<String>of();
                if(sp.quanLySerial() && serials.size()!=e.getValue())throw new IllegalStateException("Không đủ Serial sẵn bán cho "+sp.maSP());
                serialGiao.put(sp.maSP(),new ArrayDeque<>(serials));
            }
            for(var d:hd.dongs()) {
                if(stocks.get(d.maSP()).quanLySerial() && d.serials().size()!=d.soLuong())throw new IllegalStateException("Serial hóa đơn gốc không đầy đủ, cần đối chiếu trước khi xử lý.");
                for(String serial:d.serials())if(!p.serialGocConDung(serial,d.maSP(),hd.maHD()))throw new IllegalStateException("Serial nhận lại không còn khớp hóa đơn: "+serial);
            }
            int delta=0,sau=0;
            if(!y.doiHang() && hd.maKH()!=null) {
                delta=Math.subtractExact(hd.diemDung(),hd.diemCong());sau=Math.addExact(p.diem(hd.maKH()),delta);
                if(sau<0)throw new IllegalStateException("Khách đã dùng điểm tích từ hóa đơn này. Cần xử lý đủ điểm trước khi hoàn tiền; số dư không được âm.");
            }
            // Tất cả kiểm tra đã đạt; DAO mới thực hiện ghi trong cùng transaction.
            for(var d:hd.dongs())p.nhanHang(f.ma(),hd.maHD(),d,h.banTiep());
            if(y.doiHang())for(var d:hd.dongs()) {
                var sp=stocks.get(mapping.get(d.maSP()));List<String> serials=new ArrayList<>();
                if(sp.quanLySerial())for(int i=0;i<d.soLuong();i++)serials.add(serialGiao.get(sp.maSP()).removeFirst());
                p.giaoHang(f.ma(),d.maSP(),sp,d.soLuong(),serials);
            }
            if(!y.doiHang() && hd.maKH()!=null)p.hoanDiem(hd,sau,delta);
            p.hoanTat(f.ma(),maNV,new HoanTat(f.ma(),h.banTiep(),y.doiHang()?"KhongHoanTien":h.phuongThuc(),true,mapping));
            return null;
        });
    }
    private static String text(String v,String ten,int max) {
        if(v==null || v.isBlank() || v.trim().length()>max)throw new IllegalArgumentException(ten+" không được trống và tối đa "+max+" ký tự.");
        return v.trim();
    }
}
