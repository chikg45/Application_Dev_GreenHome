package bus;

import dao.KhachHangDAO;
import entity.KhachHang;
import java.sql.SQLException;
import java.util.List;

/** Validate truoc khi gui thong tin ho so khach hang xuong DAO. */
public class KhachHangBUS {
    private final KhachHangDAO dao = new KhachHangDAO();
    public List<KhachHangDAO.KhachHangThongKe> danhSach() throws SQLException {return dao.danhSach();}
    public List<String[]> lichSuHoaDon(String maKH) throws SQLException {return dao.lichSuHoaDon(maKH);}
    public KhachHang them(String ten,String sdt,String diaChi) throws SQLException {
        kiemTra(ten,sdt,diaChi);
        return dao.them(ten.trim(),sdt.trim(),diaChi==null?"":diaChi.trim());
    }
    public void capNhat(String ma,String ten,String sdt,String diaChi) throws SQLException {
        if(ma==null || !ma.matches("KH[0-9]{8}"))
            throw new IllegalArgumentException("Mã khách hàng phải là KH + 8 chữ số. Ví dụ: KH00000001.");
        kiemTra(ten,sdt,diaChi);
        dao.capNhat(ma,ten.trim(),sdt.trim(),diaChi==null?"":diaChi.trim());
    }
    public static void kiemTra(String ten,String sdt,String diaChi) {
        if(ten==null || ten.trim().isEmpty())
            throw new IllegalArgumentException("Họ tên: không được để trống.");
        if(ten.trim().length()<2 || ten.trim().length()>50)
            throw new IllegalArgumentException("Họ tên: phải dài từ 2 đến 50 ký tự (hiện có " + ten.trim().length() + ").");
        if(sdt==null || !sdt.trim().matches("0[0-9]{9}"))
            throw new IllegalArgumentException("Số điện thoại: phải có đúng 10 chữ số và bắt đầu bằng 0. Ví dụ: 0901234567; không nhập chữ, dấu cách hoặc dấu +84.");
        if(diaChi!=null && diaChi.trim().length()>255)
            throw new IllegalArgumentException("Địa chỉ: tối đa 255 ký tự (hiện có " + diaChi.trim().length() + ").");
    }
}
