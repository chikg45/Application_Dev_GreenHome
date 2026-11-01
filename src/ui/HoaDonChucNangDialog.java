package ui;

import entity.DongHoaDonBanHang;
import dao.BanHangDAO;

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
 * Đọc và in hóa đơn đã lưu trong SQL Server.
 */
public final class HoaDonChucNangDialog {

    // ==================== MAU SAC ====================

    private static final Color BG = new Color(18, 19, 21);
    private static final Color PANEL = new Color(29, 31, 34);
    private static final Color BORDER = new Color(57, 61, 67);

    private static final Color TEXT = new Color(225, 232, 242);
    private static final Color MUTED = new Color(143, 155, 173);

    private static final Color BLUE = new Color(54, 111, 232);
    private static final Color GREEN = new Color(56, 176, 125);

    // ==================== DU LIEU MAU ====================

    private final BanHangDAO banHangDAO;
    public HoaDonChucNangDialog(BanHangDAO banHangDAO) { this.banHangDAO = banHangDAO; }
    private List<HoaDon> danhSach() { return banHangDAO.getHoaDons().stream().map(HoaDon::new).toList(); }

    // ==================== DANH SACH HOA DON ====================

    public void moDanhSach(Component parent) {

        JDialog dialog = taoDialog(
            parent, "Hóa đơn bán hàng", 850, 640
        );

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        root.add(
            header(
                "Hóa đơn bán hàng",
                "Tra cứu hóa đơn đã lưu trong SQL Server.",
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

            for (HoaDon h : danhSach()) {

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

    private JPanel dongHoaDon(
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

    public void moChiTiet(
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
            "HÓA ĐƠN BÁN HÀNG",
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

        for (DongHoaDonBanHang ct : h.goc.getChiTietHoaDons()) {
            receipt.add(line(ct.getTenSP(), tien(ct.getThucTra()), true));
            receipt.add(textLine(ct.getSoLuong() + " × " + tien(ct.getDonGia())
                + " · Khuyến mãi -" + tien(ct.getGiamKhuyenMai()) + " · Điểm -" + tien(ct.getGiamDiem())));
            if (!ct.getSerialIMEI().isEmpty()) receipt.add(textLine("Serial/IMEI: " + ct.getSerialIMEI()));
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
            "Đã tính trong khuyến mãi",
            false,
            GREEN
        ));

        receipt.add(line(
            "Giảm bằng điểm",
            "−" + tien(h.goc.getGiamDiem()),
            false,
            GREEN
        ));

        receipt.add(divider());

        receipt.add(line("Giá chưa thuế trong tiền hàng", tien(h.goc.getGiaChuaThue())));
        receipt.add(line("VAT đã nằm trong tiền hàng", tien(h.goc.getTienVAT())));
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
            "Chức năng đổi/trả chưa triển khai "
            + "trong phần tiến độ này."
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
            "Chờ tích hợp chức năng Đổi / trả."
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

    private static void inHoaDon(Component parent, HoaDon h) {
        JTextArea print = new JTextArea();
        print.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        print.setText(noiDungIn(h.goc));
        try { print.print(); }
        catch (PrinterException ex) { JOptionPane.showMessageDialog(parent, "Không thể in hóa đơn: " + ex.getMessage()); }
    }

    public static String noiDungIn(entity.HoaDonBanHang h) {
        StringBuilder sb = new StringBuilder("GREEN HOME\nHÓA ĐƠN BÁN HÀNG\n\n");
        sb.append("Mã hóa đơn: ").append(h.getMaHD()).append('\n');
        sb.append("Thời gian: ").append(h.getNgayLap().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"))).append('\n');
        sb.append("Nhân viên: ").append(h.getNhanVien()).append('\n');
        sb.append("Khách hàng: ").append(h.getTenKH()).append(" - ").append(h.getSdtKH()).append('\n');
        for (DongHoaDonBanHang c : h.getChiTietHoaDons()) {
            sb.append(c.getTenSP()).append(" | ").append(c.getSoLuong()).append(" × ").append(tien(c.getDonGia()))
              .append(" | Giảm KM ").append(tien(c.getGiamKhuyenMai())).append(" | Điểm ").append(tien(c.getGiamDiem()))
              .append(" | Thực trả ").append(tien(c.getThucTra())).append('\n');
            if (!c.getSerialIMEI().isEmpty()) sb.append("Serial/IMEI: ").append(c.getSerialIMEI()).append('\n');
        }
        sb.append("Tổng tiền hàng: ").append(tien(h.tinhTongTienHang())).append('\n');
        sb.append("Khuyến mãi: -").append(tien(h.getGiamKhuyenMai())).append('\n');
        sb.append("Giảm bằng điểm: -").append(tien(h.getGiamDiem())).append('\n');
        sb.append("Giá chưa thuế: ").append(tien(h.getGiaChuaThue())).append(" | VAT đã gồm: ").append(tien(h.getTienVAT())).append('\n');
        sb.append("Thực thanh toán: ").append(tien(h.tinhTongTienThanhToan())).append('\n');
        sb.append("Phương thức: ").append(h.getPhuongThucThanhToan()).append('\n');
        sb.append("Tiền nhận: ").append(tien(h.getTienKhachDua())).append(" | Tiền thừa: ").append(tien(h.getTienThua())).append('\n');
        sb.append("Điểm dùng: ").append(h.getDiemDaDung()).append(" | Cộng: ").append(h.getDiemDaCong())
          .append(" | Số dư: ").append(h.getSoDuDiem()).append('\n');
        return sb.append("Cảm ơn quý khách!\n").toString();
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

    private HoaDon timHoaDon(String ma) {

        for (HoaDon h : danhSach()) {

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

    /** Chỉ chuyển dữ liệu sang các nhãn của thiết kế gốc, không tự tính lại giá. */
    private static final class HoaDon {
        final entity.HoaDonBanHang goc;
        final String ma, khach, sdt, thoiGian, nhanVien, phuongThuc;
        final int soLuong, diemCong, soDuDiem;
        final long giamGia, tienNhan;
        HoaDon(entity.HoaDonBanHang h) {
            goc = h; ma = h.getMaHD(); khach = h.getTenKH(); sdt = h.getSdtKH();
            thoiGian = h.getNgayLap().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
            nhanVien = h.getNhanVien(); phuongThuc = h.getPhuongThucThanhToan().toString();
            soLuong = h.getTongSoLuong(); diemCong = h.getDiemDaCong(); soDuDiem = h.getSoDuDiem();
            giamGia = h.getGiamKhuyenMai(); tienNhan = h.getTienKhachDua();
        }
        long tongTienHang() { return goc.tinhTongTienHang(); }
        long thucThanhToan() { return goc.tinhTongTienThanhToan(); }
    }
}
