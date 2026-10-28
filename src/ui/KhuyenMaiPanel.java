package ui;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class KhuyenMaiPanel extends JPanel {

    // ==================== MAU SAC ====================

    private static final Color BG = new Color(30, 33, 37);
    private static final Color CARD = new Color(18, 19, 21);
    private static final Color BORDER = new Color(57, 61, 67);
    private static final Color TEXT = new Color(225, 232, 242);
    private static final Color MUTED = new Color(143, 155, 173);
    private static final Color BLUE = new Color(54, 111, 232);
    private static final Color GREEN = new Color(60, 177, 124);

    private static final DateTimeFormatter DATE =
        DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    // ==================== DU LIEU ====================

    private final boolean laQuanLy;

    private boolean tabCaNhan = false;

    private final List<ChuongTrinh> ds = new ArrayList<>();

    private final List<ChuongTrinh> dangXem =
        new ArrayList<>();

    private final String[][] sanPham = {
        {"SP0001", "iPhone 15"},
        {"SP0002", "Dell Inspiron 15"},
        {"SP0003", "Samsung Crystal UHD"},
        {"SP0004", "Tai nghe Bluetooth"},
        {"SP0005", "Tủ lạnh Panasonic"},
        {"SP0006", "Máy giặt LG Inverter"}
    };

    private final JTextField txtTim = new JTextField();

    private final JLabel lblDem =
        label("0 chương trình", 11, MUTED, Font.PLAIN);

    private final DefaultTableModel model =
        new DefaultTableModel() {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

    private final JTable table = new JTable(model);

    private JButton tabSanPham;
    private JButton tabUuDai;

    private int soKm = 4;

    // ==================== CONSTRUCTOR ====================

    public KhuyenMaiPanel(boolean laQuanLy) {

        this.laQuanLy = laQuanLy;

        setLayout(new BorderLayout());
        setBackground(BG);

        taoDuLieuMau();
        taoGiaoDien();
        taiBang();
    }

    // ==================== DU LIEU MAU ====================

    private void taoDuLieuMau() {

        ChuongTrinh a = new ChuongTrinh(
            "KM0001",
            "Ưu đãi điện máy 5%",
            false
        );

        a.mucGiam = 5;

        a.sanPhams.addAll(
            Arrays.asList("SP0001", "SP0002", "SP0005")
        );

        ds.add(a);

        ChuongTrinh b = new ChuongTrinh(
            "KM0002",
            "Ưu đãi điện máy 10%",
            false
        );

        b.mucGiam = 10;
        b.sanPhams.add("SP0003");

        ds.add(b);

        ChuongTrinh c = new ChuongTrinh(
            "KM0003",
            "Ưu đãi điện máy 5%",
            false
        );

        c.mucGiam = 5;
        c.sanPhams.add("SP0006");

        ds.add(c);

        // Ưu đãi cá nhân mẫu

        ChuongTrinh d = new ChuongTrinh(
            "MAI5",
            "Ưu đãi cá nhân Mai 5%",
            true
        );

        d.maKhach = "KH000000002";
        d.mucGiam = 5;
        d.toiThieu = 500000;
        d.toiDa = 300000;

        ds.add(d);

        ChuongTrinh e = new ChuongTrinh(
            "AN10",
            "Ưu đãi cá nhân An 10%",
            true
        );

        e.maKhach = "KH000000001";
        e.mucGiam = 10;
        e.toiThieu = 1000000;
        e.toiDa = 500000;

        ds.add(e);
    }

    // ==================== GIAO DIEN CHINH ====================

    private void taoGiaoDien() {

        JPanel content = new JPanel(
            new BorderLayout(0, 14)
        );

        content.setBackground(BG);

        content.setBorder(
            new EmptyBorder(24, 27, 24, 27)
        );

        JPanel top = new JPanel(
            new BorderLayout(0, 13)
        );

        top.setOpaque(false);

        // -------- TIEU DE --------

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);

        JPanel title = vertical();

        title.add(label(
            "Khuyến mãi & ưu đãi",
            21,
            TEXT,
            Font.BOLD
        ));

        title.add(Box.createVerticalStrut(5));

        title.add(label(
            "Thiết lập khuyến mãi sản phẩm và mã ưu đãi cá nhân theo khách hàng.",
            11,
            MUTED,
            Font.PLAIN
        ));

        heading.add(title, BorderLayout.WEST);

        if (laQuanLy) {

            JButton them = button(
                "+  Tạo chương trình", true
            );

            them.addActionListener(e -> moForm(null));

            heading.add(them, BorderLayout.EAST);
        }

        top.add(heading, BorderLayout.NORTH);

        // -------- THONG BAO --------

        JPanel notice = new JPanel(new BorderLayout());

        notice.setBackground(
            new Color(31, 37, 48)
        );

        notice.setBorder(new CompoundBorder(
            new LineBorder(new Color(51, 65, 86)),
            new EmptyBorder(12, 14, 12, 14)
        ));

        notice.add(label(
            "◇  Khuyến mãi sản phẩm → Mã ưu đãi cá nhân → Điểm tích lũy",
            11,
            MUTED,
            Font.PLAIN
        ), BorderLayout.WEST);

        notice.add(label(
            "Mỗi sản phẩm chỉ áp dụng mức giảm hợp lệ tốt nhất",
            10,
            MUTED,
            Font.PLAIN
        ), BorderLayout.EAST);

        top.add(notice, BorderLayout.SOUTH);

        content.add(top, BorderLayout.NORTH);

        // ==================== KHUNG DANH SACH ====================

        JPanel card = new JPanel(new BorderLayout());

        card.setBackground(CARD);
        card.setBorder(new LineBorder(BORDER));

        JPanel controls = new JPanel(new BorderLayout());
        controls.setOpaque(false);

        // -------- TABS --------

        JPanel tabs = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 9, 9)
        );

        tabs.setOpaque(false);

        tabSanPham = button("Theo sản phẩm", false);
        tabUuDai = button("Ưu đãi cá nhân", false);

        tabSanPham.addActionListener(e -> doiTab(false));
        tabUuDai.addActionListener(e -> doiTab(true));

        tabs.add(tabSanPham);
        tabs.add(tabUuDai);

        controls.add(tabs, BorderLayout.NORTH);

        // -------- TIM KIEM --------

        JPanel search = new JPanel(
            new BorderLayout(10, 0)
        );

        search.setOpaque(false);

        search.setBorder(
            new EmptyBorder(3, 12, 12, 12)
        );

        styleInput(txtTim);

        txtTim.setToolTipText(
            "Tìm theo mã, tên chương trình, sản phẩm hoặc khách hàng"
        );

        JPanel searchBox = new JPanel(new BorderLayout());

        searchBox.setOpaque(false);

        searchBox.setPreferredSize(
            new Dimension(345, 35)
        );

        searchBox.add(txtTim, BorderLayout.CENTER);

        search.add(searchBox, BorderLayout.WEST);
        search.add(lblDem, BorderLayout.EAST);

        controls.add(search, BorderLayout.SOUTH);

        card.add(controls, BorderLayout.NORTH);

        // ==================== BANG ====================

        table.setRowHeight(57);

        table.setBackground(CARD);
        table.setForeground(TEXT);

        table.setSelectionBackground(
            new Color(36, 48, 69)
        );

        table.setSelectionForeground(TEXT);

        table.setFont(
            new Font("SansSerif", Font.PLAIN, 11)
        );

        table.setShowGrid(false);
        table.setFillsViewportHeight(true);

        table.getTableHeader().setPreferredSize(
            new Dimension(0, 34)
        );

        table.getTableHeader().setReorderingAllowed(false);

        // -------- HEADER BANG --------

        table.getTableHeader().setDefaultRenderer(
            new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(
                        JTable t,
                        Object v,
                        boolean selected,
                        boolean focus,
                        int row,
                        int col) {

                    super.getTableCellRendererComponent(
                        t, v, selected, focus, row, col
                    );

                    setBackground(new Color(28, 30, 33));
                    setForeground(MUTED);

                    setFont(
                        new Font("SansSerif", Font.BOLD, 10)
                    );

                    setBorder(
                        new EmptyBorder(0, 11, 0, 6)
                    );

                    return this;
                }
            }
        );

        // -------- CLICK SUA --------

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (laQuanLy && row >= 0 && col == 5) {
                    moForm(dangXem.get(row));
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);

        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD);

        card.add(scroll, BorderLayout.CENTER);

        content.add(card, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);

        // -------- SU KIEN TIM KIEM --------

        txtTim.getDocument().addDocumentListener(
            new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    taiBang();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    taiBang();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    taiBang();
                }
            }
        );
    }

    // ==================== DOI TAB ====================

    private void doiTab(boolean caNhan) {

        tabCaNhan = caNhan;

        txtTim.setText("");

        taiBang();
    }

    // ==================== TAI DANH SACH ====================

    private void taiBang() {

        tabSanPham.setForeground(
            tabCaNhan ? MUTED : new Color(157, 190, 250)
        );

        tabUuDai.setForeground(
            tabCaNhan ? new Color(157, 190, 250) : MUTED
        );

        String[] columns = tabCaNhan
            ? new String[] {
                "Chương trình",
                "Khách hàng",
                "Mức giảm",
                "Hiệu lực",
                "Trạng thái",
                "Thao tác"
            }
            : new String[] {
                "Chương trình",
                "Phạm vi sản phẩm",
                "Mức giảm",
                "Hiệu lực",
                "Trạng thái",
                "Thao tác"
            };

        model.setColumnIdentifiers(columns);
        model.setRowCount(0);

        dangXem.clear();

        String q = txtTim.getText()
                .trim()
                .toLowerCase(Locale.ROOT);

        for (ChuongTrinh km : ds) {

            if (km.caNhan != tabCaNhan) {
                continue;
            }

            String hay = (
                km.ma + " "
                + km.ten + " "
                + km.maKhach + " "
                + tenSanPhams(km)
            ).toLowerCase(Locale.ROOT);

            if (!hay.contains(q)) {
                continue;
            }

            dangXem.add(km);

            String name =
                "<html><b>" + html(km.ten)
                + "</b><br><font color='#8F9BAD'>"
                + html(km.ma)
                + "</font></html>";

            String scope = km.caNhan
                ? km.maKhach
                : km.sanPhams.size()
                  + " sản phẩm · " + tenSanPhams(km);

            String discount =
                "Phần trăm".equals(km.kieu)
                ? km.mucGiam + "%"
                : tien(km.mucGiam);

            String dates =
                "<html>"
                + DATE.format(km.tu)
                + "<br>đến "
                + DATE.format(km.den)
                + "</html>";

            model.addRow(new Object[] {
                name,
                scope,
                discount,
                dates,
                trangThai(km),
                laQuanLy ? "Sửa" : ""
            });
        }

        lblDem.setText(
            dangXem.size()
            + (tabCaNhan
                ? " ưu đãi cá nhân"
                : " chương trình")
        );

        // -------- CAN LE BANG --------

        DefaultTableCellRenderer cell =
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

                    setBorder(
                        new EmptyBorder(0, 11, 0, 6)
                    );

                    setHorizontalAlignment(
                        col == 5
                        ? SwingConstants.CENTER
                        : SwingConstants.LEFT
                    );

                    if (col == 4) {
                        setForeground(GREEN);
                    } else if (col == 5) {
                        setForeground(new Color(157, 190, 250));
                    }

                    return this;
                }
            };

        for (int i = 0; i < table.getColumnCount(); i++) {

            table.getColumnModel()
                 .getColumn(i)
                 .setCellRenderer(cell);
        }

        table.getColumnModel()
             .getColumn(0).setPreferredWidth(190);

        table.getColumnModel()
             .getColumn(1).setPreferredWidth(270);

        table.getColumnModel()
             .getColumn(2).setPreferredWidth(125);

        table.getColumnModel()
             .getColumn(3).setPreferredWidth(125);

        table.getColumnModel()
             .getColumn(4).setPreferredWidth(105);

        table.getColumnModel()
             .getColumn(5).setMaxWidth(80);
    }

    // ==================== TEN SAN PHAM ====================

    private String tenSanPhams(ChuongTrinh km) {

        List<String> names = new ArrayList<>();

        for (String[] p : sanPham) {

            if (km.sanPhams.contains(p[0])) {
                names.add(p[1]);
            }
        }

        return String.join(", ", names);
    }

    // ==================== TRANG THAI ====================

    private String trangThai(ChuongTrinh km) {

        LocalDate now = LocalDate.now();

        if (km.daDung) {
            return "Đã sử dụng";
        }

        if (!km.bat) {
            return "Tạm ngừng";
        }

        if (now.isBefore(km.tu)) {
            return "Chưa bắt đầu";
        }

        if (now.isAfter(km.den)) {
            return "Hết hạn";
        }

        return "Đang áp dụng";
    }

    // ==================== FORM THEM / SUA ====================

    private void moForm(ChuongTrinh cu) {

        if (!laQuanLy) {
            return;
        }

        boolean sua = cu != null;

        boolean caNhan = sua ? cu.caNhan : tabCaNhan;

        Window owner =
            SwingUtilities.getWindowAncestor(this);

        JDialog dialog = new JDialog(
            owner,
            sua ? "Cập nhật chương trình" : "Tạo chương trình",
            Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setSize(
            750,
            caNhan ? 730 : 790
        );

        dialog.setMinimumSize(
            new Dimension(620, 550)
        );

        dialog.setLocationRelativeTo(owner);

        dialog.setDefaultCloseOperation(
            JDialog.DISPOSE_ON_CLOSE
        );

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CARD);

        // -------- HEADER --------

        JPanel head = new JPanel(new BorderLayout());

        head.setBackground(CARD);

        head.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(19, 25, 19, 25)
        ));

        JPanel title = vertical();

        title.add(label(
            sua ? "Cập nhật chương trình" : "Tạo chương trình",
            22,
            TEXT,
            Font.BOLD
        ));

        title.add(Box.createVerticalStrut(7));

        title.add(label(
            caNhan
                ? "Mỗi mã cá nhân chỉ sử dụng một lần."
                : "Nếu trùng khuyến mãi, áp dụng mức giảm có lợi nhất cho sản phẩm.",
            12,
            MUTED,
            Font.PLAIN
        ));

        head.add(title, BorderLayout.WEST);

        JButton x = button("×", false);

        x.addActionListener(e -> dialog.dispose());

        head.add(x, BorderLayout.EAST);

        root.add(head, BorderLayout.NORTH);

        // ==================== CAC TRUONG NHAP ====================

        JPanel fields = vertical();

        fields.setBorder(
            new EmptyBorder(20, 25, 20, 25)
        );

        JTextField ten = input(
            sua ? cu.ten : ""
        );

        addField(
            fields,
            "Tên chương trình *",
            ten,
            67
        );

        JTextField maUuDai = input(
            sua ? cu.ma : ""
        );

        JTextField maKhach = input(
            sua ? cu.maKhach : ""
        );

        JTextField toiThieu = input(
            sua ? String.valueOf(cu.toiThieu) : "500000"
        );

        JTextField toiDa = input(
            sua ? String.valueOf(cu.toiDa) : "300000"
        );

        JComboBox<String> kieu =
            new JComboBox<>(new String[] {
                "Phần trăm",
                "Số tiền"
            });

        styleCombo(kieu);

        kieu.setSelectedItem(
            sua ? cu.kieu : "Phần trăm"
        );

        JTextField mucGiam = input(
            sua ? String.valueOf(cu.mucGiam) : ""
        );

        // -------- FORM UU DAI CA NHAN --------

        if (caNhan) {

            addField(
                fields,
                "Mã ưu đãi *",
                maUuDai,
                67
            );

            maUuDai.setEditable(!sua);

            addField(
                fields,
                "Mã khách hàng áp dụng *",
                maKhach,
                67
            );

            addField(
                fields,
                "Giảm theo đơn hàng (%) *",
                mucGiam,
                67
            );

            JPanel pair = new JPanel(
                new GridLayout(1, 2, 12, 0)
            );

            pair.setOpaque(false);

            pair.add(field(
                "Đơn hàng tối thiểu (VNĐ) *",
                toiThieu
            ));

            pair.add(field(
                "Giảm tối đa (VNĐ) *",
                toiDa
            ));

            addRow(fields, pair, 70);

        } else {

            // -------- FORM KHUYEN MAI SAN PHAM --------

            JPanel pair = new JPanel(
                new GridLayout(1, 2, 12, 0)
            );

            pair.setOpaque(false);

            pair.add(field(
                "Hình thức giảm *",
                kieu
            ));

            pair.add(field(
                "Mức giảm (% hoặc VNĐ) *",
                mucGiam
            ));

            addRow(fields, pair, 70);
        }

        // ==================== CHON SAN PHAM ====================

        List<JCheckBox> chonSanPham =
            new ArrayList<>();

        if (!caNhan) {

            JPanel productList = vertical();

            for (String[] sp : sanPham) {

                JCheckBox check = new JCheckBox(
                    sp[1] + "    ·    " + sp[0]
                );

                check.setSelected(
                    sua && cu.sanPhams.contains(sp[0])
                );

                check.setForeground(TEXT);
                check.setBackground(CARD);

                check.setFont(
                    new Font("SansSerif", Font.PLAIN, 12)
                );

                check.setAlignmentX(
                    Component.LEFT_ALIGNMENT
                );

                check.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 37)
                );

                productList.add(check);

                chonSanPham.add(check);
            }

            JScrollPane productScroll =
                new JScrollPane(productList);

            productScroll.setBorder(
                new LineBorder(BORDER)
            );

            productScroll.getViewport().setBackground(CARD);

            productScroll.getVerticalScrollBar()
                         .setUnitIncrement(14);

            addField(
                fields,
                "Sản phẩm áp dụng *",
                productScroll,
                200
            );
        }

        // ==================== THOI GIAN ====================

        JTextField tu = input(
            sua ? DATE.format(cu.tu) : "02/10/2026"
        );

        JTextField den = input(
            sua ? DATE.format(cu.den) : "16/11/2026"
        );

        JPanel dateRow = new JPanel(
            new GridLayout(1, 2, 12, 0)
        );

        dateRow.setOpaque(false);

        dateRow.add(field(
            "Từ ngày * (dd/MM/yyyy)",
            tu
        ));

        dateRow.add(field(
            "Đến ngày * (dd/MM/yyyy)",
            den
        ));

        addRow(fields, dateRow, 70);

        // ==================== TRANG THAI ====================

        JCheckBox bat = new JCheckBox(
            "Cho phép áp dụng trong thời gian hiệu lực",
            sua ? cu.bat : true
        );

        bat.setForeground(MUTED);
        bat.setBackground(CARD);

        bat.setFont(
            new Font("SansSerif", Font.PLAIN, 12)
        );

        addRow(fields, bat, 40);

        JLabel note = label(
            "ⓘ  Sửa chương trình không thay đổi hóa đơn đã thanh toán.",
            11,
            MUTED,
            Font.PLAIN
        );

        note.setOpaque(true);

        note.setBackground(
            new Color(31, 37, 46)
        );

        note.setBorder(
            new EmptyBorder(14, 11, 14, 11)
        );

        addRow(fields, note, 55);

        // ==================== SCROLL ====================

        JScrollPane scroll = new JScrollPane(fields);

        scroll.setBorder(null);

        scroll.getViewport().setBackground(CARD);

        scroll.getVerticalScrollBar()
              .setUnitIncrement(16);

        root.add(scroll, BorderLayout.CENTER);

        // ==================== FOOTER ====================

        JPanel footer = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 13)
        );

        footer.setBackground(
            new Color(30, 31, 34)
        );

        footer.setBorder(
            new MatteBorder(1, 0, 0, 0, BORDER)
        );

        JButton huy = button("Quay lại", false);

        JButton luu = button("Lưu thông tin", true);

        huy.addActionListener(e -> dialog.dispose());

        // ==================== SU KIEN LUU ====================

        luu.addActionListener(e -> {

            String tenMoi = ten.getText().trim();

            if (tenMoi.isEmpty()) {

                baoLoi(
                    dialog,
                    "Vui lòng nhập tên chương trình."
                );

                return;
            }

            long muc;
            long min = 0;
            long max = 0;

            try {

                muc = Long.parseLong(
                    mucGiam.getText().trim()
                );

                if (caNhan) {

                    min = Long.parseLong(
                        toiThieu.getText().trim()
                    );

                    max = Long.parseLong(
                        toiDa.getText().trim()
                    );
                }

            } catch (NumberFormatException ex) {

                baoLoi(
                    dialog,
                    "Mức giảm và số tiền phải là số nguyên hợp lệ."
                );

                return;
            }

            String loaiGiam = caNhan
                ? "Phần trăm"
                : String.valueOf(kieu.getSelectedItem());

            if (muc <= 0
                    || ("Phần trăm".equals(loaiGiam)
                        && muc > 100)
                    || min < 0
                    || max < 0) {

                baoLoi(
                    dialog,
                    "Mức giảm phải > 0; nếu tính theo % thì không vượt 100. "
                    + "Tiền không được âm."
                );

                return;
            }

            // -------- KIEM TRA NGAY --------

            LocalDate dateFrom;
            LocalDate dateTo;

            try {

                dateFrom = LocalDate.parse(
                    tu.getText().trim(),
                    DATE
                );

                dateTo = LocalDate.parse(
                    den.getText().trim(),
                    DATE
                );

            } catch (DateTimeParseException ex) {

                baoLoi(
                    dialog,
                    "Ngày phải đúng định dạng dd/MM/yyyy."
                );

                return;
            }

            if (!dateTo.isAfter(dateFrom)) {

                baoLoi(
                    dialog,
                    "Ngày kết thúc phải sau ngày bắt đầu."
                );

                return;
            }

            // -------- SAN PHAM DUOC CHON --------

            Set<String> selected =
                new LinkedHashSet<>();

            if (!caNhan) {

                for (int i = 0; i < chonSanPham.size(); i++) {

                    if (chonSanPham.get(i).isSelected()) {
                        selected.add(sanPham[i][0]);
                    }
                }

                if (selected.isEmpty()) {

                    baoLoi(
                        dialog,
                        "Hãy chọn ít nhất một sản phẩm."
                    );

                    return;
                }

            } else {

                // -------- UU DAI CA NHAN --------

                if (maUuDai.getText().trim().isEmpty()
                        || maKhach.getText().trim().isEmpty()
                        || max == 0
                        || min == 0) {

                    baoLoi(
                        dialog,
                        "Nhập mã ưu đãi, mã khách hàng, "
                        + "đơn tối thiểu và mức giảm tối đa (>0)."
                    );

                    return;
                }

                for (ChuongTrinh item : ds) {

                    if (item != cu
                            && item.ma.equalsIgnoreCase(
                                maUuDai.getText().trim()
                            )) {

                        baoLoi(
                            dialog,
                            "Mã ưu đãi đã tồn tại."
                        );

                        return;
                    }
                }
            }

            // ==================== THEM / CAP NHAT ====================

            ChuongTrinh km = cu;

            if (km == null) {

                String ma = caNhan
                    ? maUuDai.getText().trim()
                    : String.format("KM%04d", soKm++);

                km = new ChuongTrinh(
                    ma,
                    tenMoi,
                    caNhan
                );

                ds.add(km);
            }

            km.ten = tenMoi;
            km.kieu = loaiGiam;
            km.mucGiam = muc;

            km.tu = dateFrom;
            km.den = dateTo;
            km.bat = bat.isSelected();

            if (caNhan) {

                km.maKhach = maKhach.getText().trim();
                km.toiThieu = min;
                km.toiDa = max;

            } else {

                km.sanPhams.clear();
                km.sanPhams.addAll(selected);
            }

            taiBang();

            dialog.dispose();
        });

        footer.add(huy);
        footer.add(luu);

        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    // ==================== COMPONENT DUNG CHUNG ====================

    private static JPanel vertical() {

        JPanel p = new JPanel();

        p.setOpaque(false);

        p.setLayout(
            new BoxLayout(p, BoxLayout.Y_AXIS)
        );

        return p;
    }

    private static JLabel label(
            String s,
            int size,
            Color color,
            int style) {

        JLabel l = new JLabel(s);

        l.setForeground(color);

        l.setFont(
            new Font("SansSerif", style, size)
        );

        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        return l;
    }

    private static JButton button(
            String s,
            boolean blue) {

        JButton b = new JButton(s);

        b.setForeground(Color.WHITE);

        b.setBackground(
            blue ? BLUE : new Color(30, 33, 38)
        );

        b.setFont(
            new Font("SansSerif", Font.BOLD, 11)
        );

        b.setFocusPainted(false);

        b.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        b.setBorder(new CompoundBorder(
            new LineBorder(blue ? BLUE : BORDER),
            new EmptyBorder(9, 12, 9, 12)
        ));

        return b;
    }

    private static JTextField input(String value) {

        JTextField f = new JTextField(value);

        styleInput(f);

        return f;
    }

    private static void styleInput(JTextField f) {

        f.setBackground(
            new Color(22, 24, 27)
        );

        f.setForeground(TEXT);
        f.setCaretColor(TEXT);

        f.setFont(
            new Font("SansSerif", Font.PLAIN, 12)
        );

        f.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(8, 10, 8, 10)
        ));

        f.setPreferredSize(
            new Dimension(160, 36)
        );
    }

    private static void styleCombo(JComboBox<String> c) {

        c.setBackground(
            new Color(22, 24, 27)
        );

        c.setForeground(TEXT);

        c.setFont(
            new Font("SansSerif", Font.PLAIN, 12)
        );

        c.setPreferredSize(
            new Dimension(160, 36)
        );
    }

    private static JPanel field(
            String title,
            JComponent component) {

        JPanel p = new JPanel(
            new BorderLayout(0, 7)
        );

        p.setOpaque(false);

        p.add(
            label(title, 12, MUTED, Font.BOLD),
            BorderLayout.NORTH
        );

        p.add(component, BorderLayout.CENTER);

        return p;
    }

    private static void addField(
            JPanel parent,
            String title,
            JComponent comp,
            int height) {

        addRow(
            parent,
            field(title, comp),
            height
        );
    }

    private static void addRow(
            JPanel parent,
            JComponent row,
            int height) {

        row.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        row.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, height)
        );

        row.setPreferredSize(
            new Dimension(450, height)
        );

        parent.add(row);
        parent.add(Box.createVerticalStrut(13));
    }

    private static void baoLoi(
            Component parent,
            String message) {

        JOptionPane.showMessageDialog(
            parent,
            message,
            "Kiểm tra dữ liệu",
            JOptionPane.WARNING_MESSAGE
        );
    }

    private static String html(String s) {

        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static String tien(long n) {

        return java.text.NumberFormat.getIntegerInstance(
            Locale.forLanguageTag("vi-VN")
        ).format(n) + " đ";
    }

    // ==================== MODEL TAM ====================

    private static class ChuongTrinh {

        final String ma;
        final boolean caNhan;

        String ten;
        String kieu = "Phần trăm";

        long mucGiam;

        LocalDate tu = LocalDate.of(2026, 10, 2);
        LocalDate den = LocalDate.of(2026, 11, 16);

        boolean bat = true;
        boolean daDung = false;

        Set<String> sanPhams =
            new LinkedHashSet<>();

        String maKhach = "";

        long toiThieu;
        long toiDa;

        ChuongTrinh(
                String ma,
                String ten,
                boolean caNhan) {

            this.ma = ma;
            this.ten = ten;
            this.caNhan = caNhan;
        }
    }

    // ==================== CHAY THU ====================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame f = new JFrame(
                "Green Home - Khuyến mãi (demo)"
            );

            f.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
            );

            f.setSize(1150, 735);
            f.setLocationRelativeTo(null);

            f.setContentPane(
                new KhuyenMaiPanel(true)
            );

            f.setVisible(true);
        });
    }
}