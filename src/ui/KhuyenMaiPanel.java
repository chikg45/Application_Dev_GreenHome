package ui;

import dao.KhuyenMaiDAO;
import entity.KhuyenMai;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;



import java.awt.*;

import javax.swing.*;

import javax.swing.border.EmptyBorder;

import javax.swing.table.DefaultTableCellRenderer;

import javax.swing.table.DefaultTableModel;



/**

 * Giao diện quản lý khuyến mãi Green Home.

 * Chỉ thiết kế giao diện, chưa có xử lý nghiệp vụ và dữ liệu.

 */

public class KhuyenMaiPanel extends JPanel {



    private static final Color BG = new Color(30, 33, 37);

    private static final Color CARD = new Color(18, 19, 21);

    private static final Color BORDER = new Color(57, 61, 67);

    private static final Color TEXT = new Color(225, 232, 242);

    private static final Color MUTED = new Color(143, 155, 173);

    private static final Color BLUE = new Color(54, 111, 232);

    private static final Color GREEN = new Color(48, 174, 121);

    private static final Color RED = new Color(183, 88, 91);



    private final boolean laQuanLy;
    private final KhuyenMaiDAO dao = new KhuyenMaiDAO();
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");



    // Các thành phần giao diện

    private final DefaultTableModel model = new DefaultTableModel(

            new String[]{

                "Mã KM",

                "Tên chương trình",

                "Loại",

                "Giá trị",

                "Bắt đầu",

                "Kết thúc",

                "Trạng thái"

            }, 0) {

        @Override

        public boolean isCellEditable(int row, int column) {

            return false;

        }

    };



    private final JTable table = new JTable(model);



    private final JTextField txtSearch = new JTextField();



    private final JComboBox<String> cmbStatus = new JComboBox<>(

            new String[]{

                "Tất cả trạng thái",

                "Đang diễn ra",

                "Sắp diễn ra",

                "Đã kết thúc"

            }

    );



    private final JLabel lblTotal = new JLabel("0");

    private final JLabel lblActive = new JLabel("0");

    private final JLabel lblUpcoming = new JLabel("0");

    private final JLabel lblExpired = new JLabel("0");



    // Constructor

    public KhuyenMaiPanel(boolean laQuanLy) {

        this.laQuanLy = laQuanLy;



        setLayout(new BorderLayout());

        setBackground(BG);



        initUI();
        bindEvents();
        loadData();

    }



    /**

     * Khởi tạo giao diện quản lý khuyến mãi.

     */

    private void initUI() {



        JPanel content = new JPanel();

        content.setOpaque(false);

        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.setBorder(new EmptyBorder(24, 28, 25, 28));



        // ================= HEADER =================



        JPanel heading = new JPanel(new BorderLayout(12, 0));

        heading.setOpaque(false);



        JPanel headingText = vertical();



        headingText.add(label(

                "Quản lý khuyến mãi",

                22, Font.BOLD, TEXT

        ));



        headingText.add(Box.createVerticalStrut(6));



        headingText.add(label(

                "Quản lý chương trình ưu đãi và thời gian áp dụng.",

                12, Font.PLAIN, MUTED

        ));



        heading.add(headingText, BorderLayout.WEST);



        if (laQuanLy) {

            JButton add = button("+  Thêm khuyến mãi", BLUE);



            add.addActionListener(e -> openEditor(null));



            heading.add(add, BorderLayout.EAST);

        }



        fullWidth(heading);

        content.add(heading);



        content.add(Box.createVerticalStrut(20));



        // ================= THỐNG KÊ =================



        JPanel counters = new JPanel(new GridLayout(1, 4, 12, 0));

        counters.setOpaque(false);



        counters.add(counter(

                "Tổng chương trình",

                lblTotal,

                BLUE

        ));



        counters.add(counter(

                "Đang diễn ra",

                lblActive,

                GREEN

        ));



        counters.add(counter(

                "Sắp diễn ra",

                lblUpcoming,

                new Color(204, 157, 65)

        ));



        counters.add(counter(

                "Đã kết thúc",

                lblExpired,

                RED

        ));



        counters.setMaximumSize(

                new Dimension(Integer.MAX_VALUE, 104)

        );



        content.add(counters);

        content.add(Box.createVerticalStrut(20));



        // ================= KHUNG DANH SÁCH =================



        JPanel box = new JPanel(new BorderLayout(0, 16));



        box.setBackground(CARD);



        box.setBorder(BorderFactory.createCompoundBorder(

                BorderFactory.createLineBorder(BORDER),

                new EmptyBorder(18, 18, 18, 18)

        ));



        // ================= THANH CÔNG CỤ =================



        JPanel tools = new JPanel(new BorderLayout(12, 0));

        tools.setOpaque(false);



        JPanel title = vertical();



        title.add(label(

                "Danh sách khuyến mãi",

                15, Font.BOLD, TEXT

        ));



        title.add(Box.createVerticalStrut(5));



        title.add(label(

                "Tìm kiếm, lọc và xem chi tiết chương trình",

                11, Font.PLAIN, MUTED

        ));



        tools.add(title, BorderLayout.WEST);



        JPanel filters = new JPanel(

                new FlowLayout(FlowLayout.RIGHT, 10, 0)

        );



        filters.setOpaque(false);



        // Ô tìm kiếm

        styleInput(txtSearch);



        txtSearch.setPreferredSize(

                new Dimension(210, 34)

        );



        txtSearch.setToolTipText(

                "Tìm theo mã hoặc tên khuyến mãi"

        );



        txtSearch.putClientProperty(

                "JTextField.placeholderText",

                "Tìm mã / tên..."

        );



        // Bộ lọc trạng thái

        styleCombo(cmbStatus);



        cmbStatus.setPreferredSize(

                new Dimension(155, 34)

        );



        filters.add(txtSearch);

        filters.add(cmbStatus);



        tools.add(filters, BorderLayout.EAST);



        box.add(tools, BorderLayout.NORTH);



        // ================= BẢNG KHUYẾN MÃI =================



        table.setRowHeight(45);



        table.setFont(

                new Font("SansSerif", Font.PLAIN, 12)

        );



        table.setBackground(CARD);

        table.setForeground(TEXT);

        table.setGridColor(BORDER);



        table.setShowVerticalLines(false);



        table.setSelectionBackground(

                new Color(41, 58, 85)

        );



        table.setSelectionForeground(Color.WHITE);



        table.setSelectionMode(

                ListSelectionModel.SINGLE_SELECTION

        );



        table.setFillsViewportHeight(true);



        table.getTableHeader().setReorderingAllowed(false);



        table.getTableHeader().setPreferredSize(

                new Dimension(0, 40)

        );



        table.getTableHeader().setDefaultRenderer(

                new CellRenderer(true)

        );



        for (int i = 0; i < table.getColumnCount(); i++) {

            table.getColumnModel()

                    .getColumn(i)

                    .setCellRenderer(new CellRenderer(false));

        }



        // Độ rộng các cột

        table.getColumnModel().getColumn(0)

                .setPreferredWidth(75);



        table.getColumnModel().getColumn(1)

                .setPreferredWidth(220);



        table.getColumnModel().getColumn(2)

                .setPreferredWidth(95);



        table.getColumnModel().getColumn(3)

                .setPreferredWidth(100);



        table.getColumnModel().getColumn(6)

                .setPreferredWidth(120);



        JScrollPane scroll = new JScrollPane(table);



        scroll.setBorder(

                BorderFactory.createLineBorder(BORDER)

        );



        scroll.getViewport().setBackground(CARD);



        scroll.setPreferredSize(

                new Dimension(750, 370)

        );



        box.add(scroll, BorderLayout.CENTER);



        // ================= CÁC NÚT CHỨC NĂNG =================



        JPanel actions = new JPanel(new BorderLayout());

        actions.setOpaque(false);



        actions.add(

                label(

                        "Danh sách khuyến mãi",

                        10,

                        Font.PLAIN,

                        MUTED

                ),

                BorderLayout.WEST

        );



        JPanel right = new JPanel(

                new FlowLayout(FlowLayout.RIGHT, 8, 0)

        );



        right.setOpaque(false);



        // Nút xem chi tiết

        JButton detail = button(

                "Xem chi tiết",

                new Color(54, 63, 78)

        );



        detail.addActionListener(e -> viewSelected());
        right.add(detail);



        if (laQuanLy) {



            // Nút sửa

            JButton edit = button("Sửa", BLUE);



            // Nút xóa

            JButton delete = button("Xóa", RED);



            edit.addActionListener(e -> editSelected());
            delete.addActionListener(e -> deleteSelected());
            right.add(edit);

            right.add(delete);

        }



        actions.add(right, BorderLayout.EAST);



        box.add(actions, BorderLayout.SOUTH);



        fullWidth(box);

        content.add(box);



        // ================= SCROLL TRANG =================



        JScrollPane pageScroll = new JScrollPane(content);



        pageScroll.setBorder(null);



        pageScroll.getViewport().setBackground(BG);



        pageScroll.getVerticalScrollBar()

                .setUnitIncrement(18);



        add(pageScroll, BorderLayout.CENTER);

    }



    /**

     * Tạo ô thống kê.

     */

    private void bindEvents() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadData(); }
            public void removeUpdate(DocumentEvent e) { loadData(); }
            public void changedUpdate(DocumentEvent e) { loadData(); }
        });
        cmbStatus.addActionListener(e -> loadData());
    }

    private void loadData() {
        try {
            List<KhuyenMai> items = dao.search(txtSearch.getText());
            String selectedStatus = (String) cmbStatus.getSelectedItem();
            model.setRowCount(0);
            for (KhuyenMai km : items) {
                if (!"Tất cả trạng thái".equals(selectedStatus)
                        && !km.getTrangThai().equals(selectedStatus)) continue;
                String loai = "PHAN_TRAM".equals(km.getLoaiGiam()) ? "Phần trăm" : "Số tiền";
                String giaTri = "PHAN_TRAM".equals(km.getLoaiGiam())
                        ? String.format(java.util.Locale.ROOT, "%.2f", km.getPhanTramGiam()) + "%"
                        : String.format(java.util.Locale.ROOT, "%.2f", km.getSoTienGiam()) + " đ";
                model.addRow(new Object[]{km.getMaKM(), km.getTenKM(), loai, giaTri,
                        km.getNgayBatDau().format(dateFormat), km.getNgayKetThuc().format(dateFormat),
                        km.getTrangThai()});
            }
            lblTotal.setText(String.valueOf(dao.countAll()));
            lblActive.setText(String.valueOf(dao.countByStatus("Đang diễn ra")));
            lblUpcoming.setText(String.valueOf(dao.countByStatus("Sắp diễn ra")));
            lblExpired.setText(String.valueOf(dao.countByStatus("Đã kết thúc")));
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private KhuyenMai selected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khuyến mãi.");
            return null;
        }
        String id = model.getValueAt(table.convertRowIndexToModel(row), 0).toString();
        try {
            return dao.findById(id);
        } catch (SQLException ex) {
            showError(ex);
            return null;
        }
    }

    private void viewSelected() {
        KhuyenMai km = selected();
        if (km == null) return;
        JOptionPane.showMessageDialog(this,
                "Mã: " + km.getMaKM() + "\nTên: " + km.getTenKM()
                + "\nLoại: " + km.getLoaiGiam()
                + "\nPhần trăm: " + km.getPhanTramGiam() + "%"
                + "\nSố tiền: " + km.getSoTienGiam()
                + "\nBắt đầu: " + km.getNgayBatDau().format(dateFormat)
                + "\nKết thúc: " + km.getNgayKetThuc().format(dateFormat)
                + "\nTrạng thái: " + km.getTrangThai()
                + "\nMô tả: " + (km.getMoTaKM() == null ? "" : km.getMoTaKM()),
                "Chi tiết khuyến mãi", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editSelected() {
        KhuyenMai km = selected();
        if (km != null) openEditor(km);
    }

    private void deleteSelected() {
        KhuyenMai km = selected();
        if (km == null) return;
        if (JOptionPane.showConfirmDialog(this, "Xóa khuyến mãi " + km.getMaKM() + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            if (dao.delete(km.getMaKM())) loadData();
            else JOptionPane.showMessageDialog(this, "Không tìm thấy khuyến mãi.");
        } catch (SQLException ex) { showError(ex); }
    }

    private void openEditor(KhuyenMai old) {
        boolean editing = old != null;
        JTextField ma = new JTextField(editing ? old.getMaKM() : "");
        ma.setEditable(!editing);
        JTextField ten = new JTextField(editing ? old.getTenKM() : "");
        JComboBox<String> loai = new JComboBox<>(new String[]{"PHAN_TRAM", "SO_TIEN"});
        if (editing) loai.setSelectedItem(old.getLoaiGiam());
        JTextField giaTri = new JTextField(editing
                ? String.valueOf("PHAN_TRAM".equals(old.getLoaiGiam()) ? old.getPhanTramGiam() : old.getSoTienGiam()) : "0");
        JTextField batDau = new JTextField(editing ? old.getNgayBatDau().format(dateFormat) : LocalDateTime.now().format(dateFormat));
        JTextField ketThuc = new JTextField(editing ? old.getNgayKetThuc().format(dateFormat) : LocalDateTime.now().plusDays(7).format(dateFormat));
        JTextArea moTa = new JTextArea(editing && old.getMoTaKM() != null ? old.getMoTaKM() : "", 3, 20);
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Mã KM:")); form.add(ma);
        form.add(new JLabel("Tên chương trình:")); form.add(ten);
        form.add(new JLabel("Loại giảm:")); form.add(loai);
        form.add(new JLabel("Giá trị giảm:")); form.add(giaTri);
        form.add(new JLabel("Bắt đầu (dd/MM/yyyy HH:mm):")); form.add(batDau);
        form.add(new JLabel("Kết thúc (dd/MM/yyyy HH:mm):")); form.add(ketThuc);
        form.add(new JLabel("Mô tả:")); form.add(new JScrollPane(moTa));
        while (JOptionPane.showConfirmDialog(this, form,
                editing ? "Sửa khuyến mãi" : "Thêm khuyến mãi",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                String type = (String) loai.getSelectedItem();
                double value = Double.parseDouble(giaTri.getText().trim());
                KhuyenMai km = new KhuyenMai(ma.getText().trim(), ten.getText().trim(),
                        "PHAN_TRAM".equals(type) ? value : 0.0,
                        LocalDateTime.parse(batDau.getText().trim(), dateFormat),
                        LocalDateTime.parse(ketThuc.getText().trim(), dateFormat),
                        moTa.getText(), type,
                        "SO_TIEN".equals(type) ? value : 0.0);
                boolean success = editing ? dao.update(km) : dao.insert(km);
                if (!success) JOptionPane.showMessageDialog(this, "Không tìm thấy bản ghi để cập nhật.");
                else { loadData(); JOptionPane.showMessageDialog(this, "Lưu thành công."); }
                return;
            } catch (SQLException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Không thể lưu", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showError(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Lỗi cơ sở dữ liệu: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private JPanel counter(

            String title,

            JLabel value,

            Color accent

    ) {



        JPanel p = vertical();



        p.setBackground(CARD);

        p.setOpaque(true);



        p.setBorder(BorderFactory.createCompoundBorder(

                BorderFactory.createLineBorder(BORDER),

                new EmptyBorder(13, 15, 12, 15)

        ));



        p.add(label(

                title,

                11,

                Font.PLAIN,

                MUTED

        ));



        p.add(Box.createVerticalStrut(10));



        value.setForeground(accent);



        value.setFont(

                new Font("SansSerif", Font.BOLD, 25)

        );



        p.add(value);



        return p;

    }



    // ================= HÀM HỖ TRỢ GIAO DIỆN =================



    private static JPanel vertical() {



        JPanel p = new JPanel();



        p.setOpaque(false);



        p.setLayout(

                new BoxLayout(p, BoxLayout.Y_AXIS)

        );



        return p;

    }



    private static void fullWidth(JComponent component) {



        component.setAlignmentX(

                Component.LEFT_ALIGNMENT

        );



        component.setMaximumSize(

                new Dimension(

                        Integer.MAX_VALUE,

                        component.getPreferredSize().height

                )

        );

    }



    private static JLabel label(

            String text,

            int size,

            int style,

            Color color

    ) {



        JLabel l = new JLabel(text);



        l.setForeground(color);



        l.setFont(

                new Font("SansSerif", style, size)

        );



        return l;

    }



    private static JButton button(

            String text,

            Color bg

    ) {



        JButton b = new JButton(text);



        b.setFont(

                new Font("SansSerif", Font.BOLD, 12)

        );



        b.setBackground(bg);

        b.setForeground(Color.WHITE);



        b.setFocusPainted(false);

        b.setOpaque(true);



        b.setBorder(

                new EmptyBorder(10, 16, 10, 16)

        );



        return b;

    }



    private static void styleInput(JTextField field) {



        field.setBackground(

                new Color(35, 38, 43)

        );



        field.setForeground(TEXT);

        field.setCaretColor(TEXT);



        field.setFont(

                new Font("SansSerif", Font.PLAIN, 12)

        );



        field.setBorder(BorderFactory.createCompoundBorder(

                BorderFactory.createLineBorder(BORDER),

                new EmptyBorder(8, 10, 8, 10)

        ));

    }



    private static void styleCombo(

            JComboBox<String> combo

    ) {



        combo.setBackground(

                new Color(35, 38, 43)

        );



        combo.setForeground(TEXT);



        combo.setFont(

                new Font("SansSerif", Font.PLAIN, 12)

        );

    }



    /**

     * Renderer định dạng giao diện bảng.

     */

    private static class CellRenderer

            extends DefaultTableCellRenderer {



        private final boolean header;



        CellRenderer(boolean header) {

            this.header = header;

        }



        @Override

        public Component getTableCellRendererComponent(

                JTable tbl,

                Object value,

                boolean isSelected,

                boolean hasFocus,

                int row,

                int col

        ) {



            super.getTableCellRendererComponent(

                    tbl,

                    value,

                    isSelected,

                    hasFocus,

                    row,

                    col

            );



            setBorder(

                    new EmptyBorder(0, 12, 0, 8)

            );



            setFont(new Font(

                    "SansSerif",

                    header ? Font.BOLD : Font.PLAIN,

                    header ? 11 : 12

            ));



            if (header) {



                setBackground(

                        new Color(29, 32, 36)

                );



                setForeground(MUTED);



            } else {



                setBackground(

                        isSelected

                                ? tbl.getSelectionBackground()

                                : CARD

                );



                setForeground(

                        isSelected

                                ? Color.WHITE

                                : TEXT

                );



                if (col == 6 && !isSelected) {



                    if ("Đang diễn ra".equals(value)) {

                        setForeground(GREEN);



                    } else if ("Sắp diễn ra".equals(value)) {

                        setForeground(

                                new Color(220, 174, 85)

                        );



                    } else {

                        setForeground(MUTED);

                    }

                }

            }



            return this;

        }

    }

}
