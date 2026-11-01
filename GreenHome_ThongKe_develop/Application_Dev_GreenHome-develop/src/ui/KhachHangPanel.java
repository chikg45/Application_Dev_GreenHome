package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Giao diện quản lý khách hàng.
 * Hiện tại sử dụng dữ liệu mẫu, chưa kết nối SQL Server.
 */
public class KhachHangPanel extends JPanel {

    // ==================== MAU SAC ====================

    private static final Color BG = new Color(30, 33, 37);
    private static final Color CARD = new Color(18, 19, 21);
    private static final Color BORDER = new Color(57, 61, 67);

    private static final Color TEXT = new Color(225, 232, 242);
    private static final Color MUTED = new Color(143, 155, 173);

    private static final Color BLUE = new Color(54, 111, 232);

    // ==================== DU LIEU ====================

    private final List<KhachHangMau> khachHang = new ArrayList<>();

    private final List<KhachHangMau> dangHienThi = new ArrayList<>();

    private final DefaultTableModel tableModel = new DefaultTableModel(
        new String[] {
            "Khách hàng",
            "Liên hệ",
            "Điểm hiện có",
            "Lần mua gần nhất",
            "Xem",
            "Sửa"
        }, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private JTable table;
    private JTextField txtTimKiem;

    private JLabel lblSoKhach;
    private JLabel lblCoDiem;
    private JLabel lblSoDong;

    private int maTiepTheo = 5;

    // ==================== CONSTRUCTOR ====================

    public KhachHangPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);

        taoDuLieuMau();
        taoGiaoDien();
        taiDanhSach();
    }

    // ==================== DU LIEU MAU ====================

    private void taoDuLieuMau() {

        khachHang.add(new KhachHangMau(
            "KH000000001",
            "Nguyễn Văn An",
            "0901234567",
            "Gò Vấp, TP. Hồ Chí Minh",
            1230,
            "—"
        ));

        khachHang.add(new KhachHangMau(
            "KH000000002",
            "Trần Thị Mai",
            "0932345678",
            "TP. Hồ Chí Minh",
            296,
            "15/10/2026"
        ));

        khachHang.add(new KhachHangMau(
            "KH000000003",
            "Lê Hoàng Nam",
            "0383456789",
            "TP. Hồ Chí Minh",
            70,
            "16/10/2026"
        ));

        khachHang.add(new KhachHangMau(
            "KH000000004",
            "Phúc",
            "0909090909",
            "",
            0,
            "—"
        ));
    }

    // ==================== TAO GIAO DIEN ====================

    private void taoGiaoDien() {

        JPanel content = new JPanel(new BorderLayout(0, 15));

        content.setBackground(BG);
        content.setBorder(new EmptyBorder(24, 28, 24, 28));

        // -------- PHAN TREN --------

        JPanel top = new JPanel(new BorderLayout(0, 16));
        top.setOpaque(false);

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        titleBox.add(text(
            "Quản lý khách hàng",
            21,
            Font.BOLD,
            TEXT
        ));

        titleBox.add(Box.createVerticalStrut(6));

        titleBox.add(text(
            "Quản lý hồ sơ, lịch sử mua hàng và điểm tích lũy.",
            12,
            Font.PLAIN,
            MUTED
        ));

        heading.add(titleBox, BorderLayout.WEST);

        JButton btnThem = button("+  Thêm khách hàng", true);

        btnThem.addActionListener(e -> moForm(null));

        heading.add(btnThem, BorderLayout.EAST);

        top.add(heading, BorderLayout.NORTH);

        // -------- THONG KE --------

        JPanel stats = new JPanel(new GridLayout(1, 3, 13, 0));
        stats.setOpaque(false);
        stats.setPreferredSize(new Dimension(0, 105));

        lblSoKhach = text(
            "0", 24, Font.BOLD, new Color(157, 190, 250)
        );

        lblCoDiem = text(
            "0", 24, Font.BOLD, new Color(214, 173, 77)
        );

        stats.add(statCard(
            "Hồ sơ khách hàng",
            lblSoKhach,
            "Không bao gồm khách vãng lai",
            "♙"
        ));

        stats.add(statCard(
            "Khách có điểm",
            lblCoDiem,
            "Số dư điểm đang có",
            "★"
        ));

        stats.add(statCard(
            "Mã ưu đãi còn hiệu lực",
            text("2", 24, Font.BOLD, new Color(157, 190, 250)),
            "Chỉ là dữ liệu giao diện mẫu",
            "◇"
        ));

        top.add(stats, BorderLayout.CENTER);

        content.add(top, BorderLayout.NORTH);

        // -------- BANG KHACH HANG --------

        JPanel tableCard = new JPanel(new BorderLayout());

        tableCard.setBackground(CARD);

        tableCard.setBorder(
            BorderFactory.createLineBorder(BORDER)
        );

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(CARD);

        toolbar.setBorder(
            new EmptyBorder(12, 12, 12, 12)
        );

        txtTimKiem = new JTextField();

        txtTimKiem.setToolTipText(
            "Tìm theo tên, số điện thoại hoặc mã khách hàng"
        );

        styleInput(txtTimKiem);

        txtTimKiem.setPreferredSize(
            new Dimension(320, 35)
        );

        JPanel searchArea = new JPanel(new BorderLayout());
        searchArea.setOpaque(false);

        searchArea.setPreferredSize(
            new Dimension(350, 35)
        );

        searchArea.add(txtTimKiem);

        toolbar.add(searchArea, BorderLayout.WEST);

        lblSoDong = text(
            "0 khách hàng",
            11,
            Font.PLAIN,
            MUTED
        );

        toolbar.add(lblSoDong, BorderLayout.EAST);

        tableCard.add(toolbar, BorderLayout.NORTH);

        // -------- CAU HINH JTABLE --------

        table = new JTable(tableModel);

        table.setRowHeight(53);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));

        table.setBackground(CARD);
        table.setForeground(TEXT);

        table.setSelectionBackground(
            new Color(33, 47, 69)
        );

        table.setSelectionForeground(TEXT);

        table.setShowGrid(false);
        table.setFillsViewportHeight(true);

        table.getTableHeader().setReorderingAllowed(false);

        table.getTableHeader().setPreferredSize(
            new Dimension(0, 35)
        );

        table.getTableHeader().setBackground(
            new Color(28, 30, 33)
        );

        table.getTableHeader().setForeground(MUTED);

        table.getTableHeader().setFont(
            new Font("SansSerif", Font.BOLD, 11)
        );

        // Chinh do rong cot
        table.getColumnModel().getColumn(4).setMaxWidth(80);
        table.getColumnModel().getColumn(5).setMaxWidth(80);

        table.getColumnModel().getColumn(2).setPreferredWidth(110);

        // -------- CAN LE NOI DUNG BANG --------

        DefaultTableCellRenderer normal =
                new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(
                    JTable t,
                    Object value,
                    boolean selected,
                    boolean focused,
                    int row,
                    int column) {

                super.getTableCellRendererComponent(
                    t, value, selected, focused, row, column
                );

                setBorder(new EmptyBorder(0, 12, 0, 8));

                return this;
            }
        };

        for (int i = 0; i < 4; i++) {
            table.getColumnModel()
                 .getColumn(i)
                 .setCellRenderer(normal);
        }

        // -------- COT XEM VA SUA --------

        DefaultTableCellRenderer action =
                new DefaultTableCellRenderer();

        action.setHorizontalAlignment(SwingConstants.CENTER);
        action.setForeground(new Color(157, 190, 250));
        action.setBackground(CARD);

        action.setFont(
            new Font("SansSerif", Font.BOLD, 11)
        );

        table.getColumnModel()
             .getColumn(4)
             .setCellRenderer(action);

        table.getColumnModel()
             .getColumn(5)
             .setCellRenderer(action);

        // Xu ly click vao cot Xem / Sua
        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row < 0 || (col != 4 && col != 5)) {
                    return;
                }

                KhachHangMau kh = dangHienThi.get(row);

                if (col == 4) {
                    moChiTiet(kh);
                } else {
                    moForm(kh);
                }
            }
        });

        JScrollPane scroller = new JScrollPane(table);

        scroller.setBorder(null);
        scroller.getViewport().setBackground(CARD);

        tableCard.add(scroller, BorderLayout.CENTER);

        content.add(tableCard, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);

        // -------- SU KIEN TIM KIEM --------

        txtTimKiem.getDocument().addDocumentListener(
            new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    taiDanhSach();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    taiDanhSach();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    taiDanhSach();
                }
            }
        );
    }

    // ==================== THE THONG KE ====================

    private JPanel statCard(
            String title,
            JLabel value,
            String desc,
            String icon) {

        JPanel card = new JPanel(new BorderLayout());

        card.setBackground(CARD);

        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(13, 14, 13, 14)
            )
        );

        JPanel lines = new JPanel();

        lines.setLayout(
            new BoxLayout(lines, BoxLayout.Y_AXIS)
        );

        lines.setOpaque(false);

        lines.add(text(title, 11, Font.PLAIN, MUTED));
        lines.add(Box.createVerticalStrut(6));
        lines.add(value);
        lines.add(Box.createVerticalStrut(5));
        lines.add(text(desc, 10, Font.PLAIN, MUTED));

        card.add(lines, BorderLayout.CENTER);

        card.add(
            text(icon, 19, Font.BOLD, BLUE),
            BorderLayout.EAST
        );

        return card;
    }

    // ==================== TAI DANH SACH ====================

    private void taiDanhSach() {

        if (txtTimKiem == null) {
            return;
        }

        String query = txtTimKiem.getText()
                .trim()
                .toLowerCase();

        dangHienThi.clear();
        tableModel.setRowCount(0);

        for (KhachHangMau kh : khachHang) {

            if (!kh.ten.toLowerCase().contains(query)
                    && !kh.sdt.contains(query)
                    && !kh.ma.toLowerCase().contains(query)) {

                continue;
            }

            dangHienThi.add(kh);

            tableModel.addRow(new Object[] {

                "<html><b>" + escape(kh.ten)
                    + "</b><br><font color='#8F9BAD'>"
                    + kh.ma + "</font></html>",

                kh.sdt,
                kh.diem,
                kh.muaGanNhat,
                "Xem",
                "Sửa"
            });
        }

        lblSoKhach.setText(
            String.valueOf(khachHang.size())
        );

        long coDiem = khachHang.stream()
                .filter(k -> k.diem > 0)
                .count();

        lblCoDiem.setText(String.valueOf(coDiem));

        lblSoDong.setText(
            dangHienThi.size() + " khách hàng"
        );
    }

    // ==================== THEM / CAP NHAT ====================

    private void moForm(KhachHangMau khCu) {

        boolean sua = khCu != null;

        JDialog dialog = taoDialog(
            sua ? "Cập nhật khách hàng" : "Thêm khách hàng",
            580,
            455
        );

        JPanel body = new JPanel(new BorderLayout(0, 20));

        body.setBackground(CARD);
        body.setBorder(new EmptyBorder(22, 25, 22, 25));

        JPanel fields = new JPanel(new GridLayout(2, 1, 0, 17));
        fields.setOpaque(false);

        JPanel firstRow = new JPanel(new GridLayout(1, 2, 15, 0));
        firstRow.setOpaque(false);

        JTextField txtTen = new JTextField(
            sua ? khCu.ten : ""
        );

        JTextField txtSdt = new JTextField(
            sua ? khCu.sdt : ""
        );

        firstRow.add(field("Họ tên *", txtTen));
        firstRow.add(field("Số điện thoại *", txtSdt));

        fields.add(firstRow);

        JTextField txtDiaChi = new JTextField(
            sua ? khCu.diaChi : ""
        );

        fields.add(field(
            "Địa chỉ (không bắt buộc)",
            txtDiaChi
        ));

        body.add(fields, BorderLayout.NORTH);

        body.add(
            text(
                "Số dư điểm và lịch sử giao dịch không chỉnh sửa trực tiếp trong hồ sơ.",
                11,
                Font.PLAIN,
                MUTED
            ),
            BorderLayout.CENTER
        );

        JPanel actions = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );

        actions.setOpaque(false);

        JButton cancel = button("Quay lại", false);
        JButton save = button("Lưu thông tin", true);

        cancel.addActionListener(e -> dialog.dispose());

        save.addActionListener(e -> {

            String ten = txtTen.getText().trim();
            String sdt = txtSdt.getText().trim();

            // Kiem tra du lieu
            if (ten.isEmpty() || !Pattern.matches("0\\d{9}", sdt)) {

                JOptionPane.showMessageDialog(
                    dialog,
                    "Nhập họ tên và số điện thoại hợp lệ "
                        + "(10 chữ số, bắt đầu bằng 0).",
                    "Dữ liệu chưa hợp lệ",
                    JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // Kiem tra trung so dien thoai
            for (KhachHangMau kh : khachHang) {

                if (kh != khCu && kh.sdt.equals(sdt)) {

                    JOptionPane.showMessageDialog(
                        dialog,
                        "Số điện thoại đã tồn tại!",
                        "Trùng hồ sơ",
                        JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }
            }

            if (sua) {

                khCu.ten = ten;
                khCu.sdt = sdt;
                khCu.diaChi = txtDiaChi.getText().trim();

            } else {

                String ma = String.format(
                    "KH%09d",
                    maTiepTheo++
                );

                khachHang.add(new KhachHangMau(
                    ma,
                    ten,
                    sdt,
                    txtDiaChi.getText().trim(),
                    0,
                    "—"
                ));
            }

            taiDanhSach();
            dialog.dispose();
        });

        actions.add(cancel);
        actions.add(save);

        body.add(actions, BorderLayout.SOUTH);

        dialog.setContentPane(body);
        dialog.setVisible(true);
    }

 // ==================== CHI TIET KHACH HANG ====================

    private void moChiTiet(KhachHangMau kh) {

        JDialog dialog = taoDialog("Chi tiết khách hàng", 940, 760);

        dialog.setMinimumSize(new Dimension(750, 560));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CARD);

        // ==================== HEADER ====================

        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(CARD);

        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(18, 22, 18, 22)
        ));

        JPanel customerInfo = new JPanel();
        customerInfo.setOpaque(false);

        customerInfo.setLayout(
            new BoxLayout(customerInfo, BoxLayout.Y_AXIS)
        );

        customerInfo.add(text(
            kh.ten, 22, Font.BOLD, TEXT
        ));

        customerInfo.add(Box.createVerticalStrut(6));

        customerInfo.add(text(
            kh.ma + " · " + kh.sdt,
            12, Font.PLAIN, MUTED
        ));

        header.add(customerInfo, BorderLayout.WEST);

        JButton closeTop = button("✕", false);

        closeTop.addActionListener(e -> dialog.dispose());

        header.add(closeTop, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        // ==================== NOI DUNG ====================

        JPanel details = new JPanel();
        details.setBackground(CARD);

        details.setLayout(
            new BoxLayout(details, BoxLayout.Y_AXIS)
        );

        details.setBorder(
            new EmptyBorder(22, 22, 24, 22)
        );

        // ==================== THONG TIN LIEN HE ====================

        JPanel contact = new JPanel(
            new GridLayout(1, 2, 16, 0)
        );

        contact.setOpaque(false);

        contact.add(infoField(
            "Địa chỉ",
            kh.diaChi.isBlank() ? "Chưa cung cấp" : kh.diaChi
        ));

        contact.add(infoField(
            "Email",
            "Chưa cung cấp"
        ));

        contact.setAlignmentX(Component.LEFT_ALIGNMENT);

        contact.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 54)
        );

        details.add(contact);

        details.add(Box.createVerticalStrut(15));

        // ==================== THONG KE ====================

        // Du lieu giao dich mau theo anh thiet ke
        boolean coGiaoDichMau = "KH000000002".equals(kh.ma);

        JPanel stats = new JPanel(
            new GridLayout(1, 3, 12, 0)
        );

        stats.setOpaque(false);

        stats.setAlignmentX(Component.LEFT_ALIGNMENT);

        stats.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 85)
        );

        stats.add(infoStat(
            "Điểm khả dụng",
            String.valueOf(kh.diem)
        ));

        stats.add(infoStat(
            "Giao dịch mua",
            coGiaoDichMau ? "1" : "0"
        ));

        stats.add(infoStat(
            "Chi tiêu sau hoàn",
            coGiaoDichMau ? "11.610.000 đ" : "0 đ"
        ));

        details.add(stats);

        details.add(Box.createVerticalStrut(19));

        // ==================== LICH SU MUA HANG ====================

        details.add(sectionTitle("Lịch sử mua hàng"));

        details.add(Box.createVerticalStrut(10));

        if (coGiaoDichMau) {

            JPanel order = borderedRow();
            order.setLayout(new BorderLayout(10, 0));

            JPanel orderText = new JPanel();
            orderText.setOpaque(false);

            orderText.setLayout(
                new BoxLayout(orderText, BoxLayout.Y_AXIS)
            );

            orderText.add(text(
                "HD000001",
                12, Font.BOLD, TEXT
            ));

            orderText.add(Box.createVerticalStrut(6));

            orderText.add(text(
                "23:25 15/10/2026",
                11, Font.PLAIN, MUTED
            ));

            order.add(orderText, BorderLayout.WEST);

            JPanel right = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 8)
            );

            right.setOpaque(false);

            right.add(text(
                "11.610.000 đ",
                12, Font.BOLD, TEXT
            ));

            JButton detail = button("Xem chi tiết", false);

            detail.addActionListener(e -> {
                HoaDonDialog.moChiTiet(dialog, "HD000001");
            });

            right.add(detail);

            order.add(right, BorderLayout.EAST);

            details.add(order);

        } else {

            details.add(emptyRow(
                "Chưa có dữ liệu lịch sử mua hàng trong bản mẫu."
            ));
        }

        details.add(Box.createVerticalStrut(18));

        // ==================== UU DAI CA NHAN ====================

        details.add(sectionTitle("Mã ưu đãi cá nhân"));

        details.add(Box.createVerticalStrut(10));

        if (coGiaoDichMau) {

            JPanel coupon = borderedRow();

            coupon.setLayout(
                new BoxLayout(coupon, BoxLayout.Y_AXIS)
            );

            coupon.add(text(
                "MAI5  ·  Còn hiệu lực (mẫu)",
                12, Font.BOLD, TEXT
            ));

            coupon.add(Box.createVerticalStrut(7));

            coupon.add(text(
                "Giảm 5% · tối đa 300.000 đ · đơn từ 500.000 đ",
                11, Font.PLAIN, MUTED
            ));

            coupon.add(Box.createVerticalStrut(5));

            coupon.add(text(
                "02/10/2026 – 16/11/2026",
                11, Font.PLAIN, MUTED
            ));

            details.add(coupon);

        } else {

            details.add(emptyRow(
                "Chưa có ưu đãi cá nhân trong dữ liệu mẫu."
            ));
        }

        details.add(Box.createVerticalStrut(18));

        // ==================== LICH SU DIEM ====================

        details.add(sectionTitle("Lịch sử điểm"));

        details.add(Box.createVerticalStrut(10));

        if (coGiaoDichMau) {

            JPanel points = new JPanel(
                new BorderLayout(12, 0)
            );

            points.setOpaque(false);

            points.setAlignmentX(Component.LEFT_ALIGNMENT);

            points.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 50)
            );

            JPanel pointText = new JPanel();
            pointText.setOpaque(false);

            pointText.setLayout(
                new BoxLayout(pointText, BoxLayout.Y_AXIS)
            );

            pointText.add(text(
                "HD000001",
                11, Font.BOLD, TEXT
            ));

            pointText.add(Box.createVerticalStrut(5));

            pointText.add(text(
                "23:25 15/10/2026 · Số dư 296 điểm",
                11, Font.PLAIN, MUTED
            ));

            points.add(pointText, BorderLayout.WEST);

            points.add(
                text(
                    "+116 điểm",
                    12,
                    Font.BOLD,
                    new Color(56, 181, 122)
                ),
                BorderLayout.EAST
            );

            details.add(points);

        } else {

            details.add(emptyRow(
                "Chưa có lịch sử điểm trong dữ liệu mẫu."
            ));
        }

        // ==================== SCROLL ====================

        JScrollPane scroll = new JScrollPane(details);

        scroll.setBorder(null);

        scroll.getViewport().setBackground(CARD);

        scroll.getVerticalScrollBar().setUnitIncrement(16);

        scroll.setVerticalScrollBarPolicy(
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        root.add(scroll, BorderLayout.CENTER);

        // ==================== FOOTER ====================

        JPanel footer = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 12)
        );

        footer.setBackground(
            new Color(30, 31, 34)
        );

        footer.setBorder(
            new MatteBorder(1, 0, 0, 0, BORDER)
        );

        JButton close = button("Đóng", false);

        JButton edit = button("Cập nhật hồ sơ", true);

        close.addActionListener(e -> dialog.dispose());

        edit.addActionListener(e -> {

            dialog.dispose();

            moForm(kh);
        });

        footer.add(close);
        footer.add(edit);

        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);

        dialog.setLocationRelativeTo(
            SwingUtilities.getWindowAncestor(this)
        );

        dialog.setVisible(true);
    }

    // ==================== CAC HAM HO TRO ====================

    private JDialog taoDialog(
            String title,
            int width,
            int height) {

        Window owner = SwingUtilities.getWindowAncestor(this);

        JDialog d = new JDialog(
            owner,
            title,
            Dialog.ModalityType.APPLICATION_MODAL
        );

        d.setSize(width, height);
        d.setLocationRelativeTo(owner);

        d.setDefaultCloseOperation(
            JDialog.DISPOSE_ON_CLOSE
        );

        return d;
    }

    private JPanel field(String name, JTextField input) {

        JPanel holder = new JPanel(new BorderLayout(0, 7));
        holder.setOpaque(false);

        holder.add(
            text(name, 12, Font.BOLD, MUTED),
            BorderLayout.NORTH
        );

        styleInput(input);

        input.setPreferredSize(new Dimension(180, 38));

        holder.add(input, BorderLayout.CENTER);

        return holder;
    }

    private static JLabel text(
            String value,
            int size,
            int style,
            Color color) {

        JLabel label = new JLabel(value);

        label.setFont(
            new Font("SansSerif", style, size)
        );

        label.setForeground(color);

        return label;
    }

    private static JButton button(
            String name,
            boolean primary) {

        JButton button = new JButton(name);

        button.setFont(
            new Font("SansSerif", Font.BOLD, 12)
        );

        button.setForeground(TEXT);

        button.setBackground(
            primary ? BLUE : new Color(34, 38, 44)
        );

        button.setFocusPainted(false);

        button.setBorder(
            new EmptyBorder(10, 14, 10, 14)
        );

        button.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        return button;
    }

    private static void styleInput(JTextField input) {

        input.setFont(
            new Font("SansSerif", Font.PLAIN, 12)
        );

        input.setBackground(
            new Color(22, 24, 27)
        );

        input.setForeground(TEXT);
        input.setCaretColor(TEXT);

        input.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(6, 11, 6, 11)
            )
        );
    }

    private static String escape(String value) {

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
    }

    // ==================== LOP DU LIEU MAU ====================

    private static class KhachHangMau {

        String ma;
        String ten;
        String sdt;
        String diaChi;
        String muaGanNhat;

        int diem;

        KhachHangMau(
                String ma,
                String ten,
                String sdt,
                String diaChi,
                int diem,
                String muaGanNhat) {

            this.ma = ma;
            this.ten = ten;
            this.sdt = sdt;
            this.diaChi = diaChi;
            this.diem = diem;
            this.muaGanNhat = muaGanNhat;
        }
    }
    private JPanel infoField(String caption, String value) {

        JPanel panel = new JPanel();

        panel.setOpaque(false);

        panel.setLayout(
            new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        panel.add(text(
            caption, 11, Font.PLAIN, MUTED
        ));

        panel.add(Box.createVerticalStrut(7));

        panel.add(text(
            value, 12, Font.BOLD, TEXT
        ));

        return panel;
    }
    private JPanel infoStat(String caption, String value) {

        JPanel panel = new JPanel();

        panel.setBackground(
            new Color(31, 33, 37)
        );

        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(12, 14, 12, 14)
        ));

        panel.setLayout(
            new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        panel.add(text(
            caption, 11, Font.PLAIN, MUTED
        ));

        panel.add(Box.createVerticalStrut(9));

        panel.add(text(
            value,
            18,
            Font.BOLD,
            new Color(153, 184, 238)
        ));

        return panel;
    }
    private JLabel sectionTitle(String caption) {

        JLabel label = text(
            caption,
            13,
            Font.BOLD,
            new Color(156, 183, 222)
        );

        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }
    private JPanel borderedRow() {

        JPanel panel = new JPanel();

        panel.setBackground(CARD);

        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(14, 14, 14, 14)
        ));

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 95)
        );

        return panel;
    }
    private JPanel emptyRow(String message) {

        JPanel row = borderedRow();

        row.setLayout(new BorderLayout());

        row.add(
            text(
                message,
                11,
                Font.PLAIN,
                MUTED
            ),
            BorderLayout.WEST
        );

        return row;
    }
}