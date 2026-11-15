package tools;

import database.DatabaseConnection;
import java.sql.*;

/** Chỉ SELECT để đối chiếu cấu trúc thật; không sửa dữ liệu hay cấu trúc. */
public class KiemTraKetNoi {
    public static void main(String[] args) {
        String[][] bang = {
            {"DanhMuc","maDM,tenDM"}, {"ThuongHieu","maTH,tenTH"},
            {"SanPham","maSP,tenSP,maDM,maTH,giaNiemYet,tinhTrang,soLuongTon,soLuongTonToiThieu,model,donViTinh,hinhAnh,moTaSP"},
            {"KhachHang","maKH,tenKH,sdtKH,diaChi,diemTichLuy"},
            {"NhanVien","maNV,hoTen"}, {"TaiKhoan","maTK,maNV,tenDangNhap,matKhau,vaiTro"},
            {"KhuyenMai","maKM,tenKM,phanTramGiam,ngayBatDau,ngayKetThuc,moTaKM,loaiGiam,soTienGiam"},
            {"SanPhamKhuyenMai","maSP,maKM"},
            {"DonHang","maDH,ngayDat,trangThaiDH,ghiChu,maKH,maNV"},
            {"ChiTietDonHang","maDH,maSP,soLuong,donGia,serialIMEI"},
            {"HoaDon","maHD,ngayLap,phuongThucThanhToan,maKH,maNV,maDH"},
            {"ChiTietHoaDon","idChiTietHD,maHD,maSP,maKM,soLuong,donGia,serialIMEI,soTienGiam"},
            {"PhieuDoiTra","maPhieuDoiTra,ngayLap,trangThaiDuyet,maHD,maNV"},
            {"ChiTietPhieuDoiTra","maPhieuDoiTra,soLuong,donGia,hinhThucXuLy"}
        };
        try (Connection c=DatabaseConnection.getConnection()) {
            System.out.println("Ket noi thanh cong: " + c.getCatalog()); int errors=0;
            for(String[] b:bang) {
                try(Statement st=c.createStatement(); ResultSet r=st.executeQuery("SELECT TOP 0 " + b[1] + " FROM dbo." + b[0])) {
                    System.out.println("OK " + b[0]);
                } catch(SQLException e) {errors++; System.out.println("LOI " + b[0] + ": " + e.getMessage());}
            }
            if(errors>0) {System.out.println("Can doi chieu lai " + errors + " bang. Chuong trinh khong tu sua CSDL.");System.exit(1);}
            System.out.println("Tat ca bang/cot can cho Ban hang va Thong ke deu ton tai.");
        } catch(SQLException e) {System.err.println("Khong ket noi duoc: " + e.getMessage());System.exit(1);}
    }
}
