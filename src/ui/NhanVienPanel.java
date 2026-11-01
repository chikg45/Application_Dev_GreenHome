package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/** UI-11: Nhan vien & tai khoan. Du lieu mau, chua ket noi CSDL. */
public class NhanVienPanel extends JPanel {

    private static final Color BG = new Color(30, 33, 37);
    private static final Color CARD = new Color(18, 19, 21);
    private static final Color BORDER = new Color(57, 61, 67);
    private static final Color TEXT = new Color(225, 232, 242);
    private static final Color MUTED = new Color(143, 155, 173);
    private static final Color BLUE = new Color(54, 111, 232);
    private static final Color GREEN = new Color(60, 177, 124);
    private static final Color GOLD = new Color(208, 165, 72);
    private static final DateTimeFormatter DATE = DateTimeFormatter
            .ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final List<NhanVienMau> nhanViens = new ArrayList<>();
    private final List<NhanVienMau> dangHienThi = new ArrayList<>();
    private int maTiepTheo = 6;

    private final JTextField txtTim = new JTextField();
    private final JComboBox<String> cboVaiTro = new JComboBox<>(new String[] {
            "Tất cả vai trò", "Nhân viên bán hàng", "Quản lý"
    });
    private final JComboBox<String> cboTrangThai = new JComboBox<>(new String[] {
            "Mọi trạng thái", "Đang làm", "Đã nghỉ", "Tài khoản khóa"
    });
    private final JLabel lblDangLam = text("0", 23, Font.BOLD, BLUE);
    private final JLabel lblTaiKhoan = text("0", 23, Font.BOLD, GREEN);
    private final JLabel lblNgung = text("0", 23, Font.BOLD, GOLD);
    private final JLabel lblSoDong = text("0 nhân viên", 11, Font.PLAIN, MUTED);

    private final DefaultTableModel model = new DefaultTableModel(new String[] {
            "Nhân viên", "Liên hệ", "Vai trò", "Tài khoản", "Trạng thái", "Sửa", "Khóa / Mở"
    }, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);

    public NhanVienPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);
        taoGiaoDien();
        taiDanhSach();
    }



    private void taoGiaoDien() {
        JPanel page = new JPanel(new BorderLayout(0, 15));
        page.setBorder(new EmptyBorder(22, 26, 22, 26));
        page.setBackground(BG);
        add(page, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout(0, 15));
        top.setOpaque(false);
        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JPanel titles = vertical();
        titles.add(text("Nhân viên & tài khoản", 21, Font.BOLD, TEXT));
        titles.add(Box.createVerticalStrut(5));
        titles.add(text("Quản lý hồ sơ, phân quyền và trạng thái truy cập của nhân viên.", 11, Font.PLAIN, MUTED));
        heading.add(titles, BorderLayout.WEST);
        JButton btnThem = button("+  Thêm nhân viên", true);
        btnThem.addActionListener(e -> moForm(null));
        heading.add(btnThem, BorderLayout.EAST);
        top.add(heading, BorderLayout.NORTH);

        JPanel stats = new JPanel(new GridLayout(1, 3, 12, 0));
        stats.setOpaque(false);
        stats.setPreferredSize(new Dimension(0, 104));
        stats.add(stat("Đang làm việc", lblDangLam, "Nhân viên còn làm tại cửa hàng", BLUE));
        stats.add(stat("Tài khoản hoạt động", lblTaiKhoan, "Có quyền truy cập theo vai trò", GREEN));
        stats.add(stat("Đã nghỉ / khóa", lblNgung, "Nhân viên nghỉ hoặc tài khoản bị khóa", GOLD));
        top.add(stats, BorderLayout.CENTER);
        page.add(top, BorderLayout.NORTH);

        JPanel listCard = new JPanel(new BorderLayout());
        listCard.setBackground(CARD);
        listCard.setBorder(new LineBorder(BORDER));
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 10));
        filters.setBackground(CARD);
        styleInput(txtTim);
        txtTim.setPreferredSize(new Dimension(280, 36));
        txtTim.setToolTipText("Tìm họ tên, mã, SĐT hoặc tài khoản");
        styleCombo(cboVaiTro);
        styleCombo(cboTrangThai);
        filters.add(txtTim);
        filters.add(cboVaiTro);
        filters.add(cboTrangThai);
        listCard.add(filters, BorderLayout.NORTH);

        table.setRowHeight(58);
        table.setShowGrid(false);
        table.setFillsViewportHeight(true);
        table.setBackground(CARD);
        table.setForeground(TEXT);
        table.setSelectionBackground(new Color(38, 51, 74));
        table.setSelectionForeground(TEXT);
        table.setFont(new Font("SansSerif", Font.PLAIN, 11));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean selected, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, selected, focus, row, col);
                setBackground(new Color(28, 30, 33));
                setForeground(MUTED);
                setFont(new Font("SansSerif", Font.BOLD, 10));
                setBorder(new EmptyBorder(0, 12, 0, 5));
                return this;
            }
        });
        DefaultTableCellRenderer cell = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean selected, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, selected, focus, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 5));
                setHorizontalAlignment(col >= 5 ? SwingConstants.CENTER : SwingConstants.LEFT);
                if (col == 5 || col == 6) setForeground(new Color(157, 190, 250));
                else if (col == 4) setForeground("Đang làm".equals(v) ? GREEN : MUTED);
                else setForeground(TEXT);
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cell);
        }
        table.getColumnModel().getColumn(0).setPreferredWidth(220);
        table.getColumnModel().getColumn(1).setPreferredWidth(170);
        table.getColumnModel().getColumn(2).setPreferredWidth(145);
        table.getColumnModel().getColumn(3).setPreferredWidth(125);
        table.getColumnModel().getColumn(4).setPreferredWidth(105);
        table.getColumnModel().getColumn(5).setPreferredWidth(60);
        table.getColumnModel().getColumn(6).setPreferredWidth(95);

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || (col != 5 && col != 6)) return;
                NhanVienMau nv = dangHienThi.get(table.convertRowIndexToModel(row));
                if (col == 5) moForm(nv);
                else doiKhoaTaiKhoan(nv);
            }
        });
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD);
        listCard.add(scroll, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
        bottom.setBackground(CARD);
        bottom.add(lblSoDong);
        listCard.add(bottom, BorderLayout.SOUTH);
        page.add(listCard, BorderLayout.CENTER);

        txtTim.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { taiDanhSach(); }
            public void removeUpdate(DocumentEvent e) { taiDanhSach(); }
            public void changedUpdate(DocumentEvent e) { taiDanhSach(); }
        });
        cboVaiTro.addActionListener(e -> taiDanhSach());
        cboTrangThai.addActionListener(e -> taiDanhSach());
    }

    private void taiDanhSach() {
        String q = txtTim.getText().trim().toLowerCase(Locale.ROOT);
        String vaiTro = String.valueOf(cboVaiTro.getSelectedItem());
        String trangThai = String.valueOf(cboTrangThai.getSelectedItem());
        dangHienThi.clear();
        model.setRowCount(0);
        int lamViec = 0, taiKhoan = 0, ngung = 0;
        for (NhanVienMau nv : nhanViens) {
            if (nv.dangLamViec) lamViec++;
            if (nv.dangLamViec && nv.choPhepTruyCap) taiKhoan++;
            if (!nv.dangLamViec || !nv.choPhepTruyCap) ngung++;

            String searchable = (nv.ma + " " + nv.hoTen + " " + nv.sdt + " " + nv.tenDangNhap)
                    .toLowerCase(Locale.ROOT);
            boolean matchVaiTro = vaiTro.startsWith("Tất cả") || vaiTro.equals(nv.vaiTro);
            boolean matchTrangThai = "Mọi trạng thái".equals(trangThai)
                    || ("Đang làm".equals(trangThai) && nv.dangLamViec)
                    || ("Đã nghỉ".equals(trangThai) && !nv.dangLamViec)
                    || ("Tài khoản khóa".equals(trangThai) && !nv.choPhepTruyCap);
            if (!searchable.contains(q) || !matchVaiTro || !matchTrangThai) continue;
            dangHienThi.add(nv);
            model.addRow(new Object[] {
                    "<html><b>" + escape(nv.hoTen) + "</b><br><font color='#8F9BAD'>" + nv.ma + "</font></html>",
                    "<html>" + nv.sdt + "<br><font color='#8F9BAD'>CCCD: ****" + nv.cccd.substring(8) + "</font></html>",
                    nv.vaiTro,
                    "<html>" + escape(nv.tenDangNhap) + "<br><font color='#8F9BAD'>"
                            + (nv.choPhepTruyCap ? "Đang mở" : "Đã khóa") + "</font></html>",
                    nv.dangLamViec ? "Đang làm" : "Đã nghỉ",
                    "Sửa",
                    nv.dangLamViec ? (nv.choPhepTruyCap ? "Khóa" : "Mở khóa") : "—"
            });
        }
        lblDangLam.setText(String.valueOf(lamViec));
        lblTaiKhoan.setText(String.valueOf(taiKhoan));
        lblNgung.setText(String.valueOf(ngung));
        lblSoDong.setText(dangHienThi.size() + " nhân viên");
    }

    private void doiKhoaTaiKhoan(NhanVienMau nv) {
        if (!nv.dangLamViec) {
            JOptionPane.showMessageDialog(this, "Nhân viên đã nghỉ. Cập nhật trạng thái làm việc trước.");
            return;
        }
        String action = nv.choPhepTruyCap ? "Khóa" : "Mở khóa";
        int confirm = JOptionPane.showConfirmDialog(this,
                action + " tài khoản " + nv.tenDangNhap + "?\nHồ sơ và lịch sử giao dịch vẫn được giữ nguyên.",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        nv.choPhepTruyCap = !nv.choPhepTruyCap;
        taiDanhSach();
    }

    // Dùng một form cho cả thêm và sửa.
    private void moForm(NhanVienMau nvCu) {
        boolean sua = nvCu != null;
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, sua ? "Cập nhật nhân viên" : "Thêm nhân viên",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(770, 690);
        dialog.setMinimumSize(new Dimension(640, 570));
        dialog.setLocationRelativeTo(owner);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CARD);
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD);
        header.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(20, 26, 20, 26)));
        JPanel title = vertical();
        title.add(text(sua ? "Cập nhật nhân viên" : "Thêm nhân viên", 22, Font.BOLD, TEXT));
        title.add(Box.createVerticalStrut(7));
        title.add(text(sua ? "Mã hồ sơ: " + nvCu.ma : "Tạo hồ sơ và tài khoản mẫu theo vai trò.",
                12, Font.PLAIN, MUTED));
        header.add(title, BorderLayout.WEST);
        JButton x = button("×", false);
        x.addActionListener(e -> dialog.dispose());
        header.add(x, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JTextField ten = input(sua ? nvCu.hoTen : "");
        JTextField sdt = input(sua ? nvCu.sdt : "");
        JTextField cccd = input(sua ? nvCu.cccd : "");
        JTextField ngaySinh = input(sua ? DATE.format(nvCu.ngaySinh) : "");
        JTextField ngayVao = input(sua ? DATE.format(nvCu.ngayVaoLam) : DATE.format(LocalDate.now()));
        JComboBox<String> vaiTro = new JComboBox<>(new String[] {"Nhân viên bán hàng", "Quản lý"});
        styleCombo(vaiTro);
        if (sua) vaiTro.setSelectedItem(nvCu.vaiTro);
        JTextField username = input(sua ? nvCu.tenDangNhap : "");
        JTextField diaChi = input(sua ? nvCu.diaChi : "");
        JCheckBox dangLam = checkbox("Nhân viên đang làm việc", !sua || nvCu.dangLamViec);
        JCheckBox truyCap = checkbox("Cho phép truy cập tài khoản", !sua || nvCu.choPhepTruyCap);
        truyCap.setEnabled(dangLam.isSelected());
        dangLam.addActionListener(e -> {
            if (!dangLam.isSelected()) truyCap.setSelected(false);
            truyCap.setEnabled(dangLam.isSelected());
        });

        JPanel fields = new JPanel(new GridLayout(4, 2, 17, 15));
        fields.setOpaque(false);
        fields.add(field("Họ tên *", ten));
        fields.add(field("Số điện thoại *", sdt));
        fields.add(field("CCCD *", cccd));
        fields.add(field("Ngày sinh * (dd/MM/yyyy)", ngaySinh));
        fields.add(field("Ngày vào làm * (dd/MM/yyyy)", ngayVao));
        fields.add(field("Vai trò *", vaiTro));
        fields.add(field("Tên đăng nhập mẫu *", username));
        fields.add(field("Địa chỉ", diaChi));
        JPanel checks = new JPanel(new GridLayout(1, 2, 15, 0));
        checks.setOpaque(false);
        checks.add(dangLam);
        checks.add(truyCap);
        JPanel notice = new JPanel(new BorderLayout());
        notice.setBackground(new Color(31, 37, 46));
        notice.setBorder(new EmptyBorder(13, 14, 13, 14));
        notice.add(text("ⓘ Tài khoản chỉ là dữ liệu mẫu: chưa tạo mật khẩu hay quyền đăng nhập thật.",
                11, Font.PLAIN, MUTED), BorderLayout.WEST);
        JPanel content = new JPanel(new BorderLayout(0, 17));
        content.setBackground(CARD);
        content.setBorder(new EmptyBorder(25, 26, 25, 26));
        content.add(fields, BorderLayout.NORTH);
        JPanel lower = vertical();
        checks.setAlignmentX(Component.LEFT_ALIGNMENT);
        checks.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        notice.setAlignmentX(Component.LEFT_ALIGNMENT);
        notice.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        lower.add(checks);
        lower.add(Box.createVerticalStrut(15));
        lower.add(notice);
        content.add(lower, BorderLayout.CENTER);
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        root.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 13));
        footer.setBackground(new Color(30, 31, 34));
        footer.setBorder(new MatteBorder(1, 0, 0, 0, BORDER));
        JButton huy = button("Quay lại", false);
        JButton luu = button("Lưu thông tin", true);
        huy.addActionListener(e -> dialog.dispose());
        luu.addActionListener(e -> {
            String hoTen = ten.getText().trim();
            String phone = sdt.getText().trim();
            String id = cccd.getText().trim();
            String user = username.getText().trim().toLowerCase(Locale.ROOT);
            if (hoTen.isEmpty() || !phone.matches("0\\d{9}") || !id.matches("\\d{12}")) {
                warning(dialog, "Nhập họ tên, SĐT 10 chữ số và CCCD 12 chữ số hợp lệ.");
                return;
            }
            if (!user.matches("[a-z0-9._]{4,30}")) {
                warning(dialog, "Tên đăng nhập cần 4–30 ký tự: chữ thường, số, dấu chấm hoặc gạch dưới.");
                return;
            }
            LocalDate birth, joined;
            try {
                birth = LocalDate.parse(ngaySinh.getText().trim(), DATE);
                joined = LocalDate.parse(ngayVao.getText().trim(), DATE);
            } catch (DateTimeParseException ex) {
                warning(dialog, "Ngày phải đúng định dạng dd/MM/yyyy.");
                return;
            }
            if (!birth.isBefore(joined) || birth.isAfter(LocalDate.now())) {
                warning(dialog, "Ngày sinh phải trước ngày vào làm và không ở tương lai.");
                return;
            }
            for (NhanVienMau item : nhanViens) {
                if (item == nvCu) continue;
                if (item.sdt.equals(phone) || item.cccd.equals(id) || item.tenDangNhap.equals(user)) {
                    warning(dialog, "Trùng số điện thoại, CCCD hoặc tên đăng nhập.");
                    return;
                }
            }
            NhanVienMau nv = nvCu;
            if (nv == null) {
                nv = new NhanVienMau(String.format("NV%04d", maTiepTheo++), "", "", "",
                        birth, joined, "Nhân viên bán hàng", "", "", true, true);
                nhanViens.add(nv);
            }
            nv.hoTen = hoTen;
            nv.sdt = phone;
            nv.cccd = id;
            nv.ngaySinh = birth;
            nv.ngayVaoLam = joined;
            nv.vaiTro = String.valueOf(vaiTro.getSelectedItem());
            nv.tenDangNhap = user;
            nv.diaChi = diaChi.getText().trim();
            nv.dangLamViec = dangLam.isSelected();
            nv.choPhepTruyCap = nv.dangLamViec && truyCap.isSelected();
            taiDanhSach();
            dialog.dispose();
        });
        footer.add(huy);
        footer.add(luu);
        root.add(footer, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private static JPanel stat(String title, JLabel value, String desc, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(new LineBorder(BORDER), new EmptyBorder(14, 14, 14, 14)));
        JPanel lines = vertical();
        lines.add(text(title, 11, Font.PLAIN, MUTED));
        lines.add(Box.createVerticalStrut(8));
        lines.add(value);
        lines.add(Box.createVerticalStrut(6));
        lines.add(text(desc, 10, Font.PLAIN, MUTED));
        card.add(lines, BorderLayout.CENTER);
        card.add(text("●", 15, Font.BOLD, accent), BorderLayout.EAST);
        return card;
    }

    private static JPanel vertical() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private static JLabel text(String value, int size, int style, Color color) {
        JLabel l = new JLabel(value);
        l.setFont(new Font("SansSerif", style, size));
        l.setForeground(color);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private static JButton button(String title, boolean primary) {
        JButton b = new JButton(title);
        b.setFont(new Font("SansSerif", Font.BOLD, 11));
        b.setBackground(primary ? BLUE : new Color(30, 33, 38));
        b.setForeground(TEXT);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new CompoundBorder(new LineBorder(primary ? BLUE : BORDER),
                new EmptyBorder(9, 12, 9, 12)));
        return b;
    }

    private static JTextField input(String value) {
        JTextField f = new JTextField(value);
        styleInput(f);
        return f;
    }

    private static void styleInput(JTextField f) {
        f.setBackground(new Color(23, 25, 29));
        f.setForeground(TEXT);
        f.setCaretColor(TEXT);
        f.setFont(new Font("SansSerif", Font.PLAIN, 12));
        f.setBorder(new CompoundBorder(new LineBorder(BORDER), new EmptyBorder(8, 11, 8, 11)));
        f.setPreferredSize(new Dimension(160, 36));
    }

    private static void styleCombo(JComboBox<String> c) {
        c.setBackground(new Color(23, 25, 29));
        c.setForeground(TEXT);
        c.setFont(new Font("SansSerif", Font.PLAIN, 11));
        c.setPreferredSize(new Dimension(155, 36));
    }

    private static JPanel field(String caption, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        p.add(text(caption, 12, Font.BOLD, MUTED), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private static JCheckBox checkbox(String caption, boolean selected) {
        JCheckBox c = new JCheckBox(caption, selected);
        c.setOpaque(false);
        c.setForeground(MUTED);
        c.setFont(new Font("SansSerif", Font.PLAIN, 11));
        return c;
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static void warning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Kiểm tra dữ liệu", JOptionPane.WARNING_MESSAGE);
    }

    private static class NhanVienMau {
        final String ma;
        String hoTen, sdt, cccd, vaiTro, tenDangNhap, diaChi;
        LocalDate ngaySinh, ngayVaoLam;
        boolean dangLamViec, choPhepTruyCap;

        NhanVienMau(String ma, String hoTen, String sdt, String cccd,
                LocalDate ngaySinh, LocalDate ngayVaoLam, String vaiTro, String tenDangNhap,
                String diaChi, boolean dangLamViec, boolean choPhepTruyCap) {
            this.ma = ma;
            this.hoTen = hoTen;
            this.sdt = sdt;
            this.cccd = cccd;
            this.ngaySinh = ngaySinh;
            this.ngayVaoLam = ngayVaoLam;
            this.vaiTro = vaiTro;
            this.tenDangNhap = tenDangNhap;
            this.diaChi = diaChi;
            this.dangLamViec = dangLamViec;
            this.choPhepTruyCap = choPhepTruyCap;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Green Home - Nhân viên & tài khoản (demo)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 760);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new NhanVienPanel());
            frame.setVisible(true);
        });
    }
}
