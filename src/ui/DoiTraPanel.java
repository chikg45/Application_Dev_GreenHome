package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Giao diện đổi/trả dùng chung cho nhân viên và quản lý.
 * Dữ liệu hiện tại là dữ liệu demo, chưa kết nối SQL Server.
 */
public class DoiTraPanel extends JPanel {

    // ==================== MÀU SẮC ====================

    private static final Color BG = new Color(30, 33, 37);
    private static final Color CARD = new Color(18, 19, 21);
    private static final Color BORDER = new Color(57, 61, 67);
    private static final Color TEXT = new Color(225, 232, 242);
    private static final Color MUTED = new Color(143, 155, 173);
    private static final Color BLUE = new Color(54, 111, 232);
    private static final Color GREEN = new Color(59, 173, 121);
    private static final Color GOLD = new Color(208, 160, 65);
    private static final Color RED = new Color(205, 99, 102);

    private static final DateTimeFormatter NGAY =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ==================== DỮ LIỆU ====================

    private static final List<HoaDonMau> HOA_DON =
            new ArrayList<>();

    private static final List<PhieuMau> PHIEU =
            new ArrayList<>();

    private static int maTiepTheo = 3;

    private final boolean laQuanLy;

    private final List<PhieuMau> dangHienThi =
            new ArrayList<>();

    private final JTextField txtTim = new JTextField();

    private final JComboBox<String> cboTrangThai =
            new JComboBox<>(new String[] {
                "Tất cả trạng thái",
                "Chờ duyệt",
                "Đã duyệt",
                "Từ chối",
                "Hoàn tất"
            });

    private final JLabel[] lblDem = new JLabel[4];

    private final DefaultTableModel model =
            new DefaultTableModel(
                new String[] {
                    "Phiếu / hóa đơn gốc",
                    "Khách hàng",
                    "Hướng xử lý",
                    "Tiền hoàn",
                    "Trạng thái",
                    "Thao tác"
                }, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };

    private final JTable table = new JTable(model);

    // ==================== CONSTRUCTOR ====================

    public DoiTraPanel(boolean laQuanLy) {

        this.laQuanLy = laQuanLy;

        taoDuLieuMau();

        setLayout(new BorderLayout());
        setBackground(BG);

        taoGiaoDien();
        taiDanhSach();
    }

    // ==================== DỮ LIỆU MẪU ====================

    private static void taoDuLieuMau() {

        if (!HOA_DON.isEmpty()) {
            return;
        }

        // Hóa đơn 1
        HoaDonMau hd1 = new HoaDonMau(
            "HD000001",
            "Trần Thị Mai",
            "0932345678",
            LocalDate.of(2026, 10, 15)
        );

        hd1.dongs.add(new DongHoaDon(
            "CT01",
            "Samsung Crystal UHD",
            1,
            11610000
        ));

        HOA_DON.add(hd1);

        // Hóa đơn 2
        HoaDonMau hd2 = new HoaDonMau(
            "HD000002",
            "Lê Hoàng Nam",
            "0383456789",
            LocalDate.of(2026, 10, 16)
        );

        hd2.dongs.add(new DongHoaDon(
            "CT02A",
            "Tai nghe Bluetooth - Mới",
            1,
            1250000
        ));

        hd2.dongs.add(new DongHoaDon(
            "CT02B",
            "Tai nghe Bluetooth - Mới",
            1,
            1250000
        ));

        HOA_DON.add(hd2);

        // Hóa đơn 3
        HoaDonMau hd3 = new HoaDonMau(
            "HD000003",
            "Khách vãng lai",
            "",
            LocalDate.of(2026, 10, 7)
        );

        hd3.dongs.add(new DongHoaDon(
            "CT03",
            "Tủ lạnh Panasonic",
            1,
            9015500
        ));

        HOA_DON.add(hd3);

        // Hóa đơn mới để thử quy trình trong hạn 7 ngày
        HoaDonMau hd4 = new HoaDonMau(
            "HD000004",
            "Nguyễn Văn An",
            "0901234567",
            LocalDate.now().minusDays(2)
        );

        hd4.dongs.add(new DongHoaDon(
            "CT04",
            "Dell Inspiron 15",
            2,
            22500000
        ));

        HOA_DON.add(hd4);

        // Phiếu mẫu của nhân viên Trần Thị Linh
        PhieuMau p1 = new PhieuMau("DT000001", hd2);

        p1.nguoiLap = "Trần Thị Linh";
        p1.nguoiLienHe = hd2.khach;
        p1.sdt = hd2.sdt;
        p1.lyDo = "Không nghe được một bên";
        p1.hienTrang = "Hoạt động kém";
        p1.phuKien = "Không có";
        p1.huongXuLy = "Trả hàng & hoàn tiền";

        p1.chiTiet.add(new DongTra(hd2.dongs.get(0), 1));

        PHIEU.add(p1);

        // Phiếu mẫu thứ hai để quản lý xét duyệt
        PhieuMau p2 = new PhieuMau("DT000002", hd2);

        p2.nguoiLap = "Nguyễn Văn A";
        p2.nguoiLienHe = hd2.khach;
        p2.sdt = hd2.sdt;
        p2.lyDo = "Không nghe được một bên";
        p2.hienTrang = "Hoạt động kém";
        p2.phuKien = "Không có";
        p2.huongXuLy = "Trả hàng & hoàn tiền";

        p2.chiTiet.add(new DongTra(hd2.dongs.get(1), 1));

        PHIEU.add(p2);
    }

    // ==================== DANH SÁCH PHIẾU ====================

    private void taoGiaoDien() {

        JPanel page = new JPanel(new BorderLayout(0, 13));

        page.setBackground(BG);
        page.setBorder(new EmptyBorder(22, 25, 20, 25));

        JPanel top = vertical();

        // Tiêu đề
        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);

        JPanel title = vertical();

        title.add(label(
            "Xử lý đổi / trả hàng",
            21,
            TEXT,
            Font.BOLD
        ));

        title.add(Box.createVerticalStrut(6));

        title.add(label(
            laQuanLy
                ? "Kiểm tra và phê duyệt phiếu trước khi nhân viên thực hiện."
                : "Tiếp nhận yêu cầu đổi/trả; mọi phiếu đều cần quản lý duyệt.",
            11,
            MUTED,
            Font.PLAIN
        ));

        heading.add(title, BorderLayout.WEST);

        if (!laQuanLy) {

            JButton btn = button(
                "+  Tiếp nhận đổi / trả", true
            );

            btn.addActionListener(e -> moTraCuu());

            heading.add(btn, BorderLayout.EAST);
        }

        addRow(top, heading, 52);

        // Thanh quy trình
        JPanel progress = card(
            new GridLayout(1, 4, 5, 0)
        );

        String[] steps = {
            "① Nhân viên tiếp nhận",
            "② Quản lý phê duyệt",
            "③ Đổi hàng / hoàn tiền",
            "④ Đánh giá hàng nhận lại"
        };

        for (String step : steps) {

            JLabel l = label(
                step, 11, MUTED, Font.PLAIN
            );

            l.setHorizontalAlignment(
                SwingConstants.CENTER
            );

            progress.add(l);
        }

        progress.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(12, 7, 12, 7)
        ));

        addRow(top, progress, 48);

        // Bốn thẻ thống kê
        JPanel stats = new JPanel(
            new GridLayout(1, 4, 12, 0)
        );

        stats.setOpaque(false);

        String[] captions = {
            "Chờ duyệt",
            "Đã duyệt",
            "Từ chối",
            "Hoàn tất"
        };

        Color[] colors = {
            GOLD, BLUE, RED, GREEN
        };

        for (int i = 0; i < 4; i++) {

            lblDem[i] = label(
                "0",
                23,
                new Color(164, 193, 247),
                Font.BOLD
            );

            stats.add(stat(
                captions[i],
                lblDem[i],
                colors[i]
            ));
        }

        addRow(top, stats, 105);

        page.add(top, BorderLayout.NORTH);

        // Khung danh sách
        JPanel list = card(new BorderLayout());

        list.setBorder(new LineBorder(BORDER));

        JPanel filters = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 12)
        );

        filters.setOpaque(false);

        txtTim.setPreferredSize(new Dimension(300, 35));
        inputStyle(txtTim);

        txtTim.setToolTipText(
            "Tìm mã phiếu, hóa đơn, tên hoặc SĐT khách hàng"
        );

        cboTrangThai.setBackground(CARD);
        cboTrangThai.setForeground(TEXT);

        cboTrangThai.setPreferredSize(
            new Dimension(165, 35)
        );

        filters.add(txtTim);
        filters.add(cboTrangThai);

        list.add(filters, BorderLayout.NORTH);

        // JTable
        table.setBackground(CARD);
        table.setForeground(TEXT);

        table.setSelectionBackground(
            new Color(38, 51, 72)
        );

        table.setSelectionForeground(TEXT);
        table.setRowHeight(53);

        table.setFont(
            new Font("SansSerif", Font.PLAIN, 11)
        );

        table.setShowGrid(false);
        table.setFillsViewportHeight(true);

        table.getTableHeader().setReorderingAllowed(false);

        table.getTableHeader().setPreferredSize(
            new Dimension(0, 34)
        );

        // Renderer tiêu đề
        table.getTableHeader().setDefaultRenderer(
            new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(
                        JTable t, Object v,
                        boolean selected, boolean focused,
                        int row, int col) {

                    super.getTableCellRendererComponent(
                        t, v, selected, focused, row, col
                    );

                    setBackground(new Color(28, 30, 33));
                    setForeground(MUTED);

                    setFont(new Font(
                        "SansSerif", Font.BOLD, 10
                    ));

                    setBorder(
                        new EmptyBorder(0, 11, 0, 5)
                    );

                    return this;
                }
            }
        );

        // Renderer nội dung
        DefaultTableCellRenderer cell =
            new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(
                        JTable t, Object v,
                        boolean selected, boolean focused,
                        int row, int col) {

                    super.getTableCellRendererComponent(
                        t, v, selected, focused, row, col
                    );

                    setBorder(
                        new EmptyBorder(0, 11, 0, 5)
                    );

                    if (col == 4) {

                        setForeground(
                            mauTrangThai(String.valueOf(v))
                        );

                    } else if (col == 5) {

                        setForeground(
                            new Color(160, 192, 250)
                        );
                    }

                    setHorizontalAlignment(
                        col == 5
                            ? SwingConstants.CENTER
                            : SwingConstants.LEFT
                    );

                    return this;
                }
            };

        for (int i = 0; i < 6; i++) {

            table.getColumnModel()
                 .getColumn(i)
                 .setCellRenderer(cell);
        }

        // Độ rộng cột
        table.getColumnModel().getColumn(0)
             .setPreferredWidth(175);

        table.getColumnModel().getColumn(1)
             .setPreferredWidth(150);

        table.getColumnModel().getColumn(2)
             .setPreferredWidth(170);

        table.getColumnModel().getColumn(3)
             .setPreferredWidth(130);

        table.getColumnModel().getColumn(4)
             .setPreferredWidth(90);

        table.getColumnModel().getColumn(5)
             .setMaxWidth(135);

        // Click vào cột Thao tác hoặc double-click dòng
        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row >= 0
                        && (col == 5 || e.getClickCount() == 2)) {

                    moChiTiet(
                        dangHienThi.get(
                            table.convertRowIndexToModel(row)
                        )
                    );
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);

        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD);

        list.add(scroll, BorderLayout.CENTER);

        page.add(list, BorderLayout.CENTER);

        add(page, BorderLayout.CENTER);

        // Sự kiện tìm kiếm
        txtTim.getDocument().addDocumentListener(
            new DocumentListener() {

                public void insertUpdate(DocumentEvent e) {
                    taiDanhSach();
                }

                public void removeUpdate(DocumentEvent e) {
                    taiDanhSach();
                }

                public void changedUpdate(DocumentEvent e) {
                    taiDanhSach();
                }
            }
        );

        cboTrangThai.addActionListener(e -> taiDanhSach());
    }

    // ==================== TẢI DANH SÁCH ====================

    private void taiDanhSach() {

        String q = txtTim.getText()
                .trim()
                .toLowerCase(Locale.ROOT);

        String state = String.valueOf(
            cboTrangThai.getSelectedItem()
        );

        model.setRowCount(0);
        dangHienThi.clear();

        int[] dem = new int[4];

        for (PhieuMau p : PHIEU) {

            // Nhân viên demo chỉ xem phiếu của Trần Thị Linh
            if (!laQuanLy
                    && !p.nguoiLap.equals("Trần Thị Linh")) {
                continue;
            }

            int idx = Arrays.asList(
                "Chờ duyệt",
                "Đã duyệt",
                "Từ chối",
                "Hoàn tất"
            ).indexOf(p.trangThai);

            if (idx >= 0) {
                dem[idx]++;
            }

            String search = (
                p.ma + " "
                + p.hoaDon.ma + " "
                + p.nguoiLienHe + " "
                + p.sdt
            ).toLowerCase(Locale.ROOT);

            if (!search.contains(q)
                    || (!state.startsWith("Tất cả")
                        && !state.equals(p.trangThai))) {

                continue;
            }

            dangHienThi.add(p);

            model.addRow(new Object[] {

                "<html><b>" + p.ma
                    + "</b><br><font color='#8F9BAD'>"
                    + p.hoaDon.ma
                    + "</font></html>",

                p.nguoiLienHe + " · " + p.sdt,

                p.huongXuLy + " · "
                    + p.soLuong() + " sản phẩm",

                tien(p.tienHoan()),

                p.trangThai,

                laQuanLy ? "Xem & duyệt" : "Chi tiết"
            });
        }

        for (int i = 0; i < 4; i++) {
            lblDem[i].setText(String.valueOf(dem[i]));
        }
    }

    // ==================== TRA CỨU HÓA ĐƠN ====================

    private void moTraCuu() {

        JDialog dialog = taoDialog(
            "Tra cứu giao dịch mua hàng", 850, 615
        );

        JPanel root = card(new BorderLayout());

        root.add(dialogHeader(
            "Tra cứu giao dịch mua hàng",
            "Chọn hóa đơn gốc còn trong thời hạn đổi/trả 7 ngày.",
            dialog
        ), BorderLayout.NORTH);

        JPanel center = card(new BorderLayout(0, 13));

        center.setBorder(
            new EmptyBorder(20, 22, 15, 22)
        );

        JTextField tim = new JTextField();

        inputStyle(tim);

        tim.setPreferredSize(new Dimension(0, 40));

        tim.setToolTipText(
            "Mã hóa đơn, tên hoặc SĐT khách hàng"
        );

        center.add(tim, BorderLayout.NORTH);

        JPanel lines = vertical();

        JScrollPane scroll = new JScrollPane(lines);

        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD);

        scroll.getVerticalScrollBar()
              .setUnitIncrement(18);

        center.add(scroll, BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);

        // Footer
        JButton close = button("Đóng", false);

        close.addActionListener(e -> dialog.dispose());

        JPanel footer = dialogFooter();

        footer.add(close);

        root.add(footer, BorderLayout.SOUTH);

        // Hiển thị kết quả tra cứu
        Runnable render = () -> {

            lines.removeAll();

            String q = tim.getText()
                    .trim()
                    .toLowerCase(Locale.ROOT);

            for (HoaDonMau hd : HOA_DON) {

                String hay = (
                    hd.ma + " "
                    + hd.khach + " "
                    + hd.sdt
                ).toLowerCase(Locale.ROOT);

                if (!hay.contains(q)) {
                    continue;
                }

                boolean hopLe = trong7Ngay(hd.ngay);

                JPanel item = card(
                    new BorderLayout(10, 0)
                );

                item.setBorder(new CompoundBorder(
                    new LineBorder(BORDER),
                    new EmptyBorder(14, 14, 14, 14)
                ));

                JPanel text = vertical();

                text.add(label(
                    hd.ma + (hopLe
                        ? " · Trong 7 ngày"
                        : " · Quá 7 ngày"),
                    13,
                    hopLe ? GREEN : MUTED,
                    Font.BOLD
                ));

                text.add(Box.createVerticalStrut(7));

                text.add(label(
                    hd.khach + " · "
                        + (hd.sdt.isBlank()
                            ? "Không có SĐT"
                            : hd.sdt),
                    11,
                    TEXT,
                    Font.PLAIN
                ));

                text.add(Box.createVerticalStrut(5));

                text.add(label(
                    NGAY.format(hd.ngay)
                        + " · " + tien(hd.tongTien()),
                    11,
                    MUTED,
                    Font.PLAIN
                ));

                item.add(text, BorderLayout.CENTER);

                JButton choose = button(
                    hopLe ? "→ Chọn" : "Hết hạn",
                    false
                );

                choose.setEnabled(hopLe);

                choose.addActionListener(e -> {
                    dialog.dispose();
                    moTiepNhan(hd, null);
                });

                item.add(choose, BorderLayout.EAST);

                addRow(lines, item, 102);
            }

            if (lines.getComponentCount() == 0) {

                addRow(lines, label(
                    "Không tìm thấy hóa đơn.",
                    12,
                    MUTED,
                    Font.PLAIN
                ), 40);
            }

            lines.revalidate();
            lines.repaint();
        };

        tim.getDocument().addDocumentListener(
            new DocumentListener() {

                public void insertUpdate(DocumentEvent e) {
                    render.run();
                }

                public void removeUpdate(DocumentEvent e) {
                    render.run();
                }

                public void changedUpdate(DocumentEvent e) {
                    render.run();
                }
            }
        );

        render.run();

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    // ==================== TIẾP NHẬN / SỬA ====================

    private void moTiepNhan(
            HoaDonMau hd,
            PhieuMau phieuCu) {

        if (laQuanLy) {
            return;
        }

        // Chỉ kiểm tra hạn khi tạo yêu cầu mới
        if (phieuCu == null && !trong7Ngay(hd.ngay)) {

            thongBao(
                "Hóa đơn đã quá hạn 7 ngày, "
                + "không thể lập phiếu mới."
            );

            return;
        }

        JDialog dialog = taoDialog(
            phieuCu == null
                ? "Tiếp nhận đổi / trả"
                : "Sửa phiếu đổi / trả",
            830,
            770
        );

        JPanel root = card(new BorderLayout());

        root.add(dialogHeader(
            phieuCu == null
                ? "Tiếp nhận đổi / trả"
                : "Sửa & gửi lại",
            hd.ma + " · Đã thanh toán "
                + NGAY.format(hd.ngay),
            dialog
        ), BorderLayout.NORTH);

        JPanel fields = vertical();

        fields.setBorder(
            new EmptyBorder(18, 23, 18, 23)
        );

        addRow(fields, notice(
            "Hóa đơn gốc được giữ nguyên. "
            + "Chọn đơn vị hàng cần đổi/trả."
        ), 53);

        // Danh sách sản phẩm có thể đổi/trả
        List<JCheckBox> checks = new ArrayList<>();
        List<JSpinner> spinners = new ArrayList<>();
        List<DongHoaDon> options = new ArrayList<>();

        for (DongHoaDon d : hd.dongs) {

            int available =
                d.soLuong - daYeuCau(d, phieuCu);

            if (available <= 0) {
                continue;
            }

            int oldQty = phieuCu == null
                ? 0
                : phieuCu.soLuongCua(d);

            JPanel line = card(
                new BorderLayout(10, 0)
            );

            line.setBorder(new CompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(11, 12, 11, 12)
            ));

            JCheckBox cb = new JCheckBox(
                d.ten + " · " + tien(d.giaThucTra)
            );

            cb.setOpaque(false);
            cb.setForeground(TEXT);
            cb.setSelected(oldQty > 0);

            line.add(cb, BorderLayout.CENTER);

            JSpinner qty = new JSpinner(
                new SpinnerNumberModel(
                    oldQty > 0 ? oldQty : 1,
                    1,
                    available,
                    1
                )
            );

            qty.setPreferredSize(
                new Dimension(63, 28)
            );

            qty.setEnabled(cb.isSelected());

            cb.addActionListener(e ->
                qty.setEnabled(cb.isSelected())
            );

            JPanel side = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 7, 0)
            );

            side.setOpaque(false);

            side.add(label(
                "Số lượng",
                11,
                MUTED,
                Font.PLAIN
            ));

            side.add(qty);

            line.add(side, BorderLayout.EAST);

            addRow(fields, line, 65);

            checks.add(cb);
            spinners.add(qty);
            options.add(d);
        }

        if (options.isEmpty()) {

            addRow(fields, label(
                "Không còn số lượng được phép tiếp nhận.",
                12,
                RED,
                Font.PLAIN
            ), 40);
        }

        // Hướng xử lý
        JComboBox<String> huong =
            new JComboBox<>(new String[] {
                "Trả hàng & hoàn tiền",
                "Đổi sản phẩm tương đương"
            });

        styleCombo(huong);

        if (phieuCu != null) {
            huong.setSelectedItem(phieuCu.huongXuLy);
        }

        addRow(fields, field(
            "Hướng xử lý *", huong
        ), 66);

        JLabel tienHoan = label(
            "Tiền hoàn dự kiến: 0 đ",
            16,
            new Color(154, 187, 249),
            Font.BOLD
        );

        addRow(fields, notice(
            "Tiền hoàn lấy theo đơn giá thực trả "
            + "của từng dòng hóa đơn "
            + "(đã gồm VAT và giảm giá)."
        ), 57);

        addRow(fields, tienHoan, 36);

        // Thông tin liên hệ
        JTextField lienHe = input(
            phieuCu == null
                ? hd.khach
                : phieuCu.nguoiLienHe
        );

        JTextField sdt = input(
            phieuCu == null
                ? hd.sdt
                : phieuCu.sdt
        );

        addRow(fields, pair(
            field("Người liên hệ *", lienHe),
            field("Số điện thoại *", sdt)
        ), 70);

        // Lý do và hiện trạng
        JTextArea lyDo = area(
            phieuCu == null ? "" : phieuCu.lyDo
        );

        JTextArea hienTrang = area(
            phieuCu == null ? "" : phieuCu.hienTrang
        );

        JTextField phuKien = input(
            phieuCu == null ? "" : phieuCu.phuKien
        );

        addRow(fields, field(
            "Lỗi / lý do yêu cầu *",
            new JScrollPane(lyDo)
        ), 102);

        addRow(fields, field(
            "Hiện trạng sản phẩm *",
            new JScrollPane(hienTrang)
        ), 102);

        addRow(fields, field(
            "Phụ kiện đi kèm * (ghi 'Không có' nếu không có)",
            phuKien
        ), 69);

        addRow(fields, notice(
            "Phiếu gửi lên sẽ ở trạng thái Chờ duyệt. "
            + "Chưa hoàn tiền, cập nhật hàng hay điểm."
        ), 57);

        // Cập nhật tiền hoàn khi thay đổi sản phẩm
        Runnable capNhatTien = () -> {

            long value = 0;

            if (huong.getSelectedIndex() == 0) {

                for (int i = 0; i < options.size(); i++) {

                    if (checks.get(i).isSelected()) {

                        value += options.get(i).giaThucTra
                            * (Integer) spinners.get(i).getValue();
                    }
                }
            }

            tienHoan.setText(
                "Tiền hoàn dự kiến: " + tien(value)
            );
        };

        huong.addActionListener(e -> capNhatTien.run());

        for (JCheckBox c : checks) {

            c.addActionListener(e -> capNhatTien.run());
        }

        for (JSpinner s : spinners) {

            s.addChangeListener(e -> capNhatTien.run());
        }

        capNhatTien.run();

        // Vùng cuộn
        JScrollPane content = new JScrollPane(fields);

        content.setBorder(null);
        content.getViewport().setBackground(CARD);

        content.getVerticalScrollBar()
               .setUnitIncrement(18);

        root.add(content, BorderLayout.CENTER);

        // Footer
        JPanel footer = dialogFooter();

        JButton cancel = button("Quay lại", false);
        JButton send = button("Gửi quản lý duyệt", true);

        cancel.addActionListener(e -> dialog.dispose());

        // ==================== SỰ KIỆN GỬI PHIẾU ====================

        send.addActionListener(e -> {

            List<DongTra> chiTiet = new ArrayList<>();

            for (int i = 0; i < options.size(); i++) {

                if (checks.get(i).isSelected()) {

                    chiTiet.add(new DongTra(
                        options.get(i),
                        (Integer) spinners.get(i).getValue()
                    ));
                }
            }

            if (chiTiet.isEmpty()) {

                thongBao(
                    "Hãy chọn ít nhất một sản phẩm."
                );

                return;
            }

            String tenLienHe = lienHe.getText().trim();
            String soDienThoai = sdt.getText().trim();

            if (tenLienHe.isEmpty()
                    || !soDienThoai.matches("0\\d{9}")
                    || lyDo.getText().trim().isEmpty()
                    || hienTrang.getText().trim().isEmpty()
                    || phuKien.getText().trim().isEmpty()) {

                thongBao(
                    "Nhập đủ liên hệ, SĐT 10 số, "
                    + "lý do, hiện trạng và phụ kiện."
                );

                return;
            }

            PhieuMau p = phieuCu;

            if (p == null) {

                p = new PhieuMau(
                    String.format(
                        "DT%06d",
                        maTiepTheo++
                    ),
                    hd
                );

                // Tài khoản demo
                p.nguoiLap = "Trần Thị Linh";

                PHIEU.add(p);
            }

            p.chiTiet.clear();
            p.chiTiet.addAll(chiTiet);

            p.nguoiLienHe = tenLienHe;
            p.sdt = soDienThoai;

            p.lyDo = lyDo.getText().trim();
            p.hienTrang = hienTrang.getText().trim();
            p.phuKien = phuKien.getText().trim();

            p.huongXuLy =
                String.valueOf(huong.getSelectedItem());

            p.trangThai = "Chờ duyệt";
            p.nguoiDuyet = "";

            taiDanhSach();

            dialog.dispose();

            thongBao(
                "Đã gửi phiếu " + p.ma
                + " (demo). Chưa phát sinh hoàn tiền thật."
            );
        });

        footer.add(cancel);
        footer.add(send);

        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    // ==================== CHI TIẾT PHIẾU ====================

    private void moChiTiet(PhieuMau p) {

        JDialog dialog = taoDialog(
            "Phiếu " + p.ma,
            860,
            750
        );

        JPanel root = card(new BorderLayout());

        root.add(dialogHeader(
            "Phiếu " + p.ma,
            p.trangThai + " · " + p.huongXuLy,
            dialog
        ), BorderLayout.NORTH);

        JPanel details = vertical();

        details.setBorder(
            new EmptyBorder(21, 24, 25, 24)
        );

        // Logo phiếu
        JPanel brandArea = card(
            new GridLayout(2, 1)
        );

        brandArea.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        JLabel logo = label(
            "GREEN HOME",
            18,
            TEXT,
            Font.BOLD
        );

        logo.setHorizontalAlignment(
            SwingConstants.CENTER
        );

        JLabel sub = label(
            "PHIẾU ĐỔI / TRẢ · "
                + p.ma + " · " + p.trangThai,
            11,
            MUTED,
            Font.PLAIN
        );

        sub.setHorizontalAlignment(
            SwingConstants.CENTER
        );

        brandArea.add(logo);
        brandArea.add(sub);

        addRow(details, brandArea, 65);

        // Trạng thái quy trình
        JPanel stages = card(
            new GridLayout(1, 3)
        );

        stages.add(label(
            "① Tiếp nhận",
            12,
            GREEN,
            Font.PLAIN
        ));

        stages.add(label(
            "② " + (
                p.trangThai.equals("Chờ duyệt")
                    ? "Chờ duyệt"
                    : "Đã xét duyệt"
            ),
            12,
            MUTED,
            Font.PLAIN
        ));

        stages.add(label(
            "③ Hoàn tất",
            12,
            MUTED,
            Font.PLAIN
        ));

        addRow(details, stages, 36);

        addDivider(details);

        // Thông tin phiếu
        addRow(details, pair(
            info("Hóa đơn gốc", p.hoaDon.ma),
            info(
                "Người liên hệ",
                p.nguoiLienHe + " · " + p.sdt
            )
        ), 65);

        addRow(details, pair(
            info("Người lập / tiếp nhận", p.nguoiLap),
            info(
                "Người duyệt",
                p.nguoiDuyet.isBlank()
                    ? "Chưa được duyệt"
                    : p.nguoiDuyet
            )
        ), 65);

        addDivider(details);

        // Sản phẩm đổi/trả
        for (DongTra ct : p.chiTiet) {

            addRow(details, info(
                ct.dong.ten,
                "Số lượng " + ct.soLuong
                    + " · Đơn giá thực trả "
                    + tien(ct.dong.giaThucTra)
            ), 63);
        }

        addDivider(details);

        // Lý do
        addRow(details, info(
            "Lỗi / lý do",
            p.lyDo
        ), 58);

        addRow(details, pair(
            info("Hiện trạng", p.hienTrang),
            info("Phụ kiện", p.phuKien)
        ), 64);

        addDivider(details);

        // Tiền hoàn
        addRow(details, info(
            "Tiền hoàn dự kiến",
            tien(p.tienHoan())
        ), 63);

        String diemDuKien =
            p.huongXuLy.startsWith("Trả")
                ? "−" + p.diemThuHoiDuKien() + " điểm"
                : "0 điểm";

        addRow(details, info(
            "Điểm điều chỉnh dự kiến (demo)",
            diemDuKien
        ), 63);

        addRow(details, notice(
            "Mọi số liệu hoàn tiền/điểm chỉ là dự kiến. "
            + "Chưa cập nhật CSDL."
        ), 57);

        // Scroll
        JScrollPane scroll = new JScrollPane(details);

        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD);

        scroll.getVerticalScrollBar()
              .setUnitIncrement(18);

        root.add(scroll, BorderLayout.CENTER);

        // ==================== FOOTER THEO VAI TRÒ ====================

        JPanel footer = dialogFooter();

        JButton close = button("Đóng", false);

        close.addActionListener(e -> dialog.dispose());

        footer.add(close);

        // Quản lý xét duyệt
        if (laQuanLy
                && p.trangThai.equals("Chờ duyệt")) {

            JButton reject = button("Từ chối", false);
            JButton approve = button("Phê duyệt", true);

            reject.addActionListener(e ->
                thayDoiTrangThai(dialog, p, "Từ chối")
            );

            approve.addActionListener(e ->
                thayDoiTrangThai(dialog, p, "Đã duyệt")
            );

            footer.add(reject);
            footer.add(approve);

        } else if (!laQuanLy
                && (p.trangThai.equals("Chờ duyệt")
                    || p.trangThai.equals("Từ chối"))) {

            // Nhân viên sửa và gửi lại
            JButton edit = button(
                "Sửa & gửi lại",
                true
            );

            edit.addActionListener(e -> {
                dialog.dispose();
                moTiepNhan(p.hoaDon, p);
            });

            footer.add(edit);

        } else if (!laQuanLy
                && p.trangThai.equals("Đã duyệt")) {

            // Chỉ mô phỏng hoàn tất, chưa xử lý tiền/hàng.
            JButton done = button(
                "Hoàn tất (demo)",
                true
            );

            done.addActionListener(e -> {

                int ok = JOptionPane.showConfirmDialog(
                    dialog,
                    "Chỉ đổi trạng thái demo; "
                    + "không chuyển tiền hay nhập hàng thật. "
                    + "Tiếp tục?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
                );

                if (ok == JOptionPane.YES_OPTION) {

                    p.trangThai = "Hoàn tất";

                    taiDanhSach();
                    dialog.dispose();
                }
            });

            footer.add(done);
        }

        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    // ==================== PHÊ DUYỆT / TỪ CHỐI ====================

    private void thayDoiTrangThai(
            JDialog dialog,
            PhieuMau p,
            String status) {

        int answer = JOptionPane.showConfirmDialog(
            dialog,
            "Chuyển phiếu " + p.ma
                + " sang '" + status + "' (demo)?",
            "Xác nhận",
            JOptionPane.YES_NO_OPTION
        );

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        p.trangThai = status;

        // Tài khoản quản lý demo
        p.nguoiDuyet = "Trần Minh Quân";

        taiDanhSach();
        dialog.dispose();

        thongBao(
            "Đã " + status.toLowerCase(Locale.ROOT)
                + " phiếu (demo). Chưa hoàn tiền thật."
        );
    }

    // ==================== KIỂM TRA SỐ LƯỢNG ====================

    private static int daYeuCau(
            DongHoaDon line,
            PhieuMau dangSua) {

        int reserved = 0;

        for (PhieuMau p : PHIEU) {

            if (p == dangSua
                    || p.trangThai.equals("Từ chối")) {

                continue;
            }

            reserved += p.soLuongCua(line);
        }

        return reserved;
    }

    // ==================== KIỂM TRA 7 NGÀY ====================

    private static boolean trong7Ngay(LocalDate date) {

        long soNgay = ChronoUnit.DAYS.between(
            date,
            LocalDate.now()
        );

        return soNgay >= 0 && soNgay <= 7;
    }

    // ==================== THẺ THỐNG KÊ ====================

    private JPanel stat(
            String title,
            JLabel count,
            Color color) {

        JPanel panel = card(new BorderLayout());

        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(13, 13, 13, 13)
        ));

        JPanel text = vertical();

        text.add(label(
            title, 11, MUTED, Font.PLAIN
        ));

        text.add(Box.createVerticalStrut(8));
        text.add(count);
        text.add(Box.createVerticalStrut(7));

        text.add(label(
            "Phiếu đổi / trả",
            10,
            MUTED,
            Font.PLAIN
        ));

        panel.add(text, BorderLayout.CENTER);

        panel.add(label(
            "●", 15, color, Font.BOLD
        ), BorderLayout.EAST);

        return panel;
    }

    // ==================== HỘP THOẠI ====================

    private JDialog taoDialog(
            String name,
            int width,
            int height) {

        Window owner =
            SwingUtilities.getWindowAncestor(this);

        JDialog d = new JDialog(
            owner,
            name,
            Dialog.ModalityType.APPLICATION_MODAL
        );

        d.setSize(width, height);

        d.setMinimumSize(
            new Dimension(630, 490)
        );

        d.setDefaultCloseOperation(
            JDialog.DISPOSE_ON_CLOSE
        );

        d.setLocationRelativeTo(owner);

        return d;
    }

    private static JPanel dialogHeader(
            String title,
            String sub,
            JDialog dialog) {

        JPanel head = card(new BorderLayout());

        head.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(17, 23, 17, 23)
        ));

        JPanel labels = vertical();

        labels.add(label(
            title, 21, TEXT, Font.BOLD
        ));

        labels.add(Box.createVerticalStrut(7));

        labels.add(label(
            sub, 12, MUTED, Font.PLAIN
        ));

        head.add(labels, BorderLayout.WEST);

        JButton x = button("×", false);

        x.addActionListener(e -> dialog.dispose());

        head.add(x, BorderLayout.EAST);

        return head;
    }

    private static JPanel dialogFooter() {

        JPanel footer = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 12)
        );

        footer.setBackground(
            new Color(29, 31, 35)
        );

        footer.setBorder(
            new MatteBorder(1, 0, 0, 0, BORDER)
        );

        return footer;
    }

    // ==================== COMPONENT DÙNG CHUNG ====================

    private static JPanel card(LayoutManager layout) {

        JPanel p = new JPanel(layout);
        p.setBackground(CARD);

        return p;
    }

    private static JPanel vertical() {

        JPanel p = new JPanel();

        p.setOpaque(false);

        p.setLayout(
            new BoxLayout(p, BoxLayout.Y_AXIS)
        );

        return p;
    }

    private static JLabel label(
            String value,
            int size,
            Color color,
            int weight) {

        JLabel l = new JLabel(value);

        l.setForeground(color);

        l.setFont(
            new Font("SansSerif", weight, size)
        );

        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        return l;
    }

    private static JButton button(
            String title,
            boolean primary) {

        JButton b = new JButton(title);

        b.setFont(
            new Font("SansSerif", Font.BOLD, 11)
        );

        b.setForeground(Color.WHITE);

        b.setBackground(
            primary ? BLUE : new Color(29, 34, 43)
        );

        b.setFocusPainted(false);

        b.setBorder(new CompoundBorder(
            new LineBorder(primary ? BLUE : BORDER),
            new EmptyBorder(10, 12, 10, 12)
        ));

        b.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        return b;
    }

    private static JTextField input(String value) {

        JTextField f = new JTextField(value);

        inputStyle(f);

        return f;
    }

    private static void inputStyle(JTextField f) {

        f.setBackground(
            new Color(23, 25, 28)
        );

        f.setForeground(TEXT);
        f.setCaretColor(TEXT);

        f.setFont(
            new Font("SansSerif", Font.PLAIN, 12)
        );

        f.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(8, 11, 8, 11)
        ));
    }

    private static void styleCombo(
            JComboBox<String> combo) {

        combo.setBackground(
            new Color(23, 25, 28)
        );

        combo.setForeground(TEXT);

        combo.setPreferredSize(
            new Dimension(200, 36)
        );
    }

    private static JTextArea area(String initial) {

        JTextArea a = new JTextArea(
            initial, 3, 10
        );

        a.setBackground(
            new Color(23, 25, 28)
        );

        a.setForeground(TEXT);
        a.setCaretColor(TEXT);

        a.setLineWrap(true);
        a.setWrapStyleWord(true);

        a.setFont(
            new Font("SansSerif", Font.PLAIN, 12)
        );

        a.setBorder(
            new EmptyBorder(8, 10, 8, 10)
        );

        return a;
    }

    private static JPanel field(
            String title,
            JComponent value) {

        JPanel p = new JPanel(
            new BorderLayout(0, 7)
        );

        p.setOpaque(false);

        p.add(label(
            title, 12, MUTED, Font.BOLD
        ), BorderLayout.NORTH);

        p.add(value, BorderLayout.CENTER);

        return p;
    }

    private static JPanel pair(
            JPanel left,
            JPanel right) {

        JPanel p = new JPanel(
            new GridLayout(1, 2, 14, 0)
        );

        p.setOpaque(false);

        p.add(left);
        p.add(right);

        return p;
    }

    private static JPanel info(
            String title,
            String value) {

        JPanel p = vertical();

        p.add(label(
            title, 11, MUTED, Font.PLAIN
        ));

        p.add(Box.createVerticalStrut(7));

        p.add(label(
            value,
            12,
            new Color(159, 185, 226),
            Font.BOLD
        ));

        return p;
    }

    private static JPanel notice(String message) {

        JPanel p = card(new BorderLayout());

        p.setBackground(
            new Color(31, 37, 46)
        );

        p.setBorder(
            new EmptyBorder(12, 12, 12, 12)
        );

        p.add(label(
            "ⓘ  " + message,
            11,
            MUTED,
            Font.PLAIN
        ), BorderLayout.WEST);

        return p;
    }

    private static void addDivider(JPanel parent) {

        JSeparator line = new JSeparator();

        line.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        line.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 1)
        );

        parent.add(line);
    }

    private static void addRow(
            JPanel parent,
            JComponent row,
            int height) {

        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, height)
        );

        row.setPreferredSize(
            new Dimension(640, height)
        );

        parent.add(row);
        parent.add(Box.createVerticalStrut(12));
    }

    private static Color mauTrangThai(String status) {

        switch (status) {

            case "Chờ duyệt":
                return GOLD;

            case "Đã duyệt":
                return BLUE;

            case "Từ chối":
                return RED;

            default:
                return GREEN;
        }
    }

    private static String tien(long amount) {

        return NumberFormat.getIntegerInstance(
            Locale.forLanguageTag("vi-VN")
        ).format(amount) + " đ";
    }

    private void thongBao(String message) {

        JOptionPane.showMessageDialog(
            this,
            message,
            "Green Home - Dữ liệu demo",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ==================== LỚP DỮ LIỆU TẠM ====================

    private static class HoaDonMau {

        final String ma;
        final String khach;
        final String sdt;
        final LocalDate ngay;

        final List<DongHoaDon> dongs =
                new ArrayList<>();

        HoaDonMau(
                String ma,
                String khach,
                String sdt,
                LocalDate ngay) {

            this.ma = ma;
            this.khach = khach;
            this.sdt = sdt;
            this.ngay = ngay;
        }

        long tongTien() {

            long sum = 0;

            for (DongHoaDon d : dongs) {
                sum += d.giaThucTra * d.soLuong;
            }

            return sum;
        }
    }

    private static class DongHoaDon {

        final String ma;
        final String ten;
        final int soLuong;
        final long giaThucTra;

        DongHoaDon(
                String ma,
                String ten,
                int soLuong,
                long giaThucTra) {

            this.ma = ma;
            this.ten = ten;
            this.soLuong = soLuong;
            this.giaThucTra = giaThucTra;
        }
    }

    private static class DongTra {

        final DongHoaDon dong;
        final int soLuong;

        DongTra(DongHoaDon dong, int soLuong) {

            this.dong = dong;
            this.soLuong = soLuong;
        }
    }

    private static class PhieuMau {

        final String ma;
        final HoaDonMau hoaDon;

        final LocalDateTime ngayLap =
                LocalDateTime.now();

        String nguoiLap = "";
        String nguoiDuyet = "";

        String nguoiLienHe = "";
        String sdt = "";

        String huongXuLy = "Trả hàng & hoàn tiền";

        String lyDo = "";
        String hienTrang = "";
        String phuKien = "";

        String trangThai = "Chờ duyệt";

        final List<DongTra> chiTiet =
                new ArrayList<>();

        PhieuMau(
                String ma,
                HoaDonMau hoaDon) {

            this.ma = ma;
            this.hoaDon = hoaDon;
        }

        int soLuong() {

            int sum = 0;

            for (DongTra d : chiTiet) {
                sum += d.soLuong;
            }

            return sum;
        }

        int soLuongCua(DongHoaDon dong) {

            int n = 0;

            for (DongTra d : chiTiet) {

                if (d.dong == dong) {
                    n += d.soLuong;
                }
            }

            return n;
        }

        long tienHoan() {

            if (!huongXuLy.startsWith("Trả")) {
                return 0;
            }

            long sum = 0;

            for (DongTra d : chiTiet) {

                sum += d.dong.giaThucTra * d.soLuong;
            }

            return sum;
        }

        long diemThuHoiDuKien() {

            // Công thức minh họa, không phải quy định thật
            return Math.round(tienHoan() / 100000.0);
        }
    }

    // ==================== CHẠY THỬ ====================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame(
                "Green Home - Đổi / trả (demo)"
            );

            frame.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
            );

            frame.setSize(1150, 740);
            frame.setLocationRelativeTo(null);

            // false = Nhân viên; true = Quản lý
            frame.setContentPane(
                new DoiTraPanel(false)
            );

            frame.setVisible(true);
        });
    }
}