package ui;

import java.awt.*;
import java.awt.print.PrinterException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Giao diện tra cứu và chi tiết hóa đơn.
 * Hiện tại sử dụng dữ liệu mẫu, chưa kết nối CSDL.
 */
public final class HoaDonDialog {

    // ==================== MAU SAC ====================

    private static final Color BG = new Color(18, 19, 21);
    private static final Color PANEL = new Color(29, 31, 34);
    private static final Color BORDER = new Color(57, 61, 67);

    private static final Color TEXT = new Color(225, 232, 242);
    private static final Color MUTED = new Color(143, 155, 173);

    private static final Color BLUE = new Color(54, 111, 232);
    private static final Color GREEN = new Color(56, 176, 125);

    // ==================== DU LIEU MAU ====================

    private static final List<HoaDon> HOA_DON = new ArrayList<>();



    private HoaDonDialog() {
    }

    // ==================== DANH SACH HOA DON ====================

    public static void moDanhSach(Component parent) {

        JDialog dialog = taoDialog(
            parent, "Hóa đơn bán hàng", 850, 640
        );

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        root.add(
            header(
                "Hóa đơn bán hàng",
                "Tra cứu và in lại hóa đơn đã thanh toán.",
                dialog
            ),
            BorderLayout.NORTH
        );

        JPanel center = new JPanel(
            new BorderLayout(0, 16)
        );

        center.setBackground(BG);

        center.setBorder(
            new EmptyBorder(20, 24, 16, 24)
        );

        // O tim kiem

        JTextField txtTim = new HintTextField(
            "Tìm mã hóa đơn, tên hoặc số điện thoại khách..."
        );

        styleInput(txtTim);

        txtTim.setToolTipText(
            "Tìm theo mã hóa đơn, tên hoặc số điện thoại khách hàng"
        );

        txtTim.setPreferredSize(
            new Dimension(0, 43)
        );

        center.add(txtTim, BorderLayout.NORTH);

        // Danh sach hoa don

        JPanel list = vertical();

        JScrollPane scroll = new JScrollPane(list);

        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        center.add(scroll, BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);

        // Footer

        JButton dong = button("Đóng", false);

        dong.addActionListener(e -> dialog.dispose());

        JPanel footer = footer();
        footer.add(dong);

        root.add(footer, BorderLayout.SOUTH);

        // ==================== TIM KIEM ====================

        Runnable loc = () -> {

            String query = txtTim.getText()
                    .trim()
                    .toLowerCase(Locale.ROOT);

            list.removeAll();

            int count = 0;

            for (HoaDon h : HOA_DON) {

                String searchable = (
                    h.ma + " " + h.khach + " " + h.sdt
                ).toLowerCase(Locale.ROOT);

                if (searchable.contains(query)) {

                    list.add(dongHoaDon(h, dialog));

                    list.add(
                        Box.createVerticalStrut(12)
                    );

                    count++;
                }
            }

            if (count == 0) {

                JLabel empty = label(
                    "Không tìm thấy hóa đơn phù hợp.",
                    13,
                    MUTED,
                    Font.PLAIN
                );

                empty.setBorder(
                    new EmptyBorder(24, 10, 10, 0)
                );

                list.add(empty);
            }

            list.revalidate();
            list.repaint();
        };

        txtTim.getDocument().addDocumentListener(
            new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    loc.run();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    loc.run();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    loc.run();
                }
            }
        );

        loc.run();

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    // ==================== MOT HOA DON TRONG DANH SACH ====================

    private static JPanel dongHoaDon(
            HoaDon h, JDialog parent) {

        JPanel row = bordered(
            new BorderLayout(12, 0)
        );

        row.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(15, 16, 15, 16)
        ));

        row.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 105)
        );

        row.setPreferredSize(
            new Dimension(0, 105)
        );

        JPanel info = vertical();

        JPanel top = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 8, 0)
        );

        top.setOpaque(false);
        top.setAlignmentX(Component.LEFT_ALIGNMENT);

        top.add(label(
            h.ma, 15, TEXT, Font.BOLD
        ));

        JLabel status = label(
            "Đã thanh toán",
            11,
            GREEN,
            Font.BOLD
        );

        status.setOpaque(true);

        status.setBackground(
            new Color(27, 47, 38)
        );

        status.setBorder(
            new EmptyBorder(3, 6, 3, 6)
        );

        top.add(status);

        info.add(top);
        info.add(Box.createVerticalStrut(8));

        info.add(label(
            h.khach + " · " + h.thoiGian,
            12, MUTED, Font.PLAIN
        ));

        info.add(Box.createVerticalStrut(5));

        info.add(label(
            h.phuongThuc + " · " + h.soLuong + " sản phẩm",
            12, MUTED, Font.PLAIN
        ));

        row.add(info, BorderLayout.CENTER);

        // Khu vuc ben phai

        JPanel right = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 12, 12)
        );

        right.setOpaque(false);

        right.add(label(
            tien(h.thucThanhToan()),
            14,
            new Color(155, 185, 238),
            Font.BOLD
        ));

        JButton xem = button("Xem / In", false);

        xem.addActionListener(e ->
            moChiTiet(parent, h.ma)
        );

        right.add(xem);

        row.add(right, BorderLayout.EAST);

        return row;
    }

    // ==================== CHI TIET HOA DON ====================

    public static void moChiTiet(
            Component parent, String maHoaDon) {

        HoaDon h = timHoaDon(maHoaDon);

        if (h == null) {

            JOptionPane.showMessageDialog(
                parent,
                "Không tìm thấy hóa đơn: " + maHoaDon
            );

            return;
        }

        JDialog dialog = taoDialog(
            parent,
            "Hóa đơn " + h.ma,
            880,
            800
        );

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        root.add(
            header(
                "Hóa đơn " + h.ma,
                "Đối chiếu sản phẩm và bàn giao tại quầy.",
                dialog
            ),
            BorderLayout.NORTH
        );

        // ==================== THAN HOA DON ====================

        JPanel receipt = vertical();

        receipt.setOpaque(true);
        receipt.setBackground(PANEL);

        receipt.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(26, 26, 28, 26)
        ));

        // Logo va tieu de

        JPanel invoiceTitle = vertical();

        invoiceTitle.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        invoiceTitle.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 79)
        );

        JLabel brand = label(
            "GREEN HOME",
            20,
            TEXT,
            Font.BOLD
        );

        brand.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        invoiceTitle.add(brand);
        invoiceTitle.add(Box.createVerticalStrut(7));

        JLabel sub = label(
            "HÓA ĐƠN BÁN HÀNG - BẢN MẪU",
            12,
            MUTED,
            Font.BOLD
        );

        sub.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        invoiceTitle.add(sub);

        receipt.add(invoiceTitle);
        receipt.add(Box.createVerticalStrut(20));

        // ==================== THONG TIN GIAO DICH ====================

        receipt.add(line(
            "Mã hóa đơn", h.ma
        ));

        receipt.add(line(
            "Thời gian", h.thoiGian
        ));

        receipt.add(line(
            "Nhân viên", h.nhanVien
        ));

        receipt.add(line(
            "Khách hàng", h.khach
        ));

        receipt.add(line(
            "Số điện thoại",
            h.sdt.isEmpty() ? "Không cung cấp" : h.sdt
        ));

        receipt.add(divider());

        // ==================== SAN PHAM ====================

        receipt.add(line(
            h.sanPham,
            tien(h.thucThanhToan()),
            true
        ));

        receipt.add(textLine(
            h.soLuong + " × " + tien(h.donGia)
            + " · Khuyến mãi -" + tien(h.giamGia)
        ));

        if (!h.serial.isEmpty()) {

            receipt.add(textLine(
                "Serial/IMEI: " + h.serial
            ));
        }

        receipt.add(divider());

        // ==================== CAC KHOAN TIEN ====================

        receipt.add(line(
            "Tổng tiền hàng (đã gồm VAT)",
            tien(h.tongTienHang())
        ));

        receipt.add(line(
            "Khuyến mãi sản phẩm",
            "−" + tien(h.giamGia),
            false,
            GREEN
        ));

        receipt.add(line(
            "Ưu đãi cá nhân",
            "−0 đ",
            false,
            GREEN
        ));

        receipt.add(line(
            "Giảm bằng điểm",
            "−0 đ",
            false,
            GREEN
        ));

        receipt.add(divider());

        receipt.add(line(
            "Thực thanh toán",
            tien(h.thucThanhToan()),
            true,
            new Color(145, 180, 255)
        ));

        receipt.add(Box.createVerticalStrut(12));

        // ==================== THANH TOAN ====================

        receipt.add(line(
            "Phương thức", h.phuongThuc
        ));

        receipt.add(line(
            "Tiền khách đưa", tien(h.tienNhan)
        ));

        receipt.add(line(
            "Tiền thừa đã trả",
            tien(h.tienNhan - h.thucThanhToan())
        ));

        receipt.add(line(
            "Điểm được cộng / số dư sau giao dịch",
            "+" + h.diemCong
            + " / " + h.soDuDiem + " điểm"
        ));

        receipt.add(Box.createVerticalStrut(13));

        // ==================== GHI CHU ====================

        receipt.add(textLine(
            "Đối chiếu sản phẩm, số lượng và Serial/IMEI "
            + "trước khi bàn giao."
        ));

        receipt.add(textLine(
            "Hỗ trợ đổi/trả trong 7 ngày "
            + "theo chính sách cửa hàng."
        ));

        receipt.add(textLine(
            "Hóa đơn đã thanh toán được giữ nguyên "
            + "để tra cứu và in lại."
        ));

        receipt.add(divider());

        // ==================== DOI TRA LIEN QUAN ====================

        JLabel related = label(
            "Đổi / trả liên quan",
            14,
            new Color(162, 190, 236),
            Font.BOLD
        );

        related.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        receipt.add(related);
        receipt.add(Box.createVerticalStrut(12));

        receipt.add(textLine(
            "Chưa có phiếu đổi/trả trong dữ liệu mẫu."
        ));

        // ==================== SCROLL ====================

        JPanel receiptWrap = new JPanel(
            new BorderLayout()
        );

        receiptWrap.setBackground(BG);

        receiptWrap.setBorder(
            new EmptyBorder(20, 24, 20, 24)
        );

        receiptWrap.add(
            receipt, BorderLayout.CENTER
        );

        JScrollPane scroll = new JScrollPane(
            receiptWrap
        );

        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);

        scroll.getVerticalScrollBar()
              .setUnitIncrement(16);

        root.add(scroll, BorderLayout.CENTER);

        // ==================== FOOTER ====================

        JPanel footer = footer();

        JButton danhSach = button(
            "Danh sách hóa đơn", false
        );

        danhSach.addActionListener(e -> {

            dialog.dispose();

            moDanhSach(parent);
        });

        JButton in = button(
            "In hóa đơn", true
        );

        in.addActionListener(e ->
            inHoaDon(dialog, h)
        );

        footer.add(danhSach);
        footer.add(in);

        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    // ==================== IN HOA DON ====================

    private static void inHoaDon(
            Component parent, HoaDon h) {

        JTextArea print = new JTextArea();

        print.setFont(
            new Font(Font.MONOSPACED, Font.PLAIN, 12)
        );

        StringBuilder sb = new StringBuilder();

        sb.append("              GREEN HOME\n");
        sb.append("        HOA DON BAN HANG - BAN MAU\n\n");

        sb.append("Ma hoa don: ")
          .append(h.ma).append('\n');

        sb.append("Thoi gian: ")
          .append(h.thoiGian).append('\n');

        sb.append("Nhan vien: ")
          .append(h.nhanVien).append('\n');

        sb.append("Khach hang: ")
          .append(h.khach).append('\n');

        sb.append("SDT: ")
          .append(h.sdt.isEmpty()
              ? "Khong cung cap" : h.sdt)
          .append('\n');

        sb.append("------------------------------------------\n");

        sb.append(h.sanPham).append('\n');

        sb.append(h.soLuong)
          .append(" x ")
          .append(tien(h.donGia))
          .append('\n');

        if (!h.serial.isEmpty()) {

            sb.append("Serial/IMEI: ")
              .append(h.serial)
              .append('\n');
        }

        sb.append("Tong tien hang: ")
          .append(tien(h.tongTienHang()))
          .append('\n');

        sb.append("Khuyen mai: -")
          .append(tien(h.giamGia))
          .append('\n');

        sb.append("Thuc thanh toan: ")
          .append(tien(h.thucThanhToan()))
          .append('\n');

        sb.append("Phuong thuc: ")
          .append(h.phuongThuc)
          .append('\n');

        sb.append("Tien khach dua: ")
          .append(tien(h.tienNhan))
          .append('\n');

        sb.append("Tien thua: ")
          .append(tien(h.tienNhan - h.thucThanhToan()))
          .append('\n');

        sb.append("Diem cong: +")
          .append(h.diemCong)
          .append(" / So du: ")
          .append(h.soDuDiem)
          .append('\n');

        sb.append("------------------------------------------\n");
        sb.append("Cam on quy khach!\n");

        print.setText(sb.toString());

        try {

            print.print();

        } catch (PrinterException ex) {

            JOptionPane.showMessageDialog(
                parent,
                "Không thể in hóa đơn: " + ex.getMessage(),
                "Lỗi in",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ==================== CAC HAM HO TRO ====================

    private static JDialog taoDialog(
            Component parent,
            String title,
            int width,
            int height) {

        Window owner = parent == null
                ? null
                : SwingUtilities.getWindowAncestor(parent);

        JDialog dialog = new JDialog(
            owner,
            title,
            Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setSize(width, height);

        dialog.setMinimumSize(
            new Dimension(
                Math.min(width, 650),
                Math.min(height, 510)
            )
        );

        dialog.setDefaultCloseOperation(
            JDialog.DISPOSE_ON_CLOSE
        );

        dialog.setLocationRelativeTo(parent);

        return dialog;
    }

    private static JPanel header(
            String title,
            String description,
            JDialog dialog) {

        JPanel header = new JPanel(new BorderLayout());

        header.setBackground(BG);

        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(20, 24, 20, 24)
        ));

        JPanel left = vertical();

        left.add(label(
            title, 23, TEXT, Font.BOLD
        ));

        left.add(Box.createVerticalStrut(8));

        left.add(label(
            description, 12, MUTED, Font.PLAIN
        ));

        header.add(left, BorderLayout.WEST);

        JButton x = button("×", false);

        x.addActionListener(e -> dialog.dispose());

        header.add(x, BorderLayout.EAST);

        return header;
    }

    private static JPanel footer() {

        JPanel footer = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 15)
        );

        footer.setBackground(
            new Color(30, 31, 34)
        );

        footer.setBorder(
            new MatteBorder(1, 0, 0, 0, BORDER)
        );

        return footer;
    }

    private static JPanel vertical() {

        JPanel p = new JPanel();

        p.setOpaque(false);

        p.setLayout(
            new BoxLayout(p, BoxLayout.Y_AXIS)
        );

        return p;
    }

    private static JPanel bordered(
            LayoutManager layout) {

        JPanel p = new JPanel(layout);

        p.setBackground(BG);

        p.setBorder(
            new LineBorder(BORDER)
        );

        p.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        return p;
    }

    private static JLabel label(
            String s,
            int size,
            Color color,
            int weight) {

        JLabel l = new JLabel(s);

        l.setFont(
            new Font("SansSerif", weight, size)
        );

        l.setForeground(color);

        l.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        return l;
    }

    private static JButton button(
            String s,
            boolean primary) {

        JButton b = new JButton(s);

        b.setFont(
            new Font("SansSerif", Font.BOLD, 12)
        );

        b.setForeground(
            primary ? Color.WHITE : TEXT
        );

        b.setBackground(
            primary ? BLUE : new Color(36, 39, 44)
        );

        b.setFocusPainted(false);

        b.setBorder(new CompoundBorder(
            new LineBorder(primary ? BLUE : BORDER),
            new EmptyBorder(11, 13, 11, 13)
        ));

        b.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        return b;
    }

    private static void styleInput(JTextField f) {

        f.setBackground(BG);
        f.setForeground(TEXT);
        f.setCaretColor(TEXT);

        f.setFont(
            new Font("SansSerif", Font.PLAIN, 13)
        );

        f.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(9, 12, 9, 12)
        ));
    }

    // ==================== DONG THONG TIN ====================

    private static JPanel line(
            String title,
            String value) {

        return line(title, value, false, MUTED);
    }

    private static JPanel line(
            String title,
            String value,
            boolean bold) {

        return line(title, value, bold, TEXT);
    }

    private static JPanel line(
            String title,
            String value,
            boolean bold,
            Color valueColor) {

        JPanel line = new JPanel(
            new BorderLayout(10, 0)
        );

        line.setOpaque(false);

        line.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        line.setMaximumSize(
            new Dimension(
                Integer.MAX_VALUE,
                bold ? 43 : 32
            )
        );

        line.add(label(
            title,
            bold ? 14 : 12,
            bold ? TEXT : MUTED,
            bold ? Font.BOLD : Font.PLAIN
        ), BorderLayout.WEST);

        line.add(label(
            value,
            bold ? 19 : 12,
            valueColor,
            bold ? Font.BOLD : Font.PLAIN
        ), BorderLayout.EAST);

        return line;
    }

    private static JLabel textLine(String s) {

        JLabel l = label(
            s, 11, MUTED, Font.PLAIN
        );

        l.setBorder(
            new EmptyBorder(2, 0, 4, 0)
        );

        return l;
    }

    private static JSeparator divider() {

        JSeparator separator = new JSeparator();

        separator.setForeground(BORDER);

        separator.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        separator.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 22)
        );

        return separator;
    }

    // ==================== TIM HOA DON ====================

    private static HoaDon timHoaDon(String ma) {

        for (HoaDon h : HOA_DON) {

            if (h.ma.equalsIgnoreCase(ma)) {
                return h;
            }
        }

        return null;
    }

    private static String tien(long n) {

        return NumberFormat.getIntegerInstance(
            Locale.forLanguageTag("vi-VN")
        ).format(n) + " đ";
    }

    // ==================== O TIM KIEM GOI Y ====================

    private static class HintTextField extends JTextField {

        private final String hint;

        HintTextField(String hint) {
            this.hint = hint;
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            if (getText().isEmpty() && !isFocusOwner()) {

                g.setColor(MUTED);
                g.setFont(getFont());

                g.drawString(
                    hint, 12, getHeight() / 2 + 5
                );
            }
        }
    }

    // ==================== MODEL HOA DON MAU ====================

    private static class HoaDon {

        final String ma;
        final String khach;
        final String sdt;
        final String thoiGian;
        final String nhanVien;
        final String sanPham;
        final String serial;
        final String phuongThuc;

        final int soLuong;
        final int diemCong;
        final int soDuDiem;

        final long donGia;
        final long giamGia;
        final long tienNhan;

        HoaDon(
                String ma,
                String khach,
                String sdt,
                String thoiGian,
                String nhanVien,
                String sanPham,
                String serial,
                int soLuong,
                long donGia,
                long giamGia,
                String phuongThuc,
                long tienNhan,
                int diemCong,
                int soDuDiem) {

            this.ma = ma;
            this.khach = khach;
            this.sdt = sdt;
            this.thoiGian = thoiGian;
            this.nhanVien = nhanVien;
            this.sanPham = sanPham;
            this.serial = serial;

            this.soLuong = soLuong;
            this.donGia = donGia;
            this.giamGia = giamGia;

            this.phuongThuc = phuongThuc;
            this.tienNhan = tienNhan;

            this.diemCong = diemCong;
            this.soDuDiem = soDuDiem;
        }

        long tongTienHang() {
            return donGia * soLuong;
        }

        long thucThanhToan() {
            return tongTienHang() - giamGia;
        }
    }

    // ==================== CHAY THU ====================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
            moDanhSach(null)
        );
    }
}