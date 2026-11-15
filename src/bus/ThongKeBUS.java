package bus;

import dao.ThongKeDAO.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Tổng hợp theo mã (không gộp hai khách/sản phẩm chỉ vì trùng tên). */
public final class ThongKeBUS {
    private ThongKeBUS() {}
    public record Kpi(String ten, String giaTri, String moTa) {}
    public record BaoCao(List<Kpi> kpis, String tieuDe, String[] cot, List<String[]> dong,
                         Map<String,Long> bieuDo, boolean tien, String ghiChu) {}
    private static class Nhom {
        String ma, ten; LocalDate ngay; long sl;
        BigDecimal tien = BigDecimal.ZERO;
        Set<String> hoaDon = new HashSet<>();
        void cong(DongBan d) { sl += d.soLuong(); tien = tien.add(d.tien()); hoaDon.add(d.maHD()); }
    }
    public static String tien(BigDecimal n) {
        NumberFormat f = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
        f.setMaximumFractionDigits(2); return f.format(n) + " đ";
    }
    public static BaoCao tongHop(DuLieu data, String tab, String nhom) {
        List<Kpi> kpi = new ArrayList<>(); List<String[]> rows = new ArrayList<>();
        Map<String,Long> bars = new LinkedHashMap<>();
        Map<String,Nhom> hd = new LinkedHashMap<>(), sp = new LinkedHashMap<>(), kh = new LinkedHashMap<>();
        BigDecimal doanhThu = BigDecimal.ZERO, hoanDuyet = BigDecimal.ZERO; long sl = 0;
        for (DongBan d : data.ban()) {
            doanhThu = doanhThu.add(d.tien()); sl += d.soLuong();
            Nhom h = hd.computeIfAbsent(d.maHD(), key -> new Nhom()); h.ma = d.maHD(); h.ten = d.tenKH(); h.ngay = d.ngay(); h.cong(d);
            Nhom s = sp.computeIfAbsent(d.maSP(), key -> new Nhom()); s.ma = d.maSP(); s.ten = d.tenSP(); s.cong(d);
            Nhom k = kh.computeIfAbsent(d.maKH(), key -> new Nhom()); k.ma = d.maKH(); k.ten = d.tenKH(); k.cong(d);
        }
        for (PhieuTra p : data.tra()) if ("DA_DUYET".equals(p.trangThai())) hoanDuyet = hoanDuyet.add(p.tienHoan());
        String[] cols; String title; boolean money = true;
        String note = "Số liệu từ hóa đơn và chi tiết đã lưu trong CSDL.";
        if ("Doanh thu".equals(tab)) {
            kpi.add(new Kpi("Tổng bán hàng", tien(doanhThu), hd.size() + " hóa đơn đã thanh toán"));
            kpi.add(new Kpi("Hoàn tiền đã duyệt", tien(hoanDuyet), "Phiếu DA_DUYET, dòng HOAN_TIEN"));
            kpi.add(new Kpi("Bán trừ hoàn đã duyệt", tien(doanhThu.subtract(hoanDuyet)), "Theo trạng thái duyệt hiện có"));
            kpi.add(new Kpi("Sản phẩm đã bán", "" + sl, "Tổng số đơn vị trước đổi/trả"));
            title = "Tiền bán hàng theo thời gian";
            cols = new String[]{"Ngày", "Mã hóa đơn", "Khách hàng", "Số lượng", "Tiền bán"};
            Map<LocalDate,BigDecimal> time = new TreeMap<>();
            for (Nhom h : hd.values()) {
                LocalDate date = h.ngay;
                if ("Theo tháng".equals(nhom)) date = date.withDayOfMonth(1);
                else if ("Theo tuần".equals(nhom)) date = date.minusDays(date.getDayOfWeek().getValue()-1);
                time.merge(date, h.tien, BigDecimal::add);
                rows.add(new String[]{h.ngay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), h.ma, h.ten, "" + h.sl, tien(h.tien)});
            }
            time.forEach((date,value) -> bars.put(("Theo tuần".equals(nhom) ? "Tuần " : "")
                + date.format(DateTimeFormatter.ofPattern("Theo tháng".equals(nhom) ? "MM/yyyy" : "dd/MM/yyyy")), value.longValue()));
            note = "Tiền hoàn dựa trên phiếu đã duyệt; CSDL chưa có ngày/trạng thái xác nhận chi tiền thực tế.";
        } else if ("Sản phẩm".equals(tab)) {
            kpi.add(new Kpi("Mẫu hàng đã bán", ""+sp.size(), "Phân biệt theo mã sản phẩm"));
            kpi.add(new Kpi("Số lượng bán", ""+sl, "Tổng số đơn vị"));
            kpi.add(new Kpi("Doanh thu sản phẩm", tien(doanhThu), "Sau khuyến mãi và điểm"));
            kpi.add(new Kpi("Hóa đơn", ""+hd.size(), "Đếm một lần mỗi hóa đơn"));
            title = "Sản phẩm bán chạy theo số lượng"; money = false;
            cols = new String[]{"Mã SP", "Sản phẩm", "Số lượng", "Doanh thu", "Số hóa đơn"};
            List<Nhom> sorted = new ArrayList<>(sp.values());
            sorted.sort(Comparator.comparingLong((Nhom n)->n.sl).reversed().thenComparing(n->n.ma));
            for (Nhom n : sorted) {
                rows.add(new String[]{n.ma,n.ten,""+n.sl,tien(n.tien),""+n.hoaDon.size()});
                if (bars.size()<10) bars.put(n.ma + " · " + n.ten,n.sl);
            }
        } else if ("Khách hàng".equals(tab)) {
            kpi.add(new Kpi("Khách đã mua", ""+kh.size(), "Phân biệt theo mã khách"));
            kpi.add(new Kpi("Số hóa đơn", ""+hd.size(), "Hóa đơn đã thanh toán"));
            kpi.add(new Kpi("Tổng chi tiêu", tien(doanhThu), "Sau khuyến mãi và điểm"));
            kpi.add(new Kpi("Trung bình / hóa đơn", tien(hd.isEmpty() ? BigDecimal.ZERO : doanhThu.divide(BigDecimal.valueOf(hd.size()),2,java.math.RoundingMode.HALF_UP)), "Giá trị giao dịch bình quân"));
            title = "Khách hàng theo chi tiêu";
            cols = new String[]{"Mã KH", "Khách hàng", "Số hóa đơn", "Chi tiêu"};
            List<Nhom> sorted = new ArrayList<>(kh.values());
            sorted.sort(Comparator.comparing((Nhom n)->n.tien).reversed().thenComparing(n->n.ma));
            for (Nhom n : sorted) {
                rows.add(new String[]{n.ma,n.ten,""+n.hoaDon.size(),tien(n.tien)});
                if (bars.size()<10) bars.put(n.ma + " · " + n.ten,n.tien.longValue());
            }
        } else {
            long cho=0, duyet=0, tuChoi=0;
            for (PhieuTra p : data.tra()) {
                String status;
                switch(p.trangThai()) {
                    case "CHO_DUYET": cho++; status="Chờ duyệt"; break;
                    case "DA_DUYET": duyet++; status="Đã duyệt"; break;
                    default: tuChoi++; status="Từ chối";
                }
                rows.add(new String[]{p.ma(),p.ngay().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),status,tien(p.tienHoan())});
            }
            kpi.add(new Kpi("Chờ duyệt",""+cho,"Yêu cầu chưa xét duyệt"));
            kpi.add(new Kpi("Đã duyệt",""+duyet,"Theo trạng thái CSDL"));
            kpi.add(new Kpi("Từ chối",""+tuChoi,"Yêu cầu bị từ chối"));
            kpi.add(new Kpi("Hoàn tiền đã duyệt",tien(hoanDuyet),"Chỉ dòng HOAN_TIEN"));
            title="Số phiếu theo trạng thái"; money=false;
            cols=new String[]{"Mã phiếu","Ngày lập","Trạng thái","Giá trị yêu cầu hoàn"};
            bars.put("Chờ duyệt",cho); bars.put("Đã duyệt",duyet); bars.put("Từ chối",tuChoi);
            note="Giá trị hoàn lấy từ số lượng × đơn giá dòng HOAN_TIEN. Phiếu duyệt chưa có thông tin xác nhận chi tiền.";
        }
        return new BaoCao(kpi,title,cols,rows,bars,money,note);
    }
}
