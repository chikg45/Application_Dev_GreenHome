package bus;

import dao.DangNhapDAO;
import entity.NhanVien;
import enums.VaiTro;
import java.util.*;

public final class DangNhapBUS {
    private final DangNhapDAO dao;
    public record PhienDangNhap(NhanVien nhanVien,VaiTro vaiTro) {
        public boolean laQuanLy(){return vaiTro==VaiTro.QUAN_LY;}
    }
    public DangNhapBUS(){this(new DangNhapDAO());}
    public DangNhapBUS(DangNhapDAO dao){this.dao=Objects.requireNonNull(dao);}
    public PhienDangNhap dangNhap(String ten,char[] password) {
        if(ten==null || ten.isBlank() || ten.trim().length()>50 || password==null || password.length==0 || password.length>255)
            throw new IllegalArgumentException("Vui lòng nhập tên đăng nhập và mật khẩu hợp lệ.");
        var t=dao.tim(ten.trim());
        if(t==null || !t.hoatDong() || t.matKhau()==null)throw sai();
        // Giữ định dạng mật khẩu hiện tại trong schema; không tự thay dữ liệu tài khoản.
        char[] saved=t.matKhau().toCharArray();
        try{if(!Arrays.equals(saved,password))throw sai();}finally{Arrays.fill(saved,'\0');}
        return new PhienDangNhap(t.nhanVien(),VaiTro.tuChuoi(t.vaiTro()));
    }
    private static IllegalArgumentException sai(){return new IllegalArgumentException("Sai tài khoản/mật khẩu hoặc tài khoản đã khóa.");}
}
