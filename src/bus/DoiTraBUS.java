package bus;

import entity.ChiTietPhieuDoiTra;
import entity.PhieuDoiTra;
import entity.HinhThucXuLy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Xu ly cac quy tac nghiep vu doi/tra.
 * Lop nay khong truy cap SQL va khong phu thuoc giao dien.
 */
public final class DoiTraBUS {

    private DoiTraBUS() {
    }

    /**
     * Kiem tra ngay lap phieu co nam trong 7 ngay ke tu ngay mua hay khong.
     */
    public static boolean kiemTraTrongHan7Ngay(
            PhieuDoiTra phieu, LocalDateTime ngayMua) {

        if (phieu == null || ngayMua == null || phieu.getNgayLap() == null) {
            return false;
        }

        LocalDateTime ngayLap = phieu.getNgayLap();
        return !ngayLap.isBefore(ngayMua)
                && !ngayLap.isAfter(ngayMua.plusDays(7));
    }

    /**
     * Tinh tien hoan cua mot chi tiet.
     * Chi hoan tien khi hinh thuc xu ly la HOAN_TIEN.
     */
    public static BigDecimal tinhThanhTienHoan(
            ChiTietPhieuDoiTra chiTiet) {

        if (chiTiet == null) {
            throw new IllegalArgumentException(
                    "Chi tiết phiếu đổi trả không được null.");
        }

        if (chiTiet.getHinhThucXuLy() == null) {
            throw new IllegalStateException(
                    "Chưa chọn hình thức xử lý.");
        }

        if (chiTiet.getHinhThucXuLy() != HinhThucXuLy.HOAN_TIEN) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return BigDecimal.valueOf(chiTiet.getDonGia())
                .multiply(BigDecimal.valueOf(chiTiet.getSoLuong()))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Tinh tong tien hoan cua tat ca chi tiet trong phieu.
     */
    public static BigDecimal tinhTongTienHoan(PhieuDoiTra phieu) {

        if (phieu == null) {
            throw new IllegalArgumentException(
                    "Phiếu đổi trả không được null.");
        }

        BigDecimal tong = BigDecimal.ZERO;

        for (ChiTietPhieuDoiTra chiTiet : phieu.getDanhSachChiTiet()) {
            tong = tong.add(tinhThanhTienHoan(chiTiet));
        }

        return tong.setScale(2, RoundingMode.HALF_UP);
    }
    /** Các hàm nghiệp vụ có JDBC chỉ điều phối qua DAO, không truy cập SQL trong giao diện. */
    public static java.util.List<dao.DoiTraDAO.HoaDonInfo> danhSachHoaDon()
            throws java.sql.SQLException { return new dao.DoiTraDAO().hoaDon(); }
    public static java.util.List<dao.DoiTraDAO.PhieuInfo> danhSachPhieu()
            throws java.sql.SQLException { return new dao.DoiTraDAO().phieu(); }

    public static String guiPhieu(String maPhieuCu, String maHD,String maNV,
            String tenLienHe,String sdt,String lyDo,String hienTrang,String phuKien,
            String huongXuLy,String tinhTrangSP,java.util.List<dao.DoiTraDAO.DongTraInfo> dongs)
            throws java.sql.SQLException {
        if(tenLienHe==null || tenLienHe.trim().length()<2 || tenLienHe.trim().length()>100)
            throw new IllegalArgumentException("Người liên hệ: cần 2–100 ký tự, không được bỏ trống.");
        if(sdt==null || !sdt.trim().matches("0[0-9]{9}"))
            throw new IllegalArgumentException("Số điện thoại: phải có đúng 10 chữ số, bắt đầu bằng 0. Ví dụ 0901234567 (không nhập +84, chữ hoặc khoảng trắng).");
        if(lyDo==null || lyDo.trim().length()<10 || lyDo.trim().length()>500)
            throw new IllegalArgumentException("Lý do đổi/trả: phải có 10–500 ký tự, cần mô tả rõ lỗi hoặc nguyên nhân.");
        if(hienTrang==null || hienTrang.trim().length()<2 || hienTrang.trim().length()>500)
            throw new IllegalArgumentException("Hiện trạng sản phẩm: phải có 2–500 ký tự.");
        if(phuKien==null || phuKien.trim().length()<2 || phuKien.trim().length()>255)
            throw new IllegalArgumentException("Phụ kiện đi kèm: phải có 2–255 ký tự (ghi 'Không có' nếu không có).");
        if(!"HOAN_TIEN".equals(huongXuLy) && !"DOI_SAN_PHAM".equals(huongXuLy))
            throw new IllegalArgumentException("Hướng xử lý: chỉ nhận Trả hàng & hoàn tiền hoặc Đổi sản phẩm tương đương.");
        if(!java.util.Set.of("CON_NGUYEN_VEN","LOI_KY_THUAT","HU_HONG").contains(tinhTrangSP))
            throw new IllegalArgumentException("Tình trạng sản phẩm: phải chọn Còn nguyên vẹn, Lỗi kỹ thuật hoặc Hư hỏng.");
        if(dongs==null || dongs.isEmpty())
            throw new IllegalArgumentException("Sản phẩm đổi/trả: chọn ít nhất 1 dòng hóa đơn và số lượng lớn hơn 0.");
        return new dao.DoiTraDAO().luu(maPhieuCu,maHD,maNV,
            tenLienHe.trim(),sdt.trim(),lyDo.trim(),hienTrang.trim(),phuKien.trim(),
            huongXuLy,tinhTrangSP,dongs);
    }
    public static void xetDuyet(String maPhieu,String maNVQuanLy,boolean duyet)
            throws java.sql.SQLException {
        if(maPhieu==null || !maPhieu.matches("DT[0-9]{8}"))
            throw new IllegalArgumentException("Mã phiếu đổi/trả phải là DT + 8 chữ số. Ví dụ DT00000001.");
        new dao.DoiTraDAO().duyet(maPhieu,maNVQuanLy,duyet);
    }
}
