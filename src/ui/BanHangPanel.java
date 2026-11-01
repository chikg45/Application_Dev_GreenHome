package ui;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * Giao diện bán hàng dùng chung cho Quản lý và Nhân viên.
 * Dữ liệu hiện tại là dữ liệu mẫu, chưa lưu CSDL.
 */
public class BanHangPanel extends JPanel {

    // ==================== MÀU SẮC ====================

    private static final Color BG = new Color(30, 33, 37);
    private static final Color CARD = new Color(18, 19, 21);
    private static final Color BORDER = new Color(57, 61, 67);
    private static final Color INPUT = new Color(23, 25, 29);

    private static final Color TEXT = new Color(225, 232, 242);
    private static final Color MUTED = new Color(143, 155, 173);
    private static final Color BLUE = new Color(54, 111, 232);
    private static final Color GREEN = new Color(57, 174, 121);

    // ==================== DỮ LIỆU ====================

    private final List<SanPham> sanPhams = new ArrayList<>();
    private final List<KhachHang> khachHangs = new ArrayList<>();

    private final Map<SanPham, Integer> gioHang = new LinkedHashMap<>();

    private KhachHang khachDaChon;

    // ==================== COMPONENTS ====================

    private final JTextField txtTimSanPham = input();
    private final JTextField txtTimKhach = input();
    private final JTextField txtMaUuDai = input();

    private final JComboBox<String> cboDanhMuc = new JComboBox<>(
        new String[] {
            "Tất cả danh mục",
            "Điện thoại",
            "Laptop",
            "Tivi",
            "Phụ kiện",
            "Tủ lạnh",
            "Máy giặt"
        }
    );

    private final JComboBox<String> cboTinhTrang = new JComboBox<>(
        new String[] {
            "Tất cả tình trạng",
            "Còn hàng",
            "Hết hàng"
        }
    );

    private final JPanel productGrid =
        new JPanel(new GridLayout(0, 3, 12, 12));

    private final JPanel cartRows = vertical();

    private final JLabel lblKhach =
        text("Khách vãng lai", 12, Font.BOLD, TEXT);

    private final JLabel lblGio =
        text("Giỏ hàng (0)", 14, Font.BOLD, TEXT);

    private final JLabel lblTongHang =
        text("0 đ", 12, Font.PLAIN, TEXT);

    private final JLabel lblGiam =
        text("0 đ", 12, Font.PLAIN, GREEN);

    private final JLabel lblDiem =
        text("0 đ", 12, Font.PLAIN, GREEN);

    private final JLabel lblThanhToan =
        text("0 đ", 21, Font.BOLD, new Color(153, 186, 247));

    private final JCheckBox cbDungDiem =
        new JCheckBox("Dùng toàn bộ điểm hiện có");

    private final JButton btnThanhToan =
        primary("Thanh toán  →");

    // ==================== CONSTRUCTOR ====================

    public BanHangPanel() {

        setLayout(new BorderLayout());
        setBackground(BG);

        taoGiaoDien();

        locSanPham();
        capNhatGioHang();
    }

    // ==================== DỮ LIỆU MẪU ====================



    // ==================== GIAO DIỆN CHÍNH ====================

    private void taoGiaoDien() {

        JPanel content = new JPanel(new BorderLayout(0, 14));

        content.setBackground(BG);
        content.setBorder(new EmptyBorder(19, 24, 20, 24));

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        toolbar.add(
            text("▣  Chưa có đơn", 13, Font.BOLD,
                new Color(157, 190, 250)),
            BorderLayout.WEST
        );

        JPanel tools = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );

        tools.setOpaque(false);

        JButton btnLichSu = secondary("Lịch sử bán hàng");

        btnLichSu.addActionListener(e -> {
            HoaDonDialog.moDanhSach(this);
        });

        JButton btnDonMoi = secondary("+  Đơn mới");

        btnDonMoi.addActionListener(e -> {

            int ok = JOptionPane.showConfirmDialog(
                this,
                "Bỏ giỏ hàng hiện tại và tạo đơn mới?",
                "Đơn mới",
                JOptionPane.YES_NO_OPTION
            );

            if (ok == JOptionPane.YES_OPTION) {

                gioHang.clear();
                khachDaChon = null;

                txtTimKhach.setText("");
                txtMaUuDai.setText("");
                cbDungDiem.setSelected(false);

                capNhatGioHang();
            }
        });

        tools.add(btnLichSu);
        tools.add(btnDonMoi);

        toolbar.add(tools, BorderLayout.EAST);
        content.add(toolbar, BorderLayout.NORTH);

        // Chia khu vực sản phẩm và xử lý bán hàng

        JPanel columns = new JPanel(new GridBagLayout());
        columns.setOpaque(false);

        GridBagConstraints left = new GridBagConstraints();

        left.gridx = 0;
        left.gridy = 0;
        left.weightx = 0.57;
        left.weighty = 1;
        left.fill = GridBagConstraints.BOTH;
        left.insets = new Insets(0, 0, 0, 7);

        columns.add(taoKhuSanPham(), left);

        GridBagConstraints right = new GridBagConstraints();

        right.gridx = 1;
        right.gridy = 0;
        right.weightx = 0.43;
        right.weighty = 1;
        right.fill = GridBagConstraints.BOTH;
        right.insets = new Insets(0, 7, 0, 0);

        columns.add(taoKhuBanHang(), right);

        content.add(columns, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
    }

    // ==================== KHU VỰC SẢN PHẨM ====================

    private JPanel taoKhuSanPham() {

        JPanel panel = card(new BorderLayout(0, 12));

        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(14, 14, 12, 14)
        ));

        JPanel top = vertical();

        top.add(text(
            "Chọn sản phẩm", 15, Font.BOLD, TEXT
        ));

        top.add(Box.createVerticalStrut(5));

        top.add(text(
            "Tìm hàng, kiểm tra tồn và thêm vào đơn.",
            11, Font.PLAIN, MUTED
        ));

        top.add(Box.createVerticalStrut(12));

        txtTimSanPham.setToolTipText(
            "Tìm theo tên hoặc mã sản phẩm"
        );

        top.add(txtTimSanPham);
        top.add(Box.createVerticalStrut(9));

        JPanel filters = new JPanel(
            new GridLayout(1, 2, 9, 0)
        );

        filters.setOpaque(false);

        styleCombo(cboDanhMuc);
        styleCombo(cboTinhTrang);

        filters.add(cboDanhMuc);
        filters.add(cboTinhTrang);

        top.add(filters);

        panel.add(top, BorderLayout.NORTH);

        // Danh sách sản phẩm dạng lưới

        productGrid.setBackground(CARD);

        JPanel gridHolder = new JPanel(new BorderLayout());
        gridHolder.setBackground(CARD);
        gridHolder.add(productGrid, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(gridHolder);

        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD);

        scroll.getVerticalScrollBar().setUnitIncrement(18);

        scroll.setHorizontalScrollBarPolicy(
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        panel.add(scroll, BorderLayout.CENTER);

        // Sự kiện tìm kiếm trực tiếp

        txtTimSanPham.getDocument().addDocumentListener(
            new DocumentListener() {

                public void insertUpdate(DocumentEvent e) {
                    locSanPham();
                }

                public void removeUpdate(DocumentEvent e) {
                    locSanPham();
                }

                public void changedUpdate(DocumentEvent e) {
                    locSanPham();
                }
            }
        );

        cboDanhMuc.addActionListener(e -> locSanPham());
        cboTinhTrang.addActionListener(e -> locSanPham());

        return panel;
    }

    private void locSanPham() {

        productGrid.removeAll();

        String query = txtTimSanPham.getText()
                .trim()
                .toLowerCase(Locale.ROOT);

        String danhMuc = String.valueOf(
            cboDanhMuc.getSelectedItem()
        );

        String tinhTrang = String.valueOf(
            cboTinhTrang.getSelectedItem()
        );

        for (SanPham p : sanPhams) {

            boolean dungTen =
                p.ten.toLowerCase(Locale.ROOT).contains(query)
                || p.ma.toLowerCase(Locale.ROOT).contains(query);

            boolean dungDanhMuc =
                danhMuc.startsWith("Tất cả")
                || danhMuc.equals(p.danhMuc);

            boolean dungTinhTrang =
                tinhTrang.startsWith("Tất cả")
                || (tinhTrang.equals("Còn hàng") && p.ton > 0)
                || (tinhTrang.equals("Hết hàng") && p.ton <= 0);

            if (dungTen && dungDanhMuc && dungTinhTrang) {
                productGrid.add(taoTheSanPham(p));
            }
        }

        productGrid.revalidate();
        productGrid.repaint();
    }

    private JPanel taoTheSanPham(SanPham p) {

        JPanel tile = card(new BorderLayout(0, 7));

        tile.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(8, 8, 8, 8)
        ));

        tile.setPreferredSize(new Dimension(165, 220));

        tile.add(
            new HinhSanPham(p.loaiHinh),
            BorderLayout.NORTH
        );

        JPanel info = vertical();

        info.add(text(
            p.ten, 12, Font.BOLD, TEXT
        ));

        info.add(Box.createVerticalStrut(4));

        info.add(text(
            p.ma, 10, Font.PLAIN, MUTED
        ));

        info.add(Box.createVerticalStrut(5));

        info.add(text(
            tien(p.gia),
            13,
            Font.BOLD,
            new Color(161, 192, 247)
        ));

        info.add(Box.createVerticalStrut(5));

        String uuDai = p.giamPhanTram > 0
            ? "Ưu đãi: -" + p.giamPhanTram + "%"
            : "Giá đã gồm VAT";

        info.add(text(
            uuDai,
            10,
            Font.PLAIN,
            p.giamPhanTram > 0 ? GREEN : MUTED
        ));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        bottom.add(
            text("Còn " + p.ton, 10, Font.PLAIN, MUTED),
            BorderLayout.WEST
        );

        JButton add = secondary("+ Thêm");

        add.setEnabled(p.ton > 0);

        add.addActionListener(e -> themSanPham(p));

        bottom.add(add, BorderLayout.EAST);

        info.add(Box.createVerticalStrut(6));
        info.add(bottom);

        tile.add(info, BorderLayout.CENTER);

        return tile;
    }

    // ==================== KHU VỰC BÁN HÀNG ====================

    private JPanel taoKhuBanHang() {

        JPanel right = new JPanel(new BorderLayout(0, 10));

        right.setOpaque(false);
        right.setMinimumSize(new Dimension(0, 0));

        // Khu vực khách hàng ở trên cùng
        JPanel khachHang = taoKhachHangCard();
        khachHang.setPreferredSize(new Dimension(0, 135));

        // Giỏ hàng nằm ở giữa, tự co giãn
        JPanel gioHangPanel = taoGioHangCard();

        // Khu vực tính tiền luôn ở dưới cùng
        JPanel thanhToan = taoThanhToanCard();
        thanhToan.setPreferredSize(new Dimension(0, 285));

        right.add(khachHang, BorderLayout.NORTH);
        right.add(gioHangPanel, BorderLayout.CENTER);
        right.add(thanhToan, BorderLayout.SOUTH);

        return right;
    }

    // ==================== KHÁCH HÀNG ====================

    private JPanel taoKhachHangCard() {

        JPanel panel = card(new BorderLayout(0, 9));

        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(12, 13, 12, 13)
        ));

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 154)
        );

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);

        head.add(
            text("Khách hàng", 14, Font.BOLD, TEXT),
            BorderLayout.WEST
        );

        JButton btnVangLai =
            secondary("Chọn khách vãng lai");

        btnVangLai.addActionListener(e -> {
            khachDaChon = null;
            capNhatGioHang();
        });

        head.add(btnVangLai, BorderLayout.EAST);

        panel.add(head, BorderLayout.NORTH);

        JPanel center = vertical();

        JPanel search = new JPanel(
            new BorderLayout(6, 0)
        );

        search.setOpaque(false);

        txtTimKhach.setToolTipText(
            "Nhập số điện thoại hoặc tên khách hàng"
        );

        search.add(txtTimKhach, BorderLayout.CENTER);

        JButton btnTim = secondary("Tìm");

        btnTim.addActionListener(e -> timKhach());

        JButton btnThem = primary("+ Khách mới");

        btnThem.addActionListener(e -> moThemKhach());

        JPanel buttons = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 5, 0)
        );

        buttons.setOpaque(false);
        buttons.add(btnTim);
        buttons.add(btnThem);

        search.add(buttons, BorderLayout.EAST);

        center.add(search);
        center.add(Box.createVerticalStrut(11));
        center.add(lblKhach);

        panel.add(center, BorderLayout.CENTER);

        return panel;
    }

    // ==================== GIỎ HÀNG ====================

    private JPanel taoGioHangCard() {

        JPanel panel = card(new BorderLayout(0, 10));

        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(12, 13, 12, 13)
        ));

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 350)
        );

        panel.add(lblGio, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(cartRows);

        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD);

        scroll.setPreferredSize(new Dimension(0, 240));
        scroll.getVerticalScrollBar().setUnitIncrement(15);

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void themSanPham(SanPham p) {

        int dangCo = gioHang.getOrDefault(p, 0);

        if (dangCo >= p.ton) {

            thongBao(
                "Số lượng không được vượt quá hàng hiện có."
            );

            return;
        }

        gioHang.put(p, dangCo + 1);

        capNhatGioHang();
    }

    private void doiSoLuong(SanPham p, int delta) {

        int moi = gioHang.getOrDefault(p, 0) + delta;

        if (moi > p.ton) {

            thongBao("Không đủ số lượng hàng để bán.");
            return;
        }

        if (moi <= 0) {
            gioHang.remove(p);
        } else {
            gioHang.put(p, moi);
        }

        capNhatGioHang();
    }

    private void capNhatGioHang() {

        cartRows.removeAll();

        int totalCount = 0;

        for (Map.Entry<SanPham, Integer> entry
                : gioHang.entrySet()) {

            SanPham p = entry.getKey();
            int qty = entry.getValue();

            totalCount += qty;

            JPanel row = new JPanel(
                new BorderLayout(6, 4)
            );

            row.setOpaque(false);

            row.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(8, 2, 8, 2)
            ));

            row.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 75)
            );

            JPanel name = vertical();

            name.add(text(
                p.ten, 11, Font.BOLD, TEXT
            ));

            name.add(Box.createVerticalStrut(5));

            name.add(text(
                p.ma + " · " + tien(p.gia),
                10,
                Font.PLAIN,
                MUTED
            ));

            row.add(name, BorderLayout.CENTER);

            // Nút tăng giảm số lượng

            JPanel qtyButtons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 4, 0)
            );

            qtyButtons.setOpaque(false);

            JButton minus = secondary("−");
            JButton plus = secondary("+");
            JButton del = secondary("×");

            minus.addActionListener(e ->
                doiSoLuong(p, -1)
            );

            plus.addActionListener(e ->
                doiSoLuong(p, 1)
            );

            del.addActionListener(e -> {
                gioHang.remove(p);
                capNhatGioHang();
            });

            qtyButtons.add(minus);

            qtyButtons.add(text(
                String.valueOf(qty),
                12,
                Font.BOLD,
                TEXT
            ));

            qtyButtons.add(plus);
            qtyButtons.add(del);

            row.add(qtyButtons, BorderLayout.EAST);

            JPanel amount = new JPanel(
                new BorderLayout()
            );

            amount.setOpaque(false);

            amount.add(
                text("Thành tiền (đã giảm)",
                    10, Font.PLAIN, MUTED),
                BorderLayout.WEST
            );

            amount.add(
                text(
                    tien(p.giaSauGiam() * qty),
                    11,
                    Font.BOLD,
                    new Color(161, 192, 247)
                ),
                BorderLayout.EAST
            );

            row.add(amount, BorderLayout.SOUTH);

            cartRows.add(row);
        }

        if (gioHang.isEmpty()) {

            JLabel empty = text(
                "Chưa có sản phẩm trong giỏ hàng.",
                12,
                Font.PLAIN,
                MUTED
            );

            empty.setBorder(
                new EmptyBorder(24, 10, 24, 0)
            );

            cartRows.add(empty);
        }

        lblGio.setText(
            "Giỏ hàng (" + totalCount + ")"
        );

        if (khachDaChon == null) {

            lblKhach.setText(
                "Khách vãng lai · không tích điểm"
            );

        } else {

            lblKhach.setText(
                khachDaChon.ten
                + " · " + khachDaChon.diem
                + " điểm"
            );
        }

        cbDungDiem.setEnabled(
            khachDaChon != null
            && khachDaChon.diem > 0
        );

        if (!cbDungDiem.isEnabled()) {
            cbDungDiem.setSelected(false);
        }

        long gross = tongHang();
        long discount = tongGiam();

        long points = 0;

        if (cbDungDiem.isSelected()
                && khachDaChon != null) {

            points = Math.min(
                gross - discount,
                khachDaChon.diem * 1000L
            );
        }

        lblTongHang.setText(tien(gross));
        lblGiam.setText("−" + tien(discount));
        lblDiem.setText("−" + tien(points));

        lblThanhToan.setText(
            tien(gross - discount - points)
        );

        btnThanhToan.setEnabled(
            !gioHang.isEmpty()
        );

        cartRows.revalidate();
        cartRows.repaint();
    }

    // ==================== TÍNH TIỀN ====================

    private long tongHang() {

        long sum = 0;

        for (Map.Entry<SanPham, Integer> e
                : gioHang.entrySet()) {

            sum += e.getKey().gia * e.getValue();
        }

        return sum;
    }

    private long tongGiam() {

        long sum = 0;

        for (Map.Entry<SanPham, Integer> e
                : gioHang.entrySet()) {

            SanPham p = e.getKey();

            sum += (
                p.gia - p.giaSauGiam()
            ) * e.getValue();
        }

        return sum;
    }

    // ==================== THANH TOÁN CARD ====================

    private JPanel taoThanhToanCard() {

        JPanel panel = card(new BorderLayout(0, 9));

        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(12, 13, 12, 13)
        ));

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 305)
        );

        JPanel box = vertical();

        box.add(text(
            "Ưu đãi cá nhân", 13, Font.BOLD, TEXT
        ));

        box.add(Box.createVerticalStrut(7));

        JPanel promo = new JPanel(
            new BorderLayout(6, 0)
        );

        promo.setOpaque(false);
        promo.setAlignmentX(Component.LEFT_ALIGNMENT);

        promo.add(
            txtMaUuDai,
            BorderLayout.CENTER
        );

        JButton apDung = secondary("Áp dụng");

        apDung.addActionListener(e ->
            thongBao(
                "Chức năng xác minh mã ưu đãi "
                + "sẽ kết nối Service sau."
            )
        );

        promo.add(apDung, BorderLayout.EAST);

        box.add(promo);
        box.add(Box.createVerticalStrut(9));

        cbDungDiem.setOpaque(false);
        cbDungDiem.setForeground(MUTED);
        cbDungDiem.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbDungDiem.setFont(
            new Font("SansSerif", Font.PLAIN, 11)
        );

        cbDungDiem.addActionListener(e ->
            capNhatGioHang()
        );

        box.add(cbDungDiem);
        box.add(Box.createVerticalStrut(12));

        box.add(tongDong(
            "Tổng tiền hàng", lblTongHang
        ));

        box.add(tongDong(
            "Khuyến mãi sản phẩm", lblGiam
        ));

        box.add(tongDong(
            "Giảm bằng điểm", lblDiem
        ));

        box.add(Box.createVerticalStrut(10));

        JSeparator line = new JSeparator();
        line.setAlignmentX(Component.LEFT_ALIGNMENT);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        box.add(line);
        box.add(Box.createVerticalStrut(10));

        box.add(tongDong(
            "Khách cần thanh toán",
            lblThanhToan
        ));

        box.add(Box.createVerticalStrut(12));

        btnThanhToan.addActionListener(e ->
            moThanhToan()
        );

        btnThanhToan.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        btnThanhToan.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 38)
        );

        box.add(btnThanhToan);

        panel.add(box, BorderLayout.CENTER);

        return panel;
    }

    private JPanel tongDong(
            String title, JLabel number) {

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        row.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 34)
        );

        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(
            text(title, 11, Font.PLAIN, MUTED),
            BorderLayout.WEST
        );

        row.add(number, BorderLayout.EAST);

        return row;
    }

    // ==================== TÌM KHÁCH HÀNG ====================

    private void timKhach() {

        String search = txtTimKhach.getText()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (search.isEmpty()) {

            thongBao(
                "Nhập tên hoặc số điện thoại khách hàng."
            );

            return;
        }

        List<KhachHang> found = new ArrayList<>();

        for (KhachHang c : khachHangs) {

            if (c.ten.toLowerCase(Locale.ROOT).contains(search)
                    || c.sdt.contains(search)) {

                found.add(c);
            }
        }

        if (found.isEmpty()) {

            thongBao(
                "Không tìm thấy khách hàng. "
                + "Có thể thêm khách mới."
            );

            return;
        }

        KhachHang selected = (KhachHang)
            JOptionPane.showInputDialog(
                this,
                "Chọn khách hàng:",
                "Kết quả tìm kiếm",
                JOptionPane.PLAIN_MESSAGE,
                null,
                found.toArray(),
                found.get(0)
            );

        if (selected != null) {

            khachDaChon = selected;
            capNhatGioHang();
        }
    }

    // ==================== THÊM KHÁCH HÀNG ====================

    private void moThemKhach() {

        Window owner =
            SwingUtilities.getWindowAncestor(this);

        JDialog dialog = new JDialog(
            owner,
            "Thêm khách hàng",
            Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setSize(620, 450);
        dialog.setLocationRelativeTo(owner);

        JPanel body = card(
            new BorderLayout(0, 16)
        );

        body.setBorder(
            new EmptyBorder(22, 25, 22, 25)
        );

        JPanel title = vertical();

        title.add(text(
            "Thêm khách hàng",
            22,
            Font.BOLD,
            TEXT
        ));

        title.add(Box.createVerticalStrut(7));

        title.add(text(
            "Tra cứu số điện thoại trước khi tạo "
            + "để tránh trùng hồ sơ.",
            12,
            Font.PLAIN,
            MUTED
        ));

        body.add(title, BorderLayout.NORTH);

        JPanel fields = new JPanel(
            new GridLayout(2, 2, 12, 14)
        );

        fields.setOpaque(false);

        JTextField ten = input();
        JTextField sdt = input();
        JTextField email = input();
        JTextField diaChi = input();

        fields.add(field("Họ tên *", ten));
        fields.add(field("Số điện thoại *", sdt));

        fields.add(field(
            "Email (không bắt buộc)", email
        ));

        fields.add(field(
            "Địa chỉ (không bắt buộc)", diaChi
        ));

        JPanel middle = new JPanel(
            new BorderLayout(0, 14)
        );

        middle.setOpaque(false);
        middle.add(fields, BorderLayout.NORTH);

        middle.add(
            text(
                "Khách mới bắt đầu với 0 điểm. "
                + "Điểm chỉ cộng sau khi thanh toán.",
                11,
                Font.PLAIN,
                MUTED
            ),
            BorderLayout.CENTER
        );

        body.add(middle, BorderLayout.CENTER);

        JPanel actions = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );

        actions.setOpaque(false);

        JButton close = secondary("Để sau");
        JButton save = primary("Thêm & chọn khách");

        close.addActionListener(e ->
            dialog.dispose()
        );

        save.addActionListener(e -> {

            String name = ten.getText().trim();
            String phone = sdt.getText().trim();
            String mail = email.getText().trim();

            if (name.isEmpty()
                    || !phone.matches("0\\d{9}")) {

                JOptionPane.showMessageDialog(
                    dialog,
                    "Họ tên không được trống.\n"
                    + "SĐT phải có 10 chữ số, bắt đầu bằng 0."
                );

                return;
            }

            if (!mail.isEmpty()
                    && !mail.matches(
                        "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
                    )) {

                JOptionPane.showMessageDialog(
                    dialog,
                    "Email không đúng định dạng."
                );

                return;
            }

            for (KhachHang c : khachHangs) {

                if (c.sdt.equals(phone)) {

                    JOptionPane.showMessageDialog(
                        dialog,
                        "Số điện thoại đã có hồ sơ.\n"
                        + "Hãy tìm và chọn khách."
                    );

                    return;
                }
            }

            KhachHang c = new KhachHang(
                String.format(
                    "KH%09d",
                    khachHangs.size() + 1
                ),
                name,
                phone,
                mail,
                diaChi.getText().trim(),
                0
            );

            khachHangs.add(c);
            khachDaChon = c;

            txtTimKhach.setText(phone);

            capNhatGioHang();
            dialog.dispose();
        });

        actions.add(close);
        actions.add(save);

        body.add(actions, BorderLayout.SOUTH);

        dialog.setContentPane(body);
        dialog.setVisible(true);
    }

    // ==================== BƯỚC THANH TOÁN ====================

    private void moThanhToan() {

        if (gioHang.isEmpty()) {

            thongBao("Giỏ hàng đang trống.");
            return;
        }

        long total = tongHang() - tongGiam();

        if (cbDungDiem.isSelected()
                && khachDaChon != null) {

            total -= Math.min(
                total,
                khachDaChon.diem * 1000L
            );
        }

        JComboBox<String> method =
            new JComboBox<>(new String[] {
                "Tiền mặt",
                "Chuyển khoản"
            });

        JTextField cash = new JTextField(
            String.valueOf(total)
        );

        JPanel form = new JPanel(
            new GridLayout(0, 1, 6, 5)
        );

        form.add(new JLabel(
            "Khách cần thanh toán: " + tien(total)
        ));

        form.add(new JLabel(
            "Phương thức thanh toán:"
        ));

        form.add(method);

        form.add(new JLabel(
            "Số tiền nhận (tiền mặt):"
        ));

        form.add(cash);

        method.addActionListener(e ->
            cash.setEnabled(
                method.getSelectedIndex() == 0
            )
        );

        int result = JOptionPane.showConfirmDialog(
            this,
            form,
            "Bước thanh toán (demo)",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        if (method.getSelectedIndex() == 0) {

            long paid;

            try {

                paid = Long.parseLong(
                    cash.getText().trim()
                );

            } catch (NumberFormatException ex) {

                thongBao(
                    "Số tiền nhận phải là số nguyên VND hợp lệ."
                );

                return;
            }

            if (paid < total) {

                thongBao(
                    "Tiền nhận chưa đủ để thanh toán."
                );

                return;
            }

            thongBao(
                "Tiền thừa dự kiến: "
                + tien(paid - total)
                + "\nĐây là bản UI demo."
                + "\nChưa tạo hóa đơn hay trừ tồn."
            );

        } else {

            thongBao(
                "Cần xác minh tiền đã vào tài khoản "
                + "trước khi hoàn tất."
                + "\nBản UI chưa kết nối CSDL."
            );
        }

        // Không xóa giỏ vì chưa thực hiện giao dịch thật.
    }

    // ==================== HÀM GIAO DIỆN DÙNG CHUNG ====================

    private void thongBao(String message) {

        JOptionPane.showMessageDialog(
            this,
            message,
            "Green Home",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

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

    private static JLabel text(
            String value,
            int size,
            int style,
            Color color) {

        JLabel l = new JLabel(value);

        l.setForeground(color);

        l.setFont(
            new Font("SansSerif", style, size)
        );

        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        return l;
    }

    private static JTextField input() {

        JTextField f = new JTextField();

        f.setForeground(TEXT);
        f.setCaretColor(TEXT);
        f.setBackground(INPUT);

        f.setFont(
            new Font("SansSerif", Font.PLAIN, 12)
        );

        f.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(8, 9, 8, 9)
        ));

        f.setPreferredSize(
            new Dimension(140, 36)
        );

        f.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 36)
        );

        return f;
    }

    private static void styleCombo(
            JComboBox<String> combo) {

        combo.setBackground(INPUT);
        combo.setForeground(TEXT);
        combo.setFocusable(false);

        combo.setFont(
            new Font("SansSerif", Font.PLAIN, 11)
        );

        combo.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 36)
        );
    }

    private static JButton secondary(String name) {

        JButton b = new JButton(name);

        b.setBackground(
            new Color(30, 33, 38)
        );

        b.setForeground(TEXT);

        b.setFont(
            new Font("SansSerif", Font.PLAIN, 11)
        );

        b.setFocusPainted(false);

        b.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(7, 9, 7, 9)
        ));

        return b;
    }

    private static JButton primary(String name) {

        JButton b = new JButton(name);

        b.setBackground(BLUE);
        b.setForeground(Color.WHITE);

        b.setFont(
            new Font("SansSerif", Font.BOLD, 11)
        );

        b.setFocusPainted(false);

        b.setBorder(
            new EmptyBorder(9, 13, 9, 13)
        );

        return b;
    }

    private static JPanel field(
            String label,
            JTextField f) {

        JPanel p = new JPanel(
            new BorderLayout(0, 6)
        );

        p.setOpaque(false);

        p.add(
            text(label, 12, Font.BOLD, MUTED),
            BorderLayout.NORTH
        );

        p.add(f, BorderLayout.CENTER);

        return p;
    }

    private static String tien(long amount) {

        NumberFormat format =
            NumberFormat.getIntegerInstance(
                Locale.forLanguageTag("vi-VN")
            );

        return format.format(amount) + " đ";
    }

    // ==================== MODEL TẠM ====================

    private static class SanPham {

        final String ma;
        final String ten;
        final String danhMuc;
        final String loaiHinh;

        final long gia;

        final int ton;
        final int giamPhanTram;

        SanPham(
                String ma,
                String ten,
                String danhMuc,
                long gia,
                int ton,
                int giam,
                String loaiHinh) {

            this.ma = ma;
            this.ten = ten;
            this.danhMuc = danhMuc;

            this.gia = gia;
            this.ton = ton;

            this.giamPhanTram = giam;
            this.loaiHinh = loaiHinh;
        }

        long giaSauGiam() {

            return gia
                * (100 - giamPhanTram)
                / 100;
        }
    }

    private static class KhachHang {

        final String ma;
        final String ten;
        final String sdt;
        final String email;
        final String diaChi;

        final int diem;

        KhachHang(
                String ma,
                String ten,
                String sdt,
                String email,
                String diaChi,
                int diem) {

            this.ma = ma;
            this.ten = ten;
            this.sdt = sdt;

            this.email = email;
            this.diaChi = diaChi;

            this.diem = diem;
        }

        @Override
        public String toString() {

            return ten + " - " + sdt;
        }
    }

    // ==================== HÌNH SẢN PHẨM ====================

    private static class HinhSanPham extends JPanel {

        private final String kind;

        HinhSanPham(String kind) {

            this.kind = kind;

            setPreferredSize(
                new Dimension(155, 87)
            );

            setBackground(
                new Color(35, 39, 45)
            );
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D p = (Graphics2D) g.create();

            p.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;

            p.setColor(
                new Color(160, 188, 211)
            );

            switch (kind) {

                case "phone":

                    p.rotate(-0.18, cx, cy);

                    p.fillRoundRect(
                        cx - 22, cy - 35,
                        43, 70, 9, 9
                    );

                    p.setColor(
                        new Color(71, 118, 157)
                    );

                    p.fillRoundRect(
                        cx - 17, cy - 29,
                        33, 56, 5, 5
                    );

                    break;

                case "laptop":

                    p.fillRoundRect(
                        cx - 49, cy - 29,
                        98, 57, 6, 6
                    );

                    p.setColor(
                        new Color(79, 130, 163)
                    );

                    p.fillRect(
                        cx - 43, cy - 23,
                        86, 43
                    );

                    p.setColor(
                        new Color(210, 217, 224)
                    );

                    p.fillRoundRect(
                        cx - 57, cy + 27,
                        114, 7, 3, 3
                    );

                    break;

                case "tv":

                    p.fillRoundRect(
                        cx - 52, cy - 29,
                        104, 55, 4, 4
                    );

                    p.setColor(
                        new Color(82, 143, 171)
                    );

                    p.fillRect(
                        cx - 46, cy - 23,
                        92, 43
                    );

                    p.setColor(
                        new Color(209, 219, 224)
                    );

                    p.fillRect(
                        cx - 3, cy + 26,
                        6, 10
                    );

                    break;

                case "headphone":

                    p.setStroke(
                        new BasicStroke(7f)
                    );

                    p.drawArc(
                        cx - 32, cy - 25,
                        64, 57, 5, 170
                    );

                    p.fillRoundRect(
                        cx - 37, cy + 1,
                        15, 26, 8, 8
                    );

                    p.fillRoundRect(
                        cx + 22, cy + 1,
                        15, 26, 8, 8
                    );

                    break;

                case "fridge":

                    p.fillRoundRect(
                        cx - 28, cy - 38,
                        56, 77, 8, 8
                    );

                    p.setColor(
                        new Color(77, 110, 137)
                    );

                    p.drawLine(
                        cx - 28, cy - 4,
                        cx + 28, cy - 4
                    );

                    break;

                default:

                    p.fillRoundRect(
                        cx - 38, cy - 38,
                        76, 77, 8, 8
                    );

                    p.setColor(
                        new Color(73, 110, 138)
                    );

                    p.fillOval(
                        cx - 27, cy - 19,
                        54, 54
                    );

                    p.setColor(
                        new Color(179, 212, 234)
                    );

                    p.fillOval(
                        cx - 20, cy - 12,
                        40, 40
                    );

                    break;
            }

            p.dispose();
        }
    }
}