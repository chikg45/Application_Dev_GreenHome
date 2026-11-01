package dao;

import connectdb.*;
import entity.NhanVien;
import java.util.Objects;

/** Đọc tài khoản và nhân viên từ CSDL dùng chung, không tự tạo tài khoản mẫu. */
public final class DangNhapDAO {
    private final NguonKetNoi nguon;
    public record TaiKhoanDoc(NhanVien nhanVien,String matKhau,String vaiTro,boolean hoatDong) { }
    public DangNhapDAO(){this(new ConnectDB());}
    public DangNhapDAO(NguonKetNoi nguon){this.nguon=Objects.requireNonNull(nguon);}
    public TaiKhoanDoc tim(String ten) {
        return Jdbc.doc(nguon,c->{
            try(var s=c.prepareStatement("SELECT n.maNV,n.hoTen,t.matKhau,t.vaiTro,t.dangHoatDong FROM dbo.TAI_KHOAN t JOIN dbo.NHAN_VIEN n ON n.maNV=t.maNV WHERE t.tenDangNhap=?")) {
                s.setString(1,ten);try(var r=s.executeQuery()) {
                    if(!r.next())return null;
                    NhanVien nv=new NhanVien();nv.setMaNV(r.getString(1));nv.setHoTen(r.getString(2));
                    return new TaiKhoanDoc(nv,r.getString(3),r.getString(4),r.getBoolean(5));
                }
            }
        });
    }
}
