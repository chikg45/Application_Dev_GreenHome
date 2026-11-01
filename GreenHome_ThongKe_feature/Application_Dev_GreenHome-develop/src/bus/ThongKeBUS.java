package bus;

import dao.ThongKeDAO;
import java.time.LocalDate;
import java.util.List;

/** Lớp trung gian kiểm tra điều kiện và gọi DAO cho màn hình thống kê. */
public class ThongKeBUS {
    private final ThongKeDAO dao;

    public ThongKeBUS(ThongKeDAO dao) {
        if (dao == null) throw new IllegalArgumentException("DAO thống kê không được null.");
        this.dao = dao;
    }

    public List<ThongKeDAO.HoaDonThongKe> layChiTietHoaDon(LocalDate tuNgay, LocalDate denNgay) {
        kiemTraKhoangNgay(tuNgay, denNgay);
        return dao.layChiTietHoaDon(tuNgay, denNgay);
    }

    public List<ThongKeDAO.PhieuTraThongKe> layPhieuDoiTra(LocalDate tuNgay, LocalDate denNgay) {
        kiemTraKhoangNgay(tuNgay, denNgay);
        return dao.layPhieuDoiTra(tuNgay, denNgay);
    }

    private void kiemTraKhoangNgay(LocalDate tuNgay, LocalDate denNgay) {
        if (tuNgay == null || denNgay == null)
            throw new IllegalArgumentException("Vui lòng chọn đầy đủ ngày bắt đầu và ngày kết thúc.");
        if (tuNgay.isAfter(denNgay))
            throw new IllegalArgumentException("Ngày bắt đầu không được lớn hơn ngày kết thúc.");
    }
}
