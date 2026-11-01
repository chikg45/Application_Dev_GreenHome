package ui;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

/**
 * UI-10: Lich lam viec Green Home - Java Swing thuan.
 * Quan ly phan cong, nhan vien chi xem lich ca nhan.
 * Du lieu mau chi duoc luu trong bo nho.
 */
public class LichLamViecPanel extends JPanel {

    // ==================== MAU SAC ====================

    private static final Color BG = new Color(30, 33, 37);
    private static final Color CARD = new Color(18, 19, 21);
    private static final Color BORDER = new Color(58, 62, 69);
    private static final Color TEXT = new Color(226, 232, 242);
    private static final Color MUTED = new Color(145, 158, 177);
    private static final Color BLUE = new Color(54, 111, 232);

    // ==================== NGAY THANG ====================

    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                .withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter SHORT_DATE =
            DateTimeFormatter.ofPattern("dd/MM");

    // ==================== DU LIEU ====================

    private final boolean laQuanLy;
    private final String tenNguoiDung;

    private final List<NhanVien> nhanViens = new ArrayList<>();
    private final List<PhanCong> phanCongs = new ArrayList<>();

    private LocalDate dauTuan = LocalDate.now().with(
        TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
    );

    private int maTiepTheo = 1;

    // ==================== COMPONENTS ====================

    private final JPanel board =
        new JPanel(new GridBagLayout());

    private final JLabel lblTuan =
        label("", 12, Font.BOLD, TEXT);

    private final JLabel lblTong =
        label("", 11, Font.PLAIN, MUTED);

    private final JTextField txtNgay = input(10);

    private final JComboBox<String> cboNhanVien =
        new JComboBox<>();

    private final JComboBox<String> cboCa =
        new JComboBox<>(new String[] {
            "Tất cả ca",
            "Ca sáng",
            "Ca chiều",
            "Ca tối"
        });

    // ==================== CONSTRUCTOR ====================

    public LichLamViecPanel(
            boolean laQuanLy,
            String tenNguoiDung) {

        this.laQuanLy = laQuanLy;

        this.tenNguoiDung = tenNguoiDung == null
                ? ""
                : tenNguoiDung.trim();

        setBackground(BG);
        setLayout(new BorderLayout());

        taoGiaoDien();
        capNhatLich();
    }

    // Constructor phuc vu chay thu rieng
    public LichLamViecPanel(boolean laQuanLy) {

        this(
            laQuanLy,
            ""
        );
    }

    // ==================== DU LIEU MAU ====================





    // ==================== GIAO DIEN CHINH ====================

    private void taoGiaoDien() {

        JPanel page = new JPanel(
            new BorderLayout(0, 13)
        );

        page.setBackground(BG);

        page.setBorder(
            new EmptyBorder(22, 25, 18, 25)
        );

        add(page, BorderLayout.CENTER);

        JPanel top = new JPanel(
            new BorderLayout(0, 14)
        );

        top.setOpaque(false);

        // ==================== TIEU DE ====================

        JPanel heading = new JPanel(
            new BorderLayout(12, 0)
        );

        heading.setOpaque(false);

        JPanel title = vertical();

        title.add(label(
            laQuanLy
                ? "Phân công lịch làm việc"
                : "Lịch làm việc của tôi",
            21,
            Font.BOLD,
            TEXT
        ));

        title.add(Box.createVerticalStrut(6));

        title.add(label(
            laQuanLy
                ? "Chọn nhân viên, ngày và ca; theo dõi lịch trong tuần."
                : "Xem các ca đã được quản lý phân công theo tuần.",
            11,
            Font.PLAIN,
            MUTED
        ));

        heading.add(title, BorderLayout.WEST);

        // Chi quan ly moi duoc phan cong

        if (laQuanLy) {

            JButton btn = button(
                "+  Phân công",
                true
            );

            btn.addActionListener(e ->
                moForm(null, null, dauTuan)
            );

            heading.add(btn, BorderLayout.EAST);
        }

        top.add(heading, BorderLayout.NORTH);

        // ==================== THANH DIEU KHIEN ====================

        JPanel toolbar = new JPanel(
            new BorderLayout(12, 6)
        );

        toolbar.setBackground(CARD);

        toolbar.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(10, 12, 10, 12)
        ));

        JPanel nav = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 6, 0)
        );

        nav.setOpaque(false);

        JButton truoc = button("‹", false);
        JButton sau = button("›", false);
        JButton homNay = button("Tuần này", false);

        truoc.addActionListener(e -> doiTuan(-1));
        sau.addActionListener(e -> doiTuan(1));

        homNay.addActionListener(e -> {

            dauTuan = LocalDate.now().with(
                TemporalAdjusters.previousOrSame(
                    DayOfWeek.MONDAY
                )
            );

            capNhatLich();
        });

        nav.add(truoc);
        nav.add(lblTuan);
        nav.add(sau);
        nav.add(homNay);

        toolbar.add(nav, BorderLayout.WEST);

        // ==================== BO LOC ====================

        JPanel filters = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 6, 0)
        );

        filters.setOpaque(false);

        txtNgay.setText(
            DATE.format(LocalDate.now())
        );

        txtNgay.setToolTipText(
            "Nhập ngày dd/MM/yyyy và nhấn Đi tới"
        );

        JButton btnDiToi = button("Đi tới", false);

        btnDiToi.addActionListener(e -> {

            try {

                LocalDate ngay = LocalDate.parse(
                    txtNgay.getText().trim(),
                    DATE
                );

                dauTuan = ngay.with(
                    TemporalAdjusters.previousOrSame(
                        DayOfWeek.MONDAY
                    )
                );

                capNhatLich();

            } catch (DateTimeParseException ex) {

                baoLoi(
                    "Ngày không hợp lệ. Nhập theo dd/MM/yyyy."
                );
            }
        });

        filters.add(txtNgay);
        filters.add(btnDiToi);

        // Bo loc nhan vien chi danh cho quan ly

        if (laQuanLy) {

            cboNhanVien.addItem(
                "Tất cả nhân viên"
            );

            for (NhanVien nv : nhanViens) {
                cboNhanVien.addItem(nv.ten);
            }

            comboStyle(cboNhanVien);

            cboNhanVien.addActionListener(e ->
                capNhatLich()
            );

            filters.add(cboNhanVien);
        }

        comboStyle(cboCa);

        cboCa.addActionListener(e ->
            capNhatLich()
        );

        filters.add(cboCa);

        toolbar.add(filters, BorderLayout.EAST);

        top.add(toolbar, BorderLayout.SOUTH);

        page.add(top, BorderLayout.NORTH);

        // ==================== BANG LICH ====================

        JPanel main = new JPanel(
            new BorderLayout(0, 8)
        );

        main.setOpaque(false);

        JPanel caption = new JPanel(
            new BorderLayout()
        );

        caption.setOpaque(false);

        caption.add(
            lblTong,
            BorderLayout.WEST
        );

        caption.add(label(
            "Lịch phân công hiển thị theo tuần (dữ liệu mẫu)",
            10,
            Font.PLAIN,
            MUTED
        ), BorderLayout.EAST);

        main.add(caption, BorderLayout.NORTH);

        board.setBackground(CARD);

        JScrollPane scroll = new JScrollPane(board);

        scroll.getViewport().setBackground(CARD);

        scroll.setBorder(
            new LineBorder(BORDER)
        );

        scroll.getVerticalScrollBar()
              .setUnitIncrement(18);

        scroll.getHorizontalScrollBar()
              .setUnitIncrement(18);

        main.add(scroll, BorderLayout.CENTER);

        page.add(main, BorderLayout.CENTER);

        // ==================== CHU THICH CA ====================

        JPanel legend = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 17, 3)
        );

        legend.setOpaque(false);

        for (Ca ca : Ca.values()) {

            legend.add(label(
                "●  " + ca.ten + " " + ca.gio(),
                11,
                Font.PLAIN,
                ca.mauChu
            ));
        }

        page.add(legend, BorderLayout.SOUTH);
    }

    // ==================== CHUYEN TUAN ====================

    private void doiTuan(int soTuan) {

        dauTuan = dauTuan.plusWeeks(soTuan);

        capNhatLich();
    }

    // ==================== CAP NHAT BANG LICH ====================

    private void capNhatLich() {

        lblTuan.setText(
            SHORT_DATE.format(dauTuan)
            + " – "
            + SHORT_DATE.format(dauTuan.plusDays(6))
        );

        board.removeAll();

        List<NhanVien> hienThi = new ArrayList<>();

        // Loc nhan vien theo vai tro va bo loc

        for (NhanVien nv : nhanViens) {

            if (!laQuanLy
                    && !nv.ten.equalsIgnoreCase(tenNguoiDung)) {
                continue;
            }

            if (laQuanLy
                    && cboNhanVien.getSelectedIndex() > 0
                    && !nv.ten.equals(
                        cboNhanVien.getSelectedItem()
                    )) {
                continue;
            }

            hienThi.add(nv);
        }

        // ==================== TIEU DE BANG ====================

        addCell(
            label("Nhân viên", 11, Font.BOLD, TEXT),
            0, 0, 1, 155, 55, true
        );

        String[] days = {
            "Thứ hai",
            "Thứ ba",
            "Thứ tư",
            "Thứ năm",
            "Thứ sáu",
            "Thứ bảy",
            "Chủ nhật"
        };

        for (int d = 0; d < 7; d++) {

            LocalDate date = dauTuan.plusDays(d);

            JPanel head = vertical();

            head.add(label(
                days[d],
                11,
                Font.BOLD,
                MUTED
            ));

            head.add(Box.createVerticalStrut(5));

            head.add(label(
                SHORT_DATE.format(date),
                11,
                Font.BOLD,
                BLUE
            ));

            addCell(
                head,
                d + 1, 0, 1, 138, 55, true
            );
        }

        // ==================== CAC DONG NHAN VIEN ====================

        int dem = 0;

        for (int i = 0; i < hienThi.size(); i++) {

            NhanVien nv = hienThi.get(i);

            JPanel employee = vertical();

            employee.add(label(
                nv.ten,
                11,
                Font.BOLD,
                TEXT
            ));

            employee.add(Box.createVerticalStrut(5));

            employee.add(label(
                nv.ma,
                10,
                Font.PLAIN,
                MUTED
            ));

            addCell(
                employee,
                0, i + 1, 1, 155, 112, false
            );

            // 7 ngay trong tuan

            for (int d = 0; d < 7; d++) {

                LocalDate ngay = dauTuan.plusDays(d);

                JPanel cell = vertical();

                int caTrongO = 0;

                // Tim cac ca duoc phan cong

                for (PhanCong pc : phanCongs) {

                    if (!pc.nhanVien.ma.equals(nv.ma)
                            || !pc.ngay.equals(ngay)) {
                        continue;
                    }

                    if (!"Tất cả ca".equals(
                            cboCa.getSelectedItem())
                            && !pc.ca.ten.equals(
                                cboCa.getSelectedItem()
                            )) {
                        continue;
                    }

                    JButton card = shiftButton(pc);

                    card.setAlignmentX(
                        Component.LEFT_ALIGNMENT
                    );

                    card.setMaximumSize(
                        new Dimension(Integer.MAX_VALUE, 55)
                    );

                    cell.add(card);

                    cell.add(
                        Box.createVerticalStrut(5)
                    );

                    caTrongO++;
                    dem++;
                }

                // Quan ly duoc them ca

                if (laQuanLy) {

                    JButton plus = button("+", false);

                    plus.setAlignmentX(
                        Component.LEFT_ALIGNMENT
                    );

                    plus.setMaximumSize(
                        new Dimension(Integer.MAX_VALUE, 25)
                    );

                    plus.setToolTipText(
                        "Phân công " + nv.ten
                        + " ngày " + DATE.format(ngay)
                    );

                    plus.addActionListener(e ->
                        moForm(null, nv, ngay)
                    );

                    cell.add(plus);

                } else if (caTrongO == 0) {

                    cell.add(label(
                        "—",
                        12,
                        Font.PLAIN,
                        MUTED
                    ));
                }

                addCell(
                    cell,
                    d + 1, i + 1, 1, 138, 112, false
                );
            }
        }

        if (hienThi.isEmpty()) {

            JPanel empty = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
            );

            empty.setBackground(CARD);

            empty.add(label(
                "Không có nhân viên phù hợp trong dữ liệu mẫu.",
                12,
                Font.PLAIN,
                MUTED
            ));

            addCell(
                empty,
                0, 1, 8, 0, 95, false
            );
        }

        lblTong.setText(
            dem + " ca đã phân công"
            + (laQuanLy
                ? " · " + hienThi.size() + " nhân viên"
                : "")
        );

        board.revalidate();
        board.repaint();
    }

    // ==================== THE CA LAM ====================

    private JButton shiftButton(PhanCong pc) {

        JButton b = new JButton(
            "<html><b>" + pc.ca.ten + "</b><br>"
            + pc.ca.gio() + "</html>"
        );

        b.setHorizontalAlignment(
            SwingConstants.LEFT
        );

        b.setBackground(pc.ca.mauNen);
        b.setForeground(pc.ca.mauChu);

        b.setFont(
            new Font("SansSerif", Font.PLAIN, 10)
        );

        b.setFocusPainted(false);

        b.setBorder(
            new EmptyBorder(7, 8, 7, 5)
        );

        b.setToolTipText(
            laQuanLy
                ? "Bấm để sửa/xóa phân công"
                : pc.ghiChu
        );

        if (laQuanLy) {

            b.addActionListener(e ->
                moForm(pc, pc.nhanVien, pc.ngay)
            );
        }

        return b;
    }

    // ==================== TAO O TRONG BANG ====================

    private void addCell(
            Component comp,
            int x,
            int y,
            int span,
            int w,
            int h,
            boolean header) {

        JPanel wrapper = new JPanel(
            new BorderLayout()
        );

        wrapper.setBackground(
            header
                ? new Color(29, 32, 36)
                : CARD
        );

        wrapper.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 1, BORDER),
            new EmptyBorder(9, 9, 9, 9)
        ));

        wrapper.setPreferredSize(
            new Dimension(Math.max(1, w), h)
        );

        wrapper.add(comp, BorderLayout.CENTER);

        GridBagConstraints gbc =
            new GridBagConstraints();

        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = span;

        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        gbc.anchor =
            GridBagConstraints.NORTHWEST;

        board.add(wrapper, gbc);
    }

    // ==================== FORM PHAN CONG ====================

    private void moForm(
            PhanCong dangSua,
            NhanVien nvGoiY,
            LocalDate ngayGoiY) {

        if (!laQuanLy) {
            return;
        }

        Window owner =
            SwingUtilities.getWindowAncestor(this);

        JDialog dialog = new JDialog(
            owner,
            dangSua == null
                ? "Phân công ca làm việc"
                : "Cập nhật phân công",
            Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setSize(655, 570);

        dialog.setMinimumSize(
            new Dimension(560, 500)
        );

        dialog.setLocationRelativeTo(owner);

        JPanel root = new JPanel(
            new BorderLayout()
        );

        root.setBackground(CARD);

        // ==================== HEADER ====================

        JPanel top = new JPanel(
            new BorderLayout()
        );

        top.setOpaque(false);

        top.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(22, 25, 20, 25)
        ));

        JPanel heading = vertical();

        heading.add(label(
            dangSua == null
                ? "Phân công ca làm việc"
                : "Cập nhật ca làm việc",
            21,
            Font.BOLD,
            TEXT
        ));

        heading.add(Box.createVerticalStrut(7));

        heading.add(label(
            "Không phân ca trùng hoặc chồng lấn cho cùng một nhân viên.",
            12,
            Font.PLAIN,
            MUTED
        ));

        top.add(heading, BorderLayout.CENTER);

        JButton x = button("×", false);

        x.addActionListener(e ->
            dialog.dispose()
        );

        top.add(x, BorderLayout.EAST);

        root.add(top, BorderLayout.NORTH);

        // ==================== FORM NHAP ====================

        JPanel fields = vertical();

        fields.setBorder(
            new EmptyBorder(23, 25, 16, 25)
        );

        // -------- NHAN VIEN --------

        JComboBox<NhanVien> cboNhanVienForm =
            new JComboBox<>(
                nhanViens.toArray(new NhanVien[0])
            );

        cboNhanVienForm.setBackground(CARD);
        cboNhanVienForm.setForeground(TEXT);

        cboNhanVienForm.setPreferredSize(
            new Dimension(0, 37)
        );

        cboNhanVienForm.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 37)
        );

        if (nvGoiY != null) {

            cboNhanVienForm.setSelectedItem(nvGoiY);
        }

        addFormRow(
            fields,
            field("Nhân viên *", cboNhanVienForm),
            67
        );

        // -------- NGAY VA CA --------

        JTextField txtNgayForm = input(12);

        txtNgayForm.setText(
            DATE.format(ngayGoiY)
        );

        JComboBox<Ca> cboCaForm =
            new JComboBox<>(Ca.values());

        cboCaForm.setBackground(CARD);
        cboCaForm.setForeground(TEXT);

        if (dangSua != null) {

            cboCaForm.setSelectedItem(
                dangSua.ca
            );
        }

        JPanel pair = new JPanel(
            new GridLayout(1, 2, 15, 0)
        );

        pair.setOpaque(false);

        pair.add(field(
            "Ngày làm việc * (dd/MM/yyyy)",
            txtNgayForm
        ));

        pair.add(field(
            "Ca làm *",
            cboCaForm
        ));

        addFormRow(fields, pair, 71);

        // -------- GHI CHU --------

        JTextArea txtGhiChu = new JTextArea(
            dangSua == null
                ? ""
                : dangSua.ghiChu,
            4,
            20
        );

        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);

        txtGhiChu.setFont(
            new Font("SansSerif", Font.PLAIN, 12)
        );

        txtGhiChu.setBackground(
            new Color(23, 25, 29)
        );

        txtGhiChu.setForeground(TEXT);
        txtGhiChu.setCaretColor(TEXT);

        txtGhiChu.setBorder(
            new EmptyBorder(9, 10, 9, 10)
        );

        JScrollPane noteScroll =
            new JScrollPane(txtGhiChu);

        noteScroll.setBorder(
            new LineBorder(BORDER)
        );

        addFormRow(
            fields,
            field("Ghi chú", noteScroll),
            114
        );

        // -------- THONG BAO --------

        JPanel note = new JPanel(
            new BorderLayout()
        );

        note.setBackground(
            new Color(31, 37, 46)
        );

        note.setBorder(
            new EmptyBorder(13, 13, 13, 13)
        );

        note.add(label(
            "ⓘ Lịch là phân công dự kiến, không phải dữ liệu chấm công.",
            11,
            Font.PLAIN,
            MUTED
        ), BorderLayout.WEST);

        addFormRow(fields, note, 50);

        root.add(fields, BorderLayout.CENTER);

        // ==================== FOOTER ====================

        JPanel footer = new JPanel(
            new FlowLayout(
                FlowLayout.RIGHT, 10, 13
            )
        );

        footer.setBackground(
            new Color(30, 31, 34)
        );

        footer.setBorder(
            new MatteBorder(1, 0, 0, 0, BORDER)
        );

        // -------- XOA PHAN CONG --------

        if (dangSua != null) {

            JButton xoa = button(
                "Xóa phân công",
                false
            );

            xoa.addActionListener(e -> {

                int ok = JOptionPane.showConfirmDialog(
                    dialog,
                    "Xóa phân công này?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
                );

                if (ok == JOptionPane.YES_OPTION) {

                    phanCongs.remove(dangSua);

                    capNhatLich();

                    dialog.dispose();
                }
            });

            footer.add(xoa);
        }

        JButton huy = button("Quay lại", false);

        huy.addActionListener(e ->
            dialog.dispose()
        );

        JButton luu = button("Lưu thông tin", true);

        // ==================== SU KIEN LUU ====================

        luu.addActionListener(e -> {

            LocalDate ngay;

            try {

                ngay = LocalDate.parse(
                    txtNgayForm.getText().trim(),
                    DATE
                );

            } catch (DateTimeParseException ex) {

                baoLoi(
                    "Ngày không hợp lệ. Nhập theo dd/MM/yyyy."
                );

                return;
            }

            NhanVien nv = (NhanVien)
                cboNhanVienForm.getSelectedItem();

            Ca ca = (Ca)
                cboCaForm.getSelectedItem();

            if (nv == null || ca == null) {

                baoLoi(
                    "Hãy chọn nhân viên và ca làm việc."
                );

                return;
            }

            // ==================== KIEM TRA TRUNG CA ====================

            for (PhanCong pc : phanCongs) {

                // Khong so sanh voi chinh phan cong dang sua

                if (pc == dangSua
                        || !pc.nhanVien.ma.equals(nv.ma)
                        || !pc.ngay.equals(ngay)) {

                    continue;
                }

                // Hai khoang thoi gian giao nhau neu:
                // A.batDau < B.ketThuc
                // va B.batDau < A.ketThuc

                boolean overlap =
                    ca.batDau.isBefore(pc.ca.ketThuc)
                    && pc.ca.batDau.isBefore(ca.ketThuc);

                if (overlap) {

                    JOptionPane.showMessageDialog(
                        dialog,
                        nv.ten + " đã được phân "
                        + pc.ca.ten + " (" + pc.ca.gio()
                        + ") trong ngày "
                        + DATE.format(ngay) + ".",
                        "Ca bị trùng/chồng lấn",
                        JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }
            }

            // ==================== THEM HOAC SUA ====================

            if (dangSua == null) {

                phanCongs.add(new PhanCong(
                    maTiepTheo++,
                    nv,
                    ngay,
                    ca,
                    txtGhiChu.getText().trim()
                ));

            } else {

                dangSua.nhanVien = nv;
                dangSua.ngay = ngay;
                dangSua.ca = ca;

                dangSua.ghiChu =
                    txtGhiChu.getText().trim();
            }

            // Chuyen den tuan vua phan cong

            dauTuan = ngay.with(
                TemporalAdjusters.previousOrSame(
                    DayOfWeek.MONDAY
                )
            );

            capNhatLich();

            dialog.dispose();
        });

        footer.add(huy);
        footer.add(luu);

        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    // ==================== HAM HO TRO ====================

    private void baoLoi(String message) {

        JOptionPane.showMessageDialog(
            this,
            message,
            "Green Home",
            JOptionPane.WARNING_MESSAGE
        );
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
            String s,
            int size,
            int style,
            Color color) {

        JLabel l = new JLabel(s);

        l.setFont(
            new Font("SansSerif", style, size)
        );

        l.setForeground(color);

        l.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        return l;
    }

    private static JTextField input(int cols) {

        JTextField f = new JTextField(cols);

        f.setFont(
            new Font("SansSerif", Font.PLAIN, 12)
        );

        f.setBackground(
            new Color(23, 25, 29)
        );

        f.setForeground(TEXT);
        f.setCaretColor(TEXT);

        f.setBorder(new CompoundBorder(
            new LineBorder(BORDER),
            new EmptyBorder(8, 10, 8, 10)
        ));

        return f;
    }

    private static void comboStyle(
            JComboBox<String> c) {

        c.setBackground(CARD);
        c.setForeground(TEXT);

        c.setFont(
            new Font("SansSerif", Font.PLAIN, 11)
        );
    }

    private static JButton button(
            String s,
            boolean primary) {

        JButton b = new JButton(s);

        b.setFont(
            new Font("SansSerif", Font.BOLD, 11)
        );

        b.setForeground(TEXT);

        b.setBackground(
            primary
                ? BLUE
                : new Color(31, 34, 40)
        );

        b.setBorder(new CompoundBorder(
            new LineBorder(
                primary ? BLUE : BORDER
            ),
            new EmptyBorder(8, 11, 8, 11)
        ));

        b.setFocusPainted(false);

        b.setCursor(
            Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR
            )
        );

        return b;
    }

    private static JPanel field(
            String title,
            JComponent comp) {

        JPanel p = new JPanel(
            new BorderLayout(0, 7)
        );

        p.setOpaque(false);

        p.add(label(
            title,
            12,
            Font.BOLD,
            MUTED
        ), BorderLayout.NORTH);

        p.add(comp, BorderLayout.CENTER);

        return p;
    }

    private static void addFormRow(
            JPanel parent,
            JComponent row,
            int height) {

        row.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        row.setMaximumSize(
            new Dimension(
                Integer.MAX_VALUE,
                height
            )
        );

        row.setPreferredSize(
            new Dimension(450, height)
        );

        parent.add(row);

        parent.add(
            Box.createVerticalStrut(15)
        );
    }

    // ==================== MODEL NHAN VIEN MAU ====================

    private static class NhanVien {

        final String ma;
        final String ten;

        NhanVien(String ma, String ten) {

            this.ma = ma;
            this.ten = ten;
        }

        @Override
        public String toString() {

            return ten + " - " + ma;
        }
    }

    // ==================== ENUM CA LAM VIEC ====================

    private enum Ca {

        SANG(
            "Ca sáng",
            LocalTime.of(8, 0),
            LocalTime.of(14, 0),
            new Color(30, 46, 67),
            new Color(150, 187, 244)
        ),

        CHIEU(
            "Ca chiều",
            LocalTime.of(14, 0),
            LocalTime.of(18, 0),
            new Color(48, 37, 60),
            new Color(191, 157, 226)
        ),

        TOI(
            "Ca tối",
            LocalTime.of(18, 0),
            LocalTime.of(22, 0),
            new Color(56, 44, 27),
            new Color(232, 184, 102)
        );

        final String ten;

        final LocalTime batDau;
        final LocalTime ketThuc;

        final Color mauNen;
        final Color mauChu;

        Ca(
                String ten,
                LocalTime batDau,
                LocalTime ketThuc,
                Color mauNen,
                Color mauChu) {

            this.ten = ten;

            this.batDau = batDau;
            this.ketThuc = ketThuc;

            this.mauNen = mauNen;
            this.mauChu = mauChu;
        }

        String gio() {

            return batDau + "–" + ketThuc;
        }

        @Override
        public String toString() {

            return ten + " - " + gio();
        }
    }

    // ==================== MODEL PHAN CONG MAU ====================

    private static class PhanCong {

        final int ma;

        NhanVien nhanVien;
        LocalDate ngay;
        Ca ca;
        String ghiChu;

        PhanCong(
                int ma,
                NhanVien nhanVien,
                LocalDate ngay,
                Ca ca,
                String ghiChu) {

            this.ma = ma;

            this.nhanVien = nhanVien;
            this.ngay = ngay;

            this.ca = ca;
            this.ghiChu = ghiChu;
        }
    }

    // ==================== MAIN CHAY THU ====================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame f = new JFrame(
                "Green Home - Lịch làm việc (demo)"
            );

            f.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
            );

            f.setSize(1380, 770);
            f.setLocationRelativeTo(null);

            // true = Quản lý
            // false = Nhân viên bán hàng

            f.setContentPane(
                new LichLamViecPanel(true)
            );

            f.setVisible(true);
        });
    }
}