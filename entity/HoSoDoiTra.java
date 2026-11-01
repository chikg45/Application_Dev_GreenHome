package entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** Dữ liệu bổ sung cho UI/BUS/DAO; không thay đổi Entity dùng chung của nhóm. */
public final class HoSoDoiTra {
    private HoSoDoiTra() { }
    public record DongGoc(String maSP, String tenSP, int soLuong, long donGia,
                           long thucThu, List<String> serials) { }
    public record HoaDonGoc(String maHD, LocalDateTime ngay, String maKH, String tenKH,
                            String sdt, int diemDung, int diemCong, List<DongGoc> dongs) {
        public long thucThu() { return dongs.stream().mapToLong(DongGoc::thucThu).sum(); }
    }
    public record YeuCau(String maHD, String lienHe, String sdt, String lyDo,
                          String hienTrang, String phuKien, boolean doiHang,
                          boolean loiKyThuat, boolean nguyenVen, boolean duPhuKien) { }
    public record Phieu(PhieuDoiTra entity, HoaDonGoc hoaDon, String tenNguoiLap,
                         String maNVDuyet, LocalDateTime ngayDuyet, LocalDateTime ngayHoanTat,
                         String lyDoTuChoi, YeuCau yeuCau, long tienHoan,
                         Map<String,String> sanPhamDoi, String phuongThucHoan) {
        public String ma() { return entity.getMaPhieuDoiTra(); }
        public String trangThai() { return entity.getTrangThaiDuyet().getNhan(); }
    }
    public record SanPhamDoi(String maSP, String tenSP, String danhMuc, long gia,
                             long khaDung, boolean quanLySerial) {
        @Override public String toString() { return maSP+" · "+tenSP+" · còn "+khaDung; }
    }
    public record HoanTat(String maPhieu, boolean banTiep, String phuongThuc,
                           boolean daXacNhan, Map<String,String> sanPhamDoi) { }
}
