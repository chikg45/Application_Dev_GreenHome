package ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * Giao diện báo cáo thống kê Green Home.
 * Dữ liệu demo, chưa kết nối SQL Server.
 */
public class ThongKePanel extends JPanel {

    // ==================== MÀU SẮC ====================

    private static final Color BG = new Color(30, 33, 37);
    private static final Color CARD = new Color(18, 19, 21);
    private static final Color BORDER = new Color(57, 61, 67);

    private static final Color TEXT = new Color(228, 234, 244);
    private static final Color MUTED = new Color(146, 160, 180);

    private static final Color BLUE = new Color(54, 111, 232);
    private static final Color RED = new Color(188, 90, 95);
    private static final Color GREEN = new Color(60, 176, 124);

    // ==================== NGÀY THÁNG ====================

    private static final DateTimeFormatter DATE =
        DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter DAY_LABEL =
        DateTimeFormatter.ofPattern("dd/MM");

    private static final DateTimeFormatter MONTH_LABEL =
        DateTimeFormatter.ofPattern("MM/uuuu");

    // ==================== DỮ LIỆU ====================

    private final List<HoaDon> hoaDons = new ArrayList<>();
    private final List<PhieuTra> phieuTras = new ArrayList<>();

    private final JTextField txtTu = new JTextField(11);
    private final JTextField txtDen = new JTextField(11);

    private final JComboBox<String> cboNhom =
        new JComboBox<>(new String[] {
            "Theo ngày",
            "Theo tháng"
        });

    private final JPanel tabBar = new JPanel(
        new FlowLayout(FlowLayout.LEFT, 6, 0)
    );

    private final JPanel kpiPanel = new JPanel(
        new GridLayout(1, 4, 12, 0)
    );

    private final JLabel lblBieuDo = label(
        "Tiền bán hàng & hoàn trả",
        13, Font.BOLD, TEXT
    );

    private final JLabel lblMoTa = label(
        "Theo thời gian đã chọn",
        11, Font.PLAIN, MUTED
    );

    private final ChartPanel chart = new ChartPanel();

    private final DefaultTableModel model =
        new DefaultTableModel() {

            @Override
            public boolean isCellEditable(
                    int row, int column) {

                return false;
            }
        };

    private final JTable table = new JTable(model);

    private String tab = "Doanh thu";

    private LocalDate tuNgay;
    private LocalDate denNgay;

    // ==================== CONSTRUCTOR ====================

    public ThongKePanel() {

        setLayout(new BorderLayout());
        setBackground(BG);

        taoDuLieuMau();

        LocalDate now = LocalDate.now();

        tuNgay = now.withDayOfMonth(1);
        denNgay = now.withDayOfMonth(
            now.lengthOfMonth()
        );

        txtTu.setText(DATE.format(tuNgay));
        txtDen.setText(DATE.format(denNgay));

        taoGiaoDien();
        capNhatBaoCao();
    }

    // ==================== DỮ LIỆU MẪU ====================

    private void taoDuLieuMau() {

        hoaDons.add(new HoaDon(
            "HD000003",
            LocalDate.of(2026, 10, 7),
            "Khách vãng lai",
            "Tủ lạnh Panasonic",
            1,
            9015500
        ));

        hoaDons.add(new HoaDon(
            "HD000001",
            LocalDate.of(2026, 10, 15),
            "Trần Thị Mai",
            "Samsung Crystal UHD",
            1,
            11610000
        ));

        hoaDons.add(new HoaDon(
            "HD000002",
            LocalDate.of(2026, 10, 16),
            "Lê Hoàng Nam",
            "Tai nghe Bluetooth",
            2,
            2500000
        ));

        // Hai phiếu đang chờ duyệt.
        // Chưa phát sinh hoàn tiền thật.

        phieuTras.add(new PhieuTra(
            "DT000001",
            LocalDate.of(2026, 10, 18),
            "Chờ duyệt",
            1250000
        ));

        phieuTras.add(new PhieuTra(
            "DT000002",
            LocalDate.of(2026, 10, 18),
            "Chờ duyệt",
            1250000
        ));
    }

    // ==================== GIAO DIỆN ====================

    private void taoGiaoDien() {

        JPanel page = vertical();

        page.setBackground(BG);
        page.setOpaque(true);

        page.setBorder(
            new EmptyBorder(22, 27, 20, 27)
        );

        // -------- TIÊU ĐỀ --------

        JPanel heading = new JPanel(
            new BorderLayout()
        );

        heading.setOpaque(false);

        heading.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        JPanel title = vertical();

        title.add(label(
            "Báo cáo & thống kê",
            21, Font.BOLD, TEXT
        ));

        title.add(Box.createVerticalStrut(5));

        title.add(label(
            "Tổng hợp hoạt động kinh doanh trong khoảng thời gian lựa chọn.",
            11, Font.PLAIN, MUTED
        ));

        heading.add(title, BorderLayout.WEST);

        JButton btnXuat = button(
            "Xuất CSV", false
        );

        btnXuat.addActionListener(e -> xuatCsv());

        heading.add(btnXuat, BorderLayout.EAST);

        addRow(page, heading, 48, 13);

        // -------- BỘ LỌC THỜI GIAN --------

        JPanel filter = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 9, 9)
        );

        filter.setBackground(CARD);

        filter.setBorder(
            new LineBorder(BORDER)
        );

        filter.add(label(
            "Từ ngày", 11, Font.PLAIN, MUTED
        ));

        styleInput(txtTu);
        filter.add(txtTu);

        filter.add(label(
            "Đến ngày", 11, Font.PLAIN, MUTED
        ));

        styleInput(txtDen);
        filter.add(txtDen);

        filter.add(label(
            "Nhóm theo", 11, Font.PLAIN, MUTED
        ));

        cboNhom.setBackground(
            new Color(25, 27, 31)
        );

        cboNhom.setForeground(TEXT);

        cboNhom.setFont(
            new Font("SansSerif", Font.PLAIN, 11)
        );

        filter.add(cboNhom);

        // Nút áp dụng bộ lọc

        JButton btnApDung = button(
            "Áp dụng", true
        );

        btnApDung.addActionListener(
            e -> apDungThoiGian()
        );

        filter.add(btnApDung);

        // Nút chọn tháng hiện tại

        JButton btnThang = button(
            "Tháng này", false
        );

        btnThang.addActionListener(e -> {

            LocalDate n = LocalDate.now();

            txtTu.setText(DATE.format(
                n.withDayOfMonth(1)
            ));

            txtDen.setText(DATE.format(
                n.withDayOfMonth(n.lengthOfMonth())
            ));

            apDungThoiGian();
        });

        filter.add(btnThang);

        cboNhom.addActionListener(
            e -> capNhatBaoCao()
        );

        addRow(page, filter, 60, 13);

        // -------- THẺ THỐNG KÊ --------

        kpiPanel.setOpaque(false);

        addRow(page, kpiPanel, 108, 13);

        // -------- KHUNG BÁO CÁO --------

        JPanel body = new JPanel(
            new BorderLayout()
        );

        body.setBackground(CARD);
        body.setBorder(new LineBorder(BORDER));

        body.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        // Các tab thống kê

        tabBar.setOpaque(false);

        tabBar.setBorder(
            new EmptyBorder(8, 11, 8, 11)
        );

        body.add(tabBar, BorderLayout.NORTH);

        JPanel report = vertical();

        report.setBackground(CARD);
        report.setOpaque(true);

        report.setBorder(
            new EmptyBorder(11, 14, 15, 14)
        );

        JPanel chartTitle = vertical();

        chartTitle.add(lblBieuDo);
        chartTitle.add(Box.createVerticalStrut(5));
        chartTitle.add(lblMoTa);

        chartTitle.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        addRow(report, chartTitle, 38, 5);

        // Biểu đồ

        addRow(report, chart, 235, 10);

        JLabel tableTitle = label(
            "Dữ liệu chi tiết",
            12, Font.BOLD, TEXT
        );

        tableTitle.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        addRow(report, tableTitle, 20, 6);

        // -------- BẢNG CHI TIẾT --------

        table.setRowHeight(35);
        table.setShowGrid(false);

        table.setBackground(CARD);
        table.setForeground(TEXT);

        table.setSelectionBackground(
            new Color(37, 51, 75)
        );

        table.setSelectionForeground(TEXT);

        table.setFont(
            new Font("SansSerif", Font.PLAIN, 11)
        );

        table.setFillsViewportHeight(true);

        table.getTableHeader()
             .setReorderingAllowed(false);

        table.getTableHeader()
             .setPreferredSize(new Dimension(0, 31));

        // Renderer tiêu đề

        table.getTableHeader().setDefaultRenderer(
            new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(
                        JTable t,
                        Object value,
                        boolean selected,
                        boolean focus,
                        int row,
                        int col) {

                    super.getTableCellRendererComponent(
                        t, value, selected, focus, row, col
                    );

                    setBackground(
                        new Color(30, 32, 35)
                    );

                    setForeground(MUTED);

                    setFont(new Font(
                        "SansSerif", Font.BOLD, 10
                    ));

                    setBorder(
                        new EmptyBorder(0, 11, 0, 6)
                    );

                    return this;
                }
            }
        );

        JScrollPane tableScroll =
            new JScrollPane(table);

        tableScroll.setBorder(
            new LineBorder(BORDER)
        );

        tableScroll.getViewport()
                   .setBackground(CARD);

        addRow(report, tableScroll, 185, 0);

        body.add(report, BorderLayout.CENTER);

        addRow(page, body, 585, 0);

        // Cho phép cuộn toàn bộ trang khi cần

        JScrollPane wholeScroll =
            new JScrollPane(page);

        wholeScroll.setBorder(null);

        wholeScroll.getViewport()
                   .setBackground(BG);

        wholeScroll.getVerticalScrollBar()
                   .setUnitIncrement(18);

        wholeScroll.setHorizontalScrollBarPolicy(
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        add(wholeScroll, BorderLayout.CENTER);
    }

    // ==================== LỌC THỜI GIAN ====================

    private void apDungThoiGian() {

        try {

            LocalDate start = LocalDate.parse(
                txtTu.getText().trim(), DATE
            );

            LocalDate end = LocalDate.parse(
                txtDen.getText().trim(), DATE
            );

            if (start.isAfter(end)) {

                JOptionPane.showMessageDialog(
                    this,
                    "Từ ngày không được lớn hơn Đến ngày."
                );

                return;
            }

            tuNgay = start;
            denNgay = end;

            capNhatBaoCao();

        } catch (DateTimeParseException ex) {

            JOptionPane.showMessageDialog(
                this,
                "Ngày phải đúng định dạng dd/MM/yyyy."
            );
        }
    }

    // ==================== CẬP NHẬT BÁO CÁO ====================

    private void capNhatBaoCao() {

        // Cập nhật tab đang chọn

        tabBar.removeAll();

        String[] tabs = {
            "Doanh thu",
            "Sản phẩm",
            "Khách hàng",
            "Đổi / trả"
        };

        for (String name : tabs) {

            JButton b = button(
                name, name.equals(tab)
            );

            b.addActionListener(e -> {

                tab = name;

                capNhatBaoCao();
            });

            tabBar.add(b);
        }

        kpiPanel.removeAll();

        Map<String, Long> bars =
            new LinkedHashMap<>();

        String[] cols;

        List<String[]> rows = new ArrayList<>();

        // -------- LỌC HÓA ĐƠN --------

        List<HoaDon> filtered = new ArrayList<>();

        long doanhThu = 0;
        long soLuong = 0;

        for (HoaDon h : hoaDons) {

            if (trongKhoang(h.ngay)) {

                filtered.add(h);

                doanhThu += h.tien;
                soLuong += h.soLuong;
            }
        }

        // Tiền hoàn chỉ tính phiếu đã hoàn tất

        long hoanThucTe = 0;

        for (PhieuTra p : phieuTras) {

            if (trongKhoang(p.ngay)
                    && "Hoàn tất".equals(p.trangThai)) {

                hoanThucTe += p.tienHoan;
            }
        }

        // ==================== TAB DOANH THU ====================

        if ("Doanh thu".equals(tab)) {

            themKpi(
                "Tổng bán hàng",
                tien(doanhThu),
                "Hóa đơn đã thanh toán",
                BLUE
            );

            themKpi(
                "Tiền hoàn",
                tien(hoanThucTe),
                "Chỉ phiếu đã hoàn tất",
                RED
            );

            themKpi(
                "Doanh thu thuần",
                tien(doanhThu - hoanThucTe),
                "Tổng bán trừ hoàn tiền",
                GREEN
            );

            themKpi(
                "Sản phẩm đã bán",
                String.valueOf(soLuong),
                "Tổng số đơn vị",
                BLUE
            );

            lblBieuDo.setText(
                "Tiền bán hàng theo thời gian"
            );

            cols = new String[] {
                "Ngày",
                "Mã hóa đơn",
                "Khách hàng",
                "Số lượng",
                "Tiền bán"
            };

            for (HoaDon h : filtered) {

                String key = nhomNgay(h.ngay);

                bars.merge(
                    key, h.tien, Long::sum
                );

                rows.add(new String[] {
                    DATE.format(h.ngay),
                    h.ma,
                    h.khach,
                    String.valueOf(h.soLuong),
                    tien(h.tien)
                });
            }

        // ==================== TAB SẢN PHẨM ====================

        } else if ("Sản phẩm".equals(tab)) {

            Set<String> loai = new HashSet<>();

            Map<String, Long> quantities =
                new LinkedHashMap<>();

            for (HoaDon h : filtered) {

                loai.add(h.sanPham);

                quantities.merge(
                    h.sanPham,
                    (long) h.soLuong,
                    Long::sum
                );

                rows.add(new String[] {
                    h.sanPham,
                    String.valueOf(h.soLuong),
                    tien(h.tien),
                    h.ma
                });
            }

            bars.putAll(quantities);

            themKpi(
                "Mẫu hàng đã bán",
                String.valueOf(loai.size()),
                "Sản phẩm khác nhau",
                BLUE
            );

            themKpi(
                "Số lượng bán",
                String.valueOf(soLuong),
                "Tổng số đơn vị",
                GREEN
            );

            themKpi(
                "Doanh thu sản phẩm",
                tien(doanhThu),
                "Tổng tiền đã bán",
                BLUE
            );

            themKpi(
                "Hóa đơn",
                String.valueOf(filtered.size()),
                "Giao dịch có sản phẩm",
                BLUE
            );

            lblBieuDo.setText(
                "Số lượng bán theo sản phẩm"
            );

            cols = new String[] {
                "Sản phẩm",
                "Số lượng",
                "Doanh thu",
                "Hóa đơn"
            };

        // ==================== TAB KHÁCH HÀNG ====================

        } else if ("Khách hàng".equals(tab)) {

            Set<String> members = new HashSet<>();

            Map<String, Long> purchases =
                new LinkedHashMap<>();

            int guests = 0;

            for (HoaDon h : filtered) {

                purchases.merge(
                    h.khach, h.tien, Long::sum
                );

                if ("Khách vãng lai".equals(h.khach)) {
                    guests++;
                } else {
                    members.add(h.khach);
                }

                rows.add(new String[] {
                    h.khach,
                    h.ma,
                    DATE.format(h.ngay),
                    tien(h.tien)
                });
            }

            bars.putAll(purchases);

            themKpi(
                "Khách thành viên",
                String.valueOf(members.size()),
                "Có hóa đơn trong kỳ",
                BLUE
            );

            themKpi(
                "Lượt khách vãng lai",
                String.valueOf(guests),
                "Hóa đơn không có hồ sơ",
                BLUE
            );

            themKpi(
                "Tổng giao dịch",
                String.valueOf(filtered.size()),
                "Hóa đơn trong kỳ",
                GREEN
            );

            themKpi(
                "Tổng chi tiêu",
                tien(doanhThu),
                "Giá trị giao dịch",
                BLUE
            );

            lblBieuDo.setText(
                "Chi tiêu theo khách hàng"
            );

            cols = new String[] {
                "Khách hàng",
                "Hóa đơn",
                "Ngày",
                "Chi tiêu"
            };

        // ==================== TAB ĐỔI TRẢ ====================

        } else {

            int cho = 0;
            int duyet = 0;
            int tuChoi = 0;
            int hoanTat = 0;

            for (PhieuTra p : phieuTras) {

                if (!trongKhoang(p.ngay)) {
                    continue;
                }

                switch (p.trangThai) {

                    case "Chờ duyệt":
                        cho++;
                        break;

                    case "Đã duyệt":
                        duyet++;
                        break;

                    case "Từ chối":
                        tuChoi++;
                        break;

                    case "Hoàn tất":
                        hoanTat++;
                        break;
                }

                rows.add(new String[] {
                    p.ma,
                    DATE.format(p.ngay),
                    p.trangThai,
                    tien(p.tienHoan)
                });
            }

            bars.put("Chờ duyệt", (long) cho);
            bars.put("Đã duyệt", (long) duyet);
            bars.put("Từ chối", (long) tuChoi);
            bars.put("Hoàn tất", (long) hoanTat);

            themKpi(
                "Chờ duyệt",
                String.valueOf(cho),
                "Yêu cầu chưa xét duyệt",
                BLUE
            );

            themKpi(
                "Đã duyệt",
                String.valueOf(duyet),
                "Chưa hoàn tất",
                BLUE
            );

            themKpi(
                "Từ chối",
                String.valueOf(tuChoi),
                "Yêu cầu không được duyệt",
                RED
            );

            themKpi(
                "Hoàn tất",
                String.valueOf(hoanTat),
                "Đã xử lý xong",
                GREEN
            );

            lblBieuDo.setText(
                "Số phiếu theo trạng thái"
            );

            cols = new String[] {
                "Mã phiếu",
                "Ngày lập",
                "Trạng thái",
                "Tiền hoàn dự kiến"
            };
        }

        // ==================== CẬP NHẬT BẢNG ====================

        lblMoTa.setText(
            "Dữ liệu mẫu từ "
            + DATE.format(tuNgay)
            + " đến "
            + DATE.format(denNgay)
        );

        model.setColumnIdentifiers(cols);
        model.setRowCount(0);

        for (String[] row : rows) {
            model.addRow(row);
        }

        DefaultTableCellRenderer cell =
            new DefaultTableCellRenderer();

        cell.setBorder(
            new EmptyBorder(0, 11, 0, 6)
        );

        cell.setForeground(TEXT);
        cell.setBackground(CARD);

        for (int c = 0; c < table.getColumnCount(); c++) {

            table.getColumnModel()
                 .getColumn(c)
                 .setCellRenderer(cell);
        }

        // Cập nhật biểu đồ

        chart.setData(
            bars,
            "Doanh thu".equals(tab)
                || "Khách hàng".equals(tab)
        );

        tabBar.revalidate();
        tabBar.repaint();

        kpiPanel.revalidate();
        kpiPanel.repaint();
    }

    // ==================== KIỂM TRA NGÀY ====================

    private boolean trongKhoang(LocalDate ngay) {

        return !ngay.isBefore(tuNgay)
            && !ngay.isAfter(denNgay);
    }

    private String nhomNgay(LocalDate ngay) {

        return "Theo tháng".equals(
            cboNhom.getSelectedItem()
        )
            ? MONTH_LABEL.format(ngay)
            : DAY_LABEL.format(ngay);
    }

    // ==================== THẺ THỐNG KÊ ====================

    private void themKpi(
            String name,
            String value,
            String description,
            Color accent) {

        JPanel box = new JPanel(
            new BorderLayout()
        );

        box.setBackground(CARD);

        box.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(12, 13, 12, 13)
        ));

        JPanel info = vertical();

        info.add(label(
            name, 11, Font.PLAIN, MUTED
        ));

        info.add(Box.createVerticalStrut(8));

        info.add(label(
            value,
            19,
            Font.BOLD,
            new Color(160, 191, 247)
        ));

        info.add(Box.createVerticalStrut(7));

        info.add(label(
            description, 10, Font.PLAIN, MUTED
        ));

        box.add(info, BorderLayout.CENTER);

        box.add(label(
            "●", 13, Font.BOLD, accent
        ), BorderLayout.EAST);

        kpiPanel.add(box);
    }

    // ==================== XUẤT CSV ====================

    private void xuatCsv() {

        JFileChooser chooser = new JFileChooser();

        chooser.setSelectedFile(
            new File("greenhome_thongke.csv")
        );

        if (chooser.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION) {

            return;
        }

        File file = chooser.getSelectedFile();

        if (file.exists()
                && JOptionPane.showConfirmDialog(
                    this,
                    "Ghi đè tệp đã có?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
                ) != JOptionPane.YES_OPTION) {

            return;
        }

        try (BufferedWriter writer =
                Files.newBufferedWriter(
                    file.toPath(),
                    StandardCharsets.UTF_8
                )) {

            // UTF-8 BOM hỗ trợ Excel đọc tiếng Việt
            writer.write('\uFEFF');

            // Ghi tiêu đề cột

            for (int c = 0; c < model.getColumnCount(); c++) {

                if (c > 0) {
                    writer.write(',');
                }

                writer.write(
                    csv(model.getColumnName(c))
                );
            }

            writer.newLine();

            // Ghi dữ liệu

            for (int r = 0; r < model.getRowCount(); r++) {

                for (int c = 0;
                        c < model.getColumnCount();
                        c++) {

                    if (c > 0) {
                        writer.write(',');
                    }

                    writer.write(csv(
                        String.valueOf(
                            model.getValueAt(r, c)
                        )
                    ));
                }

                writer.newLine();
            }

            JOptionPane.showMessageDialog(
                this,
                "Đã xuất bảng dữ liệu mẫu thành công."
            );

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(
                this,
                "Không thể xuất CSV: "
                    + ex.getMessage()
            );
        }
    }

    private static String csv(String s) {

        return "\""
            + s.replace("\"", "\"\"")
            + "\"";
    }

    // ==================== HÀM HỖ TRỢ ====================

    private static String tien(long n) {

        return java.text.NumberFormat.getIntegerInstance(
            Locale.forLanguageTag("vi-VN")
        ).format(n) + " đ";
    }

    private static JLabel label(
            String value,
            int size,
            int style,
            Color color) {

        JLabel label = new JLabel(value);

        label.setForeground(color);

        label.setFont(new Font(
            "SansSerif", style, size
        ));

        label.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        return label;
    }

    private static JPanel vertical() {

        JPanel p = new JPanel();

        p.setOpaque(false);

        p.setLayout(
            new BoxLayout(p, BoxLayout.Y_AXIS)
        );

        return p;
    }

    private static void addRow(
            JPanel parent,
            JComponent child,
            int height,
            int gap) {

        child.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        child.setPreferredSize(
            new Dimension(950, height)
        );

        child.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, height)
        );

        parent.add(child);

        if (gap > 0) {

            parent.add(
                Box.createVerticalStrut(gap)
            );
        }
    }

    private static void styleInput(JTextField input) {

        input.setBackground(
            new Color(24, 26, 30)
        );

        input.setForeground(TEXT);
        input.setCaretColor(TEXT);

        input.setFont(new Font(
            "SansSerif", Font.PLAIN, 11
        ));

        input.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(8, 9, 8, 9)
        ));
    }

    private static JButton button(
            String title,
            boolean active) {

        JButton b = new JButton(title);

        b.setFont(new Font(
            "SansSerif",
            active ? Font.BOLD : Font.PLAIN,
            11
        ));

        b.setForeground(
            active ? Color.WHITE : TEXT
        );

        b.setBackground(
            active
                ? BLUE
                : new Color(30, 33, 38)
        );

        b.setFocusPainted(false);

        b.setBorder(new CompoundBorder(
            new LineBorder(
                active ? BLUE : BORDER
            ),
            new EmptyBorder(8, 11, 8, 11)
        ));

        b.setCursor(
            Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR
            )
        );

        return b;
    }

    // ==================== BIỂU ĐỒ ====================

    /**
     * Biểu đồ cột vẽ thủ công bằng Graphics2D.
     * Không sử dụng thư viện ngoài.
     */
    private static class ChartPanel extends JPanel {

        private Map<String, Long> data =
            new LinkedHashMap<>();

        private boolean money = true;

        ChartPanel() {
            setBackground(CARD);
        }

        void setData(
                Map<String, Long> values,
                boolean money) {

            data = new LinkedHashMap<>(values);

            this.money = money;

            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            int x0 = 55;
            int top = 15;
            int bottom = getHeight() - 37;
            int right = getWidth() - 16;

            if (right <= x0 || bottom <= top) {

                g2.dispose();
                return;
            }

            long max = 1;

            for (long v : data.values()) {
                max = Math.max(max, v);
            }

            g2.setFont(new Font(
                "SansSerif", Font.PLAIN, 10
            ));

            // -------- VẼ LƯỚI VÀ TRỤC Y --------

            for (int i = 0; i <= 4; i++) {

                int y = bottom
                    - (bottom - top) * i / 4;

                g2.setColor(
                    new Color(62, 66, 72)
                );

                g2.drawLine(
                    x0, y, right, y
                );

                String tick;

                if (money) {

                    tick = String.format(
                        Locale.US,
                        "%.1f tr",
                        (max * i / 4.0) / 1_000_000
                    );

                } else {

                    tick = String.valueOf(
                        Math.round(max * i / 4.0)
                    );
                }

                if (i == 0) {
                    tick = "0";
                }

                g2.setColor(MUTED);
                g2.drawString(tick, 4, y + 4);
            }

            // -------- KHÔNG CÓ DỮ LIỆU --------

            if (data.isEmpty()) {

                g2.setColor(MUTED);

                g2.drawString(
                    "Không có dữ liệu trong khoảng thời gian đã chọn",
                    x0 + 20,
                    (top + bottom) / 2
                );

            } else {

                // -------- VẼ CỘT --------

                int n = data.size();
                int i = 0;

                for (Map.Entry<String, Long> entry
                        : data.entrySet()) {

                    int cx = x0 + (int) (
                        (i + 0.5)
                        * (right - x0) / n
                    );

                    int height = (int) (
                        entry.getValue()
                        * 1.0 / max
                        * (bottom - top)
                    );

                    int barWidth = Math.max(
                        8,
                        Math.min(
                            35,
                            (right - x0)
                                / Math.max(1, n * 3)
                        )
                    );

                    g2.setColor(BLUE);

                    if (height > 0) {

                        g2.fillRoundRect(
                            cx - barWidth / 2,
                            bottom - height,
                            barWidth,
                            height,
                            5,
                            5
                        );
                    }

                    // -------- NHÃN TRỤC X --------

                    g2.setColor(MUTED);

                    String label = entry.getKey();

                    if (label.length() > 16) {

                        label = label.substring(0, 14)
                            + "…";
                    }

                    FontMetrics fm =
                        g2.getFontMetrics();

                    g2.drawString(
                        label,
                        cx - fm.stringWidth(label) / 2,
                        bottom + 18
                    );

                    i++;
                }
            }

            g2.dispose();
        }
    }

    // ==================== MODEL HÓA ĐƠN MẪU ====================

    private static class HoaDon {

        final String ma;
        final String khach;
        final String sanPham;

        final LocalDate ngay;

        final int soLuong;
        final long tien;

        HoaDon(
                String ma,
                LocalDate ngay,
                String khach,
                String sanPham,
                int soLuong,
                long tien) {

            this.ma = ma;
            this.ngay = ngay;
            this.khach = khach;
            this.sanPham = sanPham;
            this.soLuong = soLuong;
            this.tien = tien;
        }
    }

    // ==================== MODEL ĐỔI TRẢ MẪU ====================

    private static class PhieuTra {

        final String ma;
        final String trangThai;

        final LocalDate ngay;
        final long tienHoan;

        PhieuTra(
                String ma,
                LocalDate ngay,
                String trangThai,
                long tienHoan) {

            this.ma = ma;
            this.ngay = ngay;
            this.trangThai = trangThai;
            this.tienHoan = tienHoan;
        }
    }

    // ==================== CHẠY THỬ ====================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame(
                "Green Home - Thống kê (demo)"
            );

            frame.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
            );

            frame.setSize(1200, 760);

            frame.setLocationRelativeTo(null);

            frame.setContentPane(
                new ThongKePanel()
            );

            frame.setVisible(true);
        });
    }
}